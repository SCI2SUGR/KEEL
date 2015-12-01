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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner;

import keel.Dataset.*;
import org.core.*;

import java.util.*;
import java.math.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * <p>Title: Ant Colony Optimization</p>
 * <p>Description: Classification Algorithm by ACO (Ant Miner).
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class ACO {

    private float[][] feromona; //Aqui se almacenara la feromona para cada valor de cada atributo
    private int[] numeroValores; //Vector que guarda el numero de valores de cada atributo
    private float[][] funcionN; //Matriz con los valores N de esta manera no se vuelven a calcular
    private float[][] funcionH; //Matriz con todos los valores H +preprocesamiento
    private Vector listaValores; //Aqui se almacenaran los distintos valores que puede alcanzar cada valor (ordenados de menor a mayor)
    private Vector listaValoresRestantes; //Aqui se iran almacenando los valores asignables a la hormiga
    private Vector listaClases; //Vector de nombres de cada clase
    private Vector reglasDescubiertas; //Reglas descubiertas por las hormigas, inicialmente vacio
    private Vector reglasHormigas; //Reglas descubiertas por las hormigas en una iteracion del while
    private ConjuntoDatos cTrain; // Muestras del fichero de entrenamiento preprocesado
    private ConjuntoDatos cTrainC; //Muestras del fichero de entrenamiento sin procesamiento previo
    private ConjuntoDatos cTest; //Muestras del fichero de prueba
    private ConjuntoDatos muestrasCubiertas; //Muestras que han sido cubiertas por las reglas creadas

    private int numHormigas;
    private int maximoDatosSinCubrir;
    private int minimoCasosRegla;
    private int maxIteracionesSinConverger;
    private int numAtributos;
    private long semilla;

    private float porcentajeTrain;
    private float porcentajeTest;

    private String fInTrainP;
    private String fInTrainC;
    private String fInTest;
    private String fOutTrain;
    private String fOutTest;
    private String fOutResult;

    private String cabeceraTrain;
    private String cabeceraTest;

    boolean continuosValues; //Indica si hay que comprobar si los datos son compatibles con el algoritmo


  /**
   * Default constructor. Nothing is done.
   */
    public ACO() {
    }

    /**
     * All is OK is the data-sets have not got any continuos values
     * @return boolean a flag for the existence of continous values in the data-set
     */
    public boolean OK(){
        return (!continuosValues);
    }


  /**
   * Parameter constructor. Build the algorithm object with the parameter given.
   *
   * @param fTrainPrep String Preprocessed Training filename.
   * @param fTrain String Full training filename.
   * @param fTestOriginal String Test filename.
   * @param fSalidaTrain String Training output filename.
   * @param fSalidaTest String Test output filename.
   * @param fSalidaResult String Global results filename.
   * @param nHormigas int Number of ants considered.
   * @param maxDatos int Maximum number of uncovered data.
   * @param minCasos int Minimum number of cases that a rule must cover.
   * @param maxIter int Maximum iterations.
   * @param semillaOriginal long Seed for the random numbers generator.
   */

    public ACO(String fTrainPrep, String fTrain, String fTestOriginal,
               String fSalidaTrain,
               String fSalidaTest, String fSalidaResult, int nHormigas,
               int maxDatos, int minCasos, int maxIter,
               long semillaOriginal) {

        numHormigas = nHormigas;
        maximoDatosSinCubrir = maxDatos;
        minimoCasosRegla = minCasos;
        maxIteracionesSinConverger = maxIter;
        semilla = semillaOriginal;

        fInTrainP = new String(fTrainPrep);
        fInTrainC = new String(fTrain);
        fInTest = new String(fTestOriginal);
        fOutTrain = new String(fSalidaTrain);
        fOutTest = new String(fSalidaTest);
        fOutResult = new String(fSalidaResult);
        cabeceraTest = new String("");
        cabeceraTrain = new String("");

        listaValores = new Vector();
        listaValoresRestantes = new Vector();
        listaClases = new Vector();
        reglasDescubiertas = new Vector();
        reglasHormigas = new Vector();

        extraeDatos(); //Una vez asignadas las caracteristicas extraemos los datos de los ficheros
    }


  /**
   * 
   * Extracts the dataset from the training, full-training and test files into
   * the algorithm structures {@link ConjuntoDatos}.
   */
    private void extraeDatos() {

        continuosValues = false;

        myDataset dTrain = new myDataset();
        myDataset dTrainC = new myDataset();
        myDataset dTest = new myDataset();

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
        } catch (IOException e) {
            System.err.println("Problema leyendo los conjuntos de datos:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        creaDatos();
        cTrain = extraeMuestrasSinP(dTrain);
        cTrainC = extraeMuestrasSinP(dTrainC);
        cTest = extraeMuestrasSinP(dTest);

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
                if (X[i][j] == -1) {
                    System.err.println(
                            "El algoritmo no admite valores perdidos");
                    System.exit(0); //El algoritmo no admite valores perdidos
                }
                at = (Atributo) ((Vector) listaValores.get(j)).get((int) X[i][j]); //Cogemos el puntero que señala al atributo almacenado en lista de valores
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
                if (X[i][j] == -1) {
                    System.err.println(
                            "El algoritmo no admite valores perdidos");
                    System.exit(0); //El algoritmo no admite valores perdidos
                }
                posicion = (int) X[i][j];
                atD = atv[j];
                if (atD.getType() == 1) {
                    posicion = posicion - 1; //En los enteros se empieza a contar desde 1 y el vector desde 0 [0,9] = [1,10]
                }
                //System.out.println("------->"+X[i][j]+"  "+posicion+"  "+i+"  "+j);
                at = (Atributo) ((Vector) listaValores.get(j)).get(posicion); //Cogemos el puntero que señala al atributo almacenado en lista de valores
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
        Vector valores;
        Vector nombres;
        int maximo;
        int minimo;

        String nombre;

        //Crear lista de valores
        listaAtributos = Attributes.getInputAttributes();
        numAtributos = listaAtributos.length;
        numeroValores = new int[numAtributos]; //inicializamos el vector con el numero de valores de cada atibuto

        for (int i = 0; i < listaAtributos.length; i++) {
            valores = new Vector();
            actual = listaAtributos[i];

            if (actual.getType() == 1) { //El tipo del atributo es entero otra forma de coger sus valores
                maximo = (int) actual.getMaxAttribute();
                minimo = (int) actual.getMinAttribute();
                numeroValores[i] = (maximo - minimo) + 1; //Numero de valores de este atributo

                for (int j = minimo; j <= maximo; j++) {
                    insertar = new Atributo(String.valueOf(j), i);
                    valores.addElement(insertar);

                }
            } else { //El tipo del atributo es nominal
                nombres = actual.getNominalValuesList();

                for (int j = 0; j < nombres.size(); j++) {
                    insertar = new Atributo((String) nombres.get(j), i);
                    valores.addElement(insertar);
                }
                numeroValores[i] = nombres.size(); //Numero de valores de este atributo

            }
            listaValores.addElement(valores);
        }

        //Crear lista de nombres de clases
        actual = Attributes.getOutputAttribute(0); //Solo tenemos un atributo de salida en esta clasificacion
        nombres = actual.getNominalValuesList();
        for (int i = 0; i < nombres.size(); i++) {
            insertar = new Atributo((String) nombres.get(i), -1);
            listaClases.addElement(insertar);
        }

        //Creamos la lista de valores H
        funcionH = new float[numAtributos][];
        //Creamos la lista de valores N
        funcionN = new float[numAtributos][];
        for (int i = 0; i < numAtributos; i++) {
            funcionN[i] = new float[numeroValores[i]];
            funcionH[i] = new float[numeroValores[i]];
        }

    }

    /**
     * Initializes the values to be chosen for an ant to add it to its rule.
     */
    private void inicializaValoresRestantes() {
        listaValoresRestantes = new Vector(listaValores);
    }


    /**
     * Removes the given attribute and all its possible values for the next iteration, 
     * the ants can not select it.
     * @param eliminar Attribute to be removed.
     */
    private void eliminaAtributoColumna(Atributo eliminar) {
        int indice;

        indice = eliminar.getAtributo();
        listaValoresRestantes.set(indice, null);
    }


  /**
   * Executes the algorithm.
   */
    public void run() {
        //Conjunto de entrenamiento esta ya inicializado
        //Reglas descubiertas = {} ya esta hecho
        //Partimos todos los datos escritos hasta ahora
        System.out.println(
                "-----------------------------------------------------");

        int hormigaActual;
        int convergencia;

        Randomize generadorA; //Para los numeros aleatoreos
        generadorA = new Randomize();
        generadorA.setSeed(semilla);

        Regla hormiga;
        Regla mejor;
        Regla anterior = null;

        ComparadorRegla c;
        creaMatrizN();
        while (cTrain.tamanio() > maximoDatosSinCubrir) {
            hormigaActual = 0;
            convergencia = 1;
            reglasHormigas = new Vector();
            creaMatrizFeromona();
            do {
                inicializaValoresRestantes(); //Inicializa un vector de atributos con todos los atributos escogibles por la hormiga
                hormiga = new Regla();
                //Creamos una regla con la hormiga
                creaRegla(hormiga, generadorA);
                //Poda la regla
                podaRegla(hormiga);
                //Actualizamos feromona
                actualizarFeromona(hormiga);
                //Comprobamos la convergencia
                if (hormiga.esIgual(anterior)) {
                    convergencia++;
                } else {
                    reglasHormigas.addElement(hormiga);
                    convergencia = 1;
                    anterior = hormiga;
                }
                //Aumentamos el numero de hormigas que han generado una regla
                hormigaActual++;

            } while (hormigaActual < numHormigas &&
                     convergencia < maxIteracionesSinConverger);
            //Ordenamos las reglas de las hormigas de mayor calidad a menor
            c = Regla.obtenerComparador();
            Collections.sort(reglasHormigas, c);
            //Añade la mejor regla al conjunto de reglas descubiertas
            mejor = (Regla) reglasHormigas.get(0);
            reglasDescubiertas.addElement(mejor);
            //Quitar del conjunto de entrenamiento los casos cubiertos
            quitarCasosCubiertos(mejor);

            System.out.println("MEJOR REGLA DE LA ITERACION");
            mejor.imprime();
            System.out.println("Quedan " + cTrain.tamanio() + " muestras");

        }

        //Crear regla por defecto
        Regla generica;
        generica = creaReglaGenerica();
        reglasDescubiertas.addElement(generica);

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

        return devolver;
    }

    /**
     * Creates the entropy matrix used by the probability function.
     */
    private void creaMatrizN() {
        double[] probabilidades;
        int numValores;
        Atributo actual;

        //Primero obtenemos la funcion H que es necesaria para hacer N
        for (int i = 0; i < numAtributos; i++) {
            numValores = numeroValores[i];
            for (int j = 0; j < numValores; j++) {
                funcionH[i][j] = obtenerFuncionH(i, j);
            }
        }

        //Con la funcion H averiguada obtenemos n
        for (int i = 0; i < numAtributos; i++) {
            numValores = numeroValores[i];
            for (int j = 0; j < numValores; j++) {
                funcionN[i][j] = calculaNij(i, j);
            }
        }

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
                    clasePredicha = regla.prediccion(mt);
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

        } catch (FileNotFoundException e) {
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
                    clasePredicha = regla.prediccion(mt);
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

        } catch (FileNotFoundException e) {
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
        } catch (FileNotFoundException e) {
            System.err.println("El fichero " + fOutResult + " no se pudo crear");
            System.exit(0);
        }

        System.out.println("-------------------------------------------------");
        System.out.println("Reglas descubiertas ");

        numReglas = reglasDescubiertas.size();
        for (i = 0; i < reglasDescubiertas.size() - 1; i++) { //La regla generica se enseña despues
            regla = (Regla) reglasDescubiertas.get(i);
            regla.ordenaAtributos();
            regla.imprime();
            salida.print("REGLA " + i + " : ");
            regla.imprimeFichero(salida);
            salida.println();
            mediaCondiciones += regla.listaCondiciones().size();
        }
        //Regla generica
        System.out.println("Regla generica:");
        salida.print("REGLA DEFAULT: ");
        regla = (Regla) reglasDescubiertas.get(i);
        regla.ordenaAtributos();
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
        cTrain.eliminaMuestrasCubiertas(regla);
    }

  /**
   * Prunes the rule given as paramenter.
   * @param hormiga {@link Regla} Rule to be pruned.
   */
    private void podaRegla(Regla regla) {
        int numCondiciones;
        Regla reglaModificada;
        Atributo eliminar = null;
        Atributo mejoraCalidad = null;
        Atributo claseMejoraC = null;
        float calidadMejorada;
        float calidadActual;
        float calidadOriginal;
        Vector atributos;
        Atributo clase;
        boolean mejora = true;
        int numAtributos;
        int i;
        float muestrasCubiertas;

        numCondiciones = regla.obtenerNumCondiciones();
        while (numCondiciones > 1 && !mejora) {
            //Cogemos la lista de atributos de la regla
            atributos = regla.listaCondiciones();
            numAtributos = atributos.size();
            //Cogemos la calidad que tiene la regla actual
            calidadOriginal = regla.obtenerCalidad();
            //Ponemos a 0 la calidad mejorada
            calidadMejorada = 0; //De esta manera al terminar el for si no se ha entrado en el if no se elimina atributo
            //Iteramos por todas las condiciones que tiene la regla
            for (i = 0; i < numAtributos; i++) {
                //Cogemos el candidato a eliminar
                eliminar = (Atributo) atributos.get(i);
                //lo eliminamos del vector
                atributos.remove(i);
                //Creamos la nueva regla con los atributos restantes
                reglaModificada = new Regla();
                reglaModificada.insertaAtributos(atributos);
                clase = obtenerClaseMasAdecuada(reglaModificada);
                reglaModificada.insertarClase(clase);
                //Obtenemos la calidad de la nueva regla
                calidadActual = obtenerCalidadRegla(reglaModificada);
                if (calidadActual > calidadOriginal) { //En caso de mejorar a la regla actual guardamos el atributo a eliminar
                    mejoraCalidad = eliminar; //Y la calidad para evitar calcularla de nuevo
                    calidadMejorada = calidadActual;
                    claseMejoraC = clase;
                } else {
                    atributos.insertElementAt(eliminar, i);
                }
            }
            //Una vez fuera del for comprobamos si se mejora la calidad
            if (calidadMejorada > calidadOriginal) {
                regla.eliminaCondicion(mejoraCalidad);
                regla.insertarClase(claseMejoraC);
                regla.asignarCalidad(calidadMejorada);
            } else { //En caso contrario salimos del bucle
                mejora = false;
            }
        }

        muestrasCubiertas = obtenerCalidadRegla2(regla);
        regla.asignarMuestrasCubiertas(muestrasCubiertas);
        //calidadMejorada=obtenerCalidadRegla(regla);
        //regla.asignarCalidad(calidadMejorada);
    }


  /**
   * Updates the pheromone of the conditions used in the given ant.
   * @param hormiga Best ant/rule {@link Regla} to update the pheromone.
   */
    private void actualizarFeromona(Regla regla) {
        //Crear una lista donde se guardan los atributos
        Vector condiciones = regla.listaCondiciones();
        int numCondiciones = condiciones.size();
        Atributo condicion = null;
        Atributo valor;
        Vector valores;
        boolean terminado = false;
        float calidadAsignar;
        int atributo = 0;
        int numValores;
        int contador;
        int j = 0;
        int maximoContador;
        float sumatoriaCamino = 0; //Feromona de todos los atributos que se incrementan

        //Primero aumentar feromona
        //Por cada condicion buscar y aumentar
        for (int i = 0; i < numCondiciones; i++) {
            condicion = (Atributo) condiciones.get(i);
            atributo = condicion.getAtributo();
            valores = (Vector) listaValores.get(atributo);
            numValores = valores.size();
            j = 0;
            terminado = false;
            while (j < numValores && !terminado) {
                valor = (Atributo) valores.get(j);
                if (valor.esIgual(condicion)) {
                    terminado = true;
                } else {
                    j++;
                }
            }
            //Una vez los tenemos obtenemos su calidad!!
            calidadAsignar = regla.obtenerCalidad();
            //float antes;
            //antes=feromona[atributo][j];
            feromona[atributo][j] += feromona[atributo][j] * calidadAsignar; //Actualizamos la feromona del atributo en cuestion
            //if(Float.isInfinite(feromona[atributo][j])){
            //  System.out.println("Error en la feromona"+ calidadAsignar+"  "+antes);
            //  System.exit(0);
            //}
            sumatoriaCamino = sumatoriaCamino + feromona[atributo][j];
        }
        //En el resto evaporar feromona
        for (int i = 0; i < numAtributos; i++) {
            numValores = numeroValores[i];
            valores = (Vector) listaValores.get(i);
            for (int k = 0; k < numValores; k++) {
                condicion = (Atributo) valores.get(k);
                if (!condiciones.contains(condicion)) {
                    feromona[i][k] = feromona[i][k] / sumatoriaCamino;
                }
            }
        }
    }

    /**
     * Creates a rule using the uncovered examples.
     * @param regla {@link Regla} New returned rule.
     * @param generadorA Randomize random number generator.
     */
    private void creaRegla(Regla regla, Randomize generadorA) {
        int numMuestrasCubiertas = cTrain.tamanio(); //Antes de añadir terminos a la regla, esta cubre todos los datos
        int atributosUsados = 0; //Numero de atributos que se han añadido a la regla
        float calidadRegla;
        Atributo siguiente; //Siguiente atributo a añadir a la regla
        Atributo clase; //Clase que se asigna a la regla de que se han añadido todos los atributos
        Regla reglaTemporal = new Regla(regla);

        while (atributosUsados <= numAtributos &&
               numMuestrasCubiertas >= maximoDatosSinCubrir) { //Comprobacion de si se puede seguir escogiendo atributos o no
            siguiente = escogeSiguienteAtributo(generadorA, reglaTemporal); //Nunca sera repetido
            reglaTemporal.insertarAtributo(siguiente);
            eliminaAtributoColumna(siguiente); //Elimina valores para el atributo insertado
            atributosUsados++; //Un atributo usado mas
//---------------------------
            //Asignar clase a la regla
            clase = obtenerClaseMasAdecuada(reglaTemporal);
            //Asignar calidad a la regla
            reglaTemporal.insertarClase(clase);
//---------------------------
            numMuestrasCubiertas = muestrasCubiertasPor(reglaTemporal);
            if (numMuestrasCubiertas >= maximoDatosSinCubrir ||
                atributosUsados == 1) {
                regla.insertarAtributo(siguiente);
                regla.insertarClase(clase);
            }
        }
        //Asignar clase a la regla
        clase = obtenerClaseMasAdecuada(regla);
        //Asignar calidad a la regla
        regla.insertarClase(clase);

        calidadRegla = obtenerCalidadRegla(regla);
        regla.asignarCalidad(calidadRegla);

    }

  /**
   * Computes the quality of the rule given as parameter.
   * @param regla given rule.
   * @return the quality of the rule.
   */
    private float obtenerCalidadRegla(Regla regla) {
        float calidad;
        float TP = 0, TN = 0, FP = 0, FN = 0;
        Muestra mt;
        Atributo clasePredicha;
        Atributo claseReal;

        clasePredicha = regla.obtenerReglaPredicha();

        for (int i = 0; i < cTrain.tamanio(); i++) {
            mt = cTrain.obtenerMuestra(i);
            claseReal = mt.getClase();
            if (regla.estanAtributosEn(mt)) { //TP Y FP
                if (claseReal.esIgual(clasePredicha)) {
                    TP++;
                } else {
                    FP++;
                }
            } else { //FN Y TN
                if (claseReal.esIgual(clasePredicha)) {
                    FN++;
                } else {
                    TN++;
                }
            }
        }

        calidad = (TP / (TP + FN)) * (TN / (FP + TN));
        if (TP == 0 || TP + FN == 0 || TN == 0 || TN + FP == 0) { //Evitamos el numero infinito
            calidad = 0;
        }

        return calidad;
    }

  /**
   * Computes the quality of the rule given as parameter (v.2).
   * @param regla given rule.
   * @return the quality of the rule.
   */
    private float obtenerCalidadRegla2(Regla regla) {
        float calidad;
        float TP = 0, FN = 0;
        Muestra mt;
        Atributo clasePredicha;
        Atributo claseReal;

        clasePredicha = regla.obtenerReglaPredicha();

        for (int i = 0; i < cTrain.tamanio(); i++) {
            mt = cTrain.obtenerMuestra(i);
            claseReal = mt.getClase();
            if (regla.estanAtributosEn(mt)) { //TP Y FP
                if (claseReal.esIgual(clasePredicha)) {
                    TP++;
                }
            } else { //FN Y TN
                if (claseReal.esIgual(clasePredicha)) {
                    FN++;
                }
            }
        }

        calidad = (TP / (TP + FN));
        if (TP == 0 || TP + FN == 0) { //Evitamos el numero infinito
            calidad = 0;
        }

        return calidad;
    }

    /**
     * Finds the most adequeated class for the conditions set stored in rule given.
     * @param regla {@link Regla} rule given as parameter.
     * @return the most adequeated class for the rule.
     */
    private Atributo obtenerClaseMasAdecuada(Regla regla) {
        Atributo clase;
        Muestra mt;
        Atributo claseMt;
        int mejor = 0;
        int actual = 0;
        boolean terminado = false;
        int numClases = listaClases.size();
        int[] contadores = new int[numClases];

        for (int j = 0; j < numClases; j++) {
            contadores[j] = 0;
        }

        for (int i = 0; i < cTrain.tamanio(); i++) {
            mt = cTrain.obtenerMuestra(i);
            if (regla.estanAtributosEn(mt)) {
                claseMt = mt.getClase();
                terminado = false;
                for (int j = 0; j < numClases && !terminado; j++) {
                    if (claseMt.esIgual((Atributo) listaClases.get(j))) {
                        terminado = true;
                        contadores[j]++;
                    }
                }
            }
        }

        for (int i = 0; i < numClases; i++) {
            if (contadores[i] > actual) {
                mejor = i;
                actual = contadores[i];
            }
        }

        clase = (Atributo) listaClases.get(mejor);

        return clase;
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
            if (regla.estanAtributosEn(mt) && mt.estaClase(clase)) {
                numeroCubiertas++;
            }
        }
        return numeroCubiertas;
    }


    /**
     * Funcion que devuelve el siguiente atributo fijandose en las reglas de probabilidad y
     * de feromona,se escoge para insertar en la regla que se esta creando
     * @param generadorA Randomize Generador de numeros aleatoreos
     * @param regla Regla Regla en la que se insertaria el siguiente atributo
     * @return Atributo Atributo escogido
     */

    /**
     *  Selects the next attribute to be added to the rule given following 
     *  the probability and pheromone matrix.
     * @param generadorA Random numbers generator.
     * @param regla {@link Regla} Rule that is being built and where the attribute will be added.
     * @return Selected attribute.
     */
    private Atributo escogeSiguienteAtributo(Randomize generadorA, Regla regla) {
        Atributo devolver;
        double probabilidadEscoger = 0;
        double probabilidadAcumulada = 0;
        boolean encontrado = false;
        boolean esta;
        int atributo = 0;
        int valor = 0;
        int numValores;
        int numAtributosRestantes = listaValoresRestantes.size();
        Vector aux;

        probabilidadEscoger = generadorA.Rand(); //Double entre (0,1)

        //Calculamos la probabilidad de los atributos que quedan por coger
        atributo = 0;
        while (atributo < numAtributos && !encontrado) {
            aux = (Vector) listaValoresRestantes.get(atributo);
            //if(aux!=null){//En caso de que este atributo no este usado
            numValores = numeroValores[atributo];
            valor = 0;
            while (valor < numValores && !encontrado) {
                probabilidadAcumulada = probabilidadAcumulada +
                                        probabilidad(atributo, valor, regla);
                if (probabilidadAcumulada >= probabilidadEscoger) {
                    encontrado = true;
                } else {
                    valor++;
                }
            } //while
            // }//if
            if (!encontrado) {
                atributo++;
            }
        } //while

        if (atributo != numAtributos) {
            aux = (Vector) listaValoresRestantes.get(atributo);
        } else {
            aux = new Vector();
        }

        if (atributo == numAtributos || aux == null) { //Es el ultimo entonces!!!!!!!!
            atributo--; //Cogemos el ultimo atributo
            if (atributo < 0) { //Modulo para los numeros negativos
                atributo = numAtributos - 1;
            }
            valor = numeroValores[atributo] - 1; //Cogemos el ultimo valor
            while ((Vector) listaValoresRestantes.get(atributo) == null) {
                atributo--; //Cogemos el ultimo atributo
                if (atributo < 0) { //Modulo para los numeros negativos
                    atributo = numAtributos - 1;
                }
                valor = numeroValores[atributo] - 1; //Cogemos el ultimo valor
            }
            //System.out.print("Error al escoger Atributo: ");
            //System.out.println(probabilidadEscoger + "  " + probabilidadAcumulada+"  "+atributo+"  "+valor);
        }

        aux = (Vector) listaValores.get(atributo);
        devolver = (Atributo) aux.get(valor);

        return devolver;
    }


    /**
     * Returns the probability of the given value for the given attribute
     * to be selected and later inserted in the rule given.
     *
     * @param atributo int attribute id to check.
     * @param valor int value id of the attribute to check.
     * @param regla Regla rule given where the attribute couble be later inserted.
     * @return double probability of the attribute-value to be selected.
     */
    private double probabilidad(int atributo, int valor, Regla regla) {
        double probabilidad = 0;
        double nij;
        double denominadorNij;
        double numeradorNij;
        double valoresSinUsarNij;
        double nijDenominador;
        double tijDenominador;
        double tij;
        double denominador = 0;
        double numerador = 0;
        int numClases = listaClases.size();
        int numValores;
        boolean esta;

//-----------------------Eficiencia-----------------
        denominadorNij = funcionN[atributo][valor];
        valoresSinUsarNij = valoresSinUsarNij(regla);
        denominadorNij = denominadorNij - valoresSinUsarNij;
        numeradorNij = Math.log(listaClases.size()) / Math.log(2);
        numeradorNij = numeradorNij - funcionH[atributo][valor];
//-------------------------------------------------------
        nij = numeradorNij / denominadorNij;
        tij = feromona[atributo][valor];

        numerador = nij * tij;

        //Calculo del denominador
        for (int i = 0; i < numAtributos; i++) {
            esta = regla.tieneValorAtributo(i);
            if (!esta) { //Si no esta el atributo i sumamos
                numValores = numeroValores[i];
                for (int j = 0; j < numValores; j++) {
                    //-----------------------Eficiencia-----------------
                    denominadorNij = funcionN[i][j];
                    denominadorNij = denominadorNij - valoresSinUsarNij;
                    numeradorNij = Math.log(listaClases.size()) / Math.log(2);
                    numeradorNij = numeradorNij - funcionH[i][j];
                    //-------------------------------------------------------

                    nijDenominador = numeradorNij / denominadorNij;
                    tijDenominador = feromona[i][j];
                    denominador += (nijDenominador * tijDenominador);
                } //for2

            } //if
        } //for1

        if (denominador == 0 || numerador == 0) { //Intentamos evitar el NAN
            probabilidad = 0;
        } else {
            probabilidad = numerador / denominador;
        }

        if (Double.isInfinite(probabilidad) || Double.isNaN(probabilidad)) {
            System.out.println("Probabilidad: " + numerador + "  " +
                               denominador + "  " + nij + "  " + tij);
        }

        return probabilidad;
    }

    float valoresSinUsarNij(Regla regla) {
        Vector condiciones = regla.listaCondiciones();
        Vector valores;
        Atributo atributo;
        int pos;
        int max;
        float devolver = 0;
        double logK = Math.log(listaClases.size()) / Math.log(2);
        for (int i = 0; i < condiciones.size(); i++) {
            atributo = (Atributo) condiciones.get(i);
            pos = atributo.getAtributo();
            max = numeroValores[pos];
            for (int j = 0; j < max; j++) {
                devolver += (float) (logK - funcionH[pos][j]);
            }
        }

        return devolver;

    }


    /**
     * Computes the heuristic value for the pair attribute-value given.
     * @param atributo int Attribute id.
     * @param valor int Value id of the attribute given.
     * @return double the heuristic value for the pair attribute-value 
     */
    private float calculaNij(int atributo, int valor) {
        float devolver = 0;
        float acumulador = 0;
        int nValores;
        boolean esta;

        int numClases = listaClases.size(); //k
        double probabilidadWAij = funcionH[atributo][valor]; //H(W|Ai=Vij);
        double logK = Math.log(numClases) / Math.log(2); //log2(k)
        float denominador = 0;

        for (int i = 0; i < numAtributos; i++) { //Construccion del denominador
            nValores = numeroValores[i]; //Tomamos los distintos valores que puede tomar el atributo
            //if(!regla.tieneValorAtributo(i))
            for (int j = 0; j < nValores; j++) {
                acumulador += (logK - funcionH[i][j]);
            }
        } //for

        denominador = acumulador;

        if (denominador != 0) {
            devolver = (float) denominador;
        } else {
            devolver = 0;
        }

        return devolver;

    };

    /**
     * Computes the entropy value for the pair attribute-value given.
     * @param atributo int Attribute id.
     * @param valor int Value id of the attribute given.
     * @return double the entropy value for the pair attribute-value 
     */
    private float obtenerFuncionH(int atributo, int valor) {
        float devolver = 0;
        float[] probabilidades;
        Vector valores;
        Atributo Aij;
        Atributo W;
        float sumatoria;
        float probabilidad;
        boolean aparece = false; //Aparece el atributo en alguna muestra

        //Se coge el atributo que interviene en la funcion
        valores = (Vector) listaValores.get(atributo);
        Aij = (Atributo) valores.get(valor);

        //Valor de k en la funcion
        int numClases = listaClases.size(); //Diferentes valores para las clases

        //Vector con todas las muestras que contienen el atributo Aij
        probabilidades = cTrain.listaProbabilidadesAtributoClase(Aij,
                listaClases);

        //Modificar el modulo para hacerlo todo de una tacada

        for (int i = 0; i < numClases; i++) {
            probabilidad = probabilidades[i]; //Probabilidad
            if (probabilidad != 0) { //Si probabilidad es 0 el logaritmo da infinito
                sumatoria = (float) (Math.log(probabilidad) / Math.log(2.0)); //Calculamos el logaritmo2 de la probabilidad
                sumatoria = sumatoria * probabilidad; //Multiplicamos el logaritmo por la probabilidad
                devolver = devolver + sumatoria; //Sumatoria de probabilidades
                aparece = true;
            }
        }

        //Cambio de signo ya que es -Sumatoria
        devolver = 0 - devolver;

        if (!aparece) { //Si no aparece en ningun lado se inicializa a log2(k)
            devolver = (float) (Math.log(listaClases.size()) / Math.log(2));
        }

        return devolver;
    }
     
    /**
     * Computes the probability of being the class "clase" if the attribute-value is the one given.
     * @param muestras Vector of samples to compute the probability.
     * @param clase {@link Atributo} class to compute its probability.
     * @param valor Atributo Pair attribute-value
     * @return double computed probability.
     */
    private double probabilidadWAij(Vector muestras, Atributo clase,
                                    Atributo valor) {
        double devolver = 0;
        int total = muestras.size();
        int muestrasWAij = 0;
        Muestra mt;

        for (int i = 0; i < muestras.size(); i++) {
            mt = (Muestra) muestras.get(i);
            if (mt.estaAtributo(valor) && mt.estaClase(clase)) { //Si tenemos el atributo y la clase contenidos
                muestrasWAij++;
            }
        }

        devolver = muestrasWAij / total;

        return devolver;
    }

    /**
     * Creates and initializes the pheromone matrix with the initial value 
     * proposed by the algorithm.
     */
    private void creaMatrizFeromona() {
        float valorInicial;
        float numeroTotalValores = 0;
        Vector array;
        Vector atributo;
        int tamanio;

        for (int i = 0; i < numAtributos; i++) {
            array = (Vector) listaValores.get(i);
            numeroTotalValores = numeroTotalValores + array.size();
        }
        valorInicial = 1 / numeroTotalValores;

        feromona = new float[numAtributos][];
        for (int i = 0; i < numAtributos; i++) {
            array = (Vector) listaValores.get(i); //Obtenemos un puntero al vector con los distintos valores de este atributo
            tamanio = array.size();
            feromona[i] = new float[tamanio];
            for (int j = 0; j < tamanio; j++) {
                feromona[i][j] = valorInicial;
            }
        }

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

