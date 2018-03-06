
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

/**
 * Delegate observes Model. It is responsible for setting up the GUI, listening
 * for user input and passing it to the model. When the delegate observes that
 * the model has been updated it redraws the image accordingly. The main
 * Delegate class is assisted by three helper classes: Colours, which generates
 * and stores arrays of RGB values to use for the different colour modes;
 * MDisplay, which extends JPanel to draw and redraw the mandelbrot data (and/or
 * the zoom marquee) to a buffered image according the current colour mode; and
 * ZoomHandler, which listens for the user’s mouse input, parses the
 * co-ordinates and passes them to the model.
 * 
 */
public class Delegate implements Observer {

    static final int TEXT_HEIGHT = 10;
    private static final int TEXT_WIDTH = 10;
    static final int FRAME_SIZE = 900;
    // compensates for the height of menu and toolbar so the bottom part of the
    // image isn't hidden
    private static final int FRAME_VERTICAL_BUFFER = 90;

    private final MDisplay mDisplay;
    private ZoomHandler zoomHandler;
    private final JFrame mainFrame;
    // so we can send info to model from other delegate classes
    private Model model;
    private JToolBar toolbar;
    private JTextField inputField;
    private JMenuBar menu;

    /**
     * Constructs a new Delegate, passing the model that it will observe.
     * 
     * @param model
     *            the model that the delegate will observe
     */
    public Delegate(Model model) {

        this.model = model;
        this.mainFrame = new JFrame();
        this.menu = new JMenuBar();
        this.toolbar = new JToolBar();
        this.inputField = new JTextField(TEXT_WIDTH);
        this.mDisplay = new MDisplay();
        this.zoomHandler = new ZoomHandler(this.mDisplay, this.model);
        this.mDisplay.setZoomHandler(this.zoomHandler);
        this.setupComponents();
        model.addObserver(this);
        model.setFrameSize(Delegate.FRAME_SIZE);
        model.set();

    }

    /**
     * runAll is called whenever the display needs to be updated, e.g. when the
     * model notifies the delegate that its values have changed, or when a file
     * is loaded.
     */
    public void runAll() {
        this.model.calculate();
        // need max its to implement colour views
        this.mDisplay.setMaxIts(this.model.getMaxIts());
        // data to display
        this.mDisplay.setMData(this.model.getMData());
        System.out.println("Repainting..");
        this.mDisplay.repaint();
        this.model.addToHistory();
    }

    private void setupComponents() {
        this.setupMenu();
        this.setupToolbar();
        this.mainFrame.add(this.mDisplay, BorderLayout.CENTER);
        this.mainFrame.setSize(FRAME_SIZE, FRAME_SIZE + FRAME_VERTICAL_BUFFER);
        this.mainFrame.setVisible(true);
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupMenu() {
        JMenu file = new JMenu("File");
        JMenuItem load = new JMenuItem("Load");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveAsPNG = new JMenuItem("Save as PNG");

        file.add(load);
        file.add(save);
        file.add(saveAsPNG);
        this.menu.add(file);

        load.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    JFileChooser fileChooser = new JFileChooser();
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {

                        File fileName = fileChooser.getSelectedFile();

                        model = model.loadFromFile(fileName);
                        zoomHandler = new ZoomHandler(mDisplay, model);
                        mDisplay.setZoomHandler(zoomHandler);
                        runAll();
                    }

                } catch (Exception exception) {
                    System.out.println(exception);
                    JOptionPane.showMessageDialog(Delegate.this.mainFrame, "Sorry, could not load file.");

                }

            }
        });

        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setApproveButtonText("Save");
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {

                        String fileName = fileChooser.getSelectedFile() + ".mandelbrot";

                        model.saveToFile(fileName);

                        System.out.println("Saved.");
                        JOptionPane.showMessageDialog(Delegate.this.mainFrame, "Saved.");
                    }

                } catch (Exception exception) {
                    System.out.println(exception);
                    JOptionPane.showMessageDialog(Delegate.this.mainFrame, "Sorry, save failed.");
                }

            }
        });

        saveAsPNG.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    BufferedImage mandelbrot = Delegate.this.mDisplay.getImg();

                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setApproveButtonText("Save");
                    int returnValue = fileChooser.showOpenDialog(null);
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        File file = new File(fileChooser.getSelectedFile() + ".png");
                        ImageIO.write(mandelbrot, "png", file);
                        System.out.println("Saved.");
                        JOptionPane.showMessageDialog(Delegate.this.mainFrame, "Saved.");
                    }

                } catch (IOException exception) {
                    System.out.println(exception);
                    JOptionPane.showMessageDialog(Delegate.this.mainFrame, "Sorry, save failed.");
                }

            }
        });

        // add menubar to frame
        this.mainFrame.setJMenuBar(this.menu);
    }

    private void setupToolbar() {

        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Delegate.this.model.undo();
                Delegate.this.updateDisplay();
            }
        });

        JButton redoButton = new JButton("Redo");
        redoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Delegate.this.model.redo();
                Delegate.this.updateDisplay();
            }
        });

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Delegate.this.model.reset();
                Delegate.this.updateDisplay();
            }
        });

        JButton colourButton = new JButton("Toggle Colour View");
        colourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Delegate.this.mDisplay.setColourMode();
                Delegate.this.mDisplay.repaint();
            }
        });

        JLabel label = new JLabel("Max Iterations: ");

        this.inputField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    Delegate.this.model.setMaxIts(Delegate.this.inputField.getText());
                    Delegate.this.inputField.setText("");
                    Delegate.this.runAll();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        });

        JButton add_button = new JButton("Go"); // to translate event for
        // this
        // button into appropriate model
        // method call
        add_button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // same as when user presses carriage return key, tell model to
                // add text entered by user
                Delegate.this.model.setMaxIts(Delegate.this.inputField.getText());
                // and clear the input box in the GUI view
                Delegate.this.inputField.setText("");
                Delegate.this.runAll();
            }
        });

        // add buttons, label, and textfield to the toolbar
        this.toolbar.add(undoButton);
        this.toolbar.add(redoButton);
        this.toolbar.add(resetButton);
        this.toolbar.add(colourButton);
        this.toolbar.add(label);
        this.toolbar.add(this.inputField);
        this.toolbar.add(add_button);
        // add toolbar to north of main frame
        this.mainFrame.add(this.toolbar, BorderLayout.NORTH);
    }

    @Override
    public void update(Observable o, Object arg) {

        // Tell the SwingUtilities thread to update the GUI components.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Delegate.this.runAll();
            }
        });
    }

    /**
     * When undo or redo are called, we want to redraw the display without
     * recalculating the mandelbrot data. (the data to use will be set in the
     * model by the undo() or redo() method which gets it from the model's
     * History object)
     */
    public void updateDisplay() {
        this.mDisplay.setMaxIts(this.model.getMaxIts());
        // data to display
        this.mDisplay.setMData(this.model.getMData());
        System.out.println("Repainting..");
        this.mDisplay.repaint();
    }

}
