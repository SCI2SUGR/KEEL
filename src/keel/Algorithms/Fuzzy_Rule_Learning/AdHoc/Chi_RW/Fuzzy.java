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

package keel.Algorithms.Fuzzy_Rule_Learning.AdHoc.Chi_RW;

/**
 * <p>Title: Fuzzy</p>
 *
 * <p>Description: This class contains the representation of a fuzzy value</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Alberto Fernández (University of Granada) 16/10/2007
 * @version 1.0
 * @since JDK1.5
 */
public class Fuzzy {
  double x0, x1, x3, y;
  String name;
  int label;

  /**
   * Default constructor
   */
  public Fuzzy() {
  }

  /**
   * If fuzzyfies a crisp value
   * @param X double The crips value
   * @return double the degree of membership
   */
  public double Fuzzify(double X) {
    if ( (X <= x0) || (X >= x3)) /* If X is not in the range of D, the */
        {
      return (0.0); /* membership degree is 0 */
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
   * It makes a copy for the object
   * @return Fuzzy a copy for the object
   */
  public Fuzzy clone(){
    Fuzzy d = new Fuzzy();
    d.x0 = this.x0;
    d.x1 = this.x1;
    d.x3 = this.x3;
    d.y = this.y;
    d.name = this.name;
    d.label = this.label;
    return d;
  }

}

