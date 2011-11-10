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
	DEGL.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.DEGL;

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

import org.core.*;

//import sun.tools.tree.ThisExpression;

import java.util.StringTokenizer;




/**
 * @param k Number of neighbors
 * @param Population Size.
 * @param ParticleSize.
 * @param Scaling Factor.
 * @param Crossover rate.
 * @param Strategy (1-5).
 * @param MaxIter
 * @author Isaac Triguero
 * @version 1.0
 */
public class DEGLGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate;
  private double WeightFactor;
  private double WeightFactorAdap[];
  private int neighboors; /// numbers of neighboor to search.
  private String WeightScheme;
  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new DEGLGenerator Algorithm
   */
  
  public DEGLGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="DEGL";
      
      this.k = neigbors;
      this.PopulationSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      
      this.ScalingFactor = F;
 
      
  }
  


  /**
   * Build a new DEGLGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public DEGLGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="DEGL";
      this.k =  parameters.getNextAsInt();
      this.PopulationSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.ScalingFactor = parameters.getNextAsDouble();
      this.CrossOverRate = parameters.getNextAsDouble();
      this.WeightFactor = parameters.getNextAsDouble();
      this.neighboors = parameters.getNextAsInt();
      this.WeightScheme = parameters.getNextAsString();
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
   
      System.out.print("\nIsaac dice:  " + k + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" CR=  "+this.CrossOverRate+ "\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  
  
  public PrototypeSet mutant(PrototypeSet population[], int actual, int bestFitnessIndex, int bestNeighboor){
	  
	  
	  PrototypeSet mutant = new PrototypeSet(population[actual].size());
	  PrototypeSet xbest,nbest, p,q, r1,r2, resta1,resta2, producto1,producto2, suma, Local, Global;
	  
	  //Generate the Local trial Vector.
	  
	  nbest = population[bestNeighboor].clone();
	  
	  int prandom, qrandom;
	  int inferior =( actual-this.neighboors)%this.PopulationSize;
	  int superior = (actual+this.neighboors)%this.PopulationSize;
	  if (inferior <0 ) inferior *=-1;
	  if (superior <0) superior*= -1;
	  do{
		  prandom = RandomGenerator.Randint(inferior,superior);
		  qrandom = RandomGenerator.Randint(inferior,superior);
	  }while (prandom != actual && qrandom!= prandom && qrandom!=actual);
	  
	  p = population[prandom].clone();
	  q = population[qrandom].clone();
	  
	  resta1 = nbest.restar(population[actual]);
	  resta2 = p.restar(q);
	  
	  producto1 = resta1.mulEscalar(this.ScalingFactor);
	  producto2 = resta2.mulEscalar(this.ScalingFactor);
	  
	  suma = producto1.sumar(producto2);
	  
	  Local = population[actual].sumar(suma);

	  //Generate the Global trial Vector.

	  xbest = population[bestFitnessIndex].clone();

	  int ran1,ran2;
	  
	  do{
		  ran1 = RandomGenerator.Randint(0, this.PopulationSize);
		  ran2 = RandomGenerator.Randint(0, this.PopulationSize);
	  }while (ran1 != actual && ran2!= ran1 && ran2!= actual);
	  
	  r1 = population[ran1].clone();
	  r2 = population[ran2].clone();
	  
	  resta1 = r1.restar(r2);
	  resta2 = xbest.restar(population[actual]);
	  
	  producto1 = resta1.mulEscalar(this.ScalingFactor);
	  producto2 = resta2.mulEscalar(this.ScalingFactor);
	  
	  suma = producto1.sumar(producto2);
	  
	  Global = population[actual].sumar(suma);
	  
	  
	  // System.out.println("********Mutante**********");
	  
	  if(this.WeightScheme.equals("Adaptive")){
		  producto1 = Global.mulEscalar(this.WeightFactorAdap[actual]);
		  producto2 = Local.mulEscalar(1-this.WeightFactorAdap[actual]);	  
		  
	  }else{
		  producto1 = Global.mulEscalar(this.WeightFactor);
		  producto2 = Local.mulEscalar(1-this.WeightFactor);
	  }
	  mutant = producto1.sumar(producto2);
	  
	  // mutant.print();
	   
     mutant.applyThresholds();
	
	  return mutant;
  }
  /**
   * Generate a reduced prototype set by the DEGLGenerator method.
   * @return Reduced set by DEGLGenerator's method.
   */
   
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  DEGL is starting...\n Computing...\n");
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  if(numberOfPrototypes < trainingDataSet.getPosibleValuesOfOutput().size()){
		  System.out.println("Number of prototypes less than the number of clases");
		  numberOfPrototypes = trainingDataSet.getPosibleValuesOfOutput().size();
	  }
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	  //Algorithm
	  // First, we create the population, with PopulationSize.
	  // like a prototypeSet's vector.  
	  PrototypeSet nominalPopulation;
	  PrototypeSet population [] = new PrototypeSet [PopulationSize];
	  PrototypeSet mutation[] = new PrototypeSet[PopulationSize];
	  PrototypeSet crossover[] = new PrototypeSet[PopulationSize];
	  
	  
	  double fitness[] = new double[PopulationSize];
	  double fitness_bestPopulation[] = new double[PopulationSize];
	  PrototypeSet bestParticle = new PrototypeSet();
	  


	  //Each particle must have   Particle Size %

	  // First Stage, Initialization.
	  
	  
	  population[0]=selecRandomSet(numberOfPrototypes,true).clone() ;
	  fitness[0] = accuracy(population[0],trainingDataSet); 
	  
	  // Aseguro que al menos hay un representante de cada clase.
	  PrototypeSet clases[] = new PrototypeSet [this.numberOfClass];
	  for(int i=0; i< this.numberOfClass; i++){
		  clases[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
	  }
	
	  for(int i=0; i< population[0].size(); i++){
		  for(int j=0; j< this.numberOfClass; j++){
			  if(population[0].getFromClass(j).size() ==0 && clases[j].size()!=0){
				  
				  population[0].add(clases[j].getRandom());
			  }
		  }
	  }
	  
	  for(int i=1; i< PopulationSize; i++){
		  population[i] = new PrototypeSet();
		  for(int j=0; j< population[0].size(); j++){
			  population[i].add(trainingDataSet.getFromClass(population[0].get(j).getOutput(0)).getRandom());
		  }
		  fitness[i] = accuracy(population[i],trainingDataSet);   // SADE fitness
	  }
	  
	  //We select the best initial  particle
	 double bestFitness=fitness[0];
	  int bestFitnessIndex=0;
	  for(int i=1; i< PopulationSize;i++){
		  if(fitness[i]>bestFitness){
			  bestFitness = fitness[i];
			  bestFitnessIndex=i;
		  }
		  
	  }
	  System.out.println("Best initial Fitness " + bestFitness);
	  
	   for(int j=0;j<PopulationSize;j++){
         //Now, I establish the index of each prototype.
		  for(int i=0; i<population[j].size(); ++i)
			  population[j].get(i).setIndex(i);
	   }
      
	   
	   // if self adaptative..
	   // Initialy
	   if(this.WeightScheme.equals("Adaptive")){
		   WeightFactorAdap = new double[this.PopulationSize];
		   
		   for(int i=0; i<PopulationSize; i++){
			   WeightFactorAdap[i] = RandomGenerator.Randdouble(0,1);
		   }
	   }
	  

	   
	   for(int iter=0; iter< MaxIter; iter++){ // Main loop
		   
			  

				   
			  for(int i=0; i<PopulationSize; i++){
				  
				  
				   //Second:  Mutation Operation.
				   // I want to generate a PrototypeSet Mutation for each item of the population.
				   
				   mutation[i] = new PrototypeSet(population[i].size());
			   
				   // Pasamos la poblaci—n, y la mejor actual, adem‡s del mejor del vecindario,
				   int bestNeighboor=0;
				   double bestFitnessNeighboor = Double.MIN_VALUE;
				   
				   //Ring Topology.: Cogemos los neighboors de cada lado.
				   for(int j=i-this.neighboors; j< i+this.neighboors; j++){
					   int pos = (i+j)% this.PopulationSize;
					   if (pos <0) pos *=-1;
					   
					   if(fitness[pos]> bestFitnessNeighboor){
						   bestFitnessNeighboor = fitness[pos];
						   bestNeighboor = pos;
					   }
				   }
				   
				   mutation[i] = mutant(population, i,bestFitnessIndex, bestNeighboor).clone();

				   // Third: Binomial Crossver Operation.
				   // Now, we decide if the mutation will be the trial vector.

				   //crossover[i] = new PrototypeSet(population[i]);
				   crossover[i] = new PrototypeSet();
				   
				  for(int j=0; j< population[i].size(); j++){ // For each part of the solution
					   
					
						   double randNumber = RandomGenerator.Randdouble(0, 1);
						   
						   if(randNumber<this.CrossOverRate){
							   //crossover[i].set(j, mutation[i].get(j)); // Overwrite.
							   crossover[i].add(mutation[i].get(j));
						   }else{
							   crossover[i].add(population[i].get(j));
						   }
					   
							   
				   }
				   
				   // Fourth: Selection Operation.
				   // Decide if the trial vector is better than initial population.
				   //Crossover has the trialVector, we check its fitness.
		
				   nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(population[i]);
			       fitness[i] = accuracy(nominalPopulation,trainingDataSet);
			       
			       nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(crossover[i]);
			       
				  double trialVector = accuracy(nominalPopulation,trainingDataSet);
				  
				  //System.out.println("Trial Vector fitness = "+ trialVector);
				  //System.out.println("fitness de la particula = "+ fitness[i]);
				  
				  if(trialVector > fitness[i]){
					// Y lo guardo el actual en archivo.
					  //System.out.println("Selecting");

					  population[i] = crossover[i].clone();
					  fitness[i] = trialVector;
				  }
				   
				  
				  
				  if(fitness[i]>bestFitness){
					  
					  bestFitness = fitness[i];
					  bestFitnessIndex=i;
					  System.out.println("Iter="+ iter +" Acc= "+ bestFitness);
				  }
				  
				  
			   }
			  
			  if(this.WeightScheme.equals("Linear")){
				  this.WeightFactor = iter/this.MaxIter *1.;
			  }else if(this.WeightScheme.equals("Exponential")){
				  this.WeightFactor = Math.exp(iter/this.MaxIter*1.0 * Math.log1p(2))-1;
			  }else if(this.WeightScheme.equals("Random")){
				  this.WeightFactor = RandomGenerator.Randdouble(0, 1);
			  }else if(this.WeightScheme.equals("Adaptive")){
				  
				  for(int i=0; i<this.PopulationSize; i++){
					  double prod = this.ScalingFactor*(this.WeightFactorAdap[bestFitnessIndex]-this.WeightFactorAdap[i]); 
					  
					  int ran1,ran2;
					  
					  do{
						  ran1 = RandomGenerator.Randint(0, this.PopulationSize);
						  ran2 = RandomGenerator.Randint(0, this.PopulationSize);
					  }while (ran1 != i && ran2!= ran1 && ran2!= i);
					  
					  double prod2 = this.ScalingFactor*(this.WeightFactorAdap[ran1]-this.WeightFactorAdap[ran2]); 
					  this.WeightFactorAdap[i] += prod + prod2;
					  
					  if(this.WeightFactorAdap[i] > 0.95) this.WeightFactorAdap[i] =0.95;
					  else if(this.WeightFactorAdap[i] < 0.05) this.WeightFactorAdap[i] =0.05;
				  }
			  }
			  
			//  System.out.println("Best Fitness Generaci—n "+iter+" = " + bestFitness);
			 // System.out.println("Acc= "+ bestFitness);

	   } // End main LOOP
	     
		  
		nominalPopulation = new PrototypeSet();
        nominalPopulation.formatear(population[bestFitnessIndex]);
		System.err.println("\n% de acierto en training Nominal " + KNN.classficationAccuracy(nominalPopulation,trainingDataSet,1)*100./trainingDataSet.size() );
			  
		return nominalPopulation;
  }
  
  /**
   * General main for all the prototoype generators
   * Arguments:
   * 0: Filename with the training data set to be condensed.
   * 1: Filename which contains the test data set.
   * 3: Seed of the random number generator.            Always.
   * **************************
   * 4: .Number of neighbors
   * 5:  Swarm Size
   * 6:  Particle Size
   * 7:  Max Iter
   * 8:  C1
   * 9: c2
   * 10: vmax
   * 11: wstart
   * 12: wend
   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {
      Parameters.setUse("DEGL", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      DEGLGenerator.setSeed(seed);
      
      int k = Parameters.assertExtendedArgAsInt(args,3,"number of neighbors", 1, Integer.MAX_VALUE);
      int swarm = Parameters.assertExtendedArgAsInt(args,4,"swarm size", 1, Integer.MAX_VALUE);
      int particle = Parameters.assertExtendedArgAsInt(args,5,"particle size", 1, Integer.MAX_VALUE);
      int iter = Parameters.assertExtendedArgAsInt(args,6,"max iter", 1, Integer.MAX_VALUE);
      double c1 = Parameters.assertExtendedArgAsInt(args,7,"c1", 1, Double.MAX_VALUE);
      double c2 =Parameters.assertExtendedArgAsInt(args,8,"c2", 1, Double.MAX_VALUE);
      double vmax =Parameters.assertExtendedArgAsInt(args,9,"vmax", 1, Double.MAX_VALUE);
      double wstart = Parameters.assertExtendedArgAsInt(args,10,"wstart", 1, Double.MAX_VALUE);
      double wend =Parameters.assertExtendedArgAsInt(args,11,"wend", 1, Double.MAX_VALUE);
      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      DEGLGenerator generator = new DEGLGenerator(training, k,swarm,particle,iter, 0.5,0.5,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
