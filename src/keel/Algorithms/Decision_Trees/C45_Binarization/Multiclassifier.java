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

package keel.Algorithms.Decision_Trees.C45_Binarization;


import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import org.core.*;

import keel.Algorithms.Decision_Trees.C45.*;

/**
 * <p>Title: Multiclassifier</p>
 * <p>Description: This class implements the Main execution class for the Binarization methodology (OVO and OVO )
 * <p>Company: KEEL </p>
 * @author Mikel Galar (University of Navarra) 21/10/2010
 * @author Alberto Fernandez (University of Jaen) 15/05/2014
 * @version 1.2
 * @since JDK1.6
 */
public class Multiclassifier {

	myDataset train, val, test;
	String outputTr, outputTst, ficheroBR, fichTrain, claseMayoritaria[], cabecera, sOvo, binarization, method[];
	int nClasses, n_classifiers, neighbours, preprocessing, distance;
	boolean pruned, valid[];
	C45 classifiers[];
	myDataset[] train_sets;
	OVO ovo;
	float threshold,confidence;
	int instancesPerLeaf;
	boolean nested,dynamic;
	int[] empates;
	parseParameters parameters;
	String input_validation_name,input_test_name;
	double[] aprioriClassDistribution;
	RuleBase[] treeRuleSet;

	private boolean somethingWrong = false; //to check if everything is correct.

	/**
	 * Default constructor
	 */
	public Multiclassifier() {
	}

	/**
	 * It reads the data from the input files (training, validation and test) and parse all the parameters
	 * from the parameters array.
	 * @param parameters parseParameters It contains the input files, output files and parameters
	 */
	public Multiclassifier(parseParameters parameters) {

		this.parameters = parameters;
		train = new myDataset();
		val = new myDataset();
		test = new myDataset();
		fichTrain = parameters.getTrainingInputFile();
		try {
			System.out.println("\nReading the training set: " +
					parameters.getTrainingInputFile());
			train.readClassificationSet(parameters.getTrainingInputFile(), true);
			System.out.println("\nReading the validation set: " +
					parameters.getValidationInputFile());
			val.readClassificationSet(parameters.getValidationInputFile(), false);
			System.out.println("\nReading the test set: " +
					parameters.getTestInputFile());
			test.readClassificationSet(parameters.getTestInputFile(), false);
		}
		catch (IOException e) {
			System.err.println(
					"There was a problem while reading the input data-sets: " +
					e);
			somethingWrong = true;
		}

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasRealAttributes();
		//somethingWrong = somethingWrong || train.hasMissingAttributes();

		outputTr = parameters.getTrainingOutputFile();
		outputTst = parameters.getTestOutputFile();

		//Now we parse the parameters
		ficheroBR = parameters.getOutputFile(0);

		//Now we parse the parameters
		pruned = true;
		confidence = Float.parseFloat(parameters.getParameter(1));
		instancesPerLeaf = Integer.parseInt(parameters.getParameter(2));

		

		nClasses = train.getnClasses();
		aprioriClassDistribution = new double[nClasses];
		for (int i = 0; i < nClasses; i++) {
			aprioriClassDistribution[i] = 1.0 * train.numberInstances(i) /
			train.size();
		}

		binarization = parameters.getParameter(3);
		
		sOvo = "WEIGHTED";
		sOvo = parameters.getParameter(4);
		if (sOvo.equals("BTS"))
			threshold = Float.parseFloat(parameters.getParameter(5));
		else if (sOvo.equals("DynOVO")){
			sOvo = "WEIGHTED";
			dynamic = true;
		}
		nested = false;

		String prep = "NONE"; //parameters.getParameter(6);

		cabecera = parameters.getTestInputFile();

		String[] aux = null;
		aux = cabecera.split("\\.");
		cabecera = aux[aux.length - 2]; //aux.length-1 es la extension
		aux = cabecera.split("/");
		cabecera = aux[aux.length - 1];      

	}

	  /**
	   * It constructs a new set of OVO classifiers for NESTING aggregation
	   * @param nested if the classifier is nested
	   * @param padre the reference to the parent OVO
	   */
	public Multiclassifier(boolean nested, Multiclassifier padre) {

		train = padre.train;
		val = padre.val;
		test = padre.test;
		this.parameters = padre.parameters;
		fichTrain = padre.fichTrain;

		outputTr = parameters.getTrainingOutputFile();
		outputTst = parameters.getTestOutputFile();

		distance = padre.distance;
	    parameters.getParameter(4);
	    if (sOvo.equals("BTS"))
	        threshold = Float.parseFloat(parameters.getParameter(5));
	    this.nested = nested;       

	}

