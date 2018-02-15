package de.tu_dresden.vlp.trafficinfrastructuremonitor.model;

import com.google.common.base.Objects;
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

    @Override
    public int hashCode() {
        return Objects.hashCode(getPointCoordinates());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StopLinePoint)) return false;
        StopLinePoint that = (StopLinePoint) o;
        return Objects.equal(getPointCoordinates(), that.getPointCoordinates());
    }
}
