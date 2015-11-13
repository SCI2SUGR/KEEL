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
 * KeelToDif.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;

/**
 * <p>
 * <b> KeelToDif </b>
 * </p>
 *
 * This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the DIF format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToDif extends Exporter {


    /** KeelToDif class Constructor.
     * Initializes the variable that stores the symbols used to identify null 
     * values.
     */
    public KeelToDif() {
        nullValue = "";
    }

    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * DIF format file which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutput DIF file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()

    /**
     * Method that creates the output file with DIF format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutput DIF file path to generate.
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int type = -1;
        int numInstances = 0;
        String filename = new String();
        String element = new String();


        /* Comprobamos si el nombre del fichero tiene la extension .csv, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".dif")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".dif");
        }
        numInstances = data[0].size();

        FileWriter writer = new FileWriter(filename);

//CREAMOS LA CABECERA
        writer.write("TABLE\n");
        writer.write("0,1\n");
        writer.write("\"EXCEL\" \n");

        writer.write("VECTORS\n");
        writer.write("0," + numInstances + "\n");
        writer.write("\"\"" + "\n");

        writer.write("TUPLES\n");
        writer.write("0," + numAttributes + "\n");
        writer.write("\"\"\n");

        writer.write("DATA\n");
        writer.write("0,0\n");
        writer.write("\"\"" + "\n");


        writer.write("-1,0\n");
        writer.write("BOT\n");

        for (i = 0; i < numAttributes; i++) {
            element = (String) attribute[i].getName();

            if (!element.startsWith("\"") && !element.endsWith("\"")) {
                element = "\"" + element + "\"";
            }
            writer.write("1,0\n");
            writer.write(element + "\n");

        }

        for (i = 0; i < numInstances; i++) {
            writer.write("-1,0\n");
            writer.write("BOT\n");

            for (int j = 0; j < numAttributes; j++) {
                type = attribute[j].getType();
                element = (String) data[j].elementAt(i);

                element = element.replace("'", "");


                if (type == NOMINAL || type == -1) {
                    if (!element.startsWith("\"") && !element.endsWith("\"")) {
                        element = "\"" + element + "\"";
                    }
                    writer.write("1,0\n");
                    writer.write(element + "\n");
                }

                if (type == REAL || type == INTEGER) {
                    if (element.startsWith(".")) {
                        element = "0" + element;
                    /**
                     * Cambio realizado para que no ponga los decimales con comas, sino con puntos.
                     * Se suprime la siguiente linea.
                     */
                    //element=element.replace(".",",");
                    }
                    writer.write("0," + element + "\n");
                    writer.write("V\n");
                }

            }

        }

        writer.write("-1,0\n");
        writer.write("EOD");

        writer.close();


        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");

    }//end Save()
}// end class KeelToDif

