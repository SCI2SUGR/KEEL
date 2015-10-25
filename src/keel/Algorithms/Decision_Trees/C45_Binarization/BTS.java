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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

/**
 * <p>Title: BTS</p>
 * <p>Description: This class implements the BTS OVO scheme
 * <p>Company: KEEL </p>
 * @author Mikel Galar (University of Navarra) 21/10/2010
 * @version 1.0
 * @since JDK1.6
 */
public class BTS {

	/**
	 * This class implements the node structure of the Binary Tree
	 */
	private class Node {
		int i, j; // The classes distinguished in the node
		ArrayList<Integer> classes; // The classes from which we have instances in the node
		Node[] nextNodes; // Pointers to the next nodes y the tree

		/**
		 * It constructs a new node (the root node containing all the classes)
		 */
		public Node()
		{
			classes = new ArrayList<Integer>();
			for (int k = 0; k < classifier.nClasses; k++)
				classes.add(new Integer(k));
		}

		/**
		 * It constructs a new node containing class c
		 * @param c the first class in the node
		 */
		public Node(int c)
		{
			classes = new ArrayList<Integer>();
			classes.add(new Integer(c));
		}

		/** 
		 * It adds a new class to the node class list
		 * @param clase 
		 */
		public void add(int clase)
		{
			Integer n = new Integer(clase);
			if (!classes.contains(n))
				classes.add(n);
		}

		/**
		 * It compares two nodes
		 * @param o the node to be compared with
		 * @return true or false depending on the comparison result
		 */
		@Override
		public boolean equals(Object o)
		{
			if (o instanceof Node)
			{
				Node node = (Node)o;
				Collections.sort(node.classes);
				Collections.sort(this.classes);
				if (node.i == this.i && node.j == this.j && this.classes.equals(node.classes))
					return true;
			}
			return false;
		}


		/**
		 * Given a node, it constructs the next nodes recursively
		 */
		public void constructNextNodes()
		{
			int[] outputs = classifier.train.getOutputAsInteger();
			nextNodes = new Node[2];
			nextNodes[0] = new Node(this.i);
			nextNodes[1] = new Node(this.j);
			double[] thresholds = new double[nClasses];
			double t;
			/* Initialize the thresholds for each class */
			for (int k = 0; k < nClasses; k++)
				thresholds[k] = Double.MAX_VALUE;

			/**
			 * We have, to divide i and j classes into 0 and 1 node; then
			 * the other examples node depends on their classification
			 * For this reason, we have to compute their probability!
			 */ 
			for (int k = 0; k < classifier.train.getnData(); k++)
			{
				int clase = outputs[k];
				// If the class is in this node, we take into account the example
				if (classes.contains(new Integer(clase)))
				{
					if (clase != i && clase != j) // We know that i and j have to be in the next nodes
					{
						double[] ejemplo = classifier.train.getExample(k);
						// Obtain the confidence for the example using the ij classifier
						double[] sal = classifier.obtainConfidence(i, j, ejemplo);
						if (sal[0] > sal[1]) // Node 0 (class i)
						{
							nextNodes[0].add(clase);
							// store the new threshold if it is smaller
							t = sal[0] - 0.5;
							if (t < thresholds[clase])
								thresholds[clase] = t;
						}
						else if (sal[0] < sal[1]) // Node 1 (class j)
						{
							nextNodes[1].add(clase);
							// store the new threshold if it is smaller
							t = sal[0] - 0.5;
							if (t < thresholds[clase])
								thresholds[clase] = t;
						}
						else
						{
							//System.out.println("Jodida casualidad que da 0");
							nextNodes[1].classes = this.classes;
							nextNodes[0].classes = this.classes;
							return;
						}

					}
				}
			}
			/** Now, we look for each class, if the classes that are only in one node
			 * have a low threshold, then they are reassigned to both nodes.
			 */
			for (int k = 0; k < nClasses; k++)
			{
				Integer c = new Integer(k);
				if (classes.contains(c))
				{

					if (i != k && j != k)
					{
						if (nextNodes[0].classes.contains(c) &&
								!nextNodes[1].classes.contains(c) &&
								thresholds[k] < threshold)
							nextNodes[1].add(k);
						else if (!nextNodes[0].classes.contains(c) &&
								nextNodes[1].classes.contains(c) &&
								thresholds[k] < threshold)
							nextNodes[0].add(k);
					}
				}
			}
		}


