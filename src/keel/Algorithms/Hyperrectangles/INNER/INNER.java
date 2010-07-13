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
 * File: INNER.java
 * 
 * The INNER Algorithm.
 * It proceeds by selecting a random set of initial examples and inflating them to rules.
 * The rules obtained are postprocessed to obtain a suitable set, read to classify
 * new instaces based on both rule and distances approaches.
 * 
 * @author Written by Joaquin Derrac (University of Granada) 8/7/2009
 * @version 1.1
 * @since JDK1.5
 * 
 */

package keel.Algorithms.Hyperrectangles.INNER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import org.core.*;

import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Algorithms.Hyperrectangles.Basic.HyperrectanglesAlgorithm;

public class INNER extends HyperrectanglesAlgorithm{
	
	private int initialInstances;
	private int maxCycles;
	private int cycles;
	private int minExamples;
	private int minPresentations;
	private int presentations;
	private int regularize;
	private double minCoverage;
	private double selectThreshold;
	private boolean covered [];
	private boolean instancesSelected [];
	private Rule newRules[];
	private Rule ruleset[];
	
	/** 
	 * The main method of the class
	 * 
	 * @param script Name of the configuration script  
	 * 
	 */
	public INNER (String script) {
		
		readDataFiles(script);
		
		//Naming the algorithm
		name="INNER";
			
		denormalizeData();
		Rule.setSize(inputAtt);
		Rule.copyData(trainData,trainOutput);
		Rule.setNClasses(nClasses);
		Rule.setNInstances(nInstances);
		
		for(int i=0;i<inputAtt;i++){
			if(inputs[i].getType()==Attribute.NOMINAL){
				Rule.setNumValue(Attributes.getInputAttribute(i).getNumNominalValues(),i);
			}
			else{
				Rule.setNumValue(0,i);
			}
		}
		
		Rule.computeDefaultImpurityLevels();
		
		
		covered= new boolean [trainData.length];
		ruleset= new Rule[0];
		instancesSelected= new boolean [trainData.length];
		cycles=0;
		
		minExamples=Integer.MAX_VALUE;
		
		for(int i=0;i<nInstances.length;i++){
			if((nInstances[i]<minExamples)&&(nInstances[i]!=0)){
				minExamples=nInstances[i];
			}
		}
		
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
		
	    file = Files.readFile(script);
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

	    //Getting the Initial instances parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    initialInstances = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the Max Cycles parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    maxCycles = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the Min Coverage parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    minCoverage = Double.parseDouble(tokens.nextToken().substring(1));
	    
	    //Getting the Min Presentations parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    minPresentations = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the regularize parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    regularize = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    //Getting the select threshold parameter
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    selectThreshold = Double.parseDouble(tokens.nextToken().substring(1));

	}//end-method
	
	/**
	* The core of INNER algorithm. It obtains the rules needed to clasify. 
	*/
	public void getRules(){

		Arrays.fill(covered, false);

		while(continueInflating()){

			newRules=findBestRules();
			
			ruleset=merge(ruleset,newRules);
			computeCovered();
			cycles+=1;

		}
		
	}//end-method
	
	/**
	* Prints the current ruleset selected
	*/
	private void printRuleset(){
		System.out.println("********************************************");
		for(int i=0;i<ruleset.length;i++){
			System.out.println(ruleset[i]);
		}
		
	}//end-method 
	
	/**
	* Mark which instances are already covered by the rule set.
	*/
	private void computeCovered(){
		
		Arrays.fill(covered, false);
		
		for(int i=0;i <trainData.length;i++){
			
			for(int j=0;j<ruleset.length&&!covered[i];j++){
				if(trainOutput[i]==ruleset[j].getOutput()){
					covered[i]=ruleset[j].inside(trainData[i]);
				}
			}
		}
	}//end-method 
	
	/**
	* Test if it is necessary to continue the inflating process
	*
	* @return True to continue, false if not
	*/
	private boolean continueInflating(){
		
		if(cycles>=maxCycles){
			return false;
		}
		
		if(coverage()>minCoverage){
			return false;
		}
		
		return true;
		
	}//end-method 
	
