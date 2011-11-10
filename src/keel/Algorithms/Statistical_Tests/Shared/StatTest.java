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
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 01/01/2004
* @author Modified by Jose Otero (University of Oviedo) 01/10/2008
* @author Modified by Amelia Zafra (University of Granada) 01/01/2006
* @author Modified by Alberto Fernandez (University of Granada)01/01/2008
* @author Modified by Salvador Garcia (University of Granada) 01/01/2007
* @author Modified by Joaquin Derrac (University of Granada)29/04/2010
* @version 1.0
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Statistical_Tests.Shared;

import java.io.*;
import java.util.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import keel.Algorithms.Shared.Parsing.ProcessConfig;
import keel.GraphInterKeel.experiments.Experiments;
import org.core.Fichero;
import keel.Algorithms.Statistical_Tests.Shared.nonParametric.*;

public class StatTest {
	/**
	* <p>
	* In this class all the statistical tests and output modules are defined
	* </p>
	*/
    // statistical tests constants names
	// Naming convention: name of the test or output module
	// plus a R for regression or C for classification
	
	// Dietterich 5x2cv
    public final static int Dietterich5x2cvR = 0;
    public final static int Dietterich5x2cvC = 1;
    //t test
    public final static int tR = 2;
    public final static int tC = 3;
    //shapiro wilk
    public final static int ShapiroWilkR = 4;
    public final static int ShapiroWilkC = 5;
    //Wilcoxon signed ranks
    public final static int WilcoxonR = 6;
    public final static int WilcoxonC = 7;
    //Mann-Whitney u
    public final static int MannWhitneyR = 8;
    public final static int MannWhitneyC = 9;
    //F test
    public final static int fM = 10;
    public final static int fC = 11;
    //Summary of data, 1 algorithm
    public final static int summaryC = 12;
    public final static int summaryR = 13;
    //Summary of data, multiple algorithms
    public final static int generalC = 14;
    public final static int generalR = 15;
    public final static int tabularC = 16;
    public final static int tabularR = 17;
    // Summary of data, train & test, one algorithm
    public final static int trainTestR = 18;
    public final static int trainTestC = 19;
    //Wilcoxon Test
    public final static int globalWilcoxonC = 20;
    public final static int globalWilcoxonR = 21;
    //Friedman
    public final static int FriedmanC = 22;
    public final static int FriedmanR = 23;
    public final static int FriedmanAlignedC = 24;
    public final static int FriedmanAlignedR = 25;
    public final static int QuadeC = 26;
    public final static int QuadeR = 27;   
    //Constrast
    public final static int ContrastC = 28;
    public final static int ContrastR = 29;
    //Multiple test
    public final static int MultipleC = 30;
    public final static int MultipleR = 31;
    
    // Visualize Imbalanced
    //Summary of data, 1 algorithm imbalanced
    public final static int summaryI = 32;
    //Summary of data, multiple algorithms
    public final static int generalI = 33;
    public final static int tabularI = 34;
    // Tests for imbalanced
    //Wilcoxon Test
    public final static int globalWilcoxonI = 35;
    //Friedman
    public final static int FriedmanI = 36;
    Friedman stat;
    double[] mean;
    int[] nResults;
    int nAlgorithms = 2; 

    // Module Local Variables
    private double[][][] differences;
    private double[][][] unclassified;
    private double[][][] totals;
    private int nFolds;
    private int nOutputs;
    private DecimalFormat df = new DecimalFormat("0.0000");
    private DecimalFormatSymbols especificSymbol = new DecimalFormatSymbols();
    private DecimalFormat gbcf = new DecimalFormat("0.000000000000E00",
            especificSymbol);
    private DecimalFormat dfTable1 = new DecimalFormat("#0.0000000000");
    private DecimalFormat dfPvalue = new DecimalFormat("#0.0000");

	/**
	* <p>
	* t Distribution function with 'nu' degrees of freedom
	* </p>
	* @param x x value
	* @param nu Degrees of freedom
	* @return The value of a t distribution function with nu degrees of freedom
	*/
    private double pt(double x, int nu) {
        // px is the result
        // qx = right queue
        // bt = left/right
        double c, t, tt, vt, bt, qx, px;
        int i, nt;
        c = 0.6366197724;
        x = x / Math.sqrt(nu);
        t = Math.atan(x); //t=theta
        if (nu == 1) {
            bt = t * c;
            px = (1.0 + bt) / 2.0;
            qx = 1.0 - (px);
            return px;
        }
        nt = 2 * (nu / 2);
        if (nt == nu) {
            tt = Math.pow(Math.cos(t), 2);
            bt = 1.0;
            vt = 1.0;
            if (nu != 2) {
                i = 1;
                while (i <= nu - 3) {
                    vt = vt * i * tt / (i + 1);
                    bt = bt + vt;
                    i = i + 2;
                }
            }
            bt = bt * Math.sin(t);
            px = (1.0 + bt) / 2.0;
            qx = 1.0 - (px);
            return px;
        }
        bt = Math.cos(t);
        tt = bt * (bt);
        vt = bt;
        if (nu != 3) {
            i = 2;
            while (i <= nu - 3) {
                vt = vt * i * tt / (i + 1);
                bt = bt + vt;
                i = i + 2;
            }
        }
        bt = bt * Math.sin(t);
        bt = (bt + t) * c;
        px = (1.0 + bt) / 2.0;
        qx = 1.0 - (px);
        return px;
    }
    /**
     * <p>
     * Computes the median of a sample
     * </p>
     * @param x1 A vector with the sample values
     * @return The median of x1 values  
     */    
    private double median(double[] x1) {
    	double x[] = new double[x1.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = x1[i];
        }
        Arrays.sort(x);
        if (x.length % 2 == 1) {
            return x[x.length / 2];
        } else {
            return (x[x.length / 2 - 1] + x[x.length / 2]) / 2;
        }
    }

