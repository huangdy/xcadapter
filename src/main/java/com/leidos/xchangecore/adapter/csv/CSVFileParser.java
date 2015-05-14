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

import au.com.bytecode.opencsv.CSVReader;

import com.leidos.xchangecore.adapter.dao.MappedRecordDao;
import com.leidos.xchangecore.adapter.model.CsvConfiguration;
import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.leidos.xchangecore.adapter.util.Util;

public class CSVFileParser {

    public static MappedRecordDao getMappedRecordDao() {

        return mappedRecordDao;
    }

    public static void setMappedRecordDao(MappedRecordDao mappedRecordDao) {

        CSVFileParser.mappedRecordDao = mappedRecordDao;
    }

    private static final double Pi = 3.14159;
    private static final double Radius = 6378137.0;

    private static Logger logger = LoggerFactory.getLogger(CSVFileParser.class);
    private static MappedRecordDao mappedRecordDao;

    private static String PatternPrefix = "(?i:.*";
    private static String PatternPostfix = ".*)";

    private String filterText;
    private String distance;
    private String distanceFilterText;
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
        this.setDistance(configMap.getDistance());
        this.setDistanceFilterText(configMap.getDistanceFilterText());
        this.setCreator(configMap.getId());

        final MappingHeaderColumnNameTranslateMappingStrategy strategy = new MappingHeaderColumnNameTranslateMappingStrategy();
        strategy.setType(MappedRecord.class);
        strategy.setColumnMapping(configMap.toMap());

        final MappingCsvToBean bean = new MappingCsvToBean(configMap);

        // merge files if necessary
        File csvFile = null;
        if (baseInputStream != null)
            csvFile = this.mergeFiles(baseInputStream, file);
        else
            csvFile = file;
        this.validateConfiguration(configMap, strategy, new CSVReader(new FileReader(csvFile)));

        // try {
        this.records = bean.parse(strategy, new CSVReader(new FileReader(csvFile)));
        final Date currentDate = new Date();
        for (final MappedRecord record : this.records) {
            if (configMap.getCategoryFixed().length() > 0)
                record.setCategory(configMap.getCategoryFixed());
            record.setCreator(configMap.getId());
            record.setLastUpdated(currentDate);
            logger.debug("record: " + record.toString());
        }
        // } catch (final Throwable e) {
        // e.printStackTrace();
        // logger.error("CSV Parser: " + e.getMessage());
        // }

