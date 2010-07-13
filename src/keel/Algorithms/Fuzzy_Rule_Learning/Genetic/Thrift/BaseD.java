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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Thrift;

class BaseD {

    Difuso[][] BaseDatos;
    int n_variables, n_var_estado;
    int[] n_etiquetas;
    //int n_etiquetas;

    public TipoIntervalo[] extremos;
    //public TipoIntervalo[][] intervalos;


    public BaseD(int MaxEtiquetas, int n_var, double[][] _extremos) {
        int i, j;

        n_variables = n_var;
        n_var_estado = n_variables - 1;

        //intervalos = new TipoIntervalo[n_variables][MaxEtiquetas];
        BaseDatos = new Difuso[n_variables][MaxEtiquetas];

        for (i = 0; i < n_variables; i++) {
            BaseDatos[i] = new Difuso[MaxEtiquetas];
            //intervalos[i] = new TipoIntervalo[MaxEtiquetas];
            for (j = 0; j < MaxEtiquetas; j++) {
                BaseDatos[i][j] = new Difuso();
                //intervalos[i][j] = new TipoIntervalo();
            }
        }

        n_etiquetas = new int[n_variables];

        extremos = new TipoIntervalo[n_variables];
        for (i = 0; i < n_variables; i++) {
            extremos[i] = new TipoIntervalo();
            extremos[i].min = _extremos[i][0];
            extremos[i].max = _extremos[i][1];
            n_etiquetas[i] = MaxEtiquetas;
        }
    }


    /**
     * Rounds the generated value for the semantics
     * @param val valor a asignar
     * @param tope tope valor maximo
     * @return 0 si es muy pequeño, tope si es cercano a éste y "valor" en otro caso
     */
    public double Asigna(double val, double tope) {
        if ((val > -1E-4) && (val < 1E-4)) {
            return (0);
        }
        if ((val > tope - 1E-4) && (val < tope + 1E-4)) {
            return (tope);
        }

        return (val);
    }


    /** Generates the semantics of the linguistic variables with triangular fuzzy sets and the mutation intervals to mutate */
    public void Semantica() {
        int var, etq;
        double marca, valor;
        //double[] punto = new double[3];
        //double[] punto_medio = new double[2];

        /* we generate the fuzzy partitions of the variables */
        for (var = 0; var < n_variables; var++) {
            marca = (extremos[var].max - extremos[var].min) /
                    ((double) n_etiquetas[var] - 1);
            for (etq = 0; etq < n_etiquetas[var]; etq++) {
                valor = extremos[var].min + marca * (etq - 1);
                BaseDatos[var][etq].x0 = Asigna(valor, extremos[var].max);
                valor = extremos[var].min + marca * etq;
                BaseDatos[var][etq].x1 = Asigna(valor, extremos[var].max);
                BaseDatos[var][etq].x2 = BaseDatos[var][etq].x1;
                valor = extremos[var].min + marca * (etq + 1);
                BaseDatos[var][etq].x3 = Asigna(valor, extremos[var].max);
                BaseDatos[var][etq].y = 1;
                BaseDatos[var][etq].Nombre = "V" + (var + 1);
                BaseDatos[var][etq].Etiqueta = "E" + (etq + 1);
            }
        }
    }

    public int getnLabels(int i) {
        return this.n_etiquetas[i];
    }

    public Difuso getParticion(int i, int j) {
        return (BaseDatos[i][j]).copia();
    }

    public double AntecedenteCubreEjemplo(int[] AntRegla, double[] ejem)
    /* Calcula el grado de compatibilidad (Ri(ek)) de los antecedentes de la regla
       con el ejemplo */
    {
        int i;
        double[] grado_pertenencia;
        double min;

        grado_pertenencia = new double[n_var_estado];

        for (i = 0; i < n_var_estado; i++) {
            grado_pertenencia[i] = BaseR.Fuzzifica(ejem[i],
                    getParticion(i, AntRegla[i]));
        }

        min = 1.0;
        for (i = 0; i < n_var_estado; i++) {
            if (grado_pertenencia[i] < min) {
                min = grado_pertenencia[i];
            }
        }

        return (min);
    }

    public double getExtremoInf(int var) {
        return extremos[var].min;
    }

    public double getExtremoSup(int var) {
        return extremos[var].max;
    }

    public String printString() {
        String cadena = new String("");
        for (int i = 0; i < n_variables; i++) {
            cadena += "\nVariable " + (i + 1) + ":\n";
            for (int j = 0; j < n_etiquetas[i]; j++) {
                cadena += " Etiqueta " + (j + 1) + ": (" + BaseDatos[i][j].x0 +
                        "," + BaseDatos[i][j].x1 + "," + BaseDatos[i][j].x3 +
                        ")\n";
            }
        }
        return cadena;

    }


}

