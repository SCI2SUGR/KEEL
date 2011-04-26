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

import itk.exeura.learner.engine.basic.Category;
import itk.exeura.learner.engine.basic.Configuration;
import itk.exeura.learner.engine.basic.DiscriminativeTerm;
import itk.exeura.learner.engine.basic.DocumentSet;
import itk.exeura.learner.engine.basic.TermConjunction;
import itk.exeura.learner.engine.control.DTSelectionLoop;
import itk.exeura.learner.engine.exception.RunExperimentException;
import itk.exeura.learner.engine.optimization.ExperimentConfiguration;
import itk.exeura.learner.engine.termSelection.VocabularyReducer;
import itk.exeura.learner.wrapper.core.OlexGAparameters;
import itk.exeura.learner.wrapper.core.SFManager;
import itk.exeura.learner.wrapper.core.ScoringFunctionTermsComparator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * 
 * @author Adriana Pietramala
 * 
 */
public class WrapperManager {

	private static HashMap<String, HashMap<String, Boolean>> catPosTerms = null;

	private static HashMap<String, HashMap<String, Boolean>> catNegTerms = null;

	private static List<Integer> trainingSetDocumentsID = null;

	private static Vector<Category> categories = null;

	public static Vector<Category> getCategories() {
		return categories;
	}

	public static void setCategories(Vector<Category> categories) {
		WrapperManager.categories = categories;
	}

	public static List<Integer> getTrainingSetDocumentsID() {
		return trainingSetDocumentsID;
	}

	public static void setTrainingSetDocumentsID(
			List<Integer> trainingSetDocumentsID) {
		WrapperManager.trainingSetDocumentsID = trainingSetDocumentsID;
	}

	public static Hashtable<String, Vector<Integer>> computesTCs(
			Dataset trainingSet) {

		OlexGA_Attribute classAttribute = trainingSet.getClassAttribute();

		Hashtable<String, Vector<Integer>> TCs = new Hashtable<String, Vector<Integer>>();
		trainingSetDocumentsID = new LinkedList<Integer>();

		for (int i = 0; i < trainingSet.numItemsets(); i++) {
			// memorizza l'istanza corrente
			Itemset inst = trainingSet.itemset(i);
			// memorizza quanto vale l'indice della classe per questa
			// istanza
			// System.out.println("Controlla --> double, int che è?????");
			int classAttrV = (int) inst.getClassValue(); // inst.value(classAttribute);
			// aggiungo l'istanza al training set
			trainingSetDocumentsID.add(i);

			Vector<Integer> classAttributeDocuments = null;
			if ((classAttributeDocuments = TCs.get(classAttribute
					.value(classAttrV))) == null) {
				classAttributeDocuments = new Vector<Integer>();
				TCs.put(classAttribute.value(classAttrV),
						classAttributeDocuments);
			}
			// aggiungo il documento al vettore
			classAttributeDocuments.add(i);

		}
		return TCs;
	}

	public static List<Configuration> doLearning(Dataset trainingSet)
			throws Exception {
		catPosTerms = null;
		catNegTerms = null;

		// Step1: builds the set of categories
		OlexGA_Attribute classAttribute = trainingSet.getClassAttribute();
		categories = new Vector<Category>();
		for (int i = 0; i < classAttribute.numValues(); i++) {
			String className = classAttribute.value(i);
			Category cat = new Category();
			cat.setCategoryName(className);
			cat.setCategoryNumber(i);
			categories.add(cat);
		}

		// Step2: builds TCs <category, set of category documents>
		Hashtable<String, Vector<Integer>> TCs = WrapperManager
				.computesTCs(trainingSet);
		setLearnedCategory(trainingSet, TCs);
		Hashtable<String, Vector<Integer>> ACs = new Hashtable<String, Vector<Integer>>();

		// Step3: builds the vocabulary of each category
		VocabularyReducer.vocabularies = WrapperManager.computesVocabulary(
				trainingSet, OlexGAparameters.SCORING_FUNCTION,
				OlexGAparameters.POSITIVE_TERMS_SIZE,
				OlexGAparameters.LEARNED_CLASS_VALUE_INDEX);
		
		

		ExperimentConfiguration expConf = new ExperimentConfiguration(
				WrapperManager.getTrainingSetDocumentsID(), TCs, ACs);
		// Stores the learning results
		List<Configuration> configurations = null;

		// Step4: esecuzione fase di learning
		try {
			configurations = DTSelectionLoop.loop(expConf, WrapperManager
					.getCategories(), OlexGAparameters.FMEASURE,
					OlexGAparameters.MAX_CONJ_LENGHT,
					OlexGAparameters.INITIALIZATION_TYPE,
					OlexGAparameters.SELECTION_ALGORITHM,
					OlexGAparameters.NEGATIVE_TERMS_SIZE,
					OlexGAparameters.POP_SIZE, OlexGAparameters.GENERATIONS,
					OlexGAparameters.ATTEMPTS, OlexGAparameters.XOVER_METHOD,
					OlexGAparameters.LEARNED_CLASS_VALUE_INDEX);
		} catch (RunExperimentException e) {
			e.printStackTrace();
		}

		VocabularyReducer.vocabularies = null;

		return configurations;
	}

