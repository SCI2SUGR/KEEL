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

package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

/**
 * <p>
 * @author Written by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * 
 * @version 1.0
 * @since JDK1.2
 * </p>
 */

import java.util.Vector;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class ruleOrderAgent{ 
	
	int numClasses;
	int defaultClass;
	int numInstances;
	
	Vector<Integer> selectedRules;
	Vector<matchProfileAgent> profiles;
	
	
	double accuracy;
	boolean[] ClassifiedOK;
	int[] MatchedByRule;
	double[] ratioOfClass;
	

	int numRules;
	int numOK;
	
	int[] reglaDelPerfil;
	
	matchProfileAgent mpaDR;

	
	
	public ruleOrderAgent(int pDefaultClass, int pNumInstances, int capacity){
		
		InstanceWrapper examples[]=PopulationWrapper.ilas.getInstances();	  		//instancias de la iteracion actual


		int i;

		defaultClass=pDefaultClass;
		numInstances=pNumInstances;
		numClasses=Parameters.numClasses;

		selectedRules = new Vector<Integer>(capacity);
		profiles = new Vector<matchProfileAgent>(capacity);

		ClassifiedOK=new boolean[numInstances];
		MatchedByRule=new int[numInstances];
		ratioOfClass = new double[numClasses];

		numRules=1;
		numOK=0;

		for(i=0;i<numClasses;i++) ratioOfClass[i]=0;

		mpaDR=new matchProfileAgent(numInstances,defaultClass);
		for(i=0;i<numInstances;i++) {
			MatchedByRule[i]=0;
			int cl=examples[i].instanceClass;
			
			if(cl==defaultClass) {
				numOK++;
				ClassifiedOK[i]=true;
				mpaDR.addOK(i);
			} else {
				ClassifiedOK[i]=false;
				mpaDR.addKO(i);
			}
			ratioOfClass[cl]++;
		}
		
		mpaDR.generateProfiles();

		for(i=0;i<numClasses;i++) {
			int num=(int)ratioOfClass[i];
			ratioOfClass[i]*=Parameters.filterSmartCrossover;
			if(ratioOfClass[i]<5) ratioOfClass[i]=5;
			if(ratioOfClass[i]>num*0.20) {
				ratioOfClass[i]=num*0.20;
			}
		}

		accuracy=(double)numOK/(double)numInstances;
	}
	
	
	public int insertRule(int whichRule, matchProfileAgent mpa){
		
		int i;
		int bestPos = 0;
		double bestAcc=0;

		for(i=0;i<numRules;i++) {
			double acc=accuracyOfPosition(i,mpa);
			if(i==0 || acc>bestAcc) {
				bestPos=i;
				bestAcc=acc;
			}
		}

		if(bestAcc>accuracy) {
			addRule(whichRule,mpa,bestPos);
			return bestPos;
		}
		return -1;
	}
	
	
	public void addRule(int whichRule,matchProfileAgent mpa,int whichPos){
		
		int i;

		if(selectedRules.size()==0) {
			selectedRules.addElement(whichRule);
			profiles.addElement(mpa);
		} else if(selectedRules.size()==whichPos) {
			selectedRules.addElement(whichRule);
			profiles.addElement(mpa);
		} else {
			selectedRules.insertElementAt(whichRule,whichPos);
			profiles.insertElementAt(mpa,whichPos);
		}

		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]>=whichPos) {
				MatchedByRule[i]++;
			
				if(mpa.mapOK[i]) {
					MatchedByRule[i]=whichPos;
					if(ClassifiedOK[i]==false) {
						numOK++;
						ClassifiedOK[i]=true;
					}
				}

				if(mpa.mapKO[i]) {
					MatchedByRule[i]=whichPos;
					if(ClassifiedOK[i]==true) {
						numOK--;
						ClassifiedOK[i]=false;
					}
				}
			}
		}
		
		numRules++;
		accuracy=(double)numOK/(double)numInstances;
	}
	
	
	public void updateMPA(int whichPos,matchProfileAgent mpa){
		
		int i,j;

		profiles.set(whichPos, mpa);

		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]>=whichPos) {
				boolean matched=false;
				if(mpa.mapOK[i]) {
					matched=true;
					MatchedByRule[i]=whichPos;
					if(ClassifiedOK[i]==false) {
						numOK++;
						ClassifiedOK[i]=true;
					}
				}

				if(mpa.mapKO[i]) {
					matched=true;
					MatchedByRule[i]=whichPos;
					if(ClassifiedOK[i]==true) {
						numOK--;
						ClassifiedOK[i]=false;
					}
				}
				if(!matched && MatchedByRule[i]==whichPos) {
					boolean corr=ClassifiedOK[i]; 
					boolean found=false;
					for(j=whichPos+1;j<numRules-1 && !found;j++) {
						if(profiles.get(j).mapOK[i]) {
							MatchedByRule[i]=j;
							if(!corr) {
								ClassifiedOK[i]=true;
								numOK++;
							} 
							found=true;
						} else if(profiles.get(j).mapKO[i]) {
							MatchedByRule[i]=j;
							if(corr) {
								ClassifiedOK[i]=false;
								numOK--;
							}
							found=true;
						}
					}
					if(!found) {
						MatchedByRule[i]=numRules-1;
						if(mpaDR.mapOK[i]) {
							if(!corr) {
								ClassifiedOK[i]=true;
								numOK++;
							} 
						} else {
							if(corr) {
								ClassifiedOK[i]=false;
								numOK--;
							}
						}
					}
				}
			}
		}
		
		accuracy=(double)numOK/(double)numInstances;
	}
	
	
	public double accuracyOfPosition(int pos,matchProfileAgent mpa){
		int i;
		int newOK=numOK;

		for(i=0;i<mpa.numOK;i++) {
			int ex=mpa.listOK[i];
			if(MatchedByRule[ex]>=pos && ClassifiedOK[ex]==false) {
				newOK++;
			}
		}

		for(i=0;i<mpa.numKO;i++) {
			int ex=mpa.listKO[i];
			if(MatchedByRule[ex]>=pos && ClassifiedOK[ex]==true) {
				newOK--;
			}
		}

		return (double)newOK/(double)numInstances;
	}
	
	
	
	public int ruleNeedsInstances(int rule){
		int j;
		
		int ruleOK=0;
		for(j=0;j<numInstances;j++) {
			if(MatchedByRule[j]==rule && ClassifiedOK[j]==true) {
				ruleOK++;
			}
		}
		
		if(ruleOK<ratioOfClass[profiles.get(rule).ruleClass]) {
			return (int)(ratioOfClass[profiles.get(rule).ruleClass]-ruleOK+0.5);	
		}
		return 0;
	}


	public void refineRuleSet(){
		
		int i,j;
		
		i=0;
		while(i<numRules-1) {
			int ruleOK=0;
			for(j=0;j<numInstances;j++) {
				if(MatchedByRule[j]==i && ClassifiedOK[j]==true) {
					ruleOK++;
				}
			}
			
			if(ruleOK<ratioOfClass[profiles.get(i).ruleClass]) { 

				removeRule(i);
			} else {
				i++;
			}
		}
	}

	public void copyOrder(Vector<Integer> finalRules){
		int i;

		finalRules.removeAllElements();
		finalRules.ensureCapacity(selectedRules.size());
		for(i=0;i<selectedRules.size();i++) {
			finalRules.addElement(selectedRules.get(i));
		}
	}

	public matchProfileAgent generateActualMPA(int rule){
		int i;

		matchProfileAgent mpa = new matchProfileAgent(numInstances, profiles.get(rule).ruleClass);

		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]==rule) {
				if(ClassifiedOK[i]) {
					mpa.addOK(i);
				} else {
					mpa.addKO(i);
				}
			}
		}
		mpa.generateProfiles();

		return mpa;
	}

	public void removeMatchesOfRule(int rule,matchProfileAgent mpa){
		
		int i,j;

		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]==rule) {
				if(!mpa.mapOK[i] && !mpa.mapKO[i]) {
					boolean corr=ClassifiedOK[i]; 
					boolean found=false;
					for(j=rule+1;j<numRules-1 && !found;j++) {
						if(profiles.get(j).mapOK[i]) {
							MatchedByRule[i]=j;
							if(!corr) {
								ClassifiedOK[i]=true;
								numOK++;
							} 
							found=true;
						} else if(profiles.get(j).mapKO[i]) {
							MatchedByRule[i]=j;
							if(corr) {
								ClassifiedOK[i]=false;
								numOK--;
							}
							found=true;
						}
					}
					if(!found) {
						MatchedByRule[i]=numRules-1;
						if(mpaDR.mapOK[i]) {
							if(!corr) {
								ClassifiedOK[i]=true;
								numOK++;
							} 
						} else {
							if(corr) {
								ClassifiedOK[i]=false;
								numOK--;
							}
						}
					}
				}
			}
		}

		accuracy=(double)numOK/(double)numInstances;
	} 

	public void removeRule(int rule){
		
		int i,j;

		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]==rule) {
				boolean corr=ClassifiedOK[i]; 
				boolean found=false;
				for(j=rule+1;j<numRules-1 && !found;j++) {
					if(profiles.get(j).mapOK[i]) {
						MatchedByRule[i]=j;
						if(!corr) {
							ClassifiedOK[i]=true;
							numOK++;
						} 
						found=true;
					} else if(profiles.get(j).mapKO[i]) {
						MatchedByRule[i]=j;
						if(corr) {
							ClassifiedOK[i]=false;
							numOK--;
						}
						found=true;
					}
				}
				if(!found) {
					MatchedByRule[i]=numRules-1;
					if(mpaDR.mapOK[i]) {
						if(!corr) {
							ClassifiedOK[i]=true;
							numOK++;
						} 
					} else {
						if(corr) {
							ClassifiedOK[i]=false;
							numOK--;
						}
					}
				}
			}

			if(MatchedByRule[i]>rule) {
				MatchedByRule[i]--;
			}
		}

		selectedRules.removeElementAt(rule);
		profiles.removeElementAt(rule);
		numRules--;
		accuracy=(double)numOK/(double)numInstances;
	} 

	public int getPossibleInstancesOfRule(int rule, int[] instances){
		int i;

		int numInst=0;
		for(i=0;i<numInstances;i++) {
			if(MatchedByRule[i]>rule) {
				instances[numInst++]=i;
			}
		}
		
		return numInst;
	}


	public void addPositiveExamples(int rule,int[]instances,int numInst, matchProfileAgent mpa){
		int i;

		profiles.set(rule, mpa);

		for(i=0;i<numInst;i++) {
			int inst=instances[i];
			MatchedByRule[inst]=rule;
			if(ClassifiedOK[inst]==false) {
				numOK++;
				ClassifiedOK[inst]=true;
			}
		}

		accuracy=(double)numOK/(double)numInstances;
	}
	
	
	public double min(double a,double b) {
        if(a<b) return a;
        return b;
    }
	
	public double getAccuracy() {return accuracy;}
	public int getNumRules() {return numRules;}
	public int getRule(int rule) { return selectedRules.get(rule);}



}
