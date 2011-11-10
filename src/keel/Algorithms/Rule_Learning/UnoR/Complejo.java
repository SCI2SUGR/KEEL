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
 * <p>Title: Clase Complejo</p>
 *
 * <p>Description: Define un Complex (o regla)</p>
 *
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 *
 * <p>Company: Mi Casa </p>
 *
 * @author Rosa Venzala
 * @version 1.0
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

    public Complejo() {
    }

    /**
     * Constructor para el Complejo
     * @param nClas int Nmero de clases
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
     * Compara dos objetos de la clase Complejo
     * @param o Object Complejo a comparar
     * @return int 0 si son iguales (misma heuristica y tamaï¿½), -1 si es mejor (misma heur, menor tamaï¿½ || mayor heur)
     * 1 si es peor (misma heur, mayor tamaï¿½ || menor heuristica).
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
     * Comprueba si dos complejos son iguales
     * @param c Complejo El complejo a comparar
     * @return boolean True si son iguales. False en caso contrario
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
     * Aï¿½de el selector a la lista de selectores
     * @param s Selector el selector (conjunto atr. op. valor)
     */
    public void addSelector(Selector s) {
        compl.add(s);
    }
    
    
    /**
     * Borra el selector de la lista de selectores
     * @param s Selector el selector (conjunto atr. op. valor)
     */
    public void removeSelector(Selector s) {
        compl.remove(s);
    }
    
    /**
     * Borra los selectores de la lista de selectores que tengan como atributo el pasado
     *como argumento
     * @param atributo el atributo
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
     * Deja vacia la lista
     */
    public void clear() {
        compl.clear();
    }
    
    
    

    /**
     * Devuelve un selector en una posicion dada del complejo
     * @param indice int Posicion dentro del complejo
     * @return Selector El selector
     */
    public Selector getSelector(int indice) {
        return (Selector) compl.get(indice);
    }

    /**
     * Devuelve el tamaï¿½ del complejo
     * @return int El nmero de selectores que posee el complejo
     */
    public int size() {
        return compl.size();
    }

    /**
     * Devuelve el n de clases del problema
     * @return int idem.
     */
    public int getNClases() {
        return this.nClases;
    }

    /**
     * Devuelve la clase que define el complejo
     * @return int la clase
     */
    public int getClase() {
        return this.clase;
    }

    /**
     * Proporciona el valor de la clase al complejo
     * @param clase int La clase
     */
    public void setClase(int clase) {
        this.clase = clase;
    }

    /**
     * Calcula el valor del laplaciano para un complejo
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
    *Comprueba si la regla cubre a la instancia pasada como parametro
    *@param instancia La instancia
    *@return boolean True si la regla cubre a la instancia
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
     * Comprueba si el complejo cubre a la muestra dada
     * @param m Muestra El ejemplo
     * @return boolean True si cubre al ejemplo. False en otro caso
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
		//AÃ‘ADIMOS LA SIGUIENTE CONDICION PARA CUANDO EL VALOR EXACTO NO SE ENCUENTRA
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
     * Devuelve el valor heurï¿½tico del complejo
     * @return double idem
     */
    public double getHeuristica() {
        return heuristica;
    }

    /**
     * Asigna un valor heuristico (Wracc) al complejo
     * @param heu double el valor heuristico
     */
    public void setHeuristica(double heu) {
        heuristica = heu;
    }

    /**
     * Resetea el valor de la distribucion para el complejo
     */
    public void borraDistrib() {
        for (int i = 0; i < nClases; i++) {
            distrib[i] = 0;
        }
    }

    /**
     * Incrementa en 1 el n de ejemplo para la clase 'clase' cubiertas por el complejo
     * @param clase int El valor de la clase
     */
    public void incrementaDistrib(int clase) {
        distrib[clase]++;
    }

    /**
     * Devuelve el valor de la distribuciï¿½ para una clase dada
     * @param clase int El indice de la clase
     * @return double El valor de la distribucion
     */
    public int getDistribucionClase(int clase) {
        return distrib[clase];
    }

    /**
     * Devuelve el valor de la distribuciï¿½
     * @return double [] El valor de cada distribucion
     */
    public int[] getDistribucion() {
        return distrib;
    }

    /**
     * Imprime por pantalla el contenido del complejo (Lista -> Atributo operador valor)
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
     * Imprime en una cadena de caracteres el contenido del complejo (Lista -> Atributo operador valor)
     *@param nominal indica si el atributo es nominal(0) o numerico(1)
     *@param inf el extremo inferior del intervalo
     *@param sup el extremo superior del intervalo
     *@param ultima indica si se trata de la ultima regla del conjunto de reglas
     * @return String La cadena con el contenido del complejo
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
                    cad += " ï¿½" + valoresN[i];
                }
                cad += " ï¿½" + valoresN[valoresN.length - 1] + "";
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
			cad += " ï¿½" + valores[i];
			}*/
		if(ultima)
                cad += " , ï¿½" + sup/*valores[valores.length - 1]*/ + "]";
		else
		cad += " , ï¿½" + sup/*valores[valores.length - 1]*/ + ")";
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
     * Imprime por pantalla la distribuciï¿½ de clases para el complejo
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClases; i++) {
            System.out.print(" " + distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * Imprime en un String la distribuciï¿½ de clases para el complejo
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
     * Realizamos una copia local del nombre de las variables de entrada
     * @param atributos String[] un Array que guarda el nombre de cada variable
     */
    public void adjuntaNombreAtributos(String [] atributos){
        nombreAtributos = new String[atributos.length -1];
        for (int i = 0; i < atributos.length -1; i++){
            nombreAtributos[i] = atributos[i];
        }
    }

}

