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
* @author Written by Luciano S�nchez (University of Oviedo) 10/03/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @author Modified by Victoria Lopez (University of Granada) 06/06/2010
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.Parsing;

import keel.Algorithms.Fuzzy_Rule_Learning.Shared.Fuzzy.*;
import keel.Dataset.*;

import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;


public class ProcessConfig {
	/**
	 * <p>
	 * Class that process the configuration file for KEEL algorithms.
	 * </p>
	 * 
	 */

    public final static int IndexTrain = 0; // First file in inputData
    public final static int IndexTestKMeans = 1; // Test file in clustering algorithms
    public final static int IndexTest = 2; // It ignores the repetition of train file
    /**
     * <p> 
     * Constructor that initializes input/output parameters.
     * </p>
     */
    public ProcessConfig() {
        parInputData = new Vector();
        parInputData.add("noasignado");
        parInputData.add("noasignado");
        parOutputData = new Vector();
        outputData = new Vector();

    }

    class SyntaxError extends Exception {
    	/**
    	  * <p>
    	  * This exception is to report a syntaxes error.
    	  * </p>
    	  */
    	 /**
    	  * <p>
    	  * Creates an new SyntaxError object calling the super() method();
    	  * 
    	  * </p>	
    	  */
        SyntaxError() {
            super();
        }
        /**
         * <p>
         * 
         * Creates an new SyntaxError object calling the super(s) method() with the report string s.
         * </p>	
         *
         * @param s the report String.
         */
        SyntaxError(String s) {
            super(s);
        }
    }


    // Internal parameters
    private boolean commandLineParameters = false;
    private boolean createResultFile = false; 		// Results File

    // Configuration parameters for algorithms
    public static boolean parNewFormat = true; 		// Keel format or not
    public static int parAlgorithmType = 0; 		// Algorithm to execute    
    public static Vector parInputData; 				// Train and Test names
    public static Vector parOutputData;
    public static Vector outputData; 				// NEW: All the output files are stored in this vector (in the doOutputData method)

    public static String parResultTrainName = "resultTrain.log"; // Result file name for Trail file
    public static String parResultName = "resultTest.log"; // Global results file
    public static String parResultLabel;    // Results label
    public static int parPartitionLabelNum; // Partition label
    public static int parPopSize = 30;     // Population size
    public static int parIslandNumber = 5; // Number of populations
    public static boolean parSteady; // Steady or not
    public static int parIterNumber = 100; // Number of iterations (generations of crosses)
    public static int parTourSize = 4; // Tournament size
    public static double parMutProb = 0.01; // Mutation probability
    public static double parMutAmpl = 0.1; // Mutation amplitude
    public static double parMigProb = 0.001; // Migration probability
    public static double parLoProb = 0; // Local Optimization probability
    public static int parLoIterNumber = 2; // NUmber of iterations for Local Optimization * used nvar
    public static int parLoId = OperatorIdent.AMEBA; // Local Optimization Algorithm
    public static int parMaxHeigth = 10; // Maximum height for each individual
    public static boolean parNiche = false; // Using GA-P niches
    public static int parMaxNiche = 10; // Maximum number of individuls by niche
    public static double parIntraNicheProb = 0.5; // Crossing probability intra-niche
    public static double parDeltaFit = 1; // Waited fitness increment for a SAP overcrossing
    public static double parP0 = 0.25; // Accepting probability -deltafit on 0 iteration SAP
    public static double parP1 = 0.01; // Accepting probability -deltafit on iteration SAP
    public static int parNSUB = 25; // Number of iterations for each temperature SAP
    public static double parCrGAProb = 0.5; // GA Cross probability
    public static double parMuGAProb = 0.5; // GA Mutation probability
    public static int parRuleNumber = 5; // Number of rules (Boosting and FSS98)
    public static int parCrossId1 = OperatorIdent.GENERICROSSOVER; // Type of cross operator
    public static int parMutaId1 = OperatorIdent.GENERICMUTATION; // Type of mutation operator
    public static int parCrossId2 = OperatorIdent.GAPCROSSGA; // // Type of cross operator GAP
    public static int parMutaId2 = OperatorIdent.GAPMUTAGA; // Type of mutation operator GAP
    public static int parCrossId3 = OperatorIdent.GAPCROSSGP; // // Type of cross operator GAP
    public static int parMutaId3 = OperatorIdent.GAPMUTAGP; // Type of mutation operator GAP
    public static int parFitnessType = OperatorIdent.GI_STANDARD; // Type of fitness
    public static int[] parNetTopo; // Neural Network topology
    public static double parKernel = 1; // Kernel parameter
    public static int parNMeans = 1; // Number of means
    public static int parGALen = 10; // Number of parameters for GA string (GAP)
    public static double parSigma = 0.0001; // Initial Covariance in FSS98
    public static double parSignificanceLevel = 0.95; // Contrast Significance Level
    public static long parSeed = 1; // Random seed
    public static int parNClusters = 1; // Number of cluster in clustering problems
    public static double fuzzyTolerance = 0.1; // Tolerance added to input examples in Fuzzy Symbolic Regression

    public static String tableType1 = "YES";
    public static String tableType2 = "YES";
    public static String tableType3 = "YES";
    public static int numberLine = 2;
    public static int numberLine1 = 1;
    public static int numberLine2 = 1;
    public static int numberLine3 = 1;
    public static String dataTable1 = "TEST-TRAIN";
    public static String dataTable2 = "TEST-TRAIN";
    public static String dataTable3 = "TEST-TRAIN";
    public static String dataMatrix = "TEST-TRAIN";
    public static String matrixConfussion = "YES";
    public static int numDataset = 0;
    public static int curDataset = 0;
    public static String nameFile;

