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

/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class Particula {
    //Velocidad
    float[] velocidad;
    //Posicion
    Regla posicion;
    //Mejor Posicion
    Regla mPosicion;
    //Feromona-->No,Valores. Solo los nominales!!!!!!!!!!!!
    float[][] feromona;
    //Comparador de particulas
    private static ComparadorParticulas c;


    /**
     * Constructor
     */
    public Particula() {
        posicion = new Regla();
        mPosicion = new Regla();
        c = new ComparadorParticulas();

    }

    /**
     * Constructor
     * @param numCondiciones Numero de condiciones continuas de la regla
     */

    public Particula(int numCondiciones) {
        posicion = new Regla();
        mPosicion = new Regla();
        c = new ComparadorParticulas();
        velocidad = new float[numCondiciones];
    }

    /**
     * Modulo que reserva la memoria para el vector de velocidad de la regla
     * @param numCondiciones Numero de condiciones continuas de la regla
     */
    public void reservaVelocidad(int numCondiciones) {
        velocidad = new float[numCondiciones];
    }

    /**
     * Funcion que devuelve un comparador de Particulas
     * @return Comparador de Particulas
     */
    public ComparadorParticulas getComparadorParticulas() {
        return c;
    }

    /**
     * Modulo que inicializa la posicion de la particula con el vector de condiciones que se le pasa
     * @param pos Vector con las condiciones de la regla.
     */
    public void inicializaPosicionActualContinua(Vector pos) {
        posicion.insertaCondicionesContinuos(pos);
    }

    /**
     * Modulo que inicializa la posicion de la particula con el vector de condiciones que se le pasa
     * @param pos Vector con las condiciones de la regla.
     */

    public void inicializaPosicionActualNominal(Vector pos) {
        posicion.insertaCondicionesNominales(pos);
    }

    /**
     * Modulo que inicializa la posicion de la particula con el vector de condiciones que se le pasa
     * @param pos Vector con las condiciones de la regla.
     */

    public void inicializaMejorPosicionContinua(Vector pos) {
        mPosicion.insertaCondicionesContinuos(pos);
    }

    /**
     * Modulo que inicializa la posicion de la particula con el vector de condiciones que se le pasa
     * @param pos Vector con las condiciones de la regla.
     */

    public void inicializaMejorPosicionNominal(Vector pos) {
        mPosicion.insertaCondicionesNominales(pos);
    }

    /**
     * Modulo que inicializa el valor de la velocidad
     * @param numCondiciones Numero de condiciones continuas de la regla
     * @param valorInicial Valor inicial para la velocidad
     */
    public void inicializaVelocidad(int numCondiciones, float valorInicial) {
        for (int i = 0; i < numCondiciones; i++) {
            velocidad[i] = valorInicial;
        }
    }


    /**
     * Modulo que inicializa la feromona para las condiciones
     * @param listaCondiciones lista de las condiciones para calcular la feromona
     */
    public void inicializaFeromona(Vector listaCondiciones) {
        int numCondiciones = listaCondiciones.size();
        int numValores;
        Vector condiciones;

        feromona = new float[numCondiciones][];
        for (int i = 0; i < numCondiciones; i++) {
            condiciones = (Vector) listaCondiciones.get(i);
            numValores = condiciones.size();
            feromona[i] = new float[numValores + 1]; //El +1 es por la posicion feromona de no añadir condicion
        }

        for (int i = 0; i < numCondiciones; i++) {
            condiciones = (Vector) listaCondiciones.get(i);
            numValores = condiciones.size();
            for (int j = 0; j < numValores; j++) {
                feromona[i][j] = (float) 0.9 / numValores; //0.9 Para el estado On
            }
            feromona[i][numValores] = (float) 0.1; //0.1 Para el estado off
        }
    }


    /**
     * Modulo que asigna una clase a la posicion actual de la particula
     * @param clase Clase a asignar a la posicion
     */
    public void asignarClasePosicionActual(Atributo clase) {
        posicion.insertarClase(clase);
    }

    /**
     * Modulo que asigna una clase a la mejor posicion de la particula
     * @param clase Clase a asignar a la posicion
     */
    public void asignarClasePosicionMejor(Atributo clase) {
        mPosicion.insertarClase(clase);
    }

    /**
     * Modulo que asigna una condicion a la mejor posicion de la regla
     * @param condicion Condicion asignar a la regla
     */
    public void asignarCondicionNominalPosicionMejor(Condicion condicion) {
        mPosicion.insertarCondicionNominal(condicion);
    }

    /**
     * Modulo que asigna una condicion a la mejor posicion de la regla
     * @param condicion Condicion asignar a la regla
     */

    public void asignarCondicionContinuaPosicionMejor(Condicion condicion) {
        mPosicion.insertarCondicionContinua(condicion);
    }

    /**
     * Modulo que asigna una condicion a la posicion de la regla
     * @param condicion Condicion asignar a la regla
     */


    public void asignarCondicionNominalPosicionActual(Condicion condicion) {
        posicion.insertarCondicionNominal(condicion);
    }

    /**
     * Modulo que asigna una condicion a la posicion de la regla
     * @param condicion Condicion asignar a la regla
     */

    public void asignarCondicionContinuaPosicionActual(Condicion condicion) {
        posicion.insertarCondicionContinua(condicion);
    }

    /**
     * Funcion que devuelve la posicion actual de la particula
     * @return Regla de la posicion actual
     */
    public Regla getPosActual() {
        return posicion;
    }

    /**
     * Funcion que devuelve la mejor posicion de la particula
     * @return Regla de la mejor posicion
     */
    public Regla getMejorPosicion() {
        return mPosicion;

    }

    /**
     * Modulo que asigna la calidad a la posicion actual
     * @param calidad Calidad a asignar
     */
    public void asignarCalidadPosActual(float calidad) {
        posicion.asignarCalidad(calidad);
    }

    /**
     * Modulo que asigna la calidad a la mejor posicion
     * @param calidad Calidad a asignar
     */

    public void asignarCalidadMPosicion(float calidad) {
        mPosicion.asignarCalidad(calidad);
    }

    /**
     * Modulo que actualiza la feromona de la particula
     * @param particulas Vector con todas las particulas del entorno
     * @param indice Posicion de la particula
     * @param tamEntorno Tamaño del entorno
     * @param listaCondiciones Lista de condiciones
     */
    public void actualizaFeromona(Vector particulas, int indice, int tamEntorno,
                                  Vector listaCondiciones) {

        Vector entorno;
        Particula vecino;

        entorno = obtenerVecinos(indice, tamEntorno, particulas);
        //Ya tenemos todos los componentes del entorno
        //Ahora se ordenan por calidad para coger al mejor
        Collections.sort(entorno, c);
        vecino = (Particula) entorno.get(0);
        modificarFeromona(vecino, listaCondiciones);

    }

    /**
     * Modulo que modifica la Feromona de una particula
     * @param vecino Mejor vecino del entorno de la particula
     * @param listaCondiciones Lista con las condiciones
     */
    private void modificarFeromona(Particula vecino, Vector listaCondiciones) {
        Regla reglaVecino;
        Vector condicionesVecino;
        Vector condiciones;
        float calidadVecino;
        float calidad;
        int numCondicionesVecino;
        Condicion c;
        int valor;
        int tam;
        float sumatoria;

        reglaVecino = vecino.getPosActual();
        condicionesVecino = reglaVecino.listaCondicionesNominales();
        calidadVecino = reglaVecino.obtenerCalidad();
        calidad = posicion.obtenerCalidad();
        numCondicionesVecino = condicionesVecino.size();

        if (calidadVecino > calidad) {
            //Actualizamos la Feromona de las condiciones del vecino
            for (int i = 0; i < numCondicionesVecino; i++) {
                c = (Condicion) condicionesVecino.get(i);
                valor = (int) c.getValor().getValor();
                condiciones = (Vector) listaCondiciones.get(i);
                if (valor != -1) {
                    feromona[i][valor] += feromona[i][valor] * calidadVecino;
                } else {
                    feromona[i][condiciones.size()] += feromona[i][condiciones.
                            size()] * calidadVecino;
                }
            }
        } else {
            //Actualizamos la Feromona de las condiciones propias
            for (int i = 0; i < numCondicionesVecino; i++) { //Son el mismo numero de condiciones
                c = posicion.getCondicionNominal(i);
                valor = (int) c.getValor().getValor();
                condiciones = (Vector) listaCondiciones.get(i);
                if (valor != -1) {
                    feromona[i][valor] += feromona[i][valor] * calidad;
                } else {
                    feromona[i][condiciones.size()] += feromona[i][condiciones.
                            size()] * calidad;
                }
            }
        } //del else

        //Actualizamos la Feromona de las condiciones del mejor pasado
        for (int i = 0; i < numCondicionesVecino; i++) { //Son el mismo numero de condiciones
            c = mPosicion.getCondicionNominal(i);
            valor = (int) c.getValor().getValor();
            condiciones = (Vector) listaCondiciones.get(i);
            if (valor != -1) {
                feromona[i][valor] += feromona[i][valor] * calidad;
            } else {
                feromona[i][condiciones.size()] += feromona[i][condiciones.size()] *
                        calidad;
            }
        }

        //Normalizar Feromona
        for (int i = 0; i < numCondicionesVecino; i++) {
            //Recorrer cada atributo sumando y luego dividir
            condiciones = (Vector) listaCondiciones.get(i);
            tam = condiciones.size();
            sumatoria = 0;
            for (int j = 0; j <= tam; j++) {
                sumatoria += feromona[i][j];
            }
            for (int j = 0; j <= tam; j++) {
                feromona[i][j] = feromona[i][j] / sumatoria;
            }
        }

    }


    /**
     * Funcion que devuelve la condicion escogida para insertar en la regla
     * @param indice Posicion para la que se busca la condicion
     * @param numCondiciones Numero de condiciones Continuas
     * @param generadorA Generador de numeros Aleatoreos
     * @param heuristica Heuristicas de las condiciones
     * @return Posicion de la condicion escogida
     */
    private int obtenerCondicion(int indice, int numCondiciones,
                                 Randomize generadorA, float[][] heuristica) {
        float probabilidadEscoger;
        float probabilidadAcumulada = 0;
        int devolver = 0;
        float division = 0;

        for (int i = 0; i <= numCondiciones; i++) {
            division += feromona[indice][i] * heuristica[indice][i];
        }

        probabilidadEscoger = (float) generadorA.Rand();
        int i = 0;
        probabilidadAcumulada += (feromona[indice][i] * heuristica[indice][i]) /
                division;
        while (probabilidadAcumulada < probabilidadEscoger) {
            i++;
            probabilidadAcumulada +=
                    (feromona[indice][i] * heuristica[indice][i]) / division;
        }
        return i;
    }

    /**
     * Modulo que genera la posicion de la particula
     * @param listaCondiciones Lista condiciones
     * @param listaCondicionesVacias Lista condiciones vacias
     * @param minimoCasosRegla Minimo numero de casos que debe cubrir una regla
     * @param cTrain Conjunto de entrenamiento
     * @param generadorA Generador de numeros aleatoreos
     * @param heuristica Heuristicas de las condiciones
     */

    public void generaPosicion(Vector listaCondiciones,
                               Vector listaCondicionesVacias,
                               int minimoCasosRegla, ConjuntoDatos cTrain,
                               Randomize generadorA, float[][] heuristica) {
        Condicion elegir;
        Vector condiciones;
        int pos;
        boolean parada = false;
        posicion.eliminaCondicionesNominales();

        int i;

        posicion.limpiaCNominales();
        for (i = 0;
                 i < listaCondiciones.size() && cTrain.cubreMinimo(posicion, minimoCasosRegla);
                 i++) {
            pos = obtenerCondicion(i, ((Vector) listaCondiciones.get(i)).size(),
                                   generadorA, heuristica);
            condiciones = (Vector) listaCondiciones.get(i);
            if (pos >= condiciones.size()) {
                elegir = (Condicion) listaCondicionesVacias.get(i);
            } else {
                elegir = (Condicion) condiciones.get(pos);
            }
            posicion.insertarCondicionNominal(elegir);
        }

        //Añadimos las condiciones Vacias
        if (i < listaCondiciones.size()) { //Faltan por insertar los vacios
            for (int j = i; j < listaCondiciones.size(); j++) {
                elegir = (Condicion) listaCondicionesVacias.get(j);
                posicion.insertarCondicionNominal(elegir);
            }
        }

    }

    /**
     * Funcion que devuelve la calidad de la posicion actual
     * @return Calidad de la posicion actual
     */

    public float obtenerCalidadPosicionActual() {
        float devolver;
        devolver = posicion.obtenerCalidad();
        return devolver;
    }

    /**
     * Funcion que devuelve la calidad de la mejor posicion de la particula
     * @return Calidad de la mejor posicion de la particula
     */
    public float obtenerCalidadMejorPosicion() {
        float devolver;
        devolver = mPosicion.obtenerCalidad();
        return devolver;
    }


    /**
     * Modulo que copia de la posicion actual a la mejor posicion de la particula
     */

    public void copiaPosicionMejorPosicion() {
        Condicion co;
        mPosicion.copia(posicion);
    }


    /**
     * Modulo que inicializa la velocidad de la particula
     * @param intervalos Intervalos de las condiciones continuas
     * @param numCondicionesContinuas Numero de condiciones continuas
     * @param generadorA Generador de numeros aleatoreos
     */
    public void inicializaVelocidad(float[][] intervalos,
                                    int numCondicionesContinuas,
                                    Randomize generadorA) {
        float tamIntervalo;
        float minimo;
        float maximo;
        float velocidadMax;
        float aleatoreo;
        int i = 0;

        while (i < numCondicionesContinuas) {
            minimo = intervalos[i][0];
            maximo = intervalos[i][1];
            tamIntervalo = maximo - minimo;
            velocidadMax = (float) 0.25 * tamIntervalo;
            aleatoreo = (float) generadorA.Randdouble(0, velocidadMax);
            velocidad[i] = aleatoreo;
            i++;
            aleatoreo = (float) generadorA.Randdouble( -velocidadMax, 0);
            velocidad[i] = aleatoreo;
            i++;
        }

    }

    /**
     * Funcion que devuelve un vector con los n vecinos del entorno de la particula
     * @param indice Posicion de la particula
     * @param tamEntorno Tamaño del entorno de la particula
     * @param particulas Vector con las particulas
     * @return Vector con las particulas vecinas
     */
    public Vector obtenerVecinos(int indice, int tamEntorno, Vector particulas) {

        Vector entorno;
        Particula vecino;
        int numParticulas = particulas.size();
        int antes = tamEntorno / 2;
        int despues = antes;
        int recorrer;
        float calidadMejorVecino;
        float calidad;

        entorno = new Vector();

        //Cogemos los vecinos de su entorno
        if (antes > indice) { //Si se sale al principio del entorno cogemos particulas del final
            recorrer = antes - indice; //Particulas que hay que coger del final
            for (int i = numParticulas - recorrer; i < numParticulas; i++) {
                vecino = (Particula) particulas.get(i);
                entorno.addElement(vecino);
            }
            if (indice > 0) { //Si hay que coger tb del principio se cogen
                for (int i = 0; i < indice; i++) {
                    vecino = (Particula) particulas.get(i);
                    entorno.addElement(vecino);
                }
            }
        } else { //Si no se sale al principio del entorno
            for (int i = indice - antes; i < indice; i++) {
                vecino = (Particula) particulas.get(i);
                entorno.addElement(vecino);
            }
        }

        //Los que estan detras
        if ((indice + despues) > numParticulas) { //Si se sale al principio del entorno cogemos particulas del final
            recorrer = (indice + despues) - numParticulas; //Particulas que hay que coger del final
            for (int i = 0; i < recorrer; i++) {
                vecino = (Particula) particulas.get(i);
                entorno.addElement(vecino);
            }
            if (indice < numParticulas) { //Si hay que coger tb del principio se cogen
                for (int i = indice + 1; i < numParticulas; i++) {
                    vecino = (Particula) particulas.get(i);
                    entorno.addElement(vecino);
                }
            }
        } else { //Si no se sale al final del entorno
            for (int i = indice + 1; i < indice + despues; i++) {
                vecino = (Particula) particulas.get(i);
                entorno.addElement(vecino);
            }
        }

        return entorno;
    }


    /**
     * Modulo que calcula la velocidad de la particula
     * @param numCondiciones Numero de condiciones continuas
     * @param particulas Vector con las particulas vecinas
     * @param entorno Tamaño del entorno de la particula
     * @param indice Posicion de la particula
     * @param intervalos Intervalos para las condiciones continuas
     * @param x Factor X
     * @param c1 Factor c1
     * @param c2 Factor c2
     * @param generadorA Generador de numero aleatoreos
     */
    public void calculaVelocidad(int numCondiciones, Vector particulas,
                                 int entorno, int indice, float[][] intervalos,
                                 float x, float c1, float c2,
                                 Randomize generadorA) {
        Regla posicionVecino;
        Vector vecinos;
        Particula mejorVecino;
        float valorCalculado;
        float valorVecino;
        float valorActual;
        float mejorValor;
        float aleatoreo1;
        float aleatoreo2;
        float tamIntervalo;
        Condicion co;

        vecinos = obtenerVecinos(indice, entorno, particulas);
        Collections.sort(vecinos, c);
        mejorVecino = (Particula) vecinos.get(0);
        posicionVecino = mejorVecino.getPosActual();

        for (int i = 0; i < numCondiciones; i++) {
            co = posicion.getCondicionContinua(i);
            valorActual = co.getValor().getValor();

            co = posicionVecino.getCondicionContinua(i);
            valorVecino = co.getValor().getValor();

            co = mPosicion.getCondicionContinua(i);
            mejorValor = co.getValor().getValor();

            aleatoreo1 = (float) generadorA.Rand();
            aleatoreo2 = (float) generadorA.Rand();

            //Ya tenemos todos los valores necesarios
            valorCalculado = x *
                             (velocidad[i] + c1 * aleatoreo1 * (mejorValor - valorActual) +
                              c2 * aleatoreo2 * (valorVecino - valorActual));
            velocidad[i] = valorCalculado;

            tamIntervalo = intervalos[i][1] - intervalos[i][0];
            if (velocidad[i] > tamIntervalo * 0.25) {
                velocidad[i] = (float) tamIntervalo * (float) 0.25;
            }
            if (velocidad[i] < (0 - tamIntervalo) * 0.25) {
                velocidad[i] = (float) (0 - tamIntervalo) * (float) 0.25;
            }

        }


    }


    /**
     * Modulo que mueve la particula
     * @param numCondiciones Numero de condiciones continuas
     * @param intervalos Intervalos para las condiciones continuas
     */
    public void moverParticula(int numCondiciones, float[][] intervalos) {
        Condicion co;
        Condicion con;
        Atributo at;
        int operador;
        float valor1;
        float valor2;
        int i = 0;
        int intervalo = 0;
        while (i < numCondiciones) {
            co = posicion.getCondicionContinua(i);
            valor1 = co.getValor().getValor();
            valor1 += velocidad[i];

            if (valor1 >= intervalos[intervalo][1]) {
                valor1 = intervalos[intervalo][0];
            }
            if (valor1 < intervalos[intervalo][0]) {
                valor1 = intervalos[intervalo][0];
            }

            at = new Atributo(valor1, co.getValor().getAtributo(),
                              co.getValor().getTipo());
            operador = co.getOperador();
            con = new Condicion(at, operador);
            posicion.setCondicionContinua(i, con);
            i++;

            co = posicion.getCondicionContinua(i);
            valor2 = co.getValor().getValor();
            valor2 += velocidad[i];

            if (valor1 > valor2) {
                valor2 = (valor1 + 1);
            }
            if (valor2 > intervalos[intervalo][1]) {
                valor2 = intervalos[intervalo][1];
            }
            if (valor2 < intervalos[intervalo][0]) {
                valor2 = intervalos[intervalo][0];
            }

            at = new Atributo(valor2, co.getValor().getAtributo(),
                              co.getValor().getTipo());
            operador = co.getOperador();
            con = new Condicion(at, operador);
            posicion.setCondicionContinua(i, con);
            i++;
            intervalo++;

        }

    }

    /**
     * Modulo que imprime la posicion de la particula
     * @param numCondiciones Numero de condiciones continuas
     */
    public void imprimePosicion(int numCondiciones) {
        posicion.imprime(numCondiciones);
    }

    /**
     * Modulo que elimina las condiciones nominales de la particula
     */
    public void limpiaCNominales() {
        posicion.limpiaCNominales();
    }

    /**
     * Modulo que asigna las muestras cubiertas por una particula
     * @param numero Numero de muestras cubiertas por la particula
     */
    public void asignarMuestrasCubiertasActual(int numero) {
        posicion.asignarNumeroMuestrasCubiertas(numero);
    }

    /**
     * Modulo que asigna las muestras cubiertas por la mejor posicion de la particula
     * @param numero Numero de muestras cubiertas por la particula
     */
    public void asignarMuestrasCubiertasPasado(int numero) {
        mPosicion.asignarNumeroMuestrasCubiertas(numero);
    }

}

