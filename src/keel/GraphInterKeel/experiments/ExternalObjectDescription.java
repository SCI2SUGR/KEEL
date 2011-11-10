/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 *
 * File: ExternalObjectDescription.java
 *
 * A class for managing user methods
 *
 * @author Created by Luciano S�nchez on 11/7/04.
 * @author Modified by Julian Luengo 19/04/2009
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.io.*;
import java.util.Vector;

public class ExternalObjectDescription implements Serializable, Comparable {

    public String[] name;
    public String[] path;
    public String[] nameJar;
    public Vector<Joint> arg = new Vector<Joint>();
    private int subtype;
    private int subtypelqd;

    /**
     * Default builder
     */
    public ExternalObjectDescription() {
    }

    /**
     * Copy builder
     * @param d Object to be copied
     */
    public ExternalObjectDescription(ExternalObjectDescription d) {

        if (d.name == null) {
            name = null;
        } else {
            name = new String[d.name.length];
            //System.out.println (" >>>Length of Nombre: "+d.nombre.length );
            for (int i = 0; i < d.name.length; i++) {
                if (d.name[i] == null) {
                    name[i] = null;
                } else {
                    name[i] = new String(d.name[i]);
                }
            //  System.out.println ("   >>>Nombre["+i+"] = "+name[i] );
            }
        }
        if (d.path == null) {
            path = null;
        } else {
            path = new String[d.path.length];
            for (int i = 0; i < d.path.length; i++) {
                if (d.path[i] == null) {
                    path[i] = null;
                } else {
                    path[i] = new String(d.path[i]);
                }
            // System.out.println ("   >>>Path["+i+"] = "+path[i] );
            }
        }
        subtype = d.subtype;
        subtypelqd = d.subtypelqd;

        if (d.nameJar == null) {
            nameJar = null;
        } else {
            nameJar = new String[d.nameJar.length];
            for (int i = 0; i < d.nameJar.length; i++) {
                if (d.nameJar[i] == null) {
                    nameJar[i] = null;
                } else {
                    nameJar[i] = new String(d.nameJar[i]);
                }
            // System.out.println ("   >>>nombreJar["+i+"] = "+nameJar[i] );
            }
        }
    }

    /**
     * Copy builder. 
     * @param d Object to be copied
     * @param layer True if the layer selected is the first
     *
     */
    public ExternalObjectDescription(ExternalObjectDescription d, boolean layer) {
        if (d.name == null) {
            name = null;
        } else {
            name = new String[1];
            if (d.name[0] == null) {
                name[0] = null;
            } else {
                name[0] = new String(d.name[0]);
            }
        }
        if (d.path == null) {
            path = null;
        } else {
            path = new String[1];
            if (d.path[0] == null) {
                path[0] = null;
            } else {
                path[0] = new String(d.path[0]);
            }
        }
        subtype = d.subtype;
        subtypelqd = d.subtypelqd;
        if (d.nameJar == null) {
            nameJar = null;
        } else {
            nameJar = new String[1];
            if (d.nameJar[0] == null) {
                nameJar[0] = null;
            } else {
                nameJar[0] = new String(d.nameJar[0]);
            }
        }
    }

