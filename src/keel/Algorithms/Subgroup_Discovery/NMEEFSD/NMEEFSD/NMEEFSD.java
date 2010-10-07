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
 * NMEEFSD
 * Non-dominated Multi-objective Evolutionary algorithm for Extracting Fuzzy rules in Subgroup Discovery
 * </p>
 * <p>
 * Algorithm for the discovery of rules describing subgroups
 * @author Cristóbal J. Carmona
 * @version 1.0
 * @since JDK1.5
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.NMEEFSD.NMEEFSD;

import keel.Algorithms.Subgroup_Discovery.NMEEFSD.Calculate.*;

import keel.Dataset.*;
import org.core.*;
import java.util.Vector;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

public class NMEEFSD {

    private static int seed;                // Seed for the random generator
    private static String nombre_alg;       // Algorithm Name
    private static boolean claseSelec;      // Indicates if there is a selected class to run the algorithm or not

    private static String input_file_ref;   // Input mandatory file training
    private static String input_file_tra;   // Input mandatory file training
    private static String input_file_tst;   // Input mandatory file test
    private static String output_file_tra;  // Output file training
    private static String output_file_tst;  // Output file test
    private static String rule_file;        // Auxiliary output file for rules
    private static String measure_file;     // Auxiliary output file for quality measures of the rules
    private static String seg_file;         // Auxiliary output file for tracking
    private static String qmeasure_file;    // Output quality measure file

    // Structures
    static InstanceSet Data;
    static TableVar Variables;     // Set of variables of the dataset and their characteristics
    static TableDat Ejemplos;      // Set of instances of the dataset
    static Genetic AG;             // Genetic Algorithm

    /**
     * <p>
     * Auxiliar Gets the name for the output files, eliminating "" and skiping "="
     * </p>
     * @param s                 String of the output files
     */
    private static void GetOutputFiles(StringTokenizer s) {
        String val   = s.nextToken();
        output_file_tra = s.nextToken().replace('"',' ').trim();
        output_file_tst = s.nextToken().replace('"',' ').trim();
        rule_file    = s.nextToken().replace('"',' ').trim();
        measure_file = s.nextToken().replace('"',' ').trim();
        seg_file     = s.nextToken().replace('"',' ').trim();
        qmeasure_file= s.nextToken().replace('"',' ').trim();
    }

    /**
     * <p>
     * Auxiliar Gets the name for the input files, eliminating "" and skiping "="
     * </p>
     * @param s                 String of the input files
     */
    private static void GetInputFiles(StringTokenizer s) {
        String val   = s.nextToken(); // skip "="
        input_file_ref = s.nextToken().replace('"',' ').trim();
        input_file_tra = s.nextToken().replace('"',' ').trim();
        input_file_tst = s.nextToken().replace('"',' ').trim();
    }

