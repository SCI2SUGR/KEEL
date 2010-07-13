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
* @author Written by Luciano Sanchez (University of Oviedo) 08/03/2003
* @version 1.0
* @since JDK1.4
* </p>
*/


package keel.Algorithms.Fuzzy_Rule_Learning.Random_Sets.FSS98;
import keel.Algorithms.Shared.ClassicalOptim.*;
import org.core.*;

class Cluster {
	/**
	* <p>
	* The auxiliary class Cluster supports a cluster region of an individual.
	* It is used to store a dendogram.
	* It is intended to be used in the Fuzzy Random Sets Regression Algorithm.
	* </p>
	*/
	//The covariance matrix of the cluster
	double [][]C;  // Cluster covariance
	//The clusters centroids
	double [][]m;  // Mean cluster
	//The number of centroids
	int n;         // Number of cluster elements
	//The value of energy of the cluster
	double e;      // Cluster energy
	//The left-hand sub-tree
	Cluster leftHandStree;  // Left and rigth sub-trees
	//The right-hand sub-tree
	Cluster rightHandStree;
	//The ordinal position of the leaf node
	int ord;       // Ordinal for point if it't a terminal node
}

class MatrixCalcs {
	/**
	* <p>
	* The auxiliary class MatrixCalcs supports matrixes operations.
	* It is intended to be used in the Fuzzy Random Sets Regression Algorithm.
	* </p>
	*/

	//Exception in case a Singular Matrix is carry out
	public static class SingularError extends Exception {
		SingularError(String reason) {
			super(reason);
		}
		public String toString() {
			return "Singular Matrix: " +getMessage();
		}
	}
	//Exception in case two matrixes dimensions do not match
	public static class DimensionError extends Exception {
		DimensionError(String reason) {
			super(reason);
		}
		public String toString() {
			return "Matrixes dimensions do not match: " +getMessage();
		}
	}

	//The minimum matrix element value
	protected static final double MINVALPRO=(double)1.0e-10;


	/**
	* <p>
	* This protected static method returns the LU decomposition of an square matrix.
	* </p>
	* @param a  the matrix for which the LU decomposition will be carried out, it is
	*           a bi-dimensional array of doubles
	* @param indx the matrix index array, an array of integers for book-keeping the
	*           row interchanges
	* @return a double value with the determinant of the matrix
	* @throws ErrorSingula in case of a singular matrix
	*/
	protected static double ludcmp(double[][] a,int[] indx)
		throws SingularError {

			// Return new values for a, indx, d
			double d;
			int n=a[0].length;
			int i,imax,j,k;
			double big,dum,sum,temp;
			double[] vv=new double[n+1];
			d=(double)1.0;
			for (i=1;i<=n;i++) {
				big=(double)0.0;
				for (j=1;j<=n;j++)
					if ((temp=Math.abs(a[j-1][i-1]))>big) big=temp;
				if (big==0.0) {
					throw new SingularError("Singular Matrix in ludcmp");
				}

				vv[i-1]=(double)1.0/big;
			}

			for (j=1;j<=n;j++) {
				for (i=1;i<j;i++) {
					sum=a[j-1][i-1];
					for (k=1;k<i;k++) sum -= a[k-1][i-1]*a[j-1][k-1];
					a[j-1][i-1]=sum;
				}

				big=(double)0.0; imax=-1;
				for (i=j;i<=n;i++) {
					sum=a[j-1][i-1];
					for (k=1;k<j;k++)
						sum-=a[k-1][i-1]*a[j-1][k-1];
					a[j-1][i-1]=sum;
					if ((dum=vv[i-1]*Math.abs(sum))>=big) {
						big=dum;
						imax=i;
					}
				}

				if (j!=imax) {
					for (k=1;k<=n;k++) {
						dum=a[k-1][imax-1];
						a[k-1][imax-1]=a[k-1][j-1];
						a[k-1][j-1]=dum;
					}
					d=-d;
					vv[imax-1]=vv[j-1];
				}
				indx[j-1]=imax;
				if (Math.abs(a[j-1][j-1])<MINVALPRO)
					throw new SingularError("Singula Matrix in ludcmp");

				if (j!=n) {
					dum=(double)1.0/(a[j-1][j-1]);
					for (i=j+1;i<=n;i++) a[j-1][i-1]*=dum;
				}
			}
			return d;
		}

	/**
	* <p>
	* This protected static method carries out the LU decomposition with back substitution
	* of an square matrix.
	* </p>
	* @param a  the matrix for which the LU decomposition will be carried out, it is
	*           a bi-dimensional array of doubles
	* @param indx the matrix index array, an array of integers for book-keeping the
	*           row interchanges
	* @param b  the input/output vector, a double array
	*/
	protected static void lubksb(double[][] A,int[] indx,double[] b) {

		int i,ii=0,ip,j,n=A.length;
		double sum;

		for (i=1;i<=n;i++) {
			ip=indx[i-1];
			sum=b[ip-1];
			b[ip-1]=b[i-1];
			if (ii!=0) for (j=ii;j<=i-1;j++) sum-=A[j-1][i-1]*b[j-1];
			else if (sum!=0) ii=i;
			b[i-1]=sum;
		}

		for (i=n;i>=1;i--) {
			sum=b[i-1];
			for (j=i+1;j<=n;j++) sum-=A[j-1][i-1]*b[j-1];
			b[i-1]=sum/A[i-1][i-1];
		}

	}

