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

class BaseR {

    public Difuso[][] BaseReglas;
    public int n_reglas, n_genes;

    public double[] GradoEmp;
    public Difuso[] Consecuentes;

    public double[][] ListaTabu;

    public MiDataset tabla;
    public BaseD base_datos;

    public BaseR(int MaxReglas, BaseD base, MiDataset t) {
        int i, j;

        tabla = t;
        base_datos = base;

        n_reglas = 0;
        n_genes = tabla.n_variables * 4;

        BaseReglas = new Difuso[MaxReglas][tabla.n_variables];

        Consecuentes = new Difuso[MaxReglas];

        for (i = 0; i < MaxReglas; i++) {
            BaseReglas[i] = new Difuso[tabla.n_variables];
            Consecuentes[i] = new Difuso();

            for (j = 0; j < tabla.n_variables; j++) {
                BaseReglas[i][j] = new Difuso();
            }
        }

        GradoEmp = new double[MaxReglas];

        ListaTabu = new double[MaxReglas][tabla.n_variables];
    }


/* -------------------------------------------------------------------------
						  Fuzzification Interface  
  ------------------------------------------------------------------------- */

    public double Fuzzifica(double X, Difuso D) {
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

	/* T-norma Minimal */
    public void Min(double[] entradas) {
        int b, b2;
        double minimo, y;

        for (b = 0; b < n_reglas; b++) {
            minimo = Fuzzifica(entradas[0], BaseReglas[b][0]);

            for (b2 = 1; b2 < tabla.n_var_estado; b2++) {
                y = Fuzzifica(entradas[b2], BaseReglas[b][b2]);
                if (y < minimo) {
                    minimo = y;
                }
            }

            GradoEmp[b] = minimo;
        }
    }


/* -------------------------------------------------------------------------
							Implication Operator 
  ------------------------------------------------------------------------- */

    public void T_Min() {
        int b;

        for (b = 0; b < n_reglas; b++) {
            if (GradoEmp[b] != 0) {
                if (GradoEmp[b] == 1.0) {
                    Consecuentes[b].x0 = BaseReglas[b][tabla.n_variables -
                                         1].x0;
                    Consecuentes[b].x1 = BaseReglas[b][tabla.n_variables -
                                         1].x1;
                    Consecuentes[b].x2 = BaseReglas[b][tabla.n_variables -
                                         1].x2;
                    Consecuentes[b].x3 = BaseReglas[b][tabla.n_variables -
                                         1].x3;
                } else {
                    Consecuentes[b].x0 = BaseReglas[b][tabla.n_variables -
                                         1].x0;
                    Consecuentes[b].x1 = BaseReglas[b][tabla.n_variables -
                                         1].x0 +
                                         (BaseReglas[b][tabla.n_variables -
                                          1].x1 -
                                          BaseReglas[b][tabla.n_variables -
                                          1].x0) * GradoEmp[b];
                    Consecuentes[b].x2 = BaseReglas[b][tabla.n_variables -
                                         1].x3 +
                                         (BaseReglas[b][tabla.n_variables -
                                          1].x2 -
                                          BaseReglas[b][tabla.n_variables -
                                          1].x3) * GradoEmp[b];
                    Consecuentes[b].x3 = BaseReglas[b][tabla.n_variables -
                                         1].x3;
                }
            }

            Consecuentes[b].y = GradoEmp[b];
        }
    }


/* -------------------------------------------------------------------------
							Defuzzification Interface 
  ------------------------------------------------------------------------- */

	/** Functions to calculate the centre of gravity */
    public double AreaTrapecioX(double x0, double x1, double x2, double x3,
                                double y) {
        double izq, centro, der;

        if (x1 != x0) {
            izq = (2 * x1 * x1 * x1 - 3 * x0 * x1 * x1 + x0 * x0 * x0) /
                  (6 * (x1 - x0));
        } else {
            izq = 0;
        }

        centro = (x2 * x2 - x1 * x1) / 2.0;

        if (x3 != x2) {
            der = (2 * x2 * x2 * x2 - 3 * x3 * x2 * x2 + x3 * x3 * x3) /
                  (6 * (x3 - x2));
        } else {
            der = 0;
        }

        return (y * (izq + centro + der));
    }


    public double AreaTrapecio(double x0, double x1, double x2, double x3,
                               double y) {
        double izq, centro, der;

        if (x1 != x0) {
            izq = (x1 * x1 - 2 * x0 * x1 + x0 * x0) / (2 * (x1 - x0));
        } else {
            izq = 0;
        }

        centro = x2 - x1;

        if (x3 != x2) {
            der = (x3 * x3 - 2 * x3 * x2 + x2 * x2) / (2 * (x3 - x2));
        } else {
            der = 0;
        }

        return (y * (izq + centro + der));
    }


    // Returns the centre of gravity weight by matching
    public double WECOA() {
        double num, den;
        int i;

        num = 0;
        den = 0;
        for (i = 0; i < n_reglas; i++) {
            if (Consecuentes[i].y != 0) {
                num += GradoEmp[i] *
                        (AreaTrapecioX(Consecuentes[i].x0, Consecuentes[i].x1,
                                       Consecuentes[i].x2, Consecuentes[i].x3,
                                       Consecuentes[i].y) /
                         AreaTrapecio(Consecuentes[i].x0, Consecuentes[i].x1,
                                      Consecuentes[i].x2, Consecuentes[i].x3,
                                      Consecuentes[i].y));
                den += GradoEmp[i];
            }
        }

        if (den != 0) {
            return (num / den);
        } else {
            return ((base_datos.extremos[tabla.n_var_estado].max -
                     base_datos.extremos[tabla.n_var_estado].min) / 2.0);
        }
    }


    /* -------------------------------------------------------------------------
             Fuzzy Controller
     ------------------------------------------------------------------------- */

    // Returns the ouput of the controller
    public double FLC(double[] Entrada) {
        Min(Entrada);
        T_Min();
        return (WECOA());
    }


    /* -------------------------------------------------------------------------
             Funciones
     ------------------------------------------------------------------------- */

    // Decodes the coded RB in "Conjunto_Reglas"
    public void decodifica(TipoRegla[] Conjunto_Reglas) {
        int i, j;

        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_variables; j++) {
                BaseReglas[i][j].x0 = base_datos.BaseDatos[j][Conjunto_Reglas[i].
                                      Regla[j]].x0;
                BaseReglas[i][j].x1 = base_datos.BaseDatos[j][Conjunto_Reglas[i].
                                      Regla[j]].x1;
                BaseReglas[i][j].x2 = base_datos.BaseDatos[j][Conjunto_Reglas[i].
                                      Regla[j]].x2;
                BaseReglas[i][j].x3 = base_datos.BaseDatos[j][Conjunto_Reglas[i].
                                      Regla[j]].x3;
                BaseReglas[i][j].y = 1.0;
            }
        }
    }


    // RB to String
    public String BRtoString(int peso) {
        int i, j;
        String cadena = "";

        cadena += "Numero de reglas: " + n_reglas + "\n\n";
        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_variables; j++) {
                cadena += "" + BaseReglas[i][j].x0 + " " + BaseReglas[i][j].x1 +
                        " " + BaseReglas[i][j].x3 + "\n";
            }

            if (peso > 0) {
                cadena += "1.0" + "\n";
            }

            cadena += "\n";
        }

        return (cadena);
    }
}

