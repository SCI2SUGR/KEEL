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

package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner;

import java.util.*;

/**
 * <p>Título: Ant Colony Optimization</p>
 * <p>Descripción: Clase conjunto de datos.
 *    Representa el conjunto de datos leidos del fichero en un formato entendible
 *    para el algoritmo ACO </p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class ConjuntoDatos {

    private Vector muestras; //Vector con las muestras sacadas del fichero

    /**
     * Constructor por defecto
     * Crea un conjunto vacio.
     */
    public ConjuntoDatos() {
        muestras = new Vector();
    }


    /**
     * Constructor a partir de un vector de muestras
     * @param datos Vector Vector con las muestras a incluir en el conjunto de datos
     */
    public ConjuntoDatos(Vector datos) {
        muestras = new Vector(datos);
    }

    /**
     * Modulo que inserta una muestra en el conjunto de datos
     * @param ejemplo Muestra
     */
    public void insertaMuestra(Muestra ejemplo) {
        //addElement Añade un elemento al final del vector (perfecto)
        muestras.addElement(ejemplo);
    }

    /**
     * Modulo que elimina una muestra del conjunto de datos
     * @param indice int Indice de la muestra a eliminar
     */
    public void eliminaMuestra(int indice) {

        muestras.removeElementAt(indice);
    }

    /**
     * Funcion que elimina una muestra del conjunto de datos
     * @param muestra Muestra Muestra a eliminar del conjunto de datos
     * @return boolean Booleano que indica si ha sido eliminado o por el contrario no
     */
    public boolean eliminaMuestra(Muestra muestra) {
        boolean devolver;
        devolver = muestras.remove(muestra);
        return devolver;
    }

    /**
     * Funcion que devuelve la primera muestra del conjunto de datos
     * @return Muestra
     */
    public Muestra obtenerMuestra() {
        Muestra devolver;
        devolver = (Muestra) muestras.firstElement();
        return devolver;

    }

    /**
     * Funcion que devuelve la muestra del conjunto de datos en la posicion indice
     * @param indice int Indice de la muestra a obtener
     * @return Muestra
     */

    public Muestra obtenerMuestra(int indice) {
        Muestra devolver;
        devolver = (Muestra) muestras.get(indice);
        return devolver;
    }

    /**
     * Modulo que imprime el conjunto de datos entero por pantalla
     * @param nombre String Nombre del conjunto de datos
     */

    public void imprimir(String nombre) {
        Muestra actual;

        System.out.println("Conjunto de muestras " + nombre);
        for (int i = 0; i < muestras.size(); i++) {
            actual = (Muestra) muestras.get(i);
            actual.imprimir();
        }
    }

    /**
     * Funcion que devuelve el numero de muestras que posee el conjunto de datos
     * @return int Numero de muestras que contiene el conjunto
     */
    public int tamanio() {
        int devolver;
        devolver = muestras.size();
        return devolver;

    }


    /**
     * Funcion que devuelve un vector de probabilidades de que haya en las muestras Aij y cada W
     * @param atributo Atributo Atributo junto con su valor
     * @param clases Vector Lista con las distintas clases que hay
     * @return float [] Vector con las probabilidades.
     */
    public float[] listaProbabilidadesAtributoClase(Atributo atributo,
            Vector clases) {
        int numClases = clases.size();
        float[] devolver = new float[numClases];
        Muestra actual;
        int total = 0;
        boolean terminado = false;
        Atributo clase;

        for (int i = 0; i < numClases; i++) {
            devolver[i] = 0;
        }

        for (int i = 0; i < muestras.size(); i++) {
            actual = (Muestra) muestras.get(i);
            if (actual.estaAtributo(atributo)) { //Esta el atributo
                total++;
                terminado = false;
                for (int j = 0; j < numClases && !terminado; j++) {
                    clase = (Atributo) clases.get(j);
                    if (actual.estaClase(clase)) {
                        devolver[j]++;
                        terminado = true;
                    }
                }
            }
        }

        for (int i = 0; i < numClases; i++) {
            if (devolver[i] != 0) {
                devolver[i] = devolver[i] / total;
            }
        }

        return devolver;

    }

    /**
     * Modulo que elimina las muestras cubiertas por la regla que se pasa por parametro
     * @param regla Regla Regla que debe cubrir las muestras a eliminar
     */
    public void eliminaMuestrasCubiertas(Regla regla) {
        Vector lista = new Vector();
        Muestra mt;

        for (int i = 0; i < muestras.size(); i++) {
            mt = (Muestra) muestras.get(i);
            if (regla.cubreMuestra(mt)) {
                lista.addElement(mt);
            }
        }

        muestras.removeAll(lista);

    }

    /**
     * Funcion que calcula el porcentaje de muestras cubiertas por la regla que se
     * le pasa por parametro.
     * @param regla Regla Regla a comprobar su numero de muestras cubiertas.
     * @return float Porcentaje de muestras cubiertas.
     */
    public float porcentajeMuestrasCubiertas(Regla regla) {
        float porcentaje = 0;
        float total = muestras.size();
        float cubiertas = 0;
        Muestra mt;
        Vector eliminar = new Vector();

        for (int i = 0; i < muestras.size(); i++) {
            mt = (Muestra) muestras.get(i);
            if (regla.cubreMuestra(mt)) {
                cubiertas++;
                eliminar.addElement(mt);
            }
        }

        muestras.removeAll(eliminar);
        porcentaje = cubiertas;
        return porcentaje;
    }

    /**
     * Funcion que devuelve un Vector con las muestras que contiene el conjunto
     * de datos.
     * @return Vector Muestras que contiene el conjunto de datos.
     */
    public Vector obtenerMuestras() {
        Vector devolver = new Vector(muestras);
        return devolver;
    }

    /**
     * Funcion que devuelve la clase que aparece mas veces en el conjunto de datos
     * @param listaClases Vector Lista con las distintas clases que pueden aparecer
     * @return int Numero de la clase que aparece mas veces en el conjunto de datos
     */
    public int obtenerMayorClase(Vector listaClases) {
        int[] ocurrencias = new int[listaClases.size()];
        Muestra mt;
        Atributo clase;
        Atributo original;
        int mejor = 0;
        int posMejor = 0;

        for (int i = 0; i < listaClases.size(); i++) {
            ocurrencias[i] = 0;
        }

        for (int i = 0; i < muestras.size(); i++) {
            mt = (Muestra) muestras.get(i);
            clase = mt.getClase();
            for (int j = 0; j < listaClases.size(); j++) {
                original = (Atributo) listaClases.get(j);
                if (clase.esIgual(original)) {
                    ocurrencias[j]++;
                }
            }
        }

        for (int i = 0; i < listaClases.size(); i++) {
            if (ocurrencias[i] > mejor) {
                mejor = ocurrencias[i];
                posMejor = i;
            }
        }

        return posMejor;

    }
}

