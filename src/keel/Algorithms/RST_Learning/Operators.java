/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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


package keel.Algorithms.RST_Learning;

/**
 * 
 * File: Operators.java
 * 
 * Auxiliar class with several fuzzy operators
 * 
 * @author Written by Joaquín Derrac (University of Granada) 20/04/2010 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public class Operators{
	
	/**
	 * Lukasiewicz T-Norm
	 * 
	 * @param a First value
	 * @param b Second value
	 * 
	 * @return Result of the T-Norm
	 */
	public static double TNormLukasiewicz(double a, double b){

		if(a==0){
			return 0;
		}
		
		if(b==0){
			return 0;
		}
		
		return Math.max(a+b-1,0);
		
	}
	
    /**
     * Returns the smaller of two {@code double} values.
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the smaller of {@code a} and {@code b}.
     */
    public static double TNormMin(double a, double b){
		
		return Math.min(a,b);
		
	}
	
    /**
     * Returns the product of two {@code double} values.
     * @param   a   an argument.
     * @param   b   another argument.
     * @return  the product of two {@code double} values.
     */
    public static double TNormProd(double a, double b){
		
		if(a==0){
			return 0;
		}
		
		if(b==0){
			return 0;
		}
		
		return (a*b);
		
	}
	/**
	 * Lukasiewicz implicator
	 * 
	 * @param a First value
	 * @param b Second value
	 * 
	 * @return Result of the implicator
	 */
	public static double ImplicatorLukasiewicz(double a, double b){
		
		return Math.min(1, 1 - a + b);
	}

	

}//end-class
