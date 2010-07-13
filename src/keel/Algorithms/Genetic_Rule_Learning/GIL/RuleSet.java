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

public class RuleSet implements Comparable {
	
	Vector <Rule> reglas;
	public double fitness;
	public double completeness;
	public double consistency;
	public double cost;
	
	public RuleSet () {	
		reglas = new Vector <Rule> ();
	}
	
	/**It initializates a new rule set or chromosome (randomly and using positive examples)*/
	public RuleSet (myDataset train, int classAct) {
		
		int size, i, j, pos, ej;
		Vector <Integer> contain = new Vector <Integer> ();
		
		reglas = new Vector <Rule> ();
		size = Randomize.Randint(1, (train.getnData()/20+2));
		
		for (i=0; i<size; i++) {
			if (Randomize.Rand() < 0.5) {
				reglas.addElement(new Rule(train));				
			} else {
				/*Search examples belonging to the positive class*/		
				for (j=0; j<train.getnData(); j++) {
					if (train.getOutputAsInteger(j) == classAct) {
						contain.addElement(new Integer(j));
					}
				}
				
				if (contain.size() > 0) {
					pos = Randomize.Randint(0, contain.size());
					ej = contain.elementAt(pos);
					reglas.addElement(new Rule(train,ej));
				} else {
					reglas.addElement(new Rule(train));
				}
			}
		}		
	}
	
	/**It creates a copy of the r Rule Set*/	
	public RuleSet (RuleSet r) {
		
		int i;
		
		reglas = new Vector <Rule> ();
		for (i=0; i<r.getRuleSet().size(); i++) {
			reglas.addElement(new Rule(r.getRule(i)));
		}
	} 
	
	public Vector <Rule> getRuleSet () {
		return reglas;
	}
	
	public Rule getRule (int i) {
		return reglas.elementAt(i);
	}
	
	public void setiRule (int i, Rule rule) {
		reglas.setElementAt(rule, i);
	}
	
	public void insertRule (Rule rule) {
		reglas.insertElementAt(rule, 0);
	}
	
	public Rule dropiRule (int i) {
		return reglas.remove(i);
	}
	
	public void rulesExchange (RuleSet padre, double P1b) {
		
		int i;
		Rule tmp;
		
		for (i=0; i<reglas.size(); i++) {
			if (Randomize.Rand() < P1b) {
				tmp = reglas.remove(i);
				i--;
				padre.insertRule(tmp);
			}
		}		
		
		for (i=0; i<padre.getRuleSet().size(); i++) {
			if (Randomize.Rand() < P1b) {
				tmp = padre.dropiRule(i);
				i--;
				this.insertRule(tmp);
			}
		}
	}
	
	public void rulesCopy (RuleSet padre) {
		
		int pos;
		
		if (padre.getRuleSet().size() > 0) {		
			pos = Randomize.Randint(0, padre.getRuleSet().size());
			reglas.addElement(new Rule(padre.getRule(pos)));
		}
	}
	
	public void newEvent (myDataset train, int classAct) {
		
		boolean rules[][];
		int i, j, pos, id;
		boolean example[];
		boolean entra;
		Vector <Integer> contain = new Vector <Integer> ();
		Rule tmp;
		
		rules = new boolean[reglas.size()][];
		
		/*Search examples which match with the rule and belonging to the positive class*/
		for (i=0; i<reglas.size(); i++) {
			rules[i] = reglas.elementAt(i).toBitString();
		}
		for (i=0; i<train.getnData(); i++) {
			if (train.getOutputAsInteger(i) == classAct) {
				example = Rule.toBitString(train, i);
				entra = false;
				for (j=0; j<rules.length && !entra; j++) {
					if (Rule.match(rules[j], example)) {
						entra = true;
					}
				}
				if (!entra) {
					contain.addElement(new Integer(i));
				}
			}
		}
		
		/*Choose a positive example not covered by the rule*/
		if (contain.size() > 0){
			pos = Randomize.Randint(0, contain.size());
			id = contain.elementAt(pos);
			
			/*Create a new rule from the not covered positive example*/
			example = Rule.toBitString(train, id);
			tmp = new Rule(train);
			tmp.fromBitString(example, train);
			reglas.addElement(new Rule(tmp));
		}		
	}
	
