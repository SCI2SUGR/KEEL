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

package keel.Algorithms.Genetic_Rule_Learning.Corcoran;

/**
 * <p>Title: Individual </p>
 * <p>Description: Chromosome Definition</p>
 * @author Written by Alberto Fernández (University of Granada) 12/11/2004
 * @version 1.0
 * @since JDK1.4
 */
public class Individual {

  private double Gene[];
  private double Perf;
  private boolean noEvaluado;
  private int exitos[];
  private int fracasos[];

  /**
   * Builder
   * @param numGenes number of genes
   * @param numReglas number of rules
   */
  public Individual(int numGenes, int numReglas) {
    Gene = new double[numGenes];
    exitos = new int[numReglas];
    fracasos = new int[numReglas];
  }

  /**
   * It returns the value of the gene at position i
   * @param pos the specific position
   * @return the value of that gene
   */
  public double getGene(int pos) {
    return Gene[pos];
  };

  /**
   * Sets a value for a specific gene
   * @param pos the gene position inside the chromosome
   * @param valor the new value
   */
  public void setGene(int pos, double valor) {
    Gene[pos] = valor;
  };

  /**
   * Sets the number of correctly classified instances for a rule of the chromosome
   * @param pos rule id
   * @param valor new value
   */
  public void setExitos(int pos, int valor) {
    exitos[pos] = valor;
  };

  /**
   * Sets the number of correctly misclassified instances for a rule of the chromosome
   * @param pos rule id
   * @param valor new value
   */
  public void setFracasos(int pos, int valor) {
    fracasos[pos] = valor;
  };

  /**
   * Sets the chromosome as non-evaluated
   * @param valor boolean value
   */
  public void setN_e(boolean valor) {
    noEvaluado = valor;
  }

  /**
   * It checks if the chromosome has been evaluated
   * @return n_e true if the chromosome has been evaluated. False in other case
   */
  public boolean getN_e() {
    return noEvaluado;
  }

  /**
   * Sets the performance for the chromosome
   * @param perf fitness value
   */
  public void setPerf(double perf) {
    Perf = perf;
  }

  /**
   * It gets the fitness value for the chromosome
   * @return perf Fitness value
   */
  public double getPerf() {
    return Perf;
  }

  /**
   * It returns the whole chromosome
   * @return Gene the whole chromosome
   */
  public double[] getTodo() {
    return Gene;
  }

  /**
   * It gets the number of classified instances for each rule
   * @return An array containing the number of classified instances for each rule
   */
  public int[] getExitos() {
    return exitos;
  }

  /**
   * It gets the number of misclassified instances for each rule
   * @return An array containing the number of misclassified instances for each rule
   */
  public int[] getFracasos() {
    return fracasos;
  }

  /**
   * It decodifies the Rule Base stored in the chromosome into an string for its visualization
   * @param reglas int Total number of rules
   * @param atributos int Total number of attributes
   * @param nombreAtributos String[] Attribute Names
   * @param nombreClases String[] Class names
   * @return String An string containing the best rules found during the search process
   */
  public String decodificaBR(int reglas, int atributos,String [] nombreAtributos, String [] nombreClases) {
      String salida = new String("");
      int divisor = (2 * (atributos + 1)) - 1;
      for (int i = 0; i < reglas; i++) {
        salida += "\n" + (i + 1) + ": ";
        int indice = i * divisor; // con indice se controla la pos. del atrib. dentro del cromosoma
        boolean regla = false;
        indice += 2;
        for (int j = 1; j < atributos; j++, indice += 2) {
          if (Gene[indice] <= Gene[indice + 1]){
            if (regla){
              salida += " AND ";
            }
            salida += nombreAtributos[j] + " = [" + Gene[indice] + ", " +
                Gene[indice + 1] + "]";
            regla = true;
          }
        }
        if (!regla){
          salida += "*";
        }
        salida += ": " + nombreClases[(int)Gene[ (i * divisor) + divisor - 1]+1]; // + " ("+exitos[i]+"/"+fracasos[i]+")";
      }
    return salida;
  }


}

