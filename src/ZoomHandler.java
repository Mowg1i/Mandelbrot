
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Listens for the user’s mouse input, parses the co-ordinates and passes them
 * to the model.
 *
 */
public class ZoomHandler extends MouseAdapter {
    private int x1, x2, y1, y2;
    private MDisplay panel;
    private Model model;

    ZoomHandler(MDisplay panel, Model model) {
        this.setPanel(panel);
        this.setModel(model);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouse) {
                panel.setZooming(true);
                ZoomHandler.this.x1 = mouse.getX();
                ZoomHandler.this.y1 = mouse.getY();
                System.out.println("Mouse pressed at: " + ZoomHandler.this.x1 + "," + ZoomHandler.this.y1);
            }

            @Override
            public void mouseReleased(MouseEvent mouse) {
                panel.setZooming(false);
                ZoomHandler.this.x2 = mouse.getX();
                ZoomHandler.this.y2 = mouse.getY();
                ZoomHandler.this.makeSquare();
                model.calcMinMax(ZoomHandler.this.x1, ZoomHandler.this.y1, ZoomHandler.this.x2, ZoomHandler.this.y2);
            }
        });
        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouse) {
                ZoomHandler.this.x2 = mouse.getX();
                ZoomHandler.this.y2 = mouse.getY();
                ZoomHandler.this.makeSquare();
                panel.drawZoom(ZoomHandler.this.x1, ZoomHandler.this.y1, ZoomHandler.this.x2, ZoomHandler.this.y2);
            }
        });
    }

    /**
     * adjusts the zoomHandler's coordinates to force the zoom marquee to be
     * square when it is drawn, and to ensure that the model will be able to
     * calculate the mandelbrot data without distortion.
     */
    public void makeSquare() {

        // adjust to accommodate if zoom end is less than zoom start

        if (this.x2 < this.x1) {
            int n = this.x1;
            this.x1 = this.x2;
            this.x2 = n;
        }
        if (this.y2 < this.y1) {
            int n = this.y1;
            this.y1 = this.y2;
            this.y2 = n;
        }

        // adjust to make zoom area square

        int h = this.y2 - this.y1;
        int w = this.x2 - this.x1;

        if (w > h) {
            h = w;
        } else {
            w = h;

        }

        this.x2 = this.x1 + w;
        this.y2 = this.y1 + h;

    }

    /**
     * Sets the model that the ZoomHandler should send its parsed coordinates
     * to.
     * 
     * @param model the model that the ZoomHandler should send its parsed coordinates to.
     */
    public void setModel(Model model) {
        this.model = model;
    }

    /**
     * Sets the panel that the ZoomHandler will hjandle zooms for.
     * 
     * @param panel a MDisplay panel instance
     */
    public void setPanel(MDisplay panel) {
        this.panel = panel;
    }

}
