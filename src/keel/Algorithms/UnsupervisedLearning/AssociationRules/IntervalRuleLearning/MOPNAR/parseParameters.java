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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOPNAR;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

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
    private ArrayList <String> inputFiles;
    private String rulesFile;
    private ArrayList <String> outputFiles;
    private ArrayList <String> parameters;

    /**
     * Default constructor
     */
    public parseParameters() {
        inputFiles = new ArrayList<String>();
        outputFiles = new ArrayList<String>();
        parameters = new ArrayList<String>();

    }

    /**
     * It obtains all the necesary information from the configuration file.<br/>
     * First of all it reads the name of the input data-sets, training, validation and test.<br/>
     * Then it reads the name of the output files, where the training (validation) and test outputs will be stored<br/>
     * Finally it reads the parameters of the algorithm, such as the random seed.<br/>
     *
     * @param fileName Name of the configuration file
     *
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
        while(data.hasMoreTokens()){
            inputFiles.add(data.nextToken());
        }
    }

    /**
     * We read the output files for training and test and all the possible remaining output files
     * @param line StringTokenizer It is the line containing the output files.
     */
    private void readOutputFiles(StringTokenizer line){
        String new_line = line.nextToken(); //We read the input data line
        StringTokenizer data = new StringTokenizer(new_line, " = \" ");
        data.nextToken(); //inputFile
        rulesFile = data.nextToken();
        while(data.hasMoreTokens()){
            outputFiles.add(data.nextToken());
        }
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
        //If the algorithm is non-deterministic the first parameter is the Random SEED
    }

    
    /**
     * <p>       
     * It returns the name of the transactions input file
     * @return String the name of the transactions input file
     * </p>        
     */
    public String getTransactionsInputFile(){
        return this.transactionsFile;
    }

    /**
     * <p>       
     * It returns the name of the association rules file
     * @return String the name of the association rules file
     * </p>        
     */
    public String getAssociationRulesFile(){
        return this.rulesFile;
    }

    /**
     * <p>       
     * It returns the name of the algorithm
     * @return String the name of the algorithm
     * </p>        
     */
    public String getAlgorithmName(){
        return this.algorithmName;
    }

    /**
     * <p>       
     * It returns all the parameters as an array of Strings
     * @return String [] all the parameters of the algorithm
     * </p>        
     */
    public String [] getParameters(){
        String [] param = (String []) parameters.toArray();
        return param;
    }

    /**
     * <p>       
     * It returns the parameter in the position "pos"
     * @param pos int Position of the parameter
     * @return String [] the parameter of the algorithm in position "pos"
     * </p>        
     */
    public String getParameter(int pos){
        return (String)parameters.get(pos);
    }

    /**
     * <p>       
     * It returns all the input files
     * @return String [] all the input files
     * </p>        
     */
    public String [] getInputFiles(){
        return (String []) inputFiles.toArray();
    }

    /**
     * <p>       
     * It returns the input file in the position "pos"
     * @param pos int Position of the input file
     * @return String [] the input file of the algorithm in position "pos"
     * </p>        
     */
    public String getInputFile(int pos){
        return (String)this.inputFiles.get(pos);
    }

    /**
     * <p>       
     * It returns all the output files
     * @return String [] all the output files
     * </p>        
     */
    public String [] getOutputFiles(){
        return (String [])this.outputFiles.toArray();
    }

    /**
     * <p>       
     * It returns the output file in the position "pos"
     * @param pos int Position of the output file
     * @return String [] the output file of the algorithm in position "pos"
     * </p>        
     */
    public String getOutputFile(int pos){
        return (String)this.outputFiles.get(pos);
    }

}
