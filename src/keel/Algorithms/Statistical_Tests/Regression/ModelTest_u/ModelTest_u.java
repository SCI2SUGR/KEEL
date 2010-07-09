/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Regression.ModelTest_u;
import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class ModelTest_u {
	/**
	* <p>
	* This class has only a main method that calls Mann Whitney U test  for regression problems, defined in StatTest
	* </p>
	*/
	
	static Randomize rand;
	/**
	* <p>
	* This method reads a configuration file and calls statisticalTest with appropriate values to run the
	* Mann Whitney U test  for regression problems, defined in StatTest class
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
		pl.statisticalTest(StatTest.MannWhitneyR,tty,pc);
		
	}
}
