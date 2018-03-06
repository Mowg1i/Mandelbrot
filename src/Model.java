
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Observable;

/**
 * Model is observed by Delegate. It is responsible for keeping track of all the
 * variables needed to calculate the mandelbrot image, and for doing this
 * calculation when prompted. When its variables have been updated according the
 * the calculation, or in response to undo/redo etc, it alerts the delegate
 * which then updates the view with the new information. It implements
 * Serializable so that its current state can be saved to file.
 *
 */
public class Model extends Observable implements Serializable {

    private static final int MAX_REAL_INDEX = 1;
    private static final int MIN_REAL_INDEX = 0;
    private static final int MIN_IMAG_INDEX = 2;
    private static final int MAX_IMAG_INDEX = 3;
    private static final long serialVersionUID = 1L;
    private int[][] mData;
    private MandelbrotCalculator mCalc;
    private int maxIts;
    private double minReal;
    private double maxReal;
    private double minImag;
    private double maxImag;
    private int frameSize;
    private History history;

    /**
     * Creates a new model instance and sets its values to default.
     */
    public Model() {
        this.set();
    }

    /**
     * Updates the model's history object with the current set of values so that
     * it may be accessed in future.
     */
    void addToHistory() {
        this.history.add(this.mData, this.maxIts, this.minReal, this.maxReal, this.minImag, this.maxImag);
    }

    /**
     * Given two sets of pixel co-ordinates provided by the user's zoom marquee,
     * calculates the minimum and maximum real and imaginary values to use when
     * drawing the next image.
     * 
     * @param x1
     *            the upper left x coordinate of the zoom area
     * @param y1
     *            the upper left y coordinate of the zoom area
     * @param x2
     *            the lower right x coordinate of the zoom area
     * @param y2
     *            the lower right y coordinate of the zoom area
     */
    public void calcMinMax(double x1, double y1, double x2, double y2) {

        // width newX, height newY, start coords newStartX new rStartY;
        // send zoom info to model

        System.out.println("min coords: " + x1 + "," + y1);
        System.out.println("max coords: " + x2 + "," + y2);

        double realRange = this.maxReal - this.minReal;
        System.out.println("Real range:" + realRange);

        double imagRange = this.maxImag - this.minImag;
        System.out.println("Imag range:" + imagRange);

        double minR = this.minReal;
        double minI = this.minImag;

        this.minReal = (x1 / this.frameSize) * realRange + minR;
        System.out.println("minReal:" + this.minReal);

        this.maxReal = (x2 / this.frameSize) * realRange + minR;
        System.out.println("maxReal:" + this.maxReal);

        this.minImag = (y1 / this.frameSize) * imagRange + minI;
        System.out.println("minImag:" + this.minImag);
        this.maxImag = (y2 / this.frameSize) * imagRange + minI;
        System.out.println("maxImag:" + this.maxImag);
        this.shout();
    }

    /**
     * Calculates an array of mandelbrot data given a range of values and stores
     * it in the model.
     */
    void calculate() {
        System.out.println("Calculating mData");
        this.mData = this.mCalc.calcMandelbrotSet(this.frameSize, this.frameSize, this.minReal, this.maxReal,
                this.minImag, this.maxImag, this.maxIts, MandelbrotCalculator.DEFAULT_RADIUS_SQUARED);

    }

    /**
     * Used to update the model's stored values from history when undo or redo
     * is called.
     */
    void getDataFromHistory() {
        System.out.println("Getting data from history..");
        this.minReal = this.history.getCoords(MIN_REAL_INDEX);
        this.maxReal = this.history.getCoords(MAX_REAL_INDEX);
        this.minImag = this.history.getCoords(MIN_IMAG_INDEX);
        this.maxImag = this.history.getCoords(MAX_IMAG_INDEX);
        this.maxIts = this.history.getIts();
        this.mData = this.history.getMData();
    }

    /**
     * Returns the current stored value for the framesize to use when
     * calculating the mandelbrot data.
     * 
     * @return an integer, the width and height of the mandelbrot data.
     */
    public int getFrameSize() {
        System.out.println("getting frame size: " + this.frameSize);
        return this.frameSize;
    }

