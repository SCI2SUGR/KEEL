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
//  CCIS.java
//
//  Salvador Garc�a L�pez
//
//  Created by Salvador Garc�a L�pez 3-3-2010.
//  Copyright (c) 2004 __MyCompanyName__. All rights reserved.
//

package keel.Algorithms.Instance_Selection.CCIS;

import keel.Algorithms.Preprocess.Basic.*;

import org.core.*;
import java.util.StringTokenizer;
import java.util.Arrays;
import java.util.Vector;

public class CCIS extends Metodo {

	public CCIS (String ficheroScript) {
		super (ficheroScript);
	}

	public void ejecutar () {

		int i, j, k, m, l;
		int nClases;
		int graphWB[][];
		boolean marcas[];
		boolean marcasTmp[];		
		int nSel = 0;
		int nSelTmp;
		double conjS[][];
		double conjR[][];
		int conjN[][];
		boolean conjM[][];
		int clasesS[];
		double conjSprev[][];
		double conjRprev[][];
		int conjNprev[][];
		boolean conjMprev[][];
		int clasesSprev[];
		double conjS1[][];
		double conjR1[][];
		int conjN1[][];
		boolean conjM1[][];
		int clasesS1[];
		double conjSf[][];
		double conjRf[][];
		int conjNf[][];
		boolean conjMf[][];
		int clasesSf[];
		double conjSft[][];
		double conjRft[][];
		int conjNft[][];
		boolean conjMft[][];
		int clasesSft[];
		double dist, minDist[], mnDist;
		int pos[], tmp, tmp2;
		int withinIn[];
		int withinInSprev[];
		int betweenIn[][][];
		int betweenInS1[][][];
		int betweenInSprev[][][];
		int betweenInS[][][];
		int c1, c2;
		int bestClass;
		double minCorr, corr;
		Vector <Pareja> score;
		double in, bet;
		int totalIN, totalBET;
		double sc;
		Pareja scores [];
		boolean clasesVistas[][];
		int errorA = 0;
		int k0;
		boolean go_on;
		int errorS, errorTmp, errorSf, errorSt;
		int asociacionS1[];
		int asociacionS2[];
		boolean marcasS1[];
		int nSelS1 = 0;

		long tiempo = System.currentTimeMillis();

		/*Getting the number of differents classes*/
		nClases = 0;
		for (i=0; i<clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		
		marcas = new boolean[datosTrain.length];
		
		/*Computing the error of leave-one-out in training data*/
		for (i=0; i<datosTrain.length; i++) {
			mnDist = Double.POSITIVE_INFINITY;
			tmp = -1;
			for (j=0; j<datosTrain.length; j++) {
				if (i != j) {
					dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
					if (dist < mnDist) {
						mnDist = dist;
						tmp = j;
					}
				}
			}
			if (clasesTrain[i] != clasesTrain[tmp])
				errorA++;
		}
    
		/*Computing the relations in the within and between class graphs*/
		graphWB = new int[datosTrain.length][nClases];
		minDist = new double[nClases];
		pos = new int[nClases];
		for (i=0; i<datosTrain.length; i++) {
			Arrays.fill(minDist, Double.POSITIVE_INFINITY);
			Arrays.fill(pos, -1);
			for (j=0; j<datosTrain.length; j++) {
				if (i != j) {
					dist = KNN.distancia(datosTrain[i], realTrain[i], nominalTrain[i], nulosTrain[i], datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], distanceEu);
					if (dist < minDist[clasesTrain[j]]) {
						minDist[clasesTrain[j]] = dist;
						pos[clasesTrain[j]] = j;
					}
				}
			}
			for (j=0; j<nClases; j++) {
				graphWB[i][j] = pos[j];
			}
		}
		
		/*Computing the within in-degree*/
		withinIn = new int[datosTrain.length];
		Arrays.fill(withinIn, 0);
		for (i=0; i<datosTrain.length; i++) {
			if (graphWB[i][clasesTrain[i]] >= 0) {
				withinIn[graphWB[i][clasesTrain[i]]]++;
			}
		}
		
		/*Computing the between in-degree*/
		betweenIn = new int[nClases][nClases][datosTrain.length];
		for (i=0; i<nClases; i++)
			for (j=0; j<nClases; j++)
				Arrays.fill(betweenIn[i][j], 0);
		for (i=0; i<datosTrain.length; i++) {
			for (j=0; j<nClases; j++) {
				if (clasesTrain[i] != j) {
					if (graphWB[i][j] >= 0) {
						betweenIn[clasesTrain[i]][j][graphWB[i][j]]++;
						betweenIn[j][clasesTrain[i]][graphWB[i][j]]++;
					}
				}
			}
		}

		/*Class Conditional Selection: CC*/
		clasesVistas = new boolean[nClases][nClases];
		for (i=0; i<nClases; i++) {
			for (j=0; j<nClases; j++) {
				clasesVistas[i][j] = false;
			}
		}
		for (c1=0; c1<nClases; c1++) {
			/*Searching the best pair of classes for each class*/
			bestClass = -1;
			minCorr = Double.POSITIVE_INFINITY;
			for (i=0; i<nClases; i++) {
				if (i != c1) {
					corr = computeCorrelation (c1, i, withinIn, betweenIn);
					if (corr < minCorr) {
						minCorr = corr;
						bestClass = i;
					}
				}
			}
			c2 = bestClass;
			
			if (bestClass >= 0 && !clasesVistas[c1][c2]) {
				clasesVistas[c1][c2] = true;
				clasesVistas[c2][c1] = true;				
			
				/*Computing the total indegree*/
				totalIN = totalBET = 0;
				for (i=0; i<datosTrain.length; i++) {
					if (clasesTrain[i] == c1 || clasesTrain[i] == c2) {
						totalIN += withinIn[i];
						totalBET += betweenIn[c1][c2][i];					
					}	
				}
			
				/*Computing the score for each sample of c1 and c2 classes*/
				score = new Vector<Pareja> ();
				for (i=0; i<datosTrain.length; i++) {
					if (clasesTrain[i] == c1 || clasesTrain[i] == c2) {
						in = (double)withinIn[i]/(double)totalIN;
						bet = (double)betweenIn[c1][c2][i]/(double)totalBET;
					
						sc = kDivergence(in, bet) - kDivergence(bet, in);
						if (sc >= 0)
							score.add(new Pareja(i,sc));
					}
				}
			
				scores = new Pareja[score.size()];
				score.toArray(scores);
				Arrays.sort(scores);
				
			
				k0 = Math.max(nClases, (int) Math.ceil((double)errorA/2.0));
				for (i=0; i<k0 && i<scores.length; i++) {
					if (!marcas[scores[i].entero]) {
						marcas[scores[i].entero] = true;
						nSel++;
					}
				}
				
				go_on = true;
				marcasTmp = new boolean[datosTrain.length];
				for (j=0; j<marcas.length; j++)
					marcasTmp[j] = marcas[j];
				nSelTmp = nSel;
				for (; i<scores.length && go_on; i++) {
					/*Estimate the current error using S as selected set*/
					errorS = 0;
					for (j=0; j<datosTrain.length; j++) {
						mnDist = Double.POSITIVE_INFINITY;
						tmp = -1;
						for (k=0; k<datosTrain.length; k++) {
							if (j != k && marcas[k]) {
								dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], datosTrain[k], realTrain[k], nominalTrain[k], nulosTrain[k], distanceEu);
								if (dist < mnDist) {
									mnDist = dist;
									tmp = k;
								}
							}
						}
						if (clasesTrain[j] != clasesTrain[tmp])
							errorS++;
					}
					
					if (errorS <= errorA) {
						go_on = false;
					}
					
					/*Add a new instance to S and test its accuracy*/
					if (!marcasTmp[scores[i].entero]) {
						marcasTmp[scores[i].entero] = true;
						nSelTmp++;
					}
					
					if (go_on) {
						/*Estimate the current error using S as selected set*/
						errorTmp = 0;
						for (j=0; j<datosTrain.length; j++) {
							mnDist = Double.POSITIVE_INFINITY;
							tmp = -1;
							for (k=0; k<datosTrain.length; k++) {
								if (j != k && marcasTmp[k]) {
									dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], datosTrain[k], realTrain[k], nominalTrain[k], nulosTrain[k], distanceEu);
									if (dist < mnDist) {
										mnDist = dist;
										tmp = k;
									}
								}
							}
							if (clasesTrain[j] != clasesTrain[tmp])
								errorTmp++;
						}
						
						if (errorTmp < errorS) {
							nSel = nSelTmp;
							for (j=0; j<marcas.length; j++)
								marcas[j] = marcasTmp[j];							
						} else {
							go_on = false;
						}						
					}
				}
			}
		}
		
		/*Thin-Out Instance Selection: THIN*/
		
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
    	
		marcasS1 = new boolean[conjS.length];
		nSelS1 = 0;
    	
    	/*Computing in-degree in G^S_{bc}*/

		/*Computing the relations in the within and between class graphs*/
		graphWB = new int[conjS.length][nClases];
		minDist = new double[nClases];
		pos = new int[nClases];
		for (i=0; i<conjS.length; i++) {
			Arrays.fill(minDist, Double.POSITIVE_INFINITY);
			Arrays.fill(pos, -1);
			for (j=0; j<conjS.length; j++) {
				if (i != j) {
					dist = KNN.distancia(conjS[i], conjR[i], conjN[i], conjM[i], conjS[j], conjR[j], conjN[j], conjM[j], distanceEu);
					if (dist < minDist[clasesS[j]]) {
						minDist[clasesS[j]] = dist;
						pos[clasesS[j]] = j;
					}
				}
			}
			for (j=0; j<nClases; j++) {
				graphWB[i][j] = pos[j];
			}
		}
		
		/*Computing the between in-degree*/
		betweenInS = new int[nClases][nClases][conjS.length];
		for (i=0; i<nClases; i++)
			for (j=0; j<nClases; j++)
				Arrays.fill(betweenInS[i][j], 0);
		for (i=0; i<conjS.length; i++) {
			for (j=0; j<nClases; j++) {
				if (clasesS[i] != j) {
					if (graphWB[i][j] >= 0) {
						betweenInS[clasesS[i]][j][graphWB[i][j]]++;
						betweenInS[j][clasesS[i]][graphWB[i][j]]++;
					}
				}
			}
		}
		
		/*Add to Sf those instances with positive in-degree*/
		marcas = new boolean[conjS.length];
		nSel = 0;
    	for (i=0; i<nClases; i++) {
    		for (j=i+1; j<nClases; j++) {
    			if (clasesVistas[i][j]) {
    				for (k=0; k<conjS.length; k++) {
        				if (clasesS[k] == i || clasesS[k] == j) {
        					if (betweenInS[i][j][k] > 0) {
        						if (!marcas[k]) {
        							marcas[k] = true;
        							nSel++;
        							marcasS1[k] = true;
        							nSelS1++;
        						}
        					}
        				}    					
    				}
    			}
    		}
    	}
    	conjSf = new double[nSel][datosTrain[0].length];
    	conjRf = new double[nSel][datosTrain[0].length];
    	conjNf = new int[nSel][datosTrain[0].length];
    	conjMf = new boolean[nSel][datosTrain[0].length];
    	clasesSf = new int[nSel];
    	for (m=0, l=0; m<conjS.length; m++) {
    		if (marcas[m]) { 
    			for (j=0; j<datosTrain[0].length; j++) {
    				conjSf[l][j] = conjS[m][j];
    				conjRf[l][j] = conjR[m][j];
    				conjNf[l][j] = conjN[m][j];
    				conjMf[l][j] = conjM[m][j];
    			}
    			clasesSf[l] = clasesS[m];
    			l++;
    		}
    	}   
    	
    	
    	/*S_{prev} = S*/
    	conjSprev = new double[conjS.length][datosTrain[0].length];
    	conjRprev = new double[conjR.length][datosTrain[0].length];
    	conjNprev = new int[conjN.length][datosTrain[0].length];
    	conjMprev = new boolean[conjM.length][datosTrain[0].length];
    	clasesSprev = new int[clasesS.length];
    	for (m=0, l=0; m<conjS.length; m++) {
    		for (j=0; j<conjS[0].length; j++) {
    			conjSprev[l][j] = conjS[m][j];
    			conjRprev[l][j] = conjR[m][j];
    			conjNprev[l][j] = conjN[m][j];
    			conjMprev[l][j] = conjM[m][j];
    		}
    		clasesSprev[l] = clasesS[m];
    		l++;
    	}    
    	
    	/*S_1 = S \ S_f*/
    	conjS1 = new double[conjS.length - nSel][datosTrain[0].length];
    	conjR1 = new double[conjS.length - nSel][datosTrain[0].length];
    	conjN1 = new int[conjS.length - nSel][datosTrain[0].length];
    	conjM1 = new boolean[conjS.length - nSel][datosTrain[0].length];
    	clasesS1 = new int[conjS.length - nSel];
    	asociacionS1 = new int[conjS.length - nSel];
    	asociacionS2 = new int[conjS.length - nSel];
    	for (m=0, l=0; m<conjS.length; m++) {
    		if (!marcas[m]) { 
    			for (j=0; j<datosTrain[0].length; j++) {
    				conjS1[l][j] = conjS[m][j];
    				conjR1[l][j] = conjR[m][j];
    				conjN1[l][j] = conjN[m][j];
    				conjM1[l][j] = conjM[m][j];
    			}
    			clasesS1[l] = clasesS[m];
    			asociacionS1[l] = m;
    			asociacionS2[l] = m;
    			l++;
    		}
    	}   
    	
    	go_on = true;
    	while (go_on) {
    		/*Step 6 of the algorithm THIN*/
    		
    		/*Computing the relations in the within and between class graphs*/
    		graphWB = new int[conjS1.length][nClases];
    		minDist = new double[nClases];
    		pos = new int[nClases];
    		for (i=0; i<conjS1.length; i++) {
    			Arrays.fill(minDist, Double.POSITIVE_INFINITY);
    			Arrays.fill(pos, -1);
    			for (j=0; j<conjS1.length; j++) {
    				if (i != j) {
    					dist = KNN.distancia(conjS1[i], conjR1[i], conjN1[i], conjM1[i], conjS1[j], conjR1[j], conjN1[j], conjM1[j], distanceEu);
    					if (dist < minDist[clasesS1[j]]) {
    						minDist[clasesS1[j]] = dist;
    						pos[clasesS1[j]] = j;
    					}
    				}
    			}
    			for (j=0; j<nClases; j++) {
    				graphWB[i][j] = pos[j];
    			}
    		}
    		
    		/*Computing the between in-degree*/
    		betweenInS1 = new int[nClases][nClases][conjS1.length];
    		for (i=0; i<nClases; i++)
    			for (j=0; j<nClases; j++)
    				Arrays.fill(betweenInS1[i][j], 0);
    		for (i=0; i<conjS1.length; i++) {
    			for (j=0; j<nClases; j++) {
    				if (clasesS1[i] != j) {
    					if (graphWB[i][j] >= 0) {
    						betweenInS1[clasesS1[i]][j][graphWB[i][j]]++;
    						betweenInS1[j][clasesS1[i]][graphWB[i][j]]++;
    					}
    				}
    			}
    		}
    		
    		/*Computing the relations in the within and between class graphs*/
    		graphWB = new int[conjSprev.length][nClases];
    		minDist = new double[nClases];
    		pos = new int[nClases];
    		for (i=0; i<conjSprev.length; i++) {
    			Arrays.fill(minDist, Double.POSITIVE_INFINITY);
    			Arrays.fill(pos, -1);
    			for (j=0; j<conjSprev.length; j++) {
    				if (i != j) {
    					dist = KNN.distancia(conjSprev[i], conjRprev[i], conjNprev[i], conjMprev[i], conjSprev[j], conjRprev[j], conjNprev[j], conjMprev[j], distanceEu);
    					if (dist < minDist[clasesSprev[j]]) {
    						minDist[clasesSprev[j]] = dist;
    						pos[clasesSprev[j]] = j;
    					}
    				}
    			}
    			for (j=0; j<nClases; j++) {
    				graphWB[i][j] = pos[j];
    			}
    		}
    		
    		/*Computing the within in-degree*/
    		withinInSprev = new int[conjSprev.length];
    		Arrays.fill(withinInSprev, 0);
    		for (i=0; i<conjSprev.length; i++) {
    			if (graphWB[i][clasesSprev[i]] >= 0) {
    				withinInSprev[graphWB[i][clasesSprev[i]]]++;
    			}
    		}    		
    		
    		/*Computing the between in-degree*/
    		betweenInSprev = new int[nClases][nClases][conjSprev.length];
    		for (i=0; i<nClases; i++)
    			for (j=0; j<nClases; j++)
    				Arrays.fill(betweenInSprev[i][j], 0);
    		for (i=0; i<conjSprev.length; i++) {
    			for (j=0; j<nClases; j++) {
    				if (clasesSprev[i] != j) {
    					if (graphWB[i][j] >= 0) {
    						betweenInSprev[clasesSprev[i]][j][graphWB[i][j]]++;
    						betweenInSprev[j][clasesSprev[i]][graphWB[i][j]]++;
    					}
    				}
    			}
    		}
    		
    		/*Add to St those instances with positive in-degree in S_1 and S_prev*/
    		marcasTmp = new boolean[conjS1.length];
    		nSelTmp = 0;
        	for (i=0; i<nClases; i++) {
        		for (j=i+1; j<nClases; j++) {
        			if (clasesVistas[i][j]) {
        				for (k=0; k<conjS1.length; k++) {
        					if (clasesS1[k] == i || clasesS1[k] == j) {
        						if (betweenInS1[i][j][k] > 0 && (betweenInSprev[i][j][asociacionS2[k]] > 0 || withinInSprev[asociacionS2[k]] > 0)) {
        							if (!marcasTmp[k]) {
        								marcasTmp[k] = true;
        								nSelTmp++;
        							}
        						}
            				}        					
        				}
        			}
        		}
        	}
        	
        	/*Estimation of the error of S_f and the join of S_f and S_t*/
			errorSf = 0;
			errorSt = 0;
			for (j=0; j<datosTrain.length; j++) {
				mnDist = Double.POSITIVE_INFINITY;
				tmp = -1;
				for (k=0; k<conjSf.length; k++) {
					dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], conjSf[k], conjRf[k], conjNf[k], conjMf[k], distanceEu);
					if (dist > 0 && dist < mnDist) {
						mnDist = dist;
						tmp = k;
					}
				}
				if (tmp == -1 || clasesTrain[j] != clasesSf[tmp])
					errorSf++;
				
				tmp2 = -1;
				for (k=0; k<conjS1.length; k++) {
					if (marcasTmp[k]) {
						dist = KNN.distancia(datosTrain[j], realTrain[j], nominalTrain[j], nulosTrain[j], conjS1[k], conjR1[k], conjN1[k], conjM1[k], distanceEu);
						if (dist > 0 && dist < mnDist) {
							mnDist = dist;
							tmp2 = k;
						}
					}
				}
				
				if (tmp2 >= 0) {
					if (clasesTrain[j] != clasesS1[tmp2])
						errorSt++;
				} else {
					if (tmp == -1 || clasesTrain[j] != clasesS[tmp])
						errorSt++;					
				}
			}
			
			if (errorSt >= errorSf)
				go_on = false;
			
			if (go_on) {
				/*S_f = S_f U S_t*/
		    	conjSft = new double[nSelTmp+conjSf.length][datosTrain[0].length];
		    	conjRft = new double[nSelTmp+conjRf.length][datosTrain[0].length];
		    	conjNft = new int[nSelTmp+conjNf.length][datosTrain[0].length];
		    	conjMft = new boolean[nSelTmp+conjMf.length][datosTrain[0].length];
		    	clasesSft = new int[nSelTmp+clasesSf.length];
		    	for (m=0, l=0; m<conjSf.length; m++) {
		    		for (j=0; j<datosTrain[0].length; j++) {
		    			conjSft[l][j] = conjS[m][j];
		    			conjRft[l][j] = conjR[m][j];
		    			conjNft[l][j] = conjN[m][j];
		    			conjMft[l][j] = conjM[m][j];
		    		}
		    		clasesSft[l] = clasesS[m];
		    		l++;
		    	}   
		    	for (m=0; m<conjS1.length; m++) {
		    		if (marcasTmp[m]) {
		    			for (j=0; j<datosTrain[0].length; j++) {
		    				conjSft[l][j] = conjS1[m][j];
		    				conjRft[l][j] = conjR1[m][j];
		    				conjNft[l][j] = conjN1[m][j];
		    				conjMft[l][j] = conjM1[m][j];
		    			}
		    			clasesSft[l] = clasesS1[m];
		    			l++;
						marcasS1[asociacionS1[m]] = true;
						nSelS1++;
		    		}
		    	}   
		    	
		    	conjSf = new double[conjSft.length][datosTrain[0].length];
		    	conjRf = new double[conjRft.length][datosTrain[0].length];
		    	conjNf = new int[conjNft.length][datosTrain[0].length];
		    	conjMf = new boolean[conjMft.length][datosTrain[0].length];
		    	clasesSf = new int[clasesSft.length];
		    	for (m=0, l=0; m<conjSft.length; m++) {
		    		for (j=0; j<conjSft[0].length; j++) {
		    			conjSf[l][j] = conjSft[m][j];
		    			conjRf[l][j] = conjRft[m][j];
		    			conjNf[l][j] = conjNft[m][j];
		    			conjMf[l][j] = conjMft[m][j];
		    		}
		    		clasesSf[l] = clasesSft[m];
		    		l++;
		    	}    
		    					
				/*S_{prev} = S_1*/
		    	conjSprev = new double[conjS1.length][datosTrain[0].length];
		    	conjRprev = new double[conjR1.length][datosTrain[0].length];
		    	conjNprev = new int[conjN1.length][datosTrain[0].length];
		    	conjMprev = new boolean[conjM1.length][datosTrain[0].length];
		    	clasesSprev = new int[clasesS1.length];
		    	for (m=0, l=0; m<conjS1.length; m++) {
		    		for (j=0; j<conjS1[0].length; j++) {
		    			conjSprev[l][j] = conjS1[m][j];
		    			conjRprev[l][j] = conjR1[m][j];
		    			conjNprev[l][j] = conjN1[m][j];
		    			conjMprev[l][j] = conjM1[m][j];
		    		}
		    		clasesSprev[l] = clasesS1[m];
		    		l++;
		    	}    
		    	
				/*S_1 = S \ S_f*/
		    	conjS1 = new double[conjS.length - nSelS1][datosTrain[0].length];
		    	conjR1 = new double[conjR.length - nSelS1][datosTrain[0].length];
		    	conjN1 = new int[conjN.length - nSelS1][datosTrain[0].length];
		    	conjM1 = new boolean[conjM.length - nSelS1][datosTrain[0].length];
		    	clasesS1 = new int[clasesS.length - nSelS1];
		    	asociacionS1 = new int[conjS.length - nSelS1];
		    	for (m=0, l=0; m<conjS.length; m++) {
		    		if (!marcasS1[m]) { 
		    			for (j=0; j<datosTrain[0].length; j++) {
		    				conjS1[l][j] = conjS[m][j];
		    				conjR1[l][j] = conjR[m][j];
		    				conjN1[l][j] = conjN[m][j];
		    				conjM1[l][j] = conjM[m][j];
		    			}
		    			clasesS1[l] = clasesS[m];
		    			asociacionS1[l] = m;
		    			l++;
		    		}
		    	}   
		    	
		    	asociacionS2 = new int[conjSprev.length - nSelTmp];
		    	for (m=0, l=0; m<conjSprev.length; m++) {
		    		if (!marcasTmp[m]) { 
		    			asociacionS2[l] = m;
		    			l++;
		    		}
		    	}   
		    	
			}
    	}

		System.out.println("CCIS "+ relation + " " + (double)(System.currentTimeMillis()-tiempo)/1000.0 + "s");

		//OutputIS.escribeSalida(ficheroSalida[0], conjRf, conjNf, conjMf, clasesSf, entradas, salida, nEntradas, relation);
		//OutputIS.escribeSalida(ficheroSalida[1], test, entradas, salida, nEntradas, relation);
                
        // COn conjS me vale.
        int trainRealClass[][];
        int trainPrediction[][];
                
         trainRealClass = new int[datosTrain.length][1];
		 trainPrediction = new int[datosTrain.length][1];	
                
         //Working on training
         for ( i=0; i<datosTrain.length; i++) {
              trainRealClass[i][0] = clasesTrain[i];
              trainPrediction[i][0] = KNN.evaluate(datosTrain[i],conjS, nClases, clasesS, 1);
          }
                 
          KNN.writeOutput(ficheroSalida[0], trainRealClass, trainPrediction,  entradas, salida, relation);
                 
                 
        //Working on test
		int realClass[][] = new int[datosTest.length][1];
		int prediction[][] = new int[datosTest.length][1];	
		
		//Check  time		
				
		for (i=0; i<realClass.length; i++) {
			realClass[i][0] = clasesTest[i];
			prediction[i][0]= KNN.evaluate(datosTest[i],conjS, nClases, clasesS, 1);
		}
                
         KNN.writeOutput(ficheroSalida[1], realClass, prediction,  entradas, salida, relation);

                
                
	}
	
	private double computeCorrelation (int c1, int c2, int W[], int B[][][]) {
	
		int i;
		int in, bet;
		double dif = 0;
		double cont1 = 0, cont2 = 0;
		
		for (i=0; i<datosTrain.length; i++) {
			if (clasesTrain[i] == c1 || clasesTrain[i] == c2) {
				in = W[i];
				bet = B[c1][c2][i];
				dif += Math.abs(in - bet);
				if (clasesTrain[i] == c1)
					cont1++;
				else
					cont2++;
			}
		}
		if (Math.min(cont1, cont2) > 0)
			return dif/Math.min(cont1, cont2);
		else
			return Double.POSITIVE_INFINITY;
	}
	
	private double kDivergence (double p1, double p2) {		
	
		if (p1 == 0)
			return 0;
		return p1 * Math.log(p1 / (0.5*p1 + 0.5*p2));			
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

		/*Getting the type of distance function*/
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer (linea, "=");
		tokens.nextToken();
		distanceEu = tokens.nextToken().substring(1).equalsIgnoreCase("Euclidean")?true:false;    
	}
}

