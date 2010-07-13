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

import java.util.*;
import org.core.*;
import java.io.*;
import keel.Dataset.*;


/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class PsoAco {
    private Vector particulas; //Vector con las particulas de la poblacion
    private ConjuntoDatos cTrain; // Muestras del fichero de entrenamiento preprocesado
    private ConjuntoDatos cTrainC; //Muestras del fichero de entrenamiento sin procesamiento previo
    private ConjuntoDatos cTest; //Muestras del fichero de prueba
    private long semilla; //Semilla para numeros aleatoreos
    private String fInTrain; //Nombre del fichero de entrada Train
    private String fInTrainC; //Nombre del fichero de entrada Train de prueba
    private String fInTest; //Nombre del fichero de entrada del test
    private String fOutTrain; //Nombre del fichero de salida de Train
    private String fOutTest; //Nombre del fichero de salida de Test
    private String fOutResult; //Nombre del fichero de salida de resultados
    private String cabeceraTrain; //Cabecera de Train
    private String cabeceraTest; //Cabecera de Test


    //----------------------------------------------------------------------------
    //ASPIRANTES
    //----------------------------------------------------------------------------
    private Vector listaCondicionesNominales;
    private Vector listaCondicionesVacias; //Este solo se corresponde con las condiciones Nominales
    private float[][] intervalos;
    private int numCondicionesContinuas;
    private float[][] heuristica;
    private Vector listaClases;
    private Vector reglasDescubiertas; //Reglas halladas en el proceso
    private int maxCasosSinCubrir;
    private int numParticulas;
    private Randomize generadorA; //Generador de numeros aleatoreos
    private int tamEntorno;
    private int maxIteraciones;
    private int minimoCasosCubiertos; //Construccion regla
    private float x; //Para calcular la velocidad
    private float c1;
    private float c2;
    private boolean poblacionInicializada;
    private Regla generica;
    private float porcentajeTrain, porcentajeTest;
    private int bandera;

    public PsoAco() {
    }

    /**
     * Constructor de la clase PSOACO
     * @param fTrainPrep Fichero de entrenamiento Preprocesado
     * @param fTrain Fichero de entrenamiento
     * @param fTestOriginal Fichero de Test
     * @param fSalidaTrain Fichero de salida de Train
     * @param fSalidaTest Fichero de salida de Test
     * @param fSalidaResult Fichero de salida de Resultados
     * @param semillaOriginal Semilla
     * @param maxCasosSinCubrirO Maximo de casos sin cubrir permitidos
     * @param numParticulasO Numero de particulas
     * @param tamEntornoO Tamaño del entorno
     * @param maxIteracionesO Maximo de iteraciones que se pueden dar
     * @param minimoCasosCubiertosO Minimo casos cubiertos por una regla
     * @param xO X
     * @param c1O Coeficiente c1
     * @param c2O Coeficiente c2
     * @param banderaO Flag para eleccion de tipo de condiciones
     */
    public PsoAco(String fTrainPrep, String fTrain, String fTestOriginal,
                  String fSalidaTrain,
                  String fSalidaTest, String fSalidaResult,
                  long semillaOriginal, int maxCasosSinCubrirO,
                  int numParticulasO, int tamEntornoO, int maxIteracionesO,
                  int minimoCasosCubiertosO,
                  float xO, float c1O, float c2O, int banderaO) {

        semilla = semillaOriginal;
        fInTrain = new String(fTrainPrep);
        fInTrainC = new String(fTrain);
        fInTest = new String(fTestOriginal);
        fOutTrain = new String(fSalidaTrain);
        fOutTest = new String(fSalidaTest);
        fOutResult = new String(fSalidaResult);
        cabeceraTest = new String("");
        cabeceraTrain = new String("");
        bandera = banderaO;

        generadorA = new Randomize();
        generadorA.setSeed(semilla);

        listaCondicionesNominales = new Vector();
        listaCondicionesVacias = new Vector(); //Este solo se corresponde con las condiciones Nominales

        listaClases = new Vector();
        reglasDescubiertas = new Vector(); //Reglas halladas en el proceso

        maxCasosSinCubrir = maxCasosSinCubrirO;
        numParticulas = numParticulasO;
        tamEntorno = tamEntornoO;
        maxIteraciones = maxIteracionesO;
        minimoCasosCubiertos = minimoCasosCubiertosO; //Construccion regla
        x = xO; //Para calcular la velocidad
        c1 = c1O;
        c2 = c2O;

        poblacionInicializada = false;

        extraeDatos(); //Una vez asignadas las caracteristicas extraemos los datos
        //de los ficheros

    }

    /**
     * Modulo que extrae los datos de los tres ficheros (train, train entero y test) y
     * los inserta en conjunto de datos con un formato conocido por el algoritmo
     */
    private void extraeDatos() {

        myDataset dTrain = new myDataset();
        myDataset dTrainC = new myDataset();
        myDataset dTest = new myDataset();

        try {
            dTrain.readClassificationSet(fInTrain, true);
            dTrainC.readClassificationSet(fInTrainC, false);
            dTest.readClassificationSet(fInTest, false);
        } catch (IOException e) {
            System.err.println(
                    "There was a problem while reading the input data-sets:");
            System.err.println("-> " + e);
            System.exit(0);
        }

        creaDatos();

        cTrain = extraeMuestras(dTrain);
        cTrainC = extraeMuestras(dTrainC);
        cTest = extraeMuestras(dTest);

        cabeceraTrain = dTrainC.copyHeader();
        cabeceraTest = dTest.copyHeader();

    }

    /**
     * Modulo que Crea las estructuras de datos internas con las diferentes condiciones
     */
    private void creaDatos() {

        Attribute[] listaAtributos;
        Attribute[] listaAtributosContinuos;
        Attribute actual;
        Atributo insertar;
        Condicion insertarCon;
        Condicion vacia;
        Atributo atributoVacio;
        Vector valores;
        Vector nombres;
        Vector condicionesIgual;
        int maximo;
        int minimo;
        float Rmax;
        float Rmin;
        int RealesInsertados = 0;

        String nombre;

        //Crear lista de distintos atributos posibles
        listaAtributos = Attributes.getInputAttributes();

        //Reservamos el tamaño para los intervalos, Dado que no sabemos el tamaño reservamos 2*nunAtributos
        intervalos = new float[Attributes.getNumAttributes() * 2][];
        for (int i = 0; i < Attributes.getNumAttributes() * 2; i++) {
            intervalos[i] = new float[3];
        }

        //Para cada atributo creamos sus distintos valores en Atributos y Condiciones
        for (int i = 0; i < listaAtributos.length; i++) {
            valores = new Vector();
            actual = listaAtributos[i];
            //El tipo del atributo es entero o nominal
            //otra forma de coger sus valores (no ha habido discretizacion)
            switch (bandera) {
            case 1: //todos nominales
                System.out.println("Atributos Nominales1");
                condicionesIgual = new Vector();
                valores = new Vector();

                nombres = actual.getNominalValuesList();
                condicionesIgual = new Vector();

                //Recorremos los atributos y los insertamos, lo mismo con las condiciones
                for (int j = 0; j < nombres.size(); j++) {
                    insertar = new Atributo(j, i, 1);
                    insertarCon = new Condicion(insertar, 0);
                    condicionesIgual.addElement(insertarCon);
                    valores.addElement(insertar);
                }

                listaCondicionesNominales.addElement(condicionesIgual);

                //Insercion de las condiciones Vacias
                atributoVacio = new Atributo( -1, i, 1);
                vacia = new Condicion(atributoVacio, 0);
                listaCondicionesVacias.addElement(vacia);
                numCondicionesContinuas = 0;

                break;
            case 2:

                //todos continuos
                System.out.println("Atributos continuos2");
                nombres = actual.getNominalValuesList();
                intervalos[RealesInsertados][0] = 0;
                intervalos[RealesInsertados][1] = nombres.size() - 1;
                intervalos[RealesInsertados][2] = i;
                RealesInsertados++;

                break;
            case 3: //Primera mitad continua Segunda mitad Nominal
                if (i < listaAtributos.length / 2) {
                    System.out.println("Atributos continuos3");
                    nombres = actual.getNominalValuesList();
                    intervalos[RealesInsertados][0] = 0;
                    intervalos[RealesInsertados][1] = nombres.size() - 1;
                    intervalos[RealesInsertados][2] = i;
                    RealesInsertados++;

                } else {
                    System.out.println("Atributos Nominales3");
                    condicionesIgual = new Vector();
                    valores = new Vector();

                    nombres = actual.getNominalValuesList();
                    condicionesIgual = new Vector();
                    //Recorremos los atributos y los insertamos, lo mismo con las condiciones
                    for (int j = 0; j < nombres.size(); j++) {
                        insertar = new Atributo(j, i, 1);
                        insertarCon = new Condicion(insertar, 0);
                        condicionesIgual.addElement(insertarCon);
                        valores.addElement(insertar);
                    }

                    listaCondicionesNominales.addElement(condicionesIgual);
                    //Insercion de las condiciones Vacias
                    atributoVacio = new Atributo( -1, i, 1);
                    vacia = new Condicion(atributoVacio, 0);
                    listaCondicionesVacias.addElement(vacia);
                }
                break;
            case 4: //Primera mitad nominal segunda continua
                if (i >= listaAtributos.length / 2) {
                    System.out.println("Atributos continuos4");
                    nombres = actual.getNominalValuesList();
                    intervalos[RealesInsertados][0] = 0;
                    intervalos[RealesInsertados][1] = nombres.size() - 1;
                    intervalos[RealesInsertados][2] = i;
                    RealesInsertados++;

                } else {
                    System.out.println("Atributos Nominales4");
                    condicionesIgual = new Vector();
                    valores = new Vector();

                    nombres = actual.getNominalValuesList();
                    condicionesIgual = new Vector();
                    //Recorremos los atributos y los insertamos, lo mismo con las condiciones
                    for (int j = 0; j < nombres.size(); j++) {
                        insertar = new Atributo(j, i, 1);
                        insertarCon = new Condicion(insertar, 0);
                        condicionesIgual.addElement(insertarCon);
                        valores.addElement(insertar);
                    }

                    listaCondicionesNominales.addElement(condicionesIgual);
                    //Insercion de las condiciones Vacias
                    atributoVacio = new Atributo( -1, i, 1);
                    vacia = new Condicion(atributoVacio, 0);
                    listaCondicionesVacias.addElement(vacia);
                }

                break;
            case 10: //Atributo Real
                System.out.println("Atributos Reales");
                Rmin = (float) actual.getMinAttribute();
                Rmax = (float) actual.getMaxAttribute();
                intervalos[RealesInsertados][0] = Rmin;
                intervalos[RealesInsertados][1] = Rmax;
                intervalos[RealesInsertados][2] = i;
                RealesInsertados++;

                break;

            default: //Atributo Nominal
                System.out.println("Atributos NominalesD");
                condicionesIgual = new Vector();
                valores = new Vector();

                nombres = actual.getNominalValuesList();
                condicionesIgual = new Vector();

                //Recorremos los atributos y los insertamos, lo mismo con las condiciones
                for (int j = 0; j < nombres.size(); j++) {
                    insertar = new Atributo(j, i, 1);
                    insertarCon = new Condicion(insertar, 0);
                    condicionesIgual.addElement(insertarCon);
                    valores.addElement(insertar);
                }

                listaCondicionesNominales.addElement(condicionesIgual);

                //Insercion de las condiciones Vacias
                atributoVacio = new Atributo( -1, i, 1);
                vacia = new Condicion(atributoVacio, 0);
                listaCondicionesVacias.addElement(vacia);

                break;

            }

        } //del for

        //Crear lista de nombres de clases
        actual = Attributes.getOutputAttribute(0); //Solo tenemos un atributo de salida en esta clasificacion
        nombres = actual.getNominalValuesList();
        for (int i = 0; i < nombres.size(); i++) {
            insertar = new Atributo(i, -1, 1); //Las clases son categoricas
            listaClases.addElement(insertar);
        }

        numCondicionesContinuas = RealesInsertados * 2;
        heuristica = new float[listaCondicionesNominales.size()][];
        Vector condiciones;
        for (int i = 0; i < listaCondicionesNominales.size(); i++) {
            condiciones = (Vector) listaCondicionesNominales.get(i);
            heuristica[i] = new float[condiciones.size() + 1];
        }

    }

    /**
     * Funcion que extrae las muestras de un fichero y las devuelve en un ConjuntoDatos
     * @param original myDataset de donde se extraeran los datos
     * @return Conjunto con los datos ya adaptados al formato interno
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
                    at = new Atributo( -1, 0, 0);
                } else {
                    at = new Atributo((float) X[i][j], 0, 0); //Cogemos el puntero que señala al atributo almacenado en lista de valores
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
     * Funcion que elige la clase para la que se construira la regla
     * @return Clase para la regla a construir
     */

    private Atributo elegirClaseAsignar() {
        double probabilidadAcumulada = 0;
        double probabilidadEscoger;
        Atributo clase = null;
        int i = 0;

        probabilidadEscoger = generadorA.Rand();

        while (probabilidadAcumulada < probabilidadEscoger) {
            clase = (Atributo) listaClases.get(i);
            probabilidadAcumulada += cTrain.porcentajeMuestrasClase(clase);
            //System.out.println("Acumulada: "+probabilidadAcumulada+" Escoger: "+probabilidadEscoger);
            i++;
        }

        return clase;

    }


    /**
     * Funcion que escoge por ruleta la condicion para la posicion
     * @param index Posicion para la cual se escoge la condicion
     * @return Condicion para la posicion
     */
    private Condicion escogeRuletaCondicion(int index) {
        Vector condiciones = (Vector) listaCondicionesNominales.get(index);
        double aleatorio = generadorA.Rand();
        double porcentaje;
        double acumulado = 0;
        Condicion devolver;

        porcentaje = 0.9 / condiciones.size(); //Entre las probabilidades de introducir condicion es 0.9
        int contador = -1; //En primera iteracion = 0
        while (acumulado < aleatorio) {
            acumulado += porcentaje;
            contador++;
        }

        //  System.out.println(index);
        if (contador >= condiciones.size()) {
            devolver = (Condicion) listaCondicionesVacias.get(index);
        } else {
            devolver = (Condicion) condiciones.get(contador);
        }

        return devolver;

    }

    /**
     * Modulo que inicializa la posicion de una particula
     * @param pos Posicion de la particula
     * @param clase Clase para la que se esta construyendo la regla
     */
    private void inicializaPosicion(Vector pos, Atributo clase) {

        int numCondiciones = listaCondicionesNominales.size();
        Condicion insertar;
        for (int i = 0; i < numCondiciones; i++) {
            insertar = escogeRuletaCondicion(i);
            pos.addElement(insertar);
        }
        //Podar Pos
        //*****************************************************************
         podaRegla(pos, clase);
         //*****************************************************************

    }

    /**
     * Funcion que calcula la calidad de un regla
     * @param regla Regla para la cual se calculara la calidad
     * @return Calidad de la regla
     */
    private float calculaCalidad(Regla regla) {
        float calidad = 0;
        float TP = 0, TN = 0, FP = 0, FN = 0;
        Muestra mt;
        Atributo clasePredicha;
        Atributo claseReal;
        float k = listaClases.size();

        clasePredicha = regla.obtenerReglaPredicha();

        for (int i = 0; i < cTrain.tamanio(); i++) {
            mt = cTrain.obtenerMuestra(i);
            claseReal = mt.getClase();
            if (regla.estanCondicionesEn(mt)) { //TP Y FP
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

        if (TP < 10) {
            calidad = 0;
        } else {
            calidad = 1 + TP / (1 + k + TP + FP);
        }

        if (TP == 0 || TP + FN == 0 || TN == 0 || TN + FP == 0) { //Evitamos el numero infinito
            calidad = 0;
        }

        return calidad;

    }


    /**
     * Modulo que poda una regla
     * @param regla Regla que se va a podar
     * @param clase Clase que se predice en la regla
     */
    private void podaRegla(Vector regla, Atributo clase) {

        float confidenciaSubRegla;
        float confidenciaOriginal;
        int mejor;
        int vueltas = 0;
        int tamanio = regla.size();
        boolean parada = false;
        Vector original;
        Vector subReglas;
        Vector subregla;
        Vector hormiga;
        Condicion vacia;
        int numAtributosReglas = regla.size();

        original = new Vector(regla);
        while (!parada && vueltas < numAtributosReglas) {
            subReglas = new Vector();
            //Copiamos las reglas
            for (int i = 0; i < numAtributosReglas; i++) { //Conforme hay menos atributos se necesitan menos reglas
                subregla = new Vector(original);
                vacia = (Condicion) listaCondicionesVacias.get(i);
                subregla.set(i, vacia);
                subReglas.addElement(subregla);
            }

            confidenciaOriginal = calculaConfidencia(regla, clase);

            mejor = -1;
            for (int i = 0; i < numAtributosReglas; i++) {
                subregla = (Vector) subReglas.get(i);
                confidenciaSubRegla = calculaConfidencia(subregla, clase);
                if (confidenciaSubRegla >= confidenciaOriginal) {
                    mejor = i;
                }
            }

            if (mejor == -1) {
                parada = true;
            } else {
                subregla = (Vector) subReglas.get(mejor);
                original = new Vector(subregla);
            }
            vueltas++;
        }

        regla = new Vector(original); //Copiamos de vuelta el resultado

    }

    /**
     * Modulo que poda un regla con condiciones de intervalos continuos
     * @param regla Regla que se va a podar
     * @param clase Clase para la cula se creo la regla
     */
    private void podaReglaContinua(Vector regla, Atributo clase) {

        float confidenciaSubRegla;
        float confidenciaOriginal;
        int mejor;
        int vueltas = 0;
        int tamanio = regla.size();
        boolean parada = false;
        Vector original;
        Vector subReglas;
        Vector subregla;
        Vector hormiga;
        Condicion vacia;
        int numAtributosReglas = regla.size();
        float intervalo;
        int atributo;

        original = new Vector(regla);
        while (!parada && vueltas < numAtributosReglas) {
            subReglas = new Vector();
            //Copiamos las reglas
            for (int i = 0; i < numAtributosReglas; i++) { //Conforme hay menos atributos se necesitan menos reglas
                subregla = new Vector(original);
                vacia = (Condicion) subregla.get(i);
                atributo = vacia.getIndice();
                if (vacia.getOperador() == 1) {
                    vacia.setValor(intervalos[atributo][1]);
                } else {
                    vacia.setValor(intervalos[atributo][0]);
                }
                subregla.set(i, vacia);
                subReglas.addElement(subregla);
            }

            confidenciaOriginal = calculaConfidencia(regla, clase);

            mejor = -1;
            for (int i = 0; i < numAtributosReglas; i++) {
                subregla = (Vector) subReglas.get(i);
                confidenciaSubRegla = calculaConfidencia(subregla, clase);
                if (confidenciaSubRegla >= confidenciaOriginal) {
                    mejor = i;
                }
            }

            if (mejor == -1) {
                parada = true;
            } else {
                subregla = (Vector) subReglas.get(mejor);
                original = new Vector(subregla);
            }
            vueltas++;
        }

        regla = new Vector(original); //Copiamos de vuelta el resultado

    }


    /**
     * Funcion que calcula la confidencia de unas condiciones para una clase predefinida
     * @param condiciones Condiciones
     * @param clase Clase
     * @return Confidencia calculada
     */
    private float calculaConfidencia(Vector condiciones, Atributo clase) {
        float calidad = 0;
        boolean cubierta = false;
        Muestra mt;
        float tamanio = 0;
        float cubiertas = 0;
        Regla hormiga = new Regla(new Vector(), condiciones, clase);

        for (int i = 0; i < cTrain.tamanio(); i++) {
            mt = cTrain.obtenerMuestra(i);
            if (hormiga.estanCondicionesEn(mt)) {
                tamanio++;
                if ((hormiga.obtenerReglaPredicha()).esIgual(mt.getClase())) {
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
     * Funcion que ejecuta la hibridacion del PSo con ACO
     * @param particulas Vector con las particulas
     * @return Vector con las particulas colocadas en el espacio
     */
    private Vector PSOACO(Vector particulas) {
        System.out.println("Executing PSO-ACO");
        Particula p;
        //Vector con la posicion inicial
        Vector posicion;
        //Elegimos la clase para la que se va a buscar la regla
        Atributo claseAsignar;
        claseAsignar = elegirClaseAsignar();
        calculaHeuristicasCondiciones(claseAsignar);
        //Valores para calcular la calidad
        float calidad;
        float calidadPasado;
        Regla regla;
        Vector podada;
        //Numero de iteraciones
        int numeroIteraciones = 0;
        int numeroMuestrasCubiertas;

        int numCondiciones = listaCondicionesNominales.size();

        //Inicializacion de la poblacion
        for (int i = 0; i < numParticulas; i++) {
            p = new Particula(numCondiciones);
            p.inicializaFeromona(listaCondicionesNominales);
            posicion = new Vector(); //Meter abajo
            inicializaPosicion(posicion, claseAsignar); //Meter abajo
            p.inicializaPosicionActualNominal(posicion);
            p.inicializaMejorPosicionNominal(posicion);
            p.asignarClasePosicionActual(claseAsignar);
            p.asignarClasePosicionMejor(claseAsignar);
            regla = p.getPosActual();
            calidad = calculaCalidad(regla);
            numeroMuestrasCubiertas = cTrain.numeroMuestrasCubiertasSinClase(
                    regla);
            p.asignarCalidadMPosicion(calidad);
            p.asignarMuestrasCubiertasActual(numeroMuestrasCubiertas);
            p.asignarMuestrasCubiertasPasado(numeroMuestrasCubiertas);
            p.asignarCalidadPosActual(calidad);
            particulas.addElement(p);
        }

        System.out.println("Poblacion inicializada");
        for (int i = 0; i < numeroIteraciones; i++) {
            p = (Particula) particulas.get(i);
            p.actualizaFeromona(particulas, i, tamEntorno,
                                listaCondicionesNominales);
        }

        System.out.println("Comienzo de la busqueda");
        //Busqueda
        for (int it = 0; it < maxIteraciones; it++) {
            for (int i = 0; i < numParticulas; i++) {
                //generar la siguiente posicion con feromona y si eso actualizar la anterior
                p = (Particula) particulas.get(i);
                p.generaPosicion(listaCondicionesNominales,
                                 listaCondicionesVacias, minimoCasosCubiertos,
                                 cTrain, generadorA, heuristica);
                regla = p.getPosActual();
                //******************************************************
                 // podada=regla.listaCondicionesNominales();
                 // podaRegla(podada,claseAsignar);
                 // regla.insertaCondicionesNominales(podada);
                 //******************************************************
                  calidad = calculaCalidad(regla);
                numeroMuestrasCubiertas = cTrain.
                                          numeroMuestrasCubiertasSinClase(regla);
                p.asignarMuestrasCubiertasActual(numeroMuestrasCubiertas);
                p.asignarCalidadPosActual(calidad);
                calidadPasado = p.obtenerCalidadMejorPosicion();
                if (calidad > calidadPasado) {
                    p.copiaPosicionMejorPosicion();
                }
            }

            for (int i = 0; i < numeroIteraciones; i++) {
                p = (Particula) particulas.get(i);
                p.actualizaFeromona(particulas, i, tamEntorno,
                                    listaCondicionesNominales);
            }
        }

        return particulas;

    }

    /**
     * Funcion que genera una posicion con condiciones continuas para una particula
     * @param clase Clase para la que se genera la regla
     * @return Vector con las condiciones que establecen la posicion de la particula
     */
    private Vector generaPosicionContinua(Atributo clase) {
        Condicion coMax;
        Condicion coMin;
        Atributo at;
        Vector devolver = new Vector();
        float minimo;
        float maximo;
        float aleatoreo; //Para calcular la nueva posicion
        float velocidad; //tamaño del intervalo
        Regla rule;
        int indice;
        int i = 0;

        rule = new Regla(new Vector(), new Vector(), clase);

        while (cTrain.cubreMinimo(rule, minimoCasosCubiertos) &&
               i < numCondicionesContinuas / 2) {
            minimo = intervalos[i][0];
            maximo = intervalos[i][1];
            indice = (int) intervalos[i][2];

            aleatoreo = (float) generadorA.Randdouble(minimo, maximo);
            velocidad = aleatoreo;
            at = new Atributo(velocidad, indice, 0);
            coMax = new Condicion(at, 2);

            aleatoreo = (float) generadorA.Randdouble(velocidad, maximo);
            velocidad = aleatoreo;

            at = new Atributo(velocidad, indice, 0);
            coMin = new Condicion(at, 1);
            devolver.addElement(coMax);
            devolver.addElement(coMin);
            i++;

            rule.insertarCondicionContinua(coMax);
            rule.insertarCondicionContinua(coMin);

        }

        for (int j = i; j < numCondicionesContinuas / 2; j++) {
            minimo = intervalos[j][0];
            maximo = intervalos[j][1];
            indice = (int) intervalos[j][2];

            at = new Atributo(minimo, indice, 0);
            coMax = new Condicion(at, 2);

            at = new Atributo(maximo, indice, 0);

            coMin = new Condicion(at, 1);
            devolver.addElement(coMax);
            devolver.addElement(coMin);
        }

        return devolver;
    }

    /**
     * Funcion que ejecuta el PSO
     * @param particulas Particulas sobre las que se ejecuta el PSO
     * @return Particulas colocadas en el espacio
     */
    private Vector psoNormal(Vector particulas) {
        int numParticulas = particulas.size();
        float calidad;
        float calidadAnterior;
        Regla regla;
        Particula p;
        Vector condicionesContinuas;
        Atributo clase;
        int numeroMuestrasCubiertas;
        //Inicializar parte continua de particulas
        //Inicializar la velocidad tb de las particulas


        for (int i = 0; i < numParticulas; i++) {
            p = (Particula) particulas.get(i);
            clase = p.getPosActual().obtenerReglaPredicha();
            condicionesContinuas = generaPosicionContinua(clase);
            p.reservaVelocidad(numCondicionesContinuas);
            p.inicializaPosicionActualContinua(condicionesContinuas);
            p.inicializaMejorPosicionContinua(condicionesContinuas);
            regla = p.getPosActual();
            calidad = calculaCalidad(regla);
            numeroMuestrasCubiertas = cTrain.numeroMuestrasCubiertasSinClase(
                    regla);
            p.asignarMuestrasCubiertasActual(numeroMuestrasCubiertas);
            p.asignarMuestrasCubiertasPasado(numeroMuestrasCubiertas);
            p.asignarCalidadMPosicion(calidad);
            p.asignarCalidadPosActual(calidad);
            p.inicializaVelocidad(intervalos, numCondicionesContinuas,
                                  generadorA);
            // Regla r=p.getPosActual();
            // r.imprime(numCondicionesContinuas);

        }

        //Busqueda
        for (int i = 0; i < maxIteraciones; i++) {
            //Para cada particula
            for (int j = 0; j < numParticulas; j++) {
                //Si es mejor actualizamos pasado
                p = (Particula) particulas.get(j);
                calidad = p.obtenerCalidadPosicionActual();
                calidadAnterior = p.obtenerCalidadMejorPosicion();
                if (calidad > calidadAnterior) {
                    p.copiaPosicionMejorPosicion();
                }
                //Cogemos el mejor
                p.calculaVelocidad(numCondicionesContinuas, particulas,
                                   tamEntorno, j, intervalos, x, c1, c2,
                                   generadorA);
                //Movemos particula
                //System.out.println("Antes: ");
                // p.imprimePosicion(numCondicionesContinuas);
                p.moverParticula(numCondicionesContinuas, intervalos);

                // System.out.println("Despues: ");
                // p.imprimePosicion(numCondicionesContinuas);
                //Calcualamos su calidad
                calidad = calculaCalidad(p.getPosActual());
                numeroMuestrasCubiertas = cTrain.
                                          numeroMuestrasCubiertasSinClase(p.
                        getPosActual());
                p.asignarMuestrasCubiertasActual(numeroMuestrasCubiertas);
                p.asignarCalidadPosActual(calidad);

            }
        }
        //System.exit(0);
        return particulas;
    }

    /**
     * Modulo que calcula las heuristicas de las condiciones
     * @param clase Clase para las que se genera la regla
     */
    private void calculaHeuristicasCondiciones(Atributo clase) {
        //Heuristicas de todas las condiciones
        Condicion co;
        Vector condiciones;
        float porcentaje = 0;
        float sumatoria;

        for (int i = 0; i < listaCondicionesNominales.size(); i++) {
            condiciones = (Vector) listaCondicionesNominales.get(i);
            sumatoria = 0;
            for (int j = 0; j < condiciones.size(); j++) {
                co = (Condicion) condiciones.get(j);
                porcentaje = cTrain.porcentajeMuestrasCondicion(co, clase);
                heuristica[i][j] = porcentaje;
                sumatoria += porcentaje;
            }
            heuristica[i][condiciones.size()] = cTrain.porcentajeMuestrasClase(
                    clase);
        }
    }

    /**
     * Funcion que crea la regla generica
     * @return Regla generica creada
     */
    public Regla creaReglaGenerica() {
        Regla devolver;
        int mayor = cTrain.obtenerMayorClase(listaClases);
        Atributo clase = (Atributo) listaClases.get(mayor);
        devolver = new Regla(new Vector(), new Vector(), clase);
        return devolver;

    }


    /**
     * Modulo que poda la particula Actual
     * @param p Particula a podar
     */
    private void podaParticulaActual(Particula p) {

        Regla condiciones = p.getPosActual();
        Atributo clase;

        //Poda Nominales
        Vector nominales = condiciones.listaCondicionesNominales();
        clase = condiciones.obtenerReglaPredicha();
        podaRegla(nominales, clase);
        condiciones.insertaCondicionesNominales(nominales);

        //Poda Continuos
        //Vector continuos=condiciones.listaCondicionesContinuos();
        //podaReglaContinua(continuos,clase);
        //condiciones.insertaCondicionesContinuos(continuos);

    }

    /**
     * Modulo que poda la mejor posicion de la particula
     * @param p Particula
     */
    private void podaParticulaPasada(Particula p) {

        Regla condiciones = p.getMejorPosicion();
        Atributo clase;

        //Poda Nominales
        Vector nominales = condiciones.listaCondicionesNominales();
        clase = condiciones.obtenerReglaPredicha();
        podaRegla(nominales, clase);
        condiciones.insertaCondicionesNominales(nominales);

        //Poda Continuos
        //Vector continuos=condiciones.listaCondicionesContinuos();
        //podaReglaContinua(continuos,clase);
        //condiciones.insertaCondicionesContinuos(continuos);

    }

    /**
     * Modulo que ejecuta el algoritmo
     */

    public void run() {

        int iteraciones = 0;
        float calidad1;
        float calidad2;
        int tamanioAntes;
        int tamanioDespues;
        Vector podar;
        Atributo clase;
        Regla regla;
        Regla regla2;
        Vector particulas = new Vector();
        Vector particulasDescubiertas = new Vector();
        Particula p = new Particula();
        ComparadorParticulas c = p.getComparadorParticulas();

        for (int i = 0; i < numCondicionesContinuas; i++) {
            System.out.println(intervalos[i][0] + "  " + intervalos[i][1]);
        }

        while (cTrain.tamanio() > maxCasosSinCubrir) {
            particulas = new Vector();
            // System.out.println(iteraciones+"  "+cTrain.tamanio());
            iteraciones++;
            //PSOACO
            particulas = PSOACO(particulas);
            System.out.println("PASOACO");
            //PSONORMAL
            particulas = psoNormal(particulas);
            System.out.println("PSONormal");
            //Obtener mejor Regla
            Collections.sort(particulas, c);
            p = (Particula) particulas.get(0);

            podaParticulaActual(p);
            podaParticulaPasada(p);
            calidad1 = calculaCalidad(p.getPosActual());
            calidad2 = calculaCalidad(p.getMejorPosicion());

            if (calidad2 > calidad1) {
                regla = p.getMejorPosicion();
            } else {
                regla = p.getPosActual();
            }

            regla.imprime(numCondicionesContinuas);
            System.out.println("Quedan " + cTrain.tamanio());
            tamanioAntes = cTrain.tamanio();
            cTrain.eliminaMuestrasCubiertas(regla);
            tamanioDespues = cTrain.tamanio();

            //Añadir a reglas descubiertas
            if (tamanioDespues < tamanioAntes) {
                particulasDescubiertas.addElement(regla);
            }
        }
        generica = creaReglaGenerica();

        sacaResultadosAFicheros(particulasDescubiertas);
        imprimeReglasDescubiertas(particulasDescubiertas);

    }

    /**
     * Modulo que imprime las reglas descubiertas
     * @param reglas Reglas descubiertas
     */

    private void imprimeReglasDescubiertas(Vector reglas) {
        Regla regla;
        float accuracyTrain;
        float accuracyTest;
        float tamTrain;
        float tamTest;
        Atributo clasePrediccion;
        Atributo claseReal;
        float acertadas;
        int numRegla;
        Muestra mt;
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

        salida.println("Reglas: ");
        for (int i = 0; i < reglas.size(); i++) {
            regla = (Regla) reglas.get(i);
            regla.imprime(numCondicionesContinuas);
            regla.imprimeFichero(numCondicionesContinuas, salida);
        }

        Atributo claseGenerica = generica.obtenerReglaPredicha();

        Attribute[] listaAtributos;
        listaAtributos = Attributes.getInputAttributes();
        Attribute actual;
        Vector nombresClases;
        String nombre;
        listaAtributos = Attributes.getOutputAttributes();
        actual = listaAtributos[0];
        nombresClases = actual.getNominalValuesList();
        nombre = (String) nombresClases.get((int) claseGenerica.getValor());

        claseGenerica.imprime("Generica ==> " + nombre);
        salida.println("Generica ==> " + nombre);

        //Accuracy Train
        tamTrain = cTrainC.tamanio();
        acertadas = 0;
        for (int i = 0; i < tamTrain; i++) {
            mt = cTrainC.obtenerMuestra(i);
            numRegla = 0;
            clasePrediccion = null;
            while (clasePrediccion == null && numRegla < reglas.size()) {
                regla = (Regla) reglas.get(numRegla);
                clasePrediccion = regla.prediccion(mt);
                numRegla++;
            }
            if (clasePrediccion == null) {
                clasePrediccion = generica.obtenerReglaPredicha();
            }
            claseReal = mt.getClase();
            if (clasePrediccion.esIgual(claseReal)) {
                acertadas++;
            }
        }
        accuracyTrain = acertadas / tamTrain;

        //Accuracy Test
        tamTest = cTest.tamanio();
        acertadas = 0;
        for (int i = 0; i < tamTest; i++) {
            mt = cTest.obtenerMuestra(i);
            numRegla = 0;
            clasePrediccion = null;
            while (clasePrediccion == null && numRegla < reglas.size()) {
                regla = (Regla) reglas.get(numRegla);
                clasePrediccion = regla.prediccion(mt);
                numRegla++;
            }
            if (clasePrediccion == null) {
                clasePrediccion = generica.obtenerReglaPredicha();
            }
            claseReal = mt.getClase();
            if (clasePrediccion.esIgual(claseReal)) {
                acertadas++;
            }
        }
        accuracyTest = acertadas / tamTest;

        System.out.println("Accuracy Train " + accuracyTrain);
        salida.println("Accuracy Train " + accuracyTrain);
        System.out.println("Accuracy Test " + accuracyTest);
        salida.println("Accuracy Test " + accuracyTest);
    }

    /**
     * Modulo que imprime un vector de condiciones
     * @param total Vector con condiciones para imprimir
     */
    private void imprimeVector(Vector total) {
        Vector condiciones;
        Condicion co;
        for (int i = 0; i < total.size(); i++) {
            condiciones = (Vector) total.get(i);
            for (int j = 0; j < condiciones.size(); j++) {
                co = (Condicion) condiciones.get(j);
                co.imprime();
            }
            System.out.println(
                    "**********************************************************");
        }
    }


    /**
     * Modulo que saca los resultados de la evaluacion de las reglas sobre los ficheros
     * @param reglasDescubiertas Vector con las reglas descubiertas
     */
    public void sacaResultadosAFicheros(Vector reglasDescubiertas) {
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
        int numRegla = 0;

        porcentajeTrain = 0;
        porcentajeTest = 0;

        Attribute[] listaAtributos;
        listaAtributos = Attributes.getInputAttributes();
        Attribute actual;
        Vector nombresClases;
        String nombre;
        listaAtributos = Attributes.getOutputAttributes();
        actual = listaAtributos[0];
        nombresClases = actual.getNominalValuesList();

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
                salida.print((String) nombresClases.get((int) claseOriginal.getValor()) + " ");
                if (clasePredicha == null) {
                    clasePredicha = generica.obtenerReglaPredicha();
                }

                salida.println((String) nombresClases.get((int) clasePredicha.getValor()));
                if (claseOriginal.equals(clasePredicha)) {
                    porcentajeTrain++;
                }
            }
            porcentajeTrain = porcentajeTrain / tamanioConjunto;
            //salida.println(porcentajeTrain);

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
                salida.print((String) nombresClases.get((int) claseOriginal.getValor()) + " ");
                if (clasePredicha == null) {
                    clasePredicha = generica.obtenerReglaPredicha();
                }
                salida.println((String) nombresClases.get((int) clasePredicha.getValor()));
                if (clasePredicha.equals(claseOriginal)) {
                    porcentajeTest++;
                }

            }
            porcentajeTest = porcentajeTest / tamanioConjunto;
            //  salida.println(porcentajeTest);
        } catch (FileNotFoundException e) {
            System.err.println("El fichero " + fOutTest + " no se pudo crear");
            System.exit(0);
        }

    }


}

