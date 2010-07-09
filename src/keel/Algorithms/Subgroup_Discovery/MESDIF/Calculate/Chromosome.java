/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 18/02/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

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
    private int num_genes;        // Number of genes
      private Gene cromosoma [];   // Individual content - integer representation
      
    /**
     * <p>
     * Creates new instance of chromosome, no initialization 
     * </p>
     * @param data_var   Contents the type of the variable, and the number of labels.
     * @param length      Length of the chromosome
     */
    public Chromosome(int length, TypeVar data_var[]) {
      num_genes = length;
      cromosoma = new Gene[length];
      for (int i=0; i<num_genes; i++)
          cromosoma[i] = new Gene(data_var[i].n_etiq);
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
     * @param nFile     Files to write the chromosome
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
