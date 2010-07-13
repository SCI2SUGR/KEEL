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
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a un fichero codificado en
 * formato Uci o C4.5. Los datos codificados según este formato están
 * agrupados de tal manera contienen dos ficheros, un fichero de
 * nombres con extensión ".names" y un fichero de datos con extensión
 * ".data".
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToUci extends Exporter {


    /*
     * Constructor de la Clase KeelToUci. Inicializa los valores
     * de las variables miembro nullValue (valor nulo para para el fichero de datos) con el valor
     * del parámetro nullValueUser y la variable separator (el separador de los datos
     * del fichero de datos ".data") con el valor del parámetro separatorUser.
     *
     * @param  nullValueUser. Variable de tipo String con el valor nulo del fichero
     * de datos ".data" .
     *
     * @param  separatorUser. Variable de tipo String con el separador de los datos
     * para el fichero de datos ".data".
     */
    public KeelToUci(String nullValueUser, String separatorUser) {

        nullValue = nullValueUser;
        separator = separatorUser;

    }


    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear los ficheros de datos con formato C4.5 indicado
     * con los parámetros pathnameOutputData (fichero de datos con extensión ".data")
     * y pathnameOutputNames (fichero de nombres con extensión ".names").
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     *
     * @param  String pathnameOutputData Variable con la ruta del fichero de datos de salida
     * con formato C4.5. (fichero ".data").
     *
     * @param  String pathnameOutputNames Variable con la ruta del fichero de nombres de salida
     * con formato C4.5. (fichero ".names").
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutputData, String pathnameOutputNames) throws Exception {
        super.Start(pathnameInput);

        Save(pathnameOutputData, pathnameOutputNames);


    }//end Start()

    /*
     * Método utilizado para crear los ficheros con formato UCI (fichero
     * de nombres con extensión ".names" y fichero de datos con extensión
     * ".data") indicados por los parámetros pathnameOutputNames y pathnameOutputData.
     * Este fichero se crea a partir  de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutputNames. Variable de tipo String con
     * la ruta del fichero de nombres (".names") de salida.
     *
     * @param String pathnameOutputData. Variable de tipo String con
     * la ruta del fichero de datos (".data") de salida.
     *
     * @throws Exception.
     *
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

                Pattern p = Pattern.compile("[^A-ZÑa-zñ0-9_-]+");
                Matcher m = p.matcher(element);

                if ((m.find() && !element.equals("?") && !element.equals(nullValue) && attribute[j].getType() == NOMINAL) || element.contains(separator)) /**
                 * Cambio hecho para que los nominales con espacios en blanco se dejen
                 * con "_". Se añade la segunda linea y se comenta la primera
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


