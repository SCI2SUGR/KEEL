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

import java.util.*;

import keel.Dataset.*;

import java.io.PrintStream;

/**
 * <p>Título: Ant Colony Optimization</p>
 * <p>Descripción: Clase regla.
 *    Representa una regla descubierta por el algoritmo ACO. </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class Regla {

    private Vector condiciones; //Atributos de la regla
    private Atributo clase; //Clase de la regla
    private float calidad; //Calidad de la regla a la hora de clasificar
    private static ComparadorRegla c;
    private float muestrasCubiertas; //Segunda medicion de calidad


    /**
     * Constructor por defecto
     *
     * Crea una regla vacia, sin atributos y sin clase definida
     */


    public Regla() {

        condiciones = new Vector();
        clase = new Atributo();
        c = new ComparadorRegla();
        muestrasCubiertas = 0;
        calidad = 0;

    }

    /**
     * Constructor de copia.
     * Crea una regla a partir de la que se le pasa por parametro.
     * @param regla Regla Regla a copiar en la actual
     */

    public Regla(Regla regla) {
        condiciones = new Vector(regla.condiciones);
        clase = new Atributo(regla.clase);
        muestrasCubiertas = regla.muestrasCubiertas;
        calidad = regla.calidad;
        muestrasCubiertas = regla.muestrasCubiertas;
    }

    /**
     *  Constructor
     *  Crea una regla a partir de los atributos y la clase que se le pasan
     *  por parametro.
     *
     * @param conjuntoAtributos Vector
     * @param claseOriginal String
     */

    public Regla(Vector conjuntoAtributos, Atributo claseOriginal) {

        condiciones = new Vector(conjuntoAtributos);
        clase = claseOriginal;
        c = new ComparadorRegla();

    }

    /**
     * Modulo que inserta un atributo junto con su valor en la regla
     *
     * @param original Atributo Atributo que se inserta en la regla.
     */
    public void insertarAtributo(Atributo original) {
        condiciones.addElement(original);
    }

    /**
     * Modulo que inserta la clase a la que identifica la regla con todos
     * sus atributos
     *
     * @param original Atributo Clase que se inserta en la regla
     */
    public void insertarClase(Atributo original) {
        clase = original;
    }


    /**
     * Modulo que ordena los atributos de la regla de menor a mayor
     *
     */
    public void ordenaAtributos() {
        ComparadorAtributo c = Atributo.obtenerComparador();
        Collections.sort(condiciones, c);
    }

    /**
     * Modulo que asigna la calidad correspondiente a la regla
     *
     * @param original double Calidad que se asignara a la regla
     */
    public void asignarCalidad(float original) {
        calidad = original;
    }


    /**
     * Modulo que inserta el numero de muestras cubiertas
     * @param numero int Numero de muestras cubiertas
     */
    public void asignarMuestrasCubiertas(float numero) {
        muestrasCubiertas = numero;
    }

    /**
     * Funcion que avisa si un atributo esta ya junto con su valor en una regla
     *
     * @param original Atributo Atributo que se busca en la regla
     * @return boolean True si el atributo esta en la regla, False en caso contrario
     */
    public boolean estaAtributo(Atributo original) {
        boolean devolver = condiciones.contains(original);
        return devolver;
    }


    /**
     * Funcion que indica si un atributo ya tiene asignado un valor en la regla
     * @param atributo int se busca si el atributo ya tiene valor insertado
     * @return boolean True si el atributo ya tiene un valor, false en caso contrario
     */
    public boolean tieneValorAtributo(int atributo) {
        int indice = atributo;
        Atributo actual;
        for (int i = 0; i < condiciones.size(); i++) {
            actual = (Atributo) condiciones.get(i);
            if (actual.getAtributo() == indice) { //Si se encuentra que el atributo ya tiene un valor asignado
                return true; //se sale del bucle y se devuelve true;
            }
        }
        return false;
    }

    /**
     * Funcion que devuelve la clase predicha por la regla para la muestra en
     * el caso de que la regla tenga las mismas condiciones que la muestra. En
     * caso contrario devuelve null.
     * @param mt Muestra Muestra a comprobar su clase predicha
     * @return Atributo Clase predica por la regla
     */
    public Atributo prediccion(Muestra mt) {
        Atributo clasePredicha = null;
        if (estanAtributosEn(mt)) {
            clasePredicha = clase;
        }
        return clasePredicha;
    }

    /**
     * Funcion que indica si la regla cubre la muestra que se le pasa por argumento
     * @param ejemplo Muestra Muestra a comprobar si se cubre con la regla o no.
     * @return boolean Boolean que indica si se cubre la muestra (true) o false en caso contrario
     */

    public boolean cubreMuestra(Muestra ejemplo) {

        int numCondiciones = condiciones.size();
        int columnaComprobar;
        Atributo condicionActual;
        Atributo condicionComprobar;

        for (int i = 0; i < numCondiciones; i++) {
            condicionActual = (Atributo) condiciones.get(i); //Cogemos una condicion de la regla
            columnaComprobar = condicionActual.getAtributo(); //Miramos a que atributo corresponde la condicion
            condicionComprobar = ejemplo.getValor(columnaComprobar);

            if (!condicionActual.esIgual(condicionComprobar)) { //Si la condicion y el valor de la muestra no coinciden
                return false;
            }
        }

        if (!(ejemplo.getClase()).esIgual(clase)) { //Sino coinciden las clases
            return false;
        }

        return true;
    }

    /**
     * Funcion que indica si en la muestra se encuentran todas las condiciones de la regla
     * @param mt Muestra Muestra donde se buscaran las condiciones de la regla
     * @return boolean Indica si estan todos los atributos(true) o no (false)
     */
    public boolean estanAtributosEn(Muestra mt) {
        Atributo at;

        for (int i = 0; i < condiciones.size(); i++) {
            at = (Atributo) condiciones.get(i);
            if (!mt.estaAtributo(at)) { //Sino esta el atributo directamente false y se termina
                return false;
            }
        }

        return true; //Si termina el bucle sin entrar en el if se devuelve true, en caso contrario false
    }

    /**
     * Funcion que devuelve la clase que predice la regla
     * @return Atributo Clase predicha por la regla
     */
    public Atributo obtenerReglaPredicha() {
        return clase;
    }

    /**
     * Funcion que devuelve un vector con todas las condiciones que se siguen en la regla
     * @return Vector Lista de las condiciones.
     */
    public Vector listaCondiciones() {
        Vector devolver;
        devolver = new Vector(condiciones);
        return devolver;
    }

    /**
     * Funcion que devuelve la calidad de la regla
     * @return double Double con la calidad que tiene la regla
     */
    public float obtenerCalidad() {
        return calidad;
    }

    /**
     * Modulo que imprime la regla
     */
    public void imprime() {
        int tamanio;
        Atributo at;
        int tipo;
        int i = 0;
        String valor;
        Attribute[] nombres;
        Attribute actual;
        nombres = Attributes.getInputAttributes();
        tamanio = condiciones.size();
        for (i = 0; i < tamanio - 1; i++) {
            at = (Atributo) condiciones.get(i);
            tipo = at.getAtributo();
            actual = nombres[tipo];
            valor = at.getValor();
            System.out.print("< " + actual.getName() + " = " + valor + " >" +
                             " AND ");
        }

        at = (Atributo) condiciones.get(i);
        tipo = at.getAtributo();
        actual = nombres[tipo];
        valor = at.getValor();
        System.out.print("< " + actual.getName() + "= " + valor + " >" +
                         " ==> ");

        valor = clase.getValor();
        System.out.print(valor);
        System.out.println("\t (" + calidad + " )");

    }

    /**
     * Modulo que imprime en el flujo que se le pasa la regla
     * @param salida Fichero donde se imprimira la regla
     */
    public void imprimeFichero(PrintStream salida) {
        int tamanio;
        Atributo at;
        int tipo;
        int i = 0;
        String valor;
        Attribute[] nombres;
        Attribute actual;
        nombres = Attributes.getInputAttributes();

        tamanio = condiciones.size();
        for (i = 0; i < tamanio - 1; i++) {
            at = (Atributo) condiciones.get(i);
            tipo = at.getAtributo();
            actual = nombres[tipo];
            valor = at.getValor();
            salida.print("< " + actual.getName() + " = " + valor + " >" +
                         " AND ");
        }

        at = (Atributo) condiciones.get(i);
        tipo = at.getAtributo();
        actual = nombres[tipo];
        valor = at.getValor();
        salida.print("< " + actual.getName() + "= " + valor + " >" +
                     " ==> ");

        valor = clase.getValor();
        salida.print(valor);
    }


    /**
     * Funcion que devuelve el numero de condiciones que contiene la regla
     * @return int Numero de condiciones de la regla
     */
    public int obtenerNumCondiciones() {
        int devolver;
        devolver = condiciones.size();
        return devolver;
    }

    /**
     * Modulo que inserta las condiciones que se pasan por parametro en la regla
     * @param atributos Vector Vector con las condiciones a insertar
     */
    public void insertaAtributos(Vector atributos) {
        condiciones = new Vector(atributos);
    }

    /**
     * Modulo que elimina la condicion que se pasa por parametro de la regla
     * @param condicion Atributo Condicion a eliminar de la regla.
     */
    public void eliminaCondicion(Atributo condicion) {
        condiciones.remove(condicion);
    }

    /**
     * Funcion que indica si dos reglas son iguales o no.
     * @param regla Regla Regla a comparar con la actual.
     * @return boolean Booleano que indicia si son iguales (true) o no (false)
     */
    public boolean esIgual(Regla regla) {
        if (regla == null) {
            return false;
        }
        ComparadorAtributo c = Atributo.obtenerComparador();
        Collections.sort(regla.condiciones, c);
        Collections.sort(condiciones, c);
        if (condiciones.equals(regla.condiciones)) {
            if (clase.esIgual(regla.clase)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Funcion que devuelve un comparador de reglas.
     * @return ComparadorRegla Comparador de reglas
     */
    public static ComparadorRegla obtenerComparador() {
        return c;
    }

    public void copia(Regla regla) {
        condiciones = new Vector(regla.condiciones);
        clase = new Atributo(regla.clase);
        calidad = regla.calidad;
        muestrasCubiertas = regla.muestrasCubiertas;
    }

    public float obtenerMuestrasCubiertas() {
        return muestrasCubiertas;
    }
}

