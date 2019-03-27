/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    // todo figure out transpose for horizontal - i think height()/width() logic wrong and should be based on size of pixel array or something
    // todo more efficient transpose logic
    private Picture picture;
    private int[][] pixels;
    private boolean isTranspose;
    private int height;
    private int width;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        enforcePicture(picture);
        isTranspose = false;
        this.picture = new Picture(picture);
        height = this.picture.height();
        width = this.picture.width();
        fillPixels();
    }

    private void fillPixels() {
        pixels = new int[width()][height()];
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                pixels[col][row] = picture.getRGB(col, row);
            }
        }
    }

    // Calculate x gradient - do not call on horizontal border pixels
    private double xGradSq(int x, int y) {
        Color cLeft = picture.get(x - 1, y);
        Color cRight = picture.get(x + 1, y);
        double gRedSq = Math.pow(cLeft.getRed() - cRight.getRed(), 2);
        double gGreenSq = Math.pow(cLeft.getGreen() - cRight.getGreen(), 2);
        double gBlueSq = Math.pow(cLeft.getBlue() - cRight.getBlue(), 2);
        return gRedSq + gGreenSq + gBlueSq;
    }

    // Calculate y gradient - do not call on vertical border pixels
    private double yGradSq(int x, int y) {
        Color cUp = picture.get(x, y - 1);
        Color cDown = picture.get(x, y + 1);
        double gRedSq = Math.pow(cUp.getRed() - cDown.getRed(), 2);
        double gGreenSq = Math.pow(cUp.getGreen() - cDown.getGreen(), 2);
        double gBlueSq = Math.pow(cUp.getBlue() - cDown.getBlue(), 2);
        return gRedSq + gGreenSq + gBlueSq;
    }

    // current picture
    public Picture picture() {
        Picture returnPic = new Picture(width(), height());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                returnPic.setRGB(col, row, pixels[col][row]);
            }
        }
        return returnPic;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        enforceProperIndex(x, true);
        enforceProperIndex(y, false);
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) {
            // Border pixels are defined as having energy 1000
            return 1000.0;
        }
        return Math.sqrt(xGradSq(x, y) + yGradSq(x, y));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // !!! todo more efficient transpose
        transpose();
        int[] bestPath = findVerticalSeam();
        transpose();
        return bestPath;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (width() < 3 || height() < 3) {
            // All we have are edge pixels, so quickly bail out
            int[] bestPathEdge = new int[height()];
            for (int row = 0; row < height(); row++) {
                bestPathEdge[row] = 0;
            }
            return bestPathEdge;
        }

        double[][] totalEnergyTo = new double[width()][height()];
        int[][] parCol = new int[width()][height()];
        int[] bestPath = new int[height()]; // store best path

        // Fill in the first row (where the vertical seam starts)
        for (int col = 0; col < width(); col++) {
            totalEnergyTo[col][0] = 1000.0;
            // Store the column of the lowest-path parent to this vertex
            parCol[col][0] = -1;
        }

        for (int row = 1; row < height(); row++) {
            // Border pixels have strictly more energy than non-boarders
            totalEnergyTo[0][row] = totalEnergyTo[0][row - 1] + 1000.0;
            parCol[0][row] = 0;
            totalEnergyTo[width() - 1][row] = totalEnergyTo[width() - 1][row - 1] +
                    energy(width() - 1, row);
            parCol[width() - 1][row] = width() - 1;
            for (int col = 1; col < width() - 1; col++) {
                // parents are: (row - 1) at {col - 1, col, col + 1}
                // we are checking each pixel
                double parEnergyLeft = totalEnergyTo[col - 1][row - 1];
                double parEnergyUp = totalEnergyTo[col][row - 1];
                double parEnergyRight = totalEnergyTo[col + 1][row - 1];
                double baseEnergy = energy(col, row);

                if ((parEnergyLeft <= parEnergyUp) && (parEnergyLeft <= parEnergyRight)) {
                    totalEnergyTo[col][row] = parEnergyLeft + baseEnergy;
                    parCol[col][row] = col - 1;
                }
                else if (parEnergyUp <= parEnergyRight) {
                    totalEnergyTo[col][row] = parEnergyUp + baseEnergy;
                    parCol[col][row] = col;
                }
                else {
                    totalEnergyTo[col][row] = parEnergyRight + baseEnergy;
                    parCol[col][row] = col + 1;
                }
            }
        }

        // start the search at second-left-most element of bottom row
        int minEnergyCol = 1;
        // bottom row has same border energy, start 2nd from bottom
        double minEnergyBottom = totalEnergyTo[minEnergyCol][height() - 2];

        // Find the second-from-bottom-row pixel requiring the least energy to get to
        // Note: we know not to look at high-energy border pixels
        for (int bottomPixel = 2; bottomPixel < width() - 1; bottomPixel++) {
            if (totalEnergyTo[bottomPixel][height() - 2] < minEnergyBottom) {
                minEnergyBottom = totalEnergyTo[bottomPixel][height() - 2];
                minEnergyCol = bottomPixel;
            }
        }

        // generate the best path - choose same bottom pixel as second-from-bot
        // min energy pixel
        bestPath[height() - 1] = minEnergyCol;
        bestPath[height() - 2] = minEnergyCol;
        for (int row = height() - 2; row > 0; row--) {
            // pixel in question located at {bestPath[row], row}
            bestPath[row - 1] = parCol[bestPath[row]][row];
        }
        return bestPath;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        enforceSeam(seam, false);
        for (int col = 0; col < width(); col++) {
            for (int row = seam[col]; row < height() - 1; row++) {
                // for each pixel from the seam towards down, shift up
                pixels[col][row] = pixels[col][row + 1];
            }
        }
        height--;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        enforceSeam(seam, true);
        for (int row = 0; row < height(); row++) {
            for (int col = seam[row]; col < width() - 1; col++) {
                // For each pixel from the seam towards the right, shift left
                pixels[col][row] = pixels[col + 1][row];
            }
        }
        width--;
    }

    // Transpose the energy array
    private void transpose() {
        // Store the original dimensions since they will change with transpose
        final int[][] originalPixels = pixels.clone();
        Picture newPic = new Picture(height(), width());
        pixels = new int[height()][width()];
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                pixels[row][col] = originalPixels[col][row];
                newPic.setRGB(row, col, pixels[row][col]);
            }
        }
        int tempWidth = width;
        width = height;
        height = tempWidth;
        picture = newPic;
        isTranspose = !isTranspose;
    }

    private void enforceProperIndex(int v, boolean isWidth) {
        if (isWidth && (v < 0 || v > width() - 1)) {
            throw new java.lang.IllegalArgumentException("x is " + v + " and width is " + width());
        }
        else if (!isWidth && (v < 0 || v > height() - 1)) {
            throw new java.lang.IllegalArgumentException(
                    "y is " + v + " and height is " + height());
        }
    }

    private void enforcePicture(Picture pic) {
        if (pic == null) {
            throw new java.lang.IllegalArgumentException("pic is null");
        }
    }

    private void enforceSeam(int[] seam, boolean vertical) {
        if (seam == null) {
            throw new java.lang.IllegalArgumentException("seam is null");
        }

        if (vertical && seam.length != height()) {
            throw new java.lang.IllegalArgumentException("invalid seam height");
        }
        else if (!vertical && seam.length != width()) {
            throw new java.lang.IllegalArgumentException("invalid seam width");
        }

        int prevSeam = seam[0]; // for the first iteration
        for (int v : seam) {
            enforceProperIndex(v, vertical);
            if (Math.abs(v - prevSeam) > 1) {
                throw new java.lang.IllegalArgumentException("vertex differs by more than 1");
            }
            prevSeam = v;
        }

        if (vertical && width() <= 1) {
            throw new java.lang.IllegalArgumentException("pic width too small");
        }
        else if (!vertical && height() <= 1) {
            throw new java.lang.IllegalArgumentException("picheight too small");
        }
    }

    // Testing
    public static void main(String[] args) {
        // intentionally empty
    }
}
