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
	DE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.DE;

import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Chen.ChenGenerator;
import keel.Algorithms.Instance_Generation.HYB.HYBGenerator;
import keel.Algorithms.Instance_Generation.PSO.PSOGenerator;
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
public class DEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate;
  private int Strategy;
  private String CrossoverType; // Binomial, Exponential, Arithmetic
  
  protected int numberOfClass;
  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new DEGenerator Algorithm
   * 
   */
  
  public DEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="DE";
      
      this.k = neigbors;
      this.PopulationSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      
      this.ScalingFactor = F;
      this.CrossOverRate = CR;
      this.Strategy = strg;
      
  }
  


  /**
   * Build a new DEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public DEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="DE";
      this.k =  parameters.getNextAsInt();
      this.PopulationSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.ScalingFactor = parameters.getNextAsDouble();
      this.CrossOverRate = parameters.getNextAsDouble();
      this.Strategy =  parameters.getNextAsInt();
      this.CrossoverType = parameters.getNextAsString();
      
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      System.out.print("\nIsaac dice:  " + k + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" CR=  "+this.CrossOverRate+ " CrossverType = "+ this.CrossoverType+"\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  
  
  
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
		   		 mutant = producto.sumar(resta); //r1
		   	    break;
			   
		   	   case 2: // Vig = Xbest,G + F(Xr2,G - Xr3,G)  De best 1
			   		 resta = r2.restar(r3);
			   		 producto = resta.mulEscalar(this.ScalingFactor);
			   		 mutant = population[mejor].sumar(producto);
			   break;
			   
		   	   case 3: // Vig = ... De rand to best 1
		   		   resta = r1.restar(r2); 
		   		   resta2 = population[mejor].restar(population[actual]);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[actual].sumar(producto);
			   	   mutant = result.sumar(producto2);
			   		 			   		 
			   break;
			   
		   	   case 4: // DE best 2
		   		   resta = r1.restar(r2); 
		   		   resta2 = r3.restar(r4);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[mejor].sumar(producto);
			   	   mutant = result.sumar(producto2);
			   break;
			  
		   	   case 5: //DE rand 2
		   		   resta = r2.restar(r3); 
		   		   resta2 = r4.restar(r5);
		   		 			   		 
			   	   producto = resta.mulEscalar(this.ScalingFactor);
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = r1.sumar(producto);
			   	   mutant = result.sumar(producto2);
			   	   
  		       break;
  		       
		   	   case 6: //DE rand to best 2
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
  		       
		   	  /*// Para hacer esta estratŽgia, lo que hay que elegir es CrossoverType = Arithmetic
		   	   * case 7: //DE current to rand 1
		   		   resta = r1.restar(population[actual]); 
		   		   resta2 = r2.restar(r3);
		   		 		   		 
			   	   producto = resta.mulEscalar(RandomGenerator.Randdouble(0, 1));
			   	   producto2 = resta2.mulEscalar(this.ScalingFactor);
			   		
			   	   result = population[actual].sumar(producto);
			   	   mutant = result.sumar(producto2);
			   	   
  		       break;
  		       */
		   }   
	   

	  // System.out.println("********Mutante**********");
	 // mutant.print();
	   
     mutant.applyThresholds();
	
	  return mutant;
  }
  /**
   * Generate a reduced prototype set by the DEGenerator method.
   * @return Reduced set by DEGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  DE is starting...\n Computing...\n");
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  if(numberOfPrototypes < trainingDataSet.getPosibleValuesOfOutput().size()){
		  System.out.println("Number of prototypes less than the number of clases");
		  numberOfPrototypes = trainingDataSet.getPosibleValuesOfOutput().size();
	  }
	  
	 
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
      
	   boolean cruceExp [] = new boolean[PopulationSize];
	   
	   
	   for(int iter=0; iter< MaxIter; iter++){ // Main loop
		   
		   
		   // If we are going to use exponential, I calculate the index of possible selecting  Mutation.

		    if(this.CrossoverType.equals("Exponential")){
		     
		       for(int i=0; i<PopulationSize; i++){
					   cruceExp[i] = false;
			   }
		    		    		   
		    }   
			   
			   

		   for(int i=0; i<PopulationSize; i++){
			   //Second:  Mutation Operation.
			   // I want to generate a PrototypeSet Mutation for each item of the population.
			   
			   mutation[i] = new PrototypeSet(population[i].size());
		   
			   // Pasamos la poblaci—n, y la mejor actual, por si se usa /best/
			   mutation[i] = mutant(population, i,bestFitnessIndex).clone();

			   // Third: Crossver Operation.
			   // Now, we decide if the mutation will be the trial vector.
			   // Three Types of Crossover Operations, BINOMIAL, EXPONENTIAL ARITHMETIC
			   //crossover[i] = new PrototypeSet(population[i]);
			   crossover[i] = new PrototypeSet(population[i]);
			   
			   for(int j=0; j< population[i].size(); j++){ // For each part of the solution
				   
				   if(this.CrossoverType.equals("Binomial")){
					   double randNumber = RandomGenerator.Randdouble(0, 1);
					   
					   if(randNumber<this.CrossOverRate){
						   crossover[i].set(j, mutation[i].get(j)); // Overwrite.
					   }
				   
				   }else if(this.CrossoverType.equals("Exponential")){
					  
					   int startingPoint = RandomGenerator.Randint(0, PopulationSize);
					   
					   int L=0;
					   do{
						   L++;			   
					   }while(RandomGenerator.Randdouble(0, 1)<this.CrossOverRate && (L< population[i].size()));
				    
					   for(int m=startingPoint; m< startingPoint+L; m++){
						   crossover[i].set(m%population[i].size(), mutation[i].get(j)); // Overwrite
					   }
					   
		
					   
				   }else if(this.CrossoverType.equals("Arithmetic")){ // Uig = XiG + K*(Vi - Xi)
					   PrototypeSet resta = mutation[i].restar(population[i]);
					   crossover[i] = population[i].sumar(resta.mulEscalar(RandomGenerator.Randdouble(0, 1)));
				   }else{
					   System.err.println("ERROR, Crossover Type incorrect.");
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
			
			  
			  //double trialVector = accuracy(crossover[i],trainingDataSet);
			  //fitness[i] = accuracy(population[i],trainingDataSet);
			  
			  //System.out.println("Trial Vector fitness = "+ trialVector);
			  //System.out.println("fitness de la particula = "+ fitness[i]);
			  
			  if(trialVector > fitness[i]){
				//  System.out.println("Selecting");
				  population[i] = new PrototypeSet(crossover[i]);
				  fitness[i] = trialVector;
			  }
			   
			 // fitness[i] = accuracy(population[i],trainingDataSet); // NO HACE FALTA EVALUAR TANTAS VECES :D
			  
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
      Parameters.setUse("DE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      DEGenerator.setSeed(seed);
      
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
      
      
      DEGenerator generator = new DEGenerator(training, k,swarm,particle,iter, 0.5,0.5,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
