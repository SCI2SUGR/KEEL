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

import org.core.*;
import java.util.ArrayList;

/**
 * <p>Title: GA</p>
 *
 * <p>Description: Contains the functions for the GA</p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: KEEL </p>
 *
 * @author A. Fernandez
 * @version 1.0
 */
public class GA {

    myDataset train,test;
    BaseD baseDatos;
    static BaseR baseReglas;
    Espacio subEspacio;
    boolean learnWeights;
    int weightCrossType, populationSize, nGenerations;
    double crossProb, mutProb, a_param;
    ArrayList<Individuo> New, Old;
    static int Trials, Trials_mejor;
    int Best_guy;
    int n_var_estado;
    String output;
    double Best_perf;

    public GA(myDataset train, myDataset test, BaseD baseDatos, BaseR baseReglas,
              Espacio subEspacio,
              boolean learnWeights, int weightCrossType, int populationSize,
              int nGenerations,
              double crossProb, double mutProb, double a_param, String output) {
        this.train = train;
        this.test = test;
        this.baseDatos = baseDatos;
        this.baseReglas = baseReglas;
        this.subEspacio = subEspacio;
        this.learnWeights = learnWeights;
        this.populationSize = populationSize;
        this.weightCrossType = weightCrossType;
        this.nGenerations = nGenerations;
        this.crossProb = crossProb;
        this.mutProb = mutProb;
        this.a_param = a_param;
        this.output = output;
        n_var_estado = train.getnInputs();
        Fichero.escribeFichero(output, "");
    }

    // This has to be changed for a maximization function
    public static boolean BETTER(double X, double Y) {
        return (X) < (Y);
    }

    public Individuo lanzar() {
        Individuo solucion = new Individuo(subEspacio);

        //System.out.print("mejor solucion: ");
        //solucion.print();
        Trials = Trials_mejor = 0;
        Best_perf = Double.MAX_VALUE;
        for (int Gen = 0; Gen <= this.nGenerations; Gen++) {

            if (Gen == 0) {
                init(solucion);
                Evaluacion();
            } else {
                /* Exchange the old and new population */
                //Old = (ArrayList) New.clone();
                //New.clear();
                Old.clear();
                for (int i = 0; i < populationSize; i++){
                    Old.add(New.get(i).copia());
                }
                New.clear();
                //System.out.println("Semilla1: "+Randomize.Seed);
                /* Selection by means of the Baker method */
                Select();
                //System.out.println("Semilla2: "+Randomize.Seed);

                /* Cruce */
                if (this.learnWeights) {
                    if (this.weightCrossType == 1) {
                        CruceP_MMA();
                        //System.out.println("Semilla3: "+Randomize.Seed);
                    } else {
                        CruceP_2P();
                    }
                } else {
                    Cruce();
                }

                /* Mutation */
                Mutacion();
                //System.out.println("Semilla4: "+Randomize.Seed);

                /* Elitist Selection */
                Elitist();

                /* Evaluation of the individuals of the current population */
                Evaluacion();
            }

            /*LEARN_WEIGHTS_GA*/
            int n_reg;
            if (this.learnWeights) {
                n_reg = baseReglas.obtener_BRP(New.get(Best_guy).getGene(),
                                               New.get(Best_guy).getPeso(),
                                               subEspacio);
                solucion.copia(New.get(Best_guy));
            } else {
                n_reg = baseReglas.obtener_BR(New.get(Best_guy).getGene(),
                                              subEspacio);
                solucion.copia(New.get(Best_guy));
            }
            double ecm_tst = Error(test, n_reg);

            /*System.out.println("Gen: " + Gen + "; ECMtra-tst = " +
                               New.get(Best_guy).Perf + "-" + ecm_tst + " #R: " +
                               n_reg + "; EMS: " + Trials_mejor);*/
        }
        return solucion; //stores the best solution

    }

