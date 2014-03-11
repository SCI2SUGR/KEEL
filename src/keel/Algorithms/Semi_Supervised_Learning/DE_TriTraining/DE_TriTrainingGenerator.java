
/*
	DE_TriTraining.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  4/3/2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.DE_TriTraining;

import keel.Algorithms.Instance_Generation.Depur.Depur;
import keel.Algorithms.Semi_Supervised_Learning.utilities.KNN.KNN;
import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerNB;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerRipper;
import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerSMO;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Semi_Supervised_Learning.*;
import java.util.*;

import keel.Algorithms.Semi_Supervised_Learning.utilities.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Tri-training. You can use: Knn, C4.5, SMO and NB as classifiers.
 * @author triguero
 *
 */

public class DE_TriTrainingGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String [] classifier;
 private int [][] predictions;
 private double [][][] probabilities;
// private String final_classifier; 

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  // We need the variable K to use with k-NN rule
  private int k1;
  // In addition, we use a second variable k' to establish the numbers of neighbours
  // that must have the same class.
  private int k2;
  
  
  private C45 c45;
  private HandlerSMO smo;
  private HandlerNB nb;
  
  /**
   * Build a new DE_TriTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public DE_TriTrainingGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="DE_TriTraining";
      
  }
  


  /**
   * Build a new DE_TriTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public DE_TriTrainingGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="DE_TriTraining";

      this.classifier = new String[3];
      this.predictions = new int[3][];
      
     
      this.classifier[0] = parameters.getNextAsString();
      this.classifier[1] = parameters.getNextAsString();
      this.classifier[2] = parameters.getNextAsString();
      //this.final_classifier = parameters.getNextAsString();
      
      this.k1 = parameters.getNextAsInt();
      this.k2 = parameters.getNextAsInt();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      this.probabilities = new double[3][][];
                                      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  
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
   * @param algorithm
   * @param train
   * @param test
   * @param save. It indicates if it will save the results in the variable PREDICTIONS!
   * @return
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
   * @param train data set.
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
   * 
   * Depuration algorithm
   * @return
   */
  protected PrototypeSet Depur (PrototypeSet T, PrototypeSet labeled)
  {
	//T.print();
	 PrototypeSet Sew = new PrototypeSet (T);
	
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
		  }else if(counter[maxClass] >= this.k2){ // si está entre k1 y k2, re-etiquetar.
			  if(maxClass != class_p){
				  p.setFirstOutput(maxClass);
			  	//  System.out.println("RELABEL");
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
   * Apply the DE_TriTrainingGenerator method.
   * @return 
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
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor válido.
	  }
	  


		
		 
	  
	  for(int i = 0; i < 3; i++)
	  {
		  labeledBoostrapped[i] = new PrototypeSet(labeled.resample());     //L_i <-- Bootstrap(L)
	      this.classify(i,labeledBoostrapped[i], labeled, true);//h_i <-- Learn(L_i)  Classify LABELED DATA!
	 
	      err_prime[i] = 0.5;                                                   //e'_i <-- .5
	      s_prime[i] = 0;                                                       //l'_i <-- 0
	  }
	    

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
	           //  System.out.println("añado instancia");
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
	          
	          
	         // System.out.println(Li[i].size());
		      // Adding depuration step to the unlabeled data.
	          Li[i] =  new PrototypeSet(Depur(Li[i], labeled));
	          
	          //System.out.println(Li[i].size());
	          
	          // save Li
	          LiSaved[i]  = new PrototypeSet(Li[i].clone());
	          labeledBoostrapped[i] = new PrototypeSet(Li[i].clone());
	          
	
	          
	          this.classify(i, Li[i], labeled, true);//h_i <-- Learn(L \cup L_i)
	          

	          
	          //m_classifiers[i].buildClassifier(combine(labeled, Li[i]));        
	          err_prime[i] = err[i];                                            //e'_i <-- e_i
	          s_prime[i] = size;                                                //l'_i <-- |L_i|
	        }
	      }// end  for
	      
	   
	    } //end of repeat

	  
	   
		  
	    // testing phase.
	    
	    
		  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
		  
		  int traPrediction[] = new int[tranductive.size()];
		  int tstPrediction[] = new int[test.size()];
		  int aciertoTrs = 0;
		  int aciertoTst = 0;
		  

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
