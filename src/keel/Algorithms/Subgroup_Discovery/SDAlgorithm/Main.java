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
 * @author Writed by Alberto Fernández (University of Granada) 15/01/2006
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 24/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

import java.util.StringTokenizer;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.core.*;

public class Main {

    /**
     * <p>
     * Title: Main class of the SD algorithm
     * </p>
     */
    
    private String input_file_tra;
    private String input_file_eval;
    private String input_file_tst;

    private String output_file_tra;
    private String output_file_tst;
    private String rule_file;
    private String measure_file;

    private String nameAlgorithm;
    private int beamWidth;
    private int g;
    private float minSupp;
    private int numRules;

    /**
     * <p>
     * Constructor
     * </p>
     */
    public Main() {
    }

    /**
     * <p>
     * Auxiliar Gets the name for the output files, eliminating "" and skiping "="
     * </p>
     * @param s                 String of the output files
     */
    private void GetOutputFiles(StringTokenizer s) {
        String val   = s.nextToken(); // skip "="
        output_file_tra = s.nextToken().replace('"',' ').trim();
        output_file_tst = s.nextToken().replace('"',' ').trim();
        rule_file    = s.nextToken().replace('"',' ').trim();
        measure_file    = s.nextToken().replace('"',' ').trim();
    }

    /**
     * <p>
     * Auxiliar Gets the name for the input files, eliminating "" and skiping "="
     * </p>
     * @param s                 String of the input files
     */
    private void GetInputFiles(StringTokenizer s) {
        String val   = s.nextToken(); // skip "="
        input_file_tra = s.nextToken().replace('"',' ').trim();
        input_file_eval = s.nextToken().replace('"',' ').trim();
        input_file_tst = s.nextToken().replace('"',' ').trim();
    }

    /**
     * <p>
     * Auxiliar Gets the name for the input files, eliminating "" and skiping "="
     * </p>
     * @param nFile                 String of the input files
     */
    public void ReadParameters (String nFile) {

        try {
            int nl;  // Aux var to reed the param file
            String file, linea, tok;
            StringTokenizer lineasFichero, tokens;
            file = Files.readFile(nFile);
            file = file.toLowerCase() + "\n ";
            lineasFichero = new StringTokenizer(file,"\n\r");

            for (nl=0, linea=lineasFichero.nextToken(); lineasFichero.hasMoreTokens(); linea=lineasFichero.nextToken()) {
                nl++;
                tokens = new StringTokenizer(linea," ,\t");
                if (tokens.hasMoreTokens()) {
                    tok = tokens.nextToken();
                    if (tok.equalsIgnoreCase("algorithm"))
                        nameAlgorithm = Utils.GetParamString(tokens);
                    else if (tok.equalsIgnoreCase("inputdata"))
                        GetInputFiles(tokens);
                    else if (tok.equalsIgnoreCase("outputdata"))
                        GetOutputFiles(tokens);
                    else if (tok.equalsIgnoreCase("beamWidth"))
                        beamWidth = Utils.GetParamInt(tokens);
                    else if (tok.equalsIgnoreCase("g"))
                        g = Utils.GetParamInt(tokens);
                    else if (tok.equalsIgnoreCase("minSupp"))
                        minSupp = Utils.GetParamFloat(tokens);
                    else if (tok.equalsIgnoreCase("numRules"))
                        numRules = Utils.GetParamInt(tokens);
                    else  throw new IOException("Syntax error on line "+nl+": ["+tok+"]\n");
                }
            }
        }
	catch(FileNotFoundException e) {
            System.err.println(e+" Parameter file");
        }
        catch(IOException e) {
            System.err.println(e+"Aborting program");
            System.exit(-1);
        }
    }


    /**
     * <p>
     * This method launch the SD algorithm
     * </p>
     */
    private void execute() {
        SD sd = new SD(input_file_tra, input_file_eval, input_file_tst, output_file_tra, output_file_tst,
               rule_file, measure_file, nameAlgorithm, beamWidth, g, minSupp, numRules);
        if (sd.isOk()){
            sd.execute();
        }
    }

    /**
     * <p>
     * Main method
     * </p>
     * @param args          It has the file name with the parameters
     */
    public static void main(String args[]) {
        Main main = new Main();
        main.ReadParameters(args[0]);
        System.out.println("Launching SD_algorithm.");
        main.execute();
    }
}
