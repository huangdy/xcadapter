package com.leidos.xchangecore.adapter.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.dao.MappedRecordDao;
import com.leidos.xchangecore.adapter.model.Configuration;
import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.util.Util;

import au.com.bytecode.opencsv.CSVReader;

public class CSVFileParser {

    private static final double Pi = 3.14159;

    private static final double Radius = 6378137.0;

    private static Logger logger = LoggerFactory.getLogger(CSVFileParser.class);
    private static MappedRecordDao mappedRecordDao;

    private static String PatternPrefix = "(?i:.*";
    private static String PatternPostfix = ".*)";

    public static MappedRecordDao getMappedRecordDao() {

        return mappedRecordDao;
    }

    public static void setMappedRecordDao(MappedRecordDao mappedRecordDao) {

        CSVFileParser.mappedRecordDao = mappedRecordDao;
    }

    private final Map<String, MappedRecord> newRecords = new HashMap<String, MappedRecord>();
    private final Map<String, MappedRecord> updateRecords = new HashMap<String, MappedRecord>();
    private final Map<String, MappedRecord> deleteRecords = new HashMap<String, MappedRecord>();

    public CSVFileParser() {

        super();
    }

    public CSVFileParser(File file, InputStream baseInputStream, Configuration configMap) throws Throwable {

        super();

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

        this.validateConfiguration(configMap, strategy, new XCCSVReader(new FileReader(csvFile)));
        final List<MappedRecord> records = bean.parse(strategy, new XCCSVReader(new FileReader(csvFile)));
        final Date currentDate = new Date();

        for (final MappedRecord record : records) {
            // for the category, we will override with category.fixed if existed
            if (configMap.getCategoryFixed().length() > 0) {
                record.setCategory(configMap.getCategoryFixed());
            } else if (configMap.getCategoryPrefix().length() > 0) {
                // Or, prefix the category if category.prefix existed
                record.setCategory(configMap.getCategoryPrefix() + record.getCategory());
            }
            if (configMap.getTitlePrefix() != null && configMap.getTitlePrefix().length() > 0) {
                record.setTitle(configMap.getTitlePrefix() + record.getTitle());
            }
            record.setCreator(configMap.getId());
            record.setLastUpdated(currentDate);
            record.setCoreUri(configMap.getUri());
            logger.debug("record: " + record.toString());
        }

        parseRecords(records, configMap);

        if (baseInputStream != null) {
            csvFile.delete();
        }
    }

