/**
 * <p>
 * @author Written by Julián Luengo Martín 31/12/2006
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Preprocess.Missing_Values.ConceptAllPossibleValues;
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
        ConceptAllPossibleValues  M;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        M = new ConceptAllPossibleValues (args[0]);
        M.process();
    }
}
