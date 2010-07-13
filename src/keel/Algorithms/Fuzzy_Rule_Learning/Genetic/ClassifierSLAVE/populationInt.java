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

import java.util.ArrayList;
import org.core.*;

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class populationInt {
/**
 * <p>
 * It encodes an integer population
 * </p>
 */
 	
    double prob_mutacion;
    double prob_cruce;
    int elitismo;
    int n_individuos;
    int[] tamano;
    boolean[] modificado;
    double[] valoracion;
    int rango;
    int[][] individuos;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    populationInt() {
        prob_mutacion = 0.0;
        prob_cruce = 0.0;
        elitismo = 0;
        n_individuos = 0;
        tamano = new int[0];
        modificado = new boolean[0];
        valoracion = new double[0];
        individuos = new int[0][0];
        rango = 0;
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param rang int Range for the integer population
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     */
    populationInt(int rang, double mut, double cruce, int eli, int n) {
        prob_mutacion = mut;
        prob_cruce = cruce;
        elitismo = eli;
        n_individuos = n;
        rango = rang;
        tamano = new int[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            tamano[i] = 0;
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        valoracion = new double[n_individuos];

        individuos = new int[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            individuos[i] = new int[0];
        }
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param rang int Range for the integer population     
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     * @param tama int Size for all the individuals in the population
     */
    populationInt(int rang, double mut, double cruce, int eli, int n,
                    int tama) {
        prob_mutacion = mut;
        prob_cruce = cruce;
        elitismo = eli;
        n_individuos = n;
        rango = rang;
        tamano = new int[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            tamano[i] = tama;
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        valoracion = new double[n_individuos];

        individuos = new int[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            individuos[i] = new int[tamano[i]];
        }
    }

    /**
     * <p>
     * Creates an integer population as a copy of another one
     * </p>
     * @param x populationInt The integer population used to created the new one
     */
    populationInt(populationInt x) {
        this.prob_mutacion = x.prob_mutacion;
        this.prob_cruce = x.prob_cruce;
        this.elitismo = x.elitismo;
        this.n_individuos = x.n_individuos;
        this.rango = x.rango;

        this.tamano = new int[this.n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.tamano[i] = x.tamano[i];
        }

        this.modificado = new boolean[this.n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.modificado[i] = x.modificado[i];
        }

        this.valoracion = new double[this.n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.valoracion[i] = x.valoracion[i];
        }

        this.individuos = new int[this.n_individuos][];
        for (int i = 0; i < this.n_individuos; i++) {
            if (this.tamano[i] > 0) {
                this.individuos[i] = new int[this.tamano[i]];
                for (int j = 0; j < this.tamano[i]; j++) {
                    this.individuos[i][j] = x.individuos[i][j];
                }
            }
        }

    }

	/**
	 * <p>
	 * Returns the size of the individual "i" of the population
	 * </p>
	 * @param i int The position of the individual in the population
	 * @return int The size of the individual
	 */
    int SizeOfIndividual(int i) {
        return tamano[i];
    }

	/**
	 * <p>
	 * Returns the individual in position "i" of the population
	 * </p>
	 * @param i int The position of the individual in the population
	 * @return int[] The individual
	 */
    int[] GetIndividual(int i) {
        return individuos[i];
    }


	/**
	 * <p>
	 * Returns the individual in position "i" of the population. It size is also returned
	 * </p>
	 * @param i int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return int[] The individual
	 */
    int[] GetIndividual(int i, ArrayList<Integer> milista) {
        Integer tama;
        tama = milista.get(0);
        int tam;
        tam = tamano[i];
        tama = Integer.valueOf(tam);
        milista.add(0, tama);

        return individuos[i];
    }


	/**
	 * <p>
	 * Returns if the individual in position "i" has been modified or not
	 * </p>
	 * @param i int The position of the individual in the population	 
	 * @return TRUE if the individual in position "i" has been modified. FALSE otherwise
	 */
    boolean Modified(int i) {
        return modificado[i];
    }

	/**
	 * <p>
	 * Copies the individual "j" in population "x" to the position "i" of the current population
	 * </p>
	 * @param i int Position in the current population to copy the individual
	 * @param x populationInt Another population of individuals
	 * @param j int Position of the individual to be copied in the other population of individuals
	 */
    void CopyIndividual(int i, populationInt x, int j) {

        tamano[i] = x.tamano[i];
        individuos[i] = new int[tamano[i]];
        for (int k = 0; k < tamano[i]; k++) {
            individuos[i][k] = x.individuos[j][k];
        }
    }

	/**
	 * <p>
	 * It swaps the invididual in positions "i" and "j"
	 * </p>
	 * @param i int The position of the first individual
	 * @param j int The position of the second individual
	 */
    void Swap(int i, int j) {
        int k = 0;
        operations O = new operations();

        ArrayList<Integer> lista1 = new ArrayList<Integer>(2);

        Integer aux1 = new Integer(tamano[i]);
        Integer aux2 = new Integer(tamano[j]);

        lista1.add(aux1);
        lista1.add(aux2);

        O.Swap_int(lista1);

        aux1 = lista1.get(0);
        aux2 = lista1.get(1);
        tamano[i] = aux1.intValue();
        tamano[j] = aux2.intValue();

        ArrayList<boolean[]> lista2 = new ArrayList<boolean[]>(1);

        boolean[] aux = new boolean[2];
        aux[0] = modificado[i];
        aux[1] = modificado[j];

        lista2.add(aux);

        O.Swap_boolean(lista2);

        aux = lista2.get(0);

        modificado[i] = aux[0];
        modificado[j] = aux[1];

        ArrayList<Double> lista3 = new ArrayList<Double>(2);

        Double aux3 = new Double(valoracion[i]);
        Double aux4 = new Double(valoracion[j]);

        lista3.add(aux3);
        lista3.add(aux4);

        O.Swap_double(lista3);

        aux3 = lista3.get(0);
        aux4 = lista3.get(1);
        valoracion[i] = aux3.doubleValue();
        valoracion[j] = aux4.doubleValue();

        int longitud = individuos[i].length;
        int[] p = new int[longitud];
        for (k = 0; k < longitud; k++) {
            p[k] = individuos[i][k];
        }

        for (k = 0; k < longitud; k++) {
            individuos[i][k] = individuos[j][k];
            individuos[j][k] = p[k];
        }
    }


	/**
	 * <p>
	 * Transforms the individual in position "i" of population to a genetcode Object
	 * </p>
	 * @param i The position of the individual
	 * @param milista1 ArrayList <char[]> The individual
	 * @param milista2 ArrayList <Integer> The size of the individual
	 */
    void Code(int i, ArrayList < int[] > milista1, ArrayList<Integer>
            milista2) {
        //if (v!=0)
        //delete [] v;

        Integer tama = new Integer(tamano[i]);
        int tam = tama.intValue();
        int[] v = new int[tam];
        for (int j = 0; j < tam; j++) {
            v[j] = individuos[i][j];
        }

        milista1.add(0, v);
        milista2.add(0, tama);
    }


    private void Sort() {
        for (int i = 0; i < n_individuos - 1; i++) {
            for (int j = n_individuos - 1; j > i; j--) {
                if (valoracion[j] > valoracion[j - 1]) {
                    Swap(j, j - 1);
                }
            }
        }
    }


	/** 
	 * <p>
	 * Prints in the standard output the definition for the individual in position "i"
	 * </p>
	 * @param i int The position of the individual in the population
	 */
    void PrintDefinition(int i) {
        for (int j = 0; j < tamano[i]; j++) {
            System.out.print(individuos[i][j]);
        }
        System.out.println("");
    }


	/** 
	 * <p>
	 * Prints in the standard output the fitness of the individual in position "i"
	 * </p>
	 * @param i int The position of the individual in the population
	 */
    void PrintFitness(int i) {
        System.out.println("Fitnes: " + valoracion[i]);
    }


	/** 
	 * <p>
	 * Sets the gen "bit" of the individual in position "indiv" to the value "value"
	 * </p>
	 * @param indiv int The position of the individual in the population
	 * @param bit int The gen in the individual
	 * @param value int The new value
	 */
    void PutValue(int indiv, int bit, int value) {
        individuos[indiv][bit] = value;
    }

	/** 
	 * <p>
	 * Gets the value of the gen "bit" of the individual in position "indiv"
	 * </p>
	 * @param indiv int The position of the individual in the population
	 * @param bit int The gen in the individual
	 * @return int The new value
	 */
    int GetValue(int indiv, int bit) {
        return individuos[indiv][bit];
    }

	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 */
    void RandomInitialPopulation() {
        for (int i = 0; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                individuos[i][j] = i % rango;
            }
        }
    }

	/** 
	 * <p>
	 * It randomly creates a initial population (setting the consequent to "consecuente")
	 * </p>
	 * @param consecuente int The consequent for the individuals
	 */
    void RandomInitialPopulation(int consecuente) {
        for (int i = 0; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                individuos[i][j] = consecuente;
            }
        }
    }


	/**
	 * <p>
	 * It applies the uniform mutation operator
	 * </p>
	 */
    void UniformMutation() {
        operations O = new operations();
        int aux;
        for (int i = elitismo; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    do {
                        aux = (int) Randomize.Rand() % rango;
                    } while (aux == individuos[i][j]);
                    individuos[i][j] = aux;
                    modificado[i] = true;
                }
            }
        }
    }

	/**
	 * <p>
	 * It applies the uniform mutation operator with a probability "prob0"
	 * </p>
	 * @param prob0 double Probability of application for the operator
	 */
    void ModUniformMutation(double prob0) {
        operations O = new operations();
        int aux;
        for (int i = elitismo; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    if (O.Probability(prob0)) {
                        aux = 0;
                    } else {
                        do {
                            aux = (int) Randomize.Rand() % rango;
                        } while (aux == individuos[i][j]);
                    }

                    individuos[i][j] = aux;
                    modificado[i] = true;
                }
            }
        }
    }


	/**
	 * <p>
	 * It applies the uniform crossover operator
	 * </p>
	 */
    void UniformCrossover() {
        operations O = new operations();
        int a, p1;
        int aux;
        for (int i = elitismo; i < n_individuos; i++) {
            if (O.Probability(prob_cruce)) {
                a = O.Select_Random_Individual(n_individuos, i, elitismo);
                p1 = O.CutPoint(tamano[i]);
                modificado[i] = true;
                modificado[a] = true;
                for (int j = 0; j < p1; j++) {
                    aux = individuos[i][j];
                    individuos[i][j] = individuos[a][j];
                    individuos[a][j] = aux;
                }
            }
        }
    }


	/**
	 * <p>
	 * It applies the two points crossover operator
	 * </p>
	 */
    void TwoPointsCrossover() {
        operations O = new operations();
        int a, p1, p2;
        int aux;
        for (int i = elitismo; i < n_individuos; i++) {
            if (O.Probability(prob_cruce)) {
                a = O.Select_Random_Individual(n_individuos, i, elitismo);

                ArrayList<Integer> lista = new ArrayList<Integer>(2);

                p1 = 0;
                p2 = 0;
                Integer aux1 = new Integer(p1);
                Integer aux2 = new Integer(p2);

                lista.add(aux1);
                lista.add(aux2);

                O.CutPoint2(tamano[i], lista);

                aux1 = lista.get(0);
                aux2 = lista.get(1);
                p1 = aux1.intValue();
                p2 = aux2.intValue();

                modificado[i] = true;
                modificado[a] = true;
                for (int j = p1; j < p2; j++) {
                    aux = individuos[i][j];
                    individuos[i][j] = individuos[a][j];
                    individuos[a][j] = aux;
                }
            }
        }
    }

	/**
	 * <p>
	 * It applies the steady state uniform mutation operator
	 * </p>
	 */
    void SteadyState_UniformMutation() {
        operations O = new operations();
        int aux;
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    do {
                        aux = (int) Randomize.Rand() % rango;
                    } while (aux == individuos[i][j]);

                    individuos[i][j] = aux;
                    modificado[i] = true;
                }
            }
        }
    }


	/**
	 * <p>
	 * It applies the steady state uniform mutation operator with a probability "prob0"
	 * </p>
	 * @param prob0 double Probability of application for the operator
	 */
    void SteadyState_ModUniformMutation(double prob0) {
        operations O = new operations();
        int aux;
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    if (O.Probability(prob0)) {
                        aux = 0;
                    } else {
                        do {
                            aux = (int) Randomize.Rand() % rango;
                        } while (aux == individuos[i][j]);
                    }

                    individuos[i][j] = aux;
                    modificado[i] = true;
                }
            }
        }
    }


	/**
	 * <p>
	 * It applies the steady state uniform crossover operator
	 * </p>
	 */
    void SteadyState_UniformCrossover() {
        operations O = new operations();
        int a, b, p1;
        int aux;
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            a = O.Select_Random_Individual(n_individuos, i, 0);
            b = O.Select_Random_Individual(n_individuos, i, 0);
            p1 = O.CutPoint(tamano[i]);
            modificado[i] = true;
            for (int j = 0; j < p1; j++) {
                individuos[i][j] = individuos[a][j];
            }
            for (int j = p1; j < tamano[i]; j++) {
                individuos[i][j] = individuos[b][j];
            }
        }
    }


	/**
	 * <p>
	 * It applies the steady state two points crossover operator
	 * </p>
	 */
    void SteadyState_TwoPointsCrossover() {
        operations O = new operations();
        int a, b, p1, p2;
        int aux;
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            a = O.Select_Random_Individual(n_individuos, i, 0);
            b = O.Select_Random_Individual(n_individuos, i, 0);

            ArrayList<Integer> lista = new ArrayList<Integer>(2);

            p1 = 0;
            p2 = 0;
            Integer aux1 = new Integer(p1);
            Integer aux2 = new Integer(p2);

            lista.add(aux1);
            lista.add(aux2);

            O.CutPoint2(tamano[i], lista);

            aux1 = lista.get(0);
            aux2 = lista.get(1);
            p1 = aux1.intValue();
            p2 = aux2.intValue();

            modificado[i] = true;
            for (int j = 0; j < p1; j++) {
                individuos[i][j] = individuos[a][j];
            }
            for (int j = p1; j < p2; j++) {
                individuos[i][j] = individuos[b][j];
            }
            for (int j = p2; j < tamano[i]; j++) {
                individuos[i][j] = individuos[a][j];
            }
        }
    }


	/**
	 * <p>
	 * It applies the steady state two points crossover operator between the individual in position "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_TwoPointsCrossover(int indiv1, int indiv2) {
        int a, b, p1, p2;
        int aux;
        int i = n_individuos - 2;
        //for (int i=n_individuos-2; i<n_individuos; i++){
        //a=Select_Random_Individual(n_individuos,i,0);
        //b=Select_Random_Individual(n_individuos,i,0);
        //CutPoint2(tamano[i],p1,p2);
        modificado[i] = true;
        modificado[i + 1] = true;
        /*for (int j=0; j<p1; j++)
            individuos[i][j]=individuos[a][j];
                   for (int j=p1; j<p2; j++)
                individuos[i][j]=individuos[b][j];
                   for (int j=p2; j<tamano[i]; j++)
                individuos[i][j]=individuos[a][j];
         */
        individuos[i][0] = individuos[indiv1][0];
        individuos[i + 1][0] = individuos[indiv2][0];

        //}
    }

}

