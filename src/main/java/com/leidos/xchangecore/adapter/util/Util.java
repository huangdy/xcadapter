package com.leidos.xchangecore.adapter.util;

import gov.niem.niem.niemCore.x20.ActivityDateDocument;
import gov.niem.niem.niemCore.x20.AreaType;
import gov.niem.niem.niemCore.x20.CircularRegionType;
import gov.niem.niem.niemCore.x20.DateTimeDocument;
import gov.niem.niem.niemCore.x20.DateType;
import gov.niem.niem.niemCore.x20.IncidentType;
import gov.niem.niem.niemCore.x20.LatitudeCoordinateType;
import gov.niem.niem.niemCore.x20.LengthMeasureType;
import gov.niem.niem.niemCore.x20.LongitudeCoordinateType;
import gov.niem.niem.niemCore.x20.MeasurePointValueDocument;
import gov.niem.niem.niemCore.x20.StatusType;
import gov.niem.niem.niemCore.x20.TextType;
import gov.niem.niem.niemCore.x20.TwoDimensionalGeographicCoordinateType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.leidos.xchangecore.adapter.model.MappedRecord;
import com.saic.precis.x2009.x06.base.AssociatedGroupsDocument.AssociatedGroups;
import com.saic.precis.x2009.x06.base.IdentificationType;
import com.saic.precis.x2009.x06.base.PropertiesType;
import com.saic.precis.x2009.x06.structures.WorkProductDocument.WorkProduct;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class Util {

    public static boolean contains(Double[][] bb, Point point) {

        final LinearRing bbLinerRing = new GeometryFactory().createLinearRing(getCoordinateArray(bb));
        final Polygon bbPloygon = new GeometryFactory().createPolygon(bbLinerRing, null);
        return point.within(bbPloygon);
    }

    public static String[] convertToDegMinSec(String decimal) {

        if (decimal.indexOf("\"") == -1)
            return toDegMinSec(decimal);
        int sign = 1;
        decimal = decimal.trim();
        if (decimal.endsWith("S") || decimal.endsWith("W"))
            sign = -1;
        decimal = decimal.substring(0, decimal.length() - 1).trim();
        final String[] ret = decimal.split(" ", -1);
        ret[0] = ret[0].substring(0, ret[0].length() - 1);
        if (sign == -1)
            ret[0] = new String("-" + ret[0]);
        ret[1] = ret[1].substring(0, ret[1].length() - 1);
        ret[2] = ret[2].substring(0, ret[2].length() - 1);
        return ret;
    }

    private static CircularRegionType createCircle(String latitude, String longitude) {

        final CircularRegionType circle = CircularRegionType.Factory.newInstance();

        circle.addNewCircularRegionCenterCoordinate().set(getCircleCenter(latitude, longitude));

        final LengthMeasureType radius = circle.addNewCircularRegionRadiusLengthMeasure();
        final MeasurePointValueDocument value = MeasurePointValueDocument.Factory.newInstance();
        value.addNewMeasurePointValue().setStringValue(ONE_POINT_ZERO);
        radius.set(value);

        return circle;
    }

    private static TwoDimensionalGeographicCoordinateType getCircleCenter(String latitude,
                                                                          String longitude) {

        final TwoDimensionalGeographicCoordinateType center = TwoDimensionalGeographicCoordinateType.Factory.newInstance();

        final LatitudeCoordinateType latCoord = LatitudeCoordinateType.Factory.newInstance();
        try {
            final String[] values = convertToDegMinSec(latitude);
            latCoord.addNewLatitudeDegreeValue().setStringValue(values[0]);
            latCoord.addNewLatitudeMinuteValue().setStringValue(values[1]);
            latCoord.addNewLatitudeSecondValue().setStringValue(values[2]);
        } catch (final NumberFormatException e) {
            System.err.println("Error parsing latitude: " + e.getMessage());
            latCoord.addNewLatitudeDegreeValue().setStringValue(ZERO);
            latCoord.addNewLatitudeMinuteValue().setStringValue(ZERO);
            latCoord.addNewLatitudeSecondValue().setStringValue(ZERO_POINT_ZERO);
        }
        center.setGeographicCoordinateLatitude(latCoord);

        final LongitudeCoordinateType lonCoord = LongitudeCoordinateType.Factory.newInstance();
        try {
            final String[] values = convertToDegMinSec(longitude);
            lonCoord.addNewLongitudeDegreeValue().setStringValue(values[0]);
            lonCoord.addNewLongitudeMinuteValue().setStringValue(values[1]);
            lonCoord.addNewLongitudeSecondValue().setStringValue(values[2]);
        } catch (final NumberFormatException e) {
            System.err.println("Error parsing latitude: " + e.getMessage());
            lonCoord.addNewLongitudeDegreeValue().setStringValue(ZERO);
            lonCoord.addNewLongitudeMinuteValue().setStringValue(ZERO);
            lonCoord.addNewLongitudeSecondValue().setStringValue(ZERO_POINT_ZERO);
        }
        center.setGeographicCoordinateLongitude(lonCoord);

        return center;
    }

    private static Coordinate[] getCoordinateArray(Double[][] coords) {

        final List<Coordinate> coordianteList = new ArrayList<Coordinate>();
        for (final Double[] coord : coords)
            coordianteList.add(new Coordinate(coord[0], coord[1]));
        return coordianteList.toArray(new Coordinate[coordianteList.size()]);
    }

    private static final IdentificationType getIdentificationElement(WorkProduct workProduct) {

        IdentificationType id = null;
        if (workProduct == null)
            System.err.println("Trying to get an identification element from a null work product");
        if (workProduct != null && workProduct.getPackageMetadata() != null) {
            final XmlObject[] objects = workProduct.getPackageMetadata().selectChildren(new QName(PRECISS_NS,
                                                                                                  WORKPRODUCT_IDENTIFICATION));
            if (objects.length > 0)
                id = (IdentificationType) objects[0];
        }
        return id;
    }

    public static final String getIGID(WorkProduct workProduct) {

        final AssociatedGroups associatedGroup = getPropertiesElement(workProduct).getAssociatedGroups();
        if (associatedGroup != null && associatedGroup.sizeOfIdentifierArray() > 0)
            return associatedGroup.getIdentifierArray(0).getStringValue();
        else
            return null;
    }

    public static IncidentType getIncidentDocument(MappedRecord record) {

        final IncidentType incident = IncidentType.Factory.newInstance();

        // set Activiy Category
        incident.addNewActivityCategoryText().setStringValue(StringEscapeUtils.escapeXml(record.getCategory()));

        // set Acitivity Name
        // incident.addNewActivityName().setStringValue(StringEscapeUtils.escapeXml(record.getTitle()));
        incident.addNewActivityName().setStringValue(record.getTitle());

        // set the Activity Location as circle
        incident.addNewIncidentLocation();
        final AreaType area = incident.getIncidentLocationArray(0).addNewLocationArea();
        area.addNewAreaCircularDescriptionText().setStringValue("Location: " + record.getTitle());
        area.addNewAreaCircularRegion().set(createCircle(record.getLatitude(),
            record.getLongitude()));

        // set the ActivityDate
        setIncidentDate(incident);

        // set the Activity Description using the content ?
        incident.addNewActivityDescriptionText();
        final TextType description = incident.getActivityDescriptionTextArray(0);
        // description.setStringValue(StringEscapeUtils.escapeXml(record.getDescription()));
        description.setStringValue(record.getDescription());

        // escape the content string
        // incident.addNewIncidentObservationText().setStringValue(StringEscapeUtils.escapeXml(record.getContent()));

        final StatusType status = incident.addNewActivityStatus();
        status.addNewStatusDescriptionText().setStringValue(StringEscapeUtils.escapeXml(record.getFilter()));

        // logger.debug("getIncidentDocument: " + incident.xmlText());

        return incident;
    }

    // TODO: should be put in a common utilities class
    private static String getNowAsString() {

        final Calendar cal = Calendar.getInstance();
        //SimpleDateFormat ISO8601Local = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        final SimpleDateFormat ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        final TimeZone timeZone = TimeZone.getDefault();
        ISO8601Local.setTimeZone(timeZone);
        return ISO8601Local.format(cal.getTime());
    }

    public static IdentificationType getProductIdentification(String workProductID) {

        try {
            return IdentificationType.Factory.parse(workProductID);
        } catch (final XmlException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public static final String getProductIdentification(WorkProduct workProduct) {

        // getIdentificationElement(workProduct).getIdentifier().getStringValue();
        return getIdentificationElement(workProduct).xmlText();
    }

    private static final PropertiesType getPropertiesElement(WorkProduct workProduct) {

        PropertiesType properties = null;
        if (workProduct != null && workProduct.getPackageMetadata() != null) {
            final XmlObject[] objects = workProduct.getPackageMetadata().selectChildren(new QName(PRECISS_NS,
                                                                                                  WORKPRODUCT_PROPERTIES));
            if (objects.length > 0)
                properties = (PropertiesType) objects[0];
        }
        return properties;
    }

    public static boolean insideBoundingBox(Double[][] boundingBox, String lat, String lon) {

        final Coordinate coords = new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat));
        final Point point = new GeometryFactory().createPoint(coords);

        return contains(boundingBox, point);
    }

    private static void setIncidentDate(IncidentType incident) {

        final DateTimeDocument dateDoc = DateTimeDocument.Factory.newInstance();
        dateDoc.addNewDateTime().setStringValue(getNowAsString());

        final ActivityDateDocument activityDate = ActivityDateDocument.Factory.newInstance();
        activityDate.addNewActivityDate().set(dateDoc);
        substitute(incident.addNewActivityDateRepresentation(),
            NIEM_NS,
            ACTIVITY_DATE,
            DateType.type,
            activityDate.getActivityDate());
    }

    /**
     * Helper class for handling substitution elements
     *
     * @param parentObject
     * @param subNamespace
     * @param subTypeName
     * @param subSchemaType
     * @param theObject
     */
    private static final void substitute(XmlObject parentObject,
                                         String subNamespace,
                                         String subTypeName,
                                         SchemaType subSchemaType,
                                         XmlObject theObject) {

        final XmlObject subObject = parentObject.substitute(new QName(subNamespace, subTypeName),
            subSchemaType);
        if (subObject == parentObject)
            System.out.println("cannot change to " + subTypeName);
        else
            subObject.set(theObject);
    }

    private static String[] toDegMinSec(String decimal) {

        double d = Double.parseDouble(decimal);
        final int degrees = (int) d;
        d = Math.abs(d - degrees) * 60;
        final int minutes = (int) d;
        final double seconds = (d - minutes) * 60 + 0.005;
        final String[] ret = new String[3];
        ret[0] = String.valueOf(degrees);
        ret[1] = String.valueOf(minutes);
        ret[2] = String.valueOf(seconds).substring(0, 5);
        return ret;
    }

    private static final String ACTIVITY_DATE = "ActivityDate";

    private static final Logger logger = LoggerFactory.getLogger(Util.class);

    private static final String NIEM_NS = "http://niem.gov/niem/niem-core/2.0";

    private static final String PRECISS_NS = "http://www.saic.com/precis/2009/06/structures";

    private static final String WORKPRODUCT_IDENTIFICATION = "WorkProductIdentification";

    private static final String WORKPRODUCT_PROPERTIES = "WorkProductProperties";

    private static final String ZERO = "0";

    private static final String ZERO_POINT_ZERO = "0.0";

    private static final String ONE_POINT_ZERO = "1.0";
}
