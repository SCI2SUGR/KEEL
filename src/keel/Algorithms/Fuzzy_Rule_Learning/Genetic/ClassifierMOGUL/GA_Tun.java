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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */

import java.io.*;
import org.core.*;
import java.util.*;
import java.lang.Math;

class GA_Tun {
/**
 * <p>
 * This class implements a generational genetic algorithm
 * </p>
 */
 	
    public double prob_cruce, prob_mutacion, a, b;
    public int Mu_next, Trials;
    public double Best_current_perf;
    public int Best_guy;
    public int long_poblacion, n_genes, primer_gen_C2;
    public static double S_sigma_consecuentes = 0.00001;
    public static double c = 0.9; /*0.817*/

    public int[] sample;
    public int[] indices_ordenacion;
    public int last;
    public int[][] RuleBase;

    public Structure[] Old;
    public Structure[] New;
    public Structure[] C;
    public Structure[] temp;
    public Structure[] YaExplotados;
    public Structure Hijo;
    public T_Interval[] intervalos;
    private T_Interval intervalo_mut;

    public Adap_Tun fun_adap;
    public RuleBase_Tun base_reglas;


    /**
     * <p>
     * Constructor
     * </p>
     * @param n_poblacion int The population size
     * @param cruce double Crossover probability
     * @param mutation double Mutation probability
     * @param valor_a double Parameter a
     * @param valor_a double Parameter b
     * @param funcion Adap_Tun Adaptation function
     * @param base RuleBase_Tun The RB to be tunned
     * @param num_clases int The number of classes of the problem
     */
    public GA_Tun(int n_poblacion, double cruce, double mutacion,
                  double valor_a, double valor_b, Adap_Tun funcion,
                  RuleBase_Tun base, int num_clases) {
        int i;

        base_reglas = base;
        fun_adap = funcion;
        long_poblacion = n_poblacion;
        prob_cruce = cruce;
        prob_mutacion = mutacion;
        a = valor_a;
        b = valor_b;
        n_genes = 3 * base_reglas.n_reglas_distintas;

        prob_mutacion = prob_mutacion / (double) n_genes;

        Old = new Structure[long_poblacion];
        New = new Structure[long_poblacion];
        YaExplotados = new Structure[long_poblacion];

        for (i = 0; i < long_poblacion; i++) {
            Old[i] = new Structure(n_genes, num_clases);
            New[i] = new Structure(n_genes, num_clases);
            YaExplotados[i] = new Structure(n_genes, num_clases);
        }

        Hijo = new Structure(n_genes, num_clases);
        sample = new int[long_poblacion];
        indices_ordenacion = new int[long_poblacion];
        intervalo_mut = new T_Interval();

        intervalos = new T_Interval[n_genes];
        for (i = 0; i < n_genes; i++) {
            intervalos[i] = new T_Interval();
        }

        C = new Structure[4];
        for (i = 0; i < 4; i++) {
            C[i] = new Structure(n_genes, num_clases);
        }

        RuleBase = new int[base_reglas.n_reglas][];
        for (i = 0; i < base_reglas.n_reglas; i++) {
            RuleBase[i] = new int[base_reglas.tabla.n_inputs];
        }
    }


    private int ceil(double v) {
        int valor;

        valor = (int) Math.round(v);
        if ((double) valor < v) {
            valor++;
        }

        return (valor);
    }

    /**
     * <p>
     * It swaps the old and the new population
     * </p>
     */
    public void Swap() {
        temp = Old;
        Old = New;
        New = temp;
    }

