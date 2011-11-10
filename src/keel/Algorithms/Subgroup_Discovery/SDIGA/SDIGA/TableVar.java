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

import keel.Dataset.*;
import org.core.*;

public class TableVar {
    /**
     * <p>
     * Class defined to store the information of the variable of the dataset
     * </p>
     */

    private int num_vars;           // Number of variables
    private int n_etiq;             // Number of labels for the continuous variables
    private int MaxEtiquetas;       // Max number of labels for all of the cont variables
    private int MaxValores;         // Max number of values

    private int n_clases;           // Number of classes of the target variable
    private String clase_obj;       // Name of the target class
    private int nclase_obj;         // Number of the target class selected

    private TypeVar[] var;          // Variables characteristics (type, min-max values)
    private Fuzzy[][] BaseDatos;   // Definitions for the fuzzy sets

    private float[] GI;             // Variable Information Gain
    private float[][] intervalosGI; // Aux array for the info gain

    /**
     * <p>
     * Returns the number of variables
     * </p>
     * @return      Number of variables
     */
    public int getNVars () {
        return num_vars;
    }


    /**
     * <p>
     * Returns the number of labels for all the continuous variables
     * </p>
     * @return      Number of labels for continuous variables
     */
    public int getNLabel () {
        return n_etiq;
    }

    /**
     * <p>
     * Sets the number of labels for all the continuous variables
     * </p>
     * @param val       Number of linguistic labels
     */
    public void setNLabel (int val) {
        n_etiq = val;
    }

    /**
     * <p>
     * Returns the maximum number of labels of all the variables
     * </p>
     * @return      The maximum number of labels of all variables
     */
    public int getMaxLabel () {
        return MaxEtiquetas;
    }


    /**
     * <p>
     * Returns the maximum number of values of all the variables
     * </p>
     * @return      Maximum value of all variables
     */
    public int getMaxVal () {
        return MaxValores;
    }


    /**
     * <p>
     * Returns the number of classes of the target variable
     * </p>
     * @return      Number of values for the target variable
     */
    public int getNClass () {
        return n_clases;
    }

    /**
     * <p>
     * Sets the number of classes of the target variable
     * </p>
     * @param val     Number of classes
     */
    public void setNClass (int val) {
        n_clases = val;
    }


    /**
     * <p>
     * Returns the number of the class of the target variable
     * </p>
     * @return      Number of the class of the target variable
     */
    public int getNumClassObj () {
        return nclase_obj;
    }

    /**
     * <p>
     * Sets the number of the class of the target variable
     * </p>
     * @param clas         Number of the class of the target variable
     */
    public void setNumClassObj (int clas) {
        nclase_obj = clas;
    }


    /**
     * <p>
     * Returns the name of the class of the target variable
     * </p>
     * @return              Name of the class
     */
    public String getNameClassObj () {
        return clase_obj;
    }

    /**
     * <p>
     * Sets the name of the class of the target variable
     * </p>
     * @param clas         Name of the target variable
     */
    public void setNameClassObj (String clas) {
        clase_obj = clas;
    }


    /**
     * <p>
     * Return the belonging of a value
     * </p>
     * @param i         Position of the variable
     * @param j         Position of the value
     * @param X         Value
     * @return          Belonging grade of the value
     */
    public float Fuzzy (int i, int j, float X) {
        return BaseDatos[i][j].Fuzzy(X);
    }


    /**
     * <p>
     *  Method to return the value of the cut points X0
     * </p>
     * @param i         Position of the variable
     * @param j         Position of the value
     * @return          Value of the cut points X0
     */
    public float getX0 (int i, int j) {
        return BaseDatos[i][j].getX0();
    }

    /**
     * <p>
     *  Method to return the value of the cut points X1
     * </p>
     * @param i         Position of the variable
     * @param j         Position of the value
     * @return          Value of the cut points X1
     */
    public float getX1 (int i, int j) {
        return BaseDatos[i][j].getX1();
    }

    /**
     * <p>
     *  Method to return the value of the cut points X3
     * </p>
     * @param i         Position of the variable
     * @param j         Position of the value
     * @return          Value of the cut points X3
     */
    public float getX3 (int i, int j) {
        return BaseDatos[i][j].getX3();
    }


