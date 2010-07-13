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
 * @author Written by Rosa Venzala 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Riona;

import java.util.StringTokenizer;
import org.core.Fichero;

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;

public class Main {
/**
 * <p>
 * Main class
 * </p>
 */
    private String trainFile; 
    private String evaluationFile; 
    private String testFile;
    private String outTrainFile;
    private String outTestFile;
    private String outFile;
    private long seed;

    public Main() {
    }


    private void initArguments(String nomFichero) {
        StringTokenizer line, data;
        String fichero = Fichero.leeFichero(nomFichero); 
        String aLine;
        line = new StringTokenizer(fichero, "\n\r");
        line.nextToken();
        aLine = line.nextToken(); 
        data = new StringTokenizer(aLine, " = \" ");
        data.nextToken(); 
        trainFile = data.nextToken();
        trainFile = data.nextToken();
        testFile = data.nextToken();
        aLine = line.nextToken(); 
        data = new StringTokenizer(aLine, " = \" ");
        data.nextToken(); 
        outTrainFile = data.nextToken();
        outTestFile = data.nextToken();
        outFile = data.nextToken();
	
        aLine = line.nextToken(); //Leo una linea
        data = new StringTokenizer(aLine, " = \" ");
        data.nextToken(); 
	seed = Long.parseLong(data.nextToken());
	
    };


    private void execute() {
    	Riona riona=new Riona(trainFile,testFile,outTrainFile,outTestFile,outFile,seed);
    }

    /**
     * <p>
     * Main program
     * </p>
     * @param args Contents the name of the in-put file<br/>
     * Format:<br/>
     * <em>algorith = &lt;nombre algoritmo></em><br/>
     * <em>inputData = "&lt;fichero training&gt;" "&lt;fichero validacion&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <em>outputData = "&lt;fichero training&gt;" "&lt;fichero test&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = valor</em> (si se usa semilla)<br/>
     * <em>&lt;Descripcion1&gt; = &lt;valor1&gt;</em><br/>
     * <em>&lt;Descripcion2&gt; = &lt;valor2&gt;</em> ... (por si hay mas argumentos)<br/>
     */
    public static void main(String args[]) {
        Main myMain = new Main();
        myMain.initArguments(args[0]); //Solo cogere el primer argumento (nombre del fichero)
        System.err.println("Executing Riona");
        myMain.execute();
    }
 }   
    
   


