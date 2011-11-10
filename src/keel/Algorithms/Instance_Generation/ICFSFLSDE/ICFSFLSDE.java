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

//
//  SSMA.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-10-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.ICFSFLSDE;

import keel.Algorithms.Preprocess.Basic.*;

import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.utilities.*;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;



import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;

import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class ICFSFLSDE extends Metodo {

	  /*Own parameters of the algorithm*/
	  private int k;
	  
	 private double semilla;
  public String Script; // para releer par�metros..
  private PrototypeSet trainingDataSet;
  private PrototypeGenerator generador;
  //Par�metros DE

  
  private int PopulationSize; 
  private int ParticleSize;
  private int MaxIter; 
  private double ScalingFactor;
  private double CrossOverRate;
  private int Strategy;
  private String CrossoverType; // Binomial, Exponential, Arithmetic
  
  protected int numberOfClass;
  private double tau[] = new double[4];
  private double Fl, Fu;
  
  private int iterSFGSS;
  private int iterSFHC;
  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfStrategies; // number of strategies in the pool
  
  public ICFSFLSDE (String ficheroScript) {
	super (ficheroScript);
    
  }


  /**
   * Reads the prototype set from a data file.
   * @param nameOfFile Name of data file to be read.
   * @return PrototypeSet built with the data of the file.
   */
  public static PrototypeSet readPrototypeSet(String nameOfFile)
  {
      Attributes.clearAll();//BUGBUGBUG
      InstanceSet training = new InstanceSet();        
      try
      {
      	//System.out.print("PROBANDO:\n"+nameOfFile);
          training.readSet(nameOfFile, true); 
          training.setAttributesAsNonStatic();
          InstanceAttributes att = training.getAttributeDefinitions();
          Prototype.setAttributesTypes(att);            
      }
      catch(Exception e)
      {
          System.err.println("readPrototypeSet has failed!");
          e.printStackTrace();
      }
      return new PrototypeSet(training);
  }

  public void inic_vector_sin(int vector[], int without){

	  	for(int i=0; i<vector.length; i++) 
	  		if(i!=without)
	  			vector[i] = i; // Lo inicializo de 1 a n-1
	  }
	  
	  public void desordenar_vector_sin(int vector[]){
	  	int tmp, pos;
	  	for(int i=0; i<vector.length-1; i++){
	  		pos = Randomize.Randint(0, vector.length-1);
	  		tmp = vector[i];
	  		vector[i] = vector[pos];
	  		vector[pos] = tmp;
	  	}
	  }
	  

	  
	  public PrototypeSet mutant(PrototypeSet population[], int actual, int mejor, double SFi){
	  	  
	  	  
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
	  		   		 producto = resta.mulEscalar(SFi);
	  		   		 mutant = producto.sumar(r1);
	  		   	    break;
	  			   
	  		   	   case 2: // Vig = Xbest,G + F(Xr2,G - Xr3,G)  De best 1
	  			   		 resta = r2.restar(r3);
	  			   		 producto = resta.mulEscalar(SFi);
	  			   		 mutant = population[mejor].sumar(producto);
	  			   break;
	  			   
	  		   	   case 3: // Vig = ... De rand to best 1
	  		   		   resta = r1.restar(r2); 
	  		   		   resta2 = population[mejor].restar(population[actual]);
	  		   		 			   		 
	  			   	   producto = resta.mulEscalar(SFi);
	  			   	   producto2 = resta2.mulEscalar(SFi);
	  			   		
	  			   	   result = population[actual].sumar(producto);
	  			   	   mutant = result.sumar(producto2);
	  			   		 			   		 
	  			   break;
	  			   
	  		   	   case 4: // DE best 2
	  		   		   resta = r1.restar(r2); 
	  		   		   resta2 = r3.restar(r4);
	  		   		 			   		 
	  			   	   producto = resta.mulEscalar(SFi);
	  			   	   producto2 = resta2.mulEscalar(SFi);
	  			   		
	  			   	   result = population[mejor].sumar(producto);
	  			   	   mutant = result.sumar(producto2);
	  			   break;
	  			  
	  		   	   case 5: //DE rand 2
	  		   		   resta = r2.restar(r3); 
	  		   		   resta2 = r4.restar(r5);
	  		   		 			   		 
	  			   	   producto = resta.mulEscalar(SFi);
	  			   	   producto2 = resta2.mulEscalar(SFi);
	  			   		
	  			   	   result = r1.sumar(producto);
	  			   	   mutant = result.sumar(producto2);
	  			   	   
	    		       break;
	    		       
	  		   	   case 6: //DE rand to best 2
	  		   		   resta = r1.restar(r2); 
	  		   		   resta2 = r3.restar(r4);
	  		   		   resta3 = population[mejor].restar(population[actual]);
	  		   		   
	  			   	   producto = resta.mulEscalar(SFi);
	  			   	   producto2 = resta2.mulEscalar(SFi);
	  			   	   producto3 = resta3.mulEscalar(SFi);
	  			   	   
	  			   	   result = population[actual].sumar(producto);
	  			   	   result = result.sumar(producto2);
	  			   	   mutant = result.sumar(producto3);
	    		       break;
	    		       
	  		   	  /*// Para hacer esta estrat�gia, lo que hay que elegir es CrossoverType = Arithmetic
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
	   * Local Search Fitness Function
	   * 
	   */
	  public double lsff(double Fi, double CRi, PrototypeSet population[], int actual, int mejor){
		  PrototypeSet resta, producto, mutant;
		  PrototypeSet crossover;
		  double FitnessFi = 0;
		  
		  
		  //Mutation:
		  mutant = new PrototypeSet(population[actual].size());
	   	  mutant = mutant(population, actual, mejor, Fi);
	   	
	   	  
	   	  //Crossover
	   	  crossover =new PrototypeSet(population[actual]);
	   	  
		   for(int j=0; j< population[actual].size(); j++){ // For each part of the solution
			   
			   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
			   if(randNumber< CRi){
				   crossover.set(j, mutant.get(j)); // Overwrite.
			   }
		   }
		   
		   
		   // Compute fitness
		   PrototypeSet nominalPopulation = new PrototypeSet();
	       nominalPopulation.formatear(crossover);
	       FitnessFi =  classficationAccuracy1NN(nominalPopulation,trainingDataSet);
		   
	   	   return FitnessFi;
	  }
	  
	  
	  
	  /**
	   * SFGSS local Search.
	   * @param population
	   * 
	   */
	  public PrototypeSet SFGSS(PrototypeSet population[], int actual, int mejor, double CRi){
		  double a=0.1, b=1;
		  double fi1=0, fi2=0, fitnessFi1=0, fitnessFi2=0;
		  double phi = (1+ Math.sqrt(5))/5;
		  double scaling;
		  PrototypeSet crossover, resta, producto, mutant;
		  
		  for (int i=0; i<this.iterSFGSS; i++){ // Computation budjet
		  
			  fi1 = b - (b-a)/phi;
			  fi2 = a + (b-a)/phi;
			  
			  fitnessFi1 = lsff(fi1, CRi, population,actual,mejor);
			  fitnessFi2 = lsff(fi2, CRi,population,actual,mejor);
			  
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
		  mutant = new PrototypeSet(population[actual].size());
		  mutant = mutant(population, actual, mejor, scaling);
	   	  
	   	  //Crossover
	   	  crossover =new PrototypeSet(population[actual]);
	   	  
		   for(int j=0; j< population[actual].size(); j++){ // For each part of the solution
			   
			   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
			   if(randNumber< CRi){
				   crossover.set(j, mutant.get(j)); // Overwrite.
			   }
		   }
		   
		   
		  
		return crossover;
	  }
	  
	  /**
	   * SFHC local search
	   * 
	   */
	  
	  public  PrototypeSet SFHC(PrototypeSet population[], int actual, int mejor, double SFi, double CRi){
		  double fitnessFi1, fitnessFi2, fitnessFi3, bestFi;
		  PrototypeSet crossover, resta, producto, mutant;
		  double h= 0.5;
		  
		  
		  for (int i=0; i<this.iterSFHC; i++){ // Computation budjet
			  		  
			  fitnessFi1 = lsff(SFi-h, CRi, population,actual,mejor);
			  fitnessFi2 = lsff(SFi, CRi,  population,actual,mejor);
			  fitnessFi3 = lsff(SFi+h, CRi,  population,actual,mejor);
			  
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
		  mutant = new PrototypeSet(population[actual].size());
		  mutant = mutant(population, actual, mejor, SFi);
		 
	   	  //Crossover
	   	  crossover = new PrototypeSet(population[actual]);
	   	  
		   for(int j=0; j< population[actual].size(); j++){ // For each part of the solution
			   
			   double randNumber = RandomGenerator.Randdouble(0, 1);
				   
			   if(randNumber< CRi){
				   crossover.set(j, mutant.get(j)); // Overwrite.
			   }
		   }
		   
		   
		  
		return crossover;
	  
	  }
	  
	  
	  
	  /**
	   * Implements the 1NN algorithm
	   * @param current Prototype which the algorithm will find its nearest-neighbor.
	   * @param dataSet Prototype set in which the algorithm will search.
	   * @return Nearest prototype to current in the prototype set dataset.
	   */
	  public static Prototype _1nn(Prototype current, PrototypeSet dataSet)
	  {
	      Prototype nearestNeighbor = dataSet.get(0);
	      int indexNN = 0;
	      //double minDist = Distance.dSquared(current, nearestNeighbor);
	      //double minDist = Distance.euclideanDistance(current, nearestNeighbor);
	      double minDist =Double.POSITIVE_INFINITY;
	      double currDist;
	      int _size = dataSet.size();
	    //  System.out.println("****************");
	     // current.print();
	      for (int i=0; i<_size; i++)
	      {
	          Prototype pi = dataSet.get(i);
	          //if(!current.equals(pi))
	          //{
	             // double currDist = Distance.dSquared(current, pi);
	           currDist = Distance.euclideanDistance(pi,current);
	          // System.out.println(currDist);
	          
	           if(currDist >0){
	              if (currDist < minDist)
	              {
	                  minDist = currDist;
	                 // nearestNeighbor = pi;
	                  indexNN =i;
	              }
	          }
	          //}
	      }
	      
	     // System.out.println("Min dist =" + minDist + " Vecino Cercano = "+ indexNN);
	      
	      return dataSet.get(indexNN);
	  }
	  
	  public double classficationAccuracy1NN(PrototypeSet training, PrototypeSet test)
	  {
		int wellClassificated = 0;
	      for(Prototype p : test)
	      {
	          Prototype nearestNeighbor = _1nn(p, training);          
	          
	          if(p.getOutput(0) == nearestNeighbor.getOutput(0))
	              ++wellClassificated;
	      }
	  
	      
	      return 100.0* (wellClassificated / (double)test.size());
	  }
	  
	  
	  
	  
	  /**
	   * Generate a reduced prototype set by the SADEGenerator method.
	   * @return Reduced set by SADEGenerator's method.
	   */
	  

	  public PrototypeSet reduceSet(PrototypeSet initial)
	  {
		  System.out.print("\nThe algorithm  SFLSDE is starting...\n Computing...\n");
		  
		  //Algorithm
		  // First, we create the population, with PopulationSize.
		  // like a prototypeSet's vector.  

		  PrototypeSet population [] = new PrototypeSet [PopulationSize];
		  PrototypeSet mutation[] = new PrototypeSet[PopulationSize];
		  PrototypeSet crossover[] = new PrototypeSet[PopulationSize];
		  
		  
		  double ScalingFactor[] = new double[this.PopulationSize];
		  double CrossOverRate[] = new double[this.PopulationSize]; // Inside of the Optimization process.
		  double fitness[] = new double[PopulationSize];

		  double fitness_bestPopulation[] = new double[PopulationSize];
		  PrototypeSet bestParticle = new PrototypeSet();
		  
		
	  
		  //Each particle must have   Particle Size %

		  // First Stage, Initialization.
		  
		  PrototypeSet nominalPopulation;
		  
		  population[0]= new PrototypeSet(initial.clone()) ;
		  generador = new PrototypeGenerator(trainingDataSet);
		   nominalPopulation = new PrototypeSet();
	       nominalPopulation.formatear(population[0]);
	       
		  fitness[0] = classficationAccuracy1NN(nominalPopulation,trainingDataSet);
		  
		  System.out.println("Best initial fitness = "+ fitness[0]);

		  this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
		  
		  
	      for(int i=1; i< PopulationSize; i++){
			  population[i] = new PrototypeSet();
			  for(int j=0; j< population[0].size(); j++){
				  Prototype aux = new Prototype(trainingDataSet.getFromClass(population[0].get(j).getOutput(0)).getRandom());
				  population[i].add(aux);
			  }
			  
			  nominalPopulation = new PrototypeSet();
		      nominalPopulation.formatear(population[i]);
		      
			  fitness[i] = classficationAccuracy1NN(population[i],trainingDataSet);   // PSOfitness
			  fitness_bestPopulation[i] = fitness[i]; // Initially the same fitness.
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
		   
		   
		   // Initially the Scaling Factor and crossover for each Individual are randomly generated between 0 and 1.
		   
		   for(int i=0; i< this.PopulationSize; i++){
			   ScalingFactor[i] =  RandomGenerator.Randdouble(0, 1);
			   CrossOverRate[i] =  RandomGenerator.Randdouble(0, 1);
		   }
		   
		   
		  	   
		   double randj[] = new double[5];
		   
		   
		   for(int iter=0; iter< MaxIter; iter++){ // Main loop
			      
			   for(int i=0; i<PopulationSize; i++){

				   // Generate randj for j=1 to 5.
				   for(int j=0; j<5; j++){
					   randj[j] = RandomGenerator.Randdouble(0, 1);
				   }
				   
						   
	    			   	    
				   
				   if(i==bestFitnessIndex && randj[4] < tau[2]){
					  // System.out.println("SFGSS applied");
					   //SFGSS
					   crossover[i] = SFGSS(population, i, bestFitnessIndex, CrossOverRate[i]);
					   
					   
				   }else if(i==bestFitnessIndex &&  tau[2] <= randj[4] && randj[4] < tau[3]){
					   //SFHC
					   //System.out.println("SFHC applied");
					   crossover[i] = SFHC(population, i, bestFitnessIndex, ScalingFactor[i], CrossOverRate[i]);
					   
				   }else {
					   
					   // Fi update
					   
					   if(randj[1] < tau[0]){
						   ScalingFactor[i] = this.Fl + this.Fu*randj[0];
					   }
					   
					   // CRi update
					   
					   if(randj[3] < tau[1]){
						   CrossOverRate[i] = randj[2];
					   }
					   				   
					   // Mutation Operation.
					   
					   mutation[i] = new PrototypeSet(population[i].size());
				   
					  //Mutation:
						
					   mutation[i]  = mutant(population, i, bestFitnessIndex, ScalingFactor[i]);
					   
					    // Crossver Operation.

					   crossover[i] = new PrototypeSet(population[i]);
					   
					   for(int j=0; j< population[i].size(); j++){ // For each part of the solution
						   
						   double randNumber = RandomGenerator.Randdouble(0, 1);
							   
						   if(randNumber<CrossOverRate[i]){
							   crossover[i].set(j, mutation[i].get(j)); // Overwrite.
						   }
					   }
					   
					   
					   
					   
				   }
				   
	   
				   
				   // Fourth: Selection Operation.
			   
				   nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(population[i]);
			       fitness[i] = classficationAccuracy1NN(nominalPopulation,trainingDataSet);
			       
			       nominalPopulation = new PrototypeSet();
			       nominalPopulation.formatear(crossover[i]);
			       
				   double trialVector = classficationAccuracy1NN(nominalPopulation,trainingDataSet);
				
			  
				  if(trialVector > fitness[i]){
					  population[i] = new PrototypeSet(crossover[i]);
					  fitness[i] = trialVector;
				  }
				  
				  if(fitness[i]>bestFitness){
					  bestFitness = fitness[i];
					  bestFitnessIndex=i;
				  }
				  
				  
			   }

			   
		   }

		   
			   nominalPopulation = new PrototypeSet();
	           nominalPopulation.formatear(population[bestFitnessIndex]);
			   System.err.println("\n% de acierto en training Nominal " + classficationAccuracy1NN(nominalPopulation,trainingDataSet) );
				  
				//  nominalPopulation.print();

	  
			return nominalPopulation;
	  }
  
  
  
  /* MEzcla de algoritmos */
  public void ejecutar () {

	  int i, j, l, m;
	    int nClases;
	    int claseObt;
	    boolean marcas[];
	    int nSel = 0;
	    double conjS[][];
	    double conjR[][];
	    int conjN[][];
	    boolean conjM[][];
	    int clasesS[];
	    double minDistEnemigo[];
	    double dist;
	    int reachable[];
	    int coverage[];
	    boolean progresa;

	    long tiempo = System.currentTimeMillis();

	    /*Getting the number of differents classes*/
	    nClases = 0;
	    for (i=0; i<clasesTrain.length; i++)
	      if (clasesTrain[i] > nClases)
	        nClases = clasesTrain[i];
	    nClases++;

	    /*Inicialization of the flagged instances vector from the S, reachable and coverage sets*/
	    marcas = new boolean[datosTrain.length];
	    reachable = new int[datosTrain.length];
	    coverage = new int[datosTrain.length];
	    for (i=0; i<datosTrain.length; i++) {
	      marcas[i] = true;
	      reachable[i] = 0;
	      coverage[i] = 0;
	    }
	    nSel = datosTrain.length;

	    /*Inicialization of the matrix of minimum distences of the enemys used for see the
	     adaptability of the instance*/
	    minDistEnemigo = new double[datosTrain.length];
	    for (i=0; i<datosTrain.length; i++) {
	      minDistEnemigo[i] = Double.POSITIVE_INFINITY;
	      for (j=0; j<datosTrain.length; j++) {
	        dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
	        if (clasesTrain[i] != clasesTrain[j] && dist < minDistEnemigo[i])
	          minDistEnemigo[i] = dist;
	      }
	    }

	    /*Body of the ICF algorithm. First, apply the Wilson filter; then, get the reachable and coverage
	     sets for each instance and compare its sizes for descarting. This process is repited until there is
	     not more descarts.*/
	    for (i=0; i<datosTrain.length; i++) {
	      /*Apply ENN*/
	      claseObt = KNN.evaluacionKNN2(k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
	      if (claseObt != clasesTrain[i]) { //incorrect classification, add this instance
	        marcas[i] = false;
	        nSel--;
	      }
	    }

	    do {
	      /*Calculate of reachable and coverage*/
	      for (i=0; i<datosTrain.length; i++) {
	        if (marcas[i]) { //it is in S set
	          coverage[i] = getCoverage (i, marcas, minDistEnemigo);
	          reachable[i] = getReachable (i, marcas, minDistEnemigo);
	        }
	      }
	      progresa = false;

	      /*Elimination of instances*/
	      for (i=0; i<datosTrain.length; i++) {
	        if (marcas[i] && reachable[i] > coverage[i]) {
	          marcas[i] = false;
	          nSel--;
	          progresa = true;
	        }
	      }
	    } while (progresa);

	    /*Building of the S set from the flags*/
	    conjS = new double[nSel][datosTrain[0].length];
	    conjR = new double[nSel][datosTrain[0].length];
	    conjN = new int[nSel][datosTrain[0].length];
	    conjM = new boolean[nSel][datosTrain[0].length];
	    clasesS = new int[nSel];
	    for (m=0, l=0; m<datosTrain.length; m++) {
	      if (marcas[m]) { //the instance will be evaluated
	        for (j=0; j<datosTrain[0].length; j++) {
	          conjS[l][j] = datosTrain[m][j];
	          conjR[l][j] = realTrain[m][j];
	          conjN[l][j] = nominalTrain[m][j];
	          conjM[l][j] = nulosTrain[m][j];
	        }
	        clasesS[l] = clasesTrain[m];
	        l++;
	      }
	    }

	   

	    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
	    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	    
    /** AHORA A�ADO MI DE!! **/
    Parameters.assertBasicArgs(ficheroSalida);
    
    PrototypeGenerationAlgorithm.readParametersFile(this.Script);
    PrototypeGenerationAlgorithm.printParameters();
    
    PrototypeSet training = readPrototypeSet(ficheroSalida[0]);
 //   training.print(); // Conjunto devuelto POR SSMA
    
    trainingDataSet = readPrototypeSet(this.ficheroTraining); // Conjunto inicial
    
    
   // trainingDataSet.print();
     //this.numberOfPrototypes = (int)Math.floor((trainingDataSet.size())*ParticleSize/100.0);
 
     PrototypeSet SADE = reduceSet(training); // LLAMO al SADE
	    SADE.save(ficheroSalida[0]); // Lo guardo
        //Copy the test input file to the output test file
       // KeelFile.copy(inputFilesPath.get(TEST), outputFilesPath.get(TEST));
	    System.out.println("Time elapse:" + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
	    
	    
	    
	    // COn conjS me vale.
        int trainRealClass[][];
        int trainPrediction[][];
                
         trainRealClass = new int[datosTrain.length][1];
		 trainPrediction = new int[datosTrain.length][1];	
                
         //Working on training
         for ( i=0; i<datosTrain.length; i++) {
              trainRealClass[i][0] = clasesTrain[i];
              trainPrediction[i][0] = KNN.evaluate(datosTrain[i],SADE.prototypeSetTodouble(), nClases, SADE.getClases(), 1);
          }
                 
          KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
                 
                 
        //Working on test
		int realClass[][] = new int[datosTest.length][1];
		int prediction[][] = new int[datosTest.length][1];	
		
		//Check  time		
				
		for (i=0; i<realClass.length; i++) {
			realClass[i][0] = clasesTest[i];
			prediction[i][0]= KNN.evaluate(datosTest[i],SADE.prototypeSetTodouble(), nClases, SADE.getClases(), 1);
		}
                
         KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);
         
	    
  }

  /*Function that calculates teh number of elements of the coverage set for an instance*/
  private int getCoverage (int actual, boolean marcas[], double minDistEnemigo[]) {

    int i, suma = 0, adap;

    for (i=0; i<datosTrain.length; i++) {
      adap = 0;
      if (i != actual && marcas[i]) {
        adap = getAdaptable (actual, i, minDistEnemigo);
      }
      suma += adap;
    }

    return suma;
  }

  /*Function that calculates the number of elements of the reachable set for an instance*/
  private int getReachable (int actual, boolean marcas[], double minDistEnemigo[]) {

    int i, suma = 0, adap;

    for (i=0; i<datosTrain.length; i++) {
      adap = 0;
      if (i != actual && marcas[i]) {
        adap = getAdaptable (i, actual, minDistEnemigo);
      }
      suma += adap;
    }

    return suma;
  }

  /*Function that indicates if two instances are adaptables*/
  private int getAdaptable (int x, int y, double minDistEnemigo[]) {

    double dist;

    dist = KNN.distancia(datosTrain[x], realTrain[x], nominalTrain[x], nulosTrain[x], datosTrain[y], realTrain[y], nominalTrain[y], nulosTrain[y], distanceEu);
    if (dist < minDistEnemigo[x])
      return 1;
    else return 0;
  }


  
  public void leerConfiguracion (String ficheroScript) {

    String fichero, linea, token;
    StringTokenizer lineasFichero, tokens;
    byte line[];
    int i, j;

    ficheroSalida = new String[2];

    fichero = Fichero.leeFichero (ficheroScript);
    lineasFichero = new StringTokenizer (fichero,"\n\r");

    lineasFichero.nextToken();
    linea = lineasFichero.nextToken();

    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the name of training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
	for (i=j+1; line[i]!='\"'; i++);
	i++;
	for (j=i; line[j]!='\"'; j++);
	ficheroValidation = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    
    //Parameters.assertBasicArgs(ficheroSalida);
    
    

    
    /*Obtainin the path and the base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the name of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);

    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));
    
    /*Getting the number of neighbors*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    k = Integer.parseInt(tokens.nextToken().substring(1));
    
    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;        
    

    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.PopulationSize = Integer.parseInt(tokens.nextToken().substring(1));
    
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.MaxIter = Integer.parseInt(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.iterSFGSS = Integer.parseInt(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.iterSFHC = Integer.parseInt(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.Fl =  Double.parseDouble(tokens.nextToken().substring(1));
    
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.Fu =  Double.parseDouble(tokens.nextToken().substring(1));
    
    tau = new double[4];
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.tau[0] =  Double.parseDouble(tokens.nextToken().substring(1));
   
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.tau[1] =  Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.tau[2] =  Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.tau[3] =  Double.parseDouble(tokens.nextToken().substring(1));
    
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.Strategy = Integer.parseInt(tokens.nextToken().substring(1));
    
    
    
  }
}
