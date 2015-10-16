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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.IVTURS;


import java.util.*;
import org.core.Randomize;

/**
 * <p>Title: Rule</p>
 * <p>Description: This class codes a Fuzzy Rule</p>
 * <p>Copyright: KEEL Copyright (c) 2008</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcala (University of Granada) 09/02/2011
 * @author Modified by Jose Antonio Sanz (University of Navarra) 19/12/2011
 * @author Modified by Alberto Fernandez (University of Jaen) 24/10/2013
 * @version 1.2
 * @since JDK1.6
 */
public class Rule implements Comparable {

	int[] antecedent;
	int clas, nAnts;
	double conf[], supp[], wracc[];
	DataBase dataBase;

	/**
	 * <p>
	 * Create a rule with another one
	 * </p>
	 * @param r This a rule
	 * @return A copy of r
	 */
	public Rule(Rule r) {
		this.antecedent = new int[r.antecedent.length];
		for (int k = 0; k < this.antecedent.length; k++) {
			this.antecedent[k] = r.antecedent[k];
		}

		this.clas = r.clas;
		this.dataBase = r.dataBase;
		this.conf = new double[2];
		this.supp = new double[2];
		this.wracc = new double[2];
		this.conf[0] = r.conf[0];
		this.conf[1] = r.conf[1];
		this.supp[0] = r.supp[0];
		this.supp[1] = r.supp[1];
		this.nAnts = r.nAnts;
		this.wracc[0] = r.wracc[0];
		this.wracc[1] = r.wracc[1];
	}

	/**
	* <p>
	* Create a new rule
	* </p>
	* @param dataBase The database
	* @return A rule
	*/
	public Rule(DataBase dataBase) {
		this.antecedent = new int[dataBase.numVariables()];
		for (int i = 0; i < this.antecedent.length; i++)  this.antecedent[i] = -1;  // Don't care
		this.clas = -1;
		this.dataBase = dataBase;
		this.conf = new double[2];
		this.supp = new double[2];
		this.wracc = new double[2];
		this.conf[0] = 0.0;
		this.conf[1] = 0.0;
		this.supp[0] = 0.0;
		this.supp[1] = 0.0;
		this.nAnts = 0;
		this.wracc[0] = 0.0;
		this.wracc[1] = 0.0;
	}

	/**
	* <p>
	* Clone
	* </p>
	* @return A copy of the rule
	*/
	public Rule clone() {
		Rule r = new Rule(this.dataBase);
		r.antecedent = new int[antecedent.length];
		for (int i = 0; i < this.antecedent.length; i++) {
			r.antecedent[i] = this.antecedent[i];
		}

		r.clas = this.clas;
		r.dataBase = this.dataBase;
		r.conf[0] = this.conf[0];
		r.conf[1] = this.conf[1];
		r.supp[0] = this.supp[0];
		r.supp[1] = this.supp[1];
		r.nAnts = this.nAnts;
		r.wracc[0] = this.wracc[0];
		r.wracc[1] = this.wracc[1];

		return (r);
	}

	public void asignaAntecedente(int [] antecedent){
		this.nAnts = 0;
		for (int i = 0; i < antecedent.length; i++) {
			this.antecedent[i] = antecedent[i];
			if (this.antecedent[i] > -1)  this.nAnts++;
		}
	}

	public void setConsequent(int clas) {
		this.clas = clas;
	}


	public double[] matching(double[] example) {
		return (this.degreeProduct(example));
	}

	private double[] degreeProduct(double[] example) {
		double degree[] = new double[2];
		double var1[] = new double[2];
		double dP[] = new double[2];

		degree[0] = degree[1] = 1.0;
		for (int i = 0; i < antecedent.length && degree[0] > 0.0; i++) {
			var1 = dataBase.matching(i, antecedent[i], example[i]);
			degree[0] *= var1[0];
			degree[1] *= var1[1];
		}
		dP[0] = degree[0] * this.conf[0];
		dP[1] = degree[1] * this.conf[1];
		return (dP);
	}

	public double[] similaridadJurio(double[] example){
		double degree[] = new double[2];
		double roL1,roU1,aux;
		double sim[] = new double[2];

		sim[0] = sim[1] = 1.0;
		for (int i = 0; i < antecedent.length; i++) {//&& sim[0] > 0.0
			degree = dataBase.matching(i, antecedent[i], example[i]);

			roL1 = 1;
			roU1 = 0;

			aux = Math.pow(Math.pow(degree[0], dataBase.aut2[i]),(1/dataBase.aut1[i]));
			roL1 = Math.min(roL1,aux); //singleton is A
			aux = Math.pow(Math.pow(degree[1], dataBase.aut2[i]),(1/dataBase.aut1[i]));
			roU1 = Math.max(roU1,aux); //singleton is A
			sim[0] *= roL1;
			sim[1] *= roU1;

			if ((sim[0] == 0) && (sim[1] == 0)) break;

		}
		sim[0] *= this.conf[0];
		sim[1] *= this.conf[1];
		return (sim);
	}

	public void setConfidence(Itemset itemset) {
		double var1[] = new double[2];
		double var2[] = new double[2];

		var1 = itemset.getSupportClass();
		var2 = itemset.getSupport();

		this.conf = division(var1, var2, 0);
	}

