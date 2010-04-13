/**
 * <p>
 * @author Written by Julián Luengo Martín 31/12/2005
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.ConceptMostCommonValue;


/**
 * <p>
 * this class store a value (String) and its frequency (int)
 * </p>
 */
public class ValueFreq {
    protected String Value;
    protected int Freq;

    /**
     * <p> 
     * Creates a new instance of Pair Value-frequency
     * </p>
     */
    public ValueFreq() {
        Value = "";
        Freq = 0;
    }
    
    /**
     * <p>
     *	Creates a new pair with established value and frequency
     * </p>
     * @param newValue The value of the pair
     * @param newFreq The frequency associated
     */
    public ValueFreq(String newValue, int newFreq) {
        Value = newValue;
        Freq = newFreq;
    }
    
    /**
     * <p>
     * Test it the provided object is equal in value to this object
     * Overrides Object.equals()
     * </p>
     * @return True if the objects values are the same object, false in other case.
     */
    public boolean equals(Object obj){
        return ((ValueFreq)obj).Value == this.Value;
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
     * Compares the frequencies of two pair, and test if this object's frequency is higher than the provided one
     * </p>
     * @param ref The reference pair
     * @return true if this object has more frequency than the reference one, false other case
     */
    public boolean moreFreq(ValueFreq ref){
        return (this.Freq > ref.Freq);
    }
    
    /**
     * <p>
     * Returns the value of this object (the string)
     * </p>
     * @return The value of the object
     */
    public String getValue(){
        return Value;
    }
    
    /**
     * <p>
     * Returns the frequency of this element
     * </p>
     * @return The current frequency of this object 
     */
    public int getFreq(){
        return Freq;
    }
}
