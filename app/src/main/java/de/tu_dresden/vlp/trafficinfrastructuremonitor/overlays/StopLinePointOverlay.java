package de.tu_dresden.vlp.trafficinfrastructuremonitor.overlays;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.model.StopLinePoint;
import de.tu_dresden.vlp.trafficinfrastructuremonitor.utils.PointMath;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;

/**
 * Specific class for drawing and managing {@link StopLinePoint}s.
 *
 * @author Markus Wutzler
 */
public class StopLinePointOverlay extends ContainmentOverlay {
    private final StopLinePoint stopLinePoint;
    private Paint mPaint;
    private Point coordinate;

    public StopLinePointOverlay(StopLinePoint stopLinePoint) {
        this.stopLinePoint = stopLinePoint;
        this.mPaint = new Paint();
        this.mPaint.setColor(Color.RED);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setAntiAlias(true);
    }

    @Override
    public boolean hit(MotionEvent event, MapView mapView) {
        final Projection pj = mapView.getProjection();
        GeoPoint eventPos = (GeoPoint) pj.fromPixels((int) event.getX(), (int) event.getY());
        double distance = PointMath.distance(coordinate, pj.toPixels(eventPos, null));
        return  distance< 5.0f;
    }


    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        Projection projection = mapView.getProjection();
        coordinate = projection.toPixels(stopLinePoint.getPointCoordinates().get(0), coordinate);
        canvas.drawCircle(coordinate.x, coordinate.y, Math.max(projection.metersToPixels(10.0f),1.0f), mPaint);
    }
}