	public double[] division(double[] int1, double[] int2, int tipo){
		double resultado[] = new double[2];
		double aux, maximo, lower, upper, minimo;

		resultado[0] = resultado[1] = 0.0;

		if (tipo == 0){
			//DIVISION DE BENJA
			resultado[1] = int1[1] / int2[1];
			resultado[0] = int1[0] / int2[0];

			if (resultado[1]<resultado[0]){
				aux = resultado[1];
				resultado[1] = resultado[0];
				resultado[0] = aux;
			}
		}else if (tipo == 1){
			//WANG's DIVISION 
			int2[0] = int2[0]-int1[0];// for normalizing with the remaining classes
			int2[1] = int2[1]-int1[1];//for normalizing with the remaining classes
			if ((int1[1]-int1[0])>=(int2[1]-int2[0]))
				maximo = int1[1]-int1[0];
			else maximo = int2[1]-int2[0];
			lower = int1[0] + int2[0] + maximo;
			upper = int1[1] + int2[1] + maximo;
			if ((lower<=1)&&(upper>=1)){
				resultado[0] = int1[0];
				resultado[1] = int1[1];
			}else{
				lower = int1[0] + int2[0];
				upper = int1[1] + int2[1];
				if ((lower>1)||(upper<1)){
					resultado[0] = int1[0]/(int1[0]+int2[1]);
					resultado[1] = int1[1]/(int1[1]+int2[0]);
				}else{
					if ((lower<=1)&&(upper>=1)){
						upper = 1 - (int2[0]);
						lower = 1 - (int2[1]);
						if (int1[0]>=lower) maximo = int1[0];
						else maximo = lower;
						if (int1[1]<=upper) minimo = int1[1];
						else minimo = upper;
						resultado[0] = maximo;
						resultado[1] = minimo;
					}else{
						resultado[0] = int1[0]/(int1[0]+int2[1]);
						resultado[1] = int1[1]/(int1[1]+int2[0]);
					}
				}
			}
		}
		return(resultado);
	}

	public void setSupport(Itemset itemset) {
		this.supp[0] = itemset.getSupportClass()[0];
		this.supp[1] = itemset.getSupportClass()[1];
	}

	public void setWracc(double[] wracc) {
		this.wracc[0] = wracc[0];
		this.wracc[1] = wracc[1];
	}

	public double[] getConfidence() {
		return (this.conf);
	}

	public double[] getSupport() {
		return (this.supp);
	}

	public double[] getWracc() {
		return (this.wracc);
	}

	public int getClas() {
		return (this.clas);
	}

	public boolean isSubset(Rule a) {
		if ((this.clas != a.clas) || (this.nAnts > a.nAnts))  return (false);
		else {
			for (int k = 0; k < this.antecedent.length; k++) {
				if (this.antecedent[k] > -1) {
					if (this.antecedent[k] != a.antecedent[k])  return (false);
				}
			}
			return (true);
		}
	}
	
	/**
	* <p>
	* Calculate Wracc for this rule
	* </p>
	* @param train Training dataset
	* @param exampleWeight Weights of the patterns
	* @return the value of the measure Wracc for this rule
	*/
	public void calculateWracc (myDataset train, ArrayList<ExampleWeight> exampleWeight) {
		int i;
		double n_A, n_AC, n_C;//, degree;
		double degree[] = new double[2];
		ExampleWeight ex;

		n_A = n_AC = 0.0;
		n_C = 0.0;

		for (i=0; i < train.size(); i++) {
			ex = exampleWeight.get(i);

			if (ex.isActive()) {
				degree = this.matching(train.getExample(i));
				if (degree[0] > 0.0) {
					degree[0] *= ex.getWeight();
					degree[1] *= ex.getWeight();
					n_A += degree[0];

					if (train.getOutputAsInteger(i) == this.clas) {
						n_AC += degree[0];
						n_C += ex.getWeight();
					}
				}
				else if (train.getOutputAsInteger(i) == this.clas)  n_C += ex.getWeight();
			}
		}

		if ((n_A < 0.0000000001) || (n_AC < 0.0000000001) || (n_C < 0.0000000001))  {this.wracc[0] = this.wracc[1] = -1.0;}
		else  {this.wracc[0] = this.wracc[1] = (n_AC / n_C) * ((n_AC / n_A) - train.frecuentClass(this.clas));}
	}


	public int reduceWeight (myDataset train, ArrayList<ExampleWeight> exampleWeight) {
		int i, count;
		ExampleWeight ex;

		count = 0;

		for (i=0; i < train.size(); i++) {
			ex = exampleWeight.get(i);
			if (ex.isActive()) {
				if (this.matching(train.getExample(i))[0] > 0.0) {
					ex.incCount();
					if ((!ex.isActive()) && (train.getOutputAsInteger(i) == this.clas))  count++;
				}
			}
		}

		return (count);
	}



	public void setLabel(int pos, int label) {
		if ((antecedent[pos] < 0) && (label > -1))  this.nAnts++;
		if ((antecedent[pos] > -1) && (label < 0))  this.nAnts--;
		this.antecedent[pos] = label;
	}

	public int compareTo(Object a) {
		if (((Rule) a).wracc[0] < this.wracc[0])  return -1;
		if (((Rule) a).wracc[0] > this.wracc[0])  return 1;
		return 0;
	}

}
