/**
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.OIGA;

/**
 * <p>
 * This class implements the relation between an attribute and its classification rate
 * for sorting purposes
 * </p>
 */
public class AttributeCR implements Comparable{
	
	public int attribute;
	public double CR;
	
	/**
	 * <p>
	 * Assigns the attribute number and the CR to this object
	 * </p>
	 * @param att the reference attribute
	 * @param CR the CR to be asigned
	 */
	public AttributeCR(int att,double CR){
		attribute = att;
		this.CR = CR;
	}
	
	/**
	 * Implementation of the method compareTo for sorting (by CR)
	 */
	public int compareTo(Object o){
		AttributeCR acr = (AttributeCR) o;
		
		if(this.CR < acr.CR)
			return -1;
		if(this.CR > acr.CR)
			return 1;
		return 0;
	}

}
