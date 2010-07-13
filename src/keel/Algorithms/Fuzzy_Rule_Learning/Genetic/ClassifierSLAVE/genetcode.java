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

import java.util.ArrayList;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class genetcode {
/**
 * <p>
 * This class encodes a rule (which is formed of a multipopulation)
 * </p>
 */
 
    int binary;
    int[] nbinary;
    char[][] mbinary;
    int integer;
    int[] ninteger;
    int[][] minteger;
    int real;
    int[] nreal;
    double[][] mreal;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    genetcode() {
        binary = integer = real = 0;
        nbinary = new int[0];
        ninteger = new int[0];
        nreal = new int[0];
        mbinary = new char[0][0];
        minteger = new int[0][0];
        mreal = new double[0][0];
    }

    /**
     * <p>
     * Creates a genetcode object as a copy of another one
     * </p>
     * @param x genetcode The genetcode used to create the new one
     */
    genetcode(genetcode x) {
        this.binary = x.binary;
        this.nbinary = new int[this.binary];
        for (int i = 0; i < binary; i++) {
            this.nbinary[i] = x.nbinary[i];
        }
        this.mbinary = new char[this.binary][];
        for (int i = 0; i < this.binary; i++) {
            this.mbinary[i] = new char[this.nbinary[i]];
            for (int j = 0; j < this.nbinary[i]; j++) {
                this.mbinary[i][j] = x.mbinary[i][j];
            }
        }

        this.integer = x.integer;
        this.ninteger = new int[this.integer];
        for (int i = 0; i < this.integer; i++) {
            this.ninteger[i] = x.ninteger[i];
        }
        this.minteger = new int[this.integer][];
        for (int i = 0; i < this.integer; i++) {
            this.minteger[i] = new int[this.ninteger[i]];
            for (int j = 0; j < this.ninteger[i]; j++) {
                this.minteger[i][j] = x.minteger[i][j];
            }
        }

        this.real = x.real;
        this.nreal = new int[this.real];
        for (int i = 0; i < this.real; i++) {
            this.nreal[i] = x.nreal[i];
        }
        this.mreal = new double[real][];
        for (int i = 0; i < real; i++) {
            this.mreal[i] = new double[this.nreal[i]];
            for (int j = 0; j < this.nreal[i]; j++) {
                this.mreal[i][j] = x.mreal[i][j];
            }
        }

    }

    /**
     * <p>
     * Sets the binary part of the multipopulation
     * </p>
     * @param bin int The number of binary populations
     * @param nbin int[] The number of elements in each one of the binary populations
     * @param mbin char[][] The data for each one of the binary populations
     */
    void PutBinary(int bin, int[] nbin, char[][] mbin) {
        binary = bin;
        nbinary = new int[binary];
        for (int i = 0; i < binary; i++) {
            nbinary[i] = nbin[i];
        }
        mbinary = new char[binary][];
        for (int i = 0; i < binary; i++) {
            mbinary[i] = new char[nbinary[i]];
            for (int j = 0; j < nbinary[i]; j++) {
                mbinary[i][j] = mbin[i][j];
            }
        }

    }

    /**
     * <p>
     * Sets the integer part of the multipopulation
     * </p>
     * @param ent int The number of integer populations
     * @param nent int[] The number of elements in each one of the integer populations
     * @param ment char[][] The data for each one of the integer populations
     */
    void PutInteger(int ent, int[] nent, int[][] ment) {
        integer = ent;
        ninteger = new int[integer];
        for (int i = 0; i < integer; i++) {
            ninteger[i] = nent[i];
        }
        minteger = new int[integer][];
        for (int i = 0; i < integer; i++) {
            minteger[i] = new int[ninteger[i]];
            for (int j = 0; j < ninteger[i]; j++) {
                minteger[i][j] = ment[i][j];
            }
        }
    }

    /**
     * <p>
     * Sets the real part of the multipopulation
     * </p>
     * @param rea int The number of integer populations
     * @param nrea int[] The number of elements in each one of the real populations
     * @param mrea char[][] The data for each one of the real populations
     */
    void PutReal(int rea, int[] nrea, double[][] mrea) {
        real = rea;
        nreal = new int[real];
        for (int i = 0; i < real; i++) {
            nreal[i] = nrea[i];
        }
        mreal = new double[real][];
        for (int i = 0; i < real; i++) {
            mreal[i] = new double[nreal[i]];
            for (int j = 0; j < nreal[i]; j++) {
                mreal[i][j] = mrea[i][j];
            }
        }
    }


    /**
     * <p>
     * Sets the element "columna" of the "fila" binary population to "value"
     * </p>
     * @param fila int The binary population
     * @param columna int The element in the binary population
     * @param value char The new value
     */
    void PutValueBinary(int fila, int columna, char value) {
        mbinary[fila][columna] = value;
    }

    /**
     * <p>
     * Sets the element "columna" of the "fila" integer population to "value"
     * </p>
     * @param fila int The integer population
     * @param columna int The element in the integer population
     * @param value int The new value
     */
    void PutValueInteger(int fila, int columna, int value) {
        minteger[fila][columna] = value;
    }

    /**
     * <p>
     * Sets the element "columna" of the "fila" real population to "value"
     * </p>
     * @param fila int The real population
     * @param columna int The element in the real population
     * @param value double The new value
     */
    void PutValueReal(int fila, int columna, double value) {
        mreal[fila][columna] = value;
    }


    /**
     * <p>
     * Returns the number of elements in the binary population number "fila"
     * </p>
     * @param fila int The binary population
     * @return int The number of elements in the population
     */
    int SizeBinary(int fila) {
        return nbinary[fila];
    }

    /**
     * <p>
     * Returns the number of elements in the integer population number "fila"
     * </p>
     * @param fila int The integer population
     * @return int The number of elements in the population
     */
    int SizeInteger(int fila) {
        return ninteger[fila];
    }

    /**
     * <p>
     * Returns the number of elements in the real population number "fila"
     * </p>
     * @param fila int The real population
     * @return int The number of elements in the population
     */
    int SizeReal(int fila) {
        return nreal[fila];
    }


    /**
     * <p>
     * Returns the binary part of the multipopulation
     * </p>
     * @param milista1 ArrayList<Integer> The number of binary populations
     * @param milista2 ArrayList<int[]> The number of elements in each one of the binary populations
     * @param milista3 ArrayList<char[][]> The data for each one of the binary populations
     */
    void GetBinary(ArrayList<Integer> milista1, ArrayList<int[]> milista2,
            ArrayList<char[][]> milista3) {
        Integer bin_aux;
        int bin;
        bin = binary;
        bin_aux = Integer.valueOf(bin);
        milista1.add(0, bin_aux);

        int[] nbin = new int[bin];
        for (int i = 0; i < bin; i++) {
            nbin[i] = nbinary[i];
        }
        milista2.add(0, nbin);

        char[][] mbin;
        mbin = new char[bin][];
        for (int i = 0; i < bin; i++) {
            mbin[i] = new char[nbinary[i]];
            for (int j = 0; j < nbin[i]; j++) {
                mbin[i][j] = mbinary[i][j];
            }
        }
        milista3.add(0, mbin);
    }

    /**
     * <p>
     * Returns the integer part of the multipopulation
     * </p>
     * @param milista1 ArrayList<Integer> The number of integer populations
     * @param milista2 ArrayList<int[]> The number of elements in each one of the integer populations
     * @param milista3 ArrayList<int[][]> The data for each one of the integer populations
     */
    void GetInteger(ArrayList<Integer> milista1, ArrayList<int[]> milista2,
            ArrayList<int[][]> milista3) {
        Integer ent_aux;
        int ent;
        ent = integer;
        ent_aux = Integer.valueOf(ent);
        milista1.add(0, ent_aux);

        int[] nent = new int[ent];
        for (int i = 0; i < ent; i++) {
            nent[i] = ninteger[i];
        }
        milista2.add(0, nent);

        int[][] ment;
        ment = new int[ent][];
        for (int i = 0; i < ent; i++) {
            ment[i] = new int[ninteger[i]];
            for (int j = 0; j < nent[i]; j++) {
                ment[i][j] = minteger[i][j];
            }
        }
        milista3.add(0, ment);
    }

    /**
     * <p>
     * Returns the real part of the multipopulation
     * </p>
     * @param milista1 ArrayList<Integer> The number of real populations
     * @param milista2 ArrayList<int[]> The number of elements in each one of the real populations
     * @param milista3 ArrayList<double[][]> The data for each one of the real populations
     */
    void GetReal(ArrayList<Integer> milista1, ArrayList<int[]> milista2,
            ArrayList<double[][]> milista3) {
        Integer rea_aux;
        int rea;
        rea = real;
        rea_aux = Integer.valueOf(rea);
        milista1.add(0, rea_aux);

        int[] nrea = new int[rea];
        for (int i = 0; i < rea; i++) {
            nrea[i] = nreal[i];
        }
        milista2.add(0, nrea);

        double[][] mrea;
        mrea = new double[rea][];
        for (int i = 0; i < rea; i++) {
            mrea[i] = new double[nreal[i]];
            for (int j = 0; j < nrea[i]; j++) {
                mrea[i][j] = mreal[i][j];
            }
        }
        milista3.add(0, mrea);
    }

    /**
     * <p>
     * Returns the value in the element number "columna" of the binary population number "fila"
     * </p>
     * @param fila int The binary population
     * @param columna int The element in the binary population
     * @return char The value
     */
    char GetValueBinary(int fila, int columna) {
        return mbinary[fila][columna];
    }

    /**
     * <p>
     * Returns the value in the element number "columna" of the integer population number "fila"
     * </p>
     * @param fila int The integer population
     * @param columna int The element in the integer population
     * @return int The value
     */
    int GetValueInteger(int fila, int columna) {
        return minteger[fila][columna];
    }

    /**
     * <p>
     * Returns the value in the element number "columna" of the real population number "fila"
     * </p>
     * @param fila int The real population
     * @param columna int The element in the real population
     * @return double The value
     */
    double GetValueReal(int fila, int columna) {
        return mreal[fila][columna];
    }

}

