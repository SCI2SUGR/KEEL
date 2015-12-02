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

package keel.Algorithms.Genetic_Rule_Learning.PSO_ACO;

import java.util.Comparator;

/**
 * <p> Title: ComparadoCondicion (Conditions comparative method) </p>
 * <p>Conditions comparative method.</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class ComparadorCondicion implements Comparator {

    /**
     * Default constructor.
     */
    public ComparadorCondicion() {
    }

  /**
   * Compares two condition.
   *
   * @param o1 Object Condition to compare
   * @param o2 Object Condition to compare
   * @return int 0 if both have the same position, 1 if first element comes before, -1 if the first element comes after.
   *
   * Warning!!! Collections.sort sorts in descending order (from greater to lesser).
   * This method sorts the elements in increasing (from lesser to greater), 
   * the way needed for conditions.
   */
    public int compare(Object o1, Object o2) {
        Condicion c1 = (Condicion) o1;
        Condicion c2 = (Condicion) o2;

        Atributo original = c1.getValor();
        Atributo actual = c2.getValor();
        int atributo1, atributo2;
        float valor1, valor2;
        int devolver = 0;

        //indices para comprobar si se esta comparando el mismo atributo
        atributo1 = original.getAtributo();
        atributo2 = actual.getAtributo();
        //Cogemos los Strigs para ver que tienen el mismo valor
        valor1 = original.getValor();
        valor2 = actual.getValor();

        if (atributo1 == atributo2 && valor1 == valor2 &&
            c1.getOperador() == c2.getOperador()) { //Para ver si son iguales tiene que coincidir tambien el valor
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

