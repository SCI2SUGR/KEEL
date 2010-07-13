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

import org.core.*;

import java.util.*;

public class RuleSet implements Comparable {
	
	Rule reglas[];
	public double fitness;
	
	public RuleSet () {
		
	}
	
	/**It initializates a new rule set or chromosome (using rules from a previous level)*/
	public RuleSet (Vector <Rule> conjR) {

		Condition almacen[];
		int i, j, k;
		int L;
		int baraja[];
		Condition cdotmp[];
		int tmp, pos;
		
		almacen = new Condition[conjR.size()*conjR.elementAt(0).getRule().length];
		for (i=0, k=0; i<conjR.size(); i++) {
			for (j=0; j<conjR.elementAt(i).getRule().length; j++, k++) {
				almacen[k] = new Condition(conjR.elementAt(i).getiCondition(j));
			}
		}
		
		L = conjR.elementAt(0).getRule().length;
		baraja = new int[almacen.length];
		for (i=0; i<almacen.length; i++) {
			baraja[i] = i;
		}
		
		cdotmp = new Condition[L+1];
		reglas = new Rule[conjR.size()];
		for (i=0; i<reglas.length; i++) {			
			for (j=0; j<L+1; j++) {
				pos = Randomize.Randint(j, baraja.length);
				tmp = baraja[pos];
				baraja[pos] = baraja[j];
				baraja[j] = tmp;
				cdotmp[j] = new Condition(almacen[baraja[j]]);
			}
			reglas[i] = new Rule(cdotmp);
		}		
	}
	
	/**It creates a copy of the r Rule Set*/	
	public RuleSet (RuleSet r) {
		
		int i;
		
		reglas = new Rule[r.getRuleSet().length];
		for (i=0; i<reglas.length; i++) {
			reglas[i] = new Rule(r.getRule(i));
		}
	} 
	
	public Rule[] getRuleSet () {
		return reglas;
	}
	
	public Rule getRule (int i) {
		return reglas[i];
	}
	
	public void setiRule (int i, Rule rule) {
		reglas[i] = new Rule(rule);
	}
	
	public static void crossover1 (RuleSet hijo1, RuleSet hijo2) {
		
		int pos1, pos2;
		int tmp;
		Rule ruleTmp;
		int i;
		
		pos1 = Randomize.Randint(0, hijo1.getRuleSet().length);
		pos2 = Randomize.Randint(0, hijo1.getRuleSet().length);
		
		if (pos2 < pos1) {
			tmp = pos1;
			pos1 = pos2;
			pos2 = tmp;
		}
		
		for (i=pos1; i<=pos2; i++) {
			ruleTmp = new Rule(hijo1.getRule(i));
			hijo1.setiRule(i, hijo2.getRule(i));
			hijo2.setiRule(i, ruleTmp);
		}		
	}
	
	public static void crossover2 (RuleSet hijo1, RuleSet hijo2) {

		int pos1, pos2, pos3;
		int tmp;
		Rule ruleTmp;
		int i;
		Condition conditionTmp;
		
		pos1 = Randomize.Randint(0, hijo1.getRuleSet().length);
		pos2 = Randomize.Randint(0, hijo1.getRuleSet().length);
		pos3 = Randomize.Randint(0, hijo1.getRule(0).getRule().length);
		
		if (pos2 < pos1) {
			tmp = pos1;
			pos1 = pos2;
			pos2 = tmp;
		}
		
		for (i=pos1+1; i<pos2; i++) {
			ruleTmp = new Rule(hijo1.getRule(i));
			hijo1.setiRule(i, hijo2.getRule(i));
			hijo2.setiRule(i, ruleTmp);
		}
		
		for (i=pos3; i<hijo1.getRule(pos1).getRule().length; i++) {
			conditionTmp = new Condition(hijo1.getRule(pos1).getRule()[i]);
			hijo1.getRule(pos1).setiCondition(i, hijo2.getRule(pos1).getRule()[i]);
			hijo2.getRule(pos1).setiCondition(i, conditionTmp);			
		}

		for (i=0; i<=pos3; i++) {
			conditionTmp = new Condition(hijo1.getRule(pos2).getRule()[i]);
			hijo1.getRule(pos2).setiCondition(i, hijo2.getRule(pos1).getRule()[i]);
			hijo2.getRule(pos2).setiCondition(i, conditionTmp);			
		}
	}
	
