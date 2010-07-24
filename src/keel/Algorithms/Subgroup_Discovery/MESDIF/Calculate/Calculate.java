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
 * @author Written by Pedro González (University of Jaen) 15/08/2004
 * @author Modified by Cristóbal J. Carmona (University of Jaen) 15/04/2010
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.MESDIF.Calculate;

import keel.Dataset.*;
import org.core.*;

import java.io.IOException;
import java.util.Vector;
import java.util.StringTokenizer;


public class Calculate {
    /**
     * <p>
     * Calculate the values of subgroup discovery quality measures
     * with respect to the rules extracted by the algorithm
     * </p>
     */


    /**
     * <p>
     * Sets the value of the gen of an example as an lost value 
     * lost = max value of the variable + 1
     * </p>
     * @param example   Position of the example in the dataset
     * @param pos       Position of the variable
     **/
    public static void setLost (int example, int pos) {
        StCalculate.tabla[example].ejemplo[pos] = StCalculate.var[pos].max+1;
    }


    /**
     * <p>
     * Returns if the value of the gen of an example is a lost value or not
     * lost = max value of the variable + 1
     * </p>
     * @param example   Position of the example in the dataset
     * @param pos       Position of the variable
     * @return          If the example is a lost value
     **/
    public static boolean getLost (int example, int pos) {
        if (StCalculate.tabla[example].ejemplo[pos] == StCalculate.var[pos].max+1)
            return true;
        else
            return false;
    }


