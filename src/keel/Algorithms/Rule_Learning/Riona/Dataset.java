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
 * @author Written by Rosa Venzala 19/09/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.3
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Riona;

import java.io.*;
import keel.Dataset.*;
import java.util.*;
import org.core.*;

public class Dataset {
/**
 * <p>
 * Methods for reading the train & test file
 * </p>
 */
	private double[][] X = null;
	private String [][]X2=null;
	private boolean[][] missing = null;
	private int[] C = null;
	private String[] C2=null;
	private double[] eMaximum;
	private double[] eMinimum;

	private int nData;
	private int nVariables;
	private int nInPuts;
	private int nClasses;

	final static boolean debug = false;

	private InstanceSet IS;

	private int[] commons;
	private int []numValues;
	private double[][]sortedValuesList;
	private int[][][]counter;
	private double[][][]SVDM;
	private double[][]XSinNor=null;
	/**
	 * <p>
	 * Return the values of in-put attributes
	 * </p>
	 * @return double[][] in-put attributes
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
	 * 
	 * @param pos The position of the instance
	 * @return double[] In-put attributes for this instance
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
	 * Return the nominal value of the attribute
	 * </p>
	 */
	public String findNominalValue(int attribute,double value){
		String str="";
		boolean stop=false;
		for (int i=0;i<nData&&!stop;i++){
			if(value==XSinNor[i][attribute]){str=X2[i][attribute];stop=true;}
		}
		return str;
	}
	
	/**
	 * <p>
	 * Check if in the set of the instances the are instances of a determined class
	 * </p>
     * @param whichClass Tha lookinf for class instances
     * @return true if there are instances
	 */
	public boolean existInstanceOfClassC(int whichClass)throws ArrayIndexOutOfBoundsException{

		boolean resul=false;
		int str;
		Instance[] instanceSet;
		if (whichClass <0 || whichClass >= nClasses) {throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichClass+" class and there are only "+nClasses+".");}

