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

package keel.Algorithms.RE_SL_Methods.TSKIRL;
import java.io.*; 
import org.core.*;
import java.util.*;
import java.lang.Math;


class Lear_m6 {
	public int MaxEtiquetas = 10;

    public int aplicar_ee_11;
	public double semilla;

	public String fich_datos_chequeo, fich_datos_tst;
	public String fichero_conf, fichero_inf, ruta_salida;
	public String fichero_datos, fichero_reglas, fich_tra_obli, fich_tst_obli;
	public String informe = "";
	public String datos_inter = "";
	public String cadenaReglas = "";


	public Structure Padre, Hijo;
    public MiDataset tabla, tabla_tst;
    public BaseR base_reglas;
    public BaseD base_datos;
    public Adap fun_adap;
    public Est_evol ee_11;
    public Est_mu_landa ee_mu_landa;


	public Lear_m6 (String f_e) {
		fichero_conf = f_e;
	}


	private String Quita_blancos(String cadena) {
		StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
		return (sT.nextToken());
	}


	/** Reads the data of the configuration file */
	public void leer_conf (){
		int i, j;
		int n_gen_ee, n_gen_ee_11, Mu, Landa, N_sigma, N_alfa;
		int Omega_x, Omega_sigma, Omega_alfa, Delta_x, Delta_sigma, Delta_alfa;
		int Tipo_nichos, Tipo_fitness, n_etiquetas;
		double Omega, Epsilon, K;
		String cadenaEntrada, valor;

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


		// we read the name of the output files
		sT.nextToken();
		valor = sT.nextToken();

		ficheros = new StringTokenizer(valor, "\t ", false);
		fich_tra_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fich_tst_obli = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_reglas = ((ficheros.nextToken()).replace('\"',' ')).trim();
		fichero_inf = ((ficheros.nextToken()).replace('\"',' ')).trim();
		ruta_salida = fichero_reglas.substring(0, fichero_reglas.lastIndexOf('/') + 1);


		// we read the seed of the random generator
		sT.nextToken();
		valor = sT.nextToken();
		semilla = Double.parseDouble(valor.trim());
		Randomize.setSeed((long) semilla);

		// we read the Evolutionary Strategy Iterations
		sT.nextToken();
		valor = sT.nextToken();
		n_gen_ee = Integer.parseInt(valor.trim());

		// we read the Number of Parents for the Evolutionary Strategy (Mu)
		sT.nextToken();
		valor = sT.nextToken();
		Mu = Integer.parseInt(valor.trim());


		// we read the Number of offspring for the Evolutionary Strategy (Landa)
		sT.nextToken();
		valor = sT.nextToken();
		Landa = Integer.parseInt(valor.trim());


		// we read the Size of the Standar Deviation String (N_sigma)
		sT.nextToken();
		valor = sT.nextToken();
		N_sigma = Integer.parseInt(valor.trim());


		// we read the Size of the Angle String (N_alfa)
		sT.nextToken();
		valor = sT.nextToken();
		N_alfa = Integer.parseInt(valor.trim());


		// we read the Recombination Operator for the Solution String (Omega_x)
		sT.nextToken();
		valor = sT.nextToken();
		Omega_x = Integer.parseInt(valor.trim());


		// we read the Recombination Operator for the Deviation String (Omega_sigma)
		sT.nextToken();
		valor = sT.nextToken();
		Omega_sigma = Integer.parseInt(valor.trim());


		// we read the Recombination Operator for the Angle String (Omega_alfa)
		sT.nextToken();
		valor = sT.nextToken();
		Omega_alfa = Integer.parseInt(valor.trim());


		// we read the Number of Parents to recombine the Solution String (Delta_x)
		sT.nextToken();
		valor = sT.nextToken();
		Delta_x = Integer.parseInt(valor.trim());


		// we read the Number of Parents to recombine the Deviation String (Delta_sigma)
		sT.nextToken();
		valor = sT.nextToken();
		Delta_sigma = Integer.parseInt(valor.trim());


		// we read the Number of Parents to recombine the Angle String (Delta_alfa)
		sT.nextToken();
		valor = sT.nextToken();
		Delta_alfa = Integer.parseInt(valor.trim());


		// we read if the Estrategia de Evolucion (1+1) is applied 
		sT.nextToken();
		valor = sT.nextToken();
		aplicar_ee_11 = Integer.parseInt(valor.trim());


		// we read the Evolution Strategy Iterations 
		sT.nextToken();
		valor = sT.nextToken();
		n_gen_ee_11 = Integer.parseInt(valor.trim());


		// we read the Type of niches
		sT.nextToken();
		valor = sT.nextToken();
		Tipo_nichos = Integer.parseInt(valor.trim());


		// we read the Omega parameter for the maching degree of the positive instances
		sT.nextToken();
		valor = sT.nextToken();
		Omega = Double.parseDouble(valor.trim());


		// we read the K parameter for the percentage of allowed negative instances
		sT.nextToken();
		valor = sT.nextToken();
		K = Double.parseDouble(valor.trim());


		// we read the Epsilon parameter for the minimun maching degree required to the KB
		sT.nextToken();
		valor = sT.nextToken();
		Epsilon = Double.parseDouble(valor.trim());


		// we read the Type of Fitness Function
		sT.nextToken();
		valor = sT.nextToken();
		Tipo_fitness = Integer.parseInt(valor.trim());

		// we read the Number of Labels
		sT.nextToken();
		valor = sT.nextToken();
		n_etiquetas = Integer.parseInt(valor.trim());

		// we create all the objects
		tabla = new MiDataset(fich_datos_chequeo, true);
		base_datos = new BaseD(tabla, n_etiquetas);

		for (i=0; i<tabla.n_variables; i++) {
			base_datos.n_etiquetas[i] = n_etiquetas;
		}

		base_reglas = new BaseR (base_datos, tabla);
		fun_adap = new Adap (tabla, base_reglas, Omega, K, Epsilon, Tipo_fitness, Tipo_nichos);
		ee_mu_landa = new Est_mu_landa(base_reglas, fun_adap, tabla, n_gen_ee, Mu, Landa, N_sigma, N_alfa, Omega_x, Omega_sigma, Omega_alfa, Delta_x, Delta_sigma, Delta_alfa);
		ee_11 = new Est_evol (base_datos, fun_adap, tabla.n_var_estado, n_gen_ee_11);
		Padre = new Structure(ee_11.n_genes_ee_11);
		Hijo = new Structure(ee_11.n_genes_ee_11);
	}


