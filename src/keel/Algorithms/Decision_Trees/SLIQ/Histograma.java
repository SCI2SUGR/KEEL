
package keel.Algorithms.Decision_Trees.SLIQ;

import java.util.Vector;

/**
 * Clase que representa el histograma asociado a los nodos del árbol
 *
 * @author Francisco Charte Ojeda
 * @version 1.0 (29-12-09)
 */
public class Histograma {
    // Elementos que quedan a izquierda y derecha
    Vector<Integer>
            izquierda = new Vector<Integer>(),
            derecha = new Vector<Integer>();

    // Número de elementos en cada parte
    int totalIzquierda, totalDerecha, total;

    // Constructor por defecto
    Histograma() {
        total = totalIzquierda = totalDerecha = 0;
    }


    // Constructor para un cierto número de clases
    Histograma(int numeroClases) {
        total = totalIzquierda = totalDerecha = 0;

        for(int indice = 0; indice < numeroClases; indice++) {
            izquierda.add(0);
            derecha.add(0);
        }
    }

    // Actualización tras procesar una fila de datos
    void actualiza(int clase) {
       izquierda.set(clase, izquierda.get(clase) - 1);
       derecha.set(clase, derecha.get(clase) + 1);

       totalIzquierda--;
       totalDerecha++;
    }

    // Incrementar el contador de una cierta clase
    void incIzquierda(int clase) {
        total++;
        totalIzquierda++;

        izquierda.set(clase, izquierda.get(clase) + 1);
    }

    // Se intercambian los contenidos de los vectores
    void intercambia() {
        Vector<Integer> temporal = (Vector<Integer> )izquierda.clone();
        izquierda.removeAllElements();
        izquierda.addAll(derecha);
        derecha.removeAllElements();
        derecha.addAll(temporal);

        totalIzquierda = totalDerecha;
        totalDerecha = 0;
    }

    // Calcula el índice Gini a partir del histograma actual
    double gini() {
        double resultado = 0, frecuencia = 0.0, temporal = 1.0;

        if(totalDerecha != 0) {
            for(int indice = 0; indice < derecha.size(); indice++)
                if(derecha.get(indice) != 0) {
                    frecuencia = derecha.get(indice).floatValue() / (float )totalDerecha;
                    temporal -= frecuencia * frecuencia;
                }

            resultado += temporal * totalDerecha;
            temporal = 1.0;
        }

        if(totalIzquierda != 0) {
            for(int indice = 0; indice < izquierda.size(); indice++)
                if(izquierda.get(indice) != 0) {
                    frecuencia = izquierda.get(indice).floatValue() / (float )totalIzquierda;
                    temporal -= frecuencia * frecuencia;
                }

            resultado += temporal * totalIzquierda;
        }

        return resultado / (float )total;
    }



}
