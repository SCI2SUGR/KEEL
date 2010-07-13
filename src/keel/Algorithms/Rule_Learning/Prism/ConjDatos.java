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

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Rule_Learning.Prism;

import java.util.*;

public class ConjDatos {
/**
 * Stores a set of data with the form: attribute attribute... class
 */
    private LinkedList datos;

    /**
     * <p>
     * Constructor. Initializes the variables
     * </p>
     */
    public ConjDatos() {
        datos = new LinkedList();
    }

    /**
     * <p>
     * Removes a data item
     * </p>
     * @param i Position to delete
     */
    public void deleteDato(int i) {
        datos.remove(i);
    }

    /**
     * <p>
     * Add a data item
     * </p>
     * @param m Example
     */
    public void addDato(Muestra m) {
        Muestra mim = m.copiaMuestra();
        datos.add(mim);
    }

    /**
     * <p>
     * Returns an example
     * </p>
     * @param i Position of the example
     * @return The example
     */
    public Muestra getDato(int i) {
        return (Muestra) datos.get(i);
    }

    /**
     * <p>
     * Assign a data item
     * </p>
     * @param i Position to insert
     * @param m Example
     */
    public void setDato(int i, Muestra m) {
        datos.set(i, m);
    }

    /**
     * <p>
     * Returns the number of examples of our set of data items
     * </p>
     * @return the size
     */
    public int size() {
        return (datos.size());
    }

    /**
     * <p>
     * Prints the examples on the screen
     * </p>
     */
    public void print() {
        for (int i = 0; i < datos.size(); i++) {
            Muestra m = (Muestra) datos.get(i);
            m.print();
        }

    }

    /**
     * <p>
     * Copy the set of data in other one(new)
     * </p>
     * @return A new set of data copy of the actual set
     */
    public ConjDatos copiaConjDatos() {
        ConjDatos c = new ConjDatos();

        for (int i = 0; i < datos.size(); i++) {
            Muestra aux;
            Muestra m = (Muestra) datos.get(i);
            aux = m.copiaMuestra();
            c.addDato(aux);
        }

        return c;
    }

    /**
     * <p>
     * Adapt the examples to the [0,1] interval
     * </p>
     * @param datos Set of data
     */
    public void hazUniforme(Dataset datos) {
        datos.normaliza();
    }

}

