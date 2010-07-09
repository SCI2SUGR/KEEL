/**
 * <p>
 * @author Written by Pedro González (University of Jaen) 15/12/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

import org.core.Files;

public class Gene {
    /**
     * <p>
     * This implementation uses integer values to store the genes values (only 0/1).
     * It is used to store DNF rules, so that each variable can get more than one
     * value at a time. Each gene is an array of integer values.
     * </p>
     */
    
    private int num_elem;   // Number of elem in the gene
    private int gen[];      // Gene content - integer  representation, values 0/1


    /**
     * <p>
     * Create new instances of gene
     * </p>
     * @param length      Number of possible values of the variable
     */
    public Gene(int length) {
        num_elem = length;
        // 0..num_elem-1 contents if each value takes part or not
        gen = new int [length+1];
        // num_elem contents if the variable takes part
    }
    

    /**
     * <p>
     * Initialise the variable which does not take part in the rule
     * </p>
     */
    public void NoTakeInitGene() {
        // All the values are 0
        for (int i=0; i<num_elem; i++)
            gen[i] = 0;

        // The variable does not take part
        gen[num_elem] = 0;
        
    }

    
    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos      Position indicates a gene of a variable
     * @return              The value of the gene indicated in posicion
     */
    public int 	getGeneElem(int pos) {
        return gen[pos];
    }
    
    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos      Position indicates a gene of a variable
     * @param value         The value to insert in the gene indicated in posicion
     */
    public void setGeneElem(int pos, int value ) {
        gen[pos] = value;
    }
    
    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return      The lenght of the gene
     */
    public int getGeneLength() {
        return num_elem;
    }
    
    /**
     * <p>
     * Prints the gene
     * </p>
     * @param nFile     Files to write the chromosome
     */
    public void Print(String nFile) {
        String contents;
        contents = "Gene: ";
        for(int i=0; i<num_elem; i++) {
            if (gen[i]==1)
                contents+= "1 ";
            else
                contents+= "0 ";
        }
        contents+= "\n";
        Files.addToFile(nFile, contents);
    }
    
}
