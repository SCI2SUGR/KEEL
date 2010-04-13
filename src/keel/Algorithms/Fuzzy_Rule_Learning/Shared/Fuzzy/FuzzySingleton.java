/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;

public class FuzzySingleton extends Fuzzy {
	/** 
	* <p> 
	* Represents a singleton fuzzy set. 
	* A Fuzzy singleton is a fuzzy set whose support is a single point in U with a membership level of 1.0.
	* 
	* </p>
	*/	
	//it's the single point.
    double center;
    /** 
     * <p> 
     * A constructor for a singleton fuzzy set, given the point. 
     * 
     * </p> 
     * @param c the single point of the fuzzy set.
     */
    public FuzzySingleton(double c) {
        center=c;
    }
    /** 
     * <p> 
     * A copy constructor for a singleton fuzzy set, given other singleton fuzzy set. 
     * 
     * </p> 
     * @param b to be copied.    
     */
    public FuzzySingleton(FuzzySingleton b) {
        center=b.center;
    }
    /** 
     * <p> 
     *  Indicates whether some other object is "equal to" this one.
     * 
     * </p>
     * @param B the reference object with which to compare. 
     * @return true if this object is the same as the B argument; false otherwise. 
     */     
    public boolean equals(Fuzzy b) {
        if (!(b instanceof FuzzySingleton)) return false;
        FuzzySingleton bt=(FuzzySingleton) b;
        if (center!=bt.center) return false;
        return true;
    }
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */    
    public Fuzzy clone() {
        return new FuzzySingleton(this);
    }
    /** 
     * <p> 
     * Copies the FuzzySingleton parameter over the present instance. 
     * 
     * </p> 
     * @param b a FuzzySingleton object to be copied
     */
    public void set(FuzzySingleton b) {
        center=b.center;
    }
    /** 
     * <p> 
     *  Returns the membership level for the individual x.
     *  
     * 
     * </p>
     * @param x the individual which membership is to be calculated. 
     * @return the membership level for individual x. 
     */ 
    public double evaluateMembership(double x) {
        if (x==center) return 1;
        return 0;
    }
    /** 
     * <p> 
     *  Returns the centroid of the present fuzzy number. 	
     *  
     * </p>
     * @return the centroid of the present fuzzy number. 
     */  
    public double massCentre() {
        return center;
    }
    /** 
     * <p> 
     *  Creates and returns a FuzzyInterval with unique point of the support set.
     *  
     * </p> 
     * @return an interval with the unique point of the support set. 
     */ 
    public FuzzyInterval support() {
        return new FuzzyInterval(center,center);
    }
    /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of the singleton fuzzy set. 
     */	
    public String aString() {
        return "SINGLE("+center+")";
    }

}
