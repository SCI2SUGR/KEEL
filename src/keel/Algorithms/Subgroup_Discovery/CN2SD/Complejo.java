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
 * <p>Title: Clase Complejo</p>
 *
 * <p>Description: Define un Complex (o regla)</p>
 *
 * <p>Copyright: Copyright Alberto (c) 2006</p>
 *
 * <p>Company: Mi Casa </p>
 *
 * @author Alberto Fernández
 * @version 1.0
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

    public Complejo() {
    }

    /**
     * Constructor para el Complejo
     * @param nClas int Número de clases
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
     * Compara dos objetos de la clase Complejo
     * @param o Object Complejo a comparar
     * @return int 0 si son iguales (misma heuristica y tamaño), -1 si es mejor (misma heur, menor tamaño || mayor heur)
     * 1 si es peor (misma heur, mayor tamaño || menor heuristica).
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
     * Añade el selector a la lista de selectores
     * @param s Selector el selector (conjunto atr. op. valor)
     */
    public void addSelector(Selector s) {
        compl.add(s);
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
     * Devuelve el tamaño del complejo
     * @return int El número de selectores que posee el complejo
     */
    public int size() {
        return compl.size();
    }

    /**
     * Devuelve el nº de clases del problema
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
     * Devuelve el valor heurístico del complejo
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
            distribEj[i] = 0;
        }
    }

    /**
     * Incrementa en 1 el nº de ejemplo para la clase 'clase' cubiertas por el complejo
     * @param clase int El valor de la clase
     */
    public void incrementaDistrib(int clase) {
        distribEj[clase]++;
    }

    /**
     * Convierte la distribución en un valor entre 0 y 1
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
     * Devuelve el valor de la distribución para una clase dada
     * @param clase int El indice de la clase
     * @return double El valor de la distribucion
     */
    public double getDistribucionClase(int clase) {
        return distrib[clase];
    }

    /**
     * Devuelve el valor de la distribución
     * @return double [] El valor de cada distribucion
     */
    public double[] getDistribucion() {
        return distrib;
    }

    /**
     * Devuelve el valor de la distribución para una clase dada
     * @param clase int El indice de la clase
     * @return int El valor de la distribucion
     */
    public int getDistribucionClaseEj(int clase) {
        return distribEj[clase];
    }

    /**
     * Devuelve el valor de la distribución
     * @return int [] El valor de cada distribucion
     */
    public int[] getDistribucionEj() {
        return distribEj;
    }


    /**
     * Imprime por pantalla el contenido del complejo (Lista -> Atributo operador valor)
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
     * Imprime en una cadena de caracteres el contenido del complejo (Lista -> Atributo operador valor)
     * @return String La cadena con el contenido del complejo
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
     * Imprime por pantalla la distribución de clases para el complejo
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClases; i++) {
            System.out.print(" " + distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * Imprime en un String la distribución de clases para el complejo
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

