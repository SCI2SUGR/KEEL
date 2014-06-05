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

import org.core.Randomize;
import java.lang.*;

/**
 * <p>Title: Individual</p>
 *
 * <p>Description: This class contains the representation of the individuals of the population (CHC Algorithm)</p>
 *
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2011
 * @author Modified by Jose Antonio Sanz (University of Navarra) 19/12/2011
 * @author Modified by Alberto Fernandez (University of Jaen) 24/10/2013
 * @version 1.2
 * @since JDK1.6
 */
public class Individual implements Comparable{
	double[] gene;
	int[] geneR;
	double fitness, accuracy, w1;
	int n_e, nGenes, nSim, nAmp, nLat, ajuste;
	RuleBase ruleBase;
	double[][] gInt;//min and max values for the genes

	public Individual() {
	}

	/**
	 * <p>
	 * Builder
	 * </p>
	 * @param ruleBase Rule set
	 * @param dataBase Database
	 * @param w1 Weight for the fitness function
	 * @return Return a individual
	 */
	public Individual(RuleBase ruleBase, DataBase dataBase, double w1, int tipoAjuste) {

		this.ajuste = tipoAjuste;
		this.ruleBase = ruleBase;
		this.w1 = w1;
		this.fitness = Double.NEGATIVE_INFINITY;
		this.accuracy = 0.0;
		this.n_e = 0;
		if (this.ajuste == 1){
			this.nSim = 2*dataBase.numVariables();
			this.nAmp = 0; //amplitude tuning is not carried out
			this.nLat = 0; //lateral tuning is not carried out
		}
		else if (this.ajuste == 2){
			//amplitude tuning
			this.nAmp = dataBase.getnLabelsReal();
			this.nSim = 0; //similarity tuning is not carried out
			this.nLat = 0; //lateral tuning is not carried out
		}
		else if (this.ajuste == 3){
			//similarity + amplitude tuning
			this.nSim = 2*dataBase.numVariables();
			this.nAmp = dataBase.getnLabelsReal();
			this.nLat = 0; //lateral tuning is not carried out
		}
		else if (this.ajuste == 4){
			//all tuning processes carried out
			this.nSim = 2*dataBase.numVariables();
			this.nAmp = dataBase.getnLabelsReal();
			this.nLat = dataBase.getnLabelsReal();
		}
		else if (this.ajuste == 5){
			//similarity + lateral tuning
			this.nSim = 2*dataBase.numVariables();
			this.nAmp = 0;
			this.nLat = dataBase.getnLabelsReal();
		}
		this.nGenes = this.nSim + this.nAmp + this.nLat; //number of genes needed

		if (this.nGenes > 0){
			this.gene = new double[this.nGenes];
			this.gInt = new double[this.nGenes][];
			for (int i = 0; i < this.nSim; i++){
				this.gInt[i] = new double[2];
				//similarity
				this.gInt[i][0] = 0.01;
				this.gInt[i][1] = 1.99;
			}
			for (int i = this.nSim; i < (this.nSim+this.nAmp); i++){ //genes for carrying out the amplitude tuning
				this.gInt[i] = new double[2];
				//amplitude
				this.gInt[i][0] = 0.0;
				this.gInt[i][1] = 1.0;
			}
			for (int i = (this.nSim+this.nAmp); i < this.nGenes; i++){ //nGenes for reaching the end of the chromosome
				this.gInt[i] = new double[2];
				//lateral
				this.gInt[i][0] = 0.0;
				this.gInt[i][1] = 1.0;
			}
		}

		//rule selection
		this.geneR = new int[this.ruleBase.size()];
	}

	/**
	* <p>
	* Clone
	* </p>
	* @return A copy of the individual
	*/
	public Individual clone(){
		Individual ind = new Individual();

		ind.ruleBase = this.ruleBase;
		ind.w1 = this.w1;
		ind.fitness = this.fitness;
		ind.accuracy = this.accuracy;
		ind.n_e = this.n_e;
		ind.nGenes = this.nGenes;
		ind.nSim = this.nSim;
		ind.nAmp = this.nAmp;
		ind.nLat = this.nLat;
		ind.ajuste = this.ajuste;

		if (this.nGenes > 0)  {
			ind.gene = new double[this.nGenes];
			ind.gInt = new double[this.nGenes][];
			for (int j = 0; j < this.nGenes; j++){
				ind.gene[j] = this.gene[j];
				ind.gInt[j] = new double[2];
				ind.gInt[j][0] = this.gInt[j][0];
				ind.gInt[j][1] = this.gInt[j][1];
			}
		}

		//rule selection
		ind.geneR = new int[this.geneR.length];
		for (int j = 0; j < this.geneR.length; j++)  ind.geneR[j] = this.geneR[j];

		return ind;
	}