	/**
	 * It launches the algorithm
	 */
	public void execute() {
		if (somethingWrong) { //We do not execute the program
			System.err.println("An error was found, the data-set has missing values.");
			System.err.println("Aborting the program");
			//We should not use the statement: System.exit(-1);
		}
		else {
			//We do here the algorithm's operations

			nClasses = train.getnClasses();
	        input_validation_name = parameters.getValidationInputFile();
	        input_test_name = parameters.getTestInputFile();

			ovo = new OVO(this, sOvo, dynamic);

			n_classifiers = nClasses * (nClasses - 1) / 2;
			if (binarization.equals("OVA")){
				n_classifiers = nClasses;  
			}

			valid = new boolean[n_classifiers];
			claseMayoritaria = new String[n_classifiers];
			classifiers = new C45[n_classifiers];

			train_sets = new myDataset[n_classifiers];
			treeRuleSet = new RuleBase[n_classifiers];

			aprioriClassDistribution = new double[nClasses];
			for (int i = 0; i < nClasses; i++) {
				aprioriClassDistribution[i] = 1.0 * train.numberInstances(i) /
				train.size();
			}

			if (binarization.equals("OVO")){
				for (int i = 0, x = 0; i < nClasses - 1; i++) {
					for (int j = i + 1; j < nClasses; j++) {
						if (i != j) {
							train_sets[x] = new myDataset(train, i, j);
							x++;
						}
					}
				}
			}else{
				for (int i = 0; i < nClasses; i++) {
					train_sets[i] = new myDataset(train, i);
				}
			}

			int x, y;
			x = 0;
			y = 1;
			boolean is_ovo = binarization.equals("OVO");
			for (int i = 0; i < n_classifiers; i++) {
				String text = new String("");
				if (is_ovo){
					text = train.className(x)+" vs. "+train.className(y); 
				}else{
					text = train.className(i)+" vs. REST";
				}
				System.out.println("Classifier -> "+i+"; "+text);
				if (!train_sets[i].empty()) {
					Files.writeFile(cabecera+".tra", train_sets[i].printDataSet(!is_ovo));
					valid[i] = true;
					C45 tree = new C45(cabecera+".tra", pruned, confidence, instancesPerLeaf,!is_ovo);
					try {
						tree.generateTree();
					}
					catch (Exception e) {
						System.err.println("Error!!");
						System.err.println(e.getMessage());
						System.exit( -1);
					}
					String treeString = tree.printStringOVO();
					obtainRules(treeString,i);
					treeRuleSet[i].coverExamples();
				}
				else {
					valid[i] = false;
				}
				y++;
				if (y % nClasses == 0) {
					x++;
					y = x + 1;
				}
			}
			if (binarization.equals("OVO")){
				ovo.classifierTrainFinished();
			}
			//Finally we should fill the training and test output files
			ovo.clearTables(false);
			double accTr = doOutput(this.val, this.outputTr);
			ovo.clearTables(true);
			double accTst = doOutput(this.test, this.outputTst);
			System.out.println("Accuracy in training: " + accTr);
			System.out.println("Accuracy in test: " + accTst);
		}
	}
	
	  /**
	   * It executes the algorithm, but only for those instances which were ties in the
	   * previous OVO
	   * @param empate An array containing wether instances were ties or not
	   */
	  public void execute_nesting(int[] empate) {
	    if (somethingWrong) { //We do not execute the program
	      System.err.println("An error was found, the data-set has missing values.");
	      System.err.println("Aborting the program");
	      //We should not use the statement: System.exit(-1);
	    }
	    else {
	      //We do here the algorithm's operations

	      nClasses = train.getnClasses();

	      // Construct the OVO (it manages the aggregation)
	      ovo = new OVO(this, sOvo,false);

	      n_classifiers = nClasses * (nClasses - 1) / 2;
	      valid = new boolean[n_classifiers];
	      treeRuleSet = new RuleBase[n_classifiers];
	      claseMayoritaria = new String[n_classifiers];

	      train_sets = new myDataset[n_classifiers];

	      /* Compute a priori class distributions */
	      aprioriClassDistribution = new double[nClasses];
	      for (int i = 0; i < nClasses; i++) {
	        aprioriClassDistribution[i] = 1.0 * train.numberInstances(i) /
	            train.size();
	      }

	      /* Construct the data-set for each classifier only considering the ties
	       */
	      for (int i = 0, x = 0; i < nClasses - 1; i++) {
	        for (int j = i + 1; j < nClasses; j++) {
	          if (i != j) {
	            train_sets[x] = new myDataset(train, i, j, empate);
	            x++;
	          }
	        }
	      }

	      /* Construct the classifiers */
	      int x, y;
	      x = 0;
	      y = 1;
	      for (int i = 0; i < n_classifiers; i++) {
	        if (!train_sets[i].empty()) {
	          Fichero.escribeFichero("training.txt", train_sets[i].printDataSet(false));
	          valid[i] = true;
	          System.out.println("Training classifier[" + i + "] for classes " + x +
	                             " and " + y);

	          C45 tree = new C45("training.txt", pruned, confidence,instancesPerLeaf,false);
	          try {
	            tree.generateTree();
	          }
	          catch (Exception e) {
	            System.err.println("Error!!");
	            System.err.println(e.getMessage());
	            System.exit( -1);
	          }
	          String treeString = tree.printStringOVO();
	          obtainRules(treeString, i);
	          treeRuleSet[i].coverExamples();
	          claseMayoritaria[i] = train_sets[i].mostFrequentClass();
	        }
	        else {
	          valid[i] = false;
	        }
	        y++;
	        if (y % nClasses == 0) {
	          x++;
	          y = x + 1;
	        }

	      }
	      ovo.classifierTrainFinished();
	      //Finally we should fill the training and test output files
	      this.empates = empate;
	      doOutput(this.val, this.outputTr);
	    }
	  }


