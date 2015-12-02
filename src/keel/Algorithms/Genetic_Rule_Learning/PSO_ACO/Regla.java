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
 * <p>Title: Regla (Rule)</p>
 * <p>Description:Rule class.
 *    Represents a rule extracted by the PSO-ACO algorithm. </p>
 * @author Vicente Rubén del Pino Ruiz
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
   *  Default constructor. An empty rule is built.
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
   * Copy Constructor. Creates a new rule object by copying the one given as argument.
   * 
   * @param regla rule to be copied.
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
   *  Parameter Constructor.
   *  Creates a new rule with the given attribute values and class passed as parameters.
   * @param continuos Continuous Attributes values Vector
     * @param nominales Nominal Attributes values Vector
   * @param claseOriginal String class to set.
   */
    public Regla(Vector continuos, Vector nominales, Atributo claseOriginal) {

        cContinuos = new Vector(continuos);
        cNominales = new Vector(nominales);
        clase = claseOriginal;
        c = new ComparadorRegla();
        numeroMuestrasCubiertas = 0;

    }

  /**
   * Adds a condition (a nominal attribute with its value and an operator).
   *
   * @param original {@link Condition} condition to add.
   */
    public void insertarCondicionNominal(Condicion original) {
        cNominales.addElement(original);
    }

  /**
   * Adds a condition (a continuous attribute with its value and an operator).
   *
   * @param original {@link Condition} condition to add.
   */
    public void insertarCondicionContinua(Condicion original) {
        cContinuos.addElement(original);
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
   * Sorts the rules in increasing order.
   *
   */
    public void ordenaCondiciones() {
        ComparadorCondicion c = Condicion.getComparadorCondiciones();
        Collections.sort(cNominales, c);
        Collections.sort(cContinuos, c);
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
   * Checks if the given example is covered by the whole rule 
   * (covered by the conditions and with the same class of the rule).
   * If it is correctly covered by the rule.
   * @param ejemplo given example to check.
   * @return True if it is covered, false otherwise.
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
   * Checks if in the sample, all the rule conditions are found.
   * @param ejemplo Sample to check.
   * @return True if in the sample, all the rule conditions are found.
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
   * Returns the predicted class for the rule.
   * @return the predicted class for the rule.
   */
    public Atributo obtenerReglaPredicha() {
        return clase;
    }

  /**
   * Returns the list of nominal conditions of the rule.
   * @return Vector the list of nominal conditions of the rule.
   */
    public Vector listaCondicionesNominales() {
        Vector devolver;
        devolver = new Vector(cNominales);
        return devolver;
    }

  /**
   * Returns the list of continuous conditions of the rule.
   * @return Vector the list of continuous conditions of the rule.
   */
    public Vector listaCondicionesContinuos() {
        Vector devolver;
        devolver = new Vector(cContinuos);
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
   * Returns the number of continuous conditions of the rule.
   * @return  the number of continuous conditions of the rule.
   */
    public int obtenerNumCondicionesContinuas() {
        int devolver;
        devolver = cContinuos.size();
        return devolver;
    }

  /**
   * Returns the number of nominal conditions of the rule.
   * @return the number of nominal conditions of the rule.
   */
    public int obtenerNumCondicionesNominales() {
        int devolver;
        devolver = cNominales.size();
        return devolver;
    }


   /**
   * Adds all the nominal conditions given.
   * @param atributos Vector of conditions to add.
   */
    public void insertaCondicionesNominales(Vector atributos) {
        cNominales = new Vector(atributos);
    }

  /**
   * Adds all the continuous conditions given.
   * @param atributos Vector of conditions to add.
   */
    public void insertaCondicionesContinuos(Vector atributos) {
        cContinuos = new Vector(atributos);
    }


  /**
   * Removes the nominal condition passed as parameter.
   * @param condicion condition to be removed.
   */
    public void eliminaCondicionNominal(Condicion condicion) {
        cNominales.remove(condicion);
    }

  /**
   * Removes the continuous condition passed as parameter.
   * @param condicion condition to be removed.
   */
    public void eliminaCondicionContinuos(Condicion condicion) {
        cContinuos.remove(condicion);
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
   * Removes the nominal condition with the index given and adds a new one 
   * in that position.
   * @param indice given index where to remove and add.
   * @param nueva New condition to add.
   */
    public void eliminaCondicionNominal(int indice, Condicion nueva) {
        cNominales.remove(indice);
        cNominales.insertElementAt(nueva, indice);
    }

  /**
   * Removes the continuous condition with the index given and adds a new one 
   * in that position.
   * @param indice given index where to remove and add.
   * @param nueva New condition to add.
   */
    public void eliminaCondicionContinuos(int indice, Condicion nueva) {
        cContinuos.remove(indice);
        cContinuos.insertElementAt(nueva, indice);
    }

    /**
     * Removes the last nominal condition.
     */
    public void eliminaUltimaCondicionNominal() {
        cNominales.remove(cNominales.size() - 1);
    }


    /**
     * Removes the last continuous condition.
     */
    public void eliminaUltimaCondicionContinua() {
        cContinuos.remove(cContinuos.size() - 1);
    }


    /**
     * Returns the nominal condition with the given index.
     * @param indice given index.
     * @return the nominal condition with the given index.
     */
    public Condicion getCondicionNominal(int indice) {
        Condicion devolver;
        devolver = (Condicion) cNominales.get(indice);
        return devolver;
    }

    /**
     * Returns the continuous condition with the given index.
     * @param indice given index.
     * @return the continuous condition with the given index.
     */
    public Condicion getCondicionContinua(int indice) {
        Condicion devolver;
        devolver = (Condicion) cContinuos.get(indice);
        return devolver;
    }

    /**
     * Sets the given nominal condition into the position given.
     * @param indice given position.
     * @param co given condition.
     */
    public void setCondicionNominal(int indice, Condicion co) {
        cNominales.set(indice, co);
    }

    /**
     * Sets the given continuous condition into the position given.
     * @param indice given position.
     * @param co given condition.
     */
    public void setCondicionContinua(int indice, Condicion co) {
        cContinuos.set(indice, co);
    }

    /**
     * Removes all nominal conditions.
     */
    public void eliminaCondicionesNominales() {
        cNominales = new Vector();
    }

  /**
   * Prints on the standard output the rule.
   * @param numCondiciones number of nominal conditions.
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
   * Prints on the given file (PrintStream) the rule.
   * @param salida given file (PrintStream). 
   * @param numCondiciones number of continuous conditions.
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
   * Returns the predicted class of the given sample it they have compatible conditions,
   * null otherwise.
   * @param mt {@link Muestra} given sample.
   * @return the predicted class or null.
   */
    public Atributo prediccion(Muestra mt) {
        if (estanCondicionesEn(mt)) {
            return clase;
        } else {
            return null;
        }
    }


    /**
     * Removes all nominal conditions.
     */
    public void limpiaCNominales() {
        cNominales = new Vector();
    }

    /**
     * Assign the number of examples covered by the rule with the value given.
     * @param num number of covered examples to set.
     */
    public void asignarNumeroMuestrasCubiertas(int num) {
        numeroMuestrasCubiertas = num;
    }

    /**
     * Returns the number of examples covered by the rule. 
     * @return the number of examples covered by the rule.
     */
    public int obtenerNumeroMuestrasCubiertas() {
        return numeroMuestrasCubiertas;
    }

}


