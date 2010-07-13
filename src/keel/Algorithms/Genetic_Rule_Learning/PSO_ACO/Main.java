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

package keel.Algorithms.Genetic_Rule_Learning.PSO_ACO;

import java.util.StringTokenizer;
import org.core.Fichero;

/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class Main {

    String fTrainC;
    String fTrain;
    String fTest;
    String fOutTrain;
    String fOutTest;
    String fOutResult;
    long semilla;
    int maxCasosSinCubrir;
    int numParticulas;
    int tamEntorno;
    int maxIteraciones;
    int minimoCasosCubiertos;
    float x;
    float c1;
    float c2;
    int flag;

    private PsoAco algoritmo;


    // private PSO algoritmo;
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

//************************************************************************

         una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //maximoDatosSinCubrir
        maxCasosSinCubrir = Integer.parseInt(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //minimoCasosRegla
        minimoCasosCubiertos = Integer.parseInt(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //maxIteracionesSinConverger
        maxIteraciones = Integer.parseInt(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //Tamaño de entorno
        tamEntorno = Integer.parseInt(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //Numero de Particulas
        numParticulas = Integer.parseInt(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //x
        x = Float.parseFloat(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //c1
        c1 = Float.parseFloat(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //c2
        c2 = Float.parseFloat(datos.nextToken());

        una_linea = linea.nextToken(); //Leo una linea
        datos = new StringTokenizer(una_linea, " = \" ");
        datos.nextToken(); //flag
        flag = Integer.parseInt(datos.nextToken());

    }

    private void muestraParametros() {

        System.out.println("Argumentos leidos desde el fichero de parametros ");
        System.out.println();
        System.out.println("Maximo de datos sin cubrir: " + maxCasosSinCubrir);
        System.out.println("Minimo de casos sin cubrir por una regla: " +
                           minimoCasosCubiertos);
        System.out.println("Maximo de iteraciones sin converger: " +
                           maxIteraciones);
        System.out.println("Tamaño de entorno: " + tamEntorno);
        System.out.println("Variable X : " + x);
        System.out.println("Variable C1: " + c1);
        System.out.println("Variable C2: " + c2);
        System.out.println("Semilla: " + semilla);
        System.out.println("Numero de Particulas " + numParticulas);
        System.out.println();
        System.out.println("Fichero de entrenamiento: " + fTrain);
        System.out.println("Fichero de entrenamiento entero: " + fTrainC);
        System.out.println("Fichero de test: " + fTest);
        System.out.println("Fichero de salida de entrenamiento: " + fOutTrain);
        System.out.println("Fichero de Salida de test: " + fOutTest);
    }


    private void execute() {

        algoritmo = new PsoAco(fTrain, fTrainC, fTest, fOutTrain, fOutTest,
                               fOutResult, semilla, maxCasosSinCubrir,
                               numParticulas, tamEntorno, maxIteraciones,
                               minimoCasosCubiertos, x, c1, c2, flag);

        muestraParametros();
        algoritmo.run();
        //algoritmo.sacaResultadosAFicheros();
        //algoritmo.muestraResultados();


    }

    public static void main(String[] args) {
        Main ppal = new Main();
        ppal.extraeArgumentos(args[0]);
        //ppal.muestraParametros();
        ppal.execute();

    }


}

