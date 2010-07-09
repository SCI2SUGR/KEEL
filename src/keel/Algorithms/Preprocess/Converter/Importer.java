/*
 * Importer.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.util.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * <b> Importer </b>
 * </p>
 *
 * Clase abstracta que contiene los métodos para importar ficheros de
 * distintos formatos a ficheros con formato Keel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public abstract class Importer {

    // Almacena la definición de los atributos del fichero.
    keel.Dataset.Attribute attribute[];    // Almacena los datos del fichero.
    Vector data[];    //Almacena el tipo de cada dato del fichero.
    Vector types[];    // Almacena el valor nulo del fichero de datos
    String nullValue = new String();    // Almacena el nombre de la relación
    String nameRelation = new String();    // Almacena el separador de los datos
    String separator = new String();    // Almacena el número de atributos existentes en el fichero de datos
    int numAttributes = 0;    // Etiqueta para valores nominales.
    int NOMINAL = 0;    // Etiqueta para valores enteros.
    int INTEGER = 1;    // Etiqueta para valores reales.
    int REAL = 2;
    
    // Property for considering or not the header with the attributes names
    // (only used for CSV, TXT, Excel, HTML and PRN formats)
    protected boolean processHeader = true;

    /*
     * Method for setting a boolean indicating if the header must be processed
     *
     * @param processHeader Boolean
     */
    public void setProcessHeader(boolean processHeader) {
        this.processHeader = processHeader;
    }


    /*
     * Este método crea el fichero de salida con formato keel en la ruta pasada,
     * a partir de la información almacenada en las variables miembro
     * attribute[], data[], nameRelation y numAttributes.
     *
     * @param  String pathnameOutput Indica la ruta del fichero de salida con formato Keel.
     *
     * @throws Exception
     */
    public void Save(String pathnameOutput) throws Exception {
        keel.Dataset.Attribute attributeCurrent = new keel.Dataset.Attribute();
        String filename = pathnameOutput;
        String line = new String();
        String nameAttribute = new String();
        String nameAttributeAux = new String();
        Object element;
        Pattern p;
        Matcher m;
        int enc = 0;
        int i;
        int j;
        int k;

        /* Comprobamos si el nombre del fichero tiene la extensión .dat, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".dat")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".dat");
        }
        FileWriter fileWriter = new FileWriter(filename);

        fileWriter.write("@relation " + nameRelation + "\n");


        /* Comprobamos si hay nombres de atributos repetidos
         * y se le añade un número al final
         *  Ej: Es decir que si los atributos son:  a,b,c,c,c
         * @attribute a ... @attribute b ... @attribute c1 ... @attribute c2 ... @attribute c3 ...
         *
         */

        for (i = 0; i < numAttributes; i++) {
            nameAttribute = attribute[i].getName();
            if (i < numAttributes) {
                k = 1;
                enc = 0;
                for (j = i + 1; j < numAttributes; j++) {
                    nameAttributeAux = attribute[j].getName();
                    if (nameAttribute.equalsIgnoreCase(nameAttributeAux)) {
                        enc = 1;
                        k++;
                        nameAttributeAux = nameAttributeAux.concat(Integer.toString(k));
                        attribute[j].setName(nameAttributeAux);
                    }
                }

                if (enc == 1) {
                    nameAttribute = nameAttribute.concat("1");
                    attribute[i].setName(nameAttribute);
                }
            }

        }


        for (i = 0; i < numAttributes; i++) {

            attributeCurrent = attribute[i];


            p = Pattern.compile("\\s+");
            m = p.matcher(attributeCurrent.getName());
            attributeCurrent.setName(m.replaceAll("_"));


            if (attributeCurrent.getType() != -1) {
                line = attributeCurrent.toString();
                fileWriter.write(line + "\n");
            } else {
                fileWriter.write("@attribute " + attributeCurrent.getName() +
                        " REAL\n");
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

        for (i = 0; i < data[0].size(); i++) {

            for (j = 0; j < numAttributes; j++) {
                element = data[j].elementAt(i);
                if (j == (numAttributes - 1)) {
                    p = Pattern.compile("\\s+");
                    m = p.matcher(element.toString());
                    element = m.replaceAll("_");
                    fileWriter.write(element + "");
                } else {
                    p = Pattern.compile("\\s+");
                    m = p.matcher(element.toString());
                    element = m.replaceAll("_");
                    fileWriter.write(element + ",");
                }

            }
            fileWriter.write("\n");
        }
        fileWriter.close();

        File f = new File(filename);

        System.out.println("Fichero " + f.getName() +
                " convertido correctamente!!!");

    } //end save()


    /*
     * Método utilizado para poner en mayúscula el primer carácter de una cadena pasada
     * por parámetro.
     *
     * @param line. Variable String que almacena la cadena
     * a la que se pretende poner el primer carácter en mayúscula.
     *
     * @return Devuelve una cadena igual a la cadena pasada por parámetro
     * pero con el primer carácter en mayúscula.
     */
    public String UcFirst(String line) {
        String lineTemp = "";

        if (line.length() > 1) {
            String charFirst = (line.substring(0, 1)).toUpperCase();
            lineTemp = lineTemp.concat(charFirst);
            lineTemp = lineTemp.concat(line.substring(1, line.length()));
        } else {
            lineTemp = line;
        }
        return lineTemp;
    }


    /*
     * Metodo que devuelve el tipo del elemento pasado por parámetros
     *
     * @param item elemento pasado
     * @return int tipo del elemento pasado. 0 nominal, 1 entero y 2 real.
     */
    public int DataType(String item) {

        int type = -1;

        if (item.equals("?")) {
            return type;
        } else {
            try {
                int pruebaInt = Integer.valueOf(item);
                type = INTEGER;
            } catch (NumberFormatException nfe) {
                try {
                    double pruebaDouble = Double.valueOf(item);
                    type = REAL;
                } catch (NumberFormatException e) {
                    type = NOMINAL;
                }
            }
        }
        return type;

    } // end DataType()
}

