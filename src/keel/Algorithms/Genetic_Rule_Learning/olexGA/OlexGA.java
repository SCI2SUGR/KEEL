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

package keel.Algorithms.Genetic_Rule_Learning.olexGA;

import itk.exeura.learner.engine.basic.Configuration;
import itk.exeura.learner.engine.geneticAlgorithm.GARepAlgoTypes;
import itk.exeura.learner.engine.geneticAlgorithm.GASelectionAlgoTypes;
import itk.exeura.learner.engine.geneticAlgorithm.LearnerParameterSet;
import itk.exeura.learner.wrapper.core.OlexGAparameters;
import itk.exeura.learner.wrapper.core.SFManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.core.Files;


/**
 * 
 * @author Adriana Pietramala
 * 
 */
public class OlexGA {

	private static final long serialVersionUID = 8647630661819994437L;

	/***********************************************/
	/*** START LIST OF AVAILABLE OLEX-GA OPTIONS ***/
	/***********************************************/
	public static final String SCORING_FUNCTION = "scoringFunction";

	public static final String POSITIVE_TERMS_SIZE = "numOfFeatures";

	public static final String XOVER_METHOD = "Xover";

	public static final String XOVER_RATE = "XOverRate";

	public static final String MUTATION_RATE = "mutationRate";

	public static final String SELECTION_ALGO = "selectionAlgorithm";

	public static final String ELITISM_PROPORTION = "elitismRate";

	public static final String POPULATION_SIZE = "populationSize";

	public static final String GENERATIONS = "numOfGenerations";

	public static final String ATTEMPTS = "numOfRuns";

	public static final String LEARNED_CATEGORY_INDEX = "classIndex";

	/***********************************************/
	/**** END LIST OF AVAILABLE OLEX-GA OPTIONS ****/
	/***********************************************/

	private final int numOfOptions = 11;

	private List<Configuration> configurations = null;

	protected OlexGAparameters olexGAParams = null;

	protected Dataset m_Instances = null;

	ParametersParser pp = null; // new ParametersParser();

