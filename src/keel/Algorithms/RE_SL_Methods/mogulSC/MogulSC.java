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

package keel.Algorithms.RE_SL_Methods.mogulSC;

import org.core.*;
import java.util.*;

class MogulSC {
    public int MaxReglas;

    public int aplicar_ee;
    public double semilla, epsilon;
    public int[] indices_nc;
    public int[] Regla_act;

    public String fich_datos_chequeo, fich_datos_val, fich_datos_tst;
    public String fichero_conf, fichero_inf, ruta_salida;
    public String fichero_datos, fichero_reglas, fich_tra_obli, fich_tst_obli;
    public String cadenaReglas = "";


    public Structure Padre, Hijo;
    public MiDataset tabla, tabla_tst, tabla_val;
    public BaseR base_reglas;
    public BaseD base_datos;
    public Adap fun_adap;
    public Est_evol ee_11;


    public MogulSC(String f_e) {
        fichero_conf = f_e;
    }


    private String Quita_blancos(String cadena) {
        StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
        return (sT.nextToken());
    }


    /** Reads the data of the configuration file */
    public void leer_conf() {
        int i, j, n_etiquetas;
        int n_gen_ee, tipo_nichos, tipo_fitness;
        double omega, K;

        String cadenaEntrada, valor;

        // we read the file in a String
        cadenaEntrada = Fichero.leeFichero(fichero_conf);
        StringTokenizer sT = new StringTokenizer(cadenaEntrada, "\n\r=", false);

        // we read the algorithm's name
        sT.nextToken();
        sT.nextToken();

        // we read the name of the training and test files
        sT.nextToken();
        valor = sT.nextToken();

        StringTokenizer ficheros = new StringTokenizer(valor, "\t ", false);
        fich_datos_chequeo = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fich_datos_val = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fich_datos_tst = ((ficheros.nextToken()).replace('\"', ' ')).trim();

        // we read the name of the output files
        sT.nextToken();
        valor = sT.nextToken();

        ficheros = new StringTokenizer(valor, "\t ", false);
        fich_tra_obli = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fich_tst_obli = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fichero_reglas = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        fichero_inf = ((ficheros.nextToken()).replace('\"', ' ')).trim();
        ruta_salida = fichero_reglas.substring(0,
                                               fichero_reglas.lastIndexOf('/') +
                                               1);

        // we read the seed of the random generator
        sT.nextToken();
        valor = sT.nextToken();
        semilla = Double.parseDouble(valor.trim());
        Randomize.setSeed((long) semilla);

        // we read if the model is descriptive or approximation
        sT.nextToken();
        valor = sT.nextToken();
        aplicar_ee = Integer.parseInt(valor.trim());

        // we read the evolution strategy iterations
        sT.nextToken();
        valor = sT.nextToken();
        n_gen_ee = Integer.parseInt(valor.trim());

        // we read the type of niche
        sT.nextToken();
        valor = sT.nextToken();
        tipo_nichos = Integer.parseInt(valor.trim());

        // we read the omega parameter for the maching degree of the positive instances
        sT.nextToken();
        valor = sT.nextToken();
        omega = Double.parseDouble(valor.trim());

        // we read the K parameter for the percentage of allowed negative instances
        sT.nextToken();
        valor = sT.nextToken();
        K = Double.parseDouble(valor.trim());

        // we read the epsilon parameter for the minimun maching degree required to the KB
        sT.nextToken();
        valor = sT.nextToken();
        epsilon = Double.parseDouble(valor.trim());

        // we read the type of fitness function
        sT.nextToken();
        valor = sT.nextToken();
        tipo_fitness = Integer.parseInt(valor.trim());

        // we read the number of labels
        sT.nextToken();
        valor = sT.nextToken();
        n_etiquetas = Integer.parseInt(valor.trim());

        // we create all the objects
        tabla = new MiDataset(fich_datos_chequeo, true);
        tabla_val = new MiDataset(fich_datos_val, false);
        tabla_tst = new MiDataset(fich_datos_tst, false);

        base_datos = new BaseD(n_etiquetas, tabla);

        MaxReglas = 10000;

        base_reglas = new BaseR(MaxReglas, base_datos, tabla);

        fun_adap = new Adap(tabla, base_reglas, aplicar_ee);
        fun_adap.tipo_nichos = tipo_nichos;
        fun_adap.omega = omega;
        fun_adap.K = K;
        fun_adap.tipo_fitness = tipo_fitness;

        ee_11 = new Est_evol(base_datos, base_reglas, fun_adap, n_gen_ee);

        indices_nc = new int[tabla.long_tabla];
        Regla_act = new int[tabla.n_variables];
        Padre = new Structure(base_reglas.n_genes);
        Hijo = new Structure(base_reglas.n_genes);

        System.out.println("N_genes " + base_reglas.n_genes);

	}


