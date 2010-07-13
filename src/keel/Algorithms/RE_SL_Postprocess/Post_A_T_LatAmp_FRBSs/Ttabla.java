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

package keel.Algorithms.RE_SL_Postprocess.Post_A_T_LatAmp_FRBSs;
/**
 * Class that defines the Ttabla
 * @author Diana Arquillos
 *
 */
public class Ttabla {
	public double [] ejemplo; 
	public double nivel_cubrimiento, maximo_cubrimiento;
    int cubierto;
 
/**
 * Constructor with one parameter
 * @param valor it defines the size of vector ejemplo 
 */    public Ttabla(int valor){
    	ejemplo=new double [valor];
    	this.nivel_cubrimiento = 0.0;
		this.maximo_cubrimiento = 0.0;
		this.cubierto = 0;
    }

/**
 * Constructor that defines all elements of the class
 * @param ejemplo it defines the vector
 * @param nivel_cubrimiento it defines the nivel_cubrimiento
 * @param maximo_cubrimiento it defines the maximo_cubrimiento
 * @param cubierto it defines the cubierto
 */    public Ttabla(double [] ejemplo, double nivel_cubrimiento,
			double maximo_cubrimiento, int cubierto) {
		super();
		this.ejemplo = ejemplo;
		this.nivel_cubrimiento = nivel_cubrimiento;
		this.maximo_cubrimiento = maximo_cubrimiento;
		this.cubierto = cubierto;
	}
    
}

