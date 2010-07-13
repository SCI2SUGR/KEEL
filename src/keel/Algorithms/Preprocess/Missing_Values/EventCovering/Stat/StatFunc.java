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
 *
 * Statistical Functions
 * 
 **/

public class StatFunc {

  /**
   * Density function of the Standard Normal Distribution.
   **/
  public static double gaussianDensity(double x) { return Zn(x); }

  /**
   * Density function of the Normal Distribution with given mean
   * and standard deviation.
   **/
  public static double gaussianDensity(double x, double mean, double stdev) { 
         return Zn((mean + x)/stdev); 
  }

  /**
   * Standard Normal Distribution Function.
   **/
  public static double gaussian(double x) { return Pn(x); }

  /**
   * Normal Distribution Function with given mean and standard deviation.
   **/
  public static double gaussian(double x, double mean, double stdev) { 
         return Pn((x - mean)/stdev);
  }

  /**
   * Percentage point of the standard normal distribution.
   * (Inverse of the distribution function.)
   **/
  public static double gaussianPercentage(double p) { return Pninv(p); }


  /**
   * Percentage point of the normal distribution with given mean
   * and standard deviation.
   **/
  public static double gaussianPercentage(double p, double mean, double stdev) {
       return Pninv(p)*stdev + mean; 
  }

  /**
   * Error function.
   **/
   public static double erf(double x) { return 2.0 * Pn( x * Math.sqrt(2)) - 1; }

  /**
   * Density function of the Bivariate Standard Normal Distribution.
   **/
  public static double bivariateDensity(double x, double y, double ro) { 
         double b = Math.sqrt(1 - ro*ro);
         return (1.0 / b) * Zn(x) * Zn((y - ro * x)/b); 
  }
   

  /**
   * Chi Square Distribution Function.
   **/
  public static double chiSquare(double chisq, int n) { 
      return Pc(Math.sqrt(chisq),n); 
  }


  /**
   * Percentage point of the chi square distribution.
   * (Inverse of the distribution function.)
   **/
  public static double chiSquarePercentage(double p, int n) { 
     double y = Pcinv(p,n);
     return y * y; 
  }



  /**
   * Student t Distribution Function.
   **/
  public static double student(double t, int n) { 
      return As(t,n); 
  }


  /**
   * Percentage point of the student t distribution.
   * (Inverse of the distribution function.)
   **/
  public static double studentPercentage(double p, int n) { 
     return Asinv(p,n); 
  }


  // ------------------------------------------------
  // ------- Computations ---------------------------
  // ------------------------------------------------



  /**
   * Compute density of standard normal distribution.
   **/
  static double Zn(double x) { 
     return 1.0 / (sqrt2pi * Math.exp( x * x / 2));
  }



  /**
   * Compute Standard Normal Distribution.
   * Source: Abramovitz & Stegun, 26.2.11, pg. 932
   **/
  static double Pn(double x, double epsilon) {

      if (x==0) return 0.5;
      if (x<0) return 1 - Pn(-x,epsilon);
      if (x>12) return 1;

      double fac = x;
      double tot = fac;
      int n=1;

      while (Math.abs(fac) > epsilon) {
         fac = fac * x * x / ( 2.0 * n + 1);
         tot += fac; 
         n  = n+1;
      } 
       
      return 0.5 + Zn(x) * tot; 
  }

  static double Pn(double x) { return Pn(x,PRECISION); }



  // Double function object for use in Pinv.
  static DoubleFunc _Pn = new DoubleFunc() {
      public double F(double x) { return Pn(x); }
  };


  /**
   * Inverse Standard normal distribution.
   * Approximate using secant method.
   **/
  static double Pninv(double x) {
      if (x==0.5) return 0;
      if (x<=0) return Double.NEGATIVE_INFINITY;
      if (x>=1) return Double.POSITIVE_INFINITY;
      if (x<0.5) return -Pninv(1 - x);
      return Numeric.secant(_Pn,x,1,1.1);      
  }


  static double Qn(double x, double epsilon) { return 1-Pn(x,epsilon); }
  static double Qn(double x) { return 1-Pn(x); }

