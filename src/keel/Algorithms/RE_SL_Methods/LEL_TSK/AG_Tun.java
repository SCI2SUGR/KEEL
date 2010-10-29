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

import org.core.*;
import java.lang.Math;

class AG_Tun {
  public double prob_cruce, prob_mutacion, a, b;
  public int Mu_next, Trials;
  public double Best_current_perf, porc_pob_ee;
  public int Best_guy;
  public int long_poblacion, n_genes, primer_gen_C2;
  public int n_gen_ee;
  public static double S_sigma_consecuentes = 0.00001;
  public static double c = 0.9; /*0.817*/

  public int[] sample;
  public int[] indices_ordenacion;
  public int last;

  public Structure[] Old;
  public Structure[] New;
  public Structure[] C;
  public Structure[] temp;
  public Structure[] YaExplotados;
  public Structure Hijo;
  public TipoIntervalo[] intervalos;
  private TipoIntervalo intervalo_mut;
  private MiDataset tabla;
  private double PI = 3.1415926;

  public Adap_Tun fun_adap;
  public BaseR_TSK base_reglas;

  public AG_Tun(int n_poblacion, double cruce, double mutacion, double valor_a,
                double valor_b, double porc_pob_ee11, int gen_ee, Adap_Tun funcion,
                BaseR_TSK base, MiDataset tabla) {
    int i;

    this.base_reglas = base;
    this.fun_adap = funcion;
	this.tabla = tabla;
    this.long_poblacion = n_poblacion;
    this.prob_cruce = cruce;
    this.prob_mutacion = mutacion;
    this.a = valor_a;
    this.b = valor_b;
    this.porc_pob_ee = porc_pob_ee11;
    this.n_gen_ee = gen_ee;
    this.n_genes = (3 * this.tabla.n_var_estado + this.tabla.n_variables) * this.base_reglas.n_reglas;

    this.prob_mutacion = this.prob_mutacion / (double) this.n_genes;
	this.last = (int) (this.long_poblacion * this.prob_cruce);

    this.Old = new Structure[this.long_poblacion];
    this.New = new Structure[this.long_poblacion];
    this.YaExplotados = new Structure[this.long_poblacion];

    for (i = 0; i < this.long_poblacion; i++) {
      this.Old[i] = new Structure(this.n_genes);
      this.New[i] = new Structure(this.n_genes);
      this.YaExplotados[i] = new Structure(this.n_genes);
    }

    this.Hijo = new Structure(this.n_genes);
    this.sample = new int[this.long_poblacion];
    this.indices_ordenacion = new int[this.long_poblacion];
    this.intervalo_mut = new TipoIntervalo();

    this.intervalos = new TipoIntervalo[this.n_genes];
    for (i = 0; i < this.n_genes; i++) {
      this.intervalos[i] = new TipoIntervalo();
    }

    this.C = new Structure[4];
    for (i = 0; i < 4; i++) {
      this.C[i] = new Structure(this.n_genes);
    }
  }

  public void Intercambio() {
    temp = Old;
    Old = New;
    New = temp;
  }

