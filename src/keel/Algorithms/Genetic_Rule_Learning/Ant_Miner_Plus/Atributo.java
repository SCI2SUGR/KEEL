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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner_Plus;

/**
 * <p>Título: Ant Colony Optimization</p>
 * <p>Descripción:Clase Atributo necesaria para el ACO</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class Atributo {

    private String valor; //Valor del atributo
    private int atributo; //Identifica el atributo al que se ha asignado valor (columna del atributo)
    //-1 en caso de que sea una clase
    private boolean tipo; //tipo del atributo true=categoria, false = ordinal
    private static ComparadorAtributo c; //Comparador para los atributos

    /**
     * Constructor por defecto
     *
     * Construye un atributo vacio
     */
    public Atributo() {
        valor = new String();
        atributo = 0;
        c = new ComparadorAtributo();
        tipo = true; //por defecto categoria

    }

    /**
     *
     * Constructor de Atributo con parametros
     *
     * @param valorOriginal double  Valor del atributo que queremos introducir
     * @param atributoOriginal int  Identifica el atributo al que le estamos asignando el valor
     * @param tip int      Tipo del atributo: numerico (0) o nominal (1)
     */
    public Atributo(String valorOriginal, int atributoOriginal, boolean tip) {

        valor = new String(valorOriginal);
        atributo = atributoOriginal;
        c = new ComparadorAtributo();
        tipo = tip;

    }


    /**
     *
     * Constructor de copia, crea un atributo nuevo a partir del que se le pasa como argumento
     * copiando todos sus valores.
     *
     * @param original Atributo Atributo que queremos copiar
     */
    public Atributo(Atributo original) {
        valor = new String(original.valor);
        atributo = original.atributo;
        c = original.obtenerComparador();
        tipo = original.tipo;

    }

    /**
     * Funcion que devuelve el identificador del atributo al que estamos asignando valor.
     *
     * @return int  Devuelve el atributo al que estamos asignando un valor
     */
    public int getAtributo() {

        int devolver = atributo;
        return devolver;

    }

    /**
     * Funcion que devuelve el valor del atributo
     *
     * @return String Devuelve el valor del atributo
     */
    public String getValor() {

        String devolver = new String(valor);
        return devolver;

    }


    /**
     * Compara dos atributos
     *
     * @param o1 Object Atributo a comparar
     * @param o2 Object Atributo a comparar
     * @return int Devuelve 0 si tienen la misma posicion, 1 si el primero esta
     * antes, -1 si el primero esta despues.
     *
     * OJO!!!! Como el Collections.sort ordena de mayor a menor y el orden que interesa
     * que tengan los atributos es de menor a mayor, este CompareTo esta trucado al reves
     * es decir cuando es menor devuelve mayor y cuando es mayor devuelve menor.
     *
     */
    public int compare(Object o1, Object o2) {
        Atributo original = (Atributo) o1;
        Atributo actual = (Atributo) o2;
        int devolver = 0;

        if (actual.atributo == original.atributo &&
            actual.valor.equals(original.valor)) { //Para ver si son iguales tiene que coincidir tambien el valor
            devolver = 0;
        } else {
            if (actual.atributo < original.atributo) {
                devolver = -1;
            } else {
                if (actual.atributo > original.atributo) {
                    devolver = 1;
                }
            }
        }
        return devolver;

    }

    /**
     * Funcion usada para comparar dos atributos
     * @param obj Object Atributo a comparar con el actual.
     * @return boolean Indica si son iguales (true) o no (false)
     */
    public boolean equals(Object obj) {
        boolean devolver;
        Atributo original = (Atributo) obj;
        if (atributo == original.atributo && valor.equals(original.valor)) { //Para ver si son iguales tiene que coincidir tambien el valor
            devolver = true;
        } else {
            devolver = false;
        }
        return devolver;
    }

    /**
     * Compara dos atributos mediante su valor
     * @param at Atributo Atributo a comparar con el actual
     * @return boolean Devuelve true si son iguales y false en caso contrario
     */
    public boolean esIgual(Atributo at) {
        if (at.valor.equals(valor) && at.atributo == atributo) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Funcion que devuelve un comparador para poder comparar dos atributos
     * @return ComparadorAtributo Comparador de dos atributos
     */
    public static ComparadorAtributo obtenerComparador() {
        return c;
    }

    public boolean getTipo() {
        return tipo;
    }

    public void setTipo(boolean tip) {
        tipo = tip;
    }

}