    /**
     * Builder
     * @param n Name
     * @param p Path
     * @param s Subtype
     *
     */
    public ExternalObjectDescription(String n, String p, int s) {

        name = null;
        path = null;
        nameJar = null;
        if (n != null) {
            name = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                name[i] = new String(n);
            }
        }
        if (p != null) {
            path = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                path[i] = new String(p);
            }
        }
        if (n != null) {
            nameJar = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                nameJar[i] = new String(n + ".jar");
            }
        }
        subtype = s;
    //subtypelqd = lqd;
    }

    /**
     * Builder
     * @param n Name
     * @param p Path
     * @param s Subtype
     * * @param s Subtypelqd
     * @param j Jar
     */
    public ExternalObjectDescription(String n, String p, int s, String j) {
        //  System.out.println("Creating dsc " + n + " " + p + " " + s + " " + j);
        name = null;
        path = null;
        nameJar = null;
        if (n != null) {
            name = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                name[i] = new String(n);
            }
        }
        if (p != null) {
            path = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                path[i] = new String(p);
            }
        }
        if (j != null) {
            nameJar = new String[Layer.numLayers];
            for (int i = 0; i < Layer.numLayers; i++) {
                nameJar[i] = new String(j);
            }
        }
        subtype = s;
    //subtypelqd = lqd;
    }

    /**
     * Resize the object to the new capacity indicated
     * @param dim the new size
     */
    public void redim(int dim) {

        if (name != null) {
            String[] tmpnombre = new String[dim];
            for (int i = 0; i < tmpnombre.length; i++) {
                if (name[0] == null) {
                    tmpnombre[i] = null;
                } else {
                    tmpnombre[i] = new String(name[0]);
                }
            }
            name = tmpnombre;
        }
        if (path != null) {
            String[] tmppath = new String[dim];
            for (int i = 0; i < tmppath.length; i++) {
                if (path[0] == null) {
                    tmppath[i] = null;
                } else {
                    tmppath[i] = new String(path[0]);
                }
            }
            path = tmppath;
        }
        if (nameJar != null) {
            String[] tmpnombrejar = new String[dim];
            for (int i = 0; i < tmpnombrejar.length; i++) {
                if (nameJar[0] == null) {
                    tmpnombrejar[i] = null;
                } else {
                    tmpnombrejar[i] = new String(nameJar[0]);
                }
            }
            nameJar = tmpnombrejar;
        }
    }

    /**
     * Get args
     * @return Arguments of the node
     */
    public Vector getArg() {
        return arg;
    }

    /**
     * Set args
     * @param join Joining
     */
    public void setArg(Vector<Joint> join) {
        arg = join;
    }

    /**
     * Insert new dsc
     * @param d New dsc
     */
    void insert(ExternalObjectDescription d) {

        // insert fields of a new layer in the object

        if (subtype != d.subtype) {
            System.out.println("WARNING: Trying to merge different kinds of dsc");
            return;
        }

        if (d.name != null) {
            int len = 0;
            if (name != null) {
                len = name.length;
            }
            String[] tmpnombre = new String[len + d.name.length];
            for (int i = 0; i < name.length; i++) {
                if (name[i] == null) {
                    tmpnombre[i] = null;
                } else {
                    tmpnombre[i] = new String(name[i]);
                }
            }
            for (int i = name.length; i < tmpnombre.length; i++) {
                if (d.name[i - name.length] == null) {
                    tmpnombre[i] = null;
                } else {
                    tmpnombre[i] = new String(d.name[i - name.length]);
                }
            }
            name = tmpnombre;
        }

        if (d.path != null) {
            int len = 0;
            if (path != null) {
                len = path.length;
            }
            String[] tmppath = new String[len + d.path.length];
            for (int i = 0; i < path.length; i++) {
                if (path[i] == null) {
                    tmppath[i] = null;
                } else {
                    tmppath[i] = new String(path[i]);
                }
            }
            for (int i = path.length; i < tmppath.length; i++) {
                if (d.path[i - path.length] == null) {
                    tmppath[i] = null;
                } else {
                    tmppath[i] = new String(d.path[i - path.length]);
                }
            }
            path = tmppath;
        }

        if (d.nameJar != null) {
            int len = 0;
            if (nameJar != null) {
                len = nameJar.length;
            }
            String[] tmpnombreJar = new String[len + d.nameJar.length];
            for (int i = 0; i < nameJar.length; i++) {
                if (nameJar[i] == null) {
                    tmpnombreJar[i] = null;
                } else {
                    tmpnombreJar[i] = new String(nameJar[i]);
                }
            }
            for (int i = nameJar.length; i < tmpnombreJar.length; i++) {
                if (d.nameJar[i - nameJar.length] == null) {
                    tmpnombreJar[i] = null;
                } else {
                    tmpnombreJar[i] = new String(d.nameJar[i - nameJar.length]);
                }
            }
            nameJar = tmpnombreJar;
        }

    }

    /**
     * Insert new dsc
     * @param d New dsc
     * @param unaCapa True of it only contains one layer
     */
    void insert(ExternalObjectDescription d, boolean unaCapa) {

        // insert fields of a new layer in the object

        if (subtype != d.subtype) {
            System.out.println("WARNING: Trying to merge different kinds of dsc");
            return;
        }

        if (d.name != null) {
            int len = 0;
            if (name != null) {
                len = name.length;
            }
            String[] tmpnombre = new String[len + 1];
            for (int i = 0; i < name.length; i++) {
                if (name[i] == null) {
                    tmpnombre[i] = null;
                } else {
                    tmpnombre[i] = new String(name[i]);
                }
            }
            if (d.name[0] == null) {
                tmpnombre[name.length] = null;
            } else {
                tmpnombre[name.length] = new String(d.name[0]);
            }
            name = tmpnombre;
        }

        if (d.path != null) {
            int len = 0;
            if (path != null) {
                len = path.length;
            }
            String[] tmppath = new String[len + 1];
            for (int i = 0; i < path.length; i++) {
                if (path[i] == null) {
                    tmppath[i] = null;
                } else {
                    tmppath[i] = new String(path[i]);
                }
            }
            if (d.path[0] == null) {
                tmppath[path.length] = null;
            } else {
                tmppath[path.length] = new String(d.path[0]);
            }
            path = tmppath;
        }

        if (d.nameJar != null) {
            int len = 0;
            if (nameJar != null) {
                len = nameJar.length;
            }
            String[] tmpnombreJar = new String[len + 1];
            for (int i = 0; i < nameJar.length; i++) {
                if (nameJar[i] == null) {
                    tmpnombreJar[i] = null;
                } else {
                    tmpnombreJar[i] = new String(nameJar[i]);
                }
            }
            if (d.nameJar[0] == null) {
                tmpnombreJar[nameJar.length] = null;
            } else {
                tmpnombreJar[nameJar.length] = new String(d.nameJar[0]);
            }
            nameJar = tmpnombreJar;
        }

    }

    /**
     * Gets the name of the object at the active layer
     * Currently, only layer 0 is used in KEEL
     * @return the name of the object (at layer 0 by default)
     */
    public String getName() {
        return name[Layer.layerActivo];
    }

    /**
     * The name of the object at the layer indicated
     * @param k the number of the layer
     * @return the name of the object
     */
    public String getName(int k) {
        return name[k];
    }

    /**
     * Gets all the names
     * @return a vector with all the names
     */
    public String[] getAllNames() {
        return name;
    }

    /**
     * Gets the path at the active layer
     * @return returns the path ((at layer 0 by default)
     */
    public String getPath() {
        return path[Layer.layerActivo];
    }

    /**
     * Get the path from the indicated layer
     * @param k the index to the layer
     * @return the path of layer k
     */
    public String getPath(int k) {
        return path[k];
    }

    /**
     * Gets the subtype of this object
     * @return the current subtype
     */
    public int getSubtype() {
        return subtype;
    }

    /**
     * Gets the subtype of this object
     * @return the current subtype
     */
    public int getSubtypelqd() {
        return subtypelqd;
    }

    /**
     * Gets the JAR name (i.e. the name with the extesion ".jar") of
     * the active layer
     * @return the JAR name (at layer 0 by default)
     */
    public String getJarName() {
        return nameJar[Layer.layerActivo];
    }

    /**
     * Gets the JAR name at the indicated layer
     * @param k the number of the layer
     * @return the JAR name (at layer 0 by default)
     */
    public String getJarName(int k) {
        return nameJar[k];
    }

    /**
     * Gets the size of the names array
     * @return the names' length
     */
    public int getNamesLength() {
        return name.length;
    }

    /**
     * Sets the path of the indicated layer
     * @param p the new path
     * @param ly the number of the layer to be modified
     */
    public void setPath(String p, int ly) {
        path[ly] = new String(p);
    }

    /**
     * Sets the path of the active layer
     * @param p The new path (at layer 0 by default)
     */
    public void setPath(String p) {
        path[Layer.layerActivo] = new String(p);
    }

    /**
     * Sets the name of the current active layer
     * @param n the new name (at layer 0 by default)
     */
    public void setName(String n) {
        name[Layer.layerActivo] = new String(n);
    }

    /**
     * Sets the name of the indicated layer
     * @param n the new name
     * @param ly the layer to be modified
     */
    public void setName(String n, int ly) {
        name[ly] = new String(n);
    }

    /**
     * Sets the subtype of this object
     * @param s the new subtype
     */
    public void setSubtype(int s) {
        subtype = s;
    }

    /**
     * Sets the subtype of this object
     * @param s the new subtype
     */
    public void setSubtypelqd(int s) {
        subtypelqd = s;
    }

    /**
     * To string method
     * @return String repressentation
     */
    @Override
    public String toString() {
        if (name == null) {
            return ("NO INITIALIZED");
        } else {
            return name[Layer.layerActivo >= name.length ? name.length - 1 : Layer.layerActivo];
        }
    }

    /**
     * Gets a string with all the names from all the layers
     * @return a string with all the names of the different layers
     */
    public String enumerateNames() {
        String result = new String("");
        for (int i = 0; i < name.length; i++) {
            result += name[i];
            if (i < name.length - 1) {
                result += " ";
            }
        }
        return result;
    }

    /**
     * Test if 2 objects are equals by means of the names
     * of their current active layers
     * @param d the object to be compared
     * @return true if the names of the active layers are the same
     */
    boolean equals(ExternalObjectDescription d) {
        // System.out.println("Comparing "+nombre+" with "+d.nombre);
        if (name == null && d.name == null) {
            return true;
        }
        if (name[Layer.layerActivo] == null && d.name[Layer.layerActivo] == null) {
            return true;
        }
        // don't check other things
        return name[Layer.layerActivo].equals(d.name[Layer.layerActivo]);

    }

    /**
     * Implements the String's lexicographic order
     */
    public int compareTo(Object o) {
        ExternalObjectDescription doe = (ExternalObjectDescription) o;

        return this.getName(0).compareTo(doe.getName(0));
    }
}
