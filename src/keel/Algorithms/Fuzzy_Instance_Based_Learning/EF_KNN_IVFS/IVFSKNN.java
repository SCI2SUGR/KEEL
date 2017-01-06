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
 * File: IVFSKNN.java
 *
 * The IVFSKNN classifier.
 *
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011
 * @version 1.0
 * @since JDK1.5
 *
 */

package keel.Algorithms.Fuzzy_Instance_Based_Learning.EF_KNN_IVFS;

import keel.Algorithms.Fuzzy_Instance_Based_Learning.Util;
import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;


class IVFSKNN{

	private static final double MAX_NORM = 100000000;

    private static int K;
    private static int nClasses;
    private static double trainData[][];
    private static int trainOutput[];

	private double minM;
	private double maxM;
	
	private Interval membership [][];

    /**
     * Configure classifier settings
     *
     * @param kValue K value for knn
     * @param train Training set
     * @param classes Classes definition of the problem
     * @param nClasses Number of valid classes
     */
    public static void configureClass(int kValue, double [][] train, int [] classes, int nClasses){

        K = kValue;

        int inputAtt = train[0].length;
        IVFSKNN.nClasses = nClasses;
        trainData = new double [train.length][inputAtt];
        for(int i=0; i < train.length; i++){
            System.arraycopy(train[i], 0, trainData[i], 0, inputAtt);
        }

        trainOutput = new int [train.length];
        System.arraycopy(classes, 0, trainOutput, 0, train.length);

    }


    /**
     * Create a new IVFSKNN classifier from a chromosome
     *
     * @param solution Solution used to create the classifier
     *
     */
    public IVFSKNN(Chromosome solution) {

        ArrayList<Integer> kValues = new ArrayList<>();
        double mA = solution.getmA();
        double mB = solution.getmB();

        int [] kv = solution.getBody();
        for(int i = 0; i < kv.length; i++){
            if (kv[i] == 1){
                kValues.add(i+1);
            }
        }

        minM = Math.min(mA, mB);
        maxM = Math.max(mA, mB);

        assignTrainMembership(kValues);

    }

    /**
     * Compute class membership of all the instances in the training set
     *
     * @param kInits Values of K to use during computation
     */
    public void assignTrainMembership(ArrayList<Integer> kInits){

        membership = new Interval [trainData.length][nClasses];

        for(int i=0; i<trainData.length; i++){
            for(int j=0; j<nClasses; j++){
                membership[i][j] = new Interval();
            }
        }

        int maxKInit = Collections.max(kInits);

        Interval instance_memberships [];
        for(int instance = 0; instance < trainData.length; instance++){

            // find the k nearest examples in the training set
            int [] neighbors = findKNearest(trainData[instance], maxKInit, instance);

            instance_memberships = computeTrainMembership(neighbors, kInits);

            for(int k=0; k<nClasses; k++){
                membership[instance][k] = new Interval(instance_memberships[k]);
            }

        }

    } //end-method

    /**
     * Compute the membership of a train instance
     *
     * @param instances Neighbors of the instance
     * @param kInits Values of K to use during computation
     * @return Train membership represented as intervals
     */
    private Interval [] computeTrainMembership(int [] instances, ArrayList<Integer> kInits){

        double max_membership [] = new double [nClasses];
        double min_membership [] = new double [nClasses];
        int count [] = new int[nClasses];
        Arrays.fill(max_membership,0.0);
        Arrays.fill(min_membership,1.0);
        Arrays.fill(count,0);

        double term;
        double memb_value;
        for (int i=0; i < instances.length; i++) {
            int ins_class = trainOutput[instances[i]];
            count[ins_class]++;

            if(kInits.contains(i+1)){
                for (int k=0; k<nClasses; k++) {
                    term = ((double)count[k]/(double)(i+1));
                    memb_value = (k==ins_class)? 0.51+0.49*term:0.49*term;
                    max_membership[k] = Math.max(max_membership[k], memb_value);
                    min_membership[k] = Math.min(min_membership[k], memb_value);
                }

            }
        }

        Interval [] output = new Interval[nClasses];

        for (int k=0; k<nClasses; k++) {
            output[k] = new Interval(min_membership[k],max_membership[k]);
        }

        return output;
    }

