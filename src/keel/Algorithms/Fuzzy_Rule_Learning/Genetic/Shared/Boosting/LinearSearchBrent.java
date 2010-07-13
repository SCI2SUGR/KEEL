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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

import java.io.*;
import org.core.*;
import java.util.Vector;


class OPV {
   static double[] Suma(double[]a, double[]b) {
      double[] result=new double[a.length];
      for (int i=0;i<a.length;i++) result[i]=a[i]+b[i];
      return result;
   }
   static double[] Multiplica(double a, double[]b) {
      double[] result=new double[b.length];
      for (int i=0;i<b.length;i++) result[i]=a*b[i];
      return result;
   }
}

public class LinearSearchBrent {

  // xbus and dbus matrix form is neccesary
  // to keep compatibility with weights store process 
  // and optimization

  double [] dbus;    // Search direction
  double [] xbus;    // Initial point

  Fun f;    // Funtion to optimize

  public LinearSearchBrent(Fun vf, double vdbus[], double vxbus[]) { 
    f=vf; 
    dbus=vdbus;
    xbus=vxbus;
  }

  public double g(double alfa) { 
    // Function f in d direction
    return f.evalua(OPV.Suma(xbus,OPV.Multiplica(alfa,dbus))); 
  }

  private final double PASO_INI=1f;    // Initial step for surround min
  private final double TOL_BLIN=1e-2f; // extensive Interval 
  private final double TOL_CERO=1e-9f; // Division by 0
  private final double MIN_DELTABLIN=1e-6f; // Min brent steps

  // Values for little steps algorithm execution 
  // It is necessary introduce this params in the constructor!
  private final int MAX_ITERBLIN=10;     // Max iter. in linear search
  private final int MAX_ITERINI=10;      // Max iter. initial config

  class pair {
    double first, second;
    pair() { first=0; second=0; }
    pair(double x, double y) { first=x; second=y; }
  };

  final int menor_y=0;
  final int menor_x=1;

  private void sort(pair a[], int size, int criterio) {
     // Sort vector by insert method
     for (int i=1;i<size;i++) {
       pair x = new pair(a[i].first,a[i].second);
       int j;
       for (j=i-1;j>=0;--j) {
         if (criterio==menor_y && a[j].second<=x.second) break;
         if (criterio==menor_x && a[j].first<=x.first) break;
         a[j+1].first = a[j].first;
         a[j+1].second = a[j].second;
       }
       a[j+1].first = x.first;
       a[j+1].second = x.second;
     }   
  }

  private double q(double x, 
                  double x1, double x2, double x3,
                  double f1, double f2, double f3) {
     return 
             f1*(x-x2)*(x-x3)/(x1-x2)/(x1-x3)+
             f2*(x-x1)*(x-x3)/(x2-x1)/(x2-x3)+
             f3*(x-x1)*(x-x2)/(x3-x1)/(x3-x2);

  }

