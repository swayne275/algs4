import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private LineSegment[] segments;
    private final int numPoints;


    public BruteCollinearPoints(Point[] points) {
        enforceNotNull(points);
        enforceNoDuplicates(Arrays.copyOf(points, points.length));
        numPoints = points.length;

        bruteGenerateSegments(points);
    }

    private void bruteGenerateSegments(Point[] points) {
        ArrayList<LineSegment> collinearSegs = new ArrayList<LineSegment>();
        Point[] sortedPoints = Arrays.copyOf(points, points.length);
        Arrays.sort(sortedPoints);

        for (int p = 0; p < numPoints - 3; p++) {
            for (int q = p + 1; q < numPoints - 2; q++) {
                double slopePQ = sortedPoints[p].slopeTo(sortedPoints[q]);
                for (int r = q + 1; r < numPoints - 1; r++) {
                    double slopeQR = sortedPoints[q].slopeTo(sortedPoints[r]);
                    if (slopePQ == slopeQR) {
                        for (int s = r + 1; s < numPoints; s++) {
                            double slopeRS = sortedPoints[r].slopeTo(sortedPoints[s]);
                            if (slopeQR == slopeRS) {
                                // We have 4 collinear points. They are sorted so no need to figure out boundary points
                                collinearSegs.add(new LineSegment(sortedPoints[p], sortedPoints[s]));
                            }
                        }
                    }
                }
            }
        }
        segments = collinearSegs.toArray(new LineSegment[collinearSegs.size()]);
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
        return segments.length;
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(segments, numberOfSegments());
    }

    public static void main(String[] args) {
        Point p1 = new Point(10000, 0);
        Point p2 = new Point(0, 10000);
        Point p3 = new Point(3000, 7000);
        Point p4 = new Point(7000, 3000);
        Point p5 = new Point(20000, 21000);
        Point p6 = new Point(3000, 4000);
        Point p7 = new Point(14000, 15000);
        Point p8 = new Point(6000, 7000);
        Point[] points = {p1, p2, p3, p4, p5, p6, p7, p8};
        BruteCollinearPoints bcP = new BruteCollinearPoints(points);
        LineSegment[] segs = bcP.segments();
        for (LineSegment ls : segs) {
            System.out.println(ls.toString());
        }
        System.out.println(bcP.numberOfSegments());
    }
}
