package keel.GraphInterKeel.experiments;

import java.io.File;
import java.util.Vector;
import javax.swing.filechooser.*;

/**
 *
 * @author robles
 */
public final class KeelFileFilter extends FileFilter {

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
