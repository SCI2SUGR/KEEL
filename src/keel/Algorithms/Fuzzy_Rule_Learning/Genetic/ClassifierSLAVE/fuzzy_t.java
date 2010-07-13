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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
//import org.core.*;


public class fuzzy_t {
/**
 * <p>
 * Defines a trapezoidal fuzzy set type
 * </p>
 */
	
    public static final int MISSING = -999999999;
    double a, b, c, d;
    boolean menosinfinito, masinfinito;
    String nombre;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public fuzzy_t() {
        a = b = c = d = 0;
        menosinfinito = masinfinito = false;
        nombre = "Creado, no usado";
    }

    /**
     * <p>
     * Constructor
     * [a,b,c,d] represents a trapezoidal label and "name" is the name of the label.
     * It is assumed by defect that the extremes are delimited.
     * </p>
     * @param a double Parameter a for the trapezoidal fuzzy set
     * @param b double Parameter b for the trapezoidal fuzzy set
     * @param c double Parameter c for the trapezoidal fuzzy set
     * @param d double Parameter d for the trapezoidal fuzzy set
     * @param name String Name for the fuzzy set
     */
    public fuzzy_t(double a, double b, double c, double d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        nombre = name;
        menosinfinito = false;
        masinfinito = false;
    }


    /**
     * <p>
     * Constructor
     * [a,b,c,d] represents a trapezoidal label and "name" is the name of the label.
     * "menos" and "mas" represents if the extremes are delimited or not.
     * </p>
     * @param a double Parameter a for the trapezoidal fuzzy set
     * @param b double Parameter b for the trapezoidal fuzzy set
     * @param c double Parameter c for the trapezoidal fuzzy set
     * @param d double Parameter d for the trapezoidal fuzzy set
     * @param name String Name for the fuzzy set
     * @param menos boolean TRUE if the negative extreme is delimited. FALSE otherwise
     * @param mas boolean TRUE if the positive extreme is delimited. FALSE otherwise     
     */
    public fuzzy_t(double a, double b, double c, double d, String name,
                   boolean menos, boolean mas) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        menosinfinito = menos;
        masinfinito = mas;
        nombre = name;
    }


    /**
     * <p>
     * Creates a fuzzy_t object as a copy of "x"
     * </p>
     * @param x fuzzy_t The object used to created the new one
     */
    public fuzzy_t(fuzzy_t x) {
        this.a = x.a;
        this.b = x.b;
        this.c = x.c;
        this.d = x.d;
        this.menosinfinito = x.menosinfinito;
        this.masinfinito = x.masinfinito;
        this.nombre = x.nombre;
    }


    /**
     * <p>
     * Assigns a label
     * [a,b,c,d] represents a trapezoidal label and "name" is the name of the label.
     * "menos" and "mas" represents if the extremes are delimited or not.
     * </p>
     * @param a double Parameter a for the trapezoidal fuzzy set
     * @param b double Parameter b for the trapezoidal fuzzy set
     * @param c double Parameter c for the trapezoidal fuzzy set
     * @param d double Parameter d for the trapezoidal fuzzy set
     * @param name String Name for the fuzzy set
     * @param menos boolean TRUE if the negative extreme is delimited. FALSE otherwise
     * @param mas boolean TRUE if the positive extreme is delimited. FALSE otherwise     
     */
    public void Set(double a, double b, double c, double d, String name,
                       boolean menos, boolean mas) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        menosinfinito = menos;
        masinfinito = mas;
        nombre = name;
    }

    /**
     * <p>
     * Assigns a label
     * [a,b,c,d] represents a trapezoidal label and "name" is the name of the label.
     * It is assumed by defect that the extremes are delimited.
     * </p>
     * @param a double Parameter a from the trapezoidal fuzzy set
     * @param b double Parameter b from the trapezoidal fuzzy set
     * @param c double Parameter c from the trapezoidal fuzzy set
     * @param d double Parameter d from the trapezoidal fuzzy set
     * @param name String Name of the fuzzy set   
     */
    public void Set(double a, double b, double c, double d, String name) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        menosinfinito = false;
        masinfinito = false;
        nombre = name;
    }

	/**
	 * <p>
	 * Returns the adaptation degree of a value x to the label.
	 * </p>
	 * @param x double The value
	 * @return double The adaptation degree
	 */
    public double Adaptation(double x) {
        if (x == -999999999) {
            return 1;
        }
        if ((menosinfinito && x < c) || (masinfinito && x > b)) {
            return 1;
        }
        if (x < a) {
            return 0;
        } else if (x < b) {
            return (x - a) / (b - a);
        } else if (x <= c) {
            return 1;
        } else if (x < d) {
            return (d - x) / (d - c);
        } else {
            return 0;
        }
    }


