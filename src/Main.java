/**
 * Creates a new model and a new delegate, passing the model to the delegate to
 * observe.
 *
 */
public class Main {

    /**
     * Starts the program.
     * 
     * @param args
     *            not needed
     */
    public static void main(String[] args) {
        Model model = new Model();
        new Delegate(model);
    }
}
