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
import java.util.Vector;

public class LinearSearchBrent {
    /**
     * <p>
     * <pre>
     * Brent's method is a complicated but popular root-finding algorithm combining the bisection method, 
     * the secant method and inverse quadratic interpolation.
     * 
     * In detail in:
     * 
     *  Brent (1973). Algorithms for Minimization without Derivatives. Prentice-Hall, Englewood Cliffs, NJ.
     *  </pre>
     *  </p>
     */
    // xbus and dbus are represented in matrix format to reuse it like weights
    // store and optimization process    
    double [][][] dSearch;    // Search direction
    double [][][] xSearch;    // Start point
    
    FUN f;    // Evaluation function
    /**
     * <p>
     *  Constructor for linear search based on Brent's method.
     * </p>
     * @param vf evaluation function.
     * @param vdbus search direction matrix.
     * @param vxbus start point matrix.
     */
    public LinearSearchBrent(FUN vf, double vdbus[][][], double vxbus[][][]) { 
        f=vf; 
        dSearch=vdbus;
        xSearch=vxbus;
    }
    /**
     * <p>
     *  Returns the error of the weights dSearch.
     * </p>
     * @param alpha the factor of modification of dSearch
     * @return the error of the neural network xSearch
     */
    public double g(double alpha) { 
        // f function in d direction
        double result=0;
         
        result = f.evaluate(OPV.sum(xSearch,OPV.multiply(alpha,dSearch))); 
        
        return result;
    }
    
   private final double INIT_STEP=0.01f;     // Step to find initial configuration
   private final double TOL_BLIN=1e-4f;      // Minimum interval
   private final double TOL_CERO=1e-9f;      // Division by zero
   private final double MIN_DELTABLIN=1e-6f; // Minimum distance between Brent steps
   private final int MAX_ITERBLIN=100;       // Maximum iterations in linear b.
   private final int MAX_ITERINI=50;         // Maximum iterations initial configuration

    class pair {
        double first, second;
        pair() { first=0; second=0; }
        pair(double x, double y) { first=x; second=y; }
    };
    
