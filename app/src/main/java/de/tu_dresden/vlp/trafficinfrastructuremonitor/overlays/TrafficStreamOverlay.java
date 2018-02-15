package de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.layout.MainActivity;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStream;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.TrafficStreamElement;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Overlay} class that manages the presentation and interaction with a {@link TrafficStream} and its containments.
 * Created by Markus Wutzler on 25.01.18.
 */
public class TrafficStreamOverlay extends Overlay {
    private final TrafficStream myTrafficStream;
    private final Polyline myPolyline;
    private final List<ContainmentOverlay> myContainments;

    public TrafficStreamOverlay(TrafficStream myTrafficStream) {
        super();
        this.myTrafficStream = myTrafficStream;
        myPolyline = new Polyline();
        myPolyline.setPoints(myTrafficStream.getCoordinates());
        myPolyline.setColor(Color.BLUE);

        myContainments = new ArrayList<>();
        for (TrafficStreamElement trafficStreamElement : myTrafficStream.getContainments()) {
            ContainmentOverlay containmentOverlay = ContainmentOverlay.create(trafficStreamElement);
            if (containmentOverlay != null) {
                myContainments.add(containmentOverlay);
            }
        }
    }

    @Override
    public void draw(Canvas c, MapView mapView, boolean shadow) {
        // draw streams with a width of 10meters but at least 1.0f pixels.
        myPolyline.getPaint().setStrokeWidth(Math.max(mapView.getProjection().metersToPixels(10.0f), 1.0f));
        // draw containments
        myPolyline.draw(c, mapView, shadow);
        for (ContainmentOverlay containmentOverlay : myContainments) {
            containmentOverlay.draw(c, mapView, shadow);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e, MapView mapView) {
        if (hit(e, mapView)) {
            Context mapViewContext = mapView.getContext();
            if (mapViewContext instanceof MainActivity) {
                ((MainActivity) mapViewContext).onTrafficStreamSelected(this.myTrafficStream);
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * Instead of checking th precise match on the internal {@link Polyline}, the {@link TrafficStream}s {@link BoundingBox} is used.
     * Additionally, containments outside the box are checked conditionally.
     *
     * @param event   the {@link MotionEvent} {@see onSingleTapConfirmed}
     * @param mapView the {@link MapView}
     *
     * @return true if the touch point is inside the bounding box or precisely on a containment
     */
    private boolean hit(final MotionEvent event, final MapView mapView) {
        // TODO: If two bounding boxes overlap the behavior is ambiguous.
        BoundingBox bb = BoundingBox.fromGeoPoints(myTrafficStream.getCoordinates());
        boolean bb_contains = bb.contains(mapView.getProjection().fromPixels((int) event.getX(), (int) event.getY()));
        if (bb_contains)
            return true;
        else {
            for (ContainmentOverlay containmentOverlay : myContainments) {
                if (containmentOverlay.hit(event, mapView))
                    return true;
            }
        }
        return false;
    }
}