	/**
	* Computes the coverage rate of the current rule set
	*
	* @return Coverage rate
	*/	
	private double coverage(){
		
		double count=0.0;
		
		for(int i=0;i<covered.length;i++){
			if(covered[i]){
				count+=1.0;
			}
		}
		
		count=count/(double)covered.length;
		
		return count;
	}//end-method 
	
	/**
	* Merge two rule sets.
	*
	* @param a First rule set
	* @param b Second rule set
	*
	* @return Final rule set
	*/	
	private Rule [] merge(Rule a[],Rule b[]){
		
		Rule result []= new Rule [((a.length)+(b.length))];
		
		for(int i=0;i<a.length;i++){
			result[i]=a[i].clone();
		}
		for(int i=0;i<b.length;i++){
			result[i+a.length]= b[i].clone();
		}
	
		return result;
	}//end-method 
	
	/**
	* Gets the initial random subsets of rules from the training data
	*
	*
	* @return Initial rule set
	*/	
	private Rule [] findBestRules(){
		
		int number;
		Rule bestRules[];
		Rule finalRules[];
		int indexes [];
		int needed[]= new int [nClasses];
		int output;
		int pointer;
		int nSelected=0;
		
		//select initial instances
		
		number=selectNumberOfExamples();
		Arrays.fill(needed, number);

		indexes=generateIndex(instancesSelected.length);
		
		for(int i=0;i<instancesSelected.length;i++){
			
			pointer=indexes[i];
			output=trainOutput[pointer];
			if((needed[output]>0)&&(covered[pointer]==false)){
				instancesSelected[pointer]=true;
				needed[output]--;
				nSelected++;
			}
			else{
				instancesSelected[pointer]=false;
			}
		}
		
		bestRules= new Rule[nSelected];
		
		pointer=0;
		for(int i=0;i<instancesSelected.length;i++){
			
			if(instancesSelected[i]){
				bestRules[pointer]=new Rule(trainData[i],trainOutput[i]);
				pointer++;
			}
		}

		bestRules=generalizeInstances(bestRules);

		finalRules=pruneConditions(bestRules);

		
		return finalRules;

		
	}//end-method 
	
	/**
	* Generalizes the initial rules, by inflating them trying to cover all the instances in the training set 
	*
	* @param bestRules Initial rule set
	*
	* @return Final rule set
	*/	
	private Rule [] generalizeInstances(Rule [] bestRules){
		
		int indexes [];
		int instance;
		int nearestRule;
		double learningRate, sigmoid, percentage;
		double distance;	
		Rule otherRules [];
		
		presentations=0;
		indexes=new int [trainData.length];
		otherRules=new Rule [bestRules.length];
		
		for(int i=0;i<bestRules.length;i++){
			otherRules[i]=bestRules[i].clone();
		}
		
		while(presentations<minPresentations){

			indexes=generateIndex(indexes.length);
			
			for(int i=0;i< indexes.length;i++){
				
				instance=indexes[i];
				nearestRule=findNearestRule(trainData[instance],otherRules);
			
				//generalize rule
				
				distance=otherRules[nearestRule].distance(trainData[instance]);
				
				//compute general terms (numerical)
				learningRate=0.75*(1.0+((double)presentations/(double)minPresentations));
				sigmoid=1.0/(1.0+(Math.pow(Math.E, 20.0*distance-5.0)));
				percentage=learningRate*sigmoid; 
			
				
				//compute general terms (nominal)
				learningRate=0.675*(1.0-((double)presentations/(double)minPresentations));
				sigmoid=1.0/(1.0+(Math.pow(Math.E, 3.0*distance-5.0)));
				percentage=learningRate*sigmoid; 
				
				for(int j=0;j<trainData[instance].length;j++){
					
					//generalize nominal attribute
					if(inputs[j].getType()==Attribute.NOMINAL){
						
						otherRules[nearestRule].generalizeNominal(j,trainData[instance][j],percentage,trainOutput[instance]);

					}
					//generalize continuous attribute
					else{
						otherRules[nearestRule].generalizeContinuous(j,trainData[instance][j],percentage,trainOutput[instance]);
					}
				}

				if(presentations<minPresentations){
					presentations++;
				}
				
				//regularize
				if(presentations%regularize==0){
					
					for(int k=0;k<otherRules.length;k++){
						
						otherRules[k].computeImpurityLevel();
						
						if(bestRules[k].getImpurityLevel()>otherRules[k].getImpurityLevel()){			
							bestRules[k]=otherRules[k].clone();						
						}			
					}
					
					//get the new set of rules
					for(int k=0;k<bestRules.length;k++){
						otherRules[k]=bestRules[k].clone();
					}
				}
				
			}//end-for

		}//end-while
		
		//regularize

			
		for(int k=0;k<otherRules.length;k++){
				
			otherRules[k].computeImpurityLevel();
				
			if(bestRules[k].getImpurityLevel()>otherRules[k].getImpurityLevel()){
				bestRules[k]=otherRules[k].clone();
			}
				
		}
		
		return bestRules;
	}//end-method 
	
