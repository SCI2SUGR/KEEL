package keel.GraphInterKeel.experiments;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * <p>Title: Keel</p>
 * <p>Description: File filter </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Universidad de Granada</p>
 * @author V�ctor Manuel Gonz�lez Quevedo
 * @version 0.1
 */
public class ArchiveFilter2 extends FileFilter {

    String[] files; // filtered files extensions
    String description; // filter description

    /**
     * Builder
     * @param _files Files accepted
     * @param descr Description of the set
     */
    public ArchiveFilter2(String[] _files, String descr) {
        this.files = _files;
        this.description = descr;
    }

    /**
     * Tests if a file is accepted
     * @param f File to be tested
     * @return True if the file is accepted, false if not
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String nombre = f.getName();
        int i = nombre.lastIndexOf('.');
        if (i > 0 && i < nombre.length() - 1) {
            String extension = nombre.substring(i + 1).toLowerCase();

            for (i = 0; i < files.length; i++) {
                // check that extension is valid
                if (files[i].equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get the description of the archive
     * @return The description of the archive
     */
    public String getDescription() {
        return description;
    }
}
