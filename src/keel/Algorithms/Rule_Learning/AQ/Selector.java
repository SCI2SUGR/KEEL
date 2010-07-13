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

package keel.Algorithms.Rule_Learning.AQ;

/**
 * <p>Title: Selector</p>
 * <p>Description: This class represents a selector, that is, a structure "attribute op value"</p>
 * @author Written by Alberto Fernández (University of Granada) 11/27/2004
 * @version 1.0
 * @since JDK1.4
 */

public class Selector implements Comparable {

    int attribute; // Column number in the data-set (0...m-1)
    int operator; // Operator-> 0:= 1:<> 2:<= 3:>
    private static int EQUAL = 0;
    private static int DISTINCT = 1;
    private static int LOWEREQUAL = 2;
    private static int HIGHER = 3;
    double[] value;

    /**
     *  Class employed to store selectors in the form
     *  (attribute operator values)
     * @param atr attribute
     * @param op operator
     * @param val value
     */
    public Selector(int atr, int op, double val) {
        attribute = atr;
        operator = op;
        value = new double[1];
        value[0] = val;
    }

    /**
     *  Class employed to store selectors in the form
     *  (attribute operator values)
     * @param atr attribute
     * @param op operator
     * @param val a set of values (disjunts -> at = a or b or c)
     */
    public Selector(int atr, int op, double[] val) {
        attribute = atr;
        operator = op;
        value = new double[val.length];
        for (int i = 0; i < val.length; i++) {
            value[i] = val[i];
        }
    }

    /**
     * Comparison function between two objects of the class Selector
     * @param o Selector object to compare
     * @return 0 if they are the same (same attribute, operator and valuemismo), -1 (same attribute, operator y and lower value),
     * 1 (same attribute, operator and higher value), 2 (different attribute and operator) or 3 (different attribute).
     */
    public int compareTo(Object o) {
        Selector s2 = (Selector) o;
        int sal = -2;
        if (attribute == s2.getAttribute()) {
            if (operator == s2.getOperator() && sameValue(s2.getValues())) {
                sal = 0;
            } else
            if (operator == s2.getOperator() && value[0] <= s2.getValue()) {
                sal = -1;
            } else
            if (operator == s2.getOperator() && value[0] > s2.getValue()) {
                sal = 1;
            }
        } else {
            sal = 3;
            if (operator != s2.getOperator()) {
                sal = 2;
            }
        }
        return sal;
    }

    /**
     * It checks if two selector have the same value
     * @param values double[] Set of possible values
     * @return boolean True if they have exactly the same values. False in other case
     */
    private boolean sameValue(double[] values) {
        boolean salida = false;
        if (values.length == value.length) {
            salida = true;
            for (int i = 0; (i < values.length) && (salida); i++) {
                salida = (value[i] == values[i]);
            }
        }
        return salida;
    }

    /**
     * It checks if the values of a selector are subsumed in another
     * @param s Selector Selector to whom we compare
     * @return int 0 If it is not subsumed, -1 if this object selector is subsumued in the parameter selector and 1 if
     * the parameter object is subsumed in this selector.
     */
    public int subsumido(Selector s) {
        int salida = 0;
        int attribute = s.getAttribute();
        int operator = s.getOperator();
        double[] valuees = s.getValues();
        if ((this.attribute == attribute)) { //Same attribute
            if ((this.operator == operator) && (this.sameValue(valuees))) { //they are the same
                salida = 1;
            } else if (this.operator == EQUAL) {
                if ((operator == LOWEREQUAL) && (this.value.length == 1)) {
                    if (valuees[0] == this.value[0]) {
                        salida = -1; // At = 0, At <= 0. <- example
                    }
                } else if (operator == EQUAL) { //I check if they have the same valuess
                    if (this.value.length > valuees.length) {
                        boolean salir = false;
                        for (int i = 0; (i < valuees.length) && (!salir); i++) {
                            boolean seguir = true;
                            for (int j = 0; (j < this.value.length) && (seguir);
                                         j++) {
                                seguir = !(valuees[i] == this.value[j]);
                            }
                            salir = seguir;
                        }
                        if (!salir) {
                            salida = 1;
                        }
                    } else if (valuees.length > this.value.length) {
                        boolean salir = false;
                        for (int i = 0; (i < this.value.length) && (!salir); i++) {
                            boolean seguir = true;
                            for (int j = 0; (j < valuees.length) && (seguir); j++) {
                                seguir = !(valuees[j] == this.value[i]);
                            }
                            salir = seguir;
                        }
                        if (!salir) {
                            salida = -1;
                        }
                    }
                }
            } else if (this.operator == LOWEREQUAL) {
                if ((operator == EQUAL) && (valuees.length == 1)) {
                    if (valuees[0] == this.value[0]) {
                        salida = 1;
                    }
                }
            }
            if ((salida == 0) && (sameValue(valuees))) {
                salida = -2;
            }
        }
        return salida;
    }

    /**
     * It checks if the operator is the contrary ( = and <>, <= and >)
     * @param op int the operator to compare
     * @return boolean True if it is the contrary operator, False in other case
     */
    private boolean opContrario(int op) {
        if (operator == 0) {
            return ((op == 1) || (op == 2)); //we also take into account <=
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
     * It returns the attribute id
     * @return int the attribute id
     */
    public int getAttribute() {
        return attribute;
    }

    /**
     * It return the operator id (!=, < ...)
     * @return int the operator id
     */
    public int getOperator() {
        return operator;
    }

    /**
     * It returns the value of the associated attribute
     * @return double the value
     */
    public double getValue() {
        return value[0];
    }

    /**
     * It returns the set of values of the selector
     * @return double[] the set of values
     */
    public double[] getValues() {
        return value;
    }

    /**
     * It assigns the attribute
     * @param i id of the attribute
     */
    public void setAttribute(int i) {
        attribute = i;
    }

    /**
     * It assigns operator
     * @param i operator id
     */
    public void setoperator(int i) {
        operator = i;
    }

    /**
     * It assigns the value
     * @param f value
     */
    public void setvalue(double f) {
        value[0] = f;
    }

    /**
     * It assigns the set of values
     * @param f valuees
     */
    public void setvaluees(double[] f) {
        value = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            value[i] = f[i];
        }
    }

    /**
     * It shows the content of the selector
     * attribute - operator - value.
     */
    public void print() {
        System.out.print("(Atr" + this.getAttribute() + " ");
        switch (this.getOperator()) {
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
        double[] valuees = this.getValues();
        if (valuees.length > 1) {
            System.out.print(" " + valuees[0]);
            for (int i = 1; i < valuees.length - 1; i++) {
                System.out.print(" ^ " + valuees[i]);
            }
            System.out.print(" ^ " + valuees[valuees.length - 1] + ")");
        } else {
            System.out.print(" " + valuees[0] + ")");
        }

    }
    ;

}

