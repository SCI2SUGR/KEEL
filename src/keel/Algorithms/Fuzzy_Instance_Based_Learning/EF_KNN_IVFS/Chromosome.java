/***********************************************************************

 This file is part of KEEL-software, the Data Mining tool for regression,
 classification, clustering, pattern mining and so on.

 Copyright (C) 2004-2010

 F. Herrera (herrera@decsai.ugr.es)
 L. Sánchez (luciano@uniovi.es)
 J. Alcalá-Fdez (jalcala@decsai.ugr.es)
 S. García (sglopez@ujaen.es)
 A. Fernández (alberto.fernandez@ujaen.es)
 J. Luengo (julianlm@decsai.ugr.es)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see http://www.gnu.org/licenses/

 **********************************************************************/

/**
 *
 * File: Chromosome.java
 *
 * Implementation of the chromosomes of EF-KNN-IVFS
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import org.core.Randomize;

class Chromosome implements Comparable<Chromosome>{

    private int body [];
    private double mA;
    private double mB;

    private double fitness;

    private static final double minM = 1.0;
    private static final double maxM = 4.0;
    private static final int length = 32;

    private static final double blx_alpha = 0.5;
    private static final double mutation_ratio = 0.35;


    /**
     * Create a new chromosome, with random initialization
     */
    public Chromosome(){

        body = new int [length];

        for(int i=0; i<length; i++){
            body[i] = Randomize.RandintClosed(0,1);
        }

        double m1 = Randomize.Randdouble(minM, maxM);
        double m2 = Randomize.Randdouble(minM, maxM);

        mA = Math.min(m1, m2);
        mB = Math.max(m1, m2);

        fitness = -1.0;

    }

    /**
     * Create a copy of a chromosome
     * @param o Original chromosome
     */
    public Chromosome(Chromosome o){

        body = new int [length];

        System.arraycopy(o.body, 0, body, 0, length);

        mA = o.mA;
        mB = o.mB;
        fitness = o.fitness;
    }

    /**
     * Get lower M value
     * @return Lower M value
     */
    public double getmA() {
        return mA;
    }

    /**
     * Get higher M value
     * @return Higher M value
     */
    public double getmB() {
        return mB;
    }

    /**
     * Access to the binary part of the chromosome
     * @return The binary part of the chromosome
     */
    public int[] getBody() {
        return body;
    }

    /**
     * Get current fitness of the chromosome
     * @return Fitness value of the chromosome
     */
    public double getFitness() {
        return fitness;
    }

    /**
     * Set a fitness value
     * @param fit Value to set
     */
    public void setFitness(double fit) {
        fitness = fit;
    }

    /**
     * Crossover operator. Uses HUX for the binary part
     * and BLX-0.5 for the real valued components
     * @param second Second chromosome in the crossover process
     */
    public void crossover(Chromosome second){

        int index=0;
        int [] diff=new int [length];

        // HUX: 1) Find non-matching positions
        for(int i=0;i<length;i++){

            if(body[i]!=second.body[i]){
                diff[index]=i;
                index++;
            }
        }

        // HUX: 2) Take half of non-matching positions randomly
        for (int i=0; i<index; i++) {

            int pos = Randomize.Randint (0, index);
            int tmp = diff[i];
            diff[i] = diff[pos];
            diff[pos] = tmp;
        }

        index=index/2;

        // HUX: 3) Exchange selected positions
        for(int i=0;i<index;i++){
            int aux=body[diff[i]];
            body[diff[i]]=second.body[diff[i]];
            second.body[diff[i]]=aux;

        }

        // BLX-0.5: 1) mA value

        double min=Math.min(mA, second.mA);
        double max=Math.max(mA, second.mA);
        double range = (max-min) * blx_alpha;

        min = Math.max(minM, min - range);
        max = Math.min(maxM, max + range);

        mA = Randomize.RanddoubleOpen(min,max);
        second.mA = Randomize.RanddoubleOpen(min,max);

        // BLX-0.5: 1) mB value

        min=Math.min(mB, second.mB);
        max=Math.max(mB, second.mB);
        range = (max-min) * blx_alpha;

        min = Math.max(minM, min - range);
        max = Math.min(maxM, max + range);

        mB = Randomize.RanddoubleOpen(min,max);
        second.mB = Randomize.RanddoubleOpen(min,max);

        // sort m values
        double m1 = Math.min(mA, mB);
        double m2 = Math.max(mA, mB);
        mA = m1;
        mB = m2;

        m1 = Math.min(second.mA, second.mB);
        m2 = Math.max(second.mA, second.mB);
        second.mA = m1;
        second.mB = m2;

        // cancel fitness values
        fitness=-1.0;
        second.fitness=-1.0;

    }

    /**
     * Mutate a chromosome (during CHC stagnation)
     */
    public void mutate(){

        List<Integer> indexes = new ArrayList<>();

        for(int i = 0; i < length; i++){
            indexes.add(i);
        }

        Collections.shuffle(indexes);

        int how_many = (int)(length * mutation_ratio);

        for(int i = 0; i < how_many; i++){
            int index = indexes.get(i);
            body[index] = (body[index] + 1) % 2;
        }

    }

    /**
     * Compute the hamming distance of two chromosomes
     *
     * @param o Chromosome to compare with
     * @return Distance between both chromosomes
     */
    public int distance(Chromosome o){

        int distance = 0;

        for(int i=0; i<o.body.length; i++){
            distance += body[i] != o.body[i] ? 1: 0;
        }

        return distance;
    }

    /**
     * String representation of the chromosome
     * @return String representation of the chromosome
     */
    public String toString() {

        String output = Arrays.toString(body);
        output += ", ["+mA+","+mB+"] = "+fitness;

        return output;
    }

    /**
     * Comparison operator, based on fitness
     * @param o Chromosome to compare with
     * @return Comparison order
     */
    public int compareTo(Chromosome o){
        Double fit = o.fitness;
        return fit.compareTo(fitness);
    }

}
