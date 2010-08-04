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
 * File: Randomize.java
 *
 * Random generator
 *
 * @author Written by Admin 4/8/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.util.*;

class Randomize {

    private static long Seed;
    private static Random generador = new Random();

    /**
     * Sets the seed of the random generator with a specific value
     *
     * @param seed New seed
     */
    public static void setSeed(long seed) {
        Seed = seed;
        generador.setSeed(Seed);
    }

    /**
     * Rand computes a psuedo-random float value between 0 and 1, excluding 1
     * @return a value between 0 and 1, excluding 1
     */
    public static double Rand() {
        return (generador.nextDouble());
    }

    /**
     * Randint gives an integer value between low and high inclusive
     * @param low lower bound
     * @param high higher bound
     * @return a value between low and high inclusive
     */
    public static int Randint(int low, int high) {
        return ((int) (low + (high - low + 1) * Rand()));
    }

    /**
     * Randfloat gives a float value between low and high, including low and excluding high
     * @param low lower bound
     * @param high higher bound
     * @return a float value between low and high, including low and excluding high
     */
    public static double Randdouble(double low, double high) {
        return (low + (high - low) * Rand());
    }
}