	/**
	* <p>
	* This private static method copies one matrix into another.
	* </p>
	* @param a   the bi-dimensional double array, the destination matrix
	* @param vA  the bi-dimensional double array to be copied
	*/
	private static void copy(double[][] A, double[][] vA) {
		for (int i=0;i<vA.length;i++)
			for (int j=0;j<vA[i].length;j++) A[i][j]=vA[i][j];
	}

	/**
	* <p>
	* This private static method rotates i-j-element with the k-l-element
	* for a given matrix.
	* </p>
	* @param a   the bi-dimensional double array, the matrix to be rotated
	* @param i   the integer row index of the first element
	* @param j   the integer column index of the first element
	* @param k   the integer row index of the second element
	* @param l   the integer column index of the second element
	* @param vA  the bi-dimensional double array to be copied
	* @param s   a double, the scale for the rotating operation.
	* @param tau a double, the rate for the rotating operation.
	*/
	private static void rotate(double a[][],
							   int i, int j, int k, int l,
							   double s, double tau) {
		double g=a[i-1][j-1];
		double h=a[k-1][l-1];
		a[i-1][j-1]=g-s*(h+g*tau);
		a[k-1][l-1]=h+s*(g-h*tau);
	}

	/**
	* <p>
	* This protected static method calculates the jacobian of a given matrix.
	* </p>
	* @param a   the matrix whose jacobi is to be determined
	* @param d   the integer row index of the first element
	* @param v   the eigenvalues
	* @param k   the integer row index of the second element
	*/
	protected static void jacobi(double[][] a, double[] d, double[][] v) {
		int j,iq,ip,i;
		double tresh,theta,tau,t,sm,s,h,g,c,b[],z[];
		int n=a[0].length;
		int nrot;

		b=new double[n];
		z=new double[n];
		for (ip=1;ip<=n;ip++) {
			for (iq=1;iq<=n;iq++) v[ip-1][iq-1]=(double)0.0;
			v[ip-1][ip-1]=(double)1.0;
		}
		for (ip=1;ip<=n;ip++) {
			b[ip-1]=d[ip-1]=a[ip-1][ip-1];
			z[ip-1]=(double)0.0;
		}
		nrot=0;
		for (i=1;i<=50;i++) {
			sm=(double)0.0;
			for (ip=1;ip<=n-1;ip++) {
				for (iq=ip+1;iq<=n;iq++)
					sm+=Math.abs(a[ip-1][iq-1]);
			}
			if (sm==(double)0.0) { return; }

			if (i<4) tresh=(double)0.2*sm/(n*n);
			else tresh=(double)0.0;
			for (ip=1;ip<=n-1;ip++) {
				for (iq=ip+1;iq<=n;iq++) {
					g=(double)100.0*Math.abs(a[ip-1][iq-1]);
					if (i>4 && (double)(Math.abs(d[ip-1])+g)==
						(double)Math.abs(d[ip-1]) &&
						(double)(Math.abs(d[iq-1])+g)==
						(double)Math.abs(d[iq-1])) a[ip-1][iq-1]=(double)0.0;

					else if (Math.abs(a[ip-1][iq-1])>tresh) {
						h=d[iq-1]-d[ip-1];
						if ((double)(Math.abs(h)+g)==(double)Math.abs(h))
							t=(a[ip-1][iq-1])/h;
						else {
							theta=(double)0.5*h/(a[ip-1][iq-1]);
							t=(double)(1.0/(Math.abs(theta)+Math.sqrt(1.0+theta*theta)));
							if (theta<(double)0.0) t=-t;
						}
						c=(double)(1.0/Math.sqrt(1+t*t));
						s=t*c;
						tau=s/((double)1.0+c);
						h=t*a[ip-1][iq-1];
						z[ip-1]-=h;
						z[iq-1]+=h;
						d[ip-1]-=h;
						d[iq-1]+=h;
						a[ip-1][iq-1]=(double)0.0;
						for (j=1;j<=ip-1;j++) {
							rotate(a,j,ip,j,iq,s,tau);
						}
						for (j=ip+1;j<=iq-1;j++) {
							rotate(a,ip,j,j,iq,s,tau);
						}
						for (j=iq+1;j<=n;j++) {
							rotate(a,ip,j,iq,j,s,tau);
						}
						for (j=1;j<=n;j++) {
							rotate(v,j,ip,j,iq,s,tau);
						}
						++(nrot);
					}
				}
			}
			for (ip=1;ip<=n;ip++) {
				b[ip-1]+=z[ip-1];
				d[ip-1]=b[ip-1];
				z[ip-1]=(double)0.0;
			}
		}

		// change -> exception
		System.out.println("Demasiadas iteraciones en rutina jacobi");
	}




