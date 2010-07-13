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

package keel.Algorithms.Rule_Learning.CN2;

/**
 * <p>Title: Instance</p>
 * <p>Description: Structure for an instance used in the data-set</p>
 * @author Written by José Ramón Cano de Amo (University of Jaen) 08/02/2004
 * @author Modified by Alberto Fernández (University of Granada) 11/27/2004
 * @version 1.1
 * @since JDK1.4
 */
public class Instance {

    /**
     *  It stores an instance in the form attr, attr, attr, class
     */

    private double muest[];
    private int clas;
    private long posFile; // position in the file
    private int tam;
    private int covered;

    /**
     * Builder
     * @param m an array of attributes
     * @param cl the class of the instance
     * @param size the size of the instance (number of attributes)
     */
    public Instance(double m[], int cl, int size) {
        super();
        muest = m;
        clas = cl;
        tam = size;
        covered = 0;
    }

    /**
     * Builder without data
     * @param size the size of the instance (number of attributes)
     */
    public Instance(int size) {
        super();
        tam = size;
        muest = new double[tam];
    }

    /**
     * It returns the class of the example
     * @return the class
     */
    public int getClas() {
        return clas;
    }

    /**
     * It returns the example itself (array of values)
     * @return the complete example
     */
    public double[] getMuest() {
        return muest;
    }

    /**
     * It assigns the class
     * @param i class id
     */
    public void setClass(int i) {
        clas = i;
    }

    /**
     * It assigns the values for the instance
     * @param ds an array of values for the instance
     */
    public void setMuest(double[] ds) {
        int i;
        for (i = 0; i < tam; i++) {
            muest[i] = ds[i];
        }
    }

    /**
     * It returns the position of the example in the input data file
     * @return the file position
     */
    public long getPosFile() {
        return posFile;
    }

    /**
     * It assigns the position of the example in the input data file
     * @param l file position
     */
    public void setPosFile(long l) {
        posFile = l;
    }

    /**
     * It returns the value of the attribute i-th of the example
     * @param i attribute position
     * @return attribute vaule
     */
    public double getAttribute(int i) {
        return muest[i];
    }

    /**
     * It returns the number of attributes of the example
     * @return number of attributes
     */
    public int getNattributes() {
        return tam;
    }

    /**
     * It sets a value for an attribute
     * @param i attribute position
     * @param val new value
     */
    public void setAttribute(int i, double val) {
        muest[i] = val;
    }

    /**
     * It shows the content of the example
     */
    public void print() {
        int i;

        System.out.print("\nPos " + posFile + ": ");
        for (i = 0; i < tam; i++) {
            System.out.print(" " + muest[i]);
        }
        System.out.print("  Cl: " + clas);
    }

    /**
     * It makes a copy of the example
     * @return a new cloned example
     */
    public Instance copy() {
        Instance m = new Instance(tam);
        m.setMuest(muest);
        m.setClass(clas);
        m.setPosFile(posFile);
        return m;
    }

    /**
     * It compares if two examples are the same
     * @param m Example to compare
     * @return True if they are the same. False in other case
     */
    public boolean compare(Instance m) {
        boolean iguales = true;
        for (int i = 0; i < this.getNattributes() && iguales; i++) {
            iguales = (this.getAttribute(i) == m.getAttribute(i));
        }
        return iguales;
    }

    /**
     * It returns the "covered" value, that is, the number of rules that covers the example
     * @return the number of rules that covers the example
     */
    public int getCovered() {
        return covered;
    }

    /**
     * It gives a "covered" value
     * @param value new value
     */
    public void setCovered(int value) {
        covered = value;
    }

    /**
     * Increments the "covered" value
     */
    public void addCovered() {
        covered++;
    }

}