    /**
     * <p>
     * Reads the parameters from the file specified and stores the values
     * </p>
     * @param nFile      Fichero of parameters
     */
    public static void ReadParameters (String nFile) {
       claseSelec = false;
        String contents;

        try {
            int nl;
            String fichero, linea, tok;
            StringTokenizer lineasFichero, tokens;
            fichero = Files.readFile (nFile);
            fichero = fichero.toLowerCase() + "\n ";
            lineasFichero = new StringTokenizer(fichero,"\n\r");

            for (nl=0, linea=lineasFichero.nextToken(); lineasFichero.hasMoreTokens(); linea=lineasFichero.nextToken()) {
                nl++;
                tokens = new StringTokenizer(linea," ,\t");
                if (tokens.hasMoreTokens()) {
                    tok = tokens.nextToken();
                    if (tok.equalsIgnoreCase("algorithm"))
                        nombre_alg = Utils.getParamString(tokens);
                    else if (tok.equalsIgnoreCase("inputdata"))
                        GetInputFiles(tokens);
                    else if (tok.equalsIgnoreCase("outputdata"))
                        GetOutputFiles(tokens);
                    else if (tok.equalsIgnoreCase("RulesRep"))
                        AG.setRulesRep(Utils.getParamString(tokens).toUpperCase());
                    else if (tok.equalsIgnoreCase("StrictDominance"))
                        AG.setStrictDominance(Utils.getParamString(tokens).toUpperCase());
                    else if (tok.equalsIgnoreCase("seed"))
                        seed = Utils.getParamInt(tokens);
                    else if (tok.equalsIgnoreCase("targetClass")) {
                        Variables.setNameClassObj(Utils.getParamString(tokens));
                        claseSelec=true;
                        }
                    else if (tok.equalsIgnoreCase("nLabels"))
                        Variables.setNLabel(Utils.getParamInt(tokens));
                    else if (tok.equalsIgnoreCase("nEval"))
                        AG.setNEval(Utils.getParamInt(tokens));
                    else if (tok.equalsIgnoreCase("popLength"))
                        AG.setLengthPopulation(Utils.getParamInt(tokens));
                    else if (tok.equalsIgnoreCase("crossProb"))
                        AG.setProbCross(Utils.getParamFloat(tokens));
                    else if (tok.equalsIgnoreCase("mutProb"))
                        AG.setProbMutation(Utils.getParamFloat(tokens));
                    else if (tok.equalsIgnoreCase("diversity"))
                        AG.setDiversity(Utils.getParamString(tokens).toUpperCase());
                    else if (tok.equalsIgnoreCase("ReInitCob"))
                        AG.setReInitCob(Utils.getParamString(tokens));
                    else if (tok.equalsIgnoreCase("porcCob"))
                        AG.setPorcCob(Utils.getParamFloat(tokens));
                    else if (tok.equalsIgnoreCase("minCnf"))
                        AG.setMinCnf(Utils.getParamFloat(tokens));
                    else if (tok.equalsIgnoreCase("Obj1")){
                        AG.setNumObjectives(3);
                        AG.iniNObjectives();
                        AG.setNObjectives(0, Utils.getParamString(tokens).toUpperCase());
                         }
                    else if (tok.equalsIgnoreCase("Obj2"))
                        AG.setNObjectives(1, Utils.getParamString(tokens).toUpperCase());
                    else if (tok.equalsIgnoreCase("Obj3")){
                        String nil = Utils.getParamString(tokens);
                        if(nil.toUpperCase().compareTo("NULL")!=0)
                            AG.setNObjectives(2, nil.toUpperCase());
                        else AG.setNumObjectives(2);
                        }
                    else  throw new IOException("Syntax error on line "+nl+": ["+tok+"]\n");
                }
            }
        }
        catch(FileNotFoundException e) {
            System.err.println(e+" Parameter file");
        }
        catch(IOException e) {
            System.err.println(e+"Aborting program");
            System.exit(-1);
        }

        Files.writeFile(seg_file,"");

        contents = "--------------------------------------------\n";
        contents+= "|              Parameters Echo             |\n";
        contents+= "--------------------------------------------\n";
        contents+= "Algorithm name: " + nombre_alg + "\n";
        contents+= "Input file name training: " + input_file_tra + "\n";
        contents+= "Rules file name: " + rule_file + "\n";
        contents+= "Tracking file name: " + seg_file + "\n";
        contents+= "Representation of the Rules: " + AG.getRulesRep() + "\n";
        contents+= "Strict dominance: " + AG.getStrictDominance() + "\n";
        contents+= "Random generator seed: " + seed + "\n";
        contents+= "Selected class of the target variable: ";
        if (claseSelec)
           contents+= Variables.getNameClassObj() + "\n";
        else
           contents+= "not established\n";
        contents+= "Number of labels for the continuous variables: " + Variables.getNLabel() + "\n";
        contents+= "Number of evaluations: " + AG.getNEval() + "\n";
        contents+= "Number of individuals in the Population: " + AG.getLengthPopulation() + "\n";
        contents+= "Cross probability: " + AG.getProbCross() + "\n";
        contents+= "Mutation probability: " + AG.getProbMutation() + "\n";
        contents+= "Diversity: " + AG.getDiversity() + "\n";
        contents+= "Perform ReInitCob: " + AG.getReInitCob() + "\n";
        contents+= "Percentage of the ReInitCob: " + AG.getPorcCob() + "\n";
        contents+= "Minimum confidence threshold: " + AG.getMinCnf() + "\n";

        contents+= "Number of objetives: " + AG.getNumObjectives() + "\n";
        for(int i=1; i<=AG.getNumObjectives(); i++){
            contents+= "\tObjetive "+i+": " + AG.getNObjectives(i-1) + "\n";
        }

        Files.addToFile(seg_file,contents);

    }


