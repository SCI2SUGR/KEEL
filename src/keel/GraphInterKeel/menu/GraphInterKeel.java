package keel.GraphInterKeel.menu;

import javax.swing.UIManager;
import java.awt.*;
import java.util.Locale;

/**
 * <p>
 * @author  Administrador
 * @author Modified by  Pedro Antonio Gutiérrez and Juan Carlos Fernández(University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class GraphInterKeel {
  boolean packFrame = false;

  //Construct the application
  public GraphInterKeel() {
    Frame frame = new Frame();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
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
  //Main method
  public static void main(String[] args) {
    try {
      // Set the default locale to pre-defined locale
      Locale.setDefault(Locale.ENGLISH);
    
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new GraphInterKeel();
  }
}