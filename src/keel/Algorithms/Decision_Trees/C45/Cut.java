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

import java.util.*;


/**
 * Class to implement the calculus of the cut point
 *  
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/
public class Cut {
    /**	Classification of class values. */
    protected Classification classification;

    /** Number of subsets. */
    protected int numSubsets;

    /** Number of branches. */
    private int nBranches;

    /** Attribute to cut on. */
    private int attributeIndex;

    /** Minimum number of itemsets per leaf. */
    private int minItemsets;

    /** Cut point. */
    private double cutPoint;

    /** Information gain of cut. */
    private double infoGain;

    /** Gain ratio of cut. */
    private double gainRatio;

    /** The sum of the weights of the itemsets. */
    private double sumOfWeights;

    /** Number of cut points. */
    private int nCuts;

    /** Function to initialize the cut model.
     *
     * @param index		The attribute index.
     * @param nObj		Minimum number of itemsets.
     * @param weights	The weight of all the itemsets.
     */
    public Cut(int index, int nObj, double weights) {
        // Get index of attribute to cut on.
        attributeIndex = index;

        // Set minimum number of objects.
        minItemsets = nObj;

        // Set the sum of the weights
        sumOfWeights = weights;
    }

    /** Function to use when no cut is necessary.
     *
     * @param dist		Distribution of values per class.
     */
    public Cut(Classification dist) {
        classification = new Classification(dist);
        numSubsets = 1;
    }

    /** Function to create the cut point.
     *
     * @param trainItemsets		The dataset to classify.
     *
     * @throws Exception		If the classification cannot be made.
     */
    public void classify(Dataset trainItemsets) throws Exception {
        if (numSubsets == 1) {
            classification = new Classification(trainItemsets);
        } else {
            // Initialize the remaining itemset variables.
            numSubsets = 0;
            cutPoint = Double.MAX_VALUE;
            infoGain = 0;
            gainRatio = 0;

            // 	Different treatment for enumerated and numeric
            // attributes.
            if (trainItemsets.getAttribute(attributeIndex).isDiscret()) {
                if (nBranches != 2) {
                    nBranches = trainItemsets.getAttribute(attributeIndex).
                                numValues();
                    nCuts = nBranches;
                } else {
                    nCuts = 0;
                }

                cutDiscret(trainItemsets);
            } else {
                nCuts = 0;
                trainItemsets.sort(attributeIndex);
                cutContinuous(trainItemsets);
            }
        }
    }

    /** Function to compute the probability for itemset.
     *
     * @param classIndex		The index of the class.
     * @param itemset			The itemset.
     * @param subset			The index of the subset.
     *
     * @return					The probability computed.
     */
    public final double classProbability(int classIndex, Itemset itemset,
                                         int subset) {
        if (numSubsets == 1) {
            if (subset > -1) {
                return classification.probability(classIndex, subset);
            } else {
                double[] weights = weights(itemset);

                if (weights == null) {
                    return classification.probability(classIndex);
                } else {
                    double prob = 0;

                    for (int i = 0; i < weights.length; i++) {
                        prob += weights[i] *
                                classification.probability(classIndex, i);
                    }

                    return prob;
                }
            }
        } else {
            if (subset <= -1) {
                double[] weights = weights(itemset);

                if (weights == null) {
                    return classification.probability(classIndex);
                } else {
                    double prob = 0;

                    for (int i = 0; i < weights.length; i++) {
                        prob += weights[i] *
                                classification.probability(classIndex, i);
                    }

                    return prob;
                }
            } else {
                if (classification.perValue(subset) > 0) {
                    return classification.probability(classIndex, subset);
                } else {
                    if (classification.maxClass() == classIndex) {
                        return 1;
                    } else {
                        return 0;
                    }
                }
            }
        }
    }

    /** Function to create the cut on continuous attributes.
     *
     * @param trainItemsets			The dataset used to compute the cut.
     */
    private void cutContinuous(Dataset trainItemsets) {
        int firstMiss, next = 1, last = 0, cutIndex = -1, i;
        double currentInfoGain, defaultEnt, minCut;
        Itemset itemset;

        // Current attribute is a numeric attribute.
        classification = new Classification(2, trainItemsets.numClasses());

        // Only Dataset with known values are relevant.
        Enumeration enum2 = trainItemsets.enumerateItemsets();
        i = 0;

        while (enum2.hasMoreElements()) {
            itemset = (Itemset) enum2.nextElement();

            if (itemset.isMissing(attributeIndex)) {
                break;
            }

            classification.add(1, itemset);
            i++;
        }

        firstMiss = i;

        // Compute minimum number of Dataset required in each subset.
        minCut = 0.1 * (classification.getTotal()) /
                 ((double) trainItemsets.numClasses());

        if (minCut <= minItemsets) {
            minCut = minItemsets;
        } else if (minCut > 25) {
            minCut = 25;
        }

        // Enough Dataset with known values?
        if ((double) firstMiss < 2 * minCut) {
            return;
        }

        // Compute values of criteria for all possible cut indices.
        defaultEnt = oldEntropy(classification);

        while (next < firstMiss) {
            if (trainItemsets.itemset(next - 1).getValue(attributeIndex)
                + 1e-5 < trainItemsets.itemset(next).getValue(attributeIndex)) {
                // Move class values for all Dataset up to next
                // possible cut point.
                classification.shiftRange(1, 0, trainItemsets, last, next);

                // Check if enough Dataset in each subset and compute
                // values for criteria.
                if (classification.perValue(0) >= minCut &&
                    classification.perValue(1) >= minCut) {
                    currentInfoGain = infoGainCutCrit(
                            classification, sumOfWeights, defaultEnt);

                    if (currentInfoGain > infoGain) {
                        infoGain = currentInfoGain;
                        cutIndex = next - 1;
                    }

                    nCuts++;
                }

                last = next;
            }

            next++;
        }

        // Was there any useful cut?
        if (nCuts == 0) {
            return;
        }

        // Compute modified information gain for best cut.
        infoGain = infoGain - ((Math.log(nCuts) / Math.log(2)) / sumOfWeights);

        if (infoGain <= 0) {
            return;
        }

        // Set itemset variables' values to values for best cut.
        numSubsets = 2;
        cutPoint = (trainItemsets.itemset(cutIndex + 1).getValue(attributeIndex) +
                    trainItemsets.itemset(cutIndex).getValue(attributeIndex)) /
                   2;

        // Restore classification for best cut.
        classification = new Classification(2, trainItemsets.numClasses());
        classification.addRange(0, trainItemsets, 0, cutIndex + 1);
        classification.addRange(1, trainItemsets, cutIndex + 1, firstMiss);

        // Compute modified gain ratio for best cut.
        gainRatio = gainRatioCutCrit(classification, sumOfWeights, infoGain);
    }

    /** Function to create the cut on discret attributes.
     *
     * @param trainItemsets			The dataset used to compute the cut.
     */
    private void cutDiscret(Dataset trainItemsets) {
        Itemset itemset;

        classification = new Classification(nBranches, trainItemsets.numClasses());

        // Only Dataset with known values are relevant.
        Enumeration enum2 = trainItemsets.enumerateItemsets();

        while (enum2.hasMoreElements()) {
            itemset = (Itemset) enum2.nextElement();

            if (!itemset.isMissing(attributeIndex)) {
                classification.add((int) itemset.getValue(attributeIndex),
                                   itemset);
            }
        }

        // Check if minimum number of Dataset in at least two	subsets.
        if (classification.check(minItemsets)) {
            numSubsets = nBranches;
            infoGain = infoGainCutCrit(classification, sumOfWeights,
                                       oldEntropy(classification));
            gainRatio = gainRatioCutCrit(classification, sumOfWeights, infoGain);
        }
    }

    /** Function to set the cut point.
     *
     * @param allItemsets		The dataset used for the cut.
     */
    public final void setCutPoint(Dataset allItemsets) {
        double newCutPoint = -Double.MAX_VALUE;
        double tempValue;
        Itemset itemset;

        if ((allItemsets.getAttribute(attributeIndex).isContinuous()) &&
            (numSubsets > 1)) {
            Enumeration enum2 = allItemsets.enumerateItemsets();

            while (enum2.hasMoreElements()) {
                itemset = (Itemset) enum2.nextElement();

                if (!itemset.isMissing(attributeIndex)) {
                    tempValue = itemset.getValue(attributeIndex);

                    if (tempValue > newCutPoint && tempValue <= cutPoint) {
                        newCutPoint = tempValue;
                    }
                }
            }

            cutPoint = newCutPoint;
        }
    }

    /** Function to cut the dataset in subsets.
     *
     * @param data			The dataset to cut.
     *
     * @return				All the datasets created.
     *
     * @throws Exception	If the dataset cannot be cut.
     */
    public final Dataset[] cutDataset(Dataset data) throws Exception {
        Dataset[] itemsets = new Dataset[numSubsets];
        double[] weights;
        double newWeight;
        Itemset itemset;
        int subset, i, j;

        for (j = 0; j < numSubsets; j++) {
            itemsets[j] = new Dataset((Dataset) data, data.numItemsets());
        }

        for (i = 0; i < data.numItemsets(); i++) {
            itemset = ((Dataset) data).itemset(i);
            weights = weights(itemset);
            subset = whichSubset(itemset);

            if (subset > -1) {
                itemsets[subset].addItemset(itemset);
            } else {
                for (j = 0; j < numSubsets; j++) {
                    if (weights[j] > 0) {
                        newWeight = weights[j] * itemset.getWeight();
                        itemsets[j].addItemset(itemset);
                        itemsets[j].lastItemset().setWeight(newWeight);
                    }
                }
            }
        }

        for (j = 0; j < numSubsets; j++) {
            ((Vector) itemsets[j].itemsets).trimToSize();
        }

        return itemsets;
    }

    /** Function to reset the classification of the model.
     *
     * @param data			The new dataset used.
     *
     * @throws Exception	If the classification cannot be reset.
     */
    public void resetClassification(Dataset data) throws Exception {
        if (numSubsets == 1) {
            classification = new Classification(data, this);
        } else {
            Dataset insts = new Dataset(data, data.numItemsets());

            for (int i = 0; i < data.numItemsets(); i++) {
                if (whichSubset(data.itemset(i)) > -1) {
                    insts.addItemset(data.itemset(i));
                }
            }

            Classification newD = new Classification(insts, this);
            newD.addWithUnknownValue(data, attributeIndex);
            classification = newD;
        }
    }

    /** Returns weights if itemset is assigned to more than one subset, null otherwise.
     *
     * @param itemset		The itemset.
     * @return weights if itemset is assigned to more than one subset, null otherwise
     */
    public final double[] weights(Itemset itemset) {
        if (numSubsets == 1) {
            return null;
        } else {
            double[] weights;
            int i;

            if (itemset.isMissing(attributeIndex)) {
                weights = new double[numSubsets];

                for (i = 0; i < numSubsets; i++) {
                    weights[i] = classification.perValue(i) /
                                 classification.getTotal();
                }

                return weights;
            } else {
                return null;
            }
        }
    }

    /** Returns index of subset itemset is assigned to.
     *
     * @param itemset		The itemset.
     * @return index of subset itemset is assigned to.
     */
    public final int whichSubset(Itemset itemset) {
        if (numSubsets == 1) {
            return 0;
        } else {
            if (itemset.isMissing(attributeIndex)) {
                return -1;
            } else {
                if (itemset.getAttribute(attributeIndex).isDiscret()) {
                    return (int) itemset.getValue(attributeIndex);
                } else if (itemset.getValue(attributeIndex) <= cutPoint) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }
    }

    /** Function to check if generated model is valid.
     *
     * @return		True if the model is valid. False otherwise.
     */
    public final boolean checkModel() {
        if (numSubsets > 0) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns the classification created by the model.
     *
     * @return the classification created by the model
     */
    public final Classification classification() {
        return classification;
    }

    /** Returns the number of created subsets for the cut.
     *
     * @return the number of created subsets for the cut
     */
    public final int numSubsets() {
        return numSubsets;
    }

    /** Function to compute the gain ratio.
     *
     * @param values				The classification used to compute the gain ratio.
     * @param totalnoInst		Number of itemsets.
     * @param numerator			The information gain.
     *
     * @return					The gain ratio for the classification.
     */
    public final double gainRatioCutCrit(Classification values,
                                         double totalnoInst, double numerator) {
        double denumerator, noUnknown, unknownRate;
        int i;

        // Compute cut info.
        denumerator = cutEntropy(values, totalnoInst);

        // Test if cut is trivial.
        if (denumerator == 0) {
            return 0;
        }

        denumerator = denumerator / totalnoInst;

        return numerator / denumerator;
    }

    /** Function to compute the information gain.
     *
     * @param values				The classification used to compute the information gain.
     * @param totalNoInst			Number of itemsets.
     * @param oldEnt				The value for the entropy before cutting.
     *
     * @return						The information gain.
     */
    public final double infoGainCutCrit(Classification values,
                                        double totalNoInst, double oldEnt) {
        double numerator, noUnknown, unknownRate;
        int i;

        noUnknown = totalNoInst - values.getTotal();
        unknownRate = noUnknown / totalNoInst;
        numerator = (oldEnt - newEntropy(values));
        numerator = (1 - unknownRate) * numerator;

        // Cuts with no gain are useless.
        if (numerator == 0) {
            return 0;
        }

        return numerator / values.getTotal();
    }

    /** Function to compute the cut entropy.
     *
     * @param values				The classification used to compute the entropy.
     * @param totalnoInst			Number of itemsets.
     *
     * @return						The entropy of the cut.
     */
    private final double cutEntropy(Classification values, double totalnoInst) {
        double returnValue = 0, noUnknown;
        int i;

        noUnknown = totalnoInst - values.getTotal();

        if (values.getTotal() > 0) {
            for (i = 0; i < values.numValues(); i++) {
                returnValue = returnValue - logFunc(values.perValue(i));
            }
            returnValue = returnValue - logFunc(noUnknown);
            returnValue = returnValue + logFunc(totalnoInst);
        }

        return returnValue;
    }

    /** Function to compute entropy of classification before cutting.
     *
     * @param values		The classification used to compute the entropy before cutting.
     *
     * @return				The entropy for the classification before cutting.
     */
    public final double oldEntropy(Classification values) {
        double returnValue = 0;
        int j;

        for (j = 0; j < values.numClasses(); j++) {
            returnValue = returnValue + logFunc(values.perClass(j));
        }

        return logFunc(values.getTotal()) - returnValue;
    }

    /** Function to compute entropy of classification after cutting.
     *
     * @param values		The classification used to compute the entropy after cutting.
     *
     * @return				The entropy for the classification after cutting.
     */
    public final double newEntropy(Classification values) {
        double returnValue = 0;
        int i, j;

        for (i = 0; i < values.numValues(); i++) {
            for (j = 0; j < values.numClasses(); j++) {
                returnValue = returnValue +
                              logFunc(values.perClassPerValue(i, j));
            }

            returnValue = returnValue - logFunc(values.perValue(i));
        }

        return -returnValue;
    }

    /** Returns the log2
     *
     * @param num	The number to compute the log2.
     * @return the log2 of the number given.
     */
    protected final double logFunc(double num) {
        // Constant hard coded for efficiency reasons
        if (num < 1e-6) {
            return 0;
        } else {
            return num * Math.log(num) / Math.log(2);
        }
    }

    /** Returns information gain for the generated cut.
     *
     * @return information gain for the generated cut.
     */
    public final double getInfoGain() {
        return infoGain;
    }

    /** Returns the gain ratio for the cut.
     *
     * @return the gain ratio for the cut.
     */
    public final double getGainRatio() {
        return gainRatio;
    }

    /** Function to print left side of condition.
     *
     * @param data		The dataset.
     *
     * @return			The name of the attribute used in the cut.
     */
    public final String leftSide(Dataset data) {
        if (numSubsets == 1) {
            return "";
        } else {
            return data.getAttribute(attributeIndex).name();
        }
    }

    /** Function to print left side of condition.
    *
    * @param data		The dataset.
    *
    * @return			The name of the attribute used in the cut.
    */
   public final String leftSideOVO(Dataset data) {
       if (numSubsets == 1) {
           return "";
       } else {
           return "Att"+attributeIndex; //data.getAttribute(attributeIndex).name();
       }
   }    
    
    /** Function to print the condition satisfied by itemsets in a subset.
     *
     * @param index		The index of the value.
     * @param data		The dataset.
     *
     * @return			The value for the attribute of the cut.
     */
    public final String rightSide(int index, Dataset data) {
        if (numSubsets == 1) {
            return "";
        } else {
            StringBuffer text;

            text = new StringBuffer();

            if (data.getAttribute(attributeIndex).isDiscret()) {
                text.append(" = " +
                            data.getAttribute(attributeIndex).value(index));
            } else if (index == 0) {
                text.append(" <= " + doubleToString(cutPoint, 6));
            } else {
                text.append(" > " + doubleToString(cutPoint, 6));
            }

            return text.toString();
        }
    }

    /** Function to print label for subset index of itemsets.
     *
     * @param index		The index of the subset.
     * @param data		The dataset.
     *
     * @return			The label created.
     */
    public final String label(int index, Dataset data) {
        StringBuffer text;

        text = new StringBuffer();
        text.append(((Dataset) data).getClassAttribute().
                    value(classification.maxClass(index)));

        return text.toString();
    }

    /** Returns the index of the attribute to cut on.
     *
     * @return the index of the attribute to cut on.
     */
    public final int attributeIndex() {
        return attributeIndex;
    }

    /** Function to round a double and converts it into String.
     *
     * @param value					The value to print.
     * @param afterDecimalPoint		Number of decimals positions.
     *
     * @return						The value with the given number of decimals.
     */
    public static String doubleToString(double value, int afterDecimalPoint) {
        StringBuffer stringBuffer;
        double temp;
        int i, dotPosition;
        long precisionValue;

        temp = value * Math.pow(10.0, afterDecimalPoint);

        if (Math.abs(temp) < Long.MAX_VALUE) {
            precisionValue = (temp > 0) ? (long) (temp + 0.5) :
                             -(long) (Math.abs(temp) + 0.5);

            if (precisionValue == 0) {
                stringBuffer = new StringBuffer(String.valueOf(0));
            } else {
                stringBuffer = new StringBuffer(String.valueOf(precisionValue));
            }

            if (afterDecimalPoint == 0) {
                return stringBuffer.toString();
            }

            dotPosition = stringBuffer.length() - afterDecimalPoint;

            while (((precisionValue < 0) && (dotPosition < 1)) ||
                   (dotPosition < 0)) {
                if (precisionValue < 0) {
                    stringBuffer.insert(1, 0);
                } else {
                    stringBuffer.insert(0, 0);
                }

                dotPosition++;
            }

            stringBuffer.insert(dotPosition, '.');

            if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
                stringBuffer.insert(1, 0);
            } else if (stringBuffer.charAt(0) == '.') {
                stringBuffer.insert(0, 0);
            }

            int currentPos = stringBuffer.length() - 1;

            if (stringBuffer.charAt(currentPos) == '.') {
                stringBuffer.setCharAt(currentPos, ' ');
            }

            return stringBuffer.toString().trim();
        }

        return new String("" + value);
    }

    /** Function to round a double and converts it into String.
     *
     * @param value					The value to print.
     * @param width					The width that must have the string generated.
     * @param afterDecimalPoint		Number of decimals positions.
     *
     * @return						The value with the given number of decimals.
     */
    public static String doubleToString(double value, int width,
                                        int afterDecimalPoint) {
        String tempString = doubleToString(value, afterDecimalPoint);
        char[] result;
        int dotPosition;

        // Protects sci notation
        if ((afterDecimalPoint >= width) || (tempString.indexOf('E') != -1)) {
            return tempString;
        }

        // Initialize result
        result = new char[width];

        for (int i = 0; i < result.length; i++) {
            result[i] = ' ';
        }

        if (afterDecimalPoint > 0) {
            // Get position of decimal point and insert decimal point
            dotPosition = tempString.indexOf('.');

            if (dotPosition == -1) {
                dotPosition = tempString.length();
            } else {
                result[width - afterDecimalPoint - 1] = '.';
            }

        } else {
            dotPosition = tempString.length();
        }

        int offset = width - afterDecimalPoint - dotPosition;

        if (afterDecimalPoint > 0) {
            offset--;
        }

        // Not enough room to decimal align within the supplied width
        if (offset < 0) {
            return tempString;
        }

        // Copy characters before decimal point
        for (int i = 0; i < dotPosition; i++) {
            result[offset + i] = tempString.charAt(i);
        }

        // Copy characters after decimal point
        for (int i = dotPosition + 1; i < tempString.length(); i++) {
            result[offset + i] = tempString.charAt(i);
        }

        return new String(result);
    }
}

