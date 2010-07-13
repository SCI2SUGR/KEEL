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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Hyperrectangles.EACH;

import java.util.*;


public class RuleSet {
/**
 * <p>
 * Set of rules
 * Defines a set of rules or complex
 * </p>
 */	
	
    private LinkedList rules;
    private String className;
    private String[] classNames;

    /**
     * <p>
     * Constructor
     * </p>
     */
    public RuleSet() {
        rules = new LinkedList();
    }

    /**
     * <p>
     * Add a set of rules to the list
     * </p>
     * @param r ConjReglas The set of rules
     */
    public void addRules(RuleSet r) {
        for (int i = 0; i < r.size(); i++) {
            Complex rule = r.getRule(i);
            rules.add(rule);
        }
    }

    /**
     * <p>
     * Add a rule to the list
     * </p>
     * @param regl Rule to add
     */
    public void addRule(Complex regl) {
        rules.add(regl);
    }

    /**
     * <p>
     * Removes a rule from the list
     * </p>
     * @param i index of the rule to remove
     */
    public void removeRule(int i) {
        rules.remove(i);
    }

    /**
     * <p>
     * Removes the content of a set of rules
     * </p>
     */
    public void deleteAll(){
        rules.removeAll(rules);
    }

    /**
     * <p>
     * Returns a rule of the list
     * </p>
     * @param i index of the rule
     * @return the 'i' rule
     */
    public Complex getRule(int i) {
        return (Complex) rules.get(i);
    }
  
    /**
     * <p>
     * Returns the number of rules we are working with
     * </p>
     * @return the size of the set of rules
     */
    public int size() {
        return (rules.size());
    }

    /**
     * <p>
     * Returns the complet set if rules
     * </p>
     * @return the complet set of rules
     */
    public LinkedList getSetRules() {
        return rules;
    }

    /**
     * <p>
     * Prints on the screen the set of rules
     * </p>
     */
    public void print(int nominal) {
        for (int i = 0; i < rules.size(); i++) {
            Complex c = (Complex) rules.get(i);
            System.out.print("\nRule " + (i+1) + ": IF  ");
            c.print(nominal);
            System.out.print(" THEN "+className+" -> " + classNames[c.getClassAttribute()] + "  ");
            c.printDistribution();
        }
    }

    /**
     * <p>
     * Prints on a string the set of rules
     * </p>
     * @return A strign that stores the set of rules
     */
    public String printString(int []numValues) {
        String cad = "";
	boolean removed=false;
	boolean goAhead=true;
	Selector s1,s2;
	int counter=0,k;
	double v1[]=new double[2];
	double v2[]=new double[2];
	double aux1[],aux2[];
	int values1,values2;
        for (int i = 0; i < rules.size(); i++) {
            Complex c = (Complex) rules.get(i);
	    removed=false;
	    for (int j = 0; j < rules.size() &&(!removed); j++) {
	    	Complex c2 = (Complex) rules.get(j);
	    	if(j!=i && (c.getClassAttribute()==c2.getClassAttribute())){
		goAhead=true;
		//System.out.println(" OTRO ");
		for (k = 0; k < c.size() && goAhead; k++) {
		      s1=c.getSelector(k);aux1=s1.getValues();values1=s1.getNumValues();
		     s2=c2.getSelector(k);aux2=s2.getValues();values2=s2.getNumValues();
		       if(values1==1){v1[0]=aux1[0];v1[1]=v1[0];}
		       else{v1[0]=aux1[0];v1[1]=aux1[1];};
		       if(values2==1){v2[0]=aux2[0];v2[1]=v2[0];}
		       else{v2[0]=aux2[0];v2[1]=aux2[1];};
			if((v1[0]>=v2[0]) && (v1[1]<=v2[1]))goAhead=true;
			else goAhead=false;
			/*System.out.print(v1[0]+" , "+v1[1]);
			System.out.println("	"+v2[0]+" , "+v2[1]);*/	
		}
		if(goAhead==true /*&& (c.getClase()==c2.getClase())*/){
			removed=true;
			/*System.out.println("se elimina "+(i+1));
			System.out.println(eliminada);*/
		}
		}
	    }
	   // System.out.println(eliminada);
	    if(!removed){
	   //System.out.println("dentro");
            cad += "\n\nRule " + (counter+1) + ": IF  ";
	    cad += c.printString(numValues);
	   // if(i<10)System.out.println(cad);
            cad += " THEN "+className+ " -> " + classNames[c.getClassAttribute()] + "  ";
	    cad += "    [ Hyperrectangle weight = "+c.getWeight()+" ] ";
	    cad += "    [ Volumen = "+c.getVolume()+" ] ";
	    counter++;
	    }
           // cad += c.printDistribucionString();
	   
        }
        return cad;
    }

 
    /**
     * <p>
     * Returns the last rule(normally the one with best weight)
     * </p>
     * @return the last rule of the list
     */
    public Complex getLastRule() {
        return (Complex) rules.getLast();
    }

    /**
     * <p>
     * Remove complex with repetitive attributes
     * </p>
     */
    
    public void removeNulls() {
        boolean salir;
        for (int i = 0; i < this.size(); i++) {
            Complex aux = this.getRule(i);
            salir = false;
            for (int j = 0; (j < aux.size() - 1) && (!salir); j++) {
                Selector s = aux.getSelector(j);
                for (int h = j + 1; (h < aux.size()) && (!salir); h++) {
                    Selector s2 = aux.getSelector(h);
                    if (s.compareTo(s2) < 2) { //mismo atributo
                        this.removeRule(i); //borrando
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
    public void removeDuplicated(int tam) {
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complex aux = this.getRule(i);
            boolean seguir = true;
            for (int j = i+1; (j < this.size())&&(seguir); j++) {
                Complex aux2 = this.getRule(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if (s.compareTo(s2) == 0) { //son iguales
                            salir = true; //paso a ver el siguiente selector (si eso)
                            if (l == aux.size() - 1) {
                                /*
                                System.out.println("\nEstos son los complejos repetidos:");
                                aux.print();
                                aux2.print();
                                */
                                seguir = false;
                                this.removeRule(i); //borro porque estï¿½repe totalmente
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
    public void removeSubsumed(int tam){
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complex aux = this.getRule(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size())&&(seguir); j++) {
                Complex aux2 = this.getRule(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if ((s.compareTo(s2) == -3)||(s.compareTo(s2) == 0)) { //mirar compareTo en Selector
                            salir = true; //paso a ver el siguiente selector (si eso)
                            if ((l == aux.size() - 1)&&(aux.getDistributionClass(0) == aux2.getDistributionClass(0))) {
                            //if (l == aux.size() - 1) {
                                /*
                                System.out.println("\nEstos son los complejos subsumidos:");
                                aux.print();
                                aux2.print();
                                */
                                seguir = false;
                                this.removeRule(i); //tienen los mismos atributos y misma distribucion (son semanticament =)
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
    public void adjuntClassName(String nombreClase){
        this.className = nombreClase;
    }

    /**
     * <p>
     * Do a local copy of the name of the values of the class
     * </p>
     * @param clases String[] An array that stores the name of the value of the class
     */
    public void adjuntClassNames(String [] clases){
        classNames = new String[clases.length];
        for (int i = 0; i < clases.length; i++){
            classNames[i] = clases[i];
        }
    }


}

