/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private final int numTrials;
    private final double[] percolationFraction;

    /*
     * perform <trials> independent experiments on an <n>-by-<n> grid
     *
     * @param n      Size of the grid
     * @param trials Number of trials to perform
     */
    public PercolationStats(int n, int trials) {
        final int numSites = (n * n);
        numTrials = trials;
        percolationFraction = new double[trials];

        for (int trialNum = 0; trialNum < numTrials; trialNum++) {
            Percolation exp = new Percolation(n);
            while (!exp.percolates()) {
                // Uniform as [a, b)
                int randRow = StdRandom.uniform(1, n + 1);
                int randCol = StdRandom.uniform(1, n + 1);
                exp.open(randRow, randCol);
            }
            percolationFraction[trialNum] = (double) exp.numberOfOpenSites() / numSites;
        }
    }

    /*
     * Get the sample mean for the percolation threshhold
     *
     * @return Percolation threshhold sample mean
     */
    public double mean() {
        return StdStats.mean(percolationFraction);
    }

    /*
     * Get the sample standard deviation for the percolation threshhold
     *
     * @return Percolation threshold sample standard deviation
     */
    public double stddev() {
        return StdStats.stddev(percolationFraction);
    }

    /*
     * Compute the added/subtracted part for the 95% confidence intervals
     *
     * @return The added/subtacted part for the 95% confidence intervals
     */
    private double confidenceHelper() {
        return (1.96 * stddev()) / Math.sqrt(numTrials);
    }

    /*
     * Get the low endpoint of the 95% confidence interval
     *
     * @return Low endpoint of 95% CI
     *
     */
    public double confidenceLo() {
        return mean() - confidenceHelper();
    }

    /*
     * Get the high endpoint of the 95% confidence interval
     *
     * @return High endpoint of 95% CI
     */
    public double confidenceHi() {
        return mean() + confidenceHelper();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Invalid number of inputs!");
        }
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);

        PercolationStats stats = new PercolationStats(n, t);
        System.out.println("mean                     = " + stats.mean());
        System.out.println("stddev                   = " + stats.stddev());
        System.out.println("95 % confidence interval = [" + stats.confidenceLo()
                                   + ", " + stats.confidenceHi() + "]");
    }
}
