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
 
public class ruleset {
/**
 * <p>
 * Encodes a set of rules
 * </p>
 */
 	
    int reservado;
    int n_rule;
    genetcode[] rule;
    VectorVar domain;
    double[] peso;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    ruleset(){
     reservado=10;
     n_rule=0;
     rule= new genetcode[reservado];
     peso= new double[reservado];
     domain= new VectorVar();
    }


    /**
     * <p>
     * Creates a ruleset only having its vector of variable defined
     * </p>
     * @param x VectorVar The vector of variables
     */
    ruleset(VectorVar x){
     reservado=10;
     n_rule=0;
     rule = new genetcode [reservado];
     peso= new double[reservado];
     domain = new VectorVar(x);
    }

    /**
     * <p>
     * Creates a ruleset as a copy of another one
     * </p>
     * @param x ruleset The ruleset used to created the new one 
     */
    ruleset(ruleset x){
     this.reservado=x.reservado;
     this.n_rule=x.n_rule;
     this.rule = new genetcode[reservado];
     this.peso= new double[reservado];
     for (int i=0; i<n_rule; i++){
        this.rule[i] = new genetcode(x.rule[i]);
        this.peso[i] = x.peso[i];
     }
    }

	/**
	 * <p>
	 * Adds the domain to a set of rules
	 * Pre: Use it only one time with a ruleset created by the default constructor
	 * </p>
     	 * @param x VectorVar The vector of variables
     	 */
    void AddDomain(VectorVar x){
      domain = new VectorVar(x);
    }


    private void Realloc(){
      genetcode[] aux = new genetcode[reservado];
      double[] p = new double[reservado];

      for (int i=0; i<n_rule; i++){
        aux[i]=rule[i];
        p[i]=peso[i];
      }

      rule = new genetcode[2*reservado+1];
      peso = new double[2*reservado+1];

      for (int i=0; i<n_rule; i++){
        rule[i] = aux[i];
        peso[i] = p[i];
      }

      reservado=2*reservado+1;
    }


	/**
	 * <p>
	 * Adds a new rule at the end of the ruleset
	 * </p>
	 * @param x genetcode The new rule
	 */
    void Add(genetcode x) {
     if (reservado==n_rule){
       Realloc();
     }
     rule[n_rule] = new genetcode(x);
     peso[n_rule]=0;
     n_rule++;
    }


	/**
	 * <p>
	 * Adds a new rule at the end of the ruleset, with a weight "weight".
	 * </p>
	 * @param x genetcode The new rule
	 * @param weight double The weight of the new rule
	 */
    void Add(genetcode x, double weight) {
     if (reservado==n_rule){
       Realloc();
     }
     rule[n_rule] = new genetcode(x);
     peso[n_rule]=weight;
     n_rule++;
    }


	/**
	 * <p>
	 * Assigns a weight "weight" to the rule number "rule" in the ruleset.
	 * </p>
	 * @param rule int The number of rule in the ruleset
	 * @param weight double The weight of the new rule	 
	 */
    void Add_Weight(int rule, double weight) {
       peso[rule]=weight;
    }


	/**
	 * <p>
	 * Returns the weight of the rule number "rule" in the ruleset.
	 * </p>
	 * @param rule int The number of rule in the ruleset
	 * @return double The weight of the rule	 
	 */
    double Get_Weight(int rule){
      return peso[rule];
    }

	/**
	 * <p>
	 * Removes the last rule in the ruleset.
	 * </p>
	 */
    void Remove(){
       n_rule--;
    }


	/**
	 * <p>
	 * Extracts a rule from the ruleset
	 * </p>
	 * @param i int The number of rule in the ruleset
	 * @param milista ArrayList<Double> Keeps the weight of the extracted rule
	 * @return genetcode The rule
	 */
    genetcode Extract(int i, ArrayList<Double> milista){
      Double aux1 = milista.get(0);
      double weight = aux1.doubleValue();

      genetcode aux = new genetcode();
      if (i<n_rule){
        aux= new genetcode(rule[i]);
        weight=peso[i];
        for (int j=i+1; j<n_rule; j++){
           rule[j-1]=rule[j];
           peso[j-1]=peso[j];
        }
        n_rule--;
      }
      else
        weight=-1;

    aux1 = Double.valueOf(weight);
      milista.add(0, aux1);

      return aux;
    }

