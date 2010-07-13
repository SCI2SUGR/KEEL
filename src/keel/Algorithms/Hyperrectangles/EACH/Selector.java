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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Hyperrectangles.EACH;


public class Selector implements Comparable {
/**
 * <p>
 * Class to stores selectors with the form (attribute operator values)
 * </p>
 */
	
	// Column of data set (0..m-1)
    int attribute; 
    // Operator> 0:= 1:<> 2:<= 3:>
    int operator;
    double[] value;
    // for nominal values
    String []valueN;
    // indicates if the attribute is nominal
    boolean nominal;
    int numValues;

    /**
     * <p>
     * Class to stores selectors with the form (attribute operator values
     * </p>
     * @param attribute attribute
     * @param op operator
     * @param nominal nominal values
     * @param numeric values expressed in numerical way
     * @param util el number ofvalues
     */
    public Selector(int attribute, int op, String []nominal,double []numeric,int util) {
        this.attribute = attribute;
        this.operator = op;
        this.valueN = new String[util];
	    this.value = new double[util];
	     for (int i = 0; i < util; i++) {
              this.value[i] = numeric[i];
	          this.valueN[i]=nominal[i];
          }
	      this.nominal=true;
	      this.numValues=util;
    }
    
    /**
     * <p>
     * Class to stores selectors with the form (attribute operator values
     * </p>
     * @param attribute attribute
     * @param operation operator
     * @param values set of values (at = a ï¿½b ï¿½c)
     * @param util number of values
     */
    public Selector(int attribute, int operation, double[] values,int util) {
        this.attribute = attribute;
        this.operator = operation;
        this.value = new double[util];
        for (int i = 0; i < util; i++) {
            this.value[i] = values[i];
        }
	this.nominal=false;
	this.numValues=util;
    }



    /**
     * <p>
     * Function to compare two objects of the selector class
     * </p>
     * @param o Objeto Seclector to compare
     * @return 0 if are equals (same attribute,operator and value), -1 (same attribute, operator),
     * 		   1 (same attribute, operator and less value), [new->] 2 (different attribute and operator)
     * 		   3 (different attribute)).
     *        -2 -> Same attribute, different operator; -3 -> same attribute, value = ï¿½+/- 1 
     */
    public int compareTo(Object o) {
        Selector s2 = (Selector) o;
        int sal = -2;
        if (attribute == s2.getAttribute()) {
            if (operator == s2.getOperator() && sameValue(s2.getValues())) {
                sal = 0;
            } else
            if (subsumit(s2.getValues(),s2.getOperator())) {
                //valor[0] == s2.getValor() + 1) || (valor[0] == s2.getValor() - 1)) {
                sal = -3; //Si tiene el mismo valor y no es el operador contrario, o tiene el valor +/- 1
            } else
            if (operator == s2.getOperator() && value[0] <= s2.getZeroValue()) {
                sal = -1;
            } else
            if (operator == s2.getOperator() && value[0] > s2.getZeroValue()) {
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
     * <p>
     * Checks if two selectors have the same value or values
     * </p>
     * @param values double[] Set of possible values
     * @return boolean True if have the same values. False otherwise
     */
    private boolean sameValue(double[] values) {
        boolean out = false;
        if (values.length == value.length) {
            out = true;
            for (int i = 0; (i < values.length) && (out); i++) {
                out = (value[i] == values[i]);
            }
        }
        return out;
    }

    /**
     * <p>
     * Checks if the values of ane selector are included in other
     * </p>
     * @param values double[] Values with compare to
     * @param op int Operator to compare to
     * @return boolean True if the selector of the class is included in the class. False otherwise.
     */
    private boolean subsumit(double[] values,int op) {
        boolean out = false;
        //Si los valores son iguales y el operador no es el contrario
        out = (sameValue(values) && (!(this.isCompOperator(op))));
        if (!out){ //No estï¿½subsumido...
            if ((operator == op)&&(operator == 0)) { //Son iguales y es el ==
                if (value.length < values.length) {
                    out = true;
                    for (int i = 0; (i < value.length) && (out); i++) {
                        out = (value[i] == values[i]);
                        //System.err.print(" " + valor[i] + "," + valores[i]);
                    }
                    //System.err.println("");
                }
            }
            /*else { //O son distintos o no es el igual
                salida = ((valor[0] == valores[0] + 1) ||
                         (valor[0] == valores[0] - 1));
            }
           */
        }
        return out;
    }

    /**
     * <p>
     * Checks if the operator is the complement( = y <>, <= y >)
     * </p>
     * @param op int The operator to compare
     * @return boolean True if is the complement, False otherwise
     */
    private boolean isCompOperator(int op) {
        if (operator == 0) {
            return ((op == 1) || (op == 2)); //tb cuento el <=
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
     * Returns the attribute's id
     * </p>
     * @return attribute
     */
    public int getAttribute() {
        return attribute;
    }

    /**
     * <p>
     * Return the operator's id
     * </p>
     * @return operator
     */
    public int getOperator() {
        return operator;
    }

    /**
     * <p>
     * Return the attribute's value associated
     * </p>
     * @return value
     */
    public double getZeroValue() {
        return value[0];
    }
    
    
    /**
     * <p>
     * Returns the nominal value of the associated value
     * </p>
     * @return value
     */
    public String getNValue() {
        return valueN[0];
    }

    /**
     * <p>
     * Returns the set of values of the selector
     * </p>
     * @return double[] value
     */
    public double[] getValues() {
        return value;
    }
    
    /**
     * <p>
     * Return the set of nominal values of the selector
     * </p>
     * @return double[]
     */
    public String[] getNValues() {
        return valueN;
    }
    public int getNumValues(){
    	return numValues;
    }

    /**
     * <p>
     * Assign the attribute
     * </p>
     * @param i atribute's value
     */
    public void setAtribute(int i) {
        attribute = i;
    }

    /**
     * <p>
     * Assign the operator
     * </p>
     * @param i value of the operator
     */
    public void setOperator(int i) {
        operator = i;
    }

    /**
     * <p>
     * Assign the value
     * </p>
     * @param f value
     */
    public void setZeroValue(double f) {
        value[0] = f;
    }

    /**
     * <p>
     * Assign the values
     * </p>
     * @param f values
     */
    public void setValues(double[] f) {
        value = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            value[i] = f[i];
        }
    }


    /**
     * <p>
     * Shows the content of a selector: attribute-operator-value
     * </p>
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
	if(nominal)
        System.out.print(" " + this.getNValue() + ")\n");
	else
	System.out.println(" " + this.getZeroValue() + ")\n");
	
    };

}

