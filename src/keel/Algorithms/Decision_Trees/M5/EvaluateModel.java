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
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.M5;

import java.util.*;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Class for evaluating machine learning models. <p>
 */
public class EvaluateModel {

    /** The number of classes. */
    private int m_NumClasses;

    /** The number of folds for a cross-validation. */
    private int m_NumFolds;

    /** The weight of all incorrectly classified instances. */
    private double m_Incorrect;

    /** The weight of all correctly classified instances. */
    private double m_Correct;

    /** The weight of all unclassified instances. */
    private double m_Unclassified;

    /*** The weight of all instances that had no class assigned to them. */
    private double m_MissingClass;

    /** The weight of all instances that had a class assigned to them. */
    private double m_WithClass;

    /** Array for storing the confusion matrix. */
    private double[][] m_ConfusionMatrix;

    /** The names of the classes. */
    private String[] m_ClassNames;

    /** Is the class nominal or numeric? */
    private boolean m_ClassIsNominal;

    /** The prior probabilities of the classes */
    private double[] m_ClassPriors;

    /** The sum of counts for priors */
    private double m_ClassPriorsSum;

    /** The total cost of predictions (includes instance weights) */
    private double m_TotalCost;

    /** Sum of errors. */
    private double m_SumErr;

    /** Sum of absolute errors. */
    private double m_SumAbsErr;

    /** Sum of squared errors. */
    private double m_SumSqrErr;

    /** Sum of class values. */
    private double m_SumClass;

    /** Sum of squared class values. */
    private double m_SumSqrClass;

    /*** Sum of predicted values. */
    private double m_SumPredicted;

    /** Sum of squared predicted values. */
    private double m_SumSqrPredicted;

    /** Sum of predicted * class values. */
    private double m_SumClassPredicted;

    /** Sum of absolute errors of the prior */
    private double m_SumPriorAbsErr;

    /** Sum of absolute errors of the prior */
    private double m_SumPriorSqrErr;

    /** Total Kononenko & Bratko Information */
    private double m_SumKBInfo;

    /*** Resolution of the margin histogram */
    private static int k_MarginResolution = 500;

    /** Cumulative margin distribution */
    private double m_MarginCounts[];

    /** Number of non-missing class training instances seen */
    private int m_NumTrainClassVals;

    /** Array containing all numeric training class values seen */
    private double[] m_TrainClassVals;

    /** Array containing all numeric training class weights */
    private double[] m_TrainClassWeights;

    /** Numeric class error estimator for prior */
    private M5Kernel m_PriorErrorEstimator;

    /** Numeric class error estimator for scheme */
    private M5Kernel m_ErrorEstimator;

    /**
     * The minimum probablility accepted from an estimator to avoid
     * taking log(0) in Sf calculations.
     */
    private static final double MIN_SF_PROB = Double.MIN_VALUE;

    /** Total entropy of prior predictions */
    private double m_SumPriorEntropy;

    /** Total entropy of scheme predictions */
    private double m_SumSchemeEntropy;


    /**
     * Initializes all the counters for the evaluation and also takes a
     * cost matrix as parameter.
     *
     * @param data set of instances, to get some header information
     * @exception Exception if cost matrix is not compatible with
     * data, the class is not defined or the class is numeric
     */
    public EvaluateModel(M5Instances data) throws Exception {

        m_NumClasses = data.numClasses();
        m_NumFolds = 1;
        m_ClassIsNominal = data.classAttribute().isNominal();

        if (m_ClassIsNominal) {
            m_ConfusionMatrix = new double[m_NumClasses][m_NumClasses];
            m_ClassNames = new String[m_NumClasses];
            for (int i = 0; i < m_NumClasses; i++) {
                m_ClassNames[i] = data.classAttribute().value(i);
            }
        }

        m_ClassPriors = new double[m_NumClasses];
        setPriors(data);
        m_MarginCounts = new double[k_MarginResolution + 1];
    }

    /**
     * Returns a copy of the confusion matrix.
     *
     * @return a copy of the confusion matrix as a two-dimensional array
     */
    public double[][] confusionMatrix() {

        double[][] newMatrix = new double[m_ConfusionMatrix.length][0];

        for (int i = 0; i < m_ConfusionMatrix.length; i++) {
            newMatrix[i] = new double[m_ConfusionMatrix[i].length];
            System.arraycopy(m_ConfusionMatrix[i], 0, newMatrix[i], 0,
                             m_ConfusionMatrix[i].length);
        }
        return newMatrix;
    }

    /**
     * Performs a (stratified if class is nominal) cross-validation
     * for a classifier on a set of instances.
     *
     * @param classifier the classifier with any options set.
     * @param data the data on which the cross-validation is to be
     * performed
     * @param numFolds the number of folds for the cross-validation
     * @exception Exception if a classifier could not be generated
     * successfully or the class is not defined
     */
    public void crossValidateModel(M5 classifier,
                                   M5Instances data, int numFolds) throws
            Exception {

        // Make a copy of the data we can reorder
        data = new M5Instances(data);
        if (data.classAttribute().isNominal()) {
            data.stratify(numFolds);
        }
        // Do the folds
        for (int i = 0; i < numFolds; i++) {
            M5Instances train = data.trainCV(numFolds, i);
            setPriors(train);
            classifier.buildClassifier(train);
            M5Instances test = data.testCV(numFolds, i);
            evaluateModel(classifier, test);
        }
        m_NumFolds = numFolds;
    }

    /**
     * Performs a (stratified if class is nominal) cross-validation
     * for a classifier on a set of instances.
     *
     * @param classifierString a string naming the class of the classifier
     * @param data the data on which the cross-validation is to be
     * performed
     * @param numFolds the number of folds for the cross-validation
     * @param options the options to the classifier. Any options
     * accepted by the classifier will be removed from this array.
     * @exception Exception if a classifier could not be generated
     * successfully or the class is not defined
     */
    public void crossValidateModel(String classifierString,
                                   M5Instances data, int numFolds,
                                   String[] options) throws Exception {

        crossValidateModel(M5.forName(classifierString, options),
                           data, numFolds);
    }


    public static String evaluateModel(String classifierString,
                                       String[] options) throws Exception {

        M5 classifier;

        // Create classifier
        try {
            classifier =
                    (M5) Class.forName(classifierString).newInstance();
        } catch (Exception e) {
            throw new Exception("Can't find class with name "
                                + classifierString + '.');
        }
        return evaluateModel(classifier, options);
    }

    /**
     * A test method for this class. Just extracts the first command line
     * argument as a classifier class name and calls evaluateModel.
     * @param args an array of command line arguments, the first of which
     * must be the class name of a classifier.
     */
    public static void main(String[] args) {

        try {
            if (args.length == 0) {
                throw new Exception("The first argument must be the class name"
                                    + " of a classifier");
            }
            String classifier = args[0];
            args[0] = "";
            System.out.println(evaluateModel(classifier, args));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
        }
    }

