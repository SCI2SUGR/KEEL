/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package keel.GraphInterKeel.util;

import java.io.File;

/**
 *
 * @author Administrador
 */
public class Path {

    protected static String path = ".";

    public static String getPath() {
        return path;
    }

    public static void setPath(String path) {
        Path.path = path;
    }

    public static File getFilePath() {
        return new File(path);
    }

    public static void setFilePath(File filePath) {
        Path.path = filePath.getPath();
    }
}
