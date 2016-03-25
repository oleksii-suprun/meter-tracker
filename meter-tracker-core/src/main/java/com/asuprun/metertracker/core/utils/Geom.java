package com.asuprun.metertracker.core.utils;

import org.opencv.core.Point;

import java.util.Arrays;
import java.util.List;

/**
 * This class like java class {@link java.lang.Math} contains basic methods for performing geometry calculations.
 *
 * @author asuprun
 * @since 1.0
 */
public class Geom {

    /**
     * Calculates intersection point of two lines in 2D space. Lines has to be provided as four points. Method will
     * return (Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY) if provided lines are parallel.
     *
     * @param p11 first point of first line
     * @param p12 second point of first line
     * @param p21 first point of second line
     * @param p22 second point of s
     * @return point of intersection.
     * @since 1.0
     */
    public static Point intersection(Point p11, Point p12, Point p21, Point p22) {
        double d = ((p11.x - p12.x) * (p21.y - p22.y)) - ((p11.y - p12.y) * (p21.x - p22.x));
        if (d == 0) {
            // lines are parallel
            return new Point(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        }
        Point result = new Point();
        result.x = ((p11.x * p12.y - p11.y * p12.x) * (p21.x - p22.x) - (p11.x - p12.x) * (p21.x * p22.y - p21.y * p22.x)) / d;
        result.y = ((p11.x * p12.y - p11.y * p12.x) * (p21.y - p22.y) - (p11.y - p12.y) * (p21.x * p22.y - p21.y * p22.x)) / d;
        return result;
    }

    /**
     * Calculates angle between x axis and provided line
     *
     * @param line line represented as two points array
     * @return angle in degrees in range [-90..90]
     * @since 1.0
     */
    public static double angle(Point[] line) {
        return angle(line[0], line[1]);
    }

    /**
     * Calculates angle between x axis and provided line which identified with two points
     *
     * @param p1 first point of line
     * @param p2 second point of line
     * @return angle in degrees in range [-90..90]
     * @since 1.0
     */
    public static double angle(Point p1, Point p2) {
        return Math.toDegrees(Math.atan((p2.y - p1.y) / (p2.x - p1.x)));
    }

    /**
     * Calculates mass center of provided points
     *
     * @param points array of points
     * @return point which represents center of mass for provided points
     * @since 1.0
     */
    public static Point massCenter(Point... points) {
        if (points == null || points.length == 0) {
            throw new IllegalArgumentException("Incoming array is null or empty");
        }
        return massCenter(Arrays.asList(points));
    }

    /**
     * Calculates mass center of provided points
     *
     * @param points list of points
     * @return point which represents center of mass for provided points
     * @since 1.0
     */
    public static Point massCenter(List<Point> points) {
        if (points == null || points.isEmpty()) {
            throw new IllegalArgumentException("Incoming list is null or empty");
        }
        double avgX = points.stream().mapToDouble(c -> c.x).sum() / points.size();
        double avgY = points.stream().mapToDouble(c -> c.y).sum() / points.size();
        return new Point(avgX, avgY);
    }

    /**
     * Calculates distance between two points.
     *
     * @param p1 first point
     * @param p2 second point
     * @return distance value
     * @since 1.0
     */
    public static double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }

    /**
     * Calculates distance between line defined by two points and third point
     *
     * @param lineStart start point of line
     * @param lineEnd   end point of line
     * @param point     point of interest
     * @return length of perpendicular from provided point to provided line
     * @since 1.0
     */
    public static double distance(Point lineStart, Point lineEnd, Point point) {
        return distance(new Point[]{lineStart, lineEnd}, point);
    }

    /**
     * Calculates distance between line defined as array and third point
     *
     * @param line  array of two points (start and end) which defines line
     * @param point point from which distance is calculated
     * @return length of perpendicular from provided point to provided line
     * @since 1.0
     */
    public static double distance(Point[] line, Point point) {
        double num = (line[0].y - line[1].y) * point.x
                + (line[1].x - line[0].x) * point.y
                + (line[0].x * line[1].y - line[1].x * line[0].y);
        return num / Math.sqrt(Math.pow(line[1].x - line[0].x, 2) + Math.pow(line[1].y - line[0].y, 2));
    }
}
