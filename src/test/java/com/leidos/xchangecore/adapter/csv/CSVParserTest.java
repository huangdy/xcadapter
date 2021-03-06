package com.leidos.xchangecore.adapter.csv;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.leidos.xchangecore.adapter.model.Configuration;
import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.util.Util;

import gov.niem.niem.niemCore.x20.IncidentType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:contexts/applicationContext.xml"
})
public class CSVParserTest {

    private void testBoyd() throws Throwable {

        final String path = "src/main/webapp/config/boyd.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/boyd.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("boyd.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            final IncidentType incident = Util.getIncidentDocument(record);
            System.out.println("Record: " + incident);
        }
    }

    private void testCostco() throws Throwable {

        final String path = "src/main/webapp/config/costco.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/costco.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("costco.config", fis);
        final List<Configuration> configurationList = configFileParser.listOfConfiguration();
        for (final Configuration configuration : configurationList) {
            final CSVFileParser csvFileParser = new CSVFileParser(csvFile, null, configuration);
            MappedRecord[] records = csvFileParser.getNewRecords();
            if (records != null) {
                for (final MappedRecord record : records) {
                    final IncidentType incident = Util.getIncidentDocument(record);
                    System.out.println("New Record: " + incident);
                }
            }
            records = csvFileParser.getUpdateRecords();
            records = csvFileParser.getDeleteRecords();


        }
    }

    @Test
    public void testCSVUpload() throws Throwable {

        // this.testMacys();
        // this.testTarget();
        // this.testBoyd();
        this.testCostco();
        // this.testIrWin();
        // this.testWalgreen();
        testLowes();
    }

    private void testIrWin() throws Throwable {

        final String path = "src/main/webapp/config/irwin.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/IRWINexport.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("irwin.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            final IncidentType incident = Util.getIncidentDocument(record);
            System.out.println("Record: " + incident);
        }
    }

    private void testLowes() throws Throwable {

        final String path = "src/main/webapp/config/lowes.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/lowes.1.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("lowes.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        MappedRecord[] records = csvFileParser.getNewRecords();
        if (records != null) {
            for (final MappedRecord record : records) {
                final IncidentType incident = Util.getIncidentDocument(record);
                System.out.println("New Record: " + incident);
            }
        }
        if ((records = csvFileParser.getUpdateRecords()) != null) {
            for (final MappedRecord record : records) {
                final IncidentType incident = Util.getIncidentDocument(record);
                System.out.println("Updated Record: " + incident);
            }
        }
        if ((records = csvFileParser.getDeleteRecords()) != null) {
            for (final MappedRecord record : records) {
                final IncidentType incident = Util.getIncidentDocument(record);
                System.out.println("Deleted Record: " + incident);
            }
        }
    }

    private void testMacys() throws Throwable {

        final String path = "src/main/webapp/config/macys.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/a.Macys.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("macys.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            final IncidentType incident = Util.getIncidentDocument(record);
            System.out.println("Record: " + incident);
        }
    }

    private void testSears() throws Throwable {

        final String path = "src/main/webapp/config/sears.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/distance.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("sears.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            final IncidentType incident = Util.getIncidentDocument(record);
            System.out.println("Record: " + incident);
        }
    }

    @Test
    public void testString() {

        String key = "abc:def:";
        key = key.substring(0, key.lastIndexOf(":"));
        System.out.println("Key: " + key);

        key = "abc:def:kadfa dkerekjkkde  dde";
        final boolean found = key.matches("(?i:.*ada.*)");
        System.out.println("contains de: " + (found ? " found" : " not found"));

        key = " abc def ";
        System.out.println("[" + key + "] -> [" + key.trim() + "]");
    }

    private void testTarget() throws Throwable {

        final String path = "src/main/webapp/config/target.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final File csvFile = new File("src/test/resources/a.Target.csv");
        final ConfigFilePaser configFileParser = new ConfigFilePaser("target.config", fis);
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            null,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            final IncidentType incident = Util.getIncidentDocument(record);
            System.out.println("Record: " + incident);
        }
    }

    private void testWalgreen() throws Throwable {

        final String path = "src/main/webapp/config/walgreen.config";
        final FileInputStream fis = new FileInputStream(new File(path));
        final String baseFilename = "src/main/webapp/config/walgreen.csv";
        final FileInputStream baseIS = new FileInputStream(new File(baseFilename));
        final ConfigFilePaser configFileParser = new ConfigFilePaser("walgreen.config", fis);
        final File csvFile = new File("src/test/resources/a.Walgreen.csv");
        final CSVFileParser csvFileParser = new CSVFileParser(csvFile,
            baseIS,
            configFileParser.getConfigMap());
        final MappedRecord[] records = csvFileParser.getNewRecords();
        for (final MappedRecord record : records) {
            System.out.println("Record: " + record);
            System.out.println("Incident Document:\n" + Util.getIncidentDocument(record) + "\n");
        }

    }
}
