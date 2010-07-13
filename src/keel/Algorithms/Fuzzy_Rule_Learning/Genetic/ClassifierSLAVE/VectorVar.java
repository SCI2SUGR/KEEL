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

import java.util.*;

public class VectorVar {
/**
 * <p>
 * Defines a list of variables
 * </p>
 */
 	
    int numero;
    variable_t[] lista;

    /**
     * <p>
     * Default Constructor
     * </p>
     */
    VectorVar() {
        numero = 0;
        lista = new variable_t[0];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param tamano int The number of variables in the list
     */
    VectorVar(int tamano) {
        numero = tamano;
        lista = new variable_t[tamano];
    }


    /**
     * <p>
     * Creates a list of variables as a copy of another list
     * </p>
     * @param x VectorVar The list of variable used to create the new one
     */
    VectorVar(VectorVar x) {
        numero = x.numero;
        lista = new variable_t[numero];
        for (int i = 0; i < numero; i++) {
            lista[i] = new variable_t(x.lista[i]);
        }
    }


    /**
     * <p>
     * Creates a list of variables using the information in a set of examples
     * </p>
     * @param dataset myDataset The set of examples
     * @param num_etiquetas int The number of label per each variable in the list
     */
    VectorVar(myDataset dataset, int num_etiquetas) {
        String nom_var;
        int n_var_entradas, n_clases;
        boolean antecedente, activa;

        activa = true;

        numero = dataset.getnVars();
        lista = new variable_t[numero];
        for (int i = 0; i < numero; i++) {
            lista[i] = new variable_t();
        }

        double[] min = new double[numero];
        double[] max = new double[numero];

        min = dataset.getemin();
        max = dataset.getemax();

        n_var_entradas = dataset.getnInputs();

        for (int j = 0; j < n_var_entradas; j++) {
            nom_var = "X" + j;
            lista[j].Set(num_etiquetas, min[j], max[j], false, false,
                            nom_var);
//            lista[j].PrintDefinition();
        }

        // Now the class
        n_clases = dataset.getnClasses();

        double[] clases_num = new double[n_clases];
        String[] clases_nomb = new String[n_clases];

        for (int i = 0; i < n_clases; i++) {
            clases_nomb[i] = dataset.getOutputValue(i);
            clases_num[i] = (double) i;
        }

        nom_var = "Class";
        lista[n_var_entradas].Set(nom_var, n_clases, clases_nomb, clases_num);
//        lista[n_var_entradas].PrintDefinition();
    }


	/**
	 * <p>
	 * Copies the variable "x" in the position "pos" of the list
	 * </p>
	 * @param pos int The position in the list
	 * @param x variable_t The variable
	 */
    public void Set(int pos, variable_t x) {
        if (pos < numero) {
            lista[pos] = new variable_t(x);
        } else {
            System.out.println("The position does not exist");
        }
    }

	/**
	 * <p>
	 * It returns the number of antecedents in the list of variables
	 * </p>
	 * @return int The number of antecedents in the list of variables
	 */
    public int N_Antecedents() {
        int s = 0;
        for (int i = 0; i < numero; i++) {
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                s++;
            }
        }

        return s;
    }

    /*    public void Encode(Integer tb, Integer te) {
     System.out.println("Tama[0]: "+tb.intValue()+", Rango[0]:"+te.intValue());
            int tb_aux, te_aux;

            tb_aux = tb.intValue();
            te_aux = te.intValue();
            tb_aux = 0;
            for (int i = 0; i < numero; i++) {
                if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                    tb_aux += lista[i].N_labels();
                } else if (lista[i].IsActive() && !lista[i].IsAntecedent()) {
                    te_aux = lista[i].N_labels();
                }
            }

            tb = Integer.valueOf(tb_aux);
            te = Integer.valueOf(te_aux);
     System.out.println("Tama[0]: "+tb.intValue()+", Rango[0]:"+te.intValue());
        }
     */

	/**
	 * <p>
	 * Obtains the number of active antecedent variables and consequent variables in the list
	 * </p>
	 * @param milista ArrayList<Integer> It keeps the the number of active antecedent variables and consequent variables in the list
	 */
    public void Encode(ArrayList<Integer> milista) {
        Integer tb, te;
        tb = milista.get(0);
        te = milista.get(1);
//        System.out.println("Tama[0]: "+tb.intValue()+", Rango[0]:"+te.intValue());
        int tb_aux, te_aux;

        tb_aux = tb.intValue();
        te_aux = te.intValue();
        tb_aux = 0;
        for (int i = 0; i < numero; i++) {
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                tb_aux += lista[i].N_labels();
            } else if (lista[i].IsActive() && !lista[i].IsAntecedent()) {
                te_aux = lista[i].N_labels();
            }
        }

