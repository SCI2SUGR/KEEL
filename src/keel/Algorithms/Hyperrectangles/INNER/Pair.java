/**
 *
 * File: Pair.java
 *
 * Auxiliary class to repressent pairs of rules and its distance
 *
 * @author Written by Joaquin Derrac (University of Granada) 17/10/2009
 * @version 1.1
 * @since JDK1.5
 *
 */
package keel.Algorithms.Hyperrectangles.INNER;

public class Pair implements Comparable{
	
	private int ruleA;       //first rule
	private int ruleB;       //second rule
	private double distance; //distance between them
	
	/**
	 * Builder.
	 *
	 * @param a Identifier of the first rule
	 * @param b Identifier of the second rule
	 * @param dist Distance between rules
	 */
	public Pair (int a, int b, double dist){
		
		ruleA=a;
		ruleB=b;
		distance=dist;
	}//end-method
	
	/**
	* Returns the distance between rules
	*
	* @return Distance
	*/
	public double dist(){
		return distance;
	}//end-method
	
	/**
	* Returns the first rule of the pair
	*
	* @return Identifier of the first rule
	*/	
	public int A(){
		return ruleA;
	}//end-method
	
	/**
	* Returns the second rule of the pair
	*
	* @return Identifier of the second rule
	*/	
	public int B(){
		return ruleB;
	}//end-method	
	
	/**
	* Compare to method: Compare two pairs of rules regarding its distance 
	*
	* @return Order of the two pairs
	*/	
	public int compareTo(Object o) {
        Pair dir = (Pair)o;
        if(this.distance < dir.distance)
            return -1;
        else if(this.distance == dir.distance)
            return 0;
        else
            return 1;
	}//end-method
	
}//end-class
