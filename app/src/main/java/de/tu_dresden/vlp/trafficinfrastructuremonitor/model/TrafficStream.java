package de.tu_dresden.vlp.trafficinfrastructuremonitor.model;

import org.osmdroid.util.GeoPoint;

import java.util.List;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStream {
    private String id;
    private List<GeoPoint> coordinates;
    private List<TrafficStreamElement> containments;

    public String getId() {
        return id;
    }

    public List<GeoPoint> getCoordinates() {
        return coordinates;
    }

    public List<TrafficStreamElement> getContainments() {
        return containments;
    }
}
