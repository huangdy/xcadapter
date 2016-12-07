package com.leidos.xchangecore.adapter.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.dao.CoreConfigurationDao;
import com.leidos.xchangecore.adapter.model.Configuration;
import com.leidos.xchangecore.adapter.model.CoreConfiguration;

public class ConfigFilePaser {

    private static final Logger logger = LoggerFactory.getLogger(ConfigFilePaser.class);

    private static CoreConfigurationDao coreConfigurationDao;

    public static CoreConfigurationDao getCoreConfigurationDao() {

        return coreConfigurationDao;
    }

    public static void setCoreConfigurationDao(CoreConfigurationDao dao) {

        ConfigFilePaser.coreConfigurationDao = dao;
    }

    private final List<Configuration> configurationList = new ArrayList<Configuration>();

    public ConfigFilePaser() {

        super();
    }

    public ConfigFilePaser(String configFilename, InputStream configInputStream) throws Exception {

        super();

        final String filename = configFilename;
        final String creator = filename.substring(0, filename.indexOf("."));
        logger.debug("Creator: " + creator);

        int startCount = 0;
        int endCount = 0;
        String line = null;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(configInputStream));
            reader.mark(20480);
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                if (line.equalsIgnoreCase(Configuration.N_Configuration_Start)) {
                    startCount++;
                    continue;
                }
                else if (line.equalsIgnoreCase(Configuration.N_Configuration_End)) {
                    endCount++;
                    continue;
                }
            }
        } catch (final Exception e) {
            throw new Exception("Parsing: " + configFilename + ": " + e.getMessage());
        }

        if (startCount != endCount) {
            throw new Exception("Parsing: " + configFilename + " incompleted configuration block");
        }

        try {

            reader.reset();

            Configuration configuration = null;

            if (startCount == 0) {
                configuration = new Configuration();
                configuration.setId(creator);
            }

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.length() == 0) {
                    continue;
                }
                if (line.equalsIgnoreCase(Configuration.N_Configuration_Start)) {
                    configuration = new Configuration();
                    configuration.setId(creator);
                }
                else if (line.equalsIgnoreCase(Configuration.N_Configuration_End)) {
                    if (configuration.isValid()) {
                        getCoreConfigurationDao().makePersistent(new CoreConfiguration(configuration.getUri(),
                                                                                       configuration.getUsername(),
                                                                                       configuration.getPassword()));
                        configurationList.add(configuration);
                        configuration = null;
                        continue;
                    }
                    else {
                        throw new Exception("Parsing: " + configFilename + ": Invalid format ...");
                    }
                }
                if (configuration == null) {
                    throw new Exception("Parsing: " + configFilename + ": Invalid format ...");
                }
                final String[] tokens = line.split(",", -1);
                if (tokens.length != 2) {
                    logger.error("Invalide formated Line: [" + line + "]");
                    continue;
                }
                tokens[0] = tokens[0].trim().toLowerCase();
                tokens[1] = tokens[1].trim();
                configuration.setKeyValue(tokens);
            }
            if (configuration != null) {
                if (configuration.isValid()) {
                    getCoreConfigurationDao().makePersistent(new CoreConfiguration(configuration.getUri(),
                                                                                   configuration.getUsername(),
                                                                                   configuration.getPassword()));

                    configurationList.add(configuration);
                } else {
                    throw new Exception("... Invalid format ...");
                }
            }
            reader.close();
        } catch (final Exception e) {
            throw new Exception("Parsing: " + configFilename + ": " + e.getMessage());
        }
    }

    public Configuration getConfigMap() {

        return configurationList.get(0);
    }

    public List<Configuration> listOfConfiguration() {

        return this.configurationList;
    }
}
