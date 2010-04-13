/**
* <p>
* @author Written by Alberto Fernandez (University of Granada)01/01/2008
* @version 1.0
* @since JDK1.5
* </p>
*/
package keel.Algorithms.Statistical_Tests.Shared.Test_Friedman;

import java.util.*;
import org.core.Fichero;

public class Friedman {
	/**
	* <p>
	* In this class Friedman test is implemented
	* </p>
	*/
    int position;
    double mean[][];
    Pair ordered[][];
    Pair rank[][];
    boolean found;
    int ig;
    double sum;
    boolean visited[];
    Vector toVisit;
    double Rj[];
    double friedman;
    double summation = 0;
    double term1, term2, term3;
    double iman;
    double Qprime010[] = {0.0, 1.645, 1.960, 2.128, 2.242, 2.327, 2.394, 2.450,
                         2.498, 2.540, 2.576, 2.609, 2.639, 2.666, 2.690, 2.713,
                         2.735, 2.755, 2.733, 2.791, 2.807, 2.823, 2.838, 2.852,
                         2.866};
    double Qprime005[] = {0.0, 1.960, 2.242, 2.394, 2.498, 2.576, 2.639, 2.690,
                         2.735, 2.773, 2.807, 2.838, 2.866, 2.891, 2.914, 2.936,
                         2.955, 2.974, 2.992, 3.008, 3.024, 3.038, 3.052, 3.066,
                         3.078};
    double Qprime001[] = {0.0, 2.576, 2.807, 2.936, 3.024, 3.091, 3.144, 3.189,
                         3.227, 3.261, 3.291, 3.317, 3.342, 3.364, 3.384, 3.403,
                         3.421, 3.437, 3.453, 3.467, 3.481, 3.494, 3.506, 3.518,
                         3.529};
    double q010, q005, q001, CD010, CD005, CD001;
    boolean seen[];
    int pos, tmp;
    double min;
    double maxVal;
    double rankingRef;
    double Pi[];
    String algorithmOrder[];
    double rankingOrder[];
    int order[];
    double adjustedP[][];
    double Ci[];
    double SE;
    boolean stop, other;
    Vector indexes = new Vector();
    Vector exhaustiveI = new Vector();
    boolean[][] square;
    double minPi, tmpPi, maxAPi, tmpAPi;
    Relation[] couples;

    boolean Iman, Nemenyi, Bonferroni, Holm, Hoch, Hommel;

	/**
	* <p>
	* Default constructor
	* </p>
	*/
    public Friedman() {

    }


