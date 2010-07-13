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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

import java.util.StringTokenizer;
import java.util.ArrayList;
import org.core.Fichero;

/**
 * <p>Title: Parse Configuration File</p>
 *
 * <p>Description: It reads the configuration file (data-set files and parameters)</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class parseParameters {

    private String algorithmName;
    private String trainingFile, validationFile, testFile;
    private ArrayList <String> inputFiles;
    private String outputTrFile, outputTstFile;
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
     * Finally it read the parameters of the algorithm, such as the random seed.<br/>
     *
     * @param fileName Name of the configuration file
     *
     */
    public void parseConfigurationFile(String fileName) {
        StringTokenizer line;
        String file = Fichero.leeFichero(fileName); //file is an string containing the whole file

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
        trainingFile = data.nextToken();
        validationFile = data.nextToken();
        testFile = data.nextToken();
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
        outputTrFile = data.nextToken();
        outputTstFile = data.nextToken();
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

    public String getTrainingInputFile(){
        return this.trainingFile;
    }

    public String getTestInputFile(){
        return this.testFile;
    }

    public String getValidationInputFile(){
        return this.validationFile;
    }

    public String getTrainingOutputFile(){
        return this.outputTrFile;
    }

    public String getTestOutputFile(){
        return this.outputTstFile;
    }

    public String getAlgorithmName(){
        return this.algorithmName;
    }

    public String [] getParameters(){
        String [] param = (String []) parameters.toArray();
        return param;
    }

    public String getParameter(int pos){
        return (String)parameters.get(pos);
    }

    public String [] getInputFiles(){
        return (String []) inputFiles.toArray();
    }

    public String getInputFile(int pos){
        return (String)this.inputFiles.get(pos);
    }

    public String [] getOutputFiles(){
        return (String [])this.outputFiles.toArray();
    }

    public String getOutputFile(int pos){
        return (String)this.outputFiles.get(pos);
    }

}

