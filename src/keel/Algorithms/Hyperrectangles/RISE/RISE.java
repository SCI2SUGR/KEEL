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
 * File: RISE.java
 * 
 * The RISE Algorithm.
 * It induces a list of classification rules unifying two approaches:
 * instance-based learning and rule induction.
 * 
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @author Modified by Joaquin Derrac (University of Granada) 17/10/2009
 * @version 1.2
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Hyperrectangles.RISE;

import java.util.StringTokenizer;

import org.core.*;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Algorithms.Hyperrectangles.Basic.HyperrectanglesAlgorithm;

public class RISE extends HyperrectanglesAlgorithm{
	
	private int Q;  //SVDM parameter
	private int S;	//distance measure. Set S=2 for Euclidean distance
	
	int classVotes[]; 
	Rule ruleset[];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public RISE (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="RISE";
			
		Rule.setQ(Q);
		Rule.setS(S);
		Rule.setSize(inputAtt);
		Rule.setAttributes(inputs);
		Rule.setNClasses(nClasses);
		
		for(int i=0;i<inputAtt;i++){
			if(inputs[i].getType()==Attribute.NOMINAL){
				Rule.setNumValue(Attributes.getInputAttribute(i).getNumNominalValues(),i);
			}
			else{
				Rule.setNumValue(1,i);
			}
		}
		
		Rule.loadSVDMmatrix(trainData,trainOutput);
		
		classVotes=new int[nClasses];
		ruleset=new Rule[trainData.length];
		
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
		
	    file = Fichero.leeFichero (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    //Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();

	    //Getting the Q SVDM parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    Q = Integer.parseInt(tokens.nextToken().substring(1));

	    //Getting the S parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    S = Integer.parseInt(tokens.nextToken().substring(1));

	}//end-method

    /**
     * Extract the rules from the training set. This is the main part of the
     * RISE algorithm.
     */
	public void getRules(){

		boolean improvement;
		int index;
		double minDist,auxDist;
		Rule aux;
		double newDist;
		int points;
		boolean duplicates[];
		int utilRule;
		int pointer;
		Rule newset[];
		
		for(int i=0;i<trainData.length;i++){			
			ruleset[i]=new Rule(trainData[i],trainOutput[i]);		
			computeLaplaceAcc(ruleset[i]);
		}
		
		improvement=true;
		
		while(improvement){
			
			improvement=false;
			
			//For each rule
			for(int i=0;i<ruleset.length;i++){
			
				//find the nearest example of its class not already covered
				index=-1;
				minDist=Double.MAX_VALUE;
				for(int j=0;j<trainData.length;j++){
				
					if(trainOutput[j]==ruleset[i].getOutput()){

						if(ruleset[i].inside(trainData[j])==false){

							auxDist=ruleset[i].distance(trainData[j]);
							
							if(auxDist<minDist){
								minDist=auxDist;
								index=j;
							}
						}
					}		
				}
				
				//if a example is found
				if(index>-1){
					aux=ruleset[i].clone();
					aux.mostSpecificGeneralization(trainData[index]);
								
					computeLaplaceAcc(aux);
					
					//compute accuracy change
					points=0;
					for(int j=0;j<trainData.length;j++){
						
						newDist=aux.distance(trainData[j]);

						if(newDist==0){
							points+=tryClassification(j,i,aux);
						}
					}
					
					//apply changes	
					if(points>=0){
						
						improvement=true;
						
						//change rule[i] for aux
						ruleset[i]=aux.clone();
											
						//discard duplicates
						duplicates=new boolean [ruleset.length];
						utilRule=0;
						
						for(int j=0;j<ruleset.length;j++){
							if((ruleset[j].equals(aux))&&(j!=i)){
								duplicates[j]=true;
															}
							else{
								duplicates[j]=false;
								utilRule++;
							}
						}

						if(utilRule!=ruleset.length){
							
							newset=new Rule[utilRule];
							pointer=0;
							
							for(int j=0;j<ruleset.length;j++){
								
								if(duplicates[j]==false){
									newset[pointer]=ruleset[j].clone();
									pointer++;
								}
								
							}
							
							ruleset=new Rule[utilRule];
							
							for(int j=0;j<ruleset.length;j++){
								ruleset[j]=newset[j].clone();
							}
							
						}//end if-resize
					}
				}//end if-found
			}//end-for		
		}//end-while

	}//end-method

    /**
	 * Tests the classification status of a given instance when an old rule is
     * replaced by a new rule.
	 *
     * @param instance Instance to be tested
     * @param oldRule Rule to be removed
     * @param aux New rule
     * @return 1 if classification is improved with the new rule, -1 if the
     * classification get worse, 0 if it remains equaly.
	 */
	private int tryClassification(int instance,int oldRule,Rule aux){
		
		int oldOutput,newOutput;
		Rule save;

        //get old output
		oldOutput=evaluate(trainData[instance]);
		
		save=ruleset[oldRule].clone();
		ruleset[oldRule]=aux.clone();

        //get new output
		newOutput=evaluate(trainData[instance]);
		
		ruleset[oldRule]=save.clone();
		
		if(oldOutput==trainOutput[instance]){
			if(newOutput==trainOutput[instance]){
				return 0;
			}
			else{
				return -1;
			}
		}
		else{
			if(newOutput==trainOutput[instance]){
				return 1;
			}
			else{
				return 0;
			}			
			
		}
	}//end-method

    /**
	 * Computes the Laplace Accuracy of a rule, as a measure of its quality
	 *
     * @param aux Rule to be analized
	 */
	private void computeLaplaceAcc(Rule aux){
		
		int pos=0;
		double acc;
		
		for(int i=0;i<trainData.length;i++){
			if(aux.inside(trainData[i])){
				pos++;
			}
		}
		
		acc=(double)(pos+1.0)/(double)(trainData.length+nClasses);
		
		aux.setLaplaceAcc(acc);

	}//end-method

    /**
	 * Classifies an instance using the ruleset
	 *
     * @param instance Instance to classify
     * @return Class assigned to the instance
	 */
	protected int evaluate(double [] instance){
		
		int max;
		int maxVotes=Integer.MIN_VALUE;
		double maxAcc=Double.MIN_VALUE;
		int selected=-1;
		boolean draw;
		double minDist=Double.MAX_VALUE;
		
		draw=false;
		for(int i=0;i<ruleset.length;i++){
			if(ruleset[i].distance(instance)==minDist){
				if(ruleset[i].getLaplaceAcc()>maxAcc){
					maxAcc=ruleset[i].getLaplaceAcc();
					selected=i;
					draw=false;
				}
				if(ruleset[i].getLaplaceAcc()==maxAcc){
					draw=true;
				}
			}
			if(ruleset[i].distance(instance)<minDist){
				minDist=ruleset[i].distance(instance);
				maxAcc=ruleset[i].getLaplaceAcc();
				selected=i;
				draw=false;
			}
		}		
		
		selected=ruleset[selected].getOutput();
	
		if(draw){
			for(int i=0;i<nClasses;i++){
				classVotes[i]=0;
			}

			for(int i=0;i<ruleset.length;i++){
				if(ruleset[i].distance(instance)==minDist){
					if(ruleset[i].getLaplaceAcc()==maxAcc){
						classVotes[ruleset[i].getOutput()]++;
					}
				}
			}	
			
			max=-1;
			for(int i=0;i<nClasses;i++){
				if(maxVotes<classVotes[i]){
					max=classVotes[i];
					max=i;
				}
			}
			selected=max;
		}
		
		return selected;

	}//end-method

	/** 
	 * Writes the final ruleset obtained, in the ruleSetText variable.
	 * 
	 * @return The number of rules of the final rule set
	 */
	protected int writeRules(){
		
		String text="";
		
		text+="\n";
		
		for(int i=0;i<ruleset.length;i++){
			text+="\n";
			text+=ruleset[i];
		}
		
		ruleSetText=text;
		
		return ruleset.length;
	}
   
} //end-class 

