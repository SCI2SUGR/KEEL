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
 * @author Written by Cristóbal J. Carmona (University of Jaen) 11/08/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

import org.core.*;

public class CromDNF {
     /**
      * Defines the structure and manage the contents of a rule
      * This implementation uses disjunctive formal norm to store the gens.
      * So, variables are codified in binary genes
      */

      private int num_genes;      // Number of genes
      private Gene cromosoma [];   // Individual content - integer representation

    /**
     * <p>
     * Creates new instance of chromosome, no initialization
     * </p>
     * @param lenght      Length of the chromosome
     * @param Variables     Structure of variables of the dataset
     */
    public CromDNF(int lenght, TableVar Variables) {
      num_genes = lenght;
      cromosoma = new Gene[lenght];
      for(int i=0; i<num_genes; i++){
        cromosoma[i] = new Gene(Variables.getNLabelVar(i));
      }
    }


    /**
     * <p>
     * Random initialization of an existing chromosome
     * </p>
     */
    public void RndInitCrom( ) {

        for (int i=0; i<num_genes; i++)
            cromosoma[i].RndInitGene();

    }


    /**
     * <p>
     * Biased Random initialization of an existing chromosome
     * </p>
     * @param Variables 	Contents the type of the variable, and the number of labels.
     * @param porcVar           Participating variables in the chromosom
     */
    public void BsdInitCrom(TableVar Variables, float porcVar) {

        int num_var;

        // This array indicates if every chromosome has been initialised
        boolean crom_inic[]= new boolean[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = false;

        // Firtly, we obtain the numbero of variable which are in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcVar*Variables.getNVars()));

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                cromosoma[num_var].RndInitGene();
                crom_inic[num_var]=true;
                var++;
            }
        }

    }


    /**
     * <p>
     * Initialization based on coverage
     * </p>
     * @param pop               Main population
     * @param Variables		Contents the type of the variable, and the number of labels.
     * @param Examples          Dataset
     * @param porcCob           Percentage of participating variables
     * @param nobj        Number of objectives of the algorithm
     */
    public void CobInitCrom(Population pop, TableVar Variables, TableDat Examples, float porcCob, int nobj) {

        int num_var;

        boolean crom_inic[] = new boolean[num_genes];
        for (int i=0; i<num_genes; i++)
           crom_inic[i] = false;

        // Number of participating variables in the chromosome
        int numInterv = Randomize.Randint (1, Math.round(porcCob*Variables.getNVars()));

        boolean centi = false;
        int aleatorio = 0;
        int ii=0;
        while((!centi)&&(ii<Examples.getNEx())){
            aleatorio = Randomize.Randint(0, Examples.getNEx()-1);
            if((pop.ej_cubiertos[aleatorio]==false)&&(Examples.getClass(aleatorio)==Variables.getNumClassObj()))
                centi = true;
            ii++;
        }

        int var=0;
        while (var<numInterv) {
            num_var = Randomize.Randint (0, num_genes-1);
            // If the variable is not in the chromosome
            if (crom_inic[num_var]==false) {
                if (Variables.getContinuous(num_var)) { //Continuous variable
                    // Put in the correspondent interval //
                    float pertenencia=0, new_pert=0;
                    int interv = Variables.getNLabelVar(num_var);
                    for (int i=0; i < Variables.getNLabelVar(num_var); i++) {
                        new_pert = Variables.Fuzzy(num_var, i, (int) Examples.getDat(aleatorio, num_var));
                        if (new_pert>pertenencia) {
                            interv = i;
                            pertenencia = new_pert;
                        }
                    }
                    int number = Variables.getNLabelVar(num_var);
                    for(int l=0; l<=number; l++){
                        if(l!=num_var){
                            this.setCromGeneElem(num_var, l, false);
                        }
                    }

                    this.setCromGeneElem(num_var, interv, true);
                    this.setCromGeneElem(num_var, number, true);
                } else { //Discrete variable
                    // Put in the correspondent value //
                    int number = Variables.getNLabelVar(num_var);
                    for(int l=0; l<=number; l++){
                        if(l!=num_var){
                            this.setCromGeneElem(num_var, l, false);
                        }
                    }
                    this.setCromGeneElem(num_var, (int) Examples.getDat(aleatorio, num_var), true);
                    this.setCromGeneElem(num_var, number, true);
                }
                crom_inic[num_var]=true;
                var++;
            }
        }

        // Initialise the rest variables
        for (int i=0; i<num_genes; i++)  {
            if (crom_inic[i]==false) {
                int number = Variables.getNLabelVar(i);
                for(int l=0; l<=number; l++){
                    this.setCromGeneElem(i, l, false);
                }
            }
        }
    }

    /**
     * <p>
     * Retuns the lenght of the chromosome
     * </p>
     * @return          Lenght of the chromosome
     */
    public int getCromLenght () {
      return num_genes;
    }


    /**
     * <p>
     * Retuns the gene lenght of the chromosome
     * </p>
     * @return          Lenght of the gene
     */
    public int getCromGeneLenght (int pos) {
      return cromosoma[pos].getGeneLenght();
    }


    /**
     * <p>
     * Retuns the value of the gene indicated
     * </p>
     * @param pos      Position of the variable
     * @param elem          Position of the gene
     */
    public boolean getCromGeneElem (int pos, int elem) {
      return cromosoma[pos].getGeneElem(elem);
    }


   /**
    * <p>
    * Sets the value of the indicated gene of the Chromosome
    * </p>
    * @param pos            Position of the variable
    * @param elem           Position of the gene
    * @param val            Value to insert
    */
    public void setCromGeneElem (int pos, int elem, boolean val) {
        cromosoma[pos].setGeneElem(elem, val);
    }

    /**
     * <p>
     * Prints the chromosome genes
     * </p>
     * @param nFile         Fichero to write the chromosome
     */
    public void Print(String nFile) {
        String contents;
        contents = "Chromosome: \n";
        for(int i=0; i<num_genes; i++){
            contents += "Var "+i+": ";
            int neti = getCromGeneLenght(i);
            for(int l=0; l<=neti; l++){
                contents += this.getCromGeneElem(i, l) + " ";
            }
            contents+="\n";
        }
        if (nFile=="")
            System.out.print (contents);
        else
           Files.addToFile(nFile, contents);
    }

}
