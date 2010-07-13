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
 
public class vectordouble {
/**
 * <p>
 * Encodes a vector of double values
 * </p>
 */
 	
    public static final double MISSING = -999999999;
    int reservado;
    int numero;
    double[] data;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    vectordouble() {
        reservado = 0;
        numero = 0;
        data = new double[0];
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param tamano int The size of the vector
     */
    vectordouble(int tamano) {
        reservado = tamano;
        numero = 0;
        data = new double[tamano];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param x double[] The data to be copied in the vector
     * @param tamano int The size of the vector
     */
    vectordouble(double[] x, int tamano) {
        reservado = tamano;
        numero = tamano;
        data = new double[tamano];

        for (int i = 0; i < tamano; i++) {
            data[i] = x[i];
        }
    }

    /**
     * <p>
     * Creates a vectordouble as a copy of another one
     * </p>
     * @param x vectordouble The vectordouble used to create the new one
     */
    vectordouble(vectordouble x) {
        this.reservado = x.reservado;
        this.numero = x.numero;
        this.data = new double[reservado];

        for (int i = 0; i < numero; i++) {
            this.data[i] = x.data[i];
        }
    }


    /**
     * <p>
     * Increases the memory for the vector to the double
     * </p>
     */
    public void Realloc() {
        double[] x;

        x = new double[numero];
        for (int i = 0; i < numero; i++) {
            x[i] = data[i];
        }

        data = new double[reservado * 2 + 1];
        for (int i = 0; i < numero; i++) {
            data[i] = x[i];
        }

        reservado = reservado * 2 + 1;
    }


    /**
     * <p>
     * Add the "x" value to the vector
     * </p>
     * @param x double The value to be added to the vector
     */
    public void Append(double x) {
        if (numero == reservado) {
            Realloc();
        }
        data[numero] = x;
        numero++;
    }


    /**
     * <p>
     * Set the value to the vector in position "pos" to the value "x" (overwritting the previous value)
     * </p>
     * @param x double The new value
     * @param pos int The position in the vector
     */
    public void Put(double x, int pos) {
        if (pos < 0 || pos > numero) {
            System.out.println("The position " + pos + " does not exist");
        } else {
            data[pos] = x;
        }
    }

    /**
     * <p>
     * Set the values of the first "tamano" elements of the vector to the values in vector "x"
     * </p>
     * @param x double[] The new values
     * @param pos int The last position in the vector
     */
    public void Put(double[] x, int tamano) {
        reservado = tamano;
        numero = tamano;
        data = new double[tamano];

        for (int i = 0; i < tamano; i++) {
            data[i] = x[i];
        }
    }


    /**
     * <p>
     * Returns a new vector containing the first "tamano" elements of the vector
     * </p>
     * @param tamano Integer The last position in the vector
     * @param double[] The new vector
     */
    public double[] Convert(Integer tamano) {
        double[] x;

        x = new double[numero];
        for (int i = 0; i < numero; i++) {
            x[i] = data[i];
        }

        tamano = numero;
        return x;
    }


    /**
     * <p>
     * Returns the value in the position "pos" of the vector (or -99999999 if the position is not valid)
     * </p>
     * @param pos int The position in the vector
     * @param double The value of the vector in this position
     */
    public double At(int pos) {
        if (pos < 0 || pos > numero) {
            System.out.println("The position " + pos + " does not exist");
            return -99999999; /*return an error*/
        } else {
            return data[pos];
        }
    }


	/**
	 * <p>
	 * Prints in the standard output the vector
	 * </p>
	 */
    public void PrintDefinition() {
        System.out.println("");
        System.out.println("==========================");
        for (int i = 0; i < numero; i++) {
            System.out.print("   " + i);
        }
        System.out.println("");

        for (int i = 0; i < numero; i++) {
            System.out.print("   " + data[i]);
        }
        System.out.println("");
        System.out.println("==========================");
    }


	/**
	 * <p>
	 * Returns the maximum value of the vector
	 * </p>
	 * @return double The maximum value of the vector
	 */
    public double Max() {
        double max;

        if (numero > 0) {
            max = data[0];
            for (int i = 1; i < numero; i++) {
                if (data[i] > max) {
                    max = data[i];
                }
            }
            return max;
        } else {
            return -999999999;
        }
    }

	/**
	 * <p>
	 * Returns the minimum value of the vector
	 * </p>
	 * @return double The minimum value of the vector
	 */
    public double Min() {
        double min;

        if (numero > 0) {
            min = data[0];
            for (int i = 1; i < numero; i++) {
                if (data[i] < min) {
                    min = data[i];
                }
            }
            return min;
        } else {
            return -999999999;
        }
    }

	/**
	 * <p>
	 * Returns the position in the vector for a value "x" or -1 if the value is not found in the vector
	 * </p>
	 * @param x double The value to be found
	 * @return int The position in the vector for a value "x" or -1 if the value is not found in the vector
	 */
    public int find(double x) {
        int pos = -1;
        for (int i = 0; i < numero && pos == -1; i++) {
            if (data[i] == x) {
                pos = i;
            }
        }

        return pos;
    }


	/**
	 * <p>
	 * Returns the position in the vector for maximum value of the vector
	 * </p>
	 * @return int The position in the vector for maximum value of the vector
	 */
    public int PosMax() {
        int pos = 0;

        if (numero == 0) {
            return -1;
        }

        for (int i = 1; i < numero; i++) {
            if (data[i] > data[pos]) {
                pos = i;
            }
        }

        return pos;
    }

	/**
	 * <p>
	 * Returns the position in the vector for minimum value of the vector
	 * </p>
	 * @return int The position in the vector for minimum value of the vector
	 */
    public int PosMin() {
        int pos = 0;

        if (numero == 0) {
            return -1;
        }

        for (int i = 1; i < numero; i++) {
            if (data[i] < data[pos]) {
                pos = i;
            }
        }

        return pos;
    }


	/**
	 * <p>
	 * Returns the average value for all the values in the vector (or position -999999999 if the vector is empty)
	 * </p>
	 * @return double The average value for all the values in the vector (or position -999999999 if the vector is empty)
	 */
    public double Average() {
        double media = 0;

        if (numero > 0) {
            for (int i = 0; i < numero; i++) {
                media = media + data[i];
            }

            return media / numero;
        } else {
            return -999999999;
        }
    }


	/**
	 * <p>
	 * Sorts the values in the vector (in ascending order)
	 * </p>
	 */
    public void Sort() {
        double x;

        for (int i = 0; i < numero - 1; i++) {
            for (int j = numero - 1; j > i; j--) {
                if (data[j - 1] > data[j]) {
                    x = data[j];
                    data[j] = data[j - 1];
                    data[j - 1] = x;
                }
            }
        }
    }


}

