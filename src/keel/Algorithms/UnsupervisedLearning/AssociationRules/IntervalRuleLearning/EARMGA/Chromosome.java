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

/*
 * Note: this class has a natural ordering that is inconsistent with equals.
 */

import java.util.*;

public class Chromosome implements Comparable {
	private ArrayList<Gene> genes;
	private int lengthAnt;
	private double fitness;
	private double supportAnt;
	private double supportCon;
	private double supportAll;
	private boolean [] use;
	private int nVars;

	public Chromosome(int lengthAnt, int nVars) {
		this.genes = new ArrayList<Gene>();
		this.lengthAnt = lengthAnt;
		this.nVars = nVars;
		this.use = new boolean[this.nVars];
		for (int i=0; i < this.nVars; i++)  this.use[i] = false;
	}
	
	public Chromosome(ArrayList<Gene> geneses, int lengthAnt, int nVars) {
		int i;

		this.genes = new ArrayList<Gene>();
		this.lengthAnt = lengthAnt;
		this.nVars = nVars;

		this.use = new boolean[this.nVars];
		for (i=0; i < this.nVars; i++)  this.use[i] = false;
		
		for (i=0; i < geneses.size(); i++) {
			this.genes.add((geneses.get(i)).copy());
			this.use[(geneses.get(i)).getAttr()] = true;
		}
	}

	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes, this.lengthAnt, this.nVars);
		chromo.fitness = this.fitness;
		chromo.supportAnt = this.supportAnt;
		chromo.supportCon = this.supportCon;
		chromo.supportAll = this.supportAll;

		return  chromo;
	}

	public void setLengthAnt(int length) {
		this.lengthAnt = length;
	}	

	public int getLengthAnt() {
		return (this.lengthAnt);
	}	

	public void setSupportAnt(double value) {
		this.supportAnt = value;
	}	

	public double getSupportAnt() {
		return (this.supportAnt);
	}	

	public void setSupportCon(double value) {
		this.supportCon = value;
	}	

	public double getSupportCon() {
		return (this.supportCon);
	}	

	public void setSupportAll(double value) {
		this.supportAll = value;
	}	

	public double getSupportAll() {
		return (this.supportAll);
	}	

	public void add(Gene gen) {
		this.genes.add(gen.copy());
		this.use[gen.getAttr()] = true;
	}

	public ArrayList<Gene> getGenes() {
		return this.genes;
	}

	public Gene getGen(int i) {
		return this.genes.get(i);
	}

	public int length() {
		return this.genes.size();
	}

	public double getFit() {
		return this.fitness;
	}

	public void setFit(double fitness) {
		this.fitness = fitness;
	}	

	public boolean isUsed (int attr) {
		return (this.use[attr]);
	}	

	public void onUsed (int attr) {
		this.use[attr] = true;
	}	

	public void offUsed (int attr) {
		this.use[attr] = false;
	}	

	public boolean isSub (Chromosome chromo) {
		int i, j;
		boolean stop;

		if (this.length() > chromo.length())  return (false);

		for (i=0; i<this.length(); i++)
			if (!chromo.isUsed(this.getGen(i).getAttr()))  return (false);

		for (i=0; i<this.length(); i++) {
			stop = false;
			for (j=0; j<chromo.length() && !stop; j++) {
				if (this.getGen(i).getAttr() == chromo.getGen(j).getAttr()) {
					if (this.getGen(i).isSubValue(chromo.getGen(j)))  stop = true;
					else  j = chromo.length();
				}
		    }

			if (!stop)  return (false);
		}

		return (true);
	}	

	public boolean isEqual (Chromosome chromo) {
		int i, j;
		boolean found;

		if (this.lengthAnt != chromo.lengthAnt)  return (false);

		for (i=0; i<chromo.genes.size(); i++)
			if (!this.isUsed(chromo.genes.get(i).getAttr()))  return (false);

		for (i=0; i<=this.lengthAnt; i++) {
			found = false;
			for (j=0; j<=this.lengthAnt && !found; j++) {
				if (chromo.genes.get(i).getAttr() == this.genes.get(j).getAttr()) {
					if (chromo.genes.get(i).isEqualValue(this.genes.get(j)))  found = true;
					else  j = this.lengthAnt;
				}
		    }

			if (!found)  return (false);
		}

		for (i=this.lengthAnt + 1; i<chromo.genes.size(); i++) {
			found = false;
			for (j=this.lengthAnt + 1; j<this.genes.size() && !found; j++) {
				if (chromo.genes.get(i).getAttr() == this.genes.get(j).getAttr()) {
					if (chromo.genes.get(i).isEqualValue(this.genes.get(j)))  found = true;
					else  j = this.genes.size();
				}
		    }

			if (!found)  return (false);
		}


		return (true);
	}	

	public int compareTo (Object chr) {
		if (((Chromosome) chr).fitness < this.fitness)  return -1;
		else if (((Chromosome) chr).fitness > this.fitness)  return 1;
//		else {
//			if (((Chromosome) chr).supportAll < this.supportAll)  return -1;
//			else if (((Chromosome) chr).supportAll > this.supportAll)  return 1;
//	    }
		else  return 0;
	}

	public String toString() {
		String str = "Size: " + this.genes.size() + "; Fit: " + this.fitness + "\n";
		
		for (int i=0; i < this.genes.size(); i++)  str += this.genes.get(i) + "\n";

		return str;
	}
}

