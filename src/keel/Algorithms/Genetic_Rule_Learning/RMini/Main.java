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

package keel.Algorithms.Genetic_Rule_Learning.RMini;

import java.util.StringTokenizer;

import org.core.Fichero;
import java.util.StringTokenizer;

/**
 * <p>Title: Main Class of the Program</p>
 *
 * <p>Description: It reads the configuration file (data-set files and parameters) and launch the algorithm</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author JesÃºs JimÃ©nez
 * @version 1.0
 */
public class Main {

	private parseParameters parameters;

	/** Default Constructor */
	public Main() {
	}

	/**
	 * It launches the algorithm
	 * @param confFile String it is the filename of the configuration file.
	 */
	private void execute(String confFile) {
		parameters = new parseParameters();
		parameters.parseConfigurationFile(confFile);
		RMini method = new RMini(parameters);
		method.execute();
	}
	

	/**
	 * Main Program
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
	public static void main(String args[]) {
		Main program = new Main();
		
		System.out.println("Executing Algorithm.");
		program.execute(args[0]);
	}
}