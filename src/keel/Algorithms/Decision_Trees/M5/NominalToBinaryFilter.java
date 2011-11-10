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
 * Converts all nominal attributes into binary numeric
 * attributes. An attribute with k values is transformed into
 * k-1 new binary attributes (in a similar manner to CART if a
 * numeric class is assigned). Currently requires that a class attribute
 * be set (but this should be changed).<p>
 *
 * Valid filter-specific options are: <p>
 *
 * -N <br>
 * If binary attributes are to be coded as nominal ones.<p>
 */
public class NominalToBinaryFilter {

    /** The sorted indices of the attribute values. */
    private int[][] m_Indices = null;

    /** Are the new attributes going to be nominal or numeric ones? */
    private boolean m_Numeric = true;

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
     * Sets the format of output instances. The derived class should use this
     * method once it has determined the outputformat. The
     * output queue is cleared.
     *
     * @param outputFormat the new output format
     */
    protected void setOutputFormat(M5Instances outputFormat) {

        if (outputFormat != null) {
            m_OutputFormat = outputFormat.stringFreeStructure();
            m_OutputStringAtts = getStringIndices(m_OutputFormat);

            // Rename the attribute
            String relationName = outputFormat.relationName()
                                  + "-" + this.getClass().getName();
            if (this instanceof NominalToBinaryFilter) {
                String[] options = ((NominalToBinaryFilter)this).getOptions();
                for (int i = 0; i < options.length; i++) {
                    relationName += options[i].trim();
                }
            }
            m_OutputFormat.setRelationName(relationName);
        } else {
            m_OutputFormat = null;
        }
        m_OutputQueue = new Queue();
    }

    /**
     * Gets the currently set inputformat instances. This dataset may contain
     * buffered instances.
     *
     * @return the input Instances.
     */
    protected M5Instances getInputFormat() {

        return m_InputFormat;
    }

    /**
     * Returns a reference to the current output format without
     * copying it.
     *
     * @return a reference to the current output format
     */
    protected M5Instances outputFormatPeek() {

        return m_OutputFormat;
    }

    /**
     * Adds an output instance to the queue. The derived class should use this
     * method for each output instance it makes available.
     *
     * @param instance the instance to be added to the queue
     */
    protected void push(M5Instance instance) {

        if (instance != null) {
            copyStringValues(instance, m_OutputFormat, m_OutputStringAtts);
            instance.setDataset(m_OutputFormat);
            m_OutputQueue.push(instance);
        }
    }

    /**
     * Clears the output queue.
     */
    protected void resetQueue() {

        m_OutputQueue = new Queue();
    }

    /**
     * Adds the supplied input instance to the inputformat dataset for
     * later processing.  Use this method rather than
     * getInputFormat().add(instance). Or else.
     *
     * @param instance the <code>Instance</code> to buffer.
     */
    protected void bufferInput(M5Instance instance) {

        if (instance != null) {
            copyStringValues(instance, m_InputFormat, m_InputStringAtts);
            instance.setDataset(m_InputFormat);
            m_InputFormat.add(instance);
        }
    }

    /**
     * Returns an array containing the indices of all string attributes in the
     * input format. This index is created during setInputFormat()
     *
     * @return an array containing the indices of string attributes in the
     * input dataset.
     */
    protected int[] getInputStringIndex() {

        return m_InputStringAtts;
    }

    /**
     * Returns an array containing the indices of all string attributes in the
     * output format. This index is created during setOutputFormat()
     *
     * @return an array containing the indices of string attributes in the
     * output dataset.
     */
    protected int[] getOutputStringIndex() {

        return m_OutputStringAtts;
    }

    /**
     * Copies string values contained in the instance copied to a new
     * dataset. The Instance must already be assigned to a dataset. This
     * dataset and the destination dataset must have the same structure.
     *
     * @param instance the Instance containing the string values to copy.
     * @param destDataset the destination set of Instances
     * @param strAtts an array containing the indices of any string attributes
     * in the dataset.
     */
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

