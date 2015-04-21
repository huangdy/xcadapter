package com.leidos.xchangecore.adapter.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.dao.CsvConfigurationDao;
import com.leidos.xchangecore.adapter.model.CsvConfiguration;

public class ConfigFilePaser {

    public static CsvConfigurationDao getCsvConfigurationDao() {

        return csvConfigurationDao;
    }

    public static void setCsvConfigurationDao(CsvConfigurationDao csvConfigurationDao) {

        ConfigFilePaser.csvConfigurationDao = csvConfigurationDao;
    }

    private static final Logger logger = LoggerFactory.getLogger(ConfigFilePaser.class);

    private static CsvConfigurationDao csvConfigurationDao;

    private CsvConfiguration configMap;

    public ConfigFilePaser() {

        super();
    }

    public ConfigFilePaser(String configFilename, InputStream configInputStream) throws Exception {

        super();

        final String filename = configFilename;
        final String creator = filename.substring(0, filename.indexOf("."));
        logger.debug("Creator: " + creator);
        this.configMap = new CsvConfiguration();
        this.configMap.setId(creator);
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(configInputStream));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || (line.length() == 0)) {
                    continue;
                }
                final String[] tokens = line.split(",", -1);
                if (tokens.length != 2) {
                    logger.error("Invalide formated Line: [" + line + "]");
                    continue;
                }
                tokens[0] = tokens[0].trim().toLowerCase();
                tokens[1] = tokens[1].trim();
                this.configMap.setKeyValue(tokens);
            }
            reader.close();
        } catch (final Exception e) {
            throw new Exception("Parsing " + configFilename + ": " + e.getMessage());
        }

        // final CsvConfiguration config = getCsvConfigurationDao().findById(creator);
        getCsvConfigurationDao().makePersistent(this.configMap);
    }

    public CsvConfiguration getConfigMap() {

        return this.configMap;
    }

    public void setConfigMap(CsvConfiguration configMap) {

        this.configMap = configMap;
    }
}
