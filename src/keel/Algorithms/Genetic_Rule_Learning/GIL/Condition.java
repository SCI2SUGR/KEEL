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

package keel.Algorithms.Genetic_Rule_Learning.GIL;

import org.core.*;
import java.util.*;

public class Condition {
	
	private boolean codigo[];
	int attribute;
	
	public Condition () {
		
	}
	
	/**Creates an empty condition*/
	public Condition (int attr, myDataset train) {
		
		attribute = attr;
		
		if (train.getTipo(attr) == myDataset.NOMINAL)
			codigo = new boolean[train.numberValues(attr)];
		else
			codigo = new boolean[1 + (int)(train.getMax(attr) - train.getMin(attr))];
		Arrays.fill(codigo, false);
	}
	
	/**Creates a random condition of an attribute*/
	public Condition (int attr, myDataset train, boolean random) {
		
		int i;
		
		attribute = attr;
		if (train.getTipo(attr) == myDataset.NOMINAL)
			codigo = new boolean[train.numberValues(attr)];
		else
			codigo = new boolean[1 + (int)(train.getMax(attr) - train.getMin(attr))];
		for (i=0; i<codigo.length; i++) {			
			codigo[i] = (Randomize.Rand()<0.5?true:false);
		}
		
		arreglar();
	}
	
	/**Creates a copy of a condition*/
	public Condition (int attr, boolean c[]) {

		int i;

		attribute = attr;
		codigo = new boolean[c.length];
		for (i=0; i<codigo.length; i++) {
			codigo[i] = c[i];
		}
		
		arreglar();
	}
	
	public boolean[] getCondition() {
		return codigo;
	}
	
	public boolean getiBit(int i) {
		return codigo[i];
	}
	
	public int getSizeCondition() {
		return codigo.length;
	}
	
	public int getnValues() {
		
		int cont = 0;
		
		for (int i=0; i<codigo.length; i++) {
			if (codigo[i])
				cont++;
		}
		
		return cont;
	}
	
	public boolean empty() {
		
		boolean vacio = true;
		
		for (int i=0; i<codigo.length; i++) {
			if (codigo[i])
				vacio = false;
		}
		
		return vacio;
	}
	
	public void vaciar() {
		
		for (int i=0; i<codigo.length; i++) {
			codigo[i] = false;
		}
	}
	
	public void referenceChange() {
		
		int pos;
		
		pos = Randomize.Randint(0, codigo.length);
		codigo[pos] = !codigo[pos];
		arreglar();
	}

	public void referenceExtension(double condProb) {
		
		int i;
		
		for (i=0; i<codigo.length; i++) {
			if (codigo[i] == false) {
				if (Randomize.Rand() < condProb)
					codigo[i] = true;
			}
		}
		arreglar();
	}

	public void referenceRestriction(double condProb) {
		
		int i;
		
		for (i=0; i<codigo.length; i++) {
			if (codigo[i] == true) {
				if (Randomize.Rand() < condProb)
					codigo[i] = false;
			}
		}
		arreglar();
	}

	public String toString (myDataset train) {
		
		int i;
		String cadena = "";
		boolean primer = true;
		
		cadena += train.nameAttribute(attribute) + " = [";
		for (i=0; i<codigo.length; i++) {
			if (codigo[i]) {
				if (primer) {
					cadena += train.valueAttribute(attribute, i);
					primer = false;
				} else {
					cadena += ", " + train.valueAttribute(attribute, i);
				}
			}
		}
		cadena += "]";
		
		return cadena;
	}
	
	private void arreglar() {
		
		int i;
		boolean p = true;
		
		for (i=0; i<codigo.length; i++){
			if (!codigo[i])
				p = false;
		}
		
		if (p == true) {
			for (i=0; i<codigo.length; i++)
				codigo[i] = false;
		}
	}

}

