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
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
 * @version 1.0
 */

public class Muestra {

    private Vector condiciones; //Atributos de la regla
    private Atributo clase; //Clase de la regla
    private int posicion; //Posicion en el fichero de donde se ha extraido
    private boolean cubierta; //Indica si la muestra esta cubierta por alguna regla

    /**
     * Constructor por defecto
     * Crea una muestra vacia
     */
    public Muestra() {
        condiciones = new Vector();
        clase = new Atributo();
        posicion = -1;
        cubierta = false;
    }

    /**
     *  Constructor
     *  Crea una regla a partir de los atributos y la clase que se le pasan
     *  por parametro.
     *
     * @param conjuntoAtributos Vector
     * @param claseOriginal String
     * @param posicionFichero int
     */

    public Muestra(Vector conjuntoAtributos, Atributo claseOriginal,
                   int posicionFichero) {

        condiciones = new Vector(conjuntoAtributos);
        clase = new Atributo(claseOriginal);
        posicion = posicionFichero;
        cubierta = false;

    }

    /**
     * Modulo que inserta un atributo junto con su valor en la regla
     *
     * @param original Atributo Atributo que se inserta en la regla.
     */
    public void insertarAtributo(Atributo original) {
        condiciones.addElement(original);
    }

    /**
     * Modulo que inserta la clase a la que identifica la regla con todos
     * sus atributos
     *
     * @param original Atributo Clase que se inserta en la regla
     */
    public void insertarClase(Atributo original) {
        clase = original;
    }

    /**
     * Inserta la posicion que ocupa la muestra en el fichero de origen
     * @param posicionFichero int
     */
    public void insertaPosicion(int posicionFichero) {
        posicion = posicionFichero;

    }

    /**
     * Modulo que pone a cubierta la muestra
     */

    public void siEstaCubierta() {
        cubierta = true;

    }

    /**
     * Modulo que pone a no cubierta la muestra
     */
    public void noEstaCubierta() {
        cubierta = false;

    }

    /**
     * Funcion que indica si la muestra esta cubierta o no
     * @return boolean Booleano que indica si esta cubierta la muestra
     */
    public boolean estaCubierta() {
        return cubierta;

    }

    /**
     * Funcion que avisa si un atributo esta ya junto con su valor en una regla
     *
     * @param original Atributo Atributo que se busca en la regla
     * @return boolean True si el atributo esta en la regla, False en caso contrario
     */
    public boolean estaAtributo(Atributo original) {
        boolean devolver = condiciones.contains(original);
        return devolver;
    }

    /**
     * Funcion que avisa si la clase que tiene la muestra es igual que la pasada por parametro
     * @param original Atributo Clase a comparar con la que contiene la muestra
     * @return boolean Booleano que indica si la clase es igual (true) o no (false)
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
     * Funcion que devuelve el valor de la condicion que esta en la posicion indice
     * @param indice int Indice de la condicion
     * @return double Valor de la condicion
     */
    public Atributo getValor(int indice) {
        Atributo actual = (Atributo) condiciones.get(indice);
        return actual;

    }

    /**
     * Funcion que devuelve el valor de la clase
     * @return double Valor de la clase de la muestra
     */
    public Atributo getClase() {
        return clase;

    }

    /**
     * Modulo que imprime por pantalla todas las condiciones de la muestra, seguidas de su
     * clase correspondiente
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

