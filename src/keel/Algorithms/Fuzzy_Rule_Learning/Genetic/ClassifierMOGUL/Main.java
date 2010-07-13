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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
import java.io.*;
import java.util.*;
import java.lang.*;

public class Main {
/**	
 * <p>
 * Main Class of the Program. It launches the algorithm
 * </p>
 */
 
 
    /**
     * <p>
     * Main Program
     * </p>
     * @param args It contains the name of the configuration file<br/>
     * Format:<br/>
     * <em>algorith = &lt;algorithm name></em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;validation file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = value</em> (if used)<br/>
     * <em>&lt;Parameter1&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;Parameter2&gt; = &lt;value2&gt;</em> ... <br/>
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Remember: java Main <configuration_file>");
        } else {
            Mogul mogul = new Mogul(args[0]);
            System.out.println(
                    "Step 1: Obtaining the initial Rule Base and Data Base");
            mogul.run();
            MyDataset tabla_tra = mogul.getTable(true);
            MyDataset tabla_tst = mogul.getTable(false);
            Simplif simplificacion = new Simplif(args[0], tabla_tra, tabla_tst);
            System.out.println("Step 2: Genetic Selection of the Rules");
            simplificacion.run();
            Tuning tun = new Tuning(args[0], tabla_tra, tabla_tst);
            System.out.println("Final Step: Genetic Tuning of the FRBS");
            tun.run();
            System.out.println("Algorithm Finished!");
        }
    }
}

