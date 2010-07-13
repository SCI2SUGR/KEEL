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

package keel.Algorithms.RE_SL_Methods.mogulHC;

import java.lang.Math;
import org.core.*;

class Est_evol {

  public int n_gen_ee;
  public int long_poblacion, n_genes;
  public long n_mutaciones, n_exitos;
  public double porc_pob_ee;
  public Structure Hijo;
  public Structure[] New;
  public Structure[] YaExplotados;

  public int[] indices;

  public static double PI = 3.1415926;
  public static double c = 0.9;

  public BaseD base_datos;
  public Adap fun_adap;

  public Est_evol(BaseD base, Adap fun, AG alg_gen, double porc, int n_iter) {
    int i;

    fun_adap = fun;
    base_datos = base;

    New = alg_gen.New;
    long_poblacion = alg_gen.long_poblacion;
    n_genes = alg_gen.n_genes;

    porc_pob_ee = porc;
    n_gen_ee = n_iter;

    indices = new int[long_poblacion];
    Hijo = new Structure(n_genes);

    YaExplotados = new Structure[long_poblacion];
    for (i = 0; i < long_poblacion; i++) {
      YaExplotados[i] = new Structure(n_genes);
    }
  }

  /** Calculates the new value of sigma according to the number of mutation with hit*/
  public double AdaptacionSigma(double old_sigma, double p, double n) {
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
    return (desv * Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * PI * u2));
  }

  /** Returns 1 if the current index is in the list of selected indexes */
  int Pertenece(Structure C, Structure[] L, int n_explotados) {
    int crom, gen, esta;

    crom = 0;
    while ( (crom < n_explotados) && (L[crom].Perf > C.Perf)) {
      esta = 1;
      gen = 0;
      while (gen < n_genes && esta == 1) {
        if (C.Gene[gen] != L[crom].Gene[gen]) {
          esta = 0;
        }
        else {
          gen++;
        }
      }

      if (esta == 1) {
        return (1);
      }
      crom++;
    }

    return (0);
  }

  /** Evolution Strategy */
  public void Estrategia_Evolucion() {
    int i, j, temp, cromosoma, variable, etiqueta, punto, it_sin_exito,
        n_ya_explotados, n_a_explotar, fin;
    double n, sigma, newval, new_sigma, old_perf;

    /* we order the population by mean of the bubble method */
    for (i = 0; i < long_poblacion; i++) {
      indices[i] = i;
    }

    for (i = 0; i < long_poblacion; i++) {
      for (j = 0; j < long_poblacion - i - 1; j++) {
        if (New[indices[j + 1]].Perf > New[indices[j]].Perf) {
          temp = indices[j];
          indices[j] = indices[j + 1];
          indices[j + 1] = temp;
        }
      }
    }

    /* the evolution strategy is applied to each individual of the population with fitness better than 0 */
    i = 0;
    n_ya_explotados = 0;
    n_a_explotar = (int) (porc_pob_ee * long_poblacion);

    while ( (i < long_poblacion) && (n_ya_explotados < n_a_explotar)) {
      /* we initialize the index of the chromosome */
      cromosoma = indices[i];

      /* we store this chromosome in the list of the exploited */
      for (j = 0; j < n_genes; j++) {
        YaExplotados[n_ya_explotados].Gene[j] = New[cromosoma].Gene[j];
      }

      YaExplotados[n_ya_explotados].Perf = New[cromosoma].Perf;
      n_ya_explotados++;

      /* Inicialization of the counters */
      n_mutaciones = n_exitos = it_sin_exito = fin = 0;
      sigma = new_sigma = 1.0;

      do {
        /* we copy the C1 part of the father in the son */
        for (j = 0; j < base_datos.n_variables; j++) {
          Hijo.Gene[j] = New[cromosoma].Gene[j];
        }

        /* we generate the C2 part of the sons by means of the ES */
        variable = -1;
        etiqueta = (int) New[cromosoma].Gene[0];
        for (j = base_datos.n_variables; j < n_genes; j++) {
          punto = (j - base_datos.n_variables) % 3;
          if (punto == 0) {
            variable++;
            etiqueta = (int) New[cromosoma].Gene[variable];
          }

          /* we generate the normal value with the tipical desviation of each variable */
          n = ValorNormal(new_sigma * base_datos.S[variable]);

          /* We apply the mutation. If a value is outside mutation interval it is revised */
          newval = New[cromosoma].Gene[j] + n;

          if (newval <= base_datos.intervalos[variable][etiqueta][punto].min) {
            Hijo.Gene[j] = base_datos.intervalos[variable][etiqueta][punto].min;
          }
          else {
            if (newval >= base_datos.intervalos[variable][etiqueta][punto].max) {
              Hijo.Gene[j] = base_datos.intervalos[variable][etiqueta][punto].
                  max;
            }
            else {
              Hijo.Gene[j] = newval;
            }
          }
        }

        /* we evaluate the son */
        Hijo.Perf = fun_adap.eval(Hijo.Gene);

        /* we count the mutation */
        n_mutaciones += 1;

        /* if the son is better than the father this relieve his father, we accept sigma and we count another hit */
        if (Hijo.Perf > New[cromosoma].Perf) {
          n_exitos += 1;
          it_sin_exito = 0;
          sigma = new_sigma;

          for (j = 0; j < n_genes; j++) {
            New[cromosoma].Gene[j] = Hijo.Gene[j];
          }

          New[cromosoma].Perf = Hijo.Perf;
          New[cromosoma].n_e = 0;
        }
        else {
          it_sin_exito++;
        }

        /* we adapt sigma */
        new_sigma = AdaptacionSigma(sigma,
                                    (double) n_exitos / (double) n_mutaciones,
                                    (double) n_genes - base_datos.n_variables);

        if (it_sin_exito >= n_gen_ee) {
          fin = 1;
        }
      }
      while (fin == 0);

      /* we look for the next unrecurrent individual */
      if (n_ya_explotados < n_a_explotar) {
        do {
          i++;
        }
        while (Pertenece(New[indices[i]], YaExplotados, n_ya_explotados) == 1);
      }
    }
  }

}

