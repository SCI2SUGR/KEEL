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

package keel.Algorithms.Genetic_Rule_Learning.LogenPro;

import org.core.Randomize;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 01/01/2007
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
public class Individual implements Comparable {
/**	
 * <p>
 * An individual of the population
 * </p>
 */
 
    Condition[] Arbol;
    boolean[] tokens;
    String clase;
    int fp, tp, fn, tn;
    boolean n_e;
    double fitness;
    double soporte_min, w1, w2;
    int num_emparejados;
    int num_anys;

    /**
     * <p>
     * Constructor
     * </p>
     * @param dataset myDataset Set of examples
     * @param min_support double Minimun support parameter for the fitness function
     * @param w1 double Weight of the Confidence factor in the fitness function
     * @param w2 double Weight of the Support factor in the fitness function     
     */
    public Individual(myDataset dataset, double min_support, double w1,
                     double w2) {
        int variables = dataset.getnInputs();
        Arbol = new Condition[variables];
        num_anys = 0;
        for (int i = 0; i < variables; i++) {
            assignCondition(i, dataset);
        }
        clase = dataset.getOutputValue(Randomize.RandintClosed(0, dataset.getnClasses()));
        soporte_min = min_support;
        this.w1 = w1;
        this.w2 = w2;
        n_e = true;
        tokens = new boolean[dataset.getnData()];
    }

    /**
     * <p>
     * Determines if an example is covered by the individual
     * </p>
     * @param ejemplo double[] Example to compare
     * @param perdidos boolean [] True in those values of the example which are missing-values
     * @return boolean TRUE if the individual matches the example. FALSE otherwise.
     */
    public boolean matching(double[] ejemplo, boolean [] perdidos) {
        boolean salida = true;

        for (int i = 0; i < ejemplo.length && salida == true; i++) {
            salida = Arbol[i].matching(ejemplo[i], perdidos[i]);
        }

        return (salida);
    }

    /**
     * <p>
     * Evaluates (calculate the fitness of) the individual
     * </p>
     * @param dataset myDataset Set of examples
     */
    public void evaluate(myDataset dataset) {
        num_emparejados = 0;
        int num_correctamente_emparejados = 0;
        int ejemplos_clase_individuo = dataset.numberInstances(this.clase);

        for (int i = 0; i < dataset.getnData(); i++) {
            tokens[i] = false;
            if (this.matching(dataset.getExample(i), dataset.getMissing(i))) {
                num_emparejados++;
                tokens[i] = true;
                if (this.clase.equalsIgnoreCase(dataset.getOutputAsString(i))) {
                    num_correctamente_emparejados++;
                }
            }
        }

        double support = (double) num_emparejados / dataset.getnData();
        double normalized_cf = 0.0;
        double prob = (double) ejemplos_clase_individuo /
                      dataset.getnData();
        if (num_correctamente_emparejados > 0) {
            double cf = (double) num_correctamente_emparejados /
                        num_emparejados;
            normalized_cf = cf * Math.log(cf / prob);

        }

        if (support < soporte_min) {
            fitness = support;
        } else {
            fitness = (w1 * support) + (w2 * normalized_cf);
        }
        n_e = false;
    }

    /**
     * <p>
     * Returns the class in the consequent of the individual
     * </p>
     * @return String The name of the class in the consequent of the individual
     */
    public String getClase() {
        return (this.clase);
    }

    /**
     * <p>
     * Prints the tree encoded in the individual as a string
     * </p>
     * @return String Contains the tree encoded in the individual
     */
    public String printIndividual_bk() {
        // Se imprime el IF
        String regla = new String("IF ");
        for (int i = 0; i < Arbol.length - 1; i++) {
            regla += Arbol[i].print();
            regla += "AND";
        }
        regla += Arbol[Arbol.length - 1].print();
        regla += ", Class = " + clase + ", " + fitness;
        return regla;
    }

    /**
     * <p>
     * Prints the tree encoded in the individual as a string
     * </p>
     * @return String Contains the tree encoded in the individual
     */
    public String printIndividual() {
        String regla = new String("");
        for (int i = 0; i < Arbol.length - 1; i++) {
            regla += Arbol[i].print();
            if (Arbol[i].getType() != Arbol[i].ANY){
                regla += "AND";
            }
        }
        regla += Arbol[Arbol.length - 1].print();
        regla += ": " + clase + ", Fitness: " + fitness;
        return regla;
    }

