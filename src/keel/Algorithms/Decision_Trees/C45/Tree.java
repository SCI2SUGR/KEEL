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


package keel.Algorithms.Decision_Trees.C45;


/**
 * Class to handle the classifier tree
 * 
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @author Modified by Alberto Fernandez (UGR) 
* @version 1.2 (29-04-10)
* @since JDK 1.5
*</p>
*/
public class Tree {

    /** Total number of Nodes in the tree */
    public static int NumberOfNodes;

    /** Number of Leafs in the tree */
    public static int NumberOfLeafs;


    /** The selected model. */
    protected SelectCut model;

    /** The model of the node. */
    protected Cut nodeModel;

    /** Sons of the node. */
    protected Tree[] sons;

    /** Is this node leaf or not. */
    protected boolean isLeaf;

    /** Is this node empty or not. */
    protected boolean isEmpty;

    /** The dataset. */
    protected Dataset train;

    /** Is pruned the tree or not. */
    protected boolean prune = false;

    /** The confidence factor for pruning. */
    protected float confidence = 0.25f;

    /** To compute the average attributes per rule */
    public static int global = 0;

    /** Constructor.
     *
     * @param selectNodeModel	The cut model.
     * @param pruneTree			Prune the tree or not.
     * @param cf				Minimum confidence.
     */
    public Tree(SelectCut selectNodeModel, boolean pruneTree, float cf) {
        model = selectNodeModel;
        prune = pruneTree;
        confidence = cf;

        NumberOfNodes = 0;
        NumberOfLeafs = 0;

    }

    /** Adds one new node.
     *
     * @param data			The dataset.
     *
     * @throws Exception	If the node cannot be built.
     */
    public void buildNode(Dataset data) throws Exception {
        Dataset[] localItemsets;
        train = data;
        isLeaf = false;
        isEmpty = false;
        sons = null;
        nodeModel = model.selectModel(data);

        if (nodeModel.numSubsets() > 1) {
            localItemsets = nodeModel.cutDataset(data);
            data = null;
            sons = new Tree[nodeModel.numSubsets()];

            for (int i = 0; i < sons.length; i++) {
                sons[i] = getNewTree(localItemsets[i]);
                localItemsets[i] = null;
            }
        } else {
            isLeaf = true;

            if (data.sumOfWeights() == 0) {
                isEmpty = true;
            }

            data = null;
        }
    }

    /** Function to build the classifier tree.
     *
     * @param data			The dataset.
     *
     * @throws Exception	If the tree cannot be built.
     */
    public void buildTree(Dataset data) throws Exception {
        data = new Dataset(data);
        data.deleteWithMissing(data.getClassIndex());
        buildNode(data);
        collapse();

        if (prune) {
            prune();
        }
    }

    /** Function to collapse a tree to a node if training error doesn't increase.
     *
     */
    public final void collapse() {
        double errorsOfSubtree, errorsOfTree;
        int i;

        if (!isLeaf) {
            errorsOfSubtree = getErrors();
            errorsOfTree = nodeModel.classification().numIncorrect();

            if (errorsOfSubtree >= errorsOfTree - 1E-3) {
                // Free adjacent trees
                sons = null;
                isLeaf = true;

                // Get NoCut Model for tree.
                nodeModel = new Cut(nodeModel.classification());
            } else {
                for (i = 0; i < sons.length; i++) {
                    son(i).collapse();
                }
            }
        }
    }

    /** Function to prune a tree.
     *
     * @throws Exception	If the prune cannot be made.
     */
    public void prune() throws Exception {
        double errorsLargestBranch, errorsLeaf, errorsTree;
        int indexOfLargestBranch, i;
        Tree largestBranch;

        if (!isLeaf) {
            // Prune all subtrees.
            for (i = 0; i < sons.length; i++) {
                son(i).prune();
            }

            // Compute error for largest branch
            indexOfLargestBranch = nodeModel.classification().maxValue();

            errorsLargestBranch = son(indexOfLargestBranch).
                                  getEstimatedErrorsForBranch((Dataset) train);

            // Compute error if this Tree would be leaf
            errorsLeaf = getEstimatedErrorsForLeaf(nodeModel.classification());

            // Compute error for the whole subtree
            errorsTree = getEstimatedErrors();

            // Decide if leaf is best choice.
            if (errorsLeaf <= errorsTree + 0.1 &&
                errorsLeaf <= errorsLargestBranch + 0.1) {
                // Free son Trees
                sons = null;
                isLeaf = true;

                // Get NoCut Model for node.
                nodeModel = new Cut(nodeModel.classification());

                return;
            }

            // Decide if largest branch is better choice
            // than whole subtree.
            if (errorsLargestBranch <= errorsTree + 0.1) {
                largestBranch = son(indexOfLargestBranch);
                sons = largestBranch.sons;
                nodeModel = largestBranch.nodeModel;
                isLeaf = largestBranch.isLeaf;
                newClassification(train);
                prune();
            }
        }
    }

