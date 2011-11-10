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
	MSE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  5-3-09
	Copyright (c) 2009 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.MSE;

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
public class MSEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
  private int numberOfInitialCentroids;
  private double GradientStep;
  private double Temperature;
  
  //others variables.
  protected int numberOfPrototypes;  
  protected int numberOfClass;
 
  
  /**
   * Build a new MSEGenerator Algorithm
   */
  
  public MSEGenerator(PrototypeSet _trainingDataSet, int k, int centroid, double gradStep, double temp)
  {
      super(_trainingDataSet);
      algorithmName="MSE";
      
      this.k = k;
      this.numberOfInitialCentroids = centroid;
      this.GradientStep = gradStep;
      this.Temperature = temp;
      
   

  }
  

  /**
   * Build a new RSPGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public MSEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="MSE";
      
      this.k = parameters.getNextAsInt();
      this.numberOfInitialCentroids = parameters.getNextAsInt();
      this.GradientStep = parameters.getNextAsDouble();
      this.Temperature =parameters.getNextAsDouble();
      
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
    
      System.out.println("Isaac dice: k= " + this.k + " cent = " + this.numberOfInitialCentroids + " gs= " + this.GradientStep + " t ="+ this.Temperature);
      System.out.println("Number of class= "+ this.numberOfClass);
  }
  
  
  

  
  /**
   * Zindex probability, di = x - conjuntoi
   * @param x
   * @param conjunto
   * @param index
   * @return
   */
  protected double probabilityBelongCluster(Prototype x, PrototypeSet conjunto, int index){
	  
	  double dist = 0.0;
	  double numerator;
	  double denominator =0.0;
	  
	  for(Prototype p: conjunto){
		dist = Distance.d(x, p);  
		 // dist*=dist; //dist^2		  
		  denominator += Math.exp( -dist/ this.Temperature);
      }
	  
		dist = Distance.d(x, conjunto.get(index));  
		//dist=dist*dist; //dist^2		  
		numerator = Math.exp( -dist/ this.Temperature);
	  
		//System.out.println(" ziX = "+ numerator/denominator);
	  return (numerator/denominator);
  }
  
  
  /**
   * Desired Probabilities.
   * @param x
   * @param conjunto
   * @param index
   * @return
   */
  protected double desiredProbabilities(Prototype x, PrototypeSet conjunto, int index){
     
	  double output = 0.0; // probabilityBelongCluster(x, conjunto, index);
	  double dist = 0;  
	  double numerator;
	  double denominator =0.0;
	  
	  //if not has the same class
	  if(x.getOutput(0) != (conjunto.get(index)).getOutput(0)){
		  output = 0;
	  }else{
				  
		  for(Prototype p: conjunto){
		
			  if(x.getOutput(0) == p.getOutput(0)){
				  dist = Distance.d(x, p);  
				//  dist*=dist; //dist^2		  
				  denominator += Math.exp( -dist/ this.Temperature);
			  }
		  }
		  
			dist = Distance.d(x, conjunto.get(index));  
			//dist=dist*dist; //dist^2		  
			numerator = Math.exp( -dist/ this.Temperature);
			
			output = numerator /denominator;
	  }
	  
	//System.out.println(" ziX* = "+ output);
	  return output;
  }
  
  
  /**
   * Calculate the cost function.
   * @param X
   * @param conjunto (vectors)
   * @return
   */
  
  protected double costFunction (Prototype X, PrototypeSet conjunto){
	  double coste = 0.0;
	  double term1, term2;
	  
	  for(int i=0; i< conjunto.size(); i++){
	
		  term1 =desiredProbabilities(X,conjunto,i);
		  term2 = probabilityBelongCluster(X,conjunto, i);
		  coste += (term1-term2) * (term1-term2); 
		  
	  }
	  
	  coste = coste*0.5*this.Temperature;
	  
	  return coste;
  }
  
  /**
   * Correct the position of the prototype function.
   * @param X , prototype front Training Set.
   * @param i, prototype to correct
   * @param tData
   */
  protected void modifyLocation(Prototype X, PrototypeSet vectors, Prototype lastIncrements[], int index){
	  //double increment =0.0;
	  // Pi(t+1)  = Pi(t) + APi(t+1).
	  
	  double gradient = 0;
	  double sumatory = 0;
	  double sigmaij= 0;
	  
	  PrototypeSet tData = trainingDataSet; //Soft copy
	  
	  
	  //(X- Pi).
	//System.out.println("******");
		 
	  Prototype diference = X.sub(vectors.get(index));
	//  diference.print();
	  diference = diference.mul(probabilityBelongCluster(X,vectors,index));
	  
	  //diference.print();
	 // System.out.println("Gradient = " +this.GradientStep);
	  diference = diference.mul(this.GradientStep);
	 // diference.print();
	 // System.out.println("******");
	  
	  
	  //Gradient = (X-Pi) *Zi(X) * Sumj (Zj*(X) - Zj(X)) * (sigmaij - Zj(X))
	//  gradient =  probabilityBelongCluster(X,vectors,index); //Distance.d(X, vectors.get(index)) *
	  
	  //System.out.println("ZiX = " + gradient);
	  for(int j= 0; j<vectors.size(); j++){
		  double calc;
		  	  
		  calc= (desiredProbabilities(X,vectors,j) -probabilityBelongCluster(X,vectors,j));
		 
		  //Kronocker Delta 
		  if(index == j){
			  sigmaij = 1;
		  }else{
			  sigmaij = 0;
		  }
		  
		  calc *= (sigmaij - probabilityBelongCluster(X,vectors,j));
		  //System.out.println("Diferencia de Sumatoria=  " + calc );
		  sumatory += calc;
	  }
	  
	  //System.out.println("Sumatoria = "+ sumatory);
	  
	  //gradient *= this.GradientStep;

	  
	 // System.out.println("Gradiente = "+ gradient);
	   
	 Prototype Increment = diference.mul(sumatory);
	 
	// mu = 0.9, I write only 0.9
	 Prototype MuLastIncrement = (lastIncrements[index]).mul(0.9);
	  
	 Prototype IncrementFinal =Increment.add(MuLastIncrement);
  
	 IncrementFinal.applyThresholds();
	 //IncrementFinal.print();
	 //(lastIncrements[index]).print();
	  //increment =( this.GradientStep*gradient) +   0.9*lastIncrements[index];
	  
	  //System.out.println("Increment= " + increment);
	 
	  //add the increment: Pi(t+1) = Pi(t) + LastIncrement
	  ((Prototype)vectors.get(index)).set( ((Prototype)vectors.get(index)).add(IncrementFinal));
	  
	  //Modify the increment.
	   lastIncrements[index].set(IncrementFinal);
  }
  
  
  /**
  * Initialize the output data set    
  * @return Initial prototypeSet
  */    
  @SuppressWarnings("unchecked")
