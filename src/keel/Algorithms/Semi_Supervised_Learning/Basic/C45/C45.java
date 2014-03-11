/**
 * <p>
 * @author Written by Jose A. Saez Munoz, research group SCI2S (Soft Computing and Intelligent Information Systems).
 * DECSAI (DEpartment of Computer Science and Artificial Intelligence), University of Granada - Spain.
 * @author ISaac Triguero.
 * @author Modified by Victoria Lopez (University of Granada) 28/03/2012  
 * Date: 22/02/11
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Semi_Supervised_Learning.Basic.C45;

import java.io.*;

import keel.Algorithms.Decision_Trees.C45.Algorithm;

import keel.Dataset.Attributes;
import keel.Dataset.InstanceSet;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.TreeSet;


/**
 * Class to implement the C4.5 algorithm
 */
public class C45 extends Algorithm {
    /** Decision tree. */
    private Tree root;

    /** Is the tree pruned or not. */
    private boolean prune = true;

    /** Confidence level. */
    private float confidence = 0.25f;

    /** Minimum number of itemsets per leaf. */
    private int minItemsets = 2;

    /** The prior probabilities of the classes. */
    private double[] priorsProbabilities;

    /** Resolution of the margin histogram. */
    private static int marginResolution = 500;

    /** Cumulative margin classification. */
    private double marginCounts[];

    /** The sum of counts for priors. */
    private double classPriorsSum;

    /** The model dataset. */
    protected Dataset modelDataset;

    /** The train dataset. */
    protected Dataset trainDataset;

    /** The test dataset. */
    protected Dataset testDataset;
    
    /** Constructor.
     *
     * @param paramFile		The parameters file.
     *
     * @throws Exception	If the algorithm cannot be executed.
     */
    public C45(String trainfn, String testfn) throws Exception {
        try {

            // starts the time
            long startTime = System.currentTimeMillis();

            /* Sets the options of the execution from text file*/
            setOptions(trainfn, testfn);
            
            /* Initializes the dataset. */
        	Attributes.clearAll();
            modelDataset = new Dataset(modelFileName, true);
            trainDataset = new Dataset(trainFileName, false);
            testDataset = new Dataset(testFileName, false);

            priorsProbabilities = new double[modelDataset.numClasses()];
            priorsProbabilities();
            marginCounts = new double[marginResolution + 1];

            // generate the tree
            generateTree(modelDataset);

            //printTrain();
            //printTest();
            //printResult();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit( -1);
        }

    }

    
    /** Constructor.
    *
    * @param paramFile		The parameters file.
    *
    * @throws Exception	If the algorithm cannot be executed.
    */
   public C45(InstanceSet trainfn, InstanceSet testfn) throws Exception {
       try {

           // starts the time
           long startTime = System.currentTimeMillis();

           /* Sets the options of the execution from text file*/
           trainOutputFileName = "salidac45train.dat";
           testOutputFileName = "salidac45test.dat";
           resultFileName = "salidac45result.dat";

           prune = ParametersC45.prune;

           /* Checks that the confidence threshold is between 0 and 1. */
           confidence = (float)ParametersC45.confidence; // debe estar entre 0 y 1

           minItemsets = ParametersC45.itemsetsPerLeaf; // debe ser > 0
           
           /* Initializes the dataset. */
  
           modelDataset = new Dataset(trainfn);
           trainDataset = new Dataset(trainfn);
           testDataset = new Dataset(testfn);

           priorsProbabilities = new double[modelDataset.numClasses()];
           priorsProbabilities();
           marginCounts = new double[marginResolution + 1];

           // generate the tree
           generateTree(modelDataset);

        //   	System.out.println(modelDataset.numItemsets());
           //printTrain();
           //printTest();
           //printResult();
       } catch (Exception e) {
           System.err.println(e.getMessage());
           System.exit( -1);
       }

   }
   
