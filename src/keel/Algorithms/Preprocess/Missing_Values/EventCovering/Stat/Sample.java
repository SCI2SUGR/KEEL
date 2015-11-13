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

package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;

/**
 * Statistics class that works as a Sample and computes statistical values as mean, variace, squares sum,...
 * @author unknown
 */
public class Sample {

    /**
     * Add the given number to the summation and squares summation for mean and squares summation.
     * Increases the counter of elements in one.
     * @param X number given.
     */
    public void add(double X) {
    x += X; xx += X*X;
    n++;
    if (!mset) { 
      min=X; max=X; mset=true;
    } else {
      if (X>max) max=X;
      if (X<min) min=X;         
    }
}

    /**
     * Resets the statisticals variables.
     */
    public void reset() {
   n=0;
   x=0;
   xx=0;
   mset=false;
   min=0; max=0;
}

    /**
     * Returns the number of samples.
     * @return the number of samples.
     */
    public int getSampleSize()           { return n; }

    /**
     * Returns the summation of samples values.
     * @return  the summation of samples values.
     */
    public double getSum()               { return x; }

    /**
     * Returns the mean
     * @return the mean
     */
    public double getMean()              { return x/n; }

    /**
     * Returns the summation of the samples squares.
     * @return the summation of the samples squares.
     */
    public double getSumSquares()        { return xx; }

    /**
     * Returns the mean of the samples squares.
     * @return the mean of the samples squares.
     */
    public double getMeanSquares()       { return xx/n; }

    /**
     * Returns the variance.
     * @return the variance.
     */
    public double getVariance()          { return (xx - x*x/n)/(n-1); }

    /**
     * Returns the Standard deviation.
     * @return the Standard deviation.
     */
    public double getStandardDeviation() { return Math.sqrt(getVariance()); }

    /**
     * Returns the minimum value of all samples.
     * @return the minimum value of all samples.
     */
    public double getMin()               { return min; }

    /**
     * Returns the maximum value of all samples.
     * @return the maximum value of all samples.
     */
    public double getMax()               { return max; }

    /**
     * Returns the range (max-min).
     * @return the range (max-min).
     */
    public double getRange()             { return max-min; }

    /**
     * Returns the Variation coefficient.
     * @return the Variation coefficient.
     */
    public double getCoefficientOfVariation() { return getStandardDeviation()/getMean(); }

    /**
     * Returns a Confidence Interval for the mean value with the given confidence and sigma.
     * @param confidence confidence given.
     * @param sigma sigma given.
     * @return a Confidence Interval for the mean value with the given confidence and sigma.
     */
    public ConfidenceInterval getMean(double confidence, double sigma)  { 
  ConfidenceInterval c = new ConfidenceInterval();
  c.value = x/n;
  c.confidence = confidence;
  double w = sigma/Math.sqrt(n);
  double alpha = 1 - confidence;
  double t = StatFunc.gaussianPercentage(1 - alpha/2);
  c.min = c.value - w*t;
  c.max = c.value + w*t;
  return c;
}

    /**
     * Returns a Confidence Interval for the mean value with the given confidence.
     * @param confidence confidence given.
     * @return a Confidence Interval for the mean value with the given confidence.
     */
    public ConfidenceInterval getMean(double confidence)  { 
  ConfidenceInterval c = new ConfidenceInterval();
  c.value = x/n;
  c.confidence = confidence;
  double w = Math.sqrt(getVariance())/Math.sqrt(n);
  double t = StatFunc.studentPercentage(confidence,n-1);
  c.min = c.value - w*t;
  c.max = c.value + w*t;
  return c;
}

    /**
     * Returns a Confidence Interval for the variance value with the given confidence.
     * @param confidence confidence given.
     * @return a Confidence Interval for the variance value with the given confidence.
     */
    public ConfidenceInterval getVariance(double confidence)  { 
 ConfidenceInterval c = new ConfidenceInterval();
 c.value = getVariance();
 c.confidence = confidence;
 double w = getVariance()*(n-1);
 double alpha = 1.0 - confidence;
 double xl = StatFunc.chiSquarePercentage(1-alpha/2,n-1);
 double xh = StatFunc.chiSquarePercentage(alpha/2,n-1);
 c.min = w/xl;
 c.max = w/xh;
 return c;
}

    /**
     * Returns a Confidence Interval for the standard deviation value with the given confidence.
     * @param confidence confidence given.
     * @return a Confidence Interval for the standard deviation value with the given confidence.
     */
    public ConfidenceInterval getStandardDeviation(double confidence) { 
 ConfidenceInterval c = getVariance(confidence);
 c.min = Math.sqrt(c.min);
 c.max = Math.sqrt(c.max);
 c.value = Math.sqrt(c.value);
 return c;
}


private int n = 0;
private double x=0,xx=0;
private double min=0, max=0;
private boolean mset=false;

}

