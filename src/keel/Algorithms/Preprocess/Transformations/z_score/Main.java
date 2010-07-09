/**
 * <p>
 * @author Written by Julián Luengo Martín 10/11/2005
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Transformations.z_score;
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
        
        z_score Z;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        Z = new z_score(args[0]);
        Z.normalize();

    }
    
}
