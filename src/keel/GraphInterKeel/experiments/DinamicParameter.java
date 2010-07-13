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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package keel.GraphInterKeel.experiments;

import java.util.Vector;

/**
 *
 * @author tua
 */
public class DinamicParameter {

    public Vector<Vector<String>> parameter_data= new Vector<Vector<String>>();
   //Fist: number of instances
    //2: number of classes
    //3: number of attributes
    //4: The classes {0,1} for example
    //5: Begin the parameters of the algorithm
    
    public DinamicParameter()
    {
    }
    public int size()
    {
        return parameter_data.size();
    }
     public void insert(Vector<String> contain)
     {
         parameter_data.addElement(contain);
     }
     public void remove(int position)
     {
         parameter_data.remove(position);
     }
     public Vector<String> get(int position)
     {
         return parameter_data.get(position);
     }
     public void set(int position,Vector<String> contain)
     {
         parameter_data.set(position, contain);
     }
}

