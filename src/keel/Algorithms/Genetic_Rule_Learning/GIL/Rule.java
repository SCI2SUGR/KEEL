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

public class Rule {
	
	Condition regla[];
	public double completeness;
	public double consistency;
	
	public Rule () {
	
	}
	
	/**Creates a random rule*/
	public Rule (myDataset train) {

		double prob = Randomize.Rand();
		int i;
		regla = new Condition[train.getnInputs()];
		
		for (i=0; i<train.getnInputs(); i++) {
			if (Randomize.Rand() < prob) {
				regla[i] = new Condition(i,train,true);				
			} else {
				regla[i] = new Condition(i,train);
			}
		}		
	}
	
	/**Creates a rule that matches with a certain example*/	
	public Rule (myDataset train, int ej) {
		
		double prob = Randomize.Rand();
		int i;
		regla = new Condition[train.getnInputs()];
		boolean codigo[];
		
		for (i=0; i<train.getnInputs(); i++) {
			if (Randomize.Rand() < prob) {
				codigo = new boolean[train.numberValues(i)];
				Arrays.fill(codigo, false);
				codigo[train.valueExample(ej, i)] = true;
				regla[i] = new Condition(i,codigo);				
			} else {
				regla[i] = new Condition(i,train);
			}
		}		
	}
	
	/**Creates a copy of a rule*/
	public Rule (Rule a) {
		
		int i;
		
		regla = new Condition[a.getRule().length];
		for (i=0; i<regla.length; i++) {
			regla[i] = new Condition(i,a.getiCondition(i).getCondition());
		}		
	}
	
	public Condition[] getRule() {
		return regla;
	}
	
	public Condition getiCondition(int i) {
		return regla[i];
	}
	
	public void setiCondition (int i, Condition c) {
		regla[i] = new Condition(i,c.getCondition());
	}
	
	public Rule[] ruleSplit(double p7B, double p7C, myDataset train) {
	
		Rule reglas[];
		int attr = 0;
		int i, j, k;
		boolean tmp[], tmp2[];
		
		attr = Randomize.Randint(0, regla.length);
		if (regla[attr].empty()) {
			for (i=0; i<regla[attr].getSizeCondition(); i++) {
				regla[attr].getCondition()[i] = true;
			}
		}
		
		if (train.getTipo(attr) == myDataset.NOMINAL) {
			if (Randomize.Rand() < p7C) { //all values
				reglas = new Rule[regla[attr].getnValues()];
				for (i=0; i<reglas.length; i++)
					reglas[i] = new Rule(train);
				for (i=0; i<regla.length; i++) {
					if (i != attr) {
						for (j=0; j<reglas.length; j++) {
							reglas[j].setiCondition(i, new Condition(i,regla[i].getCondition()));
						}
					} else {	
						k = 0;
						for (j=0; j<reglas.length; j++) {
							tmp = new boolean[train.numberValues(i)];
							for ( ;k<train.numberValues(i) && !regla[i].getCondition()[k]; k++); 
							tmp[k] = true;
							reglas[j].setiCondition(i, new Condition(i,tmp));
							k++;
						}						
					}					
				}
			} else { //subset
				reglas = new Rule[2];
				reglas[0] = new Rule(train);
				reglas[1] = new Rule(train);
				for (i=0; i<regla.length; i++) {
					if (i != attr) {
						reglas[0].setiCondition(i, new Condition(i,regla[i].getCondition()));
						reglas[1].setiCondition(i, new Condition(i,regla[i].getCondition()));
					} else {	
						tmp = new boolean[train.numberValues(i)];
						tmp2 = new boolean[train.numberValues(i)];
						for (j=0; j<tmp.length; j++) {
							if (regla[i].getCondition()[j]) {
								if (Randomize.Rand() < 0.5) {
									tmp[j] = regla[i].getCondition()[j];
									tmp2[j] = !regla[i].getCondition()[j];
								} else {
									tmp2[j] = regla[i].getCondition()[j];
									tmp[j] = !regla[i].getCondition()[j];					
								}
							}
						}
						reglas[0].setiCondition(i, new Condition(i,tmp));
						reglas[1].setiCondition(i, new Condition(i,tmp2));
					}						
				}
			}
		} else {
			if (Randomize.Rand() < p7B) { //all values
				reglas = new Rule[regla[attr].getnValues()];
				for (i=0; i<reglas.length; i++)
					reglas[i] = new Rule(train);
				for (i=0; i<regla.length; i++) {
					if (i != attr) {
						for (j=0; j<reglas.length; j++) {
							reglas[j].setiCondition(i, new Condition(i,regla[i].getCondition()));
						}
					} else {	
						k = 0;
						for (j=0; j<reglas.length; j++) {
							tmp = new boolean[train.numberValues(i)];
							for ( ;k<train.numberValues(i) && !regla[i].getCondition()[k]; k++); 
							tmp[k] = true;
							reglas[j].setiCondition(i, new Condition(i,tmp));
							k++;
						}						
					}					
				}
			} else { //subset
				reglas = new Rule[2];
				reglas[0] = new Rule(train);
				reglas[1] = new Rule(train);
				for (i=0; i<regla.length; i++) {
					if (i != attr) {
						reglas[0].setiCondition(i, new Condition(i,regla[i].getCondition()));
						reglas[1].setiCondition(i, new Condition(i,regla[i].getCondition()));
					} else {	
						tmp = new boolean[train.numberValues(i)];
						tmp2 = new boolean[train.numberValues(i)];
						for (j=0; j<tmp.length; j++) {
							if (Randomize.Rand() < 0.5) {
								tmp[j] = regla[i].getCondition()[j];
								tmp2[j] = !regla[i].getCondition()[j];
							} else {
								tmp2[j] = regla[i].getCondition()[j];
								tmp[j] = !regla[i].getCondition()[j];								
							}
						}
						reglas[0].setiCondition(i, new Condition(i,tmp));
						reglas[1].setiCondition(i, new Condition(i,tmp2));												
					}					
				}
			}			
		}
		
		return reglas;
	}	

