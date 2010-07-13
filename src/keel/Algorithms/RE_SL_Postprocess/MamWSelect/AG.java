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

package keel.Algorithms.RE_SL_Postprocess.MamWSelect;
import java.lang.Math;
import org.core.*;

class AG  {
	public double prob_cruce, prob_mutacion;
	public double MMA_a = 0.35;
	public int Mu_next, Trials;
	public double Best_current_perf;
	public int Best_guy;
	public int long_poblacion, n_genes;

	public int [] sample;
	public int last;

	public Structure [] Old;
	public Structure [] New;
	public Structure [] temp;
	public Adap fun_adap;
	public char [][] regla_son;
	public double [][] peso_son;
	public Structure [] son;


	public AG (int n_poblacion, int genes, double cruce, double mutacion, Adap funcion) {
		int i;

		long_poblacion = n_poblacion;
		n_genes = genes;
		prob_cruce = cruce;
		prob_mutacion = mutacion / (double) n_genes;
		fun_adap = funcion;

		sample = new int[long_poblacion];

		Old = new Structure[long_poblacion];
		New = new Structure[long_poblacion];

		for (i=0; i<long_poblacion; i++) {
			Old[i] = new Structure(n_genes);
			New[i] = new Structure(n_genes);
		}

		regla_son = new char[2][n_genes];
		peso_son = new double[4][n_genes];

		son = new Structure[8];
		for (i=0; i<8; i++)  son[i] = new Structure(n_genes);
	}

	private int ceil (double v) {
		int valor;

		valor = (int) Math.round(v);
		if ((double)valor < v) valor++;

		return (valor);
	}

	public void Intercambio () {
		temp=Old;
		Old=New;
		New=temp;
	}

