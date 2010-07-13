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

package keel.Algorithms.Genetic_Rule_Learning.OCEC;

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
public class Attribute {

  double[] significance;
  String [] names;

  public Attribute(int n_variables) {
    significance = new double[n_variables];
    for (int i = 0; i < n_variables; i++) {
      significance[i] = 1.0;
    }
    names = new String[n_variables];
  }

  public Attribute(String [] nombres) {
    significance = new double[nombres.length];
    for (int i = 0; i < nombres.length; i++) {
      significance[i] = 1.0;
    }
    this.names = new String[nombres.length];
    this.names = nombres.clone();
  }


  public void reducir(int atributo) {
    significance[atributo] = 0.9 * significance[atributo] + 0.05;
  }

  public void incrementar(int atributo) {
    significance[atributo] = 0.9 * significance[atributo] + 0.2;
  }

  public String printString() {
    String cadena = new String("");
    for (int i = 0; i < significance.length - 1; i++) {
      cadena += names[i] + "(" + significance[i] + "), ";
    }
    cadena += names[names.length-1] + "(" + significance[significance.length - 1] + "). \n";
    return cadena;
  }

  public void print(){
    System.out.print(this.printString());
  }

}

