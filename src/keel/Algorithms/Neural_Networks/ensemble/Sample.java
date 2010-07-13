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

package keel.Algorithms.Neural_Networks.ensemble;

/**
 * <p>
 * Class that represents a sample of data
 * </p>
 * @author Written by Nicolas Garcia Pedrajas (University of Cordoba) 27/02/2007
 * @version 0.1
 * @since JDK1.5
 */
public class Sample {

    /** Sample probability in each pattern */
    double probs[];

    /** Accumulated probability */
    double cum_probs[];

    /** No of patterns */
    int npatterns;

    /**
     * <p>
     * Constructor
     * </p>
     * @param patterns No of patterns
     */
    public Sample(int patterns) {
        npatterns = patterns;
        probs = new double[npatterns];
        cum_probs = new double[npatterns];
    }

    /**
     * <p>
     * Uniform sample
     * </p>
     */
    public void GetEqualSample() {

        for (int i = 0; i < npatterns; i++) {
            probs[i] = 1.0;
            cum_probs[i] = i + 1.0;
        }

    }

    /**
     * <p>
     * Return a bagging sample
     * </p>
     */
    public void GetBaggingSample() {

        for (int i = 0; i < npatterns; i++) {
            probs[Genesis.irandom(0, npatterns)]++;
        }

        cum_probs[0] = probs[0];
        for (int i = 1; i < npatterns; i++) {
            cum_probs[i] = cum_probs[i - 1] + probs[i];
        }
    }

    /**
     * <p>
     * Return a random pattern
     * </p>
     * @return Random integer pattern
     */
    public int GetPattern() {
        double uniform;

        /* A invidual is selected using a random value. */
        uniform = Genesis.frandom(0, cum_probs[npatterns - 1]);
        int being = 0;
        while (uniform > cum_probs[being]) {
            being++;
        }

        return being;
    }

    /**
     * <p>
     * Returns an Arcing sample
     * </p>
     * @param ensemble Ensemble
     * @param data Input data
     * @param n data matrix order (number of of rows and colums)
     * @param nets Number of networks
     */
    public void GetArcingSample(Ensemble ensemble, double[][] data, int n,
                                int nets) {

        // Initial probs to 0
        for (int i = 0; i < n; i++) {
            probs[i] = 0.0;
        }

        // Classify each pattern
        for (int i = 0; i < nets; i++) {
            for (int j = 0; j < n; j++) {
                if (!(ensemble.nets[i].NetClassifyPattern(data[j]))) {
                    probs[j] += 1.0;
                }
            }
        }

        // Update weights
        probs[0] = 1.0 + Math.pow(probs[0], 4.0);
        cum_probs[0] = probs[0];
        for (int i = 1; i < n; i++) {
            probs[i] = 1.0 + Math.pow(probs[i], 4.0);
            cum_probs[i] = cum_probs[i - 1] + probs[i];
        }

    }

    /**
     * <p>
     * Returns an Ada sample
     * </p>
     * @param ensemble Ensemble
     * @param data Input data
     * @param n data matrix order (number of rows and columns)
     * @param net Number of networks
     */
    public void GetAdaSample(Ensemble ensemble, double[][] data, int n, int net) {

        // Classify each pattern using the last network
        double e_t = 0.0;
        boolean ok[] = new boolean[n];
        for (int j = 0; j < n; j++) {
            ok[j] = ensemble.nets[net].NetClassifyPattern(data[j]);
            if (!ok[j]) {
                e_t += probs[j];
            }
        }

        e_t /= n;

        // Update weights
        if (e_t > 0.5) { // New bootstrap sample
            this.GetEqualSample();
            ensemble.betta[net] = 1.0;
        } else if (e_t == 0.0) { // New bootstrap sample
            ensemble.betta[net] = 1.0e-10;
            this.GetEqualSample();
        } else {
            ensemble.betta[net] = e_t / (1 - e_t);
            if (ensemble.betta[net] < 1.0e-10) {
                ensemble.betta[net] = 1.0e-10;
            }
            if (ok[0]) {
                probs[0] /= 2.0 * (1.0 - e_t);
            } else {
                probs[0] /= 2.0 * e_t;
            }
            if (probs[0] < 1.0e-8) {
                probs[0] = 1.0e-8;
            }
            cum_probs[0] = probs[0];
            for (int i = 1; i < n; i++) {
                if (ok[0]) {
                    probs[i] /= 2.0 * (1.0 - e_t);
                } else {
                    probs[i] /= 2.0 * e_t;
                }
                if (probs[i] < 1.0e-8) {
                    probs[i] = 1.0e-8;
                }
                cum_probs[i] = cum_probs[i - 1] + probs[i];
            }
        }
    }


}

