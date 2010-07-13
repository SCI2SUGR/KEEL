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

package keel.Algorithms.Genetic_Rule_Learning.apriori;

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
    private double confidence;
    private String [] nombreAtributos;

    /**
     * Compara dos reglas
     * @param o Object Regla a comparar
     * @return int 0 si las reglas son iguales. 1 si esta regla es mejor. -1 si es peor
     */
    public int compareTo(Object o) {
        Regla r2 = (Regla) o;
        int sal = 0;

        /*
         if (support == r2.getSupport() && this.size() == r2.size()) { //Iguales
          sal = 0;
               }
               else if (support == r2.getSupport() && this.size() < r2.size()) { //Mismo support pero es mas pequeña (mejor)
          sal = 1;
               }
               else if (support == r2.getSupport() && this.size() > r2.size()) { //mismo support mas grande (peor)
          sal = -1;
               }
               else if (support > r2.getSupport()) { //Mejor por el support
          sal = 1;
               }
               else if (support < r2.getSupport()) { //Peor por el support
          sal = -1;
               }
         */
        /*
         if (support == r2.getSupport() && this.getConf() == r2.getConf()) { //Iguales
            sal = 0;
                 } else if (support == r2.getSupport() && this.getConf() > r2.getConf()) { //Mismo support pero mayor confidence (mejor)
            sal = 1;
                 } else if (support == r2.getSupport() && this.getConf() < r2.getConf()) { //mismo support menor confidence (peor)
            sal = -1;
                 } else if (support > r2.getSupport()) { //Mejor por el support
            sal = 1;
                 } else if (support < r2.getSupport()) { //Peor por el support
            sal = -1;
                 }
         */
        if (support == r2.getSupport() && this.getConf() == r2.getConf()) { //Iguales
            sal = 0;
        } else if (this.getConf() > r2.getConf()) { //Mismo support pero mayor confidence (mejor)
            sal = 1;
        } else if (this.getConf() < r2.getConf()) { //mismo support menor confidence (peor)
            sal = -1;
        } else if (this.getConf() == r2.getConf() && support > r2.getSupport()) { //Mejor por el support
            sal = 1;
        } else if (this.getConf() == r2.getConf() && support < r2.getSupport()) { //Peor por el support
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
        //clase = 0;
    }

    /**
     * Constructor
     * @param tam int Tamaño del antecedente
     */
    public Regla(int tam) {
        antecedente = new int[tam];
        posiciones = new int[tam];
        //clase = 0;
    }

    /**
     * Constructor con parametros a partir de un item
     * @param item Item El item para formar la regla
     */
    public Regla(Item item) {
        posiciones = item.getColumnas();
        antecedente = item.getItem();
        this.clase = antecedente[antecedente.length - 1];
        support = item.getSupport();
    }

    /**
     * Copia una regla
     * @return r Regla ya copiada
     */
    public Regla copiaRegla() {
        Regla r = new Regla(antecedente.length);
        for (int i = 0; i < antecedente.length; i++) {
            r.addAtributo(i, antecedente[i]);
            r.addPosicion(i, posiciones[i]);
        }
        r.setClase(this.getClase());
        r.setSupport(this.support);
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
        return this.clase;
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

    /**
     * Asigna un valor al confidence
     * @param c double El valor mencionado
     */
    public void setConf(double c) {
        this.confidence = c;
    }

    /**
     * Devuelve el valor del confidence para el regla
     * @return double confidence
     */
    public double getConf() {
        return this.confidence;
    }

    /**
     * Comprueba si la regla cubre a una muestra dada
     * @param m Muestra Ejemplo a comprobar
     * @return boolean True si la regla cubre al ejemplo m. False en caso contrario
     */
    public boolean cubre(Muestra m) {
        boolean resp = true;
        int i;
        double instancia[] = m.getMuest();
        for (i = 0; i < this.size() && resp; i++) { // recorremos los valores del antecedente
            resp = ((instancia[posiciones[i]] == antecedente[i]) ||
                    (Double.isNaN(instancia[posiciones[i]]))); //El valor de la instancia para ese atributo no coincide
            //La regla no cubre al ejemplo!
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
        System.out.print("-- Support: " + support);
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

}

