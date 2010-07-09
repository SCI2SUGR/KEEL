/**
 * <p>
 * @author Written by Julián Luengo Martín 18/02/2009
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */

package keel.Algorithms.Preprocess.Transformations.Nominal2Binary;
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
        
        Nominal2Binary MM;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        
        MM = new Nominal2Binary(args[0]);
        MM.transform();
        
    }
    
    
}
