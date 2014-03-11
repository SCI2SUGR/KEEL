
/*
	SNNRCE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.SNNRCE;

import keel.Algorithms.Semi_Supervised_Learning.Basic.NormalDistribution;
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

public class SNNRCEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private String classifier; 
 private double threshold;
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new SNNRCEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param perc Reduction percentage of the prototype set.
   */
  
  public SNNRCEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="SNNRCE";
      
  }
  


  /**
   * Build a new SNNRCEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
   * @param params Parameters of the algorithm (only % of reduced set).
   */
  public SNNRCEGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="SNNRCE";
   
    
      this.numberOfselectedExamples =  parameters.getNextAsInt();
      this.threshold = parameters.getNextAsDouble();

      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
  /**
   * Apply the SelfTrainingGenerator method.
   * @return 
   */
  
  
  public Pair<PrototypeSet, PrototypeSet> applyAlgorithm()
  {
	  System.out.print("\nThe algorithm SELF TRAINING is starting...\n Computing...\n");
	  
	  PrototypeSet labeled;
	  PrototypeSet unlabeled;
	  
	  labeled = new PrototypeSet(trainingDataSet.getAllDifferentFromClass(this.numberOfClass)); // Selecting labeled prototypes from the training set.
	  unlabeled = new PrototypeSet(trainingDataSet.getFromClass(this.numberOfClass));
	  
	  
	  // Accuracy with initial labeled data.
	  
	  System.out.println("AccTrs with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
	  System.out.println("AccTst with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
	  
	  
	 // System.out.println("AccTrs with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
	  //System.out.println("AccTst with initial labeled data ="+ KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
	  
	  
	  //labeled.print();
	  //unlabeled.print();
	  
	  System.out.println("Labeled size = " +labeled.size());
	  System.out.println("Unlabeled size = " + unlabeled.size());
	  
	  // kj is the number of prototypes added from class j, that it must be propornotional to its ratio.
	  
	  //First step: proportion of prototypes of class y.
	  
	  double kj[] = new double[this.numberOfClass];
	  double proportion[] = new double[this.numberOfClass];
	  double minimo = Double.MAX_VALUE;
	  
	  for(int i=0; i<this.numberOfClass; i++){
		  
		  if(labeled.getFromClass(i).size() == 0){
			  proportion[i] = 0;
		  }else{
			  proportion[i] = (labeled.getFromClass(i).size()*1./labeled.size());
		  }
		  
		  if(proportion[i]<minimo && proportion[i]!=0){
			  minimo = proportion[i];
		  }
		  //System.out.println(kj[i]);
	  }
	
	  
	  double maximoKj = 0;
	  
	  // The minimum ratio is establish to this.numberOfselectedExamples
	  for(int i=0; i<this.numberOfClass; i++){
		  kj[i] = Math.round(proportion[i]/minimo);
		//  System.out.println(kj[i]);

			  maximoKj+=kj[i];
		 		  
	  }
	  
	//  System.out.println("maximo = "+ maximoKj);
	  // Step 3: Construction of a neighborhodod graph for each unlabeled example.
	 

	  
	  //Construction of a neighborhood graph
	  boolean adjacencia [][] = new boolean[unlabeled.size()][labeled.size()];
	  
	  for(int l=0; l<unlabeled.size(); l++){
		   Arrays.fill(adjacencia[l], false);
	 }
	  
	  //Calculing all the distances:
	  double dist[][] = new double[unlabeled.size()][labeled.size()];
	  
	  
	  for(int p=0; p<unlabeled.size(); p++){ // From unlabeled to labeled
			  
		 for(int q=0; q<labeled.size(); q++){

			 dist[p][q]=Distance.absoluteDistance(unlabeled.get(p), labeled.get(q));
		 }
	  }
	  
	  //Build a neighborhood graph for each unlabeled data.
	  
		  
		  for(int p=0; p<unlabeled.size(); p++){ // From unlabeled to labeled
			  
			  for(int q=0; q<labeled.size(); q++){
		 					  
					 boolean edge = true;
					  
					  for(int n=0; n<labeled.size() && edge; n++){
						  
						  if(n!=q){ // n!=p &&
							  if(dist[p][q]> Math.max(dist[p][n], dist[q][n])){
								  edge = false;
							  }
						  }
						  
					  }
					  
					  adjacencia[p][q] = edge;
					  					  
			
			  }
			  
		  } //End Graph-Construcction.
		  
	  
	  // Checking cutEdges.
		  int originalLABELEDsize = labeled.size();

		  
		  for(int p=0; p<unlabeled.size(); p++){ // For each unlabeled data
			  
			  boolean cutEdge = false;
			  int examples =0;
			  double clase=0;
			  
			  for(int q=0; q<originalLABELEDsize  && !cutEdge; q++){
				  
				  // We have to check if all the neigborhood has the same class.
				  
				//  System.out.println(p + ", " + q);
				  if(adjacencia[p][q]){  // if this instance belongs to its neighborhood
					  examples++;
					  
					  if(examples == 1){
						  clase = labeled.get(q).getOutput(0);
					  }else if(labeled.get(q).getOutput(0) != clase){
						  cutEdge = true;
					  }
					  
				  }
			  }
			  
			  
			 if(!cutEdge && examples >0){
				 // we have to classify this unlabeled data.
				 Prototype nearUnlabeled = new Prototype(unlabeled.get(p));
				 nearUnlabeled.setFirstOutput(clase);
				 labeled.add(nearUnlabeled);
				 //unlabeled.remove(nearUnlabeled); 
			 }
		  }
	  
		//Always, we remove the most confident examples:
		  
		  for(int p=originalLABELEDsize; p<labeled.size(); p++){
			  unlabeled.removeWithoutClass(labeled.get(p));
		  }
	  
		 		  
		//  System.out.println("Labeled size = " +labeled.size());
		 // System.out.println("Unlabeled size = " + unlabeled.size());
		  

		  
		 // System.gc();
	  
		  
		  //Step 4: standard Selftraining.
	

		 

		  
		  // determine Nmax for each class.
		  
		  double nmax[] = new double[this.numberOfClass];
		  
		  for(int i=0; i<this.numberOfClass; i++){
			nmax[i] = proportion[i]*unlabeled.size();  
			
			System.out.println(nmax[i]);
		  }
	  
		  
	
		  
		  //For each class, we select the nearest unlabeled example.
		  PrototypeSet labeledPrima = new PrototypeSet();
		  double confidence[][] = new double[unlabeled.size()][this.numberOfClass];
		  
		  
		  boolean condicionFIN = false;
		  
		  double contadorClase[] = new double[this.numberOfClass];
		  Arrays.fill(contadorClase, 0);
		  
		  
		  while(!condicionFIN){
		  
		  
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
				  
				  Prototype NearClass[] = new Prototype[this.numberOfClass];
	
				  
				  double sumatoria = 0;
				  for (int j=0 ; j< this.numberOfClass; j++){
					  if(labeled.getFromClass(j).size() >0){
						  NearClass[j] = new Prototype (labeled.getFromClass(j).nearestTo(unlabeled.get(q)));				  
						  confidence[q][j] = Math.exp(-1*(Distance.absoluteDistance(NearClass[j], unlabeled.get(q))));
						  sumatoria+= confidence[q][j]; 
					  }else{
						  confidence[q][j] = 0;
					  }

				  }
				  
				  for (int j=0 ; j< this.numberOfClass; j++){
					  confidence[q][j]/=sumatoria;
				  }
			  
			  }
			  
			  // selecting best kj[j] prototypes.
			  
			  // determine who are the best prototypes
			  
			  PrototypeSet best[] = new  PrototypeSet[this.numberOfClass];
			  double maximoClase[] = new double[this.numberOfClass];
			  int indexClase[] = new int[this.numberOfClass];
			  
			  Arrays.fill(maximoClase, Double.MIN_VALUE);
		
			  
			    
			  
			  for (int q=0; q<unlabeled.size(); q++){  // for each unlabeled.
			  
				  for (int j=0 ; j< this.numberOfClass; j++){
	
						  if(confidence[q][j]> maximoClase[j]){
	
							  maximoClase[j] = confidence[q][j];
							  indexClase[j] = q;
						  }
					
	
				  }
			  
			  }
			  
			  
			  for (int j=0 ; j< this.numberOfClass; j++){
				
				  if(contadorClase[j]< nmax[j]){
				    Prototype nearUnlabeled = new Prototype(unlabeled.get(indexClase[j]));
				  
				  	Prototype clase = labeled.nearestTo(nearUnlabeled);
					  
				  	nearUnlabeled.setFirstOutput(clase.getOutput(0));
					  
				  	labeledPrima.add(new Prototype(nearUnlabeled));
		
				  	contadorClase[(int)clase.getOutput(0)]++;
				  }
			  }
			  
			  
			//Then we have to clean the unlabeled have.
				for (int j=0 ; j< labeledPrima.size(); j++){
					unlabeled.removeWithoutClass(labeledPrima.get(j)); 
				}
			  
			  
			  
				condicionFIN = true;
	
				
				//System.out.println(contadorClase[0]);
				
				for(int j=0; j< this.numberOfClass && condicionFIN; j++){
					if(contadorClase[j] >= nmax[j]){  // N+max
						condicionFIN = true;
					}else{
						condicionFIN = false;
					}
					
				}
				
				if (unlabeled.size()< maximoKj){
					condicionFIN = true;
				}

		  
		  } // END CONDITION
		  
		  

		  labeled.add(labeledPrima);
		  

			 		  
		 // System.out.println("Labeled size = "+labeled.size());
		 // System.out.println("UNLabeled size = "+unlabeled.size());
	  


	 // System.out.println("Labeled size = "+labeled.size());
	  //System.out.println("UNLabeled size = "+unlabeled.size());
	  

	   
	  // Step 6: Construct a relative neighborhood graph using labeled. // RELABEL STAGE!
	  

	  System.gc();
	  
	  //Construction of a neighborhood graph
	  boolean adjacencia2 [][] = new boolean[labeled.size()][labeled.size()];
	  
	  for(int l=0; l<labeled.size(); l++){
		   Arrays.fill(adjacencia2[l], false);
	 }
	  
	  //Calculing all the distances:
	  double dist2[][] = new double[labeled.size()][labeled.size()];
	  
	  
	  for(int p=0; p<labeled.size(); p++){ // From labeled to labeled
			  
		 for(int q=0; q<labeled.size(); q++){
			  			  
			if(p!=q){
			 dist2[p][q]=Distance.absoluteDistance(labeled.get(p), labeled.get(q));
			}
		 }
	  }
	  
	  //Build a neighborhood graph for each unlabeled data.
	  
		  
		  for(int p=0; p<labeled.size(); p++){ // From unlabeled to labeled
			  
			  for(int q=0; q<labeled.size(); q++){
		 					  
				  if(p!=q){
					 boolean edge = true;
					  
					  for(int n=0; n<labeled.size() && edge; n++){
						  
						  if(n!=p && n!=q){
							  if(dist2[p][q]> Math.max(dist2[p][n], dist2[q][n])){
								  edge = false;
							  }
						  }
						  
					  }
					  
					  adjacencia2[p][q] = edge;
					  					  
				  }
			  }
			  
		  } //End Graph-Construcction.
		  
	  

		  double sumCutEdge[] = new double[labeled.size()]; // Ji
		  double sumNoCutEdge[] = new double[labeled.size()]; // Ii
		  
		  double ratio[] = new double[labeled.size()]; // Ii
		  double muRatio=0, sigmaRatio=0;
		  
		  for(int p=0; p< labeled.size(); p++){
			  sumCutEdge[p] = 0;
			  sumNoCutEdge[p] = 0;
			  
			  for(int q=0; q<labeled.size(); q++){
				  if(adjacencia2[p][q]){  // if this instance belongs to its neighborhood

					  if(labeled.get(p).getOutput(0)!=labeled.get(q).getOutput(0)){
						  sumCutEdge[p] += 1./(1+dist2[p][q]);
						  //System.out.println("Alguna vez soy igual");
					  }else{
						  sumNoCutEdge[p] += 1./(1+dist2[p][q]);
					  }
					  
				  }
			  }
			  ratio[p] = sumCutEdge[p]/sumNoCutEdge[p];
			 // System.out.println("Ratio p " + ratio[p]);
			  if(!Double.isInfinite(ratio[p])){
				  //System.out.println("SumCutEdge = " + sumCutEdge[p]);
				  //System.out.println("SumNoCutEdge = " + sumNoCutEdge[p]);	  
				  muRatio+= ratio[p];
			  }
			  
			
			 
		  }

		  muRatio/=labeled.size();
		  
		  for(int p=0; p<labeled.size(); p++){
			  if(!Double.isInfinite(ratio[p])){
				  sigmaRatio += (ratio[p]-muRatio)*(ratio[p]-muRatio);
			  }
		  }
		  
		  sigmaRatio/=labeled.size();
		  
		  //System.out.println("Mean = " + muRatio + ", Sigma = "+ sigmaRatio);
		  
		  NormalDistribution normal = new NormalDistribution();
		  normal.setMean(muRatio);
		  normal.setSigma(Math.sqrt(sigmaRatio));
		  
		  double Ucritic = 1-(this.threshold/2.);
		  //System.out.println("Ucritic " + Ucritic);
		  
		  double InvNormal = normal.inverseNormalDistribution(Ucritic);
		  //System.out.println("Inversa normal " + InvNormal);
		  
		  double RatioCritical = muRatio + InvNormal*Math.sqrt(sigmaRatio);

		  
		 // System.out.println("RatioCritical " + RatioCritical);
		  
		  //Step 7: relabel 
		  
		  
		 
		  
		  for(int p=0; p< labeled.size(); p++){
			  if(ratio[p]>RatioCritical && labeled.getFromClass(labeled.get(p).getOutput(0)).size()>1){

				  if(labeled.getAllDifferentFromClass(labeled.get(p).getOutput(0)).size()>1 ){
					  Prototype NearWithDifferent = new Prototype(labeled.nearestToWithDifferentClass(labeled.get(p), labeled.get(p).getOutput(0)));
						 
					  labeled.get(p).setFirstOutput(NearWithDifferent.getOutput(0));
					  
				  }

				 
				// System.out.println("I have changed the class label, ratio = "+ ratio[p]);
			  }
		  }
		  
		//  labeled.print();
		  

		  
		  // Step 8: Apply the NNrule for the rest of prototypes of Unlabeled.
		  
		 
		  for(int p=0; p<unlabeled.size(); p++){
			  Prototype nearest = labeled.nearestTo(unlabeled.get(p));
			  unlabeled.get(p).setFirstOutput(nearest.getOutput(0));
		  }
		  
		  labeled.add(unlabeled.clone());
		  
	//	  System.out.println("Labeled size = " +labeled.size());
		//  System.out.println("Unlabeled size = " + unlabeled.size());
		  
		  // Results
	  
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
		  System.out.println("AccTrs ="+KNN.classficationAccuracy(labeled,this.transductiveDataSet,1)*100./this.transductiveDataSet.size());
		  
		  // test accuracy
		  System.out.println("AccTst ="+KNN.classficationAccuracy(labeled,this.testDataSet,1)*100./this.testDataSet.size());
	
	  
	  
	
	  

	  
	  
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