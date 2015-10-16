/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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


/*
	ADE_CoForest.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  4/3/2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.ADE_CoForest;

import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;

import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerSMO;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.ADE_CoForest.RandomTree;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;

import org.core.*;

//import sun.misc.Compare;
//import sun.misc.Sort;

import java.util.StringTokenizer;



/**
 * This class implements the Tri-training. You can use: Knn, C4.5, SMO and NB as classifiers.
 * @author triguero
 *
 */

public class ADE_CoForestGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private int k1=3, k2=2;
 private int  num_classifier;
 private double threshold = 0.75;

 private int m_numOriginalLabeledInsts = 0;
 
 /** Number of features to consider in random feature selection.
 If less than 1 will use int(logM+1) ) */
protected int m_numFeatures = 0;

/** Final number of features that were considered in last build. */
protected int m_KValue = 0;

 
 private int [][] predictions;
 private double [][][] probabilities;
 

// private String final_classifier; 

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  RandomTree [] m_classifiers;
  
  
  /**
   * Build a new ADE_CoForestGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public ADE_CoForestGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="ADE_CoForest";
      
  }
  


  /**
   * Build a new ADE_CoForestGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public ADE_CoForestGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="ADE_CoForest";

      this.predictions = new int[6][];
      
     
      this.num_classifier = parameters.getNextAsInt();
      this.threshold = parameters.getNextAsDouble();
      this.k1 = parameters.getNextAsInt();
      this.k2 = parameters.getNextAsInt();
      //this.final_classifier = parameters.getNextAsString();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      this.probabilities = new double[3][][];
                                      
    //  System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  
  
  
  /**
   * 
   * @param inst
   * @param idxInst
   * @param inbags
   * @param idExcluded
   * @return
   * @throws Exception
   */
  private double[] outOfBagDistributionForInstanceExcluded(Prototype inst, int idxInst, boolean[][] inbags, int idExcluded) throws Exception
  {
    double[] distr = new double[this.numberOfClass];
    for(int i = 0; i < this.num_classifier; i++)
    {
      if(inbags[i][idxInst] == true || i == idExcluded)
        continue;

      double[] d = m_classifiers[i].distributionForInstance(inst);
      if(d!=null){
	      for(int iClass = 0; iClass < this.numberOfClass; iClass++){
	        distr[iClass] += d[iClass];
	      }
      }
    }
    
    double sumatoria = 0;
    for(int i=0; i< distr.length;i++){
    	sumatoria+= distr[i];
    }
    
    if(sumatoria != 0){
      //Utils.normalize(distr);
      for (int i=0; i<distr.length; i++){
    	  distr[i] /= sumatoria;
      }
    	
    }
    return distr;
  }
  
  
  /**
   * 
   * @param data
   * @param weights  of the instances
   * @param inbags
   * @param id
   * @return
   * @throws Exception
   */
  
  private double measureError(PrototypeSet data, boolean[][] inbags, int id) throws Exception
  {
    double err = 0;
    double count = 0;
    
    for(int i = 0; i < data.size() && i < m_numOriginalLabeledInsts; i++)
    {
      Prototype inst = data.get(i);
      double[] distr = outOfBagDistributionForInstanceExcluded(inst, i, inbags, id);

      double maximo= Double.MIN_VALUE;
      int claseMax =0;
      
      for(int j=0; j< distr.length; j++){
    	  if(distr[j]> maximo){
    		  maximo = distr[j];
    		  claseMax = j;
    	  }
      }
    	  
      if(maximo > this.threshold)
      {
        count += inst.getWeight();
        if(claseMax != inst.getOutput(0))
          err += inst.getWeight();
      }
    }
    err /= count;
    return err;
 }
  

  
  
  /**
   * Resample instances w.r.t the weight
   *
   * @param data Instances -- the original data set
   * @param id of the classifier
   * @param sampled boolean[] -- the output parameter, indicating whether the instance is sampled
   * @return Instances
   */
  public final PrototypeSet resampleWithWeights(PrototypeSet data, int id, boolean[] sampled)
  {

    double[] weights = new double[data.size()];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = data.get(i).getWeight();
    }
    
	PrototypeSet newData = new PrototypeSet(data.clone());
	  
    if (data.size() == 0) {
      return newData;
    }
    
    
    double[] probabilities = new double[data.size()];
    double sumProbs = 0, sumOfWeights=0;
    
    for(int i=0; i<weights.length;i++){
    	sumOfWeights+=weights[i];
    }
    
    for (int i = 0; i < data.size(); i++) {
      sumProbs += Randomize.Rand();
      probabilities[i] = sumProbs;
    }
    
    for (int i = 0; i < probabilities.length; i++) {
    	probabilities[i] /= (sumProbs / sumOfWeights);
    }
   

    // Make sure that rounding errors don't mess things up
    probabilities[data.size() - 1] = sumOfWeights;
    int k = 0; int l = 0;
    sumProbs = 0;
    while ((k < data.size() && (l < data.size()))) {
      if (weights[l] < 0) {
        throw new IllegalArgumentException("Weights have to be positive.");
      }
      sumProbs += weights[l];
      while ((k < data.size()) &&
             (probabilities[k] <= sumProbs)) {
        newData.add(data.get(l));
        sampled[l] = true;
        newData.get(k).setWeight(1);
        k++;
      }
      l++;
    }
    return newData;
  }
  
  
  
  private double[] distributionForInstanceExcluded(Prototype inst, int idExcluded) throws Exception
  {
    double[] distr = new double[this.numberOfClass];
    for(int i = 0; i < this.num_classifier; i++)
    {
      if(i == idExcluded)
        continue;

      double[] d = m_classifiers[i].distributionForInstance(inst);
      for(int iClass = 0; iClass < this.numberOfClass; iClass++)
        distr[iClass] += d[iClass];
    }
    
    // Normalize:
    double sum = 0;
    for (int i = 0; i < distr.length; i++) {
      sum += distr[i];
    }
    
    for (int i = 0; i < distr.length; i++) {
    	distr[i] /= sum;
      }
    
    return distr;
  }
  
  /**
   * To judege whether the confidence for a given instance of H* is high enough,
   * which is affected by the onfidence threshold. Meanwhile, if the example is
   * the confident one, assign label to it and weigh the example with the confidence
   *
   * @param inst Instance -- The instance
   * @param idExcluded int -- the index of the individual should be excluded from H*
   * @return boolean -- true for high
   * @throws Exception - some exception
   */
  protected boolean isHighConfidence(Prototype inst, int idExcluded) throws Exception
  {
    double[] distr = distributionForInstanceExcluded(inst, idExcluded);
    
    double maximo= Double.MIN_VALUE;
    int claseMax =0;
    
    for(int j=0; j< distr.length; j++){
  	  if(distr[j]> maximo){
  		  maximo = distr[j];
  		  claseMax = j;
  	  }
    }
    
    double confidence = maximo;// getConfidence(distr);
    if(confidence > this.threshold)
    {
      double classval =  claseMax;//Utils.maxIndex(distr);
      inst.setFirstOutput(classval);  // .setClassValue(classval);    //assign label
      inst.setWeight(confidence);      //set instance weight
      return true;
    }
    else return false;
  }

  
  public int votingRule(Prototype inst) throws Exception{
	  
	    double[] res = new double[this.numberOfClass];
	    for(int j = 0; j < this.num_classifier; j++)
	    {
	      double[] distr = m_classifiers[j].distributionForInstance(inst); // Probability of each class.
	      
	      if(distr!=null){
	    	  for(int z = 0; z < res.length; z++)
	    		  res[z] += distr[z];
	      }
	    }
	    
	    // Normalice RES
	    double sum=0;
	    for(int j=0; j<res.length; j++){
	    	sum+=res[j];
	    }
	  
	    for(int j=0; j<res.length; j++){
	    	res[j]/=sum;
	    }
	    
	    /// determine the maximum value
	    
	    double maximum = 0;
	    int maxIndex = 0;

	    for (int j = 0; j < res.length; j++) {
	      if ((j == 0) || (res[j] > maximum)) {
	    	  maxIndex = j;
	    	  maximum = res[j];
	      }
	    }
	    
	    return maxIndex;
}
  
  /**
   * 
   * Depuration algorithm
   * @return
   */
  protected PrototypeSet removeOnly (PrototypeSet T, PrototypeSet labeled)
  {
	//T.print();
	 PrototypeSet Sew = new PrototypeSet (T.clone());
	
	  int toClean[] = new int [T.size()];
	  Arrays.fill(toClean, 0);
	  int pos = 0;
	  
	for ( Prototype p : T){
		 double class_p = p.getOutput(0);
		
		  PrototypeSet neighbors = KNN.knn(p, labeled, this.k1);
		
		  int counter[]= new int[this.numberOfClass];
		  Arrays.fill(counter,0);
		  
		  for(Prototype q1 :neighbors ){
			  counter[(int) q1.getOutput(0)]++; // increase the counter
		  }
		  
		  // determine the class with majority votes
		  int max = Integer.MIN_VALUE;
		  int maxClass = 0;
		  
		  for (int i=0; i< this.numberOfClass; i++){
			  if(counter[i]>max){
				  max = counter[i];
				  maxClass = i;
			  }
		  }
		  
		  
		  //System.out.println("Misma clase = "+ counter);
		  if ( counter[maxClass] < this.k2){ // Sino llega a k' eliminar
			  toClean [pos] = 1; // we will clean
			  //System.out.println("BORRAREMOS");
		  }else if(counter[maxClass] >= this.k2){ 
			  if(maxClass != class_p){
				  p.setFirstOutput(maxClass);
				 
			  }
		  }
		   
		  
		   pos++;
	}
	
	//Clean the prototypes.
	PrototypeSet aux= new PrototypeSet();
	for(int i= 0; i< toClean.length;i++){
		if(toClean[i] == 0)
			aux.add(T.get(i));
		
	}
	//Remove aux prototype set
	
	Sew = aux;
	
	//System.out.println("Result of filtering");	
	//Sew.print();

	return Sew;
	  
  }
  
  
  
  /**
   * Apply the ADE_CoForestGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm ADE_CoForest is starting...\n Computing...\n");
	  
	  PrototypeSet labeled, unlabeled;

	  double[] err = new double[this.num_classifier];             // e_i
	  double[] err_prime = new double[this.num_classifier];       // e'_i
	  double[] s_prime = new double[this.num_classifier];         // l'_i

	  boolean[][] inbags = new boolean[this.num_classifier][];
	  
	  boolean[] trigger = new boolean[this.num_classifier];

	  double[] effectRemove = new double[this.num_classifier]; // r_i     
	  
	  //obtaining labeled and unlabeled data and established indexes.
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  	  	
      for (int j=0; j< labeled.size();j++){
          labeled.get(j).setIndex(j); 
      }
      
      for (int j=0; j< unlabeled.size();j++){
    	  unlabeled.get(j).setIndex(j); 
      }
      
	  // In order to avoid problems with C45 and NB.
	  for(int p=0; p<unlabeled.size(); p++){
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor vÃ¡lido.
	  }
	  

	  //****************************************
	  
	  m_numOriginalLabeledInsts = labeled.size(); //from the original labeled data sets
	  RandomTree rTree = new RandomTree();
	  
	  // set up the random tree options
	  m_KValue = m_numFeatures;
	  if (m_KValue < 1)  m_KValue = (int) (Math.log(labeled.get(0).numberOfInputs())/Math.log(2)) +1;

	  m_classifiers = new RandomTree[this.num_classifier];
	  
	  for(int i=0; i< this.num_classifier; i++){
		  m_classifiers[i] = new RandomTree();
		  m_classifiers[i].setKValue(m_KValue);
	  }
	  
	  PrototypeSet [] labeleds = new PrototypeSet[this.num_classifier];
	  int[] randSeeds = new int[this.num_classifier];

		
		 
	  
	  for(int i = 0; i < this.num_classifier; i++)
	  {
		  ((RandomTree)m_classifiers[i]).setSeed(randSeeds[i]);
	      inbags[i] = new boolean[labeled.size()];
	      labeleds[i] = resampleWithWeights(labeled, i, inbags[i]);
	 //     labeleds[i].print();
	      m_classifiers[i].buildClassifier(labeleds[i]);
	    //  System.out.println("*******************FIN BUILD!");
	      err_prime[i] = 0.5;
	      s_prime[i] = 0;                                                //l'_i <-- 0
	  }
	    

	  //labeled.print();
	  //labeledBoostrapped[0].print();
	  
	  PrototypeSet[] Li = null;
		  
	  boolean bChanged = true;

	  /** repeat until none of h_i ( i \in {1...3} ) changes */
	    while(bChanged)
	    {
	      bChanged = false;
	      boolean[] bUpdate = new boolean[this.num_classifier];
	      Li = new PrototypeSet[this.num_classifier];


	      for(int i = 0; i < this.num_classifier; i++)
	      {
        
	    	  err[i] = measureError(labeled, inbags, i);
	          Li[i] = new PrototypeSet(); 

	          /** if (e_i < e'_i) */
	          if(err[i] < err_prime[i])
	          {
	            if(s_prime[i] == 0)
	            	s_prime[i] = Math.min(unlabeled.sumOfWeights() / 10, 100);
	            
	            
	            /** Subsample U for each hi */
	            double weight = 0;
	            unlabeled.randomize(this.SEED);
	            
	            int numWeightsAfterSubsample = (int) Math.ceil(err_prime[i] * s_prime[i] / err[i] - 1);
	            for(int k = 0; k < unlabeled.size(); k++)
	            {
	              weight +=  unlabeled.get(k).getWeight();
	              if (weight > numWeightsAfterSubsample)
	               break;
	              Li[i].add((Prototype)unlabeled.get(k));
	            }

	            /** for every x in U' do */
	            for(int j = Li[i].size() - 1; j > 0; j--)
	            {
	              Prototype curInst = Li[i].get(j);
	              if(!isHighConfidence(curInst, i))       //in which the label is assigned
	                Li[i].remove(j);
	            }//end of j

	            if(s_prime[i] < Li[i].size())
	            {
	              if(err[i] * Li[i].sumOfWeights() < err_prime[i] * s_prime[i])
	                bUpdate[i] = true;
	            }
	          }
	        }//end of for i

	        //update
	        RandomTree [] newClassifier = new RandomTree[this.num_classifier];
	       
	        
	        for(int i = 0; i < this.num_classifier; i++)
	        {
	        	newClassifier[i] = new RandomTree();
	        	
	          if(bUpdate[i])
	          {
	            double size = Li[i].sumOfWeights();

	            bChanged = true;
	            m_classifiers[i] = newClassifier[i];
	            ((RandomTree)m_classifiers[i]).setSeed(randSeeds[i]);
	            
	            for(int j = 0; j < labeled.size(); j++) // Combine labeled and Li.
		              Li[i].add(new Prototype(labeled.get(j)));
	            
	            
		          Li[i] =  new PrototypeSet(removeOnly(Li[i], labeled));

		          
	            m_classifiers[i].buildClassifier(Li[i]);
	            err_prime[i] = err[i];
	            s_prime[i] = size;
	          }
	        }

  
	    } //end of while

	  
	   
		  
	    // testing phase.
	    
	    
		  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
		  
		  int traPrediction[] = new int[tranductive.size()];
		  int tstPrediction[] = new int[test.size()];
		  int aciertoTrs = 0;
		  int aciertoTst = 0;
		  

	  //transductive phase
		  
	
		  for(int i=0; i<tranductive.size(); i++){
			  
			  // Voting RULE
			    traPrediction[i]=this.votingRule(tranductive.get(i));
		    
			    // maxIndex is the class label.		  
			    if(tranductive.get(i).getOutput(0) == traPrediction[i]){
					  aciertoTrs++;
			    }
				  
			    tranductive.get(i).setFirstOutput(traPrediction[i]);
		   }
			  
			  
		  // test phase
	      
		  for(int i=0; i<test.size(); i++){
			  
			  // Voting RULE
			    tstPrediction[i]=this.votingRule(test.get(i));
		    
			    // maxIndex is the class label.		  
			    if(test.get(i).getOutput(0) == tstPrediction[i]){
					  aciertoTst++;
			    }
				  
			    test.get(i).setFirstOutput(tstPrediction[i]);
		   }
		  
	    

		  System.out.println("% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  System.out.println("% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
	


	
	  
      return new Pair<PrototypeSet,PrototypeSet>(tranductive,test);
  }
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {  }

}
