/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010

	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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

/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    RandomTree.java
 *    Copyright (C) 2001 University of Waikato, Hamilton, New Zealand
 *
 */

package keel.Algorithms.Semi_Supervised_Learning.ADE_CoForest;

import keel.Algorithms.Semi_Supervised_Learning.utilities.RandomGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeSet;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerator;
import keel.Algorithms.Semi_Supervised_Learning.Basic.Prototype;
import keel.Algorithms.Semi_Supervised_Learning.Basic.PrototypeGenerationAlgorithm;

import keel.Algorithms.Semi_Supervised_Learning.*;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;

/*
import weka.classifiers.Classifier;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.ContingencyTables;
import weka.core.Drawable;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Randomizable;
import weka.core.RevisionUtils;
import weka.core.Utils;
import weka.core.WeightedInstancesHandler;
import weka.core.Capabilities.Capability;
*/

import java.util.Enumeration;
import java.util.Random;
import java.util.Vector;

import org.core.Randomize;

/** RandomTree
 * <!-- globalinfo-start -->
 * Class for constructing a tree that considers K randomly  chosen attributes at each node. Performs no pruning. Also has an option to allow estimation of class probabilities based on a hold-out set (backfitting).
 * <p/>
 * <!-- globalinfo-end -->
 * 
 * <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre> -K &lt;number of attributes&gt;
 *  Number of attributes to randomly investigate
 *  (&lt;0 = int(log_2(#attributes)+1)).</pre>
 * 
 * <pre> -M &lt;minimum number of instances&gt;
 *  Set minimum number of instances per leaf.</pre>
 * 
 * <pre> -S &lt;num&gt;
 *  Seed for random number generator.
 *  (default 1)</pre>
 * 
 * <pre> -depth &lt;num&gt;
 *  The maximum depth of the tree, 0 for unlimited.
 *  (default 0)</pre>
 * 
 * <pre> -N &lt;num&gt;
 *  Number of folds for backfitting (default 0, no backfitting).</pre>
 * 
 * <pre> -U
 *  Allow unclassified instances.</pre>
 * 
 * <pre> -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console</pre>
 * 
 * <!-- options-end -->
 * 
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @author Richard Kirkby (rkirkby@cs.waikato.ac.nz)
 * @version $Revision: 5928 $
 */
public class RandomTree  {//extends AbstractClassifier implements OptionHandler,
//WeightedInstancesHandler, Randomizable, Drawable {

  /** for serialization */
  static final long serialVersionUID = 8934314652175299374L;

  /** The subtrees appended to this tree. */
  protected RandomTree[] m_Successors;

  /** The attribute to split on. */
  protected int m_Attribute = -1;

  /** The split point. */
  protected double m_SplitPoint = Double.NaN;

  /** The header information. */
  protected PrototypeSet m_Info = null;

  /** The proportions of training instances going down each branch. */
  protected double[] m_Prop = null;

  /** Class probabilities from the training data. */
  protected double[] m_ClassDistribution = null;

  /** Minimum number of instances for leaf. */
  protected double m_MinNum = 1.0;

  /** The number of attributes considered for a split. */
  protected int m_KValue = 0;

  /** The random seed to use. */
  protected int m_randomSeed = 1;

  /** The maximum depth of the tree (0 = unlimited) */
  protected int m_MaxDepth = 0;

  /** Determines how much data is used for backfitting */
  protected int m_NumFolds = 0;

    /**
     * Debug flag.
     */
    protected boolean m_Debug = false; 

    /**
     * Number of classes of the problem.
     */
    protected int nClasses = 0;
  
  /** Whether unclassified instances are allowed */
  protected boolean m_AllowUnclassifiedInstances = false;

  /** a ZeroR model in case no model can be built from the data */
//  protected Classifier m_ZeroR;

  /**
   * Returns a string describing classifier
   * 
   * @return a description suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String globalInfo() {

    return "Class for constructing a tree that considers K randomly "
    + " chosen attributes at each node. Performs no pruning. Also has"
    + " an option to allow estimation of class probabilities based on"
    + " a hold-out set (backfitting).";
  }

  /**
   * Returns the tip text for this property
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String minNumTipText() {
    return "The minimum total weight of the instances in a leaf.";
  }

  /**
   * Get the value of MinNum.
   * 
   * @return Value of MinNum.
   */
  public double getMinNum() {

    return m_MinNum;
  }

  /**
   * Set the value of MinNum.
   * 
   * @param newMinNum
   *            Value to assign to MinNum.
   */
  public void setMinNum(double newMinNum) {

    m_MinNum = newMinNum;
  }

  /**
   * Returns the tip text for this property
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String KValueTipText() {
    return "Sets the number of randomly chosen attributes. If 0, log_2(number_of_attributes) + 1 is used.";
  }

  /**
   * Get the value of K.
   * 
   * @return Value of K.
   */
  public int getKValue() {

    return m_KValue;
  }

  /**
   * Set the value of K.
   * 
   * @param k
   *            Value to assign to K.
   */
  public void setKValue(int k) {

    m_KValue = k;
  }

