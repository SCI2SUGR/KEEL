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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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
 * File: Util.java
 * 
 * Auxiliar class with useful function for working with fuzzy instance learning based methods
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2012 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Fuzzy_Instance_Based_Learning;

public class Util{
	
	/** 
	 * Computes the Euclidean distance between two instances (L2 norm)
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	public static double euclideanDistance(double instance1[],double instance2[]){
		
		double length=0.0;

		for (int i=0; i<instance1.length; i++) {
			
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
			
		}
		
		
		length = Math.sqrt(length); 
				
		return length;
		
	} //end-method
	
	/** 
	 * Computes the unsquared Euclidean distance between two instances (L1 norm)
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The unsquared Euclidean distance
	 * 
	 */
	public static double euclideanDistanceNS(double instance1[],double instance2[]){
		
		double length=0.0;

		for (int i=0; i<instance1.length; i++) {
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
		}
				
		return length;
		
	} //end-method
	
	/** 
	 * Checks if two instances are the same
	 * 
	 * @param a First instance 
	 * @param b Second instance
	 * @return True if both instances are equal.
	 * 
	 */
	public static boolean same(double a[],double b[]){

		for(int i=0;i<a.length;i++){
			if(a[i]!=b[i]){
				return false;
			}
		}
		return true;
		
	}//end-method
	
	/** 
	 * Generates a string with the contents of the instance
	 * 
	 * @param a Instance to print. 
	 * 
	 * @return A string, with the values of the instance
	 * 
	 */	
	public static void printInstance(double instance[]){
		
		String exit="";
		
		for(int i=0;i<instance.length;i++){
			exit+=instance[i]+" ";
		}
		
		System.out.println(exit);
		
	}//end-method
	
	
	/** 
	 * Generates a string with the contents of a data matrix
	 * 
	 * @param a Data matrix to print. 
	 * 
	 * @return A string, with the values of the data matrix
	 * 
	 */	
	public static void printMatrix(double data[][]){
		
		String exit="";
		String cad;
		
		for(int i=0;i<data.length;i++){
			cad="";
			for(int j=0;j<data[0].length;j++){
				cad+=data[i][j]+"\t";
			}
			exit+=cad+"\n";
		}
		
		System.out.println(exit);
		
	}//end-method
	
	/**
	 * Prints a text in the standard output
	 * 
	 * @param text Text to print
	 */
	public static void print(String text){
		
		System.out.println(text);
		
	}//end-method

}//end-class
