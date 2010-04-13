/**
 * <p>
 * @author Written by Julián Luengo Martín 31/12/2005
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;

/**
 * <p>
 * this class store a pair of values (String) and their frequency (int)
 * </p>
 */
public class ValuesFreq {
    protected String Value1;
    protected String Value2;
    protected int Freq;

    /**
     * <p> 
     * Creates a new instance of Pair Value-frequency
     * </p>
     */
    public ValuesFreq() {
        Value1 = "";
        Value2 = "";
        Freq = 0;
    }
    
    /**
     * <p>
     *	Creates a new pair with established values and frequency
     * </p>
     * @param newValue1 The first value of the pair
     * @param newValue2 The second value of the pair
     * @param newFreq The frequency associated
     */
    public ValuesFreq(String newValue1, String newValue2, int newFreq) {
        Value1 = newValue1;
        Value2 = newValue2;
        Freq = newFreq;
    }
    
    /**
     * <p>
     * Test it the provided object is equal in values to this object
     * Overrides Object.equals()
     * </p>
     * @return True if the objects values are the same object, false in other case.
     */
    public boolean equals(Object obj){
        return ((ValuesFreq)obj).Value1 == this.Value1 && ((ValuesFreq)obj).Value2 == this.Value2;
    }
    
    /**
     * <p>
     * Increases this object frequency by one
     * </p>
     */
    public void incFreq(){
        this.Freq += 1;
    }
    
    /**
     * <p>
     * Compares the frequencies of two pairs, and test if this object's frequency is higher than the provided one
     * </p>
     * @param ref The reference pair
     * @return true if this object has more frequency than the reference one, false other case
     */
    public boolean moreFreq(ValuesFreq ref){
        return (this.Freq > ref.Freq);
    }
    
    /**
     * <p>
     * Returns the first value of this object (the first string)
     * </p>
     * @return The first value of the object
     */
    public String getValue1(){
        return Value1;
    }
    
    /**
     * <p>
     * Returns the second value of the object (the second string)
     * </p>
     * @return the second value
     */
    public String getValue2(){
        return Value2;
    }
    
    /**
     * <p>
     * Returns the frequency of this pair
     * </p>
     * @return the associated frequency
     */
    public int getFreq(){
        return Freq;
    }
    
}
