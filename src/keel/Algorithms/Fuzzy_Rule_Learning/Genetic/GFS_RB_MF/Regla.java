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

public class Regla{

  int[] antecedente; //etiquetas de los antecedentes
  int consecuente; //etiqueta del consecuente
  BaseD baseDatos;

  public Regla(Regla r) {
    this.antecedente = new int[r.antecedente.length];
    for (int k = 0; k < this.antecedente.length; k++) {
      this.antecedente[k] = r.antecedente[k];
    }
    this.baseDatos = r.baseDatos;
    this.consecuente = r.consecuente;
  }

  public Regla(BaseD baseDatos) {
    this.baseDatos = baseDatos;
    antecedente = new int[baseDatos.numVariables()-1];
  }

  public void asignaAntecedente(int [] antecedente){
    for (int i = 0; i < antecedente.length; i++){
      this.antecedente[i] = antecedente[i];
    }
  }

  public double compatibilidadMinimo(double[] ejemplo) {
    double minimo, grado_pertenencia;
    minimo = 1.0;
    for (int i = 0; i < antecedente.length; i++) {
      grado_pertenencia = baseDatos.pertenencia(i, antecedente[i], ejemplo[i]);
      minimo = Math.min(grado_pertenencia, minimo);
    }
    return (minimo);

  }

  public Regla clone() {
    Regla r = new Regla(baseDatos);
    r.antecedente = new int[antecedente.length];
    for (int i = 0; i < this.antecedente.length; i++) {
      r.antecedente[i] = this.antecedente[i];
    }
    r.consecuente = this.consecuente;
    return r;
  }

}

