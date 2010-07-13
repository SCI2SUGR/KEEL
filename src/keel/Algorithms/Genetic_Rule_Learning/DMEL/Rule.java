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

import java.util.Arrays;

public class Rule {
	
	Condition regla[];
	
	public Rule () {
	
	}
	
	/**Creates an empty rule*/
	public Rule (int size) {

		regla = new Condition[size];
	}
	
	/**Creates a rule that includes a set of conditions*/	
	public Rule (Condition cond[]) {
		
		int i;
		
		regla = new Condition[cond.length];
		for (i=0; i<cond.length; i++) {
			regla[i] = new Condition(cond[i]);
		}		
		
	}
	
	/**Creates a copy of a rule*/
	public Rule (Rule a) {
		
		int i;
		
		regla = new Condition[a.getRule().length];
		for (i=0; i<regla.length; i++) {
			regla[i] = new Condition(a.getiCondition(i));
		}		
	}
	
	public Condition[] getRule() {
		return regla;
	}
	
	public Condition getiCondition(int i) {
		return regla[i];
	}
	
	public void setiCondition (int i, Condition c) {
		regla[i].setCondition(c.getAttribute(), c.getValue());
	}
	

	public String toString (myDataset train) {
		
		int i;
		String cadena = "";
				
		cadena += regla[0].toString(train);
		
		for (i=1; i<regla.length; i++) {
			cadena += " AND " + regla[i].toString(train);
		}
		
		return cadena;
	}
	
	public boolean equals (Object a) {
		
		int i, j;
		Rule tmp = (Rule) a;
		boolean mascara[] = new boolean [regla.length];
		
		Arrays.fill(mascara, false);
		
		for (i=0; i<regla.length; i++) {
			for (j=0; j<tmp.getRule().length; j++) {
				if (regla[i].equals(tmp.getiCondition(j))) {
					mascara[j] = true;
				}
			}
		}
		
		for (i=0; i<mascara.length; i++)
			if (!mascara[i])
				return false;
		
		return true;
		
	}
	
}

