/*
 * Rand.java
 *
 * Created on 29 de marzo de 2004, 23:31
 */

/**
 *
 */

package keel.Algorithms.Genetic_Rule_Learning.Globals;

import java.util.*;

public class Rand {
    
    private static MTwister random;
    
    /** Generates a new instance of Random */
    public static void initRand() {
        random = new MTwister(Parameters.seed);
    }
    
    /**
     *  Returns a random real between [0,1)
     */
    public static double getReal() {
        return random.genrand_real2();
    }
    
    /**
     *  Returns a random long between [uLow,uHigh]
     */
    public static int getInteger(int uLow, int uHigh) {
    	return (uLow + (int)(random.genrand_real2()*(uHigh + 1 - uLow)));
    }
    
}
