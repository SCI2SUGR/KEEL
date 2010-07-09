package keel.Algorithms.RE_SL_Postprocess.Mam2TSK;

import java.io.*;
import org.core.*;
import java.util.*;

class BaseR {

    public Regla[] BaseReglas;
    public int max_reglas;
    public int n_reglas;
    public MiDataset tabla;

    public double[] GradoEmp;


    public BaseR(String fichero, MiDataset datos) {
        int i;

        tabla = datos;
        leer_BR(fichero);

        GradoEmp = new double[n_reglas];
    }


    /** Reads the RB of a input file */
    public void leer_BR(String fichero) {
        int i, j;
        String cadena;

        cadena = Fichero.leeFichero(fichero);

        StringTokenizer sT = new StringTokenizer(cadena, "\n\r\t ", false);
        sT.nextToken();
        sT.nextToken();
        sT.nextToken();

        n_reglas = Integer.parseInt(sT.nextToken());

        BaseReglas = new Regla[n_reglas];
        for (i = 0; i < n_reglas; i++) {
            BaseReglas[i] = new Regla(tabla.n_var_estado, tabla.n_variables);
        }

        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_var_estado; j++) {
                BaseReglas[i].Ant[j].x0 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].x1 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].x2 = BaseReglas[i].Ant[j].x1;
                BaseReglas[i].Ant[j].x3 = Double.parseDouble(sT.nextToken());
                BaseReglas[i].Ant[j].y = 1.0;
            }

            /* We don't store the consequent */
            for (j = 0; j < 3; j++) {
                Double.parseDouble(sT.nextToken());
            }
        }
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
            minimo = Fuzzifica(entradas[0], BaseReglas[b].Ant[0]);

            for (b2 = 1; (minimo != 0.0) && (b2 < tabla.n_var_estado); b2++) {
                y = Fuzzifica(entradas[b2], BaseReglas[b].Ant[b2]);
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

    public double Inferencia_TSK(double[] Entrada) {
        double num, den, salida_regla;
        int i, j;

        num = 0;
        den = 0;
        for (i = 0; i < n_reglas; i++) {
            if (GradoEmp[i] != 0.0) {
                /* we initialize the output to the 'b' value */
                salida_regla = BaseReglas[i].Cons[tabla.n_var_estado];

                for (j = 0; j < tabla.n_var_estado; j++) {
                    salida_regla += BaseReglas[i].Cons[j] * Entrada[j];
                }

                num += GradoEmp[i] * salida_regla;
                den += GradoEmp[i];
            }
        }

        if (den != 0) {
            return (num / den);
        } else {
            return ((tabla.extremos[tabla.n_var_estado].max -
                     tabla.extremos[tabla.n_var_estado].min) / 2.0);
        }
    }


    /* -------------------------------------------------------------------------
             Fuzzy Controller
     ------------------------------------------------------------------------- */

    public double FLC_TSK(double[] Entrada) {
        Min(Entrada);
        return (Inferencia_TSK(Entrada));
    }


    /** RB to String */
    public String BRtoString() {
        int i, j;
        String cadena = "";

        cadena += "Numero de reglas: " + n_reglas + "\n\n";
        for (i = 0; i < n_reglas; i++) {
            for (j = 0; j < tabla.n_var_estado; j++) {
                cadena += "" + BaseReglas[i].Ant[j].x0 + " " +
                        BaseReglas[i].Ant[j].x1 + " " + BaseReglas[i].Ant[j].x3 +
                        "\n";
            }

            for (j = 0; j < tabla.n_variables; j++) {
                cadena += BaseReglas[i].Cons[j] + " ";
            }

            cadena += "\n\n";
        }

        return (cadena);
    }


    /** Inserts the consequent of the rule "regla" in the RB */
    public void inserta_cons(int regla, double[] consecuente) {
        int i;

        /* 'a' values of the consequent */
        for (i = 0; i < tabla.n_var_estado; i++) {
            BaseReglas[regla].Cons[i] = Math.tan(consecuente[i]);
        }

        /* 'b' values of the consequent */
        BaseReglas[regla].Cons[i] = Math.tan(consecuente[tabla.n_var_estado]);
    }

}
