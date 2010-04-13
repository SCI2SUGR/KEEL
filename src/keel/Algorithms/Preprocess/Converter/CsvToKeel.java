/*
 * CsvToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.Ostermiller.util.CSVParser;
import java.io.*;

/**
 * <p>
 * <b> CsvToKeel </b>
 * </p>
 *
 *  Clase extendida de la clase Importer. Esta clase se utiliza
 * para leer datos localizados en ficheros con formato Csv
 * (valores separados por comas) y convertirlos a formato keel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */

/*
Clase utilizada para leer datos localizados en ficheros Csv y
convertirlos a formato keel
 */
public class CsvToKeel extends Importer {

    /* Constructor de la Clase CsvToKeel.
     * Inicializa los valores de las variables miembro nullValue (valor nulo)
     * y separator (el separador de los datos) con los valores de los
     * parámetros nullValueUser y separatorUser respectivamente.
     *
     * @param  nullValueUser. Variable de tipo String con el valor nulo del fichero Csv .
     * @param  separatorUser. Variable de tipo String con el valor del separador
     * de los datos del fichero Csv .
     */
    public CsvToKeel(String nullValueUser, String separatorUser) {
        separator = separatorUser;
        nullValue = nullValueUser;
    }

    /* Metodo utilizado para convertir los datos del fichero Csv indicado
     * mediante la variable pathnameInput a formato keel en el fichero
     * indicado por la ruta pathnameOutput
     *
     * @param pathnameInput ruta con los datos en formato csv
     * @param pathnameOutput ruta para el fichero de datos Keel.
     *
     * @throws Exception */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {
        Pattern p;
        Matcher m;
        String element = new String();
        int i = 0;
        int j = 0;
        int type = -1;
        int actualValueInt;
        double actualValue;
        double min;
        double max;


        File fileInput = new File(pathnameInput);

        FileReader filereader = new FileReader(pathnameInput);

        String[][] values = CSVParser.parse(filereader, separator.charAt(0));

        filereader.close();

        /* Leemos la primera linea con los nombres
        de los atributos para obtener el numero de atributos  */

        numAttributes = values[0].length;

//Reservamos memoria para almacenar la definición de los atributos y de los datos

        attribute = new Attribute[numAttributes];
        data = new Vector[numAttributes];
        types = new Vector[numAttributes];

        for (i = 0; i < numAttributes; i++) {
            attribute[i] = new Attribute();
            data[i] = new Vector();
            types[i] = new Vector();
        }


        int initialI = 1;
        if(processHeader){
                // Almacenamos el nombre de los atributos
                for (i = 0; i < numAttributes; i++) {
                    element = values[0][i];

                    element = element.replace("'", "");
                    element = element.replace("\r", "");
                    element = element.replace("\n", "");

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
                }

        }
        else{
            for (i = 0; i < numAttributes; i++) {
                    attribute[i].setName("a" + i);
            }
            initialI = 0;
        }



        for (i = initialI; i < values.length; i++) {
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
                types[j].addElement(DataType(element));
            }
        }


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

            }//end for

        }//end while



        /* Insertamos el nombre de la relación que será el mismo que el del
         * fichero pasado, pero sin extensión*/

        nameRelation = fileInput.getName();
        p = Pattern.compile("\\.[A-Za-z]+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        p = Pattern.compile("\\s+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");


// Llamamos a save para que me transforme los datos almacenamos a formato keel
        super.Save(pathnameOutput);

    }
}//end CsvToKeel()
