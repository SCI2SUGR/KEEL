package keel.Algorithms.Preprocess.Missing_Values.EM;
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
        EM em;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        else{
            em = new EM(args[0]);
            em.run();
        }
    }
    
}
