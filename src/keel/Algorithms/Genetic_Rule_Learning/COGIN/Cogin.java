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

package keel.Algorithms.Genetic_Rule_Learning.COGIN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import org.core.Files;
import org.core.Randomize;
import keel.Dataset.*;


/**
 * <p>
 * This class implements the COGIN algorithm from:
 * </p>
 * <p>
 * David Perry Greene and Stephen F. Smith. Competition-Based Induction of Decision Models from Examples. Machine Learning, 13: 229-257, 1993.
 * </p>
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class Cogin {
	
	ArrayList<Chromosome> poblation;
	ArrayList<Chromosome> offspring;
	ArrayList<Chromosome> bestPob;
//	Chromosome coverInstance[];
	long seed;
	
	InstanceSet IS;
	InstanceSet ISval;
	InstanceSet IStest;
	String input_train_name = new String();
	String input_validation_name;
	String input_test_name = new String();
	String output_train_name = new String();
	String output_test_name = new String();
	String method_output;
	int numClasses;
	double datasetEntropy = -1;
	
	double crossoverRate = 0.9;
	boolean useNegationBit = true;
	int missclassificationErrorLevel = 2;
	int generationLimit = 1000;
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public Cogin(){
		poblation = offspring = null;
	}
	
	/**
	 * <p>
	 * Builds up the COGIN with the provided parameters in KEEL format
	 * </p>
	 * @param paramfile The path to the configuration file with all the parameters in KEEL format
	 */
	public Cogin(String paramfile){
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
//		coverInstance = new Chromosome[IS.getNumInstances()];
		Attribute a =Attributes.getOutputAttribute(0);
		if(a.getType() == Attribute.NOMINAL)
			numClasses = a.getNumNominalValues();
		else
			numClasses =(int)( a.getMaxAttribute() - a.getMinAttribute());
		
		poblation = new ArrayList<Chromosome>();
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
			out = line.split("missclassificationErrorLevel = ");
			missclassificationErrorLevel = (new Integer(out[1])).intValue(); // parse the string into

			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("generationLimit = ");
			generationLimit = (new Integer(out[1])).intValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("CrossoverRate = ");
			crossoverRate = (new Double(out[1])).doubleValue(); // parse the string into
			
			do {
				line = buf_reader.readLine();
			} while (line.length() == 0);
			out = line.split("useNegationBit = ");
			useNegationBit = (out[1].compareTo("Yes")==0); // parse the string into

			file_reader.close();

		} catch (IOException e) {
			System.out.println("IO exception = " + e);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	
	protected void initialize(){
		int coveredExamples = 0;
		double rnd;
		int previousCovered;
		Chromosome rule = new Chromosome(Attributes.getInputNumAttributes());
		Gene gen;
		Instance inst;
		ArrayList<Instance> uncovered = new ArrayList<Instance>();
		boolean allCovered = false;
		
		for(int i=0;i<IS.getNumInstances();i++){
			inst = IS.getInstance(i);
			uncovered.add(i, inst);
		}
		
		System.out.println("Covered examples at initialization:");
		//first, we try to generate a completely random rule
		while(coveredExamples<IS.getNumInstances() || poblation.size() < 10){
			previousCovered = coveredExamples;
			for(int i=0;i<rule.getNumGenes();i++){
				gen = new Gene(Attributes.getInputAttribute(i));
				for(int j=0;j<gen.getNumBits();j++){
					rnd = Randomize.Rand(); 
					if(rnd<0.5)
						gen.setBit(j, '#');
					else if(rnd<0.75)
						gen.setBit(j, '0');
					else
						gen.setBit(j, '1');
				}
				if(Randomize.Rand()<0.1 && useNegationBit)
					gen.setNegation(1);
				rule.setGene(i, gen);
			}
			rule.setClass(Randomize.Randint(0, numClasses));
			//now, we search the uncovered examples that this new rule covers
			for(int i=0;i<uncovered.size();i++){
				inst = uncovered.get(i);
				if(rule.covers(inst)){
					coveredExamples++;
					uncovered.remove(i);
					i--;
				}
			}
			if(coveredExamples > previousCovered || allCovered){
				poblation.add(rule);
				System.out.print(coveredExamples+"/"+uncovered.size()+" -> ");
			}
			else{
				inst = uncovered.get(0);
				rule.makeCover(inst);
				for(int i=0;i<uncovered.size();i++){
					inst = uncovered.get(i);
					if(rule.covers(inst)){
						coveredExamples++;
						uncovered.remove(i);
						i--;
					}
				}
				if(coveredExamples > previousCovered || allCovered){
					poblation.add(rule);
					System.out.print(coveredExamples+"/"+uncovered.size()+" -> ");
				}
			}
			rule = new Chromosome(Attributes.getInputNumAttributes());
			if(coveredExamples == IS.getNumInstances())
				allCovered = true;
		}
		System.out.println("end");
	}
	
	protected void randomSelection(){
		int p1,p2,pos,tmp;
		int baraje[] = new int[poblation.size()];
		int baraje2[] = new int[poblation.size()];

		for (int i = 0; i < poblation.size(); i++){
			baraje[i] = i;
			baraje2[i] = i;
		}

		for (int i = 0; i < poblation.size(); i++) {

			pos = Randomize.Randint(i, poblation.size());
			tmp = baraje[i];
			baraje[i] = baraje[pos];
			baraje[pos] = tmp;
			
			do{
				pos = Randomize.Randint(i, poblation.size());
				if(pos>0 && baraje2[pos] == baraje[i] && baraje2[pos-1] != baraje[i])
					pos--;
				if(pos==0 && baraje2[pos] == baraje[i] && baraje2[pos+1] != baraje[i])
					pos++;
			}while(baraje2[pos] == baraje[i]);
			tmp = baraje2[i];
			baraje2[i] = baraje2[pos];
			baraje2[pos] = tmp;

		}
		
		offspring = new ArrayList<Chromosome>();
		for(int i=0;i<(int)(poblation.size()*crossoverRate);i+=2){
			p1 = baraje[i];
			p2 = baraje2[i];
			while(p1==p2){
				pos = Randomize.Randint(i, poblation.size());
				tmp = baraje2[i];
				baraje2[i] = baraje2[pos];
				baraje2[pos] = tmp;
				p2 = baraje2[i];
			}
			
			offspring.add(new Chromosome(poblation.get(p1)));
			offspring.add(new Chromosome(poblation.get(p2)));
		}
	}
	
	protected void onePointCrossover(){
		Chromosome parent1,parent2;
		Instance inst;
		int max1,max2;
		int voted1[] = new int[numClasses];
		int voted2[] = new int[numClasses];
		for(int i=0;i<offspring.size();i+=2){
			parent1 = offspring.get(i);
			parent2 = offspring.get(i+1);
			
			parent1.swapOnePoint(parent2);
			//now apply the ex-post assignment
			for(int j=0;j<numClasses;j++){
				voted1[j] = 0;
				voted2[j] = 0;
			}
			for(int j=0;j<IS.getNumInstances();j++){
				inst = IS.getInstance(j);
				if(parent1.covers(inst))
					voted1[(int)inst.getAllOutputValues()[0]]++;
				if(parent2.covers(inst))
					voted2[(int)inst.getAllOutputValues()[0]]++;
			}
			max1 = max2 = 0;
			for(int j=1;j<numClasses;j++){
				if(voted1[j] > voted1[max1])
					max1 = j;
				if(voted1[j] == voted1[max1] && Randomize.Rand()<0.5)
					max1 = j;
				
				if(voted2[j] > voted2[max2])
					max2 = j;
				if(voted2[j] == voted2[max2] && Randomize.Rand()<0.5)
					max2 = j;
			}
			parent1.setClass(max1);
			parent1.setCoveredInstances(max1);
			parent2.setClass(max2);
			parent2.setCoveredInstances(max2);
		}
	}
	
	protected void evaluate(){
		Chromosome rule;
		int missclassified,matched,output;
		int matchedClasses[];
		int unmatchedClasses[];
		double info,lex,Hm,Hunm;
		Instance inst;
		boolean match;
		//first, compute the dataset's entropy if needed -it won't change...-
		if(datasetEntropy==-1){
			int nInstancesPerClass[] = new int[numClasses];
			for(int i=0;i<IS.getNumInstances();i++){
				nInstancesPerClass[(int)IS.getInstance(i).getAllOutputValues()[0]]++;
			}
			datasetEntropy = 0;
			for(int i=0;i<numClasses;i++){
				if(nInstancesPerClass[i]!=0)
					datasetEntropy -= ((double)nInstancesPerClass[i]/IS.getNumInstances())*Math.log((double)nInstancesPerClass[i]/IS.getNumInstances());
			}
		}
		//compute the fitness for the new candidate rules
		for(int i=0;i<offspring.size();i++){
			rule = offspring.get(i);
			missclassified = 0;
			matched = 0;
			matchedClasses = new int[numClasses];
			unmatchedClasses = new int[numClasses];
			for(int j=0;j<IS.getNumInstances();j++){
				inst = IS.getInstance(j);
				output = (int)inst.getAllOutputValues()[0];
				match = rule.covers(inst);
				if(match){
					matched++;
					matchedClasses[output]++;
					if(output != rule.classify(inst))
						missclassified++;
				}
				else{
					unmatchedClasses[output]++;
				}
			}
			//the Info(R) part...
			Hm = Hunm =0;
			for(int j=0;j<numClasses;j++){
				if(matchedClasses[j]!=0)
					Hm += -(((double)matchedClasses[j]/IS.getNumInstances())*Math.log((double)matchedClasses[j]/IS.getNumInstances()));
			}
			info = Hm * matched;
			for(int j=0;j<numClasses;j++){
				if(unmatchedClasses[j]!=0)
					Hunm += -(((double)unmatchedClasses[j]/IS.getNumInstances())*Math.log((double)unmatchedClasses[j]/IS.getNumInstances()));
			}
			info += Hunm * (IS.getNumInstances()-matched);
			info = info / IS.getNumInstances();
			info = datasetEntropy - info;
			//the Lex(R) part
			lex =(double) IS.getNumInstances()/missclassificationErrorLevel;
			lex = lex - (double)missclassified/missclassificationErrorLevel;
			//set the fitness
			rule.setFitness(info+lex);
		}
	}
	
	protected void competitiveReplacement(){
		ArrayList<Chromosome> coverage = new ArrayList<Chromosome>();
		ArrayList<Instance> filter = new ArrayList<Instance>();
		Instance inst;
		Chromosome rule;
		boolean itCovers[],match;
		
		coverage.addAll(poblation);
		coverage.addAll(offspring);
		Collections.sort(coverage,Collections.reverseOrder());
		itCovers = new boolean[coverage.size()];
		
		for(int j=0;j<coverage.size();j++){
			itCovers[j] = false;
		}
		for(int i=0;i<IS.getNumInstances();i++){
			filter.add(IS.getInstance(i));
		}
		
		for(int i=0;i<IS.getNumInstances();i++){
			inst = IS.getInstance(i);
			match = false;
			for(int j=0;j<coverage.size() && !match;j++){
				rule = coverage.get(j);
				match = rule.covers(inst);
				if(match){
					filter.remove(inst);
					itCovers[j] = true;
				}
			}
		}
		
		for(int j=0,i=0;j<coverage.size();j++,i++){
			if(!itCovers[i]){
				coverage.remove(j);
				j--;
			}
		}
		poblation = coverage;
		Collections.sort(poblation,Collections.reverseOrder());
	}
	
	/**
     * <p>
     * Process the training and test files provided in the parameters file to the constructor.
     * </p>
     */
	public void run(){
		int gen = 0;
		Attribute a;
		double bestCR,CR;
		String instanciasIN[] = new String[IS.getNumInstances()];
		String instanciasOUT[] = new String[IS.getNumInstances()];
		
		for(int i=0;i<Attributes.getInputNumAttributes();i++){
			a = Attributes.getInputAttribute(i);
			if(a.getType() == Attribute.REAL){
				System.err.println("COGIN works with discrete values. Please discretize first.");
				System.exit(-1);
			}
		}
		
		initialize();
		offspring = poblation; //trick to allow the evaluate() function compute the fitness
		//of the actual poblation instead of the nonexistent offspring
		evaluate();
		offspring = null;
		bestCR = classify(IS, instanciasIN, instanciasOUT);
		while(gen < generationLimit){
			randomSelection();
			
			onePointCrossover();
			
			evaluate();
			
			competitiveReplacement();
			CR = classify(IS, instanciasIN, instanciasOUT);
			if(bestPob == null || bestCR < CR || (bestCR == CR && poblation.size()<bestPob.size())){
				bestCR = CR;
				bestPob = new ArrayList<Chromosome>();
				bestPob.addAll(poblation);
				System.out.print("\ngeneration "+gen+ " BestCR: "+bestCR);
				if((gen+1)%10==0)
					System.out.println();
			}
			gen++;
			if(gen%10==0)
				System.out.print(".");
		}
		System.out.println("\nbestCR: "+bestCR);
		instanciasIN = new String[ISval.getNumInstances()];
		instanciasOUT = new String[ISval.getNumInstances()];
		classify(ISval, instanciasIN, instanciasOUT);
		writeOutput(output_train_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
		instanciasIN = new String[IStest.getNumInstances()];
		instanciasOUT = new String[IStest.getNumInstances()];
		poblation = bestPob;
		System.out.println("Test CR: "+classify(IStest,instanciasIN,instanciasOUT));
		writeOutput(output_test_name, instanciasIN, instanciasOUT, Attributes.getInputAttributes(),
				Attributes.getOutputAttributes()[0], Attributes.getInputNumAttributes(), Attributes.getRelationName());
//		write the obtained rules to disk
		printRules();
		
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

	protected double classify(InstanceSet ISet, String instanciasIN[], String instanciasOUT[]){
		Instance inst;
		Chromosome rule;
		double input[];
		int output,predict;
		int tp;
		boolean match;
		//ordered list -IF_THEN_ELSE scheme-
		tp = 0;
		Attribute a = Attributes.getOutputAttribute(0);
		int tipo = a.getType();
		for(int i=0;i<ISet.getNumInstances();i++){
			inst = ISet.getInstance(i);
			input = inst.getAllInputValues();
			output = (int)inst.getAllOutputValues()[0];
			if(tipo!=Attribute.NOMINAL){
				instanciasIN[i] = String.valueOf(output);
			}
			else{
				instanciasIN[i] = new String(inst.getOutputNominalValues(0));
			}
			match = false;
			for(int j=0;j<poblation.size() && !match;j++){
				rule = poblation.get(j);
				match = rule.covers(inst);
				if(match){
					predict = rule.classify(inst);
					if(tipo!=Attribute.NOMINAL){
						instanciasOUT[i] = String.valueOf(predict);
					}else{
						instanciasOUT[i] = new String(a.getNominalValue(predict));
					}
					if(predict==output)
						tp++;
				}
			}
			if(instanciasOUT[i]==null)
				instanciasOUT[i] = "?";
		}
		return (double)tp/ISet.getNumInstances();
	}
	
	protected void printRules(){
		String cad = new String();
		ArrayList<Integer> conds;
		Chromosome rule;
		int value;
		Attribute a;
		for(int i=0;i<poblation.size();i++){
			cad = cad + "IF ";
			rule = poblation.get(i);
			for(int j=0;j<rule.getNumGenes();j++){
				cad += "( ";
				conds = rule.getGene(j).bin2nominal();
				Collections.sort(conds);
				a = Attributes.getAttribute(j);
				cad += a.getName()+ " is ";
				for(int k=0;k<conds.size();k++){
					value = conds.get(k).intValue();
					
					if(a.getType() == Attribute.NOMINAL && value<a.getNumNominalValues()){
						if(k!=0)
							cad += "OR ";
						cad += a.getNominalValue(value)+ " ";
					}
					else if (value <= a.getMaxAttribute() && value>=a.getMinAttribute() ){
						if(k!=0)
							cad += "OR ";
						cad += value + " ";
					}
				}
				cad += ")";
				if(j<rule.getNumGenes()-1)
				cad += " AND ";
			}
			if(Attributes.getOutputAttribute(0).getType() == Attribute.NOMINAL)
				cad += " THEN " + Attributes.getOutputAttribute(0).getNominalValue(rule.getClas());
			else
				cad += " THEN " + rule.getClas();
			cad += "\n";
			if(i<poblation.size()-1)
				cad += "ELSE ";
			
		}
		Files.writeFile(method_output, cad);
	}
}

