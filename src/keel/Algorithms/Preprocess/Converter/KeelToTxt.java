/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
 * KeelToTxt.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.FileWriter;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> KeelToTxt </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a un fichero con formato Txt.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToTxt extends Exporter {


    /*
     * Constructor de la Clase KeelToTxt. Inicializa los valores
     * de las variables miembro nullValue (valor nulo) con el valor
     * del parámetro nullValueUser y la variable separator (el separador de los datos
     * del fichero Txt) con un carácter tabulador.
     *
     * @param  nullValueUser. Variable de tipo String con el valor nulo del fichero Txt .
     */
    public KeelToTxt(String nullValueUser) {
        nullValue = nullValueUser;
        separator = "\t";
    }


    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear el fichero de datos Txt indicado en el
     * parámetro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos
     * de salida con formato Txt.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);

    }//end Start()

    /*
     * Método utilizado para crear el fichero con formato Txt
     * (indicada la ruta por el parámetro pathnameOutput) a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato Txt.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {

        int i;
        String filename = new String();
        String element = new String();


        /* Comprobamos si el nombre del fichero tiene la extensión .txt, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".txt")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".txt");
        }
        FileWriter fileWriter = new FileWriter(filename);

        for (i = 0; i < numAttributes; i++) {
            String nameAttribute = attribute[i].getName();

            nameAttribute = nameAttribute.replace("'", "");

            if (nameAttribute.contains(" ")) {
                /**
                 * Cambio hecho para que los nominales con espacios en blanco se dejen
                 * con "_". Se añade la segunda linea
                 */
                nameAttribute = "\"" + nameAttribute + "\"";
                nameAttribute = nameAttribute.replace(" ", "_");
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

                element = element.replace("\t", " ");

                Pattern p = Pattern.compile("\\s+");
                Matcher m = p.matcher(element);

                if (m.find() && !element.equals("?") && !element.equals(nullValue) && attribute[j].getType() == NOMINAL) {
                    /**
                     * Cambio hecho para que los nominales con espacios en blanco se dejen
                     * con "_". Se añade la segunda linea y se comenta la primera
                     */
                    //element="\""+element+"\"";
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

        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");

    }//end Save()
}// end class KeelToTxt

