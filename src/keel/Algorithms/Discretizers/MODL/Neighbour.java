/**
 * <p>
 * @author Written by Julián Luengo Martín 07/05/2008
 * @version 0.
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Discretizers.MODL;

import java.util.ArrayList;

/**
 * This class represents an operation of the post-optimization of the MODL discretization algorithm
 *
 */
public class Neighbour implements Comparable {
	public double cost = 0; //cost of the operation
	public int type = -1; //type: Split, MergeSplit or MergeMergeSplit
	int index= -1; //index of the split
	ArrayList<Double> interval = null; //reference to the first interval of the operation
	int intervalPosition = -1; //position of the interval in the list of all intervals
	
	final static int Split = 1;
	final static int MergeSplit = 2;
	final static int MergeMergeSplit = 3;
	
	public int compareTo(Object o){
		Neighbour n = (Neighbour) o;
		
		if(this.cost < n.cost)
			return -1;
		if(this.cost > n.cost)
			return 1;
		return 0;
	}
}
