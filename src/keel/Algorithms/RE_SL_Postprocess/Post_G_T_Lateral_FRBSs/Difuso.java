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


package keel.Algorithms.RE_SL_Postprocess.Post_G_T_Lateral_FRBSs;
/**
 * Class which defines the fuzzy number
 * @author Diana Arquillos
 *
 */
public class Difuso {
	 private double x0,x1,x2,x3,y;
	 private double ml;
	 public Difuso(){
	 } 

	 /**
	  * Constructor of the class
	  * @param x0 contains the value of the variable x0
	  * @param x1 contains the value of the variable x1
	  * @param x2 contains the value of the variable x2
	  * @param x3 contains the value of the variable x3
	  * @param y contains the value of the variable y
	  * @param m1 contains the value of the variable m1
	  */
	 public Difuso(double x0,double x1, double x2, double x3,double y, double m1){
		 this.x0=x0;
		 this.x1=x1;
		 this.x2=x2;
		 this.x3=x3;
		 this.y=y;
		 this.ml=m1;
	 }

	 /************************************************************/

	 public void set_difuso(Difuso v){
		 this.x0=v.x0;
		 this.x1=v.x1;
		 this.x2=v.x2;
		 this.x3=v.x3;
		 this.y=v.y;
		 this.ml=v.ml; 
	 }
	 
/**
 * The fuzzy interface
 * @param X The value on the one that is realized
 * @return returns the new value
 */	 
	 public double Fuzzifica (double X){
		if ((X<x0()) || (X>x3()))  /* Si X no esta en el rango de D, el */
	       return (0.0);           /* grado de pertenencia es 0 */

	    if (X<x1())
	       return ((X-x0())*(y()/(x1()-x0())));

	    if (X>x2())
	       return ((x3()-X)*(y()/(x3()-x2())));

	    return (y());
	 }
	 /**
	  * Function which calculates the area of the trapezoid to calculate the center of gravity
	  * @return the value of the area
	  */double AreaTrapecioX (){
	    double izq, centro, der;

	    if (x1() != x0())
	       izq = (2*x1()*x1()*x1() - 3*x0()*x1()*x1() + x0()*x0()*x0()) / (6*(x1()-x0()));
	    else
	       izq = 0;

	    centro = (x2()*x2()-x1()*x1())/2.0;

	    if (x3() != x2())
	       der = (2*x2()*x2()*x2() - 3*x3()*x2()*x2() + x3()*x3()*x3()) / (6*(x3()-x2()));
	    else
	       der = 0;

	    return (y() * (izq+centro+der));
	 }
	 /**
	  * Function which calculates the value of the area of the trapezoid
	  * @return the value of the area
	  */
	 double AreaTrapecio (){
	    double izq, centro, der;

	    if (x1()!=x0())
	       izq = (x1()*x1() - 2*x0()*x1() + x0()*x0()) / (2*(x1()-x0()));
	    else
	       izq = 0;

	    centro = x2()-x1();

	    if (x3() != x2())
	       der = (x3()*x3() - 2*x3()*x2() + x2()*x2()) / (2*(x3()-x2()));
	    else
	       der = 0;

	    return (y() * (izq+centro+der));
	 }

	 /************************************************************/
	 
	 public double x1(){
		 return x1;
		 
	 }
	 public double x0(){
		 return x0;
		 
	 }
	 public double x2(){
		 return x2;
		 
	 }
	 public double x3(){
		 return x3;
		 
	 }
	 public double y(){
		 return y;
		 
	 }
	 public double ml(){
		 return ml;
		 
	 }
	 public double setml(double value){
		 return ml=value;
		 
	 }
	 public void setx0(double value){
		 x0=value;
		 
	 }
	 public void setx1(double value){
		 x1=value;
		 
	 }
	 public void setx2(double value){
		 x2=value;
		 
	 }
	 public void setx3(double value){
		 x3=value;
		 
	 }
	 public void sety(double value){
		 y=value;
		 
	 }


}