    /**
     * <p>
     * Inicialization of the population
     * </p>
     */    
    public void Initialize() {
        int i, j, k, repetida, et_act;
        int[] n_etiquetas = new int[base_reglas.tabla.n_inputs];

        if (prob_mutacion < 1.0) {
            Mu_next = ceil(Math.log(Randomize.Rand()) /
                           Math.log(1.0 - prob_mutacion));
        } else {
            Mu_next = 1;
        }

        Trials = 0;

        New[0].n_e = 1;
        primer_gen_C2 = 0;

        for (j = 0; j < base_reglas.tabla.n_inputs; j++)
          n_etiquetas[j] = 0;

      for (j = 0; j < base_reglas.tabla.n_inputs; j++) {
        for (i = 0; i < base_reglas.n_reglas; i++) {
            et_act = 0;
            for (k = 0; k < j; k++)
              et_act += n_etiquetas[k];
            k = 3 * et_act;
            repetida=0;
            while ((k < primer_gen_C2) && (repetida == 0)) {
              if (base_reglas.BaseReglas[i].Ant[j].x0 == New[0].Gene[k] &&
                  base_reglas.BaseReglas[i].Ant[j].x1 == New[0].Gene[k+1] &&
                  base_reglas.BaseReglas[i].Ant[j].x3 == New[0].Gene[k+2])
                repetida = 1;
              else {
                k+=3;
                et_act++;
              }
            }
            RuleBase[i][j] = et_act;
            if (repetida == 0) {
              n_etiquetas[j]++;
              New[0].Gene[primer_gen_C2] = base_reglas.BaseReglas[i].Ant[j].x0;
              New[0].Gene[primer_gen_C2+1] = base_reglas.BaseReglas[i].Ant[j].x1;
              New[0].Gene[primer_gen_C2+2] = base_reglas.BaseReglas[i].Ant[j].x3;
              primer_gen_C2 += 3;
            }
          }
        }

        /* We calculate the intervals in with each gene can vary */
        for (i = 0; i < primer_gen_C2; i += 3) {
            intervalos[i].min = New[0].Gene[i] -
                                (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
            intervalos[i].max = New[0].Gene[i] +
                                (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;

            intervalos[i +
                    1].min = New[0].Gene[i + 1] -
                             (New[0].Gene[i + 1] - New[0].Gene[i]) / 2.0;
            intervalos[i +
                    1].max = New[0].Gene[i + 1] +
                             (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;

            intervalos[i +
                    2].min = New[0].Gene[i + 2] -
                             (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
            intervalos[i +
                    2].max = New[0].Gene[i + 2] +
                             (New[0].Gene[i + 2] - New[0].Gene[i + 1]) / 2.0;
        }

        /* The rest of the population is randomly generated by using the intervals
           previously calculated */
        for (i = 1; i < long_poblacion; i++) {
            for (j = 0; j < primer_gen_C2; j++) {
                New[i].Gene[j] = Randomize.Randdouble(intervalos[j].min,
                        intervalos[j].max);
            }
            New[i].n_e = 1;
        }
    }


    /**
     * <p>
     * Selection based on the Baker's Estocastic Universal Sampling
     * </p>
     */       
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
                if ((Old[j].n_e == 0) && (best == -1 || Old[j].Perf < perf)) {
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
        } else {
            return (y);
        }
    }


    private double S_suma_logica(double x, double y) {
        if (x > y) {
            return (x);
        } else {
            return (y);
        }
    }


    private double Promedio1(double x, double y, double p) {
        return (p * x + (1 - p) * y);
    }


    /**
     * <p>
     * Crossover Operator
     * </p>
     */  
    public void Max_Min_Crossover() {
        int mom, dad, i, j, temp;
        int[] indice = new int[4];

        last = (int) (long_poblacion * prob_cruce);

        for (mom = 0; mom < last; mom += 2) {
            dad = mom + 1;

            for (i = 0; i < n_genes; i++) {
                /* we obtain 4 offsprings: appling the t-norma, the
                 t-conorma and 2 the average function */
                C[0].Gene[i] = T_producto_logico(New[mom].Gene[i],
                                                 New[dad].Gene[i]);
                C[1].Gene[i] = S_suma_logica(New[mom].Gene[i], New[dad].Gene[i]);
                C[2].Gene[i] = Promedio1(New[mom].Gene[i], New[dad].Gene[i], a);
                C[3].Gene[i] = Promedio1(New[mom].Gene[i], New[dad].Gene[i],
                                         1.0 - a);
            }

            /* Evaluation of the 4 offsprings */
            C[0].Perf = fun_adap.eval(C[0].Gene, RuleBase);
            C[1].Perf = fun_adap.eval(C[1].Gene, RuleBase);
            C[2].Perf = fun_adap.eval(C[2].Gene, RuleBase);
            C[3].Perf = fun_adap.eval(C[3].Gene, RuleBase);

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


    /**
     * <p>
     * Non Uniform Mutation Operator
     * </p>
     * @param Gen long Current generation of the GA
     * @param n_generaciones long Maximum number of generations of the GA
     */        
    public void Non_Uniform_Mutation(long Gen, long n_generaciones) {
        int posiciones, i, j;
        double nval, m;

        posiciones = n_genes * long_poblacion;

        if (prob_mutacion > 0) {
            while (Mu_next < posiciones) {

                /* we determinate the chromosome and the gene */
                i = Mu_next / n_genes;
                j = Mu_next % n_genes;

                switch (j % 3) {
                case 0:
                        /* Left point: mutates in [intervalos[j].min,cromosoma[j+1]] */
                    intervalo_mut.min = intervalos[j].min;
                    intervalo_mut.max = New[i].Gene[j + 1];
                    break;

                case 1:
                        /* Central point: mutates in [cromosoma[j-1],cromosoma[j+1]] */
                    intervalo_mut.min = New[i].Gene[j - 1];
                    intervalo_mut.max = New[i].Gene[j + 1];
                    break;

                case 2:
                        /* Right point: mutates in [cromosoma[j-1],intervalos[j].max] */
                    intervalo_mut.min = New[i].Gene[j - 1];
                    intervalo_mut.max = intervalos[j].max;
                    break;
                }

                /* we mutate the gene */
                if (Randomize.Rand() < 0.5) {
                    nval = New[i].Gene[j] +
                           delta(Gen, intervalo_mut.max - New[i].Gene[j],
                                 n_generaciones);
                } else {
                    nval = New[i].Gene[j] -
                           delta(Gen, New[i].Gene[j] - intervalo_mut.min,
                                 n_generaciones);
                }

                New[i].Gene[j] = nval;
                New[i].n_e = 1;

                /* we calculate the next position */
                if (prob_mutacion < 1) {
                    m = Randomize.Rand();
                    Mu_next += ceil(Math.log(m) / Math.log(1.0 - prob_mutacion));
                } else {
                    Mu_next += 1;
                }
            }

            Mu_next -= posiciones;
        }
    }


    /**
     * <p>
     * Fitness Function
     * </p>
     */   
    void Evaluate() {
        double performance;
        int i, j;

        for (i = 0; i < long_poblacion; i++) {
            /* if the chromosome aren't evaluated, it's evaluate */
            if (New[i].n_e == 1) {
                New[i].Perf = fun_adap.eval(New[i].Gene, RuleBase);
                performance = New[i].Perf;
                New[i].n_e = 0;
                Trials++;
                        /* we increment the number of evaluated chromosomes */

            } else {
                performance = New[i].Perf;
            }

            /* we calculate the position of the best individual */
            if (i == 0) {
                Best_current_perf = performance;
                Best_guy = 0;
            } else if (performance < Best_current_perf) {
                Best_current_perf = performance;
                Best_guy = i;
            }
        }
    }


    /**
     * <p>
     * Elitist selection
     * </p>
     */  
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

    /**
     * <p>
     * Returns the best solution
     * </p>
     * @return double[] The best solution
     */   
    public double[] solucion() {
        return (New[Best_guy].Gene);
    }

    /**
     * <p>
     * Returns the fitness of the best solution
     * </p>
     * @return double The fitness of the best solution
     */     
    public double solucion_cla() {
        return (New[Best_guy].Perf);
    }


    /** Returns 1 if the best current rule is in the list "L" yet */
    private int Pertenece_GA(Structure C, Structure[] L, int n_explotados) {
        int crom, gen, esta;

        crom = 0;
        while (crom < n_explotados) {
            esta = 1;
            gen = 0;

            while (gen < n_genes && esta == 1) {
                if (C.Gene[gen] != L[crom].Gene[gen]) {
                    esta = 0;
                } else {
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
        return (desv * Math.sqrt( -2 * Math.log(u1)) *
                Math.sin(2 * Math.PI * u2));
    }


}