  public double EncuentraMinimoPositivo(Randomize r) {

   // Minimize one var function
   int iteracion=0;

   double x=-1,yl=-1,yr=-1,y=-1,xl=-1,xr=-1;

  // search 3 initial valid points

  Vector tresp = new Vector();
  double fit0=g(0);
  tresp.add(new pair(0,fit0));
  double PASODOBLE=PASO_INI*2;
  double PASOMITAD=PASO_INI/2;

  for (int i=0;i<MAX_ITERINI;i++) {

     tresp.add(new pair(PASODOBLE,g(PASODOBLE)));
     tresp.add(new pair(PASOMITAD,g(PASOMITAD)));
     PASODOBLE*=2;
     PASOMITAD/=2;
     if (tresp.size()>=3) {
        // minimal search
        int minj=0;
        for (int j=0;j<tresp.size();j++) {
          pair ptmp = (pair)tresp.get(j);
          if (ptmp.second<y || j==0) {
            y=ptmp.second;
            x=ptmp.first;
            minj=j;
          }
        }
        // complex search! 
        // Buscamos el punto ma la derecha de los que
        // tengan menor x y mayor y
        // Buscamos el punto m a la izquierda de los que
        // tengan mayor x y mayor y
        xl=-1;xr=-1; boolean first1=true, first2=true;
        for (int j=0;j<tresp.size();j++) {
          if (j==minj) continue;
          pair ptmp = (pair)tresp.get(j);
          if (ptmp.second>y) {
             if (ptmp.first<x) {
                if (ptmp.first>xl || first1) {
                   xl=ptmp.first; yl=ptmp.second;
                }
                first1=false;
             }
             if (ptmp.first>x) {
                if (ptmp.first<xr || first2) {
                   xr=ptmp.first; yr=ptmp.second;
                }
                first2=false;
             }
          }
        }

        // Position founded
        if (xl!=-1 && xr!=-1) break;

     }
  }
  // System.out.println("Configuracion inicial:");
  // System.out.println("xl="+xl+" yl="+yl);
  // System.out.println("x="+x+" y="+y);
  // System.out.println("xr="+xr+" yr="+yr);

 if (xl==-1 || yr==-1) {
   // System.out.println("No se encontro la configuracion inicial");
   return 0;
 }
   
   double fmin=y;
   while (xr-xl>TOL_BLIN && iteracion<MAX_ITERBLIN) {
      // Init Brent algorithm
      iteracion++;

      // If two points are equals
      // if (x-xl<TOL_BLIN) return x;
      // if (xr-x<TOL_BLIN) return x;
      // if (yl-y<TOL_BLIN && yr-y<TOL_BLIN) return x;

      double b12=xl*xl-x*x;
      double b23=x*x-xr*xr;
      double b31=xr*xr-xl*xl;
      double a12=xl-x;
      double a23=x-xr;
      double a31=xr-xl;
      double denominador=a23*yl+a31*y+a12*yr;
      if (Math.abs(denominador)<TOL_CERO) {
        // change -> Exception !!!
        System.out.println("Funcion no convexa en Brent " + g(x));
        return 0;   
      }
      double x4=0.5f*(b23*yl+b31*y+b12*yr)/(denominador);
      
      double y4=g(x4);
      if (!(xl<=x4 && x4<=xr)) {
       System.out.println("Error en determinaciÃ³n de x4, ajustar tolerancias");
       System.out.println("xl="+xl+" yl="+yl);
       System.out.println("x="+x+" y="+y);
       System.out.println("xr="+xr+" yr="+yr);
       System.out.println("x4="+x4+" y4="+y4);
       System.out.println("ql="+q(xl,xl,x,xr,yl,y,yr));
       System.out.println("q0="+q(x,xl,x,xr,yl,y,yr));
       System.out.println("qr="+q(xr,xl,x,xr,yl,y,yr));
       System.out.println("q4="+q(x4,xl,x,xr,yl,y,yr));
       return 0;
      }

      // choose 3 points that minimize \sum y_i
      pair brent[] = new pair[4];
      brent[0]=new pair(xl,yl);
      brent[1]=new pair(x,y);
      brent[2]=new pair(xr,yr);
      brent[3]=new pair(x4,y4);
      sort(brent,4,menor_x);

      // maintain the minimum
      double minvy=brent[0].second; int iminvy=0;
      for (int i=0;i<4;i++) {
         if (minvy>brent[i].second) { minvy=brent[i].second; iminvy=i; }
      }

      if (iminvy==1) {
        xl=brent[0].first; yl=brent[0].second;
        x=brent[1].first; y=brent[1].second;
        xr=brent[2].first; yr=brent[2].second;
      } else if (iminvy==2) {
        xl=brent[1].first; yl=brent[1].second;
        x=brent[2].first; y=brent[2].second;
        xr=brent[3].first; yr=brent[3].second;
      } else {
        System.out.println("Puntos no estan en la configuracion correcta");
        // Reset algorithm
        return 0;
      }

      // System.out.println("Nueva iteracion Brent");
      // System.out.println("xl="+xl+" yl="+yl);
      // System.out.println("x="+x+" y="+y);
      // System.out.println("xr="+xr+" yr="+yr);

      if (Math.abs(minvy-fmin)<MIN_DELTABLIN) {
           // System.out.println("Incremento entre pasos < delta_blin ");
           return x;
      } else {
           fmin=minvy;
      }

    }

    if (iteracion>=MAX_ITERBLIN) {
       System.out.println("Demasiadas iteraciones en Brent");
       // Reset algorithm
       return 0;

    }

    // System.out.println("Condiciones de salida alcanzadas " + g(x));
    return x;
}