	public static void mutation (RuleSet cromosoma, Vector <Rule> conjR, double pMut, int data[][], int classData[], int infoAttr[], Vector <Rule> contenedor, int nClases) {

		Condition almacen[];
		int i, j, k;
		double gain, gain2;
		RuleSet tmp;
		
		almacen = new Condition[conjR.size()*conjR.elementAt(0).getRule().length];
		for (i=0, k=0; i<conjR.size(); i++) {
			for (j=0; j<conjR.elementAt(i).getRule().length; j++, k++) {
				almacen[k] = new Condition(conjR.elementAt(i).getiCondition(j));
			}
		}
		
		for (i=0; i<cromosoma.getRuleSet().length; i++) {
			if (Randomize.Rand() < pMut) {
				k = Randomize.Randint(0, cromosoma.getRule(i).getRule().length);
				gain = cromosoma.computeFitness(data, classData, infoAttr, contenedor, nClases);
				tmp = new RuleSet(cromosoma);
				for (j=0; j<almacen.length; j++) {
					tmp.getRule(i).setiCondition(k, almacen[j]);
					gain2 = tmp.computeFitness(data, classData, infoAttr, contenedor, nClases);
					if (gain2 > gain) {
						gain = gain2;
						cromosoma = new RuleSet(tmp);
					}
				}
			}
		}

	} 
	
	double computeFitness (int data[][], int classData[], int infoAttr[], Vector <Rule> contenedor, int nClases) {
		
		int i, j, k, l;
		boolean match;
		double tmp1, tmp2;
		int pos = 0, classPredicted;
		double cont = 0;
		double Waip;
		
		for (i=0; i<data.length; i++) {
			classPredicted = -1;
			Waip = 0;
			
			/*Search a match of the example (beginning from the chromosome)*/
			for (j=0; j<reglas.length; j++) {
				match = true;
				for (k=0; k<reglas[j].getRule().length && match; k++) {
					if (data[i][reglas[j].getiCondition(k).getAttribute()] != reglas[j].getiCondition(k).getValue()) {
						match = false;
					}
				}
				if (match) {
					tmp1 = Double.NEGATIVE_INFINITY;
					for (l=0; l<nClases; l++) {
						tmp2 = 0;
						for (k=0; k<reglas[j].getRule().length; k++) {
							tmp2 += computeWeightEvidence (data, classData, reglas[j].getiCondition(k), l, infoAttr);
						}
						if (tmp2 > tmp1) {
							tmp1 = tmp2;
							pos = l;
						}
					}
					if (tmp1 > Waip) {
						classPredicted = pos;
						Waip = tmp1;
					}
				}
			}

			/*Search a match of the example (following by the container)*/
			for (j=contenedor.size()-1; j>=0; j--) {
				match = true;
				for (k=0; k<contenedor.elementAt(j).getRule().length && match; k++) {
					if (data[i][contenedor.elementAt(j).getiCondition(k).getAttribute()] != contenedor.elementAt(j).getiCondition(k).getValue()) {
						match = false;
					}
				}
				if (match) {
					tmp1 = Double.NEGATIVE_INFINITY;
					for (l=0; l<nClases; l++) {
						tmp2 = 0;
						for (k=0; k<contenedor.elementAt(j).getRule().length; k++) {
							tmp2 += computeWeightEvidence (data, classData, contenedor.elementAt(j).getiCondition(k), l, infoAttr);
						}
						if (tmp2 > tmp1) {
							tmp1 = tmp2;
							pos = l;
						}
					}
					if (tmp1 > Waip) {
						classPredicted = pos;
						Waip = tmp1;
					}
				}
			}
			
			if (classPredicted == classData[i]) {
				cont++;
			}
		}
		
		fitness = cont / (double) data.length;
		
		return fitness;
	}

