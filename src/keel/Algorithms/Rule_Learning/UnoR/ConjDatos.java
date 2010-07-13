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

package keel.Algorithms.Rule_Learning.UnoR;

import java.util.*;

/**
 * <p>Title: Clase Conjunto de Datos</p>
 *
 * <p>Description: Es la 'estructura de datos' que contiene los ejemplos</p>
 *
 * <p>Copyright: Copyright Rosa (c) 2007</p>
 *
 * <p>Company: Mi casa</p>
 *
 * @author Rosa Venzala
 * @version 1.0
 */
public class ConjDatos {

    /**
     *  Esta clase almacena conjuntos de dato de la forma.. atr atr atr.. clas
     */

    private LinkedList datos;

    /**
     *Constructor. Inicializa las variables contenedoras
     */
    public ConjDatos() {
        datos = new LinkedList();
    }

    /**
     * Borra un dato
     * @param i Posicion a borrar
     */
    public void deleteDato(int i) {
        datos.remove(i);
    }

    /**
     * Aï¿½de un dato
     * @param m Ejemplo
     */
    public void addDato(Muestra m) {
        Muestra mim = m.copiaMuestra();
        datos.add(mim);
    }

    /**
     * Devuelve un ejemplo
     * @param i Posicion del ejemplo
     * @return El ejemplo o muestra en la posicion i-esima
     */
    public Muestra getDato(int i) {
        return (Muestra) datos.get(i);
    }

    /**
     * Asigna un dato. Modifica el que hubiese de antemano
     * @param i Posicion a insertar
     * @param m Ejemplo
     */
    public void setDato(int i, Muestra m) {
        datos.set(i, m);
    }

    /**
     * Devuelve el nmero de ejemplos de nuestro conjunto de datos
     * @return El tamaï¿½
     */
    public int size() {
        return (datos.size());
    }

    /**
     * Muestra por pantalla los ejemplos
     */
    public void print() {
        for (int i = 0; i < datos.size(); i++) {
            Muestra m = (Muestra) datos.get(i);
            m.print();
        }

    }

    /**
     * Copia el conjunto de datos en otro nuevo
     * @return Un nuevo conjunto de datos copia del actual
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
     * Hace que los atributos de todos los ejemplos estï¿½ en el intervalo [0,1]
     * @param datos Conjunto de datos
     */
    public void hazUniforme(Dataset datos) {
        datos.normaliza();
    }

}