    /**
     * <p>
     * Rounds the generated value for the semantics when necesary
     * </p>
     * @param val       The value to round
     * @param tope
     * @return the rounded value
     */
    public float Round (float val, float tope) {
        if (val>-0.0001 && val<0.0001)
            return (0);
        if (val>tope-0.0001 && val<tope+0.0001)
            return (tope);
        return (val);
    }


    /**
     * <p>
     * Defined to manage de semantics of the linguistic variables
     * Generates the semantics of the linguistic variables using a partition
     * consisting of triangle simetrics fuzzy sets. The cut points al stored
     * at 0.5 level of the fuzzy sets to be considered in the computation of
     * the gain of information. Also writes the semantics of the linguistic
     * variables in the specified file
     * </p>
     * @param nFile       Name of file to write the semantics
     **/
    public void InitSemantics (String nFile) {
        int v, etq;
        float marca, valor, p_corte;
        float auxX0, auxX1, auxX3, auxY;
        String contents;

        contents = "\n--------------------------------------------\n";
        contents+=   "|  Semantics for the continuous variables  |\n";
        contents+=   "--------------------------------------------\n";

        for (v=0; v<num_vars; v++) {
           if (var[v].getContinuous()==true) {
               marca=(var[v].getMax()-var[v].getMin())/((float)(var[v].getNLabels()-1));
               p_corte = var[v].getMin() + marca / 2;
               contents+= "Fuzzy sets parameters for variable " + var[v].getName() + ":\n";
               for (etq=0; etq<var[v].getNLabels(); etq++) {
                   valor=var[v].getMin()+marca*(etq-1);
                   auxX0 = Round (valor,var[v].getMax());
                   valor=var[v].getMin()+marca*etq;
                   auxX1 = Round (valor,var[v].getMax());
                   valor=var[v].getMin()+marca*(etq+1);
                   auxX3 = Round (valor,var[v].getMax());
                   auxY = 1;
                   BaseDatos[v][etq].setVal(auxX0, auxX1, auxX3, auxY);
                   p_corte += marca;
                   contents+= "\tLabel " + etq + ": " +  BaseDatos[v][etq].getX0() + " " +  BaseDatos[v][etq].getX1() + " " +  BaseDatos[v][etq].getX3() + "\n";
               }
           }
        }
        contents+= "\n";
        if (nFile!="")
            Files.addToFile(nFile, contents);
    }


    /**
     * <p>
     * Fill TableVar with the characteristics of the variables and creates
     * characteristics and intervals for the fuzzy sets
     * </p>
     * @param size          Number of variables of the dataset
     */
    public  void Load(int size) {
        num_vars = size;  // Stores the number of variables of the dataset
        var = new TypeVar[num_vars];   // Creates space for the structure

        // For each variable of the dataset
        for (int i=0; i<num_vars; i++) {
            var[i] = new TypeVar();  // Creates space for the variable chars
            var[i].setName (Attributes.getInputAttribute(i).getName());

            if (Attributes.getInputAttribute(i).getType()==Attribute.NOMINAL) {
                var[i].setType ('e');
                var[i].setContinuous(false);
                var[i].initValues (Attributes.getInputAttribute(i).getNominalValuesList());
                var[i].setMin(0);     // Enumerated values are translated into values from 0 to number of elements - 1
                var[i].setMax(Attributes.getInputAttribute(i).getNumNominalValues()-1);
                var[i].setNLabels (Attributes.getInputAttribute(i).getNumNominalValues());
                // Update max number of values for discrete vars
                if (var[i].getNLabels() > MaxValores)
                    MaxValores = var[i].getNLabels();
            }
            else if (Attributes.getInputAttribute(i).getType()==Attribute.REAL) {
                // Real: Continuous type
                var[i].setType ('r');
                var[i].setContinuous (true);
                var[i].setMin ((float)Attributes.getInputAttribute(i).getMinAttribute());
                var[i].setMax ((float)Attributes.getInputAttribute(i).getMaxAttribute());
                var[i].setNLabels (n_etiq);
                // Update the max number of labels for cont variables and number of values
                if (var[i].getNLabels() > MaxEtiquetas)
                    MaxEtiquetas = var[i].getNLabels();
                if (var[i].getNLabels() > MaxValores)
                    MaxValores = var[i].getNLabels();
            }
            else {
                // Integer: Continuous type
                var[i].setType ('i');
                var[i].setContinuous (true);
                var[i].setMin ((float)Attributes.getInputAttribute(i).getMinAttribute());
                var[i].setMax ((float)Attributes.getInputAttribute(i).getMaxAttribute());
                var[i].setNLabels (n_etiq);
                // Update the max number of labels for cont variables and number of values
                if (var[i].getNLabels() > MaxEtiquetas)
                    MaxEtiquetas = var[i].getNLabels();
                if (var[i].getNLabels() > MaxValores)
                    MaxValores = var[i].getNLabels();
            }
        }

        // Creates "Fuzzy" characteristics and intervals
        BaseDatos = new Fuzzy[num_vars][MaxValores];
        for (int x=0; x<num_vars; x++)
            for (int y=0; y<MaxValores; y++)
                BaseDatos[x][y] = new Fuzzy();

    }


