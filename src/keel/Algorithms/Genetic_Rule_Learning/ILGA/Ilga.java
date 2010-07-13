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
 * @author Written by Julián Luengo Martín 02/03/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.ILGA;

import org.core.Files;
import org.core.Randomize;
import keel.Algorithms.Preprocess.Basic.KNN;
import keel.Dataset.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * <p>
 * This class contains the main body of the ILGA algorithm, presented by:
 * </p>
 * <p>
 * Guan, S.-U., Zhu, F. An incremental approach to genetic-algorithms-based classification. IEEE Transactions on Systems, Man, and Cybernetics, Part B: Cybernetics 35 (2), pp. 227-239
 * </p>
 */
public class Ilga {
	static int IS1 = 1;
	static int IS2 = 2;
	static int IS3 = 3;
	static int IS4 = 4;
	
	int long_poblacion;
	int n_genes;
	int nAtt;
	double prob_mutacion = 0.01;
	double crossoverRate = 1.0;
	int numberRules;
	int Mu_next;
	int stagnationLimit = 30;
	int generationLimit = 60;
	double survivorsPercent = 0.5;
	double mutationRedRate = 0.6;
	double crossoverRedRate = 0.6;
	double bestCR = -1;
	RuleSet  poblacion[];
	RuleSet previousPob[];
	RuleSet intermediatePob[];
	int incrementalStrategy;
	InstanceSet IS;
	InstanceSet ISval;
	InstanceSet IStest;
	String input_train_name = new String();
	String input_validation_name;
	String input_test_name = new String();
	String output_train_name = new String();
	String output_test_name = new String();
	String method_output;
	String attributeOrdering;
	long seed;
	
	static int attributeOrder[] = new int[Attributes.getInputNumAttributes()];
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public Ilga(){
		poblacion = null;
	}
	
