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
	SETRED.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-1-2011
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Semi_Supervised_Learning.SETRED;


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

public class SETREDGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/

 
 private int numberOfselectedExamples;
 private int MaxIter;
 private String classifier; 
 private double threshold;
  

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

  
  /**
   * Build a new SETREDGenerator Algorithm
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
  public SETREDGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="SETRED";
      
  }
  


  /**
   * Build a new SETREDGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param unlabeled Original unlabeled prototype set for SSL.
     * @param test Origital test prototype set.
     * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public SETREDGenerator(PrototypeSet t, PrototypeSet unlabeled, PrototypeSet test, Parameters parameters)
  {
      super(t,unlabeled, test, parameters);
      algorithmName="SETRED";
   
    
      this.numberOfselectedExamples =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.threshold = parameters.getNextAsDouble();

      
      //Last class is the Unknown 
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      System.out.print("\nIsaacSSL dice:  " + this.numberOfselectedExamples+ ", "+ this.numberOfClass +"\n");

  }
  
    /**
     * Computes the factorial number of the given one.
     * @param x given number.
     * @return the factorial number of the given one.
     */
    public long factorial (int x){
	 long factor =1;
	
	 if(x!=0){
	  for (int i=1; i<=x ; i++){
		  factor*=i;
	  }
	 }
	 
	  return factor;
  }
  
      /**
     * Computes the bernuilli value for a given number with the given probability and value of n.
     * @param prob bernuilli probability.
     * @param n bernuilli n value.
     * @param x given number.
     * @return the bernuilli value for a given number with the given probability and value of n.
     */
  public double bernuilli(double prob, int n, int x){
	  
	  double bernuilli = 1;
	  

  double f1 =factorial(n-x), f2 = factorial(x), f3=factorial(n);
	  
  if(f1!=0 && f2!=0 &&  f3!=0){
	  bernuilli *= f3/(f2*f1);
	  bernuilli *= Math.pow(prob, x) * Math.pow(1-prob, n-x);
  }else{
	  bernuilli=1;
  }
	  return bernuilli;
	  
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
	  
	  
	  //labeled.print();
	  //unlabeled.print();
	  
	  System.out.println("Labeled size = " +labeled.size());
	  System.out.println("Unlabeled size = " + unlabeled.size());
	  
	  
	  // kj is the number of prototypes added from class j, that it must be propornotional to its ratio.
	  
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
		  maximoKj+=kj[i];
		 // System.out.println(kj[i]);
	  }
	  


	  
	  
	  for (int i=0; i<this.MaxIter && unlabeled.size()>maximoKj; i++){
		  
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
						  confidence[q][j] =0;
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
				
				  if(contadorClase[j]< kj[j]){
				    Prototype nearUnlabeled = new Prototype(unlabeled.get(indexClase[j]));
				  
				  	Prototype clase = labeled.nearestTo(nearUnlabeled);
					  
				  	nearUnlabeled.setFirstOutput(clase.getOutput(0));
					  
				  	labeledPrima.add(new Prototype(nearUnlabeled));
		
				  	contadorClase[(int)clase.getOutput(0)]++;
				  }
			  }
			  
			  
			//Then we have to clean the unlabeled have to clean.
				for (int j=0 ; j< labeledPrima.size(); j++){
					unlabeled.removeWithoutClass(labeledPrima.get(j)); 
				}
			  
			  
			  
				condicionFIN = true;
	
				
				//System.out.println(contadorClase[0]);
				
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
		  
		  
		  /*
		  for (int j=0; j<this.numberOfClass; j++){
							  
			  for (int k=0; k< kj[j]; k++){
				  Prototype nearUnlabeled = new Prototype(unlabeled.nearestTo(labeled.getFromClass(j)));
				  
				  
				  Prototype clase = labeled.nearestTo(nearUnlabeled);
  				  
				  unlabeled.removeWithoutClass(nearUnlabeled); //First, You have to clean.
				  
				  if(clase.getOutput(0)==j){
					  	
					  nearUnlabeled.setFirstOutput(j);
					  labeledPrima.add(nearUnlabeled);
				  }
				  
			  }
			
		  }// END For each class	
		  
		  */
		  
		  
		  PrototypeSet labeledUnion = new PrototypeSet(labeled.clone());
		  labeledUnion.add(labeledPrima);
		  
		  //System.out.println("Labeled size = " +labeled.size());
		//  System.out.println("Unlabeled size = " + unlabeled.size());
		  
			  //Now, SETRED applies a Data editing technique.
			  
			  //Construction of a neighborhood graph
			  boolean adjacencia [][] = new boolean[labeledUnion.size()][labeledUnion.size()];
			  
			  for(int l=0; l<labeledUnion.size(); l++){
				  Arrays.fill(adjacencia[l], false);
			  }
			  //Calculing all the distances:
			  double dist[][] = new double[labeledUnion.size()][labeledUnion.size()];
			  
			  
			  for(int p=0; p<labeledUnion.size(); p++){
				  
				  for(int q=0; q<labeledUnion.size(); q++){
				  			  
					  if(p!=q){
						  dist[p][q]=Distance.absoluteDistance(labeledUnion.get(q), labeledUnion.get(p));
					  }
				  }
			  }
			  //Build a neighborhood graph
			  
			  for(int p=0; p<labeledUnion.size(); p++){
				  
				  for(int q=0; q<labeledUnion.size(); q++){
			 					  
					  if(p!=q){
						 boolean edge = true;
						  
						  for(int n=0; n<labeledUnion.size() && edge; n++){
							  
							  if(n!=p && n!=q){
								  if(dist[p][q]> Math.max(dist[p][n], dist[q][n])){
									  edge = false;
								  }
							  }
							  
						  }
						  
						  adjacencia[p][q] = edge;
						  					  
					  }
				  }
				  
			  } //End Graph-Construcction.
			  
		  
			  // For each prototype of L'
			  
			  //weights are 1/(1+dist[p][q])
			  // In kj[i] we have the  proportion of examples of this class.
			  
			  double sumCutEdge[] = new double[labeledPrima.size()];
			  double sumCutEdgeCuadrado[] = new double[labeledPrima.size()]; 
			  double expectation[] = new double[labeledPrima.size()]; 
			  double variance[] = new double[labeledPrima.size()]; 
			  double observation[] = new double[labeledPrima.size()]; 
			  double Z[] = new double[labeledPrima.size()];
			  double p_value[] = new double[labeledPrima.size()];
			  
			  int cont =labeledPrima.size()-1;
			  
			//  System.out.println("NÃºmero a aÃ±adir= "+ labeledPrima.size());
			  
			  
			  for(int p=labeledUnion.size()-1; p>=(labeledUnion.size()-labeledPrima.size()); p--){
				  sumCutEdge[cont] = 0;
				  sumCutEdgeCuadrado[cont] = 0;
				  int adjacentes =0;
				  
				  // Calcular Vecindario. Y cutEdges.
				  
				  for(int q=0; q<labeledUnion.size(); q++){
					
					  
					  if(adjacencia[p][q]){  // if this instance belongs to its neighborhood
						  adjacentes++;
						  
						  if(labeledUnion.get(p).getOutput(0)!=labeledUnion.get(q).getOutput(0)){
							  sumCutEdge[cont] += 1./(1+dist[p][q]);
							  sumCutEdgeCuadrado[cont] += (1./(1+dist[p][q])) * (1./(1+dist[p][q]));
							  
								  
						//	  System.out.println("Tengo cut-edges");
						  }
					  }
				  }
				  
				  // adjacentes tiene el tamaÃ±o del vecindario.
		
				  int contador =0; // to determine the number in the neighborhood
				  
				  for(int q=0; q<labeledUnion.size(); q++){
					
					  
					  if(adjacencia[p][q]){  // if this instance belongs to its neighborhood
						  
						  contador++;  // 1-
						//  System.out.println("Bernuilii -> proportion = " + (1-proportion[(int)labeledUnion.get(p).getOutput(0)])+", N = "+ adjacentes + ", X= " +contador);
						  
						  observation[cont] += (1./(1+dist[p][q]))* bernuilli(1-proportion[(int)labeledUnion.get(p).getOutput(0)], adjacentes, contador); //*Ibernuilli
					  }
					  
				  }

				//  System.out.println("SymCut Edge ->"+ sumCutEdge[cont]);
				  
				   expectation[cont] = sumCutEdge[cont]*(1.-proportion[(int)labeledUnion.get(p).getOutput(0)]);
				   variance[cont] = sumCutEdgeCuadrado[cont]* proportion[(int)labeledUnion.get(p).getOutput(0)]* (1-proportion[(int)labeledUnion.get(p).getOutput(0)]);
				  
				   
				   Z[cont] = (observation[cont]-expectation[cont])/ Math.sqrt(variance[cont]);
				   
				   
				//   System.out.println("Z ->"+ Z[cont]);
				   NormalDistribution normal = new NormalDistribution();
				   normal.setMean(expectation[cont]);
				   normal.setSigma(Math.sqrt(variance[cont]));
					 
				   p_value[cont] = normal.getTipifiedProbability(Z[cont], false);
				   
				//   System.out.println("P-value= "+ normal.getTipifiedProbability(Z[cont], false));
				   
				   
				   cont--;
			  }
			  
			  
			  for(int l=0; l<labeledPrima.size(); l++){ 
				  if(p_value[l]>this.threshold){
					  
					  labeled.add(labeledPrima.get(l)); 
				  }
				  
				  /*else{
					  System.out.println("No lo AÃ±ado, estÃ¡ a la izquierda");
				  }*/
				  
			  }


		  
		  //System.out.println("Labeled size = "+labeled.size());
		  //System.out.println("UNLabeled size = "+unlabeled.size());
	  }

	  
	  System.out.println("Labeled size = "+labeled.size());
	  System.out.println("UNLabeled size = "+unlabeled.size());
	  

	  
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