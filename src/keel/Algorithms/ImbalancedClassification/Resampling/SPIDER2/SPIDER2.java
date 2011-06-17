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

/**
 * <p>
 * File: SPIDER2.java
 * </p>
 *
 * The SPIDER algorithm is an instance selection method used to deal with
 * the imbalanced problem.
 *
 * @author Written by Jose A. Saez (University of Granada) 01/06/2011
 *  
 * @version 0.1
 * @since JDK1.5
 *
 */

package keel.Algorithms.ImbalancedClassification.Resampling.SPIDER2;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import org.core.*;
import java.util.Arrays;
import java.util.StringTokenizer;

public class SPIDER2 extends Metodo {
    /**
     * <p>
     * The SPIDER algorithm is an instance selection method used to deal with
     * the imbalanced problem.
     * </p>
     */

    /*Own parameters of the algorithm*/
    private int k;
    private boolean relabel;
    private String ampl;
    int _posID, _negID;

    /**
     * <p>
     * Constructor of the class. It configures the execution of the algorithm by
     * reading the configuration script that indicates the parameters that are
     * going to be used.
     * </p>
     *
     * @param ficheroScript   Name of the configuration script that indicates the
     * parameters that are going to be used during the execution of the algorithm
     */
    public SPIDER2 (String ficheroScript) {
        super (ficheroScript);
    }

     /**
     * <p>
     * The main method of the class that includes the operations of the algorithm.
     * It includes all the operations that the algorithm has and finishes when it
     * writes the output information into files.
     * </p>
     */
    public void run () {

    int claseObt;
    boolean safe[];
    int nSel = 0;
    
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];

    int nPos = 0;
    int nNeg = 0;
    int tmp;
    int amplify[];
    int neighbours[] = null;

    long tiempo = System.currentTimeMillis();

    /*Count of number of positive and negative examples*/
    for (int i=0; i<clasesTrain.length; i++) {
      if (clasesTrain[i] == 0)
        nPos++;
      else
        nNeg++;
    }
    if (nPos > nNeg) {
      tmp = nPos;
      nPos = nNeg;
      nNeg = tmp;
      _posID = 1;
      _negID = 0;
    } else {
      _posID = 0;
      _negID = 1;
    }
    
    
    //--------------------------------------------------------------------------
    safe = new boolean[datosTrain.length];
    Arrays.fill(safe, false);
    
    amplify = new int[datosTrain.length]; // number of times to be amplified
    Arrays.fill(amplify, 1);
    
    
    for(int i = 0 ; i < datosTrain.length ; ++i){
    	
    	// for each example of the negative class
    	if(clasesTrain[i] == _negID){
    		/*Apply KNN to the instance*/
    	    claseObt = KNN.evaluacionKNN2 (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu);
    	    if (claseObt == clasesTrain[i]) //agree with your majority, it is included in the solution set
    	    	safe[i] = true;
    	}
    }
    
    
    //RS = ejemplos de datosTrain de clase mayoritaria y safe = false
    
    if(relabel){
    	
    	//cambiar clase de ejemplos de RS por la minoritaria
    	for(int i = 0 ; i < datosTrain.length ; ++i){	
    		if(clasesTrain[i] == _negID && safe[i] == false){
    			clasesTrain[i] = _posID;
    		}
    	}
    }
    
    
    for(int i = 0 ; i < datosTrain.length ; ++i){
    	
    	// for each example of the positive class
    	if(clasesTrain[i] == _posID){
    		/*Apply KNN to the instance*/
    		neighbours = new int[k];
    	    claseObt = evaluationKNN_SPIDER2(k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, safe);
    	    if (claseObt == clasesTrain[i]) //agree with your majority, it is included in the solution set
    	    	safe[i] = true;
    	}
    }
    
    if(ampl.equalsIgnoreCase("weak")){
    	
    	for(int i = 0 ; i < datosTrain.length ; ++i){
    		if(clasesTrain[i] == _posID && safe[i] == false){
    			
    			neighbours = new int[k];
    			int n1 = evaluationKNNClass (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _negID, safe);
    			
    			neighbours = new int[k];
    			int n2 = evaluationKNNClass (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _posID, safe);
    			
    			int n = n1-n2+1;// nº vecinos de la clase mayoritaria (max k) - nº vecinos de la clase minoritaria (max k ) + 1 ;

    			amplify[i] += n;
    		}
    	}   	
    }
    
