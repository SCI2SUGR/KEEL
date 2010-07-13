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
import java.io.PrintStream;
import keel.Dataset.*;

/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class Regla {

    private Vector cNominales; //Atributos de la regla
    private Vector cContinuos;
    private Atributo clase; //Clase de la regla
    private float calidad; //Calidad de la regla a la hora de clasificar
    private static ComparadorRegla c;
    private int numeroMuestrasCubiertas;


    /**
     * Constructor por defecto
     *
     * Crea una regla vacia, sin atributos y sin clase definida
     */
    public Regla() {

        cNominales = new Vector();
        cContinuos = new Vector();
        clase = new Atributo();
        c = new ComparadorRegla();
        calidad = 0;
        numeroMuestrasCubiertas = 0;

    }

    /**
     * Constructor de copia.
     * Crea una regla a partir de la que se le pasa por parametro.
     * @param regla Regla Regla a copiar en la actual
     */
    public Regla(Regla regla) {
        c = new ComparadorRegla();
        cNominales = new Vector(regla.cNominales);
        cContinuos = new Vector(regla.cContinuos);
        clase = new Atributo(regla.clase);
        calidad = regla.calidad;
        numeroMuestrasCubiertas = regla.numeroMuestrasCubiertas;
    }

    /**
     * Constructor
     * @param continuos Condiciones continuas
     * @param nominales Condiciones nominales
     * @param claseOriginal Clase para la regla
     */
    public Regla(Vector continuos, Vector nominales, Atributo claseOriginal) {

        cContinuos = new Vector(continuos);
        cNominales = new Vector(nominales);
        clase = claseOriginal;
        c = new ComparadorRegla();
        numeroMuestrasCubiertas = 0;

    }

    /**
     * Modulo que inserta un atributo junto con su valor en la regla
     *
     * @param original Atributo Atributo que se inserta en la regla.
     */
    public void insertarCondicionNominal(Condicion original) {
        cNominales.addElement(original);
    }

    /**
     * Modulo que inserta un atributo junto con su valor en la regla
     *
     * @param original Atributo Atributo que se inserta en la regla.
     */
    public void insertarCondicionContinua(Condicion original) {
        cContinuos.addElement(original);
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
    public void ordenaCondiciones() {
        ComparadorCondicion c = Condicion.getComparadorCondiciones();
        Collections.sort(cNominales, c);
        Collections.sort(cContinuos, c);
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
     * Funcion que indica si la regla cubre la muestra que se le pasa por argumento
     * @param ejemplo Muestra Muestra a comprobar si se cubre con la regla o no.
     * @return boolean Boolean que indica si se cubre la muestra (true) o false en caso contrario
     */

    public boolean cubreMuestraTotal(Muestra ejemplo) {

        int numCondicionesNominales = cNominales.size();
        int numCondicionesContinuos = cContinuos.size();
        int columnaComprobar;
        Condicion condicionActual;
        Atributo condicionComprobar;

        for (int i = 0; i < numCondicionesNominales; i++) {
            condicionActual = (Condicion) cNominales.get(i); //Cogemos una condicion de la regla
            columnaComprobar = condicionActual.getIndice(); //Miramos a que atributo corresponde la condicion
            condicionComprobar = ejemplo.getValor(columnaComprobar);

            if (!condicionActual.cubre(condicionComprobar)) { //Si la condicion y el valor de la muestra no coinciden
                return false;
            }
        }

        for (int i = 0; i < numCondicionesContinuos; i++) {

            condicionActual = (Condicion) cContinuos.get(i); //Cogemos una condicion de la regla
            columnaComprobar = condicionActual.getIndice(); //Miramos a que atributo corresponde la condicion
            condicionComprobar = ejemplo.getValor(columnaComprobar);

            if (!condicionActual.cubre(condicionComprobar)) { //Si la condicion y el valor de la muestra no coinciden
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
     * @param ejemplo Muestra Muestra donde se buscaran las condiciones de la regla
     * @return boolean Indica si estan todos los atributos(true) o no (false)
     */
    public boolean estanCondicionesEn(Muestra ejemplo) {

        int numCondicionesNominales = cNominales.size();
        int numCondicionesContinuos = cContinuos.size();
        int columnaComprobar;
        Condicion condicionActual;
        Atributo condicionComprobar;

        for (int i = 0; i < numCondicionesNominales; i++) {

            condicionActual = (Condicion) cNominales.get(i); //Cogemos una condicion de la regla
            columnaComprobar = condicionActual.getIndice(); //Miramos a que atributo corresponde la condicion
            condicionComprobar = ejemplo.getValor(columnaComprobar);
            if (!condicionActual.cubre(condicionComprobar)) { //Si la condicion y el valor de la muestra no coinciden
                return false;
            }
        }

        for (int i = 0; i < numCondicionesContinuos; i++) {
            condicionActual = (Condicion) cContinuos.get(i); //Cogemos una condicion de la regla
            columnaComprobar = condicionActual.getIndice(); //Miramos a que atributo corresponde la condicion
            condicionComprobar = ejemplo.getValor(columnaComprobar);

            if (!condicionActual.cubre(condicionComprobar)) { //Si la condicion y el valor de la muestra no coinciden
                return false;
            }
        }

        return true;

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
    public Vector listaCondicionesNominales() {
        Vector devolver;
        devolver = new Vector(cNominales);
        return devolver;
    }

    /**
     * Funcion que devuelvo un vector con las condiciones continuas de la regla
     * @return Vector con las condiciones
     */

    public Vector listaCondicionesContinuos() {
        Vector devolver;
        devolver = new Vector(cContinuos);
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
     * Funcion que devuelve el numero de condiciones que contiene la regla
     * @return int Numero de condiciones de la regla
     */
    public int obtenerNumCondicionesContinuas() {
        int devolver;
        devolver = cContinuos.size();
        return devolver;
    }

    /**
     * Funcion que devuelve el numero de condiciones nominales de la regla
     * @return Numero de condiciones nominales de la regla
     */
    public int obtenerNumCondicionesNominales() {
        int devolver;
        devolver = cNominales.size();
        return devolver;
    }


    /**
     * Modulo que inserta las condiciones que se pasan por parametro en la regla
     * @param atributos Vector Vector con las condiciones a insertar
     */
    public void insertaCondicionesNominales(Vector atributos) {
        cNominales = new Vector(atributos);
    }

    /**
     * Modulo que inserrta condiciones continuas en la regla
     * @param atributos Condiciones a insertar en la regla
     */
    public void insertaCondicionesContinuos(Vector atributos) {
        cContinuos = new Vector(atributos);
    }


    /**
     * Modulo que elimina la condicion que se pasa por parametro de la regla
     * @param condicion Atributo Condicion a eliminar de la regla.
     */
    public void eliminaCondicionNominal(Condicion condicion) {
        cNominales.remove(condicion);
    }

    /**
     * Modulo que elimina la condicion continua de la regla que se pasa por parametro
     * @param condicion  Condicion a eliminar
     */
    public void eliminaCondicionContinuos(Condicion condicion) {
        cContinuos.remove(condicion);
    }


    /**
     * Funcion que devuelve un comparador de reglas.
     * @return ComparadorRegla Comparador de reglas
     */
    public static ComparadorRegla obtenerComparador() {
        return c;
    }

    /**
     * Modulo que copia en la regla los valores de la pasada por argumento
     * @param regla Regla a copiar
     */
    public void copia(Regla regla) {
        Condicion copia;
        Condicion original;
        cNominales.clear();
        cContinuos.clear();

        for (int i = 0; i < regla.cNominales.size(); i++) {
            original = (Condicion) regla.cNominales.get(i);
            copia = new Condicion(original);
            cNominales.addElement(copia);
        }

        for (int i = 0; i < regla.cContinuos.size(); i++) {
            original = (Condicion) regla.cContinuos.get(i);
            copia = new Condicion(original);
            cContinuos.addElement(copia);
        }
        clase = new Atributo(regla.clase);
        calidad = regla.calidad;

    }


    /**
     * Modulo que elimina la condicion nominal del indice sustituyendola por la pasada por parametro
     * @param indice Condicion a sustituir
     * @param nueva Condicion nueva
     */
    public void eliminaCondicionNominal(int indice, Condicion nueva) {
        cNominales.remove(indice);
        cNominales.insertElementAt(nueva, indice);
    }

    /**
     * Modulo que elimina la condicion nominal del indice sustituyendola por la pasada por parametro
     * @param indice Condicion a sustituir
     * @param nueva Condicion nueva
     */

    public void eliminaCondicionContinuos(int indice, Condicion nueva) {
        cContinuos.remove(indice);
        cContinuos.insertElementAt(nueva, indice);
    }

    /**
     * Modulo que elimina la ultima condicion nominal de la regla
     */
    public void eliminaUltimaCondicionNominal() {
        cNominales.remove(cNominales.size() - 1);
    }


    /**
     * Modulo que elimina la ultima condicion continua de la regla
     */
    public void eliminaUltimaCondicionContinua() {
        cContinuos.remove(cContinuos.size() - 1);
    }


    /**
     * Funcion que devuelve la condicion nominal del indice
     * @param indice Posicion de la condicion nominal a devolver
     * @return Condicion Nominal de la regla
     */
    public Condicion getCondicionNominal(int indice) {
        Condicion devolver;
        devolver = (Condicion) cNominales.get(indice);
        return devolver;
    }

    /**
     * Funcion que devuelve la condicion continua del indice
     * @param indice Posicion de la condicion continua a devolver
     * @return Condicion continua de la regla
     */

    public Condicion getCondicionContinua(int indice) {
        Condicion devolver;
        devolver = (Condicion) cContinuos.get(indice);
        return devolver;
    }

    /**
     * Funcion que inicializa una condicion a un valor pasado por parametro
     * @param indice Posicion de la condicion
     * @param co Valor
     */


    public void setCondicionNominal(int indice, Condicion co) {
        cNominales.set(indice, co);
    }

    /**
     * Funcion que inicializa una condicion a un valor pasado por parametro
     * @param indice Posicion de la condicion
     * @param co Valor
     */

    public void setCondicionContinua(int indice, Condicion co) {
        cContinuos.set(indice, co);
    }

    /**
     * Modulo que elimina las condiciones nominales de la regla
     */
    public void eliminaCondicionesNominales() {
        cNominales = new Vector();
    }

    /**
     * Modulo que imprime por pantalla la regla
     * @param numCondiciones Numero de condiciones nominales
     */

    public void imprime(int numCondiciones) {
        Attribute[] listaAtributos;
        listaAtributos = Attributes.getInputAttributes();
        Attribute actual;
        Vector nombres;
        Vector nombresClases;
        String nombre;
        float valor;
        float valor2;
        int i;
        int indice;
        Condicion co;

        //Nominales
        for (i = 0; i < cNominales.size() - 1; i++) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            System.out.print(nombre + " = < " + valor + " > AND ");
        }

        if (numCondiciones > 0 && i > 0) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            System.out.print(nombre + " = < " + valor + " > AND ");
        }

        if (numCondiciones == 0 && i > 0) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            System.out.print(nombre + " = < " + valor + " >");

        }

        //Despues las continuas
        i = 0;

        while (i < cContinuos.size() - 2 && numCondiciones > 0) {
            co = (Condicion) cContinuos.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombre = actual.getName();
            valor = co.getValor().getValor();
            i++;
            co = (Condicion) cContinuos.get(i);
            valor2 = co.getValor().getValor();
            i++;

            System.out.print(nombre + " = [ " + valor + " , " + valor2 +
                             " ] AND ");
        }

        if (numCondiciones > 0) {
            co = (Condicion) cContinuos.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombre = actual.getName();
            valor = co.getValor().getValor();
            i++;
            co = (Condicion) cContinuos.get(i);
            valor2 = co.getValor().getValor();
            i++;

            System.out.print(nombre + " = [ " + valor + " , " + valor2 + " ]");

        }

        //Y por ultimo la clase
        valor = clase.getValor();
        listaAtributos = Attributes.getOutputAttributes();
        actual = listaAtributos[0];
        nombresClases = actual.getNominalValuesList();
        nombre = (String) nombresClases.get((int) valor);

        System.out.print(" ==> " + nombre);
        System.out.println("   ( " + calidad + " )");

    }


    /**
     * Modulo que imprime en un fichero la regla
     * @param numCondiciones Numero de condiciones continuas de la regla
     * @param salida Fichero donde imprimir la regla
     */

    public void imprimeFichero(int numCondiciones, PrintStream salida) {
        Attribute[] listaAtributos;
        listaAtributos = Attributes.getInputAttributes();
        Attribute actual;
        Vector nombres;
        Vector nombresClases;
        String nombre;
        float valor;
        float valor2;
        int i;
        int indice;
        Condicion co;

        //Nominales
        for (i = 0; i < cNominales.size() - 1; i++) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            salida.print(nombre + " = < " + valor + " > AND ");
        }

        if (numCondiciones > 0 && i > 0) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            salida.print(nombre + " = < " + valor + " > AND ");
        }

        if (numCondiciones == 0 && i > 0) {
            co = (Condicion) cNominales.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombres = actual.getNominalValuesList();
            nombre = actual.getName();
            valor = co.getValor().getValor();
            salida.print(nombre + " = < " + valor + " >");

        }

        //Despues las continuas
        i = 0;

        while (i < cContinuos.size() - 2 && numCondiciones > 0) {
            co = (Condicion) cContinuos.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombre = actual.getName();
            valor = co.getValor().getValor();
            i++;
            co = (Condicion) cContinuos.get(i);
            valor2 = co.getValor().getValor();
            i++;

            salida.print(nombre + " = [ " + valor + " , " + valor2 + " ] AND ");
        }

        if (numCondiciones > 0) {
            co = (Condicion) cContinuos.get(i);
            indice = co.getIndice();
            actual = listaAtributos[indice];
            nombre = actual.getName();
            valor = co.getValor().getValor();
            i++;
            co = (Condicion) cContinuos.get(i);
            valor2 = co.getValor().getValor();
            i++;

            salida.print(nombre + " = [ " + valor + " , " + valor2 + " ]");

        }

        //Y por ultimo la clase
        valor = clase.getValor();
        listaAtributos = Attributes.getOutputAttributes();
        actual = listaAtributos[0];
        nombresClases = actual.getNominalValuesList();
        nombre = (String) nombresClases.get((int) valor);

        salida.print(" ==> " + nombre);
        salida.println("   ( " + calidad + " )");

    }


    /**
     * Funcion que devuelve la prediccion de la regla para la muestra
     * @param mt Muestra sobre la que se comprueba la regla
     * @return Null en caso de no cubrir la regla, la clase en caso contrario
     */
    public Atributo prediccion(Muestra mt) {
        if (estanCondicionesEn(mt)) {
            return clase;
        } else {
            return null;
        }
    }


    /**
     * Modulo que elimina las condiciones nominales de la regla
     */
    public void limpiaCNominales() {
        cNominales = new Vector();
    }

    /**
     * Modulo que asigna las muestras cubiertas por la regla
     * @param num Numero de muestras cubiertas
     */
    public void asignarNumeroMuestrasCubiertas(int num) {
        numeroMuestrasCubiertas = num;
    }

    /**
     * Funcion que devuekve el numero de muestras cubiertas por la regla
     * @return Numero de muestras cubiertas
     */
    public int obtenerNumeroMuestrasCubiertas() {
        return numeroMuestrasCubiertas;
    }

}


