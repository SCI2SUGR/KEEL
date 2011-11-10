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
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Prism;


public class Selector implements Comparable {
/**
 * <p>
 * Class to stores selectors with the form (attribute operator values)
 * </p>
 */
	// Column of data set (0..m-1)
    int atributo; 
    // Operator> 0:= 1:<> 2:<= 3:>
    int operador; 
    double[] valor;
    // For nominal velues
    String []valorN;
    boolean nominal;

    /**
     * <p>
     * Class to stores selectors with the form (attribute operator values)
     * </p>
     * @param atr attribute
     * @param op operator
     * @param val value
     */
    public Selector(int atr, int op, double val) {
        atributo = atr;
        operador = op;
        valor = new double[1];
        valor[0] = val;
    }
    
    /**
     * <p>
     * Class to stores selectors with the form (attribute operator values), where values are nominal
     * </p>
     * @param atr atribute
     * @param op operator
     * @param val value
     * @param nominal true indicate values are nominal
     */    
    public Selector(int atr, int op, String val,boolean nominal) {
        atributo = atr;
        operador = op;
        valorN = new String[1];
        valorN[0] = val;
	valor = new double[1];
	this.nominal=nominal;
    }

    /**
     * <p>
     * Class to stores selectors with the form (attribute operator values
     * </p>
     * @param atr attribute
     * @param op operator
     * @param val set of values (disjunct -> at = a ï¿½b ï¿½c)
     */
    public Selector(int atr, int op, double[] val) {
        atributo = atr;
        operador = op;
        valor = new double[val.length];
        for (int i = 0; i < val.length; i++) {
            valor[i] = val[i];
        }
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
        if (atributo == s2.getAtributo()) {
            if (operador == s2.getOperador() && mismoValor(s2.getValores())) {
                sal = 0;
            } else
            if (subsumido(s2.getValores(),s2.getOperador())) {
                //valor[0] == s2.getValor() + 1) || (valor[0] == s2.getValor() - 1)) {
                sal = -3; //Si tiene el mismo valor y no es el operador contrario, o tiene el valor +/- 1
            } else
            if (operador == s2.getOperador() && valor[0] <= s2.getValor()) {
                sal = -1;
            } else
            if (operador == s2.getOperador() && valor[0] > s2.getValor()) {
                sal = 1;
            }
        } else {
            sal = 3;
            if (operador != s2.getOperador()) {
                sal = 2;
            }
        }
        return sal;
    }

    /**
     * <p>
     * Checks if two selectors have the same value or values
     * </p>
     * @param valores double[] Set of possible values
     * @return boolean True if have the same values. False otherwise
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
     * Checks if the values of ane selector are included in other
     * </p>
     * @param valores double[] Values with compare to
     * @param _operador int Operator to compare to
     * @return boolean True if the selector of the class is included in the class. False otherwise.
     */
    private boolean subsumido(double[] valores,int _operador) {
        boolean salida = false;
        //Si los valores son iguales y el operador no es el contrario
        salida = (mismoValor(valores) && (!(this.opContrario(_operador))));
        if (!salida){ //No estï¿½subsumido...
            if ((operador == _operador)&&(operador == 0)) { //Son iguales y es el ==
                if (valor.length < valores.length) {
                    salida = true;
                    for (int i = 0; (i < valor.length) && (salida); i++) {
                        salida = (valor[i] == valores[i]);
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
        return salida;
    }

    /**
     * <p>
     * Checks if the operator is the complement( = y <>, <= y >)
     * </p>
     * @param op int The operator to compare
     * @return boolean True if is the complement, False otherwise
     */
    private boolean opContrario(int op) {
        if (operador == 0) {
            return ((op == 1) || (op == 2)); //tb cuento el <=
        }
        if (operador == 1) {
            return (op == 0);
        }
        if (operador == 2) {
            return ((op == 3) || (op == 0));
        }
        if (operador == 3) {
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
    public int getAtributo() {
        return atributo;
    }

    /**
     * <p>
     * Return the operator's id
     * </p>
     * @return operator
     */
    public int getOperador() {
        return operador;
    }

    /**
     * <p>
     * Return the attribute's value associated
     * </p>
     * @return value
     */
    public double getValor() {
        return valor[0];
    }
    
   /**
    * <p>
    * Returns the nominal value of the associated value
    * </p>
    * @return value
    */
    public String getValorN() {
        return valorN[0];
    }

    /**
     * <p>
     * Returns the set of values of the selector
     * </p>
     * @return double[] value
     */
    public double[] getValores() {
        return valor;
    }
    
    /**
     * <p>
     * Return the set of nominal values of the selector
     * </p>
     * @return double[]
     */
    public String[] getValoresN() {
        return valorN;
    }

    /**
     * <p>
     * Assign the attribute
     * </p>
     * @param i atribute's value
     */
    public void setAtributo(int i) {
        atributo = i;
    }

    /**
     * <p>
     * Assign the operator
     * </p>
     * @param i value of the operator
     */
    public void setOperador(int i) {
        operador = i;
    }

    /**
     * <p>
     * Assign the value
     * </p>
     * @param f value
     */
    public void setValor(double f) {
        valor[0] = f;
    }

    /**
     * <p>
     * Assign the values
     * </p>
     * @param f values
     */
    public void setValores(double[] f) {
        valor = new double[f.length];
        for (int i = 0; i < f.length; i++) {
            valor[i] = f[i];
        }
    }


    /**
     * <p>
     * Shows the content of a selector: attribute-operator-value
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
	if(nominal)
        System.out.print(" " + this.getValorN() + ")\n");
	else
	System.out.println(" " + this.getValor() + ")\n");
	
    };

}

