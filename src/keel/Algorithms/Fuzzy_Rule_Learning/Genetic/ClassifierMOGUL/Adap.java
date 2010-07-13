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

import java.util.ArrayList;

class Adap {
/**	
 * <p>
 * Contains the functions for calculate the matching degree, ... between the rules and the examples
 * </p>
 */

    public static double omega, K;
    public double[] grado_pertenencia;
    public double[] puntos;
    public double F, G, g, PC;
    public int EmparejaAnt;
    public double ClaTra, ClaTst;
    public int tipo_fitness, tipo_reglas, compa;

    public MyDataset tabla;
    public RuleBase base_reglas;
    public T_FRM frm;


    /**
     * <p>
     * Constructor
     * </p>
     * @param training MyDataset The set of examples
     * @param base RuleBase The Rule Base
     * @param FRM T_FRM The FRM used
     * @param tipo int The type of fuzzy rule
     * @param compatibilidad The association degree function
     */  
    public Adap(MyDataset training, RuleBase base, T_FRM FRM, int tipo,
                int compatibilidad) {
        tabla = training;
        base_reglas = base;
        tipo_reglas = tipo;
        frm = FRM;
        compa = compatibilidad;

        puntos = new double[tabla.n_variables];
        grado_pertenencia = new double[tabla.n_variables];
    }

	/**
	 * <p>
	 * Returns the minimum of two values
	 * </p>
	 * @param x double The first value
	 * @param y double The second value
	 * @return The winner value
	 */
    public static double Minimum(double x, double y) {
        if (x < y) {
            return (x);
        } else {
            return (y);
        }
    }


	/**
	 * <p>
	 * Returns the maximum of two values
	 * </p>
	 * @param x double The first value
	 * @param y double The second value
	 * @return The winner value
	 */
    public static double Maximum(double x, double y) {
        if (x > y) {
            return (x);
        } else {
            return (y);
        }
    }


    /* ------------------------------------------------------------------
       ----------- Obtaining the greatest value of an vector ------------
         If all the vector values are zere, it returns -1.
         Repetido is equal 0 if the greatest value is not repeated and
         it is equal 1 in the other case.
       ------------------------------------------------------------------ */

    private int MayorD(double[] s, int n, ArrayList < int[] > milista) {
        int[] aux1 = new int[1];
        aux1 = milista.get(0);
        int repetido = aux1[0];

        int i, mayor;
        double m;

        repetido = 0;
        mayor = -1;
        m = 0.0;
        for (i = 0; i < n; i++) {
            if (s[i] > m) {
                m = s[i];
                mayor = i;
                repetido = 0;
            } else if (s[i] == m) {
                repetido = 1;
            }
        }

        aux1[0] = repetido;
        milista.add(0, aux1);

        return (mayor);
    }


    /* ---------------------------------------------------------------------
       Obtaining the position of the smallest value of a vector of double numbers. If
       all the element of the vector are equal to zero, it returns -1
       --------------------------------------------------------------------- */

    private int MenorD(double[] s, int n, ArrayList < int[] > milista) {
        int[] aux1 = new int[1];
        aux1 = milista.get(0);
        int repetido = aux1[0];

        int i, menor;
        double m;

        repetido = 0;
        menor = -1;
        m = 10000.0;
        for (i = 0; i < n; i++) {
            if (s[i] < m) {
                m = s[i];
                menor = i;
                repetido = 0;
            } else if (s[i] == m) {
                repetido = 1;
            }
        }

        aux1[0] = repetido;
        milista.add(0, aux1);

        return (menor);
    }


    /* ------------------------------------------------------------------------
       C.A.R. Hoare sorting algorithm.
     ------------------------------------------------------------------------ */


    private void Or(double[] v, int izq, int der) {
        int i, j;
        double x, y;

        i = izq;
        j = der;
        x = v[(izq + der) / 2];
        do {
            while (v[i] > x && i < der) {
                i++;
            } while (x > v[j] && j > izq) {
                j--;
            }
            if (i <= j) {
                y = v[i];
                v[i] = v[j];
                v[j] = y;
                i++;
                j--;
            }
        } while (i <= j);
        if (izq < j) {
            Or(v, izq, j);
        }
        if (i < der) {
            Or(v, i, der);
        }
    }


