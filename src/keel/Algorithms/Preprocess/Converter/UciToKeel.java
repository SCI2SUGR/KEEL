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
 * UciToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.*;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.Ostermiller.util.CSVParser;

/**
 * <p>
 * <b> UciToKeel </b>
 * </p>
 *
* This class extends from the Importer class. It is used to read 
 * data with UCI format and transform them to the KEEL format. 
 * UCI format has two different files:
 *  - Name file that stores all the names of the attributes and class ".names".
 *  - Data file with all the data ".data".
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class UciToKeel extends Importer {

// Etiqueta para el valor IGNORE del fichero de nombres.
    int IGNORE = -2;


    /** UciToKeel class Constructor.
     * Initializes the variables that store the symbols separator between data.
     *
     * @param  separatorUser. Separator symbols used in the csv format.
     */
    public UciToKeel(String separatorUser) {
        separator = separatorUser;
    }

    /**
     * Method used to transform the data from the UCI file given as parameter to 
     * KEEL format file which will be stored in the second file given.
     *
     * @param pathnameInputNames UCI names file path.
     * @param pathnameInputData UCI data file path.
     * @param pathnameOutput KEEL file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInputNames, String pathnameInputData, String pathnameOutput) throws Exception {

        Pattern p;
        Matcher m;
        File f;
        BufferedReader reader;
        BufferedWriter auxFile;
        String element = new String();
        String filenameNames = "tempOf";
        String filenameData = "tempOf";
        String line = new String();
        String nameAttribute = new String();
        String lineReduced = new String();
        int i = 0;
        int j = 0;
        int type = -1;
        int actualValueInt;
        double actualValue;
        double min;
        double max;


// Limpio los caracteres no vÃ¡lidos del ficheros de nombres
        File fileInputNames = new File(pathnameInputNames);
        filenameNames = filenameNames.concat(fileInputNames.getName());

        reader = new BufferedReader(new FileReader(pathnameInputNames));

        auxFile = new BufferedWriter(new FileWriter(filenameNames));

        while ((line = reader.readLine()) != null) {

            if (!line.equals("") && !line.startsWith("|")) {
                //Quitamos los comentarios
                if (line.contains("|")) {
                    line = line.substring(0, line.indexOf('|'));                //Quitamos los espacios al principio de la lÃ­nea
                }
                p = Pattern.compile("^\\s+");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Quitamos los espacios al final de la lÃ­nea
                p = Pattern.compile("\\s+$");
                m = p.matcher(line);
                line = m.replaceAll("");

                // Quitamos el punto al final de la lÃ­nea
                p = Pattern.compile("[.]$");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Elimino cualquier valor escapado
                p = Pattern.compile("\\\\.");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Quitamos los espacios delante y detrÃ¡s de las comas
                p = Pattern.compile("\\s*,\\s*");
                m = p.matcher(line);
                line = m.replaceAll(",");

                //Quitamos los espacios delante y detrÃ¡s de los dos puntos
                p = Pattern.compile("\\s*:\\s*");
                m = p.matcher(line);
                line = m.replaceAll(":");

                p = Pattern.compile("\\s+");
                m = p.matcher(line);
                line = m.replaceAll(" ");


                auxFile.write(line + "\n");
            }
        }

        auxFile.close();
        reader.close();

// Limpio los caracteres no vÃ¡lidos del ficheros de datos
        File fileInputData = new File(pathnameInputData);
        filenameData = filenameData.concat(fileInputData.getName());

        reader = new BufferedReader(new FileReader(pathnameInputData));

        auxFile = new BufferedWriter(new FileWriter(filenameData));

        while ((line = reader.readLine()) != null) {

            if (!line.equals("") && !line.startsWith("|")) {
                //Quitamos los comentarios
                if (line.contains("|")) {
                    line = line.substring(0, line.indexOf('|'));                //Quitamos los espacios al principio de la lÃ­nea
                }
                p = Pattern.compile("^\\s+");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Quitamos los espacios al final de la lÃ­nea
                p = Pattern.compile("\\s+$");
                m = p.matcher(line);
                line = m.replaceAll("");

                // Quitamos el punto al final de la lÃ­nea
                p = Pattern.compile("[.]$");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Elimino cualquier valor escapado
                p = Pattern.compile("\\\\.");
                m = p.matcher(line);
                line = m.replaceAll("");

                //Quitamos los espacios delante y detrÃ¡s de las comas
                p = Pattern.compile("\\s*,\\s*");
                m = p.matcher(line);
                line = m.replaceAll(",");

                p = Pattern.compile("\\s+");
                m = p.matcher(line);
                line = m.replaceAll(" ");

                auxFile.write(line + "\n");
            }
        }

        auxFile.close();
        reader.close();


// Leo el fichero para ver cuantos atributos existen
        reader = new BufferedReader(new FileReader(filenameNames));

//Contamos el numero de atributos
        i = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains(":")) {
                i++;
            }
        }

        reader.close();


        numAttributes = i;

//Reservamos memoria para almacenar la definiciÃ³n de los atributos y de los datos

        attribute = new Attribute[numAttributes];
        data = new Vector[numAttributes];
        types = new Vector[numAttributes];

        for (i = 0; i < numAttributes; i++) {
            attribute[i] = new Attribute();
            data[i] = new Vector();
            types[i] = new Vector();
        }

// Leo del fichero generado con valores vÃ¡lidos
        reader = new BufferedReader(new FileReader(filenameNames));

//Saltamos la primera lÃ­nea que corresponde al nombre de las clases.
        line = reader.readLine();

        i = 0;
        while ((line = reader.readLine()) != null) {
            if (line.contains(":")) {
                nameAttribute = line.substring(0, line.indexOf(":"));
                nameAttribute = nameAttribute.replace("'", "");

                p = Pattern.compile("\\s+");
                m = p.matcher(nameAttribute);
                nameAttribute = m.replaceAll(" ");

                if (nameAttribute.contains(" ")) {
                    StringTokenizer token = new StringTokenizer(nameAttribute, " ");
                    String lineAux = "";
                    if (token.hasMoreTokens()) {
                        lineAux = token.nextToken();
                    }
                    while (token.hasMoreTokens()) {
                        lineAux = lineAux.concat(UcFirst(token.nextToken()));
                    }

                    nameAttribute = lineAux;

                }



                if (nameAttribute.equals("") || nameAttribute.equals("?") || nameAttribute.equals("<null>")) {
                    nameAttribute = "ATTRIBUTE_" + (i + 1) + "";
                }
                attribute[i].setName(nameAttribute);
                lineReduced = line.substring(line.indexOf(":") + 1, line.length()).toLowerCase();


                if (lineReduced.startsWith("ignore")) {
                    attribute[i].setType(IGNORE);
                } else {
                    if (!lineReduced.startsWith("discrete") && !lineReduced.startsWith("continuous")) {
                        lineReduced = line.substring(line.indexOf(":") + 1, line.length());

                        if (lineReduced != "") {
                            StringTokenizer listValues = new StringTokenizer(lineReduced, ",");
                            attribute[i].setType(NOMINAL);

                            while (listValues.hasMoreTokens()) {
                                element = (String) listValues.nextToken();

                                p = Pattern.compile("[^A-ZÃa-zÃ±0-9_-]+");
                                m = p.matcher(element);
                                /**
                                 * Cambio hecho para que los nominales con espacios en blanco se dejen
                                 * con subrayado bajo "_" y sin comillas simples. Se aÃ±ade la siguiente linea
                                 */
                                element = element.replace(" ", "_");

                                if (m.find() && !element.startsWith("'") && !element.endsWith("'") && !element.equals("?")) /**
                                 * Cambio hecho para que los nominales con espacios en blanco se dejen
                                 * con subrayado bajo "_" y sin comillas simples. Se comenta la siguiente linea
                                 */
                                /*
                                //element="'"+element+"'";
                                 */ {
                                    attribute[i].addNominalValue(element);
                                }
                            }
                        }

                    }//end if

                }//end else

                i++;
            }
        }

        reader.close();

        numAttributes = i;



        FileReader filereader = new FileReader(filenameData);

        String[][] values = CSVParser.parse(filereader, separator.charAt(0));

        filereader.close();

        for (i = 0; i < values.length; i++) {
            for (j = 0; j < numAttributes; j++) {
                element = values[i][j];

                p = Pattern.compile("^\\s+");
                m = p.matcher(element);
                element = m.replaceAll("");

                p = Pattern.compile("\\s+$");
                m = p.matcher(element);
                element = m.replaceAll("");

                element = element.replace("\r", " ");
                element = element.replace("\n", " ");

                if (element.equals("") || element.equals(nullValue) || element.equals("<null>")) {
                    element = "?";
                }
                data[j].addElement(element);
            }
        }




