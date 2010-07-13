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
 
public class populationReal {
/**
 * <p>
 * It encodes a real population
 * </p>
 */
 
    double prob_mutacion;
    double prob_cruce;
    int elitismo;
    int n_individuos;
    int[] tamano;
    boolean[] modificado;
    double rango_i;
    double rango_s;
    double[][] individuos;


    /**
     * <p>
     * Default Constructor
     * </p>
     */
    populationReal(){
      prob_mutacion=0.0;
      prob_cruce=0.0;
      elitismo=0;
      n_individuos=0;
      tamano= new int[0];
      modificado= new boolean[0];
      individuos= new double[0][0];
      rango_i=0;
      rango_s=0;
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param rangoi int[] Lower range for the real population
     * @param rangos int[] Upper range for the real population
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     */     
    populationReal(double raninf, double ransup, double mut, double cruce, int eli, int n){
      prob_mutacion=mut;
      prob_cruce=cruce;
      elitismo=eli;
      n_individuos=n;
      rango_i=raninf;
      rango_s=ransup;
      tamano= new int[n_individuos];
      for (int i=0; i<n_individuos; i++)
        tamano[i]=0;

      modificado= new boolean[n_individuos];
      for (int i=0; i<n_individuos; i++)
        modificado[i]=true;

      individuos = new double[n_individuos][];
      for (int i=0; i<n_individuos; i++)
        individuos[i] = new double[0];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param rangoi int[] Lower range for the real population
     * @param rangos int[] Upper range for the real population
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     * @param tama int[] Size for each individual in the population 
     */   
    populationReal(double raninf, double ransup, double mut, double cruce, int eli, int n, int[] tama){
      prob_mutacion=mut;
      prob_cruce=cruce;
      elitismo=eli;
      n_individuos=n;
      rango_i=raninf;
      rango_s=ransup;
      tamano= new int[n_individuos];
      for (int i=0; i<n_individuos; i++)
        tamano[i]=tama[i];

      modificado= new boolean[n_individuos];
      for (int i=0; i<n_individuos; i++)
        modificado[i]=true;

      individuos = new double[n_individuos][];
      for (int i=0; i<n_individuos; i++)
        individuos[i] = new double[tamano[i]];
    }


    /**
     * <p>
     * Constructor
     * </p>
     * @param rangoi int[] Lower range for the real population
     * @param rangos int[] Upper range for the real population
     * @param mut double The mutation probability
     * @param cruce double The crossover probability
     * @param eli int Number of best individuals to be keept in the selection (Elitist selection)
     * @param n int Number of individuals in the population
     * @param tama int Size for all the individuals in the population     
     */   
    populationReal(double raninf, double ransup, double mut, double cruce, int eli, int n, int tama){
      prob_mutacion=mut;
      prob_cruce=cruce;
      elitismo=eli;
      n_individuos=n;
      rango_i=raninf;
      rango_s=ransup;
      tamano= new int[n_individuos];
      for (int i=0; i<n_individuos; i++)
        tamano[i]=tama;

      modificado= new boolean[n_individuos];
      for (int i=0; i<n_individuos; i++)
        modificado[i]=true;

      individuos = new double[n_individuos][];
      for (int i=0; i<n_individuos; i++)
        individuos[i] = new double[tamano[i]];
    }


    /**
     * <p>
     * Creates a real population as a copy of another one
     * </p>
     * @param x populationReal The real population used to created the new one
     */
    populationReal(populationReal x){
      this.prob_mutacion=x.prob_mutacion;
      this.prob_cruce = x.prob_cruce;
      this.elitismo = x. elitismo;
      this.n_individuos = x.n_individuos;
      this.rango_i=x.rango_i;
      this.rango_s=x.rango_s;

      this.tamano = new int[this.n_individuos];
      for (int i=0; i<this.n_individuos; i++)
        this.tamano[i]=x.tamano[i];


      this.modificado= new boolean[this.n_individuos];
      for (int i=0; i<this.n_individuos; i++)
        this.modificado[i]=x.modificado[i];


      this.individuos = new double[this.n_individuos][];
      for (int i=0; i<this.n_individuos; i++) {
        if (this.tamano[i]>0){
          this.individuos[i] = new double[this.tamano[i]];
          for (int j=0; j<this.tamano[i]; j++)
            this.individuos[i][j]=x.individuos[i][j];
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
	 * @return double[] The individual
	 */
    double[] GetIndividual(int i) {
        return individuos[i];
    }


	/**
	 * <p>
	 * Returns the individual in position "i" of the population. It size is also returned
	 * </p>
	 * @param i int The position of the individual in the population
	 * @param milista ArrayList<Integer> Keeps the size of the individual
	 * @return double[] The individual
	 */
    double[] GetIndividual(int i, ArrayList<Integer> milista){
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
    boolean Modified(int i){
     return modificado[i];
    }

	/**
	 * <p>
	 * Copies the individual "j" in population "x" to the position "i" of the current population
	 * </p>
	 * @param i int Position in the current population to copy the individual
	 * @param x populationReal Another population of individuals
	 * @param j int Position of the individual to be copied in the other population of individuals
	 */
    void CopyIndividual(int i, populationReal x, int j){

      tamano[i]=x.tamano[i];
      individuos[i]= new double[tamano[i]];
      for (int k=0; k<tamano[i]; k++)
        individuos[i][k]=x.individuos[j][k];
    }


	/**
	 * <p>
	 * It swaps the invididual in positions "i" and "j"
	 * </p>
	 * @param i int The position of the first individual
	 * @param j int The position of the second individual
	 */
    void Swap(int i, int j){
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

        int longitud = individuos[i].length;
        double[] p = new double[longitud];
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
    void Code(int i,ArrayList < double[] > milista1, ArrayList<Integer>
            milista2){
      //if (v!=0)
        //delete [] v;

        Integer tama = new Integer(tamano[i]);
        int tam = tama.intValue();
        double[] v = new double[tam];
        for (int j = 0; j < tam; j++) {
            v[j] = individuos[i][j];
        }

        milista1.add(0, v);
        milista2.add(0, tama);
    }


	/** 
	 * <p>
	 * Prints in the standard output the definition for the individual in position "i"
	 * </p>
	 * @param i int The position of the individual in the population
	 */
    void PrintDefinition(int i){
        for (int j = 0; j < tamano[i]; j++) {
            System.out.print(individuos[i][j]);
        }
        System.out.println("");
    }

	/** 
	 * <p>
	 * Prints in the standard output the definition for the individual in position "i"
	 * </p>
	 * @param i int The position of the individual in the population
	 */
    void PrintBin(int i){
      for (int j=0; j<tamano[i]-1; j++)
        if(individuos[i][j]<individuos[i][tamano[i]-1])
          System.out.print("0");
        else
          System.out.print("1");
  System.out.println("");
    }


	/** 
	 * <p>
	 * Sets the gen "bit" of the individual in position "indiv" to the value "value"
	 * </p>
	 * @param indiv int The position of the individual in the population
	 * @param bit int The gen in the individual
	 * @param value double The new value
	 */
    void PutValue(int indiv, int bit, double value){
      individuos[indiv][bit]=value;
    }


	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 */
    void RandomInitialPopulation(){
      double a;
      for (int i=0; i<n_individuos; i++) {
        for (int j=0; j<tamano[i]-1; j++){
            //a=(1.0*(rango_s-rango_i)*rand())/(RAND_MAX+1.0);
            //individuos[i][j]= rango_i+a;
            individuos[i][j]=0.8;
        }
        individuos[i][tamano[i]-1]=0.5;
      }
    }

	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 * @param n_item i The number of element of each individual
	 */
    void RandomInitialPopulation(int n_item){
      for (int i=0; i<n_individuos; i++)
        individuos[i][tamano[i]-1]=n_item;
    }


	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 * @param I double[][] Values for each individual and gene in the population
	 * @param rango i The range for the real population
	 */
    void RandomInitialPopulation(double[][] I, int rango){
      double a;
      double max=I[0][0], min=I[0][0];

      for (int j=0; j<tamano[0]-1; j++){
          //a=(1.0*(rango_s-rango_i)*rand())/(RAND_MAX+1.0);
          //individuos[i][j]= rango_i+a;
          if (I[j][0]>max)
            max=I[j][0];
          else if (I[j][0]<min)
                 min=I[j][0];
      }

      //cout << "min= " << min << "   max= " << max << endl;


      for (int i=0; i<n_individuos; i++) {
        for (int j=0; j<tamano[i]-1; j++)
          if (min==max)
            individuos[i][j]=Randomize.Rand();
          else
            individuos[i][j]=I[j][(i%rango)+1];

        // individuos[i][tamano[i]-1]=((1.0*(max-min)*rand())/(RAND_MAX+1.0))+min;
        /*if (min==max)
          individuos[i][tamano[i]-1]=((1.0*rand())/(RAND_MAX+1.0));
        else
          individuos[i][tamano[i]-1]=I[i%(tamano[i]-1)][i%rango];*/

        individuos[i][tamano[i]-1]=Randomize.Rand();
        //individuos[i][tamano[i]-1]=0;
      }
    }


	/** 
	 * <p>
	 * It randomly creates a initial population
	 * </p>
	 * @param vacio int Not used
	 * @param rango i The range for the real population
	 */
    void RandomInitialPopulation(int vacio, int rango){

      for (int i=0; i<n_individuos; i++) {
        for (int j=0; j<tamano[i]-1; j++)
            individuos[i][j]=Randomize.Rand();

        individuos[i][tamano[i]-1]=Randomize.Rand();
      }
    }



    private void PoblacionInicialValor(double valor){
      if (valor<rango_i || valor >rango_s)
        valor=(rango_s-rango_i)/2.0;

      for (int i=0; i<n_individuos; i++)
        for (int j=0; j<tamano[i]; j++){
            individuos[i][j]= valor;
        }
    }


	/**
	 * <p>
	 * It applies the uniform mutation operator
	 * </p>
	 */
    void UniformMutation(){
        operations O = new operations();
      double aux;
      for (int i=elitismo; i<n_individuos; i++)
        for (int j=0; j<tamano[i]; j++)
          if (O.Probability(prob_mutacion)){
            do{
              aux= Randomize.RanddoubleClosed(0.0, rango_s-rango_i);
            }while (aux==individuos[i][j]);
            individuos[i][j]=aux;
            modificado[i]=true;
          }
    }

	/**
	 * <p>
	 * It applies the uniform crossover operator
	 * </p>
	 */
    void UniformCrossover(){
        operations O = new operations();
      int a,p1;
      double aux;
      for (int i=elitismo; i<n_individuos; i++)
        if (O.Probability(prob_cruce)){
          a=O.Select_Random_Individual(n_individuos,i, elitismo);
          p1=O.CutPoint(tamano[i]);
          modificado[i]=true;
          modificado[a]=true;
          for (int j=0; j<p1; j++){
             aux=individuos[i][j];
                   individuos[i][j]=individuos[a][j];
                   individuos[a][j]=aux;
          }
        }
    }


	/**
	 * <p>
	 * It applies the two points crossover operator
	 * </p>
	 */
    void TwoPointsCrossover(){
        operations O = new operations();
      int a,p1,p2;
      double aux;
      for (int i=elitismo; i<n_individuos; i++)
        if (O.Probability(prob_cruce)){
          a=O.Select_Random_Individual(n_individuos,i,elitismo);

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

          modificado[i]=true;
          modificado[a]=true;
          for (int j=p1; j<p2; j++){
             aux=individuos[i][j];
                   individuos[i][j]=individuos[a][j];
                   individuos[a][j]=aux;
          }
        }
    }


    /* void populationReal::SteadyState_UniformMutation(){
      double aux;
      for (int i=n_individuos-2; i<n_individuos; i++)
        for (int j=0; j<tamano[i]; j++)
          if (Probability(prob_mutacion)){
            do{
              aux=(1.0*(rango_s-rango_i)*rand())/(RAND_MAX+1.0);
            }while (aux==individuos[i][j]);
            individuos[i][j]=aux;
            modificado[i]=true;
          }
    }*/


	/**
	 * <p>
	 * It applies the steady state uniform mutation operator
	 * </p>
	 */
    void SteadyState_UniformMutation(){
        operations O = new operations();
      double aux;
      int pos;
      for (int i=n_individuos-2; i<n_individuos; i++){
        for (int k=0; k<tamano[i]; k++){
          //pos = (int) (1.0*tamano[i]*rand()/(RAND_MAX+1.0));
          if (O.Probability(prob_mutacion)){
             //individuos[i][k]= 1.0*rand()/(RAND_MAX+1.0);
             //individuos[i][k] = 1.0 - individuos[i][k];
             if (individuos[i][k]<=individuos[i][tamano[i]-1])
                //individuos[i][k]=(1.0*(1-individuos[i][tamano[i]-1])*rand())/(RAND_MAX+1.0)+individuos[i][tamano[i]-1];
                individuos[i][k]=individuos[i][tamano[i]-1]+0.01;
             else
                //individuos[i][k]=(1.0*(individuos[i][tamano[i]-1])*rand())/(RAND_MAX+1.0);
                individuos[i][k]=individuos[i][tamano[i]-1]-0.01;

             if (individuos[i][k]>1)
               individuos[i][k]=1;
             else if (individuos[i][k]<0)
                    individuos[i][k]=1;
          }

          //individuos[i][pos]=(1.0*rand())/(RAND_MAX+1.0);
        }
        modificado[i]=true;
      }
    }


    	/**
	 * <p>
	 * It applies the rotation operator to the individual in position "i" in the population
	 * </p>
	 * @param i int The positino of the individual
	 */
    void Rotation(int i){
        operations O = new operations();
      int p;
      double[] copia= new double[tamano[i]];

      // Make a copy of the chromosome
      for (int j=0; j<tamano[i]; j++)
        copia[j]=individuos[i][j];
      // Select a point in the chromosome
      p=O.CutPoint(tamano[i]);

      // Modify the chromosome with the rotation
      for (int j=0; j<tamano[i]; j++)
        individuos[i][j]=copia[(j+p)%tamano[i]];

    }


	/**
	 * <p>
	 * It applies the steady state uniform crossover operator
	 * </p>
	 */
    void SteadyState_UniformCrossover(){
        operations O = new operations();
      int a,b,p1;
      double aux;
      for (int i=n_individuos-2; i<n_individuos; i++){
          a=O.Select_Random_Individual(n_individuos,i, 0);
          b=O.Select_Random_Individual(n_individuos,i, 0);
          p1=O.CutPoint(tamano[i]);
          modificado[i]=true;
          for (int j=0; j<p1; j++)
                   individuos[i][j]=individuos[a][j];

          for (int j=p1; j<tamano[i]; j++)
             individuos[i][j]=individuos[b][j];
      }
    }


	/**
	 * <p>
	 * It applies the steady state two points crossover operator
	 * </p>
	 */
    void SteadyState_TwoPointsCrossover(){
        operations O = new operations();
      int a,b,p1,p2;
      double aux;
      for (int i=n_individuos-2; i<n_individuos; i++){
          a=O.Select_Random_Individual(n_individuos,i,0);
          b=O.Select_Random_Individual(n_individuos,i,0);

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

          modificado[i]=true;
          for (int j=0; j<p1; j++)
                   individuos[i][j]=individuos[a][j];

          for (int j=p1; j<p2; j++)
                   individuos[i][j]=individuos[b][j];

          for (int j=p2; j<tamano[i]; j++)
                   individuos[i][j]=individuos[a][j];
      }
    }


	/**
	 * <p>
	 * It applies the steady state two points crossover operator between the individual in positions "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_TwoPointsCrossover(int indiv1, int indiv2){
        operations O = new operations();
      int a,b,p1,p2;
      double aux;
      int i=n_individuos-2;
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

          modificado[i]=true;
          modificado[i+1]=true;
          for (int j=0; j<p1; j++){
                   individuos[i][j]=individuos[indiv1][j];
                   individuos[i+1][j]=individuos[indiv2][j];
          }

          for (int j=p1; j<p2; j++){
                   individuos[i][j]=individuos[indiv2][j];
                   individuos[i+1][j]=individuos[indiv1][j];
          }

          for (int j=p2; j<tamano[i]; j++){
                   individuos[i][j]=individuos[indiv1][j];
                   individuos[i+1][j]=individuos[indiv2][j];
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
    void SteadyState_AND_OR_Crossover(int indiv1, int indiv2){
        operations O = new operations();
      int a,b,p1,p2;
      double aux;
      int i=n_individuos-2;
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

          modificado[i]=true;
          modificado[i+1]=true;
          for (int j=p1; j<=p2; j++){
            if (individuos[indiv1][j]< individuos[indiv2][j]){
                   individuos[i][j]=individuos[indiv2][j];
                   individuos[i+1][j]=individuos[indiv1][j];
            }
            else {
                   individuos[i][j]=individuos[indiv1][j];
                   individuos[i+1][j]=individuos[indiv2][j];
            }
          }
      //}
    }


	/**
	 * <p>
	 * It applies the steady state NAND/NOR crossover operator between the individual in positions "indiv1" and "indiv2"
	 * </p>
	 * @param indiv1 int Position of the first individual
	 * @param indiv2 int Position of the second individual	 
	 */
    void SteadyState_NAND_NOR_Crossover(int indiv1, int indiv2){
        operations O = new operations();
      int a,b,p1,p2;
      double aux;
      int i=n_individuos-2;
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

          modificado[i]=true;
          modificado[i+1]=true;
          for (int j=p1; j<=p2; j++){
            if (individuos[indiv1][j]< individuos[indiv2][j]){
                   individuos[i][j]=1-individuos[indiv2][j];
                   individuos[i+1][j]=1-individuos[indiv1][j];
            }
            else {
                   individuos[i][j]=1-individuos[indiv1][j];
                   individuos[i+1][j]=1-individuos[indiv2][j];
            }
          }
      //}
    }

}

