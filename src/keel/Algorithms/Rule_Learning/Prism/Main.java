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

/**
 * <p>
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Prism;

import java.util.StringTokenizer;
import org.core.Fichero;

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;
import java.util.*;
import org.core.*;

public class Main {
/**
 * <p>
 * Reads the parameters y starts the PRISM CD algorithm
 * </p>
 */
    private String ficheroTrain;
    private String ficheroEval; 
    private String ficheroTest;
    private String ficheroSalidatr;
    private String ficheroSalidatst;
    private String ficheroSalida;
    private long semilla;

    /**
     * <p>
     * Constructor
     * </p>
     */
    public Main() {
    }

    /**
     * <p>
     * Gets all the information from the parameters file.
     * First, reads the name of the train and test files.
     * After, reads where we want to store the results.
     * Finally, reads the algorithm's parameters.
     * </p>
     * @param nomFichero Name of th parameter's file
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
	
    };

    /**
     * <p>
     * Starts the Prism algorithm
     * </p>
     */
    private void ejecutar() {
	Prism prism=new Prism(ficheroTrain,ficheroTest,ficheroSalidatr,ficheroSalidatst,ficheroSalida,semilla);
    }

    /**
     * <p>
     * Main program
     * </p>
     * @param args Name of the parameter's file<br/>
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
        System.err.println("Executing PRISM.");
        mimain.ejecutar();
    }
 }   
    
   


