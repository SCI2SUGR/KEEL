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
 * <p>This class implements an attribute. </p>
 * Description: Implements the attributes representation as
 * a pair of the attribute id and its value used by the ACO algorithm to
 * build the dataset and the conditions for the rules. 
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
   * Default constructor. Builds an empty attribute.
   */
    public Atributo() {
        valor = -1;
        atributo = 0;
        c = new ComparadorAtributo();
        tipo = 0; //por defecto Entero

    }

  /**
   * Parameter constructor. Builds an attribute by copying the values of the parameters given. 
   * @param valorOriginal Initial value of the attribute.
   * @param atributoOriginal Identifies the attribute to be represented. (attribute id). 
   * @param tip Attribute type: numeric (0) or nominal (1).
   */
    public Atributo(float valorOriginal, int atributoOriginal, int tip) {

        valor = valorOriginal;
        atributo = atributoOriginal;
        c = new ComparadorAtributo();
        tipo = tip;

    }


  /**
   * Copy Constructor. Creates a new attribute object by copying the one given as argument.
   * 
   * @param original Attribute to be copied.
   */
    public Atributo(Atributo original) {
        valor = original.valor;
        atributo = original.atributo;
        c = original.obtenerComparador();
        tipo = original.tipo;

    }

  /**
   * Returns the attribute id represented on this object.
   * 
   * @return int   the attribute id represented on this object.
   */
    public int getAtributo() {

        int devolver = atributo;
        return devolver;

    }

  /**
   * Returns the assigned value of the attribute.
   *
   * @return String the assigned value of the attribute.
   */
    public float getValor() {

        float devolver = valor;
        return devolver;

    }


  /**
   * Compares two attributes.
   * 
   *
   * @param o1 Object First attribute to compare with.
   * @param o2 Object Second attribute to compare with.
   * @return int 0 if they are the same attribute, 1 if the first attribute is 
   * lesser than the second one (will be sorted as first) or -1 if the first
   * attribute is greater (will be sorted as second).
   *
   * Warning!!! Collections.sort sorts in descending order (from greater to lesser).
   * This method sorts the elements in increasing (from lesser to greater), 
   * the way needed for attributes.
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
   * Checks if this attribute is equal to the one given.
   * @param obj Object Attribute to compare with.
   * @return boolean True if they are the same, false otherwise.
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
   * Checks if this attribute is equal to the one given.
   * @param at Object Attribute to compare with.
   * @return boolean True if they are the same, false otherwise.
   */
    public boolean esIgual(Atributo at) {
        if (at.valor == valor) {
            return true;
        } else {
            return false;
        }
    }

  /**
   * Returns the compare condition to compare two attributes.
   * @return {@link ComparadorAtributo} the compare condition.
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
     * Returns the attribute type.
     * @return the attribute type.
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * Sets the attribute type with the one given: numeric (0) or nominal (1).
     * @param tip given type.
     */
    public void setTipo(int tip) {
        tipo = tip;
    }

    /**
     * Sums the value given to the one stored.
     * @param v given value.
     */
    public void sumarValor(float v) {
        valor += v;
    }

    /**
     * Prints the information of the attribute.
     * @param cadena title or name of the attribute.
     */
    public void imprime(String cadena) {
        System.out.println(cadena + "  " + valor);
    }

    /**
     * Sets the attribute's value with the one given.
     * @param at value to set.
     */
    public void setValor(float at) {
        valor = at;
    }

}

