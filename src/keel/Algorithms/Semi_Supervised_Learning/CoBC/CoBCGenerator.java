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
	CoBC.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.CoBC;

import keel.Algorithms.Semi_Supervised_Learning.Basic.C45.*;
//import keel.Algorithms.Semi_Supervised_Learning.Basic.HandlerAdaBoost;
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
 * This class implements the Co-traning wrapper. You can use: Knn, C4.5, SMO as classifiers.
 * @author triguero
 *
 */

public class CoBCGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
  protected int numberOfClassifiers;
  private int MaxIter;
  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  int pre[][];
  double [][][] probabilities;
  
  
  String EnsembleLearn = "Bagging";
  String BaseLearn = "NN";
  int poolU = 100;
  

  
  /**
   * Build a new CoBCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public CoBCGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="CoBC";
      
  }
  


  /**
   * Build a new CoBCGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public CoBCGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="CoBC";
   
      this.MaxIter =  parameters.getNextAsInt();
      this.numberOfClassifiers = parameters.getNextAsInt();
      this.EnsembleLearn = parameters.getNextAsString();
      this.BaseLearn = parameters.getNextAsString();
      this.poolU = parameters.getNextAsInt();
      
      
      
      pre = new int[this.numberOfClassifiers][];
      probabilities = new double[this.numberOfClassifiers][][];
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " +  this.numberOfClass +"\n");

  }
  
  
  /**
   * This methods implement the voting rule in order to classify unlabeled data with the prediction pre[][]
   * @param unlabeled
   * @param pre
   * @return
   */
  
  double [] votingRule(PrototypeSet unlabeled, int pre[][]){
	  double predicho[] = new double[unlabeled.size()];
	  
	  for(int i=0; i< unlabeled.size(); i++){ // voting rule
		  
		  
		  
		  double perClass[] =  new double [this.numberOfClass];
		  Arrays.fill(perClass, 0);
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			  if(pre[j][i]!=-1)
			  perClass[(int) pre[j][i]]++;
		  }
		  
		  int Maximo = Integer.MIN_VALUE;
		  
		  for (int j=0 ; j< this.numberOfClass; j++){
			  if(perClass[j]>Maximo){
				  Maximo =(int) perClass[j];
				  predicho[i] = j;
			  }
		  }
	  } // End voting Rule
	  
	  
	  return predicho;
  }

  /**
   * Classify 
   * @param train
   * @param test
   * @throws Exception
   */
  
  public double[] clasificar(PrototypeSet train[], PrototypeSet test) throws Exception{
	  
	  double predicho[] = new double[test.size()];
	  
	  for (int i=0; i<this.numberOfClassifiers; i++){
		  
		  getSolicitaGarbageColector();
		  
		  if(this.BaseLearn.equalsIgnoreCase("NN")){ // 3NN
			//  System.out.println("Executing KNN");
			  pre[i] = KNN.classify(train[i], test, 3, probabilities[i]);

		  }else if(this.BaseLearn.equalsIgnoreCase("NB")){ // NB

			  //System.out.println("Executing NB");
			  HandlerNB nb = new HandlerNB(train[i].prototypeSetTodouble(), train[i].prototypeSetClasses(), test.prototypeSetTodouble(), test.prototypeSetClasses(),this.numberOfClass);
			  
		      pre[i] = nb.getPredictions();    
	  
		      probabilities[i] = nb.getProbabilities();
		      
		      nb = null;
		      
		  }else if(this.BaseLearn.equalsIgnoreCase("C45")){ //C45
			  //System.out.println("Executing C45");
			  
			  InstanceSet uno = train[i].toInstanceSet();
			  InstanceSet dos =  test.toInstanceSet();
			  
			  C45 c45 = new C45(train[i].toInstanceSet(), test.toInstanceSet());      // C4.5 called
			  
		      pre[i] = c45.getPredictions();    
			  
		      probabilities[i] = c45.getProbabilities();
		      
		      uno = null;
		      dos = null;
		      c45  = null;
		      

		  }else if(this.BaseLearn.equalsIgnoreCase("SMO")){ //SMO
			  
			  InstanceSet uno = train[i].toInstanceSet();
			  InstanceSet dos =  test.toInstanceSet();
			  
		      HandlerSMO SMO = new HandlerSMO(uno,dos, this.numberOfClass,String.valueOf(this.SEED));      // SMO
		      
		      pre[i] = SMO.getPredictions(0);    
		      
		      probabilities[i] = SMO.getProbabilities();
		  //    probabilities = SMO.getProbabilities();
		      uno = null;
		      dos = null;
		      SMO  = null;
		  }
		  
		  
		  getSolicitaGarbageColector();
		  
	  }
	  
  
	  predicho = votingRule(test, pre); // in predicho we have the possible label, but we have to contrast this information with the confidence level.
		
	  
	  return predicho;
	  
  }
  
  
  
  
  
  
  
  /**
   * 
   * @param Labeled
 * @throws Exception 
   */
  public double [] EnsembleLearn(PrototypeSet Labeled[], PrototypeSet test) throws Exception{
	  double predicho[] = new double[test.size()];
	  
	  
	  if(this.EnsembleLearn.equalsIgnoreCase("Bagging")){
		  
		 for(int i=0; i< this.numberOfClassifiers; i++){
			 
			 
			  getSolicitaGarbageColector();
			  
			  if(this.BaseLearn.equalsIgnoreCase("NN")){ // 3NN
				//  System.out.println("Executing KNN");
				  pre[i] = KNN.classify(Labeled[i], test, 3, probabilities[i]);

			  }else if(this.BaseLearn.equalsIgnoreCase("NB")){ // NB

				  //System.out.println("Executing NB");
				  HandlerNB nb = new HandlerNB(Labeled[i].prototypeSetTodouble(), Labeled[i].prototypeSetClasses(), test.prototypeSetTodouble(), test.prototypeSetClasses(),this.numberOfClass);
				  
			      pre[i] = nb.getPredictions();    
		  
			      probabilities[i] = nb.getProbabilities();
			      
			      nb = null;
			      
			  }else if(this.BaseLearn.equalsIgnoreCase("C45")){ //C45
				  //System.out.println("Executing C45");
				  
				  InstanceSet uno = Labeled[i].toInstanceSet();
				  InstanceSet dos =  test.toInstanceSet();
				  
				  C45 c45 = new C45(uno, dos);      // C4.5 called
				  
			      pre[i] = c45.getPredictions();    
				  
			      probabilities[i] = c45.getProbabilities();
			      
			      uno = null;
			      dos = null;
			      c45  = null;
			      

			  }else if(this.BaseLearn.equalsIgnoreCase("SMO")){ //SMO
				  
				  InstanceSet uno = Labeled[i].toInstanceSet();
				  InstanceSet dos =  test.toInstanceSet();
				  
			      HandlerSMO SMO = new HandlerSMO(uno,dos, this.numberOfClass,String.valueOf(this.SEED));      // SMO
			      
			      pre[i] = SMO.getPredictions(0);    
			      
			      probabilities[i] = SMO.getProbabilities();
			  //    probabilities = SMO.getProbabilities();
			      uno = null;
			      dos = null;
			      SMO  = null;
			  }
			  
			  
			  getSolicitaGarbageColector();
			
		 } // End training.
		 
		  predicho = votingRule(test, pre); // in predicho we have the possible label, but we have to contrast this information with the confidence level.

		 
		 
	  }else if(this.EnsembleLearn.equalsIgnoreCase("AdaBoost")){
		
  /*
			 for(int i=0; i< this.numberOfClassifiers; i++){
				 
				  getSolicitaGarbageColector();
				  
				  HandlerAdaBoost adaboost = new HandlerAdaBoost(Labeled[i].toInstanceSet(), test.toInstanceSet(), this.numberOfClassifiers, this.numberOfClass);
				  
			      predicho = adaboost.getPredictions();    


//			      probabilities[i] = adaboost.getProbabilities();
			      
			      adaboost = null;
			      
			      getSolicitaGarbageColector();
				 
				 
			 }
			 */

	  }
	  
	  
	  return predicho;
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
   * Apply the CoBCGenerator method with 3 classifiers:  C45, NB, and 3NN
   * 
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm CoBC is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  // establishing the indexes

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
	  
      
	  // 1), 2) Get the class priori probabilities, and the class growth rate.
	  
	  double kj[] = new double[this.numberOfClass];
	  double minimo = Double.MAX_VALUE;
	  
	  for(int i=0; i<this.numberOfClass; i++){
		  
		  if(labeled.getFromClass(i).size() == 0){
			  kj[i] = 0;
		  }else{
			  kj[i] = (labeled.getFromClass(i).size()*1./labeled.size());
		  }
		  
		  if(kj[i]<minimo && kj[i]!=0){
			  minimo = kj[i];
		  }
		  //System.out.println(kj[i]);
	  }
	
	  // The minimum ratio is establish to this.numberOfselectedExamples
	  // We have to determine the maximu kj[i]
	  double maximoKj = 0;
	  
	  for(int i=0; i<this.numberOfClass; i++){
		  kj[i] = Math.round(kj[i]/minimo);
		  
		  maximoKj+=kj[i];
		  //System.out.println((int)kj[i]);
	  }
	  
	  
	  // 3) construct an initial committee of N classifiers, Ensemble Learn (L,BaseLearner, N),
	  
	  PrototypeSet labeledBoostrapped[] = new PrototypeSet[this.numberOfClassifiers]; // for each classiffier.
	  for(int i=0; i< this.numberOfClassifiers; i++){
		  labeledBoostrapped[i] = new PrototypeSet(labeled.resample());     //L_i <-- Bootstrap(L)
	  }
	  
	  

	  
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  double traPrediction[] = null;
	  double tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
	  
	   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
	   
	  traPrediction = clasificar(labeledBoostrapped, tranductive);

	  
	  for(int i=0; i<tranductive.size(); i++){
		
		  if(tranductive.get(i).getOutput(0) == traPrediction[i]){
			  aciertoTrs++;
	       }
		  
		  tranductive.get(i).setFirstOutput(traPrediction[i]);
		  
	  }
	  
	  
	  // Test phase
	  
	   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
	  tstPrediction = clasificar(labeledBoostrapped, test);

	  
	  for(int i=0; i<test.size(); i++){
		
		 	  
		  if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
		  }
		  
		  
		  test.get(i).setFirstOutput(tstPrediction[i]);
		  
	  }
	  
	  

	  System.out.println("Initial Labeled size "+ labeledBoostrapped[1].size());
	  System.out.println("Initial % de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
	  System.out.println("Initial% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
	  // 6) Starting the learning process.
	  
	  for (int i=0; i<this.MaxIter && unlabeled.size()>maximoKj; i++){
		  
		  
		  PrototypeSet labeledPrima[] = new PrototypeSet[this.numberOfClassifiers];  // 7) L't <- 0
		  double confidence[][] = new double[unlabeled.size()][this.numberOfClass];
		  PrototypeSet UnlabeledPrima[] = new PrototypeSet[this.numberOfClassifiers];
		  
		  
		  for(int j=0; j< this.numberOfClassifiers && unlabeled.size()>maximoKj; j++){ // for each committee.
			  
			  labeledPrima[j] = new PrototypeSet();
			  UnlabeledPrima[j] = new PrototypeSet(); // 10) RandomSubsample (U, poolU).
			  
			  unlabeled.randomize(); //shufflte the unlabeled set.
			  
			  //System.out.println("UNLabeled size BEFORE = "+unlabeled.size());
			  
			  for(int z=0; z< this.poolU && z< unlabeled.size(); z++){
				  Prototype remove = unlabeled.remove(z);
				  UnlabeledPrima[j].add(remove);
			  }
			  
			  
		      for (int z=0; z< UnlabeledPrima[j].size();z++){
		    	  UnlabeledPrima[j].get(z).setIndex(z); 
		      }
		      
			 // System.out.println("UNLabeled size AFTER = "+unlabeled.size());
			  
			  // 11) Classify UnlabeledPrima with the ensembleLearn...
		      probabilities = new double[this.numberOfClassifiers][UnlabeledPrima[j].size()][this.numberOfClass];

			  double prediction[] = EnsembleLearn(labeledBoostrapped,UnlabeledPrima[j]);
		
			  PrototypeSet S = new PrototypeSet(); // most compent unlabeledprima data.
			  
			  // 12)  Select most confident examples
			
			  double maximoClase[][] = new double[this.numberOfClass][];
			  int indexClase[][] = new int[this.numberOfClass][];
			  
              indexClase = new int[this.numberOfClass][];
				
              
			  for (int z=0 ; z< this.numberOfClass; z++){
				 // maximoClase[j] = new double[(int) kj[j]];
				  indexClase[z] = new int[(int) kj[z]];
				  
				 //Arrays.fill(maximoClase[j], Double.MIN_VALUE);
				 Arrays.fill(indexClase[z], -1);
			  }
	
		
			  for (int z=0; z< this.numberOfClass; z++){
				  // for each class, ordenar vector de prob.
				  double [] aOrdenar = new double[UnlabeledPrima[j].size()];
				  int [] position = new int [UnlabeledPrima[j].size()];
				  
				  for(int q=0;q<UnlabeledPrima[j].size(); q++){  
					  aOrdenar[q] =  probabilities[j][q][z];
					  position[q] = q;
				  }
				  
				  Utilidades.quicksort(aOrdenar, position); // orden ascendente!
				  
				  /*
				  for(int q=0; q<unlabeled.size(); q++){
					 System.out.print(position[q]+", ");
				  }
				  */
				  //System.out.println(" ");
				  
				  
				  for(int w=UnlabeledPrima[j].size()-1; w>=UnlabeledPrima[j].size()-kj[z] && w>=0;w--){
					  indexClase[z][(UnlabeledPrima[j].size()-1)-w] = position[w];
				  }
			  }
					  
			  // IndexClase point out to the best prototypes
			  
			  
			  // Set U' = U'-S, and L= L U S.
			  
			  PrototypeSet toClean = new PrototypeSet();
			  
			  for (int z=0 ; z< this.numberOfClass; z++){
				
				  //if(contadorClase[j]< kj[j]){
				
				  for(int w=0; w<kj[z];w++){
					  
					  //From classifier 1.
					  if(indexClase[z][w]!=-1){
				  
						  Prototype nearUnlabeled = new Prototype(UnlabeledPrima[j].get(indexClase[z][w]));
						  
						  
						  if(this.BaseLearn.equalsIgnoreCase("NN")){ 
							   
							  
							  	Prototype clase = labeled.nearestTo(nearUnlabeled);
								  
							  	nearUnlabeled.setFirstOutput(clase.getOutput(0));
							  	
								  if(clase.getOutput(0)==j){
									  labeledPrima[j].add(new Prototype(nearUnlabeled));
								  }else{
									  toClean.add(UnlabeledPrima[j].get(indexClase[z][w]));
								  }
								
							//  	contadorClase[(int)clase.getOutput(0)]++;
							  	
	
								  
	
						  }else if(this.BaseLearn.equalsIgnoreCase("C45") || this.BaseLearn.equalsIgnoreCase("NB") || this.BaseLearn.equalsIgnoreCase("SMO")){
							  
							  nearUnlabeled.setFirstOutput(pre[j][indexClase[z][w]]);
							
							  if(pre[j][indexClase[z][w]]==j){
								  labeledPrima[j].add(new Prototype(nearUnlabeled));
							  }else{
								  toClean.add(UnlabeledPrima[j].get(indexClase[z][w]));
							  }
							  
							  
							 // contadorClase[pre[indexClase[j]]]++;
							  
	
						  }
					  
				  
					  
					  	
					  }
				  
				  }
			  }
			  
			  
				//Then we have to clean the unlabeled have to clean.
				for (int z=0 ; z< labeledPrima[j].size(); z++){
					//unlabeled.removeWithoutClass(labeledPrima.get(j)); 
					UnlabeledPrima[j].borrar(labeledPrima[j].get(z).getIndex()); 
				}
			  
			 for (int z=0 ; z<toClean.size(); z++){
				 // unlabeled.remove(toClean.get(j));
				  UnlabeledPrima[j].borrar(toClean.get(z).getIndex());
			  }
			  
			  // Replenish U with the rest of prototypes of U'.
			  
			  for(int z=0; z< UnlabeledPrima[j].size();z++){
				  unlabeled.add(UnlabeledPrima[j].get(z));
			  }
			  

			  
			  
		  } // End For (for each committee).
		  
		  
		  
		  for(int j=0; j< this.numberOfClassifiers; j++){
			//  System.out.println("Added Li prototypes: "+ labeledPrima[j].size());
			  if(labeledPrima[j]!=null) labeledBoostrapped[j].add(labeledPrima[j]); // It is possible that there are not prototypes in U, so labeledPrima is null
		  }
		  	
		  
		  // update fitness:
		  
		  tranductive = new PrototypeSet(this.transductiveDataSet.clone());
		  test = new PrototypeSet(this.testDataSet.clone());
		 
		 
		  traPrediction = null;
		  tstPrediction = null;
		   aciertoTrs = 0;
		   aciertoTst = 0;
		 /* 
		   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
		   
		  traPrediction = clasificar(labeledBoostrapped, tranductive);

		  
		  for(int m=0; m<tranductive.size(); m++){
			
			  if(tranductive.get(m).getOutput(0) == traPrediction[m]){
				  aciertoTrs++;
		       }
			  
			  tranductive.get(m).setFirstOutput(traPrediction[m]);
			  
		  }
		  
		  
		  // Test phase
		  
		   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
		  tstPrediction = clasificar(labeledBoostrapped, test);

		  
		  for(int m=0; m<test.size(); m++){
			
			 	  
			  if(test.get(m).getOutput(0) == tstPrediction[m]){
					  aciertoTst++;
			  }
			  
			  
			  test.get(m).setFirstOutput(tstPrediction[m]);
			  
		  }
		  
		  

		  System.out.println("update Labeled size "+ labeledBoostrapped[1].size());
	  
		  System.out.println("update - % de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
		  System.out.println("update -% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
		  */
		  
	  } // end FOR iterations.
	    
	  
	  

	  
	 // Combining stage.

	  /*
	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  double traPrediction[] = null;
	  double tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
	  */
	  
	  tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  traPrediction = null;
	  tstPrediction = null;
	   aciertoTrs = 0;
	   aciertoTst = 0;
	  
	   probabilities = new double[this.numberOfClassifiers][tranductive.size()][this.numberOfClass];
	   
	  traPrediction = clasificar(labeledBoostrapped, tranductive);

	  
	  for(int i=0; i<tranductive.size(); i++){
		
		  if(tranductive.get(i).getOutput(0) == traPrediction[i]){
			  aciertoTrs++;
	       }
		  
		  tranductive.get(i).setFirstOutput(traPrediction[i]);
		  
	  }
	  
	  
	  // Test phase
	  
	   probabilities = new double[this.numberOfClassifiers][test.size()][this.numberOfClass];
	  tstPrediction = clasificar(labeledBoostrapped, test);

	  
	  for(int i=0; i<test.size(); i++){
		
		 	  
		  if(test.get(i).getOutput(0) == tstPrediction[i]){
				  aciertoTst++;
		  }
		  
		  
		  test.get(i).setFirstOutput(tstPrediction[i]);
		  
	  }
	  
	  

	  System.out.println("Labeled size "+ labeledBoostrapped[1].size());
  
	  System.out.println("Final - % de acierto TRS = "+ (aciertoTrs*100.)/transductiveDataSet.size());
	  System.out.println("Final -% de acierto TST = "+ (aciertoTst*100.)/testDataSet.size());
	  
		


	  
	  
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
