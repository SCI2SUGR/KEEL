/**
 * <p>
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Discretizers.DIBD;

import keel.Dataset.*;
import keel.Algorithms.Discretizers.Basic.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;


 /**
  * <p>
  * Main class of DIBD (Distribution-Index-Based Discretizer)
  * </p>
  */
public class Main {

 	
//******************************************************************************************************
 	
 	/**
 	 * <p>
 	 * Creates a new instance of Main 
 	 * </p>
 	 */
 	public Main(){
 	}
 	
//******************************************************************************************************
 		
 	/**
 	 * <p>
 	 * Main method
 	 * </p>
 	 * @param args the command line arguments
 	 */
 	public static void main(String[] args){
 		
 		ParserParameters.doParse(args[0]);
 		LogManager.initLogManager();
 		InstanceSet is = new InstanceSet();
 		
 		try {	
 			is.readSet(Parameters.trainInputFile,true);
         }catch(Exception e){
         	LogManager.printErr(e.toString());
             System.exit(1);
         }
         
 		checkDataset(is);

 		Discretizer dis = new DIBD();
 		dis.buildCutPoints(is);
 		dis.applyDiscretization(Parameters.trainInputFile,Parameters.trainOutputFile);
 		dis.applyDiscretization(Parameters.testInputFile,Parameters.testOutputFile);
 		
 		LogManager.closeLog();
 	}
 	
//******************************************************************************************************

	/**
	 * <p>
	 * Checks the dataset and exits the program if there are errors:
	 * 	- more than one output
	 * 	- output attribute is not nominal
	 * </p>
	 */
	static void checkDataset(InstanceSet is){
		
		Attribute []outputs = Attributes.getOutputAttributes();
        
		if(outputs.length != 1){
			LogManager.printErr("Only datasets with one output are supported");
            System.exit(1);
        }
        
		if(outputs[0].getType() != Attribute.NOMINAL){
			LogManager.printErr("Output attribute should be nominal");
            System.exit(1);
		}
        
		Parameters.numClasses = outputs[0].getNumNominalValues();
		Parameters.numAttributes = Attributes.getInputAttributes().length;
		Parameters.numInstances = is.getNumInstances();
	}
 	
}