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

package keel.Algorithms.RE_SL_Methods.LEL_TSK;

import java.lang.Math;


class Adap_Tun {
	public double EC, EL;
	public int primer_gen_C2;

	public MiDataset tabla;
	public BaseR_TSK base_reglas;
	public double[] grado_pertenencia;

	public Adap_Tun (MiDataset training, BaseR_TSK base) {
		int i;

		tabla = training;
		base_reglas = base;

		grado_pertenencia = new double[tabla.n_var_estado];
		primer_gen_C2 = 3 * tabla.n_var_estado * base_reglas.n_reglas;
	}


	public static double Minimo (double x, double y) {
		if (x<y)  return (x);
		else  return (y);
	}


	public static double Maximo (double x, double y) {
		if (x > y)  return (x);
		else  return (y);
	}



	/** Returns the matching degree of the rule "Ri(ek)" with the instance "ejem" */
	double AntecedenteCubreEjemplo (Difuso [] AntRegla, double [] ejem) {
		int i;
		double min;

		for (i=0; i<tabla.n_var_estado; i++)
			grado_pertenencia[i] = base_reglas.Fuzzifica (ejem[i], AntRegla[i]);

		min = 1;
		for(i=0; i<tabla.n_var_estado; i++)
			if (grado_pertenencia[i]<min)  min = grado_pertenencia[i];

		return (min);
	}


	/* ---------------------- Decodificacion del cromosoma -------------------- */

	/* Pasa la Base de Conocimiento codificada en el cromosoma a una estructura
	adecuada para inferir */
	void Decodifica (double [] cromosoma) {
		int i, j;

		for (i=0; i<base_reglas.n_reglas; i++) {
			for (j=0; j<tabla.n_var_estado; j++) {
				base_reglas.BaseReglas[i].Ant[j].x0 = cromosoma[3*(i*tabla.n_var_estado+j)];
				base_reglas.BaseReglas[i].Ant[j].x1 = cromosoma[3*(i*tabla.n_var_estado+j)+1];
				base_reglas.BaseReglas[i].Ant[j].x2 = cromosoma[3*(i*tabla.n_var_estado+j)+1];
				base_reglas.BaseReglas[i].Ant[j].x3 = cromosoma[3*(i*tabla.n_var_estado+j)+2];
				base_reglas.BaseReglas[i].Ant[j].y = 1.0;
				base_reglas.BaseReglas[i].Ant[j].Nombre = "x" + (j+1);
				base_reglas.BaseReglas[i].Ant[j].Etiqueta = "E" + i + j;
				base_reglas.BaseReglas[i].Cons[j] = Math.tan (cromosoma[primer_gen_C2+i*(tabla.n_variables)+j]);
			}

			base_reglas.BaseReglas[i].Cons[j] = Math.tan (cromosoma[primer_gen_C2+i*(tabla.n_variables)+j]);
		}
	}


/* ------------------------ Medidas Globales de Error --------------------- */

	/* Error Cuadratico */
	public double ErrorCuadratico () {
		int i;
		double suma;

		for (i=0,suma=0.0; i<tabla.long_tabla; i++)
			suma += Math.pow (tabla.datos[i].ejemplo[tabla.n_var_estado]-base_reglas.FLC_TSK (tabla.datos[i].ejemplo),2.0);

		return (suma / (double)tabla.long_tabla);
	}


	/* Errores Cuadratico y Lineal */
	public void Error_tra () {
		int i,j;
		double suma1, suma2, fuerza;

		for (j=0, suma1=suma2=0.0; j<tabla.long_tabla; j++) {
			fuerza=base_reglas.FLC_TSK (tabla.datos[j].ejemplo);
			suma1 += Math.pow (tabla.datos[j].ejemplo[tabla.n_var_estado]-fuerza,2.0);
			suma2 += Math.abs (tabla.datos[j].ejemplo[tabla.n_var_estado]-fuerza);
		}

		EC = suma1 / (double)tabla.long_tabla;
		EL = suma2 / (double)tabla.long_tabla;
	}

	/* Errores Cuadratico y Lineal */
	public void Error_tst (MiDataset tabla_tst) {
		int i, j;
		double suma1, suma2, fuerza;

		for (j=0,suma1=suma2=0.0; j<tabla_tst.long_tabla; j++) {
			fuerza=base_reglas.FLC_TSK (tabla_tst.datos[j].ejemplo);
			suma1 += Math.pow (tabla_tst.datos[j].ejemplo[tabla.n_var_estado]-fuerza,2.0);
			suma2 += Math.abs (tabla_tst.datos[j].ejemplo[tabla.n_var_estado]-fuerza);
		}

		EC = suma1 / (double)tabla_tst.long_tabla;
		EL = suma2 / (double)tabla_tst.long_tabla;
	}

/* ---------------------------- Funcion fitness --------------------------- */

	double eval (double [] cromosoma) {
		Decodifica (cromosoma);
		return (ErrorCuadratico ());
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
			fuerza = base_reglas.FLC_TSK (tabla_datos.datos[j].ejemplo);
			salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] + " " + fuerza + " " + "\n";
		}

		salida = salida.substring(0, salida.length()-1);

		return (salida);
	}
}