	/**
	* Finds the nearest rule of an example
	*
	* @param example A instance
	* @param rules Current rule set
	*
	* @return Identifier of the nearest rule
	*/
	private int findNearestRule(double example[],Rule rules []){
		
		double aux;
		int result=-1;
		double dist= Double.MAX_VALUE;
		double impurity= Double.MAX_VALUE;
		
		for(int i=0;i<rules.length;i++){
			
			aux=rules[i].distance(example);
			
			if(dist>aux){
				dist=aux;
				impurity=rules[i].getImpurityLevel();
				result=i;
			}
			if((dist==aux)&&(impurity>rules[i].getImpurityLevel())){
				dist=aux;
				impurity=rules[i].getImpurityLevel();
				result=i;				
			}
		}
		
		return result;
		
	}//end-method 
	
	/**
	* Finds the nearest rule of an example, without considering a given rule
	*
	* @param example A instance
	* @param rules Current rule set
	* @param rule Rule to be avoided
	*
	* @return Identifier of the nearest rule
	*/	
	private int findNearestRuleWithout(double example[],Rule rules [],int rule){
		
		double aux;
		int result=-1;
		double dist= Double.MAX_VALUE;
		double impurity= Double.MAX_VALUE;
		
		for(int i=0;i<rules.length;i++){
			
			aux=rules[i].distance(example);
			if(i==rule){
				aux=Double.MAX_VALUE;
			}
			if(dist>aux){
				dist=aux;
				impurity=rules[i].getImpurityLevel();
				result=i;
			}
			if((dist==aux)&&(impurity>rules[i].getImpurityLevel())){
				dist=aux;
				impurity=rules[i].getImpurityLevel();
				result=i;				
			}
		}
		
		return result;
		
	}//end-method 
	
	/**
	* Prune conditions of the current rule set, to improve its generalizatio capabilities
	*
	* @param bestRules Current rule set
	*
	* @return Final rule set
	*/		
	private Rule [] pruneConditions(Rule [] bestRules){
		
		boolean copy[];
		int size,pointer=0;
		Rule prunedRules[];
		
		for(int i=0;i<bestRules.length;i++){
			bestRules[i].pruneConditions();
		}
		
		size=bestRules.length;
		copy=new boolean[size];
		Arrays.fill(copy, true);
		
		//remove inner rules
		for(int i=0;i<bestRules.length;i++){
			for(int j=0;j<bestRules.length&&copy[j];j++){
				if((i!=j)&&(bestRules[i].contains(bestRules[j]))){
					copy[j]=false;
					size--;
				}
			}
		}
		
		prunedRules=new Rule[size];
		
		for(int i=0;i<bestRules.length;i++){
			if(copy[i]){
				prunedRules[pointer]=bestRules[i].clone();
				pointer++;
			}
		}
		
		return prunedRules;

	}//end-method 
	
