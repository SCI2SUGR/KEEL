/**
* <p>
* @author Written by Joaquín Derrac (University of Granada)26/04/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Classification.FriedmanAlligned;

import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;

public class Main {
	
    public static void main(String args[]) {

        boolean tty = false;
        ProcessConfig pc = new ProcessConfig();
        System.out.println("Reading configuration file: " + args[0]);
        if (pc.fileProcess(args[0]) < 0) {
            return;
        }

        ParseFileList pl = new ParseFileList();

        pl.statisticalTest(StatTest.FriedmanAlignedC, tty, pc);

    }
}