protected PrototypeSet initDataSet()
  {
	  /*
	   * Three steps.
	   * 1) K-means.
	   * 2) Elimination rule Kohonen.
	   * 3) Elimination Rule Van de Merckt.
	   */
	  
	  PrototypeSet initial = new PrototypeSet();
	
	  //Clustering: Calling k-means with trainingDataSet and the numberOfIniticialCentroid 
	
	  // For each class we applies the standard k-means.
	  LinkedList clusters = new LinkedList();
	  
  
	  /*
	   * 	la idea es ejecutar el k-medias C veces, siendo C el nï¿½mero de clases. Coges todos los
			ejemplos de la clase 1 por separado, y ejecutas k-medias con un valor k entre 10 y 20
			(dependerï¿½ del nï¿½mero de ejemplos que haya en esa clase). Te quedas con los k centroides
			finales y repites el proceso con la segunda clase. Asï¿½ en todas las clases.

	   */
	  for(int i= 0; i< this.numberOfClass; i++){
		  PrototypeSet conjunto = trainingDataSet.getFromClass(i).clone();
		  
		  if(conjunto.size()>=this.numberOfInitialCentroids){ //SOLUTION TO FAIL
			  //System.out.println(" conjunto size = " + conjunto.size());
			  PrototypeSet centroid = new PrototypeSet();
	
			  double conjunto2[][];
			  conjunto2 = conjunto.prototypeSetTodouble();
			  double center[][];
			  center = new double [this.numberOfInitialCentroids][conjunto2[0].length];
			  
			  
			 // System.out.println("Calculados los centroides aleatorios!");
			  int clusteres[] =centroid.Cmeans (conjunto2, this.numberOfInitialCentroids, center);	
			  
	
			  clusters.add(clusteres); //i,
		
	
			  
			  //CENTER has been modified.
			  centroid.doubleToprototypeSet(center,i);
			  //centroid.print();
			  initial.add(centroid);  
		  }
	  }
	  
	  
	  //System.out.println("Calculados clusters! Initial size= " + initial.size());
		//--End clustering.

	  
		
	  //initial.print();
	  
	  //k = 20;
	  // Elimination rule kohonen
	  int majority = this.k / 2 + 1;
	  //System.out.println("Mayorï¿½a " + majority);


	  int toClean[] = new int [initial.size()];
	  Arrays.fill(toClean, 0);
	  
	  int pos =0;
	  for(Prototype q : initial){
		  
		  double class_q = q.getOutput(0);
		  //double class_q = clusters[pos];
		  
		  PrototypeSet neighbors=KNN.knn(q, trainingDataSet, this.k);
		    
		  int counter= 0;
		  for(Prototype q1 :neighbors ){
			double class_q1 = q1.getOutput(0);
			
			if(class_q1 == class_q){
				counter++;
			} 
			
		  }
		  
		  //System.out.println("Misma clase = "+ counter);
		  if ( counter < majority){ // We must eliminate this prototype.
			 /* if(!initial.remove(q)){
				  System.err.println("Ocurred an error to clean");
			  }*/
			  toClean [pos] = 1; // we will clean
			  
		  }
		  
		  pos++;
	  }
	  
	  
	  //Now we clean.
	  /*
	  for(int proto: toClean){
		  if(proto == 1){
			  initial.remove(proto);
		  }
		  
		  
	  }*/
		//Clean the prototypes.
		PrototypeSet aux= new PrototypeSet();
		for(int i= 0; i< toClean.length;i++){
			if(toClean[i] == 0)
				aux.add(initial.get(i));
			
		}
		//initial = initial.without(aux);
	  initial  = aux.clone();
		//--End elimination rule kohonen.
	  
	  
	  // Van de Merckt elimination rule.
	  /*  Para hacer esto, los prototipos se reordenan en funciï¿½n del tamaï¿½o de su correspondiente cluster.
	   *-	Tercero,  una segunda regla para eliminar propuesta por Van de Merckt descarta prototipos
	   * redundantes. (Aquellos que se pueden eliminar sin que decrezca la calidad de clasificaciï¿½n en 
	   * el TS.) Para hacer esto, los prototipos se reordenan en funciï¿½n del tamaï¿½o de su correspondiente 
	   * cluster. (nï¿½mero de patrones atraï¿½dos en el TS)., y a continuaciï¿½n se en ese orden creciente
	   *  se calcula el ï¿½ndice de clasificaciï¿½n en Training, poniendo y quitando prototipos.
		Los prototipos que no contribuyen a una mejora se eliminan.*/
	  
	  
	  //First, Prototypes are sorted as functions of the size of therir clusters.
	
	  // sort descending order b distance of an instance to this nearest unlike neigbor.

	  //Calculo el nï¿½mero de prototipos en cada cluster, por clase.
		/*  int counter[] = new int[this.numberOfInitialCentroids];
		  
		 
		  for (int i= 0; i< this.numberOfClass ; i++){
			  Arrays.fill(counter, 0);
			  for(int j= 0; j< this.numberOfInitialCentroids*this.numberOfClass; j++){
				  //System.out.println( j+i*this.numberOfInitialCentroids);
				  //System.out.println(((int[])clusters.get(i))[j+i*this.numberOfInitialCentroids]);
				  counter[((int[])clusters.get(i))[j]]++;
			  }
			  
			  
			  PrototypeSet aux = new PrototypeSet();
			  for(int k=0; k< this.numberOfInitialCentroids; k++){
				  aux.add((Prototype)initial.get(k+i*this.numberOfInitialCentroids));
			  }
			  
			  for(int k=0; k< this.numberOfInitialCentroids; k++){
			  Pair<PrototypeSet,Integer> ordenar = new Pair<PrototypeSet,Integer>((Prototype)initial.get(k+i*this.numberOfInitialCentroids),counter[k]);
			  
			  }
		  }
		  
		*/
	  
	     /* for (int k = 0; k < initial.size(); k++)
      {
          for (int j = 0; j < initial.size()-1; j++)
          {
        	  
        	  if(DistunlikeNeighbor[result.get(j).getIndex()] > DistunlikeNeighbor[result.get(j+1).getIndex()] ){
        		  Prototype aux = result.get(j);
            		result.set(j, result.get(j+1));
              		result.set(j+1,aux);
        		  
        	  }
         	
          }
      }
      */
	  
	  
	  
	  
	  // Following, the classification rate is compute on the TS in both presece and absece of each prototype,
	  // if there is no modification, we eliminated this prototype.
	  
	  
	  boolean marcas[];
	  marcas = new boolean[initial.size()];
	  Arrays.fill(marcas, true);

	  double accuracyInic =KNN.classficationAccuracy(initial, trainingDataSet);
	  double accuracy;
	  
	  for(int i=0; i< initial.size(); i++){
		  marcas[i] = false; //At the begining you don't think you can elimante.
		  
		  
		  PrototypeSet leaveOneOut = initial.without(initial.get(i));
		  accuracy = KNN.classficationAccuracy(leaveOneOut, trainingDataSet);
		  
		  if(accuracy > accuracyInic){
			  
			  marcas[i] = true; // we can eliminate
		  }
		  
		  
	  }
	  
	  //Then we create the result set..
	  
	  PrototypeSet clean = new PrototypeSet();
	  
	  for(int i=0; i< marcas.length; i++){
		  if(!marcas[i]){
			  clean.add(initial.get(i));
		  }
	  }

	  //--Endd Van de Merckt elimination rule.
	  
	  
	  
	  System.out.println("Initial size = " + initial.size());
	  System.out.println("Clean size = " + clean.size());
	  
	  return clean;
     // return super.selecRandomSet(numberOfPrototypesGenerated, true);
  }
  

  

  /**
   * Generate a reduced prototype set by the RSPGenerator method.
   * @return Reduced set by RSPGenerator's method.
   */
  
  
  @SuppressWarnings({ "unchecked", "static-access" })