	// -----------------------------
	// INTERFACE CLASS
	// -----------------------------

	// Generate one row matrix from vector
	/**
	* <p>
	* This static method returns one row matrix from a vector
	* </p>
	* @param A   the vector, an array of double values
	* @return the bi-dimensional matrix of A as the only row
	*/
	public static double[][] oneRowMatrix( double[] A) {
		double[][] R=new double[1][A.length];
		R[0]=A;
		return R;
	}
	// Generate one column matrix from vector
	/**
	* <p>
	* This static method returns one column matrix from a vector
	* </p>
	* @param A   the vector, an array of double values
	* @return the bi-dimensional matrix of A as the only column
	*/
	public static double[][] oneColumnMatrix( double[] A) {
		return tr(oneRowMatrix(A));
	}
	// Sum
	/**
	* <p>
	* This static method returns a matrix as the arithmetic sum
	* of the two matrixes given as parameters
	* </p>
	* @param A   the first matrix to sum, a bi-dimensional array of doubles
	* @param B   the second matrix to sum, a bi-dimensional array of doubles
	* @return the bi-dimensional array of doubles, the matrix (A+B)
	*/
	public static double[][] matSum( double[][] A, double[][] B)
		throws DimensionError {
			int i,j,k;
			if (A.length!=B.length || A[0].length!=B[0].length) {
				throw new DimensionError(
										 "A="+A.length+" "+A[0].length+
										 " B="+B.length+" "+B[0].length);
			}
			double[][] C=new double[A.length][A[0].length];
			for (i=0;i<C.length;i++)
				for (j=0;j<C[i].length;j++) C[i][j]=A[i][j]+B[i][j];

			return C;
		}

	// Dif
	/**
	* <p>
	* This static method returns a matrix as the arithmetic difference
	* of the two matrixes given as parameters
	* </p>
	* @param A   the first matrix, a bi-dimensional array of doubles
	* @param B   the second matrix (the minuend), a bi-dimensional array of doubles
	* @return the bi-dimensional array of doubles, the matrix (A-B)
	*/
	public static double[][] matDif( double[][] A, double[][] B)
		throws DimensionError {
			int i,j,k;
			if (A.length!=B.length || A[0].length!=B[0].length) {
				throw new DimensionError(
										 "A="+A.length+" "+A[0].length+
										 " B="+B.length+" "+B[0].length);
			}
			double[][] C=new double[A.length][A[0].length];
			for (i=0;i<C.length;i++)
				for (j=0;j<C[i].length;j++) C[i][j]=A[i][j]-B[i][j];

			return C;
		}

	// Mult matrix double
	/**
	* <p>
	* This static method returns a matrix as the arithmetic product of the
	* given matrixes and a scalar real value.
	* </p>
	* @param A   the first matrix, a bi-dimensional array of doubles
	* @param B   the scalar real value, a double
	* @return the bi-dimensional array of doubles, the matrix (A.*B)
	*/
	public static double[][] matMul( double[][] A, double k) {
		int i,j;
		double[][] C=new double[A.length][A[0].length];
		for (i=0;i<C.length;i++)
			for (j=0;j<C[i].length;j++) C[i][j]=A[i][j]*k;

		return C;
	}


	// Mult
	/**
	* <p>
	* This static method returns a matrix as the arithmetic product of the
	* two matrixes given as parameters. The dimensions of the matrixes must
	* match: the number of columns of the first must be equals to the number
	* of rows of the second.
	* </p>
	* @param A   the first matrix, a bi-dimensional array of doubles
	* @param B   the second matrix, a bi-dimensional array of doubles
	* @return the bi-dimensional array of doubles, the matrix (A*B)
	* @throws DimensionError if the matrixes dimensions do not match
	*/
	public static double[][] matMul( double[][] A, double[][] B)
		throws DimensionError {
			int i,j,k;
			if (A[0].length!=B.length) {
				throw new DimensionError(
										 "A="+A.length+" "+A[0].length+
										 " B="+B.length+" "+B[0].length);
			}
			double[][] C=new double[A.length][B[0].length];
			for (i=0;i<C.length;i++) {
				for (j=0;j<C[i].length;j++) {
					C[i][j]=0;
					for (k=0;k<A[0].length;k++) C[i][j]+=A[i][k]*B[k][j];
				}
			}
			return C;
		}

