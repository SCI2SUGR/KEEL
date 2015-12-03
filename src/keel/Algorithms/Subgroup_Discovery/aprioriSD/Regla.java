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

import java.util.LinkedList;

/**
 * <p>Title: Regla (Rule). </p>
 *
 * <p>Description: 
 * This class implements a rule object for this subgroup dicovery algorithm.
 * This class stores the antecedents and consequents. </p>
 *
 * @author Alberto Fernandez
 * @version 1.0
 */
public class Regla implements Comparable {
    private int[] antecedente;
    private int[] posiciones;
    private int clase; //la clase que se deriva de la regla
    private int support;
    private double heuristica;
    private int nClases;
    private int distrib[]; //contiene nº muestras por clase que satisfacen antecedente

    private String [] nombreAtributos;

    /**
     * Compares the rule with the one given.
     * @param o Given rule to compare with.
     * @return int 0 if they are equals. 1 if this rule is better. -1 if this rule is worse
     */
    public int compareTo(Object o) {
        Regla r2 = (Regla) o;
        int sal = 0;

        if (heuristica == r2.getHeuristica() && this.size() == r2.size()) { //Iguales
            sal = 0;
        } else if (heuristica == r2.getHeuristica() && this.size() < r2.size()) {
            sal = 1;
        } else if (heuristica == r2.getHeuristica() && this.size() > r2.size()) {
            sal = -1;
        } else if (heuristica > r2.getHeuristica()) {
            sal = 1;
        } else if (heuristica < r2.getHeuristica()) {
            sal = -1;
        }

        return (sal);
    }


    /**
     * Default constructor. Empty rule
     */
    public Regla() {
        antecedente = new int[1];
        antecedente[0] = 0;
        clase = 0;
    }

