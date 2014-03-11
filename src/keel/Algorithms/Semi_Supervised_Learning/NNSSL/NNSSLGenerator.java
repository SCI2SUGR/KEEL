
/*
	NNSSL.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.NNSSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import keel.Dataset.Attributes;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Self-traning wrapper. You can use: Knn, C4.5, SMO and Ripper as classifiers.
 * @author triguero
 *
 */

public class NNSSLGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String classifier; 
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new NNSSLGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public NNSSLGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="NNSSL";
      
  }
  


  /**
   * Build a new NNSSLGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public NNSSLGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="NNSSL";
   
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  /**
   * Apply the NNSSLGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm()
  {
	  System.out.print("\nThe algorithm SELF TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  
  
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	    
		  
		  //We have to return the classification done.
		  for(int i=0; i<this.transductiveDataSet.size(); i++){
			   tranductive.get(i).setFirstOutput((labeled.nearestTo(this.transductiveDataSet.get(i))).getOutput(0));
		  }
		  
		  for(int i=0; i<this.testDataSet.size(); i++){
			  test.get(i).setFirstOutput((labeled.nearestTo(this.testDataSet.get(i))).getOutput(0));
		  }
		  
		
		  // Transductive Accuracy 
		  System.out.println("AccTrs ="+KNN.classficationAccuracy1NN(labeled,this.transductiveDataSet)*100./this.transductiveDataSet.size());
		  
		  // test accuracy
		  System.out.println("AccTst ="+KNN.classficationAccuracy1NN(labeled,this.testDataSet)*100./this.testDataSet.size());
  
	
	  

	  
	  
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