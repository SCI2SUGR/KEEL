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
//  SSMA.javA  HIBRIDO  PSO

//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-10-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.DROP3PSO;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.utilities.*;
//import keel.Algorithms.Instance_Generation.utilities.KNN.KNN;

import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class DROP3PSO extends Metodo {

  /*Own parameters of the algorithm*/
  private int k;
 private double semilla;
 
  public String Script; // para releer par�metros..
  private PrototypeSet trainingDataSet;
  private PrototypeGenerator generador;
  //Par�metros PSO
  private int SwarmSize; // SwarmSize == P
  private int ParticleSize; // ParticleSize == K  (in the article)
  private int MaxIter; 
  private double C1;
  private double C2;
  private double VMax;
  private double Wstart;
  private double Wend;
  
  protected int numberOfClass;


  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfStrategies; // number of strategies in the pool
  
  public DROP3PSO (String ficheroScript) {
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
   * Generate a reduced prototype set by the PSOGenerator method.
   * @return Reduced set by PSOGenerator's method.
   */
  
  public PrototypeSet reduceSet(PrototypeSet initial)
  {
	  System.out.print("\nThe algorithm is starting...\n Computing...\n");

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
	  
	  //Initialization.
	  
	  population[0]= new PrototypeSet(initial) ;
	  generador = new PrototypeGenerator(trainingDataSet);
	   nominalPopulation = new PrototypeSet();
	   nominalPopulation.formatear(population[0]);
     fitness[0] = classficationAccuracy1NN(nominalPopulation,trainingDataSet);
	      
		  this.numberOfClass = trainingDataSet.getPosibleValuesOfOutput().size();
	  
  
      System.out.println("Best initial fitness = "+ fitness[0]);
      
      fitness_bestPopulation[0]  = fitness[0];

      for(int i=1; i< SwarmSize; i++){
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
				  for(int k = 0; k<num_atribs;k++){
					  
					  
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
						   velocidad[i][k][j]=-VMax;      // absolute value. �? or -VMax , Vmax. ?
						   
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
					  fitness[i] = absoluteclassficationAccuracy1NNKNN(population[i], trainingDataSet,k);  // PSO fitness
				  else
					 fitness[i] = absoluteclassficationAccuracy1NNKNN(population[i],trainingDataSet,population[i].size());
			  */
				
				  // Antes de calcular el fitness, tengo que "transformar los datos nominales.."
				   nominalPopulation = new PrototypeSet();
					  nominalPopulation.formatear(population[i]);
					  fitness[i] = classficationAccuracy1NN(nominalPopulation,trainingDataSet);
				  //fitness[i] = classficationAccuracy1NN(population[i],trainingDataSet);
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
	   
	  
	   System.err.println("Best Fitness "+ bestFitness);
	   nominalPopulation = new PrototypeSet();
       nominalPopulation.formatear(mejorPosicion[bestFitnessIndex]);

	  System.err.println("\n% de acierto en training Nominal " + classficationAccuracy1NN(nominalPopulation,trainingDataSet));
		
		   
      return nominalPopulation;
  }
  
  
  /* MEzcla de algoritmos */
  
  public void ejecutar () {

	    int i, j, l, m, n, o;
	    int nClases;
	    int claseObt;
	    boolean marcas[];
	    int nSel;
	    double conjS[][];
	    double conjR[][];
	    int conjN[][];
	    boolean conjM[][];
	    int clasesS[];
	    int vecinos[][];
	    Vector asociados[];
	    int aciertosSin;
	    int vecinosTemp[];
	    double distTemp[];
	    double dist, bestD;
	    boolean parar;
	    Referencia orden[];
	    int mayoria;

	    long tiempo = System.currentTimeMillis();

	    /*Getting the number of different classes*/
	    nClases = 0;
	    for (i=0; i<clasesTrain.length; i++)
	      if (clasesTrain[i] > nClases)
	        nClases = clasesTrain[i];
	    nClases++;

	    /*Inicialization of the instance flagged vector of the S set*/
	    marcas = new boolean[datosTrain.length];
	    for (i=0; i<datosTrain.length; i++) {
	      marcas[i] = true;
	    }
	    nSel = datosTrain.length;

	    /*Do ENN before sorting*/
	    for (i=0; i<datosTrain.length; i++) {
	      claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
	      if (claseObt != clasesTrain[i]) { //is included in the solution set if it is agree with your mayority
	        marcas[i] = false;
	        nSel--;
	      }
	    }

	    /*Construction of an instance vector with distances to the nearest enemy*/
	    orden = new Referencia[datosTrain.length];
	    for (i=0; i<datosTrain.length; i++) {
	      bestD = Double.POSITIVE_INFINITY;
	      for (j=0; j<datosTrain.length; j++) {
	        if (clasesTrain[i] != clasesTrain[j]) {
	          dist = KNN.distancia (datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
	          if (dist < bestD)
	            bestD = dist;
	        }
	      }
	      orden[i] = new Referencia (i, bestD);
	    }

	    /*Sorting the previous vector*/
	    Arrays.sort(orden);

	    /*Inicialization of the data structures of neighbors and associates*/
	    distTemp = new double[k+1];
	    vecinosTemp = new int[k+1];
	    vecinos = new int[datosTrain.length][k+1];
	    asociados = new Vector[datosTrain.length];
	    for (i=0; i<datosTrain.length; i++)
	      asociados[i] = new Vector ();

	    /*Body of the algorithm DROP3 (same as DROP2).*/
	    for (i=0; i<datosTrain.length; i++) {
	      /*Get the k+1 nearest neighbors of each instance*/
	      if (marcas[i]) {
	        KNN.evaluacionKNN2 (k+1, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos[i]);
	        for (j=0; j<vecinos[i].length; j++) {
	          if (vecinos[i][j] >= 0)
	        	  asociados[vecinos[i][j]].addElement (new Referencia (i,0));
	        }
	      }
	    }

	    /*Check if delete or not the instances considering the WITH and WITHOUT sets*/
	    for (o=0; o<datosTrain.length; o++){
	      i = orden[o].entero;
	      if (marcas[i]) { //only for instances haven�t noise filtered
	        aciertosSin = 0;

	        marcas[i] = false;
	        nSel--;
	        /*Construction of S set from the temporaly flags*/
	        conjS = new double[nSel][datosTrain[0].length];
	        conjR = new double[nSel][datosTrain[0].length];
	        conjN = new int[nSel][datosTrain[0].length];
	        conjM = new boolean[nSel][datosTrain[0].length];
	        clasesS = new int[nSel];
	        for (m=0, l=0; m<datosTrain.length; m++) {
	          if (marcas[m]) { //the instance will evaluate
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

	        marcas[i] = true;
	        nSel++;

	        /*Evaluation of associates without the instance in T*/
	        for (j=0; j<k+1; j++) {
	          if (vecinos[i][j] >= 0) {
	        	  claseObt = KNN.evaluacionKNN2 (k, conjS, conjR, conjN, conjM, clasesS, datosTrain[vecinos[i][j]], realTrain[vecinos[i][j]], nominalTrain[vecinos[i][j]], nulosTrain[vecinos[i][j]], nClases, distanceEu);
	        	  if (claseObt == clasesTrain[vecinos[i][j]])  //classify it correctly
	        		  aciertosSin++;
	          }
	        }

	        mayoria = (k+1) / 2;
	        if (aciertosSin > mayoria) {
	          /*Delete P from S*/
	          marcas[i] = false;
	          nSel--;

	          /*For each associate of P, search a new nearest neighbor*/
	          for (j=0; j<asociados[i].size(); j++) {
	            for (l=0; l<k+1; l++) {
	              vecinosTemp[l] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l];
	              vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l] = -1;
	              distTemp[l] = Double.POSITIVE_INFINITY;
	            }
	            for (l=0; l<datosTrain.length; l++) {
	              if (marcas[l]) { //it is in S
	                dist = KNN.distancia (datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], realTrain[((Referencia)(asociados[i].elementAt(j))).entero], nominalTrain[((Referencia)(asociados[i].elementAt(j))).entero], nulosTrain[((Referencia)(asociados[i].elementAt(j))).entero], datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], distanceEu);
	                parar = false;

	                /*Calculate the nearest neighbors in this situation again*/
	                for (m=0; m<(k+1) && !parar; m++) {
	                  if (dist < distTemp[m]) {
	                    parar = true;
	                    for (n=m+1; n<k+1; n++) {
	                      distTemp[n] = distTemp[n-1];
	                      vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n] = vecinos[((Referencia)(asociados[i].elementAt(j))).entero][n-1];
	                    }
	                    distTemp[m] = dist;
	                    vecinos[((Referencia)(asociados[i].elementAt(j))).entero][m] = l;
	                  }
	                }
	              }
	            }

	            /*Add to the list of associates of the new neighbor this instance*/
	            for (l=0; l<k+1; l++) {
	              parar = false;
	              for (m=0; m<asociados[vecinosTemp[l]].size() && !parar; m++) {
	                if (((Referencia)(asociados[vecinosTemp[l]].elementAt(m))).entero == ((Referencia)(asociados[i].elementAt(j))).entero
	                    && vecinosTemp[l] != i) {
	                  asociados[vecinosTemp[l]].removeElementAt(m);
	                  parar = true;
	                }
	              }
	            }
	            for (l=0; l<k+1; l++) {
	              asociados[vecinos[((Referencia)(asociados[i].elementAt(j))).entero][l]].addElement(new Referencia (((Referencia)(asociados[i].elementAt(j))).entero,0));
	            }
	          }
	        }
	      }
	    }

	    /*Construction of the S set from the flags*/
	    conjS = new double[nSel][datosTrain[0].length];
	    conjR = new double[nSel][datosTrain[0].length];
	    conjN = new int[nSel][datosTrain[0].length];
	    conjM = new boolean[nSel][datosTrain[0].length];
	    clasesS = new int[nSel];
	    for (m=0, l=0; m<datosTrain.length; m++) {
	      if (marcas[m]) { //the instance will evaluate
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

	    System.out.println("DROP3 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

	    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
	    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	    
	    /** AHORA A�ADO MI PSO!! **/
	    Parameters.assertBasicArgs(ficheroSalida);
	    
	    PrototypeGenerationAlgorithm.readParametersFile(this.Script);
	    PrototypeGenerationAlgorithm.printParameters();
	    
	    PrototypeSet training = readPrototypeSet(ficheroSalida[0]);
	 //   training.print(); // Conjunto devuelto POR SSMA
	    
	    trainingDataSet = readPrototypeSet(this.ficheroTraining); // Conjunto inicial
	    
	    
	     PrototypeSet SADE = reduceSet(training); // LLAMO al SADE
	    SADE.save(ficheroSalida[0]); // Lo guardo
	    
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
    this.SwarmSize = Integer.parseInt(tokens.nextToken().substring(1));
    
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.MaxIter = Integer.parseInt(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.C1 = Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.C2 = Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.VMax = Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.Wstart = Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.Wend = Double.parseDouble(tokens.nextToken().substring(1));
    
    
    System.out.print("\nIsaac dice:   Swar= "+SwarmSize+ " Maxiter= "+ MaxIter+" Wend=  "+this.Wend+ "\n");

    
    
    
  }
}
