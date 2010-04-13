package keel.GraphInterKeel.datacf.util;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

/**
 * <p>
 * @author Written by Ignacio Robles
 * @author Modified by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public final class KeelFileFilter extends FileFilter {

    /**
     * <p>
     * Filter for files in a FileBrowser
     * </p>
     */

    /** Extensions of the file filter */
    private Vector<String> extensions = new Vector<String>();

    /** Name of the filter */
    private String filterName = null;

    /**
     * <p>
     * Sets Name of the Filer
     * </p>
     * @param fn Name of filter
     */
    public void setFilterName(String fn) {
        filterName = new String(fn);
    }

    /**
     * <p>
     * Adds extendion to the filter
     * </p>
     * @param ex Extension for the filter
     */
    public void addExtension(String ex) {
        extensions.add(new String(ex));
    }

    /**
     * Overriding the accept method for accepting
     * directory names
     * @param f File to evaluate
     * @return boolean Is the file accepted?
     */
    @Override
    public boolean accept(File f) {
        String filename = f.getName();

        if (f.isDirectory()) {
            return true;
        }
        for (int i = 0; i < extensions.size(); i++) {
            if (filename.endsWith(extensions.elementAt(i))) {
                return true;
            }
        }
        return false;
    }



    /**
     * Returns the description of the file filter
     * @return String Description of the file filter
     */
    @Override
    public String getDescription() {
        return filterName;
    }
}
