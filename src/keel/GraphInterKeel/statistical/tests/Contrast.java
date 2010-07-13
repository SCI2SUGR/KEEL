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
 * File: Contrast.java
 * 
 * This class obtains the contrast estimation from several methods
 * 
 * @author Written by Joaquï¿½n Derrac (University of Granada) 29/04/2010
 * @version 1.1 
 * @since JDK1.5
*/
package keel.GraphInterKeel.statistical.tests;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import keel.GraphInterKeel.statistical.Configuration;
import org.core.*;

public class Contrast {
	
	/**
	* Builder
	*/
	public Contrast(){
		
	}//end-method
	
	/**
     * <p>
     * In this method, all possible post hoc statistical test between more than three algorithms results 
     * are executed, according to the configuration file
     * @param data Array with the results of the method
     * @param algorithms A vector of String with the names of the algorithms
     * </p>
     */
	public static void doContrast(double data[][], String algorithms[]) {
		
		String outputFileName = Configuration.getPath();

        String outputString = new String("");
	    outputString = header();
	        
	    outputString += computeBody(data, algorithms);

	    Files.writeFile(outputFileName, outputString);

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
	        output += "\\title{Contrast estimation.}\n";
	        output += "\\date{\\today}\n\\author{}\n\\begin{document}\n\\begin{landscape}\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\\section{Results.}\n\n";

	        output += "Estimation of the contrast between medians of samples of results considering all pairwise comparisons:\n\n";
	        		
	        return output;

	}//end-method
	 
	/**
     * <p>
     * In this method, the contrast estimation is computed
	 *
	 * @param results Array with the results of the methods
	 * @param algorithmName Array with the name of the methods employed
	 *
	 * @return A string with the contents of the test in LaTeX format
     */
	private static String computeBody(double[][] results, String algorithmName[]) {
		
		String output="";
		double CE [][][];
		double medians [][];
		double estimators [];
		
		DecimalFormat nf = (DecimalFormat) DecimalFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(0);

		DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		nf.setDecimalFormatSymbols(dfs);

		int numAlg= algorithmName.length;
		int nDatasets = results[0].length;
		
		/** CONTRAST ESTIMATION *******************************************************************************************/
	    CE = new double[numAlg][numAlg][nDatasets];
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=i+1; j<numAlg; j++) {
	    		for (int k=0; k<nDatasets; k++) {
	    			CE[i][j][k] = results[i][k] - results[j][k];
	    		}
	    	}
	    }

	    medians = new double[numAlg][numAlg];
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=i+1; j<numAlg; j++) {
	    		Arrays.sort(CE[i][j]);
	    		if (CE[i][j].length % 2 == 1) {
	    			medians[i][j] = CE[i][j][nDatasets/2];
	    		} else {
	    			medians[i][j] = (CE[i][j][nDatasets/2] + CE[i][j][(nDatasets/2)-1]) / 2.0;	    			
	    		}
	    	}
	    }
	    
	    estimators = new double[numAlg];
	    Arrays.fill(estimators, 0);
	    for (int i=0; i<numAlg; i++) {
	    	for (int j=0; j<numAlg; j++) {
		    		estimators[i] += medians[i][j] - medians[j][i];
	    	}
	    	estimators[i] /= numAlg;
	    }
		
		
		
	    /** PRINT THE CONTRAST ESTIMATION*/
		
	    output +="\\begin{table}[!htp]\n\\centering\\scriptsize\n" + "\\begin{tabular}{\n";
        for (int i=0; i<numAlg+1; i++) {
        	output +="|r";
        }
        output +="|}\n\\hline\n" + " \n";
        for (int i=0; i<numAlg; i++) {
        	output +="&" + algorithmName[i];
        }        
        output +="\\\\\n\\hline\n";
        for (int i=0; i<numAlg; i++) {
        	output +=algorithmName[i];
        	for (int j=0; j<numAlg; j++) {
        		 output +="& "+nf.format(estimators[i] - estimators[j]);
        	}
        	output +="\\\\\n\\hline\n";
        }

        output +="\n" + "\\end{tabular}\n" + "\\caption{Contrast Estimation}\n\\end{table}\n";

		output += "\n\\end{landscape}\n\\end{document}";
        return output;
		
    }//end-method

}//end-class
