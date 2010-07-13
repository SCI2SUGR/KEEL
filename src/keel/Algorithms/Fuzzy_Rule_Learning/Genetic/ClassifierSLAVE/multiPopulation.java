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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierSLAVE;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
import java.util.ArrayList;
import org.core.Randomize;

public class multiPopulation {
/**
 * <p>
 * It creates a multipopulation
 * </p>
 */
	
    int elitismo;
    int n_individuos;
    boolean[] modificado;
    int n_valoracion;
    double[][] valoracion;
    int poblacionesB;
    int poblacionesE;
    int poblacionesR;
    populationBinary[] Pb;
    populationInt[] Pe;
    populationReal[] Pr;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    multiPopulation() {
        elitismo = 0;
        poblacionesB = 0;
        poblacionesE = 0;
        n_individuos = 0;
        modificado = new boolean[0];
        n_valoracion = 0;
        valoracion = new double[0][0];
        Pb = new populationBinary[0];
        Pe = new populationInt[0];
        Pr = new populationReal[0];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param pbin int Number of binary populations
     * @param pent int Number of integer populations     
     * @param preal int Number of real populations          
     * @param rangoe int[] Range for the integer populations
     * @param rangoi int[] Lower range for the real populations
     * @param rangos int[] Upper range for the real populations
     * @param mut double[] Mutation probabilities for all the populations
     * @param cruce double[] Crossover probabilities for all the populations
     * @param eli int Number of best individuals (Elistism)
     * @param n int Number of individuals
     * @param tama int[] Size of the invididuals of each population
     * @param v_val int Number of valorations for each individual
     */
    multiPopulation(int pbin, int pent, int preal, int[] rangoe,
                   double[] rangori, double[] rangors, double[] mut,
                   double[] cruce, int eli, int n, int[] tama, int n_val) {

        elitismo = eli;
        n_individuos = n;
        poblacionesB = pbin;
        poblacionesE = pent;
        poblacionesR = preal;
        if (pbin > 0) {
            Pb = new populationBinary[pbin];
            for (int j = 0; j < pbin; j++) {
                Pb[j] = new populationBinary(mut[j], cruce[j], eli, n, tama[j]);
            }
        }

        if (pent > 0) {
            Pe = new populationInt[pent];
            for (int j = 0; j < pent; j++) {
                Pe[j] = new populationInt(rangoe[j], mut[j + pbin],
                                            cruce[j + pbin], eli, n,
                                            tama[j + pbin]);
            }
        }

        if (preal > 0) {
            Pr = new populationReal[preal];
            for (int j = 0; j < preal; j++) {
                Pr[j] = new populationReal(rangori[j], rangors[j],
                                          mut[j + pbin + pent],
                                          cruce[j + pbin + pent], eli, n,
                                          tama[j + pbin + pent]);
            }
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        n_valoracion = n_val;
        valoracion = new double[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            valoracion[i] = new double[n_valoracion];
            for (int j = 0; j < n_valoracion; j++) {
                valoracion[i][j] = 0;
            }
        }
    }


    /**
     * <p>
     * Create a multipopulation as a copy from another multipopulation
     * </p>
     * @param x multiPopulation The multipopulations used to created the new one
     */
    multiPopulation(multiPopulation x) {

        this.elitismo = x.elitismo;
        this.n_individuos = x.n_individuos;

        this.modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            this.modificado[i] = x.modificado[i];
        }

        this.n_valoracion = x.n_valoracion;
        this.valoracion = new double[n_individuos][];
        for (int i = 0; i < this.n_individuos; i++) {
            this.valoracion[i] = new double[this.n_valoracion];
            for (int j = 0; j < this.n_valoracion; j++) {
                this.valoracion[i][j] = x.valoracion[i][j];
            }
        }

        this.poblacionesB = x.poblacionesB;
        this.poblacionesE = x.poblacionesE;
        this.poblacionesR = x.poblacionesR;
        if (this.poblacionesB > 0) {
            this.Pb = new populationBinary[poblacionesB];
            for (int j = 0; j < poblacionesB; j++) {
                this.Pb[j] = new populationBinary(x.Pb[j]);
            }
        }

        if (this.poblacionesE > 0) {
            this.Pe = new populationInt[poblacionesE];
            for (int j = 0; j < this.poblacionesE; j++) {
                this.Pe[j] = new populationInt(x.Pe[j]);
            }
        }

        if (this.poblacionesR > 0) {
            this.Pr = new populationReal[poblacionesR];
            for (int j = 0; j < this.poblacionesR; j++) {
                this.Pr[j] = new populationReal(x.Pr[j]);
            }
        }

    }


	/**
	 * <p>
	 * It swap the individuals in position "i" and  "j" in all the populations
	 * </p>
	 * @param i int Position of the first individual
	 * @param j int Position of the second individual
	 */
    void Swap(int i, int j) {
        int k = 0;
        operations O = new operations();

        for (k = 0; k < poblacionesB; k++) {
            Pb[k].Swap(i, j);
        }

        for (k = 0; k < poblacionesE; k++) {
            Pe[k].Swap(i, j);
        }

        for (k = 0; k < poblacionesR; k++) {
            Pr[k].Swap(i, j);
        }

        ArrayList<boolean[]> lista2 = new ArrayList<boolean[]>(1);

        boolean[] aux = new boolean[2];
        aux[0] = modificado[i];
        aux[1] = modificado[j];

        lista2.add(aux);

        O.Swap_boolean(lista2);

        aux = lista2.get(0);

        modificado[i] = aux[0];
        modificado[j] = aux[1];

        int longitud = valoracion[i].length;
        double[] p = new double[longitud];
        for (k = 0; k < longitud; k++) {
            p[k] = valoracion[i][k];
        }

        for (k = 0; k < longitud; k++) {
            valoracion[i][k] = valoracion[j][k];
            valoracion[j][k] = p[k];
        }
    }


	/**
	 * <p>
	 * It sorts the individuals in the populations according to their valoration
	 * </p>
	 */
    void Sort() {
        int k;
        for (int i = 0; i < n_individuos - 1; i++) {
            for (int j = n_individuos - 1; j > i; j--) {
                k = 0;
                while (k < 3 && valoracion[j][k] == valoracion[j - 1][k]) {
                    k++;
                }
                if (k < 3 && valoracion[j][k] > valoracion[j - 1][k]) {
                    Swap(j, j - 1);
                }
            }
        }
    }


    private int ClaseIndividuo(int ind) {
        return Pe[0].GetValue(ind, 0);
    }

    private int ValorIndividuo(int population, int ind) {
        return Pe[population].GetValue(ind, 0);
    }


	/**
	 * <p>
	 * Returns the individual "individuo" in the binary population "subpoblacion"
	 * </p>
	 * @param subpoplacion int The number of the binary population
	 * @param individuo int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return char[] The binary individual
	 */
    char[] BinarySubpopulation(int subpoblacion, int individuo,
                                ArrayList<Integer> milista) {
        Integer tama;
        tama = milista.get(0);
        int tam = Pb[subpoblacion].SizeOfIndividual(individuo);
        tama = Integer.valueOf(tam);
        milista.add(0, tama);

        char[] puntero = new char[tam];
        puntero = Pb[subpoblacion].GetIndividual(individuo);
//        char[] puntero = Pb[subpoblacion].GetIndividual(individuo);

        return puntero;
    }

	/**
	 * <p>
	 * Returns the individual "individuo" in the integer population "subpoblacion"
	 * </p>
	 * @param subpoplacion int The number of the integer population
	 * @param individuo int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return int[] The integer individual
	 */
    int[] IntSubpopulation(int subpoblacion, int individuo,
                              ArrayList<Integer> milista) {
        Integer tama;
        tama = milista.get(0);
//        System.out.println("Subpoblacion Entera: "+ subpoblacion);
        int tam = Pe[subpoblacion].SizeOfIndividual(individuo);
        tama = Integer.valueOf(tam);
        milista.add(0, tama);

        int[] puntero = new int[tam];
        puntero = Pe[subpoblacion].GetIndividual(individuo);
//        int[] puntero = Pe[subpoblacion].GetIndividual(individuo);

        return puntero;
    }


	/**
	 * <p>
	 * Returns the individual "individuo" in the real population "subpoblacion"
	 * </p>
	 * @param subpoplacion int The number of the real population
	 * @param individuo int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return double[] The real individual
	 */
    double[] RealSubpopulation(int subpoblacion, int individuo,
                               ArrayList<Integer> milista) {
        Integer tama;
        tama = milista.get(0);
        int tam = Pr[subpoblacion].SizeOfIndividual(individuo);
        tama = Integer.valueOf(tam);
        milista.add(0, tama);

        double[] puntero = new double[tam];
        puntero = Pr[subpoblacion].GetIndividual(individuo);
//        double[] puntero = Pr[subpoblacion].GetIndividual(individuo);

        return puntero;
    }


    private void Sort(int n_clases) {
        int k;
        int[] vector = new int[n_clases];
        double[] maximo1 = new double[n_clases];
        double[] minimo1 = new double[n_clases];
        double[] maximo2 = new double[n_clases];
        double[] maximo3 = new double[n_clases];
        int[] posicion = new int[n_clases];

        for (int i = 0; i < n_clases; i++) {
            vector[i] = 0;
        }

        boolean cambio = true;
        for (int i = 0; i < n_individuos - 1 && cambio; i++) {
            cambio = false;
            for (int j = n_individuos - 1; j > i; j--) {
                k = 0;
                while (k < n_valoracion &&
                       valoracion[j][k] == valoracion[j - 1][k]) {
                    k++;
                }
                if (k < n_valoracion && valoracion[j][k] > valoracion[j - 1][k]) {
                    Swap(j, j - 1);
                    cambio = true;
                }
            }
        }

        // Modify the order to allow that rules belonging all the classes always be in the population
        for (int i = 0; i < n_individuos - 2; i++) {
            if (vector[ClaseIndividuo(i)] == 0) {
                maximo1[ClaseIndividuo(i)] = valoracion[i][0];
                maximo2[ClaseIndividuo(i)] = valoracion[i][1];
                maximo3[ClaseIndividuo(i)] = valoracion[i][2];
                posicion[ClaseIndividuo(i)] = i;
            } else {
                minimo1[ClaseIndividuo(i)] = valoracion[i][0];
            }

            vector[ClaseIndividuo(i)]++;
        }

        for (int i = n_individuos - 2; i < n_individuos; i++) {
            if (vector[ClaseIndividuo(i)] <
                (1.0 * n_individuos / (n_clases + 1.0))) {
                int j = n_individuos - 3;
                while (vector[ClaseIndividuo(j)] <
                       1.0 * n_individuos / (n_clases + 1.0)) {
                    j--;
                }

                vector[ClaseIndividuo(i)]++;
                vector[ClaseIndividuo(j)]--;
                Swap(i, j);
            }
        }

        System.out.print("[" + vector[0] + "(" + maximo1[0] + "," + minimo1[0] +
                         ")");
        for (int i = 1; i < n_clases; i++) {
            System.out.print("," + vector[i] + "(" + maximo1[i] + "," +
                             minimo1[i] + ")");
        }
        System.out.println("]");
    }

    private void Sort(int n_clases, int[] n_examples_per_class) {
        int k;
        int[][] vector = new int[n_clases][];
        vector[0] = new int[n_clases * n_individuos];
        for (int i = 1; i < n_clases; i++) {
            vector[i] = new int[n_clases * n_individuos];
        }

        double[] maximo1 = new double[n_clases];
        double[] minimo1 = new double[n_clases];
        int numero_ejemplos;
        //double vaux[3];

        numero_ejemplos = 0;
        for (int i = 0; i < n_clases; i++) {
            vector[i][0] = 0;
            numero_ejemplos += n_examples_per_class[i];
        }

        boolean cambio = true;
        for (int i = 0; i < n_individuos - 1 && cambio; i++) {
            cambio = false;
            for (int j = n_individuos - 1; j > i; j--) {
                k = 0;
                while (k < n_valoracion &&
                       valoracion[j][k] == valoracion[j - 1][k]) {
                    k++;
                }
                if (k < n_valoracion && valoracion[j][k] > valoracion[j - 1][k]) {
                    Swap(j, j - 1);
                    cambio = true;
                }
            }
        }

        // Modify the order to allow that rules belonging all the classes always be in the population
        double[] MediaPobClase = new double[n_clases];

        for (k = 0; k < n_clases; k++) {
            MediaPobClase[k] = 0;
            maximo1[k] = 0;
            minimo1[k] = 0;
        }

        for (int i = 0; i < n_individuos - 2; i++) {
            k = ClaseIndividuo(i);
            double d;
            if (vector[k][0] == 0) {
                maximo1[k] = valoracion[i][0];
                MediaPobClase[k] = valoracion[i][0];
            } else {
                minimo1[k] = valoracion[i][0];
                MediaPobClase[k] += valoracion[i][0];
            }

            vector[k][vector[k][0] + 1] = i;
            vector[k][0]++;
        }

        /* Rules with the minimum size of the population */
        int[] PobMinClase = new int[n_clases];
        for (k = 0; k < n_clases; k++) {
            if (n_examples_per_class[k] == 0) {
                PobMinClase[k] = 0;
            } else if (n_examples_per_class[k] / (1.0 * numero_ejemplos) < 0.05) {
                PobMinClase[k] = n_individuos / (2 * n_clases);
            } else if (Math.abs(maximo1[k] -
                                (1.0 * MediaPobClase[k] / vector[k][0])) <
                       0.1) {
                PobMinClase[k] = n_individuos / (2 * n_clases);
            } else {
                PobMinClase[k] = n_individuos / (1 + n_clases);
            }
        }

        /* Clean the clases where maxvalor ==minvalor */

        for (k = 0; k < n_clases; k++) {
            if (vector[k][0] > PobMinClase[k] &&
                Math.abs(maximo1[k] - (1.0 * MediaPobClase[k] / vector[k][0])) <
                0.05) {
                for (int t = PobMinClase[k]; t <= vector[k][0]; t++) {
                    valoracion[vector[k][t]][0] = -999999.0;
                    /* Make something to promote the diversity */
                    /* I'm goint to try with a rotation of the binary part */
                    Pb[0].Rotation(vector[k][t]);
                    /* I'm goint to try with a rotation of the real part */
                    Pr[0].Rotation(vector[k][t]);

                }
            }
        }

        /* Order the population */
        //bool
        cambio = true;
        for (int i = 0; i < n_individuos - 1 && cambio; i++) {
            cambio = false;
            for (int j = n_individuos - 1; j > i; j--) {
                k = 0;
                while (k < n_valoracion &&
                       valoracion[j][k] == valoracion[j - 1][k]) {
                    k++;
                }
                if (k < n_valoracion && valoracion[j][k] > valoracion[j - 1][k]) {
                    Swap(j, j - 1);
                    cambio = true;
                }
            }
        }

        /* Select the individual to be replace */
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            k = ClaseIndividuo(i);
            if (vector[k][0] < PobMinClase[k]) {
                int j = n_individuos - 3;
                while (vector[ClaseIndividuo(j)][0] <
                       PobMinClase[ClaseIndividuo(j)]) {
                    j--;
                }

                vector[ClaseIndividuo(i)][vector[ClaseIndividuo(i)][0] +
                        1] = vector[ClaseIndividuo(j)][vector[ClaseIndividuo(j)][
                             0] + 1];
                vector[ClaseIndividuo(i)][0]++;
                vector[ClaseIndividuo(j)][0]--;
                Swap(i, j);
            }
        }

        /*cout <<"[" << vector[0][0] << "(" << maximo1[0] << "," << minimo1[0] <<")";
               for (int i=1; i<n_clases; i++)
         cout << "," <<vector[i][0]<< "(" << maximo1[i] << "," << minimo1[i] <<")";
               cout <<"]" << endl; */
    }



