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

import java.util.LinkedList;
import keel.Dataset.*;


public class Complejo implements Comparable {
/**
 * Define a complex or a rule, stores selectors
 */
    private LinkedList compl;
    private int clase;
    private int nClases;
    // stores the percent by classes
    private int distrib[]; 
    // heuristic cost
    private double heuristica;
    private String [] nombreAtributos;

    public Complejo() {
    }

    /**
     * <p>
     * Constructor for the complex
     * </p>
     * @param nClas int Number of classes
     */
    public Complejo(int nClas) {
        compl = new LinkedList(); //Inicializo la lista
        nClases = nClas; //Almaceno el n de clases
        distrib = new int[nClas]; //Distribucion para las clases
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0; //Inicializo a 0
        }
    }

    /**
     * <p>
     * Compare two objects of the Complex class
     * </p>
     * @param o Object Complex to compare
     * @return int 0 if are equals (same heuristic and size, -1 if is better (same heuristic less size || 
     *         high heuristic)
     *         1 if is worst (same heuristic, high size || less heuristic).
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
     * Check is two complex are equals
     * </p>
     * @param c Complejo the complex to compare
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
     * Add the selector to the list
     * </p>
     * @param s Selector the selector
     */
    public void addSelector(Selector s) {
        compl.add(s);
    }
    
    
    /**
     * <p>
     * Remove the selector from the list
     * </p>
     * @param s Selector The selector
     */
    public void removeSelector(Selector s) {
        compl.remove(s);
    }
    
    /**
     * <p>
     * Remove the selectors from the list of the selectors that have the parameter attribute
     * </p>o
     * @param atributo the attribute
     */
    public void removeSelectorAtributo(int atributo) {
    	Selector s;
	int atrib;
        for(int i=0;i<compl.size();i++){
		s=(Selector)compl.get(i);
		atrib=s.getAtributo();
		if(atrib==atributo){compl.remove(s);
		/*CUIDADO!!!hay q volver al indice anterior pq se ha eliminado un elemento!!*/
		i=i-1;}
	}
    }
    
    /**
     * <p>
     * Leaves the list empty
     * </p>
     */
    public void clear() {
        compl.clear();
    }

    /**
     * <p>
     * Returns the selector in a position given the complex
     * </p>
     * @param indice int Podition inside the complex
     * @return Selector The selector
     */
    public Selector getSelector(int indice) {
        return (Selector) compl.get(indice);
    }

    /**
     * <p>
     * Returns the size of the complex
     * </p>
     * @return int The number of the selectors that has the complex
     */
    public int size() {
        return compl.size();
    }

    /**
     * <p>
     * Returns the number of classes of the problem
     * </p>
     * @return int idem.
     */
    public int getNClases() {
        return this.nClases;
    }

    /**
     * <p>
     * Returns the class that defines the comples
     * </p>
     * @return int the class
     */
    public int getClase() {
        return this.clase;
    }

    /**
     * <p>
     * Gives the value of the class to the complex
     * </p>
     * @param clase int the class
     */
    public void setClase(int clase) {
        this.clase = clase;
    }

    /**
     * <p>
     * Calculate the LaPlaces's value for a complex
     * </p>
     */
    public void calculaLaplaciano() {
      double nc;
      double ntot;
      int i;

      nc = distrib[clase];

      for (i = 0, ntot = 0; i < nClases; i++) {
        ntot += distrib[i];

      }
      //heuristica = (ntot - nc + nClases - 1) / (ntot + nClases);
      heuristica = (nc + 1) / (ntot + nClases);

  }

  
   /**
	* <p>
	* Checks if the rule gets the parameter instance
	* </p>
	* @param instancia the instance
	* @return boolean True if the rule gets the instance
    */
   public boolean reglaCubreInstancia(Instance instancia){
   	boolean cubierto=true;
	double cadena;
			
   	for (int i = 0; i < this.size()&&cubierto; i++) {
	    Selector s = this.getSelector(i);
            switch (s.getOperador()) {
            case 0: // se trata del igual
	    	double []valor=s.getValores();
		cubierto=false;
		for (int j = 0; (j < valor.length)&&(!cubierto); j++){
		   cadena=instancia.getInputRealValues(s.getAtributo());
			//if(si==1)System.out.println("COM en la instancia es "+cadena+" "+valor[j]+"y "+s.getAtributo());
			//System.out.println("y en el selector  "+valor[j]);
		    if(cadena==valor[j])
		    	cubierto=true;
		    else cubierto=false;
		    /*int numInputAttributes  = Attributes.getInputNumAttributes();
    		    int numOutputAttributes = Attributes.getOutputNumAttributes();
    		    int numUndefinedAttributes = Attributes.getNumAttributes();*/
                }
		break;
		}
	}
	return cubierto;	
	
   }
  
    /**
     * <p>
     * Check if the complex gets the given data
     * </p>
     * @param m Muestra The example
     * @return boolean True if gets the example
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
     * Returns the heuristic value if the complex
     * </p>
     * @return double idem
     */
    public double getHeuristica() {
        return heuristica;
    }

    /**
     * <p>
     * Assign a heuristic value(Wracc) to the complex
     * </p>
     * @param heu double the heuristic value
     */
    public void setHeuristica(double heu) {
        heuristica = heu;
    }

    /**
     * <p>
     * Reset the value of the distribution for the complex
     * </p>
     */
    public void borraDistrib() {
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0;
        }
    }

    /**
     * <p>
     * Adds 1 to the n of the example for the class 'clase' matched for the example
     * </p>
     * @param clase int El valor de la clase
     */
    public void incrementaDistrib(int clase) {
        distrib[clase]++;
    }

    /**
     * <p>
     * Returns the value of the distribution for a given class
     * </p>
     * @param clase int The index of the class
     * @return double The value of the distribution
     */
    public int getDistribucionClase(int clase) {
        return distrib[clase];
    }

    /**
     * <p>
     * Returns the value of the distribution
     * </p>
     * @return double [] The value of each distribution
     */
    public int[] getDistribucion() {
        return distrib;
    }

    /**
     * <p>
     * Prints on the screen the content of the complex(Lista -> Attribute operator value)
     * </p>
     */
     
     //RECORDAR QUE LO CAMBIE PARA QUE IMPRIMERA LOS NOMINALES
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
	    String []valoresN=s.getValoresN();
            if (valoresN.length > 1){
                System.out.print(" " + valoresN[0]);
                for (int i = 1; i < valoresN.length - 1; i++) {
                    System.out.print(" ï¿½" + valoresN[i]);
                }
                System.out.print(" ï¿½" + valoresN[valoresN.length - 1] + ")");
            }
            else{
                System.out.print(" " + valoresN[0]+")");
            }
            if (x < compl.size() - 1) {
                System.out.print(" AND ");
            }
        }
        //System.out.print(" -- "+heuristica+" -- ");
    }

    /**
     * <p>
     * Prints on a string the content of the complex(List -> Attribute operator value)
     * </p>
     * @return String Content of the complex
     */
     //RECORDAR QUE LO CAMBIE PARA QUE IMPRIMERA LOS NOMINALES
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
	    String []valoresN=s.getValoresN();
            if (valores.length > 1){
                cad += " " + valoresN[0];
                for (int i = 1; i < valores.length - 1; i++) {
                    cad += " ï¿½" + valoresN[i];
                }
                cad += " ï¿½" + valoresN[valores.length - 1] + "";
            }
            else{
                cad += " " + valoresN[0] + "";
            }
            if (x < compl.size() - 1) {
                cad += " AND ";
            }
        }
        return cad;
    }

    /**
     * <p>
     * Prints on screen the distribution of the classes for the complex
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
     * prints on a string the distribution of the classes for the complex
     * </p>
     * @return String idem
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
     * Do a local copy of the name of the in-put variables
     * </p>
     * @param atributos String[] Stores the name of the variables
     */
    public void adjuntaNombreAtributos(String [] atributos){
        nombreAtributos = new String[atributos.length -1];
        for (int i = 0; i < atributos.length -1; i++){
            nombreAtributos[i] = atributos[i];
        }
    }

}

