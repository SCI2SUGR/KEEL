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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner;



import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;

/**
 * <p>Title: myDataset</p>
 *
 * <p>Description: It contains the methods to read a Classification/Regression Dataset</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */
public class myDataset {

    private double[][] X = null;
    private boolean[][] missing = null;
    private int[] C = null;
    private double[] emaximo;
    private double[] eminimo;
    private double smaximo;
    private double sminimo;

    private int ndatos; // Number of examples
    private int nvariables; // Numer of variables
    private int nentradas; // Number of inputs
    private int nclases; // Number of classes

    final static boolean debug = false;

    /** The whole instance set. */
    public InstanceSet IS;

    private int[] comunes;

    /**
   * Outputs an array of examples with their corresponding attribute values.
   * @return double[][] an array of examples with their corresponding attribute values
   */
    public double[][] getX() {
        return X;
    }

    /**
   * Returns the output of the data-set as integer values
   * @return int[] an array of integer values corresponding to the output values of the dataset
   */
    public int[] getC() {
        int[] retorno = new int[C.length];
        for (int i = 0; i < C.length; i++) {
            retorno[i] = C[i];
        }
        return retorno;
    }

    /**
   * It returns an array with the maximum values of the attributes
   * @return double[] an array with the maximum values of the attributes
   */
    public double[] getemaximo() {
        return emaximo;
    }

    /**
   * It returns an array with the minimum values of the attributes
   * @return double[] an array with the minimum values of the attributes
   */
    public double[] geteminimo() {
        return eminimo;
    }

    /**
   * It gets the size of the data-set
   * @return int the number of examples in the data-set
   */
    public int getndatos() {
        return ndatos;
    }

    /**
   * It gets the number of variables of the data-set (including the output)
   * @return int the number of variables of the data-set (including the output)
   */
    public int getnvariables() {
        return nvariables;
    }

    /**
   * It gets the number of input attributes of the data-set
   * @return int the number of input attributes of the data-set
   */
    public int getnentradas() {
        return nentradas;
    }

    /**
   * It gets the number of output attributes of the data-set (for example number of classes in classification)
   * @return int the number of different output values of the data-set
   */
    public int getnclases() {
        return nclases;
    }

