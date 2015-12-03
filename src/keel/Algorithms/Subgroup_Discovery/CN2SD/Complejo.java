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

package keel.Algorithms.Subgroup_Discovery.CN2SD;

import java.util.LinkedList;


/**
 * <p> Stores conjunctions of selectors
 * @author Alberto Fernández
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class Complejo implements Comparable {

    /**
     *  Esta clase almacena conjunciones de Selectores
     */
    private LinkedList compl;
    private int clase;
    private int nClases;
    private double distrib[]; //contiene porcentaje nº muestras por clase que satisfacen antecedente
    private int distribEj[]; //contiene nº muestras por clase que satisfacen antecedente
    private double heuristica; // Coste segun heuristica
    private String [] nombreAtributos;

        /**
     * Default constructor.
     */
    public Complejo() {
    }

    /**
     * Constructor for Complex
     * @param nClas int number of classes
     */
    public Complejo(int nClas) {
        compl = new LinkedList(); //Inicializo la lista
        nClases = nClas; //Almaceno el nº de clases
        distrib = new double[nClas]; //Distribucion para las clases
        distribEj = new int[nClas]; //Distribucion para las clases
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0; //Inicializo a 0
            distribEj[i] = 0;
        }
    }

    /**
     * <p>
     * Compare two objects of the class
     * </p>
     * @param o complex to compare
     * @return int 0 Are equals (same heuristic and size, -1 if is major (same heuristic, less size || more heuristic)
     * 1 is worst (same heuristic, less size || less heuristic).
     */
    public int compareTo(Object o) {
        Complejo c2 = (Complejo) o;
        int sal = 0;

        if (heuristica == c2.getHeuristica() && compl.size() == c2.size()) {
            sal = 0;
        } else if (heuristica == c2.getHeuristica() && compl.size() < c2.size()) {
            sal = -1;
        } else if (heuristica == c2.getHeuristica() && compl.size() > c2.size()) {
            sal = 1;
        } else if (heuristica > c2.getHeuristica()) {
            sal = -1;
        } else if (heuristica < c2.getHeuristica()) {
            sal = 1;
        }

        return (sal);
    }

    /**
     * <p>
     * Check if two complex are equals (represent the same)
     * </p>
     * @param c El complex to compare
     * @return boolean True if are equals. False otherwise
     */
    public boolean esIgual(Complejo c){
        boolean iguales = false;
        if (c.size() == compl.size()){
            iguales = true;
            for (int i = 0; (i < c.size())&&(iguales); i++){
                iguales = (this.getSelector(i).compareTo(c.getSelector(i)) == 0);
            }
        }
        return iguales;
    }


    /**
     * <p>
     * Add the selector into the selector list
     * </p>
     * @param s the selector (set atr. op. value)
     */
    public void addSelector(Selector s) {
        compl.add(s);
    }

    /**
     * <p>
     * Return a selector in one position by giving a complex
     * </p>
     * @param indice Position inside the complex
     * @return Selector El selector
     */
    public Selector getSelector(int indice) {
        return (Selector) compl.get(indice);
    }

    /**
     * <p>
     * Return the size complex
     * </p>
     * @return int El nmero de selectores que posee el complejo
     */
    public int size() {
        return compl.size();
    }

    /**
     * <p>
     * Return the number of classes
     * </p>
     * @return int number of classes
     */
    public int getNClases() {
        return this.nClases;
    }

    /**
     * Returns the class value
     * @return int  the class value
     */
    public int getClase() {
        return this.clase;
    }

    /**
     * Sets the given value as class.
     * @param clase int given value to set.
     */
    public void setClase(int clase) {
        this.clase = clase;
    }

    /**
     * Checks if the complex covers the given sample.
     * @param m  given sample.
     * @return boolean True if the complex covers the given sample.
     */
    public boolean cubre(Muestra m) {
        boolean cubierto = true;
        double[] ejemplo = m.getMuest();
        for (int i = 0; i < this.size() && cubierto; i++) { // recorremos los selectores del complejo
            Selector s = this.getSelector(i);
            switch (s.getOperador()) {
            case 0: // se trata del igual
                double [] valor = s.getValores();
                cubierto = false;
                for (int j = 0; (j < valor.length)&&(!cubierto); j++){
                    cubierto = (ejemplo[s.getAtributo()] == valor[j]); //en cuanto uno sea true me vale (or)
                }
                /*if (!(ejemplo[s.getAtributo()] == s.getValor())) {
                    cubierto = false;
                }*/
                break;
            case 1: // se trata del distinto
                cubierto = ejemplo[s.getAtributo()] != s.getValor();
                break;
            case 2: //menor o igual
                cubierto = ejemplo[s.getAtributo()] <= s.getValor();
                break;
            case 3: //mayor
                cubierto = ejemplo[s.getAtributo()] > s.getValor();
                break;
            }
        }
        return cubierto;
    }

    /**
     * <p>
     * return the heuristic value of the complex
     * </p>
     * @return double heuristic value of the complex
     */
    public double getHeuristica() {
        return heuristica;
    }

    /**
     * <p>
     * Assign a heuristic value (Wracc) to the complex
     * </p>
     * @param heu heuristic value
     */
    public void setHeuristica(double heu) {
        heuristica = heu;
    }

    /**
     * <p>
     * reset the distribution value for the complex
     * </p>
     */
    public void borraDistrib() {
        for (int i = 0; i < nClases; i++) {
            distribEj[i] = 0;
        }
    }

    /**
     * <p>
     * Add one to the n of the complex for the class
     * </p>
     * @param clase int value of the class
     */
    public void incrementaDistrib(int clase) {
        distribEj[clase]++;
    }

    /**
     * Normalizes the distribution values [0, 1]
     */
    public void ajustaDistrib() {
        double total = 0;
        for (int i = 0; i < nClases; i++) {
            total += distribEj[i];
            distrib[i] = 0;
        }
        if (total > 0){
            for (int i = 0; i < nClases; i++) {
                distrib[i] = (double) distribEj[i] / total;
            }
        }
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @param clase int index of the class
     * @return double value of distribution
     */
    public double getDistribucionClase(int clase) {
        return distrib[clase];
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @return double [] the value of each distribution
     */
    public double[] getDistribucion() {
        return distrib;
    }

    /**
     * <p>
     * Return the value of the distribution for the given class
     * </p>
     * @param clase given class
     * @return int the value of the distribution for the given class
     */
    public int getDistribucionClaseEj(int clase) {
        return distribEj[clase];
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @return int [] the value of each distribution
     */
    public int[] getDistribucionEj() {
        return distribEj;
    }


    /**
     * Print the content of the complex (List->Attribute operator value)
     */
    public void print() {
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
            //System.out.print("(Atr" + s.getAtributo() + " ");
            System.out.print("(" + nombreAtributos[s.getAtributo()] + " ");
            switch (s.getOperador()) {
            case 0:
                System.out.print("=");
                break;
            case 1:
                System.out.print("<>");
                break;
            case 2:
                System.out.print("<=");
                break;
            default:
                System.out.print(">");
            }
            double [] valores = s.getValores();
            if (valores.length > 1){
                System.out.print(" " + valores[0]);
                for (int i = 1; i < valores.length - 1; i++) {
                    System.out.print(" ó " + valores[i]);
                }
                System.out.print(" ó " + valores[valores.length - 1] + ")");
            }
            else{
                System.out.print(" " + valores[0]+")");
            }
            if (x < compl.size() - 1) {
                System.out.print(" AND ");
            }
        }
        //System.out.print(" -- "+heuristica+" -- ");
    }

    /**
     * Returns a string with the complex contents (List -> Attribute operator value).
     * @return a string with the complex contents 
     */
    public String printString() {
        String cad = "";
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
            //cad += "Atr" + s.getAtributo() + " ";
            cad += nombreAtributos[s.getAtributo()] + " ";
            switch (s.getOperador()) {
            case 0:
                cad += "=";
                break;
            case 1:
                cad += "<>";
                break;
            case 2:
                cad += "<=";
                break;
            case 3:
                cad += ">";
                break;
            }
            double [] valores = s.getValores();
            if (valores.length > 1){
                cad += " " + valores[0];
                for (int i = 1; i < valores.length - 1; i++) {
                    cad += " ó " + valores[i];
                }
                cad += " ó " + valores[valores.length - 1] + "";
            }
            else{
                cad += " " + valores[0] + "";
            }
            if (x < compl.size() - 1) {
                cad += " AND ";
            }
        }
        return cad;
    }

    /**
     * <p>
     * Print the classes distribution for the complex
     * </p>
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClases; i++) {
            System.out.print(" " + distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * <p>
     * Print the classes distribution for the complex
     * </p>
     * @return String classes distribution for the complex
     */
    public String printDistribucionString() {
        String cad = new String("   [");
        for (int i = 0; i < nClases; i++) {
            cad += " " + distrib[i];
        }
        cad += "]";
        return cad;
    }

    /**
     * <p>
     * Local copy of the name of variables
     * </p>
     * @param atributos String[] stores the name of the variables
     */
    public void adjuntaNombreAtributos(String [] atributos){
        nombreAtributos = new String[atributos.length -1];
        for (int i = 0; i < atributos.length -1; i++){
            nombreAtributos[i] = atributos[i];
        }
    }

}

