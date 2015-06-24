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

    public MappingCsvToBean(CsvConfiguration configMap) {

        final int columns = CsvConfiguration.DefinedColumnNames.length;
        columnNames = new String[columns][];
        columnIndexes = new Integer[columns][];
        for (int i = 0; i < columns; i++) {
            columnNames[i] = configMap.getFieldValue(CsvConfiguration.DefinedColumnNames[i]).split("[.]",
                                                                                                   -1);
            columnIndexes[i] = new Integer[columnNames[i].length];
        }
        indexes = configMap.getIndex().split("[.]", -1);
        indexColumns = new Integer[indexes.length];
    }

    @Override
    protected Object convertValue(String value, PropertyDescriptor prop)
        throws InstantiationException, IllegalAccessException {

        final PropertyEditor editor = getPropertyEditor(prop);
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

        for (int i = 0; i < indexes.length; i++)
            for (int j = 0; j < headers.length; j++)
                if (indexes[i].equalsIgnoreCase(headers[j]))
                    indexColumns[i] = j;
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].length == 1)
                continue;
            for (int j = 0; j < columnNames[i].length; j++)
                for (int k = 0; k < headers.length; k++)
                    if (columnNames[i][j].equalsIgnoreCase(headers[k])) {
                        columnIndexes[i][j] = k;
                        break;
                    }
        }
    }

    private int findDuplicateName(int index) {

        int i = 0;
        for (; i < CsvConfiguration.DefinedColumnNames.length; i++)
            if (i != index && columnNames[i].length == 1 &&
                columnNames[i][0].equalsIgnoreCase(columnNames[index][0]))
                break;
        return i;
    }

    @Override
    public List<MappedRecord> parse(MappingStrategy<MappedRecord> mapper, CSVReader csvReader) {

        try {
            mapper.captureHeader(csvReader);

            figureOutMultiColumnField(((MappingHeaderColumnNameTranslateMappingStrategy) mapper).getHeaders());

            String[] columns;
            final List<MappedRecord> list = new ArrayList<MappedRecord>();
            while (null != (columns = csvReader.readNext())) {
                final MappedRecord bean = processLine(mapper, columns);
                postProcessing(bean, columns);
                list.add(bean);
            }
            return list;
        } catch (final Exception e) {
            throw new RuntimeException("Error parsing CSV!", e);
        }
    }

    // figure out the multi-column fields: index, description
    private void postProcessing(MappedRecord record, String[] columns) {

        // figure out the content
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (final String column : columns)
            sb.append(column + TokenSeparator);
        String value = sb.toString();
        value = value.substring(0, value.lastIndexOf(TokenSeparator));
        record.setContent(value + "]");

        // figuer out the index key
        sb = new StringBuffer();
        for (int i = 0; i < indexes.length; i++)
            sb.append(columns[indexColumns[i]] + TokenSeparator);
        value = sb.toString();
        value = value.substring(0, value.lastIndexOf(TokenSeparator));
        record.setIndex(value);

        // figure out the value for the multi-column field
        for (int i = 0; i < columnNames.length; i++) {
            if (columnNames[i].length == 1)
                continue;
            final boolean isDescription = CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FN_Description);
            sb = new StringBuffer();
            for (int j = 0; j < columnNames[i].length; j++)
                if (isDescription) {
                    sb.append("<br/>");
                    sb.append("<b>");
                    sb.append(columnNames[i][j] + ": ");
                    sb.append("</b>");
                    sb.append(columns[columnIndexes[i][j]] + TokenSeparator);
                } else
                    sb.append(columns[columnIndexes[i][j]] + TokenSeparator);
            value = sb.toString();
            value = value.substring(0, value.lastIndexOf(TokenSeparator));

            if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FN_Category))
                record.setCategory(value);
            else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FN_Title))
                record.setTitle(value);
            else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FN_FilterName))
                record.setFilter(value);
            else if (CsvConfiguration.DefinedColumnNames[i].equalsIgnoreCase(CsvConfiguration.FN_Description))
                record.setDescription(value);
        }

        if (record.getCategory().equals("N/A")) {
            final int i = findDuplicateName(1);
            if (i != CsvConfiguration.DefinedColumnNames.length)
                switch (i) {
                case 0:
                    record.setCategory(record.getCategory());
                    break;
                case 4:
                    record.setCategory(record.getFilter());
                    break;
                case 5:
                    record.setCategory(record.getDescription());
                    break;
                default:
                    logger.warn("Cannot map " + CsvConfiguration.DefinedColumnNames[i] +
                                "'s value into category");
                }
        }
    }
}