	public void conditionDrop() {
		
		int attr = 0;
		int i;
		int cont=0;
		
		/*Check that rule is not a complete domain rule*/
		for (i=0; i<regla.length; i++) {
			if (regla[i].empty()) {
				cont++;
			}
		}
		
		if (cont < regla.length) {
			do {
				attr = Randomize.Randint(0, regla.length);
			} while (regla[attr].empty());		
			regla[attr].vaciar();
		}
	}
	
	public Rule[] turningConjunctionIntoDisjunction (myDataset train) {
		
		Rule pareja[] = new Rule[2];
		int i;
		int ale;
		
		pareja[0] = new Rule(train);
		pareja[1] = new Rule(train);
		for (i=0; i<regla.length;i++) {
			ale = Randomize.Randint(0, 2);
			if (ale == 0) {
				pareja[0].setiCondition(i, regla[i]);
				pareja[1].setiCondition(i, new Condition(i,train));
			} else {
				pareja[1].setiCondition(i, regla[i]);
				pareja[0].setiCondition(i, new Condition(i,train));				
			}
		}		
		
		return pareja;		
	}
	
	public void conditionIntroduce (myDataset train) {
		
		Vector <Integer> cont = new Vector <Integer>();
		int i;
		int pos;
		
		for (i=0; i<regla.length; i++) {
			if (regla[i].empty()) {
				cont.add(new Integer(i));
			}
		}
		
		if (cont.size() > 0) {
			pos = Randomize.Randint(0, cont.size());
			regla[cont.elementAt(pos)] = new Condition(cont.elementAt(pos),train,true);
		}
	}
	
	public Rule[] ruleDirectedSplit (myDataset train, int classAct) {
		
		Vector <Integer> contain = new Vector <Integer> ();
		boolean rule[];
		boolean example[];
		int i, pos, id, j, k;
		Rule directed[];
		boolean dRules[][];
		int cont = 0;
		Vector <Integer> subreglas = new Vector <Integer> ();
		int contAnt = 0;
		boolean todosFalsos;

		directed = new Rule[train.getnInputs()];
		
		/*Search examples which match with the rule and belonging to the negative class*/		
		rule = this.toBitString();
		for (i=0; i<train.getnData(); i++) {
			if (train.getOutputAsInteger(i) != classAct) {
				example = Rule.toBitString(train, i);
				if (Rule.match(rule, example)) {
					contain.addElement(new Integer(i));
				}
			}
		}
		
		/*Choose a negative example of the covered by the rule*/
		if (contain.size() > 0){
			pos = Randomize.Randint(0, contain.size());
			id = contain.elementAt(pos);
			
			/*Create the new rule set which does not cover the negative example*/
			dRules = new boolean[train.getnInputs()][rule.length];
			for (i=0; i<rule.length; i++) {
				for (j=0; j<train.getnInputs(); j++) {
					dRules[j][i] = rule[i];
				}
			}
			
			for (i=0; i<train.getnInputs(); i++) {
				subreglas.addElement(i);
			}
			
			example = Rule.toBitString(train, id);
			for (i=0, k=-1; i<rule.length; i++) {
				if (i>=cont) {
					if (k >=0) {
						todosFalsos = true;
						for (j=contAnt; j<cont && todosFalsos; j++) {
							if (dRules[k][j] == true)
								todosFalsos = false;
						}
						if (todosFalsos) {
							subreglas.remove(new Integer(k));
						}
					}
					
					k++;
					contAnt = cont;
					cont += regla[k].getSizeCondition();					
				}
				if (example[i] && rule[i]) {
					dRules[k][i] = false;
				}
			}
		
			directed = new Rule[subreglas.size()];			
			for (i=0; i<subreglas.size(); i++) {
				directed[i] = new Rule (train);
				directed[i].fromBitString(dRules[subreglas.elementAt(i)], train);
			}
		} else {
			directed = new Rule[1];
			directed[0] = new Rule(this);
		}
		
		return directed;
	}
	
