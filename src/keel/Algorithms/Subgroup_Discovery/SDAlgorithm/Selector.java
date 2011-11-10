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

/**
 * <p>
 * @author Writed by Alberto Fernández (University of Granada) 15/01/2006
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDAlgorithm;

public class Selector implements Comparable {

    /**
     * <p>
     * Structure with the set of complex for the rules
     * </p>
     */

    int attribute;
    int operator; // Operator-> 0 is = || 1 is <> || 2 is <= || 3 is>
    double[] valor;

    /**
     * <p>
     * Constructor
     * </p>
     * @param atr        Attribute to add
     * @param op         Operator to add
     * @param val        Value to add
     */
    public Selector(int atr, int op, double val) {
        attribute = atr;
        operator = op;
        valor = new double[1];
        valor[0] = val;
    }

    /**
     * <p>
     * <p>
     * Constructor for several values
     * </p>
     * @param atr        Attribute to add
     * @param op         Operator to add
     * @param val        Array of values to add
     */
    public Selector(int atr, int op, double[] val) {
        attribute = atr;
        operator = op;
        valor = new double[val.length];
        for (int i = 0; i < val.length; i++) {
            valor[i] = val[i];
        }
    }


    /**
     * <p>
     * Function to compare two objects of this class
     * </p>
     * @param o             Selector to compare
     * @return                  0 equal
     *                          1 equal attribute and operator, and upper value
     *                          2 different attribute and operator
     *                          2 (distinto attribute y operator)
     *                          -1 equal attribute and operator, and lower value
     *                          -3 equal attribute and subsuming
     */
    public int compareTo(Object o) {
        Selector s2 = (Selector) o;
        int sal = -2;
        if (attribute == s2.getAtributo()) {
            if (operator == s2.getOperador() && mismoValor(s2.getValores())) {
                sal = 0;
            } else
            if (subsumido(s2.getValores(),s2.getOperador())) {
                sal = -3;
            } else
            if (operator == s2.getOperador() && valor[0] <= s2.getValor()) {
                sal = -1;
            } else
            if (operator == s2.getOperador() && valor[0] > s2.getValor()) {
                sal = 1;
            }
        } else {
            sal = 3;
            if (operator != s2.getOperador()) {
                sal = 2;
            }
        }
        return sal;
    }

    /**
     * <p>
     * Check if two selectors have the same value
     * </p>
     * @param double[]              Array with the values
     * @return                      True of False
     */
    private boolean mismoValor(double[] valores) {
        boolean salida = false;
        if (valores.length == valor.length) {
            salida = true;
            for (int i = 0; (i < valores.length) && (salida); i++) {
                salida = (valor[i] == valores[i]);
            }
        }
        return salida;
    }

    /**
     * <p>
     * Check if the values of a selector are subsuming
     * </p>
     * @param double[]          Array with the value
     * @param int               Operator for checking
     * @return                  True or Flase
     */
    private boolean subsumido(double[] valores,int _operador) {
        boolean salida = false;

        salida = (mismoValor(valores) && (!(this.opContrario(_operador))));
        if (!salida){ // They are not subsuming
            if ((operator == _operador)&&(operator == 0)) { // They are equal and the operator is equal
                if (valor.length < valores.length) {
                    salida = true;
                    for (int i = 0; (i < valor.length) && (salida); i++) {
                        salida = (valor[i] == valores[i]);
                    }
                }
            }
        }
        return salida;
    }

    /**
     * <p>
     * Check if the operator is the opposite
     * </p>
     * @param int                   The operator to check
     * @return                      True of false
     */
    private boolean opContrario(int op) {
        if (operator == 0) {
            return ((op == 1) || (op == 2));
        }
        if (operator == 1) {
            return (op == 0);
        }
        if (operator == 2) {
            return ((op == 3) || (op == 0));
        }
        if (operator == 3) {
            return (op == 2);
        }
        return false;
    }

    /**
     * <p>
     * Return the id of the attribute
     * </p>
     * @return                      The id of the attribute
     */
    public int getAtributo() {
        return attribute;
    }

    /**
     * <p>
     * Return the id of the operator
     * </p>
     * @return                      The id of the operator
     */
    public int getOperador() {
        return operator;
    }

    /**
     * <p>
     * Return the value associated with the attribute
     * </p>
     * @return                      Value of the attribute
     */
    public double getValor() {
        return valor[0];
    }

    /**
     * <p>
     * Return the set of values for the selector
     * </p>
     * @return                      Array of values
     */
    public double[] getValores() {
        return valor;
    }

    /**
     * <p>
     * Sets the attribute
     * </p>
     * @param i                   Value of the attribute
     */
    public void setAtributo(int i) {
        attribute = i;
    }

    /**
     * <p>
     * Sets the operator
     * </p>
     * @param i                   Value of the operator
     */
    public void setOperador(int i) {
        operator = i;
    }

    /**
     * <p>
     * Sets the value for the selector
     * @param f                Value of the selector
     */
    public void setValor(double f) {
        valor[0] = f;
    }

    /**
     * <p>
     * Sets a set of value for the selector
     * </p>
     * @param f              Array of values of the selector
     */
    public void setValores(double[] f) {
        valor = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            valor[i] = f[i];
        }
    }


    /**
     * <p>
     * Show the selector
     * </p>
     */
    public void print() {
        System.out.print("(Atr" + this.getAtributo() + " ");
        switch (this.getOperador()) {
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
        System.out.print(" " + this.getValor() + ")\n");
    };

}
