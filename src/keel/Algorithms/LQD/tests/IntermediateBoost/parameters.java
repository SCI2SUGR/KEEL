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

package keel.Algorithms.LQD.tests.IntermediateBoost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * 
 * File: parameters.java
 * 
 * Read the parameters original dataset
 * 
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */

public class parameters {

	
    /** pathname of the original dataset*/
    String original_data;

	public parameters(String Fileparameters) throws IOException
	{
		
		try{
			int i;
			String fichero="", linea, tok;
			StringTokenizer lineasFile, tokens;

        
        
			File fe = new File(Fileparameters);
			if(fe.exists()==false)
			{
				System.out.println("The file doesn't exist");
				System.exit(0);
			}

			BufferedReader input = new BufferedReader(new FileReader(Fileparameters));
			System.out.println(fichero);
			String read = input.readLine();
			while(read !=null)
			{
				fichero =fichero+read+"\n";
				read= input.readLine();
			}
        
			fichero += "\n";
			/*System.out.println("Total "+fichero);
			new BufferedReader(new InputStreamReader(System.in)).readLine();*/
        
			/* remove all \r characters. it is neccesary for a correct use in Windows and UNIX  */
			fichero = fichero.replace('\r', ' ');
        
			

			/* extract the differents tokens of the file */
			lineasFile = new StringTokenizer(fichero, "\n");

			i=0;
			while(lineasFile.hasMoreTokens()) 
			{
				linea = lineasFile.nextToken();  
				/*System.out.println("line ["+linea+"]");
				new BufferedReader(new InputStreamReader(System.in)).readLine();*/
				i++;
				tokens = new StringTokenizer(linea, " ,\t");
				if(tokens.hasMoreTokens())
				{
					tok = tokens.nextToken();
				  if(tok.equalsIgnoreCase("inputdata"))
					{
						getInputFiles(tokens);
				
					}
				  else throw new java.io.IOException("Syntax error on line " + i + ": [" + tok + "]\n");


				}                                                      

			}//while


		}
		catch(java.io.FileNotFoundException e){
			System.err.println(e + "Parameter file");
		}catch(java.io.IOException e){
			System.err.println(e + "Aborting program");
			System.exit(-1);
		}
    
    
		/** show the read parameter in the standard output */
		String contents = "-- Parameters echo --- \n";
		contents += "Input Original File: " + original_data +"\n";
		//new BufferedReader(new InputStreamReader(System.in)).readLine();
	}
	
	 private String getParamString(StringTokenizer s)
	 {
         String contenido = "";
         String val = s.nextToken();
         while(s.hasMoreTokens())
             contenido += s.nextToken() + " ";

         return contenido.trim();
     }

     /**obtain the names of the input files from the parameter file  
         @param s is the StringTokenizer */
     private void getInputFiles(StringTokenizer s)
     {
         String val = s.nextToken();

         original_data = s.nextToken().replace('"',  ' ').trim();
         //testFileNameInput = s.nextToken().replace('"',  ' ').trim();
     }


     /** obtain the names of the output files from the parameter file  
         @param s is the StringTokenizer */
     
     
     private int getParamInt(StringTokenizer s){
         String val = s.nextToken();
         val = s.nextToken();
         return Integer.parseInt(val);
     }
     
     /** obtain a float value from the parameter file  
     @param s is the StringTokenizer */
 
     private float getParamFloat(StringTokenizer s)
     {
    	 String val = s.nextToken();
    	 val = s.nextToken();
    	 return Float.parseFloat(val);
     }
 
 
	
}

