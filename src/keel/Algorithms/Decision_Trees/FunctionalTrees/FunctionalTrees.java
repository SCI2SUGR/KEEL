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

package keel.Algorithms.Decision_Trees.FunctionalTrees;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.io.*;
import org.core.Fichero;

/** 
 * The Functional Trees algorithm builds a decision tree model integrating in only
 * one model a decision tree and another classifier. The current version is called
 * FT-Leaves because the classifier is only present at the leaf nodes.
 * 
 * The params that can be used with this algorithm are various:
 * - minNumInstancesToSplit, the minimum number of instances that a node should have
 * to be considered for split
 * - splitCriteria, which is the criteria used to decide which is the best split for
 * a node. We consider criterias such as entropy, information gain, gini index or gain
 * ratio
 * - pruneCriteria, which is the criteria used to prune the tree when it has been built.
 * The criteria used depends on a general error on the tree or a prune for all the leaves
 * - classifierOnLeaves, the classifier that is on the leaves of the tree. The current
 * version only supports Naive Bayes, KNN, Nearest Means, KSNN and KNN Adaptive
 * - K, the parameter for some of the classifiers on the leaves
 *  
 * @author Written by Victoria Lopez Morales (University of Granada) 24/05/2009 
 * @version 0.1 
 * @since JDK1.5
 */
public class FunctionalTrees {
    /**
     * Nodes of the tree built with the Functional Trees algorithm
     */
    private TreeNode root;
    
    // Files
    /**
     * Array of files that include the name of the output file for train, test and other output
     */
    private String outFile[];
    /**
     * Name of the file that contains the test instances
     */
    private String testFile;
    /**
     * Name of the file that contains the original train instances
     */
    private String trainFile;
    /**
     * Name of the file that contains the reference instantes (current train instances)
     */
    private String referenceFile;
    
    // Datasets
    /**
     * Dataset containing all the test instances
     */
    private myDataset testDataset;
    /**
     * Dataset containing all the original train instances
     */
    private myDataset trainDataset;
    /**
     * Dataset containing all the reference instances (current train instances)
     */
    private myDataset referenceDataset;
    
    // Timing
    /**
     * Number used to store the time of the beginning of the algorithm
     */
    private long initialTime;
    /**
     * Seconds used to classify all the training instances
     */
    private double classificationTrainTime;
    /**
     * Seconds used to classify all the test instances
     */
    private double classificationTestTime;
    /**
     * Seconds used to build the tree
     */
    private double buildingTime;
    
    // Classified
    /**
     * Number of correctly classified train instances
     */
    private int correctTrain;
    /**
     * Number of incorrectly classified train instances
     */
    private int failTrain;
    /**
     * Number of correctly classified test instances
     */
    private int correctTest;
    /**
     * Number of incorrectly classified test instances
     */
    private int failTest;
    
    // Other parameters
    /**
     * User parameter: the minimum number of instances that a node should have to be considered for split
     */
    int minNumInstances;
    /**
     * User parameter: the criteria used to decide which is the best split for a node. We consider 
     * criterias such as entropy, information gain, gini index or gain ratio
     */
    int splitCriteria;
    /**
     * User parameter: criteria used to prune the tree when it has been built. The criteria used 
     * depends on a general error on the tree or a prune for all the leaves
     */
    int pruneCriteria;
    /**
     * User parameter: the classifier that is on the leaves of the tree. The current version only
     * supports Naive Bayes, KNN, Nearest Means, KSNN and KNN Adaptive
     */
    int leavesClassifier;
    /**
     * User parameter: the parameter for some of the classifiers on the leaves
     */
    int K;
    
    /**
     * Number of nodes of the tree during the building stage
     */
    int numnodes;
    