    public int[] getPredictions(){
    	int[] classesp = new int[testDataset.numItemsets()];
    	 for(int i = 0 ; i < testDataset.numItemsets() ; ++i)
			try {
				classesp[i] = (int) evaluateItemset(testDataset.itemset(i));
			} catch (Exception e) {
				e.printStackTrace();
			}
	
		return classesp;
    }
    
    
    public double[][] getProbabilities(){
    	double [][] probabilities = new double[testDataset.numItemsets()][testDataset.numClasses()];
    	
    	
    	for(int i = 0 ; i < testDataset.numItemsets() ; i++){
    		try{
    			Itemset classMissing = (Itemset) testDataset.itemset(i).copy();
    			double prediction = 0;
    			classMissing.setDataset(testDataset.itemset(i).getDataset());
    			classMissing.setClassMissing();
            
    			double[] classification = classificationForItemset(classMissing);
    			
    			probabilities[i] = classification;
    			
    		}catch (Exception e) {
				e.printStackTrace();
			}
    	}
    	


		return probabilities;
    }
    

    protected void setOptions(StreamTokenizer option) throws Exception {}


    /** Function to read the options from the execution file and assign the values to the parameters.
     *
     * @param options 		The StreamTokenizer that reads the parameters file.
     *
     * @throws Exception	If the format of the file is not correct.
     */
    protected void setOptions(String trainfn, String testfn) throws Exception {
    	
        modelFileName = trainfn;
        trainFileName = trainfn;
        testFileName = testfn;
        
        trainOutputFileName = "salidac45train.dat";
        testOutputFileName = "salidac45test.dat";
        resultFileName = "salidac45result.dat";

        prune = ParametersC45.prune;

        /* Checks that the confidence threshold is between 0 and 1. */
        confidence = (float)ParametersC45.confidence; // debe estar entre 0 y 1

        minItemsets = ParametersC45.itemsetsPerLeaf; // debe ser > 0
    }


    /** Generates the tree.
     *
     * @param itemsets		The dataset used to build the tree.
     *
     * @throws Exception	If the tree cannot be built.
     */
    public void generateTree(Dataset itemsets) throws Exception {
        SelectCut selectCut;

        selectCut = new SelectCut(minItemsets, itemsets);
        root = new Tree(selectCut, prune, confidence);
        root.buildTree(itemsets);
    }

    /** Function to evaluate the class which the itemset must have according to the classification of the tree.
     *
     * @param itemset		The itemset to evaluate.
     *
     * @return				The index of the class index predicted.
     */
    public double evaluateItemset(Itemset itemset) throws Exception {
        Itemset classMissing = (Itemset) itemset.copy();
        double prediction = 0;
        classMissing.setDataset(itemset.getDataset());
        classMissing.setClassMissing();

        double[] classification = classificationForItemset(classMissing); // ESTO LO QUE YO KIERO!!
        
        prediction = maxIndex(classification);
        updateStats(classification, itemset, itemset.numClasses());

        //itemset.setPredictedValue( prediction );

        return prediction;
    }

    /** Updates all the statistics for the current itemset.
     *
     * @param predictedClassification	Distribution of class values predicted for the itemset.
     * @param itemset					The itemset.
     * @param nClasses					The number of classes.
     *
     */
    private void updateStats(double[] predictedClassification, Itemset itemset,
                             int nClasses) {
        int actualClass = (int) itemset.getClassValue();

        if (!itemset.classIsMissing()) {
            updateMargins(predictedClassification, actualClass, nClasses);

            // Determine the predicted class (doesn't detect multiple classifications)
            int predictedClass = -1;
            double bestProb = 0.0;

            for (int i = 0; i < nClasses; i++) {
                if (predictedClassification[i] > bestProb) {
                    predictedClass = i;
                    bestProb = predictedClassification[i];
                }
            }

            // Update counts when no class was predicted
            if (predictedClass < 0) {
                return;
            }

            double predictedProb = Math.max(Double.MIN_VALUE,
                                            predictedClassification[actualClass]);
            double priorProb = Math.max(Double.MIN_VALUE,
                                        priorsProbabilities[actualClass] /
                                        classPriorsSum);
        }
    }

    /** Returns class probabilities for an itemset.
     *
     * @param itemset		The itemset.
     *
     * @throws Exception	If cannot compute the classification.
     */
    public final double[] classificationForItemset(Itemset itemset) throws
            Exception {
        return root.classificationForItemset(itemset);
    }

