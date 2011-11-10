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
 *
 * File: Parameters.java
 *
 * Read patterns file
 *
 * @author Salvador Garcia Lopez
 * @author Julian Luengo Martin (modifications 19/04/2009)
 * @author joaquín Derrac Rus (modifications 6/05/2009)
 * @author Modified by Juan Carlos Fernandez Caballero and Pedro Antonio Gutierrez (University of Córdoba) 7/07/2009
 * @author Modified Ana Palacios Jimenez and Luciano Sanchez Ramons 23-4-2010 (University of Oviedo)
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.net.*;

import org.jdom.*;
import org.jdom.input.*;

import keel.GraphInterKeel.menu.Frame;

public class Parameters implements Serializable {

    private String algorithmType; // algorithm name
    private int nParameters; // numbers of parameters
    private boolean allowsSeed; // true if algorithm accepts random seeds
    private int nOutputs; // number of extra outputs
    private int nInputs; // number of max inputs in the graph
    private int n_exe; // number of executions
    private Vector seeds; // random seeds
    public Vector descriptions = new Vector(); // parameter names
    public Vector parameterType = new Vector(); // parameter types
    public Vector domain = new Vector(); // parameter domains
    public Vector defaultValue = new Vector(); // parameter default values
    public Vector value = new Vector(); // parameter actual values
    public Vector hidden = new Vector(); // Are showed or not in the dialog
    public Vector outputs_tra = new Vector(); // .tra output files
    public Vector outputs_tst = new Vector(); // .tst output files
    public Vector tra_val = new Vector(); // original training files to validate
    public Vector tst_val = new Vector(); // original test files
    public Vector configs = new Vector(); // config files generated  by method
    public Vector additional_outputs = new Vector(); // Additional output files
    public Vector<String> dataset_used = new Vector<String>();
    public boolean cost_instance;
    public int crisp;
    public boolean fuzzy;

    /**
     * Default builder
     */
    public Parameters() {
        seeds = new Vector();
    }

    /**
     * Gets the algorithm type
     * @return the string with the algorithm type
     */
    public String getAlgorithmType() {
        return algorithmType;
    }

    /**
     * Sets the algorithm type
     * @param type the new algorithm type
     */
    public void setAlgorithmType(String type) {
        this.algorithmType = type;
    }

    /**
     * Gets the number of parameters
     * @return the current number of parameters
     */
    public int getNumParameters() {
        return nParameters;
    }

    /**
     * Sets the number of paramters
     * @param n the new number of parameters
     */
    public void setNumParameters(int n) {
        nParameters = n;
    }

    /**
     * Checks if the algorithm need seed
     * @return true if the algorithm associated uses seed
     */
    public boolean getIfSeed() {
        return allowsSeed;
    }

    /**
     * Sets the need for seed of the algorithm
     * @param seed the status for the need of seed
     */
    public void setIfSeed(boolean seed) {
        allowsSeed = seed;
    }

    /**
     * Gets the number of visible paramters
     * @return the number of visible parameters
     */
    public int getNVisibleParams() {
        int n = 0;
        for (int i = 0; i < descriptions.size(); i++) {
            if (!isHidden(i)) {
                n++;
            }
        }
        return n;
    }

    /**
     * returns if algorithm use seeds
     * @return if the algorithm uses seed
     */
    public boolean isProbabilistic() {
        return allowsSeed;
    }

    /**
     *  return number of extra outputs
     * @return the number of extra outputs
     */
    public int getNumOutputs() {
        return nOutputs;
    }

    /**
     * Sets the number of extra outputs
     * @param n the new number of extra outputs
     */
    public void setNumOutputs(int n) {
        nOutputs = n;
    }

    /**
     *  return number of input connections
     * @return the number of inputs
     */
    public int getNumInputs() {
        return nInputs;
    }

    /**
     * sets the number of inputs connections
     * @param n the new number of inputs
     */
    public void setNumInputs(int n) {
        nInputs = n;
    }

    /**
     *  return actual seeds
     * @return a vector with the current seeds
     */
    public Vector getSeeds() {
        return seeds;
    }

    /**
     * Sets the new seeds
     * @param s vector with the new seeds
     */
    public void setSeeds(Vector s) {
        seeds = s;
    }

