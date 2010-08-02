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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.GFS_RB_MF;

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

import org.core.*;

public class Individuo implements Comparable{

  int [] cromosoma1;
  double [] cromosoma2;
  double [] max, min;
  boolean n_e;
  double fitness, ecm;
  int n_etiquetas;

  public Individuo() {
  }

  public Individuo(int n_variables, int n_etiquetas, int n_reglas, BaseD baseDatos){
    this.cromosoma1 = new int[n_reglas];
    this.cromosoma2 = new double[n_variables*n_etiquetas*3]; //tres puntos por etiqueta
    for (int i = 0; i < cromosoma1.length; i++){
      cromosoma1[i] = Randomize.RandintClosed(0,n_etiquetas-1); //para las n_posibles salidas
    }
    int k = 0;
    for (int i = 0; i < n_variables; i++){
      for (int j = 0; j < n_etiquetas; j++){
        cromosoma2[k] = baseDatos.baseDatos[i][j].x0;
        cromosoma2[k+1] = baseDatos.baseDatos[i][j].x1;
        cromosoma2[k+2] = baseDatos.baseDatos[i][j].x3;
        k+=3;
      }
    }
    min = new double[k];
    max = new double[k];
    for (int i = 0; i < k; i+=3){
      min[i] = cromosoma2[i] - (cromosoma2[i+1] - cromosoma2[i])/2.0;
      max[i] = cromosoma2[i] + (cromosoma2[i+1] - cromosoma2[i])/2.0;
      min[i+1] = cromosoma2[i+1] - (cromosoma2[i+1] - cromosoma2[i])/2.0;
      max[i+1] = cromosoma2[i+1] + (cromosoma2[i+2] - cromosoma2[i+1])/2.0;
      min[i+2] = cromosoma2[i+2] - (cromosoma2[i+2] - cromosoma2[i+1])/2.0;
      max[i+2] = cromosoma2[i+2] + (cromosoma2[i+2] - cromosoma2[i+1])/2.0;
    }
    this.n_etiquetas = n_etiquetas;
    n_e = true;
    fitness = 0;
    ecm = Double.MAX_VALUE;
  }

  public Individuo(int n_variables, int n_etiquetas, int n_reglas, double [] min, double [] max) {
    this.cromosoma1 = new int[n_reglas];
    this.cromosoma2 = new double[n_variables*n_etiquetas*3];
    for (int i = 0; i < cromosoma1.length; i++){
      cromosoma1[i] = Randomize.RandintClosed(0,n_etiquetas-1); //para las n_posibles salidas
    }
    for (int i = 0; i < cromosoma2.length; i++){
      cromosoma2[i] = min[i] + (max[i] - min[i]) * Randomize.Rand();
    }
    this.n_etiquetas = n_etiquetas;
    this.max = new double[max.length];
    this.max = max.clone();
    this.min = new double[min.length];
    this.min = min.clone();
    n_e = true;
    fitness = 0;
    ecm = Double.MAX_VALUE;
  }

  public Individuo(Individuo padre, Individuo madre, int puntoCorte){
    this.cromosoma1 = new int[padre.cromosoma1.length];
    this.cromosoma2 = new double[padre.cromosoma2.length];
    for (int i = 0; i < puntoCorte; i++){
      cromosoma1[i] = padre.cromosoma1[i];
    }
    for (int i = puntoCorte; i < cromosoma1.length; i++){
      cromosoma1[i] = madre.cromosoma1[i];
    }
    //Parent Centered BLX
    double d = 1.0;
    double [] cromP, cromM;
    max = padre.dameIntervalosMax();
    min = padre.dameIntervalosMin();
    cromP = padre.cromosoma2;
    cromM = madre.cromosoma2;
    for (int i = 0; i < cromosoma2.length; i++){
      double I, A1, C1;
      I = d * Math.abs(cromP[i]-cromM[i]);
      A1 = cromP[i]-I; if (A1 < min[i]) A1 = min[i];
      C1 = cromP[i]+I; if (C1 > max[i]) C1 = max[i];
      cromosoma2[i] = A1 + Randomize.Rand()*(C1-A1);
    }
    this.n_etiquetas = padre.n_etiquetas;
    n_e = true;
    fitness = 0;
    ecm = Double.MAX_VALUE;
  }


  public void setFitness(double ecm) {
    this.fitness = 1.0 / (1.0 + ecm);
    this.ecm = ecm;
    n_e = false;
  }

  public Individuo clone(){
    Individuo ind = new Individuo();
    ind.cromosoma1 = this.cromosoma1.clone();
    ind.cromosoma2 = this.cromosoma2.clone();
    ind.fitness = this.fitness;
    ind.n_etiquetas = this.n_etiquetas;
    ind.ecm = this.ecm;
    ind.n_e = this.n_e;
    ind.max = this.dameIntervalosMax();
    ind.min = this.dameIntervalosMin();
    return ind;
  }

  public void mutar(double probMut){
    for (int i = 0; i < cromosoma1.length; i++){
      if (Randomize.Rand() < probMut){
        cromosoma1[i] = Randomize.RandintClosed(0,n_etiquetas-1);
      }
    }
    for (int i = 0; i < cromosoma2.length; i++){
      if (Randomize.Rand() < probMut){
        cromosoma2[i] = Randomize.RanddoubleClosed(min[i],max[i]-1);
      }
    }

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

  public String printString(){
    String cadena = new String("");
    return cadena;
  }

  public void print(){
    System.out.println(this.printString());
  }

  public void escribeFichero(String fichero){
    Fichero.escribeFichero(fichero,this.printString());
  }

  public double [] dameIntervalosMin(){
    return min.clone();
  }
  public double [] dameIntervalosMax(){
    return max.clone();
  }

}
