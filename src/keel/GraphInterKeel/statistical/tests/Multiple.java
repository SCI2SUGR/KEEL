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
 * File: Multiple.java
 * 
 * This class performs several statistical comparisons between NxN methods
 * 
 * @author Written by Joaqu�n Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical.tests;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import keel.GraphInterKeel.statistical.Configuration;
import org.core.*;

public class Multiple {
	
	private static boolean Iman, Nemenyi, Bonferroni, Holm, Hoch, Hommel, Scha, Berg; //post-hoc methods to apply
	private static final int MAX_ALGORITHMS = 9;
	/**
	* Builder
	*/
	public Multiple(){
		
	}//end-method
	
    /**
     * <p>
     * In this method, all possible post hoc statistical test between more than three algorithms results 
     * are executed, according to the configuration file
     * @param algorithms A vector of String with the names of the algorithms
     * </p>
     */
    public static void doMultiple(double data[][], String algorithms[]) {

		String outputFileName = Configuration.getPath();

        String outputString = new String("");
	    outputString = header();

	    outputString += runMultiple(data, algorithms);

	    Files.writeFile(outputFileName, outputString);

    }//end-method
    
	/**
	* This method runs the multiple comparison tests
	*
	* @param code A value to codify which post-hoc methods apply
	* @param results Array with the results of the methods
	* @param algorithmName Array with the name of the methods employed
	*
	* @return A string with the contents of the test in LaTeX format
	*/
    private static String runMultiple(double[][] results,String algorithmName[]) {
    	
    	int i, j, k;
    	int posicion;
    	double mean[][];
    	MultiplePair orden[][];
    	MultiplePair rank[][];
    	boolean encontrado;
    	int ig;
    	double sum;
    	boolean visto[];
    	Vector<Integer> porVisitar;
    	double Rj[];
    	double friedman;
    	double sumatoria=0;
    	double termino1, termino2, termino3;
    	double iman;
    	boolean vistos[];
    	int pos, tmp, counter;
    	String cad;
    	double maxVal;
    	double Pi[];
    	double ALPHAiHolm[];
    	double ALPHAiShaffer[];
    	String ordenAlgoritmos[];
    	double ordenRankings[];
    	int order[];
    	double adjustedP[][];
    	double SE;
    	boolean parar;
    	Vector<Integer> indices = new Vector<Integer>();
    	Vector<Vector<Relation>> exhaustiveI = new Vector<Vector<Relation>>();
    	boolean[][] cuadro;
    	double minPi, tmpPi, maxAPi,tmpAPi;
    	Relation[] parejitas;
    	Vector<Integer> T;
    	int Tarray[];
    	
		DecimalFormat nf4 = (DecimalFormat) DecimalFormat.getInstance();
		nf4.setMaximumFractionDigits(4);
		nf4.setMinimumFractionDigits(0);

		DecimalFormatSymbols dfs = nf4.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf4.setDecimalFormatSymbols(dfs);
		
		DecimalFormat nf6 = (DecimalFormat) DecimalFormat.getInstance();
		nf6.setMaximumFractionDigits(6);
		nf6.setMinimumFractionDigits(0);

		nf6.setDecimalFormatSymbols(dfs); 
    	
    	String out="";

        int nDatasets = Configuration.getNDatasets();
            
        Iman = Configuration.isIman();
        Nemenyi = Configuration.isNemenyi();
        Bonferroni = Configuration.isBonferroni();
        Holm = Configuration.isHolm();
        Hoch = Configuration.isHochberg();
        Hommel = Configuration.isHommel();
        Scha = Configuration.isShaffer();
        Berg = Configuration.isBergman();
	
    	mean = new double[nDatasets][algorithmName.length];

		//Maximize performance
        if(Configuration.getObjective()==1){

            /*Compute the average performance per algorithm for each data set*/
            for (i=0; i<nDatasets; i++) {
                for (j=0; j<algorithmName.length; j++) {
                    mean[i][j] = results[j][i];
                }
            }


        }
        //Minimize performance
        else{

            double maxValue=Double.MIN_VALUE;
            
            /*Compute the average performance per algorithm for each data set*/
            for (i=0; i<nDatasets; i++) {
                for (j=0; j<algorithmName.length; j++) {

                    if(results[j][i]>maxValue){
                        maxValue=results[j][i];
                    }

                    mean[i][j] = (-1.0 * results[j][i]);

                }
            }

            for (i=0; i<nDatasets; i++) {
                for (j=0; j<algorithmName.length; j++) {
                    mean[i][j] += maxValue;
                }
            }

        }
       
    	/*We use the pareja structure to compute and order rankings*/
    	orden = new MultiplePair[nDatasets][algorithmName.length];
    	for (i=0; i<nDatasets; i++) {
    		for (j=0; j<algorithmName.length; j++){
    			orden[i][j] = new MultiplePair (j,mean[i][j]);
    		}
    		Arrays.sort(orden[i]);

    	}

    	/*building of the rankings table per algorithms and data sets*/
    	rank = new MultiplePair[nDatasets][algorithmName.length];
    	posicion = 0;
    	for (i=0; i<nDatasets; i++) {
    		for (j=0; j<algorithmName.length; j++){
    			encontrado = false;
    		    for (k=0; k<algorithmName.length && !encontrado; k++) {
    		    	if (orden[i][k].indice == j) {
    		    		encontrado = true;
    		    		posicion = k+1;
    		    	}
    		    }
    		    rank[i][j] = new MultiplePair(posicion,orden[i][posicion-1].valor);
    		}
    	}

    	/*In the case of having the same performance, the rankings are equal*/
    	for (i=0; i<nDatasets; i++) {
    		visto = new boolean[algorithmName.length];
    		porVisitar= new Vector<Integer>();

    		Arrays.fill(visto,false);
    		for (j=0; j<algorithmName.length; j++) {
    			porVisitar.removeAllElements();
    		    sum = rank[i][j].indice;
    		    visto[j] = true;
    		    ig = 1;
    		    for (k=j+1;k<algorithmName.length;k++) {
    		    	if (rank[i][j].valor == rank[i][k].valor && !visto[k]) {
    		    		sum += rank[i][k].indice;
    		    		ig++;
    		    		porVisitar.add(new Integer(k));
    		    		visto[k] = true;
    		    	}
    		    }
    		    sum /= (double)ig;
    		    rank[i][j].indice = sum;
    		    for (k=0; k<porVisitar.size(); k++) {
    		    	rank[i][((Integer)porVisitar.elementAt(k)).intValue()].indice = sum;
    		    }
    		}
    	}

    	/*compute the average ranking for each algorithm*/
    	Rj = new double[algorithmName.length];
    	for (i=0; i<algorithmName.length; i++){
    		Rj[i] = 0;
    		for (j=0; j<nDatasets; j++) {
    		    Rj[i] += rank[j][i].indice / ((double)nDatasets);
    		}
    	}

    	/*Print the average ranking per algorithm*/
    	out+="\n\nAverage ranks obtained by applying the Friedman procedure\n\n";
    	
    	out+="\\begin{table}[!htp]\n" +
    		"\\centering\n" +
    	    "\\begin{tabular}{|c|c|}\\hline\n" +
    	    "Algorithm&Ranking\\\\\\hline\n";
    	    for (i=0; i<algorithmName.length;i++) {
    	        out+=(String)algorithmName[i]+" & "+nf4.format(Rj[i])+"\\\\\n";
    	    }
    	out+="\\hline\n\\end{tabular}\n\\caption{Average Rankings of the algorithms}\n\\end{table}";

    	/*Compute the Friedman statistic*/
    	termino1 = (12*(double)nDatasets)/((double)algorithmName.length*((double)algorithmName.length+1));
    	termino2 = (double)algorithmName.length*((double)algorithmName.length+1)*((double)algorithmName.length+1)/(4.0);
    	for (i=0; i<algorithmName.length;i++) {
    		 sumatoria += Rj[i]*Rj[i];
    	}
    	friedman = (sumatoria - termino2) * termino1;
    	out+="\n\nFriedman statistic considering reduction performance (distributed according to chi-square with "
    		+(algorithmName.length-1)+" degrees of freedom: "+nf6.format(friedman)+".\n\n";

		double pFriedman;
        pFriedman = ChiSq(friedman, (algorithmName.length-1));

        out+="P-value computed by Friedman Test: " + pFriedman +".\\newline\n\n";
		
    	/*Compute the Iman-Davenport statistic*/
    	if(Iman){
			iman = ((nDatasets-1)*friedman)/(nDatasets*(algorithmName.length-1) - friedman);
			out+="Iman and Davenport statistic considering reduction performance (distributed according to F-distribution with "
    		+(algorithmName.length-1)+" and "+ (algorithmName.length-1)*(nDatasets-1) +" degrees of freedom: "+nf6.format(iman)+".\n\n";
			
			double pIman;
		
			pIman = FishF(iman, (algorithmName.length-1),(algorithmName.length-1) * (nDatasets - 1));
			out+="P-value computed by Iman and Daveport Test: " + pIman +".\\newline\n\n";
    	}
    	
    	termino3 = Math.sqrt((double)algorithmName.length*((double)algorithmName.length+1)/(6.0*(double)nDatasets));
    		    
    	out+="\n\n\\pagebreak\n\n";
    		    
    	/************ NxN COMPARISON **************/	    

    	out+="\\section{Post hoc comparisons}";
    	out+="\n\nResults achieved on post hoc comparisons for $\\alpha = 0.05$, $\\alpha = 0.10$ and adjusted p-values.\n\n";
    	/*Compute the unadjusted p_i value for each comparison alpha=0.05*/	    
    	Pi = new double[(int)combinatoria(2,algorithmName.length)];
    	ALPHAiHolm = new double[(int)combinatoria(2,algorithmName.length)];
    	ALPHAiShaffer = new double[(int)combinatoria(2,algorithmName.length)];
    	ordenAlgoritmos = new String[(int)combinatoria(2,algorithmName.length)];
    	ordenRankings = new double[(int)combinatoria(2,algorithmName.length)];
    	order = new int[(int)combinatoria(2,algorithmName.length)];
    	parejitas = new Relation[(int)combinatoria(2,algorithmName.length)];
    	T = new Vector<Integer>();
    	T = trueHShaffer(algorithmName.length);
    	Tarray = new int[T.size()];
    	for (i=0; i<T.size(); i++) {
    		 Tarray[i] = ((Integer)T.elementAt(i)).intValue();
    	}
    	Arrays.sort(Tarray);

    	SE = termino3;
    	vistos = new boolean[(int)combinatoria(2,algorithmName.length)];
    	for (i=0, k=0; i<algorithmName.length;i++) {
    		for (j=i+1; j<algorithmName.length;j++,k++) {
    		    ordenRankings[k] = Math.abs(Rj[i] -Rj[j]);
    		    ordenAlgoritmos[k] = (String)algorithmName[i] + " vs. " + (String)algorithmName[j];
    		    parejitas[k] = new Relation(i,j);
    		}
    	}
    		    
    	Arrays.fill(vistos,false);
    	for (i=0; i<ordenRankings.length; i++) {
    		for (j=0;vistos[j]==true;j++);
    		pos = j;
    		maxVal = ordenRankings[j];
    		for (j=j+1;j<ordenRankings.length;j++) {
    		    if (vistos[j] == false && ordenRankings[j] > maxVal) {
    		    	pos = j;
    		    	maxVal = ordenRankings[j];
    		    }
    		}
    		vistos[pos] = true;
    		order[i] = pos;
    	}
    		    
    	/*Computing the logically related hypotheses tests (Shaffer and Bergmann-Hommel)*/
    	
    	pos = 0;
    	tmp = Tarray.length-1;
    	for (i=0; i<order.length; i++) {
    		Pi[i] = 2*CDF_Normal.normp((-1)*Math.abs((ordenRankings[order[i]])/SE));
    		ALPHAiHolm[i] = 0.05/((double)order.length-(double)i);
    		ALPHAiShaffer[i] = 0.05/((double)order.length-(double)Math.max(pos,i));
    		if (i == pos && Pi[i] <= ALPHAiShaffer[i]) {
    		    tmp--;
    		    pos = (int)combinatoria(2,algorithmName.length) - Tarray[tmp];
    		}
    	}
    		
    	out+="\\subsection{P-values for $\\alpha=0.05$}\n\n";
    	
    	int count=4;
    	
    	if(Holm){
    		count++;
    	}
    	if(Scha){
    		count++;
    	}
    	out+="\\begin{table}[!htp]\n\\centering\\scriptsize\n" +
    	     "\\begin{tabular}{"+printC(count)+"}\n" +
    	     "$i$&algorithms&$z=(R_0 - R_i)/SE$&$p$";
    	if(Holm){
    		out+="&Holm";
    	}
    	if(Scha){
    		out+="&Shaffer";
    	}

    	out+="\\\\\n\\hline";
    	        
    	for (i=0; i<order.length; i++) {
    	    out+=(order.length-i) + "&" + ordenAlgoritmos[order[i]] + "&" +
    	    nf6.format(Math.abs((ordenRankings[order[i]])/SE)) + "&" +
    	    nf6.format(Pi[i]); 
    	    if(Holm){
    	    	out+="&" + nf6.format(ALPHAiHolm[i]);
    	    }
    	    if(Scha){
    	    	out+="&" + nf6.format(ALPHAiShaffer[i]);
    	    }
    	    out+="\\\\\n";
    	}
    		    
    	out+="\\hline\n" + "\\end{tabular}\n\\caption{P-values Table for $\\alpha=0.05$}\n" + "\\end{table}";  	        
    		    
    	/*Compute the rejected hipotheses for each test*/
    	
    	if(Nemenyi){
    		out+="Nemenyi's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)(order.length))+"$.\n\n";
    	}
    	
    	if(Holm){
    		parar = false;
    		for (i=0; i<order.length && !parar; i++) {
    			if (Pi[i] > ALPHAiHolm[i]) {	    		
    		    	out+="Holm's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
    		    	parar = true;
    			}
    		}
    	}
    	
    	if(Scha){
    		parar = false;
    		for (i=0; i<order.length && !parar; i++) {
    			if (Pi[i] <= ALPHAiShaffer[i]) {	    		
    				out+="Shaffer's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiShaffer[i])+"$.\n\n";
    				parar = true;
    			}
    		}
    	}
    		    
    	/*For Bergmann-Hommel's procedure, 9 algorithms could suppose intense computation*/
    	if ((algorithmName.length <= MAX_ALGORITHMS)&&(Berg)) {
    		for (i=0; i<algorithmName.length; i++) {
    			 indices.add(new Integer(i));
    		}	    	
    		exhaustiveI = obtainExhaustive(indices);
    		cuadro = new boolean[algorithmName.length][algorithmName.length];
    		for (i=0; i<algorithmName.length; i++) {
    			Arrays.fill(cuadro[i], false);
    		}
    		        
    		for (i=0; i<exhaustiveI.size(); i++) {	
    	        minPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(0)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(0)).j])/SE);
    		    
    	        for (j=1; j<((Vector<Relation>)exhaustiveI.elementAt(i)).size(); j++) {
    		    	tmpPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).j])/SE);
    		        if (tmpPi < minPi) {
    		        	minPi = tmpPi;
    		        }
    		    }
    		    if (minPi > (0.05/((double)((Vector<Relation>)exhaustiveI.elementAt(i)).size()))) {	        		
    			    for (j=0; j<((Vector<Relation>)exhaustiveI.elementAt(i)).size(); j++) {
    			        cuadro[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).i][((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).j] = true;
    			    }	        		
    		    }
    		}
    		
    		if(Berg){
    			cad="";
	    		cad+="Bergmann's procedure rejects these hypotheses:\n\n";
	    		cad+="\\begin{itemize}\n\n";
	    		
	    		counter=0;
	    		for (i=0; i<cuadro.length;i++) {
	    			for (j=i+1; j<cuadro.length;j++) {					
	    				if (cuadro[i][j] == false) {
	    					cad+="\\item "+algorithmName[i]+" vs. "+algorithmName[j]+"\n\n";
	    					counter++;
	    				}
	    			}
	    		}
	    		cad+="\\end{itemize}\n\n";
	    		
	    		if(counter>0){
	    			out+=cad;
	    		}
	    		else{
	    			out+="Bergmann's procedure does not reject any hypotheses.\n\n";
	    		}
    		}
    	}
 		
    	out+="\\pagebreak\n\n";
    	out+="\\subsection{P-values for $\\alpha=0.10$}\n\n";
    	
    	/*Compute the unadjusted p_i value for each comparison alpha=0.10*/	    
    	Pi = new double[(int)combinatoria(2,algorithmName.length)];
    	ALPHAiHolm = new double[(int)combinatoria(2,algorithmName.length)];
    	ALPHAiShaffer = new double[(int)combinatoria(2,algorithmName.length)];
    	ordenAlgoritmos = new String[(int)combinatoria(2,algorithmName.length)];
    	ordenRankings = new double[(int)combinatoria(2,algorithmName.length)];
    	order = new int[(int)combinatoria(2,algorithmName.length)];

    	SE = termino3;
    	vistos = new boolean[(int)combinatoria(2,algorithmName.length)];
    	for (i=0, k=0; i<algorithmName.length;i++) {
    		 for (j=i+1; j<algorithmName.length;j++,k++) {
    		    ordenRankings[k] = Math.abs(Rj[i] -Rj[j]);
    		    ordenAlgoritmos[k] = (String)algorithmName[i] + " vs. " + (String)algorithmName[j];
    		 }
    	}
    		    
    	Arrays.fill(vistos,false);
    	for (i=0; i<ordenRankings.length; i++) {
    		for (j=0;vistos[j]==true;j++);
    		pos = j;
    		maxVal = ordenRankings[j];
    		
    		for (j=j+1;j<ordenRankings.length;j++) {
    		    if (vistos[j] == false && ordenRankings[j] > maxVal) {
    		    	pos = j;
    		    	maxVal = ordenRankings[j];
    		    }
    		}
    		vistos[pos] = true;
    		order[i] = pos;
    	}
    		    
    	/*Computing the logically related hypotheses tests (Shaffer and Bergmann-Hommel)*/
    	pos = 0;	    
    	tmp = Tarray.length-1;
    	for (i=0; i<order.length; i++) {
    		Pi[i] = 2*CDF_Normal.normp((-1)*Math.abs((ordenRankings[order[i]])/SE));
    		ALPHAiHolm[i] = 0.1/((double)order.length-(double)i);
    		ALPHAiShaffer[i] = 0.1/((double)order.length-(double)Math.max(pos,i));
    		if (i == pos && Pi[i] <= ALPHAiShaffer[i]) {
    		    tmp--;
    		    pos = (int)combinatoria(2,algorithmName.length) - Tarray[tmp];
    		}
    	}

    	
    	count=4;
    	
    	if(Holm){
    		count++;
    	}
    	if(Scha){
    		count++;
    	}
    	out+="\\begin{table}[!htp]\n\\centering\\scriptsize\n" +
	     "\\begin{tabular}{"+printC(count)+"}\n" +
	     "$i$&algorithms&$z=(R_0 - R_i)/SE$&$p$";
    	if(Holm){
    		out+="&Holm";
    	}
    	if(Scha){
    		out+="&Shaffer";
    	}
    	out+="\\\\\n\\hline";
    	        
    	for (i=0; i<order.length; i++) {
    		out+=(order.length-i) + "&" + ordenAlgoritmos[order[i]] + "&" +
    		nf6.format(Math.abs((ordenRankings[order[i]])/SE)) + "&" +
    		nf6.format(Pi[i]);
    		if(Holm){
    	    	out+="&" + nf6.format(ALPHAiHolm[i]);
    	    }
    	    if(Scha){
    	    	out+="&" + nf6.format(ALPHAiShaffer[i]);
    	    }
    	    out+="\\\\\n";
    	}
    		    
    	out+="\\hline\n" + "\\end{tabular}\n\\caption{P-values Table for $\\alpha=0.10$}\n" + "\\end{table}";
    	        	    
    	/*Compute the rejected hipotheses for each test*/
    	
    	if(Nemenyi){
    		out+="Nemenyi's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.10/(double)(order.length))+"$.\n\n";
    	}  
    	
    	if(Holm){
    		parar = false;
    	
    		for (i=0; i<order.length && !parar; i++) {
    			if (Pi[i] > ALPHAiHolm[i]) {	    		
    				out+="Holm's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
    				parar = true;
    			}
    		}
    	}

    	if(Scha){
    		parar = false;
    		for (i=0; i<order.length && !parar; i++) {
    			if (Pi[i] <= ALPHAiShaffer[i]) {	    		
    				out+="Shaffer's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiShaffer[i])+"$.\n\n";
    				parar = true;
    			}
    		}
    	}
    		    
    	/*For Bergmann-Hommel's procedure, 9 algorithms could suppose intense computation*/
    	if ((algorithmName.length <= MAX_ALGORITHMS)&(Berg)) {
    		
    		indices.removeAllElements();
    		for (i=0; i<algorithmName.length; i++) {
    			indices.add(new Integer(i));
    		}
    		
    		exhaustiveI = obtainExhaustive(indices);
    		cuadro = new boolean[algorithmName.length][algorithmName.length];
    		
    		for (i=0; i<algorithmName.length; i++) {
    			Arrays.fill(cuadro[i], false);
    		}
    		
    		for (i=0; i<exhaustiveI.size(); i++) {	
    			minPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(0)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(0)).j])/SE);
    		    for (j=1; j<((Vector<Relation>)exhaustiveI.elementAt(i)).size(); j++) {
    		        tmpPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).j])/SE);
    		        if (tmpPi < minPi) {
    		        	minPi = tmpPi;
    		        }
    		    }
    		        	
    		    if (minPi > 0.1/((double)((Vector<Relation>)exhaustiveI.elementAt(i)).size())) {	        		
    			    for (j=0; j<((Vector<Relation>)exhaustiveI.elementAt(i)).size(); j++) {
    			        cuadro[((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).i][((Relation)((Vector<Relation>)exhaustiveI.elementAt(i)).elementAt(j)).j] = true;
    			    }	        		
    		    }
    		}
    		 
    		if(Berg){
    			
    			cad="";
	    		cad+="Bergmann's procedure rejects these hypotheses:\n\n";
	    		cad+="\\begin{itemize}\n\n";
	    		
	    		counter=0;
	    		for (i=0; i<cuadro.length;i++) {
	    			for (j=i+1; j<cuadro.length;j++) {					
	    				if (cuadro[i][j] == false) {
	    					cad+="\\item "+algorithmName[i]+" vs. "+algorithmName[j]+"\n\n";
	    					counter++;
	    				}
	    			}
	    		}
	    		cad+="\\end{itemize}\n\n";
	    		
	    		if(counter>0){
	    			out+=cad;
	    		}
	    		else{
	    			out+="Bergmann's procedure does not reject any hypotheses.\n\n";
	    		}

    		}
    	}
    	
    	out+="\\pagebreak\n\n";
    	
    	/************ ADJUSTED P-VALUES NxN COMPARISON **************/	    

    	out+="\\subsection{Adjusted p-values}\n\n";
    	
    	adjustedP = new double[Pi.length][4];
    	pos = 0;
    	tmp = Tarray.length-1;
    		    
    	for (i=0; i<adjustedP.length; i++) {
    		adjustedP[i][0] = Pi[i] * (double)(adjustedP.length);
    		adjustedP[i][1] = Pi[i] * (double)(adjustedP.length-i);
    		adjustedP[i][2] = Pi[i] * ((double)adjustedP.length-(double)Math.max(pos,i));
    		    	
    		if (i == pos) {
    		    tmp--;
    		    pos = (int)combinatoria(2,algorithmName.length) - Tarray[tmp];
    		}
    		    	
    		if (algorithmName.length <= MAX_ALGORITHMS) {
    		    maxAPi = Double.MIN_VALUE;
    		    minPi = Double.MAX_VALUE;
    			for (j=0; j<exhaustiveI.size(); j++) {
    			    if (exhaustiveI.elementAt(j).toString().contains(parejitas[order[i]].toString())) {
    			        minPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(j)).elementAt(0)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(j)).elementAt(0)).j])/SE);
    				    for (k=1; k<((Vector<Relation>)exhaustiveI.elementAt(j)).size(); k++) {
    				        tmpPi = 2*CDF_Normal.normp((-1)*Math.abs(Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(j)).elementAt(k)).i] - Rj[((Relation)((Vector<Relation>)exhaustiveI.elementAt(j)).elementAt(k)).j])/SE);
    				        if (tmpPi < minPi) {
    				        	minPi = tmpPi;
    				        }
    				    }		        		
    				    tmpAPi = minPi * (double)(((Vector<Relation>)exhaustiveI.elementAt(j)).size());
    				    if (tmpAPi > maxAPi) {
    				        maxAPi = tmpAPi;
    				    }			     
    			    }
    			}	    		
    			
    			adjustedP[i][3] = maxAPi;
    		}
    	}
    		    
    	for (i=1; i<adjustedP.length; i++) {
    		if (adjustedP[i][1] < adjustedP[i-1][1])
    		    adjustedP[i][1] = adjustedP[i-1][1];
    		if (adjustedP[i][2] < adjustedP[i-1][2])
    		    adjustedP[i][2] = adjustedP[i-1][2];
    		if (adjustedP[i][3] < adjustedP[i-1][3])
    		    adjustedP[i][3] = adjustedP[i-1][3];
    	}

    			
    	count=3;
        
    	if(Nemenyi){
            count++;
        }	
        if(Holm){
        	count++;
        }
        if(Scha){
        	count++;
        }
        if(Berg){
        	count++;
        }

    	out+="\\begin{table}[!htp]\n\\centering\\scriptsize\n" +
    	           		"\\begin{tabular}{"+printC(count)+"}\n" +
    	           		"i&hypothesis&unadjusted $p$";
    	
    	if(Nemenyi){
    		out+="&$p_{Neme}$";
        }	
        if(Holm){
        	out+="&$p_{Holm}$";
        }
        if(Scha){
        	out+="&$p_{Shaf}$";
        }
        if(Berg){
        	out+="&$p_{Berg}$"; 
        }

    	out+="\\\\\n\\hline";
    	
    	for (i=0; i<Pi.length; i++) {	    	
    	        out+=(i+1) + "&" + algorithmName[parejitas[order[i]].i] + " vs ." + algorithmName[parejitas[order[i]].j] + "&" + nf6.format(Pi[i]);
    	        if(Nemenyi){
    	        	out+="&" + nf6.format(adjustedP[i][0]);
    	        }
    	        if(Holm){
    	        	out+="&" + nf6.format(adjustedP[i][1]);
    	        }
    	        if(Scha){
    	        	out+="&" + nf6.format(adjustedP[i][2]);
    	        }
    	        if(Berg){
    	        	out+="&" + nf6.format(adjustedP[i][3]);
    	        }

    	        out+="\\\\\n";
    	}
    		    
    	out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values}\n" + "\\end{table}\n\n";
    	out+="\\end{landscape}\n\\end{document}";
    	
    	return out;
    	
    }//end-method
    
	
	/**
	* Obtain all exhaustive comparisons possible from an array of indexes
	*
	* @param indices A verctos of indexes.
	*
	* @return A vector with vectors containing all the possible relations between the indexes 
	*/
    @SuppressWarnings("unchecked")
	public static Vector<Vector<Relation>> obtainExhaustive (Vector<Integer> indices) {
		
		Vector<Vector<Relation>> result = new Vector<Vector<Relation>>();
		int i,j,k;
		String binario;
		boolean[] number = new boolean[indices.size()];
		Vector<Integer> ind1, ind2;
		Vector<Relation> set = new Vector<Relation>();
		Vector<Vector<Relation>> res1, res2;
		Vector<Relation> temp;
		Vector<Relation> temp2;
		Vector<Relation> temp3;

		ind1 = new Vector<Integer>();
		ind2 = new Vector<Integer>();
		temp = new Vector<Relation>();
		temp2 = new Vector<Relation>();
		temp3 = new Vector<Relation>();
		
		for (i=0; i<indices.size();i++) {
			for (j=i+1; j<indices.size();j++) {
				set.addElement(new Relation(((Integer)indices.elementAt(i)).intValue(),((Integer)indices.elementAt(j)).intValue()));
			}
		}
		if (set.size()>0)
			result.addElement(set);
		
		for (i=1; i<(int)(Math.pow(2, indices.size()-1)); i++) {
			Arrays.fill(number, false);
			ind1.removeAllElements();
			ind2.removeAllElements();
			temp.removeAllElements();
			temp2.removeAllElements();
			temp3.removeAllElements();
			binario = Integer.toString(i, 2);
			for (k=0; k<number.length-binario.length();k++) {
				number[k] = false;
			}
			for (j=0; j<binario.length();j++,k++) {
				if (binario.charAt(j) == '1')
					number[k] = true;
			}
			for (j=0; j<number.length; j++) {
				if (number[j] == true) {
					ind1.addElement(new Integer(((Integer)indices.elementAt(j)).intValue()));					
				} else {					
					ind2.addElement(new Integer(((Integer)indices.elementAt(j)).intValue()));					
				}
			}
			res1 = obtainExhaustive (ind1);
			res2 = obtainExhaustive (ind2);
			for (j=0; j<res1.size();j++) {
				result.addElement(new Vector<Relation>((Vector<Relation>)res1.elementAt(j)));
			}
			for (j=0; j<res2.size();j++) {
				result.addElement(new Vector<Relation>((Vector<Relation>)res2.elementAt(j)));
			}
			for (j=0; j<res1.size();j++) {
				temp = (Vector<Relation>)((Vector<Relation>)res1.elementAt(j)).clone();
				for (k=0; k<res2.size();k++) {
					temp2 = (Vector<Relation>)temp.clone();
					temp3 = (Vector<Relation>) ((Vector<Relation>)res2.elementAt(k)).clone();
					if (((Relation)temp2.elementAt(0)).i < ((Relation)temp3.elementAt(0)).i) {
						temp2.addAll((Vector<Relation>)temp3);					
						result.addElement(new Vector<Relation>(temp2));						
					} else {
						temp3.addAll((Vector<Relation>)temp2);					
						result.addElement(new Vector<Relation>(temp3));					
						
					}
				}
			} 
		}
		for (i=0;i<result.size();i++) {
			if (((Vector<Relation>)result.elementAt(i)).toString().equalsIgnoreCase("[]")) {
				result.removeElementAt(i);
				i--;
			}
		}
		for (i=0;i<result.size();i++) {
			for (j=i+1; j<result.size(); j++) {	
				if (((Vector<Relation>)result.elementAt(i)).toString().equalsIgnoreCase(((Vector<Relation>)result.elementAt(j)).toString())) {
					result.removeElementAt(j);
					j--;
				}
			}
		}
		return result;		
	}//end-method
	
	/**
	* Computes the (N/M) combinatory number
	*
	* @param n N value
	* @param m M value
	*
	* @return The (N/M) combinatory number
	*/
    public static double combinatoria (int m, int n) {

		double result = 1;
		int i;
		
		if (n >= m) {
			for (i=1; i<=m; i++)
				result *= (double)(n-m+i)/(double)i;
		} else {
			result = 0;
		}
		return result;
	}//end-method
    
	/**
	* Computes the trueHShaffer distribution from a given parameter.
	*
	* @param k K parameter
	*
	* @return The trueHShaffer distribution
	*/
    public static Vector<Integer> trueHShaffer (int k) {
		
		Vector<Integer> number;
		int j;
		Vector<Integer> tmp, tmp2;
		int p;
		
		number = new Vector<Integer>();
		tmp = new Vector<Integer>();
		if (k <= 1) {			
			number.addElement(new Integer(0));	
		} else {
			for (j=1; j<=k; j++) {
				tmp = trueHShaffer (k-j);
				tmp2 = new Vector<Integer>();
				for (p=0; p<tmp.size(); p++) {
					tmp2.addElement(((Integer)(tmp.elementAt(p))).intValue() + (int)combinatoria(2,j));
				}
				number = unionVectores (number,tmp2);
			}
		}
		
		return number;
	}//end-method
	
	/**
	* Joins two vectors 
	*
	* @param a First vector
	* @param b Second vector
	*
	* @return The joint of both vectors
	*/
	public static Vector<Integer> unionVectores (Vector<Integer> a, Vector<Integer> b) {

		int i;
		
		for (i=0; i<b.size(); i++) {
			if (a.contains(new Integer((Integer)(b.elementAt(i)))) == false) {
				a.addElement(b.elementAt(i));
			}			
		}
		
		return a;		
	}//end-method
	
	/**
	* Chi square distribution
	*
	* @param x Chi^2 value
	* @param n Degrees of freedom
	*
	* @return P-value associated
	*/
	private static double ChiSq(double x, int n) {
        if (n == 1 & x > 1000) {
            return 0;
        }
        if (x > 1000 | n > 1000) {
            double q = ChiSq((x - n) * (x - n) / (2 * n), 1) / 2;
            if (x > n) {
                return q;
            }
            {
                return 1 - q;
            }
        }
        double p = Math.exp( -0.5 * x);
        if ((n % 2) == 1) {
            p = p * Math.sqrt(2 * x / Math.PI);
        }
        double k = n;
        while (k >= 2) {
            p = p * x / k;
            k = k - 2;
        }
        double t = p;
        double a = n;
        while (t > 0.0000000001 * p) {
            a = a + 2;
            t = t * x / a;
            p = p + t;
        }
        return 1 - p;
    }
	
	/**
	* Fisher distribution
	*
	* @param f Fisher value
	* @param n1 N1 value
	* @param n2 N2 value
	*
	* @return P-value associated
	*/
	private static double FishF(double f, int n1, int n2) {
        double x = n2 / (n1 * f + n2);
        if ((n1 % 2) == 0) {
            return StatCom(1 - x, n2, n1 + n2 - 4, n2 - 2) * Math.pow(x, n2 / 2.0);
        }
        if ((n2 % 2) == 0) {
            return 1 -
                    StatCom(x, n1, n1 + n2 - 4, n1 - 2) *
                    Math.pow(1 - x, n1 / 2.0);
        }
        double th = Math.atan(Math.sqrt(n1 * f / (1.0*n2)));
        double a = th / (Math.PI / 2.0);
        double sth = Math.sin(th);
        double cth = Math.cos(th);
        if (n2 > 1) {
            a = a +
                sth * cth * StatCom(cth * cth, 2, n2 - 3, -1) / (Math.PI / 2.0);
        }
        if (n1 == 1) {
            return 1 - a;
        }
        double c = 4 * StatCom(sth * sth, n2 + 1, n1 + n2 - 4, n2 - 2) * sth *
                   Math.pow(cth, n2) / Math.PI;
        if (n2 == 1) {
            return 1 - a + c / 2.0;
        }
        int k = 2;
        while (k <= (n2 - 1) / 2.0) {
            c = c * k / (k - .5);
            k = k + 1;
        }
        return 1 - a + c;
    }
	
	/**
	* StatCom distribution
	*
	* @param q q parameter
	* @param i i parameter
	* @param j j parameter
	* @param b b parameter
	*
	* @return P-value associated
	*/
	private static double StatCom(double q, int i, int j, int b) {
        double zz = 1;
        double z = zz;
        int k = i;
        while (k <= j) {
            zz = zz * q * k / (k - b);
            z = z + zz;
            k = k + 2;
        }
        return z;
    }
	
	/**
	* Prints as many "c" as desired
	*
	* @param n Number of "c" to print
	*
	* @return A string with all the "c"s
	*/
	public static String printC(int n){
		
		String out="";
		
		for(int i=0;i<n;i++){
			out+="c";
		}
		
		return out;
	}//end-method
	
	/**
	* <p>
	* This method composes the header of the LaTeX file where the results are saved
	* </p>
	* @return A string with the header of the LaTeX file
	*/    
    private static String header() {
    	
        String output = new String("");
        output += "\\documentclass[a4paper,10pt]{article}\n";
        output += "\\usepackage{graphicx}\n";
        output += "\\usepackage{lscape}\n";
        output += "\\title{Output tables for the test of Multiple comparisons.}\n";
        output += "\\author{}\n\\date{\\today}\n\\begin{document}\n";
        output += "\\begin{landscape}\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\\section{Average rankings of Friedman test}\n\n";
        
        return output;

    }//end-method

}//end-class