    /**
     * <p>
     * Computes the mean of a sample
     * </p>
     * @param x A vector with the sample values
     * @return The mean of x values  
     */    	 
    private double mean(double[] x) {
        double result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i];
        }
        return result / x.length;
    }

    /**
     * <p>
     * Computes the p-value of Dietterich 5x2cv statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    	
    private double[] test5x2cv(double[][][] err, double significance,
                               PrintStream p) {
        double[] result = new double[err[0][0].length];

        for (int out = 0; out < result.length; out++) {
            double[] dm1 = new double[err[0].length];
            double[] dm2 = new double[err[1].length];
            double err1 = 0;
            for (int i = 0; i < err[0].length; i++) {
                err1 += err[0][i][out];
                dm1[i] = err[0][i][out];
            }
            err1 /= err[0].length;
            double err2 = 0;
            for (int i = 0; i < err[1].length; i++) {
                err2 += err[1][i][out];
                dm2[i] = err[1][i][out];
            }
            err2 /= err[1].length;
            p.println("Mean error, algorithm 1, output " + out + " = " + err1);
            p.println("Mean error, algorithm 2, output " + out + " = " + err2);
            p.println();
            p.println("Median of error algorithm 1, output " + out + " =" +
                      median(dm1));
            p.println("Median of error algorithm 2, output " + out + " =" +
                      median(dm2));
            double[] s2 = new double[5];
            float mlq = 0;
            for (int i = 0; i < 5; i++) {
                double tmp, tmp1, tmp2;
                tmp = (err[0][2 * i][out] - err[1][2 * i][out] + err[0][2 * i +
                       1][out] - err[1][2 * i + 1][out]) / 2;
                tmp1 = err[0][2 * i][out] - err[1][2 * i][out] - tmp;
                tmp2 = err[0][2 * i + 1][out] - err[1][2 * i + 1][out] - tmp;
                s2[i] = tmp1 * tmp1 + tmp2 * tmp2;
                mlq += s2[i];
            }
            mlq /= 5;
            result[out] = 2 *
                        (1 -
                         pt(Math.abs(err[0][0][out] - err[1][0][out]) /
                            Math.sqrt(mlq), 5));
            p.println("Hull hypothesis, true difference in means is equal to 0");
            if (result[out] < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
        }
        return result;
    }

    
    /**
     * <p>
     * Computes the p-value of Student paired t statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    	
    private double[] testt(double[][][] err, double significance, PrintStream p) {
        double[] result = new double[err[0][0].length];
        double[] mean = new double[err[0][0].length];
        double[] variance = new double[err[0][0].length];

        for (int out = 0; out < result.length; out++) {
            double m = 0, m2 = 0;
            for (int i = 0; i < err[0].length; i++) {
                double diff = err[0][i][out] - err[1][i][out];
                m += diff;
                m2 += diff * diff;
            }
            m /= err[0].length;
            m2 /= err[0].length;
            double sigma = Math.sqrt(m2 - m * m) * err[0].length /
                           (err[0].length - 1);
            if (sigma > 0) {
                double x = m / sigma * Math.sqrt(err[0].length);
                int glib = err[0].length - 1;
                result[out] = 2 * (1 - pt(Math.abs(x), glib));
                p.println(
                        "Null hypothesis, true difference in means is equal to 0");
                if (result[out] < 1 - significance) {
                    p.println("Output=" + out + ": There is evidence against H0");
                } else {
                    p.println("Output=" + out +
                              ": There is no evidence against H0");
                }
            } else {
                p.println("Both algorithms have constant difference");
                if (m != 0) {
                    p.println("Output=" + out + ": There is evidence against H0");
                } else {
                    p.println("Output=" + out +
                              ": There is no evidence against H0");
                }
            }
        }
        return result;
    }
    /**
     * <p>
     * Computes the p-value of Student unpaired t statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    	
    private double[] testtvar(double[][][] err, double significance, PrintStream p) {
        double[] result = new double[err[0][0].length];
        double[] mean1 = new double[err[0][0].length];
        double[] mean2 = new double[err[0][0].length];
        double[] sqrMean1 = new double[err[0][0].length];
        double[] sqrMean2 = new double[err[0][0].length];

        for (int out = 0; out < result.length; out++) {
            double m = 0, m2 = 0;
            for (int i = 0; i < err[0].length; i++) {
                mean1[out] += err[0][i][out];
                sqrMean1[out] += err[0][i][out] * err[0][i][out];
            }
            for (int i = 0; i < err[1].length; i++) {
                mean2[out] += err[1][i][out];
                sqrMean2[out] += err[1][i][out] * err[1][i][out];
            }
            mean1[out] /= err[0].length;
            mean2[out] /= err[1].length;
            sqrMean1[out] /= err[0].length;
            sqrMean2[out] /= err[1].length;
            double var1 = (sqrMean1[out] - mean1[out] * mean1[out]) *
                          (err[0].length / (double) (err[0].length - 1));
            double var2 = (sqrMean2[out] - mean2[out] * mean2[out]) *
                          (err[1].length / (double) (err[1].length - 1));
            double t = (mean1[out] - mean2[out]) /
                       Math.sqrt(var1 / err[0].length + var2 / err[1].length);
            double dg = (var1 / err[0].length + var2 / err[1].length);
            dg = dg * dg;
            dg /= (
                    (var1 / err[0].length * var1 / err[0].length) /
                    (err[0].length - 1) +
                    (var2 / err[1].length * var2 / err[1].length) /
                    (err[1].length - 1)
                    );
            result[out] = 2 * (1 - pt(Math.abs(t), (int) dg));
            p.println("Null hypothesis, true difference in means is equal to 0");
            if (result[out] < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
        }
        return result;
    }
    /**
     * <p>
     * Computes the p-value of Snedecor F statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    	
    private double[] testf(double[][][] err, double significance, PrintStream p) {
        double[] result = new double[err[0][0].length];
        int numDF, denDF;
        //For each dataset
        for (int out = 0; out < err[0][0].length; out++) {
            //mean algo1
            double meanAlg1 = 0;
            for (int it = 0; it < err[0].length; it++) {
                meanAlg1 += err[0][it][out];
            }
            meanAlg1 /= err[0].length;
            //mean algo2
            double meanAlg2 = 0;
            for (int it = 0; it < err[1].length; it++) {
                meanAlg2 += err[1][it][out];
            }
            meanAlg2 /= err[1].length;
            //sample variance algo1
            double sampleVariance1 = 0;
            for (int it = 0; it < err[0].length; it++) {
                sampleVariance1 += (err[0][it][out] - meanAlg1) *
                        (err[0][it][out] - meanAlg1);
            }
            sampleVariance1 /= err[0].length - 1;
            sampleVariance1 = Math.sqrt(sampleVariance1);
            //sample variance algo2
            double sampleVariance2 = 0;
            for (int it = 0; it < err[1].length; it++) {
                sampleVariance2 += (err[1][it][out] - meanAlg2) *
                        (err[1][it][out] - meanAlg2);
            }
            sampleVariance2 /= err[1].length - 1;
            sampleVariance2 = Math.sqrt(sampleVariance2);
            //F statistic
            double statistic = 0;
            if (sampleVariance1 > sampleVariance2) {
                statistic = sampleVariance1 * sampleVariance1 / sampleVariance2 / sampleVariance2;
                numDF = err[0].length - 1;
                denDF = err[1].length - 1;
            } else {
                statistic = sampleVariance2 * sampleVariance2 / sampleVariance1 / sampleVariance1;
                numDF = err[1].length - 1;
                denDF = err[0].length - 1;
            }
            double pValue = pf(statistic, numDF, denDF);
            if (pValue < 1 - pValue) {
                pValue *= 2;
            } else {
                pValue = 2 * (1 - pValue);
            }
            //Null Hypothesis: Equal variances
            //Alternative Hypothesis : different variances
            p.println("Null hypothesis, equal variances");
            if (pValue < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
            result[out] = pValue;
        }
        return result;
    }
    /**
     * <p>
     * Computes the p-value of  Mann Whitney U statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    
    private double[] testu(double[][][] err, double significance, PrintStream p) {
        double[] result = new double[err[0][0].length];
        //For each output
        for (int out = 0; out < err[0][0].length; out++) {
            //class pair is used to redefine < operator
            //First member is the value
            //Second member is the sample from which the value is taken
            class pair {
                double first;
                int second;
                pair() {
                    first = 0;
                    second = 0;
                }

                pair(double x, int y) {
                    first = x;
                    second = y;
                }
            };
            //Now, samples from both algorithms are concatenated
            //The number of elements in each sample is the same for
            //both algorithms in KEEL but
            //this algorithm is generic.
            pair totSample[] = new pair[err[0].length + err[1].length];
            for (int i = 0; i < err[0].length; i++) {
                totSample[i] = new pair(err[0][i][out], 1);
            }
            for (int i = 0; i < err[1].length; i++) {
                totSample[i + err[0].length] = new pair(err[1][i][out], 2);
            }
            //Sorting of the whole sample
            Arrays.sort(totSample, new Comparator() {
                public int compare(Object a, Object b) {
                    pair x = (pair) a;
                    pair y = (pair) b;
                    if (x.first < y.first) {
                        return -1;
                    } else
                    if (x.first > y.first) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            //Rank asignment
            double rank[] = new double[totSample.length];
            int i = 0;
            //for each element
            while (i < totSample.length) {
                //Starting in the next
                int j = i + 1;
                //while j is in the range
                while (j < totSample.length) {
                    //if both samples are equal
                    if (totSample[i].first == totSample[j].first) {
                        j++;
                    }
                    //if not
                    else {
                        break;
                    }
                }
                //For elements from i to j, asign rank
                for (int k = i; k < j; k++) {
                    rank[k] = (double) (i + j - 1) / 2 + 1;
                }
                //Restart before processing next elements
                i = j;
            }
            //Smallest sample.
            int min = 0;
            int iMin, iMax;
            if (err[0].length < err[1].length) {
                min = 1;
                iMin = 0;
                iMax = 1;
            } else {
                min = 2;
                iMin = 1;
                iMax = 0;
            }
            //
            //Statistic computation
            //The sum of the ranks from smallest population is computed
            double sum = 0;
            for (int k = 0; k < totSample.length; k++) {
                if (totSample[k].second == min) {
                    sum += rank[k];
                }
            }
            //the statistic
            double z = err[iMin].length * err[iMax].length +
                       (double) err[iMin].length * (err[iMin].length + 1) /
                       2 - sum;
            //Distribution approximation for m>=10, n>=10 -> N(mean,sigma)
            double mean = err[iMin].length * (double) err[iMax].length / 2;
            double sigma = Math.sqrt(err[iMin].length * err[iMax].length *
                                     (double) (err[iMin].length +
                                               err[iMax].length + 1) / 12);
            if (z > err[0].length * (err[0].length + 1) / 4) {
                result[out] = pnorm(z - 1, true, mean, sigma * sigma);
            } else {
                result[out] = pnorm(z, false, mean, sigma * sigma);
            }
            result[out] = result[out] < 1 ? 2 * result[out] : 1;
            p.println("Null hypothesis, true difference in means is equal to 0");
            if (result[out] < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
        }
        return result;
    }
    
    /**
     * <p>
     * Computes the p-value of  Wilcoxon signed rank test statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */  
    private double[] testrs(double[][][] err, double significance, PrintStream p) {
        double[] result = new double[err[0][0].length];
        //For each output
        for (int out = 0; out < err[0][0].length; out++) {
            //Diferences between algorithm are calculated
            //The number of ties are counted
            double diff[] = new double[err[0].length];
            int nulls = 0;
            for (int i = 0; i < err[0].length; i++) {
                diff[i] = err[0][i][out] - err[1][i][out];
                if (diff[i] == 0.0) {
                    nulls++;
                }
            }
            //class pair is used to redefine < operator
            //First member is the absolute value
            //Second member is the sign
            class pair {
                double first;
                int second;
                pair() {
                    first = 0.0;
                    second = 0;
                }

                pair(double x, int y) {
                    first = x;
                    second = y;
                }
            };
            //Remove 0 values and build pair vector
            pair diff2[] = new pair[err[0].length - nulls];
            int idiff2 = 0;
            for (int i = 0; i < err[0].length; i++) {
                if (diff[i] != 0) {
                    //First is the absolute value, and the second is the sign (1 or -1)
                    diff2[idiff2] = new pair(Math.abs(diff[i]),
                                             (int) (Math.abs(diff[i]) / diff[i]));
                    idiff2++;
                }
            }
            //sorting by absolute value
            Arrays.sort(diff2, new Comparator() {
                public int compare(Object a, Object b) {
                    pair x = (pair) a;
                    pair y = (pair) b;
                    if (x.first < y.first) {
                        return -1;
                    } else
                    if (x.first > y.first) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            //Compute ranks
            double rank[] = new double[diff2.length];
            int i = 0;
            //for each element
            while (i < diff2.length) {
                //Starting in the next
                int j = i + 1;
                //while j is in the range
                while (j < diff2.length) {
                    //if both samples are equal
                    if (diff2[i].first == diff2[j].first) {
                        j++;
                    }
                    //if not
                    else {
                        break;
                    }
                }
                //For element from i to j, asign rank
                for (int k = i; k < j; k++) {
                    rank[k] = (double) (i + j - 1) / 2 + 1;
                }
                //Restart before processed elements
                i = j;
            }
            //smallest sample.
            double sum = 0;
            for (int k = 0; k < diff2.length; k++) {
                sum += rank[k] * diff2[k].second;
            }
            double mean = 0;
            double variance = diff2.length * (diff2.length + 1) *
                              (2 * diff2.length + 1) / 6.0;
            if (sum > 0) {
                result[out] = pnorm((sum - .5) / Math.sqrt(variance), true);
            } else {
                result[out] = pnorm((sum + .5) / Math.sqrt(variance), true);
            }
            if (result[out] < 1 - result[out]) {
                result[out] = 2 * result[out];
            } else {
                result[out] = 2 * (1 - result[out]);
            }
            p.println("Null hypothesis, true difference in means is equal to 0");
            if (result[out] < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
        }
        return result;
    }

    /**
     * <p>
     * Computes the p-value of  Wilcoxon signed rank test statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets
     * </p>
     * @param err A matrix with the samples values indexed by algorithm and fold
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */
    private double [] testrsImbMeasure(double [][] data, double significance, PrintStream p) {
        double [] result = new double[1];
        double [][] measure = new double [data.length][data[0].length];

        // The test is thought to be done with the error measure
        for (int i=0; i<data.length; i++)
            for (int j=0; j<data[0].length; j++)
                measure[i][j] = 1.0 - data[i][j];

        //For each output
        //Diferences between algorithm are calculated
        //The number of ties are counted
        double diff[] = new double[measure[0].length];
        int nulls = 0;
        for (int i = 0; i < measure[0].length; i++) {
            diff[i] = measure[0][i] - measure[1][i];
            if (diff[i] == 0.0) {
                nulls++;
            }
        }
        //class pair is used to redefine < operator
        //First member is the absolute value
        //Second member is the sign
        class pair {
            double first;
            int second;
            pair() {
                first = 0.0;
                second = 0;
            }

            pair(double x, int y) {
                first = x;
                second = y;
            }
        };
        //Remove 0 values and build pair vector
        pair diff2[] = new pair[measure[0].length - nulls];
        int idiff2 = 0;
        for (int i = 0; i < measure[0].length; i++) {
            if (diff[i] != 0) {
                //First is the absolute value, and the second is the sign (1 or -1)
                diff2[idiff2] = new pair(Math.abs(diff[i]),
                                         (int) (Math.abs(diff[i]) / diff[i]));
                idiff2++;
            }
        }
        //sorting by absolute value
        Arrays.sort(diff2, new Comparator() {
            public int compare(Object a, Object b) {
                pair x = (pair) a;
                pair y = (pair) b;
                if (x.first < y.first) {
                    return -1;
                } else
                if (x.first > y.first) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        //Compute ranks
        double rank[] = new double[diff2.length];
        int i = 0;
        //for each element
        while (i < diff2.length) {
            //Starting in the next
            int j = i + 1;
            //while j is in the range
            while (j < diff2.length) {
                //if both samples are equal
                if (diff2[i].first == diff2[j].first) {
                    j++;
                }
                //if not
                else {
                    break;
                }
            }
            //For element from i to j, asign rank
            for (int k = i; k < j; k++) {
                rank[k] = (double) (i + j - 1) / 2 + 1;
            }
            //Restart before processed elements
            i = j;
        }
        //smallest sample.
        double sum = 0;
        for (int k = 0; k < diff2.length; k++) {
            sum += rank[k] * diff2[k].second;
        }
        double mean = 0;
        double variance = diff2.length * (diff2.length + 1) *
                          (2 * diff2.length + 1) / 6.0;
        if (sum > 0) {
            result[0] = pnorm((sum - .5) / Math.sqrt(variance), true);
        } else {
            result[0] = pnorm((sum + .5) / Math.sqrt(variance), true);
        }
        if (result[0] < 1 - result[0]) {
            result[0] = 2 * result[0];
        } else {
            result[0] = 2 * (1 - result[0]);
        }
        p.println("Null hypothesis, true difference in means is equal to 0");
        if (result[0] < 1 - significance) {
            p.println("Output=" + 0 + ": There is evidence against H0");
        } else {
            p.println("Output=" + 0 + ": There is no evidence against H0");
        }
        return result;
    }

    /**
     * <p>
     * Quoted from original Fortran documentation:
     * Evaluates the tail area of the standardised normal curve 
     * from x to infinity if upper is true or
     * from minus infinity to x if upper is false.
     * Translated from Fortran to C and from C to Java.
     * Original code published in Applied Statistics (1973) vol22 no.3
     * Algorithm AS66
     * </p>
     * @param x x value
     * @param upper an int used as a boolean
     * @return The value of the tail area of the standardised normal curve
     */  
    public static double alnorm(double x, int upper) {
        double ret, z, y;
        int up;
        //Constants tuned according to  AS 66, originally  LTONE=7.0 UTZERO=18.66
        //LTONE=(n+9)/3, n number of decimal digits
        //UTZERO=x/ 4.94065645841246544e-324 (java smallest double) < exp(x*x/2)/x/sqrt(2*PI)
        double LTONE = 8.333333;
        double UTZERO = 38.46742;
        up = upper;
        z = x;
        if (x < 0.0) {
            up = up == 0 ? 1 : 0;
            z = -x;
        }
        if (!(z <= LTONE || (up == 1 && z <= UTZERO))) {
            ret = 0.0;
        } else {
            y = 0.5 * z * z;
            if (z <= 1.28) {
                ret = 0.5 - z * (0.398942280444 - 0.399903438504 * y /
                                 (y + 5.75885480458 - 29.8213557808 /
                                  (y + 2.62433121679 + 48.6959930692 /
                                   (y + 5.92885724438))));
            } else {
                ret = 0.398942280385 * Math.exp( -y) /
                      (z - 3.8052e-8 + 1.00000615302 /
                       (z + 3.98064794e-4 + 1.98615381364 /
                        (z - 0.151679116635 + 5.29330324926 /
                         (z + 4.8385912808 - 15.1508972451 /
                          (z + 0.742380924027 + 30.789933034 /
                           (z + 3.99019417011))))));
            }
        }
        if (up == 0) {
            ret = 1.0 - ret;
        }
        return ret;
    }

/*
 
 C
C	ALGORITHM AS241  APPL. STATIST. (1988) VOL. 37, NO. 3
C
C	Produces the normal deviate Z corresponding to a given lower
C	tail area of P; Z is accurate to about 1 part in 10**16.
 */
    
    /**
     * <p>
     * Quoted from original Fortran documentation:
     * Produces the normal deviate Z corresponding to a given lower
     * tail area of P; Z is accurate to about 1 part in 10**16.
     * Translated from Fortran to C and from C to Java.
     * Original code published in Applied Statistics (1988) vol37 no. 3
     * Algorithm AS241
     * </p>
     * @param p Area value
     * @return The normal deviate value corresponding to lower tail area equal to p
     */  
    public static double ppnd16(double p) {
        double zero = 0.0, one = 1.0, half = 0.5;
        double split1 = 0.425, split2 = 5.0;
        double const1 = 0.180625, const2 = 1.6;
        double a[] = {
                     3.3871328727963666080e0,
                     1.3314166789178437745e+2,
                     1.9715909503065514427e+3,
                     1.3731693765509461125e+4,
                     4.5921953931549871457e+4,
                     6.7265770927008700853e+4,
                     3.3430575583588128105e+4,
                     2.5090809287301226727e+3};
        double b[] = {
                     0.0,
                     4.2313330701600911252e+1,
                     6.8718700749205790830e+2,
                     5.3941960214247511077e+3,
                     2.1213794301586595867e+4,
                     3.9307895800092710610e+4,
                     2.8729085735721942674e+4,
                     5.2264952788528545610e+3};
        double c[] = {
                     1.42343711074968357734e0,
                     4.63033784615654529590e0,
                     5.76949722146069140550e0,
                     3.64784832476320460504e0,
                     1.27045825245236838258e0,
                     2.41780725177450611770e-1,
                     2.27238449892691845833e-2,
                     7.74545014278341407640e-4};
        double d[] = {
                     0.0,
                     2.05319162663775882187e0,
                     1.67638483018380384940e0,
                     6.89767334985100004550e-1,
                     1.48103976427480074590e-1,
                     1.51986665636164571966e-2,
                     5.47593808499534494600e-4,
                     1.05075007164441684324e-9};
        double e[] = {
                     6.65790464350110377720e0,
                     5.46378491116411436990e0,
                     1.78482653991729133580e0,
                     2.96560571828504891230e-1,
                     2.65321895265761230930e-2,
                     1.24266094738807843860e-3,
                     2.71155556874348757815e-5,
                     2.01033439929228813265e-7};
        double f[] = {
                     0.0,
                     5.99832206555887937690e-1,
                     1.36929880922735805310e-1,
                     1.48753612908506148525e-2,
                     7.86869131145613259100e-4,
                     1.84631831751005468180e-5,
                     1.42151175831644588870e-7,
                     2.04426310338993978564e-15};
        double q, r, ret;
        q = p - half;
        if (Math.abs(q) <= split1) {
            r = const1 - q * q;
            ret = q * (((((((a[7] * r + a[6]) * r + a[5]) * r + a[4]) * r + a[3])
                         * r + a[2]) * r + a[1]) * r + a[0]) /
                  (((((((b[7] * r + b[6]) * r + b[5]) * r + b[4]) * r + b[3])
                     * r + b[2]) * r + b[1]) * r + one);
            return ret;
        } else {
            if (q < zero) {
                r = p;
            } else {
                r = one - p;
            }
            if (r <= zero) {
                return zero;
            }
            r = Math.sqrt( -Math.log(r));
            if (r <= split2) {
                r -= const2;
                ret = (((((((c[7] * r + c[6]) * r + c[5]) * r + c[4]) * r + c[3])
                         * r + c[2]) * r + c[1]) * r + c[0]) /
                      (((((((d[7] * r + d[6]) * r + d[5]) * r + d[4]) * r + d[3])
                         * r + d[2]) * r + d[1]) * r + one);
            } else {
                r -= split2;
                ret = (((((((e[7] * r + e[6]) * r + e[5]) * r + e[4]) * r + e[3])
                         * r + e[2]) * r + e[1]) * r + e[0]) /
                      (((((((f[7] * r + f[6]) * r + f[5]) * r + f[4]) * r + f[3])
                         * r + f[2]) * r + f[1]) * r + one);
            }
            if (q < zero) {
                ret = -ret;
            }
            return ret;
        }
    }

    /**
     * <p>
     * Quoted from original Fortran documentation:
     * Calculates approximate expected values of normal order statistics.
     * Translated from Fortran to C and from C to Java.
     * Original code published in Applied Statistics 1982) Vol.31, No.2
     * Algorithm 177.3
     * </p>
     * @param n The sample size
     * @param n2 The number of order statistics required; must be <= n/2
     * @return The first n2 expected values
     */  
    public static double[] nscor2(int n, int n2) {
        //Algorithm AS 177.3 Appl. Statist. (1982) Vol.31, No.2
        double eps[] = {0.419885, 0.450536, 0.456936, 0.468488};
        double dl1[] = {0.112063, 0.121770, 0.239299, 0.215159};
        double dl2[] = {0.080122, 0.111348, -0.211867, -0.115049};
        double gam[] = {0.474798, 0.469051, 0.208597, 0.259784};
        double lam[] = {0.282765, 0.304856, 0.407708, 0.414093};
        double bb = -0.283833, d = -0.106136, b1 = 0.5641896;
        double e1, e2, l1;
        int i, k;
        k = (n2 < 3) ? n2 : 3;
        double s[] = new double[n2];
        for (i = 0; i < k; ++i) {
            e1 = (1.0 + i - eps[i]) / (n + gam[i]);
            e2 = Math.pow(e1, lam[i]);
            s[i] = e1 + e2 * (dl1[i] + e2 * dl2[i]) / n - correc(1 + i, n);
        }
        if (n2 != k) {
            for (i = 3; i < n2; ++i) {
                l1 = lam[3] + bb / (1.0 + i + d);
                e1 = (1.0 + i - eps[3]) / (n + gam[3]);
                e2 = Math.pow(e1, l1);
                s[i] = e1 + e2 * (dl1[3] + e2 * dl2[3]) / n - correc(1 + i, n);
            }
        }
        for (i = 0; i < n2; ++i) {
            s[i] = -ppnd16(s[i]);
        }
        return s;
    }

    

    /**
     * <p>
     * Quoted from original Fortran documentation:
     * Calculates correction for tail area of the i-th largest of n
     * order statistics.
     * Translated from Fortran to C and from C to Java.
     * Original code published in Applied Statistics (1982) Vol.31, No.2
     * Algorithm 177.4
     * </p>
     * @param i ith largest ranking
     * @param n Sample size
     * @return The correction for tail area of the i-th largest of n order statistics.
     */  
    public static double correc(int i, int n) {
        //Algorithm AS 177.4 Appl. Statist. (1982) Vol.31, No.2
        double c1[] = {9.5, 28.7, 1.9, 0.0, -7.0, -6.2, -1.6};
        double c2[] = { -6.195e3, -9.569e3, -6.728e3, -17.614e3,
                      -8.278e3, -3.570e3, 1.075e3};
        double c3[] = {9.338e4, 1.7516e5, 4.1040e5, 2.157e6,
                      2.376e6, 2.065e6, 2.065e6};
        double mic = 1.0e-6, c14 = 1.9e-5;
        double an, ret_val;
        ret_val = c14;
        if (i * n == 4) {
            return ret_val;
        }
        ret_val = 0.0;
        if (i < 1 || i > 7) {
            return ret_val;
        } else if (i != 4 && n > 20) {
            return ret_val;
        } else if (i == 4 && n > 40) {
            return ret_val;
        } else {
            an = 1.0 / (double) (n * n);
            ret_val = (c1[i - 1] + an * (c2[i - 1] + an * c3[i - 1])) * mic;
            return ret_val;
        }
    }

    

    /**
     * <p>
     * Obtains an array of weights for calculating Shapiro Wilk statistic
     * Translated from Fortran to C and from C to Java.
     * Original code published in Appl. Statist.  (1982) Vol. 31, No. 2
     * Algorithm AS 181.1
     * </p>
     * @param n The sample size
     * @param n2 The number of order statistics required; must be <= n/2
     * @return The array of weights for calculating Shapiro Wilk statistic
     */  
    public static double[] wcoef(int n, int n2) {
        double a[] = new double[n2];
        // Algorithm AS 181.1   Appl. Statist.  (1982) Vol. 31, No. 2
        double c4[] = {0.6869, 0.1678};
        double c5[] = {0.6647, 0.2412};
        double c6[] = {0.6431, 0.2806, 0.0875};
        double rsqrt2 = 0.70710678;
        double a1star, a1sq, sastar, an;
        int j;

        if (n > 6) {
            a = nscor2(n, n2);
            for (sastar = 0.0, j = 1; j < n2; ++j) {
                sastar += a[j] * a[j];
            }
            sastar *= 8.0;
            an = n;
            if (n <= 20) {
                an--;
            }
            a1sq = Math.exp(Math.log(6.0 * an + 7.0) - Math.log(6.0 * an + 13.0)
                            +
                            0.5 *
                            (1.0 + (an - 2.0) * Math.log(an + 1.0) - (an - 1.0)
                             * Math.log(an + 2.0)));
            a1star = sastar / (1.0 / a1sq - 2.0);
            sastar = Math.sqrt(sastar + 2.0 * a1star);
            a[0] = Math.sqrt(a1star) / sastar;
            for (j = 1; j < n2; ++j) {
                a[j] = 2.0 * a[j] / sastar;
            }
        } else {
            a[0] = rsqrt2;
            if (n != 3) {
                if (n - 3 == 3) {
                    for (j = 0; j < 3; ++j) {
                        a[j] = c6[j];
                    }
                } else if (n - 3 == 2) {
                    for (j = 0; j < 2; ++j) {
                        a[j] = c5[j];
                    }
                } else {
                    for (j = 0; j < 2; ++j) {
                        a[j] = c4[j];
                    }
                }
            }
        }
        return a;
    }

    /**
     * <p>
     * Quoted from the original Fortran documentation:
     * Calculates the algebraic polynomial of order nord-1 with array of
     * coefficients c.  Zero order coefficient is c(1)
     * Translated from Fortran to C and from C to Java.
     * Original code published in Appl. Statist.  (1982) Vol. 31, No. 2
     * Algorithm AS 181.2
     * </p>
     * @param c Vector of coefficients
     * @param nord order of the polinomial + 1
     * @param x x value
     * @return The value of the polinomial
     */  
    public static double poly(double[] c, int nord, double x) {
        //Algorithm AS 181.2   Appl. Statist.  (1982) Vol. 31, No. 2
        double p;
        int n2, i, j;
        if (nord == 1) {
            return c[0];
        }
        p = x * c[nord - 1];
        if (nord != 2) {
            n2 = nord - 2;
            j = n2;
            for (i = 0; i < n2; ++i) {
                p = (p + c[j--]) * x;
            }
        }
        return c[0] + p;
    }
    /**
     * <p>
     * This method computes the statistic and the p-value of Shapiro Wilk test using Royston algorithm.
     * Part of the code was translated from Fortran to C and from C to Java,
     * finally encapsulated in this method calling other 181.x Algorithms.
     * Original code published in Appl. Statist.  (1982) Vol. 31, No. 2
     * Algorithm AS 181
     * </p>
     * @param x Vector with sample values.
     * @return A vector with the statistic ([0] element) and the p-value ([1] element) of the test.
     */  

    //Statistic and pvalue is calculated for one sample
    public static double[] testroyston(double[] x) {
        int n = x.length;
        double eps, mean = 0, ssq = 0;
        int n2 = (int) Math.floor((double) n / 2);
        double[] a = new double[n2];
        double[] xcopy = new double[n];
        for (int ii = 0; ii < n; ++ii) {
            xcopy[ii] = x[ii];
            mean += x[ii];
        }
        mean /= n;
        Arrays.sort(xcopy);
        for (int ii = 0; ii < n; ++ii) {
            ssq += (mean - x[ii]) * (mean - x[ii]);
        }
        a = wcoef(n, n2);
        eps = a[0] * a[0] / (1.0 - 1.0 / (double) n);
        //Algorithm AS 181
        //J.P. Royston, 1982.
        //Applied Statistics 31(2):176-180
        double eu3, lamda, ybar, sdy, al, un, ww, y, z;
        int i, j, n3, nc;
        double wa[] = {0.118898, 0.133414, 0.327907};
        double wb[] = { -0.37542, -0.492145, -1.124332, -0.199422};
        double wc[] = { -3.15805, 0.729399, 3.01855, 1.558776};
        double wd[] = {0.480385, 0.318828, 0.0, -0.0241665, 0.00879701,
                      0.002989646};
        double we[] = { -1.91487, -1.37888, -0.04183209, 0.1066339, -0.03513666,
                      -0.01504614};
        double wf[] = { -3.73538, -1.015807, -0.331885, 0.1773538, -0.01638782,
                      -0.03215018, 0.003852646};
        double unl[] = { -3.8, -3.0, -1.0};
        double unh[] = {8.6, 5.8, 5.4};
        int nc1[] = {5, 5, 5};
        int nc2[] = {3, 4, 5};
        double c[] = new double[5];
        int upper = 1;
        double pi6 = 1.90985932, stqr = 1.04719755;
        double zero = 0.0, tqr = 0.75, one = 1.0;
        double onept4 = 1.4, three = 3.0, five = 5.0;
        double w = 0, pw = 0;
        double c1[][] = { { -1.26233, -2.28135, -3.30623}, {1.87969, 2.26186,
                        2.76287}, {0.0649583, 0.0, -0.83484}, { -0.0475604, 0.0,
                        1.20857}, { -0.0139682, -0.00865763, -0.507590}
        };
        double c2[][] = { { -0.287696, -1.63638, -5.991908}, {1.78953, 5.60924,
                        21.04575}, { -0.180114, -3.63738, -24.58061}, {0.0,
                        1.08439, 13.78661}, {0.0, 0.0, -2.835295}
        };
        double[] res = new double[2];
        i = n - 1;
        for (w = 0.0, j = 0; j < n2; ++j) {
            w += a[j] * (xcopy[i--] - xcopy[j]);
        }
        w *= w / ssq;
        if (w > one) {
            w = one;
            res[0] = w;
            res[1] = pw;
            return res;
        } else if (n > 6) {
            if (n <= 20) {
                al = Math.log((double) n) - three;
                lamda = poly(wa, 3, al);
                ybar = Math.exp(poly(wb, 4, al));
                sdy = Math.exp(poly(wc, 4, al));
            } else {
                al = Math.log((double) n) - five;
                lamda = poly(wd, 6, al);
                ybar = Math.exp(poly(we, 6, al));
                sdy = Math.exp(poly(wf, 7, al));
            }
            y = Math.pow(one - w, lamda);
            z = (y - ybar) / sdy;
            pw = alnorm(z, upper);
            res[0] = w;
            res[1] = pw;
            return res;
        }
        //Else will not ocurr in Keel. Minimun will be for 5x2cv
        else {
            if (w >= eps) {
                ww = w;
                if (w >= eps) {
                    ww = w;
                    if (n == 3) {
                        pw = pi6 *
                             (Math.atan(Math.sqrt(ww / (one - ww))) - stqr);
                        res[0] = w;
                        res[1] = pw;
                        return res;
                    }
                    un = Math.log((w - eps) / (one - w));
                    n3 = n - 3;
                    if (un >= unl[n3 - 1]) {
                        if (un <= onept4) {
                            nc = nc1[n3 - 1];
                            for (i = 0; i < nc; ++i) {
                                c[i] = c1[i][n3 - 1];
                            }
                            eu3 = Math.exp(poly(c, nc, un));
                        } else {
                            if (un > unh[n3 - 1]) {
                                res[0] = w;
                            }
                            res[1] = pw;
                            return res;
                        }
                        ww = (eu3 + tqr) / (one + eu3);
                        pw = pi6 *
                             (Math.atan(Math.sqrt(ww / (one - ww))) - stqr);
                        res[0] = w;
                        res[1] = pw;
                        return res;
                    }
                }
            }
            pw = zero;
            res[0] = w;
            res[1] = pw;
            return res;
        }
    }
    /**
     * <p>
     * Computes the p-value of  Shapiro Wilk statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param p Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */    
    public static double[] testsw(double[][][] err, double significance,
                                  PrintStream p) {
        //The result is a vector of pvalues from two algorithms for each dataset
        double[] res = new double[err[0][0].length * 2];
        //For each algorithm
        for (int alg = 0; alg < 2; alg++) {
            //For each output
            for (int out = 0; out < err[0][0].length; out++) {
                double[] outErr = new double[err[0].length];
                //Algorithm error is copied to errsal, for one output
                for (int i = 0; i < outErr.length; i++) {
                    outErr[i] = err[alg][i][out];
                }
                //It's calculated Pvalue asociated with one algorithm error, for one output
                double[] pvsw = new double[2];
                pvsw = testroyston(outErr);
                res[out + alg * err[0][0].length] = pvsw[1];
                p.println("Null hypothesis, error distribution is normal");
                if (pvsw[1] < 1 - significance) {
                    p.println("Output=" + out +
                              ": There is evidence against H0 for algorithm: " +
                              alg + " output: " + out);
                } else {
                    p.println("Output=" + out +
                              ": There is no evidence against H0 for algorithm: " +
                              alg + " output: " + out);
                }
            }
        }
        return res;
    }

    /**
     * <p>
     * Computes the p-value of  5x2cv-f Alpaydin statistical test for a set of samples obtained from
     * two algorithms and an arbitrary number of datasets 
     * </p>
     * @param err A cubic matrix with the samples values indexed by algorithm, fold and dataset
     * @param significance 1-level of the test
     * @param PrintStream Output stream for tracing purposes
     * @return A vector of p-values, one for each dataset
     */ 
    private double[] test5x2cvf(double[][][] err, double significance,
                                PrintStream p) {
        // This algorithm calculates the p-value obtanied contrasting two differents algoritms
        // using 5x2cv-f test
        // First index is the algorithm
        // Second index is the iteration
        // Third index is the output
        // The calculus is repeated for each output, returning a vector of p-values
        double[] result = new double[err[0][0].length];
        for (int out = 0; out < result.length; out++) {
            //Statistic numerator
            //10 error measures
            double num = 0;
            for (int iter = 0; iter < 10; iter++) {
                num += (err[0][iter][out] - err[1][iter][out]) *
                        (err[0][iter][out] - err[1][iter][out]);
            }
            //Statistic denominator
            double[] s2 = new double[5];
            float mlq = 0;
            for (int i = 0; i < 5; i++) {
                double tmp, tmp1, tmp2;
                tmp = (err[0][2 * i][out] - err[1][2 * i][out] + err[0][2 * i +
                       1][out] - err[1][2 * i + 1][out]) / 2;
                tmp1 = err[0][2 * i][out] - err[1][2 * i][out] - tmp;
                tmp2 = err[0][2 * i + 1][out] - err[1][2 * i + 1][out] - tmp;
                s2[i] = tmp1 * tmp1 + tmp2 * tmp2;
                mlq += s2[i];
            }
            //statistic
            double estatistic = num / 2 / mlq;
            //pvalues
            result[out] = (1 - pf(estatistic, 10, 5));
            //Contrast Hypothesis
            //H0: equal means
            //H1: different means
            //if  resul[s] is > confianza, don't reject H0
            p.println("Null hypothesis, true difference in means is equal to 0");
            if (result[out] < 1 - significance) {
                p.println("Output=" + out + ": There is evidence against H0");
            } else {
                p.println("Output=" + out + ": There is no evidence against H0");
            }
        }
        return result;
    }

    
    /**
     * <p>
     * Computes natural logarithm of the gamma function.
     *  Based on the code in "Numerical Recipes in C"
     * </p>
     * @param c The argument of the gamma function
     * @return The value of the natural logarithm of the gamma function.
     */ 
    public static double lnfgamma(double c) {
        int j;
        double x, y, tmp, ser;
        double[] cof = {76.18009172947146, -86.50532032941677,
                       24.01409824083091, -1.231739572450155,
                       0.1208650973866179e-2, -0.5395239384953e-5};
        y = x = c;
        tmp = x + 5.5 - (x + 0.5) * Math.log(x + 5.5);
        ser = 1.000000000190015;
        for (j = 0; j <= 5; j++) {
            ser += (cof[j] / ++y);
        }
        return (Math.log(2.5066282746310005 * ser / x) - tmp);
    }

    /**
     * <p>
     * Computes natural logarithm of the beta function.
     * </p>
     * @param a The first argument of the beta function
     * @param b The second argument of the beta function
     * @return The value of the natural logarithm of the beta function.
     */ 
    public static double lnfbeta(double a, double b) {
        return (lnfgamma(a) + lnfgamma(b) - lnfgamma(a + b));
    }

    /**
     * <p>
     * Quoted from original Fortran documentation:
     * Computes incomplete beta function ratio for arguments
     * x between zero and one, p and q positive.
     * log of complete beta function, beta, is assumed to be known
     * Original code published in Applied Statistics Vol32, No.1
     * Algorithm AS 63 
     * </p>
     * @param x x value
     * @param p The first argument of the beta function
     * @param q The second argument of the beta function
     * @return The value of the incomplete beta function ratio for x with p and q arguments.
     */ 
    public static double betainv(double x, double p, double q) { 
        double beta = lnfbeta(p, q), acu = 1E-14;
        double cx, psq, pp, qq, x2, term, ai, betain, ns, rx, temp;
        boolean indx;
        if (p <= 0 || q <= 0) {
            return ( -1.0);
        }
        if (x <= 0 || x >= 1) {
            return ( -1.0);
        }
        psq = p + q;
        cx = 1 - x;
        if (p < psq * x) {
            x2 = cx;
            cx = x;
            pp = q;
            qq = p;
            indx = true;
        } else {
            x2 = x;
            pp = p;
            qq = q;
            indx = false;
        }
        term = 1;
        ai = 1;
        betain = 1;
        ns = qq + cx * psq;
        rx = x2 / cx;
        temp = qq - ai;
        if (ns == 0) {
            rx = x2;
        } while (temp > acu && temp > acu * betain) {
            term = term * temp * rx / (pp + ai);
            betain = betain + term;
            temp = Math.abs(term);
            if (temp > acu && temp > acu * betain) {
                ai++;
                ns--;
                if (ns >= 0) {
                    temp = qq - ai;
                    if (ns == 0) {
                        rx = x2;
                    }
                } else {
                    temp = psq;
                    psq += 1;
                }
            }
        }
        betain *= Math.exp(pp * Math.log(x2) + (qq - 1) * Math.log(cx) - beta) /
                pp;
        if (indx) {
            betain = 1 - betain;
        }
        return (betain);
    }

    /**
     * <p>
     * Computes cumulative Snedecor F distribution 
     * </p>
     * @param x x value
     * @param df1 Numerator degrees of freedom
     * @param df2 Denominator degrees of freedom
     * @return The value of the cumulative Snedecor F(df1, df2) distribution for x
     */     
    public static double pf(double x, double df1, double df2) {
    	return (betainv(df1 * x / (df1 * x + df2), 0.5 * df1, 0.5 * df2));
    }

    
    /**
     * <p>
     * Computes cumulative N(0,1) distribution. Based om Algorithm AS66 Applied Statistics (1973) vol22 no.3
     * </p>
     * @param z x value
     * @param upper A boolean value, if true the integral is evaluated from z to infinity, from minus infinity to z otherwise
     * @return The value of the cumulative N(0,1) distribution for z
     */  
    public static double pnorm(double z, boolean upper) {
        //Algorithm AS 66: "The Normal Integral"
        //Applied Statistics
        double ltone = 7.0,
                       utzero = 18.66,
                                con = 1.28,
                                      a1 = 0.398942280444,
                                           a2 = 0.399903438504,
                                                a3 = 5.75885480458,
                a4 = 29.8213557808,
                     a5 = 2.62433121679,
                          a6 = 48.6959930692,
                               a7 = 5.92885724438,
                                    b1 = 0.398942280385,
                                         b2 = 3.8052e-8,
                                              b3 = 1.00000615302,
                b4 = 3.98064794e-4,
                     b5 = 1.986153813664,
                          b6 = 0.151679116635,
                               b7 = 5.29330324926,
                                    b8 = 4.8385912808,
                                         b9 = 15.1508972451,
                                              b10 = 0.742380924027,
                b11 = 30.789933034,
                      b12 = 3.99019417011;
        double y, alnorm;
        if (z < 0) {
            upper = !upper;
            z = -z;
        }
        if (z <= ltone || upper && z <= utzero) {
            y = 0.5 * z * z;
            if (z > con) {
                alnorm = b1 * Math.exp( -y) /
                         (z - b2 +
                          b3 /
                          (z + b4 +
                           b5 /
                           (z - b6 +
                            b7 / (z + b8 - b9 / (z + b10 + b11 / (z + b12))))));
            } else {
                alnorm = 0.5 -
                         z *
                         (a1 - a2 * y / (y + a3 - a4 / (y + a5 + a6 / (y + a7))));
            }
        } else {
            alnorm = 0;
        }
        if (!upper) {
            alnorm = 1 - alnorm;
        }
        return (alnorm);
    }

    
    /**
     * <p>
     * Computes cumulative N(mu,sigma) distribution. 
     * </p>
     * @param x x value
     * @param upper A boolean value, if true the integral is evaluated from z to infinity, from minus infinity to z otherwise
     * @param mu The mean of the distribution
     * @param sigma2 The variance of the distribution
     * @return The value of the cumulative N(mu,sigma) distribution for x
     */      
    public static double pnorm(double x, boolean upper, double mu,
                               double sigma2) {
        return (pnorm((x - mu) / Math.sqrt(sigma2), upper));
    }

    
    /**
     * <p>
     * Computes the RMS of the data in a cubic matrix with samples values indexed 
     * by algorithm, fold and dataset, when two algorithms are compared.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */        
    private void doRMS(double[][][] d) {
        nFolds = d.length / nAlgorithms;
        int nds = 0, np = 0;
        nOutputs = d[0][0].length / 2;
        differences = new double[nAlgorithms][nFolds][nOutputs];
        totals = new double[nAlgorithms][nFolds][nOutputs];

        for (int i = 0; i < differences.length; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] = 0;
                }
            }
        }
        np = 0;
        nds = 0;
        for (int i = 0; i < d.length; i++) {
            if (i > ((np + 1) * (nFolds)) - 1) {
                np++;
            }
            nds = i - np * nFolds;
            for (int j = 0; j < d[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    double err = d[i][j][k] - d[i][j][k + nOutputs];
                    differences[np][nds][k] += err * err;
                    totals[np][nds][k]++;
                }
            }
        }

        for (int i = 0; i < nAlgorithms; i++) {
            for (int j = 0; j < nFolds; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] /= totals[i][j][k];
                }
            }
        }
    }
    /**
     * <p>
     * Computes the RMS of the data in a cubic matrix with samples values indexed
     *  by algorithm, fold and dataset, when more than two algorithms are compared.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */   
    private void doRMS2(double[][][][] d) {

        nOutputs = d[0][0][0].length / 2; //All the algorithms solve the same problem

        differences = new double[nAlgorithms][][];
        totals = new double[nAlgorithms][][];
        nResults = new int[nAlgorithms];

        for (int i = 0; i < d.length; i++) { //for each algorithm
            differences[i] = new double[d[i].length][];
            totals[i] = new double[d[i].length][];
            nResults[i] = d[i].length;
            for (int j = 0; j < d[i].length; j++) { //for each fold 
                differences[i][j] = new double[nOutputs];
                totals[i][j] = new double[nOutputs];
                for (int h = 0; h < nOutputs; h++) { //for both outputs of each result
                    differences[i][j][h] = 0;
                    totals[i][j][h] = 0;
                }
            }
        }

        for (int i = 0; i < d.length; i++) { //for each algorithm
            for (int j = 0; j < d[i].length; j++) { //for each fold 
                for (int k = 0; k < d[i][j].length; k++) { //for all the results in the file
                    for (int h = 0; h < nOutputs; h++) { //for both outputs of each result
                        double err = d[i][j][k][h] - d[i][j][k][h + nOutputs];
                        differences[i][j][h] += err * err;
                        totals[i][j][h]++;
                    }
                }
            }
        }
        
        for (int i = 0; i < nAlgorithms; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < differences[i][j].length; k++) {
                    differences[i][j][k] /= totals[i][j][k];
                }
            }
        }
    }

    /**
     * <p>
     * Computes the classification error of the data in a cubic matrix with samples values indexed
     *  by algorithm, fold and dataset, when two algorithms are compared.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */   
    private void doErrClass(double[][][] d) {
        // d.length is not odd
        // from d[0][][] to d[d.length/2-1] is first algorithm
        // from d[d.length/2][][] to d[d.length-1] is first algorithm
        // nFolds is the number of iterations for each algorithm
        // nOutputs is the number of outputs for each algorithm (1)
        nFolds = d.length / nAlgorithms;
        int nds = 0, np = 0;
        nOutputs = d[0][0].length / 2;
        differences = new double[nAlgorithms][nFolds][nOutputs];
        totals = new double[nAlgorithms][nFolds][nOutputs];

        for (int i = 0; i < differences.length; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] = 0;
                    totals[i][j][k] = 0;
                }
            }
        }

        np = 0;
        nds = 0;
        for (int i = 0; i < d.length; i++) {
            if (i > ((np + 1) * (nFolds)) - 1) {
                np++;
            }
            nds = i - np * nFolds;
            for (int j = 0; j < d[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    if (d[i][j][k] != d[i][j][k + nOutputs]) {
                        differences[np][nds][k]++;
                    }
                    totals[np][nds][k]++;
                }
            }
        }

        for (int i = 0; i < nAlgorithms; i++) {
            for (int j = 0; j < nFolds; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] /= totals[i][j][k];
                }
            }
        }
    }
    
    /**
     * <p>
     * Computes the classification error of the data in a cubic matrix with samples values indexed
     *  by algorithm, fold and dataset, when more than two algorithms are compared.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */   
    private void doErrClass2(double[][][][] d) {

        nOutputs = d[0][0][0].length / 2; //All the algorithms solve the same problem
        differences = new double[nAlgorithms][][];
        totals = new double[nAlgorithms][][];
        nResults = new int[nAlgorithms];

        for (int i = 0; i < d.length; i++) { //for each algorithm
            differences[i] = new double[d[i].length][];
            totals[i] = new double[d[i].length][];
            nResults[i] = d[i].length;
            for (int j = 0; j < d[i].length; j++) { //for each fold
                differences[i][j] = new double[nOutputs];
                totals[i][j] = new double[nOutputs];
                for (int h = 0; h < nOutputs; h++) { //for both outputs of each algorithm
                    differences[i][j][h] = 0;
                    totals[i][j][h] = 0;
                }
            }
        }
        for (int i = 0; i < d.length; i++) { //for each algorithm
            for (int j = 0; j < d[i].length; j++) { //for each fold
                for (int k = 0; k < d[i][j].length; k++) { //for all the results in the file
                    for (int h = 0; h < nOutputs; h++) { //for both outputs of each algorithm
                        if (d[i][j][k][h] != d[i][j][k][h + nOutputs]) {
                            differences[i][j][h]++;
                        }
                        totals[i][j][h]++;
                    }
                }
            }
        }
        for (int i = 0; i < nAlgorithms; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < differences[i][j].length; k++) {
                    differences[i][j][k] /= totals[i][j][k];
                }
            }
        }
    }
    
    /**
     * <p>
     * Computes the RMS of the data in a cubic matrix with samples values indexed
     *  by algorithm, fold and dataset.
     *  This method is needed when an output summary module is selected.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */   
    private void rmsSummary(double[][][] d) {
        nFolds = d.length;
        int nds = 0, np = 0;
        nOutputs = d[0][0].length / 2;
        differences = new double[1][nFolds][nOutputs];
        totals = new double[1][nFolds][nOutputs];

        for (int i = 0; i < differences.length; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] = 0;
                }
            }
        }

        for (int i = 0; i < d.length; i++) {
            if (i < nFolds) {
                nds = i;
                np = 0;
            } else {
                nds = i - nFolds;
                np = 1;
            }

            for (int j = 0; j < d[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    double err = d[i][j][k] - d[i][j][k + nOutputs];
                    differences[np][nds][k] += err * err;
                    totals[np][nds][k]++;
                }
            }
        }

        for (int j = 0; j < nFolds; j++) {
            for (int k = 0; k < nOutputs; k++) {
                differences[0][j][k] /= totals[0][j][k];
            }
        }

    }
    /**
     * <p>
     * Computes the classification error of the data in a cubic matrix with samples values indexed
     *  by algorithm, fold and dataset.
     *  This method is needed when an output summary module is selected.
     * </p>
     * @param d The cubic matrix
     * @return Nothing, some local variables of this module are modified instead
     */ 
    private void classSummary(double[][][] d) {
        nFolds = d.length;
        int nds = 0, np = 0;
        nOutputs = d[0][0].length / 2;
        differences = new double[1][nFolds][nOutputs];
        unclassified = new double[1][nFolds][nOutputs];
        totals = new double[1][nFolds][nOutputs];
        for (int i = 0; i < differences.length; i++) {
            for (int j = 0; j < differences[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    differences[i][j][k] = 0;
                    unclassified[i][j][k] = 0;
                    totals[i][j][k] = 0;
                }
            }
        }
        for (int i = 0; i < d.length; i++) {
            if (i < nFolds) {
                nds = i;
                np = 0;
            } else {
                nds = i - nFolds;
                np = 1;
            }
            for (int j = 0; j < d[i].length; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    if (d[i][j][k] != d[i][j][k + nOutputs]) {
                        differences[np][nds][k]++;
                    }
                    if (d[i][j][k] == -1 || d[i][j][k + nOutputs] == -1) {
                        unclassified[np][nds][k]++;
                    }
                    totals[np][nds][k]++;
                }
            }
        }

        for (int j = 0; j < nFolds; j++) {
            for (int k = 0; k < nOutputs; k++) {
                differences[0][j][k] /= totals[0][j][k];
                unclassified[0][j][k] /= totals[0][j][k];
            }
        }
    }
    /**
     * <p>
     * This method calls the selected statistical test or output module.
     * </p>
     * @param selector An int that selects the statistical test or module to be applied. The relationship 
     * value / statistical test or output module is done via the public final static variables defined at
     * the beginning of this class
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param dtrain Train data
     * @param significance 1-level of the statistical test
     * @param nres Output file name
     * @param nameRel Algorithms names
     * @param nameResults Results names
     * @param labels Class labels
     */ 
    public StatTest(int selector, double[][][][] d, double[][][][] dtrain,
                    double significance, String nres, String nameRel,
                    Vector nameResults, String[] labels) {

        FileOutputStream out, out1, out2, out3, out4, gout;
        PrintStream p, p1, p2, p3, p4;
        especificSymbol.setDecimalSeparator('.');
        gbcf.setDecimalFormatSymbols(especificSymbol);
        dfTable1.setDecimalFormatSymbols(especificSymbol);
        dfPvalue.setDecimalFormatSymbols(especificSymbol);

        try {
            double[] alpha;
            double[] gERR;
            double[] gUNC;

            String[] aux;
            String[] algorithm;
            int pos, count;
            ArrayList<String> algs = new ArrayList<String>();

            switch (selector) {
            case StatTest.Dietterich5x2cvR:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("5x2cv Test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = test5x2cv(differences, significance, p);
                p.println("p-valores:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.Dietterich5x2cvC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("5x2cv Test, Classification");
                doErrClass(d[0]);
                p.println("Classification error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }
                alpha = test5x2cv(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.tR:

                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("t test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testt(differences, significance, p);
                p.println("p-valores:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.tC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("t test, Classification");
                doErrClass(d[0]);
                p.println("Classificacion error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testt(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }
                p.close();
                break;
            case StatTest.globalWilcoxonI:
            	//testrs (Wilcoxon for two samples) is done for one datase
                out = new FileOutputStream(nres);
                p = new PrintStream(out);
                
                aux = null;
                aux = nres.split("/");

                String algAuxIW = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAuxIW += aux[3].charAt(pos);
                    }
                    algs.add(algAuxIW);
                    pos += 2;
                    algAuxIW = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = 2;
                mean = new double[nAlgorithms];
                mean[0] = mean[1] = 0;
                nResults = new int[nAlgorithms];

                // Compute the imbalanced measure
                int [][] confusion_matrix_wilcoxon;
                double [][] imbalanced_measure_wx = new double[nAlgorithms][];

                nOutputs = d[0][0][0].length/2;
                for (int a=0; a<nAlgorithms; a++) {
                    imbalanced_measure_wx[a] = new double [d[a].length];
                    nResults[a] = d[a].length;
                    confusion_matrix_wilcoxon = confusion_matrix (d[a]);
                    for (int j = 0; j < nResults[a]; j++) {
                        int TP_wx = confusion_matrix_wilcoxon[j][0];
                        int FN_wx = confusion_matrix_wilcoxon[j][1];
                        int FP_wx = confusion_matrix_wilcoxon[j][2];
                        int TN_wx = confusion_matrix_wilcoxon[j][3];

                        int total_instances_wx = TP_wx + FN_wx + FP_wx + TN_wx;
                        double measure_wx = 0;

                        if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                            double TPrate_wx = (double)TP_wx/(double)(TP_wx+FN_wx);
                            double FPrate_wx = (double)FP_wx/(double)(FP_wx+TN_wx);
                            
                            if (((TP_wx+FN_wx) == 0) || ((FP_wx+TN_wx) == 0)) {
                            	measure_wx = 0;
                          	}
                          	else {
                          		measure_wx = (double)(1+TPrate_wx-FPrate_wx)/2.0;
                          	}
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                            measure_wx = Math.sqrt(((double)TP_wx/(double)(TP_wx+FN_wx)) * ((double)TN_wx/(double)(FP_wx+TN_wx)));
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                            measure_wx = (double)(TP_wx+TN_wx)/(double)total_instances_wx;
                        }
                        imbalanced_measure_wx[a][j] = measure_wx;
                    }
                }

                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += imbalanced_measure_wx[j][i];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Wilcoxon signed rank test, Imbalanced Classification");
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("Area Under the ROC Curve in each fold:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("Geometric Mean in each fold:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("Classification Accuracy in each fold:");
                }
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        p.print(imbalanced_measure_wx[i][j]);
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                //testrs (Wilcoxon for two samples) is called
                if (nResults[0] == nResults[1]) {
                    alpha = testrsImbMeasure(imbalanced_measure_wx, significance, p); //test for equality

                    p.println("p-values:");
                    for (int i = 0; i < alpha.length; i++) {
                        p.print(alpha[i] + " ");
                    }
                }
                p.close();

                // Global Wilcoxon (for more than two samples) is called
                wilcoxonGlobal(nres, algorithm, StatTest.globalWilcoxonI);
                break;
            case StatTest.FriedmanI:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                String algAuxI = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAuxI += aux[3].charAt(pos);
                    }
                    algs.add(algAuxI);
                    pos += 2;
                    algAuxI = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                mean = new double[nAlgorithms];
                nResults = new int[nAlgorithms];

                // Compute the imbalanced measure
                int [][] confusion_matrix_friedman;
                double [][] imbalanced_measure_fr = new double[nAlgorithms][];

                nOutputs = d[0][0][0].length/2;
                for (int a=0; a<nAlgorithms; a++) {
                    mean[a] = 0;
                    imbalanced_measure_fr[a] = new double [d[a].length];
                    nResults[a] = d[a].length;
                    confusion_matrix_friedman = confusion_matrix (d[a]);
                    for (int j = 0; j < nResults[a]; j++) {
                        int TP_fr = confusion_matrix_friedman[j][0];
                        int FN_fr = confusion_matrix_friedman[j][1];
                        int FP_fr = confusion_matrix_friedman[j][2];
                        int TN_fr = confusion_matrix_friedman[j][3];

                        int total_instances_fr = TP_fr + FN_fr + FP_fr + TN_fr;
                        double measure_fr = 0;

                        if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                            double TPrate_fr = (double)TP_fr/(double)(TP_fr+FN_fr);
                            double FPrate_fr = (double)FP_fr/(double)(FP_fr+TN_fr);
                            
                            if (((TP_fr+FN_fr) == 0) || ((FP_fr+TN_fr) == 0)) {
                            	measure_fr = 0;
                          	}
                          	else {
                          		measure_fr = (double)(1+TPrate_fr-FPrate_fr)/2.0;
                          	}
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                            measure_fr = Math.sqrt(((double)TP_fr/(double)(TP_fr+FN_fr)) * ((double)TN_fr/(double)(FP_fr+TN_fr)));
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                            measure_fr = (double)(TP_fr+TN_fr)/(double)total_instances_fr;
                        }
                        imbalanced_measure_fr[a][j] = measure_fr;
                    }
                }

                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += imbalanced_measure_fr[j][i];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Friedman Test, Imbalanced Classification");
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("Area Under the ROC Curve in each fold:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("Geometric Mean in each fold:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("Classification Accuracy in each fold:");
                }
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        p.print(imbalanced_measure_fr[i][j]);
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres, StatTest.FriedmanI);
                break;
            case StatTest.ShapiroWilkR:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("Shapiro-Wilk test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testsw(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.ShapiroWilkC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("Shapiro-Wilk test, Classification");
                doErrClass(d[0]);
                p.println("Classification error in each cada fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testsw(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.WilcoxonR:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("Wilcoxon signed rank test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testrs(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.WilcoxonC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("Wilcoxon signed rank test, Classification");
                doErrClass(d[0]);
                p.println("Classification error in each foldfold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testrs(differences, significance, p);
                p.println("p-valores:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.globalWilcoxonR:

                //testrs (Wilcoxon for two samples) is done for one datase 
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                doRMS2(d);

                aux = null;
                aux = nres.split("/");

                algorithm = new String[2];
                algorithm[0] = new String("" + aux[3].charAt(3)); //After TST

                for (pos = 4;
                           (aux[3].charAt(pos) != 'v') ||
                           (aux[3].charAt(pos + 1) != 's');
                           pos++) { //until  "vs" is not found
                    algorithm[0] += aux[3].charAt(pos);
                }

                algorithm[1] = aux[3].substring(pos + 2, aux[3].length());

                mean = new double[2];
                mean[0] = mean[1] = 0;

                for (int i = 0; i < 2; i++) { //for both algorithms
                    for (int j = 0; j < nResults[i]; j++) {
                        mean[i] += differences[i][j][0];
                    }
                }
                mean[0] /= nResults[0];
                mean[1] /= nResults[1];

                p.println("Wilcoxon signed rank test, Regression");
                p.println("Regression error in each foldfold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                //testrs (Wilcoxon for two samples) is called
                if (nResults[0] == nResults[1]) {
                    alpha = testrs(differences, significance, p);
                    p.println("p-valores:");
                    for (int i = 0; i < alpha.length; i++) {
                        p.print(alpha[i] + " ");
                    }
                }
                p.close();

                //Global Wilcoxon (for more than two samples) is called
                wilcoxonGlobal(nres, algorithm, StatTest.globalWilcoxonR);

                break;

            case StatTest.globalWilcoxonC:

            	//testrs (Wilcoxon for two samples) is done for one datase
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                doErrClass2(d);

                aux = null;
                aux = nres.split("/");

                algorithm = new String[2];
                algorithm[0] = new String("" + aux[3].charAt(3)); //After TST

                for (pos = 4;
                           (aux[3].charAt(pos) != 'v') ||
                           (aux[3].charAt(pos + 1) != 's');
                           pos++) { //Until "vs" is not found
                    algorithm[0] += aux[3].charAt(pos);
                }

                algorithm[1] = aux[3].substring(pos + 2, aux[3].length());

                mean = new double[2];
                mean[0] = mean[1] = 0;

                for (int i = 0; i < 2; i++) { //para los dos algoritmos
                    for (int j = 0; j < nResults[i]; j++) {
                        mean[i] += differences[i][j][0];
                    }
                }
                mean[0] /= nResults[0];
                mean[1] /= nResults[1];

                p.println("Wilcoxon signed rank test, Classification");
                p.println("Classification error in each foldfold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                //testrs (Wilcoxon for two samples) is called
                if (nResults[0] == nResults[1]) {
                    alpha = testrs(differences, significance, p); //test for equality

                    p.println("p-valores:");
                    for (int i = 0; i < alpha.length; i++) {
                        p.print(alpha[i] + " ");
                    }
                }
                p.close();

                // Global Wilcoxon (for more than two samples) is called
                wilcoxonGlobal(nres, algorithm, StatTest.globalWilcoxonC);

                break;

            case StatTest.FriedmanR:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                //The number of algorithms is unknown
                aux = null;
                aux = nres.split("/");

                String algAux = new String("" + aux[3].charAt(3)); //After TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;

                doRMS2(d);

                mean = new double[nAlgorithms];
                for (int i = 0; i < mean.length; i++) {
                    for (int j = 0; j < nResults[i]; j++) {
                        mean[i] += differences[i][j][0];
                    }
                    mean[i] /= nResults[i];
                }

                p.println("Friedman Test, Regression");
                p.println("Regression error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
               //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.FriedmanR); 
                break;

            case StatTest.FriedmanC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                doErrClass2(d);

                mean = new double[nAlgorithms];
                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += differences[j][i][0];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Friedman Test, Classification");
                p.println("Classification error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.FriedmanC);
                break;
            
            case StatTest.FriedmanAlignedR:
            	
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                //The number of algorithms is unknown
                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //After TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;

                doRMS2(d);

                mean = new double[nAlgorithms];
                for (int i = 0; i < mean.length; i++) {
                    for (int j = 0; j < nResults[i]; j++) {
                        mean[i] += differences[i][j][0];
                    }
                    mean[i] /= nResults[i];
                }

                p.println("Friedman Test, Regression");
                p.println("Regression error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
               //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.FriedmanAlignedR); 
            break;

            case StatTest.FriedmanAlignedC:
            	
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                doErrClass2(d);

                mean = new double[nAlgorithms];
                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += differences[j][i][0];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Friedman Test, Classification");
                p.println("Classification error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.FriedmanAlignedC); 
            break;
            
            case StatTest.QuadeR:
            	
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                //The number of algorithms is unknown
                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //After TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;

                doRMS2(d);

                mean = new double[nAlgorithms];
                for (int i = 0; i < mean.length; i++) {
                    for (int j = 0; j < nResults[i]; j++) {
                        mean[i] += differences[i][j][0];
                    }
                    mean[i] /= nResults[i];
                }

                p.println("Friedman Test, Regression");
                p.println("Regression error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
               //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.QuadeR); 
                break;

            case StatTest.QuadeC:
            	
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                doErrClass2(d);

                mean = new double[nAlgorithms];
                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += differences[j][i][0];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Friedman Test, Classification");
                p.println("Classification error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                stat = new Friedman();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                stat.runPostHoc(significance, nResults, algorithm, nres,StatTest.QuadeC); 
            break;
                
            case StatTest.MultipleC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                doErrClass2(d);

                mean = new double[nAlgorithms];
                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += differences[j][i][0];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Multiple Test, Classification");
                p.println("Classification error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                Multiple multipleStat = new Multiple();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                multipleStat.runPostHoc(significance, nResults, algorithm, nres);
                break;
             
            case StatTest.MultipleR:
            	
            	 out = new FileOutputStream(nres);
                 p = new PrintStream(out);

                 //The number of algorithms is unknown
                 aux = null;
                 aux = nres.split("/");

                 algAux = new String("" + aux[3].charAt(3)); //After TST

                 pos = 4;
                 do {

                     for (;
                          (pos < aux[3].length()) &&
                          ((aux[3].charAt(pos) != 'v') ||
                           (aux[3].charAt(pos + 1) != 's'));
                          pos++) { //until "vs" is not found
                         algAux += aux[3].charAt(pos);
                     }
                     algs.add(algAux);
                     pos += 2;
                     algAux = new String("");
                 } while (pos < aux[3].length());

                 algorithm = new String[algs.size()];

                 for (int i = 0; i < algs.size(); i++) {
                     algorithm[i] = (String) algs.get(i);
                 }

                 nAlgorithms = algorithm.length;
                 
                 doRMS2(d);
                 
                 mean = new double[nAlgorithms];

                 for (int i = 0; i < mean.length; i++) {
                     for (int j = 0; j < nResults[i]; j++) {
                         mean[i] += differences[i][j][0];
                     }
                     mean[i] /= nResults[i];
                 }

                 p.println("Multiple test, Regression");
                 p.println("Regression error in each fold:");
                 for (int i = 0; i < nAlgorithms; i++) {
                     p.println("Algorithm = " + algorithm[i]);
                     for (int j = 0; j < nResults[i]; j++) {
                         p.print("Fold " + j + " : ");
                         for (int k = 0; k < nOutputs; k++) {
                             p.print(differences[i][j][k] + " ");
                         }
                         p.println();
                     }
                     p.println("Mean Value: " + mean[i]);
                 }


                Multiple multipleRStat = new Multiple();
                //Workaround: in this context, significance is a code identify which post hoc test will be done
                multipleRStat.runPostHoc(significance, nResults, algorithm, nres);
                
            break;
                
            case StatTest.ContrastC:
            	
            	Contrast module= new Contrast();
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                aux = null;
                aux = nres.split("/");

                algAux = new String("" + aux[3].charAt(3)); //Despues de TST

                pos = 4;
                do {
                    for (;
                         (pos < aux[3].length()) &&
                         ((aux[3].charAt(pos) != 'v') ||
                          (aux[3].charAt(pos + 1) != 's'));
                         pos++) { //until "vs" is not found
                        algAux += aux[3].charAt(pos);
                    }
                    algs.add(algAux);
                    pos += 2;
                    algAux = new String("");
                } while (pos < aux[3].length());

                algorithm = new String[algs.size()];
                for (int i = 0; i < algs.size(); i++) {
                    algorithm[i] = (String) algs.get(i);
                }

                nAlgorithms = algorithm.length;
                doErrClass2(d);

                mean = new double[nAlgorithms];
                for (int j = 0; j < nAlgorithms; j++) {
                    for (int i = 0; i < nResults[j]; i++) {
                        mean[j] += differences[j][i][0];
                    }
                    mean[j] /= nResults[j];
                }

                p.println("Contrast Estimation, Classification");
                p.println("Classification error in each fold:");
                for (int i = 0; i < nAlgorithms; i++) {
                    p.println("Algorithm = " + algorithm[i]);
                    for (int j = 0; j < nResults[i]; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                    p.println("Mean Value: " + mean[i]);
                }

                module.compute(nResults, algorithm, nres);

                break;

            case StatTest.ContrastR:

            	Contrast moduleR= new Contrast();

            	 out = new FileOutputStream(nres);
                 p = new PrintStream(out);

                 //The number of algorithms is unknown
                 aux = null;
                 aux = nres.split("/");

                 algAux = new String("" + aux[3].charAt(3)); //After TST

                 pos = 4;
                 do {

                     for (;
                          (pos < aux[3].length()) &&
                          ((aux[3].charAt(pos) != 'v') ||
                           (aux[3].charAt(pos + 1) != 's'));
                          pos++) { //until "vs" is not found
                         algAux += aux[3].charAt(pos);
                     }

                     algs.add(algAux);
                     pos += 2;
                     algAux = new String("");
                 } while (pos < aux[3].length());

                 algorithm = new String[algs.size()];

                 for (int i = 0; i < algs.size(); i++) {
                     algorithm[i] = (String) algs.get(i);
                 }

                 nAlgorithms = algorithm.length;
                 
                 doRMS2(d);
                 
                 mean = new double[nAlgorithms];

                 for (int i = 0; i < mean.length; i++) {
                     for (int j = 0; j < nResults[i]; j++) {
                         mean[i] += differences[i][j][0];
                     }
                     mean[i] /= nResults[i];
                 }

                 p.println("Contrast Estimation, Regression");
                 p.println("Regression error in each fold:");
                 for (int i = 0; i < nAlgorithms; i++) {
                     p.println("Algorithm = " + algorithm[i]);
                     for (int j = 0; j < nResults[i]; j++) {
                         p.print("Fold " + j + " : ");
                         for (int k = 0; k < nOutputs; k++) {
                             p.print(differences[i][j][k] + " ");
                         }
                         p.println();
                     }
                     p.println("Mean Value: " + mean[i]);
                 }

                moduleR.compute(nResults, algorithm, nres);
                break;
                
            case StatTest.MannWhitneyR:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("Mann-Whitney test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testu(differences, significance, p);
                p.println("p-valores:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.MannWhitneyC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("Mann-Whitney test, Classification");
                doErrClass(d[0]);
                p.println("Classification error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testu(differences, significance, p);
                p.println("p-valores:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.fM:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                // Half of file belongs to the first problem,
                // the rest to the second
                p.println("F test, Modeling");
                doRMS(d[0]);
                p.println("Mean cuadratic error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testf(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;

            case StatTest.fC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("F test, Classification");
                doErrClass(d[0]);
                p.println("Classification error in each fold:");
                for (int i = 0; i < 2; i++) {
                    p.println("Algorithm = " + i);
                    for (int j = 0; j < nFolds; j++) {
                        p.print("Fold " + j + " : ");
                        for (int k = 0; k < nOutputs; k++) {
                            p.print(differences[i][j][k] + " ");
                        }
                        p.println();
                    }
                }

                //The selected test or output module is called
                alpha = testf(differences, significance, p);
                p.println("p-value:");
                for (int i = 0; i < alpha.length; i++) {
                    p.print(alpha[i] + " ");
                }

                p.close();
                break;
            case StatTest.summaryC:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                classSummary(d[0]);
                double sume2[] = new double[nOutputs];
                p.println("TEST RESULTS");
                p.println("============");
                p.println("Classifier= " + nameRel);
                gERR = new double[nOutputs];
                gUNC = new double[nOutputs];
                double serr = 0;
                for (int j = 0; j < nFolds; j++) {
                    p.print("Fold " + j + " : CORRECT=");
                    for (int k = 0; k < nOutputs; k++) {
                        gERR[k] += differences[0][j][k];
                        gUNC[k] += unclassified[0][j][k];
                        serr = differences[0][j][k];
                        sume2[k] += serr * serr;
                        p.print((1 - differences[0][j][k]) + " N/C=" +
                                unclassified[0][j][k] + " ");
                    }
                    p.println();
                }
                for (int k = 0; k < nOutputs; k++) {
                    gERR[k] /= nFolds;
                    gUNC[k] /= nFolds;
                    sume2[k] /= nFolds;
                }
                p.println("Global Classification Error + N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print((gERR[k]) + " ");
                }
                p.println();
                p.println("stddev Global Classification Error + N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(Math.sqrt( -gERR[k] * gERR[k] + sume2[k]) + " ");
                }
                p.println();
                p.println("Correctly classified:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(1 - (gERR[k]) + " ");
                }
                p.println();
                p.println("Global N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(gUNC[k] + " ");
                }
                p.println();
                p.println();
                for (int j = 0; j < nOutputs; j++) {
                    sume2[j] = 0;
                }
                p.println("TRAIN RESULTS");
                p.println("============");
                p.println("Classifier= " + nameRel);
                classSummary(dtrain[0]);
                p.println("Summary of data, Classifiers: " + nameRel);
                gERR = new double[nOutputs];
                gUNC = new double[nOutputs];
                for (int j = 0; j < nFolds; j++) {
                    p.print("Fold " + j + " : CORRECT=");
                    for (int k = 0; k < nOutputs; k++) {
                        gERR[k] += differences[0][j][k];
                        gUNC[k] += unclassified[0][j][k];
                        serr = differences[0][j][k];
                        sume2[k] += serr * serr;
                        p.print((1 - differences[0][j][k]) + " N/C=" +
                                unclassified[0][j][k] + " ");
                    }
                    p.println();
                }
                for (int k = 0; k < nOutputs; k++) {
                    gERR[k] /= nFolds;
                    gUNC[k] /= nFolds;
                    sume2[k] /= nFolds;
                }
                p.println("Global Classification Error + N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print((gERR[k]) + " ");
                }
                p.println();
                p.println("stddev Global Classification Error + N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(Math.sqrt( -gERR[k] * gERR[k] + sume2[k]) + " ");
                }
                p.println();
                p.println("Correctly classified:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(1 - (gERR[k]) + " ");
                }
                p.println();
                p.println("Global N/C:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(gUNC[k] + " ");
                }
p.println();

                p.close();
                break;

            case StatTest.summaryI:
                out = new FileOutputStream(nres);
                p = new PrintStream(out);
                double g_measure, l_measure, g_unc;
                int [][] confusion_matrix;
                double sumeI;
                double serrI;

                // Confusion matrix = TP FN FP TN
                confusion_matrix = confusion_matrix(d[0]);

                nFolds = d[0].length;
                nOutputs = d[0][0][0].length;

                p.println("TEST RESULTS");
                p.println("============");
                p.println("Classifier= " + nameRel);
                g_unc = 0;
                g_measure = 0;
                sumeI = 0;
                serrI = 0;
                gUNC = new double[nOutputs];
                for (int j = 0; j < nFolds; j++) {
                    double measure;
                    int TP = confusion_matrix[j][0];
                    int FN = confusion_matrix[j][1];
                    int FP = confusion_matrix[j][2];
                    int TN = confusion_matrix[j][3];

                    int total_instances = TP + FN + FP + TN;

                    measure = 0;
                    if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                        p.print("Fold " + j + " : AUC=");
                        double TPrate = (double)TP/(double)(TP+FN);
                        double FPrate = (double)FP/(double)(FP+TN);
                        
                        if (((TP+FN) == 0) || ((FP+TN) == 0)) {
                        	measure = 0;
                        }
                        else {
                        	measure = (double)(1+TPrate-FPrate)/2.0;
                        }
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                        p.print("Fold " + j + " : G_MEAN=");
                        measure = Math.sqrt(((double)TP/(double)(TP+FN)) * ((double)TN/(double)(FP+TN)));
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                        p.print("Fold " + j + " : CORRECT=");
                        measure = (double)(TP+TN)/(double)total_instances;
                    }

                    serrI = measure;
                    sumeI += serrI * serrI;
                    g_measure += measure;
                    l_measure = 0;
                    for (int k = 0; k < nOutputs; k++) {
                        gUNC[k] += unclassified[0][j][k];
                        l_measure += unclassified[0][j][k];
                    }
                    g_unc += ((double)l_measure/(double)total_instances);
                    p.print(measure + " N/C=" + (double)l_measure/(double)total_instances + " ");
                    p.println();
                }
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("Global Classification Area Under the ROC Curve:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("Global Classification Geometric Mean:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("Global Classification Standard Accuracy:");
                }
                p.print((g_measure/(double)nFolds) + " ");
                p.println();
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("stddev Global Classification Area Under the ROC Curve:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("stddev Global Classification Geometric Mean:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("stddev Global Classification Standard Accuracy:");
                }
                p.print(Math.sqrt( -(g_measure/(double)nFolds) * (g_measure/(double)nFolds) + (sumeI/(double)nFolds)) + " ");
                p.println();

                p.println("Global N/C:");
                p.print((g_unc/(double)nFolds) + " ");
                p.println();
                p.println();

                // Confusion matrix = TP FN FP TN
                confusion_matrix = confusion_matrix(dtrain[0]);

                nFolds = dtrain[0].length;
                nOutputs = dtrain[0][0][0].length;

                p.println("TRAIN RESULTS");
                p.println("============");
                p.println("Classifier= " + nameRel);
                g_measure = 0;
                g_unc = 0;
                sumeI = 0;
                serrI = 0;
                gUNC = new double[nOutputs];
                for (int j = 0; j < nFolds; j++) {
                    double measure;
                    int TP = confusion_matrix[j][0];
                    int FN = confusion_matrix[j][1];
                    int FP = confusion_matrix[j][2];
                    int TN = confusion_matrix[j][3];

                    int total_instances = TP + FN + FP + TN;

                    measure = 0;
                    if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                        p.print("Fold " + j + " : AUC=");
                        double TPrate = (double)TP/(double)(TP+FN);
                        double FPrate = (double)FP/(double)(FP+TN);
                        
                        if (((TP+FN) == 0) || ((FP+TN) == 0)) {
                        	measure = 0;
                        }
                        else {
                        	measure = (double)(1+TPrate-FPrate)/2.0;
                        }
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                        p.print("Fold " + j + " : G_MEAN=");
                        measure = Math.sqrt(((double)TP/(double)(TP+FN)) * ((double)TN/(double)(FP+TN)));
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                        p.print("Fold " + j + " : CORRECT=");
                        measure = (double)(TP+TN)/(double)total_instances;
                    }

                    serrI = measure;
                    sumeI += serrI * serrI;
                    g_measure += measure;
                    l_measure = 0;
                    for (int k = 0; k < nOutputs; k++) {
                        gUNC[k] += unclassified[0][j][k];
                        l_measure += unclassified[0][j][k];
                    }
                    g_unc += ((double)l_measure/(double)total_instances);
                    p.print(measure + " N/C=" + (double)l_measure/(double)total_instances + " ");
                    p.println();
                }
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("Global Classification Area Under the ROC Curve:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("Global Classification Geometric Mean:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("Global Classification Standard Accuracy:");
                }
                p.print((g_measure/(double)nFolds) + " ");
                p.println();
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    p.println("stddev Global Classification Area Under the ROC Curve:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    p.println("stddev Global Classification Geometric Mean:");
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    p.println("stddev Global Classification Standard Accuracy:");
                }
                p.print(Math.sqrt( -(g_measure/(double)nFolds) * (g_measure/(double)nFolds) + (sumeI/(double)nFolds)) + " ");
                p.println();

                p.println("Global N/C:");
                p.print((g_unc/(double)nFolds) + " ");
                p.println();
                p.println();

                p.close();
                break;

            case StatTest.tabularC:
                Hashtable hsTable = new Hashtable();

                String lastMethod = new String("");

                String Dataset = new String("No initialized");

                List algorithms = new ArrayList();

                for (int i = 0; i < nameResults.size(); i++) {
                    String name = (String) (nameResults.get(i));
                    String[] fields = name.split("/");
                    Dataset = fields[3];
                    if (!lastMethod.equals(fields[2])) {
                        lastMethod = fields[2];
                        algorithms.add(fields[2]);
                    }
                }

                                //workaround
								//To take into account pre-processing algorithms
                				//the previous assignment is not valid
                                //changed (4/4/2007) to this one
                				//if finally always sub folders with dataset name are used,
                                //everything is OK
								Dataset = nameRel;
                count = 0;

                //If the table of the classification by algorithm and by fold was selected
                if (ProcessConfig.tableType1.equalsIgnoreCase("YES")) {
                    out1 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count));
                    p1 = new PrintStream(out1);
                    //contador++;
                    doTableType1(p1, d[0], nameResults, selector, labels, nameRel,
                                 algorithms, Dataset, "TEST RESULTS");
                    if (ProcessConfig.dataTable1.equalsIgnoreCase("TEST-TRAIN")) {
                        doTableType1(p1, dtrain[0], nameResults, selector, labels,
                                     nameRel, algorithms, Dataset,
                                     "TRAIN RESULTS");
                    }
                    p1.close();
                }
                count++;

                //If the table of the classification by algorithm, by fold and by class was selected
                if (ProcessConfig.tableType2.equalsIgnoreCase("YES")) {
                    out2 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count));
                    p2 = new PrintStream(out2);
                    //contador++;
                    doTableType2(hsTable, ProcessConfig.numberLine, p2, d[0],
                                 nameResults, selector, labels, algorithms, Dataset,
                                 "TEST RESULTS");
                    if (ProcessConfig.dataTable2.equalsIgnoreCase("TEST-TRAIN")) {
                        doTableType2(hsTable, ProcessConfig.numberLine, p2,
                                     dtrain[0], nameResults, selector, labels,
                                     algorithms, Dataset, "TRAIN RESULTS");
                    }

                    p2.close();
                }
                count++;

                //If the table of the global average and variance was selected
                if (ProcessConfig.tableType3.equalsIgnoreCase("YES")) {
                    File source_file = new File((String) ProcessConfig.
                                                parOutputData.get(count));
                    boolean header = false;
                    header = source_file.exists();
                    out3 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count), true);
                    p3 = new PrintStream(out3);
                    
                    doTableType3(header, p3, d[0], nameResults, selector, labels,
                                 nameRel, algorithms, Dataset, "TEST RESULTS");
                    p3.close();

                    if (ProcessConfig.dataTable3.equalsIgnoreCase("TEST-TRAIN")) {
                        source_file = new File((String) ProcessConfig.
                                               parOutputData.get(count + 1));
                        header = source_file.exists();
                        out3 = new FileOutputStream((String) ProcessConfig.
                                parOutputData.get(count + 1), true);
                        p3 = new PrintStream(out3);
                        doTableType3(header, p3, dtrain[0], nameResults, selector,
                                     labels, nameRel, algorithms, Dataset,
                                     "TRAIN RESULTS");
                        p3.close();
                    }

                    //Check if this iteration is the last dataset
                    if (ProcessConfig.curDataset == ProcessConfig.numDataset) {

                        //If only test data is selected, the file is just renamed
                        if (ProcessConfig.dataTable3.equalsIgnoreCase(
                                "ONLY-TEST")) {
                            String nameFile = (String) ProcessConfig.
                                              parOutputData.get(count);
                            nameFile = nameFile.substring(0,
                                    nameFile.indexOf("_Test")) +
                                       nameFile.
                                       substring(nameFile.indexOf("_s"),
                                                 nameFile.length());

                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).renameTo(new File(nameFile));
                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).delete();

                        } else {

                            //Join the file
                            String nameFile = (String) ProcessConfig.
                                              parOutputData.get(count);
                            nameFile = nameFile.substring(0,
                                    nameFile.indexOf("_Test")) +
                                       nameFile.substring(nameFile.indexOf("_s"));

                            FileInputStream in = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(count));
                            FileInputStream in1 = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(count +
                                    1));
                            FileOutputStream out_aux = new FileOutputStream(
                                    nameFile);

                            int n = 0, c;
                            while ((c = in.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            } while ((c = in1.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            }
                            in.close();
                            in1.close();
                            out_aux.close();

                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).delete();
                            (new File((String) ProcessConfig.parOutputData.get(
                                    count + 1))).delete();

                        }

                    }
                }
                count += 2;

                //If the table of the confusion matrix was selected
                if (ProcessConfig.matrixConfussion.equalsIgnoreCase("YES")) {
                    for (int i = 0; i < algorithms.size(); i++) {
                        out4 = new FileOutputStream((String) ProcessConfig.
                                parOutputData.get(count));
                        p4 = new PrintStream(out4);
                        count++;
                        doConfusionMatrix(i, hsTable, p4, d[0], nameResults, selector,
                                          labels, algorithms, "TEST RESULTS");
                        if (ProcessConfig.dataMatrix.equalsIgnoreCase(
                                "TEST-TRAIN")) {
                            doConfusionMatrix(i, hsTable, p4, dtrain[0],
                                              nameResults, selector, labels,
                                              algorithms, "TRAIN RESULTS");
                        }
                        p4.close();
                    }
                }
                break;

 case StatTest.tabularI:
                Hashtable hsTableI = new Hashtable();

                String lastMethodI = new String("");

                String DatasetI = new String("No initialized");

                List algorithmsI = new ArrayList();

                for (int i = 0; i < nameResults.size(); i++) {
                    String name = (String) (nameResults.get(i));
                    String[] fields = name.split("/");
                    DatasetI = fields[3];
                    if (!lastMethodI.equals(fields[2])) {
                        lastMethodI = fields[2];
                        algorithmsI.add(fields[2]);
                    }
                }

                                //workaround
								//To take into account pre-processing algorithms
                				//the previous assignment is not valid
                                //changed (4/4/2007) to this one
                				//if finally always sub folders with dataset name are used,
                                //everything is OK
								DatasetI = nameRel;
                count = 0;

                //If the table of the classification by algorithm and by fold was selected
                if (ProcessConfig.tableType1.equalsIgnoreCase("YES")) {
                    out1 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count));
                    p1 = new PrintStream(out1);
                    //contador++;
                    doTableType1(p1, d[0], nameResults, selector, labels, nameRel,
                                 algorithmsI, DatasetI, "TEST RESULTS");
                    if (ProcessConfig.dataTable1.equalsIgnoreCase("TEST-TRAIN")) {
                        doTableType1(p1, dtrain[0], nameResults, selector, labels,
                                     nameRel, algorithmsI, DatasetI,
                                     "TRAIN RESULTS");
                    }
                    p1.close();
                }
                count++;

                //If the table of the classification by algorithm, by fold and by class was selected
                if (ProcessConfig.tableType2.equalsIgnoreCase("YES")) {
                    out2 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count));
                    p2 = new PrintStream(out2);
                    //contador++;
                    doTableType2(hsTableI, ProcessConfig.numberLine, p2, d[0],
                                 nameResults, selector, labels, algorithmsI, DatasetI,
                                 "TEST RESULTS");
                    if (ProcessConfig.dataTable2.equalsIgnoreCase("TEST-TRAIN")) {
                        doTableType2(hsTableI, ProcessConfig.numberLine, p2,
                                     dtrain[0], nameResults, selector, labels,
                                     algorithmsI, DatasetI, "TRAIN RESULTS");
                    }

                    p2.close();
                }
                count++;

                //If the table of the global average and variance was selected
                if (ProcessConfig.tableType3.equalsIgnoreCase("YES")) {
                    File source_file = new File((String) ProcessConfig.
                                                parOutputData.get(count));
                    boolean header = false;
                    header = source_file.exists();
                    out3 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(count), true);
                    p3 = new PrintStream(out3);

                    doTableType3(header, p3, d[0], nameResults, selector, labels,
                                 nameRel, algorithmsI, DatasetI, "TEST RESULTS");
                    p3.close();

                    if (ProcessConfig.dataTable3.equalsIgnoreCase("TEST-TRAIN")) {
                        source_file = new File((String) ProcessConfig.
                                               parOutputData.get(count + 1));
                        header = source_file.exists();
                        out3 = new FileOutputStream((String) ProcessConfig.
                                parOutputData.get(count + 1), true);
                        p3 = new PrintStream(out3);
                        doTableType3(header, p3, dtrain[0], nameResults, selector,
                                     labels, nameRel, algorithmsI, DatasetI,
                                     "TRAIN RESULTS");
                        p3.close();
                    }

                    //Check if this iteration is the last dataset
                    if (ProcessConfig.curDataset == ProcessConfig.numDataset) {

                        //If only test data is selected, the file is just renamed
                        if (ProcessConfig.dataTable3.equalsIgnoreCase(
                                "ONLY-TEST")) {
                            String nameFile = (String) ProcessConfig.
                                              parOutputData.get(count);
                            nameFile = nameFile.substring(0,
                                    nameFile.indexOf("_Test")) +
                                       nameFile.
                                       substring(nameFile.indexOf("_s"),
                                                 nameFile.length());

                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).renameTo(new File(nameFile));
                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).delete();

                        } else {

                            //Join the file
                            String nameFile = (String) ProcessConfig.
                                              parOutputData.get(count);
                            nameFile = nameFile.substring(0,
                                    nameFile.indexOf("_Test")) +
                                       nameFile.substring(nameFile.indexOf("_s"));

                            FileInputStream in = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(count));
                            FileInputStream in1 = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(count +
                                    1));
                            FileOutputStream out_aux = new FileOutputStream(
                                    nameFile);

                            int n = 0, c;
                            while ((c = in.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            } while ((c = in1.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            }
                            in.close();
                            in1.close();
                            out_aux.close();

                            (new File((String) ProcessConfig.parOutputData.get(
                                    count))).delete();
                            (new File((String) ProcessConfig.parOutputData.get(
                                    count + 1))).delete();

                        }

                    }
                }
                count += 2;

                //If the table of the confusion matrix was selected
                if (ProcessConfig.matrixConfussion.equalsIgnoreCase("YES")) {
                    for (int i = 0; i < algorithmsI.size(); i++) {
                        out4 = new FileOutputStream((String) ProcessConfig.
                                parOutputData.get(count));
                        p4 = new PrintStream(out4);
                        count++;
                        doConfusionMatrix(i, hsTableI, p4, d[0], nameResults, selector,
                                          labels, algorithmsI, "TEST RESULTS");
                        if (ProcessConfig.dataMatrix.equalsIgnoreCase(
                                "TEST-TRAIN")) {
                            doConfusionMatrix(i, hsTableI, p4, dtrain[0],
                                              nameResults, selector, labels,
                                              algorithmsI, "TRAIN RESULTS");
                        }
                        p4.close();
                    }
                }
                break;
				
            case StatTest.summaryR:
                
                out = new FileOutputStream(nres);
                p = new PrintStream(out);

                p.println("TEST RESULTS");
                p.println("============");
                p.println("Model = " + nameRel);
                rmsSummary(d[0]);
                p.println("MSE of all folds:");
                gERR = new double[nOutputs];
                sume2 = new double[gERR.length];
                for (int j = 0; j < nFolds; j++) {
                    p.print("Fold " + j + " : ");
                    for (int k = 0; k < nOutputs; k++) {
                        gERR[k] += differences[0][j][k];
                        sume2[k] += differences[0][j][k] * differences[0][j][k];
                        p.print(differences[0][j][k] + " ");
                    }
                    p.println();
                }
                for (int k = 0; k < nOutputs; k++) {
                    gERR[k] /= nFolds;
                    sume2[k] /= nFolds;
                }
                p.println("Global MSE:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(gERR[k] + " ");
                }
                p.println();
                p.println("Global stdev:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(Math.sqrt(sume2[k] - gERR[k] * gERR[k]) + " ");
                }
                p.println();
                p.println("\nTRAIN RESULTS");
                p.println("============");
                p.println("Model = " + nameRel);
                rmsSummary(dtrain[0]);
                p.println("MSE of all folds:");
                gERR = new double[nOutputs];
                for (int j = 0; j < nOutputs; j++) {
                    sume2[j] = 0;
                }
                for (int j = 0; j < nFolds; j++) {
                    p.print("Fold " + j + " : ");
                    for (int k = 0; k < nOutputs; k++) {
                        gERR[k] += differences[0][j][k];
                        sume2[k] += differences[0][j][k] * differences[0][j][k];
                        p.print(differences[0][j][k] + " ");
                    }
                    p.println();
                }
                for (int k = 0; k < nOutputs; k++) {
                    gERR[k] /= nFolds;
                    sume2[k] /= nFolds;
                }
                p.println("Global MSE:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(gERR[k] + " ");
                }
                p.println();
                p.println("Global stdev:");
                for (int k = 0; k < nOutputs; k++) {
                    p.print(Math.sqrt(sume2[k] - gERR[k] * gERR[k]) + " ");
                }

                p.close();
                break;
            case StatTest.tabularR:

                classSummary(d[0]);

                List algorithms1 = new ArrayList();
                String lastMethod1 = new String("");
                String Dataset1 = new String("No initialized");

                for (int i = 0; i < nameResults.size(); i++) {
                    String name = (String) (nameResults.get(i));
                    String[] fields = name.split("/");
                    Dataset1 = fields[3];
                    if (!lastMethod1.equals(fields[2])) {
                        lastMethod1 = fields[2];
                        algorithms1.add(fields[2]);
                    }
                }

                //workaround
				//To take into account pre-processing algorithms
				//the previous assignment is not valid
                //changed (4/4/2007) to this one
				//if finally always sub-folders with dataset name are used,
                //everything is OK
                
				Dataset1 = nameRel;
				
                int countFile = 0;

                //If the table of the classification by algorithm and by fold was selected
                if (ProcessConfig.tableType1.equalsIgnoreCase("YES")) {
                    out1 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(countFile));
                    p1 = new PrintStream(out1);
                    //contadorFile++;
                    doTableType1_Regression(p1, d[0], nameResults, selector, labels,
                                            nameRel, algorithms1, Dataset1,
                                            "TEST RESULTS");
                    if (ProcessConfig.dataTable1.equalsIgnoreCase("TEST-TRAIN")) {
                        doTableType1_Regression(p1, dtrain[0], nameResults, selector,
                                                labels, nameRel, algorithms1,
                                                Dataset1, "TRAIN RESULTS");
                    }
                    p1.close();

                }
                countFile++;

                //If the table of the global average and variance was selected
                if (ProcessConfig.tableType2.equalsIgnoreCase("YES")) {
                    File source_file = new File((String) ProcessConfig.
                                                parOutputData.get(countFile));
                    boolean header = false;
                    header = source_file.exists();
                    out3 = new FileOutputStream((String) ProcessConfig.
                                                parOutputData.get(countFile), true);
                    p3 = new PrintStream(out3);
                    countFile++;
                    doTableType3_Regression(header, p3, d[0], nameResults, selector,
                                            labels, nameRel, algorithms1,
                                            Dataset1, "TEST RESULTS");
                    p3.close();
                    if (ProcessConfig.dataTable2.equalsIgnoreCase("TEST-TRAIN")) {
                        source_file = new File((String) ProcessConfig.
                                               parOutputData.get(countFile));
                        header = source_file.exists();
                        out3 = new FileOutputStream((String) ProcessConfig.
                                parOutputData.get(countFile), true);
                        p3 = new PrintStream(out3);
                        doTableType3_Regression(header, p3, dtrain[0],
                                                nameResults, selector, labels,
                                                nameRel, algorithms1, Dataset1,
                                                "TRAIN RESULTS");
                        p3.close();
                    }
                    countFile++;

                    if (ProcessConfig.curDataset == ProcessConfig.numDataset) {
                        //If only test data is selected, the file is just renamed
                        if (ProcessConfig.dataTable2.equalsIgnoreCase(
                                "ONLY-TEST")) {
                            String nameFile = (String) ProcessConfig.
                                              parOutputData.get(countFile -
                                    2);
                            nameFile = nameFile.substring(0,
                                    nameFile.indexOf("_Test")) +
                                       nameFile.
                                       substring(nameFile.indexOf("_s"),
                                                 nameFile.length());

                            (new File((String) ProcessConfig.parOutputData.get(
                                    countFile - 2))).renameTo(new File(
                                            nameFile));
                            (new File((String) ProcessConfig.parOutputData.get(
                                    countFile - 2))).delete();

                        } else {

                            String nameFile1 = (String) ProcessConfig.
                                               parOutputData.get(countFile -
                                    2);
                            nameFile1 = nameFile1.substring(0,
                                    nameFile1.indexOf("_Test")) +
                                        nameFile1.
                                        substring(nameFile1.indexOf("_s"));

                            FileInputStream in = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(
                                            countFile - 2));
                            FileInputStream in1 = new FileInputStream((String)
                                    ProcessConfig.parOutputData.get(
                                            countFile - 1));
                            FileOutputStream out_aux = new FileOutputStream(
                                    nameFile1);

                            int n = 0, c;
                            while ((c = in.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            } while ((c = in1.read()) != -1) {
                                out_aux.write(c);
                                n++;
                            }
                            in.close();
                            in1.close();

                            out_aux.close();

                            (new File((String) ProcessConfig.parOutputData.get(
                                    countFile - 2))).delete();
                            (new File((String) ProcessConfig.parOutputData.get(
                                    countFile - 1))).delete();
                        }

                    }

                }
                break;

            case StatTest.generalR:
            case StatTest.generalC:

			case StatTest.generalI:
                int contadorFile1 = 0;
                out = new FileOutputStream((String) ProcessConfig.parOutputData.
                                           get(contadorFile1));
                p = new PrintStream(out);
                contadorFile1++;
                out1 = new FileOutputStream((String) ProcessConfig.
                                            parOutputData.get(contadorFile1));
                p1 = new PrintStream(out1);
                doGeneralResults(p1, p, d[0], nameResults, selector, labels);
                p1.close();
                p.close();
                break;
            }
        } catch (Exception e) {
            System.err.println("Error while creating results file " + nres);
            System.err.println("Error Information: " + e);
        }
    }

    /**
     * <p>
     * This method produces several output files with the results of the selected algorithms:
     * A) p-value matrix by dataset (in each matrix, the p-values are obtained testing each algorithm
     * against each one of the others using a suitable statistical test, checking before for normality and homocedasticity),
     * B)trace of the execution by dataset including results by fold, errors of the algorithms, statistical tests applied, p-values obtained and
     * p-value matrix. 
     * </p>
     * @param p1 One of the files where the results are stored
     * @param p The other file where the results are stored
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Results names
     * @param selector An int that selects the statistical test or module to be applied. The relationship 
     * value / statistical test or output module is done via the public final static variables defined at
     * the beginning of this class
     * @param labels Class labels
     * @return Nothing, the results and tables ares stored in the appropriate files
     */ 
    private void doGeneralResults(PrintStream p1, PrintStream p, double[][][] d,
                                  Vector nameResults, int selector, String[] labels) {
        try {
            // Parse the vector of names and guess the number of experiments
            Vector indexes = new Vector();
            String lastMethod = new String("");
            String header = new String("");
            Vector Vheader = new Vector();
            String Dataset = new String("No initialized");
            int lastIndex = -1;
            for (int i = 0; i < nameResults.size(); i++) {
                String name = (String) (nameResults.get(i));
                String fields[] = name.split("/");

                Dataset = fields[3];

                if (!lastMethod.equals(fields[2])) {
                    // New method
                    indexes.add(new Vector());
                    lastIndex++;
                    lastMethod = fields[2];
                    header += fields[2] + " ";
                    Vheader.add(new String(fields[2]));
                }
                ((Vector) indexes.get(lastIndex)).add(new Integer(i));
            }
            p.println("Results:");
            p.println("Detected " + indexes.size() + " methods");
            p.print("Folds=");
            int nfolds = ((Vector) indexes.get(0)).size();
            nfolds /= 2; // Half of files for test
            for (int i = 0; i < indexes.size(); i++) {
                p.print(((Vector) indexes.get(i)).size() / 2 + " ");
            }
            p.println();
            for (int i = 0; i < indexes.size(); i++) {
                if (nfolds != ((Vector) indexes.get(i)).size() / 2) {
                    p.println("Error: different number of folds");
                    return;
                }
            }
            int totExamples = 0;
            int nExamples[] = new int[nfolds];
            for (int j = 0; j < nfolds; j++) {
                totExamples += nExamples[j];
            }
            for (int j = 0; j < nfolds; j++) {
                nExamples[j] = d[j].length;
                for (int i = 0; i < indexes.size(); i++) {
                    if (nExamples[j] != d[j + i * nfolds].length) {
                        System.out.println("Different number of examples! "
                                           + nExamples[j] + " " + d[j +
                                           i * nfolds].length);
                        return;
                    }
                    for (int k = 0; k < nExamples[j]; k++) {
                        if (d[j + i * nfolds][k].length != 2) {
                            System.out.println("Methods must have one output!");
                            return;
                        }
                    }
                }
            }

            p1.println("------------------------------------------------------");
            p1.println("P-Value Matrix");
            p1.println("Dataset used: " + Dataset);
            p1.println("------------------------------------------------------");
            p1.println();

			double sample[][];
            double error[][][] = new double[indexes.size()][nfolds][];
            p.println("Results: Test-Dataset " + header);
            for (int j = 0; j < nfolds; j++) {
                p.println("Fold=" + j);
                // k-th example of the j-th fold in the i-th algorithm
                for (int i = 0; i < indexes.size(); i++) {
                    error[i][j] = new double[nExamples[j]];
                }
                for (int k = 0; k < nExamples[j]; k++) {
                    p.print(doLabel(d[j][k][0], labels) + " ");
                    for (int i = 0; i < indexes.size(); i++) {
                        p.print(doLabel(d[j + i * nfolds][k][1], labels) + " ");

                        error[i][j][k] = doErr(d[j + i * nfolds][k][0],
                                               d[j + i * nfolds][k][1], selector);
                    }
                    p.println();
                }
            }
			if (selector != StatTest.generalI) {
                sample = new double[indexes.size()][nfolds];
                for (int i = 0; i < indexes.size(); i++) {
                    for (int j = 0; j < nfolds; j++) {
                        for (int k = 0; k < nExamples[j]; k++) {
                            sample[i][j] += error[i][j][k];
                        }
                    }

                    for (int j = 0; j < nfolds; j++) {
                        sample[i][j] /= nExamples[j];
                    }
                    p.print("Sample #" + i + " = ");
                    for (int j = 0; j < sample[i].length; j++) {
                        p.print(sample[i][j] + " ");
                    }
                    p.println();
                }
            }
            else {
                sample = new double[indexes.size()][nfolds];
                int [] confusion_matrix = new int[4];
                for (int i = 0; i < indexes.size(); i++) {
                    for (int j = 0; j < nfolds; j++) {
                        for (int m=0; m<4; m++)
                            confusion_matrix[m] = 0;

						for (int k = 0; k < nExamples[j]; k++) {
                        	if (error[i][j][k] != -1.0) {

                        		confusion_matrix[(int)error[i][j][k]]++;
                        	}

                        }

                        double measure;
                        int TP = confusion_matrix[0];
                        int FN = confusion_matrix[1];
                        int FP = confusion_matrix[2];
                        int TN = confusion_matrix[3];

                        measure = 0;
                        if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                            double TPrate = (double)TP/(double)(TP+FN);
                            double FPrate = (double)FP/(double)(FP+TN);
                            if (((TP+FN) == 0) || ((FP+TN) == 0)) {
		                        	measure = 0;
		                        }
		                        else {
		                        	measure = (double)(1+TPrate-FPrate)/2.0;
		                        }
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                            measure = Math.sqrt(((double)TP/(double)(TP+FN)) * ((double)TN/(double)(FP+TN)));
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                            int total_instances = TP + FN + FP + TN;
                            measure = (double)(TP+TN)/(double)total_instances;
                        }

                        sample[i][j] = 1.0-measure;
                    }
                    p.print("Sample #" + i + " = ");
                    for (int j = 0; j < sample[i].length; j++) {
                        p.print(sample[i][j] + " ");
                    }
                    p.println();
                }

            }

            double MEAN[] = new double[indexes.size()];
            double MEDIAN[] = new double[indexes.size()];
            p.println("Expected error: \n" + header);
            for (int k = 0; k < indexes.size(); k++) {
                p.print(df.format(MEAN[k] = mean(sample[k])) + " ");
            }
            p.println();
            p.println("Median of errors: \n" + header);
            for (int k = 0; k < indexes.size(); k++) {
                p.print(df.format(MEDIAN[k] = median(sample[k])) + " ");
            }
            p.println();
            // Check whether the samples follow the normal distribution
            boolean normal[] = new boolean[indexes.size()];
            double vSignificance[] = {0.99, 0.95, 0.90};
            double pValues[][][] = new double[vSignificance.length][sample.length][
                                   sample.length];
            for (int cnf = 0; cnf < vSignificance.length; cnf++) {
                p.println("***** CONFIDENCE LEVEL=" + vSignificance[cnf]);
                p1.println("---- Confidence level=" + vSignificance[cnf] + " ----");

                for (int i = 0; i < indexes.size(); i++) {
                    double[] pvw = testroyston(sample[i]);
                    p.println("Pv normality test: " + pvw[1]);
                    // The level must be a parameter
                    if (pvw[1] > 1 - vSignificance[cnf]) {
                        p.println("Output of algorithm " + i + " is normal " +
                                  pvw[0] + " " + pvw[1]);
                        normal[i] = true;
                    } else {
                        p.println("Output of algorithm " + i +
                                  " is NOT normal " + pvw[0] + " " + pvw[1]);
                        normal[i] = false;
                    }
                }
                double errorTest[][][] = new double[2][nfolds][1];
                for (int i = 0; i < sample.length; i++) {
                    for (int j = i + 1; j < sample.length; j++) {
                        for (int k = 0; k < nfolds; k++) {
                            errorTest[0][k][0] = sample[i][k];
                            errorTest[1][k][0] = sample[j][k];
                        }
                        double result[];
                        if (normal[i] && normal[j]) {
                            // Check variances
                            p.println("f-test between " + i + " and " + j);
                            // f-test
                            result = testf(errorTest, vSignificance[cnf], p);
                            p.println("Pv F test: " + result[0]);
                            if (result[0] < 1 - vSignificance[cnf]) {
                                // Different variances
                                p.println("t-test between " + i + " and " + j +
                                          " different variances");
                                // paired t-test
                                result = testtvar(errorTest, vSignificance[cnf], p);
                                p.println("Pv t test (ineq. var): " + result[0]);
                                pValues[cnf][i][j] = result[0];
                            } else {
                                // Equal variances
                                p.println("t-test between " + i + " and " + j +
                                          " equal variances");
                                // paired t-test
                                result = testt(errorTest, vSignificance[cnf], p);
                                p.println("Pv t test (eq. var): " + result[0]);
                                pValues[cnf][i][j] = result[0];
                            }
                        } else {
                            // nonparametric test
                            p.println("Wilcoxon test between " + i + " and " +
                                      j);
                            result = testrs(errorTest, vSignificance[cnf], p);
                            p.println("Pv wilcoxon test: " + result[0]);
                            pValues[cnf][i][j] = result[0];
                        }
                    }
                }
                p.println("p-value matrix");
                for (int i = 0; i < Vheader.size(); i++) {
                    p1.print("," + Vheader.get(i));
                }
                p1.println();
                for (int i = 0; i < sample.length; i++) {
                    p1.print(Vheader.get(i));
                    for (int j = 0; j < sample.length; j++) {
                        p1.print("," + dfPvalue.format(pValues[cnf][i][j]));
                        p.print(df.format(pValues[cnf][i][j]) + " ");
                    }
                    p.println();
                    p1.println();

                }
                p1.println();

            }
            // Keep log of the results
            String[] THeader = new String[indexes.size()];
            for (int i = 0; i < THeader.length; i++) {
                THeader[i] = new String((String) Vheader.elementAt(i));
            }
            outputFile.appendResults(Dataset, THeader, MEAN, MEDIAN, pValues,
                                     sample, vSignificance);
        } catch (Exception e) {
            System.out.println("Exception in general statistics module");
            e.printStackTrace();
        }
    }

    /**
     * <p>
	 * Computes the confusion matrix for the imbalanced problem of the data in a
     * cubic matrix with samples values indexed by algorithm, fold and dataset.
     * This method also modifies the unclassified matrix
     * </p>
     * @param d The cubic matrix
     * @param index Index used to compute the confusion matrix
     * @return the confusion matrix for the data, some local variables of this
     * module are also modified
     */
    private int [][] confusion_matrix(double[][][] d) {
        int [][] confusion_matrix = new int [d.length][4];
        int num_folds = d.length;

        // Initialize the confusion matrix
        for (int num_f=0; num_f<num_folds; num_f++) {
            for (int conf=0; conf<4; conf++) {
                confusion_matrix[num_f][conf]=0;
            }
        }

        // Initialize the unclassified matrix
        unclassified = new double[1][d.length][d[0][0].length];
        for (int j=0; j<num_folds; j++)
            for (int k=0; k<d[0][0].length; k++)
                unclassified[0][j][k] = 0;

        // Initialize the num_instances vector for each fold
        for (int j = 0; j < num_folds; j++) {
            for (int k = 0; k < d[j].length; k++) {
                // Compute the confusion matrix items
                if ((d[j][k][0] == 0.0) && (d[j][k][1] == 0.0)) {
                    confusion_matrix[j][0]++;
                }
                else if ((d[j][k][0] == 0.0) && (d[j][k][1] == 1.0)) {
                    confusion_matrix[j][1]++;
                }
                else if ((d[j][k][0] == 1.0) && (d[j][k][1] == 0.0)) {
                    confusion_matrix[j][2]++;
                }
                else if ((d[j][k][0] == 1.0) && (d[j][k][1] == 1.0)) {
                    confusion_matrix[j][3]++;
                }
                else {
                    if ((d[j][k][0] != -1.0) && (d[j][k][1] != -1.0))
                        System.err.println("The data isn't appropiate for the confusion matrix computation");
                }
                
                // Compute the unclassified items
                for (int a = 0; a < d[0][0].length; a++) {
                    if (d[j][k][a] == -1) {
                        unclassified[0][j][a]++;
                    }
                }
            }
        }
        // If the positive-negative classes are changed, change the position of the data
        if ((confusion_matrix[0][0]+confusion_matrix[0][1]) > (confusion_matrix[0][2]+confusion_matrix[0][3])) {
            int aux;

            for (int i=0; i<num_folds; i++) {
                // Exchange TP with TN
                aux = confusion_matrix[i][0];
                confusion_matrix[i][0] = confusion_matrix[i][3];
                confusion_matrix[i][3] = aux;
                // Exchange FP with FN
                aux = confusion_matrix[i][1];
                confusion_matrix[i][1] = confusion_matrix[i][2];
                confusion_matrix[i][2] = aux;
            }
        }

        return confusion_matrix;
    }

    /**
     * <p>
     * This method computes the confusion matrix for classifications algorithms
     * </p>
     * @param algorithm An int that identifies the algorithm in the whole data d
     * @param ht A hashtable
     * @param p A print stream where the data is saved
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param selector An int that selects the statistical test or module to be applied. The relationship 
     * value / statistical test or output module is done via the public final static variables defined at
     * the beginning of this class
     * @param labels Class labels
     * @param algorithms List of algorithms
     * @param dataType Type of the data
     * @return Nothing, the results and tables ares stored in the appropriate files
     */ 
    private void doConfusionMatrix(int algorithm, Hashtable ht, PrintStream p,
                                   double[][][] d, Vector nameResults, int selector,
                                   String[] labels, List algorithms,
                                   String dataType) {

        int nfold_real = d.length / algorithms.size();
        int nsamples[] = new int[d.length];
        List typeclasses = new ArrayList();
        nOutputs = d[0][0].length / 2;

        //All classes
        for (int strings = 0; strings < labels.length; strings++) {
            typeclasses.add(labels[strings]);
        }

        String vclass_estimated;
        String vclass_real;
        InformationAboutClass nclass;

        //Number of samples
        for (int fold = 0; fold < nfold_real; fold++) {
            nsamples[fold] = d[fold + nfold_real * algorithm].length;
        }

        // Obtain for each class, the number of correct, and mistake classifications
        for (int fold = 0; fold < nfold_real; fold++) {
            for (int sample = 0; sample < nsamples[fold]; sample++) {
                for (int out = 0; out < nOutputs; out++) {
                    vclass_real = doLabel(d[fold +
                                          nfold_real *
                                          algorithm][sample][out], labels);

                    //Check if this class is in the hashTable
                    if (ht.containsKey(vclass_real)) {
                        nclass = (StatTest.InformationAboutClass) ht.get(
                                vclass_real);
                    }
                    //If this class is not in the hashTable, we add it
                    else {
                        nclass = new InformationAboutClass(vclass_real);
                        ht.put(vclass_real, nclass);
                    }

                    // Check if real class and estimated class are the same
                    vclass_estimated = doLabel(d[fold +
                                               nfold_real *
                                               algorithm][sample][out +
                                               nOutputs], labels);

                    //unclassified are not counted
                    if (vclass_real.equals(vclass_estimated)) {
                        nclass.setCorrectClassifications(nclass.
                                getCorrectClassifications() + 1);
                    } else {
                        nclass.setMistakeClassifications(nclass.
                                getMistakeClassifications() + 1);
                        nclass.setMistakeClass(vclass_estimated);
                    }
                    nclass.setTotalNumberClass(nclass.getTotalNumberClass() + 1);

                }
            }
        }

        //Do the confusion matrix with the before information

        if (dataType.equalsIgnoreCase("TEST RESULTS")) {
            //Print the confusion matrix
            p.println();
            p.println();
            p.println(
                    "---------------------------------------------------------");
            p.println("CONFUSSION MATRIX. ALGORITHM: " +
                      algorithms.get(algorithm));
            p.println(
                    "---------------------------------------------------------");
        }

        p.println();
        p.println(dataType);

        //Print the classes
        for (int i = 0; i < typeclasses.size(); i++) {
            p.print("," + typeclasses.get(i));
        }

        p.println();
        for (int h = 0; h < typeclasses.size(); h++) {

            String theClass = (String) typeclasses.get(h);
            InformationAboutClass other_class = (StatTest.InformationAboutClass)
                                                ht.get(theClass);

            // If this class is not in the hash table, this row is filled with 0
            if (other_class == null) {
                p.print(theClass);
                for (int i = 0; i < typeclasses.size(); i++) {
                    p.print(",0");
                }
            }
            // Check the mistake and correct classifications for the current class
            else {
                List classesMistake = other_class.getMistakeClass();
                int num = 0;
                p.print(theClass);

                for (int i = 0; i < typeclasses.size(); i++) {
                    num = 0;
                    if (theClass.equals(typeclasses.get(i))) {
                        p.print("," + other_class.getCorrectClassifications());
                    } else {
                        for (int j = 0; j < classesMistake.size(); j++) {
                            if (classesMistake.get(j).equals(typeclasses.get(i))) {
                                num++;
                            }
                        }

                        p.print("," + num);
                    }
                }
            }

            p.println();
        }
        ht.clear();
    }

    /**
     * <p>
     * This method computes the error between the obtained and actual value of an output variable, 
     * it works for regression and for classification algorithms
     * </p>
     * @param a Output of the algorithm
     * @param b Actual value
     * @param t An int that identifies the method that calls this one, to know if it StatTest.generalR 
     * (R stands for Regression) or  StatTest.generalC (C stands for classification)
     * or a classification problem
     * @return The error
     */ 
    double doErr(double a, double b, int t) {
        switch (t) {
        case StatTest.generalR:
            return (a - b) * (a - b);
        case StatTest.generalC:
            if (a == b) {
                return 0;
            } else {
                return 1;
            }
		case StatTest.generalI:
            if ((a == 0.0) && (b == 0.0)) {
                return 0;
            }
            else if((a == 0.0) && (b == 1.0)) {
                return 1;
            }
            else if((a == 1.0) && (b == 0.0)) {
                return 2;
            }
            else if((a == 1.0) && (b == 1.0)) {
                return 3;

            }        
			else if ((a == -1.0) || (b == -1.0)) {
            	return -1.0;
            }
		default:
            System.out.println("Unknown type of error");
            return 0;
        }
    }
    /**
     * <p>
     * This method selects the label that matches with the output of a classification algorithm
     * </p>
     * @param a Output of the algorithm
     * @param labels The available labels for this problem/dataset
     * @return The appropriate label or "unclassified" if the output of the algorithm casts to int -1
     */ 
    String doLabel(double a, String[] labels) {
        if (labels == null) {
            return df.format(a);
        } else {
            int nl = (int) a;
            if (nl == -1) {
                return "unclassified";
            } else {
                return labels[nl];
            }
        }
    }

    /**
     * <p>
     * This method saves the table containing the classification rate by algorithm, and by fold
     * </p>
     * @param p A print stream where the table is saved
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Name of the results file (not used)
     * @param selector An int that identifies the statistical test or output module 
     * @param labels Class labels
     * @param nameRel Algorithms names (not used)
     * @param algoritms List of algorithms
     * @param Dataset Name of the dataset
     * @param dataType Type of the data
     * @return Nothing, the table is saved in a file
     */ 
    private void doTableType1(PrintStream p, double[][][] d, Vector nameResults,
                              int selector, String[] labels, String nameRel,
                              List algorithms, String Dataset, String dataType) {

        int [][] confusion_matrix = new int [1][4];
        nFolds = d.length;
        nOutputs = d[0][0].length / 2;
        int nfold_real = d.length / algorithms.size();
        if (selector != StatTest.tabularI) {
            classSummary(d);
        }
        else {
            // Confusion matrix = TP FN FP TN
            confusion_matrix = confusion_matrix(d);
        }

        //Print the headline
        if (dataType.equalsIgnoreCase("TEST RESULTS")) {
            p.println();
            p.println();
            p.println("------------------------------------------------------");
            p.println("Table: Classification rate by algorithm and by fold");
            p.println("Dataset used: " + Dataset);
            p.println("------------------------------------------------------");
        }

        p.println();
        p.println(dataType);
        p.println();

        // Print the test
        if (ProcessConfig.numberLine1 == 2) {
            p.print(Dataset);
            for (int i = 0; i < algorithms.size(); i++) {
                p.print("," + algorithms.get(i));
                p.print("," + algorithms.get(i));
            }

            p.println();
            if (selector != StatTest.tabularI) {
                for (int i = 0; i < algorithms.size(); i++) {
                    p.print("," + "Correctly Classified" + "," + "Not Classified");
                }
            }
            else {
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    for (int i = 0; i < algorithms.size(); i++) {
                        p.print("," + "Area Under the ROC Curve" + "," + "Not Classified");
                    }
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    for (int i = 0; i < algorithms.size(); i++) {
                        p.print("," + "Geometric Mean" + "," + "Not Classified");
                    }
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    for (int i = 0; i < algorithms.size(); i++) {
                        p.print("," + "Correctly Classified" + "," + "Not Classified");
                    }
                }
            }
			
        } else if (ProcessConfig.numberLine1 == 1) {

            p.print(Dataset);
            for (int i = 0; i < algorithms.size(); i++) {
                if (selector != StatTest.tabularI) {
                    p.print("," + algorithms.get(i) + "--" + "Correctly Classified");
                    p.print("," + algorithms.get(i) + "--" + "Not Classified");
                }
                else {
                    if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                        p.print("," + algorithms.get(i) + "--" + "Area Under the ROC Curve");
                        p.print("," + algorithms.get(i) + "--" + "Not Classified");
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                        p.print("," + algorithms.get(i) + "--" + "Geometric Mean");

                        p.print("," + algorithms.get(i) + "--" + "Not Classified");
                    }
                    else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                        p.print("," + algorithms.get(i) + "--" + "Correctly Classified");
                        p.print("," + algorithms.get(i) + "--" + "Not Classified");
                    }
                }
            }
        }

		if (selector == StatTest.tabularI) {
            for (int j = 0; j < nfold_real; j++) {
                p.println();
                p.print("Fold" + j);

                double measure, unc_measure;
                int TP = confusion_matrix[j][0];
                int FN = confusion_matrix[j][1];
                int FP = confusion_matrix[j][2];
                int TN = confusion_matrix[j][3];

                int total_instances = TP + FN + FP + TN;

                measure = 0;
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    double TPrate = (double)TP/(double)(TP+FN);
                    double FPrate = (double)FP/(double)(FP+TN);
                    if (((TP+FN) == 0) || ((FP+TN) == 0)) {
                        	measure = 0;
                        }
                        else {
                        	measure = (double)(1+TPrate-FPrate)/2.0;
                        }
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    measure = Math.sqrt(((double)TP/(double)(TP+FN)) * ((double)TN/(double)(FP+TN)));
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    measure = (double)(TP+TN)/(double)total_instances;
                }
                unc_measure = 0;
                for (int k = 0; k < nOutputs*2; k++) {
                	unc_measure += unclassified[0][j][k];
                }
                
                for (int k = 0; k < nOutputs; k++) {
                	for (int n = 0; n < algorithms.size(); n++) {
                        p.print("," +
                                dfTable1.format(measure) + "," +
                                dfTable1.format((double)unc_measure/(double)total_instances));
                    }
                }
				
            }  
        }
        else {
            for (int j = 0; j < nfold_real; j++) {
                p.println();
                p.print("Fold" + j);
                for (int k = 0; k < nOutputs; k++) {
                    for (int n = 0; n < algorithms.size(); n++) {
                        p.print("," +
                                dfTable1.format((1 - differences[0][j +
                                                 nfold_real * n][k])) + "," +
                                dfTable1.format(unclassified[0][j +
                                                nfold_real * n][k]));
                    }
                }
            }
        }

        p.println();

    }

    /**
     * <p>
     * This method saves the table which contains the classification by class, by algorithm, by fold
     * </p>
     * @param ht A hashtable
     * @param numberLine Number of lines (not used)
     * @param p A print stream where the table is saved
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Name of the results file (not used)
     * @param selector An int that identifies the statistical test or output module (not used) 
     * @param labels Class labels
     * @param algoritms List of algorithms
     * @param Dataset Name of the dataset
     * @param dataType Type of the data
     * @return Nothing, the table is saved in a file
     */ 
    private void doTableType2(Hashtable ht, int numberLine, PrintStream p,
                              double[][][] d, Vector nameResults, int selector,
                              String[] labels, List algorithms, String Dataset,
                              String dataType) {

        List typeclasses = new ArrayList();
        classSummary(d);

        for (int classes = 0; classes < labels.length; classes++) {
            typeclasses.add(labels[classes]);
        }

        if (dataType.equalsIgnoreCase("TEST RESULTS")) {

            // Print the headline
            p.println();
            p.println();
            p.println(
                    "--------------------------------------------------------------");
            p.println(
                    "Table: Classification rate by fold, by algorithm and by class");
            p.println("Dataset used: " + Dataset);
            p.println(
                    "--------------------------------------------------------------");

        }

        p.println();
        p.println(dataType);
        p.println();

        // Print the headline in one or two lines
        if (ProcessConfig.numberLine2 == 1) {
            p.print(Dataset);
            for (int j = 0; j < algorithms.size(); j++) {
                for (int i = 0; i < typeclasses.size(); i++) {
                    p.print("," + algorithms.get(j) + "\\" + typeclasses.get(i));
                }
            }
        } else if (ProcessConfig.numberLine2 == 2) {
            p.print(Dataset);
            for (int j = 0; j < algorithms.size(); j++) {
                for (int i = 0; i < typeclasses.size(); i++) {
                    p.print("," + algorithms.get(j));
                }
            }
            p.println();
            for (int j = 0; j < algorithms.size(); j++) {
                for (int i = 0; i < typeclasses.size(); i++) {
                    p.print("," + typeclasses.get(i));
                }
            }
        }

        int nfold = d.length;
        int nsamples[] = new int[nfold];
        int nfold_real = nfold / algorithms.size();
        nOutputs = d[0][0].length / 2;

        String vclass_estimated;
        String vclass_real;
        InformationAboutClass nclass;

        for (int fold = 0; fold < nfold; fold++) {
            nsamples[fold] = d[fold].length;
        }

        for (int fold = 0; fold < nfold_real; fold++) {
            p.println();
            p.print("Fold" + fold);

            //By each algorithm
            for (int n = 0; n < algorithms.size(); n++) {

                for (int sample = 0; sample < nsamples[fold]; sample++) {
                    for (int out = 0; out < nOutputs; out++) {
                        //InformationAboutClass nclass;
                        vclass_real = doLabel(d[fold +
                                              nfold_real * n][sample][out],
                                              labels);

                        //Check if this class is in the hashTable
                        if (ht.containsKey(vclass_real)) {
                            nclass = (StatTest.InformationAboutClass) ht.get(
                                    vclass_real);
                        }
                        //If this class is not in the hashTable, we add it
                        else {
                            nclass = new InformationAboutClass(vclass_real);
                            ht.put(vclass_real, nclass);
                        }

                        // Check if real class and estimated class are the same
                        vclass_estimated = doLabel(d[fold +
                                nfold_real * n][sample][out +
                                nOutputs],
                                labels);

                        if (vclass_real.equals(vclass_estimated)) {
                            nclass.setCorrectClassifications(nclass.
                                    getCorrectClassifications() + 1);
                        } else {
                            nclass.setMistakeClassifications(nclass.
                                    getMistakeClassifications() + 1);
                            nclass.setMistakeClass(vclass_estimated);
                        }
                        nclass.setTotalNumberClass(nclass.getTotalNumberClass() +
                                1);

                    }
                }

                //Print a row of the table
                for (int i = 0; i < typeclasses.size(); i++) {

                    InformationAboutClass other_class = (StatTest.
                            InformationAboutClass) ht.get(typeclasses.get(i));

                    //If this class is not in hashTable
                    if (other_class == null) {
                        p.print("," + dfTable1.format(0.0));
                    } else {
                        if (other_class.getTotalNumberClass() != 0) {
                            p.print("," +
                                    dfTable1.format((double) other_class.
                                    getCorrectClassifications() /
                                    other_class.getTotalNumberClass()));
                        }

                    }

                }

                ht.clear();

            }
        }

        p.println();
    }
    /**
     * <p>
     * This method saves the table which contains the global average and variance
     * </p>
     * @param header A boolean indicating when the table must have header (true) or not (false)
     * @param p A print stream where the table is saved 
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Name of the results file (not used)
     * @param selector An int that identifies the statistical test or output module (not used) 
     * @param labels Class labels
     * @param nameRel Algorithms names (not used)
     * @param algoritms List of algorithms
     * @param Dataset Name of the dataset
     * @param dataType Type of the data
     * @return Nothing, the table is saved in a file
     */ 
	private void doTableType3(boolean header, PrintStream p, double[][][] d,
                              Vector nameResults, int selector, String[] labels,
                              String nameRel, List algorithms, String Dataset,
                              String dataType) {

        double[] gERR = new double[algorithms.size()];

        double[] gUNC = new double[algorithms.size()];

        double[] gCORR = new double[algorithms.size()];


        double sume2[] = new double[algorithms.size()];
        double sume3[] = new double[algorithms.size()];
        int nfold_real = d.length / algorithms.size();
        if (selector != StatTest.tabularI) {
            classSummary(d);
        }

        if (header == false) {
            if (dataType.equalsIgnoreCase("TEST RESULTS")) {
                p.println();
                p.println();
                p.println(
                        "------------------------------------------------------");
                p.println("Global Average and Variance");
                p.println(
                        "------------------------------------------------------");

            }

            p.println();
            p.println();
            p.println(dataType);

            if (ProcessConfig.numberLine3 == 2) {
                p.println();
                p.print("Dataset");
                for (int i = 0; i < algorithms.size(); i++) {
                    p.print("," + algorithms.get(i));
                    p.print("," + algorithms.get(i));
                    p.print("," + algorithms.get(i));
                }

                p.println();
                for (int i = 0; i < algorithms.size(); i++) {
                    if (selector != StatTest.tabularI) {
                        p.print("," + "Average (Correctly Classified)" + "," +
                            "Variance (Correctly Classified)" +
                            ",Not Classified");
                    }
                    else {
                        if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                            p.print("," + "Average (Area Under the ROC Curve)" + "," +
                                "Variance (Area Under the ROC Curve)" +
                                ",Not Classified");
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                            p.print("," + "Average (Geometric Mean)" + "," +
                                "Variance (Geometric Mean)" +
                                ",Not Classified");
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                            p.print("," + "Average (Correctly Classified)" + "," +
                                "Variance (Correctly Classified)" +
                                ",Not Classified");
                        }
                    }
                }
            } else if (ProcessConfig.numberLine3 == 1) {
                p.println();
                p.print("Dataset");
                for (int i = 0; i < algorithms.size(); i++) {
                    if (selector != StatTest.tabularI) {
                        p.print("," + algorithms.get(i) +
                                "--Average (Correctly Classified)");
                        p.print("," + algorithms.get(i) +
                                "--Variance (Correctly Classified)");
                        p.print("," + algorithms.get(i) + "--Not Classified");
                    }
                    else {
                        if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                            p.print("," + algorithms.get(i) +
                                    "--Average (Area Under the ROC Curve)");
                            p.print("," + algorithms.get(i) +
                                    "--Variance (Area Under the ROC Curve)");
                            p.print("," + algorithms.get(i) + "--Not Classified");
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                            p.print("," + algorithms.get(i) +
                                    "--Average (Geometric Mean)");
                            p.print("," + algorithms.get(i) +
                                    "--Variance (Geometric Mean)");
                            p.print("," + algorithms.get(i) + "--Not Classified");
                        }
                        else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                            p.print("," + algorithms.get(i) +
                                    "--Average (Correctly Classified)");
                            p.print("," + algorithms.get(i) +
                                    "--Variance (Correctly Classified)");
                            p.print("," + algorithms.get(i) + "--Not Classified");
                        }
                    }
                }
            }
        }

        if (selector != StatTest.tabularI) {
            double serr = 0;
            double scorr = 0;

            for (int j = 0; j < nfold_real; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    for (int n = 0; n < algorithms.size(); n++) {
                        gERR[n] += differences[0][j + nfold_real * n][k];
                        gUNC[n] += unclassified[0][j + nfold_real * n][k];
                        gCORR[n] += (1 - differences[0][j + nfold_real * n][k]);
                        serr = differences[0][j + nfold_real * n][k];
                        scorr = (1 - differences[0][j + nfold_real * n][k]);
                        sume2[n] += serr * serr;
                        sume3[n] += scorr * scorr;
                    }
                }
            }


            //Calculate the average and the variance
            double average;
            double variance;
            int totalSamples = nfold_real;
            p.println();
            p.print(Dataset);

            for (int i = 0; i < algorithms.size(); i++) {
                gCORR[i] /= totalSamples;
                sume3[i] /= totalSamples;
                gUNC[i] /= totalSamples;

                average = gCORR[i];
                variance = -gCORR[i] * gCORR[i] + sume3[i];
                p.print("," + dfTable1.format(average) + "," +
                        dfTable1.format(variance) + "," + dfTable1.format(gUNC[i]));
            }
        }
        else {
            // Confusion matrix = TP FN FP TN
            int [][] confusion_matrix = confusion_matrix(d);

            nFolds = d.length;
            nOutputs = d[0][0].length;

            double g_measure = 0;
            double serrI;
            double sumeI = 0;
            double aux_unc;
            gUNC = new double[1];

            for (int j = 0; j < nFolds; j++) {
                double measure;
                int TP = confusion_matrix[j][0];
                int FN = confusion_matrix[j][1];
                int FP = confusion_matrix[j][2];
                int TN = confusion_matrix[j][3];

                int total_instances = TP + FN + FP + TN;

                measure = 0;
                if (ProcessConfig.imbalancedMeasure == ProcessConfig.AUC) {
                    double TPrate = (double)TP/(double)(TP+FN);
                    double FPrate = (double)FP/(double)(FP+TN);
                    if (((TP+FN) == 0) || ((FP+TN) == 0)) {
                        	measure = 0;
                        }
                        else {
                        	measure = (double)(1+TPrate-FPrate)/2.0;
                        }
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.GMEAN) {
                    measure = Math.sqrt(((double)TP/(double)(TP+FN)) * ((double)TN/(double)(FP+TN)));
                }
                else if (ProcessConfig.imbalancedMeasure == ProcessConfig.STANDARDACCURACY) {
                    measure = (double)(TP+TN)/(double)total_instances;
                }

                serrI = measure;
                sumeI += serrI * serrI;
                g_measure += measure;
                aux_unc = 0;
                for (int k = 0; k < nOutputs; k++) {
                    aux_unc += unclassified[0][j][k];
                }
                aux_unc = (double)aux_unc/(double)total_instances;
                gUNC[0] += aux_unc; 
            }
            gUNC[0] = (double)gUNC[0]/(double)nFolds;

            //Calculate the average and the variance
            double average;
            double variance;

            p.println();
            p.print(Dataset);

            average = (double)g_measure/(double)nFolds;
            variance = -(g_measure/(double)nFolds) * (g_measure/(double)nFolds) + (sumeI/(double)nFolds);
            for (int i = 0; i < algorithms.size(); i++) {
                p.print("," + dfTable1.format(average) + "," +
                        dfTable1.format(variance) + "," + dfTable1.format(gUNC[i]));
            }
        }
    }

    /**
     * <p>
     * This method saves the table containing RMS by algorithm, and by fold
     * </p>
     * @param header A boolean indicating when the table must have header (true) or not (false)
     * @param p A print stream where the table is saved 
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Name of the results file (not used)
     * @param selector An int that identifies the statistical test or output module (not used) 
     * @param labels Class labels (not used)
     * @param nameRel Algorithms names (not used)
     * @param algoritms List of algorithms
     * @param Dataset Name of the dataset
     * @param dataType Type of the data
     * @return Nothing, the table is saved in a file
     */ 
    private void doTableType1_Regression(PrintStream p, double[][][] d,
                                         Vector nameResults, int selector,
                                         String[] labels, String nameRel,
                                         List algorithms, String Dataset,
                                         String dataType) {
        rmsSummary(d);
        nOutputs = d[0][0].length / 2;
        int nfold_real = d.length / algorithms.size();

        //Print the header
        if (dataType.equalsIgnoreCase("TEST RESULTS")) {
            p.println();
            p.println();
            p.println("------------------------------------------------------");
            p.println("Table: RMS by algorithm and by fold");
            p.println("Dataset used: " + Dataset);
            p.println("------------------------------------------------------");
        }

        p.println();
        p.println();
        p.println(dataType);
        p.println();

        // Print the test
        if (ProcessConfig.numberLine1 == 2) {

            p.print(Dataset);
            for (int i = 0; i < algorithms.size(); i++) {
                for (int j = 0; j < nOutputs; j++) {
                    p.print("," + algorithms.get(i));
                }

            }
            p.println();
            for (int i = 0; i < algorithms.size(); i++) {
                for (int j = 0; j < nOutputs; j++) {
                    p.print("," + "Output (MSE) ");
                }
            }
        } else {
            p.print(Dataset);
            for (int i = 0; i < algorithms.size(); i++) {

                for (int j = 0; j < nOutputs; j++) {
                    p.print("," + algorithms.get(i) + "--Output (MSE) ");
                }

            }

        }

        for (int j = 0; j < nfold_real; j++) {
            p.println();
            p.print("Fold" + j);
            for (int k = 0; k < nOutputs; k++) {
                for (int n = 0; n < algorithms.size(); n++) {
                    p.print("," +
                            dfTable1.format(differences[0][j +
                                            nfold_real * n][k]));
                }
            }
        }

        p.println();
    }

    /**
     * <p>
     * This method saves the table which contains the global average and variance
     * </p>
     * @param header A boolean indicating when the table must have header (true) or not (false)
     * @param p A print stream where the table is saved 
     * @param d A cubic matrix with samples values indexed by algorithm, fold and dataset
     * @param nameResults Name of the results file (not used)
     * @param selector An int that identifies the statistical test or output module (not used) 
     * @param labels Class labels (not used)
     * @param nameRel Algorithms names (not used)
     * @param algoritms List of algorithms
     * @param Dataset Name of the dataset
     * @param dataType Type of the data
     * @return Nothing, the table is saved in a file
     */ 
    private void doTableType3_Regression(boolean header, PrintStream p,
                                         double[][][] d, Vector nameResults,
                                         int selector, String[] labels,
                                         String nameRel, List algorithms,
                                         String Dataset, String dataType) {

        nOutputs = d.length / 2;
        double[][] gERR = new double[algorithms.size()][nOutputs];
        ;
        double sume2[][] = new double[algorithms.size()][nOutputs];
        int nfold_real = d.length / algorithms.size();
        rmsSummary(d);

        if (header == false) {
            if (dataType.equalsIgnoreCase("TEST RESULTS")) {
                p.println();
                p.println();
                p.println(
                        "------------------------------------------------------");
                p.println("Global Average and Variance");
                p.println(
                        "------------------------------------------------------");
            }

            p.println();
            p.println();
            p.println(dataType);
            p.println();
        }

        if (header == false) {
            if (ProcessConfig.numberLine2 == 2) {

                p.print("DATASET");
                for (int i = 0; i < algorithms.size(); i++) {
                    for (int j = 0; j < nOutputs; j++) {
                        p.print("," + algorithms.get(i));
                        p.print("," + algorithms.get(i));
                    }
                }
                p.println();
                for (int i = 0; i < algorithms.size(); i++) {
                    for (int j = 0; j < nOutputs; j++) {
                        p.print("," + "Average (MSE), Variance (MSE) ");
                    }
                }
            }

            if (ProcessConfig.numberLine2 == 1) {

                p.print("DATASET");
                for (int i = 0; i < algorithms.size(); i++) {
                    for (int j = 0; j < nOutputs; j++) {
                        p.print("," + algorithms.get(i) + "--Average (MSE) ");
                        p.print("," + algorithms.get(i) + "--Variance (MSE) ");
                    }
                }

            }
        }

        double serr = 0;

        for (int n = 0; n < algorithms.size(); n++) {
            for (int j = 0; j < nfold_real; j++) {
                for (int k = 0; k < nOutputs; k++) {
                    gERR[n][k] += differences[0][j + nfold_real * n][k];
                    serr = differences[0][j + nfold_real * n][k];
                    sume2[n][k] += serr * serr;
                }
            }
        }

        //Calculate the average and the variance
        double average;
        double variance;
        int totalSamples = nfold_real;
        p.println();
        p.print(Dataset);

        for (int i = 0; i < algorithms.size(); i++) {
            for (int j = 0; j < nOutputs; j++) {
                gERR[i][j] /= totalSamples;
                sume2[i][j] /= totalSamples;

                average = gERR[i][j];
                variance = -gERR[i][j] * gERR[i][j] + sume2[i][j];
                p.print("," + gbcf.format(average) + "," + gbcf.format(variance));
                //p.print("," + average + "," + variance);

            }
        }

    }


    public class InformationAboutClass {
    	/**
    	* <p>
    	* Class properties:
    	* Class name
    	* Number of correct classifications
    	* Number of mistakes
    	* List of incorrect classes
    	* Number of not classified
    	* Number of examples of this class
    	* </p>
    	*/
        //Class name
        String name;

        //Number of correct classifications
        int correctClassifications;
        //Number of mistakes
        int mistakeClassifications;
        //List of incorrect classes
        List mistakeClass;
        //Number of not classified
        int notClassified;
        //Number of examples of this class
        int totalNumberClass;
        
        //Constructor
        public InformationAboutClass() {
            super();
            name = new String();
            correctClassifications = 0;
            mistakeClassifications = 0;
            notClassified = 0;
            totalNumberClass = 0;
            mistakeClass = new ArrayList();
        }

        public InformationAboutClass(String name) {
            super();
            this.name = name;
            correctClassifications = 0;
            mistakeClassifications = 0;
            notClassified = 0;
            totalNumberClass = 0;
            mistakeClass = new ArrayList();
        }

        //Setters and getters
        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setCorrectClassifications(int correctClassifications) {
            this.correctClassifications = correctClassifications;
        }

        public int getCorrectClassifications() {
            return correctClassifications;
        }

        public void setMistakeClassifications(int mistakeClassifications) {
            this.mistakeClassifications = mistakeClassifications;
        }

        public int getMistakeClassifications() {
            return mistakeClassifications;
        }

        public void getNotClassified(int notClassified) {
            this.notClassified = notClassified;
        }

        public int getNotClassified() {
            return notClassified;
        }

        public void setTotalNumberClass(int totalNumberClass) {
            this.totalNumberClass = totalNumberClass;
        }

        public int getTotalNumberClass() {
            return totalNumberClass;
        }

        public void setMistakeClass(String mistakeClass) {
            this.mistakeClass.add(mistakeClass);
        }

        public List getMistakeClass() {
            return mistakeClass;
        }
    }

    /**
     * <p>
     * This method compares two algorithms across multiple datasets, i.e. the samples are
     * the mean error of each algorithm in each dataset. Results from more than three
     *  datasets are needed (for each algorithm).
     * </p>
     * @param fileName
     * @param algorithm 
     * @return Nothing, the result are saved in a LaTeX file
     */ 
    private void wilcoxonGlobal(String fileName, String algorithm[], int sel) {
		//Steps:
    	//For each algorithm, compute the mean error from all folds
    	//Store this data for each algorithm in a file
    	//Read this data from previous file.
    	//If there are more than three datasets by algorithm the comparison is done
    	//An error is printed otherwise

    	//LaTex output file
        String outputFileName = new String(""); //final output file
        String[] aux = null;
        aux = fileName.split("/");
        for (int i = 0; i < 4; i++) {
            outputFileName += aux[i] + "/";
        }

        //How many files?
        String number = new String("" + aux[4].charAt(6));
        if (aux[4].charAt(7) != 's') {
            number += aux[4].charAt(7);
        }
        int numberOfResults = Integer.parseInt("" + number);
        numberOfResults++; //The first is 0
        String outputString = new String("");
        if (numberOfResults > 3) {
            double[][] results = new double[2][numberOfResults];
            for (int i = 0, j = 0; i < numberOfResults; i++, j += 2) {
                String outputFileWholeName = outputFileName + "result" + i + "s0.stat";
                StringTokenizer line;
                String outputFileWholeNameString = Fichero.leeFichero(outputFileWholeName); //file is a string containing the whole file
                line = new StringTokenizer(outputFileWholeNameString, "\n\r\t");

                line.nextToken(); //Title
                line.nextToken(); //Subtitle
                line.nextToken(); //First algorithm
                for (int h = 0; h < nResults[0]; h++) {
                    line.nextToken(); //All the algorithms
                }
                String resultString = line.nextToken(); //Mean Value: the value
                StringTokenizer res = new StringTokenizer(resultString, " ");
                res.nextToken(); //mean
                res.nextToken(); //value:
                if (StatTest.globalWilcoxonI != sel)
                    results[0][i] = Double.parseDouble(res.nextToken());
                else
                    results[0][i] = 1 - Double.parseDouble(res.nextToken());

                line.nextToken(); //Second algorithm
                for (int h = 0; h < nResults[1]; h++) {
                    line.nextToken(); //All the results
                }
                resultString = line.nextToken(); //Mean Value: valor
                res = new StringTokenizer(resultString, " ");
                res.nextToken(); //mean
                res.nextToken(); //value:
                if (StatTest.globalWilcoxonI != sel)
                    results[1][i] = Double.parseDouble(res.nextToken());
                else
                    results[1][i] = 1 - Double.parseDouble(res.nextToken());
            }
            outputString = doWilcoxon(results, algorithm[0], algorithm[1]);
        } else {
            outputString = doLatexHeader();
            outputString +=
                    "There are few datasets to execute the non-parametric test\n";
            outputString +=
                    "Please select more than THREE datasets in order to have significative results\n";
            outputString += "\\end{document}";
        }
        outputFileName += "output.tex";
        Fichero.escribeFichero(outputFileName, outputString);

    }

    /**
     * This method calculates the p-value obtained with signed ranks wilconxon test
     * @param err double[][] List of output errors for each algorithm
     * @param algorithm1 String The name of the first algorithm
     * @param algorithm2 String The name of the second algorithm
     * @return String the wilcoxon tables with the outputs in LaTeX format
     */
    private String doWilcoxon(double[][] err, String algorithm1,
                                 String algorithm2) {

        double result;
        int rNeg, rPos;
        double sumrNeg, sumrPos;
        rNeg = rPos = 0;
        sumrNeg = sumrPos = 0.0;

        //Differences between algorithm are calculated
        //it's counted the number of null differences
        double diff[] = new double[err[0].length];
        int nulls = 0;
        for (int i = 0; i < err[0].length; i++) {
            diff[i] = err[0][i] - err[1][i];
            if (diff[i] == 0.0) {
                nulls++;
            }
        }
        //class pair is used to redefine comparison operator
        //First member is the absolute value
        //Second member is the sign
        class pair {
            double first;
            int second;
            pair() {
                first = 0.0;
                second = 0;
            }

            pair(double x, int y) {
                first = x;
                second = y;
            }
        };
        //Remove 0 values and build pair vector
        pair diff2[] = new pair[err[0].length - nulls];
        int idiff2 = 0;
        for (int i = 0; i < err[0].length; i++) {
            if (diff[i] != 0) {
                //First is the absolute value, and the second is the sign (1 or -1)
                diff2[idiff2] = new pair(Math.abs(diff[i]),
                                         (int) (Math.abs(diff[i]) / diff[i]));
                idiff2++;
            }
        }
        //order by absolute value
        Arrays.sort(diff2, new Comparator() {
            public int compare(Object a, Object b) {
                pair x = (pair) a;
                pair y = (pair) b;
                if (x.first < y.first) {
                    return -1;
                } else
                if (x.first > y.first) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        //Calculate ranks
        double rank[] = new double[diff2.length];
        int i = 0;
        //for each element
        while (i < diff2.length) {
            //Starting in the next
            int j = i + 1;
            //while j is in the range
            while (j < diff2.length) {
                //if both samples are equal
                if (diff2[i].first == diff2[j].first) {
                    j++;
                }
                //if not
                else {
                    break;
                }
            }
            //For element from i to j, assign rank
            for (int k = i; k < j; k++) {
                rank[k] = (double) (i + j - 1) / 2 + 1;
            }
            //Restart before processed elements
            i = j;
        }
        //It's calculated the smallest sample.
        double sum = 0;
        for (int k = 0; k < diff2.length; k++) {
            sum += rank[k] * diff2[k].second;
        }
        double mean = 0;
        double variance = diff2.length * (diff2.length + 1) *
                          (2 * diff2.length + 1) / 6.0;
        if (sum > 0) {
            result = pnorm((sum - .5) / Math.sqrt(variance), true);
        } else {
            result = pnorm((sum + .5) / Math.sqrt(variance), true);
        }
        if (result < 1 - result) {
            result = 2 * result;
        } else {
            result = 2 * (1 - result);
        }

        //rNeg and rPos are computed
        for (i = 0; i < diff2.length; i++) {
            if (diff2[i].second < 0) {
                rPos++;
                sumrPos += rank[i];
            } else {
                rNeg++;
                sumrNeg += rank[i];
            }
        }

        return doLatexOutput(rNeg, rPos, nulls, (diff2.length + nulls),
                                 sumrNeg, sumrPos, result, algorithm1,
                                 algorithm2);
    }

    
    /**
     * This method saves the results of a Wilcoxon test across multiple datasets in a file in LaTeX format
     * @param rNeg Number of negative ranked
     * @param rPos Number of positive ranked
     * @param ties Number of ties
     * @param total Size of the data
     * @param rNegSum Sum of negative ranked
     * @param rPosSum Sum of positive ranked
     * @param p_value P value of the test
     * @param alg1 Name of the first algorithm
     * @param alg2 Name of the second algorithm
     * @return String the wilcoxon tables with the outputs in LaTeX format
     */    
    private String doLatexOutput(int rNeg, int rPos, int ties, int total,
                                     double rNegSum, double rPosSum,
                                     double p_value, String alg1, String alg2) {

        String output = new String("");

        double rNegMean, rPosMean;

        rNegMean = rPosMean = 0.0;

        if (rNeg > 0) {
            rNegMean = rNegSum / rNeg;
        }
        if (rPos > 0) {
            rPosMean = rPosSum / rPos;
        }

        output += doLatexHeader();

        output += "\t\\begin{table}[!th]\n";
        output +=
                "\t\\caption{Ranks for the algorithms selected. Positive Ranks correspond to " +
                alg1 + ". ";
        output += "Negative Ranks correspond to " + alg2 + ".}\\label{ranks}\n";
        output += "\t\\centering\n\t\\begin{tabular}{|ll|c|c|c|}\n";
        output += "\t\\hline\n\t&&N&Mean Rank&Sum of Ranks\\\\\n\t\\hline\n";

        output += "\t" + alg1 + " vs. " + alg2 + "&Positive Ranks&" + rPos +
                "&" +
                rPosMean + "&" + rPosSum + "\\\\\n";
        output += "\t&Negative Ranks&" + rNeg + "&" + rNegMean + "&" +
                rNegSum + "\\\\\n";
        output += "\t&Ties&" + ties + "&&\\\\\n";
        output += "\t&Total&" + total + "&&\\\\\n";
        output += "\t\\hline\n\t\\end{tabular}\n\t\\end{table}\n\n";

        double rPosTot, rNegTot;
        double tieSum = 0;
        for (int i = 0; i < ties; i++) {
            tieSum += i;
        }
        tieSum /= 2.0;
        rPosTot = (rPosSum + tieSum) + (ties * rPosSum);
        rNegTot = (rNegSum + tieSum) + (ties * rNegSum);

        output += "\t\\begin{table}[!th]\n";
        output += "\t\\caption{Test statistics for the algorithms selected. Positive Ranks ($R^+$) correspond to " +
                alg1 + ". ";
        output += "Negative Ranks ($R^-$) correspond to " + alg2 +
                ".}\\label{wcx}\n";
        output += "\t\\centering\n\t\\begin{tabular}{|l|c|c|c|}\n";
        output +=
                "\t\\hline\n\tComparison&$R^+$&$R^-$&p-value\\\\\n\t\\hline\n";
        output += "\n\t" + alg1 + " vs. " + alg2 + "&" + rPosTot + "&" +
                rNegTot + "&" + p_value + "\\\\\n";
        output += "\t\\hline\n\t\\end{tabular}\n\t\\end{table}\n\n";

        output += "\n\n\\end{document}";

        return output;
    }

    /**
     * This method composes the header of a LaTeX file
     * @return String the header of the LaTeX file
     */    
    private String doLatexHeader() {
        String output = new String("");
        output += "\\documentclass[a4paper,12pt]{article}\n";
        output += "\\usepackage [english] {babel}\n";
        output += "\\usepackage [latin1]{inputenc}\n";
        output += "\\usepackage{graphicx}\n";
        output += "\\usepackage{fancyhdr}\n";
        output += "\\pagestyle{fancy}\\fancyfoot[C]{Page \\thepage}\n";
        output += "\\fancyhead[L]{Wilcoxon Signed Ranks Test.}\n";
        output +=
                "\\textwidth=17cm \\topmargin=-0.5cm \\oddsidemargin=-0.5cm \\textheight=23cm\n";
        output +=
                "\\title{Output Tables for the Wilcoxon Signed Ranks Test.}\n";
        output +=
                "\\date{\\today}\n\\begin{document}\n\\maketitle\n\\section{Tables.}\n\n";

        return output;
    }
    
}

