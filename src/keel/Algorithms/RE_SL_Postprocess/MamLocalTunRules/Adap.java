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

package keel.Algorithms.RE_SL_Postprocess.MamLocalTunRules;
import java.lang.Math;


class Adap {

	public double [] grado_pertenencia;
	public double medcb, mincb, tau;
	public double maxEC;
	public double EC, EL;
	public int long_regla;
	public int tipo_fitness;

	public MiDataset tabla;
	public BaseR base_reglas;

	public Adap (MiDataset training, BaseR base, double valor_tau, int tipo) {
		int i;

		tabla = training;
		base_reglas = base;

		tau = valor_tau;
		tipo_fitness = tipo;
		long_regla = 3 * tabla.n_variables;

		maxEC = 0.0;
		for (i=0; i<tabla.long_tabla; i++)
			maxEC += Math.pow (tabla.datos[i].ejemplo[tabla.n_var_estado], 2.0);

		maxEC /= 2.0;

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
	void Decodifica (double [] cromosoma) {
		int i, j;

		for (i=0; i<base_reglas.n_reglas; i++) {
			for (j=0; j<tabla.n_variables; j++) {
				base_reglas.BaseReglas[i][j].x0 = cromosoma[long_regla * i + 3 * j];
				base_reglas.BaseReglas[i][j].x1 = cromosoma[long_regla * i + 3 * j + 1];
				base_reglas.BaseReglas[i][j].x2 = cromosoma[long_regla * i + 3 * j + 1];
				base_reglas.BaseReglas[i][j].x3 = cromosoma[long_regla * i + 3 * j + 2];
				base_reglas.BaseReglas[i][j].y = 1;
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

	double eval (double [] cromosoma) {
		if (tipo_fitness==1)  return (eval_EC (cromosoma));
		else  return (eval_EC_cubr(cromosoma));
	}


	double eval_EC (double [] cromosoma) {
		Decodifica (cromosoma);
		return (ErrorCuadratico ());
	}


	/* Funcion fitness que pondera el error cuadratico por la desviacion del grado
	de cubrimiento de la base con respecto al valor optimo 1 */
	double eval_EC_cubr (double [] cromosoma) {
		double ec, fitness;

		/* Se calcula la adecuacion de la base de conocimiento codificada en el
		cromosoma actual, se estudia la posible penalizacion del mismo y se
		devuelve el valor final */
		Decodifica (cromosoma);
		ec = ErrorCuadratico ();
		Cubrimientos_Base ();

		if (mincb >= tau)  fitness = ec;
		else  fitness = 10E37;

		return (fitness);
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