    /**
     * <p>
     * In this method, all possible post hoc statistical test between more than three algorithms results 
     * are executed, according to the configuration file
     * @param code A double that identifies which methods will be applied
     * @param nfold A vector of int with fold number by algorithm
     * @param algorithms A vector of String with the names of the algorithms
     * @param fileName A String with the name of the output file
     * </p>
     */
    public void runPostHoc(double code, int nfold[], String algorithms[],
                         String fileName) {

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
                results[k][i] = 1 - Double.parseDouble(res.nextToken()); //guess
              }
            }
            outputString += runFriedman(code, results, algorithms);
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
        Fichero.escribeFichero(outputFileName, outputString);

    }
    /**
     * <p>
     * In this method, Friedman statistical test is done. 
     * @param code A double that identifies the p-value adjustement to be used: Iman, Nemenyi,
     *  Bonferroni, Holm, Hochberg or Hommel
     * @param results A matrix of double with the sample data
     * @param algorithmName A string vector with algorithms names
     * @return A string with the result of the statistical test, in LaTeX format
     * </p>
     */
    private String runFriedman(double code, double[][] results,
                                 String algorithmName[]) {
        String output = new String("");
        int i, j, k, m;
        double ALPHAiHolm[];
        double ALPHAiHolland[];
        double ALPHAiRom[];
        double ALPHAiShaffer[];

        decode(code); //obtain the code of the p-value adjustment to apply
        int nDatasets = results[0].length;
        int nAlgorithms = results.length;

        //a Pair is sused tos sort the data and ranking computing
        ordered = new Pair[nDatasets][nAlgorithms]; //Number of datasets, number of algorithms
        for (i = 0; i < nDatasets; i++) {
            for (j = 0; j < nAlgorithms; j++) {
                ordered[i][j] = new Pair(j, results[j][i]);
            }
            Arrays.sort(ordered[i]);
        }

        //ranking table by dataset and algorithm
        rank = new Pair[nDatasets][nAlgorithms];
        position = 0;
        for (i = 0; i < nDatasets; i++) {
            for (j = 0; j < nAlgorithms; j++) {
                found = false;
                for (k = 0; k < nAlgorithms && !found; k++) {
                    if (ordered[i][k].index == j) {
                        found = true;
                        position = k + 1;
                    }
                }
                rank[i][j] = new Pair(position, ordered[i][position - 1].value);
            }
        }

        //if the performance is the same for two algorithms the ranking must eb de same too
        for (i = 0; i < nDatasets; i++) {
            visited = new boolean[nAlgorithms];
            toVisit = new Vector();

            Arrays.fill(visited, false);
            for (j = 0; j < nAlgorithms; j++) {
                toVisit.removeAllElements();
                sum = rank[i][j].index;
                visited[j] = true;
                ig = 1;
                for (k = j + 1; k < nAlgorithms; k++) {
                    if (rank[i][j].value == rank[i][k].value && !visited[k]) {
                        sum += rank[i][k].index;
                        ig++;
                        toVisit.add(new Integer(k));
                        visited[k] = true;
                    }
                }
                sum /= (double) ig;
                rank[i][j].index = sum;
                for (k = 0; k < toVisit.size(); k++) {
                    rank[i][((Integer) toVisit.elementAt(k)).intValue()].
                            index = sum;
                }
            }
        }

        //mean ranking value by algorithm
        Rj = new double[nAlgorithms];
        for (i = 0; i < nAlgorithms; i++) {
            Rj[i] = 0;
            for (j = 0; j < nDatasets; j++) {
                Rj[i] += rank[j][i].index / ((double) nDatasets);
            }
        }

        //The mean ranking value by algorithm is saved
        output += "\\begin{table}[!htp]\n" +
                "\\centering\n" +
                "\\caption{Average Rankings of the algorithms\n}" +
                "\\begin{tabular}{c|c}\n" +
                "Algorithm&Ranking\\\\\n\\hline\n";
        for (i = 0; i < nAlgorithms; i++) {
            output += algorithmName[i] + "&" + Rj[i] +
                    "\\\\\n";
        }
        output += "\\end{tabular}\n\\end{table}";

        //Friedman statistic computation
        term1 = (12 * (double) nDatasets) /
                   ((double) nAlgorithms *
                    ((double) nAlgorithms + 1));
        term2 = (double) nAlgorithms * ((double) nAlgorithms + 1) *
                   ((double) nAlgorithms + 1) / (4.0);
        for (i = 0; i < nAlgorithms; i++) {
            summation += Rj[i] * Rj[i];
        }
        friedman = (summation - term2) * term1;
        output += "\n\nFriedman statistic considering reduction performance (distributed according to chi-square with " +
                (nAlgorithms - 1) + " degrees of freedom: " +
                friedman + ".";
        double pFriedman, pIman;
        pFriedman = this.ChiSq(friedman, nAlgorithms - 1);

        output += "P-value computed by Friedman Test: " + pFriedman +
                ".\\newline\n\n";

        if (this.Iman) {
        	//Iman and Davenport statistic computation
            iman = ((nDatasets - 1) * friedman) /
                   (nDatasets * (nAlgorithms - 1) - friedman);
            output += "Iman and Davenport statistic considering reduction performance (distributed according to F-distribution with " +
                    (nAlgorithms - 1) + " and " +
                    (nAlgorithms - 1) * (nDatasets - 1) +
                    " degrees of freedom: " + iman;
            pIman = this.FishF(iman, nAlgorithms - 1,
                               (nAlgorithms - 1) * (nDatasets - 1));
            output += ". P-value computed by Iman and Daveport Test: " + pIman +
                    ".\\newline\n\n";
        }

        term3 = Math.sqrt((double) nAlgorithms *
                             ((double) nAlgorithms + 1) /
                             (6.0 * (double) nDatasets));

        q010 = Qprime010[nAlgorithms - 1];
        q005 = Qprime005[nAlgorithms - 1];
        q001 = Qprime001[nAlgorithms - 1];

        CD010 = q010 * term3;
        CD005 = q005 * term3;
        CD001 = q001 * term3;

        if (this.Bonferroni) {
            output +=
                    "Critical Difference (CD) value for Bonferroni-Dunn test considering $p=0.10$: " +
                    CD010 + ".\n\n";
            output +=
                    "Critical Difference (CD) value for Bonferroni-Dunn test considering $p=0.05$: " +
                    CD005 + ".\n\n";
            output +=
                    "Critical Difference (CD) value for Bonferroni-Dunn test considering $p=0.01$: " +
                    CD001 + ".\n\n";
        }
        
        /************ Comparison against a control **************/
        //Non adjusted Pi value is computed for each comparison alpha=0.05
        Pi = new double[nAlgorithms - 1];
        ALPHAiHolm = new double[nAlgorithms - 1];
        ALPHAiHolland = new double[nAlgorithms - 1];
        algorithmOrder = new String[nAlgorithms - 1];
        SE = term3;
        seen = new boolean[nAlgorithms];
        rankingRef = 0.0;
        Arrays.fill(seen, false);
        for (i = 0; i < nAlgorithms; i++) {
            for (j = 0; seen[j] == true; j++) {
                ;
            }
            pos = j;
            maxVal = Rj[j];
            for (j = j + 1; j < nAlgorithms; j++) {
                if (i > 0) {
                    if (seen[j] == false && Rj[j] > maxVal) {
                        pos = j;
                        maxVal = Rj[j];
                    }
                } else {
                    if (seen[j] == false && Rj[j] < maxVal) {
                        pos = j;
                        maxVal = Rj[j];
                    }
                }
            }
            seen[pos] = true;
            if (i == 0) {
                rankingRef = maxVal;
            } else {
                ALPHAiHolm[i -
                        1] = 0.05 / ((double) nAlgorithms - (double) i);
                ALPHAiHolland[i -
                        1] = 1.0 -
                             Math.pow((1.0 - 0.05),
                                      (1.0 /
                                       ((double) nAlgorithms - (double) i)));
                algorithmOrder[i -
                        1] = new String((String) algorithmName[pos]);
                Pi[i-1] = 2 * CDF_Normal.normp(( -1) * Math.abs((rankingRef - maxVal) / SE));
            }
        }

        //Non adjusted Pi value is computed for each comparison alpha=0.1
        Pi = new double[nAlgorithms - 1];
        ALPHAiHolm = new double[nAlgorithms - 1];
        ALPHAiHolland = new double[nAlgorithms - 1];
        SE = term3;
        seen = new boolean[nAlgorithms];
        rankingRef = 0.0;
        Arrays.fill(seen, false);
        for (i = 0; i < nAlgorithms; i++) {
            for (j = 0; seen[j] == true; j++) {
                ;
            }
            pos = j;
            maxVal = Rj[j];
            for (j = j + 1; j < nAlgorithms; j++) {
                if (i > 0) {
                    if (seen[j] == false && Rj[j] > maxVal) {
                        pos = j;
                        maxVal = Rj[j];
                    }
                } else {
                    if (seen[j] == false && Rj[j] < maxVal) {
                        pos = j;
                        maxVal = Rj[j];
                    }
                }
            }
            seen[pos] = true;
            if (i == 0) {
                rankingRef = maxVal;
            } else {
                ALPHAiHolm[i -
                        1] = 0.1 / ((double) nAlgorithms - (double) i);
                ALPHAiHolland[i -
                        1] = 1.0 -
                             Math.pow((1.0 - 0.1),
                                      (1.0 /
                                       ((double) nAlgorithms - (double) i)));

                Pi[i - 1] = 2 * CDF_Normal.normp(( -1) * Math.abs((rankingRef - maxVal) / SE));
            }
        }

        /************ Adjusted P values against a control **************/

        adjustedP = new double[nAlgorithms - 1][5];
        for (i = 0; i < adjustedP.length; i++) {
            adjustedP[i][0] = Pi[i] * (double) (nAlgorithms - 1);
            adjustedP[i][1] = Pi[i] * (((double) (nAlgorithms - 1)) - i);
            adjustedP[i][2] = Pi[i] * (((double) (nAlgorithms - 1)) - i);
            adjustedP[i][4] = 1.0 -
                              Math.pow((1.0 - Pi[i]),
                                       ((double) (nAlgorithms - 1)) - i);
        }

        for (i = 1; i < adjustedP.length; i++) {
            if (adjustedP[i][1] < adjustedP[i - 1][1]) {
                adjustedP[i][1] = adjustedP[i - 1][1];
            }
            if (adjustedP[i][4] < adjustedP[i - 1][4]) {
                adjustedP[i][4] = adjustedP[i - 1][4];
            }
        }
        for (i = adjustedP.length - 2; i >= 0; i--) {
            if (adjustedP[i][2] > adjustedP[i + 1][2]) {
                adjustedP[i][2] = adjustedP[i + 1][2];
            }
        }
        //Hommel adjustment
        Ci = new double[adjustedP.length + 1];
        for (i = 0; i < adjustedP.length; i++) {
            adjustedP[i][3] = Pi[i];
        }
        for (m = adjustedP.length; m > 1; m--) {
            for (i = adjustedP.length; i > (adjustedP.length - m); i--) {
                Ci[i] = ((double) m * Pi[i - 1]) /
                        ((double) (m + i - adjustedP.length));
            }
            min = Double.POSITIVE_INFINITY;
            for (i = adjustedP.length; i > (adjustedP.length - m); i--) {
                if (Ci[i] < min) {
                    min = Ci[i];
                }
            }
            for (i = adjustedP.length; i > (adjustedP.length - m); i--) {
                if (adjustedP[i - 1][3] < min) {
                    adjustedP[i - 1][3] = min;
                }
            }
            for (i = 1; i <= (adjustedP.length - m); i++) {
                Ci[i] = Math.min(min, (double) m * Pi[i - 1]);
            }
            for (i = 1; i <= (adjustedP.length - m); i++) {
                if (adjustedP[i - 1][3] < Ci[i]) {
                    adjustedP[i - 1][3] = Ci[i];
                }
            }
        }

        output += "\\begin{table}[!htp]\n\\centering\\scriptsize\n\\caption{Adjusted $p$-values}\n" +
                "\\begin{tabular}{ccccccc}\n" +
                "i&algorithm&unadjusted $p$";
        if (this.Bonferroni) {
            output += "&$p_{Bonf}$";
        }
        if (this.Holm) {
            output += "&$p_{Holm}$";
        }
        if (this.Hoch) {
            output += "&$p_{Hoch}$";
        }
        if (this.Hommel) {
            output += "&$p_{Homm}$";
        }
        output += "\\\\\n\\hline";
        for (i = 0; i < Pi.length; i++) {
            output += (i + 1) + "&" + algorithmOrder[i] + "&" + Pi[i];
            if (this.Bonferroni) {
                output += "&" + adjustedP[i][0];
            }
            if (this.Holm) {
                output += "&" + adjustedP[i][1];
            }
            if (this.Hoch) {
                output += "&" + adjustedP[i][2];
            }
            if (this.Hommel) {
                output += "&" + adjustedP[i][3];
            }
            output += "\\\\";

        }

        output += "\\hline\n" + "\\end{tabular}\n" + "\\end{table}\n";

        /************ All vs. All comparison **************/

        //Non adjusted Pi value is computed for each comparison alpha=0.05
        Pi = new double[(int) nCombinations(2, nAlgorithms)];

        ALPHAiHolm = new double[(int) nCombinations(2, nAlgorithms)];

        ALPHAiShaffer = new double[(int) nCombinations(2, nAlgorithms)];

        algorithmOrder = new String[(int) nCombinations(2, nAlgorithms)];

        rankingOrder = new double[(int) nCombinations(2, nAlgorithms)];

        order = new int[(int) nCombinations(2, nAlgorithms)];

        couples = new Relation[(int) nCombinations(2, nAlgorithms)];

        SE = term3;
        seen = new boolean[(int) nCombinations(2, nAlgorithms)];

        for (i = 0, k = 0; i < nAlgorithms; i++) {
            for (j = i + 1; j < nAlgorithms; j++, k++) {
                rankingOrder[k] = Math.abs(Rj[i] - Rj[j]);
                algorithmOrder[k] = algorithmName[i] + " vs. " +
                                     algorithmName[j];
                couples[k] = new Relation(i, j);
            }
        }

        Arrays.fill(seen, false);

        for (i = 0; i < rankingOrder.length; i++) {
            for (j = 0; seen[j] == true; j++) {
                ;
            }
            pos = j;
            maxVal = rankingOrder[j];
            for (j = j + 1; j < rankingOrder.length; j++) {
                if (seen[j] == false && rankingOrder[j] > maxVal) {
                    pos = j;
                    maxVal = rankingOrder[j];
                }
            }
            seen[pos] = true;
            order[i] = pos;
        }

        /*Shaffer and Bergmann tests*/
        pos = 0;
        tmp = nAlgorithms;
        for (i = 0; i < order.length; i++) {
            Pi[i] = 2 *
                    CDF_Normal.normp(( -1) *
                                     Math.abs((rankingOrder[order[i]]) / SE));
            ALPHAiHolm[i] = 0.05 / ((double) order.length - (double) i);
            ALPHAiShaffer[i] = 0.05 /
                               ((double) order.length -
                                (double) Math.max(pos, i));
            if (i == pos && Pi[i] <= ALPHAiShaffer[i]) {
                tmp--;
                pos = (int) nCombinations(2, nAlgorithms) -
                      (int) nCombinations(2, tmp);
            }
        }


        if (nAlgorithms < 9) {
            for (i = 0; i < nAlgorithms; i++) {
                indexes.add(new Integer(i));
            }
            exhaustiveI = obtainExhaustive(indexes);
            square = new boolean[nAlgorithms][nAlgorithms];
            for (i = 0; i < nAlgorithms; i++) {
                Arrays.fill(square[i], false);
            }
            for (i = 0; i < exhaustiveI.size(); i++) {
                minPi = 2 *
                        CDF_Normal.normp(( -1) *
                                         Math.abs(Rj[((Relation) ((Vector)
                        exhaustiveI.
                        elementAt(i)).elementAt(0)).i] -
                                                  Rj[((Relation) ((Vector)
                        exhaustiveI.elementAt(i)).elementAt(0)).j]) / SE);
                for (j = 1; j < ((Vector) exhaustiveI.elementAt(i)).size(); j++) {
                    tmpPi = 2 *
                            CDF_Normal.normp(( -1) *
                                             Math.abs(Rj[((Relation) ((Vector)
                            exhaustiveI.
                            elementAt(i)).elementAt(j)).i] -
                            Rj[((Relation) ((Vector)
                                            exhaustiveI.elementAt(i)).
                                elementAt(j)).j]) / SE);
                    if (tmpPi < minPi) {
                        minPi = tmpPi;
                    }
                }
                if (minPi >
                    (0.05 / ((double) ((Vector) exhaustiveI.elementAt(i)).size()))) {
                    for (j = 0; j < ((Vector) exhaustiveI.elementAt(i)).size();
                             j++) {
                        square[((Relation) ((Vector) exhaustiveI.elementAt(i)).
                                elementAt(j)).i][((Relation) ((Vector)
                                exhaustiveI.elementAt(i)).elementAt(j)).j] = true;
                    }
                }
            }
        }

        //Non adjusted Pi value is computed for each comparison alpha=0.1
        Pi = new double[(int) nCombinations(2, nAlgorithms)];

        ALPHAiHolm = new double[(int) nCombinations(2, nAlgorithms)];

        ALPHAiShaffer = new double[(int) nCombinations(2, nAlgorithms)];

        algorithmOrder = new String[(int) nCombinations(2, nAlgorithms)];

        rankingOrder = new double[(int) nCombinations(2, nAlgorithms)];

        order = new int[(int) nCombinations(2, nAlgorithms)];

        SE = term3;
        seen = new boolean[(int) nCombinations(2, nAlgorithms)];

        for (i = 0, k = 0; i < nAlgorithms; i++) {
            for (j = i + 1; j < nAlgorithms; j++, k++) {
                rankingOrder[k] = Math.abs(Rj[i] - Rj[j]);
                algorithmOrder[k] = algorithmName[i] + " vs. " +
                                     algorithmName[j];
            }
        }

        Arrays.fill(seen, false);

        for (i = 0; i < rankingOrder.length; i++) {
            for (j = 0; seen[j] == true; j++) {
                ;
            }
            pos = j;
            maxVal = rankingOrder[j];
            for (j = j + 1; j < rankingOrder.length; j++) {
                if (seen[j] == false && rankingOrder[j] > maxVal) {
                    pos = j;
                    maxVal = rankingOrder[j];
                }
            }
            seen[pos] = true;
            order[i] = pos;
        }

        /*Shaffer and Bergmann tests*/
        pos = 0;
        tmp = nAlgorithms;
        for (i = 0; i < order.length; i++) {
            Pi[i] = 2 *
                    CDF_Normal.normp(( -1) *
                                     Math.abs((rankingOrder[order[i]]) / SE));
            ALPHAiHolm[i] = 0.1 / ((double) order.length - (double) i);
            ALPHAiShaffer[i] = 0.1 /
                               ((double) order.length -
                                (double) Math.max(pos, i));
            if (i == pos && Pi[i] <= ALPHAiShaffer[i]) {
                tmp--;
                pos = (int) nCombinations(2, nAlgorithms) -
                      (int) nCombinations(2, tmp);
            }
        }


        if (nAlgorithms < 9) {
            indexes.removeAllElements();
            for (i = 0; i < nAlgorithms; i++) {
                indexes.add(new Integer(i));
            }
            exhaustiveI = obtainExhaustive(indexes);
            square = new boolean[nAlgorithms][nAlgorithms];
            for (i = 0; i < nAlgorithms; i++) {
                Arrays.fill(square[i], false);
            }
            for (i = 0; i < exhaustiveI.size(); i++) {
                minPi = 2 *
                        CDF_Normal.normp(( -1) *
                                         Math.abs(Rj[((Relation) ((Vector)
                        exhaustiveI.
                        elementAt(i)).elementAt(0)).i] -
                                                  Rj[((Relation) ((Vector)
                        exhaustiveI.elementAt(i)).elementAt(0)).j]) / SE);
                for (j = 1; j < ((Vector) exhaustiveI.elementAt(i)).size(); j++) {
                    tmpPi = 2 *
                            CDF_Normal.normp(( -1) *
                                             Math.abs(Rj[((Relation) ((Vector)
                            exhaustiveI.
                            elementAt(i)).elementAt(j)).i] -
                            Rj[((Relation) ((Vector)
                                            exhaustiveI.elementAt(i)).
                                elementAt(j)).j]) / SE);
                    if (tmpPi < minPi) {
                        minPi = tmpPi;
                    }
                }
                if (minPi >
                    0.1 / ((double) ((Vector) exhaustiveI.elementAt(i)).size())) {
                    for (j = 0; j < ((Vector) exhaustiveI.elementAt(i)).size();
                             j++) {
                        square[((Relation) ((Vector) exhaustiveI.elementAt(i)).
                                elementAt(j)).i][((Relation) ((Vector)
                                exhaustiveI.elementAt(i)).elementAt(j)).j] = true;
                    }
                }
            }
        }

        /************ All vs All adjusted p values **************/

        adjustedP = new double[Pi.length][4];
        pos = 0;
        tmp = nAlgorithms;
        for (i = 0; i < adjustedP.length; i++) {
            adjustedP[i][0] = Pi[i] * (double) (adjustedP.length);
            adjustedP[i][1] = Pi[i] * (double) (adjustedP.length - i);
            adjustedP[i][2] = Pi[i] *
                              ((double) adjustedP.length -
                               (double) Math.max(pos, i));
            if (i == pos) {
                tmp--;
                pos = (int) nCombinations(2, nAlgorithms) -
                      (int) nCombinations(2, tmp);
            }
            if (nAlgorithms < 9) {
                maxAPi = Double.MIN_VALUE;
                minPi = Double.MAX_VALUE;
                for (j = 0; j < exhaustiveI.size(); j++) {
                    if (exhaustiveI.elementAt(j).toString().contains(couples[
                            order[i]].toString())) {
                        minPi = 2 *
                                CDF_Normal.normp(( -1) *
                                                 Math.abs(Rj[((Relation) ((
                                Vector)
                                exhaustiveI.elementAt(j)).elementAt(0)).i] -
                                Rj[((
                                        Relation) ((Vector) exhaustiveI.
                                elementAt(j)).
                                    elementAt(0)).j]) / SE);
                        for (k = 1;
                                 k < ((Vector) exhaustiveI.elementAt(j)).size();
                                 k++) {
                            tmpPi = 2 *
                                    CDF_Normal.normp(( -1) *
                                    Math.abs(Rj[((Relation) ((
                                            Vector)
                                    exhaustiveI.elementAt(j)).elementAt(k)).i] -
                                             Rj[((Relation) ((Vector)
                                    exhaustiveI.elementAt(j)).
                                                 elementAt(k)).j]) /
                                    SE);
                            if (tmpPi < minPi) {
                                minPi = tmpPi;
                            }
                        }
                        tmpAPi = minPi *
                                 (double) (((Vector) exhaustiveI.elementAt(j)).
                                           size());
                        if (tmpAPi > maxAPi) {
                            maxAPi = tmpAPi;
                        }
                    }
                }
                adjustedP[i][3] = maxAPi;
            }
        }

        for (i = 1; i < adjustedP.length; i++) {
            if (adjustedP[i][1] < adjustedP[i - 1][1]) {
                adjustedP[i][1] = adjustedP[i - 1][1];
            }
            if (adjustedP[i][2] < adjustedP[i - 1][2]) {
                adjustedP[i][2] = adjustedP[i - 1][2];
            }
            if (adjustedP[i][3] < adjustedP[i - 1][3]) {
                adjustedP[i][3] = adjustedP[i - 1][3];
            }
        }

        if (this.Nemenyi) {
            output += "\\begin{table}[!htp]\n\\centering\\scriptsize\n\\caption{Adjusted $p$-values}\n" +
                    "\\begin{tabular}{ccccc}\n" +
                    "i&hypothesis&unadjusted $p$&$p_{Neme}$\\\\\n\\hline";
            for (i = 0; i < Pi.length; i++) {
                output += (i + 1) + "&" +
                        algorithmName[couples[order[i]].i] +
                        " vs ." +
                        algorithmName[couples[order[i]].j] +
                        "&" + Pi[i] +
                        "&" + adjustedP[i][0] + "\\\\";
            }
            output += "\\hline\n" + "\\end{tabular}\n" + "\\end{table}\n";
        }
        output += "\\end{document}";
        return output;
    }


    private void computeROM(double alpha, double vector[]) {

        int i, j;
        int m;
        double sum1, sum2;

        m = vector.length;

        vector[m - 1] = alpha;
        vector[m - 2] = alpha / 2.0;

        for (i = 3; i <= m; i++) {
            sum1 = sum2 = 0;
            for (j = 1; j < (i - 1); j++) {
                sum1 += Math.pow(alpha, (double) j);
            }
            for (j = 1; j < (i - 2); j++) {
                sum2 += nCombinations(j, i) *
                        Math.pow(vector[m - j - 1], (double) (i - j));
            }
            vector[m - i] = (sum1 - sum2) / (double) i;
        }

    }

	/**
	* <p>
	* Binomial coefficient (m,n) 
	* </p>
	* @param m Number of possibilities
	* @param n Size of the unordered outcomes
	* @return The value of the binomial coefficient
	*/
    private double nCombinations(int m, int n) {

        double result = 1;
        int i;

        for (i = 1; i <= m; i++) {
            result *= (double) (n - m + i) / (double) i;
        }
        return result;
    }

    private Vector obtainExhaustive(Vector indexes) {

        Vector result = new Vector();
        int i, j, k;
        String binary;
        boolean[] number = new boolean[indexes.size()];
        Vector ind1, ind2;
        Vector set = new Vector();
        Vector res1, res2;
        Vector temp;
        Vector temp2;
        Vector temp3;

        ind1 = new Vector();
        ind2 = new Vector();
        temp = new Vector();
        temp2 = new Vector();
        temp3 = new Vector();

        for (i = 0; i < indexes.size(); i++) {
            for (j = i + 1; j < indexes.size(); j++) {
                set.addElement(new Relation(((Integer) indexes.elementAt(i)).
                                            intValue(),
                                            ((Integer) indexes.elementAt(j)).
                                            intValue()));
            }
        }
        if (set.size() > 0) {
            result.addElement(set);
        }

        for (i = 1; i < (int) (Math.pow(2, indexes.size() - 1)); i++) {
            Arrays.fill(number, false);
            ind1.removeAllElements();
            ind2.removeAllElements();
            temp.removeAllElements();
            temp2.removeAllElements();
            temp3.removeAllElements();
            binary = Integer.toString(i, 2);
            for (k = 0; k < number.length - binary.length(); k++) {
                number[k] = false;
            }
            for (j = 0; j < binary.length(); j++, k++) {
                if (binary.charAt(j) == '1') {
                    number[k] = true;
                }
            }
            for (j = 0; j < number.length; j++) {
                if (number[j] == true) {
                    ind1.addElement(new Integer(((Integer) indexes.elementAt(j)).
                                                intValue()));
                } else {
                    ind2.addElement(new Integer(((Integer) indexes.elementAt(j)).
                                                intValue()));
                }
            }
            res1 = obtainExhaustive(ind1);
            res2 = obtainExhaustive(ind2);
            for (j = 0; j < res1.size(); j++) {
                result.addElement(new Vector((Vector) res1.elementAt(j)));
            }
            for (j = 0; j < res2.size(); j++) {
                result.addElement(new Vector((Vector) res2.elementAt(j)));
            }
            for (j = 0; j < res1.size(); j++) {
                temp = (Vector) ((Vector) res1.elementAt(j)).clone();
                for (k = 0; k < res2.size(); k++) {
                    temp2 = (Vector) temp.clone();
                    temp3 = (Vector) ((Vector) res2.elementAt(k)).clone();
                    if (((Relation) temp2.elementAt(0)).i <
                        ((Relation) temp3.elementAt(0)).i) {
                        temp2.addAll((Vector) temp3);
                        result.addElement(new Vector(temp2));
                    } else {
                        temp3.addAll((Vector) temp2);
                        result.addElement(new Vector(temp3));

                    }
                }
            }
        }
        for (i = 0; i < result.size(); i++) {
            if (((Vector) result.elementAt(i)).toString().equalsIgnoreCase("[]")) {
                result.removeElementAt(i);
                i--;
            }
        }
        for (i = 0; i < result.size(); i++) {
            for (j = i + 1; j < result.size(); j++) {
                if (((Vector) result.elementAt(i)).toString().equalsIgnoreCase(((
                        Vector) result.elementAt(j)).toString())) {
                    result.removeElementAt(j);
                    j--;
                }
            }
        }
        return result;
    }
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
    }

	/**
	* <p>
	* This method decodes composes the header of the LaTeX file where the results are saved
	* </p>
	* @return A string with the header of the LaTeX file
	*/    
    private String header() {
        String output = new String("");
        output += "\\documentclass[a4paper,12pt]{article}\n";
        output += "\\usepackage [english] {babel}\n";
        output += "\\usepackage [latin1]{inputenc}\n";
        output += "\\usepackage{graphicx}\n";
        output += "\\usepackage{fancyhdr}\n";
        output += "\\pagestyle{fancy}\\fancyfoot[C]{Page \\thepage}\n";
        output += "\\fancyhead[L]{Friedman Test and Post-Hoc Tests.}\n";
        output +=
                "\\textwidth=17cm \\topmargin=-0.5cm \\oddsidemargin=-0.5cm \\textheight=23cm\n";
        output +=
                "\\title{Output Tables for the Friedman Test and Post-Hoc Tests.}\n";
        output +=
                "\\date{\\today}\n\\begin{document}\n\\maketitle\n\\section{Tables.}\n\n";

        return output;

    }
    
	/**
	* <p>
	* This method computes the cumulative distribution function of a Chi-square distribution
	* @param x x value
	* @param n The parameter of the Chi Square distribution
	* </p>
	* @return A double with the cumulative distribution value
	*/    
    private double ChiSq(double x, int n) {
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
	* <p>
	* This method computes the cumulative distribution function of Fisher F distribution
	* @param f x value
	* @param n1 The first parameter of the Fisher F distribution
	* @param n2 The second parameter of the Fisher F distribution
	* </p>
	* @return A double with the cumulative distribution value
	*/    
    private double FishF(double f, int n1, int n2) {
        double x = n2 / (n1 * f + n2);
        if ((n1 % 2) == 0) {
            return StatCom(1 - x, n2, n1 + n2 - 4, n2 - 2) * Math.pow(x, n2 / 2);
        }
        if ((n2 % 2) == 0) {
            return 1 -
                    StatCom(x, n1, n1 + n2 - 4, n1 - 2) *
                    Math.pow(1 - x, n1 / 2);
        }
        double th = Math.atan(Math.sqrt(n1 * f / n2));
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
            return 1 - a + c / 2;
        }
        double k = 2;
        while (k <= (n2 - 1) / 2) {
            c = c * k / (k - .5);
            k = k + 1;
        }
        return 1 - a + c;
    }

    private double StatCom(double q, int i, int j, double b) {
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

}
