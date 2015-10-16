/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. SÃ¡nchez (luciano@uniovi.es)
    J. AlcalÃ¡-Fdez (jalcala@decsai.ugr.es)
    S. GarcÃ­a (sglopez@ujaen.es)
    A. FernÃ¡ndez (alberto.fernandez@ujaen.es)
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
* @author Written by Salvador Garcia (University of JaÃ©n) 6/06/2009
* @version 0.1
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Hyperrectangles.EHS_CHC;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Arrays;
import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;

import org.core.*;

public class EHS_CHC extends Metodo {

	/* Paths and names of I/O files */
	private String ficheroReferencia;

	/* Own parameters of the algorithm */
	private long semilla;
	private double alfa;
	private double beta;
	private double r;
	private double prob0to1Rec;
	private double prob0to1Div;
	private int tamPoblacion;
	private int nEval;
	public boolean filtering;
	private int K=3;
	
	/* Data structures */
	protected InstanceSet referencia;

	/* Data matrix */
	double datosReferencia[][];
	int clasesReferencia[];
	double datosTest[][];
	int clasesTest[];
        double distancias[][];


	/* Extra */
	boolean nulosTest[][];
	boolean nulosReferencia[][];
	int nominalTest[][];
	int nominalReferencia[][];
	double realTest[][];
	double realReferencia[][];

