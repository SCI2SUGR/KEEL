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
 * <p>
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Hyperrectangles.EACH;


public class Sample {
/**
 * <p>
 * Stores one data with the form: attribute attribute class
 * </p>
 */

    private double sample[];
    private int classAttribute;
    private long posFile; // orden de aparicion en el fichero
    private int size;
    private int covered;

    /**
     * <p>
     * Constructor
     * </p>
     * @param m un Vector of attributes(valores)
     * @param c The owner class of the data
     * @param size Size of the data
     */
    public Sample(double m[], int c, int size) {
        super();
        sample = m;
        classAttribute = c;
        this.size = size;
        covered = 0;
    }

    /**
     * <p>
     * Other constructor, more easy
     * </p>
     * @param size The size of the data(n attributes)
     */
    public Sample(int size) {
        this.size = size;
        sample = new double[this.size];
    }

    /**
     * <p>
     * Returns the example's class
     * </p>
     * @return the class
     */
    public int getClassSelector() {
        return classAttribute;
    }

    /**
     * <p>
     * Returns the attributes(array of values)
     * </p>
     * @return the complex data
     */
    public double[] getSample() {
        return sample;
    }

    /**
     * <p>
     * Assigns the class
     * </p>
     * @param i number of the class
     */
    public void setClase(int i) {
        classAttribute = i;
    }

    /**
     * <p>
     * Assigns the in-puts of the data
     * </p>
     * @param ds An array of values for the data
     */
    public void setSample(double[] ds) {
        int i;
        for (i = 0; i < size; i++) {
            sample[i] = ds[i];
        }
    }

    /**
     * <p>
     * Returns the position of the example inf the in-put file of data
     * </p>
     * @return the position in the file
     */
    public long getPosFile() {
        return posFile;
    }

    /**
     * <p>
     * Assigns the position of the example in the in-put file of data
     * </p>
     * @param l the position in the file
     */
    public void setPosFile(long l) {
        posFile = l;
    }

    /**
     * <p>
     * Returns the value of the attribute 'i' of the example
     * </p>
     * @param i The atribute's position
     * @return The value of the attribute
     */
    public double getSample(int i) {
        return sample[i];
    }

    /**
     * <p>
     * Returns the number of attributes of the example
     * </p>
     * @return number of attributes
     */
    public int getNAttributes() {
        return size;
    }

    /**
     * <p>
     * Gives value to an atribute
     * </p>
     * @param i Position of the attribute
     * @param val new value
     */
    public void setSample(int i, double val) {
        sample[i] = val;
    }

    /**
     * <p>
     * Prints on the screen the example's content
     * </p>
     */
    public void print() {
        int i;

        System.out.print("\nPos " + posFile + ": ");
        for (i = 0; i < size; i++) {
            System.out.print(" " + sample[i]);
        }
        System.out.print("  Cl: " + classAttribute);
    }

    /**
     * <p>
     * Do a copy of the example
     * </p>
     * @return A new copy of the example
     */
    public Sample copySample() {
        Sample m = new Sample(size);
        m.setSample(sample);
        m.setClase(classAttribute);
        m.setPosFile(posFile);
        return m;
    }

    /**
     * <p>
     * Do a copy of the example
     * </p>
     * @return A new copy of the example
     */
    public boolean compare(Sample m) {
        boolean bEquals = true;
        for (int i = 0; i < this.getNAttributes() && bEquals; i++) {
            bEquals = (this.getSample(i) == m.getSample(i));
        }
        return bEquals;
    }

    /**
     * <p>
     * Returns the number of times that the example has benn matched
     * </p>
     * @return idem.
     */
    public int getCovered() {
        return covered;
    }

    /**
     * <p>
     * Adds one to the number of times that the example has been matched
     * </p>
     */
    public void incrementCovered() {
        covered++;
    }

    /**
     * <p>
     * Assign a new value for the 'n' times that the example has benn matched
     * </p>
     * @param d value
     */
    public void setCovered(int d) {
        covered = d;
    }


}

