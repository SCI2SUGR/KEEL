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
 * File: Wilcoxon.java.
 *
 * Wilcoxon signed ranks test
 *
 * @author Written by Joaquin Derrac (University of Granada) 1/12/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.statistical.tests;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import keel.GraphInterKeel.statistical.Configuration;
import org.core.*;

public class Wilcoxon {

    private static DecimalFormat nf;
    private static double wilcoxonRanks[][];
    private static double exactPValues[][];
    private static double asymptoticPValues[][];
    private static String confidenceIntervals95[][];
    private static String confidenceIntervals90[][];
    private static double exactConfidence90[][];
    private static double exactConfidence95[][];
    private static int wins90[];
    private static int wins95[];
    private static int draw90[];
    private static int draw95[];
    private static double data[][];
    private static int columns;
    private static int rows;
    private static String algorithms[];
    private static String outputFileName;
    private static String outputSummaryFileName;

    /**
     * Builder
     */
    public Wilcoxon() {
    }//end-method

    /**
     * In this method, all possible pairwise Wilcoxon comparisons are performed
     *
     * @param newData Array with the results of the method
     * @param newAlgorithms A vector of String with the names of the algorithms
     */
    public static void doWilcoxon(double newData[][], String newAlgorithms[]) {

        outputFileName = Configuration.getPath();

        String outputString = "";
        outputString = header();

        data = new double[newData[0].length][newData.length];
        algorithms = new String[newAlgorithms.length];
        columns = data[0].length;
        rows = data.length;

        //reverse data matrix
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                data[i][j] = newData[j][i];
            }
        }
        System.arraycopy(newAlgorithms, 0, algorithms, 0, newAlgorithms.length);

        wilcoxonRanks = new double[columns][columns];
        exactPValues = new double[columns][columns];
        asymptoticPValues = new double[columns][columns];
        confidenceIntervals95 = new String[columns][columns];
        confidenceIntervals90 = new String[columns][columns];
        exactConfidence90 = new double[columns][columns];
        exactConfidence95 = new double[columns][columns];

        wins90 = new int[columns];
        wins95 = new int[columns];

        draw90 = new int[columns];
        draw95 = new int[columns];

        Arrays.fill(wins90,0);
        Arrays.fill(wins95,0);

        Arrays.fill(draw90,0);
        Arrays.fill(draw95,0);

        nf = (DecimalFormat) DecimalFormat.getInstance();
        nf.setMaximumFractionDigits(6);
        nf.setMinimumFractionDigits(0);

        DecimalFormatSymbols dfs = nf.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        nf.setDecimalFormatSymbols(dfs);

        Files.writeFile(outputFileName, outputString);

        computeBody();

        outputString = footer();

        Files.addToFile(outputFileName, outputString);

        //write summary file
        outputSummaryFileName = outputFileName.substring(0, outputFileName.length() - 4) + "_Summary.tex";

        outputString = headerSummary();

        Files.addToFile(outputSummaryFileName, outputString);

        computeSummary();

        outputString = footer();

        Files.addToFile(outputSummaryFileName, outputString);

    }//end-method

    /**
     * Computes body of the summary file
     *
     */
    public static void computeSummary() {

        String text = "\n";

        //print the rank matrix

        text += "\\begin{sidewaystable}[!htp]\n\\centering\\scriptsize\n"
                + "\\resizebox{\\textwidth}{!}{\\begin{tabular}{\n";
        text += "|c";
        for (int i = 0; i < columns; i++) {
            text += "|r";
        }
        text += "|}\n\\hline\n";

        for (int i = 0; i < columns; i++) {
            text += "&(" + (i + 1) + ") ";
        }
        text += "\\\\\n\\hline\n";
        for (int i = 0; i < columns; i++) {
            text += algorithms[i] + " (" + (i + 1) + ")";
            for (int j = 0; j < columns; j++) {
                if (i != j) {
                    text += "& " + wilcoxonRanks[i][j];
                } else {
                    text += "& -";
                }
            }
            text += "\\\\\n\\hline\n";
        }

        text += "\n" + "\\end{tabular}}\n" + "\\caption{Ranks computed by the Wilcoxon test}\n";
        text += "\n\\end{sidewaystable}\n";
        text += "\n \\clearpage \n\n";

        Files.addToFile(outputSummaryFileName, text);

        //print the p-value matrix

        text = "\n";

        text += "\\begin{sidewaystable}[!htp]\n\\centering\\scriptsize\n"
                + "\\resizebox{\\textwidth}{!}{\\begin{tabular}{\n";
        text += "|c";
        for (int i = 0; i < columns; i++) {
            text += "|c";
        }
        text += "|}\n\\hline\n";

        for (int i = 0; i < columns; i++) {
            text += "&(" + (i + 1) + ") ";
        }
        text += "\\\\\n\\hline\n";

        if (rows <= 50) {
            for (int i = 0; i < columns; i++) {
                text += algorithms[i] + " (" + (i + 1) + ")";
                for (int j = 0; j < columns; j++) {
                   
                    if (i < j) {//0.1
                        text += "& " + getSymbol(i,j,exactPValues[i][j], exactPValues[j][i], 0.1) + " ";
                    }
                    if (i == j) {
                        text += "& -";
                    }
                    if (i > j) {//0.05
                        text += "& " + getSymbol(i,j,exactPValues[i][j], exactPValues[j][i], 0.05) + " ";
                    }
                }
                
                text += "\\\\\n\\hline\n";
            }
        } else {
            for (int i = 0; i < columns; i++) {
                text += algorithms[i] + " (" + (i + 1) + ")";
                for (int j = 0; j < columns; j++) {
                    if (i < j) {//0.1
                        text += "& " + getSymbol(i,j,asymptoticPValues[i][j], asymptoticPValues[j][i], 0.1) + " ";
                    }
                    if (i == j) {
                        text += "& -";
                    }
                    if (i > j) {//0.05
                        text += "& " + getSymbol(i,j,asymptoticPValues[i][j], asymptoticPValues[j][i], 0.05) + " ";
                    }
                }
                text += "\\\\\n\\hline\n";
            }
        }

        text += "\n" + "\\end{tabular}}\n" + "\\caption{Summary of the Wilcoxon test. \\textbullet = "
                + "the method in the row improves the method of the column. \\textopenbullet = "
                + "the method in the column improves the method of the row. Upper diagonal of level significance $\\alpha=0.9$,"
                + "Lower diagonal level of significance $\\alpha=0.95$}\n";
        text += "\n\\end{sidewaystable}\n";
        text += "\n \\clearpage \n\n";

        Files.addToFile(outputSummaryFileName, text);

        text = "\n";

        //print the summary table

        text += "\\begin{table}[!htp]\n\\centering\\scriptsize\n"
                + "\\begin{tabular}{\n";
        text += "|c|c|c|c|c|}\n\\hline\n";
        text += "&\\multicolumn{2}{c|}{$\\alpha=0.9$} & \\multicolumn{2}{c|}{$\\alpha=0.95$}\\\\\\hline\n";
        text += "Method & + & $\\pm$ & + & $\\pm$ ";

        text += "\\\\\n\\hline\n";
        for (int i = 0; i < columns; i++) {
            text += algorithms[i]+" & "+wins90[i]+" & "+draw90[i]+" & "+wins95[i]+" & "+draw95[i];
            text += "\\\\\n\\hline\n";
        }

        text += "\n" + "\\end{tabular}\n" + "\\caption{Wilcoxon test summary results}\n";
        text += "\n\\end{table}\n";
        text += "\n \\clearpage \n\n";

        Files.addToFile(outputSummaryFileName, text);

    }//end-method

    /**
     * Computes a symbol for the symbol table
     *
     * @param indexA Index of first element
     * @param indexB Index of second element
     * @param pA First p-value
     * @param pB Second p-value
     * @param threshold minimun p-value
     * @return
     */
    private static String getSymbol(int indexA,int indexB,double pA, double pB, double threshold){

        if(threshold==0.1){
            if((pA<pB)&&(pA<threshold)){
                wins90[indexA]++;
                draw90[indexA]++;
                return "\\textbullet";
            }

            if((pA>pB)&&(pB<threshold)){
                wins90[indexB]++;
                draw90[indexB]++;
                return "\\textopenbullet";
            }

            draw90[indexA]++;
            draw90[indexB]++;

        }else{
            if((pA<pB)&&(pA<threshold)){
                wins95[indexA]++;
                draw95[indexA]++;
                return "\\textbullet";
            }

            if((pA>pB)&&(pB<threshold)){
                wins95[indexB]++;
                draw95[indexB]++;
                return "\\textopenbullet";
            }

            draw95[indexA]++;
            draw95[indexB]++;

        }

        return "";

    }//end-method

    /**
     * Computes body of the report file (i.e. the test itself)
     *
     */
    public static void computeBody() {

        double value;
        String text;

        for (int first = 0; first < columns; first++) {
            for (int second = 0; second < columns; second++) {
                if (first != second) {
                    computeRanks(first, second);
                }
            }
        }

        text = "\n";

        //print individual comparisons

        for (int first = 0; first < columns; first++) {

            text += "\n\\section{Detailed results for " + algorithms[first] + "}\n\n";
            text += "\n\\subsection{Results}\n\n";
            text += "\\begin{table}[!htp]\n\\centering\\small\n" + "\\begin{tabular}{\n";
            text += "|c|c|c|c|c|";
            text += "}\n\\hline\n";
            text += " VS & $R^{+}$ & $R^{-}$ & Exact P-value & Asymptotic P-value \\\\ \\hline \n";
            for (int second = 0; second < columns; second++) {
                if (first != second) {
                    text += algorithms[second] + " & " + wilcoxonRanks[first][second] + " & " + wilcoxonRanks[second][first] + " & ";

                    if (rows < 51) {
                        value = exactPValues[first][second];
                        if (value != 1.0) {
                            text += value + " & ";
                        } else {
                            text += "$\\geq$ 0.2 & ";
                        }
                    } else {
                        text += "- & ";
                    }

                    value = asymptoticPValues[first][second];
                    text += nf.format(value);
                    text += "\\\\ \\hline \n";
                }
            }

            text += "\n" + "\\end{tabular}\n" + "\\caption{Results obtained by the Wilcoxon test for algorithm " + algorithms[first] + "}\n\\end{table}\n";

            text += "\n\\subsection{Confidence intervals for Median of diferences}\n\n";

            text += "\\begin{table}[!htp]\n\\centering\\small\n" + "\\begin{tabular}{\n";
            text += "|c|c|c|";
            text += "}\n\\hline\n";
            text += " $\\alpha$=0.90 & Confidence interval & Exact confidence \\\\ \\hline \n";
            for (int second = 0; second < columns; second++) {
                if (first != second) {
                    text += algorithms[second] + " & " + confidenceIntervals90[first][second] + " & " + nf.format(exactConfidence90[first][second]) + "\\\\ \\hline \n";
                }
            }
            text += "\n" + "\\end{tabular}\n" + "\\caption{Confidence intervals for algorithm " + algorithms[first] + " ($\\alpha$=0.90)}\n\\end{table}\n";

            text += "\\begin{table}[!htp]\n\\centering\\small\n" + "\\begin{tabular}{\n";
            text += "|c|c|c|";
            text += "}\n\\hline\n";
            text += " $\\alpha$=0.95 & Confidence interval & Exact confidence \\\\ \\hline \n";
            for (int second = 0; second < columns; second++) {
                if (first != second) {
                    text += algorithms[second] + " & " + confidenceIntervals95[first][second] + " & " + nf.format(exactConfidence95[first][second]) + "\\\\ \\hline \n";
                }
            }
            text += "\n" + "\\end{tabular}\n" + "\\caption{Confidence intervals for algorithm " + algorithms[first] + " ($\\alpha$=0.95)}\n\\end{table}\n";
            text += "\n \\clearpage \n\n";

            Files.addToFile(outputFileName, text);

            text = "";
        }

    }//end-method

    /**
     * Compute ranks and associated p-values for a giver pair of samples
     *
     * @param first First sample
     * @param second Second sample
     */
    public static void computeRanks(int first, int second) {

        double AOld[], A[];
        double BOld[], B[];
        double diffOld[], diff[];
        int ties, N, pointer;
        boolean sign[];
        double ranks[];
        double RA, RB;
        ArrayList<Double> walsh;
        int criticalN;
        String interval;

        AOld = new double[rows];
        BOld = new double[rows];
        diffOld = new double[rows];

        ties = 0;

        for (int i = 0; i < rows; i++) {

            if (Configuration.getObjective() == 1) {
                AOld[i] = data[i][first];
                BOld[i] = data[i][second];
            } else {
                AOld[i] = data[i][second];
                BOld[i] = data[i][first];
            }

            diffOld[i] = Math.abs(AOld[i] - BOld[i]);

            if (diffOld[i] == 0.0) {
                ties++;
            }
        }

        N = rows - ties;

        A = new double[N];
        B = new double[N];
        diff = new double[N];
        sign = new boolean[N];
        ranks = new double[N];

        pointer = 0;

        for (int i = 0; i < rows; i++) {

            if (diffOld[i] != 0.0) {
                A[pointer] = AOld[i];
                B[pointer] = BOld[i];
                diff[pointer] = Math.abs(A[pointer] - B[pointer]);
                if ((A[pointer] - B[pointer]) > 0.0) {
                    sign[pointer] = true;
                } else {
                    sign[pointer] = false;
                }
                pointer++;
            }

        }

        //compute ranks
        double min;
        double points;
        int tied;
        String tiedString = "";

        Arrays.fill(ranks, -1.0);

        for (int rank = 1; rank <= N;) {
            min = Double.MAX_VALUE;
            tied = 1;

            for (int i = 0; i < N; i++) {
                if ((ranks[i] == -1.0) && diff[i] == min) {
                    tied++;
                }
                if ((ranks[i] == -1.0) && diff[i] < min) {
                    min = diff[i];
                    tied = 1;
                }

            }

            //min has the lower unassigned value
            if (tied == 1) {
                points = rank;
            } else {
                tiedString += (tied + "-");
                points = 0.0;
                for (int k = 0; k < tied; k++) {
                    points += (rank + k);
                }
                points /= tied;
            }

            for (int i = 0; i < N; i++) {
                if (diff[i] == min) {
                    ranks[i] = points;
                }
            }

            rank += tied;
        }

        //compute sumOfRanks

        RA = 0.0;
        RB = 0.0;

        for (int i = 0; i < ranks.length; i++) {
            if (sign[i]) {
                RA += ranks[i];
            } else {
                RB += ranks[i];
            }
        }

        //Treatment of 0's
        double increment;
        double sum0;
        if (ties > 1) {
            //discard a tie if there's an odd number of them
            if (ties % 2 == 1) {
                increment = ties - 1.0;
            } else {
                increment = ties;
            }

            //Adition of 0 ranked differences
            sum0 = (((double) increment + 1.0) * (double) increment) / 2.0;
            sum0 /= 2.0;

            RA += sum0;
            RB += sum0;

            //Reescaling of the rest of ranks
            for (int i = 0; i < ranks.length; i++) {
                if (sign[i]) {
                    RA += increment;
                } else {
                    RB += increment;
                }
            }
			
			//Updating N so it correctly contain the ties
            N+=increment;
        }
        
        //save the ranks
        wilcoxonRanks[first][second] = RA;
        wilcoxonRanks[second][first] = RB;

        //compute exact pValue
        exactPValues[first][second] = WilcoxonDistribution.computeExactProbability(N, RB);
        exactPValues[second][first] = WilcoxonDistribution.computeExactProbability(N, RA);

        //compute asymptotic P Value

        int tiesDistribution[];

        tiesDistribution = decode(tiedString);

        asymptoticPValues[first][second] = WilcoxonDistribution.computeAsymptoticProbability(N, RB, tiesDistribution);
        asymptoticPValues[second][first] = WilcoxonDistribution.computeAsymptoticProbability(N, RA, tiesDistribution);

        //compute confidence intervals
        walsh = new ArrayList<Double>();

        double aux, aux2;
        for (int i = 0; i < diffOld.length - 1; i++) {
            if (Configuration.getObjective() == 1) {
                aux = AOld[i] - BOld[i];
            } else {
                aux = BOld[i] - AOld[i];
            }

            walsh.add(aux);
            for (int j = i + 1; j < diffOld.length; j++) {
                if (Configuration.getObjective() == 1) {
                    aux2 = AOld[j] - BOld[j];
                } else {
                    aux2 = BOld[j] - AOld[j];
                }
                walsh.add((aux + aux2) / 2.0);
            }
        }

        Collections.sort(walsh);

        //Find critical levels

        criticalN = findCriticalValue(diffOld.length, 0.05, tiesDistribution);
        criticalN = Math.max(criticalN, 0);

        //Build interval
        interval = "[";
        interval += nf.format(walsh.get(criticalN));
        interval += " , ";
        interval += nf.format(walsh.get(walsh.size() - (criticalN + 1)));
        interval += "]";

        confidenceIntervals95[first][second] = interval;
        exactConfidence95[first][second] = 1.0 - (WilcoxonDistribution.computeExactProbability(diffOld.length, criticalN));

        criticalN = findCriticalValue(diffOld.length, 0.1, tiesDistribution);
        criticalN = Math.max(criticalN, 0);

        //Build interval
        interval = "[";
        interval += nf.format(walsh.get(criticalN));
        interval += " , ";
        interval += nf.format(walsh.get(walsh.size() - (criticalN + 1)));
        interval += "]";

        confidenceIntervals90[first][second] = interval;
        exactConfidence90[first][second] = 1.0 - (WilcoxonDistribution.computeExactProbability(diffOld.length, criticalN));

    }//end-method

    /**
     * Find the first critical value lower than alpha
     *
     * @param N N parameter
     * @param alpha Limit p-value
     *
     * @return Critical value
     */
    private static int findCriticalValue(int N, double alpha, int[] tiesDistribution) {

        double limit = alpha;
        int critical = -1;

        if (N < 51) {
            do {
                critical++;
            } while (WilcoxonDistribution.computeExactProbability(N, critical) < limit);
        } else {
            do {
                critical++;
            } while (WilcoxonDistribution.computeAsymptoticProbability(N, critical, tiesDistribution) < limit);
        }
        critical--;

        return critical;
    }//end-method

    /**
     * Decodes an string of ties
     * @param cad String
     *
     * @return Integer array representation
     */
    private static int[] decode(String cad) {

        int result[];
        String array[];

        if (cad.equals("")) {
            result = new int[0];
        } else {
            array = cad.split("-");
            result = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                result[i] = Integer.parseInt(array[i]);
            }
        }

        return result;

    }//end-method

    /**
     * Footer of the report
     *
     * @return Contents of the footer
     */
    private static String footer() {
        String output = "";

        output += "\n\\end{document}";

        return output;
    }//end-method

    /**
     *
     * This method composes the header of the LaTeX file where the results are saved
     *
     * @return A string with the header of the LaTeX file
     */
    private static String header() {
        String output = "";
        output += "\\documentclass[a4paper,10pt]{article}\n";
        output += "\\title{Wilcoxon Signed Ranks test.}\n";
        output += "\\date{\\today}\n\\author{KEEL non-parametric statistical module}\n\\begin{document}\n\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\n";

        return output;

    }//end-method

    /**
     *
     * This method composes the header of the summary LaTeX file 
     *
     * @return A string with the header of the summary LaTeX file
     */
    private static String headerSummary() {

        String output = "";
        output += "\\documentclass[a4paper,10pt]{article}\n";
        output += "\\title{Wilcoxon Signed Ranks test.}\n";
        output += "\\usepackage{rotating}\n";
        output += "\\usepackage{textcomp}\n";
        output += "\\date{\\today}\n\\author{KEEL non-parametric statistical module}\n\\begin{document}\n\n\\pagestyle{empty}\n\\maketitle\n\\thispagestyle{empty}\n\n";

        return output;

    }//end-method
}//end-class

