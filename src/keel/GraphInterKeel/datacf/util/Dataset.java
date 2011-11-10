/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S·nchez (luciano@uniovi.es)
    J. Alcal·-Fdez (jalcala@decsai.ugr.es)
    S. GarcÌa (sglopez@ujaen.es)
    A. Fern·ndez (alberto.fernandez@ujaen.es)
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

package keel.GraphInterKeel.datacf.util;

import java.util.Vector;
import keel.Dataset.*;

/**
 * <p>
 * @author Jesus Alcala Fernandez 24-6-2004
 * @author Modified by Pedro Antonio Guti√©rrez and Juan Carlos Fern√°ndez (University of C√≥rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
public class Dataset {

    /**
     * Class for representing the information contained in a Dataset
     */

    /** Example number */
    private int nData;

    /** Variable number */
    private int nVariables;

    /** Input number */
    protected int nInputs;

    /** Output number */
    protected int nOutputs;

    /** Relation name */
    private String relation;

    /** Data matrix */
    private Vector dataVector;

    /** Column names */
    private Vector attributes;

    /** Data types */
    private Vector types;

    /** Type element names */
    private Vector ranges;

    /** Selected input variables */
    private Vector selInputs;

    /** Selected output variables */
    private Vector selOutputs;

    /** Debuggin flag */
    final static boolean debug = true;

    /**
     * <p>
     * Return the relation name
     * </p>
     * @return relation name
     */
    public String getRelacion() {
        return (relation);
    }

    /**
     * <p>
     * Returns a vector of vectors in which each vector is an example/pattern
     * Note: each value is stored as a String (must be converted)
     * </p>
     * @return Vector of vector for patterns
     */
    public Vector getDataVector() {
        return (dataVector);
    }

    /**
     * <p>
     * Returns a vector with variable names
     * </p>
     * @return Vector with variable names
     */
    public Vector getAttributes() {
        return (attributes);
    }

    /**
     * <p>
     * Return a vector with variable types (integer, real, nominal)
     * </p>
     * @return Vector with variable types (integer, real, nominal)
     */
    public Vector getTypes() {
        return (types);
    }

    /**
     * <p>
     * Return a vector of vectors in which each vector contains the ranges
     * for the variable
     * </p>
     * @return Vector of vectors in which each vector contains the ranges
     * for the variable
     */
    public Vector getRanges() {
        return (ranges);
    }

    /**
     * <p>
     * Return a vector that contains input variables
     * </p>
     * @return Vector that contains input variables
     */
    public Vector getInputs() {
        return (selInputs);
    }

    /**
     * <p>
     * Return a vector that contains output variables
     * </p>
     * @return Vector that contains output variables
     */
    public Vector getOutputs() {
        return (selOutputs);
    }

    /**
     * <p>
     * Return variable number
     * </p>
     *
     * @return Variable number
     */
    public int getNVariables() {
        return (nVariables);
    }

    /**
     * <p>
     * Sets input variable number
     * </p>
     * @param nVariables Input variable number
     */
    public void setNVariables(int nVariables) {
        this.nVariables = nVariables;
    }

    /**
     * <p>
     * Return input variable number
     * </p>
     * @return Input variable number
     */
    public int getNInputs() {
        return (nInputs);
    }

    /**
     * <p>
     * Set input variable number
     * </p>
     * @param nInputs New Input Variable Number
     */
    public void setNentradas(int nInputs) {
        this.nInputs = nInputs;
    }

    /**
     * <p>
     * Return output variable number
     * </p>
     * @return Output variable number
     */
    public int getNOutputs() {
        return (nOutputs);
    }

    /**
     * <p>
     * Set output variable number
     * </p>
     * @param nOutputs New output variable number
     */
    public void setNOutputs(int nOutputs) {
        this.nOutputs = nOutputs;
    }

    /**
     * <p>
     * Return example/patterns number
     * </p>
     * @return Example/patterns number
     */
    public int getNData() {
        return (nData);
    }

    /**
     * <p>
     * Return attribute name at index position
     * </p>
     * @param index Index position
     * @return Attribute name at index position
     */
    public String getAttributeIndex(int index) {
        return ((String) attributes.elementAt(index));
    }

    /**
     * <p>
     * Return attribute type at index position
     * </p>
     * @param index Index position
     * @return Attribute type at index position
     */
    public String getAttributeTypeIndex(int index) {
        return ((String) types.elementAt(index));
    }

    /**
     * <p>
     * Return example/pattern at index position
     * </p>
     * @param index Index position
     * @return Example/pattern at index position
     */
    public Vector getPatternIndex(int index) {
        return ((Vector) dataVector.elementAt(index));
    }

    /**
     * <p>
     * Return data at position (i,j)
     * </p>
     * @param i position i
     * @param j position j
     * @return Data at position (i,j)
     */
    public String getDataIndex(int i, int j) {
        return ((String) ((Vector) dataVector.elementAt(i)).elementAt(j));
    }

    /**
     * <p>
     * Return range of index variable
     * </p>
     * @param index Index position
     * @return Range of index variable
     */
    public Vector getRange(int index) {
        return ((Vector) ranges.elementAt(index));
    }

    /**
     * <p>
     * Return range value at index of var variable
     * </p>
     * @param var Number of int variable
     * @param index Index position
     * @return Range value at index of var variable
     */
    public Integer getRangesInt(int var, int index) {
        return ((Integer) ((Vector) ranges.elementAt(var)).elementAt(index));
    }

    /**
     * <p>
     * Return range value at index of var variable
     * </p>
     * @param var Number of double variable
     * @param index Index position
     * @return Range value at index of var variable
     */
    public Double getRangesReal(int var, int index) {
        return ((Double) ((Vector) ranges.elementAt(var)).elementAt(index));
    }

    /**
     * <p>
     * Returns range value at index of var variable
     * </p>
     * @param var Number of enum variable
     * @param index Index position
     * @return Range value at index of var variable
     */
    public String getRangesEnum(int var, int index) {
        return ((String) ((Vector) ranges.elementAt(var)).elementAt(index));
    }

    /**
     * <p>
     * Return the range of a variable
     * </p>
     * @param variableName Name of the variable
     * @return Range of the variable
     */
    public Vector getRangesVar(String variableName) {
        int i;

        for (i = 0; i < nVariables; i++) {
            if (variableName.equals(attributes.elementAt(i))) {
                return ((Vector) ranges.elementAt(i));
            }
        }
        return (null);
    }

    /**
     * <p>
     * Read a DataSet
     * </p>
     * @param fileName File Name with a dataset
     */
    public Dataset(String fileName) {

        try {
            dataVector = new Vector();
            attributes = new Vector();
            types = new Vector();
            ranges = new Vector();
            selInputs = new Vector();
            selOutputs = new Vector();

            nVariables = nInputs = nOutputs = nData = 0;

            Attributes.clearAll();
            InstanceSet data = new InstanceSet();

            System.out.println("Before reading the dataset");

            data.readSet(fileName, true);

            System.out.println("After reading the dataset");

            data.setAttributesAsNonStatic();

            InstanceAttributes attrib = data.getAttributeDefinitions();


            relation = fileName.replaceAll(".dat", "");

            relation=relation.substring(relation.lastIndexOf("\\")+1, relation.length());
 
            nData = data.getNumInstances();
            nVariables = attrib.getNumAttributes();
            nInputs = attrib.getInputNumAttributes();
            nOutputs = attrib.getOutputNumAttributes();

            for (int i = 0; i < nVariables; i++) {
                keel.Dataset.Attribute a = attrib.getAttribute(i);
                // name
                attributes.addElement(a.getName());

                // type
                int tipo = a.getType();
                if (tipo == keel.Dataset.Attribute.NOMINAL) {
                    types.addElement(new String("nominal"));
                } else if (tipo == keel.Dataset.Attribute.REAL) {
                    types.addElement(new String("real"));
                } else if (tipo == keel.Dataset.Attribute.INTEGER) {
                    types.addElement(new String("integer"));
                }

                // rank
                if (tipo == keel.Dataset.Attribute.NOMINAL) {
                    ranges.addElement(a.getNominalValuesList());
                } else if (tipo == keel.Dataset.Attribute.REAL) {
                    Vector r = new Vector();
                    r.addElement(new Double(a.getMinAttribute()));
                    r.addElement(new Double(a.getMaxAttribute()));
                    ranges.addElement(r);
                } else if (tipo == keel.Dataset.Attribute.INTEGER) {
                    Vector r = new Vector();
                    r.addElement(new Integer((int) a.getMinAttribute()));
                    r.addElement(new Integer((int) a.getMaxAttribute()));
                    ranges.addElement(r);
                }

                // function
                int funcion = a.getDirectionAttribute();
                if (funcion == keel.Dataset.Attribute.INPUT) {
                    selInputs.addElement(a.getName());
                } else if (funcion == keel.Dataset.Attribute.OUTPUT) {
                    selOutputs.addElement(a.getName());
                }
            }
            // data
            for (int i = 0; i < nData; i++) {
                Instance inst = data.getInstance(i);
                Vector fila = new Vector();
                int ent = 0;
                int sal = 0;
                for (int j = 0; j < nVariables; j++) {
                    int funcion = attrib.getAttribute(j).getDirectionAttribute();
                    if (funcion == keel.Dataset.Attribute.INPUT) {
                        int tipo = attrib.getAttribute(j).getType();
                        if (tipo == keel.Dataset.Attribute.NOMINAL) {
                            if (inst.getInputMissingValues(ent) == false) {
                                fila.addElement(inst.getInputNominalValues(ent));
                            } else {
                                fila.addElement(null);
                            }
                        } else if (tipo == keel.Dataset.Attribute.REAL) {
                            if (inst.getInputMissingValues(ent) == false) {
                                fila.addElement(Double.toString(inst.getInputRealValues(ent)));
                            } else {
                                fila.addElement(null);
                            }
                        } else if (tipo == keel.Dataset.Attribute.INTEGER) {
                            if (inst.getInputMissingValues(ent) == false) {
                                fila.addElement(Integer.toString((int) inst.getInputRealValues(ent)));
                            } else {
                                fila.addElement(null);
                            }
                        }
                        ent++;
                    } else if (funcion == keel.Dataset.Attribute.OUTPUT) {
                        int tipo = attrib.getAttribute(j).getType();
                        if (tipo == keel.Dataset.Attribute.NOMINAL) {
                            fila.addElement(inst.getOutputNominalValues(sal));
                        } else if (tipo == keel.Dataset.Attribute.REAL) {
                            fila.addElement(Double.toString(inst.getOutputRealValues(sal)));
                        } else if (tipo == keel.Dataset.Attribute.INTEGER) {
                            fila.addElement(Integer.toString((int) inst.getOutputRealValues(sal)));
                        }
                        sal++;
                    }
                }
                dataVector.addElement(fila);
            }

        } catch (Exception e) {
        }
    }
}

