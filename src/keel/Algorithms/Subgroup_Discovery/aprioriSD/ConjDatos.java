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

package keel.Algorithms.Subgroup_Discovery.aprioriSD;

import java.util.*;


/**
 * <p> Stores a set of data with the form: attribute attribute..class.
 * @author José Ramón Cano de Amo
 * @version 1.1
 * @since JDK1.2
 * </p>
 */
public class ConjDatos {

  /**
   *  Esta clase almacena conjuntos de dato de la forma.. atr atr atr.. clas
   */

  private LinkedList datos;
//	private int nAtrib;

    /**
     * <p>
     * Constructor. Initializes the variables
     * </p>
     */
  public ConjDatos() { //int nAtributos) {
    super();
    datos = new LinkedList();
//		nAtrib=nAtributos;
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

//	public int getNatributos(){
//		return nAtrib;
//	}

    /**
     * <p>
     * Returns an example
     * </p>
     * @param i Position of the example
     * @return The example
     */
  public Muestra getDato(int i) {
    Muestra m = (Muestra) datos.get(i);
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
    int i;
    for (i = 0; i < datos.size(); i++) {
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
    int i;
    ConjDatos c = new ConjDatos();

    for (i = 0; i < datos.size(); i++) {
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
  public void hazUniforme(Dataset datos){
    datos.normaliza();
  }

}

