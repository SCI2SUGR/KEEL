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

package keel.Algorithms.Statistical_Classifiers.Shared.MatrixCalcs;

import java.io.*;

public class MatrixCalcs {

  
  // Protected/private methods
  protected static final double MINVALPRO=(double)1.0e-10;

  protected static double ludcmp(double[][] a,int[] indx)
    throws ErrorSingular {

    // New values for a, indx, d
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
        throw new ErrorSingular("Matriz singular en ludcmp");
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
          throw new ErrorSingular("Matriz Singular en ludcmp");

      if (j!=n) {
	dum=(double)1.0/(a[j-1][j-1]);
	for (i=j+1;i<=n;i++) a[j-1][i-1]*=dum;
      }
    }
    return d;
  }

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

  private static void copia(double[][] A, double[][] vA) {
    for (int i=0;i<vA.length;i++)
     for (int j=0;j<vA[i].length;j++) A[i][j]=vA[i][j];
  }

  private static void rotate(double a[][],
                             int i, int j, int k, int l,
                             double s, double tau) {
    double g=a[i-1][j-1];
    double h=a[k-1][l-1];
    a[i-1][j-1]=g-s*(h+g*tau);
    a[k-1][l-1]=h+s*(g-h*tau);
  }

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

    System.out.println("Demasiadas iteraciones en rutina jacobi");
  }




  // -----------------------------
  // CLASS ACCESS INTERFACE
  // -----------------------------

  // Tranform a vector to a matrix row
  public static double[][] fila( double[] A) {
    double[][] R=new double[1][A.length];
    R[0]=A;
    return R;
  }
  // Tranform a vector to a matrix column
  public static double[][] columna( double[] A) {
    return tr(fila(A));
  }
  // Matrix addition
  public static double[][] matsum( double[][] A, double[][] B)
    throws ErrorDimension {
    int i,j,k;
    if (A.length!=B.length || A[0].length!=B[0].length) {
      throw new ErrorDimension(
      "A="+A.length+" "+A[0].length+
      " B="+B.length+" "+B[0].length);
    }
    double[][] C=new double[A.length][A[0].length];
    for (i=0;i<C.length;i++) 
      for (j=0;j<C[i].length;j++) C[i][j]=A[i][j]+B[i][j];

    return C;
  }
  // Scalar-Matrix product
  public static double[][] matmul( double[][] A, double k) {
    int i,j;
    double[][] C=new double[A.length][A[0].length];
    for (i=0;i<C.length;i++) 
      for (j=0;j<C[i].length;j++) C[i][j]=A[i][j]*k;

    return C;
  }


  // Matrix product
  public static double[][] matmul( double[][] A, double[][] B)
    throws ErrorDimension {
    int i,j,k;
    if (A[0].length!=B.length) {
      throw new ErrorDimension(
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

  // Matrix transposition
  public static double[][] tr(double[][] A) {

    int i,j;
    double[][] R=new double[A[0].length][A.length];
    for (i=0;i<R.length;i++) {
      for (j=0;j<R[i].length;j++) R[i][j]=A[j][i];
    }
    return R;
  }

  // Matrix Determinant
  public static  double determinante(double[][] vA) 
    throws ErrorSingular {

    int N=vA.length;
    double[][] A = new double[N][N]; copia(A,vA);
    double[][] Y= new double[N][N];
    int [] indx=new int [N];
    double[] col=new double[N];
    double d = ludcmp(A,indx);

    for (int j=0;j<N;j++) { d*=A[j][j]; }

    return d;
  }

  // Matrix inverse
  public static  double[][] inv(double[][] vA) throws ErrorSingular {

    int N=vA.length;
    double[][] A = new double[N][N]; copia(A,vA);
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


  public static boolean inv_prot(double[][] A) {

    int N=A[0].length;
    int[] indx=new int[N];
    double[][] Y=new double[N][N];
    double[] col=new double[N];

    try {
      double d=ludcmp(A,indx);
    } catch(ErrorSingular e) {
      return true;
    }

    for (int j=0;j<N;j++) {
      int i;
      for (i=0;i<N;i++) col[i]=(double)0.0;
      col[j]=(double)1.0;
      lubksb(A,indx,col);
      for (i=0;i<N;i++) Y[j][i]=col[i];
    }

    copia(A,Y);
    return false;

  }

  public static double[][] I(int n) {
    double[][] result=new double[n][n];
    for (int i=0;i<n;i++) {
      for (int j=0;j<n;j++)
         if (i==j) result[i][i]=1; else result[i][j]=0;
    }
    return result;
  }

  public static void valores_propios(double[][] A,
		       double[][] P,
		       double[][] D) {

    // Matrix diagonalize. Temporal matrix is P.
    // (trn(P) = inv(P), P*D*tr(P)=A and diaginal D)
     
    double[] d=new double[A[0].length];
    jacobi(A,d,P);

    for (int i=0;i<D.length;i++)
      for (int j=0;j<D[i].length;j++) {
         if (i==j) D[i][i]=d[i]; else D[i][j]=0;
       }

  }

  public static double dot( double[] a, double[] b) {
    double result=0;
    for (int i=0; i<a.length;i++) result+=a[i]*b[i];
    return result;
  }

  // ---------------------
  // Tests
  // ---------------------

  protected static void matprn(double [][] B) {

    for (int i=0;i<B.length;i++) {
     for (int j=0;j<B[i].length;j++)
       System.out.print(B[i][j]+" ");
     System.out.println();
    }
  }

  public static void main(String argv[]) {

    try {

    double[] A=new double[3];
    A[0]=1; A[1]=2; A[2]=3;
    double[][] B=fila(A);
    double[][] C=matmul(tr(B),B);
    C[0][0]=7;
    C[1][1]=13;
    C[2][2]=19;
    matprn(C);

    double [][] D=new double[C.length][C.length];
    copia(D,C);
    boolean b=inv_prot(D);
    if (b) System.out.println("La matriz era singular");
    matprn(D);
    double [][] E=matmul(D,C);
    matprn(E);

    D=inv(C);
    E=matmul(D,C);
    matprn(E);

    double [][] P=new double[C.length][C.length];
    double [][] Diag=new double[C.length][C.length];
    valores_propios(C,P,Diag);

    matprn(C);
    matprn(P);
    matprn(Diag);

    D=matmul(matmul(P,Diag),tr(P));
    matprn(D);

    } catch(ErrorDimension e) {
        System.err.println(e);
    } catch(ErrorSingular e) {
        System.err.println(e);
    }

  }

}

