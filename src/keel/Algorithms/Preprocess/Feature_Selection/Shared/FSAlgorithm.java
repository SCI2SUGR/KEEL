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

/**
 * 
 * File: FSAlgorithm.java
 * 
 * A general framework for FS Algorithms.
 * This class contains all common operations in the developement of a 
 * FS algorithm. 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Preprocess.Feature_Selection.Shared;

import java.util.StringTokenizer;

import keel.Algorithms.Preprocess.Basic.CheckException;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

import org.core.Files;

public abstract class FSAlgorithm {

	//Files

	protected String outFile[];
	protected String testFile;
	protected String trainFile;
	protected String referenceFile;
	
	//Instance Sets
	
	protected InstanceSet train;
	protected InstanceSet test;
	
	protected Instance temp;	
	
	//Data
	
	protected int inputAtt;
	protected Attribute[] inputs;
	protected Attribute output;
	protected boolean[] nulls;
	
	protected double trainData[][];
	protected double trainReal[][];
	protected int trainOutput[];
	protected int trainNominal[][];
	protected boolean trainNulls[][];
	protected double testData[][];
	protected int testOutput[];

	protected String relation;
	
	protected int nClasses;
	protected int nInstances[];
	
	//Timing
	
	protected long initialTime;
	
	//Naming
	
	protected String name;
	
	//Random seed
	
	protected long seed;
	
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
		    inputs = Attributes.getInputAttributes();
		    output = Attributes.getOutputAttribute(0);
		    
			//Normalize the data
		    
			normalize();
			
	    } catch (Exception e) {
			System.err.println(e);
			System.exit(1);
	    }
	    
	    //Read of test data files
	    try {
			test = new InstanceSet();
			test.readSet(testFile, false);
			
	    } catch (Exception e) {
			System.err.println(e);
			System.exit(1);
	    }

		//Now, the data is loaded and preprocessed

	    //Get the number of classes
	    nClasses=Attributes.getOutputAttribute(0).getNumNominalValues();
	    
	    //And the number of instances on each class
	    
	    nInstances=new int[nClasses];
	    for(int i=0;i<nClasses;i++){
	    	nInstances[i]=0;
		}
	    for(int i=0;i<trainOutput.length;i++){
	    	nInstances[trainOutput[i]]++;
	    }
	    
	}//end-method 

	/** 
	 * Reads configuration script, and extracts its contents.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readConfiguracion (String script) {

		String fichero, linea, token;
		StringTokenizer lineasFichero, tokens;
		byte line[];
	    int i, j;

	    outFile = new String[3];

	    fichero = Files.readFile(script);
	    lineasFichero = new StringTokenizer (fichero,"\n\r");

	    lineasFichero.nextToken();
	    linea = lineasFichero.nextToken();

	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    token = tokens.nextToken();

	    //Getting the names of training and test files
	    
	    line = token.getBytes();
	    for (i=0; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    trainFile = new String (line,i,j-i);
	    for (i=j+1; line[i]!='\"'; i++);
	    i++;
	    for (j=i; line[j]!='\"'; j++);
	    testFile = new String (line,i,j-i);

	    //Getting the path and base name of the results files
	    
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
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
	protected void normalize() throws CheckException {

	    StringTokenizer tokens;
	    double minimum[];
	    double range[];

	    //Check if dataset corresponding with a classification problem
	    
	    if (Attributes.getOutputNumAttributes() < 1) {
			throw new CheckException ("This dataset haven´t outputs, so it not corresponding to a classification problem.");
	    } else if (Attributes.getOutputNumAttributes() > 1) {
			throw new CheckException ("This dataset have more of one output.");
	    }

	    if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new CheckException ("This dataset have an input attribute with float values, so it not corresponding to a classification 	problem.");
	    }

	    //Copy the data
	    
	    tokens = new StringTokenizer (train.getHeader()," \n\r");
	    tokens.nextToken();
	    relation = tokens.nextToken();

	    trainData = new double[train.getNumInstances()][inputAtt];
	    trainReal = new double[train.getNumInstances()][inputAtt];
	    trainNominal = new int[train.getNumInstances()][inputAtt];
	    trainOutput = new int[train.getNumInstances()];
	    trainNulls = new boolean[train.getNumInstances()][inputAtt];

	    for (int i=0; i<train.getNumInstances(); i++) {

	    	temp = train.getInstance(i);
			trainData[i] = temp.getAllInputValues();
			trainOutput[i] = (int)temp.getOutputRealValues(0);
			nulls = temp.getInputMissingValues();
			
			//Clean missing values
	        for (int j=0; j<nulls.length; j++){
	        	if (nulls[j]) {
	        		trainData[i][j]=0.0;
	        		trainNulls[i][j]=true;
		        }
	        	else{
	        		trainNulls[i][j]=false;
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

	            	trainNominal[i][j] = (int)trainData[i][j]; 

	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		trainData[i][j] /= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}

	            }else{
	            	trainReal[i][j] = trainData[i][j];
	                trainData[i][j] -= minimum[j];
	                trainData[i][j] /= range[j];

	            }
	    	}
	    }
	    
	} //end-method 

	/** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 * 
	 */
	protected double euclideanDistance(double instance1[],double instance2[]){
		
		double length=0.0;

		for (int i=0; i<instance1.length; i++) {
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
		}
			
		length = Math.sqrt(length); 
				
		return length;
		
	} //end-method
	
	/** 
	 * Checks if two instances are the same
	 * 
	 * @param a First instance 
	 * @param b Second instance
	 * @return True if both instances are equal.
	 * 
	 */
	protected boolean same(double a[],double b[]){

		for(int i=0;i<a.length;i++){
			if(a[i]!=b[i]){
				return false;
			}
		}
		return true;
		
	}
	
	/** 
	 * Generates a string with the contents of the instance
	 * 
	 * @param instance Instance to print. 
	 * 
	 * @return instance A string, with the values of the instance
	 * 
	 */	
	public static String printInstance(int instance[]){
		
		String exit="";
		
		for(int i=0;i<instance.length;i++){
			exit+=instance[i]+" ";
		}
		return exit;
	}
	
	/** 
	 * Sets the time counter
	 * 
	 */
	protected void setInitialTime(){
		
		initialTime = System.currentTimeMillis();
		
	}//end-method

}//end-class

