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

class AG_Sel {
  public double prob_cruce, prob_mutacion;
  public int Mu_next, Trials;
  public double Best_current_perf;
  public int Best_guy;
  public int long_poblacion, n_genes;

  public int[] sample;
  public int last;

  public Structure[] Old;
  public Structure[] New;
  public Structure[] temp;
  public Adap_Sel fun_adap;

  public AG_Sel(int n_poblacion, int genes, double cruce, double mutacion,
            Adap_Sel funcion) {
    int i;

    long_poblacion = n_poblacion;
    n_genes = genes;
    prob_cruce = cruce;
    prob_mutacion = mutacion / (double) n_genes;
    fun_adap = funcion;

    sample = new int[long_poblacion];

    Old = new Structure[long_poblacion];
    New = new Structure[long_poblacion];

    for (i = 0; i < long_poblacion; i++) {
      Old[i] = new Structure(n_genes);
      New[i] = new Structure(n_genes);
    }
  }

  public void Intercambio() {
    temp = Old;
    Old = New;
    New = temp;
  }

  /** Inicialization of the population */
  public void Initialize() {
    int i, j;

    last = (int) ( (prob_cruce * long_poblacion) - 0.5);

    Trials = 0;

    if (prob_mutacion < 1.0) {
      Mu_next = (int) (Math.log(Randomize.Rand()) /
                       Math.log(1.0 - prob_mutacion));
      Mu_next++;
    }

    else {
      Mu_next = 1;
    }

    for (j = 0; j < n_genes; j++) {
      New[0].GeneSel[j] = '1';
    }
    New[0].n_e = 1;

    for (i = 1; i < long_poblacion; i++) {
      for (j = 0; j < n_genes; j++) {
        if (Randomize.RandintClosed(0, 1) == 0) {
          New[i].GeneSel[j] = '0';
        }
        else {
          New[i].GeneSel[j] = '1';
        }
      }

      New[i].n_e = 1;
    }
  }

  /* Selection based on the Baker's Estocastic Universal Sampling */
  void Select() {
    double expected, factor, perf, ptr, sum, rank_max, rank_min;
    int i, j, k, best, temp;

    rank_min = 0.75;

    /* we assign the ranking to each element:
         The best: ranking = long_poblacion-1
      The worse: ranking = 0 */
    for (i = 0; i < long_poblacion; i++) {
      Old[i].n_e = 0;
    }

    /* we look for the best ordered non element */
    for (i = 0; i < long_poblacion - 1; i++) {
      best = -1;
      perf = 0.0;
      for (j = 0; j < long_poblacion; j++) {
        if ( (Old[j].n_e == 0) && (best == -1 || Old[j].Perf < perf)) {
          perf = Old[j].Perf;
          best = j;
        }
      }

      /* we assign the ranking */
      Old[best].n_e = long_poblacion - 1 - i;
    }

    /* we normalize the ranking */
    rank_max = 2.0 - rank_min;
    factor = (rank_max - rank_min) / (double) (long_poblacion - 1);

    /* we assign the number of replicas of each chormosome according to the select probability */
    k = 0;
    ptr = Randomize.Rand();
    for (sum = i = 0; i < long_poblacion; i++) {
      expected = rank_min + Old[i].n_e * factor;
      for (sum += expected; sum >= ptr; ptr++) {
        sample[k++] = i;
      }
    }

    /* we complete the population if necessary */
    if (k != long_poblacion) {
      for (; k < long_poblacion; k++) {
        sample[k] = Randomize.RandintClosed(0, long_poblacion - 1);
      }
    }

    /* we shuffle the selected chromosomes */
    for (i = 0; i < long_poblacion; i++) {
      j = Randomize.RandintClosed(i, long_poblacion - 1);
      temp = sample[j];
      sample[j] = sample[i];
      sample[i] = temp;
    }

    /* we create the new population */
    for (i = 0; i < long_poblacion; i++) {
      k = sample[i];
      for (j = 0; j < n_genes; j++) {
        New[i].GeneSel[j] = Old[k].GeneSel[j];
      }

      New[i].Perf = Old[k].Perf;
      New[i].n_e = 0;
    }
  }

