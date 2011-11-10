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
 * @author Written by Rosa Venzala 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Riona;

import java.util.LinkedList;
import keel.Dataset.*;

public class Complex implements Comparable {
/**
 * <p>
 * Stores conjunctions of selectors
 * </p>
 */
	
    private LinkedList compl;
    // Class of the rule
    private int classAttribute;
    private int nClasses;
    // Percent
    private int distribution[]; 
    // Heuristic cost
    private double heuristic; 
    private String [] nameAttributes;
    // weight of H
    private double weight;
    // volume of H
    private double volume;
    // dimension of H  hole(0), lina(1), rectangle(2)
    private int nDimensions;

    public Complex() {
    }

    /**
     * Constructor for Complejo
     * @param nClas int number of classes
     */
    public Complex(int nClas) {
        compl = new LinkedList(); //Inicializo la lista
        nClasses = nClas; //Almaceno el n de clases
        distribution = new int[nClas]; //Distribucion para las clases
        for (int i = 0; i < nClasses; i++) {
            distribution[i] = 0; //Inicializo a 0
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
        Complex c2 = (Complex) o;
        int sal = 0;

        if (heuristic == c2.getHeuristic() && compl.size() == c2.size()) {
            sal = 0;
        } else if (heuristic == c2.getHeuristic() && compl.size() < c2.size()) {
            sal = -1;
        } else if (heuristic == c2.getHeuristic() && compl.size() > c2.size()) {
            sal = 1;
        } else if (heuristic > c2.getHeuristic()) {
            sal = -1;
        } else if (heuristic < c2.getHeuristic()) {
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
    public boolean isEqual(Complex c){
        boolean bEquals = false;
        if (c.size() == compl.size()){
            bEquals = true;
            for (int i = 0; (i < c.size())&&(bEquals); i++){
                bEquals = (this.getSelector(i).equivalents(c.getSelector(i)) == 0);
            }
	    if(bEquals && (classAttribute!=c.getClassAttribute()))bEquals=false;
        }
	if(c.size()>compl.size()){
		bEquals = true;
            	for (int i = 0; (i < compl.size())&&(bEquals); i++){
                bEquals = (this.getSelector(i).equivalents(c.getSelector(i)) == 0);
            }
	    if(bEquals && (classAttribute!=c.getClassAttribute()))bEquals=false;
	}
        return bEquals;
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
     * Drop the selector of the list selectors
     * </p>
     * @param s the selector (set atr. op. value)
     */
    public void removeSelector(Selector s) {
        compl.remove(s);
    }
    
    /**
     * <p>
     * Borra los selectores de la lista de selectores que tengan como atributo el pasado
     * como argumento
     * </p>
     * @param attribute el atributo
     */
    public void removeSelectorAttribute(int attribute) {
    	Selector s;
	int atrib;
        for(int i=0;i<compl.size();i++){
		s=(Selector)compl.get(i);
		atrib=s.getAttribute();
		if(atrib==attribute){compl.remove(s);
		i=i-1;}
	}
    }
    
    /**
     * <p>
     * Cleans the list
     * </p>
     */
    public void clear() {
        compl.clear();
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
        return this.nClasses;
    }

    /**
     * <p>
     * Return the class that define the complex
     * </p>
     * @return int the class
     */
    public int getClassAttribute() {
        return this.classAttribute;
    }

    /**
     * <p>
     * Gives the value of the class to the complex
     * </p>
     * @param clase int The class
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
    	this.nDimensions=d;
    }
    public int getDimensions(){
    	return nDimensions;
    }


    /**
     * <p>
     * Calculate the value of laplace for a complex
     * </p>
     */
    public void computeLaPlace() {
      double nc;
      double ntot;
      int i;

      nc = distribution[classAttribute];

      for (i = 0, ntot = 0; i < nClasses; i++) {
        ntot += distribution[i];

      }
      heuristic = (nc + 1) / (ntot + nClasses);
  }

  
  /**
   * <p>
   * Check if the rule match with the parameter instance
   * </p>
   * @param ejemplo The instance
   * @return boolean True if match
  */
   public boolean ruleCoversInstance(double []ejemplo/*Instance instancia*/){
   	boolean bCovered=true;
   	for (int i = 0; i < this.size()&&bCovered; i++) {
	    Selector s = this.getSelector(i);
            switch (s.getOperator()) {
            case 0: // se trata del igual
	    	double []valores=s.getValues();
		bCovered=false;
		    if (Attributes.getInputAttribute(s.getAttribute()).getType() == Attribute.NOMINAL){
		    for (int j = 0; (j < valores.length)&&(!bCovered); j++){
		    	if(ejemplo[s.getAttribute()]==valores[j])bCovered=true;
		    }
		    }
		    else {if( (ejemplo[s.getAttribute()] >= valores[0]) && (ejemplo[s.getAttribute()] <= valores[1]))  bCovered=true;}
		break;
		}
	}
	return bCovered;
   }
  

    /**
     * <p>
     * return the heuristic value of the complex
     * </p>
     * @return double heuristic value of the complex
     */
    public double getHeuristic() {
        return heuristic;
    }

    /**
     * <p>
     * Assign a heuristic value (Wracc) to the complex
     * </p>
     * @param heuristic heuristic value
     */
    public void setHeuristic(double heuristic) {
        this.heuristic = heuristic;
    }

    /**
     * <p>
     * reset the distribution value for the complex
     * </p>
     */
    public void removeDistribution() {
        for (int i = 0; i < nClasses; i++) {
            distribution[i] = 0;
        }
    }

    /**
     * <p>
     * Add one to the n of the complex for the class
     * </p>
     * @param classAttribute int value of the class
     */
    public void incrementDistribution(int classAttribute) {
        distribution[classAttribute]++;
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @param classAttribute int index of the class
     * @return double value of distribution
     */
    public int getDistributionClass(int classAttribute) {
        return distribution[classAttribute];
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @return double [] the value of each distribution
     */
    public int[] getDistribution() {
        return distribution;
    }

    /**
     * <p>
     * Print the content of the complex (List->Attribute operator value)
     * </p>
     */
    public void print() {
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
	    
	    double [] values = s.getValues();
	    String []valuesN=s.getNValues();
	    
            System.out.print("(" + nameAttributes[s.getAttribute()] + " ");
	    
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
            
	    if (Attributes.getInputAttribute(s.getAttribute()).getType() == Attribute.NOMINAL){
	    	if (valuesN.length > 1){
                System.out.print(" {" + valuesN[0]);
                for (int i = 1; i < valuesN.length - 1; i++) {
                    System.out.print(", " + valuesN[i]);
                }
                System.out.print(", " + valuesN[valuesN.length - 1] + "})");
            }
            else{
                System.out.print(" " + valuesN[0]+")");
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
	System.out.println("-> "+classAttribute);
	}
    }

    /**
     * <p>
     * Print the complex content using a string (List->Attribute operator value)
     * </p>
     * @return String string with the content of the complex
     */
    public String printString(int []numValues) {

        String str = "";
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
	    double [] values = s.getValues();
	    String []valuesN=s.getNValues();

            str += nameAttributes[s.getAttribute()] + " ";
	    
            switch (s.getOperator()) {
            case 0:

		if (Attributes.getInputAttribute(s.getAttribute()).getType() == Attribute.NOMINAL)
			str += "=";
		else str += "in";
                break;
            case 1:
                str += "<>";
                break;
            case 2:
                str += "<=";
                break;
            case 3:
                str += ">";
                break;
            }
            
	if (Attributes.getInputAttribute(s.getAttribute()).getType() == Attribute.NOMINAL){

			str += " " + valuesN[0] + "";

	    }
	    else{
	    	if (values.length > 1){
                str += " [" + values[0];
		 for (int i = 1; i < values.length - 1; i++) {
			str += " ï¿½" + values[i];
			}
                str += " , " + values[values.length - 1] + "]";
		}
		else{
			str += " " + values[0] + "";
		}
	    }
	    
            if (x < compl.size() - 1) {
                str += " AND ";
            }
	}
	if(compl.size()==0)str+="true";
        return str;
    }

    /**
     * <p>
     * Print the classes distribution for the complex
     * </p>
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClasses; i++) {
            System.out.print(" " + distribution[i]);
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
        String str = new String("   [");
        for (int i = 0; i < nClasses; i++) {
            str += " " + distribution[i];
        }
        str += "]";
        return str;
    }

    /**
     * <p>
     * Local copy of the name of variables
     * </p>
     * @param attributes String[] stores the name of the variables
     */
    public void adjuntNameAttributes(String [] attributes){
        nameAttributes = new String[attributes.length -1];
        for (int i = 0; i < attributes.length -1; i++){
            nameAttributes[i] = attributes[i];
        }
    }
    
    
    /**
     * <p>
     * Return an array of size numAttributes.
     * Each content 1 if selector exists, 0 otherwise
     * </p>
     */
    public int []existingSelectors(){
    	int []conditions=new int[nameAttributes.length];
    	for(int i=0;i<nameAttributes.length;i++){
    		conditions[i]=0;
    		for(int j=0;j<compl.size();j++){
    			Selector s;
    		    s=(Selector) compl.get(j);
    		    int at=s.getAttribute();
    		    conditions[at]=1;
    		    
    		}	
    	}
    	return conditions;
    }

}