    /** 
     * Creates a FunctionalTrees instance by reading the script file that contains all the 
     * information needed for running the algorithm
     *
     * @param script    The configuration script which contains the parameters of the algorithm
     */     
    public FunctionalTrees (String script)  {
        // We start time-counting
        initialTime = System.currentTimeMillis();
        
        // Read of the script file
        readConfiguration(script); // Names of the input and output files  
        readParameters(script); // Parameters for the Functional Trees algorithm
        
        // Reading datasets
        try {
            trainDataset = new myDataset(trainFile, 1);
            testDataset = new myDataset(testFile, 3);
            referenceDataset = new myDataset(referenceFile, 2);
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }
        
        // Start building the tree
        buildTree();
    } // end-method
    
    /**
     * This method builds the tree with all the data stored in the class, in a process that first
     * builds the tree and then prunes it, obtaining finally a tree with classifiers at the leaves
     * that can be used to classify different instances
     */
    private void buildTree () {
        long buildTime;
        
        // Initialize root node using training data set
        System.out.println("\nInitializing root node");
        buildTime = System.currentTimeMillis();
        
        root = new TreeNode (0, null, null, false, -1, null, trainDataset, leavesClassifier, K);
        System.out.println("Root node initialized");
        numnodes = 1;
        
        // Build the tree
        growTree(root);

        // After the tree is built, we prune the tree
        System.out.println("\nBeginning prune...");
        pruneTree();
        System.out.println("Prune finished!");
        
        // Check the time spent during the tree building
        buildingTime = (double)(System.currentTimeMillis()-buildTime)/1000.0;
        
        System.out.println("\nBuilding of the tree finished!!");
        System.out.println(numnodes + " nodes generated");
    }
    
    /**
     * Builds the tree from a tree node that functions as a root node, with all the data stored in
     * the class
     * 
     * @param node  Tree node that is considered as a root node from which we are generating descendant
     * nodes
     */
    public void growTree (TreeNode node) {
        Split best_split;
        ArrayList <TreeNode> nodes;
        
        System.out.println("\nBeginning node processing...");
        
        // Check if the node is partitionable 
        if (node.isPartitionable(minNumInstances)) {
            // for each attribute A
            // Evaluate splits on attribute A
            best_split = node.evaluateAllSplits (splitCriteria);
                
            // Use best split to split node N into N1 and N2
            nodes = node.split(best_split, numnodes);
                
            if (nodes == null) {
                // The split cannot be done, so this node will be a leaf
                node.setAsLeaf();
            }
            else {
                // The split is done, and two new nodes are created
                numnodes += 2;
                
                // We grow the tree from those new nodes
                growTree (nodes.get(0));
                growTree (nodes.get(1));
            }
        }
        else {
            // The node is not partitionable, set as a leaf node
            node.setAsLeaf();
        }
    }
    
    /**
     * Prunes the tree accordingly to the prune criteria, this means, makes some of the non-leaf nodes
     * as leaves and deletes its descendants
     */
    public void pruneTree() {
        if (pruneCriteria == 0) { // The prune we have to do is a prune of all leaves
            root.pruneAllLeaves();
        }
        else if (pruneCriteria == 1) { // The prune we have to do is for all leaves
            root.pruneWithError();
        }
    }
    
    /**
     * This method performs the classification for all the instances: the train and the test sets
     */
    public void execute () {
        System.out.println();
        System.out.println("Beginning classification...");
        System.out.println();
     
        // Classify the train set
        print(referenceDataset, outFile[0], 0);
        // Classify the test set
        print(testDataset, outFile[1], 1);
        // Print other results like the performance of the algorithm and the tree
        printResults(trainDataset, outFile[2]);

        System.out.println("Classification FINISHED!!");
        System.out.println();
        
        System.out.println(getStatistical());
    } // end-method