	// Transposed
	/**
	* <p>
	* This static method returns the transpose of a given matrix.
	* </p>
	* @param A   the matrix to transpose
	* @return the bi-dimensional array of doubles, the matrix (A*B)
	*/
	public static double[][] tr(double[][] A) {

		int i,j;
		double[][] R=new double[A[0].length][A.length];
		for (i=0;i<R.length;i++) {
			for (j=0;j<R[i].length;j++) R[i][j]=A[j][i];
		}
		return R;
	}

	// Determinant
	/**
	* <p>
	* This static method returns the determinant of a given matrix provided
	* it is not a Singular Matrix.
	* </p>
	* @param A   the matrix whose determinant is to be calculated, a
	*            bi-dimensional array of doubles
	* @return a double value with the determinant of A calculated.
	* @throws SingularError if the matrix A is singular.
	*/
	public static  double determinant(double[][] vA)
		throws SingularError {

			int N=vA.length;
			double[][] A = new double[N][N]; copy(A,vA);
			double[][] Y= new double[N][N];
			int [] indx=new int [N];
			double[] col=new double[N];
			double d = ludcmp(A,indx);

			for (int j=0;j<N;j++) { d*=A[j][j]; }

			return d;
		}

	// Inverse
	/**
	* <p>
	* This static method returns the inverse of a given matrix provided
	* it is not a Singular Matrix.
	* </p>
	* @param A   the matrix whose inverse is to be calculated, a
	*            bi-dimensional array of doubles
	* @return a bi-dimensional array of double values, the inv(A)
	* @throws SingularError if the matrix A is singular.
	*/
	public static  double[][] inv(double[][] vA) throws SingularError {

		int N=vA.length;
		double[][] A = new double[N][N]; copy(A,vA);
		double[][] Y= new double[N][N];
		int [] indx=new int [N];
		double[] col=new double[N];
		double d = ludcmp(A,indx);

		for (int j=0;j<N;j++) {
			int i;
			for (i=0;i<N;i++) col[i]=(double)0.0;
			col[j]=(double)1.0;
			lubksb(A,indx,col);
			for (i=0;i<N;i++) Y[j][i]=col[i];
		}
		return Y;
	}


	/**
	* <p>
	* This static method returns the inverse of a given matrix provided
	* it is not a Singular Matrix. If it does, then the method returns
	* false.
	* </p>
	* @param A   the matrix whose inverse is to be calculated, a
	*            bi-dimensional array of doubles. Matrix A is modified
	*            in this methods, and at the end, contains the calculated
	*            inverse matrix.
	* @return true if A is not a Singular Matrix, otherwise it is false
	*/
	public static boolean protectedInverse(double[][] A) {

		int N=A[0].length;
		int[] indx=new int[N];
		double[][] Y=new double[N][N];
		double[] col=new double[N];

		try {
			double d=ludcmp(A,indx);
		} catch(SingularError e) {
			return true;
		}

		for (int j=0;j<N;j++) {
			int i;
			for (i=0;i<N;i++) col[i]=(double)0.0;
			col[j]=(double)1.0;
			lubksb(A,indx,col);
			for (i=0;i<N;i++) Y[j][i]=col[i];
		}

		copy(A,Y);
		return false;

	}

	/**
	* <p>
	* This static method returns the Identity matrix with dimension nxn
	* </p>
	* @param n   the dimension of the Identity matrix
	* @return a bi-dimensional array of double values, the I matrix
	*/
	public static double[][] I(int n) {
		double[][] result=new double[n][n];
		for (int i=0;i<n;i++) {
			for (int j=0;j<n;j++)
				if (i==j) result[i][i]=1; else result[i][j]=0;
		}
		return result;
	}

	/**
	* <p>
	* This static method calculates the eigenvalues of a given matrix.
	* </p>
	* @param A   the matrix whose eigenvalues are to be calculated, a
	*            bi-dimensional array of doubles
	* @param P   a bi-dimensional array of dobule values with the
	*            calculated search matrix, P accomplish with
	*                tr(P)=inv(P) and P*D*tr(P)=A
	* @param D   the matrix with the calculated eigenvalues, a
	*            bi-dimensional array of doubles
	*/
	public static void getEigenValues(double[][] A,
									   double[][] P,
									   double[][] D) {

		// Diagonalize matrix
		// Search matrix P / tr(P)=inv(P)
		// and P*D*tr(P)=A and D diagonal

		double[] d=new double[A[0].length];
		jacobi(A,d,P);

		for (int i=0;i<D.length;i++)
			for (int j=0;j<D[i].length;j++) {
				if (i==j) D[i][i]=d[i]; else D[i][j]=0;
			}

	}

	/**
	* <p>
	* This static method calculates the scalar product of two vectors.
	* </p>
	* @param a   an array of doubles with the first vector
	* @param b   an array of doubles with the second vector
	* @return the scalar product of the two vectors as a double value
	*/
	public static double dot( double[] a, double[] b) {
			double result=0;
			for (int i=0; i<a.length;i++) result+=a[i]*b[i];
			return result;
		}

		// ---------------------
		// Code Test
		// ---------------------

