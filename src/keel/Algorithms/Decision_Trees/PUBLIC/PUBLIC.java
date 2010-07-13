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


package keel.Algorithms.Decision_Trees.PUBLIC;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.ArrayDeque;

import java.io.*;

import org.core.Fichero;

/**
 * 
 * File: PUBLIC.java
 * 
 * The PUBLIC algorithm builds a decision tree model integrating the steps of
 * building and pruning in one phase, and after the model is built, the
 * classification is done according to that model.
 * 
 * The params that can be used with this algorithm are basically two:
 * n, the number of nodes expanded between the pruning method is called
 * PUBLIC(1), PUBLIC(S), PUBLIC(V) are the variants of the algorithm depending
 * on the estimation on the lower bound for the subtree
 * 
 * @author Written by Victoria Lopez Morales (University of Granada) 13/03/2009 
 * @version 0.1 
 * @since JDK1.5
 */

public class PUBLIC {
	// Tree
    /**
     * Nodes of the tree built with the PUBLIC algorithm with minimal information
     */
    private TreeNode root;
    /**
     * Nodes of the tree built with the PUBLIC algorithm with complete information (including datasets)
     */
    private ArrayList <Node> all_nodes;

    /**
     * Queue used during the building of the tree, needed for building and pruning
     */
    private ArrayDeque <Node> queue;
    
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
	 * User parameter: number of nodes that have to be proccessed between the call to the prune procedure
	 */
	private int nodesBetweenPrune;
	/**
	 * User parameter: kind of prune estimation used in the prune procedure
	 */
	private char publicPruneEstimation;
	
    /** 
     * Creates a PUBLIC instance by reading the script file that contains all the information needed
     * for running the algorithm
     *
     * @param script    The configuration script which contains the parameters of the algorithm
     */     
    public PUBLIC (String script)  {
        // We start time-counting
        initialTime = System.currentTimeMillis();
        
        // Read of the script file
		readConfiguration(script); // Names of the input and output files  
		readParameters(script); // Parameters for the PUBLIC algorithm
		
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
    } 
    
