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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

/**
 * <p>
 * @author Written by Alvaro Lopez
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.io.*;
import java.util.*;

import org.core.*;

public class FingramsProcess {
	/**
	 * <p>
	 * It provides the implementation of the algorithm to be run in a process
	 * </p>
	 */

	private myDataset dataset;
	private DataBase database;
	private ArrayList<Itemset> outputVariables;

	/**
	 * <p>
	 * It creates a new process for the algorithm by setting up its parameters
	 * </p>
	 * 
	 * @param dataset
	 *            The instance of the dataset for dealing with its records
	 * @param nEvaluations
	 *            The maximum number of evaluations to reach before stopping the
	 *            genetic learning
	 * @param popSize
	 *            The maximum size of population to handle after each generation
	 * @param nBitsGene
	 *            The number of bit digits for encoding a displacement within a
	 *            gene
	 * @param phi
	 *            It represents the value used for decreasing the "L" threshold
	 *            (CURRENTLY NOT USED)
	 * @param d
	 *            It indicates the value for controlling the Parent Centric BLX
	 *            crossover
	 * @param nFuzzyRegionsForNumericAttributes
	 *            The number of fuzzy regions with which numeric attributes are
	 *            evaluated
	 * @param useMaxForOneFrequentItemsets
	 *            It indicates whether the max operator must be used while
	 *            discovering 1-Frequent Itemsets
	 * @param minSupport
	 *            The user-specified minimum support for the mined association
	 *            rules
	 * @param minConfidence
	 *            The user-specified minimum confidence for the mined
	 *            association rules
	 */
	public FingramsProcess(myDataset dataset, DataBase database) {
		this.dataset = dataset;
		this.database = database;
		this.outputVariables = new ArrayList<Itemset>();
	}

	/**
	 * <p>
	 * It runs the algorithm for mining association rules
	 * </p>
	 */
	public int generateFile(String ruleBaseFile, double blankThreshold,
			String fingramsFile, double minimumLift) {
		int variable, value, nRules;
		boolean first;
		Itemset antecedent, consequent, rule;
		Item aux;

		String stringRules = new String("");
		String dataAnt, dataCons, dataRule, text, line;
		StringTokenizer data;
		File file = null;
		FileReader fr = null;
		BufferedReader br = null;

		this.outputVariables.clear();

		PrintWriter fingrams_writer;

		try {
			fingrams_writer = new PrintWriter(fingramsFile);

		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return -1;
		}

		for (int i = 0; i < dataset.getnVars(); i++)
			this.outputVariables.add(new Itemset());

		nRules = 0;
		int numberMinimumLiftRules=0;
		try {
			// Apertura del fichero y creacion de BufferedReader para poder
			// hacer una lectura comoda (disponer del metodo readLine()).
			file = new File(ruleBaseFile);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			// Lectura del fichero
			br.readLine();
			br.readLine();

			// ///////////////////

			while ((line = br.readLine()) != null) {
				data = new StringTokenizer(line, " = \" ");
				if (data.nextToken().equalsIgnoreCase("<rule")) {
					nRules++;

					antecedent = new Itemset();
					br.readLine();
					data = new StringTokenizer(br.readLine(), " = \" ");
					first = true;
					while (data.nextToken().equalsIgnoreCase("<attribute")) {
						data.nextToken();
						text = data.nextToken();

						variable = this.dataset.posVariable(text);
						data.nextToken();
						text = data.nextToken();

						value = this.database.posValue(variable, text);
						antecedent.add(new Item(variable, value));
						data = new StringTokenizer(br.readLine(), " = \" ");
					}

					br.readLine();
					data = new StringTokenizer(br.readLine(), " = \" ");

					consequent = new Itemset();
					variable=0;
					
					while (data.nextToken().equalsIgnoreCase("<attribute")) {
						data.nextToken();
						text = data.nextToken();

						variable = this.dataset.posVariable(text);
						data.nextToken();
						text = data.nextToken();

						value = this.database.posValue(variable, text);
						consequent.add(new Item(variable, value));
						//this.outputVariables.get(variable).addNew(new Item(variable, value));
						data = new StringTokenizer(br.readLine(), " = \" ");
					}

					
					
					rule = new Itemset();
					rule.addItemset(antecedent);
					rule.addItemset(consequent);

					dataRule = rule.calculateSupport(dataset, database,
							blankThreshold);
					dataAnt = antecedent.calculateSupport(dataset, database,
							blankThreshold);
					dataCons = consequent.calculateSupport(dataset, database,
							blankThreshold);

					double actualLift = (rule.getSupport() / antecedent
							.getSupport()) / consequent.getSupport();
					
					if (actualLift >= minimumLift) {
						numberMinimumLiftRules++;
						this.outputVariables.get(variable).addNew(consequent.get(0));
					}
					br.readLine();
				}
			}

			System.err.println("Number of initial rules: " + (nRules));

			String outputFile = new String("Association\n" + this.outputLine()
					+ "\nBlank threshold: " + blankThreshold + "\n\nRules: "
					+ numberMinimumLiftRules + "\nExamples: "+dataset.getnTrans() + "\n\n");

			fingrams_writer.print("" + outputFile);
			fingrams_writer.flush();

			// //////////////////////*/

			file = new File(ruleBaseFile);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			// Lectura del fichero
			br.readLine();
			br.readLine();

			nRules = 0;

			while ((line = br.readLine()) != null) {
				stringRules = "";
				data = new StringTokenizer(line, " = \" ");
				if (data.nextToken().equalsIgnoreCase("<rule")) {
					System.err.print("Rule: " + (nRules));
					stringRules = stringRules + "Rule" + nRules + ": IF";
					nRules++;

					antecedent = new Itemset();
					br.readLine();
					data = new StringTokenizer(br.readLine(), " = \" ");
					first = true;
					while (data.nextToken().equalsIgnoreCase("<attribute")) {
						data.nextToken();
						text = data.nextToken();
						if (first) {
							stringRules = stringRules + " " + text + " is ";
							first = false;
						} else
							stringRules = stringRules + " AND " + text + " is ";
						variable = this.dataset.posVariable(text);
						data.nextToken();
						text = data.nextToken();
						stringRules = stringRules + text;
						value = this.database.posValue(variable, text);
						antecedent.add(new Item(variable, value));
						data = new StringTokenizer(br.readLine(), " = \" ");
					}
					stringRules = stringRules + " THEN";
					consequent = new Itemset();
					br.readLine();
					data = new StringTokenizer(br.readLine(), " = \" ");
					first = true;
					while (data.nextToken().equalsIgnoreCase("<attribute")) {
						data.nextToken();
						text = data.nextToken();
						if (first) {
							stringRules = stringRules + " " + text + " is ";
							first = false;
						} else
							stringRules = stringRules + " AND " + text + " is ";
						variable = this.dataset.posVariable(text);
						data.nextToken();
						text = data.nextToken();
						stringRules = stringRules + text;
						value = this.database.posValue(variable, text);
						consequent.add(new Item(variable, value));
						this.outputVariables.get(variable).addNew(
								new Item(variable, value));
						data = new StringTokenizer(br.readLine(), " = \" ");
					}
					stringRules = stringRules + "\n";
					rule = new Itemset();
					rule.addItemset(antecedent);
					rule.addItemset(consequent);

					dataRule = rule.calculateSupport(dataset, database,
							blankThreshold);
					dataAnt = antecedent.calculateSupport(dataset, database,
							blankThreshold);
					dataCons = consequent.calculateSupport(dataset, database,
							blankThreshold);

					stringRules += "Rule Support  => ("
							+ (rule.getSupport() * dataset.getnTrans()) + ") ("
							+ rule.getSupport() + ")  => ";
					if (!dataRule.equalsIgnoreCase(""))
						stringRules += dataRule + "\n";
					else
						stringRules += "There are no items \n";

					stringRules += "Antecedent Support  => ("
							+ (antecedent.getSupport() * dataset.getnTrans())
							+ ") (" + antecedent.getSupport() + ")  => ";
					if (!dataAnt.equalsIgnoreCase(""))
						stringRules += dataAnt + "\n";
					else
						stringRules += "There are no items \n";

					stringRules += "Consequent Support  => ("
							+ (consequent.getSupport() * dataset.getnTrans())
							+ ") (" + consequent.getSupport() + ")  => ";
					if (!dataCons.equalsIgnoreCase(""))
						stringRules += dataCons + "\n";
					else
						stringRules += "There are no items \n";

					double actualLift = (rule.getSupport() / antecedent
							.getSupport()) / consequent.getSupport();

					System.err.println("; Lift: "+actualLift);
					
					if (actualLift >= minimumLift) {
						// fingrams_writer = new PrintWriter(fingramsFile);
						fingrams_writer.print(stringRules + "\n");
						fingrams_writer.flush();
					}

					br.readLine();
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			fingrams_writer.flush();
			fingrams_writer.close();
			System.err.println("Number of final rules with lift higher than "+minimumLift+": "+numberMinimumLiftRules);
			System.err.println("File succesfully writed");
			System.err.flush();
			// En el finally cerramos el fichero, para asegurarnos
			// que se cierra tanto si todo va bien como si salta
			// una excepcion.
			try {
				if (null != fr) {
					fr.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
				return -1;
			}
		}

		
		return 0;
	}

	public String outputLine() {
		Itemset itemset;
		Item item;
		String[] nameVariables = this.dataset.names();
		String line = "";

		for (int i = 0; i < dataset.getnVars(); i++) {
			itemset = this.outputVariables.get(i);
			if (itemset.size() > 0) {
				line = line + nameVariables[i] + "(";
				for (int j = 0; j < itemset.size(); j++) {
					item = itemset.get(j);
					line = line
							+ this.database.print(item.getVariable(),
									item.getValue());
					if (j < (itemset.size() - 1))
						line = line + ",";
				}
				line = line + ");";
			}
		}
		
		if (line==""){
			line="---";
		}
		
		return (line+"\n");
	}
}