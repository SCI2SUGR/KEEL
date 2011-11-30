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

package keel.Algorithms.Associative_Classification.ClassifierFuzzyFCRA;

/**
 * This class contains the representation to select rules
 *
 * @author Written by Jesus Alcala (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Selected implements Comparable{
  double probability;
  int post;

  /**
   * <p>
   * Parameters Constructor
   * </p>
   * @param prob double Probability
   * @param pos int Position
   */
  public Selected(double prob, int pos) {
    this.probability = prob;
    this.post = pos;
  }

  /**
   * Function to compare objects of the Selected class
   * Necessary to be able to use "sort" function
   * It sorts in an increasing order of probability
   */
  public int compareTo(Object a) {
    if ( ( (Selected) a).probability < this.probability) {
      return -1;
    }
    if ( ( (Selected) a).probability > this.probability) {
      return 1;
    }
    return 0;
  }

}

