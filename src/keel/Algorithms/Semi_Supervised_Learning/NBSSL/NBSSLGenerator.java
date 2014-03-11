
/*
	NBSSL.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.NBSSL;

import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.ParametersC45;
import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Self-traning wrapper. You can use: Knn, C4.5, SMO and Ripper as classifiers.
 * @author triguero
 *
 */

public class NBSSLGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new NBSSLGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public NBSSLGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="NBSSL";
      
  }
  


  /**
   * Build a new NBSSLGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public NBSSLGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="NBSSL";
   
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  public int [] applyNB(PrototypeSet labeled, PrototypeSet unlabeled)  throws Exception{

  
	  // labeled.save("labeled.dat");
	  // unlabeled.save("unlabeled.dat");
	
	  
     // HandlerNB nb = new HandlerNB("labeled.dat", "unlabeled.dat", unlabeled.size(), this.numberOfClass);      // C4.5 called
      
     HandlerNB nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), unlabeled.prototypeSetTodouble(), unlabeled.prototypeSetClasses(),this.numberOfClass);
      
      int[] pre = nb.getPredictions();    
      
      for(int i=0; i< pre.length;i++){
    	  System.out.print(pre[i]+",");
      }
      return pre;
  }
  
  /**
   * Apply the NBSSLGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm SELF TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	    
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	    
	  int traPrediction[] = applyNB(labeled, tranductive);
	  int tstPrediction[] = applyNB(labeled, test);
	  
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
		  //We have to return the classification done.
		  for(int i=0; i<this.transductiveDataSet.size(); i++){
			  if(tranductive.get(i).getOutput(0) == traPrediction[i]){
				  aciertoTrs++;
			  }
			  
			  tranductive.get(i).setFirstOutput(traPrediction[i]);
		  }
		  
		  System.out.println("% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  
		  for(int i=0; i<this.testDataSet.size(); i++){
			  if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
			  }
			  test.get(i).setFirstOutput(tstPrediction[i]);
		  }
		  
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
