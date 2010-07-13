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

package keel.Algorithms.Genetic_Rule_Learning.M5Rules;

/**
 * Class for performance measures
 */

public final class Measures {

    int type; /* type for printing */
    double correlation; /* correlation coefficient */
    double meanAbsErr; /* mean absolute error */
    double meanSqrErr; /* mean sqaured error */

    /**
     * Constructs a Measures object which could containing the performance measures
     */
    public Measures() {

        type = 0;
        correlation = 0.0;
        meanAbsErr = 0.0;
        meanSqrErr = 0.0;
    }

    /**
     * Converts the performance measures to a string
     * @param absDev the absolute deviation of the class attribute
     * @param sd the standard deviation of the class attribute
     * @param set the type of the performance measures, is one of "t","T","f","F","x", for training, test, training of one fold in cross-validation, test of one test in cross-validation, cross-validation final resuls respectively
     * @param smooth either "u" or "s" for unsmoothed or smoothed
     * @return the converted string
     */
    public final String toString(double absDev, double sd, String set,
                                 String smooth) {

        StringBuffer text = new StringBuffer();

        switch (type) {
        case 0:
            text.append("    Correlation coefficient:\t\t" +
                        M5.doubleToStringF(correlation, 9, 3) + "\t\t\t      " +
                        set + smooth);
            text.append("    Mean absolute error:\t\t" +
                        M5.doubleToStringG(meanAbsErr, 9, 4) + "\t\t\t      " +
                        set + smooth);
            text.append("    Root mean squared error:\t\t" +
                        M5.doubleToStringG(Math.sqrt(Math.abs(meanSqrErr)), 9,
                                           4) + "\t\t\t      " + set + smooth);
            text.append("    Relative absolute error:\t\t" +
                        M5.doubleToStringF(meanAbsErr / absDev * 100.0, 9, 2) +
                        " %\t\t\t      " + set + smooth);
            text.append("    Root relative squared error:\t" +
                        M5.
                        doubleToStringF(Math.sqrt(Math.abs(meanSqrErr)) / sd * 100.0,
                                        9, 2) + " %\t\t\t      " + set + smooth);
            break;
        case 1:
            text.append("    Correlation coefficient:\t\t" +
                        M5.doubleToStringF(correlation, 9, 3) + "\t\t\t      " +
                        set + smooth);
            text.append("    Mean absolute error:\t\t" +
                        M5.doubleToStringG(meanAbsErr, 9, 4) + "\t\t\t      " +
                        set + smooth);
            text.append("    Root mean squared error:\t\t" +
                        M5.doubleToStringG(Math.sqrt(Math.abs(meanSqrErr)), 9,
                                           4) + "\t\t\t      " + set + smooth);
            text.append("    Relative absolute error:\t\tundefined\t\t\t      " +
                        set + smooth);
            text.append(
                    "    Root relative squared error:\tundefined\t\t\t      " +
                    set + smooth);
            break;
        case 2:
            text.append("    Correlation coefficient:\t\t" +
                        M5.doubleToStringF(correlation, 9, 3) + "\t\t\t      " +
                        set + smooth);
            text.append("    Mean absolute error:\t\tundefined\t\t\t      " +
                        set + smooth);
            text.append("    Root mean squared error:\t\tundefined\t\t\t      " +
                        set + smooth);
            text.append("    Relative absolute error:\t\tundefined\t\t\t      " +
                        set + smooth);
            text.append(
                    "    Root relative squared error:\tundefined\t\t\t      " +
                    set + smooth);
            break;
        default:
            M5.errorMsg("wrong type in Measures.print().");
        }

        return text.toString();
    }

    /**
     * Adds up performance measures for cross-validation
     * @param m performance measures of a fold
     */
    public final void incremental(Measures m) {

        correlation += m.correlation;
        meanAbsErr += m.meanAbsErr;
        meanSqrErr += m.meanSqrErr;
    }


}

