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

/**
 * Clase para gestionar la lista de clases de un dataset
 *
 * @author Francisco Charte Ojeda
 * @version 1.0 (30-12-09)
 */
public class ListaClases {
    /** indice de la clase */
    public int clase;
    /** Nodo hoja asociado */
    public Node hoja;

    /** Constructor
     *
     * @param clase indice de la clase
     * @param hoja  Nodo hoja asociado
     */
    public ListaClases(int clase, Node hoja) {
        this.clase = clase;
        this.hoja = hoja;
    }
}

