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
 * DbToKeel.java
 */
package keel.Algorithms.Preprocess.Converter;

import keel.Dataset.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;

/**
 * <p>
 * <b> DbToKeel </b>
 * </p>
 *
 * Clase extendida de la clase Importer.
 * Esta clase es utilizada para leer datos localizados en tablas de bases de datos SQL
 * y convertirlos a formato keel.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class DbToKeel extends Importer {

//Variable que almacena el driver  para conectarse a la base de datos.
    private String driverName = new String();// Variable que almacena el nombre de la base de datos.
    private String databaseURL = new String();// Variable que almacena el nombre de  la tabla que contiene los datos a convertir.
    private String tableName = new String();// Variable que almacena el login o nombre de usuario de la base de datos.
    private String login = new String();// Variable que almacena el password o contraseña de la base de datos.
    private String password = new String();

    /*
     * Constructor de la Clase DbToKeel. Inicializa los valores de las variables
     * miembro driverName, databaseURL, tableName, login y password
     * con el valor de los parámetros driverNameUser, databaseURLUser, tableNameUser,
     * loginUser, passwordUser respectivamente.
     *
     * @param String driverNameUser Variable de tipo String con el valor del driver de la conexion a la base de datos.
     * @param String conecctionUser Variable de tipo String con el tipo de conexion a la base de datos.
     * @param String databaseURL: Variable que almacena la dirección URL de la base de datos
     * de la forma jdbc:subprotocol:subname donde subprotocol es el nombre
     * del controlador, y subname es una referencia controlador-específica a la base de datos.
     * @param String loginUser Variable de tipo String con el login o nombre de usuario para conectarse a la base de datos.
     * @param String passwordUser Variable de tipo String con el password o contraseña para conectarse a la base de datos.
     *
     */
    public DbToKeel(String driverNameUser, String databaseURLUser, String tableNameUser, String loginUser, String passwordUser) {
        driverName = driverNameUser;
        databaseURL = databaseURLUser;
        tableName = tableNameUser;
        login = loginUser;
        password = passwordUser;

    }


    /*
     * Metodo utilizado para convertir los datos de la tabla dentro de una base
     * de datos SQL a un fichero de datos con formato keel en el fichero
     * indicado por la ruta pathnameOutput.
     *
     * @param pathnameOutput ruta con los datos en formato keel
     *
     * @throws Exception
     */
    public void Start(String pathnameOutput) throws Exception {
        Pattern p;
        Matcher m;
        String element = new String();
        int i = 0;
        int j = 0;
        int type = -1;
        int numInstances = 0;
        int actualValueInt;
        double actualValue;
        double min;
        double max;

//Se declara la variable de tipo conexion, que guardará la conexión a la base de datos
        Connection conn;
//Se declara la variable de tipo Statement que nos permitirá ejecutar sentencias
        Statement statement;
//Se declara la variable de tipo Resulset para recoger lo que devuelve una sentencia SELECT
        ResultSet result;
        ResultSetMetaData resultmd;


// Se carga el driver JDBC-ODBC
        try {
            Class.forName(driverName);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }



        try {  // Se establece la conexión con la base de datos
            conn = DriverManager.getConnection(databaseURL, login, password); //URL BaseDatos, usuario, clave
            statement = conn.createStatement();



            try {
                result = statement.executeQuery("SELECT * FROM " + tableName);


                resultmd = result.getMetaData();
                numAttributes = resultmd.getColumnCount();



//Reservamos memoria para almacenar la definición de los atributos y de los datos

                attribute = new Attribute[numAttributes];
                data = new Vector[numAttributes];
                types = new Vector[numAttributes];

                for (i = 0; i < numAttributes; i++) {
                    attribute[i] = new Attribute();
                    data[i] = new Vector();
                    types[i] = new Vector();
                }


// Almacenamos el nombre de los atributos
                for (i = 0; i < numAttributes; i++) {
                    try {
                        element = resultmd.getColumnName(i + 1);
                    } catch (NullPointerException e) {
                        element = "?";
                    }


                    element = element.replace("'", "");
                    element = element.replace("\"", "");
                    element = element.replace("\n", " ");
                    element = element.replace("\r", " ");

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


                numInstances = result.getRow();

                while (result.next()) {
                    for (i = 0; i < numAttributes; i++) {
                        try {
                            element = (result.getObject(i + 1)).toString();
                        } catch (NullPointerException e) {
                            element = "?";
                        }


                        p = Pattern.compile("^\\s+");
                        m = p.matcher(element);
                        element = m.replaceAll("");

                        p = Pattern.compile("\\s+$");
                        m = p.matcher(element);
                        element = m.replaceAll("");

                        element = element.replace("'", "");
                        element = element.replace("\"", "");
                        element = element.replace("\n", " ");
                        element = element.replace("\r", " ");

                        if (element.equals("") || element.equals("<null>")) {
                            element = "?";
                        }
                        data[i].addElement(element);
                    }
                }


                statement.close();
                conn.close();


            } catch (SQLException e) {
                System.out.println(e);
                System.exit(2);
            }

        } catch (SQLException e) {
            System.out.println(e);
            System.exit(3);
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


                    if (m.find() && !element.equals("?")) {
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


        nameRelation = tableName;

        p = Pattern.compile("\\.[A-Za-z]+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

        p = Pattern.compile("\\s+");
        m = p.matcher(nameRelation);
        nameRelation = m.replaceAll("");

// Llamamos a save para que me transforme los datos almacenamos a formato keel
        super.Save(pathnameOutput);


    }
}//end DbToKeel()

