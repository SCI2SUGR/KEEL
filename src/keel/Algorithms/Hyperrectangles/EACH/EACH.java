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

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import keel.Dataset.*;
import java.util.*;
import java.lang.*;
import java.text.DecimalFormat;

import org.core.*;


public class EACH {
/**
 * <p>
 * Main methods of thw EACHsd algorithm
 * </p>
 */
	
	private Dataset train ; 
	private Dataset test;
	private String outFile;
    private String outTrainFile;
    private String outTestFile;
	private String []classes=null;
	private String []testClasses=null;
	private String []nameAttributes=null;
	private RuleSet rulesSet;
	private Selector s;
	private Complex rule;
	private EachDataSet dataTrain,testData;
	// To evaluate the rules
	private RuleQualityEvaluation evalRule;
	private int sampleForClassEvaluation[];
    private int sampleForTestClass[];
	private long seed;
	// nominal(0) or numeric(1)
	private int []types;
	private InstanceSet IS;
	private int size; 
	private Hyperrectangle H[];
	private int numH; 
	private int match1,match2;
	private HyperrectangleSet Hset;
	private int choseen[];
	private double before[][]=null;
	private double dataWithoutNor[][]=null;
	private Hyperrectangle hyp;
	
	public EACH(){
	}
	
	/**
	 * <p>
	 * Constructor
	 * </p>
	 * @param fTrain Train file
	 * @param fTest Test file
	 * @param fOutTrain Out-put file Train
	 * @param fOutTest Out-put file Test 
	 * @param fOut Out-put file
	 * @param seed Seed
	 * @param delta Delta
	 * @param secondChance Second chance
	 */
	public EACH(String fTrain,String fTest,
               String fOutTrain,
               String fOutTest, String fOut,long seed,double delta, int secondChance){
	
	outFile = fOut;outTrainFile = fOutTrain;
        outTestFile = fOutTest;
		
	dataTrain = new EachDataSet();
	testData = new EachDataSet();
	
	train=new Dataset();
	test=new Dataset();
	
	//s=new Selector(0,0,0.);
	this.seed=seed;
	size=1;
		
	try {
	train.readSet(fTrain, true);
	test.readSet(fTest,false);
	train.computeMostComon();//eval.calculaMasComunes();
	test.computeMostComon();
	dataTrain = createSet(train); 
	testData = createSet(test); 
		
	rulesSet=new RuleSet();
	types=new int [train.getNInPuts()];
	types=train.typesVariable();
	classes=train.giveClasses();
	testClasses=test.giveClasses();
	nameAttributes=train.getNames();
	
	double[][]lista;//almacenamos para cada atributo la lista ordenada de valores
	lista=train.getListValues();
	
	H=new Hyperrectangle[train.getNData()/*size*/];
	/*elegidos=new int[size];//esto antes de inicializar la memoria
	inicializaMemoria(size);*/
	numH=size;//inicialmente numH es 1
	int []desordenadas=ramdomDataGeneration();//desordenamos las instancias en el train set
	dataWithoutNor=new double[train.getNData()][];
	for(int i=0;i<train.getNData();i++)dataWithoutNor[i]=new double[train.getNInPuts()];
	before=train.getX();
	for(int i=0;i<train.getNData();i++){
		for(int j=0;j<train.getNInPuts();j++)dataWithoutNor[i][j]=before[i][j];
	}
	
	train.normalize();//Convierte todos los valores del conjunto de datos en el intervalo [0,1] a la hora de facilitar los calculos de las distancias
	
	IS=train.getInstanceSet();
	//la memoria inicial solo contiene un H point
	H[0]=new Hyperrectangle(train.getNInPuts(),train.getX(desordenadas[0]),train.getC(desordenadas[0]),desordenadas[0]);
	IS.getInstance(desordenadas[0]).print();System.out.println();
	for(int i=1;i<train.getNData();i++)H[i]=new Hyperrectangle();
	
	
	int matches[]=new int[2];
	int claseH1,claseH2,claseE;
	double pesos[]=new double[train.getNInPuts()];
	for(int i=0;i<train.getNInPuts();i++)pesos[i]=1.;
	Hset=new HyperrectangleSet(H,train.getNInPuts(),train.getNData(),size,delta);
	//Hset.setMaxMinValuesAtrib(train.getemaximo(),train.geteminimo());
	Hset.setWeightAtrib(pesos);
	for(int i=0;i<train.getNInPuts();i++)
		System.out.println(train.getMinimum()[i]+" "+train.getMaximum()[i]);
	//main loop
	/*empezamos en 1 pq la primera instancia ya se ha clasificado*/
	for(int i=1;i<train.getNData();i++){
		//MATCHING PROCESS
		//find the two closest matches to the new example
		//obtenemos los indices de los 2 H mas cercano al ejemplo actual i
		matches=Hset.distance(train.getX(desordenadas[i]));
		match1=matches[0];match2=matches[1];
		claseH1=H[matches[0]].getClassAttribute();
		claseH2=H[matches[1]].getClassAttribute();
		claseE=train.getC(desordenadas[i]);
		//H[matches[0]].print();System.out.print(",");H[matches[1]].print();
		//System.out.println("CLASES "+claseE+" "+claseH1+" "+claseH2);
		
		//si se produce un matching con H1
		if(claseE==claseH1){
			System.out.println("MATCH CON prime "+match1+" que es : "/*+H[matches[0]].getNumInstance()*/);
			hyp=Hset.getHyperrectangle(match1);
			hyp.print();
			IS.getInstance(desordenadas[i]).print();System.out.println();
			//adjust weight for success
			//generalize exemplar
			H[match1].adjustWeightSuccess();
			H[match1].generalizeExemplar(train.getX(desordenadas[i]),desordenadas[i]);
		}
		else{
			//adjust weight for failure
			H[match1].adjustWeightFailure();
			
			if(secondChance==1){
			
				if(claseE==claseH2){
				System.out.println("MATCH CON l sgunda "+match2/*+H[match2].getNumInstance()*/);
				hyp=Hset.getHyperrectangle(match2);
				hyp.print();
				IS.getInstance(desordenadas[i]).print();System.out.println();
				//adjust weight for success and generalize exemplar
				H[match2].adjustWeightSuccess();
				H[match2].generalizeExemplar(train.getX(desordenadas[i]),desordenadas[i]);
				}
				else{
				//adjust weight for failure
				H[match2].adjustWeightFailure();
				//store the example as a new exemplar
				H[numH]=new Hyperrectangle(train.getNInPuts(),train.getX(desordenadas[i]),train.getC(desordenadas[i]),desordenadas[i]);
				Hset.store_in_memory(H[numH]);
				numH++;
				System.out.println("no match, crear un nuevo H, soy "+desordenadas[i]);
				IS.getInstance(desordenadas[i]).print();System.out.println();
				Hset.adjustFeatureWeights(train.getX(desordenadas[i]),match1);
				}
			}//del if greedy false
			
			else{//VARIANTE GREEDY
			//SI ERROR(CLASEe!=CLASEH1)DIRECTAMENTE CREAR UN NUEVO H
				H[numH]=new Hyperrectangle(train.getNInPuts(),train.getX(desordenadas[i]),train.getC(desordenadas[i]),desordenadas[i]);
				Hset.store_in_memory(H[numH]);
				numH++;
				System.out.println("no match, crear un nuevo H, soy "+desordenadas[i]);
				IS.getInstance(desordenadas[i]).print();System.out.println();
				Hset.adjustFeatureWeights(train.getX(desordenadas[i]),match1);
			}
		}
		//}//del if
	}
	//creamos una regla por cada H
	for(int i=0;i<numH;i++){
		//AHORA PODEMOS CALCULAR EL VOLUMEN DE CADA H
		H[i].calculeVolume();
		rule=createRule(i);
		complexEvaluation(rule,dataTrain);
		rulesSet.addRule(rule);
	}
	rulesSet.adjuntClassNames(classes);
        rulesSet.adjuntClassName(nameAttributes[train.getNInPuts()]);
	//conjunto_reglas.print(1);
	
	
	//EVALUAMOS LA CALIDAD DE LAS REGLAS
	int [] clasesEval;
	clasesEval = train.getC();
	sampleForClassEvaluation = new int[train.getNClasses()];
	for (int j = 0; j < train.getNClasses(); j++) {
		sampleForClassEvaluation[j] = 0;
		for (int i = 0; i < dataTrain.size(); i++) {
			if (/*valorClases[j]*/j == clasesEval[i]) {
			sampleForClassEvaluation[j]++;
			}
	}}
	
	clasesEval = test.getC();
	sampleForTestClass = new int[test.getNClasses()];
	for (int j = 0; j < test.getNClasses(); j++) {
		sampleForTestClass[j] = 0;
		for (int i = 0; i < testData.size(); i++) {
			if (/*valorClases[j]*/j == clasesEval[i]) {
			sampleForTestClass[j]++;
			}
	}}
		
	evalRule = new RuleQualityEvaluation(rulesSet, dataTrain, testData,
			sampleForClassEvaluation, sampleForTestClass,classes,testClasses);
	//GENERAMOS LA SALIDA
	generaSalida();
		
	}
	catch (IOException e) {
		System.err.println("There was a problem while trying to read the dataset files:");
		System.err.println("-> " + e);
		}
}

