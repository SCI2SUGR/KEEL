/*
 * Created on 11-jun-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.Comparator;
import java.util.Vector;

/**
 * @author Sebas
 */
public class IndividuoComparator implements Comparator {
    private int posBondad;

    /**
     *
     * @param posBondad
     */
    public IndividuoComparator(int posBondad) {
        this.posBondad = posBondad;
    }

    /**
     * Compare
     * @param arg0
     * @param arg1
     */
    public int compare(Object arg0, Object arg1) {
        int result = 0;

        double valor0 = ((Double) ((Vector) arg0).get(posBondad)).doubleValue();
        double valor1 = ((Double) ((Vector) arg1).get(posBondad)).doubleValue();

        if (valor0 < valor1) {
            result = -1;
        } else if (valor0 > valor1) {
            result = 1;
        }

        return result;
    }
}
