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
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Prism;

import java.io.*;
import keel.Dataset.*;
import java.util.Arrays;

public class Dataset {
/**
 * <p>
 * Class to manage data sets
 * </p>
 */
    private double[][] X = null;
    // for the nominal values
    private String [][]X2=null;
    private boolean[][] missing = null;
    private int[] C = null;
    // for the nominal classes
    private String[] C2=null;
    private double[] emaximo;
    private double[] eminimo;

    // Number of examples
    private int ndatos; 
    // Number of variables
    private int nvariables; 
    // Number of in-puts
    private int nentradas; 
    // Number of classes
    private int nclases;

    final static boolean debug = false;

    private InstanceSet IS;

    private int[] comunes;

    /**
     * <p>
     * Return the values of the in-put attributes
     * </p>
     * @return double[][] An array with the in-put attributes
     */
    public double[][] getX() {
        return X;
    }
    
    public String[][]getX2(){
    	return X2;
    }
    
    /**
     * <p>
     * Return the values of the in-put attributes for an instance
     * </p>
     * @param pos The position of the instance in the set of values
     * @return double[] An array with the in-put attributes for the instance
     */
    public double []getX(int pos){
    	return X[pos];
    }
    
    public InstanceSet getInstanceSet(){
    	return IS;
    }
    
    /**
     * <p>
     * Checks if in the instance set the are instances of a one determinet class
     * </p>
     * @param whichClass the class 
     * @return true if there are instances
     */
    public boolean hayInstanciasDeClaseC(int whichClass)throws ArrayIndexOutOfBoundsException{
 
	boolean resul=false;
	int cadena;
	Instance[] instanceSet;
	if (whichClass <0 || whichClass >= nclases) {throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichClass+" class and there are only "+nclases+".");}
	
