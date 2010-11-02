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
import org.core.*;


class Est_evol {

    public double sigma;
    public int n_gen_ee;
    public long n_mutaciones, n_exitos;

    public static double c = 0.9;

    public BaseD base_datos;
    public BaseR base_reglas;
    public Adap fun_adap;
	private double PI = 3.1415926;

    public Est_evol(BaseD base_da, BaseR base_re, Adap fun, int n_gen_ee) {
        this.base_datos = base_da;
        this.base_reglas = base_re;
        this.fun_adap = fun;
		this.n_gen_ee = n_gen_ee;
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
        return (desv * Math.sqrt( -2.0 * Math.log(u1)) * Math.sin(2.0 * PI * u2));
    }


    /** Evolution Strategy */
    public void Estrategia_Evolucion(Structure Padre, Structure Hijo) {
        int j, variable, etiqueta, gen, it_sin_exito, fin;
        double x0, x1, x2, newx1, newx, S, n, new_sigma;

        /* Inicialization of the counters */
        n_mutaciones = n_exitos = it_sin_exito = fin = 0;
        sigma = new_sigma = 1.0;


        do {
            for (variable = 0; variable < base_datos.tabla.n_variables; variable++) {
                /* we copy the C1 part of the father in the son */
                Hijo.Gene[variable] = Padre.Gene[variable];
                etiqueta = (int) Padre.Gene[variable];
				
                gen = base_datos.tabla.n_variables + 3 * variable;

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
                } else {
                    if (newx1 >= x2) {
                        Hijo.Gene[gen + 1] = x2;
                        newx1 = x2;
                    } else {
                        Hijo.Gene[gen + 1] = newx1;
                    }
                }

                /* Adaptation of S and mutation of the left point */
                S = Adap.Minimo(x0 - base_datos.intervalos[variable][etiqueta].min, newx1 - x0) / 2.0;
                n = ValorNormal(new_sigma * S);
                newx = x0 + n;
                if (newx <= base_datos.intervalos[variable][etiqueta].min) {
                    Hijo.Gene[gen] = base_datos.intervalos[variable][etiqueta].min;
                } else {
                    if (newx >= newx1) {
                        Hijo.Gene[gen] = newx1;
                    } else {
                        Hijo.Gene[gen] = newx;
                    }
                }

                /* Adaptation of S and mutation of the center right */
                S = Adap.Minimo(x2 - newx1, base_datos.intervalos[variable][etiqueta].max - x2) / 2.0;
                n = ValorNormal(new_sigma * S);
                newx = x2 + n;

                if (newx <= newx1) {
                    Hijo.Gene[gen + 2] = newx1;
                } else {
                    if (newx >= base_datos.intervalos[variable][etiqueta].max) {
                        Hijo.Gene[gen + 2] = base_datos.intervalos[variable][etiqueta].max;
                    } else {
                        Hijo.Gene[gen + 2] = newx;
                    }
                }													 
            }

            /* we evaluate the son */
            Hijo.Perf = fun_adap.eval(Hijo.Gene);

            /* we count the mutation */
            n_mutaciones += 1;

            /* if the son is better than the father this relieve his father, we accept sigma and we count another hit */
            if (Hijo.Perf > Padre.Perf) {
                n_exitos += 1;
                it_sin_exito = 0;
                sigma = new_sigma;

                for (j = 0; j < base_reglas.n_genes; j++) {
                    Padre.Gene[j] = Hijo.Gene[j];
                }

                Padre.Perf = Hijo.Perf;
            } else {
                it_sin_exito++;
            }

            /* we adapt sigma */
            new_sigma = AdaptacionSigma(sigma, n_exitos / (double) n_mutaciones, (double) base_reglas.n_genes - base_datos.tabla.n_variables);
//			System.out.println("new_sigma  = " + new_sigma);

            if (it_sin_exito >= n_gen_ee) {
                fin = 1;
            }
       } while (fin == 0);   
    }
}
