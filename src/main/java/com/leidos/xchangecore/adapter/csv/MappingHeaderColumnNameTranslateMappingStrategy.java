package com.leidos.xchangecore.adapter.csv;

import au.com.bytecode.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import com.leidos.xchangecore.adapter.model.MappedRecord;

public class MappingHeaderColumnNameTranslateMappingStrategy
    extends HeaderColumnNameTranslateMappingStrategy<MappedRecord> {

    public String[] getHeaders() {

        return header;
    }
}
