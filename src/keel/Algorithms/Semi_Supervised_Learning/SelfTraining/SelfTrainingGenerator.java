
/*
	SelfTraining.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.SelfTraining;

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
import keel.Dataset.Attributes;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * This class implements the Self-traning wrapper. You can use: Knn, C4.5, and SMO as classifiers.
 * @author triguero
 *
 */

public class SelfTrainingGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String classifier; 
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new SelfTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public SelfTrainingGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="SelfTraining";
      
  }
  


  /**
   * Build a new SelfTrainingGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public SelfTrainingGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="SelfTraining";
   
    
      this.numberOfselectedExamples =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.classifier = parameters.getNextAsString();

      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
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
   * Apply the SelfTrainingGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm() throws Exception
  {
	  System.out.print("\nThe algorithm SELF TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  // established indexes
	  for(int i=0; i<labeled.size(); i++){
		  labeled.get(i).setIndex(i);
	  }
	  
	  for(int i=0; i<unlabeled.size(); i++){
		  unlabeled.get(i).setIndex(i);
	  }
	  
	  // Accuracy with initial labeled data.
	  
	  if(this.classifier.equalsIgnoreCase("NN")){
	  
		  System.out.println("AccTrs with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
		  System.out.println("AccTst with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
		  
		  
		  System.out.println("Labeled size = " +labeled.size());
		  System.out.println("Unlabeled size = " + unlabeled.size());
	  
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
		 // System.out.println(kj[i]);
	  }
	  


	  // In order to avoid problems with C45 and NB.
	  for(int p=0; p<unlabeled.size(); p++){
		  unlabeled.get(p).setFirstOutput(0); // todos con un valor válido.
	  }
	  
	  
	  for (int i=0; i<this.MaxIter && unlabeled.size()>maximoKj; i++){
		  
		  PrototypeSet labeledPrima = new PrototypeSet();
		
		  //double maximoClase[] = new double[this.numberOfClass];
		  //int indexClase[] = new int[this.numberOfClass];
		  
		  
		  double maximoClase[][] = new double[this.numberOfClass][];
		  int indexClase[][] = new int[this.numberOfClass][];

	  /*    
	      
		  boolean condicionFIN = false;
		  
		  double contadorClase[] = new double[this.numberOfClass];
		  Arrays.fill(contadorClase, 0);
		  
		  while(!condicionFIN){
		*/	  
			  
			  
		      int[] pre = new int[unlabeled.size()];    
		      double [][] probabilities = new double[unlabeled.size()][this.numberOfClass];
		      

			  
			  if(this.classifier.equalsIgnoreCase("NN")){ 
				  
			 
				  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
					  
					  Prototype NearClass[] = new Prototype[this.numberOfClass];
		
					  
					  double sumatoria = 0;
					  for (int j=0 ; j< this.numberOfClass; j++){
						 // unlabeled.get(q).print();
						 // System.out.println("Labeled size = "+labeled.getFromClass(j).size());
						  if(labeled.getFromClass(j).size() >0){
						  
							  NearClass[j] = new Prototype (labeled.getFromClass(j).nearestTo(unlabeled.get(q)));				  
							  probabilities[q][j] = Math.exp(-1*(Distance.absoluteDistance(NearClass[j], unlabeled.get(q))));
							  sumatoria+= probabilities[q][j];
						  }else{
							  probabilities[q][j] = 0;
						  }
					  }
					  
					  for (int j=0 ; j< this.numberOfClass; j++){
						  probabilities[q][j]/=sumatoria;
					  }
				  
				  }
				  

			  
			  
			  }else if(this.classifier.equalsIgnoreCase("C45")){
				  
				   /*labeled.save("labeled.dat");
					
				  unlabeled.save("unlabeled.dat");
				
			     // C45 c45 = new C45("labeled.dat", "unlabeled.dat");      // C4.5 called
			      */
				  getSolicitaGarbageColector();
				  
				  C45 c45 = new C45(labeled.toInstanceSet(), unlabeled.toInstanceSet());      // C4.5 called
				  
			      pre = c45.getPredictions();    
				  
			      probabilities = c45.getProbabilities();
				  
			      c45 = null;
			      
			      getSolicitaGarbageColector();
			  }else if(this.classifier.equalsIgnoreCase("NB")){
				  
				 // System.out.println("Naive Bayes Executing...");
					 
				  /*
				 labeled.save("labeled.dat");
				  unlabeled.save("unlabeled.dat");
			      HandlerNB nb = new HandlerNB("labeled.dat", "unlabeled.dat", unlabeled.size(), this.numberOfClass);      // C4.5 called
	               */
				  
				  getSolicitaGarbageColector();
				  
				  HandlerNB nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), unlabeled.prototypeSetTodouble(), unlabeled.prototypeSetClasses(),this.numberOfClass);
				  
			      pre = nb.getPredictions();    
		  
			      probabilities = nb.getProbabilities();
  
			      //System.out.println("Naive Bayes Finishes...");
			     
			      nb= null;
			      getSolicitaGarbageColector();
			      
				  
			  }else if(this.classifier.equalsIgnoreCase("SMO")){
				  getSolicitaGarbageColector();
				 // System.out.println("SVM Executing...");
					 
			      HandlerSMO SMO = new HandlerSMO(labeled.toInstanceSet(), unlabeled.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
			      
			      pre = SMO.getPredictions(0);    
			      
			      probabilities = SMO.getProbabilities();
  
			    //  System.out.println("SVM Finishes...");
			     
			      SMO = null;
			      getSolicitaGarbageColector();
				  
			  }
			  
			  // selecting best kj[j] prototypes.
			  
			  // determine who are the best prototypes
			  
			  // determine who are the best prototypes
			  
			  // maximoClase = new double[this.numberOfClass][];
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
					  aOrdenar[q] =  probabilities[q][j];
					  position[q] = q;
				  }
				  
				  Utilidades.quicksort(aOrdenar, position); // orden ascendente!
				  
				  /*
				  for(int q=0; q<unlabeled.size(); q++){
					 System.out.print(position[q]+", ");
				  }
				  */
				  //System.out.println(" ");
				  
				  
				  for(int z=unlabeled.size()-1; z>=unlabeled.size()-kj[j];z--){
					  indexClase[j][(unlabeled.size()-1)-z] = position[z];
				  }
			  }
					  
		
			  /*
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
	
		
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
			  
			  PrototypeSet toClean = new PrototypeSet();
			  
			  for (int j=0 ; j< this.numberOfClass; j++){
				
				  //if(contadorClase[j]< kj[j]){
				
				  for(int z=0; z<kj[j];z++){
					  
					  //From classifier 1.
					  if(indexClase[j][z]!=-1){
				  
						  Prototype nearUnlabeled = new Prototype(unlabeled.get(indexClase[j][z]));
						  
						  
						  if(this.classifier.equalsIgnoreCase("NN")){ 
							   
							  
							  	Prototype clase = labeled.nearestTo(nearUnlabeled);
								  
							  	nearUnlabeled.setFirstOutput(clase.getOutput(0));
							  	
								  if(clase.getOutput(0)==j){
									  labeledPrima.add(new Prototype(nearUnlabeled));
								  }else{
									  toClean.add(unlabeled.get(indexClase[j][z]));
								  }
								
							//  	contadorClase[(int)clase.getOutput(0)]++;
							  	
	
								  
	
						  }else if(this.classifier.equalsIgnoreCase("C45") || this.classifier.equalsIgnoreCase("NB") || this.classifier.equalsIgnoreCase("SMO") ){
							  
							  nearUnlabeled.setFirstOutput(pre[indexClase[j][z]]);
							
							  if(pre[indexClase[j][z]]==j){
								  labeledPrima.add(new Prototype(nearUnlabeled));
							  }else{
								  toClean.add(unlabeled.get(indexClase[j][z]));
							  }
							  
							  
							 // contadorClase[pre[indexClase[j]]]++;
							  
	
						  }
					  
				  
					  
					  	
					  }
				  
				  }
			  }
			  
			  
				//Then we have to clean the unlabeled have to clean.
				for (int j=0 ; j< labeledPrima.size(); j++){
					//unlabeled.removeWithoutClass(labeledPrima.get(j)); 
					unlabeled.borrar(labeledPrima.get(j).getIndex()); 
				}
			  
			  for (int j=0 ; j<toClean.size(); j++){
				 // unlabeled.remove(toClean.get(j));
				  unlabeled.borrar(toClean.get(j).getIndex());
			  }
			  
			  
			  
				//condicionFIN = true;
	
				
				/*
				  for(int m=0; m<this.numberOfClass; m++){
					  System.out.println("COntador de "+ m+" =" +contadorClase[m]);
					  System.out.println(kj[m]);
				  }
				  */
			/*
				for(int j=0; j< this.numberOfClass && condicionFIN; j++){
					if(contadorClase[j] >= kj[j]){
						condicionFIN = true;
					}else{
						condicionFIN = false;
					}
					
				}
				
				if (unlabeled.size()< maximoKj){
					condicionFIN = true;
				}

		  
		  } // END CONDITION
		  */
		  
		  labeled.add(labeledPrima);
		  
				  
		  
		  //Re-established indexes
		  for(int j=0; j<labeled.size(); j++){
			  labeled.get(j).setIndex(j);
		  }
		  
		  for(int j=0; j<unlabeled.size(); j++){
			  unlabeled.get(j).setIndex(j);
		  }
		  
		  System.out.println("Labeled size = "+labeled.size());
		  System.out.println("UNLabeled size = "+unlabeled.size());
	  }

	  
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
		  
			 // labeled.save("labeled.dat");
		  
		  //transductiveDataSet.save("unlabeled.dat");
		
	      //C45 c45 = new C45("labeled.dat", "unlabeled.dat");      // C4.5 called
	      
		  C45 c45 = new C45(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet());      // C4.5 called
		  
	      
		  traPrediction = c45.getPredictions();
		  
		//  testDataSet.save("unlabeled.dat");
			
	      //c45 = new C45("labeled.dat", "unlabeled.dat");      // C4.5 called
		  
	      c45 = new C45(labeled.toInstanceSet(), testDataSet.toInstanceSet());      // C4.5 called
	      
		  tstPrediction = c45.getPredictions();
		  
		  
		  
	  }else if(this.classifier.equalsIgnoreCase("NB")){
		  
		
			
		  HandlerNB nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), transductiveDataSet.prototypeSetTodouble(), transductiveDataSet.prototypeSetClasses(),this.numberOfClass);
		  
		  traPrediction = nb.getPredictions();  
  

		  nb = new HandlerNB(labeled.prototypeSetTodouble(), labeled.prototypeSetClasses(), testDataSet.prototypeSetTodouble(), testDataSet.prototypeSetClasses(),this.numberOfClass);
		  tstPrediction = nb.getPredictions();
		  
	     /*
		  labeled.save("labeled.dat");
	  
		  transductiveDataSet.save("unlabeled.dat");
		
	      HandlerNB nb = new HandlerNB("labeled.dat", "unlabeled.dat", transductiveDataSet.size(), this.numberOfClass);      // C4.5 called
	      
    	  traPrediction = nb.getPredictions();  
		  
		  testDataSet.save("unlabeled.dat");
			
		  nb = new HandlerNB("labeled.dat", "unlabeled.dat", testDataSet.size(), this.numberOfClass); 
		  
		  tstPrediction = nb.getPredictions();
		 */

		 

		  
	  }else if(this.classifier.equalsIgnoreCase("SMO")){
		  
		
	      HandlerSMO SMO = new HandlerSMO(labeled.toInstanceSet(), transductiveDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED));      // SMO
	      
	      traPrediction = SMO.getPredictions(0);    
	      
			

	      SMO = new HandlerSMO(labeled.toInstanceSet(), testDataSet.toInstanceSet(), this.numberOfClass,String.valueOf(this.SEED)); 
		  tstPrediction = SMO.getPredictions(0);

		  
	  }
	  
	  
	  
	  if(this.classifier.equalsIgnoreCase("C45") || this.classifier.equalsIgnoreCase("NB") || this.classifier.equalsIgnoreCase("SMO")  ){
	
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
