
package keel.Algorithms.Decision_Trees.SLIQ;

/**
 * Clase para gestionar la lista de clases de un dataset
 *
 * @author Francisco Charte Ojeda
 * @version 1.0 (30-12-09)
 */
public class ListaClases {
    /** Índice de la clase */
    public int clase;
    /** Nodo hoja asociado */
    public Node hoja;

    /** Constructor
     *
     * @param clase Índice de la clase
     * @param hoja  Nodo hoja asociado
     */
    public ListaClases(int clase, Node hoja) {
        this.clase = clase;
        this.hoja = hoja;
    }
}