// Sets a name to the label
    private void Poner_Nombre(String name) {
        this.nombre = name;
    }


	/**
	 * <p>
	 * Prints in a String the definition of the label
	 * </p>
	 * @return String The definition of the label
	 */
    public String PrintDefinitionToString() {
        String cadena = "";

        cadena += "Name: " + nombre + "\n";
        if (menosinfinito) {
            cadena += "[-inf,-inf," + c + "," + d + "]\n";
        } else if (masinfinito) {
            cadena += "[" + a + "," + b + ",inf,inf]\n";
        } else {
            cadena += "[" + a + "," + b + "," + c + "," + d + "]\n";
        }

        return(cadena);
    }


	/**
	 * <p>
	 * Prints in the standard output the definition of the label
	 * </p>
	 */
    public void PrintDefinition() {
        System.out.println("Name: " + nombre);
        if (menosinfinito) {
            System.out.println("[-inf,-inf," + c + "," + d + "]");
        } else if (masinfinito) {
            System.out.println("[" + a + "," + b + ",inf,inf]");
        } else {
            System.out.println("[" + a + "," + b + "," + c + "," + d + "]");
        }
    }

	/**
	 * <p>
	 * Prints in the standard output the name of the label
	 * </p>
	 */
    public void Print() {
        System.out.println(nombre);
    }


	/**
	 * <p>
	 * Prints in a String the name of the label
	 * </p>
	 * @return String The name of the label
	 */
    public String SPrint() {
        return nombre;
    }


	/**
	 * <p>
	 * Returns a fuzzy_t object with the label
	 * </p>
	 */	 
    public fuzzy_t FuzzyLabel() {
        fuzzy_t aux = new fuzzy_t(this);

        return aux;
    }


	/**
	 * <p>
	 * Returns the central value of the label
	 * </p>
	 * @return double The central value of the label
	 */
    public double CenterLabel() {
        if (menosinfinito) {
            return c;
        } else if (masinfinito) {
            return b;
        } else {
            return (b + c) / 2.0;
        }
    }


	/**
	 * <p>
	 * Returns if the fuzzy label represents a crisp value
	 * </p>
	 * @return boolean TRUE if the fuzzy label represents a crisp value. FALSE otherwise
	 */
    public boolean IsDiscrete() {
        return (a == d);
    }


	/**
	 * <p>
	 * Returns if the fuzzy label represents an interval
	 * </p>
	 * @return boolean TRUE iif the fuzzy label represents an interval. FALSE otherwise
	 */
    public boolean IsInterval() {
        return (a == b && c == d && b != c);
    }


	/**
	 * <p>
	 * Returns if the fuzzy label represents a fuzzy set
	 * </p>
	 * @return boolean TRUE if the the fuzzy label represents a fuzzy set. FALSE otherwise
	 */
    public boolean IsFuzzy() {
        return (!IsDiscrete() && !IsInterval());
    }


	/**
	 * <p>
	 * Returns the area of the label.
	 * </p>
	 */
    public double Area() {
        return ((b - a) / 2.0) + ((d - c) / 2.0) + (c - d);
    }

}

