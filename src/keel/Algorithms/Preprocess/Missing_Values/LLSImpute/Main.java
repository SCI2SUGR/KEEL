package keel.Algorithms.Preprocess.Missing_Values.LLSImpute;
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
        LLSImpute lls;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        else{
            lls = new LLSImpute(args[0]);
            lls.run();
        }
    }
    
}
