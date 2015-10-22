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


package keel.Algorithms.Decision_Trees.C45;

import java.io.*;
import keel.Dataset.Attributes;


/**
* This is the main class
* 
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/
public abstract class Algorithm {
    /** The name of the file that contains the information to build the model. */
    protected static String modelFileName = "";

    /** The name of the file that contains the information to make the training. */
    protected static String trainFileName = "";

    /** The name of the file that contains the information to make the test. */
    protected static String testFileName = "";

    /** The name of the train output file. */
    protected static String trainOutputFileName;

    /** The name of the test output file. */
    protected static String testOutputFileName;

    /** The name of the result file. */
    protected static String resultFileName;

    /** Correctly classified itemsets. */
    protected int correct = 0;

    /** Correctly classified in test. */
    protected int testCorrect = 0;

    /** The model dataset. */
    protected Dataset modelDataset;

    /** The train dataset. */
    protected Dataset trainDataset;

    /** The test dataset. */
    protected Dataset testDataset;

    /** The log file. */
    protected static BufferedWriter log;

    /** The instant of starting the algorithm. */
    protected long startTime = System.currentTimeMillis();

    /** Function to initialize the stream tokenizer.
     *
     * @param tokenizer		The tokenizer.
     */
    protected void initTokenizer(StreamTokenizer tokenizer) {
        tokenizer.resetSyntax();
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.wordChars(' ' + 1, '\u00FF');
        tokenizer.whitespaceChars(',', ',');
        tokenizer.quoteChar('"');
        tokenizer.quoteChar('\'');
        tokenizer.ordinaryChar('=');
        tokenizer.ordinaryChar('{');
        tokenizer.ordinaryChar('}');
        tokenizer.ordinaryChar('[');
        tokenizer.ordinaryChar(']');
        tokenizer.eolIsSignificant(true);
    }


    /** Function to get the name of the relation and the names, types and possible values of every attribute in
     *  a dataset.
     *
     * @return The name and the attributes of the relation.
     */
    protected String getHeader() {
        String header;
        header = "@relation " + Attributes.getRelationName() + "\n";
        header += Attributes.getInputAttributesHeader();
        header += Attributes.getOutputAttributesHeader();
        header += Attributes.getInputHeader() + "\n";
        header += Attributes.getOutputHeader() + "\n";
        header += "@data\n";

        return header;
    }


    /** Puts the tokenizer in the first token of the next line.
     *
     * @param tokenizer		The tokenizer which reads this function.
     *
     * @return				True if reaches the end of file. False otherwise.
     *
     * @throws Exception	If cannot read the tokenizer.
     */
    protected boolean getNextToken(StreamTokenizer tokenizer) throws Exception {
        try {
            if (tokenizer.nextToken() == StreamTokenizer.TT_EOF) {
                return false;
            } else {
                tokenizer.pushBack();
                while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {
                    ;
                } while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
                    ;
                }

                if (tokenizer.sval == null) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return false;
        }
    }


    /** Function to read the options from the execution file and assign the values to the parameters.
     *
     * @param options 		The StreamTokenizer that reads the parameters file.
     *
     * @throws Exception	If the format of the file is not correct.
     */
    protected abstract void setOptions(StreamTokenizer options) throws
            Exception;

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
