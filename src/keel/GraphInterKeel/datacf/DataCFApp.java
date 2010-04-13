package keel.GraphInterKeel.datacf;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * <p>
 * @author Written by Juan Carlos Fernández and Pedro Antonio Gutiérrez (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class DataCFApp extends SingleFrameApplication {

    /**
     * <p>
     * The main class of the application.
     * </p>
     */
    /**
     * <p>
     * At startup create and show the main frame of the application.
     * </p>
     */
    @Override
    protected void startup() {
        //show(new DataCFView(this));
    }

    /**
     * <p>
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     * @param root Root window
     * </p>
     */
    @Override
    protected void configureWindow(java.awt.Window root) {
    }

    /**
     * <p>
     * A convenient static getter for the application instance.
     * @return the instance of DataCFApp
     * </p>
     */
    public static DataCFApp getApplication() {
        return Application.getInstance(DataCFApp.class);
    }

    /**
     * <p>
     * Main method launching the application.
     * @param args Arguments of the program
     * </p>
     */
    public static void main(String[] args) {
        launch(DataCFApp.class, args);

    }
}
