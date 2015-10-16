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
	CoTraining.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.CoTraining;

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
 * This class implements the Co-traning wrapper. You can use: Knn, C4.5, SMO   as classifiers.
 * @author triguero
 *
 */

public class CoTrainingGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String classifier1; 
 private String classifier2; 
 private String final_classifier; 
 private int InitialPOOL;
  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new CoTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public CoTrainingGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="CoTraining";
      
  }
  


  /**
   * Build a new CoTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public CoTrainingGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="CoTraining";
   
    
      this.numberOfselectedExamples =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.InitialPOOL = parameters.getNextAsInt();
      this.classifier1 = parameters.getNextAsString();
      this.classifier2 = parameters.getNextAsString();
      this.final_classifier = parameters.getNextAsString();
      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
     // System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  
  
  public void cambiarContextoAttributes()throws Exception{
	  // Return to the same Attributes problem.
	  Attributes.clearAll();
	  InstanceSet mojon2 = new InstanceSet();
	  mojon2.readSet("antiguo.dat", true);
      mojon2.setAttributesAsNonStatic();
      InstanceAttributes att = mojon2.getAttributeDefinitions();
      Prototype.setAttributesTypes(att);  
      PrototypeSet intercambio = new PrototypeSet(mojon2);
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
   * Apply the CoTrainingGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm Co-TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled, labeled_sub1, labeled_sub2;
	  PrototypeSet unlabeled, unlabeled_sub1, unlabeled_sub2;
	  ArrayList<Integer> T1, T2;
	  
	  
	  //The Original attribute sets are randomly partitioned into two subsets with similar sizes:
	  
	  Pair<PrototypeSet,PrototypeSet> training = trainingDataSet.divideFeaturesRandomly();
	  
	  T1 = new ArrayList<Integer> (training.first().getFeatures1());
	  T2 = new ArrayList<Integer> (training.first().getFeatures2());
	  
	  labeled_sub1 = new PrototypeSet(training.first().getAllDifferentFromClass(this.numberOfClass));
	  labeled_sub2 = new PrototypeSet(training.second().getAllDifferentFromClass(this.numberOfClass));
	  unlabeled_sub1 = new PrototypeSet(training.first().getFromClass(this.numberOfClass));
	  unlabeled_sub2 = new PrototypeSet(training.second().getFromClass(this.numberOfClass));


	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  


      for (int j=0; j< labeled.size();j++){
          labeled.get(j).setIndex(j); 
    	  labeled_sub1.get(j).setIndex(j); 
     	  labeled_sub2.get(j).setIndex(j); 
      }
      
      for (int j=0; j< unlabeled.size();j++){
    	  unlabeled_sub2.get(j).setIndex(j); 
    	  unlabeled_sub1.get(j).setIndex(j); 
    	  unlabeled.get(j).setIndex(j); 
      }
      

	 
	  // Accuracy with initial labeled data.

/*
	  if(this.final_classifier.equalsIgnoreCase("NN")){
	  
		  System.out.println("AccTrs with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
		  System.out.println("AccTst with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
		  
		  
		  System.out.println("Labeled size = " +labeled.size());
		  System.out.println("Unlabeled size = " + unlabeled.size());
	  
	  }
	  
*/	  
	  //labeled.print();
	  //unlabeled.print();
	  
	  // kj is the number of prototypes added from class j, that it must be propornotional to its ratio.
	  
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
		//  System.out.println((int)kj[i]);
	  }
	  


	  // In order to avoid problems with C45 and NB.
	  for(int p=0; p<unlabeled.size(); p++){
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor vÃ¡lido.
		  unlabeled_sub1.get(p).setFirstOutput(0);
		  unlabeled_sub2.get(p).setFirstOutput(0);
		 // unlabeled.get(p).setIndex(p); // established the index value
	  }

	
	  // Create a pool of examples by choosing u examples at random from Unlabeled. 
	  
	  PrototypeSet pool1 = new PrototypeSet();
	  PrototypeSet pool2 = new PrototypeSet();	  
	  PrototypeSet POOL = new PrototypeSet();
	  
      ArrayList<Integer> indexes =  RandomGenerator.generateDifferentRandomIntegers(0, unlabeled.size()-1);
      
      if(unlabeled.size()<this.InitialPOOL){
    	this.InitialPOOL = unlabeled.size(); 
      } 
      
      for (int i=0; i< this.InitialPOOL;i++){
    	  POOL.add(new Prototype(unlabeled.get(indexes.get(i))));
          pool1.add(new Prototype(unlabeled_sub1.get(indexes.get(i))));
          pool2.add(new Prototype(unlabeled_sub2.get(indexes.get(i))));
          //System.out.println("i =" + indexes.get(i));
      }
	  
	  

	  for (int i=0; i< this.InitialPOOL;i++){
		  unlabeled.borrar(POOL.get(i).getIndex()); // Lo saco del conjunto de no etiquetados. 
		  unlabeled_sub1.borrar(pool1.get(i).getIndex());
		  unlabeled_sub2.borrar(pool2.get(i).getIndex());
	  }
	 
      
	  //established the indexes:
	 
      for (int j=0; j< unlabeled.size();j++){
    	  unlabeled_sub2.get(j).setIndex(j); 
    	  unlabeled_sub1.get(j).setIndex(j); 
    	  unlabeled.get(j).setIndex(j); 
      }
      
      for (int j=0; j< POOL.size();j++){
          POOL.get(j).setIndex(j); 
          pool1.get(j).setIndex(j); 
          pool2.get(j).setIndex(j); 
      }
      

     // POOL.get(0).print();
     // pool1.get(0).print();
     // pool2.get(0).print();
      
      /*
    
	  System.out.println("Labeled size = "+labeled.size());
	  System.out.println("UNLabeled size = "+unlabeled.size());
	  System.out.println("UNLabeledsub1 size = "+unlabeled_sub1.size());
	  System.out.println("UNLabeledsub2 size = "+unlabeled_sub2.size());
	  
	  System.out.println("POOL size = "+POOL.size());
	  System.out.println("pool1 size = "+pool1.size());
	  System.out.println("pool2 size = "+pool2.size());
	  */
	  
      /********************************************/
      //Saving the Attributes state in a file.
	  PrototypeSet noInstancias = new PrototypeSet();
	  noInstancias.add(labeled.get(0));
	  noInstancias.save("antiguo.dat");
	  /**********************************************/
	  

	  
	  for(int j=T2.size()-1; j>=0;j--){ // quitar del otro conjunto.
		  if(!Attributes.removeAttribute(true,T2.get(j))){
			  System.err.println("ERROR TO CLEAN");
		  }
		 // System.out.print("Elimino : "+T2.get(j)+ ", ");
	  }

	  Prototype.setAttributesTypes();
	  
     
	  PrototypeSet sinInstancias = new PrototypeSet();
	  sinInstancias.save("l1.dat");

      cambiarContextoAttributes(); // Change context
     
	  for(int j=T1.size()-1; j>=0;j--){ // quitar del otro conjunto.
		  if(!Attributes.removeAttribute(true,T1.get(j))){
			  System.err.println("ERROR TO CLEAN");
		  }
	  }
	  
	  // Re-establecer tipos de los Prototype para poder guardar!!
	  Prototype.setAttributesTypes();
	  
	  sinInstancias = new PrototypeSet();
	  sinInstancias.save("l2.dat");
	  
	  cambiarContextoAttributes(); // Change context
	  
	  
	  for (int i=0; i<this.MaxIter && POOL.size()>maximoKj ; i++){ //
		  
		  PrototypeSet labeledPrima = new PrototypeSet();
		  PrototypeSet labeled_sub1Prima = new PrototypeSet();
		  PrototypeSet labeled_sub2Prima = new PrototypeSet();
		  
		  double maximoClase[][] = new double[this.numberOfClass][];
		  int indexClase[][] = new int[this.numberOfClass][];
		  
		  double maximoClase2[][] = new double[this.numberOfClass][];
		  int indexClase2[][] = new int[this.numberOfClass][];

		  
		      int[] pre = new int[pool1.size()];    
		      double [][] probabilities = new double[pool1.size()][this.numberOfClass];
		      int[] pre2 = new int[pool2.size()];    
		      double [][] probabilities2 = new double[pool2.size()][this.numberOfClass];	      

		      //**********************************************
		      //Train a view-1 classifier from labeled_sub1:
		      //**********************************************
		      
			  //Reading Header, and fill InstanceSET.
			  Attributes.clearAll();
			  InstanceSet label = new InstanceSet();
			  label.readSet("l1.dat", true);
	          label.setAttributesAsNonStatic();
	          InstanceAttributes att = label.getAttributeDefinitions();
	          Prototype.setAttributesTypes(att);  
			  PrototypeSet intercambio = new PrototypeSet(label);
		      
			  if(this.classifier1.equalsIgnoreCase("NN")){ 
				  
				  for (int q=0; q<pool1.size(); q++){  // for each unlabeled.
					  
					  Prototype NearClass[] = new Prototype[this.numberOfClass];
	        		  double sumatoria = 0;
					  for (int j=0 ; j< this.numberOfClass; j++){
						 // unlabeled.get(q).print();
						 // System.out.println("Labeled size = "+labeled.getFromClass(j).size());
						  if(labeled_sub1.getFromClass(j).size() >0){
					
							  NearClass[j] = new Prototype (labeled_sub1.getFromClass(j).nearestTo(pool1.get(q)));		
							  probabilities[q][j] = Math.exp(-1*(Distance.absoluteDistance(NearClass[j], pool1.get(q))));
							  sumatoria+= probabilities[q][j];
						  }else{
							  probabilities[q][j] = 0;
						  }
					  }
					  
					  for (int j=0 ; j< this.numberOfClass; j++){
						  probabilities[q][j]/=sumatoria;
					  }
				  
				  }
			  
			  }else if(this.classifier1.equalsIgnoreCase("C45")){

				  getSolicitaGarbageColector();
				  
				  
				  C45 c45 = new C45(labeled_sub1.toInstanceSet(), pool1.toInstanceSet());
					 
				  pre = c45.getPredictions();    
					  
				  probabilities = c45.getProbabilities();
				  c45 = null;
				  getSolicitaGarbageColector();
				  				  
			  }else if(this.classifier1.equalsIgnoreCase("NB")){
				  
				  getSolicitaGarbageColector();
				  
				  HandlerNB nb = new HandlerNB(labeled_sub1.prototypeSetTodouble(), labeled_sub1.prototypeSetClasses(), pool1.prototypeSetTodouble(), pool1.prototypeSetClasses(),this.numberOfClass);
				  
			      pre = nb.getPredictions();    
		  
			      probabilities = nb.getProbabilities();
			      nb= null;
			      getSolicitaGarbageColector();
				  
			  }else if(this.classifier1.equalsIgnoreCase("SMO")){
				  getSolicitaGarbageColector();
				 // System.out.println("SVM Executing...");
					 
			      HandlerSMO SMO = new HandlerSMO(labeled_sub1.toInstanceSet(), pool1.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
			      
			      pre = SMO.getPredictions(0);    
			      
			      probabilities = SMO.getProbabilities();
  
			      SMO  = null;
			      getSolicitaGarbageColector();
			    //  System.out.println("SVM Finishes...");
		  
			  }
			  
			  this.cambiarContextoAttributes(); // Change Context
  
			  // determine who are the best prototypes
			  
			  indexClase = new int[this.numberOfClass][];
				
              
			  for (int j=0 ; j< this.numberOfClass; j++){
				 // maximoClase[j] = new double[(int) kj[j]];
				  indexClase[j] = new int[(int) kj[j]];
				  
				 //Arrays.fill(maximoClase[j], Double.MIN_VALUE);
				 Arrays.fill(indexClase[j], -1);
			  }
	
		
			  for (int j=0; j< this.numberOfClass; j++){
				  // for each class, ordenar vector de prob.
				  double [] aOrdenar = new double[pool1.size()];
				  int [] position = new int [pool1.size()];
				  
				  for(int q=0;q<pool1.size(); q++){  
					  aOrdenar[q] =  probabilities[q][j];
					  position[q] = q;
				  }
				  
				  Utilidades.quicksort(aOrdenar, position); // orden ascendente!
 
				  
				  for(int z=pool1.size()-1; z>=pool1.size()-kj[j];z--){
					  indexClase[j][(pool1.size()-1)-z] = position[z];
				  }
			  }
			  /*
			  maximoClase = new double[this.numberOfClass][];
              indexClase = new int[this.numberOfClass][];
				 
			  for (int j=0 ; j< this.numberOfClass; j++){
				  maximoClase[j] = new double[(int) kj[j]];
				  indexClase[j] = new int[(int) kj[j]];
				  
				 Arrays.fill(maximoClase[j], Double.MIN_VALUE);
				 Arrays.fill(indexClase[j], -1);
			  }
	
			  
			
			  for (int q=0; q<pool1.size(); q++){  // for each unlabeled.
	
				  for (int j=0; j< this.numberOfClass; j++){
				  
					  boolean fin = false;
					  for(int z=(int)kj[j]-1; z>=0 && !fin; z--){
						  if(probabilities[q][j]> maximoClase[j][z]){
								fin = true;
							  maximoClase[j][z] = probabilities[q][j];
							  indexClase[j][z] = q;
						  }
					  }
						 

				  }
			  
			  }
			  */
			  
			  //**********************************************
			  //Train a view-1 classifier from labeled_sub2:
			  //**********************************************
			  
			  //Reading Header, and fill InstanceSET.
			  Attributes.clearAll();
			  label = new InstanceSet();
			  label.readSet("l2.dat", true);
	          label.setAttributesAsNonStatic();
	          att = label.getAttributeDefinitions();
	          Prototype.setAttributesTypes(att);  
			  intercambio = new PrototypeSet(label);
			  
			  if(this.classifier2.equalsIgnoreCase("NN")){ 
				  
				  for (int q=0; q<pool2.size(); q++){  // for each unlabeled.
					  
					  Prototype NearClass[] = new Prototype[this.numberOfClass];
	        		  double sumatoria = 0;
					  for (int j=0 ; j< this.numberOfClass; j++){
						 // unlabeled.get(q).print();
						 // System.out.println("Labeled size = "+labeled.getFromClass(j).size());
						  if(labeled_sub2.getFromClass(j).size() >0){
						  
							  NearClass[j] = new Prototype (labeled_sub2.getFromClass(j).nearestTo(pool2.get(q)));				  
							  probabilities2[q][j] = Math.exp(-1*(Distance.absoluteDistance(NearClass[j], pool2.get(q))));
							  sumatoria+= probabilities2[q][j];
						  }else{
							  probabilities2[q][j] = 0;
						  }
					  }
					  
					  for (int j=0 ; j< this.numberOfClass; j++){
						  probabilities2[q][j]/=sumatoria;
					  }
				  
				  }
			  
			  }else if(this.classifier2.equalsIgnoreCase("C45")){
				  
				  C45 c45 = new C45(labeled_sub2.toInstanceSet(), pool2.toInstanceSet());      // C4.5 called
				  
			      pre2 = c45.getPredictions();    
				  
			      probabilities2 = c45.getProbabilities();
				  
				  c45 = null;
				  System.gc();
				  
			  }else if(this.classifier2.equalsIgnoreCase("NB")){
				  
				  HandlerNB nb = new HandlerNB(labeled_sub2.prototypeSetTodouble(), labeled_sub2.prototypeSetClasses(), pool2.prototypeSetTodouble(), pool2.prototypeSetClasses(),this.numberOfClass);
				  
			      pre2 = nb.getPredictions();    
		  
			      probabilities2 = nb.getProbabilities();
  
			      //System.out.println("Naive Bayes Finishes...");
			      nb= null;
			      System.gc();
				  
			  }else if(this.classifier2.equalsIgnoreCase("SMO")){
				  
				 // System.out.println("SVM Executing...");
					 
			      HandlerSMO SMO = new HandlerSMO(labeled_sub2.toInstanceSet(), pool2.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
			      
			      pre2 = SMO.getPredictions(0);    
			      
			      probabilities2 = SMO.getProbabilities();
  
			      SMO  = null;
			      System.gc();
			    //  System.out.println("SVM Finishes...");
		  
			  }
			  
			  this.cambiarContextoAttributes(); // Change Context
			  // selecting best kj[j] prototypes.
			  
			  
			  indexClase2 = new int[this.numberOfClass][];
				
              
			  for (int j=0 ; j< this.numberOfClass; j++){
				 // maximoClase[j] = new double[(int) kj[j]];
				  indexClase2[j] = new int[(int) kj[j]];
				  
				 //Arrays.fill(maximoClase[j], Double.MIN_VALUE);
				 Arrays.fill(indexClase2[j], -1);
			  }
	
		
			  for (int j=0; j< this.numberOfClass; j++){
				  // for each class, ordenar vector de prob.
				  double [] aOrdenar = new double[pool2.size()];
				  int [] position = new int [pool2.size()];
				  
				  for(int q=0;q<pool2.size(); q++){  
					  aOrdenar[q] =  probabilities2[q][j];
					  position[q] = q;
				  }
				  
				  Utilidades.quicksort(aOrdenar, position); // orden ascendente!
 
				  
				  for(int z=pool2.size()-1; z>=pool2.size()-kj[j];z--){
					  indexClase[j][(pool2.size()-1)-z] = position[z];
				  }
			  }
			  
			  /*
			  // determine who are the best prototypes
				 maximoClase2 = new double[this.numberOfClass][];
				 indexClase2 = new int[this.numberOfClass][];
				 
			  for (int j=0 ; j< this.numberOfClass; j++){
				  maximoClase2[j] = new double[(int) kj[j]];
				  indexClase2[j] = new int[(int) kj[j]];
				  
				 Arrays.fill(maximoClase2[j], Double.MIN_VALUE);
				 Arrays.fill(indexClase2[j], -1);
			  }
		

		 
				  for (int q=0; q<pool2.size(); q++){  // for each unlabeled.
		
					  for (int j=0 ; j< this.numberOfClass; j++){
					  
						  boolean fin = false;
						  for(int z=(int)kj[j]-1; z>=0 && !fin; z--){
							  if(probabilities2[q][j]> maximoClase2[j][z]){
									fin = true;
								  maximoClase2[j][z] = probabilities2[q][j];
								  indexClase2[j][z] = q;
							  }
						  }
							 

					  }
				  
				  }
				  
			  
			  */
			  
			  
			  // adding most-confident predictions:
			 //Add these self-labeled examples to Labeled
				  
			  PrototypeSet toClean = new PrototypeSet();
			  PrototypeSet toCleanU1 = new PrototypeSet();
			  PrototypeSet toCleanU2 = new PrototypeSet();
			  
			  for (int j=0 ; j< this.numberOfClass; j++){
				
				  for(int z=0; z<kj[j];z++){
	  
					  //From classifier 1.
					  if(indexClase[j][z]!=-1){
				
						  Prototype nearUnlabeled = new Prototype(POOL.get(indexClase[j][z]));
						 
						  nearUnlabeled.setFirstOutput(pre[indexClase[j][z]]);
							
						  if(pre[indexClase[j][z]]==j){
							  labeledPrima.add(new Prototype(nearUnlabeled));
							  Prototype nearFeatures = pool2.get(nearUnlabeled.getIndex());
							  labeled_sub2Prima.add(new Prototype(nearFeatures));  // to the 2nd classifier
							  
							  toCleanU2.add(nearFeatures);
							  toCleanU1.add(pool1.get(nearUnlabeled.getIndex()));
							//  System.out.println("AÃ±adoo 1");
						  }else{
							  toClean.add(POOL.get(indexClase[j][z]));
							  toCleanU1.add(new Prototype(pool1.get(POOL.get(indexClase[j][z]).getIndex())));
							  toCleanU2.add(new Prototype(pool2.get(POOL.get(indexClase[j][z]).getIndex())));
						  }
						  
						  
					  }
					  
					  //From classifier 2.
					  if(indexClase2[j][z]!=-1){
						  Prototype nearUnlabeled = new Prototype(POOL.get(indexClase2[j][z]));
							 
						  nearUnlabeled.setFirstOutput(pre2[indexClase2[j][z]]);
							
						  if(pre2[indexClase2[j][z]]==j){
							  labeledPrima.add(new Prototype(nearUnlabeled));
							  Prototype nearFeatures = pool1.get(nearUnlabeled.getIndex());
							  labeled_sub1Prima.add(new Prototype(nearFeatures)); // to the 1st classifier
							  toCleanU1.add(nearFeatures);
							  toCleanU2.add(pool2.get(nearUnlabeled.getIndex()));
							//  System.out.println("AÃ±adoo 2");
						  }else{
							  toClean.add(POOL.get(indexClase2[j][z]));
							  toCleanU1.add(new Prototype(pool1.get(POOL.get(indexClase2[j][z]).getIndex())));
							  toCleanU2.add(new Prototype(pool2.get(POOL.get(indexClase2[j][z]).getIndex())));
						  }
					  }
				  }
				 
			  }
			  
			/*  System.out.println("labeled prima size = "+labeledPrima.size());
			  System.out.println("to clean size = "+toClean.size());
			  System.out.println("to clean1 size = "+toCleanU1.size());
			  System.out.println("to clean2 size = "+toCleanU2.size());
		*/
			//Then we have to clean the unlabeled data
							  
				for (int j=0 ; j< labeledPrima.size(); j++){
					POOL.borrar(labeledPrima.get(j).getIndex()); //.removeWithoutClass(labeledPrima.get(j));
				}
			
				for (int j=0 ; j<toClean.size(); j++){
					  POOL.borrar(toClean.get(j).getIndex()); //.remove(toClean.get(j));
				}
				  
				//toCleanU1.print();
//pool1.print();
				for (int j=0 ; j< toCleanU1.size(); j++){
					pool1.borrar(toCleanU1.get(j).getIndex()); //.removeWithoutClass(toCleanU1.get(j));
				}
				
				//toCleanU2.print();
				for (int j=0 ; j< toCleanU2.size(); j++){
					pool2.borrar(toCleanU1.get(j).getIndex());
					
				}
				
			  
		  labeled.add(labeledPrima.clone());
		  labeled_sub1.add(labeled_sub1Prima.clone());
		  labeled_sub2.add(labeled_sub2Prima.clone());
		  
		  //Replenish U'
		  //Choose 2*Kj[j] random  examples from U
		  
		  toClean = new PrototypeSet();
		  toCleanU1 = new PrototypeSet();
		  toCleanU2 = new PrototypeSet();
		  

		  for(int j=0; j<this.numberOfClass; j++){
			  
			  for (int z=0; z<(kj[j]*2) && z<unlabeled.getFromClass(j).size();z++){
				  Prototype anadir = unlabeled.getFromClass(j).getRandom();
				  toClean.add(anadir);
				  POOL.add(anadir);
				 // anadir.print();
				  //unlabeled_sub1.get(anadir.getIndex()).print();
				  //unlabeled_sub2.get(anadir.getIndex()).print();
		          pool1.add(unlabeled_sub1.get(anadir.getIndex()));
		          toCleanU1.add(unlabeled_sub1.get(anadir.getIndex()));
		          pool2.add(unlabeled_sub2.get(anadir.getIndex()));
		          toCleanU2.add(unlabeled_sub2.get(anadir.getIndex()));
		          
			  }
		  }
		  
		  for (int j=0 ; j<toClean.size(); j++){
			  unlabeled.borrar(toClean.get(j).getIndex()); //.remove(toClean.get(j));
			  unlabeled_sub1.borrar(toCleanU1.get(j).getIndex());
			  unlabeled_sub2.borrar(toCleanU2.get(j).getIndex());
		  }
		  
		 
	/*	  System.out.println("Labeled size = "+labeled.size());
		  System.out.println("UNLabeled size = "+unlabeled.size());
		  System.out.println("UNLabeledsub1 size = "+unlabeled_sub1.size());
		  System.out.println("UNLabeledsub2 size = "+unlabeled_sub2.size());
		  
		  System.out.println("POOL size = "+POOL.size());
		  System.out.println("pool1 size = "+pool1.size());
		  System.out.println("pool2 size = "+pool2.size());
	*/
		  //re-established the indexes:
		  
	      for (int j=0; j< POOL.size();j++){
	          POOL.get(j).setIndex(j); 
	          pool1.get(j).setIndex(j); 
	          pool2.get(j).setIndex(j); 
	      }
	      
	      for (int j=0; j< labeled.size();j++){
	          labeled.get(j).setIndex(j); 
	      }
	      
	      for (int j=0; j< labeled_sub1.size();j++){
	    	  labeled_sub1.get(j).setIndex(j); 
	      }
	      
	      for (int j=0; j< labeled_sub2.size();j++){
	    	  labeled_sub2.get(j).setIndex(j); 
	      }
	      
	      for (int j=0; j< unlabeled_sub1.size();j++){
	    	  unlabeled_sub2.get(j).setIndex(j); 
	    	  unlabeled_sub1.get(j).setIndex(j); 
	    	  unlabeled.get(j).setIndex(j); 
	      }
	      
				  

		  
	  }

	  
	//  System.out.println("Labeled size = " +labeled.size());
	 // System.out.println("Unlabeled size = " + unlabeled.size());
	  
	  

	// labeled.print();

	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  int traPrediction[] = null;
	  int tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
	  
	  if(this.final_classifier.equalsIgnoreCase("NN")){
		  
		  //We have to return the classification done.
		  for(int i=0; i<this.transductiveDataSet.size(); i++){
			   tranductive.get(i).setFirstOutput((labeled.nearestTo(this.transductiveDataSet.get(i))).getOutput(0));
		  }
		  
		  for(int i=0; i<this.testDataSet.size(); i++){
			  test.get(i).setFirstOutput((labeled.nearestTo(this.testDataSet.get(i))).getOutput(0));
		  }
		  
		  // Transductive Accuracy 
		  System.out.println("AccTrs ="+KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
		  
		  // test accuracy
		  System.out.println("AccTst ="+KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
	  
	  }else if(this.final_classifier.equalsIgnoreCase("C45")){
     
		  C45 c45 = new C45(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet());      // C4.5 called
		  
		  traPrediction = c45.getPredictions();
		  
	      c45 = new C45(labeled.toInstanceSet(), testDataSet.toInstanceSet());      // C4.5 called
	      
		  tstPrediction = c45.getPredictions();
		  
	  }else if(this.final_classifier.equalsIgnoreCase("NB")){
	
		  HandlerNB nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), transductiveDataSet.prototypeSetTodouble(), transductiveDataSet.prototypeSetClasses(),this.numberOfClass);
		  
		  traPrediction = nb.getPredictions();  

		  nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), testDataSet.prototypeSetTodouble(), testDataSet.prototypeSetClasses(),this.numberOfClass);
		  tstPrediction = nb.getPredictions();
  
	  }else if(this.final_classifier.equalsIgnoreCase("SMO")){
		  
		
	      HandlerSMO SMO = new HandlerSMO(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
	      
	      traPrediction = SMO.getPredictions(0);    
	      
			

	      SMO = new HandlerSMO(labeled.toInstanceSet(), testDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED)); 
		  tstPrediction = SMO.getPredictions(0);

		  
	  }
	  
	  
	  if(this.final_classifier.equalsIgnoreCase("C45") || this.final_classifier.equalsIgnoreCase("NB") || this.final_classifier.equalsIgnoreCase("SMO") ){
	
	      aciertoTrs = 0;
	      aciertoTst = 0;
	  
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
	  
		  
	  }


	  tranductive.save("outputCoSMO.dat");

	  
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
