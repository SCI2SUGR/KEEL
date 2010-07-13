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

package keel.Algorithms.Preprocess.Missing_Values.EM;

public abstract class Function {

	  /**
	   * the value of the function at the point x
	   * @param x the argument of the function
	   * @return the value of the function at x
	   */
	public abstract double valueAt(double x);

	  /**
	   * the array of values of this function at an array of values
	   * @param x the array of argument values
	   * @return the array of function values
	   */
	public double[] valuesAt(double[] x) {
	   int N=x.length;
	   double[] y = new double[N];
	   for(int i=0; i<N ;i++) y[i]=valueAt(x[i]);
	   return y;}

	/**
	   * the array of values of this function at a 2 dimensional array of values
	   * @param x the array of argument values
	   * @return the array of function values
	   */

	public double[][] valuesAt(double[][] x) {
	  int N=x.length;
	  double[][] y = new double[N][];
	  for(int i=0; i<N; i++) {
	    y[i]=new double[x[i].length];
	    for(int j=0; j<x[i].length;j++)
	      y[i][j]=valueAt(x[i][j]); }
	  return y;}

	/**
	 * The maximum of this function over an interval [a,A] evaluated at N+1 evenly
	 * distributed values
	 * @param a the left endpoint of the interval
	 * @param A the right enpoint of the interval
	 * @param N the number of subdivision intervals
	 * @return the greatest among the computed function values
	 */

	public double max(double a, double A, int N) {
	   double x=a, dx=(A-a)/N, y=valueAt(a), M=y;
	   for(int i=0; i<=N; i++) {x+=dx;y=valueAt(x);if(valueAt(x)>M) M=y;}
	   return M;   }

	/**
	 * The minimum of this function over an interval [a,A] evaluated at N+1 evenly
	 * distributed values
	 * @param a the left endpoint of the interval
	 * @param A the right enpoint of the interval
	 * @param N the number of subdivision intervals
	 * @return the least among the computed function values
	 */

	public double min(double a, double A, int N) {
	   double x=a, dx=(A-a)/N, y = valueAt(a), M=y;
	   for(int i=0; i<=N; i++) {x+=dx;y=valueAt(x);if(valueAt(x)<M) M=y;}
	   return M;  }

	 /**
	  * the composite of to functions
	  * @param f the first function
	  * @param g the second function
	  * @return f o g - first g then f
	  */

	public static Function compose(final Function f, final Function  g) {
	  return new Function()
	  {public double valueAt(double x) {
	    return f.valueAt(g.valueAt(x));};}; }

	  /**
	   * the sum of two functions
	   * @param f the first function
	   * @param g the second function
	   * @return the function f+g defined by (f+g)(x)=f(x)+g(x)
	   */

	public static Function add(final Function f, final Function g) {
	   return new Function()
	   {public double valueAt(double x) {
	      return f.valueAt(x)+g.valueAt(x);};}; }

	  /**
	   * the difference of two functions
	   * @param f the first function
	   * @param g the second function
	   * @return the function f-g defined by (f-g)(x)=f(x)-g(x)
	   */
	public static Function sub(final Function f, final Function g) {
	   return new Function()
	   {public double valueAt(double x) {
	      return f.valueAt(x)-g.valueAt(x);};};}

	  /**
	   * the product of two functions
	   * @param f the first function
	   * @param g the second function
	   * @return the function f*g defined by (f*g)(x)=f(x)*g(x)
	   */

	public static Function mul(final Function f, final Function g) {
	   return new Function()
	   {public double valueAt(double x) {
	      return f.valueAt(x)*g.valueAt(x);};};}

	  /**
	   * the quaotient of two functions
	   * @param f the first function
	   * @param g the second function
	   * @return the function f/g defined by (f/g)(x)=f(x)/g(x)
	   */

	public static Function div(final Function f, final Function g) {
	   return new Function()
	   {public double valueAt(double x) {
	      return f.valueAt(x)/g.valueAt(x);};};}

//	/**
//	 * sets a coordinate system that fits the graph of the function f over the interval
//	 * [xL,xH]
//	 * @param g the graphics context
//	 * @param comp the component
//	 * @param f the function whose graph is to be fitted within the coordinate system
//	 * @param xL the left endpoint of the argument interval
//	 * @param xH the right endpoint of the argument interval
//	 * @param N the number of subintervals in the argument interval
//	 */
//
//	public static void setFittedCoordSystem(Graphics2D g, Component comp, Function f,
//	  double xL, double xH, int N) {
//	  double yL=f.min(xL,xH,N), yH=f.max(xL,xH,N);
//	  CoordSystem.setFittedCoordSystem(g,comp,xL,xH,yL,yH); }
//
//	/**
//	 * sets a coordinate system that fits the graph of the function f over the interval
//	 * [xL,xH]
//	 * @param g the graphics context
//	 * @param comp the component
//	 * @param f the function whose graph is to be fitted within the coordinate system
//	 * @param xL the left endpoint of the argument interval
//	 * @param xH the right endpoint of the argument interval
//	 * @param N the number of subintervals in the argument interval
//	 * @param d the width of a white empty frame around ther graph
//	 */
//
//	public static void setFittedCoordSystem(Graphics2D g, Component comp, Function f,
//	  double xL, double xH, int N, double d) {
//	  double yL=f.min(xL,xH,N), yH=f.max(xL,xH,N);
//	  CoordSystem.setFittedCoordSystem(g,comp,xL-d,xH+d,yL-d,yH+d); }
//
//	/**
//	 * sets a coordinate system that contains the graphs of the functions f over the interval
//	 * [xL,xH]
//	 * @param g the graphics context
//	 * @param comp the component
//	 * @param f the array of functions whose graphs are to be fitted within the coordinate system
//	 * @param xL the left endpoint of the argument interval
//	 * @param xH the right endpoint of the argument interval
//	 * @param N the number of subintervals in the argument interval
//	*/
//
//	public static void setFittedCoordSystem(Graphics2D g, Component comp, Function[] f,
//	  double xL, double xH, int N) {
//	  int M=f.length;
//	  double[] yl=new double[M], yh=new double[M];
//	  for(int i=0; i<M; i++) {yl[i]=f[i].min(xL,xH,N);yh[i]=f[i].max(xL,xH,N);}
//	  double yL=ArrayUtil.min(yl), yH=ArrayUtil.max(yh);
//	  CoordSystem.setFittedCoordSystem(g,comp,xL,xH,yL,yH);}
//

	}


