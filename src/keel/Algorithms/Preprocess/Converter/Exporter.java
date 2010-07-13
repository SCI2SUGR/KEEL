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
 * Exporter.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.Ostermiller.util.CSVParser;

/**
 * <p>
 * <b> Exporter </b>
 * </p>
 *
 * Clase abstracta que contiene los métodos para exportar un fichero de
 * datos con formato keel a ficheros de distintos formatos.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public abstract class Exporter {
    // Almacena la definición de los atributos del fichero.
    keel.Dataset.Attribute attribute[];    // Almacena los datos del fichero.
    Vector data[];    //Almacena el tipo de cada dato del fichero.
    Vector types[];    // Almacena el valor nulo del fichero de datos a crear.
    String nullValue = new String();    // Almacena el nombre de la relación del fichero
    String nameRelation = new String();    // Almacena el separador de los datos para el nuevo fichero generado.
    String separator = new String();    // Almacena el número de atributos existentes en el fichero de datos
    int numAttributes = 0;    // Etiqueta para valores nominales.
    int NOMINAL = 0;    // Etiqueta para valores enteros.
    int INTEGER = 1;    // Etiqueta para valores reales.
    int REAL = 2;


    /*
     * Este método lee los datos almacenados en un fichero con formato keel
     * correspondiente al parámetro de entrada pathnameInput y
     * cargar la definición de los atributos en un vector de objetos de la
     * clase Attribute (del paquete keel.Dataset), los datos en el vector
     * data[], y el nombre de la relación en la variable miembro
     * nameRelation y el número de atributos en la variable miembro
     * numAttributes.
     *
     * @param  String pathnameOutput Indica la ruta del fichero de entrada con formato Keel.
     *
     * @throws Exception
     */
    public void Start(String pathnameInput) throws Exception {
        BufferedReader reader;
        Pattern p;
        Matcher m;
        File f;
        StringTokenizer token;
        String line = new String();
        String tokenInitial = new String();
        String nameAttribute = new String();
        String typeAttribute = new String();
        String element = new String();
        String lineReduced = new String();
        String filename = "tempOf";
        int i;
        int j;
        int indexInitial = 0;
        int indexSecond = 0;
        int type = -1;


        File fileInput = new File(pathnameInput);

        filename = filename.concat(fileInput.getName());


        reader = new BufferedReader(new FileReader(pathnameInput));

        BufferedWriter auxFile = new BufferedWriter(new FileWriter(filename));

        while ((line = reader.readLine()) != null) {
            p = Pattern.compile("\\s*,\\s*");
            m = p.matcher(line);
            line = m.replaceAll(",");

            p = Pattern.compile("^\\s+");
            m = p.matcher(line);
            line = m.replaceAll("");

            p = Pattern.compile("\\s+$");
            m = p.matcher(line);
            line = m.replaceAll("");

            p = Pattern.compile("\\s+");
            m = p.matcher(line);
            line = m.replaceAll(" ");

            auxFile.write(line + "\n");
        }

        auxFile.close();
        reader.close();


        reader = new BufferedReader(new FileReader(filename));

        /* Contamos el número de atributos que existen*/
        line = reader.readLine();
        token = new StringTokenizer(line, " ");


        while (!(line.equalsIgnoreCase("@data"))) {
            if (line.startsWith("@")) {
                tokenInitial = token.nextToken().toLowerCase();

                if (tokenInitial.equals("@attribute")) {
                    numAttributes++;
                }
                if (tokenInitial.equals("@relation")) {
                    nameRelation = token.nextToken();
                }
            }

            line = reader.readLine();
            token = new StringTokenizer(line, " ");
        }// end while()

        reader.close();


        /* Reservamos memoria para guardar la informacion de los atributos*/
        attribute = new Attribute[numAttributes];
        data = new Vector[numAttributes];

        for (i = 0; i < numAttributes; i++) {
            attribute[i] = new Attribute();
            data[i] = new Vector();
        }


// Insertamos la definición de los atributos en Attribute
        reader = new BufferedReader(new FileReader(filename));

        line = reader.readLine();

        i = -1;

        while (!(line.equalsIgnoreCase("@data"))) {

            if (line.startsWith("@")) {
                token = new StringTokenizer(line, " ");
                tokenInitial = token.nextToken();

                if (tokenInitial.equalsIgnoreCase("@attribute")) {
                    i++;
                    nameAttribute = token.nextToken();


                    if (nameAttribute.startsWith("'")) {
                        indexInitial = line.indexOf("\'");
                        indexSecond = line.indexOf("\'", indexInitial + 1);
                        nameAttribute = line.substring(indexInitial, indexSecond + 1);

                    } else {
                        if (nameAttribute.contains("{")) {
                            nameAttribute = nameAttribute.substring(0, nameAttribute.indexOf("{"));
                        }
                    }


                    indexSecond = line.indexOf(nameAttribute) + nameAttribute.length();


                    if (nameAttribute.contains(" ") && !nameAttribute.startsWith("\'")) {
                        nameAttribute = "'" + nameAttribute + "'";
                    }
                    attribute[i].setName(nameAttribute);

                    lineReduced = line.substring(indexSecond, line.length());

                    p = Pattern.compile("^\\s+");
                    m = p.matcher(lineReduced);
                    lineReduced = m.replaceAll("");

                    p = Pattern.compile("\\s+$");
                    m = p.matcher(lineReduced);
                    lineReduced = m.replaceAll("");

                    token = new StringTokenizer(lineReduced, " ");


                    if (token.hasMoreTokens()) {
                        typeAttribute = token.nextToken().toLowerCase();

                        if (typeAttribute.startsWith("real")) {
                            attribute[i].setType(REAL);
                        } else {
                            if (typeAttribute.startsWith("integer")) {
                                attribute[i].setType(INTEGER);
                            } else {
                                attribute[i].setType(NOMINAL);

                                if (line.contains("{") && line.contains("}")) {
                                    lineReduced = line.substring(line.indexOf("{") + 1, line.indexOf("}"));

                                    p = Pattern.compile("^\\s+");
                                    m = p.matcher(lineReduced);
                                    lineReduced = m.replaceAll("");

                                    p = Pattern.compile("\\s+$");
                                    m = p.matcher(lineReduced);
                                    lineReduced = m.replaceAll("");

                                    if (lineReduced != "") {
                                        StringTokenizer listValues = new StringTokenizer(lineReduced, ",");

                                        while (listValues.hasMoreTokens()) {
                                            attribute[i].addNominalValue(listValues.nextToken());
                                        }
                                    }
                                }
                            }//end else
                        }//end else
                    }//end if

                    type = attribute[i].getType();

                    if (type == REAL || type == INTEGER) {
                        if (line.contains("[") && line.contains("]")) {

                            lineReduced = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

                            p = Pattern.compile("^\\s+");
                            m = p.matcher(lineReduced);
                            lineReduced = m.replaceAll("");

                            p = Pattern.compile("\\s+$");
                            m = p.matcher(lineReduced);
                            lineReduced = m.replaceAll("");


                            if (lineReduced != "") {
                                StringTokenizer range = new StringTokenizer(lineReduced, ",");
                                if (type == REAL) {
                                    attribute[i].setBounds(Double.valueOf(range.nextToken()), Double.valueOf(range.nextToken()));
                                }
                                if (type == INTEGER) {
                                    attribute[i].setBounds(Integer.valueOf(range.nextToken()), Integer.valueOf(range.nextToken()));
                                }
                            }
                        }

                    }

                }//end if()

            }//end if()

            line = reader.readLine();

        }//end while()


        /* Almacenamos los datos en un fichero temporal para luego poder
        ser parseado con CSVParser por ','
         */
        BufferedWriter writer = new BufferedWriter(new FileWriter("temp"));
        while ((line = reader.readLine()) != null) {
            // Saltamos las líneas comentadas
            if (!line.startsWith("%") && !line.equals("\n") && !line.equals("\r") && !line.equals("")) {
                line = line.replace("'", "\"");
                writer.write(line + "\n");
            }
        }

        writer.close();
        reader.close();

        FileReader filereader = new FileReader("temp");

        String[][] values = CSVParser.parse(filereader, ',');

        filereader.close();

        for (i = 0; i < values.length; i++) {
            for (j = 0; j < numAttributes; j++) {
                element = values[i][j];
                element = element.replace("\r", " ");
                element = element.replace("\n", " ");

                type = attribute[j].getType();

                if (element.equalsIgnoreCase("?") || element.equalsIgnoreCase("<null>") || element.equals("")) {
                    data[j].addElement(nullValue);
                } else {
                    data[j].addElement(element);
                }
            }
        }

        /* Recogemos la lista de valores nominales de los datos, para aquellos atributos que
        no hayan definido la lista en la declaración */
        for (i = 0; i < numAttributes; i++) {
            type = attribute[i].getType();

            if (type == NOMINAL && attribute[i].getNumNominalValues() == 0) {
                for (j = 0; j < data[0].size(); j++) {
                    element = (String) data[i].elementAt(j);

                    if (!(attribute[i].isNominalValue(element))) {
                        attribute[i].addNominalValue(element);
                    }
                }
            }
        }



        f = new File(filename);
        f.delete();

        f = new File("temp");
        f.delete();

    }//end Start()
}//end Class Exporter


