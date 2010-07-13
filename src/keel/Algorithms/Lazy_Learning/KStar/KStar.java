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
 * File: KStar.java
 * 
 * The KStar Algorithm.
 * A new instance based classifier, wich rather than using distance, defines
 * a "transformation probability" from test instance to each train instance.
 * It accumulates all the "transformation probabilities" and select the class
 * wich has the most
 * 
 * @author Written by Joaquín Derrac (University of Granada) 14/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.KStar;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.*;

public class KStar extends LazyAlgorithm{
	
	//Parameters
	
	int selectionMethod;
	double blendFactor;
	
	//Adictional structures
	
	double trainDistances [];
	double classProb [];
	ArrayList <Hashtable<Double,Double>> scaleTable;
	
	//Constants
	
	private static final double EPSILON = 1e-5;
	private static final double ROOT_FINDER_ACCURACY= 0.01;
	private static final int ROOT_FINDER_MAX_ITER=30;
	private static final int RANDOM = 1;
	private static final int FIXED = 2;
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public KStar (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="KStar";

		//Inicialization of auxiliar structures
	    
	    classProb=new double[nClasses];

	    trainDistances=new double[trainData.length];
  
	    scaleTable=new ArrayList<Hashtable<Double, Double>>();

	    for(int i=0;i<trainData.length;i++){
	    	scaleTable.add(new Hashtable<Double, Double>());
	    }
	    
	    //Initialization of random generator
	    
	    Randomize.setSeed(seed);
	    
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
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    
	    //Getting the seed
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    seed = Long.parseLong(tokens.nextToken().substring(1));

	    //Getting the selectionMethod
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    selectionMethod = tokens.nextToken().substring(1).equalsIgnoreCase("Random")?RANDOM:FIXED;   

	    //Getting the blendFactor
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    blendFactor = Double.parseDouble(tokens.nextToken().substring(1));

	}//end-method  

	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output;
		double probability;

		output=-1;
		
		for(int i=0;i<classProb.length;i++){
			
			classProb[i]=0.0;
		}
		
		probability=0.0;
		
		//find class transformation probability distribution
		
		for(int i=0;i<trainData.length;i++){
			
			probability=calcTransProb(i,example);
			
			classProb[trainOutput[i]]+=probability;
		}
		
		switch(selectionMethod){
		
			case RANDOM:
				output=findRandomOutput(example);				
				break;
				
			case FIXED:
				output=findFixedOutput(example);
				break;
		};
				