    public  static double computeWeightEvidence (int data[][], int classData[], Condition cond, int clase, int infoAttr[]) {

    	double sigmaAi, sigma, B;
    	double ProbPos, ProbNeg;
    	double prob;
    	
    	prob = Prob(data, classData, cond, clase);
    	
    	sigmaAi = 0.5 * (1 - prob);
    	
    	sigma = 0.5 * (1 - infoAttr.length * prob);
    	
    	B = sigma / (sigma + sigmaAi);
    	
    	ProbPos = B * ProbCondPositive(data, classData, cond, clase) + (1-B) * ProbClass(data, classData, cond, clase);
    	ProbNeg = B * ProbCondNegative(data, classData, cond, clase) + (1-B) * ProbClass(data, classData, cond, clase);
    	
    	return (Math.log(ProbPos/ProbNeg)/Math.log(Math.E));    	
    }
    
    public static double ProbCondPositive (int data[][], int classData[], Condition cond, int clase) {

    	int i;
    	double tmp1, tmp2;
    	boolean hecho;
    	
    	tmp1 = 0;
    	tmp2 = 0;
    	for (i=0; i<data.length; i++) {
    		if (data[i][cond.getAttribute()] == cond.getValue()) {
    			tmp1++;
    			hecho = true;
    			if (classData[i] != clase) {
    				hecho = false;
    			}
    			if (hecho) {
    				tmp2++;
    			}
    		}
    	}
    	
    	return tmp2/tmp1;

    }

    public static double ProbCondNegative (int data[][], int classData[], Condition cond, int clase) {
    	
    	int i;
    	double tmp1, tmp2;
    	boolean hecho;
    	
    	tmp1 = 0;
    	tmp2 = 0;
    	for (i=0; i<data.length; i++) {
    		if (data[i][cond.getAttribute()] != cond.getValue()) {
    			tmp1++;
    			hecho = true;
    			if (classData[i] != clase) {
    				hecho = false;
    			}
    			if (hecho) {
    				tmp2++;
    			}
    		}
    	}
    	
    	return tmp2/tmp1;
    }

    public static double ProbClass (int data[][], int classData[], Condition cond, int clase) {

    	int i;
    	double tmp1, tmp3;
    	boolean hecho;
    	
    	tmp3 = tmp1 = 0;
    	for (i=0; i<data.length; i++) {
    		if (classData[i] == clase) {
    			tmp1++;
    		}
    		hecho = true;
  			if (data[i][cond.getAttribute()] == -1) {
   				hecho = false;
   			}
    		if (hecho) {
    			tmp3++;
    		}
    	}
    	
    	return tmp1/tmp3;
    }

    public static double Prob (int data[][], int classData[], Condition cond, int clase) {
    	
    	int i;
    	double tmp3;
    	boolean hecho;
    	
    	tmp3 = 0;
    	for (i=0; i<data.length; i++) {
    		hecho = true;
    		if (data[i][cond.getAttribute()] == -1) {
    			hecho = false;
    		}
    		if (hecho) {
    			tmp3++;
    		}
    	}
    	
    	return tmp3 / (double)data.length;
    }
	
	/**Function that lets compare cromosomes to sort easily*/
	public int compareTo (Object o1) {

	    if (this.fitness > ((RuleSet)o1).fitness)
	      return -1;
	    else if (this.fitness < ((RuleSet)o1).fitness)
	      return 1;
	    else return 0;
	    
	}

	public String toString (myDataset train) {
		
		int i;
		String cadena = "";
		
		for (i=0; i<reglas.length; i++) {
			cadena += reglas[i].toString(train) + "\n";
		}
		
		return cadena;
	}

	public boolean equals (Object a) {
		
		int i, j;
		RuleSet tmp = (RuleSet) a;
		boolean mascara[] = new boolean [reglas.length];
		
		Arrays.fill(mascara, false);
		
		for (i=0; i<reglas.length; i++) {
			for (j=0; j<tmp.getRuleSet().length; j++) {
				if (reglas[i].equals(tmp.getRule(i))) {
					mascara[i] = true;
				}
			}
		}
		
		for (i=0; i<mascara.length; i++)
			if (!mascara[i])
				return false;
		
		return true;
		
	}

}