  public double EncuentraMinimoSimple() {

   // Minimize one variable funcion
   int iteracion=0;

   double x=-1,yl=-1,yr=-1,y=-1,xl=-1,xr=-1;
   double xmin=0, ant_conv=0;

   // Initial configuration
   xl=-5; yl=g(xl);
   x=0; y=g(0);
   xr=5; yr=g(xr);

   while (xr-xl>TOL_BLIN && iteracion<MAX_ITERBLIN) {

      iteracion++;

      double b12=xl*xl-x*x;
      double b23=x*x-xr*xr;
      double b31=xr*xr-xl*xl;
      double a12=xl-x;
      double a23=x-xr;
      double a31=xr-xl;
      double denominador=a23*yl+a31*y+a12*yr;
      if (Math.abs(denominador)<TOL_CERO) {
        // System.out.println("Funcion no convexa en Brent " + g(x));
        return xmin;   
      }
      double x4=0.5f*(b23*yl+b31*y+b12*yr)/(denominador);
      
      double y4=g(x4);

      // choose 3 point that minimize \sum y_i
      pair brent[] = new pair[4];
      brent[0]=new pair(xl,yl);
      brent[1]=new pair(x,y);
      brent[2]=new pair(xr,yr);
      brent[3]=new pair(x4,y4);
      sort(brent,4,menor_x);

      // eliminate the maximum
      double maxvy=brent[0].second; int imaxvy=0;
      double minvy=maxvy; int iminvy=imaxvy;
      for (int i=1;i<4;i++) {
         if (maxvy<brent[i].second) { maxvy=brent[i].second; imaxvy=i; }
         if (minvy>brent[i].second) { minvy=brent[i].second; iminvy=i; }
      }

      if (imaxvy==3) {
        xl=brent[0].first; yl=brent[0].second;
        x=brent[1].first; y=brent[1].second;
        xr=brent[2].first; yr=brent[2].second;
      } else if (imaxvy==0) {
        xl=brent[1].first; yl=brent[1].second;
        x=brent[2].first; y=brent[2].second;
        xr=brent[3].first; yr=brent[3].second;
      } else if (imaxvy==1) {
        xl=brent[0].first; yl=brent[0].second;
        x=brent[2].first; y=brent[2].second;
        xr=brent[3].first; yr=brent[3].second;
      } else if (imaxvy==2) {
        xl=brent[0].first; yl=brent[0].second;
        x=brent[1].first; y=brent[1].second;
        xr=brent[3].first; yr=brent[3].second;
      }

      // System.out.println("Nueva iteracion Brent");
      // System.out.println("xl="+xl+" yl="+yl);
      // System.out.println("x="+x+" y="+y);
      // System.out.println("xr="+xr+" yr="+yr);
      // System.out.println("Convergencia en "+iteracion+" "+(yl+y+yr));
      xmin=brent[iminvy].first;
      if (yl+y+yr>=ant_conv && iteracion>1) {
          return xmin;
      }
      ant_conv=yl+y+yr;

    }

    if (iteracion>=MAX_ITERBLIN) {
       // System.out.println("Demasiadas iteraciones en Brent");
       return xmin;
    }

    // normal exit point!
    return xmin;
}


}