    final int lower_y=0;
    final int lower_x=1;
    /**
     * <p>
     *  Sorts the vector a by sorting direct method.
     * </p>
     * 
     * @param a the vector to be sorted
     * @param size the size of the vector
     * @param criterium if it is lower_y the second member is compared; else the fist one.
     */
    private void sort(pair a[], int size, int criterium) {
        // Insertion sort
        for (int i=1;i<size;i++) {
            pair x = new pair(a[i].first,a[i].second);
            int j;
            for (j=i-1;j>=0;--j) {
                if (criterium==lower_y && a[j].second<=x.second) break;
                if (criterium==lower_x && a[j].first<=x.first) break;
                a[j+1].first = a[j].first;
                a[j+1].second = a[j].second;
            }
            a[j+1].first = x.first;
            a[j+1].second = x.second;
        }   
    }
    /**
     * <p>
     * 
     * </p>
     * @param x
     * @param x1
     * @param x2
     * @param x3
     * @param f1
     * @param f2
     * @param f3
     * @return
     */
    private double q(double x, 
                    double x1, double x2, double x3,
                    double f1, double f2, double f3) {
        return 
        f1*(x-x2)*(x-x3)/(x1-x2)/(x1-x3)+
        f2*(x-x1)*(x-x3)/(x2-x1)/(x2-x3)+
        f3*(x-x1)*(x-x2)/(x3-x1)/(x3-x2);
        
    }
    /*
     * <p>
     *   Minimize function g(). Function g() is the error of the neural network.
     * </p>
     *  @param r random numbers generator
     *  @return the alpha that minimize function g().
     */
    public double minimumSearch(Randomize r) {

        boolean debug=false;
        if (debug) System.out.println("BL ...0");
        
        // 1-variable function minimization
        int iteracion=0;
        
        double x=-1,yl=-1,yr=-1,y=-1,xl=-1,xr=-1;
        
        // Search for three points in right configuration
        
        Vector tresp = new Vector();
        tresp.add(new pair(0,g(0)));
        double DOUBLESTEP=INIT_STEP*2;
        double AVESTEP=INIT_STEP/2;

        if (debug) System.out.println("BL ...1/2");
        
        for (int i=0;i<MAX_ITERINI;i++) {
        
            if (debug) System.out.println("BL ...1");
            tresp.add(new pair(DOUBLESTEP,g(DOUBLESTEP)));
            tresp.add(new pair(AVESTEP,g(AVESTEP)));
            DOUBLESTEP*=2;
            AVESTEP/=2;
            if (tresp.size()>=3) {
                // Search for minimum
                int minj=0;
                for (int j=0;j<tresp.size();j++) {
                    pair ptmp = (pair)tresp.get(j);
                    if (ptmp.second<y || j==0) {
                        y=ptmp.second;
                        x=ptmp.first;
                        minj=j;
                    }
                }
                // Searching for right-sidest points lower than x and higher than y
                // Searching for left-sidest points higher than x and lower than y
                
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
                
                // Centered position
                if (xl!=-1 && xr!=-1) break;
                
            }
        }
        
        if (xl==-1 || yr==-1) {
            return x;
        }

        if (debug) System.out.println("BL ...2");
        
        double fmin=y;
        while (xr-xl>TOL_BLIN && iteracion<MAX_ITERBLIN) {
            // Brent
            iteracion++;
        
            if (debug) System.out.println("BL ...3 "+iteracion);
            
            
            double b12=xl*xl-x*x;
            double b23=x*x-xr*xr;
            double b31=xr*xr-xl*xl;
            double a12=xl-x;
            double a23=x-xr;
            double a31=xr-xl;
            double denominador=a23*yl+a31*y+a12*yr;
            if (Math.abs(denominador)<TOL_CERO) {
                System.out.println("Funcion no convexa en Brent " + g(x));
                return x;   
            }
            double x4=0.5f*(b23*yl+b31*y+b12*yr)/(denominador);
            
            double y4=g(x4);

            if (debug) System.out.println("BL ...4 "+iteracion);

            if (!(xl<=x4 && x4<=xr)) {
                System.out.println("Error while finding x4, check tolerance");
                System.out.println("xl="+xl+" yl="+yl);
                System.out.println("x="+x+" y="+y);
                System.out.println("xr="+xr+" yr="+yr);
                System.out.println("x4="+x4+" y4="+y4);
                System.out.println("ql="+q(xl,xl,x,xr,yl,y,yr));
                System.out.println("q0="+q(x,xl,x,xr,yl,y,yr));
                System.out.println("qr="+q(xr,xl,x,xr,yl,y,yr));
                System.out.println("q4="+q(x4,xl,x,xr,yl,y,yr));
                return x;
            }
            
            // Keep tree point that minimize \sum y_i
            pair brent[] = new pair[4];
            brent[0]=new pair(xl,yl);
            brent[1]=new pair(x,y);
            brent[2]=new pair(xr,yr);
            brent[3]=new pair(x4,y4);
            sort(brent,4,lower_x);
            
            // Keep the minimum
            double minvy=brent[0].second; int iminvy=0;
            for (int i=0;i<4;i++) {
                if (minvy>brent[i].second) { minvy=brent[i].second; iminvy=i; }
            }

            if (debug) System.out.println("BL ...5 "+iteracion);
            
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
                // Algorithm is restarted
                return x;
            }
            
            if (Math.abs(minvy-fmin)<MIN_DELTABLIN) {
                return x;
            } else {
                fmin=minvy;
            }
            
        }
        
        if (iteracion>=MAX_ITERBLIN) {
            System.out.println("Too many iterations in Brent");
            // Algorithm is restarted
            return x;
            
        }
		
		// output conditions reached
        
        return x;
    }
    
    
}
