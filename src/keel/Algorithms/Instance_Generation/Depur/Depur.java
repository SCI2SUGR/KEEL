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
	Depur.java
	Isaac Triguero Velï¿½zquez.
	
	Created by Isaac Triguer o Velï¿½zquez  11-8-2008
	Copyright (c) 2008 __MyCompanyName__. All rights reserved.

*/

package keel.Algorithms.Instance_Generation.Depur;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;

import java.util.StringTokenizer;



/** 
 * @param  k
 * @param  k'
 * @author Isaac Triguero
 * @version 1.0
 */
public class Depur extends Metodo {

  /*Own parameters of the algorithm*/
  
  // We need the variable K to use with k-NN rule
  private int k;
  // In addition, we use a second variable k' to establish the numbers of neighbours
  // that must have the same class.
  private int k2;

  /**
   * Constructor.
   * 
   * @param ficheroScript
   * 
   */
  public Depur (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
	  
    int S[]; /* Binary Vector, to decide if the instance will be included*/
    int i, j, l, cont;
    int nClases;
    int tamS;
    int transformations;
    
    int claseObt[];
    int clasePredominante;


    long tiempo = System.currentTimeMillis();


    transformations=0;
    /*Getting the number of different classes*/

    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    if (nClases < 2) {
      System.err.println("Input dataset is empty");
      nClases = 0;
    }


    /*Algorithm body.
       First, S=TS.
       Then, for each instance of TS, the first step is to repeat the aplication of the k-nn, and then
       we decide if we need to change the label of the instance or we don't need it.
    
     */
    

    /*Inicialization of the candidates set, S=X, where X is the original Training Set*/
    S = new int[datosTrain.length];
    for (i=0; i<S.length; i++)
      S[i] = 1;  /* All included*/
    
    tamS = datosTrain.length;

    
    System.out.print("K= "+k+"\n");
	System.out.print("K'= "+k2+"\n");
    
	
    for(i=0; i<datosTrain.length;i++){
    	
    	
    	/* I need find the k-nn of   i in X - {i}, so I make conjS without i*/
    	conjS = new double[datosTrain.length-1][datosTrain[0].length];
        conjR = new double[datosTrain.length-1][datosTrain[0].length];
        conjN = new int[datosTrain.length-1][datosTrain[0].length];
        conjM = new boolean[datosTrain.length-1][datosTrain[0].length];
        clasesS = new int[datosTrain.length-1];
       
        cont=0;
        for (j = 0; j < datosTrain.length; j++) {
        	
        	if(i!=j){  
        		for (l = 0; l < datosTrain[0].length; l++) {
        	  
	            conjS[cont][l] = datosTrain[j][l];
	            conjR[cont][l] = realTrain[j][l];
	            conjN[cont][l] = nominalTrain[j][l];
	            conjM[cont][l] = nulosTrain[j][l];
	            }
        	
        		clasesS[cont] = clasesTrain[j];
        		cont++; 
       
        	}
        }
        
        
        /*Do KNN to the instance*/
            claseObt = KNN.evaluacionKNN3(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu);
          
            /*
            System.out.print("Las clases de los k vecinos mï¿½s cercanos son\n");
            for(int m=0;m<k;m++){
            	System.out.print(claseObt[m]+ "  ");
            }
            System.out.print("\n-----------------------------------------------\n");
                       
             */
            
        /*Now, we must check that we have at least k2 neighboors with the same class. */
            int max =0;
            clasePredominante = 0;
            
            
            for(int m=0;m<claseObt.length;m++){
            	int claseDeInstancia= claseObt[m];  // Select one class.
            	int iguales=0;
            	
            	for(j=0; j< claseObt.length;j++){	// Check numbers of instances with this class
            		if(j!=m){                       // I can't count the same.
		            	if(claseObt[j]==claseDeInstancia){
		            		iguales++;
		            		
		            	}
            		}
                }
            	
            	// I must check if there is another class with more instances.
            	if(iguales >max){
            		max = iguales;      
            		clasePredominante = claseObt[m];
            	}
            }
            
            //System.out.print("max " + max +"\n");
            //System.out.print("Clase Predominante: "+clasePredominante+"\n");
          
            /* Max+1 = number of neighbours with the same class*/
            if( (max) >= k2 ){
            	/* if there are at least k2 neighbour, we change the class in S, */
            	
            	if(clasePredominante!= clasesTrain[i]) transformations++;
            	
            	clasesTrain[i]=clasePredominante;
            	S[i]=1;
            	
            }else{
            	/* Discard.*/
            	tamS--;
            	S[i] =0;
            } 
            
           
            
    }
    
    System.out.print("S size resultante= " + tamS +"\n");
    System.out.print("Transformations = " + transformations +"\n");

    /*Construction of the S set from the previous vector S*/
    conjS = new double[tamS][datosTrain[0].length];
    conjR = new double[tamS][datosTrain[0].length];
    conjN = new int[tamS][datosTrain[0].length];
    conjM = new boolean[tamS][datosTrain[0].length];
    clasesS = new int[tamS];
    
    cont =0; /* To establish the sets' sizes */
    for (j = 0; j < datosTrain.length; j++) {
    	
    	if(S[j]==1){  /* Checking the instance is included*/
    		for (l = 0; l < datosTrain[0].length; l++) {
    	  
            conjS[cont][l] = datosTrain[j][l];
            conjR[cont][l] = realTrain[j][l];
            conjN[cont][l] = nominalTrain[j][l];
            conjM[cont][l] = nulosTrain[j][l];
            }
    	
    		clasesS[cont] = clasesTrain[j];
    		cont++; 
   
    	}
    }

    System.out.println("Time elapse: "+ (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
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

    /*Getting the names of the training and test files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTraining = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroTest = new String (line,i,j-i);

    /*Getting the path and base name of the results files*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();

    /*Getting the names of output files*/
    line = token.getBytes();
    for (i=0; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[0] = new String (line,i,j-i);
    for (i=j+1; line[i]!='\"'; i++);
    i++;
    for (j=i; line[j]!='\"'; j++);
    ficheroSalida[1] = new String (line,i,j-i);
    


    /*Getting the number of neighbours*/
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     k = Integer.parseInt(tokens.nextToken().substring(1));
     
     
     
     /*Getting the k' */
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     k2 = Integer.parseInt(tokens.nextToken().substring(1));
  
     /*Getting the type of distance function*/
     linea = lineasFichero.nextToken();
     tokens = new StringTokenizer (linea, "=");
     tokens.nextToken();
     distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}

