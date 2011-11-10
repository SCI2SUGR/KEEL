/**
 * <p>
 * @author Created by Pedro González (University of Jaen) 18/02/2004
 * @author Modified by Pedro González (University of Jaen) 4/08/2007
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 30/06/2010
 * @version 2.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.MESDIF;

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
    public void initCromRnd () {
        for (int i=0; i<num_genes; i++)
            chromosome[i].InitGeneRnd();
    }


    /**
     * <p>
     * Biased Random initialization of an existing chromosome
     * The random inicializacion is biased by generating
     *    chromosomes with a maximum number or participating variables
     *    and for an indicated % of the population (the rest is random)
     * </p>
     * @param porcVar
     */
    public void initCromBsd (float porcVar) {
        int num_var;

        /* Array of integers to show if each chromosome is initialized */
        int  crom_inic[]= new int[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = 0;

        /* First, obtain the number of variables to appear in the chromosome */
        int numInterv = Randomize.Randint (1, Math.round(porcVar*num_genes));

        /* Initialize numInterv variables to take part in the individual */
        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            /* If not initialized, initialize and increase the count */
            if (crom_inic[num_var]==0) {
                chromosome[num_var].InitGeneRnd();
                crom_inic[num_var]=1;
                var++;
            }
        }

        /* Initialize the rest of the variables to non-intervene */
        for (int i=0; i<num_genes; i++)  {
            if (crom_inic[i]==0)
                chromosome[i].noTakeInitGene();
        }

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
     * Retuns the length of the chromosome
     * </p>
     * @return                  Length of the chromosome
     */
    public int getCromLength () {
      return num_genes;
    }


    /**
     * Retuns the gene lenght of the chromosome
     */
    public int getCromGeneLength (int pos) {
      return chromosome[pos].getGeneLength();
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
