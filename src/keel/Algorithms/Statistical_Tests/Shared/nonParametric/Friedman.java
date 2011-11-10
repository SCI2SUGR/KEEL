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
 * This class performs several statisticla comparisons between 1xN methods
 * 
 * @author Written by Joaquín Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import keel.Algorithms.Statistical_Tests.Shared.StatTest;

import org.core.*;

public class Friedman {
	
	private boolean Iman, Nemenyi, Bonferroni, Holm, Hoch, Hommel, Scha, Berg, Holland, Rom, Finner, Li; //post-hoc methods to apply
	
	private boolean Friedman, Alligned, Quade; //Main methods to apply
	
	/**
	* Builder
	*/
	public Friedman(){
		
	}//end-method
	
    /**
     * <p>
     * In this method, all possible post hoc statistical test between more than three algorithms results 
     * are executed, according to the configuration file
     * @param code A double that identifies which methods will be applied
     * @param nfold A vector of int with fold number by algorithm
     * @param algorithms A vector of String with the names of the algorithms
     * @param fileName A String with the name of the output file
	 * @param type Type of test to carry out
     * </p>
     */
    public void runPostHoc(double code, int nfold[], String algorithms[],
                         String fileName, int type) {
    	
    	//store type of test 
    	switch(type){
    	
    		case StatTest.FriedmanAlignedC:
    		case StatTest.FriedmanAlignedR:
    			
    			Alligned=true;
    			Friedman=false;
    			Quade=false;
    		
    		break;
    		
    		case StatTest.FriedmanC:
    		case StatTest.FriedmanR:
			case StatTest.FriedmanI:
    			
    			Alligned=false;
    			Friedman=true;
    			Quade=false;
    		
    		break;
    		
    		case StatTest.QuadeC:
    		case StatTest.QuadeR:
    			
    			Alligned=false;
    			Friedman=false;
    			Quade=true;
    		
    		break;
    		
    		default:
    			
    			Alligned=false;
				Friedman=false;
				Quade=false;
				
    		break;
    	
    	}

        int nAlgorithm = algorithms.length;
        String outputFileName = new String(""); //Final output file
        String[] aux = null;
        aux = fileName.split("/");
        for (int i = 0; i < 4; i++) {
            outputFileName += aux[i] + "/";
        }
        String outputString = new String("");
        outputString = header();
        
        //If the number of algorithms is less than three, the test cannot be applied
        if (nAlgorithm < 3){
          outputString +=
              "There are few algorithms to execute the non-parametric test\n";
          outputString +=
              "Please select THREE or more algorithms in order to have significative results\n";
            outputString += "\\end{document}";
        }else{

          //Number of files?
              int pointer=6;
              String myNumber="";

              while(aux[4].charAt(pointer)!='s'){
                myNumber+=aux[4].charAt(pointer);
                pointer++;
              }
              int nResults = Integer.parseInt(myNumber);

              nResults++; //The first is indexed by 0

          if (nResults > 3) {
            double[][] results = new double[nAlgorithm][nResults];
            for (int i = 0, j = 0; i < nResults; i++, j += 2) {
              String outputFile = outputFileName + "result" + i + "s0.stat";
              StringTokenizer line;
              String file = Fichero.leeFichero(outputFile); //file is an string containing the whole file
              line = new StringTokenizer(file, "\n\r\t");

              line.nextToken(); //Title
              line.nextToken(); //Subtitle
              for (int k = 0; k < nAlgorithm; k++) {
                line.nextToken(); //First algorithm
                for (int h = 0; h < nfold[k]; h++) {
                  line.nextToken(); //Todos los resultados
                }
                String result = line.nextToken(); //Mean Value: value
                StringTokenizer res = new StringTokenizer(result, " ");
                res.nextToken(); //mean
                res.nextToken(); //value:
				if (type != StatTest.FriedmanI)
                    results[k][i] = 1 - Double.parseDouble(res.nextToken()); //guess
                else
                    results[k][i] = Double.parseDouble(res.nextToken());
              }
            }
            outputString += runMultiple(code, results, algorithms);
          }
          else {
            outputString +=
                "There are few datasets to execute the non-parametric test\n";
            outputString +=
                "Please select FOUR or more data-sets in order to have significative results\n";
            outputString += "\\end{document}";
          }
        }

        outputFileName += "output.tex";
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
    private String runMultiple(double code, double[][] results,String algorithmName[]) {
    	
    	String out="";
    	
		int i, j, k, m;
		int posicion;
		int counter;
		double mean[][];
		double meanAR[][];
		Pair orden[][];
		Pair rank[][];
		Pair ordenAR[];
		Pair rankAR[];
		Pair bRank[];
		Pair bOrden[];
		double diffBlocks[];
		boolean encontrado;
		int ig;
		double sum;
		boolean visto[];
		Vector <Integer> porVisitar;
		double Rj[];
		double RjAR[];
		double Sj[];
		double SMj[];
		double min, max;
		double friedman;
		double sumatoria=0;
		double temporal;
		double termino1, termino2, termino3;
		double numerador, denominador;
		double iman;
		boolean vistos[];
		int pos;
		double maxVal, minVal;
		double diffMax = 0;
		double Pm = 0;
		double PmAR = 0;
		double PmQ = 0;		
		double rankingRef;
		double Pi[];
		double PiAR[];
		double PiQ[];
		double ALPHAiHolm[];
		double ALPHAiHolland[];
		double ALPHAiRom[];
		double ALPHAiFinner[];
		double ALPHA2Li = 0;
		double adjustedRom[];
		String ordenAlgoritmosF[];
		String ordenAlgoritmosAF[];
		String ordenAlgoritmosQ[];
		double adjustedP[][];
		double Ci[];
		double SE;
		boolean parar, otro;
		double pFriedman, pIman;

		decode(code);
		
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
		
        int nDatasets = results[0].length;
        int nAlgorithms = results.length;
            
        decode(code); //obtain the code of the p-value adjustment to apply
            
    	mean = new double[nDatasets][algorithmName.length];

    	/*Compute the average performance per algorithm for each data set*/
    	for (i=0; i<nDatasets; i++) {
    	    for (j=0; j<algorithmName.length; j++) {
    	    	mean[i][j] = results[j][i];
    	    }
    	}

		/** FRIEDMAN PROCEDURE ****************************************************************************************/

	    /*We use the Pair structure to compute and order rankings*/
	    orden = new Pair[nDatasets][nAlgorithms];
	    for (i=0; i<nDatasets; i++) {
	    	for (j=0; j<nAlgorithms; j++){
	    		orden[i][j] = new Pair (j,mean[i][j]);
	    	}
	    	Arrays.sort(orden[i]);
	    }

	    /*building of the rankings table per algorithms and data sets*/
	    rank = new Pair[nDatasets][nAlgorithms];
	    posicion = 0;
	    for (i=0; i<nDatasets; i++) {
	    	for (j=0; j<nAlgorithms; j++){
	    		encontrado = false;
	    		for (k=0; k<nAlgorithms && !encontrado; k++) {
	    			if (orden[i][k].indice == j) {
	    				encontrado = true;
	    				posicion = k+1;
	    			}
	    		}
	    		rank[i][j] = new Pair(posicion,orden[i][posicion-1].valor);
	    	}
	    }

	    /*In the case of having the same performance, the rankings are equal*/
	    for (i=0; i<nDatasets; i++) {
	    	visto = new boolean[nAlgorithms];
	    	porVisitar= new Vector <Integer> ();

	    	Arrays.fill(visto,false);
	    	for (j=0; j<nAlgorithms; j++) {
		    	porVisitar.removeAllElements();
	    		sum = rank[i][j].indice;
	    		visto[j] = true;
	    		ig = 1;
	    		for (k=j+1;k<nAlgorithms;k++) {
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
	    			rank[i][porVisitar.elementAt(k).intValue()].indice = sum;
	    		}
	    	}
	    }

	    /*compute the average ranking for each algorithm*/
	    Rj = new double[nAlgorithms];
	    for (i=0; i<nAlgorithms; i++){
	    	Rj[i] = 0;
	    	for (j=0; j<nDatasets; j++) {
	    		Rj[i] += rank[j][i].indice / ((double)nDatasets);
	    	}
	    }
	    
	    /**FRIEDMAN ALIGNED RANKS PROCEDURE *******************************************************************************/
	    
		meanAR = new double[nDatasets][nAlgorithms];
		/*Compute the average performance per algorithm for each data set*/
		for (i=0; i<nDatasets; i++) {
			sum = 0;
			for (j=0; j<nAlgorithms; j++) {
				sum += mean[i][j];
			}
			sum /= nAlgorithms;
			for (j=0; j<nAlgorithms; j++) {
				meanAR[i][j] = mean[i][j] - sum;
			}
		}

	    /*We use the Pair structure to compute and order rankings*/
	    ordenAR = new Pair[nDatasets * nAlgorithms];
	    for (i=0; i<nDatasets * nAlgorithms; i++) {
	    		ordenAR[i] = new Pair (i,meanAR[i/nAlgorithms][i%nAlgorithms]);
	    }
	    Arrays.sort(ordenAR);
		
	    /*building of the rankings table per algorithms and data sets*/
	    rankAR = new Pair[nDatasets * nAlgorithms];
	    posicion = 0;
	    for (i=0; i<nDatasets * nAlgorithms; i++) {
	    	encontrado = false;
	    	for (k=0; k<nAlgorithms * nDatasets && !encontrado; k++) {
	    		if (ordenAR[k].indice == i) {
	    			encontrado = true;
	    			posicion = k+1;
	    		}
	    	}
	    	rankAR[i] = new Pair(posicion,ordenAR[posicion-1].valor);
	    }
	    
	    /*In the case of having the same performance, the rankings are equal*/
	    visto = new boolean[nAlgorithms*nDatasets];
	    porVisitar= new Vector <Integer> ();

	    Arrays.fill(visto,false);
	    for (i=0; i<nAlgorithms*nDatasets; i++) {
	    	porVisitar.removeAllElements();
	    	sum = rankAR[i].indice;
	    	visto[i] = true;
	    	ig = 1;
	    	for (j=i+1;j<nAlgorithms*nDatasets;j++) {
	    		if (rankAR[i].valor == rankAR[j].valor && !visto[j]) {
	    			sum += rankAR[j].indice;
	    			ig++;
	    			porVisitar.add(new Integer(j));
	    			visto[j] = true;
	    		}
	    	}
	    	sum /= (double)ig;
	    	rankAR[i].indice = sum;
	    	for (j=0; j<porVisitar.size(); j++) {
	    		rankAR[porVisitar.elementAt(j).intValue()].indice = sum;
	    	}
	    }
   
	    /*compute the average ranking for each algorithm*/
	    RjAR = new double[nAlgorithms];
	    for (i=0; i<nAlgorithms; i++){
	    	RjAR[i] = 0;
	    	for (j=0; j<nDatasets; j++) {
	    		RjAR[i] += rankAR[j*nAlgorithms + i].indice / ((double)nDatasets);
	    	}
	    }
	    
	    /** QUADE TEST ***************************************************************************************************/
	    
	    diffBlocks = new double[nDatasets];
	    for (i=0; i<nDatasets; i++) {
	    	min = mean[i][0];
	    	max = mean[i][0];
	    	for (j=1; j<mean[i].length; j++) {
	    		if (mean[i][j] < min){
	    			min = mean[i][j];
	    		} else if (mean[i][j] > max) {
	    			max = mean[i][j];
	    		}
	    	}
	    	diffBlocks[i] = max - min;
	    }
	    
	    /*We use the Pair structure to compute and order rankings*/
	    bOrden = new Pair[nDatasets];
	    for (i=0; i<nDatasets; i++) {
	    	bOrden[i] = new Pair (i,diffBlocks[i]);
	    }
    	Arrays.sort(bOrden);

	    /*building of the rankings table per algorithms and data sets*/
	    bRank = new Pair[nDatasets];
	    posicion = 0;
	    for (i=0; i<nDatasets; i++){
	    	encontrado = false;
	    	for (j=0; j<nDatasets && !encontrado; j++) {
	    		if (bOrden[j].indice == i) {
	    			encontrado = true;
	    			posicion = j+1;
	    		}
	    	}
	    	bRank[i] = new Pair(nDatasets+1-posicion,bOrden[posicion-1].valor);
	    }

	    /*In the case of having the same performance, the rankings are equal*/
	    visto = new boolean[nDatasets];
	    porVisitar= new Vector <Integer> ();

	    Arrays.fill(visto,false);
	    for (i=0; i<nDatasets; i++) {
	    	porVisitar.removeAllElements();
	    	sum = bRank[i].indice;
	    	visto[i] = true;
	    	ig = 1;
	    	for (j=i+1;j<nDatasets;j++) {
	    		if (bRank[i].valor == bRank[j].valor && !visto[j]) {
	    			sum += bRank[j].indice;
	    			ig++;
	    			porVisitar.add(new Integer(j));
	    			visto[j] = true;
	    		}
	    	}
	    	sum /= (double)ig;
	    	bRank[i].indice = sum;
	    	for (j=0; j<porVisitar.size(); j++) {
	    		bRank[porVisitar.elementAt(j).intValue()].indice = sum;
	    	}
	    }

	    /*compute the average ranking for each algorithm*/
	    Sj = new double[nAlgorithms];
	    SMj = new double[nAlgorithms];
	    for (i=0; i<nAlgorithms; i++){
	    	SMj[i] = 0;
	    	for (j=0; j<nDatasets; j++) {
	    		Sj[i] += ((rank[j][i].indice)-(nAlgorithms+1)/2)* bRank[j].indice;
	    		SMj[i] += (rank[j][i].indice)* bRank[j].indice / (((double)nDatasets*(nDatasets+1))/2.0);
	    	}
	    }
	    
	    /** Print the results of multiple comparison tests ****************************************************************/
	    
	    if(Friedman){
	    	out+="\n\\section{Average rankings of Friedman test}\n\n";
	    	out+="\nAverage ranks obtained by each method in the Friedman test.\n\n";
	    	
	    	/*Print the average ranking per algorithm for Friedman*/
	    	out+="\\begin{table}[!htp]\n" +
	    		"\\centering\n" +
	    		"\\begin{tabular}{|c|c|}\\hline\n" +
	    		"Algorithm&Ranking\\\\\\hline\n";
	    	for (i=0; i<nAlgorithms;i++) {
	    		out+=algorithmName[i]+"&"+nf4.format(Rj[i])+"\\\\";
	    	}
	    	out+="\\hline\\end{tabular}\n\\caption{Average Rankings of the algorithms (Friedman)}\n\\end{table}";

	    	/*Compute the Friedman statistic*/
	    	termino1 = (12*(double)nDatasets)/((double)nAlgorithms*((double)nAlgorithms+1));
	    	termino2 = (double)nAlgorithms*((double)nAlgorithms+1)*((double)nAlgorithms+1)/(4.0);
	    	for (i=0; i<nAlgorithms;i++) {
	    		sumatoria += Rj[i]*Rj[i];
	    	}
	    	friedman = (sumatoria - termino2) * termino1;
	    	out+="\n\nFriedman statistic (distributed according to chi-square with "+(nAlgorithms-1)+" degrees of freedom): "+nf6.format(friedman)+". ";

        	
        	pFriedman = ChiSq(friedman, (nAlgorithms-1));

        	out+="\\newline P-value computed by Friedman Test: " + nf6.format(pFriedman) +".\\newline\n\n";
    
        	if(Iman){
	        	/*Compute the Iman-Davenport statistic*/
		    	iman = ((nDatasets-1)*friedman)/(nDatasets*(nAlgorithms-1) - friedman);
		    	out+="Iman and Davenport statistic (distributed according to F-distribution with "+(nAlgorithms-1)+" and "+ (nAlgorithms-1)*(nDatasets-1) +" degrees of freedom): "+nf6.format(iman)+". ";
	        	pIman = FishF(iman, (nAlgorithms-1),(nAlgorithms-1) * (nDatasets - 1));
	        	out+="\\newline P-value computed by Iman and Daveport Test: " + nf6.format(pIman) +".\\newline\n\n";
	    	}
        	
        	out+="\n\\newpage\n";
	    }
	    
	    termino3 = Math.sqrt((double)nAlgorithms*((double)nAlgorithms+1)/(6.0*(double)nDatasets));
	    
	    if(Alligned){
	    
	    	out+="\n\\section{Average rankings of Friedman Alligned test}\n\n";
	    
	    	out+="\nAverage ranks obtained by each method in the Friedman Alligned test.\n\n";
	    
	    	/*Print the average ranking per algorithm for Aligned Friedman*/
	    	out+="\\begin{table}[!htp]\n" +
	    	"\\centering\n" +
	    	"\\begin{tabular}{|c|c|}\\hline\n" +
	    	"Algorithm&Ranking\\\\\\hline\n";
	    	for (i=0; i<nAlgorithms;i++) {
	    		out+=(String)algorithmName[i]+"&"+nf4.format(RjAR[i])+"\\\\";
	    	}
	    	out+="\\hline\\end{tabular}\n\\caption{Average Rankings of the algorithms (Aligned Friedman)}\n\\end{table}";


	    	/*Compute the Aligned Friedman statistic*/
	    	termino1 = (double)nAlgorithms-1;
	    	termino2 = (double)nAlgorithms*((double)nDatasets*nDatasets)/4.0;
	    	termino2 *= ((double)nAlgorithms*(double)nDatasets + 1)*((double)nAlgorithms*(double)nDatasets + 1);
	    
	    	sumatoria = 0;
	    	for (i=0; i<nAlgorithms;i++) {
	    		temporal = 0;
	    		for (j=0; j<nDatasets; j++) {
	    			temporal += (double)rankAR[j*nAlgorithms+i].indice;
	    		}
	    		sumatoria += temporal * temporal;
	    	}
	    	sumatoria /= (double)nAlgorithms;
	    	numerador = sumatoria - termino2;
	    	numerador *= termino1;
	    
	    	termino1 = (double)nAlgorithms*(double)nDatasets*((double)nAlgorithms*(double)nDatasets + 1)* ((double)nAlgorithms*(double)nDatasets*2 + 1);
	    	termino1 /= 6;
	    
	    	sumatoria = 0;
	    	for (i=0; i<nDatasets;i++) {
	    		temporal = 0;
	    		for (j=0; j<nAlgorithms; j++) {
	    			temporal += (double)rankAR[i*nAlgorithms+j].indice;
	    		}
	    		sumatoria += temporal * temporal;
	    	}
	    	denominador = termino1 - sumatoria;
	    
	    	friedman = numerador / denominador;
	    	out+="\n\nAligned Friedman statistic (distributed according to chi-square with "+(nAlgorithms-1)+" degrees of freedom): "+nf6.format(friedman)+". ";

	    	pFriedman = ChiSq(friedman, (nAlgorithms-1));

	    	out+="\\newline P-value computed by Aligned Friedman Test: " + nf6.format(pFriedman) +".\\newline\n\n";
        
	    	out+="\n\\newpage\n";
	    }
	    
	    if(Quade){
	    	out+="\n\\section{Average rankings of Quade test}\n\n";
	    
	    	out+="\nAverage ranks obtained by each method in the Quade test.\n\n";
	    
	    	/*Print the average ranking per algorithm for Quade*/
	    	out+="\\begin{table}[!htp]\n" +
	    		"\\centering\n" +
	    		"\\begin{tabular}{|c|c|}\\hline\n" +
	    		"Algorithm&Ranking\\\\\\hline\n";
	    	for (i=0; i<nAlgorithms;i++) {
	    		out+=(String)algorithmName[i]+"&"+nf4.format(SMj[i])+"\\\\";
	    	}
	    	out+="\\hline\\end{tabular}\n\\caption{Average Rankings of the algorithms (Quade)}\n\\end{table}";
        

	    	/*Compute the Quade statistic*/
	    	sumatoria = 0;
	    	for (i=0; i<nAlgorithms; i++) {
	    		sumatoria += Sj[i]*Sj[i];
	    	}
	    	sumatoria /= nDatasets;
	    	termino1 = (nDatasets*(nDatasets+1)*(2*nDatasets+1)*nAlgorithms*(nAlgorithms+1)*(nAlgorithms-1)) / 72;
	    	iman = ((nDatasets-1)*sumatoria) / (termino1 - sumatoria);
	    	out+="Quade statistic (distributed according to F-distribution with "+(nAlgorithms-1)+" and "+ (nAlgorithms-1)*(nDatasets-1) +" degrees of freedom): "+nf6.format(iman)+". ";
	    	pIman = FishF(iman, (nAlgorithms-1),(nAlgorithms-1) * (nDatasets - 1));
	    	out+="\\newline P-value computed by Quade Test: " + nf6.format(pIman) +".\\newline\n\n";

	    	out+="\n\\newpage\n";
	    }
        
	    /************ COMPARING A CONTROL METHOD *************************************************************************/	    

		/*Compute the unadjusted p_i value for each comparison alpha=0.05*/
	    Pi = new double[nAlgorithms-1];
	    PiAR = new double[nAlgorithms-1];
	    PiQ = new double[nAlgorithms-1];
	    ALPHAiHolm = new double[nAlgorithms-1];
	    ALPHAiHolland = new double[nAlgorithms-1];
	    ALPHAiRom = new double[nAlgorithms-1];
	    ALPHAiFinner = new double[nAlgorithms-1];
	    ordenAlgoritmosF = new String[nAlgorithms-1];
	    ordenAlgoritmosAF = new String[nAlgorithms-1];
	    ordenAlgoritmosQ = new String[nAlgorithms-1];
	    adjustedRom = new double[nAlgorithms-1];

	    calcularROM(0.05,ALPHAiRom, adjustedRom);
	    
	    /** USING FRIEDMAN TEST ******************************************************************************************/
	    
	if(Friedman){
	    out+="\n\\section{Post hoc comparison (Friedman)}\n\n";
	    
	    out+="\nP-values obtained in by applying post hoc methods over the results of Friedman procedure.\n\n";
	    
	    SE = termino3;
	    vistos = new boolean[nAlgorithms];
	    rankingRef = 0.0;
	    Arrays.fill(vistos,false);
	    for (i=0; i<nAlgorithms;i++) {
	    	for (j=0;vistos[j]==true;j++);
	    	pos = j;
	    	maxVal = Rj[j];
	    	minVal = Rj[j];
	    	for (j=j+1;j<nAlgorithms;j++) {
	    		if (i > 1) {
	    			if (vistos[j] == false && Rj[j] > maxVal) {
	    				pos = j;
	    				maxVal = Rj[j];
	    			}
	    		} else if (i == 1) {
	    			if (vistos[j] == false && Rj[j] > maxVal) {
	    				pos = j;
	    				maxVal = Rj[j];
	    			}	    			
	    			if (vistos[j] == false && Rj[j] < minVal) {
	    				minVal = Rj[j];
	    			}	    			
	    		} else {
	    			if (vistos[j] == false && Rj[j] < maxVal) {
	    				pos = j;
	    				maxVal = Rj[j];
	    			}
	    		}
	    	}
	    	vistos[pos] = true;
	    	if (i==1) {
	    		diffMax = minVal - rankingRef;
		    	Pm = 2*CDF_Normal.normp((-1)*Math.abs((diffMax)/SE));
	    		ALPHA2Li = (1 - Pm) / (1 - 0.05) * 0.05;
	    	}
	    	if (i==0) {

	    		counter=4;
	    		
	    		if((Holm)||(Hoch)||(Hommel)){
	    			counter++;
	    		}
	    		if(Holland){
	    			counter++;
	    		}
	    		if(Rom){
	    			counter++;
	    		}
	    		if(Finner){
	    			counter++;
	    		}
	    		if(Li){
	    			counter++;
	    		}
	    		rankingRef = maxVal;
                out+="\\begin{table}[!htp]\n\\centering\\footnotesize\n" +
               		"\\begin{tabular}{"+printC(counter)+"}\n" +
               		"$i$&algorithm&$z=(R_0 - R_i)/SE$&$p$";
               if((Holm)||(Hoch)||(Hommel)){
            	   out+="&";
            	   if(Holm){
            		   out+="Holm ";
            	   }
            	   if(Hoch){
            		   out+="Hochberg ";
            	   }
            	   if(Hommel){
            		   out+="Hommel ";
            	   }
               }
               if(Holland){
            	   out+="&Holland";
               }
               if(Rom){
            	   out+="&Rom";
               }
               if(Finner){
            	   out+="&Finner";
               }
               if(Li){
            	   out+="&Li";
               }
               out+="\\\\\n\\hline";

	    	} else {
	    		ALPHAiHolm[i-1] = 0.05/((double)nAlgorithms-(double)i);
	    		ALPHAiHolland[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/((double)nAlgorithms-(double)i)));
	    		ALPHAiFinner[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/(((double)nAlgorithms-1)/(double)i)));
	    		ordenAlgoritmosF[i-1] = new String ((String)algorithmName[pos]);
	    		
                out+=(nAlgorithms-i) + "&" + algorithmName[pos] + "&" +
                	nf6.format(Math.abs((rankingRef-maxVal)/SE)) + "&" +
                	nf6.format(2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE)));
                	if((Holm)||(Hoch)||(Hommel)){
                		out+="&" + nf6.format(ALPHAiHolm[i-1]);
                	}
               		if(Holland){
               			out+="&" + nf6.format(ALPHAiHolland[i-1]);
               		}
                	if(Rom){
                		out+="&" + nf6.format(ALPHAiRom[i-1]);
                	}
                	if(Finner){
                		out+="&" + nf6.format(ALPHAiFinner[i-1]);
                	}
                	if(Li){
                    	out+="&" + ((i==(nAlgorithms-1))?(0.05):(nf6.format(ALPHA2Li)));
                	}
                	out+="\\\\";
           		Pi[i-1] = 2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE));
	    	}
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Post Hoc comparison Table for $\\alpha=0.05$ (FRIEDMAN)}\n" + "\\end{table}";
        
	    
	    /*Compute the rejected hypotheses for each test*/
	    
	    if(Bonferroni){
	    	out+="Bonferroni-Dunn's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)(nAlgorithms-1))+"$.\n\n";
	    }
	    
	    if(Holm){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (Pi[i] > ALPHAiHolm[i]) {	    		
		    		out+="Holm's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }

	    if(Hoch){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (Pi[i] <= ALPHAiHolm[i]) {	    		
	    			out+="Hochberg's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Hommel){
	    	otro = true;
	    	for (j=nAlgorithms-1; j>0 && otro; j--) {
	    		otro = false;
	    		for (k=1; k<=j && !otro; k++) {
	    			if (Pi[nAlgorithms-1-j+k-1] <= 0.05*(double)k/(double)j) {
	    				otro = true;
	    			}
	    		}
	    	}
	    	if (otro == true) {
	    		out+="Hommel's procedure rejects all hypotheses.\n\n";
	    	} else {
	    		j++;
	    		out+="Hommel's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)j)+"$.\n\n";
	    	}
	    }
	    
	    if(Holland){
	    	parar = false;
	    
	    	for (i=0; i<nAlgorithms-1 && !parar; i++) {
	    		if (Pi[i] > ALPHAiHolland[i]) {	    		
	    			out+="Holland's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolland[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Rom){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (Pi[i] <= ALPHAiRom[i]) {	    		
	    			out+="Rom's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiRom[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Finner){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (Pi[i] > ALPHAiFinner[i]) {	    		
		    		out+="Finner's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiFinner[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }
	    if(Li){
		    if (Pi[nAlgorithms-2] < 0.05) {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+0.05+"$.\n\n";
		    } else {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHA2Li)+"$.\n\n";	    	
		    }
	    }

	    out+="\n\\newpage\n";

	}
	    /** USING ALIGNED FRIEDMAN TEST *******************************************************************************************/
	
	if(Alligned){
	    out+="\n\\section{Post hoc comparison (Friedman Alligned)}\n\n";
	    
	    out+="\nP-values obtained in by applying post hoc methods over the results of Friedman Alligned procedure.\n\n";
	    
	    SE = Math.sqrt((double)nAlgorithms*((double)nDatasets*(double)nAlgorithms+1)/6.0);
	    vistos = new boolean[nAlgorithms];
	    rankingRef = 0.0;
	    Arrays.fill(vistos,false);
	    for (i=0; i<nAlgorithms;i++) {
	    	for (j=0;vistos[j]==true;j++);
	    	pos = j;
	    	maxVal = RjAR[j];
	    	minVal = RjAR[j];
	    	for (j=j+1;j<nAlgorithms;j++) {
	    		if (i > 1) {
	    			if (vistos[j] == false && RjAR[j] > maxVal) {
	    				pos = j;
	    				maxVal = RjAR[j];
	    			}
	    		} else if (i == 1) {
	    			if (vistos[j] == false && RjAR[j] > maxVal) {
	    				pos = j;
	    				maxVal = RjAR[j];
	    			}	    			
	    			if (vistos[j] == false && RjAR[j] < minVal) {
	    				minVal = RjAR[j];
	    			}	    			
	    		} else {
	    			if (vistos[j] == false && RjAR[j] < maxVal) {
	    				pos = j;
	    				maxVal = RjAR[j];
	    			}
	    		}
	    	}
	    	vistos[pos] = true;
	    	if (i==1) {
	    		diffMax = minVal - rankingRef;
		    	PmAR = 2*CDF_Normal.normp((-1)*Math.abs((diffMax)/SE));
	    		ALPHA2Li = (1 - PmAR) / (1 - 0.05) * 0.05;
	    	}
	    	if (i==0) {
	    		counter=4;
	    		
	    		if((Holm)||(Hoch)||(Hommel)){
	    			counter++;
	    		}
	    		if(Holland){
	    			counter++;
	    		}
	    		if(Rom){
	    			counter++;
	    		}
	    		if(Finner){
	    			counter++;
	    		}
	    		if(Li){
	    			counter++;
	    		}
	    		rankingRef = maxVal;  
                out+="\\begin{table}[!htp]\n\\centering\\footnotesize\n" +
               		"\\begin{tabular}{"+printC(counter)+"}\n" +
               		"$i$&algorithm&$z=(R_0 - R_i)/SE$&$p$";
               if((Holm)||(Hoch)||(Hommel)){
            	   out+="&";
            	   if(Holm){
            		   out+="Holm ";
            	   }
            	   if(Hoch){
            		   out+="Hochberg ";
            	   }
            	   if(Hommel){
            		   out+="Hommel ";
            	   }
               }
               if(Holland){
            	   out+="&Holland";
               }
               if(Rom){
            	   out+="&Rom";
               }
               if(Finner){
            	   out+="&Finner";
               }
               if(Li){
            	   out+="&Li";
               }
               out+="\\\\\n\\hline";

	    	} else {

	    		ALPHAiHolm[i-1] = 0.05/((double)nAlgorithms-(double)i);
	    		ALPHAiHolland[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/((double)nAlgorithms-(double)i)));
	    		ALPHAiFinner[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/(((double)nAlgorithms-1)/(double)i)));
	    		ordenAlgoritmosAF[i-1] = new String ((String)algorithmName[pos]);
	    		
                out+=(nAlgorithms-i) + "&" + algorithmName[pos] + "&" +
                nf6.format(Math.abs((rankingRef-maxVal)/SE)) + "&" +
               	nf6.format(2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE)));
               	if((Holm)||(Hoch)||(Hommel)){
                 	out+="&" + nf6.format(ALPHAiHolm[i-1]);
                }
                if(Holland){
                	out+="&" + nf6.format(ALPHAiHolland[i-1]);
                }
                if(Rom){
                 	out+="&" + nf6.format(ALPHAiRom[i-1]);
                }
                if(Finner){
                 	out+="&" + nf6.format(ALPHAiFinner[i-1]);
                 }
                if(Li){
                    out+="&" + ((i==(nAlgorithms-1))?(0.05):(nf6.format(ALPHA2Li)));
                }
                out+="\\\\";
           		PiAR[i-1] = 2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE));
            	
	    	}
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Post Hoc comparison Table for $\\alpha=0.05$ (FRIEDMAN ALLIGNED)}\n" + "\\end{table}";
        
	    /*Compute the rejected hypotheses for each test*/
	    
	    if(Bonferroni){
	    	out+="Bonferroni-Dunn's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)(nAlgorithms-1))+"$.\n\n";
	    }
	    
	    if(Holm){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (PiAR[i] > ALPHAiHolm[i]) {	    		
		    		out+="Holm's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }

	    if(Hoch){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (PiAR[i] <= ALPHAiHolm[i]) {	    		
	    			out+="Hochberg's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Hommel){
	    	otro = true;
	    	for (j=nAlgorithms-1; j>0 && otro; j--) {
	    		otro = false;
	    		for (k=1; k<=j && !otro; k++) {
	    			if (PiAR[nAlgorithms-1-j+k-1] <= 0.05*(double)k/(double)j) {
	    				otro = true;
	    			}
	    		}
	    	}
	    	if (otro == true) {
	    		out+="Hommel's procedure rejects all hypotheses.\n\n";
	    	} else {
	    		j++;
	    		out+="Hommel's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)j)+"$.\n\n";
	    	}
	    }
	    
	    if(Holland){
	    	parar = false;
	    
	    	for (i=0; i<nAlgorithms-1 && !parar; i++) {
	    		if (PiAR[i] > ALPHAiHolland[i]) {	    		
	    			out+="Holland's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolland[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Rom){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (PiAR[i] <= ALPHAiRom[i]) {	    		
	    			out+="Rom's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiRom[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Finner){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (PiAR[i] > ALPHAiFinner[i]) {	    		
		    		out+="Finner's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiFinner[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }
	    if(Li){
		    if (PiAR[nAlgorithms-2] < 0.05) {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+0.05+"$.\n\n";
		    } else {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHA2Li)+"$.\n\n";	    	
		    }
	    }

	    out+="\n\\newpage\n";
	}
	    /** USING QUADE TEST **********************************************************************************************/
	
	if(Quade){
		
	
	    out+="\n\\section{Post hoc comparison (Quade)}\n\n";
	    
	    out+="\nP-values obtained in by applying post hoc methods over the results of Quade procedure.\n\n";
	    
	    SE = Math.sqrt(((double)nAlgorithms*((double)nAlgorithms+1)*((double)nAlgorithms-1)*((double)nDatasets*2+1))/(18.0*(double)nDatasets*((double)nDatasets+1)));
	    vistos = new boolean[nAlgorithms];
	    rankingRef = 0.0;
	    Arrays.fill(vistos,false);
	    for (i=0; i<nAlgorithms;i++) {
	    	for (j=0;vistos[j]==true;j++);
	    	pos = j;
	    	maxVal = SMj[j];
	    	minVal = SMj[j];
	    	for (j=j+1;j<nAlgorithms;j++) {
	    		if (i > 1) {
	    			if (vistos[j] == false && SMj[j] > maxVal) {
	    				pos = j;
	    				maxVal = SMj[j];
	    			}
	    		} else if (i == 1) {
	    			if (vistos[j] == false && SMj[j] > maxVal) {
	    				pos = j;
	    				maxVal = SMj[j];
	    			}	    			
	    			if (vistos[j] == false && SMj[j] < minVal) {
	    				minVal = SMj[j];
	    			}	    			
	    		} else {
	    			if (vistos[j] == false && SMj[j] < maxVal) {
	    				pos = j;
	    				maxVal = SMj[j];
	    			}
	    		}
	    	}
	    	vistos[pos] = true;
	    	if (i==1) {
	    		diffMax = minVal - rankingRef;
		    	PmQ = 2*CDF_Normal.normp((-1)*Math.abs((diffMax)/SE));
	    		ALPHA2Li = (1 - PmQ) / (1 - 0.05) * 0.05;
	    	}
	    	
	    	if (i==0) {
	    		counter=4;
	    		
	    		if((Holm)||(Hoch)||(Hommel)){
	    			counter++;
	    		}
	    		if(Holland){
	    			counter++;
	    		}
	    		if(Rom){
	    			counter++;
	    		}
	    		if(Finner){
	    			counter++;
	    		}
	    		if(Li){
	    			counter++;
	    		}
	    		rankingRef = maxVal;
                out+="\\begin{table}[!htp]\n\\centering\\footnotesize\n" +
               		"\\begin{tabular}{"+printC(counter)+"}\n" +
               		"$i$&algorithm&$z=(R_0 - R_i)/SE$&$p$";
               if((Holm)||(Hoch)||(Hommel)){
            	   out+="&";
            	   if(Holm){
            		   out+="Holm ";
            	   }
            	   if(Hoch){
            		   out+="Hochberg ";
            	   }
            	   if(Hommel){
            		   out+="Hommel ";
            	   }
               }
               if(Holland){
            	   out+="&Holland";
               }
               if(Rom){
            	   out+="&Rom";
               }
               if(Finner){
            	   out+="&Finner";
               }
               if(Li){
            	   out+="&Li";
               }
               out+="\\\\\n\\hline";

	    	} else {

	    		ALPHAiHolm[i-1] = 0.05/((double)nAlgorithms-(double)i);
	    		ALPHAiHolland[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/((double)nAlgorithms-(double)i)));
	    		ALPHAiFinner[i-1] = 1.0 - Math.pow((1.0 - 0.05),(1.0/(((double)nAlgorithms-1)/(double)i)));
	    		ordenAlgoritmosQ[i-1] = new String ((String)algorithmName[pos]);
	    		
                out+=(nAlgorithms-i) + "&" + algorithmName[pos] + "&" +
                nf6.format(Math.abs((rankingRef-maxVal)/SE)) + "&" +
               	nf6.format(2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE)));
               	if((Holm)||(Hoch)||(Hommel)){
                 	out+="&" + nf6.format(ALPHAiHolm[i-1]);
                }
                if(Holland){
                	out+="&" + nf6.format(ALPHAiHolland[i-1]);
                }
                if(Rom){
                 	out+="&" + nf6.format(ALPHAiRom[i-1]);
                }
                if(Finner){
                 	out+="&" + nf6.format(ALPHAiFinner[i-1]);
                 }
                if(Li){
                    out+="&" + ((i==(nAlgorithms-1))?(0.05):(nf6.format(ALPHA2Li)));
                }
                out+="\\\\";
           		PiQ[i-1] = 2*CDF_Normal.normp((-1)*Math.abs((rankingRef-maxVal)/SE));
	    	}

	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Post Hoc comparison Table for $\\alpha=0.05$ (Quade)}\n" + "\\end{table}";
        
	    
	    /*Compute the rejected hypotheses for each test*/
	    
	    if(Bonferroni){
	    	out+="Bonferroni-Dunn's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)(nAlgorithms-1))+"$.\n\n";
	    }
	    
	    if(Holm){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (PiQ[i] > ALPHAiHolm[i]) {	    		
		    		out+="Holm's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }

	    if(Hoch){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (PiQ[i] <= ALPHAiHolm[i]) {	    		
	    			out+="Hochberg's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolm[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Hommel){
	    	otro = true;
	    	for (j=nAlgorithms-1; j>0 && otro; j--) {
	    		otro = false;
	    		for (k=1; k<=j && !otro; k++) {
	    			if (PiQ[nAlgorithms-1-j+k-1] <= 0.05*(double)k/(double)j) {
	    				otro = true;
	    			}
	    		}
	    	}
	    	if (otro == true) {
	    		out+="Hommel's procedure rejects all hypotheses.\n\n";
	    	} else {
	    		j++;
	    		out+="Hommel's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(0.05/(double)j)+"$.\n\n";
	    	}
	    }
	    
	    if(Holland){
	    	parar = false;
	    
	    	for (i=0; i<nAlgorithms-1 && !parar; i++) {
	    		if (PiQ[i] > ALPHAiHolland[i]) {	    		
	    			out+="Holland's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiHolland[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Rom){
	    	parar = false;
	    	for (i=nAlgorithms-2; i>=0 && !parar; i--) {
	    		if (PiQ[i] <= ALPHAiRom[i]) {	    		
	    			out+="Rom's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiRom[i])+"$.\n\n";
	    			parar = true;
	    		}
	    	}
	    }

	    if(Finner){
		    parar = false;
		    for (i=0; i<nAlgorithms-1 && !parar; i++) {
		    	if (PiQ[i] > ALPHAiFinner[i]) {	    		
		    		out+="Finner's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHAiFinner[i])+"$.\n\n";
		    		parar = true;
		    	}
		    }
	    }
	    if(Li){
		    if (PiQ[nAlgorithms-2] < 0.05) {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+0.05+"$.\n\n";
		    } else {
		    	out+="Li's procedure rejects those hypotheses that have a p-value $\\le"+nf6.format(ALPHA2Li)+"$.\n\n";	    	
		    }
	    }


	    out+="\n\\newpage\n";

	}    
	    
	/************ ADJUSTED P-VALUES IN 1xN **************************************************************************/
	    
	    
	/** FRIEDMAN *****************************************************************************************************/

	if(Friedman){
		
	
	    out+="\n\\section{Adjusted P-Values (Friedman)}\n\n";
	    
	    out+="\nAdjusted P-values obtained through the application of the post hoc methods (Friedman).\n\n";
	    
	    adjustedP = new double[nAlgorithms-1][8];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][0] = Pi[i] * (double)(nAlgorithms-1);
	    	adjustedP[i][1] = Pi[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][2] = Pi[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][4] = 1.0 - Math.pow((1.0 - Pi[i]),((double)(nAlgorithms-1))-i);
	    	adjustedP[i][5] = Pi[i] * adjustedRom[i];
	    	adjustedP[i][6] = 1.0 - Math.pow((1.0 - Pi[i]),((double)(nAlgorithms-1))/(i+1));
	    	adjustedP[i][7] = Pi[i] / (Pi[i] + 1 - Pm);
	    }
	    
	    for (i=1; i<adjustedP.length; i++) {
	    	if (adjustedP[i][1] < adjustedP[i-1][1])
	    		adjustedP[i][1] = adjustedP[i-1][1];
	    	if (adjustedP[i][4] < adjustedP[i-1][4])
	    		adjustedP[i][4] = adjustedP[i-1][4];
	    	if (adjustedP[i][6] < adjustedP[i-1][6])
	    		adjustedP[i][6] = adjustedP[i-1][6];
	    }
	    for (i=adjustedP.length-2; i>=0; i--) {
	    	if (adjustedP[i][2] > adjustedP[i+1][2])
	    		adjustedP[i][2] = adjustedP[i+1][2];
	    	if (adjustedP[i][5] > adjustedP[i+1][5])
	    		adjustedP[i][5] = adjustedP[i+1][5];
	    }
	    
	    /*Algoritmo que calcula los valores p ajustados para Hommel*/
	    Ci= new double[adjustedP.length+1];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][3] = Pi[i];
	    }
	    for (m=adjustedP.length; m>1; m--) {	    	
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    			Ci[i] = ((double)m*Pi[i-1])/((double)(m+i-adjustedP.length));
	    	}
	    	min = Double.POSITIVE_INFINITY;
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (Ci[i] < min)
	    			min = Ci[i];
	    	}
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (adjustedP[i-1][3] < min)
	    			adjustedP[i-1][3] = min;
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		Ci[i] = Math.min(min, (double)m * Pi[i-1]);
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		if (adjustedP[i-1][3] < Ci[i])
	    			adjustedP[i-1][3] = Ci[i];
	    	}
	    }
       
       	counter=3;
		
       	if(Bonferroni){
			counter++;
		}
		if(Holm){
			counter++;
		}
		if(Hoch){
			counter++;
		}
		if(Hommel){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Bonferroni){
			out+="&$p_{Bonf}$";
		}
		if(Holm){
			out+="&$p_{Holm}$";
		}
		if(Hoch){
			out+="&$p_{Hochberg}$";
		}
		if(Hommel){
			out+="&$p_{Hommel}$";
		}   		          		
        out+="\\\\\n\\hline"; 
        
	    for (i=0; i<Pi.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosF[i] + "&" + nf6.format(Pi[i]);
	    	if(Bonferroni){
	    		out+="&" + nf6.format(adjustedP[i][0]);
			}
			if(Holm){
				out+="&" + nf6.format(adjustedP[i][1]);
			}
			if(Hoch){
				out+="&" + nf6.format(adjustedP[i][2]);
			}
			if(Hommel){
				out+="&" + nf6.format(adjustedP[i][3]);
			}
			out+="\\\\";
            		
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (FRIEDMAN) (I)}\n" + "\\end{table}\n";

	    counter=3;
		
		if(Holland){
			counter++;
		}
		if(Rom){
			counter++;
		}
		if(Finner){
			counter++;
		}
		if(Li){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Holland){
			out+="&$p_{Holland}$";
		}
		if(Rom){
			out+="&$p_{Rom}$";
		}
		if(Finner){
			out+="&$p_{Finner}$";
		}
		if(Li){
			out+="&$p_{Li}$";
		}   		          		
        out+="\\\\\n\\hline";
	    for (i=0; i<Pi.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosF[i] + "&" + nf6.format(Pi[i]);
	    	if(Holland){
	    		out+="&" + nf6.format(adjustedP[i][4]);
			}
			if(Rom){
				out+="&" + nf6.format(adjustedP[i][5]);
			}
			if(Finner){
				out+="&" + nf6.format(adjustedP[i][6]);
			}
			if(Li){
				out+="&" + nf6.format(adjustedP[i][7]);
			}
			out+="\\\\";
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (FRIEDMAN) (II)}\n" + "\\end{table}\n";


	    out+="\n\\newpage\n";
	    
	}
	    /** ALIGNED FRIEDMAN *****************************************************************************************************/

	if(Alligned){
		
	
	    out+="\n\\section{Adjusted P-Values (Friedman Alligned)}\n\n";
	    
	    out+="\nAdjusted P-values obtained through the application of the post hoc methods (Friedman Alligned).\n\n";
	    
	    adjustedP = new double[nAlgorithms-1][8];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][0] = PiAR[i] * (double)(nAlgorithms-1);
	    	adjustedP[i][1] = PiAR[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][2] = PiAR[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][4] = 1.0 - Math.pow((1.0 - PiAR[i]),((double)(nAlgorithms-1))-i);
	    	adjustedP[i][5] = PiAR[i] * adjustedRom[i];
	    	adjustedP[i][6] = 1.0 - Math.pow((1.0 - PiAR[i]),((double)(nAlgorithms-1))/(i+1));
	    	adjustedP[i][7] = PiAR[i] / (PiAR[i] + 1 - PmAR);
	    }
	    
	    for (i=1; i<adjustedP.length; i++) {
	    	if (adjustedP[i][1] < adjustedP[i-1][1])
	    		adjustedP[i][1] = adjustedP[i-1][1];
	    	if (adjustedP[i][4] < adjustedP[i-1][4])
	    		adjustedP[i][4] = adjustedP[i-1][4];
	    	if (adjustedP[i][6] < adjustedP[i-1][6])
	    		adjustedP[i][6] = adjustedP[i-1][6];
	    }
	    for (i=adjustedP.length-2; i>=0; i--) {
	    	if (adjustedP[i][2] > adjustedP[i+1][2])
	    		adjustedP[i][2] = adjustedP[i+1][2];
	    	if (adjustedP[i][5] > adjustedP[i+1][5])
	    		adjustedP[i][5] = adjustedP[i+1][5];
	    }
	    
	    /*Algoritmo que calcula los valores p ajustados para Hommel*/
	    Ci= new double[adjustedP.length+1];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][3] = PiAR[i];
	    }
	    for (m=adjustedP.length; m>1; m--) {	    	
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    			Ci[i] = ((double)m*PiAR[i-1])/((double)(m+i-adjustedP.length));
	    	}
	    	min = Double.POSITIVE_INFINITY;
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (Ci[i] < min)
	    			min = Ci[i];
	    	}
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (adjustedP[i-1][3] < min)
	    			adjustedP[i-1][3] = min;
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		Ci[i] = Math.min(min, (double)m * PiAR[i-1]);
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		if (adjustedP[i-1][3] < Ci[i])
	    			adjustedP[i-1][3] = Ci[i];
	    	}
	    }
	    
	    counter=3;
		
       	if(Bonferroni){
			counter++;
		}
		if(Holm){
			counter++;
		}
		if(Hoch){
			counter++;
		}
		if(Hommel){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Bonferroni){
			out+="&$p_{Bonf}$";
		}
		if(Holm){
			out+="&$p_{Holm}$";
		}
		if(Hoch){
			out+="&$p_{Hochberg}$";
		}
		if(Hommel){
			out+="&$p_{Hommel}$";
		}   		          		
        out+="\\\\\n\\hline"; 
        
	    for (i=0; i<PiAR.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosAF[i] + "&" + nf6.format(PiAR[i]);
	    	if(Bonferroni){
	    		out+="&" + nf6.format(adjustedP[i][0]);
			}
			if(Holm){
				out+="&" + nf6.format(adjustedP[i][1]);
			}
			if(Hoch){
				out+="&" + nf6.format(adjustedP[i][2]);
			}
			if(Hommel){
				out+="&" + nf6.format(adjustedP[i][3]);
			}
			out+="\\\\";
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (ALIGNED FRIEDMAN) (I)}\n" + "\\end{table}\n";

	    counter=3;
		
		if(Holland){
			counter++;
		}
		if(Rom){
			counter++;
		}
		if(Finner){
			counter++;
		}
		if(Li){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Holland){
			out+="&$p_{Holland}$";
		}
		if(Rom){
			out+="&$p_{Rom}$";
		}
		if(Finner){
			out+="&$p_{Finner}$";
		}
		if(Li){
			out+="&$p_{Li}$";
		}   		          		
        out+="\\\\\n\\hline";
	    for (i=0; i<PiAR.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosAF[i] + "&" + nf6.format(PiAR[i]);
	    	if(Holland){
	    		out+="&" + nf6.format(adjustedP[i][4]);
			}
			if(Rom){
				out+="&" + nf6.format(adjustedP[i][5]);
			}
			if(Finner){
				out+="&" + nf6.format(adjustedP[i][6]);
			}
			if(Li){
				out+="&" + nf6.format(adjustedP[i][7]);
			}
			out+="\\\\";
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (ALIGNED FRIEDMAN) (II)}\n" + "\\end{table}\n";

	    out+="\n\\newpage\n";
	}  
	    /** QUADE *****************************************************************************************************/

	if(Quade){
		
	
	    out+="\n\\section{Adjusted P-Values (Quade)}\n\n";
	    
	    out+="\nAdjusted P-values obtained through the application of the post hoc methods (Quade).\n\n";
	    
	    adjustedP = new double[nAlgorithms-1][8];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][0] = PiQ[i] * (double)(nAlgorithms-1);
	    	adjustedP[i][1] = PiQ[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][2] = PiQ[i] * (((double)(nAlgorithms-1))-i);
	    	adjustedP[i][4] = 1.0 - Math.pow((1.0 - PiQ[i]),((double)(nAlgorithms-1))-i);
	    	adjustedP[i][5] = PiQ[i] * adjustedRom[i];
	    	adjustedP[i][6] = 1.0 - Math.pow((1.0 - PiQ[i]),((double)(nAlgorithms-1))/(i+1));
	    	adjustedP[i][7] = PiQ[i] / (PiQ[i] + 1 - PmQ);
	    }
	    
	    for (i=1; i<adjustedP.length; i++) {
	    	if (adjustedP[i][1] < adjustedP[i-1][1])
	    		adjustedP[i][1] = adjustedP[i-1][1];
	    	if (adjustedP[i][4] < adjustedP[i-1][4])
	    		adjustedP[i][4] = adjustedP[i-1][4];
	    	if (adjustedP[i][6] < adjustedP[i-1][6])
	    		adjustedP[i][6] = adjustedP[i-1][6];
	    }
	    for (i=adjustedP.length-2; i>=0; i--) {
	    	if (adjustedP[i][2] > adjustedP[i+1][2])
	    		adjustedP[i][2] = adjustedP[i+1][2];
	    	if (adjustedP[i][5] > adjustedP[i+1][5])
	    		adjustedP[i][5] = adjustedP[i+1][5];
	    }
	    
	    /*Algoritmo que calcula los valores p ajustados para Hommel*/
	    Ci= new double[adjustedP.length+1];
	    for (i=0; i<adjustedP.length; i++) {
	    	adjustedP[i][3] = PiQ[i];
	    }
	    for (m=adjustedP.length; m>1; m--) {	    	
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    			Ci[i] = ((double)m*PiQ[i-1])/((double)(m+i-adjustedP.length));
	    	}
	    	min = Double.POSITIVE_INFINITY;
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (Ci[i] < min)
	    			min = Ci[i];
	    	}
	    	for (i=adjustedP.length; i> (adjustedP.length-m); i--) {
	    		if (adjustedP[i-1][3] < min)
	    			adjustedP[i-1][3] = min;
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		Ci[i] = Math.min(min, (double)m * PiQ[i-1]);
	    	}
	    	for (i=1; i<=(adjustedP.length-m); i++) {
	    		if (adjustedP[i-1][3] < Ci[i])
	    			adjustedP[i-1][3] = Ci[i];
	    	}
	    }
	    
	    counter=3;
		
       	if(Bonferroni){
			counter++;
		}
		if(Holm){
			counter++;
		}
		if(Hoch){
			counter++;
		}
		if(Hommel){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Bonferroni){
			out+="&$p_{Bonf}$";
		}
		if(Holm){
			out+="&$p_{Holm}$";
		}
		if(Hoch){
			out+="&$p_{Hochberg}$";
		}
		if(Hommel){
			out+="&$p_{Hommel}$";
		}   		          		
        out+="\\\\\n\\hline"; 
        
	    for (i=0; i<PiQ.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosQ[i] + "&" + nf6.format(PiQ[i]);
	    	if(Bonferroni){
	    		out+="&" + nf6.format(adjustedP[i][0]);
			}
			if(Holm){
				out+="&" + nf6.format(adjustedP[i][1]);
			}
			if(Hoch){
				out+="&" + nf6.format(adjustedP[i][2]);
			}
			if(Hommel){
				out+="&" + nf6.format(adjustedP[i][3]);
			}
			out+="\\\\";
	    }
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (QUADE) (I)}\n" + "\\end{table}\n";

	    counter=3;
		
		if(Holland){
			counter++;
		}
		if(Rom){
			counter++;
		}
		if(Finner){
			counter++;
		}
		if(Li){
			counter++;
		}
	    out+="\\begin{table}[!htp]\n\\centering\\small\n" +
           		"\\begin{tabular}{"+printC(counter)+"}\n" +
           		"i&algorithm&unadjusted $p$";
	    if(Holland){
			out+="&$p_{Holland}$";
		}
		if(Rom){
			out+="&$p_{Rom}$";
		}
		if(Finner){
			out+="&$p_{Finner}$";
		}
		if(Li){
			out+="&$p_{Li}$";
		}   		          		
        out+="\\\\\n\\hline";
	    
	    for (i=0; i<PiQ.length; i++) {	    	
	    	out+=(i+1) + "&" + ordenAlgoritmosQ[i] + "&" + nf6.format(PiQ[i]);
	    	if(Holland){
	    		out+="&" + nf6.format(adjustedP[i][4]);
			}
			if(Rom){
				out+="&" + nf6.format(adjustedP[i][5]);
			}
			if(Finner){
				out+="&" + nf6.format(adjustedP[i][6]);
			}
			if(Li){
				out+="&" + nf6.format(adjustedP[i][7]);
			}
			out+="\\\\";
	    }
	    
	    out+="\\hline\n" + "\\end{tabular}\n\\caption{Adjusted $p$-values (QUADE) (II)}\n" + "\\end{table}\n";
	}
	
	out+="\\end{landscape}\\end{document}";  
    	
    return out;
    	
	}//end-method
    
	/**
	* Computes ROM adjusted p-values
	*
	* @param alpha Alpha value
	* @param vector Array with the unadjusted p-values
	* @param adjusted Array to store the p-values
	*
	*/
    public static void calcularROM(double alpha, double vector[], double adjusted[]) {
		
		int i, j;
		int m;
		double suma1, suma2;
		
		m = vector.length;
		
		vector[m-1] = alpha;
		vector[m-2] = alpha/2.0;
		adjusted[m-1] = 1;
		adjusted[m-2] = 2;
		
		for (i=3; i<=m; i++) {
			suma1 = suma2 = 0;
			for (j=1;j<(i-1);j++) {
				suma1 += Math.pow(alpha, (double)j);
			}
			for (j=1;j<(i-2);j++){
				suma2 += combinatoria(j,i)*Math.pow(vector[m-j-1], (double)(i-j));
			}
			vector[m-i] = (suma1-suma2)/(double)i;
			adjusted[m-i] = vector[m-1] / vector[m-i];
		}		
		
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
	* Computes the Chi Square distribution
	*
	* @param x X value
	* @param n N value
	*
	* @return The Chi Square value
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
    }//end-method

	/**
	* Computes the Fisher distribution
	* @param f  F value
	* @param n1 N1 value
	* @param n2 N2 value
	*
	* @return The Fisher value
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
    }//end-method
    
	/**
	* Computes the statCom distribution
	* @param q  Q value
	* @param i  I value
	* @param j  J value
	* @param b  B value
	*
	* @return The statCom value
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
    }//end-method
	
	/**
	* Prints as many "c" as desired
	*
	* @param n Number of "c" to print
	*
	* @return A string with all the "c"s
	*/
	public String printC(int n){
		
		String out="";
		
		for(int i=0;i<n;i++){
			out+="c";
		}
		
		return out;
	}//end-method
    
	/**
	* <p>
	* This method decodes the parameter and assigns the attributes that specify  the p-value adjustment to use
	* </p>
	* @param cod A double with the code
	* @return Nothing, the values of Iman, Nemenyi, Bonferroni, Holm, Hoch and Hommel are changed
	*/
    private void decode(double cod) {
    	
        int code = (int) cod; //cast a entero
        Iman = (code % 2 > 0);
        code /= 2;
        Nemenyi = (code % 2 > 0);
        code /= 2;
        Bonferroni = (code % 2 > 0);
        code /= 2;
        Holm = (code % 2 > 0);
        code /= 2;
        Hoch = (code % 2 > 0);
        code /= 2;
        Hommel = (code % 2 > 0);
        code /= 2;
        Scha = (code % 2 > 0);
        code /= 2;
        Berg = (code % 2 > 0);
        code /= 2;
        Holland = (code % 2 > 0);
        code /= 2;
        Rom = (code % 2 > 0);
        code /= 2;
        Finner = (code % 2 > 0);
        code /= 2;
        Li = (code % 2 > 0);
    }//end-method
    
	/**
	* <p>
	* This method decodes composes the header of the LaTeX file where the results are saved
	* </p>
	* @return A string with the header of the LaTeX file
	*/    
    private String header() {
    	
        String output = new String("");
        output += "\\documentclass[a4paper,10pt]{article}\n";
        output += "\\usepackage{graphicx}\n";
        output += "\\usepackage{lscape}\n";
        output += "\\title{Output tables for 1xN statistical comparisons.}\n";
        output += "\\author{}\n\\date{\\today}\n\\begin{document}\n";
        output += "\\begin{landscape}\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n";
        
        return output;

    }//end-method

}//end-class
