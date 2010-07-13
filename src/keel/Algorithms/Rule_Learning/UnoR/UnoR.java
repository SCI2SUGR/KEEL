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

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import keel.Dataset.*;
import java.util.*;

import org.core.Fichero;

/**
 * <p>Title: Clase UnoRSD</p>
 *
 * <p>Description: Contiene los metodos principales del algoritmo UnoRsd</p>
 *
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 *
 * <p>Company: Mi casa </p>
 *
 * @author Rosa Venzala
 * @version 1.0
 */
public class UnoR {
	
	private Dataset train ; //ficheroTrain);Dataset eval;
	private Dataset test; //ficheroTest);  }
	private String ficheroSalida;
        private String ficheroSalidaTr;
        private String ficheroSalidaTst;
	private String []clases=null;
	private String []clasesTest=null;//para cuando no coinciden los indices de las clases
					//como por ejemplo en abalone
	private String []nombre_atributos=null;
	private ConjReglas conjunto_reglas[];
	private Selector s;
	private Complejo regla;
	private ConjDatos datosTrain,datosTest;
	private EvaluaCalidadReglas evReg[]; // Para evaluar la calidad de las reglas
	private int muestPorClaseEval[];
    	private int muestPorClaseTest[];
	private long seed;
	private int ganador; //identifica al atributo finalmente ganador
	private int []tipos;//indica si el atributo de entrada es nominal(0) o numerico(1)

	
	public UnoR(){
	}
	
public UnoR(String ficheroTrain,String ficheroTest,
               String fSalidaTr,
               String fSalidaTst, String fsalida,long semilla,int SMALL){
	
	ficheroSalida = fsalida;ficheroSalidaTr = fSalidaTr;
        ficheroSalidaTst = fSalidaTst;
		
	datosTrain = new ConjDatos();datosTest = new ConjDatos();
	
	train=new Dataset();test=new Dataset();
	s=new Selector(0,0,0.);
	seed=semilla;
		
	try {
	train.leeConjunto(ficheroTrain, true);
	test.leeConjunto(ficheroTest,false);
	train.calculaMasComunes();//eval.calculaMasComunes();
	test.calculaMasComunes();
	datosTrain = creaConjunto(train); 
	datosTest = creaConjunto(test); 
		
	conjunto_reglas=new ConjReglas[train.getnentradas()];
	tipos=new int [train.getnentradas()];
	tipos=train.tiposVar();
	clases=train.dameClases();
	clasesTest=test.dameClases();
	/*for (int j=0; j<train.getnclases();j++)
			System.out.println(clases[j]);*/
		
	double[][]lista;//almacenamos para cada atributo la lista ordenada de valores
	lista=train.getListValues();
	int []num_valores=new int [train.getnentradas()];
	//getNumValues2 solo se puede llamar despues de getListValues();
	num_valores=train.getNumValues2();
		//en num_valores guardamos el numero de valores que tiene cada atributo
		/*for (int j=0; j<train.getnentradas();j++){
			System.out.println(num_valores[j]);
		     for (int k=0; k<num_valores[j];k++)System.out.print(lista[j][k]+" ");
		     System.out.println();
		}*/
		
	int [][][]matriz;
	matriz=train.creaCount();
		/*for (int i=0; i<train.getnclases();i++) {
		for (int j=0; j<train.getnentradas();j++){
		     for (int k=0; k<num_valores[j];k++)System.out.print(matriz[i][j][k]+" ");
		     System.out.println();
		 }
		 System.out.println();
		 }*/
		 
		 //ahora el siguiente paso es obtener la clase optima para cada atributo-valor
	 int [][]optima;
	 optima=train.getClaseOptima(matriz,seed);
		/* for (int j=0; j<train.getnentradas();j++){
		     for (int k=0; k<num_valores[j];k++)System.out.print(optima[j][k]+" ");
		     System.out.println();
		 }*/
		 
	/*ahora el siguiente paso es DISCRETIZAR, clasificar los valores de cada 
	 atributo en intervalos*/
	int optimalClass;
	 nombre_atributos=train.dameNombres();
	 for (int i=0; i<train.getnentradas();i++){
		conjunto_reglas[i]=new ConjReglas();
		Interval I1=new Interval(SMALL,train.getndatos());
		 for (int j=0; j<num_valores[i];j++){
		 	if(tipos[i]==0){//es nominal, no hay q hacer intervalos
				regla=createRule(i,train.findNominalValue(i,lista[i][j]),lista[i][j],optima[i][j]);
				conjunto_reglas[i].addRegla(regla);
				}
			else{
			 //tenems q obtener la clase optima provisional dl intervalo
			 optimalClass=I1.optimalClass(train.getnclases(),seed,false);
			 //todavia no hay mas de small valores,no hay clase optima
			 //o la clase de este nuevo valor coincide con la optima
			 if((I1.getNumValues()<=SMALL) || (optimalClass==-1)
			 ||(optima[i][j]==optimalClass)){
				I1.add(lista[i][j]);
				I1.setClass(optima[i][j],I1.getNumValues()-1);
			 }
			 else{//si coincide cn la clase optima lo metemos,si no, pasa a otro nuevo intervalo
				//System.out.println("El intervalo tiene "+I1.getNumValues());
				//I1.print();
				j--;//para q empiece con este valor que corto el intervalo
				regla=createRule(i,I1,false);
				//INCLUIMOS ESTA REGLA YA PARA EL CONJUNTO FINAL DE REGLAS
				conjunto_reglas[i].addRegla(regla);
				I1.clear();
			  }
			}
		}
		if(tipos[i]==1){//solo numerico, si se han creado intervalos
			//System.out.println(i+"El intervalo tiene ya fuera"+I1.getNumValues());
			//I1.print();
			//the rightmost interval  (true)
			regla=createRule(i,I1,true);
			//regla.print(tipos[i]);
			conjunto_reglas[i].addRegla(regla);
		}
	 }
		 
	//INTERVALOS CREADOS!!!!
	//POR CADA INTERVALO hemos CREAdo UNA REGLA
	
	//EVALUAMOS LA CALIDAD DE LAS REGLAS
	int [] clasesEval;
	clasesEval = train.getC();
	muestPorClaseEval = new int[train.getnclases()];
	for (int j = 0; j < train.getnclases(); j++) {
		muestPorClaseEval[j] = 0;
		for (int i = 0; i < datosTrain.size(); i++) {
			if (/*valorClases[j]*/j == clasesEval[i]) {
			muestPorClaseEval[j]++;
			}
	}}
	
	//TAMBIEN PARA TEST!! NO TIENEN POR QUE SER IGUALES!!!
	clasesEval = test.getC();
	muestPorClaseTest = new int[test.getnclases()];
	for (int j = 0; j < test.getnclases(); j++) {
		muestPorClaseTest[j] = 0;
		for (int i = 0; i < datosTest.size(); i++) {
			if (/*valorClases[j]*/j == clasesEval[i]) {
			muestPorClaseTest[j]++;
			}
	}}
		
	evReg=new EvaluaCalidadReglas[train.getnentradas()];
	double []aciertos=new double[train.getnentradas()];
	for (int i=0; i<train.getnentradas();i++){
		conjunto_reglas[i].adjuntaNombreClases(clases);
        	conjunto_reglas[i].adjuntaNombreClase(nombre_atributos[train.getnentradas()]);
		conjunto_reglas[i].print(tipos[i]);
		evReg[i] = new EvaluaCalidadReglas(conjunto_reglas[i], datosTrain, datosTest,muestPorClaseEval, muestPorClaseTest,clases,clasesTest);
		aciertos[i]=evReg[i].getAccuracyTrain();
	}
	//quedarnos con el maximo de aciertos y este sera el atributo final
	//GENERAMOS LA SALIDA
	ganador=train.getMaximo(aciertos,seed);
	generaSalida(evReg[ganador],ganador);
		
	}
	catch (IOException e) {
		System.err.println("There was a problem while trying to read the dataset files:");
		System.err.println("-> " + e);
		//System.exit(0);
		}
}
	
