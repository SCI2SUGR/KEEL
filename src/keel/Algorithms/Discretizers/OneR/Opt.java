/**
 * <p>
 * @author Written by Julián Luengo Martín 28/10/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Discretizers.OneR;

import java.util.Arrays;

/**
 * <p>
 * This class represents the optimum class for a given explanatory value
 * </p>
 */
public class Opt {
	double _value;
	int count[];
	int prior[];
	boolean clean;
	int max;
	
	public Opt(){
		_value = Double.NaN;
		count = null;
		clean = false;
		max = -1;
	}
	
	/**
	 * Creates a new object with the given elements
	 * @param value the explanatory value
	 * @param numClases the total number of different classes
	 */
	public Opt(double value,int numClasses){
		_value = value;
		count = new int[numClasses];
		clean = false;
		prior = new int[numClasses];
	}
	
	/**
	 * Sets the priority of the classes associated to this explanatory value (the less, the more priority)
	 * @param p array with the priority associated to each class
	 */
	public void setPrior(int p[]){
		prior = Arrays.copyOf(p, p.length);
		
	}
	
	/**
	 * Increases the count for the class indicated
	 * @param index the class for which the count will be incremented by one
	 */
	public void countClass(int index){
		count[index]++;
		clean = false;
	}

	/**
	 * Computes the optimum class for the explanatory value
	 * @return the optimum class found
	 */
	public int getOptClass(){
		if(!clean){
			max = 0;
			for(int i=1;i<count.length;i++){
				if(count[i]>max)
					max = i;
				else if(prior[i]<prior[max])
					max = i;

			}
			clean = true;
		}
		return max;
	}
	
	/**
	 * Gets the explanatory value associated
	 * @return the explanatory value associated
	 */
	public double getValue(){
		return _value;
	}
	
}
