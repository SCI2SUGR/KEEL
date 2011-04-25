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

package keel.Algorithms.Instance_Generation.ICFLVQ3;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerator;
import keel.Algorithms.Instance_Generation.Basic.Prototype;
import keel.Algorithms.Instance_Generation.Basic.PrototypeGenerationAlgorithm;
import keel.Algorithms.Instance_Generation.Basic.PrototypeSet;
import keel.Algorithms.Instance_Generation.LVQ.LVQ3;
import keel.Algorithms.Instance_Generation.utilities.*;
//import keel.Algorithms.Instance_Generation.utilities.KNN.KNN;

import keel.Dataset.Attributes;
import keel.Dataset.InstanceAttributes;
import keel.Dataset.InstanceSet;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class ICFLVQ3 extends Metodo {

  /*Own parameters of the algorithm*/
  private int k;
 private double semilla;
 
  public String Script; // para releer par�metros..
  private PrototypeSet trainingDataSet;
  private PrototypeGenerator generador;
  //Par�metros LVQ3: Solo me hacen falta 4;

  private int Maxiter;
  private double alpha0;
  private double windowW;
  private double epsilon;
  
  
  protected int numberOfClass;


  

  protected int numberOfPrototypes;  // Particle size is the percentage
  protected int numberOfStrategies; // number of strategies in the pool
  
  public ICFLVQ3 (String ficheroScript) {
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

	    System.out.println("ICF "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

	    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
	    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	    
	    
	    /** AHORA A�ADO MI PSO!! **/
	    Parameters.assertBasicArgs(ficheroSalida);
	    
	    PrototypeGenerationAlgorithm.readParametersFile(this.Script);
	    PrototypeGenerationAlgorithm.printParameters();
	    
	    PrototypeSet training = readPrototypeSet(ficheroSalida[0]);

	    
	    
	    trainingDataSet = readPrototypeSet(this.ficheroTraining); // Conjunto inicial
	    generador = new PrototypeGenerator(trainingDataSet);
	    
	    
	   // trainingDataSet.print();
	    double initialAcc = classficationAccuracy1NN(training,trainingDataSet);
	    System.out.println("Initial Acc = "+ initialAcc); 
	    
	     PrototypeSet LVQ3 = makeLVQ3Reduction(training, trainingDataSet); // LLAMO al LVQ3
	     
	     
	    PrototypeSet nominalPopulation = new PrototypeSet();
	     nominalPopulation.formatear(LVQ3);
	     initialAcc = classficationAccuracy1NN(nominalPopulation,trainingDataSet);

	     System.out.println("Final Acc = "+ initialAcc); 
	     
	     //LVQ3.print();
		 LVQ3.save(ficheroSalida[0]); // Lo guardo
		 
		 
		    // COn conjS me vale.
	        int trainRealClass[][];
	        int trainPrediction[][];
	                
	         trainRealClass = new int[datosTrain.length][1];
			 trainPrediction = new int[datosTrain.length][1];	
	                
	         //Working on training
	         for ( i=0; i<datosTrain.length; i++) {
	              trainRealClass[i][0] = clasesTrain[i];
	              trainPrediction[i][0] = KNN.evaluate(datosTrain[i],LVQ3.prototypeSetTodouble(), nClases, LVQ3.getClases(), 1);
	          }
	                 
	          KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
	                 
	                 
	        //Working on test
			int realClass[][] = new int[datosTest.length][1];
			int prediction[][] = new int[datosTest.length][1];	
			
			//Check  time		
					
			for (i=0; i<realClass.length; i++) {
				realClass[i][0] = clasesTest[i];
				prediction[i][0]= KNN.evaluate(datosTest[i],LVQ3.prototypeSetTodouble(), nClases, LVQ3.getClases(), 1);
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


  
	  /**
	   * Performs a LVQ3-reduction of the set.
	   * @param w Window width.
	   * @param e Epsilon.
	   * @param iter Number of iterations.
	   * @param Np Number of prototypes to be generated.
	   */
	  private PrototypeSet makeLVQ3Reduction(PrototypeSet InitialSet, PrototypeSet training)
	  {
	      int size = InitialSet.size();
	      
	      
	      LVQ3 lvq3 = new LVQ3(InitialSet,training, this.Maxiter, size, this.alpha0, this.windowW, this.epsilon);
	      PrototypeSet reducedByLVQ3 = lvq3.reduceSet();
	      return reducedByLVQ3;
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
    this.Maxiter = Integer.parseInt(tokens.nextToken().substring(1));
    
    
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.alpha0= Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.windowW = Double.parseDouble(tokens.nextToken().substring(1));
    
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    this.epsilon = Double.parseDouble(tokens.nextToken().substring(1));
    
        
    
    System.out.print("\nIsaac dice:   alpha0= "+this.alpha0+ " Maxiter= "+ this.Maxiter+" epsilon=  "+this.epsilon+ "\n");

    
    
    
  }
}