    private Double[][] calculateBoundingBox(Map<String, MappedRecord> filterRecordSet, double distance) {

        final Collection<MappedRecord> records = filterRecordSet.values();
        double south = 0.0;
        double north = 0.0;
        double west = 0.0;
        double east = 0.0;
        for (final MappedRecord r : records) {
            final double lat = Double.parseDouble(r.getLatitude());
            north = lat > 0 ? lat > north ? lat : north : lat < north ? lat : north;
            south = lat > 0 ? lat < south ? lat : south : lat > south ? lat : south;
            final double lon = Double.parseDouble(r.getLongitude());
            west = lon > 0 ? lon < west ? lon : west : lon > west ? west : lon;
            east = lon > 0 ? lon > east ? lon : east : lon < east ? east : lon;
            if (south == 0) {
                south = north;
            }
            if (north == 0) {
                north = south;
            }
            if (east == 0) {
                east = west;
            }
            if (west == 0) {
                west = east;
            }
        }
        /*
        //Earth’s radius, sphere
        R=6378137

        //offsets in meters
        dn = 100
        de = 100

        //Coordinate offsets in radians
        dLat = dn/R
        dLon = de/(R*Cos(Pi*lat/180))

        //OffsetPosition, decimal degrees
        latO = lat + dLat * 180/Pi
        lonO = lon + dLon * 180/Pi
         */
        final double d = distance * 1000.0;
        final double deltaLat = d / Radius * 180 / Pi;
        north += deltaLat * (north > 0 ? 1 : -1);
        south -= deltaLat * (south > 0 ? 1 : -1);
        final double northDelta = d / (Radius * Math.cos(Pi * north / 180.0)) * 180.0 / Pi;
        final double northWestLon = west - northDelta;
        final double northEastLon = east + northDelta;
        final double southDelta = d / (Radius * Math.cos(Pi * south / 180.0)) * 180.0 / Pi;
        final double southWestLon = west - southDelta;
        final double southEastLon = east + southDelta;
        final Double[][] boundingBox = new Double[5][2];
        boundingBox[0][0] = northWestLon;
        boundingBox[0][1] = north;
        boundingBox[1][0] = northEastLon;
        boundingBox[1][1] = north;
        boundingBox[2][0] = southEastLon;
        boundingBox[2][1] = south;
        boundingBox[3][0] = southWestLon;
        boundingBox[3][1] = south;
        boundingBox[4][0] = northWestLon;
        boundingBox[4][1] = north;

        return boundingBox;
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

    public MappedRecord[] getDeleteRecords() {

        if (this.deleteRecords.isEmpty()) {
            return null;
        } else {
            return this.deleteRecords.values().toArray(new MappedRecord[this.deleteRecords.values().size()]);
        }
    }

    public MappedRecord[] getNewRecords() {

        if (this.newRecords.isEmpty()) {
            return null;
        } else {
            return this.newRecords.values().toArray(new MappedRecord[this.newRecords.values().size()]);
        }
    }

    public MappedRecord[] getUpdateRecords() {

        if (this.updateRecords.isEmpty()) {
            return null;
        } else {
            return this.updateRecords.values().toArray(new MappedRecord[this.updateRecords.values().size()]);
        }
    }

    private File mergeFiles(InputStream is, File f) {

        final CSVReader baseReader = new CSVReader(new InputStreamReader(is));
        CSVReader indexedReader = null;
        try {
            String[] headers = baseReader.readNext();
            final String[] baseHeaders = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                baseHeaders[i] = headers[i].trim();
            }
            indexedReader = new CSVReader(new FileReader(f));
            headers = indexedReader.readNext();
            final String[] indexHeaders = new String[headers.length];
            for (int i = 0; i < headers.length; i++) {
                indexHeaders[i] = headers[i].trim();
            }

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

    private void parseRecords(List<MappedRecord> records, Configuration config) {

        // reset the update and delete set
        this.newRecords.clear();
        this.updateRecords.clear();
        this.deleteRecords.clear();

        logger.debug("total records: " + records.size());

        final boolean negativeExpression = config.getFilterText().startsWith("!");
        final String filterText = negativeExpression ? config.getFilterText().substring(1) : config.getFilterText();
        final String pattern = PatternPrefix + filterText + PatternPostfix;
        logger.debug("Filter Pattern: " + pattern);

        // find the matched filter text records
        for (final MappedRecord r : records) {
            final boolean isMatched = r.getFilter().matches(pattern);
            if (isMatched && negativeExpression == false || isMatched == false && negativeExpression == true) {
                newRecords.put(r.getIndex(), r);
            }
        }
        logger.debug("filtered records: " + newRecords.size());
        final Set<MappedRecord> distanceSet = new HashSet<MappedRecord>();
        if (config.getDistance().length() > 0 && newRecords.size() > 1) {
            final Double[][] boundingBox = this.calculateBoundingBox(newRecords, Double.parseDouble(
                config.getDistance()));
            final Collection<MappedRecord> newRecordSet = newRecords.values();
            for (final MappedRecord r : newRecordSet) {
                if (r.getFilter().equalsIgnoreCase(config.getDistanceFilterText())) {
                    if (Util.insideBoundingBox(boundingBox, r.getLatitude(), r.getLongitude())) {
                        distanceSet.add(r);
                    }
                }
            }
            logger.debug("within the BoundingBox: " + distanceSet.size() + " records found");
            for (final MappedRecord r : distanceSet) {
                newRecords.put(r.getIndex(), r);
            }
        }
        distanceSet.clear();

        // get the existed records for this creator, for example: target
        final List<MappedRecord> existedRecordList = getMappedRecordDao().findByCreator(config.getId());
        final Map<String, MappedRecord> existedRecordSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord record : existedRecordList) {
            logger.debug("instance's Core: " + record.getCoreUri() + " and configuration's Core: " + config.getUri());
            if (record.getCoreUri().equalsIgnoreCase(config.getUri())) {
                existedRecordSet.put(record.getIndex(), record);
            }
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
            if (newRecords.containsKey(key)) {
                final MappedRecord record = newRecords.remove(key);
                if (inCoreSet.get(key).getContent().equalsIgnoreCase(record.getContent()) == false) {
                    record.setWorkProductID(inCoreSet.get(key).getWorkProductID());
                    logger.debug("for update: WPID: " + record.getWorkProductID());
                    this.updateRecords.put(key, record);
                }
            } else {
                // logger.debug("IGID: " + key + " existed ... Auto.Close: " + (config.isAutoClose() ? "true" : "false"));
                if (config.isAutoClose() == true) {
                    this.deleteRecords.put(key, inCoreSet.get(key));
                }
            }
        }
        logger.debug("records need to be created: " + this.newRecords.size());
        logger.debug("records need to be updated: " + this.updateRecords.size());
        logger.debug("records need to be deleted: " + this.deleteRecords.size());
    }

    private void validateConfiguration(Configuration csvConfiguration,
                                       MappingHeaderColumnNameTranslateMappingStrategy strategy,
                                       CSVReader csvReader) throws Throwable {

        strategy.captureHeader(csvReader);

        for (final String columnName : Configuration.DefinedColumnNames) {
            final String column = csvConfiguration.getValue(columnName);
            final String[] columns = column.split("\\.", -1);
            for (final String c : columns) {
                boolean found = false;
                for (final String h : strategy.getHeaders()) {
                    if (c.equalsIgnoreCase(h.trim())) {
                        found = true;
                        break;
                    }
                }
                // if the column is not specified in configuration file, it's valid
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