		return output;
	}
	
	/** 
	 * Evaluates a instance to predict its class.
	 * Selects the most probability class
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */
	private int findFixedOutput(double example[]){
		
		int output=-1;
		double max=Double.MIN_VALUE;
		
		for(int i=0;i<classProb.length;i++){
		
			if(max<classProb[i]){
				max=classProb[i];
				output=i;
			}
			
		}
		
		return output;
	}

	/** 
	 * Evaluates a instance to predict its class.
	 * Selects a random class, using before 
	 * estimated probabilities.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted
	 * 
	 */	
	private int findRandomOutput(double example[]){
		
		int output=-1;
		double sum;
		double value;
		boolean found=false;

		sum=0.0;
		
		//check the Prob Vector
		
		for(int i=0;i<classProb.length;i++){
			sum+=classProb[i];	
		}
		
		//get a Random Value
		
		value=Randomize.Randdouble(0.0,sum); 
		
		sum=0.0;
		
		for(int i=0;i<classProb.length&&!found;i++){

			sum+=classProb[i];
			
			if(sum>value){
				output=i;
				found=true;
			}
		}
		
		return output;
	}

	/** 
	 * Estimates the "transformation probability" from the test instance
	 * to a train instance
	 * 
	 * @param instance Index to train instance
	 * @param example Instance evaluated 
	 * @return Transformation probability for the instance
	 * 
	 */
	private double calcTransProb(int instance,double example[]){
		
		double probability=0.0;
		
		for(int i=0;i< example.length;i++){
		
			probability+=calcAttTransProb(trainData[instance][i],example[i],i);

		}
		
		return probability;
	}

	/** 
	 * Estimates the "transformation probability" from an attribute of the test instance
	 * to an attribute of a train instance
	 * 
	 * @param instance Index to train instance
	 * @param example Instance evaluated 
	 * @param feature Attribute to be evaluated
	 * @return Transformation probability for the attribute
	 * 
	 */
	private double calcAttTransProb(double train,double test,int feature){
		
		double probability=0.0;
		double distance;
		double scale;

		//check if we had calculated that scale before
		
		Hashtable<Double, Double> auxTable= scaleTable.get(feature);
		
		if(auxTable.containsKey(test)){			
			scale=auxTable.get(test);
		}
		else{
			scale=calcScale(train,test,feature);
			scaleTable.get(feature).put(test, scale);
		}

		//calculate distance
		distance = Math.abs( test - train );
		
		//calculate probability
		probability = PStar( distance, scale );
		
		return probability;
	}
	
	/** 
	 * The PStar distribution
	 * 
	 * @param x In value
	 * @param scale Scale used
	 * @return Value of PStar distribution
	 * 
	 */
	private double PStar(double x, double scale) {
		
		double value;
		
		value=scale * Math.exp( -2.0 * x * scale );
		
		return value;
		    
	}
	
	/** 
	 * Calculates a scale value to select the apropiate number of
	 * instances to be measured by the PStar distance.
	 * 
	 * @param train Value for train instance
	 * @param test Value for test instance
	 * @param feature Attribute selected
	 * @return Value of scale
	 * 
	 */
	private double calcScale(double train,double test,int feature){
		
		double nearest;
		double lowest;
		int lowestcount;
		double scale;
		double root;
		double desiredInstances;
		double bottomSphere;
		double bottomRoot;
		double upRoot;
		double upSphere;
		double actualSphere;
		double zero;
		double best;
		boolean finish=false;
		int iterations;
		
		lowest=-1.0; //there are no lowest instance
		nearest=-1.0; //there are no nearest instance
		lowestcount=0;
		scale=-1.0;
		
		for (int i=0; i<trainData.length; i++) {
 
			trainDistances[i] = Math.abs(trainData[i][feature] - test);
			
			if ( (trainDistances[i]+EPSILON) < nearest || nearest == -1.0 ) {
				
				if ( (trainDistances[i]+EPSILON) < lowest || lowest == -1.0 ) {
					nearest = lowest;
					lowest = trainDistances[i];
					lowestcount = 1;
				}else{
					if ( Math.abs(trainDistances[i]-lowest) < EPSILON ) {			
						lowestcount++;
					}
					else{
						nearest = trainDistances[i];
					}
				}
			}
		}	
		
		//check if data values are all the same
				
	    if (nearest == -1.0 || lowest == -1.0) { // 
	    	
	    	scale=1.0;

		}
	    else{
	    	//root finding algorithm
	    	
	    	//initial root
	    	
	    	root=1.0 /(nearest - lowest);
	    	
	    	//final sphere size desired (blend % of no-lowest instances)
	    	
	    	desiredInstances=lowestcount+((trainData.length-lowestcount)*blendFactor);
		    if (blendFactor == 0) {
		    	desiredInstances += 1.0;
		    }
	    	
		    // root is bracketed in interval [bot,up]
		    
		    bottomRoot = 0.0 + ROOT_FINDER_ACCURACY / 2.0;
		    
		    upRoot = root * 16;     // a great value
		      
		    bottomSphere=calculateSphereSize(bottomRoot);
		    upSphere=calculateSphereSize(upRoot);

			if (bottomSphere < 0) {    
				// Couldn't include that many 
                // instances - going for max possible
				scale = bottomRoot;
			}
			
			if (upSphere > 0) { 
				// Couldn't include that few, 
                // going for min possible
				scale=upRoot;
			}		    
		    
			if(scale==-1.0){
				
				scale=1.0;
				
				//start of the iterative process
				
				best=Double.MAX_VALUE;
				iterations=0;
				while(!finish){

					actualSphere=calculateSphereSize(root);
					zero=actualSphere-desiredInstances;
					
					if ( Math.abs(zero) < best ) {
						best = Math.abs(zero);
						scale = root;
					}
					if ( Math.abs(zero) <= ROOT_FINDER_ACCURACY ) {
						// the algorithm has converged to a solution!
						finish=true;
					}
					
					if (zero > 0.0) {
						bottomRoot = root;
						root = (root + upRoot) / 2.0;
					}
					else{
						upRoot = root;
						root = (root + bottomRoot) / 2.0;					
					}

					if (iterations > ROOT_FINDER_MAX_ITER) {
						System.out.println("Warning: ROOT_FINDER_MAX_ITER exceeded");
					}
				}//end of the iterative process
			}
	    }//end-root finding algorithm

		return scale;
	}
	
	/** 
	 * Calculates the sphereSize (number of instances affected)
	 * for a given scale.
	 * 
	 * @param scale Scale used
	 * @return Sphere size
	 * 
	 */
	private double calculateSphereSize(double scale) {

	    double sphereSize;
	    double pstar;                // P*(b|a)
	    double pstarSum = 0.0;       // sum(P*)
	    double pstarSquareSum = 0.0; // sum(P*^2)
	    double inc;
	    
	    
	    for (int i = 0; i < trainData.length; i++) {
	    	
			pstar = PStar(trainDistances[i], scale );

			inc = pstar;
			pstarSum += inc;
			pstarSquareSum += inc * inc;
		      
		}

	    if(pstarSquareSum!=0){
	    	sphereSize = pstarSum * pstarSum / pstarSquareSum;
	    }
	    else{
	    	sphereSize = 0.0;
	    }
	    
	    return sphereSize;
	}
	
} //end-class 

