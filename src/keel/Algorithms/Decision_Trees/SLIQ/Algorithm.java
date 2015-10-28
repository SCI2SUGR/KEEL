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

package keel.Algorithms.Decision_Trees.SLIQ;
import java.io.*;

import keel.Dataset.Attributes;

/**
 * Abstract class of the algorithm implemented.
 */
public abstract class Algorithm {
	/** File's name which contains all the information needed to build the model. */
	protected static String modelFileName = "";	 
	
	/** Training dataset file's name. */
	protected static String trainFileName = "";	 
	
	/** Test dataset file's name. */
	protected static String testFileName = "";	
	
	/** Name of the training output file. */
	protected static String trainOutputFileName;
	
	/** Name of the test output file. */
	protected static String testOutputFileName;	
	
	/** Results file's name. */
	protected static String resultFileName;		
	
	/** Number of correctly classified example from training dataset. */
	protected int correct = 0;					
	
	/** Number of correctly classified example from test dataset. */
	protected int testCorrect = 0;				
	
	/** Model dataset. */
	protected Dataset modelDataset;
	
	/** Training dataset. */
	protected Dataset trainDataset;				
	
	/** Test dataset. */
	protected Dataset testDataset;				
	
	/** Log buffer. */
	protected static BufferedWriter log;		
	
	/** Starting time of the execution. */
	protected long startTime = System.currentTimeMillis();
	
	/** Function to initialize the stream tokenizer.
     *
     * @param tokenizer		The tokenizer.
     */
 	protected void initTokenizer(StreamTokenizer tokenizer) {
 		tokenizer.resetSyntax();         
 		tokenizer.whitespaceChars( 0, ' ' );    
 		tokenizer.wordChars( ' '+1,'\u00FF' );
 		tokenizer.whitespaceChars( ',',',' );
 		tokenizer.quoteChar( '"' );
 		tokenizer.quoteChar( '\''  );
 		tokenizer.ordinaryChar( '=' );
 		tokenizer.ordinaryChar( '{' );
 		tokenizer.ordinaryChar( '}' );
 		tokenizer.ordinaryChar( '[' );
 	  	tokenizer.ordinaryChar( ']' );
 	  	tokenizer.eolIsSignificant( true );
 	}
  	 

 	/** Function to get the name of the relation and the names, types and possible values of every attribute in
     *  a dataset.
     *
     * @return The name and the attributes of the relation.
     */
	protected String getHeader() {
		String header;		
		header = "@relation "+Attributes.getRelationName()+"\n";
	    header += Attributes.getInputAttributesHeader();
	    header += Attributes.getOutputAttributesHeader();
	    header += Attributes.getInputHeader()+"\n";
	    header += Attributes.getOutputHeader()+"\n";
	    header += "@data\n";
			
		return header;
	}	
	
	/** Function to read the options from the execution file and assign the values to the parameters.
     *
     * @param options 		The StreamTokenizer that reads the parameters file.
     *
     * @throws Exception	If the format of the file is not correct.
     */
	protected abstract void setOptions(StreamTokenizer options)  throws Exception;
	
    /** Evaluates the algorithm and writes the results in the file.
     *
     * @throws java.io.IOException If the file cannot be written.
     */
	protected abstract void printResult() throws IOException;
	
    /** Evaluates the test dataset and writes the results in the file.
     *
     * @throws java.io.IOException If the file cannot be written.
     */
	protected abstract void printTest() throws IOException;
	
    /** Evaluates the training dataset and writes the results in the file.
     *
     * @throws java.io.IOException If the file cannot be written.
     */
	protected abstract void printTrain() throws IOException;
}

