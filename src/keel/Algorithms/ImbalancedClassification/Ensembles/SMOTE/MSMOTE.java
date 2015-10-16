/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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
//  SMOTE.java
//
//  Salvador García López
//
//  Created by Salvador García López 30-3-2006.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.ImbalancedClassification.Ensembles.SMOTE;

import java.util.Arrays;
import keel.Algorithms.ImbalancedClassification.Ensembles.Basic.*;
import keel.Dataset.Attribute;

import org.core.*;

import java.util.StringTokenizer;
import keel.Algorithms.ImbalancedClassification.Ensembles.multi_C45;
import keel.Dataset.*;

public class MSMOTE extends Metodo {

  /*Own parameters of the algorithm*/
  private long semilla;
  private int kSMOTE, kClean;
  private int ASMO;
  private boolean balance;
  private double smoting;

  public MSMOTE (String ficheroScript) {
    super (ficheroScript);
  }

  public MSMOTE (InstanceSet IS, long seed, int kClean, int k, int ASMO, boolean bal, double smoting, String distance) {
     int nClases, i, j, l, m, n;
    double VDM;
    int Naxc, Nax, Nayc, Nay;
    double media, SD;
    this.kClean = kClean;

     this.semilla = seed;
     this.training = new InstanceSet(IS);
     this.test = new InstanceSet(IS);
     this.kSMOTE = k;
     this.balance = bal;
     this.smoting = smoting;
     distanceEu = distance.equalsIgnoreCase("Euclidean")?true:false;
     ficheroSalida = new String[2];
     ficheroSalida[0] = multi_C45.outputTr.substring(0,multi_C45.outputTr.length()-4) + "train.tra";
     ficheroSalida[1] = multi_C45.outputTr.substring(0,multi_C45.outputTr.length()-4) + "train.tst";

       try {
         /*Normalize and check the data*/
         normalizar();
       }
       catch (Exception e) {
         System.err.println(e);
         System.exit(1);
       }

     /*Previous computation for HVDM distance*/
    if (distanceEu == false) {
      stdDev = new double[Attributes.getInputNumAttributes()];
      nominalDistance = new double[Attributes.getInputNumAttributes()][][];
      nClases = Attributes.getOutputAttribute(0).getNumNominalValues();
      for (i = 0; i < nominalDistance.length; i++) {
        if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
          nominalDistance[i] = new double[Attributes.getInputAttribute(i).
              getNumNominalValues()][Attributes.getInputAttribute(i).
              getNumNominalValues()];
          for (j = 0; j < Attributes.getInputAttribute(i).getNumNominalValues();
               j++) {
            nominalDistance[i][j][j] = 0.0;
          }
          for (j = 0; j < Attributes.getInputAttribute(i).getNumNominalValues();
               j++) {
            for (l = j + 1;
                 l < Attributes.getInputAttribute(i).getNumNominalValues(); l++) {
              VDM = 0.0;
              Nax = Nay = 0;
              for (m = 0; m < training.getNumInstances(); m++) {
                if (nominalTrain[m][i] == j) {
                  Nax++;
                }
                if (nominalTrain[m][i] == l) {
                  Nay++;
                }
              }
              for (m = 0; m < nClases; m++) {
                Naxc = Nayc = 0;
                for (n = 0; n < training.getNumInstances(); n++) {
                  if (nominalTrain[n][i] == j && clasesTrain[n] == m) {
                    Naxc++;
                  }
                  if (nominalTrain[n][i] == l && clasesTrain[n] == m) {
                    Nayc++;
                  }
                }
                VDM +=
                    ( ( (double) Naxc / (double) Nax) - ( (double) Nayc / (double) Nay)) *
                    ( ( (double) Naxc / (double) Nax) -
                     ( (double) Nayc / (double) Nay));
              }
              nominalDistance[i][j][l] = Math.sqrt(VDM);
              nominalDistance[i][l][j] = Math.sqrt(VDM);
            }
          }
        }
        else {
          media = 0;
          SD = 0;
          for (j = 0; j < training.getNumInstances(); j++) {
            media += realTrain[j][i];
            SD += realTrain[j][i] * realTrain[j][i];
          }
          media /= (double) realTrain.length;
          stdDev[i] = Math.sqrt( Math.abs((SD / ( (double) realTrain.length)) - (media * media)));
        }
      }
    }

  }

  public void ejecutar () {

    int nPos = 0;
    int nNeg = 0;
    int i, j, l, m;
    int tmp;
    int posID, negID;
    int positives[];
    double conjS[][];
    double conjR[][];
    int conjN[][];
    boolean conjM[][];
    int clasesS[];
    double genS[][];
	double genR[][];
	int genN[][];
	boolean genM[][];
    int clasesGen[];
    int tamS;
    int pos;
    int neighbors[][];
    int nn;
    int type[];


    int claseObt;
    boolean marcas[];
    int nSel = 0;

    long tiempo = System.currentTimeMillis();

        /*Inicialization of the flagged instances vector for a posterior copy*/
    marcas = new boolean[datosTrain.length];
    for (i=0; i<datosTrain.length; i++)
      marcas[i] = false;
    
        /*Count of number of positive and negative examples*/
    for (i=0; i<clasesTrain.length; i++) {
      if (clasesTrain[i] == 0)
        nPos++;
      else
        nNeg++;
    }
    if (nPos > nNeg) {
      tmp = nPos;
      nPos = nNeg;
      nNeg = tmp;
      posID = 1;
      negID = 0;
    } else {
      posID = 0;
      negID = 1;
    }

        /*Body of the algorithm. For each instance in T, search the correspond class conform his mayority
     from the nearest neighborhood. Is it is positive, the instance is selected.*/
    for (i=0; i<datosTrain.length; i++) {
      /*Apply KNN to the instance*/
      claseObt = KNN.evaluacionKNN2 (kClean, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], 2, distanceEu);
      if (claseObt == clasesTrain[i] || clasesTrain[i] != negID) { //agree with your majority, it is included in the solution set
        marcas[i] = true;
        nSel++;
      }
    }

    /*Building of the S set from the flags*/
    conjS = new double[nSel][datosTrain[0].length];
    conjR = new double[nSel][datosTrain[0].length];
    conjN = new int[nSel][datosTrain[0].length];
    conjM = new boolean[nSel][datosTrain[0].length];
    clasesS = new int[nSel];
    for (i=0, l=0; i<datosTrain.length; i++) {
      if (marcas[i]) { //the instance will be copied to the solution
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
   datosTrain = conjS;
   realTrain = conjR;
   nominalTrain = conjN;
   nulosTrain = conjM;
   clasesTrain = clasesS;
   nNeg = 0; nPos = 0;
   for (i=0; i<clasesTrain.length; i++) {
      if (clasesTrain[i] == posID)
        nPos++;
      else
        nNeg++;
    }
   if (nNeg < nPos)
   {
       System.out.println("MSMOTE "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");
       OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
       return;
   }
   conjS = null;
   conjR = null;;
   conjN = null;
   conjM = null;
   clasesS = null;


    /*Localize the positive instances*/
    positives = new int[nPos];
    for (i=0, j=0; i<clasesTrain.length; i++) {
      if (clasesTrain[i] == posID) {
        positives[j] = i;
        j++;
      }
    }

    /*Randomize the instance presentation*/
    //Randomize.setSeed (semilla);
    for (i=0; i<positives.length; i++) {
      tmp = positives[i];
      pos = Randomize.Randint(0,positives.length-1);
      positives[i] = positives[pos];
      positives[pos] = tmp;
    }


    /*Obtain k-nearest neighbors of each positive instance*/
    neighbors = new int[positives.length][kSMOTE];
    for (i=0; i<positives.length; i++) {
    	switch (ASMO) {
        	case 0:
        		KNN.evaluacionKNN2 (kSMOTE, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[positives[i]], realTrain[positives[i]], nominalTrain[positives[i]], nulosTrain[positives[i]], 2, distanceEu, neighbors[i]);
        		break;
        	case 1:
        		evaluacionKNNClass (kSMOTE, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[positives[i]], realTrain[positives[i]], nominalTrain[positives[i]], nulosTrain[positives[i]], 2, distanceEu, neighbors[i],posID);
        		break;
        	case 2:
        		evaluacionKNNClass (kSMOTE, datosTrain, realTrain, nominalTrain, nulosTrain, clasesTrain, datosTrain[positives[i]], realTrain[positives[i]], nominalTrain[positives[i]], nulosTrain[positives[i]], 2, distanceEu, neighbors[i],negID);
        		break;
    	}
    }


    /* Verify sample type,
     * security = all neighbors from miniority class 2
     * Border = Neither all from minority nor from majority 1
     * Latent noise = all neighbors from majority class 0
     */
    type = new int[neighbors.length];
    int noiseCount = 0;
    for (i = 0; i < neighbors.length; i++)
    {
       int count = 0;
       for (j = 0; j < kSMOTE; j++)
          if (clasesTrain[neighbors[i][j]] == posID)
            count++;
       if (count == 0)
       {
          type[i] = 0;
          noiseCount++;
       }
       else if (count > 0 && count < kSMOTE)
          type[i] = 1;
       else
          type[i] = 2;
    }

    /*Interpolation of the minority instances*/
    if (balance) {
    	genS = new double[nNeg-nPos][datosTrain[0].length];
    	genR = new double[nNeg-nPos][datosTrain[0].length];
    	genN = new int[nNeg-nPos][datosTrain[0].length];
    	genM = new boolean[nNeg-nPos][datosTrain[0].length];
    	clasesGen = new int[nNeg-nPos];
    } else {
    	genS = new double[(int)((nPos - noiseCount)*smoting)][datosTrain[0].length];
    	genR = new double[(int)((nPos - noiseCount)*smoting)][datosTrain[0].length];
    	genN = new int[(int)((nPos - noiseCount)*smoting)][datosTrain[0].length];
    	genM = new boolean[(int)((nPos - noiseCount)*smoting)][datosTrain[0].length];
    	clasesGen = new int[(int)((nPos - noiseCount)*smoting)];
    }
    i = 0;
    
    int aux = 0;
    for (i = 0; i < positives.length; i++) {
        if (type[i] != 0)
            aux++;
    }
    if (aux == 0)
        Arrays.fill(type, 1);
        
    for (int count = 0; count<genS.length; ) {
       if (type[i%positives.length] != 0)
       {
    	clasesGen[count] = posID;
        if (type[i%positives.length] == 2)
            nn = Randomize.Randint(0,kSMOTE-1);
        else
           nn = 0;
    	interpola (realTrain[positives[i%positives.length]],
                realTrain[neighbors[i%positives.length][nn]],
                nominalTrain[positives[i%positives.length]],
                nominalTrain[neighbors[i%positives.length][nn]],
                nulosTrain[positives[i%positives.length]],
                nulosTrain[neighbors[i%positives.length][nn]],
                genS[count],genR[count],genN[count],genM[count]);
        count++;
       }
       /*else
          count--;*/
       i++;
    }

	if (balance) {
		tamS = 2*nNeg;
	} else {
		tamS = nNeg + nPos + (int)((nPos - noiseCount)*smoting);
	}
   /*Construction of the S set from the previous vector S*/
    conjS = new double[tamS][datosTrain[0].length];
    conjR = new double[tamS][datosTrain[0].length];
    conjN = new int[tamS][datosTrain[0].length];
    conjM = new boolean[tamS][datosTrain[0].length];
    clasesS = new int[tamS];
    for (j=0; j<datosTrain.length; j++) {
      for (l=0; l<datosTrain[0].length; l++) {
        conjS[j][l] = datosTrain[j][l];
        conjR[j][l] = realTrain[j][l];
        conjN[j][l] = nominalTrain[j][l];
        conjM[j][l] = nulosTrain[j][l];
      }
      clasesS[j] = clasesTrain[j];
    }
    for (m=0;j<tamS; j++, m++) {
      for (l=0; l<datosTrain[0].length; l++) {
        conjS[j][l] = genS[m][l];
        conjR[j][l] = genR[m][l];
        conjN[j][l] = genN[m][l];
        conjM[j][l] = genM[m][l];
      }
      clasesS[j] = clasesGen[m];
    }

    System.out.println("MSMOTE "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

    OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
//    OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
  }

	public static int evaluacionKNNClass (int nvec, double conj[][], double real[][], int nominal[][], boolean nulos[][], int clases[], double ejemplo[], double ejReal[], int ejNominal[], boolean ejNulos[], int nClases, boolean distance, int vecinos[], int clase) {

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
		for (i=0; i<nvec; i++) {
			vecinosCercanos[i] = -1;
			minDistancias[i] = Double.POSITIVE_INFINITY;
		}

		for (i=0; i<conj.length; i++) {
			dist = KNN.distancia(conj[i], real[i], nominal[i], nulos[i], ejemplo, ejReal, ejNominal, ejNulos, distance);
			if (dist > 0 && clases[i] == clase) {
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

	void interpola (double ra[], double rb[], int na[], int nb[], boolean ma[], boolean mb[], double resS[], double resR[], int resN[], boolean resM[]) {

		int i;
		double diff;
		double gap;
		int suerte;

		for (i=0; i<ra.length; i++) {
			if (ma[i] == true && mb[i] == true) {
				resM[i] = true;
				resS[i] = 0;
			} else if (ma[i] == true){
				if (entradas[i].getType() == Attribute.REAL) {
					resR[i] = rb[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					resR[i] = rb[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else {
					resN[i] = nb[i];
					resS[i] = (double)resN[i] / (double)(entradas[i].getNominalValuesList().size() - 1);
				}
			} else if (mb[i] == true) {
				if (entradas[i].getType() == Attribute.REAL) {
					resR[i] = ra[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					resR[i] = ra[i];
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else {
					resN[i] = na[i];
					resS[i] = (double)resN[i] / (double)(entradas[i].getNominalValuesList().size() - 1);
				}
			} else {
				resM[i] = false;
				if (entradas[i].getType() == Attribute.REAL) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = ra[i] + gap*diff;
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else if (entradas[i].getType() == Attribute.INTEGER) {
					diff = rb[i] - ra[i];
					gap = Randomize.Rand();
					resR[i] = Math.round(ra[i] + gap*diff);// Math.round( (ra[i] + gap*diff)*
                                               // (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute())); //Math.round(ra[i] + gap*diff);
					resS[i] = (resR[i] + entradas[i].getMinAttribute()) / (entradas[i].getMaxAttribute() - entradas[i].getMinAttribute());
				} else {
					suerte = Randomize.Randint(0, 2);
					if (suerte == 0) {
						resN[i] = na[i];
					} else {
						resN[i] = nb[i];
					}
					resS[i] = (double)resN[i] / (double)(entradas[i].getNominalValuesList().size() - 1);
				}
			}
		}
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

    /*Getting the seed*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    semilla = Long.parseLong(tokens.nextToken().substring(1));

    /*Getting the number of neighbors*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    kSMOTE = Integer.parseInt(tokens.nextToken().substring(1));

    /*Getting the type of SMOTE algorithm*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();
    token = token.substring(1);
    if (token.equalsIgnoreCase("both")) ASMO = 0;
    else if (token.equalsIgnoreCase("minority")) ASMO = 1;
    else ASMO = 2;

    /*Getting the type of balancing in SMOTE*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    token = tokens.nextToken();
    token = token.substring(1);
    if (token.equalsIgnoreCase("YES")) balance = true;
    else balance = false;

    /*Getting the quantity of smoting*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    smoting = Double.parseDouble(tokens.nextToken().substring(1));

    /*Getting the type of distance function*/
    linea = lineasFichero.nextToken();
    tokens = new StringTokenizer (linea, "=");
    tokens.nextToken();
    distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;
  }
}
