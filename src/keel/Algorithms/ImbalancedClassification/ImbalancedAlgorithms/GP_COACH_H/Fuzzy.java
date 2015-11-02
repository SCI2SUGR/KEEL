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

package keel.Algorithms.ImbalancedClassification.ImbalancedAlgorithms.GP_COACH_H;

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
 * @author Modified by Victoria Lopez (University of Granada) 27/01/2011
 * @version 1.0
 * @since JDK1.5
 */
public class Fuzzy implements Comparable {
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
   * 
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
   * Modifies the current fuzzy value according to a provided lateral displacement
   * 
   * @param displacement	Real value that contains the lateral displacement that needs to be applied to the fuzzy label
   */
  public void lateralDisplace (double displacement) {
	double displacement_in_interval, displacement_fuzzy;
	double int_low, int_high, int_all, int_displacement;
	
  	if ((displacement >= CHC_Chromosome.MAX_LATERAL_TUNING) || (displacement < CHC_Chromosome.MIN_LATERAL_TUNING)) {
  		System.err.println("The generated displacement for the membership function is not in the correct range [" + CHC_Chromosome.MIN_LATERAL_TUNING + ", " + CHC_Chromosome.MAX_LATERAL_TUNING + ")");
  		System.exit(-1);
  	}
  	
  	// Modify the fuzzy values of the membership function
  	int_low = 2.0*(x1 - x0);
  	int_high = 2.0*(x3 - x1);
  	int_all = x3 - x0;
  	
  	if (((int_low - int_high) > 0.00000001) || ((int_low - int_all) > 0.00000001)) {
  		System.err.println("The original fuzzy label is not well formed: " + x0 + " " + x1 + " " + x3 + " so we cannot lateral displace it");
  		System.exit(-1);
  	}
  	
  	int_displacement = CHC_Chromosome.MAX_LATERAL_TUNING - CHC_Chromosome.MIN_LATERAL_TUNING;
  	displacement_in_interval = ((double)(displacement-CHC_Chromosome.MIN_LATERAL_TUNING)/(double)int_displacement)-0.5;
  	displacement_fuzzy = (x3-x1) * displacement_in_interval;
  	
  	x0 += displacement_fuzzy;
  	x1 += displacement_fuzzy;
  	x3 += displacement_fuzzy;
  } 

  /**
   * It makes a copy for the object
   * 
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
  
  /**
   * Compares this object with the specified object for order, according to the number of label measure 
   * 
     * @param a Objecto to compare with.
   * @return a negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object
   */
  public int compareTo(Object a) {
      if (((Fuzzy) a).label > this.label) {
          return -1;
      }
      if (((Fuzzy) a).label < this.label) {
          return 1;
      }
      return 0;
  }
  
  /**
   * Compares this fuzzy label with another fuzzy label to check if both fuzzy labels are equal
   * 
   * @param o	fuzzy label that is going to be compared with the current fuzzy label
   * @return true, if the fuzzy labels are the same; false, otherwise
   */
  public boolean equals (Object o) {
	  if (o == null)
		  return false;
	  if (o == this)
		  return true;
	  if (!(o instanceof Fuzzy))
		  return false;
	  
	  Fuzzy f = (Fuzzy) o;
	  
	  if (x0 != f.x0)
		  return false;
	  
	  if (x1 != f.x1)
		  return false;
	  
	  if (x3 != f.x3)
		  return false;
	  
	  if (y != f.y)
		  return false;
	  
	  if (label != f.label)
		  return false;
	  
	  if (!name.equals(f.name))
		  return false;
	  
	  return true;
  }
  
  /**
   * Computes the hash code associated to the current fuzzy label
   * 
   * @return the hash code associated to the current fuzzy label
   */
  public int hashCode() {
	  int result = 17;

	  result = 31 * result + label;
	  result = 31 * result + (int) (Double.doubleToLongBits(x0)^((Double.doubleToLongBits(x0) >>> 32)));
	  result = 31 * result + (int) (Double.doubleToLongBits(x1)^((Double.doubleToLongBits(x1) >>> 32)));
	  result = 31 * result + (int) (Double.doubleToLongBits(x3)^((Double.doubleToLongBits(x3) >>> 32)));
	  result = 31 * result + (int) (Double.doubleToLongBits(y)^((Double.doubleToLongBits(y) >>> 32)));
	  result = 31 * result + name.hashCode();
	  
	  return result;
  }
}