	public EHS_CHC(String ficheroScript) {

		/* Read of the script file */
		leerConfiguracion(ficheroScript);
                
		/* Read of data files */
		try {
			training = new InstanceSet();
			training.readSet(ficheroTraining, true);

			/* Normalize and check the data */
			normalizarTrain();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		try {
			test = new InstanceSet();
			test.readSet(ficheroTest, false);

			/* Normalize the data */
			normalizarTest();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		try {
			referencia = new InstanceSet();
			referencia.readSet(ficheroReferencia, false);

			/* Normalize the data */
			normalizarReferencia();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	private void normalizarTrain() throws CheckException {

		int i, j, k;
		Instance temp;
		double caja[];
		StringTokenizer tokens;
		boolean nulls[];

		/* Check if dataset corresponding with a classification problem */
		if (Attributes.getOutputNumAttributes() < 1) {
			throw new CheckException(
					"This dataset haven't outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
			throw new CheckException("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new CheckException(
					"This dataset have an input attribute with float values, so it not corresponding to a classification problem.");
		}

		entradas = Attributes.getInputAttributes();
		salida = Attributes.getOutputAttribute(0);
		nEntradas = Attributes.getInputNumAttributes();
		tokens = new StringTokenizer(training.getHeader(), " \n\r");
		tokens.nextToken();
		relation = tokens.nextToken();

		datosTrain = new double[training.getNumInstances()][Attributes
				.getInputNumAttributes()];
		clasesTrain = new int[training.getNumInstances()];
		caja = new double[1];

		nulosTrain = new boolean[training.getNumInstances()][Attributes
				.getInputNumAttributes()];
		nominalTrain = new int[training.getNumInstances()][Attributes
				.getInputNumAttributes()];
		realTrain = new double[training.getNumInstances()][Attributes
				.getInputNumAttributes()];

		for (i = 0; i < training.getNumInstances(); i++) {
			temp = training.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTrain[i] = training.getInstance(i).getAllInputValues();
			for (j = 0; j < nulls.length; j++)
				if (nulls[j]) {
					datosTrain[i][j] = 0.0;
					nulosTrain[i][j] = true;
				}
			caja = training.getInstance(i).getAllOutputValues();
			clasesTrain[i] = (int) caja[0];
			for (k = 0; k < datosTrain[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					nominalTrain[i][k] = (int) datosTrain[i][k];
					datosTrain[i][k] /= Attributes.getInputAttribute(k)
							.getNominalValuesList().size() - 1;
				} else {
					realTrain[i][k] = datosTrain[i][k];
					datosTrain[i][k] -= Attributes.getInputAttribute(k)
							.getMinAttribute();
					datosTrain[i][k] /= Attributes.getInputAttribute(k)
							.getMaxAttribute()
							- Attributes.getInputAttribute(k).getMinAttribute();
				}
			}
		}
	}

	/*
	 * This function builds the data matrix for classification reference and
	 * normalizes inputs values
	 */
	private void normalizarReferencia() throws CheckException {

		int i, j, k;
		Instance temp;
		double caja[];
		boolean nulls[];

		/* Check if dataset corresponding with a classification problem */
		if (Attributes.getOutputNumAttributes() < 1) {
			throw new CheckException(
					"This dataset haven't outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
			throw new CheckException("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new CheckException(
					"This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
		}

		datosReferencia = new double[referencia.getNumInstances()][Attributes
				.getInputNumAttributes()];
		clasesReferencia = new int[referencia.getNumInstances()];
		caja = new double[1];

		nulosReferencia = new boolean[referencia.getNumInstances()][Attributes
				.getInputNumAttributes()];
		nominalReferencia = new int[referencia.getNumInstances()][Attributes
				.getInputNumAttributes()];
		realReferencia = new double[referencia.getNumInstances()][Attributes
				.getInputNumAttributes()];

		/* Get the number of instances that have a null value */
		for (i = 0; i < referencia.getNumInstances(); i++) {
			temp = referencia.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosReferencia[i] = referencia.getInstance(i).getAllInputValues();
			for (j = 0; j < nulls.length; j++)
				if (nulls[j]) {
					datosReferencia[i][j] = 0.0;
					nulosReferencia[i][j] = true;
				}
			caja = referencia.getInstance(i).getAllOutputValues();
			clasesReferencia[i] = (int) caja[0];
			for (k = 0; k < datosReferencia[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					nominalReferencia[i][k] = (int) datosReferencia[i][k];
					datosReferencia[i][k] /= Attributes.getInputAttribute(k)
							.getNominalValuesList().size() - 1;
				} else {
					realReferencia[i][k] = datosReferencia[i][k];
					datosReferencia[i][k] -= Attributes.getInputAttribute(k)
							.getMinAttribute();
					datosReferencia[i][k] /= Attributes.getInputAttribute(k)
							.getMaxAttribute()
							- Attributes.getInputAttribute(k).getMinAttribute();
				}
			}
		}
	}

	/*
	 * This function builds the data matrix for classification test and
	 * normalizes inputs values
	 */
	private void normalizarTest() throws CheckException {

		int i, j, k;
		Instance temp;
		double caja[];
		boolean nulls[];

		/* Check if dataset corresponding with a classification problem */
		if (Attributes.getOutputNumAttributes() < 1) {
			throw new CheckException(
					"This dataset haven't outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
			throw new CheckException("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new CheckException(
					"This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
		}

		datosTest = new double[test.getNumInstances()][Attributes
				.getInputNumAttributes()];
		clasesTest = new int[test.getNumInstances()];
		caja = new double[1];

		nulosTest = new boolean[test.getNumInstances()][Attributes
				.getInputNumAttributes()];
		nominalTest = new int[test.getNumInstances()][Attributes
				.getInputNumAttributes()];
		realTest = new double[test.getNumInstances()][Attributes
				.getInputNumAttributes()];

		for (i = 0; i < test.getNumInstances(); i++) {
			temp = test.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTest[i] = test.getInstance(i).getAllInputValues();
			for (j = 0; j < nulls.length; j++)
				if (nulls[j]) {
					datosTest[i][j] = 0.0;
					nulosTest[i][j] = true;
				}
			caja = test.getInstance(i).getAllOutputValues();
			clasesTest[i] = (int) caja[0];
			for (k = 0; k < datosTest[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					nominalTest[i][k] = (int) datosTest[i][k];
					datosTest[i][k] /= Attributes.getInputAttribute(k)
							.getNominalValuesList().size() - 1;
				} else {
					realTest[i][k] = datosTest[i][k];
					datosTest[i][k] -= Attributes.getInputAttribute(k)
							.getMinAttribute();
					datosTest[i][k] /= Attributes.getInputAttribute(k)
							.getMaxAttribute()
							- Attributes.getInputAttribute(k).getMinAttribute();
				}
			}
		}
	}

	public void ejecutar() {

		int i, l, j, k;
		int nClases;
		Hyper database [];
		String cadena = "";
		Cromosoma poblacion[], newPob[], pobTemp[];
		int baraje[];
		int ev = 0;
		Cromosoma C[];
		int tmp, pos;
		int tamC;
	    int d;
	    int tamData;
	    int stat = 0;
	    boolean state [] = new boolean[1];
	    boolean marcas[];
	    int claseObt;
	    int nSel = 0;
	    
	    double conjS[][];
	    double conjR[][];
	    int conjN[][];
	    boolean conjM[][];
	    int clasesS[];
            long tiempos[];
	    
	    
		tiempos = new long[5];
		
		/* Getting the number of differents classes */
		nClases = 0;
		for (i = 0; i < clasesTrain.length; i++)
			if (clasesTrain[i] > nClases)
				nClases = clasesTrain[i];
		nClases++;
		tiempos[0] = System.currentTimeMillis();
		
		if (filtering) {
		    /*Inicialization of the flagged instances vector for a posterior copy*/
		    marcas = new boolean[datosReferencia.length];
		    for (i=0; i<datosReferencia.length; i++)
		      marcas[i] = false;
			
		    for (i=0; i<datosReferencia.length; i++) {
		        /*Apply KNN to the instance*/
		        claseObt = KNN.evaluacionKNN2 (K, datosReferencia, realReferencia, nominalReferencia, nulosReferencia, clasesReferencia, datosReferencia[i], realReferencia[i], nominalReferencia[i], nulosReferencia[i], nClases, true);
		        if (claseObt == clasesTrain[i]) { //agree with your majority, it is included in the solution set
		          marcas[i] = true;
		          nSel++;
		        }
		      }

		    /*Building of the S set from the flags*/
		    conjS = new double[nSel][datosReferencia[0].length];
		    conjR = new double[nSel][datosReferencia[0].length];
		    conjN = new int[nSel][datosReferencia[0].length];
		    conjM = new boolean[nSel][datosReferencia[0].length];
		    clasesS = new int[nSel];
		    for (i=0, l=0; i<datosReferencia.length; i++) {
		      if (marcas[i]) { //the instance will be copied to the solution
		        for (j=0; j<datosReferencia[0].length; j++) {
		          conjS[l][j] = datosReferencia[i][j];
		          conjR[l][j] = realReferencia[i][j];
		          conjN[l][j] = nominalReferencia[i][j];
		          conjM[l][j] = nulosReferencia[i][j];
		        }
		        clasesS[l] = clasesReferencia[i];
		        l++;
		      }
		    }

			tamData = conjS.length;		
			database = new Hyper[tamData];		
			database = composeHyper (conjS, conjN, conjM, clasesS, database, nClases);		
			d = database.length / 4;		    
		} else {		
			tamData = datosReferencia.length;		
			database = new Hyper[tamData];		
			database = composeHyper (datosReferencia, nominalReferencia, nulosReferencia, clasesReferencia, database, nClases);		
			d = database.length / 4;
		}
		
		/******************************************************************/
		/*From here, the Hyperrectangle selection algorithm can be coupled*/
		/******************************************************************/
	    tiempos[0]=System.currentTimeMillis() - tiempos[0];
            System.out.println("EHS_CHC " + relation + " Cosntruccion de Hiperectangulos realizado en  "+ (double) (tiempos[0]) / 1000.0+ "s");
            System.out.println("El numero de Hiperrectangulos es : "+database.length);  
	    System.out.println("Construyendo el modelo");  
            tiempos[1] = System.currentTimeMillis();
            distancias=new double[database.length][datosReferencia.length];
            for(int ii=0;ii<database.length;ii++)
                Arrays.fill(distancias[ii],-1);
	    /*Random inicialization of the population*/
	    Randomize.setSeed (semilla);
	    poblacion = new Cromosoma[tamPoblacion];
	    baraje = new int[tamPoblacion];
	    for (i=0; i<tamPoblacion; i++)
	    	poblacion[i] = new Cromosoma (database.length);
		
	    /*Initial evaluation of the poblation*/
	    for (i=0; i<tamPoblacion; i++)
	    	poblacion[i].evalua(datosReferencia, nominalReferencia, nulosReferencia, clasesReferencia, database,distancias,alfa, nClases,beta);

	    /*Until stop condition*/
	    while (ev < nEval) {
	    	C = new Cromosoma[tamPoblacion];
	      
	    	/*Selection(r) of C(t) from P(t)*/
	    	for (i=0; i<tamPoblacion; i++)
	    		baraje[i] = i;
	    	for (i=0; i<tamPoblacion; i++) {
	    		pos = Randomize.Randint (i, tamPoblacion-1);
	    		tmp = baraje[i];
	    		baraje[i] = baraje[pos];
	    		baraje[pos] = tmp;
	    	}
	    	for (i=0; i<tamPoblacion; i++)
	    		C[i] = new Cromosoma (database.length, poblacion[baraje[i]]);
	      
	    	/*Structure recombination in C(t) constructing C'(t)*/
	    	tamC = recombinar (C, d, database.length);
	    	newPob = new Cromosoma[tamC];
	    	for (i=0, l=0; i<C.length; i++) {
	    		if (C[i].esValido()) { //the cromosome must be copied to the new poblation C'(t)
	    			newPob[l] = new Cromosoma (database.length, C[i]);
	    			l++;
	    		}
	    	}
	      
	        /*Structure evaluation in C'(t)*/
	    	for (i=0; i<newPob.length; i++) {
	    		newPob[i].evalua(datosReferencia, nominalReferencia, nulosReferencia, clasesReferencia, database,distancias, alfa, nClases, beta);
	    		ev++;        
	        }

	        /*Selection(s) of P(t) from C'(t) and P(t-1)*/
	        Arrays.sort(poblacion);
	        Arrays.sort(newPob);
	        
	        /*If the best of C' is worse than the worst of P(t-1), then there will no changes*/
	        if (tamC==0 || newPob[0].getCalidad() < poblacion[tamPoblacion-1].getCalidad()) {
	        	d--;
	        } else {
	        	pobTemp = new Cromosoma[tamPoblacion];
	        	for (i=0, j=0, k=0; i<tamPoblacion && k<tamC; i++) {
	        		if (poblacion[j].getCalidad() > newPob[k].getCalidad()) {
	        			pobTemp[i] = new Cromosoma (database.length, poblacion[j]);
	        			j++;
	        		} else {
	        			pobTemp[i] = new Cromosoma (database.length, newPob[k]);
	        			k++;
	        		}
	        	}
	        	if (k == tamC) { //there are cromosomes for copying
	        		for (; i<tamPoblacion; i++) {
	        			pobTemp[i] = new Cromosoma (database.length, poblacion[j]);
	        			j++;
	        		}
	        	}
	        	poblacion = pobTemp;
	        }
	        
	        /*Last step of the algorithm*/
	        if (d < 0) {
	        	for (i=1; i<tamPoblacion; i++) {
	        		poblacion[i].divergeCHC (r, poblacion[0], prob0to1Div);
	        	}
	        	for (i=0; i<tamPoblacion; i++)
	        		if (!(poblacion[i].estaEvaluado())) {
	        			poblacion[i].evalua(datosReferencia, nominalReferencia, nulosReferencia, clasesReferencia, database,distancias, alfa, nClases, beta);
	        			ev++;
	        		}

	        	/*Reinicialization of d value*/
	        	d = (int)(r*(1.0-r)*(double)database.length);
	        }	    	
	    }

	    Arrays.sort(poblacion);
	    database = reduceHyper (database, poblacion[0].getBody());
            tiempos[1]=System.currentTimeMillis() - tiempos[1];
            System.out.println("EHS_CHC " + relation + " Modelo construido "+ (double) (tiempos[1]) / 60000.0+ "Min");
            
            //distancias=new double[database.length][datosTrain.length];
            //for(int ii=0;ii<database.length;ii++)
            //            Arrays.fill(distancias[ii],-1);	
                 /******************************************************************/
		/*Classification task*/
		/******************************************************************/
	    tiempos[2]=System.currentTimeMillis();	
            if (salida.getType() == Attribute.INTEGER) {
			int salidaKNN[][];
			int prediccion[][];

			/* Output of the training file */
			salidaKNN = new int[datosTrain.length][1];
			prediccion = new int[datosTrain.length][1];
			for (i = 0; i < salidaKNN.length; i++) {
				salidaKNN[i][0] = clasesTrain[i];
				prediccion[i][0] = evaluacionKNNHyper(database, datosTrain[i], nominalTest[i], nulosTest[i], nClases);
			}

			tiempos[2]= System.currentTimeMillis() - tiempos[2];
			System.out.println("EHS_CHC " + relation + " Train "+ (double) (tiempos[2]) / 1000.0+ "s");
			Output.escribeSalida(ficheroSalida[0], salidaKNN, prediccion, entradas, salida, nEntradas, relation);
			tiempos[3] = System.currentTimeMillis();

                        distancias=new double[database.length][datosTest.length];
                        for(int ii=0;ii<database.length;ii++)
                            Arrays.fill(distancias[ii],-1);	
			/* Output of the test file */
			salidaKNN = new int[datosTest.length][1];
			prediccion = new int[datosTest.length][1];
			for (i = 0; i < salidaKNN.length; i++) {
				salidaKNN[i][0] = clasesTest[i];
				prediccion[i][0] = evaluacionKNNHyper(database, datosTest[i], nominalTest[i], nulosTest[i], nClases);
			}

			tiempos[3]= System.currentTimeMillis() - tiempos[3];
			System.out.println("EHS_CHC " + relation + " Test "+ (double) (tiempos[3]) / 1000.0+ "s");
			Output.escribeSalida(ficheroSalida[1], salidaKNN, prediccion, entradas, salida, nEntradas, relation);

		} else {
			String salidaKNN[][];
			String prediccion[][];
                        /* Output of the training file */
			salidaKNN = new String[datosTrain.length][1];
			prediccion = new String[datosTrain.length][1];
			for (i = 0; i < salidaKNN.length; i++) {
				salidaKNN[i][0] = (String) salida.getNominalValuesList().elementAt(clasesTrain[i]);
				prediccion[i][0] = (String) salida.getNominalValuesList().elementAt(
						evaluacionKNNHyper(database, datosTrain[i], nominalTrain[i], nulosTrain[i], nClases));
			}
                        tiempos[2]= System.currentTimeMillis() - tiempos[2];
			System.out.println("EHS_CHC " + relation + " Train "+ (double) (tiempos[2]) / 1000.0+ "s");
			Output.escribeSalida(ficheroSalida[0], salidaKNN, prediccion, entradas, salida, nEntradas, relation);
			tiempos[3] = System.currentTimeMillis();

			/* Output of the test file */
			distancias=new double[database.length][datosTrain.length];
                        for(int ii=0;ii<database.length;ii++)
                            Arrays.fill(distancias[ii],-1);	
                        salidaKNN = new String[datosTest.length][1];
			prediccion = new String[datosTest.length][1];
			for (i = 0; i < salidaKNN.length; i++) {
				salidaKNN[i][0] = (String) salida.getNominalValuesList().elementAt(clasesTest[i]);
				prediccion[i][0] = (String) salida.getNominalValuesList().elementAt(
						evaluacionKNNHyper(database, datosTest[i], nominalTest[i], nulosTest[i], nClases, state));
				if (state[0] == true) {
					stat++;
				}
			}
                        tiempos[3]= System.currentTimeMillis() - tiempos[3];
			System.out.println("EHS_CHC " + relation + " Test "+ (double) (tiempos[3]) / 1000.0+ "s");
			Output.escribeSalida(ficheroSalida[1], salidaKNN, prediccion, entradas, salida, nEntradas, relation);
		}


	    cadena += "Number of rules: "+database.length + "\n";
	    cadena += "Examples Covered: "+ (double)stat / (double)datosTest.length + "\n";
		for (i=0; i<database.length; i++) {
			cadena += database[i] + "\n";
		}
		Fichero.escribeFichero(ficheroSalida[2], cadena);
		 
	}

	/*Function that determines the cromosomes who have to be crossed and the other ones who have to be removed
	   It returns the number of remaining cromosomes in the poblation*/
	private int recombinar (Cromosoma C[], int d, int len) {

		int i, j;
	    int distHamming;
	    int tamC = 0;

	    for (i=0; i<C.length/2; i++) {
	    	distHamming = 0;
	    	for (j=0; j<len; j++)
	    		if (C[i*2].getGen(j) != C[i*2+1].getGen(j))
	    			distHamming++;
	    	if ((distHamming/2) > d) {
	    		for (j=0; j<len; j++) {
	    			if ((C[i*2].getGen(j) != C[i*2+1].getGen(j)) && Randomize.Rand() < 0.5) {
	    				if (C[i*2].getGen(j)) C[i*2].setGen(j,false);
	    				else if (Randomize.Rand() < prob0to1Rec) C[i*2].setGen(j,true);
	    				if (C[i*2+1].getGen(j)) C[i*2+1].setGen(j,false);
	    				else if (Randomize.Rand() < prob0to1Rec) C[i*2+1].setGen(j,true);
	    			}
	    		}
	    		tamC += 2;
	    	} else {
	    		C[i*2].borrar();
	    		C[i*2+1].borrar();
	    	}
	    }

	    return tamC;
	}
	
	public Hyper [] composeHyper (double train[][], int nominal[][], boolean missing[][], int clases[], Hyper database[], int nClases) {
		
		int i, j, k;
		Vector <Integer> visitados = new Vector <Integer> ();
		double minDist, dist;
		double min, max;
		double x[], y[];
		boolean marcas[];
		int nSel = database.length;
		Hyper datared[];
		boolean nom[][];
		
		x = new double[train[0].length];
		y = new double[train[0].length];
		nom = new boolean[train[0].length][];
		
		marcas = new boolean[database.length];
		Arrays.fill(marcas, true);
		
		for (i=0; i<train.length; i++) {
			visitados.removeAllElements();
			minDist = Double.POSITIVE_INFINITY;
			for (j=0; j<train.length; j++) {
				if (clases[i] != clases[j]) {
					dist = EHS_CHC.distancia(train[i], nominal[i], missing[i], train[j], nominal[j], missing[j]);
					if (dist < minDist) {
						minDist = dist;
					}					
				}
			}
                        //Podriamos limitar a K elementos a ver que pasa 
			for (j=0; j<train.length; j++) {
				if (clases[i] == clases[j]) {
					dist = EHS_CHC.distancia(train[i], nominal[i], missing[i], train[j], nominal[j], missing[j]);
					if (dist < minDist) {
						visitados.add(j);
					}					
				}
			}
			
			if (visitados.size() == 0) {
				visitados.add(i);				
			}

			for (j=0; j<train[0].length; j++) {
				if (Attributes.getInputAttribute(j).getType() == Attribute.NOMINAL) {
					nom[j] = new boolean[Attributes.getInputAttribute(j).getNumNominalValues()];
					Arrays.fill(nom[j], false);
				} else {
					nom[j] = new boolean[0];
				}
			}
			
			for (j=0; j<train[0].length; j++) {
				if (nom[j].length == 0) {
					min = train[visitados.elementAt(0)][j];				
					max = train[visitados.elementAt(0)][j];
					for (k=1; k<visitados.size(); k++) {
						if (train[visitados.elementAt(k)][j] < min) {
							min = train[visitados.elementAt(k)][j];						
						} else if (train[visitados.elementAt(k)][j] > max) {
							max = train[visitados.elementAt(k)][j];						
						}
					}
					x[j] = min;
					y[j] = max;
				} else {
					for (k=0; k<visitados.size(); k++) {
						nom[j][nominal[visitados.elementAt(k)][j]] = true;
					}
				}
			}
			database[i] = new Hyper(x,y,nom,clases[i]);
		}
	
		/*Remove duplicates*/
		for (i=0; i<database.length; i++) {
			for (j=i+1; j<database.length && marcas[i]; j++) {
				if (database[i].equalTo(database[j])) {
					marcas[i] = false;
					nSel--;
				}
			}
		}
		
		datared = new Hyper[nSel];
		for (i=0, k=0; i<database.length; i++) {
			if (marcas[i]) {
				datared[k] = new Hyper(database[i].x, database[i].y, database[i].nom, database[i].clase);
				k++;
			}
		}
		
	    return datared;
	}

	public Hyper [] reduceHyper (Hyper database[], boolean marcas[]) {
		
		int i, k;
		int nSel = 0;
		Hyper datared[];
		
		for (i=0; i<marcas.length; i++)
			if (marcas[i])
				nSel++;

		datared = new Hyper[nSel];
		for (i=0, k=0; i<marcas.length; i++) {
			if (marcas[i]) {
				datared[k] = new Hyper(database[i].x, database[i].y, database[i].nom, database[i].clase);
				k++;
			}
		}
		
	    return datared;
	}

	public void leerConfiguracion(String ficheroScript) {

		String fichero, linea, token;
		StringTokenizer lineasFichero, tokens;
		byte line[];
		int i, j;

		ficheroSalida = new String[3];

		fichero = Fichero.leeFichero(ficheroScript);
		lineasFichero = new StringTokenizer(fichero, "\n\r");

		lineasFichero.nextToken();
		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();

		/* Getting the names of training and test files */
		line = token.getBytes();
		for (i = 0; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroReferencia = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroTraining = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroTest = new String(line, i, j - i);

		/* Getting the path and base name of the results files */
		linea = lineasFichero.nextToken();
		tokens = new StringTokenizer(linea, "=");
		tokens.nextToken();
		token = tokens.nextToken();

		/* Getting the names of output files */
		line = token.getBytes();
		for (i = 0; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroSalida[0] = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroSalida[1] = new String(line, i, j - i);
		for (i = j + 1; line[i] != '\"'; i++)
			;
		i++;
		for (j = i; line[j] != '\"'; j++)
			;
		ficheroSalida[2] = new String(line, i, j - i);

	    /*Getting the seed*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    semilla = Long.parseLong(tokens.nextToken().substring(1));

	    /*Getting the size of the poblation and the number of evaluations*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    tamPoblacion = Integer.parseInt(tokens.nextToken().substring(1));
	    
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    nEval = Integer.parseInt(tokens.nextToken().substring(1));

	    /*Getting the equilibrate alfa factor and r value*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    alfa = Double.parseDouble(tokens.nextToken().substring(1));

	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    beta = Double.parseDouble(tokens.nextToken().substring(1));

	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    r = Double.parseDouble(tokens.nextToken().substring(1));

	    /*Getting the probability of change bits*/
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    prob0to1Rec = Double.parseDouble(tokens.nextToken().substring(1));
	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    prob0to1Div = Double.parseDouble(tokens.nextToken().substring(1));	  

	    linea = lineasFichero.nextToken();
	    tokens = new StringTokenizer (linea, "=");
	    tokens.nextToken();
	    token = tokens.nextToken();
	    token = token.substring(1);
	    if (token.equalsIgnoreCase("false")) filtering = false;
	    else filtering = true;
            
	    
	}

	public static int evaluacionKNNHyper (Hyper database[], double ejemplo[], int nominal[], boolean missing[], int nClases) {

		int i;
		int vecinoCercano;
		double minDistancia;
		double dist;
		int dimensions;
		int votes[] = new int[nClases];
		int pos, minVotes;

		vecinoCercano = -1;
		minDistancia = Double.POSITIVE_INFINITY;

		Arrays.fill(votes, 0);

		
		for (i = 0; i < database.length; i++) {
			dist = distancia(database[i],ejemplo,nominal,missing);
			if (dist > 0) {
				if (dist < minDistancia) {
					minDistancia = dist;
					vecinoCercano = i;
				}
			} else {
				dimensions = database[i].dimensions();
				if (dimensions > 0) {
					minDistancia = 0;
					votes[database[i].clase]++;
				}
			}
		}

		if (minDistancia > 0) {
			return database[vecinoCercano].clase;
		} else {
			pos = 0;
			minVotes = votes[0];
			for (i=1; i<votes.length; i++) {
				if (votes[i] > minVotes) {
					pos = i;
					minVotes = votes[i];
				}
			}
			return pos;
		}		
	}

	public static int evaluacionKNNHyper (Hyper database[], double ejemplo[], int nominal[], boolean missing[], int nClases, boolean state[]) {

		int i;
		int vecinoCercano;
		double minDistancia;
		double dist;
		int dimensions;
		int votes[] = new int[nClases];
		int pos;
		double minVolume, volume;
		Vector <Integer> cand_rules = new Vector <Integer> ();

		vecinoCercano = -1;
		minDistancia = Double.POSITIVE_INFINITY;

		Arrays.fill(votes, 0);

		
		for (i = 0; i < database.length; i++) {
			dist = distancia(database[i],ejemplo,nominal,missing);
			if (dist > 0) {
				if (dist < minDistancia) {
					minDistancia = dist;
					vecinoCercano = i;
				}
			} else {
				dimensions = database[i].dimensions();
				if (dimensions > 0) {
					minDistancia = 0;
					cand_rules.add(i);
				}
			}
		}

		if (minDistancia > 0) {
			state[0] = false;
			return database[vecinoCercano].clase;
		} else {
			state[0] = true;
			minVolume = database[cand_rules.elementAt(0)].volume();
			pos = 0;
			for (i=1; i<cand_rules.size(); i++) {
				volume = database[cand_rules.elementAt(i)].volume();
				if (volume < minVolume) {
					pos = i;
					minVolume = volume;
				}
			}
			return database[cand_rules.elementAt(pos)].clase;
		}		
	}

	public static double distancia(double ej1[], int nom1[], boolean mis1[], double ej2[], int nom2[], boolean mis2[]) {

		int i;
		double suma = 0;

		for (i = 0; i < ej1.length; i++) {
			if (mis1[i] != true && mis2[i] != true) {
				if (Attributes.getInputAttribute(i).getType() == Attribute.NOMINAL) {
					if (nom1[i] != nom2[i]) {
						suma += 1;
					}
				} else {
					suma += (ej1[i] - ej2[i]) * (ej1[i] - ej2[i]);
				}
			}
		}
		suma = Math.sqrt(suma);

		return suma;
	}
	
	public static double distancia (Hyper h1, double ej2[], int nom2[], boolean mis2[]) {

		int i;
		double suma = 0;

		for (i = 0; i < ej2.length; i++) {
			if (!mis2[i]) { //the example has no missing value in this attribute
				if (h1.nom[i].length == 0) { // real value
					if (ej2[i] < h1.x[i])
						suma += (h1.x[i] - ej2[i]) * (h1.x[i] - ej2[i]);
					else if (ej2[i] > h1.y[i])
						suma += (h1.y[i] - ej2[i]) * (h1.y[i] - ej2[i]);
				} else {
					if (h1.nom[i][nom2[i]] == false) { //the rule does not cover the nominal value
						suma += 1;
					}
				}
			}
		}
		suma = Math.sqrt(suma);

		return suma;
	}
}