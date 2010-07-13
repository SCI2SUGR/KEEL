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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

import java.util.*;

public class Gene {
	
	public static final int NOMINAL = 0;
	public static final int INTEGER = 1;
	public static final int REAL = 2;

	private int attr;
	private int type;
	private ArrayList<Integer> value;

	public Gene() {
		this.value = new ArrayList<Integer>();
	}

	public Gene copy() {
		int i;
		Gene gen = new Gene();
		
		gen.attr = this.attr;
		gen.type = this.type;
		for (i=0; i < this.value.size(); i++) gen.value.add(new Integer(this.value.get(i).intValue()));

		return gen;
	}

	public int getAttr() {
		return attr;
	}

	public void setAttr(int attr) {
		this.attr = attr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public ArrayList<Integer> getValue() {
		return this.value;
	}

	public int getValue(int pos) {
		return this.value.get(pos).intValue();
	}

	public void setValue(ArrayList<Integer> newIntervals) {
		int i=0;

		this.value.clear();

		for (i=0; i < newIntervals.size(); i++)
			this.value.add(new Integer (newIntervals.get(i).intValue()));
	}

	public void clearValue() {
		this.value.clear();
	}
/*
	public void removeValue(int pos) {
		this.value.remove(pos);
	}
*/
	public void setValue(int inter) {
		this.value.clear();
		this.value.add(new Integer (inter));
	}

	public void addValue(int inter) {
		this.value.add(new Integer (inter));
	}

	public boolean isUsed(int inter) {
		int i;

		for (i=0; i < this.value.size(); i++) {
			if (this.value.get(i).intValue()==inter)  return (true);
		}

		return (false);
	}

	public int numIntervals() {
		return (this.value.size());
	}

	public boolean isEqualValue(Gene gen) {
		int i, j;
		boolean found;

		if (this.value.size() != gen.value.size())  return (false);
		for (i=0; i < this.value.size(); i++) {
			found = false;
			for (j=0; j < gen.value.size() && !found; j++) {
				if (this.value.get(i).intValue() == gen.value.get(j).intValue())  found = true;
			}

			if (!found)  return (false);
		}

		return (true);
	}

	public boolean isSubValue(Gene gen) {
		int i, j;
		boolean found;

		if (this.numIntervals() > gen.numIntervals())  return (false);
		for (i=0; i < this.numIntervals(); i++) {
			found = false;
			for (j=0; j < gen.numIntervals() && !found; j++) {
				if (this.getValue(i) == gen.getValue(j))  found = true;
			}

			if (!found)  return (false);
		}

		return (true);
	}



	public String toString() {
		return "A: " + attr + "; T: " + type + "; V: " + value;
	}
}

