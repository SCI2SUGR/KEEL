/**
 * File: NormalDistribution.java.
 *
 * Class representation of the Normal distribution
 *
 * @author Written by Joaquin Derrac (University of Granada) 1/12/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.statistical.tests;

public class NormalDistribution{
	
	private double mu;
	private double sigma;

    /**
     * Set the mean of the distribution
     *
     * @param value Mean value
     */
	public void setMean(double value){
		mu=value;
	}//end-method

	/**
     * Set the sigma value of the distribution
     *
     * @param value Sigma value
     */
	public void setSigma(double value){
		sigma=value;
	}//end-method

    /**
     * Get the mean of the distribution
     *
     * @return Mean value
     */
	public double getMean(){
		return mu;
	}//end-method

    /**
     * Get the sigma value of the distribution
     *
     * @return Sigma value
     */
	public double getSigma(){
		return sigma;
	}//end-method

    /**
     * Default builder
     */
	public NormalDistribution(){
		
	}//end-method

    /**
     * Computes punctual probability for a given point
     *
     * @param x Point selected
     * @return Punctual probability
     */
	public double getProbability(double x){
		
		double value=0.0;
		
		value=Math.pow(Math.E,-(x-mu)*(x-mu)/(2.0*sigma*sigma));
		
		value/=Math.sqrt(2.0*Math.PI*sigma*sigma);
		
		return value;
	}//end-method

    /**
     * Computes cumulated distribution frequency for a given value
     *
     * @param x Z value
     * @return Cumulated distribution frequency
     */
	public double getCumulatedProbability(double x){
		
		double value;
		
		double z= ((x-mu)/sigma);

		value=getTipifiedProbability(z, false);
		
		return value;
	}//end-method
	
	/**
     * 
     * Computes cumulative N(0,1) distribution. Based om Algorithm AS66 Applied Statistics (1973) vol22 no.3
     * 
     * @param z x value
     * @param upper A boolean value, if true the integral is evaluated from z to infinity, from minus infinity to z otherwise
     * @return The value of the cumulative N(0,1) distribution for z
     */  
    public double getTipifiedProbability(double z, boolean upper) {
        //Algorithm AS 66: "The Normal Integral"
        //Applied Statistics
        double ltone = 7.0,
        utzero = 18.66,
        con = 1.28,
        a1 = 0.398942280444,
        a2 = 0.399903438504,
        a3 = 5.75885480458,
        a4 = 29.8213557808,
        a5 = 2.62433121679,
        a6 = 48.6959930692,
        a7 = 5.92885724438,
        b1 = 0.398942280385,
        b2 = 3.8052e-8,
        b3 = 1.00000615302,
        b4 = 3.98064794e-4,
        b5 = 1.986153813664,
        b6 = 0.151679116635,
        b7 = 5.29330324926,
        b8 = 4.8385912808,
        b9 = 15.1508972451,
        b10 = 0.742380924027,
        b11 = 30.789933034,
        b12 = 3.99019417011;
		
        double y, alnorm;
        if (z < 0) {
            upper = !upper;
            z = -z;
        }
        if (z <= ltone || upper && z <= utzero) {
            y = 0.5 * z * z;
            if (z > con) {
                alnorm = b1 * Math.exp( -y) /
                         (z - b2 +
                          b3 /
                          (z + b4 +
                           b5 /
                           (z - b6 +
                            b7 / (z + b8 - b9 / (z + b10 + b11 / (z + b12))))));
            } else {
                alnorm = 0.5 -
                         z *
                         (a1 - a2 * y / (y + a3 - a4 / (y + a5 + a6 / (y + a7))));
            }
        } else {
            alnorm = 0;
        }
        if (!upper) {
            alnorm = 1 - alnorm;
        }
        return (alnorm);
    }//end-method
	
    /*
     * Computes inverse cumulative distribution.
     * From http://home.online.no/~pjacklam/notes/invnorm/
     * Error is bounded to 1.15E-09
     *
     * @param p CDF probability
     * 
     * @return Z value associated
     */
    public double inverseNormalDistribution(double p){
    	
    	   double a1 = -3.969683028665376e+01;
    	   double a2 = 2.209460984245205e+02;
    	   double a3 = -2.759285104469687e+02;
    	   double a4 = 1.383577518672690e+02;
    	   double a5 = -3.066479806614716e+01;
    	   double a6 = 2.506628277459239e+00;

    	   double b1 = -5.447609879822406e+01;
    	   double b2 = 1.615858368580409e+02;
    	   double b3 = -1.556989798598866e+02;
    	   double b4 = 6.680131188771972e+01;
    	   double b5 = -1.328068155288572e+01;

    	   double c1 = -7.784894002430293e-03;
    	   double c2 = -3.223964580411365e-01;
    	   double c3 = -2.400758277161838e+00;
    	   double c4 = -2.549732539343734e+00;
    	   double c5 = 4.374664141464968e+00;
    	   double c6 = 2.938163982698783e+00;

    	   double d1 = 7.784695709041462e-03;
    	   double d2 = 3.224671290700398e-01;
    	   double d3 = 2.445134137142996e+00;
    	   double d4 = 3.754408661907416e+00;

    	   double p_low = 0.02425;
    	   double p_high = 1.0 - p_low;
    	   
    	   double q;
    	   double x=0.0;
    	   double r;

    	   if(p<=0){
    		   return Double.NEGATIVE_INFINITY;
    	   }
    	   
    	   if(p>=1){
    		   return Double.POSITIVE_INFINITY;
    	   }
    	   
    	   //Rational approximation for lower region.

    	   if (p < p_low){
    		   q = Math.sqrt(-2.0*Math.log(p));
    		   x = (((((c1*q+c2)*q+c3)*q+c4)*q+c5)*q+c6) /
     	            ((((d1*q+d2)*q+d3)*q+d4)*q+1.0);
    		   
    		   return x;
    	   }
    	     
    	   //Rational approximation for central region.
    	   
    	   if (p <= p_high){
    		   q = p - 0.5;
    		   r = q*q;
    		   x = (((((a1*r+a2)*r+a3)*r+a4)*r+a5)*r+a6)*q /
     	           (((((b1*r+b2)*r+b3)*r+b4)*r+b5)*r+1.0);
    		   
    		   return x;
    	   }
    	  
    	   //Rational approximation for upper region.
    	   
    	   if (p_high < p){
    	      q = Math.sqrt(-2.0*Math.log(1.0-p));
    	      x = -(((((c1*q+c2)*q+c3)*q+c4)*q+c5)*q+c6) /
    	             ((((d1*q+d2)*q+d3)*q+d4)*q+1.0);
    	      
    	      return x;
    	   }

    	   return x;
    }//end-method

}//end-class

