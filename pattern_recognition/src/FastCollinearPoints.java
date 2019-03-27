import edu.princeton.cs.algs4.StdIn;

import java.util.Arrays;

public class FastCollinearPoints {
    private LineSegment[] segments;
    private int numSegs;

    public FastCollinearPoints(Point[] points) {
        enforceNotNull(points);
        int n = points.length;

        // Copy the points, sort them by value, check for duplicates
        Point[] pointsCpy = new Point[n];
        System.arraycopy(points, 0, pointsCpy, 0, n);
        enforceNoDuplicates(pointsCpy);

        numSegs = 0;
        segments = new LineSegment[n];

        for (int iP = 0; iP < n; iP++) {
            Point p = pointsCpy[iP]; // "origin" point for this loop iteration

            // Copy in all points except for Point p and sort based on their slope with Point p
            Point[] pointsToCompare = new Point[n - 1];
            System.arraycopy(pointsCpy, 0, pointsToCompare, 0, iP); // copy Points before p
            System.arraycopy(pointsCpy, iP + 1, pointsToCompare, iP, n - iP - 1); // copy Points after p
            Arrays.sort(pointsToCompare, 0, pointsToCompare.length - 1, p.slopeOrder()); // sort based on slope with p

            int currentStartIndex = 0; // beginning of current segment (excluding p)
            for (int j = 1; j < n - 1; j++) {
                Point currentStartPoint = pointsToCompare[currentStartIndex];
                Point currentPoint = pointsToCompare[j];

                if (currentStartPoint.slopeTo(p) != currentPoint.slopeTo(p)) {
                    // Once we have found the end of the points of same slope with p we calculate the length
                    // of the segment with this slope
                    int segmentLength = j - currentStartIndex + 1;
                    if (segmentLength >= 4 && currentStartPoint.compareTo(p) > 0) {
                        // add a segment from the origin to the last point with the same slope
                        segments[numSegs++] = new LineSegment(p, pointsToCompare[j - 1]);

                        if (segments.length == numSegs) {
                            resize(numSegs * 2);
                        }
                    }
                    // reset our starting index to begin looking at points with the next slope
                    currentStartIndex = j;
                }
            }
        }
    }

    private void resize(int newSize) {
        LineSegment[] copy = new LineSegment[newSize];
        System.arraycopy(segments, 0, copy, 0, newSize / 2);
        segments = copy;
    }

    private void enforceNotNull(Point[] points) {
        if (points == null) {
            throw new java.lang.IllegalArgumentException("<points> is null");
        }

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new java.lang.IllegalArgumentException("Point " + (i + 1) + " is null");
            }
        }
    }

    private void enforceNoDuplicates(Point[] points) {
        // Go from O(n^2) to O(n*lgn)
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new java.lang.IllegalArgumentException("Found duplicate points");
            }
        }
    }

    public int numberOfSegments() {
        return numSegs;
    }

    public LineSegment[] segments() {
        LineSegment[] segsToReturn = new LineSegment[numSegs];
        System.arraycopy(segments, 0, segsToReturn, 0, numSegs);
        return segsToReturn;
    }

    public static void main(String[] args) {
        final int k = Integer.parseInt(StdIn.readString());
        int idx = 0;
        Point[] points = new Point[k];
        while (!StdIn.isEmpty()) {
            int x = Integer.parseInt(StdIn.readString());
            int y = Integer.parseInt(StdIn.readString());
            points[idx++] = new Point(x, y);
        }
        FastCollinearPoints fcP = new FastCollinearPoints(points);
        LineSegment[] segs = fcP.segments();
        System.out.println("Num segs: " + fcP.numberOfSegments());
        for (LineSegment ls : segs) {
            System.out.println(ls.toString());
        }
    }
}