	boolean applyOperators (double p12, double p13, double p14, double condProb, double cons, double comp) {
		
		int i;
		boolean act = false;
		
		for (i=0; i<regla.length; i++) {
			if (Randomize.Rand() < p12) {
				regla[i].referenceChange();
				act = true;
			}
            if (Randomize.Rand() < (p13*(1.5-comp)*(0.5+cons))) {
            	regla[i].referenceExtension(condProb);
				act = true;
            }
            if (Randomize.Rand() < (p14*(1.5-comp)*(0.5+cons))) {
            	regla[i].referenceRestriction(condProb);
				act = true;
            }
		}

		return act;
	}	
	
	public boolean[] toBitString () {
	
		int cont= 0, i, j, k;
		boolean cadena[];
		
		for (i=0; i<regla.length; i++) {
			cont += regla[i].getSizeCondition();
		}
		
		cadena = new boolean[cont];
		
		for (i=0, k=0; i<regla.length; i++) {
			if (regla[i].empty()) {
				for (j=0; j<regla[i].getSizeCondition(); j++, k++) {
					cadena[k] = true;
				}
			} else {
				for (j=0; j<regla[i].getSizeCondition(); j++, k++) {
					cadena[k] = regla[i].getiBit(j);
				}				
			}
		}
		
		return cadena;		
	}
	
	public void fromBitString (boolean cadena[], myDataset train) {
		
		int i, j, k;
		boolean vacio;
		boolean codigo[];
		
		for (i=0, k=0; i<regla.length; i++) {
			vacio = true;
			for (j=k; j<(regla[i].getSizeCondition()+k) && vacio; j++) {
				if (cadena[j] == false)
					vacio = false; 
			}
			if (vacio) {
				regla[i] = new Condition(i, train);
				k += regla[i].getSizeCondition();
			} else {
				codigo = new boolean[regla[i].getSizeCondition()];
				for (j=0; j<codigo.length; j++, k++) {
					codigo[j] = cadena[k];
				}
				regla[i] = new Condition (i,codigo);
			}
		}	
		
	}
	
	public static boolean match (boolean rule[], boolean example[]) {		
		
		int i;
		
		for (i=0; i<rule.length; i++) {
			if (example[i] && !rule[i])
				return false;
		}
		
		return true;		
	}
	
    /**
     * It return a bit string correspoding to a certain example
     * @param train myDataset input dataset
     * @param ej identification of the example within the train set
     */
    public static boolean[] toBitString (myDataset train, int ej) {

		int cont= 0, i, j, k;
		boolean cadena[];
		boolean codigo[];
		
		for (i=0; i<train.getnInputs(); i++) {
			cont += train.numberValues(i);
		}

		cadena = new boolean[cont];
		
		for (i=0, k=0; i<train.getnInputs(); i++) {				
			codigo = new boolean[train.numberValues(i)];
			Arrays.fill(codigo, false);
			codigo[train.valueExample(ej, i)] = true;
			for (j=0; j<codigo.length; j++, k++) {
				cadena[k] = codigo[j];
			}
		}
		
		return cadena;
    }	

	double computeCompleteness(myDataset train, int classAct) {
		
		int i;
		boolean example[];
		boolean rule[];
		int e = 0, E = 0;
		
		for (i=0; i<train.getnData(); i++) {
			example = Rule.toBitString(train, i);
			rule = this.toBitString();
			if (Rule.match(rule, example)) {
				E++;
				if (train.getOutputAsInteger(i) == classAct) {
					e++;
				}
			}
		}
		
		completeness = (double)e / (double)E; 
		
		return completeness;
	}
	
	double computeConsistency(myDataset train, int classAct) {
		
		int i;
		boolean example[];
		boolean rule[];
		int e = 0, E = 0;
		
		for (i=0; i<train.getnData(); i++) {
			example = Rule.toBitString(train, i);
			rule = this.toBitString();
			if (Rule.match(rule, example)) {
				E++;
				if (train.getOutputAsInteger(i) != classAct) {
					e++;
				}
			}
		}
		
		consistency = 1.0 - ((double)e / (double)E); 
		
		return consistency;
	}

	public String toString (myDataset train) {
		
		int i;
		String cadena = "";
		int cont=0;
				
		for (i=0; i<regla.length; i++) {
			if (!regla[i].empty()) {
				cadena += regla[i].toString(train) + " ";				
			} else {
				cont++;
			}
		}
		
		if (cont == regla.length)
			cadena = "Complete Domain";
		return cadena;
	}
	
	
}

