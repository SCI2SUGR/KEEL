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

class BaseR {

    public Difuso[][] BaseReglas;
    //public Structure[] Pob_reglas;
    public int n_reglas, n_genes;
    public int n_var_estado;

    public double[] GradoEmp;
    public Difuso[] Consecuentes;

    //public double[][] ListaTabu;

    public myDataset tabla;
    public BaseD base_datos;

    int pos_gen;

    public BaseR(int n_genes, BaseD base, myDataset t) {
        int i, j;

        tabla = t;
        base_datos = base;

        n_reglas = 0;
        this.n_genes = n_genes;

        /*Pob_reglas = new Structure[tabla.getnData()];
                 for (i = 0; i < tabla.getnData(); i++) {
            Pob_reglas[i] = new Structure(n_genes);
                 }*/

        BaseReglas = new Difuso[n_genes][tabla.getnVars()];

        Consecuentes = new Difuso[n_genes];

        for (i = 0; i < n_genes; i++) {
            BaseReglas[i] = new Difuso[tabla.getnVars()];
            Consecuentes[i] = new Difuso();

            for (j = 0; j < tabla.getnVars(); j++) {
                BaseReglas[i][j] = new Difuso();
            }
        }

        GradoEmp = new double[n_genes];
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

    /* T-norma Minimal */
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
     * @param x0 primer valor del trapecio (esquina izq.)
     * @param x1 segundo valor del trapecio (soporte izq.)
     * @param x2 tercer valor del trapecio (soporte dcho.)
     * @param x3 cuarto valor del trapecio (esquina dcha.)
     * @param y valor y
     * @return el area del trapecio
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
            /*if (this.learnWeights) {
                cadena += BaseReglas[i][j - 1].y;
                         }*/

            cadena += "\n";
        }

        return (cadena);
    }

    /* ---------------------- Decodificacion del cromosoma -------------------- */

    void RecorreAntecedentes (int [] cromosoma,int []Regla_act,
               int pos)
    /* Recorre todas las posibles combinaciones de antecedentes para decodificar
       el cromosoma. El algoritmo seguido esta basado en la tecnica backtracking
       para hacer un recorrido exhaustivo por todas las combinaciones de
       antecedentes posibles. */
    {
       int i;
       //System.err.println("Mira -> "+pos_gen+", "+pos);
       if (pos==n_var_estado) { /* Si la combinacion de antecedentes tiene */
                                /* consecuente asociado, la meto en la BC */
          if (cromosoma[pos_gen]!=base_datos.getnLabels(n_var_estado)) {
             /* Construccion del antecedente */
             for (i=0;i<n_var_estado;i++)
                this.BaseReglas[n_reglas][i]=base_datos.getParticion(i,Regla_act[i]);

             /* Construccion del consecuente */
             BaseReglas[n_reglas][n_var_estado]= base_datos.getParticion(n_var_estado, cromosoma[pos_gen]);

             n_reglas++;
          }
          pos_gen++;
       }
       else
          for (Regla_act[pos]=0; Regla_act[pos]< base_datos.getnLabels(pos); Regla_act[pos]++)
             RecorreAntecedentes(cromosoma,Regla_act,pos+1);
}

    /* Pasa la Base de Conocimiento codificada en el cromosoma a una estructura
       adecuada para inferir */
    public int decodifica(int[] cromosoma) {
        int [] Regla_act = new int[this.tabla.getnVars()];

        n_reglas = 0;
        pos_gen = 0;
        RecorreAntecedentes(cromosoma, Regla_act, 0);

        return n_reglas;
    }

}

