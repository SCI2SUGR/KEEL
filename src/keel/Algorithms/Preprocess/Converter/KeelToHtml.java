/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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
 * KeelToHtml.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import org.jdom.*;
import org.jdom.output.*;
import org.jdom.output.XMLOutputter;

/**
 * <p>
 * <b> KeelToHtml </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a una tabla en un fichero con
 * formato html.
 *
 * @author Teresa Prieto L√≥pez (UCO)
 * @version 1.0
 */
public class KeelToHtml extends Exporter {

    /*
     * Constructor de la Clase KeelToHtml. Inicializa el valor
     * de la variable miembro nullValue (valor nulo para un dato de la tabla html).
     * Este valor se inicializa con la cadena <null>.
     *
     */
    public KeelToHtml() {
        nullValue = "<null>";
    }


    /*
     * Este m√©todo llama al m√©todo Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al m√©todo Save() para crear una tabla en un fichero con formato html indicado en el
     * par√°metro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos de salida
     * con formato html.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()

    /*
     * M√©todo utilizado para crear una tabla en un fichero html
     * indicada la ruta por el par√°metro pathnameOutput. Este fichero se crea a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato html.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int k;
        String value = new String();
        String filename = new String();
        String nameElement = new String();
        org.jdom.Attribute attributesTable;
        Element childrenRoot;
        Element children;
        String vowel[] = {"a", "e", "i", "o", "u", "A", "E", "I", "O", "U"};
        String vowel_accent[] = {"·", "È", "Ì", "Û", "˙", "¡", "…", "Õ", "”", "⁄"};



        /* Comprobamos si el nombre del fichero tiene la extensi√≥n .xml, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".html")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".html");
        }
        Element root = new Element("html");
        Document myDocument = new Document(root);

        Element head = new Element("head");
        Element h1 = new Element("h1");
        attributesTable = new org.jdom.Attribute("align", "center");
        h1.setAttribute(attributesTable);
        nameRelation = nameRelation.replace("\"", "");
        nameRelation = nameRelation.replace("'", "");
        h1.addContent(nameRelation.toUpperCase());
        head.addContent(h1);
        root.addContent(head);


        Element body = new Element("body");
        root.addContent(body);

        Element table = new Element("table");

        attributesTable = new org.jdom.Attribute("border", "1");
        table.setAttribute(attributesTable);
        attributesTable = new org.jdom.Attribute("cellspacing", "1");
        table.setAttribute(attributesTable);
        attributesTable = new org.jdom.Attribute("cellpadding", "0");
        table.setAttribute(attributesTable);

        body.addContent(table);


        childrenRoot = new Element("tr");
        attributesTable = new org.jdom.Attribute("align", "center");
        childrenRoot.setAttribute(attributesTable);

        for (i = 0; i < numAttributes; i++) {
            value = attribute[i].getName();
            value = value.replace("'", "");
            value = value.replace("\"", "");

            if (value.equals("?") || value.equals("<null>")) {
                value = "ATTRIBUTE_" + (i + 1) + "";
            }
            children = new Element("td").addContent(value);

            childrenRoot.addContent(children);
        }
        table.addContent(childrenRoot);


        for (i = 0; i < data[0].size(); i++) {
            childrenRoot = new Element("tr");

            for (j = 0; j < numAttributes; j++) {
                value = (String) data[j].elementAt(i);
                value = value.replace("\"", "");

                for (k = 0; k < vowel.length; k++) {
                    value = value.replace(vowel_accent[k], "&" + vowel[k] + "acute;");
                }
                children = new Element("td").addContent(value);

                childrenRoot.addContent(children);
            }
            table.addContent(childrenRoot);
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
}// end class KeelToHtml

