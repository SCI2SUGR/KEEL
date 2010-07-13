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
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Prism;

import java.util.*;

public class ConjReglas {
/**
 * <p>
 * Set of rules
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
    public ConjReglas() {
        reglas = new LinkedList();
    }

    /**
     * <p>
     * Add a set of rules to the list
     * </p>
     * @param r ConjReglas The set of rules
     */
    public void addReglas(ConjReglas r) {
        for (int i = 0; i < r.size(); i++) {
            Complejo regla = r.getRegla(i);
            reglas.add(regla);
        }
    }

    /**
     * <p>
     * Add a rule to the list
     * </p>
     * @param regl Rule to add
     */
    public void addRegla(Complejo regl) {
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
     * Removes the content of a set of rules
     * </p>
     */
    public void deleteAll(){
        reglas.removeAll(reglas);
    }

    /**
     * <p>
     * Returns a rule of the list
     * </p>
     * @param i index of the rule
     * @return the 'i' rule
     */
    public Complejo getRegla(int i) {
        return (Complejo) reglas.get(i);
    }

    /**
     * Devuelve la regla como una nueva copia
     * @param i regla i-esima
     * @return una regla de la lista como una nueva copia
     *
         public Complejo getNuevaRegla(int i) {
        Complejo c = (Complejo) reglas.get(i);
        Complejo c2 = new Complejo(c.getSelector(0), c.getNclases());
        for (int j = 1; j < c.size(); j++) {
            c2.addSelector(c.getSelector(j));
        }
        return c2;
         }*/

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
         public ConjReglas copiaConjReglas() {
        int i;
        ConjReglas c = new ConjReglas();

        for (i = 0; i < reglas.size(); i++) {
            Complejo comp = (Complejo) reglas.get(i);
            c.addRegla(comp.copiaRegla());
        }

        return c;
         }*/

    /**
     * <p>
     * Prints on the screen the set of rules
     * </p>
     */
    public void print() {
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            System.out.print("\nRule " + (i+1) + ": IF  ");
            c.print();
            System.out.print(" THEN "+nombreClase+" -> " + valorNombreClases[c.getClase()] + "  ");
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
        String cad = "";
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            cad += "\nRule " + (i+1) + ": IF  ";
            cad += c.printString();
            cad += " THEN "+nombreClase+ " -> " + valorNombreClases[c.getClase()] + "  ";
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
    public Complejo getUltimaRegla() {
        return (Complejo) reglas.getLast();
    }

    /**
     * <p>
     * Remove complex with repetitive attributes
     * </p>
     */
    public void eliminaNulos() {
        boolean salir;
        for (int i = 0; i < this.size(); i++) {
            Complejo aux = this.getRegla(i);
            salir = false;
            for (int j = 0; (j < aux.size() - 1) && (!salir); j++) {
                Selector s = aux.getSelector(j);
                for (int h = j + 1; (h < aux.size()) && (!salir); h++) {
                    Selector s2 = aux.getSelector(h);
                    if (s.compareTo(s2) < 2) { //mismo atributo
                        this.deleteRegla(i); //borrando
                        salir = true;
                        i--;
                    }
                }
            }
        }
    }

    /**
     * <p>
     * Remove repetitive complex(at1 = 0 ^ at2 = 0 -- at2 = 0 ^ at1 = 0)
     * </p>
     * @param tam Size of the star
     */
    public void eliminaRepetidos(int tam) {
        for (int i = 0; i < this.size()-1; i++) {
       // for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i+1; (j < this.size())&&(seguir); j++) {
                Complejo aux2 = this.getRegla(j);
                //seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
			//System.out.println("Comparando ");
			
                        if (s.compareTo(s2) == 0) { //son iguales
                            salir = true; //paso a ver el siguiente selector (si eso)
			   /* System.out.println("selectores iguales");
			    System.out.println("regla "+(i+1)+" y "+(j+1));
				s.print();s2.print();*/
                            if (l == aux.size() - 1) {
                                
                               /* System.out.println("\nEstos son los complejos repetidos:");
                                aux.print();
                                aux2.print();*/
                                
                                seguir = false;
                                this.deleteRegla(i); //borro porque estï¿½repe totalmente
				/*System.out.println("Se borra la regla "+(i+1));
				aux.print();*/
                                i--;
                            }
                        }
                    }
                    parar = !salir; //si salir == true -> no paro (parar = false)
                }
            }
        }
    }

    /**
     * <p>
     * Remove rules are the same ina semantic way(At = 1, At <> 0, At = [0,1])
     * </p>
     * @param tam int Size of the star
     */
    public void eliminaSubsumidos(int tam){
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size())&&(seguir); j++) {
                Complejo aux2 = this.getRegla(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if ((s.compareTo(s2) == -3)||(s.compareTo(s2) == 0)) { //mirar compareTo en Selector
                            salir = true; //paso a ver el siguiente selector (si eso)
                            if ((l == aux.size() - 1)&&(aux.getDistribucionClase(0) == aux2.getDistribucionClase(0))) {
                            //if (l == aux.size() - 1) {
                                /*
                                System.out.println("\nEstos son los complejos subsumidos:");
                                aux.print();
                                aux2.print();
                                */
                                seguir = false;
                                this.deleteRegla(i); //tienen los mismos atributos y misma distribucion (son semanticament =)
                                i--;
                            }
                        }
                    }
                    parar = !salir; //si salir == true -> no paro (parar = false)
                }
            }
        }
    }

    /**
     * <p>
     * Do a clocal copy of the name of the class variable
     * </p>
     * @param nombreClase Name of the class
     */
    public void adjuntaNombreClase(String nombreClase){
        this.nombreClase = nombreClase;
    }

    /**
     * <p>
     * Do a local copy of the name of the values of the class
     * </p>
     * @param clases String[] An array that stores the name of the value of the class
     */
    public void adjuntaNombreClases(String [] clases){
        valorNombreClases = new String[clases.length];
        for (int i = 0; i < clases.length; i++){
            valorNombreClases[i] = clases[i];
        }
    }


}