   /**
    * <p>
    * Dataset interpretation - read the dataset and stores the values
    * The attribute designed in "outputs" at the dataset file is the target variable
    * If it is not established, the last one is taken as output - defined in the methods
    * that manages the dataset.
    * All the variables except this are stored in "tabla.ejemplo"
    * Target variable is stored in "tabla.clase"
    * </p>
    * @param input_file             File to capture
    **/
    public static void CaptureDataset (String input_file)
    throws IOException   {

       try {

        int i, j;

        // Declares the dataset
        StCalculate.Data = new InstanceSet();
        // Load in memory the dataset
    	StCalculate.Data.readSet(input_file,false);

        // Sets the number of instances (examples)
        StCalculate.n_eje  = StCalculate.Data.getNumInstances();
        // Sets the number of input attributes (variables)
        StCalculate.num_vars = Attributes.getInputNumAttributes();

        // Check that there is only one output variable
        if (Attributes.getOutputNumAttributes()>1) {
  		System.out.println("This algorithm can not process MIMO datasets");
  		System.out.println("All outputs but the first one will be removed");
	}

        if (Attributes.getOutputNumAttributes()<1) {
  		System.out.println("This algorithm can not process datasets without outputs");
  		System.out.println("Zero-valued output generated");
	}

        // Chek that the output variable is nominal
        if (Attributes.getOutputAttribute(0).getType()!=Attribute.NOMINAL) {
            // If the output variables is not enumeratad, the algorithm can not be run
            try {
                throw new IllegalAccessException("Finish");
            } catch( IllegalAccessException term) {
                System.err.println("Target variable is not a discrete one.");
                System.err.println("Algorithm can not be run.");
                System.out.println("Program aborted.");
                System.exit(-1);
            }
        }

        // Set the number of classes of the output attribute - this attribute must be nominal
        StCalculate.n_clases = Attributes.getOutputAttribute(0).getNumNominalValues();
        StCalculate.name_class = new String[StCalculate.n_clases];
        for(int pos=0; pos<StCalculate.n_clases; pos++){
            StCalculate.name_class[pos] = Attributes.getOutputAttribute(0).getNominalValue(pos);
        }

        // Screen output of the output variable and selected class
        System.out.println ( "Output variable: " + Attributes.getOutputAttribute(0).getName()); // + "; Target class number: " + StCalculate.n_clasObj);

        // Set the variables characteristics
        StCalculate.var = new TypeVar[StCalculate.num_vars];

        for (i=0; i<StCalculate.num_vars; i++) {
            StCalculate.var[i] = new TypeVar();
            StCalculate.var[i].nombre = Attributes.getInputAttribute(i).getName();

            if (Attributes.getInputAttribute(i).getType()==Attribute.NOMINAL) {
            	// Nominal (enumerated) variable - Discrete type
                StCalculate.var[i].tipoDato = 'e';
                StCalculate.var[i].continua = false;
                StCalculate.var[i].valores = new Vector(Attributes.getInputAttribute(i).getNominalValuesList());
                StCalculate.var[i].min = 0;     // Enumerated values are translated into values from 0 to number of elements - 1
                StCalculate.var[i].max = Attributes.getInputAttribute(i).getNumNominalValues()-1;
                StCalculate.var[i].n_etiq = Attributes.getInputAttribute(i).getNumNominalValues();
                // Update max number of values for discrete vars
                if (StCalculate.var[i].n_etiq > StCalculate.MaxValores)
                    StCalculate.MaxValores = StCalculate.var[i].n_etiq;
            }
            else if (Attributes.getInputAttribute(i).getType()==Attribute.REAL) {
                // Real: Continuous type
                StCalculate.var[i].tipoDato = 'r';
                StCalculate.var[i].continua = true;
                StCalculate.var[i].min = (float)Attributes.getInputAttribute(i).getMinAttribute();
                StCalculate.var[i].max = (float)Attributes.getInputAttribute(i).getMaxAttribute();
                StCalculate.var[i].n_etiq = StCalculate.Param.n_etiq;
                // Update the max number of labels for cont variables and number of values
                if (StCalculate.var[i].n_etiq > StCalculate.MaxEtiquetas)
                    StCalculate.MaxEtiquetas = StCalculate.var[i].n_etiq;
                if (StCalculate.var[i].n_etiq > StCalculate.MaxValores)
                    StCalculate.MaxValores = StCalculate.var[i].n_etiq;
            }
            else {
                // Integer: Continuous type
                StCalculate.var[i].tipoDato = 'i';
                StCalculate.var[i].continua = true;
                StCalculate.var[i].min = (float)Attributes.getInputAttribute(i).getMinAttribute();
                StCalculate.var[i].max = (float)Attributes.getInputAttribute(i).getMaxAttribute();
                StCalculate.var[i].n_etiq = StCalculate.Param.n_etiq;
                // Update the max number of labels for cont variables and number of values
                if (StCalculate.var[i].n_etiq > StCalculate.MaxEtiquetas)
                    StCalculate.MaxEtiquetas = StCalculate.var[i].n_etiq;
                if (StCalculate.var[i].n_etiq > StCalculate.MaxValores)
                    StCalculate.MaxValores = StCalculate.var[i].n_etiq;
            }

        }

        // Fill the "tabla" structure  with the data from dataset
        StCalculate.tabla = new TTable[StCalculate.n_eje];

        for (i=0; i<StCalculate.n_eje; i++) {
            // num attribute is not used
            StCalculate.tabla[i] = new TTable();
            StCalculate.tabla[i].fcubierto = false;  // Set example to not covered - fuzzy
            StCalculate.tabla[i].ccubierto = false;  // Set example to not covered - crisp
            StCalculate.tabla[i].ejemplo = new float[StCalculate.num_vars];

	    // Stores de values for all the input variables
	    Instance inst = StCalculate.Data.getInstance(i);
	    double instValues[] = new double[StCalculate.num_vars];
            instValues = inst.getAllInputValues();
            // Gets all the input attributes of the instance, converting enumerated to consecutive integers

            for (j=0; j<StCalculate.num_vars; j++) {
                if (inst.getInputMissingValues(j))
                    // If the value is a lost one, sets as max value + 1
                    setLost (i, j);  // old :StCalculate.tabla[i].ejemplo[j] = StCalculate.var[j].max+1;
                else {
                        // Stores the value
                        // NOTE: automatic translation from enum to integer for nominal values
                        StCalculate.tabla[i].ejemplo[j] = (float) instValues[j];
                }
            }

            // Set the value for the target variable of the example
            double classValue[] = new double[1];
            classValue = inst.getAllOutputValues();
            StCalculate.tabla[i].clase = (int) classValue[0];

        }

     } catch (Exception e) {
       System.out.println("DBG: Exception in readSet");
       e.printStackTrace();
       }

    }


