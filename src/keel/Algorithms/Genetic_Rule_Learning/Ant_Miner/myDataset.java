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

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;

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

    public InstanceSet IS;

    private int[] comunes;

    /**
     * Devuelve los valores de los atributos de entrada
     * @return double[][] Un array con los atributos de entrada
     */
    public double[][] getX() {
        return X;
    }

    /**
     * Devuelve los valores para la salida (clase)
     * @return int[] Un array con los valores de la clase
     */
    public int[] getC() {
        int[] retorno = new int[C.length];
        for (int i = 0; i < C.length; i++) {
            retorno[i] = C[i];
        }
        return retorno;
    }

    /**
     * Devuelve un array con los valores máximos de los atributos de entrada
     * @return double[] idem
     */
    public double[] getemaximo() {
        return emaximo;
    }

    /**
     * Devuelve un array con los valores mínimos de los atributos de entrada
     * @return double[] idem
     */
    public double[] geteminimo() {
        return eminimo;
    }

    /**
     * Devuelve el número de datos
     * @return int el número de ejemplos
     */
    public int getndatos() {
        return ndatos;
    }

    /**
     * Devuelve el número de variables
     * @return int El número de variables (incluyendo entrada y salida)
     */
    public int getnvariables() {
        return nvariables;
    }

    /**
     * Devuelve el número de variables de entrada
     * @return int El total de variables de entrada
     */
    public int getnentradas() {
        return nentradas;
    }

    /**
     * Devuelve el número total de clases
     * @return int el número de clases distintas
     */
    public int getnclases() {
        return nclases;
    }

    /**
     * Comprueba si un atributo está "perdido" o no
     * @param i int Número de ejemplo
     * @param j int Número de atributo
     * @return boolean True si falta, False en otro caso
     */
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }

    /**
     * Constructor. Crea un nuevo conjunto de instancias
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
     * Devuelve los tipos de cada entrada (NOMINAL[0] o NUMERICO[1])
     * @return int[] Un vector que contiene 0 o 1 en funcion de si los atributos son nominales o numericos
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
     * Calcula los valores mas comunes para cada columna o atributo
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
     * Devuelve el valor mas común del atributo i-esimo
     * @param i int Número de atributo
     * @return int Valor mas comnun para esta variable
     */
    public int masComun(int i) {
        return comunes[i];
    }

    /**
     * Devuelve el rango superior del atributo i-esimo
     * @param i int Numero de atributo de entrada
     * @return double Valor maximo que puede tomar el atributo
     */
    private double dameRangoSup(int i) {
        return Attributes.getAttribute(i).getMaxAttribute();
    }

    /**
     * Devuelve el rango inferior del atributo i-esimo
     * @param i int Numero de atributo de entrada
     * @return double Valor minimo que puede tomar el atributo
     */
    private double dameRangoInf(int i) {
        return Attributes.getAttribute(i).getMinAttribute();
    }

    /**
     * Devuelve el rango superior e inferior para cada atributo de la base de datos
     * @return double[][] Un array de dos valores para cada atributo: rango superior, rango inferior
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
     * Devuelve los tipos de cada atributo
     * @return int[] Un array de enteros, cada uno representa un tipo (NOMINAL,NUMERICO)
     */
    public int[] dameTipos() {
        int tipos[] = new int[this.getnentradas()];
        for (int i = 0; i < this.getnentradas(); i++) {
            tipos[i] = Attributes.getAttribute(i).getType() + 2; //trapicheo para que 2 sea NOMINAL
        }
        return tipos;
    }

    /**
     * Devuelve el nombre de las variables del problema
     * @return String[] Un Array con los nombres de las variables del problema
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
     * Devuelve el valor de las clases
     * @return String[] Un array con el valor para las distintas salidas (clases)
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
     * Devuelve el nombre de cada valor para los distintos atributos
     * @return String[][] Una cadena con el valor de cada atributo en formato CADENA (sea nominal o numerico)
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
     * Comprueba si en la base de datos hay alguna entrada de tipo real o contínua
     * @return boolean True si existe alguna entrada continua. False en caso contrario
     */
    public boolean hayAtributosContinuos() {
        return Attributes.hasRealAttributes();
    }

}