	/**
	 * Constructor for the KEEL parameter file
	 * @param paramfile the file with the parameters of this method
	 */
	public Ilga(String paramfile){
		config_read(paramfile);
		Randomize.setSeed(seed);
		poblacion = new RuleSet[long_poblacion];
		nAtt = 0;
		n_genes = numberRules*(3*nAtt+1);
		try {
			IS = new InstanceSet();
			IStest = new InstanceSet();
			ISval = new InstanceSet();
			// Load in memory a dataset that contains a classification problem
			IS.readSet(input_train_name, true);
			ISval.readSet(input_validation_name, false);
			IStest.readSet(input_test_name,false);
			
			attributeOrder = new int[Attributes.getInputNumAttributes()];
		} catch (Exception e) {
			System.out.println("Dataset exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}

		for(int i=0;i<long_poblacion;i++){
			poblacion[i] = new RuleSet(numberRules,0);
		}
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
			out = line.split("ProbMutation = ");
			prob_mutacion = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("CrossoverRate = ");
			crossoverRate = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("popSize = ");
			long_poblacion = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("ruleNumber = ");
			numberRules = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("stagnationLimit = ");
			stagnationLimit = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("generationLimit = ");
			generationLimit = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("SurvivorsPercent = ");
			survivorsPercent = (new Double(out[1])).doubleValue();
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("mutationRedRate = ");
			mutationRedRate = (new Double(out[1])).doubleValue();
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("crossoverRedRate = ");
			crossoverRedRate = (new Double(out[1])).doubleValue();
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("AttributeOrder = ");
			attributeOrdering = (new String(out[1])); 

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("incrementalStrategy = ");
			incrementalStrategy = (new Integer(out[1])).intValue(); // parse the string into
			
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
	 * One-point crossover
	 * @param cr1 index of parent 1 in poblation
	 * @param cr2 index of parent 2 in poblation
	 */
	public void onePointCrossover(int cr1,int cr2){
		RuleSet rule1 = poblacion[cr1];
		RuleSet rule2 = poblacion[cr2];
		int cutpoint;
		int cutpoint_rule;
		int cutpoint_variable;

		//there are 3*number of attribute elements, plus class value in each cromosome
		do{
			cutpoint = Randomize.Randint(0, n_genes);
			cutpoint_rule = cutpoint/(3*nAtt+1);
			cutpoint_variable = cutpoint%(3*nAtt+1);
		}while(cutpoint_variable != (nAtt-1) && Randomize.Rand()>crossoverRedRate);
		
		//rule1 is replaced from cutpoint (inclusive) to the end of his rule set
		rule1.copyFromPointtoEnd(rule2, cutpoint_rule, cutpoint_variable);
		//rule2 is replaced from the begining of his rule set to cutpoint (not inclusive)
		rule2.copyFromBegintoPoint(rule1, cutpoint_rule, cutpoint_variable);
		//childs must be evaluated
		rule1.setEvaluated(false);
		rule2.setEvaluated(false);
	}
	/**
	 * It performs a one point crossover in the new poblation, using adjacent chromosomes as parents
	 */
	public void crossOver(){		
		for(int i=0;i<long_poblacion;i=i+2){
			if(Randomize.Rand() < crossoverRate && i+1 < long_poblacion)
				onePointCrossover(i,i+1);
		}
	}
	/**
	 * Copy the survivorsPercent proportion of the old poblation into the bottom half of 
	 * the new one
	 */
	public void elitism(){
		int parentspreserved = (int)(long_poblacion*survivorsPercent);
//		we keep the best parents and sons
//		Arrays.sort(poblacion,Collections.reverseOrder());
		for(int i=parentspreserved,j=0;i<long_poblacion;i++,j++){
			poblacion[i] = previousPob[j];
		}
	}
	/**
	 * Applies mutation in the new poblation
	 */
	public void mutate(){
		int posiciones, i, j;
		double m;

		posiciones=n_genes*long_poblacion;

		if (prob_mutacion>0)
			while (Mu_next<posiciones){
				/* Se determina el cromosoma y el gen que corresponden a la posicion que
			se va a mutar */
				i=Mu_next/n_genes;
				j=Mu_next%n_genes;

				/* Se efectua la mutacion sobre ese gen */
				if((j%(3*nAtt+1))/3 != (nAtt-1) && Randomize.Rand()<mutationRedRate)
					poblacion[i].mutate(j);

				/* Se marca el cromosoma mutado para su posterior evaluacion */
				poblacion[i].setEvaluated(false);

				/* Se calcula la siguiente posicion a mutar */
				if (prob_mutacion<1)
				{	
					m = Randomize.Rand();
					Mu_next += Math.ceil (Math.log(m) / Math.log(1.0 - prob_mutacion));
				}
				else
					Mu_next += 1;
			}

		Mu_next -= posiciones;
	}
	/**
	 * Applies a roulette wheel selection
	 */
	public void selection(){
		RuleSet  temp[];
		double probability[] = new double [long_poblacion];
		double total;
		double prob;
		int sel;
		
		temp = new RuleSet[long_poblacion];
		//sort the poblation in order of fitness
		Arrays.sort(poblacion, Collections.reverseOrder());
		
		probability[0] = poblacion[0].getFitness();
		for(int i=1;i<long_poblacion;i++){
			probability[i] = probability[i-1]+poblacion[i].getFitness();
		}
		total = probability[long_poblacion-1];
		for(int i=0;i<long_poblacion;i++){
			probability[i] /= total;
		}
		for(int i=0;i<long_poblacion;i++){
			prob = Randomize.Rand();
			sel = -1;
			for(int j=0;j<long_poblacion && sel==-1;j++){
				if(probability[j]>prob)
					sel = j;
			}
			temp[i] = new RuleSet(poblacion[sel]);
		}
		
		previousPob = poblacion;
		poblacion = temp;
		
	}
	/**
	 * Applies a tournament selection, with tournament size of 2
	 */
	public void tournament_selection(){
		int i, j, k, mejor_torneo;

		int tam_torneo = 2;
		int Torneo[] = new int[tam_torneo];
		boolean repetido;
		RuleSet sample[] = new RuleSet[long_poblacion];
		
		for (i=0;i<long_poblacion;i++){
			
			Torneo[0] = Randomize.Randint(0,long_poblacion);
			mejor_torneo=Torneo[0];
	   
			for (j=1;j<tam_torneo;j++)
			{
				do
				{
					Torneo[j] = Randomize.Randint(0,long_poblacion);
					repetido=false; k=0;
					while ((k<j) && (!repetido)){
						if (Torneo[j]==Torneo[k])
							repetido=true;
						else
							k++;
					}
				}
				while (repetido);
	     
				if (poblacion[Torneo[j]].fitness > poblacion[mejor_torneo].fitness)
					mejor_torneo=Torneo[j];
			}
	       
			sample[i] = new RuleSet(poblacion[mejor_torneo]);
		}
		previousPob = poblacion;
		poblacion = sample;
	}
	/**
	 * Its evaluate the NEW poblation, with the train data
	 */
	public void evaluate(){
		double fitness_train,fitness_test;
		for(int j=0;j<long_poblacion;j++){
			fitness_train = poblacion[j].classify(IS);
//			poblacion[j].setEvaluated(false);
//			fitness_test = poblacion[j].classify(IStest);
//			poblacion[j].fitness = (fitness_train+fitness_test)/2.0;
		}
		Arrays.sort(poblacion,Collections.reverseOrder());
	}
	
	/**
	 * @param sem the SEM model evolved for the actual attribute
	 */
	public void IGA(SEM sem, int whichSEM){
		int sel;
		RuleSet previousBestPoblation = new RuleSet(poblacion[0]);
		
		for(int i=0;i<long_poblacion;i++){
			//SEM size is long_poblacion/2
			sel = Randomize.Randint(0, long_poblacion/2);
			
			if(this.incrementalStrategy == Ilga.IS1){
				poblacion[i] = new RuleSet(previousBestPoblation);
				poblacion[i].IS3(whichSEM);
			}
			else if(this.incrementalStrategy == Ilga.IS2){
				poblacion[i] = new RuleSet(previousBestPoblation);
				poblacion[i].IS4(sem.getChromosome(sel));
			}
			else if(this.incrementalStrategy == Ilga.IS3)
				poblacion[i].IS3(whichSEM);
			else if(this.incrementalStrategy == Ilga.IS4)
				poblacion[i].IS4(sem.getChromosome(sel));
			
			poblacion[i].setEvaluated(false);
		}
	}
	
	
	/**
	 * Runs the ILGA algorithm, with first creates and evolve a single SEM for 
	 * each attribute. The next step comprises the selection of a attribute order
	 * for integration in an incremental way.
	 * For each attribte, we use the SEM evolved for it, and integrate its 1 attribute rules
	 * in the OIGA ones, using the selected IGA approach.
	 * Each integration implies an GA execution for adapting the rules.
	 * 
	 */
	public void run(){
		boolean endCondition = false;
		AttributeCR attCR[] = new AttributeCR[Attributes.getInputNumAttributes()];
		SEM selector[] = new SEM[Attributes.getInputNumAttributes()];
		int gen = 0;
		int stagnation = 0;
		RuleSet swp;
		String instanciasIN[];
		String instanciasOUT[];
		int pos;
		
		
		//set the initial order of the attributes
		for(int i=0;i<Attributes.getInputNumAttributes();i++){
			//set the first attribute to the current, in order to be used by
			//the SEM
			attributeOrder[0] = i;
			attCR[i] = new AttributeCR(i,0);
			selector[i] = new SEM(long_poblacion/2,numberRules/2,i,IS);
			selector[i].setGenerationLimit(generationLimit/2);
			selector[i].IStest = this.IStest;
			selector[i].prob_mutacion = this.prob_mutacion;
			selector[i].run();
			
			attCR[i].CR = selector[i].getCR();
			System.out.println("SEM ["+i+"] CR: "+selector[i].getCR());
		}	
		//now set the attributeOrder vector, with the attribute order for the IGA
		if(attributeOrdering.compareTo("descendent")==0)
			Arrays.sort(attCR,Collections.reverseOrder());
		if(attributeOrdering.compareTo("ascendent")==0)
			Arrays.sort(attCR);
		for(int i=0;i<Attributes.getInputNumAttributes();i++){
			if(attributeOrdering.compareTo("original")==0)
				attributeOrder[i] = i;
			if(attributeOrdering.compareTo("random")==0){
				boolean found = false;
				pos = Randomize.Randint (0, Attributes.getInputNumAttributes());
				for(int j=i-1;j>=0 && !found;j--){
					if(attributeOrder[j] == pos)
						found = true;
				}
				while(found){
					pos = (pos+1)%Attributes.getInputNumAttributes();
					found = false;
					for(int j=i-1;j>=0 && !found;j--){
						if(attributeOrder[j] == pos)
							found = true;
					}
				}
				attributeOrder[i] = pos;
			}
			if(attributeOrdering.compareTo("descendent")==0)
				attributeOrder[i] = attCR[i].attribute;
			if(attributeOrdering.compareTo("ascendent")==0)
				attributeOrder[i] = attCR[i].attribute;
		}

		for(int i=0;i<Attributes.getInputNumAttributes();i++){
			//integrate the rules of the SEM
			IGA(selector[attributeOrder[i]],attributeOrder[i]);
			nAtt++;
			n_genes = numberRules*(3*nAtt+1);
			//Evolve the entire rule set with new attributes
			bestCR = -1;
			stagnation = 0;
			endCondition = false;
			gen = 0;
			
			evaluate();
			while(!endCondition){
				tournament_selection();

				crossOver();

				mutate();
				elitism();
				evaluate();
				
				Arrays.sort(poblacion,Collections.reverseOrder());
				gen++;
				if(bestCR!=poblacion[0].getFitness())
					stagnation = 0;
				else
					stagnation++;
				if(gen>generationLimit || stagnation > stagnationLimit || poblacion[0].getFitness()==1.0)
					endCondition = true;
				bestCR = poblacion[0].getFitness();
			}
			System.out.print("\nattribute ["+attributeOrder[i]+"] added. "+(gen-1)+"/"+generationLimit+" iterations used.");
			System.out.print(" CR = "+bestCR);
		}
		System.out.println("\nCR train: "+bestCR);
		for(int i=0;i<1;i++){
			poblacion[i].setEvaluated(false);
			System.out.println("["+i+"] CR test: "+poblacion[i].classify(IStest));
		}
		
		//Write Train file results
		instanciasIN = new String[ISval.getNumInstances()];
		instanciasOUT = new String[ISval.getNumInstances()];
		for(int i=0;i<ISval.getNumInstances();i++){
			Attribute a = Attributes.getOutputAttribute(0);

			int tipo = a.getType();

			int claseObt = poblacion[0].classify(ISval.getInstance(i));
			if(tipo!=Attribute.NOMINAL){
				instanciasIN[i] = new String(String.valueOf(ISval.getInstance(i).getOutputNominalValues(0)));
				if(claseObt!=-1)
					instanciasOUT[i] = new String(String.valueOf(claseObt));
				else
					instanciasOUT[i] = new String("?");
			}
			else{
				instanciasIN[i] = new String(ISval.getInstance(i).getOutputNominalValues(0));
				if(claseObt!=-1)
					instanciasOUT[i] = new String(a.getNominalValue(claseObt));
				else
					instanciasOUT[i] = new String("?");
			}
		}
		writeOutput(output_train_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
		//write test file results
		instanciasIN = new String[IStest.getNumInstances()];
		instanciasOUT = new String[IStest.getNumInstances()];
		for(int i=0;i<IStest.getNumInstances();i++){
			Attribute a = Attributes.getOutputAttribute(0);

			int tipo = a.getType();

			int claseObt = poblacion[0].classify(IStest.getInstance(i));
			if(tipo!=Attribute.NOMINAL){
				instanciasIN[i] = new String(String.valueOf(IStest.getInstance(i).getOutputNominalValues(0)));
				if(claseObt!=-1)
					instanciasOUT[i] = new String(String.valueOf(claseObt));
				else
					instanciasOUT[i] = new String("?");
			}
			else{
				instanciasIN[i] = new String(IStest.getInstance(i).getOutputNominalValues(0));
				if(claseObt!=-1)
					instanciasOUT[i] = new String(a.getNominalValue(claseObt));
				else
					instanciasOUT[i] = new String("?");
			}
		}
		writeOutput(output_test_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
//		write the obtained rules to disk
		printRules();
	}
	
	/**
	 * <p>
	 * Print the rules to the file passed as parameters in the configuration file
	 * </p>
	 */
	protected void printRules(){
		Rule r;
		String cad = new String();
		Attribute a;
		double lims[] = null;
		for(int i=0;i<poblacion[0].numberRules;i++){
			r = poblacion[0].reglas[i];
			cad += "IF ";
			for(int j=0;j<r.numAttributes;j++){
				a = Attributes.getInputAttribute(j);
				for(int n=0;n<r.numAttributes;n++){
					if(attributeOrder[n] == j)
						lims = r.getLimits(n);
				}
				if(a.getType()!=Attribute.NOMINAL)
					cad += String.valueOf(lims[0]) + " < " + a.getName() + " < " + String.valueOf(lims[1]);
				else{
					cad += a.getName() + " in [";
					for(int k=(int)lims[0];k<=(int)lims[1];k++){
						if(k != (int)lims[0])
							cad += ",";
						cad += a.getNominalValue(k);
					}
					cad += "]";
				}
				
				if(j<r.numAttributes-1)
					cad += " AND ";
			}
			if(Attributes.getOutputAttribute(0).getType() == Attribute.NOMINAL)
				cad += " THEN " + Attributes.getOutputAttribute(0).getNominalValue(r.getClas());
			else
				cad += " THEN " + r.getClas();
			cad +="\n";
		}
		Files.writeFile(method_output, cad);
	}

}

