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
//  SSMA.javA  HIBRIDO LVq3

//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-10-2005.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Generation.SSMALVQ3;

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

public class SSMALVQ3 extends Metodo {

  /*Own parameters of the algorithm*/

  private long semilla;
  private int tamPoblacion;
  private double nEval;
  private double pCross;
  private double pMut;
  private int kNeigh;
 
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
  
  public SSMALVQ3 (String ficheroScript) {
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

    int i, j, l;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    Cromosoma poblacion[];
    double ev = 0;
    double dMatrix[][];
    int sel1, sel2, comp1, comp2;
    Cromosoma hijos[];
    double umbralOpt;
    boolean veryLarge;
    double GAeffort=0, LSeffort=0, temporal;
    double fAcierto=0, fReduccion=0;
    int contAcierto=0, contReduccion=0;
    int nClases;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    if (datosTrain.length > 9000) {
      veryLarge = true;
    } else {
      veryLarge = false;
    }

    if (veryLarge == false) {
      /*Construct a distance matrix of the instances*/
      dMatrix = new double[datosTrain.length][datosTrain.length];
      for (i = 0; i < dMatrix.length; i++) {
        for (j = i + 1; j < dMatrix[i].length; j++) {
          dMatrix[i][j] = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
        }
      }
      for (i = 0; i < dMatrix.length; i++) {
        dMatrix[i][i] = Double.POSITIVE_INFINITY;
      }
      for (i = 0; i < dMatrix.length; i++) {
        for (j = i - 1; j >= 0; j--) {
          dMatrix[i][j] = dMatrix[j][i];
        }
      }
    } else {
      dMatrix = null;
    }

    /*Random inicialization of the population*/
    Randomize.setSeed (semilla);
    poblacion = new Cromosoma[tamPoblacion];
    for (i=0; i<tamPoblacion; i++)
      poblacion[i] = new Cromosoma (kNeigh, datosTrain.length, dMatrix, datosTrain, realTrain, nominalTrain, nulosTrain, distanceEu);

    /*Initial evaluation of the population*/
    for (i=0; i<tamPoblacion; i++) {
      poblacion[i].evaluacionCompleta(nClases, kNeigh, clasesTrain);
    }

    umbralOpt = 0;

    /*Until stop condition*/
    while (ev < nEval) {

      Arrays.sort(poblacion);

      if (fAcierto >= (double)poblacion[0].getFitnessAc()*100.0/(double)datosTrain.length) {
        contAcierto++;
      } else {
        contAcierto=0;
      }
      fAcierto = (double)poblacion[0].getFitnessAc()*100.0/(double)datosTrain.length;

      if (fReduccion >= (1.0-((double)poblacion[0].genesActivos()/(double)datosTrain.length))*100.0) {
        contReduccion++;
      } else {
        contReduccion=0;
      }
      fReduccion = (1.0-((double)poblacion[0].genesActivos()/(double)datosTrain.length))*100.0;

      if (contReduccion >= 10 || contAcierto >= 10){
        if (Randomize.Randint(0,1)==0) {
          if (contAcierto >= 10) {
            contAcierto = 0;
            umbralOpt++;
          } else {
            contReduccion = 0;
            umbralOpt--;
          }
        } else {
          if (contReduccion >= 10) {
            contReduccion = 0;
            umbralOpt--;
          } else {
            contAcierto = 0;
            umbralOpt++;
          }
        }
      }

      /*Binary tournament selection*/
      comp1 = Randomize.Randint(0,tamPoblacion-1);
      do {
        comp2 = Randomize.Randint(0,tamPoblacion-1);
      } while (comp2 == comp1);

      if (poblacion[comp1].getFitness() > poblacion[comp2].getFitness())
        sel1 = comp1;
      else sel1 = comp2;
      comp1 = Randomize.Randint(0,tamPoblacion-1);
      do {
        comp2 = Randomize.Randint(0,tamPoblacion-1);
      } while (comp2 == comp1);
      if (poblacion[comp1].getFitness() > poblacion[comp2].getFitness())
        sel2 = comp1;
      else
        sel2 = comp2;


      hijos = new Cromosoma[2];
      hijos[0] = new Cromosoma (kNeigh, poblacion[sel1], poblacion[sel2], pCross,datosTrain.length);
      hijos[1] = new Cromosoma (kNeigh, poblacion[sel2], poblacion[sel1], pCross,datosTrain.length);
      hijos[0].mutation (kNeigh, pMut, dMatrix, datosTrain, realTrain, nominalTrain, nulosTrain, distanceEu);
      hijos[1].mutation (kNeigh, pMut, dMatrix, datosTrain, realTrain, nominalTrain, nulosTrain, distanceEu);

      /*Evaluation of offsprings*/
      hijos[0].evaluacionCompleta(nClases, kNeigh, clasesTrain);
      hijos[1].evaluacionCompleta(nClases, kNeigh, clasesTrain);
      ev+=2;
      GAeffort += 2;
      temporal = ev;
      if (hijos[0].getFitness() > poblacion[tamPoblacion-1].getFitness() || Randomize.Rand() < 0.0625) {
    	  ev += hijos[0].optimizacionLocal(nClases, kNeigh, clasesTrain,dMatrix,umbralOpt, datosTrain, realTrain, nominalTrain, nulosTrain, distanceEu);
      }
      if (hijos[1].getFitness() > poblacion[tamPoblacion-1].getFitness() || Randomize.Rand() < 0.0625) {
          ev += hijos[1].optimizacionLocal(nClases, kNeigh, clasesTrain,dMatrix,umbralOpt, datosTrain, realTrain, nominalTrain, nulosTrain, distanceEu);
      }

      LSeffort += (ev - temporal);

      /*Replace the two worst*/
      if (hijos[0].getFitness() > poblacion[tamPoblacion-1].getFitness()) {
        poblacion[tamPoblacion-1] = new Cromosoma (kNeigh, datosTrain.length, hijos[0]);
      }
      if (hijos[1].getFitness() > poblacion[tamPoblacion-2].getFitness()) {
        poblacion[tamPoblacion-2] = new Cromosoma (kNeigh, datosTrain.length, hijos[1]);
      }

/*      System.out.println(ev + " - (" + umbralOpt + ")" +
                         (double)poblacion[0].getFitnessAc()*100.0/(double)datosTrain.length + " / " +
                         (double)poblacion[tamPoblacion-1].getFitnessAc()*100.0/(double)datosTrain.length + " - " +
                         (1.0-((double)poblacion[0].genesActivos()/(double)datosTrain.length))*100.0 + " / " +
                         (1.0-((double)poblacion[tamPoblacion-1].genesActivos()/(double)datosTrain.length))*100.0);*/
    }

    Arrays.sort(poblacion);
    nSel = poblacion[0].genesActivos();

    /*Construction of S set from the best cromosome*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (poblacion[0].getGen(i)) { //the instance must be copied to the solution
        for (j=0; j<datosTrain[i].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("SSMA "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
    
    
    /** AHORA A�ADO MI DE!! **/
    Parameters.assertBasicArgs(ficheroSalida);
    
    PrototypeGenerationAlgorithm.readParametersFile(this.Script);
    PrototypeGenerationAlgorithm.printParameters();
    
    PrototypeSet training = readPrototypeSet(ficheroSalida[0]);
    //training.print(); // Conjunto devuelto POR SSMA

    
    
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
     
     LVQ3.print();
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
			prediction[i][0]= KNN.evaluate(datosTest[i],LVQ3.prototypeSetTodouble(), nClases,LVQ3.getClases(), 1);
		}
             
      KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);
      
        
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

    /*Getting the size of the poblation and the number of evaluations*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nEval = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the probabilities of evolutionary operators*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    pCross = Double.parseDouble(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    pMut = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kNeigh = Integer.parseInt(tokens.nextToken().substring(1));
 
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
