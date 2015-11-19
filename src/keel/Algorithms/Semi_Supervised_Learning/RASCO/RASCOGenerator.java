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
	RASCO.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-3-2011
	Copyright (c) 2011 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.RASCO;

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

//import sun.misc.Compare;
//import sun.misc.Sort;

import java.util.StringTokenizer;



/**
 * This class implements the RASCO algorithm. You can use: Knn, C4.5, SMO and  as classifiers.
 * @author triguero
 *
 */

public class RASCOGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 
 private int MaxIter = 10;
 private String classifier = "C45";
 private int numberOfViews = 30;

    /**
     * Number of prototypes.
     */
    protected int numberOfPrototypes;  // Particle size is the percentage

    /**
     * Number of classes.
     */
 protected int numberOfClass;


  
  /**
   * Build a new RASCOGenerator Algorithm
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
  public RASCOGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="RASCO";
      
  }
  


  /**
   * Build a new RASCOGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
     * @param test Origital test prototype set.
     * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public RASCOGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="RASCO";
   
      this.MaxIter =  parameters.getNextAsInt();
      this.numberOfViews =  parameters.getNextAsInt();
      this.classifier = parameters.getNextAsString();

      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " + this.classifier+ ", "+ this.numberOfClass +"\n");

      
      
      numberOfViews = 10;
  }
  
    /**
     * Changes the attributes to a different context.
     * @throws Exception if the context can not be changed.
     */
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
   * Apply the RASCOGenerator method.
     * @return transductive test sets.
     * @throws java.lang.Exception if the algorithm can not be applied.
     */
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm RASCO is starting...\n Computing...\n");
	  
	  PrototypeSet labeled, unlabeled;
	  
	  PrototypeSet labeled_subX[] = new PrototypeSet[this.numberOfViews];
	  PrototypeSet unlabeled_subX[] = new PrototypeSet[this.numberOfViews];
	  PrototypeSet training[] = new PrototypeSet[this.numberOfViews];

	  //System.out.println("paso 1");
	  //The Original attribute sets are randomly partitioned into this.numberOfViews subsets with dimension = (numberofFeatures/2):
	  
	  int dimension = trainingDataSet.get(0).numberOfInputs()/2;
	  System.out.println("Dimension: "+dimension);
	  
	  int indices[][] = new int[this.numberOfViews][dimension];
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	
	 training = trainingDataSet.divideFeaturesRandomly(this.numberOfViews, dimension, indices);

	  for(int i=0; i<this.numberOfViews;i++){
		  labeled_subX[i]= training[i].getAllDifferentFromClass(this.numberOfClass);
		  unlabeled_subX[i]=training[i].getFromClass(this.numberOfClass);
		  

	  }
	



	  
	  training = null;
	  System.gc();