	private static void setLearnedCategory(Dataset inst, 
			Hashtable<String, Vector<Integer>> tCs) {
		String learningFor = "";
		int min = Integer.MAX_VALUE;
		for (Iterator<String> it = tCs.keySet().iterator(); it.hasNext(); ) {
			String cat = it.next();
			if (tCs.get(cat).size() < min) {
				learningFor = cat;
				min = tCs.get(cat).size();
			}
		}
		OlexGAparameters.LEARNED_CLASS_VALUE_INDEX = 1;//inst.getClassAttribute().valueIndex(learningFor);
		System.out.println("Learning for category: INDEX " + OlexGAparameters.LEARNED_CLASS_VALUE_INDEX + " LABEL " + learningFor);
	}

	public static long timeConverter(long startTime, long stopTime) {
		return Math.round(((double) (stopTime - startTime)) / 1000);
	}

	public static int computesComplementaryIndex(int classForIRStatistics) {
		assert (categories.size() == 2);

		return (classForIRStatistics == 1 ? 0 : 1);
	}

	public static Hashtable<String, List<DiscriminativeTerm>> computesVocabulary(
			Dataset inst, int functionType, int numTerms,
			int classForIRStatistics) {

		Hashtable<String, List<DiscriminativeTerm>> vocabularies = new Hashtable<String, List<DiscriminativeTerm>>();

		Hashtable<String, List<Integer>> termsDocumentSet = new Hashtable<String, List<Integer>>();
		// memorizza l'attributo di classe
		OlexGA_Attribute classAttribute = inst.getClassAttribute();
		// costruisce il termsDocumentSet
		vocabularies = computesTermsDocuments(inst, functionType,
				classAttribute, termsDocumentSet, classForIRStatistics,
				computesComplementaryIndex(classForIRStatistics));

		return vocabularies;
	}

	private static Hashtable<String, List<DiscriminativeTerm>> computesTermsDocuments(
			Dataset dataset, int functionType, OlexGA_Attribute classAttribute,
			Hashtable<String, List<Integer>> termsDocumentSet,
			int classForIRStatistics, int complementClassForIRStatistics) {
		Hashtable<String, List<DiscriminativeTerm>> vocabularies = new Hashtable<String, List<DiscriminativeTerm>>();

		// preparo la struttura dati che memorizza il vocabolario
		for (int i = 0; i < classAttribute.numValues(); i++) {
			String categoryName = classAttribute.value(i);
			vocabularies
					.put(categoryName, new LinkedList<DiscriminativeTerm>());
		}

		for (int i = 0; i < dataset.numAttributes(); i++) {
			OlexGA_Attribute at = dataset.getAttribute(i);

			if (at.equals(classAttribute))
				continue;

			int A = 0;
			int B = 0;
			int C = 0;
			int D = 0;

			DiscriminativeTerm dt = new DiscriminativeTerm(at.name(), i + 1);
			dt.setDocumentSet(new DocumentSet(dataset.numItemsets()));

			for (int j = 0; j < dataset.numItemsets(); j++) {
				Itemset inst = dataset.itemset(j);
				double termAttribute = inst.getValue(i);
				int classAttrValue = ((int) inst.getClassValue());

				// salta l'istanza se l'attributo è missing
				if (inst.isMissing(i)) {
					continue;
				}

				// sto considerando una istanza della categoria di classe
				if (classAttrValue == classForIRStatistics) {
					// il termine appare nella istanza della categoria di
					// classe
					if (termAttribute > 0.0) { //was != 0
						A++;
						dt.getDocumentSet().addElement(j);
					} else
						C++;
				}
				// sto considerando un'istanza che non appartiene alla
				// categoria di classe
				else {
					// il termine appare nell'istanza complementare
					if (termAttribute > 0.0) { //WAS !=0
						B++;
						dt.getDocumentSet().addElement(j);
					} else
						D++;
				}
			}
			if (A != 0) {
				dt.setScoreValue(SFManager.computesFunctionValue(A, B, C, D, (A
						+ B + C + D), functionType));
				List<DiscriminativeTerm> terms1 = null;
				if ((terms1 = vocabularies.get(classAttribute
						.value(classForIRStatistics))) != null) {
					terms1.add(dt);
				} else {
					System.err.println("A computation: Empty list of terms");
				}
			}

			if (B != 0) {
				List<DiscriminativeTerm> terms1 = null;
				if ((terms1 = vocabularies.get(classAttribute
						.value(complementClassForIRStatistics))) != null) {
					terms1.add(dt);
				} else {
					System.err.println("B computation: Empty list of terms");
				}
			}
		}
		List<DiscriminativeTerm> terms = vocabularies.get(classAttribute
				.value(classForIRStatistics));
		Collections.sort(terms, new ScoringFunctionTermsComparator());

		return vocabularies;
	}