    /**
     * <p>
     * It assigns a condition to the atribute in position "atributo"
     * </p>
     * @param atributo int The position of the attribute
     * @param dataset myDataset The set of examples
     */
    private void assignCondition(int atributo, myDataset dataset) {
        if ((Randomize.Rand() > 0.5) && (num_anys < dataset.getnInputs() - 1)) {
            Condition c = new Condition(0, Condition.ANY, dataset.nameVar(atributo));
            Arbol[atributo] = c;
            num_anys++;
        } else {
            assignConditionNoAny(atributo, dataset);
        }
    }

    /**
     * <p>
     * It check it is possible to apply the dropping condition operator, which only can be applied if 
     * the number of "ANY" condition is the tree is less than number of input variables minus one.
     * </p>
     * @param atributos int Number of input variables
     * @return boolean TRUE is if posible to apply the dropping condition operator. FALSE otherwise.
     */
    public boolean applicableDropping(int atributos) {
        return (num_anys < (atributos - 1));
    }

    /**
     * <p>
     * It assigns a condition to the atribute in position "atributo", but different to "ANY"
     * </p>
     * @param atributo int The position of the attribute
     * @param dataset myDataset The set of examples
     */	
    public void assignConditionNoAny(int atributo, myDataset dataset) {
        if (dataset.getType(atributo) == dataset.REAL) {
            int tipo = Randomize.RandintClosed(2, Condition.TIPOMAX + 1);
            if (tipo == Condition.ENTRE) {
                double valor1 = Randomize.RanddoubleClosed(dataset.
                        getMin(atributo), dataset.getMax(atributo) + 1);
                double valor2 = Randomize.RanddoubleClosed(dataset.
                        getMin(atributo), dataset.getMax(atributo));
                if (valor1 > valor2) {
                    Condition c = new Condition(valor2, valor1,dataset.nameVar(atributo));
                    Arbol[atributo] = c;
                } else {
                    Condition c = new Condition(valor1, valor2,dataset.nameVar(atributo));
                    Arbol[atributo] = c;
                }
            } else {
                double valor1 = Randomize.RanddoubleClosed(dataset.
                        getMin(atributo), dataset.getMax(atributo));
                Condition c = new Condition(valor1, tipo,dataset.nameVar(atributo));
                Arbol[atributo] = c;
            }
        } else if (dataset.getType(atributo) == dataset.INTEGER) {
            int tipo = Randomize.RandintClosed(0, Condition.TIPOMAX + 1);
            if (tipo == Condition.ENTRE) {
                double valor1 = Randomize.RandintClosed((int) dataset.
                        getMin(atributo), (int) dataset.getMax(atributo) + 1);
                double valor2 = Randomize.RandintClosed((int) dataset.
                        getMin(atributo), (int) dataset.getMax(atributo) + 1);
                if (valor1 > valor2) {
                    Condition c = new Condition(valor2, valor1,dataset.nameVar(atributo));
                    Arbol[atributo] = c;
                } else {
                    Condition c = new Condition(valor1, valor2,dataset.nameVar(atributo));
                    Arbol[atributo] = c;
                }
            } else {
                double valor1 = Randomize.RandintClosed((int) dataset.
                        getMin(atributo), (int) dataset.getMax(atributo) + 1);
                Condition c = new Condition(valor1, tipo,dataset.nameVar(atributo));
                Arbol[atributo] = c;
            }
        } else {
            double valor = Randomize.RandintClosed((int) dataset.
                    getMin(atributo), (int) dataset.getMax(atributo) + 1);
            Condition c = new Condition(valor, Condition.IGUAL,dataset.nameVar(atributo));
            Arbol[atributo] = c;
        }
    }

    /**
     * <p>
     * It changes the class in the consequent of the individual by a new one
     * </p>
     * @param dataset myDataset The set of examples
     */	
    public void assignNewClass(myDataset dataset) {
        String clase_old = this.clase;
        String clase_new = this.clase;

        do {
            clase_new = dataset.getOutputValue(Randomize.RandintClosed(0, dataset.getnClasses()));
        } while (clase_old == clase_new);

        this.clase = clase_new;
        //System.out.println("Changing Class");
    }

    /**
     * <p>
     * It sets the condition for the variable in position "atributo" to ANY
     * </p>
     * @param atributo int The position of the variable
     * @param nombre String The name of the variable
     */	
    public void setAny(int atributo, String nombre) {
        Condition c = new Condition(0, Condition.ANY, nombre);
        Arbol[atributo] = c;
        num_anys++;
    }

