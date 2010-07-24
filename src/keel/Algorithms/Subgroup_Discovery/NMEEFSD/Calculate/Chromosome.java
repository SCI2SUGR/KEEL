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
 * @author Written by Pedro González (University of Jaen) 18/02/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate;

import org.core.Files;

public class Chromosome {
    /**
     * <p>
     * Defines the structure and manage the contents of a rule
     * This implementation uses only integer values to store the gens.
     * So, variables values must be discretized (if they are continuous)
     * or translated into integers (if they are enumerated)
     * Uses Keel defined Random
     * </p>
     */
    private int num_genes;          // Number of genes
      private Gene cromosoma [];   // Individual content - integer representation
      
    /**
     * <p>
     * Creates new instance of chromosome, no initialization 
     * </p>
     * @param datos_var   Contents the type of the variable, and the number of labels.
     * @param length      Length of the chromosome
     */
    public Chromosome(int length, TypeVar datos_var[]) {
      num_genes = length;
      cromosoma = new Gene[length];
      for (int i=0; i<num_genes; i++)
          cromosoma[i] = new Gene(datos_var[i].n_etiq);
    }

    /**
     * <p>
     * Empty initialization of an existing chromosome
     * We denote that the variable does not take part by setting 
     * the value to n_etiq (valid class are from 0 to n_etiq-1)
     * </p>
     */
    public void InitCromEmp() {
        for (int i=0; i<num_genes; i++) 
            cromosoma[i].NoTakeInitGene();
    }

    /**
     * <p>
     * Retuns the value of the position of the gene indicated
     * </p>
     * @param pos      The position indicates the variable
     * @param elem          The position of the gene of the variable
     * @return              The value of the gene
     */
    public int getCromElem (int pos, int elem) {
        return cromosoma[pos].getGeneElem(elem);
    }

    /**
     * <p>
     * Sets the value of the indicated position of the gene of the chromosome
     * </p>
     * @param pos      The position indicates the variable
     * @param elem          The position of the gene of the variable
     * @param value         The value to insert in the gene
     */
    public void setCromElem (int pos, int elem, int value ) {
      cromosoma[pos].setGeneElem(elem, value);
    }

    
    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return      The gene lenght of the chromosome
     */
    public int getCromLength () {
      return num_genes;
    }
   
    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @param pos    The position indicates the variable
     * @return            The gene lenght of the chromosome
     */
    public int getCromGeneLength (int pos) {
      return cromosoma[pos].getGeneLength();
    }
   
    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile     Fichero to write the chromosome
     */
    public void Print(String nFile) {
        String contents;
        contents = "Chromosome: ";
        for(int i=0; i<num_genes; i++)
            contents+= cromosoma[i] + " ";
        contents+= "\n";
        if (nFile=="") 
            System.out.print (contents);
        else 
           Files.addToFile(nFile, contents);
    }
    
}
