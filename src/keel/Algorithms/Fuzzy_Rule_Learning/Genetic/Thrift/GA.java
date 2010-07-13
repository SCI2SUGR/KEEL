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

import org.core.*;
import java.util.ArrayList;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GA {

    myDataset train, test;
    BaseD baseDatos;
    static BaseR baseReglas;
    int populationSize, nEvaluations, n_genes;
    double crossProb, mutProb;
    ArrayList<Individuo> New, Old;
    static int Trials, Trials_mejor;
    int Best_guy;
    int n_var_estado;
    String output;
    int Mu_next;
    double Best_perf;

    public GA(myDataset train, myDataset test, BaseD baseDatos,
              BaseR baseReglas,
              int populationSize,
              int nEvaluations, int n_genes,
              double crossProb, double mutProb, String output) {
        this.train = train;
        this.test = test;
        this.baseDatos = baseDatos;
        this.baseReglas = baseReglas;
        this.populationSize = populationSize;
        this.nEvaluations = nEvaluations;
        this.n_genes = n_genes;
        this.crossProb = crossProb;
        this.mutProb = mutProb;
        this.output = output;
        n_var_estado = train.getnInputs();
        Fichero.escribeFichero(output, "");
    }

    // Cambiar si voy a maximizar
    public static boolean BETTER(double X, double Y) {
        return (X) < (Y);
    }

    public Individuo lanzar() {

        Individuo solucion = new Individuo();
        Trials = Trials_mejor = 0;
        Best_perf = Double.MAX_VALUE;
        init();
        Evaluacion();

        //for (int Gen = 0; Gen <= this.nGenerations; Gen++) {
        int Gen = 0;

        do {

            int n_r = baseReglas.decodifica(New.get(Best_guy).getGene());
            //System.out.println("desv: " + train.variance(train.getnInputs()));
            double ecn_tra = (2.0 / train.variance(train.getnInputs())) *
                             New.get(Best_guy).Perf;
            double ecn_tst = (2.0 / test.variance(test.getnInputs())) *
                             Error(test, n_r);
            System.out.println("Gen: " + Gen + ", #Ev: " + Trials +
                               "; ECMntra-tst = " +
                               ecn_tra + "-" + ecn_tst + " #R: " +
                               n_r + "; EMS: " + Trials_mejor);

            /* Intercambio de las poblaciones nueva y antigua */
            //Old = (ArrayList) New.clone();
            Old.clear();
            for (int i = 0; i < populationSize; i++) {
                Old.add(New.get(i).copia());
            }
            New.clear();

            /* Seleccion mediante el metodo de Baker */
            Select();

            /* Cruce */
            Cruce();

            /* Mutacion */
            Mutacion();

            /* Seleccion elitista */
            Elitist();

            /* Evaluacion de los individuos de la poblacion actual */
            Evaluacion();
            Gen++;
        } while (Trials <= nEvaluations);

        /*APRENDER_PESOS_GA*/

        solucion.copia(New.get(Best_guy));
        int n_r = baseReglas.decodifica(New.get(Best_guy).getGene());
        double ecm_tst = Error(test, n_r);

        System.out.println("Gen: " + Gen + ", #Ev: " + Trials +
                           "; ECMtra-tst = " +
                           New.get(Best_guy).Perf + "-" + ecm_tst + " #R: " +
                           n_r + "; EMS: " + Trials_mejor);
        return solucion; //guarda la mejor solucion

    }

    private void init() {
        New = new ArrayList<Individuo>(populationSize);
        Old = new ArrayList<Individuo>(populationSize);

        if (this.mutProb < 1.0) {
            Mu_next = (int) Math.ceil(Math.log(Randomize.Rand()) /
                                      Math.log(1.0 - mutProb));
        } else {
            Mu_next = 1;
        }
        //System.err.println("Mu-next: "+this.Mu_next);
        //System.err.println("Genes: "+n_genes);

        for (int i = 0; i < populationSize; i++) {
            Individuo ind = new Individuo(n_genes);
            for (int j = 0; j < n_genes; j++) {
                ind.setGene(j,
                            Randomize.RandintClosed(0,
                        baseDatos.getnLabels(n_var_estado)));
            }
            New.add(ind);
            //System.out.print("Cromosoma["+i+"]: ");
            //ind.print();
        }
    }

    private void Evaluacion() {
        double performance, Best_current_perf;
        Best_current_perf = Double.MAX_VALUE;
        boolean eval = false;

        for (int i = 0; i < this.populationSize; i++) {
            if (New.get(i).noEvaluado()) { /* Si no esta evaluado, se evalua */
                New.get(i).Perf = evalua(New.get(i).getGene());
                performance = New.get(i).Perf;
                //System.out.println("Individuo[" + i + "]: " + performance);
                New.get(i).evaluado();
                eval = true;
            } else {
                performance = New.get(i).Perf;
                eval = false;
            }

            if (performance < Best_perf) {
                Best_perf = performance;
                Trials_mejor = Trials;
            }

            if (eval) {
                String cadena = new String("Trails: " + Trials + ", Perf: " +
                                           Best_perf + "\n");
                Fichero.AnadirtoFichero(output, cadena);
            }

            /* Calculo de la posicion del mejor individuo en la poblacion actual */
            if (i == 0) {
                Best_current_perf = performance;
                Best_guy = 0;
            } else
            if (BETTER(performance, Best_current_perf)) {
                Best_current_perf = performance;
                Best_guy = i;
            }
        }
    }

    public double evalua(int[] cromosoma) {
        double ecm;
        Trials++;

        /* Se obtiene la BR según los consecuentes utilizados */
        int n_reglas = baseReglas.decodifica(cromosoma);

        ecm = Error(train, n_reglas);
        return ecm;
    }

    public static double Error(myDataset datos, int n_reglas) {
        int j;
        double suma, fuerza;

        for (j = 0, suma = 0.0; j < datos.getnData(); j++) {
            fuerza = baseReglas.FLC(datos.getExample(j), n_reglas);
            suma += 0.5 * Math.pow(datos.getOutputAsReal(j) - fuerza, 2.0);
        }

        return (suma / (double) datos.getnData());

    }

    private void Select() {
        double expected, factor, perf, ptr, sum, rank_max, rank_min;
        int i, j, k, best, temp;
        int[] sample = new int[populationSize];

        /* La seleccion se hara siguiendo el modelo de ranking lineal r_min=0.75 */
        rank_min = 0.75;

        /* Asignamos a cada elemento su ranking en la poblacion:
            rank = populationSize-1 para el mejor y ranking = 0 para el peor.
         Se usa el campo n_e para almacenar el ranking de cada elemento */
        for (i = 0; i < populationSize; i++) {
            Old.get(i).setRanking(0);
        }

        for (i = 0; i < populationSize - 1; i++) {
            /* Se encuentra el mejor elemento de los que restan por ordenar*/
            best = -1;
            perf = 0.0;
            for (j = 0; j < populationSize; j++) {
                if (Old.get(j).getRanking() == 0 &&
                    (best == -1 || this.BETTER(Old.get(j).Perf, perf))) {
                    perf = Old.get(j).Perf;
                    best = j;
                }
            }
            /* Se marca dicho elemento con su ranking */
            Old.get(best).setRanking(populationSize - 1 - i);
        }

        /* Se normaliza para crear las probabilidades */
        rank_max = 2.0 - rank_min;
        factor = (rank_max - rank_min) / (double) (populationSize - 1);

        /* Se asigna el numero de copias esperadas a cada cromosoma en funcion de
         la probabilidad de seleccion que tenga asociada. Se procede a la seleccion
           de los cromosomas segun el metodo de Baker */
        k = 0;
        ptr = Randomize.Rand();
        for (sum = i = 0; i < populationSize; i++) {
            expected = rank_min + Old.get(i).getRanking() * factor;
            for (sum += expected; sum >= ptr; ptr++) {
                sample[k++] = i;
            }
        }

        if (k != populationSize) {
            /* Aseguro que se seleccione toda la poblacion si falta algun miembro */
            for (; k < populationSize; k++) {
                sample[k] = Randomize.RandintClosed(0, populationSize-1);
            }
        }
        /*for (i = 0; i < populationSize; i++) {
            System.out.print(sample[i]+", ");
                 }
                 System.out.println("");*/

        /* Se procede a barajar los cromosomas seleccionados para aplicar posterior-
           mente los operadores geneticos */
        for (i = 0; i < populationSize; i++) {
            j = Randomize.RandintClosed(i, populationSize-1);
            temp = sample[j];
            sample[j] = sample[i];
            sample[i] = temp;
        }

        /* Se crea la nueva poblacion con los individuos seleccionados */
        for (i = 0; i < populationSize; i++) {
            k = sample[i];
            Individuo ind = new Individuo(n_genes);
            for (j = 0; j < n_genes; j++) {
                //New.get(i).Gene[j] =  Old.get(k).Gene[j];
                ind.setGene(j, Old.get(k).getGene(j));
            }
            //New.get(i).Perf = Old.get(k).Perf;
            //New.get(i).evaluado();
            ind.Perf = Old.get(k).Perf;
            ind.evaluado();
            New.add(ind);
            //System.out.print("Individuo[" + i + "][" + k + "]: ");
            //New.get(i).print();
        }

    }

    private void Cruce()
    /* Operador de cruce multipunto en dos puntos */
    {
        int mom, dad, xpoint1, xpoint2, i;
        int temp;

        for (mom = 0; mom < (int) crossProb; mom += 2) {
            dad = mom + 1;

            /* Se generan los dos puntos de cruce*/
            xpoint1 = Randomize.RandintClosed(0, n_genes-1);
            if (xpoint1 != n_genes - 1) {
                xpoint2 = Randomize.RandintClosed(xpoint1 + 1, n_genes-1);
            } else {
                xpoint2 = n_genes - 1;
            }

            /* Se cruzan las partes contenidas entre ambos puntos */
            for (i = xpoint1; i <= xpoint2; i++) {
                temp = New.get(mom).Gene[i];
                New.get(mom).Gene[i] = New.get(dad).Gene[i];
                New.get(dad).Gene[i] = temp;
            }

            /* Se marcan los dos nuevos cromosomas para su posterior evaluacion */
            New.get(mom).setNoEvaluado();
            New.get(dad).setNoEvaluado();
            /*System.out.print("Individuo["+mom+"]: ");
                         New.get(mom).print();
                         System.out.print("Individuo["+dad+"]: ");
                         New.get(dad).print();*/
        }

    }

    private void Mutacion() {
        int posiciones, i, j;
        double m;

        posiciones = n_genes * populationSize;

        if (mutProb > 0) {
            while (Mu_next < posiciones) {
                /* Se determina el cromosoma y el gen que corresponden a la posicion que
                   se va a mutar */
                i = (int) Mu_next / n_genes;
                j = (int) Mu_next % n_genes;

                /* Se efectua la mutacion sobre ese gen */
                if (New.get(i).getGene(j) == 0)
                /* Si estoy en la primera etiqueta, es */
                {
                    New.get(i).Gene[j] += 1; /* obligatorio sumar */
                } else {
                    if (New.get(i).Gene[j] ==
                        baseDatos.getnLabels(n_var_estado) - 1)
                    /* Si estoy en la */
                    {
                        New.get(i).Gene[j] -= 1;
                        /* ultima, lo obligatorio es restar */
                    } else {
                        if (New.get(i).Gene[j] ==
                            baseDatos.getnLabels(n_var_estado))
                        /* Si es el simbolo */
                        {
                            New.get(i).Gene[j] = Randomize.RandintClosed(0,
                                    baseDatos.getnLabels(n_var_estado)-1);
                            /* nulo, le asocio cualquiera */
                        } else {
                            /* En cualquier otro caso, sumo o resto aleatoriamente */
                            m = Randomize.Rand();
                            if (m < 0.5) {
                                New.get(i).Gene[j] += 1;
                            } else {
                                New.get(i).Gene[j] -= 1;
                            }
                        }
                    }
                }

                New.get(i).setNoEvaluado();

                /* Se calcula la siguiente posicion a mutar */
                if (mutProb < 1.0) {
                    m = Randomize.Rand();
                    Mu_next +=
                            (int) Math.ceil(Math.log(m) /
                                            Math.log(1.0 - mutProb));
                } else {
                    Mu_next += 1;
                }
            }
        }

        Mu_next -= posiciones;

    }

    private void Elitist()
    /* Seleccion elitista */
    {
        int i, k;
        boolean found = false;

        /* Se estudia a ver si el mejor cromosoma de la poblacion anterior ha sido
           seleccionado para formar parte de la nueva */
        for (i = 0; i < populationSize && (!found); i++) {
            for (k = 0, found = true; (k < n_genes) && (found); k++) {
                found = (New.get(i).Gene[k] == Old.get(Best_guy).Gene[k]);
            }
        }

        /* Si el mejor cromosoma no ha perdurado, se sustituye el ultimo de la
           poblacion por este. */
        if (!found) {
            for (k = 0; k < n_genes; k++) {
                New.get(populationSize - 1).Gene[k] = Old.get(Best_guy).Gene[k];
            }
            New.get(populationSize - 1).Perf = Old.get(Best_guy).Perf;
            New.get(populationSize - 1).evaluado();
        }
    }

    public static int dameTrials() {
        return Trials_mejor;
    }

}

