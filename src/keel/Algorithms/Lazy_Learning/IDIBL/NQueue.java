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
 * File: NQueue.java
 * 
 * A class modelling a queue of neighbors.It sorts its elements 
 * automatically, satrting from the nearest neighbor. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 16/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.IDIBL;

public class NQueue {

	private static int MAX_SIZE; 
	private Neighbour queue [];
	private int size;
	
	/** 
	 * Buider.Builts a empty queue.
	 * 
	 */
	public NQueue() {
		
		queue=new Neighbour[MAX_SIZE];
		
		for(int i=0;i<MAX_SIZE;i++){
			queue[i]=new Neighbour(-1,Double.MAX_VALUE,-1);
		}
			
		size=0;
		
	}//end-method
	
	/** 
	 * Sets maximun size of the queue
	 * 
	 * @param max_size Maximun size
	 */
	public static void setMAX_SIZE(int max_size) {
		
		MAX_SIZE = max_size;
		
	}//end-method
	
	/** 
	 * Get a neighbor
	 * 
	 * @param position Position of the neighbor
	 * @return The neighbor
	 * 
	 */
	public Neighbour get(int position){
		
		if(position<size){
			return queue[position];
		}
		else{
			return null;
		}
	}//end-method
	
	/** 
	 * Insert a new neighbor
	 * 
	 * @param item Neighbor to be inserted
	 * 
	 */
	public void insert(Neighbour item){
		
		int position;

		position=getPosition(item.getDistance());

		if(position !=-1){

			if(size<MAX_SIZE){
				size++;
			}
			
			for(int i=size-1;i>position;i--){
				
				queue[i]=queue[i-1];
				
			}
			
			if(position<MAX_SIZE){
				queue[position]=item;
			}
		}		
	}//end-method
	
	/** 
	 * Get insert position for a new neighbor
	 * 
	 * @param value Distance of the neighbor
	 * @return Insert position selected
	 * 
	 */
	private int getPosition(double value){
		
		int position;
		int min;
		int max;
		double other;
		
		if(size>2){
			min=0;
			max=size-1;
			
			position=(min+max)/2;
			
			while((max-min)>1){
			
				other=queue[position].getDistance();
				
				if(other>value){
					
					max=position;
					
				}else{
					min=position;
				}
				
				position=(max+min)/2;
			}
			
			if(queue[min].getDistance()>value){
				return min;
			}
			else{
				return max;
			}	
		}
		else{
			
			position=0;
			
			for(int i=0;i<size;i++){
				if(queue[position].getDistance()<value){
					position++;
				}
			}
			
			return position;
		}
		
	}//end-method
	
	/** 
	 * Get actual size of the queue
	 * 
	 * @return Size
	 * 
	 */
	public int getSize(){
		
		return size;
		
	}//end-method
	
}//end-class

