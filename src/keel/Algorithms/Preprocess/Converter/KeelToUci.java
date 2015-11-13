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
 * KeelToUci.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> KeelToUci </b>
 * </p>
 *
 * This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the UCI format. 
 * This format has two different files:
 *  - Name file that stores all the names of the attributes and class ".names".
 *  - Data file with all the data ".data".
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToUci extends Exporter {


    /** KeelToUci class Constructor.
     * Initializes the variables that store the symbols used to identify null 
     * values and separator between data.
     *
     * @param  nullValueUser. Null value symbols.
     * @param  separatorUser. Separator symbols used in the csv format.
     */
    public KeelToUci(String nullValueUser, String separatorUser) {

        nullValue = nullValueUser;
        separator = separatorUser;

    }


    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * UCI format files which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutputData UCI data file path.
     * @param pathnameOutputNames UCI names file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutputData, String pathnameOutputNames) throws Exception {
        super.Start(pathnameInput);

        Save(pathnameOutputData, pathnameOutputNames);


    }//end Start()

    /**
     * Method that creates the output files with UCI format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutputNames UCI names file path to generate (".names"). 
     * @param pathnameOutputData UCI data file path to generate (".data"). 
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutputNames, String pathnameOutputData) throws Exception {
        Attribute attributeCurrent = new Attribute();
        FileWriter fileWriter;
        String filenameNames = new String();
        String filenameData = new String();
        String nameAttribute = new String();
        String ending = new String();
        int i;
        int j;
        int type;
        int numNominalValues;


// Comprobamos si el nombre del fichero de nombre tiene la extension .names
        if (pathnameOutputNames.endsWith(".names")) {
            filenameNames = pathnameOutputNames;
        } else {
            filenameNames = pathnameOutputNames.concat(".names");// Comprobamos si el nombre del fichero de datos tiene la extension .data
        }
        if (pathnameOutputData.endsWith(".data")) {
            filenameData = pathnameOutputData;
        } else {
            filenameData = pathnameOutputData.concat(".data");
        }
        fileWriter = new FileWriter(filenameNames);

        nameRelation = nameRelation.replace(":", "\\:");
        nameRelation = nameRelation.replace(",", "\\,");
        nameRelation = nameRelation.replace("'", "\\'");
        nameRelation = nameRelation.replace(".", "");
        nameRelation = nameRelation.replace("|", "");


        fileWriter.write(nameRelation + "\n");


        for (i = 0; i < numAttributes; i++) {
            attributeCurrent = attribute[i];

            nameAttribute = attributeCurrent.getName();

            nameAttribute = nameAttribute.replace(" ", "");
            nameAttribute = nameAttribute.replace(":", "");
            nameAttribute = nameAttribute.replace(",", "");
            nameAttribute = nameAttribute.replace("'", "");
            nameAttribute = nameAttribute.replace("|", "");
            nameAttribute = nameAttribute.replace(".", "");


            type = attributeCurrent.getType();

            String aux = nameAttribute + ": ";

            switch (type) {
                case 0:
                    numNominalValues = attributeCurrent.getNumNominalValues();

                    if (numNominalValues < 10) {
                        ending = ",";
                        for (j = 0; j < numNominalValues; j++) {
                            if (j == attributeCurrent.getNumNominalValues() - 1) {
                                ending = "";
                            }
                            aux += (String) attributeCurrent.getNominalValue(j) + ending;
                        }
                        aux += '.';

                    } else {
                        aux += "discrete <" + numNominalValues + ">.";
                    }
                    break;
                case 1:
                    aux += "continuous.";
                    break;
                case 2:
                    aux += "continuous.";
                    break;

            }

            fileWriter.write(aux + "\n");
        }

        fileWriter.close();


        fileWriter = new FileWriter(filenameData);

        for (i = 0; i < data[0].size(); i++) {
            for (j = 0; j < numAttributes; j++) {
                String element = (String) data[j].elementAt(i);

                Pattern p = Pattern.compile("[^A-ZÃa-zÃ±0-9_-]+");
                Matcher m = p.matcher(element);

                if ((m.find() && !element.equals("?") && !element.equals(nullValue) && attribute[j].getType() == NOMINAL) || element.contains(separator)) /**
                 * Cambio hecho para que los nominales con espacios en blanco se dejen
                 * con "_". Se aÃ±ade la segunda linea y se comenta la primera
                 */
                //element="\""+element+"\"";
                {
                    element = element.replace(" ", "_");
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




        File file = new File(filenameNames);
        System.out.println("Fichero " + file.getName() + " creado correctamente");

        file = new File(filenameData);
        System.out.println("Fichero " + file.getName() + " creado correctamente");

    }//end Save()
}//end class KeelToUci