    /** Function to get the classification of classes.
     *
     * @param itemset		The itemset to classify.
     *
     * @return				The classification of class values for the itemset.
     *
     * @throws Exception	If the probabilities cannot be computed.
     */
    public final double[] classificationForItemset(Itemset itemset) throws
            Exception {
        double[] doubles = new double[itemset.numClasses()];

        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = getProbabilities(i, itemset, 1);
        }

        return doubles;
    }

    /** Function to compute the class probabilities of a given itemset.
     *
     * @param classIndex	The index of the class attribute.
     * @param itemset		The itemset.
     * @param weight		The weight.
     *
     * @return				The probability of the class.
     *
     * @throws Exception	If the probabilities cannot be computed.
     */
    private double getProbabilities(int classIndex, Itemset itemset,
                                    double weight) throws Exception {
        double[] weights;
        double prob = 0;
        int treeIndex, i, j;

        if (isLeaf) {
            return weight * nodeModel.classProbability(classIndex, itemset, -1);
        } else {
            treeIndex = nodeModel.whichSubset(itemset);

            if (treeIndex == -1) {
                weights = nodeModel.weights(itemset);

                for (i = 0; i < sons.length; i++) {
                    if (!son(i).isEmpty) {
                        prob +=
                                son(i).getProbabilities(classIndex, itemset,
                                weights[i] * weight);
                    }
                }

                return prob;
            } else {
                if (son(treeIndex).isEmpty) {
                    return weight *
                            nodeModel.classProbability(classIndex, itemset,
                            treeIndex);
                } else {
                    return son(treeIndex).getProbabilities(classIndex, itemset,
                            weight);
                }
            }
        }
    }

    /** Function to print the tree.
     *
     * @return String representation of the tree.
     */
    public String toString() {
        try {
            StringBuffer text = new StringBuffer();

            if (!isLeaf) {
                NumberOfNodes++;
                printTree(0, text);
            }

            return text.toString();
        } catch (Exception e) {
            return "Can not print the tree.";
        }
    }

    /** 
     * Function to print the tree (OVO code).
     * @return String representation of the tree (OVO code).
     */
    public String toStringOVO() {
        try {
            StringBuffer text = new StringBuffer();

            if (!isLeaf) {
                NumberOfNodes++;
                printTreeOVO(0, text);
            }

            return text.toString();
        } catch (Exception e) {
            return "Can not print the tree.";
        }
    }    
    
    /** Function to print the tree.
     *
     * @param depth			Depth of the node in the tree.
     * @param text			The tree.
     *
     * @throws Exception	If the tree cannot be printed.
     */
    private void printTree(int depth, StringBuffer text) throws Exception {
        int i, j;
        String aux = "";
        
        for (int k = 0; k < depth; k++) {
            aux += "\t";
        }

        for (i = 0; i < sons.length; i++) {
            text.append(aux);

            if (i == 0) {
                text.append("if ( " + nodeModel.leftSide(train) +
                            nodeModel.rightSide(i, train) + " ) then\n" + aux +
                            "{\n");
            } else {
                text.append("elseif ( " + nodeModel.leftSide(train) +
                            nodeModel.rightSide(i, train) + " ) then\n" + aux +
                            "{\n");
            }

            if (sons[i].isLeaf) {
                NumberOfLeafs++;
                text.append(aux + "\t" + train.getClassAttribute().name() +
                            " = \"" + nodeModel.label(i, train) + "\"\n");
            } else {
                NumberOfNodes++;
                sons[i].printTree(depth + 1, text);
            }

            text.append(aux + "}\n");
        }
    }

    /** Function to print the tree.
    *
    * @param depth			Depth of the node in the tree.
    * @param text			The tree.
    *
    * @throws Exception	If the tree cannot be printed.
    */
   private void printTreeOVO(int depth, StringBuffer text) throws Exception {
       int i, j;
       String aux = "";

       for (i = 0; i < sons.length; i++) {
           text.append(aux);

           if (i == 0) {
               text.append("if ( " + nodeModel.leftSideOVO(train) +
                           nodeModel.rightSide(i, train) + " ) then\n" + aux +
                           "");
           } else {
               text.append("elseif "+depth+" ( " + nodeModel.leftSideOVO(train) +
                           nodeModel.rightSide(i, train) + " ) then\n" + aux +
                           "");
           }

           if (sons[i].isLeaf) {
               NumberOfLeafs++;
               text.append(aux + "\t" + train.getClassAttribute().name() +
                           " = " + nodeModel.label(i, train) + " \n");
           } else {
               NumberOfNodes++;
               sons[i].printTreeOVO(depth + 1, text);
           }
       }
   }
    
    
   /**
   * Function to compute the number of attributes of the tree.
   * @param depth			Depth of the node in the tree.
   *
   */
    private void attributesPerRule(int depth){
      depth++;
      for (int i = 0; i < sons.length; i++) {
        if (sons[i].isLeaf) {
          global += depth;
        }
        else {
          sons[i].attributesPerRule(depth);
        }
      }
    }

    /**
     * Function to compute the number of attributes of the tree
     * @return int number of attributes of the tree
     */
    public int getAttributesPerRule(){
      global = 0;
      if (!isLeaf) {
    	  this.attributesPerRule(0);
      }
      return global;
    }

    /** Returns the son with the given index.
     *
     * @param index	The index of the son.
     */
    private Tree son(int index) {
        return (Tree) sons[index];
    }

    /** Function to create a new tree.
     *
     * @param data			The dataset.
     *
     * @return				The new tree.
     *
     * @throws Exception	If the new tree cannot be created.
     */
    protected Tree getNewTree(Dataset data) throws Exception {
        Tree newNode = new Tree(model, prune, confidence);
        newNode.buildNode((Dataset) data);

        return newNode;
    }

    /** Function to compute the estimated errors.
     *
     * @return	The estimated errors.
     */
    private double getEstimatedErrors() {
        double errors = 0;
        int i;

        if (isLeaf) {
            return getEstimatedErrorsForLeaf(nodeModel.classification());
        } else {
            for (i = 0; i < sons.length; i++) {
                errors = errors + son(i).getEstimatedErrors();
            }

            return errors;
        }
    }

    /** Function to compute the estimated errors for one branch.
     *
     * @param data			The dataset over the errors has to be computed.
     *
     * @return				The error computed.
     *
     * @throws Exception	If the errors cannot be computed.
     */
    private double getEstimatedErrorsForBranch(Dataset data) throws Exception {
        Dataset[] localItemsets;
        double errors = 0;
        int i;

        if (isLeaf) {
            return getEstimatedErrorsForLeaf(new Classification(data));
        } else {
            Classification savedDist = nodeModel.classification;
            nodeModel.resetClassification(data);
            localItemsets = (Dataset[]) nodeModel.cutDataset(data);
            nodeModel.classification = savedDist;

            for (i = 0; i < sons.length; i++) {
                errors += son(i).getEstimatedErrorsForBranch(localItemsets[i]);
            }

            return errors;
        }
    }

    /** Function to compute the estimated errors for leaf.
     *
     * @param theClassification	The classification of the classes.
     *
     * @return					The estimated errors for the leaf.
     */
    private double getEstimatedErrorsForLeaf(Classification theClassification) {
        if (theClassification.getTotal() == 0) {
            return 0;
        } else {
            return theClassification.numIncorrect() +
                    errors(theClassification.getTotal(),
                           theClassification.numIncorrect(), confidence);
        }
    }

    /** Function to compute the errors on training data.
     *
     * @return	The errors.
     */
    private double getErrors() {
        double errors = 0;
        int i;

        if (isLeaf) {
            return nodeModel.classification().numIncorrect();
        } else {
            for (i = 0; i < sons.length; i++) {
                errors += son(i).getErrors();
            }

            return errors;
        }
    }

    /** Function to create a new classification.
     *
     * @param data			The dataset.
     *
     * @throws Exception	If the classification cannot be built.
     */
    private void newClassification(Dataset data) throws Exception {
        Dataset[] localItemsets;

        nodeModel.resetClassification(data);
        train = data;

        if (!isLeaf) {
            localItemsets = (Dataset[]) nodeModel.cutDataset(data);

            for (int i = 0; i < sons.length; i++) {
                son(i).newClassification(localItemsets[i]);
            }
        }
    }

    /** Function to compute estimated extra error for given total number of itemsets and errors.
     *
     * @param N		The weight of all the itemsets.
     * @param e		The weight of the itemsets incorrectly classified.
     * @param CF	Minimum confidence.
     *
     * @return		The errors.
     */
    private static double errors(double N, double e, float CF) {
        // Some constants for the interpolation.
        double Val[] = {0, 0.000000001, 0.00000001, 0.0000001, 0.000001,
                0.00001, 0.00005, 0.0001,
                0.0005, 0.001, 0.005, 0.01, 0.05, 0.10, 0.20, 0.40, 1.00};
        double Dev[] = {100, 6.0, 5.61, 5.2, 4.75, 4.26, 3.89, 3.72, 3.29, 3.09,
                2.58,
                2.33, 1.65, 1.28, 0.84, 0.25, 0.00};

        double Val0, Pr, Coeff = 0;
        int i = 0;

        while (CF > Val[i]) {
            i++;
        }

        Coeff = Dev[i - 1] +
                (Dev[i] - Dev[i - 1]) * (CF - Val[i - 1]) / (Val[i] - Val[i - 1]);
        Coeff = Coeff * Coeff;

        if (e == 0) {
            return N * (1 - Math.exp(Math.log(CF) / N));
        } else {
            if (e < 0.9999) {
                Val0 = N * (1 - Math.exp(Math.log(CF) / N));

                return Val0 + e * (errors(N, 1.0, CF) - Val0);
            } else {
                if (e + 0.5 >= N) {
                    return 0.67 * (N - e);
                } else {
                    Pr = (e + 0.5 + Coeff / 2 + Math.sqrt(Coeff * ((e + 0.5)
                            * (1 - (e + 0.5) / N) + Coeff / 4))) / (N + Coeff);

                    return (N * Pr - e);
                }
            }
        }
    }
}