		/**
		 * It obtains the correspoding class for the given example
		 * @param ejemplo the example to be evaluated
		 * @return the predicted class
		 */
		public String obtainClass(double[] ejemplo)
		{
			if (this.nextNodes == null)
			{
				// It is a leaf node
				if (this.classes.size() == 1)
					return classifier.train.getOutputValue(
							this.classes.get(0).intValue());
				else
				{
					/**
					 * If it is a leaf node containing more than one class, 
					 * then we use the voting strategy between those classes
					 */
					double[][] tabla = new double[nClasses][nClasses];
					for (int k = 0; k < nClasses; k++)
					{
						for (int t = k + 1; t < nClasses; t++)
						{
							if (classes.contains(new Integer(k)) && classes.contains(new Integer(t)))
							{
								int sal = classifier.obtainClass(k, t, ejemplo);
								if (sal == k)
									tabla[k][t] = 1;
								else if (sal == t)
									tabla[t][k] = 1;
							}
						}
					}
					double max[] = new double[nClasses];
					for (int k = 0; k < nClasses; k++) {
						double sum_clase = 0.0;
						for (int t = 0; t < nClasses; t++) {
							sum_clase += tabla[k][t];
						}
						max[k] = sum_clase;
					}
					return classifier.ovo.getOutputTies(max);
				}
			}
			else
			{
				/**
				 * If it is an internal node (non-leaf), then classify the example
				 * using the corresponding classiffier and continue with the corresponding
				 * child node
				 */ 
				int clase = classifier.obtainClass(i, j, ejemplo);
				if (clase == i)
					return nextNodes[0].obtainClass(ejemplo);
				else if (clase == j)
					return nextNodes[1].obtainClass(ejemplo);
				else
				{
					// break ties by a priori distribution
					double[] cs = new double[nClasses];
					for (int k = 0; k < classes.size(); k++)
						cs[classes.get(k).intValue()] = 1;
					return ovo.getOutputTies(cs);
				}
			}
		}
	}

	Multiclassifier classifier;   // Pointer to the OVO classifiers
	int       nClasses;
	float     threshold;    // The threshold parameter
	Node      root;         // The root node
	double[][] averagePerClass, stdPerClass; // Statistics to construct a balanced tree
	OVO       ovo;
	ArrayList<Node> trainedList;    // List of trained nodes

	/**
	 * Binary Tree of Classifiers constructor
	 * @param classifier The OVO classifier
	 * @param threshold The minimum threshold for accepting a class being only in one node
         * @param ovo The OVO methodology
	 */
	public BTS(Multiclassifier classifier, float threshold, OVO ovo)
	{
		this.classifier = classifier;
		nClasses = classifier.nClasses;
		this.threshold = threshold;
		this.ovo = ovo;

		classifier.train.computeStatisticsPerClass();
		this.averagePerClass = classifier.train.getAveragePerClass();
		this.stdPerClass = this.classifier.train.getStdPerClass();
		trainedList = new ArrayList<Node>();
	}

	/**
	 * It initilizes the root node of the tree
	 */
	public void initialize()
	{
		System.out.println("Initializing BTS");
		root = new Node();
		System.out.println("Initialize BTS finished");
	}

