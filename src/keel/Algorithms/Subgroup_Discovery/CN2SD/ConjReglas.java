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

import java.util.*;

/**
 * <p>Title: Clase Conjunto de Reglas</p>
 *
 * <p>Description: Define un conjunto de reglas o complejos</p>
 *
 * <p>Copyright: Copyright Alberto (c) 2006</p>
 *
 * <p>Company: Mi Casa </p>
 *
 * @author Alberto
 * @version 1.0
 */
public class ConjReglas {

    private LinkedList reglas;
    private String nombreClase;
    private String[] valorNombreClases;

    /**
     * Constructor
     */
    public ConjReglas() {
        reglas = new LinkedList();
    }

    /**
     * Añade todo un conjunto de reglas a la lista
     * @param r ConjReglas El conjunto de reglas
     */
    public void addReglas(ConjReglas r) {
        for (int i = 0; i < r.size(); i++) {
            Complejo regla = r.getRegla(i);
            reglas.add(regla);
        }
    }

    /**
     * Añade una regla a la lista
     * @param regl Regla a añadir
     */
    public void addRegla(Complejo regl) {
        reglas.add(regl);
    }

    /**
     * Elimina una regla de la lista
     * @param i indice de la regla a eliminar
     */
    public void deleteRegla(int i) {
        reglas.remove(i);
    }

    /**
     * Vacía el contenido del conjunto de reglas
     */
    public void deleteAll() {
        reglas.removeAll(reglas);
    }

    /**
     * Devuelve una regla de la lista
     * @param i indice de la regla
     * @return la regla i-esima
     */
    public Complejo getRegla(int i) {
        return (Complejo) reglas.get(i);
    }

    /**
     * Devuelve el numero de reglas con las que estamos trabajando
     * @return El tamaño del conjunto de reglas
     */
    public int size() {
        return (reglas.size());
    }

    /**
     * Devuelve el conjunto completo de reglas
     * @return el conjunto completo de reglas
     */
    public LinkedList getConjReglas() {
        return reglas;
    }

    /**
     * Muestra por pantalla el conjunto de reglas
     */
    public void print() {
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            System.out.print("\nRule " + (i+1) + ": IF  ");
            c.print();
            System.out.print(" THEN "+nombreClase+" -> " + c.getClase() + "  ");
            c.printDistribucion();
        }
    }

    /**
     * Imprime en una cadena el conjunto de reglas
     * @return una cadena de texto (string) que almacena el conjunto de reglas
     */
    public String printString() {
        String cad = "";
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            cad += "\nRule " + (i+1) + ": IF  ";
            cad += c.printString();
            cad += " THEN "+nombreClase+ " -> " + valorNombreClases[c.getClase()] + "  ";
            cad += c.printDistribucionString();
        }
        return cad;
    }

    /**
     * Devuelve la última regla (normalmente aquella con mejor peso)
     * @return la última regla de a lista
     */
    public Complejo getUltimaRegla() {
        return (Complejo) reglas.getLast();
    }

    /**
     * Se encarga de eliminar complejos con atributos repetidos
     */
    public void eliminaNulos() {
        boolean salir;
        for (int i = 0; i < this.size(); i++) {
            Complejo aux = this.getRegla(i);
            salir = false;
            for (int j = 0; (j < aux.size() - 1) && (!salir); j++) {
                Selector s = aux.getSelector(j);
                for (int h = j + 1; (h < aux.size()) && (!salir); h++) {
                    Selector s2 = aux.getSelector(h);
                    if (s.compareTo(s2) < 2) { //mismo atributo
                        this.deleteRegla(i); //borrando
                        salir = true;
                        i--;
                    }
                }
            }
        }
    }

    /**
     * Eliminamos aquellos complejos repetidos (at1 = 0 ^ at2 = 0 -- at2 = 0 ^ at1 = 0)
     * @param tam Tamaño de las estrella
     */
    public void eliminaRepetidos(int tam) {
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size()) && (seguir); j++) {
                Complejo aux2 = this.getRegla(j);
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
                                this.deleteRegla(i); //borro porque está repe totalmente
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
     * Elimino reglas que sean semánticamente iguales (At = 1, At <> 0, At = [0,1])
     * @param tam int Tamaño de la estrella
     */
    public void eliminaSubsumidos(int tam) {
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size()) && (seguir); j++) {
                Complejo aux2 = this.getRegla(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if ((s.compareTo(s2) == -3) || (s.compareTo(s2) == 0)) { //mirar compareTo en Selector
                            salir = true; //paso a ver el siguiente selector (si eso)
                            if ((l == aux.size() - 1) &&
                                (aux.getDistribucionClaseEj(0) ==
                                 aux2.getDistribucionClaseEj(0))) {
                                //if (l == aux.size() - 1) {
                                /*
                                 System.out.println("\nEstos son los complejos subsumidos:");
                                                                 aux.print();
                                                                 aux2.print();
                                 */
                                seguir = false;
                                this.deleteRegla(i); //tienen los mismos atributos y misma distribucion (son semanticament =)
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
     * Realizamos una copia local del nombre de la variable clase
     * @param nombreClase String nombre de la clase
     */
    public void adjuntaNombreClase(String nombreClase){
        this.nombreClase = nombreClase;
    }

    /**
     * Realizamos una copia local del nombre de las valores de la clase
     * @param clases String[] un Array que guarda el nombre de valor de la clase
     */
    public void adjuntaNombreClases(String [] clases){
        valorNombreClases = new String[clases.length];
        for (int i = 0; i < clases.length; i++){
            valorNombreClases[i] = clases[i];
        }
    }

}

