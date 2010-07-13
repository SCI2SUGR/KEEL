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

class AG {
  public double prob_cruce, prob_mutacion, a, b;
  public int Mu_next, Trials;
  public double Best_current_perf;
  public int Best_guy;
  public int long_poblacion, n_variables, n_genes;
  public int cruces_simples, cruces_mma;

  public int[] sample;
  public int last;

  public Structure[] Old;
  public Structure[] New;
  public Structure[] C;
  public Structure[] temp;
  public Adap fun_adap;

  public AG(int n_poblacion, int n_var, double cruce, double mutacion,
            double valor_a, double valor_b, Adap funcion) {
    int i;

    fun_adap = funcion;
    n_variables = n_var;
    long_poblacion = n_poblacion;
    prob_cruce = cruce;
    a = valor_a;
    b = valor_b;

    n_genes = 4 * n_variables;
    prob_mutacion = mutacion / (double) n_genes;

    sample = new int[long_poblacion];

    Old = new Structure[long_poblacion];
    New = new Structure[long_poblacion];

    for (i = 0; i < long_poblacion; i++) {
      Old[i] = new Structure(n_genes);
      New[i] = new Structure(n_genes);
    }

    C = new Structure[4];
    for (i = 0; i < 4; i++) {
      C[i] = new Structure(n_genes);
    }
  }

  private int ceil(double v) {
    int valor;

    valor = (int) Math.round(v);
    if ( (double) valor < v) {
      valor++;
    }

    return (valor);
  }

  /** Inicialization of the population */
  public void Initialize(BaseR base_reglas, BaseD base_datos, MiDataset tabla) {
    int i, j, k, tercio_pob, final_grupo1_2, etiqueta, pos_etiq;
    double grado_pertenencia, max_pert;
    int[] seleccionados = new int[ceil(long_poblacion / 3.0)];
    int[] indices_nc = new int[tabla.long_tabla];

    last = (int) ( (prob_cruce * long_poblacion) - 0.5);

    Trials = 0;

    if (prob_mutacion < 1.0) {
      Mu_next = ceil(Math.log(Randomize.Rand()) / Math.log(1.0 - prob_mutacion));
    }

    else {
      Mu_next = 1;
    }

    for (i = j = 0; i < tabla.no_cubiertos && j < tabla.long_tabla; j++) {
      if (tabla.datos[j].cubierto == 0) {
        indices_nc[i] = j;
        i++;
      }
    }

    /* Random selection of the examples. If there are under the necessary all examples are selected */
    tercio_pob = ceil(long_poblacion / 3.0);

    /* if there aren't suficient, all examples are used */
    if (tabla.no_cubiertos <= tercio_pob) {
      final_grupo1_2 = tabla.no_cubiertos;
      for (i = 0; i < tabla.no_cubiertos; i++) {
        seleccionados[i] = indices_nc[i];
      }
    }

    /* if there are suficient, the examples are randomly selected */
    else {
      final_grupo1_2 = tercio_pob;
      for (i = 0; i < tercio_pob; i++) {
        seleccionados[i] = indices_nc[Randomize.RandintClosed(0,
            tabla.no_cubiertos - 1)];
      }
    }

    /* Generation of the chromosomes */
    for (i = 0; i < final_grupo1_2; i++) {
      for (j = 0; j < n_variables; j++) {
        /* Determination of the better label for each variable */
        max_pert = 0.0;
        etiqueta = 0;
        pos_etiq = tabla.n_variables + 3 * j;
        for (k = 0; k < base_datos.n_etiquetas[j]; k++) {
          grado_pertenencia = base_reglas.Fuzzifica(tabla.datos[seleccionados[i]].
              ejemplo[j], base_datos.BaseDatos[j][k]);
          if (grado_pertenencia > max_pert) {
            max_pert = grado_pertenencia;
            etiqueta = k;
          }
        }

        /* Group 1 */
        New[i].Gene[j] = (double) etiqueta;
        New[i].Gene[pos_etiq] = base_datos.BaseDatos[j][etiqueta].x0;
        New[i].Gene[pos_etiq + 1] = base_datos.BaseDatos[j][etiqueta].x1;
        New[i].Gene[pos_etiq + 2] = base_datos.BaseDatos[j][etiqueta].x3;

        /* Group 2 */
        New[final_grupo1_2 + i].Gene[j] = (double) etiqueta;
        for (k = 0; k < 3; k++) {
          New[final_grupo1_2 + i].Gene[pos_etiq +
              k] = Randomize.Randdouble(base_datos.intervalos[j][etiqueta][k].
                                        min,
                                        base_datos.intervalos[j][etiqueta][k].
                                        max);
        }
      }

      New[i].n_e = 1;
      New[final_grupo1_2 + i].n_e = 1;
    }

    /* Group 3 */
    for (i = 2 * final_grupo1_2; i < long_poblacion; i++) {
      for (j = 0; j < tabla.n_variables; j++) {
        etiqueta = Randomize.RandintClosed(0, base_datos.n_etiquetas[j] - 1);
        pos_etiq = tabla.n_variables + 3 * j;
        New[i].Gene[j] = etiqueta;
        for (k = 0; k < 3; k++) {
          New[i].Gene[pos_etiq +
              k] = Randomize.Randdouble(base_datos.intervalos[j][etiqueta][k].
                                        min,
                                        base_datos.intervalos[j][etiqueta][k].
                                        max);
        }
      }

      New[i].n_e = 1;
    }
  }

