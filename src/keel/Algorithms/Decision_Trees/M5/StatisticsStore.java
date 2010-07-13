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
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

import java.io.*;
import java.util.*;

/**
 * Stores some statistics.
 */
public final class StatisticsStore {
    int numInstances; // number of the instances
    int missingInstances; // number of the instances with missing values
    int first; // index of the first instance
    int last; // index of the last instance
    int attr; // attribute
    double sum; // sum of the instances for attribute
    double sqrSum; // squared sum of the instances for attribute
    double va; // variance
    double sd; // standard deviation


    /**
     * Constructs an object which stores some statistics of the instances such
     *      as sum, squared sum, variance, standard deviation
     * @param low the index of the first instance
     * @param high the index of the last instance
     * @param attribute the attribute
     * @param inst the instances
     */

    public StatisticsStore(int low, int high, int attribute, M5Instances inst) {
        int i, count = 0;
        double value;

        numInstances = high - low + 1;
        missingInstances = 0;
        first = low;
        last = high;
        attr = attribute;
        sum = 0.0;
        sqrSum = 0.0;
        for (i = first; i <= last; i++) {
            if (inst.instance(i).isMissing(attr) == false) {
                count++;
                value = inst.instance(i).value(attr);
                sum += value;
                sqrSum += value * value;
            }

            if (count > 1) {
                va = (sqrSum - sum * sum / count) / count;
                va = Math.abs(va);
                sd = Math.sqrt(va);
            } else {
                va = 0.0;
                sd = 0.0;
            }
        }
    }

    /**
     * Converts the stats to a string
     * @return the converted string
     */
    public final String toString() {

        StringBuffer text = new StringBuffer();

        text.append("Print statistic values of instances (" + first + "-" +
                    last +
                    "\n");
        text.append("    Number of instances:\t" + numInstances + "\n");
        text.append("    NUmber of instances with unknowns:\t" +
                    missingInstances +
                    "\n");
        text.append("    Attribute:\t\t\t:" + attr + "\n");
        text.append("    Sum:\t\t\t" + sum + "\n");
        text.append("    Squared sum:\t\t" + sqrSum + "\n");
        text.append("    Stanard Deviation:\t\t" + sd + "\n");

        return text.toString();
    }


}