	/**
	 * It extracts the rule set from a given file exported by the C4.5 classifier
	 * @param treeString the contain of the file (rule set)
	 * @param classifier classifier id of the ensemble
	 */
	private void obtainRules(String treeString, int classifier) {
		String rules = new String("");
		StringTokenizer lines = new StringTokenizer(treeString, "\n"); //read lines
		String line = lines.nextToken(); //First line @TotalNumberOfNodes X
		line = lines.nextToken(); //Second line @NumberOfLeafs Y
		//The tree starts
		Vector <String>variables = new Vector<String>();
		Vector <String>values = new Vector<String>();
		Vector <String>operators = new Vector<String>();
		int contador = 0;
		while (lines.hasMoreTokens()) {
			line = lines.nextToken();
			StringTokenizer field = new StringTokenizer(line, " \t");
			String cosa = field.nextToken(); //Possibilities: "if", "elseif", "class"
			if (cosa.compareToIgnoreCase("if") == 0) {
				field.nextToken(); //(
				variables.add(field.nextToken()); //variable name (AttX, X == position)
				operators.add(field.nextToken()); //One of three: "=", "<=", ">"
				values.add(field.nextToken()); //Value
			}
			else if (cosa.compareToIgnoreCase("elseif") == 0) {
				int dejar = Integer.parseInt(field.nextToken());
				for (int i = variables.size() - 1; i >= dejar; i--) {
					variables.remove(variables.size() - 1);
					operators.remove(operators.size() - 1);
					values.remove(values.size() - 1);
				}
				field.nextToken(); //(
				variables.add(field.nextToken()); //variable name (AttX, X == position)
				operators.add(field.nextToken()); //One of three: "=", "<=", ">"
				values.add(field.nextToken()); //Value
			}
			else { //Class --> rule generation
				field.nextToken(); // =
				contador++; //I have a new rule
				rules += "\nRULE-" + contador + ": IF ";
				int i;
				for (i = 0; i < variables.size() - 1; i++) {
					rules += (String) variables.get(i) + " " + (String) operators.get(i) +
					" " + (String) values.get(i) + " AND ";
				}
				rules += (String) variables.get(i) + " " + (String) operators.get(i) +
				" " + (String) values.get(i);
				rules += " THEN class = " + field.nextToken();
				variables.remove(variables.size() - 1);
				operators.remove(operators.size() - 1);
				values.remove(values.size() - 1);
			}
		}
		treeRuleSet[classifier] = new RuleBase(train_sets[classifier], rules);
	}  

	/**
	 * It generates the output file from a given dataset and stores it in a file
	 * @param dataset myDataset input dataset
	 * @param filename String the name of the file
	 * @return the Accuracy of the classifier
	 */
	private double doOutput(myDataset dataset, String filename) {
		String output = new String("");
		/*if (!nested)
			dataset.normalize();
		*/
		output = dataset.copyHeader(); //we insert the header in the output file
		int [] hits = new int[nClasses];
		//We write the output for each example
		for (int i = 0; i < dataset.getnData(); i++) {
			int clase = dataset.getOutputAsInteger(i);
			String actualClass = dataset.getOutputAsString(i);              
			String prediccion = this.classificationOutput(dataset.getExample(i));
			output += actualClass + " " + prediccion + "\n";
			if (actualClass.equalsIgnoreCase(prediccion)) {
				hits[clase]++;
			}
		}
		Files.writeFile(filename, output);
		double accAvg = 0;
		int numClases = 0;
		for (int i = 0; i < nClasses; i++){
			try{
				int datos = dataset.numberInstances(i);
				if (datos > 0){
					numClases++;
					double acc = (1.0*hits[i])/datos;
					System.out.print("Cl["+i+"]: "+hits[i]+"/"+datos+"("+acc+")\t");
					accAvg += acc;
				}
			}catch(Exception e){
				System.err.println("NO examples for class "+i);
			}
		}
		System.out.println("");
		accAvg /= numClases;
		return (100.0*accAvg);
	}


