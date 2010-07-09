/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Amelia Zafra (University of Granada) 01/01/2007
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @author Modified by Victoria Lopez (University of Granada) 01/06/2010
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Classification.Imbalanced_Summary;
import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class Imbalanced_Summary {
	/**
	* <p>
	* This class has only a main method that calls Model_Summary output method for classification problems, defined in StatTest
	* </p>
	*/
	static Randomize rand;
	/**
	* <p>
	* This method reads a configuration file and calls statisticalTest with appropriate values to run
	* Model_Summary output module for classification problems, defined in StatTest class
	* @param args A string that contains the command line arguments
	* </p>
	*/	
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int algorithm=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		
		ParseFileList pl=new ParseFileList();
		pl.statisticalTest(StatTest.summaryI,tty,pc);
		
	}
}
