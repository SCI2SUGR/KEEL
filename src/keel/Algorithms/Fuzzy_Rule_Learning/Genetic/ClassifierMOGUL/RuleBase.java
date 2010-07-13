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

class RuleBase {
/**	
 * <p>
 * It encodes the Rule Base
 * </p>
 */
 
        public FuzzySet [][] BaseReglas;
        public Structure [] Pob_reglas;
        public int n_reglas, n_genes, tipo_reglas, num_clases;

        public double [] GradoEmp;
        public T_Consequent [][] Consecuentes;

        public double [][]ListaTabu;

        public MyDataset tabla;
        public DataBase base_datos;
        
        
    /**
     * <p>
     * Constructor
     * </p>
     * @param MaxRules int The maximum number of rules int the RB
     * @param base DataBase The given Data Base
     * @param t MyDataset The set of examples
     * @param tipo int The type of the consequent of the rules
     */        
        public RuleBase (int MaxRules, DataBase base, MyDataset t, int tipo) {
                int i, j;

                tabla = t;
                base_datos = base;

                n_reglas = 0;
                n_genes = tabla.n_inputs * 4;
                tipo_reglas = tipo;
                num_clases = tabla.nClasses;

                Pob_reglas = new Structure[tabla.long_tabla];
                for (i=0; i<tabla.long_tabla; i++)  Pob_reglas[i] = new Structure (n_genes, num_clases);

                BaseReglas = new FuzzySet [MaxRules][tabla.n_inputs];

                Consecuentes = new T_Consequent[MaxRules][];

                for (i=0; i<MaxRules; i++) {
                        BaseReglas[i] = new FuzzySet [tabla.n_variables];

                        switch(tipo_reglas){
                        case 1: Consecuentes[i] = new T_Consequent[1];
                                Consecuentes[i][0] = new T_Consequent();
                                break;
                        case 2: Consecuentes[i] = new T_Consequent[1];
                                Consecuentes[i][0] = new T_Consequent();
                                break;
                        case 3: Consecuentes[i] = new T_Consequent[num_clases];
                                for(j=0; j < tabla.nClasses; j++){
                                    Consecuentes[i][j] = new T_Consequent();
                                }
                        }

                        for (j=0; j<tabla.n_inputs; j++)	BaseReglas[i][j] = new FuzzySet();
                }

                GradoEmp = new double[MaxRules];
                ListaTabu = new double[MaxRules][tabla.n_variables];
        }


        /**
         * <p>
         * Inserts a rule in the RB
         * </p>
         * @param Padre Structure An individual of the population is inserted as a rule
         */
        public void insert_rule (Structure Padre) {
                int i, pos_individuo;

                for (i=0; i < tabla.n_inputs; i++) {
                pos_individuo = tabla.n_inputs + 3 * i;
                ListaTabu[n_reglas][i] = Padre.Gene[pos_individuo+1];
                BaseReglas[n_reglas][i].Nombre = base_datos.BaseDatos[i][(int)Padre.Gene[i]].Nombre;
                BaseReglas[n_reglas][i].Etiqueta = base_datos.BaseDatos[i][(int)Padre.Gene[i]].Etiqueta;
                BaseReglas[n_reglas][i].x0 = Padre.Gene[pos_individuo];
                BaseReglas[n_reglas][i].x1 = Padre.Gene[pos_individuo + 1];
                BaseReglas[n_reglas][i].x2 = Padre.Gene[pos_individuo + 1];
                BaseReglas[n_reglas][i].x3 = Padre.Gene[pos_individuo + 2];
                BaseReglas[n_reglas][i].y = 1.0;
        }

             switch(tipo_reglas){
             case 1:
             case 2: Consecuentes[n_reglas][0].clase = Padre.Consecuente[0].clase;
                     Consecuentes[n_reglas][0].gcerteza = Padre.Consecuente[0].gcerteza;
                     break;
             case 3:
                 for(i=0; i < tabla.nClasses; i++){
                     Consecuentes[n_reglas][i].clase = Padre.Consecuente[i].clase;
                     Consecuentes[n_reglas][i].gcerteza = Padre.Consecuente[i].gcerteza;
                 }
             }

                n_reglas++;
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
        public double Fuzzification (double X, FuzzySet D) {
                /* If X are not in the rank D, the degree is 0 */
                if ((X<D.x0) || (X>D.x3))  return (0);
                if (X<D.x1)  return ((X-D.x0)*(D.y/(D.x1-D.x0)));
                if (X>D.x2)  return ((D.x3-X)*(D.y/(D.x3-D.x2)));

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
        public void Min (double [] entradas) {
                int b, b2;
                double minimo, y;

                for (b=0; b<n_reglas; b++) {
                        minimo = Fuzzification (entradas[0], BaseReglas[b][0]);

                        for (b2=1; b2 < tabla.n_inputs; b2++) {
                                y = Fuzzification (entradas[b2], BaseReglas[b][b2]);
                                if (y < minimo)  minimo=y;
                        }

                        GradoEmp[b] = minimo;
                }
        }



        /**
         * <p>
         * Prints the RB to a String
         * </p>
         * @return String The BR as a String object
         */
        public String BRtoString () {
                int i, j, k;
                String cadena="";

                cadena += "Number of rules: " + n_reglas + "\n\n";
                for (i=0; i < n_reglas; i++) {
                        for (j=0; j < tabla.n_inputs; j++)
                                cadena += "" + BaseReglas[i][j].Etiqueta + " " + BaseReglas[i][j].x0 + " " + BaseReglas[i][j].x1 + " " + BaseReglas[i][j].x3 + "\n";

                            switch(tipo_reglas){
                            case 1: cadena += "Class " + Consecuentes[i][0].clase + "\n";
                                    break;
                            case 2: cadena += "Class " + Consecuentes[i][0].clase + " CertaintyDegree " + Consecuentes[i][0].gcerteza + "\n";
                                    break;
                            case 3: for(k = 0; k < num_clases; k++){
                                    cadena += "Class " + Consecuentes[i][k].clase + " CertaintyDegree " + Consecuentes[i][k].gcerteza + "\n";
                                }
                            }


                        cadena += "\n";
                }

                return (cadena);
        }
}

