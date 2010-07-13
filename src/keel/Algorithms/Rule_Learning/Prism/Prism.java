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
 * @author Written by Rosa Venzala (University of Granada)  02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Prism;

import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import keel.Dataset.*;
import java.util.*;

import org.core.*;


public class Prism {
/**
 * <p>
 * Contents the principal methods of the Prismsd algorithm
 * </p>
 */
	
	
	private Dataset train ; 
	private Dataset test; 
	private String ficheroSalida;
    private String ficheroSalidaTr;
    private String ficheroSalidaTst;
	private String [][]valores=null;private String []clases=null;private int []clasitas=null;
	private boolean reglaPerfecta,cubierta;
	private InstanceSet instancias;
	private String []nombre_atributos=null;
	private double accuracy=-1.;private double accuracy_ant=-1.;
	private int clase,seleccionado=0,p=0,atributo,num_cubiertas,num_correctas;
	private ConjReglas conjunto_reglas;
	private Selector s;
	private Complejo almacen,regla;
	private boolean hayContinuos = false;
	private ConjDatos datosTrain,datosTest;
	private EvaluaCalidadReglas evReg; // Para evaluar la calidad de las reglas
	private int muestPorClaseEval[];
    	private int muestPorClaseTest[];
	private Instance instancia;
	private long seed;

	/**
	 * <p>
	 * Checks if there're sets or not.
	 * </p>
	 * @return OK(there're sets) or not OK
	 */
    public boolean todoBien(){
         return (!hayContinuos);
    }
	
	public Prism(){
	}
	
