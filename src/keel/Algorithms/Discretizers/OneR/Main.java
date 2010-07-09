/**
 * <p>
 * @author Written by Julián Luengo Martín 28/10/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */

package keel.Algorithms.Discretizers.OneR;

import java.io.IOException;
import java.util.*;

import org.core.Randomize;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Algorithms.Statistical_Classifiers.Naive_Bayes.Algorithm;
import keel.Algorithms.Statistical_Classifiers.Naive_Bayes.myDataset;
import keel.Algorithms.Discretizers.Basic.*;

public class Main {
	

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
		checkDataset();
		
		Discretizer dis;
		String name=Parameters.algorithmName;
		dis=new OneR(Parameters.minimumValuesOfSameClassPerInterval);
		dis.buildCutPoints(is);
		dis.applyDiscretization(Parameters.trainInputFile,Parameters.trainOutputFile);
		dis.applyDiscretization(Parameters.testInputFile,Parameters.testOutputFile);
		LogManager.closeLog();
		
	}

	static void checkDataset() {
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
	}
}
