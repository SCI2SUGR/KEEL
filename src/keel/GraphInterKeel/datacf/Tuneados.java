/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.GraphInterKeel.datacf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author pagutierrez
 */
public class Tuneados {
    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        try {
                        // Set System L&F
                        UIManager.setLookAndFeel(
                            UIManager.getSystemLookAndFeelClassName());
                        } 
                        catch (UnsupportedLookAndFeelException e) {
                           // handle exception
                        }
                        catch (ClassNotFoundException e) {
                           // handle exception
                        }
                        catch (InstantiationException e) {
                           // handle exception
                        }
                        catch (IllegalAccessException e) {
                           // handle exception
                        }
       DataCFFrame frame = new DataCFFrame();
       frame.getSelectorToolbar().setVisible(false);
       frame.getSelectorTabbedPane().removeAll();
       frame.addImportTab(false,false);
       frame.setVisible(true);
    }
}