    private double SumaPonderada(double[] a, double[] w, int n) {
        int i;
        double suma;

        suma = 0.0;
        for (i = 0; i < n; i++) {
            suma = suma + a[i] * w[i];
        }
        return (suma);
    }


    /* -------------------------------------------------------------------------
                                   FITNESS FUNCTION
     ------------------------------------------------------------------------- */

    /* ------------------------- Criteria of rules -------------------------- */

    /**
     * <p>
     * Returns the matching degree of the rule "Ri(ek)" with the instance "ejem"
     * </p>
     * @param indiv Structure The individual enconding the rule
     * @param ejem double[] The given example
     * @return double The matching degree
     */
    public double RuleCoversExample(Structure indiv, double[] ejem) {
        int i, pos_individuo;
        double minimo_ant, minimo_con;
        FuzzySet D = new FuzzySet();

        EmparejaAnt = 0;

        for (i = 0; i < tabla.n_inputs; i++) {
            pos_individuo = tabla.n_inputs + 3 * i;
            D.x0 = indiv.Gene[pos_individuo];
            D.x1 = indiv.Gene[pos_individuo + 1];
            D.x2 = indiv.Gene[pos_individuo + 1];
            D.x3 = indiv.Gene[pos_individuo + 2];
            D.y = 1;
            grado_pertenencia[i] = base_reglas.Fuzzification(ejem[i], D);
        }

        minimo_ant = 1;
        minimo_con = 1;
        for (i = 0; i < tabla.n_inputs; i++) {
            if (grado_pertenencia[i] < minimo_ant) {
                minimo_ant = grado_pertenencia[i];
            }
        }

        if (minimo_ant > 0) {
            EmparejaAnt = 1;
        }

        switch (tipo_reglas) {
        case 1:
        case 2:
            if (indiv.Consecuente[0].clase == ejem[tabla.n_inputs]) {
                minimo_con = indiv.Consecuente[0].gcerteza;
            } else {
                minimo_con = 0.0;
            }
            break;
        case 3:
            minimo_con = indiv.Consecuente[(int) ejem[tabla.n_inputs]].gcerteza;
            break;
        }
        return (minimo_ant * minimo_con);
    }


    /**
     * <p>
     * Calcules the rule's criteria by the rule "cromosoma":
     *    - High frequency values [Her95]
     *    - High average covering degree over positive examples [Her95]
     *    - Small negative example set [Gon95]
     * </p>
     * @param indiv Structure The invididual encoding the rule
     */
    public void RulesCriteria(Structure indiv) {
        int i, n_ejem_pos, n_ejem_neg, n_ejem_ya_cub;
        double RCE, frec_acumulada, SumaRCEpositivos, umbral;

        n_ejem_pos = n_ejem_neg = 0;
        frec_acumulada = SumaRCEpositivos = 0.0;

        /* we count the number of negative and positive examples */
        for (i = 0; i < tabla.long_tabla; i++) {
            RCE = RuleCoversExample(indiv, tabla.datos[i].ejemplo);
            if (tabla.datos[i].cubierto == 0) {
                frec_acumulada += RCE;
                if (RCE >= omega) {
                        /* If the example has a cover higher than omega*/
                    /* then it's considered a positive example */
                    n_ejem_pos++;
                    SumaRCEpositivos += RCE;
                }
            }
            /* If the inputs is covered and the outputs isn't then the example is negative */
            if (RCE == 0.0 && EmparejaAnt > 0) {
                n_ejem_neg++;
            }
        }

        /* Rule's frecuency [Her95] */
        F = frec_acumulada / tabla.no_cubiertos;

        /* Average covering degree over positive examples [Her95] */
        if (n_ejem_pos > 0) {
            G = SumaRCEpositivos / (double) n_ejem_pos;
        } else {
            G = 0.0;
        }

        /* Penalty on the set of negative examples [Gon95] */
        umbral = K * n_ejem_pos;

        /* we don't penalize if the number of negative examples is less or equal than "umbral" */
        if (n_ejem_neg <= umbral) {
            g = 1.0;
        } else {
            g = 1.0 / (n_ejem_neg - umbral + Math.exp(1.0));
        }

        /* If we use the fitness function with penalty by overfitting, we'll calculate this penalty */
        if (tipo_fitness == 2) {
            n_ejem_ya_cub = 0;
            for (i = 0; i < tabla.long_tabla; i++) {
                RCE = RuleCoversExample(indiv, tabla.datos[i].ejemplo);

                /* If the example was covered by another rule it's counted for calculating the penalty */
                if (tabla.datos[i].cubierto > 0 && RCE != 0) {
                    n_ejem_ya_cub++;
                }

                if (n_ejem_ya_cub <= umbral) {
                    PC = 1.0;
                } else {
                    PC = 1.0 / (n_ejem_ya_cub - umbral + Math.exp(1.0));
                }
            }
        }
    }


