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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Fuzzy_Ish_Selec;

import org.core.Randomize;

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
public class Individuo implements Comparable{

  BaseR br,br_clasif;
  boolean[] cromosoma;
  double fitness, accuracy;
  double w_acc, w_size;

  public Individuo() {
  }

  public int compareTo(Object a) {
    if ( ( (Individuo) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (Individuo) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }


  public Individuo(BaseR baseReglas, double w_acc, double w_size, double p_include) {
    br = baseReglas.clone();
    this.w_acc = w_acc;
    this.w_size = w_size;

    cromosoma = new boolean[baseReglas.size()];
    for (int i = 0; i < baseReglas.size(); i++) {
      cromosoma[i] = true;
    }

    for (int j = br.size() - 1; j >= 0; j--) {
      if (Randomize.Rand() < (1 - p_include)) {
        cromosoma[j] = false;
        //br_clasif.eliminaRegla(j);
      }
    }
  }

  public Individuo(Individuo padre, Individuo madre, int puntoCorte){
     br = padre.devuelveBR();
     this.w_acc = padre.w_acc;
     this.w_size = padre.w_size;
     cromosoma = new boolean[padre.size()];
     for (int i = 0; i < puntoCorte; i++){
       cromosoma[i] = padre.cromosoma[i];
     }
     for (int i = puntoCorte; i < this.size(); i++){
       cromosoma[i] = madre.cromosoma[i];
     }
  }

  public int size(){
    return cromosoma.length;
  }

  public void mutar(double prob){
    for (int i = 0; i < this.size(); i++){
      if (Randomize.Rand() < prob) {
        cromosoma[i] = !cromosoma[i];
      }
    }
  }

  public BaseR devuelveBR(){
    return br.clone();
  }

  public Individuo clone(){
    Individuo i = new Individuo();
    i.br = this.br.clone();
    i.cromosoma = new boolean[this.size()];
    for (int j = 0; j < this.size(); j++){
      i.cromosoma[j] = this.cromosoma[j];
    }
    i.fitness = this.fitness;
    return i;
  }

  public double clasifica(){
    br_clasif = br.clone();
    br_clasif.actualiza(cromosoma);
    accuracy = br_clasif.clasifica();
    fitness = w_acc*br_clasif.fitness - w_size*br_clasif.size(); //br.fitness == n_clasificados
    return accuracy;
  }

}

