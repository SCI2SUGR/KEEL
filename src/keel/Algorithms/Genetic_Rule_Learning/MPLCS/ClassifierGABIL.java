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
 * <p>
 * @author Written by Jaume Bacardit (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 23/12/2008
 * @author Modified by Jose A. Saez Munoz (ETSIIT, Universidad de Granada - Granada) 10/09/10
 * 
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.MPLCS;

import java.util.Vector;
import keel.Dataset.*;
import keel.Algorithms.Genetic_Rule_Learning.MPLCS.Assistant.Globals.*;

public class ClassifierGABIL
    extends Classifier implements Cloneable {
/**
 * <p>
 * Contains the classifier for the GABIL knowledge representation
 * </p>
 */
	
	
    public class cleanTarget{
    	int maxAtt;
    	int maxValue;
    	int maxNeg;
    	
    	public cleanTarget(){}
    }

	public class activationsAtt{
        int numValues;
        int[][] actValues;
		
		public activationsAtt(){}
	}
	
	public class attInstances{
	      int numValues;
	       int[][][] valueInstances;
	       int[][] numInst;
		
		public attInstances(){}
	}
	
	public class splittedRule{
        int[] rule1;
        matchProfileAgent mpa1;
        int[] rule2;
        matchProfileAgent mpa2;
		
		
		public splittedRule(){}
	}

	
  // The cromosome
  int[] crm;
  int defaultClass;

  public ClassifierGABIL() {
    isEvaluated = false;
  }

  public void initRandomClassifier() {
    numRules = Parameters.initialNumberOfRules;
    int ruleSize = Globals_GABIL.ruleSize;
    double prob = Parameters.probOne;
    int nC = Parameters.numClasses;
    crm = new int[numRules * ruleSize];
    int base = 0;

    if (Globals_DefaultC.defaultClassPolicy == Globals_DefaultC.AUTO) {
      defaultClass = Rand.getInteger(0, Parameters.numClasses - 1);
    }
    else {
      defaultClass = Globals_DefaultC.defaultClass;
    }

    for (int i = 0; i < numRules; i++) {
      InstanceWrapper ins = null;
      if (PopulationWrapper.smartInit) {
        if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.DISABLED) {
          ins = PopulationWrapper.getInstanceInit(defaultClass);
        }
        else {
          ins = PopulationWrapper.getInstanceInit(Parameters.numClasses);
        }
      }

      int base2 = base;
      for (int j = 0; j < Parameters.numAttributes; j++) {
        int value;
        if (ins != null) {
          value = ins.getNominalValue(j);
        }
        else {
          value = -1;
        }
        for (int k = 0; k < Globals_GABIL.size[j]; k++) {
          if (k != value) {
            if (Rand.getReal() < prob) {
              crm[base2 + k] = 1;
            }
            else {
              crm[base2 + k] = 0;
            }
          }
          else {
            crm[base2 + k] = 1;
          }
        }
        base2 += Globals_GABIL.size[j];
      }

      if (ins != null) {
        crm[base2] = ins.classOfInstance();
      }
      else {
        do {
          crm[base2] = Rand.getInteger(0, nC - 1);
        }
        while (Globals_DefaultC.enabled &&
               crm[base2] == defaultClass);
      }

      base += ruleSize;
    }

    resetPerformance();
  }

  public double computeTheoryLength() {
    int base = 0;
    int ruleSize = Globals_GABIL.ruleSize;
    theoryLength = 0;
    for (int i = 0; i < numRules; i++) {
      if (PerformanceAgent.getActivationsOfRule(i) > 0) {
        int base2 = base;
        for (int j = 0; j < Parameters.numAttributes; j++) {
          double countFalses = 0;
          int numValues = Globals_GABIL.size[j];
          for (int k = 0; k < numValues; k++) {
            if (crm[base2 + k] == 0) {
              countFalses++;
            }
          }
          theoryLength += numValues + countFalses;
          base2 += Globals_GABIL.size[j];
        }
      }
      base += ruleSize;
    }

    if (Globals_DefaultC.enabled) {
      theoryLength += 0.00000001;
    }
    return theoryLength;
  }

  /**
   * This function classifies input instances. It returns a class
   * prediction of -1 if the input example cannot be classified
   */
  public int doMatch(InstanceWrapper ins) {
    int nA = Parameters.numAttributes;
    boolean okMatch;
    int i, j;
    int base = 0;
    int ruleSize = Globals_GABIL.ruleSize;

    int[] val = ins.getNominalValues();

    for (i = 0; i < numRules; i++) {
      okMatch = true;

      for (j = 0; okMatch && j < nA; j++) {
        if (crm[base + Globals_GABIL.offset[j] + val[j]] == 0) {
          okMatch = false;
        }
      }

      if (okMatch) {
        positionRuleMatch = i;
        return crm[base + ruleSize - 1];
      }
      base += ruleSize;
    }
    if (Globals_DefaultC.enabled) {
      positionRuleMatch = numRules;
      return defaultClass;
    }
    return -1;
  }

  public void printClassifier() {
    int nA = Parameters.numAttributes;
    int ruleSize = Globals_GABIL.ruleSize;
    String str;
    int base = 0;

    for (int i = 0; i < numRules; i++) {
      str = i + ":";
      for (int j = 0; j < nA; j++) {
        Attribute att = Attributes.getAttribute(j);
        String temp = "Att " + att.getName() + " is ";
        boolean irr = true;
        boolean first = true;
        for (int k = 0; k < Globals_GABIL.size[j]; k++) {
          if (crm[base + Globals_GABIL.offset[j] + k] == 1) {
            if (first) {
              temp += att.getNominalValue(k);
              first = false;
            }
            else {
              temp += "," + att.getNominalValue(k);
            }
          }
          else {
            irr = false;
          }
        }
        if (!irr) {
          Main.numAttsBest++;
          str += temp + "|";
        }
      }
      int cl = crm[base + ruleSize - 1];
      String name = Attributes.getAttribute(Parameters.numAttributes).
          getNominalValue(cl);
      str += name;
      LogManager.println(str);
      base += ruleSize;
    }
    if (Globals_DefaultC.enabled) {
      LogManager.println(numRules + ":Default rule -> "
                         +
                         Attributes.getAttribute(Parameters.numAttributes).
                         getNominalValue(defaultClass));
    }
  }

  public int getNumRules() {
    if (Globals_DefaultC.enabled) {
      return numRules + 1;
    }
    return numRules;
  }

  public Classifier[] crossoverClassifiers(Classifier _parent2) {
    ClassifierGABIL offspring1 = new ClassifierGABIL();
    ClassifierGABIL offspring2 = new ClassifierGABIL();
    ClassifierGABIL parent2 = (ClassifierGABIL) _parent2;

    int ruleSize = Globals_GABIL.ruleSize;
    int ruleP1 = (int) Rand.getInteger(0, numRules - 1);
    int ruleP2 = (int) Rand.getInteger(0, parent2.numRules - 1);
    offspring1.numRules = ruleP1 + parent2.numRules - ruleP2;
    offspring2.numRules = ruleP2 + numRules - ruleP1;
    int cutPoint = (int) Rand.getInteger(0, Globals_GABIL.ruleSize);
    offspring1.defaultClass = offspring2.defaultClass = defaultClass;

    offspring1.crm = new int[ruleSize * offspring1.numRules];
    offspring2.crm = new int[ruleSize * offspring2.numRules];

    System.arraycopy(crm, 0, offspring1.crm, 0, ruleP1 * ruleSize);
    System.arraycopy(parent2.crm, 0, offspring2.crm, 0, ruleP2 * ruleSize);

    int base1 = ruleP1 * ruleSize;
    int base2 = ruleP2 * ruleSize;

    System.arraycopy(crm, base1, offspring1.crm, base1, cutPoint);
    System.arraycopy(parent2.crm, base2, offspring2.crm, base2, cutPoint);
    System.arraycopy(crm, base1 + cutPoint, offspring2.crm, base2 + cutPoint,
                     ruleSize - cutPoint);
    System.arraycopy(parent2.crm, base2 + cutPoint, offspring1.crm,
                     base1 + cutPoint, ruleSize - cutPoint);

    base1 += ruleSize;
    base2 += ruleSize;
    System.arraycopy(crm, base1, offspring2.crm, base2,
                     (numRules - ruleP1 - 1) * ruleSize);
    System.arraycopy(parent2.crm, base2, offspring1.crm, base1,
                     (parent2.numRules - ruleP2 - 1) * ruleSize);

    Classifier[] ret = new Classifier[2];
    ret[0] = offspring1;
    ret[1] = offspring2;

    return ret;
  }

  public Classifier copy() {
    int ruleSize = Globals_GABIL.ruleSize;
    ClassifierGABIL ret = new ClassifierGABIL();

    ret.numRules = numRules;
    ret.theoryLength = theoryLength;
    ret.exceptionsLength = ret.exceptionsLength;
    ret.crm = new int[numRules * ruleSize];
    System.arraycopy(crm, 0, ret.crm, 0, numRules * ruleSize);
    ret.defaultClass = defaultClass;

    ret.setAccuracy(accuracy);
    ret.setFitness(fitness);
    ret.isEvaluated = isEvaluated;
    ret.setNumAliveRules(numAliveRules);
    return ret;
  }

  public void doMutation() {
    int whichRule = (int) Rand.getInteger(0, numRules - 1);
    int ruleSize = Globals_GABIL.ruleSize;
    int base = whichRule * ruleSize;
    int gene;

    if (Globals_DefaultC.numClasses > 1
        && Rand.getReal() < 0.1) {
      gene = ruleSize - 1;
    }
    else {
      gene = (int) Rand.getInteger(0, ruleSize - 2);
    }

    if (gene < ruleSize - 1) {
      if (crm[base + gene] == 1) {
        crm[base + gene] = 0;
      }
      else {
        crm[base + gene] = 1;
      }
    }
    else {
      int oldValue = crm[base + gene];
      int newValue;
      do {
        newValue = (int) Rand.getInteger(0, Parameters.numClasses - 1);
      }
      while (newValue == oldValue ||
             (Globals_DefaultC.enabled && newValue == defaultClass));
      crm[base + gene] = newValue;
    }

    isEvaluated = false;
  }

  public void deleteRules(int[] whichRules) {
    if (numRules == 1 || whichRules.length == 0) {
      return;
    }

    int ruleSize = Globals_GABIL.ruleSize;
    int rulesToDelete = whichRules.length;
    if (whichRules[rulesToDelete - 1] == numRules) {
      rulesToDelete--;
    }

    int[] newCrm = new int[ruleSize * (numRules - rulesToDelete)];
    int countPruned = 0;
    int baseOrig = 0;
    int baseNew = 0;

    for (int i = 0; i < numRules; i++) {
      if (countPruned < rulesToDelete) {
        if (i != whichRules[countPruned]) {
          System.arraycopy(crm, baseOrig, newCrm, baseNew, ruleSize);
          baseNew += ruleSize;
        }
        else {
          countPruned++;
        }
      }
      else {
        System.arraycopy(crm, baseOrig, newCrm, baseNew, ruleSize);
        baseNew += ruleSize;
      }
      baseOrig += ruleSize;
    }
    numRules -= rulesToDelete;
    crm = newCrm;
  }

  public double getLength() {
    return numAliveRules;
  }

  public int numSpecialStages() {
    return 0;
  }

  public void doSpecialStage(int stage) {}

  public int getNiche() {
    if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.AUTO) {
      return 0;
    }
    return defaultClass;
  }

  public int getNumNiches() {
    if (Globals_DefaultC.defaultClassPolicy != Globals_DefaultC.AUTO) {
      return 1;
    }
    return Parameters.numClasses;
  }
  
  
   public void crossoverRSW(Classifier[] parents, int num){
	   
	   int ruleSize=Globals_GABIL.ruleSize;
	   int i, k;
	   
	   
		Vector<int[]> candidateRules = new Vector<int[]>(50,10);
	   Vector<matchProfileAgent>profiles = new Vector<matchProfileAgent>(50,10);
	   int ruleCount=0;
	   
  	for(k=0;k<num;k++) {
  		ClassifierGABIL parent=(ClassifierGABIL)parents[k];
  		int pos=0;
  		for(i=0;i<parent.numRules;pos+=Globals_GABIL.ruleSize,i++) {
  		    int[] newRule = new int[ruleSize];
  		    System.arraycopy(parent.crm, pos, newRule, 0, ruleSize);
  			matchProfileAgent mpa = evaluateRuleCX(newRule);
  			candidateRules.addElement(newRule);
  			ruleCount++;
  			profiles.addElement(mpa);
  		}
  	}

  	Vector<Integer> ruleOrder = new Vector<Integer>(50,10);
  	findOrder(profiles,candidateRules,ruleOrder);

  	numRules = ruleOrder.size(); 
  	int length2 = numRules * ruleSize;
  	crm = new int[length2];
  	for (i = 0; i < numRules; i++){
  		System.arraycopy(candidateRules.get(ruleOrder.get(i)), 0, crm,i*ruleSize , ruleSize);
  	}


  }
  
  
   
   public matchProfileAgent evaluateRuleCX(int[] rule){
	   
   	int i;
   	int cl = rule[Globals_GABIL.ruleSize-1];
   	
   	
   	
   	int max = PopulationWrapper.ilas.getNumInstancesOfIteration();
   	InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();

   	matchProfileAgent mpa=new matchProfileAgent(max,cl);

   	for (i = 0; i < max; i++) {
   		if (ruleMatches(rule, instances[i])) {
   			if (instances[i].instanceClass == cl) {
   				mpa.addOK(i);
   			} else {
   				mpa.addKO(i);
   			}
   		}
   	}
   	mpa.generateProfiles();

   	return mpa;
   }
  
  
  
   public void findOrder(Vector<matchProfileAgent> profiles, Vector<int[]>candidateRules,Vector<Integer> ruleOrder){
	   
	   int numInst=PopulationWrapper.ilas.getNumInstancesOfIteration();
	   int i,j;

		ruleOrderAgent best=null;
		
		for(j=0;j<Parameters.repetitionsRuleOrdering;j++) {
				int totalRules=profiles.size();
				Sampling samp=new Sampling(totalRules);
				ruleOrderAgent roa = new ruleOrderAgent(defaultClass,numInst,totalRules);

				for(i=0;i<totalRules;i++) {
					int rule=samp.getSample();
					roa.insertRule(rule,profiles.get(rule));
				}

		                if(Parameters.doRuleCleaning) {
		                        int num=roa.getNumRules()-1;
		                        for(i=0;i<num;i++) {
		                                matchProfileAgent mpa=roa.generateActualMPA(i);
		                                if(cleanRule(candidateRules.get(roa.getRule(i)),mpa)) {
		                                        roa.removeMatchesOfRule(i,mpa);
		                                }
		                        }
		                }

		                if(Parameters.doRuleGeneralizing){
		                        int numCand = 0;
		                        int [] candidates = new int[numInst];
		                        for(i=0;i<roa.getNumRules()-1;i++) {
		                                int whichRule=roa.getRule(i);
		                                int[] rule=candidateRules.get(whichRule);
		                                numCand=roa.getPossibleInstancesOfRule(i,candidates);
		                                int[] newInstances = new int[numCand];
		                                int numNew = 0;
		                                numNew=generalizeRule(rule,candidates,numCand,newInstances,whichRule,false);
		                                candidateRules.set(whichRule, rule);
		                                if(numNew>0){
		                                        matchProfileAgent mpa=evaluateRuleCX(rule);
		                                        profiles.set(whichRule, mpa);
		                                        roa.addPositiveExamples(i,newInstances,numNew,mpa);
		                                }
		                        }
		                }


		                if(Parameters.doRuleSplitting) {
		                        for(i=0;i<roa.getNumRules()-1;i++) {
		                                matchProfileAgent mpa=roa.generateActualMPA(i);
		                                splittedRule spl=new splittedRule();
		                                if(editRule(candidateRules.get(roa.getRule(i)),mpa,spl)) {
		                                        roa.removeRule(i);
		                                        profiles.addElement(spl.mpa1);
		                                        profiles.addElement(spl.mpa2);
		                                        candidateRules.addElement(spl.rule1);
		                                        candidateRules.addElement(spl.rule2);
		                                        roa.insertRule(profiles.size()-2,profiles.get(profiles.size()-2));
		                                        roa.insertRule(profiles.size()-1,profiles.get(profiles.size()-1));
		                                }
		                                                          
		                        }
		                }


				roa.refineRuleSet();

				if(best==null) {
					best=roa;
				} else {
					double accOld=best.getAccuracy();
					double accNew=roa.getAccuracy();
					if(accNew>accOld) {
						best=roa;
					} else if(accNew==accOld && roa.getNumRules()<best.getNumRules()) {
						best=roa;
					} else {
					}
				}
			}

			best.copyOrder(ruleOrder);
			if(ruleOrder.size()==0) {
				int pos=Rand.getInteger(0, profiles.size()-1);
				ruleOrder.addElement(pos);
			}
		}
  
  
  
  
   
   public void doLocalSearch(){
	   
	   int ruleSize=Globals_GABIL.ruleSize;
	   int numInstances = PopulationWrapper.ilas.getNumInstancesOfIteration();
	   int[] remainingInstances = new int[numInstances];
	   int[] matched = new int[numInstances];
       int i;
       
       for(i=0;i<numInstances;i++) {
    	   remainingInstances[i]=i;
       }


       int index=0;    
       boolean lastSplitted=false;
           
       while(index<numRules) {      
    	   int pos = index*ruleSize;
    	   int[] rule = getRule(pos);
    	   Integer numMatches = computeMatches(rule,remainingInstances,numInstances,matched);
    	   


    	   if(Parameters.doRuleCleaning) {         
    		   if(ruleCleaning(rule,matched,numMatches, pos)) {    
    			   numMatches = computeMatches(rule,remainingInstances,numInstances,matched);        
    		   }
    	   }

                  
    	   if(Parameters.doRuleSplitting) {  
    		   
    		   if(lastSplitted){  
    			   lastSplitted=false;     
    		   } 
    		   
    		   else{              
    			   int[] newRule=ruleSplitting(rule,matched,numMatches,pos);
                                   
    			   if(newRule!=null) {
                                         
    				   int[] newChromosome = new int[ruleSize*(numRules+1)];
    				   System.arraycopy(crm, 0, newChromosome, 0, (index+1)*ruleSize);
    				   System.arraycopy(newRule, 0, newChromosome, (index+1)*ruleSize,ruleSize);
    				   System.arraycopy(crm, (index+1)*ruleSize, newChromosome, (index+2)*ruleSize, (numRules-index-1)*ruleSize);
    				   crm = new int[ruleSize*(numRules+1)];

    				   System.arraycopy(newChromosome, 0, crm, 0, (numRules+1)*ruleSize);
    				   numRules++;
    				   int length2=numRules*ruleSize;
    				   pos=index*ruleSize;
    				   newRule = getRule(pos);
    				   numMatches = computeMatches(newRule,remainingInstances,numInstances,matched);
    				   lastSplitted=true;
    			   }
    		   }
                
    	   }

    	   numInstances=updateMatches(remainingInstances,numInstances,matched,numMatches);
    	   
    	   if(numInstances==0){
    		   break;
    	   }

                   
    	   if(Parameters.doRuleGeneralizing) {
                        
    		   int[] ruleG = getRule(pos);
    		   numMatches=generalizeRule(ruleG,remainingInstances,numInstances,matched,pos, true);

    		   if(numMatches>0) {
    			   numInstances=updateMatches(remainingInstances,numInstances,matched,numMatches);
    			   if(numInstances==0) {
    				   break;
    			   }
    		   }
    	   }

                   
    	   index++;
           
       }


   }
   
   
   public int computeMatches(int[] rule, int[] candidates, int numCandidates, int[] matched)
   {
           int j;
           int numMatches=0;
          	InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();

           for(j=0;j<numCandidates;j++) {
                   int inst=candidates[j];
                   if(ruleMatches(rule,instances[inst])) {
                           matched[numMatches++]=inst;
                   }
           }

           return numMatches;
   }

  	public boolean ruleMatches(int rule[], InstanceWrapper ins){
  		
  		int j;

  		for (j = 0; j < Parameters.numAttributes ; j++)
  			if (rule[Globals_GABIL.offset[j] + ins.nominalValues[j]] == 0)
  				return false;
  		
  		return true;
  	}
   
   
  
   public boolean ruleCleaning(int[] rule, int[] matched, int numMatches, int pos){
	
	   int ruleSize=Globals_GABIL.ruleSize;
	   int i;
	   InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();

	   int[] listOK = new int[numMatches];
	   int[] listKO = new int[numMatches];
	   int numOK=0,numKO=0;


	   int cl=rule[ruleSize-1];
	   
	   for(i=0;i<numMatches;i++) {
	   
		   if(instances[matched[i]].instanceClass==cl) {
			   listOK[numOK++]=matched[i];
		   }
		   else {
			   listKO[numKO++]=matched[i];
		   }
	   }


	        cleanTarget ct=cleanTargetOfRule(listOK,numOK,listKO,numKO);
	        boolean changed=false;
	        if(ct.maxNeg>0) {
	                changed=true;
	                crm[pos+Globals_GABIL.offset[ct.maxAtt]+ct.maxValue] = 0;
	        }


	        return changed;
	}
  
  	
  	//*********************************************************************
    //***************** Rule of position pos ******************************
    //*********************************************************************

  	public int[] getRule(int pos){
  		
  		int ruleSize=Globals_GABIL.ruleSize;
  		int rule[]=new int[ruleSize];

  		System.arraycopy(crm, pos, rule, 0, ruleSize);
  		
  		return rule;
  	}
  
  	
  	
  	public cleanTarget cleanTargetOfRule(int[] instOK, int numOK, int[] instKO, int numKO){
  		
  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();
  	    int i,j;


  		int numAttributes=Parameters.numAttributes; 	    
  		int[][] countKO=new int[numAttributes][];
  	    int[][] countOK=new int[numAttributes][];
  		int[] candidateAtt=new int[numAttributes];
  		int[] countCandidateValues=new int[numAttributes];
  	    
  		cleanTarget ct = new cleanTarget();
  		ct.maxValue=-1; 
  		ct.maxAtt=-1;
  		ct.maxNeg=0;

  	    
  		int numCandidateAtts=numAttributes;
  	    
  		for(i=0;i<numAttributes;i++) {
  	    
  			candidateAtt[i]=i;
  			int numValues=Globals_GABIL.size[i];
  			countOK[i]= new int[numValues];
  			countKO[i]= new int[numValues];
  	        
  			countCandidateValues[i]=numValues;
  	        
  			for(j=0;j<numValues;j++) {
  				countOK[i][j]=countKO[i][j]=0;
  			}

  		}

  	    
  		for(i=0;i<numOK && numCandidateAtts>0;i++) {
  	    
  			InstanceWrapper ins=instances[instOK[i]];
  	        
  			int indexAtt=0;
  	        
  			while(indexAtt<numCandidateAtts) {
  				 int att=candidateAtt[indexAtt];
  				 int value=ins.nominalValues[att];
  	            
  				if(countOK[att][value]==0) {
  					countCandidateValues[att]--;
  	                
  					if(countCandidateValues[att]==0) {
  						candidateAtt[indexAtt]=candidateAtt[numCandidateAtts-1];
  						numCandidateAtts--;
  					} else {
  						indexAtt++;
  					}
  	                
  				} else {
  					indexAtt++;
  				}
  	            
  				countOK[att][value]++;
  	            
  			}
  	        
  		}


  		if(numCandidateAtts==0) {
  		
  			
  			return ct;
  		
  		}


  		for(i=0;i<numKO;i++) {
  			
  			InstanceWrapper ins=instances[instKO[i]];
  	        
  			for(j=0;j<numCandidateAtts;j++) {
  				 int att=candidateAtt[j];
  				 int value=ins.nominalValues[att];
  				countKO[att][value]++;
  			}

  		}


  		for(j=0;j<numCandidateAtts;j++) {
  	    
  			 int att=candidateAtt[j];
  			
  			int numValues=Globals_GABIL.size[att];
  	        
  			for(i=0;i<numValues;i++) {
  	        
  				if(countOK[att][i]==0 && countKO[att][i]>ct.maxNeg) {
  	            
  					ct.maxAtt=att;
  	                
  					ct.maxValue=i;
  	                
  					ct.maxNeg=countKO[att][i];
  	                
  				}
  	            
  			}

  		}



  		return ct;
  	
  	}
  	
  	
  	

  	public int[] ruleSplitting(int[] rule, int[] matched, int numInst, int posRR){
  		int ruleSize=Globals_GABIL.ruleSize;

  	        int i,j,k,l,m,n,o,p,q;
  	  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();

  	        int[] listOK = new int[numInst];
  	        int[] listKO = new int[numInst];
  	        int numOK=0,numKO=0;

  	        int cl=rule[ruleSize-1];
  	        for(i=0;i<numInst;i++) {
  	                if(instances[matched[i]].instanceClass==cl) {
  	                        listOK[numOK++]=matched[i];
  	                } else {
  	                        listKO[numKO++]=matched[i];
  	                }
  	        }

  	        if(numKO==0) return null;
  	        activationsAtt[] act=computeActivationStats(listOK,numOK,listKO,numKO);

  	        int maxNeg=0;
  	        int splittedAtt = 0;
  	        int splittedValue = 0;
  	        int valueOrRest = 0;
  	        Vector<Integer>conflicting = new Vector<Integer>(50,10);
  	        int negAtt = 0;
  	        int negValue = 0;

  	        for(i=0;i<Parameters.numAttributes;i++) {
  	                int numConflictingValues=0;
  	                int[] targetConflicting=new int[act[i].numValues];
  	                for(j=0;j<act[i].numValues;j++) {
  	                        if(act[i].actValues[j][0]>0 && act[i].actValues[j][1]>0) {
  	                                targetConflicting[numConflictingValues++]=j;
  	                        }
  	                }

  	                if(numConflictingValues>1) {
  	                        attInstances att=computeInstOfAtt(i,listOK,numOK,listKO,numKO);
  	                        for(l=0;l<numConflictingValues;l++) {
  	                                int target=targetConflicting[l];

  	                                int numOK1=att.numInst[target][0];
  	                                int numKO1=att.numInst[target][1];
  	                                int numOK2=numOK-numOK1;
  	                                int numKO2=numKO-numKO1;
  	                                int[] listOK2 = new int[numOK2];
  	                               int[] listKO2 = new int[numKO2];
  	                                int countOK=0;
  	                                int countKO=0;
  	                                for(m=0;m<act[i].numValues;m++) {
  	                                        if(m!=target) {
  	                                                for(n=0;n<att.numInst[m][0];n++) {
  	                                                        listOK2[countOK++]=att.valueInstances[m][0][n];
  	                                                }
  	                                                for(n=0;n<att.numInst[m][1];n++) {
  	                                                        listKO2[countKO++]=att.valueInstances[m][1][n];
  	                                                }
  	                                        }
  	                                }

  	                                cleanTarget ct=cleanTargetOfRule(att.valueInstances[target][0]
  	                                        ,numOK1,att.valueInstances[target][1],numKO1);
  	                                if(ct.maxNeg>maxNeg) {
  	                                        maxNeg=ct.maxNeg;
  	                                        splittedAtt=i;
  	                                        splittedValue=target;
  	                                        valueOrRest=1;
  	                                        conflicting.removeAllElements();
  	                                        for(q=0;q<numConflictingValues;q++) {
  	                                                if(targetConflicting[q]!=target) {
  	                                                        conflicting.addElement(targetConflicting[q]);
  	                                                }
  	                                        }
  	                                        negAtt=ct.maxAtt;
  	                                        negValue=ct.maxValue;
  	                                }

  	                                ct=cleanTargetOfRule(listOK2,numOK2,listKO2,numKO2);
  	                                if(ct.maxNeg>maxNeg) {
  	                                        maxNeg=ct.maxNeg;
  	                                        splittedAtt=i;
  	                                        splittedValue=target;
  	                                        valueOrRest=0;
  	                                        conflicting.removeAllElements();
  	                                        for(q=0;q<numConflictingValues;q++) {
  	                                                if(targetConflicting[q]!=target) {
  	                                                        conflicting.addElement(targetConflicting[q]);
  	                                                }
  	                                        }
  	                                        negAtt=ct.maxAtt;
  	                                        negValue=ct.maxValue;
  	                                }

  	          
  	                        }
  	                }
  	        }

  	        int[] newRule=null;
  	        if(maxNeg>0) {
  	                newRule = new int[ruleSize];
  	                System.arraycopy(rule, 0, newRule, 0, ruleSize);

  	                for(i=0;i<conflicting.size();i++) {
  	                	crm[posRR+Globals_GABIL.offset[splittedAtt]+conflicting.get(i)]=0;
  	                }
  	                
  			newRule[Globals_GABIL.offset[splittedAtt]+splittedValue]=0;

  	                if(valueOrRest!=0) {
  				crm[posRR+Globals_GABIL.offset[negAtt]+negValue]=0;
  	                } else {
  				newRule[Globals_GABIL.offset[negAtt]+negValue]=0;
  	                }
  	        }


  	        return newRule;
  	}

  	
  	
  	
  	public activationsAtt[] computeActivationStats(int[] instOK, int numOK, int[] instKO, int numKO){
  	        int i,j;
  	  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();

  	        activationsAtt[] act=new activationsAtt[Parameters.numAttributes];
  	        
  	        for(int pp=0 ; pp<Parameters.numAttributes;++pp)
  	        	act[pp]=new activationsAtt();
  	        
  	        
  	        for(i=0;i<Parameters.numAttributes;i++) {
  	                int numValues=Globals_GABIL.size[i];
  	                act[i].numValues=numValues;
  	                act[i].actValues=new int[numValues][];
  	                for(j=0;j<numValues;j++) {
  	                        act[i].actValues[j]=new int[2];
  	                        act[i].actValues[j][0]=act[i].actValues[j][1]=0;
  	                }
  	        }


  	        for(j=0;j<numOK;j++) {
  	        	InstanceWrapper ins=instances[instOK[j]];
  	                for(i=0;i<Parameters.numAttributes;i++) {
  	                        act[i].actValues[ins.nominalValues[i]][0]++;
  	                }
  	        }
  	        for(j=0;j<numKO;j++) {
  	        	InstanceWrapper ins=instances[instKO[j]];
  	                for(i=0;i<Parameters.numAttributes;i++) {
  	                        act[i].actValues[ins.nominalValues[i]][1]++;
  	                }
  	        }

  	        return act;
  	}
  	
  	
  	
  	
  	
  	public attInstances computeInstOfAtt(int attribute,int[] instOK,int numOK,int[]instKO,int numKO)
  	{
  	        int numInst=numOK+numKO;
  	  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();
  	        int i,j,k;
  	        int numValues=Globals_GABIL.size[attribute];

  	        attInstances att=new attInstances();
  	        att.numValues=numValues;
  	        att.valueInstances = new int[numValues][][];
  	        att.numInst = new int[numValues][];
  	        for(i=0;i<numValues;i++) {
  	                att.numInst[i]= new int[2];
  	                att.numInst[i][0]=0;
  	                att.numInst[i][1]=0;
  	                att.valueInstances[i]= new int[2][];
  	                att.valueInstances[i][0] = new int[numInst];
  	                att.valueInstances[i][1] = new int[numInst];
  	        }

  	        for(i=0;i<numOK;i++) {
  	                int inst=instOK[i];
  	                int value=instances[inst].nominalValues[attribute];
  	                att.valueInstances[value][0][att.numInst[value][0]++]=inst;
  	        }
  	        for(i=0;i<numKO;i++) {
  	                int inst=instKO[i];
  	                int value=instances[inst].nominalValues[attribute];
  	                att.valueInstances[value][1][att.numInst[value][1]++]=inst;
  	        }

  	        return att;
  	}
  	
  	
  	
  	public int updateMatches(int[] candidates, int numCand, int[] matched, int numMatched){
  	        int numLeft=0;
  	        int i;
  	        
  	        int[] candidatesLeft = new int[numCand-numMatched];
  	        int indexMatched=0;
  	        int currMatched=matched[0];
  	        
  	        


  	        
  	        
  	        for(i=0;i<numCand;i++){
  	        	if(candidates[i]==currMatched) {
  	        		indexMatched++;
  	        		if(indexMatched<numMatched)
  	        			currMatched=matched[indexMatched];
  	        	}
  	        	
  	        	else{

  	        		candidatesLeft[numLeft++]=candidates[i];
  	        	}
  	        }
  	        
  	        for(i=0;i<numLeft;i++){
  	        	candidates[i]=candidatesLeft[i];
  	        }


  	        return numLeft;
  	}
  	
  	
  	
  	public int generalizeRule(int[] rule, int[] candidates, int numCand, int[] newInstances, int posRR, boolean opcion){
  		
  	        int i,j;
  	        int ruleSize = Globals_GABIL.ruleSize;
  	        int len=ruleSize-1;
  	        int[] countPos = new int[len];
  	        int[] countNeg = new int[len];
  	        Vector<Vector<Integer>> candInst= new Vector<Vector<Integer>>(len,1);
  	  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();
  	        int cl=rule[ruleSize-1];

  	        for(i=0;i<len;i++) {
  	                countPos[i]=countNeg[i]=0;
  	                candInst.add(i, null);
  	        }

  	        for(i=0;i<numCand;i++) {
  	                int posInst=candidates[i];
  	                InstanceWrapper ins=instances[posInst];
  	                int numMatches=0;
  	                int whichPos = 0;
  	                for(j=0;j<Parameters.numAttributes;j++) {
  	                        int value=ins.nominalValues[j];
  	                        int pos=Globals_GABIL.offset[j]+value;
  	                        if(rule[pos]==0) {
  	                                numMatches++;
  	                                whichPos=pos;
  	                                if(numMatches>=2) break;
  	                        }
  	                }

  	                if(numMatches==1) {
  	                        if(ins.instanceClass==cl) {
  	                                countPos[whichPos]++;
  	                                if(countNeg[whichPos]==0) {
  	                                        if(candInst.get(whichPos)==null) {
  	                                                candInst.set(whichPos, new Vector<Integer>((int)(numCand*0.1)));
  	                                        }
  	                                        candInst.get(whichPos).addElement(posInst);
  	                                }
  	                        } else {
  	                                countNeg[whichPos]++;
  	                                candInst.set(whichPos, null);
  	                        }
  	                }
  	        }

  	        int max=0;
  	        int bestPos=0;
  	        for(i=0;i<len;i++) {
  	                if(countNeg[i]==0 && countPos[i]>max) {
  	                        bestPos=i;
  	                        max=countPos[i];
  	                }
  	        }

  	        
  	        if(max>0) {
  	        	
  	        	if(opcion)
  	        		crm[posRR+bestPos]=1;
  	        	else{
  	        		rule[bestPos]=1;
  	        	}
  	        		
  	        		

  	                for(i=0;i<max;i++) {
  	                        newInstances[i]=candInst.get(bestPos).elementAt(i);
  	                }
  	        }


  	        return max;
  	        
  	}
 	
  	
  	

  	public boolean editRule(int[] rule, matchProfileAgent mpa, splittedRule spl){
  		int i,j,k,l,m,n,o,p,q;

  		if(mpa.numKO==0) return false;
  		
  		activationsAtt[] act=computeActivationStats(mpa.listOK,mpa.numOK,mpa.listKO,mpa.numKO);

  		int maxNeg=0;
  		int splittedAtt = 0;
  		int splittedValue = 0;
  		boolean valueOrRest = false;
  		Vector<Integer>conflicting=new Vector<Integer>(50,10);
  		int negAtt = 0;
  		int negValue = 0;

  		for(i=0;i<Parameters.numAttributes;i++) {
  			int numConflictingValues=0;	
  			int[] targetConflicting=new int[act[i].numValues];
  			for(j=0;j<act[i].numValues;j++) {
  				if(act[i].actValues[j][0]>0 && act[i].actValues[j][1]>0) {
  					targetConflicting[numConflictingValues++]=j;
  				}
  			}

  			if(numConflictingValues>1) {
  				attInstances att=computeInstOfAtt(i,mpa.listOK,mpa.numOK,mpa.listKO,mpa.numKO);
  				for(l=0;l<numConflictingValues;l++) {
  					int target=targetConflicting[l];

  					int numOK1=att.numInst[target][0];
  					int numKO1=att.numInst[target][1];
  					int numOK2=mpa.numOK-numOK1;
  					int numKO2=mpa.numKO-numKO1;
  					int[]listOK2 = new int[numOK2];
  					int[]listKO2 = new int[numKO2];
  					int countOK=0;
  					int countKO=0;
  					for(m=0;m<act[i].numValues;m++) {
  						if(m!=target) {
  							for(n=0;n<att.numInst[m][0];n++) {
  								listOK2[countOK++]=att.valueInstances[m][0][n];
  							}
  							for(n=0;n<att.numInst[m][1];n++) {
  								listKO2[countKO++]=att.valueInstances[m][1][n];
  							}
  						}
  					}

  	                                cleanTarget ct=cleanTargetOfRule(att.valueInstances[target][0]
  	                                        ,numOK1,att.valueInstances[target][1],numKO1);
  	                                if(ct.maxNeg>maxNeg) {
  	                                        maxNeg=ct.maxNeg;
  	                                        splittedAtt=i;
  	                                        splittedValue=target;
  	                                        valueOrRest=true;
  	                                        conflicting.removeAllElements();
  	                                        for(q=0;q<numConflictingValues;q++) {
  	                                                if(targetConflicting[q]!=target) {
  	                                                        conflicting.addElement(targetConflicting[q]);
  	                                                }
  	                                        }
  	                                        negAtt=ct.maxAtt;
  	                                        negValue=ct.maxValue;
  	                                }

  	                                ct=cleanTargetOfRule(listOK2,numOK2,listKO2,numKO2);
  	                                if(ct.maxNeg>maxNeg) {
  	                                        maxNeg=ct.maxNeg;
  	                                        splittedAtt=i;
  	                                        splittedValue=target;
  	                                        valueOrRest=false;
  	                                        conflicting.removeAllElements();
  	                                        for(q=0;q<numConflictingValues;q++) {
  	                                                if(targetConflicting[q]!=target) {
  	                                                        conflicting.addElement(targetConflicting[q]);
  	                                                }
  	                                        }
  	                                        negAtt=ct.maxAtt;
  	                                        negValue=ct.maxValue;
  	                                }




  				}
  			}
  		}

  		boolean splitted=false;
  		if(maxNeg>0) {
  			splitted=true;
  			int[] r1= new int[Globals_GABIL.ruleSize];
  			System.arraycopy(rule, 0, r1, 0, Globals_GABIL.ruleSize);
  			int[] r2= new int[Globals_GABIL.ruleSize];
  			System.arraycopy(rule, 0, r2, 0, Globals_GABIL.ruleSize);

  			for(i=0;i<conflicting.size();i++) {
  				r1[Globals_GABIL.offset[splittedAtt]+conflicting.get(i)]=0;
  			}
  			
  			r2[Globals_GABIL.offset[splittedAtt]+splittedValue]=0;

  			if(valueOrRest) {
  				r1[Globals_GABIL.offset[negAtt]+negValue]=0;
  			} else {
  				r2[Globals_GABIL.offset[negAtt]+negValue]=0;
  			}
  			spl.rule1=r1;
  			spl.rule2=r2;
  			spl.mpa1=evaluateRuleCX(r1);
  			spl.mpa2=evaluateRuleCX(r2);
  		}

  		return splitted;
  	}



  	public boolean cleanRule(int[] rule ,matchProfileAgent mpa)
  	{
  	        int i,j;

  	        if(mpa.numKO==0) return false;

  	        cleanTarget ct=cleanTargetOfRule(mpa.listOK,mpa.numOK,mpa.listKO,mpa.numKO);
  	        boolean changed=false;
  	        if(ct.maxNeg>0) {
  	                changed=true;

  			rule[Globals_GABIL.offset[ct.maxAtt]+ct.maxValue]=0;
  	                int[] removedExamples=new int[ct.maxNeg];
  	                instOfAttAndValue(ct.maxAtt,ct.maxValue,mpa.listOK,mpa.numOK,mpa.listKO,mpa.numKO,removedExamples);
  	                mpa.removeMatched(removedExamples,ct.maxNeg);
  	        }

  	        return changed;
  	}


  	public void instOfAttAndValue(int attribute,int value, int[] instOK, int numOK,int[]instKO, int numKO, int []list)
  	{

	  		InstanceWrapper[] instances = PopulationWrapper.ilas.getInstances();
  		int i;

  		int num=0;

  		for(i=0;i<numOK;i++) {
  			int instValue=instances[instOK[i]].nominalValues[attribute];
  			if(instValue==value) {
  				list[num++]=instOK[i];
  			}
  		}
  		for(i=0;i<numKO;i++) {
  			int instValue=instances[instKO[i]].nominalValues[attribute];
  			if(instValue==value) {
  				list[num++]=instKO[i];
  			}
  		}
  	}


  	
  	
  	
}