	/**
	 * It computes the output class for a given example
	 * @param example
	 * @return the output class from the decision matrix (OVO) or vector (OVA) 
	 */
	private String classificationOutput(double[] example) {
		/**
      Here we should include the algorithm directives to generate the
      classification output from the input example
		 */
		if (binarization.equals("OVO")){
			return ovo.computeClassScores(example);
		}else{
			return ovo.computeClassScoresOVA(example);
		}
	}

	/**
	 * It computes the output class according to the learned system 
	 * @param x first class in the classification.
         * @param y second class in the classification.
	 * @param example given example to compute its class.
	 * @return the output class of the given example.
	 */
	protected int obtainClass(int x, int y, double[] example){
		int i = 0;
		for (int i2 = 0; i2 < x; i2++)
			i += nClasses - (i2 + 1);
		i += y - x - 1;
		if (valid[i]) {
			String clase = "?";
			for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?")); j++) {
				if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
					clase = treeRuleSet[i].ruleBase.get(j).clase;
				}
			}
			return train_sets[i].numericClass(clase);
		}
		else
			return -1;
	}

    /**
     * Computes and returns the confidence vector for a given example for the classification class x vs class y. 
     * The vector is two-dimensional. The first position is the confidence for the class x and the second for the class y. 
     * @param x first class in the classification.
     * @param y second class in the classification.
     * @param example example given to compute the matrix.
     * @return OVA vector for the given example.
     */
    protected double[] obtainConfidence(int x, int y, double[] example){
		int i = 0;
		double[] salida = new double[2];
		for (int i2 = 0; i2 < x; i2++)
			i += nClasses - (i2 + 1);
		i += y - x - 1;
		double confidence = 0;
		if (valid[i]) {
			String clase = "?";
			for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?")); j++) {
				if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
					clase = treeRuleSet[i].ruleBase.get(j).clase;
					confidence = treeRuleSet[i].ruleBase.get(j).confidence();
				}
			}
			int clase_num = train_sets[i].numericClass(clase);
			if (clase_num == x){
				salida[0] = confidence;//(int)salida[0] == x ? salida[1] : 1 - salida[1];
			}else{
				salida[0] = 1-confidence;
			}
			salida[1] = 1-salida[0];
			return salida;
		}
		else
		{
			salida[0] = 0.0;
			salida[1] = 0.0;
			return salida;
		}
	}

    /**
     * Computes and returns the one-vs-one matrix for a given example. 
     * In this matrix, the value table[x][y] represents the confidence of the fact that the example given belong to the class x in the classification x vs y.
     * @param example example given to compute the matrix.
     * @return OVO matrix for the given example.
     */
    protected double[][] ovo_table(double[] example)
	{
		double[][] tabla = new double[nClasses][nClasses];
		int x, y;
		x = 0;
		y = 1;
		for (int i = 0; i < n_classifiers; i++) {
			if (valid[i]) {
				String clase = "?";
				double confidence = 0;
				for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?")); j++) {
					if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
						clase = treeRuleSet[i].ruleBase.get(j).clase;
						confidence = treeRuleSet[i].ruleBase.get(j).confidence();
					}
				}
				int clase_num = train_sets[i].numericClass(clase);
				if (x == clase_num) {
					tabla[x][y] = confidence;
					tabla[y][x] = 1 - confidence;
				}
				else {//if (y == clase_num[0]){
					tabla[y][x] = confidence;
					tabla[x][y] = 1 - confidence;
				}
			}
			else {
				tabla[x][y] = tabla[y][x] = 0;
			}
			y++;
			if (y % nClasses == 0) {
				x++;
				y = x + 1;
			}
		}
		return tabla;
	}

    /**
     * Computes and returns the one-vs-all vector for a given example. 
     * In this vector, the value table[x] represents the confidence of the fact that the example given belong to the class x in the classification x vs the rest.
     * @param example example given to compute the matrix.
     * @return OVA vector for the given example.
     */
    protected double [] ova_table(double [] example){
		double[] grado_asoc = new double[this.n_classifiers];
		for (int i = 0; i < this.n_classifiers; i++) {
			if (valid[i]) {
				String clase = "?";
				double confidence = 0;
				for (int j = 0; (j < treeRuleSet[i].size()) && (clase.equals("?")); j++) {
					if (treeRuleSet[i].ruleBase.get(j).covers(example)) {
						clase = treeRuleSet[i].ruleBase.get(j).clase;
						confidence = treeRuleSet[i].ruleBase.get(j).confidence();
					}
				}
				int clase_num = train_sets[i].numericClass(clase);
				grado_asoc[i] = 0.0;
				if (clase_num == 0){ 
					grado_asoc[i] = confidence;
				}
			}
		}	
		return grado_asoc;
	}
}
