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

package keel.Algorithms.Genetic_Rule_Learning.DMEL;

public class Condition {
	
	private int attribute;
	private int value;
	
	public Condition () {
		
	}
	
	/**Creates a condition*/
	public Condition (int attr, int val) {
		
		attribute = attr;
		value = val;
	
	}	
	
	/**Creates a copy of a condition*/
	public Condition (Condition c) {

		attribute = c.attribute;
		value = c.value;

	}
	
	public int getAttribute() {
		return attribute;
	}

	public int getValue() {
		return value;
	}
	
	public void setCondition(int attr, int val) {		
		attribute = attr;
		value = val;
	}


	public String toString (myDataset train) {
		
		String cadena = "";		
		cadena += train.nameAttribute(attribute) + " = " + train.valueAttribute(attribute, value);		
		return cadena;
	}
	
	public boolean equals (Object a) {
		
		Condition tmp = (Condition) a;
		
		if (attribute == tmp.attribute && value == tmp.value)
			return true;
		return false;		
	}

}

