
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

//part of delegate
/**
 * Extends JPanel to draw and redraw the mandelbrot data (and/or the zoom
 * marquee) to a buffered image according the current colour mode.
 *
 */
public class MDisplay extends JPanel {

    private static final int COLOUR_MODE_5 = 5;

    private static final int COLOUR_MODE_4 = 4;

    private static final int COLOUR_MODE_3 = 3;

    private static final int COLOUR_MODE_2 = 2;

    private static final int COLOUR_MODE_1 = 1;

    // given mandelbrot array paints data

    // to keep track of zoom co-ordinates and whether to redraw yet
    // newx and newy are the width and height of the rectangle, start x and
    // starty are the start position of the rectangle
    private int x2, y2, x1, y1;

    private boolean zooming = false;
    private BufferedImage img;
    private int[][] mData = {{0},{0}};

    private int maxIts;

    private Colours colours;

    private ZoomHandler zoomHandler;

    private int colourMode = 0;

    MDisplay() {
        this.colours = new Colours();
    }

    /**
     * Updates MDisplay's record of the zoom coordinates and repaints the image
     * including the zoom marquee.
     * 
     * @param x1
     *            the parsed upper left x coordinate of the zoom area
     * @param y1
     *            the parsed upper left y coordinate of the zoom area
     * @param x2
     *            the parsed lower right x coordinate of the zoom area
     * @param y2
     *            the parsed lower right y coordinate of the zoom area
     */
    public void drawZoom(int x1, int y1, int x2, int y2) {

        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.repaint();
    }

    /**
     * Returns the buffered image created by MDisplay's paint method.
     * 
     * @return a buffered image created by the display
     */
    public BufferedImage getImg() {
        return this.img;
    }

    @Override
    public void paint(Graphics graphics) {

        super.paint(graphics);
        // System.out.println(this.maxIts);
        // System.out.println(this.MData.length);
        // System.out.println(this.MData[0].length);

        this.img = new BufferedImage(Delegate.FRAME_SIZE, Delegate.FRAME_SIZE, BufferedImage.TYPE_INT_ARGB);

        System.out.println("Painting display");

        // TODO for each element in mData,
        // set relevant pixel in img appropriately
        for (int i = 0; i < this.mData.length; i++) {
            for (int j = 0; j < this.mData[0].length; j++) {

                int n = this.mData[i][j];
                if (this.colourMode == 0) {

                    if (n == this.maxIts) {
                        this.img.setRGB(j, i, this.colours.get("black"));
                    } else {
                        this.img.setRGB(j, i, this.colours.get("white"));
                    }

                } else {

                    // calculate where to index into the colour array by mapping
                    // the mandelbrot data value to the range 0-254.
                    // (colour arrays are 255 in length.)
                    // casting to doubles for accuracy
                    int nc = (int) (((double) n / (double) this.maxIts) * (double) Colours.getColMax() - 1);

                    if (this.colourMode == COLOUR_MODE_1) {
                        this.img.setRGB(j, i, this.colours.get(COLOUR_MODE_1, nc));
                    } else if (this.colourMode == COLOUR_MODE_2) {
                        this.img.setRGB(j, i, this.colours.get(COLOUR_MODE_2, nc));
                    } else if (this.colourMode == COLOUR_MODE_3) {
                        this.img.setRGB(j, i, this.colours.get(COLOUR_MODE_3, nc));
                    } else if (this.colourMode == COLOUR_MODE_4) {
                        this.img.setRGB(j, i, this.colours.get(COLOUR_MODE_4, nc));
                    } else if (this.colourMode == COLOUR_MODE_5) {
                        this.img.setRGB(j, i, this.colours.get(COLOUR_MODE_5, nc));
                    }
                    if (n == this.maxIts) {
                        this.img.setRGB(j, i, this.colours.get("black"));
                    }
                }

            }

        }
        graphics.drawImage(this.img, 0, 0, this);

        if (this.zooming) {
            graphics.drawRect(this.x1, this.y1, this.x2 - this.x1, this.y2 - this.y1);
        }
    }

    /**
     * Called by the delegate when the user toggles the colour mode.
     */
    public void setColourMode() {
        if (this.colourMode < this.colours.getNumOfColours()) {
            this.colourMode++;
        } else {
            this.colourMode = 0;
        }

        System.out.println("Colour mode: " + this.colourMode);

    }

    /**
     * Used by the delegate to set the ZoomHandler of the display.
     * 
     * @param zh
     *            a ZoomHandler instance.
     */
    public void setZoomHandler(ZoomHandler zh) {
        this.zoomHandler = zh;
    }

    /**
     * Sets the maximum number of iterations, which is used to determine the
     * colour of each pixel when the image is painted, depending on the colour
     * scheme.
     * 
     * @param m
     *            the number of iterations
     */
    public void setMaxIts(int m) {
        this.maxIts = m;

    }

    /**
     * Sets the mandelbrot data to be painted.
     * 
     * @param m
     *            2d array of values calculated by the MandelbrotCalculator
     *            class.
     */
    public void setMData(int[][] m) {
        this.mData = m;

    }

    /**
     * Updates MDisplay's zooming variable, which is used to determine whether
     * the zoom marquee should be drawn when the display is repainted.
     * 
     * @param b
     *            boolean true or false
     */
    public void setZooming(boolean b) {
        this.zooming = b;
    }

}
