package de.tu_dresden.vlp.trafficinfrastructuremonitor.model;

import android.location.Location;
import org.osmdroid.util.GeoPoint;

/**
 * Bug Fix for hashCode Method
 *
 * @author Markus Wutzler
 * @see org.osmdroid.util.GeoPoint
 */
public class MyGeoPoint extends GeoPoint {
    public MyGeoPoint(double aLatitude, double aLongitude) {
        super(aLatitude, aLongitude);
    }

    public MyGeoPoint(double aLatitude, double aLongitude, double aAltitude) {
        super(aLatitude, aLongitude, aAltitude);
    }

    public MyGeoPoint(Location aLocation) {
        super(aLocation);
    }

    public MyGeoPoint(GeoPoint aGeopoint) {
        super(aGeopoint);
    }

    @Override
    public int hashCode() {
        return 37 * (17 * (int)(super.getLatitude()*1E6) + (int)(super.getLongitude()*1E6)) + (int)super.getAltitude();
    }
}
