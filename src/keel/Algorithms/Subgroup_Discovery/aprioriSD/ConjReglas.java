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

package keel.Algorithms.Subgroup_Discovery.aprioriSD;

import java.util.*;

/**
 * <p>Título: Conjunto de reglas</p>
 * <p>Descripción: Estructura para almacenar un conjunto completo de reglas</p>
 * <p>Creado: 23-feb-2006 </p>
 * @author Alberto Fernández Hilario
 * @version 1.0
 */
public class ConjReglas {

    private LinkedList reglas;
    private String nombreClase;
    private String[] valorNombreClases;

    /**
     * Constructor
     */
    public ConjReglas() {
        super();
        reglas = new LinkedList();
    }

    /**
     * Añade una regla a la lista
     * @param regl Regla a añadir
     */
    public void addRegla(Regla regl) {
        reglas.add(regl);
    }

    /**
     * Elimina una regla de la lista
     * @param i indice de la regla a eliminar
     */
    public void deleteRegla(int i) {
        reglas.remove(i);
    }

    /**
     * Devuelve una regla de la lista
     * @param i indice de la regla
     * @return la regla i-esima
     */
    public Regla getRegla(int i) {
        return (Regla) reglas.get(i);
    }

    /**
     * Devuelve la regla como una nueva copia
     * @param i regla i-esima
     * @return una regla de la lista como una nueva copia
     */
    public Regla getNuevaRegla(int i) {
        Regla c = (Regla) reglas.get(i);
        Regla c2 = c.copiaRegla();
        return c2;
    }

    /**
     * Devuelve el numero de reglas con las que estamos trabajando
     * @return El tamaño del conjunto de reglas
     */
    public int size() {
        return (reglas.size());
    }

    /**
     * Devuelve el conjunto completo de reglas
     * @return el conjunto completo de reglas
     */
    public LinkedList getConjReglas() {
        return reglas;
    }

    /**
     * Realiza una copia del conjunto completo de reglas
     * @return el conjunto completo de reglas como una nueva copia
     */
    public ConjReglas copiaConjReglas() {
        int i;
        ConjReglas c = new ConjReglas();

        for (i = 0; i < reglas.size(); i++) {
            Regla comp = (Regla) reglas.get(i);
            c.addRegla(comp.copiaRegla());
        }

        return c;
    }

    /**
     * Muestra por pantalla el conjunto de reglas
     */
    public void print() {
        for (int i = 0; i < reglas.size(); i++) {
            Regla c = (Regla) reglas.get(i);
            System.out.print("\nRule " + (i + 1) + ": IF  ");
            c.print();
            System.out.print(" THEN " + nombreClase + " -> " + c.getClase() +
                             "  ");
            System.out.print("-- Support: " + c.getSupport());

            c.printDistribucion();
        }
    }

    /**
     * Imprime en una cadena el conjunto de reglas
     * @return una cadena de texto (string) que almacena el conjunto de reglas
     */
    public String printString() {
        int i;
        String cad = "";
        for (i = 0; i < reglas.size(); i++) {
            Regla c = (Regla) reglas.get(i);
            cad += "\nRule " + (i + 1) + ": IF  ";
            cad += c.printString();
            cad += " THEN " + nombreClase + " -> " +
                    valorNombreClases[c.getClase()] + "  ";
            cad += c.printSupport();
            cad += c.printDistribucionString();
        }
        return cad;
    }

    /**
     * Devuelve la última regla (normalmente aquella con mejor peso)
     * @return la última regla de a lista
     */
    public Regla getUltimaRegla() {
        return (Regla) reglas.getLast();
    }

    /**
     * Realiza un ajuste de valores para todas las reglas del conjunto
     * @param cambio int[] Un vector con los nuevos valores (cambio[0] -> nuevo valor para el valor 0...)
     */
    public void ajusta(int[] cambio) {
        for (int i = 0; i < reglas.size(); i++) {
            ((Regla) reglas.get(i)).ajusta(cambio);
        }
    }

    /**
     * Realizamos una copia local del nombre de la variable clase
     * @param nombreClase String nombre de la clase
     */
    public void adjuntaNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    /**
     * Realizamos una copia local del nombre de las valores de la clase
     * @param clases String[] un Array que guarda el nombre de valor de la clase
     */
    public void adjuntaNombreClases(String[] clases) {
        valorNombreClases = new String[clases.length];
        for (int i = 0; i < clases.length; i++) {
            valorNombreClases[i] = clases[i];
        }
    }

}

