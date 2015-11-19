/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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


package keel.Algorithms.Semi_Supervised_Learning.Basic;

import java.io.*;


/**
 * Class to implement Parameters the SMO algorithm
 * <p>
 * @author Written by I. Triguero and J. A. SÃ¡ez, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * Date: 10/02/11
 * @version 1.0
 * @since JDK1.6
 * </p>
 */
public class ParametersSMO {

    /**
     * Number of partitions.
     */
    static public int numPartitions;

    /**
     * Number of instances.
     */
    static public int numInstances;

    /**
     * Number of classes.
     */
    static public int numClasses;

    /**
     * Training input filename.
     */
    static public String trainInputFile;

    /**
     * Test input filename.
     */
    static public String testInputFile;

    /**
     * Seed for random purpose.
     */
    static public String seed;
  }


