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

import keel.Dataset.*;
import org.core.*;

import java.math.*;
import java.util.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * <p>Title: Ant Colony Optimization</p>
 * <p>Description: Classification Algorithm by ACO (Advanced Ant Miner).
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class ACO {

  private float[][] feromona; //Aqui se almacenara la feromona para cada valor de cada atributo
  private float[][] probabilidad; //Aqui se guardan las probabilidades de cada condicion
  private float[] feromonaClases; //Aqui vendra la feromona para las clases
  private int[] numeroValoresMuestras; //Vector que guarda el numero de valores de cada atributo
  private int[] numeroValoresReglas; //Vector que guarda el numero de valores de las reglas
  private float[][] funcionH; //Matroz con todos los valores Heuristicos necesarios
  private float[] funcionHClases; //Heuristica de las clases
  private Vector listaValores; //Aqui se almacenaran los distintos valores que puede alcanzar cada valor (ordenados de menor a mayor)
  private Vector listaCondiciones; //Aqui se almacenaran los distintos valores que puede tomar una condicion
  private Vector listaAtributosVacios; //Aqui se almacenaran los distintas condiciones vacias
  private Vector listaCondicionesVacias; //Aqui se guardan los distintas condiciones vacia
  private Vector listaClases; //Vector de nombres de cada clase
  private Vector reglasDescubiertas; //Reglas descubiertas por las hormigas, inicialmente vacio
  private Regla generica; //Regla generica que se construye al principio del bucle while
  private Vector reglasHormigas; //Reglas descubiertas por las hormigas en una iteracion del while
  private ConjuntoDatos cTrain; // Muestras del fichero de entrenamiento preprocesado
  private ConjuntoDatos cTrainC; //Muestras del fichero de entrenamiento sin procesamiento previo
  private ConjuntoDatos cTest; //Muestras del fichero de prueba
  private ConjuntoDatos cStop; //Conjunto de muestras para la parada temprana
  private ConjuntoDatos muestrasCubiertas; //Muestras que han sido cubiertas por las reglas creadas
  private float errorParada;

  private int numHormigas;
  private int maximoDatosSinCubrir;
  private int minimoCasosRegla;
  private int maxIteracionesSinConverger;
  private int numAtributos; //Numero de atributos que tiene una muestra
  private int numAtributosReglas; //Numero de atributos que tiene una regla
  private long semilla;
  private float factorEvap;

  private int[] tieneCondicionVacia;

  private float maxFeromona; //Limites de Feromona
  private float minFeromona;

  private float alfa; //Parametros para el calculo de la probabilidad
  private float beta;

  private float porcentajeTrain;
  private float porcentajeTest;

  private String fInTrainP;
  private String fInTrainC;
  private String fInTest;
  private String fInStop;
  private String fOutTrain;
  private String fOutTest;
  private String fOutResult;

  float errorNuevo;

  private String cabeceraTrain;
  private String cabeceraTest;
  private Randomize generadorA;

  boolean primero = false;

  boolean continuosValues; //Indica si hay que comprobar si los datos son compatibles con el algoritmo

  /**
   * Default constructor. Nothing is done.
   */
  public ACO() {
  }

  /**
   * All is OK if the data-sets have not got any continuos values
   * @return boolean a flag for the existence of continous values in the data-set (true if there is none continuous values.
   */
  public boolean OK() {
    return (!continuosValues);
  }

  /**
   * Parameter constructor. Build the algorithm object with the parameter given.
   *
   * @param fTrainPrep String Preprocessed Training filename.
   * @param fTrain String Full training filename.
   * @param fTestOriginal String Test filename.
   * @param fStop String Stop dataset filename. 
   * @param fSalidaTrain String Training output filename.
   * @param fSalidaTest String Test output filename.
   * @param fSalidaResult String Global results filename.
   * @param nHormigas int Number of ants considered.
   * @param maxDatos int Maximum number of uncovered data.
   * @param minCasos int Minimum number of cases that a rule must cover.
   * @param maxIter int Maximum iterations.
   * @param minimoFeromona float Minimum pheromone value.
   * @param maximoFeromona float Maximum pheromone value.
   * @param alfaIn float alpha parameter.
   * @param betaIn float beta parameter.
   * @param semillaOriginal long Seed for the random numbers generator.
   */

  public ACO(String fTrainPrep, String fTrain, String fTestOriginal,
             String fStop, String fSalidaTrain,
             String fSalidaTest, String fSalidaResult, int nHormigas,
             int maxDatos, int minCasos, int maxIter,
             float minimoFeromona, float maximoFeromona, float alfaIn,
             float betaIn, long semillaOriginal) {

    numHormigas = nHormigas;
    maximoDatosSinCubrir = maxDatos;
    minimoCasosRegla = minCasos;
    maxIteracionesSinConverger = maxIter;
    semilla = semillaOriginal;
    minFeromona = minimoFeromona;
    maxFeromona = maximoFeromona;
    alfa = alfaIn;
    beta = betaIn;
    factorEvap = (float) 0.85;

    fInTrainP = new String(fTrainPrep);
    fInTrainC = new String(fTrain);
    fInTest = new String(fTestOriginal);
    fInStop = new String(fStop);
    fOutTrain = new String(fSalidaTrain);
    fOutTest = new String(fSalidaTest);
    fOutResult = new String(fSalidaResult);
    cabeceraTest = new String("");
    cabeceraTrain = new String("");

    listaValores = new Vector();
    listaCondiciones = new Vector();
    listaAtributosVacios = new Vector();
    //listaValoresRestantes=new Vector();
    listaClases = new Vector();
    reglasDescubiertas = new Vector();
    reglasHormigas = new Vector();
    listaCondicionesVacias = new Vector();

    generadorA = new Randomize();
    generadorA.setSeed(semilla);

    extraeDatos(); //Una vez asignadas las caracteristicas extraemos los datos de los ficheros
  }

  /**
   * 
   * Extracts the dataset from the training, full-training and test files into
   * the algorithm structures {@link ConjuntoDatos}.
   */
  private void extraeDatos() {

    myDataset dTrain = new myDataset();
    myDataset dTrainC = new myDataset();
    myDataset dTest = new myDataset();
    myDataset dStop = new myDataset();

    try {
      dTrain.readClassificationSet(fInTrainP, true);
      if (dTrain.hayAtributosContinuos()) {
        System.err.println(
            "Ant_Miner may not handle continuous attributes.\nPlease discretize the data-set");
        continuosValues = true;
      }
      dTrainC.readClassificationSet(fInTrainC, false);
      if (dTrainC.hayAtributosContinuos()) {
        System.err.println(
            "Ant_Miner may not handle continuous attributes.\nPlease discretize the data-set");
        continuosValues = true;
      }
      dTest.readClassificationSet(fInTest, false);
      dStop.readClassificationSet(fInStop, false);
    }
    catch (IOException e) {
      System.err.println("Problema leyendo los conjuntos de datos:");
      System.err.println("-> " + e);
      System.exit(0);
    }

    creaDatos();
    cTrain = extraeMuestrasSinP(dTrain);
    cTrainC = extraeMuestrasSinP(dTrainC);
    cTest = extraeMuestrasSinP(dTest);
    cStop = extraeMuestrasSinP(dStop);

    cabeceraTrain = dTrainC.copyHeader();
    cabeceraTest = dTest.copyHeader();

  }

  /**
   * Extracts the data from the given dataset into an algorithm structure {@link ConjuntoDatos}.
   * 
   * @param original myDataset dataset to extract the data. 
   * @return {@link ConjuntoDatos} dataset structure used by the ACO algorithm. 
   */
  private ConjuntoDatos extraeMuestras(myDataset original) {
    double[][] X;
    int[] C;

    int nDatos;
    int nAtributos;
    Atributo at;
    Muestra mt;

    Vector muestras = new Vector();
    ConjuntoDatos devolver;

    X = original.getX();
    C = original.getC();

    nDatos = original.getndatos();
    nAtributos = original.getnentradas();

    Attribute[] atv = Attributes.getInputAttributes();

    //Construimos el Conjunto de Datos con los datos ya preprocesados
    muestras = new Vector();
    for (int i = 0; i < nDatos; i++) {
      mt = new Muestra();
      for (int j = 0; j < nAtributos; j++) {
        if (X[i][j] == -1) { //Se encuentra perdido
          at = (Atributo) listaAtributosVacios.get(j);
        }
        else {
          at = (Atributo) ( (Vector) listaValores.get(j)).get( (int) X[i][j]); //Cogemos el puntero que señala al atributo almacenado en lista de valores
        }
        mt.insertarAtributo(at);
      }
      at = (Atributo) listaClases.get(C[i]);
      mt.insertaPosicion(i);
      mt.insertarClase(at);
      muestras.addElement(mt);
    }

    devolver = new ConjuntoDatos(muestras);

    return devolver;
  }


  /**
   * Extracts the unprocessed data from the given dataset into an algorithm structure {@link ConjuntoDatos}.
   * 
   * @param original myDataset dataset to extract the data. 
   * @return {@link ConjuntoDatos} dataset structure used by the ACO algorithm. 
   */
  private ConjuntoDatos extraeMuestrasSinP(myDataset original) {
    double[][] X;
    int[] C;

    int nDatos;
    int posicion;
    int nAtributos;
    Atributo at;
    Muestra mt;

    Vector muestras = new Vector();
    ConjuntoDatos devolver;

    X = original.getX();
    C = original.getC();

    nDatos = original.getndatos();
    nAtributos = original.getnentradas();

    Attribute[] atv = Attributes.getInputAttributes();
    Attribute atD;

    //Construimos el Conjunto de Datos con los datos ya preprocesados
    muestras = new Vector();
    for (int i = 0; i < nDatos; i++) {
      mt = new Muestra();
      for (int j = 0; j < nAtributos; j++) {
        posicion = (int) X[i][j];
        atD = atv[j];
        if (atD.getType() == 1) {
          posicion = posicion - 1; //En los enteros se empieza a contar desde 1 y el vector desde 0 [0,9] = [1,10]
        }
        //System.out.println("------->"+X[i][j]+"  "+posicion+"  "+i+"  "+j);
        if (posicion == -1) { //Es un valor perdido
          at = (Atributo) listaAtributosVacios.get(j);
        }
        else {
          at = (Atributo) ( (Vector) listaValores.get(j)).get(posicion); //Cogemos el puntero que señala al atributo almacenado en lista de valores
        }
        mt.insertarAtributo(at);
      }
      at = (Atributo) listaClases.get(C[i]);
      mt.insertaPosicion(i);
      mt.insertarClase(at);
      muestras.addElement(mt);
    }

    devolver = new ConjuntoDatos(muestras);

    return devolver;
  }

  /**
   * Creates the arrays and lists of data needed for the well functioning of the algorithm:
   * - The list with the different values each attribute can take
   * - The list with the different values of the output class.
   */
  private void creaDatos() {

    Attribute[] listaAtributos;
    Attribute actual;
    Atributo insertar;
    Condicion insertarConMenor;
    Condicion insertarConMayor;
    Condicion insertarCon;
    Condicion vacia;
    Atributo atributoVacio;
    Vector valores;
    Vector nombres;
    Vector condicionesIgual;
    Vector condicionesMenor;
    Vector condicionesMayor;
    int maximo;
    int minimo;

    String nombre;

    //Crear lista de distintos atributos posibles
    listaAtributos = Attributes.getInputAttributes();
    numAtributos = listaAtributos.length;
    //inicializamos el vector con el numero de valores de cada atibuto
    numeroValoresMuestras = new int[numAtributos];

    //Para cada atributo creamos sus distintos valores en Atributos y Condiciones
    for (int i = 0; i < listaAtributos.length; i++) {
      valores = new Vector();
      actual = listaAtributos[i];
      //El tipo del atributo es entero
      //otra forma de coger sus valores (no ha habido discretizacion)
      if (actual.getType() == 1) {
        condicionesMenor = new Vector(); //Lista de condiciones
        condicionesMayor = new Vector();

        maximo = (int) actual.getMaxAttribute();
        minimo = (int) actual.getMinAttribute();
        numeroValoresMuestras[i] = (maximo - minimo) + 1; //Numero de valores de este atributo
        //Recorremos los distintos valores numericos que tiene insertando atributos
        //y condiciones
        for (int j = minimo; j <= maximo; j++) {
          insertar = new Atributo(String.valueOf(j), i, false); //Atributo
          insertarConMenor = new Condicion(insertar, 1); //Condicion <
          insertarConMayor = new Condicion(insertar, 2); //Condicion >
          valores.addElement(insertar); //Insertamos el atributo
          condicionesMenor.addElement(insertarConMenor); //insertamos la condicion <
          condicionesMayor.addElement(insertarConMayor); //Insertamos la condicion >
        }
        listaValores.addElement(valores); //Insercion del vector de los atributos
        //Insercion de las condiciones
        atributoVacio = new Atributo("Null", i, false); //Atributo vacio
        vacia = new Condicion(atributoVacio, 0); //Condicion vacia
        listaAtributosVacios.addElement(atributoVacio); //Insercion A Vacio
        listaCondicionesVacias.addElement(vacia); //Insercion C Vacia
        listaCondicionesVacias.addElement(vacia);
        listaCondiciones.addElement(condicionesMayor); //Se insertan las condiciones
        listaCondiciones.addElement(condicionesMenor);
      }
      else { //El tipo del atributo es nominal (se han discretizado los datos)
        nombres = actual.getNominalValuesList();
        condicionesIgual = new Vector();
        //Recorremos los atributos y los insertamos, lo mismo con las condiciones
        for (int j = 0; j < nombres.size(); j++) {
          insertar = new Atributo( (String) nombres.get(j), i, true);
          insertarCon = new Condicion(insertar, 0);
          condicionesIgual.addElement(insertarCon);
          valores.addElement(insertar);
        }
        numeroValoresMuestras[i] = nombres.size(); //Numero de valores de este atributo
        listaValores.addElement(valores); //Insercion del atributo
        //Insercion de las condiciones
        atributoVacio = new Atributo("Null", i, true);
        listaAtributosVacios.addElement(atributoVacio);
        vacia = new Condicion(atributoVacio, 0);
        listaCondicionesVacias.addElement(vacia);
        listaCondiciones.addElement(condicionesIgual);
      }

    }

    //Crear lista de nombres de clases
    actual = Attributes.getOutputAttribute(0); //Solo tenemos un atributo de salida en esta clasificacion
    nombres = actual.getNominalValuesList();
    for (int i = 0; i < nombres.size(); i++) {
      insertar = new Atributo( (String) nombres.get(i), -1, true); //Las clases son categoricas
      listaClases.addElement(insertar);
    }

    //Ahora reservo el numero de condiciones
    //Y los diferentes valores de cada una
    //Tambien se reserva el sitio para la feromona
    numAtributosReglas = listaCondiciones.size();
    numeroValoresReglas = new int[numAtributosReglas];
    probabilidad = new float[numAtributosReglas][];
    feromona = new float[numAtributosReglas][];
    funcionH = new float[numAtributosReglas][];
    feromonaClases = new float[listaClases.size()];
    funcionHClases = new float[listaClases.size()];
    for (int i = 0; i < numAtributosReglas; i++) {
      numeroValoresReglas[i] = ( (Vector) listaCondiciones.get(i)).size();
      feromona[i] = new float[numeroValoresReglas[i] + 1]; //Hay que contar tambien las condiciones
      probabilidad[i] = new float[numeroValoresReglas[i] + 1]; //vacias, que tb tienen su h,p,y f
      funcionH[i] = new float[numeroValoresReglas[i] + 1];
    }

    inicializaSiTieneCondicionVacia();

  }

  /**
   * Initializes a vector that indicates if the corresponding attribute of the rule 
   * can be assigned with a null/empty condition. 
   */
  public void inicializaSiTieneCondicionVacia() {
    tieneCondicionVacia = new int[listaCondiciones.size()];
    Attribute[] listaAtributos;
    Attribute actual;
    int j = 0;
    //Crear lista de distintos atributos posibles
    listaAtributos = Attributes.getInputAttributes();
    for (int i = 0; i < listaAtributos.length; i++) {
      actual = listaAtributos[i];
      if (actual.getType() == 1) { //Si tiene mayor y menor
        tieneCondicionVacia[j] = 0;
        j++;
        tieneCondicionVacia[j] = 0;
        j++;
      }
      else {
        tieneCondicionVacia[j] = 1;
        j++;
      }
    }

  }

  /**
   * Executes the algorithm.
   */
  public void run() {

    boolean parada = false;
    boolean convergencia = false;
    Vector hormigas = new Vector(); //Vector de hormigas
    Regla hormiga;
    ComparadorRegla c = new ComparadorRegla();
    Regla mejorHormiga = null;
    int quedanAnterior = 0;
    int quedanAhora = 0;

    System.out.println(
        "-------------------------------------------------------");
    //Creamos una regla generica y eliminamos la clase mas comun.
    Atributo clasePredefinida;
    generica = creaReglaGenerica();
    //reglasDescubiertas.addElement(generica);
    System.out.println("Regla generica creada");
    errorParada = 0;
    System.out.println("Error de parada calculado");
    //Quitar datos cubiertos por nueva regla (abajo)
    //quitarCasosCubiertosEspecial(generica);
    int numeroHormigas = 0;

    //While !paradaTemprana  o ninguna regla cubra alguna muestra(todas con calidad 0)
    while (cTrain.tamanio() > maximoDatosSinCubrir ||
           cTrain.porcentajeMuestrasClase(generica.obtenerReglaPredicha()) >=
           0.9) {
      //Inicializa feromona
      inicializaFeromona();
      //System.out.println("Feromona Inicializada");
      //Inicializa heuristicas
      calculaHeuristicasClases(); //********************************
      // System.out.println("Heuristicas de clases calculada");
      clasePredefinida = elegirClasePredefinida();
      //System.out.println("Clase predefinida calculada");
      calculaHeuristicasCondiciones(clasePredefinida);
      //System.out.println("Heuristicas de las condiciones calculadas");
      //Convergencia=0
      convergencia = false;

      System.out.println("----------------------------------------");
      int indice = 0;
      numeroHormigas = 0;
      hormigas = new Vector();
      //Inicializa probabilidades ejes
      inicializaProbabilidades();
      while (numeroHormigas < numHormigas &&
             indice < maxIteracionesSinConverger && !convergencia) {
        hormiga = new Regla();
        //System.out.println("Probabilidades calculadas");
        hormiga.insertarClase(clasePredefinida);
        //System.out.println("Hormigas creadas");
        //--hormigas van desde principio a fin
        recorreCamino(hormiga);
        //System.out.println("Caminos recorridos");
        //--evapora feromona en todos los ejes
        evaporaFeromona();
        //--poda regla de la mejor hormiga
        podaRegla(hormiga);
        //--ajusta feromona(por si ha salido intervalo)
        aumentaFeromona(hormiga);
        //Comprobar convergencia
        convergencia = comprobarConvergencia(hormiga);
        //System.out.println("Convergencia calculada");
        hormigas.addElement(hormiga);
        numeroHormigas++;
        //System.out.println(numHormigas+"   "+numeroHormigas);
        if (errorParada == errorNuevo) {
          indice++;
        }
        else {
          indice = 0;
        }
        // System.out.println("----------------------------------------");
      }
      c = Regla.obtenerComparador();
      Collections.sort(hormigas, c);
      mejorHormiga = (Regla) hormigas.get(0);
      reglasDescubiertas.addElement(mejorHormiga);
      //Quitar datos cubiertos por nueva regla (abajo)
      quitarCasosCubiertos(mejorHormiga);
      System.out.println("Casos cubiertos quitados");
      mejorHormiga.imprime(listaCondicionesVacias);
      System.out.println("Quedan " + cTrain.tamanio() + " datos");
      System.out.println("Accuracy: " + obtenerAccuracy(cStop));

      //Comprobar parada
      parada = comprobarParada(mejorHormiga);

      //Meter en comprobar parada
      if (quedanAhora == 0) {
        quedanAhora = cTrain.tamanio();
        quedanAnterior = cTrain.tamanio();
      }
      else {
        quedanAhora = cTrain.tamanio();
        if (quedanAhora == quedanAnterior) {
          parada = true;
        }
        else {
          quedanAnterior = quedanAhora;
        }
      }

    }

    //TERMINADO EL PROCESO DE BUSQUEDA
    reglasDescubiertas.addElement(generica);
    //Imprimir en ficheros las reglas y accuracys
    imprimeReglasAccuracy();
    //Imprimir las predicciones
    imprimePredicciones();

  }

  /**
   * Prints the values predicted by the extracted rules on the training and test results files.
   */
  private void imprimePredicciones() {

    File fichero;
    FileOutputStream flujo;
    PrintStream salida = null;
    Atributo clasePredicha;
    Atributo claseOriginal;
    Regla actual;
    int tamanio;
    ConjuntoDatos conjunto;
    Muestra mt;

//----------------------------------------------- Datos TRAIN
    try {
      fichero = new File(fOutTrain); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;
    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutTrain + " no se pudo crear");
      System.exit(0);
    }

    conjunto = cTrainC;
    salida.print(cabeceraTrain);

    tamanio = conjunto.tamanio();
    for (int i = 0; i < tamanio; i++) {
      mt = conjunto.obtenerMuestra(i);
      claseOriginal = mt.getClase();
      clasePredicha = null;
      for (int j = 0; j < reglasDescubiertas.size() && clasePredicha == null; j++) {
        actual = (Regla) reglasDescubiertas.get(j);
        clasePredicha = actual.prediccion(mt, listaCondicionesVacias);
      }

      if (clasePredicha == null) { //En el caso de que no lo cubran las reglas descubiertas
        clasePredicha = generica.obtenerReglaPredicha();
      }

      salida.print(claseOriginal.getValor());
      salida.print("\t" + clasePredicha.getValor());
      salida.println();
    }

//------------------------------------------------Datos TEST
    try {
      fichero = new File(fOutTest); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;
    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutTrain + " no se pudo crear");
      System.exit(0);
    }

    conjunto = cTest;
    salida.print(cabeceraTest);

    tamanio = conjunto.tamanio();
    for (int i = 0; i < tamanio; i++) {
      mt = conjunto.obtenerMuestra(i);
      claseOriginal = mt.getClase();
      clasePredicha = null;
      for (int j = 0; j < reglasDescubiertas.size() && clasePredicha == null; j++) {
        actual = (Regla) reglasDescubiertas.get(j);
        clasePredicha = actual.prediccion(mt, listaCondicionesVacias);
      }

      if (clasePredicha == null) { //En el caso de que no lo cubran las reglas descubiertas
        clasePredicha = generica.obtenerReglaPredicha();
      }

      salida.print(claseOriginal.getValor());
      salida.print("\t" + clasePredicha.getValor());
      salida.println();
    }

  }

  /**
   * Prints, on the standard output and the global results file, the extracted rules 
   * and their accuracy.
   */
  private void imprimeReglasAccuracy() {
    Regla regla;
    int i;
    Atributo clase;
    double mediaCondiciones = 0;
    int numReglas;

    File fichero;
    FileOutputStream flujo;
    PrintStream salida = null;

    try {
      fichero = new File(fOutResult); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;
    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutResult + " no se pudo crear");
      System.exit(0);
    }

    System.out.println("-------------------------------------------------");
    System.out.println("Reglas descubiertas ");

    numReglas = reglasDescubiertas.size();
    for (i = 0; i < reglasDescubiertas.size() - 1; i++) { //La regla generica se enseña despues
      regla = (Regla) reglasDescubiertas.get(i);
      regla.imprime(listaCondicionesVacias);
      salida.print("REGLA " + i + " : ");
      regla.imprimeFichero(salida, listaCondicionesVacias);
      salida.println();
      mediaCondiciones += regla.obtenerNumCondicionesReales() - 1;
    }
    //Regla generica
    System.out.println("Regla generica:");
    salida.print("REGLA DEFAULT: ");
    regla = (Regla) reglasDescubiertas.get(i);
    clase = regla.obtenerReglaPredicha();
    System.out.println("< All > ==> " + clase.getValor());
    salida.println("< ALL > ==> " + clase.getValor());
    mediaCondiciones++;

    porcentajeTrain = obtenerAccuracy(cTrainC);
    porcentajeTest = obtenerAccuracy(cTest);

    System.out.println("Porcentaje sobre Train: " + porcentajeTrain);
    System.out.println("Porcentaje sobre Test: " + porcentajeTest);
    System.out.println("Numero de reglas: " + numReglas);
    System.out.println("Numero medio de condiciones: " +
                       mediaCondiciones / (numReglas));
    salida.println("Porcentaje sobre Train: " + porcentajeTrain);
    salida.println("Porcentaje sobre Test: " + porcentajeTest);
    salida.println("Numero de reglas: " + numReglas);
    salida.println("Numero medio de condiciones: " +
                   mediaCondiciones / numReglas);

  }

  /**
   * Checks if the algorithm should stop following the stop conditions set.
   * @param mHormiga {@link Regla} Last extracted rule during the last ants iteration.
   * @return True if it should stop, false otherwise.
   */
  private boolean comprobarParada(Regla mHormiga) {
    int tamanioTest = cTrainC.tamanio();
    int tamanioTrain = cTrain.tamanio();
    Atributo predefinida = generica.obtenerReglaPredicha();

    //Conjuntos grandes

    errorNuevo = obtenerAccuracy(cStop);
    if (errorNuevo < errorParada) {
      reglasDescubiertas.remove(reglasDescubiertas.lastElement());
      return true;
    }
    else {
      errorParada = errorNuevo; //Actualizamos el error
      primero = false;
    }

    //Conjuntos pequeños
    if (tamanioTrain <= tamanioTest * 0.1) {
      return true;
    }
    if (tamanioTrain > tamanioTest * 0.1) {
      return false;
    }
    //Si el conjunto es pequeño y todas las muestras que quedan
    //son de la clase predefinida se sale
    if (cTrain.porcentajeMuestrasClase(predefinida) >= 0.9) {
      return true;
    }

    return false;

  }

  /**
   * Checks if the algorithm have converged.
   * @param hormiga {@link Regla} Last rule extracted from the pheromone matrix.  
   * @return True if the algorithm have converged, false otherwise.
   */
  private boolean comprobarConvergencia(Regla hormiga) {
    boolean encontrado = false;
    Regla extraida = new Regla();
    extraida.insertarClase(hormiga.obtenerReglaPredicha());
    Vector condiciones;
    Condicion insertar;
    int limite;
    int numCondiciones = 0;

    for (int i = 0; i < numAtributosReglas; i++) {
      condiciones = (Vector) listaCondiciones.get(i);
      encontrado = false;
      for (int j = 0; j < numeroValoresReglas[i]; j++) {
        if (feromona[i][j] > minFeromona && feromona[i][j] < maxFeromona) {
          return false;
        }
        if (feromona[i][j] >= maxFeromona) {
          if (encontrado) {
            return false;
          }
          else {
            insertar = (Condicion) condiciones.get(j);
            extraida.insertarCondicion(insertar);
            numCondiciones++;
          }
          encontrado = true;
        }
      }
      limite = numeroValoresReglas[i];
      if (feromona[i][limite] > minFeromona &&
          feromona[i][limite] < maxFeromona) {
        return false;
      }
      if (feromona[i][limite] >= maxFeromona) {
        if (encontrado) {
          return false;
        }
        else {
          insertar = (Condicion) listaCondicionesVacias.get(i);
          extraida.insertarCondicion(insertar);
        }
        encontrado = true;
      }

    }

    asignarCalidad(extraida);
    hormiga = new Regla(extraida);
    hormiga.insertaNumCondicionesReales(numCondiciones);
    return true;
  }

  /**
   * Removes all the ants given (of the last extracted rule)
   * @param hormigas Given ants to remove.
   */
  private void mataHormigas(Vector hormigas) {
    hormigas = null;
  }

  /**
   * 
   * Increases the pheromone of the conditions used in the given ant.
   * @param hormiga Best ant/rule {@link Regla} to increase the pheromone.
   */
  private void aumentaFeromona(Regla hormiga) {

    int posicion;
    int indice = 0;
    Vector valores;
    Vector condiciones;
    Condicion co;
    Atributo actual;
    Atributo comparar;
    float calidad;
    Condicion cActual;
    boolean para;

    calidad = hormiga.obtenerCalidad();
    condiciones = hormiga.listaCondiciones();

    for (int i = 0; i < numAtributosReglas; i++) {
      condiciones = hormiga.listaCondiciones();
      cActual = (Condicion) condiciones.get(i);
      //Si no es una condicion vacia
      if (!listaCondicionesVacias.contains(cActual)) {
        actual = cActual.getValor();
        posicion = i;
        //Iteramos hasta que encontremos el valor al que corresponde
        para = false;
        condiciones = (Vector) listaCondiciones.get(posicion);
        for (int p = 0; p < condiciones.size() && !para; p++) {
          co = ( (Condicion) condiciones.get(p));
          comparar = co.getValor();
          indice = p;
          if (actual.esIgual(comparar)) {
            para = true;
          }
        }

        //System.out.println("Aumenta Feromona [ "+posicion+" ] [ "+indice+" ] :"+feromona[posicion][indice]);
        feromona[posicion][indice] += (calidad / 2);
        if (feromona[posicion][indice] > maxFeromona) {
          feromona[posicion][indice] = maxFeromona;
        }
      } //del if
      else {
        actual = cActual.getValor();
        posicion = i;
        feromona[posicion][numeroValoresReglas[posicion]] += (calidad / 2);
        if (feromona[posicion][numeroValoresReglas[posicion]] > maxFeromona) {
          feromona[posicion][numeroValoresReglas[posicion]] = maxFeromona;
        }
      }
    } //Iteramos todas las condiciones de la regla

  }

  /**
   * Prunes the rule given as paramenter.
   * @param hormiga {@link Regla} Rule to be pruned.
   */
  private void podaRegla(Regla hormiga) {

    float confidenciaSubRegla;
    float confidenciaOriginal;
    int mejor;
    int vueltas = 0;
    boolean parada = false;
    Regla original;
    Vector subReglas;
    Regla subregla;
    Condicion vacia;

    original = new Regla(hormiga);
    while (!parada && vueltas < numAtributosReglas) {
      subReglas = new Vector();
      //Copiamos las reglas
      for (int i = 0; i < numAtributosReglas; i++) { //Conforme hay menos atributos se necesitan menos reglas
        subregla = new Regla(original);
        if (tieneCondicionVacia[i] == 1) {
          vacia = (Condicion) listaCondicionesVacias.get(i);
        }
        else {
          vacia = obtenerCondicionVacia(i);
        }
        subregla.eliminaCondicion(i, vacia);
        subReglas.addElement(subregla);
      }

      confidenciaOriginal = calculaConfidencia(hormiga);

      mejor = -1;
      for (int i = 0; i < numAtributosReglas; i++) {
        subregla = (Regla) subReglas.get(i);
        confidenciaSubRegla = calculaConfidencia(subregla);
        if (confidenciaSubRegla >= confidenciaOriginal) {
          mejor = i;
        }
      }

      if (mejor == -1) {
        parada = true;
      }
      else {
        subregla = (Regla) subReglas.get(mejor);
        original = new Regla(subregla);
        asignarCalidad(original); //Se le calcula su calidad
      }
      vueltas++;
    }

    asignarCalidad(original);
    hormiga = new Regla(original); //Copiamos de vuelta el resultado
    hormiga.insertaNumCondicionesReales(hormiga.obtenerNumCondiciones() -
                                        vueltas);
  }

  /**
   * Computes the confidence of the given ant/rule with the training set.
   * @param hormiga {@link Regla} Rule to compute its confidence.
   * @return Confidence of the given ant/rule
   */
  private float calculaConfidencia(Regla hormiga) {
    float calidad = 0;
    boolean cubierta = false;
    Muestra mt;
    float tamanio = 0;
    float cubiertas = 0;

    for (int i = 0; i < cTrain.tamanio(); i++) {
      mt = cTrain.obtenerMuestra(i);
      if (hormiga.estanCondicionesEn(mt, listaCondicionesVacias)) {
        tamanio++;
        if ( (hormiga.obtenerReglaPredicha()).esIgual(mt.getClase())) {
          cubiertas++;
        }
      }
    }

    if (cubiertas == 0 || tamanio == 0) {
      return 0;
    }

    calidad = cubiertas / tamanio;

    return calidad;

  }

  /**
   * 
   * Evaporates the pheromone of the matrix using the factor given 
   * as a parameter of the algoritm.
   */
  private void evaporaFeromona() {
    for (int i = 0; i < numAtributosReglas; i++) {
      for (int j = 0; j <= numeroValoresReglas[i]; j++) { //El igual se debe al valor vacio
        feromona[i][j] = feromona[i][j] * factorEvap;
        if (feromona[i][j] < minFeromona) {
          feromona[i][j] = minFeromona;
        }
      }
    }
  }

  /**
   * Computes the probabilities of conditions to be used in a rule 
   * during the next ants iteration.
   */
  private void inicializaProbabilidades() {
    float sumatoria;
    int limite;

    for (int i = 0; i < numAtributosReglas; i++) {
      sumatoria = 0;
      if (tieneCondicionVacia[i] == 1) {
        limite = numeroValoresReglas[i] + 1;
      }
      else {
        limite = numeroValoresReglas[i];
      }
      //Calcula sumatoria de feromona de los siguientes por su heuristica
      for (int h = 0; h < limite; h++) {
        sumatoria += (Math.pow(feromona[i][h], alfa)) *
            (Math.pow(funcionH[i][h], beta));
      }
      for (int j = 0; j < limite; j++) {
        probabilidad[i][j] = (float) ( (Math.pow(feromona[i][j], alfa)) *
                                      (Math.pow(funcionH[i][j], beta))); //divide
        // System.out.println("Numerador: "+probabilidad[i][j]+" Denominador: "+sumatoria);
        probabilidad[i][j] = probabilidad[i][j] / sumatoria;
      }
    }

  }

  /**
   * Creates the path of the given ant and computes its quality.
   * @param hormiga {@link Regla} Ant to create its path. 
   */
  private void recorreCamino(Regla hormiga) {

    creaCamino(hormiga);
    asignarCalidad(hormiga);

  }

  /**
   * Creates the path of the given ant.
   * @param hormiga {@link Regla} Ant to create its path. 
   */
  private void creaCamino(Regla hormiga) {

    Condicion actual;
    Condicion anterior = null;
    boolean parada = false;
    int i;
    for (i = 0; i < numAtributosReglas && !parada; i++) {
      //Elegir condicion a asignar a la regla
      actual = escogeSiguienteCondicion(i, anterior);
      anterior = actual; //La guardamos en memoria para los intervalos
      hormiga.insertarCondicion(actual);
      if (!cTrain.cubreMinimo(hormiga, listaCondicionesVacias, minimoCasosRegla)) {
        parada = true;
      }
    }

    if (hormiga.listaCondiciones().size() > 1 && parada) {
      hormiga.eliminaUltimaCondicion();
      i--;
    }

    hormiga.insertaNumCondicionesReales(i);

    Condicion vacia;
    for (int j = i; j < numAtributosReglas; j++) {
      if (tieneCondicionVacia[j] == 1) {
        vacia = (Condicion) listaCondicionesVacias.get(j);
      }
      else {
        vacia = obtenerCondicionVacia(j);
      }
      hormiga.insertarCondicion(vacia);
    }
  }

  Condicion obtenerCondicionVacia(int posicion) {
    Condicion co;
    Vector condiciones = (Vector) listaCondiciones.get(posicion);
    co = (Condicion) condiciones.get(0);

    if (co.getOperador() == 2) {
      return co;
    }
    else {
      co = (Condicion) condiciones.get(condiciones.size() - 1);
      return co;
    }

  }

  /**
   * Computes the quality of the rule given.
   * @param hormiga {@link Regla} given rule.
   */
  private void asignarCalidad(Regla hormiga) {
    float calidad = 0;
    boolean cubierta = false;
    Muestra mt;
    float tamanio = 0;
    float cubiertas = 0;
    Atributo clase;
    Atributo claseHormiga;
    Vector condiciones = hormiga.listaCondiciones();

    claseHormiga = hormiga.obtenerReglaPredicha();
    if (!listaCondicionesVacias.containsAll(condiciones)) {
      for (int i = 0; i < cTrain.tamanio(); i++) {
        mt = cTrain.obtenerMuestra(i);
        clase = mt.getClase();
        //Penalizo las reglas que no tienen condiciones
        if (hormiga.estanCondicionesEn(mt, listaCondicionesVacias)) {
          tamanio++;
          if (claseHormiga.getValor().equals(clase.getValor())) {
            cubiertas++;
            cubierta = true;
          }
        }
      }
    }

    if (cubiertas == 0) {
      calidad = 0;
    }
    else {
      calidad = (cubiertas / tamanio) + (cubiertas / cTrain.tamanio());
    }

    hormiga.asignarCalidad(calidad);
    hormiga.asignarMuestrasCubiertas(cubiertas);

  }

  /**
   * Computes which is the next condition to be added on a rule.
   * @param posicion Last position where the rule is built.
   * @param anterior Last condition added to a rule.
   * @return {@link Condicion} Selected condition to be added in the rule.
   */
  private Condicion escogeSiguienteCondicion(int posicion, Condicion anterior) {
    double probabilidadEscoger;
    double probabilidadAcumulada = 0;
    Condicion devolver = null;
    Condicion co = null;
    Vector condiciones;
    int indice = -1;
    Atributo valor;
    Atributo limite = null;
    Vector valores;
    float probabilidadesEliminar = 0;

    probabilidadEscoger = generadorA.Rand();

    if (anterior == null) {
      valor = new Atributo("TMP", 0, true); //El atributo no es ordinal (no hay anterior)
    }
    else {
      valor = anterior.getValor();
    }
    //Meter en tipo el atributo de la condicion que corresponde ahora!!!

    valores = (Vector) listaCondiciones.get(posicion); //Cogemos los distintos valores
    co = (Condicion) valores.get(0);
    limite = co.getValor(); //Cogemos el atributo para comparar

    //Hay que mirar tb que los dos correspondan al mismo tipo
    if (!valor.getTipo() && valor.getAtributo() == limite.getAtributo()) { //Si el atributo anterior es ordinal
      indice = 0;
      valores = (Vector) listaCondiciones.get(posicion); //Cogemos los distintos valores
      co = (Condicion) valores.get(indice);
      limite = co.getValor(); //Cogemos el atributo para comparar
      while (!valor.equals(limite) &&
             indice < numeroValoresReglas[posicion] - 1) { //Iteramos hasta llegar al limite inferior
        probabilidadesEliminar += probabilidad[posicion][indice];
        indice++;
        co = (Condicion) valores.get(indice);
        limite = co.getValor(); //Cogemos el atributo para comparar
      }

    }
    probabilidadAcumulada = probabilidadesEliminar;

    //Ahora empezamos a mirar las probabilidades desde el limite
    while (probabilidadAcumulada < probabilidadEscoger &&
           indice < numeroValoresReglas[posicion]) {
      indice++;
      probabilidadAcumulada += probabilidad[posicion][indice]; //Recordar que el vacio es el ultimo
    }

    condiciones = (Vector) listaCondiciones.get(posicion);
    co = (Condicion) condiciones.get(0);
    if (indice >= condiciones.size()) {
      if (tieneCondicionVacia[posicion] == 1) {
        devolver = (Condicion) listaCondicionesVacias.get(posicion);
      }
      else {
        devolver = obtenerCondicionVacia(posicion);
      }
    }
    else {
      devolver = (Condicion) condiciones.get(indice);
    }
    return devolver;
  }

  /**
   * 
   * Creates a ant with a default defined class.
   * @param hormiga {@link Regla} Ant to be created.
   * @param clase Class to be assigned.
   */
  private void creaHormigas(Regla hormiga, Atributo clase) {

    hormiga = new Regla();
    hormiga.insertarClase(clase);

  }

  /**
   * Initializes the pheromone for all the conditions.
   */
  private void inicializaFeromona() {

    int limite;

    for (int i = 0; i < numAtributosReglas; i++) {
      if (tieneCondicionVacia[i] == 1) {
        limite = numeroValoresReglas[i] + 1;
      }
      else {
        limite = numeroValoresReglas[i];
        feromona[i][numeroValoresReglas[i]] = 0;
      }

      for (int j = 0; j < limite; j++) {
        feromona[i][j] = maxFeromona;
      }
    }

    for (int i = 0; i < listaClases.size(); i++) {
      feromonaClases[i] = maxFeromona;
    }
  }

  /**
   * Selects the default class of the ants for the next iteration.
   * @return Selected Class.
   */
  private Atributo elegirClasePredefinida() {
    Atributo devolver = null;
    double probabilidadEscoger;
    double acumulado = 0;
    float denominador = 0;
    int indice = -1; //En la primera iteracion del while se vuelve 0

    for (int i = 0; i < listaClases.size(); i++) {
      denominador += (Math.pow(funcionHClases[i], beta)) *
          (Math.pow(feromonaClases[i], alfa));
    }

    probabilidadEscoger = generadorA.Rand(); //Double entre (0,1)
    while (acumulado <= probabilidadEscoger) {
      indice++;
      acumulado += calculaProbabilidadClase(indice) / denominador;
    }

    devolver = (Atributo) listaClases.get(indice);

    return devolver;
  }

  /**
   * Computes the probability of the given class.
   * @param indice given class.
   * @return the probability of the given class.
   */

  private float calculaProbabilidadClase(int indice) {

    double denominador = 0;
    double numerador = 0;
    float devolver;

    numerador = (Math.pow(funcionHClases[indice], beta)) *
        (Math.pow(feromonaClases[indice], alfa));

    devolver = (float) (numerador);

    return devolver;
  }

  /**
   * Computes the heuristic values of all the classes.
   */
  private void calculaHeuristicasClases() {
    Atributo clase;
    float porcentaje;
    int tamanio = cTrain.tamanio();

    for (int i = 0; i < listaClases.size(); i++) {
      clase = (Atributo) listaClases.get(i);
      funcionHClases[i] = cTrain.porcentajeMuestrasClase(clase);
    }

  }

  /**
   * Computes the heuristic values of the conditions following the given class.
   * @param clase given class for all the conditions.
   */
  private void calculaHeuristicasCondiciones(Atributo clase) {
    //Heuristicas de todas las condiciones
    Condicion co;
    Vector condiciones;
    float porcentaje = 0;
    float sumatoria;

    for (int i = 0; i < listaCondiciones.size(); i++) {
      condiciones = (Vector) listaCondiciones.get(i);
      sumatoria = 0;
      for (int j = 0; j < condiciones.size(); j++) {
        co = (Condicion) condiciones.get(j);
        porcentaje = cTrain.porcentajeMuestrasCondicion(co, clase);
        funcionH[i][j] = porcentaje;
        sumatoria += porcentaje;
      }
      if (tieneCondicionVacia[i] == 1) {
        funcionH[i][condiciones.size()] = cTrain.porcentajeMuestrasClase(clase);
      }
      else {
        funcionH[i][condiciones.size()] = 0;
      }
    }
  }

  /**
   * Creates a default rule en case that any of the generated ones by the
   * algorithm can be applied.
   * @return {@link Regla} a default rule: assign the same class to every example.
   */

  private Regla creaReglaGenerica() {
    Regla devolver = new Regla();
    int mayorClase = cTrain.obtenerMayorClase(listaClases); //Obtenemos la clase mas repetida entre las muestras
    Atributo clase = (Atributo) listaClases.get(mayorClase);

    //Regla sin condiciones pero con clase
    devolver.insertarClase(clase);
    asignarCalidad(devolver);
    devolver.insertaNumCondicionesReales(0);

    listaClases.remove(clase); //Eliminamos la clase para que no se pueda escoger.
    return devolver;
  }

  /**
   * Computes the accuracy of the extracted rules for a given dataset.
   * @param conjunto given dataset
   * @return the accuracy of the extracted rules for a given dataset.
   */
  private float obtenerAccuracy(ConjuntoDatos conjunto) {
    int tamanio;
    float porcentaje;
    float aciertos = 0;
    Muestra mt;
    Atributo clasePredicha = null;
    Atributo claseOriginal;
    Regla actual;

    tamanio = conjunto.tamanio();
    for (int i = 0; i < tamanio; i++) {
      mt = conjunto.obtenerMuestra(i);
      claseOriginal = mt.getClase();
      clasePredicha = null;
      for (int j = 0; j < reglasDescubiertas.size() && clasePredicha == null; j++) {
        actual = (Regla) reglasDescubiertas.get(j);
        clasePredicha = actual.prediccion(mt, listaCondicionesVacias);
      }

      if (clasePredicha == null) { //En el caso de que no lo cubran las reglas descubiertas
        clasePredicha = generica.obtenerReglaPredicha();
      }
      if (clasePredicha.esIgual(claseOriginal)) {
        aciertos++;
      }
    }

    porcentaje = aciertos / tamanio;
    return porcentaje;
  }

  /**
   * Prints all the results on the output results files.
   */
  public void sacaResultadosAFicheros() {
    File fichero;
    FileOutputStream flujo;
    PrintStream salida;
    int tamanioConjunto;
    int tamanioReglas;
    Muestra mt;
    Regla regla;
    Atributo clasePredicha = null;
    Atributo claseOriginal;
    boolean terminado = false;

    porcentajeTrain = 0;
    porcentajeTest = 0;

    //Primero sacamos los datos de TRAIN
    try {
      fichero = new File(fOutTrain); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;

      salida.print(cabeceraTrain);
      //Ahora imprimimos todos las clases
      tamanioReglas = reglasDescubiertas.size();
      tamanioConjunto = cTrainC.tamanio();
      //Rotamos todas las muestras
      for (int i = 0; i < tamanioConjunto; i++) {
        mt = cTrainC.obtenerMuestra(i);
        //Rotamos todas las reglas
        terminado = false;
        for (int j = 0; j < tamanioReglas && !terminado; j++) {
          regla = (Regla) reglasDescubiertas.get(j);
          clasePredicha = regla.prediccion(mt, listaCondicionesVacias);
          if (clasePredicha != null) {
            terminado = true;
          }
        }
        claseOriginal = mt.getClase();
        salida.print(claseOriginal.getValor() + " ");
        salida.println(clasePredicha.getValor());
        if (claseOriginal.equals(clasePredicha)) {
          porcentajeTrain++;
        }
      }
      porcentajeTrain = porcentajeTrain / tamanioConjunto;

    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutTrain + " no se pudo crear");
      System.exit(0);
    }

    //Datos de Test
    try {
      fichero = new File(fOutTest); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;

      salida.print(cabeceraTest);
      //Ahora imprimimos todos las clases
      tamanioReglas = reglasDescubiertas.size();
      tamanioConjunto = cTest.tamanio();
      //Rotamos todas las muestras
      for (int i = 0; i < tamanioConjunto; i++) {
        mt = cTest.obtenerMuestra(i);
        //Rotamos todas las reglas
        terminado = false;
        for (int j = 0; j < tamanioReglas && !terminado; j++) {
          regla = (Regla) reglasDescubiertas.get(j);
          clasePredicha = regla.prediccion(mt, listaCondicionesVacias);
          if (clasePredicha != null) {
            terminado = true;
          }
        }
        claseOriginal = mt.getClase();
        salida.print(claseOriginal.getValor() + " ");
        salida.println(clasePredicha.getValor());
        if (clasePredicha.equals(claseOriginal)) {
          porcentajeTest++;
        }

      }
      porcentajeTest = porcentajeTest / tamanioConjunto;

    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutTest + " no se pudo crear");
      System.exit(0);
    }

  }

  /**
   * Prints on the standard output the algorithm results.
   * Prints each extrated rule followed by its acurracies for training and
   * test.
   */
  public void muestraResultados() {

    Regla regla;
    float porcentajeTrn = 0;
    float porcentajeTst = 0;
    int i;
    int tamanioTrain = cTrainC.tamanio();
    int tamanioTst = cTest.tamanio();
    Atributo clase;
    double mediaCondiciones = 0;
    int numReglas;

    File fichero;
    FileOutputStream flujo;
    PrintStream salida = null;

    try {
      fichero = new File(fOutResult); //Abrimos el fichero de salida para Train
      flujo = new FileOutputStream(fichero); //Enganchamos el flujo con el fichero
      salida = new PrintStream(flujo); //Asignamos el flujo a salida;
    }
    catch (FileNotFoundException e) {
      System.err.println("El fichero " + fOutResult + " no se pudo crear");
      System.exit(0);
    }

    System.out.println("-------------------------------------------------");
    System.out.println("Reglas descubiertas ");

    numReglas = reglasDescubiertas.size();
    for (i = 0; i < reglasDescubiertas.size() - 1; i++) { //La regla generica se enseña despues
      regla = (Regla) reglasDescubiertas.get(i);
      regla.ordenaCondiciones();
      regla.imprime(listaCondicionesVacias);
      salida.print("REGLA " + i + " : ");
      regla.imprimeFichero(salida, listaCondicionesVacias);
      salida.println();
      mediaCondiciones += regla.listaCondiciones().size();
    }
    //Regla generica
    System.out.println("Regla generica:");
    salida.print("REGLA DEFAULT: ");
    regla = (Regla) reglasDescubiertas.get(i);
    regla.ordenaCondiciones();
    clase = regla.obtenerReglaPredicha();
    System.out.println("< All > ==> " + clase.getValor());
    salida.println("< ALL > ==> " + clase.getValor());
    mediaCondiciones++;

    System.out.println("Porcentaje sobre Train: " + porcentajeTrain);
    System.out.println("Porcentaje sobre Test: " + porcentajeTest);
    System.out.println("Numero de reglas: " + numReglas);
    System.out.println("Numero medio de condiciones: " +
                       mediaCondiciones / numReglas);
    salida.println("Porcentaje sobre Train: " + porcentajeTrain);
    salida.println("Porcentaje sobre Test: " + porcentajeTest);
    salida.println("Numero de reglas: " + numReglas);
    salida.println("Numero medio de condiciones: " +
                   mediaCondiciones / numReglas);

  }

  /**
   * Removes from the training set the covered cases by the given rule.
   * @param regla {@link Regla} given rule that cover the different cases to be removed.
   */
  private void quitarCasosCubiertos(Regla regla) {
    cTrain.eliminaMuestrasCubiertas(regla, listaCondicionesVacias);
  }

  /**
   * Removes from the training set the covered cases which have the same class as the rule given.
   * @param regla {@link Regla} given rule.
   */
  private void quitarCasosCubiertosEspecial(Regla regla) {
    cTrain.eliminaMuestrasClase(regla);
  }

  /**
   * Returns the number of examples of the training set covered by the given rule.
   * @param regla {@link Regla} given rule to check with.
   * @return int number of examples covered by the given rule.
   */
  private int muestrasCubiertasPor(Regla regla) {
    int numeroCubiertas = 0;
    int numeroMuestras = cTrain.tamanio();
    Muestra mt;
    Atributo clase = regla.obtenerReglaPredicha();

    for (int i = 0; i < numeroMuestras; i++) {
      mt = cTrain.obtenerMuestra(i);
      if (regla.estanCondicionesEn(mt) && mt.estaClase(clase)) {
        numeroCubiertas++;
      }
    }
    return numeroCubiertas;
  }

  /**
   * Prints the two lists created with the possible values and classes.
   */
  private void imprimeListas() {
    System.out.println("Lista con distintos valores para cada atributo");
    Vector auxiliar;
    Atributo at;

    for (int i = 0; i < numAtributos; i++) {
      System.out.println("Atributo: " + i);
      auxiliar = (Vector) listaValores.get(i);
      for (int j = 0; j < auxiliar.size(); j++) {
        at = (Atributo) auxiliar.get(j);
        System.out.print("\t" + at.getValor());
      }
      System.out.println();
    }
    System.out.println();

    System.out.println("Lista con distintos valores para la clase");
    for (int i = 0; i < listaClases.size(); i++) {
      at = (Atributo) listaClases.get(i);
      System.out.println("\t" + at.getValor());
    }
    System.out.println();

  }

}