	/**
	* Generates a randomized list of indexes, from 0 to an especified limit 
	*
	* @param top Limit (not included)
	*
	* @return The list of indexes
	*/	
	private int [] generateIndex(int top){
		
		int indexes [] = new int [top];
		int aux1,aux2,aux;
		
		for(int i=0;i<top;i++){
			indexes[i]=i;
		}
		
		for(int i=0;i<top;i++){
			
			aux1=Randomize.Randint(0, top);
			aux2=Randomize.Randint(0, top);
			
			aux=indexes[aux1];
			indexes[aux1]=indexes[aux2];
			indexes[aux2]=aux;
		}
		
		return indexes;
		
	}//end-method 
	
	/**
	* Computes the number of examples to be selected
	*
	* @return Number of examples
	*/	
	private int selectNumberOfExamples(){
		
		int result;
		int value;
		
		value= (int)Math.floor(Math.pow(Math.E,(double)minExamples/(50.0/(Math.log(10.0)))));
		if(value<1){
			value=1;
		}
		
		result=Math.min(initialInstances, value);
		
		return result;
		
	}//end-method 
	
	/**
	* Performs the postprocessing phase of INNER
	*
	*/
	public void postProcess(){
		
		firstGeneralize();
		selection();
		secondGeneralize();
		computeCovered();
		if(coverage()<100.0){
			finalCoverage();
		}
		
	}//end-method
	
	/**
	* First generalization process. It tryes to extend the rules, without allowing intersections
	*
	*/
	private void firstGeneralize(){

		ArrayList<Pair> list;
		Pair actual;
		boolean intersect;
		Rule aux[];
		Rule extended;
		int index;
		
		for(int k=0;k<nClasses;k++){
			list=computeExtensibleList(k);
			
			for(int i=0;i<list.size();i++){
				actual=list.get(i);
				extended=ruleset[actual.A()].testExtension(ruleset[actual.B()]);
				if(extended!=null){
					intersect=false;
					for(int l=0;l<ruleset.length&&!intersect;l++){
						if(extended.getOutput()!=ruleset[l].getOutput()){
							intersect=extended.intersect(ruleset[l]);
						}
					}
					if(!intersect){
						
						//extend rule
						ruleset[actual.A()]=extended.clone();
						
						//eliminar la interior
						if(extended.contains(ruleset[actual.B()])){
							index=actual.B();
							aux= new Rule [ruleset.length];
							System.arraycopy(ruleset, 0, aux, 0, ruleset.length);
							ruleset= new Rule [aux.length-1];
							for(int pointer=0;pointer<index;pointer++){
								ruleset[pointer]=aux[pointer].clone();
							}
							for(int pointer=index;pointer<ruleset.length;pointer++){
								ruleset[pointer]=aux[pointer+1].clone();
							}
							list=computeExtensibleList(k);
							i=0;
						}

					}
					
				}
				
			}

		}//end-for class
		
	}//end-method	
	
	/**
	* Computes the list of extensible pairs of rules.
	*
	* @param clas Class of the rules
	*
	* @return List of extensible pairs
	*/
	@SuppressWarnings("unchecked")
	private ArrayList<Pair> computeExtensibleList(int clas){
		
		ArrayList<Pair> list= new ArrayList<Pair>();
		
		for(int i=0;i<ruleset.length;i++){
			
			if(ruleset[i].getOutput()==clas){
				for(int j=i+1;j<ruleset.length;j++){
					if(ruleset[j].getOutput()==clas){
					if(extensible(ruleset[i],ruleset[j])){
						list.add(new Pair(i,j,ruleset[i].ruleDistance(ruleset[j])));
					}
					}
				}
			}//end-if
		}//end-for rules (A)
		
		Collections.sort(list);
		
		return list;
		
	}//end-method 
	
