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
 * Statistics class used for linear regression stats.
 * @author sergio
 */
public class LinearRegression {

    /**
     * Add the given numbers to the summation and squares summation for mean and squares summation.
     * Increases the counter of elements in one.
     * @param X function x value given.
     * @param Y function y value given.
     */
    public void add(double X, double Y) {
    x += X; xx += X*X;
    y += Y; yy += Y*Y;
    xy += X*Y;
    n++;
}

    /**
     *  Resets the statisticals variables.
     */
    public void reset() {
   n=0;
   x=0;
   xx=0;
   y=0;
   yy=0;
   xy=0;
}

    /**
     * Returns the number of samples.
     * @return the number of samples.
     */
    public int getN()     { return n; }

    /**
     * Returns the summation of X samples values.
     * @return  the summation of X samples values.
     */
    public double getX()     { return x; }

    /**
     * Returns the summation of Y samples values.
     * @return  the summation of Y samples values.
     */
    public double getY()     { return y; }

    /**
     * Returns the X values mean
     * @return the X values mean
     */
    public double getXavg()  { return x/n; }

    /**
     * Returns the Y values mean
     * @return the Y values mean
     */
    public double getYavg()  { return y/n; }

    /**
     * Returns the summation of the X samples squares.
     * @return the summation of the X samples squares.
     */
    public double getXX()    { return xx; }

    /**
     * Returns the summation of the Y samples squares.
     * @return the summation of the Y samples squares.
     */
    public double getYY()    { return yy; }

    /**
     * Returns the summation of the X-Y products.
     * @return the summation of the X-Y products.
     */
    public double getXY()    { return xy; }

    /**
     * Returns the mean of the X samples squares.
     * @return the mean of the X samples squares.
     */
    public double getXXavg() { return xx/n; }

    /**
     * Returns the mean of the Y samples squares.
     * @return the mean of the Y samples squares.
     */
    public double getYYavg() { return yy/n; }

    /**
     * Returns the mean of the X-Y products.
     * @return the mean of the X-Y products.
     */
    public double getXYavg() { return xy/n; }

    /**
     * Returns the statistical Sxx.
     * @return the statistical Sxx.
     */
    public double getSxx()   { return xx - x*x/n; }

    /**
     * Returns the statistical Syy.
     * @return the statistical Syy.
     */
    public double getSyy()   { return yy - y*y/n; }

    /**
     * Returns the statistical Sxy.
     * @return the statistical Sxy
.     */
    public double getSxy()   { return xy - x*y/n; }


// beta1

    /**
     * Returns the regression Slope (Beta1)
     * @return the regression Slope (Beta1)
     */
    public double getBeta1() { return getSxy() / getSxx(); }

    /**
     * Returns the regression Slope (Beta1)
     * @return the regression Slope (Beta1)
     */
    public double getSlope() { return getBeta1(); }

// beta0

    /**
     * Returns the regression intercept (Beta0)
     * @return the regression intercept (Beta0)
     */
    public double getBeta0() { return (y/n) - getSlope() * (x/n); }

    /**
     * Returns the regression intercept (Beta0)
     * @return the regression intercept (Beta0)
     */
    public double getIntercept() { return getBeta0(); }

// SSe

    /**
     * Returns the sum of squared errors (SSe)
     * @return the sum of squared errors (SSe)
     */
    public double getSSe() { return getSyy() - getBeta1()*getSxy(); }

    /**
     * Returns the sum of squared errors (SSe)
     * @return the sum of squared errors (SSe)
     */
    public double getErrorSumOfSquares() { return getSSe(); }

    /**
     * Returns the sum of squares due to regression (SSr)
     * @return the sum of squares due to regression (SSr)
     */
    public double getSSr() { return getBeta1() * getSyy(); }

// sigma^2

    /**
     * Returns the variance error (sigma^2)
     * @return the variance error (sigma^2)
     */
    public double getSigmaSq() { return getSSe() / (n-2); }

    /**
     * Returns the variance error (sigma^2)
     * @return the variance error (sigma^2)
     */
    public double getErrorVariance() { return getSigmaSq(); }

// se(beta1)

    /**
     * Returns the Standard errors slope - Se(beta1)
     * @return the Standard errors slope - Se(beta1)
     */
    public double getSeBeta1() { return Math.sqrt( getSigmaSq() / getSxx()); }

    /**
     * Returns the Standard errors slope - Se(beta1)
     * @return the Standard errors slope - Se(beta1)
     */
    public double getStdErrorSlope() { return getSeBeta1(); }

// se(beta0)
 
    /**
     * Returns the Standard errors intercept - Se(beta0)
     * @return the Standard errors slope - Se(beta0)
     */
    public double getSeBeta0() { 
  return Math.sqrt( getSigmaSq() * ( 1/n +  (x/n) * (x/n) / getSxx()) ); }

    /**
     * Returns the Standard errors intercept - Se(beta0)
     * @return the Standard errors slope - Se(beta0)
     */
    public double getStdErrorIntercept() { return getSeBeta0(); }

    /**
     * Returns the statistical F0.
     * @return the statistical F0.
     */
    public double getF0() { return getSSr() / getSigmaSq(); }


// correlation

    /**
     * Returns the correlation value R.
     * @return  the correlation value R.
     */
    public double getR() { return getSxy() / Math.sqrt(getSxx() * getSyy()); }

    /**
     * Returns the correlation value R.
     * @return  the correlation value R.
     */
    public double getCorrelation() { return getR(); }

//
 
    /**
     * Returns the statistical T0.
     * @return the statistical T0.
     */
    public double getT0() { 
  double r = getR();
  return r * Math.sqrt(n-2) / Math.sqrt(1 - r*r); 
}

private int n = 0;
private double x=0,xx=0,y=0,yy=0,xy=0;

}

