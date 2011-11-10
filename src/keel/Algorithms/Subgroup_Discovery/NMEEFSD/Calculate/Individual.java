/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
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
 * @author Written by Pedro Gonz�lez (University of Jaen) 15/02/2004
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate;

import org.core.Files;

public class Individual {

    /**
     * <p>
     * Defines an individual, composed by a chromosome
     * Includes variables to store the size, the fitness, and if the individual is evaluated or not
     * </p>
     */

    private Chromosome chromosome;  // Individual contents
    private int tamano;            // Number of genes 
    private float fitness;         // Individual Fitness
    private boolean evaluado;      // Individual evaluated or not
    private String clase_obj;      // Value of the selected Class for the target variable -- from param
    private int n_clasObj;         // Number of the label corresponding the target class -- from globals
    private int total_clase;       // Number of examples of the database belonging to the class of this individual
      
    private int num_var;           // Number of variables taking part in the individual
 
    /**
     * <p>
     * Creates new instance of Individual
     * </p>
     * @param lenght      Length of the chromosome for the individual
     * @param datos_v     Contents the type of the variable, and the number of labels.
     */
    public Individual(int lenght, TypeVar datos_v[]) {
      tamano = lenght;
      chromosome = new Chromosome(lenght, datos_v);
      fitness = 0;
      evaluado = false;
    }
 
    /**
     * <p>
     * Empty initialization of the individual. Performed by init_crom method
     * </p>
     */
    public void InitIndEmp () {
      chromosome.InitCromEmp();     // Empty initialization method
      evaluado = false;              // Individual not evaluated
      fitness = 0  ;                 // Current fitness
    }


    /**
     * <p>
     * Returns the size of the Chromosome
     * </p>
     * @return      The size of the Chromosome
     */
    public int getIndivSize () {
        return tamano;
    }
    
    
    /**
     * <p>
     * Returns the hole Chromosome
     * </p>
     * @return      The Chromosome of the individual
     */
    public Chromosome getIndivCrom () {
        return chromosome;
    }
    
    /**
     * <p>
     * Returns the indicated value of the gene of the Chromosome
     * </p>
     * @param pos       Position of the variable in the Chromosome
     * @param elem      Position of the gen of the variable
     * @return          Value of the gen of the variable
     */
    public int getCromElem (int pos, int elem) {
        return chromosome.getCromElem (pos, elem);
    }

    /**
     * <p>
     * Sets the value of the indicated value of the gene of the Chromosome
     * </p>
     * @param pos       Position of the variable in the Chromosome
     * @param elem      Position of the gen of the variable
     */
    public void setCromElem (int pos, int elem, int val) {
        chromosome.setCromElem(pos, elem, val);
    }

    
    /**
     * <p>
     * Returns the number of variables of the rule (including the consequent)
     * </p>
     * @return      Number of variables of the Chromosome
     */
    public int getNumVar () {
        return num_var;
    }

    /**
     * <p>
     * Sets the number of variables of the rule (including the consequent)
     * </p>
     * @param num       Sets the number of variables of the Chromosome
     */
    public void setNumVar (int num) {
        num_var = num;
    }
    
    
    /**
     * <p>
     * Returns the number of the class of the individual
     * </p>
     * @return      Number of the class of the individual
     */
    public int getNumClass () {
        return n_clasObj;
    }

    /**
     * <p>
     * Sets the number of the class of the individual
     * </p>
     * @param val   Sets the number of classes
     */
    public void setNumClass (int val) {
        n_clasObj = val;
    }
    
    /**
     * <p>
     * Returns the string with the name of the class of the individual
     * </p>
     * @return      Name of the class of the individual
     */
    public String getNameClass () {
        return clase_obj;
    }

    /**
     * <p>
     * Sets the value of the name of the class of the individual
     * </p>
     * @param val       Value to introduce in the name of the class
     */
    public void setNameClass (String val) {
        clase_obj = val;
    }
    
    /**
     * <p>
     * Returns the number of the class of the invividual
     * </p>
     * @return      Number of total classes of the individual
     */
    public int getTotalClass () {
        return total_clase;
    }

    /**
     * <p>
     * Sets the number of the class of the individual
     * </p>
     * @param val   Number of total classes of the individual
     */
    public void setTotalClass (int val) {
        total_clase = val;
    }
    

    
    /**
     * <p>
     * Returns if the individual has been evaluated
     * </p>
     * @return      If the individual has been evaluated
     */
    public boolean getIndivEvaluated () {
        return evaluado;
    }

    /**
     * <p>
     * Sets state of the individual
     * </p>
     * @param val       Boolean value with the state of evaluated
     */
    public void setIndivEvaluated (boolean val) {
        evaluado = val;
    }

