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
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.SaturationFilter;

/**
 * <p>
 * This class implements a set of integers and its basic operations
 * </p>
 */
public class IntegerSet {
	
	private int[] elements;
	private int numElements;
	
	final private int size;
	final private int MAXSIZE = 100;
	
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor without parameters 
	 * </p>
	 */	
	public IntegerSet(){
		
		elements = new int[MAXSIZE];
		numElements = 0;
		size = MAXSIZE;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * Constructor with initial size 
	 * </p>
	 */	
	public IntegerSet(int _maxsize){
		
		elements = new int[_maxsize];
		numElements = 0;
		size = _maxsize;
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It adds a value to the set if it is not present
	 * @param elem the new value to add 
	 * </p>
	 */	
	public void addValue(int elem){
		
		boolean presence = false;
		// ver si esta el elemento en el conjunto
		for(int i = 0 ; i < numElements && !presence ; ++i)
			if(elements[i] == elem)
				presence = true;
		
		if(!presence){
		
			if(numElements == size){
				int[] aux = new int[size];
				System.arraycopy(elements, 0, aux, 0, numElements);
				elements = new int[2*size];
				System.arraycopy(aux, 0, elements, 0, numElements);
			}
		
			elements[numElements++] = elem;
		}
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It removes a value of the set if it is present
	 * @param elem the vale to remove 
	 * </p>
	 */	
	public void removeValue(int elem){
		
		for(int i = 0 ; i < numElements ; ++i){
			if(elements[i] == elem){
				elements[i] = elements[numElements-1]; // meto el ultimo valido
				numElements--;
				return;
			}
		}
		
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns the size of the set
	 * @return the size of the set 
	 * </p>
	 */	
	public int size(){
		return numElements;
	}

//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns the element at position i
	 * @param i the position
	 * @return the value of the element 
	 * </p>
	 */	
	public int getElement(int i){
		return elements[i];
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It prints the set
	 * </p>
	 */
	public void print(){
		
		System.out.println("\n\n-------------------------------------");
		System.out.println("Number of elements = " + numElements);
		for(int i = 0 ; i < numElements ; ++i)
			System.out.print(elements[i] + ", ");		
		System.out.println();
		System.out.println("\n\n-------------------------------------");
	}
	
//*******************************************************************************************************************************
	
	/**
	 * <p>
	 * It returns true if the element e is present in the set
	 * @param e the value to find
	 * @return true if e is present,false otherwise
	 * </p>
	 */
	public boolean presence(int e){

		for(int i = 0 ; i < numElements ; ++i)
			if(elements[i] == e)
				return true;
		
		return false;
	}

}