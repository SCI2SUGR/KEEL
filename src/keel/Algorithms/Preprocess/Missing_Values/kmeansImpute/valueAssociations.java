/**
 * <p>
 * @author Written by Julián Luengo Martín 07/03/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.kmeansImpute;


/**
 * <p>
 * This class stores a frequency list of classes for a given value, i.e. for a same value in a attribute, 
 * it stores the number of times a determined class is associated with the value. 
 * </p>
 */
public class valueAssociations {
    protected FreqList list = null;
    protected double value;

    /**
     * <p> 
     * Creates a new instance of valueAssociations
     * </p> 
     */
    public valueAssociations(double value1) {
        this.value = value1;
        list = new FreqList();
    }
    
    /**
     * <p>
     * This method adds an element (a class) to the list
     * </p>
     * @param elem The index of the class we want to add
     */
    public void addElement(double elem){
        list.AddElement(String.valueOf(elem));
    }
    
    /**
     * <p>
     * Resets the iterator of the list to the beginning
     * </p>
     */
    public void reset(){
        list.reset();
    }
    
    /**
     * <p>
     * Returns the frequency of the class pointed by the current state of the list's iterator
     * </p>
     * @return The current frequency 
     */
    public int getCurrentFreq(){
        return list.getCurrent().getFreq();
    }
    
    /**
     * <p>
     * Obtains the value of the class pointed by the current state of the list's iterator
     * </p>
     * @return The current frequency
     */
    public String getCurrentValue(){
        return list.getCurrent().getValue();
    }
    
    /**
     * <p>
     * Iterates to the next element in the list
     * </p>
     */
    public void iterate(){
        list.iterate();
    }
    
    /**
     * <p>
     * Tests if the iterator is out of the bounds of the list
     * </p>
     * @return True if the iterator has reached the end of the list, false in other case
     */
    public boolean outOfBounds(){
        return list.outOfBounds();
    }
    
    /**
     * <p>
     * Gives the total number of elements in the list (i.e. the sum of the frequencies).
     * </p>
     * @return the total number of elements in the list
     */
    public int totalElems(){
        return list.totalElems();
    }
    
    /**
     * <p>
     * The reference value of this object 
     * </p>
     * @return The representative value of this object. 
     */
    public double getValue(){
        return value;
    }
}
