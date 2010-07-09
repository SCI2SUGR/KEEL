/**
 * <p>
 * @author Created by Pedro González (University of Jaen) 18/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 20/04/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package SDIGA;

import org.core.*;

public class CromCAN {
    /**
     * <p>
     * Defines the structure and manage the contents of a canonical rule.
     * This implementation uses only integer values to store the gens.
     * </p>
     */

      private int num_genes;      // Number of genes
      private int chromosome [];   // Individual content - integer representation
      
    /**
     * <p>
     * Creates new instance of chromosome, no initialization
     * </p>
     * @param length          Length of the chromosome
     */
    public CromCAN(int length) {
      num_genes = length;
      chromosome = new int [length];
    }

    /**
     * <p>
     * Random initialization of an existing chromosome
     * </p>
     * @param Variables     contents the characteristics of the variables
     */
    public void initCrom(TableVar Variables) {
        for (int i=0; i<num_genes; i++) 
            chromosome[i] = Randomize.Randint (0, Variables.getNLabelVar(i));
    }
    
    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos          Position of the variable in the chromosome
     * @return                  Value of the variable
     */
    public int getCromElem (int pos) {
      return chromosome[pos];
    }
    
    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos          Position of the variable in the chromosome
     * @param value             Value of the variable
     */
    public void setCromElem (int pos, int value ) {
      chromosome[pos] = value;
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
     * @param nFile         File to write the chromosome
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
    
}
