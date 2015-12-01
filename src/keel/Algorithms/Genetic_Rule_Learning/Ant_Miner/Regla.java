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
 * <p>Title: Regla (Rule)</p>
 * <p>Description:Rule class.
 *    Represents a rule extracted by the ACO algorithm. </p>
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
   *  Default constructor. An empty rule is built.
   */
    public Regla() {

        condiciones = new Vector();
        clase = new Atributo();
        c = new ComparadorRegla();
        muestrasCubiertas = 0;
        calidad = 0;

    }

  /**
   * Copy Constructor. Creates a new rule object by copying the one given as argument.
   * 
   * @param regla rule to be copied.
   */
    public Regla(Regla regla) {
        condiciones = new Vector(regla.condiciones);
        clase = new Atributo(regla.clase);
        muestrasCubiertas = regla.muestrasCubiertas;
        calidad = regla.calidad;
        muestrasCubiertas = regla.muestrasCubiertas;
    }

  /**
   *  Parameter Constructor.
   *  Creates a new rule with the given attribute values and class passed as parameters.
   * @param conjuntoAtributos Atributes values Vector
   * @param claseOriginal String class to set.
   */
    public Regla(Vector conjuntoAtributos, Atributo claseOriginal) {

        condiciones = new Vector(conjuntoAtributos);
        clase = claseOriginal;
        c = new ComparadorRegla();

    }

  /**
   * Adds a condition (an attribute with its value and an operator).
   *
   * @param original {@link Condition} condition to add.
   */
    public void insertarAtributo(Atributo original) {
        condiciones.addElement(original);
    }

    /**
     * Adds a class that identifies the rule with all its attributes.
     *
     * @param original {@link Atributo} class added to the rule.
     */
    public void insertarClase(Atributo original) {
        clase = original;
    }


  /**
   * Sorts the attributs in increasing order.
   *
   */
    public void ordenaAtributos() {
        ComparadorAtributo c = Atributo.obtenerComparador();
        Collections.sort(condiciones, c);
    }

  /**
   * Assigns the given quality to the rule.
   *
   * @param original double Quality to assign.
   */
    public void asignarCalidad(float original) {
        calidad = original;
    }


  /**
   * Sets the number of examples covered by the rule.
   * @param numero int number of covered examples to be set.
   */
    public void asignarMuestrasCubiertas(float numero) {
        muestrasCubiertas = numero;
    }

    /**
     * Checks if the given attribute/condition is already in the example.
     *
     * @param original given attribute to be checked.
     * @return True if the given attribute/condition is already in the example, false otherwise. 
     */
    public boolean estaAtributo(Atributo original) {
        boolean devolver = condiciones.contains(original);
        return devolver;
    }


    /**
     * Checks if the given attribute is already in the rule with a value.
     *
     * @param atributo given attribute to be checked.
     * @return True if the given attribute is already in the example, false otherwise. 
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
   * Returns the predicted class of the given sample it they have compatible conditions,
   * null otherwise.
   * @param mt {@link Muestra} given sample.
   * @return the predicted class or null.
   */
    public Atributo prediccion(Muestra mt) {
        Atributo clasePredicha = null;
        if (estanAtributosEn(mt)) {
            clasePredicha = clase;
        }
        return clasePredicha;
    }

   /**
   * Checks if the given example is covered by the whole rule 
   * (covered by the conditions and with the same class of the rule).
   * If it is correctly covered by the rule.
   * @param ejemplo given example to check.
   * @return True if it is covered, false otherwise.
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
   * Checks if in the sample, all the rule conditions/attributes are found.
   * @param mt Sample to check.
   * @return True if in the sample, all the rule conditions are found.
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
   * Returns the predicted class for the rule.
   * @return the predicted class for the rule.
   */
    public Atributo obtenerReglaPredicha() {
        return clase;
    }

  /**
   * Returns the list of conditions of the rule.
   * @return Vector the list of conditions of the rule.
   */
    public Vector listaCondiciones() {
        Vector devolver;
        devolver = new Vector(condiciones);
        return devolver;
    }

  /**
   * Returns the quality of the rule.
   * @return the quality of the rule.
   */
    public float obtenerCalidad() {
        return calidad;
    }

  /**
   * Prints on the standard output the rule.
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
   * Prints on the given file (PrintStream) the rule.
   * @param salida given file (PrintStream). 
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
   * Returns the number of conditions in the rule.
   * @return int the number of conditions in the rule.
   */
    public int obtenerNumCondiciones() {
        int devolver;
        devolver = condiciones.size();
        return devolver;
    }

  /**
   * Adds all the conditions given.
   * @param atributos Vector of conditions to add.
   */
    public void insertaAtributos(Vector atributos) {
        condiciones = new Vector(atributos);
    }

  /**
   * Removes the condition passed as parameter.
   * @param condicion condition to be removed.
   */
    public void eliminaCondicion(Atributo condicion) {
        condiciones.remove(condicion);
    }

  /**
   * Checks if the rule is equal to the given one.
   * @param regla Rule to compare with.
   * @return True if the rules are equal.
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
   * Returns the rules comparative method.
   * @return the rules comparative method.
   */
    public static ComparadorRegla obtenerComparador() {
        return c;
    }

      /** Copies the rule given as argument.
   * 
   * @param regla rule to be copied.
   */
    public void copia(Regla regla) {
        condiciones = new Vector(regla.condiciones);
        clase = new Atributo(regla.clase);
        calidad = regla.calidad;
        muestrasCubiertas = regla.muestrasCubiertas;
    }

      /**
   * Returns the number of covered samples.
   * @return the number of covered samples.
   */
    public float obtenerMuestrasCubiertas() {
        return muestrasCubiertas;
    }
}

