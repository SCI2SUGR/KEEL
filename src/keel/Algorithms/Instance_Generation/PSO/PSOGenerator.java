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
	PSO.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  11-8-2008
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.PSO;

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
import keel.Dataset.Attributes;

import org.core.*;

import org.core.*;

import java.util.StringTokenizer;



/**
 * 
 * @param  k
 * @param  SwarmSize
 * @param Particle Size
 * @param MaxIter
 * @param C1
 * @param C2
 * @param VMax
 * @param Wstart
 * @param Wend
 * @author Isaac Triguero
 * @version 1.0
 */
public class PSOGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int SwarmSize; // SwarmSize == P
  private int ParticleSize; // ParticleSize == K  (in the article)
  private int MaxIter; 
  private double C1;
  private double C2;
  private double VMax;
  private double Wstart;
  private double Wend;
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfClass;
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  
  /**
   * Build a new PSOGenerator Algorithm
   */
  
  public PSOGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double c1, double c2, double vmax, double wstart, double wend)
  {
      super(_trainingDataSet);
      algorithmName="PSO";
      
      this.k = neigbors;
      this.SwarmSize = poblacion;
      this.ParticleSize = perc;
      this.MaxIter = iteraciones;
      this.numberOfPrototypes = getSetSizeFromPercentage(perc);
      
      this.C1 = c1;
      this.C2 = c2;
      this.VMax = vmax;
      this.Wend = wend;
      this.Wstart = wstart;
  }
  


  /**
   * Build a new PSOGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public PSOGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="PSO";
      this.k =  parameters.getNextAsInt();
      this.SwarmSize =  parameters.getNextAsInt();
      this.ParticleSize =  parameters.getNextAsInt();
      this.MaxIter =  parameters.getNextAsInt();
      this.C1 = parameters.getNextAsDouble();
      this.C2 = parameters.getNextAsDouble();
      this.VMax = parameters.getNextAsDouble();
      this.Wstart = parameters.getNextAsDouble();
      this.Wend = parameters.getNextAsDouble();

      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      
      this.numberOfPrototypes = getSetSizeFromPercentage(ParticleSize);
      
      System.out.print("\nIsaac dice:  " + k + " Swar= "+SwarmSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" Wend=  "+Wend+ "\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  
  /**
   * Generate a reduced prototype set by the PSOGenerator method.
   * @return Reduced set by PSOGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");
	  
	  
	  System.out.println("Number of prototypes, result set = "+numberOfPrototypes+ "\n");
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-numberOfPrototypes)*100)/trainingDataSet.size()+ "\n");


	 //trainingDataSet.getFromClass(0).print();
	  
	  //Algorithm
	  // First, we create the population, with SwarmSize.
	  // like a prototypeSet's vector.  
	  
	  PrototypeSet population [] = new PrototypeSet [SwarmSize];
	  PrototypeSet mejorPosicion [] = new PrototypeSet [SwarmSize];
	  PrototypeSet nominalPopulation  = new PrototypeSet();
	  
	  double fitness[] = new double[SwarmSize];
	  double fitness_bestPopulation[] = new double[SwarmSize];
	  PrototypeSet bestParticle = new PrototypeSet();
	  
	  
	  double inertia = ((Wstart-Wend)*(MaxIter))/ (MaxIter + Wend);
	  int mejorParticula =0;  // The best particle in the population
	  double aleatorio;
	  
	  
	  //Each particle must have   Particle Size %

	 // trainingDataSet.print();
	  population[0]= new PrototypeSet(selecRandomSet(numberOfPrototypes,true).clone()) ;
	  fitness[0] = accuracy(population[0],trainingDataSet); 
	  
	  // Aseguro que al menos hay un representante de cada clase.
	  PrototypeSet clases[] = new PrototypeSet [this.numberOfClass];
	  for(int i=0; i< this.numberOfClass; i++){
		  clases[i] = new PrototypeSet(trainingDataSet.getFromClass(i));
		  
		  System.out.println("Clase"+i +" : "+clases[i].size());
	  }
	
	  for(int i=0; i< population[0].size(); i++){
		  for(int j=0; j< this.numberOfClass; j++){
			  if(population[0].getFromClass(j).size() ==0 && clases[j].size()!=0){
				  Prototype aux = new Prototype(clases[j].getRandom()); 
				  population[0].add(aux);
			  }
		  }
	  }
	  
	  population[0].print();
	  
	  for(int i=1; i< SwarmSize; i++){
		  population[i] = new PrototypeSet();
		  for(int j=0; j< population[0].size(); j++){
			  Prototype aux = new Prototype(trainingDataSet.getFromClass(population[0].get(j).getOutput(0)).getRandom());
			  population[i].add(aux);
		  }
		  fitness[i] = accuracy(population[i],trainingDataSet);   // PSOfitness
		  fitness_bestPopulation[i] = fitness[i]; // Initially the same fitness.
	  }
	  
	 // population[0].print();
	  /*
	  for(int i=0; i< SwarmSize; i++){
		  population[i]=selecRandomSet(numberOfPrototypes,true).clone() ;
		  fitness[i] = absoluteAccuracyKNN(population[i], trainingDataSet,k);  // PSO fitness
		  fitness_bestPopulation[i] = fitness[i]; // Initially the same fitness.
		 // System.out.print("Fitness " + i + " = "+ fitness[i] + "\n");
		  
		  
		  //System.out.print("\nPoblaciï¿½n " + i + "\n");
		  //population[i].print();
		  
	  }*/
	  
	  //We select the best initial  particle
	  double bestFitness=fitness[0];
	  int bestFitnessIndex=0;
	  for(int i=1; i< SwarmSize;i++){
		  if(fitness[i]>bestFitness){
			  bestFitness = fitness[i];
			  bestFitnessIndex=i;
		  }
		  
	  }
	  
	   for(int j=0;j<SwarmSize;j++){
		   mejorPosicion[j] = population[j].clone(); // hard-copy.Save the best position of the particle.
		   											//Initial mejorPosicion = initial population.
		   
		   
		   
		 //Now, I establish the index of each prototype.
		  for(int i=0; i<population[j].size(); ++i)
			  population[j].get(i).setIndex(i);
	   }
      

		  double velocidad[][][] = new double[SwarmSize][][]; // tri-dimensional vector
		  
		  int num_atribs = population[0].get(0).numberOfInputs();
		  
		  for(int i=0; i<SwarmSize;i++){
			  velocidad[i]= new double[population[0].size()][];  // velocity matrix.
			  
			  // Initially there is no velocity, no memory..
			  for(int j=0; j<population[0].size();j++){
				  velocidad[i][j] = new double[num_atribs];
				  for(k=0; k<num_atribs;k++){
					  
					  
					  velocidad[i][j][k] = RandomGenerator.Randdouble(-VMax, VMax)*1. ;   // the initial velocity, a random number between -Vmax , Vmax
					 // System.out.println(velocidad[i][j][k]);
				  }
			  }
		  }
		   
		  
		  
		  
		  
	   for(int iter=0; iter< MaxIter; iter++){ // Main loop
		   
		   for(int i=0; i< SwarmSize; i++){
			   
			   
			   for(int k = 0; k< population[i].size();k++){
				   
				   Prototype resta = mejorPosicion[i].get(k).sub(population[i].get(k));
				   Prototype restaBestParticle =  mejorPosicion[bestFitnessIndex].get(k).sub(population[i].get(k));
				  
				   for(int j=0; j< num_atribs ; j++){
					   velocidad[i][k][j]= inertia * velocidad[i][k][j] ;  // Memory velocity.
					   aleatorio =RandomGenerator.Randdouble(0, 1) ;
					   
					   
					   
					   velocidad[i][k][j]+= C1*aleatorio* resta.getInput(j) ;                // Cognition part.
					   aleatorio =RandomGenerator.Randdouble(0, 1) ;
					   velocidad[i][k][j]+= C2*aleatorio * restaBestParticle.getInput(j) ;  // Social part.
				   
				   //System.out.print(aleatorio + "\t");
					   // Then we do  xi = xi + vi.
					   if(velocidad[i][k][j]>VMax){
						   velocidad[i][k][j] = VMax;  // The particles's velocities has a maximum velocity.
					   }else if(velocidad[i][k][j]< -VMax){
						   velocidad[i][k][j]=-VMax;      // absolute value. ï¿½? or -VMax , Vmax. ?
						   
					   }
					   
					   
					   //System.out.print("\nVelocidad ="+ velocidad[i][k][j] + "\n");
					  // System.out.print("\nvalor= "+  population[i].get(k).getInput(j)+ "\n");
					  
					 
					   
					   double suma = population[i].get(k).getInput(j) + velocidad[i][k][j]*1.;
					   
					   //if(suma>1) suma = 1;
					   //else if( suma<0) suma = 0;  // Establish the  normalize limits [0,1]
					   //System.out.print("\nSuma= "+  suma+ "\n");
					  population[i].get(k).setInput(j,suma); // We add the velocity to the attribute
					  population[i].get(k).applyThresholds();
				   }
				   
			   }
		   }
		   
		   //Now we have xi = xi + vi.for all particles.
		   // Particles has changed, We must calculate fitness and compare all.
		  
			  for(int i=0; i< SwarmSize; i++){
				  /*
				  if(k<=population[i].size())
					  fitness[i] = absoluteAccuracyKNN(population[i], trainingDataSet,k);  // PSO fitness
				  else
					 fitness[i] = absoluteAccuracyKNN(population[i],trainingDataSet,population[i].size());
			  */
				
				  // Antes de calcular el fitness, tengo que "transformar los datos nominales.."
				   nominalPopulation = new PrototypeSet();
					  nominalPopulation.formatear(population[i]);
					  fitness[i] = accuracy(nominalPopulation,trainingDataSet);
				  //fitness[i] = accuracy(population[i],trainingDataSet);
			  }
		   

			  	
			  
			  for(int i=0; i< SwarmSize;i++){
				  
				  // Where is the best? 
				  if(fitness[i]>bestFitness){
					  bestFitness = fitness[i];
					  bestFitnessIndex=i;
				  }
				  
				//Save the best particles!
				  if(fitness[i]>fitness_bestPopulation[i]){
					  fitness_bestPopulation[i] = fitness[i];
					  mejorPosicion[i] = population[i].clone(); // Hard Copy.
					  
				  }
			  }
			  
			  
			  
			  //Calculate the new inertia.
		   inertia = ((Wstart-Wend)*(MaxIter-iter))/ (MaxIter + Wend);
		   
	   }
	   

	   
     // PrototypeSet result = new PrototypeSet(SwarmSize);
      /*for(int i=0; i<SwarmSize; ++i)
      {
         // Prototype averaged = C.get(i).avg();
         // double averagedClass = C.get(i).mostFrequentClass();
         // averaged.setLabel(averagedClass);
          //result.add(averaged);
          //System.out.println("Prototipo " + i + " tiene clase " + averagedClass);
      }*/
	  // System.out.println("Best fitness= "+bestFitness);
	   //System.out.println("Generated set \n");
	   //mejorPosicion[bestFitnessIndex].print();
		  
	   nominalPopulation = new PrototypeSet();
		  nominalPopulation.formatear(mejorPosicion[bestFitnessIndex]);
	//	  System.err.println("\n% de acierto en training Nominal " + PSOGenerator.accuracy(nominalPopulation, trainingDataSet) );
		      
		  
	  // trainingDataSet.getFromClass(0).print();
     // System.err.println("\n% de acierto en training " + PSOGenerator.accuracy(mejorPosicion[bestFitnessIndex], trainingDataSet) );
      //return mejorPosicion[bestFitnessIndex];
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
      Parameters.setUse("PSO", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      PSOGenerator.setSeed(seed);
      
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
      
      
      PSOGenerator generator = new PSOGenerator(training, k,swarm,particle,iter, c1,c2,vmax,wstart,wend );
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
