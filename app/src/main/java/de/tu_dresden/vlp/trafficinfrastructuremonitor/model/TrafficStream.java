package de.tu_dresden.vlp.trafficinfrastructuremonitor.model;

import de.tu_dresden.vlp.trafficinfrastructuremonitor.utils.WktParser;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStream {
    private String id;
    private List<GeoPoint> coordinates;
    private List<TrafficStreamElement> containments;

    public TrafficStream(String id, List<GeoPoint> coordinates) {
        this.id = id;
        this.coordinates = coordinates;
        this.containments = new ArrayList<>();
    }

    public TrafficStream(String id, String coordinatesWkt) {
        this(id, WktParser.parseLineString(coordinatesWkt));
    }

    public String getId() {
        return id;
    }

    public List<GeoPoint> getCoordinates() {
        return coordinates;
    }

    public List<TrafficStreamElement> getContainments() {
        return containments;
    }

    @Override
    public String toString() {
        return "TrafficStream{" +
                "id='" + id + '\'' +
                ", coordinates=" + coordinates +
                ", containments=" + containments +
                '}';
    }
}
