package com.leidos.xchangecore.adapter.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.leidos.xchangecore.adapter.model.MappedRecord;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:contexts/applicationContext.xml"
})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class DaoTest {

    @Autowired
    MappedRecordDao mappedRecordDao;

    @Test
    public void testMappedRecord() {

        final MappedRecord record = new MappedRecord();
        record.setCategory("category");
        record.setTitle("title");
        record.setContent("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm  nnnnnnnnnnnnnnn<nnn nnnnnnnnnnnnn    nnnnnnnnnnnnnnn>/!@#$%^&*()_+nnnn           nnnnnnnnnnnnnmmmmmmmmmmmmmmmmmmmmmmmmmmm");
        record.setIndex("indexKey");
        record.setCreator("creator");
        record.setDescription("<br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b><br><b>Description: No Desc</b>");
        record.setWorkProductID("<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id<id>1234</id><name>Name</name><id>1234</id");
        record.setFilter("Open");
        mappedRecordDao.makePersistent(record);
    }

}
