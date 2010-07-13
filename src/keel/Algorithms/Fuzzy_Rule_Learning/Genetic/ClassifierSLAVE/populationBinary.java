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

/**
 * <p>
 * @author Written by Francisco José Berlanga (University of Jaén) 01/01/2007
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class populationBinary {
/**
 * <p>
 * It encodes a binary population
 * </p>
 */
 
    double prob_mutacion;
    double prob_cruce;
    int elitismo;
    int n_individuos;
    int[] tamano;
    boolean[] modificado;
    double[] valoracion;
    char[][] individuos;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    populationBinary() {
        prob_mutacion = 0.0;
        prob_cruce = 0.0;
        elitismo = 0;
        n_individuos = 0;
        tamano = new int[0];
        modificado = new boolean[0];
        valoracion = new double[0];
        individuos = new char[0][0];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     */
    populationBinary(double mut, double cruce, int eli, int n) {
        prob_mutacion = mut;
        prob_cruce = cruce;
        elitismo = eli;
        n_individuos = n;
        tamano = new int[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            tamano[i] = 0;
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        valoracion = new double[n_individuos];

        individuos = new char[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            individuos[i] = new char[0];
        }
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     * @param tama int Size for all the individuals in the population
     */
    populationBinary(double mut, double cruce, int eli, int n, int tama) {
        prob_mutacion = mut;
        prob_cruce = cruce;
        elitismo = eli;
        n_individuos = n;
        tamano = new int[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            tamano[i] = tama;
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        valoracion = new double[n_individuos];

        individuos = new char[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            individuos[i] = new char[tamano[i]];
        }
    }

    /**
     * <p>
     * Constructor
     * </p>
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     * @param tama int[] Size for each individual in the population
     */
    populationBinary(double mut, double cruce, int eli, int n, int[] tama) {
        prob_mutacion = mut;
        prob_cruce = cruce;
        elitismo = eli;
        n_individuos = n;
        tamano = new int[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            tamano[i] = tama[i];
        }

        modificado = new boolean[n_individuos];
        for (int i = 0; i < n_individuos; i++) {
            modificado[i] = true;
        }

        valoracion = new double[n_individuos];

        individuos = new char[n_individuos][];
        for (int i = 0; i < n_individuos; i++) {
            individuos[i] = new char[tamano[i]];
        }
    }

    /**
     * <p>
     * Creates a binary population as a copy of another one
     * </p>
     * @param x populationBinary The binary population used to created the new one
     */
    populationBinary(populationBinary x) {
        this.prob_mutacion = x.prob_mutacion;
        this.prob_cruce = x.prob_cruce;
        this.elitismo = x.elitismo;
        this.n_individuos = x.n_individuos;

        this.tamano = new int[n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.tamano[i] = x.tamano[i];
        }

        this.modificado = new boolean[n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.modificado[i] = x.modificado[i];
        }

        this.valoracion = new double[n_individuos];
        for (int i = 0; i < this.n_individuos; i++) {
            this.valoracion[i] = x.valoracion[i];
        }

        this.individuos = new char[n_individuos][];
        for (int i = 0; i < this.n_individuos; i++) {
            if (this.tamano[i] > 0) {
                this.individuos[i] = new char[this.tamano[i]];
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
	 * @return char[] The individual
	 */
    char[] GetIndividual(int i) {
        return individuos[i];
    }


	/**
	 * <p>
	 * Returns the individual in position "i" of the population. It size is also returned
	 * </p>
	 * @param i int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return char[] The individual
	 */
    char[] GetIndividual(int i, ArrayList<Integer> milista) {
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
	 * @param x populationBinary Another population of individuals
	 * @param j int Position of the individual to be copied in the other population of individuals
	 */
    void CopyIndividual(int i, populationBinary x, int j) {

        tamano[i] = x.tamano[j];
        individuos[i] = new char[tamano[i]];
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
        char[] p = new char[longitud];
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
    void Code(int i, ArrayList < char[] > milista1, ArrayList<Integer>
            milista2) {
        //if (v!=0)
        //delete [] v;

        Integer tama = new Integer(tamano[i]);
        int tam = tama.intValue();
        char[] v = new char[tam];
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
        System.out.println("Fitness: " + valoracion[i]);
    }


	/** 
	 * <p>
	 * Sets the gen "bit" of the individual in position "indiv" to the value "value"
	 * </p>
	 * @param indiv int The position of the individual in the population
	 * @param bit int The gen in the individual
	 * @param value char The new value
	 */
    void PutValue(int indiv, int bit, char value) {
        individuos[indiv][bit] = value;
    }


	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 */
    void RandomInitialPopulation() {
        operations O = new operations();
        for (int i = 0; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                individuos[i][j] = (O.Probability(0.6)) ? '1' : '0';
            }
        }
        //individuos[i][j]= '1';
    }


    private void PoblacionInicialValor(char valor) {
        for (int i = 0; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                individuos[i][j] = valor;
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
        for (int i = elitismo; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    if (individuos[i][j] == '1') {
                        individuos[i][j] = '0';
                    } else {
                        individuos[i][j] = '1';
                    }
                    modificado[i] = true;
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
        for (int i = n_individuos - 2; i < n_individuos; i++) {
            for (int j = 0; j < tamano[i]; j++) {
                if (O.Probability(prob_mutacion)) {
                    //cout << "Mutado: " << i << "(" << j <<")"<<endl;
                    if (individuos[i][j] == '1') {
                        individuos[i][j] = '0';
                    } else {
                        individuos[i][j] = '1';
                    }
                    modificado[i] = true;
                }
            }
        }

    }
    
    	/**
	 * <p>
	 * It applies the rotation operator to the individual in position "i" in the population
	 * </p>
	 * @param i int The positino of the individual
	 */
    void Rotation(int i) {
        int p;
        char[] copia = new char[tamano[i] + 1];
        operations O = new operations();

        // Make a copy of the chromosome
        for (int j = 0; j < tamano[i]; j++) {
            copia[j] = individuos[i][j];
        }
        // Select a point in the chromosome
        p = O.CutPoint(tamano[i]);

        // Modify the chomosome with the rotation
        for (int j = 0; j < tamano[i]; j++) {
            individuos[i][j] = copia[(j + p) % tamano[i]];
        }

    }

	/**
	 * <p>
	 * It applies the uniform crossover operator
	 * </p>
	 */
    void UniformCrossover() {
        int a, p1;
        char aux;
        operations O = new operations();
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
	 * It applies the steady state uniform crossover operator
	 * </p>
	 */
    void SteadyState_UniformCrossover() {
        int a, b, p1;
        char aux;
        operations O = new operations();
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
	 * It applies the two points crossover operator
	 * </p>
	 */
    void TwoPointsCrossover() {
        int a, p1, p2;
        char aux;
        operations O = new operations();
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
	 * It applies the steady state two points crossover operator
	 * </p>
	 */
    void SteadyState_TwoPointsCrossover() {
        int a, b, p1, p2;
        char aux;
        operations O = new operations();
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
	 * It applies the steady state two points crossover operator between the individual in positions "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_TwoPointsCrossover(int indiv1, int indiv2) {
        int a, b, p1, p2;
        char aux;
        int i = n_individuos - 2;
        operations O = new operations();
        //for (int i=n_individuos-2; i<n_individuos; i++){
        //a=Select_Random_Individual(n_individuos,i,0);
        //b=Select_Random_Individual(n_individuos,i,0);

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
        modificado[i + 1] = true;
        for (int j = 0; j < p1; j++) {
            individuos[i][j] = individuos[indiv1][j];
            individuos[i + 1][j] = individuos[indiv2][j];
        }
        for (int j = p1; j < p2; j++) {
            individuos[i][j] = individuos[indiv2][j];
            individuos[i + 1][j] = individuos[indiv1][j];
        }
        for (int j = p2; j < tamano[i]; j++) {
            individuos[i][j] = individuos[indiv1][j];
            individuos[i + 1][j] = individuos[indiv2][j];
        }

        //}
    }

	/**
	 * <p>
	 * It applies the steady state AND/OR crossover operator between the individual in positions "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_AND_OR_Crossover(int indiv1, int indiv2) {
        int a, b, p1, p2;
        char aux;
        int i = n_individuos - 2;
        operations O = new operations();

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
        modificado[i + 1] = true;
        for (int j = p1; j < p2; j++) {
            if (individuos[indiv1][j] == '1' && individuos[indiv2][j] == '1') {
                individuos[i][j] = '1';
            } else {
                individuos[i][j] = '0';
            }

            if (individuos[indiv1][j] == '1' || individuos[indiv2][j] == '1') {
                individuos[i + 1][j] = '1';
            } else {
                individuos[i + 1][j] = '0';
            }
        }
    }

	/**
	 * <p>
	 * It applies the steady state NAND/NOR crossover operator between the individual in positions "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_NAND_NOR_Crossover(int indiv1, int indiv2) {
        int a, b, p1, p2;
        char aux;
        int i = n_individuos - 2;
        modificado[i] = true;
        modificado[i + 1] = true;
        for (int j = 0; j < tamano[i]; j++) {
            if (individuos[indiv1][j] == '1' && individuos[indiv2][j] == '1') {
                individuos[i][j] = '0';
            } else {
                individuos[i][j] = '1';
            }

            if (individuos[indiv1][j] == '1' || individuos[indiv2][j] == '1') {
                individuos[i + 1][j] = '0';
            } else {
                individuos[i + 1][j] = '1';
            }
        }
    }

}