public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  System.out.println("Number of class "+ this.numberOfClass);
	  
      PrototypeSet outputDataSet = initDataSet();        
 
      System.out.println("Accuracy % " +accuracy(outputDataSet,trainingDataSet));
      System.out.println("Reduction % " + (100-(outputDataSet.size()*100)/trainingDataSet.size()) );
      
      
      int dsort[] = new int [trainingDataSet.size()];
      inic_vector(dsort);
      desordenar_vector(dsort);
      
      
      
      int it=0;
      
      double error = Double.POSITIVE_INFINITY;
      double newError = 0;
      Prototype increments[] = new Prototype[outputDataSet.size()];
      
     // Arrays.fill(increments, 0); // initially there wasn't Increments.
    
      for(int i=0; i< increments.length; i++){
    	  
    	  increments[i] = new Prototype  (trainingDataSet.get(0).numberOfInputs(),1);
    	  for(int j=0; j< increments[i].numberOfInputs(); j++){
    		  increments[i].setInput(j, 0);
    	  }
    	  
      }
      
      
      boolean cambio = true;
      while(cambio)
      {
    	  cambio = false;
          //Debug.errorln("Iteration " + it);
          
          Prototype instance = trainingDataSet.get(dsort[it% trainingDataSet.size()]);

          
          for ( int i= 0; i< outputDataSet.size(); i++){
        	  modifyLocation(instance, outputDataSet, increments,i);        
          }
          
          newError = costFunction (instance, outputDataSet);
          // if there is a stabilisation of the error function or n(t) is too small
          // we finish.
          if ( newError < error || this.GradientStep == 0){
        	  cambio = true;
        	  error = newError;
          }
          
          ++it;
          
          //Deterministic annealing.
          this.GradientStep *= 0.5;
          this.Temperature = 0.9*this.Temperature;// - 0.1* this.Temperature;
          
      }
      
      //Checking all values is in the interval [0,1]
      outputDataSet.applyThresholds();
      
      
   // 7. Detect and eliminate the inactive prototypes.
      
           
      System.out.println("Iterations = "+ it);
      
      System.out.println("Accuracy % " +accuracy(outputDataSet,trainingDataSet));
     System.out.println("Reduction % " + (100-(outputDataSet.size()*100)/trainingDataSet.size()) );
      
     
     
	  boolean marcas[];
	  marcas = new boolean[outputDataSet.size()];
	  Arrays.fill(marcas, true);

	  double accuracyInic =KNN.classficationAccuracy(outputDataSet, trainingDataSet);
	  double accuracy;
	  
	  for(int i=0; i< outputDataSet.size(); i++){
		  marcas[i] = false; //At the begining you don't think you can elimante.
		  
		  
		  PrototypeSet leaveOneOut = outputDataSet.without(outputDataSet.get(i));
		  accuracy = KNN.classficationAccuracy(leaveOneOut, trainingDataSet);
		  
		  if(accuracy > accuracyInic){
			  
			  marcas[i] = true; // we can eliminate
		  }
		  
		  
	  }
	  
	  //Then we create the result set..
	  
	  PrototypeSet clean = new PrototypeSet();
	  
	  for(int i=0; i< marcas.length; i++){
		  if(!marcas[i]){
			  clean.add(outputDataSet.get(i));
		  }
	  }
	  
      System.out.println("Accuracy % " +accuracy(clean,trainingDataSet));
      System.out.println("Reduction % " + (100-(clean.size()*100)/trainingDataSet.size()) );
       
      
      return outputDataSet;   
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
      Parameters.setUse("MSE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      MSEGenerator.setSeed(seed);
      
      int blocks =Parameters.assertExtendedArgAsInt(args,10,"number of blocks", 1, Integer.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      MSEGenerator generator = new MSEGenerator(training, 3,20,0.05,50);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