	/**
	 * <p>
	 * Constructor with all the attributes to initialize
	 * </p>
	 * @param ficheroTrain Train file
	 * @param ficheroTest Test file
	 * @param fSalidaTr Out-put train file
	 * @param fSalidaTst Out-put test file
	 * @param fsalida Out-put file
	 * @param semilla seed
	 */
	public Prism(String ficheroTrain,String ficheroTest,
               String fSalidaTr,
               String fSalidaTst, String fsalida,long semilla){
	
	  	ficheroSalida = fsalida;ficheroSalidaTr = fSalidaTr;
        	ficheroSalidaTst = fSalidaTst;
		seed=semilla;
		
		datosTrain = new ConjDatos();//datosEval = new ConjDatos();
		datosTest = new ConjDatos();
	
		train=new Dataset();test=new Dataset();
		s=new Selector(0,0,0.);
		conjunto_reglas=new ConjReglas();
		
		try {
		Randomize.setSeed(seed);
		System.out.println("la semilla es "+seed);
		train.leeConjunto(ficheroTrain, true);
		test.leeConjunto(ficheroTest,false);//
            	if (train.hayAtributosContinuos()/*|| train.hayAtributosDiscretos()*/){
                System.err.println("\nPrism may not work properly with real or integer attributes.\n");
                //System.exit(-1);
                hayContinuos = true;
            	}
		if(!hayContinuos){
		train.calculaMasComunes();//eval.calculaMasComunes();
		test.calculaMasComunes();
		datosTrain = creaConjunto(train); //Leemos los datos de entrenamiento (todos seguidos como un String)//datosEval = creaConjunto(eval);
		datosTest = creaConjunto(test); 
		
		valores=train.getX2();//obtengo los valores nominales
		clases=train.getC2();
		clasitas=train.getC();
		/*System.out.println(train.getndatos());
		System.out.println(train.getnentradas());
		for(int i=0;i<train.getndatos();i++){
			for(int j=0;j<train.getnentradas();j++)
				System.out.print(valores[i][j]);
			System.out.print(clases[i]);System.out.println(clasitas[i]);}*/
		//COMENZAMOS EL ALGORITMO PRISM
		//FOR EACH CLASS C
		clases=train.dameClases();
		int unavez=0,candidato;
		for(int i=0;i<train.getnclases();i++){
		  System.out.println("CLASE :"+clases[i]+"\n");
			//initialize E to the instance set
			/*Cuando haya que inicializar de nuevo el conjunto de instancias no es necesario insertar aquellas que se eliminaron, sino que nos va a bastar con inicializar otra vez el conjunto mediante el fichero de entrenamiento. Por eso hay un metodo para insertar una instancia*/
		  train.leeConjunto(ficheroTrain,false);
		  nombre_atributos=train.dameNombres();
		  instancias=train.getInstanceSet();
			
		  //While E contains instances in class C
		  while(train.hayInstanciasDeClaseC(i)){
			//Create a rule R with an empty left-hand side that predicts class C
			regla=new Complejo(train.getnclases());
			regla.setClase(i);
			regla.adjuntaNombreAtributos(nombre_atributos);
			//esto lo hacemos solo aqui pq luego vamos quitando selectores del almacen
			almacen=hazSelectores(train);
			almacen.adjuntaNombreAtributos(nombre_atributos);
			do{
			   //FOR EACH ATTRIBUTE A NOT MENTIONED IN R, AND EACH VALUE V
			   accuracy_ant=-1.;p=0;
			   int seleccionados[]=new int[almacen.size()];
			   	for(int jj=0;jj<almacen.size();jj++)
					seleccionados[jj]=0;	
			   System.out.println();
			   for(int j=0;j<almacen.size();j++){
				//tenemos que quitar el selector anterior
				if(j>0)regla.removeSelector(s);
				s=almacen.getSelector(j);
				//if(i==0)
				s.print();
				//CONSIDER ADDING THE CONDITION A=V TO THE LHS OF R
				regla.addSelector(s);
				accuracy=getAccuracy(i);
				//if(i==0)	{
				System.out.println("correctas "+num_correctas+" cubiertas "+num_cubiertas);	
				System.out.println("Acurracy "+accuracy);
				//}
				
				if( (accuracy>accuracy_ant)||((accuracy==accuracy_ant) &&(num_correctas>p)) ){
				
					//if((accuracy==accuracy_ant) &&(num_correctas>p)){
					//System.out.println("atn "+accuracy_ant);
					//System.out.println("ahora "+accuracy);
				        accuracy_ant=accuracy;
					seleccionado=j;
					p=num_correctas;
					
					//si se encuentra un superior hay que quitar
					//todo lo q se hay ido almacenando en esta iteracion
					for(int jj=0;jj<almacen.size();jj++)
					seleccionados[jj]=0;	
					//}
				}
				else{ 
				  if((accuracy==accuracy_ant) &&(num_correctas==p)){
					seleccionados[seleccionado]=1;
					seleccionados[j]=1;
				   }
				 }	
			   }
			   //seleccionamos uno de los seleccionados en el caso de empate
			   int contador=0;
			   for(int jj=0;jj<almacen.size();jj++){
					if(seleccionados[jj]==1){
						contador++;
						System.out.println("OPCION "+jj);
					}
			   }
			   if(contador>0){
			   	candidato=Randomize.RandintClosed(1, contador);
				contador=0;
				for(int jj=0;jj<almacen.size();jj++){
					if(seleccionados[jj]==1){
						contador++;
						if(contador==candidato)seleccionado=jj;
					}
			   	}
			   }
			   System.out.println("ELEGIDO "+seleccionado);
			   
			   //antes hay que quitar el q metimos
			   regla.removeSelector(s);
			   s=almacen.getSelector(seleccionado);
			   s.print();
			   //ADD A=V TO R
			   regla.addSelector(s);
			   /*AHORA HAY QUE QUITAR DEL ALMACEN SE SELECTORES AQUELLOS QUE 
			   HACEN REFERENCIA AL ATRIBUTO SELECCIONADO*/
			   //obtener el atributo del selector ganador
			   atributo=s.getAtributo();
			   //se borran todos los q tengan ese atributo
			   //System.out.println("ALMACEN");almacen.print();
			   almacen.removeSelectorAtributo(atributo);
				
			   reglaPerfecta=perfectRule(regla,train);	
			}while(!reglaPerfecta && (regla.size() < train.getnentradas()));
			
			System.out.println("\n");
			System.out.println("\nREGLA............................................");
			regla.print();
			System.out.println("\n");
			/*necesitamos evaluar la regla para obtener la salida del metodo
			para compararla con la salida esperada*/
			evaluarComplejo(regla,datosTrain);
			//INCLUIMOS ESTA REGLA YA PARA EL CONJUNTO FINAL DE REGLAS
			conjunto_reglas.addRegla(regla);
			//REMOVE THE INSTANCES COVERED BY R FROM E
			
			//Instance instancia;
			/*for(int k=0;k<instancias.getNumInstances();k++){
			instancia=instancias.getInstance(k);
			System.out.print(k+" ");
		   	instancia.print();
			System.out.println();
		   	}*/
			 removeInstancesCovered(i);
			 for(int k=0;k<instancias.getNumInstances();k++){
			instancia=instancias.getInstance(k);
			clase=instancia.getOutputNominalValuesInt(0);
		        if(clase==i){
			System.out.print(k+" ");
		   	instancia.print();
			System.out.println();
			}
		   	}
			//instancias.print();
			System.out.println("\n");
		}//del while
		}//del for de las clases
		
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
		}
		}
		conjunto_reglas.eliminaRepetidos(1);
		evReg = new EvaluaCalidadReglas(conjunto_reglas, datosTrain, datosTest,
                                        muestPorClaseEval, muestPorClaseEval,clases);
		//GENERAMOS LA SALIDA
		generaSalida();
		System.out.println("la semilla es "+seed);
		}//del if
		}catch (IOException e) {
		System.err.println("There was a problem while trying to read the dataset files:");
		System.err.println("-> " + e);
		//System.exit(0);
		}
	}
	
	/** 
	 * <p>
	 * Evaluation of the complex over the example's set for see witch match all the class
	 * </p>
     * @param c Complex to evaluate 
     * @param e Set of data
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
	 * <p>
	 * Returns the fraction of correct instances of the instance's set for the rule 'regla'
	 * </p>
	 * @param i Number of the rule
	 * @return Fraction of correct instances of the instance's set for the rule 'regla'
	 */
	private double getAccuracy(int i){
	
		Instance instancia;
		double Accuracy;
		num_cubiertas=0;
		num_correctas=0;
		for(int k=0;k<instancias.getNumInstances();k++){
			instancia=instancias.getInstance(k);
			cubierta=regla.reglaCubreInstancia(instancia);
			if(cubierta){
				num_cubiertas++;
				clase=instancia.getOutputNominalValuesInt(0);
				if(clase==i)num_correctas++;
			 }
		}
		Accuracy=(double)num_correctas/(double)num_cubiertas;
		if(num_cubiertas==0)Accuracy=0;
		return Accuracy;
	}
	
	
	/**
	 * <p>
	 * Removes from the instance's set those instances that matches with the rule
	 * </p>
	 * @param i Numebr of the rule
	 */
	private void removeInstancesCovered(int i){
	
	for(int k=0;k<instancias.getNumInstances();k++){

		instancia=instancias.getInstance(k);
		/*System.out.print(k+" ");
		   instancia.print();
		System.out.println();*/
		cubierta=regla.reglaCubreInstancia(instancia);
		if(cubierta){
			//  System.out.println("CUBIERTA");
			 clase=instancia.getOutputNominalValuesInt(0);
		       // if(clase==i){
			instancias.removeInstance(k);
			instancia.print();
			 System.out.println();
			 k=k-1;
			//}
		}
	}
		
	}
	
	/**
	 * <p>
	 * Calculates the necessary statistical data and creates KEEL out-put files
	 * </p>
     */
	private void generaSalida() {
		Fichero f = new Fichero();
		String cad = "";
		String miSalida = new String("");
        	miSalida = train.copiaCabeceraTest();
		//System.out.println("\n Estas son las reglas encontradas:");
		//conjReglasFinal.print();
		conjunto_reglas.adjuntaNombreClases(clases);
        	conjunto_reglas.adjuntaNombreClase(nombre_atributos[train.getnentradas()]);
		cad = conjunto_reglas.printString();
		/*cad += "\n\n" + evReg.printString() + "\n\n  Time (seconds); " +
			(tiempo / 1000);*/
		
		f.escribeFichero(ficheroSalida, cad);
	
		f.escribeFichero(ficheroSalidaTr,
                         miSalida + evReg.salida(datosTrain));
        	f.escribeFichero(ficheroSalidaTst,
                         miSalida + evReg.salida(datosTest));
	}
	
	/**
	 * <p>
	 * Creates a set of data(attribute/class)
	 * </p>
     * @param mis_datos Must be a set of data readen from the file (see doc Dataset.java)
     * @return The set of data created, a list of examples (see ConjDatos.java and Muestra.java)
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
    
   /**
    * <p>
    * Returns True if the rule is perfect for the data set.
    * </p>
    */  
    public boolean perfectRule(Complejo regla,Dataset train){
    	ConjDatos datosTrain;
	datosTrain=new ConjDatos();
	datosTrain=creaConjunto(train);
	boolean perfecta=false;
	
	/*Muestra m = datosTrain.getDato(3);//la primera instancia basicamente
	System.out.println(m.getClase());*/
	
	Muestra m;
	ConjDatos cubiertas;
	cubiertas=new ConjDatos();
	
	/*todas las instancias que cubra la regla las metemos en un conjunto*/
	for(int i=0;i<train.getndatos();i++){
		m=datosTrain.getDato(i);
		if(regla.cubre(m)){
			cubiertas.addDato(m);
		}
	}
	/*perfecta sera true si todos los ejemplos del conjunto 'cubiertas' tienen la misma clase que predice la regla*/
	for(int i=0;i<cubiertas.size();i++){
		if(cubiertas.getDato(i).getClase()!=regla.getClase()){perfecta=false;return perfecta;}
		else perfecta=true;
		
	}
	return perfecta;
	
    }
    
    /**
     * <p>
     * Creates the total selector's set for get all the possible rules
     * </p>
     */
    private Complejo hazSelectores(Dataset train) {
    
    	Complejo almacenSelectores;
    	int nClases=train.getnclases();
        almacenSelectores = new Complejo(nClases); //Aqui voy a almacenar los selectores (numVariable,operador,valor)
    	Attribute []atributos=null;
	int num_atributos,type;
	Vector nominalValues;
	atributos=Attributes.getAttributes();
	num_atributos=Attributes.getNumAttributes();
	Selector s;
	
	for(int i=0;i<train.getnentradas();i++){ 
		type=atributos[i].getType();
		switch (type){
			case 0://NOMINAL
			nominalValues=atributos[i].getNominalValuesList();
			//System.out.print("{");
			for (int j=0; j<nominalValues.size(); j++){
				//System.out.print ((String)nominalValues.elementAt(j)+"  ");
				s = new Selector(i, 0,(String)nominalValues.elementAt(j),true); //[atr,op,valor]
				//incluimos tb los valores en double para facilitar algunas funciones
				s.setValor((double)j);
				almacenSelectores.addSelector(s); 
				//s.print();
			}
			//System.out.println("}");
			break;
		}
		//System.out.println(num_atributos);
	}
	return almacenSelectores;
    }

}
