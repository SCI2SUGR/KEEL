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
 * @author Created by Pedro González (University of Jaen) 18/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDIGA.SDIGA;

import org.core.*;


public class CromDNF {
    /**
     * <p>
     * Defines the structure and manage the contents of a DNF rule.
     * This implementation uses only binary values to store the possible values of the variable.
     * </p>
     */
      private int num_genes;      // Number of genes
      private Gene chromosome [];   // Individual content - integer representation

    /**
     * <p>
     * Creates new instance of chromosome, no initialization
     * </p>
     * @param length          Length of the chromosome
     */
    public CromDNF(int length, TableVar Variables) {
      num_genes = length;
      chromosome = new Gene [length];
      for (int i=0; i<num_genes; i++)
          chromosome[i] = new Gene(Variables.getNLabelVar(i));

    }

    /**
     * <p>
     * Random initialization of an existing chromosome
     * </p>
     */
    public void initCrom() {
        for (int i=0; i<num_genes; i++)
            chromosome[i].RndInitGene();
    }

    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos          Position of the variable in the chromosome
     * @param elem              Position of the gene of the variable
     * @return                  Value of the gene
     */
    public int getCromElemGene (int pos, int elem) {
        return chromosome[pos].getGeneElem(elem);
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos          Position of the variable in the chromosome
     * @param elem              Position of the gene of the variable
     * @param value             Value of the gene
     */
    public void setCromElemGene (int pos, int elem, int value ) {
      chromosome[pos].setGeneElem(elem, value);
    }

    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return                  Length of the chromosome
     */
    public int getCromLenght () {
      return num_genes;
    }

    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile             Files to write the chromosome
     */
    public void print(String nFile) {
        String contents;
        contents = "Chromosome: ";
        for(int i=0; i<num_genes; i++)
            contents+= chromosome[i] + " ";
        contents+= "\n";
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }

    /**
     * <p>
     * Gets the chromosome genes
     * </p>
     * @return              The chromosome in a string
     */
    public String print() {
        String contents;
        contents = "Chromosome: \n";
        for(int i=0; i<num_genes; i++)
            contents+= chromosome[i].print();
        contents+= "\n";
        return contents;
    }

}