    /**
     * <p>
     * Returns the maximum valid value for the variable "pos"
     * </p>
     * @param pos           Position of the variable
     * @return              Maximum value for the variable
     */
    public float getMax (int pos) {
        return var[pos].getMax();
    }

    /**
     * <p>
     * Returns the minimum valid value for the variable "pos"
     * </p>
     * @param pos           Position of the variable
     * @return              Minimum value for the variable
     */
    public float getMin (int pos) {
        return var[pos].getMin();
    }


    /**
     * <p>
     * Returns the number of labels of the var indicated by "pos"
     * </p>
     * @param pos           Position of the variable
     * @return              Number of labels for the variable
     */
    public int getNLabelVar (int pos) {
        return var[pos].getNLabels();
    }


    /**
     * <p>
     * Returns if the variable "pos" is or not continua
     * </p>
     * @param pos           Position of the variable
     * @return              Value true if the variable is continuous or false in otherwise
     */
    public boolean getContinuous (int pos) {
        return var[pos].getContinuous();
    }


    /**
     * <p>
     * Returns the type of the variable "pos"
     * </p>
     * @param pos           Position of the variable
     * @return              Type of the variable
     */
    public char getType (int pos) {
        return var[pos].getType();
    }


    /**
     * <p>
     * Returns the gain of the variable "pos"
     * </p>
     * @param pos           Position of the variable
     * @return              Gain of the variable
     */
    public float getGain (int pos) {
        return GI[pos];
    }


