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

package keel.Algorithms.Subgroup_Discovery.aprioriSD;

import java.util.StringTokenizer;
import org.core.Fichero;

/**
 /**
 * <p>Title: Main Class of the Program</p>
 *
 * <p>Description: It reads the configuration file (data-set files and parameters) and launch the algorithm</p>
 * @author José Ramón Cano de Amo 28-7-2004.
 * @version 1.0
*/
public class Main {

  private String ficheroTrain; //Fichero de entramiento -> Por ejemplo discretizado
  private String ficheroEval; //Fichero de evaluación -> original
  private String ficheroTest;
  private String ficheroSalidatr;
  private String ficheroSalidatst;
  private String ficheroSalida;
  private double Smin;
  private double Cmin;
  private int N;
  private int postpoda;

    /** Default constructor. */
    public Main() {
    }

    /**
     * Obtiene toda la información necesaria del fichero de parametros<br/>
     * En primer lugar lee el nombre de los archivos de datos de entrenamiento y test<br/>
     * Posteriormente lee los ficheros donde queremos guardar las salidas<br/>
     * Por último lee los parametros del algoritmo, tales como la semilla o el nº de iteraciones<br/>
     *
     * @param nomFichero Nombre del fichero de parametros
     *
     */
        /**
     * Obtains al the information needed from the configuration file given.
     *
     * @param nomFichero given configuration filename.
     *
     */
    private void preparaArgumentos(String nomFichero){
      StringTokenizer linea,datos;
      String fichero = Fichero.leeFichero(nomFichero); //guardo todo el fichero como un String para procesarlo:
      String una_linea;
      linea = new StringTokenizer(fichero,"\n\r");
      linea.nextToken(); //Paso del nombre del algoritmo
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken(); //inputData
      ficheroTrain = datos.nextToken();
      ficheroEval = datos.nextToken(); //fichero de evaluación
      ficheroTest = datos.nextToken();
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken(); //outputData
      ficheroSalidatr = datos.nextToken();
      ficheroSalidatst = datos.nextToken();
      ficheroSalida = datos.nextToken();
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken();
      Smin = Double.parseDouble(datos.nextToken()); //minimo support
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken();
      Cmin = Double.parseDouble(datos.nextToken()); //minimo confidence
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken();
      N = Integer.parseInt(datos.nextToken()); //Numero de reglas
      una_linea = linea.nextToken(); //Leo una linea
      datos = new StringTokenizer(una_linea," = \" ");
      datos.nextToken(); //postpoda
      String postpodaAux = datos.nextToken();
      postpoda = 0;
      if (postpodaAux.compareTo("SELECT_N_RULES_PER_CLASS") == 0){
        postpoda = 1;
        }
    };

    /**
     *  Executes the  AprioriSD algorithm
     */
    private void ejecutar(){
        aprioriSD algoritmo = new aprioriSD(ficheroTrain,ficheroEval,ficheroTest,ficheroSalidatr,ficheroSalidatst,ficheroSalida,Smin,Cmin,N,postpoda);
        if (algoritmo.todoBien()){
        	algoritmo.ejecutar();
    	}
    }

    /**
     * Main Program
     * @param args It contains the name of the configuration file<br/>
     * Format:<br/>
     * <em>algorith = &lt;algorithm name></em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;validation file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = value</em> (if used)<br/>
     * <em>&lt;Parameter1&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;Parameter2&gt; = &lt;value2&gt;</em> ... <br/>
     */
  public static void main(String args[]) {

    Main aprioriSD_ = new Main();

    aprioriSD_.preparaArgumentos(args[0]); //Solo cogere el primer argumento (nombre del fichero)

    System.err.println("Executing APRIORISD.");

    aprioriSD_.ejecutar();

  }
}

