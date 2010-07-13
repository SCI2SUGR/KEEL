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
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 23/01/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy;


public class FuzzyAlphaCut extends Fuzzy {
	/** 
	* <p>
	* <pre> 
	* Represents an alpha-cut for any type of fuzzy number (triangular, trapezoidal and singleton).
	*  
	* An alpha cut is defined as:
	* 
	*  A_alpha = {x| mu(x)  >= alpha}  
	* 
	*  Given the fuzzy set A defined over the Universe of the discourse U, and the membership function mu.  
	*  
	* This implementation uses two arrays for the extremes of the support set:
	* -left[]
	* -right[]
	* 
	* and an array for the alpha-cut:
	* -alpha[]
	* 
	* 
	* 
	*     membership f
    *     1.0 -                  ---------------------------
	*         |                 /                           \   
	*         |                /                             \
	* alpha[1]----------------/                               \
	*         |              /                                 \
	*         |             /                                   \ 
	*       a -            /                                     \ 
	*         |           /                                       \        
	*         |          /                                         \  
	* alpha[0]----------/                                           \
	*         |        /                                             \
 	*         |       /                                               \
	*     0.0 -------|----------|---------------------------|----------|----------
	*          left[0]      left[1]                       right[1]   right[0]  <-- Support
    *
	* 
	* 
	* Detailed in:
	* 
	* Zadeh, L. Fuzzy logic, IEEE Computer, 1:83, (1988)
	* <pre>
	* </p> 
	*/
	// left and right are the extremes of the fuzzy set support, and alpha is the membership threshold. 
    double[] left, right, alpha;
     
