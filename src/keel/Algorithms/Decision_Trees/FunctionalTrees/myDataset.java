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

package keel.Algorithms.Decision_Trees.FunctionalTrees;

import java.util.ArrayList;
import java.util.StringTokenizer;

import keel.Algorithms.Preprocess.Basic.CheckException;
import keel.Dataset.Attribute;
import keel.Dataset.Attributes;
import keel.Dataset.Instance;
import keel.Dataset.InstanceSet;

/**
 * This class contains the most useful information about a dataset, and provides a set of functions to
 * manage this information easily.
 *
 * @author Written by Victoria Lopez Morales (University of Granada) 15/05/2009
 * @version 0.1
 * @since JDK1.6
 */
public class myDataset {
    /**
     * Representative name of the dataset
     */
    private String name;

    /**
     * The number of attributes that the dataset has
     */
    private int numAtr;
    /**
     * The number of instances that the dataset has
     */
    private int numIns;

    /**
     * All the information about the attributes that the dataset uses
     */
    private ArrayList<myAttribute> attributes;

    /**
     * All the information about the output attribute of the dataset
     */
    private myAttribute outputAttribute;

    /**
     * The number of different classes that there are in the dataset
     */
    private int numClasses;
    /**
     * The number of instances of each class that there are in the dataset
     */
    private int nInstances[];

    /**
     * Values of the attributes for each instance in the dataset
     */
    private double data[][];
    /**
     * Class of each instance in the dataset
     */
    private int output[];

    /**
     * Kind of the dataset we are considering. kind = 1 if we are considering a train dataset; kind = 2 if we are considering a reference dataset; and kind = 3 if we are considering a test dataset.
     */
    private int kind;

    /** 
     * Creates a dataset by reading the .dat file that contains the information of it,
     * and gives values to every field of the class
     *
     * @param nameFile  The name of the file that is going to be read
     * @param newkind   The kind of dataset to determine if the file is for training, reference or test
     * @exception keel.Algorithms.Preprocess.Basic.CheckException Thrown from the CheckException class
     */     
    public myDataset(String nameFile, int newkind) throws CheckException {
        Attribute at;
        String nameat;
        double min, max;
        ArrayList<String> nomValues;
        StringTokenizer tokens;
        Instance temp;
        boolean[] nulls;
        InstanceSet IS;

        kind = newkind;
        IS = new InstanceSet();

        // Read of data file
        try {
            if (newkind == 3) {
                IS.readSet(nameFile, false);
            } else {
                IS.readSet(nameFile, true);
            }
        } catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        // Check if dataset corresponding with a classification problem
        if (Attributes.getOutputNumAttributes() < 1) {
            throw new CheckException ("This dataset doesn't have any outputs, so it doesn't belong to a classification problem");
        } else if (Attributes.getOutputNumAttributes() > 1) {
            throw new CheckException("This dataset has more than one output");
        }

        if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
            throw new CheckException("This dataset has an output attribute with float values, so it doesn't belong to a classification problem");
        }

        // Get the name, number of attributes and number of instances of the dataset
        name = new String (Attributes.getRelationName());
        numAtr = Attributes.getInputNumAttributes();
        numIns = IS.getNumInstances();

        // Create vectors to hold information
        attributes = new ArrayList<myAttribute>(numAtr);

        // Store attribute inputs
        for (int j = 0; j < numAtr; j++) {
            at = Attributes.getInputAttribute(j);
            nameat = new String (at.getName());

            // Check if it is real or integer
            if ((at.getType() == 1) || (at.getType() == 2)) {
                // Create continuous attribute
                min = (double) at.getMinAttribute();
                max = (double) at.getMaxAttribute();
                attributes.add(new myAttribute(nameat, at.getType(), min, max, true));
            } else {
                // Create nominal attribute
                myAttribute aux;
                int numNominal = at.getNumNominalValues();

                nomValues = new ArrayList<String>(numNominal);
                for (int k = 0; k < numNominal; k++) {
                    nomValues.add(at.getNominalValue(k));
                }

                aux = new myAttribute(nameat, 3, true);
                aux.setValues(nomValues);
                attributes.add(aux);
            }

        } // for

