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
 * @author Modified by M.R. Suárez (University of Oviedo) 18/12/2008
 * @author Modified by Enrique A. de la Cal (University of Oviedo) 21/12/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.OptimLocal;

public class OPV {
/**
 * <p>
 * Class for vector, matrix and cubic matrix operations
 * </p>
 */
 
	/**
	 * <p>
	 * This static method implements the sum of vectors
	 * </p>
	 * @param a The first vector of double values
	 * @param b The second vector of double values
	 * @return A vector that is the sum of the other two
	 */
    public static double [] sum(double a[], double []b) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]+b[i];
        return result;
    }
    
    /**
     * <p>
     * This static method implements the subtract of vectors
     * </p>
     * @param a The first vector of double values
	 * @param b The second vector of double values
	 * @return A vector that is the subtract of the other two
     */
    public static double [] minus(double a[], double []b) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]-b[i];
        return result;
    }
    
    /**
     * <p>
     * This static method implements the operation of changing the sing applied to a vector
     * </p>
     * @param a The vector of double values
     * @return A vector whose values have been changed to sing
     */ 
    public static double [] changeSign(double a[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]*(-1.0f);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the maximum operation between two vectors elements
     * </p> 
     * @param a The first vector of double values
	 * @param b The second vector of double values 
     * @return The maximum of two vectors 
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
     * This static method implements the minimum operation between two vectors elements
     * </p> 
     * @param a The first vector of double values
	 * @param b The second vector of double values 
     * @return The minimum of two vectors 
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
     * This static method scale a vector
     * </p>
     * @param a The vector, of double values, to be scaled
     * @param mx The vector of maximum values
     * @param mn The vector of minimum values
     * @return The vector whose values are scaled
     */
    public static double [] scale(double a[], double mx[], double mn[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=(a[i]-mn[i])/(mx[i]-mn[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method is for inverse scale of a vector
     * </p>
     * @param a The vector, of double values, to be inverse scaled
     * @param mx The vector of maximum values
     * @param mn The vector of minimum values 
     * @return The vector whose values are in inverse scale
     */
    public static double [] invScale(double a[], double mx[], double mn[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=a[i]*(mx[i]-mn[i])+mn[i];
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies each element of a vector by a double value
     * </p>
     * @param k The double value
     * @param a The vector of double values
     * @return A vector whose values are multiplied by a double value
     */
    public static double [] multiply( double k, double a[]) {
        double result[]=new double[a.length]; 
        for (int i=0;i<a.length;i++) result[i]=k*a[i];
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies two matrix and add the result of the multiplications
     * </p>
     * @param a The first vector of double values
     * @param b The second vector of double values
     * @return A double value result of the multiplication
     */
    public static double multiply(double a[], double b[]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+a[i]*b[i];
        return result;
    }
    
    /**
     * <p>
     * This static method genetares a matrix multiplication from two vectors
     * </p>
     * @param a The first vector of double values
     * @param b The second vector of double values
     * @return A matrix whose elements are the multiplication of the two vectors
     */
    public static double[][] MultCuad(double a[], double b[]) {
        double[][] result = new double[a.length][a.length];
        for (int i=0;i<a.length;i++) 
            for (int j=0;j<b.length;j++)  
               result[i][j]=a[i]*b[j];
        return result;
    }
    
    /**
     * <p>
     * This static method multiply each row of a matrix by a double value
     * </p>
     * @param A The matrix with double values
     * @param x The double values
     * @return A double vector that each element is the sum of each element row of a matrix mutiply by a double value 
     */
    public static double[] multiply(double A[][], double x[]) {
        double [] result = new double[A.length];
        for (int i=0;i<A.length;i++) result[i]=multiply(A[i],x);
        return result;
    }
       
    /**
     * <p>
     * This static method implements the sum of matrix
     * </p>
     * @param a The first matrix of double values
     * @param b The second matrix of double values
     * @return A matrix that is the sum of the other two 
     */
    public static double [][] sum(double a[][], double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=sum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the subtract of matrix
     * </p>
     * @param a The first matrix of double values
     * @param b The second matrix of double values
     * @return A matrix that is the subtract of the other two
     */
    public static double [][] minus(double a[][], double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=minus(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the operation of changing the sing applied to a matrix
     * </p>
     * @param a The matrix of double values
     * @return A matrix whose values have been changed to sing
     */
    public static double [][] changeSign(double a[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=changeSign(a[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the maximum operation between two matrix elements
     * </p>
     * @param a The first matrix of double values
     * @param b The second matrix of double values
     * @return A matrix whose values are the maximum 
     */
    public static double [][] maximum(double a[][],double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=maximum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the minimum operation between two matrix elements
     * </p>
     * @param a The first matrix of double values
     * @param b The second matrix of double values
     * @return A matrix whose values are the minimun
     */
    public static double [][] minimum(double a[][],double b[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=minimum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method scales a matrix
     * </p>
     * @param a The matrix, of double values, to be scaled
     * @param mx The matrix of maximum double values
     * @param mn The matrix of minimum double values
     * @return The matrix whose values are scaled
     */
    public static double [][] scale(double a[][], double mx[][], double mn[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=scale(result[i],mx[i],mn[i]);
        return result;
    }
    

    /**
     * <p>
     * This static method is for inverse scale a mtrix
     * </p>
     * @param a The matrix, of double values, to be inverse scaled
     * @param mx The matrix of maximum double values
     * @param mn The matrix of minimum double values
     * @return The matrix whose values are inverse scaled
     */
    public static double [][] invScale(double a[][], double mx[][], double mn[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=invScale(result[i],mx[i],mn[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies
     * each double element of a matrix by a double value 
     * </p>
     * @param k The double value
     * @param a The matrix with double values
     * @return A matrix whose elements are multiplied by a value
     */
    public static double [][] multiply( double k, double a[][]) {
        double result[][]=new double[a.length][]; 
        for (int i=0;i<a.length;i++) result[i]=multiply(k,a[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies two matrix and add the result of the multiplications 
     * </p>
     * @param a The first matrix with double values
     * @param b The second matrix with double values
     * @return A double value result of the multiplication
     */
    public static double multiply(double a[][], double b[][]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+multiply(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the sum of cubic matrix
     * </p>
     * @param a The first cubic matrix with double values
     * @param b The second cubic matrix with double values
     * @return A cubic matrix that is the sum of the other two
     */
    public static double [][][] sum(double a[][][], double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=sum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the subtract of cubic matrix
     * </p>
     * @param a The first cubic matrix with double values
     * @param b The second cubic matrix with double values
     * @return A cubic matrix that is the subtract of the other two
     */
    public static double [][][] minus(double a[][][], double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=minus(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the operation of changing the sing applied to a cubic matrix
     * </p>
     * @param a The cubic matrix of double values
     * @return A cubic matrix whose values have been changed to sing
     */
    public static double [][][] changeSign(double a[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=changeSign(a[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the maximum operation between two cubic matrix elements
     * </p>
     * @param a The first cubic matrix of double values
     * @param b The second cubic matrix of double values
     * @return A cubic matrix whose values are the maximum 
     */
    public static double [][][] maximum(double a[][][],double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=maximum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method implements the minimum operation between two cubic matrix elements
     * </p>
     * @param a The first cubic matrix of double values
     * @param b The second cubic matrix of double values
     * @return A cubic matrix whose values are the minimum 
     */
    public static double [][][] minimum(double a[][][],double b[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=minimum(a[i],b[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method scales a cubic matrix
     * </p>
     * @param a The cubic matrix, of double values, to be scaled
     * @param mx The cubic matrix of maximum double values
     * @param mn The cubic matrix of minimum double values
     * @return The cubic matrix whose values are scaled
     */
    public static double [][][] scale(double a[][][], double mx[][][], double mn[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=scale(result[i],mx[i],mn[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method is for inverse scale a cubic mtrix
     * </p>
     * @param a The cubic matrix, of double values, to be inverse scaled
     * @param mx The cubic matrix of maximum double values
     * @param mn The cubic matrix of minimum double values
     * @return The cubic matrix whose values are inverse scaled
     */
    public static double [][][] invScale(double a[][][], double mx[][][], double mn[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=invScale(result[i],mx[i],mn[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies each double element of a cubic matrix by a double value 
     * </p>
     * @param k The double value
     * @param a The cubic matrix with double values
     * @return A cubic matrix whose elements are multiplied by a value
     */
    public static double [][][] multiply( double k, double a[][][]) {
        double result[][][]=new double[a.length][][]; 
        for (int i=0;i<a.length;i++) result[i]=multiply(k,a[i]);
        return result;
    }
    
    /**
     * <p>
     * This static method multiplies two cubic matrix and add the result of the multiplications 
     * </p>
     * @param a The first cubic matrix with double values
     * @param b The second cubic matrix with double values
     * @return A double value result of the multiplication
     */
    public static double multiply(double a[][][], double b[][][]) {
        double result=0;
        for (int i=0;i<a.length;i++) result=result+multiply(a[i],b[i]);
        return result;
    }
    
    
}

