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
 * <p>Title: Rules comparative method.</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class ComparadorRegla implements Comparator {
    
    /**
     * Default constructor.
     */
    public ComparadorRegla() {
    }

  /**
   * Compares two rules.
   *
   * @param o1 Object Attribute to compare
   * @param o2 Object Attribute to compare
   * @return int 0 if both have the same position, 1 if first element comes before, -1 if the first element comes after.
   *
   * Warning!!! Collections.sort sorts in descending order (from greater to lesser).
   * This method sorts the elements in increasing (from lesser to greater), 
   * the way needed for rules.
   */
    public int compare(Object o1, Object o2) {
        float calidad1;
        float calidad2;
        Regla regla1;
        Regla regla2;
        int devolver = 0;
        int reglas1;
        int reglas2;

        regla1 = (Regla) o1;
        regla2 = (Regla) o2;

        calidad1 = regla1.obtenerCalidad();
        calidad2 = regla2.obtenerCalidad();
        reglas1 = regla1.obtenerNumeroMuestrasCubiertas();
        reglas2 = regla2.obtenerNumeroMuestrasCubiertas();

        if (calidad1 < calidad2) {
            devolver = 1;
        } else {
            if (calidad1 > calidad2) {
                devolver = -1;
            } else {
                if (reglas1 < reglas2) {
                    devolver = 1;
                } else
                if (reglas1 > reglas2) {
                    devolver = -1;
                } else {
                    devolver = 1;
                }
            }
        }

        return devolver;

    }


}

