/**
 *
 * File: Application1.java
 *
 * Application to process the execution of a experiment. Graph version
 *
 * @author Joaquin Derrac (University of Granada) 15/6/2009
 * @version 1.0
 * @since JDK1.5
 *
 */
package keel.RunKeelGraph;

import javax.swing.UIManager;
import java.awt.*;

public class Application1 {

    boolean packFrame = false;

    /**
     * Default builder
     */
    public Application1() {
        Frame1 frame = new Frame1();
        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        } else {
            frame.validate();
        }
        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        frame.setVisible(true);
    }

    /**
     * Main method
     * @param args Params of the method
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Application1();
    }
}