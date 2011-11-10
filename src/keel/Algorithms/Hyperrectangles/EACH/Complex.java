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

import java.util.LinkedList;
import keel.Dataset.*;

public class Complex implements Comparable {

    /**
     * <p>
     * This class storages conj. of Selectors
     * </p>
     */
    private LinkedList compl;
    private int classAttribute;
    private int numClasses;
    private int distrib[]; 
    private double heuristic; 
    private String [] attributeNames;
    private double weight;
    private double volume;
    private int numDimensions;

    public Complex() {
    }

    /**
     * <p>
     * Constructor for the complex
     * </p>
     * @param nClass int Number of classes
     */
    public Complex(int nClass) {
        compl = new LinkedList(); //Inicializo la lista
        numClasses = nClass; //Almaceno el n de clases
        distrib = new int[nClass]; //Distribucion para las clases
        for (int i = 0; i < numClasses; i++) {
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
        Complex c2 = (Complex) o;
        int out = 0;

        if (heuristic == c2.getHeuristic() && compl.size() == c2.size()) {
            out = 0;
        } else if (heuristic == c2.getHeuristic() && compl.size() < c2.size()) {
            out = -1;
        } else if (heuristic == c2.getHeuristic() && compl.size() > c2.size()) {
            out = 1;
        } else if (heuristic > c2.getHeuristic()) {
            out = -1;
        } else if (heuristic < c2.getHeuristic()) {
            out = 1;
        }

        return (out);
    }

    /**
     * <p>
     * Check is two complex are equals
     * </p>
     * @param c complex to compare
     * @return boolean True if are equals. False otherwise
     */
    public boolean isEqual(Complex c){
        boolean bEquals = false;
        if (c.size() == compl.size()){
            bEquals = true;
            for (int i = 0; (i < c.size())&&(bEquals); i++){
                bEquals = (this.getSelector(i).compareTo(c.getSelector(i)) == 0);
            }
        }
        return bEquals;
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
     * @param attribute the attribute
     */
    public void removeSelectorAttribute(int attribute) {
    	Selector s;
	int atrib;
        for(int i=0;i<compl.size();i++){
		s=(Selector)compl.get(i);
		atrib=s.getAttribute();
		if(atrib==attribute){
			compl.remove(s);
			/*CUIDADO!!!hay q volver al indice anterior pq se ha eliminado un elemento!!*/
			i=i-1;
		}
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
        return this.numClasses;
    }

    /**
     * Devuelve la clase que define el complejo
     * @return int la clase
     */
    public int getClassAttribute() {
        return this.classAttribute;
    }

    /**
     * <p>
     * Returns the class that defines the comples
     * </p>
     */
    public void setClassAttribute(int clase) {
        this.classAttribute = clase;
    }
    
    public void setWeight(double w){
    	this.weight=w;
    }
    public double getWeight(){
    	return weight;
    }
    
    public void setVolume(double v){
    	this.volume=v;
    }
    public double getVolume(){
    	return volume;
    }

	public void setDimensions(int d){
    	this.numDimensions=d;
    }
    public int getDimensions(){
    	return numDimensions;
    }


    /**
     * <p>
     * Calculate the LaPlaces's value for a complex
     * </p>
     */
    public void computeLaplace() {
      double nc;
      double ntot;
      int i;

      nc = distrib[classAttribute];

      for (i = 0, ntot = 0; i < numClasses; i++) {
        ntot += distrib[i];

      }
      //heuristica = (ntot - nc + nClases - 1) / (ntot + nClases);
      heuristic = (nc + 1) / (ntot + numClasses);

  }

  
    /**
	* <p>
	* Checks if the rule gets the parameter instance
	* </p>
	* @param instance the instance
	* @return boolean True if the rule gets the instance
    */
   public boolean instanceCoveredByRule(Instance instance){
   	boolean bCovered=true;
	double cadena;
   	for (int i = 0; i < this.size()&&bCovered; i++) {
	    Selector s = this.getSelector(i);
            switch (s.getOperator()) {
            case 0: // se trata del igual
	    	double []valor=s.getValues();
		bCovered=false;
		for (int j = 0; (j < valor.length)&&(!bCovered); j++){
		   cadena=instance.getInputRealValues(s.getAttribute());
  		    
		    if(cadena==valor[j])
		    	bCovered=true;
		    else bCovered=false;
		    /*int numInputAttributes  = Attributes.getInputNumAttributes();
    		    int numOutputAttributes = Attributes.getOutputNumAttributes();
    		    int numUndefinedAttributes = Attributes.getNumAttributes();*/
                }
		break;
		}
	}
	return bCovered;
   }
  
   /**
    * <p>
    * Check if the complex gets the given data
    * </p>
    * @param m Muestra The example
    * @return boolean True if gets the example
    */
    public boolean isCovered(Sample m) {
        boolean cubierto = true;
        double[] sample = m.getSample();
        for (int i = 0; i < this.size() && cubierto; i++) { // recorremos los selectores del complejo
            Selector s = this.getSelector(i);
            switch (s.getOperator()) {
            case 0: // se trata del igual
                double [] valor = s.getValues();
                cubierto = false;
		//ahora cada selector solo guarda el minimo y maximo
		//para cuando se trata de una excepcion
		if(valor.length==1){
			if(sample[s.getAttribute()]==valor[0])cubierto=true;
		}
		//ahora ya sabemos que valor solo va a tener 2 componentes
		else{
			if( (sample[s.getAttribute()] >= valor[0]) && (sample[s.getAttribute()] <= valor[1]))  cubierto=true;
		}
                break;
            case 1: // se trata del distinto
                cubierto = sample[s.getAttribute()] != s.getZeroValue();
                break;
            case 2: //menor o igual
                cubierto = sample[s.getAttribute()] <= s.getZeroValue();
                break;
            case 3: //mayor
                cubierto = sample[s.getAttribute()] > s.getZeroValue();
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
    public double getHeuristic() {
        return heuristic;
    }

    /**
     * <p>
     * Assign a heuristic value(Wracc) to the complex
     * </p>
     * @param heu double the heuristic value
     */
    public void setHeuristic(double heu) {
        heuristic = heu;
    }

    /**
     * Resetea el valor de la distribucion para el complejo
     */
    public void removeDistribution() {
        for (int i = 0; i < numClasses; i++) {
            distrib[i] = 0;
        }
    }

    /**
     * Incrementa en 1 el n de ejemplo para la clase 'clase' cubiertas por el complejo
     * @param clase int El valor de la clase
     */
    public void incrementDistribution(int clase) {
        distrib[clase]++;
    }

    /**
     * Devuelve el valor de la distribuciï¿½ para una clase dada
     * @param clase int El indice de la clase
     * @return double El valor de la distribucion
     */
    public int getDistributionClass(int clase) {
        return distrib[clase];
    }

    /**
     * Devuelve el valor de la distribuciï¿½
     * @return double [] El valor de cada distribucion
     */
    public int[] getDistribution() {
        return distrib;
    }

    /**
     * Imprime por pantalla el contenido del complejo (Lista -> Atributo operador valor)
     */
     
    public void print(int nominal) {
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
	    
	    double [] values = s.getValues();
	    String []nValues=s.getNValues();
	    
            //System.out.print("(Atr" + s.getAtributo() + " ");
            System.out.print("(" + attributeNames[s.getAttribute()] + " ");
	    
            switch (s.getOperator()) {
            case 0:
                if(values.length>1 /*|| valoresN.length>1*/)System.out.print("in");
		else System.out.print("=");
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
            
	    if(nominal==0){ //el atributo es nominal
	    	if (nValues.length > 1){
                System.out.print(" " + nValues[0]);
                for (int i = 1; i < nValues.length - 1; i++) {
                    System.out.print(" " + nValues[i]);
                }
                System.out.print(" " + nValues[nValues.length - 1] + ")");
            }
            else{
                System.out.print(" " + nValues[0]+")");
            }
	    }
	    else{
	    	if (values.length > 1){
                System.out.print(" [" + values[0]);
                for (int i = 1; i < values.length - 1; i++) {
                    System.out.print(" " + values[i]);
                }
                System.out.print(" " + values[values.length - 1] + "])");
		}
		else{
			System.out.print(" " + values[0]+")");
		}
	    }
            
            if (x < compl.size() - 1) {
                System.out.print(" AND ");
            }
        //System.out.print(" -- "+heuristica+" -- ");
	System.out.println();
	}
    }

    /**
     * <p>
     * Prints on a String the complex content (List->Attribute)
     * </p>
     */
    public String printString(int []numValues) {

        String cad = "";
        for (int x = 0; x < compl.size(); x++) {
        	Selector s = (Selector) compl.get(x);
            //cad += "Atr" + s.getAtributo() + " ";
        	double [] values = s.getValues();
        	String [] nValues=s.getNValues();
	    //si para un atributo aparecen todos sus valores, la condicion es true
	    //no imprimimos condicion
        	if(values.length<numValues[s.getAttribute()]){
        		
	   	 if ((x	 < (compl.size())) &&(x>0)) {
                	cad += " AND ";
            	}
            cad += attributeNames[s.getAttribute()] + " ";
	    
            switch (s.getOperator()) {
            case 0:
		if(values.length>1)cad += "in";
		else cad += "=";
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
            
	if (Attributes.getInputAttribute(s.getAttribute()).getType() == Attribute.NOMINAL){
	    	if (nValues.length > 1){
                cad += " [" + nValues[0];
                for (int i = 1; i < nValues.length - 1; i++) {
                    cad += " ï¿½" + nValues[i];
                }
                cad += " ,ï¿½" + nValues[nValues.length - 1] + "] ";
		}
		else{
			cad += " " + nValues[0] + "";
		}
	    }
	    else{
	    	if (values.length > 1){
                cad += " [" + values[0];
		 for (int i = 1; i < values.length - 1; i++) {
			cad += " ï¿½" + values[i];
			}
                cad += " , " + values[values.length - 1] + "]";
		}
		else{
			cad += " " + values[0] + "";
		}
	    }
            
        }
	}
        return cad;
    }

    /**
     * <p>
     * Prints on the screen the class distribution of the complex
     * </p>
     */
    public void printDistribution() {
        System.out.print("   [");
        for (int i = 0; i < numClasses; i++) {
            System.out.print(" " + distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * <p>
     * Prints on the screen the class distribution for the complex
     * </p>
     * @return String idem
     */
    public String printDistributionString() {
        String str = new String("   [");
        for (int i = 0; i < numClasses; i++) {
            str += " " + distrib[i];
        }
        str += "]";
        return str;
    }

    /**
     * <p>
     * Do a local copy of the name of the input variables
     * </p>
     * @param atributos String[] an aray that stores the variable's name
     */
    public void addNameAttributes(String [] atributos){
        attributeNames = new String[atributos.length -1];
        for (int i = 0; i < atributos.length -1; i++){
            attributeNames[i] = atributos[i];
        }
    }

}

