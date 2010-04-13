package keel.GraphInterKeel.experiments;

import java.util.*;

class Randomize {

    private static long Seed;
    private static Random generador = new Random();

    /**
     * Sets the seed of the random generator with a specific value
     *
     * @param seed New seed
     */
    public static void setSeed(long seed) {
        Seed = seed;
        generador.setSeed(Seed);
    }

    /**
     * Rand computes a psuedo-random float value between 0 and 1, excluding 1
     * @return a value between 0 and 1, excluding 1
     */
    public static double Rand() {
        return (generador.nextDouble());
    }

    /**
     * Randint gives an integer value between low and high inclusive
     * @param low lower bound
     * @param high higher bound
     * @return a value between low and high inclusive
     */
    public static int Randint(int low, int high) {
        return ((int) (low + (high - low + 1) * Rand()));
    }

    /**
     * Randfloat gives a float value between low and high, including low and excluding high
     * @param low lower bound
     * @param high higher bound
     * @return a float value between low and high, including low and excluding high
     */
    public static double Randdouble(double low, double high) {
        return (low + (high - low) * Rand());
    }
}
