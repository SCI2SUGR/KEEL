/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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

package keel.Algorithms.RE_SL_Methods.mogulTSK_IRL;

import java.lang.Math;
import org.core.*;

class Est_evol {

  public double sigma;
  public int n_genes_ee_11, n_gen_ee_11;
  public long n_mutaciones, n_exitos;

  public static double c = 0.9; /*0.817*/

  public BaseD base_datos;
  public Adap fun_adap;
  int n_var_estado;

  public Est_evol(BaseD base_da, Adap fun, int n_var_est, int gen_ee) {
    base_datos = base_da;
    fun_adap = fun;

    n_var_estado = n_var_est;
    n_genes_ee_11 = (n_var_estado * 4) + n_var_estado + 1;
    n_gen_ee_11 = gen_ee;
  }

  /** Calculates the new value of sigma according to the number of mutation with hit*/
  private double AdaptacionSigma(double old_sigma, double p, double n) {
    /* if p<1/5, sigma lowers (c<1 -> sigma*c^(1/n)<sigma) */
    if (p < 0.2) {
      return (old_sigma * Math.pow(c, 1.0 / n));
    }

    /* if p>1/5, sigma increases (c<1 -> sigma/c^(1/n)>sigma)*/
    if (p > 0.2) {
      return (old_sigma / Math.pow(c, 1.0 / n));
    }

    /* if p=1/5, sigma doesn't change*/
    return (old_sigma);
  }

  /** Generates a normal value with hope 0 and tipical deviation "desv */
  public double ValorNormal(double desv) {
    double u1, u2;

    /* we generate 2 uniform values [0,1] */
    u1 = Randomize.Rand();
    u2 = Randomize.Rand();

    /* we calcules a normal value with the uniform values */
    return (desv * Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * Math.PI * u2));
  }

  /** Evolution Strategy */
  public void Estrategia_Evolucion(Structure Padre, Structure Hijo) {
    int j, variable, etiqueta, gen, it_sin_exito, fin;
    double x0, x1, x2, newx1, newx, S, n, new_sigma;

    /* Inicialization of the counters */
    n_mutaciones = n_exitos = it_sin_exito = fin = 0;
    sigma = new_sigma = 1.0;
    Padre.Perf = fun_adap.eval_11(Padre.Gene, 1);

    do {
      for (variable = 0; variable < n_var_estado; variable++) {
        /* we copy the C1 part of the father in the son */
        Hijo.Gene[variable] = Padre.Gene[variable];
        etiqueta = (int) Padre.Gene[variable];
        gen = n_var_estado + 3 * variable;

        /* we obtain the fuzzy set */
        x0 = Padre.Gene[gen];
        x1 = Padre.Gene[gen + 1];
        x2 = Padre.Gene[gen + 2];

        /* Adaptation of S and mutation of the center point */
        S = Adap.Minimo(x1 - x0, x2 - x1) / 2.0;
        n = ValorNormal(new_sigma * S);
        newx1 = x1 + n;

        if (newx1 <= x0) {
          Hijo.Gene[gen + 1] = x0;
          newx1 = x0;
        }
        else {
          if (newx1 >= x2) {
            Hijo.Gene[gen + 1] = x2;
            newx1 = x2;
          }
          else {
            Hijo.Gene[gen + 1] = newx1;
          }
        }

        /* Adaptation of S and mutation of the left point */
        S = Adap.Minimo(x0 - base_datos.intervalos[variable][etiqueta].min,
                        newx1 - x0) / 2.0;
        n = ValorNormal(new_sigma * S);
        newx = x0 + n;
        if (newx <= base_datos.intervalos[variable][etiqueta].min) {
          Hijo.Gene[gen] = base_datos.intervalos[variable][etiqueta].min;
        }
        else {
          if (newx >= newx1) {
            Hijo.Gene[gen] = newx1;
          }
          else {
            Hijo.Gene[gen] = newx;
          }
        }

        /* Adaptation of S and mutation of the center right */
        S = Adap.Minimo(x2 - newx1,
                        base_datos.intervalos[variable][etiqueta].max - x2) /
            2.0;
        n = ValorNormal(new_sigma * S);
        newx = x2 + n;

        if (newx <= newx1) {
          Hijo.Gene[gen + 2] = newx1;
        }
        else {
          if (newx >= base_datos.intervalos[variable][etiqueta].max) {
            Hijo.Gene[gen + 2] = base_datos.intervalos[variable][etiqueta].max;
          }
          else {
            Hijo.Gene[gen + 2] = newx;
          }
        }
      }

      for (j = n_var_estado + 3 * n_var_estado; j < n_genes_ee_11; j++) {
        Hijo.Gene[j] = Padre.Gene[j];
      }

      /* we evaluate the son */
      if ( (Hijo.Perf = fun_adap.eval_11(Hijo.Gene, 1)) <= 0) {
        Hijo.Perf = Padre.Perf + 1; /* si sale 0 es que no tiene cubrimiento */
      }

      /* we count the mutation */
      n_mutaciones += 1;

      /* if the son is better than the father this relieve his father, we accept sigma and we count another hit */
      if (Hijo.Perf < Padre.Perf) {
        /*if (FH>=FP && GH>=GP && gH>=gP && PNH>=PNP)*/
        n_exitos += 1;
        it_sin_exito = 0;
        sigma = new_sigma;

        for (j = 0; j < n_genes_ee_11; j++) {
          Padre.Gene[j] = Hijo.Gene[j];
        }

        Padre.Perf = Hijo.Perf;
      }
      else {
        it_sin_exito++;
      }

      /* we adapt sigma */
      new_sigma = AdaptacionSigma(sigma, n_exitos / (double) n_mutaciones,
                                  (double) n_genes_ee_11 - n_var_estado);

      if (it_sin_exito >= n_gen_ee_11) {
        fin = 1;
      }

    }
    while (fin == 0);

    /* we obtain the positive examples of the current rule */
    fun_adap.ejemplos_positivos(Padre.Gene);
  }

  /** Initialization of the antecedent */
  public void inicializa_ant(Structure Padre, int regla, BaseR base_reglas) {
    int i, pos_individuo, etiqueta;

    for (i = 0; i < n_var_estado; i++) {
      etiqueta = base_reglas.Pob_reglas[regla][i];
      pos_individuo = n_var_estado + 3 * i;
      Padre.Gene[i] = (double) etiqueta;
      Padre.Gene[pos_individuo] = base_datos.BaseDatos[i][etiqueta].x0;
      Padre.Gene[pos_individuo + 1] = base_datos.BaseDatos[i][etiqueta].x1;
      Padre.Gene[pos_individuo + 2] = base_datos.BaseDatos[i][etiqueta].x3;
    }
  }
}

