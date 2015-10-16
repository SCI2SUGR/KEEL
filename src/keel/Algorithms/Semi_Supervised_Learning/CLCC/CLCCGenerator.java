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
	CLCC.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  4/3/2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.CLCC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;

import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerSMO;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Utilidades;

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


import java.util.StringTokenizer;



/**
 * This class implements the CLCC. You can use: Knn, C4.5, SMO and NB as classifiers.
 * @author triguero
 *
 */

public class CLCCGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private int  num_classifier;
 private double threshold = 0.75;
 private double beta = 0.4;
 private int initialCluster = 2;
 private int frequency = 3;
 private int set_num = 6;
 private boolean optionalPart = true;
 
 private int m_numOriginalLabeledInsts = 0;
 
 /** Number of features to consider in random feature selection.
 If less than 1 will use int(logM+1) ) */
protected int m_numFeatures = 0;

/** Final number of features that were considered in last build. */
protected int m_KValue = 0;

 
 private int [][] predictions;
 private double [][] confidence;
 

// private String final_classifier; 

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  RandomTree [] m_classifiers;
  
  
  /**
   * Build a new CLCCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public CLCCGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="CLCC";
      
  }
  


  /**
   * Build a new CLCCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public CLCCGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="CLCC";

      this.predictions = new int[6][];
      
     
      this.num_classifier = parameters.getNextAsInt();
      this.threshold = parameters.getNextAsDouble();
      this.beta = parameters.getNextAsDouble();
      this.initialCluster = parameters.getNextAsInt();
      

      this.frequency = parameters.getNextAsInt();
      this.set_num  = parameters.getNextAsInt();
      if(parameters.getNextAsString().equalsIgnoreCase("true")){
    	  this.optionalPart = true;
      }else{
    	  this.optionalPart = false;
      }
      
      //this.final_classifier = parameters.getNextAsString();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
   
      if(this.initialCluster < this.numberOfClass || this.initialCluster > 2* this.numberOfClass) // if the value is not correctly specified
      {
    	  this.initialCluster = RandomGenerator.Randint(this.numberOfClass, 2*this.numberOfClass);
      }
   
      
      System.out.println("Initial Cluster = "+this.initialCluster);
      
     
                                      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

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
	    
	    this.confidence[inst.getIndex()] = res.clone(); // MODIFIED FOR CLCC ALGORITHM!!
	    
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
   * It applies a coforest-sim algorithm and fill the this.confidence matrix.
   * @param labeled
   * @param unlabeled
   * @return
   * @throws Exception
   */
  
  public PrototypeSet co_forest_sim(PrototypeSet labeled, PrototypeSet unlabeled) throws Exception{
	  

	  
	  double[] err = new double[this.num_classifier];             // e_i
	  double[] err_prime = new double[this.num_classifier];       // e'_i
	  double[] s_prime = new double[this.num_classifier];         // l'_i

	  boolean[][] inbags = new boolean[this.num_classifier][];
	  
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
	            
	            m_classifiers[i].buildClassifier(Li[i]);
	            err_prime[i] = err[i];
	            s_prime[i] = size;
	          }
	        }

  
	    } //end of while
	    
	    
		  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  
		  this.confidence = new double[tranductive.size()][this.numberOfClass]; // for the next step.
		  
		  int traPrediction[] = new int[tranductive.size()];
		  int aciertoTrs = 0;
		  
		  for(int i=0; i<tranductive.size(); i++){
			  tranductive.get(i).setIndex(i); // establish de index
			  
			  // Voting RULE
			    traPrediction[i]=this.votingRule(tranductive.get(i));
		    
			    // maxIndex is the class label.		  
			    if(tranductive.get(i).getOutput(0) == traPrediction[i]){
					  aciertoTrs++;
			    }
				  
			    tranductive.get(i).setFirstOutput(traPrediction[i]);
		   }
		  
		  
		  System.out.println("% de acierto TRS Coforest = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  
		 
		  
		  
	    return tranductive;
  }
  
  
  public double penalty(int numCluster, int n){
	  
	  if (numCluster >= this.numberOfClass){
		  return Math.sqrt((numCluster-this.numberOfClass)/(n*1.));
	  }else{
		  return 0.;
	  }
  }
  
  public double objetiveFunction(PrototypeSet[] cluster, PrototypeSet Lstar, PrototypeSet centers){ //int currentCluster, int Nj
	  double result =0;
	  int n = Lstar.size();
	  
	  for(int j=0; j< cluster.length; j++){ // for each cluster

		  int clase = (int) centers.get(j).getOutput(0); // the same class than the center of the cluster
		  
		  for(int i=0; i< cluster[j].size(); i++){
			  int indice = cluster[j].get(i).getIndex(); // index of the prototype inserted in the cluster (Corresponing with the confidence

			  result+= (1.-this.confidence[indice][clase])/(n*1.) ;
		  }
	  }
	  
	  result += this.beta*penalty(cluster.length, n);
	  
	 // System.out.println("result = "+result);
	  return result;
  }
  

  /**
   * Create a cluster from the centers and the complete set of prototypes
   * @param centers
   * @param set
   * @return
   */
  
  public PrototypeSet[] createCluster(PrototypeSet clusterCenters, PrototypeSet Lstar){
	  PrototypeSet clusters [] = null;
	  
	  clusters = new PrototypeSet[clusterCenters.size()];
	  for(int i=0; i< clusterCenters.size(); i++){
		  clusters[i] = new PrototypeSet();
	  }
	  
	  for(int j=0; j< Lstar.size(); j++){ // for each prototype
		  Prototype near = clusterCenters.nearestTo(Lstar.get(j));  // near have the index of its center.
		  //System.out.println(near.getIndex());
		  clusters[near.getIndex()].add(new Prototype(Lstar.get(j))); // adding the prototype to its corresponding cluster.
	  }
  
	  return clusters;
  
  }
  
  /**
   * 
   * @param Lstar
   */
  public PrototypeSet[] localClusterCenter(PrototypeSet Lstar){
	 
	  //step 1: initialize
	  PrototypeSet CX [] = new PrototypeSet [this.frequency]; // in each iteration the best centers are stored in CX.
	  
	  PrototypeSet clusterCenters = new PrototypeSet();   // CMS
	  PrototypeSet clusters [] = null;   				// CR
	  
	  for (int i=0; i< this.frequency; i++){ // step 2
		  
		  clusterCenters = new PrototypeSet();   // CMS
		  
		  // 3) Select k cluster randomly from Lstar: Checking if all the classes have been included: (added by ISaac)

		 /*
		  for (int j=0; j<this.numberOfClass;j++){
			  PrototypeSet delaClase = Lstar.getFromClass(j);
			  if(delaClase.size()>0){
				  clusterCenters.add(new Prototype(delaClase.getRandom()));
				  clusterCenters.get(j).setIndex(j); // establish its index.
			  }
		  }
		 
		*/
		  
		  ArrayList <Integer> lista  = RandomGenerator.generateDifferentRandomIntegers(0, Lstar.size()-1);
		  for(int j=0; j< this.initialCluster; j++){ //this.numberOfClass
			  clusterCenters.add(new Prototype(Lstar.get(lista.get(j))));
			  clusterCenters.get(j).setIndex(j); // establish its index.
		  }
		  

		  // 4) Cluster are created:
		  clusters = createCluster(clusterCenters, Lstar); // in each iteration
		  
		  // Greedy search.
		  boolean change = true;
		  
		  PrototypeSet clusterCentersAdd = new PrototypeSet(clusterCenters.clone()); //CMSadd
		  PrototypeSet clustersAdd [] = null;                                          // CRadd
		  
		  PrototypeSet clusterCentersRe = new PrototypeSet(clusterCenters.clone()); // CMSre
		  PrototypeSet clustersRe [] = null;										// CRre
		  
		  while (change){ // step 5
			  
			//  System.out.println("Greedy time");
			 // System.out.println("Size = "+clusterCenters.size());
 
			  clusterCentersAdd = new PrototypeSet(clusterCenters.clone());
			  clusterCentersRe = new PrototypeSet(clusterCenters.clone());
			  
			  // 6a) Adding a single non-center object in Lstar ot the set of centers.
			  boolean inserted= false;
			  
			  while(!inserted){
				  inserted = true;
				  Prototype random = Lstar.getRandom();
				  
				  for (int  j=0; j< clusterCenters.size(); j++){ // checking that it is not include in clusterCenters.
					  if(random.equals(clusterCenters.get(j))){
						  inserted = false;
					  }
				  }
  
				  if(inserted){
					  clusterCentersAdd.add(new Prototype(random));
					  clusterCentersAdd.get(clusterCentersAdd.size()-1).setIndex(clusterCentersAdd.size()-1);
				  }
			  }
			  
			  clustersAdd = createCluster(clusterCentersAdd, Lstar); // create cluster.
			  
			  // 6b) Remove a center object from the set of centers.
			  int borrar = RandomGenerator.Randint(0, clusterCenters.size()-1);
			  
			  if(clusterCenters.size()>this.numberOfClass){
				  clusterCentersRe.remove(borrar); // I only remove if i have enough clusters
			  
				  // re-establish indexes of clusterCenterRe
				  for(int j=0; j<clusterCentersRe.size(); j++){
					  clusterCentersRe.get(j).setIndex(j);
				  }
			  
			  }
			  clustersRe = createCluster(clusterCentersRe, Lstar);// create cluster.
			  
			  
			  // 7) Select the best clusters from clustersAdd and clustersRe
			  
			  double Eadd = objetiveFunction(clustersAdd, Lstar, clusterCentersAdd);
			  double Ere = objetiveFunction(clustersRe, Lstar, clusterCentersRe);
			  double Enew;
			  
			  PrototypeSet clusterCentersNew = null;   // CMSnew
			  PrototypeSet clustersNew [] = null;      // CRnew
			  
			  
			  if(Eadd < Ere){
				  clusterCentersNew = new PrototypeSet(clusterCentersAdd.clone());
				  clustersNew = clustersAdd.clone();
				  Enew = Eadd;
			//	  System.out.println("Add");
			  }else{
				  clusterCentersNew = new PrototypeSet(clusterCentersRe.clone());
				  clustersNew = clustersRe.clone();
				  Enew = Ere;
				//  System.out.println("Re");
			  }
			  

			  
			  // 8) Checking if the new clustering is better or not in terms of objetive function.
			  
			 // System.out.println("Clusters size = "+ clusters.length);
			 // System.out.println("ClustersCenter size = "+ clusterCenters.size());
			  
			  double Eold = objetiveFunction(clusters, Lstar, clusterCenters);
	
			  if(Enew < Eold){
				  clusterCenters = new PrototypeSet(clusterCentersNew.clone());  // CMS = CMSNew
				  clusters = clustersNew.clone();
			  }else if((Enew == Eold) && clustersNew.length< clusters.length){
				  clusterCenters = new PrototypeSet(clusterCentersNew.clone());  // CMS = CMSNew
				  clusters = clustersNew.clone();
			  }else{
				  change  = false;
				  CX[i] = new PrototypeSet(clusterCenters.clone());  // CX = CX U CMS
			  }
				  
				  
			  
		  }
	  }
	  
	  
	  return CX;
	  
  }
   
  
  /**
   * 
   * @param CX
   * @return
   */
  public PrototypeSet ProcessCluster(PrototypeSet [] CX, PrototypeSet Lstar){
	  PrototypeSet LabeledStarStar = new PrototypeSet(Lstar.clone()); // the generated prototypeSet.
	  
	  PrototypeSet clusters[][] = new PrototypeSet[CX.length][];   				// CR
	  double E[] = new double [CX.length];
	  
	  // 1) Identify the best set of cluster's centers. 
	  double bestObjective = Double.MAX_VALUE;
	  int bestObject = -1;
	  
	  for (int i=0; i< CX.length; i++){
		  clusters[i] = createCluster(CX[i], Lstar); // Create the cluster.
		  E[i] = objetiveFunction(clusters[i], Lstar, CX[i]);
		  if(E[i]< bestObjective){
			  bestObjective = E[i];
			  bestObject = i;
		  }
	  }
	  
	  // 2) for each prototype of the center set.
	  
	  int classCluster[]= new int [CX[bestObject].size()];
	  
	  for (int i=0; i< CX[bestObject].size(); i++){
		  
		  // Divide the cluster into three different segments: but I only use 2 segments
		  
		  // each segment will be formed by num_objects/ 3
		  int examplesPerSegment = clusters[bestObject][i].size()/3;		  
		  
		//  System.out.println("Examples per segment: "+ examplesPerSegment);
		  		  
		  classCluster[i] =(int) CX[bestObject].get(i).getOutput(0); // obtaining the class of the center.
		  
		 // System.out.println("Clase cluster: "+ classCluster[i]);
		  
		  // obtaining the ''examplesPerSegment'' with the highest confidence and closets to the cluster Center.
		  // obtaining the ''examplesPerSegment'' with the lowest confidence and closets to the cluster Center.
		  
	  
		  double confianza[] = new double[clusters[bestObject][i].size()];
		  double distancia [] = new double[clusters[bestObject][i].size()];
		  double combinacion[] = new double[clusters[bestObject][i].size()];
		  
		  for(int j=0; j<clusters[bestObject][i].size(); j++){ // for each prototype of the cluster i
			  int indice = clusters[bestObject][i].get(j).getIndex();
			  confianza[j] =  this.confidence[indice][classCluster[i]];
			  distancia[j] = Distance.d(clusters[bestObject][i].get(j), CX[bestObject].get(i));		
			  combinacion[j] = ((1-confianza[j])+distancia[j])/2; // we have to minimize this value.
		  }
		  
		  int position[] = Utils.stableSort(combinacion);
		 
		  
		  // we have to choose the examplesPerSegment for the first segment
		  // and the last examplesPerSegmente for the last segement.
		  
		  // step 4: change the class-label of objects in first segment to Clasi and remove the objects in the last segment.
		  
		  for (int z=0; z<examplesPerSegment; z++){ // relabel first segment.
			  clusters[bestObject][i].get(position[z]).setFirstOutput(classCluster[i]);
			  this.confidence[clusters[bestObject][i].get(position[z]).getIndex()][classCluster[i]] = 1;
		  }
		  
		  int aBorrar[]  = new int[examplesPerSegment];
		  int cont=0;	  
		  
		  for (int z=(clusters[bestObject][i].size()-examplesPerSegment); z<clusters[bestObject][i].size(); z++){ // remove last segment.
			aBorrar[cont] = position[z];
			cont++;
		  }
		  
		  Arrays.sort(aBorrar);
		  
		  for(int z=examplesPerSegment-1; z>=0; z--){
			  clusters[bestObject][i].remove(aBorrar[z]);
		  }
		

		  
	  }
	  
	  
	  LabeledStarStar = new PrototypeSet();
	  
	  for (int i=0; i< CX[bestObject].size(); i++){
		  LabeledStarStar.add(new PrototypeSet(clusters[bestObject][i].clone()));
	  }

	  
	  return LabeledStarStar;
  }
  
  
  /**
   * Apply the CLCCGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm CLCC is starting...\n Computing...\n");
	  
	  PrototypeSet labeled, unlabeled;
	  
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
	  
	  // Step 1: applying co-forest-sim
	  
	  PrototypeSet Lstar = new PrototypeSet(co_forest_sim(labeled, unlabeled).clone());  // the confidence vector has been stored in this.confidence

      for (int j=0; j< Lstar.size();j++){
          Lstar.get(j).setIndex(j); 
      }
      
	  	
	  // Step 2: Center-based supervised clustering is trained in Lstar
	  
      System.out.println("****");
	  PrototypeSet[] CX = localClusterCenter(Lstar);
		  

	   if(this.optionalPart){ // steps 3 and 4 are optional.
		   
	       // Step 3: Process cluster
	   
	       PrototypeSet LstarStar = ProcessCluster(CX, Lstar);


	       // Step 4: Center-based supervised clustering is trained  agin in Lstar
		  
	       CX = localClusterCenter(LstarStar);
	   }
	   
	   
	   // step 5: Select the first ''this.set_num center'' sets as the CMS_Set accoring to the value of the objective function.
	   
	   PrototypeSet clusters[][] = new PrototypeSet[CX.length][];   	
	   double bestClusters [] = new double [this.set_num];
	   int indexBestClusters[] = new int[this.set_num];
	   Arrays.fill(bestClusters, Double.MAX_VALUE);
	   Arrays.fill(indexBestClusters, -1);
	   
	   
	   double E[] = new double [CX.length];
		  
	   System.out.println("Cx length "+ CX.length);
	   
		
		
		for (int i=0; i< CX.length; i++){
			clusters[i] = createCluster(CX[i], Lstar); // Create the cluster.
			E[i] = objetiveFunction(clusters[i], Lstar, CX[i]);
		//	System.out.println(E[i]);
	    }
		 
		System.out.println("*************");
			
		int position[] = Utils.stableSort(E); // sort in ascending order.
			
		// Step 6: Train ''set_num" of 1-NN classifiers by using center set CMS in CMS_Set
		
		// construct the final prototypesets

	      
		PrototypeSet finalSet [] = new PrototypeSet[this.set_num];
		
		for(int i=0; i< this.set_num; i++){
			finalSet[i] = new PrototypeSet();
			
			//System.out.println(position[i]);
			
			for(int j=0; j<CX[position[i]].size(); j++ ){
				finalSet[i].add(CX[position[i]].get(j));
			}
			
			System.out.println(finalSet[i].size());
			  
			
		}
	  
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
			  double prob[] = new double[this.numberOfClass];
			  Arrays.fill(prob,0);
 			  
				for(int j=0; j<this.set_num; j++ ){ // for each classifier
					Prototype cercano = finalSet[j].nearestTo(tranductive.get(i));
					prob[(int)cercano.getOutput(0)]++; // increment this counter
				}
				
			   // determine the class.
				double maximo = Double.MIN_VALUE;
				
				for(int j=0; j< this.numberOfClass; j++){
					if(prob[j] > maximo){
						maximo = prob[j];
						traPrediction[i] = j;
					}
				}
				
		        if(tranductive.get(i).getOutput(0) == traPrediction[i]){
					  aciertoTrs++;
			    }
				  
			    tranductive.get(i).setFirstOutput(traPrediction[i]);
		   }
			  
			  
		  // test phase
	      
		  for(int i=0; i<test.size(); i++){
			  
			  // Voting RULE
		
			  double prob[] = new double[this.numberOfClass];
			  Arrays.fill(prob,0);
 			  
				for(int j=0; j<this.set_num; j++ ){ // for each classifier
					Prototype cercano = finalSet[j].nearestTo(test.get(i));
					prob[(int)cercano.getOutput(0)]++; // increment this counter
				}
				
			   // determine the class.
				double maximo = Double.MIN_VALUE;
				
				for(int j=0; j< this.numberOfClass; j++){
					if(prob[j] > maximo){
						maximo = prob[j];
						tstPrediction[i] = j;
					}
				}
		    
			   
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