    /**
     * <p>
     * It returns the number (position) of the attribute name indicated.
     * </p>
     * @param _name     Name of the attribute.
     * @return          Position of the attribute.
     */
    public static int getNumAttribute(String _name) {
        int i;
        // The correct input attributes are chosen with the functions getInputNumAttribute
        // and getInputAttribute 
        for (i=0; i<Attributes.getInputNumAttributes(); i++) {
            if (Attributes.getInputAttribute(i).getName().equals(_name))
               return i;
        }
        return -1;
    }



   /**
    * <p>
    * Generate the population with the rules obtained by the algorithm
    * </p>
    * @param nFile      File of the rules obtained by the algorithm
    **/
    public static void CaptureRules (String nFile)
    throws IOException   {

       try {
            String file, linea, tok;
            StringTokenizer lineasFichero, tokens;
            file = Files.readFile(nFile);
            file = file.toLowerCase() + "\n ";
            lineasFichero = new StringTokenizer(file,"\n\r");
            int numero; 
            numero = 0;

            do {
                if  (!lineasFichero.hasMoreTokens()) break;
                linea=lineasFichero.nextToken();
                tokens = new StringTokenizer(linea," ,\t");
		if (tokens.hasMoreTokens()) {
                    tok = tokens.nextToken();

                    if (tok.equalsIgnoreCase("generated")) {
                        tokens.nextToken();
                        StCalculate.NumReglasGeneradas++;
                    }

                    else if (tok.equalsIgnoreCase("variable")) {
                        numero++;
                        String NombreVar = tokens.nextToken(); 
                        for (int k=0; k<Attributes.getInputNumAttributes();k++) {
                            if (NombreVar.equalsIgnoreCase(Attributes.getInputAttribute(k).getName()))
                               NombreVar = Attributes.getInputAttribute(k).getName();
                        }

                        int NumVar = getNumAttribute(NombreVar);

                        tokens.nextToken();
                        String ValorVar = tokens.nextToken();
                        int ValorNum;
                        if (ValorVar.equalsIgnoreCase("label")) {
                            // Continuous variable
                            do {
                                ValorVar = tokens.nextToken();
                                ValorNum = Integer.parseInt(ValorVar);
                                StCalculate.poblac.setCromElem(StCalculate.NumReglasGeneradas-1, NumVar, ValorNum, 1);
                                tokens.nextToken(); tokens.nextToken(); tokens.nextToken();
                                if (tokens.hasMoreTokens())
                                    ValorVar = tokens.nextToken();
                                else
                                    break;
                            } while (true);
                        }
                        else {
                            // Discrete variable
                            do {
                                for (int k=0; k<Attributes.getInputAttribute(NumVar).getNumNominalValues(); k++) {
                                    if (ValorVar.equalsIgnoreCase(Attributes.getInputAttribute(NumVar).getNominalValue(k)))
                                        ValorVar = Attributes.getInputAttribute(NumVar).getNominalValue(k);
                                }
                                ValorNum = Attributes.getInputAttribute(NumVar).convertNominalValue(ValorVar);
                                StCalculate.poblac.setCromElem(StCalculate.NumReglasGeneradas-1, NumVar, ValorNum, 1);
                                if (tokens.hasMoreTokens())
                                    ValorVar = tokens.nextToken();
                                else
                                    break;
                            } while (true);
                        }
                        StCalculate.poblac.setCromElem(StCalculate.NumReglasGeneradas-1, NumVar, StCalculate.var[NumVar].n_etiq, 1);
                    }

                    else if (tok.equalsIgnoreCase("consecuent:")) {

                        numero++;
                        StCalculate.poblac.setIndivNvar(StCalculate.NumReglasGeneradas-1, numero);
                        numero=0;


                        String ValorCons = tokens.nextToken();
                        for (int k=0; k<Attributes.getOutputAttribute(0).getNumNominalValues(); k++) {
                            if (ValorCons.equalsIgnoreCase(Attributes.getOutputAttribute(0).getNominalValue(k)))
                               ValorCons = Attributes.getOutputAttribute(0).getNominalValue(k);
                        }
                        int ValorClase = Attributes.getOutputAttribute(0).convertNominalValue(ValorCons);

                        StCalculate.poblac.setIndivNameClass (StCalculate.NumReglasGeneradas-1, ValorCons);
                        StCalculate.poblac.setIndivNumClass  (StCalculate.NumReglasGeneradas-1, ValorClase);

                        StCalculate.poblac.setIndivTotalClass(StCalculate.NumReglasGeneradas-1, Utils.ExamplesClass(ValorClase));

                        for (int k=0; k<StCalculate.NumReglasGeneradas-1;k++) {
                            if (StCalculate.poblac.Compare(k,StCalculate.NumReglasGeneradas-1)) {
                                StCalculate.poblac.initIndEmp (StCalculate.NumReglasGeneradas-1);

                                StCalculate.NumReglasGeneradas--;

                            }
                        }

                    }
		}
            } while (true);
     } catch (Exception e) {
       System.out.println("DBG: Exception in readRules");
       e.printStackTrace();
       }

    }



