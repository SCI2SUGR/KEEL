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
 * KeelToXml.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom.*;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

/**
 * <p>
 * <b> KeelToXml </b>
 * </p>
 *
 * This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the XML format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToXml extends Exporter {


    /** KeelToXml class Constructor.
     * Initializes the variable that stores the symbols used to identify null 
     * values.
     */
    public KeelToXml() {
        nullValue = "";
    }

    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * XML format file which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutput XML file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        super.Start(pathnameInput);

        Save(pathnameOutput);

    }//end Start()

    /**
     * Method that creates the output file with XML format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutput XML file path to generate.
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int k;
        String value = new String();
        String filename = new String();
        String nameElement = new String();
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"á", "é", "í", "ó", "ú", "Á", "É", "Í", "Ó", "Ú"};

        /* Comprobamos si el nombre del fichero tiene la extensiÃ³n .xml, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".xml")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".xml");
        }
        Element root = new Element("root");
        Document myDocument = new Document(root);

        for (i = 0; i < data[0].size(); i++) {
            nameRelation = NameLabelValid(nameRelation);

            if (nameRelation.equals("")) {
                nameRelation = "root";
            }
            Element childrenRoot = new Element(nameRelation);

            for (j = 0; j < numAttributes; j++) {
                nameElement = attribute[j].getName();

                nameElement = NameLabelValid(nameElement);

                if (nameElement.equals("?") || nameElement.equals("<null>")) {
                    nameElement = "ATTRIBUTE_" + (j + 1) + "";
                }
                value = (String) data[j].elementAt(i);

                value = value.replace("\"", "");

                for (k = 0; k < vowel.length; k++) {
                    value = value.replace(vowel_accent[k], "&" + vowel[k] + "acute;");
                }
                Element children = new Element(nameElement).addContent(value);

                childrenRoot.addContent(children);
            }
            root.addContent(childrenRoot);
        }

        try {


            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());

            outputter.output(myDocument, new FileWriter(pathnameOutput));

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }


        File f = new File(filename);


        System.out.println("Fichero " + f.getName() + " creado correctamente");


    }//end Save()


    /**
     * Transforms an attribute name given of the KEEL file to a valid xml tag. 
     * @param nameLabelUser attribute name with KEEL format.
     * @return valid xml tag of the attribute name given.
     * @throws Exception
     */
    public String NameLabelValid(String nameLabelUser) throws Exception {
        String nameLabel = new String();

        nameLabel = nameLabelUser;

        Pattern p = Pattern.compile("\\s+");
        Matcher m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");

        nameLabel = nameLabel.replace("'", "");
        nameLabel = nameLabel.replace("\"", "");
        nameLabel = nameLabel.replace(" ", "_");
        nameLabel = nameLabel.replace(":", "_");
        nameLabel = nameLabel.replace(".", "_");
        nameLabel = nameLabel.replace("-", "_");

        String nameAux = nameLabel.toLowerCase();
        if (nameAux.startsWith("xml")) {
            nameLabel = nameLabel.substring(3);
        }
        p = Pattern.compile("[^A-ZÃa-zÃ±0-9_]+");
        m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");

        p = Pattern.compile("^[0-9]+");
        m = p.matcher(nameLabel);
        nameLabel = m.replaceAll("");


        return nameLabel;

    }//end NameLabelValid()
}// end KeelToXml