    /**
     * <p>
     * Returns the fitness of the individual
     * </p>
     * @return      Value of the fitness of the individual
     */
    public float getIndivPerf () {
        return fitness;
    }
    
    /**
     * <p>
     * Sets the fitness of the individual 
     * </p>
     * @param perf      Value to introduce in the fitnees of the individual
     */
    public void setIndivPerf (float perf) {
        fitness = perf;
    }

    
    /**
     * <p>
     * Returns if the indicated individual is equal to another individual
     * </p>
     * @param a      Individual to compare
     * @return          If both individuals are equal or not
     */
    public boolean EqualTo (Individual a) {
        boolean equals= true;
        int number;

        for (int i=0;i<this.tamano;i++) {
            number = this.getIndivCrom().getCromGeneLength(i);
            for (int j=0;j<number;j++) {
                if (this.getCromElem(i,j) != a.getCromElem(i,j)) {
                    equals = false;
                    break;
                }
            } 
        }
    
        return equals;
    }
    
   /**
    * <p>
    * Counts the number of examples of the DataSet belonging to the number of the class indicated
    * </p>
    * @param num_class      The number of the class
    * @return               The number of examples for the class "num_class"
    **/ 
    private int ExamplesClass (int num_class) {
        int num=0;  // Initialize to 0
        for (int i=0; i<StCalculate.n_eje; i++) {
            /* If the example class is the target class, increase the number of examples of the target class */
            if (StCalculate.tabla[i].clase == num_class)
                num++;
        }
        return num;
    }
    
    /**
     * <p>
     * Return the number of interval of a value in a variable
     * </p>
     */
    private int NumInterval (float valor, int num_var) {

        float pertenencia, new_pert;
        int interv;
        interv = -1;
        pertenencia = new_pert = 0;
        for (int i=0; i<StCalculate.var[num_var].n_etiq;i++) {
            new_pert = StCalculate.BaseDatos[num_var][i].Fuzzy (valor);
            if (new_pert>pertenencia) {
                interv = i;
                pertenencia = new_pert;
            }
        }
        return interv;        
    }
    
    
    
