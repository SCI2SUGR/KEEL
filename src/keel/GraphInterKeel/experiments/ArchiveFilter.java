package keel.GraphInterKeel.experiments;

import java.io.*;

/**
 * <p>Title: Keel</p>
 * <p>Description: File filter</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Universidad de Granada</p>
 * @author V�ctor Manuel Gonz�lez Quevedo
 * @version 0.1
 */
public class ArchiveFilter implements FilenameFilter {

    String[] files; // filtered file extensions

    /**
     * Builder
     * @param _files Files currently selected
     */
    public ArchiveFilter(String[] _files) {
        this.files = _files;
    }

    /**
     * Test if the file shoudl be accepted
     * @param dir Directory containing the file
     * @param name Name of the file
     * @return True if the file is accepted. False, if not
     */
    public boolean accept(File dir, String name) {
        // Accept only the correct files

        File f = new File(dir, name);
        if (f.isDirectory()) {
            return true;
        }

        int i = name.lastIndexOf('.');
        if (i > 0 && i < name.length() - 1) {
            String extension = name.substring(i + 1).toLowerCase();

            for (i = 0; i < files.length; i++) {
                // Check that extension is valid
                if (files[i].equals(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
}