	/**
	 * <p>
	 * Inserts a rule in the ruleset
	 * </p>
	 * @param i int The position to insert the new rule in the ruleset
	 * @param r genetcode The rule to be inserted
	 * @param weight double The weight of the new rule 
	 */
    void Insert(int i, genetcode r, double weight){
      if (i<=n_rule){
        if (reservado==n_rule)
          Realloc();

        for (int j=n_rule; j>i; j--){
           rule[j]=rule[j-1];
           peso[j]=peso[j-1];
        }

        rule[i]= new genetcode(r);
        peso[i]=weight;
        n_rule++;
      }
    }

	/**
	 * <p>
	 * Returns the number of rules in the ruleset.
	 * </p>
	 * @return int The number of rules in the ruleset.
	 */
    int N_rule(){
      return n_rule;
    }


	/**
	 * <p>
	 * Returns the average number of variables per rule in the ruleset.
	 * </p>
	 * @return double The average number of variables per rule in the ruleset.
	 */
    double Variables_per_rule(){
     int r = 0;
     int[] nr = new int[0];
     double[][] mr = new double[0][0];
     double cont=0;

     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
         Integer aux1 = new Integer(r);
         lista1.add(aux1);

         ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
         lista2.add(nr);

         ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
         lista3.add(mr);

       rule[i].GetReal(lista1,lista2,lista3);

    aux1 = lista1.get(0);
    r = aux1.intValue();

    nr = lista2.get(0);

    mr = lista3.get(0);

       for (int j=0;j<nr[0]-1;j++)
         if (mr[0][j]>=mr[0][nr[0]-1])
           cont=cont+1;
     }