    /**
     * Parameter Constructor. Builds a rule with the parameters given.
     * @param tam int number of antecedents
     * @param nClases int number of classes of the problem
     */
    public Regla(int tam, int nClases) {
        antecedente = new int[tam];
        posiciones = new int[tam];
        this.nClases = nClases;
        distrib = new int[nClases];
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0;
        }
        clase = 0;
    }

    /**
     * Parameter Constructor. Builds a rule with the item given.
     * @param item Item item used to build the rule.
     * @param nClases int number of classes of the problem
     */
    public Regla(Item item, int nClases) {
        posiciones = item.getColumnas();
        antecedente = item.getItem();
        this.nClases = nClases;
        distrib = new int[nClases];
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0;
        }
        this.clase = antecedente[antecedente.length - 1];
        support = item.getSupport();
    }

    /**
     * Copy the rule
     * @return r the rule copied.
     */
    public Regla copiaRegla() {
        Regla r = new Regla(antecedente.length, this.nClases);
        for (int i = 0; i < antecedente.length; i++) {
            r.addAtributo(i, antecedente[i]);
            r.addPosicion(i, posiciones[i]);
        }
        r.setClase(clase);
        r.setSupport(this.support);
        r.setHeuristicaWRAcc(heuristica);
        r.setDistrib(this.getDistribucion());
        r.nClases = this.nClases;
        r.adjuntaNombreAtributos(nombreAtributos);
        return r;
    }

    /**
     * Adds a new antecedent (attribute + value).
     * @param i int given attribute index.
     * @param a int given value of the attribute.
     */
    public void addAtributo(int i, int a) {
        antecedente[i] = a;
    }

    /**
     * Adds an attribute to the rule.
     * @param i int index of the array.
     * @param pos int attribute position
     */
    public void addPosicion(int i, int pos) {
        posiciones[i] = pos;
    }


    /**
     * Returns the ith antecedent
     * @param i int position in the rule.
     * @return antecedent.
     */
    public int getAtributo(int i) {
        return antecedente[i];
    }

    /**
     * Returns the rule size (number of attributes/antecedents)
     * @return int the rule size
     */
    public int size() {
        return (antecedente.length - 1);
    }

    /**
     * Returns the rule class.
     * @return the rule class.
     */
    public int getClase() {
        return clase;
    }

    /**
     * Sets the rule class with the value given.
     * @param i given value to set the class.
     */
    public void setClase(int i) {
        clase = i;
    }

    /**
     * Sets the support value of the rule.
     * @param s int given support value.
     */
    public void setSupport(int s) {
        support = s;
    }

    /**
     * Returns  the support value of the rule.
     * @return int  the support value of the rule.
     */
    public int getSupport() {
        return support;
    }

    /**
     * Checks if the rule covers the sample given.
     * @param m given sample.
     * @return  True if the rule covers the sample given.
     */
    public boolean cubre(Muestra m) {
        boolean resp = true;
        int i;
        double instancia[] = m.getMuest();
        for (i = 0; i < this.size() && resp; i++) { // recorremos los valores del antecedente
            if (instancia[posiciones[i]] != antecedente[i]) { //El valor de la instancia para ese atributo no coincide
                resp = false; //La regla no cubre al ejemplo!
            }
        }
        return resp;
    }

    /**
     * Prints the rule information on the standard output.
     */
    public void print() {
        for (int x = 0; x < this.size(); x++) {
            System.out.print("("+nombreAtributos[posiciones[x]] + " = ");
            System.out.print(" " + antecedente[x] + ")");
            if (x < this.size() - 1) {
                System.out.print(" AND ");
            }
        }
    }

    /**
     *  Returns a string with the rule information.
     * @return String with the rule information.
     */
    public String printString() {
        String cad = "";
        for (int x = 0; x < this.size(); x++) {
            cad += nombreAtributos[posiciones[x]] + " =  ";
            cad += antecedente[x] + " ";
            if (x < this.size() - 1) {
                cad += " AND ";
            }
        }
        return cad;
    }

    /**
     * Returns the support of the rule as string.
     * @return the support of the rule as string. 
     */
    public String printSupport(){
        String cad = new String("-- Support: " + support);
        return cad;
    }

        /**
     * <p>
     * Local copy of the values of the attributes of the rule.
     * </p>
     * @param cambio the new values (0 -> X, 1 -> Y...) 
     */
    public void ajusta(int[] cambio) {
        for (int i = 0; i < antecedente.length; i++) {
            antecedente[i] = cambio[antecedente[i]];
        }
        clase = cambio[clase];
    }

    /**
     * <p>
     * Local copy of the name of variables
     * </p>
     * @param atributos String[] stores the name of the variables
     */
    public void adjuntaNombreAtributos(String [] atributos){
        nombreAtributos = new String[atributos.length];
        for (int i = 0; i < atributos.length; i++){
            nombreAtributos[i] = atributos[i];
        }
    }

    /**
     * <p>
     * Assign a heuristic value (Wracc) to the rule
     * </p>
     * @param heu heuristic value
     */
    public void setHeuristicaWRAcc(double heu) {
        heuristica = heu;
    }

    /**
     * <p>
     * return the heuristic value of the rule
     * </p>
     * @return double heuristic value of the rule
     */
    public double getHeuristica() {
        return heuristica;
    }

    /**
     * Sets the given distribution.
     * @param distribucion int[] given distribution to set.
     */
    public void setDistrib(int [] distribucion){
        for (int i = 0; i < distribucion.length; i++){
            this.distrib[i] = distribucion[i];
        }
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @return double [] the value of each distribution
     */
    public int[] getDistribucion() {
        int ret[] = new int[nClases];
        for (int i = 0; i < nClases; i++) {
            ret[i] = distrib[i];
        }
        return ret;
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @param i int index of the class
     * @return double value of distribution
     */
    public int getDistribucionClase(int i) {
        return distrib[i];
    }
    
    /**
     * <p>
     * Add one to the n of the rule for the class
     * </p>
     * @param i int value of the class
     */
    public void incremDistribClase(int i) {
      distrib[i] ++;
  }

    /**
     * <p>
     * Print the classes distribution for the rule
     * </p>
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClases; i++) {
            System.out.print(" "+distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * <p>
     * Print the classes distribution for the rule
     * </p>
     * @return String classes distribution for the rule
     */
    public String printDistribucionString() {
        String cad;
        cad = " [";
        for (int i = 0; i < nClases; i++) {
            cad += " "+distrib[i];
        }
        cad += "]";

        return cad;
    }


}

