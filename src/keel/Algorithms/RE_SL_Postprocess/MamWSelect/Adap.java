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
import java.lang.Math;


class Adap {

	public double [] grado_pertenencia;
	public double medcb, mincb, tau;
	public double maxEC, ec_ini;
	public double EC, EL;
    public int cont_soluciones, gl_ini;
	public char [][]ListaTabu;
	public double [][]ListaTabuR;
	public int ISHI = 0;

	public MiDataset tabla;
	public BaseR base_reglas;
	public BaseR base_total;

	public Adap (MiDataset training, BaseR base, BaseR base_t, int n_soluciones, double valor_tau) {
		int i;

		tabla = training;
		base_reglas = base;
		base_total = base_t;

		tau = valor_tau;
		cont_soluciones = 0;
		gl_ini = 0;

		maxEC = 0.0;
		for (i=0; i<tabla.long_tabla; i++)
			maxEC += Math.pow (tabla.datos[i].ejemplo[tabla.n_var_estado], 2.0);

		maxEC /= 2.0;

		ListaTabu = new char[n_soluciones][base_total.n_reglas];
		ListaTabuR = new double[n_soluciones][base_total.n_reglas];

		grado_pertenencia = new double[tabla.n_variables];
	}


	public static double Minimo (double x, double y) {
		if (x<y)  return (x);
		else  return (y);
	}


	public static double Maximo (double x, double y) {
		if (x > y)  return (x);
		else  return (y);
	}


/* -------------------------------------------------------------------------
                               FUNCION FITNESS
  ------------------------------------------------------------------------- */

/* ------------------------- Criterios de reglas -------------------------- */

	/* Calcula el grado de compatibilidad (Ri(ek)) de la regla con el ejemplo */
	public double ReglaCubreEjemplo (Difuso [] R, double []ejem) {
		int i;
		double minimo;

		for(i=0; i < tabla.n_variables; i++)
 			grado_pertenencia[i] = base_reglas.Fuzzifica (ejem[i],R[i]);

		minimo = 1;
		for(i=0; i<tabla.n_variables; i++)
			if (grado_pertenencia[i] < minimo)	 minimo = grado_pertenencia[i];

		return (minimo);
	}



	/* Calcula los grados de cubrimiento medio y minimo de la Base de Conocimiento
	sobre el conjunto de ejemplos  */
	public void Cubrimientos_Base () {
		int i, j;
		double RCE, cb;

		for (i=0; i<tabla.long_tabla; i++) {
			tabla.datos[i].nivel_cubrimiento = 0.0;
			tabla.datos[i].maximo_cubrimiento = 0.0;
			for (j=0;j<base_reglas.n_reglas;j++) {
				RCE = ReglaCubreEjemplo (base_reglas.BaseReglas[j], tabla.datos[i].ejemplo);
				tabla.datos[i].nivel_cubrimiento += RCE;
				tabla.datos[i].maximo_cubrimiento = Maximo (tabla.datos[i].maximo_cubrimiento,RCE);
			}
		}

		cb = 0;
		mincb = 10E37;
		for (i=0; i<tabla.long_tabla; i++) {
			cb += tabla.datos[i].nivel_cubrimiento;
			if (tabla.datos[i].nivel_cubrimiento < mincb)
				mincb = tabla.datos[i].nivel_cubrimiento;
		}

		medcb = cb / (double) tabla.long_tabla;
	}



	/* ---------------------- Decodificacion del cromosoma -------------------- */

	/* Pasa la Base de Conocimiento codificada en el cromosoma a una estructura
	adecuada para inferir */
	void Decodifica (char [] cromosoma, double [] cromosomaR) {
		int i, j;

		base_reglas.n_reglas = 0;
		for (i=0; i<base_total.n_reglas; i++) {
			if (cromosoma[i]=='1') {
				for (j=0; j<tabla.n_variables; j++) {
					base_reglas.BaseReglas[base_reglas.n_reglas][j].x0 = base_total.BaseReglas[i][j].x0;
					base_reglas.BaseReglas[base_reglas.n_reglas][j].x1 = base_total.BaseReglas[i][j].x1;
					base_reglas.BaseReglas[base_reglas.n_reglas][j].x2 = base_total.BaseReglas[i][j].x2;
					base_reglas.BaseReglas[base_reglas.n_reglas][j].x3 = base_total.BaseReglas[i][j].x3;
					base_reglas.BaseReglas[base_reglas.n_reglas][j].y = base_total.BaseReglas[i][j].y;
				}
				
				base_reglas.BaseP[base_reglas.n_reglas] = cromosomaR[i];
				base_reglas.n_reglas++;
			}
		}
	}


/* --------------- Criterios especificos de la aplicacion ----------------- */

