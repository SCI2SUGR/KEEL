/**
 * <p>
 * @author Written by Julián Luengo Martín 18/03/2010
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Discretizers.Khiops;

import java.util.ArrayList;

/**
 * This class represents the cost variation associated with merging two adjacent intervals
 */
public class DeltaValue implements Comparable{
	//pointers list
	public DeltaValue prev = null; //item for the previous merge of intervals
	public DeltaValue next = null; //pointer for the next merge of intervals
	public ArrayList<Double> leftInterval = null; //the left interval in our boundary
	public ArrayList<Double> rightInterval = null; //the right interval in our boundary
	public Double leftChi2Row = null; //the chi2 row of the left interval in our boundary
	public Double rightChi2Row = null; //the chi2 row of the right interval in our boundary
	//cost variation
	public double delta = 0; //the cost derived from merging the two intervals (erase the boundary), that is, the Chi2 variation
	//any of the intervals implied in this merge does not meet the frequency constraints
	public boolean freqConstrMet = false;
	//index
	public int index= -1; //index of the first element in the LEFT interval in the global sorted real values 
	
	/**
	 * Method from interface Comparable, so this object can be sorted in Java lists
	 * @param o Object to be compared to
	 */
	public int compareTo(Object o){
		
		DeltaValue d = (DeltaValue) o;
		//check the frequency constraint
//		if(!this.freqConstrMet && !d.freqConstrMet)
//			return 0;
		if(!this.freqConstrMet && d.freqConstrMet)
			return 1;
		if(this.freqConstrMet && !d.freqConstrMet)
			return -1;	
		//normal sorting otherwise
		if(this.delta < d.delta)
			return -1;
		if(this.delta > d.delta)
			return 1;
		return 0;
	}

}
