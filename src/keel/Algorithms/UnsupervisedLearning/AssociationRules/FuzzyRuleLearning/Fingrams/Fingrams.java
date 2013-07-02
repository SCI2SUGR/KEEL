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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.core.*;
import keel.Dataset.*;

public class Fingrams {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

    private myDataset dataset;
	private DataBase database;
	private FingramsProcess fingramsProcess;
    
    private String rulesBaseFile;
    private String dataBaseFile;
    private String fingramsFile;
	private String fileTime, fileHora, namedataset;
	
	long startTime, totalTime;
    
	private double blankThreshold;
    
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Fingrams() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Fingrams(parseParameters parameters) {
    	this.startTime = System.currentTimeMillis();
        this.dataset = new myDataset();
        
        try {
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
            this.dataset.readDataSet(parameters.getTransactionsInputFile());
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasNumericalAttributes();
		this.somethingWrong = this.somethingWrong || this.dataset.hasMissingAttributes();
    	
        this.dataBaseFile = parameters.getInputFile(0);
        this.rulesBaseFile = parameters.getInputFile(1);
		this.fingramsFile = parameters.getOutputFile(0);
	


        this.fileTime = this.fingramsFile.substring(0, this.fingramsFile.lastIndexOf('/')) + "/time.txt";
        this.fileHora = this.fingramsFile.substring(0, this.fingramsFile.lastIndexOf('/')) + "/hora.txt";
		this.namedataset = this.dataset.getRelationName();
       
        this.blankThreshold = Double.parseDouble(parameters.getParameter(0));
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } 
		else {
			this.database = new DataBase (this.dataBaseFile, this.dataset);
			this.fingramsProcess = new FingramsProcess(this.dataset, this.database);
			String out = this.fingramsProcess.generateFile(rulesBaseFile, blankThreshold);
        	
			try {				
				PrintWriter fingrams_writer = new PrintWriter(this.fingramsFile);
				fingrams_writer.print("" + out);				
				fingrams_writer.close();
				
				System.out.println("\nAlgorithm Finished");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    }
}