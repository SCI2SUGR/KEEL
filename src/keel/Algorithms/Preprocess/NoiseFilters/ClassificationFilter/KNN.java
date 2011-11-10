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
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 06/01/10
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Preprocess.NoiseFilters.ClassificationFilter;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import java.util.Arrays;


/**
 * <p>
 * The KNN algorithm tries to find the K nearest instances in the
 * training data, selecting the most present class.
 * 
 * Euclidean (L2), Manhattan (L1) and HVDM distances can be used as
 * distance function by the classifier.
 * </p>
 */
public class KNN {

	//Constants: distance type
	private static final int MANHATTAN = 1;
	private static final int EUCLIDEAN = 2;
	private static final int HVDM = 3 ;
	
	//Parameters
	private int k;
	private int distanceType;
	
	//Additional structures
	private double stdDev [];
	private double nominalDistance [][][];
	
	protected Instance temp;	
	
	//Data
	protected int inputAtt;
	protected Attribute[] inputs;
	protected Attribute output;
	protected boolean[] nulls;
	
	protected double trainData[][];
	protected int trainOutput[];
	protected double testData[][];
	protected int testOutput[];
	protected String relation;
	
	protected int nClasses;
	protected int nInstances[];

	//Random seed
	protected long seed;
	
	//Results
	protected int realClass[];
	protected int prediction[];
	protected int trainRealClass[];
	protected int trainPrediction[];
	
	private Instance[] trainInst;
	private Instance[] testInst;
	
	/** 
	 * The main method of the class
	 * 
	 */
	public KNN(Instance[] trainI, Instance[] testI) {
		
		// set parameters
		trainInst = trainI;
		testInst = testI;
		
		trainData = new double[trainInst.length][Parameters.numAttributes];
		for(int i = 0 ; i < trainInst.length ; ++i)
			System.arraycopy(trainInst[i].getAllInputValues(), 0, trainData[i], 0, Parameters.numAttributes);
		
		testData = new double[testInst.length][Parameters.numAttributes];
		for(int i = 0 ; i < testInst.length ; ++i)
			System.arraycopy(testInst[i].getAllInputValues(), 0, testData[i], 0, Parameters.numAttributes);
	  

		
		k = Parameters.numNeighbors;
		
		if(Parameters.distanceType.equalsIgnoreCase("euclidean"))
			distanceType = EUCLIDEAN;
		else if(Parameters.distanceType.equalsIgnoreCase("manhattan"))
			distanceType = MANHATTAN;
		else if(Parameters.distanceType.equalsIgnoreCase("hvdm"))
			distanceType = HVDM;
		
		// read Data Files -----------------------
		inputAtt = Attributes.getInputNumAttributes();
	    inputs = Attributes.getInputAttributes();
	    output = Attributes.getOutputAttribute(0);
	    
		//Normalize the datasets
		normalizeTrain();
		normalizeTest();
			
		
	    //Get the number of classes
	    nClasses = Attributes.getOutputAttribute(0).getNumNominalValues();
	    
	    //And the number of instances on each class
	    nInstances=new int[nClasses];
	    Arrays.fill(nInstances, 0);
		
	    for(int i=0;i<trainOutput.length;i++)
	    	nInstances[trainOutput[i]]++;	    
		
		//Initialization of auxiliary structures
		if(distanceType==HVDM){
			stdDev= new double [inputAtt];
			calculateHVDM();
		}
	}

	public int[] getPredictions(){
		return prediction;
	}
	
//******************************************************************************************************

