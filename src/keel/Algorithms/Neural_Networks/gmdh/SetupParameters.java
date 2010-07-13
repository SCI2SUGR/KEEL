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

package keel.Algorithms.Neural_Networks.gmdh;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.core.Randomize;

/**
 * <p>
 * Class for capturing the global parameters and data
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class SetupParameters extends Parameters{

  protected int omega, max_nodes; // Max number of nodes
  //public long seed;
  //protected Random random;
  protected double Tend, To, aRange, LM_convergence, w_mse, w_k;
  //public int Noutputs, Ninputs, n_train_patterns, n_test_patterns;
  //public String train_file, test_file, val_file, train_output, test_output, problem;
  public String error /* {mse, missclass} */;
  //public boolean tipify_inputs, verbose;

  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public SetupParameters() {
  }

  /**
   * <p>
   * Method that takes the global parameters from a file
   * </p>
   * @param file_name Name of file to load
   */
  public void LoadParameters(String file_name) {
    InputStream paramsFile;
    String line;
    int pos1, pos2;

    Properties props = new Properties();

    try {
      paramsFile = new FileInputStream(file_name);
      props.load(paramsFile);
      paramsFile.close();
    }
    catch (IOException ioe) {
      System.out.println("I/O Exception.");
      System.exit(0);
    }

    // Load global parameters
    omega = Integer.parseInt(props.getProperty("Omega"));
    max_nodes = Integer.parseInt(props.getProperty("MaxNodes"));
    Tend = Double.parseDouble(props.getProperty("Tend"));
    alpha = Double.parseDouble(props.getProperty("alpha"));
    To = Double.parseDouble(props.getProperty("To"));
    aRange = Double.parseDouble(props.getProperty("a_Range"));
    LM_convergence = Double.parseDouble(props.getProperty("LM_convergence"));
    w_mse = Double.parseDouble(props.getProperty("w_mse"));
    w_k = Double.parseDouble(props.getProperty("w_k"));

    seed = Long.parseLong(props.getProperty("seed"));
    if (seed != -1) {
    	Randomize.setSeed(seed);
    }

    line = props.getProperty("inputData");
    pos1 = line.indexOf("\"", 0);
    pos2 = line.indexOf("\"", pos1 + 1);
    train_file = line.substring(pos1 + 1, pos2);
    pos1 = line.indexOf("\"", pos2 + 1);
    pos2 = line.indexOf("\"", pos1 + 1);
    val_file = line.substring(pos1 + 1, pos2);
    pos1 = line.indexOf("\"", pos2 + 1);
    pos2 = line.indexOf("\"", pos1 + 1);
    test_file = line.substring(pos1 + 1, pos2);

    line = props.getProperty("outputData");
    pos1 = line.indexOf("\"", 0);
    pos2 = line.indexOf("\"", pos1 + 1);
    train_output = line.substring(pos1 + 1, pos2);
    pos1 = line.indexOf("\"", pos2 + 1);
    pos2 = line.indexOf("\"", pos1 + 1);
    test_output = line.substring(pos1 + 1, pos2);

    test_data = Boolean.valueOf(props.getProperty("Test_data")).booleanValue();
    tipify_inputs = Boolean.valueOf(props.getProperty("Tipify_inputs")).booleanValue();
    verbose = Boolean.valueOf(props.getProperty("Verbose")).booleanValue();
    problem = props.getProperty("Problem");
    error = props.getProperty("Error");
  }
}

