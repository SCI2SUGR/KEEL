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

/**
 * <p>
 * @author Writed by Alberto Fernández (University of Granada) 15/01/2006
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 24/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

import java.util.*;

public class SetRules {

    /**
     * <p>
     * Defines a set of rules or complex
     * </p>
     */

    private LinkedList reglas;
    private String nombreClase;
    private String[] valorNombreClases;

    /**
     * <p>
     * Constructor
     * </p>
     */
    public SetRules() {
        reglas = new LinkedList();
    }

    /**
     * <p>
     * Add a set of rules to the list
     * </p>
     * @param r                  The set of rules to add
     */
    public void addReglas(SetRules r) {
        for (int i = 0; i < r.size(); i++) {
            Complex regla = r.getRule(i);
            reglas.add(regla);
        }
    }

    /**
     * <p>
     * Add a rule to the list
     * </p>
     * @param regl                   Complex to add
     */
    public void addRegla(Complex regl) {
        reglas.add(regl);
    }

    /**
     * <p>
     * Delete a rule of the list
     * </p>
     * @param i                       Number of complex to delete
     */
    public void deleteRegla(int i) {
        reglas.remove(i);
    }

    /**
     * <p>
     * Delete all the content of the list
     * </p>
     */
    public void deleteAll() {
        reglas.removeAll(reglas);
    }

    /**
     * <p>
     * Return a complex of the rule
     * </p>
     * @param i                       Index of the rule
     * @return                          A rule
     */
    public Complex getRule(int i) {
        return (Complex) reglas.get(i);
    }

    /**
     * <p>
     * Return the number of rules
     * </p>
     * @return                      Number of rules
     */
    public int size() {
        return (reglas.size());
    }

    /**
     * <p>
     * Return the set of rules
     * </p>
     * @return                  The set of rules
     */
    public LinkedList getConjReglas() {
        return reglas;
    }

    /**
     * <p>
     * Show the set of rules
     * </p>
     */
    public void print() {
        for (int i = 0; i < reglas.size(); i++) {
            Complex c = (Complex) reglas.get(i);
            System.out.print("\nRule " + (i+1) + ": IF  ");
            c.print();
            System.out.print(" THEN "+nombreClase+" -> " + c.getClass() + "  ");
            c.printDistrib();
        }
    }

    /**
     * <p>
     * Print string the set of rules
     * </p>
     * @return                  A string with the set or rules
     */
    public String printString() {
        String cad = "";
        for (int i = 0; i < reglas.size(); i++) {
            Complex c = (Complex) reglas.get(i);
            cad += "Rule " + (i+1) + ": IF  ";
            cad += c.printString();
            cad += " THEN "+nombreClase+ " -> " + valorNombreClases[c.getClas()] + "  ";
            cad += c.printDistribucionString();
            cad += "\n";
        }
        return cad;
    }

    /**
     * <p>
     * Return the last rule
     * </p>
     * @return                  The last rule
     */
    public Complex getUltimaRegla() {
        return (Complex) reglas.getLast();
    }

    /**
     * <p>
     * Delete rules with the same attributes
     * </p>
     */
    public void deleteNull() {
        boolean salir;
        for (int i = 0; i < this.size(); i++) {
            Complex aux = this.getRule(i);
            salir = false;
            for (int j = 0; (j < aux.size() - 1) && (!salir); j++) {
                Selector s = aux.getSelector(j);
                for (int h = j + 1; (h < aux.size()) && (!salir); h++) {
                    Selector s2 = aux.getSelector(h);
                    if (s.compareTo(s2) < 2) {
                        this.deleteRegla(i);
                        salir = true;
                        i--;
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Delete rules with the same complex
     * </p>
     * @param tam                   Size of the set of rules
     */
    public void deleteEqual(int tam) {

        for (int i = 0; i < tam; i++) {
            Complex aux = this.getRule(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size()) && (seguir); j++) {
                Complex aux2 = this.getRule(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if (s.compareTo(s2) == 0) {
                            salir = true;
                            if (l == aux.size() - 1) {
                                seguir = false;
                                this.deleteRegla(i);
                                i--;
                            }
                        }
                    }
                    parar = !salir;
                }
            }
        }
    }

    /**
     * <p>
     * Delete rules with equal semantics
     * </p>
     * @param tam                               Size of the set of rules
     */
    public void eliminaSubsumidos(int tam) {

        for (int i = 0; i < tam; i++) {
            Complex aux = this.getRule(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size()) && (seguir); j++) {
                Complex aux2 = this.getRule(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if ((s.compareTo(s2) == -3) || (s.compareTo(s2) == 0)) {
                            salir = true; 
                            if ((l == aux.size() - 1) &&
                                (aux.getDistribClassEx(0) ==
                                 aux2.getDistribClassEx(0))) {
                                seguir = false;
                                this.deleteRegla(i);
                                i--;
                                tam--;
                            }
                        }
                    }
                    parar = !salir;
                }
            }
        }
    }

    /**
     * <p>
     * Delete rules with the same attribute
     * </p>
     * @param beamWidth               Size of the beam
     */
    public void deleteEqualAttributes(int beamWidth) {

        for (int i = 0; i < this.size(); i++) {
            Selector s = this.getRule(i).getSelector(0);
            for (int j = i+1; j < this.size(); j++){
                Selector s2 = this.getRule(j).getSelector(0);
                if ((s.getAtributo() == s2.getAtributo())&&(this.size() > beamWidth)){
                    this.deleteRegla(j);
                    j--;
                }
            }
        }
    }

    /**
     * <p>
     * Delete rules with low support
     * </p>
     * @param beamWidth           Size of the beam
     * @param minSup         Minimum support
     */
    public void deleteRulesLowSupport(int beamWidth, float minSup) {

        for (int i = 0; i < this.size() && i >= beamWidth; i++) {
            if(this.getRule(i).getSup() < minSup)
                this.deleteRegla(i);
                    i--;
        }
    }

    /**
     * <p>
     * Copy the name of the class
     * </p>
     * @param nameClass                 The name of the class
     */
    public void addNameClass(String nameClass){
        this.nombreClase = nameClass;
    }

    /**
     * <p>
     * Copy the complete names of the classes
     * </p>
     * @param classes                 The complete names of the classes
     */
    public void addNameClasses(String [] classes){
        valorNombreClases = new String[classes.length];
        for (int i = 0; i < classes.length; i++){
            valorNombreClases[i] = classes[i];
        }
    }

}