    /**
     * Takes string values referenced by an Instance and copies them from a
     * source dataset to a destination dataset. The instance references are
     * updated to be valid for the destination dataset. The instance may have the
     * structure (i.e. number and attribute position) of either dataset (this
     * affects where references are obtained from). The source dataset must
     * have the same structure as the filter input format and the destination
     * must have the same structure as the filter output format.
     *
     * @param instance the instance containing references to strings in the source
     * dataset that will have references updated to be valid for the destination
     * dataset.
     * @param instSrcCompat true if the instance structure is the same as the
     * source, or false if it is the same as the destination
     * @param srcDataset the dataset for which the current instance string
     * references are valid (after any position mapping if needed)
     * @param destDataset the dataset for which the current instance string
     * references need to be inserted (after any position mapping if needed)
     */
    protected void copyStringValues(M5Instance instance, boolean instSrcCompat,
                                    M5Instances srcDataset,
                                    M5Instances destDataset) {

        copyStringValues(instance, instSrcCompat, srcDataset, m_InputStringAtts,
                         destDataset, m_OutputStringAtts);
    }

    /**
     * Takes string values referenced by an Instance and copies them from a
     * source dataset to a destination dataset. The instance references are
     * updated to be valid for the destination dataset. The instance may have the
     * structure (i.e. number and attribute position) of either dataset (this
     * affects where references are obtained from). Only works if the number
     * of string attributes is the same in both indices (implicitly these string
     * attributes should be semantically same but just with shifted positions).
     *
     * @param instance the instance containing references to strings in the source
     * dataset that will have references updated to be valid for the destination
     * dataset.
     * @param instSrcCompat true if the instance structure is the same as the
     * source, or false if it is the same as the destination (i.e. which of the
     * string attribute indices contains the correct locations for this instance).
     * @param srcDataset the dataset for which the current instance string
     * references are valid (after any position mapping if needed)
     * @param srcStrAtts an array containing the indices of string attributes
     * in the source datset.
     * @param destDataset the dataset for which the current instance string
     * references need to be inserted (after any position mapping if needed)
     * @param destStrAtts an array containing the indices of string attributes
     * in the destination datset.
     */
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
     * This will remove all buffered instances from the inputformat dataset.
     * Use this method rather than getInputFormat().delete();
     */
    protected void flushInput() {

        if (m_InputStringAtts.length > 0) {
            m_InputFormat = m_InputFormat.stringFreeStructure();
        } else {
            // This more efficient than new Instances(m_InputFormat, 0);
            m_InputFormat.delete();
        }
    }

    /**
     * @deprecated use <code>setInputFormat(Instances)</code> instead.
     */
    public boolean inputFormat(M5Instances instanceInfo) throws Exception {

        return setInputFormat(instanceInfo);
    }


    /**
     * @deprecated use <code>getOutputFormat()</code> instead.
     */
    public final M5Instances outputFormat() {

        return getOutputFormat();
    }

