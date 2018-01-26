package de.tu_dresden.vlp.trafficinfrastructuremonitor.model;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Markus Wutzler on 26.01.18.
 */
public class StopLinePoint extends TrafficStreamElement {
    private List<GeoPoint> pointCoordinates;

    public StopLinePoint(List<GeoPoint> pointCoordinates) {
        this.pointCoordinates = new ArrayList<>(pointCoordinates);
    }

    public List<GeoPoint> getPointCoordinates() {
        return pointCoordinates;
    }
}
