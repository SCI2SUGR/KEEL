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
 * Class Parameters
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */

public class Parameters {

  /** Number of units in each layer*/
  public int Nhidden[], Noutputs, Ninputs;
  
  /** Number of train, validation and test patterns */
  public int n_train_patterns, n_val_patterns,
      n_test_patterns;
  
  /** Number of cycles */
  public int cycles;
  
  /** Number of hidden layers */
  public int Nhidden_layers;
  
  /** Check if test,validation or cross validation data is going to be used*/
  public boolean test_data, val_data, cross_validation;
  
  /** Check if is going to be tipified */
  public boolean tipify_inputs;
  
  /** Verbose output */
  public boolean verbose;
  
  /** Save at the end of the output */
  public boolean save;
  
  /** Problem coefficients */
  public double eta, alpha, // For momentum term
      lambda, // For weight decay term
      improve, // Minimum improve in crossvalidation training
      threshold;
  
  /** Random seed */
  public long seed;
  
  /** File names */
  public String train_file, test_file, train_output, test_output, model_output, val_file;
  
  /** Type of the problem ( CLASSIFICATION | REGRESSION ) */
  public String problem;
  
  /** Not used */
  public String bp_type;
  
  /** Transfer function ( LOG | HTAN | LINEAR ) */
  public String transfer[];
  
  /**
   * <p>
   * Empty constructor
   * </p>
   */
  public Parameters() {
  }

  /**
   * <p>
   * Load parameters from file_name to global
   * </p>
   * @param file_name Data file name
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

    // Learning coefficients
    eta = Double.parseDouble(props.getProperty("Eta"));
    alpha = Double.parseDouble(props.getProperty("Alpha"));
    lambda = Double.parseDouble(props.getProperty("Lambda"));
  
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
    
    pos1 = line.indexOf("\"", pos2 + 1);
    pos2 = line.indexOf("\"", pos1 + 1);
    model_output = line.substring(pos1 + 1, pos2);

    cross_validation = Boolean.valueOf(props.getProperty("Crossvalidation")).
        booleanValue();
    cycles = Integer.parseInt(props.getProperty("Cycles"));
    improve = Double.parseDouble(props.getProperty("Improve"));
    seed = Long.parseLong(props.getProperty("seed"));
    if (seed != -1) {
    	Randomize.setSeed(seed);
    }
    problem = props.getProperty("Problem");
    // bp_type = props.getProperty("BPtype");
    bp_type = "BPstd";
    test_data = Boolean.valueOf(props.getProperty("Test_data")).booleanValue();
    tipify_inputs = Boolean.valueOf(props.getProperty("Tipify_inputs")).
        booleanValue();
    verbose = Boolean.valueOf(props.getProperty("Verbose")).booleanValue();
    save = Boolean.valueOf(props.getProperty("SaveAll")).booleanValue();

    TestParameters();
  }

  /**
   * <p>
   * Checks if coefficients are coherents
   * </p>
   */
  private void TestParameters() {

    // Test the consistency of the parameters
    if (eta < 0.0 || alpha < 0.0 || lambda < 0.0) {
      System.out.println("Negative learning coefficients");
      System.exit(1);
    }

    if (cross_validation && !val_data) {
      System.out.println("Cross validation without validation data");
      System.exit(1);
    }

    if (cycles < 0) {
      System.out.println("Invalid negative values");
      System.exit(1);
    }

    if (bp_type.compareToIgnoreCase("BPstd") != 0 &&
        bp_type.compareToIgnoreCase("BPmax") != 0) {
      System.out.println("Not a valid bp type");
      System.exit(1);
    }

    for (int i = 0; i < Nhidden_layers + 1; i++) {
      if (transfer[i].compareToIgnoreCase("Log") != 0 &&
          transfer[i].compareToIgnoreCase("Lin") != 0 &&
          transfer[i].compareToIgnoreCase("Htan") != 0) {
        System.out.println("Transfer function not valid");
        System.exit(1);
      }
    }
  }
}