    /** Update the cumulative record of classification margins.
     *
     * @param predictedClassification	Distribution of class values predicted for the itemset.
     * @param actualClass				The class value.
     * @param nClasses					Number of classes.
     */
    private void updateMargins(double[] predictedClassification,
                               int actualClass, int nClasses) {
        double probActual = predictedClassification[actualClass];
        double probNext = 0;

        for (int i = 0; i < nClasses; i++) {
            if ((i != actualClass) && ( //Comparators.isGreater( predictedClassification[i], probNext ) ) )
                    predictedClassification[i] > probNext)) {
                probNext = predictedClassification[i];
            }
        }

        double margin = probActual - probNext;
        int bin = (int) ((margin + 1.0) / 2.0 * marginResolution);
        marginCounts[bin]++;
    }

    /** Evaluates if a string is a boolean value.
     *
     * @param value		The string to evaluate.
     *
     * @return			True if value is a boolean value. False otherwise.
     */
    private boolean isBoolean(String value) {
        if (value.equalsIgnoreCase("TRUE") || value.equalsIgnoreCase("FALSE")) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns index of maximum element in a given array of doubles. First maximum is returned.
     *
     * @param doubles		The array of elements.
     *
     */
    public static int maxIndex(double[] doubles) {
        double maximum = 0;
        int maxIndex = 0;

        for (int i = 0; i < doubles.length; i++) {
            if ((i == 0) || //
                doubles[i] > maximum) {
                maxIndex = i;
                maximum = doubles[i];
            }
        }

        return maxIndex;
    }

    /** Sets the class prior probabilities.
     *
     * @throws Exception	If cannot compute the probabilities.
     */
    public void priorsProbabilities() throws Exception {
        for (int i = 0; i < modelDataset.numClasses(); i++) {
            priorsProbabilities[i] = 1;
        }

        classPriorsSum = modelDataset.numClasses();

        for (int i = 0; i < modelDataset.numItemsets(); i++) {
            if (!modelDataset.itemset(i).classIsMissing()) {
                try {
                    priorsProbabilities[(int) modelDataset.itemset(i).
                            getClassValue()] += modelDataset.itemset(i).
                            getWeight();
                    classPriorsSum += modelDataset.itemset(i).getWeight();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
    
    public boolean [] selectedTrainingInstances () {
    	int leafs, current_leaf_id;
    	boolean [] selected = new boolean [modelDataset.numItemsets()];
    	int [] leaf = new int [modelDataset.numItemsets()];
    	TreeSet <Integer> leafs_ids = new TreeSet <Integer> ();
    	ArrayList <Integer> leafs_id = new ArrayList <Integer> ();
    	ArrayList <ArrayList <Integer>> clusters;
    	
    	Arrays.fill(selected, false);
    	
    	for (int i=0; i<modelDataset.numItemsets(); i++) {
    		leaf[i] = root.classifyingLeaf (modelDataset.itemset(i));
    		leafs_ids.add(leaf[i]);
    	}
    	
    	leafs = leafs_ids.size();    	
    	leafs_id = new ArrayList <Integer> (leafs);
    	clusters = new ArrayList <ArrayList <Integer>> (leafs);
    	for (int i=0; i<leafs; i++) {
    		clusters.add(new ArrayList <Integer> ());
    		current_leaf_id = leafs_ids.first();
    		leafs_id.add(current_leaf_id);
    		leafs_ids.remove(current_leaf_id);
    	}
    	
    	for (int i=0; i<modelDataset.numItemsets(); i++) {
    		clusters.get(leafs_id.indexOf(leaf[i])).add(i);
    	}
    	
    	for (int i=0; i<clusters.size(); i++) {
    		selected[getCentroid(clusters.get(i))] = true;
    	}    	
    	
    	return selected;
    }

    private int getCentroid (ArrayList <Integer> clusterIds) {
    	double [] centroid_values = new double [modelDataset.numAttributes()];
    	int nearest_centroid;
    	double distance_centroid, distance;    	

    	// Compute a centroid of real values
		Arrays.fill(centroid_values, 0.0);
		
		for (int i=0; i<clusterIds.size(); i++) {
			for (int j=0; j<modelDataset.numAttributes(); j++) {
				centroid_values[j] += modelDataset.itemset(clusterIds.get(i)).getValue(j);
			}
		}
		
		for (int j=0; j<modelDataset.numAttributes(); j++) {
			centroid_values[j] /= clusterIds.size();
		}

		// Search for the nearest instance to the centroid
		nearest_centroid = clusterIds.get(0);
		distance_centroid = 0.0;
		for (int i=0; i<modelDataset.numAttributes(); i++) {
			distance_centroid += (modelDataset.itemset(clusterIds.get(0)).getValue(i)-centroid_values[i])*(modelDataset.itemset(clusterIds.get(0)).getValue(i)-centroid_values[i]);
		}
		distance_centroid = Math.sqrt(distance_centroid);
		
		for (int j=1; j<clusterIds.size(); j++) {
			distance = 0.0;
			for (int i=0; i<modelDataset.numAttributes(); i++) {
				distance += (modelDataset.itemset(clusterIds.get(j)).getValue(i)-centroid_values[i])*(modelDataset.itemset(clusterIds.get(j)).getValue(i)-centroid_values[i]);
			}
			distance = Math.sqrt(distance);
			
			if (distance < distance_centroid) {
				nearest_centroid = clusterIds.get(j);
				distance_centroid = distance;
			}
		}
    	
    	return nearest_centroid;
	}


    /** Writes the tree and the results of the training and the test in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printResult() throws IOException {
        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
        long seconds = totalTime % 60;
        long minutes = ((totalTime - seconds) % 3600) / 60;
        String tree = "";
        PrintWriter resultPrint;

        tree += toString();
        tree += "\n@TotalNumberOfNodes " + root.NumberOfNodes;
        tree += "\n@NumberOfLeafs " + root.NumberOfLeafs;

        tree += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
        tree += "\n@PercentageOfCorrectlyClassifiedTraining " +
                (float) (correct * 100.0) / (float) trainDataset.numItemsets() +
                "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTraining " +
                (trainDataset.numItemsets() - correct);
        tree += "\n@PercentageOfInCorrectlyClassifiedTraining " +
                (float) ((trainDataset.numItemsets() - correct) * 100.0) /
                (float) trainDataset.numItemsets() + "%";

        tree += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
        tree += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
        tree += "\n@PercentageOfCorrectlyClassifiedTest " +
                (float) (testCorrect * 100.0) / (float) testDataset.numItemsets() +
                "%";
        tree += "\n@NumberOfInCorrectlyClassifiedTest " +
                (testDataset.numItemsets() - testCorrect);
        tree += "\n@PercentageOfInCorrectlyClassifiedTest " +
                (float) ((testDataset.numItemsets() - testCorrect) * 100.0) /
                (float) testDataset.numItemsets() + "%";

        tree += "\n\n@ElapsedTime " +
                (totalTime - minutes * 60 - seconds) / 3600 + ":" +
                minutes / 60 + ":" + seconds;

        resultPrint = new PrintWriter(new FileWriter(resultFileName));
        resultPrint.print(getHeader() + "\n@decisiontree\n\n" + tree);
        resultPrint.close();
    }

    /** Evaluates the training dataset and writes the results in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printTrain() {
        String text = getHeader();

        for (int i = 0; i < trainDataset.numItemsets(); i++) {
            try {
                Itemset itemset = trainDataset.itemset(i);
                int cl = (int) evaluateItemset(itemset);

                if (cl == (int) itemset.getValue(trainDataset.getClassIndex())) {
                    correct++;
                }

                text += trainDataset.getClassAttribute().value(((int) itemset.
                        getClassValue())) + " " + trainDataset.getClassAttribute().value(cl)
                         + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(
                    trainOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the training output file: " +
                               e.getMessage());
        }
    }

    /** Evaluates the test dataset and writes the results in the file.
     *
     * @exception 	If the file cannot be written.
     */
    public void printTest() {
        String text = getHeader();

        for (int i = 0; i < testDataset.numItemsets(); i++) {
            try {
                int cl = (int) evaluateItemset(testDataset.itemset(i));
                Itemset itemset = testDataset.itemset(i);

                if (cl == (int) itemset.getValue(testDataset.getClassIndex())) {
                    testCorrect++;
                }

                text += testDataset.getClassAttribute().value(((int) itemset.
                        getClassValue())) + " " + testDataset.getClassAttribute().value(cl) + "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(
                    testOutputFileName));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the training output file.");
        }
    }


    /** Function to print the tree.
     *
     */
    public String toString() {
        return root.toString();
    }


}


