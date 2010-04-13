/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada) 02/06/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 16/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.Prism;

import java.util.*;

public class ConjDatos {
/**
 * Stores a set of data with the form: attribute attribute... class
 */
    private LinkedList datos;

    /**
     * <p>
     * Constructor. Initializes the variables
     * </p>
     */
    public ConjDatos() {
        datos = new LinkedList();
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

    /**
     * <p>
     * Returns an example
     * </p>
     * @param i Position of the example
     * @return The example
     */
    public Muestra getDato(int i) {
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
        for (int i = 0; i < datos.size(); i++) {
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
     * <p>
     * Adapt the examples to the [0,1] interval
     * </p>
     * @param datos Set of data
     */
    public void hazUniforme(Dataset datos) {
        datos.normaliza();
    }

}
