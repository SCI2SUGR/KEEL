/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Discretizers.CACC;

import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.Globals.*;
import keel.Algorithms.Discretizers.Basic.*;


/**
 * 
 * <p>
 * Main class of CACC algorithm (discretization algorithm based on Class-Attribute Contingency Coefficient)
 * </p>
 * 
 * <p>
 * @author Written by Jose A. Saez Munoz (SCI2S research group, DECSAI in ETSIIT, University of Granada), 21/12/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class Main {

	
//******************************************************************************************************
	
	/**
	 * <p>
	 * Creates a new instance of Main.
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
        
		checkDataset();

		Discretizer dis = new CACC();
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
	static void checkDataset(){
		
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
	}
	
}
