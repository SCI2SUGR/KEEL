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

/**
 * <p>
 * @author Written by Julián Luengo Martín 14/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import keel.Dataset.*;

import org.core.Files;
import org.core.Randomize;

/**
 * <p>
 * This class contains the main body of the CORE algorithm, presented by:
 * </p>
 * <p>
 * Tan, K.C., Yu, Q., Ang, J.H. A coevolutionary algorithm for rules discovery in data mining. 	International Journal of Systems Science 37 (12), pp. 835-864 
 * </p>
 */
public class Core {
	Population main;
	Population previous;
	Copopulation ruleSet[];
	Cochromosome bestRuleSet;
	ArrayList<Chromosome> bestRulePool;
	ArrayList<Chromosome> randomRules;
	
	int popSize;
	int numClasses;
	long seed;
	int generationLimit;
	int nCopopulations;
	int copopSize;
	
	double prob_mutacion;
	double crossoverRate;
	double regenerationProb;
	
	String input_train_name = new String();
	String input_validation_name;
	String input_test_name = new String();
	String output_train_name = new String();
	String output_test_name = new String();
	String method_output;
	
	InstanceSet IS;
	InstanceSet ISval;
	InstanceSet IStest;
	static int majorityClass;
	int populationMu;
	int copopulationMu;
	
	/**
	 * <p>
	 * Default constructor, sets all structures to null.
	 * </p>
	 */
	public Core(){
		main = null;
		ruleSet = null;
	}
	
