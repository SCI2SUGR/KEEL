/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
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
	POC.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.POC;

import keel.Algorithms.Genetic_Rule_Learning.Globals.Rand;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Instance_Generation.Chen.ChenGenerator;
import keel.Algorithms.Instance_Generation.HYB.HYBGenerator;
import keel.Algorithms.Instance_Generation.*;
import java.util.*;

import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.utilities.KNN.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * 
 * @param  numberOfInitialsCentroids
 * @param  k, to use with knn rule in the initialization.
 * @param GradientStep
 * @param Temperature
 * @author Isaac Triguero
 * @version 1.0
 */
public class POCGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  

  private double alfaRatio;
  private String method;

  //others variables.
  protected int numberOfPrototypes;  
  protected int numberOfClass;
 
  
  /**
   * Build a new POCGenerator Algorithm
   *
   */
  
  public POCGenerator(PrototypeSet _trainingDataSet, double ratio, String Method)
  {
      super(_trainingDataSet);
      algorithmName="POC";
      
      this.alfaRatio = ratio;
      this.method = Method;   

  }
  

  /**
   * Build a new RSPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public POCGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="POC";
      
     
      this.method = parameters.getNextAsString();
      this.alfaRatio = parameters.getNextAsDouble();
      
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      System.out.println("Isaac dice: ratio= " + this.alfaRatio+ " method = " + this.method );
      System.out.println("Number of class= "+ this.numberOfClass);
  }
  
  
  

  
  
  /**
   * S is a training set of n pattern composed of TWO subsets, S1 y S2, with n1,n2 sizes,
   * @param S
   * @return
   * @post the idea is find training points that are close to the decision boundaries and on the correct side of those boundaries.
   * We use this function to find poc-nn pattern and the reamining patterns can be discarded.
   */
  protected Pair<Prototype,Prototype> finding_poc_nn (PrototypeSet S,double class1, double class2){
	  
	  
	  //obtein the protototype with class 1 and 2. (by pairwise) 
	  PrototypeSet S1 = S.getFromClass(class1);
	  PrototypeSet S2 = S.getFromClass(class2);
	  
	  //System.out.println("S1 size = " + S1.size());
	  //System.out.println("S2 size = " + S2.size());
	  Prototype Xm, Xp1, Xp2;
	  
	  if(S1.size() >= S2.size()){
		 Xm = S1.avg();
	 
		 Xp2 = S2.nearestTo(Xm); 
		 Xp1 = S1.nearestTo(Xp2);
	  }else{
		  Xm = S2.avg();
     	  Xp1 = S1.nearestTo(Xm);
		  Xp2 = S2.nearestTo(Xp1);
	  }
	  
	  return new Pair<Prototype,Prototype>(Xp1,Xp2);
  }
  
  
  /**
   * Prototype Selection by Poc-NN algorithm for TWO class classification problem.
   * @param S
   * @return
   */
  protected PrototypeSet selecting_poc_nn( PrototypeSet S,double class1,double class2){
	  PrototypeSet pocNNset = new PrototypeSet(S.size());
	  Pair<Prototype, Prototype> pocnn;
	  Prototype center, auxC;
	  Prototype w;
	  double b;
	  Prototype Xp1, Xp2;
	  PrototypeSet R1, R2;
	  
	  //First find a pocnn prototype in S.
	 // System.out.println("Selecting con class1= "+class1 + " , class2= "+class2);
	  pocnn = finding_poc_nn(S,class1,class2);
	  Xp1 = pocnn.first();
	  Xp2 = pocnn.second();

//	  System.out.println("Iter");
	//  Xp1.print();
	  //Xp2.print();
	  
	  
	  if(Xp1 != null && Xp2 != null ){
	  
		  //Determinae the center point.
		  auxC = Xp1.add(Xp2);
		  center = auxC.mul(0.5);
		
		//  center.print();
		  //Create a separating Hyperplane h: {x|w*x - b = 0}
		  	// calculate w. = (xp1-xp2) // ||xp1-xp2|| (the module)
		  
			  w = Xp1.sub(Xp2);
			  double module = 1./w.module();
			
			  // System.out.println("Module = "+ module);
			  w = w.mul(module);
			  
		    // b = w * c.
			  b = w.mulEscalar(center);
			  //System.out.println("b = " + b );
		  
		  // Save Xp1, Xp2 and the hyperplane.
			  pocNNset.add(Xp1);
			  pocNNset.add(Xp2);
			 // pocNNset.add(w);
			  //pocNNset.add(center);
			//Divide all pattern of S into two regions, R1 and R2
			  
			  R1 = new PrototypeSet();
			  R2 = new PrototypeSet();
			  
			  //Distance between Xp1 and Xp2
			  double dist = Distance.d(Xp1, Xp2);
			  //System.out.println(alfa);
			  //Prototype ecuation1 = w*xi - b >= 0
			  for(int i=0; i< S.size(); i++){
				  Prototype Xi = S.get(i);
				  double Aux;
				  
				  Aux = w.mulEscalar(Xi);
				  Aux -=b;
				  
				  //System.out.println("Aux ="+ Aux);
				  //System.out.println(alfa);
				//  double AuxAbs = Math.abs(Aux);
				  
				  
				  //if(AuxAbs > alfa){ //alfa
					  if(Aux>=0){
					  	R1.add(Xi);
					  }else{
						  R2.add(Xi);
					  }
				  //} // the restard prototype will be considered outliers
				  
				  
			  }
			  
			  //Divide...
	
			//  System.out.println("Prototypes R1 "+ R1.size());
			  //System.out.println("Prototypes R2 " + R2.size());
			  double clasR1 =  Xp1.getOutput(0);
			  double clasR2 = Xp2.getOutput(0);
			  
			  // 6. Find any misclassficiation in both regions. (errores de clasificaciï¿½n)
			  int misR1=0, misR2=0;
			 
			  /*
			  for(Prototype p: R1){
				PrototypeSet aux = R1.without(p);
				if(aux.size()>0){
					Prototype near = aux.nearestTo(p);
					if(near.getOutput(0) != p.getOutput(0)) misR1++;  
				}
			  }
		
			  
			//  System.out.println("errores R1= " + misR1);
			  for(Prototype p: R2){
				  PrototypeSet aux = R2.without(p);
				  if(aux.size()>0){
					Prototype near = aux.nearestTo(p);
					if(near.getOutput(0) != p.getOutput(0)){ misR2++;  
					//p.print();
					//near.print();
					}
				}
			}
			*/
			  //System.out.println("errores R2= " + misR2);
			  
			 boolean marcas[] = new boolean[R1.size()];
			 Arrays.fill(marcas, false);
			 
			  for(int p=0; p< R1.size(); p++){
				  if(R1.get(p).getOutput(0) != clasR1){ // If not has the same class, Misclasifccication
					  double Aux = w.mulEscalar(R1.get(p)); // Acceptance interval
					  Aux -=b;
					  Aux = Math.abs(Aux);
					  
					  if(Aux > this.alfaRatio*dist )
					  	  misR1++;
					  else{
						 marcas[p] = true; // para luego borrarlo...
						 
					  }
				  }
			  }
			  
			  // Borro lo que estï¿½ marcado como outliers!
			  for(int p=R1.size()-1; p> 0; p--){
				  if(marcas[p]){
					  R1.remove(p);
				  }
			  }
			 // System.out.println("errores R1= " + misR1);
			  
			  marcas = new boolean[R2.size()];
			Arrays.fill(marcas, false);
			
			for(int p=0; p<R2.size(); p++){
				  if(R2.get(p).getOutput(0) != clasR2){ // If not has the same class, Misclasifccication
					  double Aux = w.mulEscalar(R2.get(p)); // Acceptance interval
					  Aux -=b;
					  Aux = Math.abs(Aux);
					  
					  if(Aux > this.alfaRatio*dist )
					  	  misR2++;
					  else{
						  marcas[p] = true; // para luego borrarlo...
					  }
				  } 
			  }
		  
			  for(int p=R2.size()-1; p> 0; p--){
				  if(marcas[p]){
					  R2.remove(p);
				  }
			  }
			  
			  //System.out.println("errores R2= " + misR2);
			 
			  if( misR1> 0 && R1.size()>0){
				 pocNNset.add(selecting_poc_nn(R1,class1,class2));
		      }
			  
			  if(misR2 >0 && R2.size()>0){
				 pocNNset.add(selecting_poc_nn(R2,class1,class2));
			  }
	  }else{
		  return null;
	  }
	  
	  return pocNNset;
  } 
  
  
  protected PrototypeSet replacing_poc_nn(PrototypeSet S,double class1, double class2){
	  PrototypeSet morNNset = new PrototypeSet();
	Prototype Xmor = new Prototype();
	  PrototypeSet pocNNset = new PrototypeSet(S.size());
	  Pair<Prototype, Prototype> pocnn;
	  Prototype center;
	  Prototype w;
	  double b;
	  Prototype Xp1, Xp2;
	  PrototypeSet R1, R2;
	  
	  //First find a pocnn prototype in S.
	  pocnn = finding_poc_nn(S,class1,class2);
	  Xp1 = pocnn.first();
	  Xp2 = pocnn.second();

	  if(Xp1 != null && Xp2 != null ){
			  
		 
		  //Determinae the center point.
		  center = Xp1.add(Xp2);
		  center = center.mul(0.5);
		
		  //Create a separating Hyperplane h: {x|w*x - b = 0}
		  	// calculate w. = (xp1-xp2) // ||xp1-xp2|| (the module)
		  
			  w = Xp1.sub(Xp2);
			  double module = 1./w.module();
			 // System.out.println("Module = "+ module);
			  w = w.mul(module);
			  
		    // b = w * c.
			  b = w.mulEscalar(center);
			  //System.out.println("b = " + b );
		  
		  // Save Xp1, Xp2 and the hyperplane.
			  pocNNset.add(Xp1);
			  pocNNset.add(Xp2);
			 // pocNNset.add(w);
			  pocNNset.add(center);
			//Divide all pattern of S into two regions, R1 and R2
			  
			  R1 = new PrototypeSet();
			  R2 = new PrototypeSet();
			  
			  //Distance between Xp1 and Xp2
			  double dist = Distance.d(Xp1, Xp2);
			 
			  //Prototype ecuation1 = w*xi - b >= 0
			  for(int i=0; i< S.size(); i++){
				  Prototype Xi = S.get(i);
				  double Aux;
				  
				  Aux = w.mulEscalar(Xi);
				  Aux -=b;
				  
				  //System.out.println("Aux ="+ Aux);
				  //System.out.println(alfa);
				  double AuxAbs = Math.abs(Aux);
				  
				  
				  if(Aux>=0){
					  	R1.add(Xi);
					  }else{
						  R2.add(Xi);
					  }
				  
				  
			  }
			  //Divide...
	
			  //System.out.println("Prototypes R1 "+ R1.size());
			  //System.out.println("Prototypes R2 " + R2.size());
			  double clasR1 =  Xp1.getOutput(0);
			  double clasR2 = Xp2.getOutput(0);
			  
			  // 6. Find any misclassficiation in both regions. (errores de clasificaciï¿½n)
			  int misR1=0, misR2=0;
			  
			  
				 boolean marcas[] = new boolean[R1.size()];
				 Arrays.fill(marcas, false);
				 
				  for(int p=0; p< R1.size(); p++){
					  if(R1.get(p).getOutput(0) != clasR1){ // If not has the same class, Misclasifccication
						  double Aux = w.mulEscalar(R1.get(p)); // Acceptance interval
						  Aux -=b;
						  Aux = Math.abs(Aux);
						  
						  if(Aux > this.alfaRatio*dist )
						  	  misR1++;
						  else{
							 marcas[p] = true; // para luego borrarlo...
							 
						  }
					  }
				  }
				  
				  // Borro lo que estï¿½ marcado como outliers!
				  for(int p=R1.size()-1; p> 0; p--){
					  if(marcas[p]){
						  R1.remove(p);
					  }
				  }
				 // System.out.println("errores R1= " + misR1);
				  
				  marcas = new boolean[R2.size()];
				Arrays.fill(marcas, false);
				
				for(int p=0; p<R2.size(); p++){
					  if(R2.get(p).getOutput(0) != clasR2){ // If not has the same class, Misclasifccication
						  double Aux = w.mulEscalar(R2.get(p)); // Acceptance interval
						  Aux -=b;
						  Aux = Math.abs(Aux);
						  
						  if(Aux > this.alfaRatio*dist )
						  	  misR2++;
						  else{
							  marcas[p] = true; // para luego borrarlo...
						  }
					  } 
				  }
			  
				  for(int p=R2.size()-1; p> 0; p--){
					  if(marcas[p]){
						  R2.remove(p);
					  }
				  }
				  
			  if( misR1> 0 && R1.size()>0){
				  morNNset.add(replacing_poc_nn(R1,class1,class2));
		      }else{
		    	  if(R1.size()!=0){
		    		  Xmor = R1.avg();
		    		  morNNset.add(Xmor);
		    	  }
		      }
			  
			  if(misR2 >0 && R2.size()>0){
				  morNNset.add(selecting_poc_nn(R2,class1,class2));
			  }else{
				  if(R2.size()!=0){
					  Xmor = R2.avg();
					  morNNset.add(Xmor);
				  }
		      }
		  
	
			  if(misR1==0 && misR2 == 0){
				  if(R1.size()!=0){
					  Xmor = R1.avg();
					  morNNset.add(Xmor);
				  }
				  if(R2.size()!=0){
					  Xmor = R2.avg();
					  morNNset.add(Xmor);
				  }
				  return morNNset;
			  }
	  }else return null;
	  
	  
	  return morNNset;
	  
	  
  }
  @SuppressWarnings({ "unchecked", "static-access" })
  public PrototypeSet reduceSet()
  {

	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of class "+ this.numberOfClass);  
	  
	  PrototypeSet result = new PrototypeSet();
	  PrototypeSet sal;
	 
		  if(this.numberOfClass == 2){
			  if(this.method.equals("selecting")){
				  result =  selecting_poc_nn(trainingDataSet,0.0,1.0);
			  }else{
				  result =  replacing_poc_nn(trainingDataSet,0.0,1.0); 
			  }
		  }else{
			  
			  //Obtain all possibles pairwise 
			  for(int i=0; i<this.numberOfClass-1;i++){
				  for(int j=i+1; j< this.numberOfClass;j++){
					  //System.out.println("Selecting between pair "+i + ","+j);
					  
					  PrototypeSet pairwise = trainingDataSet.getFromClass(i);
					  pairwise.add(trainingDataSet.getFromClass(j));
					  
					  if(this.method.equals("selecting")){
						  sal =selecting_poc_nn(pairwise,i,j);
						  if(sal!=null)
							  result.add(sal);
					  }else{
						  sal =replacing_poc_nn(pairwise,i,j);
						  if(sal!=null)
						  result.add(sal);
					  }
				  }
			  }
			  
			  
		  }
	  
	  

		  if(result.size() ==0){
			  for(int i=1; i< this.numberOfClass;i++){
				  PrototypeSet aux = trainingDataSet.getFromClass(i);
				  if(aux.size()!=0)
					  result.add( aux.get(RandomGenerator.RandintClosed(0,aux.size())));
			  }
			
		  }
      System.out.println("Accuracy % " +accuracy(result,trainingDataSet));
      System.out.println("Reduction % " + (100-(result.size()*100.)/trainingDataSet.size()) );
      

      
      if(result.size() > 1){
		  boolean marcas[];
		  marcas = new boolean[result.size()];
		  Arrays.fill(marcas, true);
	
		  double accuracyInic =KNN.classficationAccuracy(result, trainingDataSet);
		  double accuracy;
		  
		  for(int i=0; i< result.size(); i++){
			  marcas[i] = false; //At the begining you don't think you can elimante.
			  
			  
			  PrototypeSet leaveOneOut = result.without(result.get(i));
			  accuracy = KNN.classficationAccuracy(leaveOneOut, trainingDataSet);
			  
			  if(accuracy > accuracyInic){
				  
				  marcas[i] = true; // we can eliminate
			  }
			  
			  
		  }
		  
		  //Then we create the result set..
		  
		  PrototypeSet clean = new PrototypeSet();
		  
		  for(int i=0; i< marcas.length; i++){
			  if(!marcas[i]){
				  clean.add(result.get(i));
			  }
		  }
		  
	      System.out.println("Accuracy % " +accuracy(clean,trainingDataSet));
	      System.out.println("Reduction % " + (100-(clean.size()*100)/trainingDataSet.size()) );
      }
	  return  result;
 }
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * 4: .Number of blocks

   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {
      Parameters.setUse("POC", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      POCGenerator.setSeed(seed);
      
     // int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      POCGenerator generator = new POCGenerator(training, 0.5, "selection");
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
