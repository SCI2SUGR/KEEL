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
//  ZhangTS.java
//
//  Salvador García López
//
//  Created by Salvador García López 30-7-2004.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.ZhangTS;

import keel.Algorithms.Preprocess.Basic.*;
import java.util.StringTokenizer;
import java.util.Vector;
import org.core.*;

public class ZhangTS extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private int tabuSize;
  private double t;

  public ZhangTS (String ficheroScript) {
    super (ficheroScript);
  }

  public void ejecutar () {

    int i, j, k, l;
    int nClases;
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    int nSel = 0;
    Cromosoma Snext, Scurr, Sbest;
    boolean temp[];
    Cromosoma TL[];
    int posTL, NTL;
    Vector <Integer> movs = new Vector <Integer>();
    int mov;
    int fin = 0;

    long tiempo = System.currentTimeMillis();

    /*Getting the number of differents classes*/
    nClases = 0;
    for (i=0; i<clasesTrain.length; i++)
      if (clasesTrain[i] > nClases)
        nClases = clasesTrain[i];
    nClases++;

    Randomize.setSeed (semilla);

    /*Generation of a random solution*/
    Scurr = new Cromosoma (datosTrain.length);
    Scurr.evaluaError(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, distanceEu);

    /*Inicialization of Sbest*/
    temp = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      temp[i] = true;
    Sbest = new Cromosoma (temp);
    Sbest.evaluaError(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, distanceEu);

    /*Inicialization of the tabu list*/
    TL = new Cromosoma[tabuSize];
    posTL = 0;
    NTL = 0;
    k = 0;

    while (fin < 100) {
      fin++;
      /*Calculate forbbiden moves according to tabu list*/
      for (i=0; i<NTL; i++) {
        mov = Scurr.differenceAtOne(TL[i]);
        if (mov >= 0)
          movs.addElement(new Integer(mov));
      }

      /*Search for the best neighbor en N+ or N+ depending on the error threshold*/
      if (Scurr.getErrorRate() > t) {
        Snext = Scurr.getSnextNplus(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, distanceEu, movs);
      } else {
        Snext = Scurr.getSnextNminus(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, distanceEu, movs);
      }
      movs.removeAllElements();

      Snext.evaluaError(datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, distanceEu);
      Scurr = new Cromosoma (datosTrain.length, Snext);
      if ((Scurr.getErrorRate() <= t && Scurr.genesActivos() < Sbest.genesActivos()) || (Scurr.genesActivos() == Sbest.genesActivos() && Scurr.getErrorRate() < Sbest.getErrorRate())) {
        Sbest = new Cromosoma (datosTrain.length, Scurr);
        fin = 0;
      }
      k = k + 1;
      TL[posTL] = new Cromosoma (datosTrain.length, Scurr);
      posTL++;
      posTL %= tabuSize;
      if (NTL < tabuSize)
        NTL++;
    }

    nSel = Sbest.genesActivos();

    /*Building of teh S set from the best cromosome obtained*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (Sbest.getGen(i)) { //the instance must be copied to the solution
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

    System.out.println("ZhangTS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

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

    /*Getting the names of training and test files*/
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
    
    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

    /*Getting the size of the tabu list*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    tabuSize = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the error threshold*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    t = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
  }
}
