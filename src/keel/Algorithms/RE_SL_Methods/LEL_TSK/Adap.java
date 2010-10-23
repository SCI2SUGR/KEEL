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

class Adap {

    public static double omega, K;
    public double[] grado_pertenencia;
    public double[] puntos;
    public double F, G, g, PC;
    public int EmparejaAnt;
    public double EC, EL;
    public int tipo_fitness, tipo_nichos;
	public int aplicar_ee;

    public MiDataset tabla;
    public BaseR base_reglas;

    public Adap(MiDataset training, BaseR base, int ee) {
        tabla = training;
        base_reglas = base;
		aplicar_ee = ee;

        puntos = new double[tabla.n_variables];
        grado_pertenencia = new double[tabla.n_variables];
    }

    public static double Minimo(double x, double y) {
        if (x < y) {
            return (x);
        } else {
            return (y);
        }
    }


    public static double Maximo(double x, double y) {
        if (x > y) {
            return (x);
        } else {
            return (y);
        }
    }


    /* -------------------------------------------------------------------------
                                   FITNESS FUNCTION
     ------------------------------------------------------------------------- */

    /* ------------------------- Criteria of rules -------------------------- */

    /** Returns the matching degree of the rule "Ri(ek)" with the instance "ejem" */
    public double ReglaCubreEjemplo(double[] cromosoma, double[] ejem) {
        int i, pos_individuo;
        double minimo_ant, minimo_con;
        Difuso D = new Difuso();

        EmparejaAnt = 0;

        for (i = 0; i < tabla.n_variables; i++) {
            pos_individuo = tabla.n_variables + 3 * i;
            D.x0 = cromosoma[pos_individuo];
            D.x1 = cromosoma[pos_individuo + 1];
            D.x2 = cromosoma[pos_individuo + 1];
            D.x3 = cromosoma[pos_individuo + 2];
            D.y = 1;
            grado_pertenencia[i] = base_reglas.Fuzzifica(ejem[i], D);
        }

        minimo_ant = 1;
        minimo_con = 1;
        for (i = 0; i < tabla.n_var_estado; i++) {
            if (grado_pertenencia[i] < minimo_ant) {
                minimo_ant = grado_pertenencia[i];
            }
        }

        if (minimo_ant > 0) {
            EmparejaAnt = 1;
        }

        for (i = tabla.n_var_estado; i < tabla.n_variables; i++) {
            if (grado_pertenencia[i] < minimo_con) {
                minimo_con = grado_pertenencia[i];
            }
        }

        return (Minimo(minimo_ant, minimo_con));
    }


    /** Calcules the rule's criteria by the rule "cromosoma":
          - High frequency values [Her95]
          - High average covering degree over positive examples [Her95]
          - Small negative example set [Gon95]
     */
    public void CriteriosReglas(double[] cromosoma) {
        int i, n_ejem_pos, n_ejem_neg, n_ejem_ya_cub;
        double RCE, frec_acumulada, SumaRCEpositivos, umbral;

        n_ejem_pos = n_ejem_neg = 0;
        frec_acumulada = SumaRCEpositivos = 0.0;

        /* we count the number of negative and positive examples */
        for (i = 0; i < tabla.long_tabla; i++) {
            RCE = ReglaCubreEjemplo(cromosoma, tabla.datos[i].ejemplo);
            if (tabla.datos[i].cubierto == 0) {
                frec_acumulada += RCE;
                if (RCE >= omega) {
                        /* If the example has a cover higher than omega*/
                    /* then it's considered a positive example */
                    n_ejem_pos++;
                    SumaRCEpositivos += RCE;
                }
            }
            /* If the inputs is covered and the outputs isn't then the example is negative */
            if (RCE == 0.0 && EmparejaAnt > 0) {
                n_ejem_neg++;
            }
        }

        /* Rule's frecuency [Her95] */
        F = frec_acumulada / tabla.no_cubiertos;

        /* Average covering degree over positive examples [Her95] */
        if (n_ejem_pos > 0) {
            G = SumaRCEpositivos / (double) n_ejem_pos;
        } else {
            G = 0.0;
        }

        /* Penalty on the set of negative examples [Gon95] */
        umbral = K * n_ejem_pos;

        /* we don't penalize if the number of negative examples is less or equal than "umbral" */
        if (n_ejem_neg <= umbral) {
            g = 1.0;
        } else {
            g = 1.0 / (n_ejem_neg - umbral + Math.exp(1.0));
        }

        /* If we use the fitness function with penalty by overfitting, we'll calculate this penalty */
        if (tipo_fitness == 2) {
            n_ejem_ya_cub = 0;
            for (i = 0; i < tabla.long_tabla; i++) {
                RCE = ReglaCubreEjemplo(cromosoma, tabla.datos[i].ejemplo);

                /* If the example was covered by another rule it's counted for calculating the penalty */
                if (tabla.datos[i].cubierto > 0 && RCE != 0) {
                    n_ejem_ya_cub++;
                }

                if (n_ejem_ya_cub <= umbral) {
                    PC = 1.0;
                } else {
                    PC = 1.0 / (n_ejem_ya_cub - umbral + Math.exp(1.0));
                }
            }
        }
    }

