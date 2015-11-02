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

import org.core.Files;

/**
 * <p>Title: DataBase</p>
 * <p>Description: Fuzzy Data Base</p>
 * <p>Copyright: Copyright KEEL (c) 2008</p>
 * <p>Company: KEEL </p>
 * @author Written by Jesus Alcalá (University of Granada) 09/02/2011
 * @author Modified by Jose Antonio Sanz (University of Navarra) 19/10/2011
 * @author Modified by Alberto Fernandez (University of Jaen) 24/10/2013
 * @version 1.2
 * @since JDK1.6
 */
public class DataBase {
	int n_variables, partitions;
	int[] nLabels;
	boolean[] varReal;
	Fuzzy[][] dataBase;
	Fuzzy[][] dataBaseIni;
	double[] aut1; //array for storing the values of the 1st automorphism for each variable
	double[] aut2; //array for storing the values of the 1st automorphism for each variable
	String names[];

    /**
     * Default constructor.
     */
    public DataBase() {
	}

	/**
	 * <p>
	 * This method builds the database, creating the initial linguistic partitions
	 * </p>
	 * @param nLabels Number of Linguistic Values
	 * @param train Training dataset
	 */
	public DataBase(int nLabels, myDataset train) {
		double mark, value, rank, labels;
		double[][] ranks = train.returnRanks();

		this.n_variables = train.getnInputs();
		this.names = (train.names()).clone();
		this.nLabels = new int[this.n_variables];
		this.varReal = new boolean[this.n_variables];
		this.dataBase = new Fuzzy[this.n_variables][];
		this.dataBaseIni = new Fuzzy[this.n_variables][];

		this.aut1 = new double[this.n_variables];
		this.aut2 = new double[this.n_variables];

		for (int i = 0; i < this.n_variables; i++) {
			rank = Math.abs(ranks[i][1] - ranks[i][0]);

			this.varReal[i] = false;

			if (train.isNominal(i))  this.nLabels[i] = ((int) rank) + 1;
			else if (train.isInteger(i) && ((rank + 1) <= nLabels))  this.nLabels[i] = ((int) rank) + 1;
			else {
				this.nLabels[i] = nLabels;
				this.varReal[i] = true;
			}

			//Both automorphisms are set to 1 for having the identity funtion in the initial FRM
			this.aut1[i] = 1.0;
			this.aut2[i] = 1.0;

			this.dataBase[i] = new Fuzzy[this.nLabels[i]];
			this.dataBaseIni[i] = new Fuzzy[this.nLabels[i]];

			mark = rank / (this.nLabels[i] - 1.0);
			for (int j = 0; j < this.nLabels[i]; j++) {
				this.dataBase[i][j] = new Fuzzy();
				this.dataBaseIni[i][j] = new Fuzzy();
				//LOWER BOUND
				value = ranks[i][0] + mark * (j - 1);
				this.dataBaseIni[i][j].x0 = this.dataBase[i][j].x0 = this.setValue(value, ranks[i][0], ranks[i][1]);
				value = ranks[i][0] + mark * j;
				this.dataBaseIni[i][j].x1 = this.dataBase[i][j].x1 = this.setValue(value, ranks[i][0], ranks[i][1]);
				value = ranks[i][0] + mark * (j + 1);
				this.dataBaseIni[i][j].x3 = this.dataBase[i][j].x3 = this.setValue(value, ranks[i][0], ranks[i][1]);
				//UPPER BOUND
				value = this.dataBaseIni[i][j].x1;
				this.dataBaseIni[i][j].b1 = this.dataBase[i][j].b1 = this.setValue(value, ranks[i][0], ranks[i][1]);
				value = this.dataBaseIni[i][j].x0 - (mark/2);
				this.dataBaseIni[i][j].b3 = this.dataBase[i][j].b3 = this.setValue(value, ranks[i][0], ranks[i][1]);
				value = this.dataBaseIni[i][j].x3 + (mark/2);
				this.dataBaseIni[i][j].b4 = this.dataBase[i][j].b4 = this.setValue(value, ranks[i][0], ranks[i][1]);
				this.dataBaseIni[i][j].y = this.dataBase[i][j].y = 1.0;
				this.dataBase[i][j].name = new String("L_" + j + "(" + this.nLabels[i] + ")");
				this.dataBaseIni[i][j].name = new String("L_" + j + "(" + this.nLabels[i] + ")");
			}
		}
	}

	private double setValue(double val, double min, double tope) {
		if (val > min - 1E-4 && val < min + 1E-4)  return (min);
		if (val > tope - 1E-4 && val < tope + 1E-4)  return (tope);
		return (val);
	}

