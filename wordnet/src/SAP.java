/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class SAP {
    private static final int SHORTEST = 1;
    private static final int ANCESTOR = 2;
    private final Digraph dg;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null) {
            throw new java.lang.IllegalArgumentException("Null digraph input");
        }
        dg = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == w) {
            // if the same point, path length is 0
            return 0;
        }
        return findCommonAncestor(v, w, SHORTEST);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        return findCommonAncestor(v, w, ANCESTOR);
    }

    /**
     * Finds the common ancestor (or shortest path between v, w). Flag could be seen as hacky, but
     * it makes length() calls using this more efficient
     *
     * @param v    first vertex
     * @param w    second vertex
     * @param flag SHORTEST to return length of shortest path (-1 if none), ANCESTOR to retern
     *             shortest common ancestor (-1 if none)
     * @return Shortest path or shortest ancestor depending on flag
     */
    private int findCommonAncestor(int v, int w, int flag) {
        if (v == w) {
            // If they are the same point, that's the ancestor
            return v;
        }
        BreadthFirstDirectedPaths bfdpV = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths bfdpW = new BreadthFirstDirectedPaths(dg, w);
        int ancestor = -1;
        int minLen = Integer.MAX_VALUE;
        for (int vert = 0; vert < dg.V(); vert++) {
            if (bfdpV.hasPathTo(vert) && bfdpW.hasPathTo(vert)) {
                int lenTotal = bfdpV.distTo(vert) + bfdpW.distTo(vert);
                if (lenTotal < minLen) {
                    minLen = lenTotal;
                    ancestor = vert;
                }
            }
        }
        if (flag == SHORTEST) {
            if (minLen == Integer.MAX_VALUE) {
                // no path found
                return -1;
            }
            return minLen;
        }
        return ancestor;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        enforceNotNullIt(v);
        enforceNotNullIt(w);
        return findCommonAncestor(v, w)[0];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        enforceNotNullIt(v);
        enforceNotNullIt(w);
        return findCommonAncestor(v, w)[1];
    }

    private int[] findCommonAncestor(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths bfdpVIt = new BreadthFirstDirectedPaths(dg, v);
        BreadthFirstDirectedPaths bfdpWIt = new BreadthFirstDirectedPaths(dg, w);
        int minAncestor = -1;
        int minLen = Integer.MAX_VALUE;
        for (int vert = 0; vert < dg.V(); vert++) {
            if (bfdpVIt.hasPathTo(vert) && bfdpWIt.hasPathTo(vert)) {
                int lenTotal = bfdpVIt.distTo(vert) + bfdpWIt.distTo(vert);
                if (lenTotal < minLen) {
                    minLen = lenTotal;
                    minAncestor = vert;
                }
            }
        }
        if (minLen == Integer.MAX_VALUE) {
            // No path found
            minLen = -1;
        }
        return new int[] { minLen, minAncestor };
    }

    private void enforceNotNullIt(Iterable<Integer> test) {
        if (test == null) {
            throw new java.lang.IllegalArgumentException("Null iterator passed in");
        }
        Iterator<Integer> it = test.iterator();
        while (it.hasNext()) {
            if (it.next() == null) {
                throw new java.lang.IllegalArgumentException("Null value in iterator");
            }
        }
    }

    public static void main(String[] args) {
        // Main test code
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
        // digraph2 test code
        /*
        Digraph G = new Digraph(6);
        G.addEdge(1, 0);
        G.addEdge(1, 2);
        G.addEdge(2, 3);
        G.addEdge(3, 4);
        G.addEdge(4, 5);
        G.addEdge(5, 0);
        SAP test = new SAP(G);
        System.out.println(test.length(2, 0));
        System.out.println(test.ancestor(2, 0));*/
    }
}
