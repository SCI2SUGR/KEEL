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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner;

import java.util.Comparator;

/**
 * <p> Title: ComparadorAtributo (Attributes comparative method) </p>
 * <p> Description: Attributes comparative method.</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class ComparadorAtributo implements Comparator {

  /**
     * Default constructor.
     */
    public ComparadorAtributo() {
  }
    
  /**
   * Compares two attributes
   *
   * @param o1 Object Attribute to compare
   * @param o2 Object Attribute to compare
   * @return int 0 if both have the same position, 1 if first element comes before, -1 if the first element comes after.
   *
   * Warning!!! Collections.sort sorts in descending order (from greater to lesser).
   * This method sorts the elements in increasing (from lesser to greater), 
   * the way needed for attributes.
   * 
   */
    public int compare(Object o1, Object o2) {
        Atributo original = (Atributo) o1;
        Atributo actual = (Atributo) o2;
        int atributo1, atributo2;
        String valor1, valor2;
        int devolver = 0;

        atributo1 = original.getAtributo();
        atributo2 = actual.getAtributo();
        valor1 = original.getValor();
        valor2 = actual.getValor();

        if (atributo1 == atributo2 && valor1.equals(valor2)) { //Para ver si son iguales tiene que coincidir tambien el valor
            devolver = 0;
        } else {
            if (atributo1 < atributo2) {
                devolver = -1;
            } else {
                if (atributo1 > atributo2) {
                    devolver = 1;
                }
            }
        }
        return devolver;

    }

}

