package keel.Algorithms.Decision_Trees.SLIQ;

import java.util.Comparator;

/**
 * Clase para gestionar la lista ordenada de cada atributo
 *
 * @author Francisco Charte Ojeda
 * @version 1.0 (30-12-09)
 */
public class ListaAtributos {
    /** Valor del atributo */
    public double valor;
    /** Índice que apunta a la lista de clases */
    public int indice;

    /** Constructor
     *
     * @param valor  Valor del atributo
     * @param indice Índice de la entrada en la lista de clases que le corresponde
     */
    public ListaAtributos(double valor, int indice) {
        this.valor = valor;
        this.indice = indice;
    }

    /**
     * Clase interna que facilita la comparación de objetos ListaAtributos
     * para facilitar la ordenación
     */
    static class Comparador implements Comparator {

        public int compare(Object o1, Object o2) {
            return ((ListaAtributos)o1).valor < ((ListaAtributos)o2).valor ? -1 : 1;
        }

    }
}
