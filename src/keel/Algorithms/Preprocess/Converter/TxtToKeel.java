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
 * TxtToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> TxtToKeel </b>
 * </p>
 *
 *  Clase extendida de la clase Importer. Esta clase se utiliza
 *  para leer datos localizados en ficheros con formato Txt
 * (datos separados por tabuladores) y convertirlos a formato keel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class TxtToKeel extends Importer {

    /*
     * Constructor de la Clase TxtToKeel. Inicializa los valores
     * de las variables miembro nullValue (valor nulo) con el valor del parámetro
     * nullValueUser y la variable miembro separator (el separador de los datos)
     * al carácter tabulador.
     *
     * @param nullValueUser. Variable de tipo String con el valor nulo del fichero Txt.
     *
     */
    public TxtToKeel(String nullValueUser) {
        separator = "\t";
        nullValue = nullValueUser;
    }

    /* Metodo utilizado para convertir los datos del fichero Txt indicado
     * mediante la variable pathnameInput a formato keel en el fichero
     * indicado por la ruta pathnameOutput
     *
     * @param pathnameInput ruta con los datos en formato csv
     * @param pathnameOutput ruta para el fichero de datos Keel.
     *
     * @throws Exception */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        BufferedReader reader;
        Pattern p;
        Matcher m;
        File f;
        StringTokenizer token = null;
        String line = new String();
        String names = new String();
        String lineAux = new String();
        String lineAux2 = new String();
        String element = new String();
        int i = 0;
        int j = 0;
        int type = -1;
        int actualValueInt;
        double actualValue;
        double min;
        double max;


        File fileInput = new File(pathnameInput);

        reader = new BufferedReader(new FileReader(pathnameInput));
        BufferedWriter auxFile = new BufferedWriter(new FileWriter("temp"));


        while ((line = reader.readLine()) != null && line != "") {
            lineAux = "";
            lineAux2 = "";

            token = new StringTokenizer(line, separator);
            int numToken = token.countTokens();

            for (i = 0; i < numToken; i++) {
                p = Pattern.compile(separator + "" + separator);
                m = p.matcher(line);
                line = m.replaceAll(separator + "?" + separator);
            }

            p = Pattern.compile("^" + separator);
            m = p.matcher(line);
            line = m.replaceAll("?" + separator);

            p = Pattern.compile(separator + "$");
            m = p.matcher(line);
            line = m.replaceAll(separator + "?");


            token = new StringTokenizer(line, separator);

            while (token.hasMoreTokens()) {
                lineAux = token.nextToken();

                if (lineAux.equals(nullValue) || lineAux.equals("<null>") || lineAux.equals("") || lineAux == null) {
                    lineAux2 = lineAux2.concat("?");
                } else {
                    lineAux2 = lineAux2.concat(lineAux);
                }
                if (token.hasMoreTokens()) {
                    lineAux2 = lineAux2.concat(separator);
                }
            }

            auxFile.write(lineAux2 + "\n");

        }
        auxFile.close();
        reader.close();


        /* Leemos la primera linea con los nombres
        de los atributos para obtener el numero de atributos  */

        reader = new BufferedReader(new FileReader("temp"));
        names = reader.readLine();

        token = new StringTokenizer(names, separator);
        numAttributes = token.countTokens();


//Reservamos memoria para almacenar la definición de los atributos y de los datos
        attribute = new Attribute[numAttributes];
        data = new Vector[numAttributes];
        types = new Vector[numAttributes];

        for (i = 0; i < numAttributes; i++) {
            attribute[i] = new Attribute();
            data[i] = new Vector();
            types[i] = new Vector();
        }


        if(processHeader){
            i = 0;

            // Almacenamos el nombre de los atributos
            while (token.hasMoreTokens()) {
                element = token.nextToken();
                element = element.replace("'", "");
                element = element.replace("\"", "");

                p = Pattern.compile("\\s+");
                m = p.matcher(element);
                element = m.replaceAll(" ");

                if (element.contains(" ")) {
                    StringTokenizer tokenUcfirts = new StringTokenizer(element, " ");
                    String lineUcfirts = "";
                    if (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = tokenUcfirts.nextToken();
                    }
                    while (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = lineUcfirts.concat(UcFirst(tokenUcfirts.nextToken()));
                    }

                    element = lineUcfirts;

                }



                if (element.equals("") || element.equals("?") || element.equals("<null>")) {
                    element = "ATTRIBUTE_" + (i + 1) + "";
                }
                attribute[i].setName(element);
                i++;
            }
        }
        else{
            for (i = 0; i < numAttributes; i++) {
                    attribute[i].setName("a" + i);
            }
            reader.close();
            reader = new BufferedReader(new FileReader("temp"));
        }


// Asignamos el tipo de los atributos

        while ((line = reader.readLine()) != null) {

            token = new StringTokenizer(line, separator);

            for (i = 0; i < numAttributes; i++) {
                element = token.nextToken();

                p = Pattern.compile("^\\s+");
                m = p.matcher(element);
                element = m.replaceAll("");

                p = Pattern.compile("\\s+$");
                m = p.matcher(element);
                element = m.replaceAll("");

                p = Pattern.compile("\\s+");
                m = p.matcher(element);
                element = m.replaceAll(" ");

                element = element.replace("\"", "");

                if (element.equals("") || element.equals(nullValue)) {
                    element = "?";
                }
                data[i].addElement(element);
                types[i].addElement(DataType(element));

            }

        }

        reader.close();


        for (i = 0; i < numAttributes; i++) {
            if (types[i].contains(NOMINAL)) {
                attribute[i].setType(NOMINAL);
            } else {
                if (types[i].contains(REAL)) {
                    attribute[i].setType(REAL);
                } else {
                    if (types[i].contains(INTEGER)) {
                        attribute[i].setType(INTEGER);
                    } else {
                        attribute[i].setType(-1);
                    }
                }
            }

        }



        reader = new BufferedReader(new FileReader("temp"));

        line = reader.readLine(); //leemos la linea de atributos


        for (i = 0; i < data[0].size(); i++) {

            for (j = 0; j < numAttributes; j++) {

                element = (String) data[j].elementAt(i);

                type = attribute[j].getType();


                if (type == NOMINAL) {
                    p = Pattern.compile("[^A-ZÑa-zñ0-9_-]+");
                    m = p.matcher(element);

                    /**
                     * Cambio hecho para que los nominales con espacios en blanco se dejen
                     * con subrayado bajo "_" y sin comillas simples. Se añade la siguiente linea
                     */
                    element = element.replace(" ", "_");

                    if (m.find() && !element.startsWith("'") && !element.endsWith("'") && !element.equals("?")) {
                        /**
                         * Cambio hecho para que los nominales con espacios en blanco se dejen
                         * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                         */
                        /*
                        //element="'"+element+"'";
                         */
                        data[j].set(i, element);
                    }

                    if (!(attribute[j].isNominalValue(element)) && !element.equals("?")) {
                        attribute[j].addNominalValue(element);
                    }
                }


                if (type == INTEGER) {
                    if (!element.equals("?")) {
                        actualValueInt = Integer.valueOf(element);
                        data[j].set(i, actualValueInt);

                        if ((attribute[j].getFixedBounds()) == false) {
                            attribute[j].setBounds(actualValueInt, actualValueInt);
                        } else {
                            min = attribute[j].getMinAttribute();
                            max = attribute[j].getMaxAttribute();
                            if (actualValueInt < min) {
                                attribute[j].setBounds(actualValueInt, max);
                            }
                            if (actualValueInt > max) {
                                attribute[j].setBounds(min, actualValueInt);
                            }
                        }
                    }

                }

                if (type == REAL) {
                    if (!element.equals("?")) {
                        actualValue = Double.valueOf(element);
                        data[j].set(i, actualValue);

                        if ((attribute[j].getFixedBounds()) == false) {
                            attribute[j].setBounds(actualValue, actualValue);
                        } else {
                            min = attribute[j].getMinAttribute();
                            max = attribute[j].getMaxAttribute();
                            if (actualValue < min) {
                                attribute[j].setBounds(actualValue, max);
                            }
                            if (actualValue > max) {
                                attribute[j].setBounds(min, actualValue);
                            }
                        }
                    }
                }

            }//end while

        }//end while

        reader.close();


        /* Insertamos el nombre de la relación que será el mismo que el del
         * fichero pasado, pero sin extensión*/

        nameRelation = fileInput.getName();
        p = Pattern.compile("\\.[A-Za-z]+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        p = Pattern.compile("\\s+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        f = new File("temp");
        f.delete();

// Llamamos a save para que me transforme los datos almacenamos a formato keel
        super.Save(pathnameOutput);


    }
}//end TxtToKeel()

