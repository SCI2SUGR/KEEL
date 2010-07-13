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
 * <p>Título: Programa principal de Cn2</p>
 * <p>Descripción: Es la clase ppal, la que se ejecuta al lanzar el programa</p>
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

    /** Constructor por defecto */
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
     * Lanza el programa AprioriSD
     */
    private void ejecutar(){
        aprioriSD algoritmo = new aprioriSD(ficheroTrain,ficheroEval,ficheroTest,ficheroSalidatr,ficheroSalidatst,ficheroSalida,Smin,Cmin,N,postpoda);
        if (algoritmo.todoBien()){
        	algoritmo.ejecutar();
    	}
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

    Main aprioriSD_ = new Main();

    aprioriSD_.preparaArgumentos(args[0]); //Solo cogere el primer argumento (nombre del fichero)

    System.err.println("Executing APRIORISD.");

    aprioriSD_.ejecutar();

  }
}

