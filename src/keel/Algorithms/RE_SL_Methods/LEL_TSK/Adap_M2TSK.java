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


class Adap_M2TSK {

	public double [] grado_pertenencia;
	public double EC, EL;
	public int n_ejemplos_positivos;
	public int [] indices_ep;

	public MiDataset tabla, tabla_tst;
	public BaseR_TSK base_reglas;

	public Adap_M2TSK (MiDataset training, MiDataset test, BaseR_TSK base) {
		int i;

		tabla = training;
		tabla_tst = test;
		base_reglas = base;

		indices_ep = new int [tabla.long_tabla];
		grado_pertenencia = new double[tabla.n_var_estado];
		n_ejemplos_positivos = 0;
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
                               FITNESS FUNCTION
  ------------------------------------------------------------------------- */

/* ------------------------- Criteria of rules -------------------------- */

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



	/** Inference process with the rule "Consecuentes" */
	double Alfa_Error (double [] Consecuentes) {
		int i, j;
		double suma, salida;
		float aux;

		for (i=0, suma=0.0; i<n_ejemplos_positivos; i++) {
			salida = Math.tan (Consecuentes[tabla.n_var_estado]);
			for (j=0; j<tabla.n_var_estado; j++) {
				salida += Math.tan (Consecuentes[j]) * tabla.datos[indices_ep[i]].ejemplo[j];
			}

			suma += tabla.datos[indices_ep[i]].nivel_cubrimiento * Math.pow (tabla.datos[indices_ep[i]].ejemplo[tabla.n_var_estado]-salida, 2.0);
		}

		return (suma);
	}


	/** Mean Square Error(MSE) and Mean Linear Error(MLE) by training */
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

	/** Mean Square Error(MSE) and Mean Linear Error(MLE) by test */
	public void Error_tst () {
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


	/** Returns the fitness of the rule "cromosoma" */
	double eval (double [] cromosoma) {
		return (Alfa_Error (cromosoma));
	}



	/** Calculates the positive examples of the rule "regla" */
	public void ejemplos_positivos (int regla) {
		int i;

		n_ejemplos_positivos = 0;
		for (i=0; i < tabla.long_tabla; i++) {
			tabla.datos[i].nivel_cubrimiento = AntecedenteCubreEjemplo (base_reglas.BaseReglas[regla].Ant, tabla.datos[i].ejemplo);
			if (tabla.datos[i].nivel_cubrimiento > 0.0) {
				indices_ep[n_ejemplos_positivos] = i;
				n_ejemplos_positivos++;
			}
		}
	}

/* -------------------------------------------------------------------------
             Common Function
   ------------------------------------------------------------------------- */

	/** Returns the data for creating the KEEL output file */
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

