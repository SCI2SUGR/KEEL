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
 * File: CPW.java
 * 
 * Class and prototipe weigthed learning.
 * A 1-Nearest Neighbor classifier which used weights to improve its 
 * efectiveness. Uses class weights and prototipes weights
 * 
 * @author Written by Joaquín Derrac (University of Granada) 15/11/2008 
 * @version 1.0 
 * @since JDK1.5
 * 
 */
package keel.Algorithms.Lazy_Learning.CPW;

import keel.Algorithms.Lazy_Learning.LazyAlgorithm;

import java.util.*;
import org.core.Files;


public class CPW extends LazyAlgorithm{
	
	//Parameters
	
	double BETA;
	double MU;
	double RO;
	double epsilon;
	
	//Adictional structures
	
	double pWeights[];
	double cWeights[][];

	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public CPW (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="CPW";    
		
		//Inicialization of auxiliar structures
	    
	    pWeights=new double[trainData.length];
		cWeights=new double[nClasses][inputAtt];
		
		for(int i=0;i<pWeights.length;i++){
			pWeights[i]=1.0;
		}
		
		for(int i=0;i<cWeights.length;i++){
			for(int j=0;j<inputAtt;j++){	
				cWeights[i][j]=1.0;			
			}			
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
		StringTokenizer fileLines, tokens;
		
	    file = Files.readFile (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    
	    //Getting the Beta parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    BETA = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the Mu parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    MU = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the Ro parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    RO = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the epsilon parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    epsilon = Double.parseDouble(tokens.nextToken().substring(1));
	    
	} //end-method
	

	/** 
	 * Algorithm to calculate weights.
	 * 
	 */
	public void calculateWeights(){
		
		double error;
		double errorAnterior;
		double Q;
		double ratio;
		int same;
		int diff;
		int classSame;
		int classDiff;
		double distSame;
		double distDiff;		
		
		errorAnterior=Double.MAX_VALUE;
		error=errorEstimation();

		while(Math.abs(error-errorAnterior)> epsilon){
			
			errorAnterior=error;
			
			for(int i=0;i<trainData.length;i++){
				
				same=findEqual(i);
				diff=findNoEqual(i);
				
				classSame=trainOutput[same];
				classDiff=trainOutput[diff];
				distSame=weightedDistance(trainData[i],same);
				distDiff=weightedDistance(trainData[i],diff);
				
				ratio=distSame/distDiff;
				
				Q=derivativeSigmoid(ratio)*ratio;
				
				pWeights[same]=pWeights[same]-((RO*Q)/pWeights[same]);
				pWeights[diff]=pWeights[diff]+((RO*Q)/pWeights[diff]);
				
				for(int j=0; j<inputAtt; j++){
				
					cWeights[classSame][j]=cWeights[classSame][j]-(MU*Q*ratio(i,same,j,distSame)*cWeights[classSame][j]);
					cWeights[classDiff][j]=cWeights[classDiff][j]+(MU*Q*ratio(i,diff,j,distDiff)*cWeights[classDiff][j]);
				}
				
			}
			
			error=errorEstimation();
			
		}
		
	}//end-method

	/** 
	 * Estimates actual classification error.
	 * 
	 * @return Error estimated
	 *  
	 */
	private double errorEstimation(){
		
		double total=0.0;
		double distance1;
		double distance2;
		int equal;
		int notEqual;
		
		for(int i=0;i<trainData.length;i++){
		
			equal=findEqual(i);
			notEqual=findNoEqual(i);
			
			distance1=weightedDistance(trainData[i],equal);
			distance2=weightedDistance(trainData[i],notEqual);

			total+=sigmoid(distance1/distance2);	
			
		}
		
		total /=trainData.length;
		
		return total;
		
	}//end-method

	/** 
	 * Sigmoid function.
	 * 
	 * @param z Z value of sigmoid
	 * @return Value of the sigmoid
	 * 
	 */
	private double sigmoid(double z) {
		
		return 1.0 / (1.0 + Math.exp(BETA*(1-z)));
		
	}//end-method

	/** 
	 * Derivative sigmoid function.
	 * 
	 * @param z Z value of derivative sigmoid
	 * @return Value of the derivative sigmoid
	 * 
	 */
	private double derivativeSigmoid(double z) {

		double total;
		double up;
		double down;
		
		up= BETA*Math.exp(BETA*(1-z));
		
		down= 1.0 + Math.exp(BETA*(1-z));
		down= down*down;
		
		total= up/down;
		
		return total;
		
	}//end-method

	/** 
	 * Calculates change ratio between two features.
	 * 
	 * @param instance1 Index of first instance
	 * @param instance2 Index of second instance
	 * @param feature Attibute to be tested
	 * @param dist Distance between both instances
	 * @return Value of the ratio
	 * 
	 */
	private double ratio(int instance1,int instance2,int feature,double dist){
		
		double up=trainData[instance1][feature]-trainData[instance2][feature];
		
		return ((up*up)-(dist*dist));
		
	}//end-method

	/** 
	 * Find nearest instance of the same class.
	 * 
	 * @param instance Index of instance
	 * @return Index to the instance
	 * 
	 */
	private int findEqual(int instance){
		
		double distance=Double.MAX_VALUE;
		double aux;
		int insClass=trainOutput[instance];
		int selected=0;
		
		for(int i=0;i<trainData.length;i++){
		
			if(trainOutput[i]==insClass){
				if(i!=instance){
					
					aux=weightedDistance(trainData[i],instance);
					
					if(distance>aux){					
						distance=aux;
						selected=i;
					}
				}
			}
		}
		
		return selected;
		
	}//end-method
	
	/** 
	 * Find nearest instance of different class.
	 * 
	 * @param instance Index of instance
	 * @return Index to the instance
	 * 
	 */
	private int findNoEqual(int instance){
		
		double distance=Double.MAX_VALUE;
		double aux;
		int insClass=trainOutput[instance];
		int selected=0;
		
		for(int i=0;i<trainData.length;i++){
		
			if(trainOutput[i]!=insClass){

				aux=weightedDistance(trainData[i],instance);
				
				if(distance>aux){					
					distance=aux;
					selected=i;
				}
			}
		}
		
		return selected;
		
	}//end-method
	
	/** 
	 * Evaluates a instance to predict its class.
	 * 
	 * @param example Instance evaluated 
	 * @return Class predicted 
	 * 
	 */
	protected int evaluate (double example[]) {
		
		int output=0;
		double aux;
		double min=Double.MAX_VALUE;
		
		//1-NN rule
		for(int i=0;i<trainData.length;i++){
			
			aux=weightedDistance(example,i);
			
			if((aux<min)&&(aux!=0.0)){//leave one out
				min=aux;
				output=i;
			}
		}
		
		output=trainOutput[output];
		
		return output;
		
	}//end-method
	
	/** 
	 * Calculates euclidean weighted distance between a test instance
	 * and a train instance
	 * 
	 * @param example Test instance
	 * @param reference Index of train instance
	 * @return Distance calculated 
	 * 
	 */
	private double weightedDistance(double example [], int reference){
		
		double dist=0.0;
		double aux;
		double a,b,c;
		
		int referenceClass=trainOutput[reference];
		
		for(int i=0;i<inputAtt;i++){
	
			a=example[i];
			b=trainData[reference][i];
			c=a-b;
			
			aux=cWeights[referenceClass][i]*c*c;
	
			dist+=aux;

		}

		dist=pWeights[reference]*Math.sqrt(dist);

		return dist;
		
	}//end-method
	
} //end-class 


