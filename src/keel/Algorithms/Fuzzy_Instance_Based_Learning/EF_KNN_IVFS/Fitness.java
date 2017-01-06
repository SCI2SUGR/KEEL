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
 *
 * File: Fitness.java
 *
 * Implementation of the fitness function of EF-KNN-IVFS
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;


class Fitness {

    private static int evaluations = 0;

    /**
     * Evaluate a chromosome using a IVFSKNN classifier
     * @param solution Chromosome to evaluate
     * @return score of the Chromosome
     */
    public static double evaluate(Chromosome solution){

        IVFSKNN classifier = new IVFSKNN(solution);

        evaluations++;
        return classifier.getLooScore();

    }

    /**
     * Get the current number of evaluations spent
     * @return Amount of evaluations used
     */
    public static int getEvaluations() {
        return evaluations;
    }
}
