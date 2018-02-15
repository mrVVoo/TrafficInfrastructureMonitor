package de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays;

import android.view.MotionEvent;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.StopLinePoint;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStreamElement;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;


/**
 * Generic super class for all overlays part of a {@link de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream}.
 *
 * @author Markus Wutzler
 */
public abstract class ContainmentOverlay extends Overlay {

    /**
     * Returns an overlay for a {@link TrafficStreamElement}.
     *
     * @param element the {@link TrafficStreamElement}
     *
     * @return a concrete overlay or null
     */
    public static ContainmentOverlay create(TrafficStreamElement element) {
        if (element instanceof StopLinePoint) {
            return new StopLinePointOverlay((StopLinePoint) element);
        } else
            return null;
    }

    /**
     * Returns true if the overlay was touched.
     *
     * @param event   the {@link MotionEvent} required for the position
     * @param mapView the {@link MapView}
     *
     * @return true if the overlay was hit.
     */
    public abstract boolean hit(final MotionEvent event, final MapView mapView);
}
