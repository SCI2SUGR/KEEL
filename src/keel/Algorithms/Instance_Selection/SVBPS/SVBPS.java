/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
//  SVBPS.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 16-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.SVBPS;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;
import org.libsvm.*;

public class SVBPS extends Metodo {

  /*Own parameters of the algorithm*/
  private int k;
  private String kernelType;
  private double C;
  private double eps;
  private int degree;
  private double gamma;
  private double nu;
  private double p;
  private int shrinking;

  public SVBPS (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l, m, n, o;
    int nClases;
	svm_parameter SVMparam= new svm_parameter();
	svm_problem SVMp = null;
	svm_model svr = null;
	double exTmp[];
    boolean marcas[];
    boolean coincide, igual;
    Instance inst;
    int nSel;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    Referencia orden[];
    int vecinos[][];
    Vector asociados[];
    double dist, bestD;
    int vecinosTemp[];
    double distTemp[];
    int aciertosSin;
    int claseObt;
    int mayoria;
    boolean parar;

    long tiempo = System.currentTimeMillis();
    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;
    
	//SVM PARAMETERS
	SVMparam.C = C;
	SVMparam.cache_size = 10; //10MB of cache
	SVMparam.degree = degree;
	SVMparam.eps = eps;
	SVMparam.gamma = gamma;
	SVMparam.nr_weight = 0;
	SVMparam.nu = nu;
	SVMparam.p = p;
	SVMparam.shrinking = shrinking;
	SVMparam.probability = 0;
	if(kernelType.compareTo("LINEAR")==0){
		SVMparam.kernel_type = svm_parameter.LINEAR;
	}else if(kernelType.compareTo("POLY")==0){
		SVMparam.kernel_type = svm_parameter.POLY;
	}else if(kernelType.compareTo("RBF")==0){
		SVMparam.kernel_type = svm_parameter.RBF;
	}else if(kernelType.compareTo("SIGMOID")==0){
		SVMparam.kernel_type = svm_parameter.SIGMOID;
	}
	SVMparam.svm_type = svm_parameter.C_SVC;
	
	SVMp = new svm_problem();
	SVMp.l = datosTrain.length;
	SVMp.y = new double[SVMp.l];
	SVMp.x = new svm_node[SVMp.l][datosTrain[0].length+1];
	for(i=0;i<SVMp.l;i++)
		for(j=0;j<Attributes.getInputNumAttributes()+1;j++)
			SVMp.x[i][j] = new svm_node();
	
	for (i=0; i<datosTrain.length; i++) {
		SVMp.y[i] = clasesTrain[i];
		for (j=0; j < Attributes.getInputNumAttributes(); j++){
			SVMp.x[i][j].index = j;
			SVMp.x[i][j].value = datosTrain[i][j];
		}
		//end of instance
		SVMp.x[i][Attributes.getInputNumAttributes()].index = -1;
	}
	
	if(svm.svm_check_parameter(SVMp, SVMparam)!=null){
		System.err.print("SVM parameter error in training: ");
		System.err.println(svm.svm_check_parameter(SVMp, SVMparam));
		System.exit(-1);
	}
	
	//Train the SVM
	svr = svm.svm_train(SVMp, SVMparam);
	exTmp = new double[datosTrain[0].length];
	marcas = new boolean[datosTrain.length];
	Arrays.fill(marcas, false);
	nSel = 0;
	for (i=0; i<svr.getSV().length; i++) {
		for (j=0; j<svr.getSV()[i].length-1; j++) {
			exTmp[j] = svr.getSV()[i][j].value;
		}
		coincide = false;
		for (j=0; j<datosTrain.length && !coincide; j++) {
			igual = true;
			for (l=0; l<datosTrain[j].length && igual; l++) {
				if (exTmp[l] != datosTrain[j][l]) {
					igual = false;
				}
			}
			if (igual) {
				marcas[j] = true;
				nSel++;
				coincide = true;
			}
		}
	}

    /*Building an instance vector with distances to the nearest enemy*/
    orden = new Referencia[datosTrain.length];
    for (i=0; i<datosTrain.length; i++) {
      bestD = Double.POSITIVE_INFINITY;
      for (j=0; j<datosTrain.length; j++) {
        if (clasesTrain[i] != clasesTrain[j]) {
          dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
          if (dist < bestD)
            bestD = dist;
        }
      }
      orden[i] = new Referencia (i, bestD);
    }

    /*Sort the previos vector*/
    Arrays.sort(orden);

    /*Inicialization of data structures of neighbors and associates*/
    distTemp = new double[k+1];
    vecinosTemp = new int[k+1];
    vecinos = new int[datosTrain.length][k+1];
    asociados = new Vector[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      asociados[i] = new Vector ();

    /*Body of the DROP2 algorithm. It calculates, for each instance, a set of associates instances
     and look if the deletion of the main instance produces a change of accuracy in those associates*/
    for (i=0; i<datosTrain.length; i++) {
      /*Calculate the k+1 nearest neighbors of each instance*/
      KNN.evaluacionKNN2(k+1, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], nClases, distanceEu, vecinos[i]);
      for (j=0; j<vecinos[i].length; j++) {
    	  asociados[vecinos[i][j]].addElement (new Referencia (i,0));
      }
    }

    /*Check if deleting or not the instances considering the WITH and WITHOUT sets*/
    for (o=0; o<datosTrain.length; o++){
    	i = orden[o].entero;
      
    	if (marcas[i]) {
    		aciertosSin = 0;
    		marcas[i] = false;
    		nSel--;
    	  
    		/*Construction of S set from the temporal flags*/
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

    		marcas[i] = true;
    		nSel++;

    		/*Evaluation of associates without the instance in T*/
    		for (j=0; j<k+1; j++) {
    			claseObt = KNN.evaluacionKNN2(k, conjS, conjR, conjN, conjM, clasesS, datosTrain[vecinos[i][j]], realTrain[vecinos[i][j]], nominalTrain[vecinos[i][j]], nulosTrain[vecinos[i][j]], nClases, distanceEu);
    			if (claseObt == clasesTrain[vecinos[i][j]])  //it classify it correctly
    				aciertosSin++;
    		}
    		mayoria = (k+1) / 2;
    		if (aciertosSin > mayoria) {
    			/*Delete P of S*/
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
    					if (marcas[l]) { //is in S
    						dist = KNN.distancia(datosTrain[((Referencia)(asociados[i].elementAt(j))).entero], realTrain[((Referencia)(asociados[i].elementAt(j))).entero], nominalTrain[((Referencia)(asociados[i].elementAt(j))).entero], nulosTrain[((Referencia)(asociados[i].elementAt(j))).entero], datosTrain[l], realTrain[l], nominalTrain[l], nulosTrain[l], distanceEu);
    						parar = false;

    						/*Get the nearest neighbors in this situation again*/
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

    /*Construction of S set from the temporal flags*/
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

    System.out.println("SVBPS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, this.k);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, this.k);
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

    /*Getting the names of the training and test files*/
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

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kernelType = tokens.nextToken().substring(1);

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    C = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    eps = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    degree = Integer.parseInt(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    gamma = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nu = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    p = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    shrinking = Integer.parseInt(tokens.nextToken().substring(1));
    
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

 }
}
