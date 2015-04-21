package com.leidos.xchangecore.adapter.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.leidos.xchangecore.adapter.dao.MappedRecordDao;
import com.leidos.xchangecore.adapter.model.CsvConfiguration;
import com.leidos.xchangecore.adapter.model.MappedRecord;

public class CSVFileParser {

    public static MappedRecordDao getMappedRecordDao() {

        return mappedRecordDao;
    }

    public static void setMappedRecordDao(MappedRecordDao mappedRecordDao) {

        CSVFileParser.mappedRecordDao = mappedRecordDao;
    }
    private static Logger logger = LoggerFactory.getLogger(CSVFileParser.class);
    private static MappedRecordDao mappedRecordDao;
    private static String PatternPrefix = "(?i:.*";
    private static String PatternPostfix = ".*)";

    private String filterText;
    private String creator;

    private List<MappedRecord> records = null;
    private final Map<String, MappedRecord> updateRecords = new HashMap<String, MappedRecord>();
    private final Map<String, MappedRecord> deleteRecords = new HashMap<String, MappedRecord>();

    public CSVFileParser() {

        super();
    }

    public CSVFileParser(File file, InputStream baseInputStream, CsvConfiguration configMap)
        throws Throwable {

        super();
        this.setFilterText(configMap.getFilterText());
        this.setCreator(configMap.getId());

        final MappingHeaderColumnNameTranslateMappingStrategy strategy = new MappingHeaderColumnNameTranslateMappingStrategy();
        strategy.setType(MappedRecord.class);
        strategy.setColumnMapping(configMap.toMap());

        final MappingCsvToBean bean = new MappingCsvToBean(configMap);

        // merge files if necessary
        File csvFile = null;
        if (baseInputStream != null) {
            csvFile = this.mergeFiles(baseInputStream, file);
        } else {
            csvFile = file;
        }
        this.validateConfiguration(configMap, strategy, new CSVReader(new FileReader(csvFile)));

        // try {
        this.records = bean.parse(strategy, new CSVReader(new FileReader(csvFile)));
        final Date currentDate = new Date();
        for (final MappedRecord record : this.records) {
            record.setCreator(configMap.getId());
            record.setLastUpdated(currentDate);
            logger.debug("record: " + record);
        }
        // } catch (final Throwable e) {
        // e.printStackTrace();
        // logger.error("CSV Parser: " + e.getMessage());
        // }

        if (baseInputStream != null) {
            csvFile.delete();
        }
    }

    public List<MappedRecord> getAllRecords() {

        return this.records;
    }

    private String getCombinedLine(String[] indexHeaders, String[] baseHeaders, int index) {

        // write the header first
        final StringBuffer sb = new StringBuffer();
        for (final String indexHeader : indexHeaders) {
            sb.append(indexHeader);
            sb.append(",");
        }
        for (int i = 0; i < baseHeaders.length; i++) {
            if (i == index) {
                continue;
            }
            sb.append(baseHeaders[i]);
            sb.append(",");
        }
        String header = sb.toString();
        header = header.substring(0, header.lastIndexOf(","));
        return header + "\n";
    }

    public String getCreator() {

        return this.creator;
    }

    public MappedRecord[] getDeleteRecords() {

        if (this.deleteRecords.isEmpty()) {
            return null;
        } else {
            return this.deleteRecords.values().toArray(new MappedRecord[this.deleteRecords.values().size()]);
        }
    }

    public String getFilterText() {

        return this.filterText;
    }

    public MappedRecord[] getRecords() {

        // reset the update and delete set
        this.updateRecords.clear();
        this.deleteRecords.clear();

        logger.debug("total records: " + this.records.size());

        final String pattern = PatternPrefix + this.getFilterText() + PatternPostfix;
        logger.debug("Filter Pattern: " + pattern);
        // find the matched filter text records
        final Map<String, MappedRecord> filterRecordSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord r : this.records) {
            if (r.getFilter().matches(pattern)) {
                // filterRecords.add(r);
                filterRecordSet.put(r.getIndex(), r);
            }
        }
        logger.debug("filtered records: " + filterRecordSet.size());

