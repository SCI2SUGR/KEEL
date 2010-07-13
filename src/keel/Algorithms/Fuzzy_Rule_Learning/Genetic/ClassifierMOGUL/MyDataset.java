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

package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.ClassifierMOGUL;

/**
 * <p>
 * @author Written by Jesus Alcala Fernandez (University of Granada) 01/01/2004
 * @author Modified by Francisco José Berlanga (University of Jaén) 09/12/2008 
 * @version 1.0
 * @since JDK 1.6
 * </p>
 */
 
import java.io.*;
import org.core.*;
import keel.Dataset.*;
import java.util.*;

public class MyDataset {
/**	
 * <p>
 * It contains the methods to read a Dataset
 * </p>
 */
 	
    public T_Table[] datos;
    public int n_variables, n_inputs, long_tabla;
    public int nClasses;
    public int no_cubiertos;
    public String[] output;
    public String[] clases;
    public T_Interval[] extremos;
    public String fichero;
    public InstanceSet IS;
    public boolean noOutputs;


    /**
     * <p>    
     * Stores in memory the contents of the data file "f"
     * </p>
     * @param f String The name containing the Data Set
     * @param train boolean TRUE is the Data Set contains the training data. FALSE if it contains the test data     
     */    
    public MyDataset(String f, boolean train) {
        fichero = f;
        IS = new InstanceSet();

        try {
            processModelDataset(f, train);
        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }
    }


    /**
     * <p>    
     * Reads the Data Sets
     * </p>
     * @param nfejemplos String The name containing the Data Set
     * @param train boolean TRUE is the Data Set contains the training data. FALSE if it contains the test data     
     */ 
    public void processModelDataset(String nfejemplos, boolean train) throws
            IOException {
        int i, j, k, pos;

        try {
            // Load in memory a dataset that contains a regression problem
            IS.readSet(nfejemplos, train);

            // We read the number of instances and variables
            long_tabla = IS.getNumInstances();
            n_inputs = Attributes.getInputNumAttributes();
            no_cubiertos = long_tabla;

            // Check that there is only one output variable and
            // it is nominal

            if (Attributes.getOutputNumAttributes() > 1) {
                System.out.println(
                        "This algorithm can not process MIMO datasets");
                System.out.println(
                        "All outputs but the first one will be removed");
            }

            boolean noOutputs = false;
            if (Attributes.getOutputNumAttributes() < 1) {
                System.out.println(
                        "This algorithm can not process datasets without outputs");
                System.out.println("Zero-valued output generated");
                noOutputs = true;
            }

            n_variables = n_inputs + Attributes.getOutputNumAttributes();
            output = new String[long_tabla];
            clases = new String[long_tabla];

            // Initialice and fill our own tables
            datos = new T_Table[long_tabla];

            // Maximum and minimum of inputs/output data
            extremos = new T_Interval[n_variables];
            for (i = 0; i < n_variables; i++) {
                extremos[i] = new T_Interval();
            }

            // All values are casted into double/integer
            nClasses = 0;
            for (i = 0, k = 0; i < long_tabla; i++) {
                Instance inst = IS.getInstance(i);

                if (inst.existsAnyMissingValue() == true) {
                    System.out.println(
                            "This algorithm can not process missing values");
                    System.out.println("This algorithm don't use the instance " +
                                       (i + 1) +
                            ". You have to apply before a preprocess method");
                } else {
                    datos[k] = new T_Table(n_variables);
                    for (j = 0; j < n_inputs; j++) {
                        datos[k].ejemplo[j] = IS.getInputNumericValue(i, j);
                        if (datos[k].ejemplo[j] > extremos[j].max || k == 0) {
                            extremos[j].max = datos[k].ejemplo[j];
                        }
                        if (datos[k].ejemplo[j] < extremos[j].min || k == 0) {
                            extremos[j].min = datos[k].ejemplo[j];
                        }
                    }

                    if (noOutputs) {
                        datos[k].ejemplo[j] = 0;
                        output[k] = "";
                    } else {
                        datos[k].ejemplo[j] = IS.getOutputNumericValue(i, 0);
                        output[k] = IS.getOutputNominalValue(i, 0);
                    }

                    if ((int) datos[k].ejemplo[j] > nClasses) {
                        nClasses = (int) datos[k].ejemplo[j];
                    }

                    pos = (int) datos[k].ejemplo[j];
                    clases[pos] = output[k];

                    k++;
                }
            }
            nClasses++;
            System.out.println("Number of classes=" + nClasses);

            long_tabla = k;
        }

    catch (Exception e) {
        System.out.println("DBG: Exception in readSet");
        e.printStackTrace();
    }
}


    /**
     * <p>    
     * It returns the header
     * </p>  
     * @ Return String The Header of the Data Set
     */
public String getHeader() {
    return (IS.getHeader());
}


    /**
     * <p>    
     * It create a new table with the examples from the Data Set
     * </p>   
     */
public void newTable() {
    int pos;

    // Initialice and fill our own tables
    datos = new T_Table[long_tabla];

    // All values are casted into double/integer
    nClasses = 0;
    for (int i = 0, k = 0, j = 0; i < long_tabla; i++) {
        Instance inst = IS.getInstance(i);
        datos[k] = new T_Table(n_variables);
        for (j = 0; j < n_inputs; j++) {
            datos[k].ejemplo[j] = IS.getInputNumericValue(i, j);
            if (datos[k].ejemplo[j] > extremos[j].max || k == 0) {
                extremos[j].max = datos[k].ejemplo[j];
            }
            if (datos[k].ejemplo[j] < extremos[j].min || k == 0) {
                extremos[j].min = datos[k].ejemplo[j];
            }
        }

        if (noOutputs) {
            datos[k].ejemplo[j] = 0;
            output[k] = "";
        } else {
            datos[k].ejemplo[j] = IS.getOutputNumericValue(i, 0);
            output[k] = IS.getOutputNominalValue(i, 0);
        }

        if ((int) datos[k].ejemplo[j] > nClasses) {
            nClasses = (int) datos[k].ejemplo[j];
        }

        pos = (int) datos[k].ejemplo[j];
        clases[pos] = output[k];

        k++;
    }
    nClasses++;
    System.out.println("Number of classes=" + nClasses);

}

    /**
     * <p>    
     * Return the output value of the example in position "pos"
     * </p>  
     * @return String The output value for example in position "pos" 
     */
public String getOutputAsString(int pos) {
    return output[pos];
}

    /**
     * <p>    
     * Return the name of the class in position "pos"
     * </p>  
     * @return String The name of the class in position "pos" 
     */
public String getClassAsString(int pos) {
    return clases[pos];
}


}

