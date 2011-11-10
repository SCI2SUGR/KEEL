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
 * File: MultiplePair.java
 * 
 * This class defines a comparable pair of two double values.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical.tests;

public class MultiplePair implements Comparable {

  public double indice; //first element
  public double valor;  //second element
  
  /**
  * Default builder
  */
  public MultiplePair() {

  }//end-method

  /**
  * Builder
  *
  * @param i First double
  * @param v Second double
  */
  public MultiplePair(double i, double v) {
    indice = i;
    valor = v;
  }//end-method

  /**
  * CompareTo method
  *
  * @param o1 pair
  * @return A integer representing the order
  */
  public int compareTo (Object o1) { //sort by absolute value

    if (Math.abs(this.valor) > Math.abs(((MultiplePair)o1).valor))
      return -1;
    else if (Math.abs(this.valor) < Math.abs(((MultiplePair)o1).valor))
      return 1;
    else return 0;
  }//end-method


}//end-class
