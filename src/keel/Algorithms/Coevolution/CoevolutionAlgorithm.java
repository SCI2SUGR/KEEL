/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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
 * File: CoevolutionAlgorithm.java
 * 
 * A general framework for Coevolutionary Algorithms.
 * This class contains all common operations in the development of a 
 * Coevolutionary algorithm. Any Coevolutionary can extend this class and, 
 * by implementing the abstract "evaluate" and "readParameters" method,
 * getting most of its work already done.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.1 
 * @since JDK1.5
 * 
 */
 
package keel.Algorithms.Coevolution;

import java.util.StringTokenizer;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

import org.core.Fichero;

/**
 * 
 * File: CoevolutionAlgorithm.java
 * 
 * A general framework for Coevolutionary Algorithms.
 * This class contains all common operations in the development of a 
 * Coevolutionary algorithm. Any Coevolutionary can extend this class and, 
 * by implementing the abstract "evaluate" and "readParameters" method,
 * getting most of its work already done.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/1/2010 
 * @version 1.1 
 * @since JDK1.5
 * 
 */
public abstract class CoevolutionAlgorithm {

	//Files

    /**
     * Output files names
     */
    
	protected String outFile[];

    /**
     * Test file name
     */
    protected String testFile;

    /**
     * Train file name
     */
    protected String trainFile;

    /**
     * Reference file name
     */
    protected String referenceFile;
	
	//Instance Sets
	
    /**
     * Training dataset
     */
    	
	protected InstanceSet train;

    /**
     * Test dataset
     */
    protected InstanceSet test;

    /**
     * Reference dataset
     */
    protected InstanceSet reference;
	
    /**
     * Temporal instance.
     */
    protected Instance temp;	
	
	//Data
	
    /**
     * Number of input attributes
     */
    protected int inputAtt;

    /**
     * Inputs attributes
     */
    protected Attribute[] inputs;

    /**
     * Output attribute
     */
    protected Attribute output;

    /**
     * Missing values of a instance
     */
    protected boolean[] nulls;
	
    /**
     * Training input data.
     */
    protected double trainData[][];

    /**
     * Training output data.
     */
    protected int trainOutput[];

    /**
     * Test input data.
     */
    protected double testData[][];

    /**
     * Test output data.
     */
    protected int testOutput[];

    /**
     * Reference input data.
     */
    protected double referenceData[][];

    /**
     * Reference output data.
     */
    protected int referenceOutput[];

    /**
     * Relation string.
     */
    protected String relation;
	
    /**
     * Number of classes.
     */
    protected int nClasses;

    /**
     * Number of instances of each classes.
     */
    protected int nInstances[];
	
    /**
     * Initial time.
     */
    protected long initialTime;
	
    /**
     * Generation model time.
     */
    protected double modelTime;

    /**
     * Training prediction time.
     */
    protected double trainingTime;

    /**
     * Test prediction time.
     */
    protected double testTime;
	
	
	
    /**
     *Naming.
     */
    	
	protected String name;

	
    /**
     *Random seed.
     */
    	
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

	    fichero = Fichero.leeFichero (script);
	    lineasFichero = new StringTokenizer (fichero,"\n\r");

	    lineasFichero.nextToken();
	    linea = lineasFichero.nextToken();

	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    token = tokens.nextToken();

	    //Getting the names of training and test files
	    //reference file will be used as comparision
	    
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
     * @throws keel.Algorithms.Coevolution.CoevolutionAlgorithm.CheckException (if the dataset is not a classification problem)
	 */	
	protected void normalizeTrain() throws CheckException {

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
     * @throws keel.Algorithms.Coevolution.CoevolutionAlgorithm.CheckException (if the dataset is not a classification problem)
	 */	
	protected void normalizeTest() throws CheckException {

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
     * @throws keel.Algorithms.Coevolution.CoevolutionAlgorithm.CheckException (if the dataset is not a classification problem)
	 */	
	protected void normalizeReference() throws CheckException {

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
         * @param instance Instance to print
	 * 
	 * @return A string, with the values of the instance
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
	
	
	class CheckException  extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		/**
		 * Creates a new instance of CheckException
		 */
		  public CheckException() {
		    super();
		  }//end CheckException


		/**
		 * Does instance a new CheckException with the message
		 * specified and the Vector with all the errors.
		 * @param msg is the message of the exception
		 * @param _errors is a vector with all the errors.
		 */
		  public CheckException(String msg){
		    super(msg);
		  }//end ChecktException

		}//end CheckException


}//end-class

