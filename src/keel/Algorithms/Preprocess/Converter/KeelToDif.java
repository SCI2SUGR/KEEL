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
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a un fichero con formato Dif.
 * (fichero de intercambio de datos).
 *
 * @author Teresa Prieto Lï¿½pez (UCO)
 * @version 1.0
 */
public class KeelToDif extends Exporter {


    /*
     * Constructor de la Clase KeelToDif. Inicializa el valor
     * de la variable miembro nullValue (valor nulo para un dato en el fichero)
     * a una cadena vacï¿½a.
     */
    public KeelToDif() {
        nullValue = "";
    }

    /*
     * Este mï¿½todo llama al mï¿½todo Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al mï¿½todo Save() para crear el fichero de datos Dif indicado en el
     * parï¿½metro de entrada pathnameOutput.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     * @param  String pathnameOutput Variable con la ruta del fichero de datos de salida
     * con formato Dif.
     *
     * @throws Exception.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()

    /*
     * Mï¿½todo utilizado para crear el fichero con formato Dif
     * indicada la ruta por el parï¿½metro pathnameOutput. Este fichero se crea a partir
     * de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @param String pathnameOutput. Variable de tipo String con
     * la ruta del fichero de datos de salida con formato Dif.
     *
     * @throws Exception.
     *
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int type = -1;
        int numInstances = 0;
        String filename = new String();
        String element = new String();


        /* Comprobamos si el nombre del fichero tiene la extensiï¿½n .csv, si no la tiene
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

