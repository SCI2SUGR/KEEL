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

package keel.Algorithms.Neural_Networks.gann;

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

public class SetupParameters extends Parameters {
	
  public int n_indiv, max_generations;
  public double elite, w_range, connectivity;
  public double p_param, p_bp, p_struct;

  //public Random random;

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
    int pos2;

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
    Nhidden_layers = Integer.parseInt(props.getProperty("Hidden_layers"));
    Nhidden = new int[Nhidden_layers + 1];
    line = props.getProperty("Hidden_nodes");

    // Number of nodes per layer
    int j = 0;
    int pos1 = 0;
    do {
      pos2 = line.indexOf(" ", pos1);
      if (pos2 != -1) {
        Nhidden[j] = Integer.parseInt(line.substring(pos1, pos2));
        pos1 = pos2 + 1;
        j++;
      }
      else {
        Nhidden[j] = Integer.parseInt(line.substring(pos1));
        j++;
      }
    }
    while (pos2 != -1 && j < Nhidden_layers);

    for (int i = j; i < Nhidden_layers - 1; i++) {
      Nhidden[i] = Nhidden[j - 1];
    }
    Nhidden[Nhidden_layers - 1] = Integer.parseInt(line.substring(pos1));

    // Transfer functions per layer
    transfer = new String[Nhidden_layers + 1];
    line = props.getProperty("Transfer");
    j = pos1 = 0;
    do {
      pos2 = line.indexOf(" ", pos1);
      if (pos2 != -1) {
        transfer[j] = line.substring(pos1, pos2);
        pos1 = pos2 + 1;
        j++;
      }
      else {
        transfer[j] = line.substring(pos1);
        j++;
      }

    }
    while (pos2 != -1 && j < Nhidden_layers);

    for (int i = j; i < Nhidden_layers; i++) {
      transfer[i] = transfer[j - 1];
    }
    transfer[Nhidden_layers] = line.substring(pos1);

    eta = Double.parseDouble(props.getProperty("Eta"));
    alpha = Double.parseDouble(props.getProperty("Alpha"));
    lambda = Double.parseDouble(props.getProperty("Lambda"));
    n_indiv = Integer.parseInt(props.getProperty("Individuals"));
    max_generations = Integer.parseInt(props.getProperty("Max_generations"));
    elite = Double.parseDouble(props.getProperty("Elite"));
    w_range = Double.parseDouble(props.getProperty("W_range"));
    connectivity = Double.parseDouble(props.getProperty("Connectivity"));
    p_bp = Double.parseDouble(props.getProperty("P_bp"));
    p_struct = Double.parseDouble(props.getProperty("P_struct"));
    p_param = Double.parseDouble(props.getProperty("P_param"));

    //threshold = Double.parseDouble(props.getProperty("Threshold"));
    test_data = Boolean.valueOf(props.getProperty("Test_data")).booleanValue();
    val_data = Boolean.valueOf(props.getProperty("Validation_data")).
        booleanValue();
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

    cross_validation = Boolean.valueOf(props.getProperty("Crossvalidation")).
        booleanValue();
    cycles = Integer.parseInt(props.getProperty("BP_cycles"));
    improve = Double.parseDouble(props.getProperty("Improve"));
    seed = Long.parseLong(props.getProperty("seed"));
    if (seed != -1) {
    	Randomize.setSeed(seed);
    }
    problem = props.getProperty("Problem");
    // bp_type = props.getProperty("BPtype");
    bp_type = "BPstd";
    tipify_inputs = Boolean.valueOf(props.getProperty("Tipify_inputs")).
        booleanValue();
    save = Boolean.valueOf(props.getProperty("SaveAll")).booleanValue();
  }

}

