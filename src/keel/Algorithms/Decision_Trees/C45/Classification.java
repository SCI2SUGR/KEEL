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
 * Class to handle a classification of class values.
 * 
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 10/10/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/
public class Classification {
    /** Weight of itemsets per class per value. */
    private double perClassPerValue[][];

    /** Weight of itemsets per value. */
    private double perValue[];

    /** Weight of itemsets per class. */
    private double perClass[];

    /** Total weight of itemsets. */
    private double total;

    /** Function to create and initialize a new classification.
     *
     * @param numValues		Number of values used to make the classification.
     * @param numClasses	Number of classes used to make the classification.
     */
    public Classification(int numValues, int numClasses) {
        int i;

        perClassPerValue = new double[numValues][0];
        perValue = new double[numValues];
        perClass = new double[numClasses];

        for (i = 0; i < numValues; i++) {
            perClassPerValue[i] = new double[numClasses];
        }

        total = 0;
    }

    /** Function to create a new classification with only one value.
     *
     * @param source		The dataset.
     *
     * @throws Exception 	If cannot read the dataset.
     */
    public Classification(Dataset source) throws Exception {
        perClassPerValue = new double[1][0];
        perValue = new double[1];
        total = 0;
        perClass = new double[source.numClasses()];
        perClassPerValue[0] = new double[source.numClasses()];
        Enumeration enum2 = source.enumerateItemsets();

        while (enum2.hasMoreElements()) {
            add(0, (Itemset) enum2.nextElement());
        }
    }

    /** Function to create a new classification with the given dataset.
     *
     * @param source		The dataset.
     * @param model			The model selected to make the classification.
     *
     * @throws Exception	If cannot build the classification.
     */
    public Classification(Dataset source, Cut model) throws Exception {
        int index;
        Itemset itemset;
        double[] weights;
        perClassPerValue = new double[model.numSubsets()][0];
        perValue = new double[model.numSubsets()];
        total = 0;
        perClass = new double[source.numClasses()];

        for (int i = 0; i < model.numSubsets(); i++) {
            perClassPerValue[i] = new double[source.numClasses()];
        }

        Enumeration enum2 = source.enumerateItemsets();

        while (enum2.hasMoreElements()) {
            itemset = (Itemset) enum2.nextElement();
            index = model.whichSubset(itemset);

            if (index != -1) {
                add(index, itemset);
            } else {
                weights = model.weights(itemset);
                addWeights(itemset, weights);
            }
        }
    }

    /** Function to create a new classification with only one value by merging all ç
     * values of given classification.
     *
     * @param toMerge		The original classification to merge.
     */

    public Classification(Classification toMerge) {
        total = toMerge.total;
        perClass = new double[toMerge.numClasses()];
        System.arraycopy(toMerge.perClass, 0, perClass, 0, toMerge.numClasses());
        perClassPerValue = new double[1][0];
        perClassPerValue[0] = new double[toMerge.numClasses()];
        System.arraycopy(toMerge.perClass, 0, perClassPerValue[0], 0,
                         toMerge.numClasses());
        perValue = new double[1];
        perValue[0] = total;
    }

    /** Function to add the given itemset to given the value.
     *
     * @param valueIndex		The index of the value.
     * @param itemset			The itemset to add.
     */
    public final void add(int valueIndex, Itemset itemset) {
        int classIndex;
        double weight;

        classIndex = (int) itemset.getClassValue();
        weight = itemset.getWeight();
        perClassPerValue[valueIndex][classIndex] = perClassPerValue[valueIndex][
                classIndex] + weight;

        perValue[valueIndex] = perValue[valueIndex] + weight;
        perClass[classIndex] = perClass[classIndex] + weight;
        total = total + weight;
    }