        // get the existed records for this creator, for example: target
        final List<MappedRecord> existedRecordList = getMappedRecordDao().findByCreator(this.getCreator());
        final Map<String, MappedRecord> existedRecordSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord record : existedRecordList) {
            existedRecordSet.put(record.getIndex(), record);
        }
        logger.debug("existed records: " + existedRecordSet.size());

        // if the existed record contains the IGID, then we assume it's been saved in XchangeCore already
        final Map<String, MappedRecord> inCoreSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord r : existedRecordList) {
            if (r.getIgID() != null) {
                inCoreSet.put(r.getIndex(), r);
            }
        }
        logger.debug("records in Core: " + inCoreSet.size());

        // if the in-core record is part of the new incident, we will perform an update of it
        // if the in-core recrod is not part of the new incident, we will delete it from XchangeCore
        final Set<String> inCoreKeySet = inCoreSet.keySet();
        for (final String key : inCoreKeySet) {
            if (filterRecordSet.containsKey(key)) {
                final MappedRecord record = filterRecordSet.remove(key);
                if (inCoreSet.get(key).getContent().equalsIgnoreCase(record.getContent()) == false) {
                    this.updateRecords.put(key, record);
                }
            } else {
                this.deleteRecords.put(key, inCoreSet.get(key));
            }
        }
        logger.debug("records need to be updated: " + this.updateRecords.size());
        logger.debug("records need to be deleted: " + this.deleteRecords.size());

        return filterRecordSet.values().toArray(new MappedRecord[filterRecordSet.values().size()]);
    }

    public MappedRecord[] getUpdateRecords() {

        if (this.updateRecords.isEmpty()) {
            return null;
        } else {
            return this.updateRecords.values().toArray(new MappedRecord[this.updateRecords.values().size()]);
        }
    }

    public void makePersist() {

        for (final MappedRecord record : this.records) {
            getMappedRecordDao().makePersistent(record);
        }
    }

    private File mergeFiles(InputStream is, File f) {

        final CSVReader baseReader = new CSVReader(new InputStreamReader(is));
        CSVReader indexedReader = null;
        try {
            final String[] baseHeaders = baseReader.readNext();
            indexedReader = new CSVReader(new FileReader(f));
            final String[] indexHeaders = indexedReader.readNext();
            final int[] columnNumbers = this.whichColumn(baseHeaders, indexHeaders);
            final HashMap<String, String[]> indexMap = new HashMap<String, String[]>();
            // read in the target file
            String[] columns = null;
            while ((columns = indexedReader.readNext()) != null) {
                indexMap.put(columns[columnNumbers[0]], columns);
            }
            indexedReader.close();
            logger.debug("index file contains " + indexMap.size() + " records");

            final File temp = File.createTempFile(f.getName(), ".tmp");
            final BufferedWriter writer = new BufferedWriter(new FileWriter(temp));

            writer.write(this.getCombinedLine(indexHeaders, baseHeaders, columnNumbers[1]));

            // merge with base csv file
            while ((columns = baseReader.readNext()) != null) {
                final String key = columns[columnNumbers[1]];
                if (indexMap.containsKey(key)) {
                    // write the merged line
                    logger.debug("index file contain [" + key + "]");
                    writer.write(this.getCombinedLine(indexMap.get(key), columns, columnNumbers[1]));
                }
            }
            baseReader.close();
            writer.flush();
            writer.close();
            return temp;
        } catch (final Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (baseReader != null) {
                    baseReader.close();
                }
                if (indexedReader != null) {
                    indexedReader.close();
                }
            } catch (final Throwable e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return null;
    }

    public void setCreator(String creator) {

        this.creator = creator;
    }

    public void setFilterText(String filterText) {

        this.filterText = filterText;
    }

    public void setRecords(List<MappedRecord> records) {

        this.records = records;
    }

    private void validateConfiguration(CsvConfiguration csvConfiguration,
                                       MappingHeaderColumnNameTranslateMappingStrategy strategy,
                                       CSVReader csvReader) throws Throwable {

        strategy.captureHeader(csvReader);

        for (final String columnName : CsvConfiguration.DefinedColumnNames) {
            final String column = csvConfiguration.getValue(columnName);
            final String[] columns = column.split("\\.", -1);
            for (final String c : columns) {
                boolean found = false;
                for (final String h : strategy.getHeaders()) {
                    if (c.equalsIgnoreCase(h)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new Exception("Column: " + c + " is invalid column name");
                }
            }
        }
    }

    private int[] whichColumn(String[] baseHeaders, String[] indexHeaders) {

        for (int i = 0; i < indexHeaders.length; i++) {
            for (int j = 0; j < baseHeaders.length; j++) {
                if (indexHeaders[i].equalsIgnoreCase(baseHeaders[j])) {
                    return new int[] {
                        i,
                        j
                    };
                }
            }
        }
        return null;
    }
}