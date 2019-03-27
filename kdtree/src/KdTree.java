/* *****************************************************************************
 *  Name: Stephen Wayne
 *  Date: 2/13/2019
 *  Description: Part of Assignment 5
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;

public class KdTree {
    private static final int X_DIR = 1;
    private static final int Y_DIR = 2;
    private Node root;
    private Point2D nearest;
    private int size;

    /**
     * Construct an empty set of points
     */
    public KdTree() {
        root = null;
        size = 0;
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
        return size;
    }

    /**
     * If p not present, return the "should be" parent. Else return p's node
     *
     * @param p Point to search the tree for
     * @return "Should be" parent if not found, else node containing p
     */
    private Node getParent(Point2D p) {
        enforceNotNullPoint(p);
        Node parent = root;
        Node searchNode = parent;
        while (searchNode != null && !p.equals(searchNode.p)) {
            parent = searchNode;
            if (searchNode.orientation == X_DIR) {
                if (p.x() < searchNode.p.x()) {
                    searchNode = searchNode.lb;
                }
                else {
                    searchNode = searchNode.rt;
                }
            }
            else {
                if (p.y() < searchNode.p.y()) {
                    searchNode = searchNode.lb;
                }
                else {
                    searchNode = searchNode.rt;
                }
            }
        }

        if (searchNode == null) {
            // Didn't find parent, but p should be a child of this
            return parent;
        }
        return searchNode;
    }

    public void insert(Point2D p) {
        enforceNotNullPoint(p);
        if (contains(p)) {
            return;
        }

        if (root == null) {
            root = new Node(p, X_DIR, new RectHV(0, 0, 1, 1));
            size++;
            return;
        }

        // Add a leaf node
        Node parent = getParent(p);
        if (parent.orientation == X_DIR) {
            double parentX = parent.p.x(); // cut point calls in half
            if (p.x() < parentX) {
                parent.lb = new Node(p, Y_DIR, new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                                          parentX, parent.rect.ymax()));
            }
            else {
                parent.rt = new Node(p, Y_DIR, new RectHV(parentX, parent.rect.ymin(),
                                                          parent.rect.xmax(), parent.rect.ymax()));
            }
        }
        else {
            double parentY = parent.p.y(); // cut point calls in half
            if (p.y() < parentY) {
                parent.lb = new Node(p, X_DIR,
                                     new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                                parent.rect.xmax(), parentY));
            }
            else {
                parent.rt = new Node(p, X_DIR,
                                     new RectHV(parent.rect.xmin(), parentY,
                                                parent.rect.xmax(), parent.rect.ymax()));
            }
        }
        size++;
    }

    /**
     * does the set contain point p?
     *
     * @param p Point to check against
     * @return True if the set contains p
     */
    public boolean contains(Point2D p) {
        enforceNotNullPoint(p);
        if (isEmpty()) {
            return false;
        }

        Node parent = getParent(p);
        // getParent() returns containing node, if found
        return p.equals(parent.p);
    }

    /**
     * draw all points to standard draw
     */
    public void draw() {
        // !!! SW TODO
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
        range(rect, root, rectPoints);
        return rectPoints;
    }

    private void range(RectHV rect, Node n, LinkedList<Point2D> points) {
        if (n == null) {
            return;
        }
        if (rect.contains(n.p)) {
            points.add(n.p);
        }
        if (n.lb != null && rect.intersects(n.lb.rect)) {
            range(rect, n.lb, points);
        }
        if (n.rt != null && rect.intersects(n.rt.rect)) {
            range(rect, n.rt, points);
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
        nearest = root.p;
        nearest(root, p);
        return nearest;
    }

    private void nearest(Node n, Point2D p) {
        double distSq = nearest.distanceSquaredTo(p);

        if (n != null && n.rect.distanceSquaredTo(p) < distSq) {
            if (n.p.distanceSquaredTo(p) < distSq) {
                nearest = n.p;
            }
            nearest(n.lb, p);
            nearest(n.rt, p);
            /*
            if (n.orientation == X_DIR) {
                if (n.p.x() <= p.x()) {
                    // Probably have a better chance in left subtree
                    nearest(n.lb, p);
                    nearest(n.rt, p);
                }
                else {
                    // Probably have a better chance in right subtree
                    nearest(n.rt, p);
                    nearest(n.lb, p);
                }
            }
            else {
                if (n.p.y() <= p.y()) {
                    // Probably have a better chance in left subtree
                    nearest(n.lb, p);
                    nearest(n.rt, p);
                }
                else {
                    // Probably have a better chance in right subtree
                    nearest(n.rt, p);
                    nearest(n.lb, p);
                }
            }*/
        }
    }

    private static class Node {
        private final RectHV rect;     // the axis-aligned rectangle corresponding to this node
        private final int orientation; // 1 == x-oriented, 2 == y-oriented
        private Point2D p;             // the point
        private Node lb;               // the left/bottom subtree
        private Node rt;               // the right/top subtree

        public Node(Point2D p, int orientation, RectHV rect) {
            this.p = p;
            this.orientation = orientation;
            this.lb = null;
            this.rt = null;
            this.rect = rect;
        }
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

    public static void main(String[] args) {
        Point2D p1 = new Point2D(0.7, 0.2);
        Point2D p2 = new Point2D(0.5, 0.4);
        Point2D p3 = new Point2D(0.2, 0.3);
        Point2D p4 = new Point2D(0.4, 0.7);
        Point2D p5 = new Point2D(0.9, 0.6);
        // Point2D query = new Point2D(0.93, 0.48);
        // Point2D query = new Point2D(0.41, 0.27);
        Point2D query = new Point2D(0.284, 0.422);

        KdTree kdTest = new KdTree();
        kdTest.insert(p1);
        kdTest.insert(p2);
        kdTest.insert(p3);
        kdTest.insert(p4);
        kdTest.insert(p5);
        System.out.println("Nearest: " + kdTest.nearest(query));
    }
}
