/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2007
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Symbolic_Regression.crispSymRegSAP;
import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Algorithms.Symbolic_Regression.Shared.*;
import keel.Algorithms.Shared.Parsing.*;
import org.core.*;



public class crispSymRegSAP {
	/**
	 * Wrapper for symbolicRegression with crisp values and based on SA (Simulated Annealing) paradigm).
	 */
	//Random number generator
	static Randomize rand;
	/**
	 * <p>
	 * Loads the configuration for SA algorithm (with crisp values) from file args[0] using class ProcessConfig.
	 * </p>
	 * @param args command line parameters. args[0] must contains the configuration file.
	 */
	public static void main(String args[]) {
		
		boolean tty=false;
		ProcessConfig pc=new ProcessConfig();
		System.out.println("Reading configuration file: "+args[0]);
		if (pc.fileProcess(args[0])<0) return;
		int result=pc.parAlgorithmType;
		rand=new Randomize();
		rand.setSeed(pc.parSeed);
		
		ParseFileRegSym pl=new ParseFileRegSym();
		pl.symbolicRegressionFuzzySAP(FuzzyRegressor.Crisp,tty,pc,rand);
		
	}
}
