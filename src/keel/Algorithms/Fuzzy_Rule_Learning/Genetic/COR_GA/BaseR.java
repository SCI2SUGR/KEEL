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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.COR_GA;

/**
 * <p>Title: Rule Base</p>
 *
 * <p>Description: It contains the Rule Base</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

class BaseR {

    public Difuso[][] BaseReglas;
    //public Structure[] Pob_reglas;
    public int n_reglas, n_genes;
    public int n_var_estado;

    public double[] GradoEmp;
    public Difuso[] Consecuentes;


    public myDataset tabla;
    public BaseD base_datos;
    boolean learnWeights, deleteRules;

    public BaseR(int MaxReglas, BaseD base, myDataset t, boolean learnWeights,
                 boolean deleteRules) {
        int i, j;

        tabla = t;
        base_datos = base;
        this.learnWeights = learnWeights;
        this.deleteRules = deleteRules;

        n_reglas = 0;
        n_genes = tabla.getnVars() * 4;

        BaseReglas = new Difuso[MaxReglas][tabla.getnVars()];

        Consecuentes = new Difuso[MaxReglas];

        for (i = 0; i < MaxReglas; i++) {
            BaseReglas[i] = new Difuso[tabla.getnVars()];
            Consecuentes[i] = new Difuso();

            for (j = 0; j < tabla.getnVars(); j++) {
                BaseReglas[i][j] = new Difuso();
            }
        }

        GradoEmp = new double[MaxReglas];
        //ListaTabu = new double[MaxReglas][tabla.getnVars()];
        n_var_estado = tabla.getnInputs();
    }


    /* -------------------------------------------------------------------------
            Fuzzification Interface
     ------------------------------------------------------------------------- */

    public static double Fuzzifica(double X, Difuso D) {
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

    /* Minimum T-norm */
    public void Min(double[] entradas) {
        int b, b2;
        double minimo, y;

        for (b = 0; b < n_reglas; b++) {
            minimo = Fuzzifica(entradas[0], BaseReglas[b][0]);

            for (b2 = 1; b2 < tabla.getnInputs(); b2++) {
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
                    Consecuentes[b].x0 = BaseReglas[b][tabla.getnVars() -
                                         1].x0;
                    Consecuentes[b].x1 = BaseReglas[b][tabla.getnVars() -
                                         1].x1;
                    Consecuentes[b].x2 = BaseReglas[b][tabla.getnVars() -
                                         1].x2;
                    Consecuentes[b].x3 = BaseReglas[b][tabla.getnVars() -
                                         1].x3;
                } else {
                    Consecuentes[b].x0 = BaseReglas[b][tabla.getnVars() -
                                         1].x0;
                    Consecuentes[b].x1 = BaseReglas[b][tabla.getnVars() -
                                         1].x0 +
                                         (BaseReglas[b][tabla.getnVars() -
                                          1].x1 -
                                          BaseReglas[b][tabla.getnVars() -
                                          1].x0) * GradoEmp[b];
                    Consecuentes[b].x2 = BaseReglas[b][tabla.getnVars() -
                                         1].x3 +
                                         (BaseReglas[b][tabla.getnVars() -
                                          1].x2 -
                                          BaseReglas[b][tabla.getnVars() -
                                          1].x3) * GradoEmp[b];
                    Consecuentes[b].x3 = BaseReglas[b][tabla.getnVars() -
                                         1].x3;
                }
            }

            Consecuentes[b].y = GradoEmp[b];
        }
    }


    /* -------------------------------------------------------------------------
           Defuzzification Interface
     ------------------------------------------------------------------------- */

    /**
     * Functions to calculate the centre of gravity
     * @param x0 first value of the trapezoid (left corner)
     * @param x1 second value of the trapezoid (left support)
     * @param x2 third value of the trapezoid (right support)
     * @param x3 fourth value of the trapezoid (right corner)
     * @param y y value
     * @return the area of the trapezoid
     */
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


    /** Returns the centre of gravity weight by matching
     * @return Returns the centre of gravity weight by matching
     * */
    public double WECOA() {
        double num, den;
        int i;

        num = 0;
        den = 0;
        for (i = 0; i < n_reglas; i++) {
            if (Consecuentes[i].y != 0) {
                num += (learnWeights ? (BaseReglas[i][n_var_estado].y) : (1.0)) *
                        GradoEmp[i] *
                        (AreaTrapecioX(Consecuentes[i].x0, Consecuentes[i].x1,
                                       Consecuentes[i].x2, Consecuentes[i].x3,
                                       Consecuentes[i].y) /
                         AreaTrapecio(Consecuentes[i].x0, Consecuentes[i].x1,
                                      Consecuentes[i].x2, Consecuentes[i].x3,
                                      Consecuentes[i].y));
                den += (learnWeights ? (BaseReglas[i][n_var_estado].y) : (1.0)) *
                        GradoEmp[i];
            }
        }

        if (den != 0) {
            return (num / den);
        } else {
            return 0.0; // ((base_datos.extremos[tabla.getnInputs()].max -
            //base_datos.extremos[tabla.getnInputs()].min) / 2.0);
        }
    }


    /* -------------------------------------------------------------------------
             Fuzzy Controller
     ------------------------------------------------------------------------- */

    /**
     *  Returns the ouput of the controller
     * @param Entrada Es el ejemplo
     * @param n_reglas Numero de reglas
     * @return the ouput of the controller
     * */
    public double FLC(double[] Entrada, int n_reglas) {
        this.n_reglas = n_reglas;

        Min(Entrada);
        T_Min();
        return (WECOA());
    }


    /** RB to String
     * @return returns a string containing the rule base
     * */
    public String BRtoString() {
        int i, j;
        String cadena = "";

        cadena += "Numero de reglas: " + n_reglas + "\n\n";
        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.getnVars(); j++) {
                cadena += "" + BaseReglas[i][j].x0 + " " + BaseReglas[i][j].x1 +
                        " " + BaseReglas[i][j].x3 + "\n";
            }
            if (this.learnWeights) {
                cadena += BaseReglas[i][j - 1].y;
            }

            cadena += "\n";
        }

        return (cadena);
    }

    public int obtener_BR(int[] consecuentes, Espacio subEspacio) {
        int i, j;

        int n_reg = 0;
        for (i = 0; i < subEspacio.size(); i++) { //n_reglas
            if (consecuentes[i] != -1) {
                /* The rule is stored */
                for (j = 0; j < n_var_estado; j++) {
                    BaseReglas[n_reg][j] = base_datos.getParticion(j,
                            subEspacio.get(i).getAntecedente(j));
                }
                BaseReglas[n_reg][j] = base_datos.getParticion(j,
                        consecuentes[i]);
                n_reg++;
            }
        }
        return n_reg;

    }

    public int obtener_BRP(int[] consecuentes, double[] pesos,
                           Espacio subEspacio) {
        int i, j;
        int n_reg = 0;

        for (i = 0; i < subEspacio.size(); i++) { //n_reglas
            if (pesos[i] == 0.0 && deleteRules) {
                consecuentes[i] = -1;
            }
            if ((consecuentes[i] != -1) && (pesos[i] != 0.0)) {
                /* Se almacena la regla */
                for (j = 0; j < n_var_estado; j++) {
                    BaseReglas[n_reg][j] = base_datos.getParticion(j,
                            subEspacio.get(i).getAntecedente(j));
                }
                BaseReglas[n_reg][j] = base_datos.getParticion(j,
                        consecuentes[i]);
                BaseReglas[n_reg][n_var_estado].y = pesos[i];
                //System.err.println("BR["+n_reg+"]: "+BaseReglas[n_reg][n_var_estado].y);
                n_reg++;
            }
        }
        return n_reg;
    }

}