  /** Inicialization of the population */
  public void Initialize() {
    int i, j, temp, mitad_Pob;
    double Valor_Inicial_Sigma = 0.001;


    if (prob_mutacion < 1.0) {
      Mu_next = (int) Math.ceil(Math.log(Randomize.Rand()) / Math.log(1.0 - prob_mutacion));
    }
    else {
      Mu_next = 1;
    }

    Trials = 0;

    /* Los conjuntos difusos de los antecedentes de las reglas constituyen la
       primera parte del primer cromosoma de la poblacion inicial.
       Se inicializa C1 en el primer cromosoma. */
    New[0].n_e = 1;
    primer_gen_C2 = 0;

    for (i = 0; i < base_reglas.n_reglas; i++) {
      for (j = 0; j < tabla.n_var_estado; j++) {
        New[0].Gene[primer_gen_C2] = base_reglas.BaseReglas[i].Ant[j].x0;
        New[0].Gene[primer_gen_C2 + 1] = base_reglas.BaseReglas[i].Ant[j].x1;
        New[0].Gene[primer_gen_C2 + 2] = base_reglas.BaseReglas[i].Ant[j].x3;
        primer_gen_C2 += 3;
      }
    }

    /* Se establecen los intervalos en los que varia cada gen de la primera
       parte en la primera generacion */
    for (i = 0; i < primer_gen_C2; i += 3) {
      intervalos[i].min = New[0].Gene[i] - (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
      intervalos[i].max = New[0].Gene[i] + (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;

      intervalos[i + 1].min = New[0].Gene[i + 1] - (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
      intervalos[i + 1].max = New[0].Gene[i + 1] + (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;

      intervalos[i + 2].min = New[0].Gene[i + 2] - (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
      intervalos[i + 2].max = New[0].Gene[i + 2] + (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
    }

    /* Se inicializa la segunda parte del primer cromosoma con los parametros
       de los consecuentes de las reglas de la BC inicial, junto con los inter-
       valos correspondientes */
    for (i = 0; i < base_reglas.n_reglas; i++) {
      for (j = 0; j < tabla.n_variables; j++) {
        temp = primer_gen_C2 + i * (tabla.n_variables) + j;
        New[0].Gene[temp] = Math.atan(base_reglas.BaseReglas[i].Cons[j]);
        intervalos[temp].min = (-1.0 * PI / 2.0) + 1E-10;
        intervalos[temp].max = (PI / 2.0) - 1E-10;
      }
    }

    /* Se genera la segunda mitad de la poblacion inicial generando aleatoriamen-
       te C1 y manteniendo C2 */
    mitad_Pob = (int) Math.ceil(long_poblacion / 2.0);
    for (i = 1; i < mitad_Pob; i++) {
      for (j = 0; j < primer_gen_C2; j++) {
        New[i].Gene[j] = intervalos[j].min + Randomize.Randdouble(intervalos[j].min, intervalos[j].max);
      }

      for (j = primer_gen_C2; j < n_genes; j++) {
        New[i].Gene[j] = New[0].Gene[j];
      }

      New[i].n_e = 1;
    }

    /* Se genera el resto de la poblacion inicial generando aleatoriamente C1
       a partir de los intervalos anteriores y mutando C2 */
    for (i = mitad_Pob; i < long_poblacion; i++) {
      for (j = 0; j < primer_gen_C2; j++) {
        New[i].Gene[j] = intervalos[j].min + Randomize.Randdouble(intervalos[j].min, intervalos[j].max);
      }

      for (j = primer_gen_C2; j < n_genes; j++) {
        /* Comprobamos que no se salgan del intervalo permitido [-PI/2,PI/2] */
        do {
          New[i].Gene[j] = New[0].Gene[j] + ValorNormal(Valor_Inicial_Sigma);
        }
        while (New[i].Gene[j] <= (-1.0 * PI / 2.0) || New[i].Gene[j] >= (PI / 2.0));
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

  /** Max-Min-Aritmetical Crossover */
  public void Max_Min_Crossover() {
    int mom, dad, i, j, temp;
    int[] indice = new int[4];

    for (mom = 0; mom < last; mom += 2) {
      dad = mom + 1;

      for (i = 0; i < n_genes; i++) {
        /* we obtain 4 offsprings: appling the t-norma, the
             t-conorma and 2 the average function */
        C[0].Gene[i] = T_producto_logico(New[mom].Gene[i], New[dad].Gene[i]);
        C[1].Gene[i] = S_suma_logica(New[mom].Gene[i], New[dad].Gene[i]);
        C[2].Gene[i] = Promedio1(New[mom].Gene[i], New[dad].Gene[i], a);
        C[3].Gene[i] = Promedio1(New[mom].Gene[i], New[dad].Gene[i], 1.0 - a);
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
          if (C[indice[j + 1]].Perf < C[indice[j]].Perf) {
            temp = indice[j];
            indice[j] = indice[j + 1];
            indice[j + 1] = temp;
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

      Trials += 2;
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

  /** Mutation Non Uniform */
  public void Mutacion_No_Uniforme(long Gen, long n_generaciones) {
    int posiciones, i, j;
    double nval, m;

    posiciones = n_genes * long_poblacion;

    if (prob_mutacion > 0) {
      while (Mu_next < posiciones) {

        /* we determinate the chromosome and the gene */
        i = Mu_next / n_genes;
        j = Mu_next % n_genes;

        /* Se determinan los intervalos de mutacion de ese gen y se calcula el
             valor mutado */
        if (j >= primer_gen_C2) { /* Consecuente: muta en [-PI/2,PI/2] */
          intervalo_mut.min = intervalos[j].min;
          intervalo_mut.max = intervalos[j].max;
        }
        else {
          switch (j % 3) {
            case 0:
                /* Punto izquierdo: muta en [intervalos[j].min,cromosoma[j+1]] */
              intervalo_mut.min = intervalos[j].min;
              intervalo_mut.max = New[i].Gene[j + 1];
              break;

            case 1:
                /* Punto central: muta en [cromosoma[j-1],cromosoma[j+1]] */
              intervalo_mut.min = New[i].Gene[j - 1];
              intervalo_mut.max = New[i].Gene[j + 1];
              break;

            case 2:
                /* Punto derecho: muta en [cromosoma[j-1],intervalos[j].max] */
              intervalo_mut.min = New[i].Gene[j - 1];
              intervalo_mut.max = intervalos[j].max;
              break;
          }
        }

        /* we mutate the gene */
        if (Randomize.Rand() < 0.5) {
          nval = New[i].Gene[j] +
              delta(Gen, intervalo_mut.max - New[i].Gene[j], n_generaciones);
        }
        else {
          nval = New[i].Gene[j] -
              delta(Gen, New[i].Gene[j] - intervalo_mut.min, n_generaciones);
        }

        New[i].Gene[j] = nval;
        New[i].n_e = 1;

        /* we calculate the next position */
        if (prob_mutacion < 1) {
          m = Randomize.Rand();
          Mu_next += (int) Math.ceil(Math.log(m) / Math.log(1.0 - prob_mutacion));
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

  /** Returns 1 if the best current rule is in the list "L" yet */
  private int Pertenece_AG(Structure C, Structure[] L, int n_explotados) {
    int crom, gen, esta;

    crom = 0;
    while (crom < n_explotados) {
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
  private double ValorNormal(double desv) {
    double u1, u2;

    /* we generate 2 uniform values [0,1] */
    u1 = Randomize.Rand();
    u2 = Randomize.Rand();

    /* we calcules a normal value with the uniform values */
    return (desv * Math.sqrt( -2 * Math.log(u1)) * Math.sin(2 * PI * u2));
  }

  /** Evolution Strategy (1+1) */
  void EE_1_1(Structure Padre, int Muta_C1, int Muta_C2) {
    int j, gen, n_mutaciones, n_exitos, it_sin_exito, fin;
    double x0, x1, x2, newx1, newx, S, m, sigma, new_sigma;

    /* Inicialization of the counters */
    n_mutaciones = n_exitos = it_sin_exito = fin = 0;
    sigma = new_sigma = 1.0;

    do {
      if (Muta_C1 == 1) {
        /* Mutation of C1 */
        for (gen = 0; gen < primer_gen_C2; gen += 3) {
          /* we obtain the fuzzy set */
          x0 = Padre.Gene[gen];
          x1 = Padre.Gene[gen + 1];
          x2 = Padre.Gene[gen + 2];

          /* Adaptation of S and mutation of the center point */
          S = Adap.Minimo(x1 - x0, x2 - x1) / 2.0;
          m = ValorNormal(new_sigma * S);
          newx1 = x1 + m;

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
          S = Adap.Minimo(x0 - intervalos[gen].min, newx1 - x0) / 2.0;
          m = ValorNormal(new_sigma * S);
          newx = x0 + m;

          if (newx <= intervalos[gen].min) {
            Hijo.Gene[gen] = intervalos[gen].min;
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
          S = Adap.Minimo(x2 - newx1, intervalos[gen + 2].max - x2) / 2.0;
          m = ValorNormal(new_sigma * S);
          newx = x2 + m;

          if (newx <= newx1) {
            Hijo.Gene[gen + 2] = newx1;
          }
          else {
            if (newx >= intervalos[gen + 2].max) {
              Hijo.Gene[gen + 2] = intervalos[gen + 2].max;
            }
            else {
              Hijo.Gene[gen + 2] = newx;
            }
          }
        }
      }

      /* we don't mutate the antecedent (C1) */
      else {
        for (gen = 0; gen < primer_gen_C2; gen++) {
          Hijo.Gene[gen] = Padre.Gene[gen];
        }
      }

      if (Muta_C2 == 1) {
        /* Mutation of C2 */
        for (gen = primer_gen_C2; gen < n_genes; gen++) {
          m = ValorNormal(new_sigma * S_sigma_consecuentes);
          newx = Padre.Gene[gen] + m;

          if (newx < intervalos[gen].min) {
            Hijo.Gene[gen] = intervalos[gen].min;
          }
          else {
            if (newx > intervalos[gen].max) {
              Hijo.Gene[gen] = intervalos[gen].max;
            }
            else {
              Hijo.Gene[gen] = newx;
            }
          }
        }
      }
      /* we don't mutate the consequent (C2) */
      else {
        for (gen = primer_gen_C2; gen < n_genes; gen++) {
          Hijo.Gene[gen] = Padre.Gene[gen];
        }
      }

      /* we evaluate the son */
      Hijo.Perf = fun_adap.eval(Hijo.Gene);

      /* we count the mutation */
      n_mutaciones += 1;

      /* if the son is better than the father this relieve his father, we accept sigma and we count another hit */
      if (Hijo.Perf < Padre.Perf) {
        n_exitos += 1;
        it_sin_exito = 0;
        sigma = new_sigma;

        for (j = 0; j < n_genes; j++) {
          Padre.Gene[j] = Hijo.Gene[j];
        }

        Padre.Perf = Hijo.Perf;
      }
      else {
        it_sin_exito++;
      }

      /* we adapt sigma */
      new_sigma = AdaptacionSigma(sigma, n_exitos / (double) n_mutaciones, (double) n_genes - tabla.n_var_estado);

      if (it_sin_exito >= n_gen_ee) {
        fin = 1;
      }

    }
    while (fin == 0);
  }

  /* Main of the Evolution Strategy (1+1) */
  public void Estrategia_Evolucion() {
    int i, j, temp, cromosoma, n_ya_explotados, n_a_explotar;

    /* we evaluate the population */
    for (i = 0; i < long_poblacion; i++) {
      if (New[i].n_e == 1) {
        New[i].Perf = fun_adap.eval(New[i].Gene);
        New[i].n_e = 0;
      }
    }

    /* we order the population by means of the bubble method */
    for (i = 0; i < long_poblacion; i++) {
      indices_ordenacion[i] = i;
    }

    for (i = 0; i < long_poblacion; i++) {
      for (j = 0; j < long_poblacion - i - 1; j++) {
        if (New[indices_ordenacion[j +
            1]].Perf < New[indices_ordenacion[j]].Perf) {
          temp = indices_ordenacion[j];
          indices_ordenacion[j] = indices_ordenacion[j + 1];
          indices_ordenacion[j + 1] = temp;
        }
      }
    }

    /* the evolution strategy is applied to each individual of the population with fitness better than 0 */
    i = 0;
    n_ya_explotados = 0;
    n_a_explotar = (int) (porc_pob_ee * long_poblacion);

    while ( (i < long_poblacion) && (n_ya_explotados < n_a_explotar)) {
      /* we initialize the index of the chromosome */
      cromosoma = indices_ordenacion[i];

      /* we store this chromosome in the list of the exploited */
      for (j = 0; j < n_genes; j++) {
        YaExplotados[n_ya_explotados].Gene[j] = New[cromosoma].Gene[j];
      }

      /* Inicialization of the counters */
      YaExplotados[n_ya_explotados].Perf = New[cromosoma].Perf;
      n_ya_explotados++;

      /* we apply the ES(1+1) */
      EE_1_1(New[cromosoma], 1, 1);

      /* we look for the next unrecurrent individual */
      if (n_ya_explotados < n_a_explotar) {
        do {
          i++;
        }
        while (i < long_poblacion &&
               Pertenece_AG(New[indices_ordenacion[i]], YaExplotados,
                            n_ya_explotados) == 1);
      }
    }
  }

}