	/** 
	 * Executes the classification of train and test data sets
	 * 
	 */	
	public void execute(){
		
		trainRealClass = new int[trainData.length];
		trainPrediction = new int[trainData.length];
		
		//Working on training
		for (int i=0; i<trainRealClass.length; i++) {
			trainRealClass[i] = trainOutput[i];
			trainPrediction[i] = evaluate(trainData[i]);
		}
		
		//Working on test
		realClass = new int[testData.length];
		prediction = new int[testData.length];
		
		for (int i=0; i<realClass.length; i++) {
			realClass[i] = testOutput[i];
			prediction[i] = evaluate(testData[i]);
			//System.out.println(realClass[i] + ", " + prediction[i]);
		}
		
	}
	
//******************************************************************************************************

	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 */
	protected int evaluate(double example[]){
	
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction, predictionValue;
		boolean stop;

		nearestN = new int[k];
		minDist = new double[k];

		Arrays.fill(nearestN, 0);
		Arrays.fill(minDist, Double.MAX_VALUE);
		
		
	    //KNN Method starts here
		for (int i=0 ; i<trainData.length ; i++){
		
		    dist = distance(trainData[i],example);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neighbors
				stop=false;
				
				for(int j=0;j<k && !stop;j++){
				
					if (dist < minDist[j]){
					    
						for (int l = k - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
		}
		
		//we have check all the instances... see what is the most present class
		selectedClasses= new int[nClasses];
		Arrays.fill(selectedClasses, 0);
		
		for (int i=0; i<k; i++)
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;
	}

//******************************************************************************************************

	/** 
	 * Computes the distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return Distance calculated
	 */	
	private double distance(double instance1[], double instance2[]){
		
		double dist = 0.0;
		
		switch (distanceType){
		
			case HVDM:  dist=HVDMDistance(instance1,instance2);
						break;
						
			case MANHATTAN:
						dist=manhattanDistance(instance1,instance2);
						break;
						
			case EUCLIDEAN:
			default:
						dist=euclideanDistance(instance1,instance2);
						break;		
		};

		return dist;
	}
	
//******************************************************************************************************

	/** 
	 * Computes the distance matrixes for HVDM distance
	 * 
	 */	
	private void calculateHVDM(){
		
		double mean;
		double quad;
		double VDM;
		int Nax,Nay,Naxc,Nayc;
		
		// esto lo hace con train
		nominalDistance = new double[Attributes.getInputNumAttributes()][][];
		
		for (int i=0; i<inputAtt; i++) {
			//HVDM for numerical attributes uses the std dev of data 
			if (Attributes.getAttribute(i).getType() != Attribute.NOMINAL) {
				mean = 0.0;
				quad = 0.0;
				for (int j=0; j<trainData.length; j++) {
					mean += trainData[j][i];
					quad += trainData[j][i]*trainData[j][i];
				}
				mean /= (double)trainData.length;
				stdDev[i] = Math.sqrt((quad/((double)trainData.length)) - (mean*mean));
			}
			else{
				// esto lo hace con train
				nominalDistance[i] = new double[Attributes.getInputAttribute(i).getNumNominalValues()][Attributes.getInputAttribute(i).getNumNominalValues()];
				for (int j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) { 
    				nominalDistance[i][j][j] = 0.0;
    			}
				
				// ESTO LO HACE CON TRAIN
    			for (int j=0; j<Attributes.getInputAttribute(i).getNumNominalValues(); j++) {
    				for (int l=j+1; l<Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
    					
    					VDM = 0.0;
    					Nax = Nay = 0;
    					
    					for (int m=0; m<trainData.length; m++) {
    						if (real2Nom(trainData[m][i],i) == j) {
    							Nax++;
    						}
    						if (real2Nom(trainData[m][i],i) == l) {
    							Nay++;
    						}
    					}
    					for (int m=0; m<nClasses; m++) {
    						Naxc = Nayc = 0;
        					for (int n=0; n<trainData.length; n++) {
        						if (real2Nom(trainData[n][i],i) == j && trainOutput[n] == m) {
        							Naxc++;
        						}
        						if (real2Nom(trainData[n][i],i) == l && trainOutput[n] == m) {
        							Nayc++;
        						}
        					}
        					VDM += (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay)) * (((double)Naxc / (double)Nax) - ((double)Nayc / (double)Nay));
    					}
    					nominalDistance[i][j][l] = Math.sqrt(VDM);
    					nominalDistance[i][l][j] = Math.sqrt(VDM);
    				}
    			}
			}
		}
		
	}
	
//******************************************************************************************************
	
	/** 
	 * Computes the HVDM distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The HVDM distance
	 */
	private double HVDMDistance(double [] instance1,double [] instance2){
		
		double result=0.0;
		
    	for (int i=0; i<instance1.length; i++) {
    		if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL)
    			result += nominalDistance[i][real2Nom(instance1[i],i)][real2Nom(instance2[i],i)];
    		else
    			result += Math.abs(instance1[i]-instance2[i]) / (4.0*stdDev[i]);
    	}
    	result = Math.sqrt(result);  
		
		return result;
	}
	
//******************************************************************************************************

	/** 
	 * Converts a real value to its representation as Nominal in the data set
	 * 
	 * @param real Real value
	 * @param att Attribute in the data set
	 * 
	 * @return The HVDM distance
	 */
	private int real2Nom(double real,int att){
		
		int result = 0;
		
		// con TRAIN
		result=(int)(real*((Attributes.getInputAttribute(att).getNominalValuesList().size())-1));

		return result;  
	}
	
//******************************************************************************************************
	
	/** 
	 * This function builds the data matrix for training data and normalizes inputs values
	 */	
	protected void normalizeTrain(){

	    double minimum[];
	    double range[];

	    trainData = new double[trainInst.length][inputAtt];
	    trainOutput = new int[trainInst.length];
	    
	    for (int i=0 ; i < trainInst.length ; i++) {
		
	    	temp = trainInst[i];
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
	    minimum = new double[inputAtt];
	    range = new double[inputAtt];	    

	    for (int i=0; i<inputAtt; i++) {
            if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
            	// ESTO DEBERIA SER TENIENDO EN CUANTA SOLO EL TRAIN, NO TOOD COMO ESTARA...
		    	minimum[i] = Attributes.getInputAttribute(i).getMinAttribute();
		    	range[i] = Attributes.getInputAttribute(i).getMaxAttribute()-minimum[i];
            }
	    }
	    
	    //Both real and nominal data are normaliced in [0,1]
	    
	    for (int i=0; i<trainInst.length; i++) {
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
	    
	}
	
//******************************************************************************************************

	/** 
	 * This function builds the data matrix for test data and normalizes inputs values
	 */	
	protected void normalizeTest(){

	    double minimum[];
	    double range[];

	    testData = new double[testInst.length][inputAtt];
	    testOutput = new int[testInst.length];
	    
	    for (int i=0; i<testInst.length; i++) {
		
	    	temp = testInst[i];
	    	testData[i] = temp.getAllInputValues();
	    	testOutput[i] = (int)temp.getOutputRealValues(0);
			nulls = temp.getInputMissingValues();
			
			//Clean missing values
	        for (int j=0; j<nulls.length; j++)
	        	if (nulls[j])
	        		testData[i][j]=0.0;
	    }
	    
	    //Normalice the data
	    minimum=new double[inputAtt];
	    range=new double[inputAtt];	    

	    for (int i=0; i<inputAtt; i++) {
            if (Attributes.getInputAttribute(i).getType() != Attribute.NOMINAL) {
            	// ESTO LO HACE CON TRAIN
		    	minimum[i] = Attributes.getInputAttribute(i).getMinAttribute();
		    	range[i] = Attributes.getInputAttribute(i).getMaxAttribute()-minimum[i];
            }
	    }
	    
	    //Both real and nominal data are normaliced in [0,1]
	    for (int i=0; i<testInst.length; i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	// ESTO LO HACE CON TEST
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
	    
	}
	
//******************************************************************************************************

	/** 
	 * Executes the classification of reference and test data sets
	 * 
	 */
	public void executeReference(){
		
		//Working on test
		realClass = new int[testData.length];
		prediction = new int[testData.length];	
		
		for (int i=0 ; i<realClass.length ; i++){
			realClass[i] = testOutput[i];
			prediction[i] = evaluate(testData[i]);
		}
	
	}
	
//******************************************************************************************************

	/** 
	 * Calculates the Euclidean distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 */
	protected double euclideanDistance(double instance1[], double instance2[]){
		
		double length = 0.0;

		for (int i=0; i<instance1.length; i++)
			length += (instance1[i]-instance2[i])*(instance1[i]-instance2[i]);
		
		length = Math.sqrt(length); 
				
		return length;
	}
	
//******************************************************************************************************
	
	/** 
	 * Calculates the Manhattan distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The Euclidean distance
	 */
	protected double manhattanDistance(double instance1[], double instance2[]){
		
		double length = 0.0;

		for (int i=0; i<instance1.length; i++)
			length += Math.abs(instance1[i]-instance2[i]);
				
		return length;
	}
	
//******************************************************************************************************
	
	/** 
	 * Generates a string with the contents of the instance
	 * 
	 * @param instance a Instance to print. 
	 * 
	 * @return A string, with the values of the instance
	 * 
	 */	
	public static String printInstance(int instance[]){
		
		String exit="";
		
		for(int i=0;i<instance.length;i++)
			exit+=instance[i]+" ";
		
		return exit;
	}
	
}