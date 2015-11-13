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
 * KeelToPropertyList.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

/**
 * <p>
 * <b> KeelToPropertyList </b>
 * </p>
 *
* This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the PropertyList (xml) format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToPropertyList extends Exporter {

    /** KeelToPropertyList class Constructor.
     * Initializes the variable that stores the symbols used to identify null 
     * values
     */
    public KeelToPropertyList() {
        nullValue = "";
    }


    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * PropertyList format file which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutput PropertyList file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);

    }//end Start()

    /**
     * Method that creates the output file with PropertyList format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutput PropertyList file path to generate.
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int k;
        int type;
        int numInstances;
        String valueAttribute = new String();
        String filename = new String();
        String nameElement = new String();
        String labelType = new String();
        Element children;
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"á", "é", "í", "ó", "ú", "Á", "É", "Í", "Ó", "Ú"};



        /* Comprobamos si el nombre del fichero tiene la extensiÃ³n .xml, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".plist")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".plist");
        }
        Element root = new Element("plist");
        Document myDocument = new Document(root);


        org.jdom.Attribute attributePlist = new org.jdom.Attribute("version", "1.0");
        root.setAttribute(attributePlist);


        numInstances = data[0].size();

        for (i = 0; i < numInstances; i++) {

            Element childrenDict = new Element("dict");

            for (j = 0; j < numAttributes; j++) {
                type = attribute[j].getType();

                nameElement = attribute[j].getName();
                nameElement = nameElement.replace("\"", "");
                nameElement = nameElement.replace("'", "");

                if (nameElement.startsWith("\"") && nameElement.endsWith("\"")) {
                    nameElement = nameElement.substring(1, (nameElement.length()) - 1).replaceAll(" ", "_");
                }
                children = new Element("key").addContent(nameElement);

                childrenDict.addContent(children);

                valueAttribute = (String) data[j].elementAt(i);
                valueAttribute = valueAttribute.replace("\"", "");

                for (k = 0; k < vowel.length; k++) {
                    valueAttribute = valueAttribute.replace(vowel_accent[k], "&" + vowel[k] + "acute;");
                }
                if (valueAttribute.equals("?") || valueAttribute.equals("<null>")) {
                    valueAttribute = "";
                }
                if (type == INTEGER) {
                    labelType = "integer";
                }
                if (type == REAL) {
                    labelType = "real";
                }
                if (type == NOMINAL || type == -1) {
                    labelType = "string";
                }
                children = new Element(labelType).addContent(valueAttribute);

                childrenDict.addContent(children);
            }
            root.addContent(childrenDict);
        }

        try {

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(myDocument, new FileWriter(pathnameOutput));

            File f = new File(filename);

            System.out.println("Fichero " + f.getName() + " creado correctamente");

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


    }//end Savd()
}// end class KeelToPropertyList

