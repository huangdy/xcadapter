package com.leidos.xchangecore.adapter.util;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

public class LatLonConversionTest {

    @Test
    public void testLatLonConversion() {

        final String latInTime = "37° 47' 52.971\" N";
        final String lonInTime = "122° 24' 28.121\" W";
        final String latInDecimal = "37.798047";
        final String lonInDecimal = "-122.407811";

        System.out.println("latInTime: [" + StringEscapeUtils.escapeXml(latInTime) + "]");
        System.out.println("latInTime: [" + StringEscapeUtils.escapeJava(latInTime) + "]");
        System.out.println("latInTime: [" + StringEscapeUtils.escapeSql(latInTime) + "]");
        String[] locs = Util.convertToDegMinSec(latInTime);
        System.out.println("[" + latInTime + "] -> [" + locs[0] + "* " + locs[1] + "' " + locs[2] +
            "\"]");
        locs = Util.convertToDegMinSec(lonInTime);
        System.out.println("[" + lonInTime + "] -> [" + locs[0] + "* " + locs[1] + "' " + locs[2] +
            "\"]");
        locs = Util.convertToDegMinSec(latInDecimal);
        System.out.println("[" + latInDecimal + "] -> [" + locs[0] + "* " + locs[1] + "' " +
            locs[2] + "\"]");
        locs = Util.convertToDegMinSec(lonInDecimal);
        System.out.println("[" + lonInDecimal + "] -> [" + locs[0] + "* " + locs[1] + "' " +
            locs[2] + "\"]");
    }
}
