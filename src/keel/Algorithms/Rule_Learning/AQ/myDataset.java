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

package keel.Algorithms.Rule_Learning.AQ;

import java.util.*;

/**
 * <p>Title: myDataset</p>
 * <p>Description: Manage the data-sets</p>
 * @author Written by José Ramón Cano de Amo (University of Jaén) 08/02/2004
 * @author Modified by Alberto Fernández (University of Granada) 11/28/2004
 * @since JDK1.4
 * @version 1.1
 */
public class myDataset {

    private LinkedList datos;

    /**
     * Builder. It initialize the container variables
     */
    public myDataset() {
        super();
        datos = new LinkedList();
    }

    /**
     * It removes a data
     * @param i Position to remove
     */
    public void deleteData(int i) {
        datos.remove(i);
    }

    /**
     * It adds a data
     * @param m Example
     */
    public void addData(Instance m) {
        Instance mim = m.copy();
        datos.add(mim);
    }

    /**
     * It return an example
     * @param i Position of the example
     * @return i-th example
     */
    public Instance getData(int i) {
        Instance m = (Instance) datos.get(i);
        return (Instance) datos.get(i);
    }

    /**
     * It assigns a data. Modifies a previously one
     * @param i Position to insert
     * @param m Example
     */
    public void setData(int i, Instance m) {
        datos.set(i, m);
    }

    /**
     * It returns the number of examples of the data-set
     * @return The size
     */
    public int size() {
        return (datos.size());
    }

    /**
     * It prints the examples
     */
    public void print() {
        int i;
        for (i = 0; i < datos.size(); i++) {
            Instance m = (Instance) datos.get(i);
            m.print();
        }

    }

    /**
     * It copies the data-set in a new one
     * @return A new cloned data-set
     */
    public myDataset copyDataSet() {
        int i;
        myDataset c = new myDataset();

        for (i = 0; i < datos.size(); i++) {
            Instance aux;
            Instance m = (Instance) datos.get(i);
            aux = m.copy();
            c.addData(aux);
        }

        return c;
    }

}