	public void run () {
		int i, j, etiqueta, pos_individuo;
		double ec, el, ec_tst, el_tst;

		/* We read the configutate file and we initialize the structures and variables */
		leer_conf();

		if (tabla.salir==false) {
		/* we generate the semantics of the linguistic variables */
		base_datos.Semantica ();

		/* we store the DB in the report file */
		informe += "Initial Data Base: \n\n";
		for (i=0; i < tabla.n_var_estado;i++) {
			informe += "  Variable de estado " + (i+1) + ":\n";
			for (j=0; j<base_datos.n_etiquetas[i]; j++)
				informe += "    Label " + (j+1) + ": (" + base_datos.BaseDatos[i][j].x0 + "," + base_datos.BaseDatos[i][j].x1 + "," + base_datos.BaseDatos[i][j].x3 + ")\n";

			informe += "\n";
		}

		informe += "\n";
		Fichero.escribeFichero(fichero_inf, informe);

		/* Phase 1: Generation of the antecedents of the RB */
		base_reglas.Generate (fun_adap);
		
		/* Iterative Rule Learning */
		for (i=0; i<base_reglas.n_reglas && tabla.no_cubiertos>0; i++) {

			Padre.Perf = 0.0;

			/* Inicialization of the antecedents for the ES (1+1) */
			ee_11.inicializa_ant (Padre, i, base_reglas);

			fun_adap.ejemplos_positivos (Padre.Gene);

			/* Inicialization of the consequents for the ES (1+1) */
			ee_mu_landa.inicializa_cons (Padre);

			/* Phase 3: Optimization of the current rule */
			if (aplicar_ee_11==1)  ee_11.Estrategia_Evolucion (Padre, Hijo);

			/* we apply the strategy Evolution for learning the consequent */
			ee_mu_landa.EE_Mu_Landa ();

			Padre.Perf = ee_mu_landa.fitness_solucion ();
			
			/* we store the consequent of the best father */
			ee_mu_landa.guardar_solucion (Padre);

			/* we store the rule in the RB */
			base_reglas.inserta_regla(i, Padre.Gene);

			/* we study the covering of the examples */
			if (aplicar_ee_11==1)  fun_adap.cubrimiento(Padre.Gene);

		} 
		base_reglas.n_reglas = i;   

		/* we calcule the MSEs */
		fun_adap.Error_tra ();
		ec = fun_adap.EC;
		el = fun_adap.EL;

		tabla_tst = new MiDataset(fich_datos_tst, false);
		fun_adap.Error_tst (tabla_tst);
		ec_tst = fun_adap.EC;
		el_tst = fun_adap.EL;

		/* we write the RB */
		cadenaReglas = base_reglas.BRtoString();
		cadenaReglas += "\nMSEtra: " + ec + "  MLEtra: " + el;
		cadenaReglas += "\nMSEtst: " + ec_tst + "  MLEtst: " + el_tst;

		Fichero.escribeFichero(fichero_reglas, cadenaReglas);

		/* we write the obligatory output files*/
		String salida_tra = tabla.getCabecera();
		salida_tra += fun_adap.getSalidaObli(tabla);
		Fichero.escribeFichero(fich_tra_obli, salida_tra);

		String salida_tst = tabla_tst.getCabecera();
		salida_tst += fun_adap.getSalidaObli(tabla_tst);
		Fichero.escribeFichero(fich_tst_obli, salida_tst);

		/* we write the MSEs in specific files */
		Fichero.AnadirtoFichero(ruta_salida + "Lear_m6comunR.txt", "" + base_reglas.n_reglas + "\n");
		Fichero.AnadirtoFichero(ruta_salida + "Lear_m6comunTRA.txt", "" + ec + "\n");
		Fichero.AnadirtoFichero(ruta_salida + "Lear_m6comunTST.txt", "" + ec_tst + "\n");
	}
	}
}


