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
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Preprocess.NoiseFilters.ANR;
import org.core.*;

public class OPV {
	/**
	 * <p>
	 * Vector operations class.
	 * </p>
	 */
	/**
	 * <p>
	 * returns the sum of vector a and b.
	 * </p>
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return the sum of a and b (a+b).
	 */
	public static double [] sum(double a[], double []b) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]+b[i];
        return result;
    }
	/**
	 * <p>
	 * returns the subtraction of vector a and b.
	 * </p> 
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return the subtraction of a and b (a-b).
	 */
    public static double [] subtract(double a[], double []b) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]-b[i];
        return result;
    }
    /**
	 * <p>
	 * returns a copy of the parameter vector with changed sign.
	 * </p> 
	 * @param a 1-dimensional vector.
	 * @return vector a with changed sign.
	 */
    public static double [] signChange(double a[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]*(-1.0f);
        return result;
    }
    /**
     * <p>
     * Returns a vector with the maximum of each component of a and b
     * </p>
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return a vector with the maximum of each component of a and b
     */
    public static double [] maximum(double a[],double b[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) 
            if (a[i]>b[i]) result[i]=a[i];
            else result[i]=b[i];
        return result;
    }
    /**
     * <p>
     * Returns a vector with the minimum of each component of a and b
     * </p>
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return a vector with the minimum of each component of a and b
     */
    public static double [] minimum(double a[],double b[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) 
            if (a[i]<b[i]) result[i]=a[i];
            else result[i]=b[i];
        return result;
    }
    /**
     * <p>
     * Returns a scaled copy of vector a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 1-dimensional vector.
	 * @param mx 1-dimensional vector with maximum value for each component of a.
	 * @param mn 1-dimensional vector with minimum value for each component of a.
	 * @return a scaled copy of vector a.
     */
    public static double [] scale(double a[], double mx[], double mn[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=(a[i]-mn[i])/(mx[i]-mn[i]);
        return result;
    }
    /**
     * <p>
     * Returns a inverted scaled copy of vector a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 1-dimensional vector.
	 * @param mx 1-dimensional vector with maximum value for each component of a.
	 * @param mn 1-dimensional vector with minimum value for each component of a.
	 * @return a inverted scaled copy of vector a.
     */
    public static double [] invScale(double a[], double mx[], double mn[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]*(mx[i]-mn[i])+mn[i];
        return result;
    }
	/**
	 * <p>
	 * returns the multiplication of scalar k by vector a.
	 * </p> 
	 * @param k a scalar value.
	 * @param a 1-dimensional vector.
	 * @return multiplication of k by a (k*a).
	 */
    public static double [] multiply( double k, double a[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=k*a[i];
        return result;
    }
	/**
	 * <p>
	 * returns the multiplication of vector a and b.
	 * </p> 
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return the multiplication of a and b (a*b).
	 */
    public static double multiply(double a[], double b[]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+a[i]*b[i];
        return result;
    }
	/**
	 * <p>
	 * returns square matrix with multiplication of each component of a by b and vice versa.
	 * </p> 
	 * @param a 1-dimensional vector.
	 * @param b 1-dimensional vector.
	 * @return a square matrix with the multiplication of a and b.
	 */
    public static double[][] multiCuad(double a[], double b[]) {
        double[][] result = new double[a.length][a.length];
        for (int i=0;i<a.length;i++) 
            for (int j=0;j<b.length;j++)  
               result[i][j]=a[i]*b[j];
        return result;
    }
    /**
	 * <p>
	 * returns a vector with multiplication of each row of matrix A by vector x.
	 * </p> 
	 * @param A a matrix.
	 * @param x 1-dimensional vector.
	 * @return a vector with multiplication of each row of matrix A by vector x.
	 */
    public static double[] multiply(double A[][], double x[]) {
        double [] result = new double[A.length];
        for (int i=0;i<A.length;i++) result[i]=multiply(A[i],x);
        return result;
    }
    // Matrix operations
    /**
	 * <p>
	 * returns the sum of matrix a and b.
	 * </p>
	 * @param a 2-dimensional vector.
	 * @param b 2-dimensional vector.
	 * @return the sum of a and b (a+b).
	 */
    public static double [][] sum(double a[][], double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=sum(a[i],b[i]);
        return result;
    }
    /**
	 * <p>
	 * returns the subtraction of matrix a and b.
	 * </p>
	 * @param a 2-dimensional vector.
	 * @param b 2-dimensional vector.
	 * @return the subtraction of a and b (a-b).
	 */
    public static double [][] subtract(double a[][], double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=subtract(a[i],b[i]);
        return result;
    }
    /**
	 * <p>
	 * returns a copy of the matrix a with changed sign.
	 * </p> 
	 * @param a 2-dimensional vector.
	 * @return a copy of matrix with changed sign.
	 */
    public static double [][] signChange(double a[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=signChange(a[i]);
        return result;
    }
    /**
     * <p>
     * Returns a matrix with the maximum of each component of a and b
     * </p>
	 * @param a 2-dimensional vector.
	 * @param b 2-dimensional vector.
	 * @return a matrix with the maximum of each component of a and b
     */
    public static double [][] maximum(double a[][],double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=maximum(a[i],b[i]);
        return result;
    }
    /**
     * <p>
     * Returns a matrix with the minimum of each component of a and b
     * </p>
	 * @param a 2-dimensional vector.
	 * @param b 2-dimensional vector.
	 * @return a matrix with the minimum of each component of a and b
     */
    public static double [][] minimum(double a[][],double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=minimum(a[i],b[i]);
        return result;
    }
    /**
     * <p>
     * Returns a scaled copy of matrix a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 2-dimensional vector.
	 * @param mx 2-dimensional vector with maximum value for each component of a.
	 * @param mn 2-dimensional vector with minimum value for each component of a.
	 * @return a scaled copy of matrix a.
     */
    public static double [][] scale(double a[][], double mx[][], double mn[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=scale(result[i],mx[i],mn[i]);
        return result;
    }
    /**
     * <p>
     * Returns a inverted scaled copy of matrix a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 2-dimensional vector.
	 * @param mx 2-dimensional vector with maximum value for each component of a.
	 * @param mn 2-dimensional vector with minimum value for each component of a.
	 * @return a inverted scaled copy of matrix a.
     */
    public static double [][] invScale(double a[][], double mx[][], double mn[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=invScale(result[i],mx[i],mn[i]);
        return result;
    }
    /**
	 * <p>
	 * returns the multiplication of scalar k by matrix a.
	 * </p> 
	 * @param k a scalar value.
	 * @param a 2-dimensional vector.
	 * @return multiplication of k by a (k*a).
	 */
    public static double [][] multiply( double k, double a[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=multiply(k,a[i]);
        return result;
    }
	/**
	 * <p>
	 * returns sum of the respective row vectors multiplication.
	 * </p> 
	 * @param a 2-dimensional vector.
	 * @param b 2-dimensional vector.
	 * @return the sum of the multiplication of a[i] and b[i].
	 */
    
    public static double multiply(double a[][], double b[][]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+multiply(a[i],b[i]);
        return result;
    }
    
    // Cubic matrix operations
    /**
	 * <p>
	 * returns the sum of matrix a and b.
	 * </p>
	 * @param a 3-dimensional vector.
	 * @param b 3-dimensional vector.
	 * @return the sum of a and b (a+b).
	 */
    public static double [][][] sum(double a[][][], double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=sum(a[i],b[i]);
        return result;
    }
    /**
	 * <p>
	 * returns the subtraction of matrix a and b.
	 * </p>
	 * @param a 3-dimensional vector.
	 * @param b 3-dimensional vector.
	 * @return the sum of a and b (a+b).
	 */
    public static double [][][] subtract(double a[][][], double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=subtract(a[i],b[i]);
        return result;
    }
    /**
	 * <p>
	 * returns a copy of the parameter vector with changed sign.
	 * </p> 
	 * @param a 3-dimensional vector.
	 * @return vector a with changed sign.
	 */
    public static double [][][] signChange(double a[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=signChange(a[i]);
        return result;
    }
    /**
     * <p>
     * Returns a matrix with the maximum of each component of a and b
     * </p>
	 * @param a 3-dimensional vector.
	 * @param b 3-dimensional vector.
	 * @return a matrix with the maximum of each component of a and b
     */
    public static double [][][] maximum(double a[][][],double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=maximum(a[i],b[i]);
        return result;
    }
    /**
     * <p>
     * Returns a matrix with the minimum of each component of a and b
     * </p>
	 * @param a 3-dimensional vector.
	 * @param b 3-dimensional vector.
	 * @return a matrix with the minimum of each component of a and b
     */
    public static double [][][] minimum(double a[][][],double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=minimum(a[i],b[i]);
        return result;
    }
    /**
     * <p>
     * Returns a scaled copy of matrix a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 3-dimensional vector.
	 * @param mx 3-dimensional vector with maximum value for each component of a.
	 * @param mn 3-dimensional vector with minimum value for each component of a.
	 * @return a scaled copy of matrix a.
     */
    public static double [][][] scale(double a[][][], double mx[][][], double mn[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=scale(result[i],mx[i],mn[i]);
        return result;
    }
    /**
     * <p>
     * Returns a inverted scaled copy of matrix a. The scaling factor is given by mx and mn.
     * </p>
	 * @param a 3-dimensional vector.
	 * @param mx 3-dimensional vector with maximum value for each component of a.
	 * @param mn 3-dimensional vector with minimum value for each component of a.
	 * @return a inverted scaled copy of matrix a.
     */
    public static double [][][] invScale(double a[][][], double mx[][][], double mn[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=invScale(result[i],mx[i],mn[i]);
        return result;
    }
    /**
	 * <p>
	 * returns the multiplication of scalar k by matrix a.
	 * </p> 
	 * @param k a scalar value.
	 * @param a 3-dimensional vector.
	 * @return multiplication of k by a (k*a).
	 */
    public static double [][][] multiply( double k, double a[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=multiply(k,a[i]);
        return result;
    }
    /**
	 * <p>
	 * returns the sum of the respective 2-dimensions matrix multiplication (a[i] by b[i]).
	 * </p> 
     * @param a 3-dimensional vector.
	 * @param b 3-dimensional vector.
	 * @return the sum of the multiplication of a[i] and b[i].	 */
    public static double multiply(double a[][][], double b[][][]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+multiply(a[i],b[i]);
        return result;
    }
    
    
}