     return cont/n_rule;
    }


	/**
	 * <p>
	 * Returns the proportion of variables been used in the rules in relation with the global number of rules been used in the ruleset
	 * </p>
	 * @return double The proportion of variables been used in the rules in relation with the global number of rules been used in the ruleset
	 */
    double Variables_Used(){
     int r = 0;
     int[] nr = new int[0];
     int b = 0;
     int[] nb = new int[0];
     char[][] mb = new char[0][0];
     double[][] mr = new double[0][0];
     double cont=0;

     ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
     Integer aux1 = new Integer(r);
     lista1.add(aux1);

     ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
     lista2.add(nr);

     ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
     lista3.add(mr);

   rule[0].GetReal(lista1,lista2,lista3);

aux1 = lista1.get(0);
r = aux1.intValue();

nr = lista2.get(0);

mr = lista3.get(0);

     int[] used= new int[nr[0]];

     for (int j=0; j<nr[0]-1; j++)
       used[j]=0;

     int donde,z, n_casos;
     char[] s;
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista1b = new ArrayList<Integer>(1);
         Integer aux1b = new Integer(r);
         lista1b.add(aux1b);

         ArrayList<int[]> lista2b = new ArrayList<int[]>(1);
         lista2b.add(nr);

         ArrayList<double[][]> lista3b = new ArrayList<double[][]>(1);
         lista3b.add(mr);

       rule[i].GetReal(lista1b,lista2b,lista3b);

    aux1b = lista1b.get(0);
    r = aux1b.intValue();

    nr = lista2b.get(0);

mr = lista3b.get(0);


    ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
    Integer aux4 = new Integer(b);
    lista4.add(aux4);

    ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
    lista5.add(nb);

    ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
    lista6.add(mb);

  rule[i].GetBinary(lista4,lista5,lista6);

aux4 = lista4.get(0);
b = aux4.intValue();

nb = lista5.get(0);

mb = lista6.get(0);

       donde=0;
       s= new char[nb[0]+1];
       for (int j=0; j<nb[0]; j++)
         s[j]=mb[0][j];
       s[nb[0]]='\0';

       for (int j=0;j<nr[0]-1;j++){
         n_casos=domain.SizeDomain(j);
         for(z=0; z<n_casos && s[donde+z]=='1';z++);

         if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
           used[j]++;
         donde+=n_casos;
       }

     }

     for (int j=0; j<nr[0]-1; j++)
       if (used[j]>0)
         cont=cont+1;

     return cont;
    }


	/**
	 * <p>
	 * Returns the proportion of variables been used in the rules in relation with the global number of rules been used in the ruleset.
	 * It also retuns the number of times that each variable appears in the ruleset
	 * </p>
	 * @param milista ArrayList<int[]> Keeps the number of times that each variable appears in the ruleset
	 * @return double The proportion of variables been used in the rules in relation with the global number of rules been used in the ruleset	 
	 */
    double Frecuence_each_Variables(ArrayList<int[]> milista){
        int r = 0;
        int[] nr = new int[0];
        int b = 0;
        int[] nb = new int[0];
        char[][] mb = new char[0][0];
        double[][] mr = new double[0][0];
     double cont=0;

     ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
     Integer aux1 = new Integer(r);
     lista1.add(aux1);

     ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
     lista2.add(nr);

     ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
     lista3.add(mr);

   rule[0].GetReal(lista1,lista2,lista3);

aux1 = lista1.get(0);
r = aux1.intValue();

nr = lista2.get(0);

mr = lista3.get(0);

     int[] frec = new int[nr[0]];
     for (int j=0; j<nr[0]-1; j++)
       frec[j]=0;

     int donde,z, n_casos;
     char[] s;
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista1b = new ArrayList<Integer>(1);
         Integer aux1b = new Integer(r);
         lista1b.add(aux1b);

         ArrayList<int[]> lista2b = new ArrayList<int[]>(1);
         lista2b.add(nr);

         ArrayList<double[][]> lista3b = new ArrayList<double[][]>(1);
         lista3b.add(mr);

       rule[i].GetReal(lista1b,lista2b,lista3b);

    aux1b = lista1b.get(0);
    r = aux1b.intValue();

    nr = lista2b.get(0);

mr = lista3b.get(0);


    ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
    Integer aux4 = new Integer(b);
    lista4.add(aux4);

    ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
    lista5.add(nb);

    ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
    lista6.add(mb);

  rule[i].GetBinary(lista4,lista5,lista6);

aux4 = lista4.get(0);
b = aux4.intValue();

nb = lista5.get(0);

mb = lista6.get(0);

       donde=0;
       s= new char[nb[0]+1];
       for (int j=0; j<nb[0]; j++)
         s[j]=mb[0][j];
       s[nb[0]]='\0';

       for (int j=0;j<nr[0]-1;j++){
         n_casos=domain.SizeDomain(j);
         for(z=0; z<n_casos && s[donde+z]=='1';z++);

         if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
           frec[j]++;
         donde+=n_casos;
       }

     }

     for (int j=0; j<nr[0]-1; j++)
       if (frec[j]>0)
         cont=cont+1;


    milista.add(0, frec);

     return cont/(nr[0]-1);
    }


	/**
	 * <p>
	 * Returns the number of label that have been used in the rules
	 * </p>
	 * @return int The number of label that have been used in the rules
	 */
    int Labels_per_RB(){
            int r = 0;
            int[] nr = new int[0];
            int b = 0;
            int[] nb = new int[0];
            char[][] mb = new char[0][0];
            double[][] mr = new double[0][0];

            ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
            Integer aux1 = new Integer(r);
            lista1.add(aux1);

            ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
            lista2.add(nr);

            ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
            lista3.add(mr);

          rule[0].GetReal(lista1,lista2,lista3);

       aux1 = lista1.get(0);
       r = aux1.intValue();

       nr = lista2.get(0);

       mr = lista3.get(0);

     int donde,z, n_casos, medida=0;
     char[] s;
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista1b = new ArrayList<Integer>(1);
         Integer aux1b = new Integer(r);
         lista1b.add(aux1b);

         ArrayList<int[]> lista2b = new ArrayList<int[]>(1);
         lista2b.add(nr);

         ArrayList<double[][]> lista3b = new ArrayList<double[][]>(1);
         lista3b.add(mr);

       rule[i].GetReal(lista1b,lista2b,lista3b);

    aux1b = lista1b.get(0);
    r = aux1b.intValue();

    nr = lista2b.get(0);

mr = lista3b.get(0);


    ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
    Integer aux4 = new Integer(b);
    lista4.add(aux4);

    ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
    lista5.add(nb);

    ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
    lista6.add(mb);

  rule[i].GetBinary(lista4,lista5,lista6);

aux4 = lista4.get(0);
b = aux4.intValue();

nb = lista5.get(0);

mb = lista6.get(0);

       donde=0;
       s= new char[nb[0]+1];
       for (int j=0; j<nb[0]; j++)
         s[j]=mb[0][j];
       s[nb[0]]='\0';

       for (int j=0;j<nr[0]-1;j++){
         n_casos=domain.SizeDomain(j);
         for(z=0; z<n_casos && s[donde+z]=='1';z++);

         if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos)
           medida++;
         donde+=n_casos;
       }

     }


     return medida;

    }

	/**
	 * <p>
	 * Returns the position of the rule with the best adaptation degree of the example (v) in the ruleset
	 * </p>
	 * @param v vectordouble An example
	 * @return int The position of the rule with the best adaptation degree
	 */
    int InferenceC(vectordouble v){
     double max=0,aux;
     int re=-1;

     int n = 0;
     int[] nn = new int[0];
     int r = 0;
     int[] nr = new int[0];
     int b = 0;
     int[] nb = new int[0];
     char[][] mb = new char[0][0];
     int[][]nnn = new int[0][0];
     double[][] mr = new double[0][0];

     String regla;
     int var1, var2;
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
         Integer aux4 = new Integer(b);
         lista4.add(aux4);

         ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
         lista5.add(nb);

         ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
         lista6.add(mb);

       rule[i].GetBinary(lista4,lista5,lista6);

     aux4 = lista4.get(0);
     b = aux4.intValue();

     nb = lista5.get(0);

     mb = lista6.get(0);

        char[] s= new char[nb[0]+1];
        for (int j=0; j<nb[0]; j++)
          s[j]=mb[0][j];
        s[nb[0]]='\0';
        regla = String.copyValueOf(s);


        ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista7.add(aux1);

        ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
        lista8.add(nn);

        ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
        lista9.add(nnn);

      rule[i].GetInteger(lista7,lista8,lista9);

    aux1 = lista7.get(0);
    n = aux1.intValue();

    nn = lista8.get(0);

    nnn = lista9.get(0);


        ArrayList<Integer> lista1b = new ArrayList<Integer>(1);
        Integer aux1b = new Integer(r);
        lista1b.add(aux1b);

        ArrayList<int[]> lista2b = new ArrayList<int[]>(1);
        lista2b.add(nr);

        ArrayList<double[][]> lista3b = new ArrayList<double[][]>(1);
        lista3b.add(mr);

      rule[i].GetReal(lista1b,lista2b,lista3b);

   aux1b = lista1b.get(0);
   r = aux1b.intValue();

   nr = lista2b.get(0);

mr = lista3b.get(0);

        aux=domain.Adaptation(v,regla,mr[0],mr[0][nr[0]-1]);

        //Apply the relations

        //aux=domain[0].Adaptation(v,regla);

        aux = aux*peso[i];

        if (aux>max){
         max=aux;
         re=i;
        }
        else if (r!=0 && aux>0 && aux==max && peso[i]>peso[re]) {
               max=aux;
               re=i;
             }
     }
     if (re!=-1) {
         ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
         Integer aux1 = new Integer(n);
         lista7.add(aux1);

         ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
         lista8.add(nn);

         ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
         lista9.add(nnn);

       rule[re].GetInteger(lista7,lista8,lista9);

     aux1 = lista7.get(0);
     n = aux1.intValue();

     nn = lista8.get(0);

     nnn = lista9.get(0);

       return nnn[0][0];
     }
     else
       return -1;
    }


	/**
	 * <p>
	 * Retuns the position of the rule with the best adaptation degree of the example (v) in the ruleset and
	 * also this adaptation degree
	 * </p>
	 * @param v vectordouble An example
	 * @param milista ArrayList<Double> The adaptation degree
	 * @return int The position of the rule with the best adaptation degree
	 */
    int InferenceC(vectordouble v, ArrayList<Double> milista){
        Double aux0 = milista.get(0);
            double grado = aux0.doubleValue();

     double max=0,aux;
     int re=-1;

     int n = 0;
     int[] nn = new int[0];
     int r = 0;
     int[] nr = new int[0];
     int b = 0;
     int[] nb = new int[0];
     char[][] mb = new char[0][0];
     int[][]nnn = new int[0][0];
     double[][] mr = new double[0][0];

     String regla;
     int var1, var2;
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
         Integer aux4 = new Integer(b);
         lista4.add(aux4);

         ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
         lista5.add(nb);

         ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
         lista6.add(mb);

       rule[i].GetBinary(lista4,lista5,lista6);

     aux4 = lista4.get(0);
     b = aux4.intValue();

     nb = lista5.get(0);

     mb = lista6.get(0);

        char[] s= new char[nb[0]+1];
        for (int j=0; j<nb[0]; j++)
          s[j]=mb[0][j];
        s[nb[0]]='\0';
        regla = String.copyValueOf(s);

        ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista7.add(aux1);

        ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
        lista8.add(nn);

        ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
        lista9.add(nnn);

      rule[i].GetInteger(lista7,lista8,lista9);

    aux1 = lista7.get(0);
    n = aux1.intValue();

    nn = lista8.get(0);

    nnn = lista9.get(0);


        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1b = new Integer(r);
        lista1.add(aux1b);

        ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
        lista2.add(nr);

        ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
        lista3.add(mr);

      rule[i].GetReal(lista1,lista2,lista3);

   aux1b = lista1.get(0);
   r = aux1b.intValue();

   nr = lista2.get(0);

mr = lista3.get(0);

        aux=domain.Adaptation(v,regla,mr[0],mr[0][nr[0]-1]);

        //Apply the relations

        //aux=domain[0].Adaptation(v,regla);

        aux = aux*peso[i];



        if (aux>max){
         max=aux;
         re=i;
         grado = aux;
        }
        else if (r!=0 && aux>0 && aux==max && peso[i]>peso[re]) {
               max=aux;
               re=i;
               grado = aux;
             }
     }

     aux0 = Double.valueOf(grado);
     milista.add(0, aux0);

     if (re!=-1) {
         ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
         Integer aux1 = new Integer(n);
         lista7.add(aux1);

         ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
         lista8.add(nn);

         ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
         lista9.add(nnn);

       rule[re].GetInteger(lista7,lista8,lista9);

     aux1 = lista7.get(0);
     n = aux1.intValue();

     nn = lista8.get(0);

     nnn = lista9.get(0);

       return nnn[0][0];
     }
     else
       return -1;
    }



	/**
	 * <p>
	 * Retuns the position of the rule with the best adaptation degree of the example (v) in the ruleset and
	 * also this adaptation degree
	 * </p>
	 * @param v vectordouble An example
	 * @param milista1 ArrayList<Double> The adaptation degree
	 * @param milista2 ArrayList<Integer> The rule that have been fired
	 * @return int The position of the rule with the best adaptation degree
	 */
    int InferenceC(vectordouble v, ArrayList<Double> milista1, ArrayList<Integer> milista2){
        Double aux01 = milista1.get(0);
            double grado = aux01.doubleValue();

            Integer aux02 = milista2.get(0);
                int regla_disparada = aux02.intValue();



     double max=0,aux;
     int re=-1;

     int n = 0;
     int[] nn = new int[0];
     int r = 0;
     int[] nr = new int[0];
     int b = 0;
     int[] nb = new int[0];
     char[][] mb = new char[0][0];
     int[][]nnn = new int[0][0];
     double[][] mr = new double[0][0];

     String regla;
     int var1, var2;


     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
         Integer aux4 = new Integer(b);
         lista4.add(aux4);

         ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
         lista5.add(nb);

         ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
         lista6.add(mb);

       rule[i].GetBinary(lista4,lista5,lista6);

     aux4 = lista4.get(0);
     b = aux4.intValue();

     nb = lista5.get(0);

     mb = lista6.get(0);

        char[] s= new char[nb[0]+1];
        for (int j=0; j<nb[0]; j++)
          s[j]=mb[0][j];
        s[nb[0]]='\0';
        regla = String.copyValueOf(s);

        ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista7.add(aux1);

        ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
        lista8.add(nn);

        ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
        lista9.add(nnn);

      rule[i].GetInteger(lista7,lista8,lista9);

    aux1 = lista7.get(0);
    n = aux1.intValue();

    nn = lista8.get(0);

    nnn = lista9.get(0);


        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1b = new Integer(r);
        lista1.add(aux1b);

        ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
        lista2.add(nr);

        ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
        lista3.add(mr);

      rule[i].GetReal(lista1,lista2,lista3);

   aux1b = lista1.get(0);
   r = aux1b.intValue();

   nr = lista2.get(0);

mr = lista3.get(0);


        aux=domain.Adaptation(v,regla,mr[0],mr[0][nr[0]-1]);

        //Apply the relations

        //aux=domain[0].Adaptation(v,regla);

        aux = aux*peso[i];



        if (aux>max){
         max=aux;
         re=i;
         grado = aux;
        }
        else if (r!=0 && aux>0 && aux==max && peso[i]>peso[re]) {
               max=aux;
               re=i;
               grado = aux;
             }
     }

     regla_disparada=re;

     aux01 = Double.valueOf(grado);
     milista1.add(0, aux01);

     aux02 = Integer.valueOf(regla_disparada);
     milista2.add(0, aux02);

     if (re!=-1) {
         ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
         Integer aux1 = new Integer(n);
         lista7.add(aux1);

         ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
         lista8.add(nn);

         ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
         lista9.add(nnn);

       rule[re].GetInteger(lista7,lista8,lista9);

     aux1 = lista7.get(0);
     n = aux1.intValue();

     nn = lista8.get(0);

     nnn = lista9.get(0);

       return nnn[0][0];
     }
     else
       return -1;
    }



    private double InferenceF(vectordouble v){
     double aux,salida=0,neg,pos,center;

     int n = 0;
     int[] nn = new int[0];
     int r = 0;
     int[] nr = new int[0];
     int b = 0;
     int[] nb = new int[0];
     char[][] mb = new char[0][0];
     int[][]nnn = new int[0][0];
     double[][] mr = new double[0][0];

     String regla;
     int conse=domain.Consequent();
     for (int i=0; i<n_rule; i++){
         ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
         Integer aux4 = new Integer(b);
         lista4.add(aux4);

         ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
         lista5.add(nb);

         ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
         lista6.add(mb);

       rule[i].GetBinary(lista4,lista5,lista6);

     aux4 = lista4.get(0);
     b = aux4.intValue();

     nb = lista5.get(0);

     mb = lista6.get(0);

        char[] s= new char[nb[0]+1];

        for (int j=0; j<nb[0]; j++)
          s[j]=mb[0][j];
        s[nb[0]]='\0';
        regla = String.copyValueOf(s);

        ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
        Integer aux1 = new Integer(n);
        lista7.add(aux1);

        ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
        lista8.add(nn);

        ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
        lista9.add(nnn);

      rule[i].GetInteger(lista7,lista8,lista9);

    aux1 = lista7.get(0);
    n = aux1.intValue();

    nn = lista8.get(0);

    nnn = lista9.get(0);


        ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
        Integer aux1b = new Integer(r);
        lista1.add(aux1b);

        ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
        lista2.add(nr);

        ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
        lista3.add(mr);

      rule[i].GetReal(lista1,lista2,lista3);

   aux1b = lista1.get(0);
   r = aux1b.intValue();

   nr = lista2.get(0);

mr = lista3.get(0);

        aux=domain.Adaptation(v,regla,mr[0],mr[0][nr[0]]);
        if(aux>0){
            ArrayList<double[]> milista = new ArrayList<double[]>(1);
            double[] list = new double[2];
            list[0] = pos = 0.0;
            list[1] = neg = 0.0;

         domain.AdaptationC(v,nnn[0][0],milista);

         list = milista.get(0);
         pos = list[0];
         neg = list[1];

         center=domain.CenterLabel(conse,nnn[0][0]);
         salida+=center*pos;
        }
     }
       return salida;
    }


	/**
	 * <p>
	 * Returns the domain of all the variables
	 * </p>
	 * @return VectorVar The domain of all the variables
	 */
    VectorVar ArrayVar(){
      return (domain);
    }


	/**
	 * <p>
	 * Prints in a String the definition for all the variables in all the rules in the ruleset
	 * </p>
	 * @return String The definition for all the variables in all the rules in the ruleset
	 */
    String PrintDefinitionToString(){
        String cadena = "";

        cadena += "Number of rules = " + n_rule + "\n\n";
        for (int i=0; i<n_rule; i++){
            cadena += PrintDefinitionToString(i) + "\n";
        }

        return(cadena);
    }


	/**
	 * <p>
	 * Prints in a String the definition for all the variables in the rule in position "i" in the ruleset
	 * </p>
	 * @param i int The position of the rule in the ruleset
	 * @return String The definition for all the variables in the rule
	 */
    String PrintDefinitionToString(int i){
        String cadena = "";

        int n = 0;
        int[] nn = new int[0];
        int r = 0;
        int[] nr = new int[0];
        int b = 0;
        int[] nb = new int[0];
        char[][] mb = new char[0][0];
        int[][]nnn = new int[0][0];
        double[][] mr = new double[0][0];

     String regla;
     int var1, var2;
     int n_ant;

     // extract the binary component
     ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
     Integer aux4 = new Integer(b);
     lista4.add(aux4);

     ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
     lista5.add(nb);

     ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
     lista6.add(mb);

   rule[i].GetBinary(lista4,lista5,lista6);

 aux4 = lista4.get(0);
 b = aux4.intValue();

 nb = lista5.get(0);

 mb = lista6.get(0);

    char[] s= new char[nb[0]+1];
     for (int j=0; j<nb[0]; j++)
       s[j]=mb[0][j];
     s[nb[0]]='\0';
     regla = String.copyValueOf(s);

     ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
     Integer aux1 = new Integer(n);
     lista7.add(aux1);

     ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
     lista8.add(nn);

     ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
     lista9.add(nnn);

   rule[i].GetInteger(lista7,lista8,lista9);

 aux1 = lista7.get(0);
 n = aux1.intValue();

 nn = lista8.get(0);

 nnn = lista9.get(0);


     ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
     Integer aux1b = new Integer(r);
     lista1.add(aux1b);

     ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
     lista2.add(nr);

     ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
     lista3.add(mr);

   rule[i].GetReal(lista1,lista2,lista3);

aux1b = lista1.get(0);
r = aux1b.intValue();

nr = lista2.get(0);

mr = lista3.get(0);

     n_ant = domain.N_Antecedents();
     int z;
     int j=0;
     int donde=0;
     int n_casos=0;
     cadena += "IF ";
     while (j<n_ant){
       if (domain.IsActive(j) && domain.IsAntecedent(j)){
         n_casos=domain.SizeDomain(j);
         for(z=0; z<n_casos && regla.charAt(donde+z)=='1';z++);
         if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos){
           cadena += "     ";
           cadena += domain.SPrintVar(j);
           cadena += " = {";
           for(int t=donde; t<donde+n_casos;t++){
             if (regla.charAt(t)=='1'){
               cadena += " ";
               cadena += domain.SPrintDomain(j,t-donde);
             }
           }
           cadena += " }";
         }

       }
       donde+=n_casos;
       j++;
     }

     //Conclusion de la regla
     cadena += " THEN ";
     cadena += domain.SPrintVar(domain.Consequent());
     cadena += " IS ";
     cadena += domain.SPrintDomain(domain.Consequent(),nnn[0][0]);
     cadena += "   W " + peso[i] + "\n";

     return (cadena);
    }


	/**
	 * <p>
	 * Prints in in the standard output the definition for all the variables in the rule in position "i" in the ruleset
	 * </p>
	 * @param i int The position of the rule in the ruleset
	 */
    void Print(int i){
        int n = 0;
        int[] nn = new int[0];
        int r = 0;
        int[] nr = new int[0];
        int b = 0;
        int[] nb = new int[0];
        char[][] mb = new char[0][0];
        int[][]nnn = new int[0][0];
        double[][] mr = new double[0][0];

     String regla;
     int var1, var2;
     int n_ant;

     // extract the binary component
     ArrayList<Integer> lista4 = new ArrayList<Integer>(1);
     Integer aux4 = new Integer(b);
     lista4.add(aux4);

     ArrayList<int[]> lista5 = new ArrayList<int[]>(1);
     lista5.add(nb);

     ArrayList<char[][]> lista6 = new ArrayList<char[][]>(1);
     lista6.add(mb);

   rule[i].GetBinary(lista4,lista5,lista6);

 aux4 = lista4.get(0);
 b = aux4.intValue();

 nb = lista5.get(0);

 mb = lista6.get(0);

    char[] s= new char[nb[0]+1];
     for (int j=0; j<nb[0]; j++)
       s[j]=mb[0][j];
     s[nb[0]]='\0';
     regla = String.copyValueOf(s);

     ArrayList<Integer> lista7 = new ArrayList<Integer>(1);
     Integer aux1 = new Integer(n);
     lista7.add(aux1);

     ArrayList<int[]> lista8 = new ArrayList<int[]>(1);
     lista8.add(nn);

     ArrayList<int[][]> lista9 = new ArrayList<int[][]>(1);
     lista9.add(nnn);

   rule[i].GetInteger(lista7,lista8,lista9);

 aux1 = lista7.get(0);
 n = aux1.intValue();

 nn = lista8.get(0);

 nnn = lista9.get(0);


     ArrayList<Integer> lista1 = new ArrayList<Integer>(1);
     Integer aux1b = new Integer(r);
     lista1.add(aux1b);

     ArrayList<int[]> lista2 = new ArrayList<int[]>(1);
     lista2.add(nr);

     ArrayList<double[][]> lista3 = new ArrayList<double[][]>(1);
     lista3.add(mr);

   rule[i].GetReal(lista1,lista2,lista3);

aux1b = lista1.get(0);
r = aux1b.intValue();

nr = lista2.get(0);

mr = lista3.get(0);

     n_ant = domain.N_Antecedents();
     int z;
     int j=0;
     int donde=0;
     int n_casos=0;
     System.out.println("IF");
     while (j<n_ant){
       if (domain.IsActive(j) && domain.IsAntecedent(j)){
         n_casos=domain.SizeDomain(j);
         for(z=0; z<n_casos && regla.charAt(donde+z)=='1';z++);
         if (mr[0][j]>=mr[0][nr[0]-1] && z!=n_casos){
           System.out.print("     ");
           domain.PrintVar(j);
           System.out.print(" = {");
           for(int t=donde; t<donde+n_casos;t++){
             if (regla.charAt(t)=='1'){
               System.out.print(" ");
               domain.PrintDomain(j,t-donde);
             }
           }
           System.out.println("}");
         }

       }
       donde+=n_casos;
       j++;
     }

     //Conclusion de la regla
     System.out.print("THEN ");
     domain.PrintVar(domain.Consequent());
     System.out.print(" IS ");
     domain.PrintDomain(domain.Consequent(),nnn[0][0]);
     System.out.println("   W " + peso[i]);
    }


}

