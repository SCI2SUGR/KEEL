/**
 * File: Main.java
 * 
 * A Main class to process the paramethers of the method and launch the test
 * 
 * @author Written by Joaquin Derrac (University of Granada) 29/04/2010
 * @author Modified by Victoria Lopez (University of Granada) 06/06/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.Algorithms.Statistical_Tests.Classification.ImbFriedman;

import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;

public class Main {
	
	/**
	* Main method
	*
	* @param args Arguments of the program
	*/
    public static void main(String args[]) {

        boolean tty = false;
        ProcessConfig pc = new ProcessConfig();
        System.out.println("Reading configuration file: " + args[0]);
        if (pc.fileProcess(args[0]) < 0) {
            return;
        }

        ParseFileList pl = new ParseFileList();

        pl.statisticalTest(StatTest.FriedmanI, tty, pc);

    }//end-method
	
}//end-class