  public void Intercambio() {
    temp = Old;
    Old = New;
    New = temp;
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
        if ( (Old[j].n_e == 0) && (best == -1 || Old[j].Perf > perf)) {
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
        New[i].Gene[j] = Old[k].Gene[j];
      }

      New[i].Perf = Old[k].Perf;
      New[i].n_e = 0;
    }
  }

  private double T_producto_logico(double x, double y) {
    if (x < y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  private double S_suma_logica(double x, double y) {
    if (x > y) {
      return (x);
    }
    else {
      return (y);
    }
  }

  private double Promedio1(double x, double y, double p) {
    return (p * x + (1 - p) * y);
  }

  /** Max-Min-Aritmetical */
  void Cruce_MMA_Simple(int n_variables) {
    int mom, dad, misma_regla, variable, etiqueta, xpoint1, i, j, intercambio;
    int[] indice = new int[4];
    double temp1, temp2, temp;

    cruces_simples = cruces_mma = 0;
    for (mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      /* we prove if dad and mom are the same rule */
      misma_regla = 1;
      i = 0;

      while (i < n_variables && misma_regla == 1) {
        if (New[dad].Gene[i] != New[mom].Gene[i]) {
          misma_regla = 0;
        }
        else {
          i++;
        }
      }

      /* Max-Min-Aritmetical */
      if (misma_regla == 1) {
        /* we increment the number of the Max-Min-Aritmetical crossover */
        cruces_mma++;

        /* we calculate the variable and the label */
        variable = -1;
        for (i = n_variables; i < n_genes; i++) {
          temp1 = New[mom].Gene[i];
          temp2 = New[dad].Gene[i];

          /* we obtain 4 offsprings: appling the t-norma, the
               t-conorma and 2 the average function */
          C[0].Gene[i] = T_producto_logico(temp1, temp2);
          C[1].Gene[i] = S_suma_logica(temp1, temp2);
          C[2].Gene[i] = Promedio1(temp1, temp2, a);
          C[3].Gene[i] = Promedio1(temp1, temp2, 1.0 - a);
        }

        /* Evaluation of the 4 offsprings */
        C[0].Perf = fun_adap.eval(C[0].Gene);
        C[1].Perf = fun_adap.eval(C[1].Gene);
        C[2].Perf = fun_adap.eval(C[2].Gene);
        C[3].Perf = fun_adap.eval(C[3].Gene);

        /* we order the offsprings by means of the bubble method */
        for (i = 0; i < 4; i++) {
          indice[i] = i;
        }

        for (i = 0; i < 4; i++) {
          for (j = 0; j < 3 - i; j++) {
            if (C[indice[j + 1]].Perf > C[indice[j]].Perf) {
              intercambio = indice[j];
              indice[j] = indice[j + 1];
              indice[j + 1] = intercambio;
            }
          }
        }

        for (i = 0; i < n_genes; i++) {
          New[mom].Gene[i] = C[indice[0]].Gene[i];
          New[dad].Gene[i] = C[indice[1]].Gene[i];
        }

        /* we update the fitness of the offsprings */
        New[mom].Perf = C[indice[0]].Perf;
        New[dad].Perf = C[indice[1]].Perf;
        New[mom].n_e = 0;
        New[dad].n_e = 0;

        Trials += 4;
      }

      /* Simple Crossover */
      else {

        /* we increment the number of simple crossover */
        cruces_simples++;

        /* we calculate the crossing point in the first part of the chromosome */
        xpoint1 = Randomize.RandintClosed(1, n_variables - 1);

        /* we cross both part out of a this point */
        for (i = xpoint1; i < n_variables; i++) {
          temp = New[mom].Gene[i];
          New[mom].Gene[i] = New[dad].Gene[i];
          New[dad].Gene[i] = temp;

          for (j = 0; j < 3; j++) {
            temp = New[mom].Gene[n_variables + 3 * i + j];
            New[mom].Gene[n_variables + 3 * i + j] = New[dad].Gene[n_variables +
                3 * i + j];
            New[dad].Gene[n_variables + 3 * i + j] = temp;
          }
        }

        New[mom].n_e = 1;
        New[dad].n_e = 1;
      }
    }
  }

  private double delta(long t, double y, long n_generaciones) {
    double r, potencia, subtotal, sub;

    r = Randomize.Rand();
    sub = 1.0 - (double) t / (double) n_generaciones;
    potencia = Math.pow(sub, (double) b);
    subtotal = Math.pow(r, potencia);
    return (y * (1.0 - subtotal));
  }

  /** Uniform Non Mutation */
  public void Mutacion_Thrift_No_Uniforme(long Gen, long n_generaciones,
                                          BaseD base_datos) {
    int posiciones, i, j, variable, etiqueta, punto;
    double nval, m;

    posiciones = n_genes * long_poblacion;

    if (prob_mutacion > 0) {
      while (Mu_next < posiciones) {

        /* we determinate the chromosome and the gene */
        i = Mu_next / n_genes;
        j = Mu_next % n_genes;

        m = Randomize.Rand();

        /* the gene is of C1 */
        if (j < base_datos.n_variables) {
          /* if I'm in the fisrt label */
          if ( (m < 0.5 &&
                (New[i].Gene[j] != (double) base_datos.n_etiquetas[j] - 1)) ||
              New[i].Gene[j] == 0.0) {
            nval = New[i].Gene[j] + 1.0;
          }

          else {
            nval = New[i].Gene[j] - 1.0;
          }

          /* we update the membership function */
          variable = j;
          etiqueta = (int) nval;
          New[i].Gene[base_datos.n_variables +
              3 * variable] = base_datos.BaseDatos[variable][etiqueta].x0;
          New[i].Gene[base_datos.n_variables + 3 * variable +
              1] = base_datos.BaseDatos[variable][etiqueta].x1;
          New[i].Gene[base_datos.n_variables + 3 * variable +
              2] = base_datos.BaseDatos[variable][etiqueta].x3;
        }
        /* else the gene is of C2 */
        else {
          /* we calculate the variable and label of the gene */
          variable = (int) (j - base_datos.n_variables) / 3;
          etiqueta = (int) New[i].Gene[variable];
          punto = (j - base_datos.n_variables) % 3;

          /* we mutate the gene */
          if (m < 0.5) {
            nval = New[i].Gene[j] +
                delta(Gen,
                      base_datos.intervalos[variable][etiqueta][punto].max -
                      New[i].Gene[j], n_generaciones);
          }
          else {
            nval = New[i].Gene[j] -
                delta(Gen,
                      New[i].Gene[j] -
                      base_datos.intervalos[variable][etiqueta][punto].min,
                      n_generaciones);
          }
        }

        New[i].Gene[j] = nval;
        New[i].n_e = 1;

        /* we calculate the next position */
        if (prob_mutacion < 1) {
          m = Randomize.Rand();
          Mu_next += (int) (Math.log(m) / Math.log(1.0 - prob_mutacion));
          Mu_next++;
        }
        else {
          Mu_next += 1;
        }
      }

      Mu_next -= posiciones;
    }
  }

  /** Fitness Function */
  void Evaluate() {
    double performance;
    int i, j;

    for (i = 0; i < long_poblacion; i++) {
      /* if the chromosome aren't evaluated, it's evaluate */
      if (New[i].n_e == 1) {
        New[i].Perf = fun_adap.eval(New[i].Gene);
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
      else if (performance > Best_current_perf) {
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
        if (New[i].Gene[k] != Old[Best_guy].Gene[k]) {
          found = 0;
        }
      }
    }

    if (found == 0) {
      for (k = 0; k < n_genes; k++) {
        New[long_poblacion - 1].Gene[k] = Old[Best_guy].Gene[k];
      }

      New[long_poblacion - 1].Perf = Old[Best_guy].Perf;
      New[long_poblacion - 1].n_e = 0;
    }
  }

  /** Returns the best solution*/
  public double[] solucion() {
    return (New[Best_guy].Gene);
  }

  /** Returns the fitness of the best solution */
  public double solucion_ec() {
    return (New[Best_guy].Perf);
  }

  /** Solution to String */
  public String SoluciontoString() {
    int i, pos_etiq;
    String cadena;

    cadena = "";
    for (i = 0; i < n_variables; i++) {
      pos_etiq = n_variables + 3 * i;
      cadena += "    " + New[Best_guy].Gene[pos_etiq] + " " +
          New[Best_guy].Gene[pos_etiq + 1] + " " + New[Best_guy].Gene[pos_etiq +
          2] + "\n";
    }

    return (cadena);
  }

}

