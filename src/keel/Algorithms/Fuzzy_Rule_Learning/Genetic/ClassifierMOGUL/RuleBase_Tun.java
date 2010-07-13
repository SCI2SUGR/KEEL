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

public class RuleBase_Tun {
/**	
 * <p>
 * It encodes a Rule Base for the tunning process
 * </p>
 */
 
    public Rule[] BaseReglas;
    public char[] modificador;
    public int max_reglas, n_reglas_distintas;
    public int tipo_reglas, tipo_modificadores, n_etiquetas;
    public int n_reglas;
    public MyDataset tabla;

    public double[] GradoEmp;


    /**
     * <p>
     * Constructor
     * </p>
     * @param fichero String The name of the file containing the previous simplified RB
     * @param datos MyDataset The set of examples
     * @param tipo1 int The type of fuzzy rule
     * @param tipo2 int The type of liguistic modifier
     * @param num_etiq int The number of liguistic labels per variable
     */  
    public RuleBase_Tun(String fichero, MyDataset datos, int tipo1, int tipo2,
                     int num_etiq) {
        int i;

        tabla = datos;
        tipo_reglas = tipo1;
        tipo_modificadores = tipo2;
        n_etiquetas = num_etiq;

        leer_BR(fichero);

        GradoEmp = new double[n_reglas];
    }

/*    public RuleBase_Tun(int Max_reglas, MyDataset datos, int tipo1, int tipo2,
                     int num_etiq) {
        int i, j;
        int n_genes_modificadores = 0;

        tabla = datos;
        n_reglas = 0;
        max_reglas = Max_reglas;
        tipo_reglas = tipo1;
        tipo_modificadores = tipo2;
        n_etiquetas = num_etiq;

        BaseReglas = new Rule[max_reglas];

        switch (tipo_modificadores) {
        case 0:
            n_genes_modificadores = 0;
            break;
        case 1:
            n_genes_modificadores = tabla.n_inputs * n_etiquetas;
            break;
        case 2:
            n_genes_modificadores = tabla.n_inputs * max_reglas;
            break;
        }

        modificador = new char[n_genes_modificadores];

        GradoEmp = new double[max_reglas];

        for (i = 0; i < max_reglas; i++) {
            BaseReglas[i] = new Rule(tabla.n_inputs, tabla.n_variables);
        }
    }
*/

