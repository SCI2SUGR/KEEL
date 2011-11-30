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
* @author Written by Manuel Moreno (Universidad de Córdoba) 01/07/2008
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Decision_Trees.CART;

import java.util.ArrayList;
import java.util.Arrays;

import keel.Algorithms.Decision_Trees.CART.impurities.IImpurityFunction;
import keel.Algorithms.Decision_Trees.CART.tree.DecisionTree;
import keel.Algorithms.Decision_Trees.CART.tree.TreeNode;
import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;


/**
 * Main class of algorithm CART: Classification And Regression Trees (Breiman and al., 1984) CART are binary trees
 *
 */
public class CART 
{
	/** CART Tree) */
	private DecisionTree tree;

	/** Maximum Depth */
	private int maxDepth;
	
	/** Regression flag. 
	 * This flag is true when dealing with regression problems.
	 * False for classification problems.
	 */
	private boolean regression;

	/** Impurity function. Usually used Gini, Twoing functions */
	private IImpurityFunction impurityFunction;

	/** Building tree data set */
	private DoubleTransposedDataSet dataset;


	/////////////////////////////////////////////////////////////////////
	// ------------------------------------------------------ Constructor
	/////////////////////////////////////////////////////////////////////

	/**
	 * Default constructor
	 * 
	 * @param dataset Dataset to learn
	 */
	public CART (DoubleTransposedDataSet dataset) {
		this.dataset = dataset;

	}

	/**
	 * Constructor with impurity function
	 * 
	 * @param dataset Dataset to learn
	 * @param impurityFunction the impurity function
	 */
	public CART (DoubleTransposedDataSet dataset, IImpurityFunction impurityFunction) {
		this.dataset = dataset;

		// Set impurity function
		this.impurityFunction = impurityFunction;
		this.impurityFunction.setDataset(dataset);
	}

	/////////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Getters and Setters
	/////////////////////////////////////////////////////////////////////

	/**
	 * It returns the decision tree
	 * 
	 * @return the decision tree
	 */
	public DecisionTree getTree() 
	{
		return tree;
	}

	/**
	 * It returns the impurity function
	 * 
	 * @return the impurityFunction
	 */
	public IImpurityFunction getImpurityFunction() 
	{
		return impurityFunction;
	}

	/**
	 * 
	 * It sets the impurity function
	 * 
	 * @param impurityFunction the impurityFunction to set
	 */
	public void setImpurityFunction(IImpurityFunction impurityFunction) 
	{
		this.impurityFunction = impurityFunction;
		this.impurityFunction.setDataset(dataset);
	}

	/**
	 * It returns the maximal depth
	 * 
	 * @return the maxDepth
	 */
	public int getMaxDepth() {
		return maxDepth;
	}

