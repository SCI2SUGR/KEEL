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
	SADE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.SADE;

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
public class SADEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private double ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate[];
  private int LearningPeriod;
  private int Strategy;
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfStrategies; // number of strategies in the pool
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new SADEGenerator Algorithm
   * 
   */
  
  public SADEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones)
  {
      super(_trainingDataSet);
      algorithmName="SADE";
      
      this.k = neigbors;
      this.PopulationSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      

      
  }
  


  /**
   * Build a new SADEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public SADEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="SADE";
      this.k =  parameters.getNextAsInt();
      this.PopulationSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsDouble();
      this.MaxIter =  parameters.getNextAsInt();
      this.LearningPeriod = parameters.getNextAsInt();
      this.numberOfStrategies = parameters.getNextAsInt();
      
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      System.out.print("\nIsaac dice:  " + k + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" LP=  "+this.LearningPeriod+ "\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  
  
  /**
   * I modified the order of the list of strategies,  i need it because i want to do the Same POOL like the paper.
   * Original POOL = DE/Rand/1/bin. | DE/rand-to-best/2/bin | De/Rand/2/bin | DE/current-to-rand/1/arithmetic
   * @param population
   * @param actual
   * @param mejor
   */
  public PrototypeSet mutant(PrototypeSet population[], int actual, int mejor){
	  
	  
	  PrototypeSet mutant = new PrototypeSet(population.length);
	  PrototypeSet r1,r2,r3,r4,r5, resta, producto, resta2, producto2, result, producto3, resta3;
	  
	//We need three differents solutions of actual
		   
	  int lista[] = new int[population.length];
      inic_vector_sin(lista,actual);
      desordenar_vector_sin(lista);
		      
	  // System.out.println("Lista = "+lista[0]+","+ lista[1]+","+lista[2]);
	  
	   r1 = population[lista[0]];
	   r2 = population[lista[1]];
	   r3 = population[lista[2]];
	   r4 = population[lista[3]];
	   r5 = population[lista[4]];
		   
			switch(this.Strategy){
		   	   case 1: // ViG = Xr1,G + F(Xr2,G - Xr3,G) De rand 1
		   		 resta = r2.restar(r3);
		   		 producto = resta.mulEscalar(this.ScalingFactor);
		   		 mutant = producto.sumar(r1);
		   	    break;
			   
		   	   case 2:  //DE rand 2
		   		   resta = r2.restar(r3); 
		   		   resta2 = r4.restar(r5);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = r1.sumar(producto);
			   	   mutant = result.sumar(producto2);
			   break;
			   
			   
		   	   case 3: 		//DE rand to best 2
		   		   resta = r1.restar(r2); 
		   		   resta2 = r3.restar(r4);
		   		   resta3 = population[mejor].restar(population[actual]);
		   		   
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   	   producto3 = resta3.mulEscalar(this.ScalingFactor);
			   	   
			   	   result = population[actual].sumar(producto);
			   	   result = result.sumar(producto2);
			   	   mutant = result.sumar(producto3);
			   	   

			   		 			   		 
			   break;
			   
		   	   case 4: //DE current to rand 1
		   		   resta = r1.restar(population[actual]); 
		   		   resta2 = r2.restar(r3);
		   		 		   		 
			   	   producto = resta.mulEscalar(RandomGenerator.Randdouble(0, 1));
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[actual].sumar(producto);
			   	   mutant = result.sumar(producto2);
			   	   
			   break;
			  
			
		   		 
		   	   case 5:
		   		// Vig = Xbest,G + F(Xr2,G - Xr3,G)  De best 1
			   		 resta = r2.restar(r3);
			   		 producto = resta.mulEscalar(this.ScalingFactor);
			   		 mutant = population[mejor].sumar(producto);
  		       break;
  		       
		   	   case 6: 
		   		   // Vig = ... De rand to best 1
		   		   resta = r1.restar(r2); 
		   		   resta2 = population[mejor].restar(population[actual]);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[actual].sumar(producto);
			   	   mutant = result.sumar(producto2);
  		       break;
  		       
		   	   case 7: // DE best 2
		   		   resta = r1.restar(r2); 
		   		   resta2 = r3.restar(r4);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[mejor].sumar(producto);
			   	   mutant = result.sumar(producto2);
  		       break;
  		       
		   }   
	   

	 // System.out.println("********Mutante**********");
	 // mutant.print();
	   
     mutant.applyThresholds();
     //System.out.println("********Mutante Reformado**********");
	// mutant.print();
	  return mutant;
  }
  
  
  public double Skg (int strategy, int successRate[][], int failureRate[][]){
	  double numerator=0, denominator=0;
	  
	  for(int k=0; k<this.LearningPeriod; k++){
		  numerator += successRate[k][strategy];
		  denominator += successRate[k][strategy] + failureRate[k][strategy];
	  }
	  
	  double SKG = numerator/denominator + 0.01; // Add epsilon 0.01
	  
	  return SKG;
  }
  
  /*
   * The probability of use the K strategy is:  pk,G = SkG / SUM(Sk,g).
   */
  
  public double updateProbability(int strategy,int successRate[][],int failureRate[][]){
	  
	  double numerator=0, denominator =0;
	  
	  numerator =Skg(strategy,successRate, failureRate);
	  
	  for(int i =0; i< this.numberOfStrategies;i++){
		  denominator+=Skg(i,successRate, failureRate);
	  }
	  
	  
	  return (numerator/denominator);
  }
  
  /**
   * I use this function to calculate what strategy we must use with the probability strategy we have.
   * @param ProbabilityStrategy
   *
   */
  public int selectStrategy(double ProbabilityStrategy[]){
	  
	  double random=RandomGenerator.Randdouble(0, 1);
	  //System.out.println("Random = "+random);
	  double aux =0;
	  boolean end = false;
	  int selected=1;
	  
	  for(int i=0; i< this.numberOfStrategies && !end; i++){
		  aux += ProbabilityStrategy[i];
		  
		 if(random <= aux){
			 selected = i+1; // because we count 1..to 7 strategy.
			 end  =true;
		 }
	  }
	  
	  return selected;
  }
  
  /**
   * Generate a reduced prototype set by the SADEGenerator method.
   * @return Reduced set by SADEGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  SADE is starting...\n Computing...\n");
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  if(numberOfPrototypes < trainingDataSet.getPosibleValuesOfOutput().size()){
		  System.out.println("Number of prototypes less than the number of clases");
		  numberOfPrototypes = trainingDataSet.getPosibleValuesOfOutput().size();
	  }
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	//  trainingDataSet.print();
	  //Algorithm
	  // First, we create the population, with PopulationSize.
	  // like a prototypeSet's vector.  
	  PrototypeSet nominalPopulation;
	  PrototypeSet population [] = new PrototypeSet [PopulationSize];
	  PrototypeSet mutation[] = new PrototypeSet[PopulationSize];
	  PrototypeSet crossover[] = new PrototypeSet[PopulationSize];
	  
	  
	  // Variables necesarias para hacer el algoritmo adaptativo.
	  int kStrategies = this.numberOfStrategies;
	
	  double ProbabilityStrategy[] = new double[kStrategies];
	  double CRmk[] = new double[kStrategies];
	  double CRmemory[][] = new double[this.LearningPeriod][];
	  double CRm=0.5;
	  
	  int successRate[][] = new int[this.LearningPeriod][];
	  int failureRate[][] = new int[this.LearningPeriod][];
	  
	  for(int i=0; i<this.LearningPeriod; i++){
		  successRate[i] = new int[kStrategies];
		  failureRate[i] = new int[kStrategies];
		  CRmemory[i] = new double[kStrategies];
		  for(int j=0; j<kStrategies; j++){
			  successRate[i][j] = 1;
			  failureRate[i][j] = 1;
			  CRmemory[i][j] = 0.5;
		  }
	  }
	  
	  /* In SaDE algorithm, the initial probability of use each strategy
	   * is 1/kStrategies.
	   */
	  
	  for(int i=0; i<ProbabilityStrategy.length; i++){
		  ProbabilityStrategy[i] = 1./kStrategies;
		  CRmk[i] = 0.5; // Initial 0.5
	  }
	  
	  //each Strategy has different  CR.	  
	  this.CrossOverRate= new double[this.numberOfStrategies];
	  double F[] = new double[this.PopulationSize];
	  
	  double fitness[] = new double[PopulationSize];
	  double fitness_bestPopulation[] = new double[PopulationSize];
	  PrototypeSet bestParticle = new PrototypeSet();
	  
	  
  
	  //Each particle must have   Particle Size %

	  // First Stage, Initialization. Todas las particulas "IGUALES" structura
	  
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
	  
	   for(int j=0;j<PopulationSize;j++){
         //Now, I establish the index of each prototype.
		  for(int i=0; i<population[j].size(); ++i)
			  population[j].get(i).setIndex(i);
	   }
      
	 
	   //Step 3:
	   for(int iter=1; iter<= MaxIter; iter++){ // Main loop
		   
	  		  
			   //Step 3.1
			  if(iter%this.LearningPeriod == 0){  // if G > LP
				  
				  for(int k=0; k<kStrategies; k++){
						  
					  // Update Pk,G ->  PjIter
					  ProbabilityStrategy[k]= updateProbability(k, successRate, failureRate);

				//	  System.out.println("Prob Strategy j= "+k+ " -> "+ ProbabilityStrategy[k]);
					  // Remove the first element of Success and failure memory.
					  successRate[iter%this.LearningPeriod][k]= 0;
					  failureRate[iter%this.LearningPeriod][k] =0;
				  }
				 // System.out.println("********");
				  
			  } 
			  
			  /* Step 3.2.*/
			// Assign trial vector generation strategy: Lo eligo a posteriori antes de cada llamada a mutaci—n.
			  //System.out.println("Aplicamos las estrategia = "+ this.Strategy);
			  
			  // assign control parameter F . N(0.5,0.3)
			          // if Z sigue una distrubuci—n 0,1, X = sigma*Z + mu sigue la dis n(mu,sigma) 
			  for(int i=0; i< this.PopulationSize; i++){
				  F[i]= RandomGenerator.RandGaussian()*0.3 + 0.5;
			  }
			 // System.out.println("Scaling Factor = "+ this.ScalingFactor); // Correcto
			  
			  
			  /* Assign control parameter CR */
			  if(iter%this.LearningPeriod == 0){
				 
				  for(int k=0; k< this.numberOfStrategies; k++){
					  CRmk[k] = 0 ;
					  
					  for(int m=0; m< this.LearningPeriod; m++){
						  CRmk[k] += CRmemory[m][k];
					  }
					  
					  CRmk[k] /= this.LearningPeriod;
				  }
				  
			  }
			  
			  for (int k=0; k< this.numberOfStrategies; k++){
				 this.CrossOverRate[k] =   RandomGenerator.RandGaussian()*0.1 + CRmk[k];
				 
				 while( this.CrossOverRate[k] < 0 || this.CrossOverRate[k]>1){
					 this.CrossOverRate[k] =   RandomGenerator.RandGaussian()*0.1 + CRmk[k];
				 }
			  }
			  
			  //System.out.println("CR k= " + this.CrossOverRate[this.Strategy-1]);
			  /* Step 3.3*/
			  
			  for(int i=0; i<PopulationSize; i++){
				  // Assign trial vector generation strategy
				  this.Strategy = selectStrategy(ProbabilityStrategy); // cada estrategia se elige antes de cada 
				  
				   //Second:  Mutation Operation.
				   // I want to generate a PrototypeSet Mutation for each item of the population.
				   this.ScalingFactor = F[i];
				   
				   mutation[i] = new PrototypeSet(population[i].size());
			   
				   // Pasamos la poblaci—n, y la mejor actual, por si se usa /best/
				   mutation[i] = mutant(population, i,bestFitnessIndex).clone();
	
				   // Third: Crossver Operation.
				   // Now, we decide if the mutation will be the trial vector.
				   
				   crossover[i] = new PrototypeSet(population[i]);
				   
				   if(this.Strategy !=4){
					   
						  for(int j=0; j< population[i].size(); j++){ // For each part of the solution
							   
							
							   double randNumber = RandomGenerator.Randdouble(0, 1);
							   
							   if(randNumber<this.CrossOverRate[this.Strategy-1]){
								   crossover[i] = mutation[i].clone(); // Overwrite.
							   }
							   
									   
						   }

				   }else{ // if we use currento-rand, "there is no crossover, It's ready in mutation operator"
					   crossover[i] = mutation[i].clone(); // Overwrite.
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
				  
				  //double trialVector = accuracy(crossover[i],trainingDataSet);
				  //fitness[i] = accuracy(population[i],trainingDataSet);
				  
				  //System.out.println("Trial Vector fitness = "+ trialVector);
				  //System.out.println("fitness de la particula = "+ fitness[i]);
				  
				  if(trialVector > fitness[i]){
					//  System.out.println("Selecting");
					  // Actualizo el SuccesRate de esta strategia
					  successRate[iter%this.LearningPeriod][this.Strategy-1]++;
					  CRmemory[iter%this.LearningPeriod][this.Strategy-1] = this.CrossOverRate[this.Strategy-1];
					  population[i] = crossover[i].clone();
					  fitness[i] = trialVector;
				  }else{
					  // Actualizo el FailureRate de esta estrategia.
					  failureRate[iter%this.LearningPeriod][this.Strategy-1]++;
				  }
				   
				  //fitness[i] = accuracy(population[i],trainingDataSet);
				  
				  if(fitness[i]>bestFitness){
					  bestFitness = fitness[i];
					  bestFitnessIndex=i;
					  System.out.println("Iter="+ iter +" Acc= "+ bestFitness);
				  }
				  
				  
		   }


		   
	   }

	    
	  nominalPopulation = new PrototypeSet();
      nominalPopulation.formatear(population[bestFitnessIndex]);
	  System.err.println("\n% de acierto en training Nominal " + KNN.classficationAccuracy(nominalPopulation,trainingDataSet,1)*100./trainingDataSet.size() );
	   //nominalPopulation.print();

  
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
      Parameters.setUse("SADE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      SADEGenerator.setSeed(seed);
      
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
      
      
      SADEGenerator generator = new SADEGenerator(training, k,swarm,particle,iter);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
