package de.tu_dresden.vlp.trafficinfrastructuremonitor.utils;

import android.graphics.Point;

/**
 * Helper Class for dealing with {@link Point} calculations.
 */
public class PointMath {
    /**
     * Compute the distance from A to B
     **/
    public static double distance(Point A, Point B) {
        double dX = A.x - B.x;
        double dY = A.y - B.y;
        return Math.sqrt(dX * dX + dY * dY);
    }

    /**
     * Compute the dot product AB x AC
     **/
    public static double dot(Point A, Point B, Point C) {
        double AB_X = B.x - A.x;
        double AB_Y = B.y - A.y;
        double BC_X = C.x - B.x;
        double BC_Y = C.y - B.y;
        return AB_X * BC_X + AB_Y * BC_Y;
    }

    /**
     * Compute the cross product AB x AC
     */
    public static double cross(Point A, Point B, Point C) {
        double AB_X = B.x - A.x;
        double AB_Y = B.y - A.y;
        double AC_X = C.x - A.x;
        double AC_Y = C.y - A.y;
        return AB_X * AC_Y - AB_Y * AC_X;
    }
}
