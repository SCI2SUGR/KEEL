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

package keel.Algorithms.RE_SL_Postprocess.Genetic_NFRM;

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

  MatrizR cromosoma1;
  double [] cromosoma2;
  double [] soporte;
  boolean n_e;
  double fitness, ecm;
  int n_etiquetas, n_variables;

  public Individuo() {
  }

  public Individuo(MatrizR mr, double [] ajuste, double [] soporte) {
    this.cromosoma1 = mr;
    this.cromosoma2 = new double[ajuste.length];
    for (int i = 0; i < ajuste.length; i++){
      cromosoma2[i] = ajuste[i];
    }
    this.soporte = new double[soporte.length];
    for (int i = 0; i < soporte.length; i++){
      this.soporte[i] = soporte[i];
    }
    int aux = ajuste.length/2;
    n_etiquetas = aux/soporte.length;
    n_variables = aux/n_etiquetas;
    n_e = true;
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
    ind.soporte = this.soporte.clone();
    ind.fitness = this.fitness;
    ind.ecm = this.ecm;
    ind.n_e = this.n_e;
    ind.n_etiquetas = this.n_etiquetas;
    ind.n_variables = this.n_variables;
    return ind;
  }

  public void mutar(double probMut){
    cromosoma1.mutar(probMut);
    for (int i = 0; i  < n_variables; i++){
      for (int j = 0; j < n_etiquetas; j++){
        if (Randomize.Rand() < probMut) {
          cromosoma2[j+(i*n_etiquetas)] = Randomize.RanddoubleClosed(-1.0*soporte[i], soporte[i]);
          cromosoma2[j+(i*n_etiquetas)+1] = Randomize.RanddoubleClosed(-1.0*soporte[i], soporte[i]);
        }
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
    cadena += "Fuzzy Relation Matrix:\n";
    cadena += cromosoma1.printString();
    return cadena;
  }

  public void print(){
    System.out.println(this.printString());
  }

  public void escribeFichero(String fichero){
    Fichero.escribeFichero(fichero,this.printString());
  }

}

