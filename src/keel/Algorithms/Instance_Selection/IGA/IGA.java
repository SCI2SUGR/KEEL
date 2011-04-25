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
//  IGA.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 23-3-2006.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.IGA;

import keel.Algorithms.Preprocess.Basic.*;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;
import org.core.*;

public class IGA extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private double pMutacion1to0;
  private double pMutacion0to1;
  private int tamPoblacion;
  private int nEval;
  private double alpha;
  private int kNeigh;

  public IGA (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, l;
    int nClases;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    Cromosoma poblacion[];
    int ev = 0;
    int pos1, pos2;
    Cromosoma newPob[];

    long tiempo = System.currentTimeMillis();

    /*Getting the number of different classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    /*Random inicialization of the poblation*/
    Randomize.setSeed (semilla);
    poblacion = new Cromosoma[tamPoblacion];
    for (i=0; i<tamPoblacion; i++)
      poblacion[i] = new Cromosoma (datosTrain.length);

    /*Initial evaluation of the poblation*/
    for (i=0; i<tamPoblacion; i++)
      poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);

    while (ev < nEval) {
      newPob = new Cromosoma[tamPoblacion];

      for (i=0; i<tamPoblacion-1; i+=2) {
        pos1 = Randomize.Randint(0,tamPoblacion-1);
        do {
          pos2 = Randomize.Randint(0, tamPoblacion-1);
        } while (pos1 == pos2);
        ev += cruceOrtogonal (poblacion, newPob, pos1, pos2, i, ev, nEval, nClases);
      }

      /*Mutation of the cromosomes*/
      for (i=0; i<tamPoblacion; i++)
        newPob[i].mutacion(pMutacion1to0, pMutacion0to1);

        /*Evaluation of the poblation*/
      for (i=0; i<tamPoblacion; i++) {
          poblacion[i].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);
          ev++;
      }

      poblacion = newPob;
    }

    Arrays.sort(poblacion);
    nSel = poblacion[0].genesActivos();

    /*Building of S set from the best cromosome obtained*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (poblacion[0].getGen(i)) { //the instance must be copied to the solution
        for (j=0; j<datosTrain[0].length; j++) {
          conjS[l][j] = datosTrain[i][j];
          conjR[l][j] = realTrain[i][j];
          conjN[l][j] = nominalTrain[i][j];
          conjM[l][j] = nulosTrain[i][j];
        }
        clasesS[l] = clasesTrain[i];
        l++;
      }
    }

    System.out.println("IGA "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
    
    // COn conjS me vale.
    int trainRealClass[][];
    int trainPrediction[][];
            
     trainRealClass = new int[datosTrain.length][1];
	 trainPrediction = new int[datosTrain.length][1];	
            
     //Working on training
     for ( i=0; i<datosTrain.length; i++) {
          trainRealClass[i][0] = clasesTrain[i];
          trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, this.kNeigh);
      }
             
      KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
             
             
    //Working on test
	int realClass[][] = new int[datosTest.length][1];
	int prediction[][] = new int[datosTest.length][1];	
	
	//Check  time		
			
	for (i=0; i<realClass.length; i++) {
		realClass[i][0] = clasesTest[i];
		prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, this.kNeigh);
	}
            
     KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);

  }

  /*Function that implements the uniform crossover between two selected cromosomes*/
  public int cruceOrtogonal (Cromosoma poblacion[], Cromosoma newPob[], int sel1, int sel2, int pos, int nEv, int nEval, int nClases) {

    int i, j;
    int gamma = 0;
    int omega;
    int levels = 2;
    int ortogonal[][];
    double fitness[];
    boolean cuerpo[];
    Vector <Integer> posiciones;
    Cromosoma cTemp;
    int ev = 0;
    double Sjk[][];
    double diff = Double.POSITIVE_INFINITY;
    int posDiff = -1;
    Cromosoma grupo[] = new Cromosoma[4];
    int resto = nEval - nEv;
    int omegaBueno;

    grupo[0] = new Cromosoma(datosTrain.length,poblacion[sel1]);
    grupo[1] = new Cromosoma(datosTrain.length,poblacion[sel2]);

    cuerpo = new boolean[poblacion[sel1].getSize()];
    posiciones = new Vector <Integer>();
    /*Contabilize the number of bits different between parents (Hamming Distance)*/
    for (i=0; i<poblacion[sel1].getSize(); i++) {
      if (poblacion[sel1].getGen(i) != poblacion[sel2].getGen(i)) {
        gamma++;
        posiciones.add(new Integer(i));
      } else {
        cuerpo[i] = poblacion[sel1].getGen(i);
      }
    }
    omega = (int)Math.pow(2,Math.ceil((Math.log(gamma+1)/Math.log(2.0))));
    fitness = new double[omega];

    ortogonal = ortogonalArray (omega,gamma,levels);

    if (resto < omega)
      omegaBueno = resto;
    else
      omegaBueno = omega;

    for (i=0; i<omegaBueno; i++) {
      for (j=0; j<posiciones.size(); j++) {
        if (ortogonal[i][j] == 1)
          cuerpo[((Integer)posiciones.elementAt(j)).intValue()] = poblacion[sel1].getGen(((Integer)posiciones.elementAt(j)).intValue());
        else
          cuerpo[((Integer)posiciones.elementAt(j)).intValue()] = poblacion[sel2].getGen(((Integer)posiciones.elementAt(j)).intValue());
      }
      cTemp = new Cromosoma (cuerpo);
      cTemp.evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);
      ev++;
      fitness[i] = cTemp.getCalidad();
    }

    Sjk = new double[2][posiciones.size()];
    for (i=0; i<posiciones.size(); i++) {
      for (j=0; j<omegaBueno; j++) {
        if (ortogonal[j][i] == 1)
          Sjk[0][i] += fitness[j];
        else
          Sjk[1][i] += fitness[j];
      }
    }

    for (i=0; i<posiciones.size(); i++) {
      if (Sjk[0][i] > Sjk[1][i])
        cuerpo[((Integer)posiciones.elementAt(i)).intValue()] = poblacion[sel1].getGen(((Integer)posiciones.elementAt(i)).intValue());
      else
        cuerpo[((Integer)posiciones.elementAt(i)).intValue()] = poblacion[sel2].getGen(((Integer)posiciones.elementAt(i)).intValue());
      if (Math.abs(Sjk[0][i]-Sjk[1][i]) < diff) {
        diff = Math.abs(Sjk[0][i]-Sjk[1][i]);
        posDiff = i;
      }
    }
    grupo[2] = new Cromosoma (cuerpo);
    grupo[2].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);
    if (posDiff >= 0) {
      cuerpo[((Integer)posiciones.elementAt(posDiff)).intValue()] = !cuerpo[((Integer)posiciones.elementAt(posDiff)).intValue()];
      grupo[3] = new Cromosoma (cuerpo);
      grupo[3].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);
    } else {
      grupo[3] = new Cromosoma (cuerpo);
      grupo[3].evalua(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, alpha, kNeigh, nClases, distanceEu);
    }
    ev+=2;

    Arrays.sort(grupo);

    newPob[pos] = new Cromosoma (datosTrain.length,grupo[0]);
    newPob[pos+1] = new Cromosoma (datosTrain.length,grupo[1]);

    return ev;
  }

  /*This function calculates an Orthogonal Array using the general algorithm*/
  int[][] ortogonalArray (int QJ, int N, int Q) {

    int J;
    int matrix[][];
    int i, j, k, s, t;

    J = (int)(Math.log(QJ)/Math.log(Q));
    matrix = new int[QJ][QJ];

    /*Construction of basic columns*/
    for (k=1; k<=J; k++) {
      j = ((int)(Math.pow(Q,k-1))-1)/(Q-1) + 1;
      for (i=1; i<= QJ; i++) {
        matrix[i-1][j-1] = ((int)(Math.floor(((double)i-1)/(Math.pow(Q,J-k))))) % Q;
      }
    }

    /*Construction of non-basic columns*/
    for (k=2; k<=J; k++) {
      j = ((int)(Math.pow(Q,k-1))-1)/(Q-1) + 1;
      for (s=1; s<=(j-1); s++) {
        for (t=1; t<=(Q-1); t++) {
          for (i=0; i<QJ; i++) {
            matrix[i][j+(s-1)*(Q-1)+t-1] = (matrix[i][s-1]*t+matrix[i][j-1]) % Q;
          }
        }
      }
    }

    for (i=0; i<QJ; i++)
      for (j=0; j<N; j++)
        matrix[i][j]++;

    return matrix;
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

    /*Getting the name of the training and test files*/
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

    /*Getting the names of the output files*/
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

    /*Getting the mutation and cross probability*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    pMutacion1to0 = Double.parseDouble(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    pMutacion0to1 = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the size of the poblation and number of evaluations*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    nEval = Integer.parseInt(tokens.nextToken().substring(1));

    /*Obtain the weight factor values*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    alpha = Double.parseDouble(tokens.nextToken().substring(1));

    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kNeigh = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}