// Asignamos el tipo de los atributos
        for (i = 0; i < data[0].size(); i++) {
            for (j = 0; j < numAttributes; j++) {
                element = (String) data[j].elementAt(i);

                if (attribute[j].getType() == -1) {
                    types[j].addElement(DataType(element));
                }
            }

        }


        for (i = 0; i < numAttributes; i++) {
            if (attribute[i].getType() != IGNORE && attribute[i].getType() == -1) {
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
        }



        for (i = 0; i < data[0].size(); i++) {

            for (j = 0; j < numAttributes; j++) {

                element = (String) data[j].elementAt(i);

                type = attribute[j].getType();


                if (type == NOMINAL) {
                    p = Pattern.compile("[^A-ZÃa-zÃ±0-9_-]+");
                    m = p.matcher(element);

                    /**
                     * Cambio hecho para que los nominales con espacios en blanco se dejen
                     * con subrayado bajo "_" y sin comillas simples. Se aÃ±ade la siguiente linea
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



        /* Insertamos el nombre de la relaciÃ³n que serÃ¡ el mismo que el del
         * fichero pasado, pero sin extensiÃ³n*/

        nameRelation = fileInputNames.getName();
        p = Pattern.compile("\\.[A-Za-z]+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        p = Pattern.compile("\\s+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

//Borramos los ficheros temporales
        f = new File(filenameNames);
        f.delete();

        f = new File(filenameData);
        f.delete();

// Llamamos a save para que me transforme los datos almacenamos a formato keel
        Save(pathnameOutput);

    }

    /**
     * Method that creates the output file with KEEL format given as parameter 
     * using all the structures built by the start method.  
     * @param pathnameOutput KEEL file path to generate.
     * @throws Exception if the file can not be written.
     */
    @Override
    public void Save(String pathnameOutput) throws Exception {
        Vector dataAux[];
        Attribute attributeCurrent = new Attribute();
        String filename = pathnameOutput;
        String line = new String();
        int i;
        int j;
        int k;
        int cont = 0;


        /* Comprobamos si el nombre del fichero tiene la extensiÃ³n .dat, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".dat")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".dat");
        }
        FileWriter fileWriter = new FileWriter(filename);

        fileWriter.write("@relation " + nameRelation + "\n");

        for (i = 0; i < numAttributes; i++) {
            attributeCurrent = attribute[i];
            if (attributeCurrent.getType() != IGNORE) {
                if (attributeCurrent.getType() != -1) {
                    line = attributeCurrent.toString();
                    fileWriter.write(line + "\n");
                } else {
                    fileWriter.write("@attribute " + attributeCurrent.getName() + " REAL\n");
                }
            }
        }


        cont = 0;
        for (i = 0; i < numAttributes; i++) {
            attributeCurrent = attribute[i];
            if (attributeCurrent.getType() != IGNORE) {
                cont++;
            }
        }

        dataAux = new Vector[cont];

        for (i = 0; i < cont; i++) {
            dataAux[i] = new Vector();
        }
        k = 0;
        for (i = 0; i < numAttributes; i++) {
            attributeCurrent = attribute[i];

            if (attributeCurrent.getType() != IGNORE) {
                for (j = 0; j < data[0].size(); j++) {
                    dataAux[k].addElement(data[i].elementAt(j));
                }
                k++;
            }
        }

        fileWriter.write("@inputs ");
        for (i = 0; i < numAttributes-1; i++) {
            fileWriter.write(attribute[i].getName() );
            if(i!=numAttributes-2)
              fileWriter.write(", ");
            else
              fileWriter.write("\n");                
        }
        
        fileWriter.write("@outputs " );
        fileWriter.write(attribute[numAttributes-1].getName() + "\n");

        fileWriter.write("@data" + "\n");

        for (i = 0; i < dataAux[0].size(); i++) {
            for (j = 0; j < dataAux.length; j++) {
                if (j == (numAttributes - 1)) {
                    fileWriter.write(dataAux[j].elementAt(i) + "");
                } else {
                    fileWriter.write(dataAux[j].elementAt(i) + ",");
                }
            }

            fileWriter.write("\n");
        }

        fileWriter.close();

        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " convertido correctamente");

    }//end save()
}//end UciToKeel()