	/*Crea una regla para un atributo numerico dado un intervalo
	*El consecuente de la regla es la clase optima del intervalo
	*El antecedente son los valores del intervalo
	*@param rightmost es true si es el ultimo intervalo para el atributo atr
	*return la regla creada 
	*/
	private Complejo createRule(int atr,Interval I1,boolean rightmost){
		Complejo Regla;int op;
		Selector s = new Selector(atr, 0,I1.getValues(),I1.getNumValues());	
		Regla=new Complejo(train.getnclases());
		if(rightmost)op=I1.optimalClass(train.getnclases(),seed,true);
		Regla.setClase(I1.getOptimalClass());
		Regla.adjuntaNombreAtributos(nombre_atributos);
		Regla.addSelector(s);
		//Regla.print(tipos[atr]);//numerico o nominal
		evaluarComplejo(Regla,datosTrain);
		return Regla;
	}
	
	/*Crea una regla en el caso de q el atributo sea nominal*/
	private Complejo createRule(int atr,String nombre,/*String*/double valor,int claseOptima){
		Complejo Regla;
		Selector s = new Selector(atr, 0,nombre,valor,true);	
		Regla=new Complejo(train.getnclases());
		Regla.setClase(claseOptima);
		Regla.adjuntaNombreAtributos(nombre_atributos);
		Regla.addSelector(s);
		//Regla.print(tipos[atr]);//numerico-1 o nominal-0
		evaluarComplejo(Regla,datosTrain);
		return Regla;
	}
	
	
	/** Evaluacion de los complejos sobre el conjunto de ejemplo para ver cuales se
	*   cubren de cada clase
	* @param c Complejo a evaluar
	* @param e Conjunto de datos
	*/
	private void evaluarComplejo(Complejo c, ConjDatos e) {
		c.borraDistrib();
		for (int i = 0; i < e.size(); i++) {
		int cl = e.getDato(i).getClase();
	
		if (c.cubre(e.getDato(i))) {
			c.incrementaDistrib(cl);
		}
		}
		c.calculaLaplaciano();
	}
	