	/**
	 * It sets the maximal depth
	 * 
	 * @param maxDepth the maxDepth to set
	 */
	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}
	
	/**
	 * Returns if we are dealing with a regression problem
	 * 
	 * @return the regression
	 */
	public boolean isRegression() {
		return regression;
	}

	/**
	 * It sets if we are dealing with a regression problem
	 * 
	 * @param regression the regression to set
	 */
	public void setRegression(boolean regression) {
		this.regression = regression;
	}

	/////////////////////////////////////////////////////////////////////
	// ---------------------------------------------------------- Methods
	/////////////////////////////////////////////////////////////////////

	/**
	 * This function find the best possible values for splitting
	 * 
	 * @param patterns pattern indexes
	 * @return the best possible values for splitting
	 */
	private double[][] splittingValues(int [] patterns)
	{
		int ninputs = dataset.getNofinputs();

		int npatterns = patterns.length;

		// Reserve memory for result
		double [][] splittingValues = new double[ninputs][npatterns-1];

		//computes medians values from the first one to the last but one
		for (int j=0; j<ninputs; j++) {
			// Get all values for input j
			double [] aux = dataset.getObservationsOf(j);

			// Get a copy of values in order to avoid damages in data set
			double [] x_j = new double[npatterns];
			for (int i=0; i<npatterns; i++) {
				int patternIndex = patterns[i];
				x_j[i] = aux[patternIndex];
			}

			// Sort values in vector x_j from min to max value
			Arrays.sort(x_j);

			// get splitting values as the middle of adjacent values (Xi + Xi+1)/2
			for (int i=0; i<x_j.length-1; i++) {
				splittingValues[j][i] = (x_j[i] + x_j[i+1])/2;
			}

		}
		return splittingValues;
	}

	/**
	 * Constructs decision tree
	 */
	public void build_tree () 
	{
		// Create tree
		tree = new DecisionTree();

		// Create root node

		// Root node contains all patterns in data set
		int [] patterns = new int[dataset.getNofobservations()];
		for (int i=0; i<patterns.length; i++)
			patterns[i]=i;//each index points to each pattern in data set

		TreeNode root = new TreeNode(null,patterns);

		// Set root node of the tree 
		tree.setRoot(root);

		// Make tree grow
		grow(root);
	}

	/**
	 * This is a recursive function that receive a node and check if it can be split. 
	 * If true, it adds sons, and try to grow them.
	 * @param node the node to check
	 */
	private void grow(TreeNode node)
	{
		if (node == null) // Check if node is null
			return;
		else {
			if (stopCriteria(node)) {// Check stop criteria
				
				if (regression)
					assignMean(node); // Assign its output value
				else
					assignClass(node); // Assign its output class.
				
				// Stop building the tree
				return;
			}
			else {
				splitNode(node); // Split node
				if (regression)
					assignMean(node); // Assign its output value
				else
					assignClass(node); // Assign its output class.
				// TODO a–adir asignar media
				grow(node.getLeftSon()); // grow left node
				grow(node.getRightSon()); // grow right node
			}	
		}
	}
	
	/**
	 * It splits a node into two sons
	 * 
	 * @param node node to split into two sons
	 */
	private void splitNode(TreeNode node)
	{
		//double time = System.currentTimeMillis();
		// Consider each variable x_j at a time
		int ninputs = dataset.getNofinputs();
		int npatterns = node.getPatterns().length;
		double [][] gains = new double[ninputs][npatterns-1];
		int bestSplit_i=0; 
		int bestSplit_j=0;

		 // Assign current node impurities (this increases the performance)
		try {
			node.setImpurities(impurityFunction.impurities(node.getPatterns(), 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// For each input variable x_j calculate possible splitting values x*_j
		// as the middle of adjacent values (x^i_j + x^i+1_j)/2
		double [][] splittingValues = splittingValues(node.getPatterns());

		for (int j=0; j<ninputs; j++) {
			// Among all questions x_j <= x*j choose the "best" (highest
			// change of impurity)

			// Check every split value
			for (int i=0; i<npatterns-1; i++) {
				// Compute gain for current node and input variable j for given value
				gains[j][i] = computeImpuritiesGain(node, j, splittingValues[j][i]);
				//System.out.println("Gain["+j+"]["+i+"]="+gains[j][i]);
				if (gains[j][i] >= gains[bestSplit_j][bestSplit_i]) { // Gain -> max
					bestSplit_i = i;
					bestSplit_j = j;
				}
			}
		}
		
		// Set variable and value
		node.setVariable(bestSplit_j);
		node.setValue(splittingValues[bestSplit_j][bestSplit_i]);


		// Split node in two sons
		ArrayList<int []> arrays = dividePatterns(node); // Fill patterns toLeft and toRight
		int [] toLeft = arrays.get(0);
		int [] toRight = arrays.get(1);

		// Create sons
		TreeNode leftSon = new TreeNode(node, toLeft);
		TreeNode rightSon = new TreeNode(node, toRight);

		// Link sons
		node.setLeftSon(leftSon);
		node.setRightSon(rightSon);
		
		//System.out.println("Split node: "+(System.currentTimeMillis()-time)+"ms");
	}

	/**
	 * This function assign output class to a node. 
	 * Its class label is that of the majority class in that node patterns 
	 * 
	 * @param node tree node to assign its output class
	 */
	private void assignClass(TreeNode node) 
	{
		//double time = System.currentTimeMillis();
		
		// Patterns in current node
		int [] patterns = node.getPatterns();

		// Data set outputs
		double [][] outputs = dataset.getAllOutputs();

		// Counter of patterns in each class
		int [] patternsInClass = new int [outputs.length];

		// Determine majority class in current node
		for (int i=0; i<outputs.length; i++) { // For each class
			for (int j=0; j<patterns.length; j++) { // For each pattern
				int patternIndex = patterns[j];
				if ( outputs[i][patternIndex] == 1.0) // if patterns owns to class i
					patternsInClass[i]++;
			}
		}

		// Find majority class
		int majorityClass = 0;
		for (int i=1; i<patternsInClass.length; i++) {
			if (patternsInClass[i] > patternsInClass[majorityClass])
				majorityClass = i;
		}

		// Assign majority class to the node
		node.setOutputClass(majorityClass);
		
		//System.out.println("Asign Classes: "+(System.currentTimeMillis()-time)+"ms");
	}
	
	/**
	 * Assign predicted value as the mean of the output 
	 * from each pattern in this node
	 * 
	 * @param node TreeNode to compute
	 */
	private void assignMean(TreeNode node)
	{		
		// Patterns in current node
		int [] patterns = node.getPatterns();

		// Data set outputs
		double [] outputs = dataset.getOutput(0);
		
		// Compute mean
		double mean = 0;
		for (int i=0; i<patterns.length; i++) {
			int patternIndex = patterns[i];
			mean += outputs[patternIndex];
		}
		mean = mean/patterns.length;
		
		// Assign mean
		node.setOutputValue(mean);
	}
	
	/**
	 * This function divides patterns associated to a node using its variable and split value into two
	 * groups of patterns depending on the condition (variable <= splitValue)
	 * 
	 * @param from Node to split in two branches. It must contain variable, split value and associated patterns.
	 * @return Return toLeft This parameter will be deleted!. It will contain the patterns on left branch. toRight This parameter will be deleted!. It will contain the patterns on right branch
	 */
	private ArrayList<int[]> dividePatterns(TreeNode from) 
	{
		//double time = System.currentTimeMillis();
		int [] patterns = from.getPatterns();
		int variable = from.getVariable();
		double limitValue = from.getValue();

		ArrayList<Integer> leftBranch = new ArrayList<Integer>();
		ArrayList<Integer> rightBranch = new ArrayList<Integer>();

		// Divide patterns using condition variable <= value
		for (int j=0; j< patterns.length; j++) {
			int patternIndex = patterns[j];
			double patternValue = dataset.getAllInputs()[variable][patternIndex];

			// This pattern goes to left or right branch?
			if (patternValue <= limitValue)
				leftBranch.add(patternIndex);
			else 
				rightBranch.add(patternIndex);
		}

		// Convert into arrays
		int [] toLeft = new int [leftBranch.size()];
		for (int i=0; i<toLeft.length; i++)
			toLeft[i] = leftBranch.get(i);

		int [] toRight = new int [rightBranch.size()];
		for (int i=0; i<toRight.length; i++)
			toRight[i] = rightBranch.get(i);

		// Construct a List for result
		ArrayList<int[]> result = new ArrayList<int[]>();
		result.add(toLeft);
		result.add(toRight);
		
		//System.out.println("Divide Patterns: "+(System.currentTimeMillis()-time)+"ms");

		return result;
	}

	/**
	 * This function calculates the impurities variance between 
	 * parent (current node) and both sons
	 * 
	 * @param node Current node 
	 * @param inputvar Input Data set variable index
	 * @param limitValue Limit value to compare 
	 * @return impurities gain
	 */
	private double computeImpuritiesGain(TreeNode node, int inputvar, double limitValue)
	{
		
		ArrayList<Integer> leftBranch = new ArrayList<Integer>();
		ArrayList<Integer> rightBranch = new ArrayList<Integer>();

		int [] patterns = node.getPatterns();
		// For each pattern in parent node
		for (int j=0; j< patterns.length; j++) {
			int patternIndex = patterns[j];
			double patternValue = dataset.getAllInputs()[inputvar][patterns[j]];

			// This pattern goes to left or right branch?
			if (patternValue <= limitValue)
				leftBranch.add(patternIndex);
			else 
				rightBranch.add(patternIndex);
		}
		
		// Compute right, left and parent impurities in order
		// to obtain the gain
		int [] leftPatterns = new int [leftBranch.size()];
		int [] rightPatterns = new int [rightBranch.size()];
		double parentImpurities = 0f;
		double leftImpurities = 0f;
		double rightImpurities = 0f;
		
		try { // Impurities functions can throw exceptions
			// obtain impurities in left branch (using cost 1)	
			for (int i=0; i<leftPatterns.length; i++)
				leftPatterns[i] = leftBranch.get(i);
			leftImpurities = impurityFunction.impurities(leftPatterns, 1);
			
			// obtain impurities in right branch
			for (int i=0; i<rightPatterns.length; i++)
				rightPatterns[i] = rightBranch.get(i);
			rightImpurities = impurityFunction.impurities(rightPatterns, 1);
			 

			// obtain impurities of current node
			// parentImpurities = impurityFunction.impurities(patterns, 1);
			parentImpurities = node.getImpurities();
		} catch (Exception e) {
			e.printStackTrace();
		} 


		// return i(t) - P_l*i(t_l) - P_r*i(t_r)
		double P_l = leftPatterns.length/(double)patterns.length;
		double P_r = rightPatterns.length/(double)patterns.length;


		// System.out.println("Gain: "+parentImpurities+"-("+P_l+"*"+leftImpurities+" + "+P_r+"*"+rightImpurities+")");
		return ( parentImpurities - (P_l*leftImpurities) - (P_r*rightImpurities));
	}

	/**
	 * Prune decision tree
	 */
	public void prune_tree() {
		// TODO A prune method can be used
	}

	/**
	 * It checks if the stop criteria has been reached
	 * 
	 * @return true if stop criteria has been reached. False otherwise
	 */
	public boolean stopCriteria(TreeNode node) {

		//double time = System.currentTimeMillis();
		
		int [] patterns = node.getPatterns();
		
		// If a node have only one pattern (needed for next criteria)
		if (patterns.length < 2)
			return true;
		
		// If tree depth reaches user-specified limit
		if(tree.depth() >= maxDepth)
			return true;
		
		// If a node becomes pure
		// (all cases in a node have identical values of the dependent variable)
		boolean equalDependant = true;
		for (int i=0; i<patterns.length-1; i++) {
			int patternIndex = patterns[i];
			int nextPatternIndex = patterns[i+1]; //Be sure there is more than one pattern
			double [] prev_output = dataset.getOutputs(patternIndex);
			double [] next_output = dataset.getOutputs(nextPatternIndex);
			equalDependant = Arrays.equals(prev_output, next_output);
			if (!equalDependant) //Case any difference, break
				break;
		}
		if (equalDependant) // Case all outputs are equals
			return true;

		// TODO Other possible stop criteria
		// If all cases in a node have identical values for each predict
		
		// If the size of a node is less than the user-specified minimum node size value 

		// If the split of a node result in a child whose node size is less than the
		// user-specified minimum

		//System.out.println("Stop Criteria: "+(System.currentTimeMillis()-time)+"ms");		
		// Otherwise
		return false;
	}

	/**
	 * It gets the classification results
	 * 
	 * @param dataset used for checking error in 
	 * @return error produced applying the data set given as argument
	 * 
	 */
	public byte[][] getClassificationResults(DoubleTransposedDataSet dataset)
	{
		double [][] inputs = transposedMatrix(dataset.getAllInputs());

		int noutputs = dataset.getNofoutputs();
		int npatterns = dataset.getNofobservations();

		// Result matrix with predicted values
		byte [][] predicted = new byte [noutputs][npatterns];

		// For each pattern determine if it is correctly classified
		TreeNode root = tree.getRoot();

		for (int i=0; i<npatterns; i++) {
			double [] pattern = inputs[i];
			
			int predictedClass = (int) root.evaluate(pattern, regression);
			
			// Initialize values
			for (int j=0; j<noutputs; j++) {
				predicted[j][i] = 0;
			}

			// Check if prediction is correct
			predicted[predictedClass][i] = 1;
		}

		// return CCR
		return predicted;
	}
	
	
	/**
	 * 
	 * It gets the regression results
	 * 
	 * @param dataset used for checking error in 
	 * @return error produced applying the data set given as argument
	 * 
	 */
	public double[] getRegressionResults(DoubleTransposedDataSet dataset)
	{
		double [][] inputs = transposedMatrix(dataset.getAllInputs());

		int npatterns = dataset.getNofobservations(); 

		// Result matrix with predicted values
		double [] predicted = new double [npatterns];

		// For each pattern determine if it is correctly classified
		TreeNode root = tree.getRoot();

		for (int i=0; i<npatterns; i++) {
			double [] pattern = inputs[i];
			double predictedValue = root.evaluate(pattern, regression);

			// Check if prediction is correct
			predicted[i] = predictedValue;
		}

		// return CCR
		return predicted;
	}
	

	/**
	 * 
	 * It returns the transposed matrix of a given one
	 * 
	 * @param a input matrix
	 * @return transposed matrix of a
	 */
	private double [][] transposedMatrix(double [][] a)
	{
		int rows = a.length;
		int cols = a[rows-1].length;
		double[][] b = new double [cols][rows];
		for (int i=0; i< rows; i++) {
			for (int j=0; j<cols; j++) {
				b[j][i] = a[i][j];
			}
		}
		return b;
	}

}