	public static double doValidation(Itemset inst, Configuration conf,
			String className) {

		double belongs = 0;

		if ((catPosTerms == null) && (catNegTerms == null)) {
			catPosTerms = new HashMap<String, HashMap<String, Boolean>>();
			catNegTerms = new HashMap<String, HashMap<String, Boolean>>();
		}

		HashMap<String, Boolean> pTerms = catPosTerms.get(className);
		// Only for fast access
		if (pTerms == null) {
			pTerms = buildsTemporaryDTermsHashMap(conf.getPositiveTerms());
			catPosTerms.put(className, pTerms);
		}

		HashMap<String, Boolean> nTerms = catNegTerms.get(className);
		// Only for fast access
		if (nTerms == null) {
			nTerms = buildsTemporaryDTermsHashMap(conf.getNegativeTerms());
			catNegTerms.put(className, nTerms);
		}

		int i = 0;
		for (; i < inst.values.length; i++) {
			OlexGA_Attribute at = inst.getAttribute(i);
			// L'attributo è presente nell'istanza corrente
			if (inst.getValue(i) != 0.0) {
				// il nome dell'attributo
				String attributeName = at.name();
				// verifico che l'attributo sia un termine positivo
				if (pTerms.get(attributeName) != null)
					// l'istanza corrente contiene ALMENO un termine positivo
					break;
			}
		}

		// l'istanza corrente NON contiene nemmeno un termine positivo
		// non può essere classificata come appartenente alla categoria
		if (i == inst.values.length) {
			return belongs;
		}

		// l'istanza corrente contiene almeno un termine positivo
		int j = 0;
		for (; j < inst.values.length; j++) {
			OlexGA_Attribute at = inst.getAttribute(j);
			// Verifico che l'attributo faccia parte dell'istanza in esame
			if (inst.getValue(j) != 0.0) {
				String attributeName = at.name();
				// l'attributo corrente è un termine negativo
				if (nTerms.get(attributeName) != null) {
					break;
				}
			}
		}
		// ho scandito tutti gli attributi dell'istanza
		// e nessuno di questi è un termine negativo
		if (j == inst.values.length) {
			belongs = 1;
		}
		return belongs;
	}

	// public static double[] doValidation(Instance inst, List<Configuration>
	// configurations) {
	// return doValidationOriginal(inst, configurations);
	// }
	//
	// public static double[] doValidationOriginal(Instance inst,
	// List<Configuration> configurations) {
	// double[] belongs = new double[inst.numClasses()];
	// for (int i = 0; i < configurations.size(); i++) {
	//
	// Configuration conf = configurations.get(i);
	// if (conf.getPositiveTerms().size() != 0) {
	// belongs[i] = doValidation(inst, conf,
	// conf.getCategory().getCategoryName());
	// }
	// }
	//
	// if (OlexGAparameters.LEARNED_CLASS_VALUE_INDEX == -1) {
	// return belongs;
	// }
	//
	// if (belongs[OlexGAparameters.LEARNED_CLASS_VALUE_INDEX] == 0.0) {
	// belongs[computesComplementaryIndex(OlexGAparameters.LEARNED_CLASS_VALUE_INDEX)]
	// = 1.0;
	//
	// }
	// return belongs;
	// }

	private static HashMap<String, Boolean> buildsTemporaryDTermsHashMap(
			Vector<TermConjunction> posTerms) {
		HashMap<String, Boolean> pTerms = new HashMap<String, Boolean>();
		Iterator<TermConjunction> posTIter = posTerms.iterator();
		while (posTIter.hasNext()) {
			TermConjunction tc = posTIter.next();
			Set<DiscriminativeTerm> discrTerm = tc.getTerms();
			Iterator<DiscriminativeTerm> discrTermIterator = discrTerm
					.iterator();
			while (discrTermIterator.hasNext()) {
				DiscriminativeTerm dt = discrTermIterator.next();
				String clearDTName = dt.getTermValue().replace("\"", "");
				pTerms.put(clearDTName, true);
			}
		}

		return pTerms;
	}
}
