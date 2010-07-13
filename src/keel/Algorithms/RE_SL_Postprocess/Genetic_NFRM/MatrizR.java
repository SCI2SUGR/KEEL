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

public class MatrizR implements Comparable{

  double [] matriz;
  int n_reglas, n_etiquetas;
  double fitness;
  boolean n_e;

  public MatrizR(BaseR baseReglas) {
    n_reglas = baseReglas.size();
    n_etiquetas = baseReglas.numEtiquetas();
    matriz = new double[n_reglas*n_etiquetas]; //num_reglas*num_salidas
    //Inicializo a cero
    for (int i = 0; i < matriz.length; i++){
      matriz[i] = 0.0;
    }
    //Ahora pongo los "1.0" en el sitio correspondiente
    for (int i = 0; i < baseReglas.size(); i++){
      int consecuente = baseReglas.dameRegla(i).consecuente;
      matriz[(i*n_etiquetas)+consecuente] = 1.0;
    }
    n_e = true;
  }

  public MatrizR(int n_reglas, int n_etiquetas) {
    this.n_reglas = n_reglas;
    this.n_etiquetas = n_etiquetas;
    matriz = new double[n_reglas*n_etiquetas]; //num_reglas*num_salidas
    //Inicializo aleatoriamente
    for (int i = 0; i < matriz.length; i++){
      matriz[i] = Randomize.Rand();
    }
    n_e = true;
  }

  public MatrizR(MatrizR padre, MatrizR madre, int puntoCorte){
    this.n_etiquetas = padre.n_etiquetas;
    this.n_reglas = padre.n_reglas;
    this.matriz = new double[n_etiquetas*n_reglas];
    for (int i = 0; i < puntoCorte; i++){
      matriz[i] = padre.matriz[i];
    }
    for (int i = puntoCorte; i < this.size(); i++){
      matriz[i] = madre.matriz[i];
    }
    n_e = true;
  }

  public MatrizR clone(){
    MatrizR m = new MatrizR(this,this,0);
    m.n_e = false;
    return m;
  }

  public int size(){
    return matriz.length;
  }

  public double damePeso(int regla, int salida){
    return matriz[(n_etiquetas*regla)+salida];
  }

  public void setFitness(double ecm){
    this.fitness = 1.0/(1.0 + ecm);
    n_e = false;
  }

  public void mutar(double prob){
    for (int i = 0; i < this.size(); i++){
      if (prob > Randomize.Rand()){
        if (Randomize.Rand() > 0.5){
          matriz[i] *= 0.9;
          if (matriz[i] < 0) matriz[i] = 0;
        }else{
          matriz[i] *= 1.1;
          if (matriz[i] > 1) matriz[i] = 1;
        }
      }
    }
  }

  public String printString(){
    String salida = new String("");
    salida += "Rules\t";
    for (int j = 0; j < n_etiquetas; j++){
      salida += "Output("+j+")\t";
    }
    for (int i = 0; i < n_reglas; i++){
      salida += "\nR"+i;
      for (int j = 0; j < n_etiquetas; j++){
        salida += "\t"+matriz[(n_etiquetas*i)+j];
      }
    }
    return salida;
  }

  public void print(){
    System.out.println(this.printString());
  }

  public int compareTo(Object a) {
    if ( ( (MatrizR) a).fitness < this.fitness) {
      return -1;
    }
    if ( ( (MatrizR) a).fitness > this.fitness) {
      return 1;
    }
    return 0;
  }

}

