/*
 * Created on 27-feb-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.Comparator;
import java.util.Vector;

/**
 * @author Sebas
 */
public class AtributoComparator implements Comparator {
    private int atributo;
    private int clase;

    /**
     * Constructor
     * @param atributo
     * @param clase
     */
    public AtributoComparator(int atributo, int clase) {
        this.atributo = atributo;
        this.clase = clase;
    }


    /**
     * Compare
     * @param arg0
     * @param arg1
     */
    public int compare(Object arg0, Object arg1) {
        int result = 0;
        double valor0;
        double valor1;

        if (((Vector) arg0).get(atributo) instanceof Double) {
            valor0 = ((Double) ((Vector) arg0).get(atributo)).doubleValue();
            valor1 = ((Double) ((Vector) arg1).get(atributo)).doubleValue();
        } else {
            valor0 = ((Integer) ((Vector) arg0).get(atributo)).doubleValue();
            valor1 = ((Integer) ((Vector) arg1).get(atributo)).doubleValue();
        }

        if (valor0 < valor1) {
            result = -1;
        } else if (valor0 > valor1) {
            result = 1;
        } else {
            int clase0 = ((Integer) ((Vector) arg0).get(clase)).intValue();
            int clase1 = ((Integer) ((Vector) arg1).get(clase)).intValue();

            if (clase0 < clase1) {
                result = -1;
            } else if (clase0 > clase1) {
                result = 1;
            }
        }
        return result;
    }
}
