package de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays;

import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import org.osmdroid.views.overlay.Polyline;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStreamOverlay extends Polyline {
    private final TrafficStream myTrafficStream;

    public TrafficStreamOverlay(TrafficStream myTrafficStream) {
        super();
        this.myTrafficStream = myTrafficStream;
        this.setPoints(myTrafficStream.getCoordinates());
    }
}
