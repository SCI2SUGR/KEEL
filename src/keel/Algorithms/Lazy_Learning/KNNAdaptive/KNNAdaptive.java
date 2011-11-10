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
 * File: KNNAdaptive.java
 * 
 * The KNN Adaptive Algorithm.
 * Similar to the KNN algorithm, distances are weighted by the distance
 * from the training instance to its nearest enemy.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.KNNAdaptive;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import org.core.*;
import java.util.StringTokenizer;

public class KNNAdaptive extends LazyAlgorithm{

	//Parameters
	
	private int k;
	private int distanceType;
	
	//Constants
	
	private static final int MANHATTAN = 1;
	private static final int EUCLIDEAN = 2;
	
	//Adictional structures
	
	double radius[];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public KNNAdaptive (String script) {
	  
		readDataFiles(script);
		
		//Naming the algorithm
		name="KNN Adaptative";

		//Inicialization of auxiliar structures
		radius=new double[trainData.length];	
		
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

	    //Getting the number of neighbors
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    k = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the type of distance function
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    distanceType = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?EUCLIDEAN:MANHATTAN;   
		
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
			nearestN[i] = -1;
			minDist[i] = Double.MAX_VALUE;
		}
		
	    //KNN Method starts here
	    
		for (int i=0; i<trainData.length; i++) {
		
		    dist = adaptativeDistance(trainData[i],example,i);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neigbours
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
	 * Calculates the distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @return Distance calculated
	 * 
	 */	
	private double distance(double instance1[],double instance2[]){
		
		double dist=0.0;
		
		switch (distanceType){
		
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
	 * Calculates the adaptative distance between two instances
	 * 
	 * @param instance1 First instance 
	 * @param instance2 Second instance
	 * @param index Index of train instance in radius structure 
	 * @return Distance calculated
	 * 
	 */	
	private double adaptativeDistance(double instance1[],double instance2[],int index){
		
		double dist;
		
		dist=distance(instance1,instance2);
		
		//Apply the radius conversion				
		dist=dist/radius[index];

		return dist;
		
	} //end-method	
	
	/** 
	 * Precalculates the radius of each train instance
	 * 
	 */	
	
	public void calculateRadius(){
	
		int ownClass;
		double minDist;
		double dist;

		for(int i=0;i<trainData.length;i++){
			
			ownClass=trainOutput[i];

			minDist=Double.MAX_VALUE;
			
			//Search the nearest enemy (instance from another class)
			for(int j=0; j<trainData.length;j++){
			
				if(ownClass!=trainOutput[j]){
					
					dist = distance(trainData[i],trainData[j]);
					
					if(dist<minDist){
						minDist=dist;
					}
				
				}
			}
			radius[i]=minDist;
		}
		
	} //end-method	
	
} //end-class 