    else if(ampl.equalsIgnoreCase("strong")){
    	
    	for(int i = 0 ; i < datosTrain.length ; ++i){
    		if(clasesTrain[i] == _posID && safe[i] == false){
    	
    		     neighbours = new int[k+2];
    			 claseObt = evaluationKNN_SPIDER2 (k+2, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, safe);
    	    	 
    			 if (claseObt == clasesTrain[i]){
    				 neighbours = new int[k];
    				 int n1 = evaluationKNNClass (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _negID, safe);
    	    			
    				 neighbours = new int[k];
    				 int n2 = evaluationKNNClass (k, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _posID, safe);
    	    			
    				 int n = (n1-n2)+1;// nº vecinos de la clase mayoritaria (max k) - nº vecinos de la clase minoritaria (max k ) + 1 ;

    	    		 amplify[i] += n;	 
    			 }
    			 
    			 else{
    				 neighbours = new int[k+2];
    				 int n1 = evaluationKNNClass (k+2, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _negID, safe);
    	    			
    				 neighbours = new int[k+2];
    				 int n2 = evaluationKNNClass (k+2, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu, neighbours, _posID, safe);
    	    			
    				 int n = n1-n2+1;// nº vecinos de la clase mayoritaria (max k) - nº vecinos de la clase minoritaria (max k ) + 1 ;

    	    		 amplify[i] += n;
    			 }
    		}
    	}
    	
    }
    
    //--------------------------------------------------------------------------------------

    nSel = 0;
    for (int i = 0; i < datosTrain.length; i++) {
       if ((clasesTrain[i] == _posID) || (clasesTrain[i] == _negID && safe[i] == true))
         nSel += amplify[i];
    }
    
    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    
    int acumulados = 0;
    
    for (int i=0; i<datosTrain.length; ++i) {
      if ((clasesTrain[i] == _posID) || (clasesTrain[i] == _negID && safe[i] == true)) { //the instance will be copied to the solution
        
    	  for (int t = 0; t < amplify[i] ; t++){
    		  
    		  for (int j=0; j<datosTrain[0].length; j++) {
    			  conjS[acumulados+t][j] = datosTrain[i][j];
    			  conjR[acumulados+t][j] = realTrain[i][j];
    			  conjN[acumulados+t][j] = nominalTrain[i][j];
    			  conjM[acumulados+t][j] = nulosTrain[i][j];
    		  }
           
    		  clasesS[acumulados+t] = clasesTrain[i];
        }
    	  
    	  acumulados += amplify[i];
      }
    }

    System.out.println("SPIDER2 "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }


