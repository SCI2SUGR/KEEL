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
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Prism;


public class Muestra {
/**
 * <p>
 * Stores one data with the form: attribute attribute class
 * </p>
 */

    private double muest[];
    private int clase;
    //position in the file
    private long posFile; 
    private int tam;
    private int cubierta;

    /**
     * <p>
     * Constructor
     * </p>
     * @param m un Vector of attributes(valores)
     * @param cl The owner class of the data
     * @param tamano Size of the data
     */
    public Muestra(double m[], int cl, int tamano) {
        super();
        muest = m;
        clase = cl;
        tam = tamano;
        cubierta = 0;
    }

    /**
     * <p>
     * Other constructor, more easy
     * </p>
     * @param tamano The size of the data(n attributes)
     */
    public Muestra(int tamano) {
        tam = tamano;
        muest = new double[tam];
    }

    /**
     * <p>
     * Returns the example's class
     * </p>
     * @return the class
     */
    public int getClase() {
        return clase;
    }

    /**
     * <p>
     * Returns the attributes(array of values)
     * </p>
     * @return the complex data
     */
    public double[] getMuest() {
        return muest;
    }

    /**
     * <p>
     * Assigns the class
     * </p>
     * @param i number of the class
     */
    public void setClase(int i) {
        clase = i;
    }

    /**
     * <p>
     * Assigns the in-puts of the data
     * </p>
     * @param ds An array of values for the data
     */
    public void setMuest(double[] ds) {
        int i;
        for (i = 0; i < tam; i++) {
            muest[i] = ds[i];
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
    public double getAtributo(int i) {
        return muest[i];
    }

    /**
     * <p>
     * Returns the number of attributes of the example
     * </p>
     * @return number of attributes
     */
    public int getNatributos() {
        return tam;
    }

    /**
     * <p>
     * Gives value to an atribute
     * </p>
     * @param i Position of the attribute
     * @param val new value
     */
    public void setAtributo(int i, double val) {
        muest[i] = val;
    }

    /**
     * <p>
     * Prints on the screen the example's content
     * </p>
     */
    public void print() {
        int i;

        System.out.print("\nPos " + posFile + ": ");
        for (i = 0; i < tam; i++) {
            System.out.print(" " + muest[i]);
        }
        System.out.print("  Cl: " + clase);
    }

    /**
     * <p>
     * Do a copy of the example
     * </p>
     * @return A new copy of the example
     */
    public Muestra copiaMuestra() {
        Muestra m = new Muestra(tam);
        m.setMuest(muest);
        m.setClase(clase);
        m.setPosFile(posFile);
        return m;
    }

    /**
     * <p>
     * Compare if two examples are equals
     * </p>
     * @param m Example to compare
     * @return True if are equals. False otherwise
     */
    public boolean compara(Muestra m) {
        boolean iguales = true;
        for (int i = 0; i < this.getNatributos() && iguales; i++) {
            iguales = (this.getAtributo(i) == m.getAtributo(i));
        }
        return iguales;
    }

    /**
     * <p>
     * Returns the number of times that the example has benn matched
     * </p>
     * @return idem.
     */
    public int getCubierta() {
        return cubierta;
    }

    /**
     * <p>
     * Adds one to the number of times that the example has been matched
     * </p>
     */
    public void incrementaCubierta() {
        cubierta++;
    }

    /**
     * <p>
     * Assign a new value for the 'n' times that the example has benn matched
     * </p>
     * @param d value
     */
    public void setCubierta(int d) {
        cubierta = d;
    }


}

