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
 * This class stores an instance with a P(x) value associated
 * </p>
 */
public class InstanceP {
    public Instance inst;
    public double Px;
    public int index;
    /** Creates a new instance of InstanceP */
    public InstanceP() {
        inst = null;
        Px = 0;
    }
    
    /**
     * <p>
     * Creates a new InstanceP with the arguments passed
     * </p>
     * @param i the proper instace (referenced)
     * @param p the p value associated
     * @param in the index of the instance
     */
    public InstanceP(Instance i,double p,int in){
        inst = i;
        Px = p;
        index = in;
    }
}