	/** Creates a new instance of Core
     * @param paramfile The path to the configuration file with all the parameters in KEEL format
     */
	public Core(String paramfile){
		int occurrences[];
		
		config_read(paramfile);
		Randomize.setSeed(seed);
		IS = new InstanceSet();
		ISval = new InstanceSet();
		IStest = new InstanceSet();
		
		try {
			IS.readSet(input_train_name, true);
			ISval.readSet(input_validation_name, false);
			IStest.readSet(input_test_name, false);
		} catch (Exception e) {
			System.out.println("Dataset exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
		Attribute a =Attributes.getOutputAttribute(0);
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		occurrences = new int[numClasses];
		for(int i=0;i<IS.getNumInstances();i++)
			occurrences[(int)IS.getInstance(i).getAllOutputValues()[0]]++;
		majorityClass = 0;
		for(int i=1;i<numClasses;i++)
			if(occurrences[i] > occurrences[majorityClass])
				majorityClass = i;
		
	}
	
	/**
	 * Read the pattern file, and parse data into strings
	 * @param fileParam the file with the parameters
	 */
	private void config_read(String fileParam) {
		File inputFile = new File(fileParam);

		if (inputFile == null || !inputFile.exists()) {
			System.out.println("parameter " + fileParam
					+ " file doesn't exists!");
			System.exit(-1);
		}
		// begin the configuration read from file
		try {
			FileReader file_reader = new FileReader(inputFile);
			BufferedReader buf_reader = new BufferedReader(file_reader);
			// FileWriter file_write = new FileWriter(outputFile);

			String line;

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0); // avoid empty lines for processing
											// ->
			// produce exec failure
			String out[] = line.split("algorithm = ");
			// alg_name = new String(out[1]); //catch the algorithm name
			// input & output filenames
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("inputData = ");
			out = out[1].split("\\s\"");
			input_train_name = new String(out[0].substring(1,out[0].length() - 1));
			input_validation_name = new String(out[1].substring(0,out[2].length() - 1));
			input_test_name = new String(out[2].substring(0,out[2].length() - 1));
			if (input_validation_name.charAt(input_validation_name.length() - 1) == '"')
				input_validation_name = input_validation_name.substring(0, input_validation_name
						.length() - 1);
			if (input_test_name.charAt(input_test_name.length() - 1) == '"')
				input_test_name = input_test_name.substring(0, input_test_name
						.length() - 1);

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("outputData = ");
			out = out[1].split("\\s\"");
			output_train_name = new String(out[0].substring(1,
					out[0].length() - 1));
			output_test_name = new String(out[1].substring(0,
					out[1].length() - 1));
			method_output = new String(out[2].substring(0,out[2].length() - 1));
			method_output = method_output.trim();
			if (method_output.charAt(method_output.length() - 1) == '"')
				method_output = method_output.substring(0,
						method_output.length() - 1);
			if (output_test_name.charAt(output_test_name.length() - 1) == '"')
				output_test_name = output_test_name.substring(0,
						output_test_name.length() - 1);

			// parameters
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("seed = ");
			seed = (new Long(out[1])).longValue(); 
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("popSize = ");
			popSize = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("CopopulationSize = ");
			copopSize = (new Integer(out[1])).intValue();
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("generationLimit = ");
			generationLimit = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("numberOfCopopulations = ");
			nCopopulations = (new Integer(out[1])).intValue();
			
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("CrossoverRate = ");
			crossoverRate = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("ProbMutation = ");
			prob_mutacion = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("RegenerationProbability = ");
			regenerationProb = (new Double(out[1])).doubleValue(); // parse the string into
			
			file_reader.close();

		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * Writes the output in KEEL format
	 * @param fileName output file
	 * @param instancesIN output from instances of the input data set
	 * @param instancesOUT class of classified instances
	 * @param inputs the input attributes
	 * @param output the output attribute
	 * @param nInputs number of input attributes
	 * @param relation data set name
	 */
	public static void writeOutput(String fileName,
			String instancesIN[], String instancesOUT[],
			Attribute inputs[], Attribute output, int nInputs,
			String relation) {

		String cadena = "";
		int i, j, k;
		int aux;

		/* Printing input attributes */
		cadena += "@relation " + relation + "\n";
		for (i = 0; i < nInputs; i++) {
			cadena += "@attribute " + inputs[i].getName() + " ";
			if (inputs[i].getType() == Attribute.NOMINAL) {
				cadena += "{";
				for (j = 0; j < inputs[i].getNominalValuesList().size(); j++) {
					cadena += (String) inputs[i].getNominalValuesList()
							.elementAt(j);
					if (j < inputs[i].getNominalValuesList().size() - 1) {
						cadena += ", ";
					}
				}
				cadena += "}\n";
			} else {
				if (inputs[i].getType() == Attribute.INTEGER) {
					cadena += "integer";
					cadena += " ["
							+ String.valueOf((int) inputs[i]
									.getMinAttribute())
							+ ", "
							+ String.valueOf((int) inputs[i]
									.getMaxAttribute()) + "]\n";
				} else {
					cadena += "real";
					cadena += " ["
							+ String.valueOf(inputs[i].getMinAttribute())
							+ ", "
							+ String.valueOf(inputs[i].getMaxAttribute())
							+ "]\n";
				}
			}
		}

		/* Printing output attribute */
		cadena += "@attribute " + output.getName() + " ";
		if (output.getType() == Attribute.NOMINAL) {
			cadena += "{";
			for (j = 0; j < output.getNominalValuesList().size(); j++) {
				cadena += (String) output.getNominalValuesList().elementAt(j);
				if (j < output.getNominalValuesList().size() - 1) {
					cadena += ", ";
				}
			}
			cadena += "}\n";
		} else {
			cadena += "integer ["
					+ String.valueOf((int) output.getMinAttribute()) + ", "
					+ String.valueOf((int) output.getMaxAttribute()) + "]\n";
		}

		/* Printing the data */
		cadena += "@data\n";

		Files.writeFile(fileName, cadena);
		cadena = "";
		for (i = 0; i < instancesIN.length; i++) {
			cadena += instancesIN[i] + " " + instancesOUT[i];

			cadena += "\n";

		}
		Files.addToFile(fileName, cadena);
	}
	
	/**
	 * Applies a tournament selection, with tournament size of 2
	 */
	public void tournament_selection(){
		int i, j, k, p, pMej, mejor_torneo, acum;

		int tam_torneo = 2;
		int Torneo[] = new int[tam_torneo];
		boolean repetido;
		Population sample = new Population();
		for (i=0;i<popSize;i++){

			Torneo[0] = Randomize.Randint(0,popSize);
			mejor_torneo=Torneo[0];
			
			pMej = 0;
			acum = main.getNumRules(pMej);
			while(acum<=mejor_torneo){
				mejor_torneo -= main.getNumRules(pMej);
				pMej++;
				acum = main.getNumRules(pMej);
			}

			for (j=1;j<tam_torneo;j++)
			{
				do
				{
					Torneo[j] = Randomize.Randint(0,popSize);
					repetido=false; k=0;
					while ((k<j) && (!repetido)){
						if (Torneo[j]==Torneo[k])
							repetido=true;
						else
							k++;
					}
				}
				while (repetido);

				p = 0;
				acum = main.getNumRules(p);
				while(acum<=Torneo[j]){
					Torneo[j] -= main.getNumRules(p);
					p++;
					acum = main.getNumRules(p);
				}

				
				
				if (main.getRule(p,Torneo[j]).fitness > main.getRule(pMej,mejor_torneo).fitness){
					mejor_torneo=Torneo[j];
					pMej = p;
				}
			}

			sample.addRule(pMej,new Chromosome (main.getRule(pMej,mejor_torneo)));
		}
		previous = main;
		main = sample;
	}
	
	/**
	 * Applies mutation in the new poblation
	 */
	public void mutate(){
		int posiciones, i, j,p,acum;
		int n_genes;
		int Mu_next;
		double m;
//		for(p=0;p<numClasses;p++){
			n_genes = Attributes.getInputNumAttributes();
			posiciones=n_genes*popSize;
			Mu_next = populationMu;

			if (prob_mutacion>0)
				while (Mu_next<posiciones){
					/* The chromosome and the gen which correspond to the position to mutate is determined*/
					i=Mu_next/n_genes;
					j=Mu_next%n_genes;
					p=0;
					acum = main.getNumRules(p);
					while(acum<=i){
						i -= main.getNumRules(p);
						p++;
						acum = main.getNumRules(p);
					}
					

					/* The mutation on the gene is performed */
					main.mutate(p,i,j);

					/* The mutated chromosome is marked for latter evaluation */
					main.setEvaluated(p,i,false);

					/* The next mutation position is computed */
					if (prob_mutacion<1)
					{
						m = Randomize.Rand();
						Mu_next += Math.ceil (Math.log(m) / Math.log(1.0 - prob_mutacion));
					}
					else
						Mu_next += 1;
				}

			Mu_next -= posiciones;
//			main.setMu(p, Mu_next);
			populationMu = Mu_next;
//		}
	}

	/**
	 * <p>
	 * Applies the crossover between 2 chromosomes
	 * </p>
	 */
	public void crossOver(){
		Chromosome c1,c2;
		int cutpoint = -1;
		
		for(int p=0;p<numClasses;p++){
			for (int i=0;i<main.getNumRules(p);i+=2){
				if(Randomize.Rand() < crossoverRate && i+1 < main.getNumRules(p)){
					c1 = main.getRule(p, i);
					c2 = main.getRule(p, i+1);
					cutpoint = Randomize.Randint(0, Attributes.getInputNumAttributes());
					
					c1.swap(c2, cutpoint);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Initialize all populations (both Michigan and Pitts approaches) randomly
	 * </p>
	 */
	public void initialize(){
		main = new Population();
		bestRulePool = new ArrayList<Chromosome>();
		Chromosome c;
		int relation;
		double bounds[] = new double[2];
		double value;
		ArrayList<String> values;
		Attribute a;
		for(int cl=0;cl<numClasses;cl++){
			for(int p=0;p<popSize/numClasses;p++){
				c = new Chromosome();
				for(int j=0;j<Attributes.getInputNumAttributes();j++){
					a = Attributes.getInputAttribute(j);
					if(Randomize.Rand()<0.25){
						a = Attributes.getInputAttribute(j);
						if(Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL){
							values = new ArrayList<String>();
							for(int i=0;i<a.getNumNominalValues();i++){
								if(Randomize.Rand()<0.5)
									values.add((String)(a.getNominalValuesList().toArray()[i]));
							}
							c.addNominalGene(j, a, Randomize.Randint(0, 2), values);
						}
						else{
							relation = Randomize.Randint(2, 8);
							if(relation==Gene.outOfBound || relation==Gene.inBound){
								bounds[0] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
								bounds[1] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
								if(bounds[0]>bounds[1]){
									value = bounds[0];
									bounds[0] = bounds[1];
									bounds[1] = value;
								}
								c.addRealBoundedGene(j, a, relation, bounds);
							}
							else{
								value = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
								c.addRealGene(j, a, relation, value);
							}
						}
					}
				}
				c.setClass(cl);
				main.addRule(cl, c);
			}
		}
		for(int p=0;p<popSize%numClasses;p++){
			c = new Chromosome();
			for(int j=0;j<Attributes.getInputNumAttributes();j++){
				a = Attributes.getInputAttribute(j);
				if(Randomize.Rand()<0.5){
					a = Attributes.getInputAttribute(j);
					if(Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL){
						values = new ArrayList<String>();
						for(int i=0;i<a.getNumNominalValues();i++){
							if(Randomize.Rand()<0.5)
								values.add((String)(a.getNominalValuesList().toArray()[i]));
						}
						c.addNominalGene(j, a, Randomize.Randint(0, 2), values);
					}
					else{
						relation = Randomize.Randint(2, 8);
						if(relation==Gene.outOfBound || relation==Gene.inBound){
							bounds[0] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
							bounds[1] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
							if(bounds[0]>bounds[1]){
								value = bounds[0];
								bounds[0] = bounds[1];
								bounds[1] = value;
							}
							c.addRealBoundedGene(j, a, relation, bounds);
						}
						else{
							value = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
							c.addRealGene(j, a, relation, value);
						}
					}
				}
			}
			c.setClass(Randomize.Randint(0, numClasses));
			main.addRule(c.getClas(), c);
		}
		
		ruleSet = new Copopulation[nCopopulations];
	}
	
	/**
	 * <p>
	 * Evaluates the current population (set of rules) with the training data set
	 * </p>
	 */
	public void evaluate(){
		main.evaluate(IS);
	}
	
	/**
	 * <p>
	 * Ensures the elitism, keeping the best rule. Besides, it sorts the rule set by their fitness.
	 * </p>
	 */
	public void elitism(){
		Chromosome rule;
		int _class;
		for(int p=0;p<numClasses;p++){
			Collections.sort(main.rules[p].rules,Collections.reverseOrder());
		}
		for(int p=0;p<bestRulePool.size();p++){
			rule = bestRulePool.get(p);
			_class = rule.getClas();
			Collections.sort(main.rules[_class].rules,Collections.reverseOrder());
			if(main.rules[_class].rules.size() > 0)
				main.rules[_class].rules.set(main.rules[_class].rules.size()-1, rule);
			else
				main.rules[_class].rules.add(rule);
		}
	}
	
	/**
	 * <p>
	 * Performs the token competition. The rules "fights" for covering the instances,
	 * following the fitness order.
	 * </p>
	 */
	public void token_competition(){
		Instance inst;
		int output,predicted;
		boolean exist;
		double input[];
		double adjusted_fitness;
		Chromosome rule;
		Chromosome tokenCaptured[] = new Chromosome[IS.getNumInstances()];
		
		int numberOfClassOccurences[] = new int[numClasses];
		
		for(int p=0;p<numClasses;p++){
			for (int i=0;i<main.getNumRules(p);i++){
				main.getRule(p, i).resetTokens();
			}
		}
		//token competition
		for(int k=0;k<IS.getNumInstances();k++){
			inst = IS.getInstance(k);
			output = (int) inst.getAllOutputValues()[0];
			input = inst.getAllInputValues();
			numberOfClassOccurences[output]++;
			for (int i=0;i<main.getNumRules(output);i++){
				//only consider the subpopulation for the class of the analyzed instance
				predicted = main.getRule(output, i).evaluate(input);
				if(predicted==output){ //is true-positive?
					//the token will be captured if none got it or the rules has more fitness
					if(tokenCaptured[k]== null){ 
						tokenCaptured[k] = main.getRule(output, i);
						main.getRule(output, i).tokenCaptured();						
					}else if(main.getRule(output, i).fitness > tokenCaptured[k].fitness){
						tokenCaptured[k].tokenLost();
						tokenCaptured[k] = main.getRule(output, i);
						main.getRule(output, i).tokenCaptured();
					}
				}
			}
		}
		//adjust the fitness
//		bestRulePool = new ArrayList<Chromosome>();
		for(int p=0;p<numClasses;p++){
			for (int i=0;i<main.getNumRules(p);i++){
				adjusted_fitness = main.getRule(p,i).fitness;
				adjusted_fitness = adjusted_fitness *(double)main.getRule(p,i).capturedTokens/numberOfClassOccurences[p];
//				if(main.getRule(p, i).fitness>0 && !bestRulePool.contains(main.getRule(p, i)))
//					bestRulePool.add(main.getRule(p, i));
				main.getRule(p, i).setFitness(adjusted_fitness);
				if(main.getRule(p, i).fitness>0 && !bestRulePool.contains(main.getRule(p, i)))
					bestRulePool.add(main.getRule(p, i));
			}
		}
		for (int i=0;bestRuleSet!=null && i<bestRuleSet.getNumRules();i++){
			rule = bestRuleSet.rules.get(i);
			exist = false;
			for(int j=0;j<bestRulePool.size() && !exist;j++){
				if(bestRulePool.get(j).same(rule))
					exist = true;
			}
			if(rule.fitness > 0  && !exist)
				bestRulePool.add(new Chromosome (rule));
		}
		Collections.sort(bestRulePool,Collections.reverseOrder());
	}
	
	/**
	 * <p>
	 * Randomize the rules with fitness below the average, to obtain diversity.
	 * </p>
	 */
	public void regeneration(){
		Chromosome rule;
		double meanFitness = 0,value;
		Attribute a;
		ArrayList<String> values;
		double bounds[] = new double[2];
		int relation;
		//compute the average fitness, and randomize all those rules under the 
		//obtained mean value
		for(int p=0;p<numClasses;p++){
			meanFitness = 0;
			for (int i=0;i<main.getNumRules(p);i++){
				rule = main.getRule(p, i);
				meanFitness += rule.fitness;				
			}
			meanFitness /= main.getNumRules(p);
			for (int r=0;r<main.getNumRules(p);r++){
				rule = main.getRule(p, r);
				if(rule.fitness < meanFitness && Randomize.Rand() < regenerationProb){ //regenerate rule?
					rule = new Chromosome();
					for(int j=0;j<Attributes.getInputNumAttributes();j++){
						a = Attributes.getInputAttribute(j);
						if(Randomize.Rand()<0.5){
							a = Attributes.getInputAttribute(j);
							if(Attributes.getInputAttribute(j).getType()==Attribute.NOMINAL){
								values = new ArrayList<String>();
								for(int i=0;i<a.getNumNominalValues();i++){
									if(Randomize.Rand()<0.5)
										values.add((String)(a.getNominalValuesList().toArray()[i]));
								}
								rule.addNominalGene(j, a, Randomize.Randint(0, 2), values);
							}
							else{
								relation = Randomize.Randint(2, 8);
								if(relation==Gene.outOfBound || relation==Gene.inBound){
									bounds[0] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
									bounds[1] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
									if(bounds[0]>bounds[1]){
										value = bounds[0];
										bounds[0] = bounds[1];
										bounds[1] = value;
									}
									rule.addRealBoundedGene(j, a, relation, bounds);
								}
								else{
									value = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
									rule.addRealGene(j, a, relation, value);
								}
							}
						}
					}
					rule.setClass(Randomize.Randint(0, numClasses));
					//update the subpopulations
					main.removeRule(p, main.getRule(p, r));
					main.addRule(rule.getClas(), rule);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Creates a new copopulation, for restarting purposes.
	 * </p>
	 */
	public void create_copopulation(){
		Chromosome rule;
		Cochromosome rs;
		int selectedRule;
		int i;
		
		for( i=0;i<nCopopulations;i++){
			ruleSet[i] = new Copopulation();
		}
		
//		i = Randomize.Randint(0, nCopopulations);
		for( i=0;i<nCopopulations && i< bestRulePool.size()+randomRules.size();i++){
			for(int j=1;j<=copopSize;j++){
				rs = new Cochromosome();
				for(int k=0;k<i+1;k++){
					selectedRule = Randomize.Randint(0, bestRulePool.size()+randomRules.size());
					if(selectedRule < bestRulePool.size()){
						rule = new Chromosome(bestRulePool.get(selectedRule));
					}
					else{
						selectedRule-=bestRulePool.size();
						rule = new Chromosome( randomRules.get(selectedRule));

					}
//					while(rs.contains(rule)){
//						selectedRule = (selectedRule+1)%(bestRulePool.size()+randomRules.size());
//						if(selectedRule < bestRulePool.size()){
//							rule = bestRulePool.get(selectedRule);
//						}
//						else{
////							selectedRule-=bestRulePool.size();
//							rule = randomRules.get(selectedRule-bestRulePool.size());
//
//						}
//					}
					rs.addChromosome(rule);
					Collections.sort(rs.rules,Collections.reverseOrder());
				}
				ruleSet[i].addRule(rs);
			}
		}
		int acum = 0;
		for(int b=0;b<ruleSet.length;b++)
			acum += ruleSet[b].chrom.size();
//		System.out.println(acum+" "+(bestRulePool.size()+randomRules.size()));
	}
	
	/**
	 * <p>
	 * Evaluates the current copopulation
	 * </p>
	 */
	public void evaluate_copopulation(){
		for(int i=0;i<ruleSet.length;i++){
			for(int j=0;j<ruleSet[i].chrom.size();j++){
				Collections.sort(ruleSet[i].chrom.get(j).rules,Collections.reverseOrder());
				ruleSet[i].chrom.get(j).evaluate(IS);
				if(bestRuleSet == null || ruleSet[i].chrom.get(j).fitness > bestRuleSet.fitness)
					bestRuleSet = new Cochromosome(ruleSet[i].chrom.get(j));
				else if(ruleSet[i].chrom.get(j).fitness == bestRuleSet.fitness && ruleSet[i].chrom.get(j).getNumRules() < bestRuleSet.getNumRules())
					bestRuleSet = new Cochromosome(ruleSet[i].chrom.get(j));
			}
		}
	}
	
	/**
	 * <p>
	 * Applies the mutation in the current copopulation.
	 * </p>
	 */
	public void mutate_copopulation(){
		int posiciones, i, j,sel;
		int n_genes = 0;
		int Mu_next,acum,pos;
		double m;
		double rnd;
		Chromosome rule;
		Cochromosome rules;
//		ArrayList<Chromosome> bag = new ArrayList<Chromosome>();
//		for(int p=0;p<ruleSet.length;p++){
//			n_genes += ruleSet[p].chrom.size(); 
//		}
		n_genes = copopSize;
		posiciones = n_genes*ruleSet.length;
		Mu_next = copopulationMu;
		
		if (prob_mutacion>0)
			while (Mu_next<posiciones){
				/* The chromosome and the gen which correspond to the position to mutate is determined*/

//				i=Mu_next/n_genes;
				pos = Mu_next;
				acum = 0;
				i = 0;
				while(acum+ruleSet[i].chrom.size()<=pos){
					acum += ruleSet[i].chrom.size();
					i++;					
				}

//				i--;
//				pos = pos%acum;
				pos-=acum;
				j = pos;
//				System.err.println("i/j: "+i+"/"+j+"  Mu_next:"+Mu_next);
				/* The mutation on the gene is performed */
				rnd = Randomize.Rand();
				if((rnd < 0.5 && i>1) || i == (nCopopulations-1)){
					sel = Randomize.Randint(0, ruleSet[i].chrom.get(j).getNumRules());
					//remove one random rule -chromosome- from the cochromosome
					ruleSet[i].chrom.get(j).removeChromosome(sel);
					//put the new cochromosome in a subpopulation with less rules per 
					//cochromosome
					rules = ruleSet[i].chrom.get(j);
					Collections.sort(rules.rules,Collections.reverseOrder());
					/* Se marca el cromosoma mutado para su posterior evaluacion */
					rules.setEvaluated(false);
					ruleSet[i].chrom.remove(rules);
					ruleSet[i-1].addRule(rules);
					
				}
				else{
					sel = Randomize.Randint(0, bestRulePool.size()+randomRules.size());
					if(sel < bestRulePool.size()){
						rule = bestRulePool.get(sel);
					}
					else{
						sel-=bestRulePool.size();
						rule = randomRules.get(sel);

					}

//					while(ruleSet[i].chrom.get(j).contains(rule)){
//						sel = (sel+1)%(bestRulePool.size()+randomRules.size());
//						if(sel < bestRulePool.size()){
//							rule = bestRulePool.get(sel);
//						}
//						else{
////							selectedRule-=bestRulePool.size();
//							rule = randomRules.get(sel-bestRulePool.size());
//
//						}
//					}
					//add the random rule to the chromosome 
					ruleSet[i].chrom.get(j).addChromosome(rule);
					rules = ruleSet[i].chrom.get(j);
					Collections.sort(rules.rules,Collections.reverseOrder());
					/* The mutated chromosome is marked for latter evaluation */
					rules.setEvaluated(false);
					
					ruleSet[i].chrom.remove(rules);
					ruleSet[i+1].addRule(rules);
				}
				
				

				/* The next mutation position is computed */
				if (prob_mutacion<1)
				{
					m = Randomize.Rand();
					Mu_next += Math.ceil (Math.log(m) / Math.log(1.0 - prob_mutacion));
				}
				else
					Mu_next += 1;
			}

		Mu_next -= posiciones;
		copopulationMu = Mu_next;
	}
	
	/**
	 * <p>
	 * Evaluates the copopulation, mutating and evaluating it for a established number
	 * of iterations.
	 * </p>
	 */
	public void evolve_copopulation(){
		int gen = 0;
		evaluate_copopulation();
		while(gen<generationLimit){
			mutate_copopulation();
			evaluate_copopulation();
			gen++;
			if(gen%10==0)
				System.out.print(".");
		}
		System.out.print(" ");
		
	}
	
	/**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
	public void run(){
		int gen = 0;
		Chromosome rule;
		int selectedRule,randomPoolSize;
		int stagnation = 0;
		double previousCR = -1;
		boolean createPop = true;
		String instanciasIN[] = new String[IS.getNumInstances()];
		String instanciasOUT[] = new String[IS.getNumInstances()];
		
		copopulationMu = 0;
		initialize();
		
		evaluate();
		
		while(gen<generationLimit){
			if(gen > 0)
//			System.out.print("True best CR: "+bestRuleSet.classify(IS, instanciasIN, instanciasOUT)+" ");
			tournament_selection();
			
			crossOver();
			
			mutate();
			
			evaluate();
			
			token_competition();
			
			regeneration();
			
			elitism();
			//get one-tenth of the main poblation for diversity
			randomRules = new ArrayList<Chromosome>();
			randomPoolSize = (int)Math.max(nCopopulations*2 - bestRulePool.size(),0.1*popSize);
			for(int i=0;i<randomPoolSize;i++){
				selectedRule = Randomize.Randint(0, popSize);
				rule = main.getRule(selectedRule);
//				while(randomRules.contains(rule) && bestRulePool.contains(rule)){
//					selectedRule = (selectedRule+1)%popSize;
//					rule = main.getRule((selectedRule)%popSize);
//				}
				randomRules.add(rule);
			}
			if(createPop){
				create_copopulation();
				createPop = false;
//				evaluate_copopulation();
				
			}
			
//			mutate_copopulation();
//			evaluate_copopulation();
			
			evolve_copopulation();
			
			//stagnation control <-- reset the population if
			//no improvement in 20 iterations has been achieved
			if(previousCR != bestRuleSet.fitness){
				stagnation = 0;
				previousCR = bestRuleSet.fitness;
				System.out.print("\ngeneration "+(gen+1)+" ");
				System.out.print("pool size: "+bestRulePool.size());
				System.out.print(" Best CR: "+bestRuleSet.fitness);
			}
			else
				stagnation++;
				
			if(stagnation > 20){
//				Make a hard restart
				initialize();
//				
				evaluate();
				createPop = true;
				stagnation = 0;
				System.out.print(" ** Restart Done ** ");
			}
			gen++;
		}
//		Collections.sort(bestRuleSet.rules,Collections.reverseOrder());
		instanciasIN = new String[ISval.getNumInstances()];
		instanciasOUT = new String[ISval.getNumInstances()];
		System.out.println("\nTrain CR: "+bestRuleSet.classify(ISval,instanciasIN,instanciasOUT));
		writeOutput(output_train_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
		instanciasIN = new String[IStest.getNumInstances()];
		instanciasOUT = new String[IStest.getNumInstances()];
		System.out.println("Test CR: "+bestRuleSet.classify(IStest,instanciasIN,instanciasOUT));
		writeOutput(output_test_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
//		write the obtained rules to disk
		printRules();
		
	}
	
	/**
	 * <p>
	 * Print the final set of rules to disk
	 * </p>
	 */
	protected void printRules(){
		String cad = new String();
		Attribute a;
		Chromosome rule;
		Gene gen;
		for(int i=0;i<bestRuleSet.getNumRules();i++){
			cad = cad + "IF ";
			rule = bestRuleSet.rules.get(i);
			
			for(int j=0;j<rule.chrom.length;j++){
				if(rule.chrom[j] != null){
					gen = rule.chrom[j];
					a = gen.att;
					
					if(gen.type == Attribute.NOMINAL){
						cad += a.getName();
						if(gen.relation == Gene.equal)
							cad += " equals [";
						else
							cad += " not equals [";
						for(int k=0;k<gen.nominalValue.length;k++){
							if(gen.nominalValue[k])
								cad += a.getNominalValue(k) + ",";
						}
						cad += "]";
					}
					else{
						cad += a.getName();
						if(gen.relation == Gene.greaterThanOrEqual)
							cad += " >= " + gen.realValue;
						else if(gen.relation == Gene.greaterThan)
							cad += " > " + gen.realValue;
						else if(gen.relation == Gene.lessThan)
							cad += " < " + gen.realValue;
						else if(gen.relation == Gene.lessThanOrEqual)
							cad += " <= " + gen.realValue;
						else if(gen.relation == Gene.inBound)
							cad += " in [" + gen.bound1 +"," + gen.bound2 +"]";
						else if(gen.relation == Gene.outOfBound)
							cad += " out of [" + gen.bound1 +"," + gen.bound2 +"]";
					}
				}
				if(j<rule.chrom.length-1)
					cad += " AND ";
			}
			if(Attributes.getOutputAttribute(0).getType() == Attribute.NOMINAL)
				cad += " THEN " + Attributes.getOutputAttribute(0).getNominalValue(rule.getClas());
			else
				cad += " THEN " + rule.getClas();
			
			cad += "\n";
			if(i<bestRuleSet.getNumRules()-1)
				cad += "ELSE ";
			
		}
		Files.writeFile(method_output, cad);
	}
}