        // Copy the data
        tokens = new StringTokenizer(IS.getHeader(), " \n\r");
        tokens.nextToken();
        tokens.nextToken();
        
        // Get space for the instances
        data = new double[IS.getNumInstances()][numAtr];
        output = new int[IS.getNumInstances()];

        for (int i = 0; i < IS.getNumInstances(); i++) {
            // Store the values of the instances in the corresponding data structures        	
            temp = IS.getInstance(i);
            data[i] = temp.getAllInputValues();
            output[i] = (int) temp.getOutputRealValues(0);
            nulls = temp.getInputMissingValues();

            // Clean missing values
            for (int j = 0; j < nulls.length; j++) {
                if (nulls[j]) {
                    data[i][j] = 0.0;
                }
            }
        }

        // Store output attributes
        at = Attributes.getOutputAttribute(0);
        nameat = new String (at.getName());

        // Check if it is real
        if ((at.getType() == 1) || (at.getType() == 2)) {
            // Create continuous attribute
            min = (double) at.getMinAttribute();
            max = (double) at.getMaxAttribute();
            outputAttribute = new myAttribute(nameat, at.getType(), min, max, false);
        } else { 
            // Create nominal attribute
            myAttribute aux;
            int numNominal = at.getNumNominalValues();

            nomValues = new ArrayList<String>(numNominal);
            for (int k = 0; k < numNominal; k++) {
                nomValues.add(at.getNominalValue(k));
            }

            aux = new myAttribute(nameat, 3, false);
            aux.setValues(nomValues);
            outputAttribute = aux;
        }

        // Get the number of classes
        numClasses = Attributes.getOutputAttribute(0).getNumNominalValues();

        // And the number of instances on each class
        nInstances = new int[numClasses];
        for (int i = 0; i < numClasses; i++) {
            nInstances[i] = 0;
        }
        for (int i = 0; i < output.length; i++) {
            nInstances[output[i]]++;
        }
        
        IS.setAttributesAsNonStatic();
        
