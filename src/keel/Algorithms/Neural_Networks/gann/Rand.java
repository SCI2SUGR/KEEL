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

import org.core.Randomize;

/**
 * <p>
 * Class to obtain random values
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Rand {
	
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public Rand() {
  }

  /**
   * <p>
   * Method that returns a random double value between min and max values
   * </p>
   * @param min Minimum double value
   * @param max Maximum double value
   * @return random double between min and max values
   */
  public static double frandom( double min, double max) {
	  return Randomize.Randdouble(min, max);
  }

  /**
   * <p>
   * Method that returns a random integer value between min and max values
   * </p>
   * @param min Minimum double value
   * @param max Maximum double value
   * @return random int between min and max values
   */
  public static int irandom( double min, double max) {
	  return (int) Randomize.Randdouble(min, max);
  }

  /**
   * <p>
   * Generate a normal distributed value for N(m, s)
   * </p>
   * @param mean Mean of the normal distribution
   * @param sigma Standard deviation of the normal distribution
   * @return normal distribution value
   */
  public static double Normal(double mean, double sigma) {

    double fac, r, v1, v2, gasdev;

    do {
      v1 = 2 * frandom( 0, 1) - 1;
      v2 = 2 * frandom( 0, 1) - 1;
      r = v1 * v1 + v2 * v2;
    }
    while (r >= 1);
    fac = Math.sqrt( -2 * Math.log(r) / r);
    gasdev = v2 * fac;

    return ( (gasdev * sigma) + mean);
  }

}