	/**
	 * 
	 */
	public OlexGA() {
		String dummy = "";
		pp = new ParametersParser();
		try {
			// setOptions(dummy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public OlexGA(String configurationFile) {
		this();
		try {
			this.setOptions(configurationFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Builds the model
	 * 
	 * @param inputFile Instances
	 * 
	 */
	public void buildClassifier(String inputFile) throws Exception {

		this.m_Instances = new Dataset(inputFile, true);
		if (DatasetChecker.testWithFail(m_Instances))
			configurations = WrapperManager.doLearning(m_Instances);
		else
			throw new Exception("Dataset Check failed");
	}

	public void setOptions(String configurationFile) throws Exception {
		String tmpStr;

		pp.parseConfigurationFile(configurationFile);

		// Scoring function
		setScoringFunction(pp.getParameterValue(SCORING_FUNCTION));

		// Crossover method
		setXOver(pp.getParameterValue(XOVER_METHOD));

		// Positive terms size
		setNumOfFeatures(pp.getParameterValue(POSITIVE_TERMS_SIZE));

		// Crossover rate
		setXOverRate(pp.getParameterValue(XOVER_RATE));

		// Mutation rate
		setMutationRate(pp.getParameterValue(MUTATION_RATE));

		// Selection algorithm
		setSelectionAlgorithm(pp.getParameterValue(SELECTION_ALGO));

		// Population size
		setPopulationSize(pp.getParameterValue(POPULATION_SIZE));

		// Generations
		setNumOfGenerations(pp.getParameterValue(GENERATIONS));

		// Attempts
		setNumOfRuns(pp.getParameterValue(ATTEMPTS));

		// Elite proportion
		setElitismRate(pp.getParameterValue(ELITISM_PROPORTION));

//		setClassIndex(pp.getParameterValue(LEARNED_CATEGORY_INDEX));

	}

	public String[] getOptions() {
		String[] options = new String[2 * numOfOptions];

		int current = 0;
		options[current++] = "-" + SCORING_FUNCTION;
		options[current++] = "" + OlexGAparameters.SCORING_FUNCTION;
		options[current++] = "-" + POSITIVE_TERMS_SIZE;
		options[current++] = "" + OlexGAparameters.POSITIVE_TERMS_SIZE;
		options[current++] = "-" + XOVER_METHOD;
		options[current++] = "" + OlexGAparameters.XOVER_METHOD;
		options[current++] = "-" + XOVER_RATE;
		options[current++] = "" + OlexGAparameters.XOVER_RATE;
		options[current++] = "-" + MUTATION_RATE;
		options[current++] = "" + OlexGAparameters.MUTATION_RATE;
		options[current++] = "-" + SELECTION_ALGO;
		options[current++] = "" + OlexGAparameters.SELECTION_ALGORITHM;
		options[current++] = "-" + POPULATION_SIZE;
		options[current++] = "" + OlexGAparameters.POP_SIZE;
		options[current++] = "-" + GENERATIONS;
		options[current++] = "" + OlexGAparameters.GENERATIONS;
		options[current++] = "-" + ATTEMPTS;
		options[current++] = "" + OlexGAparameters.ATTEMPTS;
		options[current++] = "-" + ELITISM_PROPORTION;
		options[current++] = "" + OlexGAparameters.ELITE_PROPORTION;
		options[current++] = "-" + LEARNED_CATEGORY_INDEX;
		options[current++] = "" + OlexGAparameters.LEARNED_CLASS_VALUE_INDEX;

		while (current < options.length) {
			options[current++] = "";
		}

		return options;
	}

	/**
	 * Returns an instance of a TechnicalInformation object, containing detailed
	 * information about the technical background of this class, e.g., paper
	 * reference or book this class is based on.
	 * 
	 * @return the technical information about this class
	 */
	public String getTechnicalInformation() {
		String result;

		result = new String("REFERENCES:");
		result += "TITLE: A Genetic Algorithm for Text Classification Rule Induction";
		result += "\nAUTHORS: A. Pietramala, Veronica L. Policicchio, P. Rullo, I. Sidhu";
		result += "\nIN PROCEEDINGS of "
				+ "LNAI - Machine Learning and Knowledge Discovery in Databases - Part II";
		result += "\nYEAR: 2008";
		result += "\nPAGES: 188-203";
		result += "WEB SITE:\n" + "http://www.unical.it/Olex-GA/";
		return result;
	}

	public String toString() {
		return getTechnicalInformation();
	}

	public void setNumOfFeatures(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int num;
			try {
				num = Integer.parseInt(tmpStr);
			} catch (NumberFormatException e) {
				System.out
						.println("ga reprod type not found --> using 50 instead");
				num = 50;
			}

			OlexGAparameters.POSITIVE_TERMS_SIZE = num;
		}
	}

	public int getNumOfFeatures() {
		return OlexGAparameters.POSITIVE_TERMS_SIZE;
	}

	public void setScoringFunction(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int id = SFManager.getScoringFunctionInternalIndex(tmpStr);
			if (id == -1) {
				id = 0;
			}
			OlexGAparameters.SCORING_FUNCTION = id;
		}
	}

	public String getScoringFunction() {
		return SFManager.getScoringFunctions()[OlexGAparameters.SCORING_FUNCTION];
	}

	public void setXOver(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int id = GARepAlgoTypes.getXoverIndex(tmpStr);
			if (id == -1) {
				id = 0;
				System.out.println("ga reprod type not found --> using " + id
						+ " instead");

			}
			OlexGAparameters.XOVER_METHOD = id;
		}
	}

	public String getXOver() {
		return GARepAlgoTypes.getRepAlgoritms()[OlexGAparameters.XOVER_METHOD];
	}

	public void setSelectionAlgorithm(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int id = GASelectionAlgoTypes.getSelectAlgoIndex(tmpStr);
			if (id == -1) {
				System.out.println("ga selction type not found --> using 0");
				id = 0;
			}
			OlexGAparameters.SELECTION_ALGORITHM = id;
		}

	}

	public String getSelectionAlgorithm() {
		return GASelectionAlgoTypes.getSelectionAlgorithmTypes()[OlexGAparameters.SELECTION_ALGORITHM];
	}

//	public void setClassIndex(String tmpStr) {
//		if (tmpStr != null && tmpStr != "") {
//			int id = Integer.parseInt(tmpStr);
//			if (id == -1) {
//				id = 1;
//				System.out.println("ga reprod type not found --> using " + id
//						+ " instead");
//
//			}
//			OlexGAparameters.LEARNED_CLASS_VALUE_INDEX = id;
//		}
//
//	}
//
//	public int getClassIndex() {
//		return OlexGAparameters.LEARNED_CLASS_VALUE_INDEX;
//	}