    public static boolean Iman;
    public static boolean Nem;
    public static boolean Bon;
    public static boolean Holm;
    public static boolean Hoch;
    public static boolean Hommel;
    public static boolean Scha;
    public static boolean Berg;
    public static boolean Holland;
    public static boolean Rom;
    public static boolean Finner;
    public static boolean Li;
    public static int imbalancedMeasure;
    public static final int AUC = 1;
    public static final int GMEAN = 2;
    public static final int STANDARDACCURACY = 3;
    private int nl;

    /**
     * <p>
     * method that process the configuration file nfconfig. 
     * </p>
     * @param nfconfig name of the file to be configured.
     * @return  a value of 0 if function returns successfully, 
                On an unsuccessful call, a negative value is returned. 
     */
    public int fileProcess(String nfconfig) {
    	
        try {
        	
            File f1 = new File(nfconfig);
            System.out.println(f1.getAbsolutePath());
            System.out.println(f1.getParent());

            numDataset = (new File(f1.getParent())).listFiles().length;

            StringTokenizer tokens_name;
            String aux = new String();
            tokens_name = new StringTokenizer(nfconfig, "/");

            while (tokens_name.hasMoreTokens()) {
                aux = tokens_name.nextToken();
                System.out.println("Aux : " + aux);
            }

            curDataset = Integer.parseInt(aux.substring(aux.indexOf("g") + 1,
                    aux.indexOf("s"))) + 1;

            String linea;
            nl = 0;
            StringTokenizer tokens;
           
            BufferedReader in = new BufferedReader(new FileReader(nfconfig));
            do {
                //next line file
                linea = in.readLine();
                if (linea == null) {
                    break; // EOF
                }

                nl++;
                String lineCopy = linea.trim();
                if (lineCopy.length() == 0) {
                    continue; // Empty line (CR)
                }
                if (lineCopy.charAt(0) == '#') {
                    continue;
                }

                tokens = new StringTokenizer(linea, " ,\t");
                if (!tokens.hasMoreTokens()) {
                    continue;
                }

                String tmp;
                tmp = tokens.nextToken();

                // Skip "="
                String equal = tokens.nextToken();

                if (tmp.equalsIgnoreCase("algorithm")) {
                    doAlgorithmDesc(tokens);
                } else if (tmp.equalsIgnoreCase("subAlgorithm")) {
                    doAlgorithmType(tokens);
                } else if (tmp.equalsIgnoreCase("dataformat")) {
                    doDataFormat(tokens);
                } else if (tmp.equalsIgnoreCase("InputData")) {
                    doInputData(tokens);
                } else if (tmp.equalsIgnoreCase("OutputData")) {
                    doOutputData(tokens);
                } else if (tmp.equalsIgnoreCase("OutLabel")) {
                    doResLabel(tokens);
                } else if (tmp.equalsIgnoreCase("OutputDataTabular")) {
                    doOutputDataTabular(tokens);
                } else if (tmp.equalsIgnoreCase("NumLabels")) {
                    doLabelNum(tokens);
                } else if (tmp.equalsIgnoreCase("PopSize")) {
                    doPopSize(tokens);
                } else if (tmp.equalsIgnoreCase("NumIsland")) {
                    doNumIslands(tokens);
                } else if (tmp.equalsIgnoreCase("Steady")) {
                    doSteady(tokens);
                } else if (tmp.equalsIgnoreCase("NumItera")) {
                    doNumItera(tokens);
                } else if (tmp.equalsIgnoreCase("TourSize")) {
                    doTourSize(tokens);
                } else if (tmp.equalsIgnoreCase("ProbMuta")) {
                    doMutaProb(tokens);
                } else if (tmp.equalsIgnoreCase("AmplMuta")) {
                    doMutaAmpl(tokens);
                } else if (tmp.equalsIgnoreCase("ProbMigra")) {
                    doMigraProb(tokens);
                } else if (tmp.equalsIgnoreCase("ProbOptimLocal")) {
                    doLocalOptimProb(tokens);
                } else if (tmp.equalsIgnoreCase("NumOptimLocal")) {
                    doLocalOptimNum(tokens);
                } else if (tmp.equalsIgnoreCase("IdOptimLocal")) {
                    doLocalOptimId(tokens);
                } else if (tmp.equalsIgnoreCase("NichingGAP")) {
                    doNiche(tokens);
                } else if (tmp.equalsIgnoreCase("MaxIndNiche")) {
                    doMaxIndNiche(tokens);
                } else if (tmp.equalsIgnoreCase("probIntraNiche")) {
                    doIntraNicheProb(tokens);
                } else if (tmp.equalsIgnoreCase("deltaFitSAP")) {
                    dodeltaFitSAP(tokens);
                } else if (tmp.equalsIgnoreCase("P0SAP")) {
                    doP0SAP(tokens);
                } else if (tmp.equalsIgnoreCase("P1SAP")) {
                    doP1SAP(tokens);
                } else if (tmp.equalsIgnoreCase("NSUBSAP")) {
                    doNSUBSAP(tokens);
                } else if (tmp.equalsIgnoreCase("ProbCrossGA")) {
                    doCrossProbGA(tokens);
                } else if (tmp.equalsIgnoreCase("ProbMutaGA")) {
                    doMutaProbGA(tokens);
                } else if (tmp.equalsIgnoreCase("NumRules")) {
                    doRuleNumberBoost(tokens);
                } else if (tmp.equalsIgnoreCase("TopologyMLP")) {
                    doTopoMLP(tokens);
                } else if (tmp.equalsIgnoreCase("sigmaKernel")) {
                    dosigmaKernel(tokens);
                } else if (tmp.equalsIgnoreCase("NumNeigb")) {
                    doNumNeigb(tokens);
                } else if (tmp.equalsIgnoreCase("LenChainGAP")) {
                    doLenChainGAP(tokens);
                } else if (tmp.equalsIgnoreCase("SigmaFSS98")) {
                    doSigmaFSS98(tokens);
                } else if (tmp.equalsIgnoreCase("SignLevel")) {
                    doSignificanceLevel(tokens);
                } else if (tmp.equalsIgnoreCase("MaxTreeHeight")) {
                    doSignificanceLevel(tokens);
                } else if (tmp.equalsIgnoreCase("Seed")) {
                    doSeed(tokens);
                } else if (tmp.equalsIgnoreCase("nInput")) {
                    doNInput(tokens);
                } else if (tmp.equalsIgnoreCase("nClusters")) {
                    doNClusters(tokens);
                } else if (tmp.equalsIgnoreCase("1-Classification")) {
                    doTableType1(tokens);
                } else if (tmp.equalsIgnoreCase("1-RMS")) {
                    doTableType1(tokens);
                } else if (tmp.equalsIgnoreCase("2-Classification")) {
                    doTableType2(tokens);
                } else if (tmp.equalsIgnoreCase("3-Global")) {
                    doTableType3(tokens);
                } else if (tmp.equalsIgnoreCase("numberLine")) {
                    doNumberLine(tokens);
                } else if (tmp.equalsIgnoreCase("1-Header")) {
                    doNumberLine1(tokens);
                } else if (tmp.equalsIgnoreCase("2-Header")) {
                    doNumberLine2(tokens);
                } else if (tmp.equalsIgnoreCase("3-Header")) {
                    doNumberLine3(tokens);
                } else if (tmp.equalsIgnoreCase("4-Confussion")) {
                    doMatrixConfussion(tokens);
                } else if (tmp.equalsIgnoreCase("1-Data")) {
                    doDataTable1(tokens);
                } else if (tmp.equalsIgnoreCase("2-Global")) {
                    doTableType2(tokens);
                } else if (tmp.equalsIgnoreCase("2-Data")) {
                    doDataTable2(tokens);
                } else if (tmp.equalsIgnoreCase("3-Data")) {
                    doDataTable3(tokens);
                } else if (tmp.equalsIgnoreCase("4-Data")) {
                    doDataMatrix(tokens);
                } else if (tmp.equalsIgnoreCase("Apply-Iman-Davenport")) {
                    doImanDavenport(tokens); 	//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Nemenyi")) {
                    doNemenyi(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Bonferroni-Dunn")) {
                    doBonferroni(tokens); 		//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Holm")) {
                    doHolm(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Hochberg")) {
                    doHoch(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Hommel")) {
                    doHommel(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Holland")) {
                    doHolland(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Rom")) {
                    doRom(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Finner")) {
                    doFinner(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Li")) {
                    doLi(tokens); 			//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Shaffer")) {
                    doSchaffer(tokens); 	//Every post-hoc is read for Friedman test
                } else if (tmp.equalsIgnoreCase("Apply-Bergman")) {
                    doBergman(tokens); 		//Every post-hoc is read for Friedman test
				} else if (tmp.equalsIgnoreCase("imbalancedMeasure")) {
                    doImbalancedMeasure(tokens); 		//This data is read for Vis-Imb-Check, Vis-Imb-General, Vis-Imb-Tabular
                }else {
                    throw new SyntaxError("Parametro desconocido " + tmp);
                }

            } while (true);

        } catch (FileNotFoundException e) {
            System.err.println(e + " Fichero de configuracion no encontrado");
            return -1;
        } catch (IOException e) {
            System.err.println(e + " Error lectura");
            return -2;
        } catch (SyntaxError e) {
            System.err.println("Error de sintaxis en linea " + nl + " : " + e);
            return -3;
        } catch (Exception e) {
            System.err.println("Error en fichero de configuracion");
            return -4;
        }
        return 0;

    }
 
    /**
     * <p>
     * Prints to standard output the algorithm description in the configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doAlgorithmDesc(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Descripcion de algoritmo no especificada");
        }
        String tmp = tokens.nextToken();
        System.out.println("Descripcion algoritmo = " + tmp);
    }
    /**
     * <p>
     * Sets the class member parTipoAlgoritmo with the constant assigned to algorithm specified in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doAlgorithmType(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("TipoAlgoritmo no especificado");
        }
        String tmp = tokens.nextToken();
        if (tmp.equalsIgnoreCase("ClasifFuzzyWangMendel")) {
            parAlgorithmType = 1;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyPitts")) {
            parAlgorithmType = 2;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyGP")) {
            parAlgorithmType = 3;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyGAP")) {
            parAlgorithmType = 4;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzySAP")) {
            parAlgorithmType = 5;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyAdaBoost")) {
            parAlgorithmType = 6;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyLogitBoost")) {
            parAlgorithmType = 7;
        } else if (tmp.equalsIgnoreCase("ClasifFuzzyMaxLogitBoost")) {
            parAlgorithmType = 8;
        } else if (tmp.equalsIgnoreCase("ClasifMLPerceptron")) {
            parAlgorithmType = 27;
        } else if (tmp.equalsIgnoreCase("ClasifLinearLMS")) {
            parAlgorithmType = 25;
        } else if (tmp.equalsIgnoreCase("ClasifPolQuadraticLMS")) {
            parAlgorithmType = 26;
        } else if (tmp.equalsIgnoreCase("ClasifADLinear")) {
            parAlgorithmType = 23;
        } else if (tmp.equalsIgnoreCase("ClasifADQuadratic")) {
            parAlgorithmType = 24;
        } else if (tmp.equalsIgnoreCase("ClasifKernel")) {
            parAlgorithmType = 28;
        } else if (tmp.equalsIgnoreCase("ClasifKNN")) {
            parAlgorithmType = 29;
        } else if (tmp.equalsIgnoreCase("ModelFuzzyWangMendel")) {
            parAlgorithmType = 9;
        } else if (tmp.equalsIgnoreCase("ModelFuzzyPitts")) {
            parAlgorithmType = 10;
        } else if (tmp.equalsIgnoreCase("ModelFuzzyGP")) {
            parAlgorithmType = 11;
        } else if (tmp.equalsIgnoreCase("ModelFuzzyGAP")) {
            parAlgorithmType = 12;
        } else if (tmp.equalsIgnoreCase("ModelFuzzySAP")) {
            parAlgorithmType = 13;
        } else if (tmp.equalsIgnoreCase("ModelGAPRegSym")) {
            parAlgorithmType = 14;
        } else if (tmp.equalsIgnoreCase("ModelIntervalGAPRegSym")) {
            parAlgorithmType = 15;
        } else if (tmp.equalsIgnoreCase("ModelFuzzyGAPRegSym")) {
            parAlgorithmType = 16;
        } else if (tmp.equalsIgnoreCase("ModelSAPRegSym")) {
            parAlgorithmType = 17;
        } else if (tmp.equalsIgnoreCase("ModelIntervalSAPRegSym")) {
            parAlgorithmType = 18;
        } else if (tmp.equalsIgnoreCase("ModelFuzzySAPRegSym")) {
            parAlgorithmType = 19;
        } else if (tmp.equalsIgnoreCase("ModelMLPerceptron")) {
            parAlgorithmType = 22;
        } else if (tmp.equalsIgnoreCase("ModelLinearLMS")) {
            parAlgorithmType = 20;
        } else if (tmp.equalsIgnoreCase("ModelPolQuadraticLMS")) {
            parAlgorithmType = 21;
        } else if (tmp.equalsIgnoreCase("ModelFSS98")) {
            parAlgorithmType = 30;
        } else if (tmp.equalsIgnoreCase("StatTestClas5x2cv")) {
            parAlgorithmType = 31;
        } else if (tmp.equalsIgnoreCase("StatTestMod5x2cv")) {
            parAlgorithmType = 32;
        } else if (tmp.equalsIgnoreCase("StatTestClast")) {
            parAlgorithmType = 33;
        } else if (tmp.equalsIgnoreCase("StatTestModt")) {
            parAlgorithmType = 34;
        } else if (tmp.equalsIgnoreCase("StatTestClasSW")) {
            parAlgorithmType = 35;
        } else if (tmp.equalsIgnoreCase("StatTestModSW")) {
            parAlgorithmType = 36;
        } else if (tmp.equalsIgnoreCase("StatTestClasRS")) {
            parAlgorithmType = 37;
        } else if (tmp.equalsIgnoreCase("StatTestModRS")) {
            parAlgorithmType = 38;
        } else if (tmp.equalsIgnoreCase("StatTestClasU")) {
            parAlgorithmType = 39;
        } else if (tmp.equalsIgnoreCase("StatTestModU")) {
            parAlgorithmType = 40;
        } else if (tmp.equalsIgnoreCase("StatTestClasF")) {
            parAlgorithmType = 41;
        } else if (tmp.equalsIgnoreCase("StatTestModF")) {
            parAlgorithmType = 42;
        } else if (tmp.equalsIgnoreCase("StatCheckCL")) {
            parAlgorithmType = 43;
        } else if (tmp.equalsIgnoreCase("StatCheckMO")) {
            parAlgorithmType = 44;
        } else if (tmp.equalsIgnoreCase("StatGeneralCL")) {
            parAlgorithmType = 45;
        } else if (tmp.equalsIgnoreCase("StatGeneralMO")) {
            parAlgorithmType = 46;
        } else if (tmp.equalsIgnoreCase("ClusterKMeans")) {
            parAlgorithmType = 47;
        } else if (tmp.equalsIgnoreCase("StatTabularCL")) {
            parAlgorithmType = 48;
        } else if (tmp.equalsIgnoreCase("StatTabularMO")) {
            parAlgorithmType = 49;
        } else if (tmp.equalsIgnoreCase("Wilcoxon")) {
            parAlgorithmType = 50;
        } else if (tmp.equalsIgnoreCase("Friedman")) {
            parAlgorithmType = 51;
        } else if (tmp.equalsIgnoreCase("Multiple")) {
            parAlgorithmType = 51;
            parAlgorithmType = 52;
        } else if (tmp.equalsIgnoreCase("ImbWilcoxon")) {
            parAlgorithmType = 53;
        } else if (tmp.equalsIgnoreCase("FriedmanImb")) {
            parAlgorithmType = 54;
        } else if (tmp.equalsIgnoreCase("StatTabularImb")) {
            parAlgorithmType = 55;
        } else if (tmp.equalsIgnoreCase("StatCheckImb")) {
            parAlgorithmType = 56;
        } else if (tmp.equalsIgnoreCase("StatGeneralImb")) {
            parAlgorithmType = 57;
        }else {
            throw new SyntaxError("TipoAlgoritmo " + tmp + " no reconocido");
        }

        System.out.println("Ejecutando algoritmo " + tmp);

    }
    /**
     * <p>
     * Sets the class member parFomatoNuevo with true if the data are specified in the keel new format; false otherwise.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doDataFormat(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Formato no especificado");
        }
        String tmp = tokens.nextToken();
        if (tmp.equalsIgnoreCase("keel")) {
            parNewFormat = true;
        } else {
            parNewFormat = false;
        }
    }
    /**
     * <p>
     * Input data are extracted from configuration file. 
     * First parameter is train file name, second parameter is test name in every algorithm except in statistical tests.
     * Train file name is repeated in stream.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doInputData(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("inputdata no especificado");
        }
        String linea = new String();
        while (tokens.hasMoreTokens()) {
            linea = linea + tokens.nextToken();
        }
        System.out.println("Procesando inputdata[" + linea + "]");
        String tmp;
        parInputData = new Vector();
        StringTokenizer tk = new StringTokenizer(linea, "\"");
        while (tk.hasMoreTokens()) {
            tmp = tk.nextToken();
            tmp = tmp.trim();
            if (tmp.length() > 0) {
                parInputData.add(tmp);
            }
        }
    }
    /**
     * <p>
     * Output data are extracted from configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doOutputData(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("OutputData no especificado");
        }
        String linea = new String();
        while (tokens.hasMoreTokens()) {
            linea = linea + tokens.nextToken();
        }
        System.out.println("Procesando argumento[" + linea + "]");
        String tmp;
        StringTokenizer tk = new StringTokenizer(linea, "\"");
        while (tk.hasMoreTokens()) {
            tmp = tk.nextToken();
            tmp = tmp.trim();
            if (tmp.length() > 0) {
                System.out.println("Nombre resultados train [" + tmp + "]");
                parResultTrainName = tmp;
                tmp = new String("");
                if (tk.hasMoreTokens()) {
                    tmp = tk.nextToken();
                    tmp = tmp.trim();
                }
                if (tmp.length() == 0) {
                    tmp = new String("result.log");
                }
                System.out.println("Nombre resultados test [" + tmp + "]");
                parResultName = tmp;

                break;
            }
        }
        createResultFile = true;

        //Nuevo: Para leer m�s ficheros de salida:
        while (tk.hasMoreTokens()) {
            tmp = tk.nextToken();
            tmp = tmp.trim();
            if (tmp.length() > 0) {
                outputData.add(tmp); //A�ado la ruta del fichero de salida
            }
        }

    }
    /**
     * <p>
     * Output data are extracted from configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doOutputDataTabular(StringTokenizer tokens) throws SyntaxError {

        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("inputdata no especificado");
        }
        String linea = new String();
        while (tokens.hasMoreTokens()) {
            linea = linea + tokens.nextToken();
        }
        System.out.println("Procesando outputdata[" + linea + "]");
        String tmp;
        parOutputData = new Vector();
        StringTokenizer tk = new StringTokenizer(linea, "\"");
        while (tk.hasMoreTokens()) {
            tmp = tk.nextToken();
            tmp = tmp.trim();
            if (tmp.length() > 0) {
                System.out.println("Procesando outputdata[" + tmp + "]");
                parOutputData.add(tmp);
            }
        }

        createResultFile = true;
    }
    /**
     * <p>
     * Sets the class member parResultName with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doAppNombreResult(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("result no especificado");
        }
        String tmp = tokens.nextToken();
        parResultName = tmp;
        createResultFile = false;
    }
    /**
     * <p>
     * Sets the class member parResultLabelwith with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doResLabel(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("etiqueta no especificado");
        }
        String tmp = tokens.nextToken();
        parResultLabel = tmp;
    }
    /**
     * <p>
     * Sets the class member parNumEtiqPar with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doLabelNum(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de etiquetas no especificado");
        }
        String tmp = tokens.nextToken();
        parPartitionLabelNum = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parTamPobla with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doPopSize(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Tamano Poblacion no especificado");
        }
        String tmp = tokens.nextToken();
        parPopSize = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parNumIslas with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doNumIslands(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de islas no especificado");
        }
        String tmp = tokens.nextToken();
        parIslandNumber = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parSteady with true if the next token in configuration file is 1; else otherwise.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doSteady(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Steady no especificado");
        }
        String tmp = tokens.nextToken();
        if (Integer.parseInt(tmp) == 0) {
            parSteady = false;
        } else {
            parSteady = true;
        }
    }
    /**
     * <p>
     * Sets the class member parNumItera with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doNumItera(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de iteraciones no especificado");
        }
        String tmp = tokens.nextToken();
        parIterNumber = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parTorneo with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doTourSize(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Torneo no especificado");
        }
        String tmp = tokens.nextToken();
        parTourSize = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parProbMuta with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doMutaProb(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Probabilidad de mutacion no especificado");
        }
        String tmp = tokens.nextToken();
        parMutProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parAmplMuta with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doMutaAmpl(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Amplitud de mutacion no especificado");
        }
        String tmp = tokens.nextToken();
        parMutAmpl = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parProbMig with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doMigraProb(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Probabilidad de migracion no especificado");
        }
        String tmp = tokens.nextToken();
        parMigProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parProbOl with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doLocalOptimProb(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Prob. de optim local no especificado");
        }
        String tmp = tokens.nextToken();
        parLoProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parNumOl with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doLocalOptimNum(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Iteraciones optim. local no especificado");
        }
        String tmp = tokens.nextToken();
        parLoIterNumber = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parIdOl with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doLocalOptimId(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Tipo de optim local no especificado");
        }
        String tmp = tokens.nextToken();
        parLoId = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parNichos with true if the next token in configuration file is 1; else otherwise.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doNiche(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de nichos no especificado");
        }
        String tmp = tokens.nextToken();
        int inichos = Integer.parseInt(tmp);
        if (inichos == 1) {
            parNiche = true;
        } else {
            parNiche = false;
        }
    }
    /**
     * <p>
     * Sets the class member parNichoMax with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doMaxIndNiche(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "Maximo de individuos en nicho no especificado");
        }
        String tmp = tokens.nextToken();
        parMaxNiche = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parProbIntra with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doIntraNicheProb(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Prob. de cruce intranicho no especificado");
        }
        String tmp = tokens.nextToken();
        parIntraNicheProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parDeltaFit with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void dodeltaFitSAP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("delta fitness SAP no especificado");
        }
        String tmp = tokens.nextToken();
        parDeltaFit = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parP0 with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doP0SAP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("P0 SAP no especificado");
        }
        String tmp = tokens.nextToken();
        parP0 = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parP1 with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doP1SAP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("P1 SAP no especificado");
        }
        String tmp = tokens.nextToken();
        parP1 = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parNSUB with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */   
    void doNSUBSAP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Iteraciones por T. SAP no especificado");
        }
        String tmp = tokens.nextToken();
        parNSUB = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parPrCrGA with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */   
    void doCrossProbGA(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Prob. Cruce GA no especificado");
        }
        String tmp = tokens.nextToken();
        parCrGAProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parPrMuGA with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */   
    void doMutaProbGA(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Prob. Muta GA no especificado");
        }
        String tmp = tokens.nextToken();
        parMuGAProb = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parNumReglas with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doRuleNumberBoost(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero reglas boosting no especificado");
        }
        String tmp = tokens.nextToken();
        parRuleNumber = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parAlturaMax with the next token in configuration file.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */     
    void doAlturaMax(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Altura maxima arbol no especificado");
        }
        String tmp = tokens.nextToken();
        parMaxHeigth = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parTopoNet with the next tokens in configuration file. This parameter is componed with the sizes 
     * of the hidden layers of a perceptron.
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void doTopoMLP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Topologia red neuronal no especificado");
        }
        Vector vtmp = new Vector();
        while (tokens.hasMoreTokens()) {
            String tmp = tokens.nextToken();
            int itmp = Integer.parseInt(tmp);
            vtmp.addElement(new Integer(itmp));
        }
        parNetTopo = new int[vtmp.size()];
        Integer Itmp;
        for (int i = 0; i < parNetTopo.length; i++) {
            Itmp = (Integer) vtmp.elementAt(i);
            parNetTopo[i] = Itmp.intValue();
        }

    }
    /**
     * <p>
     * Sets the class member parKernel with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void dosigmaKernel(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Sigma kernel no especificado");
        }
        String tmp = tokens.nextToken();
        parKernel = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parNMeans with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void doNumNeigb(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de vecinos no especificado");
        }
        String tmp = tokens.nextToken();
        parNMeans = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parLENCAD with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void doLenChainGAP(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Longitud cadena GAP no especificado");
        }
        String tmp = tokens.nextToken();
        parGALen = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member parSigma with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void doSigmaFSS98(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Sigma FSS98 no especificado");
        }
        String tmp = tokens.nextToken();
        parSigma = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parNivelSignifica with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */  
    void doSignificanceLevel(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Nivel de significacion no especificado");
        }
        String tmp = tokens.nextToken();
        parSignificanceLevel = Double.parseDouble(tmp);
    }
    /**
     * <p>
     * Sets the class member parSeed with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doSeed(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Semilla no especificada");
        }
        String tmp = tokens.nextToken();
        parSeed = Long.parseLong(tmp);
    }
    /**
     * <p>
     * This parameter is skipped by now. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNInput(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("nInput no especificado");
        }
        String tmp = tokens.nextToken();
        // We ignore the value, by now
    }
    /**
     * <p>
     * Sets the class member parNClusters with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNClusters(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Numero de clusters no especificado");
        }
        String tmp = tokens.nextToken();
        parNClusters = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member tableType1 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doTableType1(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 1 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        tableType1 = tmp;

    }
    /**
     * <p>
     * Sets the class member tableType2 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doTableType2(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 2 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        tableType2 = tmp;
    }
    /**
     * <p>
     * Sets the class member tableType3 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doTableType3(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 3 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        tableType3 = tmp;
    }
    /**
     * <p>
     * Sets the class member numberLine with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNumberLine(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "Elecci�n del n�mero de l�neas no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        numberLine = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member numberLine1 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNumberLine1(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "Elecci�n del n�mero de l�neas no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        numberLine1 = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member numberLine2 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNumberLine2(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "Elecci�n del n�mero de l�neas no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        numberLine2 = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member numberLine3 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNumberLine3(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "Elecci�n del n�mero de l�neas no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        numberLine3 = Integer.parseInt(tmp);
    }
    /**
     * <p>
     * Sets the class member matrixConfussion with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doMatrixConfussion(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 2 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        matrixConfussion = tmp;
    }
    /**
     * <p>
     * Sets the class member dataTable1 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doDataTable1(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 3 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        dataTable1 = tmp;
    }
    /**
     * <p>
     * Sets the class member dataTable2 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doDataTable2(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 3 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        dataTable2 = tmp;
    }
    /**
     * <p>
     * Sets the class member dataTable3 with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doDataTable3(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 3 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        dataTable3 = tmp;
    }
    /**
     * <p>
     * Sets the class member dataMatrix with the next tokens in configuration file. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doDataMatrix(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError("Elecci�n de la tabla 3 no especificada");
        }
        String tmp = tokens.nextToken();
        while (!tmp.equals("=")) {
            tmp = tokens.nextToken();
        }
        tmp = tokens.nextToken();
        dataMatrix = tmp;
    }

    /**
     * <p>
     * Sets the class member Iman with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is update with value 1. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doImanDavenport(StringTokenizer tokens) throws SyntaxError {
        this.parSignificanceLevel = 0;
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Iman = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Iman = true;
            parSignificanceLevel = 1;
        }
    }
    /**
     * <p>
     * Sets the class member Nem with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 2. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doNemenyi(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Nem = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Nem = true;
            parSignificanceLevel += 2;
        }
    }
    /**
     * <p>
     * Sets the class member Bon with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 4. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doBonferroni(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Bon = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Bon = true;
            parSignificanceLevel += 4;
        }
    }
    /**
     * <p>
     * Sets the class member Holm with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 8. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 

    void doHolm(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Holm = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Holm = true;
            parSignificanceLevel += 8;
        }
    }
    /**
     * <p>
     * Sets the class member Hoch with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 16.
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 

    void doHoch(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Hoch = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Hoch = true;
            parSignificanceLevel += 16;
        }
    }
    /**
     * <p>
     * Sets the class member Hommel with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 32. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doHommel(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Iman-Davenport Derivation'");
        }
        String tmp = tokens.nextToken();
        Hommel = false;
        if (tmp.equalsIgnoreCase("YES")) {
            Hommel = true;
            parSignificanceLevel += 32;
        }
    }
    
    /**
     * <p>
     * Sets the class member Schaffer with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 64. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doSchaffer(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Shaffer Method'");
        }
        String tmp = tokens.nextToken();
        Scha = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Scha = true;
            parSignificanceLevel += 64;
        }
    }
    
    /**
     * <p>
     * Sets the class member Bergman with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 128. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doBergman(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Bergman Method'");
        }
        String tmp = tokens.nextToken();
        Scha = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Scha = true;
            parSignificanceLevel += 128;
        }
    }
    
    /**
     * <p>
     * Sets the class member Holland with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 256. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doHolland(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Holland Method'");
        }
        String tmp = tokens.nextToken();
        Holland = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Holland = true;
            parSignificanceLevel += 256;
        }
    }
    
    /**
     * <p>
     * Sets the class member Rom with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 512. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doRom(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Rom Method'");
        }
        String tmp = tokens.nextToken();
        Rom = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Rom = true;
            parSignificanceLevel += 512;
        }
    }
    
    /**
     * <p>
     * Sets the class member Finner with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 1024. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doFinner(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Finner Method'");
        }
        String tmp = tokens.nextToken();
        Finner = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Finner = true;
            parSignificanceLevel += 1024;
        }
    }
    
    /**
     * <p>
     * Sets the class member Li with true if the next tokens in configuration file is 1; false otherwise. 
     * parNivelSignifica is incremented in 2048. 
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */ 
    void doLi(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'Apply Li Method'");
        }
        String tmp = tokens.nextToken();
        Li = false;
        if (tmp.equalsIgnoreCase("YES")) {
        	Li = true;
            parSignificanceLevel += 2048;
        }
	}
    /**
     * <p>
     * Sets the class member imbalancedMeasure with 1 if the measure is AUC, 2 if
     * the measure is g_mean and 3 if the measure is the standard accuracy
     * </p>
     * @param tokens tokens recover class connected to the configuration file.
     * @throws SyntaxError
     */
    void doImbalancedMeasure(StringTokenizer tokens) throws SyntaxError {
        if (!tokens.hasMoreTokens()) {
            throw new SyntaxError(
                    "No value selected for 'imbalancedMeasure'");
        }
        String tmp = tokens.nextToken();
        imbalancedMeasure = AUC;
        if (tmp.equalsIgnoreCase("AreaUndertheROCCurve")) {
            imbalancedMeasure = AUC;
        }
        else if(tmp.equalsIgnoreCase("GeometricMean")) {
            imbalancedMeasure = GMEAN;
        }
        else if (tmp.equalsIgnoreCase("StandardAccuracy")) {
            imbalancedMeasure = STANDARDACCURACY;
        }
    }

/**
 *<p>
 * Writes the result file with expected and obtained data for modelling problems.
 *</p>
 * @param expected expected data vector.
 * @param obtained expected data vector
 */
    private void createResults(double[] expected, double[] obtained) {
        FileOutputStream out;
        PrintStream p;

        try {

            out = new FileOutputStream(parResultName);
            p = new PrintStream(out);
            copyTestHeader(p);
            for (int i = 0; i < expected.length; i++) {
                p.print(expected[i] + " " + obtained[i] + "\n");
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultName);
        }

    }

    /**
     *<p>
     * Writes the result file with expected and obtained data for classification problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    private void createResults(int[] expected, int[] obtained) {
        FileOutputStream out;
        PrintStream p;
        Attribute at = Attributes.getOutputAttribute(0);

        // Check whether the output value is nominal or integer
        boolean isNominal = (at.getType() == at.NOMINAL);

        try {

            out = new FileOutputStream(parResultName);
            p = new PrintStream(out);
            copyTestHeader(p);
            for (int i = 0; i < expected.length; i++) {
                if (isNominal) {
                    String expectedLabel, obtainedLabel;

                    if (expected[i] == -1) {
                        expectedLabel = new String("unclassified");
                    } else {
                        expectedLabel = at.getNominalValue(expected[i]);
                    }

                    if (obtained[i] == -1) {
                        obtainedLabel = new String("unclassified");
                    } else {
                        obtainedLabel = at.getNominalValue(obtained[i]);
                    }

                    p.print(expectedLabel + " " + obtainedLabel + "\n");
                } else {
                    p.print(expected[i] + " " + obtained[i] + "\n");

                }
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultName);
        }

    }

    /**
     *<p>
     * Writes the result file with pattern and class data for clustering problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    private void createResults(double[][] pattern, int[] pclass) {
        FileOutputStream out;
        PrintStream p;

        try {

            out = new FileOutputStream(parResultName);
            p = new PrintStream(out);
            copyClusterHeader(p);
            p.println("@attribute cluster integer");
            p.println("@data");
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[i].length; j++) {
                    p.print(pattern[i][j] + ",");
                }
                p.print(pclass[i] + "\n");
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultName);
        }

    }
    
    /**
     *<p>
     * Writes the training result file with expected and obtained data for modelling problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    private void createTrainingResults(double[] expected, double[] obtained) {
        FileOutputStream out;
        PrintStream p;

        try {

            out = new FileOutputStream(parResultTrainName);
            p = new PrintStream(out);
            copyTestHeader(p);
            for (int i = 0; i < expected.length; i++) {
                p.print(expected[i] + " " + obtained[i] + "\n");
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultTrainName);
        }

    }

    /**
     *<p>
     * Writes the training result file with expected and obtained data for classification problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    private void createTrainingResults(int[] expected, int[] obtained) {
        FileOutputStream out;
        PrintStream p;
        Attribute at = Attributes.getOutputAttribute(0);

        // Check whether the output value is nominal or integer
        boolean isNominal = (at.getType() == at.NOMINAL);

        try {

            out = new FileOutputStream(parResultTrainName);
            p = new PrintStream(out);
            copyTestHeader(p);
            for (int i = 0; i < expected.length; i++) {
                if (isNominal) {
                    String labelDeseado, labelObtenido;

                    if (expected[i] == -1) {
                        labelDeseado = new String("unclassified");
                    } else {
                        labelDeseado = at.getNominalValue(expected[i]);
                    }

                    if (obtained[i] == -1) {
                        labelObtenido = new String("unclassified");
                    } else {
                        labelObtenido = at.getNominalValue(obtained[i]);
                    }

                    p.print(labelDeseado + " " + labelObtenido + "\n");
                } else {
                    p.print(expected[i] + " " + obtained[i] + "\n");

                }
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultTrainName);
        }

    }


    /**
     *<p>
     * Writes the training result file with pattern and class data for clustering problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    private void createTrainingResults(double[][] pattern, int[] pclass) {
        FileOutputStream out;
        PrintStream p;

        try {

            out = new FileOutputStream(parResultTrainName);
            p = new PrintStream(out);
            copyClusterHeader(p);
            p.println("@attribute cluster integer");
            p.println("@data");
            for (int i = 0; i < pattern.length; i++) {
                for (int j = 0; j < pattern[i].length; j++) {
                    p.print(pattern[i][j] + ",");
                }
                p.print(pclass[i] + "\n");
            }
            p.close();
        } catch (Exception e) {
            System.err.println("Error creando fichero de resultados " +
                               parResultTrainName);
        }

    }

/**
 * <p>
 *  adds a numerical value to results file.
 * </p>
 * @param test the value to be added.
 */
    private void appendResults(double test) {
        FileOutputStream out;
        PrintStream p;

        try {

            out = new FileOutputStream(parResultName, true);
            p = new PrintStream(out);
            p.print(test + " ");
            p.close();
        } catch (Exception e) {
            System.err.println(
                    "Error anhadiendo un dato al fichero de resultados");
        }

    }
/**
 * <p>
 *  print to stream p the header of configuration file stored in the class.
 * </p>
 * @param p the stream to print.
 */
    private void copyTestHeader(PrintStream p) {

        // Header of the output file
        p.println("@relation " + Attributes.getRelationName());
        p.print(Attributes.getInputAttributesHeader());
        p.print(Attributes.getOutputAttributesHeader());
        p.println(Attributes.getInputHeader());
        p.println(Attributes.getOutputHeader());
        p.println("@data");

    }
    /**
    * <p>
    *  print to stream p the cluster header (clustering problems) of configuration file stored in the class.
    *  
    * </p>
    * @param p the stream to print.
    */
    private void copyClusterHeader(PrintStream p) {

        // Header of the output file, clustering problems
        p.println("@relation " + Attributes.getRelationName());
        p.print(Attributes.getInputAttributesHeader());
        p.println(Attributes.getInputHeader());

    }

    private String lastRel = new String();
    /**
     * <p>
     *  Skips the header of the results file.
     *  
     * </p>
     * @param in the stream to read.
     */
    public String[] skipHeader(BufferedReader in) {

        String[] result = null;
        String line = new String();
        String lastAttribute = new String();
        int counter;

        try {
            do {
                in.mark(1024);
                line = in.readLine();
                line.trim();
                if (line.length() == 0) {
                    continue;
                }
                line = line.toLowerCase();
                if (line.startsWith("@attribute")) {
                    lastAttribute = line;
                }
                if (line.startsWith("@relation")) {
                    // Save the name of the relation, if changed
                    String[] ff = line.split(" ");
                    String ff1;
                    if (ff.length > 1) {
                        counter = 1;
                        while(
                             ((ff[counter].equals(" "))||(ff[counter].equals(""))||
                              (ff[counter].equals("  ")))&&counter<ff.length){
                            counter++;
                        }
                        
                        ff1 = ff[counter];
                        if (!ff1.equals(lastRel)) {
                            lastRel = ff1;
                        }
                    }
                }
            } while (line.indexOf('@') != -1);
            in.reset();

            // It generates string table with valid values for outputs

            // With { } is a list of values, else an integer or real
            int pos1 = lastAttribute.indexOf('{');
            int pos2 = lastAttribute.indexOf('}');
            if (pos1 >= 0 && pos2 >= 0) {
                // It's a list of values
                lastAttribute = lastAttribute.substring(pos1 + 1, pos2);
                result = lastAttribute.split(",");
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i].trim();
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Error saltando la cabecera del fichero de resultados");
        }

        return result;

    }

/**
 * <p>
 * Returns the last token after read the last relation in results file.
 * </p>
 * @return the last token after read the last relation in results file.
 */
    public String getRelation() {
        return lastRel;
    }
    /**
     *<p>
     * Writes the result file with expected and obtained data for modelling problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    public void results(double[] expected, double[] obtained) {
        createResults(expected, obtained);
    }
    /**
     *<p>
     * Writes the result file with expected and obtained data for classification problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    public void results(int[] expected, int[] obtained) {
        createResults(expected, obtained);
    }
    /**
     *<p>
     * Writes the result file with pattern and obtained data for clustering problems.
     *</p>
     * @param pattern expected data vector.
     * @param obtained expected data vector
     */
    public void results(double[][] pattern, int[] obtained) {
        createResults(pattern, obtained);
    }

    /**
     *<p>
     * Writes the training result file with expected and obtained data for modelling problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    public void trainingResults(double[] expected, double[] obtained) {
        createTrainingResults(expected, obtained);
    }
    /**
     *<p>
     * Writes the training result file with expected and obtained data for classification problems.
     *</p>
     * @param expected expected data vector.
     * @param obtained expected data vector
     */
    public void trainingResults(int[] expected, int[] obtained) {
        createTrainingResults(expected, obtained);
    }
    /**
     *<p>
     * Writes the training result file with pattern and obtained data for clustering problems.
     *</p>
     * @param pattern expected data vector.
     * @param obtained expected data vector
     */
    public void trainingResults(double[][] pattern, int[] obtained) {
        createTrainingResults(pattern, obtained);
    }


}