    /**
     * This method builds the tree with all the data stored in the class, in a process that integrates
     * in one the building and the pruning phases, obtaining finally a tree that can be used to 
     * classify different instances
     */
    private void buildTree () {
        Node auxnode;
        TreeNode auxtreenode;
        ArrayList <Node> nodes;
        Split best_split;
        int numnodes, numnodesproccessed;
        long buildTime;
        
        all_nodes = new ArrayList <Node> ();
        
        // (1) Initialize root node using data set S
        System.out.println("\nInitializing root node");
        buildTime = System.currentTimeMillis();
        auxnode = initializeRootNode ();
        System.out.println("Root node initialized");
        numnodes = 1;
        numnodesproccessed = 0;
        
        // (2) Initialize queue Q to contain root node
        queue = new ArrayDeque <Node>();
        queue.add(auxnode);
        
        all_nodes.add(auxnode);
        
        // (3) While Q is not empty do
        while (!queue.isEmpty()) {
            // (4) Dequeue the first node N in Q
            auxnode = queue.poll();
            System.out.println("\nBeginning node processing...");

            // (5) if N is not pure 
            if (!auxnode.isPure()) {
                // (6) for each attribute A
                // (7) Evaluate splits on attribute A
                best_split = auxnode.evaluateAllSplits();
                
                // (8) Use best split to split node N into N1 and N2
                nodes = auxnode.split(best_split, numnodes);
                
                if (nodes == null) {
                    // The split cannot be done
                    auxtreenode = root.getNode(auxnode.getIdentifier());
                    auxtreenode.setLeaf(true);
                    auxtreenode.setOutputClass(auxnode.getMajorOutputClass());
                    numnodesproccessed++;
                }
                else {
                    // Arrange the tree to this information
                    auxtreenode = root.getNode(auxnode.getIdentifier());
                    auxtreenode.setLeft(new TreeNode(numnodes+1, null, null, false, -1, null));
                    auxtreenode.setRight(new TreeNode(numnodes+2, null, null, false, -1, null));
                    auxtreenode.setCondition(new Split(best_split));
                    
                    // (9) Append N1 and N2 to Q
                    queue.add((Node)nodes.get(0));
                    queue.add((Node)nodes.get(1));
                    all_nodes.add((Node)nodes.get(0));
                    all_nodes.add((Node)nodes.get(1));
                    numnodes += 2;
                    numnodesproccessed++;
                }
            }
            else {
                // This node is pure, we set it as a leaf node
                auxtreenode = root.getNode(auxnode.getIdentifier());
                auxtreenode.setLeaf(true);
                auxtreenode.setOutputClass(auxnode.getOutputClass());
                numnodesproccessed++;
            }
            
            if (numnodesproccessed%nodesBetweenPrune == 0) {
                // Start pruning
                System.out.println("\nBeginning pruning...");
                computeCostPrunePublic(root);
                System.out.println("Pruning phase finished!");
            }
        }

        // Before finishing the tree, make sure that the tree is perfectly pruned
        System.out.println("\nBeginning final prune...");
        computeCostPrunePublic(root);
        System.out.println("Last prune finished!");
        
        // Check the time spent during the tree building
        buildingTime = (double)(System.currentTimeMillis()-buildTime)/1000.0;
        
        System.out.println("\nBuilding of the tree finished!!");
        System.out.println(numnodes + " nodes generated");
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
	 * Reads the configuration script, and extracts its contents.
	 * 
	 * @param script   Name of the configuration script  
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

	    // Getting the names of training and test files
	    // reference file will be used as comparison
	    
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
	 * Reads the configuration script, to extract the parameter's values
	 * 
	 * @param script   Name of the configuration script  
	 * 
	 */	
	protected void readParameters (String script) {
		String file;
		String line;
		StringTokenizer fileLines, tokens;
		
	    file = Fichero.leeFichero (script);
	    fileLines = new StringTokenizer (file,"\n\r");
	    
	    // Discard in/out files definition
	    fileLines.nextToken();
	    fileLines.nextToken();
	    fileLines.nextToken();
	    //fileLines.nextToken();

	    // Getting the number of nodes generated between prune phases
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    nodesBetweenPrune = Integer.parseInt(tokens.nextToken().substring(1));
	    if (nodesBetweenPrune < 1) {
	    	System.err.println("Error: The minimum number of nodes that are generated between prunes is 1");
	    	System.exit(-1);
	    }

	    // Getting the version of the PUBLIC algorithm  
	    line = fileLines.nextToken();
	    tokens = new StringTokenizer (line, "=");
	    tokens.nextToken();
	    publicPruneEstimation = tokens.nextToken().substring(1).charAt(7); 
	    if ((publicPruneEstimation != '1') && (publicPruneEstimation != 'S') && (publicPruneEstimation != 'V')) {
	    	System.err.println("Error: The different ways to estimate the cost of the tree for pruning are PUBLIC(1), PUBLIC(S) or PUBLIC(V)");
	    	System.exit(-1);
	    }
	} //end-method
	
	/**
	 * Initializes the root node of the tree from the current train dataset
	 * 
	 * @return the root node of the tree
	 */
	private Node initializeRootNode () {
		Node auxnode;
		
		// Create a node with the whole dataset
		auxnode = new Node (trainDataset, 1);
		
		// Mark the node created as the root of the tree
		root = new TreeNode (1, null, null, false, -1, null);
		
		return auxnode;
	}
	
	/**
	 * Computes the estimated cost for the tree and prunes it accordingly to the MDL principle
	 * 
	 * @param node Tree node from which we are going to calculate the cost and prune
	 * @return the estimated cost of encoding the tree
	 */
	private double computeCostPrunePublic (TreeNode node) {
	    double minCost1, minCost2, minCostN, aux, costV1, costV2;
	    Node aux_node = null;
	    boolean found = false;
	    
	    // Get the corresponding node
        for (int i=0; i<all_nodes.size() && !found; i++) {
            aux_node = (Node)all_nodes.get(i);
            if (aux_node.getIdentifier() == node.getIdentifier()) {
                found = true;
            }
        }
        
        if ((node.getLeft() == null)&&(node.getRight() == null)&&(node.isLeaf() == false)) {
	        // This is a "yet to be expanded" leaf, get its lower bound cost
	        switch (publicPruneEstimation) {
	            case '1':
	                return 1;
	            case 'S':
	                return computeMinCostS(aux_node);
	            case 'V':
	                costV1 = computeMinCostV(aux_node);
	                costV2 = computeMinCostV2(aux_node);
	                if (costV1 < costV2)
	                    return costV1;
	                else
	                    return costV2;
	            default:
	                System.err.println("The prune estimation selected isn't correct");
	                System.exit(-1);
	                break;
	        }
	    }
	    
        // Check if the tree is correctly built
	    if (((node.getLeft() == null) && (node.getRight() != null)) || ((node.getLeft() != null) && (node.getRight() == null))) {
	        System.err.println("The node " + node.getIdentifier() + " is badly built");
	        System.exit(-1);
	    }
	    
	    if (node.isLeaf()) {
	        // This is a "pruned" or "not expandable" leaf
            return (C(aux_node) + 1); 
	    }
	    
	    minCost1 = computeCostPrunePublic(node.getLeft());
	    minCost2 = computeCostPrunePublic(node.getRight());
	    
	    minCostN = C_split(node, aux_node) + minCost1 + minCost2;
	    aux = C(aux_node) + 1;
	    
	    if (aux < minCostN)
	        minCostN = aux;
	    
	    if (minCostN == aux) {
	        ArrayList <Integer> nodesToRemove;
	        boolean removed;
	        
	        System.out.println("Node " + node.getIdentifier() + " and its children are pruned");
	        
	        // Prune child nodes N1 and N2 from tree
            // Delete nodes N1 and N2 and all their descendants from Q
	        nodesToRemove = node.deleteDescendants(node.getIdentifier());
	        
	        for (int i=0; i<nodesToRemove.size(); i++) {
	            removed = false;
	            for (int j=0; j<all_nodes.size() && !removed; j++) {
	                if (all_nodes.get(j).getIdentifier() == nodesToRemove.get(i)) {
	                    queue.remove((Node)all_nodes.get(j)); 
	                    all_nodes.remove(j);
                        removed = true;
	                }
	            }
	        }
	        
	        // Mark node N as pruned
	        node.setLeaf(true);
	        node.setOutputClass(aux_node.getMajorOutputClass());
	    }
	    
	    return minCostN;
	}
	
	/**
	 * Computes the cost of encoding data records used to estimate the cost of the tree
	 * 
	 * @param aux_node Node to which we are going to compute the cost of encoding data records
	 * @return cost of encoding data records in the corresponding node
	 */
	private double C(Node aux_node) {
	    double cost = 0;
	    int ni;
	    
	    // Calculate the first member of the cost
	    for (int i=0; i<trainDataset.getNumClasses(); i++) {
	        ni = aux_node.getNumItemsClassI(i);
	        if (ni != 0)
	            cost += (double) ni * (Math.log((double)aux_node.getNumRegisters()/(double)ni)/Math.log(2.0)); 
	    }
	    
	    // Calculate the second member of the cost
	    cost += (((double)(aux_node.getNumClasses()-1))/2.0) * (Math.log((double)aux_node.getNumRegisters()/2.0)/Math.log(2.0));
	    
	    // Calculate the third member of the cost
	    cost += (Math.log((double)Math.pow(Math.PI,(double)aux_node.getNumClasses()/2.0)/(double)gamma(aux_node.getNumClasses(), 2))/Math.log(2.0));
	    
	    return cost;
	}
	
	/**
	 * Computes the gamma function for a fractional number
	 * 
	 * @param dividend Dividend of the fractional number for which we are computing the gamma function
	 * @param divisor  Divisor of the fractional number for which we are computing the gamma function
	 * @return the gamma value of the fractional number given
	 */
	private double gamma (int dividend, int divisor) {
	    double gamma;
	    
	    if (divisor == 2) {
	        if ((dividend%2) == 0) {
	            // The number is divisible
	            gamma = factorial((dividend/divisor)-1);
	        }
	        else {
	            // The number is not divisible
	            if (dividend != 1)
	                gamma = Math.sqrt(Math.PI) * ((double)double_factorial(dividend-2)/Math.pow(2.0,(dividend-1)/2.0));
	            else
	                gamma = Math.sqrt(Math.PI);
	        }
            return gamma;
	    }
	    else {
	        System.err.println("This gamma function only computes integers or numbers divided by two");
	        System.exit(-1);
	        return 0.0;
	    }
	}
	
	/**
	 * Computes the factorial of a number 
	 * 
	 * @param x    Number to which we are computing the factorial
	 * @return the factorial of the number
	 */
	private int factorial (int x) {
	    int aux;

	    aux=1;
	    
	    if (x==0)
	        aux=1;
	    else 
	        aux = aux * factorial(x-1);
	    
	    return aux;
	}
	
	/**
	 * Computes the double factorial of a number
	 * 
	 * @param x    Number to which we are computing the factorial
	 * @return the double factorial of the number
	 */
	private int double_factorial (int x) {
	    int aux;
	    
	    aux = 1;
	    
	    if (x == 1) {
	        aux = 1;
	    }
	    else {
	        aux = aux * double_factorial(x-2);
	    }

	    return aux;
	}
	
	/**
	 * Computes the cost of the encoding of splitting a node
	 * 
	 * @param node Node of the tree with minimal information
	 * @param aux_node Node of the tree with complete information
	 * @return Cost of encoding a split for the node
	 */
	private double C_split (TreeNode node, Node aux_node) {
	    double cost;
	    
	    cost = Math.log((double)trainDataset.getNumAtr())/Math.log(2.0);
	    
	    if (trainDataset.getAttributes().get(node.getCondition().getAttribute()).isNominal()) {
	        // The attribute is categorical
	        cost += Math.log((Math.pow(2.0, (double)trainDataset.getAttributes().get(node.getCondition().getAttribute()).getValues().size()))-2.0)/Math.log(2.0);
	    }
	    else {
	        // The attribute is nominal
	        int aux;
	        aux = aux_node.getDifferentValuesAttributeI(node.getCondition().getAttribute());
	        cost += Math.log(((double)aux)-1)/Math.log(2.0);
	    }
	    
	    return cost;
	}
	
	/**
	 * Computes a lower bound of the cost for the node based in the posibility of a split
	 * 
	 * @param N    node for which we are estimating a cost
	 * @return lower bound of the cost for the node
	 */
	private double computeMinCostS (Node N) {
        double aux, tmpCost;
        int s;
        ArrayList<ArrayList<Integer>> ni;
        
        // Obtain a list with n1,...,nk in decreasing order
        ni = N.getDecreasedNI();

        if (ni.get(0).size() == 1) {
            // if k = 1 return (C(S) + 1)
            return (C(N) + 1);
        }
        
        s = 0;
        
        tmpCost = 2 * s + 1 + s * (Math.log((double)trainDataset.getNumAtr())/Math.log(2.0));
        for (int i=s+2; i < ni.get(0).size(); i++) {
            tmpCost += ni.get(1).get(i);
        }
        
        while (((s + 1) < (ni.get(0).size()-1)) && (ni.get(1).get(s+2) > (2 + (Math.log((double)trainDataset.getNumAtr())/Math.log(2.0))))) {
            tmpCost = tmpCost + 2 + (Math.log((double)trainDataset.getNumAtr())/Math.log(2.0)) - ni.get(1).get(s+2);
            s++;
        }
        
        aux = C(N) + 1;
        if (tmpCost < aux)
            aux = tmpCost;
        
        return aux;
    }
    
	/**
     * Computes a lower bound of the cost for the node based in the posibility of a split with aditional
     * information
     * 
     * @param N    node for which we are estimating a cost
     * @return lower bound of the cost for the node
     */
    private double computeMinCostV (Node N) {
        double aux, max, tmpCost, minCost, auxCost;
        int k, s;
        ArrayList <ArrayList <Integer>> ni;
        
        k = N.getNumClasses();

        if (k == 1) {
            // if k = 1 return (C(S) + 1)
            return (C(N) + 1);
        }
        
        // Obtain a list with the k classes in decreasing order of ni - V(Si)
        ni = N.getDecreasedNIV();
        
        s = 1;
        
        tmpCost = 1;
        for (int i=0; i < k; i++) {
            tmpCost += ni.get(1).get(i);
        }
        minCost = tmpCost;
        
        while (s <= k) {
            tmpCost = tmpCost + 2 + (Math.log((double)trainDataset.getNumAtr())/Math.log(2.0)) - (ni.get(1).get(s-1) - N.V(s-1));
            
            max = 0;
            for (int w=s+1; w<=k; w++) {
                if (ni.get(1).get(w-1) > max) {
                    max = ni.get(1).get(w-1);
                }
            }
            auxCost = tmpCost - max;
            
            if (auxCost < minCost) {
                minCost = auxCost;
            }

            s++;
        }
        
        aux = C(N) + 1;
        
        if (minCost < aux)
            aux = minCost;
        
        return aux;
    }
    
    /**
     * Computes a lower bound of the cost for the node based in the posibility of a split with aditional
     * information in a different way that computeMinCostV2
     * 
     * @param N    node for which we are estimating a cost
     * @return lower bound of the cost for the node
     */
    private double computeMinCostV2 (Node N) {
        double aux, tmpCost, minCost, auxCost;
        int k, s;
        ArrayList <ArrayList <Integer>> ni;
        int [] B;
        
        k = N.getNumClasses();

        if (k == 1) {
            // if k = 1 return (C(S) + 1)
            return (C(N) + 1);
        }
        
        B = new int [2*k];
        ni = N.getDecreasedNI();
        
        // Initialize B
        for (int i=0; i<k; i++) {
            B[2*i+1] = (int)N.V(i);
            B[2*i] = ni.get(1).get(i) - B[2*i+1];
        }
        
        // Sort array B in decreasing order of B[i]
        for(int i=0; i<2*k -1; i++){
            int current = B[i];
            int w=i;
            
            for(int j=i+1; j<B.length;j++){
               if(current < B[j]){
                 w = j;
                 current = B[j];
               }
            }
            
            B[w] = B[i];
            B[i] = current;
        }
        
        s = 0;
        
        tmpCost = 1;
        for (int i=0; i < k; i++) {
            tmpCost += ni.get(1).get(i);
        }
        minCost = tmpCost;
        
        while (s < (2*k-1)) {
            tmpCost = tmpCost + 2 + (Math.log((double)trainDataset.getNumAtr())/Math.log(2.0)) - B[s];
            
            auxCost = tmpCost - B[s+1];
            
            if (auxCost < minCost) {
                minCost = auxCost;
            }

            s++;
        }
        
        aux = C(N) + 1;
        
        if (minCost < aux)
            aux = minCost;
        
        return aux;
    }
    
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
     * @param atts  Attributes in the data set that are used for building the tree and describing the 
     * instance given
     * @return the class asigned to the item given
     */
	public int evaluateItem (double [] item, ArrayList <myAttribute> atts) {
	    return root.evaluate(item, atts);
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
                int cl = (int) evaluateItem(item, data.getAttributes());

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
        text = text + "\n@outputs " + data.getOutputAttribute().getName() + "\n@data\n\n@decisiontree\n\n" + root.printTree(data.getAttributes(), data.getOutputAttribute()) + "\n";
        
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