	public void setXOverRate(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			double num;
			try {
				num = Double.parseDouble(tmpStr);
			} catch (NumberFormatException e) {
				System.out
						.println("ga xover rate not found --> using 1.0 instead");
				num = 1.0;
			}
			OlexGAparameters.XOVER_RATE = num;
		}
		LearnerParameterSet.xOverRate = OlexGAparameters.XOVER_RATE;
	}

	public double getXOverRate() {
		return OlexGAparameters.XOVER_RATE;
	}

	public void setMutationRate(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			double num;
			try {
				num = Double.parseDouble(tmpStr);
			} catch (NumberFormatException e) {
				num = 1.0;
				System.out.println("ga xover rate not found --> using " + num
						+ " instead");

			}
			OlexGAparameters.MUTATION_RATE = num;
		}
		LearnerParameterSet.mutationRate = OlexGAparameters.MUTATION_RATE;

	}

	public double getMutationRate() {
		return OlexGAparameters.MUTATION_RATE;
	}

	public void setElitismRate(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			double num;
			try {
				num = Double.parseDouble(tmpStr);
			} catch (NumberFormatException e) {
				num = 0.001;
				System.out.println("ga elitism rate not found --> using " + num
						+ " instead");

			}
			OlexGAparameters.ELITE_PROPORTION = num;
		}
	}

	public double getElitismRate() {
		return OlexGAparameters.ELITE_PROPORTION;
	}

	public void setPopulationSize(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int num;
			try {
				num = Integer.parseInt(tmpStr);
			} catch (NumberFormatException e) {
				num = 500;
				System.out.println("ga pop size not found --> using " + num
						+ " instead");

			}
			OlexGAparameters.POP_SIZE = num;
		}
	}

	public int getPopulationSize() {
		return OlexGAparameters.POP_SIZE;
	}

	public void setNumOfGenerations(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int num;
			try {
				num = Integer.parseInt(tmpStr);
			} catch (NumberFormatException e) {
				num = 200;
				System.out.println("ga pop size not found --> using " + num
						+ " instead");

			}
			OlexGAparameters.GENERATIONS = num;
		}
	}

	public int getNumOfGenerations() {
		return OlexGAparameters.GENERATIONS;
	}

	public void setNumOfRuns(String tmpStr) {
		if (tmpStr != null && tmpStr != "") {
			int num;
			try {
				num = Integer.parseInt(tmpStr);
			} catch (NumberFormatException e) {
				num = 1;
				System.out.println("ga pop size not found --> using " + num
						+ " instead");

			}
			OlexGAparameters.ATTEMPTS = num;
		}
	}

	public int getNumOfRuns() {
		return OlexGAparameters.ATTEMPTS;
	}

	public List<OlexResult>/* HashMap<String, String> *//* String */classify(
			Dataset dataset) {

		List<OlexResult> res = new LinkedList<OlexResult>();

		// HashMap<String, String> classRes = new HashMap<String, String>();
		StringBuffer classRes = new StringBuffer();

		OlexGA_Attribute classAt = dataset.getClassAttribute();

		String category = dataset.getClassAttribute().value(
				OlexGAparameters.LEARNED_CLASS_VALUE_INDEX);
		int complIndex = WrapperManager
				.computesComplementaryIndex(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX);
		String complementary = dataset.getClassAttribute().value(complIndex);

		for (int j = 0; j < dataset.numItemsets(); j++) {
			Itemset inst = dataset.itemset(j);
			String classAs = "";
			double d = WrapperManager.doValidation(inst, configurations
					.get(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX), category);
			if (d != 0.0) {
				classAs = category;

			} else {
				classAs = complementary;
			}
			OlexResult n = new OlexResult(category, classAt.value((int) inst
					.getClassValue()), classAs);
			res.add(n);

			// classRes.put(classAt.value((int) inst.getClassValue()), classAs);
			classRes.append(classAt.value((int) inst.getClassValue()) + "\t"
					+ classAs + "\n");
		}

		// return classRes;
		// return classRes.toString();
		return res;
	}

	public String populationSizeTipText() {
		return "Number of chromosomes or individuals";
	}

	public String xOverRateTipText() {
		return "The chance that two chromosomes will swap their bits.";
	}

	public String xOverMethodTipText() {
		return "The method used for individuals reproduction.";
	}

	public String elitismProportionTipText() {
		return "The percentage of the best chromosomes to be copied into the new population.";
	}

	public String generationsTipText() {
		return "Number of times the process of going from the current population to the next on is executed.";
	}

	public String attemptsTipText() {
		return "Number of times the genetic algorithm is executed.";
	}

	public String selectionAlgorithmTipText() {
		return "The method of choosing members from the population of chromosomes.";
	}

	public String scoringFunctionTipText() {
		return "If featureSelection is FALSE, the scoring function that will be used to perform feature selection."
				+ "If featureSelection is TRUE, the scoring function that you have used to perform feature selection.";
	}

	public String initializationTypeTipText() {
		return "The method used to initialize the population.";
	}

	public String performFeatureSelectionTipText() {
		return "TRUE if you already perform feature selection, FALSE otherwise.";
	}

	public String mutationRateTipText() {
		return "The chance that a bit within a chromosome will be flipped.";
	}

	public String maxPositiveTermsSizeTipText() {
		return "The max size of the subset of terms of the given vocabulary used as candidate positive.";
	}

	private String computeMeasures(List<OlexResult> trainClass) {

		int TP = 0, TN = 0, FN = 0, FP = 0;
		double P, R, F;
		StringBuffer string = new StringBuffer();
		for (Iterator iterator = trainClass.iterator(); iterator.hasNext();) {
			OlexResult olexResult = (OlexResult) iterator.next();
			String expected = olexResult.getExpected();
			String predicted = olexResult.getPredicted();
			String classLearned = olexResult.getClassLearned();

			if (predicted.equalsIgnoreCase(classLearned)) {
				if (predicted.equalsIgnoreCase(expected)) {
					TP++;
				} else {
					FP++;
				}
			} else {
				if (predicted.equalsIgnoreCase(expected)) {
					TN++;
				} else {
					FN++;
				}
			}
		}

		if (TP == 0) {
			P = R = F = 0;
		} else {
			P = (double) TP / (TP + FP);
			R = (double) TP / (TP + FN);
			F = 2 * P * R / (P + R);
		}
		string.append("TP=" + TP + "\n");
		string.append("FP=" + FP + "\n");
		string.append("FN=" + FN + "\n");
		string.append("TN=" + TN + "\n");

		string.append("P=" + P + "\n");
		string.append("R=" + R + "\n");
		string.append("F=" + F + "\n");

		return string.toString();
	}

	public static void main(String args[]) throws Exception {
		System.out.println("ciaoooo o");
		String configurationFile = args[0].substring(args[0].lastIndexOf("./"));
		System.out.println("conf file " + configurationFile);
		OlexGA olex = new OlexGA(configurationFile);

		try {
			olex.buildClassifier(olex.pp.getTrainingInputFile());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Learning finito...ora bisogna classificare");

		Dataset dataset = new Dataset(olex.pp.getTrainingInputFile(), false);
		List<OlexResult> trainClass = olex.classify(dataset);

		String string = dataset.copyHeader() + olex.toString(trainClass);
		Configuration configuration = olex.configurations
				.get(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX);
		String details = "***** Measures on Training Set ***** \nF-measure: "
				+ configuration.getFmeasure();
		String additionalInfo = olex.computeMeasures(trainClass);
		String info = details + "\n" + additionalInfo;
		info += "\nLEARN TIME: " + configuration.getLearning_time() + " sec.";
		Files.writeFile(olex.pp.getTrainingOutputFile(), string);
		dataset = new Dataset(olex.pp.getTestInputFile(), false);
		long startClassificationtime = System.currentTimeMillis();
		List<OlexResult> testClass = olex.classify(dataset);
		long final_ct = (long) Math
				.ceil(((double) System.currentTimeMillis() - startClassificationtime) / 1000);

		info += "\n***** Measures on Test Set ***** \n"
				+ olex.computeMeasures(testClass);
		info += "\nVALIDATION TIME: " + final_ct + " sec.";
		String string2 = dataset.copyHeader() + olex.toString(testClass);
		Files.writeFile(olex.pp.getTestOutputFile(), string2);
		System.out.println("scrittura file di output");
		Files.writeFile(olex.pp.getOutputFile(0), info);
	}

	
	public static void testClassification(String testfile, Configuration conf) throws Exception {
		System.out.println("ciaoooo o");
		OlexGA olex = new OlexGA();		
		Dataset dataset = new Dataset(testfile, false);
		long startClassificationtime = System.currentTimeMillis();
		List<OlexResult> testClass = olex.classify(dataset);
		long final_ct = (long) Math
				.ceil(((double) System.currentTimeMillis() - startClassificationtime) / 1000);

		String info = "\n***** Measures on Test Set ***** \n"
				+ olex.computeMeasures(testClass);
		info += "\nVALIDATION TIME: " + final_ct + " sec.";
//		String string2 = dataset.copyHeader() + olex.toString(testClass);
//		Files.writeFile(olex.pp.getTestOutputFile(), string2);
//		System.out.println("scrittura file di output");
//		Files.writeFile(olex.pp.getOutputFile(0), info);
		System.out.println(info);
	}

	
	private String toString(List<OlexResult> trainClass) {

		StringBuffer string = new StringBuffer();
		// string.append("EXPECTED " + "\t" + "PREDICTED" + "\n");
		for (Iterator iterator = trainClass.iterator(); iterator.hasNext();) {
			OlexResult olexResult = (OlexResult) iterator.next();
			String expected = olexResult.getExpected();
			String predicted = olexResult.getPredicted();
			string.append(expected + " " + predicted + "\n");
		}
		return string.toString();
	}

}
