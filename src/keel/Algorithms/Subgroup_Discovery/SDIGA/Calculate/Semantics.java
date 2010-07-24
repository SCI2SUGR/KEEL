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

/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.Calculate;

import org.core.Files;


public class Semantics {

    /**
     * <p>
     * This class is defined to manage de semantics of the linguistic variables
     * </p>
     */

    /**
     * <p>
     * This method generates the semantics of the linguistic variables
     * using a partition consisting of triangle simetrics fuzzy sets.
     * The cut points al stored at 0.5 level of the fuzzy sets to be
     * considered in the computation of the gain of information 
     * </p>
     */
    public static void Initialise () {
        int v, etq;
        float marca, valor, p_corte;

        for (v=0; v<StCalculate.num_vars; v++) {
           if (StCalculate.var[v].continua==true) {
               marca=(StCalculate.var[v].max-StCalculate.var[v].min)/((float)(StCalculate.var[v].n_etiq-1));
               p_corte = StCalculate.var[v].min + marca / 2;
               for (etq=0; etq<StCalculate.var[v].n_etiq; etq++) {
                   StCalculate.intervalos[v][etq] = p_corte;
                   valor=StCalculate.var[v].min+marca*(etq-1);
                   StCalculate.BaseDatos[v][etq].x0=Utils.Assigned(valor,StCalculate.var[v].max);
                   valor=StCalculate.var[v].min+marca*etq;
                   StCalculate.BaseDatos[v][etq].x1=Utils.Assigned(valor,StCalculate.var[v].max);
                   valor=StCalculate.var[v].min+marca*(etq+1);
                   StCalculate.BaseDatos[v][etq].x3=Utils.Assigned(valor,StCalculate.var[v].max);
                   StCalculate.BaseDatos[v][etq].y=1;
                   p_corte += marca;
               }
           }
        }
    }

    
    /**
     * <p>
     * This method writes the semantics of the linguistic variables at the file specified
     * </p>
     * @param nFile       Files to write Semantics
     **/
    public static void Write (String nFile){
        int i, j;
        String contents;
        contents = "\n\n";
        contents+= "--------------------------------------------\n";
        contents+= "|  Semantics for the continuous variables  |\n";
        contents+= "--------------------------------------------\n";
        
        for (i=0; i<StCalculate.num_vars; i++) {
            if (StCalculate.var[i].continua==true) {
                contents+= "Fuzzy sets parameters for variable " + i + "\n";
                for (j=0; j<StCalculate.var[i].n_etiq; j++) {
                    contents+= "\tEtq " + j + ": " +  StCalculate.BaseDatos[i][j].x0 + " " +  StCalculate.BaseDatos[i][j].x1 + " " +  StCalculate.BaseDatos[i][j].x3 + "\n";
                }
                contents+= "\tPoints for the computation of the info gain: ";
                for (j=0; j<StCalculate.var[i].n_etiq; j++)
                    contents += StCalculate.intervalos[i][j] + "  ";
                contents+= "\n";
            }
        }
        contents+= "\n";
        Files.addToFile(nFile, contents);
    }

}
