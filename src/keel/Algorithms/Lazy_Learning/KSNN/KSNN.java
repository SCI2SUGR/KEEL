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
 * File: KSNN.java
 * 
 * The K Symetrical NN Algorithm.
 * A enhanced K-NN classifier.For each test instance, votes are
 * recieved from its K-Nearest Neighbors and, in addiction, from
 * train instances who would accept the test instance as one of their
 * K-Nearest Neighbors  
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.KSNN;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.*;

public class KSNN extends LazyAlgorithm{

	//Parameters
	
	int K;
	
	//Adictional structures
	
	double further[];
	boolean selected[];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public KSNN (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="Center NN";

		//Inicialization of auxiliar structures

		further=new double [trainData.length];
		selected=new boolean [trainData.length];	
		
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
	    K = Integer.parseInt(tokens.nextToken().substring(1));

	}//end-method
	
	/** 
	 * Calculates, for each train instance, the distance to its
	 * further K neighbour.
	 * 
	 */
	public void getFurtherNeighbor(){
		
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		
		nearestN = new int[K];
		minDist = new double[K];
		
		for(int instance=0;instance<trainData.length;instance++){

		    for (int i=0; i<K; i++) {
				nearestN[i] = -1;
				minDist[i] = Double.POSITIVE_INFINITY;
			}
		    
			//find its K nearest neigbours

			for (int i=0; i<trainData.length; i++) {
			
			    dist = euclideanDistance(trainData[instance],trainData[i]);

				if (dist > 0.0){ //leave-one-out
				
					//see if it's nearer than our previous selected neigbours
					stop=false;
					
					for(int j=0;j<K && !stop;j++){
					
						if (dist < minDist[j]) {
						    
							for (int l = K - 1; l >= j+1; l--) {
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
			
			//Get the maximun distance
			further[instance]=minDist[K-1];
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
		
		int output;
		int votes[];
		double minDist[];
		int nearestN[];
		double dist;
		boolean stop;
		int maxVotes;
		
		votes=new int[nClasses];
		nearestN = new int[K];
		minDist = new double[K];
		
		for (int i=0; i<trainData.length; i++) {
			selected[i]=false;
		}
		
		//find its K nearest neigbours
		
	    for (int i=0; i<K; i++) {
			nearestN[i] = -1;
			minDist[i] = Double.POSITIVE_INFINITY;
		}
		
		for (int i=0; i<trainData.length; i++) {
		
		    dist = euclideanDistance(example,trainData[i]);

			if (dist > 0.0){ //leave-one-out
			
				//see if it's nearer than our previous selected neigbours
				stop=false;
				
				for(int j=0;j<K && !stop;j++){
				
					if (dist < minDist[j]) {
					    
						for (int l = K - 1; l >= j+1; l--) {
							minDist[l] = minDist[l - 1];
							nearestN[l] = nearestN[l - 1];
						}	
						
						minDist[j] = dist;
						nearestN[j] = i;
						stop=true;
					}
				}
			}
			
			//Select if the example would be a nearest neighbour
			if(dist<further[i]){
				selected[i]=true;
			}
			
		}
		
		//Select the neighbours		
		for (int i=0; i<K; i++) {
			selected[nearestN[i]]=true;
		}

		//Voting process
		
		for (int i=0; i<nClasses; i++) {
			votes[i]=0;
		}

		for (int i=0; i<trainData.length; i++) {
			if(selected[i]==true){
				votes[trainOutput[i]]++;
			}
		}	
		
		//Select the final output
		output=-1;
		maxVotes=0;
		
		for(int i=0;i<nClasses;i++){
			
			if(maxVotes<votes[i]){			
				maxVotes=votes[i];
				output=i;
			}
		}
		
		return output;
		
	}//end-method
	
} //end-class 

