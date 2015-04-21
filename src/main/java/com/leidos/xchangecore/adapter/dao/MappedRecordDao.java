package com.leidos.xchangecore.adapter.dao;

import java.util.List;

import org.hibernate.criterion.Restrictions;

import com.leidos.xchangecore.adapter.model.MappedRecord;

public class MappedRecordDao
    extends GenericHibernateDAO<MappedRecord, Integer> {

    private static final String C_Creator = "creator";

    public List<MappedRecord> findByCreator(String creator) {

        return findByCriteria(Restrictions.eq(C_Creator, creator));
    }

    @Override
    public void makeTransient(MappedRecord record) {

        super.makeTransient(findById(record.getId()));
    }
}