	/**
	* <p>
	* This static method prints out to the standard output a given matrix.
	* </p>
	* @param B   a bi-dimiensional array of doubles with the matrix to be printed
	*/
		protected static void matprn(double [][] B) {

			for (int i=0;i<B.length;i++) {
				for (int j=0;j<B[i].length;j++)
					System.out.print(B[i][j]+" ");
				System.out.println();
			}
		}

		/**
		* <p>
		* This static method is given to test this class.
		* </p>
		* @param argv   an array of Strings with the parameters
		*/
		public static void main(String argv[]) {

			try {

				double[] A=new double[3];
				A[0]=1; A[1]=2; A[2]=3;
				double[][] B=oneRowMatrix(A);
				double[][] C=matMul(tr(B),B);
				C[0][0]=7;
				C[1][1]=13;
				C[2][2]=19;
				matprn(C);

				double [][] D=new double[C.length][C.length];
				copy(D,C);
				boolean b=protectedInverse(D);
				if (b) System.out.println("La matriz era singular");
				matprn(D);
				double [][] E=matMul(D,C);
				matprn(E);

				D=inv(C);
				E=matMul(D,C);
				matprn(E);

				double [][] P=new double[C.length][C.length];
				double [][] Diag=new double[C.length][C.length];
				getEigenValues(C,P,Diag);

				matprn(C);
				matprn(P);
				matprn(Diag);

				D=matMul(matMul(P,Diag),tr(P));
				matprn(D);

			} catch(DimensionError e) {
				System.err.println(e);
			} catch(SingularError e) {
				System.err.println(e);
			}

		}

}


public class RSFSS {
	/**
	* <p>
	* RSFSS is the model to be obtained as the regression model using the
	* fuzzy random sets regression algorithm.
	*
	* Detailed in:
	*
	* L. Sánchez. A Random Sets-Based Method for Identifying Fuzzy Models. Fuzzy Sets
	* and Systems 98:3 (1998) 343-354.
	*
	* </p>
	*/
    //The input data from the dataset
	double [][]X;
	//the output data from the dataset
    double []Y;
    //The mean Y in a cluster
    double []meanY;
    //The covariance matrix for each cluster
    double [][][]Covar;
    //The centres matrix for each cluster found
    double [][][]centres;
    //The rule coefficients
    double [][][]A;
	//The rule weight
    double w[];

	/**
	* <p>
	* This private method clones a given double values array.
	* </p>
	* @param x   the array of doubles to clone
	* @return an array of double values, a perfect copy of x.
	*/
	private double[] clone(double[] x) {
		double[] res=new double[x.length];
		for (int i=0;i<x.length;i++) res[i]=x[i];
		return res;
	}
	/**
	* <p>
	* This private method copies all the elements in a given
	* double values array but the last one, which is skipped.
	* The resultant vector has one dimension less than the
	* given one.
	* </p>
	* @param x   the array of doubles to clone
	* @return an array of double values with all the elements in x except the last one.
	*/
	private double[] truncate(double[] x) {
		double[] res=new double[x.length-1];
		for (int i=0;i<res.length;i++) res[i]=x[i];
		return res;
	}
	/**
	* <p>
	* This private method copies all the elements in a given
	* double values bi-dimensional array.
	* </p>
	* @param x   the bi-dimensional array of doubles to copy
	* @return a bi-dimensional array of double values as a copy of x.
	*/
	private double[][] clone(double[][] x) {
		double[][] res=new double[x.length][];
		for (int i=0;i<x.length;i++) res[i]=clone(x[i]);
		return res;
	}
	/**
	* <p>
	* This private method copies all the elements in a given
	* double values bi-dimensional array but the last one in each row,
	* which is skipped.
	* The resultant matrix has one column less than the given one.
	* </p>
	* @param x   the bi-dimensional array of doubles to truncate
	* @return a bi-dimensional array of double values truncating the last column in x.
	*/
	private double[][] truncateColumn(double[][] x) {
		double[][] res=new double[x.length-1][];
		for (int i=0;i<res.length;i++) res[i]=truncate(x[i]);
		return res;
	}
	/**
	* <p>
	* This private method copies all the rows in a given double
	* values bi-dimensional array but the last row, which is skipped.
	* The resultant matrix has one row less than the given one.
	* </p>
	* @param x   the bi-dimensional array of doubles to truncate
	* @return a bi-dimensional array of double values truncating the last row in x.
	*/
	private double[][] truncateRow(double[][] x) {
		double[][] res=new double[x.length-1][];
		for (int i=0;i<res.length;i++) res[i]=clone(x[i]);
		return res;
	}
	/**
	* <p>
	* This private method inserts in a matrix one new row.
	* </p>
	* @param x   the bi-dimensional array of doubles
	* @param y   the array of doubles to insert as the last row
	* @return a bi-dimensional array of double values with the new row included.
	*/
	private double[] insertRow(double[] x, double y) {
		double[] res=new double[x.length+1];
		for (int i=0;i<x.length;i++) res[i]=x[i];
		res[res.length-1]=y;
		return res;
	}