    /**
     * Returns the history object of the model.
     * 
     * @return the model's history object.
     */
    public History getHistory() {
        return this.history;
    }

    /**
     * Returns the model's stored value of the maximum number of iterations.
     * 
     * @return an integer greater than zero representing the current maximum
     *         iterations.
     */
    public int getMaxIts() {
        return this.maxIts;
    }

    /**
     * Returns the model's stored mandelbrot data array.
     * @return a 2d int array of mandelbrot data.
     */
    public int[][] getMData() {
        System.out.println("getting mData");
        return this.mData;
    }

    /**
     * Loads a model in from file.
     * 
     * @param fileName
     *            the filename given to load0
     * @return the loaded model
     * @throws Exception
     *             exception gets caught by the delegate if something goes wrong
     *             when reading the file.
     */
    public Model loadFromFile(File fileName) throws Exception {

        FileInputStream fileInput = new FileInputStream(fileName);
        ObjectInputStream objectInput = new ObjectInputStream(fileInput);

        System.out.println("Loading:");
        Model m = (Model) objectInput.readObject();
        objectInput.close();

        return m;

    }

    /**
     * Rolls the history forward and updates the model's values if possible.
     */
    void redo() {
        try {
            this.history.redo();
            this.getDataFromHistory();
        } catch (Exception e) {
            System.out.println("Cannot redo.");
        }
    }

    /**
     * Clears the history and resets the model's values.
     */
    void reset() {
        try {
            System.out.println("Resetting..");
            this.history.clear();
            this.getDataFromHistory();
        } catch (Exception e) {
            System.out.println("Cannot reset.");
        }
    }

    /**
     * Saves the model to file.
     * 
     * @param fileName
     *            The desired filename of the file to output.
     * @throws Exception
     *             exception gets caught by the delegate if something goes wrong
     *             when writing the file.
     */
    public void saveToFile(String fileName) throws Exception {

        FileOutputStream fileOutput = new FileOutputStream(fileName);
        ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
        objectOutput.writeObject(this);
        objectOutput.close();

    }

    /**
     * Resets the model's values to the defaults defined in the
     * MandelbrotCalculator class.
     */
    public void set() {

        this.mCalc = new MandelbrotCalculator();
        this.minReal = MandelbrotCalculator.INITIAL_MIN_REAL;
        this.maxIts = MandelbrotCalculator.INITIAL_MAX_ITERATIONS;
        this.maxReal = MandelbrotCalculator.INITIAL_MAX_REAL;
        this.minImag = MandelbrotCalculator.INITIAL_MIN_IMAGINARY;
        this.maxImag = MandelbrotCalculator.INITIAL_MAX_IMAGINARY;
        this.frameSize = Delegate.FRAME_SIZE;
        this.history = new History();
        this.shout();

    }

    /**
     * Sets the framesize () the square dimensions of the mandelbrot data array)
     * to use when calculating the data.
     * 
     * @param frameSize
     *            an integer that will be used to define the length and width of
     *            the mandelbrot image.
     */
    public void setFrameSize(int frameSize) {
        this.frameSize = frameSize;
    }

    /**
     * sets the maximum number of iterations to use when calculating the
     * mandelbrot data.
     *
     * @param input
     *            an integer greater than 0.
     */
    public void setMaxIts(String input) {
        try {
            if (Integer.parseInt(input) > 0) {
                this.maxIts = Integer.parseInt(input);
            }
        } catch (Exception e) {
            System.out.println("Invalid max iteration input.");
        }
        System.out.println("Max iterations: " + this.maxIts);
    }

    /**
     * sets the Model's mdata - used for loading in .mandelbrot files and
     * resetting.
     *
     * @param mData
     *            a 2d array of mandelbrot image data
     */
    public void setMData(int[][] mData) {
        System.out.println("setting mData");
        this.mData = mData;
    }

    /**
     * Notifies the delegate that something has changed and so the display
     * should be updated.
     */
    public void shout() {
        this.setChanged();
        this.notifyObservers();
    }

    /**
     * Rolls back the history to the previous state and updates the model with
     * the values.
     */
    void undo() {
        try {
            this.history.undo();
            this.getDataFromHistory();
        } catch (Exception e) {
            System.out.println("Cannot undo.");
        }

    }

}
