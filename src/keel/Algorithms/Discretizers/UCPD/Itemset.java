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

package keel.Algorithms.Discretizers.UCPD;


/**
 * <p>
 * This class lets to manipulate itemsets
 * </p>
 * 
 * @author Written by Jose A. Saez (University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 */
public class Itemset {	
	
	private int[] items;	// values of each item
	private int numAtt;		// number of attributes
	private int last;		// index of last valid item
	private double support;	// support of the itemset
	
	
//******************************************************************************************************

	/**
	 * <p>
	 * Constructor for creating a Itemset object
	 * </p>
	 * @param values array of values for the Itemset
	 * @param tam number of attributes
	 * @param lastp last index of a valid item
	 */	
	public Itemset(int[] values, int tam, int lastp){
		
		numAtt=tam;
		last = lastp;
		
		items = new int[numAtt];
		for(int i = 0 ; i < numAtt ; ++i)
			items[i] = values[i];
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It combines two itemsets to get a new itemset based on the creation rules of A priori algorithm
	 * </p>
	 * @param itemset2 the other itemset
	 * @return the combined itemset of this and itemset2
	 */	
	public Itemset combine(Itemset itemset2){
		
		// see if first part is equal (except the last element)
		boolean equal = true;
		for(int i = 0 ; i < last; ++i)
			if(items[i] != itemset2.items[i])
				equal = false;
		
		if(equal == false)
			return null;
		
		// copy the first part
		int[] newItemset = new int[items.length];
		for(int i = 0 ; i < items.length ; ++i)
			newItemset[i] = -1;
		int ult = 0;

		for(int i = 0 ; i < last; ++i)
			newItemset[i] = items[i];
		
		// add the two final new elements if they are of different attribute
		if(last != itemset2.last){
			newItemset[last] = items[last];
			newItemset[itemset2.last] = itemset2.items[itemset2.last];
			ult = last > itemset2.last? last : itemset2.last;
		}
		
		else{
			return null;
		}
		
		
		return new Itemset(newItemset, items.length, ult);
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It checks if the example given follows the patron of the itemset
	 * </p>
	 * @param example an instance
	 * @return true if the example follows the patron of the itemset, otherwise false
	 */
	public boolean into(int[] example){
		
		boolean res = true;
		
		for(int i = 0 ; i < items.length && res ; ++i)
			if(items[i] != -1 && example[i] != items[i])
				res = false;
		
		return res;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It checks if the itemset given is equals to this itemset
	 * </p>
	 * @param itemset2 second itemset
	 * @return true if both are equal, otherwise false
	 */
	public boolean equalsTo(Itemset itemset2){
		
		for(int i = 0 ; i < items.length ; ++i)
			if(items[i] != itemset2.items[i])
				return false;
		
		return true;
	}

//******************************************************************************************************

	/**
	 * <p>
	 * It returns the items array of this itemset
	 * </p>
	 * @return the items array
	 */
	public int[] getItems(){
		
		return items;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It returns the size (number of different attributes) of this itemset
	 * </p>
	 * @return the size of this itemset
	 */
	public int size(){
		
		return numAtt;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It returns the support of this itemset
	 * </p>
	 * @return the support value
	 */
	public double getSupport(){
		
		return support;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It sets the support of this itemset
	 * </p>
	 * @value sup the new support
	 */
	public void setSupport(double sup){
		
		support = sup;
	}
	
//******************************************************************************************************

	/**
	 * <p>
	 * It prints the itemset
	 * </p>
	 */
	public void print(){
		
		System.out.print("\n\nITEM (" + last +") = ");
		for(int i = 0 ; i < items.length ; ++i)
			if(items[i] != -1)
				System.out.print("Attribute[" + i + "] = " + items[i] + " , ");
	}
	
}