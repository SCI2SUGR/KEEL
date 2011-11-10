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
 * @author Written by Rosa Venzala (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Hyperrectangles.EACH;

import java.util.*;


public class EachDataSet {
/**
 * Stores a set of data with the form: attribute attribute... class
 */

    private LinkedList data;

    /**
     * <p>
     * Constructor. Initializes the variables
     * </p>
     */
    public EachDataSet() {
        data = new LinkedList();
    }

    /**
     * Borra un dato
     * @param i Posicion a borrar
     */
    public void removeData(int i) {
        data.remove(i);
    }

    /**
     * <p>
     * Removes a data item
     * </p>
     * @param m Position to delete
     */
    public void addData(Sample m) {
        Sample mim = m.copySample();
        data.add(mim);
    }

    /**
     * <p>
     * Returns an example
     * </p>
     * @param i Position of the example
     * @return The example in the position 'i'
     */
    public Sample getData(int i) {
        return (Sample) data.get(i);
    }

    /**
     * <p>
     * Assign a data item.
     * </p>
     * @param i Position to insert
     * @param m Example
     */
    public void setData(int i, Sample m) {
        data.set(i, m);
    }

    /**
     * <p>
     * Returns the number of examples of the data set
     * </p>
     * @return the size
     */
    public int size() {
        return (data.size());
    }

    /**
     * <p>
     * Shows the examples on the screen
     * </p>
     */
    public void print() {
        for (int i = 0; i < data.size(); i++) {
            Sample m = (Sample) data.get(i);
            m.print();
        }

    }

    /**
     * <p>
     * Copy the data set in other new
     * </p>
     * @return A new set of data, a copy of the actual
     */
    public EachDataSet copyDataSet() {
        EachDataSet c = new EachDataSet();

        for (int i = 0; i < data.size(); i++) {
            Sample aux;
            Sample m = (Sample) data.get(i);
            aux = m.copySample();
            c.addData(aux);
        }

        return c;
    }

    /**
     * <p>
     * Adapt the examples to the [0,1] interval
     * </p>
     * @param datos Set of data
     */
    public void doUniform(Dataset datos) {
        datos.normalize();
    }

}

