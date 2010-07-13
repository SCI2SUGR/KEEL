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
 * <p>Titulo: Regla</p>
 *
 * <p>Descripcion: Contiene la estructura de datos de una regla en particular</p>
 *
 * <p>Copyright: Alberto Copyright (c) 2006</p>
 *
 * <p>Compañia: Mi casa</p>
 *
 * @author Alber
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
     * Compara dos reglas
     * @param o Object Regla a comparar
     * @return int 0 si las reglas son iguales. 1 si esta regla es mejor. -1 si es peor
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
     * Constructor por defecto. Asigna el valor 0 a la regla 0
     */
    public Regla() {
        antecedente = new int[1];
        antecedente[0] = 0;
        clase = 0;
    }

    /**
     * Constructor
     * @param tam int Tamaño del antecedente
     * @param nClases int Numero de clases del problema
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
     * Constructor con parametros a partir de un item
     * @param item Item El item para formar la regla
     * @param nClases int Numero de clases del problema
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
     * Copia una regla
     * @return r Regla ya copiada
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
     * Añade un atributo a la regla
     * @param i int Posicion dentro del antecedente
     * @param a int El valor del atributo
     */
    public void addAtributo(int i, int a) {
        antecedente[i] = a;
    }

    /**
     * Añade un atributo a la regla
     * @param i int Posicion dentro del array
     * @param pos int Posicion del atributo
     */
    public void addPosicion(int i, int pos) {
        posiciones[i] = pos;
    }


    /**
     * Devuelve el atributo i-esimo
     * @param i int Posicion dentro de la regla
     * @return double El atributo devuelto del antecedente
     */
    public int getAtributo(int i) {
        return antecedente[i];
    }

    /**
     * Devuelve el tamaño de la regla (nº atributos en el antecedente)
     * @return int El tamaño de la regla
     */
    public int size() {
        return (antecedente.length - 1);
    }

    /**
     * @return la clase
     */
    public int getClase() {
        return clase;
    }

    /**
     * @param i valor para la clase
     */
    public void setClase(int i) {
        clase = i;
    }

    /**
     * Inserta el valor de support para esta regla
     * @param s int El valor
     */
    public void setSupport(int s) {
        support = s;
    }

    /**
     * Devuelve el valor de support para esta regla
     * @return int El valor C
     */
    public int getSupport() {
        return support;
    }

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
     * Imprime la regla
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
     * Imprime la regla
     * @return String La regla escrita como conocemos
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
     * Imprime el support de la regla
     * @return String Una cadena indicando el support
     */
    public String printSupport(){
        String cad = new String("-- Support: " + support);
        return cad;
    }

    /**
     * Realiza un reajuste de los valores de los atributos de la regla
     * @param cambio int[] Los nuevos valores (0 -> X, 1 -> Y...)
     */
    public void ajusta(int[] cambio) {
        for (int i = 0; i < antecedente.length; i++) {
            antecedente[i] = cambio[antecedente[i]];
        }
        clase = cambio[clase];
    }

    /**
     * Realizamos una copia local del nombre de las variables de entrada
     * @param atributos String[] un Array que guarda el nombre de cada variable
     */
    public void adjuntaNombreAtributos(String [] atributos){
        nombreAtributos = new String[atributos.length];
        for (int i = 0; i < atributos.length; i++){
            nombreAtributos[i] = atributos[i];
        }
    }

    /**
     * Ajusta el valor de la heurística WRAcc a un nuevo valor
     * @param heu double El nuevo valor de la heurística para la regla
     */
    public void setHeuristicaWRAcc(double heu) {
        heuristica = heu;
    }

    /**
     * Devuelve el valor heurístico WRAcc de la regla
     * @return double el valor heuristico
     */
    public double getHeuristica() {
        return heuristica;
    }

    /**
     * Inserta una distribucion dada
     * @param distribucion int[] los valores de la distribucion
     */
    public void setDistrib(int [] distribucion){
        for (int i = 0; i < distribucion.length; i++){
            this.distrib[i] = distribucion[i];
        }
    }

    /**
     * Devuelve el valor de la distribucion de la regla dada
     * @return int[] El nº de ejemplos de train que cubre la regla para cada clase
     */
    public int[] getDistribucion() {
        int ret[] = new int[nClases];
        for (int i = 0; i < nClases; i++) {
            ret[i] = distrib[i];
        }
        return ret;
    }

    /**
     * Devuelve la distribucion para una clase concreta
     * @param i int El identificador de la clase
     * @return int El nº de ejemplos cubiertos por la regla
     */
    public int getDistribucionClase(int i) {
        return distrib[i];
    }

    /**
     * Aunmenta en 1 los ejemplos cubiertos para la clase i
     * @param i int Indice de clase
     */
    public void incremDistribClase(int i) {
      distrib[i] ++;
  }

    /**
     * Imprime la distribución de la regla
     */
    public void printDistribucion() {
        System.out.print("   [");
        for (int i = 0; i < nClases; i++) {
            System.out.print(" "+distrib[i]);
        }
        System.out.print("]");
    }

    /**
     * Guarda la distribución como un String
     * @return String La distribucion ("visual")
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