    /** 
     * Reads configuration script, and extracts its contents.
     * 
     * @param script Name of the configuration script  
     */ 
    protected void readConfiguration (String script) {
        String fichero, linea, token;
        StringTokenizer lineasFichero, tokens;
        byte line[];
        int i, j;

        outFile = new String[3];

        fichero = Fichero.leeFichero (script);
        lineasFichero = new StringTokenizer (fichero,"\n\r");

        lineasFichero.nextToken();
        linea = lineasFichero.nextToken();

        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        token = tokens.nextToken();

        //Getting the names of training and test files
        //reference file will be used as comparison
        
        line = token.getBytes();
        for (i=0; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        trainFile = new String (line,i,j-i);
        for (i=j+1; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        referenceFile = new String (line,i,j-i);
        for (i=j+1; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        testFile = new String (line,i,j-i);

        //Getting the path and base name of the results files
        
        linea = lineasFichero.nextToken();
        tokens = new StringTokenizer (linea, "=");
        tokens.nextToken();
        token = tokens.nextToken();

        //Getting the names of output files
        
        line = token.getBytes();
        for (i=0; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        outFile[0] = new String (line,i,j-i);
        for (i=j+1; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        outFile[1] = new String (line,i,j-i);
        for (i=j+1; line[i]!='\"'; i++);
        i++;
        for (j=i; line[j]!='\"'; j++);
        outFile[2] = new String (line,i,j-i);
        
    } //end-method
    
    /** 
     * Reads configuration script, to extract the parameter's values.
     * 
     * @param script Name of the configuration script  
     * 
     */ 
    protected void readParameters (String script) {
        String file;
        String line;
        String str_splitCriteria, str_pruneCriteria, str_leavesClassifier;
        StringTokenizer fileLines, tokens;
        
        file = Fichero.leeFichero (script);
        fileLines = new StringTokenizer (file,"\n\r");
        
        // Discard in/out files definition
        fileLines.nextToken();
        fileLines.nextToken();
        fileLines.nextToken();
        //fileLines.nextToken();

        // Getting the number of minimum instances to perform a split
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        minNumInstances = Integer.parseInt(tokens.nextToken().substring(1));
        System.out.println ("The minimum number of instances to let split a node is " + minNumInstances);
        if (minNumInstances < 1) {
            System.err.println("Error: The minimum number of instances in a node to be partitioned is at least 1");
            System.exit(-1);
        }

        // Getting the split criteria used in the algorithm  
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        str_splitCriteria = tokens.nextToken().trim(); 
        
        if (str_splitCriteria.equals("Entropy")) {
            splitCriteria = 0;
        }
        else if (str_splitCriteria.equals("InformationGain")) {
            splitCriteria = 1;
        }
        else if (str_splitCriteria.equals("GiniIndex")) {
            splitCriteria = 2;
        }
        else if (str_splitCriteria.equals("GainRatio")) {
            splitCriteria = 3;
        }
        else {
            System.err.println("Error: The different ways to calculate a split in Functional Trees are \"Entropy\", \"InformationGain\", \"GiniIndex\" or \"GainRatio\"");
            System.exit(-1);
        }
        System.out.println("The split criteria is " + str_splitCriteria);
     
        // Getting the prune criteria used in the algorithm  
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        str_pruneCriteria = tokens.nextToken().trim(); 
        
        if (str_pruneCriteria.equals("pruneAllLeaves")) {
            pruneCriteria = 0;
        }
        else if (str_pruneCriteria.equals("pruneErrorLeaves")) {
            pruneCriteria = 1;
        }
        else {
            System.err.println("Error: The different ways to prune in Functional Trees are \"pruneAllLeaves\" or \"pruneErrorLeaves\"");
            System.exit(-1);
        }
        System.out.println("The prune criteria is " + str_pruneCriteria);
        
        // Getting the classifier used in the leaves of the tree  
        line = fileLines.nextToken();
        tokens = new StringTokenizer (line, "=");
        tokens.nextToken();
        str_leavesClassifier = tokens.nextToken().trim(); 
        
        if (str_leavesClassifier.equals("NaiveBayes")) {
            leavesClassifier = 0;
        }
        else if (str_leavesClassifier.equals("KNN")) {
            leavesClassifier = 1;
        }
        else if (str_leavesClassifier.equals("NM")) {
            leavesClassifier = 2;
        }
        else if (str_leavesClassifier.equals("KSNN")) {
            leavesClassifier = 3;
        }
        else if (str_leavesClassifier.equals("KNNAdaptive")) {
            leavesClassifier = 4;
        }
        else {
            System.err.println("Error: The different classifiers that can be used in Functional Trees are are \"NaiveBayes\", \"KNN\", \"NM\", \"KSNN\" or \"KNNAdaptive\"");
            System.exit(-1);
        }
        System.out.println("The classifier used at leaves is " + str_leavesClassifier);
        
        // Getting the parameters for the classifier selected if necessary
        if ((leavesClassifier == 1) || (leavesClassifier == 3) || (leavesClassifier == 4)) {
            line = fileLines.nextToken();
            tokens = new StringTokenizer (line, "=");
            tokens.nextToken();
            
            K = Integer.parseInt(tokens.nextToken().substring(1));
            System.out.println ("The number of neighboors in the selected classifier is " + K);
            if (K < 1) {
                System.err.println("Error: The minimum number of neighboors in the selected classifier is at least 1");
                System.exit(-1);
            }
        }

        System.out.println();
    } //end-method
    
    /**
     * Gets the general information about the dataset in a string form
     * 
     * @param dat   Dataset from which we are obtaining the general information
     * @return a string with the general information about the dataset
     */
    private String getHeader (myDataset dat) {
        String header;
        ArrayList <myAttribute> attributes;
        myAttribute output;
        
        attributes = dat.getAttributes();
        output = dat.getOutputAttribute();
     
        // Get information about the dataset and the attributes
        header = "@relation " + dat.getName() + "\n";
        for (int i=0; i<attributes.size(); i++) {
            switch (attributes.get(i).getAttributeType()) {
                case 1: header += "@attribute " + attributes.get(i).getName() + " integer[" + (int)attributes.get(i).getMin() + "," + (int)attributes.get(i).getMax() + "]\n";
                    break;
                case 2: header += "@attribute " + attributes.get(i).getName() + " real[" + attributes.get(i).getMin() + "," + attributes.get(i).getMax() + "]\n";
                    break;
                case 3: header += "@attribute " + attributes.get(i).getName() + " {";
                    for (int j=0; j<attributes.get(i).getValues().size()-1; j++) {
                        header += attributes.get(i).getValue(j) + ",";
                    }
                    header += attributes.get(i).getValue(attributes.get(i).getValues().size()-1) + "}\n";
                    break;
            }
        }
        
        // Get information about the output attribute
        switch (output.getAttributeType()) {
            case 1: header += "@attribute " + output.getName() + " integer[" + (int)output.getMin() + "," + (int)output.getMax() + "]\n";
                break;
            case 2: header += "@attribute " + output.getName() + " real[" + output.getMin() + "," + output.getMax() + "]\n";
                break;
            case 3: header += "@attribute " + output.getName() + " {";
                for (int j=0; j<output.getValues().size()-1; j++) {
                    header += output.getValue(j) + ",";
                }
                header += output.getValue(output.getValues().size()-1) + "}\n";
                break;
        }
        
        return header;
    }
    
    /**
     * Classifies a given item with the information stored in the tree
     * 
     * @param item  Data attribute values for the item we are classifying
     *
     * @return the class asigned to the item given
     */
    public int evaluateItem (double [] item) {
        return root.evaluate(item);
    }
    
    /**
     * Prints in a file the result of the classification made with the tree generated by the PUBLIC
     * algorithm. This can be done over the train set or the test set.
     * 
     * @param data Dataset that we are classifying
     * @param filename Name of the file that is going to store the results
     * @param type 0 if we are working with a train set, 1 if we are working with a test set
     */
    public void print (myDataset data, String filename, int type) {
        String text = getHeader(data);
        double item[];
        int correct, fail;
        long time;
        
        text += "@data\n";
        
        item = new double[data.getNumAtr()];
        correct = 0;
        fail = 0;
        
        // Check the time spent
        time = System.currentTimeMillis();
        
        for (int i = 0; i < data.getNumIns(); i++) {
            // Evaluate all the instances
            try {
                item = data.getDataItem (i);
                int cl = (int) evaluateItem(item);
                
                if (cl == (int) data.getOutputI(i)) {
                    correct++;
                }
                else {
                    fail++;
                }

                text += data.getOutputAttribute().getValue((int)data.getOutputI(i)) + " " + data.getOutputAttribute().getValue(cl)+ "\n";
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
     
        // Print the corresponding results
        if (type == 0) {
            classificationTrainTime = (double)(System.currentTimeMillis()-time)/1000.0;
            correctTrain = correct;
            failTrain = fail;
        }
        else if (type == 1) {
            classificationTestTime = (double)(System.currentTimeMillis()-time)/1000.0;
            correctTest = correct;
            failTest = fail;
        }
        else {
            System.err.println("Wrong dataset for printing results");
            System.exit(-1);
        }

        try {
            PrintWriter print = new PrintWriter(new FileWriter(filename));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the output file " + filename + ": " + e.getMessage());
        }
    }   
    
    /**
     * Gets the general information about the performance of the algorithm. This information includes
     * the number of nodes and leafs of the tree, the performance in training and test and the time
     * spent in the operations.
     * 
     * @return a string with all the important information about the performance of the algorithm
     */
    private String getStatistical () {
        String text = "";
        
        text = text + "@TotalNumberOfNodes " + root.getNumNodes() + "\n";
        text = text + "@NumberOfLeafs " + root.getLeafs() + "\n\n";
        
        text = text + "@NumberOfItemsetsTraining " + referenceDataset.getNumIns() + "\n";
        text = text + "@NumberOfCorrectlyClassifiedTraining " + correctTrain + "\n";
        text = text + "@PercentageOfCorrectlyClassifiedTraining " + ((double)correctTrain*100.0/(double)referenceDataset.getNumIns()) + "%\n";
        text = text + "@NumberOfIncorrectlyClassifiedTraining " + failTrain + "\n";
        text = text + "@PercentageOfIncorrectlyClassifiedTraining " + ((double)failTrain*100.0/(double)referenceDataset.getNumIns()) + "%\n\n";
         
        text = text + "@NumberOfItemsetsTest " + testDataset.getNumIns() + "\n";
        text = text + "@NumberOfCorrectlyClassifiedTest " + correctTest + "\n";
        text = text + "@PercentageOfCorrectlyClassifiedTest " + ((double)correctTest*100.0/(double)testDataset.getNumIns()) + "%\n";
        text = text + "@NumberOfIncorrectlyClassifiedTest " + failTest + "\n";
        text = text + "@PercentageOfIncorrectlyClassifiedTest " + ((double)failTest*100.0/(double)testDataset.getNumIns()) + "%\n\n";

        text = text + "@TotalElapsedTime " + (double)(System.currentTimeMillis()-initialTime)/1000.0 + "s\n";
        text = text + "@BuildingElapsedTime " + buildingTime + "s\n";
        text = text + "@ClassificationTrainElapsedTime " + classificationTrainTime + "s\n";
        text = text + "@ClassificationTestElapsedTime " + classificationTestTime + "s\n";
        
        return text;
    }
    
    /**
     * Prints in a file the result of the classification made with the tree generated by the PUBLIC
     * algorithm, this means, the tree itself and the general information about it
     * 
     * @param data Dataset that we are working with
     * @param filename Name of the file that is going to store the results
     */
    public void printResults (myDataset data, String filename) {
        String text = getHeader(data);
        
        text += "@inputs\n";
        for (int i=0; i<data.getAttributes().size(); i++) {
            text = text + data.getAttributes().get(i).getName() + " ";
        }
        text = text + "\n@outputs " + data.getOutputAttribute().getName() + "\n@data\n\n@decisiontree\n\n" + root.printTree() + "\n";
        
        text += getStatistical ();
        
        try {
            PrintWriter print = new PrintWriter(new FileWriter(filename));
            print.print(text);
            print.close();
        } catch (IOException e) {
            System.err.println("Can not open the output file " + filename + ": " + e.getMessage());
        }
    }
}


