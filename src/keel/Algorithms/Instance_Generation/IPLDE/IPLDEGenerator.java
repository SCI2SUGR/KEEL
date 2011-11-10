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
	IPLDE.java
	Isaac Triguero Velazquez.
	
	Created by Isaac Triguero Velazquez  23-7-2009
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.IPLDE;

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
public class IPLDEGenerator extends PrototypeGenerator {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
 
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private int iterBasicDE;
  private double ScalingFactor;
  private double CrossOverRate;
  private int Strategy;
  private String CrossoverType; // Binomial, Exponential, Arithmetic
  private double tau[] = new double[4];
  protected int numberOfClass;
  protected int numberOfPrototypes;  // Particle size is the percentage
  /** Parameters of the initial reduction process. */
  private String[] paramsOfInitialReducction = null;

  private int iterSFGSS;
  private int iterSFHC;
  
  
  /**
   * Build a new IPLDEGenerator Algorithm
   */
  
  public IPLDEGenerator(PrototypeSet _trainingDataSet, int neigbors,int poblacion, int perc, int iteraciones, double F, double CR, int strg)
  {
      super(_trainingDataSet);
      algorithmName="IPLDE";
      
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
   * Build a new IPLDEGenerator Algorithm
   * @param t Original prototype set to be reduced.
   * @param parameters Parameters of the algorithm (only % of reduced set).
   */
  public IPLDEGenerator(PrototypeSet t, Parameters parameters)
  {
      super(t, parameters);
      algorithmName="IPLDE";
      this.k =  parameters.getNextAsInt();
      this.iterBasicDE =  parameters.getNextAsInt();//*trainingDataSet.get(0).numberOfInputs(); //NC*1000
      this.iterSFGSS =  parameters.getNextAsInt();
      this.iterSFHC =  parameters.getNextAsInt();
      this.ScalingFactor = parameters.getNextAsDouble();
      this.CrossOverRate = parameters.getNextAsDouble();
      this.tau[0] =  parameters.getNextAsDouble();
      this.tau[1] =  parameters.getNextAsDouble();
      
      this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
      System.out.print("\nIsaac dice:  " + k + " Swar= "+PopulationSize+ " Particle=  "+ ParticleSize + " Maxiter= "+ MaxIter+" CR=  "+this.CrossOverRate+ " CrossverType = "+ this.CrossoverType+"\n");
      //numberOfPrototypes = getSetSizeFromPercentage(parameters.getNextAsDouble());
  }
  

   
  public PrototypeSet mutant(PrototypeSet population, double SFi){
	  
	  
	  PrototypeSet mutant = new PrototypeSet(population.clone());
	  Prototype r1,r2,r3,r4,r5, resta,resta2,producto2, producto, nearest;
	  

	  for(int i=0; i< population.size(); i++){
      
		  //PrototypeSet mismaClase= KNN.getNearestNeighborsWithSameClassAs(population.get(i), population, 5);
		  //PrototypeSet mismaClase= KNN.getNearestNeighborsWithSameClassAs(population.get(i), trainingDataSet, 3);
	      
		   PrototypeSet mismaClase = trainingDataSet.getFromClass(population.get(i).getOutput(0));
		  

	      PrototypeSet aux = new PrototypeSet();
		  
	      if(mismaClase.size() < 5){

	    	  for(int j=mismaClase.size(); j < 5; j++){
	    		Prototype Perturbance = new Prototype(population.get(i));

	    		for(int k=0; k< Perturbance.numberOfInputs(); k++){
	        		 Perturbance.setInput(k, population.get(i).getInput(k)+RandomGenerator.Randdouble(-0.01*j, 0.01*j));
	        	}
	    		aux.add(Perturbance);
	    		
	    	  }
	    	  
	    	  mismaClase.add(aux);
	    	  
	    	  
	      }
	      
	      int lista[] = new int[mismaClase.size()];
	      inic_vector_sin(lista,i);
	      desordenar_vector_sin(lista);
	      
	      
	       r1 = mismaClase.get(lista[0]);
		   r2 =  mismaClase.get(lista[1]);
		   r3 =  mismaClase.get(lista[2]);
		   r4 =  mismaClase.get(lista[3]);
		   r5 =  mismaClase.get(lista[4]);
		   
			switch(this.Strategy){
				case 1:// ViG = Xr1,G + F(Xr2,G - Xr3,G) De rand 1
					resta = r2.sub(r3);
					producto = resta.mul(SFi);
					mutant.set(i, producto.add(r1));
				break;
			

				case 2: //DE rand to nearest 1
					resta = r1.sub(r2);
					nearest = KNN.getNearestNeighborsWithSameClassAs(population.get(i), trainingDataSet, 1).get(0);
					
					resta2 = nearest.sub(population.get(i));
					
					producto = resta.mul(SFi);
					producto2 = resta.mul(SFi);
					
					producto = producto.add(producto2);
					mutant.set(i, (population.get(i)).add(producto));
				
				break;
					
	  		       
				case 3://DE current to rand 1
					resta = r2.sub(r3);
					resta2= r1.sub(population.get(i));
					
					double aleatorio = RandomGenerator.Randdouble(0, 1);
					producto = resta.mul(SFi*aleatorio);
					producto2 = resta2.mul(aleatorio);
					
					producto = producto.add(producto2);
					
					mutant.set(i, producto.add(population.get(i)));
				break;
				
				
				case 4://  De rand 2
					resta = r2.sub(r3);
					resta2= r4.sub(r5);
					
					producto = resta.mul(SFi);
					producto2 = resta2.mul(SFi);
					
					producto = producto.add(producto2);
					
					mutant.set(i, producto.add(r1));
				break;
		
				
				
			}
		  
	  }
		   

	 // System.out.println("********Mutant**********");
	 // mutant.print();
	   
     mutant.applyThresholds();
	
	  return mutant;
  }
  
  

  /**
   * Local Search Fitness Function
   */
  public double lsff(double Fi, double CRi, PrototypeSet population){
	  PrototypeSet resta, producto, mutation;
	  PrototypeSet crossover;
	  double FitnessFi = 0;
	  
	  
	  //Mutation:
	  mutation = new PrototypeSet(population.size());
   	  mutation = mutant(population, Fi);
   	
   	  //Crossover
   	crossover =new PrototypeSet(mutation);
   	  /*crossover =new PrototypeSet(population.clone());
   	  
	   for(int i=0; i< mutation.size(); i++){
			for(int j=0; j< mutation.get(i).numberOfInputs(); j++){
				   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
				   if(randNumber<CRi){
					   Prototype Aux = mutation.get(i);
					   crossover.get(i).setInput(j, Aux.getInput(j)); // Overwrite.
				   }
			   
			}

	   }
	   
	  */ 
	   // Compute fitness
	   PrototypeSet nominalPopulation = new PrototypeSet();
       nominalPopulation.formatear(crossover);
       FitnessFi = accuracy(nominalPopulation,trainingDataSet);
	   
   	   return FitnessFi;
  }
  
  
  
  /**
   * SFGSS local Search.
   * @param population
   */
  public PrototypeSet SFGSS(PrototypeSet population, double CRi){
	  double a=0.1, b=1;
	  double fi1=0, fi2=0, fitnessFi1=0, fitnessFi2=0;
	  double phi = (1+ Math.sqrt(5))/5;
	  double scaling;
	  PrototypeSet crossover, resta, producto, mutation;
	  
	  for (int i=0; i<this.iterSFGSS; i++){ // Computation budjet
	  
		  fi1 = b - (b-a)/phi;
		  fi2 = a + (b-a)/phi;
		  
		  fitnessFi1 = lsff(fi1, CRi, population);
		  fitnessFi2 = lsff(fi2, CRi,population);
		  
		  if(fitnessFi1> fitnessFi2){
			  b = fi2;
		  }else{
			  a = fi1;  
		  }
	  
	  } // End While
	  
	  
	  if(fitnessFi1> fitnessFi2){
		  scaling = fi1;
	  }else{
		  scaling = fi2;
	  }
	  
	  
	  
	  //Mutation:
	  mutation = new PrototypeSet(population.size());
   	  mutation = mutant(population, scaling);
   	
   	  //Crossover
  	crossover =new PrototypeSet(mutation);
  	
 	  /*crossover =new PrototypeSet(population.clone());
   	  
	   for(int i=0; i< mutation.size(); i++){
			for(int j=0; j< mutation.get(i).numberOfInputs(); j++){
				   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
				   if(randNumber<CRi){
					   Prototype Aux = mutation.get(i);
					   crossover.get(i).setInput(j, Aux.getInput(j)); // Overwrite.
				   }
			   
			}

	   }
	   
	   */
	  
	return crossover;
  }
  
  /**
   * SFHC local search
   */
  
  public  PrototypeSet SFHC(PrototypeSet population, double SFi, double CRi){
	  double fitnessFi1, fitnessFi2, fitnessFi3, bestFi;
	  PrototypeSet crossover, resta, producto, mutation;
	  double h= 0.5;
	  
	  
	  for (int i=0; i<this.iterSFHC; i++){ // Computation budjet
		  		  
		  fitnessFi1 = lsff(SFi-h, CRi, population);
		  fitnessFi2 = lsff(SFi, CRi,  population);
		  fitnessFi3 = lsff(SFi+h, CRi,  population);
		  
		  if(fitnessFi1 >= fitnessFi2 && fitnessFi1 >= fitnessFi3){
			  bestFi = SFi-h;
		  }else if(fitnessFi2 >= fitnessFi1 && fitnessFi2 >= fitnessFi3){
			  bestFi = SFi;
			  h = h/2; // H is halved.
		  }else{
			  bestFi = SFi;
		  }
		  
		  SFi = bestFi;
	  }
	  
	  
	  //Mutation:
	  mutation = new PrototypeSet(population.size());
   	  mutation = mutant(population, SFi);
   	
   	  //Crossover
  	crossover =new PrototypeSet(mutation);
  	
 	  /*  crossover =new PrototypeSet(population.clone());
   	  
	   for(int i=0; i< mutation.size(); i++){
			for(int j=0; j< mutation.get(i).numberOfInputs(); j++){
				   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
				   if(randNumber<CRi){
					   Prototype Aux = mutation.get(i);
					   crossover.get(i).setInput(j, Aux.getInput(j)); // Overwrite.
				   }
			   
			}

	   }
	   
	   */
	   
	  
	return crossover;
  
  }
  
  
  
  
  /**
   * 
   *
   */
  public PrototypeSet basicDE(PrototypeSet myTrain){ 
	 
	  double fitness;
  	  Prototype r1,r2,r3, resta, producto, resta2, producto2;
  	  
      Prototype crossover;
	  
      PrototypeSet nominalPopulation;
	   nominalPopulation = new PrototypeSet();
       nominalPopulation.formatear(myTrain);
      
	  fitness = accuracy(nominalPopulation,trainingDataSet);
	  // System.out.println("fitness "+ fitness);	

	  
   double randj[] = new double[5];
	   
	  // Generate randj for j=1 to 5.
	   for(int j=0; j<5; j++){
		   randj[j] = RandomGenerator.Randdouble(0, 1);
		}
			   
		  for(int i=0; i< this.iterBasicDE; i++){
			  
			  
			  PrototypeSet modificados = new PrototypeSet(myTrain);
			  
			  if(i%1000==0){ 
				  if(randj[4] < tau[0]){
					  // System.out.println("SFGSS applied");
					   //SFGSS
					   modificados = SFGSS(myTrain, this.CrossOverRate);
					   
					   
				   }else if(tau[0] <= randj[4] && randj[4] < tau[1]){
					  modificados = SFHC(myTrain, this.ScalingFactor, this.CrossOverRate);
				  }
			  }else{ 
			 
				  ScalingFactor = 0.1+ 0.9*RandomGenerator.Randdouble(0, 1);
				  
				  for(int j=0; j< myTrain.size(); j++){
					  
					   PrototypeSet mismaClase = trainingDataSet.getFromClass(myTrain.get(j).getOutput(0));
						  
	
					      PrototypeSet aux = new PrototypeSet();
						  
					      if(mismaClase.size() < 3){
	
					    	  for(int l=mismaClase.size(); l < 5; l++){
					    		Prototype Perturbance = new Prototype(myTrain.get(j));
	
					    		for(int k=0; k< Perturbance.numberOfInputs(); k++){
					        		 Perturbance.setInput(k, myTrain.get(j).getInput(k)+RandomGenerator.Randdouble(-0.01*l, 0.01*l));
					        	}
					    		aux.add(Perturbance);
					    		
					    	  }
					    	  
					    	  mismaClase.add(aux);
					    	  
					    	  
					      }
					      
				      ArrayList<Integer> indexes =  RandomGenerator.generateDifferentRandomIntegers(0, mismaClase.size()-1);
			           r1 = mismaClase.get(indexes.get(0));
			    	   r2 = mismaClase.get(indexes.get(1));
			    	   r3 = mismaClase.get(indexes.get(2));
			    	   
					  	  	   
			    	   	  
					//DE current to rand 1
						resta = r2.sub(r3);
						resta2= r1.sub(myTrain.get(j));
						
						double aleatorio = RandomGenerator.Randdouble(0, 1);
						producto = resta.mul(this.ScalingFactor*aleatorio);
						producto2 = resta2.mul(aleatorio);
						
						producto = producto.add(producto2);
						
						crossover = producto.add(myTrain.get(j)); // Current
				
						crossover.applyThresholds();
	
						modificados.set(j,crossover);
						  
				  } // End mutation and crossover
			  } //end else

			  
			   nominalPopulation = new PrototypeSet();
		       nominalPopulation.formatear(modificados);
		    		       
			  double trialFitness =accuracy(nominalPopulation,trainingDataSet);
				
			  if(trialFitness > fitness){
				  //System.out.println("Selecting");
				  fitness = trialFitness;
				  myTrain = new PrototypeSet(modificados.clone());
			  } 

		  
	   
	  }

		  

		  
		  
	  return myTrain;
  }
  
    
  
  /**
   * Generate a reduced prototype set by the IPLDEGenerator method.
   * @return Reduced set by IPLDEGenerator's method.
   */
  
  
  public PrototypeSet reduceSet()
  {
	  System.out.print("\nThe algorithm  IPADE is starting...\n Computing...\n");
	  this.Strategy = 3;
	  
	  PrototypeSet solucion = new PrototypeSet();
	  
	  PrototypeSet Clases [] = new PrototypeSet[this.numberOfClass];
	  double fitnessClass[] = new double[this.numberOfClass];
	  PrototypeSet nominalPopulation;
	  

	  for(int i=0; i<this.numberOfClass; i++){
		 
		  
		  if(trainingDataSet.getFromClass(i).size() >0){ 
			  Clases[i] = new PrototypeSet(trainingDataSet.getFromClass(i).clone());
			  
			  System.out.println("Size ->"+Clases[i].size());
			  Prototype centroid = Clases[i].avg();
			  //centroid.print();
			  solucion.add(centroid); // Centroide
			  
		  }
		 
	  }
	  
	  
	 // solucion.print();
	  
	  
	  solucion=basicDE(solucion); //Initial Optimization
	 // solucion.print();
	  
	  double Fitness= accuracy(solucion,trainingDataSet);
	//  System.out.println("Initial Global Fitness = "+ Fitness);
	  
	  boolean claseMarcada[] = new boolean[this.numberOfClass];
	  boolean fin[] = new boolean[this.numberOfClass];
	  Arrays.fill(claseMarcada, false);
	  Arrays.fill(fin, true);
	  
	  int iterOptimizada[] = new int [this.numberOfClass];
	  Arrays.fill(iterOptimizada, 1);
	  
	  int iter=0;
	  while(!Arrays.equals(claseMarcada, fin)){	  
		  
		//  System.out.println("iter "+ iter+ " fitness= "+Fitness);
		 // System.out.println("iter "+ iter+ " Red= "+((trainingDataSet.size()-solucion.size())*100.)/trainingDataSet.size());
		  
		  
		  double minFitness= Double.MAX_VALUE;
		  int objetivo= -1;
		  
		  for(int j=0; j<this.numberOfClass; j++){
			  if(trainingDataSet.getFromClass(j).size()>1){
				  
				  
				  fitnessClass[j]=accuracy(solucion,trainingDataSet.getFromClass(j));
				   
				  //System.out.println("Fitness class["+j+"]= " +fitnessClass[j]);
				  
				  
				  if(fitnessClass[j] < minFitness && !claseMarcada[j]){
					  minFitness = fitnessClass[j];
					  objetivo = j;
				  }
				  
				  if(fitnessClass[j] == 100){
					  claseMarcada[j] = true;
				  }
			  }else{
				  claseMarcada[j] = true;
			  } 
		   }
		 
			  
		 // System.out.println("Objective =" + objetivo);
		  PrototypeSet tester;
		 
		  
		 
			  if(!claseMarcada[objetivo]){
				  PrototypeSet solucion2 = new PrototypeSet(solucion.clone());
				 // solucion2.add(trainingDataSet.getFromClass(objetivo).getRandom()); // A–ado uno Y pruebo a optimizar.
				  solucion2.add(trainingDataSet.farthestTo(solucion.getFromClass(objetivo).getRandom())); 
				  
				  tester = basicDE(solucion2).clone();
			  		  
				   nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(solucion);
			       Fitness= accuracy(nominalPopulation,trainingDataSet);
			       
			       nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(tester);
				  double trialFitness= accuracy(nominalPopulation,trainingDataSet);
				  
				  
				  //System.out.println("Trial fitnss= " + trialFitness);
				  if(trialFitness > Fitness){
					  iterOptimizada[objetivo]++;
					  solucion = new PrototypeSet(tester.clone());
					  Fitness = trialFitness;
				  }else{
					  claseMarcada[objetivo] = true;
				  }
				  
			  }
		  
			  //Fitness= accuracy(solucion,trainingDataSet);
			  //System.out.println("Fitness = "+ Fitness);
		  
			  iter++;
		  
		  
	  }
	  
	  //solucion.print();
	  nominalPopulation = new PrototypeSet();
      nominalPopulation.formatear(solucion);
	  double trialFitness= accuracy(nominalPopulation,trainingDataSet);
	  
	  System.out.println("Final Fitness = "+ trialFitness);
	  System.out.println("Reduction %, result set = "+((trainingDataSet.size()-solucion.size())*100.)/trainingDataSet.size()+ "\n");
	  

     return solucion;
  }
  
  /**
   * General main for all the prototoype generators
   * @param args Arguments of the main function.
   */
  public static void main(String[] args)
  {
      Parameters.setUse("IPLDE", "<seed> <Number of neighbors>\n<Swarm size>\n<Particle Size>\n<MaxIter>\n<DistanceFunction>");        
      Parameters.assertBasicArgs(args);
      
      PrototypeSet training = PrototypeGenerationAlgorithm.readPrototypeSet(args[0]);
      PrototypeSet test = PrototypeGenerationAlgorithm.readPrototypeSet(args[1]);
      
      
      long seed = Parameters.assertExtendedArgAsInt(args,2,"seed",0,Long.MAX_VALUE);
      IPLDEGenerator.setSeed(seed);
      
      int k = Parameters.assertExtendedArgAsInt(args,3,"number of neighbors", 1, Integer.MAX_VALUE);
      int swarm = Parameters.assertExtendedArgAsInt(args,4,"swarm size", 1, Integer.MAX_VALUE);
      int particle = Parameters.assertExtendedArgAsInt(args,5,"particle size", 1, Integer.MAX_VALUE);
      int iter = Parameters.assertExtendedArgAsInt(args,6,"max iter", 1, Integer.MAX_VALUE);

      
      //String[] parametersOfInitialReduction = Arrays.copyOfRange(args, 4, args.length);
     //System.out.print(" swarm ="+swarm+"\n");
      
      
      IPLDEGenerator generator = new IPLDEGenerator(training, k,swarm,particle,iter, 0.5,0.5,1);
      
  	  
      PrototypeSet resultingSet = generator.execute();
      
  	//resultingSet.save(args[1]);
      //int accuracyKNN = KNN.classficationAccuracy(resultingSet, test, k);
      int accuracy1NN = KNN.classficationAccuracy(resultingSet, test);
      generator.showResultsOfAccuracy(Parameters.getFileName(), accuracy1NN, test);
  }

}
