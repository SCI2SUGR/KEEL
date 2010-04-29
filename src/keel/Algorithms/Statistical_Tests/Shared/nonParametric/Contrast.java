/**
* <p>
* @author Written by Joaquín Derrac (University of Granada)26/04/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

import java.text.NumberFormat;
import java.util.*;

import org.core.*;

public class Contrast {
	
	public Contrast(){
		
	}
	
	public void compute(int nfold[], String algorithms[],String fileName) {
		
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
	          int nResults = Integer.parseInt("" + aux[4].charAt(6));
	          nResults++; //The first is indexed by 0

	          if (nResults > 3) {
	            double[][] results = new double[nAlgorithm][nResults];
	            for (int i = 0, j = 0; i < nResults; i++, j += 2) {
	              String outputFile = outputFileName + "result" + i + "s0.stat";
	              StringTokenizer line;
	              String file = Files.readFile(outputFile); //file is an string containing the whole file
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
	                results[k][i] = 1 - Double.parseDouble(res.nextToken()); //guess
	              }
	            }
	            outputString += computeBody(results, algorithms);
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
		
		
		
	}
	
	 private String header() {
	        String output = new String("");
	        output += "\\documentclass[a4paper,10pt]{article}\n";
	        output += "\\usepackage{graphicx}\n";
	        output += "\\usepackage{lscape}\n";
	        output += "\\title{Contrast estimation.}\n";
	        output += "\\date{\\today}\n\\begin{document}\n\\begin{landscape}\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\\section{Results.}\n\n";

	        output += "Estimation of the contrast between medians of samples of results considering all pairwise comparisons:\n\n";
	        		
	        return output;

	}
	 
	private String computeBody(double[][] results, String algorithmName[]) {
		
		String output="";
		double CE [][][];
		double medians [][];
		double estimators [];
		
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(3);

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
	}
	
}