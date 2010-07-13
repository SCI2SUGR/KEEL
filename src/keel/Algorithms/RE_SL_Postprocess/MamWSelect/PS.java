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

package keel.Algorithms.RE_SL_Postprocess.MamWSelect;
import java.io.*; 
import org.core.*;
import java.util.*;
import java.lang.Math;

class PS {
	public double semilla;
	public long cont_soluciones;
	public long Gen, n_genes, n_reglas, n_generaciones;
	public int n_soluciones;

	public String fich_datos_chequeo, fich_datos_tst;
	public String fichero_conf, fichero_datos, ruta_salida;
	public String fichero_br, fichero_reglas, fich_tra_obli, fich_tst_obli;
	public String cadenaReglas = "";


	public MiDataset tabla, tabla_tst;
    public BaseR base_reglas;
    public BaseR base_total;
    public Adap fun_adap;
    public AG alg_gen;

	public PS (String f_e) {
		fichero_conf = f_e;
	}


	private String Quita_blancos(String cadena) {
		StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
		return (sT.nextToken());
	}


	/** Reads the data of the configuration file */
	public void leer_conf (){
		int i, j;
		String cadenaEntrada, valor;
		double cruce, mutacion, porc_num_reglas, tau;
		int long_poblacion, cpeso;


		// we read the file in a String
		cadenaEntrada = Fichero.leeFichero(fichero_conf);
		StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

		// we read the algorithm's name
		sT.nextToken();
		sT.nextToken();

		// we read the name of the training and test files
		sT.nextToken();
		valor = sT.nextToken();

		StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
		ficheros.nextToken();
		fich_datos_chequeo = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fich_datos_tst = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_br = ((ficheros.nextToken()).replace('\"',' ')).trim();


		// we read the name of the output files
		sT.nextToken();
		valor = sT.nextToken();

		ficheros = new StringTokenizer(valor, "\t ", false);
		fich_tra_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fich_tst_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_reglas = ((ficheros.nextToken()).replace('\"',' ')).trim();
		ruta_salida = fich_tst_obli.substring(0, fich_tst_obli.lastIndexOf('/') + 1);

		// we read the seed of the random generator
		sT.nextToken();
		valor = sT.nextToken();
		semilla = Double.parseDouble(valor.trim());
		Randomize.setSeed((long) semilla);;

		// we read the Number of Iterations
		sT.nextToken();
		valor = sT.nextToken();
		n_generaciones = Long.parseLong(Quita_blancos(valor));

		// we read the Population Size
		sT.nextToken();
		valor = sT.nextToken();
		long_poblacion = Integer.parseInt(Quita_blancos(valor));

		// we read the Tau parameter for the minimun maching degree required to the KB
		sT.nextToken();
		valor = sT.nextToken();
		tau = Double.parseDouble(Quita_blancos(valor));

		n_soluciones = 1;

		// we read the Cross Probability
		sT.nextToken();
		valor = sT.nextToken();
		cruce = Double.parseDouble(Quita_blancos(valor));

		// we read the Mutation Probability
		sT.nextToken();
		valor = sT.nextToken();
		mutacion = Double.parseDouble(Quita_blancos(valor));

		// we read the KB Output File Format with Weight values to 1 (0/1)
//		sT.nextToken();
//		valor = sT.nextToken();
		cpeso = 1;

		// we create all the objects
		tabla = new MiDataset(fich_datos_chequeo, true);
		if (tabla.salir==false) {
		base_total = new BaseR(fichero_br, cpeso, tabla);
		base_reglas = new BaseR(base_total.n_reglas, cpeso, tabla);
		fun_adap = new Adap(tabla, base_reglas, base_total, n_soluciones, tau);
		alg_gen = new AG(long_poblacion, base_total.n_reglas, cruce, mutacion, fun_adap);
	    }
	}


	public void run () {
		int i, j;
		double ec, el, min_CR, ectst, eltst;

		/* We read the configutate file and we initialize the structures and variables */
		leer_conf();

		if (tabla.salir==false) {
		/* Inicializacion del contador de soluciones ya generadas */
		cont_soluciones = 0;

		do {

			/* Generation of the initial population */
			alg_gen.Initialize (base_total);
			Gen = 0;

			/* Evaluation of the initial population  */
			alg_gen.Evaluate ();
			Gen++;

			/* Main of the genetic algorithm */
			do {
				/* Interchange of the new and old population */
				alg_gen.Intercambio();

				/* Selection by means of Baker */
				alg_gen.Select ();

				/* Crossover */
				alg_gen.Cruce ();

				/* Mutation */
				alg_gen.Mutacion_Uniforme ();

				/* Elitist selection */
				alg_gen.Elitist ();

				/* Evaluation of the current population */
				alg_gen.Evaluate ();

				/* we increment the counter */
				Gen++;
			} while (Gen<=n_generaciones);

			/* we store the RB in the Tabu list */
			if (Aceptar (alg_gen.solucion())==1) {
				fun_adap.guardar_solucion(alg_gen.solucion(), alg_gen.solucionR());

				/* we increment the number of solutions */
				cont_soluciones++;

				fun_adap.Decodifica (alg_gen.solucion(), alg_gen.solucionR());
				fun_adap.Cubrimientos_Base();

		        /* we calcule the MSEs */
				fun_adap.Error_tra ();
				ec = fun_adap.EC;
				el = fun_adap.EL;

				tabla_tst = new MiDataset(fich_datos_tst, false);
				fun_adap.Error_tst (tabla_tst);
				ectst = fun_adap.EC;
				eltst = fun_adap.EL;


		        /* we calculate the minimum and maximum matching */
				min_CR = 1.0;
				for (i=0; i < tabla.long_tabla; i++)
					min_CR = Adap.Minimo (min_CR, tabla.datos[i].maximo_cubrimiento);


		        /* we write the RB */
				cadenaReglas = base_reglas.BRtoString();
				cadenaReglas += "\n\nMinimum of C_R: " + min_CR + " Minimum covering degree: " + fun_adap.mincb + "\nAverage covering degree: " + fun_adap.medcb + " MLE: " + el + "\nMSEtra: " + ec + " , MSEtst: " + ectst + "\n";

				Fichero.escribeFichero(fichero_reglas, cadenaReglas);

		        /* we write the obligatory output files*/
				String salida_tra = tabla.getCabecera();
				salida_tra += fun_adap.getSalidaObli(tabla);
				Fichero.escribeFichero(fich_tra_obli, salida_tra);

				String salida_tst = tabla_tst.getCabecera();
				salida_tst += fun_adap.getSalidaObli(tabla_tst);
				Fichero.escribeFichero(fich_tst_obli, salida_tst);

				/* we write the MSEs in specific files */
				Fichero.AnadirtoFichero(ruta_salida + "PSimplifcomunR.txt", "" + base_reglas.n_reglas + "\n");
				Fichero.AnadirtoFichero(ruta_salida + "PSimplifcomunTRA.txt", "" + ec + "\n");
				Fichero.AnadirtoFichero(ruta_salida + "PSimplifcomunTST.txt", "" + ectst + "\n");
			}

			/* the multimodal GA finish when the condition is true */
		} while (Parada ()==0);
       }
	}


	/** Criterion of stop */
	public int Parada () {
		if (cont_soluciones == n_soluciones)  return (1);
		else  return (0);
	}


	/** Criterion to accept the solutions */
	int Aceptar (char [] cromosoma) {
		return (1);
	}
}