        if (baseInputStream != null)
            csvFile.delete();
    }

    private Double[][] calculateBoundingBox(Map<String, MappedRecord> filterRecordSet,
                                            double distance) {

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
            if (south == 0)
                south = north;
            if (north == 0)
                north = south;
            if (east == 0)
                east = west;
            if (west == 0)
                west = east;
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
        final double d = Double.parseDouble(this.getDistance()) * 1000.0;
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
            if (i == index)
                continue;
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

        if (this.deleteRecords.isEmpty())
            return null;
        else
            return this.deleteRecords.values().toArray(new MappedRecord[this.deleteRecords.values().size()]);
    }

    public String getDistance() {

        return this.distance;
    }

    public String getDistanceFilterText() {

        return this.distanceFilterText;
    }

    public String getFilterText() {

        return this.filterText;
    }

    public MappedRecord[] getRecords() {

        // reset the update and delete set
        this.updateRecords.clear();
        this.deleteRecords.clear();

        logger.debug("total records: " + this.records.size());

        final boolean negativeExpression = this.getFilterText().startsWith("!");
        final String filterText = negativeExpression ? this.getFilterText().substring(1) : this.getFilterText();
        final String pattern = PatternPrefix + filterText + PatternPostfix;
        logger.debug("Filter Pattern: " + pattern);
        // find the matched filter text records
        final Map<String, MappedRecord> filterRecordSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord r : this.records) {
            final boolean isMatched = r.getFilter().matches(pattern);
            if (isMatched && negativeExpression == false || isMatched == false &&
                negativeExpression == true)
                filterRecordSet.put(r.getIndex(), r);
        }
        logger.debug("filtered records: " + filterRecordSet.size());

        final Set<MappedRecord> recordSet = new HashSet<MappedRecord>();
        if (this.getDistance().length() > 0) {
            final Double[][] boundingBox = this.calculateBoundingBox(filterRecordSet,
                Double.parseDouble(this.getDistance()));
            final Collection<MappedRecord> records = filterRecordSet.values();
            for (final MappedRecord r : this.records)
                if (r.getFilter().equalsIgnoreCase(this.getDistanceFilterText()))
                    if (Util.insideBoundingBox(boundingBox, r.getLatitude(), r.getLongitude()))
                        recordSet.add(r);
            return recordSet.toArray(new MappedRecord[recordSet.size()]);

        }

        // get the existed records for this creator, for example: target
        final List<MappedRecord> existedRecordList = getMappedRecordDao().findByCreator(this.getCreator());
        final Map<String, MappedRecord> existedRecordSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord record : existedRecordList)
            existedRecordSet.put(record.getIndex(), record);
        logger.debug("existed records: " + existedRecordSet.size());

        // if the existed record contains the IGID, then we assume it's been saved in XchangeCore already
        final Map<String, MappedRecord> inCoreSet = new HashMap<String, MappedRecord>();
        for (final MappedRecord r : existedRecordList)
            if (r.getIgID() != null)
                inCoreSet.put(r.getIndex(), r);
        logger.debug("records in Core: " + inCoreSet.size());

        // if the in-core record is part of the new incident, we will perform an update of it
        // if the in-core recrod is not part of the new incident, we will delete it from XchangeCore
        final Set<String> inCoreKeySet = inCoreSet.keySet();
        for (final String key : inCoreKeySet)
            if (filterRecordSet.containsKey(key)) {
                final MappedRecord record = filterRecordSet.remove(key);
                if (inCoreSet.get(key).getContent().equalsIgnoreCase(record.getContent()) == false)
                    this.updateRecords.put(key, record);
            } else
                this.deleteRecords.put(key, inCoreSet.get(key));
        logger.debug("records need to be updated: " + this.updateRecords.size());
        logger.debug("records need to be deleted: " + this.deleteRecords.size());

        return filterRecordSet.values().toArray(new MappedRecord[filterRecordSet.values().size()]);
    }

    public MappedRecord[] getUpdateRecords() {

        if (this.updateRecords.isEmpty())
            return null;
        else
            return this.updateRecords.values().toArray(new MappedRecord[this.updateRecords.values().size()]);
    }

    public void makePersist() {

        for (final MappedRecord record : this.records)
            getMappedRecordDao().makePersistent(record);
    }

    private File mergeFiles(InputStream is, File f) {

        final CSVReader baseReader = new CSVReader(new InputStreamReader(is));
        CSVReader indexedReader = null;
        try {
            String[] headers = baseReader.readNext();
            final String[] baseHeaders = new String[headers.length];
            for (int i = 0; i < headers.length; i++)
                baseHeaders[i] = headers[i].trim();
            indexedReader = new CSVReader(new FileReader(f));
            headers = indexedReader.readNext();
            final String[] indexHeaders = new String[headers.length];
            for (int i = 0; i < headers.length; i++)
                indexHeaders[i] = headers[i].trim();

            final int[] columnNumbers = this.whichColumn(baseHeaders, indexHeaders);
            final HashMap<String, String[]> indexMap = new HashMap<String, String[]>();
            // read in the target file
            String[] columns = null;
            while ((columns = indexedReader.readNext()) != null)
                indexMap.put(columns[columnNumbers[0]], columns);
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
                if (baseReader != null)
                    baseReader.close();
                if (indexedReader != null)
                    indexedReader.close();
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

    public void setDistance(String distance) {

        this.distance = distance;
    }

    public void setDistanceFilterText(String distanceFilterText) {

        this.distanceFilterText = distanceFilterText;
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
                for (final String h : strategy.getHeaders())
                    if (c.equalsIgnoreCase(h.trim())) {
                        found = true;
                        break;
                    }
                // if the column is not specified in configuration file, it's valid
                if (!found)
                    throw new Exception("Column: " + c + " is invalid column name");
            }
        }
    }

    private int[] whichColumn(String[] baseHeaders, String[] indexHeaders) {

        for (int i = 0; i < indexHeaders.length; i++)
            for (int j = 0; j < baseHeaders.length; j++)
                if (indexHeaders[i].equalsIgnoreCase(baseHeaders[j]))
                    return new int[] {
                    i,
                    j
                };
        return null;
    }
}