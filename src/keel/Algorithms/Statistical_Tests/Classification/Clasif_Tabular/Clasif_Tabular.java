/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Amelia Zafra (University of Granada) 01/01/2007
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Classification.Clasif_Tabular;
import keel.Algorithms.Statistical_Tests.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class Clasif_Tabular {
	/**
	* <p>
	* This class has only a main method that calls Model_Tabular output method for classification problems, defined in StatTest
	* </p>
	*/
	static Randomize rand;
	/**
	* <p>
	* This method reads a configuration file and calls statisticalTest with appropriate values to run
	* Model_Tabular output module for classification problems, defined in StatTest class
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
		pl.statisticalTest(StatTest.tabularC,tty,pc);
		
	}
}