	/**
	* <p>
	* This method groups two Clusters in a new one provided both are independent.
	* Otherwise an exception is thrown.
	* </p>
	* @param l   the first (left) Cluster
	* @param r   the second (right) cluster
	* @return a new Cluster.
	* @throws {@link MatrixCalcs.DimensionError}, {@link MatrixCalcs.SingularError}
	*         if the Clusters given as parameters aren't independent.
	*/
	Cluster groupTwoClusters(Cluster l, Cluster r) throws MatrixCalcs.DimensionError, MatrixCalcs.SingularError {
		Cluster result = new Cluster();
		double p=(1.0*l.n)/(l.n+r.n);
		result.n=l.n+r.n;
		result.m=MatrixCalcs.matSum(MatrixCalcs.matMul(l.m,p),MatrixCalcs.matMul(r.m,(1-p)));
		double[][] tmpl=MatrixCalcs.matMul(l.C,p);
		double[][] tmpr=MatrixCalcs.matMul(r.C,1-p);
		double[][] mresta=MatrixCalcs.matDif(l.m,r.m);
		double[][] tmpm=MatrixCalcs.matMul(mresta,MatrixCalcs.tr(mresta));
		tmpm=MatrixCalcs.matMul(tmpm,p*(1-p));
		result.C=MatrixCalcs.matSum(tmpl,MatrixCalcs.matSum(tmpr,tmpm));
		result.e=Math.log(MatrixCalcs.determinant(result.C));
		result.leftHandStree=l;
		result.rightHandStree=r;
		result.ord=-1;
		return result;
	}

	/**
	* <p>
	* This method sets a label to a the first unlabeled Cluster in the dendogram.
	* The depth-search is left-handed search.
	* </p>
	* @param c   the list of labels, an array of integers
	* @param cl  the dendogram, a Cluster
	* @param val the label, an integer
	*/
	void setLabel(int c[], Cluster cl, int val) {
		if (cl.ord>=0) {
			c[cl.ord]=val;
		} else {
			setLabel(c,cl.leftHandStree,val);
			setLabel(c,cl.rightHandStree,val);
		}

	}

	/**
	* <p>
	* Class constructor.
	* </p>
	* @param pX  the input data from the dataset as a bi-dimensional double array
	* @param pY  the output data from the dataset as an array of doubles
	*/
	public RSFSS(double[][]pX, double[]pY) {
	    System.out.println("Generating an RSFSS");
        X=pX; Y=pY;
	}

