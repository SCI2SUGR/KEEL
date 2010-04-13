package keel.Algorithms.Genetic_Rule_Learning.PSO_ACO;

import java.util.Comparator;

/**
 * <p>Título: Hibridación Pso Aco</p>
 * <p>Descripción: Hibridacion entre los dos algoritmos Pso y Aco</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Empresa: </p>
 * @author Vicente Rubén del Pino
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
        int reglas1;
        int reglas2;

        regla1 = (Regla) o1;
        regla2 = (Regla) o2;

        calidad1 = regla1.obtenerCalidad();
        calidad2 = regla2.obtenerCalidad();
        reglas1 = regla1.obtenerNumeroMuestrasCubiertas();
        reglas2 = regla2.obtenerNumeroMuestrasCubiertas();

        if (calidad1 < calidad2) {
            devolver = 1;
        } else {
            if (calidad1 > calidad2) {
                devolver = -1;
            } else {
                if (reglas1 < reglas2) {
                    devolver = 1;
                } else
                if (reglas1 > reglas2) {
                    devolver = -1;
                } else {
                    devolver = 1;
                }
            }
        }

        return devolver;

    }


}