    /**
     * <p>
     * Evaluate the individual
     * </p>
     * @param GI            Gain Info array
     * @param total_class   Total number of examples of the class
     * @return              An Result object
     */
    public Result CalcInd (float[] GI, int total_class) {
        
        int ejCompAntFuzzy=0;                // Number of compatible examples with the antecedent of any class - fuzzy version --- unused
        int ejCompAntCrisp=0;                // Number of compatible examples with the antecedent of any class - crisp version
        int ejCompAntClassFuzzy=0;           // Number of compatible examples (antecedent and class) - fuzzy version
        int ejCompAntClassCrisp=0;           // Number of compatible examples (antecedent and class) - crisp version
        int ejCompAntClassNewFuzzy=0;        // Number of new covered compatible examples (antec and class) - fuzzy version
        int ejCompAntClassNewCrisp=0;        // Number of new covered compatible examples (antec and class) - crisp version

        float gradoCompAntFuzzy=0;           // Total compatibility degree with the antecedent - fuzzy version
        float gradoCompAntClassFuzzy=0;      // Total compatibility degree with antecedent and class - fuzzy version

        float disparoFuzzy;    // Final compatibility degree of the example with the individual - fuzzy version
        float disparoCrisp;    // Final compatibility degree of the example with the individual - crisp version
        
        float pertenencia, pert, completitud, fsupport, csupport, confianza, cconfianza, interes;
        float accuracy, coverage, significance, unusualness;

        Result res = new Result();
        
        int cubreClase[] = new int[StCalculate.n_clases];
        int ejClase[] = new int[StCalculate.n_clases];
        for (int i=0; i<StCalculate.n_clases; i++) {
            cubreClase[i]=0;
            ejClase[i] = ExamplesClass(i);
        }

        int num_var_no_interv=0;   // Number of variables that don't take part in the rule
        
        for (int i=0; i<StCalculate.n_eje; i++) { // For each example of the dataset
            disparoFuzzy = 1;
            disparoCrisp = 1;
            num_var_no_interv = 0;

            // Compute all chromosome values
            for (int j=0; j<StCalculate.num_vars; j++) {
                if (!StCalculate.var[j].continua) {  
                    // Discrete Variable
                    if (chromosome.getCromElem(j,StCalculate.var[j].n_etiq)==1){
                        // Variable j takes part in the rule
                        if ((chromosome.getCromElem(j,(int)StCalculate.tabla[i].ejemplo[j])==0) && (!Calculate.getLost(i,j))) {
                            disparoFuzzy = 0;
                            disparoCrisp =0;
                        }
                    }
                    else
                        num_var_no_interv++;  // Variable does not take part
                }
                else {	
                    // Continuous variable
                    if (chromosome.getCromElem(j, StCalculate.var[j].n_etiq)==1){
                        // Variable takes part in the rule
                        // Fuzzy computation
                        if (!Calculate.getLost(i,j)) {
                            pertenencia = 0;
                            for (int k=0; k<StCalculate.var[j].n_etiq; k++) {
                                if (chromosome.getCromElem(j,k)==1)
                                    pert = StCalculate.BaseDatos[j][k].Fuzzy(StCalculate.tabla[i].ejemplo[j]);
                                else pert = 0;
                                pertenencia = Utils.Maximum (pertenencia, pert);
                            }
                            disparoFuzzy = Utils.Minimum (disparoFuzzy, pertenencia);
                        }
                        // Crisp computation
                        if (!Calculate.getLost(i,j))
                            if (chromosome.getCromElem(j, NumInterval (StCalculate.tabla[i].ejemplo[j], j))==0)
                                disparoCrisp = 0;
                    }
                    else
                        num_var_no_interv++;  // Variable does not take part
                }
            }

            // Update globals counters
            gradoCompAntFuzzy += disparoFuzzy;
            if (disparoFuzzy>0) {
                ejCompAntFuzzy++;
                if (StCalculate.tabla[i].clase == this.n_clasObj) {
                    gradoCompAntClassFuzzy +=disparoFuzzy;
                    ejCompAntClassFuzzy ++;
                }
                if ((!StCalculate.tabla[i].fcubierto) &&  (StCalculate.tabla[i].clase == this.n_clasObj)) {
                    // If example not covered and belongs to the target class
                    ejCompAntClassNewFuzzy++;       // Increments the number of covered examples
                    StCalculate.tabla[i].fcubierto = true;  // Marks example
                }
            }
            if (disparoCrisp>0) {
                ejCompAntCrisp++;
                if (StCalculate.tabla[i].clase == this.n_clasObj) {
                    ejCompAntClassCrisp ++;
                }
                cubreClase[StCalculate.tabla[i].clase]++;
                if ((!StCalculate.tabla[i].ccubierto) &&  (StCalculate.tabla[i].clase == /*StCalculate*/this.n_clasObj)) {
                    ejCompAntClassNewCrisp++;
                    StCalculate.tabla[i].ccubierto = true;  // Marks example
                }
            }

        } // End of cycle for each example

        // Compute the measures
        
        // Compute Completitud
        // Examples of the rule->class / examples of the class
        if (total_class != 0)
            completitud = ((float)ejCompAntClassFuzzy/total_class);
        else
            completitud = 0;
                
        csupport = ((float)ejCompAntClassCrisp/StCalculate.n_eje);
        
        fsupport = ((float)gradoCompAntClassFuzzy/StCalculate.n_eje);

        if (gradoCompAntFuzzy != 0)
            confianza = (float)gradoCompAntClassFuzzy/gradoCompAntFuzzy;  
        else
            confianza = 0;
        
        if (ejCompAntCrisp != 0)
            cconfianza = (float)ejCompAntClassCrisp/ejCompAntCrisp;
        else
            cconfianza = 0;
        
        accuracy = (float)(ejCompAntClassCrisp+1) / (ejCompAntCrisp + StCalculate.n_clases);

        coverage = ((float)ejCompAntCrisp/StCalculate.n_eje);
      
        float sumaSignClase=0;
        for (int i=0; i<StCalculate.n_clases; i++) {
            if (cubreClase[i]!=0) {  // If 0 don�t add
                sumaSignClase += cubreClase[i] * Math.log10((float)cubreClase[i]/(ejClase[i]*coverage));
            }
        }
        significance = 2 * sumaSignClase;

        if (ejCompAntCrisp==0)
            unusualness = 0;
        else
            unusualness =  coverage * ( (float)ejCompAntClassCrisp/ejCompAntCrisp - (float)total_class/StCalculate.n_eje);

        if (num_var_no_interv >= StCalculate.num_vars) {
            completitud = fsupport = csupport = confianza = cconfianza = interes = 0;
            accuracy = coverage = significance = unusualness = 0;
        }
            
        res.perf = -1;
        res.comp = completitud; 
        res.csup = csupport;
        res.fsup = fsupport;
        res.fconf = confianza;
        res.cconf = cconfianza;
        res.accu = accuracy;
        res.cov = coverage;
        res.sign = significance;
        res.unus = unusualness;

        return (res);
    }

    
    /**
     * <p>
     * Method to Print the contents of the individual
     * </p>
     * @param nFile     Fichero to write the individual
     */
    public void Print(String nFile) {
        String contents;
        chromosome.Print(nFile);
        contents = "Fitness "+fitness + "\n";
        contents+= "Evaluated? " + evaluado + "\n";
        Files.addToFile(nFile, contents);
    }
   
    
}
