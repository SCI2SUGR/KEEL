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
 * KeelToDb.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.sql.*;

/**
 * <p>
 * <b> KeelToDb </b>
 * </p>
 *
 * Clase extendida de la clase Exporter. Esta clase permite convertir
 * un fichero de datos con formato Keel a una nueva tabla en una base de
 * datos SQL.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToDb extends Exporter {

//Variable que almacena el driver  para conectarse a la base de datos.
    private String driverName = new String();// Variable que almacena el nombre de la base de datos.
    private String databaseURL = new String();// Variable que almacena el nombre de  la tabla que contiene los datos a convertir.
    private String tableName = new String();// Variable que almacena el login o nombre de usuario de la base de datos.
    private String login = new String();// Variable que almacena el password o contraseña de la base de datos.
    private String password = new String();

    /*
     * Constructor de la Clase DbToKeel. Inicializa los valores de las variables
     * miembro driverName, databaseURL, tableName, login y password
     * con el valor de los parámetros driverNameUser, databaseURLUser, tableNameUser, loginUser, passwordUser respectivamente.
     * También inicializa el valor para la variable miembro nullValue a una cadena con valor "null".
     *
     * @param String driverNameUser Variable de tipo String con el valor del driver de la conexion a la base de datos.
     * @param String databaseURL: Variable que almacena la dirección URL de la base de datos
     * de la forma jdbc:subprotocol:subname donde subprotocol es el nombre
     * del controlador, y subname es una referencia controlador-específica a la base de datos.
     * @param String tableNameUser Variable de tpo String con el nombre de la tabla que contiene los datos.
     * @param String loginUser Variable de tipo String con el login o nombre de usuario para conectarse a la base de datos.
     * @param String passwordUser Variable de tipo String con el password o contraseña para conectarse a la base de datos.
     *
     */
    public KeelToDb(String driverNameUser, String databaseURLUser, String tableNameUser, String loginUser, String passwordUser) {
        driverName = driverNameUser;
        databaseURL = databaseURLUser;
        tableName = tableNameUser;
        login = loginUser;
        password = passwordUser;
        nullValue = "null";

    }

    /*
     * Este método llama al método Start de la clase superior Exporter para
     * cargar los datos del fichero Keel y posteriormente hace una llamada
     * al método Save() para crear una nueva tabla en la base de datos.
     *
     * @param  String pathnameInput Variable con la ruta del fichero de datos keel.
     *
     * @throws Exception.
     */
    @Override
    public void Start(String pathnameInput) throws Exception {

        super.Start(pathnameInput);

        Save();


    }//end Start()

    /*
     * Método utilizado para crear la tabla en la base de datos SQL
     * a partir de los datos almacenados en el vector de objetos de la clase
     * Attribute, el vector data[], y la variable nameRelation.
     *
     * @throws Exception.
     *
     */
    public void Save() throws Exception {
        Pattern p;
        Matcher m;
        int i = 0;
        int j = 0;
        int type = -1;
        String typeTable = new String();
        String element = new String();
        String line = new String();
        String nameAttribute = new String();

//Se declara la variable de tipo conexion, que guardará la conexión a la base de datos
        Connection conn;
//Se declara la variable de tipo Statement que nos permitirá ejecutar sentencias
        Statement statement;
//Se declara la variable de tipo Resulset para recoger lo que devuelve una sentencia SELECT
        ResultSet result;



// Se carga el driver
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

                p = Pattern.compile("[^A-Za-z0-9_$#]+");
                m = p.matcher(tableName);
                tableName = m.replaceAll("");

                if (tableName.length() > 30) {
                    tableName = tableName.substring(0, 30);                //Se elimina la tabla en caso de que ya existiese
                }
                statement.execute("DROP TABLE " + tableName);

            } catch (SQLException e) {
            }


            //Esto es codigo SQL
            statement.execute("CREATE TABLE " + tableName);


            for (i = 0; i < numAttributes; i++) {
                type = attribute[i].getType();

                if (type == NOMINAL || type == -1) {
                    typeTable = "text";
                }
                if (type == REAL || type == INTEGER) {
                    typeTable = "double";
                }
                if (type == INTEGER) {
                    typeTable = "integer";
                }
                nameAttribute = attribute[i].getName();

                if (nameAttribute.contains(" ")) {
                    StringTokenizer tokenUcfirts = new StringTokenizer(nameAttribute, " ");
                    String lineUcfirts = "";
                    if (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = tokenUcfirts.nextToken();
                    }
                    while (tokenUcfirts.hasMoreTokens()) {
                        lineUcfirts = lineUcfirts.concat(UcFirst(tokenUcfirts.nextToken()));
                    }

                    nameAttribute = lineUcfirts;
                }

                p = Pattern.compile("[^A-Za-z0-9_$#]+");
                m = p.matcher(nameAttribute);
                nameAttribute = m.replaceAll("");

                if (nameAttribute.length() > 30) {
                    nameAttribute = nameAttribute.substring(0, 30);
                }
                if (nameAttribute.equals("")) {
                    nameAttribute = "ATTRIBUTE_" + (i + 1) + "";
                }
                nameAttribute = "\"" + nameAttribute + "\"";

                statement.execute("ALTER TABLE " + tableName + " ADD " + nameAttribute + " " + typeTable);

            }


            for (i = 0; i < data[0].size(); i++) {
                line = "";
                for (j = 0; j < numAttributes; j++) {
                    element = (String) data[j].elementAt(i);

                    type = attribute[j].getType();

                    if (type == NOMINAL && !element.equals(nullValue)) {
                        element = "'" + element + "'";
                    }
                    if (j == numAttributes - 1) {
                        line = line.concat(element);
                    } else {
                        line = line.concat(element + ",");
                    }
                }

                statement.execute("INSERT INTO " + tableName + " VALUES (" + line + ")");
            }
            statement.close();
            conn.close();

        } catch (Exception e) {
            System.out.println(e);
            System.exit(2);
        }

        attribute = null;
        data = null;

        System.out.println("La tabla " + tableName + " ha sido creada correctamente");


    }//end Save()

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
}// end class KeelToDb