	instanceSet=IS.getInstances();
	//este bucle va a sustituir a la funcion antes definida en instance set
	for(int i=0;i<IS.getNumInstances();i++){
			cadena=instanceSet[i].getOutputNominalValuesInt(0);
			if(cadena==whichClass){resul=true;
			//System.out.println(i);
			}
	}
	//resul=IS.hayInstanciasDeClaseC(whichClass);
	return resul;
    }
      

    /**
     * <p>
     * Returns the values for the out-put(class)
     * </p>
     * @return int[] An array with the values of the class
     */
    public int[] getC() {
        int[] retorno = new int[C.length];
        for (int i = 0; i < C.length; i++) {
            retorno[i] = C[i];
        }
        return retorno;
    }
    
    public String[] getC2() {
        String[] retorno = new String[C2.length];
        for (int i = 0; i < C2.length; i++) {
            retorno[i] = C2[i];
        }
        return retorno;
    }

    /**
     * <p>
     * Returns an array with the maximum values of the in-put attributes
     * </p>
     * @return double[] idem
     */
    public double[] getemaximo() {
        return emaximo;
    }

    /**
     * <p>
     * Returns an array with the minimum values of the in-put values
     * </p>
     * @return double[] idem
     */
    public double[] geteminimo() {
        return eminimo;
    }

    /**
     * <p>
     * Return the number of examples
     * </p>
     * @return int the number of examples
     */
    public int getndatos() {
        return ndatos;
    }

    /**
     * <p>
     * Returns the number of variables
     * </p>
     * @return int The number of variables(including in-put and out-put)
     */
    public int getnvariables() {
        return nvariables;
    }

    /**
     * <p>
     * Return the number of in-put variables
     * </p>
     * @return int Total of the in-put variables
     */
    public int getnentradas() {
        return nentradas;
    }

    /**
     * <p>
     * Returns the total number of classes
     * </p>
     * @return int the number of classes
     */
    public int getnclases() {
        return nclases;
    }

    /**
     * <p>
     * Checks if one attribute is lost or not
     * </p>
     * @param i int Number of example
     * @param j int Number of attribue
     * @return boolean True if lost
     */
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }

    /**
     * <p>
     * Constructor, creates a new set of instances
     * </p>
     */
    public Dataset() {
        IS = new InstanceSet(); // Init a new set of instances
    }

    /**
     * <p>
     * Reads the file of examples(Train&Test)
     * </p>
     * @param nfejemplos String Nom of the examples file
     * @param train boolean True if Train set. False is test set.
     * @throws IOException A possible I/O error
     */
    public void leeConjunto(String nfejemplos, boolean train) throws            IOException {
        try {
            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfejemplos, train);
            ndatos = IS.getNumInstances();
            nentradas = Attributes.getInputNumAttributes();
            nvariables = nentradas + Attributes.getOutputNumAttributes();
	    
	   /* System.out.println(ndatos);
	    System.out.println(nentradas);
	    System.out.println(nvariables);*/

            // Check that there is only one output variable
            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
                System.exit(1); //TERMINAR
            }
            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
                System.exit(1); //TERMINAR
            }

            // Initialice and fill our own tables
            X = new double[ndatos][nentradas];
	    X2 = new String[ndatos][nentradas];
            missing = new boolean[ndatos][nentradas];
            C = new int[ndatos];
	    C2=new String[ndatos];

            // Maximum and minimum of inputs
            emaximo = new double[nentradas];
            eminimo = new double[nentradas];

            // All values are casted into double/integer
            nclases = 0;
            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nentradas; j++) {
                    X2[i][j] = IS.getInputNominalValue(i, j); //inst.getInputRealValues(j);
		    X[i][j] = IS.getInputNumericValue(i, j);
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
		    C[i] =  (int)IS.getOutputNumericValue(i, 0);
                    C2[i] =  IS.getOutputNominalValue(i, 0); //(int)inst.getOutputRealValues(i);
                }
                if (C[i] > nclases) {
                    nclases = C[i];
                }
            }
            nclases++;
            System.out.println("Number of classes=" + nclases);
	    //IMPRIME TODOS LOS ATRIBUTOS Y TODAS LAS INSTANCIAS
	   // IS.print();

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }

    /**
     * <p>
     * Returns a string with the header of the file
     * </p>
     * @return String The data of the header of the file
     */
    public String copiaCabeceraTest() {
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
     * <p>
     * Convert all the values of the set of values in the inetrval[0,1]
     * </p>
     */
    public void normaliza() {
        int atts = this.getnentradas();
        double maximos[] = new double[atts];
        for (int j = 0; j < atts; j++) {
            maximos[j] = 1.0 / (emaximo[j] - eminimo[j]);
        }
        for (int i = 0; i < this.getndatos(); i++) {
            for (int j = 0; j < atts; j++) {
                if (isMissing(i, j)) {
                    ; //no escojo este ejemplo
                } else {
                    X[i][j] = (X[i][j] - eminimo[j]) * maximos[j];
                }
            }
        }
    }

    /**
     * <p>
     * Return the types of each in-put(NOMINAL[0] o NUMERIC[1])
     * </p>
     * @return int[] A vector with (NOMINAL[0] o NUMERIC[1])
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
     * <p>
     * Calculate the values most commons for each column or attribute
     * </p>
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
     * <p>
     * Return the value most common of the attribute 'i'
     * </p>
     * @param i int Number of the attribute
     * @return int Most common value for this variable
     */
    public int masComun(int i) {
        return comunes[i];
    }

    /**
     * <p>
     * Returns the name of the problem's variables
     * </p>
     * @return String[] An array with the name of the problem's variables
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
     * <p>
     * Returns teh value of the classes
     * </p>
     * @return String[] An aray with the name of the out-puts(classes)
     */
    public String[] dameClases(){
        String [] salida = new String[nclases];
        Attribute at = Attributes.getOutputAttribute(0);
        if (at.getType() == at.NOMINAL){
            for (int i = 0; i < nclases; i++) {
                salida[i] = at.getNominalValue(i);
            }
        }
        else{
            salida = null; //luego guardarï¿½el valor de las clases numï¿½icas
        }
        return salida;
    }

    /**
     * <p>
     * Checks if in the data base there is a in-put type real or continous
     * </p>
     * @return boolean True if exists, False otherwise
     */
    public boolean hayAtributosContinuos(){
        return Attributes.hasRealAttributes();
    }
    
    /**
     * <p>
     * Checks if in the data base there is an in-put type integer
     * </p>
     * @return boolean True if exists, False otherwise
     */
    public boolean hayAtributosDiscretos(){
        return Attributes.hasIntegerAttributes();
    }

}

