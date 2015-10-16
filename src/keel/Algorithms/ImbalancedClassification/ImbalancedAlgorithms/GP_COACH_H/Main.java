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
 * <p>Title: Main Class of the Program</p>
 *
 * <p>Description: It reads the configuration file (data-set files and parameters) and launch the GP-COACH algorithm</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Victoria Lopez (University of Granada) 11/01/2011
 * @version 1.0
 */

package keel.Algorithms.ImbalancedClassification.ImbalancedAlgorithms.GP_COACH_H;

import org.core.Files;
import java.io.File;

/**
 * <p>Title: Main Class of the Program</p>
 *
 * <p>Description: It reads the configuration file (data-set files and parameters) and launch the GP-COACH-H algorithm</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Victoria Lopez (University of Granada) 11/01/2011
 * @version 1.0
 */
public class Main {

	private parseParameters parametersAll;
	
    /** Default Constructor */
    public Main() {
    }

    /**
     * It launches first the SMOTE algorithm, and then, the GP-COACH-H algorithm
     * @param confFile String it is the filename of the configuration file.
     */
    private void execute(String confFile) {
    	SMOTE smote;
    	parseParameters parametersGPCOACHH;
    	String fileSMOTE = "";
    	String fileGPCOACHH = "";
    	String fileConfName = "";
    	String smt_tr;
    	String smt_tst;
    	
    	parametersAll = new parseParameters();
    	parametersAll.parseConfigurationFile(confFile);
        
    	smt_tr = parametersAll.getTrainingInputFile().substring(0, parametersAll.getTrainingInputFile().length()-4) + "-SMOTE.dat";
    	smt_tst = parametersAll.getTestInputFile().substring(0, parametersAll.getTrainingInputFile().length()-4) + "-SMOTE.dat";
    	fileConfName = parametersAll.getTrainingInputFile().substring(parametersAll.getTrainingInputFile().lastIndexOf("/")+1, parametersAll.getTrainingInputFile().lastIndexOf("tra"));
        	
    	fileSMOTE += "algorithm = SMOTE\ninputData = \"" + parametersAll.getTrainingInputFile() + "\" \"" + parametersAll.getTestInputFile() + "\"\n";
    	fileSMOTE += "outputData = \"" + smt_tr + "\" \"" + smt_tst + "\"\n\n";
    	fileSMOTE += "seed = " + Long.parseLong(parametersAll.getParameter(0)) + "\n";
    	fileSMOTE += "Number of Neighbors = " + Integer.parseInt(parametersAll.getParameter(1)) + "\n";
    	fileSMOTE += "Type of SMOTE = " + parametersAll.getParameter(2) + "\n";
    	fileSMOTE += "Balancing = " + parametersAll.getParameter(3) + "\n";
    	fileSMOTE += "Quantity of generated examples = " + Integer.parseInt(parametersAll.getParameter(4)) + "\n";
    	fileSMOTE += "Distance Function = " + parametersAll.getParameter(5) + "\n";
    	fileSMOTE += "Type of Interpolation = " + parametersAll.getParameter(6) + "\n";
    	fileSMOTE += "Alpha = " + Double.parseDouble(parametersAll.getParameter(7)) + "\n";
    	fileSMOTE += "Mu = " + Double.parseDouble(parametersAll.getParameter(8)) + "\n";

    	Files.writeFile(fileConfName + "_SMOTE_cfg.txt", fileSMOTE);
    	smote = new SMOTE (fileConfName + "_SMOTE_cfg.txt");
        smote.run();
        
        fileGPCOACHH += "algorithm = Hierarchical genetic programming fuzzy rule based classification system with rule selection and tuning (GP-COACH-H)\n";
        fileGPCOACHH += "inputData = \"" + smt_tr + "\" \"" + parametersAll.getValidationInputFile() + "\" \"" + smt_tst + "\"\n";
        fileGPCOACHH += "outputData = \"" + parametersAll.getTrainingOutputFile() + "\" \"" + parametersAll.getTestOutputFile() + "\" \"" + parametersAll.getOutputFile(0) + "\" \"" + parametersAll.getOutputFile(1) + "\"\n\n";
        fileGPCOACHH += "seed = " + Long.parseLong(parametersAll.getParameter(0)) + "\n";
        fileGPCOACHH += "Number of Labels = " + Integer.parseInt(parametersAll.getParameter(9)) + "\n";
        fileGPCOACHH += "T-norm/T-conorm for the Computation of the Compatibility Degree = " + parametersAll.getParameter(10) + "\n";
        fileGPCOACHH += "Rule Weight = " + parametersAll.getParameter(11) + "\n";
        fileGPCOACHH += "Fuzzy Reasoning Method = " + parametersAll.getParameter(12) + "\n";
        fileGPCOACHH += "Number of Generations = " + Integer.parseInt(parametersAll.getParameter(13)) + "\n";
        fileGPCOACHH += "Initial Number of Fuzzy Rules (0 for 5*n_var) = " + Integer.parseInt(parametersAll.getParameter(14)) + "\n";
        fileGPCOACHH += "Alpha Raw Fitness = " + Double.parseDouble(parametersAll.getParameter(15)) + "\n";
        fileGPCOACHH += "Crossover probability = " + Double.parseDouble(parametersAll.getParameter(16)) + "\n";
        fileGPCOACHH += "Mutation probability = " + Double.parseDouble(parametersAll.getParameter(17)) + "\n";
        fileGPCOACHH += "Insertion probability = " + Double.parseDouble(parametersAll.getParameter(18)) + "\n";
        fileGPCOACHH += "Dropping Condition probability = " + Double.parseDouble(parametersAll.getParameter(19)) + "\n";
        fileGPCOACHH += "Tournament Selection Size = " + Integer.parseInt(parametersAll.getParameter(20)) + "\n";
        fileGPCOACHH += "Global Fitness Weight 1 = " + Double.parseDouble(parametersAll.getParameter(21)) + "\n";
        fileGPCOACHH += "Global Fitness Weight 2 = " + Double.parseDouble(parametersAll.getParameter(22)) + "\n";
        fileGPCOACHH += "Global Fitness Weight 3 = " + Double.parseDouble(parametersAll.getParameter(23)) + "\n";
        fileGPCOACHH += "Global Fitness Weight 4 = " + Double.parseDouble(parametersAll.getParameter(24)) + "\n";
        fileGPCOACHH += "Alpha Hierarchical Procedure = " + Double.parseDouble(parametersAll.getParameter(25)) + "\n";
        fileGPCOACHH += "CHC Number of Evaluations = " + Integer.parseInt(parametersAll.getParameter(26)) + "\n";
        fileGPCOACHH += "CHC Population Size = " + Integer.parseInt(parametersAll.getParameter(27)) + "\n";
        fileGPCOACHH += "CHC Number of Bits per Gen = " + Integer.parseInt(parametersAll.getParameter(28)) + "\n";

    	Files.writeFile(fileConfName + "_GPCOACHH_cfg.txt", fileGPCOACHH);
    	
        parametersGPCOACHH = new parseParameters();
        parametersGPCOACHH.parseConfigurationFile(fileConfName + "_GPCOACHH_cfg.txt");
            	
        GP_COACH_H method = new GP_COACH_H(parametersGPCOACHH);
        method.execute();
        
        // Delete auxiliary files
    	try{
    		(new File(fileConfName + "_SMOTE_cfg.txt")).delete();
    		(new File(fileConfName + "_GPCOACHH_cfg.txt")).delete();
    		(new File(smt_tr)).delete();
    		(new File(smt_tst)).delete();
    	}catch(Exception e){
     		e.printStackTrace();
     	}
    }

    /**
     * Main Program
     * @param args It contains the name of the configuration file<br/>
     * Format:<br/>
     * <em>algorithm = &lt;algorithm name></em><br/>
     * <em>inputData = "&lt;training file&gt;" "&lt;validation file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <em>outputData = "&lt;training file&gt;" "&lt;test file&gt;"</em> ...<br/>
     * <br/>
     * <em>seed = value</em> (if used)<br/>
     * <em>&lt;Parameter1&gt; = &lt;value1&gt;</em><br/>
     * <em>&lt;Parameter2&gt; = &lt;value2&gt;</em> ... <br/>
     */
    public static void main(String args[]) {
        long t_ini = System.currentTimeMillis();    	
        Main program = new Main();
        System.out.println("Executing Algorithm.");
        program.execute(args[0]);
        long t_fin = System.currentTimeMillis();
        long t_exec = t_fin - t_ini;
        long hours = t_exec / 3600000;
        long rest = t_exec % 3600000;
        long minutes = rest / 60000;
        rest %= 60000;
        long seconds = rest / 1000;
        rest %= 1000;
        System.out.println("Execution Time: " + hours + ":" + minutes + ":" +
                           seconds + "." + rest);        
    }

}

