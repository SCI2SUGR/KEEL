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

package keel.Algorithms.RE_SL_Methods.MamWM;

import java.util.*;

class WM {
    public int MaxReglas = 10000;

    public double[] grado_pertenencia;
    public int[] Regla_act;
    public TipoRegla[] Conjunto_Reglas;
    public int peso;

    public String fich_datos_chequeo, fich_datos_tst;
    public String fichero_conf, ruta_salida;
    public String fichero_reglas, fichero_inf, fich_tra_obli, fich_tst_obli;
    public String informe = "";
    public String cadenaReglas = "";

    public MiDataset tabla, tabla_tst;
    public BaseR base_reglas;
    public BaseD base_datos;
    public Adap fun_adap;


    public WM(String f_e) {
        fichero_conf = f_e;
    }


    private String Quita_blancos(String cadena) {
        StringTokenizer sT = new StringTokenizer(cadena, "\t ", false);
        return (sT.nextToken());
    }


    /** Reads the data of the configuration file */
    public void leer_conf() {
        int i, n_etiquetas;
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
        ficheros.nextToken();
        fich_datos_chequeo = ((ficheros.nextToken()).replace('\"', ' ')).trim();
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

        // we read the Number of labels
        sT.nextToken();
        valor = sT.nextToken();
        n_etiquetas = Integer.parseInt(valor.trim());

        // we read the KB Output File Format with Weight values to 1 (0/1)
        peso = 0;

        // we create all the objects
        tabla = new MiDataset(fich_datos_chequeo, true);
        tabla_tst = new MiDataset(fich_datos_tst, false);
        base_datos = new BaseD(n_etiquetas, tabla.n_variables);

        for (i = 0; i < tabla.n_variables; i++) {
            base_datos.n_etiquetas[i] = n_etiquetas;
            base_datos.extremos[i].min = tabla.extremos[i].min;
            base_datos.extremos[i].max = tabla.extremos[i].max;
        }

        MaxReglas = tabla.long_tabla; //MaxReglas == #Ejemplos
        base_reglas = new BaseR(MaxReglas, base_datos, tabla);
        fun_adap = new Adap(tabla, tabla_tst, base_reglas);
        Regla_act = new int[tabla.n_variables];
        grado_pertenencia = new double[tabla.n_variables];
        Conjunto_Reglas = new TipoRegla[tabla.long_tabla];

        for (i = 0; i < tabla.long_tabla; i++) {
            Conjunto_Reglas[i] = new TipoRegla(tabla.n_variables);
        }
    }


    public void run() {
        int i, j, k, etiqueta, pos;
        double pert_act, grado_act, ec, el, ec_tst, el_tst;

        /* We read the configutate file and we initialize the structures and variables */
        leer_conf();

        if (tabla.salir == false) {
            /* we generate the semantics of the linguistic variables */
            base_datos.Semantica();

            /* we store the DB in the report file */
            informe = "\n\nInitial Data Base: \n\n";
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

            /* Inicialization of the counter of uncovered examples */
            base_reglas.n_reglas = 0;

            /* Iterative Rule Learning */
            for (i = 0; i < tabla.long_tabla; i++) {
                /* Generation of the best rule for the current example */
                for (j = 0; j < tabla.n_variables; j++) {

                    /* Determination of the best label for each variable */
                    grado_pertenencia[j] = 0.0;
                    etiqueta = 0;

                    for (k = 0; k < base_datos.n_etiquetas[j]; k++) {
                        pert_act = base_reglas.Fuzzifica(tabla.datos[i].ejemplo[
                                j], base_datos.BaseDatos[j][k]);
                        if (pert_act > grado_pertenencia[j]) {
                            grado_pertenencia[j] = pert_act;
                            etiqueta = k;
                        }
                    }

                    Regla_act[j] = etiqueta;
                }

                /* we calculate the covered degree */
                grado_act = 1.0;
                for (j = 0; j < tabla.n_variables; j++) {
                    grado_act *= grado_pertenencia[j];
                }

                /* we insert the new rule in the RB */
                pos = Pertenece(Regla_act, Conjunto_Reglas,
                                base_reglas.n_reglas);

                /* if the rule didn't exist */
                if (pos == -1) {
                    for (j = 0; j < tabla.n_variables; j++) {
                        Conjunto_Reglas[base_reglas.n_reglas].Regla[j] =
                                Regla_act[j];
                    }
                    Conjunto_Reglas[base_reglas.n_reglas].grado = grado_act;
                    base_reglas.n_reglas++;
                }

                /* if a rule with equal antecedet exist in the RB and the covered degree of the current rule is better, the consequent is replaced */
                else if (Conjunto_Reglas[pos].grado < grado_act) {
                    Conjunto_Reglas[pos].grado = grado_act;
                    if (Conjunto_Reglas[pos].Regla[tabla.n_var_estado] !=
                        Regla_act[tabla.n_var_estado]) {
                        Conjunto_Reglas[pos].Regla[tabla.n_var_estado] =
                                Regla_act[tabla.n_var_estado];
                    }
                }
            }

            /* we decode the generated rules */
            base_reglas.decodifica(Conjunto_Reglas);

            /* we calcule the MSEs */
            fun_adap.Error_tra();
            ec = fun_adap.EC;
            el = fun_adap.EL;

            fun_adap.Error_tst();
            ec_tst = fun_adap.EC;
            el_tst = fun_adap.EL;

            /* we write the RB */
            cadenaReglas = base_reglas.BRtoString(peso);
            cadenaReglas += "\nECMtra: " + ec + " ECMtst: " + ec_tst + "\n";

            Fichero.escribeFichero(fichero_reglas, cadenaReglas);

            /* we write the obligatory output files*/
            String salida_tra = tabla.getCabecera();
            salida_tra += fun_adap.getSalidaObli(tabla);
            Fichero.escribeFichero(fich_tra_obli, salida_tra);

            String salida_tst = tabla_tst.getCabecera();
            salida_tst += fun_adap.getSalidaObli(tabla_tst);
            Fichero.escribeFichero(fich_tst_obli, salida_tst);

            /* we write the MSEs in specific files */
            Fichero.AnadirtoFichero(ruta_salida + "WMcomunR.txt",
                                    "" + base_reglas.n_reglas + "\n");
            Fichero.AnadirtoFichero(ruta_salida + "WMcomunTRA.txt",
                                    "" + ec + "\n");
            Fichero.AnadirtoFichero(ruta_salida + "WMcomunTST.txt",
                                    "" + ec_tst + "\n");
        }
    }


    /**
     * Returns 1 if the better current rule is in the list "L" yet
     * @param R int[] New rule to check
     * @param L TipoRegla[] List of rules
     * @param n_generadas int Total number of rules
     * @return int 1 if the better current rule is in the list "L" yet, -1 else.
     */
    int Pertenece(int[] R, TipoRegla[] L, int n_generadas) {
        int nreg, var, esta;

        nreg = 0;
        while (nreg < n_generadas) {
            esta = 1;
            var = 0;

            while (var < tabla.n_var_estado && esta == 1) {
                if (R[var] != L[nreg].Regla[var]) {
                    esta = 0;
                } else {
                    var++;
                }
            }

            if (esta == 1) {
                return (nreg);
            }
            nreg++;
        }

        return ( -1);
    }
}

