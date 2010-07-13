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

package keel.Algorithms.Genetic_Rule_Learning.Advanced_Ant_Miner;

import java.util.StringTokenizer;
import org.core.Fichero;

public class Main {

  private int numHormigas;
  private int maximoDatosSinCubrir;
  private int minimoCasosRegla;
  private int maxIteracionesSinConverger;
  private float alfa;
  private float beta;
  private float maxFeromona;
  private float minFeromona;
  private long semilla;
  private int discretizado;

  private String fTrain;
  private String fTrainC;
  private String fTest;
  private String fOutTrain;
  private String fOutTest;
  private String fOutResult;
  private String fStop;

  private ACO algoritmo;

  public Main() {

  }

  private void extraeArgumentos(String ficheroParametros) {
    StringTokenizer linea, datos;
    String fichero = Fichero.leeFichero(ficheroParametros); //guardo todo el fichero como un String para procesarlo:
    String una_linea;

    linea = new StringTokenizer(fichero, "\n\r");
    linea.nextToken(); //Paso del nombre del algoritmo

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //inputData

    fTrain = datos.nextToken(); //fichero de entrenamiento
    fTrainC = datos.nextToken(); //fichero de entrenamiento sin preprocesamiento
    fTest = datos.nextToken(); //fichero de test
    fStop = fTrainC;
    una_linea = linea.nextToken(); //Leo una linea

    datos = new StringTokenizer(una_linea, " = \" ");

    datos.nextToken(); //outputData
    fOutTrain = datos.nextToken();
    fOutTest = datos.nextToken();
    fOutResult = datos.nextToken();

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //seed
    semilla = Long.parseLong(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //nunHormigas
    numHormigas = Integer.parseInt(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //maximoDatosSinCubrir
    maximoDatosSinCubrir = Integer.parseInt(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //minimoCasosRegla
    minimoCasosRegla = Integer.parseInt(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //maxIteracionesSinConverger
    maxIteracionesSinConverger = Integer.parseInt(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //Maximo de feromona
    maxFeromona = Float.parseFloat(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //Minimo de feromona
    minFeromona = Float.parseFloat(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //alfa
    alfa = Float.parseFloat(datos.nextToken());

    una_linea = linea.nextToken(); //Leo una linea
    datos = new StringTokenizer(una_linea, " = \" ");
    datos.nextToken(); //beta
    beta = Float.parseFloat(datos.nextToken());
  }

  private void muestraParametros() {

    System.out.println("Argumentos leidos desde el fichero de parametros ");
    System.out.println();
    System.out.println("Numero de hormigas: " + numHormigas);
    System.out.println("Maximo de datos sin cubrir: " + maximoDatosSinCubrir);
    System.out.println("Minimo de casos sin cubrir por una regla: " +
                       minimoCasosRegla);
    System.out.println("Maximo de iteraciones sin converger: " +
                       maxIteracionesSinConverger);
    System.out.println("Maximo de feromona permitido: " + maxFeromona);
    System.out.println("Minimo de feromona permitido: " + minFeromona);
    System.out.println("Alfa: " + alfa);
    System.out.println("Beta: " + beta);
    System.out.println("Semilla: " + semilla);
    System.out.println();
    System.out.println("Fichero de entrenamiento: " + fTrain);
    System.out.println("Fichero de entrenamiento entero: " + fTrainC);
    System.out.println("Fichero de test: " + fTest);
    System.out.println("Fichero de Parada Temprana" + fStop);
    System.out.println("Fichero de salida de entrenamiento: " + fOutTrain);
    System.out.println("Fichero de Salida de test: " + fOutTest);
  }

  private void execute() {
    algoritmo = new ACO(fTrain, fTrainC, fTest, fStop, fOutTrain,
                        fOutTest, fOutResult, numHormigas, maximoDatosSinCubrir,
                        minimoCasosRegla, maxIteracionesSinConverger,
                        minFeromona, maxFeromona, alfa, beta, semilla);

    if (algoritmo.OK()) {
      algoritmo.run();
      algoritmo.sacaResultadosAFicheros();
      algoritmo.muestraResultados();
    }
    else {
      System.err.println("Aborting execution!");
    }

  }

  public static void main(String[] args) {
    Main ppal = new Main();
    ppal.extraeArgumentos(args[0]);
    //ppal.muestraParametros();
    ppal.execute();

  }

}

