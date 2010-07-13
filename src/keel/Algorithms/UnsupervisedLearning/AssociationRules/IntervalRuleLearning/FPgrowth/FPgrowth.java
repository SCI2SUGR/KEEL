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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.FPgrowth;

/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.1
 * @since JDK1.6
 * </p>
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FPgrowth {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

    private myDataset trans;
    
    private String rulesFilename;
    private String valuesFilename;
    private FPgrowthProcess proc;
    private ArrayList<AssociationRule> associationRules;
	

    private int nPartitionForNumericAttributes;
    private double minSupport;
    private double minConfidence;
    
    
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public FPgrowth() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public FPgrowth(parseParameters parameters) {
    	
        this.rulesFilename = parameters.getAssociationRulesFile();
        this.valuesFilename = parameters.getOutputFile(0);
        
        this.nPartitionForNumericAttributes = Integer.parseInt(parameters.getParameter(0));
        
        try {
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
            
            this.trans = new myDataset(this.nPartitionForNumericAttributes);
            this.trans.readDataSet(parameters.getTransactionsInputFile());
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }

        this.minSupport = Double.parseDouble(parameters.getParameter(1));
        this.minConfidence = Double.parseDouble(parameters.getParameter(2));
    }

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } else {
        	this.proc = new FPgrowthProcess(this.trans, this.minSupport, this.minConfidence);
        	this.proc.run();
        	this.associationRules = this.proc.generateRulesSet();
        	this.proc.printReport(this.associationRules);
        	
        	/*for (int i=0; i < this.associationRules.size(); i++) {
        		System.out.println(this.associationRules.get(i));
        	}*/
        	        	
			try {
				int r, i;
				short[] terms;
				AssociationRule a_r;
				
				double[] step_values = this.trans.getSteps();
				ArrayList<Integer> id_attr_values = this.trans.getIDsOfAllAttributeValues();
				
				PrintWriter rules_writer = new PrintWriter(this.rulesFilename);
				PrintWriter values_writer = new PrintWriter(this.valuesFilename);
				
				rules_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rules_writer.println("<rules>");
				
				values_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				values_writer.println("<values>");
				
				for (r=0; r < this.associationRules.size(); r++) {
					a_r = this.associationRules.get(r);
					
					rules_writer.println("<rule id=\"" + r + "\">");
					values_writer.println("<rule id=\"" + r + "\" rule_support=\"" + a_r.getRuleSupport() + "\" antecedent_support=\"" + a_r.getAntecedentSupport() + "\" confidence=\"" + a_r.getConfidence() + "\"/>");
					
					rules_writer.println("<antecedents>");			
					terms = a_r.getAntecedent();
					
					for (i=0; i < terms.length; i++)
						this.createRule(id_attr_values.get(terms[i] - 1), step_values, rules_writer);
						
					rules_writer.println("</antecedents>");
					
					rules_writer.println("<consequents>");			
					terms = a_r.getConsequent();
					
					for (i=0; i < terms.length; i++)
						this.createRule(id_attr_values.get(terms[i] - 1), step_values, rules_writer);
					
					rules_writer.println("</consequents>");
					
					rules_writer.println("</rule>");
				}
				
				rules_writer.println("</rules>");
				values_writer.println("</values>");
				
				rules_writer.close();
				values_writer.close();
				
				System.out.println("\nAlgorithm Finished");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    }
    
    private void createRule(int fake_value, double[] step_values, PrintWriter w) {
    	int id_attr, true_value;
    	
    	id_attr = fake_value % trans.getnVars();
		true_value = (fake_value - id_attr) / trans.getnVars();
		
		w.print("<attribute name=\"" + trans.getAttributeName(id_attr) + "\" value=\"");
		
		if (trans.getAttributeType(id_attr) == myDataset.NOMINAL) w.print( trans.getNominalValue(id_attr, true_value) );
		else w.print("[" + (this.trans.getMin(id_attr) + step_values[id_attr] * true_value) + ", " + (this.trans.getMin(id_attr) + step_values[id_attr] * (true_value + 1)) + "]");
		
		w.println("\"/>");
    }
    
}