	/**
     * Calcula los datos estadï¿½ticos necesarios y crea los ficheros KEEL de salida
     */
	private void generaSalida(EvaluaCalidadReglas evReg,int atrib) {
		Fichero f = new Fichero();
		String cad = "";
		String miSalida = new String("");
        	miSalida = train.copiaCabeceraTest();
		//System.out.println("\n Estas son las reglas encontradas:");
		//conjReglasFinal.print();
		//clases=train.dameClases();
		conjunto_reglas[atrib].adjuntaNombreClases(clases);
        	conjunto_reglas[atrib].adjuntaNombreClase(nombre_atributos[train.getnentradas()]);
		cad = conjunto_reglas[atrib].printString(tipos[atrib]);
		cad += "\n\n" + evReg.printString() + "\n\n  Time (seconds); ";
		f.escribeFichero(ficheroSalida, cad);
	
		f.escribeFichero(ficheroSalidaTr,
                         miSalida + evReg.salida(datosTrain,true));
        	f.escribeFichero(ficheroSalidaTst,
                         miSalida + evReg.salida(datosTest,false));
        
	}
	
	/**
     	* Crea un conjunto de datos (atributos/clase) segun los obtenidos de un fichero de datos
     	* @param mis_datos Debe ser un conjunto de datos leido del fichero (mirar doc Dataset.java)
     	* @return El conjunto de datos ya creado, es decir, una lista enlazada de muestras (consultar ConjDatos.java y Muestra.java)
     	*/
	private ConjDatos creaConjunto(Dataset mis_datos) {
        ConjDatos datos = new ConjDatos(); //Creo un nuevo conjunto de datos
        int tam = mis_datos.getnentradas(); //Pillo el nmero de atributos de entrada (suponemos una sola salida [clase])
        double[] vars = new double[tam]; //Creamos el vector que guardarï¿½los valores de los atributos (aun siendo enteros o enum)
        double[][] X;
        int[] C;
        int clase = 0; //Variable que contendrï¿½el valor para la clase
        X = mis_datos.getX();
        C = mis_datos.getC();
        for (int i = 0; i < mis_datos.getndatos(); i++) {
            //System.out.print("\n"+i+":");
            for (int j = 0; (j < tam); j++) {
                //System.out.print(" "+X[i][j]);
                if (mis_datos.isMissing(i, j)) {
                    vars[j] = mis_datos.masComun(j);
                } else { //CAMBIAR POR OTROS METODOS DE MANEJO DE VALORES PERDIDOS (15-NN).
                    vars[j] = X[i][j]; //Double.parseDouble(mis_datos.getDatosIndex(i, j)); //pillo el valor del atributo j para el ejemplo i
                }
            }
            //if (!salir) {
            clase = C[i]; //Integer.parseInt(mis_datos.getDatosIndex(i, tam));
            Muestra m = new Muestra(vars, clase, tam); //Creo un nuevo dato del conjunto con sus variables, su clase y el num variables
            m.setPosFile(i);
            datos.addDato(m);
            //}
        }
        return datos;
    }
}
    
    