    /** 
    * <p> 
    * A copy constructor specialized for Triangular Fuzzy Numbers (FuzzyNumberTRIANG). 
    * 
    * </p> 
    * @param b a FuzzyNumberTRIANG object to be copied
    */
    public FuzzyAlphaCut(FuzzyNumberTRIANG b) {
        left=new double[2];
        right=new double[2];
        alpha=new double[2];
        left[0]=b.left; left[1]=b.center;
        right[0]=b.right; right[1]=b.center;
        alpha[0]=0; alpha[1]=1;
    }
    /** 
     * <p> 
     * A copy constructor specialized for Fuzzy Intervals (FuzzyInterval). 
     * 
     * </p> 
     * @param b a FuzzyInterval object to be copied
     */    
    public FuzzyAlphaCut(FuzzyInterval b) {
        left=new double[2];
        right=new double[2];
        alpha=new double[2];
        left[0]=b.a; left[1]=b.a;
        right[0]=b.b; right[1]=b.b;
        alpha[0]=0; alpha[1]=1;
    }
    /** 
     * <p> 
     * A copy constructor specialized for Fuzzy Singleton Sets(FuzzySingleton). 
     * 
     * </p> 
     * @param b is the FuzzySingleton instance to be copied
     */ 
    public FuzzyAlphaCut(FuzzySingleton b) {
        left=new double[2];
        right=new double[2];
        alpha=new double[2];
        left[0]=b.center; left[1]=b.center;
        right[0]=b.center; right[1]=b.center;
        alpha[0]=0; alpha[1]=1;
    }
    /** 
     * <p> 
     * A copy constructor specialized for Fuzzy Alpha Cuts(FuzzyAlphaCut). 
     * 
     * </p> 
     * @param b a FuzzyAlphaCut object to be copied
     */ 
    public FuzzyAlphaCut(FuzzyAlphaCut b) {
        left=new double[b.left.length];
        for (int i=0;i<left.length;i++) left[i]=b.left[i];
        right=new double[b.right.length];
        for (int i=0;i<right.length;i++) right[i]=b.right[i];
        alpha=new double[b.alpha.length];
        for (int i=0;i<alpha.length;i++) alpha[i]=b.alpha[i];
    }
    /** 
     * <p> 
     * Creates and returns a copy of this object.
     * 
     * </p>
     * @return a clone of this instance. 
     */ 
    public Fuzzy clone() {
        return new FuzzyAlphaCut(this);
    }
    /** 
     * <p> 
     * Copies the FuzzyAlphaCut parameter over the present instance. 
     * 
     * </p> 
     * @param b a FuzzyAlphaCut object to be copied
     */ 
    public void set(FuzzyAlphaCut b) {
        left=new double[b.left.length];
        for (int i=0;i<left.length;i++) left[i]=b.left[i];
        right=new double[b.right.length];
        for (int i=0;i<right.length;i++) right[i]=b.right[i];
        alpha=new double[b.alpha.length];
        for (int i=0;i<alpha.length;i++) alpha[i]=b.alpha[i];
    }
    /** 
     * <p> 
     *  Indicates whether some other object is "equal to" this one.
     * 
     * </p>
     * @param B the reference object with which to compare. 
     * @return true if this object is the same as the B argument; false otherwise. 
     */ 
    public boolean equals(Fuzzy B) {
 
        if (!(B instanceof FuzzyAlphaCut)) return false;
        FuzzyAlphaCut b=(FuzzyAlphaCut)B;
        for (int i=0;i<left.length;i++) if (left[i]!=b.left[i]) return false;
        for (int i=0;i<right.length;i++) if (right[i]!=b.right[i]) return false;
        for (int i=0;i<alpha.length;i++) if (alpha[i]!=b.alpha[i]) return false;
        return true;
    }
    /** 
     * <p> 
     *  Returns the membership level for the individual x.
     *  
     * 
     * </p>
     * @param x the individual which membership is to be calculated. 
     * @return the membership level for individual x. 
     */ 
    public double evaluateMembership(double x) {
        //if x is out of the support the membership level is 0.        
        if (x<left[0] || x>right[0]) return 0;
        //else x is in the support so its membership level can be calculated.
        for (int i=1;i<left.length;i++) {
            if (x<left[i] || x>right[i]) {
                if (x<left[i]) {
                    return alpha[i-1]+(alpha[i]-alpha[i-1])*(x-left[i-1])/(left[i]-left[i-1]);
                } else {
                    return alpha[i-1]+(alpha[i]-alpha[i-1])*(right[i-1]-x)/(right[i-1]-right[i]);
                }
            }
        }
        return 1;
    }
/** 
 * <p> 
 *  Returns the alpha-cut interval (le, ri) for alpha a.
 * 
 *<pre> 
 *   membership f
 *     1.0 -                  ---------------------------
 *         |                 /                           \   
 *         |                /                             \
 * alpha[1]----------------/                               \
 *         |              /                                 \
 *         |             /                                   \ 
 *       a -------------/-------------------------------------\ 
 *         |           /|                                     |\        
 *         |          / |                                     | \  
 * alpha[0]----------/  |                                     |  \
 *         |        /   |                                     |   \
 *         |       /    |                                     |    \
 *     0.0 -------|-----+----|--------------------------|-----+----|----------
 *          left[0]    le  left[1]                   right[1] ri right[0]  
 *</pre> 
 * </p>
 * @param a the alpha value for which the alpha-cut is to be calculated. 
 * @return the alpha-cut(le,ri) interval for alpha a. 
 */ 
    public FuzzyInterval alphaCut(double a) {
        //if alpha is 0 the alpha-cut interval is the support
    	if (a==0) return support();
        //if any of the extremes of the support are out of the limits the alpha-cut interval is the support
        if (left[0]<NEGATIVEINF || right[0]>POSITIVEINF) return support();
        //else  
        for (int i=1;i<left.length;i++) {
            if (a<alpha[i]) {
                double f=(a-alpha[i-1])/(alpha[i]-alpha[i-1]);
                double le=f*(left[i]-left[i-1])+left[i-1];
                double ri=f*(right[i]-right[i-1])+right[i-1];
                return new FuzzyInterval(le,ri);
            }
        }
        
        return new FuzzyInterval(left[left.length-1],right[right.length-1]);
        
    }
    /** 
     * <p> 
     *  Creates and returns a FuzzyInterval with the extremes of the support set.
     *  
     * </p> 
     * @return an interval with the extremes of the support set. 
     */ 
    public FuzzyInterval support() {
        return new FuzzyInterval(left[0],right[0]);
    }
    /** 
     * <p> 
     *  Returns the sum of the present FuzzyInterval and the parameter x.
     *  
     * </p>
     * @param x to be summed.
     * @return the sum of the present FuzzyAlphaCut and the parameter x. 
     */ 
    public FuzzyAlphaCut sum(FuzzyAlphaCut x) {
        
        if (left.length != x.left.length) System.out.println("FuzzyAlphaCut::suma: option not supported");
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            result.left[i]+=x.left[i];
            result.right[i]+=x.right[i];
        }
        return result;
    }
    /** 
     * <p> 
     *  Returns the subtract of the present FuzzyInterval and the parameter x.
     *  
     * </p>
     * @param x to be subtracted.
     * @return the subtract of the present FuzzyAlphaCut and the parameter x. 
     */ 
    public FuzzyAlphaCut subtract(FuzzyAlphaCut x) {
        
        if (left.length != x.left.length) System.out.println("FuzzyAlphaCut::resta: option not supported");
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            result.left[i]-=x.right[i];
            result.right[i]-=x.left[i];
        }
        return result;
    }
    /** 
     * <p> 
     *  Returns the multiplication of the present FuzzyInterval and the parameter x.
     *  
     * </p>
     * @param x to be multiplied.
     * @return the multiplication of the present FuzzyAlphaCut and the scalar x. 
     */ 
    public FuzzyAlphaCut multiply(double k) {
        
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            if (k>0) { result.left[i]=left[i]*k; result.right[i]=right[i]*k; }
            else { result.left[i]=right[i]*k; result.right[i]=left[i]*k; }
			
        }
        return result;
    }
    /** 
     * <p> 
     *  Returns the multiplication of the present FuzzyInterval and the parameter x.
     *  
     * </p>
     * @param x to be multiplied.
     * @return the multiplication of the present FuzzyAlphaCut and the parameter x. 
     */ 
    public FuzzyAlphaCut multiply(FuzzyAlphaCut x) {
        
        if (left.length != x.left.length) System.out.println("FuzzyAlphaCut::producto: option not supported");
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        
        
        for (int i=0;i<left.length;i++) {
            
            double a=result.left[i]*x.left[i];
            double max=a, min=a;
            double b=result.right[i]*x.right[i];
            if (max<b) max=b; if (min>b) min=b;
            double c=result.left[i]*x.right[i];
            if (max<c) max=c; if (min>c) min=c;
            double d=result.right[i]*x.left[i];
            if (max<d) max=d; if (min>d) min=d;
            if (max*min>0) {
                if (result.left[i]*result.right[i]<0 ||
                    x.left[i]*x.right[i]<0) {
                    if (max<0) max=0; else min=0;
                }
            
            } 
            result.left[i]=min;
            result.right[i]=max;
			
			            
        }
        return result;
    }

    /** 
     * <p> 
     *  Returns the square root of the present FuzzyInterval.
     *  
     * </p>
     * @return the square root of the present FuzzyAlphaCut. 
     */ 
    public FuzzyAlphaCut sqrt() {

        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            
            double a=Math.sqrt(Math.abs(left[i]));
            double b=Math.sqrt(Math.abs(right[i]));
            if (a<b) {
                result.left[i]=a; result.right[i]=b;
            } else {
                result.left[i]=b; result.right[i]=a;
            }
            if (left[i]*right[i]<0) { result.left[i]=0; }

        }
        return result;
    }
	
    /** 
     * <p> 
     *  Returns the square of the present FuzzyInterval.
     *  
     * </p>
     * @return the square of the present FuzzyAlphaCut. 
     */ 
	
	public FuzzyAlphaCut sqr() {
		
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            
            double a=left[i]*left[i];
            double b=right[i]*right[i];
            if (a<b) {
                result.left[i]=a; result.right[i]=b;
            } else {
                result.left[i]=b; result.right[i]=a;
            }
            if (left[i]*right[i]<0) { result.left[i]=0; }
			
			
        }
        return result;
    }
	
	   /** 
     * <p> 
     *  Returns the exponential of the present FuzzyInterval.
     *  
     * </p>
     * @return the exponential of the present FuzzyAlphaCut. 
     */
    public FuzzyAlphaCut exp() {
        
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
        for (int i=0;i<left.length;i++) {
            
            double le=left[i], ri=right[i];
            if (le>30) le=30; 
            if (ri>30) ri=30; 
            
            double a=Math.exp(le);
            double b=Math.exp(ri);
            
            result.left[i]=a; result.right[i]=b;
      
        }
        return result;
    }
    /** 
     * <p> 
     *  Returns the logarithm of the present FuzzyInterval.
     *  
     * </p>
     * @return the logarithm of the present FuzzyAlphaCut. 
     */  
    public FuzzyAlphaCut log() {
        
        FuzzyAlphaCut result=new FuzzyAlphaCut(this);
		final double MINIMUM=1.0E-6;
		final double MINUSINF=Math.log(MINIMUM);
        for (int i=0;i<left.length;i++) {
			double a=MINUSINF, b=MINUSINF;
			if (left[i]>MINIMUM) a=Math.log(left[i]);
			if (right[i]>MINIMUM) b=Math.log(right[i]);
			result.left[i]=a; result.right[i]=b;
        }
        return result;
    }
    
    /** 
     * <p> 
     *  Returns the centroid of the present alpha-cut. 	
     *  
     * </p>
     * @return the centroid of the present FuzzyAlphaCut. 
     */   
    public double massCentre() {
        double mass=0; double sumxw=0, a, b, c, d, h, w, xc;
        c=left[0]; d=right[0];
        for (int i=1;i<left.length;i++) {
            a=left[i]; b=right[i]; h=alpha[i]-alpha[i-1];
            w=h/2*(-a+b-c+d);
            xc=((a-c)*(2*a+c)+3*(a+b)*(b-a)+(d-b)*(2*b+d))/(3*(-a+b-c+d));
            c=a; d=b;
            mass+=w;
            sumxw+=xc*w;
        }
        if (mass==0) {
            // It's a singleton
            return left[0];
        } else {
            return sumxw/mass;
        }
    }
    /** 
     * <p> 
     *  Returns the average amplitude of a fuzzy set. It halves the nonspecificity.   	
     *  
     * </p>
     * @return the average amplitude of a fuzzy set. 
     */   
	public void averageAmplitude() {
	   // Halves the nonspecificity
	   for (int i=0;i<left.length;i++) {
		   double c=(left[i]+right[i])/2;
		   double m=right[i]-c;
	       left[i]=c-m/2;
		   right[i]=c+m/2;
	   }
		
	}
	
	 /** 
     * <p> 
     *  Returns the linear combination of two alpha-cuts a and b with alpha "alphap".   	
     *  
     *  linear_combination = alphap * a + (1-alphap) * b
     *  
     * </p>
     * @param a 
     * @param b
     * @param alphap
     * @return the linear combination of alpha-cuts a and b with alphap. 
     */
	public void linearComb(FuzzyAlphaCut a,FuzzyAlphaCut b,double alphap) {
		
		// Linear combination of 'a' and 'b'
		
        left=new double[b.left.length];
        for (int i=0;i<left.length;i++) left[i]=a.left[i]*alphap+b.left[i]*(1-alphap);
        right=new double[b.right.length];
        for (int i=0;i<right.length;i++) right[i]=a.right[i]*alphap+b.right[i]*(1-alphap);
        alpha=new double[b.alpha.length];
        for (int i=0;i<alpha.length;i++) alpha[i]=b.alpha[i];
		
    }
	 /** 
     * <p> 
     *  Returns a printable version of the instance.   	
     *
     * </p>
     
     * @return a String with a printable version of alpha-cut. 
     */	
    public String aString() {
        String result="ALFA_CORTES{";
        for (int i=0;i<left.length;i++) result+=("["+left[i]+", "+right[i]+"] ");
        return result+"}";
    }

}