	/** Inicialization of the population */
	public void Initialize (BaseR base_total) {
		int i, j;

		last = (int) ((prob_cruce * long_poblacion) - 0.5);

		Trials = 0;

		if (prob_mutacion < 1.0)
			Mu_next = ceil (Math.log(Randomize.Rand()) / Math.log(1.0 - prob_mutacion));
		else  Mu_next = 1;

		for (j=0; j<n_genes; j++) {
			New[0].Gene[j] = '1';

			if (base_total.cpeso == 1)  New[0].GeneR[j] = base_total.BaseP[j];
			else  New[0].GeneR[j] = 1.0;
		}
		New[0].n_e = 1;

		for (i=1; i < long_poblacion; i++) {
			for (j=0; j<n_genes; j++) {
				if (Randomize.RandintClosed(0,1) == 0)  New[i].Gene[j] = '0';
				else  New[i].Gene[j] = '1';

				New[i].GeneR[j] = Randomize.Randdouble(0.0, 1.0);
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
		for (i=0; i<long_poblacion; i++)  Old[i].n_e = 0;

		/* we look for the best ordered non element */
		for (i=0; i<long_poblacion-1; i++) {
			best = -1;
			perf = 0.0;
			for (j=0; j<long_poblacion; j++) {
				if ((Old[j].n_e==0) && (best==-1 || Old[j].Perf < perf)) {
					perf = Old[j].Perf;
					best = j;
				}
			}

			/* we assign the ranking */
			Old[best].n_e = long_poblacion - 1 - i;
		}

		/* we normalize the ranking */
		rank_max = 2.0 - rank_min;
		factor = (rank_max-rank_min)/(double)(long_poblacion-1);

		/* we assign the number of replicas of each chormosome according to the select probability */
		k = 0;
		ptr = Randomize.Rand ();
		for (sum = i = 0; i<long_poblacion; i++) {
			expected = rank_min + Old[i].n_e * factor;
			for (sum += expected; sum>=ptr; ptr++)  sample[k++] = i;
		}

		/* we complete the population if necessary */
		if (k != long_poblacion) {
			for (;k<long_poblacion; k++)  sample[k] = Randomize.RandintClosed (0, long_poblacion-1);
		}

		/* we shuffle the selected chromosomes */
		for (i=0; i<long_poblacion; i++) {
			j = Randomize.RandintClosed (i, long_poblacion-1);
			temp = sample[j];
			sample[j] = sample[i];
			sample[i] = temp;
		}

		/* we create the new population */
		for (i=0; i<long_poblacion; i++) {
			k = sample[i];
			for (j=0; j<n_genes; j++) {
				New[i].Gene[j] = Old[k].Gene[j];
				New[i].GeneR[j] = Old[k].GeneR[j];
			}

			New[i].Perf = Old[k].Perf;
			New[i].n_e = 0;
		}
	}


	/* Multipoint Crossover in two points */
	void Cruce_Multipunto () {
		int mom, dad, xpoint1, xpoint2, i, j;
		char temp;

		for (mom=0; mom<last; mom+=2) {
			dad = mom+1;

			/* we select 2 points */
			xpoint1 = Randomize.RandintClosed (0,n_genes-1);

			if (xpoint1 != n_genes-1)
				xpoint2 = Randomize.RandintClosed (xpoint1 + 1, n_genes - 1);

			else  xpoint2 = n_genes - 1;

			/* we cross the individuals between xpoint1 and xpoint2 */
			for (i=xpoint1; i<=xpoint2; i++) {
				temp = New[mom].Gene[i];
				New[mom].Gene[i] = New[dad].Gene[i];
				New[dad].Gene[i] = temp;
			}

			New[mom].n_e = 1;
			New[dad].n_e = 1;
		}
	}

	/* Crossover operator in 2 point */
	public void Cruce_2P (char [] dad1, char [] dad2) {
		int i, j, xpoint1, xpoint2, tmp_int;
		
		/* we select 2 points */
		xpoint1 = Randomize.RandintClosed(0,n_genes-1);
		
		do 
			xpoint2 = Randomize.RandintClosed(0,n_genes-1);
		while (xpoint2 == xpoint1);
		
		if (xpoint2 < xpoint1) {
			tmp_int = xpoint2;
			xpoint2 = xpoint1;
			xpoint1 = tmp_int;
		}
		
		/* we cross the parts */
		for (i=0; i<xpoint1; i++) { 
			regla_son[0][i] = dad1[i];
			regla_son[1][i] = dad2[i];
		}

		for (i=xpoint1; i<=xpoint2; i++) {
			regla_son[0][i] = dad2[i];
			regla_son[1][i] = dad1[i];
		}

		for (i=xpoint2+1; i<n_genes; i++) { 
			regla_son[0][i] = dad1[i];
			regla_son[1][i] = dad2[i];
		}
	}


	private double T_producto_logico (double x ,double y) {
		if (x<y)  return (x);
		else  return (y);
	}


	private double S_suma_logica (double x ,double y) {
		if (x>y)  return (x);
		else  return (y);
	}


	private double Promedio1 (double x, double y, double p) {
		return (p*x + (1-p)*y);
	}


	/* Max-Min-Aritmetical Crossover */
	void Cruce_MMA (double [] dad1, double [] dad2) {
		int i, j;

		/* we obtain 4 offsprings: appling the t-norma, the
		t-conorma and 2 the average function */
		for (i=0; i<n_genes; i++) {
			peso_son[0][i] = T_producto_logico (dad1[i], dad2[i]);
			peso_son[1][i] = S_suma_logica (dad1[i], dad2[i]);
			peso_son[2][i] = Promedio1 (dad1[i], dad2[i], MMA_a);
			peso_son[3][i] = Promedio1 (dad1[i], dad2[i], 1.0 - MMA_a);
		}
	}


	/* Crossover Operator */
	void Cruce () {
		int dad1, dad2, l, i, j, k, n, tmp;
		int [] indice = new int[8];

		for (i=0; i<last; i+=2) {
			dad1 = i;
			dad2 = i+1;

			Cruce_2P (New[dad1].Gene, New[dad2].Gene);
			Cruce_MMA (New[dad1].GeneR, New[dad2].GeneR);

			/* 8 offsprings (2x4) */
			for (l=k=0; l<2; l++)
				for (j=0; j<4; j++,k++) {
					for (n=0; n<n_genes; n++) {
						son[k].Gene[n] = regla_son[l][n];
						son[k].GeneR[n] = peso_son[j][n];
					}
				}

			/* evaluation of the offsprings */
			for (l=0; l<8; l++) {
				son[l].Perf = fun_adap.eval(son[l].Gene, son[l].GeneR);
				indice[l] = l;
			}
	
			/* Selection of the 2 best*/
			for (l=0; l<8; l++)
				for (j=0; j<7-l; j++)
					if (son[indice[j+1]].Perf < son[indice[j]].Perf) {
						tmp = indice[j];
						indice[j] = indice[j+1];
						indice[j+1] = tmp;
					}

			for (n=0; n<n_genes; n++) {
				New[dad1].Gene[n] = son[indice[0]].Gene[n];
				New[dad2].Gene[n] = son[indice[1]].Gene[n];
				New[dad1].GeneR[n] = son[indice[0]].GeneR[n];
				New[dad2].GeneR[n] = son[indice[1]].GeneR[n];
			}

			New[dad1].Perf = son[indice[0]].Perf;
			New[dad1].n_e = 0;
			New[dad2].Perf = son[indice[1]].Perf;
			New[dad2].n_e = 0;
		}
	}


	/* Uniform Mutation */
	void Mutacion_Uniforme () {
		int posiciones, i, j;
		double m;

		posiciones = n_genes * long_poblacion;

		if (prob_mutacion>0) {
			while (Mu_next<posiciones) {
				/* we determinate the chromosome and the gene */
				i = Mu_next / n_genes;
				j = Mu_next % n_genes;

				/* we mutate the gene */
				if (New[i].Gene[j]=='0')  New[i].Gene[j]='1';
				else  New[i].Gene[j]='0';

				/* we mutate the rule's weight */
				New[i].GeneR[j]= Randomize.Randdouble(0.0,1.0);

				
				New[i].n_e=1;

				/* we calculate the next position */
				if (prob_mutacion<1) {
					m = Randomize.Rand();
					Mu_next += ceil (Math.log(m) / Math.log(1.0 - prob_mutacion));
				}
				else  Mu_next += 1;
			}
		}

		Mu_next -= posiciones;
	}

	/** Fitness Function */
	void Evaluate() {
		double performance;
		int i, j;

		for (i=0; i<long_poblacion; i++) {
			/* if the chromosome aren't evaluated, it's evaluate */
			if (New[i].n_e == 1) {
				New[i].Perf = fun_adap.eval (New[i].Gene, New[i].GeneR);
				performance = New[i].Perf;
				New[i].n_e = 0;
				Trials++;       /* we increment the number of evaluated chromosomes */

			}
			else  performance = New[i].Perf;

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
	void Elitist () {
		 int i, k, found;

		 /* if the best individual of the old population aren't in the new population, we remplace the last individual for this */
		 for (i=0, found=0; i<long_poblacion && (found==0); i++)
			for (k=0, found=1; (k<n_genes) && (found==1); k++)
				if ((New[i].Gene[k] != Old[Best_guy].Gene[k]) || (New[i].GeneR[k] != Old[Best_guy].GeneR[k]))  
					found = 0;

		 if (found == 0) {
			for (k=0; k<n_genes; k++) {
				New[long_poblacion-1].Gene[k] = Old[Best_guy].Gene[k];
				New[long_poblacion-1].GeneR[k] = Old[Best_guy].GeneR[k];
			}

			New[long_poblacion-1].Perf = Old[Best_guy].Perf;
			New[long_poblacion-1].n_e = 0;
		}
	}

	/** Returns the best solution*/
	public char [] solucion () {
		return (New[Best_guy].Gene);
	}

	/** Returns the weights of the best solution */
	public double [] solucionR () {
		return (New[Best_guy].GeneR);
	}

	/** Returns the fitness of the best solution */
	public double solucion_ec () {
		return (New[Best_guy].Perf);
	}
}