        tb = Integer.valueOf(tb_aux);
        te = Integer.valueOf(te_aux);
//        System.out.println("Tama[0]: "+tb.intValue()+", Rango[0]:"+te.intValue());

        milista.add(0, tb);
        milista.add(1, te);
    }


	/**
	 * <p>
	 * Prints in the standard output the definition of the variable in position "variable" in the list
	 * </p>
	 */
    public void PrintDefinition(int variable) {
        if (variable >= 0 && variable < numero) {
            lista[variable].PrintDefinition();
        } else {
            System.out.println("That variable does not exist");
        }
    }

	/**
	 * <p>
	 * Prints in a String the definition of all the variables in the list
	 * </p>
	 * @return String The definition of all the variables in the list
	 */
    public String PrintDefinitionToString() {
        String cadena = "";
        for (int i = 0; i < numero; i++) {
            cadena += lista[i].PrintDefinitionToString() + "\n";
        }
        return (cadena);
    }

	/**
	 * <p>
	 * Prints in the standard output the definition of all the variables in the list
	 * </p>
	 */
    public void PrintDefinition() {
        for (int i = 0; i < numero; i++) {
            lista[i].PrintDefinition();
        }
    }

	/**
	 * <p>
	 * Prints in the standard output the name of the variable in position "variable" in the list
	 * </p>
	 */
    public void PrintVar(int variable) {
        lista[variable].PrintVar();
    }

	/**
	 * <p>
	 * Returns a string with the name of the variable in position "variable" in the list
	 * </p>
	 * @return String The name of the variable in position "variable" in the list
	 */
    public String SPrintVar(int variable) {
        return lista[variable].SPrintVar();
    }

	/**
	 * <p>
	 * Prints in the standard ouput the name of the label "value" of the variable in position "variable" in the list.
	 * </p>
	 * @param variable int The variable in position in the list	 
	 * @param value int The label of the variable
	 */
    public void PrintDomain(int variable, int value) {
        lista[variable].PrintDomain(value);
    }

	/**
	 * <p>
	 * Returns a string with the name of the label "value" of the variable in position "variable" in the list.
	 * </p>
	 * @param variable int The variable in position in the list		 
	 * @param value int The label of the variable
	 * @return String The name of the label "value" of the variable.	 
	 */
    public String SPrintDomain(int variable, int value) {
        return lista[variable].SPrintDomain(value);
    }

	/**
	 * <p>
	 * Returns if the variable in position "variable" is considered in the learning process.
	 * </p>
	 * @param variable int The variable in position in the list	 
	 * @return boolean TRUE is the variable is considered in the learning process. FALSE otherwise
	 */
    public boolean IsActive(int variable) {
        return lista[variable].IsActive();
    }

	/**
	 * <p>
	 * Retuns if the variable in position "variable" is an antecedent of the rule.
	 * </p>
	 * @param variable int The variable in position in the list	 
	 * @return boolean TRUE is the variable is an antecedent of the rule. FALSE otherwise	 
	 */
    public boolean IsAntecedent(int variable) {
        return lista[variable].IsAntecedent();
    }

	/**
	 * <p>
	 * Retuns the number of variables in the list
	 * </p>
	 * @return int The number of variables in the list
	 */
    public int TotalVariables() {
        return numero;
    }

	/**
	 * <p>
	 * Retuns the number of labels in the variable in position "variable" of the list domain.
	 * </p>
	 * @param variable int The variable in position in the list		 
	 * @return int The number of labels in the variable's domain.
	 */
    public int SizeDomain(int variable) {
        return lista[variable].SizeDomain();
    }

	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to the variable in position "variable" of the list.
	 * </p>
	 * @param x double The value
	 * @param variable int The variable in position in the list		 	 
	 * @return double the adaptation degree of a value x to the domain of the variable.	 
	 */
    public double Adaptation(double x, int variable) {
        if (variable >= 0 && variable < numero) {
            return lista[variable].Adaptation(x);
        } else {
            System.out.println("That variable does not exist");
            return ( -1);
            //exit(1);
        }
    }

	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to the label "dominio" of the variable in position "variable" of the list.
	 * </p>
	 * @param x double The value
	 * @param variable int The variable in position in the list		 
	 * @param dominio int The label in the domain	 
	 * @return double the adaptation degree of a value x to the domain of the variable.	 
	 */
    public double Adaptation(double x, int variable, int dominio) {
        if (variable >= 0 && variable < numero) {
            return lista[variable].Adaptation(x, dominio);
        } else {
            System.out.println("That variable does not exist");
            return ( -1);
            //exit(1);
        }
    }


	/**
	 * <p>
	 * Returns the adaptation degree of a certain value x to a set of label "dominio" of the variable in position "variable" of the list.
	 * Param "dominio" is given as an ordered vector (String of strings) with zeros and ones
	 * representing the absence or the presence, respectively.
	 * </p>
	 * @param x double The value
	 * @param variable int The variable in position in the list		 
	 * @param dominio String The set of labels in the domain 
	 * @return double the adaptation degree of a value x to the domain.	 
	 */
    public double Adaptation(double x, int variable, String dominio) {
        if (variable >= 0 && variable < numero) {
            return lista[variable].Adaptation(x, dominio);
        } else {
            System.out.println("That variable does not exist");
            return ( -1);
            //exit(1);
        }

    }

	/**
	 * <p>
	 * Returns the adaptation degree of set of values in "x" to a set rules enconded in a String
	 * </p>
	 * @param x vectordouble The vector of values
	 * @param regla String The set of rules
	 * @return double the adaptation degree.	 
	 */
    public double Adaptation(vectordouble x, String regla) {
        double max = 1, aux;
        String sub;
        int trozo = 0, tam;
        for (int i = 0; i < numero && max > 0; i++) {
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                tam = lista[i].N_labels();
                sub = regla.substring(trozo, trozo+tam);
                aux = lista[i].Adaptation(x.At(i), sub);
                if (aux < max) {
                    max = aux;
                }
                trozo += tam;
            }
        }

        return max;
    }

    private int NumActiveLabels(String sub, int tam) {
        int n = 0;
        char[] array_sub = new char[sub.length()];

        array_sub = sub.toCharArray();

        for (int i = 0; i < tam; i++) {
            if (array_sub[i] == '1') {
                n++;
            }
        }
        return n;
    }

    private void SequenceOfActiveLabels(String sub, int tam, ArrayList<int[]> milista) {
        int[] list = new int[3];
        list = milista.get(0);
        int unos, ceros, n_unos;
        char[] array_sub = new char[sub.length()];

        array_sub = sub.toCharArray();

        int n = 0;
        unos = list[0];
        ceros = list[1];
        n_unos = list[2];
        boolean last_uno;
        int i = 1;
        if (array_sub[0] == '0') {
            ceros++;
            last_uno = false;
        } else {
            unos++;
            last_uno = true;
            n_unos++;
        } while (i < tam) {
            if (array_sub[i] == '1') {
                n_unos++;
            }
            if (last_uno && array_sub[i] == '0') {
                last_uno = false;
                ceros++;
            } else if (!last_uno && array_sub[i] == '1') {
                last_uno = true;
                unos++;
            }
            i++;
        }

        list[0] = unos;
        list[1] = ceros;
        list[2] = n_unos;

      milista.add(0, list);
    }

	/**
	 * <p>
	 * Returns if the set rules encoded in the String "regla" is valid or not. Its simplicity is also calculated
	 * </p>
	 * @param regla String The set of rules
	 * @param var double[] Contains the information measure for each variable
	 * @param umbral double Activation threshold (only variable with its information measure equal o greater than this value are considered in the rule).
	 * @param milista ArrayList<Double> Keeps the simplicity of the rule	 
	 * @return boolean TRUE if all the rules in the set of rules are valid. FALSE otherwise
	 */
    public boolean Is_Valid(String regla, double[] var, double umbral,
                             ArrayList<Double> milista) {
        Double aux1 = milista.get(0);
        double simplicidad = aux1.doubleValue();
        String sub;
        int trozo = 0, tam;

        int [] list = new int[3];
        for(int i = 0; i < 3; i++){
            list[i] = 0;
        }

        int unos, ceros, n_unos;

        int i = 0;
        simplicidad = 0.0;
        boolean valida = true;
        while (i < numero && valida) {
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                tam = lista[i].N_labels();
                if (var[i] >= umbral) {
                    sub = regla.substring(trozo, trozo+tam);
                    //unos = NumActiveLabels(sub,tam);
                    ArrayList<int[]> lista1 = new ArrayList<int[]>(1);
                    lista1.add(list);

                    SequenceOfActiveLabels(sub, tam, lista1);

                    list = lista1.get(0);
                    unos = list[0];
                    ceros = list[1];
                    n_unos = list[2];

                    valida = (unos != 0);
                    if (valida) {
                        if (unos == 1 || ceros == 1) {
                            simplicidad = simplicidad + 1.0;
                        }
                    }
                }
                trozo += tam;
            }
            i++;
        }

        aux1 = Double.valueOf(simplicidad);
        milista.add(0, aux1);

        return valida;
    }

	/**
	 * <p>
	 * Returns the adaptation degree of set of values in "x" to a set rules enconded in a String, taking into account the activation
	 * threshold for the variables
	 * </p>
	 * @param x vectordouble The vector of values	 
	 * @param regla String The set of rules
	 * @param var double[] Contains the information measure for each variable
	 * @param umbral double Activation threshold (only variable with its information measure equal o greater than this value are considered in the rule).
	 * @return double the adaptation degree.
	 */
    public double Adaptation(vectordouble x, String regla, double[] var,
                             double umbral) {
        double max = 1, aux;
        String sub;
        int trozo = 0, tam, unos;
        for (int i = 0; i < numero && max > 0; i++) {
//            System.out.println("Trozo es "+trozo+", Tam es "+lista[i].N_labels()+" y Regla mide "+regla.length());
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                tam = lista[i].N_labels();
                if (var[i] >= umbral) {
//                    System.out.println("Regla: " + regla+", ("+trozo+","+tam+")");
                    sub = regla.substring(trozo, trozo+tam);
//                    System.out.println("Sub: " + sub);
                    unos = NumActiveLabels(sub, tam);
                    if (unos == 0) {
                        max = 0;
                    } else if (unos < tam) {
                        aux = lista[i].Adaptation(x.At(i), sub);
                        if (aux < max) {
                            max = aux;
                        }
                    }
                }
                trozo += tam;
            }
        }

        return max;
    }

	/**
	 * <p>
	 * Returns the adaptation degree of set of values in "x" to a set rules enconded in a String, taking into account the activation
	 * threshold for the variables
	 * </p>
	 * @param x vectordouble The vector of values	 
	 * @param regla String The set of rules
	 * @param var double[] Contains the information measure for each variable
	 * @param umbral double Activation threshold (only variable with its information measure equal o greater than this value are considered in the rule).
	 * @param umbral2 double Minimum adaptation threshold.	 
	 * @return double The adaptation degree if it is greater or equal to the minium adaptation threshold. 0 otherwise.
	 */
    public double Adaptation(vectordouble x, String regla, double[] var,
                             double umbral, double umbral2) {
        double max = 1, aux;
        String sub;
        int trozo = 0, tam, unos;

        if (umbral2 < 0) {
            umbral2 = -umbral2;
        }

        for (int i = 0; i < numero && max >= umbral2 && max > 0; i++) {
            if (lista[i].IsActive() && lista[i].IsAntecedent()) {
                tam = lista[i].N_labels();
                if (var[i] >= umbral) {
                    sub = regla.substring(trozo, trozo+tam);
                    unos = NumActiveLabels(sub, tam);
                    if (unos == 0) {
                        max = 0;
                    } else if (unos < tam) {
                        aux = lista[i].Adaptation(x.At(i), sub);
                        if (aux < max) {
                            max = aux;
                        }
                    }
                }
                trozo += tam;
            }
        }

        if (max >= umbral2) {
            return max;
        } else {
            return 0;
        }
    }


	/**
	 * <p>
	 * Calculates the adaptation degree of set of values in "x" to a certain label "etiq". Also calculates the adaptation
	 * degree to its complementary, that is, the rest of labels in the variable (not including "etiq").
	 * </p>
	 * @param x vectordouble The vector of values	 
	 * @param etiq int The label in the variable
	 * @param milista ArrayList <double[]> Keeps the adaptation to the label and its complementary value
	 */
    public void AdaptationC(vectordouble x, int etiq, ArrayList<double[]> milista){
        double[] aux1 = new double[2];
        aux1 = milista.get(0);
        double pos = aux1[0];
        double neg = aux1[1];

        double valor, aux;
        int i = 0;
        while (lista[i].IsAntecedent() && i < numero - 1) {
            i++;
        }

        valor = x.At(i);
        pos = lista[i].Adaptation(valor, etiq);
        neg = 0.0;
        for (int j = 0; j < lista[i].N_labels(); j++) {
            if (j != etiq) {
                aux = lista[i].Adaptation(valor, j);
                if (aux > neg) {
                    neg = aux;
                }
            }
        }

        aux1[0] = pos;
        aux1[1] = neg;

        milista.add(0, aux1);
    }

	/**
	 * <p>
	 * Returns the area of the label number "lab" in the variable in position "var" of the list.
	 * </p>
	 * @param var int The position of the variable	 
	 * @param lab int The label number
	 * @return double The area of the label number "l" in the variable in position "var" of the list.
	 */
    public double Area(int var, int lab) {
        return lista[var].Area(lab);
    }

	/**
	 * <p>
	 * Returns a fuzzy_t object with the definition of the label number "lab" in the variable in position "var" of the list.
	 * </p>
	 * @param var int The position of the variable	 
	 * @param lab int The label number
	 * @return fuzzy_t A fuzzy_t object with the definition of the label number "lab" in the variable in position "var" of the list.
	 */
    public fuzzy_t FuzzyLabel(int var, int lab) {
        return lista[var].FuzzyLabel(lab);
    }

	/**
	 * <p>
	 * Returns the central value of the label number "lab" in the variable in position "var" of the list.
	 * </p>
	 * @param var int The position of the variable	 
	 * @param lab int The label number
	 * @return double The central value of the label number "lab" in the variable in position "var" of the list.
	 */
    public double CenterLabel(int var, int lab) {
        return lista[var].CenterLabel(lab);
    }

	/**
	 * <p>
	 * Returns if the domain associated to the variable in position "var" of the list is only formed by crisp values.
	 * </p>
	 * @param var int The position of the variable	 	 
	 * @return TRUE if the domain associated to the variable in position "var" of the list is only formed by crisp values. FALSE otherwise
	 */
    public boolean IsDiscrete(int var) {
        return lista[var].IsDiscrete();
    }

	/**
	 * <p>
	 * Returns if the domain associated to the variable in position "var" of the list is only formed by intervals.
	 * </p>
	 * @param var int The position of the variable	 	 
	 * @return TRUE if the domain associated to the variable in position "var" of the list is only formed by intervals. FALSE otherwise
	 */
    public boolean IsInterval(int var) {
        return lista[var].IsInterval();
    }

	/**
	 * <p>
	 * Returns if the domain associated to the variable in position "var" of the list is only formed by fuzzy sets.
	 * </p>
	 * @param var int The position of the variable	 		 
	 * @return TRUE if the domain associated to the variable in position "var" of the list is only formed by fuzzy sets. FALSE otherwise
	 */
    public boolean IsFuzzy(int var) {
        return lista[var].IsFuzzy();
    }


	/**
	 * <p>
	 * Creates a new domain_t object containing the domain of the variable in position "var" of the list
	 * </p>
	 * @param var int The position of the variable	 		 
	 * @return domain_t A new domain_t object containing the domain of the variable in position "var" of the list
	 */
    public domain_t Domain(int var) {
        domain_t aux = new domain_t(lista[var].Domain());
        return aux;
    }

	/**
	 * <p>
	 * Creates a new variable_t object containing the variable in position "var" of the list
	 * </p>
	 * @param var int The position of the variable	 		 
	 * @return domain_t A new varible_t object containing the variable in position "var" of the list
	 */
    public variable_t Variable(int var) {
        variable_t aux = new variable_t(lista[var].Variable());
        return aux;
    }

	/**
	 * <p>
	 * Returns the lower value of the definition interval of variable in position "var" of the list
	 * </p>
	 * @param var int The position of the variable	 	 
	 * @return double The lower value of the definition interval of variable in position "var" of the list
	 */
    public double Inf_Range(int var) {
        return lista[var].Inf_Range();
    }

	/**
	 * <p>
	 * Returns the upper value of the definition interval of variable in position "var" of the list
	 * </p>
	 * @param var int The position of the variable		 
	 * @return double The upper value of the definition interval of variable in position "var" of the list
	 */
    public double Sup_Range(int var) {
        return lista[var].Sup_Range();
    }

	/**
	 * <p>
	 * Returns the position of the consequent inside the list of variables
	 * </p>
	 * @return int The position of the consequent inside the list of variables
	 */	 
    public int Consequent() {
        int i = 0;
        while ((lista[i].IsAntecedent() || !lista[i].IsActive()) && i < numero) {
            i++;
        }

        if (i != numero) {
            return i;
        } else {
            System.out.println("There is not consequent variable");
            return ( -1);
        }
    }

}

