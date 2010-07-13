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
 * File: KNN.java
 * 
 * The KNN algorithm tries to find the K nearest instances in the
 * training data, selecting the most present class.
 * 
 * Euclidean (L2), Manhattan (L1) and HVDM distances can be used as
 * distance function by the classifier.
 * 
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @author Modified by Joaquín Derrac (University of Granada) 10/18/2008 
 * @version 1.1 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.KNN;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;
import keel.Dataset.Attribute;

import org.core.Files;
import java.util.StringTokenizer;

public class KNN extends LazyAlgorithm{

	//Parameters
	
	private int k;
	private int distanceType;
	
	//Constants
	
	private static final int MANHATTAN = 1;
	private static final int EUCLIDEAN = 2;
	private static final int HVDM = 3 ;
	
	//Additional structures
	
	private double stdDev [];
	private double nominalDistance [][][];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public KNN (String script) {
	  
		readDataFiles(script);
		
		//Naming the algorithm
		name="KNN";

		//Initialization of auxiliary structures
		
		if(distanceType==HVDM){
			
			stdDev= new double [inputAtt];
			
			calculateHVDM();
			
		}
		
		//Initialization stuff ends here. So, we can start time-counting
		
		setInitialTime();
		
	} //end-method 
	
	/** 
	 * Reads configuration script, to extract the parameter's values.
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		
		String file;
		String line;
		String type;
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

	    //Getting the number of neighbors
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    k = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the type of distance function
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    type = tokens.nextToken().substring(1); 
	    
	    distanceType=EUCLIDEAN;
	    
	    if(type.equalsIgnoreCase("MANHATTAN")){
	    	distanceType=MANHATTAN;
	    }
	    if(type.equalsIgnoreCase("HVDM")){
	    	distanceType=HVDM;
	    }	    

	}//end-method

	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	protected int evaluate (double example[]) {
	
		double minDist[];
		int nearestN[];
		int selectedClasses[];
		double dist;
		int prediction;
		int predictionValue;
		boolean stop;

		nearestN = new int[k];
		minDist = new double[k];
	
	    for (int i=0; i<k; i++) {
			nearestN[i] = 0;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
		    dist = distance(trainData[i],example);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neighbors
				stop=false;
				
				for(int j=0;j<k && !stop;j++){
				
					if (dist < minDist[j]) {
					    
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
	
		for (int i=0; i<nClasses; i++) {
			selectedClasses[i] = 0;
		}	
		
		for (int i=0; i<k; i++) {
			selectedClasses[trainOutput[nearestN[i]]]+=1;
		}
		
		prediction=0;
		predictionValue=selectedClasses[0];
		
		for (int i=1; i<nClasses; i++) {
		    if (predictionValue < selectedClasses[i]) {
		        predictionValue = selectedClasses[i];
		        prediction = i;
		    }
		}
		
		return prediction;
	
	} //end-method	
	
	/** 
	 * Computes the distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return Distance calculated
	 * 
	 */	
	private double distance(double instance1[],double instance2[]){
		
		double dist=0.0;
		
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
		
	} //end-method	

	/** 
	 * Computes the distance matrixes for HVDM distance
	 * 
	 */	
	private void calculateHVDM(){
		
		double mean;
		double quad;
		double VDM;
		int Nax,Nay,Naxc,Nayc;
		
		nominalDistance = new double[train.getAttributeDefinitions().getInputNumAttributes()][][];
		
		for (int i=0; i<inputAtt; i++) {
			//HVDM for numerical attributes uses the std dev of data 
			if (train.getAttributeDefinitions().getInputAttribute(i).getType() != Attribute.NOMINAL) {
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
				nominalDistance[i] = new double[train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues()][train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues()];
				for (int j=0; j<train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues(); j++) { 
    				nominalDistance[i][j][j] = 0.0;
    			}
    			for (int j=0; j<train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues(); j++) {
    				for (int l=j+1; l<train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues(); l++) {
    					
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
			}//end-IF (nominal)	
		}
		
	}//end-method	
	
	/** 
	 * Computes the HVDM distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return The HVDM distance
	 * 
	 */
	private double HVDMDistance(double [] instance1,double [] instance2){
		
		double result=0.0;
		
    	for (int i=0; i<instance1.length; i++) {
    		if (train.getAttributeDefinitions().getInputAttribute(i).getType() == Attribute.NOMINAL) {
    			result += nominalDistance[i][real2Nom(instance1[i],i)][real2Nom(instance2[i],i)];
    		} else {
    			result += Math.abs(instance1[i]-instance2[i]) / (4.0*stdDev[i]);
    		}
    	}
    	result = Math.sqrt(result);  
		
		return result;
		
	}//end-method	

	/** 
	 * Converts a real value to its representation as Nominal in the data set
	 * 
	 * @param real Real value
	 * @param att Attribute in the data set
	 * 
	 * @return The HVDM distance
	 * 
	 */
	private int real2Nom(double real,int att){
		
		int result;
		
		result=(int)(real*((train.getAttributeDefinitions().getInputAttribute(att).getNominalValuesList().size())-1));

		return result;  
		
	}//end-method	
	
} //end-class 

