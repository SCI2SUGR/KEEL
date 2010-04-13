/**
 * <p>
 * @author Written by Julián Luengo Martín 10/11/2005
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Transformations.decimal_scaling;
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
        decimal_scaling dec;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        
        dec = new decimal_scaling(args[0]);
        dec.normalize();
    }
    
}