    /**
   * This function checks if the attribute value is missing
   * @param i int Example id
   * @param j int Variable id
   * @return boolean True is the value is missing, else it returns false
   */
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }

    /**
     * Constructor. Create a new instance set.
     */
    public myDataset() {
        IS = new InstanceSet(); // Init a new set of instances
    }

    /**
     * It reads the whole input data-set and it stores each example and its associated output value in
     * local arrays to ease their use.
     * @param datasetFile String name of the file containing the dataset
     * @param train boolean It must have the value "true" if we are reading the training data-set
     * @throws IOException If there ocurs any problem with the reading of the data-set
     */
    public void readClassificationSet(String datasetFile, boolean train) throws
            IOException {
        try {
            // Load in memory a dataset that contains a classification problem
            IS.readSet(datasetFile, train);
            ndatos = IS.getNumInstances();
            nentradas = Attributes.getInputNumAttributes();
            nvariables = nentradas + Attributes.getOutputNumAttributes();

            // Check that there is only one output variable
            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
                System.exit(1);
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1);
            }

            // Initialice and fill our own tables
            X = new double[ndatos][nentradas];
            missing = new boolean[ndatos][nentradas];
            C = new int[ndatos];

            // Maximum and minimum of inputs
            emaximo = new double[nentradas];
            eminimo = new double[nentradas];

            // All values are casted into double/integer
            nclases = 0;
            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nentradas; j++) {
                    X[i][j] = IS.getInputNumericValue(i, j); //inst.getInputRealValues(j);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > emaximo[j] || i == 0) {
                        emaximo[j] = X[i][j];
                    }
                    if (X[i][j] < eminimo[j] || i == 0) {
                        eminimo[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    C[i] = 0;
                } else {
                    C[i] = (int) IS.getOutputNumericValue(i, 0); //(int)inst.getOutputRealValues(i);
                }
                if (C[i] > nclases) {
                    nclases = C[i];
                }
            }
            nclases++;
            System.out.println("Number of classes=" + nclases);

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }

    /**
     * It copies the header of the dataset
     * @return String A string containing all the data-set information
     */
    public String copyHeader() {
        // Header of the output file
        String p = new String("");
        p = "@relation " + Attributes.getRelationName() + "\n";
        p += Attributes.getInputAttributesHeader();
        p += Attributes.getOutputAttributesHeader();
        p += Attributes.getInputHeader() + "\n";
        p += Attributes.getOutputHeader() + "\n";
        p += "@data\n";
        return p;
    }

    /**
     * Returns the type of each input attribute (NOMINAL = 0 OR NUMERIC = 1)
     * @return int[] Vector with binary values, indicating if the attributes are nominal or numeric.
     */
    public int[] tiposVar() {
        int[] tipos = new int[this.nentradas];
        for (int i = 0; i < this.nentradas; i++) {
            tipos[i] = 1;
            if (Attributes.getAttribute(i).getType() == Attribute.NOMINAL) {
                tipos[i] = 0;
            }
        }
        return tipos;
    }

    /**
     * Computes the most frequent values for every attribute.
     */
    public void calculaMasComunes() {
        comunes = new int[nentradas];
        int[] aux = new int[ndatos];
        for (int i = 0; i < nentradas; i++) {
            for (int j = 0; j < ndatos; j++) {
                if (this.isMissing(j, i)) {
                    aux[j] = -1;
                } else {
                    aux[j] = (int) X[j][i];
                }
            }
            Arrays.sort(aux);
            int mascomun = aux[0];
            int contador = 1, j;
            for (j = 1; (aux[j] == mascomun) && (j < ndatos - 1); j++, contador++) {
                ;
            }
            int contador2 = 1;
            int mascomun2 = aux[j];
            if (j + 1 < ndatos) {
                for (j = j + 1; j < ndatos; j++) {
                    if (aux[j] == mascomun2) {
                        contador2++;
                    } else {
                        mascomun2 = aux[j];
                        if (contador2 > contador) {
                            contador = contador2;
                            mascomun = mascomun2;
                            contador2 = 1;
                        }
                    }
                }
            }
            comunes[i] = mascomun;
        }
    }

    /**
     * Returns the most frequent value of the ith attribute.
     * @param i int Number of the attribute
     * @return int The most frequent value of that attribute.
     */
    public int masComun(int i) {
        return comunes[i];
    }

    /**
     * Returns the greater value of the attribute given.
     * @param i int attribute's id.
     * @return double the greater value of the attribute given.
     */
    private double dameRangoSup(int i) {
        return Attributes.getAttribute(i).getMaxAttribute();
    }

    /**
     * Returns the lesser value of the attribute given.
     * @param i int attribute's id.
     * @return double the lesser value of the attribute given.
     */
    private double dameRangoInf(int i) {
        return Attributes.getAttribute(i).getMinAttribute();
    }

    /**
     * Returns the minimum and maximum values of every attributes as a matrix.
     * The matrix has a size of number_of_attributes x 2 ([nAttributes][2]).
     * The minimum value is located at the first position of each array and the maximum, at the second.
     * @return Matrix which stores the minimum and maximum values of every attributes.
     */
    public double[][] dameRangos() {
        double rangos[][] = new double[this.getnentradas()][2];
        for (int i = 0; i < this.getnentradas(); i++) {
            rangos[i][0] = dameRangoInf(i);
            rangos[i][1] = dameRangoSup(i);
        }
        return rangos;
    }

    /**
     * Returns the type of each input attribute
     * @return int[] the type of each input attribute
     */
    public int[] dameTipos() {
        int tipos[] = new int[this.getnentradas()];
        for (int i = 0; i < this.getnentradas(); i++) {
            tipos[i] = Attributes.getAttribute(i).getType() + 2; //trapicheo para que 2 sea NOMINAL
        }
        return tipos;
    }

    /**
     * It returns the name of every input attributes.
     * @return String [] Array of strings with the name of every input attribute's names.
     */
    public String[] dameNombres() {
        String[] salida = new String[nvariables];
        for (int i = 0; i < nentradas; i++) {
            salida[i] = Attributes.getInputAttribute(i).getName();
        }
        salida[nentradas] = Attributes.getOutputAttribute(0).getName();
        return salida;
    }

    /**
     * It returns the name of every output values (possible classes).
     * @return String [] Array of strings with the name of every output attribute's names.
     */
    public String[] dameClases() {
        String[] salida = new String[nclases];
        Attribute at = Attributes.getOutputAttribute(0);
        if (at.getType() == at.NOMINAL) {
            for (int i = 0; i < nclases; i++) {
                salida[i] = at.getNominalValue(i);
            }
        } else {
            salida = null; //luego guardaré el valor de las clases numéricas
        }
        return salida;
    }

    /**
     * Returns the values as String for every attribute.
     * @return String[][] array of String, the values for every attribute. 
     */
    public String[][] dameValores() {
        String[][] salida = new String[nentradas][];
        for (int i = 0; i < nentradas; i++) {
            Attribute at = Attributes.getInputAttribute(i);
            if (at.getType() == at.NOMINAL) {
                salida[i] = new String[at.getNumNominalValues()];
                for (int j = 0; j < at.getNumNominalValues(); j++) {
                    salida[i][j] = at.getNominalValue(j);
                }
            } else if (at.getType() == at.INTEGER) {
                salida[i] = new String[(int) at.getMaxAttribute() -
                            (int) at.getMinAttribute() + 1];
                for (int j = 0, k = (int) at.getMinAttribute();
                                    j <
                                    (int) at.getMaxAttribute() - (int) at.getMinAttribute() +
                                    1; j++,
                                    k++) {
                    salida[i][j] = "" + k;
                }
            }
        }
        return salida;
    }

    /**
     * It returns an array with the minimum values of the attributes
     * @return double[] an array with the minimum values of the attributes
     */
    public double[] valoresMin() {
        double[] min = new double[nentradas];
        for (int i = 0; i < nentradas; i++) {
            Attribute at = Attributes.getInputAttribute(i);
            if (at.getType() == at.INTEGER) {
                min[i] = at.getMinAttribute();
            } else {
                min[i] = 0;
            }
        }
        return min;
    }

    /**
     * It checks if the data-set has any real value
     * @return boolean True if it has some real values, else false.
     */
    public boolean hayAtributosContinuos() {
        return Attributes.hasRealAttributes();
    }

}