	/* Error Cuadratico */
	double ErrorCuadratico () {
		int i;
		double suma;

		for (i=0,suma=0.0; i<tabla.long_tabla; i++)
			suma += 0.5 * Math.pow (tabla.datos[i].ejemplo[tabla.n_var_estado]-base_reglas.FLC (tabla.datos[i].ejemplo),2.0);

		return (suma / (double)tabla.long_tabla);
	}


	/* Errores Cuadratico y Lineal */
	void Error_tra () {
		int i,j;
		double suma1, suma2, fuerza;

		for (j=0, suma1=suma2=0.0; j<tabla.long_tabla; j++) {
			fuerza=base_reglas.FLC (tabla.datos[j].ejemplo);
			suma1 += 0.5 * Math.pow (tabla.datos[j].ejemplo[tabla.n_var_estado]-fuerza,2.0);
			suma2 += Math.abs (tabla.datos[j].ejemplo[tabla.n_var_estado]-fuerza);
		}

		EC = suma1 / (double)tabla.long_tabla;
		EL = suma2 / (double)tabla.long_tabla;
	}

	/* Errores Cuadratico y Lineal */
	void Error_tst (MiDataset tabla_tst) {
		int i, j;
		double suma1, suma2, fuerza;

		for (j=0,suma1=suma2=0.0; j<tabla_tst.long_tabla; j++) {
			fuerza=base_reglas.FLC (tabla_tst.datos[j].ejemplo);
			suma1 += 0.5 * Math.pow (tabla_tst.datos[j].ejemplo[tabla.n_var_estado]-fuerza,2.0);
			suma2 += Math.abs (tabla_tst.datos[j].ejemplo[tabla.n_var_estado]-fuerza);
		}

		EC = suma1 / (double)tabla_tst.long_tabla;
		EL = suma2 / (double)tabla_tst.long_tabla;
	}

/* ---------------------------- Funcion fitness --------------------------- */

	double eval (char [] cromosoma, double [] cromosomaR) {
		return (eval_EC (cromosoma, cromosomaR));
	}


	/* Funcion fitness empleada por Paco y Manolo: Se minimiza el error cuadratico
	siempre que la base posea un grado de cubrimiento minimo superior a tau. El
	cromosoma toma un valor maximo de fitness cuando la base que codifica no
	presenta el grado de cubrimiento exigido */
	double eval_EC (char [] cromosoma, double [] cromosomaR) {
		int i;
		double ec, Pen_nicho;

		/* Se calcula la adecuacion de la base de conocimiento codificada en el
		cromosoma actual, se estudia la posible penalizacion del mismo y se
		devuelve el valor final */
		Decodifica (cromosoma, cromosomaR);
		Cubrimientos_Base ();

		if (ISHI==1) {
			if (gl_ini==0) {
				ec_ini = ErrorCuadratico ();
				gl_ini=1;
			}
			ec = ErrorCuadratico()+ 0.1 * ec_ini * (base_reglas.n_reglas / (double)base_total.n_reglas);
		}
		else  ec = ErrorCuadratico ();

		return (ec);
	}




/* -------------------------------------------------------------------------
			   Funcion de penalizacion
  ------------------------------------------------------------------------- */


	public void guardar_solucion (char [] cromosoma, double [] cromosomaR) {
		int i;

		for (i=0; i<base_total.n_reglas; i++) {
			ListaTabu[cont_soluciones][i] = cromosoma[i];
			ListaTabuR[cont_soluciones][i] = cromosomaR[i];
		}

		cont_soluciones++;
	}


/* -------------------------------------------------------------------------
             Funciones comunes
   ------------------------------------------------------------------------- */

	/* salida */
	public String getSalidaObli (MiDataset tabla_datos) {
		int j;
		double fuerza;
		String salida;

		salida = "@data\n";
		for (j=0; j<tabla_datos.long_tabla; j++) {
			fuerza = base_reglas.FLC (tabla_datos.datos[j].ejemplo);
			salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] + " " + fuerza + " " + "\n";
		}

		salida = salida.substring(0, salida.length()-1);

		return (salida);
	}
}



