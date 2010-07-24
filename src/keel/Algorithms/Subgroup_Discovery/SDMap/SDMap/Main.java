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
 * @author Written by Alberto Fernandez (University of Granada) 01/02/2006
 * @author Modified by Nicola Flugy Papa (Politecnico di Milano) 24/03/2009
 * @author Modified by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

public class Main {
	/**
	 * <p>
	 * It reads the configuration file (data-set files and parameters) and launch the algorithm
	 * </p>
	 */

    private parseParameters parameters;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    public Main() {
    }

    /**
     * <p>
     * It launches the algorithm
     * </p>
     * @param confFile String it is the filename of the configuration file.
     */
    private void execute(String confFile) {
        parameters = new parseParameters();
        parameters.parseConfigurationFile(confFile);
        FPgrowth method = new FPgrowth(parameters);
        if(!method.isContinuous())
            method.execute();
        else System.out.println("\nThe dataset has continuous variable and it cannot be executed.\nAlgorithm finished");
    }

    /**
     * <p>
     * Main Program
     * </p>
     * <p>
     * <em>algorith = &lt;algorithm name></em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;test file&gt;"</em><br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;" "&lt;rule file&gt;" "&lt;measure file&gt;"</em><br/>
     * <br/>
     * <em>&lt;MinimumSupport&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;MinimumConfidence&gt; = &lt;value2&gt;</em><br/>
     * <em>&lt;RulesReturn&gt; = &lt;value3&gt;</em><br/>
     * </p>
     * @param args It contains the name of the configuration file
     */
    public static void main(String args[]) {
        Main program = new Main();
        StopWatch sw = new StopWatch();
        sw.start();

        program.execute(args[0]);

        sw.stop();
        sw.print();
    }
}