    /**
     * <p>
     * Calculates the clasification accuracy in training and test data
     * </p>
     * @param training boolean TRUE for training data. FALSE for test data
     * @param tabla MyDataset The set of examples
     */
    public void Clasification_accuracy(boolean training, MyDataset tabla) {
        double clasi = 100.0 * Clasification(tabla);
        if (training == true) {
            ClaTra = clasi;
        } else {
            ClaTst = clasi;
        }
    }


	/**
	 * <p>
	 * Obtains the correct percentaje for the examples in the set "t"
	 * </p>
	 * @param t MyDataset The set of examples
	 * @return double The correct percentage on the data in "t" dataset
	 */
    public double Clasification(MyDataset t) {
        int i, mc, mejor_clase, contador, mejor_regla, num_ejemplos;
        double porcentaje, produc;
        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        ArrayList<double[]> lista2 = new ArrayList<double[]>(1);
        int[] aux1 = new int[2];
        double[] aux2 = new double[1];

        mc = 0;
        contador = 0;
        mejor_clase = 0;
        mejor_regla = 0;
        produc = 0.0;

        for (i = 0; i < t.long_tabla; i++) {
            aux1[0] = mejor_clase;
            aux1[1] = mejor_regla;
            lista1.add(0, aux1);

            aux2[0] = produc;
            lista2.add(0, aux2);

            switch (frm.fagre) {
            case 0:
                BuscarMejorClase1(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 1:
                MejorSumaClases(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 2:
                MejorMedia(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 3:
                BuscarMejorClaseASOWA(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 4:
                BuscarMejorClaseOSOWA(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 5:
                BuscarMejorClaseQUASIARIT(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 6:
                BuscarMejorClaseBADD(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 7:
                BuscarMejorClaseOWA(lista1, lista2, t.datos[i].ejemplo);
                break;
            case 8:
                BuscarMejorClaseQUASIOWA(lista1, lista2, t.datos[i].ejemplo);
            }

            aux1 = lista1.get(0);
            mejor_clase = aux1[0];
            mejor_regla = aux1[1];

            aux2 = lista2.get(0);
            produc = aux2[0];

            /* If the maximum compatibility is greatest that zero, the example is classified. Otherwise, the
               example is considered as not classified */

            if ((produc > 0.0) &&
                (mejor_clase == (int) t.datos[i].ejemplo[t.n_inputs])) {
                contador++;
            }
        }
        porcentaje = ((double) contador) / t.long_tabla;
        return (porcentaje);
    }


	/**
	 * <p>
	 * Obtains the best class for the given example according to the FRM used
	 * </p>
	 * @param t MyDataset The set of examples
	 * @param n_ejemplo int The position of the example in the set of examples
	 * @return int The obtained class
	 */
    public int Clasification(MyDataset t, int n_ejemplo) {
        int mejor_clase, mejor_regla;
        double produc;
        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        ArrayList<double[]> lista2 = new ArrayList<double[]>(1);
        int[] aux1 = new int[2];
        double[] aux2 = new double[1];

        mejor_clase = 0;
        mejor_regla = 0;
        produc = 0.0;

        aux1[0] = mejor_clase;
        aux1[1] = mejor_regla;
        lista1.add(0, aux1);

        aux2[0] = produc;
        lista2.add(0, aux2);

        switch (frm.fagre) {
        case 0:
            BuscarMejorClase1(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 1:
            MejorSumaClases(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 2:
            MejorMedia(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 3:
            BuscarMejorClaseASOWA(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 4:
            BuscarMejorClaseOSOWA(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 5:
            BuscarMejorClaseQUASIARIT(lista1, lista2,
                                      t.datos[n_ejemplo].ejemplo);
            break;
        case 6:
            BuscarMejorClaseBADD(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 7:
            BuscarMejorClaseOWA(lista1, lista2, t.datos[n_ejemplo].ejemplo);
            break;
        case 8:
            BuscarMejorClaseQUASIOWA(lista1, lista2, t.datos[n_ejemplo].ejemplo);
        }

        aux1 = lista1.get(0);
        mejor_clase = aux1[0];
        mejor_regla = aux1[1];

        aux2 = lista2.get(0);
        produc = aux2[0];

        /* If the maximum compatibility is greatest that zero, the example is classified. Otherwise, the
               example is considered as not classified */
        if (produc <= 0.0) {
            mejor_clase = -1;
        }

        return (mejor_clase);
    }


    /* -----------------------------------------------------------------------
       ------------------ FRM 1 (Classic) -------------------
     ----------------------------------------------------------------------- */

    private void BuscarMejorClase1(ArrayList < int[] > milista1,
                           ArrayList < double[] > milista2, double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        FuzzySet[] D = new FuzzySet[tabla.n_inputs];
        for (int i = 0; i < tabla.n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        int r;
        double max, produc, resu, aux2;
        int l, c;

        r = 0;
        max = 0.0;
        c = -1;
        while (r < base_reglas.n_reglas) {
            for (int i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            produc = MatchingDegree(ejemplo, D);
            switch (tipo_reglas) {
            case 2:
                produc = produc * base_reglas.Consecuentes[r][0].gcerteza;
                break;
            case 3:
                c = -1; /* Look for the best class for this rule */
                aux2 = 0.0;
                for (l = 0; l < tabla.nClasses; l++) {
                    resu = produc * base_reglas.Consecuentes[r][l].gcerteza;
                    if (resu > aux2) {
                        aux2 = resu;
                        c = l;
                    }
                }
                produc = aux2;
            }
            if (produc > max) {
                max = produc;
                mejor_regla = r;
                switch (tipo_reglas) {
                case 1:
                case 2:
                    mejor_clase = base_reglas.Consecuentes[r][0].clase;
                    break;
                case 3:
                    mejor_clase = c;
                }
            }
            r++;
        }
        compatib = max;

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* ----------------------------------------------------------------------
       ---------------------- FRM 2 (Normalized Sum) ------------------------
     ----------------------------------------------------------------------- */

    private void MejorSumaClases(ArrayList < int[] > milista1,
                         ArrayList < double[] > milista2, double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        int r;
        double produc, resu;
        int l, c, i, aux;
        double[] suma = new double[tabla.nClasses];

        for (i = 0; i < tabla.nClasses; i++) {
            suma[i] = 0.0;
        }

        /* Calculation of the compatibility degree among the example and the rules of each one of the classes */
        r = 0;
        while (r < base_reglas.n_reglas) {
            for (i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            produc = MatchingDegree(ejemplo, D);
            switch (tipo_reglas) {
            case 1:
                if (produc > 0.0) {
                    suma[base_reglas.Consecuentes[r][0].clase] += produc;
                }
                break;
            case 2:
                produc = produc * base_reglas.Consecuentes[r][0].gcerteza;
                if (produc > 0.0) {
                    suma[base_reglas.Consecuentes[r][0].clase] += produc;
                }
                break;
            case 3:
                for (l = 0; l < tabla.nClasses; l++) {
                    resu = produc * base_reglas.Consecuentes[r][l].gcerteza;
                    if (resu > 0.0) {
                        suma[l] += resu;
                    }
                }
            }
            r++;
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        aux = 0;
        auxiliar[0] = aux;
        lista1.add(0, auxiliar);

        c = MayorD(suma, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        aux = auxiliar[0];

        if (c != -1) {
            mejor_clase = c;
            compatib = suma[c];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* ---------------------------------------------------------------------------
     ----------------------------- FRM 3 (Arithmetic Mean) ----------------------
     --------------------------------------------------------------------------- */

    private void MejorMedia(ArrayList < int[] > milista1,
                    ArrayList < double[] > milista2, double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        int r;
        double produc, resu;
        double[] suma = new double[tabla.nClasses];
        double[] cociente = new double[tabla.nClasses];
        int l, c, i, pos, aux;
        int[] n = new int[tabla.nClasses];

        for (i = 0; i < tabla.nClasses; i++) {
            suma[i] = 0.0;
            n[i] = 0;
        }

        /* Calculation of the compatibility degree among the example and the rules of each one of the classes */
        r = 0;
        while (r < base_reglas.n_reglas) {
            for (i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            produc = MatchingDegree(ejemplo, D);
            switch (tipo_reglas) {
            case 1:
                if (produc > 0.0) {
                    pos = base_reglas.Consecuentes[r][0].clase;
                    suma[pos] += produc;
                    n[pos]++;
                }
                break;
            case 2:
                produc = produc * base_reglas.Consecuentes[r][0].gcerteza;
                if (produc > 0.0) {
                    pos = base_reglas.Consecuentes[r][0].clase;
                    suma[pos] += produc;
                    n[pos]++;
                }
                break;
            case 3:
                for (l = 0; l < tabla.nClasses; l++) {
                    resu = produc * base_reglas.Consecuentes[r][l].gcerteza;
                    if (resu > 0.0) {
                        suma[l] += resu;
                        n[l]++;
                    }
                }
            }
            r++;
        }

        /* Calculation of the mean by classes */
        for (i = 0; i < tabla.nClasses; i++) {
            if (n[i] > 0) {
                cociente[i] = suma[i] / n[i];
            } else {
                cociente[i] = 0.0;
            }
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        aux = 0;
        auxiliar[0] = aux;
        lista1.add(0, auxiliar);

        c = MayorD(cociente, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        aux = auxiliar[0];

        if (c != -1) {
            mejor_clase = c;
            compatib = cociente[c];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* ------------------------------------------------------------------------
       --------------------- FRM 4 (OWA) ---------------------------
     ------------------------------------------------------------------------ */

    private double Q(double i, double a, double b) {

        if (i < a) {
            return (0);
        } else if (i <= b) {
            return ((i - a) / (b - a));
        } else {
            return (1);
        }
    }

    /* ------------------------------------------------------------------------ */

    private double Peso(int i, double a, double b, int n) {
        double resu;

        if (i == 0) {
            resu = Q((i + 1) / ((double) n), a, b);
        } else {
            resu = Q((i + 1) / ((double) n), a, b) - Q(i / ((double) n), a, b);
        }
        return (resu);
    }


    /* ------------------------------------------------------------------------ */

    private void ObtenerPesos(ArrayList < double[] > lista, int contador, double a,
                      double b) {
        double[] w = new double[contador];
        w = lista.get(0);
        int i;

        for (i = 0; i < contador; i++) {
            w[i] = Peso(i, a, b, contador);
        }

        lista.add(0, w);
    }


    /* ------------------------------------------------------------------------ */

    private double CalcularOWA(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu;
        int r, contador, j;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;
        /* Look for the "A" vector for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (int i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }

            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
            }
            r++;
        }

        if (contador != 0) {
            /* Sort "A" vector */
            Or(A, 0, contador - 1);

            ArrayList<double[]> lista1 = new ArrayList<double[]>(1);
            double[] w = new double[contador];
            lista1.add(0, w);

            /* Obtaining the weights */
            ObtenerPesos(lista1, contador, frm.a, frm.b);

            w = lista1.get(0);

            /* Calculating the final value for owa */
            resu = SumaPonderada(A, w, contador);
            return (resu);
        } else {
            return (0);
        }
    }

    /* ------------------------------------------------------------------------ */

    private void BuscarMejorClaseOWA(ArrayList < int[] > milista1,
                             ArrayList < double[] > milista2, double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] owa = new double[tabla.nClasses];
        int i, repetido, pos;

        for (i = 0; i < tabla.nClasses; i++) {
            owa[i] = CalcularOWA(i, ejemplo);
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(owa, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];
        if (pos != -1) {
            mejor_clase = pos;
            compatib = owa[pos];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* -------------------------------------------------------------------------
     ----------------------- FRM 5 (OR-LIKE SOWA) --------------------------
     ------------------------------------------------------------------------- */

    private double CalcularOSOWA(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu, amax, suma;
        int r, contador, j, posicion, repetido;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;
        suma = 0.0;

        /* Look for the set of value to be added for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (int i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
            }
            r++;
        }
        if (contador == 0) {
            return (0);
        } else {
            ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
            int[] auxiliar = new int[1];
            repetido = 0;
            auxiliar[0] = repetido;
            lista1.add(0, auxiliar);

            posicion = MayorD(A, contador, lista1);

            auxiliar = lista1.get(0);
            repetido = auxiliar[0];

            amax = A[posicion];
            resu = (1 - frm.a) * (1 / (double) contador) * suma + frm.a * amax;
            return (resu);
        }
    }


    /* ------------------------------------------------------------------------- */

    private void BuscarMejorClaseOSOWA(ArrayList < int[] > milista1,
                               ArrayList < double[] > milista2,
                               double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] osowa = new double[tabla.nClasses];
        int i, pos, repetido;

        for (i = 0; i < tabla.nClasses; i++) {
            osowa[i] = CalcularOSOWA(i, ejemplo);
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(osowa, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];
        if (pos != -1) {
            mejor_clase = pos;
            compatib = osowa[pos];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* -------------------------------------------------------------------------
     ----------------------- FRM 6 (AND-LIKE SOWA) --------------------------
     ------------------------------------------------------------------------- */

    private double CalcularASOWA(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu, amax, suma;
        int r, contador, j, posicion, repetido;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;
        suma = 0.0;

        /* Look for the set of value to be added for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (int i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
            }
            r++;
        }
        if (contador == 0) {
            return (0);
        } else {
            ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
            int[] auxiliar = new int[1];
            repetido = 0;
            auxiliar[0] = repetido;
            lista1.add(0, auxiliar);

            posicion = MenorD(A, contador, lista1);

            auxiliar = lista1.get(0);
            repetido = auxiliar[0];

            amax = A[posicion];
            resu = (1 - frm.a) * (1 / (double) contador) * suma + frm.a * amax;
            return (resu);
        }
    }


    /* ------------------------------------------------------------------------ */


    private void BuscarMejorClaseASOWA(ArrayList < int[] > milista1,
                               ArrayList < double[] > milista2,
                               double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] asowa = new double[tabla.nClasses];
        int i, pos, repetido;

        for (i = 0; i < tabla.nClasses; i++) {
            asowa[i] = CalcularASOWA(i, ejemplo);
        }
        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(asowa, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];
        if (pos != -1) {
            mejor_clase = pos;
            compatib = asowa[pos];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* -------------------------------------------------------------------------
     ----------------------- FRM 7 (QUASI-OWA) --------------------------
     ------------------------------------------------------------------------- */

    private double CalcularQUASIOWA(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu, amax, suma;
        int r, contador, j, posicion, repetido, i;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;
        suma = 0.0;

        /* Look for the set of value to be added for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma += comp;
                    }
                }
            }
            r++;
        }

        if (contador != 0) {
            /* Sort "A" vector */
            Or(A, 0, contador - 1);

            /* Obtaning the weights */
            ArrayList<double[]> lista1 = new ArrayList<double[]>(1);
            double[] w = new double[contador];
            lista1.add(0, w);

            ObtenerPesos(lista1, contador, frm.a, frm.b);

            w = lista1.get(0);

            /* Calculation of the final value for quasiowa */
            for (i = 0; i < contador; i++) {
                A[i] = Math.pow(A[i], frm.p);
            }
            resu = SumaPonderada(A, w, contador);
            resu = Math.pow(resu, 1 / frm.p);

            return (resu);
        } else {
            return (0);
        }
    }


    /* --------------------------------------------------------------------------- */

    private void BuscarMejorClaseQUASIOWA(ArrayList < int[] > milista1,
                                  ArrayList < double[] > milista2,
                                  double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] quasiowa = new double[tabla.nClasses];
        int i, pos, repetido;

        for (i = 0; i < tabla.nClasses; i++) {
            quasiowa[i] = CalcularQUASIOWA(i, ejemplo);
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(quasiowa, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];

        if (pos != -1) {
            mejor_clase = pos;
            compatib = quasiowa[pos];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* -------------------------------------------------------------------------
     ----------------------- FRM 8 (BADD) --------------------------
     ------------------------------------------------------------------------- */

    private double CalcularBADD(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu, amax, suma1, suma2;
        int r, contador, j, posicion, repetido;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (int i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;
        suma1 = 0.0;
        suma2 = 0.0;

        /* Look for the set of value to be added for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (int i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma1 += Math.pow(comp, frm.p + 1);
                        suma2 += Math.pow(comp, frm.p);
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma1 += Math.pow(comp, frm.p + 1);
                        suma2 += Math.pow(comp, frm.p);
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                        suma1 += Math.pow(comp, frm.p + 1);
                        suma2 += Math.pow(comp, frm.p);
                    }
                }
            }
            r++;
        }
        if (contador == 0) {
            return (0);
        } else {
            resu = suma1 / suma2;
            return (resu);
        }
    }


    /* --------------------------------------------------------------------------- */

    private void BuscarMejorClaseBADD(ArrayList < int[] > milista1,
                              ArrayList < double[] > milista2, double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] badd = new double[tabla.nClasses];
        int i, pos, repetido;

        for (i = 0; i < tabla.nClasses; i++) {
            badd[i] = CalcularBADD(i, ejemplo);
        }

        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(badd, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];

        if (pos != -1) {
            mejor_clase = pos;
            compatib = badd[pos];
            mejor_regla = -2;
        } else {
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /* -------------------------------------------------------------------------
     ----------------------- FRM 9 (Quasiarithmetic Mean) --------------------------
     ------------------------------------------------------------------------- */

    private double CalcularQUASIARIT(int clase, double[] ejemplo) {
        double[] A = new double[base_reglas.n_reglas];
        double comp, resu;
        int r, contador, j, posicion, repetido, i;

        int n_inputs = ejemplo.length - 1;
        FuzzySet[] D = new FuzzySet[n_inputs];
        for (i = 0; i < n_inputs; i++) {
            D[i] = new FuzzySet();
        }

        r = 0;
        contador = 0;

        /* Look for the set of value to be added for the class "clase" */
        while (r < base_reglas.n_reglas) {
            for (i = 0; i < tabla.n_inputs; i++) {
                D[i] = base_reglas.BaseReglas[r][i];
            }
            switch (tipo_reglas) {
            case 1:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
                break;
            case 2:
                if (base_reglas.Consecuentes[r][0].clase == clase) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][0].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
                break;
            case 3:
                if (base_reglas.Consecuentes[r][clase].gcerteza > 0.0) {
                    comp = MatchingDegree(ejemplo, D);
                    comp = comp * base_reglas.Consecuentes[r][clase].gcerteza;
                    if (comp > 0.0) {
                        A[contador] = comp;
                        contador++;
                    }
                }
            }
            r++;
        }

        if (contador == 0) {
            return (0);
        }
        /* Obtaining the weights */
        double[] w = new double[contador];
        for (i = 0; i < contador; i++) {
            w[i] = 1 / ((double) contador);
        }

        /* Calculation of the final value for quasiarithmetic mean */
        for (i = 0; i < contador; i++) {
            A[i] = Math.pow(A[i], frm.p);
        }
        resu = SumaPonderada(A, w, contador);
        resu = Math.pow(resu, 1 / frm.p);

        return (resu);
    }


    /* --------------------------------------------------------------------------- */

    private void BuscarMejorClaseQUASIARIT(ArrayList < int[] > milista1,
                                   ArrayList < double[] > milista2,
                                   double[] ejemplo) {
        int[] auxiliar1 = new int[2];
        double[] auxiliar2 = new double[1];

        auxiliar1 = milista1.get(0);
        int mejor_clase = auxiliar1[0];
        int mejor_regla = auxiliar1[1];

        auxiliar2 = milista2.get(0);
        double compatib = auxiliar2[0];

        double[] quasiarit = new double[tabla.nClasses];
        int i, pos, repetido;

        for (i = 0; i < tabla.nClasses; i++) {
            quasiarit[i] = CalcularQUASIARIT(i, ejemplo);
        }
        ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
        int[] auxiliar = new int[1];
        repetido = 0;
        auxiliar[0] = repetido;
        lista1.add(0, auxiliar);

        pos = MayorD(quasiarit, tabla.nClasses, lista1);

        auxiliar = lista1.get(0);
        repetido = auxiliar[0];

        if (pos != -1) {
            mejor_clase = pos;
            compatib = quasiarit[pos];
            mejor_regla = -2;
        } else { /* example not classified */
            mejor_clase = -1;
            compatib = 0.0;
            mejor_regla = -2;
        }

        auxiliar1[0] = mejor_clase;
        auxiliar1[1] = mejor_regla;
        milista1.add(0, auxiliar1);

        auxiliar2[0] = compatib;
        milista2.add(0, auxiliar2);
    }


    /**
     * <p>
     * Returns the rule's fitness
     * <p>
     * @param indiv Structure The invididual representing the rule
     * @return double The fitness value
     */
    public double eval(Structure indiv) {
        double fitness;

        /* We calculate the fitness through the tree penality criteria */
        RulesCriteria(indiv);
        fitness = F * G * g;

        return (fitness);
    }


	/**
	 * <p>
	 * Returns the matching degree between an examples and the fuzzy sets
	 * </p>
	 * @param ejemplo double [] The given example
	 * @param D FuzzySet[] The fuzzy sets
	 * @return double The matching degree
	 */
    public double MatchingDegree(double[] ejemplo, FuzzySet[] D) {
        double result = 0.0;

        switch (compa) {
        case 0:
            result = CompatibilidadMinimo(ejemplo, D);
            break;
        case 1:
            result = CompatibilidadProducto(ejemplo, D);
            break;
        case 2:
            result = CompatibilidadMedia(ejemplo, D);
            break;
        case 3:
            result = CompatibilidadMedia2(ejemplo, D);
        }

        return (result);
    }


    private double CompatibilidadMinimo(double[] ejemplo, FuzzySet[] D) {
        double minimo, grado_pertenencia;
        int i;

        minimo = 1.0;
        for (i = 0; i < tabla.n_inputs; i++) {
            grado_pertenencia = base_reglas.Fuzzification(ejemplo[i], D[i]);
            if (grado_pertenencia < minimo) {
                minimo = grado_pertenencia;
            }
        }
        return (minimo);
    }


    private double CompatibilidadProducto(double[] ejemplo, FuzzySet[] D) {
        double producto, grado_pertenencia;
        int i;

        producto = 1.0;
        for (i = 0; i < tabla.n_inputs; i++) {
            grado_pertenencia = base_reglas.Fuzzification(ejemplo[i], D[i]);
            producto = producto * grado_pertenencia;
        }
        return (producto);
    }


    private double CompatibilidadMedia(double[] ejemplo, FuzzySet[] D) {
        double media, grado_pertenencia;
        int i;

        media = 0.0;
        for (i = 0; i < tabla.n_inputs; i++) {
            grado_pertenencia = base_reglas.Fuzzification(ejemplo[i], D[i]);
            media += grado_pertenencia;
        }
        media = media / tabla.n_inputs;
        return (media);
    }


    private double CompatibilidadMedia2(double[] ejemplo, FuzzySet[] D) {
        double media, grado_pertenencia;
        int i, etiqueta, nulo;

        nulo = 0;
        media = 0.0;
        for (i = 0; i < tabla.n_inputs; i++) {
            grado_pertenencia = base_reglas.Fuzzification(ejemplo[i], D[i]);
            if (grado_pertenencia == 0.0) {
                nulo = 1;
            }
            media += grado_pertenencia;
        }
        if (nulo == 1) {
            return (0.0);
        } else {
            media = media / tabla.n_inputs;
            return (media);
        }
    }


    /**
     * <p>
     * Returns the data for creating the KEEL output file
     * </p>
     * @param tabla_datos MyDataset The set of examples
     * @return String The data for creatring the KEEL output file
     */
    public String ObligatoryOutputFile(MyDataset tabla_datos) {
        int j;
        int clase;
        String salida;

        salida = "@data\n";
        for (j = 0; j < tabla_datos.long_tabla; j++) {
            clase = Clasification(tabla_datos, j);
            if(clase == -1){
                salida +=
//                    (int) (tabla_datos.datos[j]).ejemplo[tabla_datos.n_inputs]) +
//                    " " + clase + " " + "\n";
                        tabla_datos.getOutputAsString(j) +
                        " ?\n";
            }
            else{
                salida +=
//                    (int) (tabla_datos.datos[j]).ejemplo[tabla_datos.n_inputs]) +
//                    " " + clase + " " + "\n";
                        tabla_datos.getOutputAsString(j) +
                        " " + tabla_datos.getClassAsString(clase) + " " + "\n";
            }
        }

        salida = salida.substring(0, salida.length() - 1);

        return (salida);
    }

}

