/**
 * <p>
 * @author Written by Julián Luengo Martín 14/05/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;
import java.util.*;
import keel.Dataset.*;

/**
 * <p>
 * This class is a cluster C for the EventCovering method
 * </p>
 */
public class Cluster {
    public Vector C;
    int number;
    
    /** Creates a new instance of Cluster */
    public Cluster() {
        C = new Vector();
        number = -1;
    }
    
    /**
     * <p>
     * Adds one instance to the cluster (referencing it)
     * </p>
     * @param inst the instance to be added
     */
    public void addInstance(Instance inst){
        C.addElement(inst);
    }
    
    /**
     * <p>
     * Returns the number of times a determined attribute value appears in the instances of this cluster
     * </p>
     * @param at the attribute value
     * @param numat the attribute index
     * @return the observed times the value has appeared in attribute numat in this set of instances
     */
    public int getObserved(String at,int numat){
        Instance inst;
        double [] input;
        double [] output;
        int obs = 0;
        
        for(int i=0;i<C.size();i++){
            inst = (Instance)C.elementAt(i);
            input = inst.getAllInputValues();
            output = inst.getAllOutputValues();
            
            if(numat < input.length){
                if(String.valueOf(input[numat]).compareTo(at) == 0)
                    obs++;
            }
            else{
                if(String.valueOf(output[numat-input.length]).compareTo(at) == 0)
                    obs++;
            }
        }
        return obs;
    }
    
    /**
     * <p>
     * Set the number of this cluster
     * </p>
     * @param index the new index
     */
    public void setNumber(int index){
        number = index;
    }
    
    /**
     * <p>
     * Gets the number of this cluster
     * </p>
     * @return the index of this cluster
     */
    public int getNumber(){
        return number;
    }
    
}