   /**
	* <p>
	* Desordenar aleatoriamente un vector de numeros
    * </p>
 	* @return v, el vector desordenado 
	*/
	private int[] ramdomDataGeneration(){
		int aux,indice;
		int []v=new int [train.getNData()];
		Randomize.setSeed(seed);
		for(int i=0;i<train.getNData();i++)v[i]=i;
		for(int i=0;i<train.getNData()-1;i++){
			indice=Randomize.Randint(i+1,train.getNData());//desde i+1 a ndatos-1
			//intercambiamos los valores de estas posiciones
			aux=v[i];v[i]=v[indice];v[indice]=aux;
		}
		return v;
	}
	
	
	
	/*Devuelve true si el elemento buscado ya esta en el vector
	*Vamos a usar esta fucion para detectar si una instancia ya se ha incluido en la 
	*memoria inicial, para no volverla a incluir
	*/
	private boolean yetChossen(int []vector, int util,int buscado){
		boolean encontrado=false;
		for(int i=0;i<util;i++){
			if(vector[i]==buscado)encontrado=true;
		}
		return encontrado;
	}
	
	
	/*Crea una regla para un hyperrectangulo
	*El consecuente de la regla es la clase de H
	*return la regla creada 
	*/
	private Complex createRule(int id){
		Complex Regla;
		int []v=new int[2];
		double []v2=new double[2];
		String []nomi=new String[2];
		Regla=new Complex(train.getNClasses());
		Regla.setClassAttribute(H[id].getClassAttribute());
		Regla.addNameAttributes(nameAttributes);
		Regla.setWeight(H[id].getWeight());
		Regla.setVolume(H[id].getVolume());
		Regla.setDimensions(H[id].getDimensions());
		Selector s;
		for(int i=0;i<train.getNInPuts();i++){
			v=H[id].getLowerAndUpperValues(i);
			v2[0]=dataWithoutNor[v[0]][i];
			v2[1]=dataWithoutNor[v[1]][i];
			nomi[0]=train.findNominalValue(i,before[v[0]][i]);
			nomi[1]=train.findNominalValue(i,before[v[1]][i]);
			if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL){
				//SI ES UN HOLE CREAR EL SELECTOR SOLO CON UN VALOR, NO CON DOS
				//if(H[id].getNumEjemplos()==1){
				//no es el numero de puntos sino el numero de valores
				//distintos en cada selector, o 1 o 2
				if(v[0]==v[1]){
				s=new Selector(i,0,nomi,v2,1);}
				else {s =new Selector(i,0,nomi,v2,2);}
			}
			else{
				if(v[0]==v[1]){
				s=new Selector(i,0,v2,1);}
				else {s =new Selector(i,0,v2,2);}
			}	
			Regla.addSelector(s);
		}
		//ahora nuestras reglas las hemos definido de forma diferente
		//el conjunto de valores de un selector ahora solo son 2 valores, el minimo
		//y el maximo.
		return Regla;
	}
	
	
	
   /** 
	* 
	* <p>
	* Evaluacion de los complejos sobre el conjunto de ejemplo para ver cuales se
	* cubren de cada clase
	* </p>
	* @param c Complejo a evaluar
	* @param e Conjunto de datos
	*/
	private void complexEvaluation(Complex c, EachDataSet e) {
		c.removeDistribution();
		for (int i = 0; i < e.size(); i++) {
		int cl = e.getData(i).getClassSelector();
	
		if (c.isCovered(e.getData(i))) {
			c.incrementDistribution(cl);
		}
		}
		c.computeLaplace();
	}
	
	/**
     * Calcula los datos estadï¿½ticos necesarios y crea los ficheros KEEL de salida
     */
	private void generaSalida() {
		Fichero f = new Fichero();
		String cad = "";
		String miSalida = new String("");
        	miSalida = train.copyHeaderTest();
		int []num_valores=new int [train.getNInPuts()];
		//getNumValues2 solo se puede llamar despues de getListValues();
		num_valores=train.getNumValues2();
		
		rulesSet.adjuntClassNames(classes);
        	rulesSet.adjuntClassName(nameAttributes[train.getNInPuts()]);
		cad=rulesSet.printString(num_valores);
		cad +=Hset.printWeightsAtributes(nameAttributes);
		cad += "\n\n" + evalRule.printString();
		f.escribeFichero(outFile, cad);
		f.escribeFichero(outTrainFile,
                         miSalida + evalRule.out(dataTrain,true));
        	f.escribeFichero(outTestFile,
                         miSalida + evalRule.out(testData,false));
        
	}
	
	/**
     	* Crea un conjunto de datos (atributos/clase) segun los obtenidos de un fichero de datos
     	* @param mis_datos Debe ser un conjunto de datos leido del fichero (mirar doc Dataset.java)
     	* @return El conjunto de datos ya creado, es decir, una lista enlazada de muestras (consultar ConjDatos.java y Muestra.java)
     	*/
	private EachDataSet createSet(Dataset mis_datos) {
        EachDataSet datos = new EachDataSet(); //Creo un nuevo conjunto de datos
        int tam = mis_datos.getNInPuts(); //Pillo el nmero de atributos de entrada (suponemos una sola salida [clase])
        double[] vars = new double[tam]; //Creamos el vector que guardarï¿½los valores de los atributos (aun siendo enteros o enum)
        double[][] X;
        int[] C;
        int clase = 0; //Variable que contendrï¿½el valor para la clase
        X = mis_datos.getX();
        C = mis_datos.getC();
        for (int i = 0; i < mis_datos.getNData(); i++) {
            //System.out.print("\n"+i+":");
            for (int j = 0; (j < tam); j++) {
                //System.out.print(" "+X[i][j]);
                if (mis_datos.isMissing(i, j)) {
                    vars[j] = mis_datos.mostCommon(j);
                } else { //CAMBIAR POR OTROS METODOS DE MANEJO DE VALORES PERDIDOS (15-NN).
                    vars[j] = X[i][j]; //Double.parseDouble(mis_datos.getDatosIndex(i, j)); //pillo el valor del atributo j para el ejemplo i
                }
            }
            //if (!salir) {
            clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
            Sample m = new Sample(vars, clase, tam); //Creo un nuevo dato del conjunto con sus variables, su clase y el num variables
            m.setPosFile(i);
            datos.addData(m);
            //}
        }
        return datos;
    }
}
    
    

