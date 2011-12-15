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

package keel.Algorithms.Genetic_Rule_Learning.PSO_ACO;

/**
 * <p>This class implements an attribute</p>
 * 
 * @author Vicente Rubén del Pino
 * @version 1.0
 */
public class Atributo {

    private float valor; //Valor del atributo
    private int atributo; //Identifica el atributo al que se ha asignado valor (columna del atributo)
    //-1 en caso de que sea una clase
    private int tipo; //tipo del atributo 0-->Entero, 1--->Nominal, 2--->Real
    private static ComparadorAtributo c; //Comparador para los atributos

    /**
     * Constructor por defecto
     *
     * Construye un atributo vacio
     */
    public Atributo() {
        valor = -1;
        atributo = 0;
        c = new ComparadorAtributo();
        tipo = 0; //por defecto Entero

    }

    /**
     *
     * Constructor de Atributo con parametros
     *
     * @param valorOriginal double  Valor del atributo que queremos introducir
     * @param atributoOriginal int  Identifica el atributo al que le estamos asignando el valor
     * @param tip int      Tipo del atributo: numerico (0) o nominal (1)
     */
    public Atributo(float valorOriginal, int atributoOriginal, int tip) {

        valor = valorOriginal;
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
        valor = original.valor;
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
    public float getValor() {

        float devolver = valor;
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
            actual.valor == original.valor) { //Para ver si son iguales tiene que coincidir tambien el valor
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
        if (valor == original.valor) { //Para ver si son iguales tiene que coincidir tambien el valor
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
        if (at.valor == valor) {
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

    /*
      public static double [] obtenerIntervalos(int tipo,String valor,Discretizer discretizador){
        double [] devolver = new double [2];
        boolean terminado=false;
        int valorNumerico;
        double menor;
        double mayor;
        int tamanio=discretizador.getNumIntervals(tipo);

        valorNumerico=Integer.parseInt(valor.trim());

        if(valorNumerico==0){
          mayor=discretizador.getCutPoint(tipo,0);
          devolver[0]=-1;
          devolver[1]=mayor;
          return devolver;
        }
        if(valorNumerico==tamanio-1){
          menor=mayor=discretizador.getCutPoint(tipo,tamanio-2);
          devolver[0]=menor;
          devolver[1]=-1;
          return devolver;
        }

        mayor=discretizador.getCutPoint(tipo,valorNumerico);
        menor=discretizador.getCutPoint(tipo,valorNumerico-1);
        devolver[0]=menor;
        devolver[1]=mayor;
        return devolver;
      }
     */


    /**
     * Funcion que devuelve el tipo del Atributo
     * @return Tipo del atributo
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * Funcion que inicializa el tipo del atributo
     * @param tip Tipo del atributo
     */
    public void setTipo(int tip) {
        tipo = tip;
    }

    /**
     * Modulo que suma un valor al valor del atributo
     * @param v Valor a sumar
     */
    public void sumarValor(float v) {
        valor += v;
    }

    /**
     * Funcion que imprime el atributo
     * @param cadena Cadena de titulo
     */
    public void imprime(String cadena) {
        System.out.println(cadena + "  " + valor);
    }

    /**
     * Funcion que inicializa el valor del atributo
     * @param at Valor para el atributo
     */
    public void setValor(float at) {
        valor = at;
    }

}