    private double Acierto(double[] conjunto, int n_examples) {
        double aciertos = 0;
        for (int i = 0; i < n_examples; i++) {
            if (conjunto[i] > 0) {
                aciertos++;
            }
        }
        return aciertos / n_examples;
    }


    private boolean Mayor(double[] v1, double[] v2, int n) {
        int k = 0;
        while (k < n && v1[k] == v2[k]) {
            k++;
        }

        if (k == n) {
            return false;
        } else {
            return v1[k] > v2[k];
        }
    }


    private void PrintDefinition(int i) {
        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].PrintDefinition(i);
        }

        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].PrintDefinition(i);
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].PrintDefinition(i);
        }

    }

    private void PrintDefinition() {
        for (int i = 0; i < n_individuos; i++) {
            PrintDefinition(i);
            PrintFitness(i);
        }
    }


    private double MediaFitness() {
        double media = 0;
        for (int i = 0; i < n_individuos; i++) {
            media += valoracion[i][0];
        }
        return media / n_individuos;
    }


    private double MediaFitness_Estacionario() {
        double media = 0;
        for (int i = 0; i < n_individuos - 2; i++) {
            media += valoracion[i][0];
        }
        return media / (n_individuos - 2);
    }




    private void PrintFitness(int i) {
        System.out.print("Fitness: ");
        for (int j = 0; j < n_valoracion; j++) {
            System.out.print(valoracion[i][j] + " ");
        }
        System.out.print("[" + MediaFitness() + "]");
        System.out.print("");
    }


	/**
	 * <p>
	 * It prints the steady state fitness for the individual "i"
	 * </p>
	 * @param i int The position of the individual
	 */
    public void Print_SteadyState_Fitness(int i) {
        double minf0, minf1, minf2, maxf0, maxf1, maxf2;
        System.out.print("Fitness: ");
        for (int j = 0; j < n_valoracion; j++) {
            System.out.print(valoracion[i][j] + "   ");
        }
        //cout << "[" << MediaFitness_Estacionario(minf0,maxf0,minf1,maxf1,minf2,maxf2) << "]";
        //cout << "(" << minf0 << "," << maxf0 <<") , ("<<minf1 <<","<<maxf1<<") , ("<<minf2 << "," << maxf2 <<") ";
        System.out.print("Class => " + ClaseIndividuo(i));
        System.out.println("");
    }


    private void PintaIndividuo(int i) {
        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].PrintBin(i);
        }
        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].PrintDefinition(i);
        }
        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].PrintDefinition(i);
        }
        System.out.print("Fitness: ");
        for (int j = 0; j < n_valoracion; j++) {
            System.out.print(valoracion[i][j] + " ");
        }
        System.out.println("");

    }


	/**
	 * <p>
	 * Returns the number of individuals
	 * </p>
	 * @return int The number of individuals
	 */
    int N_individuals() {
        return n_individuos;
    }

    private int Elite() {
        return elitismo;
    }

	/**
	 * <p>
	 * Returns the valoration choosed
	 * </p>
	 * @return int The valoration choosed
	 */
    int N_Valoracion() {
        return n_valoracion;
    }


	/**
	 * <p>
	 * Transforms the population in position "i" to a genetcode Object
	 * </p>
	 * @param i The position of the population
	 * @param code genetcode The genetcode Object
	 */
    void Code(int i, genetcode code) {
        // Binary part
        char[][] bin = new char[poblacionesB][];
        for (int j = 0; j < poblacionesB; j++) {
            bin[j] = new char[0];
        }
        int[] tb = new int[poblacionesB];
        for (int j = 0; j < poblacionesB; j++) {
            ArrayList<char[]> lista1 = new ArrayList<char[]>(1);
            lista1.add(bin[j]);

            ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
            Integer aux = new Integer(tb[i]);

            lista2.add(aux);

            Pb[j].Code(i, lista1, lista2);

            bin[j] = lista1.get(0);

            aux = lista2.get(0);
            tb[j] = aux.intValue();
        }

        code.PutBinary(poblacionesB, tb, bin);

        // Integer part
        int[][] ent = new int[poblacionesE][];
        for (int j = 0; j < poblacionesE; j++) {
            ent[j] = new int[0];
        }
        int[] te = new int[poblacionesE];
        for (int j = 0; j < poblacionesE; j++) {
            ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
            lista1.add(ent[j]);

            ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
            Integer aux = new Integer(te[i]);

            lista2.add(aux);

            Pe[j].Code(i, lista1, lista2);

            ent[j] = lista1.get(0);

            aux = lista2.get(0);
            te[j] = aux.intValue();
        }

        code.PutInteger(poblacionesE, te, ent);

        // Real part
        double[][] rea = new double[poblacionesR][];
        for (int j = 0; j < poblacionesR; j++) {
            rea[j] = new double[0];
        }
        int[] tr = new int[poblacionesR];
        for (int j = 0; j < poblacionesR; j++) {
            ArrayList<double[]> lista1 = new ArrayList<double[]>(1);
            lista1.add(rea[j]);

            ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
            Integer aux = new Integer(tr[i]);

            lista2.add(aux);

            Pr[j].Code(i, lista1, lista2);

            rea[j] = lista1.get(0);

            aux = lista2.get(0);
            tr[j] = aux.intValue();
        }

        code.PutReal(poblacionesR, tr, rea);
    }


    private void PutCodigo(int i, genetcode code) {
        // Binary part
        for (int j = 0; j < poblacionesB; j++) {
            for (int k = 0; k < code.SizeBinary(j); k++) {
                Pb[j].PutValue(i, k, code.GetValueBinary(j, k));
            }
        }

        // Integer part
        for (int j = 0; j < poblacionesE; j++) {
            for (int k = 0; k < code.SizeInteger(j); k++) {
                Pe[j].PutValue(i, k, code.GetValueInteger(j, k));
            }
        }

        // Real part
        for (int j = 0; j < poblacionesR; j++) {
            for (int k = 0; k < code.SizeReal(j); k++) {
                Pr[j].PutValue(i, k, code.GetValueReal(j, k));
            }
        }

    }


	/**
	 * <p>
	 * Checks it the invididual in position "i" has been modified
	 * </p>
	 * @return TRUE if the invididual has been modified. FALSE otherwise
	 */
    boolean Modified(int i) {
        return modificado[i];
    }

    private void PutModificado(int i) {
        modificado[i] = true;
    }

	/**
	 * <p>
	 * Sets the valoration of the individual in position "i" to the values in "valor"
	 * </p>
	 * @param i int The position of the individual
	 * @param valor double[] The valorations to be assigned to the individual
	 */
    void Valoration(int i, double[] valor) {
        for (int j = 0; j < n_valoracion; j++) {
            valoracion[i][j] = valor[j];
        }
        modificado[i] = false;
    }



    private void RandomInitialPopulation() {
        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].RandomInitialPopulation();
        }

        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].RandomInitialPopulation();
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].RandomInitialPopulation();
        }

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }


	/**
	 * <p>
	 * Initializes all the populations
	 * </p>
	 * @param I double[][] The intervals
	 * @param rango int The rank
	 */
    void RandomInitialPopulation(double[][] I, int rango) {

        Pb[0].RandomInitialPopulation();

        Pe[0].RandomInitialPopulation();

        Pr[0].RandomInitialPopulation(I, rango);
        for (int j = 1; j < poblacionesR; j++) {
            Pr[j].RandomInitialPopulation();
        }

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }


	/**
	 * <p>
	 * Initializes all the populations
	 * </p>
	 * @param I double[][] The intervals
	 * @param rango int The rank
	 * @param n_items int The number of items
	 * @param consecuente int The consequent
	 */
    void RandomInitialPopulation(double[][] I, int rango, int n_items,
                                   int consecuente) {
        // Value level
        Pb[0].RandomInitialPopulation();

        // Consequent
        Pe[0].RandomInitialPopulation(consecuente);
        // Dominance
        Pe[1].RandomInitialPopulation(0);

        // Variable level
        Pr[0].RandomInitialPopulation(I, rango);
        // Set of covered examples
        Pr[1].RandomInitialPopulation(n_items);

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }


    private void PoblacionInicialAleatoria_4L(double[][] I, int rango, int n_items) {

        // Value level
        Pb[0].RandomInitialPopulation();

        // Consequent
        Pe[0].RandomInitialPopulation();
        // Dominance
        Pe[1].RandomInitialPopulation(0);

        // Variable level
        Pr[0].RandomInitialPopulation(I, rango);
        // Set of covered examples
        Pr[1].RandomInitialPopulation(n_items);

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }

    private void PoblacionInicialAleatoria_4L(int rango, int n_items) {

        // Value level
        Pb[0].RandomInitialPopulation();

        // Consequent
        Pe[0].RandomInitialPopulation();
        // Dominance
        Pe[1].RandomInitialPopulation(0);

        // Variable level
        Pr[0].RandomInitialPopulation(0, rango);
        // Set of covered examples
        Pr[1].RandomInitialPopulation(n_items);

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }


    private void RandomInitialPopulation(int consecuente) {
        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].RandomInitialPopulation();
        }

        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].RandomInitialPopulation(consecuente);
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].RandomInitialPopulation();
        }

        for (int j = 0; j < n_individuos; j++) {
            modificado[j] = true;
        }

        for (int j = 0; j < n_individuos; j++) {
            for (int k = 0; k < n_valoracion; k++) {
                valoracion[j][k] = 0;
            }
        }

    }


	/**
	 * <p>
	 * Uniform mutation operator for the populations
	 * </p>
	 */
    void UniformMutation() {

        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }


    private void MutacionUniformeModEnt(double prob0) {

        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesE; j++) {
            if (j < 1) {
                Pe[j].UniformMutation();
            } else {
                Pe[j].ModUniformMutation(prob0);
            }
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }


    private void UniformCrossover() {

        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }
        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }


	/**
	 * <p>
	 * Two points crossover operator for the populations
	 * </p>
	 */
    void TwoPointsCrossover() {
        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].TwoPointsCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].TwoPointsCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].TwoPointsCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }

	/**
	 * <p>
	 * Steady state Uniform mutation operator for the populations
	 * </p>
	 */
    void SteadyState_UniformMutation() {

        Pb[0].SteadyState_UniformMutation();

        //Pe[0][0].SteadyState_UniformMutation();

        Pr[0].SteadyState_UniformMutation();

    }


    private void MutacionUniformeModEnt_Estacionario(double prob0) {

        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].SteadyState_UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesE; j++) {
            if (j > 0) {
                Pe[j].SteadyState_ModUniformMutation(prob0);
            } else {
                Pe[j].SteadyState_UniformMutation();
            }
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].SteadyState_UniformMutation();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }


    private void SteadyState_UniformCrossover() {

        for (int j = 0; j < poblacionesB; j++) {
            Pb[j].SteadyState_UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pb[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }
        for (int j = 0; j < poblacionesE; j++) {
            Pe[j].SteadyState_UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pe[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

        for (int j = 0; j < poblacionesR; j++) {
            Pr[j].SteadyState_UniformCrossover();
            for (int i = 0; i < n_individuos; i++) {
                if (Pr[j].Modified(i)) {
                    modificado[i] = true;
                }
            }
        }

    }


	/**
	 * <p>
	 * Steady state two points crossover operator for the populations
	 * </p>
	 */
    void SteadyState_TwoPointsCrossover() {
        operations O = new operations();
        int a = O.Select_Random_Individual(n_individuos - 2, -1, 0);
        int b = O.Select_Random_Individual(n_individuos - 2, a, 0);

        Pb[0].SteadyState_TwoPointsCrossover(a, b);
        Pe[0].SteadyState_TwoPointsCrossover(a, b);
        Pr[0].SteadyState_TwoPointsCrossover(a, b);

        modificado[a] = true;
        modificado[b] = true;

    }


	/**
	 * <p>
	 * Steady state logical based crossover operator for the populations
	 * </p>
	 */
    void SteadyState_LogicalBasedCrossover() {
        operations O = new operations();
        int a = O.Select_Random_Individual(n_individuos - 2, -1, 0);
        int b = O.Select_Random_Individual(n_individuos - 2, a, 0);

//cout <<"Cruce de : " << a << " y b: " << b << endl;

        if (O.Probability(1.0)) {
            Pb[0].SteadyState_TwoPointsCrossover(a, b);

            Pe[0].SteadyState_TwoPointsCrossover(a, b);

            Pr[0].SteadyState_TwoPointsCrossover(a, b);
        } else {
            if (ClaseIndividuo(a) == ClaseIndividuo(b)) {
                // If they belong to the same class: AND_OR
                Pb[0].SteadyState_AND_OR_Crossover(a, b);

                Pe[0].SteadyState_TwoPointsCrossover(a, b);

                Pr[0].SteadyState_AND_OR_Crossover(a, b);
            } else {
                // If they belong to the different classes: NAND_NOR
                Pb[0].SteadyState_NAND_NOR_Crossover(a, b);

                Pe[0].SteadyState_TwoPointsCrossover(a, b);

                Pr[0].SteadyState_NAND_NOR_Crossover(a, b);
            }
        }

        modificado[n_individuos - 2] = true;
        modificado[n_individuos - 1] = true;
    }


	/**
	 * <p>
	 * Selection mechanism
	 * </p>
	 */
    void Selection() {
        multiPopulation Paux = new multiPopulation(this);
        double a;
        int j = 0;

        // Elite stay in the current population

        // Pass the rest
        for (int i = elitismo; i < n_individuos; i++) {
            a = Randomize.Rand() % 10 + 1;
            while (a > 0) {
                a = a - (1.0 / (j + 1)) * 0.5;
                j++;
                if (j == n_individuos) {
                    j = 0;
                }
            }

            for (int k = 0; k < poblacionesB; k++) {
                Pb[k].CopyIndividual(i, Paux.Pb[k], j);
            }

            for (int k = 0; k < poblacionesE; k++) {
                Pe[k].CopyIndividual(i, Paux.Pe[k], j);
            }

            for (int k = 0; k < poblacionesR; k++) {
                Pr[k].CopyIndividual(i, Paux.Pr[k], j);
            }

            for (int k = 0; k < n_valoracion; k++) {
                valoracion[i][k] = Paux.valoracion[j][k];
            }
            modificado[i] = Paux.modificado[j];
        }

    }

	/**
	 * <p>
	 * Returns the fitness value for individual in position "i"
	 * </p>
	 * @param i int The position of the individual
	 * @return double The fitness value
	 */
    double FitnessValue(int i) {
        return valoracion[i][0];
    }

	/**
	 * <p>
	 * Returns the "j" fitness value for individual in position "i"
	 * </p>
	 * @param i int The position of the individual
	 * @param j int The type of fitness	 
	 * @return double The fitness value
	 */
    double FitnessValue(int i, int j) {
        return valoracion[i][j];
    }


    private boolean Diferencia(int i, int j, int n, double minimo) {
        double tot = 0;
        int tpi = 0;
        int tpj = 0;
        double[] ci;
        double[] cj;

        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(tpi);
        lista1.add(aux1);

        ci = RealSubpopulation(1, i, lista1);

        aux1 = lista1.get(0);
        tpi = aux1.intValue();

        ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
        Integer aux2 = new Integer(tpj);
        lista2.add(aux2);

        cj = RealSubpopulation(1, j, lista2);

        aux2 = lista2.get(0);
        tpj = aux2.intValue();

        for (int z = 0; z < n && tot <= minimo; z++) {
            if (ci[z] > 0 && cj[z] == 0) {
                tot++;
            }
        }

        return (tot > minimo);
    }

    private void Cubrimiento(int i, ArrayList < double[] > milista, int n_examples) {
        double[] conjunto = new double[n_examples];
        conjunto = milista.get(0);

        int n = 0;
        int m = 0;
        double aux, positivos = 0, negativos = 0;
        double[] regla;

        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista1.add(aux1);

        regla = RealSubpopulation(1, i, lista1);

        aux1 = lista1.get(0);
        n = aux1.intValue();

        int[] dom;

        ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
        Integer aux2 = new Integer(m);
        lista2.add(aux2);

        dom = IntSubpopulation(1, i, lista2);

        aux2 = lista2.get(0);
        m = aux2.intValue();

        double val_old = valoracion[i][0], pos_old = valoracion[i][3],
                neg_old = valoracion[i][4];

        for (int j = 0; j < n_examples; j++) {
            aux = regla[j] * valoracion[i][2];
            if (regla[j] > 0 && conjunto[j] == 0) {
                conjunto[j] = aux;
                positivos = positivos + regla[j];
            } else if (regla[j] < 0 && aux < conjunto[j]) {
                conjunto[j] = aux;
                negativos = negativos - regla[j];
            }
        }

        if (positivos > 0) {
            dom[0] = 1;
            valoracion[i][0] = (positivos + negativos) * valoracion[i][2];
        } else {
            dom[0] = 0;
        }

        milista.add(0, conjunto);
    }


    private void RecalcularFitness(int i, double[] conjunto, int n_examples) {
        int n = 0;
        int m = 0;
        double positivos = 0;
        double negativos = 0;
        double peso = valoracion[i][2];
        double aux;

        double[] regla;

        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista1.add(aux1);

        regla = RealSubpopulation(1, i, lista1);

        aux1 = lista1.get(0);
        n = aux1.intValue();

        int[] dom;

        ArrayList<Integer> lista2 = new ArrayList<Integer>(1);
        Integer aux2 = new Integer(m);
        lista2.add(aux2);

        dom = IntSubpopulation(1, i, lista2);

        aux2 = lista2.get(0);
        m = aux2.intValue();

        for (int j = 0; j < n_examples; j++) {
            aux = regla[j] * peso;
            if (conjunto[j] > 0 && conjunto[j] == aux) {
                positivos = positivos + regla[j];
            } else if (conjunto[j] < 0 && conjunto[j] == aux) {
                negativos = negativos - regla[j];
            }
        }
        if (positivos > 0) {
            dom[0] = 1;
            valoracion[i][0] = (positivos - negativos) * peso;
        } else {
            dom[0] = 0;
        }
    }


    private double Incremento_Acierto(double[] agregado, double[] peso_agregado,
                              double[] adap_individuo, double peso,
                              int n_examples) {
        double aciertos = 0;
        double fallos = 0;
        double acierto_old = 0;
        double fallo_old = 0;

        for (int i = 0; i < n_examples; i++) {
            if (agregado[i] > 0) {
                acierto_old++;
                if (adap_individuo[i] < 0 &&
                    ( -(adap_individuo[i] * peso) > agregado[i] ||
                     ( -(adap_individuo[i] * peso) == agregado[i] &&
                      peso > peso_agregado[i]))) {
                    fallos++;
                }
            } else {
                fallo_old++;
                if (adap_individuo[i] > 0 &&
                    ((adap_individuo[i] * peso) > -agregado[i] ||
                     ((adap_individuo[i] * peso) == -agregado[i]) &&
                     peso > peso_agregado[i])) {
                    aciertos++;
                }
            }
        }

        return acierto_old - fallos + aciertos;
    }


    private double Maximum(double a, double b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    private double Valor_Absoluto(double x) {
        if (x < 0) {
            return -x;
        } else {
            return x;
        }
    }


    private void Combinar(ArrayList < double[] > milista, double peso, int n_examples,
                  int ind) {
        double[] agregado = new double[n_examples];
        double[] peso_agregado = new double[n_examples];
        double[] disparada = new double[n_examples];
        double[] adap_individuo = new double[n_examples];

        agregado = milista.get(0);
        peso_agregado = milista.get(1);
        disparada = milista.get(2);
        adap_individuo = milista.get(3);

        double v_abs1, v_abs2;
        //bool *ha_lanzado = new bool[ind+1];

        // Esto es nuevo para eliminar de la elite las reglas que no aportan
        //for (int i=0; i<=ind; i++)
        //  ha_lanzado[i]=false;

        for (int i = 0; i < n_examples; i++) {
            v_abs1 = Valor_Absoluto(adap_individuo[i] * peso);
            v_abs2 = Valor_Absoluto(agregado[i]);
            if (v_abs1 > v_abs2 || (v_abs1 == v_abs2 && peso > peso_agregado[i])) {
                agregado[i] = adap_individuo[i] * peso;
                peso_agregado[i] = peso;
                disparada[i] = ind;
            }
            //if (disparada[i]!=-1)
            //  ha_lanzado[(int)disparada[i]]=true;
        }

//   int *don, ncl;
//   for (int i=0; i<=ind; i++){
//     if (!ha_lanzado[i]){
//       don=IntSubpopulation(1,i,ncl);
//       don[0]=0;
//     }
//   }




        /*    if (adap_individuo[i]>0 && agregado[i]>=0){
              if (adap_individuo[i]*peso > agregado[i]){
                disparada[i]=ind;
                agregado[i]=adap_individuo[i]*peso;
              }
            }
            else if (adap_individuo[i]>0 && agregado[i]<0 && adap_individuo[i]*peso>-agregado[i]){
                   agregado[i]=adap_individuo[i]*peso;
                   disparada[i]=ind;
                 }
                 else if (adap_individuo[i]<0 && agregado[i]>0 && -adap_individuo[i]*peso>agregado[i]){
                        agregado[i]=adap_individuo[i]*peso;
                        disparada[i]=ind;
                      }
                      else if (adap_individuo[i]<0 && agregado[i]<=0){
                             if (-adap_individuo[i]*peso>-agregado[i]){
                               disparada[i]=ind;
                               agregado[i]=adap_individuo[i]*peso;
                             }
                           }
          }*/

        milista.add(0, agregado);
        milista.add(1, peso_agregado);
        milista.add(2, disparada);
        milista.add(3, adap_individuo);
    }


    private double Refitness(double[] agregacion, double[] peso_agregacion, int ind,
                     int n_examples) {
        double aciertos = 0, errores = 0;
        double v_abs1, v_abs2;
        int tam = 0;

        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux = new Integer(tam);
        lista1.add(aux);

        double[] adaptacion = RealSubpopulation(1, ind, lista1);

        aux = lista1.get(0);
        tam = aux.intValue();

        for (int i = 0; i < n_examples; i++) {
            v_abs1 = Valor_Absoluto(adaptacion[i] * valoracion[ind][2]);
            v_abs2 = Valor_Absoluto(agregacion[i]);
            if (agregacion[i] > 0) { // Is covered
                if (adaptacion[i] < 0 &&
                    (v_abs1 > v_abs2 ||
                     (v_abs1 == v_abs2 &&
                      valoracion[ind][2] > peso_agregacion[i]))) {
                    errores = errores + v_abs1;
                }
            } else { // Is not covered
                if (adaptacion[i] > 0 && v_abs1 > v_abs2) {
                    aciertos = aciertos + v_abs1;
                } else if (adaptacion[i] < 0 && v_abs1 > v_abs2) {
                    errores = errores + v_abs1;
                }
            }

        }

        if (aciertos == 0 && errores == 0) {
            return -n_examples;
        } else {
            return aciertos - errores;
        }
    }

}