	/**
	* Test if a given pair of rules is extensible or not
	*
	* @param a First rule
	* @param b Second rule
	*
	* @return True if the pair is extensible. False, if not.
	*/
	private boolean extensible(Rule a, Rule b){
		
		int fails=0;
		
		for(int i=0;i<inputAtt&&fails<2;i++){
			
			if(a.inclusionDegree(b, i)>0.0){
				fails++;
			}
		}
		
		if(fails<2){
			return true;
		}
		else{
			return false;
		}
		
	}//end-method 

	/**
	* Removes form the current rule set those rules with an higher impurity level, if 
	* the classification rates do not became lower.
	*/
	@SuppressWarnings("unchecked")
	private void selection(){

		int hitsWith;
		int hitsWithout;
		Rule aux[];
		
		Arrays.sort(ruleset,ruleset[0]);

		hitsWith=hitsTraining();
		for(int i=0;i<ruleset.length;i++){
			
			if(ruleset[i].getImpurityLevel()>selectThreshold){
				hitsWithout=hitsTrainingWithout(i);
				if(hitsWith<=hitsWithout){
					
					aux= new Rule [ruleset.length];
					System.arraycopy(ruleset, 0, aux, 0, ruleset.length);
					ruleset= new Rule [aux.length-1];
					for(int pointer=0;pointer<i;pointer++){
						ruleset[pointer]=aux[pointer].clone();
					}
					for(int pointer=i;pointer<ruleset.length;pointer++){
						ruleset[pointer]=aux[pointer+1].clone();
					}
					hitsWith=hitsWithout;
					i--;
				}		
			}
		}
	}//end-method	
	
	/**
	* Computes the number of hits with the current rule set.
	*
	* @return Number of hits.
	*/
	private int hitsTraining(){
		
		int hits=0;
		
		for(int i=0;i<trainData.length;i++){
			if(ruleset[(findNearestRule(trainData[i], ruleset))].getOutput()==trainOutput[i]){
				hits++;
			}
		}
		
		return hits;
	}//end-method 
	
	/**
	* Computes the number of hits with the current rule set, excluding a given rule.
	*
	* @param Rule to be avoided
	* @return Number of hits.
	*/
	private int hitsTrainingWithout(int rule){
		
		int hits=0;
		
		for(int i=0;i<trainData.length;i++){
			if(ruleset[(findNearestRuleWithout(trainData[i], ruleset,rule))].getOutput()==trainOutput[i]){
				hits++;
			}
		}
		
		return hits;
	}//end-method 
	
	/**
	* Second generalization process. It tryes to extend the rules, allowing intersections
	*
	*/
	private void secondGeneralize(){
		ArrayList<Pair> list;
		Pair actual;
		Rule aux[];
		Rule extended;
		int index;
		
		for(int k=0;k<nClasses;k++){
			list=computeExtensibleList(k);
			
			for(int i=0;i<list.size();i++){
				actual=list.get(i);
				extended=ruleset[actual.A()].testExtension(ruleset[actual.B()]);
				if(extended!=null){
					//extend rule
					ruleset[actual.A()]=extended.clone();
						
					//eliminar la interior
					if(extended.contains(ruleset[actual.B()])){
						index=actual.B();
						aux= new Rule [ruleset.length];
						System.arraycopy(ruleset, 0, aux, 0, ruleset.length);
						ruleset= new Rule [aux.length-1];
						for(int pointer=0;pointer<index;pointer++){
							ruleset[pointer]=aux[pointer].clone();
						}
						for(int pointer=index;pointer<ruleset.length;pointer++){
							ruleset[pointer]=aux[pointer+1].clone();
						}
						list=computeExtensibleList(k);
						i=0;
					}

				}
					
			}

		}//end-for class
		
	}//end-method
	
