import edu.princeton.cs.algs4.StdRandom;

import java.util.LinkedList;

public class Board {
    private static final int BLANK_SQUARE = 0;
    private final int dimension;
    private final int[][] tiles;

    private int blankRow = -1;
    private int blankCol = -1;
    private int manhattan = -1;
    private int hamming = -1;

    private int sr1 = -1;
    private int sr2 = -1;
    private int sc1 = -1;
    private int sc2 = -1;

    /**
     * Construct a board from an n-by-n array of blocks, and build the goal
     *
     * @param blocks Inital game
     */
    public Board(int[][] blocks) {
        dimension = blocks.length;
        tiles = new int[dimension][dimension];

        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                tiles[row][col] = blocks[row][col];
                if (tiles[row][col] == BLANK_SQUARE) {
                    blankRow = row;
                    blankCol = col;
                }
            }
        }
    }

    private Board(int[][] blocks, int i1, int j1, int i2, int j2, int ham, int man) {
        dimension = blocks.length;
        tiles = new int[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                tiles[row][col] = blocks[row][col];
                if (tiles[row][col] == BLANK_SQUARE) {
                    blankRow = row;
                    blankCol = col;
                }
            }
        }
        int temp = tiles[i1][j1];
        tiles[i1][j1] = tiles[i2][j2];
        tiles[i2][j2] = temp;

        // cache hamming and manhattan to save n^2 time on those calls
        this.hamming = ham;
        this.manhattan = man;
    }

    public int dimension() {
        // board dimension n
        return dimension;
    }

    public int hamming() {
        // number of blocks out of place
        if (hamming != -1) {
            // We have cached the value for this block so return it
            return hamming;
        }
        hamming = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                int expectedVal = (row * dimension) + col + 1;
                if (tiles[row][col] != expectedVal) {
                    if (tiles[row][col] != BLANK_SQUARE) {
                        // We don't punish the blank square
                        hamming++;
                    }
                }
            }
        }
        return hamming;
    }

    /**
     * Calculate the manhattan distance for tiles
     *
     * @return Manhattan distance of tiles
     */
    public int manhattan() {
        // sum of Manhattan distances between blocks and goal
        if (manhattan != -1) {
            return manhattan;
        }

        manhattan = 0;
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (tiles[row][col] != BLANK_SQUARE) {
                    manhattan += manhattanSub(tiles[row][col], row, col);
                }
            }
        }
        return manhattan;
    }

    /**
     * Computes the manhattan distance of a single block
     *
     * @param value   Value in the block in question
     * @param currRow Row location of value
     * @param currCol Column location of value
     * @return Manhattan distance for value
     */
    private int manhattanSub(int value, int currRow, int currCol) {
        // Derive the "correct" location for <value>
        int correctRow = (value - 1) / dimension; // floor integer division
        int correctCol = (value - 1) % dimension;

        int dRow = Math.abs(currRow - correctRow);
        int dCol = Math.abs(currCol - correctCol);
        return dRow + dCol;
    }

    public boolean isGoal() {
        // is this board the goal board?
        return hamming() == 0;
    }

    public Board twin() {
        // a board that is obtained by exchanging any pair of blocks
        if (sc1 == -1) {
            // We haven't yet generated our random numbers
            while (((sr1 == sr2) && (sc1 == sc2)) || (tiles[sr1][sc1] == BLANK_SQUARE) || (
                    tiles[sr2][sc2] == BLANK_SQUARE)) {
                // Blank square does not count as a tile
                sr1 = StdRandom.uniform(dimension);
                sr2 = StdRandom.uniform(dimension);
                sc1 = StdRandom.uniform(dimension);
                sc2 = StdRandom.uniform(dimension);
            }
        }

        // return exch(row1, col1, row2, col2);
        // Can't preserve mahattan/hamming for this operation
        return new Board(tiles, sr1, sc1, sr2, sc2, -1, -1);
    }

    public boolean equals(Object y) {
        // does this board equal y?
        if (y == this) {
            return true;
        }
        else if (y == null) {
            return false;
        }
        else if (this.getClass() != y.getClass()) {
            return false;
        }

        Board yBoard = (Board) y; // required ugly cast

        if (yBoard.dimension() != dimension) {
            return false;
        }
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                if (this.tiles[row][col] != yBoard.tiles[row][col]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Exchange two elements and return the new board. We must copy the original board to avoid
     * mutating the original object
     *
     * @param row1 First element row to exchange
     * @param col1 First element col to exchange
     * @param row2 Second element row to exchange
     * @param col2 Second element col to exchanage
     * @return New board after the exchange
     */
    private Board exch(int row1, int col1, int row2, int col2) {
        int[][] newBoard = new int[dimension][dimension];
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                // Copy tiles into a new data strucutre
                newBoard[row][col] = tiles[row][col];
            }
        }
        int temp = newBoard[row1][col1];
        newBoard[row1][col1] = newBoard[row2][col2];
        newBoard[row2][col2] = temp;
        return new Board(newBoard);
    }

    public Iterable<Board> neighbors() {
        // All neighboring boards
        LinkedList<Board> neighborBoards = new LinkedList<Board>();

        // add board with blank moved left (if applicable)
        if (blankCol > 0) {
            neighborBoards.add(exch(blankRow, blankCol - 1, blankRow, blankCol));
        }
        // add board with blank moved right (if applicable)
        if (blankCol < (dimension - 1)) {
            neighborBoards.add(exch(blankRow, blankCol + 1, blankRow, blankCol));
        }
        // add board with blank moved up (if applicable)
        if (blankRow > 0) {
            neighborBoards.add(exch(blankRow - 1, blankCol, blankRow, blankCol));
        }
        // add board with blank moved down (if applicable)
        if (blankRow < (dimension - 1)) {
            neighborBoards.add(exch(blankRow + 1, blankCol, blankRow, blankCol));
        }

        return neighborBoards;
    }

    public String toString() {
        // string representation of this board
        StringBuilder s = new StringBuilder();
        s.append(dimension + "\n");
        for (int row = 0; row < dimension; row++) {
            for (int col = 0; col < dimension; col++) {
                s.append(String.format("%2d ", tiles[row][col]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args) {
        // unit tests
        int[][] test = new int[2][2];
        test[0][0] = 1;
        test[0][1] = 0;
        test[1][0] = 2;
        test[1][1] = 3;
        Board b = new Board(test);
        /*
        Iterable<Board> n1 = b.neighbors();
        for (Board theB : n1) {
            System.out.println(theB.toString());
        }*/
        System.out.println("Original:\n" + b.toString());
        Board twin = b.twin();
        System.out.println("Twin:\n" + twin.toString());
        System.out.println("Original again:\n" + b.toString());
    }
}
