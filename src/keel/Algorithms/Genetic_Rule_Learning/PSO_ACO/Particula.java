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
 * <p>Title: Particula (Particle)</p>
 * <p>Description: Particle class:
 *    Implements the particles needed on the Particle Swarm Optimization algorithm (PSO).
 *    This class stores the position and velocity of a standard particle of this algorithm,
 *    also manage those variables by progressing through time, but also stores and manages information used
 *    by the ACO algorithm like the pheromone matrix. This allows to hybridize these tow algorithms.
 * @author Vicente Rubén del Pino Ruiz
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
   *  Default constructor. An empty particle is built.
   */
    public Particula() {
        posicion = new Regla();
        mPosicion = new Regla();
        c = new ComparadorParticulas();

    }

  /**
     *  Paramater Constructor.
     *  Creates an particle with the number of taken condictions given.
     *
     * @param numCondiciones number of continuous conditions given.
     */
    public Particula(int numCondiciones) {
        posicion = new Regla();
        mPosicion = new Regla();
        c = new ComparadorParticulas();
        velocidad = new float[numCondiciones];
    }

    /**
     * Reserves memory enough to manage the particle velocity. 
     * The velocity vector will be as bigger as the number of conditions taken by the rule.
     * @param numCondiciones number of conditions taken by the rule.
     */
    public void reservaVelocidad(int numCondiciones) {
        velocidad = new float[numCondiciones];
    }

  /**
   * Returns the Particles comparative method.
   * @return the Particles comparative method. 
   */
    public ComparadorParticulas getComparadorParticulas() {
        return c;
    }

    /**
     * Initializes the actual continuous particle position with the given conditions vector
     * @param pos given conditions vector.
     */
    public void inicializaPosicionActualContinua(Vector pos) {
        posicion.insertaCondicionesContinuos(pos);
    }
    
    /**
     * Initializes the actual nominal particle position with the given conditions vector
     * @param pos given conditions vector.
     */
    public void inicializaPosicionActualNominal(Vector pos) {
        posicion.insertaCondicionesNominales(pos);
    }

    /**
     * Initializes the best continuous particle position with the given conditions vector
     * @param pos given conditions vector.
     */
    public void inicializaMejorPosicionContinua(Vector pos) {
        mPosicion.insertaCondicionesContinuos(pos);
    }

    /**
     * Initializes the best nominal particle position with the given conditions vector
     * @param pos given conditions vector.
     */
    public void inicializaMejorPosicionNominal(Vector pos) {
        mPosicion.insertaCondicionesNominales(pos);
    }

    /**
     * Initializes the particle velocity with the given value for all the conditions considered.
     * @param numCondiciones number of continuous conditions considered.
     * @param valorInicial initial velocity value.
     */
    public void inicializaVelocidad(int numCondiciones, float valorInicial) {
        for (int i = 0; i < numCondiciones; i++) {
            velocidad[i] = valorInicial;
        }
    }


     /**
     * Initializes the particle pheromone values for the given list of conditions.
     * @param listaCondiciones conditions to computes their pheromone values.
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
     * Assigns the class for the actual position of the particle.
     * @param clase class to be assgined.
     */
    public void asignarClasePosicionActual(Atributo clase) {
        posicion.insertarClase(clase);
    }

    /**
     * Assigns the class to the best position of the particle.
     * @param clase class to be assigned.
     */
    public void asignarClasePosicionMejor(Atributo clase) {
        mPosicion.insertarClase(clase);
    }
    
     /**
     * Assigns the given nominal condition to the best position of the particle.
     * @param condicion given condition to be assigned.
     */
    public void asignarCondicionNominalPosicionMejor(Condicion condicion) {
        mPosicion.insertarCondicionNominal(condicion);
    }

     /**
     * Assigns the given continuous condition to the best position of the particle.
     * @param condicion given condition to be assigned.
     */
    public void asignarCondicionContinuaPosicionMejor(Condicion condicion) {
        mPosicion.insertarCondicionContinua(condicion);
    }

     /**
     * Assigns the given nominal condition to the actual position of the particle.
     * @param condicion given condition to be assigned.
     */
    public void asignarCondicionNominalPosicionActual(Condicion condicion) {
        posicion.insertarCondicionNominal(condicion);
    }

     /**
     * Assigns the given continuous condition to the actual position of the particle.
     * @param condicion given condition to be assigned.
     */
    public void asignarCondicionContinuaPosicionActual(Condicion condicion) {
        posicion.insertarCondicionContinua(condicion);
    }

    /** 
     * Returns the actual position of the particle.
     * @return the actual position of the particle. 
     */
    public Regla getPosActual() {
        return posicion;
    }

     /** 
     * Returns the best position of the particle.
     * @return the best position of the particle. 
     */
    public Regla getMejorPosicion() {
        return mPosicion;

    }

     /**
     * Assigns the given quality to the actual position of the particle.
     * @param calidad given quality to be assigned.
     */
    public void asignarCalidadPosActual(float calidad) {
        posicion.asignarCalidad(calidad);
    }

     /**
     * Assigns the given quality to the best position of the particle.
     * @param calidad given quality to be assigned.
     */
    public void asignarCalidadMPosicion(float calidad) {
        mPosicion.asignarCalidad(calidad);
    }

    /**
     * Updates the pheromone values following the environment of particles.
     * @param particulas All the particles in the environment.
     * @param indice Position of particle to update its pheromone. 
     * @param tamEntorno Environment size.
     * @param listaCondiciones Conditions list.
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
     * Modifies the pheromone of this particle using its best neighbour
     * @param vecino Best neighbour of the particle.
     * @param listaCondiciones Conditions list. 
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
     * Selects a condition to be added into the actual rule.
     * @param indice pheromone index.
     * @param numCondiciones number of conditions considered
     * @param generadorA Random numbers generator.
     * @param heuristica Heuristic values for the different conditions.
     * @return the selected condition to be added into the actual rule.
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
     * Generates the particle position using the given posible conditions, its heuristic
     * values and the data to be covered.
     * @param listaCondiciones all posible conditions to be used to generate the position (rule).
     * @param listaCondicionesVacias empty/null conditions list.
     * @param minimoCasosRegla minimum number of cases that have to be covered to consider the rule as valid.
     * @param cTrain the data to be covered.
     * @param generadorA Random numbers generator.
     * @param heuristica Heuristic values for the different conditions.
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
     * Returns the quality of the actual position.
     * @return the quality of the actual position. 
     */
    public float obtenerCalidadPosicionActual() {
        float devolver;
        devolver = posicion.obtenerCalidad();
        return devolver;
    }

    /**
     * Returns the quality of the best position.
     * @return the quality of the best position. 
     */
    public float obtenerCalidadMejorPosicion() {
        float devolver;
        devolver = mPosicion.obtenerCalidad();
        return devolver;
    }

    /**
     * Copies the actual position as best position of the particle.
     */
    public void copiaPosicionMejorPosicion() {
        Condicion co;
        mPosicion.copia(posicion);
    }

    
    /**
     * Initializes the particle velocity using the given conditions intervals.
     * @param intervalos continuous conditions intervals.
     * @param numCondicionesContinuas number of continuous conditions considered.
     * @param generadorA Random numbers generator.
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
     * Returns a vector with the neighbours of the particle.
     * @param indice position of the particle in the environment.
     * @param tamEntorno size of the environment.
     * @param particulas vector of considered particles, environment.
     * @return a vector with the neighbours of the particle. 
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
     * Computes the velocity of the particle.
     * @param numCondiciones number of conditions considered.
     * @param particulas vector of considered particles, environment.
     * @param entorno size of the environment.
     * @param indice particle position in the environment.
     * @param intervalos continuous conditions intervals.
     * @param x Factor X
     * @param c1 Factor c1
     * @param c2 Factor c2
     * @param generadorA Random numbers generator.
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
     * Moves the particle to the next position, with the actual position and its velocity.
     * @param numCondiciones  number of conditions considered.
     * @param intervalos  continuous conditions intervals.
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
     * Prints on the standard output the actual position.
     * @param numCondiciones number of conditions considered.
     */
    public void imprimePosicion(int numCondiciones) {
        posicion.imprime(numCondiciones);
    }

    /**
     * Removes all nominal conditions from the particle.
     */
    public void limpiaCNominales() {
        posicion.limpiaCNominales();
    }

    /**
     * Assigns the number of examples covered by the actual position of the particle.
     * @param numero number of examples covered.
     */
    public void asignarMuestrasCubiertasActual(int numero) {
        posicion.asignarNumeroMuestrasCubiertas(numero);
    }

    /**
     * Assigns the number of examples covered by the best position of the particle.
     * @param numero number of examples covered.
     */
    public void asignarMuestrasCubiertasPasado(int numero) {
        mPosicion.asignarNumeroMuestrasCubiertas(numero);
    }

}

