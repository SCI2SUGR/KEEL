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

/**
 * <p>Title: Polynomial Interpolation</p>
 * <p>Description: Polynomial interpoaltion using Newton's and Lagrange's methods</p>
 * <p>Copyright: Copyright Byrge Birkeland(c) 2002</p>
 * <p>Company: Agder University College</p>
 * @author Byrge Birkeland
 * @version 1.0
 */

public class Interpolation {

	/**
	 * implements Horners-s method to compute the polynomial
	 * a0+(t-x0)(a1+(t-x1)(a2+(t-x2)(a3+(...(an-1+(t-an))...))))))
	 * @param a the vector of coefficients
	 * @param x the vector of x coordinates of nodes to be interpolated
	 * @return the polynomial function
	 * a0+(t-x0)(a1+(t-x1)(a2+(t-x2)(a3+(...(an-1+(t-an))...))))))
	 */

	public static final Function horner(final double[] a,final double[] x) {
		return new Function() {
			public double valueAt(double t) {
				int n=a.length;
				double v=a[n-1];
				for (int i=1; i<n;i++) v=v*(t-x[n-1-i]) + a[n-1-i];
				return v;   }
		};
	}

	/**
	 * finds the divided differences, i.e. the coefrficients i Newton's interpolation
	 * polynomial intyerpolating the nodes (xi,yi)
	 * @param x the x coordinates of the nodes to be interpolated
	 * @param y the y coordinates of the nodes to be interpolated
	 * @return the coefficients (divided differences) of the interpolating polynomial
	 */

	public static double[] divDif(double[] x, double[] y) {
		int N=x.length;
		double[][] M = new double[N][N];
		for (int i=0; i<N; i++) M[i][0]=y[i];
		for (int j=1; j<N; j++)
			for (int i=0;i<N-j;i++)
				M[i][j]=(M[i+1][j-1]-M[i][j-1])/(x[i+j]-x[i]);
		return M[0]; }

	public static final Function newtonInterp(final double[] x, final double[] y) {
		final double[] a=divDif(x,y);
		return new Function() {
			public double valueAt(double t) {
				return horner(a,x).valueAt(t); }
		};
	}

	/**
	 * finds the piecewice linear function whose graph interpolates the points (ti,xi)
	 * @param t the argument values of the points to be interpolated
	 * @param x the ordinate values if the points to bed interpolated
	 * @return the linear function whose graph passes through the points (t0,x0) and
	 */

	public static final Function splineDegreeOne(final double[] t,final double[] x) {
		return new Function() {
			public double valueAt(double u) {
				double w=0;
				if(u<=t[0]) w = x[0]+(x[1]-x[0])*(u-t[0])/(t[1]-t[0]);
				if(u>t[0]) {
					int i=0;  for (int k=1;k<x.length;k++) { if (u-t[i]>0) i++;}
					i--;
					w = x[i]+(x[i+1]-x[i])*(u-t[i])/(t[i+1]-t[i]); }
				return w;  }
		}; }

	/**
	 * gives the derivatives at the nodes of the quadratic spline functions that
	 * interpolates the nodes (ti,xi)
	 * @param t the argument values of the points to be interpolated
	 * @param x the ordinate values of the points to be interpolated
	 * @param z0 the value of the first derivative at the node with index 0
	 * @return the derivatives at the nodes of the quadratic spline functions that
	 * interpolates the nodes (ti,xi)
	 */

	public static double[] quadSplineCoeff(double[] t, double [] x, double z0) {
		int n=t.length;
		double[] z=new double[n];
		z[0]=z0;
		for(int i=1; i<n; i++) z[i]=-z[i-1]+2*(x[i]-x[i-1])/(t[i]-t[i-1]);
		return z; }

	/**
	 * finds the piecewise second degree function that interpolates given nodes (xi,yi)
	 * @param t the argument values of the nodes
	 * @param x the ordinate values of the nodes
	 * @param z the vector of derivatives at the nodes
	 * @return the piecewise second degree function that interpolates given nodes (xi,yi)
	 */