    public void run() {
        int i, j, pos_individuo;
        double RCE, min_CR, min_CVR, ec, el, ec_tst, el_tst, PN, fitness;

        /* We read the configutate file and we initialize the structures and variables */
        leer_conf();

        if (tabla.salir == false) {
            System.out.println("IRLSC-Mam");
            /* we generate the semantics of the linguistic variables */
            base_datos.Semantica();

            /* we store the DB in the report file */
            String informe = "Initial Data Base: \n\n";
            for (i = 0; i < tabla.n_variables; i++) {
                informe += "  Variable " + (i + 1) + ":\n";
                for (j = 0; j < base_datos.n_etiquetas[i]; j++) {
                    informe += "    Label " + (j + 1) + ": (" +
                            base_datos.BaseDatos[i][j].x0 + "," +
                            base_datos.BaseDatos[i][j].x1 + "," +
                            base_datos.BaseDatos[i][j].x3 + ")\n";
                }

                informe += "\n";
            }

            informe += "\n";
            Fichero.escribeFichero(fichero_inf, informe);

            /* Inicialization of the counters */
            tabla.no_cubiertos = tabla.long_tabla;
            base_reglas.n_reglas = 0;

            /* Iterative Rule Learning */
            do {

                /* Phase 1: Generation of the better rule */
                Generate();

                fun_adap.CriteriosReglas(Padre.Gene);
				fitness = fun_adap.F * fun_adap.G * fun_adap.g;

                if (fun_adap.tipo_fitness == 1) {
                    PN = fun_adap.LNIR(Padre.Gene);
                    fitness *= PN;
                } else {
                    fitness *= fun_adap.PC;
                }

                /* Phase 2: Optimization of the rule */
                if (aplicar_ee == 1) {
                    ee_11.Estrategia_Evolucion(Padre, Hijo);

                    fun_adap.CriteriosReglas(Padre.Gene);
                    fitness = fun_adap.F * fun_adap.G * fun_adap.g;
                    if (fun_adap.tipo_fitness == 1) {
                        PN = fun_adap.LNIR(Padre.Gene);
                        fitness *= PN;
                    } else {
                        fitness *= fun_adap.PC;
                    }
                }

                /* The rule is stored in the RB */
                base_reglas.inserta_regla(Padre);

                /* we calculate the matching degree of the rule with each example. the covered examples are marked */
                for (i = 0; i < tabla.long_tabla; i++) {
                    RCE = fun_adap.ReglaCubreEjemplo(Padre.Gene, tabla.datos[i].ejemplo);

                    tabla.datos[i].nivel_cubrimiento += RCE;
                    tabla.datos[i].maximo_cubrimiento = Adap.Maximo(tabla.datos[i].maximo_cubrimiento, RCE);
                    if ((tabla.datos[i].nivel_cubrimiento >= epsilon) && (tabla.datos[i].cubierto == 0)) {
                        tabla.datos[i].cubierto = 1;
                        tabla.no_cubiertos--;
                    }
                }

                /* the multimodal GA finish when the condition is true */
            } while (Parada() == 0);

            /* we calculate the minimum and maximum matching */
            min_CR = 1.0;
            min_CVR = 10E37;
            for (i = 0; i < tabla.long_tabla; i++) {
                min_CR = Adap.Minimo(min_CR, tabla.datos[i].maximo_cubrimiento);
                min_CVR = Adap.Minimo(min_CVR, tabla.datos[i].nivel_cubrimiento);
            }

            /* we calcule the MSEs */
            fun_adap.Error_tra();
            ec = fun_adap.EC;
            el = fun_adap.EL;

            fun_adap.Error_tst(tabla_tst);
            ec_tst = fun_adap.EC;
            el_tst = fun_adap.EL;

            /* we write the RB */
            cadenaReglas = base_reglas.BRtoString();
            cadenaReglas += "\nMSEtra: " + ec + " MSEtst: " + ec_tst +
                    "\nMinimun C_R: " + min_CR + " MSE CV_R: " + min_CVR + "\n";

            Fichero.escribeFichero(fichero_reglas, cadenaReglas);

            /* we write the obligatory output files*/
            String salida_tra = tabla_val.getCabecera();
            salida_tra += fun_adap.getSalidaObli(tabla_val);
            Fichero.escribeFichero(fich_tra_obli, salida_tra);

            String salida_tst = tabla_tst.getCabecera();
            salida_tst += fun_adap.getSalidaObli(tabla_tst);
            Fichero.escribeFichero(fich_tst_obli, salida_tst);

            /* we write the MSEs in specific files */
            Fichero.AnadirtoFichero(ruta_salida + "MogulSCcomunR.txt", "" + base_reglas.n_reglas + "\n");
            Fichero.AnadirtoFichero(ruta_salida + "MogulSCcomunTRA.txt", "" + ec + "\n");
            Fichero.AnadirtoFichero(ruta_salida + "MogulSCcomunTST.txt", "" + ec_tst + "\n");
        }
    }


