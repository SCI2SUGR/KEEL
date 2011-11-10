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

import java.util.LinkedList;
import java.text.DecimalFormat;

import keel.Dataset.*;

public class Complex implements Comparable {

    /**
     * <p>
     * This class has the different selectors for the dataset
     * </p>
     */
    private LinkedList compl;
    private int clas;
    private int nClasses;
    private double distrib[]; 
    private int distribEx[];
    private double q_g;
    private double sup;
    private double TP;
    private double FP;
    private String [] nameAttributes;

    public Complex() {
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param nClas             Number of classes
     */
    public Complex(int nClas) {
        compl = new LinkedList();       //Initialise the complex
        nClasses = nClas; 
        distrib = new double[nClas];    //Distribution of the classes
        distribEx = new int[nClas];     //Distribution of the examples of the classes
        for (int i = 0; i < nClasses; i++) {
            distrib[i] = 0;
            distribEx[i] = 0;
        }
    }

    /**
     * <p>
     * Compare two objects
     * </p>
     * @param o            Complex to compare
     * @return                  0 equal || -1 better || 1 worse
     */
    public int compareTo(Object o) {
        Complex c2 = (Complex) o;
        int sal = 0;

        if (q_g == c2.getQg()){
            if (sup > c2.getSup()){
                sal = -1;
            } else if (sup < c2.getSup()){
                sal = 1;
            } else {
                if (compl.size() == c2.size()) {
                    sal = 0;
                } else if (compl.size() < c2.size()) {
                    sal = -1;
                } else {
                    sal = 1;
                }
            }

        } else if (q_g > c2.getQg()) {
            sal = -1;
        } else if (q_g < c2.getQg()) {
            sal = 1;
        }

        return (sal);
    }

    /**
     * <p>
     * Check if two complex are equal
     * </p>
     * @param c           Complex to compare
     * @return                  True of False
     */
    public boolean isEqual(Complex c){
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
     * Add the selector to the complex
     * </p>
     * @param s          The selector to add
     */
    public void addSelector(Selector s) {
        compl.add(s);
    }

    /**
     * <p>
     * Get a selector
     * </p>
     * @param index             Position of the selector
     * @return                  Selector
     */
    public Selector getSelector(int index) {
        return (Selector) compl.get(index);
    }

    /**
     * <p>
     * Return the size of the complex
     * </p>
     * @return              The number of selectors
     */
    public int size() {
        return compl.size();
    }

    /**
     * <p>
     * Return the number of classes
     * </p>
     * @return              The number of classes
     */
    public int getNClass() {
        return this.nClasses;
    }

    /**
     * <p>
     * Return the class of the complex
     * </p>
     * @return              The class of the complex
     */
    public int getClas() {
        return this.clas;
    }

    /**
     * <p>
     * Asigne the class to the complex
     * </p>
     * @param value             Class for the complex
     */
    public void setClas(int value) {
        this.clas = value;
    }

    /**
     * <p>
     * Check if the complex covers to the example
     * </p>
     * @param m               Instance to study
     * @return                      True or False
     */
    public boolean cover(Instance m) {
        boolean cubierto = true;
        double[] ejemplo = m.getMuest();
        for (int i = 0; i < this.size() && cubierto; i++) {
            Selector s = this.getSelector(i);
            switch (s.getOperador()) {
            case 0: // =
                double [] valor = s.getValores();
                cubierto = false;
                for (int j = 0; (j < valor.length)&&(!cubierto); j++){
                    cubierto = (ejemplo[s.getAtributo()] == valor[j]); 
                }
                break;
            case 1: // <>
                cubierto = ejemplo[s.getAtributo()] != s.getValor();
                break;
            case 2: // <=
                cubierto = ejemplo[s.getAtributo()] <= s.getValor();
                break;
            case 3: // >
                cubierto = ejemplo[s.getAtributo()] > s.getValor();
                break;
            }
        }
        return cubierto;
    }

    /**
     * <p>
     * Return the value of Qg
     * </p>
     * @return              The value of Qg
     */
    public double getQg() {
        return q_g;
    }

    /**
     * <p>
     * Return the value of Sup
     * </p>
     * @return              The value of Sup
     */
    public double getSup() {
        return sup;
    }

    /**
     * <p>
     * Return the value of TP
     * </p>
     * @return              The value of TP
     */
    public double getTP() {
        return TP;
    }

    /**
     * <p>
     * Return the value of FP
     * </p>
     * @return              The value of FP
     */
    public double getFP() {
        return FP;
    }

    /**
     * <p>
     * Set the value of Qg
     * </p>
     * @param heu              The value of Qg
     */
    public void setQg(double heu) {
        q_g = heu;
    }

    /**
     * <p>
     * Set the value of TP
     * </p>
     * @param heu              The value of TP
     */
    public void setTP(double heu) {
        TP = heu;
    }

    /**
     * <p>
     * Set the value of FP
     * </p>
     * @param heu              The value of FP
     */
    public void setFP(double heu) {
        FP = heu;
    }

    /**
     * <p>
     * Set the value of Sup
     * </p>
     * @param heu              The value of Sup
     */
    public void setSup(double heu) {
        sup = heu;
    }

    /**
     * <p>
     * Reset the value of the distribution
     * </p>
     */
    public void deleteDistrib() {
        for (int i = 0; i < nClasses; i++) {
            distribEx[i] = 0;
        }
    }

    /**
     * <p>
     * Increments the number of example for the class cover for the complex
     * </p>
     * @param value             The value of the class
     */
    public void incrementDistrib(int value) {
        distribEx[value]++;
    }

    /**
     * <p>
     * Convert the distribution between 0 and 1
     * </p>
     */
    public void adjustDistrib() {
        double total = 0;
        for (int i = 0; i < nClasses; i++) {
            total += distribEx[i];
            distrib[i] = 0;
        }
        if (total > 0){
            for (int i = 0; i < nClasses; i++) {
                distrib[i] = (double) distribEx[i] / total;
            }
        }
    }

    /**
     * <p>
     * Return the value of a distribution
     * </p>
     * @param value         Index of the class
     * @return              The value of the distribution for this class
     */
    public double getDistribClass(int value) {
        return distrib[value];
    }

    /**
     * <p>
     * Return the value of the complete distribution
     * </p>
     * @return double[]         The value of the complete distribution
     */
    public double[] getDistrib() {
        return distrib;
    }

    /**
     * <p>
     * Return the value of the distribution for the example of a class
     * </p>
     * @param value             Index of the class
     * @return                  Value of the distribution
     */
    public int getDistribClassEx(int value) {
        return distribEx[value];
    }

    /**
     * <p>
     * Return the value of the complete distribution for the example of a class
     * </p>
     * @return                  Value of the distribution
     */
    public int[] getDistribEx() {
        return distribEx;
    }


    /**
     * <p>
     * Print the content of the complex
     * </p>
     */
    public void print() {
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
            //System.out.print("(Atr" + s.getAtributo() + " ");
            System.out.print("(" + nameAttributes[s.getAtributo()] + " ");
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
                    System.out.print(" � " + valores[i]);
                }
                System.out.print(" � " + valores[valores.length - 1] + ")");
            }
            else{
                System.out.print(" " + valores[0]+")");
            }
            if (x < compl.size() - 1) {
                System.out.print(" AND ");
            }
        }
    }

    /**
     * <p>
     * Print a String the content of the complex
     * </p>
     * @return              Complex
     */
    public String printString() {
        String cad = "";
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
            Attribute a = Attributes.getAttribute(nameAttributes[s.getAtributo()]);

            cad += nameAttributes[s.getAtributo()] + " ";
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
                cad += " " + a.getNominalValue((int) valores[0]);
                for (int i = 1; i < valores.length - 1; i++) {
                    cad += " � " + a.getNominalValue((int) valores[i]);
                }
                cad += " � " + a.getNominalValue((int) valores[valores.length - 1]) + "";
            }
            else{
                cad += " " + a.getNominalValue((int) valores[0]) + "";
            }
            if (x < compl.size() - 1) {
                cad += " AND ";
            }
        }
        return cad;
    }

    /**
     * <p>
     * Print the distribution
     * </p>
     */
    public void printDistrib() {

        DecimalFormat d = new DecimalFormat("0.00");

        System.out.print("   [");
        for (int i = 0; i < nClasses; i++) {
            System.out.print(" " + d.format(distrib[i]));
        }
        System.out.print("]");
    }

    /**
     * <p>
     * Print a String with the distribution
     * </p>
     * @return                      Complete distribution for the classes
     */
    public String printDistribucionString() {

        DecimalFormat d = new DecimalFormat("0.00");

        String cad = new String("\n[");
        for (int i = 0; i < nClasses; i++) {
            cad += "  " + d.format(distrib[i]);
        }
        cad += "  ]\n";
        return cad;
    }

    /**
     * <p>
     * Copy the name of the attributes
     * </p>
     * @param attrib              Array with the name of the attributes
     */
    public void adjuntaNombreAtributos(String [] attrib){
        nameAttributes = new String[attrib.length -1];
        for (int i = 0; i < attrib.length -1; i++){
            nameAttributes[i] = attrib[i];
        }
    }

}
