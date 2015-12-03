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
 * <p> Set of rules. Defines a set of rules or complex
 * @author  Alberto Fernández Hilario
 * @version 1.1
 * @since JDK1.2
 * </p>
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
     * <p>
     * Add a rule to the list
     * </p>
     * @param regl Rule to add
     */
    public void addRegla(Regla regl) {
        reglas.add(regl);
    }

    /**
     * <p>
     * Removes a rule from the list
     * </p>
     * @param i index of the rule to remove
     */
    public void deleteRegla(int i) {
        reglas.remove(i);
    }

    /**
     * <p>
     * Returns a rule of the list
     * </p>
     * @param i index of the rule
     * @return the 'i' rule
     */
    public Regla getRegla(int i) {
        return (Regla) reglas.get(i);
    }

    /**
     * <p>
     * Returns a rule as a copy of the list
     * </p>
     * @param i index of the rule
     * @return the 'i' rule as a copy
     */
    public Regla getNuevaRegla(int i) {
        Regla c = (Regla) reglas.get(i);
        Regla c2 = c.copiaRegla();
        return c2;
    }

    /**
     * <p>
     * Returns the number of rules we are working with
     * </p>
     * @return the size of the set of rules
     */
    public int size() {
        return (reglas.size());
    }

    /**
     * <p>
     * Returns the complet set if rules
     * </p>
     * @return the complet set of rules
     */
    public LinkedList getConjReglas() {
        return reglas;
    }

    /**
     * <p>
     * Do a copy of the complet set of the rules
     * </p>
     * @return The neew set of rules with a new copy
     *
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
     * <p>
     * Prints on the screen the set of rules
     * </p>
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
     * <p>
     * Prints on a string the set of rules
     * </p>
     * @return A strign that stores the set of rules
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
     * <p>
     * Returns the last rule(normally the one with best weight)
     * </p>
     * @return the last rule of the list
     */
    public Regla getUltimaRegla() {
        return (Regla) reglas.getLast();
    }
    
        /**
     * <p>
     * Do a local copy of the values of the rules.
     * </p>
     * @param cambio int[] vector with the new values. 
     */
    public void ajusta(int[] cambio) {
        for (int i = 0; i < reglas.size(); i++) {
            ((Regla) reglas.get(i)).ajusta(cambio);
        }
    }

    /**
     * <p>
     * Do a clocal copy of the name of the class variable
     * </p>
     * @param nombreClase Name of the class
     */
    public void adjuntaNombreClase(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    /**
     * <p>
     * Do a local copy of the name of the values of the class
     * </p>
     * @param clases String[] An array that stores the name of the value of the class
     */
    public void adjuntaNombreClases(String[] clases) {
        valorNombreClases = new String[clases.length];
        for (int i = 0; i < clases.length; i++) {
            valorNombreClases[i] = clases[i];
        }
    }

}

