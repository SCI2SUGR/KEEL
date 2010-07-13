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

/**
 * <p>Tï¿½ulo: Clase Dataset</p>
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
     * Devuelve los valores de los atributos de entrada
     * @return double[][] Un array con los atributos de entrada
     */
    public double[][] getX() {
        return X;
    }
    
    public String[][]getX2(){
    	return X2;
    }
    
    /**
     * Devuelve los valores de los atributos de entrada para una instancia determinada
     *@param pos La posicion de la instancia en el conjunto de datos
     * @return double[] Un array con los atributos de entrada para esa instancia
     */
    public double []getX(int pos){
    	return X[pos];
    }
    
    public InstanceSet getInstanceSet(){
    	return IS;
    }
    
    /*Devuelve el valor nominal del valor double del atributo
    */
    public String findNominalValue(int atr,double valor){
    	String dev="";
	boolean parar=false;
 	for (int i=0;i<ndatos&&!parar;i++){
	  	if(valor==X[i][atr]){dev=X2[i][atr];parar=true;}
	  }
	return dev;
    }
    /*
    *Comprueba si en el conjunto de instancias quedan aun instancias de una clase 
    * determinada
    @param La clase de la que se busca si hay instancias
    @return true si hay instancias, false en otro caso
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
     
    /*
    Obtiene para cada atributo la lista ordenada de sus posibles valores sin repetir
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
    * Crea un 3-D array a partir del training set, almacenando para cada clase, cada atributo
    * y cada valor el numero de ejemplos de clase C que tienen el valor V para el atributo A
    * COUNT[C,V,A]
    * @return int [][][] Una matriz con el numero de ejemplos
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
    /*
    busca un elemento en un vector previamente ordenado en orden ascendente
    Devuelve el indice donde se encuentra el valor
    Devuelve -1 si no se encuentra el valor
    */
    private int buscar(double[]v,double valor){
	boolean encontrado=false;
	int indice=-1;
    	for(int i=0;i<v.length&&(!encontrado);i++){
		if(v[i]==valor){encontrado=true;indice=i;}
	}
	return indice;
    }
    /*
    * Devuelve un vector con la clase optima para cada par atributo-valor
    * @return int [][]el vector con las clases optimas
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
    
    /*
    * Devuelve el indice donde se encuentra el maximo de un array de enteros
    * Si hay varios maximo devuelve uno aleatoriamente
    * @return int el indice donde se encuentra el maximo valor
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
    
    /*
    * Devuelve el indice donde se encuentra el maximo de un array de doubles
    * Si hay varios maximo devuelve uno aleatoriamente
    * @return int el indice donde se encuentra el maximo valor
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
    
    /*
    * Devuelve para cada atributo el numero de valores distintos que toma para el conjunto
    * de datos
    * @return int [] un array con el numero de valores distintos para cada atributo
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
    /*
    * Devuelve para cada atributo el numero de valores distintos que toma para el conjunto
    * de datos
    * @return int [] un array con el numero de valores distintos para cada atributo
    */
    public int []getNumValues2(){
	return num_valores;
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
    
    public String[] getC2() {
        String[] retorno = new String[C2.length];
        for (int i = 0; i < C2.length; i++) {
            retorno[i] = C2[i];
        }
        return retorno;
    }

    /**
     * Devuelve un array con los valores mï¿½imos de los atributos de entrada
     * @return double[] idem
     */
    public double[] getemaximo() {
        return emaximo;
    }

    /**
     * Devuelve un array con los valores mï¿½imos de los atributos de entrada
     * @return double[] idem
     */
    public double[] geteminimo() {
        return eminimo;
    }

    /**
     * Devuelve el nmero de datos
     * @return int el nmero de ejemplos
     */
    public int getndatos() {
        return ndatos;
    }

    /**
     * Devuelve el nmero de variables
     * @return int El nmero de variables (incluyendo entrada y salida)
     */
    public int getnvariables() {
        return nvariables;
    }

    /**
     * Devuelve el nmero de variables de entrada
     * @return int El total de variables de entrada
     */
    public int getnentradas() {
        return nentradas;
    }

    /**
     * Devuelve el nmero total de clases
     * @return int el nmero de clases distintas
     */
    public int getnclases() {
        return nclases;
    }

    /**
     * Comprueba si un atributo estï¿½"perdido" o no
     * @param i int Nmero de ejemplo
     * @param j int Nmero de atributo
     * @return boolean True si falta, False en otro caso
     */
    public boolean isMissing(int i, int j) {
        // True is the value is missing (0 in the table)
        return missing[i][j];
    }

    /**
     * Constructor. Crea un nuevo conjunto de instancias
     */
    public Dataset() {
        IS = new InstanceSet(); // Init a new set of instances
    }

    
    /**
     * Lee el fichero de ejemplos (train o test)
     * @param nfejemplos String Nombre del fichero de ejemplos
     * @param train boolean True si se refiere al conjunto de entrenamiento. False si es test
     * @throws IOException Un posible error de E/S
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
     * Devuelve un String con la cabecera del fichero
     * @return String Los datos de la cabecera del fichero (train)
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
     * Convierte todos los valores del conjunto de datos en el intervalo [0,1]
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
     * Devuelve los tipos de cada entrada (NOMINAL[0] o NUMERICO[1])
     * @return int[] Un vector que contiene 0 o 1 en funcion de si los atributos son nominales o numericos
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
     * Devuelve el valor mas comn del atributo i-esimo
     * @param i int Nmero de atributo
     * @return int Valor mas comnun para esta variable
     */
    public int masComun(int i) {
        return comunes[i];
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
            salida = null; //luego guardarï¿½el valor de las clases numï¿½icas
        }
        return salida;
    }

    /**
     * Comprueba si en la base de datos hay alguna entrada de tipo real o contï¿½ua
     * @return boolean True si existe alguna entrada continua. False en caso contrario
     */
    public boolean hayAtributosContinuos(){
        return Attributes.hasRealAttributes();
    }

}

