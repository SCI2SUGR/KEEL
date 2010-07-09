/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 20/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;

public class FuzzyPartition  {
	/** 
	* <p> 
	* Represents a partition of fuzzy sets.  
	* 
	* </p> 
	*/
	//the container
    private Fuzzy[] content;
    /** 
     * <p> 
     * A constructor for a fuzzy partition of n fuzzy sets whose support is uniformely defined between min and max.
     * The first and last fuzzy set are a FuzzyNumberTRLEFT and FuzzyNumberTRRIGHT respectively while 
     * the middle fuzzy sets are triangular fuzzy sets.   
     * 
     * </p> 
     * @param min the lower extreme of the support set in the whole fuzzy partition.
     * @param max the upper extreme of the support set in the whole fuzzy partition.
     * @param n the number of fuzzy sets in the partition.
     */
    public FuzzyPartition(double min, double max, int n) {
        content=new Fuzzy[n];
        //the amplitude of each fuzzy set support is calculated 
        double d=(max-min)/(n-1);
        double iz=min-d, ce=min, de=min+d;

        content[0]=new FuzzyNumberTRLEFT(ce,de);
        for (int i=1;i<n-1;i++) {
            iz+=d; ce+=d; de+=d;
            content[i]=new FuzzyNumberTRIANG(iz,ce,de);
        }
        iz+=d; ce+=d; de+=d;
        content[n-1]=new FuzzyNumberTRRIGHT(iz,ce);
    }
    /** 
     * <p> 
     * A constructor for a fuzzy partition of n fuzzy sets whose support is given in a vector. 
     * This type of partition is not separated uniformely.
     * The first and last fuzzy set are a FuzzyNumberTRLEFT and FuzzyNumberTRRIGHT respectively while 
     * the middle fuzzy sets are triangular fuzzy sets.   
     * 
     * </p> 
     * @param values the shared extremes for each fuzzy set in the partition.  
     */
	public FuzzyPartition(double values[]) {
		content=new Fuzzy[values.length];
        int n=content.length;
        content[0]=new FuzzyNumberTRLEFT(values[0],values[1]);
        for (int i=1;i<n-1;i++) {
            content[i]=new FuzzyNumberTRIANG(values[i-1],values[i],values[i+1]);
        }
        content[n-1]=new FuzzyNumberTRRIGHT(values[n-2],values[n-1]);	
	}
	/** 
     * <p> 
     *  Characteristic points from fuzzy partition are copied to a vector of numbers.
     *  If partition is defined in N instead R, this method is not necessary.          
     * 
     * </p> 
     * @return a vector with characteristic points of the fuzzy sets in the partition.  
     */
        public double[] toVector() {

        	if (content[0] instanceof FuzzySingleton) return new double[0];
        
           // Partition with 2 elements
            if (content.length==2) {
                double []result = new double[2];
                 FuzzyNumberTRLEFT b=(FuzzyNumberTRLEFT)content[0];
                 result[0]=b.center;
                 result[1]=b.right;
                 return result;
            }
        
           double []result = new double[content.length];
           FuzzyNumberTRIANG b;

           b=(FuzzyNumberTRIANG)content[1];
           result[0]=b.left;
           for (int i=1;i<result.length-1;i++) {
             b=(FuzzyNumberTRIANG)content[i];
             result[i]=b.center;
           }
           b=(FuzzyNumberTRIANG)content[result.length-2];
           result[result.length-1]=b.right;

           return result;

        }
    /** 
     * <p> 
     * A constructor for a fuzzy partition of n singleton fuzzy sets with values i. 
     *    
     * </p> 
     * @param n the number of singleton fuzzy sets in the partition.  
     */
    public FuzzyPartition(int n) {
        content=new Fuzzy[n];
        for (int i=0;i<n;i++) {
            content[i]=new FuzzySingleton(i);
        }
    }
    /** 
     * <p> 
     * A copy constructor for fuzy partition, given other fuzzy partition. 
     * 
     * </p> 
     * @param p to be copied.    
     */
    public FuzzyPartition(FuzzyPartition p) {
        content=new Fuzzy[p.content.length];
        for (int i=0;i<content.length;i++) content[i]=p.content[i].clone();
    }
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */    
    public FuzzyPartition clone() {
        return new FuzzyPartition(this);
    }
    /** 
     * <p> 
     * Copies the Fuzzy partition parameter over the present instance. 
     * 
     * </p> 
     * @param p a fuzzy partition object to be copied
     */
    public void set(FuzzyPartition p) {
        content=new Fuzzy[p.content.length];
        for (int i=0;i<content.length;i++) content[i]=p.content[i].clone();
    }
    /** 
     * <p> 
     * Returns the characteristic points of the fuzzy set n. 
     * 
     * </p> 
     * @param n the index of the partition content to return.
     * @return the content of partition n.
     */
    public Fuzzy getComponent(int n) {
        return content[n];
    }
    /** 
     * <p> 
     * Rewrites the fuzzy set n of current partition. 
     * 
     * </p> 
     * @param n the index of the fuzzy set in the partition.
     * @param b the new fuzzy set.
     */
    public void setComponent(int n, Fuzzy b) {
        content[n]=b.clone();
    }
    /** 
     * <p> 
     * Returns the number of fuzzy sets in the partition 
     * 
     * </p> 
     * @return the number of fuzzy sets in the partition 
     */
    public int size() {
        return content.length;
    }
    /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of the fuzzy partition. 
     */	
    public String aString() {
        String result="PART(";
        for (int i=0;i<content.length;i++) result+=(content[i].aString()+" ");
        result+=")";
        return result;
    }

}