    private void init(Individuo solucion) {
        New = new ArrayList<Individuo>(populationSize);
        Old = new ArrayList<Individuo>(populationSize);
        Individuo ind = new Individuo(subEspacio.size());
        ind.copia(solucion);
        //ind.generaAleatorio(1.0, 1.0);
        New.add(ind);
        for (int i = 1; i < populationSize; i++) {
            Individuo ind2 = new Individuo(subEspacio.size());
            for (int j = 0; j < subEspacio.size(); j++) {
                ind2.setGene(j, subEspacio.get(j).getConsecuente(
                        //Randomize.RandintClosed(0,subEspacio.numConsecuentes(j))));
                        Randomize.RandintClosed(0,subEspacio.numConsecuentes(j)-1)));
                ind2.setPeso(j, Randomize.Rand());
            }
            New.add(ind2);
        }
    }

    private void Evaluacion() {
        double performance, Best_current_perf;
        Best_current_perf = Double.MAX_VALUE;
        boolean eval = false;

        for (int i = 0; i < this.populationSize; i++) {
            if (New.get(i).noEvaluado()) { /* If not evaluated, evaluate it*/
                /*LEARN_WEIGHTS_GA*/
                if (this.learnWeights) {
                    New.get(i).Perf = evaluaP(New.get(i).getGene(),
                                              New.get(i).getPeso());
                } else {
                    New.get(i).Perf = evalua(New.get(i).getGene());
                }
                performance = New.get(i).Perf;
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
                String cadena = new String("Trails: "+Trials+", Perf: "+Best_perf+"\n");
                Fichero.AnadirtoFichero(output, cadena);
            }

            /* Computation of the position of the best individual in the current population */
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

    public double evaluaP(int[] consecuentes, double[] pesos) {
        double ecm;
        Trials++;

        /* We obtain the RB for the used consequents */
        int n_reglas = baseReglas.obtener_BRP(consecuentes, pesos, subEspacio);

        ecm = Error(train, n_reglas);
        return ecm;
    }

    public double evalua(int[] consecuentes) {
        double ecm;
        Trials++;

        /* We obtain the RB for the used consequents */
        int n_reglas = baseReglas.obtener_BR(consecuentes, subEspacio);

        ecm = Error(train, n_reglas);
        return ecm;
    }

    public static double Error(myDataset datos, int n_reglas) {
        int j;
        double suma, fuerza = 0;
        for (j = 0, suma = 0.0; j < datos.getnData(); j++) {
            //System.err.print("Ejemplo["+j+"]: ");
            //System.err.println("BR[0]: "+baseReglas.BaseReglas[0][baseReglas.n_var_estado].y);
            fuerza = baseReglas.FLC(datos.getExample(j), n_reglas);
            suma += 0.5 * Math.pow(datos.getOutputAsReal(j) - fuerza, 2.0);
        }
        return (suma / (double) datos.getnData());

    }


    private void Select() {
        double expected, factor, perf, ptr, sum, rank_max, rank_min;
        int i, j, k, best;
        int[] sample = new int[populationSize];

        /*  Selection will be performed following the lineal ranking model r_min=0.75 */
        rank_min = 0.75;

        /* We asign to each element its ranking in the population:
           rank = populationSize-1 for the best y ranking = 0 for the worst.
           The field n_e is used to store the ranking of each element*/
        for (i = 0; i < populationSize; i++) {
            Old.get(i).setRanking(0);
        }

        for (i = 0; i < populationSize - 1; i++) {
            /* We found the best element of those remaining to order */
            best = -1;
            perf = 0.0;
            for (j = 0; j < populationSize; j++) {
                if (Old.get(j).getRanking() == 0 &&
                    (best == -1 || this.BETTER(Old.get(j).Perf, perf))) {
                    perf = Old.get(j).Perf;
                    best = j;
                }
            }
            /* We mark this element with its ranking */
            Old.get(best).setRanking(populationSize - 1 - i);
        }

        /* It is normalized to build the probabilities */
        rank_max = 2.0 - rank_min;
        factor = (rank_max - rank_min) / (double) (populationSize - 1);

        /* It is assigned the number of expected copies to each chromosome in function
        of the selection probability that it has associated. Then, we proceed to the
        selection of the chromosomes following the Baker method*/
        k = 0;
        ptr = Randomize.Rand();
        for (sum = i = 0; i < populationSize; i++) {
            expected = rank_min + Old.get(i).getRanking() * factor;
            //System.out.println("Rank_min: "+rank_min+" Rank: "+Old.get(i).getRanking()+" factor: "+factor+" == Exp: "+expected);
            for (sum += expected; sum >= ptr; ptr++) {
                sample[k++] = i;
            }
        }
        //System.out.println("SemillaX: "+Randomize.Seed);
        if (k != populationSize) {
            /* We ensure that all population is selected if some member is missing */
            for (; k < populationSize; k++) {
                sample[k] = Randomize.RandintClosed(0, populationSize-1);
            }
        }

        /* The new population is created with the selected individuals */
        for (i = 0; i < populationSize; i++) {
            k = sample[i];
            Individuo ind = new Individuo(subEspacio.size());
            for (j = 0; j < subEspacio.size(); j++) {
                //New.get(i).setGene(j, Old.get(k).getGene(j));
                ind.setGene(j, Old.get(k).getGene(j));
                /*LEARN_WEIGHTS_GA*/
                if (this.learnWeights) {
                    //New.get(i).setPeso(j, Old.get(k).getPeso(j));
                    ind.setPeso(j, Old.get(k).getPeso(j));
                }
            }
            //New.get(i).Perf = Old.get(k).Perf;
            //New.get(i).evaluado();
            ind.Perf = Old.get(k).Perf;
            ind.evaluado();
            New.add(ind);
        }
    }

    private void Cruce()
    /* Multipoint crossover operator in two points */
    {
        int mom, dad, tmp, xpoint1, xpoint2, i, j, n_cruces;
        int[] pool = new int[populationSize];
        int gen;

        for (i = 0; i < populationSize; i++) {
            pool[i] = i;
        }

        n_cruces = (int) (populationSize * this.crossProb);

        for (i = 0; i < n_cruces; i += 2) {
            tmp = Randomize.RandintClosed(0, populationSize - i -1);
            mom = pool[tmp];
            pool[tmp] = pool[populationSize - i - 1];

            tmp = Randomize.RandintClosed(0, populationSize - i - 2);
            dad = pool[tmp];
            pool[tmp] = pool[populationSize - i - 2];

            /* Two crossover points are generated*/
            xpoint1 = Randomize.RandintClosed(0, subEspacio.size() -1);
            do {
                xpoint2 = Randomize.RandintClosed(0, subEspacio.size() -1);
            } while (xpoint2 == xpoint1);
            if (xpoint2 < xpoint1) {
                tmp = xpoint1;
                xpoint1 = xpoint2;
                xpoint2 = tmp;
            }

            /* The parts contained between both points are crossed */
            for (j = xpoint1; j <= xpoint2; j++) {
                gen = New.get(mom).getGene(j);
                New.get(mom).setGene(j, New.get(dad).getGene(j));
                New.get(dad).setGene(j, gen);
            }

            /* The two new chromosomes are marked for their posterior evaluation */
            New.get(mom).setNoEvaluado();
            New.get(dad).setNoEvaluado();
        }

    }

    private void CruceP_2P()
    /* Multipoint crossover operator in two points with weights*/
    {
        int mom, dad, tmp, xpoint1, xpoint2, xpointP1, xpointP2, i, j, n_cruces;
        int[] pool = new int[populationSize];
        int gen;
        double genP;

        for (i = 0; i < populationSize; i++) {
            pool[i] = i;
        }

        n_cruces = (int) (populationSize * this.crossProb);

        for (i = 0; i < n_cruces; i += 2) {
            tmp = Randomize.RandintClosed(0, populationSize - i -1);
            mom = pool[tmp];
            pool[tmp] = pool[populationSize - i - 1];

            tmp = Randomize.RandintClosed(0, populationSize - i - 2);
            dad = pool[tmp];
            pool[tmp] = pool[populationSize - i - 2];

            /* Two crossover points are generated*/
            xpoint1 = Randomize.RandintClosed(0, subEspacio.size() -1);
            do {
                xpoint2 = Randomize.RandintClosed(0, subEspacio.size() -1);
            } while (xpoint2 == xpoint1);
            if (xpoint2 < xpoint1) {
                tmp = xpoint1;
                xpoint1 = xpoint2;
                xpoint2 = tmp;
            }
            /* Two crossover points for the weights are generated*/
            if (this.weightCrossType == 3) {
                xpointP1 = Randomize.RandintClosed(0, subEspacio.size() -1);
                do {
                    xpointP2 = Randomize.RandintClosed(0, subEspacio.size() -1);
                } while (xpointP2 == xpointP1);
                if (xpointP2 < xpointP1) {
                    tmp = xpointP1;
                    xpointP1 = xpointP2;
                    xpointP2 = tmp;
                }
            } else {
                xpointP1 = xpoint1;
                xpointP2 = xpoint2;
            }

            /* The parts contained between both points are crossed */
            for (j = xpoint1; j <= xpoint2; j++) {
                gen = New.get(mom).getGene(j);
                New.get(mom).setGene(j, New.get(dad).getGene(j));
                New.get(dad).setGene(j, gen);
            }
            /* The parts contained between both points are crossed (weights part)*/
            for (j = xpointP1; j <= xpointP2; j++) {
                genP = New.get(mom).getPeso(j);
                New.get(mom).setPeso(j, New.get(dad).getPeso(j));
                New.get(dad).setPeso(j, genP);
            }

            /* The two new chromosomes are marked for their posterior evaluation */
            New.get(mom).setNoEvaluado();
            New.get(dad).setNoEvaluado();
        }

    }

    private double T_producto_logico(double x, double y) {
        if (x < y) {
            return (x);
        } else {
            return (y);
        }
    }


    private double S_suma_logica(double x, double y) {
        if (x > y) {
            return (x);
        } else {
            return (y);
        }
    }


    private double Promedio1(double x, double y, double p) {
        return (p * x + (1 - p) * y);
    }

    private void CruceP_MMA()
    /* Multipoint crossover operator in two points */
    {
        int mom, dad, tmp, xpoint1, xpoint2, i, j, k, n_cruces;
        int[] pool = new int[populationSize];
        int[] indice = new int[8];
        int gen1, gen2;
        double genP1, genP2;
        Individuo[] C = new Individuo[8];

        for (i = 0; i < 8; i++) {
            C[i] = new Individuo(subEspacio.size());
        }

        for (i = 0; i < populationSize; i++) {
            pool[i] = i;
        }

        n_cruces = (int) (populationSize * this.crossProb);
        for (i = 0; i < n_cruces; i += 2) {
            //System.out.print("SemillaX: "+Randomize.Seed+" ");
            tmp = Randomize.RandintClosed(0, populationSize - i -1);
            mom = pool[tmp];
            pool[tmp] = pool[populationSize - i - 1];

            tmp = Randomize.RandintClosed(0, populationSize - i - 2);
            dad = pool[tmp];
            pool[tmp] = pool[populationSize - i - 2];
            //System.out.println("Semilla: "+Randomize.Seed);

            xpoint1 = Randomize.RandintClosed(0, subEspacio.size() -1);
            do {
                xpoint2 = Randomize.RandintClosed(0, subEspacio.size() -1);
            } while (xpoint2 == xpoint1);
            if (xpoint2 < xpoint1) {
                tmp = xpoint1;
                xpoint1 = xpoint2;
                xpoint2 = tmp;
            }
            for (j = 0; j < subEspacio.size(); j++) {
                gen1 = New.get(mom).getGene(j);
                gen2 = New.get(dad).getGene(j);
                genP1 = New.get(mom).getPeso(j);
                genP2 = New.get(dad).getPeso(j);
                if (j < xpoint1 || j > xpoint2) {
                    C[0].setGene(j, gen1);
                    C[1].setGene(j, gen1);
                    C[2].setGene(j, gen1);
                    C[3].setGene(j, gen1);
                    C[4].setGene(j, gen2);
                    C[5].setGene(j, gen2);
                    C[6].setGene(j, gen2);
                    C[7].setGene(j, gen2);
                } else {
                    C[0].setGene(j, gen2);
                    C[1].setGene(j, gen2);
                    C[2].setGene(j, gen2);
                    C[3].setGene(j, gen2);
                    C[4].setGene(j, gen1);
                    C[5].setGene(j, gen1);
                    C[6].setGene(j, gen1);
                    C[7].setGene(j, gen1);
                }

                /* Weights: Four offsprings are generated: one applying the t-norm, one applying
                the t-conorm and the last two of them with the average function*/
                C[0].setPeso(j, T_producto_logico(genP1, genP2));
                C[4].setPeso(j, T_producto_logico(genP1, genP2));
                C[1].setPeso(j, S_suma_logica(genP1, genP2));
                C[5].setPeso(j, S_suma_logica(genP1, genP2));
                C[2].setPeso(j, Promedio1(genP1, genP2, this.a_param));
                C[6].setPeso(j, Promedio1(genP1, genP2, this.a_param));
                C[3].setPeso(j, Promedio1(genP1, genP2, 1.0 - this.a_param));
                C[7].setPeso(j, Promedio1(genP1, genP2, 1.0 - this.a_param));
            }

            /* Selection */
            for (k = 0; k < 8; k++) {
                C[k].Perf = evaluaP(C[k].getGene(), C[k].getPeso());
                indice[k] = k;
            }
            for (k = 0; k < 8; k++) {
                for (j = 0; j < 7 - k; j++) {
                    if (BETTER(C[indice[j + 1]].Perf, C[indice[j]].Perf)) {
                        tmp = indice[j];
                        indice[j] = indice[j + 1];
                        indice[j + 1] = tmp;
                    }
                }
            }

            /* Assignement */
            /* First offspring */
            New.get(mom).copia(C[indice[0]]);
            New.get(mom).Perf = C[indice[0]].Perf;
            New.get(mom).evaluado();

            /* Second offspring */
            New.get(dad).copia(C[indice[1]]);
            New.get(dad).Perf = C[indice[1]].Perf;
            New.get(dad).evaluado();
        }
    }

    private void Mutacion() {
        int i, regla, tmp, indiv, n_mut;
        int[] pool = new int[populationSize];

        for (i = 0; i < populationSize; i++) {
            pool[i] = i;
        }

        n_mut = (int) (populationSize * this.mutProb);

        for (i = 0; i < n_mut; i++) {
            tmp = Randomize.RandintClosed(0, populationSize - i -1);
            indiv = pool[tmp];
            pool[tmp] = pool[populationSize - i - 1];

            /* We randomly obtain a subspace with more than a possible consequent */
            do {
                regla = Randomize.RandintClosed(0, subEspacio.size() -1);
            } while (subEspacio.get(regla).numConsecuentes() == 1);

            /* We randomly generate a different consequent from the current one */
            do {
                tmp = subEspacio.get(regla).getConsecuente(Randomize.
                        RandintClosed(0, subEspacio.get(regla).numConsecuentes()-1));
            } while (tmp == New.get(indiv).getGene(regla));
            New.get(indiv).setGene(regla, tmp);

             /*LEARN_WEIGHTS_GA*/
            if (this.learnWeights) {
                regla = Randomize.RandintClosed(0, subEspacio.size()-1);
                New.get(indiv).setPeso(regla, Randomize.Rand());
            }

            /* We mark the mutated chromosome for its evaluation */
            New.get(indiv).setNoEvaluado();
        }

    }

    private void Elitist()
    /* Elitist Selection */
    {
        int i, k;
        boolean found = false;

        /* We study if the best chromosome of the old population has been selected
        to form part of the new one */
        for (i = 0; i < populationSize && (!found); i++) {
            for (k = 0, found = true; (k < subEspacio.size()) && (found); k++) {
                found = (New.get(i).getGene(k) == Old.get(Best_guy).getGene(k));
                /*LEARN_WEIGHTS_GA*/
                if (this.learnWeights) {
                    found = found &&
                            (New.get(i).getPeso(k) ==
                             Old.get(Best_guy).getPeso(k));
                }
            }
        }

        /* If the best chromosome has not remained, we replace the last one of the population for the former */
        if (!found) {
            for (k = 0; k < subEspacio.size(); k++) {
                New.get(populationSize - 1).setGene(k,
                        Old.get(Best_guy).getGene(k));
                /*LEARN_WEIGHTS_GA*/
                if (this.learnWeights) {
                    New.get(populationSize - 1).setPeso(k,
                            Old.get(Best_guy).getPeso(k));
                }
            }
            New.get(populationSize - 1).Perf = Old.get(Best_guy).Perf;
            New.get(populationSize - 1).evaluado();
        }
    }

    public static int dameTrials(){
        return Trials_mejor;
    }

}