	//Modelling algorithm based on Random sets FSS 2000
	/**
	* <p>
	* This methods carries out the modelling algorithm based on Random Sets FSS 2000 without using
	* lables for the clusters.
	* </p>
	* @param NC an integer with the number of clusters to generate
	* @param r  a Randomize object
	* @param sigma a double value with the sigmoide value for the centres membership functions
	*/
    public void RSFSSX3(int NC, Randomize r, double sigma) {

		double [][]C0=MatrixCalcs.matMul(MatrixCalcs.I(X[0].length+1),sigma);
		Cluster dendogram[]=new Cluster[X.length];
		boolean unmarked[]=new boolean[X.length];

		try {
		    int numclusters=X.length;
			for (int i=0;i<X.length;i++) {
				dendogram[i]=new Cluster();
				dendogram[i].m=MatrixCalcs.oneColumnMatrix(insertRow(X[i],Y[i]));
				dendogram[i].C=clone(C0);
				dendogram[i].n=1;
				dendogram[i].e=Math.log(MatrixCalcs.determinant(dendogram[i].C));
				dendogram[i].leftHandStree=null;
				dendogram[i].rightHandStree=null;
				dendogram[i].ord=i;
			}

			do {
				// Nearest cluster are joined
				double minEnergy=0; int mini=0; int minj=0; boolean isTheFirst=true;
				for (int i=0;i<X.length;i++)
				{
					if (unmarked[i]) continue;
					for (int j=i+1;j<X.length;j++)
					{
						if (unmarked[j]) continue;
						Cluster tmp=groupTwoClusters(dendogram[i],dendogram[j]);
						if (tmp.e<minEnergy || isTheFirst) {
							isTheFirst=false;
							minEnergy=tmp.e;
							mini=i; minj=j;
						}
					}
				}
				//System.out.println("Number of clusters="+numclusters+". Clusters association"+mini+" "+minj);
				dendogram[mini]=groupTwoClusters(dendogram[mini],dendogram[minj]);
				unmarked[minj]=true; dendogram[minj].n=0;
				numclusters--;
			} while(numclusters!=NC);

			// Let's calcule regression plane for each cluster points/examples
			A=new double[NC][][];
			Covar=new double[NC][][];
			int c[]=new int[X.length];
            centres = new double[NC][][];
			int num[]=new int[NC];
			meanY = new double[NC];
			w=new double[NC];

			int theRule=0;
			for (int example=0;example<X.length;example++) {
				if (unmarked[example]) continue;
				System.out.println("Cluster labelling "+theRule);
				Covar[theRule]=MatrixCalcs.inv(truncateColumn(dendogram[example].C));
				setLabel(c,dendogram[example],theRule);
				centres[theRule]=truncateRow(dendogram[example].m);
				meanY[theRule]=dendogram[example].m[dendogram[example].m.length-1][0];
				num[theRule]=dendogram[example].n;
				w[theRule]=(1.0*num[theRule])/X.length*MatrixCalcs.determinant(Covar[theRule]);
				System.out.println("Cluster associated examples "+theRule+" = "+num[theRule]);
				theRule++;
			}

			// It's only used examples corresponding to each rule
			double theWeights[] = new double[c.length];
            for (theRule=0; theRule<NC; theRule++) {

				System.out.println("Consequent processing "+theRule);


                // Regression planes are calculated

                // Xm is X - centros[theRule] * root weight
                // Ym is Y - Y mean *  root weight
                double[][] Xm = new double[num[theRule]][X[0].length];
                double[][] Ym = new double[num[theRule]][1];
				int example=0;
                for (int i=0;i<X.length;i++) {
				    if (c[i]!=theRule) continue;
                    for (int j=0;j<Xm[example].length;j++) {
                        Xm[example][j]=(X[i][j]-centres[theRule][j][0]);
                    }
                    Ym[example][0]=(Y[i]-meanY[c[i]]);
					example++;
                }

                A[theRule] = MatrixCalcs.matMul(
												   MatrixCalcs.matMul(
																		   MatrixCalcs.inv(MatrixCalcs.matMul(MatrixCalcs.tr(Xm),Xm)),
																		   MatrixCalcs.tr(Xm)),Ym);

                System.out.print("Rule coefficient "+theRule+" ");
                for (int i=0;i<A[theRule].length;i++) System.out.print(A[theRule][i][0]+" ");
                System.out.print(" Y="+meanY[theRule]+" centres=");
                for (int i=0;i<centres[theRule].length;i++)
                    System.out.print(centres[theRule][i][0]+" ");
                System.out.println();
            }



        } catch(MatrixCalcs.SingularError e) {
			System.err.println("Singular Matrix: "+e);
		} catch(MatrixCalcs.DimensionError e) {
			System.err.println("Internal Error: "+e);
		}


	}



	/**
	* <p>
	* This methods returns a double value with the membership degree of an example to a given
	* cluster.
	* </p>
	* @param x the example for which its membership degreee is to be evaluated
	* @param c the centres  matrix
	* @param C the covariance matrix
	* @return the double value with the membership degree of the given example.
	* @throws {@link MatrixCalcs.DimensionError} if any calculation error occurs
	*/
	double evaluateMembership(double x[], double c[][], double C[][]) throws MatrixCalcs.DimensionError {

        double [][] xc = MatrixCalcs.matDif(MatrixCalcs.oneRowMatrix(x),MatrixCalcs.tr(c));
        double[][] tmp = MatrixCalcs.matMul(xc,C);
        tmp = MatrixCalcs.matMul(tmp,MatrixCalcs.tr(xc));

        // This parameter can be line command parameter
		return Math.exp(-0.5*tmp[0][0]);

    }

	/**
	* <p>
	* This methods returns a double value with the distance between the given example and
	* a cluster.
	* </p>
	* @param x the example for which its membership degreee is to be evaluated
	* @param c the centres  matrix
	* @param A the rule coefficient
	* @return the double value with the distance of the given example and the cluster.
	*/
    double predy(double []x, double [][]c, double[][]A, double my) {

        double suma=0;
        for (int i=0;i<x.length;i++) suma+=(x[i]-c[i][0])*A[i][0];
        return my+suma;

    }

	/**
	* <p>
	* This methods returns a evaluation of the given example using all the clusters found.
	* </p>
	* @param x the example for which its membership degreee is to be evaluated
	* @return the double array with the evaluation of the given example for eac cluster.
	*/
    public double[] getOutput(double []x) {

        double num=0;
        double den=0;
        try {
            for (int i=0;i<Covar.length;i++) {
                double mu = evaluateMembership(x,centres[i],Covar[i]);
                double sal= predy(x,centres[i],A[i],meanY[i]);
                num+=w[i]*mu*sal;
                den+=w[i]*mu;
            }
        } catch (MatrixCalcs.DimensionError e) {
            System.err.println("Internal Error");
        }
        double res[] = new double[1];
        if (den>0) res[0]=num/den;
        return res;
    }


