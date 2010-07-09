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
     * A�de un dato
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
     * @return El tama�
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
     * Hace que los atributos de todos los ejemplos est� en el intervalo [0,1]
     * @param datos Conjunto de datos
     */
    public void hazUniforme(Dataset datos) {
        datos.normaliza();
    }

}
