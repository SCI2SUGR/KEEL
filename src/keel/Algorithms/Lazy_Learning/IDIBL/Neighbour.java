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
 * File: Neighbour.java
 * 
 * A class modelling a neighbor. It keeps the distance calculated,
 * and their indexes to train and reference sets. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 16/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.IDIBL;

class Neighbour {
	
	int instance; //index in reference set
	double distance;
	int trainInstance; //index in train set
	
	public Neighbour(int instance,double distance,int trainInstance) {

		this.distance = distance;
		this.instance = instance;
		this.trainInstance = trainInstance;
		
	}//end-method

	/** 
	 * Get reference index.
	 * 
	 * @return Index
	 */
	public int getInstance() {
		
		return instance;
		
	}//end-method
	
	/** 
	 * Get train index.
	 * 
	 * @return Index
	 * 
	 */
	public int getTrainInstance() {
		
		return trainInstance;
		
	}//end-method
	
	/** 
	 * Get distance.
	 * 
	 * @return Distance
	 * 
	 */
	public double getDistance() {
		
		return distance;
		
	}//end-method
	
	/** 
	 * Set instance.
	 * 
	 * @param instance Index to reference set.
	 * 
	 */
	public void setInstance(int instance) {
		
		this.instance = instance;
		
	}//end-method

	/** 
	 * Set instance.
	 * 
	 * @param trainInstance Index to train set.
	 * 
	 */
	public void setTrainInstance(int trainInstance) {
		
		this.trainInstance = trainInstance;
		
	}//end-method
	
	/** 
	 * Set distance.
	 * 
	 * @param distance Distance to set.
	 * 
	 */
	
	public void setDistance(double distance) {
		
		this.distance = distance;
		
	}//end-method

}

