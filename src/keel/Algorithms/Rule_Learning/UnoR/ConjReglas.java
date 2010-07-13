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

import java.util.*;

/**
 * <p>Title: Clase Conjunto de Reglas</p>
 *
 * <p>Description: Define un conjunto de reglas o complejos</p>
 *
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 *
 * <p>Company: Mi Casa </p>
 *
 * @author Rosa Venzala
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
     * Aï¿½de todo un conjunto de reglas a la lista
     * @param r ConjReglas El conjunto de reglas
     */
    public void addReglas(ConjReglas r) {
        for (int i = 0; i < r.size(); i++) {
            Complejo regla = r.getRegla(i);
            reglas.add(regla);
        }
    }

    /**
     * Aï¿½de una regla a la lista
     * @param regl Regla a aï¿½dir
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
     * Vacï¿½ el contenido del conjunto de reglas
     */
    public void deleteAll(){
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
     * Devuelve la regla como una nueva copia
     * @param i regla i-esima
     * @return una regla de la lista como una nueva copia
     *
         public Complejo getNuevaRegla(int i) {
        Complejo c = (Complejo) reglas.get(i);
        Complejo c2 = new Complejo(c.getSelector(0), c.getNclases());
        for (int j = 1; j < c.size(); j++) {
            c2.addSelector(c.getSelector(j));
        }
        return c2;
         }*/

    /**
     * Devuelve el numero de reglas con las que estamos trabajando
     * @return El tamaï¿½ del conjunto de reglas
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
     * Realiza una copia del conjunto completo de reglas
     * @return el conjunto completo de reglas como una nueva copia
     *
         public ConjReglas copiaConjReglas() {
        int i;
        ConjReglas c = new ConjReglas();

        for (i = 0; i < reglas.size(); i++) {
            Complejo comp = (Complejo) reglas.get(i);
            c.addRegla(comp.copiaRegla());
        }

        return c;
         }*/

    /**
     * Muestra por pantalla el conjunto de reglas
     */
    public void print(int nominal) {
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            System.out.print("\nRule " + (i+1) + ": IF  ");
            c.print(nominal);
            System.out.print(" THEN "+nombreClase+" -> " + valorNombreClases[c.getClase()] + "  ");
            c.printDistribucion();
        }
    }

    /**
     * Imprime en una cadena el conjunto de reglas
     * @return una cadena de texto (string) que almacena el conjunto de reglas
     */
    public String printString(int nominal) {
        String cad = "";
	if(nominal==0){//es nominal
        for (int i = 0; i < reglas.size(); i++) {
            Complejo c = (Complejo) reglas.get(i);
            cad += "\nRule " + (i+1) + ": IF  ";
            cad += c.printString(nominal,0.,0.,false);
            cad += " THEN "+nombreClase+ " -> " + valorNombreClases[c.getClase()] + "  ";
            cad += c.printDistribucionString();
        }
	}
	else{//es numerico. Imprimimos las reglas de manera optima
		int contador=0,num_regla=0;
		double []valores;
		double inf,sup;
		//for (int i = 0; i < reglas.size(); i++) {
		while(contador<(reglas.size())){
		Complejo c = (Complejo) reglas.get(contador);
		valores=c.getSelector(0).getValores();
		inf=valores[0];
		sup=valores[valores.length-1];
		contador++;
		if(contador<(reglas.size())){
			Complejo c2 = (Complejo) reglas.get(contador);
			while(c.getClase()==c2.getClase()&&contador<reglas.size()){
				valores=c2.getSelector(0).getValores();
				sup=valores[valores.length-1];
				contador++;
				if(contador<(reglas.size()))c2=(Complejo)reglas.get(contador);
			}
		}
		cad += "\nRule " + (num_regla+1) + ": IF  ";
			//la impresion de las reglas ha de cubrir todo el espacio
			//usamos intervalos abiertos con el extremo inferior de la siguiente
			//regla, no usamos el extremo superior de la regla actual
		if(contador>=reglas.size())
		cad += c.printString(nominal,inf,sup,true);
		else{
		Complejo c2 = (Complejo) reglas.get(contador);
		valores=c2.getSelector(0).getValores();
		cad += c.printString(nominal,inf,/*sup*/valores[0],false);
		}
		cad += " THEN "+nombreClase+ " -> " + valorNombreClases[c.getClase()] + "  ";
		num_regla++;
		 cad += c.printDistribucionString();
        	}
	}
        return cad;
    }

    /**
     * Devuelve la ltima regla (normalmente aquella con mejor peso)
     * @return la ltima regla de a lista
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
     * @param tam Tamaï¿½ de las estrella
     */
    public void eliminaRepetidos(int tam) {
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i+1; (j < this.size())&&(seguir); j++) {
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
                                this.deleteRegla(i); //borro porque estï¿½repe totalmente
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
     * Elimino reglas que sean semï¿½ticamente iguales (At = 1, At <> 0, At = [0,1])
     * @param tam int Tamaï¿½ de la estrella
     */
    public void eliminaSubsumidos(int tam){
        //for (int i = 0; i < this.size() - 1; i++) {
        for (int i = 0; i < tam; i++) {
            Complejo aux = this.getRegla(i);
            boolean seguir = true;
            for (int j = i + 1; (j < this.size())&&(seguir); j++) {
                Complejo aux2 = this.getRegla(j);
                seguir = false;
                boolean parar = false;
                for (int l = 0; (l < aux.size()) && (!parar); l++) {
                    Selector s = aux.getSelector(l);
                    boolean salir = false;
                    for (int h = 0; (h < aux2.size()) && (!salir); h++) {
                        Selector s2 = aux2.getSelector(h);
                        if ((s.compareTo(s2) == -3)||(s.compareTo(s2) == 0)) { //mirar compareTo en Selector
                            salir = true; //paso a ver el siguiente selector (si eso)
                            if ((l == aux.size() - 1)&&(aux.getDistribucionClase(0) == aux2.getDistribucionClase(0))) {
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

