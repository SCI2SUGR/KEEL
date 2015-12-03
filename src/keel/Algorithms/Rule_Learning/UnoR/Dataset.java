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

package keel.Algorithms.Rule_Learning.UnoR;



import java.io.*;
import keel.Dataset.*;
import java.util.*;
//import java.util.Arrays;
import org.core.*;

/**
 * <p>Title: Data-set</p>
 * <p>Description: It contains the methods for reading the training and test files</p>
 * @author Written by Rosa Venzala 2007
 * @version 1.0
 * @since JDK1.4
 */
public class Dataset {

    private double[][] X = null;
    private String [][]X2=null;//para los datos nominales
    private boolean[][] missing = null;
    private int[] C = null;
    private String[] C2=null;//para las clases nominales
    private double[] emaximo;
    private double[] eminimo;

    private int ndatos; // Number of examples
    private int nvariables; // Numer of variables
    private int nentradas; // Number of inputs
    private int nclases; // Number of classes

    final static boolean debug = false;

    private InstanceSet IS;

    private int[] comunes;
    private int []num_valores;//numero de valores distintos para cada entrada
    private double[][]sortedValuesList;//para cada entrada se tiene la lista de valores 
    					//ordenados en orden ascendente y sin repeticiones
    private String []auxiliar2=null;//vector tamaÃ±o numero real de clases en train que contiene el nombre de las clases en nominal

    /**
     * It returns the values of the input attributes
     * @return double[][] An array with the input attributes
     */
    public double[][] getX() {
        return X;
    }
    
    /**
     * <p>
     * Return the values of the in-put attributes
     * </p>
     * @return double[][] An array with the in-put attributes
     */
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
    
        /**
     * Returns the instance set
     * @return the instance set
     */
    public InstanceSet getInstanceSet(){
    	return IS;
    }
    
