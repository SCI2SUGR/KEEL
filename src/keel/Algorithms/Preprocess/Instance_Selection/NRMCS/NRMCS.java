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
//  NRMCS.java
//
//  Salvador García López
//
//  Created by Salvador García López 4-2-2009.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Preprocess.Instance_Selection.NRMCS;

import keel.Algorithms.Preprocess.Basic.*;
import org.core.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;

public class NRMCS extends Metodo {
	
	/*Parameters of the algorithm*/
	private double errorThreshold;

	public NRMCS (String ficheroScript) {
		super (ficheroScript);
	}

	public void ejecutar () {

		int i, j, l;
		int nClases;
		boolean marcas[];
		int nSel;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		int NUNsample[];
		double NUNdistance[];
		int votes[];
		Vector <Integer> identifiers[];
		boolean candidateVote[];
		boolean inclusion;
		boolean newcandidate[];
		int maxVotes, posMax;
		int count;
		int nSeltmp;
		boolean continuar;
		boolean voters[];
		int noiseN;
		long errorN;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of differents classes*/
		nClases = 0;
		for (i = 0; i < clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;

		/*Inicialization of the flagged instances vector for a further copy*/
		marcas = new boolean[datosTrain.length];
		candidateVote = new boolean[datosTrain.length];
		newcandidate = new boolean[datosTrain.length];
		voters = new boolean[datosTrain.length];
		for (i = 0; i < datosTrain.length; i++) {
			newcandidate[i] = true;
		}
		nSeltmp = datosTrain.length;

		NUNsample = new int[datosTrain.length];
		NUNdistance = new double[datosTrain.length];
		votes = new int[datosTrain.length];
		identifiers = new Vector [datosTrain.length];
		
		errorN = Math.round((double)(errorThreshold*datosTrain.length));

		/*Body of the algorithm Minimal Consistent Set*/
		do {
			for (i=0; i<datosTrain.length; i++) {
				identifiers[i] = new Vector <Integer> ();
			}

			for (i=0; i<datosTrain.length; i++) {
				marcas[i] = newcandidate[i];
			}
			nSel = nSeltmp;
			Arrays.fill(voters, false);
			calcularNUN(NUNsample, NUNdistance, marcas, votes, identifiers, voters);
			
			Arrays.fill(candidateVote,false);
			for (i = 0; i < datosTrain.length; i++) {
				if (!marcas[i]) {
					inclusion = true;
					for (j = 0; j < datosTrain.length && inclusion; j++) {
						if (clasesTrain[i] != clasesTrain[j]) {
							if (KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu) < NUNdistance[i]) {
								inclusion = false;
							}
						}
					}
					candidateVote[i] = inclusion;
				} else{
					candidateVote[i] = true;
				}
			}

			/*NRMCS: Noise remove stage*/
			noiseN = 0;
			for (i = 0; i < datosTrain.length; i++) {
				if (!voters[i] || votes[i] <= 1) {
					candidateVote[i] = false;
					noiseN++;
				} 
			}			
			
			Arrays.fill(newcandidate, false);
			count = datosTrain.length - noiseN;

			continuar = true;
			while (count > errorN && continuar) {
				maxVotes = Integer.MIN_VALUE;
				posMax = -1;

				/*Search the most voted candidate*/
				for (i = 0; i < candidateVote.length; i++) {
					if (candidateVote[i]) {
						if (votes[i] > maxVotes) {
							maxVotes = votes[i];
							posMax = i;
						}
					}
				}

				if (posMax >= 0) {
					/*Include the most voted candidate into the new candidate list*/
					newcandidate[posMax] = true;
					candidateVote[posMax] = false;
					/*Delete the voters and update corresponding lists of voters*/
					for (i = 0; i < identifiers[posMax].size(); i++) {
						for (j = 0; j < datosTrain.length; j++) {
							if (candidateVote[j]) {
								if (identifiers[j].contains(identifiers[posMax].elementAt(i))) {
									votes[j]--;
									identifiers[j].removeElement(identifiers[posMax].elementAt(i));
								}
							}
						}
						count--;
					}
				} else {
					continuar = false;
				}
			}

			nSeltmp = 0;
			for (i = 0; i < datosTrain.length; i++) {
				if (newcandidate[i]) {
					nSeltmp++;
				}
			}
		} while (nSeltmp < nSel);

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

		System.out.println("NRMCS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

		OutputIS.escribeSalida(ficheroSalida[0], conjR, conjN, conjM, clasesS, entradas, salida, nEntradas, relation);
		OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
	}

    void calcularNUN (int id[], double dist[], boolean flag[], int votos[], Vector <Integer> ident[], boolean voters[]) {

    	int i, j;
    	int pos;
    	double minDist, distan;

    	Arrays.fill(votos,0);

    	for (i=0; i<datosTrain.length; i++) {
    		pos = -1;
    		minDist = Double.POSITIVE_INFINITY;

    		/*Finding NUN instance for i instance*/
    		for (j = 0; j < datosTrain.length; j++) {
    			if (flag[j]) {
    				distan = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
    				if (i != j && distan < minDist && clasesTrain[i] != clasesTrain[j]) {
    					minDist = distan;
    					pos = j;
    				}
    			}
    		}
    		id[i] = pos;
    		dist[i] = minDist;

    		/*Finding the neighbouring instances (identifying them) closer than its NUN*/
    		for (j=0; j<datosTrain.length; j++) {
    			if (KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu) < dist[i] && clasesTrain[i] == clasesTrain[j]) {
    				votos[j]++;
    				ident[j].addElement(new Integer(i));
    				if (i != j) {
    					voters[i] = true;
    				}
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

    	/*Getting the type of distance function*/
    	linea = lineasFichero.nextToken();
    	tokens = new StringTokenizer (linea, "=");
    	tokens.nextToken();
    	distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;  

        linea = lineasFichero.nextToken();
        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        errorThreshold = Double.parseDouble(tokens.nextToken().substring(1));    
    }
}

