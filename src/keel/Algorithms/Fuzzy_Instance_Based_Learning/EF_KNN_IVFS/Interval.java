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
 * File: Interval.java
 *
 * An implementation of (ordered) intervals for the EFKNNIVFS algorithm.
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

class Interval {

    private double a;
    private double b;

    /**
     * Create a new (zeroed) interval
     */
    public Interval(){
        a = 0;
        b = 0;
    }

    /**
     * Create a copy of an interval
     * @param o Original interval to copy
     */
    public Interval(Interval o){
        a = o.a;
        b = o.b;
    }

    /**
     * Build a new interval, respecting the order of values (lower first)
     * @param _a First value
     * @param _b Second value
     */
    public Interval(double _a, double _b){

        a = (_a < 0.0) ? 0.0:_a;
        b = (_b < 0.0) ? 0.0:_b;

        if(a > b){
            double aux = a;
            a = b;
            b = aux;
        }
    }

    /**
     * Interval addition operation
     * @param o Interval to add
     */
    public void addInterval(Interval o){

        a += o.a;
        b += o.b;
    }

    /**
     * Interval multiplication operation
     * @param o Interval to multiply
     */
    public void timesInterval(Interval o){

        double min = a * o.a;
        double max = min;

        double val = a * o.b;
        min = (min < val) ? min:val;
        max = (max > val) ? max:val;

        val = b * o.a;
        min = (min < val) ? min:val;
        max = (max > val) ? max:val;

        val = b * o.b;
        min = (min < val) ? min:val;
        max = (max > val) ? max:val;

        a = min;
        b = max;

    }

    /**
     * Get the lower value of the interval
     * @return Lower value of the interval
     */
    public double getA(){
        return a;
    }

    /**
     * Get the higher value of the interval
     * @return Higher value of the interval
     */
    public double getB(){
        return b;
    }

    /**
     * String representation of the interval
     * @return String representation of the interval
     */
    public String toString() {
        return "["+a+","+b+"]";
    }
}
