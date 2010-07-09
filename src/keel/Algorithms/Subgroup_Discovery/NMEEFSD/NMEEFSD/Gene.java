/**
 * <p>
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package NMEEFSD;

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

    private int num_elem;       // Number of elem in the gene
    private boolean gen[];      // Gene content - boolean representation

    /**
     * <p>
     * Creates new instance of gene
     * </p>
     * @param lenght          Number of posibles values for the variable
     */
    public Gene(int lenght) {
        num_elem = lenght;
        gen = new boolean [lenght+1];
    }

    /**
     * <p>
     * Random initialization of an existing gene
     * </p>
     */
    public void RndInitGene() {
        double aux;
        int interv=0;

        for (int i=0; i<num_elem; i++) {  // Gene num_elem
            aux = Randomize.Randdouble(0.0,1.0);
            // Rand returns a random doble from 0 to 1, including 0 but excluding 1
            if (aux<0.5) {
                gen[i] = false;
            }
            else {
                gen[i] = true;
                interv++;  // Counts the number of 1 of the variable
            }
        }
        // If number of 1 equals 0 or num of values, the variable does not take part
        if (interv==0 || interv==num_elem) {
            gen[num_elem] = false;
        }
        else {
            gen[num_elem] = true;
        }
    }


    /** 
     * <p>
     * Non-intervene Initialization of an existing gene
     * </p>
     */
    public void NoTakeInitGene() {

        // All the values are 0
        for (int i=0; i<num_elem; i++)
            gen[i] = false;

        // The variable does not take part, so mark with 0 the element "num_elem"
        gen[num_elem] = false;

    }


    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos          Position of the gene
     * @return                  Value of the gene
     */
    public boolean getGeneElem(int pos) {
        return gen[pos];
    }

    /**
     * <p>
     * Sets the value of the indicated gene of the chromosome
     * </p>
     * @param pos          Position of the gene
     * @param value             Value of the gene
     */
    public void setGeneElem(int pos, boolean value ) {
        gen[pos] = value;
    }

    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return              Lenght of the gene
     */
    public int getGeneLenght() {
        return num_elem;
    }

    /**
     * <p>
     * Prints the gene
     * </p>
     * @param nFile         Name of the file to write the gene
     */
    public void Print(String nFile) {
        String contents;
        contents = "Gene: ";
        for(int i=0; i<num_elem; i++) {
            if (gen[i]==true)
                contents+= "1 ";
            else
                contents+= "0 ";
        }
        contents+= "\n";
        Files.addToFile(nFile, contents);
    }

}
