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

package keel.Algorithms.RE_SL_Methods.TSK_IRL;

import java.io.*;
import org.core.*;
import java.util.*;

class BaseR {

  public Regla[] BaseReglas;
  public int max_reglas;
  public int n_reglas, n_etiq_distintas;

  public double[] GradoEmp;
  public double[] cromosoma;
  public double[][] ListaTabu;
  public int[] Regla_act;
  public int [][] b_reglas;
  public int[][] Pob_reglas;

  public MiDataset tabla;
  public BaseD base_datos;

  public BaseR(BaseD base_d, MiDataset t) {
    int i;

    this.tabla = t;
    this.base_datos = base_d;
    this.n_reglas = 0;

    this.max_reglas = 1;
    for (i = 0; i < tabla.n_var_estado; i++) {
      max_reglas *= base_datos.n_etiquetas[i];
    }

    Regla_act = new int[tabla.n_var_estado];
    cromosoma = new double[tabla.n_var_estado * 5 + 1];
    GradoEmp = new double[max_reglas];
    ListaTabu = new double[max_reglas][tabla.n_var_estado];
    Pob_reglas = new int[max_reglas][tabla.n_var_estado];

    BaseReglas = new Regla[this.max_reglas];
    for (i = 0; i < this.max_reglas; i++) {
      BaseReglas[i] = new Regla(tabla.n_var_estado, tabla.n_variables);
    }
  }

  public BaseR(String fichero, MiDataset datos) {
    int i;

    tabla = datos;
    leer_BR(fichero);
	max_reglas = n_reglas;
	b_reglas = new int[n_reglas][tabla.n_variables];

    GradoEmp = new double[n_reglas];
  }

  /** Reads the RB of a input file */
  public void leer_BR(String fichero) {
    int i, j, k, repetida;
    String cadena;

	System.out.println("\n\nEntrando en LeerBR");
    cadena = Fichero.leeFichero(fichero);

    StringTokenizer sT = new StringTokenizer(cadena, "\n\r\t ", false);
    sT.nextToken();
    sT.nextToken();
    sT.nextToken();

    n_reglas = Integer.parseInt(sT.nextToken());
	System.out.println("\n\nEntrando en LeerBR");

    BaseReglas = new Regla[n_reglas];
    for (i = 0; i < n_reglas; i++) {
      BaseReglas[i] = new Regla(tabla.n_var_estado, tabla.n_variables);
    }

	this.n_etiq_distintas = 0;

	for (i = 0; i < n_reglas; i++) {
      for (j = 0; j < tabla.n_var_estado; j++) {
        BaseReglas[i].Ant[j].x0 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].x1 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].x2 = BaseReglas[i].Ant[j].x1;
        BaseReglas[i].Ant[j].x3 = Double.parseDouble(sT.nextToken());
        BaseReglas[i].Ant[j].y = 1.0;

		k = repetida = 0;
		while (k < i && repetida == 0) {
			if (BaseReglas[i].Ant[j].x0==BaseReglas[k].Ant[j].x0 && BaseReglas[i].Ant[j].x1==BaseReglas[k].Ant[j].x1 && BaseReglas[i].Ant[j].x3==BaseReglas[k].Ant[j].x3) {
				repetida = 1;
			}
			else {
				k++;
			}
		}
			
		if (repetida == 0)  this.n_etiq_distintas++;
	  }

      for (j = 0; j < tabla.n_variables; j++) {
        BaseReglas[i].Cons[j] = Double.parseDouble(sT.nextToken());
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

  /* -------------------------------------------------------------------------
           Generic Function
   ------------------------------------------------------------------------- */



  /** If "Antecedente" has positive examples it's stored in the structure "Pob_reglas" */
  void CompruebaEjemplos(int[] Antecedente, Adap fun_adap) {
    int i, hay_ejemplos, etiqueta, pos_individuo;

    for (i = 0; i < tabla.n_var_estado; i++) {
      etiqueta = Antecedente[i];
      pos_individuo = tabla.n_var_estado + 3 * i;
      cromosoma[i] = (double) etiqueta;
      cromosoma[pos_individuo] = base_datos.BaseDatos[i][etiqueta].x0;
      cromosoma[pos_individuo + 1] = base_datos.BaseDatos[i][etiqueta].x1;
      cromosoma[pos_individuo + 2] = base_datos.BaseDatos[i][etiqueta].x3;
    }

    i = hay_ejemplos = 0;
    /* we look for a positive example */
    while (i < tabla.long_tabla && hay_ejemplos == 0) {
      if (fun_adap.AntecedenteCubreEjemplo(cromosoma, tabla.datos[i].ejemplo) >
          0) {
        hay_ejemplos = 1;
      }
      else {
        i++;
      }
    }

    /* if there are positive examples the antecedent is stored in "Pob_reglas" */
    if (hay_ejemplos > 0) {
      for (i = 0; i < tabla.n_var_estado; i++) {
        Pob_reglas[n_reglas][i] = Antecedente[i];
      }
      n_reglas++;
    }
  }

  /** Returns all possible antecedents with positive examples*/
  void RecorreAntecedentes(int[] Regla_act, int pos, Adap fun_adap) {
    int i;

    if (pos == tabla.n_var_estado) {
      CompruebaEjemplos(Regla_act, fun_adap);
    }
    else {
      for (Regla_act[pos] = 0; Regla_act[pos] < base_datos.n_etiquetas[pos];
           Regla_act[pos]++) {
        RecorreAntecedentes(Regla_act, pos + 1, fun_adap);
      }
    }
  }

  /** Generates all possible antecedents */
  void Generate(Adap fun_adap) {
    int i, j, k, etiqueta;
    double grado_pertenencia, max_pert;

    n_reglas = 0;
    RecorreAntecedentes(Regla_act, 0, fun_adap);
  }

  /** Inserts a rule in the RB */
  public void inserta_regla(int regla, double[] datos) {
    int i, pos_individuo;

    for (i = 0; i < tabla.n_var_estado; i++) {
      pos_individuo = tabla.n_var_estado + 3 * i;
      ListaTabu[regla][i] = datos[pos_individuo + 1];
      BaseReglas[regla].Ant[i].x0 = datos[pos_individuo];
      BaseReglas[regla].Ant[i].x1 = datos[pos_individuo + 1];
      BaseReglas[regla].Ant[i].x2 = datos[pos_individuo + 1];
      BaseReglas[regla].Ant[i].x3 = datos[pos_individuo + 2];
      BaseReglas[regla].Ant[i].y = 1.0;

      /* 'a' values of the consequent */
      BaseReglas[regla].Cons[i] = Math.tan(datos[tabla.n_var_estado +
                                           tabla.n_var_estado * 3 + i]);
    }

    /* 'b' value of the consequent */
    BaseReglas[regla].Cons[i] = Math.tan(datos[tabla.n_var_estado +
                                         tabla.n_var_estado * 4]);
  }

  /** RB to String */
  public String BRtoString() {
    int i, j;
    String cadena = "";

    cadena += "Numero de reglas: " + n_reglas + "\n\n";
    for (i = 0; i < n_reglas; i++) {
      for (j = 0; j < tabla.n_var_estado; j++) {
        cadena += "" + BaseReglas[i].Ant[j].x0 + " " + BaseReglas[i].Ant[j].x1 +
            " " + BaseReglas[i].Ant[j].x3 + "\n";
      }

      for (j = 0; j < tabla.n_variables; j++) {
        cadena += BaseReglas[i].Cons[j] + " ";
      }

      cadena += "\n\n";
    }

    return (cadena);
  }

}

