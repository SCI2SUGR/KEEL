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
 * @author Writed by Pedro González (University of Jaen) 15/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.SDIGA;

import org.core.*;

public class Gene {
    /**
     * <p>
     * This implementation uses boolean values to store the genes values
     * It is used to store DNF rules, so that each variable can can get more than one value at a time
     * Each gene is an array of boolean values, false indicates that the value is not present,
     * true indicates that the value is present
     * </p>
     */

    private int num_elem;   // Number of elem in the gene
    private int gen[];      // Gene content - integer  representation, values 0/1
      
    /**
     * <p>
     * Creates new instance of gene
     * </p>
     * @param length          Number of posibles values for the variable
     */
    public Gene(int length) {
        num_elem = length;
        gen = new int [length+1];
    }

    /**
     * <p>
     * Random initialization of an existing gene
     * </p>
     */
    public void RndInitGene() {
        double aux;
        int interv=0;
        
        for (int i=0; i<num_elem; i++) {
            aux = Randomize.Rand();
            // Rand returns a random doble from 0 to 1, including 0 but excluding 1
            if (aux<0.5) 
                gen[i] = 0;
            else {
                gen[i] = 1;
                // Counts the number of 1 of the variable
                interv++;
            }
        }
        // If number of 1 equals 0 or num of values, the variable does not take part
        if (interv==0 || interv==num_elem) 
            gen[num_elem] = 0;
        else
            gen[num_elem] = 1;
    }
    
    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos          Position of the gene
     * @return                  Value of the gene
     */
    public int getGeneElem (int pos) {
      return gen[pos];
    }
    
    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos          Position of the gene
     * @param value             Value of the gene
     */
    public void setGeneElem (int pos, int value ) {
      gen[pos] = value;
    }
    
    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return              Lenght of the gene
     */
    public int getGeneLenght () {
      return num_elem;
    }
   
    /**
     * <p>
     * Prints the gene
     * </p>
     * @param nFile         Name of the file to write the gene
     */
    public void print(String nFile) {
        String contents;
        contents = "Gene: ";
        for(int i=0; i<num_elem; i++)
            contents+= gen[i] + " ";
        contents+= "(" + gen[num_elem] + ")\n";
        Files.addToFile(nFile, contents);
    }

    /**
     * <p>
     * Gets the genes of the variable
     * </p>
     * @return              The genes of the variable in a string
     */
    public String print() {
        String contents;
        contents = "Gene: ";
        for(int i=0; i<num_elem; i++)
            contents+= gen[i] + " ";
        contents+= "(" + gen[num_elem] + ")\n";
        return contents;
    }
    
    
}
