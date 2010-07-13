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

/*
 * Created on 27-feb-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.Comparator;
import java.util.Vector;

/**
 * @author Sebas
 */
public class AtributoComparator implements Comparator {
    private int atributo;
    private int clase;

    /**
     * Constructor
     * @param atributo
     * @param clase
     */
    public AtributoComparator(int atributo, int clase) {
        this.atributo = atributo;
        this.clase = clase;
    }


    /**
     * Compare
     * @param arg0
     * @param arg1
     */
    public int compare(Object arg0, Object arg1) {
        int result = 0;
        double valor0;
        double valor1;

        if (((Vector) arg0).get(atributo) instanceof Double) {
            valor0 = ((Double) ((Vector) arg0).get(atributo)).doubleValue();
            valor1 = ((Double) ((Vector) arg1).get(atributo)).doubleValue();
        } else {
            valor0 = ((Integer) ((Vector) arg0).get(atributo)).doubleValue();
            valor1 = ((Integer) ((Vector) arg1).get(atributo)).doubleValue();
        }

        if (valor0 < valor1) {
            result = -1;
        } else if (valor0 > valor1) {
            result = 1;
        } else {
            int clase0 = ((Integer) ((Vector) arg0).get(clase)).intValue();
            int clase1 = ((Integer) ((Vector) arg1).get(clase)).intValue();

            if (clase0 < clase1) {
                result = -1;
            } else if (clase0 > clase1) {
                result = 1;
            }
        }
        return result;
    }
}

