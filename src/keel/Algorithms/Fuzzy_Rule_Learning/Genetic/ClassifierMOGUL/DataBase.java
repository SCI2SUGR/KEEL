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

class DataBase {
/**	
 * <p>
 * It encodes the Data Base
 * </p>
 */

        public FuzzySet [][]BaseDatos;
        public int n_variables;
        public int [] n_etiquetas;

        public T_Interval [] extremos;
        public T_Interval [][]intervalos;


    /**
     * <p>
     * Constructor
     * </p>
     * @param MaxEtiquetas int The maximum number of label per variable
     * @param n_var int The number of input variables
     */
        public DataBase (int MaxEtiquetas, int n_var) {
                int i, j;

                n_variables = n_var;

                intervalos = new T_Interval [n_variables][MaxEtiquetas];
                BaseDatos = new FuzzySet[n_variables][MaxEtiquetas];

                for (i=0; i < n_variables; i++) {
                        BaseDatos[i] = new FuzzySet[MaxEtiquetas];
                        intervalos[i] = new T_Interval [MaxEtiquetas];
                        for (j=0; j < MaxEtiquetas; j++) {
                                BaseDatos[i][j] = new FuzzySet();
                                intervalos[i][j] = new T_Interval();
                        }
                }

                n_etiquetas = new int[n_variables];

                extremos = new T_Interval[n_variables];
                for (i=0; i<n_variables; i++)  extremos[i] = new T_Interval();
        }


    /**
     * <p>
     * Rounds the generated value for the semantics
     * </p>
     * @param val double The value to be rounded
     * @param tope double The maximum and minimum values allowed
     */            
        public double Assign (double val, double tope) {
                if ((val > -1E-4) && (val < 1E-4))  return (0);
                if ((val > tope-1E-4) && (val < tope+1E-4))  return (tope);

                return (val);
        }


    /**
     * <p>
     * Generates the semantics of the linguistic variables with triangular fuzzy sets and the mutation intervals to mutate
     * </p>
     */        
        public void Semantic () {
                int var, etq;
                double marca, valor;
                double [] punto = new double[3];
                double [] punto_medio = new double[2];

                /* we generate the fuzzy partitions of the variables */
                for (var=0; var<n_variables; var++) {
                        marca = (extremos[var].max-extremos[var].min) / ((double)n_etiquetas[var]-1);
                        for (etq=0; etq<n_etiquetas[var]; etq++) {
                                valor = extremos[var].min+marca*(etq-1);
                                BaseDatos[var][etq].x0 = Assign (valor,extremos[var].max);
                                valor = extremos[var].min + marca * etq;
                                BaseDatos[var][etq].x1 = Assign (valor,extremos[var].max);
                                BaseDatos[var][etq].x2 = BaseDatos[var][etq].x1;
                                valor = extremos[var].min + marca * (etq + 1);
                                BaseDatos[var][etq].x3 = Assign (valor,extremos[var].max);
                                BaseDatos[var][etq].y = 1;
                                BaseDatos[var][etq].Nombre = "V" + (var+1);
                                BaseDatos[var][etq].Etiqueta = "L" + (etq+1);
                        }
                }

                /* we generate the mutation intervals for each gene */
                for (var=0; var < n_variables; var++) {
                        for (etq=0; etq < n_etiquetas[var]; etq++) {
                                punto[0] = BaseDatos[var][etq].x0;
                                punto[1] = BaseDatos[var][etq].x1;
                                punto[2] = BaseDatos[var][etq].x3;
                                punto_medio[0] = (punto[1] - punto[0]) / 2.0;
                                punto_medio[1] = (punto[2] - punto[1]) / 2.0;
                                intervalos[var][etq].min = punto[0] - punto_medio[0];
                                intervalos[var][etq].max = punto[2] + punto_medio[1];
                        }
                }
        }

}