    /**
     * Find the K nearest instances in the training set to a query
     *
     * @param query Query instance
     * @param k K value
     * @param trainIndex Instance in the training set to omit (if loo)
     *
     * @return Indexes if the K nearest neighbors to the query
     */
    private int [] findKNearest(double [] query, int k, int trainIndex){

        double minDist[];
        int nearestN[];
        double dist;
        boolean stop;

        nearestN = new int[k];
        minDist = new double[k];

        Arrays.fill(nearestN, -1);
        Arrays.fill(minDist, Double.MAX_VALUE);

        //KNN search

        for (int i=0; i<trainData.length; i++) {

            if (i != trainIndex){ //leave-one-out

                dist = Util.euclideanDistance(trainData[i], query);

                //see if it's near than the previously selected neighbors
                stop=false;

                for(int j=0;j<k && !stop;j++){

                    if (dist < minDist[j]) {

                        for (int l = k - 1; l >= j+1; l--) {
                            minDist[l] = minDist[l - 1];
                            nearestN[l] = nearestN[l - 1];
                        }

                        minDist[j] = dist;
                        nearestN[j] = i;
                        stop=true;
                    }
                }
            }
        }

        return nearestN;
    }

    /**
     * Estimate leave-one-out classification accuracy of the model
     *
     * @return Accuracy estimation
     */
    public double getLooScore(){

        int trainPrediction;
        int hits = 0;

        for(int i=0;i<trainData.length;i++){

            Interval [] membership = computeMembership(trainData[i], i);
            trainPrediction = computeClass(membership);
            hits += trainPrediction == trainOutput[i] ? 1:0;
        }

        return (double) hits / (double)trainData.length;
    }

    /**
     * Classify a test instance using the model
     *
     * @param instance Instance to classify
     * @return class computed
     */
    public int classifyInstance(double [] instance){

        Interval [] membership = computeMembership(instance, -1);

        return computeClass(membership);
    }

    /**
     * Evaluates a instance to predict its class membership
     *
     * @param index Index of the instance in the test set
     * @param example Instance evaluated
     *
     */
    private Interval [] computeMembership(double example[], int index) {

        //prepare votes
        Interval [] votes = new Interval[nClasses];
        for(int k = 0; k < nClasses; k++){
            votes[k] = new Interval(0.0, 0.0);
        }

        // find the k nearest examples in the training set
        int [] neighbors = findKNearest(example, K, index);

        double minExp = 2.0/(minM-1.0);
        double maxExp = 2.0/(maxM-1.0);
        for(int neighbor: neighbors){

            // compute weighted norm
            double distance =  Util.euclideanDistance(example, trainData[neighbor]);

            double norm_low = MAX_NORM;
            double norm_high = MAX_NORM;
            if(distance!=0.0){
                norm_low = 1.0 / Math.pow(distance, minExp);
                norm_high = 1.0 / Math.pow(distance, maxExp);
            }

            Interval norm = new Interval(norm_low, norm_high);
            for(int k = 0; k < nClasses; k++){

                Interval vote = new Interval(norm);
                vote.timesInterval(membership[neighbor][k]);
                votes[k].addInterval(vote);
            }
        }

        return votes;

    } //end-method

    /**
     * Estimate the most likely class based on the votes obtained
     *
     * @param votes Neighbor's votes
     * @return Class estimated for the test instance
     */
    private int computeClass(Interval [] votes){

        double bestLower = votes[0].getA();
        double bestUpper = votes[0].getB();
        int best_class = 0;

        //lexicographic order with respect to the lower bound
        for(int k = 1; k < nClasses; k++){
            double low = votes[k].getA();
            double high = votes[k].getB();
            if(low > bestLower){
                bestLower = low;
                bestUpper = high;
                best_class = k;
            }
            else if((low == bestLower) && (high > bestUpper)){
                bestLower = low;
                bestUpper = high;
                best_class = k;
            }

        }

        return best_class;
    }

} //end-class