	public void decode(double[] gene, int tipoAjuste) {
		int i, j, pos;
		double displacement,aux1,aux2,aux;

		pos = 0;
		if ((tipoAjuste == 1) || (tipoAjuste == 3) || (tipoAjuste == 4) || (tipoAjuste == 5)){
			//Similarity Tuning
			for (i=0; i < n_variables; i++) {
				aux1 = gene[i];
				//Offset to the correct interval
				if (aux1>1.0) aux1 = (-1.0)*(1.0/(aux1-2.0));
				this.aut1[i] = aux1;
				aux2 = gene[n_variables+i];
				//Offset to the correct interval
				if (aux2>1.0) aux2 = (-1.0)*(1.0/(aux2-2.0));
				this.aut2[i] = aux2;
			}
		}
		if ((tipoAjuste == 2) || (tipoAjuste == 3) || (tipoAjuste == 4) || (tipoAjuste == 5)){
			//shift the position from where genes start for this kind of tuning; 
			//this is because the similarity tuning is performed in 0, 2 is not carried out and then pos = 0 
			if ((tipoAjuste == 3) || (tipoAjuste == 4) || (tipoAjuste == 5)){ pos = 2*n_variables;} 

			if (tipoAjuste!=5){//Tuning "5" does not carry out the similarity tuning
				//amplitude tuning
				for (i=0; i < n_variables; i++) {
					if (varReal[i]) {
						for (j=0; j < this.nLabels[i]; j++, pos++) { //amplitude tuning
							displacement = (2*gene[pos]) * (this.dataBaseIni[i][2].x0 - this.dataBaseIni[i][2].b3);
							//this is carried out with dataBase (and not dataBaseIni) since the lateral tuning could generate invalid IVFS
							//in the case of amplitude tuning or similarity (in isolation), it does not affect the behaviour: lower bound remains the same
							this.dataBase[i][j].b3 = this.dataBase[i][j].x0 - displacement;
							this.dataBase[i][j].b4 = this.dataBase[i][j].x3 + displacement;
						}
					}
				}
			}
			if ((tipoAjuste == 4) || (tipoAjuste == 5)){
				//lateral tuning
				for (i=0; i < n_variables; i++) {
					if (varReal[i]) {
						for (j=0; j < this.nLabels[i]; j++, pos++) {
							//lateral tuning
							if (j == 0)  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j+1].x1 - this.dataBaseIni[i][j].x1);
							else if (j == (this.nLabels[i]-1))  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j].x1 - this.dataBaseIni[i][j-1].x1);
							else {
								if ((gene[pos] - 0.5) < 0.0)  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j].x1 - this.dataBaseIni[i][j-1].x1);
								else  displacement = (gene[pos] - 0.5) * (this.dataBaseIni[i][j+1].x1 - this.dataBaseIni[i][j].x1);
							}
							//the shift performed by the amplitude tuning is stored  
							aux =   this.dataBase[i][j].x0 - this.dataBase[i][j].b3;
							//the lateral tuning is carried out over the initial IVFS
							this.dataBase[i][j].x0 = this.dataBaseIni[i][j].x0 + displacement;
							this.dataBase[i][j].x1 = this.dataBaseIni[i][j].x1 + displacement;
							this.dataBase[i][j].x3 = this.dataBaseIni[i][j].x3 + displacement;
							this.dataBase[i][j].b1 = this.dataBaseIni[i][j].b1 + displacement;
							this.dataBase[i][j].b3 = this.dataBaseIni[i][j].b3 + displacement;
							this.dataBase[i][j].b4 = this.dataBaseIni[i][j].b4 + displacement;
							//the amplitude tuning, which was carried out previoulsy, is now applied
							this.dataBase[i][j].b3 = this.dataBase[i][j].x0 - aux;
							this.dataBase[i][j].b4 = this.dataBase[i][j].x3 + aux;

						}
					}
				}
			}
		}      
	}

	public int numVariables() {
		return (this.n_variables);
	}

	public int getnLabelsReal() {
		int i, count;

		count = 0;

		for (i=0; i < n_variables; i++) {
			if (varReal[i])  count += this.nLabels[i];
		}

		return (count);
	}

	public int numLabels(int variable) {
		return (this.nLabels[variable]);
	}

	public int[] getnLabels() {
		return (this.nLabels);
	}

	public double[] matching(int variable, int label, double value) {
		double match[] = new double[2];
		if ((variable < 0) || (label < 0)){
			match[0]=match[1]=1.0;
		}  // Don't care
		else{
			match = this.dataBase[variable][label].Fuzzifica(value);
		}
		return(match);
	}

	public String print_triangle(int var, int label) {
		String cadena = new String("");

		Fuzzy d = this.dataBase[var][label];

		cadena = d.name + ": \t" + d.x0 + "\t" + d.x1 + "\t" + d.x3 + "\t" + d.b3 + "\t" + d.b1 + "\t" + d.b4 + "\n";
		return cadena;
	}

	public String print(int var, int label) {
		return (this.dataBase[var][label].getName());
	}

	public String printString() {
		String string = new String("@Using Triangular Membership Functions as antecedent fuzzy sets");
		for (int i = 0; i < this.n_variables; i++) {
			string += "\n\n@Number of Labels in Variable " + (i+1) + ": " + this.nLabels[i];
			string += "\n" + this.names[i] + ":\n";
			for (int j = 0; j < this.nLabels[i]; j++) {
				string += this.dataBase[i][j].name + ": (" + this.dataBase[i][j].x0 + "," + this.dataBase[i][j].x1 + "," + this.dataBase[i][j].x3 + "," + this.dataBase[i][j].b3 +  "," + this.dataBase[i][j].b1 + "," + this.dataBase[i][j].b4 + ")\n";
			}
		}

		string += "\n\n@Values of the automorphisms:\n";

		for (int i = 0; i < this.n_variables; i++) {
			string += "\nVariable " + (i+1) + " (" + this.names[i] + "):\n";
			string += "Automosphism 1: " + this.aut1[i] + "\nAutomorphism 2:" + this.aut2[i] + "\n";
		}

		return string;
	}

	public void saveFile(String filename) {
		String stringOut = new String("");
		stringOut = printString();
		Files.writeFile(filename, stringOut);
	}

}
