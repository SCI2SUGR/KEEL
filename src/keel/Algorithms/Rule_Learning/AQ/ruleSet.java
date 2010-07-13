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

package keel.Algorithms.Rule_Learning.AQ;

import java.util.*;

/**
 * <p>Title: Rule Set</p>
 * <p>Description: Structure to store a complete rule set</p>
 * @author Written by José Ramón Cano de Amo (University of Jaen) 07/31/2004
 * @author Modified by Alberto Fernández (University of Granada) 11/29/2004
 * @version 1.1
 * @since JDK1.4
 */
public class ruleSet {

    private LinkedList reglas;
    private String nombreClase;
    private String[] valorNombreClases;

    /**
     * Builder
     */
    public ruleSet() {
        super();
        reglas = new LinkedList();
    }

    /**
     * It adds one rule to the list
     * @param regl Rule to add
     */
    public void addRule(Complex regl) {
        reglas.add(regl);
    }

    /**
     * It removes one rule of the list
     * @param i index of the rule to remove
     */
    public void deleteRule(int i) {
        reglas.remove(i);
    }

    /**
     * Devuelve una regla de la lista
     * @param i indice de la regla
     * @return la regla i-esima
     */
    public Complex getRule(int i) {
        return (Complex) reglas.get(i);
    }

    /**
     * It returns the rule as a new copy
     * @param i i-th rule
     * @return a cloned rule
     */
    public Complex getNewRule(int i) {
        Complex c = (Complex) reglas.get(i);
        Complex c2 = new Complex(c.getSelector(0), c.getNclasses());
        for (int j = 1; j < c.size(); j++) {
            c2.addSelector(c.getSelector(j));
        }
        return c2;
    }

    /**
     * It returns the size of the rule set
     * @return the size of the rule set
     */
    public int size() {
        return (reglas.size());
    }

    /**
     * It returns the complete rule-set
     * @return the complete rule-set
     */
    public LinkedList getruleSet() {
        return reglas;
    }

    /**
     * It carries out a copy of the full rule-set
     * @return a cloned rule-set
     */
    public ruleSet copyRuleSet() {
        int i;
        ruleSet c = new ruleSet();

        for (i = 0; i < reglas.size(); i++) {
            Complex comp = (Complex) reglas.get(i);
            c.addRule(comp.copyRule());
        }

        return c;
    }

    /**
     * It prints the rule-set
     */
    public void print() {
        for (int i = 0; i < reglas.size(); i++) {
            Complex c = (Complex) reglas.get(i);
            System.out.print("\nRule " + (i + 1) + ": IF  ");
            c.print();
            System.out.print(" THEN " + nombreClase + " -> " +
                             valorNombreClases[c.getClas()] + "  ");
            c.printDistribution();
        }
    }

    /**
     * It prints on a string the rule-set
     * @return a text string with the rule set
     */
    public String printString() {
        String cad = "";
        for (int i = 0; i < reglas.size(); i++) {
            Complex c = (Complex) reglas.get(i);
            cad += "\nRule " + (i + 1) + ": IF  ";
            cad += c.printString();
            cad += " THEN " + nombreClase + " -> " +
                    valorNombreClases[c.getClas()] + "  ";
            cad += c.printDistributionString();
        }
        return cad;
    }

    /**
     * It returns the last rule (normally, the one with best weight)
     * @return the last rule of the list
     */
    public Complex getLastRule() {
        return (Complex) reglas.getLast();
    }

    /**
     * It removes rules that are semantically equal
     * @param size int Star size
     */
    public void deleteSubsumed(int size) {
        for (int i = 0; (i < size - 1) && (i < this.size()); i++) {
            Complex aux = this.getRule(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size()) && (seguir); j++) {
                Complex aux2 = this.getRule(j);
                seguir = false;
                if (aux.almostSame(aux2)) {
                    Selector s = aux.getSelector(aux.size() - 1);
                    Selector s2 = aux2.getSelector(aux2.size() - 1);
                    int sub = s.subsumido(s2);
                    if (sub != 0) {
                        if (sub == -1) {
                            this.deleteRule(i);
                            i--;
                            seguir = false;
                            /*System.out.println(
                                    "\n(" + sub +
                                    ") Estos son los Complexs subsumidos:");
                            aux.print();
                            aux2.print();
                            */
                        } else if ((sub == -2) && (aux.sameDistribution(aux2))) {
                            this.deleteRule(i);
                            i--;
                            seguir = false;
                            /*System.out.println(
                                    "\n(" + sub +
                                    ") Estos son los Complexs subsumidos:");
                            aux.print();
                            aux2.print();
                            */
                        } else if (sub == 1) {
                            this.deleteRule(j);
                            j--;
                            /*System.out.println(
                                    "\n(" + sub +
                                    ") Estos son los Complexs subsumidos:");
                            aux2.print();
                            aux.print();
                            */
                        }
                    }
                } else {
                    seguir = false;
                }
            }
        }
    }

    /**
     * It performs a local copy of the name of the output class
     * @param nombreClase String Class name
     */
    public void addClassName(String nombreClase) {
        this.nombreClase = nombreClase;
    }

    /**
     * It performs a local copy of the names of the output classes
     * @param clases String[] An array that stores the name of each class
     */
    public void addClassNames(String[] clases) {
        valorNombreClases = new String[clases.length];
        for (int i = 0; i < clases.length; i++) {
            valorNombreClases[i] = clases[i];
        }
    }

}