	/**
	* Performs a final inflating process. This time, the impurity level of the rules
	* is computed employing only the instances already covered by each rule.
	*
	*/
	private void finalCoverage(){
		
		int indexes [];
		int instance;
		int nearestRule;
		double learningRate, sigmoid, percentage;
		double distance;	
		Rule otherRules [];
		Rule bestRules [];
		
		bestRules=new Rule [ruleset.length];
		for(int i=0;i<ruleset.length;i++){
			bestRules[i]=ruleset[i].clone();
		}
		
		presentations=0;
		indexes=new int [trainData.length];
		otherRules=new Rule [bestRules.length];
		
		for(int i=0;i<bestRules.length;i++){
			otherRules[i]=bestRules[i].clone();
		}
		
		while(presentations<minPresentations){

			indexes=generateIndex(indexes.length);
			
			for(int i=0;i< indexes.length;i++){
				
				instance=indexes[i];
				nearestRule=findNearestRule(trainData[instance],otherRules);
			
				//generalize rule
				
				distance=otherRules[nearestRule].distance(trainData[instance]);
				
				//compute general terms (numerical)
				learningRate=0.75*(1.0+((double)presentations/(double)minPresentations));
				sigmoid=1.0/(1.0+(Math.pow(Math.E, 20.0*distance-5.0)));
				percentage=learningRate*sigmoid; 
			
				//compute general terms (nominal)
				learningRate=0.675*(1.0-(presentations/minPresentations));
				sigmoid=1.0/(1.0+(Math.pow(Math.E, 10.0*distance-5.0)));
				percentage=learningRate*sigmoid; 
				
				for(int j=0;j<trainData[instance].length;j++){
					
					//generalize nominal attribute
					if(inputs[j].getType()==Attribute.NOMINAL){
						
						otherRules[nearestRule].generalizeNominal(j,trainData[instance][j],percentage,trainOutput[instance]);

					}
					//generalize continuous attribute
					else{
						otherRules[nearestRule].generalizeContinuous(j,trainData[instance][j],percentage,trainOutput[instance]);
					}
				}

				if(presentations<minPresentations){
					presentations++;
				}
				
				//regularize (special)
				if(presentations%regularize==0){
					
					for(int k=0;k<otherRules.length;k++){
						if(bestRules[k].getSpecialImpurityLevel(bestRules)>=otherRules[k].getSpecialImpurityLevel(bestRules)){			
							bestRules[k]=otherRules[k].clone();						
						}			
					}
					
					//get the new set of rules
					for(int k=0;k<bestRules.length;k++){
						otherRules[k]=bestRules[k].clone();
					}
				}
				
			}//end-for

		}//end-while
		
		//regularize

			
		for(int k=0;k<otherRules.length;k++){
			otherRules[k].computeImpurityLevel();
				
			if(bestRules[k].getSpecialImpurityLevel(bestRules)>=otherRules[k].getSpecialImpurityLevel(bestRules)){
				bestRules[k]=otherRules[k].clone();
			}
				
		}
		
		
		ruleset=new Rule [bestRules.length];
		
		for(int i=0;i<bestRules.length;i++){
			ruleset[i]=bestRules[i].clone();
		}
		
	}//end-method

    /**
	 * Classifies an instance using the ruleset
	 *
     * @param instance Instance to classify
     * @return Class assigned to the instance
	 */
	protected int evaluate(double instance[]){
		
		int selected=-1;
		int index;
		
		index=findNearestRule(instance,ruleset);
		
		selected=ruleset[index].getOutput();
		
		return selected;

	}//end-method
	
    /**
	 * Denormalizes nominal data
	 *
	 */	
	private void denormalizeData(){
			
	    for (int i=0; i<train.getNumInstances(); i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		trainData[i][j] *= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}
	            }
	    	}
	    }
	    
	    for (int i=0; i<test.getNumInstances(); i++) {
	    	for (int j = 0; j < inputAtt; j++) {
	            if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
	            	if(Attributes.getInputAttribute(j).getNominalValuesList().size()>1){
	            		testData[i][j] *= Attributes.getInputAttribute(j).getNominalValuesList().size()-1;
	            	}
	            }
	    	}
	    }

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