   /**
    * <p>
    * Computes and stores the information gain of each attribute (variable) of the dataset
    * </p>
    * @param Examples       Set of instances of the dataset
    * @param nFile    Name of the file
    */
    public void GainInit (TableDat Examples, String nFile) {

        int i, j, h, v;
        boolean encontrado;
        float info_gk, suma, suma1, suma2, p_clase, logaritmo;
        int num_clase[] = new int[n_clases];
        int num_vars = this.getNVars();
        int MaxValores = this.getMaxVal();
        float p[][] = new float[num_vars][MaxValores];
        float p_cond[][][] = new float [n_clases][num_vars][MaxValores];
        GI = new float[num_vars];
        intervalosGI = new float[num_vars][MaxValores];

        String contents;
        contents = "\n--------------------------------------------\n";
        contents+=   "|       Computation of the info gain       |\n";
        contents+=   "--------------------------------------------\n";
        contents+=   "Points for computation of the info gain:\n";

        // Loads the values for "intervalosGI"
        float marca, p_corte;
        for (int v1=0; v1<num_vars; v1++) {
           if (this.getContinuous(v1)==true) {
               contents+= "\tVariable " + var[v1].getName() + ": ";
               marca=(this.getMax(v1)-this.getMin(v1))/((float)(this.getNLabelVar(v1)-1));
               p_corte = this.getMin(v1) + marca / 2;
               for (int et=0; et<this.getNLabelVar(v1); et++) {
                   intervalosGI[v1][et] = p_corte;
                   contents += intervalosGI[v1][et] + "  ";
                   p_corte += marca;
               }
               contents+= "\n";
           }
        }

        // Structure initialization
        for (i=0; i<n_clases; i++)
            num_clase[i] = 0;
        for (i=0; i<num_vars; i++)
            for (j=0; j<MaxValores; j++) {
                p[i][j] = 0;        // Simple probabilities matrix
                for (h=0; h<n_clases; h++)
                    p_cond[h][i][j]=0;     // Conditional probabilities matrix
            }

        // Computation of the Simple and Conditional probabilities matrixs
        for (i=0; i<Examples.getNEx(); i++) {
            num_clase[Examples.getClass(i)]++;      // distribution by classes
            for (j=0; j<num_vars; j++) {      // distribution by values
                if (!this.getContinuous(j))  { // Discrete variable
                    if (!Examples.getLost(this,i,j)) {
                        // if the value is not a lost one
                        p[j][(int)Examples.getDat(i,j)]++;
                        p_cond[(int)Examples.getClass(i)][j][(int)Examples.getDat(i,j)]++;
                    }
                }
                else { // Continuous variable
                    encontrado = false;
                    h = 0;
                    while (!encontrado && h<this.getNLabelVar(j)) {
                        if (Examples.getDat(i,j)<=intervalosGI[j][h])
                            encontrado = true;
                        else
                            h++;
                    }
                    if (encontrado == true) {
                        p[j][h]++;
                        p_cond[(int)Examples.getClass(i)][j][h]++;
                    }
                    else {
                      if (!Examples.getLost(this, i,j)) {
                          // Lost value
                          System.out.println("Fallo al calcular la ganancia de infor, Variable " + j + " Ejemplo " + i);
                          return;
                      }
                    }
                }
            }
        }
        for (h=0; h<n_clases; h++)
            for (i=0; i<num_vars; i++) {
                if (!this.getContinuous(i))   // Discrete variable
                    for (j=(int)this.getMin(i); j<=(int)this.getMax(i); j++)
                        p_cond[h][i][j] = p_cond[h][i][j] / Examples.getNEx();
                else // Continuous variable
                    for (j=0; j<this.getNLabelVar(i); j++)
                        p_cond[h][i][j] = p_cond[h][i][j] / Examples.getNEx();
            }
        for (i=0; i<num_vars; i++) {
            if (!this.getContinuous(i))  // Discrete variable
                for (j=(int)this.getMin(i); j<=(int)this.getMax(i); j++)
                    p[i][j] = p[i][j] / Examples.getNEx();
            else  // Continuous variable
                for (j=0; j<this.getNLabelVar(i); j++)
                    p[i][j] = p[i][j] / Examples.getNEx();
        }

        // Info Gk computation
        suma = 0;
        for (i=0; i<n_clases; i++) {
            p_clase = ((float)num_clase[i])/Examples.getNEx();
            if (p_clase>0) {
                logaritmo = (float)(Math.log((double)p_clase)/Math.log(2));
                suma += p_clase * logaritmo;
            }
        }
        info_gk = (-1) * suma;

        // Information gain computation for each attibute
        for (v=0; v<num_vars; v++) {
            suma = info_gk;
            suma1 = 0;
            if (!this.getContinuous(v)) {   // Discrete Variable
                for (i=(int)this.getMin(v); i<=(int)this.getMax(v); i++) {
                    suma2=0;
                    for (j=0; j<n_clases; j++)
                        if (p_cond[j][v][i]>0){
                            logaritmo = (float) (Math.log(p_cond[j][v][i])/Math.log(2));
                            suma2+= p_cond[j][v][i]*logaritmo;
                        }
                    suma1+=p[v][i]*(-1)*suma2;
                }
            }
            else {      // Continuous Variable
                for (i=0; i<this.getNLabelVar(v); i++) {
                    suma2=0;
                    for (j=0; j<n_clases; j++)
                        if (p_cond[j][v][i]>0) {
                            logaritmo = (float)(Math.log(p_cond[j][v][i])/Math.log(2));
                            suma2+= p_cond[j][v][i]*logaritmo ;
                        }
                    suma1+=p[v][i]*(-1)*suma2;
                }
            }
            GI[v] = suma + (-1)*suma1;
        }

        contents+=   "Information Gain of the variables:\n";
        for (v=0; v<num_vars; v++) {
           if (this.getContinuous(v)==true)
               contents+= "\tVariable " + var[v].getName() + ": " + GI[v] + "\n";
        }

        if (nFile!="")
                Files.addToFile(nFile, contents);

    }


    /**
     * <p>
     * Creates a new instance of TableVar
     * </p>
     */
    public TableVar() {
    }

}
