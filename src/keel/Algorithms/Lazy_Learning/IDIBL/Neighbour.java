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
