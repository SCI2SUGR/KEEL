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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Hyperrectangles.EACH;

/**

 * <p>Descripciï¿½: Contiene los metodos de lectura del fichero de train y test</p>
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 * <p>Empresa: Mi Casa</p>
 * @author Rosa Venzala
 * @version 1.0
 */

import java.io.*;
import keel.Dataset.*;
import java.util.*;
//import java.util.Arrays;
import org.core.*;

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
    // for nominal classes
    private String[] C2=null;
    private double[] eMaximum;
    private double[] eMinimum;

    // number of examples
    private int nData; 
    // number of variables
    private int nVariables;
    // number of in-puts
    private int nInputs;
    // number of classes
    private int nClasses;

    final static boolean debug = false;

    private InstanceSet IS;

    private int[] common;
    private int []numValues;
    private double[][]sortedValuesList;

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
    
    public double []getXNor(int pos){
    	normalize();
    	return X[pos];
    }
    
    public InstanceSet getInstanceSet(){
    	return IS;
    }
    
    /**
     * <p>
     * Returns the nominal value of the double value of the attribute
     * </p>
     */
    public String findNominalValue(int atr,double valor){
    	String dev="";
	boolean parar=false;
 	for (int i=0;i<nData&&!parar;i++){
	  	if(valor==X[i][atr]){dev=X2[i][atr];parar=true;}
	  }
	return dev;
    }
    /**
     * <p>
     * Checks if in the instances set left instances of a determined class
     * </p>
     * @param whichClass The class
     * @return true if there're instances, false otherwise
     */
    public boolean thereInstancesOfClass(int whichClass)throws ArrayIndexOutOfBoundsException{

	boolean resul=false;
	int cadena;
	Instance[] instanceSet;
	if (whichClass <0 || whichClass >= nClasses) {throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichClass+" class and there are only "+nClasses+".");}
	
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
     * Gets for each attribute the sorted list of its possible values
     * </p>
     */
    public double [][]getListValues(){
    	double [][]valuesList=new double[nInputs] [nData];
    	for (int i=0;i<nInputs;i++){
	  double []vector=new double [nData];
	  for(int j=0;j<nData;j++){
	  	vector[j]=X[j][i];
	  }
	  vector=removeDuplicated(vector,i);
	   Arrays.sort(vector,0,numValues[i]);
	  valuesList[i]=vector;
	}
	sortedValuesList=valuesList;
	return valuesList;
    }
    
    private double[] removeDuplicated(double[]v,int atributo){
    	int contador=0;
	boolean encontrado;
	double[]aux=new double[v.length];
    	for(int i=0;i<v.length;i++){
		encontrado=false;
		for(int j=0;j<contador&&(!encontrado);j++){
		   if(aux[j]==v[i])encontrado=true;//el valor ya esta
		}
		if(!encontrado){aux[contador]=v[i];contador++;}
	}
	numValues[atributo]=contador;
	return aux;//v=aux;
    }
    
    /**
     * <p>
     * Creates a matrix training set, stored for each class, each attribute, and each value
     * the number of examples of class C that have value V for the attribute A COUNT[C,V,A]
     * </p>
     * @return int [][][] A matrix with the number of examples
     */
    public int [][][] createCount(){
    	int [][][] count=new int [nClasses][nInputs] [nData];;
	//para definir la matriz Count, nvalores podria ser en el peor caso
	// el numero de instancias, es decir, no se repite ningun valor para algun atributo
	//ndatos es IS.getNumInstances();
	//obtener un array ordenado de los valores de un atributo sin repetirse
	 //esta seria la nueva funcion getNumValues, la de antes no sirve
	 //buscar el valor actual en este vector y devolver el indice
	double valor_actual;String nominal_actual;int indice;
	int []num_valores=new int [nInputs];
	int []tipos=new int [nInputs];
	num_valores=getNumValues();
	for (int i=0; i<nClasses;i++) {for (int j=0; j<nInputs;j++){
		     for (int k=0; k<nData;k++)count[i][j][k]=0;
	}}	 
	for (int i=0; i<nClasses;i++) {
		for (int j=0; j<nInputs;j++){
		     tipos=typesVariable();
		     for (int k=0; k<nData;k++){
			//System.out.println("clase "+C[k]);
			if(C[k]==i){//esta instancia es de la clase actual i
			  valor_actual=X[k][j];nominal_actual=X2[k][j];
			  //System.out.println("valor actual es "+X[k][j]);
			 // System.out.println("valor actual NOMINAL es "+X2[k][j]);
			 //El atributo es nominal
			  if(tipos[j]==0)count[i][j][(int)valor_actual]++;
			  //para los numericos es cuando tenemos que ordenar
			  else{
				indice=search(sortedValuesList[j],valor_actual);
				if(indice==-1)System.err.println("Error: el valor no se encontro en el vector");
			  	count[i][j][indice]++;
			   }// System.out.println("la clase atributo y valor "+i+" "+j+" "+(int)valor_actual+" lleva "+Count[i][j][(int)valor_actual]);
			}
	}}}//de los 3for
	return count;
    }
    
    
    /**
     * <p>
     * Look for an element in a sorted vector.
     * Returns the index where found it, returns -1 otherwise.
     * </p>
     */
    private int search(double[]v,double valor){
	boolean found=false;
	int index=-1;
    	for(int i=0;i<v.length&&(!found);i++){
		if(v[i]==valor){found=true;index=i;}
	}
	return index;
    }
    
    /**
     * <p>
     * Returns a vector with the optimum class for each pair attribute-value
     * </p>
     * @return int [][] vector with the optimum classes
     */
    public int [][]getOptimumClass(int [][][]Count,long seed){
    	int [][]optimum=new int[nInputs] [nData];
    	int []vector=new int[nClasses];
    	for (int i=0; i<nInputs;i++){
	    for (int j=0; j<numValues[i];j++){
	    	for(int k=0;k<nClasses;k++)vector[k]=Count[k][i][j];
		optimum[i][j]=getMaximum(vector,seed);
	    }
	}
	return optimum;
    }
    
    /**
     * <p>
     * Returns the index where is the maximum in an array of integers
     * </p>
    * @return int index where is the aximum value
    */
    private int getMaximum(int []num, long seed){
    	Randomize.setSeed(seed);
    	int max=num[0];int indice=0;
		int []options=new int[nClasses];
		int counter=0;
		options[counter]=0;
		counter++;
    	for(int i=1;i<num.length;i++){
	       if(num[i]>max){max=num[i];indice=i;counter=0;options[counter]=i;counter++;}
		else{if(num[i]==max){options[counter]=i;counter++;}}
		}
		if((counter-1)>0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
			indice=Randomize.RandintClosed(0, counter);
			indice=options[indice];
		}
		return indice;
    }
    
    /**
     * <p>
     * Returns the index where is the maximum in an array of doubles
     * </p>
    * @return int index where is the aximum value
    */
    public int getMax(double []num, long seed){
    	Randomize.setSeed(seed);
    	double max=num[0];
    	int index=0;
    	int []options=new int[nInputs];
    	int counter=0;
		options[counter]=0;counter++;
	    	for(int i=1;i<num.length;i++){
		       if(num[i]>max){max=num[i];index=i;counter=0;options[counter]=i;counter++;}
			else{if(num[i]==max){options[counter]=i;counter++;}}
		}
		if((counter-1) > 0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
			index=Randomize.RandintClosed(0, counter);
			System.out.println("Elegimos "+index);
			index=options[index];
		}
		return index;
    }
    
    /**
     * <p>
     * Returns for each attribute the number of different values 
     * </p>
     * @return int [] an array with the number of different values
     */
    public int []getNumValues(){
    	int []num=new int [nInputs];
    	for(int i=0;i<nInputs;i++){
		num[i]=0;
		for (int j=1;j<nData;j++){
			if((int)X[j][i]> num[i])num[i]=(int)X[j][i];
		}
		num[i]++;
	}
	return num;
    }
    
    /**
     * <p>
     * Returns for each attribute the number of different values 
     * </p>
     * @return int [] an array with the number of different values
     */
    public int []getNumValues2(){
	return numValues;
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
     * Devuelve el valor de los atributos de salida para una instancia determinada
     *@param pos La posicion de la instancia en el conjunto de datos
     * @return int el valor de la clase para esa instancia
     */
    public int getC(int pos){
    	return C[pos];
    }

    /**
     * <p>
     * Returns an array with the maximum values of the in-put attributes
     * </p>
     * @return double[] idem
     */
    public double[] getMaximum() {
        return eMaximum;
    }

    /**
     * <p>
     * Returns an array with the minimum values of the in-put values
     * </p>
     * @return double[] idem
     */
    public double[] getMinimum() {
        return eMinimum;
    }

    /**
     * <p>
     * Return the number of examples
     * </p>
     * @return int the number of examples
     */
    public int getNData() {
        return nData;
    }

    /**
     * <p>
     * Returns the number of variables
     * </p>
     * @return int The number of variables(including in-put and out-put)
     */
    public int getNVariables() {
        return nVariables;
    }

    /**
     * <p>
     * Return the number of in-put variables
     * </p>
     * @return int Total of the in-put variables
     */
    public int getNInPuts() {
        return nInputs;
    }

    /**
     * <p>
     * Returns the total number of classes
     * </p>
     * @return int the number of classes
     */
    public int getNClasses() {
        return nClasses;
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
    public void readSet(String nfejemplos, boolean train) throws            IOException {
        try {
            // Load in memory a dataset that contains a classification problem
            IS.readSet(nfejemplos, train);
            nData = IS.getNumInstances();
            nInputs = Attributes.getInputNumAttributes();
            nVariables = nInputs + Attributes.getOutputNumAttributes();
	    
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
            X = new double[nData][nInputs];
	    X2 = new String[nData][nInputs];
            missing = new boolean[nData][nInputs];
            C = new int[nData];
	    C2=new String[nData];
	    numValues=new int[nInputs];
	    sortedValuesList=new double[nInputs] [nData];

            // Maximum and minimum of inputs
            eMaximum = new double[nInputs];
            eMinimum = new double[nInputs];

            // All values are casted into double/integer
            nClasses = 0;
            for (int i = 0; i < nData; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nInputs; j++) {
                    X2[i][j] = IS.getInputNominalValue(i, j); //inst.getInputRealValues(j);
		    X[i][j] = IS.getInputNumericValue(i, j);
		   // System.out.println(X[i][j]);
                    missing[i][j] = inst.getInputMissingValues(j);
                    if (X[i][j] > eMaximum[j] || i == 0) {
                        eMaximum[j] = X[i][j];
                    }
                    if (X[i][j] < eMinimum[j] || i == 0) {
                        eMinimum[j] = X[i][j];
                    }
                }

                if (noOutputs) {
                    C[i] = 0;
                } else {
		    C[i] =  (int)IS.getOutputNumericValue(i, 0);
                    C2[i] =  IS.getOutputNominalValue(i, 0); //(int)inst.getOutputRealValues(i);
                }
                if (C[i] > nClasses) {
                    nClasses = C[i];
                }
            }
            nClasses++;
            System.out.println("Number of classes=" + nClasses);
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
    public String copyHeaderTest() {
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
    public void normalize() {
        int atts = this.getNInPuts();
        double moximus[] = new double[atts];
        for (int j = 0; j < atts; j++) {
            moximus[j] = 1.0 / (eMaximum[j] - eMinimum[j]);
        }
        for (int i = 0; i < this.getNData(); i++) {
            for (int j = 0; j < atts; j++) {
                if (isMissing(i, j)) {
                    ; //no escojo este ejemplo
                } else {
                    X[i][j] = (X[i][j] - eMinimum[j]) * moximus[j];
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
    public int[] typesVariable() {
        int[] types = new int[this.nInputs];
        for (int i = 0; i < this.nInputs; i++) {
            types[i] = 1;
            if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
                types[i] = 0;
            }
        }
        return types;
    }

    /**
     * <p>
     * Calculate the values most commons for each column or attribute
     * </p>
     */
    public void computeMostComon() {
        common = new int[nInputs];
        int[] aux = new int[nData];
        for (int i = 0; i < nInputs; i++) {
            for (int j = 0; j < nData; j++) {
                if (this.isMissing(j, i)) {
                    aux[j] = -1;
                } else {
                    aux[j] = (int) X[j][i];
                }
            }
            Arrays.sort(aux);
            int mostC1 = aux[0];
            int counter1 = 1, j;
            for (j = 1; (aux[j] == mostC1) && (j < nData - 1); j++, counter1++) {
                ;
            }
            int counter2 = 1;
            int mostC2 = aux[j];
            if (j + 1 < nData) {
                for (j = j + 1; j < nData; j++) {
                    if (aux[j] == mostC2) {
                        counter2++;
                    } else {
                        mostC2 = aux[j];
                        if (counter2 > counter1) {
                            counter1 = counter2;
                            mostC1 = mostC2;
                            counter2 = 1;
                        }
                    }
                }
            }
            common[i] = mostC1;
        }
    }

    /**
     * <p>
     * Return the value most common of the attribute 'i'
     * </p>
     * @param i int Number of the attribute
     * @return int Most common value for this variable
     */
    public int mostCommon(int i) {
        return common[i];
    }

    /**
     * <p>
     * Returns the name of the problem's variables
     * </p>
     * @return String[] An array with the name of the problem's variables
     */
    public String[] getNames() {
        String[] out = new String[nVariables];
        for (int i = 0; i < nInputs; i++) {
            out[i] = Attributes.getInputAttribute(i).getName();
        }
        out[nInputs] = Attributes.getOutputAttribute(0).getName();
        return out;
    }

    /**
     * <p>
     * Returns teh value of the classes
     * </p>
     * @return String[] An aray with the name of the out-puts(classes)
     */
    public String[] giveClasses(){
        String [] out = new String[nClasses];
        Attribute at = Attributes.getOutputAttribute(0);
        if (at.getType() == at.NOMINAL){
            for (int i = 0; i < nClasses; i++) {
                out[i] = at.getNominalValue(i);
            }
        }
        else{
            out = null; //luego guardarï¿½el valor de las clases numï¿½icas
        }
        return out;
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

}