    /**
     * <p>
     * Reads the previous simplified RB from a input file
     * </p>
     * @param fichero String The name of the file containing the simplified RB
     */
    public void leer_BR(String fichero) {
        int i, j, k, repetida, n_modificadores, cont;
        String cadena, aux;
        int n_genes_modificadores = 0;

        cadena = MyFile.ReadMyFile(fichero);

        StringTokenizer sT = new StringTokenizer(cadena, "\n\r\t ", false);
        sT.nextToken();
        sT.nextToken();
        sT.nextToken();

        n_reglas = Integer.parseInt(sT.nextToken());

        switch (tipo_modificadores) {
        case 0:
            n_genes_modificadores = 0;
            break;
        case 1:
            n_genes_modificadores = tabla.n_inputs * n_etiquetas;
            break;
        case 2:
            n_genes_modificadores = tabla.n_inputs * n_reglas;
            break;
        }

        modificador = new char[n_genes_modificadores];


        BaseReglas = new Rule[n_reglas];
        for (i = 0; i < n_reglas; i++) {
            BaseReglas[i] = new Rule(tabla.n_inputs, tabla.nClasses);
        }

        n_modificadores = 0;
        n_reglas_distintas = 0;
        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_inputs; j++) {
                BaseReglas[i].Ant[j].Etiqueta = sT.nextToken();
                BaseReglas[i].Ant[j].x0 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].x1 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].x2 = BaseReglas[i].Ant[j].x1;
                BaseReglas[i].Ant[j].x3 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].y = 1.0;

                k = repetida=0;
                while ((k < i) && (repetida == 0)) {
                  if (BaseReglas[i].Ant[j].x0 == BaseReglas[k].Ant[j].x0 &&
                      BaseReglas[i].Ant[j].x1 == BaseReglas[k].Ant[j].x1 &&
                      BaseReglas[i].Ant[j].x3 == BaseReglas[k].Ant[j].x3)
                    repetida = 1;
                  else
                    k++;
                }
                if (repetida == 0)
                  n_reglas_distintas++;

            }

            switch (tipo_reglas) {
            case 1:
                sT.nextToken();
                BaseReglas[i].Cons[0].clase = Integer.parseInt(sT.nextToken());
                break;
            case 2:
                sT.nextToken();
                BaseReglas[i].Cons[0].clase = Integer.parseInt(sT.nextToken());
                sT.nextToken();
                BaseReglas[i].Cons[0].gcerteza = Double.parseDouble(sT.
                        nextToken());
                break;
            case 3:
                for (j = 0; j < tabla.nClasses; j++) {
                    sT.nextToken();
                    BaseReglas[i].Cons[j].clase = Integer.parseInt(sT.nextToken());
                    sT.nextToken();
                    BaseReglas[i].Cons[j].gcerteza = Double.parseDouble(sT.
                            nextToken());
                }
            }

            if (tipo_modificadores == 2) {
                /* Hedges by partition/rule */
                sT.nextToken();
                sT.nextToken();
                sT.nextToken();
                sT.nextToken();
                sT.nextToken();

                for (j = 0; j < tabla.n_inputs; j++) {
                    sT.nextToken();
                    sT.nextToken();
                    aux = sT.nextToken();
                    if (aux.contains("No")) {
                        modificador[n_modificadores++] = '0';
                        sT.nextToken();
                    } else {
                        if (aux.contains("More")) {
                            modificador[n_modificadores++] = '1';
                            sT.nextToken();
                            sT.nextToken();
                        } else {
                            modificador[n_modificadores++] = '2';
                        }
                    }
                }
            }
        }

        if (tipo_modificadores == 1) {
            sT.nextToken();
            sT.nextToken();
            sT.nextToken();

            cont = 0;
            for (j = 0; j < tabla.n_inputs; j++) {
                sT.nextToken();
                sT.nextToken();
                for (i = 0; i < n_etiquetas; i++) {
                    sT.nextToken();
                    sT.nextToken();
                    aux = sT.nextToken();
                    if (aux.contains("No")) {
                        modificador[cont++] = '0';
                        sT.nextToken();
                    } else {
                        if (aux.contains("More")) {
                            modificador[cont++] = '1';
                            sT.nextToken();
                            sT.nextToken();
                        } else {
                            modificador[cont++] = '2';
                        }
                    }
                }
            }
        }
    }


    /* -------------------------------------------------------------------------
                                                  Fuzzification Interface
     ------------------------------------------------------------------------- */

	/*
	 * <p>
	 * Fuzzification Interface
	 * </p>
	 * @param X double The value of the example
	 * @param D FuzzySet The fuzzy set
	 * @return The fuzzification value 
	 */
    public double Fuzzification(double X, FuzzySet D) {
        /* If X are not in the rank D, the degree is 0 */
        if ((X < D.x0) || (X > D.x3)) {
            return (0);
        }
        if (X < D.x1) {
            return ((X - D.x0) * (D.y / (D.x1 - D.x0)));
        }
        if (X > D.x2) {
            return ((D.x3 - X) * (D.y / (D.x3 - D.x2)));
        }

        return (D.y);
    }


    /* -------------------------------------------------------------------------
                                                        Conjunction Operator
     ------------------------------------------------------------------------- */

        /*
         * <p>
         * Minimum T-norm
         * </p>
         * @param entradas double[] The given example
         */
    public void Min(double[] entradas) {
        int b, b2;
        double minimo, y;

        for (b = 0; b < n_reglas; b++) {
            minimo = Fuzzification(entradas[0], BaseReglas[b].Ant[0]);

            for (b2 = 1; (minimo != 0.0) && (b2 < tabla.n_inputs); b2++) {
                y = Fuzzification(entradas[b2], BaseReglas[b].Ant[b2]);
                if (y < minimo) {
                    minimo = y;
                }
            }

            GradoEmp[b] = minimo;
        }
    }


    /* -------------------------------------------------------------------------
                         Inference of a TSK Fuzzy System
     ------------------------------------------------------------------------- */

    /*    public double Inferencia_TSK (double [] Entrada) {
                double num, den, salida_regla;
                int i, j;

                num = 0;
                den = 0;
                for (i=0; i < n_reglas; i++) {
                        if (GradoEmp[i] != 0.0) {
     */
    /* we initialize the output to the 'b' value */
    /*                            salida_regla = BaseReglas[i].Cons[tabla.n_inputs];
                                for (j=0; j<tabla.n_inputs; j++)
     salida_regla += BaseReglas[i].Cons[j] * Entrada[j];

                                num += GradoEmp[i] * salida_regla;
                                den += GradoEmp[i];
                        }
                }

                if (den != 0)  return (num / den);
                else  return ((tabla.extremos[tabla.n_inputs].max - tabla.extremos[tabla.n_inputs].min)/2.0);
        }
     */


    /* -------------------------------------------------------------------------
     Fuzzy Controller
     ------------------------------------------------------------------------- */

    /*    public double FLC_TSK (double [] Entrada) {
                Min (Entrada);
                return (Inferencia_TSK (Entrada));
        }
     */


        /**
         * <p>
         * Prints the RB to a String
         * </p>
         * @return String The BR as a String object
         */
    public String BRtoString() {
        int i, j;
        String cadena = "";

        cadena += "Number of rules: " + n_reglas + "\n\n";
        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_inputs; j++) {
                cadena += "" + BaseReglas[i].Ant[j].Etiqueta + " " +
                        BaseReglas[i].Ant[j].x0 + " " + BaseReglas[i].Ant[j].x1 +
                        " " + BaseReglas[i].Ant[j].x3 + "\n";
            }

            switch (tipo_reglas) {
            case 1:
                cadena += "Class " + BaseReglas[i].Cons[0].clase + "\n";
                break;
            case 2:
                cadena += "Class " + BaseReglas[i].Cons[0].clase + " CertaintyDegree " +
                        BaseReglas[i].Cons[0].gcerteza + "\n";
                break;
            case 3:
                for (j = 0; j < tabla.nClasses; j++) {
                    cadena += "Class " + BaseReglas[i].Cons[j].clase +
                            " CertaintyDegree " + BaseReglas[i].Cons[j].gcerteza +
                            "\n";
                }
            }

            cadena += "\n";

            if (tipo_modificadores == 2) {
                /* Hedges by partition/rule */
                cadena += "Hedges of the rule " + (i + 1) + ":\n";
                for (j = 0; j < tabla.n_inputs; j++) {
                    switch (modificador[(tabla.n_inputs * i) + j]) {
                    case '0':
                        cadena += "Var " + (j + 1) + "\t No hedge\n";
                        break;
                    case '1':
                        cadena += "Var " + (j + 1) + "\t More or less\n";
                        break;
                    case '2':
                        cadena += "Var " + (j + 1) + "\t Very\n";
                        break;
                    }
                }
                cadena += "\n";
            }
        }

        if (tipo_modificadores == 1) {
            cadena += "Hedges by partition:\n";
            for (j = 0; j < tabla.n_inputs; j++) {
                cadena += "Variable " + (j + 1) + "\n";
                for (i = 0; i < n_etiquetas; i++) {
                    switch (modificador[(j * n_etiquetas) + i]) {
                    case '0':
                        cadena += "\tL " + (i + 1) + "\t No hedge\n";
                        break;
                    case '1':
                        cadena += "\tL " + (i + 1) + "\t More or Less\n";
                        break;
                    case '2':
                        cadena += "\tL " + (i + 1) + "\t Very\n";
                    }
                }
            }
        }

        cadena += "\n";

        return (cadena);
    }

}