/*
	  unlabeled.print();

	  System.out.println("unlabeled size = "+unlabeled.size());
	  
	  unlabeled_subX[0].print();
	  
	  System.out.println("unlabeledO size = "+ unlabeled_subX[0].size());

	  unlabeled_subX[1].print();
	  
	  System.out.println("unlabeled1 size = "+ unlabeled_subX[1].size());
	  
	  System.out.println("unlabeled size = "+unlabeled.size());
	  System.out.println("unlabeledO size = "+ unlabeled_subX[0].size());
	 */
      for (int j=0; j< labeled.size();j++){
          labeled.get(j).setIndex(j); 

          for(int k=0; k<this.numberOfViews; k++){
	    	  labeled_subX[k].get(j).setIndex(j); 
          }
          
      }
      
  
      
      for (int j=0; j< unlabeled.size();j++){
    	  unlabeled.get(j).setIndex(j); 
          for(int k=0; k<this.numberOfViews; k++){
	    	  unlabeled_subX[k].get(j).setIndex(j); 
          }
      }
      

	  
	  
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
		  System.out.println((int)kj[i]);
	  }
	  


	  // In order to avoid problems with C45 and NB.
	  for(int p=0; p<unlabeled.size(); p++){
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor vÃ¡lido.
          for(int k=0; k<this.numberOfViews; k++){
	    	  unlabeled_subX[k].get(p).setFirstOutput(0);
          }
	  }

	    

      /********************************************/
      //Saving the Attributes state in a file.
	  PrototypeSet noInstancias = new PrototypeSet();
	  noInstancias.add(labeled.get(0));
	  noInstancias.save("antiguo.dat");
	  /**********************************************/
	  

	  for(int i=0; i< this.numberOfViews; i++){
	  
		  for(int j=indices[i].length-1; j>=0;j--){ // quitar del otro conjunto.
			  if(!Attributes.removeAttribute(true,indices[i][j])){
				  System.err.println("ERROR TO CLEAN");
			  }
		  }
	
		  Prototype.setAttributesTypes();
		  PrototypeSet sinInstancias = new PrototypeSet();
		  sinInstancias.save("labeled"+i+".dat");
	
	      cambiarContextoAttributes(); // Change context
      
	  }



	  
	  for (int i=0; i<this.MaxIter && unlabeled.size()>maximoKj ; i++){ //
		  
				  
		  PrototypeSet labeledPrima = new PrototypeSet();
		  
		  double maximoClase[][] = new double[this.numberOfClass][];
		  int indexClase[][] = new int[this.numberOfClass][];
		  
		  int[][] pre = new int[this.numberOfViews][unlabeled.size()];    
		  // In RASCO we don't need a measure of confident from the classifier
		 // double [][] probabilities = new double[unlabeled.size()][this.numberOfClass];

	
	      
		  for(int j=0; j< this.numberOfViews; j++){
		      //**********************************************
		      //Train a view-1 classifier from labeled_subX:
		      //**********************************************
		      
			  //Reading Header, and fill InstanceSET.
			  Attributes.clearAll();
			  InstanceSet label = new InstanceSet();
			  label.readSet("labeled"+j+".dat", true);
	          label.setAttributesAsNonStatic();
	          InstanceAttributes att = label.getAttributeDefinitions();
	          Prototype.setAttributesTypes(att);  
			  PrototypeSet intercambio = new PrototypeSet(label);
			  
			  
			 // System.out.println("paso 2");

			  
			  if(this.classifier.equalsIgnoreCase("NN")){ 
				  
				 
				  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
					  
					  pre[j][q] = (int) labeled_subX[j].nearestTo(unlabeled_subX[j].get(q)).getOutput(0);
				  }				  
			  
			  }else if(this.classifier.equalsIgnoreCase("C45")){

				  getSolicitaGarbageColector();
				  
				  //labeled_subX[j].save("mojon"+j+".dat");
				  InstanceSet uno = labeled_subX[j].toInstanceSet();
				  InstanceSet dos =  unlabeled_subX[j].toInstanceSet();
				  
				  
			     // Thread.sleep(100000);
				
				  
				  C45 c45 = new C45(uno, dos);
					 
				  pre[j] = c45.getPredictions();   
				  
				
			      uno = null;
			      dos = null;  
				  c45 = null;
				  getSolicitaGarbageColector();
				  
				  //probabilities = c45.getProbabilities();
				  				  
			  }else if(this.classifier.equalsIgnoreCase("NB")){
				  getSolicitaGarbageColector();
				 // labeled_subX[j].print();
				 // unlabeled_subX[j].print();        
		          
				  
				  HandlerNB nb = new HandlerNB(labeled_subX[j].prototypeSetTodouble(), labeled_subX[j].prototypeSetClasses(), unlabeled_subX[j].prototypeSetTodouble(),  unlabeled_subX[j].prototypeSetClasses(),this.numberOfClass);
				  
			      pre[j] = nb.getPredictions();    
	
			      nb= null;
				  getSolicitaGarbageColector();
			   //  probabilities = nb.getProbabilities();
  			  
			  }else if(this.classifier.equalsIgnoreCase("SMO")){
				  getSolicitaGarbageColector();
				  InstanceSet uno = labeled_subX[j].toInstanceSet();
				  InstanceSet dos =  unlabeled_subX[j].toInstanceSet();
				  
			      HandlerSMO SMO = new HandlerSMO(uno,dos, this.numberOfClass,String.valueOf(this.SEED));      // SMO
			      
			      pre[j] = SMO.getPredictions(0);    
			      
			  //    probabilities = SMO.getProbabilities();
			      uno = null;
			      dos = null;
			      SMO  = null;
				  getSolicitaGarbageColector();
  	  
			  }
			  
			  this.cambiarContextoAttributes(); // Change Context

			  
		//	  System.out.println("paso 3");
		
			  
			  
		      // Force the Cleaning of some variables
		      label = null;
		      intercambio = null;
		      att = null;
		      
		      System.gc();	  
			  
			  
		  } // end for views
		  

		//  System.out.println("paso 4");
  
		  // determine the confident class label from the voting rule.
			int predicho[] = new int[unlabeled.size()];  
			double confidence[][] = new double[unlabeled.size()][this.numberOfClass];
			
			for(int q=0; q<unlabeled.size(); q++){ 
				Arrays.fill(confidence[q], 0);
			}
			 
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
	
				  for(int j=0; j<this.numberOfViews;j++){
					  confidence[q][pre[j][q]]++; // the confidence of q belongs to pre[j][q] increases
				  }
			  
				//  System.out.println("*********************");
				  for(int j=0; j<this.numberOfClass;j++){
				//	  System.out.println("Confidence "+confidence[q][j]);
					  confidence[q][j]/= (this.numberOfViews*1.);
					// System.out.println("Confidence "+confidence[q][j]);
				  }
				  
			  }
			  
			  
			  // determine the class.
			  
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
				  
				  double maximo = Double.MIN_VALUE;
				  
				  for(int j=0; j<this.numberOfClass;j++){
					  if(confidence[q][j]>maximo){
						  maximo = confidence[q][j];
						  predicho[q] = j;
					  }
				  }
				  
			  }
		
			  
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
				  double [] aOrdenar = new double[unlabeled.size()];
				  int [] position = new int [unlabeled.size()];
				  
				  for(int q=0;q<unlabeled.size(); q++){  
					  aOrdenar[q] =  confidence[q][j];
					  position[q] = q;
				  }
				  
				  Utilidades.quicksort(aOrdenar, position); // orden ascendente!
				  
				  
				  
				  for(int z=unlabeled.size()-1; z>=unlabeled.size()-kj[j];z--){
					  indexClase[j][(unlabeled.size()-1)-z] = position[z];
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
	
			  
			
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
	
				  for (int j=0; j< this.numberOfClass; j++){
				  
					  boolean fin = false;
					  for(int z=(int)kj[j]-1; z>=0 && !fin; z--){
						  if(confidence[q][j]>= maximoClase[j][z]){
							  //Resolve ties randomly
							  if(confidence[q][j]> maximoClase[j][z] || (confidence[q][j]== maximoClase[j][z] && Randomize.Rand()<0.5)){
								fin = true;
							    maximoClase[j][z] = confidence[q][j];
							    indexClase[j][z] = q;
							  }
						  }
					  }
						 

				  }
			  
			  }
			  
			  */
			  // adding most-confident predictions:
			 //Add these self-labeled examples to Labeled
				  
			  PrototypeSet toClean = new PrototypeSet();
			  
			  for (int j=0 ; j< this.numberOfClass; j++){
				
				  for(int z=0; z<kj[j];z++){
	  
					  if(indexClase[j][z]!=-1){ // it can ocurr
						  
						  
						  Prototype nearUnlabeled = new Prototype(unlabeled.get(indexClase[j][z]));
						 
						  nearUnlabeled.setFirstOutput(predicho[indexClase[j][z]]);
							
						  if(predicho[indexClase[j][z]]==j){
							  labeledPrima.add(new Prototype(nearUnlabeled));
							  
							//  System.out.println("AÃ±adoo 1");
						  }else{
							  toClean.add(unlabeled.get(indexClase[j][z]));
							//  System.err.println("ERRORRACO DE COJONES");
						  }
	  
					  }
				  }
				 
			  }
			  
			/*  System.out.println("labeled prima size = "+labeledPrima.size());
			  System.out.println("to clean size = "+toClean.size());

			 */
			//Then we have to clean the unlabeled data
							  
				for (int j=0 ; j< labeledPrima.size(); j++){
					unlabeled.borrar(labeledPrima.get(j).getIndex()); //.removeWithoutClass(labeledPrima.get(j));
				}
			
				for (int j=0 ; j<toClean.size(); j++){
					  unlabeled.borrar(toClean.get(j).getIndex()); //.remove(toClean.get(j));
				}
				  
	
			  
		  labeled.add(labeledPrima.clone());
		  
		  // project L to each of the subspace to get 
		  
		  for(int j=0; j<this.numberOfViews;j++){
			  ArrayList<Integer> lista = new ArrayList<Integer>();
			  
			  for(int z=0; z<indices[j].length; z++){
				  lista.add((Integer) indices[j][z]);
			  }
			  
			  
			  for(int k=0; k<labeledPrima.size(); k++){
				  labeled_subX[j].add(labeledPrima.get(k).getPrototypeWithSelectedInputs(lista));
			  }
		  }
	


		  
		 
		  System.out.println("Labeled size = "+labeled.size());
		  System.out.println("UNLabeled size = "+unlabeled.size());
		  

	
		  //re-established the indexes:
		  
	      for (int j=0; j< labeled.size();j++){
	          labeled.get(j).setIndex(j); 
	          
	          for(int k=0; k<this.numberOfViews; k++){
		    	  labeled_subX[k].get(j).setIndex(j); 
	          }
	      }
	      	      
	      for (int j=0; j< unlabeled.size();j++){
	    	  unlabeled.get(j).setIndex(j); 
	    	  
	          for(int k=0; k<this.numberOfViews; k++){
		    	  unlabeled_subX[k].get(j).setIndex(j); 
	          }
	      }
	      

	      // force the cleaning
	      pre = null;
	      labeledPrima = null;
	      toClean = null;
	      System.gc();
		  
	  } //END of LOOP

	  
	//  labeled.print();
	  System.out.println("Labeled size = " +labeled.size());
	  System.out.println("Unlabeled size = " + unlabeled.size());
	  

	// labeled.print();

	  PrototypeSet tranductive = new PrototypeSet(this.transductiveDataSet.clone());
	  PrototypeSet test = new PrototypeSet(this.testDataSet.clone());
	 
	 
	  int traPrediction[] = null;
	  int tstPrediction[] = null;
	  int aciertoTrs = 0;
	  int aciertoTst = 0;
	  
	  
	  if(this.classifier.equalsIgnoreCase("NN")){
		  
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
	  
	  }else if(this.classifier.equalsIgnoreCase("C45")){
     
		  C45 c45 = new C45(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet());      // C4.5 called
		  
		  traPrediction = c45.getPredictions();
		  
	      c45 = new C45(labeled.toInstanceSet(), testDataSet.toInstanceSet());      // C4.5 called
	      
		  tstPrediction = c45.getPredictions();
		  
	  }else if(this.classifier.equalsIgnoreCase("NB")){
	
		  HandlerNB nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), transductiveDataSet.prototypeSetTodouble(), transductiveDataSet.prototypeSetClasses(),this.numberOfClass);
		  
		  traPrediction = nb.getPredictions();  

		  nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), testDataSet.prototypeSetTodouble(), testDataSet.prototypeSetClasses(),this.numberOfClass);
		  tstPrediction = nb.getPredictions();
  
	  }else if(this.classifier.equalsIgnoreCase("SMO")){
		  
		
	      HandlerSMO SMO = new HandlerSMO(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
	      
	      traPrediction = SMO.getPredictions(0);    
	      
			

	      SMO = new HandlerSMO(labeled.toInstanceSet(), testDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED)); 
		  tstPrediction = SMO.getPredictions(0);

		  
	  }
	  
	  
	  if(this.classifier.equalsIgnoreCase("C45") || this.classifier.equalsIgnoreCase("NB") || this.classifier.equalsIgnoreCase("SMO") ){
	
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
