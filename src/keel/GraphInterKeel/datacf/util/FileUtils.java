package keel.GraphInterKeel.datacf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * <p>
 * @author Written by Pedro Antonio Gutiérrez and Juan Carlos Fernández (University of Córdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class FileUtils {

    /**
     * <p>
     * Utilities for Files. Copy, delete,...
     * </p>
     */

    /**
     * <p>
     * Utilities for Files. Deletes recursively all the files and directories
     * contained in a given path
     * </p>
     * @param dirPath Path to delete all the files contained in it
     */
    public static void recursiveDelete(File dirPath) {
        String[] ls = dirPath.list();

        for (int idx = 0; idx < ls.length; idx++) {
            File file = new File(dirPath, ls[idx]);
            if (file.isDirectory()) {
                recursiveDelete(file);
            }
            file.delete();
        }
    }

    /**
     * <p>
     * Utilities for Files. Deletes recursively all the files and directories
     * contained in a given path and also the path
     * </p>
     * @param dirPath Path to delete all the files contained in it
     */
    public static void deletePath(File dirPath) {
        recursiveDelete(dirPath);
        dirPath.delete();
    }

    /**
     * <p>
     * Copy files
     * </p>
     * @param fromFileName From file name
     * @param toFileName Target file name
     * @throws java.io.IOException
     */
    @SuppressWarnings("empty-statement")
    public static void copy(String fromFileName, String toFileName)
            throws IOException {
        File fromFile = new File(fromFileName);
        File toFile = new File(toFileName);

        if (!fromFile.exists()) {
            throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
        }
        if (!fromFile.isFile()) {
            throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
        }
        if (!fromFile.canRead()) {
            throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);
        }
        if (toFile.isDirectory()) {
            toFile = new File(toFile, fromFile.getName());
        }
        if (toFile.exists()) {
            if (!toFile.canWrite()) {
                throw new IOException("FileCopy: " + "destination file is unwriteable: " + toFileName);
            }
        } else {
            String parent = toFile.getParent();
            if (parent == null) {
                parent = System.getProperty("user.dir");
            }
            File dir = new File(parent);
            if (!dir.exists()) {
                throw new IOException("FileCopy: " + "destination directory doesn't exist: " + parent);
            }
            if (dir.isFile()) {
                throw new IOException("FileCopy: " + "destination is not a directory: " + parent);
            }
            if (!dir.canWrite()) {
                throw new IOException("FileCopy: " + "destination directory is unwriteable: " + parent);
            }
        }

        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead); // write
            }
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                    ;
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
    }
}