  /**
   * Returns the tip text for this property
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String seedTipText() {
    return "The random number seed used for selecting attributes.";
  }

  /**
   * Set the seed for random number generation.
   * 
   * @param seed
   *            the seed
   */
  public void setSeed(int seed) {

    m_randomSeed = seed;
  }

  /**
   * Gets the seed for the random number generations
   * 
   * @return the seed for the random number generation
   */
  public int getSeed() {

    return m_randomSeed;
  }

  /**
   * Returns the tip text for this property
   * 
   * @return tip text for this property suitable for displaying in the
   *         explorer/experimenter gui
   */
  public String maxDepthTipText() {
    return "The maximum depth of the tree, 0 for unlimited.";
  }

  /**
   * Get the maximum depth of trh tree, 0 for unlimited.
   * 
   * @return the maximum depth.
   */
  public int getMaxDepth() {
    return m_MaxDepth;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String numFoldsTipText() {
    return "Determines the amount of data used for backfitting. One fold is used for "
      + "backfitting, the rest for growing the tree. (Default: 0, no backfitting)";
  }
  
  /**
   * Get the value of NumFolds.
   *
   * @return Value of NumFolds.
   */
  public int getNumFolds() {
    
    return m_NumFolds;
  }
  
  /**
   * Set the value of NumFolds.
   *
   * @param newNumFolds Value to assign to NumFolds.
   */
  public void setNumFolds(int newNumFolds) {
    
    m_NumFolds = newNumFolds;
  }

  /**
   * Returns the tip text for this property
   * @return tip text for this property suitable for
   * displaying in the explorer/experimenter gui
   */
  public String allowUnclassifiedInstancesTipText() {
    return "Whether to allow unclassified instances.";
  }
  
  /**
   * Get the value of NumFolds.
   *
   * @return Value of NumFolds.
   */
  public boolean getAllowUnclassifiedInstances() {
    
    return m_AllowUnclassifiedInstances;
  }
  
  /**
   * Set the value of AllowUnclassifiedInstances.
   *
   * @param newAllowUnclassifiedInstances Value to assign to AllowUnclassifiedInstances.
   */
  public void setAllowUnclassifiedInstances(boolean newAllowUnclassifiedInstances) {
    
    m_AllowUnclassifiedInstances = newAllowUnclassifiedInstances;
  }

  /**
   * Set the maximum depth of the tree, 0 for unlimited.
   * 
   * @param value
   *            the maximum depth.
   */
  public void setMaxDepth(int value) {
    m_MaxDepth = value;
  }

  /**
   * Lists the command-line options for this classifier.
   * 
   * @return an enumeration over all possible options

  public Enumeration listOptions() {

    Vector newVector = new Vector();

    newVector.addElement(new Option(
        "\tNumber of attributes to randomly investigate\n"
        + "\t(<0 = int(log_2(#attributes)+1)).", "K", 1,
    "-K <number of attributes>"));

    newVector.addElement(new Option(
        "\tSet minimum number of instances per leaf.", "M", 1,
    "-M <minimum number of instances>"));

    newVector.addElement(new Option("\tSeed for random number generator.\n"
        + "\t(default 1)", "S", 1, "-S <num>"));

    newVector.addElement(new Option(
        "\tThe maximum depth of the tree, 0 for unlimited.\n"
        + "\t(default 0)", "depth", 1, "-depth <num>"));

    newVector.
      addElement(new Option("\tNumber of folds for backfitting " +
			    "(default 0, no backfitting).",
                            "N", 1, "-N <num>"));
    newVector.
      addElement(new Option("\tAllow unclassified instances.",
			    "U", 0, "-U"));

    Enumeration enu = super.listOptions();
    while (enu.hasMoreElements()) {
      newVector.addElement(enu.nextElement());
    }

    return newVector.elements();
  }
   
  /**
   * Gets options from this classifier.
   * 
   * @return the options for the current setup
   
  public String[] getOptions() {
    Vector result;
    String[] options;
    int i;

    result = new Vector();

    result.add("-K");
    result.add("" + getKValue());

    result.add("-M");
    result.add("" + getMinNum());

    result.add("-S");
    result.add("" + getSeed());

    if (getMaxDepth() > 0) {
      result.add("-depth");
      result.add("" + getMaxDepth());
    }

    if (getNumFolds() > 0) {
      result.add("-N"); 
      result.add("" + getNumFolds());
    }

    if (getAllowUnclassifiedInstances()) {
      result.add("-U");
    }

    options = super.getOptions();
    for (i = 0; i < options.length; i++)
      result.add(options[i]);

    return (String[]) result.toArray(new String[result.size()]);
  }
  */

  /**
   * Parses a given list of options.
   * <p/>
   * 
   * <!-- options-start -->
   * Valid options are: <p/>
   * 
   * <pre> -K &lt;number of attributes&gt;
   *  Number of attributes to randomly investigate
   *  (&lt;0 = int(log_2(#attributes)+1)).</pre>
   * 
   * <pre> -M &lt;minimum number of instances&gt;
   *  Set minimum number of instances per leaf.</pre>
   * 
   * <pre> -S &lt;num&gt;
   *  Seed for random number generator.
   *  (default 1)</pre>
   * 
   * <pre> -depth &lt;num&gt;
   *  The maximum depth of the tree, 0 for unlimited.
   *  (default 0)</pre>
   * 
   * <pre> -N &lt;num&gt;
   *  Number of folds for backfitting (default 0, no backfitting).</pre>
   * 
   * <pre> -U
   *  Allow unclassified instances.</pre>
   * 
   * <pre> -D
   *  If set, classifier is run in debug mode and
   *  may output additional info to the console</pre>
   * 
   * <!-- options-end -->
   * 
   * @param options
   *            the list of options as an array of strings
   * @throws Exception
   *             if an option is not supported

  public void setOptions(String[] options) throws Exception {
    String tmpStr;

    tmpStr = Utils.getOption('K', options);
    if (tmpStr.length() != 0) {
      m_KValue = Integer.parseInt(tmpStr);
    } else {
      m_KValue = 0;
    }

    tmpStr = Utils.getOption('M', options);
    if (tmpStr.length() != 0) {
      m_MinNum = Double.parseDouble(tmpStr);
    } else {
      m_MinNum = 1;
    }

    tmpStr = Utils.getOption('S', options);
    if (tmpStr.length() != 0) {
      setSeed(Integer.parseInt(tmpStr));
    } else {
      setSeed(1);
    }

    tmpStr = Utils.getOption("depth", options);
    if (tmpStr.length() != 0) {
      setMaxDepth(Integer.parseInt(tmpStr));
    } else {
      setMaxDepth(0);
    }
    String numFoldsString = Utils.getOption('N', options);
    if (numFoldsString.length() != 0) {
      m_NumFolds = Integer.parseInt(numFoldsString);
    } else {
      m_NumFolds = 0;
    }

    setAllowUnclassifiedInstances(Utils.getFlag('U', options));

    super.setOptions(options);

    Utils.checkForRemainingOptions(options);
  }
  
     */

  /**
   * Returns default capabilities of the classifier.
   * 
   * @return the capabilities of this classifier
  
  public Capabilities getCapabilities() {
    Capabilities result = super.getCapabilities();
    result.disableAll();

    // attributes
    result.enable(Capability.NOMINAL_ATTRIBUTES);
    result.enable(Capability.NUMERIC_ATTRIBUTES);
    result.enable(Capability.DATE_ATTRIBUTES);
    result.enable(Capability.MISSING_VALUES);

    // class
    result.enable(Capability.NOMINAL_CLASS);
    result.enable(Capability.MISSING_CLASS_VALUES);

    return result;
  }
 */
  /**
   * Builds classifier.
   * 
   * @param data
   *            the data to train with
   * @throws Exception
   *             if something goes wrong or the data doesn't fit
   */
  public void buildClassifier(PrototypeSet data) throws Exception {

	  this.nClasses = data.getPosibleValuesOfOutput().size();
	  

    // Make sure K value is in range
    if (m_KValue > data.size() - 1)
      m_KValue = data.size() - 1;
    if (m_KValue < 1)
      m_KValue = (int) (Math.log(data.get(0).numberOfInputs())/Math.log(2)) + 1;

    // can classifier handle the data?
 //   getCapabilities().testWithFail(data);

    // Figure out appropriate datasets
    PrototypeSet train = null;
    PrototypeSet backfit = null;
  
    //Random rand = data.getRandomNumberGenerator(m_randomSeed);
    if (m_NumFolds <= 0) {
      train = data;
    } else {
      data.randomize();
      data.stratify(m_NumFolds);
      train = data.trainCV(m_NumFolds, 1).clone(); // randomize included
      backfit = data.testCV(m_NumFolds, 1).clone();
    }

    // Create the attribute indices window
    int[] attIndicesWindow = new int[data.get(0).numberOfInputs() - 1];
    int j = 0;
    for (int i = 0; i < attIndicesWindow.length; i++) {
      if (j == data.get(0).numberOfInputs() )  //data.classIndex()
        j++; // do not include the class
      attIndicesWindow[i] = j++;
    }

    // Compute initial class counts
    double[] classProbs = new double[this.nClasses];
    for (int i = 0; i < train.size(); i++) {
      Prototype inst = train.get(i);
      classProbs[(int) inst.getOutput(0)] += inst.getWeight();
    }

    // Build tree 
    buildTree(train, classProbs, new PrototypeSet(), m_MinNum, m_Debug, attIndicesWindow,  0, getAllowUnclassifiedInstances());
      
    // Backfit if required
    if (backfit != null) {
      backfitData(backfit);
    }
  }

  /**
   * Backfits the given data into the tree.
     * @param data data used to backfit it into the tree.
     * @throws java.lang.Exception if the backfitting can not be done.
   */
  public void backfitData(PrototypeSet data) throws Exception {

    // Compute initial class counts
    double[] classProbs = new double[this.nClasses];
    for (int i = 0; i < data.size(); i++) {
      Prototype inst = data.get(i);
      classProbs[(int) inst.getOutput(0)] += inst.getWeight();
    }

    // Fit data into tree
    backfitData(data, classProbs);
  }

  /**
   * Computes class distribution of an instance using the decision tree.
   * 
   * @param instance
   *            the instance to compute the distribution for
   * @return the computed class distribution
   * @throws Exception
   *             if computation fails
   */
  public double[] distributionForInstance(Prototype instance) throws Exception {


    double[] returnedDist = null;

 //   System.out.println("DISTRUBITON FOR INSTANCE = "+m_Attribute);
   // System.out.println("Number of class "+ this.nClasses);
    
    if (m_Attribute > -1) {

      // Node is not a leaf
      /*if (instance.isMissing(m_Attribute)) {

        // Value is missing
        returnedDist = new double[this.nClasses]; //m_Info.numClasses()

        // Split instance up
        for (int i = 0; i < m_Successors.length; i++) {
          double[] help = m_Successors[i]
                                       .distributionForInstance(instance);
          if (help != null) {
            for (int j = 0; j < help.length; j++) {
              returnedDist[j] += m_Prop[i] * help[j];
            }
          }
        }
      } else */
    	
    	// no hay missing values en mi programa.
    	
       if (Attributes.getInputAttribute(m_Attribute).getType() == Attribute.NOMINAL)  {

        // For nominal attributes
        returnedDist = m_Successors[(int) instance.getInput(m_Attribute)].distributionForInstance(instance);
      } else {

        // For numeric attributes
        if (instance.getInput(m_Attribute) < m_SplitPoint) {
          returnedDist = m_Successors[0].distributionForInstance(instance);
        } else {
          returnedDist = m_Successors[1].distributionForInstance(instance);
        }
      }
    }


    // Node is a leaf or successor is empty?
    if ((m_Attribute == -1) || (returnedDist == null)) {
 
      // Is node empty?
      if (m_ClassDistribution == null) {
        if (getAllowUnclassifiedInstances()) {
        
          return new double[this.nClasses]; //m_Info.numClasses()
        } else {
          return null;
        }
      }

      // Else return normalized distribution
      double[] normalizedDistribution = (double[]) m_ClassDistribution.clone();
      
      //    Utils.normalize(normalizedDistribution);
      double sumatoria =0;
      
      for(int j=0; j<normalizedDistribution.length; j++){
    	  sumatoria+=normalizedDistribution[j];
      }
      
      for(int j=0; j<normalizedDistribution.length; j++){
    	  normalizedDistribution[j]/=sumatoria;
      }
      
  
      
      return normalizedDistribution;
    } else {
      return returnedDist;
    }
  }

  /**
   * Outputs the decision tree as a graph
   * 
   * @return the tree as a graph
  
  public String toGraph() {

    try {
      StringBuffer resultBuff = new StringBuffer();
      toGraph(resultBuff, 0);
      String result = "digraph Tree {\n" + "edge [style=bold]\n"
      + resultBuff.toString() + "\n}\n";
      return result;
    } catch (Exception e) {
      return null;
    }
  }
 */
  /**
   * Outputs one node for graph.
   * 
   * @param text
   *            the buffer to append the output to
   * @param num
   *            unique node id
   * @return the next node id
   * @throws Exception
   *             if generation fails
   
  public int toGraph(StringBuffer text, int num) throws Exception {

    int maxIndex = Utils.maxIndex(m_ClassDistribution);
    String classValue = m_Info.classAttribute().value(maxIndex);

    num++;
    if (m_Attribute == -1) {
      text.append("N" + Integer.toHexString(hashCode()) + " [label=\""
          + num + ": " + classValue + "\"" + "shape=box]\n");
    } else {
      text.append("N" + Integer.toHexString(hashCode()) + " [label=\""
          + num + ": " + classValue + "\"]\n");
      for (int i = 0; i < m_Successors.length; i++) {
        text.append("N" + Integer.toHexString(hashCode()) + "->" + "N"
            + Integer.toHexString(m_Successors[i].hashCode())
            + " [label=\"" + m_Info.attribute(m_Attribute).name());
        if (m_Info.attribute(m_Attribute).isNumeric()) {
          if (i == 0) {
            text.append(" < "
                + Utils.doubleToString(m_SplitPoint, 2));
          } else {
            text.append(" >= "
                + Utils.doubleToString(m_SplitPoint, 2));
          }
        } else {
          text.append(" = " + m_Info.attribute(m_Attribute).value(i));
        }
        text.append("\"]\n");
        num = m_Successors[i].toGraph(text, num);
      }
    }

    return num;
  }

*/
  /**
   * Outputs the decision tree.
   * 
   * @return a string representation of the classifier
   
  public String toString() {

    // only ZeroR model?
    if (m_ZeroR != null) {
      StringBuffer buf = new StringBuffer();
      buf
      .append(this.getClass().getName().replaceAll(".*\\.", "")
          + "\n");
      buf.append(this.getClass().getName().replaceAll(".*\\.", "")
          .replaceAll(".", "=")
          + "\n\n");
      buf
      .append("Warning: No model could be built, hence ZeroR model is used:\n\n");
      buf.append(m_ZeroR.toString());
      return buf.toString();
    }

    if (m_Successors == null) {
      return "RandomTree: no model has been built yet.";
    } else {
      return "\nRandomTree\n==========\n"
      + toString(0)
      + "\n"
      + "\nSize of the tree : "
      + numNodes()
      + (getMaxDepth() > 0 ? ("\nMax depth of tree: " + getMaxDepth())
          : (""));
    }
  }

*/
  /**
   * Outputs a leaf.
   * 
   * @return the leaf as string
   * @throws Exception
   *             if generation fails
 
  protected String leafString() throws Exception {

    double sum = 0, maxCount = 0;
    int maxIndex = 0;
    if (m_ClassDistribution != null) {
      sum = Utils.sum(m_ClassDistribution);
      maxIndex = Utils.maxIndex(m_ClassDistribution);
      maxCount = m_ClassDistribution[maxIndex];
    } 
    return " : "
    + m_Info.classAttribute().value(maxIndex)
    + " ("
    + Utils.doubleToString(sum, 2)
    + "/"
    + Utils.doubleToString(sum - maxCount, 2) + ")";
  }
  */
  /**
   * Recursively outputs the tree.
   * 
   * @param level
   *            the current level of the tree
   * @return the generated subtree
  
  protected String toString(int level) {

    try {
      StringBuffer text = new StringBuffer();

      if (m_Attribute == -1) {

        // Output leaf info
        return leafString();
      } else if (m_Info.attribute(m_Attribute).isNominal()) {

        // For nominal attributes
        for (int i = 0; i < m_Successors.length; i++) {
          text.append("\n");
          for (int j = 0; j < level; j++) {
            text.append("|   ");
          }
          text.append(m_Info.attribute(m_Attribute).name() + " = "
              + m_Info.attribute(m_Attribute).value(i));
          text.append(m_Successors[i].toString(level + 1));
        }
      } else {

        // For numeric attributes
        text.append("\n");
        for (int j = 0; j < level; j++) {
          text.append("|   ");
        }
        text.append(m_Info.attribute(m_Attribute).name() + " < "
            + Utils.doubleToString(m_SplitPoint, 2));
        text.append(m_Successors[0].toString(level + 1));
        text.append("\n");
        for (int j = 0; j < level; j++) {
          text.append("|   ");
        }
        text.append(m_Info.attribute(m_Attribute).name() + " >= "
            + Utils.doubleToString(m_SplitPoint, 2));
        text.append(m_Successors[1].toString(level + 1));
      }

      return text.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return "RandomTree: tree can't be printed";
    }
  }
 */
  /**
   * Recursively backfits data into the tree.
   * 
   * @param data
   *            the data to work with
   * @param classProbs
   *            the class distribution
   * @throws Exception
   *             if generation fails
   */
  protected void backfitData(PrototypeSet data, double[] classProbs) throws Exception {

    // Make leaf if there are no training instances
    if (data.size() == 0) {
      m_Attribute = -1;
      m_ClassDistribution = null;
      m_Prop = null;
      return;
    }

    // Check if node doesn't contain enough instances or is pure
    // or maximum depth reached
    m_ClassDistribution = (double[]) classProbs.clone();

    /*    if (Utils.sum(m_ClassDistribution) < 2 * m_MinNum
        || Utils.eq(m_ClassDistribution[Utils.maxIndex(m_ClassDistribution)], Utils
                    .sum(m_ClassDistribution))) {
      
      // Make leaf
      m_Attribute = -1;
      m_Prop = null;
      return;
      }*/

    // Are we at an inner node
    if (m_Attribute > -1) {
      
      // Compute new weights for subsets based on backfit data
      m_Prop = new double[m_Successors.length];
      for (int i = 0; i < data.size(); i++) {
        Prototype inst = data.get(i);
      //  if (!inst.isMissing(m_Attribute)) {
          if (Attributes.getInputAttribute(m_Attribute).getType() == Attribute.NOMINAL) { //data.attribute(m_Attribute).isNominal()
            m_Prop[(int)inst.getInput(m_Attribute)] += inst.getWeight();
          } else {
            m_Prop[(inst.getInput(m_Attribute) < m_SplitPoint) ? 0 : 1] += inst.getWeight();
          }
        //}
      }

      // If we only have missing values we can make this node into a leaf
      double sum_proc =0;
      
      for(int j=0; j<m_Prop.length; j++){
    	  sum_proc+=m_Prop[j];
      }
      if (sum_proc <= 0) { //Utils.sum(m_Prop)
        m_Attribute = -1;
        m_Prop = null;
        return;
      }

      // Otherwise normalize the proportions
      //Utils.normalize(m_Prop);
      for(int j=0; j<m_Prop.length; j++){
    	 m_Prop[j]/= sum_proc;
      }
      
      // Split data
      PrototypeSet[] subsets = splitData(data);
      
      // Go through subsets
      for (int i = 0; i < subsets.length; i++) {
        
        // Compute distribution for current subset
        double[] dist = new double[this.nClasses];
        for (int j = 0; j < subsets[i].size(); j++) {
          dist[(int)subsets[i].get(j).getOutput(0)] += subsets[i].get(j).getWeight();
        }
        
        // Backfit subset
        m_Successors[i].backfitData(subsets[i], dist);
      }

      // If unclassified instances are allowed, we don't need to store the class distribution
      if (getAllowUnclassifiedInstances()) {
        m_ClassDistribution = null;
        return;
      }

      // Otherwise, if all successors are non-empty, we don't need to store the class distribution
      boolean emptySuccessor = false;
      for (int i = 0; i < subsets.length; i++) {
        if (m_Successors[i].m_ClassDistribution == null) {
          emptySuccessor = true;
          return;
        }
      }
      m_ClassDistribution = null;
      
      // If we have a least two non-empty successors, we should keep this tree
      /*      int nonEmptySuccessors = 0;
      for (int i = 0; i < subsets.length; i++) {
        if (m_Successors[i].m_ClassDistribution != null) {
          nonEmptySuccessors++;
          if (nonEmptySuccessors > 1) {
            return;
          }
        }
      }
      
      // Otherwise, this node is a leaf or should become a leaf
      m_Successors = null;
      m_Attribute = -1;
      m_Prop = null;
      return;*/
    }
  }

  /**
   * Recursively generates a tree.
   * 
   * @param data
   *            the data to work with
   * @param classProbs
   *            the class distribution
   * @param header
   *            the header of the data
   * @param minNum
   *            the minimum number of instances per leaf
   * @param debug
   *            whether debugging is on
   * @param attIndicesWindow
   *            the attribute window to choose attributes from
   * @param depth
   *            the current depth
     * @param allow flag to allow unclassified instances or not.
   * @throws Exception
   *             if generation fails
   */
  protected void buildTree(PrototypeSet data, double[] classProbs, PrototypeSet header,
                           double minNum, boolean debug, int[] attIndicesWindow,
                            int depth, boolean allow) throws Exception {

    // Store structure of dataset, set minimum number of instances
    m_Info = header;
    m_Debug = debug;
    m_MinNum = minNum;
    m_AllowUnclassifiedInstances = allow;

    // Make leaf if there are no training instances
    if (data.size() == 0) {
      m_Attribute = -1;
      m_ClassDistribution = null;
      m_Prop = null;
      return;
    }
    
   // System.out.println("HE ENTRAO!");

    // Check if node doesn't contain enough instances or is pure
    // or maximum depth reached
    m_ClassDistribution = (double[]) classProbs.clone();

    if (Utils.sum(m_ClassDistribution) < 2 * m_MinNum
        || Utils.eq(m_ClassDistribution[Utils.maxIndex(m_ClassDistribution)], Utils
            .sum(m_ClassDistribution))
            || ((getMaxDepth() > 0) && (depth >= getMaxDepth()))) {
      // Make leaf
      m_Attribute = -1;
      m_Prop = null;
      return;
    }

    // Compute class distributions and value of splitting
    // criterion for each attribute
    double[] vals = new double[data.get(0).numberOfInputs()];
    double[][][] dists = new double[data.get(0).numberOfInputs()][0][0];
    double[][] props = new double[data.get(0).numberOfInputs()][0];
    double[] splits = new double[data.get(0).numberOfInputs()];
    
    // Investigate K random attributes
    int attIndex = 0;
    int windowSize = attIndicesWindow.length;
    int k = m_KValue;
    boolean gainFound = false;
    while ((windowSize > 0) && (k-- > 0 || !gainFound)) {
      
      int chosenIndex = RandomGenerator.Randint(0,windowSize);//random.nextInt(windowSize);
      attIndex = attIndicesWindow[chosenIndex];
      
      // shift chosen attIndex out of window
      attIndicesWindow[chosenIndex] = attIndicesWindow[windowSize - 1];
      attIndicesWindow[windowSize - 1] = attIndex;
      windowSize--;
      
	  //System.out.println("NÃºmero de clases: "+ this.nClasses);
      splits[attIndex] = distribution(props, dists, attIndex, data);
      vals[attIndex] = gain(dists[attIndex], priorVal(dists[attIndex]));
      
      if (Utils.gr(vals[attIndex], 0))
        gainFound = true;
    }
      
    //System.out.println("SALGO DEL BUCLE MALDITO");
    // Find best attribute
    m_Attribute = Utils.maxIndex(vals);
    double[][] distribution = dists[m_Attribute];

    // Any useful split found? 
    if (Utils.gr(vals[m_Attribute], 0)) {

      // Build subtrees
      m_SplitPoint = splits[m_Attribute];
      m_Prop = props[m_Attribute];
      PrototypeSet[] subsets = splitData(data.clone());
      
      //subsets[0].print();
      m_Successors = new RandomTree[distribution.length];
      
      for (int i = 0; i < distribution.length; i++) {
    //	  System.out.println("distribuciÃ³n: "+i);
        m_Successors[i] = new RandomTree();
        m_Successors[i].nClasses = this.nClasses;
        m_Successors[i].setKValue(m_KValue);
        m_Successors[i].setMaxDepth(getMaxDepth());
        m_Successors[i].buildTree(subsets[i], distribution[i], header, m_MinNum, m_Debug, attIndicesWindow,  depth + 1, allow);
      }

      // If all successors are non-empty, we don't need to store the class distribution
      boolean emptySuccessor = false;
      for (int i = 0; i < subsets.length; i++) {
        if (m_Successors[i].m_ClassDistribution == null) {
          emptySuccessor = true;
          break;
        }
      }
      if (!emptySuccessor) {
        m_ClassDistribution = null;
      }
    } else {

      // Make leaf
      m_Attribute = -1;
    }
  }

  /**
   * Computes size of the tree.
   * 
   * @return the number of nodes
   */
  public int numNodes() {

    if (m_Attribute == -1) {
      return 1;
    } else {
      int size = 1;
      for (int i = 0; i < m_Successors.length; i++) {
        size += m_Successors[i].numNodes();
      }
      return size;
    }
  }

  /**
   * Splits instances into subsets based on the given split.
   * 
   * @param data
   *            the data to work with
   * @return  the subsets of instances
   * @throws Exception
   *             if something goes wrong
   */
  protected PrototypeSet[] splitData(PrototypeSet data) throws Exception {

    // Allocate array of Instances objects
	  PrototypeSet[] subsets = new PrototypeSet[m_Prop.length];
    for (int i = 0; i < m_Prop.length; i++) {
      subsets[i] = new PrototypeSet(); //data, data.numInstances());
    }

    // Go through the data
    for (int i = 0; i < data.size(); i++) {

      // Get instance
      Prototype inst = data.get(i);

      // Does the instance have a missing value?
    /*  if (inst.isMissing(m_Attribute)) {
        
        // Split instance up
        for (int k = 0; k < m_Prop.length; k++) {
          if (m_Prop[k] > 0) {
        	  Prototype copy = new Prototype(inst);
            copy.setWeight(m_Prop[k] * inst.getWeight());
            subsets[k].add(copy);
          }
        }

        // Proceed to next instance
        continue;
      }*/

      // Do we have a nominal attribute?
      if (Attributes.getInputAttribute(m_Attribute).getType() == Attribute.NOMINAL) {//data.attribute(m_Attribute).isNominal()
        subsets[(int)inst.getInput(m_Attribute)].add(inst);

        // Proceed to next instance
        continue;
      }

      // Do we have a numeric attribute?
      if (!(Attributes.getInputAttribute(m_Attribute).getType() == Attribute.NOMINAL)) { // ata.attribute(m_Attribute).isNumeric()
        subsets[(inst.getInput(m_Attribute) < m_SplitPoint) ? 0 : 1].add(inst);

        // Proceed to next instance
        continue;
      }
      
      // Else throw an exception
      throw new IllegalArgumentException("Unknown attribute type");
    }

    // Save memory: NO SE LO QUE HACE!
    /*for (int i = 0; i < m_Prop.length; i++) {
      subsets[i].compactify();
    }
*/
    // Return the subsets
    return subsets;
  }

  /**
   * Computes class distribution for an attribute.
   * 
   * @param props
   * @param dists
   * @param att
   *            the attribute index
   * @param data
   *            the data to work with
     * @return  class distribution for an attribute.
   * @throws Exception
   *             if something goes wrong
   */
  protected double distribution(double[][] props, double[][][] dists, int att, PrototypeSet data)  throws Exception {

    double splitPoint = Double.NaN;
    Attribute attribute = Attributes.getAttribute(att); // data.attribute(att);
    double[][] dist = null;
    int indexOfFirstMissingValue = -1;

    if (attribute.getType() == Attribute.NOMINAL) {

      // For nominal attributes
      dist = new double[attribute.getNumNominalValues()][this.nClasses]; //.numValues()
      for (int i = 0; i < data.size(); i++) {
        Prototype inst = data.get(i);
       /* if (inst.isMissing(att)) {

          // Skip missing values at this stage
          if (indexOfFirstMissingValue < 0) {
            indexOfFirstMissingValue = i;
          }
          continue;
        }*/
        dist[(int) inst.getInput(att)][(int) inst.getOutput(0)] += inst.getWeight();
      }
    } else {

      // For numeric attributes
      double[][] currDist = new double[2][this.nClasses];
      dist = new double[2][this.nClasses];

      // Sort data
      data.sort(att);

      // Move all instances into second subset
      for (int j = 0; j < data.size(); j++) {
    	  Prototype inst = data.get(j);
        /*if (inst.isMissing(att)) {

          // Can stop as soon as we hit a missing value
          indexOfFirstMissingValue = j;
          break;
        }*/
    	//  System.out.println("Clases : " +this.nClasses);
    	 // System.out.println("Acceso a: "+(int) inst.getOutput(0));
        currDist[1][(int) inst.getOutput(0)] += inst.getWeight();
      }

      // Value before splitting
      double priorVal = priorVal(currDist);

      // Save initial distribution
      for (int j = 0; j < currDist.length; j++) {
        System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
      }

      // Try all possible split points
      double currSplit = data.get(0).getInput(att);
      double currVal, bestVal = -Double.MAX_VALUE;
      for (int i = 0; i < data.size(); i++) {
        Prototype inst = data.get(i);
       /* if (inst.isMissing(att)) {

          // Can stop as soon as we hit a missing value
          break;
        }*/

        // Can we place a sensible split point here?
        if (inst.getInput(att) > currSplit) {

          // Compute gain for split point
          currVal = gain(currDist, priorVal);

          // Is the current split point the best point so far?
          if (currVal > bestVal) {

            // Store value of current point
            bestVal = currVal;

            // Save split point
            splitPoint = (inst.getInput(att) + currSplit) / 2.0;

            // Save distribution
            for (int j = 0; j < currDist.length; j++) {
              System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
            }
          }
        }
        currSplit = inst.getInput(att);

        // Shift over the weight
        currDist[0][(int) inst.getOutput(0)] += inst.getWeight();
        currDist[1][(int) inst.getOutput(0)] -= inst.getWeight();
      }
    }

    // Compute weights for subsets
    props[att] = new double[dist.length];
    for (int k = 0; k < props[att].length; k++) {
    	
    	double sum=0;
    	for(int z=0; z<dist[k].length;z++){
    		sum+=dist[k][z];
    	}
    	
      props[att][k] =  sum;//Utils.sum(dist[k]);
    }
    
    if (Utils.eq(Utils.sum(props[att]), 0)) {
      for (int k = 0; k < props[att].length; k++) {
        props[att][k] = 1.0 / (double) props[att].length;
      }
    } else {
      Utils.normalize(props[att]);
    }

    // Any instances with missing values ?
    if (indexOfFirstMissingValue > -1) {

      // Distribute weights for instances with missing values
      for (int i = indexOfFirstMissingValue; i < data.size(); i++) {
    	  Prototype inst = data.get(i);
        if (!(Attributes.getInputAttribute(m_Attribute).getType() == Attribute.NOMINAL)) {

         // Can be sure that value is missing, so no test required
          for (int j = 0; j < dist.length; j++) {
            dist[j][(int) inst.getOutput(0)] += props[att][j] * inst.getWeight();
          }
        }
      }
    }

    // Return distribution and split point
    dists[att] = dist;
    return splitPoint;
  }

  /**
   * Computes value of splitting criterion before split.
   * 
   * @param dist
   *            the distributions
   * @return the splitting criterion
   */
  protected double priorVal(double[][] dist) {

    return ContingencyTables.entropyOverColumns(dist);
  }

  /**
   * Computes value of splitting criterion after split.
   * 
   * @param dist
   *            the distributions
   * @param priorVal
   *            the splitting criterion
   * @return the gain after the split
   */
  protected double gain(double[][] dist, double priorVal) {

    return priorVal - ContingencyTables.entropyConditionedOnRows(dist);
  }
  

  /**
   * Returns the revision string.
   * 
   * @return the revision
   
  public String getRevision() {
    return RevisionUtils.extract("$Revision: 5928 $");
  }
*/
  /**
   * Main method for this class.
   * 
   * @param argv
   *            the commandline parameters
   */
  public static void main(String[] argv) {
   // runClassifier(new RandomTree(), argv);
  }

  /**
   * Returns graph describing the tree.
   * 
   * @return the graph describing the tree
   * @throws Exception
   *             if graph can't be computed
   
  public String graph() throws Exception {

    if (m_Successors == null) {
      throw new Exception("RandomTree: No model built yet.");
    }
    StringBuffer resultBuff = new StringBuffer();
    toGraph(resultBuff, 0, null);
    String result = "digraph RandomTree {\n" + "edge [style=bold]\n"
    + resultBuff.toString() + "\n}\n";
    return result;
  }

*/
  /**
   * Returns the type of graph this classifier represents.
   * 
   * @return Drawable.TREE

  public int graphType() {
    return Drawable.TREE;
  }
   */
  /**
   * Outputs one node for graph.
   * 
   * @param text
   *            the buffer to append the output to
   * @param num
   *            the current node id
   * @param parent
   *            the parent of the nodes
   * @return the next node id
   * @throws Exception
   *             if something goes wrong
  
  protected int toGraph(StringBuffer text, int num, RandomTree parent)
  throws Exception {

    num++;
    if (m_Attribute == -1) {
      text.append("N" + Integer.toHexString(RandomTree.this.hashCode())
          + " [label=\"" + num + leafString() + "\""
          + " shape=box]\n");

    } else {
      text.append("N" + Integer.toHexString(RandomTree.this.hashCode())
          + " [label=\"" + num + ": "
          + m_Info.attribute(m_Attribute).name() + "\"]\n");
      for (int i = 0; i < m_Successors.length; i++) {
        text.append("N"
            + Integer.toHexString(RandomTree.this.hashCode())
            + "->" + "N"
            + Integer.toHexString(m_Successors[i].hashCode())
            + " [label=\"");
        if (m_Info.attribute(m_Attribute).isNumeric()) {
          if (i == 0) {
            text.append(" < "
                + Utils.doubleToString(m_SplitPoint, 2));
          } else {
            text.append(" >= "
                + Utils.doubleToString(m_SplitPoint, 2));
          }
        } else {
          text.append(" = " + m_Info.attribute(m_Attribute).value(i));
        }
        text.append("\"]\n");
        num = m_Successors[i].toGraph(text, num, this);
      }
    }

    return num;
  }
  
   */
}

