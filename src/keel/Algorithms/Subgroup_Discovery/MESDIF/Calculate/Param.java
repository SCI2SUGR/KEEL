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
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

public class Param {
    /**
     * <p>
     * This class manages the values of the variables defined by the parameters file
     * </p>
     */
    public String input_file_tra;   // Input mandatory file
    public String input_file_tst;   // Input mandatory file
    public String output_file_tra;   // Input mandatory file
    public String output_file_tst;   // Input mandatory file
    public String rul_file;         // Auxiliary input file with rules
    public String measure_file;     // Auxiliary output file for quality measures of the rules
    public int n_etiq;              // Number of labels
    public int long_poblacion;      // Number of rules

    /**
     * <p>
     * Create an instance Param
     * </p>
     * @param aoutput_file_tra   String with the name of the test file
     * @param aoutput_file_tst   String with the name of the test file
     * @param ainput_file_tra    String with the name of the test file
     * @param ainput_file_tst    String with the name of the test file
     * @param arule_file         String with the name of the rule file
     * @param aquality_file      String with the name of the output quality file
     * @param nlabels            Number of labels for continuous variable
     */
    public Param (String aoutput_file_tra, String aoutput_file_tst, String ainput_file_tra, String ainput_file_tst, String arule_file, String aquality_file, int nlabels) {
        output_file_tra = aoutput_file_tra;
        output_file_tst = aoutput_file_tst;
        input_file_tra = ainput_file_tra;
        input_file_tst = ainput_file_tst;
        rul_file = arule_file;
        measure_file = aquality_file;
        n_etiq = nlabels;
    }
}