    /** Function to add all itemsets with unknown values for given attribute.
     *
     * @param source			The dataset that contains all the itemsets.
     * @param attIndex			The index of the attribute with possible unknown values.
     */
    public final void addWithUnknownValue(Dataset source, int attIndex) {
        double[] probs;
        double weight, newWeight;
        int classIndex;
        Itemset itemset;
        int j;
        probs = new double[perValue.length];

        for (j = 0; j < perValue.length; j++) {
            //if ( Comparators.isEqual( total, 0 ) )
            if (total == 0) {
                probs[j] = 1.0 / probs.length;
            } else {
                probs[j] = perValue[j] / total;
            }
        }

        Enumeration enum2 = source.enumerateItemsets();

        while (enum2.hasMoreElements()) {
            itemset = (Itemset) enum2.nextElement();

            if (itemset.isMissing(attIndex)) {
                classIndex = (int) itemset.getClassValue();
                weight = itemset.getWeight();
                perClass[classIndex] = perClass[classIndex] + weight;
                total = total + weight;

                for (j = 0; j < perValue.length; j++) {
                    newWeight = probs[j] * weight;
                    perClassPerValue[j][classIndex] = perClassPerValue[j][
                            classIndex] + newWeight;
                    perValue[j] = perValue[j] + newWeight;
                }
            }
        }
    }

    /** Function to add all itemsets in given range to given value.
     *
     * @param valueIndex		The index of the value.
     * @param source			The itemset to add.
     * @param start				The index of the first itemset to add.
     * @param end				The index of the first itemset that will not be added.
     */
    public final void addRange(int valueIndex, Dataset source, int start,
                               int end) {
        double sumOfWeights = 0;
        int classIndex;
        Itemset itemset;
        int i;

        for (i = start; i < end; i++) {
            itemset = (Itemset) source.itemset(i);
            classIndex = (int) itemset.getClassValue();
            sumOfWeights = sumOfWeights + itemset.getWeight();
            perClassPerValue[valueIndex][classIndex] += itemset.getWeight();
            perClass[classIndex] += itemset.getWeight();
        }

        perValue[valueIndex] += sumOfWeights;
        total += sumOfWeights;
    }

    /** Funtion to add the given itemset to all values weighting it according to given weights.
     *
     * @param itemset		The itemset to add.
     * @param weights		The weights of the itemset for every value.
     *
     */
    public final void addWeights(Itemset itemset, double[] weights) {
        int classIndex;
        int i;

        classIndex = (int) itemset.getClassValue();

        for (i = 0; i < perValue.length; i++) {
            double weight = itemset.getWeight() * weights[i];
            perClassPerValue[i][classIndex] = perClassPerValue[i][classIndex] +
                                              weight;
            perValue[i] = perValue[i] + weight;
            perClass[classIndex] = perClass[classIndex] + weight;
            total = total + weight;
        }
    }

    /** Function to check if at least two values contain a minimum number of itemsets.
     *
     * @param minItemsets		The minimum number of itemsets.
     *
     * @return					True if the condition is satisfied. False otherwise.
     */
    public final boolean check(double minItemsets) {
        int counter = 0;
        int i;

        for (i = 0; i < perValue.length; i++) {
            //
            if (perValue[i] >= minItemsets) {
                counter++;
            }
        }

        if (counter > 1) {
            return true;
        } else {
            return false;
        }
    }