    /** Returns 1 if the best current rule is in the list "L" yet */
    private int Pertenece(int n_generadas) {
        int nreg, var, esta;

        nreg = 0;
        while (nreg < n_generadas) {
            esta = 1;
            var = 0;

            while ((var < tabla.n_variables) && (esta == 1)) {
                if (Regla_act[var] != base_reglas.Pob_reglas[nreg].Gene[var]) {
                    esta = 0;
                } else {
                    var++;
                }
            }

            if (esta == 1) {
                return (1);
            }
            nreg++;
        }

        return (0);
    }


    /* Generates the best rule */
    public void Generate() {
        int i, j, k, etiqueta, pos_individuo, n_reg_generadas, indice_mejor;
        double grado_pertenencia, max_pert;

        /* we obtain the uncovered examples */
        i = j = 0;
        while ((i < tabla.no_cubiertos) && (j < tabla.long_tabla)) {
            if (tabla.datos[j].cubierto == 0) {
                indices_nc[i] = j;
                i++;
            }
            j++;
        }

        /* we generate the best rule for each example */
        n_reg_generadas = 0;
        for (i = 0; i < tabla.no_cubiertos; i++) {
            /* Determination of the best label for each variable */
            for (j = 0; j < tabla.n_variables; j++) {
                max_pert = 0;
                etiqueta = 0;
                for (k = 0; k < base_datos.n_etiquetas[j]; k++) {
                    grado_pertenencia = base_reglas.Fuzzifica(tabla.datos[indices_nc[i]].ejemplo[j], base_datos.BaseDatos[j][k]);
                    if (grado_pertenencia > max_pert) {
                        max_pert = grado_pertenencia;
                        etiqueta = k;
                    }
                }

                Regla_act[j] = etiqueta;
            }


            /* if the rule aren't in the set, it's insert */
            if (Pertenece(n_reg_generadas) == 0) {
                for (j = 0; j < tabla.n_variables; j++) {
                    etiqueta = Regla_act[j];
                    pos_individuo = tabla.n_variables + 3 * j;
                    base_reglas.Pob_reglas[n_reg_generadas].Gene[j] = (double) etiqueta;
                    base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo] = base_datos.BaseDatos[j][etiqueta].x0;
                    base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo + 1] = base_datos.BaseDatos[j][etiqueta].x1;
                    base_reglas.Pob_reglas[n_reg_generadas].Gene[pos_individuo + 2] = base_datos.BaseDatos[j][etiqueta].x3;
                }

                n_reg_generadas++;
            }
        }

        /* we obtain the best rule */
        Padre.Perf = 0;
        indice_mejor = 0;
        for (i = 0; i < n_reg_generadas; i++) {
            base_reglas.Pob_reglas[i].Perf = fun_adap.eval(base_reglas.Pob_reglas[i].Gene);

            if (base_reglas.Pob_reglas[i].Perf > Padre.Perf) {
                Padre.Perf = base_reglas.Pob_reglas[i].Perf;
                indice_mejor = i;
            }
        }

        for (i = 0; i < base_reglas.n_genes; i++) {
            Padre.Gene[i] = base_reglas.Pob_reglas[indice_mejor].Gene[i];
        }

        Padre.Perf = base_reglas.Pob_reglas[indice_mejor].Perf;
    }


    /** Criterion of stop */
    public int Parada() {
        if ((tabla.no_cubiertos == 0) || (base_reglas.n_reglas == MaxReglas)) {
            return (1);
        } else {
            return (0);
        }
    }


}

