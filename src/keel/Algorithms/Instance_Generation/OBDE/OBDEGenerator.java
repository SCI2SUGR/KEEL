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
	OBDE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.OBDE;

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
public class OBDEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate;
  private double JumpingRate;
  private int Strategy;
  private String CrossoverType; // Binomial, Exponential, Arithmetic
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new OBDEGenerator Algorithm
   */
  
  public OBDEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, double JR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="OBDE";
      
      this.k = neigbors;
      this.PopulationSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      
      this.ScalingFactor = F;
      this.CrossOverRate = CR;
      this.JumpingRate = JR;
      this.Strategy = strg;
      
  }
  


  /**
   * Build a new OBDEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public OBDEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="OBDE";
      this.k =  parameters.getNextAsInt();
      this.PopulationSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.ScalingFactor = parameters.getNextAsDouble();
      this.CrossOverRate = parameters.getNextAsDouble();
      this.JumpingRate = parameters.getNextAsDouble();
      this.Strategy =  parameters.getNextAsInt();
      this.CrossoverType = parameters.getNextAsString();
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      
      System.out.print("\nIsaac dice:  JR= " + this.JumpingRate + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" CR=  "+this.CrossOverRate+ "\n");
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
		   		 mutant = producto.sumar(resta);
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
  
  public int[] mejoresParticulas(PrototypeSet population[]){
		int number = this.PopulationSize;
		  int index[] = new int[number];
		  int ind= 0;
		  double mejor = Double.MIN_VALUE;
		  double acc;
		  
		  for(int i=0; i< population.length; i++){
			  acc =accuracy(population[i],trainingDataSet); 
			  
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
				  acc =accuracy(population[i],trainingDataSet); 
				  
				  if(acc > mejor && acc < accuracy(population[index[j-1]],trainingDataSet))
				  {
					ind = i;
					mejor = acc;
				  }	  
			  }
			  index[j] =  ind;
		  
		  }
		  return index;
	  }
	  
  
  
  /**
   * Generate a reduced prototype set by the DEGenerator method.
   * @return Reduced set by DEGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  DE is starting...\n Computing...\n");
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  
	  if(numberOfPrototypes < 6){
		  System.out.println("Number of prototypes less than 6, we increse to 6");
		  numberOfPrototypes = 6;
	  }
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");

	  //Algorithm
	  // First, we create the population, with PopulationSize.
	  // like a prototypeSet's vector.  
	  
	  PrototypeSet population [] = new PrototypeSet [PopulationSize];
	  PrototypeSet populationAux [] = new PrototypeSet [PopulationSize];
	  PrototypeSet mutation[] = new PrototypeSet[PopulationSize];
	  PrototypeSet crossover[] = new PrototypeSet[PopulationSize];
	  
	  
	  double fitness[] = new double[PopulationSize];
	  double fitness_bestPopulation[] = new double[PopulationSize];
	  PrototypeSet bestParticle = new PrototypeSet();
	  
	  
  
	  //Each particle must have   Particle Size %

	  // First Stage, Initialization.
	  
	  populationAux[0]=selecRandomSet(numberOfPrototypes,true).clone() ;
	  fitness[0] = accuracy(populationAux[0],trainingDataSet); 
	  
	  for(int i=1; i< PopulationSize; i++){
		  populationAux[i] = new PrototypeSet();
		  for(int j=0; j< populationAux[0].size(); j++){
			  populationAux[i].add(trainingDataSet.getFromClass(populationAux[0].get(j).getOutput(0)).getRandom());
		  }
		  fitness[i] = accuracy(populationAux[i],trainingDataSet);   // SADE fitness
	  }
	  
	  /*
	  for(int i=0; i< PopulationSize; i++){
		  populationAux[i]=selecRandomSet(numberOfPrototypes,true).clone() ;
		  fitness[i] = accuracy(populationAux[i],trainingDataSet);   // DE fitness
      }
	  */
	  
	  // Then I generate the Opposite Population.
	  PrototypeSet opposite [] = new PrototypeSet [PopulationSize];
	  double valor;
	  
	  for(int i=0; i<populationAux.length; i++){
		  
		  opposite[i] = populationAux[i].clone();
		  
		  for(int j=0; j<opposite[i].size(); j++){
			  
			  for(int k=0; k<opposite[i].get(j).numberOfInputs(); k++){
				  valor = 1-opposite[i].get(j).getInput(k);
				  opposite[i].get(j).setInput(k, valor);  
			  }
			  opposite[i].applyThresholds();
			  Prototype Nearest = KNN._1nn(opposite[i].get(j),trainingDataSet);
			 
			  opposite[i].get(j).setClass(Nearest.getOutput(0));
		  }
	  }
	  
	//  population[0].print();
	//opposite[0].print();
	
	  PrototypeSet PopulationOpposite [] = new PrototypeSet [PopulationSize*2];
	  
	  for(int i=0; i< population.length; i++){
		  PopulationOpposite[i] = populationAux[i].clone();
		  PopulationOpposite[i+populationAux.length] = opposite[i].clone();
	  }
	  
	int indices[]= mejoresParticulas(PopulationOpposite);
	
	 for(int i=0; i<indices.length; i++){
		//  System.out.println(indices[i]);
		 population[i] = PopulationOpposite[indices[i]].clone();
		/* if(indices[i]>PopulationSize){
			 System.out.println("Cogido del Opuesto");
		 }*/
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
			  double trialVector = accuracy(crossover[i],trainingDataSet);
			  fitness[i] = accuracy(population[i],trainingDataSet);
			  
			  //System.out.println("Trial Vector fitness = "+ trialVector);
			  //System.out.println("fitness de la particula = "+ fitness[i]);
			  
			  if(trialVector > fitness[i]){
				//  System.out.println("Selecting");
				  population[i] = crossover[i].clone();
			  }
			   
			  fitness[i] = accuracy(population[i],trainingDataSet);
			  
			  if(fitness[i]>bestFitness){
				  bestFitness = fitness[i];
				  bestFitnessIndex=i;
			  }
			  
			  
		   }
		   
		   double randNumber = RandomGenerator.Randdouble(0, 1);
		   
		   if(randNumber < this.JumpingRate){
			   populationAux  = new PrototypeSet [PopulationSize];
			   populationAux = population.clone();
			
			   

				  //CALCULAR EL MêNIMO Y EL MçXIMO DE ESTE VALOR EN EL RESTO DE LA POBLACIîN.
				  double min[][] = new double[population[0].size()][opposite[0].get(0).numberOfInputs()];
				  double max[][] = new double[population[0].size()][opposite[0].get(0).numberOfInputs()];
				  for(int i=0; i< population[0].size(); i++){
					  for(int j=0; j<opposite[0].get(0).numberOfInputs(); j++ ){
						  min[i][j] = 1;
						  max[i][j] = 0;
					  }
				  }
				  
				  for(int i=0; i< population.length; i++){
					  for(int j=0; j< population[i].size(); j++){
						  for(int k=0; k<population[i].get(j).numberOfInputs(); k++){
							  if(population[i].get(j).getInput(k)<min[j][k])
							  {
								  min[j][k] = population[i].get(j).getInput(k);
							  }
							  if(population[i].get(j).getInput(k)>max[j][k])
							  {
								  max[j][k] = population[i].get(j).getInput(k);
							  }
						  }
					  }
				  }
				  
				  // Fin de calcular cada uno de los minimos.
				  
				  for(int i=0; i<populationAux.length; i++){
					  
					  opposite[i] = populationAux[i].clone();
					  
					  for(int j=0; j<opposite[i].size(); j++){
						  
						  
       					  for(int k=0; k<opposite[i].get(j).numberOfInputs(); k++){
       						//System.out.println(max[j][k]);
							  valor = min[j][k]+max[j][k]-opposite[i].get(j).getInput(k);
							  opposite[i].get(j).setInput(k, valor);  
						  }
						  opposite[i].applyThresholds();
						  Prototype Nearest = KNN._1nn(opposite[i].get(j),trainingDataSet);
						 
						  opposite[i].get(j).setClass(Nearest.getOutput(0));
					  }
				  }
				  
				  PopulationOpposite = new PrototypeSet [PopulationSize*2];
				  
				  for(int i=0; i< population.length; i++){
					  PopulationOpposite[i] = populationAux[i].clone();
					  PopulationOpposite[i+populationAux.length] = opposite[i].clone();
				  }
				  
				indices= mejoresParticulas(PopulationOpposite);
				
				 for(int i=0; i<indices.length; i++){
					//  System.out.println(indices[i]);
					 population[i] = PopulationOpposite[indices[i]].clone();
					/* if(indices[i]>PopulationSize){
						 System.out.println("Cogido del Opuesto");
					 }*/
				 }
				 
				  
				  
		   }
		   
	   }// End Main LOOP

	     bestFitness = 0;
	     bestFitnessIndex = 0;
		  for(int i=0; i< PopulationSize;i++){
			  
			  // Where is the best? 
			  if(fitness[i]>bestFitness){
				  bestFitness = fitness[i];
				  bestFitnessIndex=i;
			  }
			  
		  }
		  
	   
      //System.err.println("\n% de acierto en training " + DEGenerator.accuracy(population[bestFitnessIndex], trainingDataSet) );
        //population[bestFitnessIndex].print();
		 System.err.println("\n% de acierto en training " + KNN.classficationAccuracy(population[bestFitnessIndex],trainingDataSet,1)*100./trainingDataSet.size());
		 return population[bestFitnessIndex];
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
      Parameters.setUse("OBDE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      OBDEGenerator.setSeed(seed);
      
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
      
      
      OBDEGenerator generator = new OBDEGenerator(training, k,swarm,particle,iter, 0.5,0.5,0.3,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