   /**
    * <p>
    * Read the dataset and stores the values
    * </p>
    */
    public static void CaptureDataset () throws IOException   {

        try {

        // Declaration of the dataset and load in memory
        Data = new InstanceSet();
        Data.readSet(input_file_ref,true);

        // Check that there is only one output variable
        if (Attributes.getOutputNumAttributes()>1) {
  		System.out.println("This algorithm can not process MIMO datasets");
  		System.out.println("All outputs but the first one will be removed");
	}
	boolean noOutputs=false;
	if (Attributes.getOutputNumAttributes()<1) {
  		System.out.println("This algorithm can not process datasets without outputs");
  		System.out.println("Zero-valued output generated");
  		noOutputs=true;
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
        Variables.setNClass(Attributes.getOutputAttribute(0).getNumNominalValues());

        // Screen output of the output variable and selected class
        System.out.println ( "Output variable: " + Attributes.getOutputAttribute(0).getName());

        // Creates the space for the variables and load the values.
        Variables.Load (Attributes.getInputNumAttributes());

        // Setting and file writing of fuzzy sets characteristics for continuous variables
        String nombreF = seg_file;
        Variables.InitSemantics (nombreF);

        // Creates the space for the examples and load the values
        Ejemplos.Load(Data,Variables);

        } catch (Exception e) {
            System.out.println("DBG: Exception in readSet");
            e.printStackTrace();
        }

    }

   /**
    * <p>
    * Dataset file writting to output file
    * </p>
    * @param filename           Output file
    */
    public static void WriteOutDataset (String filename) {
        String contents;
        contents = Data.getHeader();
        contents+= Attributes.getInputHeader() + "\n";
        contents+= Attributes.getOutputHeader() + "\n\n";
        contents+= "@data \n";
        Files.writeFile(filename, contents);
    }


   /**
    * <p>
    * Dataset file writting to tracking file
    * </p>
    * @param filename           Tracking file
    */
    public static void WriteSegDataset (String filename) {
        String contents="\n";
        contents+= "--------------------------------------------\n";
        contents+= "|               Dataset Echo               |\n";
        contents+= "--------------------------------------------\n";
        contents+= "Number of examples: " + Ejemplos.getNEx() + "\n";
        contents+= "Number of variables: " + Variables.getNVars()+ "\n";
        contents+= Data.getHeader() + "\n";
        if (filename!="")
            Files.addToFile(filename, contents);
    }


    /**
     * <p>
     * Writes the rule and the quality measures
     * </p>
     * @param pob                       Actual population
     * @param nobj                      Number of objectives
     * @param fileRule                  Files of rules
     * @param fileQuality               File of quality measures
     * @param nclase                    Number of the class
     * @param cab_measure_file          File of header measure
     * @param vmarca                    Vector which marks if the rule is repeated
     */
    static void WriteRule (Population pob, int nobj, String fileRule, String fileQuality, int nclase, String cab_measure_file, Vector vmarca) {

          String contents;
          int NumRules;
          int marca = 0;

          if(nclase==0){
              Files.writeFile(fileRule, "");
              Files.writeFile(fileQuality, cab_measure_file);
          }

         NumRules = -1;
         for(int aux=0; aux<pob.getNumIndiv(); aux++){
                // Write the quality measures of the rule in "measure_file"
                QualityMeasures Result = new QualityMeasures(nobj);
                Result = pob.getIndiv(aux).getMeasures();
                marca = (Integer) vmarca.get(aux);
                if((Result.getCnf()>AG.getMinCnf())&&(marca!=1)){
                    NumRules++;
                    // Rule File
                    contents = "GENERATED RULE " + NumRules + "\n";
                    contents+= "\tAntecedent\n";

                    //Canonical rules
                    if(AG.getRulesRep().compareTo("CAN")==0){

                        CromCAN regla = pob.getIndivCromCAN(aux);
                        for (int auxi=0; auxi<Variables.getNVars(); auxi++) {
                            if (!Variables.getContinuous(auxi)) {    // Discrete variable
                                if (regla.getCromElem(auxi)<Variables.getNLabelVar(auxi)) {
                                    contents+= "\t\tVariable " + Attributes.getInputAttribute(auxi).getName() + " = " ;
                                    contents+= Attributes.getInputAttribute(auxi).getNominalValue(regla.getCromElem(auxi)) + "\n";
                                }
                            }
                            else {  // Continuous variable
                                if (regla.getCromElem(auxi)<Variables.getNLabelVar(auxi)) {
                                    contents+= "\t\tVariable " + Attributes.getInputAttribute(auxi).getName() + " = ";
                                    contents+= "Label " + regla.getCromElem(auxi);
                                    contents+= " \t (" + Variables.getX0(auxi,(int) regla.getCromElem(auxi));
                                    contents+= " " + Variables.getX1(auxi,(int) regla.getCromElem(auxi));
                                    contents+= " " + Variables.getX3(auxi,(int) regla.getCromElem(auxi)) +")\n";
                                }
                            }
                        }
                    } else {
                    //DNF rules
                        CromDNF regla = pob.getIndivCromDNF(aux);

                        for (int i=0; i<Variables.getNVars(); i++) {
                            if (regla.getCromGeneElem(i,Variables.getNLabelVar(i))==true){
                                if (!Variables.getContinuous(i)) {    // Discrete variable
                                    contents+= "\tVariable " + Attributes.getInputAttribute(i).getName() + " = " ;
                                    for (int j=0; j<Variables.getNLabelVar(i); j++) {
                                        if (regla.getCromGeneElem(i, j)==true)
                                            contents+= Attributes.getInputAttribute(i).getNominalValue(j) + " ";
                                    }
                                    contents+= "\n";
                                }
                                else {  // Continuous variable
                                    contents+= "\tVariable " + Attributes.getInputAttribute(i).getName() + " = ";
                                    for (int j=0; j<Variables.getNLabelVar(i); j++) {
                                        if (regla.getCromGeneElem(i, j)==true) {
                                            contents+= "Label " + j;
                                            contents+= " (" + Variables.getX0(i,j);
                                            contents+= " " + Variables.getX1(i,j);
                                            contents+= " " + Variables.getX3(i,j) +")\t";
                                        }
                                    }
                                    contents+= "\n";
                                }
                            }
                        }
                    }
                    contents+= "\tConsecuent: " + Variables.getNameClassObj()+"\n\n";
                    Files.addToFile(fileRule, contents);

                    DecimalFormat sixDecimals = new DecimalFormat("0.000000");
                    contents = "" + Variables.getNumClassObj();
                    for(int auxi=0; auxi<AG.getNumObjectives(); auxi++){
                        contents+= "\t"+sixDecimals.format(Result.getObjectiveValue(auxi));
                    }
                    contents+= "\t"+sixDecimals.format(Result.getCnf())+ "\n";

                    Files.addToFile(fileQuality, contents);
                }
          }
    }

     /**
      * <p>
      * Main method of the algorithm
      * </p>
      **/
    public static void main(String[] args) throws Exception {

        String contents;                    // String for the file contents
        String NameRule, NameMeasure;       // String containing de original names for the rules and measures files
        boolean terminar = false;           // Indicates no more repetition for the rule generation of diferent classes

        if (args.length != 1) {
          System.err.println("Syntax error. Usage: java AGI <parameterfile.txt>" );
          return;
        }

        // Initial echo
        System.out.println("\nNMEEF-SD implementation");

        Variables = new TableVar();
        Ejemplos = new TableDat();
        AG = new Genetic();

        // Read parameter file and initialize parameters
        ReadParameters (args[0]);
        NameRule = rule_file;
        NameMeasure = measure_file;

        // Read the dataset, store values and echo to output and seg files
        CaptureDataset ();
        //WriteOutDataset(output_file); // Creates and writes the file
        WriteSegDataset (seg_file);

        // Create and initilize gain information array
        Variables.GainInit(Ejemplos, seg_file);

        // Screen output of same parameters
        System.out.println ("\nSeed: " + seed);    // Random Seed
        System.out.println ("\nOutput variable: " + Attributes.getOutputAttribute(0).getName() ); // Output variable


        // Generation of rules for one class or all the classes
        if (claseSelec) { // If there is one class indicated as a parameter, only generate rules of thas class
            terminar = true;
            // Set the number and the name of the selected class of the output variable from its value
            Variables.setNumClassObj(-1);  // To assure an invalid value if the class name in the param file is invalid
            for (int z=0; z<Variables.getNClass(); z++) {
                if (Attributes.getOutputAttribute(0).getNominalValue(z).equalsIgnoreCase(Variables.getNameClassObj())) {
                    Variables.setNameClassObj(Attributes.getOutputAttribute(0).getNominalValue(z));
                    Variables.setNumClassObj(z);
                }
            }
            // If the value is invalid, generate rules for all the classes
            if (Variables.getNumClassObj()==-1) {
                System.out.println ( "Class name invalid (" + Variables.getNameClassObj() + "). Generate rules for all the classes");
                claseSelec=false;
                terminar = false;
                Variables.setNumClassObj(0);
            }
            else
               System.out.println ( "Generate rules for class " + Variables.getNameClassObj() + " only");
        }
        else{   // No class indicated, so generate rules of all the classes
            Variables.setNumClassObj(0);
            System.out.println ( "Generate rules for all the classes");
        }

        // Initialize measure file
        String cab_measure_file;
        cab_measure_file = "--------------------------------------------\n";
        cab_measure_file+= "|              Measures file               |\n";
        cab_measure_file+= "--------------------------------------------\n\n";
        cab_measure_file+= "MEASURES USED AS OBJECTIVES:\n\t";


        for(int i=0; i<AG.getNumObjectives(); i++){
            cab_measure_file+= AG.getNObjectives(i)+"\t";
        }

        // Include in this header the measures to be written in the quality measures file
        cab_measure_file+= "\n\nCLASS";
        for(int i=0; i<AG.getNumObjectives(); i++){
            cab_measure_file+= "\t"+AG.getNObjectives(i).toUpperCase();
        }
        cab_measure_file += "\tFCNF\n";

        int nclase=0;

        do {        

            // Initialization of random generator seed. Done after load param values
            if (seed!=0) Randomize.setSeed (seed);  

            // If no class especified, define the class for each iteration
            if (!claseSelec) // Set the nominal value of the class
                Variables.setNameClassObj(Attributes.getOutputAttribute(0).getNominalValue(Variables.getNumClassObj()));

            // Tracking to file and "seg" file
            System.out.println ("\nTarget class number: " + Variables.getNumClassObj() + " (value " + Variables.getNameClassObj() + ")");

            contents = "\n";
            contents+= "--------------------------------------------\n";
            contents+= "|                 Class "+Variables.getNumClassObj()+"                  |\n";
            contents+= "--------------------------------------------\n\n";


            Files.addToFile(seg_file, contents);

            // Set all the examples as not covered
            for (int ej=0; ej<Ejemplos.getNEx(); ej++)
                Ejemplos.setCovered (ej,false);  // Set example to not covered

            // Load the number of examples of the target class
            Ejemplos.setExamplesClassObj(Variables.getNumClassObj());

            // Variables Initialization
            //Ejemplos.setExamplesCovered(0);

            System.out.println("Processing");

            Population result = AG.GeneticAlgorithm(Variables,Ejemplos,seg_file);
            Vector marcar;
            if(AG.getRulesRep().compareTo("CAN")==0){
                marcar = AG.RemoveRepeatedCAN(result);
            } else {
                marcar = AG.RemoveRepeatedDNF(result,Variables);
            }

            WriteRule(result, AG.getNumObjectives(), NameRule, NameMeasure, nclase, cab_measure_file, marcar);

            // Termination Echo
            System.out.println("Target class terminated.");

            if (!claseSelec) { // No class indicated to generate de rules (generate rules for all the classes)
            	// Set num_clase as the next class
            	Variables.setNumClassObj(Variables.getNumClassObj()+1);
                // If there are no more classes, set terminar as true
                if (Variables.getNumClassObj()>=Variables.getNClass())
                    terminar = true;
            }

            nclase++;
            
       } while (terminar==false);

       System.out.println("Algorithm terminated\n");
       System.out.println("--------------------\n");
       System.out.println("Calculating values of the quality measures\n");

       //Calculate the quality measures
       Files.writeFile(output_file_tra,Data.getHeader());
       Files.writeFile(output_file_tst,Data.getHeader());
       Calculate.Calculate(output_file_tra, output_file_tst, input_file_tra, input_file_tst, rule_file, qmeasure_file, Variables.getNLabel());

  }

}
