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
 * 
 * File: Itemset.java
 * 
 * A class modelling a Itemset for Deeps algorithm
 * It provides basic functionality such insert and drop
 * elements, test if is a subset or a superset of any other
 * itemset, and more.  
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Lazy_Learning.Deeps;

class Itemset {


	private int items [];
	private int size;	
	
	//maximun size allowed
	private static int maxItems=1;
	
	/** 
	 * Sets the maximun size allowed for every Itemset
	 * 
	 * @param value The maximun size  
	 * 
	 */
	public static void setMaxItems(int value){
		
		maxItems=value;
		
	}//end-method 
	
	/** 
	 * Default buider.Builts and empty Itemset. 
	 * 
	 */	
	public Itemset(){
		
		items= new int [maxItems];
		size=0;
		
	}//end-method 
	
	/** 
	 * Buider.Builts an itemset, using a binary vector which
	 * represent what items are going to be present in the Itemset 
	 * 
	 * @param inItems Binary items vector  
	 * 
	 */
	public Itemset(int inItems[]){
		
		items= new int [maxItems];
		
		for(int i=0;i<inItems.length;i++){	
			if(inItems[i]==1){
				this.add(i);
			}
		}
		
	}//end-method 
	
	/** 
	 * Test if the Itemset contains a given value
	 * 
	 * @param value Value to test pertenency  
	 * @return The position of the value. -1 if value is not contained
	 * 
	 */	
	public int contains(int value){
		
		for(int i=0;i<size;i++){	
			if(items[i]==value){
				return i;
			}
		}	
		return -1;
		
	}//end-method 

	/** 
	 * Adds a value to the Itemset, if it is not full.
	 * Does not allow the presence of duplicates.
	 * 
	 * @param value Value to be added 
	 * 
	 */
	public void add(int value){
			
		if(this.contains(value)==-1){

			items[size]=value;
			size++;
		}
		
	}//end-method 

	/** 
	 * Drops a value from the Itemset, if it is in.
	 * 
	 * @param value Value to be dropped 
	 * 
	 */
	public void drop(int value){
		
		int position=this.contains(value);
		
		if(position>-1){
			for(int i=position;i<size;i++){	
				items[i]=items[i+1];
			}	
			size--;
		}
		
	}//end-method 

	/** 
	 * Returns an item from the Itemset.
	 * 
	 * @param position Position of the item
	 * @return The item
	 * 
	 */
	public int get(int position){
		
		return items[position];
		
	}//end-method  

	/** 
	 * Returns the size of the Itemset.
	 * 
	 * @return Size of the Itemset
	 * 
	 */
	public int getSize(){
		
		return size;
		
	}//end-method 

	/** 
	 * Test if the Itemset is a subset of another Itemset
	 * 
	 * @param great Itemset to compare 
	 * @return True if the Itemset is a subset of 'great'. Else, returns false
	 * 
	 */
	public boolean isSubset(Itemset great){
		
		for(int i=0;i<size;i++){	
			if(great.contains(items[i])==-1){
				return false;
			}
		}	

		return true;
		
	}//end-method 

	/** 
	 * Test if the Itemset is a superset of another Itemset
	 * 
	 * @param little Itemset to compare 
	 * @return True if the Itemset is a superset of 'great'. Else, returns false
	 * 
	 */
	public boolean isSuperset(Itemset little){
		
		
		for(int i=0;i<little.getSize();i++){	
			if(this.contains(little.get(i))==-1){
				return false;
			}
		}	

		return true;
		
	}//end-method 

	/** 
	 * Get the Itemset diference as:
	 * Items(this)-Items(Other)
	 * 
	 * @param other Itemset to substract 
	 * @return A new Itemset containing the difference
	 * 
	 */
	public Itemset diference(Itemset other){
		
		Itemset result=new Itemset();
		
		for(int i=0;i<size;i++){	
			if(other.contains(items[i])==-1){
				result.add(items[i]);
			}
		}	

		return result;
		
	}//end-method 

	/** 
	 * Merge the Itemset with other:
	 * Items(this)+Items(Other)
	 * 
	 * @param other Itemset to add 
	 * @return A new Itemset containing the merge
	 * 
	 */
	public Itemset merge(Itemset other){
		
		Itemset result=new Itemset();

		for(int i=0;i<size;i++){	
			result.add(items[i]);
		}	

		for(int i=0;i<other.getSize();i++){	
			result.add(other.get(i));
		}
		return result;
		
	}//end-method 

	/** 
	 * Test if the Itemset is a superset of another Itemset,
	 * represented in binary form.
	 * 
	 * @param array Binary itemset to compare 
	 * @return True if the Itemset is a supersetset of 'array'. Else, returns false
	 * 
	 */
	public boolean isSubSetBinary(int array[]){
		
		for(int i=0;i<size;i++){	
			if(array[items[i]]==0){
				return false;
			}
		}	

		return true;
	
	}//end-method 

	/** 
	 * Prints the Itemset
	 * 
	 * @return A String representation of the Itemset
	 * 
	 */
	public String toString() {
		
		String chain="";
		
		chain+=" <";
		for(int i=0;i<size;i++){
			chain+=items[i]+" ";
		}
		chain+=">";
		
		return chain;
		
	}//end-method 

} //end-class