    /**
     *  return seed at index position
     * @param index the index of the seed
     * @return the seed at position index
     */
    public String getSeed(int index) {
        return (String) (seeds.elementAt(index));
    }

    /**
     *  return number of executions
     * @return the number of executions
     */
    public int getExe() {
        return n_exe;
    }

    /**
     *  modify number of executions
     * @param executions the new number of executions
     */
    public void setExe(int executions) {
        n_exe = executions;
    }

    /**
     *  return parameter names
     * @return the descriptions of the parameters
     */
    public Vector setDescriptions() {
        return descriptions;
    }

    /**
     *  return parameter name for parameter at index position
     * @param index the index of the parameter
     * @return the description of the indexed parameter
     */
    public String getDescriptions(int index) {
        return (String) (descriptions.elementAt(index));
    }

    /**
     * Gets the hidden parameters
     * @return a vector with the not visible parameters
     */
    public Vector getHidden() {
        return hidden;
    }

    /**
     * Checks if the parameter at position i is hidden
     * @param i position of the parameter
     * @return true if it is not visible, false otherwise
     */
    public boolean isHidden(int i) {
        if (hidden.size() <= i) {
            return false;
        } else {
            Integer I = (Integer) (hidden.elementAt(i));
            if (I.intValue() == 1) {
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     *  return parameter types
     * @return a vector with the types of all parameters
     */
    public Vector getParameterTypes() {
        return parameterType;
    }

    /**
     * return parameter name for parameter at index position
     * @param index the index of the parameter
     * @return the name of the indexed parameter
     */
    public String getParameterType(int index) {
        return (String) (parameterType.elementAt(index));
    }

    /**
     * return parameter domains
     * @return a vector with all parameter domains
     */
    public Vector getDomains() {
        return domain;
    }

    /**
     * return parameter domain for parameter at index position
     * @param index The position of the indexed parameter
     * @return the domain of the parameter
     */
    public Vector getDomain(int index) {
        return (Vector) (domain.elementAt(index));
    }

    /**
     * returns domain value at position pos for parameter at index position
     * @param index the index of the parameter
     * @param pos the position of the value of the parameter
     * @return the domain of the selected position
     */
    public String getDomainValue(int index, int pos) {
        // LSR remove blank spaces
        String result = (String) (((Vector) (domain.elementAt(index))).elementAt(
                pos));
        return result.trim();
    }

    /**
     * return parameter default values
     * @return a vector with the default values
     */
    public Vector getDefaultValues() {
        return defaultValue;
    }

    /**
     * return default value for parameter at index position
     * @param index the index of the parameter
     * @return the default value for the parameter
     */
    public String getDefaultValue(int index) {
        return (String) (defaultValue.elementAt(index));
    }

    /**
     * return actual values
     * @return a vector with all the actual values
     */
    public Vector getValues() {
        return value;
    }

    /**
     * return actual value for parameter at index position
     * @param index the index of the parameter
     * @return the current value
     */
    public String getValue(int index) {
        return (String) (value.elementAt(index));
    }

    /**
     * updates value vector
     * @param input the new vector with all the values
     */
    public void setValues(Vector input) {
        value = (Vector) input.clone();
    }

    /**
     * updates actual value for parameter at index position
     * @param index the position of the parameter
     * @param input the new value
     */
    public void setValue(int index, String input) {
        value.setElementAt(input, index);
    }

    /**
     * removes seed
     * @param input the seed to be removed
     */
    public void removeSeed(String input) {
        seeds.remove(input);
    }

    /**
     * adds seed
     * @param input the seed to be added
     */
    public void addSeed(String input) {
        if (!seeds.contains(input)) {
            seeds.addElement(input);
        }
    }

    /**
     *  removes all seeds
     */
    public void clearSeeds() {
        seeds.clear();
    }

    /**
     * return .tra output files
     * @return the training output files (paths)
     */
    public Vector getTrainingOutputFiles() {
        return outputs_tra;
    }

    /**
     * return .tst output files
     * @return the test output files (paths)
     */
    public Vector getTestOutputFiles() {
        return outputs_tst;
    }

    /**
     * return training files to validate
     * @return the validation training files
     */
    public Vector getTrainingValidationFiles() {
        return tra_val;
    }

    /**
     * return original test files
     * @return the test file for validation
     */
    public Vector getTestFiles() {
        return tst_val;
    }

    /**
     * Gets datasets useds
     * @return Datasets useds
     */
    public Vector getdataset_used() {
        return dataset_used;
    }

    /**
     * Gets cost instances
     * @return Cost instances
     */
    public boolean getCost_instances() {
        return cost_instance;
    }

    /**
     * Get fuzzy status
     * @return Fuzzy status
     */
    public boolean getFuzzy() {
        return fuzzy;
    }

    /**
     * Get crisp status
     * @return Crisp status
     */
    public int getCrisp() {
        return crisp;
    }

    /**
     * Is cost instance
     * @return Is cost instance
     */
    public boolean isCost_instance() {

        return cost_instance;
    }

    /**
     * Is crisp
     * @return Is crisp
     */
    public int isCrisp() {
        return crisp;
    }

    /**
     * Is fuzzy
     * @return Is fuzzy
     */
    public boolean isFuzzy() {
        return fuzzy;
    }

    /**
     * Set cost instance
     * @param cost Type of cost
     */
    public void setcost_instance(boolean cost) {
        cost_instance = cost;
    }

    /**
     * Set crisp
     * @param cr Id of crisp
     */
    public void setcrisp(int cr) {
        crisp = cr;
    }

    /**
     * Set fuzzy status
     * @param fuz Fuzzy status
     */
    public void setfuzzy(boolean fuz) {
        fuzzy = fuz;
    }

    /**
     * return additional output files
     * @return the additional output files for the method
     */
    public Vector getAdditionalOutputFiles() {
        return additional_outputs;
    }

    /**
     * return config files generated by method
     * @return a vector with all the paths to the configuration files
     */
    public Vector getConfigs() {
        return configs;
    }

    /**
     * Copy the parameters to this object
     * @param param the reference parameters object to be copied
     */
    public void copyParameters(Parameters param) {
        algorithmType = param.getAlgorithmType();
        nParameters = param.getNumParameters();
        allowsSeed = param.isProbabilistic();
        nOutputs = param.getNumOutputs();

        n_exe = param.getExe();
        seeds = (Vector) param.getSeeds().clone();

        descriptions = (Vector) param.setDescriptions().clone();
        parameterType = (Vector) param.getParameterTypes().clone();
        domain = (Vector) param.getDomains().clone();
        defaultValue = (Vector) param.getDefaultValues().clone();
        value = (Vector) param.getValues().clone();
        hidden = (Vector) param.getHidden().clone();
        dataset_used = (Vector) param.getdataset_used().clone();
        cost_instance = (Boolean) param.getCost_instances();
        crisp = param.getCrisp();
        fuzzy = (Boolean) param.getFuzzy();
    }

    /**
     * Builder that copies another Parametros object
     * @param param reference object
     */
    public Parameters(Parameters param) {
        algorithmType = param.getAlgorithmType();
        nParameters = param.getNumParameters();
        allowsSeed = param.isProbabilistic();
        nOutputs = param.getNumOutputs();

        n_exe = param.getExe();
        seeds = (Vector) param.getSeeds().clone();

        if (nParameters > 0) {
            descriptions = (Vector) param.setDescriptions().clone();
            parameterType = (Vector) param.getParameterTypes().clone();
            domain = (Vector) param.getDomains().clone();
            defaultValue = (Vector) param.getDefaultValues().clone();
            value = (Vector) param.getValues().clone();
            hidden = (Vector) param.getHidden().clone();
        }
        dataset_used = (Vector) param.getdataset_used().clone();
        cost_instance = param.getCost_instances();
        crisp = param.getCrisp();
        fuzzy = param.getFuzzy();
    }

    /**
     * Read pattern file
     * @param fileName the XML file with the parameters
     * @param test true if this parameter file correspond to a test algorithm
     */
    public Parameters(String fileName, boolean test) {
        try {
            int i = 0, j;
            n_exe = 1;
            seeds = new Vector();
            descriptions = new Vector();
            parameterType = new Vector();
            domain = new Vector();
            defaultValue = new Vector();
            value = new Vector();
            hidden = new Vector();
            Document doc = new Document();

            String token, temp;
            Element specification, tempEle;

            URL urlFile;

            urlFile = new URL(fileName);
            try {
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(new File(urlFile.getPath()));
            } catch (JDOMException e) {
                e.printStackTrace();
            }

            specification = doc.getRootElement();

            int con = 0;
            String va = specification.getChildText("dataset" + con);
            while (va != null) {
                dataset_used.addElement(va);
                con++;
                va = specification.getChildText("dataset" + con);
            }

            va = specification.getChildText("cost_instances");
            if (va != null) {
                if (va.compareTo("Yes") == 0) {
                    cost_instance = true;
                } else {
                    cost_instance = false;
                }
            }

            va = specification.getChildText("crisp");
            if (va != null) {
                if (va.compareTo("Yes") == 0) {
                    crisp = 1;
                } else {
                    crisp = 0;
                }
            }

            va = specification.getChildText("fuzzy");
            if (va != null) {
                if (va.compareTo("Yes") == 0) {
                    fuzzy = true;
                } else {
                    fuzzy = false;
                }
            }


            algorithmType = specification.getChildText("name");
            nParameters = Integer.parseInt(specification.getChildText("nParameters"));
            if (Integer.parseInt(specification.getChildText("seed")) == 1) {
                allowsSeed = true;
            } else {
                allowsSeed = false;
            }
            nOutputs = Integer.parseInt(specification.getChildText("nOutput"));


            if (test) { //pattern file corresponding with a test algorithm
                if (specification.getChildText("nInput").trim().equals("N")) {
                    nInputs = -1;
                } else {
                    nInputs = Integer.parseInt(specification.getChildText("nInput"));
                }
            }

            // parameters
            List parametrosL = specification.getChildren("parameter");
            for (i = 0; i < nParameters; i++) {
                tempEle = (Element) parametrosL.get(i);
                token = tempEle.getChildText("name");
                if (token.equalsIgnoreCase("semilla")) {
                    throw new IOException("Parameter 'semilla' is deprecated\n");
                } else if (descriptions.contains(token)) {
                    throw new IOException("Parameter [" +
                            token + "] is duplicated\n");
                } else {
                    descriptions.addElement(new String(token));
                }
                token = tempEle.getChildText("type");

                if (token.equalsIgnoreCase("integer") || token.equalsIgnoreCase("real") ||
                        token.equalsIgnoreCase("list") || token.equalsIgnoreCase("text")) {
                    parameterType.addElement(new String(token));
                } else {
                    throw new IOException("Value list of parameter [" + token +
                            "]" +
                            " has an error.");
                }
                domain.addElement(new Vector());
                if (tempEle.getChild("domain") != null) {
                    List dominios = tempEle.getChild("domain").getChildren();
                    for (j = 0; j < dominios.size(); j++) {
                        temp = ((Element) dominios.get(j)).getText();
                        ((Vector) (domain.elementAt(i))).addElement(new String(temp));
                    }
                }
                token = tempEle.getChildText("default");
                defaultValue.addElement(new String(token));
                value.addElement(new String(token));

                // check if 'disabled' appears
                hidden.addElement(new Integer(0));
                if (tempEle.getChild("disabled") != null) {
                    hidden.setElementAt(new Integer(1), hidden.size() - 1);
                //System.out.println("Hidden " + savedesc + " parameter");
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println(e + "Pattern file not found");
        } catch (IOException e) {
            System.err.println(e + "IO Error not specified");
        }
    }

    /**
     * Write a configuration script for the method, employing its parameters
     * @param path Path of the method
     * @param baseName Baseline name of the config files
     * @param methodName Name of the method
     * @param problemName Name of the problem
     * @param set Set of input files
     * @param result Baseline name of result files
     * @param pre True if this is a preprocess method
     * @param valType Type of validation (0- k-Fold  1- 5x2 2- None)
     * @param numFolds Number of folds in cross validation
     * @param expType Type of experiment
     */
    public void writeScripts(String path, String baseName, String methodName,
            String problemName, Vector set, String result,
            boolean pre, int valType, int numFolds, int expType) {

        String fichero, nombre, aux;
        int i, j, k, cont = 0;
        int times;
        File f;
        String typeCV = "";

        //Compute validation prefix to name the files
        if (valType == 0) {
            typeCV = "-" + numFolds + "-";
        } else {
            if (valType == 1) {
                typeCV = "-5x2-";
            }
        }

        //Get the number of executions of the method
        times = n_exe;
        if (!allowsSeed) {
            times = 1;
        }


        try {
            // Check that script doesn't exist ()
            if (!allowsSeed) {

                nombre = (new File(path)).getCanonicalPath() + File.separator +
                        baseName + cont + ".txt";
                f = new File(nombre);
                while (f.exists()) {
                    cont++;
                    nombre = (new File(path)).getCanonicalPath() + File.separator +
                            baseName + cont + ".txt";
                    f = new File(nombre);
                }
            } else {
                nombre = (new File(path)).getCanonicalPath() + File.separator +
                        baseName + cont + "s0.txt";
                f = new File(nombre);
                while (f.exists()) {
                    cont++;
                    nombre = (new File(path)).getCanonicalPath() + File.separator +
                            baseName + cont + "s0.txt";
                    f = new File(nombre);
                }
            }
        } catch (IOException e) {
            System.err.println(e + "IO error not specified");
        }

        //For every set of files
        for (i = 0; i < ((Vector) (set.elementAt(0))).size(); i++) {
            fichero = "";

            //For any execution
            for (j = 0; j < times; j++) {
                fichero = "algorithm = " + algorithmType + "\n";
                fichero += "inputData = ";

                if (pre) {
                    fichero += "\"" + ((Vector) (set.elementAt(0))).elementAt(i) +
                            "\" ";
                    fichero += "\"" + ((Vector) (set.elementAt(2))).elementAt(i) +
                            "\" ";
                } else {
                    if (expType != 2) {
                        for (k = 0; k < 3; k++) {
                            fichero += "\"" + ((Vector) (set.elementAt(k))).elementAt(i) +
                                    "\" ";
                        }
                    } else {
                        //unsupervised
                        fichero += "\"" + ((Vector) (set.elementAt(0))).elementAt(i) +
                                "\" ";
                    }
                }
                if (set.size() == 5) {
                    Vector temp = (Vector) ((Vector) (set.elementAt(4))).elementAt(i);
                    for (int s = 0; s < temp.size(); s++) {
                        fichero += "\"" + temp.elementAt(s) + "\" ";
                    }
                }

                fichero += "\n";
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (Frame.buttonPressed == 0) //Button Experiments pressed
                {
                    if (!allowsSeed) {
                        if (pre) {
                            aux = "../datasets/" + methodName + "." +
                                    problemName + "/" + methodName + "." +
                                    problemName + typeCV + (cont + 1) + "tra.dat";

                            outputs_tra.add(aux);
                            fichero += "outputData = \"" + aux + "\" ";

                            aux = "../datasets/" + methodName + "." +
                                    problemName + "/" + methodName + "." +
                                    problemName + typeCV + (cont + 1) + "tst.dat";

                            outputs_tst.add(aux);
                            fichero += "\"" + aux + "\" ";

                        } else {
                            aux = "../results/" + methodName + "." +
                                    problemName + "/" + result + cont + ".tra";

                            outputs_tra.add(aux);
                            fichero += "outputData = \"" + aux + "\" ";

                            aux = "../results/" + methodName + "." +
                                    problemName + "/" + result + cont + ".tst";

                            outputs_tst.add(aux);
                            fichero += "\"" + aux + "\" ";

                        }
                    } else {
                        if (pre) {

                            aux = "../datasets/" + methodName + "." +
                                    problemName + "/" + methodName + "s" + j + "." +
                                    problemName + typeCV + (cont + 1) + "tra.dat";

                            outputs_tra.add(aux);
                            fichero += "outputData = \"" + aux + "\" ";

                            aux = "../datasets/" + methodName + "." +
                                    problemName + "/" + methodName + "s" + j + "." +
                                    problemName + typeCV + (cont + 1) + "tst.dat";

                            outputs_tst.add(aux);
                            fichero += "\"" + aux + "\" ";

                        } else {
                            aux = "../results/" + methodName + "." +
                                    problemName + "/" + result + cont + "s" + j + ".tra";

                            outputs_tra.add(aux);
                            fichero += "outputData = \"" + aux + "\" ";

                            aux = "../results/" + methodName + "." +
                                    problemName + "/" + result + cont + "s" + j + ".tst";

                            outputs_tst.add(aux);
                            fichero += "\"" + aux + "\" ";

                        }
                    }
                } else //Button Teaching pressed
                {
                    if (!allowsSeed) {
                        fichero += "outputData = \"./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont + ".tra\" ";
                        fichero += "\"./experiment/results/" + methodName + "/" + problemName + "/" +
                                result + cont + ".tst\" ";

                        outputs_tra.add(new String("./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont +
                                ".tra"));
                        outputs_tst.add(new String("./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont +
                                ".tst"));
                    } else {
                        fichero += "outputData = \"./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont + "s" + j + ".tra\" ";
                        fichero += "\"./experiment/results/" + methodName + "/" + problemName +
                                "/" +
                                result + cont + "s" + j + ".tst\" ";

                        outputs_tra.add(new String("./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont + "s" +
                                j +
                                ".tra"));
                        outputs_tst.add(new String("./experiment/results/" + methodName + "/" +
                                problemName + "/" + result + cont + "s" +
                                j +
                                ".tst"));
                    }
                }
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (pre) {

                    if (methodName.endsWith("-D") || methodName.endsWith("-FS") || methodName.endsWith("-TR")) {
                        //using modified training data as validation file
                        tra_val.add(outputs_tra.elementAt(i));
                        tst_val.add(outputs_tst.elementAt(i));

                    } else {
                        //using initial data as validation file
                        tra_val.add(((Vector) set.elementAt(1)).elementAt(i));
                        tst_val.add(((Vector) set.elementAt(3)).elementAt(i));
                    }

                } else {
                    //using initial data as validation file
                    tra_val.add(((Vector) set.elementAt(1)).elementAt(i));
                    tst_val.add(((Vector) set.elementAt(3)).elementAt(i));
                }

                if (nOutputs != 0) {
                    Vector temp = new Vector();
                    for (k = 0; k < nOutputs; k++) {
                        /***************************************************************
                         ***************  EDUCATIONAL KEEL  ****************************
                         **************************************************************/
                        if (Frame.buttonPressed == 0) //Button Experiments pressed
                        {
                            if (!allowsSeed) {

                                if (pre) {
                                    aux = "\"../datasets/" + methodName + "." +
                                            problemName + "/" + result + (cont + 1) + "e" + k + ".txt\" ";

                                    fichero += aux + " ";
                                    temp.add(aux);
                                } else {
                                    aux = "\"../results/" + methodName + "." +
                                            problemName + "/" + result + cont + "e" + k + ".txt\" ";

                                    fichero += aux + " ";
                                    temp.add(aux);
                                }
                            } else {
                                if (pre) {
                                    aux = "\"../datasets/" + methodName + "." +
                                            problemName + "/" + result + (cont + 1) + "s" + j + "e" + k + ".txt\" ";

                                    fichero += aux + " ";
                                    temp.add(aux);
                                } else {
                                    aux = "\"../results/" + methodName + "." +
                                            problemName + "/" + result + cont + "s" + j + "e" + k + ".txt\" ";

                                    fichero += aux + " ";
                                    temp.add(aux);
                                }

                            }
                        } else //Button Teaching pressed
                        {
                            if (!allowsSeed) {
                                fichero += "\"./experiment/results/" + methodName + "/" + problemName +
                                        "/" + result + cont + "e" + k + ".txt\" ";
                                temp.add(new String("./experiment/results/" + methodName +
                                        "/" +
                                        problemName + "/" + result +
                                        cont + "e" + k + ".txt"));
                            } else {
                                fichero += "\"./experiment/results/" + methodName + "/" + problemName +
                                        "/" + result + cont + "s" + j + "e" + k + ".txt\" ";
                                temp.add(new String("./experiment/results/" + methodName + "/" +
                                        problemName + "/" + result +
                                        cont + "s" + j + "e" + k + ".txt"));
                            }
                        }
                    /***************************************************************
                     ***************  EDUCATIONAL KEEL  ****************************
                     **************************************************************/
                    }
                    additional_outputs.add(temp);
                }

                fichero += "\n\n";

                if (allowsSeed) {
                    fichero += "seed = " + seeds.elementAt(j) + "\n";
                }

                for (k = 0; k < descriptions.size(); k++) {
                    fichero += descriptions.elementAt(k) + " = " + value.elementAt(k) +
                            "\n";
                }


                try {
                    String cfg;
                    if (!allowsSeed) {
                        nombre = (new File(path)).getCanonicalPath() + File.separator +
                                baseName + cont + ".txt";

                        cfg = "/" + methodName + "/" + problemName + "/" +
                                baseName + cont + ".txt";
                    } else {

                        nombre = (new File(path)).getCanonicalPath() + File.separator +
                                baseName + cont + "s" + j + ".txt";

                        cfg = "/" + methodName + "/" + problemName + "/" +
                                baseName + cont + "s" + j + ".txt";

                    }

                    Files.writeFile(nombre, fichero);
                    configs.addElement(cfg);

                } catch (IOException e) {
                    System.err.println(e + "IO error not specified");
                }
            }
            cont++;
        }
    }

    /**
     * Write a configuration script for the test, employing its parameters
     * @param path Path of the method
     * @param baseName Baseline name of the config files
     * @param methodName Name of the method
     * @param problemName Name of the problem
     * @param set Set of input files
     * @param result Baseline name of result files
     * @param pre True if this is a preprocess method
     * @param fullName List of algorithms tested
     * @param relationBBDD Names of the database employed
     */
    public void writeTestScripts(String path, String baseName,
            String methodName,
            String problemName, Vector set,
            String result,
            boolean pre, String fullName, String relationBBDD) {


        // Only for tests

        String fichero, nombre;
        int i, j, k, cont = 0;
        File f;

        try {
            // Check that script doesn't exist
            nombre = (new File(path)).getCanonicalPath() + File.separator +
                    baseName + cont + "s0.txt";
            f = new File(nombre);
            while (f.exists()) {
                cont++;
                nombre = (new File(path)).getCanonicalPath() + File.separator +
                        baseName + cont + "s0.txt";
                f = new File(nombre);
            }
        } catch (IOException e) {
            System.err.println(e + "IO error not specified");
        }

        fichero = "";
        for (j = 0; j < n_exe; j++) {
            fichero = "algorithm = " + algorithmType + "\n";
            fichero += "inputData = ";


            // Changed: LSR 070205
            // inputData: first results from algorithm 1
            //
            // Vector temp = (Vector) (set.elementAt(0));
            // for (int s = 0; s < temp.size(); s++) {
            //  fichero += "\"" + temp.elementAt(s) + "\" ";
            // }

            // then results from algorithm 2

            // temp = (Vector) (set.elementAt(1));
            // for (int s = 0; s < temp.size(); s++) {
            //   fichero += "\"" + temp.elementAt(s) + "\" ";
            // }

            Vector temp;
            for (k = 0; k < set.size(); k++) {
                temp = (Vector) (set.elementAt(k));
                for (int s = 0; s < temp.size(); s++) {
                    fichero += "\"" + temp.elementAt(s) + "\" ";
                }

            }

            StringTokenizer namesDataset = new StringTokenizer((String) ((Vector) (set.elementAt(0))).elementAt(0), "/");
            List names = new ArrayList();

            while (namesDataset.hasMoreElements()) {
                names.add(namesDataset.nextToken());
            }

            String nameDataset = (String) names.get(names.size() - 2);

            // Name of the algorithms to apply
            List namesAlgorithms = new ArrayList();
            int inicio = fullName.indexOf("vs");
            String aux;
            if (inicio == -1) {
                inicio = fullName.length();
                namesAlgorithms.add(fullName.substring(3, inicio));
            } else {
                aux = fullName.substring(inicio + 2, fullName.length());
                namesAlgorithms.add(fullName.substring(3, inicio));

                while (aux.indexOf("vs") != -1) {
                    inicio = aux.indexOf("vs");
                    namesAlgorithms.add(aux.substring(0, inicio));
                    aux = aux.substring(inicio + 2, aux.length());
                }

                namesAlgorithms.add(aux);

            }

            // Outfile names
            fichero += "\n";

            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            String cadRutaParcial = "";
            if (Frame.buttonPressed == 0) //Button Experiments pressed
            {
                cadRutaParcial = "\"../results/";
            } else //Button Teaching pressed
            {
                cadRutaParcial = "\"./experiment/results/";
            }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            if (methodName.equals("Vis-Clas-Tabular") || methodName.equals("Vis-Imb-Tabular")) {

                fichero += "outputDataTabular = ";
                //  if(ProcessConfig.tableType1.equalsIgnoreCase("YES"))
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + relationBBDD + "_" + "ByFoldByClassifier_" + "s" + j + ".stat\" ";
                //  if(ProcessConfig.tableType2.equalsIgnoreCase("YES"))
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + relationBBDD + "_" + "ByFoldByClassifierByClass_" + "s" + j + ".stat\" ";
                //  if(ProcessConfig.tableType3.equalsIgnoreCase("YES"))
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + "Summary_Test_" + "s" + j + ".stat\" ";
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + "Summary_Train_" + "s" + j + ".stat\" ";
                for (int name = 0; name < namesAlgorithms.size(); name++) {
                    fichero += cadRutaParcial + methodName + "/" +
                            problemName + "/" + relationBBDD + "_" + namesAlgorithms.get(name) + "_ConfussionMatrix_" + "s" + j + ".stat\" ";
                }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            } else if (methodName.equals("Vis-Regr-Tabular")) {
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                fichero += "outputDataTabular = ";
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + relationBBDD + "_" + "ByFoldByAlgorithm_" + "s" + j + ".stat\" ";
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + "Summary_Test_" + "s" + j + ".stat\" ";
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + "Summary_Train_" + "s" + j + ".stat\" ";
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            } else if (methodName.equalsIgnoreCase("Vis-Clas-General") || methodName.equalsIgnoreCase("Vis-Regr-General") || methodName.equalsIgnoreCase("Vis-Imb-General")) {
                fichero += "outputDataTabular = ";
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + result + cont + "s" + j + ".stat\" ";

                fichero += cadRutaParcial + methodName + "/" +
                        problemName + "/" + "pvalueMatrix" + cont + "s" + j + ".stat\" ";

                String cadRutaParcial2 = "";
                if (Frame.buttonPressed == 0) //Button Experiments pressed
                {
                    cadRutaParcial2 = "../results/";
                } else //Button Teaching pressed
                {
                    cadRutaParcial2 = "./experiment/results/";
                }

                outputs_tra.add(new String(cadRutaParcial2 + methodName + "/" +
                        problemName + "/" + result + cont + "s" +
                        j +
                        ".stat"));
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            } else {
                /***************************************************************
                 ***************  EDUCATIONAL KEEL  ****************************
                 **************************************************************/
                if (Frame.buttonPressed == 0) //Button Experiments pressed
                {
                    if (nOutputs == 0) {
                        fichero += "outputData = \"../results/" + methodName + "/" +
                                problemName + "/" + result + cont + "s" + j + ".stat\" ";

                        outputs_tra.add(new String("../results/" + methodName + "/" +
                                problemName + "/" + result + cont + "s" +
                                j +
                                ".stat"));
                    } else {
                        String auxString = "outputData = ";
                        for (int counter = 0; counter < nOutputs; counter++) {
                            auxString += "\"../results/" + methodName + "/" +
                                    problemName + "/" + result + cont + "s" + j + "file" + counter + ".stat\" ";

                            outputs_tra.add(new String("../results/" + methodName + "/" +
                                    problemName + "/" + result + cont + "s" +
                                    j + "file" + counter + ".stat"));
                        }
                        fichero += auxString;
                    }
                } else //Button Teaching pressed
                {
                    fichero += "outputData = \"./experiment/results/" + methodName + "/" +
                            problemName + "/" + result + cont + "s" + j + ".stat\" ";

                    outputs_tra.add(new String("./experiment/results/" + methodName + "/" +
                            problemName + "/" + result + cont + "s" +
                            j +
                            ".stat"));
                }
            /***************************************************************
             ***************  EDUCATIONAL KEEL  ****************************
             **************************************************************/
            }

            fichero += "\n\n";

            if (allowsSeed) {
                fichero += "seed = " + seeds.elementAt(j) + "\n";
            }
            for (k = 0; k < descriptions.size(); k++) {
                fichero += descriptions.elementAt(k) + " = " + value.elementAt(k) +
                        "\n";
            }

            try {
                nombre = (new File(path)).getCanonicalPath() + File.separator +
                        baseName + cont + "s" + j + ".txt";
                Files.writeFile(nombre, fichero);
                String cfg = "/" + methodName + "/" + problemName + "/" +
                        baseName + cont + "s" + j + ".txt";
                configs.addElement(cfg);
            } catch (IOException e) {
                System.err.println(e + "IO error not specified");
            }
        }
        cont++;

    }
}

