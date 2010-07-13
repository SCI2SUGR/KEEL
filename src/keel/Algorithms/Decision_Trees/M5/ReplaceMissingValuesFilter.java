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

import java.io.*;
import java.util.*;

/**
 * Replaces all missing values for nominal and numeric attributes in a
 * dataset with the modes and means from the training data.
 */
public class ReplaceMissingValuesFilter {

    /** The modes and means */
    private double[] m_ModesAndMeans = null;
    /** Debugging mode */
    private boolean m_Debug = false;

    /** The output format for instances */
    private M5Instances m_OutputFormat = null;

    /** The output instance queue */
    private Queue m_OutputQueue = null;

    /** Indices of string attributes in the output format */
    private int[] m_OutputStringAtts = null;

    /** Indices of string attributes in the input format */
    private int[] m_InputStringAtts = null;

    /** The input format for instances */
    private M5Instances m_InputFormat = null;

    /** Record whether the filter is at the start of a batch */
    protected boolean m_NewBatch = true;

    /**
     * Sets the format of the input instances.
     *
     * @param instanceInfo an Instances object containing the input
     * instance structure (any instances contained in the object are
     * ignored - only the structure is required).
     * @return true if the outputFormat may be collected immediately
     * @exception Exception if the input format can't be set
     * successfully
     */
    public boolean setInputFormat(M5Instances instanceInfo) throws Exception {

        superSetInputFormat(instanceInfo);
        setOutputFormat(instanceInfo);
        m_ModesAndMeans = null;
        return true;
    }

    protected int[] getStringIndices(M5Instances insts) {

        // Scan through getting the indices of String attributes
        int[] index = new int[insts.numAttributes()];
        int indexSize = 0;
        for (int i = 0; i < insts.numAttributes(); i++) {
            if (insts.attribute(i).type() == M5Attribute.STRING) {
                index[indexSize++] = i;
            }
        }
        int[] result = new int[indexSize];
        System.arraycopy(index, 0, result, 0, indexSize);
        return result;
    }

    protected void setOutputFormat(M5Instances outputFormat) {

        if (outputFormat != null) {
            m_OutputFormat = outputFormat.stringFreeStructure();
            m_OutputStringAtts = getStringIndices(m_OutputFormat);

            // Rename the attribute
            String relationName = outputFormat.relationName()
                                  + "-" + this.getClass().getName();

            m_OutputFormat.setRelationName(relationName);
        } else {
            m_OutputFormat = null;
        }
        m_OutputQueue = new Queue();
    }

    public boolean superSetInputFormat(M5Instances instanceInfo) throws
            Exception {

        m_InputFormat = instanceInfo.stringFreeStructure();
        m_InputStringAtts = getStringIndices(instanceInfo);
        m_OutputFormat = null;
        m_OutputQueue = new Queue();
        m_NewBatch = true;
        return false;
    }

    protected M5Instances getInputFormat() {

        return m_InputFormat;
    }