	public void reset() {

		//automorphisms tuning
		if (this.ajuste == 1){
			if (this.nGenes > 0) {
				for (int i = 0; i < this.nGenes; i++)  this.gene[i] = 1.0;
			}
		}


		//rule selection
		//initialize with values '1' for taking into account all rules
		for (int i = 0; i < this.geneR.length; i++)  this.geneR[i] = 1;
	}

	public void resetAmp(int numInd) {
		if(this.ajuste == 2)
		{
			//amplitude tuning
			if (numInd == 0)
				if (this.nGenes > 0) {
					for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 0.5;
				}
				else if (numInd == 1)
					if (this.nGenes > 0) {
						for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 0.0;
					}
					else if (numInd == 2)
						if (this.nGenes > 0) {
							for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 1.0;
						}

			//rule selection
			//initialize with values '1' for taking into account all rules
			for (int i = 0; i < this.geneR.length; i++)  this.geneR[i] = 1;
		}
		else if(this.ajuste == 3)
		{
			//amplitude + similarity tuning
			if (numInd == 0)
				if (this.nGenes > 0) {
					for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
					for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 0.5;
				}
				else if (numInd == 1)
					if (this.nGenes > 0) {
						for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
						for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 0.0;
					}
					else if (numInd == 2)
						if (this.nGenes > 0) {
							for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
							for (int i = this.nSim; i < this.nGenes; i++)  this.gene[i] = 1.0;
						}

			//rule selection
			//initialize with values '1' for taking into account all rules
			for (int i = 0; i < this.geneR.length; i++)  this.geneR[i] = 1;
		}
		else if(this.ajuste == 4)
		{
			//amplitude + similarity + lateral tuning
			if (numInd == 0)
				if (this.nGenes > 0) {
					for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
					for (int i = this.nSim; i < (this.nSim+this.nAmp); i++)  this.gene[i] = 0.5;
					for (int i = (this.nSim+this.nAmp); i < this.nGenes; i++)  this.gene[i] = 0.5;
				}
				else if (numInd == 1)
					if (this.nGenes > 0) {
						for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
						for (int i = this.nSim; i < (this.nSim+this.nAmp); i++)  this.gene[i] = 0.0;
						for (int i = (this.nSim+this.nAmp); i < this.nGenes; i++)  this.gene[i] = 0.5;
					}
					else if (numInd == 2)
						if (this.nGenes > 0) {
							for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
							for (int i = this.nSim; i < (this.nSim+this.nAmp); i++)  this.gene[i] = 1.0;
							for (int i = (this.nSim+this.nAmp); i < this.nGenes; i++)  this.gene[i] = 0.5;
						}

			//rule selection
			//initialize with values '1' for taking into account all rules
			for (int i = 0; i < this.geneR.length; i++)  this.geneR[i] = 1;
		}
		else if(this.ajuste == 5)
		{
			//similarity + lateral tuning
			if (numInd == 0)
				if (this.nGenes > 0) {
					for (int i = 0; i < this.nSim; i++)  this.gene[i] = 1.0;
					for (int i = (this.nSim+this.nAmp); i < this.nGenes; i++)  this.gene[i] = 0.5;
				}
			//rule selection
			//initialize with values '1' for taking into account all rules
			for (int i = 0; i < this.geneR.length; i++)  this.geneR[i] = 1;
		}
	}


	public void randomValues () {
		//automorphisms tuning
		if (this.nGenes > 0) {
			for (int i = 0; i < this.nGenes; i++)  this.gene[i] = this.gInt[i][0] + (this.gInt[i][1]-this.gInt[i][0])*Randomize.Rand();
		}

		//rule selection: total execution
		for (int i = 0; i < this.geneR.length; i++){
			if (Randomize.Rand() < 0.5)  this.geneR[i] = 0;
			else  this.geneR[i] = 1;
		}
	}


	public int size(){
		return this.geneR.length;
	}

	public int getnSelected() {
		int i, count;

		count = 0;
		for (i=0; i < this.geneR.length; i++) {
			if (this.geneR[i] > 0)  count++;
		}

		return (count);
	}


	public boolean isNew () {
		return (this.n_e == 1);
	}

	public void onNew () {
		this.n_e = 1;
	}

	public void offNew () {
		this.n_e = 0;
	}

	public void setw1 (double value) {
		this.w1 = value;
	}

	public double getAccuracy() {
		return  this.accuracy;
	}

	public double getFitness() {
		return  this.fitness;
	}



