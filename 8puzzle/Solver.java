import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.LinkedList;

public class Solver {
    private LinkedList<Board> solution;
    private int numMoves = -1;
    private boolean isSolvable = false;

    private class SearchNode {
        private Board board;
        private SearchNode prevSearchNode;
        private int movesSoFar;

        public SearchNode(Board b) {
            this(b, null, 0);
        }

        public SearchNode(Board b, SearchNode prevSearchNode) {
            this(b, prevSearchNode, prevSearchNode.movesSoFar + 1);
        }

        private SearchNode(Board b, SearchNode prevSearchNode, int movesSoFar) {
            this.board = b;
            this.prevSearchNode = prevSearchNode;
            this.movesSoFar = movesSoFar;
        }
    }

    /**
     * find a solution to the inital board using the A* algorithm
     *
     * @param initial Initial board to solve for
     */
    public Solver(Board initial) {
        if (initial == null) {
            throw new java.lang.IllegalArgumentException();
        }

        solution = new LinkedList<Board>();

        Comparator<SearchNode> comparator = new Comparator<SearchNode>() {
            @Override
            public int compare(SearchNode sn1, SearchNode sn2) {
                int manhattan1 = sn1.board.manhattan();
                int manhattan2 = sn2.board.manhattan();
                int priority1 = sn1.movesSoFar + manhattan1;
                int priority2 = sn2.movesSoFar + manhattan2;
                if (priority1 == priority2) {
                    // Say the one with fewer moves is better
                    return manhattan1 - manhattan2;
                }
                return priority1 - priority2;
            }
        };

        MinPQ<SearchNode> mainMinPQ = new MinPQ<SearchNode>(comparator);
        MinPQ<SearchNode> twinMinPQ = new MinPQ<SearchNode>(comparator);

        SearchNode mainSearchNode = new SearchNode(initial);
        SearchNode twinSearchNode = new SearchNode(initial.twin());

        // Arrive at the solution to one of the two boards
        while (!mainSearchNode.board.isGoal() && !twinSearchNode.board.isGoal()) {
            // add neighbors not same as prev
            for (Board b : mainSearchNode.board.neighbors()) {
                if (mainSearchNode.prevSearchNode == null ||
                        !b.equals(mainSearchNode.prevSearchNode.board)) {
                    mainMinPQ.insert(new SearchNode(b, mainSearchNode));
                }
            }
            for (Board b : twinSearchNode.board.neighbors()) {
                if (twinSearchNode.prevSearchNode == null ||
                        !b.equals(twinSearchNode.prevSearchNode.board)) {
                    twinMinPQ.insert(new SearchNode(b, twinSearchNode));
                }
            }

            // delete minimum priority search node from each priority queue, advance the move
            mainSearchNode = mainMinPQ.delMin();
            twinSearchNode = twinMinPQ.delMin();
        }

        if (mainSearchNode.board.isGoal()) {
            // The un-altered board is the solution, therefore we are solvable
            this.isSolvable = true;
            this.numMoves = mainSearchNode.movesSoFar;
            SearchNode walker = mainSearchNode;
            while (walker != null) {
                Board solutionStep = walker.board;
                // looping and adding latest un-added move to beginning will
                // put them in order from first move -> last move
                solution.addFirst(solutionStep);
                walker = walker.prevSearchNode;
            }
        }
    }

    public boolean isSolvable() {
        // is the initial board solvable?
        return isSolvable;
    }

    public int moves() {
        // min number of moves to solve initial board; -1 if unsolvable
        return numMoves;
    }

    public Iterable<Board> solution() {
        // sequence of boards in a shortest solution; null if unsolvable
        if (!isSolvable()) {
            return null;
        }
        return solution;
    }

    public static void main(String[] args) {
        // solve a slider puzzle
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
