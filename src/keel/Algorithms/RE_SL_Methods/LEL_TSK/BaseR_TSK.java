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

import java.io.*;
import org.core.*;
import java.util.*;

class BaseR_TSK {

  public Regla[] BaseReglas;
  public int max_reglas;
  public int n_reglas;
  public MiDataset tabla;

  public double[] GradoEmp;
  public int[] allZero;

  public BaseR_TSK(String fichero, MiDataset datos, boolean sel) {
    int i;

    tabla = datos;
    leer_BR(fichero, sel);
    max_reglas = n_reglas;
    allZero = new int[tabla.n_var_estado];

    GradoEmp = new double[n_reglas];
  }

  public BaseR_TSK(int Max_reglas, MiDataset datos) {
    int i, j;

    tabla = datos;
    n_reglas = 0;
    max_reglas = Max_reglas;

    BaseReglas = new Regla[max_reglas];

    GradoEmp = new double[max_reglas];
    allZero = new int[tabla.n_var_estado];
	

    for (i = 0; i < max_reglas; i++) {
      BaseReglas[i] = new Regla(tabla.n_var_estado, tabla.n_variables);
    }
  }

  /** Reads the RB of a input file */
  public void leer_BR(String fichero, boolean sel) {
    int i, j;
    String cadena;

    cadena = Fichero.leeFichero(fichero);

    StringTokenizer sT = new StringTokenizer(cadena, "\n\r\t ", false);
    sT.nextToken();
    sT.nextToken();
    sT.nextToken();

    n_reglas = Integer.parseInt(sT.nextToken());

    BaseReglas = new Regla[n_reglas];
    for (i = 0; i < n_reglas; i++) {
      BaseReglas[i] = new Regla(tabla.n_var_estado, tabla.n_variables);
    }

    for (i = 0; i < n_reglas; i++) {
      for (j = 0; j < tabla.n_var_estado; j++) {
        BaseReglas[i].Ant[j].x0 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].x1 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].x2 = BaseReglas[i].Ant[j].x1;
        BaseReglas[i].Ant[j].x3 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].y = 1.0;
      }
	  
      if (sel) {
        for (j = 0; j < tabla.n_variables; j++) {
          BaseReglas[i].Cons[j] = Double.parseDouble(sT.nextToken());
        }
      }
      else {
        /* We don't store the consequent */
        for (j = 0; j < 3; j++) {
          Double.parseDouble(sT.nextToken());
        }
      }
    }
  }

  /* -------------------------------------------------------------------------
          Fuzzification Interface
   ------------------------------------------------------------------------- */

  public double Fuzzifica(double X, Difuso D) {
    /* If X are not in the rank D, the degree is 0 */
    if ( (X < D.x0) || (X > D.x3)) {
      return (0);
    }
    if (X < D.x1) {
      return ( (X - D.x0) * (D.y / (D.x1 - D.x0)));
    }
    if (X > D.x2) {
      return ( (D.x3 - X) * (D.y / (D.x3 - D.x2)));
    }

    return (D.y);
  }

  /* -------------------------------------------------------------------------
         Conjunction Operator
   ------------------------------------------------------------------------- */

  /* T-norma Minimal */
  public void Min(double[] entradas) {
    int b, b2;
    double minimo, y;

    for (b = 0; b < n_reglas; b++) {
      minimo = Fuzzifica(entradas[0], BaseReglas[b].Ant[0]);

      for (b2 = 1; (minimo != 0.0) && (b2 < tabla.n_var_estado); b2++) {
        y = Fuzzifica(entradas[b2], BaseReglas[b].Ant[b2]);
        if (y < minimo) {
          minimo = y;
        }
      }

      GradoEmp[b] = minimo;
    }
  }

  /* -------------------------------------------------------------------------
                    Inference of a TSK Fuzzy System
   ------------------------------------------------------------------------- */

  public double Inferencia_TSK(double[] Entrada) {
    double num, den, salida_regla;
    int i, j;

    num = 0;
    den = 0;
    for (i = 0; i < n_reglas; i++) {
      if (GradoEmp[i] != 0.0) {
        /* we initialize the output to the 'b' value */
        salida_regla = BaseReglas[i].Cons[tabla.n_var_estado];

        for (j = 0; j < tabla.n_var_estado; j++) {
          salida_regla += BaseReglas[i].Cons[j] * Entrada[j];
        }

        num += GradoEmp[i] * salida_regla;
        den += GradoEmp[i];
      }
    }

    if (den != 0) {
      return (num / den);
    }
    else {
      return ( (tabla.extremos[tabla.n_var_estado].max - tabla.extremos[tabla.n_var_estado].min) / 2.0);
    }
  }

  /* -------------------------------------------------------------------------
           Fuzzy Controller
   ------------------------------------------------------------------------- */

  public double FLC_TSK(double[] Entrada) {
    Min(Entrada);
    return (Inferencia_TSK(Entrada));
  }

  /** RB to String */
  public String BRtoString() {
    int i, j;
    String cadena = "";

    cadena += "Numero de reglas: " + n_reglas + "\n\n";
    for (i = 0; i < n_reglas; i++) {
      for (j = 0; j < tabla.n_var_estado; j++) {
        cadena += "" + BaseReglas[i].Ant[j].x0 + " " +  BaseReglas[i].Ant[j].x1 +
            " " + BaseReglas[i].Ant[j].x3 +"\n";
      }

      for (j = 0; j < tabla.n_variables; j++) {
        cadena += BaseReglas[i].Cons[j] + " ";
      }

      cadena += "\n\n";
    }

    return (cadena);
  }

  /** Inserts the consequent of the rule "regla" in the RB */
  public void inserta_cons(int regla, double[] consecuente, Adap_M2TSK fun_adap) {
    int i;

    for (i = 0; i < tabla.n_var_estado; i++) {
      allZero[i] = 1;
    }
	
	for (int j=0; j < fun_adap.n_ejemplos_positivos; j++)
       for (int k=0;  k < tabla.n_var_estado; k++) {
          if (tabla.datos[fun_adap.indices_ep[j]].ejemplo[k] != 0.0) {
			  allZero[k] = 0;
		  }
	   }
    
	/* 'a' values of the consequent */
    for (i = 0; i < tabla.n_var_estado; i++) {
	  if (allZero[i] == 1)  BaseReglas[regla].Cons[i] = 0.0;
      else  BaseReglas[regla].Cons[i] = Math.tan(consecuente[i]);
    }

    /* 'b' values of the consequent */
    BaseReglas[regla].Cons[tabla.n_var_estado] = Math.tan(consecuente[tabla.n_var_estado]);
  }

  public void clean (Adap_Tun fun_adap) {
    int i, j, k;

	for (i = 0; i < this.n_reglas; i++) {						 
		for (j = 0; j < tabla.n_var_estado; j++)  allZero[j] = 1;
		for (j = 0; j < tabla.long_tabla; j++) {
			if (fun_adap.AntecedenteCubreEjemplo (BaseReglas[i].Ant, tabla.datos[j].ejemplo) > 0.0) {
				for (k = 0; k < tabla.n_var_estado; k++) {
					if (tabla.datos[j].ejemplo[k]!= 0.0)  allZero[k] = 0;
				}
			}
		}
		
		for (k=0; k < tabla.n_var_estado; k++)
			if (allZero[k] == 1)  BaseReglas[i].Cons[k] = 0.0;
	}
  }
}
