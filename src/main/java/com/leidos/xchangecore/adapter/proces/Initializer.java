package com.leidos.xchangecore.adapter.proces;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.dao.CoreConfigurationDao;
import com.leidos.xchangecore.adapter.dao.MappedRecordDao;
import com.leidos.xchangecore.adapter.model.CoreConfiguration;
import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.webclient.WebServiceClient;

public class Initializer {

    private static CoreConfigurationDao coreConfigurationDao;

    private static final Logger logger = LoggerFactory.getLogger(Initializer.class);

    private static MappedRecordDao mappedRecordDao;

    public static CoreConfigurationDao getCoreConfigurationDao() {

        return coreConfigurationDao;
    }

    public static MappedRecordDao getMappedRecordDao() {

        return mappedRecordDao;
    }

    public static void setCoreConfigurationDao(CoreConfigurationDao coreConfigurationDao) {

        Initializer.coreConfigurationDao = coreConfigurationDao;
    }

    public static void setMappedRecordDao(MappedRecordDao mappedRecordDao) {

        Initializer.mappedRecordDao = mappedRecordDao;
    }

    public Initializer(String creator) {
        final List<MappedRecord> recordList = getMappedRecordDao().findByCreator(creator);
        for (final MappedRecord record : recordList) {
            logger.debug("Record: " + record);
            final CoreConfiguration config = getCoreConfigurationDao().findById(record.getCoreUri());
            final WebServiceClient wsClient = new WebServiceClient(config.getUri(),
                config.getUsername(),
                config.getPassword());
            wsClient.getIncident(record.getIgID());
        }
        // create the record if needed
    }

}
