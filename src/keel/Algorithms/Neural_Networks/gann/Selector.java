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

package keel.Algorithms.Neural_Networks.gann;

import java.util.Arrays;

/**
 * <p>
 * Class Selector. It represents several selection methods: ordered, roulette and tournament
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Selector {
	
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public Selector() {
  }

  /**
   * <p>
   * Ordered selection method
   * </p>
   * @param fitness Fitness array
   * @param k Individual index
   * @param n Number of individuals
   * @return Order obtained
   */
  public static int Ordered(double fitness[], int k, int n) {

    Ranking ord[];
    Compare c;

    ord = new Ranking[n];
    c = new Compare ();

    for (int i = 0; i < n; i++) {
      ord[i] = new Ranking ();
      ord[i].fitness = fitness[i];
      ord[i].order = i;
    }

    // Sort vector
    Arrays.sort (ord, c);

    return ord[k].order;
  }

  /**
   * <p>
   * Roulette selection method
   * </p>
   * @param fitness Fitness array
   * @param n Number of individuals
   * @param global Global Definition parameters
   * @return Selected individual
   */
  public static int Roulette(double fitness[], int n, SetupParameters global) {
    int being;
    double uniform, prob[];

    prob = new double[n];

    // Create cummulative probabilities vector
    prob[0] = fitness[0];
    for (int i = 1; i < n; i++) {
      prob[i] = prob[i - 1] + fitness[i];
    }

    /*
     * A invidual is selected using a random value.
     */
    uniform = Rand.frandom( 0, prob[n - 1]);
    being = 0;
    while (uniform > prob[being]) {
      being++;
    }
    return (being);
  }

  /**
   * <p>
   * Tournament selection method
   * </p>
   * @param fitness Fitness array
   * @param n_ops Number of individuals selected
   * @param n Number of individuals
   * @param global Global Definition parameters
   * @return Selected individual
   */
  public static int Tournament(double fitness[], int n_ops, int n,
                               SetupParameters global) {

    int i, best, beings[];

    beings = new int[n_ops];

    /*
     * N individuals are chosen.
     */
    for (i = 0; i < n_ops; i++) {
      beings[i] = Rand.irandom( 0, n);
    }

    /*
     * The most fitted is returned.
     */
    best = beings[0];
    for (i = 1; i < n_ops; i++) {
      if (fitness[beings[i]] > fitness[best]) {
        best = beings[i];

      }
    }
    return best;

  }
}

