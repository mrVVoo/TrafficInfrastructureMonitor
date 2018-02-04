package de.tu_dresden.vlp.trafficinfrastructuremonitor.utils;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Markus Wutzler on 26.01.18.
 */
public class WktParser {
    public static List<GeoPoint> parseLineString(String lineString) {
        if (!lineString.toLowerCase().startsWith("linestring"))
            return Collections.emptyList();

        List<GeoPoint> line = new ArrayList<>();
        for (String stringCoordinate : lineString.substring(lineString.indexOf(" ") + 2, lineString.length() - 1).split(",")) {
            String[] coordinates = stringCoordinate.trim().split("\\s");
            line.add(new GeoPoint(Double.valueOf(coordinates[1]), Double.valueOf(coordinates[0])));
        }
        return line;
    }
}
