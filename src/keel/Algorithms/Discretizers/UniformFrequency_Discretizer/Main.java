/**
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Discretizers.UniformFrequency_Discretizer;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Algorithms.Discretizers.Basic.*;


public class Main {
/**
 * <p>
 * Main class Uniform Frequency discretizer.
 * </p>
 */
	
	/** Creates a new instance of Main */
	public Main() {
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		ParserParameters.doParse(args[0]);
		LogManager.initLogManager();
		InstanceSet is=new InstanceSet();
		
		try {	
			is.readSet(Parameters.trainInputFile,true);
            } catch(Exception e) {
                        LogManager.printErr(e.toString());
                        System.exit(1);
            }
		checkDataset(is);

		Discretizer dis;
		String name=Parameters.algorithmName;
		dis=new UniformFrequencyDiscretizer(Parameters.numIntervals);
		Rand.initRand();		
		dis.buildCutPoints(is);
		dis.applyDiscretization(Parameters.trainInputFile,Parameters.trainOutputFile);
		dis.applyDiscretization(Parameters.testInputFile,Parameters.testOutputFile);
		LogManager.closeLog();
	}

        static void checkDataset(InstanceSet is) {
                Attribute []outputs=Attributes.getOutputAttributes();
                if(outputs.length!=1) {
                        LogManager.printErr("Only datasets with one output are supported");
                        System.exit(1);
                }
                if(outputs[0].getType()!=Attribute.NOMINAL) {
                        LogManager.printErr("Output attribute should be nominal");
                        System.exit(1);
                }
                Parameters.numClasses=outputs[0].getNumNominalValues();
                Parameters.numAttributes=Attributes.getInputAttributes().length;
        		Parameters.numInstances = is.getNumInstances();
        }
}
