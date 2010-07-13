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
 * @author Written by Rosa Venzala 19/09/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Riona;


public class Selector implements Comparable {
/**
 * <p>
 * Structure for storing one condition of the antecedent of a rule
 * </p>
 */
	
	// column of dataset (0..m-1)
	int attribute; 
	// operator -> 0:= 1:<> 2:<= 3:>
	int operator;
	double[] value;
	// for nominal values
	String []valueN; 
	// indicates if is a nominal attribute
	boolean nominal; 
	int numValues;
	// posiction in Train Set of maximum
	int numUp;
	// posiction in Train Set of minimum
	int numLow;

	/**
	 * <p>
	 * Class that stores selectors with the form: (attribute operator values) where values are nominal
	 * </p>
	 * @param attr attribute
	 * @param op operator
	 * @param nomi nominal values
	 * @param num the values in numerical way
	 * @param util the number of values
	 * @param numI the position in the dataset
	 */

	public Selector(int attr, int op, String []nomi,double []num,int util,int numI) {
		attribute = attr;
		operator = op;
		valueN = new String[util];
		value = new double[util];
		for (int i = 0; i < util; i++) {
			value[i] = num[i];
			valueN[i]=nomi[i];
		}
		this.nominal=true;
		numValues=util;
		numUp=numI;
		numLow=numI;
	}
	
	/**
	 * <p>
	 * Class that stores datasets with the form: (attribute operator values)
	 * </p>
	 * @param attr attribute
	 * @param op operator
	 * @param val the set of values
	 * @param util the number of values
	 * @param numI the position in the dataset
	 */
	public Selector(int attr, int op, double[] val,int util,int numI) {
		attribute = attr;
		operator = op;
		value = new double[util];
		for (int i = 0; i < util; i++) {
			value[i] = val[i];
		}
		this.nominal=false;
		numValues=util;
		numUp=numI;
		numLow=numI;
	}


	/**
	 * <p>
	 * Function for comparing two objects of the selector class
	 * </p>
	 * @param o object to compare
	 * @return  0 if are equals (same attribute, operator and the value), 
	 *          -1 (same attribute, operator and less value),
	 *          1 (mismo atributo, operador and less value), 
	 *          [new->] 2 (no same attribute and operator) 
	 *          3 (different attrbute)).
	 * 			-2 -> Same attribute, no same operator; 
	 * 			-3 -> Same attribute, value = ï¿½+/- 1
	 */
	public int compareTo(Object o) {
		Selector s2 = (Selector) o;
		int sal = -2;
		if (attribute == s2.getAttribute()) {
			if (operator == s2.getOperator() && sameValue(s2.getValues())) {
				sal = 0;
			} else
				if (subsumed(s2.getValues(),s2.getOperator())) {
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
	 * Returns 0 if the selectors are equivalents
	 * </p>
	 * @param o Selector 
	 * @return 0 if are equivalents, 1 otherwise
	 */
	public int equivalents(Object o){
		Selector s=(Selector)o;
		int equal=1;
		if((attribute == s.getAttribute())&&(operator == s.getOperator())){
			if (nominal){
				if((value[0]==s.getZeroValue()) &&(value[1]==s.get1Value()))equal=0;
			}
			else{
				if((s.getZeroValue()>=value[0])&&(s.get1Value()<=value[1])){
					equal=0;
				}
			}
		}
		return equal;
	}

	/**
	 * <p>
	 * Checks if two selectors have the same value or values
	 * </p>
	 * @param values double[] set of possible values
	 * @return boolean True if have the same values, false otherwise
	 */
	private boolean sameValue(double[] values) {
		boolean outPut = false;
		if (values.length == value.length) {
			outPut = true;
			for (int i = 0; (i < values.length) && (outPut); i++) {
				outPut = (value[i] == values[i]);
			}
		}
		return outPut;
	}

	/**
	 * <p>
	 * Checks if the values of one selector are included in other
	 * </p>
	 * @param values double[] Values to check with
	 * @param _operator int Values to check with
	 * @return boolean True if the selector of the class is included, False otherwise
	 */
	private boolean subsumed(double[] values,int _operator) {
		boolean outPut = false;
		//Si los valores son iguales y el operador no es el contrario
		outPut = (sameValue(values) && (!(this.contraryOperator(_operator))));
		if (!outPut){ //No estï¿½subsumido...
			if ((operator == _operator)&&(operator == 0)) { //Son iguales y es el ==
				if (value.length < values.length) {
					outPut = true;
					for (int i = 0; (i < value.length) && (outPut); i++) {
						outPut = (value[i] == values[i]);
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
		return outPut;
	}

	/**
	 * <p>
	 * Checks if the operator is the complementary( = y <>, <= y >)
	 * </p>
	 * @param op int the operator to compare
	 * @return boolean True if is complementary, False otherwise
	 */
	private boolean contraryOperator(int op) {
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
	 * Return the id of the attribute
	 * </p>
	 * @return attribute
	 */
	public int getAttribute() {
		return attribute;
	}

	/**
	 * <p>
	 * Returns the id of the operator
	 * </p>
	 * @return operator
	 */
	public int getOperator() {
		return operator;
	}

	/**
	 * <p>
	 * Returns the value or less extrem of the associated attribute
	 * </p>
	 * @return value
	 */
	public double getZeroValue() {
		return value[0];
	}

	/**
	 * <p>
	 * Returns the value or high extrem of the associated attribute 
	 * </p>
	 * @return value
	 */
	public double get1Value() {
		return value[1];
	}

	/**
	 * <p>
	 * Returns the nominal value of the attribute
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
	 * @return double[]
	 */
	public double[] getValues() {
		return value;
	}

	/**
	 * <p>
	 * Returns the set of nominal values ofthe selector
	 * </p>
	 * @return double[]
	 */
	public String[] getNValues() {
		return valueN;
	}
	
	/**
	 * <p>
	 * Returns the number of values
	 * </p>
	 * @return number of values
	 */
	public int getNumValues(){
		return numValues;
	}
	
	/**
	 * <p>
	 * Returns the numebr of instances
	 * </p>
	 * @return Number of instances
	 */
	public int[]getNumInstances(){
		int []v=new int[2];
		v[0]=numLow;
		v[1]=numUp;
		return v;
	}
	
	/**
	 * <p>
	 * Returns nominal attribute
	 * </p>
	 * @return nominal attribute
	 */
	public boolean isNominal(){
		return nominal;
	}
	
	/**
	 * <p>
	 * Set the high number
	 * </p>
	 * @param num high number
	 */
	public void setNumUp(int num){
		numUp=num;
	}
	
	/**
	 * <p>
	 * Set the low number
	 * </p>
	 * @param num low number
	 */
	public void setNumLow(int num){
		numLow=num;
	}

	/**
	 * <p>
	 * Assigns the attribute
	 * </p>
	 * @param i value of the attribute
	 */
	public void setAttributeValue(int i) {
		attribute = i;
	}

	/**
	 * <p>
	 * Assigns the operator
	 * </p>
	 * @param i value of the operator
	 */
	public void setOperator(int i) {
		operator = i;
	}

	/**
	 * <p>
	 * Assigns the value
	 * </p>
	 * @param f value
	 */
	public void setValue(double f) {
		value[0] = f;
	}

	/**
	 * <p>
	 * Assigns the values
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
	 * Shows the content of the selector selector:
	 * Attribute - operator - value
	 * </p>
	 */
	public void print() {
		System.out.print("(Attr" + this.getAttribute() + " ");
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

