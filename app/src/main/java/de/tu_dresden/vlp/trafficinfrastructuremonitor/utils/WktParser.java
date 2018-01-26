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
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\s\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(lineString);
        for (int i = 0; i < matcher.groupCount(); i++) {
            String[] coordinates = matcher.group(i).split("\\s");
            line.add(new GeoPoint(Double.valueOf(coordinates[1]), Double.valueOf(coordinates[0])));
        }
        return line;
    }
}
