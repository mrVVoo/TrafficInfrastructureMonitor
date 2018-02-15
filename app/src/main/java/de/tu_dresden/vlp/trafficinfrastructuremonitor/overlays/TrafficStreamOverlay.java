package de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays;

import android.content.Context;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.R;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.layout.MainActivity;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.layout.MapViewFragment;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

/**
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStreamOverlay extends Polyline implements Polyline.OnClickListener {
    private final TrafficStream myTrafficStream;

    public TrafficStreamOverlay(TrafficStream myTrafficStream) {
        super();
        this.myTrafficStream = myTrafficStream;
        this.setPoints(myTrafficStream.getCoordinates());
        setOnClickListener(this);
    }

    @Override
    public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
        Context mapViewContext = mapView.getContext();
        if (mapViewContext instanceof MainActivity) {
            ((MainActivity) mapViewContext).onTrafficStreamSelected(this.myTrafficStream);
            return true;
        }
        return false;
    }
}
