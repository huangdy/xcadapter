package com.leidos.xchangecore.adapter.csv;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.CsvToBean;
import au.com.bytecode.opencsv.bean.MappingStrategy;

import com.leidos.xchangecore.adapter.model.CsvConfiguration;
import com.leidos.xchangecore.adapter.model.MappedRecord;

public class MappingCsvToBean
extends CsvToBean<MappedRecord> {

    private static final Logger logger = LoggerFactory.getLogger(MappingCsvToBean.class);

    private static final String TokenSeparator = ":";

    private final String[] indexes;
    private final Integer[] indexColumns;

    private final String[][] columnNames;
    private final Integer[][] columnIndexes;
    private final String titlePrefix;

    public MappingCsvToBean(CsvConfiguration configMap) {

        final int columns = CsvConfiguration.DefinedColumnNames.length;
        this.columnNames = new String[columns][];
        this.columnIndexes = new Integer[columns][];
        for (int i = 0; i < columns; i++) {
            this.columnNames[i] = configMap.getFieldValue(CsvConfiguration.DefinedColumnNames[i]).split("[.]",
                -1);
            this.columnIndexes[i] = new Integer[this.columnNames[i].length];
        }
        this.indexes = configMap.getIndex().split("[.]", -1);
        this.indexColumns = new Integer[this.indexes.length];
        this.titlePrefix = configMap.getTitlePrefix();
    }

    @Override
    protected Object convertValue(String value, PropertyDescriptor prop)
        throws InstantiationException, IllegalAccessException {

        final PropertyEditor editor = this.getPropertyEditor(prop);
        Object obj = value;
        if (null != editor) {
            try {
                editor.setAsText(value);
            } catch (final NumberFormatException e) {
                logger.warn("Value: [" + value + "]: " + e.getMessage());
                editor.setAsText("0");
            }
            obj = editor.getValue();
        }
        return obj;
    }

    // figure out the index key in column order
    private void figureOutMultiColumnField(String[] headers) {

        for (int i = 0; i < this.indexes.length; i++) {
            for (int j = 0; j < headers.length; j++) {
                if (this.indexes[i].equalsIgnoreCase(headers[j])) {
                    this.indexColumns[i] = j;
                }
            }
        }
        for (int i = 0; i < this.columnNames.length; i++) {
            if (this.columnNames[i].length == 1) {
                continue;
            }
            for (int j = 0; j < this.columnNames[i].length; j++) {
                for (int k = 0; k < headers.length; k++) {
                    if (this.columnNames[i][j].equalsIgnoreCase(headers[k])) {
                        this.columnIndexes[i][j] = k;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public List<MappedRecord> parse(MappingStrategy<MappedRecord> mapper, CSVReader csvReader) {

        try {
            mapper.captureHeader(csvReader);

            this.figureOutMultiColumnField(((MappingHeaderColumnNameTranslateMappingStrategy) mapper).getHeaders());

            String[] columns;
            final List<MappedRecord> list = new ArrayList<MappedRecord>();
            while (null != (columns = csvReader.readNext())) {
                final MappedRecord bean = this.processLine(mapper, columns);
                this.postProcessing(bean, columns);
                list.add(bean);
            }
            return list;
        } catch (final Exception e) {
            throw new RuntimeException("Error parsing CSV!", e);
        }
    }

    // figure out the multi-column fields: index, description
    private void postProcessing(MappedRecord record, String[] columns) {

        // prefix the title
        record.setTitle(this.titlePrefix + " - " + record.getTitle());
        logger.debug("record.title: " + record.getTitle());

        // figure out the content
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (final String column : columns) {
            sb.append(column + TokenSeparator);
        }
        String value = sb.toString();
        value = value.substring(0, value.lastIndexOf(TokenSeparator));
        record.setContent(value + "]");

        // figuer out the index key
        sb = new StringBuffer();
        for (int i = 0; i < this.indexes.length; i++) {
            sb.append(columns[this.indexColumns[i]] + TokenSeparator);
        }
        value = sb.toString();
        value = value.substring(0, value.lastIndexOf(TokenSeparator));
        record.setIndex(value);

        // figure out the value for the multi-column field
        for (int i = 0; i < this.columnNames.length; i++) {
            if (this.columnNames[i].length == 1) {
                continue;
            }
            final boolean isDescription = CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FieldName_Description);
            sb = new StringBuffer();
            for (int j = 0; j < this.columnNames[i].length; j++) {
                if (isDescription) {
                    sb.append("<br/>");
                    sb.append("<b>");
                    sb.append(this.columnNames[i][j] + ": ");
                    sb.append("</b>");
                    sb.append(columns[this.columnIndexes[i][j]] + TokenSeparator);
                } else {
                    sb.append(columns[this.columnIndexes[i][j]] + TokenSeparator);
                }
            }
            value = sb.toString();
            value = value.substring(0, value.lastIndexOf(TokenSeparator));

            if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FieldName_Category)) {
                record.setCategory(value);
            } else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FieldName_Title)) {
                record.setTitle(value);
            } else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FieldName_FilterName)) {
                record.setFilter(value);
            } else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FieldName_Description)) {
                record.setDescription(value);
            }
        }
    }
}