    /**
     * <p>
     * Computes the k nearest neighbors of a given item belonging to a fixed class.
     * With that neighbors a suggested class for the item is returned.
     * </p>
     *
     * @param nvec  Number of nearest neighbors that are going to be searched
     * @param conj  Matrix with the data of all the items in the dataset
     * @param real  Matrix with the data associated to the real attributes of the dataset
     * @param nominal   Matrix with the data associated to the nominal attributes of the dataset
     * @param nulos Matrix with the data associated to the missing values of the dataset
     * @param clases    Array with the associated class for each item in the dataset
     * @param ejemplo   Array with the data of the specific item in the dataset used
     * as a reference in the nearest neighbor search
     * @param ejReal    Array with the data of the real attributes of the specific item in the dataset
     * @param ejNominal Array with the data of the nominal attributes of the specific item in the dataset
     * @param ejNulos   Array with the data of the missing values of the specific item in the dataset
     * @param nClases   Class of the specific item in the dataset
     * @param distance  Kind of distance used in the nearest neighbors computation.
     * If true the distance used is the euclidean, if false the HVMD distance is used
     * @param vecinos   Array that will have the nearest neighbours id for the current specific item
     * @param clase Class of the neighbours searched for the item
     * @return the majority class for all the neighbors of the item
     */
    public int evaluationKNNClass (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, int vecinos[], int clase, boolean[] isSafe) {

            int i, j, l;
            boolean parar = false;
            int vecinosCercanos[];
            double minDistancias[];
            int votos[];
            double dist;

            if (nvec > conj.length)
            	nvec = conj.length;

            votos = new int[nClases];
            vecinosCercanos = new int[nvec];
            minDistancias = new double[nvec];
            for (i=0; i<nvec; i++){
            	vecinosCercanos[i] = -1;
                minDistancias[i] = Double.POSITIVE_INFINITY;
            }

            for (i=0; i<conj.length; i++) {
            	
            	if(isSafe[i] || clases[i] == _posID){
            		
                    dist = KNN.distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
                    if (dist > 0) {
                            parar = false;
                            for (j = 0; j < nvec && !parar; j++) {
                                    if (dist < minDistancias[j]) {
                                            parar = true;
                                            for (l = nvec - 1; l >= j+1; l--) {
                                                    minDistancias[l] = minDistancias[l - 1];
                                                    vecinosCercanos[l] = vecinosCercanos[l - 1];
                                            }
                                            minDistancias[j] = dist;
                                            vecinosCercanos[j] = i;
                                    }
                            }
                    }
                    
            	}
            }

            for (j=0; j<nClases; j++) {
            	votos[j] = 0;
            }

            for (j=0; j<nvec; j++) {
            	if (vecinosCercanos[j] >= 0)
            		votos[clases[vecinosCercanos[j]]]++;
            }
            
            for (i=0; i<vecinosCercanos.length; i++)
                vecinos[i] = vecinosCercanos[i];
            
            return votos[clase];
    }

    
    
    public int evaluationKNN_SPIDER2 (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, int vecinos[], boolean[] isSafe) {

        int i, j, l;
        boolean parar = false;
        int vecinosCercanos[];
        double minDistancias[];
        int votos[];
        double dist;
        int votada, votaciones;

        if (nvec > conj.length)
        	nvec = conj.length;

        votos = new int[nClases];
        vecinosCercanos = new int[nvec];
        minDistancias = new double[nvec];
        for (i=0; i<nvec; i++){
        	vecinosCercanos[i] = -1;
            minDistancias[i] = Double.POSITIVE_INFINITY;
        }

        for (i=0; i<conj.length; i++) {
        	
        	if(isSafe[i] || clases[i] == _posID){
        		
                dist = KNN.distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
                if (dist > 0) {
                        parar = false;
                        for (j = 0; j < nvec && !parar; j++) {
                                if (dist < minDistancias[j]) {
                                        parar = true;
                                        for (l = nvec - 1; l >= j+1; l--) {
                                                minDistancias[l] = minDistancias[l - 1];
                                                vecinosCercanos[l] = vecinosCercanos[l - 1];
                                        }
                                        minDistancias[j] = dist;
                                        vecinosCercanos[j] = i;
                                }
                        }
                }
                
        	}
        }

        for (j=0; j<nClases; j++) {
            votos[j] = 0;
        }

        for (j=0; j<nvec; j++) {
            if (vecinosCercanos[j] >= 0)
                    votos[clases[vecinosCercanos[j]]] ++;
        }

        votada = 0;
        votaciones = votos[0];
        for (j=1; j<nClases; j++) {
            if (votaciones < votos[j]) {
                    votaciones = votos[j];
                    votada = j;
            }
        }

        for (i=0; i<vecinosCercanos.length; i++)
            vecinos[i] = vecinosCercanos[i];

        return votada;
    }

 
    

  /**
   * <p>
   * Obtains the parameters used in the execution of the algorithm and stores
   * them in the private variables of the class
   * </p>
   *
   * @param ficheroScript Name of the configuration script that indicates the
   * parameters that are going to be used during the execution of the algorithm
   */
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
    
    /*Getting the relabeling option*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    relabel = tokens.nextToken().substring(1).equalsIgnoreCase("true")?true:false;
        
    /*Getting the ampl option*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    ampl = tokens.nextToken().substring(1);
}

  
}
