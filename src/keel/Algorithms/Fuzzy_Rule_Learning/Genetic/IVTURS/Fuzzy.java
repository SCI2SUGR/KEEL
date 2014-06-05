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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.IVTURS;

/**
 * <p>Title: Fuzzy</p>
 * <p>Description: This class contains the representation of a fuzzy value</p>
 * <p>Copyright: Copyright KEEL (c) 2007</p>
 * <p>Company: KEEL </p>
 * @author Jesus Alcalá (University of Granada) 09/02/2011
 * @author Jose Antonio Sanz (University of Navarra) 19/12/2011
 * @author Alberto Fernandez (University of Jaen) 24/10/2013
 * @version 1.2
 * @since JDK1.6
 */
public class Fuzzy {
	double x0, x1, x3, b1, b3, b4, y;
	String name;

	public Fuzzy() {
	}

	public double[] Fuzzifica(double X) {
		double gP[] = new double[2];
		//Lower bound computation
		if (X == x1) { 
			gP[0]=1.0;
		}

		if ( (X <= x0) || (X >= x3)) { /* X is not within the range of D, */
			gP[0]=0.0; /* membership function is 0 */
		}

		if ((X < x1)&(X > x0)) {
			gP[0]=( (X - x0) * (y / (x1 - x0)));
		}

		if ((X > x1)&(X<x3)) {
			gP[0]=( (x3 - X) * (y / (x3 - x1)));
		}
		//Upper bound computation
		if ((X<=b3)||(X>=b4)){
			gP[1]=0.0;
		}
		if (X==b1){
			gP[1]=1.0;
		}
		if ((X<b1)&(X>b3)){
			gP[1]=((X-b3)*(y/(b1-b3)));
		}
		if ((X>b1)&(X<b4)){
			gP[1]=((b4-X)*(y/(b4-b1)));
		}

		return (gP);

	}

	public Fuzzy clone(){
		Fuzzy d = new Fuzzy();
		d.x0 = this.x0;
		d.x1 = this.x1;
		d.x3 = this.x3;
		d.b1 = this.b1;
		d.b3 = this.b3;
		d.b4 = this.b4;
		d.y = this.y;
		d.name = new String(this.name);

		return d;
	}

	public String getName(){
		return (this.name);
	}
}
