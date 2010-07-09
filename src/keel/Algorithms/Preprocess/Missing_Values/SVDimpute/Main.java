/*
 * Main.java
 */
package keel.Algorithms.Preprocess.Missing_Values.SVDimpute;
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
        SVDimpute svd;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        else{
            svd = new SVDimpute(args[0]);
            svd.run();
        }
    }
    
}