    /**
     * <p>
     * Returns the nominal value of the double value of the attribute
     * </p>
     * @param atr attribute id.
     * @param valor attribute id.
     * @return the nominal value of the double value of the attribute
     */
    public String findNominalValue(int atr,double valor){
    	String dev="";
	boolean parar=false;
 	for (int i=0;i<ndatos&&!parar;i++){
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
     * Gets for each attribute the sorted list of its possible values
     * </p>
     * @return list with each attribute the sorted list of its possible values
     */
    public double [][]getListValues(){
    	double [][]lista_valores=new double[nentradas] [ndatos];
    	for (int i=0;i<nentradas;i++){
	  double []vector=new double [ndatos];
	  for(int j=0;j<ndatos;j++){
	  	vector[j]=X[j][i];
	  }
	  vector=elimina_repetidos(vector,i);
	   Arrays.sort(vector,0,num_valores[i]);
	  lista_valores[i]=vector;
	}
	sortedValuesList=lista_valores;
	return lista_valores;
    }
    
    private double[] elimina_repetidos(double[]v,int atributo){
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
	num_valores[atributo]=contador;
	return aux;//v=aux;
    }
    
    /**
     * <p>
     * Creates a matrix training set, stored for each class, each attribute, and each value
     * the number of examples of class C that have value V for the attribute A COUNT[C,V,A]
     * </p>
     * @return int [][][] A matrix with the number of examples
     */
    public int [][][] creaCount(){
    	int [][][] Count=new int [nclases][nentradas] [ndatos];;
	//para definir la matriz Count, nvalores podria ser en el peor caso
	// el numero de instancias, es decir, no se repite ningun valor para algun atributo
	//ndatos es IS.getNumInstances();
	//obtener un array ordenado de los valores de un atributo sin repetirse
	 //esta seria la nueva funcion getNumValues, la de antes no sirve
	 //buscar el valor actual en este vector y devolver el indice
	double valor_actual;String nominal_actual;int indice;
	int []num_valores=new int [nentradas];
	int []tipos=new int [nentradas];
	num_valores=getNumValues();
	for (int i=0; i<nclases;i++) {for (int j=0; j<nentradas;j++){
		     for (int k=0; k<ndatos;k++)Count[i][j][k]=0;
	}}	 
	for (int i=0; i<nclases;i++) {
		for (int j=0; j<nentradas;j++){
		     tipos=tiposVar();
		     for (int k=0; k<ndatos;k++){
			//System.out.println("clase "+C[k]);
			if(C[k]==i){//esta instancia es de la clase actual i
			  valor_actual=X[k][j];nominal_actual=X2[k][j];
			  //System.out.println("valor actual es "+X[k][j]);
			 // System.out.println("valor actual NOMINAL es "+X2[k][j]);
			 //El atributo es nominal
			  if(tipos[j]==0)Count[i][j][(int)valor_actual]++;
			  //para los numericos es cuando tenemos que ordenar
			  else{
				indice=buscar(sortedValuesList[j],valor_actual);
				if(indice==-1)System.err.println("Error: el valor no se encontro en el vector");
			  	Count[i][j][indice]++;
			   }// System.out.println("la clase atributo y valor "+i+" "+j+" "+(int)valor_actual+" lleva "+Count[i][j][(int)valor_actual]);
			}
	}}}//de los 3for
	return Count;
    }

        /**
     * <p>
     * Look for an element in a sorted vector.
     * Returns the index where found it, returns -1 otherwise.
     * </p>
     */
    private int buscar(double[]v,double valor){
	boolean encontrado=false;
	int indice=-1;
    	for(int i=0;i<v.length&&(!encontrado);i++){
		if(v[i]==valor){encontrado=true;indice=i;}
	}
	return indice;
    }
    
    /**
     * <p>
     * Returns a vector with the optimum class for each pair attribute-value
     * </p>
     * @param Count each pair attribute-value
     * @param seed seed.
     * @return int [][] vector with the optimum classes
     */
    public int [][]getClaseOptima(int [][][]Count,long seed){
    	int [][]optima=new int[nentradas] [ndatos];
	int []vector=new int[nclases];
    	for (int i=0; i<nentradas;i++){
	    for (int j=0; j<num_valores[i];j++){
	    	for(int k=0;k<nclases;k++)vector[k]=Count[k][i][j];
		optima[i][j]=getMaximo(vector,seed);
	    }
	}
	return optima;
    }
    
    /**
     * <p>
     * Returns the index where is the maximum in an array of integers
     * </p>
    * @return int index where is the aximum value
    */
    private int getMaximo(int []num, long seed){
    	Randomize.setSeed(seed);
    	int max=num[0];int indice=0;
	int []opciones=new int[nclases];int contador=0;
	opciones[contador]=0;contador++;
    	for(int i=1;i<num.length;i++){
	       if(num[i]>max){max=num[i];indice=i;contador=0;opciones[contador]=i;contador++;}
		else{if(num[i]==max){opciones[contador]=i;contador++;}}
	}
	if((contador-1)>0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
		indice=Randomize.RandintClosed(0, contador);
		indice=opciones[indice];
	}
	return indice;
    }
    
    /**
     * <p>
     * Returns the index where is the maximum in an array of doubles
     * </p>
     * @param num array of doubles given.
     * @param seed seed
    * @return int index where is the aximum value
    */
    public int getMaximo(double []num, long seed){
    	Randomize.setSeed(seed);
    	double max=num[0];int indice=0;
	int []opciones=new int[nentradas];int contador=0;
	opciones[contador]=0;contador++;
    	for(int i=1;i<num.length;i++){
	       if(num[i]>max){max=num[i];indice=i;contador=0;opciones[contador]=i;contador++;}
		else{if(num[i]==max){opciones[contador]=i;contador++;}}
	}
	if((contador-1) > 0){//es que hay mas de una clase que es optima, la elegimos aleatoriamente
		indice=Randomize.RandintClosed(0, contador);
		System.out.println("Elegimos "+indice);
		indice=opciones[indice];
	}
	return indice;
    }
    
    /**
     * <p>
     * Returns for each attribute the number of different values 
     * </p>
     * @return int [] an array with the number of different values
     */
    public int []getNumValues(){
    	int []num=new int [nentradas];
    	for(int i=0;i<nentradas;i++){
		num[i]=0;
		for (int j=1;j<ndatos;j++){
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
	return num_valores;
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
    
    /**
     * <p>
     * Returns the values for the out-put(class)
     * </p>
     * @return int[] An array with the values of the class
     */
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
     * Return the number of input variables
     * </p>
     * @return int Total of the input variables
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
     * Checks if one attribute is missing or not
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
	    num_valores=new int[nentradas];
	    sortedValuesList=new double[nentradas] [ndatos];

            // Maximum and minimum of inputs
            emaximo = new double[nentradas];
            eminimo = new double[nentradas];

            // All values are casted into double/integer
            nclases = 0;
	    int posiblesClases;
	    boolean clasesQueAparecen[];
	    Vector nose;
	    
	    Attribute at = Attributes.getOutputAttribute(0);
		posiblesClases=at.getNumNominalValues();
		nose=at.getNominalValuesList();
		int []auxiliar = new int[posiblesClases];
	    	auxiliar2=new String[posiblesClases];
		System.out.println("posibles "+posiblesClases);
		clasesQueAparecen=new boolean[posiblesClases];
		for(int j=0;j<posiblesClases;j++)clasesQueAparecen[j]=false;
		
            for (int i = 0; i < ndatos; i++) {
                Instance inst = IS.getInstance(i);
                for (int j = 0; j < nentradas; j++) {
                    X2[i][j] = IS.getInputNominalValue(i, j); //inst.getInputRealValues(j);
		    X[i][j] = IS.getInputNumericValue(i, j);
		   // System.out.println(X[i][j]);
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
		    int num=  (int)IS.getOutputNumericValue(i, 0);
		    //marcamos esta clase como aparecida
		    clasesQueAparecen[num/*C[i]*/]=true;
		    C[i] =  (int)IS.getOutputNumericValue(i, 0);
                    C2[i] =  IS.getOutputNominalValue(i, 0); //(int)inst.getOutputRealValues(i);
		    
                }
		//System.out.println("valor leido=" + C[i]+" "+C2[i]);
                /*if (C[i] > nclases) { //ESTO NO ES BUENA SOLUCION 
                    nclases = C[i]; 
                }*/
            }
	    //del vector clasesAparecen solo nos quedamos con los valores true
	    int indiceaux=0;
	    for(int i=0;i<posiblesClases;i++){//System.out.println(clasesQueAparecen[i]);
	    	if(clasesQueAparecen[i]==true){
			auxiliar[indiceaux]=i;auxiliar2[indiceaux]=(String)nose.elementAt(i);
			indiceaux++;
		}
	    }
           // nclases++;
	   nclases=indiceaux;
	   //auxiliar contiene el indice de la clase y auxiliar2 el valor en nominal
	  /* for(int i=0;i<nclases;i++){
	   	System.out.println("holaaa" + auxiliar[i]+" "+auxiliar2[i]);}*/
	   
            System.out.println("Number of classes=" + nclases);
	    //IMPRIME TODOS LOS ATRIBUTOS Y TODAS LAS INSTANCIAS
	   // IS.print();
	   
	   //AHORA C NECESITA LA NUEVA NUMERACION, SOLO LAS CLASES QUE APARECEN EN LAS 
	   //INSTANCIAS, NO TODAS LAS POSIBLES EN @ATTRIBUTE, ORDENADAS
	   for(int i=0;i<nclases;i++){
	   	for (int j = 0; j < ndatos; j++) {
		if(auxiliar2[i].compareTo(IS.getOutputNominalValue(j, 0))==0) {
			C[j]=i;}//System.out.println(C[j]+" ahora es "+i);}
	   }
	   }
	  // for (int j = 0; j < ndatos; j++)System.out.println("ahora leido=" + C[j]+" "+C2[j]);

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
            if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
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
	System.out.println(+at.getNumNominalValues());
        if (at.getType() == at.NOMINAL){
            for (int i = 0; i < nclases; i++) {
                salida[i] = auxiliar2[i];/*YA NO ES ESTO at.getNominalValue(i);*/
            }
        }
        else{
            salida = null; //luego guardara el valor de las clases numericas
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

}

