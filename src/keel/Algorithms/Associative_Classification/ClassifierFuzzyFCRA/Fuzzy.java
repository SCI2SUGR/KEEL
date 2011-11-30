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
 * This class contains the representation of a fuzzy value
 *
 * @author Jesus Alcalá (University of Granada) 09/02/2010
 * @version 1.0
 * @since JDK1.5
 */
public class Fuzzy {
  double x0, x1, x3, y;
  String name;

  public Fuzzy() {
  }


  /**
   * If fuzzyfies a crisp value
   * @param X double The crips value
   * @return double the degree of membership
   */
  public double Fuzzifica(double X) {
    if (X == x1) { 
      return (1.0); 
    }

	if ( (X <= x0) || (X >= x3)) { 
      return (0.0); 
    }

    if (X < x1) {
      return ( (X - x0) * (y / (x1 - x0)));
    }

    if (X > x1) {
      return ( (x3 - X) * (y / (x3 - x1)));
    }

    return (y);

  }

  /**
   * <p>
   * Clone Function
   * </p>
   */
  public Fuzzy clone(){
    Fuzzy d = new Fuzzy();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x3 = this.x3;
    d.y = this.y;
    d.name = this.name;
    return d;
  }

  /**
   * <p>
   * It returns the name of the fuzzy set
   * </p>
   * @return String The name of the fuzzy set
   */
  public String getName(){
	  return (this.name);
  }
}

