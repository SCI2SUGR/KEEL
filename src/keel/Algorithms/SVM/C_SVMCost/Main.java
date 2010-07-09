/**
 * <p>
 * @author Written by Juli�n Luengo Mart�n 01/01/2006
 * @author Modified by Victoria López Morales 01/05/2010
 * @version 0.3
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.SVM.C_SVMCost;


public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	svmClassifierCost M;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        M = new svmClassifierCost (args[0]);
        M.process();
    }
    
}