   /**
    * <p>
    * Return the number of rules obtained by the algorithm
    * </p>
    * @param nFile              File of rules obtained by the algorithm
    * @return                   The number of rules obtained by the algorithm
    **/
    public static int CaptureNumRules (String nFile)
    throws IOException   {
       int numero =0;

       try {
            String file, linea, tok;
            StringTokenizer lineasFichero, tokens;
            file = Files.readFile(nFile);
            file = file.toLowerCase() + "\n ";
            lineasFichero = new StringTokenizer(file,"\n\r");

            do {
                if  (!lineasFichero.hasMoreTokens()) break;
                linea=lineasFichero.nextToken();
                tokens = new StringTokenizer(linea," ,\t");
		if (tokens.hasMoreTokens()) {
                    tok = tokens.nextToken();
                    if (tok.equalsIgnoreCase("generated")) {
                        tokens.nextToken();
                        numero++;
                    }
		}
            } while (true);


     } catch (Exception e) {
       System.out.println("DBG: Exception in readRules");
       e.printStackTrace();
       }

    return numero;

    }



    /**
     * <p>
     * Calculate is the main method of the Calculate
     * </p>
     * @param output_file_tra    Name of the training output file
     * @param output_file_tst    Name of the test output file
     * @param input_file_tra    Name of the training file
     * @param input_file_tst    Name of the test file
     * @param rule_file         Name of the rule file
     * @param quality_file      Name of the output quality file
     * @param nlabels           Number of labels for the continuous variables
     **/
    public static void Calculate (String output_file_tra, String output_file_tst, String input_file_tra, String input_file_tst, String rule_file, String quality_file, int nlabels) throws Exception {

        StCalculate.Param = new Param (output_file_tra,output_file_tst,input_file_tra,input_file_tst, rule_file, quality_file, nlabels);

        CaptureDataset (StCalculate.Param.input_file_tra);

        // Creates "Fuzzy" characteristics and intervals
        StCalculate.BaseDatos = new Fuzzy[StCalculate.num_vars][StCalculate.MaxEtiquetas];
        for (int x=0; x<StCalculate.num_vars; x++)
            for (int y=0; y<StCalculate.MaxEtiquetas; y++)
                StCalculate.BaseDatos[x][y] = new Fuzzy();
        StCalculate.intervalos = new float[StCalculate.num_vars][StCalculate.MaxEtiquetas];

        // Setting and file writing of fuzzy sets characteristics for continuous variables
        Semantics.Initialise();

        // Create and initilize gain information array
        StCalculate.GI = new float[StCalculate.num_vars];
        Gain.Init();

        // Set all the examples as not covered
        for (int ej=0; ej<StCalculate.n_eje; ej++) {
            StCalculate.tabla[ej].fcubierto = false;  // Set example to not covered - fuzzy
            StCalculate.tabla[ej].ccubierto = false;  // Set example to not covered - crisp
        }

        // Read the number of rules of the rule_file
        StCalculate.Param.long_poblacion = CaptureNumRules (StCalculate.Param.rul_file);
        System.out.print ("The file contains " + StCalculate.Param.long_poblacion + " rules");

        // Creates and initialices the population
        StCalculate.poblac = new Population(StCalculate.Param.long_poblacion, StCalculate.num_vars, StCalculate.var);
        StCalculate.poblac.initPopEmp ();

        // Variables Initialization
        StCalculate.total_ej_cubiertos = 0;
        StCalculate.NumReglasGeneradas=0;

        // Reed the rules of the file
        CaptureRules (StCalculate.Param.rul_file);
        // Counts the number of examples of each class
        System.out.println (" (" + StCalculate.NumReglasGeneradas + " are different)");

        StCalculate.poblac.CalcPobOutput(StCalculate.Param.output_file_tra);  // Covered examples are marked







        // Read the dataset, store values and echo to output and seg files
        CaptureDataset (StCalculate.Param.input_file_tst);

        // Creates "Fuzzy" characteristics and intervals
        StCalculate.BaseDatos = new Fuzzy[StCalculate.num_vars][StCalculate.MaxEtiquetas];
        for (int x=0; x<StCalculate.num_vars; x++)
            for (int y=0; y<StCalculate.MaxEtiquetas; y++)
                StCalculate.BaseDatos[x][y] = new Fuzzy();
        StCalculate.intervalos = new float[StCalculate.num_vars][StCalculate.MaxEtiquetas];

        // Setting and file writing of fuzzy sets characteristics for continuous variables
        Semantics.Initialise();

        // Create and initilize gain information array
        StCalculate.GI = new float[StCalculate.num_vars];
        Gain.Init();

        // Set all the examples as not covered
        for (int ej=0; ej<StCalculate.n_eje; ej++) {
            StCalculate.tabla[ej].fcubierto = false;  // Set example to not covered - fuzzy
            StCalculate.tabla[ej].ccubierto = false;  // Set example to not covered - crisp
        }

        // Read the number of rules of the rule_file
        StCalculate.Param.long_poblacion = CaptureNumRules (StCalculate.Param.rul_file);
        System.out.print ("The file contains " + StCalculate.Param.long_poblacion + " rules");

        // Creates and initialices the population
        StCalculate.poblac = new Population(StCalculate.Param.long_poblacion, StCalculate.num_vars, StCalculate.var);
        StCalculate.poblac.initPopEmp ();

        // Variables Initialization
        StCalculate.total_ej_cubiertos = 0;
        StCalculate.NumReglasGeneradas=0;

        // Reed the rules of the file
        CaptureRules (StCalculate.Param.rul_file);
        // Counts the number of examples of each class
        System.out.println (" (" + StCalculate.NumReglasGeneradas + " are different)");

        StCalculate.poblac.CalcPob(StCalculate.Param.measure_file);  // Covered examples are marked
        StCalculate.poblac.CalcPobOutput(StCalculate.Param.output_file_tst);  // Covered examples are marked


        System.out.println ("Program finished\n");
    }


}