    /** Returns index of value containing maximum number of itemsets.
     *
     * @return Index of value containing maximum number.
     */
    public final int maxValue() {
        double max;
        int maxIndex;
        int i;

        max = 0;
        maxIndex = -1;
        for (i = 0; i < perValue.length; i++) {
            if (perValue[i] >= max) {
                max = perValue[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    /** Returns class with highest frequency over all values.
     *
     * @return The class with highest frequency 
     */
    public final int maxClass() {
        double maxCount = 0;
        int maxIndex = 0;
        int i;

        for (i = 0; i < perClass.length; i++) {
            if (perClass[i] > maxCount) {
                maxCount = perClass[i];
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    /** Returns class with highest frequency for given value.
     *
     * @param index			The index of the value.
     * @return The class with highest frequency for given value.
     */
    public final int maxClass(int index) {
        double maxCount = 0;
        int maxIndex = 0;
        int i;

        if (perValue[index] > 0) {
            for (i = 0; i < perClass.length; i++) {
                if (perClassPerValue[index][i] > maxCount) {
                    maxCount = perClassPerValue[index][i];
                    maxIndex = i;
                }
            }

            return maxIndex;
        } else {
            return maxClass();
        }
    }

    /** Returns number of values.
     *
     * @return Number of values
     */
    public final int numValues() {
        return perValue.length;
    }

    /** Returns number of classes.
     *
     * @return Number of classes.
     */
    public final int numClasses() {
        return perClass.length;
    }

    /** Returns the weight of all itemsets of the class with highest frequency.
     *
     * @return  the weight of all itemsets of the class with highest frequency.
     */
    public final double numCorrect() {
        return perClass[maxClass()];
    }

    /** Returns incorrectly classifed
     *
     * @return incorrectly classifed.
     */
    public final double numIncorrect() {
        return total - numCorrect();
    }

    /** Returns the number of incorrectly classified itemsets for the given value.
     *
     * @param index		The index of the value.
     * @return The number of incorrectly classified itemsets for the given value.
     */
    public final double numIncorrect(int index) {
        return perValue[index] - numCorrect(index);
    }

    /** Returns the number of correctly classified itemsets for the given value.
     *
     * @param index		The index of the value.
     * @return Number of correctly classified itemsets for the given value.
     */
    public final double numCorrect(int index) {
        return perClassPerValue[index][maxClass(index)];
    }

    /** Returns total weight of itemsets.
     *
     * @return total weight of itemsets.
     */
    public final double getTotal() {
        return total;
    }

    /** Returns number of itemsets of given class in given value.
     *
     * @param valueIndex		The index of the value.
     * @param classIndex		The index of the class.
     * @return number of itemsets of given class in given value.
     */
    public final double perClassPerValue(int valueIndex, int classIndex) {
        return perClassPerValue[valueIndex][classIndex];
    }

    /** Returns number of (possibly fractional) itemsets in given value.
     *
     * @param valueIndex		The index of the value.
     * @return number of (possibly fractional) itemsets in given value.
     */
    public final double perValue(int valueIndex) {
        return perValue[valueIndex];
    }

    /** Returns number of itemsets of given class.
     *
     * @param classIndex		The index of the class.
     * @return number of itemsets of given class.
     */
    public final double perClass(int classIndex) {
        return perClass[classIndex];
    }

    /** Returns relative frequency of class over all values.
     *
     * @param classIndex		The index of the class.
     * @return relative frequency of class over all values.
     */
    public final double probability(int classIndex) {
        if (total != 0) {
            return perClass[classIndex] / total;
        } else {
            return 0;
        }
    }

    /** Returns relative frequency of class for given value.
     *
     * @param classIndex		The index of the class.
     * @param attIndex			The index of the attribute.
     * @return relative frequency of class for given value.
     */
    public final double probability(int classIndex, int attIndex) {
        if (perValue[attIndex] > 0) {
            return perClassPerValue[attIndex][classIndex] / perValue[attIndex];
        } else {
            return probability(classIndex);
        }
    }

    /** Function to shift all itemsets in given range from one value to another.
     *
     * @param from				The minimum value.
     * @param to				The maximum value.
     * @param source			The dataset.
     * @param start				The index of the first itemset to add.
     * @param end				The index of the first itemset that will not be added.
     */
    public final void shiftRange(int from, int to, Dataset source, int start,
                                 int end) {
        int classIndex;
        double weight;
        Itemset itemset;
        int i;

        for (i = start; i < end; i++) {
            itemset = (Itemset) source.itemset(i);
            classIndex = (int) itemset.getClassValue();
            weight = itemset.getWeight();
            perClassPerValue[from][classIndex] -= weight;
            perClassPerValue[to][classIndex] += weight;
            perValue[from] -= weight;
            perValue[to] += weight;
        }
    }
}