    /**
     * Evaluates a classifier with the options given in an array of
     * strings. <p>
     *
     * Valid options are: <p>
     *
     * -t name of training file <br>
     * Name of the file with the training data. (required) <p>
     *
     * -T name of test file <br>
     * Name of the file with the test data. If missing a cross-validation
     * is performed. <p>
     *
     * -c class index <br>
     * Index of the class attribute (1, 2, ...; default: last). <p>
     *
     * -x number of folds <br>
     * The number of folds for the cross-validation (default: 10). <p>
     *
     * -s random number seed <br>
     * Random number seed for the cross-validation (default: 1). <p>
     *
     * -m file with cost matrix <br>
     * The name of a file containing a cost matrix. <p>
     *
     * -l name of model input file <br>
     * Loads classifier from the given file. <p>
     *
     * -d name of model output file <br>
     * Saves classifier built from the training data into the given file. <p>
     *
     * -v <br>
     * Outputs no statistics for the training data. <p>
     *
     * -o <br>
     * Outputs statistics only, not the classifier. <p>
     *
     * -i <br>
     * Outputs detailed information-retrieval statistics per class. <p>
     *
     * -k <br>
     * Outputs information-theoretic statistics. <p>
     *
     * -p <br>
     * Outputs predictions for test instances (and nothing else). <p>
     *
     * -r <br>
     * Outputs cumulative margin distribution (and nothing else). <p>
     *
     * -g <br>
     * Only for classifiers that implement "Graphable." Outputs
     * the graph representation of the classifier (and nothing
     * else). <p>
     *
     * @param classifier machine learning classifier
     * @param options the array of string containing the options
     * @exception Exception if model could not be evaluated successfully
     * @return a string describing the results */
    public static String evaluateModel(M5 classifier,
                                       String[] options) throws Exception {

        M5Instances train = null, tempTrain, test = null, template = null;
        int seed = 1, folds = 10, classIndex = -1;
        String trainFileName, testFileName, sourceClass,
                classIndexString, seedString, foldsString, objectInputFileName,
                objectOutputFileName, attributeRangeString;
        boolean IRstatistics = false, noOutput = false,
                                                 printClassifications = false,
                trainStatistics = true,
                                  printMargins = false,
                                                 printComplexityStatistics = false,
                printGraph = false, classStatistics = false, printSource = false;
        StringBuffer text = new StringBuffer();
        BufferedReader trainReader = null, testReader = null;
        ObjectInputStream objectInputStream = null;
        Random random;
        StringBuffer schemeOptionsText = null;
        Interval attributesToOutput = null;
        long trainTimeStart = 0, trainTimeElapsed = 0,
                testTimeStart = 0, testTimeElapsed = 0;

        try {

            // Get basic options (options the same for all schemes)
            classIndexString = M5StaticUtils.getOption('c', options);
            if (classIndexString.length() != 0) {
                classIndex = Integer.parseInt(classIndexString);
            }
            trainFileName = M5StaticUtils.getOption('t', options);
            objectInputFileName = M5StaticUtils.getOption('l', options);
            objectOutputFileName = M5StaticUtils.getOption('d', options);
            testFileName = M5StaticUtils.getOption('T', options);
            if (trainFileName.length() == 0) {
                if (objectInputFileName.length() == 0) {
                    throw new Exception("No training file and no object " +
                                        "input file given.");
                }
                if (testFileName.length() == 0) {
                    throw new Exception("No training file and no test " +
                                        "file given.");
                }
            } else if ((objectInputFileName.length() != 0) &&
                       (true ||
                        (testFileName.length() == 0))) {
                throw new Exception("Classifier not incremental, or no " +
                                    "test file provided: can't " +
                                    "use both train and model file.");
            }
            try {
                if (trainFileName.length() != 0) {
                    trainReader = new BufferedReader(new FileReader(
                            trainFileName));
                }
                if (testFileName.length() != 0) {
                    testReader = new BufferedReader(new FileReader(testFileName));
                }
                if (objectInputFileName.length() != 0) {
                    InputStream is = new FileInputStream(objectInputFileName);
                    if (objectInputFileName.endsWith(".gz")) {
                        is = new GZIPInputStream(is);
                    }
                    objectInputStream = new ObjectInputStream(is);
                }
            } catch (Exception e) {
                throw new Exception("Can't open file " + e.getMessage() + '.');
            }
            if (testFileName.length() != 0) {
                template = test = new M5Instances(testReader, 1);
                if (classIndex != -1) {
                    test.setClassIndex(classIndex - 1);
                } else {
                    String name = test.NameClassIndex();
                    if (!name.equalsIgnoreCase("")) {
                        test.setClass(test.attribute(name));
                    } else {
                        test.setClassIndex(test.numAttributes() - 1);
                    }
                }
                if (classIndex > test.numAttributes()) {
                    throw new Exception("Index of class attribute too large.");
                }
            }
            if (trainFileName.length() != 0) {

                train = new M5Instances(trainReader);

                template = train;
                if (classIndex != -1) {
                    train.setClassIndex(classIndex - 1);
                } else {

                    String name = train.NameClassIndex();
                    if (!name.equalsIgnoreCase("")) {
                        train.setClass(train.attribute(name));
                    } else {
                        train.setClassIndex(train.numAttributes() - 1);
                    }
                }
                if (classIndex > train.numAttributes()) {
                    throw new Exception("Index of class attribute too large.");
                }
                //train = new Instances(train);
            }
            if (template == null) {
                throw new Exception(
                        "No actual dataset provided to use as template");
            }
            seedString = M5StaticUtils.getOption('s', options);
            if (seedString.length() != 0) {
                seed = Integer.parseInt(seedString);
            }
            foldsString = M5StaticUtils.getOption('x', options);
            if (foldsString.length() != 0) {
                folds = Integer.parseInt(foldsString);
            }

            classStatistics = M5StaticUtils.getFlag('i', options);
            noOutput = M5StaticUtils.getFlag('o', options);
            trainStatistics = !M5StaticUtils.getFlag('v', options);
            printComplexityStatistics = M5StaticUtils.getFlag('k', options);
            printMargins = M5StaticUtils.getFlag('r', options);
            printGraph = M5StaticUtils.getFlag('g', options);
            sourceClass = M5StaticUtils.getOption('z', options);
            printSource = (sourceClass.length() != 0);

            // Check -p option
            try {
                attributeRangeString = M5StaticUtils.getOption('p', options);
            } catch (Exception e) {
                throw new Exception(e.getMessage() +
                                    "\nNOTE: the -p option has changed. " +
                                    "It now expects a parameter specifying a range of attributes " +
                                    "to list with the predictions. Use '-p 0' for none.");
            }
            if (attributeRangeString.length() != 0) {
                printClassifications = true;
                if (!attributeRangeString.equals("0")) {
                    attributesToOutput = new Interval(attributeRangeString);
                }
            }

            // If a model file is given, we can't process
            // scheme-specific options
            if (objectInputFileName.length() != 0) {
                M5StaticUtils.checkForRemainingOptions(options);
            } else {

                // Set options for classifier
                /*if (classifier instanceof OptionHandler)
                  {*/
                for (int i = 0; i < options.length; i++) {
                    if (options[i].length() != 0) {
                        if (schemeOptionsText == null) {
                            schemeOptionsText = new StringBuffer();
                        }
                        if (options[i].indexOf(' ') != -1) {
                            schemeOptionsText.append('"' + options[i] + "\" ");
                        } else {
                            schemeOptionsText.append(options[i] + " ");
                        }
                    }
                }
                ((M5) classifier).setOptions(options);
                /*}*/
            }
            M5StaticUtils.checkForRemainingOptions(options);
        } catch (Exception e) {
            throw new Exception("\nException: " + e.getMessage());
        }

        // Setup up evaluation objects
        EvaluateModel trainingEvaluation = new EvaluateModel(new M5Instances(
                template, 0));
        EvaluateModel testingEvaluation = new EvaluateModel(new M5Instances(
                template, 0));

        if (objectInputFileName.length() != 0) {

            // Load classifier from file
            classifier = (M5) objectInputStream.readObject();
            objectInputStream.close();
        }

        // Build the classifier if no object file provided
        if (objectInputFileName.length() == 0) {

            // Build classifier in one go
            tempTrain = new M5Instances(train);
            trainingEvaluation.setPriors(tempTrain);
            testingEvaluation.setPriors(tempTrain);
            trainTimeStart = System.currentTimeMillis();
            classifier.buildClassifier(tempTrain);
            trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
        }

        // Save the classifier if an object output file is provided
        if (objectOutputFileName.length() != 0) {
            OutputStream os = new FileOutputStream(objectOutputFileName);
            if (objectOutputFileName.endsWith(".gz")) {
                os = new GZIPOutputStream(os);
            }
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
            objectOutputStream.writeObject(classifier);
            objectOutputStream.flush();
            objectOutputStream.close();
        }

        // Output test instance predictions only
        if (printClassifications) {
            return printClassifications(classifier, new M5Instances(template, 0),
                                        testFileName, classIndex,
                                        attributesToOutput);
        }

        try {
            StringBuffer a = new StringBuffer();

            a.append(classifier.getHeader(testFileName));

            a.append(printClassifications(classifier, new M5Instances(test, 0),
                                          testFileName, classIndex,
                                          attributesToOutput));

            PrintWriter pw = new PrintWriter(new FileOutputStream(classifier.
                    testOutFileName));
            pw.print(a.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            StringBuffer a = new StringBuffer();

            a.append(classifier.getHeader(trainFileName));

            a.append(printClassifications(classifier, new M5Instances(train, 0),
                                          trainFileName, classIndex,
                                          attributesToOutput));

            PrintWriter pw = new PrintWriter(new FileOutputStream(classifier.
                    trainOutFileName));
            pw.print(a.toString());
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        text.append(classifier.getHeaderNoData(trainFileName));

        if (!(noOutput || printMargins)) {
            /*    		if (schemeOptionsText != null)
                  {
                   text.append("\n@Options: "+schemeOptionsText);
                   text.append("\n");
                  }*/
            text.append("\n" + classifier.toString() + "\n");
        }

        // Compute error estimate from training data
        if ((trainStatistics) && (trainFileName.length() != 0)) {

            testTimeStart = System.currentTimeMillis();
            trainingEvaluation.evaluateModel(classifier,
                                             train);
            testTimeElapsed = System.currentTimeMillis() - testTimeStart;

            // Print the results of the training evaluation
            if (printMargins) {
                return trainingEvaluation.toCumulativeMarginDistributionString();
            } else {
                text.append(trainingEvaluation.
                            toSummaryString("\n@Error on training" +
                                            " data:", printComplexityStatistics));
                if (template.classAttribute().isNominal()) {
                    if (classStatistics) {
                        text.append("\n" +
                                    trainingEvaluation.toClassDetailsString());
                    }
                    text.append("\n" + trainingEvaluation.toMatrixString());
                }

            }
        }

        // Compute proper error estimates
        if (testFileName.length() != 0) {

            // Testing is on the supplied test data
            while (test.readInstance(testReader)) {

                testingEvaluation.evaluateModelOnce((M5) classifier,
                        test.instance(0));
                test.delete(0);
            }
            testReader.close();

            text.append("\n" + testingEvaluation.
                        toSummaryString("@Error on test data:",
                                        printComplexityStatistics));
        } else if (trainFileName.length() != 0) {

            // Testing is via cross-validation on training data
            random = new Random(seed);
            random.setSeed(seed);
            train.randomize(random);
            testingEvaluation.
                    crossValidateModel(classifier, train, folds);
            if (template.classAttribute().isNumeric()) {
                text.append("\n" + testingEvaluation.
                            toSummaryString("@Cross-validation:\n",
                                            printComplexityStatistics));
            } else {
                text.append("\n" + testingEvaluation.
                            toSummaryString("@Stratified " +
                                            "cross-validation:\n",
                                            printComplexityStatistics));
            }
        }
        if (template.classAttribute().isNominal()) {
            if (classStatistics) {
                text.append("\n" + testingEvaluation.toClassDetailsString());
            }
            text.append("\n" + testingEvaluation.toMatrixString());
        }

        text.append("\n@ElapsedTime: " +
                    M5StaticUtils.doubleToString((trainTimeElapsed +
                                                  testTimeElapsed) / 1000.0, 2) +
                    " seconds");
        //text.append("\n@Time taken to test model on training data: " +
        //	    M5StaticUtils.doubleToString(testTimeElapsed / 1000.0,2) +
        //	    " seconds");


        return text.toString();
    }


    /**
     * Evaluates the classifier on a given set of instances.
     *
     * @param classifier machine learning classifier
     * @param data set of test instances for evaluation
     * @exception Exception if model could not be evaluated
     * successfully
     */
    public void evaluateModel(M5 classifier,
                              M5Instances data) throws Exception {

        double[] predicted;

        for (int i = 0; i < data.numInstances(); i++) {
            evaluateModelOnce((M5) classifier,
                              data.instance(i));
        }
    }

    /**
     * Evaluates the classifier on a single instance.
     *
     * @param classifier machine learning classifier
     * @param instance the test instance to be classified
     * @return the prediction made by the clasifier
     * @exception Exception if model could not be evaluated
     * successfully or the data contains string attributes
     */
    public double evaluateModelOnce(M5 classifier,
                                    M5Instance instance) throws Exception {

        M5Instance classMissing = (M5Instance) instance.copy();
        double pred = 0;
        classMissing.setDataset(instance.dataset());
        classMissing.setClassMissing();
        if (m_ClassIsNominal) {

            pred = classifier.classifyInstance(classMissing);
            updateStatsForClassifier(makeDistribution(pred),
                                     instance);

        } else {
            pred = classifier.classifyInstance(classMissing);
            updateStatsForPredictor(pred,
                                    instance);
        }
        return pred;
    }

    /**
     * Evaluates the supplied distribution on a single instance.
     *
     * @param dist the supplied distribution
     * @param instance the test instance to be classified
     * @exception Exception if model could not be evaluated
     * successfully
     */
    public double evaluateModelOnce(double[] dist,
                                    M5Instance instance) throws Exception {
        double pred;
        if (m_ClassIsNominal) {
            pred = M5StaticUtils.maxIndex(dist);
            updateStatsForClassifier(dist, instance);
        } else {
            pred = dist[0];
            updateStatsForPredictor(pred, instance);
        }
        return pred;
    }

    /**
     * Evaluates the supplied prediction on a single instance.
     *
     * @param prediction the supplied prediction
     * @param instance the test instance to be classified
     * @exception Exception if model could not be evaluated
     * successfully
     */
    public void evaluateModelOnce(double prediction,
                                  M5Instance instance) throws Exception {

        if (m_ClassIsNominal) {
            updateStatsForClassifier(makeDistribution(prediction),
                                     instance);
        } else {
            updateStatsForPredictor(prediction, instance);
        }
    }


    /**
     * Gets the number of test instances that had a known class value
     * (actually the sum of the weights of test instances with known
     * class value).
     *
     * @return the number of test instances with known class
     */
    public final double numInstances() {

        return m_WithClass;
    }

    /**
     * Gets the number of instances incorrectly classified (that is, for
     * which an incorrect prediction was made). (Actually the sum of the weights
     * of these instances)
     *
     * @return the number of incorrectly classified instances
     */
    public final double incorrect() {

        return m_Incorrect;
    }

    /**
     * Gets the percentage of instances incorrectly classified (that is, for
     * which an incorrect prediction was made).
     *
     * @return the percent of incorrectly classified instances
     * (between 0 and 100)
     */
    public final double pctIncorrect() {

        return 100 * m_Incorrect / m_WithClass;
    }

    /**
     * Gets the total cost, that is, the cost of each prediction times the
     * weight of the instance, summed over all instances.
     *
     * @return the total cost
     */
    public final double totalCost() {

        return m_TotalCost;
    }

    /**
     * Gets the average cost, that is, total cost of misclassifications
     * (incorrect plus unclassified) over the total number of instances.
     *
     * @return the average cost.
     */
    public final double avgCost() {

        return m_TotalCost / m_WithClass;
    }

    /**
     * Gets the number of instances correctly classified (that is, for
     * which a correct prediction was made). (Actually the sum of the weights
     * of these instances)
     *
     * @return the number of correctly classified instances
     */
    public final double correct() {

        return m_Correct;
    }

    /**
     * Gets the percentage of instances correctly classified (that is, for
     * which a correct prediction was made).
     *
     * @return the percent of correctly classified instances (between 0 and 100)
     */
    public final double pctCorrect() {

        return 100 * m_Correct / m_WithClass;
    }

    /**
     * Gets the number of instances not classified (that is, for
     * which no prediction was made by the classifier). (Actually the sum
     * of the weights of these instances)
     *
     * @return the number of unclassified instances
     */
    public final double unclassified() {

        return m_Unclassified;
    }

    /**
     * Gets the percentage of instances not classified (that is, for
     * which no prediction was made by the classifier).
     *
     * @return the percent of unclassified instances (between 0 and 100)
     */
    public final double pctUnclassified() {

        return 100 * m_Unclassified / m_WithClass;
    }

    /**
     * Returns the estimated error rate or the root mean squared error
     * (if the class is numeric). If a cost matrix was given this
     * error rate gives the average cost.
     *
     * @return the estimated error rate (between 0 and 1, or between 0 and
     * maximum cost)
     */
    public final double errorRate() {

        if (!m_ClassIsNominal) {
            return Math.sqrt(m_SumSqrErr / m_WithClass);
        }

        return m_Incorrect / m_WithClass;

    }

    /**
     * Returns value of kappa statistic if class is nominal.
     *
     * @return the value of the kappa statistic
     */
    public final double kappa() {

        double[] sumRows = new double[m_ConfusionMatrix.length];
        double[] sumColumns = new double[m_ConfusionMatrix.length];
        double sumOfWeights = 0;
        for (int i = 0; i < m_ConfusionMatrix.length; i++) {
            for (int j = 0; j < m_ConfusionMatrix.length; j++) {
                sumRows[i] += m_ConfusionMatrix[i][j];
                sumColumns[j] += m_ConfusionMatrix[i][j];
                sumOfWeights += m_ConfusionMatrix[i][j];
            }
        }
        double correct = 0, chanceAgreement = 0;
        for (int i = 0; i < m_ConfusionMatrix.length; i++) {
            chanceAgreement += (sumRows[i] * sumColumns[i]);
            correct += m_ConfusionMatrix[i][i];
        }
        chanceAgreement /= (sumOfWeights * sumOfWeights);
        correct /= sumOfWeights;

        if (chanceAgreement < 1) {
            return (correct - chanceAgreement) / (1 - chanceAgreement);
        } else {
            return 1;
        }
    }

    /**
     * Returns the correlation coefficient if the class is numeric.
     *
     * @return the correlation coefficient
     * @exception Exception if class is not numeric
     */
    public final double correlationCoefficient() throws Exception {

        if (m_ClassIsNominal) {
            throw
                    new Exception("Can't compute correlation coefficient: " +
                                  "class is nominal!");
        }

        double correlation = 0;
        double varActual =
                m_SumSqrClass - m_SumClass * m_SumClass / m_WithClass;
        double varPredicted =
                m_SumSqrPredicted - m_SumPredicted * m_SumPredicted /
                m_WithClass;
        double varProd =
                m_SumClassPredicted - m_SumClass * m_SumPredicted / m_WithClass;

        if (M5StaticUtils.smOrEq(varActual * varPredicted, 0.0)) {
            correlation = 0.0;
        } else {
            correlation = varProd / Math.sqrt(varActual * varPredicted);
        }

        return correlation;
    }

    /**
     * Returns the mean absolute error. Refers to the error of the
     * predicted values for numeric classes, and the error of the
     * predicted probability distribution for nominal classes.
     *
     * @return the mean absolute error
     */
    public final double meanAbsoluteError() {

        return m_SumAbsErr / m_WithClass;
    }

    /**
     * Returns the mean absolute error of the prior.
     *
     * @return the mean absolute error
     */
    public final double meanPriorAbsoluteError() {

        return m_SumPriorAbsErr / m_WithClass;
    }

    /**
     * Returns the relative absolute error.
     *
     * @return the relative absolute error
     * @exception Exception if it can't be computed
     */
    public final double relativeAbsoluteError() throws Exception {

        return 100 * meanAbsoluteError() / meanPriorAbsoluteError();
    }

    /**
     * Returns the root mean squared error.
     *
     * @return the root mean squared error
     */
    public final double rootMeanSquaredError() {

        return Math.sqrt(m_SumSqrErr / m_WithClass);
    }

    /**
     * Returns the root mean prior squared error.
     *
     * @return the root mean prior squared error
     */
    public final double rootMeanPriorSquaredError() {

        return Math.sqrt(m_SumPriorSqrErr / m_WithClass);
    }

    /**
     * Returns the root relative squared error if the class is numeric.
     *
     * @return the root relative squared error
     */
    public final double rootRelativeSquaredError() {

        return 100.0 * rootMeanSquaredError() /
                rootMeanPriorSquaredError();
    }

    /**
     * Calculate the entropy of the prior distribution
     *
     * @return the entropy of the prior distribution
     * @exception Exception if the class is not nominal
     */
    public final double priorEntropy() throws Exception {

        if (!m_ClassIsNominal) {
            throw
                    new Exception("Can't compute entropy of class prior: " +
                                  "class numeric!");
        }

        double entropy = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            entropy -= m_ClassPriors[i] / m_ClassPriorsSum
                    * M5StaticUtils.log2(m_ClassPriors[i] / m_ClassPriorsSum);
        }
        return entropy;
    }


    /**
     * Return the total Kononenko & Bratko Information score in bits
     *
     * @return the K&B information score
     * @exception Exception if the class is not nominal
     */
    public final double KBInformation() throws Exception {

        if (!m_ClassIsNominal) {
            throw
                    new Exception("Can't compute K&B Info score: " +
                                  "class numeric!");
        }
        return m_SumKBInfo;
    }

    /**
     * Return the Kononenko & Bratko Information score in bits per
     * instance.
     *
     * @return the K&B information score
     * @exception Exception if the class is not nominal
     */
    public final double KBMeanInformation() throws Exception {

        if (!m_ClassIsNominal) {
            throw
                    new Exception("Can't compute K&B Info score: "
                                  + "class numeric!");
        }
        return m_SumKBInfo / m_WithClass;
    }

    /**
     * Return the Kononenko & Bratko Relative Information score
     *
     * @return the K&B relative information score
     * @exception Exception if the class is not nominal
     */
    public final double KBRelativeInformation() throws Exception {

        if (!m_ClassIsNominal) {
            throw
                    new Exception("Can't compute K&B Info score: " +
                                  "class numeric!");
        }
        return 100.0 * KBInformation() / priorEntropy();
    }

    /**
     * Returns the total entropy for the null model
     *
     * @return the total null model entropy
     */
    public final double SFPriorEntropy() {

        return m_SumPriorEntropy;
    }

    /**
     * Returns the entropy per instance for the null model
     *
     * @return the null model entropy per instance
     */
    public final double SFMeanPriorEntropy() {

        return m_SumPriorEntropy / m_WithClass;
    }

    /**
     * Returns the total entropy for the scheme
     *
     * @return the total scheme entropy
     */
    public final double SFSchemeEntropy() {

        return m_SumSchemeEntropy;
    }

    /**
     * Returns the entropy per instance for the scheme
     *
     * @return the scheme entropy per instance
     */
    public final double SFMeanSchemeEntropy() {

        return m_SumSchemeEntropy / m_WithClass;
    }

    /**
     * Returns the total SF, which is the null model entropy minus
     * the scheme entropy.
     *
     * @return the total SF
     */
    public final double SFEntropyGain() {

        return m_SumPriorEntropy - m_SumSchemeEntropy;
    }

    /**
     * Returns the SF per instance, which is the null model entropy
     * minus the scheme entropy, per instance.
     *
     * @return the SF per instance
     */
    public final double SFMeanEntropyGain() {

        return (m_SumPriorEntropy - m_SumSchemeEntropy) / m_WithClass;
    }

    /**
     * Output the cumulative margin distribution as a string suitable
     * for input for gnuplot or similar package.
     *
     * @return the cumulative margin distribution
     * @exception Exception if the class attribute is nominal
     */
    public String toCumulativeMarginDistributionString() throws Exception {

        if (!m_ClassIsNominal) {
            throw new Exception(
                    "Class must be nominal for margin distributions");
        }
        String result = "";
        double cumulativeCount = 0;
        double margin;
        for (int i = 0; i <= k_MarginResolution; i++) {
            if (m_MarginCounts[i] != 0) {
                cumulativeCount += m_MarginCounts[i];
                margin = (double) i * 2.0 / k_MarginResolution - 1.0;
                result = result + M5StaticUtils.doubleToString(margin, 7, 3) +
                         ' '
                         + M5StaticUtils.doubleToString(cumulativeCount * 100
                        / m_WithClass, 7, 3) + '\n';
            } else if (i == 0) {
                result = M5StaticUtils.doubleToString( -1.0, 7, 3) + ' '
                         + M5StaticUtils.doubleToString(0, 7, 3) + '\n';
            }
        }
        return result;
    }


    /**
     * Calls toSummaryString() with no title and no complexity stats
     *
     * @return a summary description of the classifier evaluation
     */
    public String toSummaryString() {

        return toSummaryString("", false);
    }

    /**
     * Calls toSummaryString() with a default title.
     *
     * @param printComplexityStatistics if true, complexity statistics are
     * returned as well
     */
    public String toSummaryString(boolean printComplexityStatistics) {

        return toSummaryString("=== Summary ===\n", printComplexityStatistics);
    }

    /**
     * Outputs the performance statistics in summary form. Lists
     * number (and percentage) of instances classified correctly,
     * incorrectly and unclassified. Outputs the total number of
     * instances classified, and the number of instances (if any)
     * that had no class value provided.
     *
     * @param title the title for the statistics
     * @param printComplexityStatistics if true, complexity statistics are
     * returned as well
     * @return the summary as a String
     */
    public String toSummaryString(String title,
                                  boolean printComplexityStatistics) {

        double mae, mad = 0;
        StringBuffer text = new StringBuffer();

        text.append(title + "\n");
        try {
            if (m_WithClass > 0) {
                if (m_ClassIsNominal) {

                    text.append("@Correctly Classified Instances     ");
                    text.append(M5StaticUtils.doubleToString(correct(), 12, 4) +
                                "     " +
                                M5StaticUtils.doubleToString(pctCorrect(),
                            12, 4) + " %\n");
                    text.append("@Incorrectly Classified Instances   ");
                    text.append(M5StaticUtils.doubleToString(incorrect(), 12, 4) +
                                "     " +
                                M5StaticUtils.doubleToString(pctIncorrect(),
                            12, 4) + " %\n");
                    text.append("@Kappa statistic                    ");
                    text.append(M5StaticUtils.doubleToString(kappa(), 12, 4) +
                                "\n");

                    if (printComplexityStatistics) {
                        text.append("@K&B Relative Info Score            ");
                        text.append(M5StaticUtils.doubleToString(
                                KBRelativeInformation(), 12, 4)
                                    + " %\n");
                        text.append("@K&B Information Score              ");
                        text.append(M5StaticUtils.doubleToString(KBInformation(),
                                12, 4)
                                    + " bits");
                        text.append(M5StaticUtils.doubleToString(
                                KBMeanInformation(), 12, 4)
                                    + " bits/instance\n");
                    }
                } else {
                    text.append("@Correlation coefficient            ");
                    text.append(M5StaticUtils.doubleToString(
                            correlationCoefficient(), 12, 4) +
                                "\n");
                }
                if (printComplexityStatistics) {
                    text.append("@Class complexity | order 0         ");
                    text.append(M5StaticUtils.doubleToString(SFPriorEntropy(),
                            12, 4)
                                + " bits");
                    text.append(M5StaticUtils.doubleToString(SFMeanPriorEntropy(),
                            12, 4)
                                + " bits/instance\n");
                    text.append("@Class complexity | scheme          ");
                    text.append(M5StaticUtils.doubleToString(SFSchemeEntropy(),
                            12, 4)
                                + " bits");
                    text.append(M5StaticUtils.doubleToString(
                            SFMeanSchemeEntropy(), 12, 4)
                                + " bits/instance\n");
                    text.append("@Complexity improvement     (Sf)    ");
                    text.append(M5StaticUtils.doubleToString(SFEntropyGain(),
                            12, 4) + " bits");
                    text.append(M5StaticUtils.doubleToString(SFMeanEntropyGain(),
                            12, 4)
                                + " bits/instance\n");
                }

                text.append("@Mean absolute error                ");
                text.append(M5StaticUtils.doubleToString(meanAbsoluteError(),
                        12, 4)
                            + "\n");
                text.append("@Root mean squared error            ");
                text.append(M5StaticUtils.
                            doubleToString(rootMeanSquaredError(), 12, 4)
                            + "\n");
                text.append("@Relative absolute error            ");
                text.append(M5StaticUtils.doubleToString(relativeAbsoluteError(),
                        12, 4) + " %\n");
                text.append("@Root relative squared error        ");
                text.append(M5StaticUtils.doubleToString(
                        rootRelativeSquaredError(),
                        12, 4) + " %\n");
            }
            if (M5StaticUtils.gr(unclassified(), 0)) {
                text.append("@UnClassified Instances             ");
                text.append(M5StaticUtils.doubleToString(unclassified(), 12, 4) +
                            "     " +
                            M5StaticUtils.doubleToString(pctUnclassified(),
                        12, 4) + " %\n");
            }
            //text.append("@Total Number of Instances          ");
            //text.append(M5StaticUtils.doubleToString(m_WithClass, 12, 4) + "\n");
            if (m_MissingClass > 0) {
                text.append("@Ignored Class Unknown Instances            ");
                text.append(M5StaticUtils.doubleToString(m_MissingClass, 12, 4) +
                            "\n");
            }
        } catch (Exception ex) {
            // Should never occur since the class is known to be nominal
            // here
            System.err.println("Arggh - Must be a bug in EvaluateModel class");
        }

        return text.toString();
    }

    /**
     * Calls toMatrixString() with a default title.
     *
     * @return the confusion matrix as a string
     * @exception Exception if the class is numeric
     */
    public String toMatrixString() throws Exception {

        return toMatrixString("=== Confusion Matrix ===\n");
    }

    /**
     * Outputs the performance statistics as a classification confusion
     * matrix. For each class value, shows the distribution of
     * predicted class values.
     *
     * @param title the title for the confusion matrix
     * @return the confusion matrix as a String
     * @exception Exception if the class is numeric
     */
    public String toMatrixString(String title) throws Exception {

        StringBuffer text = new StringBuffer();
        char[] IDChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                         'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                         'u', 'v', 'w', 'x', 'y', 'z'};
        int IDWidth;
        boolean fractional = false;

        if (!m_ClassIsNominal) {
            throw new Exception("EvaluateModel: No confusion matrix possible!");
        }

        // Find the maximum value in the matrix
        // and check for fractional display requirement
        double maxval = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            for (int j = 0; j < m_NumClasses; j++) {
                double current = m_ConfusionMatrix[i][j];
                if (current < 0) {
                    current *= -10;
                }
                if (current > maxval) {
                    maxval = current;
                }
                double fract = current - Math.rint(current);
                if (!fractional
                    && ((Math.log(fract) / Math.log(10)) >= -2)) {
                    fractional = true;
                }
            }
        }

        IDWidth = 1 + Math.max((int) (Math.log(maxval) / Math.log(10)
                                      + (fractional ? 3 : 0)),
                               (int) (Math.log(m_NumClasses) /
                                      Math.log(IDChars.length)));
        text.append(title).append("\n");
        for (int i = 0; i < m_NumClasses; i++) {
            if (fractional) {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth - 3))
                        .append("   ");
            } else {
                text.append(" ").append(num2ShortID(i, IDChars, IDWidth));
            }
        }
        text.append("   <-- classified as\n");
        for (int i = 0; i < m_NumClasses; i++) {
            for (int j = 0; j < m_NumClasses; j++) {
                text.append(" ").append(
                        M5StaticUtils.doubleToString(m_ConfusionMatrix[i][j],
                        IDWidth,
                        (fractional ? 2 : 0)));
            }
            text.append(" | ").append(num2ShortID(i, IDChars, IDWidth))
                    .append(" = ").append(m_ClassNames[i]).append("\n");
        }
        return text.toString();
    }

    public String toClassDetailsString() throws Exception {

        return toClassDetailsString("=== Detailed Accuracy By Class ===\n");
    }

    /**
     * Generates a breakdown of the accuracy for each class,
     * incorporating various information-retrieval statistics, such as
     * true/false positive rate, precision/recall/F-Measure.  Should be
     * useful for ROC curves, recall/precision curves.
     *
     * @param title the title to prepend the stats string with
     * @return the statistics presented as a string
     */
    public String toClassDetailsString(String title) throws Exception {

        if (!m_ClassIsNominal) {
            throw new Exception("EvaluateModel: No confusion matrix possible!");
        }
        StringBuffer text = new StringBuffer(title
                                             + "\nTP Rate   FP Rate"
                                             + "   Precision   Recall"
                                             + "  F-Measure   Class\n");
        for (int i = 0; i < m_NumClasses; i++) {
            text.append(M5StaticUtils.doubleToString(truePositiveRate(i), 7, 3))
                    .append("   ");
            text.append(M5StaticUtils.doubleToString(falsePositiveRate(i), 7, 3))
                    .append("    ");
            text.append(M5StaticUtils.doubleToString(precision(i), 7, 3))
                    .append("   ");
            text.append(M5StaticUtils.doubleToString(recall(i), 7, 3))
                    .append("   ");
            text.append(M5StaticUtils.doubleToString(fMeasure(i), 7, 3))
                    .append("    ");
            text.append(m_ClassNames[i]).append('\n');
        }
        return text.toString();
    }

    /**
     * Calculate the number of true positives with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double numTruePositives(int classIndex) {

        double correct = 0;
        for (int j = 0; j < m_NumClasses; j++) {
            if (j == classIndex) {
                correct += m_ConfusionMatrix[classIndex][j];
            }
        }
        return correct;
    }

    /**
     * Calculate the true positive rate with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified positives
     * ------------------------------
     *       total positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double truePositiveRate(int classIndex) {

        double correct = 0, total = 0;
        for (int j = 0; j < m_NumClasses; j++) {
            if (j == classIndex) {
                correct += m_ConfusionMatrix[classIndex][j];
            }
            total += m_ConfusionMatrix[classIndex][j];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculate the number of true negatives with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double numTrueNegatives(int classIndex) {

        double correct = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i != classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j != classIndex) {
                        correct += m_ConfusionMatrix[i][j];
                    }
                }
            }
        }
        return correct;
    }

    /**
     * Calculate the true negative rate with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified negatives
     * ------------------------------
     *       total negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the true positive rate
     */
    public double trueNegativeRate(int classIndex) {

        double correct = 0, total = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i != classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j != classIndex) {
                        correct += m_ConfusionMatrix[i][j];
                    }
                    total += m_ConfusionMatrix[i][j];
                }
            }
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculate number of false positives with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * incorrectly classified negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double numFalsePositives(int classIndex) {

        double incorrect = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i != classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j == classIndex) {
                        incorrect += m_ConfusionMatrix[i][j];
                    }
                }
            }
        }
        return incorrect;
    }

    /**
     * Calculate the false positive rate with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * incorrectly classified negatives
     * --------------------------------
     *        total negatives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double falsePositiveRate(int classIndex) {

        double incorrect = 0, total = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i != classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j == classIndex) {
                        incorrect += m_ConfusionMatrix[i][j];
                    }
                    total += m_ConfusionMatrix[i][j];
                }
            }
        }
        if (total == 0) {
            return 0;
        }
        return incorrect / total;
    }

    /**
     * Calculate number of false negatives with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * incorrectly classified positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double numFalseNegatives(int classIndex) {

        double incorrect = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i == classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j != classIndex) {
                        incorrect += m_ConfusionMatrix[i][j];
                    }
                }
            }
        }
        return incorrect;
    }

    /**
     * Calculate the false negative rate with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * incorrectly classified positives
     * --------------------------------
     *        total positives
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the false positive rate
     */
    public double falseNegativeRate(int classIndex) {

        double incorrect = 0, total = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i == classIndex) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (j != classIndex) {
                        incorrect += m_ConfusionMatrix[i][j];
                    }
                    total += m_ConfusionMatrix[i][j];
                }
            }
        }
        if (total == 0) {
            return 0;
        }
        return incorrect / total;
    }

    /**
     * Calculate the recall with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified positives
     * ------------------------------
     *       total positives
     * </pre><p>
     * (Which is also the same as the truePositiveRate.)
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the recall
     */
    public double recall(int classIndex) {

        return truePositiveRate(classIndex);
    }

    /**
     * Calculate the precision with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * correctly classified positives
     * ------------------------------
     *  total predicted as positive
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the precision
     */
    public double precision(int classIndex) {

        double correct = 0, total = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            if (i == classIndex) {
                correct += m_ConfusionMatrix[i][classIndex];
            }
            total += m_ConfusionMatrix[i][classIndex];
        }
        if (total == 0) {
            return 0;
        }
        return correct / total;
    }

    /**
     * Calculate the F-Measure with respect to a particular class.
     * This is defined as<p>
     * <pre>
     * 2 * recall * precision
     * ----------------------
     *   recall + precision
     * </pre>
     *
     * @param classIndex the index of the class to consider as "positive"
     * @return the F-Measure
     */
    public double fMeasure(int classIndex) {

        double precision = precision(classIndex);
        double recall = recall(classIndex);
        if ((precision + recall) == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    /**
     * Sets the class prior probabilities
     *
     * @param train the training instances used to determine
     * the prior probabilities
     * @exception Exception if the class attribute of the instances is not
     * set
     */
    public void setPriors(M5Instances train) throws Exception {

        if (!m_ClassIsNominal) {

            m_NumTrainClassVals = 0;
            m_TrainClassVals = null;
            m_TrainClassWeights = null;
            m_PriorErrorEstimator = null;
            m_ErrorEstimator = null;

            for (int i = 0; i < train.numInstances(); i++) {
                M5Instance currentInst = train.instance(i);
                if (!currentInst.classIsMissing()) {
                    addNumericTrainClass(currentInst.classValue(),
                                         currentInst.weight());
                }
            }

        } else {
            for (int i = 0; i < m_NumClasses; i++) {
                m_ClassPriors[i] = 1;
            }
            m_ClassPriorsSum = m_NumClasses;
            for (int i = 0; i < train.numInstances(); i++) {
                if (!train.instance(i).classIsMissing()) {
                    m_ClassPriors[(int) train.instance(i).classValue()] +=
                            train.instance(i).weight();
                    m_ClassPriorsSum += train.instance(i).weight();
                }
            }
        }
    }

    /**
     * Updates the class prior probabilities (when incrementally
     * training)
     *
     * @param instance the new training instance seen
     * @exception Exception if the class of the instance is not
     * set
     */
    public void updatePriors(M5Instance instance) throws Exception {
        if (!instance.classIsMissing()) {
            if (!m_ClassIsNominal) {
                if (!instance.classIsMissing()) {
                    addNumericTrainClass(instance.classValue(),
                                         instance.weight());
                }
            } else {
                m_ClassPriors[(int) instance.classValue()] +=
                        instance.weight();
                m_ClassPriorsSum += instance.weight();
            }
        }
    }

    /**
     * Tests whether the current evaluation object is equal to another
     * evaluation object
     *
     * @param obj the object to compare against
     * @return true if the two objects are equal
     */
    public boolean equals(Object obj) {

        if ((obj == null) || !(obj.getClass().equals(this.getClass()))) {
            return false;
        }
        EvaluateModel cmp = (EvaluateModel) obj;
        if (m_ClassIsNominal != cmp.m_ClassIsNominal) {
            return false;
        }
        if (m_NumClasses != cmp.m_NumClasses) {
            return false;
        }

        if (m_Incorrect != cmp.m_Incorrect) {
            return false;
        }
        if (m_Correct != cmp.m_Correct) {
            return false;
        }
        if (m_Unclassified != cmp.m_Unclassified) {
            return false;
        }
        if (m_MissingClass != cmp.m_MissingClass) {
            return false;
        }
        if (m_WithClass != cmp.m_WithClass) {
            return false;
        }

        if (m_SumErr != cmp.m_SumErr) {
            return false;
        }
        if (m_SumAbsErr != cmp.m_SumAbsErr) {
            return false;
        }
        if (m_SumSqrErr != cmp.m_SumSqrErr) {
            return false;
        }
        if (m_SumClass != cmp.m_SumClass) {
            return false;
        }
        if (m_SumSqrClass != cmp.m_SumSqrClass) {
            return false;
        }
        if (m_SumPredicted != cmp.m_SumPredicted) {
            return false;
        }
        if (m_SumSqrPredicted != cmp.m_SumSqrPredicted) {
            return false;
        }
        if (m_SumClassPredicted != cmp.m_SumClassPredicted) {
            return false;
        }

        if (m_ClassIsNominal) {
            for (int i = 0; i < m_NumClasses; i++) {
                for (int j = 0; j < m_NumClasses; j++) {
                    if (m_ConfusionMatrix[i][j] != cmp.m_ConfusionMatrix[i][j]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Prints the predictions for the given dataset into a String variable.
     */
    private static String printClassifications(M5 classifier,
                                               M5Instances train,
                                               String testFileName,
                                               int classIndex,
                                               Interval attributesToOutput) throws
            Exception {

        StringBuffer text = new StringBuffer();
        if (testFileName.length() != 0) {
            BufferedReader testReader = null;
            try {
                testReader = new BufferedReader(new FileReader(testFileName));
            } catch (Exception e) {
                throw new Exception("Can't open file " + e.getMessage() + '.');
            }
            M5Instances test = new M5Instances(testReader, 1);

            if (classIndex != -1) {
                test.setClassIndex(classIndex - 1);
            } else {
                String name = test.NameClassIndex();
                if (!name.equalsIgnoreCase("")) {
                    test.setClass(test.attribute(name));
                } else {
                    test.setClassIndex(test.numAttributes() - 1);
                }
            }
            int i = 0;
            while (test.readInstance(testReader)) {
                M5Instance instance = test.instance(0);
                M5Instance withMissing = (M5Instance) instance.copy();
                withMissing.setDataset(test);
                double predValue =
                        ((M5) classifier).classifyInstance(withMissing);

                predValue = roundNum(predValue);

                if (test.classAttribute().isNumeric()) {
                    if (instance.classIsMissing()) {
                        text.append("missing ");
                    } else {
                        text.append(instance.classValue()+" ");
                    }
                    if (M5Instance.isMissingValue(predValue)) {
                        text.append("missing ");
                    } else {
                        text.append(predValue);
                    }
                    text.append(" " +
                                attributeValuesString(withMissing, attributesToOutput) +
                                "\n");
                } else {
                    if (M5Instance.isMissingValue(predValue)) {
                        text.append("missing ");
                    } else {
                        text.append(test.classAttribute().value((int) predValue) +
                                    " ");
                    }
                    text.append(attributeValuesString(withMissing, attributesToOutput) + " "
                                +
                                instance.toString(instance.classIndex()) +
                                "\n");
                }
                test.delete(0);
                i++;
            }
            testReader.close();
        }
        return text.toString();
    }


    /* To round to 4 decimal in a double */
    public static double roundNum(double num) {
        double valor = 0;

        valor = num;

        valor = valor * 10000;
        valor = java.lang.Math.round(valor);
        valor = valor / 10000;

        return valor;

    }


    /**
     * Builds a string listing the attribute values in a specified range of indices,
     * separated by commas and enclosed in brackets.
     *
     * @param instance the instance to print the values from
     * @param attributes the range of the attributes to list
     * @return a string listing values of the attributes in the range
     * @throws Exception
     */
    private static String attributeValuesString(M5Instance instance,
                                                Interval attRange) throws
            Exception {
        StringBuffer text = new StringBuffer();
        if (attRange != null) {
            boolean firstOutput = true;
            attRange.setUpper(instance.numAttributes() - 1);
            for (int i = 0; i < instance.numAttributes(); i++) {
                if (attRange.isInRange(i) && i != instance.classIndex()) {
                    if (firstOutput) {
                        text.append("(");
                    } else {
                        text.append(",");
                    }
                    text.append(instance.toString(i));
                    firstOutput = false;
                }
            }
            if (!firstOutput) {
                text.append(")");
            }
        }
        return text.toString();
    }

    /**
     * Make up the help string giving all the command line options
     *
     * @param classifier the classifier to include options for
     * @return a string detailing the valid command line options
     */
    private static String makeOptionString(M5 classifier) {

        StringBuffer optionsText = new StringBuffer("");

        // General options
        optionsText.append("\n\nGeneral options:\n\n");
        optionsText.append("-t <name of training file>\n");
        optionsText.append("\tSets training file.\n");
        optionsText.append("-T <name of test file>\n");
        optionsText.append("\tSets test file. If missing, a cross-validation");
        optionsText.append(" will be performed on the training data.\n");
        optionsText.append("-c <class index>\n");
        optionsText.append("\tSets index of class attribute (default: last).\n");
        optionsText.append("-x <number of folds>\n");
        optionsText.append(
                "\tSets number of folds for cross-validation (default: 10).\n");
        optionsText.append("-s <random number seed>\n");
        optionsText.append(
                "\tSets random number seed for cross-validation (default: 1).\n");
        optionsText.append("-m <name of file with cost matrix>\n");
        optionsText.append("\tSets file with cost matrix.\n");
        optionsText.append("-l <name of input file>\n");
        optionsText.append("\tSets model input file.\n");
        optionsText.append("-d <name of output file>\n");
        optionsText.append("\tSets model output file.\n");
        optionsText.append("-v\n");
        optionsText.append("\tOutputs no statistics for training data.\n");
        optionsText.append("-o\n");
        optionsText.append("\tOutputs statistics only, not the classifier.\n");
        optionsText.append("-i\n");
        optionsText.append("\tOutputs detailed information-retrieval");
        optionsText.append(" statistics for each class.\n");
        optionsText.append("-k\n");
        optionsText.append("\tOutputs information-theoretic statistics.\n");
        optionsText.append("-p <attribute range>\n");
        optionsText.append(
                "\tOnly outputs predictions for test instances, along with attributes "
                + "(0 for none).\n");
        optionsText.append("-r\n");
        optionsText.append("\tOnly outputs cumulative margin distribution.\n");

        /*if (classifier instanceof Drawable) {
          optionsText.append("-g\n");
          optionsText.append("\tOnly outputs the graph representation"
             + " of the classifier.\n");
             }*/

        // Get scheme-specific options
        /*if (classifier instanceof OptionHandler)
             {*/
        optionsText.append("\nOptions specific to "
                           + classifier.getClass().getName()
                           + ":\n\n");
        Enumeration enuma = ((M5) classifier).listOptions();
        while (enuma.hasMoreElements()) {
            Information option = (Information) enuma.nextElement();
            optionsText.append(option.synopsis() + '\n');
            optionsText.append(option.description() + "\n");
        }
        /*}*/
        return optionsText.toString();
    }


    /**
     * Method for generating indices for the confusion matrix.
     *
     * @param num integer to format
     * @return the formatted integer as a string
     */
    private String num2ShortID(int num, char[] IDChars, int IDWidth) {

        char ID[] = new char[IDWidth];
        int i;

        for (i = IDWidth - 1; i >= 0; i--) {
            ID[i] = IDChars[num % IDChars.length];
            num = num / IDChars.length - 1;
            if (num < 0) {
                break;
            }
        }
        for (i--; i >= 0; i--) {
            ID[i] = ' ';
        }

        return new String(ID);
    }


    /**
     * Convert a single prediction into a probability distribution
     * with all zero probabilities except the predicted value which
     * has probability 1.0;
     *
     * @param predictedClass the index of the predicted class
     * @return the probability distribution
     */
    private double[] makeDistribution(double predictedClass) {

        double[] result = new double[m_NumClasses];
        if (M5Instance.isMissingValue(predictedClass)) {
            return result;
        }
        if (m_ClassIsNominal) {
            result[(int) predictedClass] = 1.0;
        } else {
            result[0] = predictedClass;
        }
        return result;
    }

    /**
     * Updates all the statistics about a classifiers performance for
     * the current test instance.
     *
     * @param predictedDistribution the probabilities assigned to
     * each class
     * @param instance the instance to be classified
     * @exception Exception if the class of the instance is not
     * set
     */
    private void updateStatsForClassifier(double[] predictedDistribution,
                                          M5Instance instance) throws Exception {

        int actualClass = (int) instance.classValue();
        double costFactor = 1;

        if (!instance.classIsMissing()) {
            updateMargins(predictedDistribution, actualClass, instance.weight());

            // Determine the predicted class (doesn't detect multiple
            // classifications)
            int predictedClass = -1;
            double bestProb = 0.0;
            for (int i = 0; i < m_NumClasses; i++) {
                if (predictedDistribution[i] > bestProb) {
                    predictedClass = i;
                    bestProb = predictedDistribution[i];
                }
            }

            m_WithClass += instance.weight();

            // Update counts when no class was predicted
            if (predictedClass < 0) {
                m_Unclassified += instance.weight();
                return;
            }

            double predictedProb = Math.max(MIN_SF_PROB,
                                            predictedDistribution[actualClass]);
            double priorProb = Math.max(MIN_SF_PROB,
                                        m_ClassPriors[actualClass]
                                        / m_ClassPriorsSum);
            if (predictedProb >= priorProb) {
                m_SumKBInfo += (M5StaticUtils.log2(predictedProb) -
                                M5StaticUtils.log2(priorProb))
                        * instance.weight();
            } else {
                m_SumKBInfo -= (M5StaticUtils.log2(1.0 - predictedProb) -
                                M5StaticUtils.log2(1.0 - priorProb))
                        * instance.weight();
            }

            m_SumSchemeEntropy -= M5StaticUtils.log2(predictedProb) *
                    instance.weight();
            m_SumPriorEntropy -= M5StaticUtils.log2(priorProb) *
                    instance.weight();

            updateNumericScores(predictedDistribution,
                                makeDistribution(instance.classValue()),
                                instance.weight());

            // Update other stats
            m_ConfusionMatrix[actualClass][predictedClass] += instance.weight();
            if (predictedClass != actualClass) {
                m_Incorrect += instance.weight();
            } else {
                m_Correct += instance.weight();
            }
        } else {
            m_MissingClass += instance.weight();
        }
    }

    /**
     * Updates all the statistics about a predictors performance for
     * the current test instance.
     *
     * @param predictedValue the numeric value the classifier predicts
     * @param instance the instance to be classified
     * @exception Exception if the class of the instance is not
     * set
     */
    private void updateStatsForPredictor(double predictedValue,
                                         M5Instance instance) throws Exception {

        if (!instance.classIsMissing()) {

            // Update stats
            m_WithClass += instance.weight();
            if (M5Instance.isMissingValue(predictedValue)) {
                m_Unclassified += instance.weight();
                return;
            }
            m_SumClass += instance.weight() * instance.classValue();
            m_SumSqrClass += instance.weight() * instance.classValue()
                    * instance.classValue();
            m_SumClassPredicted += instance.weight()
                    * instance.classValue() * predictedValue;
            m_SumPredicted += predictedValue;
            m_SumSqrPredicted += predictedValue * predictedValue;

            if (m_ErrorEstimator == null) {
                setNumericPriorsFromBuffer();
            }
            double predictedProb = Math.max(m_ErrorEstimator.getProbability(
                    predictedValue
                    - instance.classValue()),
                                            MIN_SF_PROB);
            double priorProb = Math.max(m_PriorErrorEstimator.getProbability(
                    instance.classValue()),
                                        MIN_SF_PROB);

            m_SumSchemeEntropy -= M5StaticUtils.log2(predictedProb) *
                    instance.weight();
            m_SumPriorEntropy -= M5StaticUtils.log2(priorProb) *
                    instance.weight();
            m_ErrorEstimator.addValue(predictedValue - instance.classValue(),
                                      instance.weight());

            updateNumericScores(makeDistribution(predictedValue),
                                makeDistribution(instance.classValue()),
                                instance.weight());

        } else {
            m_MissingClass += instance.weight();
        }
    }

    /**
     * Update the cumulative record of classification margins
     *
     * @param predictedDistribution the probability distribution predicted for
     * the current instance
     * @param actualClass the index of the actual instance class
     * @param weight the weight assigned to the instance
     */
    private void updateMargins(double[] predictedDistribution,
                               int actualClass, double weight) {

        double probActual = predictedDistribution[actualClass];
        double probNext = 0;

        for (int i = 0; i < m_NumClasses; i++) {
            if ((i != actualClass) &&
                (predictedDistribution[i] > probNext)) {
                probNext = predictedDistribution[i];
            }
        }

        double margin = probActual - probNext;
        int bin = (int) ((margin + 1.0) / 2.0 * k_MarginResolution);
        m_MarginCounts[bin] += weight;
    }

    /**
     * Update the numeric accuracy measures. For numeric classes, the
     * accuracy is between the actual and predicted class values. For
     * nominal classes, the accuracy is between the actual and
     * predicted class probabilities.
     *
     * @param predicted the predicted values
     * @param actual the actual value
     * @param weight the weight associated with this prediction
     */
    private void updateNumericScores(double[] predicted,
                                     double[] actual, double weight) {

        double diff;
        double sumErr = 0, sumAbsErr = 0, sumSqrErr = 0;
        double sumPriorAbsErr = 0, sumPriorSqrErr = 0;
        for (int i = 0; i < m_NumClasses; i++) {
            diff = predicted[i] - actual[i];
            sumErr += diff;
            sumAbsErr += Math.abs(diff);
            sumSqrErr += diff * diff;
            diff = (m_ClassPriors[i] / m_ClassPriorsSum) - actual[i];
            sumPriorAbsErr += Math.abs(diff);
            sumPriorSqrErr += diff * diff;
        }
        m_SumErr += weight * sumErr / m_NumClasses;
        m_SumAbsErr += weight * sumAbsErr / m_NumClasses;
        m_SumSqrErr += weight * sumSqrErr / m_NumClasses;
        m_SumPriorAbsErr += weight * sumPriorAbsErr / m_NumClasses;
        m_SumPriorSqrErr += weight * sumPriorSqrErr / m_NumClasses;
    }

    /**
     * Adds a numeric (non-missing) training class value and weight to
     * the buffer of stored values.
     *
     * @param classValue the class value
     * @param weight the instance weight
     */
    private void addNumericTrainClass(double classValue, double weight) {

        if (m_TrainClassVals == null) {
            m_TrainClassVals = new double[100];
            m_TrainClassWeights = new double[100];
        }
        if (m_NumTrainClassVals == m_TrainClassVals.length) {
            double[] temp = new double[m_TrainClassVals.length * 2];
            System.arraycopy(m_TrainClassVals, 0,
                             temp, 0, m_TrainClassVals.length);
            m_TrainClassVals = temp;

            temp = new double[m_TrainClassWeights.length * 2];
            System.arraycopy(m_TrainClassWeights, 0,
                             temp, 0, m_TrainClassWeights.length);
            m_TrainClassWeights = temp;
        }
        m_TrainClassVals[m_NumTrainClassVals] = classValue;
        m_TrainClassWeights[m_NumTrainClassVals] = weight;
        m_NumTrainClassVals++;
    }

    /**
     * Sets up the priors for numeric class attributes from the
     * training class values that have been seen so far.
     */
    private void setNumericPriorsFromBuffer() {

        double numPrecision = 0.01; // Default value
        if (m_NumTrainClassVals > 1) {
            double[] temp = new double[m_NumTrainClassVals];
            System.arraycopy(m_TrainClassVals, 0, temp, 0, m_NumTrainClassVals);
            int[] index = M5StaticUtils.sort(temp);
            double lastVal = temp[index[0]];
            double currentVal, deltaSum = 0;
            int distinct = 0;
            for (int i = 1; i < temp.length; i++) {
                double current = temp[index[i]];
                if (current != lastVal) {
                    deltaSum += current - lastVal;
                    lastVal = current;
                    distinct++;
                }
            }
            if (distinct > 0) {
                numPrecision = deltaSum / distinct;
            }
        }
        m_PriorErrorEstimator = new M5Kernel(numPrecision);
        m_ErrorEstimator = new M5Kernel(numPrecision);
        m_ClassPriors[0] = m_ClassPriorsSum = 0.0001; // zf correction
        for (int i = 0; i < m_NumTrainClassVals; i++) {
            m_ClassPriors[0] += m_TrainClassVals[i] * m_TrainClassWeights[i];
            m_ClassPriorsSum += m_TrainClassWeights[i];
            m_PriorErrorEstimator.addValue(m_TrainClassVals[i],
                                           m_TrainClassWeights[i]);
        }
    }

}


