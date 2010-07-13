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


import org.core.*;

/**
 * The class that writest the data base
 * @author Diana
 *
 */
public class EscribeBCLing {
	public int numReglas;
	public double []base;
	public double exit;
	

	/**
	 * It the function that writes
	 * @param fichero the name of file
	 * @param ec_tra The value of training MSE
	 * @param ec_tst The value of test MSE
	 * @param B An element of the Base class
	 * @param P An element of poblacion class
	 */
	public void write(String fichero, double ec_tra, double ec_tst, Base B, Poblacion P){
	
	
		int i,j;
		 String output = new String("");
		 output += "Numero de reglas: ";
		 output += B.getN_reglas()+"\n"+"\n";
		   for (i=0; i<B.getN_reglas(); i++) {
			   for (j=0; j<B.getN_variables(); j++)
				   output+=B.getBDatos_x0(j,B.getBregla(B.getIndex(i), j))+" "+B.getBDatos_x1(j,B.getBregla(B.getIndex(i), j))+" "+
						   B.getBDatos_x3(j,B.getBregla(B.getIndex(i), j))+"\n";		     
			   output+="\n";
		   }
			   if (B.getSalidaPDEF()!= 87654321)
				   output+="\nSalida por defecto: "+B.getSalidaPDEF()+"\n";

			   		output+="\n";
					
					output+="\nMSEtra: "+ec_tra+" MSEtst: "+ec_tst+"\n";

					 Fichero.escribeFichero(fichero, output);	   
			}
		  
	}
	
	

	


