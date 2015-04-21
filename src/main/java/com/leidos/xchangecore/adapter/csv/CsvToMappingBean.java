package com.leidos.xchangecore.adapter.csv;

import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.model.MappedRecord;

import au.com.bytecode.opencsv.bean.CsvToBean;

public class CsvToMappingBean
    extends CsvToBean<MappedRecord> {

    private static final Logger logger = LoggerFactory.getLogger(CsvToMappingBean.class);

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
}