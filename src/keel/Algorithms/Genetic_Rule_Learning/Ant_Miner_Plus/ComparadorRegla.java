package keel.Algorithms.Genetic_Rule_Learning.Ant_Miner_Plus;

import java.util.Comparator;

/**
 * <p>Título: Ant Colony Optimization</p>
 * <p>Descripción:Comparador entre reglas</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class ComparadorRegla implements Comparator {
    public ComparadorRegla() {
    }

    /**
     * Compara dos reglas
     *
     * @param o1 Object Regla a comparar
     * @param o2 Object Regla a comparar
     * @return int Devuelve 0 si tienen la misma posicion, 1 si el primero esta
     * antes, -1 si el primero esta despues.
     *
     *
     */
    public int compare(Object o1, Object o2) {
        float calidad1;
        float calidad2;
        Regla regla1;
        Regla regla2;
        int devolver = 0;

        regla1 = (Regla) o1;
        regla2 = (Regla) o2;

        calidad1 = regla1.obtenerCalidad();
        calidad2 = regla2.obtenerCalidad();

        float muestras1 = regla1.obtenerMuestrasCubiertas();
        float muestras2 = regla2.obtenerMuestrasCubiertas();

        if (calidad1 == calidad2) { //Para ver si son iguales tiene que coincidir tambien el valor
            if (muestras1 < muestras2) {
                devolver = 1;
            } else
            if (muestras1 > muestras2) {
                devolver = -1;
            } else {
                devolver = 1;
            }
        } else {
            if (calidad1 < calidad2) {
                devolver = 1;
            } else {
                if (calidad1 > calidad2) {
                    devolver = -1;
                }
            }
        }
        return devolver;

    }


}
