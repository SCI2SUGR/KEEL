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

/***********************************************************************

	This file is part of the Fuzzy Instance Based Learning package, a
	Java package implementing Fuzzy Nearest Neighbor Classifiers as 
	complementary material for the paper:
	
	Fuzzy Nearest Neighbor Algorithms: Taxonomy, Experimental analysis and Prospects

	Copyright (C) 2012
	
	J. Derrac (jderrac@decsai.ugr.es)
	S. García (sglopez@ujaen.es)
	F. Herrera (herrera@decsai.ugr.es)

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


package keel.Algorithms.Fuzzy_Instance_Based_Learning;

import java.util.Arrays;
import java.util.StringTokenizer;

import keel.Dataset.*;

import org.core.Files;

/**
 * 
 * File: FuzzyIBLAlgorithm.java
 * 
 * Main class for FuzzyIBL methods. Provides a basic framework for reading
 * and preprocessing data, conduct the experiment and report results
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2012 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
public abstract class FuzzyIBLAlgorithm {

	//Files

	protected String outFile[];
	protected String testFile;
	protected String trainFile;
	protected String referenceFile;
	
	//Instance Sets
	
	protected InstanceSet train;
	protected InstanceSet test;
	protected InstanceSet reference;
	
	protected Instance temp;	
	
	//Data
	
	protected int inputAtt;
	protected int trainSize;
	protected Attribute[] inputs;
	protected Attribute output;
	protected boolean[] nulls;
	protected boolean[] nominal;
	
	protected double trainData[][];
	protected int trainOutput[];
	protected double testData[][];
	protected int testOutput[];
	protected double referenceData[][];
	protected int referenceOutput[];
	protected String relation;
	
	protected int nClasses;
	protected int nInstances[];
	
	//Naming
	
	protected String name;
	
	//Random seed
	
	protected long seed;
	
	//Classification
	protected int trainPrediction[];
	protected int testPrediction[];	
	
	/** 
	 * Read the configuration and data files, and process it.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	protected void readDataFiles(String script){
	    
		//Read of the script file
		readConfiguracion(script);   
		readParameters(script);

	    //Read of training data files
	    try {
			train = new InstanceSet();
			train.readSet(trainFile, true);

		    inputAtt = Attributes.getInputNumAttributes();
		    trainSize = train.getNumInstances();
		    inputs = Attributes.getInputAttributes();
		    output = Attributes.getOutputAttribute(0);
		    
			//Normalize the data
		    
			normalizeTrain();
			
	    } catch (Exception e) {
			System.err.println(e);
			System.exit(1);
	    }
	    
	    //Read of test data files
	    try {
			test = new InstanceSet();
			test.readSet(testFile, false);

			//Normalize the data
			normalizeTest();
			
			
	    } catch (Exception e) {
			System.err.println(e);
			System.exit(1);
	    }
	    
	    //Read of reference data files
	    try {
			reference = new InstanceSet();
			reference.readSet(referenceFile, false);

			//Normalize the data
			normalizeReference();
			
	    } catch (Exception e) {
			System.err.println(e);
			System.exit(1);
	    }

		//Now, the data is loaded and preprocessed

	    //Get the number of classes
	    nClasses=Attributes.getOutputAttribute(0).getNumNominalValues();
	    
	    //And the number of instances on each class
	    
	    nInstances=new int[nClasses];
	    Arrays.fill(nInstances, 0);

	    for(int i=0;i<trainOutput.length;i++){
	    	nInstances[trainOutput[i]]++;
	    }
	    
	    //Initialize classification structures
	    trainPrediction= new int [trainSize];
	    testPrediction= new int [test.getNumInstances()];
	    
	    Arrays.fill(trainPrediction, -1);
	    Arrays.fill(testPrediction, -1);
	    
	}//end-method 

	/** 
	 * Reads configuration script, and extracts its contents.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readConfiguracion (String script) {

		String file, cad, token;
		StringTokenizer fileLines, tokens;
		byte line[];
	    int i, j;

	    outFile = new String[3];

	    file = Files.readFile(script);
	    fileLines = new StringTokenizer (file,"\n\r");

	    fileLines.nextToken();
	    cad = fileLines.nextToken();

	    tokens = new StringTokenizer (cad, "=");
	    tokens.nextToken();
	    token = tokens.nextToken();

	    //Getting the names of training and test files
	    //reference file will be used as comparison
	    
	    line = token.getBytes();
	    for (i=0; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    trainFile = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    referenceFile = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    testFile = new String (line,i,j-i);

	    //Getting the path and base name of the results files
	    
	    cad = fileLines.nextToken();
	    tokens = new StringTokenizer (cad, "=");
	    tokens.nextToken();
	    token = tokens.nextToken();

	    //Getting the names of output files
	    
	    line = token.getBytes();
	    for (i=0; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    outFile[0] = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    outFile[1] = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    outFile[2] = new String (line,i,j-i);
	    
	} //end-method

	/** 
	 * Reads the parameters of the algorithm. 
	 * Must be implemented in the subclass.
	 * 
	 * @param script Configuration script
	 * 
	 */
	protected abstract void readParameters(String script);
	
	/** 
	 * This function builds the data matrix for training data and normalizes inputs values
	 */	
	protected void normalizeTrain() throws DataException {

	    StringTokenizer tokens;
	    double minimum[];
	    double range[];

	    //Check if dataset corresponding with a classification problem
	    
	    if (Attributes.getOutputNumAttributes() < 1) {
			throw new DataException ("This dataset haven´t outputs, so it not corresponding to a classification problem.");
	    } else if (Attributes.getOutputNumAttributes() > 1) {
			throw new DataException ("This dataset have more of one output.");
	    }

	    if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new DataException ("This dataset have an input attribute with float values, so it not corresponding to a classification problem.");
	    }

	    //Copy the data
	    
	    tokens = new StringTokenizer (train.getHeader()," \n\r");
	    tokens.nextToken();
	    relation = tokens.nextToken();

	    trainData = new double[train.getNumInstances()][inputAtt];
	    trainOutput = new int[train.getNumInstances()];
	    
	    for (int i=0; i<train.getNumInstances(); i++) {
		
	    	temp = train.getInstance(i);
			trainData[i] = temp.getAllInputValues();
			trainOutput[i] = (int)temp.getOutputRealValues(0);
			nulls = temp.getInputMissingValues();
			
			//Clean missing values
	        for (int j=0; j<nulls.length; j++){
	        	if (nulls[j]) {
	        		trainData[i][j]=0.0;
		        }
	        }
	    }
	    
	    //Normalice the data
	    
	    minimum=new double[inputAtt];
	    range=new double[inputAtt];	    

	    for (int i=0; i<inputAtt; i++) {
            if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
		    	minimum[i]=Attributes.getInputAttribute(i).getMinAttribute();
		    	range[i]=Attributes.getInputAttribute(i).getMaxAttribute()-minimum[i];
            }
	    }
	    
	    //Both real and nominal data are normaliced in [0,1]
	    
	    for (int i=0; i<train.getNumInstances(); i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		trainData[i][j] /= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}
	            }else{
	                trainData[i][j] -= minimum[j];
	                trainData[i][j] /= range[j];
	            }
	    	}
	    }
	    
	} //end-method 

	/** 
	 * This function builds the data matrix for test data and normalizes inputs values
	 */	
	protected void normalizeTest() throws DataException {

	    StringTokenizer tokens;
	    double minimum[];
	    double range[];

	    //Check if dataset corresponding with a classification problem
	    
	    if (Attributes.getOutputNumAttributes() < 1) {
			throw new DataException ("This dataset haven´t outputs, so it not corresponding to a classification problem.");
	    } else if (Attributes.getOutputNumAttributes() > 1) {
			throw new DataException ("This dataset have more of one output.");
	    }

	    if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new DataException ("This dataset have an input attribute with float values, so it not corresponding to a classification 	problem.");
	    }

	    //Copy the data
	    
	    tokens = new StringTokenizer (test.getHeader()," \n\r");
	    tokens.nextToken();
	    tokens.nextToken();

	    testData = new double[test.getNumInstances()][inputAtt];
	    testOutput = new int[test.getNumInstances()];
	    
	    for (int i=0; i<test.getNumInstances(); i++) {
		
	    	temp = test.getInstance(i);
	    	testData[i] = temp.getAllInputValues();
	    	testOutput[i] = (int)temp.getOutputRealValues(0);
			nulls = temp.getInputMissingValues();
			
			//Clean missing values
	        for (int j=0; j<nulls.length; j++){
	        	if (nulls[j]) {
	        		testData[i][j]=0.0;
		        }
	        }
	    }
	    
	    //Normalice the data
	    
	    minimum=new double[inputAtt];
	    range=new double[inputAtt];	    

	    for (int i=0; i<inputAtt; i++) {
            if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
		    	minimum[i]=Attributes.getInputAttribute(i).getMinAttribute();
		    	range[i]=Attributes.getInputAttribute(i).getMaxAttribute()-minimum[i];
            }
	    }
	    
	    //Both real and nominal data are normaliced in [0,1]
	    
	    for (int i=0; i<test.getNumInstances(); i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		testData[i][j] /= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}
	            }
	            else{
	            	testData[i][j] -= minimum[j];
	            	testData[i][j] /= range[j];
	            }
	    	}
	    }
	    
	} //end-method 
	
	/** 
	 * This function builds the data matrix for reference data and normalizes inputs values
	 */	
	protected void normalizeReference() throws DataException {

	    StringTokenizer tokens;
	    double minimum[];
	    double range[];

	    //Check if dataset corresponding with a classification problem
	    
	    if (Attributes.getOutputNumAttributes() < 1) {
			throw new DataException ("This dataset haven´t outputs, so it not corresponding to a classification problem.");
	    } else if (Attributes.getOutputNumAttributes() > 1) {
			throw new DataException ("This dataset have more of one output.");
	    }

	    if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new DataException ("This dataset have an input attribute with float values, so it not corresponding to a classification problem.");
	    }

	    //Copy the data
	    
	    tokens = new StringTokenizer (reference.getHeader()," \n\r");
	    tokens.nextToken();
	    tokens.nextToken();

	    referenceData = new double[reference.getNumInstances()][inputAtt];
	    referenceOutput = new int[reference.getNumInstances()];
	    
	    for (int i=0; i<reference.getNumInstances(); i++) {
		
	    	temp = reference.getInstance(i);
	    	referenceData[i] = temp.getAllInputValues();
	    	referenceOutput[i] = (int)temp.getOutputRealValues(0);
			nulls = temp.getInputMissingValues();
			
			//Clean missing values
	        for (int j=0; j<nulls.length; j++){
	        	if (nulls[j]) {
	        		referenceData[i][j]=0.0;
		        }
	        }
	    }
	    
	    //Normalice the data
	    
	    minimum=new double[inputAtt];
	    range=new double[inputAtt];	    

	    for (int i=0; i<inputAtt; i++) {
            if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
		    	minimum[i]=Attributes.getInputAttribute(i).getMinAttribute();
		    	range[i]=Attributes.getInputAttribute(i).getMaxAttribute()-minimum[i];
            }
	    }
	    
	    //Both real and nominal data are normaliced in [0,1]
	    
	    for (int i=0; i<reference.getNumInstances(); i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		referenceData[i][j] /= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}
	            }else{
	            	referenceData[i][j] -= minimum[j];
	            	referenceData[i][j] /= range[j];
	            }
	    	}
	    }
	    
	}//end-method 
	
	/**
	 * Prints KEEL standard output files.
	 * 
	 * @param filename Name of output file
	 * @param realClass Real output of instances
	 * @param prediction Predicted output for instances
	 */
	protected void writeOutput(String filename, int [] realClass, int [] prediction) {
	
		String text = "";
		
		/*Printing input attributes*/
		text += "@relation "+ relation +"\n";

		for (int i=0; i<inputs.length; i++) {
			
			text += "@attribute "+ inputs[i].getName()+" ";
			
		    if (inputs[i].getType() == Attribute.NOMINAL) {
		    	text += "{";
		        for (int j=0; j<inputs[i].getNominalValuesList().size(); j++) {
		        	text += (String)inputs[i].getNominalValuesList().elementAt(j);
		        	if (j < inputs[i].getNominalValuesList().size() -1) {
		        		text += ", ";
		        	}
		        }
		        text += "}\n";
		    } else {
		    	if (inputs[i].getType() == Attribute.INTEGER) {
		    		text += "integer";
		        } else {
		        	text += "real";
		        }
		        text += " ["+String.valueOf(inputs[i].getMinAttribute()) + ", " +  String.valueOf(inputs[i].getMaxAttribute())+"]\n";
		    }
		}

		/*Printing output attribute*/
		text += "@attribute "+ output.getName()+" ";

		if (output.getType() == Attribute.NOMINAL) {
			text += "{";
			
			for (int j=0; j<output.getNominalValuesList().size(); j++) {
				text += (String)output.getNominalValuesList().elementAt(j);
		        if (j < output.getNominalValuesList().size() -1) {
		        	text += ", ";
		        }
			}		
			text += "}\n";	    
		} else {
		    text += "integer ["+String.valueOf(output.getMinAttribute()) + ", " + String.valueOf(output.getMaxAttribute())+"]\n";
		}

		/*Printing data*/
		text += "@data\n";

		Files.writeFile(filename, text);
		
		if (output.getType() == Attribute.INTEGER) {
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			     text += "" + realClass[i] + " ";
			     text += "" + prediction[i] + " "; 
			      
			     text += "\n";			      
			     
			     if((i%10)==9){
			    	 Files.addToFile(filename, text);
			    	 text = "";
			     }     
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}
		}
		else{
			
			text = "";
			
			for (int i=0; i<realClass.length; i++) {
			      
			      
			    text += "" + (String)output.getNominalValuesList().elementAt(realClass[i]) + " ";
			    
			    if(prediction[i]>-1){
		    		  text += "" + (String)output.getNominalValuesList().elementAt(prediction[i]) + " ";
		    	}
		    	else{
		    		  text += "" + "Unclassified" + " ";
		    	}
			    
			    text += "\n";
			      
			    if((i%10)==9){
			    	Files.addToFile(filename, text);
			    	text = "";
			    } 
			}			
			
			if((realClass.length%10)!=0){
				Files.addToFile(filename, text);
			}		
		}
		
	}//end-method 


}//end-class
