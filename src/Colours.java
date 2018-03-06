import java.awt.Color;
import java.util.ArrayList;

/**
 * Generates and stores arrays of RGB values to use for the different colour
 * modes.
 *
 */
public class Colours {

    private static final int GRADIENT_5 = 5;
    private static final int GRADIENT_4 = 4;
    private static final int GRADIENT_3 = 3;
    private static final int GRADIENT_2 = 2;
    private static final int GRADIENT_1 = 1;
    private static final int COL_MAX = 255;

    /**
     * Returns the number of colours in a gradient array. This is 255 for sake
     * of ease.
     * 
     * @return the maximum size of any gradient array.
     */
    public static int getColMax() {
        return COL_MAX;
    }

    // gradient1
    private static final int R = 200;
    private static final int G = 200;

    private static final int B = 250;

    // end colours
    private static final int RE = 100;

    private static final int GE = 0;
    private static final int BE = 50;
    // gradient2
    private static final int R2 = 10;
    private static final int G2 = 20;
    private static final int B2 = 50;

    // end colours
    private static final int RE2 = 250;
    private static final int GE2 = 230;
    private static final int BE2 = 150;

    private int black = new Color(0, 0, 0).getRGB();
    private int white = new Color(COL_MAX, COL_MAX, COL_MAX).getRGB();
    private final int numOfColours = 5;

    private ArrayList<Integer> gradient1 = new ArrayList<Integer>();
    private ArrayList<Integer> gradient2 = new ArrayList<Integer>();
    private ArrayList<Integer> gradient3 = new ArrayList<Integer>();
    private ArrayList<Integer> gradient4 = new ArrayList<Integer>();
    private ArrayList<Integer> gradient5 = new ArrayList<Integer>();

    // gradient1
    // at each step change the value by:
    private double rs = ((double) RE - R) / COL_MAX;

    private double gs = ((double) GE - G) / COL_MAX;
    private double bs = ((double) BE - B) / COL_MAX;

    // gradient2
    // at each step
    private double rs2 = ((double) RE2 - R2) / COL_MAX;

    private double gs2 = ((double) GE2 - G2) / COL_MAX;
    private double bs2 = ((double) BE2 - B2) / COL_MAX;

    Colours() {
        for (int k = 0; k < COL_MAX; k++) {

            this.gradient1
                    .add(new Color(R + (int) (this.rs * k), G + (int) (this.gs * k), B + (int) (this.bs * k)).getRGB());
            this.gradient2
                    .add(new Color(R2 + (int) (this.rs2 * k), G2 + (int) (this.gs2 * k), B2 + (int) (this.bs2 * k))
                            .getRGB());
            this.gradient3.add(new Color(COL_MAX - k, COL_MAX - k, k).getRGB());
            this.gradient4.add(new Color(0, k, k).getRGB());
            this.gradient5.add(new Color(k, 0, 0).getRGB());
        }

    }

    /**
     * Given a gradient number g and an integer i, returns the colour integer
     * from the appropriate gradient array list at index i.
     * 
     * @param g
     *            the gradient number, which is used to select the appropriate
     *            arraylist.
     * @param i
     *            the index of the colour to return from g
     * @return a colour integer
     */
    public int get(int g, int i) {

        switch (g) {

            case GRADIENT_1:
                return this.gradient1.get(i);
            case GRADIENT_2:
                return this.gradient2.get(i);
            case GRADIENT_3:
                return this.gradient3.get(i);
            case GRADIENT_4:
                return this.gradient4.get(i);
            case GRADIENT_5:
                return this.gradient5.get(i);

            default:
                return -1;
        }

    }

    /**
     * Used for the basic black and white colour view, simply returns the black
     * and white colour values.
     * 
     * @param s
     *            takes a string, either 'black' or 'white'.
     * @return returns the colour integer for black or white, or -1 if the
     *         string input is neither.
     */
    public int get(String s) {
        if (s == "black") {
            return this.black;
        } else if (s == "white") {
            return this.white;
        }
        return -1;
    }

    /**
     * Returns the number of colour schemes implemented in the Colours class.
     * 
     * @return number of colour schemes
     */
    public int getNumOfColours() {
        return this.numOfColours;
    }

}
