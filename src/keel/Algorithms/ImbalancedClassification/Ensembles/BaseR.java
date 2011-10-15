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

package keel.Algorithms.ImbalancedClassification.Ensembles;

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

import java.util.ArrayList;
import java.util.StringTokenizer;

public class BaseR {

  ArrayList<Regla> baseReglas;
  myDataset train;

  public BaseR() {
    baseReglas = new ArrayList<Regla> ();
  }

  /**
   * Obtengo la base de reglas a traves del fichero de reglas (extraido a partir del arbol de decision)
   * @param reglas String
   * @param train myDataset conjunto de datos de entrenamiento
   */
  public BaseR(myDataset train, String reglas) {
    baseReglas = new ArrayList<Regla> ();
    this.train = train;
    StringTokenizer tokens = new StringTokenizer(reglas, "\n");
    while (tokens.hasMoreTokens()) {
      String regla = tokens.nextToken();
      //System.err.println("Regla -> "+regla);
      Regla r = new Regla(train, regla);
      baseReglas.add(r);
    }
  }

  public String printString() {
    String cadena = new String("");
    cadena += "Number of Rules: " + baseReglas.size() + "\n";
    for (int i = 0; i < baseReglas.size(); i++) {
      cadena += "Rule[" + (i + 1) + "]: " + baseReglas.get(i).printString();
    }
    return cadena;
  }

  public String printStringF() {
    String cadena = new String("");
    cadena += "Number of Rules: " + baseReglas.size() + "\n";
    for (int i = 0; i < baseReglas.size(); i++) {
      cadena += "Rule[" + (i + 1) + "]: " + baseReglas.get(i).printStringF();
    }
    return cadena;
  }

  public int size() {
    return baseReglas.size();
  }

  /**
   * Detecta las reglas que cubren un small-disjunt
   */
  public void cubrirEjemplos() {
    for (int i = 0; i < this.size(); i++) {
      baseReglas.get(i).cubrirEjemplos();
    }
  }

  /**
   * Realiza la misma operación que la anterior pero considerando los pesos
   * @param weights los pesos de los ejemplos
   */
  public void cubrirEjemplos(double weights[]) {
    for (int i = 0; i < this.size(); i++) {
      baseReglas.get(i).cubrirEjemplos(weights);
    }
     train = null;
  }


}
