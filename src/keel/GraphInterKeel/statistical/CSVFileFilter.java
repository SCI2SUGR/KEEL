/**
 * File: CSVFileFilter.java
 *
 * This class filter CSV methods for file dialogs in the module
 *
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @version 1.1
 * @since JDK1.5
*/

package keel.GraphInterKeel.statistical;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

public final class CSVFileFilter extends FileFilter {

    private Vector<String> extensions = new Vector<String>();
    private String filterName = null;

    /**
     * Set the filter name
     * @param fn New name of the filter
     */
    public void setFilterName(String fn) {
        filterName = new String(fn);
    }

    /**
     * Adds an extension to the filter
     * @param ex Nex extension to add
     */
    public void addExtension(String ex) {
        extensions.add(new String(ex));
    }

    /**
     * Test if the file is accepted
     * @param f File to be tested
     * @return True if the file is accepted, false if not
     */
    public boolean accept(File f) {
        String filename = f.getName();

        for (int i = 0; i < extensions.size(); i++) {
            if (filename.endsWith(extensions.elementAt(i))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the filter name
     * @return The filter name
     */
    public String getDescription() {
        return filterName;
    }
}