    /** Mean Square Error(MSE) and Mean Linear Error(MLE) by training */
    public void Error_tra() {
        int j;
        double suma1, suma2, fuerza;

        for (j = 0, suma1 = suma2 = 0.0; j < tabla.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla.datos[j].ejemplo);
            suma1 += Math.pow(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza, 2.0);
            suma2 += Math.abs(tabla.datos[j].ejemplo[tabla.n_var_estado] - fuerza);
        }

        EC = suma1 / (double) tabla.long_tabla;
        EL = suma2 / (double) tabla.long_tabla;
    }

    /** Mean Square Error(MSE) and Mean Linear Error(MLE) by test */
    public void Error_tst(MiDataset tabla_tst) {
        int j;
        double suma1, suma2, fuerza;

        for (j = 0, suma1 = suma2 = 0.0; j < tabla_tst.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla_tst.datos[j].ejemplo);
            suma1 += Math.pow(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] - fuerza, 2.0);
            suma2 += Math.abs(tabla_tst.datos[j].ejemplo[tabla_tst.n_var_estado] - fuerza);
        }

        EC = suma1 / (double) tabla_tst.long_tabla;
        EL = suma2 / (double) tabla_tst.long_tabla;
    }

    /** Returns the rule's fitness */
    public double eval(double[] cromosoma) {
        if (tipo_fitness == 1) {
            return (eval_mulmodal(cromosoma));
        } else {
            return (eval_criterios(cromosoma));
        }
    }


    /** Fitness with penalty by niches */
    public double eval_mulmodal(double[] cromosoma) {
        double fitness;

        /* We calculate the fitness through the tree penality criteria */
        CriteriosReglas(cromosoma);
        fitness = F * G * g;

        /* If the ES is used we consider the niches */
        if (aplicar_ee == 1) {
            fitness *= LNIR(cromosoma);
        }

        return (fitness);
    }


    /** Fitness with penalty by overfitting */
    public double eval_criterios(double[] cromosoma) {
        double fitness;

        /* We calculate the fitness through the tree penality criteria */
        CriteriosReglas(cromosoma);
        fitness = F * G * g;

        /* If the ES is used we consider the penality */
        if (aplicar_ee == 1) {
            fitness *= PC;
        }

        return (fitness);
    }


    /* -------------------------------------------------------------------------
                   Functions by the interaction ratio (niches)
     ------------------------------------------------------------------------- */


    /** Small Penalty: The rule "cromosoma" is more penalized if this is near to the centers of the other learned rules */
    public double NIR1(double[] cromosoma) {
        int i;
        double cubr_act, max_cubr;

        /* we assess the matching degree of the rule "cromosoma" with the centers of the other learned rules */
        max_cubr = (double) 0.0;
        for (i = 0; i < base_reglas.n_reglas; i++) {
            cubr_act = ReglaCubreEjemplo(cromosoma, base_reglas.ListaTabu[i]);

            if (cubr_act > max_cubr) {
                max_cubr = cubr_act;
            }
        }

        return (max_cubr);
    }


    /** Average Penalty 1: The rule "cromosoma" is more penalized if this is near to the support and centers of the other learned rules */
    public double NIR2(double[] cromosoma) {
        int i, j;
        double[] cubr_act = new double[3];
        double max_cubr;

        /* we assess the matching degree of the rule "cromosoma" with the support and centers of the other learned rules */
        max_cubr = (double) 0.0;
        for (i = 0; i < base_reglas.n_reglas; i++) {
            /* left point */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x0;
            }

            cubr_act[0] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[0] > max_cubr) {
                max_cubr = cubr_act[0];
            }

            /* center */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x1;
            }

            cubr_act[1] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[1] > max_cubr) {
                max_cubr = cubr_act[1];
            }

            /* right point */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x3;
            }
            cubr_act[2] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[2] > max_cubr) {
                max_cubr = cubr_act[2];
            }
        }

        return (max_cubr);
    }


    /** Average Penalty 2: The rule "cromosoma" is more penalized if it's near to the certers of the learned rules and if these are more near to the certers of the rule "cromosoma" */
    public double NIR3(double[] cromosoma) {
        int i, j;
        double cubr_act, max_cubr_otras, max_cubr_nueva, grado_pertenencia;

        /* we assess the matching degree of the rule "cromosoma" with the centers of the other learned rules */
        max_cubr_otras = (double) 0.0;
        for (i = 0; i < base_reglas.n_reglas; i++) {
            cubr_act = ReglaCubreEjemplo(cromosoma, base_reglas.ListaTabu[i]);
            if (cubr_act > max_cubr_otras) {
                max_cubr_otras = cubr_act;
            }
        }

        /* we assess the matching degree of the learned rules with the centers of the "cromosoma" */
        max_cubr_nueva = (double) 0.0;

        for (i = 0; i < base_reglas.n_reglas; i++) {
            cubr_act = 1.0;
            for (j = 0; j < tabla.n_variables; j++) {
                grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.
                        n_variables + (3 * j) + 1], base_reglas.BaseReglas[i][j]);
                if (grado_pertenencia < cubr_act) {
                    cubr_act = grado_pertenencia;
                }
            }

            if (cubr_act > max_cubr_nueva) {
                max_cubr_nueva = cubr_act;
            }
        }

        return (Adap.Maximo(max_cubr_otras, max_cubr_nueva));
    }


    /** High Penalty: The rule "cromosoma" is more penalized if it's near to the support and certers of the learned rules and if these are more near to the support and certers of the rule "cromosoma" */
    public double NIR4(double[] cromosoma) {
        int i, j;
        double[] cubr_act = new double[3];
        double max_cubr_otras, max_cubr_nueva, grado_pertenencia;

        /* we assess the matching degree of the rule "cromosoma" with the centers and supports of the other learned rules */
        max_cubr_nueva = (double) 0.0;
        for (i = 0; i < base_reglas.n_reglas; i++) {
            /* left point */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x0;
            }

            cubr_act[0] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[0] > max_cubr_nueva) {
                max_cubr_nueva = cubr_act[0];
            }

            /* center */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x1;
            }

            cubr_act[1] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[1] > max_cubr_nueva) {
                max_cubr_nueva = cubr_act[1];
            }

            /* right point */
            for (j = 0; j < tabla.n_variables; j++) {
                puntos[j] = base_reglas.BaseReglas[i][j].x3;
            }

            cubr_act[2] = ReglaCubreEjemplo(cromosoma, puntos);
            if (cubr_act[2] > max_cubr_nueva) {
                max_cubr_nueva = cubr_act[2];
            }
        }

        /* we assess the matching degree of the learned rules with the support and centers of the rule "cromosoma" */
        max_cubr_otras = (double) 0.0;
        for (i = 0; i < base_reglas.n_reglas; i++) {
            cubr_act[0] = cubr_act[1] = cubr_act[2] = 1.0;
            for (j = 0; j < tabla.n_variables; j++) {
                /* left point */
                grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.
                        n_variables + (3 * j)], base_reglas.BaseReglas[i][j]);
                if (grado_pertenencia < cubr_act[0]) {
                    cubr_act[0] = grado_pertenencia;
                }

                /* center */
                grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.
                        n_variables + (3 * j) + 1], base_reglas.BaseReglas[i][j]);
                if (grado_pertenencia < cubr_act[1]) {
                    cubr_act[1] = grado_pertenencia;
                }

                /* right point */
                grado_pertenencia = base_reglas.Fuzzifica(cromosoma[tabla.
                        n_variables + (3 * j) + 2], base_reglas.BaseReglas[i][j]);
                if (grado_pertenencia < cubr_act[2]) {
                    cubr_act[2] = grado_pertenencia;
                }
            }

            if (cubr_act[0] > max_cubr_otras) {
                max_cubr_otras = cubr_act[0];
            }
            if (cubr_act[1] > max_cubr_otras) {
                max_cubr_otras = cubr_act[1];
            }
            if (cubr_act[2] > max_cubr_otras) {
                max_cubr_otras = cubr_act[2];
            }
        }

        return (Adap.Maximo(max_cubr_otras, max_cubr_nueva));
    }


    /** Returns the penalty by niches */
    public double LNIR(double[] cromosoma) {
        double salida = 1;

        switch (tipo_nichos) {
        case 1:
            salida = 1 - NIR1(cromosoma);
            break;
        case 2:
            salida = 1 - NIR2(cromosoma);
            break;
        case 3:
            salida = 1 - NIR3(cromosoma);
            break;
        case 4:
            salida = 1 - NIR4(cromosoma);
            break;
        }

        return (salida);
    }


    /** Returns the data for creating the KEEL output file */
    public String getSalidaObli(MiDataset tabla_datos) {
        int j;
        double fuerza;
        String salida;

        salida = "@data\n";
        for (j = 0; j < tabla_datos.long_tabla; j++) {
            fuerza = base_reglas.FLC(tabla_datos.datos[j].ejemplo);
            salida += (tabla_datos.datos[j]).ejemplo[tabla_datos.n_var_estado] +
                    " " + fuerza + " " + "\n";
        }

        salida = salida.substring(0, salida.length() - 1);

        return (salida);
    }


}
