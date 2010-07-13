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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Thrift;

class Difuso {
	/* This class allows trapezium or triangular-shaped fuzzy set */
	public double x0, x1 ,x2 ,x3, y;
	public String Nombre, Etiqueta;

        public Difuso copia(){
            Difuso copy = new Difuso();
            copy.x0 = this.x0;
            copy.x1 = this.x1;
            copy.x2 = this.x2;
            copy.x3 = this.x3;
            copy.y = this.y;
            copy.Nombre = this.Nombre;
            copy.Etiqueta = this.Etiqueta;
            return copy;
        }
}

