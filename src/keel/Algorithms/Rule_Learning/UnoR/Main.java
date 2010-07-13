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

package keel.Algorithms.Rule_Learning.UnoR;

import java.util.StringTokenizer;
import org.core.Fichero;

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;

/**
 * <p>Title: Clase principal del programa</p>
 *
 * <p>Description: Lee los parametros y lanza el algoritmo 1R SD</p>
 *
 * <p>Copyright: Copyright (c) Rosa 2007</p>
 *
 * <p>Company: Yo</p>
 *
 * @author Rosa Venzala
 * @version 1.0
 */
public class Main {
    private String ficheroTrain; //Fichero de entramiento -> Por ejemplo discretizado
    private String ficheroEval; //Fichero de evaluaciï¿½ -> original
    private String ficheroTest;
    private String ficheroSalidatr;
    private String ficheroSalidatst;
    private String ficheroSalida;
    private long semilla;
    private int SMALL;

    /** Constructor por defecto */
    public Main() {
    }

    /**
     * Obtiene toda la informaciï¿½ necesaria del fichero de parametros<br/>
     * En primer lugar lee el nombre de los archivos de datos de entrenamiento y test<br/>
     * Posteriormente lee los ficheros donde queremos guardar las salidas<br/>
     * Por ltimo lee los parametros del algoritmo<br/>
     *
     * @param nomFichero Nombre del fichero de parametros
     *
     */
    private void preparaArgumentos(String nomFichero) {
        StringTokenizer linea, datos;
        String fichero = Fichero.leeFichero(nomFichero); //guardo todo el fichero como un String para procesarlo:
        String una_linea;
        linea = new StringTokenizer(fichero, "\n\r");
        linea.nextToken(); //Paso del nombre del algoritmo
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //inputData
        ficheroTrain = datos.nextToken();
	ficheroTrain = datos.nextToken();
	//System.err.println(ficheroTrain);
       // ficheroEval = datos.nextToken(); //fichero de evaluaciï¿½
        ficheroTest = datos.nextToken();
        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //outputData
        ficheroSalidatr = datos.nextToken();
	//System.err.println(ficheroSalidatr);
        ficheroSalidatst = datos.nextToken();
        ficheroSalida = datos.nextToken();
	
	una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); 
	semilla = Long.parseLong(datos.nextToken());
	
	una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //SMALL
        SMALL = Integer.parseInt(datos.nextToken());
	
    };

    /**Lanza el programa Prism
     */
    private void ejecutar() {
	UnoR uno_r=new UnoR(ficheroTrain,ficheroTest,ficheroSalidatr,ficheroSalidatst,ficheroSalida,semilla,SMALL);
    }

    /**
     * Programa principal
     * @param args Contendra el nombre del fichero de parametros<br/>
     * Formato:<br/>
     * <em>algorith = &lt;nombre algoritmo></em><br/>
     * <em>inputData = "&lt;fichero training&gt;" "&lt;fichero validacion&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <em>outputData = "&lt;fichero training&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = valor</em> (si se usa semilla)<br/>
     * <em>&lt;Descripcion1&gt; = &lt;valor1&gt;</em><br/>
     * <em>&lt;Descripcion2&gt; = &lt;valor2&gt;</em> ... (por si hay mas argumentos)<br/>
     */
    public static void main(String args[]) {
        Main mimain = new Main();
        mimain.preparaArgumentos(args[0]); //Solo cogere el primer argumento (nombre del fichero)
        System.err.println("Executing 1-R.");
        mimain.ejecutar();
    }
 }   
    
   