		instanceSet=IS.getInstances();
		//este bucle va a sustituir a la funcion antes definida en instance set
		for(int i=0;i<IS.getNumInstances();i++){
			str=instanceSet[i].getOutputNominalValuesInt(0);
			if(str==whichClass){resul=true;
			//System.out.println(i);
			}
		}
		//resul=IS.hayInstanciasDeClaseC(whichClass);
		return resul;
	}

	/**
	 * <p>
	 * Gets for each attribute the ordered list of the possible values
	 * </p>
	 */
	public double [][]getListValues(){
		double [][]valuesList=new double[nInPuts] [nData];
		for (int i=0;i<nInPuts;i++){
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

	private double[] removeDuplicated(double[]v,int attribute){
		int counter=0;
		boolean bFound;
		double[]aux=new double[v.length];
		for(int i=0;i<v.length;i++){
			bFound=false;
			for(int j=0;j<counter&&(!bFound);j++){
				if(aux[j]==v[i])bFound=true;//el valor ya esta
			}
			if(!bFound){aux[counter]=v[i];counter++;}
		}
		numValues[attribute]=counter;
		return aux;//v=aux;
	}

	/**
	 * <p>
	 * Creates a 3D array from training set, stored for each class, each attribute and each
	 * value  the number of examples of the class C witch have the value V for the attribute
	 * A COUNT[C,V,A]
	 * </p>
	 * @return int [][][] Matrix with the number of examples
	 */
	public int [][][] creaCount(){
		counter=new int [nClasses][nInPuts] [nData];
		//para definir la matriz Count, nvalores podria ser en el peor caso
		// el numero de instancias, es decir, no se repite ningun valor para algun atributo
		//ndatos es IS.getNumInstances();
		//obtener un array ordenado de los valores de un atributo sin repetirse
		//esta seria la nueva funcion getNumValues, la de antes no sirve
		//buscar el valor actual en este vector y devolver el indice
		double valor_actual;String nominal_actual;int indice;
		int []num_valores=new int [nInPuts];
		int []tipos=new int [nInPuts];
		/*num_valores=*/setNumValues();
		for (int i=0; i<nClasses;i++) {for (int j=0; j<nInPuts;j++){
			for (int k=0; k<nData;k++)counter[i][j][k]=0;
		}}	 
		for (int i=0; i<nClasses;i++) {
			for (int j=0; j<nInPuts;j++){
				tipos=variableType();
				for (int k=0; k<nData;k++){
					//System.out.println("clase "+C[k]);
					if(C[k]==i){//esta instancia es de la clase actual i
						valor_actual=X[k][j];nominal_actual=X2[k][j];
						//System.out.println("valor actual es "+X[k][j]);
						// System.out.println("valor actual NOMINAL es "+X2[k][j]);
						//El atributo es nominal
						if(tipos[j]==0)counter[i][j][(int)valor_actual]++;
						//para los numericos es cuando tenemos que ordenar
						else{
							indice=find(sortedValuesList[j],valor_actual);
							if(indice==-1)System.err.println("Error: el valor no se encontro en el vector");
							counter[i][j][indice]++;
						}// System.out.println("la clase atributo y valor "+i+" "+j+" "+(int)valor_actual+" lleva "+Count[i][j][(int)valor_actual]);
					}
				}}}//de los 3for
		return counter;
	}
	/**
	 * <p>
	 * Search and element in an ordered vector
	 * Returns the index where is the element
	 * Return -1 if doesn't exist the value
	 * </p>
	 */
	private int find(double[]v,double valor){
		boolean bFound=false;
		int index=-1;
		for(int i=0;i<v.length&&(!bFound);i++){
			if(v[i]==valor){bFound=true;index=i;}
		}
		return index;
	}
	/**
	 * <p>
	 * Returns a vector with the class for each pair attribute-value
	 * </p>
	 * @return int [][] the vector with the classes
	 */
	public int [][]getOptimumClass(int [][][]cuonter,long seed){
		int [][]optimum=new int[nInPuts] [nData];
		int []vector=new int[nClasses];
		for (int i=0; i<nInPuts;i++){
			for (int j=0; j<numValues[i];j++){
				for(int k=0;k<nClasses;k++)vector[k]=cuonter[k][i][j];
				optimum[i][j]=getMaximum(vector,seed);
			}
		}
		return optimum;
	}

	/**
	 * <p>
	 * Returns the index where is the maximum of an array of integers
	 * If there are more than one, returns one of them
	 * </p>
	 * @return int the index where is the maximum value
	 */
	public int getMaximum(int []num, long seed){
		Randomize.setSeed(seed);
		int max=num[0];
		int index=0;
		int []options=new int[nClasses];
		int counter=0;
		options[counter]=0;counter++;
		for(int i=1;i<num.length;i++){
			if(num[i]>max){max=num[i];index=i;counter=0;options[counter]=i;counter++;}
			else{if(num[i]==max){options[counter]=i;counter++;}}
		}
		if((counter-1)>0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
			index=Randomize.RandintClosed(0, counter);
			index=options[index];
		}
		return index;
	}

	/**
	 * <p>
	 * Returns the index where is the maximum of a double array
	 * </p>
	 * If there are more than one. returns one of them
	 * @return int the index where is the maximum value
	 */
	public int getMaxim(double []num, long seed){
		Randomize.setSeed(seed);
		double max=num[0];
		int index=0;
		int []options=new int[nInPuts];
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
	 * Returns for each attribute the number of attributes for each set of values
	 * </p>
	 */
	public void/*int []*/setNumValues(){
		//int []num=new int [nentradas];
		for(int i=0;i<nInPuts;i++){
			numValues[i]=0;
			for (int j=1;j<nData;j++){
				if((int)X[j][i]> numValues[i])numValues[i]=(int)X[j][i];
			}
			numValues[i]++;
		}
		//return num_valores;
	}
	/**
	 * <p>
	 * Returns for each attributes the number of values for the set
	 * </p>
	 * @return int [] an array with the different values
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
	 * <p>
	 * Returns the value of the attributes of the out-put for an instance
	 * </p>
	 * @param pos The position of the instance in the set of values
	 * @return int The value of the class for that instance
	 */
	public int getC(int pos){
		return C[pos];
	}

	/**
	 * <p>
	 * Returns an array with the minium values of the attributes of the in-put
	 * </p>
	 * @return double[] idem
	 */
	public double[] getEMaximum() {
		return eMaximum;
	}

	/**
	 * <p>
	 * Returns an array with the minium values of the in-put attributes
	 * </p>
	 * @return double[] idem
	 */
	public double[] getEMinimum() {
		return eMinimum;
	}

	/**
	 * <p>
	 * Returns the number of examples
	 * </p>
	 * @return int the numebr of examples
	 */
	public int getNData() {
		return nData;
	}

	/**
	 * <p>
	 * Returns the number of variables
	 * </p>
	 * @return int The numebr of variables(in-put and out-put)
	 */
	public int getNVariables() {
		return nVariables;
	}

	/**
	 * <p>
	 * Returns the number of in-put variables
	 * </p>
	 * @return int Total variables of in-put
	 */
	public int getInPuts() {
		return nInPuts;
	}

	/**
	 * <p>
	 * Returns the total number of classes
	 * </p>
	 * @return int Number of classes
	 */
	public int getNClasses() {
		return nClasses;
	}

	/**
	 * <p>
	 * Checks if an attribute is lost or not
	 * </p>
	 * @param i int Number of the example
	 * @param j int Number of the attribute
	 * @return boolean True if lost, false otherwise
	 */
	public boolean isMissing(int i, int j) {
		// True is the value is missing (0 in the table)
		return missing[i][j];
	}

	/**
	 * <p>
	 * Constructor. Creates a new set of instances.
	 * </p>
	 */
	public Dataset() {
		IS = new InstanceSet(); // Init a new set of instances
	}


	/**
	 * <p>
	 * Reads the file of examples (Train&Test)
	 * </p>
	 * @param samples Name of the file of examples
	 * @param train True if Train, False is Test 
	 * @throws IOException A possible error de I/O
	 */
	public void readSet(String samples, boolean train) throws            IOException {
		try {
			// Load in memory a dataset that contains a classification problem
			IS.readSet(samples, train);
			nData = IS.getNumInstances();
			nInPuts = Attributes.getInputNumAttributes();
			nVariables = nInPuts + Attributes.getOutputNumAttributes();

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
			X = new double[nData][nInPuts];
			X2 = new String[nData][nInPuts];
			missing = new boolean[nData][nInPuts];
			C = new int[nData];
			C2=new String[nData];
			numValues=new int[nInPuts];
			sortedValuesList=new double[nInPuts] [nData];
			SVDM=new double[nInPuts][][];
			XSinNor = new double[nData][nInPuts];

			// Maximum and minimum of inputs
			eMaximum = new double[nInPuts];
			eMinimum = new double[nInPuts];

			// All values are casted into double/integer
			nClasses = 0;
			for (int i = 0; i < nData; i++) {
				Instance inst = IS.getInstance(i);
				for (int j = 0; j < nInPuts; j++) {
					X2[i][j] = IS.getInputNominalValue(i, j); //inst.getInputRealValues(j);
					X[i][j] = IS.getInputNumericValue(i, j);
					XSinNor[i][j] = IS.getInputNumericValue(i, j);
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
	 * Returns the header of the file
	 * </p>
	 * @return String Header of the file(train)
	 */
	public String copyTestHeader() {
		// Header of the output file
		String str = new String("");
		str = "@relation " + Attributes.getRelationName() + "\n";
		str += Attributes.getInputAttributesHeader();
		str += Attributes.getOutputAttributesHeader();
		str += Attributes.getInputHeader() + "\n";
		str += Attributes.getOutputHeader() + "\n";
		str += "@data\n";
		return str;
	}

	/**
	 * <p>
	 * Converts all the values of the set into the [0,1] interval
	 * </p>
	 */
	public void normalize() {
		int attributes = this.getInPuts();
		double maximus[] = new double[attributes];
		for (int j = 0; j < attributes; j++) {
			maximus[j] = 1.0 / (eMaximum[j] - eMinimum[j]);
		}
		for (int i = 0; i < this.getNData(); i++) {
			for (int j = 0; j < attributes; j++) {
				if (isMissing(i, j)||(Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL)||(eMaximum[j]==eMinimum[j])) {
					; //no escojo este ejemplo
					//no normalizamos tampoco si es nominal o si la diferencia entre el 
					//maximo y minimo es 0, es decir, solo hay un valor, para que no de NaN
				} else {
					X[i][j] = (X[i][j] - eMinimum[j]) * maximus[j];
				}
			}
		}
	}


	/**
	 * <p>
	 * Returns the types of each in-put (NOMINAL[0] or NUMERICO[1])
	 * </p>
	 * @return int[] Vector with 0(nominal) or 1(numeric) 
	 */
	public int[] variableType() {
		int[] types = new int[this.nInPuts];
		for (int i = 0; i < this.nInPuts; i++) {
			types[i] = 1;
			if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
				types[i] = 0;
			}
		}
		return types;
	}

	/**
	 * <p>
	 * Calculates the values for each column and attribute
	 * </p>
	 */
	public void calculateMostCommon() {
		commons = new int[nInPuts];
		int[] aux = new int[nData];
		for (int i = 0; i < nInPuts; i++) {
			for (int j = 0; j < nData; j++) {
				if (this.isMissing(j, i)) {
					aux[j] = -1;
				} else {
					aux[j] = (int) X[j][i];
				}
			}
			Arrays.sort(aux);
			int mostCommon = aux[0];
			int counter = 1, j;
			for (j = 1; (aux[j] == mostCommon) && (j < nData - 1); j++, counter++) {
				;
			}
			int contador2 = 1;
			int mascomun2 = aux[j];
			if (j + 1 < nData) {
				for (j = j + 1; j < nData; j++) {
					if (aux[j] == mascomun2) {
						contador2++;
					} else {
						mascomun2 = aux[j];
						if (contador2 > counter) {
							counter = contador2;
							mostCommon = mascomun2;
							contador2 = 1;
						}
					}
				}
			}
			commons[i] = mostCommon;
		}
	}

	/**
	 * <p>
	 * Returns the value most comon of the 'i' attribute
	 * </p>
	 * @param i int Number of the attribute
	 * @return int Most comon value for this variable
	 */
	public int mostCommon(int i) {
		return commons[i];
	}

	/**
	 * <p>
	 * Returns the name of the problem variables
	 * </p>
	 * @return String[] An array with the name of problem's variables
	 */
	public String[] giveNames() {
		String[] out = new String[nVariables];
		for (int i = 0; i < nInPuts; i++) {
			out[i] = Attributes.getInputAttribute(i).getName();
		}
		out[nInPuts] = Attributes.getOutputAttribute(0).getName();
		return out;
	}

	/**
	 * <p>
	 * Returns the value of the classes
	 * </p>
	 * @return String[] An array with the value for each out-put
	 */
	public String[] giveClasses(){
		String [] out = new String[nClasses];
		Attribute attribute = Attributes.getOutputAttribute(0);
		if (attribute.getType() == attribute.NOMINAL){
			for (int i = 0; i < nClasses; i++) {
				out[i] = attribute.getNominalValue(i);
			}
		}
		else{
			out = null; //luego guardarï¿½el valor de las clases numï¿½icas
		}
		return out;
	}

	/**
	 * <p>
	 * Checks if in the class the is any in-put of real type or continous
	 * </p>
	 * @return boolean True if exists
	 */
	public boolean existContinousAttributes(){
		return Attributes.hasRealAttributes();
	}




	/**
	 * <p>
	 * Gets the most near example
	 * </p>
	 * @return masCercano The position in the set of values of the example most near
	 */

	public int nearestSample(Complex R,int defaultClass,long seed,int s,int q){
		int nearest=-1;
		double distance=100000.;
		double actual;
		int []nearests=new int[nData];
		int counternearests=0;
		int []nearestMostFrequentClass=new int[nData];
		int countNearestMostFrequentClass=0;
		// Instance []Iset=IS.getInstances();
		boolean existEquals=false;
		for(int i=0;i<nData;i++){
			if((R.getClassAttribute()==C[i]) && (!R.ruleCoversInstance(X[i]/*Iset[i]*/))){
				actual=distance(R,X[i]/*Iset[i]*/,s,q,distance);
				if(actual<distance){
					distance=actual;
					nearest=i;
					nearests[0]=i;
					counternearests=1;
					// System.out.print("SOY "+i+" distancia "+distancia+"	");
				}
				else{
					if(actual==distance){
						nearests[counternearests]=i;
						counternearests++;
						existEquals=true;
						//  System.out.print("SOY IGUAL"+i+" distancia "+distancia+"	");
					}
				}	   
			}
		}
		if(existEquals){
			// System.out.println("hay iguales");
			boolean hayMostFrequentClass=false;
			for(int j=0;j<counternearests;j++){
				if(C[nearests[j]]==defaultClass){
					nearestMostFrequentClass[countNearestMostFrequentClass]=/*i*/nearests[j];
					countNearestMostFrequentClass++;
					hayMostFrequentClass=true;
					//System.out.print("cl defecto "+j+"	");
				}
			}
			int selec;
			Randomize.setSeed(seed);
			if(hayMostFrequentClass){
				// System.out.print("elegir entre "+contMasCercMostFrequentClass+"	");
				selec=Randomize.Randint(0,countNearestMostFrequentClass);
				nearest=nearestMostFrequentClass[selec];
				// System.out.print(masCercMostFrequentClass[0]+" , "+masCercMostFrequentClass[1]+" , "+masCercMostFrequentClass[2]+"	");
				//0an-1
			}
			else {
				selec=Randomize.Randint(0,counternearests);
				nearest=nearests[selec];
			}
		}
		//System.out.println("SELECCION "+masCercano);
		return nearest;
	}

	/**
	 * <p>
	 * Calculates the distance betwen one rule and an example/instance
	 * </p>
	 * @param R the rule
	 * @param E the example
	 * @param s parameter to calculate the distance
	 * @param q parameter to calculate the distance
	 * @param minDist lowest distance
	 * @return dist the distance
	 */
	public double distance(Complex R,double []E,int s,int q,double minDist){
		int at;
		double dist=0;
		Selector se;
		double e,r,factor,absolut;
		for(int i=0;i<R.size();i++){
			se=R.getSelector(i);
			at=se.getAttribute();
			e=E[at];
			if (Attributes.getInputAttribute(at).getType() == Attribute.NOMINAL){
				r=se.getZeroValue();
				//if(e!=r)dist=dist+1; 
				for(int c=0;c<nClasses;c++){
					//System.out.print(probabilidad(c,at,r));
					//System.out.println(probabilidad(c,at,e));
					factor=Math.abs(probability(c,at,r)-probability(c,at,e));
					factor=Math.pow(factor,q);
					//System.out.println(factor);
					dist=dist+factor;
				}
				//System.out.println(" atr "+at+" r "+r+" e "+e);
			}
			else {
				double []values=se.getValues();
				if(e > values[1])
					factor=e-values[1];
				else{if(e < values[0])
					factor=values[0]-e;
				else factor=0;
				}
				factor=Math.pow(factor,s);
				dist=dist+factor;   
			}
			if(dist>minDist)break;//si la distancia que estamos calculando ya es superior a la minima calculada paramos los calculos, esta distancia no sera considerada porque siempre buscamos la minima distancia
		}
		return dist;
	}


	/**
	 * <p>
	 * Calculates the distance betwen two examples
	 * </p>
	 * @param E the number of the example in the dataset
	 * @param E_test the example of the test
	 * @param minDist the lowest distance
	 * @return dist the distance
	 */
	public double distance(int E,double []E_test,/*int N,*/double minDist){
		double dist=0;
		double e,r,factor,absolute;
		for(int at=0;at<nInPuts;at++){
			e=E_test[at];
			r=X[E][at];
			if (Attributes.getInputAttribute(at).getType() == Attribute.NOMINAL){
				for(int c=0;c<nClasses;c++){
					factor=Math.abs(probability(c,at,r/*XSinNor[E][at]*/)-probability(c,at,e/*XSinNor[N][at]*/));
					//factor=Math.pow(factor,q);
					dist=dist+factor;
				}
			}
			else {
				factor=Math.abs(r-e);
				//factor=Math.pow(factor,s);
				dist=dist+factor;   
			}
			if(dist>minDist)break;//si la distancia que estamos calculando ya es superior a la minima calculada paramos los calculos, esta distancia no sera considerada porque siempre buscamos la minima distancia
		}
		return dist;
	}




	/**
	 * <p>
	 * Calculates the probability of one example with value 'valor' for the attribute 'attr'
	 * </p>
	 * @param classAttribute the class
	 * @param attribute number of atribute
	 * @param value the value of the attribute
	 * @return la probability
	 */

	private double probability(int classAttribute,int attribute,double value){
		int num=0;
		int denom=0;
		double fraction;
		for(int i=0;i<nData;i++){
			if(XSinNor[i][attribute]==value){
				denom++;
				if(C[i]==classAttribute)num++;
			}
		}
		if(denom==0)fraction=0;
		else fraction=(double)num/(double)denom;
		return fraction;
	}

	/**
	 * <p>
	 * Calculates the class most frecuent in the set of values
	 * </p>
	 * @return the number of the class most frecuent
	 */
	public int mostFrequentClass(long seed){
		int frequences[]=new int[nClasses];
		for(int i=0;i<nClasses;i++)frequences[i]=0;
		for(int i=0;i<nData;i++){
			frequences[C[i]]++;
		}
		int mostFrequent=getMaximum(frequences,seed);
		return mostFrequent;
	}

	/**
	 * <p>
	 * Calculates the number of positive examples the math with the rule
	 * </p>
	 * @param R the rule
	 * @return number of positives examples
	 */
	public int getNumPosExamples(Complex R){
		int count=0;
		for(int i=0;i<nData;i++){
			if(R.ruleCoversInstance(X[i])){
				if(R.getClassAttribute()==C[i])count++;
			}
		}
		return count;
	}

	/**
	 * <p>
	 * Calculates the number of negative examples that match with the rule
	 * </p>
	 * @param R the rule
	 * @return number of negative examples
	 */
	public int getNumNegExamples(Complex R){
		int count=0;
		for(int i=0;i<nData;i++){
			if(R.ruleCoversInstance(X[i])){
				if(R.getClassAttribute()!=C[i])count++;
			}
		}
		return count;
	}

	/**
	 * <p>
	 * Calculates the neighbour of one test example
	 * </p>
	 * @param test the exmaple of test
	 * @param k the size of the neighbourhood
	 */
	public int [] getNeighbourSet(double[] test,int k){
		double distances[]=new double[nData];
		double distances2[]=new double[nData];
		double minDist=100000000;
		int []selected=new int[nData];
		for(int j=0;j<(nData);j++)selected[j]=-2;
		int pos;
		for(int i=0;i<(nData);i++){
			distances[i]=distance(i,test,/*Numtest,*/minDist);
			//System.out.println(i+" dist total: "+distancias[i]);
			distances2[i]=distances[i];
		}
		Arrays.sort(distances,0,nData);
		//nos quedamos con los k mas cercanos

		//leave-one out methodology j=1 j<=k
		//la primera distancia no la queremos siempre sera 0, corresponde a el mismo
		for(int j=1;(j<=k || (distances[j]==distances[k/*-1*/]));j++){
			pos=lookup(distances2,distances[j],selected);
			selected[j-1]=pos;
			//System.out.println(elegidos[j]);
		}
		return selected;
	}
	/**
	 * <p>
	 * Search an element in an ordered vector
	 * Returns the index if find the value
	 * Returns -1 otherwise
	 * </p>
	 */
	private int lookupInt(int[]v,int value){
		boolean found=false;
		int index=-1;
		for(int i=0;i<v.length&&(!found);i++){
			if(v[i]==value){found=true;index=i;}
		}
		return index;
	}
	private int lookup(double[]v,double value,int []selected){
		boolean found=false;
		int index=-1;
		int exist;
		for(int i=0;i<v.length&&(!found);i++){
			exist=lookupInt(selected,i);
			if(v[i]==value && exist==-1){found=true;index=i;}
		}
		return index;
	}

	/**
	 * <p>
	 * Gets a matrix where store for each nominal attribute, the distances betwen all the possible values
	 * </p>
	 */
	public void computeSVDM(){
		/*int []numV=getNumValues();*/
		for(int attr=0;attr<nInPuts;attr++){
			if(variableType()[attr]==0){//SOLO SI ES NOMINAL
				int nV=numValues[attr];
				double dist,factor;
				SVDM[attr]=new double[nV][];
				for(int i=0;i<nV;i++)SVDM[attr][i]=new double[nV];
				for(int i=0;i<nV;i++){
					for(int j=i;j<nV;j++){
						if(j==i)SVDM[attr][i][j]=0.;
						else{
							dist=0;
							for(int c=0;c<nClasses;c++){
								factor=Math.abs(probability(c,attr,(double)i)-probability(c,attr,(double)j));
								dist=dist+factor;
							}
							SVDM[attr][i][j]=dist;
							SVDM[attr][j][i]=dist;
						}
					}
				}
			}
		}
		/*for(int a=0;a<nentradas;a++){
		int nV=num_valores[a];
		for(int b=0;b<nV;b++){
			for(int c=0;c<nV;c++)System.out.print(SVDM[a][b][c]+" ");
			//System.out.println();
		}
		//System.out.println();
	}*/

	}

	/**
	 * <p>
	 * Creates a vector with the values of the attributes that d(tst[atr],trn[atr])
	 * </p>
	 * @param attr the attribute
	 * @param test the test example
	 * @param train the train example
	 */
	public double[]createBall(int attr,double test,double train){
		//int []numV=getNumValues();
		int nV=numValues[attr];
		double[]components=new double[nV];
		for(int i=0;i<nV;i++)components[i]=-1;

		if(test!=-1){
			double radio=SVDM[attr][(int)test][(int)train];
			int count=0;
			for(int i=0;i<nV;i++){
				/*if(atr==0){System.out.println(SVDM[atr][(int)tst][i]);
		System.out.println("radio "+radio);
		}*/
				if(SVDM[attr][(int)test][i]<=radio){components[count]=(double)i;count++;/*System.out.println("el "+i+ "en componentes ");*/}
			}
		}
		else{
			if(nV>1){
				components[0]=test;
				components[1]=train;
			}else{ //Julian - If no correspondant example in test/train exists, do not include selector
				components = null;
			}
		}
		return components;
	}
	
	/**
	 * <p>
	 * Gets the real value
	 * </p>
	 */
	public double getRealValue(int at,String str){
		for(int i=0;i<numValues[at];i++){
			String n=findNominalValue(at,(double)i);
			if(n.compareTo(str)==0)return (double)i;
		}
		return (-1.);
	}

	/**
	 * <p>
	 * Returns the class most frecuent of the set of instances
	 * </p>
	 */
	public int getMostFrequentClass(){
		int [] clasesEval;
		clasesEval = getC();
		int sampleForClasTrain[] = new int[nClasses];
		for (int j = 0; j < nClasses; j++) {
			sampleForClasTrain[j] = 0;
			for (int i = 0; i < nData; i++) {
				if (j == clasesEval[i]) {
					sampleForClasTrain[j]++;
				}
			}}
		int clasePorDefecto=0;
		for (int i = 0, clase = -1; i < nClasses; i++) {
			if (sampleForClasTrain[i] > clase) {
				clasePorDefecto = i;
				clase = sampleForClasTrain[i];
			}
		}
		return clasePorDefecto;
	}

	/**
	 * <p>
	 * Calculates the k examples most near of the set
	 * </p>
	 * @param test the test example
	 * @param k number of neighbours
	 */
	public int [] getNN(double[] test,int k){
		double distacnes[]=new double[nData];
		double distances2[]=new double[nData];
		double minDist=100000000;
		int []selected=new int[nData];
		for(int j=0;j<nData;j++)selected[j]=-2;
		int position;
		for(int i=0;i<nData;i++){
			distacnes[i]=distance(i,test,/*Numtest,*/minDist);
			//System.out.println(i+" dist total: "+distancias[i]);
			distances2[i]=distacnes[i];
		}
		Arrays.sort(distacnes,0,nData);

		//leave-one out methodology j=1 j<=k
		//la primera distancia no la queremos siempre sera 0, corresponde a el mismo
		for(int j=1;(j<=k);j++){

			position=lookup(distances2,distacnes[j],selected);
			selected[j-1]=position;
		}
		return selected;
	}


}