    /**
     * <p>
     * It returns the condition for the variable in position "atributo"
     * </p>
     * @param atributo int The position of the variable
     * @return Condition Condition for the variable
     */	
    public Condition getCondition(int atributo) {
        return Arbol[atributo].clone();
    }

    /**
     * <p>
     * It sets to "c" the condition for the variable in position "atributo"
     * </p>
     * @param atributo int The position of the variable
     * @param c Condition Condition for the variable
     */	
    public void setCondition(int atributo, Condition c) {
        Arbol[atributo] = c;
    }

    /**
     * <p>
     * Constructor (create the individual as a copy of another individual)
     * </p>
     * @param padre Individual The individual used to assign the values to the new one
     */
    public Individual(Individual padre) {
        Arbol = new Condition[padre.Arbol.length];
        for (int i = 0; i < Arbol.length; i++) {
            Arbol[i] = padre.getCondition(i);
        }
        n_e = false;
        clase = padre.getClase();
        soporte_min = padre.soporte_min;
        w1 = padre.w1;
        w2 = padre.w2;
        tokens = new boolean[padre.tokens.length];
    }

    /**
     * <p>
     * It returns if the individual has been evaluated o not
     * </p>
     * @return boolean TRUE is the individual has been evaluated. FALSE otherwise. 
     */
    public boolean non_evaluated() {
        return n_e;
    }

    /**
     * <p>
     * It returns the number of examples that the individual matches
     * </p>
     * @return int Number of matched examples by the individual
     */
    public int ideal() {
        return this.num_emparejados;
    }

    /**
     * <p>
     * It sets the fitness of the individual to "fitness"
     * </p>
     * @param fitness double A fitness value
     */
    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    /**
     * <p>
     * It returns the fitness of the individual
     * </p>
     * @return double The fitness value of the individual
     */
    public double getFitness() {
        return this.fitness;
    }

    /**
     * <p>
     * It checks is the example in position "idEjemplo" is covered by the individual
     * </p>
     * @return boolean TRUE if the example in position "idEjemplo" is covered. FALSE otherwise
     */
    public boolean isCovered(int idEjemplo) {
        return tokens[idEjemplo];
    }

    /**
     * <p>
     * It checks is the condition for the variable in position "atributo" is equal to ANY
     * </p>
     * @return boolean TRUE if the condition is ANY. FALSE otherwise
     */
    public boolean isAny(int atributo) {
        return (Arbol[atributo].getType() == Condition.ANY);
    }

    /**
     * <p>
     * It generates a tree that matches the given example
     * </p>
     * @param example double[] The given example
     * @param train myDataset The set of examples
     */
    public void replace(double example[], myDataset train) {
        n_e = true;
        fitness = 0.0;
        for (int i = 0; i < train.getnInputs(); i++) {
            if (train.getType(i) == train.NOMINAL) {
                Condition c = new Condition(example[i], Condition.IGUAL, train.nameVar(i));
            } else {
                double aleat1 = train.getMin(i);
                double aleat2 = train.getMax(i);
                if (example[i] == train.getMax(i)) {
                    Condition c = new Condition(example[i],
                                                Condition.MENORIGUAL, train.nameVar(i));
                    Arbol[i] = c;
                } else if (example[i] == train.getMin(i)) {
                    Condition c = new Condition(example[i],
                                                Condition.MAYORIGUAL, train.nameVar(i));
                    Arbol[i] = c;
                } else {
                    do {
                        aleat1 = Randomize.RanddoubleClosed(train.getMin(i),
                                train.getMax(i));
                    } while (aleat1 >= example[i]);
                    do {
                        aleat2 = Randomize.RanddoubleClosed(train.getMin(i),
                                train.getMax(i));
                    } while (aleat2 <= example[i]);
                }
                Condition c = new Condition(aleat1, aleat2, train.nameVar(i));
                Arbol[i] = c;

            }
        }

    }

    /**
     * <p>
     * Compares the fitness value of two individuals
     * </p>
     * @return int Returns -1 if the the fitness of the first individual is lesser than the fitness of the second one.
     * 1 if the the fitness of the first individual is greater than the fitness of the second one.
     * 0 if both individuals have the same fitness value
     */
    public int compareTo(Object a) {
        if (((Individual) a).fitness < this.fitness) {
            return -1;
        }
        if (((Individual) a).fitness > this.fitness) {
            return 1;
        }
        return 0;
    }

}