    /**
     * Gets the format of the output instances. This should only be called
     * after input() or batchFinished() has returned true. The relation
     * name of the output instances should be changed to reflect the
     * action of the filter (eg: add the filter name and options).
     *
     * @return an Instances object containing the output instance
     * structure only.
     * @exception NullPointerException if no input structure has been
     * defined (or the output format hasn't been determined yet)
     */
    public final M5Instances getOutputFormat() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output format defined.");
        }
        return new M5Instances(m_OutputFormat, 0);
    }


    /**
     * Output an instance after filtering and remove from the output queue.
     *
     * @return the instance that has most recently been filtered (or null if
     * the queue is empty).
     * @exception NullPointerException if no output structure has been defined
     */
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

    /**
     * Output an instance after filtering but do not remove from the
     * output queue.
     *
     * @return the instance that has most recently been filtered (or null if
     * the queue is empty).
     * @exception NullPointerException if no input structure has been defined
     */
    public M5Instance outputPeek() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output instance format defined");
        }
        if (m_OutputQueue.empty()) {
            return null;
        }
        M5Instance result = (M5Instance) m_OutputQueue.peek();
        return result;
    }

    /**
     * Returns the number of instances pending output
     *
     * @return the number of instances  pending output
     * @exception NullPointerException if no input structure has been defined
     */
    public int numPendingOutput() {

        if (m_OutputFormat == null) {
            throw new NullPointerException("No output instance format defined");
        }
        return m_OutputQueue.size();
    }

    /**
     * Returns whether the output format is ready to be collected
     *
     * @return true if the output format is set
     */
    public boolean isOutputFormatDefined() {

        return (m_OutputFormat != null);
    }

    /**
     * Gets an array containing the indices of all string attributes.
     *
     * @param insts the Instances to scan for string attributes.
     * @return an array containing the indices of string attributes in
     * the input structure. Will be zero-length if there are no
     * string attributes
     */
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

    /**
     * Filters an entire set of instances through a filter and returns
     * the new set.
     *
     * @param data the data to be filtered
     * @param filter the filter to be used
     * @return the filtered set of data
     * @exception Exception if the filter can't be used successfully
     */
    public static M5Instances useFilter(M5Instances data,
                                        NominalToBinaryFilter filter) throws
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

    /**
     * Method for testing filters.
     *
     * @param options should contain the following arguments: <br>
     * -i input_file <br>
     * -o output_file <br>
     * -c class_index <br>
     * or -h for help on options
     * @exception Exception if something goes wrong or the user requests help on
     * command options
     */
    public static void filterFile(NominalToBinaryFilter filter,
                                  String[] options) throws Exception {

        boolean debug = false;
        M5Instances data = null;
        Reader input = null;
        PrintWriter output = null;
        boolean helpRequest;

        try {
            helpRequest = M5StaticUtils.getFlag('h', options);

            if (M5StaticUtils.getFlag('d', options)) {
                debug = true;
            }
            String infileName = M5StaticUtils.getOption('i', options);
            String outfileName = M5StaticUtils.getOption('o', options);
            String classIndex = M5StaticUtils.getOption('c', options);

            if (filter instanceof NominalToBinaryFilter) {
                ((NominalToBinaryFilter) filter).setOptions(options);
            }

            M5StaticUtils.checkForRemainingOptions(options);
            if (helpRequest) {
                throw new Exception("Help requested.\n");
            }
            if (infileName.length() != 0) {
                input = new BufferedReader(new FileReader(infileName));
            } else {
                input = new BufferedReader(new InputStreamReader(System.in));
            }
            if (outfileName.length() != 0) {
                output = new PrintWriter(new FileOutputStream(outfileName));
            } else {
                output = new PrintWriter(System.out);
            }

            data = new M5Instances(input, 1);
            if (classIndex.length() != 0) {
                if (classIndex.equals("first")) {
                    data.setClassIndex(0);
                } else if (classIndex.equals("last")) {
                    data.setClassIndex(data.numAttributes() - 1);
                } else {
                    data.setClassIndex(Integer.parseInt(classIndex) - 1);
                }
            }
        } catch (Exception ex) {
            String filterOptions = "";
            // Output the error and also the valid options
            if (filter instanceof NominalToBinaryFilter) {
                filterOptions += "\nFilter options:\n\n";
                Enumeration enuma = ((NominalToBinaryFilter) filter).
                                    listOptions();
                while (enuma.hasMoreElements()) {
                    Information option = (Information) enuma.nextElement();
                    filterOptions += option.synopsis() + '\n'
                            + option.description() + "\n";
                }
            }

            String genericOptions = "\nGeneral options:\n\n"
                                    + "-h\n"
                                    + "\tGet help on available options.\n"
                                    + "\t(use -b -h for help on batch mode.)\n"
                                    + "-i <file>\n"
                                    +
                    "\tThe name of the file containing input instances.\n"
                                    +
                    "\tIf not supplied then instances will be read from stdin.\n"
                                    + "-o <file>\n"
                                    +
                    "\tThe name of the file output instances will be written to.\n"
                                    +
                    "\tIf not supplied then instances will be written to stdout.\n"
                                    + "-c <class index>\n"
                                    +
                    "\tThe number of the attribute to use as the class.\n"
                                    +
                    "\t\"first\" and \"last\" are also valid entries.\n"
                                    +
                    "\tIf not supplied then no class is assigned.\n";

            throw new Exception('\n' + ex.getMessage()
                                + filterOptions + genericOptions);
        }

        if (debug) {
            System.err.println("Setting input format");
        }
        boolean printedHeader = false;
        if (filter.setInputFormat(data)) {
            if (debug) {
                System.err.println("Getting output format");
            }
            output.println(filter.getOutputFormat().toString());
            printedHeader = true;
        }

        // Pass all the instances to the filter
        while (data.readInstance(input)) {
            if (debug) {
                System.err.println("Input instance to filter");
            }
            if (filter.input(data.instance(0))) {
                if (debug) {
                    System.err.println("Filter said collect immediately");
                }
                if (!printedHeader) {
                    throw new Error(
                            "Filter didn't return true from setInputFormat() "
                            + "earlier!");
                }
                if (debug) {
                    System.err.println("Getting output instance");
                }
                output.println(filter.output().toString());
            }
            data.delete(0);
        }

        // Say that input has finished, and print any pending output instances
        if (debug) {
            System.err.println("Setting end of batch");
        }
        if (filter.batchFinished()) {
            if (debug) {
                System.err.println("Filter said collect output");
            }
            if (!printedHeader) {
                if (debug) {
                    System.err.println("Getting output format");
                }
                output.println(filter.getOutputFormat().toString());
            }
            if (debug) {
                System.err.println("Getting output instance");
            } while (filter.numPendingOutput() > 0) {
                output.println(filter.output().toString());
                if (debug) {
                    System.err.println("Getting output instance");
                }
            }
        }
        if (debug) {
            System.err.println("Done");
        }

        if (output != null) {
            output.close();
        }
    }

    /**
     * Method for testing filters ability to process multiple batches.
     *
     * @param options should contain the following arguments:<br>
     * -i (first) input file <br>
     * -o (first) output file <br>
     * -r (second) input file <br>
     * -s (second) output file <br>
     * -c class_index <br>
     * or -h for help on options
     * @exception Exception if something goes wrong or the user requests help on
     * command options
     */
    public static void batchFilterFile(NominalToBinaryFilter filter,
                                       String[] options) throws Exception {

        M5Instances firstData = null;
        M5Instances secondData = null;
        Reader firstInput = null;
        Reader secondInput = null;
        PrintWriter firstOutput = null;
        PrintWriter secondOutput = null;
        boolean helpRequest;
        try {
            helpRequest = M5StaticUtils.getFlag('h', options);

            String fileName = M5StaticUtils.getOption('i', options);
            if (fileName.length() != 0) {
                firstInput = new BufferedReader(new FileReader(fileName));
            } else {
                throw new Exception("No first input file given.\n");
            }

            fileName = M5StaticUtils.getOption('r', options);
            if (fileName.length() != 0) {
                secondInput = new BufferedReader(new FileReader(fileName));
            } else {
                throw new Exception("No second input file given.\n");
            }

            fileName = M5StaticUtils.getOption('o', options);
            if (fileName.length() != 0) {
                firstOutput = new PrintWriter(new FileOutputStream(fileName));
            } else {
                firstOutput = new PrintWriter(System.out);
            }

            fileName = M5StaticUtils.getOption('s', options);
            if (fileName.length() != 0) {
                secondOutput = new PrintWriter(new FileOutputStream(fileName));
            } else {
                secondOutput = new PrintWriter(System.out);
            }
            String classIndex = M5StaticUtils.getOption('c', options);

            if (filter instanceof NominalToBinaryFilter) {
                ((NominalToBinaryFilter) filter).setOptions(options);
            }
            M5StaticUtils.checkForRemainingOptions(options);

            if (helpRequest) {
                throw new Exception("Help requested.\n");
            }
            firstData = new M5Instances(firstInput, 1);
            secondData = new M5Instances(secondInput, 1);
            if (!secondData.equalHeaders(firstData)) {
                throw new Exception("Input file formats differ.\n");
            }
            if (classIndex.length() != 0) {
                if (classIndex.equals("first")) {
                    firstData.setClassIndex(0);
                    secondData.setClassIndex(0);
                } else if (classIndex.equals("last")) {
                    firstData.setClassIndex(firstData.numAttributes() - 1);
                    secondData.setClassIndex(secondData.numAttributes() - 1);
                } else {
                    firstData.setClassIndex(Integer.parseInt(classIndex) - 1);
                    secondData.setClassIndex(Integer.parseInt(classIndex) - 1);
                }
            }
        } catch (Exception ex) {
            String filterOptions = "";
            // Output the error and also the valid options
            if (filter instanceof NominalToBinaryFilter) {
                filterOptions += "\nFilter options:\n\n";
                Enumeration enume = ((NominalToBinaryFilter) filter).
                                    listOptions();
                while (enume.hasMoreElements()) {
                    Information option = (Information) enume.nextElement();
                    filterOptions += option.synopsis() + '\n'
                            + option.description() + "\n";
                }
            }

            String genericOptions = "\nGeneral options:\n\n"
                                    + "-h\n"
                                    + "\tGet help on available options.\n"
                                    + "-i <filename>\n"
                                    +
                    "\tThe file containing first input instances.\n"
                                    + "-o <filename>\n"
                                    +
                    "\tThe file first output instances will be written to.\n"
                                    + "-r <filename>\n"
                                    +
                    "\tThe file containing second input instances.\n"
                                    + "-s <filename>\n"
                                    +
                    "\tThe file second output instances will be written to.\n"
                                    + "-c <class index>\n"
                                    +
                    "\tThe number of the attribute to use as the class.\n"
                                    +
                    "\t\"first\" and \"last\" are also valid entries.\n"
                                    +
                    "\tIf not supplied then no class is assigned.\n";

            throw new Exception('\n' + ex.getMessage()
                                + filterOptions + genericOptions);
        }
        boolean printedHeader = false;
        if (filter.setInputFormat(firstData)) {
            firstOutput.println(filter.getOutputFormat().toString());
            printedHeader = true;
        }

        // Pass all the instances to the filter
        while (firstData.readInstance(firstInput)) {
            if (filter.input(firstData.instance(0))) {
                if (!printedHeader) {
                    throw new Error(
                            "Filter didn't return true from setInputFormat() "
                            + "earlier!");
                }
                firstOutput.println(filter.output().toString());
            }
            firstData.delete(0);
        }

        // Say that input has finished, and print any pending output instances
        if (filter.batchFinished()) {
            if (!printedHeader) {
                firstOutput.println(filter.getOutputFormat().toString());
            } while (filter.numPendingOutput() > 0) {
                firstOutput.println(filter.output().toString());
            }
        }

        if (firstOutput != null) {
            firstOutput.close();
        }
        printedHeader = false;
        if (filter.isOutputFormatDefined()) {
            secondOutput.println(filter.getOutputFormat().toString());
            printedHeader = true;
        }
        // Pass all the second instances to the filter
        while (secondData.readInstance(secondInput)) {
            if (filter.input(secondData.instance(0))) {
                if (!printedHeader) {
                    throw new Error("Filter didn't return true from"
                                    + " isOutputFormatDefined() earlier!");
                }
                secondOutput.println(filter.output().toString());
            }
            secondData.delete(0);
        }

        // Say that input has finished, and print any pending output instances
        if (filter.batchFinished()) {
            if (!printedHeader) {
                secondOutput.println(filter.getOutputFormat().toString());
            } while (filter.numPendingOutput() > 0) {
                secondOutput.println(filter.output().toString());
            }
        }
        if (secondOutput != null) {
            secondOutput.close();
        }
    }


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
        if (instanceInfo.classIndex() < 0) {
            throw new Exception("No class has been assigned to the instances");
        }
        setOutputFormat();
        m_Indices = null;
        if (instanceInfo.classAttribute().isNominal()) {
            return true;
        } else {
            return false;
        }
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

    /**
     * Input an instance for filtering. Filter requires all
     * training instances be read before producing output.
     *
     * @param instance the input instance
     * @return true if the filtered instance may now be
     * collected with output().
     * @throws Exception
     * @exception IllegalStateException if no input format has been set
     */
    public boolean input(M5Instance instance) throws Exception {

        if (getInputFormat() == null) {
            throw new IllegalStateException("No input instance format defined");
        }
        if (m_NewBatch) {
            resetQueue();
            m_NewBatch = false;
        }
        if ((m_Indices != null) ||
            (getInputFormat().classAttribute().isNominal())) {
            convertInstance(instance);
            return true;
        }
        bufferInput(instance);
        return false;
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
        if ((m_Indices == null) &&
            (getInputFormat().classAttribute().isNumeric())) {
            computeAverageClassValues();
            setOutputFormat();

            // Convert pending input instances

            for (int i = 0; i < getInputFormat().numInstances(); i++) {
                convertInstance(getInputFormat().instance(i));
            }
        }
        flushInput();

        m_NewBatch = true;
        return (numPendingOutput() != 0);
    }

    /**
     * Returns an enumeration describing the available options
     *
     * @return an enumeration of all the available options
     */
    public Enumeration listOptions() {

        Vector newVector = new Vector(1);

        newVector.addElement(new Information(
                "\tSets if binary attributes are to be coded as nominal ones.",
                "N", 0, "-N"));

        return newVector.elements();
    }


    /**
     * Parses the options for this object. Valid options are: <p>
     *
     * -N <br>
     * If binary attributes are to be coded as nominal ones.<p>
     *
     * @param options the list of options as an array of strings
     * @exception Exception if an option is not supported
     */
    public void setOptions(String[] options) throws Exception {

        setBinaryAttributesNominal(M5StaticUtils.getFlag('N', options));

        if (getInputFormat() != null) {
            setInputFormat(getInputFormat());
        }
    }

    /**
     * Gets the current settings of the filter.
     *
     * @return an array of strings suitable for passing to setOptions
     */
    public String[] getOptions() {

        String[] options = new String[1];
        int current = 0;

        if (getBinaryAttributesNominal()) {
            options[current++] = "-N";
        }

        while (current < options.length) {
            options[current++] = "";
        }
        return options;
    }

    /**
     * Gets if binary attributes are to be treated as nominal ones.
     *
     * @return true if binary attributes are to be treated as nominal ones
     */
    public boolean getBinaryAttributesNominal() {

        return!m_Numeric;
    }

    /**
     * Sets if binary attributes are to be treates as nominal ones.
     *
     * @param bool true if binary attributes are to be treated as nominal ones
     */
    public void setBinaryAttributesNominal(boolean bool) {

        m_Numeric = !bool;
    }

    /** Computes average class values for each attribute and value
     * @throws Exception */
    private void computeAverageClassValues() throws Exception {

        double totalCounts, sum;
        M5Instance instance;
        double[] counts;

        double[][] avgClassValues = new double[getInputFormat().numAttributes()][
                                    0];
        m_Indices = new int[getInputFormat().numAttributes()][0];
        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            M5Attribute att = getInputFormat().attribute(j);
            if (att.isNominal()) {
                avgClassValues[j] = new double[att.numValues()];
                counts = new double[att.numValues()];
                for (int i = 0; i < getInputFormat().numInstances(); i++) {
                    instance = getInputFormat().instance(i);
                    if (!instance.classIsMissing() &&
                        (!instance.isMissing(j))) {
                        counts[(int) instance.value(j)] += instance.weight();
                        avgClassValues[j][(int) instance.value(j)] +=
                                instance.weight() * instance.classValue();
                    }
                }
                sum = M5StaticUtils.sum(avgClassValues[j]);
                totalCounts = M5StaticUtils.sum(counts);
                if (M5StaticUtils.gr(totalCounts, 0)) {
                    for (int k = 0; k < att.numValues(); k++) {
                        if (M5StaticUtils.gr(counts[k], 0)) {
                            avgClassValues[j][k] /= (double) counts[k];
                        } else {
                            avgClassValues[j][k] = sum / (double) totalCounts;
                        }
                    }
                }
                m_Indices[j] = M5StaticUtils.sort(avgClassValues[j]);
            }
        }
    }

    /** Set the output format.
     * @throws Exception */
    private void setOutputFormat() throws Exception {

        if (getInputFormat().classAttribute().isNominal()) {
            setOutputFormatNominal();
        } else {
            setOutputFormatNumeric();
        }
    }

    /**
     * Convert a single instance over. The converted instance is
     * added to the end of the output queue.
     *
     * @param instance the instance to convert
     * @throws Exception
     */
    private void convertInstance(M5Instance inst) throws Exception {

        if (getInputFormat().classAttribute().isNominal()) {
            convertInstanceNominal(inst);
        } else {
            convertInstanceNumeric(inst);
        }
    }

    /**
     * Set the output format if the class is nominal.
     */
    private void setOutputFormatNominal() {

        M5Vector newAtts;
        int newClassIndex;
        StringBuffer attributeName;
        M5Instances outputFormat;
        M5Vector vals;

        // Compute new attributes

        newClassIndex = getInputFormat().classIndex();
        newAtts = new M5Vector();
        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            M5Attribute att = getInputFormat().attribute(j);
            if ((!att.isNominal()) ||
                (j == getInputFormat().classIndex())) {
                newAtts.addElement(att.copy());
            } else {
                if (att.numValues() <= 2) {
                    if (m_Numeric) {
                        newAtts.addElement(new M5Attribute(att.name()));
                    } else {
                        newAtts.addElement(att.copy());
                    }
                } else {

                    if (j < getInputFormat().classIndex()) {
                        newClassIndex += att.numValues() - 1;
                    }

                    // Compute values for new attributes
                    for (int k = 0; k < att.numValues(); k++) {
                        attributeName =
                                new StringBuffer(att.name() + "=");
                        attributeName.append(att.value(k));
                        if (m_Numeric) {
                            newAtts.
                                    addElement(new M5Attribute(attributeName.
                                    toString()));
                        } else {
                            vals = new M5Vector(2);
                            vals.addElement("f");
                            vals.addElement("t");
                            newAtts.
                                    addElement(new M5Attribute(attributeName.
                                    toString(), vals));
                        }
                    }
                }
            }
        }
        outputFormat = new M5Instances(getInputFormat().relationName(),
                                       newAtts, 0);
        outputFormat.setClassIndex(newClassIndex);
        setOutputFormat(outputFormat);
    }

    /**
     * Set the output format if the class is numeric.
     */
    private void setOutputFormatNumeric() {

        if (m_Indices == null) {
            setOutputFormat(null);
            return;
        }
        M5Vector newAtts;
        int newClassIndex;
        StringBuffer attributeName;
        M5Instances outputFormat;
        M5Vector vals;

        // Compute new attributes

        newClassIndex = getInputFormat().classIndex();
        newAtts = new M5Vector();
        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            M5Attribute att = getInputFormat().attribute(j);
            if ((!att.isNominal()) ||
                (j == getInputFormat().classIndex())) {
                newAtts.addElement(att.copy());
            } else {
                if (j < getInputFormat().classIndex()) {
                    newClassIndex += att.numValues() - 2;
                }

                // Compute values for new attributes

                for (int k = 1; k < att.numValues(); k++) {
                    attributeName =
                            new StringBuffer(att.name() + "=");
                    for (int l = k; l < att.numValues(); l++) {
                        if (l > k) {
                            attributeName.append(',');
                        }
                        attributeName.append(att.value(m_Indices[j][l]));
                    }
                    if (m_Numeric) {
                        newAtts.
                                addElement(new M5Attribute(attributeName.
                                toString()));
                    } else {
                        vals = new M5Vector(2);
                        vals.addElement("f");
                        vals.addElement("t");
                        newAtts.
                                addElement(new M5Attribute(attributeName.
                                toString(), vals));
                    }
                }
            }
        }
        outputFormat = new M5Instances(getInputFormat().relationName(),
                                       newAtts, 0);
        outputFormat.setClassIndex(newClassIndex);
        setOutputFormat(outputFormat);
    }

    /**
     * Convert a single instance over if the class is nominal. The converted
     * instance is added to the end of the output queue.
     *
     * @param instance the instance to convert
     */
    private void convertInstanceNominal(M5Instance instance) {

        double[] vals = new double[outputFormatPeek().numAttributes()];
        int attSoFar = 0;

        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            M5Attribute att = getInputFormat().attribute(j);
            if ((!att.isNominal()) || (j == getInputFormat().classIndex())) {
                vals[attSoFar] = instance.value(j);
                attSoFar++;
            } else {
                if (att.numValues() <= 2) {
                    vals[attSoFar] = instance.value(j);
                    attSoFar++;
                } else {
                    if (instance.isMissing(j)) {
                        for (int k = 0; k < att.numValues(); k++) {
                            vals[attSoFar + k] = instance.value(j);
                        }
                    } else {
                        for (int k = 0; k < att.numValues(); k++) {
                            if (k == (int) instance.value(j)) {
                                vals[attSoFar + k] = 1;
                            } else {
                                vals[attSoFar + k] = 0;
                            }
                        }
                    }
                    attSoFar += att.numValues();
                }
            }
        }
        M5Instance inst = null;
        if (instance instanceof M5SparseInstance) {
            inst = new M5SparseInstance(instance.weight(), vals);
        } else {
            inst = new M5Instance(instance.weight(), vals);
        }
        copyStringValues(inst, false, instance.dataset(), getInputStringIndex(),
                         getOutputFormat(), getOutputStringIndex());
        inst.setDataset(getOutputFormat());
        push(inst);
    }

    /**
     * Convert a single instance over if the class is numeric. The converted
     * instance is added to the end of the output queue.
     *
     * @param instance the instance to convert
     */
    private void convertInstanceNumeric(M5Instance instance) {

        double[] vals = new double[outputFormatPeek().numAttributes()];
        int attSoFar = 0;

        for (int j = 0; j < getInputFormat().numAttributes(); j++) {
            M5Attribute att = getInputFormat().attribute(j);
            if ((!att.isNominal()) || (j == getInputFormat().classIndex())) {
                vals[attSoFar] = instance.value(j);
                attSoFar++;
            } else {
                if (instance.isMissing(j)) {
                    for (int k = 0; k < att.numValues() - 1; k++) {
                        vals[attSoFar + k] = instance.value(j);
                    }
                } else {
                    int k = 0;
                    while ((int) instance.value(j) != m_Indices[j][k]) {
                        vals[attSoFar + k] = 1;
                        k++;
                    } while (k < att.numValues() - 1) {
                        vals[attSoFar + k] = 0;
                        k++;
                    }
                }
                attSoFar += att.numValues() - 1;
            }
        }
        M5Instance inst = null;
        if (instance instanceof M5SparseInstance) {
            inst = new M5SparseInstance(instance.weight(), vals);
        } else {
            inst = new M5Instance(instance.weight(), vals);
        }
        copyStringValues(inst, false, instance.dataset(), getInputStringIndex(),
                         getOutputFormat(), getOutputStringIndex());
        inst.setDataset(getOutputFormat());
        push(inst);
    }


}


