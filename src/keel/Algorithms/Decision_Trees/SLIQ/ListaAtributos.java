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

package keel.Algorithms.Decision_Trees.SLIQ;

import java.util.Comparator;

/**
 *
 * This class manages an ordered list of each attribute.
 *
 * @author Francisco Charte Ojeda
 * @version 1.0 (30-12-09)
 */
public class ListaAtributos {
    /** Attribute value*/
    public double valor;
    /** Index on the classes list*/
    public int indice;

    /** Parameter Constructor.  The ListaAtributos structures will be initialized with the parameters given.
     *
     * @param valor  attribute value
     * @param indice index on the classes list
     */
    public ListaAtributos(double valor, int indice) {
        this.valor = valor;
        this.indice = indice;
    }

    /**
     * Class used to compare different ListaAtributos objects, being possible to sort them.
     */
    static class Comparador implements Comparator {

        public int compare(Object o1, Object o2) {
            return ((ListaAtributos)o1).valor < ((ListaAtributos)o2).valor ? -1 : 1;
        }

    }
}