	public void rulesGeneralization (myDataset train) {
		
		int pos1, pos2, i;
		boolean regla1[], regla2[];
		boolean generalRule[];
		Rule tmp;
		
		if (reglas.size() > 1) {
			pos1 = Randomize.Randint(0, reglas.size());
			do {
				pos2 = Randomize.Randint(0, reglas.size());				
			} while (pos1 == pos2);
			
			regla1 = reglas.elementAt(pos1).toBitString();
			regla2 = reglas.elementAt(pos2).toBitString();
			generalRule = new boolean[regla1.length];
			for (i=0; i<generalRule.length; i++) {
				if (regla1[i] || regla2[i]) {
					generalRule[i] = true;
				} else {
					generalRule[i] = false;
				}
			}
			
			if (pos1 > pos2) {
				reglas.remove(pos1);
				reglas.remove(pos2);
			} else {
				reglas.remove(pos2);
				reglas.remove(pos1);				
			}

			tmp = new Rule(train);
			tmp.fromBitString(generalRule, train);
			reglas.addElement(new Rule(tmp));
		}		
	}
	
	void rulesDrop () {
		
		int pos;
		
		if (reglas.size() > 0) {		
			pos = Randomize.Randint(0, reglas.size());		
			reglas.remove(pos);
		}
	}

	void rulesSpecialization (myDataset train) {
		
		int pos1, pos2, i;
		boolean regla1[], regla2[];
		boolean generalRule[];
		Rule tmp;
		
		if (reglas.size() > 1) {
			pos1 = Randomize.Randint(0, reglas.size());
			do {
				pos2 = Randomize.Randint(0, reglas.size());				
			} while (pos1 == pos2);
			
			regla1 = reglas.elementAt(pos1).toBitString();
			regla2 = reglas.elementAt(pos2).toBitString();
			generalRule = new boolean[regla1.length];
			for (i=0; i<generalRule.length; i++) {
				if (regla1[i] && regla2[i]) {
					generalRule[i] = true;
				} else {
					generalRule[i] = false;
				}
			}
			
			if (pos1 > pos2) {
				reglas.remove(pos1);
				reglas.remove(pos2);
			} else {
				reglas.remove(pos2);
				reglas.remove(pos1);				
			}

			tmp = new Rule(train);
			tmp.fromBitString(generalRule, train);
			reglas.addElement(new Rule(tmp));
		}		
	}
	
