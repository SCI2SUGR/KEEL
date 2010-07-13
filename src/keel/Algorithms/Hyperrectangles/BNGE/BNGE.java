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
 * File: BNGE.java
 *
 * The BNGE Algorithm.
 * The algorithm tries to show that is enough to consider a small neighbourhood
 * to achieve classification accuracy comparable to an algorithm considering the
 * whole learning set, combining this k-nearest neighbours method and a
 * rule-based algorithm.
 *
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @author Modified by Joaquin Derrac (University of Granada) 17/10/2009
 * @version 1.2
 * @since JDK1.5
 *
 */

package keel.Algorithms.Hyperrectangles.BNGE;

import java.util.StringTokenizer;

import org.core.*;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Algorithms.Hyperrectangles.Basic.HyperrectanglesAlgorithm;

public class BNGE extends HyperrectanglesAlgorithm{
	
	Rule ruleset[];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public BNGE (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="BNGE";
			
		Rule.setSize(inputAtt);
		Rule.setAttributes(inputs);
		
		for(int i=0;i<inputAtt;i++){
			if(inputs[i].getType()==Attribute.NOMINAL){
				Rule.setNumValue(Attributes.getInputAttribute(i).getNumNominalValues(),i);
			}
			else{
				Rule.setNumValue(1,i);
			}
		}

		ruleset=new Rule[trainData.length];
		
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

        file = Fichero.leeFichero (script);
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
        
	}//end-method

    /**
     * Extract the rules from the training set. This is the main part of the
     * BNGE algorithm.
     */
	public void getRules(){
		
		int randomIndex[]=new int [trainData.length];
		int pos,tmp;
		boolean canMerge,testing;
		int indexMerge;
		double distMerge,auxDist;
		Rule newRule;
		Rule newSet [];

        //random initialization of the ruleset
	    for (int i=0; i<trainData.length; i++){
	    	randomIndex[i] = i;
	    }

	    for (int i=0; i<trainData.length; i++) {
	    	
	    	pos = Randomize.Randint (0, trainData.length);
	    	tmp = randomIndex[i];
	    	randomIndex[i] = randomIndex[pos];
	    	randomIndex[pos] = tmp;
	    }
	
		for(int i=0;i<trainData.length;i++){			
			ruleset[i]=new Rule(trainData[randomIndex[i]],trainOutput[randomIndex[i]]);		
		}

        //merging process
		canMerge=true;
		while(canMerge){
			canMerge=false;
			
			for(int i=0;i<ruleset.length&& !canMerge;i++){
				
				//find their nearest hyperrectangle
				indexMerge=-1;
				distMerge=Double.MAX_VALUE;
				for(int j=i+1;j<ruleset.length;j++){
					if(ruleset[i].getOutput()==ruleset[j].getOutput()){
						auxDist=ruleset[i].distanceRule(ruleset[j]);
						if(distMerge>auxDist){
							distMerge=auxDist;
							indexMerge=j;
						}
					}
				}
				
				//try to merge
				if(indexMerge>-1){
					
					newRule=ruleset[i].clone();					
					newRule.merge(ruleset[indexMerge]);
					
					testing=true;
					for(int j=0;j<ruleset.length&&testing;j++){
						
						if((j!=i)&&(j!=indexMerge)&&(newRule.getOutput()!=ruleset[j].getOutput())){
							if(newRule.overlap(ruleset[j])){
								testing=false;
							}				
						}
					}
					
					if(testing){
						
						ruleset[i]=newRule.clone();
						
						newSet=new Rule[ruleset.length-1];
						
						System.arraycopy(ruleset, 0, newSet, 0, indexMerge);
						System.arraycopy(ruleset, indexMerge+1, newSet, indexMerge, (ruleset.length-indexMerge-1));
						
						ruleset=new Rule[newSet.length];
						
						System.arraycopy(newSet, 0, ruleset, 0, newSet.length);
						
						canMerge=true;
					}
				}
			}

		}//end-while

	}//end-method

    /**
	 * Classifies an instance using the ruleset
	 *
     * @param instance Instance to classify
     * @return Class assigned to the instance
	 */
	protected int evaluate(double instance[]){
		
		double minArea=Double.MAX_VALUE;
		double minDist=Double.MAX_VALUE;
		int selected=-1;
		
		for(int i=0;i<ruleset.length;i++){
			if(ruleset[i].distance(instance)==minDist){
				if(ruleset[i].getArea()<minArea){
					minArea=ruleset[i].getArea();
					selected=i;
				}
			}
			if(ruleset[i].distance(instance)<minDist){
				minDist=ruleset[i].distance(instance);
				minArea=ruleset[i].getArea();
				selected=i;
			}
		}		
		
		selected=ruleset[selected].getOutput();
		
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
	}//end-method
   
} //end-class 

