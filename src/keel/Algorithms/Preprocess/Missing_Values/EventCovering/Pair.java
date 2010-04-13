/**
 * <p>
 * @author Written by Julián Luengo Martín 14/05/2006
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;

/**
 * <p>
 * This class is a pair of ints
 * </p>
 */
public class Pair {
    public int e1;
    public int e2;
    /** Creates a new instance of Pair */
    public Pair() {
        
    }
    
    /**
     * <p>
     * Creates a pair with the provided integers
     * </p>
     * @param a the first element
     * @param b the second element
     */
    public Pair(int a, int b){
        e1 = a;
        e2 = b;
    }  
}
