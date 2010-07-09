/**
 * Main.java
 *

 */
package keel.Algorithms.Preprocess.Missing_Values.BPCA;
/**
 *
 * @author Julián Luengo Martín
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BPCA bpcafill;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        else{
        	bpcafill = new BPCA(args[0]);
        	bpcafill.run();
        }
    }
    
}
