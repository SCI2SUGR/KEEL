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
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.SaturationFilter;


/**
 * <p>
 * This class implements the Quicksort algorithm. It lets sort an array of values from lowest to highest and
 * vice versa basing on an option
 * </p>
 */
public class Quicksort {
	
	// tags
	static public final int LOWEST_FIRST = 0;
	static public final int HIGHEST_FIRST = 1;
	static private int option;
	
	// this class is used to keep values and their original position
	static private class OrderStructure {
		int pos;
		double value;
		public OrderStructure(int i, double v){pos=i; value=v;}
	}
	
	
//******************************************************************************************************

	/**
	 * <p>
	 * Sorts the values array basing on opt
 	 * </p>
	 * @param vector double values array to sort
	 * @param size number of elements to sort
	 * @param opt equals to HIGHEST_FIRST to sort from highest to lowest, and equals to LOWEST_FIRST to
	 * sort from lowest to highest
	 * @return array of positions for each value (for example, the first value of the array represents the original
	 * position of the element in the original array to sort)
	 */	
	static public int[] sort(double[] vector, int size, int opt){
		
		int i;	// loop indexes
		option = opt;
		
		OrderStructure[] os = new OrderStructure[size];
		for(i = 0 ; i < size ; ++i)
			os[i] = new OrderStructure(i,vector[i]);
		
		sortValues(os,0,size-1);
		
		int[] positions = new int[size];
		for(i = 0 ; i < size ; ++i)
			positions[i] = os[i].pos;
		
		return positions;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * Sorts the values between begin and end indexes
 	 * </p>
	 * @param values values to sort with their original positions
	 * @param begin start position to sort
	 * @param end end position to sort
	 */	
	static private void sortValues(OrderStructure[] values, int begin, int end){
			
		double pivot;
		OrderStructure temp;
		int i, j;

		i = begin;
		j = end;
		pivot = values[(i+j)/2].value;
			
		do {
			
			if(option == HIGHEST_FIRST){
				while(values[i].value > pivot) i++;
				while(values[j].value < pivot) j--;
			}
			
			else if(option == LOWEST_FIRST){
				while(values[i].value < pivot) i++;
				while(values[j].value > pivot) j--;
			}
			
			if(i <= j){
				if(i < j){
					temp = values[i];
					values[i] = values[j];
					values[j] = temp;
				}
					
				i++;
				j--;
			}
			
		}while(i <= j);
		
		if(begin < j) sortValues(values,begin,j);
		if(i < end) sortValues(values,i,end);
	}

}