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

public class Numeric {


  public static double secant(DoubleFunc fun, double a, double x0, double x1) {
       double xi = x0, xii= x1, x;
       double yi = fun.F(x0)-a, yii = fun.F(x1)-a, y;

       do {
             x = xii - yii * (xii - xi) / (yii - yi);
             y = fun.F(x)-a;

             xi = xii; xii = x;
             yi = yii; yii = y;
       } while (Math.abs(y)>PRECISION);

       return x;
  }

   /**
    * find inverse x of fun.F so that a = F(x), 
    * where x>=0, and fun.F is monotonically increasing.
    * start is a starting point, >= 0.
    **/
   public static double binsearch(DoubleFunc fun, double a, double start) {
        double xl = 0, xh = start, w = xh - xl;
        double y = fun.F(xh)-a; 
        while (y<-PRECISION) {
           xl = xh;
           xh = xh + w;
           y = fun.F(xh)-a; 
         
        }

        double xm = xl + w/2;
        y = fun.F(xm)-a; 
        while (Math.abs(y)>PRECISION && Math.abs(w)>PRECISION) {
            if (y>0) xh = xm; else xl = xm;
            w = xh - xl;
            xm = xl + w/2.0;
            y = fun.F(xm)-a; 
      //        System.out.println("w = "+w);
        }
        return xm;
   }

   public static double PRECISION = 1e-14;
}