	public static final Function quadSpline(final double[] t, final double[] x,final double[] z) {
		return new Function() {
			public double valueAt(double u) {
				double w=0.0;
				if (u<=t[0]) w=(z[1]-z[0])*(u-t[0])*(u-t[0])*0.5/(t[1]-t[0])
				+z[0]*(u-t[0])+x[0];
				if (u>t[0])
				{int i=0;
				for (int k=1;k<t.length;k++) {if (u-t[i]>0) i++;}
				i--;
				w=(z[i+1]-z[i])*(u-t[i])*(u-t[i])*0.5/(t[i+1]-t[i])+z[i]*(u-t[i])+x[i]; }
				return w;
			}
		};
	}

	/**
	 * finds the natural cubic spline function that interpolates the noeds (xi,yi)
	 * @param t the argument values of the points to be interpolated
	 * @param x the ordinate values of the points to be interpolated
	 * @param z0 the value of the second derivative at the node with index 0
	 * @param zn the value of the second derivative at the final node
	 * @return the vector of second derivatives at the nodes
	 */

	public static double[] cubicSplineCoeff(double[] t, double[] x, double z0, double zn) {
		int n=t.length;
		double[] h=new double[n-1], b=new double[n-1],
		u=new double[n], v=new double[n],z=new double[n];
		u[0]=0; v[0]=0;
		for(int i=0;i<n-1;i++)
		{h[i]=t[i+1]-t[i]; b[i]=(x[i+1]-x[i])/h[i];}
		u[1]=2*(h[0]+h[1]); v[1]=6*(b[1]-b[0]);
		for(int i=2;i<n-1;i++)
		{ u[i]=2*(h[i]+h[i-1])-h[i-1]*h[i-1]/u[i-1];
		v[i]=6*(b[i]-b[i-1])-h[i-1]*v[i-1]/u[i-1]; }
		z[n-1]=zn;
		for(int i=n-2;i>0;i--) z[i]=(v[i]-h[i]*z[i+1])/u[i];
		z[0]=z0;
		return z;}

	/**
	 * finds the cubic spline functions that interpolates the nodes (ti,xi)
	 * @param t the argument values of the nodes
	 * @param x the ordinate values of the nodes
	 * @param z the vector of second derivatives at the nodes
	 * @return the piecewise cubuc function that interpolates the nodes (xi,yi) and
	 * has the given second derivatives at the noeds
	 */

	public static final Function cubicSpline(final double[] t,final double[] x, final double[] z) {
		return new Function() {
			public double valueAt(double u) {
				int n=t.length,i=0;
				if(u<=t[0]) i=0;
				else
				{i=0; for(int k=1;k<n;k++) if(u-t[i]>0) i++; i--; }
				double h=t[i+1]-t[i];
				double tmp=0.5*z[i]+(u-t[i])*(z[i+1]-z[i])/6/h;
				tmp=-h*(z[i+1]+2*z[i])/6+(x[i+1]-x[i])/h+(u-t[i])*tmp;
				return x[i]+(u-t[i])*tmp; }
		}; }

	/**
	 * converts a double[] array to a String object
	 * @param x the array to be converted
	 * @return the corresponding String object
	 */

	public static String toString(double[] x) {
		String s="[";
		for (int i=0; i<x.length;i++) s+=x[i]+"  ";
		s+="]\n"; return s; }

	/**
	 * converts a double[][] array to a String object
	 * @param x the array to bed converted
	 * @return the corresponding String object
	 */

	public static String toString(double[][] x) {
		String s="[";
		for (int r=0; r<x.length; r++) {
			s+="\n[";
			for(int c=0;c<x[r].length;c++) s+=x[r][c]+"  ";
			s+="]"; }
		s+="\n]\n";
		return s;}
}