	/**
	 * It constructs the balanced binary tree
	 */
	public void construct()
	{
		Node actualNode = root;
		ArrayList<Node> recNodes = new ArrayList<Node>();
		trainedList.add(actualNode);

		while (actualNode != null)
		{
			if (actualNode.classes.size() > 1)
			{
				// Compute the distances to the center node (mean center)
				double[] dist = distances(actualNode);
				// Take the two lowest distances!

				/* Sort the array but store the indexes! */
				TreeMap<Double, List<Integer>> map = new TreeMap<Double, List<Integer>>();
				for(int i = 0; i < dist.length; i++) {
					List<Integer> ind = map.get(dist[i]);
					if(ind == null){
						ind = new ArrayList<Integer>();
						map.put(dist[i], ind);
					}
					ind.add(i);
				}
				// Now flatten the list
				List<Integer> indices = new ArrayList<Integer>();
				for(List<Integer> arr : map.values()) {
					indices.addAll(arr);
				}

				/* Try different combinations of pairs until one is accepted */
				boolean accepted = false;
				for (int p = 0; p < dist.length && !accepted; p++) {
					for (int q = p + 1; q < dist.length && !accepted; q++) {
						int i = actualNode.classes.get(indices.get(p));
						int j = actualNode.classes.get(indices.get(q));
						actualNode.i = i < j ? i : j;
						actualNode.j = i >= j ? i : j;
						actualNode.constructNextNodes();

						if (actualNode.nextNodes[0].classes.size() < actualNode.classes.size() ||
								actualNode.nextNodes[1].classes.size() < actualNode.classes.size())
							// Instances properly divided! -> the node is accepted
							accepted = true;
							System.out.println("Nodo actual: " + actualNode.classes.size() + " Nodo 0: "
									+ actualNode.nextNodes[0].classes.size() + " Nodo 1: "
									+ actualNode.nextNodes[1].classes.size() + " accepted = " + accepted);
					}
				}
				if (!accepted) {
					/* Worst situation occurred, the node will not has no children nodes */
					System.out.println("Worst situation, nClasses = " + actualNode.classes.size() + " / " + classifier.nClasses);
					actualNode.nextNodes = null;
				}
				else {
					/* See wheter the new nodes are currently trained or they have to be constructed */
					if (actualNode.nextNodes[0].classes.size() > 1 && !trainedList.contains(actualNode.nextNodes[0])) {
						recNodes.add(actualNode.nextNodes[0]);
						trainedList.add(actualNode.nextNodes[0]);
					}
					else if (trainedList.contains(actualNode.nextNodes[0])) {
						actualNode.nextNodes[0] = trainedList.get(trainedList.indexOf(actualNode.nextNodes[0]));
					}
					if (actualNode.nextNodes[1].classes.size() > 1 && !trainedList.contains(actualNode.nextNodes[1])) {
						recNodes.add(actualNode.nextNodes[1]);
						trainedList.add(actualNode.nextNodes[1]);
					}
					else if (trainedList.contains(actualNode.nextNodes[1])) {
						actualNode.nextNodes[1] = trainedList.get(trainedList.indexOf(actualNode.nextNodes[1]));
					}
					Collections.sort(actualNode.nextNodes[0].classes);
					Collections.sort(actualNode.nextNodes[1].classes);
				}
			}
			if (recNodes.size() > 0) {
				// Go for the next node!
				actualNode = recNodes.get(0);
				recNodes.remove(0);
			}
			else
				actualNode = null;
		}
	}

	/**
	 * It computes the mean center of the classes and then it computes the
	 *  distances between this center and all the classes
	 * @param node  Node from which to obtain the classes
	 * @return the vector with the distances from each class to the mean center
	 */
	private double[] distances(Node node)
	{
		int nInputs = this.classifier.train.getnInputs();
		double[] meanNode = new double[nInputs];
		double[] dist = new double[node.classes.size()];

		for (int i = 0; i < nInputs; i++) {
			meanNode[i] = 0;
			for (int j = 0; j < nClasses; j++) {
				if (node.classes.contains(new Integer(j)))
					meanNode[i] += this.averagePerClass[j][i];
			}
			meanNode[i] /= node.classes.size();
		}

		for (int i = 0; i < dist.length; i++) {
			dist[i] = 0;
			for(int j = 0; j < meanNode.length; j++) {
				double aux =  this.averagePerClass[node.classes.get(i)][j];

				if (aux == classifier.train.average(j))
					dist[i] += 1;
				else
					dist[i] += (meanNode[j] - this.averagePerClass[node.classes.get(i)][j]) *
					(meanNode[j] - this.averagePerClass[node.classes.get(i)][j]);
			}
			dist[i] = Math.sqrt(dist[i]);
		}
		return dist;
	}

	/**
	 * It computes the class for a given example
	 * @param ejemplo the example to be classified
	 * @return the predicted class
	 */
	public String computeClass(double[] ejemplo)
	{
		String output = root.obtainClass(ejemplo);
		return output;
	}
}
