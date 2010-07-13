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
 * File: LBR.java
 * 
 * The LBR Algorithm.
 * A Lazy Approach of the Naive Bayes Classifier. It tries to find
 * the optimal subset of features and instances to use to build an
 * ad-hoc NB Classifier for each testing instance.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Lazy_Learning.LBR;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;
import keel.Algorithms.Lazy_Learning.Statistics;

public class LBR extends LazyAlgorithm{
		
	int [] query;
	private static final double SIGVALUE = 0.05;
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public LBR (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="LBR";

		//Inicialization of auxiliar structures
		
		BayesianC.setNClasses(nClasses,inputAtt);
		BayesianC.setNumValues();
		
		for(int i=0;i<inputAtt;i++){
			BayesianC.setNumValue(train.getAttributeDefinitions().getInputAttribute(i).getNumNominalValues(),i);
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
		//No parameters to read in this algorithm
	} //end-method 
		
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted 
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output=-1;
		boolean finished;
		BayesianC classifier;
		int tempError;
		int bestError;
		int bestAtt;
		int oldError;
		
		finished=false;

		createQuery(example);
		
		//create the initial classifier
		classifier=new BayesianC(trainData,trainOutput);
		classifier.calcProbabilities();
		
		//get Loo error
		classifier.doLeaveOneOut();

		
		while(!finished){
			bestError=Integer.MAX_VALUE;
			bestAtt=-1;
			
			//Find the attribute with lesser error.
			for(int i=0;i<query.length;i++){
				
				tempError=classifier.tempClassifier(i,query[i]);
				
				if(bestError>=tempError){
					bestError=tempError;
					bestAtt=i;
				}
			}
			if(bestAtt!=-1){
				
				oldError=classifier.getOldError(bestAtt,query[bestAtt]);
				
				//Test if the attribute selected reduces significatively the error
				if( (classifier.looError()>(bestError+oldError))&&(statTest(bestError,bestError+oldError)<=SIGVALUE)){	

					//prune the train set			
					classifier.prune(bestAtt,query[bestAtt]);				
					classifier.doLeaveOneOut();
					
					//prune the query
					pruneQuery(bestAtt);
				}
				else{
					
					//If differences are not significative, finish
					finished=true;
				}
			}
			else{
				//If no attribute reduces the error
				finished=true;
			}
		}
		
		//Classify the initial query
		output=classifier.classify(query);
	
		return output;
	} //end-method 

	/** 
	 * Creates the query in integer representation
	 * 
	 * @param example Instance evaluated 
	 * 
	 */
	private void createQuery(double [] example){
		
		query=new int [example.length];
		
		for(int i=0;i<example.length;i++){
			query[i]=(int)(example[i]*(double)(BayesianC.getNumValue(i)-1));
		}
		
	} //end-method 
	
	/** 
	 * Prunes the query, erasing the attribute selected
	 * 
	 * @param attribute Attribute to be pruned
	 * 
	 */
	private void pruneQuery(int bestAtt){
		
		int [] newQuery;
		
		newQuery=new int [query.length-1];
		
		System.arraycopy(query, 0, newQuery, 0, bestAtt);
		if(bestAtt!=query.length-1){
			System.arraycopy(query, bestAtt+1, newQuery, bestAtt,query.length-(bestAtt+1));
		}
		
		query=new int [newQuery.length];
		System.arraycopy(newQuery, 0, query, 0, newQuery.length);

	} //end-method 
	
	
	/** 
	 * Performs a statistical test to decide if new error rate
	 * is significatively lower than old error rate
	 * 
	 * @param errorsNew New error rate
	 * @param errorsOld Old error rate
	 * 
	 * @return P-Value obtained 
	 * 
	 */
	
	private double statTest(int errorsNew,int errorsOld){
		
		binomP((double)errorsNew, (double)(errorsNew+ errorsOld), 0.5 );
		
		return 0.0;
	} //end-method 
	
	/** 
	 * Performs a binomial test
	 * 
	 * @param r R value
	 * @param  n N Value
	 * @param p Probability
	 * 
	 * @return P-Value obtained 
	 * 
	 */
	public double binomP(double r, double n, double p){

		if (n == r) return 1.0;
		    return Statistics.incompleteBeta(n-r, r+1.0, 1.0-p);
	} //end-method 
	
} //end-class 