  /* Multipoint Crossover */
  void Cruce_Multipunto() {
    int mom, dad, xpoint1, xpoint2, i, j;
    char temp;

    for (mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      /* we generate the 2 crossing points */
      xpoint1 = Randomize.RandintClosed(0, n_genes - 1);

      if (xpoint1 != n_genes - 1) {
        xpoint2 = Randomize.RandintClosed(xpoint1 + 1, n_genes - 1);
      }

      else {
        xpoint2 = n_genes - 1;
      }

      /* we cross the individuals between xpoint1 and xpoint2 */
      for (i = xpoint1; i <= xpoint2; i++) {
        temp = New[mom].GeneSel[i];
        New[mom].GeneSel[i] = New[dad].GeneSel[i];
        New[dad].GeneSel[i] = temp;
      }

      New[mom].n_e = 1;
      New[dad].n_e = 1;
    }
  }

  /* Operador de Mutacion Uniforme */
  void Mutacion_Uniforme() {
    int posiciones, i, j;
    double m;

    posiciones = n_genes * long_poblacion;

    if (prob_mutacion > 0) {
      while (Mu_next < posiciones) {
        /* we determinate the chromosome and the GeneSel */
        i = Mu_next / n_genes;
        j = Mu_next % n_genes;

        /* we mutate the GeneSel */
        if (New[i].GeneSel[j] == '0') {
          New[i].GeneSel[j] = '1';
        }
        else {
          New[i].GeneSel[j] = '0';
        }

        New[i].n_e = 1;

        /* we calculate the next position */
        if (prob_mutacion < 1) {
          m = Randomize.Rand();
          Mu_next += (int) (Math.log(m) / Math.log(1.0 - prob_mutacion)) + 1;
        }
        else {
          Mu_next += 1;
        }
      }
    }

    Mu_next -= posiciones;
  }

  /** Fitness Function */
  void Evaluate() {
    double performance;
    int i, j;

    for (i = 0; i < long_poblacion; i++) {
      /* if the chromosome aren't evaluated, it's evaluate */
      if (New[i].n_e == 1) {
        New[i].Perf = fun_adap.eval(New[i].GeneSel);
        performance = New[i].Perf;
        New[i].n_e = 0;
        Trials++; /* we increment the number of evaluated chromosomes */

      }
      else {
        performance = New[i].Perf;
      }

      /* we calculate the position of the best individual */
      if (i == 0) {
        Best_current_perf = performance;
        Best_guy = 0;
      }
      else if (performance < Best_current_perf) {
        Best_current_perf = performance;
        Best_guy = i;
      }
    }
  }

  /* Elitist selection */
  void Elitist() {
    int i, k, found;

    /* if the best individual of the old population aren't in the new population, we remplace the last individual for this */
    for (i = 0, found = 0; i < long_poblacion && (found == 0); i++) {
      for (k = 0, found = 1; (k < n_genes) && (found == 1); k++) {
        if (New[i].GeneSel[k] != Old[Best_guy].GeneSel[k]) {
          found = 0;
        }
      }
    }

    if (found == 0) {
      for (k = 0; k < n_genes; k++) {
        New[long_poblacion - 1].GeneSel[k] = Old[Best_guy].GeneSel[k];
      }

      New[long_poblacion - 1].Perf = Old[Best_guy].Perf;
      New[long_poblacion - 1].n_e = 0;
    }
  }

  /** Returns the best solution*/
  public char[] solucion() {
    return (New[Best_guy].GeneSel);
  }

  /** Returns the fitness of the best solution */
  public double solucion_ec() {
    return (New[Best_guy].Perf);
  }
}

