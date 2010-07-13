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

//

//  LVQ.java

//

//  Julián Luengo Martín

//

//  Created by Julián Luengo Martín Julio 2007

//  Proyecto KEEL

//

package keel.Algorithms.Neural_Networks.LVQ;

import keel.Algorithms.Preprocess.Basic.*;
import keel.Dataset.*;

import org.core.*;

import java.util.StringTokenizer;

import java.util.Vector;

public class LVQ extends Metodo {

	String ficheroReferencia;
	double datosReferencia[][];
	int clasesReferencia[];
	
	InstanceSet referencia;
	/* Own parameters of the algorithm */

	private long semilla;

	double alpha = 0, nu = 0;

	int n_p, T;

	double datosTest[][];

	int clasesTest[];

	public LVQ(String ficheroScript) {
		super(ficheroScript);
		try {
			referencia = new InstanceSet();
			referencia.readSet(ficheroReferencia, false);

			/*Normalize the data*/
			normalizarReferencia();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	public void ejecutar() {

		int i, j, l, m;
		double alfai;
		int nClases;

		int claseObt;

		boolean marcas[];
		boolean notFound;

		int nSel, init;
		int clasSel[];
		double conjS[][];

		int clasesS[];

		int baraje[];

		int pos, tmp;
		String instanciasIN[];
		String instanciasOUT[];

		long tiempo = System.currentTimeMillis();

		/* Getting the number of differents classes */

		nClases = 0;

		for (i = 0; i < clasesTrain.length; i++)

			if (clasesTrain[i] > nClases)

				nClases = clasesTrain[i];

		nClases++;

		/* Shuffle the train set */

		baraje = new int[datosTrain.length];

		Randomize.setSeed(semilla);

		for (i = 0; i < datosTrain.length; i++)

			baraje[i] = i;

		for (i = 0; i < datosTrain.length; i++) {

			pos = Randomize.Randint(i, datosTrain.length - 1);

			tmp = baraje[i];

			baraje[i] = baraje[pos];

			baraje[pos] = tmp;

		}

		/*
		 * Inicialization of the flagged instaces vector for a posterior
		 * elimination
		 */

		marcas = new boolean[datosTrain.length];

		for (i = 0; i < datosTrain.length; i++)

			marcas[i] = false;

		if (datosTrain.length > 0) {

			// marcas[baraje[0]] = true; //the first instance is included always

			nSel = n_p;
			if (nSel < nClases)
				nSel = nClases;

		} else {

			System.err.println("Input dataset is empty");

			nSel = 0;

		}
		clasSel = new int[nClases];
		System.out.print("Selecting initial neurons... ");
		// at least, there must be 1 neuron of each class at the beginning
		init = nClases;
		for (i = 0; i < nClases && i < datosTrain.length; i++) {
			pos = Randomize.Randint(0, datosTrain.length - 1);
			tmp = 0;
			while ((clasesTrain[pos] != i || marcas[pos])
					&& tmp < datosTrain.length) {
				pos = (pos + 1) % datosTrain.length;
				tmp++;
			}
			if (tmp < datosTrain.length)
				marcas[pos] = true;
			else
				init--;
			// clasSel[i] = i;
		}
		for (i = init; i < Math.min(nSel, datosTrain.length); i++) {
			tmp = 0;
			pos = Randomize.Randint(0, datosTrain.length - 1);
			while (marcas[pos]) {
				pos = (pos + 1) % datosTrain.length;
				tmp++;
			}
			// if(i<nClases){
			// notFound = true;
			// do{
			// for(j=i-1;j>=0 && notFound;j--){
			// if(clasSel[j] == clasesTrain[pos])
			// notFound = false;
			// }
			// if(!notFound)
			// pos = Randomize.Randint (0, datosTrain.length-1);
			// }while(!notFound);
			// }
			// clasSel[i] = clasesTrain[pos];
			marcas[pos] = true;
			init++;

		}
		nSel = init;
		System.out.println("Initial neurons selected: " + nSel);

		/* Building of the S set from the flags */

		conjS = new double[nSel][datosTrain[0].length];

		clasesS = new int[nSel];

		for (m = 0, l = 0; m < datosTrain.length; m++) {

			if (marcas[m]) { // the instance must be copied to the solution

				for (j = 0; j < datosTrain[0].length; j++) {

					conjS[l][j] = datosTrain[m][j];

				}

				clasesS[l] = clasesTrain[m];

				l++;

			}

		}

		alfai = alpha;
		boolean change = true;
		/* Body of the LVQ algorithm. */

		// Train the network
		for (int it = 0; it < T && change; it++) {
			change = false;
			alpha = alfai;
			for (i = 1; i < datosTrain.length; i++) {
				// search for the nearest neuron to training instance
				pos = NN(nSel, conjS, datosTrain[baraje[i]]);
				// nearest neuron labels correctly the class of training
				// instance?

				if (clasesS[pos] != clasesTrain[baraje[i]]) { // NO - repel
																// the neuron
					for (j = 0; j < conjS[pos].length; j++) {
						conjS[pos][j] = conjS[pos][j] - alpha
								* (datosTrain[baraje[i]][j] - conjS[pos][j]);
					}
					change = true;
				} else { // YES - migrate the neuron towards the input vector
					for (j = 0; j < conjS[pos].length; j++) {
						conjS[pos][j] = conjS[pos][j] + alpha
								* (datosTrain[baraje[i]][j] - conjS[pos][j]);
					}
				}
				alpha = nu * alpha;
			}
			// Shuffle again the training partition
			baraje = new int[datosTrain.length];

			for (i = 0; i < datosTrain.length; i++)

				baraje[i] = i;

			for (i = 0; i < datosTrain.length; i++) {

				pos = Randomize.Randint(i, datosTrain.length - 1);

				tmp = baraje[i];

				baraje[i] = baraje[pos];

				baraje[pos] = tmp;

			}
		}
		System.out
				.println("LVQ " + relation + " "
						+ (double) (System.currentTimeMillis() - tiempo)
						/ 1000.0 + "s");
		// Classify the train data set
		instanciasIN = new String[datosReferencia.length];
		instanciasOUT = new String[datosReferencia.length];
		for (i = 0; i < datosReferencia.length; i++) {
			/* Classify the instance selected in this iteration */
			Attribute a = Attributes.getOutputAttribute(0);

			int tipo = a.getType();
			claseObt = KNN.evaluacionKNN2(1, conjS, clasesS, datosReferencia[i],
					nClases);
			if(tipo!=Attribute.NOMINAL){
				instanciasIN[i] = new String(String.valueOf(clasesReferencia[i]));
				instanciasOUT[i] = new String(String.valueOf(claseObt));
			}
			else{
				instanciasIN[i] = new String(a.getNominalValue(clasesReferencia[i]));
				instanciasOUT[i] = new String(a.getNominalValue(claseObt));
			}
		}

		escribeSalida(ficheroSalida[0], instanciasIN, instanciasOUT, entradas,
				salida, nEntradas, relation);

		// Classify the test data set
		normalizarTest();
		instanciasIN = new String[datosTest.length];
		instanciasOUT = new String[datosTest.length];
		for (i = 0; i < datosTest.length; i++) {
			/* Classify the instance selected in this iteration */
			Attribute a = Attributes.getOutputAttribute(0);

			int tipo = a.getType();

			claseObt = KNN.evaluacionKNN2(1, conjS, clasesS, datosTest[i],
					nClases);
			if(tipo!=Attribute.NOMINAL){
				instanciasIN[i] = new String(String.valueOf(clasesTest[i]));
				instanciasOUT[i] = new String(String.valueOf(claseObt));
			}
			else{
				instanciasIN[i] = new String(a.getNominalValue(clasesTest[i]));
				instanciasOUT[i] = new String(a.getNominalValue(claseObt));
			}
		}

		escribeSalida(ficheroSalida[1], instanciasIN, instanciasOUT, entradas,
				salida, nEntradas, relation);

	}

	protected int NN(int nSel, double conj[][], double ejemplo[]) {
		double mindist, dist;
		int nneigh = -1;

		mindist = Double.POSITIVE_INFINITY;

		for (int i = 0; i < nSel; i++) {
			dist = KNN.distancia(conj[i], ejemplo);
			if (dist < mindist) {
				mindist = dist;
				nneigh = i;
			}
		}
		return nneigh;
	}

	public void leerConfiguracion(String ficheroScript) {

		String fichero, linea, token;

		StringTokenizer lineasFichero, tokens;

		byte line[];

		int i, j;

		ficheroSalida = new String[2];

		fichero = Fichero.leeFichero(ficheroScript);

		lineasFichero = new StringTokenizer(fichero, "\n\r");

		lineasFichero.nextToken();

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		token = tokens.nextToken();

		/* Getting the names of the training and test files */

	    line = token.getBytes();

	    for (i=0; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	    ficheroTraining = new String (line,i,j-i);

	    for (i=j+1; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	    ficheroReferencia = new String (line,i,j-i);

	    for (i=j+1; line[i]!='\"'; i++);

	    i++;

	    for (j=i; line[j]!='\"'; j++);

	    ficheroTest = new String (line,i,j-i);
	    
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

		/* Getting the seed */

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		semilla = Long.parseLong(tokens.nextToken().substring(1));

		/* Getting the number of iterations */

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		T = Integer.parseInt(tokens.nextToken().substring(1));

		/* Getting the number of neurons */

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		n_p = Integer.parseInt(tokens.nextToken().substring(1));

		/* Getting the alpha factor */

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		alpha = Double.parseDouble(tokens.nextToken().substring(1));

		/* Getting the nu factor */

		linea = lineasFichero.nextToken();

		tokens = new StringTokenizer(linea, "=");

		tokens.nextToken();

		nu = Double.parseDouble(tokens.nextToken().substring(1));
	}

	public static void escribeSalida(String nombreFichero,
			String instanciasIN[], String instanciasOUT[],
			Attribute entradas[], Attribute salida, int nEntradas,
			String relation) {

		String cadena = "";
		int i, j, k;
		int aux;

		/* Printing input attributes */
		cadena += "@relation " + relation + "\n";
		for (i = 0; i < nEntradas; i++) {
			cadena += "@attribute " + entradas[i].getName() + " ";
			if (entradas[i].getType() == Attribute.NOMINAL) {
				cadena += "{";
				for (j = 0; j < entradas[i].getNominalValuesList().size(); j++) {
					cadena += (String) entradas[i].getNominalValuesList()
							.elementAt(j);
					if (j < entradas[i].getNominalValuesList().size() - 1) {
						cadena += ", ";
					}
				}
				cadena += "}\n";
			} else {
				if (entradas[i].getType() == Attribute.INTEGER) {
					cadena += "integer";
					cadena += " ["
							+ String.valueOf((int) entradas[i]
									.getMinAttribute())
							+ ", "
							+ String.valueOf((int) entradas[i]
									.getMaxAttribute()) + "]\n";
				} else {
					cadena += "real";
					cadena += " ["
							+ String.valueOf(entradas[i].getMinAttribute())
							+ ", "
							+ String.valueOf(entradas[i].getMaxAttribute())
							+ "]\n";
				}
			}
		}

		/* Printing output attribute */
		cadena += "@attribute " + salida.getName() + " ";
		if (salida.getType() == Attribute.NOMINAL) {
			cadena += "{";
			for (j = 0; j < salida.getNominalValuesList().size(); j++) {
				cadena += (String) salida.getNominalValuesList().elementAt(j);
				if (j < salida.getNominalValuesList().size() - 1) {
					cadena += ", ";
				}
			}
			cadena += "}\n";
		} else {
			cadena += "integer ["
					+ String.valueOf((int) salida.getMinAttribute()) + ", "
					+ String.valueOf((int) salida.getMaxAttribute()) + "]\n";
		}

		/* Printing the data */
		cadena += "@data\n";

		Fichero.escribeFichero(nombreFichero, cadena);
		cadena = "";
		for (i = 0; i < instanciasIN.length; i++) {
			cadena += instanciasIN[i] + " " + instanciasOUT[i];

			cadena += "\n";

		}
		Fichero.AnadirtoFichero(nombreFichero, cadena);
	}

	private void normalizarTest() {

		int i, j, cont = 0, k;
		Instance temp;
		boolean hecho;
		double caja[];
		StringTokenizer tokens;
		boolean nulls[];

		/* Check if dataset corresponding with a classification problem */

		if (Attributes.getOutputNumAttributes() < 1) {
			System.err
					.println("This dataset haven´t outputs, so it not corresponding to a classification problem.");
			System.exit(-1);
		} else if (Attributes.getOutputNumAttributes() > 1) {
			System.err.println("This dataset have more of one output.");
			System.exit(-1);
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			System.err
					.println("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
			System.exit(-1);
		}

		datosTest = new double[test.getNumInstances()][Attributes
				.getInputNumAttributes()];
		clasesTest = new int[test.getNumInstances()];
		caja = new double[1];

		for (i = 0; i < test.getNumInstances(); i++) {
			temp = test.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosTest[i] = test.getInstance(i).getAllInputValues();
			for (j = 0; j < nulls.length; j++)
				if (nulls[j])
					datosTest[i][j] = 0.0;
			caja = test.getInstance(i).getAllOutputValues();
			clasesTest[i] = (int) caja[0];
			for (k = 0; k < datosTest[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					datosTest[i][k] /= Attributes.getInputAttribute(k)
							.getNominalValuesList().size() - 1;
				} else {
					datosTest[i][k] -= Attributes.getInputAttribute(k)
							.getMinAttribute();
					datosTest[i][k] /= Attributes.getInputAttribute(k)
							.getMaxAttribute()
							- Attributes.getInputAttribute(k).getMinAttribute();
				}
			}
		}
	}
	
	 /*This function builds the data matrix for classification reference and normalizes inputs values*/

	private void normalizarReferencia () throws CheckException {

		int i, j, cont = 0, k;
		Instance temp;
		boolean hecho;
		double caja[];
		StringTokenizer tokens;
		boolean nulls[];

		/*Check if dataset corresponding with a classification problem*/

		if (Attributes.getOutputNumAttributes() < 1) {
			throw new CheckException ("This dataset haven´t outputs, so it not corresponding to a classification problem.");
		} else if (Attributes.getOutputNumAttributes() > 1) {
			throw new CheckException ("This dataset have more of one output.");
		}

		if (Attributes.getOutputAttribute(0).getType() == Attribute.REAL) {
			throw new CheckException ("This dataset have an input attribute with floating values, so it not corresponding to a classification problem.");
		}

		datosReferencia = new double[referencia.getNumInstances()][Attributes.getInputNumAttributes()];
		clasesReferencia = new int[referencia.getNumInstances()];
		caja = new double[1];

		/*Get the number of instances that have a null value*/
		for (i=0; i<referencia.getNumInstances(); i++) {
			temp = referencia.getInstance(i);
			nulls = temp.getInputMissingValues();
			datosReferencia[i] = referencia.getInstance(i).getAllInputValues();
			for (j=0; j<nulls.length; j++)
				if (nulls[j])
					datosReferencia[i][j]=0.0;
			caja = referencia.getInstance(i).getAllOutputValues();
			clasesReferencia[i] = (int)caja[0];
			for (k=0; k<datosReferencia[i].length; k++) {
				if (Attributes.getInputAttribute(k).getType() == Attribute.NOMINAL) {
					datosReferencia[i][k] /= Attributes.getInputAttribute(k).getNominalValuesList().size()-1;
				} else {
					datosReferencia[i][k] -= Attributes.getInputAttribute(k).getMinAttribute();
					datosReferencia[i][k] /= Attributes.getInputAttribute(k).getMaxAttribute() - Attributes.getInputAttribute(k).getMinAttribute();
				}
			}
		}
	}
}

