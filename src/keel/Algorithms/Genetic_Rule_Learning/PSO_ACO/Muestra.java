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

import java.util.*;
import keel.Dataset.*;

/**
 * <p>Title: Muestra (Sample)</p>
 * <p>Description: Sample class:
 *    Stores an example of a dataset. These examples are treated as rules, 
 *    every pair attribute-value is considered to be conditions for the class of the example. </p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */
public class Muestra {

    private Vector condiciones; //Atributos de la regla
    private Atributo clase; //Clase de la regla
    private int posicion; //Posicion en el fichero de donde se ha extraido
    private boolean cubierta; //Indica si la muestra esta cubierta por alguna regla

  /**
   *  Default constructor. An empty sample is built.
   */
    public Muestra() {
        condiciones = new Vector();
        clase = new Atributo();
        posicion = -1;
        cubierta = false;
    }

  /**
     *  Paramater Constructor.
     *  Creates an example with the attributes values and the class given.
     *
     * @param conjuntoAtributos Vector attributes values.
     * @param claseOriginal String example class.
     * @param posicionFichero int file position (example id).
     */
    public Muestra(Vector conjuntoAtributos, Atributo claseOriginal,
                   int posicionFichero) {

        condiciones = new Vector(conjuntoAtributos);
        clase = new Atributo(claseOriginal);
        posicion = posicionFichero;
        cubierta = false;

    }

    /**
     * Adds an attribute with its value to the example.
     *
     * @param original {@link Atributo} pair attribute value to add.
     */
    public void insertarAtributo(Atributo original) {
        condiciones.addElement(original);
    }

    /**
     * Adds a class that identifies the example/rule with all its attributes.
     *
     * @param original {@link Atributo} class added to the rule.
     */
    public void insertarClase(Atributo original) {
        clase = original;
    }

    /**
     * Sets tha position of the example in the file (example id).
     * @param posicionFichero int given position.
     */
    public void insertaPosicion(int posicionFichero) {
        posicion = posicionFichero;

    }

    /**
     * Sets the example as covered.
     */

    public void siEstaCubierta() {
        cubierta = true;

    }

    /**
     * Sets the example as uncovered.
     */
    public void noEstaCubierta() {
        cubierta = false;

    }

    /**
     * Checks if the example is covered.
     * @return True if the example is covered, false otherwise.
     */
    public boolean estaCubierta() {
        return cubierta;

    }
    
    /**
     * Checks if the given attribute is already in the example.
     *
     * @param original given attribute to be checked.
     * @return True if the given attribute is already in the example, false otherwise. 
     */
    public boolean estaAtributo(Atributo original) {
        boolean devolver = condiciones.contains(original);
        return devolver;
    }

    /**
     * Checks if the class of the example is the same as the one passed as parameter.
     * @param original Class to compare with.
     * @return True if the class is the same, false otherwise.
     */
    public boolean estaClase(Atributo original) {
        boolean devolver;
        float s1 = clase.getValor();
        float s2 = original.getValor();
        if (s1 == s2) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Returns the value for the attribute/condition in the given position.
     * @param indice int attribute position.
     * @return double value of the attribute in the given position.
     */
    public Atributo getValor(int indice) {
        Atributo actual = (Atributo) condiciones.get(indice);
        return actual;

    }

    /**
     * Returns the class of this example.
     * @return the class of this example.
     */
    public Atributo getClase() {
        return clase;

    }

    /**
     * Prints on the standard output the example as rule (conditions ---> class).
     */
    public void imprimir() {
        float valor;
        Atributo at;
        int i;
        for (i = 0; i < condiciones.size() - 1; i++) {
            at = (Atributo) condiciones.get(i);
            valor = at.getValor();
            System.out.print(valor + " AND ");
        }

        //La ultima iteracion no se pinta el AND
        at = (Atributo) condiciones.get(i);
        valor = at.getValor();
        System.out.print(valor);

        //Ultima iteracion
        Attribute actual;
        Vector nombres;
        String nombre = null;
        actual = Attributes.getOutputAttribute(0); //Solo tenemos un atributo de salida en esta clasificacion
        nombres = actual.getNominalValuesList();

        valor = clase.getValor();
        nombre = (String) nombres.get((int) valor);
        System.out.print(" ---> " + valor + " \n");

    }


}

