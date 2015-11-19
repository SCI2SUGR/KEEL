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
	TriTraining.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  4/3/2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.TriTraining;

import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;

import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerSMO;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

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

public class TriTrainingGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String [] classifier;
 private int [][] predictions;
 private double [][][] probabilities;
// private String final_classifier; 

    /**
     * Number of prototypes.
     */
    protected int numberOfPrototypes;  // Particle size is the percentage

    /**
     * Number of classes.
     */
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  private C45 c45;
  private HandlerSMO smo;
  private HandlerNB nb;
  
  /**
   * Build a new TriTrainingGenerator Algorithm
   * @param _trainingDataSet Original prototype set to be reduced.
     * @param neigbors number of neighbours considered. (not used)
     * @param poblacion population size. (not used)
   * @param perc Reduction percentage of the prototype set.
     * @param iteraciones number of iterations. (not used)
     * @param wend ending w value. (not used)
     * @param c1 class 1 value. (not used)
     * @param vmax maximum v value. (not used)
     * @param c2 class 2 value. (not used)
     * @param wstart starting w value. (not used)
   */
  public TriTrainingGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="TriTraining";
      
  }
  


  /**
   * Build a new TriTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
     * @param test Origital test prototype set.
     * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public TriTrainingGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="TriTraining";

      this.classifier = new String[3];
      this.predictions = new int[3][];
      
     
      this.classifier[0] = parameters.getNextAsString();
      this.classifier[1] = parameters.getNextAsString();
      this.classifier[2] = parameters.getNextAsString();
      //this.final_classifier = parameters.getNextAsString();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      this.probabilities = new double[3][][];
                                      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
    /**
     * Asks for the garbage collector.
     */
    public void getSolicitaGarbageColector(){

	  try{
	//  System.out.println( "********** INICIO: 'LIMPIEZA GARBAGE COLECTOR' **********" );
	  Runtime basurero = Runtime.getRuntime();
	//  System.out.println( "MEMORIA TOTAL 'JVM': " + basurero.totalMemory() );
	 // System.out.println( "MEMORIA [FREE] 'JVM' [ANTES]: " + basurero.freeMemory() );
	  basurero.gc(); //Solicitando ...
	 // System.out.println( "MEMORIA [FREE] 'JVM' [DESPUES]: " + basurero.freeMemory() );
	  //System.out.println( "********** FIN: 'LIMPIEZA GARBAGE COLECTOR' **********" );
	  }
	  catch( Exception e ){
	  e.printStackTrace();
	  }
	  
  
  }
  
  
  /**
   * Classify a test set with the algorithm specified.
   * @param idAlg classifier id to use (KNN, C4.5, SMO or NB)
   * @param train training dataset to build the model.
   * @param test test dataset to evaluate.
   * @param save. It indicates if it will save the results in the variable PREDICTIONS!
   * @return predicted classes for the test instances.
   * @throws Exception
   */
  
  public int[] classify(int idAlg,PrototypeSet train, PrototypeSet test, boolean save) throws Exception{
	 

	  getSolicitaGarbageColector();
	  
	  int [] pre = new int[test.size()];
	  
	  if(this.classifier[idAlg].equalsIgnoreCase("NN")){
		  //train.print();
		  //test.print();
		  
		 // System.out.println("Ejecuto NN");
		  
  		  for(int i=0; i<test.size(); i++){
  			  Prototype clase = train.nearestTo(test.get(i));
  			  if(clase==null){ //test has the train instance.
  				  System.out.println("SOY NULL");
  			  }else{
  				  pre[i] = (int)clase.getOutput(0);
  			 }
  		  }
  		 
  		probabilities[idAlg] = new double[test.size()][this.numberOfClass];
  		  
  		 for (int q=0; q<test.size(); q++){  // for each unlabeled.
			  
			  Prototype NearClass[] = new Prototype[this.numberOfClass];

			  
			  double sumatoria = 0;
			  for (int j=0 ; j< this.numberOfClass; j++){
				 // unlabeled.get(q).print();
				 // System.out.println("Labeled size = "+labeled.getFromClass(j).size());
				  if(train.getFromClass(j).size() >0){
				  
					  NearClass[j] = new Prototype (train.getFromClass(j).nearestTo(test.get(q)));				  
					  probabilities[idAlg][q][j] = Math.exp(-1*(Distance.absoluteDistance(NearClass[j], test.get(q))));
					  sumatoria+= probabilities[idAlg][q][j];
				  }else{
					  probabilities[idAlg][q][j] = 0;
				  }
			  }
			  
			  for (int j=0 ; j< this.numberOfClass; j++){
				  probabilities[idAlg][q][j]/=sumatoria;
			  }
		  
		  }
  		  
	  }else if(this.classifier[idAlg].equalsIgnoreCase("C45")){
		 // System.out.println("Ejecuto C45");
		  
  		 this.c45 = new C45(train.toInstanceSet(), test.toInstanceSet());      // C4.5 called
  		  pre = c45.getPredictions();    
  		
  		  probabilities[idAlg] = c45.getProbabilities();
  		  
	  }else if(this.classifier[idAlg].equalsIgnoreCase("SMO")){
		  //System.out.println("Ejecuto SMO");
  		  this.smo = new HandlerSMO(train.toInstanceSet(), test.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
  		  pre = this.smo.getPredictions(0);  
  		
  		  probabilities[idAlg] = this.smo.getProbabilities();
	  }else if(this.classifier[idAlg].equalsIgnoreCase("NB")){
		//  System.out.println("Ejecuto NB");
		//	 train.save("tra.dat");
		//	 test.save("tst.dat");
		//    this.nb = new HandlerNB("tra.dat", "tst.dat", test.size(), this.numberOfClass);      
		  this.nb = new HandlerNB(train.prototypeSetTodouble(), train.prototypeSetClasses(), test.prototypeSetTodouble(), test.prototypeSetClasses(),this.numberOfClass);
		  pre = this.nb.getPredictions().clone();   
		  probabilities[idAlg] = this.nb.getProbabilities().clone();
		  
	  }

	  if(save){
		  this.predictions[idAlg] = new int[test.size()];
		  this.predictions[idAlg] = pre.clone();
	  }
	  
	  getSolicitaGarbageColector();
	  
	  return pre;
  }
  
  /**
   * Measure combined error excluded the classifier 'id' on the given data set
   *
   * @param data Instances The data set
   * @param id int The id of classifier to be excluded
   * @return double The error
   * @throws Exception Some Exception
   */
  protected double measureError(PrototypeSet data, int id) throws Exception
  {
	
	int c1 = (id+1)%3;
	int c2 = (id+2)%3;
	
    double err = 0;
    int count = 0;

    for(int i = 0; i < data.size(); i++)
    {

      if(this.predictions[c1][i] == this.predictions[c2][i])
      {
        count++;
        if(this.predictions[c1][i] != data.get(i).getOutput(0))
          err += 1.;
      }
    }

    err /= count;
    
    return err;
  }

    /**
     * Classifies a instance with the given index.
     * @param InstanceID index of the instance to classify.
     * @return predicted class of the instance given.
     */
    public int votingRule(int InstanceID){
	  
	    double[] res = new double[this.numberOfClass];
	    for(int j = 0; j < 3; j++)
	    {
	      double[] distr = this.probabilities[j][InstanceID];//m_classifiers[i].distributionForInstance(inst); // Probability of each class.
	      for(int z = 0; z < res.length; z++)
	        res[z] += distr[z];
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
   * Apply the TriTrainingGenerator method.
     * @return transductive test sets.
     * @throws java.lang.Exception if the algorithm can not be applied.
     */
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm TRI-TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled, unlabeled;
	  PrototypeSet labeledBoostrapped[] = new PrototypeSet[3]; // for each classiffier.

	  
	  double[] err = new double[3];             // e_i
	  double[] err_prime = new double[3];       // e'_i
	  double[] s_prime = new double[3];         // l'_i
	  
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
	  

//*******************************
	  
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	  
	  int traPrediction[] = new int[tranductive.size()];
	  int tstPrediction[] = new int[test.size()];
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  


		
		 
	  
	  for(int i = 0; i < 3; i++)
	  {
		  labeledBoostrapped[i] = new PrototypeSet(labeled.resample());     //L_i <-- Bootstrap(L)
	      this.classify(i,labeledBoostrapped[i], labeled, true);//h_i <-- Learn(L_i)  Classify LABELED DATA!
	      
	 
	      err_prime[i] = 0.5;                                                   //e'_i <-- .5
	      s_prime[i] = 0;                                                       //l'_i <-- 0
	  }
	    

	  
	  
  //transductive phase
	  
      for(int i = 0; i < 3; i++)
      {
    	   if(labeledBoostrapped[i] == null){ // It can ocurrs if equation 9 is not satisfied
    		   labeledBoostrapped[i] = new PrototypeSet(labeled.clone());
    	   }

    	   this.classify(i, labeledBoostrapped[i], tranductive, true);//h_i <-- Learn(L \cup L_i)
      }


	  for(int i=0; i<tranductive.size(); i++){
		  
		  // Voting RULE
		    traPrediction[i]=this.votingRule(i);
	    
		    // maxIndex is the class label.		  
		    if(tranductive.get(i).getOutput(0) == traPrediction[i]){
				  aciertoTrs++;
		    }
			  
		    tranductive.get(i).setFirstOutput(traPrediction[i]);
	   }
		  
		  
	  // test phase
      for(int i = 0; i < 3; i++)
      {
    	   if(labeledBoostrapped[i] == null){ // It can ocurrs if equation 9 is not satisfied
    		   labeledBoostrapped[i] = new PrototypeSet(labeled.clone());
    	   }
    	  this.classify(i, labeledBoostrapped[i], test, true);//h_i <-- Learn(L \cup L_i)
      }
      
	  for(int i=0; i<test.size(); i++){
		  
		  // Voting RULE
		    tstPrediction[i]=this.votingRule(i);
	    
		    // maxIndex is the class label.		  
		    if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
		    }
			  
		    test.get(i).setFirstOutput(tstPrediction[i]);
	   }
	  
    
	//  System.out.println("Labeled size "+ labeledBoostrapped[0].size());
	 // System.out.println("Initial % de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
	 // System.out.println("Initial % de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
  
	  //************************************************************
	  
	  
	  //labeled.print();
	  //labeledBoostrapped[0].print();
	  
	  PrototypeSet[] Li = null;
	  PrototypeSet[] LiSaved = new PrototypeSet[3];
	  
	  boolean bChanged = true;

	  /** repeat until none of h_i ( i \in {1...3} ) changes */
	    while(bChanged)
	    {
	      bChanged = false;
	      boolean[] bUpdate = new boolean[3];
	      Li = new PrototypeSet[3];

	      
	      
	      /** for i \in {1...3} do */
	      for(int i = 0; i < 3; i++)
	      {
	        Li[i] = new PrototypeSet();         //L_i <-- \phi
	        // Measure error in the labeled DATA! THEY ASSUME that unlabeled data hold the same distribution as that held by the labeled ones.
	        
	        err[i] = measureError(labeled, i);         //e_i <-- MeasureError(h_j & h_k) (j, k \ne i)

	       // System.out.println("Error = "+ err[i]);
	        /** if (e_i < e'_i) */
	        if(err[i] < err_prime[i])
	        {
	        //	System.out.println("ENTRO AKI!");
	          /** for every x \in U do */
	          int preC1[],preC2[];
	                    
	          preC1 = this.classify(((i+1)%3), labeledBoostrapped[i], unlabeled, false);
	          preC2 = this.classify(((i+2)%3), labeledBoostrapped[i], unlabeled, false);  // I don't want to modifiy the prediction variables.
	        	
	          for(int j = 0; j < unlabeled.size(); j++)
	          {
	            Prototype curInst = new Prototype(unlabeled.get(j));
	            
	            //curInst.setDataset(Li[i]);
	            
	            double classval = preC1[j];//m_classifiers[(i+1)%3].classifyInstance(curInst);

	            
	         /*   if(classval==-1){
	            	System.err.println("ESTO K ES!!");
	            	unlabeled.get(j).print();
	            }
	           */
	            
	            /** if h_j(x) = h_k(x) (j,k \ne i) */
	            if(classval ==  preC2[j] && classval!=-1)
	            {
	              curInst.setFirstOutput(classval); //  setClassValue(classval);
	              Li[i].add(curInst);                //L_i <-- L_i \cup {(x, h_j(x))}
	           //  System.out.println("aÃ±ado instancia");
	            }
	            
	            
	          }// end of for j

	          /** if (l'_i == 0 ) */
	          if(s_prime[i] == 0)
	            s_prime[i] = Math.floor(err[i] / (err_prime[i] - err[i]) + 1);   //l'_i <-- floor(e_i/(e'_i-e_i) +1)

	          /** if (l'_i < |L_i| ) */
	          if(s_prime[i] < Li[i].size())
	          {
	            /** if ( e_i * |L_i| < e'_i * l'_i) */
	            if(err[i] * Li[i].size() < err_prime[i] * s_prime[i])
	              bUpdate[i] = true;                                          // update_i <-- TURE

	            /** else if (l'_i > (e_i / (e'_i - e_i))) */
	            else if (s_prime[i] > (err[i] / (err_prime[i] - err[i])))
	            {
	              int numInstAfterSubsample = (int) Math.ceil(err_prime[i] * s_prime[i] / err[i] - 1);
	              Li[i].randomize(this.SEED);
	              Li[i] = new PrototypeSet(Li[i],0, numInstAfterSubsample);  //L_i <-- Subsample(L_i, ceilling(e'_i*l'_i/e_i-1)
	              bUpdate[i] = true;                                              //update_i <-- TRUE
	            }
	          }
	        }
	      }//end for i = 1...3

	      
	     
	      
	      //update
	      for(int i = 0; i < 3; i++)
	      {
	        /** if update_i = TRUE */
	        if(bUpdate[i])
	        {
	          int size = Li[i].size();
	          bChanged = true;
	          
	          for(int j = 0; j < labeled.size(); j++) // Combine labeled and Li.
	              Li[i].add(new Prototype(labeled.get(j)));
	          
	          // save Li
	          LiSaved[i]  = new PrototypeSet(Li[i].clone());
	          labeledBoostrapped[i] = new PrototypeSet(Li[i].clone());
	          
	          this.classify(i, Li[i], labeled, true);//h_i <-- Learn(L \cup L_i)
	          //m_classifiers[i].buildClassifier(combine(labeled, Li[i]));        
	          err_prime[i] = err[i];                                            //e'_i <-- e_i
	          s_prime[i] = size;                                                //l'_i <-- |L_i|
	          
	          
	          tranductive = new PrototypeSet(this.transductiveDataSet.clone());
			  test = new PrototypeSet(this.testDataSet.clone());
			  
			  traPrediction = new int[tranductive.size()];
			  tstPrediction= new int[test.size()];
			  aciertoTrs = 0;
			  aciertoTst = 0;
			  /*
//transductive phase
			  
		      for(int m= 0; m< 3; m++)
		      {
		    	   if(LiSaved[m] == null){ // It can ocurrs if equation 9 is not satisfied
		    		   LiSaved[m] = new PrototypeSet(labeled.clone());
		    	   }

		    	   this.classify(m, LiSaved[m], tranductive, true);//h_m<-- Learn(L \cup L_i)
		      }

		
			  for(int m=0; m<tranductive.size(); m++){
				  
				  // Voting RULE
				    traPrediction[m]=this.votingRule(m);
			    
				    // maxIndex is the class label.		  
				    if(tranductive.get(m).getOutput(0) == traPrediction[m]){
						  aciertoTrs++;
				    }
					  
				    tranductive.get(m).setFirstOutput(traPrediction[m]);
			   }
				  
				  
			  // test phase
		      for(int m= 0; m< 3; m++)
		      {
		    	   if(LiSaved[m] == null){ // It can ocurrs if equation 9 is not satisfied
		    		   LiSaved[m] = new PrototypeSet(labeled.clone());
		    	   }
		    	  this.classify(m, LiSaved[m], test, true);//h_m<-- Learn(L \cup L_i)
		      }
		      
			  for(int m=0; m<test.size(); m++){
				  
				  // Voting RULE
				    tstPrediction[m]=this.votingRule(m);
			    
				    // maxIndex is the class label.		  
				    if(test.get(m).getOutput(0) == tstPrediction[m]){
						  aciertoTst++;
				    }
					  
				    test.get(m).setFirstOutput(tstPrediction[m]);
			   }
			  
		    

			  System.out.println("update-Labeled size "+ LiSaved[1].size());
			  System.out.println("update-% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
			  System.out.println("update-% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
		  
	          
	          */
	          
	          
	          
	          
	          
	          
	          
	          
	          
	          
	        }
	      }// end  for
	      
	   
	    } //end of repeat

	  
	   
		  
	    // testing phase.
	    
	    /*
		  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
		  
		  int traPrediction[] = new int[tranductive.size()];
		  int tstPrediction[] = new int[test.size()];
		  int aciertoTrs = 0;
		  int aciertoTst = 0;
		  */
	    
		  tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  test = new PrototypeSet(this.testDataSet.clone());
		  
		  traPrediction = new int[tranductive.size()];
		  tstPrediction= new int[test.size()];
		  aciertoTrs = 0;
		  aciertoTst = 0;
		  

	  //transductive phase
		  
	      for(int i = 0; i < 3; i++)
	      {
	    	   if(LiSaved[i] == null){ // It can ocurrs if equation 9 is not satisfied
	    		   LiSaved[i] = new PrototypeSet(labeled.clone());
	    	   }

	    	   this.classify(i, LiSaved[i], tranductive, true);//h_i <-- Learn(L \cup L_i)
	      }

	
		  for(int i=0; i<tranductive.size(); i++){
			  
			  // Voting RULE
			    traPrediction[i]=this.votingRule(i);
		    
			    // maxIndex is the class label.		  
			    if(tranductive.get(i).getOutput(0) == traPrediction[i]){
					  aciertoTrs++;
			    }
				  
			    tranductive.get(i).setFirstOutput(traPrediction[i]);
		   }
			  
			  
		  // test phase
	      for(int i = 0; i < 3; i++)
	      {
	    	   if(LiSaved[i] == null){ // It can ocurrs if equation 9 is not satisfied
	    		   LiSaved[i] = new PrototypeSet(labeled.clone());
	    	   }
	    	  this.classify(i, LiSaved[i], test, true);//h_i <-- Learn(L \cup L_i)
	      }
	      
		  for(int i=0; i<test.size(); i++){
			  
			  // Voting RULE
			    tstPrediction[i]=this.votingRule(i);
		    
			    // maxIndex is the class label.		  
			    if(test.get(i).getOutput(0) == tstPrediction[i]){
					  aciertoTst++;
			    }
				  
			    test.get(i).setFirstOutput(tstPrediction[i]);
		   }
		  
	    

		  System.out.println("Labeled size "+ LiSaved[1].size());
		  System.out.println("% de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  System.out.println("% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
	

     //tranductive.save("outputTri.dat");
	
	  
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
