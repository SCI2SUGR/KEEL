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

import java.io.*;
import java.util.*;
/**
 * The class that reads the file of the rule base
 * @author Diana Arquillos
 *
 */
public class LeerWm {


	private String rutaFichero;
	public int numReglas;
	public double []base;
	public double exit;
	/**
	 * It stores the name of the file
	 * @param ruta it contains the name of the file
	 */
	public LeerWm(String ruta) {

		this.rutaFichero = ruta;

	}

	/**
	 * It reads the file
	 * @param n_variables it contains the number of variables
	 */
	public void leer(int n_variables){


		File fichero = new File(this.rutaFichero);
		String linea = null;
		StringTokenizer tokens = null;
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(fichero));
			linea = reader.readLine();
			tokens = new StringTokenizer(linea,":");
			tokens.nextElement();
			numReglas = (int)Double.parseDouble(tokens.nextElement().toString());
			base = new double [numReglas*(3*n_variables)]; 			
			int i=0;
			int j=0;
			double aux;
			int contador=0;	
			
			linea = reader.readLine();
		
			while( j<((numReglas*n_variables))){
				linea = reader.readLine();
				tokens = new StringTokenizer(linea,"  ");
				
				aux = Double.parseDouble(tokens.nextElement().toString());
				base[i]=aux;
				i++;
				aux = Double.parseDouble(tokens.nextElement().toString());
				base[i]=aux;
				aux = Double.parseDouble(tokens.nextElement().toString());
				i++;
				base[i]=aux;
				i++;
				j++;
				contador++;
				if(contador==n_variables){
					contador=0;
					linea= reader.readLine();
				}
			}
			exit =-1;
		}
				
			
		catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		catch (Exception e) {

			e.printStackTrace();
		}
	}
	

}



