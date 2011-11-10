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
	JADE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.JADE;

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
public class JADEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate[];
  private int Strategy;
  private String CrossoverType; // Binomial, Exponential, Arithmetic
  //private boolean Archive; // JAde with or without Archive.
  private double p; // to select the number of pbest
  private double c;
  protected int numberOfClass; 
  protected int numberOfbetters; // numero de mejores atener en cuenta
  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new JADEGenerator Algorithm
   *
   */
  
  public JADEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="JADE";
      
      this.k = neigbors;
      this.PopulationSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      
      this.ScalingFactor = F;
      
      this.Strategy = strg;
      
  }
  


  /**
   * Build a new JADEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public JADEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="JADE";
      this.k =  parameters.getNextAsInt();
      this.PopulationSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.p = parameters.getNextAsDouble();
      this.c =parameters.getNextAsDouble();
       
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      this.numberOfbetters= (int) (this.p*PopulationSize);
      if( numberOfbetters <1) numberOfbetters  = 1;
      
      System.out.println("Numero de p-best = "+ this.numberOfbetters);
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      System.out.print("\nIsaac dice:  " + k + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" CR=  "+this.CrossOverRate+ "\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  
  
  public int[] mejoresParticulas(PrototypeSet population[],double fitness[]){
	int number = this.numberOfbetters;
	  int index[] = new int[number];
	  int ind= 0;
	  double mejor = Double.MIN_VALUE;
	  double acc;
	  
	  for(int i=0; i< population.length; i++){
		  acc =fitness[i]; //accuracy(population[i],trainingDataSet); 
		  
		  if(acc > mejor )
		  {
			ind = i;
			mejor = acc;
		  }	  
	  }
	  index[0] = ind;
	  
	  for (int j=1; j<number; j++){
		  mejor = Double.MIN_VALUE;
		  for(int i=0; i< population.length; i++){
			  acc = fitness[i];//accuracy(population[i],trainingDataSet); 
			  
			  //if(acc > mejor && acc < accuracy(population[index[j-1]],trainingDataSet))
			  if(acc > mejor && acc < fitness[index[j-1]])
			  {
				ind = i;
				mejor = acc;
			  }	  
		  }
		  index[j] =  ind;
	  
	  }
	  return index;
  }
  
  public PrototypeSet mutant(PrototypeSet population[], double fitness[], int actual,  PrototypeSet Archivo[],int utilArchivo){
	  
	  
	  PrototypeSet mutant = new PrototypeSet(population.length);
	  PrototypeSet r1,r2,xbest, resta, producto, resta2, producto2, result;
	  
		   
	  // r1 different to actual	      
	  int ran;
	   do{
		   ran =  RandomGenerator.Randint(0, population.length);   
	   }while(ran == actual);
	   
	   r1 = population[ran];
	   
	  int number;
	  
	  do{ 
		  number = RandomGenerator.Randint(0, population.length+ utilArchivo);
	  }while (number==ran || number == actual );
	  
	   if(number < population.length){
		 r2 = population[number];  
	   }else
		 r2 = Archivo[number-population.length];
	   
	   // Tengo que sacar los 100p % mejores de la poblaci—n actual.
	  // System.out.println("Numero de p-best = "+ num_mejores);
	  
	   
	   int indices[] = new int [this.numberOfbetters];
	   
	   indices = mejoresParticulas(population,fitness);
	   number = RandomGenerator.Randint(0, indices.length);
	   
	   xbest = population[indices[number]];

	   /*
	   for(int i=0; i< population.length; i++){
		   System.out.println(accuracy(population[i],trainingDataSet));
	   }
	   for( int i=0; i< num_mejores ; i++){
		   System.out.println(indices[i]);
	   }
	   */
			switch(this.Strategy){
		   	   case 1: 
		   		   resta = xbest.restar(population[actual]);
		   		   resta2 = r1.restar(r2);
		   		   
		   		   producto = resta.mulEscalar(this.ScalingFactor);
		   		   producto2 = resta2.mulEscalar(this.ScalingFactor);
		   		   
		   		   result = producto.sumar(producto2);
		   		   mutant = population[actual].sumar(result);
		   	    break;
			   		   	
		   }   
	   

	  // System.out.println("********Mutante**********");
	 // mutant.print();
	   
     mutant.applyThresholds();
	
	  return mutant;
  }
  /**
   * Generate a reduced prototype set by the JADEGenerator method.
   * @return Reduced set by JADEGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  JADE is starting...\n Computing...\n");
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  if(numberOfPrototypes < trainingDataSet.getPosibleValuesOfOutput().size()){
		  System.out.println("Number of prototypes less than the number of clases");
		  numberOfPrototypes = trainingDataSet.getPosibleValuesOfOutput().size();
	  }
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	  
	//  System.out.println("training Size->" +trainingDataSet.size());
	  
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
	  
	  double meanCR = 0.5;
	  double meanF = 0.5;
	  PrototypeSet Archivo[] = new PrototypeSet[this.PopulationSize];
	  int utilArchivo = 0;
	  
	  // we save the differents successful F and CR.
	  double SF[] = new double[this.PopulationSize];
	  double SCR[] = new double[this.PopulationSize];
	  
	  this.CrossOverRate= new double[this.PopulationSize];
	  double F[] = new double[this.PopulationSize];
	  

	  //Each particle must have   Particle Size %

	  // First Stage, Initialization.
	  
	  population[0]=selecRandomSet(numberOfPrototypes,true).clone() ;
	  
	  
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
	  
	  //population[0].print();
	  
	  fitness[0] = accuracy(population[0],trainingDataSet); 
	  
	 // population[0].print();
	  for(int i=1; i< PopulationSize; i++){
		  population[i] = new PrototypeSet();
		  for(int j=0; j< population[0].size(); j++){
			  population[i].add(trainingDataSet.getFromClass(population[0].get(j).getOutput(0)).getRandom());
		  }
		  fitness[i] = accuracy(population[i],trainingDataSet);   // DE fitness, no hace falta formatear porque son aleatorios!
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
	  
	   for(int j=0;j<PopulationSize;j++){
         //Now, I establish the index of each prototype.
		  for(int i=0; i<population[j].size(); ++i)
			  population[j].get(i).setIndex(i);
	   }
      

	   System.out.println("Initial Fitness=  "+bestFitness);
	this.Strategy = 1;
	   
	   for(int iter=0; iter< MaxIter; iter++){ // Main loop
		   
			  int utilF = 0;
			  int utilCR = 0;
			  
			  // If we are going to use exponential, I calculate the index of possible selecting  Mutation.
 
				   
			  for(int i=0; i<PopulationSize; i++){
				  
				  //Generate CRi
				  this.CrossOverRate[i] = RandomGenerator.RandGaussian()*0.1 + meanCR;
				  //Normalize
				  if(this.CrossOverRate[i]>1) this.CrossOverRate[i] =1;
				  if(this.CrossOverRate[i]<0) this.CrossOverRate[i] =0;
				  
				  //Generate Fi
				  double uniforme;
				  do{
					  uniforme= RandomGenerator.Randdouble(0,1);
					  F[i]  = 0.1*Math.tan(3.14161*uniforme) + meanF;
				  }while (F[i] <=0);
				  
				  if(F[i] >1 ) F[i] = 1;
				  
				  this.ScalingFactor = F[i];
				  //System.out.println("Fi = " + F[i]);
				  //System.out.println("CRi = "+ this.CrossOverRate[i]);
				  // Randomly choose xbestp, as one of the 100p% best Vector
				  
				   //Second:  Mutation Operation.
				   // I want to generate a PrototypeSet Mutation for each item of the population.
				   
				   mutation[i] = new PrototypeSet(population[i].clone());
			   
				   // Pasamos la poblaci—n, y la mejor actual, por si se usa /best/
				   mutation[i] = mutant(population,fitness, i, Archivo, utilArchivo).clone();

				   // Third: Crossver Operation.
				   // Now, we decide if the mutation will be the trial vector.

				   crossover[i] = new PrototypeSet(population[i].clone());
				   
				  for(int j=0; j< population[i].size(); j++){ // For each part of the solution
					   
					
						   double randNumber = RandomGenerator.Randdouble(0, 1);
						   
						   if(randNumber<this.CrossOverRate[i]){
							   crossover[i].set(j, mutation[i].get(j)); // Overwrite.
						   }
					   
							   
				   }
				   
				   // Fourth: Selection Operation.
				   // Decide if the trial vector is better than initial population.
				   //Crossover has the trialVector, we check its fitness.
				   
				  // crossover[i].applyThresholds();
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
					  Archivo[utilArchivo%PopulationSize] = new PrototypeSet(population[i].clone());
					  utilArchivo++;
					  SCR[utilCR%PopulationSize] = this.CrossOverRate[i];
					  utilCR++;
					  SF[utilF%PopulationSize] = F[i];
					  utilF++;
					  population[i] = new PrototypeSet(crossover[i].clone());
					  fitness[i]  = trialVector;
					  utilArchivo = utilArchivo%PopulationSize;
				  }
				   
				  
				  
				  if(fitness[i]>bestFitness){
					  bestFitness = fitness[i];
					  bestFitnessIndex=i;
					  System.out.println("Iter="+ iter +" Acc= "+ bestFitness);
					 // System.out.println("Best Fitness index= "+bestFitnessIndex);
				  }
				  
				  
			   }
			//  System.out.println("Best Fitness Generaci—n "+iter+" = " + bestFitness);
			  // Now we remove solutions from A.
			  
			  if(utilArchivo > this.PopulationSize){
				  utilArchivo = this.PopulationSize;				  
			  }

			  double meanA= 0;
			  double meanL =0;
			  double numerator=0, denominator =0;
			  
			  for(int i=0; i< utilCR; i++){
				  meanA+= SCR[i];
				  numerator += SF[i] * SF[i];
				  denominator += SF[i];
			  }
			  meanL = numerator/denominator;
			  
			  meanCR =(1-c)*meanCR + c * meanA;
			  meanF = (1-c)*meanF + c * meanL;
			  

			 // System.out.println("Acc= "+ bestFitness);
	   } // End main LOOP
	     
	   
  
	//   System.out.println("training Size" +trainingDataSet.size());
	  // System.out.println("Best Fitness "+bestFitness);
		   nominalPopulation = new PrototypeSet();
           nominalPopulation.formatear(population[bestFitnessIndex]);
           
  //         System.out.println("Best Fitness2 "+  accuracy(nominalPopulation,trainingDataSet));
//			 System.err.println("\n% de acierto en training Nominal " + KNN.classficationAccuracy(nominalPopulation,trainingDataSet,1)*100./trainingDataSet.size() );
				  
			//  nominalPopulation.print();

  
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
      Parameters.setUse("JADE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      JADEGenerator.setSeed(seed);
      
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
      
      
      JADEGenerator generator = new JADEGenerator(training, k,swarm,particle,iter, 0.5,0.5,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
