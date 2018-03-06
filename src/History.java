import java.io.Serializable;
import java.util.ArrayList;

/**
 * History keeps track of the current state of the Model’s variables, plus any
 * past or previous states, to enable undo and redo.
 * 
 *
 * 
 */
public class History implements Serializable {

    private ArrayList<int[][]> dataList;
    private ArrayList<Integer> itsList;
    private ArrayList<double[]> coordsList;

    private int curPos;

    History() {
        this.dataList = new ArrayList<int[][]>();
        this.itsList = new ArrayList<Integer>();
        this.coordsList = new ArrayList<double[]>();
        this.curPos = 0;

    }

    /**
     * takes a variety of values and adds each one to the appropriate arraylist
     * in history.
     * 
     * @param mData
     *            a 2d array of values calculated by the mandelbrotCalculator
     *            class and used to draw the image
     * @param maxIts
     *            the maximum number of iterations
     * @param minReal
     *            the minimum real number used to calculate the mandelbrot data
     * @param maxReal
     *            the minimum real number used to calculate the mandelbrot data
     * @param minImag
     *            the minimum imaginary number used to calculate the mandelbrot
     *            data
     * 
     * @param maxImag
     *            the maximum imaginary number used to calculate the mandelbrot
     *            data
     */
    public void add(int[][] mData, int maxIts, double minReal, double maxReal, double minImag, double maxImag) {
        System.out.println("Saving to history..");
        this.dataList.add(mData);
        this.itsList.add(maxIts);
        double[] coords = {minReal, maxReal, minImag, maxImag};
        this.coordsList.add(coords);
        System.out.println("History position: " + this.curPos);
        this.redo();
    }

    /**
     * Saves the very first entry of each arraylist in history (which are the
     * program's default starting vales), then clears all data from the history
     * and sets each variable to those starting values.
     */
    public void clear() {
        this.curPos = 0;
        int origMaxIt = this.itsList.get(0);
        this.itsList = new ArrayList<Integer>();
        this.itsList.add(origMaxIt);

        int[][] origData = this.dataList.get(0);
        this.dataList = new ArrayList<int[][]>();
        this.dataList.add(origData);

        double[] origCoords = this.coordsList.get(0);
        this.coordsList = new ArrayList<double[]>();
        this.coordsList.add(origCoords);

        System.out.println("History position: " + this.curPos);

    }

    /**
     * decrements the current history position.
     */
    public void undo() {
        if (this.curPos > 0) {
            this.curPos--;
            System.out.println("Decremented pos. History position: " + this.curPos);
        }

    }

    /**
     * Returns the value at the given index of coordsList. CoordsList an
     * arraylist containing the minimum and maximum real and imaginary values at
     * the current history position. The values are ordered thus within the
     * array: minReal, maxReal, minImag, maxImag.
     * 
     * @param i
     *            an int from 0 - 3 inclusive, referring to: minReal, maxReal,
     *            minImag, maxImag
     * @return an arraylist containing the minimum and maximum real and
     *         imaginary values at the current history position
     */
    public double getCoords(int i) {

        return this.coordsList.get(this.curPos)[i];

    }

    /**
     * returns the number of maximum iterations at the current history position.
     * 
     * @return the maximum number of iterations at the current history position
     */
    public int getIts() {

        return this.itsList.get(this.curPos);
    }

    /**
     * returns the mandelbrot data at the current history position.
     * 
     * @return a 2d array containing the mandelbrot image data at the current
     *         history position
     */
    public int[][] getMData() {

        return this.dataList.get(this.curPos);
    }

    /**
     * increments the current history position.
     */
    public void redo() {
        if (this.curPos < this.dataList.size() - 1) {
            this.curPos++;
            System.out.println("Incremented pos. History position: " + this.curPos);
        }

    }
}
