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

package keel.Algorithms.Rule_Learning.UnoR;

import java.util.LinkedList;
import keel.Dataset.*;

/**
 * <p> Stores conjunctions of selectors
 * @author Written by Rosa Venzala 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
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
    private int distrib[]; //contiene porcentaje n muestras por clase que satisfacen antecedente
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
        nClases = nClas; //Almaceno el n de clases
        distrib = new int[nClas]; //Distribucion para las clases
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0; //Inicializo a 0
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
     * Drop the selector of the list selectors
     * </p>
     * @param s the selector (set atr. op. value)
     */
    public void removeSelector(Selector s) {
        compl.remove(s);
    }
    
    /**
     * <p>
     * Removes the selectors that have the attribute given as argument from the proper list
     * </p>
     * @param atributo given attribute.
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
        return this.nClases;
    }

    /**
     * <p>
     * Return the class that define the complex
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
     * @param clase int The class
     */
    public void setClase(int clase) {
        this.clase = clase;
    }

    /**
     * Computes the laplacian value of the complex.
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
   * Checks if the rule covers the given instance.
    *@param instancia given instance.
    *@return boolean True if the rule covers the instances, false otherwise.
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
			/*System.out.println("en la instancia es "+cadena);
			System.out.println("y en el selector  "+valor[j]);*/
  		    
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
   * Checks if the complex covers the given sample.
    *@param m given sample.
    *@return boolean True if the complex covers the instances, false otherwise.
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
		//AÃADIMOS LA SIGUIENTE CONDICION PARA CUANDO EL VALOR EXACTO NO SE ENCUENTRA
		//EN LA LISTA PERO AL ESTAR ENTRE LOS EXTREMOS TAMBIEN PERTENECE AL INTERVALO
		if(!cubierto){
			if((ejemplo[s.getAtributo()]>=valor[0]) && (ejemplo[s.getAtributo()]<=valor[valor.length-1]))cubierto=true;
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
            distrib[i] = 0;
        }
    }

    /**
     * <p>
     * Add one to the n of the complex for the class
     * </p>
     * @param clase int value of the class
     */
    public void incrementaDistrib(int clase) {
        distrib[clase]++;
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @param clase int index of the class
     * @return double value of distribution
     */
    public int getDistribucionClase(int clase) {
        return distrib[clase];
    }

    /**
     * <p>
     * Return the value of the distribution
     * </p>
     * @return double [] the value of each distribution
     */
    public int[] getDistribucion() {
        return distrib;
    }

    /**
     * <p>
     * Print the content of the complex (List->Attribute operator value)
     * </p>
     * @param nominal if 0 is nominal.
     */
    public void print(int nominal) {
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
	    
	    double [] valores = s.getValores();
	    String []valoresN=s.getValoresN();
	    
            //System.out.print("(Atr" + s.getAtributo() + " ");
            System.out.print("(" + nombreAtributos[s.getAtributo()] + " ");
            switch (s.getOperador()) {
            case 0:
                if(valores.length>1 /*|| valoresN.length>1*/)System.out.print("in");
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
	    	if (valoresN.length > 1){
                System.out.print(" " + valoresN[0]);
                for (int i = 1; i < valoresN.length - 1; i++) {
                    System.out.print(" " + valoresN[i]);
                }
                System.out.print(" " + valoresN[valoresN.length - 1] + ")");
            }
            else{
                System.out.print(" " + valoresN[0]+")");
            }
	    }
	    else{
	    	if (valores.length > 1){
                System.out.print(" [" + valores[0]);
                for (int i = 1; i < valores.length - 1; i++) {
                    System.out.print(" " + valores[i]);
                }
                System.out.print(" " + valores[valores.length - 1] + "])");
		}
		else{
			System.out.print(" " + valores[0]+")");
		}
	    }
            
            if (x < compl.size() - 1) {
                System.out.print(" AND ");
            }
        }
        //System.out.print(" -- "+heuristica+" -- ");
	System.out.println();
    }

    /**
     * Returns a string with the complex contents (List -> Attribute operator value).
     * @param nominal indicate if the attribute is nominal(0) or numeric(1).
     *@param inf initial value of the interval
     *@param sup end value of the interval
     *@param ultima indicates if it is the last rule of the set.
     * @return a string with the complex contents 
     */
    public String printString(int nominal,double inf,double sup,boolean ultima) {
        String cad = "";
        for (int x = 0; x < compl.size(); x++) {
            Selector s = (Selector) compl.get(x);
            //cad += "Atr" + s.getAtributo() + " ";
            cad += nombreAtributos[s.getAtributo()] + " ";
	    double [] valores = s.getValores();
	    String []valoresN=s.getValoresN();
            switch (s.getOperador()) {
            case 0:
		if(valores.length>1 /*|| valoresN.length>1*/)cad += "in";
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
            
	    if(nominal==0){
	    	if (valoresN.length > 1){
                cad += " " + valoresN[0];
                for (int i = 1; i < valoresN.length - 1; i++) {
                    cad += " , " + valoresN[i];
                }
                cad += " , " + valoresN[valoresN.length - 1] + "";
		}
		else{
			cad += " " + valoresN[0] + "";
		}
	    }
	    else{
	    	//No imprimimos todo el intervalo, sino solo los extremos inferior y superior
		//Necesitamos los parametros inf y sup pq estamos agrupando las reglas
		// consecutivas que tienen la misma clase
	    	if (valores.length > 1){
                cad += " [" + inf/*valores[0]*/;
		 /*for (int i = 1; i < valores.length - 1; i++) {
			cad += " , " + valores[i];
			}*/
		if(ultima)
                cad += " , " + sup/*valores[valores.length - 1]*/ + "]";
		else
		cad += " , " + sup/*valores[valores.length - 1]*/ + ")";
		}
		else{
			cad += " " + valores[0] + "";
		}
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

