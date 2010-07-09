/**
 * <p>
 * @author Written by Julián Luengo Martín 10/11/2005
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */

package keel.Algorithms.Preprocess.Transformations.min_max;
/**
 *
 * @author Julian
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        min_max MM;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        
        MM = new min_max(args[0]);
        MM.normalize();
        
    }
    
    
}
