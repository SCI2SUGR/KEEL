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

package keel.Algorithms.Decision_Trees.DT_oblicuo;

import org.core.Randomize;


/**
 * 
 *
 * @author Anonymous - 2011
 * @version 1.0
 * @since JDK1.6
 */
public class Individuo
    implements Comparable {

  double[] cromosoma;
  double fitness;

  /**
   * Constructor of the class
   * 
   * @param tam size of the object
   */
  public Individuo(int tam) {
    cromosoma = new double[tam];
    for (int i = 0; i < tam; i++){
      cromosoma[i] = Randomize.RanddoubleClosed(-200.0,200.0); //propuesta autores
    }
    fitness = 0.0;
  }


  /**
   * Constructor of the class
   * 
   * @param pesos weights of the object
   * @param valor values of the object
   */
  public Individuo(double[] pesos, double valor) {
    cromosoma = new double[pesos.length + 1];
    int i;
    for (i = 0; i < pesos.length; i++) {
      cromosoma[i] = pesos[i];
    }
    cromosoma[i] = valor;
    fitness = 0.0;
  }

  public Individuo(Individuo padre, Individuo madre, int puntoCorte){
    cromosoma = new double[padre.size()];
    for (int i = 0; i < puntoCorte; i++){
      cromosoma[i] = padre.cromosoma[i];
    }
    for (int i = puntoCorte; i < cromosoma.length; i++){
      cromosoma[i] = madre.cromosoma[i];
    }
    fitness = 0.0;
  }

  public Individuo(Individuo padre, Individuo madre, boolean [] mascara){
    cromosoma = new double[padre.size()];
    for (int i = 0; i < cromosoma.length; i++){
      if (mascara[i])
        cromosoma[i] = padre.cromosoma[i];
      else
        cromosoma[i] = madre.cromosoma[i];
    }
    fitness = 0.0;
  }

  private boolean cubre(double[] ejemplo) {
    double aux = 0;
    int j;
    for (j = 0; j < ejemplo.length; j++) {
      aux += ejemplo[j] * cromosoma[j];
    }
    aux += cromosoma[j];
    return (aux >= 0);
  }

  public double impureza(double[][] ejemplos, int clases[], int n_clases) {
    double imp = Double.MIN_VALUE;
    int ejemplos_ii[], ejemplos_di[];
    int n_ejemplos_i, n_ejemplos_d;
    int n_ejemplos = ejemplos.length;
    n_ejemplos_i = n_ejemplos_d = 0;
    ejemplos_ii = new int[n_clases];
    ejemplos_di = new int[n_clases];
    //calculo lo necesario
    for (int i = 0; i < ejemplos.length; i++) {
      int clase = clases[i];
      if (this.cubre(ejemplos[i])) {
        n_ejemplos_i++;
        ejemplos_ii[clase]++;
      }
      else {
        n_ejemplos_d++;
        ejemplos_di[clase]++;
      }
    }
    if ( (n_ejemplos_i > 0) && (n_ejemplos_d > 0)) {
      imp = 0;
      for (int i = 0; i < n_clases; i++) {
        imp += Math.abs(
            ( (1.0 * ejemplos_ii[i] / n_ejemplos_i) -
             (1.0 * ejemplos_di[i] / n_ejemplos_d)));
        //System.err.println("imp["+i+"]: "+imp+" ("+( (1.0 * ejemplos_ii[i] / n_ejemplos_i) - (1.0 * ejemplos_di[i] / n_ejemplos_d))+") n_i:"+ejemplos_ii[i]+", n_d:" +ejemplos_di[i]);
      }
    }
    imp *= imp;
    imp *= (n_ejemplos_i * n_ejemplos_d);
    imp /= (1.0 * n_ejemplos * n_ejemplos);
    fitness = imp;
    return imp;
  }

  public Individuo clone() {
    Individuo i = new Individuo(this.cromosoma.length);
    i.cromosoma = this.cromosoma.clone();
    i.fitness = this.fitness;
    return i;
  }

  public int size(){
    return cromosoma.length;
  }

  /**
   * Funcion de minimizacion del fitness
   * @param a Object Otro arbol
   * @return int el valor para la comparativa
   */
  public int compareTo(Object a) {
    if ( ( (Individuo) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (Individuo) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

}