        if (kind == 3) {
            Attributes.clearAll();
        }
    }

    /** 
     * Creates a dataset from another existing dataset
     *
     * @param dataset  Original dataset from which we are going to create a copy
     */   
    public myDataset(myDataset dataset) {
        myAttribute aux;

        // Copy each data field to the new dataset
        kind = dataset.kind;
        name = dataset.name;
        numAtr = dataset.numAtr;
        numIns = dataset.numIns;
        numClasses = dataset.numClasses;
        outputAttribute = new myAttribute(dataset.outputAttribute);
        attributes = new ArrayList<myAttribute>();
        for (int i = 0; i < dataset.attributes.size(); i++) {
            aux = new myAttribute((myAttribute) dataset.attributes.get(i));
            attributes.add((myAttribute) aux);
        }
        nInstances = new int[numClasses];
        System.arraycopy(dataset.nInstances, 0, nInstances, 0, dataset.nInstances.length);
        output = new int[numIns];
        System.arraycopy(dataset.output, 0, output, 0, dataset.output.length);
        data = new double[numIns][numAtr];
        for (int i=0; i<numIns; i++) {
            for (int j=0; j<numAtr; j++) {
                data[i][j] = dataset.data[i][j];
            }
        }
    }
    
    /** 
     * Check if a dataset is the same dataset as another object
     *
     * @param obj  Object that is checked to see if it is the same dataset
     * @return true if the datasets are the same, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */ 
    public boolean equals (Object obj) {
        // First we check if the reference is the same
        if (this == obj)
            return true;
        
        // Then we check if the object exists and is from the class myDataset
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        
        // object must be myDataset at this point
        myDataset test = (myDataset)obj;
        
        // We check if the values for nInstances are the same
        for (int i=0; i<numClasses; i++) {
            if (nInstances[i] != test.nInstances[i])
                return false;
        }
        
        // We check if the values for output are the same
        for (int i=0; i<numIns; i++) {
            if (output[i] != test.output[i])
                return false;
        }
        
        // We check if the values for data are the same
        for (int i=0; i<numIns; i++) {
            for (int j=0; j<numAtr; j++) {
                if (data[i][j] != test.data[i][j]) {
                    return false;
                }
            }
        }

        // We check the other class attributes of the dataset
        return ((numAtr == test.numAtr) &&
                (numIns == test.numIns) &&
                (numClasses == test.numClasses) &&
                (kind == test.kind) &&
                (name == test.name || (name != null && name.equals(test.name))) &&
                (attributes == test.attributes || (attributes != null && attributes.equals(test.attributes))) &&
                (outputAttribute == test.outputAttribute || (outputAttribute != null && outputAttribute.equals(test.outputAttribute))));
    }

    /** 
     * Hash-code function for the class that is used when object is inserted in a structure like a hashtable
     *
     * @return the hash code obtained
     * @see java.lang.Object#hashCode()
     */ 
    public int hashCode() {
        int hash = 7;

        hash = 31 * hash + (null == name ? 0 : name.hashCode());
        hash = 31 * hash + numAtr;
        hash = 31 * hash + numIns;
        hash = 31 * hash + (null == outputAttribute ? 0 : outputAttribute.hashCode());
        hash = 31 * hash + numClasses;
        hash = 31 * hash + kind;
        return hash;
    }

    /** 
     * Overriden function that converts the class to a string
     *
     * @return the string representation of the class
     * @see java.lang.Object#toString()
     */ 
    public String toString() {
        String aux;
        
        // First we get the dataset name
        aux = new String(name);
        aux += "\n";
        
        // Then, we get the attributes in the dataset
        for (int i=0; i<attributes.size(); i++) {
            aux = aux + (myAttribute)attributes.get(i) + "\n" ;
        }
        // and the output attribute
        aux = aux + outputAttribute + "\n";
        
        // Then, we print the values of the attributes and the output class for that data
        for (int i=0; i<numIns; i++) {
            for (int j=0; j<numAtr; j++) {
                aux = aux + data[i][j] + " ";
            }
            aux = aux + output[i] + "\n";
        }
        
        // Finally, we see the kind of dataset we're dealing with
        switch (kind) {
            case 1: aux = aux + "Training dataset\n";
                break;
            case 2: aux = aux + "Reference dataset\n";
                break;
            case 3: aux = aux + "Test dataset\n";
                break;
            default: System.err.println("Error: This dataset isn't correctly specified\n");
                System.exit(1);
                break;
        }
        
        return aux;
    }
    
    /** 
     * Gets the name of the dataset
     *
     * @return the name of the dataset
     */
    public String getName() {
        return name;
    }

    /**
     * Replaces the name of the dataset with another new name
     * 
     * @param name  New name for the dataset 
     */
    public void setName(String name) {
        this.name = name;
    }

    /** 
     * Gets the number of attributes that the dataset has
     *
     * @return the number of attributes that the dataset has
     */
    public int getNumAtr() {
        return numAtr;
    }

    /**
     * Replaces the number of attributes in this dataset with a new number of attributes
     * 
     * @param numAtr  New number of attributes for this dataset  
     */
    public void setNumAtr(int numAtr) {
        this.numAtr = numAtr;
    }

    /** 
     * Gets the number of instances that the dataset has
     *
     * @return the number of instances that the dataset has
     */
    public int getNumIns() {
        return numIns;
    }

    /**
     * Replaces the number of instances in this dataset with a new number of instances
     * 
     * @param numIns  New number of instances for this dataset   
     */
    public void setNumIns(int numIns) {
        this.numIns = numIns;
    }

    /** 
     * Gets all the information about the attributes that the dataset uses
     *
     * @return all the information about the attributes that the dataset uses
     */
    public ArrayList<myAttribute> getAttributes() {
        return attributes;
    }

    /**
     * Replaces all the information about the attributes that the dataset uses with new information about the attributes
     * 
     * @param attributes  New information about the attributes that the dataset uses  
     */
    public void setAttributes(ArrayList<myAttribute> attributes) {
        this.attributes = attributes;
    }
    
    /** 
     * Gets all the information about ith the attribute that the dataset uses
     *
     * @return all the information about the ith attribute that the dataset uses
     */
    public myAttribute getAttributeI (int i) {
        myAttribute aux;
        
        aux = (myAttribute)attributes.get(i);
        
        return aux;
    }

    /**
     * Replaces the information about the ith attribute that the dataset uses with new information about that attribute
     * 
     * @param i Position of the attribute that is going to be replaced
     * @param att  New information about the attribute that the dataset uses  
     */
    public void setAttributeI (int i, myAttribute att) {
        attributes.set(i, att);
    }

    /** 
     * Gets all the information about the output attribute of the dataset
     *
     * @return all the information about the output attribute of the dataset
     */
    public myAttribute getOutputAttribute() {
        return outputAttribute;
    }

    /**
     * Replaces the information about the output attribute with new information about that attribute
     * 
     * @param outputAttribute  Attribute to be stored like the output attribute  
     */
    public void setOutputAttribute(myAttribute outputAttribute) {
        this.outputAttribute = outputAttribute;
    }

    /** 
     * Gets the number of different classes that there are in the dataset
     *
     * @return the number of different classes that there are in the dataset
     */
    public int getNumClasses() {
        return numClasses;
    }

    /**
     * Replaces the number of classes in this dataset with a new number of classes
     * 
     * @param numClasses  New number of classes for this dataset   
     */
    public void setNumClasses(int numClasses) {
        this.numClasses = numClasses;
    }

    /** 
     * Gets the number of instances of each class that there are in the dataset
     *
     * @return the number of instances of each class that there are in the dataset
     */
    public int getNInstancesI (int i) {
        return nInstances[i];
    }

    /**
     * Replaces the number of instances of a class that there are in the dataset with a another number of instances for that class
     * 
     * @param i Class which number of instances is going to be modified
     * @param instances  New number of instances for the ith class  
     */
    public void setNInstancesI(int i, int instances) {
        nInstances[i] = instances;
    }

    /** 
     * Gets the value of the jth attribute for the ith instance in the dataset
     *
     * @return the value of the jth attribute for the ith instance in the dataset
     */
    public double getDataI (int i, int j) {
        return data[i][j];
    }

    /** 
     * Gets the value of the ith instance in the dataset
     *
     * @return the value of the ith instance in the dataset
     */
    public double [] getDataItem (int i) {
        return data[i];
    }

    /**
     * Replaces the value of the ith instance at the jth attribute in this dataset with the specified value
     * 
     * @param i Position of the instance which value is going to be replaced
     * @param j Attribute which value is going to be replaced
     * @param data  Value to be stored at the specified instance and attribute  
     */
    public void setDataI (int i, int j, double data) {
        this.data[i][j] = data;
    }

    /** 
     * Gets the class of each instance in the dataset
     *
     * @return the class of each instance in the dataset
     */
    public int getOutputI (int i) {
        return output[i];
    }

    /**
     * Replaces the output attribute value at the specified instance in this dataset with the specified value
     * 
     * @param i Index of the instance output value to replace
     * @param output  Value to be stored at the specified instance  
     */
    public void setOutputI (int i, int output) {
        this.output[i] = output;
    }

    /** 
     * Gets the kind of the dataset we are considering (training, reference, test)
     *
     * @return the name of the dataset
     */
    public int getKind() {
        return kind;
    }

    /**
     * Changes the kind of dataset to a new kind
     * 
     * @param kind  New kind for the dataset  
     */
    public void setKind(int kind) {
        this.kind = kind;
    }
}

