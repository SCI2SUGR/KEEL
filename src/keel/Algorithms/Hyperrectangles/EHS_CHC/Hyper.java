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



package keel.Algorithms.Hyperrectangles.EHS_CHC;

/**
* <p>Hyper class.
* @author Written by Salvador Garcia (University of Jaén) 6/06/2009
* @version 0.1
* @since JDK1.5
* </p>
*/
public class Hyper {
	
	public double x[];
	public double y[];
	public boolean nom[][];
	public int clase;
	
	public Hyper (double pointX[], double pointY[], boolean values[][], int c) {
		
		int i, j;
		
		clase = c;
		x = new double[pointX.length];
		y = new double[pointX.length];
		nom = new boolean[pointX.length][];
		for (i=0; i<x.length; i++) {
			if (values[i].length == 0) {
				nom[i] = new boolean[0];
				x[i] = pointX[i];
				y[i] = pointY[i];
			} else {
				nom[i] = new boolean[values[i].length];
				for (j=0; j<nom[i].length; j++) {
					nom[i][j] = values[i][j];
				}
			}
		}		
	}
	
	public boolean equalTo (Hyper obj) {
		
		boolean res = true;
		int i, j;
		
		for (i=0; i<x.length && res; i++) {
			if (nom[i].length == 0) {
				if (x[i] != obj.x[i])
					res = false;
				if (y[i] != obj.y[i])
					res = false;
			} else {
				for (j=0; j<nom[i].length && res; j++) {
					if (nom[i][j] != obj.nom[i][j])
						res = false;
				}
			}
			if (clase != obj.clase)
				res = false;		
		}
		
		return res;
	}

	public double volume () {
		
		double prod = 1;
		int i, j, cont;
		
		for (i=0; i<x.length; i++) {
			cont = 0;
			if (nom[i].length == 0) {
				if (x[i] != y[i]) {
					prod *= (y[i]-x[i]);
				}
			} else {
				for (j=0; j<nom[i].length; j++) {
					if (nom[i][j]) {
						cont++;
					}
				}
				if (cont > 0) {
					prod *= (double)cont / (double) nom[i].length;
				}
			} 
		}
		
		return prod;
	}
	
	public int dimensions () {
		
		int suma = x.length;
		int i, j;
		boolean non;

		for (i=0; i<x.length; i++) {
			if (nom[i].length == 0) {
				if (x[i] == y[i]) {
					suma--;
				}
			} else {
				non = false;
				for (j=0; j<nom[i].length && !non; j++) {
					if (nom[i][j] == false) {
						non = true;
					}
				}
				if (!non) {
					suma--;
				}
			}
		}
		
		return suma;	
	}
	
	public String toString () {
		
		int i, j;
		String cadena = "";
		boolean non;
		
		for (i=0; i<x.length; i++) {
			if (nom[i].length == 0) {
				cadena += "[" + x[i] + ", " + y[i] + "], ";
			} else {
				non = false;
				for (j=0; j<nom[i].length && !non; j++) {
					if (nom[i][j] == false) {
						non = true;
					}					
				}
				if (!non) {
					cadena += "[Don't Care], ";
				} else {
					cadena += "[";
					for (j=0; j<nom[i].length; j++) {
						if (nom[i][j]) {
							cadena += "X";
						} else {
							cadena += "O";							
						}
					}
					cadena += "], ";
				}
			}
		}
		cadena += clase + "\n";		
		
		return cadena;
	}
}