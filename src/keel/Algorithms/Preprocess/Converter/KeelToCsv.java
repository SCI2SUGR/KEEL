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

/*
 * KeelToCsv.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> KeelToCsv </b>
 * </p>
 * This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the CSV format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToCsv extends Exporter {


    /** KeelToCsv class Constructor.
     * Initializes the variables that store the symbols used to identify null 
     * values and separator between data.
     *
     * @param  nullValueUser. Null value symbols.
     * @param  separatorUser. Separator symbols used in the csv format.
     */
    public KeelToCsv(String nullValueUser, String separatorUser) {
        nullValue = nullValueUser;
        separator = separatorUser;
    }

    
    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * CSV format file which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutput CSV file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()


    
    /**
     * Method that creates the output file with CSV format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutput CSV file path to generate.
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutput) throws Exception {

        int i;
        String filename = new String();
        String element = new String();


        /* Comprobamos si el nombre del fichero tiene la extensiÃ³n .csv, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".csv")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".csv");
        }
        FileWriter fileWriter = new FileWriter(filename);

        for (i = 0; i < numAttributes; i++) {
            String nameAttribute = attribute[i].getName();

            nameAttribute = nameAttribute.replace("'", "");

            if (nameAttribute.contains(separator)) {
                nameAttribute = "\"" + nameAttribute + "\"";
            }
            if (i == (numAttributes - 1)) {
                fileWriter.write(nameAttribute + "\n");
            } else {
                fileWriter.write(nameAttribute + separator);
            }
        }

        for (i = 0; i < data[0].size(); i++) {
            for (int j = 0; j < numAttributes; j++) {
                element = (String) data[j].elementAt(i);

                Pattern p = Pattern.compile("[^A-ZÃa-zÃ±0-9_-]+");
                Matcher m = p.matcher(element);

                if ((m.find() && !element.equals("?") && !element.equals(nullValue) && attribute[j].getType() == NOMINAL) || element.contains(separator)) {
                    element = "\"" + element + "\"";
                }
                if (j == (numAttributes - 1)) {
                    fileWriter.write(element + "");
                } else {
                    fileWriter.write(element + separator);
                }
            }

            fileWriter.write("\n");
        }

        fileWriter.close();

        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");

    }//end Save()
}// end class KeelToCsv

