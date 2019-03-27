/* *****************************************************************************
 *  Name: Stephen Wayne
 *  Date: 2/13/2019
 *  Description: Part of Assignment 5
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;

public class PointSET {
    private SET<Point2D> points;

    /**
     * Construct an empty set of points
     */
    public PointSET() {
        points = new SET<Point2D>();
    }

    /**
     * is the set empty?
     *
     * @return True if empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * number of points in the set
     *
     * @return Number of points in the set
     */
    public int size() {
        return points.size();
    }

    /**
     * add the point to the set (if it is not already in the set)
     *
     * @param p Point to add to the set
     */
    public void insert(Point2D p) {
        enforceNotNullPoint(p);
        if (!contains(p)) {
            points.add(p);
        }
    }

    /**
     * does the set contain point p?
     *
     * @param p Point to check against
     * @return True if the set contains p
     */
    public boolean contains(Point2D p) {
        enforceNotNullPoint(p);
        return points.contains(p);
    }

    /**
     * draw all points to standard draw
     */
    public void draw() {
        // Draw the points
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        for (Point2D point : points) {
            point.draw();
        }

        // Draw the splitting lines !!! SW TODO
        StdDraw.setPenColor(StdDraw.RED);
        StdDraw.setPenRadius();
    }

    /**
     * all points that are inside the rectangle (or on the boundary)
     *
     * @param rect Rectangle to check against
     * @return All points in the rectangle (or on the boundary)
     */
    public Iterable<Point2D> range(RectHV rect) {
        enforceNotNullRectangle(rect);
        LinkedList<Point2D> rectPoints = new LinkedList<Point2D>();
        for (Point2D point : points) {
            if (rect.contains(point)) {
                rectPoints.add(point);
            }
        }
        return rectPoints;
    }

    private void enforceNotNullPoint(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException("Point is null");
        }
    }

    private void enforceNotNullRectangle(RectHV rect) {
        if (rect == null) {
            throw new java.lang.IllegalArgumentException("Rectangle is null");
        }
    }

    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     *
     * @param p Point to check against
     * @return Nearest neighbor point; null if set is empty
     */
    public Point2D nearest(Point2D p) {
        enforceNotNullPoint(p);
        if (isEmpty()) {
            return null;
        }
        Point2D nearestPoint = null;
        double nearestDistanceSQ = 4.0;
        for (Point2D pointToCompare : points) {
            double distSQ = p.distanceSquaredTo(pointToCompare);
            if (distSQ < nearestDistanceSQ) {
                nearestDistanceSQ = distSQ;
                nearestPoint = pointToCompare;
            }
        }
        return nearestPoint;
    }

    public static void main(String[] args) {
        // intentionally empty
    }
}
