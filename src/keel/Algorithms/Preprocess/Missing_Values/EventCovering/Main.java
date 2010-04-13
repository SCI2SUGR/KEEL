/*
 * Main.java
 *
 * Created on 1 de enero de 2006, 17:15
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package keel.Algorithms.Preprocess.Missing_Values.EventCovering;
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
        EventCovering M;
        if (args.length != 1)
            System.err.println("Error. Only a parameter is needed.");
        else{
            M = new EventCovering(args[0]);
            M.process();
        }
    }
    
}