  static double An(double x, double epsilon) { return Pn(x,epsilon)*2-1; }
  static double An(double x) { return Pn(x)*2-1; }


  // STUDENT

  static double As(double t, int n) {
     if (t==0) return 0.5;
     if (n<=0) return 0;
     double theta = Math.atan(t / Math.sqrt(n));
     if (n==1) return 2.0 * theta / Math.PI;

     if (n % 2 == 1) {
       double cos = Math.cos(theta);
       double fac = cos;
       double tot = cos;
       for (int i=1; i<=(n-3)/2; i++) {
          fac = fac * cos * cos * 2.0 * i / (2.0 * i + 1);
          tot += fac;
         if (Math.abs(fac)<PRECISION) break;
       }       
       tot = theta + Math.sin(theta) * tot;
       return  2.0 * tot / Math.PI;  

     } else {

       double cos = Math.cos(theta);
       double fac = 1;
       double tot = 1;
       for (int i=1; i<=(n-2)/2; i++) {
          fac = fac * cos * cos * (2.0 * i - 1) / (2.0 * i);
          tot += fac;
         if (Math.abs(fac)<PRECISION) break;
       }       
       tot = Math.sin(theta) * tot;
       return  tot;  
     }
  }


  /**
   * Inverse Student distribution.
   * Approximate using binsearch method.
   **/
  static double Asinv(double x, int n) {
      if (x<=PRECISION) return Double.NEGATIVE_INFINITY;
      if (x>=1-PRECISION) return Double.POSITIVE_INFINITY;

      if (x==0.5) return 0;
      if (x<0.5) return -Asinv(1-x,n);

      StuFunc _Pc = new StuFunc();
      _Pc.n = n;
      double ans=Numeric.binsearch(_Pc,x,n);
      return ans;
  }



  //  CHI SQUARE


  static double Pc(double chi, int n) {
      return 1.0-Qc(chi,n);
  }


  static double Qc(double chi, int n) {
      double y=0;
      if (n % 2 == 0) y= QcEven(chi,n);
      else y = QcOdd(chi,n);
      if (y<=PRECISION) y=0;
      if (y>=1-PRECISION) y=1;
      return y;
  }

  static double QcEven(double x, int n) {

      if (x<=0 || n<=0) return 1;

      double fac = 1;
      double tot = fac;

      for (int i=1; i<=(n-2)/2; i++) {
         fac = fac * x * x / ( 2.0 * i);
         if (Math.abs(fac)<PRECISION) break;
         tot += fac; 
      } 
       
      return sqrt2pi * Zn(x) * tot; 
        
  }


  static double QcOdd(double x, int n) {

      if (x<=0 || n<=0) return 1;

      double fac = x;
      double tot = fac;

      for (int i=2; i<=(n-1)/2; i++) {
         fac = fac * x * x / ( 2.0 * i - 1);
         if (Math.abs(fac)<PRECISION) break;
         tot += fac; 
      } 
      return 2 * Qn(x) + 2 * Zn(x) * tot; 
  }



  /**
   * Inverse Chi Square distribution.
   * Approximate using binsearch method.
   **/
  static double Pcinv(double x, int n) {
      if (x<=PRECISION) return Double.NEGATIVE_INFINITY;
      if (x>=1-PRECISION) return Double.POSITIVE_INFINITY;

      ChiFunc _Pc = new ChiFunc();
      _Pc.n = n;
      double ans=Numeric.binsearch(_Pc,x,n);
      return ans;
  }


  
  // STATICS

  static double PRECISION = 1e-30;
  static double sqrt2pi = Math.sqrt(2*Math.PI);

   public static void main(String[] args) {

      for (int i=0; i<100; i++) 
        System.out.println(i+" "+Pcinv(1.0*i/100.0,50));  
  }
 
}


class ChiFunc implements DoubleFunc {
    public double F(double x) { return StatFunc.Pc(x,n); }
    int n;
}

class StuFunc implements DoubleFunc {
    public double F(double x) { return StatFunc.As(x,n); }
    int n;
}



