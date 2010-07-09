/**
 * <p>
 * @author Written by Julián Luengo Martín 07/05/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Discretizers.MODL;

import java.util.ArrayList;

/**
 * This class represents the cost variation associated with merging two adjacent intervals
 *
 */
public class DeltaValue implements Comparable{
	//pointer list
	public DeltaValue prev = null; //item for the previous bound of intervals
	public DeltaValue next = null; //pointer for the next bound of intervals
	public ArrayList<Double> leftInterval = null; //the left interval in our boundary
	public ArrayList<Double> rightInterval = null; //the right interval in our boundary
	//cost variation
	public double delta = 0; //the cost derived from merging the two intervals (erase the boundary)
	//index
	public int index= -1; //index of the first element in the LEFT interval in the global sorted real values 
	
	/**
	 * Method from interface Comparable, so this object can be sorted in Java lists
	 * @param o Object to be compared to
	 */
	public int compareTo(Object o){
		DeltaValue d = (DeltaValue) o;
		if(this.delta < d.delta)
			return -1;
		if(this.delta > d.delta)
			return 1;
		return 0;
	}

}