	/*************************************************************************/
	/* Translations between string representation and floating point vectors */
	/*************************************************************************/
	private int StringRep(Individual indiv, int BITS_GEN) {
		int i, j, pos, length, count;
		long n;
		char last;
		double INCREMENTO;
		char[] stringIndiv1;
		char[] stringIndiv2;
		char[] stringAux;

		length = this.nGenes * BITS_GEN;
		stringIndiv1 = new char[length];
		stringIndiv2 = new char[length];
		stringAux = new char[length];

		INCREMENTO = 1.0 / (Math.pow(2.0, (double) BITS_GEN) - 1.0);

		pos = 0;
		for (i=0; i < this.nGenes; i++) {
			n = (int) (this.gene[i] / INCREMENTO + 0.5);

			for (j = BITS_GEN - 1; j >=0 ; j--) {
				stringAux[j] = (char) ('0' + (n & 1));
				n >>= 1;
			}

			last = '0';
			for (j=0; j < BITS_GEN; j++, pos++) {
				if (stringAux[j] != last)  stringIndiv1[pos] = (char) ('0' + 1);
				else  stringIndiv1[pos] = (char) ('0' + 0);
				last = stringAux[j];
			}
		}

		pos = 0;
		for (i=0; i < this.nGenes; i++) {
			n = (int) (indiv.gene[i] / INCREMENTO + 0.5);

			for (j = BITS_GEN - 1; j >=0 ; j--) {
				stringAux[j] = (char) ('0' + (n & 1));
				n >>= 1;
			}

			last = '0';
			for (j=0; j < BITS_GEN; j++, pos++) {
				if (stringAux[j] != last)  stringIndiv2[pos] = (char) ('0' + 1);
				else  stringIndiv2[pos] = (char) ('0' + 0);
				last = stringAux[j];
			}
		}

		count = 0;
		for (i=0; i < length; i++) {
			if (stringIndiv1[i] != stringIndiv2[i])  count++;
		}

		return  count;
	}


	public int distHamming(Individual ind, int BITS_GEN) {
		int i, count;

		count = 0;
		for (i=0; i < this.geneR.length; i++) {
			if (this.geneR[i] != ind.geneR[i])  count++;
		}

		if (this.nGenes > 0)  count += StringRep(ind, BITS_GEN);

		return (count);
	}


	public void Hux(Individual indiv) {
		int i, dist, random, aux, nPos;
		int [] position;

		position = new int[this.geneR.length];
		dist = 0;

		for (i = 0; i < this.geneR.length; i++) {
			if (this.geneR[i] != indiv.geneR[i]) {
				position[dist] = i;
				dist++;
			}
		}

		nPos = dist / 2;

		for (i = 0; i < nPos; i++) {
			random = Randomize.Randint(0, dist);

			aux = this.geneR[position[random]];
			this.geneR[position[random]] = indiv.geneR[position[random]];
			indiv.geneR[position[random]] = aux;

			dist--;

			aux = position[dist];
			position[dist] = position[random];
			position[random] = aux;
		}
	}

	public void xPC_BLX(Individual indiv, double d) {
		double I, A1, C1;
		int i;

		for (i=0; i < this.nGenes; i++) {
			I = d * Math.abs(gene[i] - indiv.gene[i]);

			A1 = gene[i] - I; if (A1 < 0.0) A1 = this.gInt[i][0];
			C1 = gene[i] + I; if (C1 > 1.0) C1 = this.gInt[i][1];
			gene[i] = A1 + Randomize.Rand() * (C1 - A1);

			A1 = indiv.gene[i] - I; if (A1 < 0.0) A1 = this.gInt[i][0];
			C1 = indiv.gene[i] + I; if (C1 > 1.0) C1 = this.gInt[i][1];
			indiv.gene[i] = A1 + Randomize.Rand() * (C1 - A1);
		}
	}


	public RuleBase generateRB() {
		int i, bestRule;
		RuleBase ruleBase = this.ruleBase.clone();

		ruleBase.evaluate(this.gene, this.geneR, this.ajuste);
		ruleBase.setDefaultRule();

		for (i=geneR.length - 1; i >= 0; i--) {
			if (geneR[i] < 1)  ruleBase.remove(i);
		}  

		return ruleBase;
	}

	/**
	* <p>
	* Evaluate this individual (fitness function)
	* </p>
	* @return void
	*/
	public void evaluate() {
		this.ruleBase.evaluate(this.gene, this.geneR, this.ajuste);
		this.accuracy = this.ruleBase.getAccuracy();

		this.fitness = this.accuracy;
	}


	public int compareTo(Object a) {
		if ( ( (Individual) a).fitness < this.fitness) {
			return -1;
		}
		if ( ( (Individual) a).fitness > this.fitness) {
			return 1;
		}
		return 0;
	}

}