    /**
     * Input an instance for filtering. Filter requires all
     * training instances be read before producing output.
     *
     * @param instance the input instance
     * @return true if the filtered instance may now be
     * collected with output().
     * @throws Exception
     * @exception IllegalStateException if no input format has been set.
     */
    public boolean input(M5Instance instance) throws Exception {

        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }
        if (m_NewBatch) {
            resetQueue();
            m_NewBatch = false;
        }
        if (m_ModesAndMeans == null) {
            bufferInput(instance);
            return false;
        } else {
            convertInstance(instance);
            return true;
        }
    }

    protected void resetQueue() {

        m_OutputQueue = new Queue();
    }

    protected void bufferInput(M5Instance instance) {

        if (instance != null) {
            copyStringValues(instance, m_InputFormat, m_InputStringAtts);
            instance.setDataset(m_InputFormat);
            m_InputFormat.add(instance);
        }
    }

    private void copyStringValues(M5Instance inst, M5Instances destDataset,
                                  int[] strAtts) {

        if (strAtts.length == 0) {
            return;
        }
        if (inst.dataset() == null) {
            throw new IllegalArgumentException(
                    "Instance has no dataset assigned!!");
        } else if (inst.dataset().numAttributes() != destDataset.numAttributes()) {
            throw new IllegalArgumentException(
                    "Src and Dest differ in # of attributes!!");
        }
        copyStringValues(inst, true, inst.dataset(), strAtts,
                         destDataset, strAtts);
    }

    protected void copyStringValues(M5Instance instance, boolean instSrcCompat,
                                    M5Instances srcDataset,
                                    M5Instances destDataset) {

        copyStringValues(instance, instSrcCompat, srcDataset, m_InputStringAtts,
                         destDataset, m_OutputStringAtts);
    }

    protected void copyStringValues(M5Instance instance, boolean instSrcCompat,
                                    M5Instances srcDataset, int[] srcStrAtts,
                                    M5Instances destDataset, int[] destStrAtts) {
        if (srcDataset == destDataset) {
            return;
        }
        if (srcStrAtts.length != destStrAtts.length) {
            throw new IllegalArgumentException(
                    "Src and Dest string indices differ in length!!");
        }
        for (int i = 0; i < srcStrAtts.length; i++) {
            int instIndex = instSrcCompat ? srcStrAtts[i] : destStrAtts[i];
            M5Attribute src = srcDataset.attribute(srcStrAtts[i]);
            M5Attribute dest = destDataset.attribute(destStrAtts[i]);
            if (!instance.isMissing(instIndex)) {
//System.err.println(instance.value(srcIndex)
//                   + " " + src.numValues()
//                   + " " + dest.numValues());
                int valIndex = dest.addStringValue(src,
                        (int) instance.value(instIndex));
// setValue here shouldn't be too slow here unless your dataset has
// squillions of string attributes
                instance.setValue(instIndex, (double) valIndex);
            }
        }
    }

    /**
     * Signify that this batch of input to the filter is finished.
     * If the filter requires all instances prior to filtering,
     * output() may now be called to retrieve the filtered instances.
     *
     * @return true if there are instances pending output
     * @throws Exception
     * @exception IllegalStateException if no input structure has been defined
     */
    public boolean batchFinished() throws Exception {

        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }

        if (m_ModesAndMeans == null) {
            // Compute modes and means
            double sumOfWeights = getInputFormat().sumOfWeights();
            double[][] counts = new double[getInputFormat().numAttributes()][];
            for (int i = 0; i < getInputFormat().numAttributes(); i++) {
                if (getInputFormat().attribute(i).isNominal()) {
                    counts[i] = new double[getInputFormat().attribute(i).
                                numValues()];
                    counts[i][0] = sumOfWeights;
                }
            }
            double[] sums = new double[getInputFormat().numAttributes()];
            for (int i = 0; i < sums.length; i++) {
                sums[i] = sumOfWeights;
            }
            double[] results = new double[getInputFormat().numAttributes()];
            for (int j = 0; j < getInputFormat().numInstances(); j++) {
                M5Instance inst = getInputFormat().instance(j);
                for (int i = 0; i < inst.numValues(); i++) {
                    if (!inst.isMissingSparse(i)) {
                        double value = inst.valueSparse(i);
                        if (inst.attributeSparse(i).isNominal()) {
                            counts[inst.index(i)][(int) value] += inst.weight();
                            counts[inst.index(i)][0] -= inst.weight();
                        } else if (inst.attributeSparse(i).isNumeric()) {
                            results[inst.index(i)] += inst.weight() *
                                    inst.valueSparse(i);
                        }
                    } else {
                        if (inst.attributeSparse(i).isNominal()) {
                            counts[inst.index(i)][0] -= inst.weight();
                        } else if (inst.attributeSparse(i).isNumeric()) {
                            sums[inst.index(i)] -= inst.weight();
                        }
                    }
                }
            }
            m_ModesAndMeans = new double[getInputFormat().numAttributes()];
            for (int i = 0; i < getInputFormat().numAttributes(); i++) {
                if (getInputFormat().attribute(i).isNominal()) {
                    m_ModesAndMeans[i] = (double) M5StaticUtils.maxIndex(counts[
                            i]);
                } else if (getInputFormat().attribute(i).isNumeric()) {
                    if (M5StaticUtils.gr(sums[i], 0)) {
                        m_ModesAndMeans[i] = results[i] / sums[i];
                    }
                }
            }

            // Convert pending input instances
            for (int i = 0; i < getInputFormat().numInstances(); i++) {
                convertInstance(getInputFormat().instance(i));
            }
        }
        // Free memory
        flushInput();

        m_NewBatch = true;
        return (numPendingOutput() != 0);
    }


    protected void flushInput() {

        if (m_InputStringAtts.length > 0) {
            m_InputFormat = m_InputFormat.stringFreeStructure();
        } else {
            // This more efficient than new Instances(m_InputFormat, 0);
            m_InputFormat.delete();
        }
    }

    public int numPendingOutput() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output instance format defined");
        }
        return m_OutputQueue.size();
    }

    /**
     * Convert a single instance over. The converted instance is
     * added to the end of the output queue.
     *
     * @param instance the instance to convert
     * @throws Exception
     */
    private void convertInstance(M5Instance instance) throws Exception {

        M5Instance inst = null;
        if (instance instanceof M5SparseInstance) {
            double[] vals = new double[instance.numValues()];
            int[] indices = new int[instance.numValues()];
            int num = 0;
            for (int j = 0; j < instance.numValues(); j++) {
                if (instance.isMissingSparse(j) &&
                    (instance.attributeSparse(j).isNominal() ||
                     instance.attributeSparse(j).isNumeric())) {
                    if (m_ModesAndMeans[instance.index(j)] != 0.0) {
                        vals[num] = m_ModesAndMeans[instance.index(j)];
                        indices[num] = instance.index(j);
                        num++;
                    }
                } else {
                    vals[num] = instance.valueSparse(j);
                    indices[num] = instance.index(j);
                    num++;
                }
            }
            if (num == instance.numValues()) {
                inst = new M5SparseInstance(instance.weight(), vals, indices,
                                            instance.numAttributes());
            } else {
                double[] tempVals = new double[num];
                int[] tempInd = new int[num];
                System.arraycopy(vals, 0, tempVals, 0, num);
                System.arraycopy(indices, 0, tempInd, 0, num);
                inst = new M5SparseInstance(instance.weight(), tempVals,
                                            tempInd,
                                            instance.numAttributes());
            }
        } else {
            double[] vals = new double[getInputFormat().numAttributes()];
            for (int j = 0; j < instance.numAttributes(); j++) {
                if (instance.isMissing(j) &&
                    (getInputFormat().attribute(j).isNominal() ||
                     getInputFormat().attribute(j).isNumeric())) {
                    vals[j] = m_ModesAndMeans[j];
                } else {
                    vals[j] = instance.value(j);
                }
            }
            inst = new M5Instance(instance.weight(), vals);
        }
        inst.setDataset(instance.dataset());
        push(inst);
    }


    protected void push(M5Instance instance) {

        if (instance != null) {
            copyStringValues(instance, m_OutputFormat, m_OutputStringAtts);
            instance.setDataset(m_OutputFormat);
            m_OutputQueue.push(instance);
        }
    }

    public static M5Instances useFilter(M5Instances data,
                                        ReplaceMissingValuesFilter filter) throws
            Exception {
        /*
         System.err.println(filter.getClass().getName()
                     + " in:" + data.numInstances());
         */
        for (int i = 0; i < data.numInstances(); i++) {
            filter.input(data.instance(i));
        }
        filter.batchFinished();
        M5Instances newData = filter.getOutputFormat();
        M5Instance processed;
        while ((processed = filter.output()) != null) {
            newData.add(processed);
        }

        /*
         System.err.println(filter.getClass().getName()
                     + " out:" + newData.numInstances());
         */
        return newData;
    }


    public final M5Instances getOutputFormat() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output format defined.");
        }
        return new M5Instances(m_OutputFormat, 0);
    }

    public M5Instance output() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output instance format defined");
        }
        if (m_OutputQueue.empty()) {
            return null;
        }
        M5Instance result = (M5Instance) m_OutputQueue.pop();
        // Clear out references to old strings occasionally
        if (m_OutputQueue.empty() && m_NewBatch) {
            if (m_OutputStringAtts.length > 0) {
                m_OutputFormat = m_OutputFormat.stringFreeStructure();
            }
        }
        return result;
    }


}