	boolean applyOperators (double p7a, double p7b, double p7c, double p8, double p9, double p10, double p11, double p12, double p13, double p14, double condProb, myDataset train, int classAct) {
		
		Rule tmpRule1[][];
		Rule tmpRule2[][];
		Rule tmpRule3[][];
		int i, j, l;
		double comp, cons;
		boolean act = false;
		
		tmpRule1 = new Rule[reglas.size()][];
		tmpRule2 = new Rule[reglas.size()][];
		tmpRule3 = new Rule[reglas.size()][];
		
		for (i=0; i<reglas.size();i++) {
			tmpRule1[i] = new Rule[0];
			tmpRule2[i] = new Rule[0];
			tmpRule3[i] = new Rule[0];
		}
		
		for (i=0, l=0; i<reglas.size(); i++,l++) {
			if (Randomize.Rand() < p7a) {
				tmpRule1[l] = reglas.elementAt(i).ruleSplit(p7b, p7c, train);
				reglas.remove(i);
				i--;
				act = true;
			} 
		}

		for (i=0, l=0; i<reglas.size(); i++,l++) {
			cons = reglas.elementAt(i).computeConsistency(train, classAct);
			comp = reglas.elementAt(i).computeCompleteness(train, classAct);
            if (Randomize.Rand() < (p8*(1.5-comp)*(0.5+cons))) {
            	reglas.elementAt(i).conditionDrop();
				act = true;
            }			
            if (Randomize.Rand() < (p9*(1.5-comp)*(0.5+cons))) {
				tmpRule2[l] = reglas.elementAt(i).turningConjunctionIntoDisjunction(train);
				reglas.remove(i);
				i--;
				act = true;
            } 		
		}

		for (i=0, l=0; i<reglas.size(); i++,l++) {
			cons = reglas.elementAt(i).computeConsistency(train, classAct);
			comp = reglas.elementAt(i).computeCompleteness(train, classAct);
            if (Randomize.Rand() < (p10*(0.5+comp)*(1.5-cons))) {
            	reglas.elementAt(i).conditionIntroduce(train);
				act = true;
            }			
            if (Randomize.Rand() < (p11*(0.5+comp)*(1.5-cons))) {
				tmpRule3[l] = reglas.elementAt(i).ruleDirectedSplit(train, classAct);
				reglas.remove(i);
				i--;
				act = true;
            } 		
		}
		
		for (i=0; i<tmpRule1.length; i++) {
			for (j=0; j<tmpRule1[i].length; j++) {
				reglas.add(new Rule(tmpRule1[i][j]));
			}
		}
		for (i=0; i<tmpRule2.length; i++) {
			for (j=0; j<tmpRule2[i].length; j++) {
				reglas.add(new Rule(tmpRule2[i][j]));
			}
		}
		for (i=0; i<tmpRule3.length; i++) {
			for (j=0; j<tmpRule3[i].length; j++) {
				reglas.add(new Rule(tmpRule3[i][j]));
			}
		}
		
		for (i=0; i<reglas.size(); i++) {
			cons = reglas.elementAt(i).computeConsistency(train, classAct);
			comp = reglas.elementAt(i).computeCompleteness(train, classAct);
			act |= reglas.elementAt(i).applyOperators(p12, p13, p14, condProb, cons, comp);
		}
		
		return act;
	}
	
	double computeCost() {
		
		int i, j;
		int contCond = 0;
		
		for (i=0; i<reglas.size(); i++) {
			for (j=0; j<reglas.elementAt(i).getRule().length; j++) {
				if (!reglas.elementAt(i).getRule()[j].empty()) {
					contCond++;
				}
			}
		}
		
		cost = (2.0 * (double)reglas.size()) + (double)contCond;
		
		return cost;		
	}
	
	double computeCompleteness(myDataset train, int classAct) {
		
		int i, j;
		boolean example[];
		boolean rule[];
		int e = 0, E = 0;
		boolean valido;
		
		for (i=0; i<train.getnData(); i++) {
			if (train.getOutputAsInteger(i) == classAct) {
				example = Rule.toBitString(train, i);
				valido = false;
				for (j=0; j<reglas.size(); j++) {
					rule = reglas.elementAt(j).toBitString();
					if (Rule.match(rule, example)) {
						valido = true;
					}
				}
				E++;
				if (valido)
					e++;
			}
		}
		
		completeness = (double)e / (double)E; 
		
		return completeness;
	}
	
	double computeConsistency(myDataset train, int classAct) {
		
		int i, j;
		boolean example[];
		boolean rule[];
		int e = 0, E = 0;
		boolean valido;
		
		for (i=0; i<train.getnData(); i++) {
			if (train.getOutputAsInteger(i) != classAct) {
				example = Rule.toBitString(train, i);
				valido = false;
				for (j=0; j<reglas.size(); j++) {
					rule = reglas.elementAt(j).toBitString();
					if (Rule.match(rule, example)) {
						valido = true;
					}
				}
				E++;
				if (valido)
					e++;
			}
		}
		
		consistency = 1.0 - ((double)e / (double)E); 
		
		return consistency;
	}
	
	double computeFitness (myDataset train, int classAct, double minCost, double maxCost, double f, double w1, double w2, double w3) {
		
		double correctness;
		
		computeCompleteness(train, classAct);
		computeConsistency(train, classAct);
		
		correctness = (w1*completeness + w2*consistency) / (w1+w2);
		
		fitness = correctness * Math.pow(1 + w3 * (1 - ((cost - minCost)/(maxCost - minCost))), f);
		
		return fitness;
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
		
		for (i=0; i<reglas.size(); i++) {
			cadena += reglas.elementAt(i).toString(train) + "\n";
		}
		
		return cadena;
	}
	
}

