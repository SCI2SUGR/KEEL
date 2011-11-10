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

import java.util.StringTokenizer;
import java.util.ArrayList;

import org.core.Files;

public class parseParameters {
	/**
	 * <p>
	 * It reads the configuration file (data-set files and parameters)
	 * </p>
	 */

    private String algorithmName;
    private String transactionsFile;
    private String testFile;
    private String output_file_tra;
    private String output_file_tst;
    private String rule_file;
    private String measure_file;
    private ArrayList <String> parameters;

    /**
     * <p>
     * Default constructor
     * </p>
     */
    public parseParameters() {
        parameters = new ArrayList<String>();

    }

    /**
     * <p>
     * It obtains all the necesary information from the configuration file.<br/>
     * First of all it reads the name of the input data-sets, training, validation and test.<br/>
     * Then it reads the name of the output files, where the training (validation) and test outputs will be stored<br/>
     * Finally it reads the parameters of the algorithm, such as the random seed.<br/>
     * </p>
     * @param fileName Name of the configuration file
     */
    public void parseConfigurationFile(String fileName) {
        StringTokenizer line;
        String file = Files.readFile(fileName); //file is an string containing the whole file

        line = new StringTokenizer(file, "\n\r");
        readName(line); //We read the algorithm name
        readInputFiles(line); //We read all the input files
        readOutputFiles(line); //We read all the output files
        readAllParameters(line); //We read all the possible parameters

    };

    /**
     * It reads the name of the algorithm from the configuration file
     * @param line StringTokenizer It is the line containing the algorithm name.
     */
    private void readName(StringTokenizer line){
        StringTokenizer data = new StringTokenizer(line.nextToken(), " = \" ");
        data.nextToken();
        algorithmName = new String(data.nextToken());
        while(data.hasMoreTokens()){
            algorithmName += " "+data.nextToken(); //We read the algorithm name
        }
    }

    /**
     * We read the input data-set files and all the possible input files
     * @param line StringTokenizer It is the line containing the input files.
     */
    private void readInputFiles(StringTokenizer line){
        String new_line = line.nextToken(); //We read the input data line
        StringTokenizer data = new StringTokenizer(new_line, " = \" ");
        data.nextToken(); //inputFile
        transactionsFile = data.nextToken();
        testFile = data.nextToken();
    }

    /**
     * We read the output files for training and test and all the possible remaining output files
     * @param line StringTokenizer It is the line containing the output files.
     */
    private void readOutputFiles(StringTokenizer line){
        String new_line = line.nextToken(); //We read the input data line
        StringTokenizer data = new StringTokenizer(new_line, " = \" ");
        data.nextToken(); //inputFile
        output_file_tra = data.nextToken();
        output_file_tst = data.nextToken();
        rule_file = data.nextToken();
        measure_file = data.nextToken();
    }

    /**
     * We read all the possible parameters of the algorithm
     * @param line StringTokenizer It contains all the parameters.
     */
    private void readAllParameters(StringTokenizer line){
        String new_line,cadena;
        StringTokenizer data;
        while (line.hasMoreTokens()) { //While there is more parameters...
            new_line = line.nextToken();
            data = new StringTokenizer(new_line, " = ");
            cadena = new String("");
            while (data.hasMoreTokens()){
                cadena = data.nextToken(); //parameter name
            }
            parameters.add(cadena); //parameter value
        }
    }

    /**
     * <p>
     * Gets the name of the train file 
     * </p>
     * @return          A string with the name of the file
     */
    public String getTransactionsInputFile(){
        return this.transactionsFile;
    }

    /**
     * <p>
     * Gets the name of the test file
     * </p>
     * @return          A string with the name of the file
     */
    public String getTestInputFile(){
        return this.testFile;
    }

    /**
     * <p>
     * Gets the name of the rule file
     * </p>
     * @return          A string with the name of the file
     */
    public String getAssociationRulesFile(){
        return this.rule_file;
    }

    /**
     * <p>
     * Gets the name of the measure file
     * </p>
     * @return          A string with the name of the file
     */
    public String getAssociationMeasuresFile(){
        return this.measure_file;
    }

    /**
     * <p>
     * Gets the name of the output for train file
     * </p>
     * @return          A string with the name of the file
     */
    public String getOutputFileTra(){
        return this.output_file_tra;
    }

    /**
     * <p>
     * Gets the name of the output for test file
     * </p>
     * @return          A string with the name of the file
     */
    public String getOutputFileTst(){
        return this.output_file_tst;
    }

    /**
     * <p>
     * Gets the name of the algorithm
     * </p>
     * @return          A string with the name of the algorithm
     */
    public String getAlgorithmName(){
        return this.algorithmName;
    }

    /**
     * <p>
     * Gets an array with the parameters used by the algorithm
     * </p>
     * @return          An array of string
     */
    public String [] getParameters(){
        String [] param = (String []) parameters.toArray();
        return param;
    }

    /**
     * <p>
     * Gets a string with the parameter of a determined position used by the algorithm
     * </p>
     * @param pos       Position of the parameter
     * @return          A string with the value of the parameter
     */
    public String getParameter(int pos){
        return (String)parameters.get(pos);
    }

}
