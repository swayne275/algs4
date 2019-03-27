/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private static final int topSite = 0; // value for the virtual top node
    private final int bottomSite;         // value for the virtual bottom node
    private final WeightedQuickUnionUF uf;

    private boolean[][] grid;
    private final int size;
    private int numOpenSites;

    // Create an n-by-n grid, with all sites blocked
    public Percolation(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("Percolation size can't be less than 1");
        }
        grid = new boolean[n][n]; // inits to false
        size = n;
        numOpenSites = 0;
        bottomSite = size * size + 1;
        // Need to store 2 extra sites for the topSite, bottomSite virtual sites
        uf = new WeightedQuickUnionUF(size * size + 2);
    }

    // open site (row, col) if it is not open already
    public void open(int row, int col) {
        checkBounds(row, col);
        if (isOpen(row, col)) {
            return;
        }

        grid[row - 1][col - 1] = true;
        numOpenSites++;
        int siteIdx = getIndex(row, col);

        if (row == 1) {
            // Top column, connect to the TOP node
            uf.union(siteIdx, topSite);
        }
        if (row == size) {
            // Bottom row, connect to the BOTTOM node
            uf.union(siteIdx, bottomSite);
        }

        if (col > 1 && isOpen(row, col - 1)) {
            // Connect to left node, if it exists and is open
            uf.union(siteIdx, getIndex(row, col - 1));
        }
        if (col < size && isOpen(row, col + 1)) {
            // Connect to right node, if it exists and is open
            uf.union(siteIdx, getIndex(row, col + 1));
        }
        if (row > 1 && isOpen(row - 1, col)) {
            // Connect to above node, if it exists and is open
            uf.union(siteIdx, getIndex(row - 1, col));
        }
        if (row < size && isOpen(row + 1, col)) {
            // Connect to below node, if it exists and is open
            uf.union(siteIdx, getIndex(row + 1, col));
        }
    }

    // is site (row, col) open?
    public boolean isOpen(int row, int col) {
        checkBounds(row, col);
        return grid[row - 1][col - 1];
    }

    // is site (row, col) connected to topSite?
    public boolean isFull(int row, int col) {
        return isOpen(row, col) && uf.connected(getIndex(row, col), topSite);
    }

    // number of open sites
    public int numberOfOpenSites() {
        return numOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        // if any node in the bottom row touches topSite we have percolated
        return uf.connected(topSite, bottomSite);
    }

    // Verify row/column given is not out of bounds
    private void checkBounds(int row, int col) {
        if (row < 1 || row > size) {
            throw new java.lang.IllegalArgumentException("Row index out of bounds");
        }
        else if (col < 1 || col > size) {
            throw new java.lang.IllegalArgumentException("Column index out of bounds");
        }
    }

    // Transform 2d (row, col) into 1d index (for looking at rofots)
    private int getIndex(int row, int col) {
        checkBounds(row, col);
        return size * (row - 1) + col;
    }

    public static void main(String[] args) {
        // Test client
        Percolation percTest = new Percolation(4);
        percTest.open(1, 1);
        System.out.println("Percolates?");
        System.out.println(percTest.percolates());
        percTest.open(2, 2);
        System.out.println("Percolates?");
        System.out.println(percTest.percolates());
        percTest.open(3, 1);
        System.out.println("Percolates?");
        System.out.println(percTest.percolates());
        percTest.open(4, 1);
        System.out.println("Percolates?");
        System.out.println(percTest.percolates());
        percTest.open(2, 1);
        System.out.println("Percolates?");
        System.out.println(percTest.percolates());
    }
}