	//Modelling algorithm based on Random sets FSS 2000
	/**
	* <p>
	* This methods carries out the modelling algorithm based on Random Sets FSS 2000 using
	* lables for the clusters.
	* </p>
	* @param NC an integer with the number of clusters to generate
	* @param r  a Randomize object
	* @param sigma a double value with the sigmoide value for the centres membership functions
	*/
    public void RSFSSX2(int NC, Randomize r, double sigma) {

		double [][]C0=MatrixCalcs.matMul(MatrixCalcs.I(X[0].length),sigma);
		Cluster dendogram[]=new Cluster[X.length];
		boolean unmarked[]=new boolean[X.length];

		try {
		    int numclusters=X.length;
			for (int i=0;i<X.length;i++) {
				dendogram[i]=new Cluster();
				dendogram[i].m=clone(MatrixCalcs.oneColumnMatrix(X[i]));
				dendogram[i].C=clone(C0);
				dendogram[i].n=1;
				dendogram[i].e=Math.log(MatrixCalcs.determinant(dendogram[i].C));
				dendogram[i].leftHandStree=null;
				dendogram[i].rightHandStree=null;
				dendogram[i].ord=i;
			}

			do {
				// Nearest cluster are joined
				double minEnergy=0; int mini=0; int minj=0; boolean theFirst=true;
				for (int i=0;i<X.length;i++)
				{
					if (unmarked[i]) continue;
					for (int j=i+1;j<X.length;j++)
					{
						if (unmarked[j]) continue;
						Cluster tmp=groupTwoClusters(dendogram[i],dendogram[j]);
						if (tmp.e<minEnergy || theFirst) {
							theFirst=false;
							minEnergy=tmp.e;
							mini=i; minj=j;
						}
					}
				}
				//System.out.println("Clusters="+numclusters+". Cluster association "+mini+" "+minj);
				dendogram[mini]=groupTwoClusters(dendogram[mini],dendogram[minj]);
				unmarked[minj]=true; dendogram[minj].n=0;
				numclusters--;
			} while(numclusters!=NC);

			// Let's calcule regression plane for each cluster points/examples
			A=new double[NC][][];
			Covar=new double[NC][][];
			int c[]=new int[X.length];
            centres = new double[NC][][];
			w = new double[NC];

			int theRule=0;
			for (int example=0;example<X.length;example++) {
				if (unmarked[example]) continue;
				System.out.println("Cluster labelling "+theRule);
				Covar[theRule]=MatrixCalcs.inv(dendogram[example].C);
				setLabel(c,dendogram[example],theRule);
				centres[theRule]=clone(dendogram[example].m);
				theRule++;
			}


			// It's calculated means for independent variables
			int num[]=new int[NC];
			meanY = new double[NC];

			for (int example=0;example<X.length;example++) {
				meanY[c[example]]+=Y[example];
				num[c[example]]++;
			}

			for (theRule=0;theRule<NC;theRule++) {
				System.out.println("Cluster association examples "+theRule+" = "+num[theRule]);
				meanY[theRule]/=num[theRule];
				w[theRule]=(1.0*num[theRule])/X.length*MatrixCalcs.determinant(Covar[theRule]);
			}


			double theWeights[] = new double[c.length];
            for (theRule=0; theRule<NC; theRule++) {

				System.out.println("Consequent processing "+theRule);

                 // Regression planes are calculated

                 // Xm is X - centros[theRule] * root weight
                 // Ym is Y - Y mean *  root weight

                double[][] Xm = new double[num[theRule]][X[0].length];
                double[][] Ym = new double[num[theRule]][1];
				int example=0;
                for (int i=0;i<X.length;i++) {
				    if (c[i]!=theRule) continue;
                    for (int j=0;j<Xm[example].length;j++) {
                        Xm[example][j]=(X[i][j]-centres[theRule][j][0]);
                    }
                    Ym[example][0]=(Y[i]-meanY[c[i]]);
					example++;
                }

                try {
					A[theRule] = MatrixCalcs.matMul(
													   MatrixCalcs.matMul(
																			   MatrixCalcs.inv(MatrixCalcs.matMul(MatrixCalcs.tr(Xm),Xm)),
																			   MatrixCalcs.tr(Xm)),Ym);
                } catch(MatrixCalcs.SingularError e) {
					System.err.println("Quite few examples for theRule "+ theRule+" : "+e+" : ");
					A[theRule]=new double[X[0].length][1];
				}
                System.out.print("Rule Coefficients "+theRule+" ");
                for (int i=0;i<A[theRule].length;i++) System.out.print(A[theRule][i][0]+" ");
                System.out.print(" Y="+meanY[theRule]+" entres=");
                for (int i=0;i<centres[theRule].length;i++)
                    System.out.print(centres[theRule][i][0]+" ");
                System.out.println();
            }



		} catch(MatrixCalcs.SingularError e) {
			System.err.println("Internal Error:" +e);
		} catch(MatrixCalcs.DimensionError e) {
			System.err.println("Internal Error: "+e);
		}


	}


}

