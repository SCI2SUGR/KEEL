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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Apriori;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.core.Files;

/**
 * <p> It gathers all the parameters, launches the algorithm, and prints out the results
	 *
 * @author Written by Alberto Fernández (University of Granada)
 * @author Modified by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @author Modified by Diana Martín (dmartin@ceis.cujae.edu.cu) 
 * @version 1.1
 * @since JDK1.6
 * </p>
 */
public class Apriori {
    

    private myDataset trans;
    
    private String rulesFilename;
    private String valuesFilename;
    private String valuesOrderFilename;
    private String fileTime, fileHora, namedataset;
    private AprioriProcess proc;
    private ArrayList<AssociationRule> associationRules;
	

    private int nPartitionForNumericAttributes;
    private double minSupport;
    private double minConfidence;
    long startTime, totalTime;
    
    
    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Apriori() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public Apriori(parseParameters parameters) {
    	this.startTime = System.currentTimeMillis();
    	
        this.rulesFilename = parameters.getAssociationRulesFile();
        this.valuesFilename = parameters.getOutputFile(0);
        this.valuesOrderFilename = parameters.getOutputFile(1);
        
        this.fileTime = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/time.txt";
        this.fileHora = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/hora.txt";

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
        	this.proc = new AprioriProcess(this.trans, this.minSupport, this.minConfidence);
        	this.proc.run();
        	this.associationRules = this.proc.generateRulesSet();
              	        	
			try {
				int r, i;
				ArrayList<Integer> terms;
				AssociationRule a_r;
				
				double[] step_values = this.trans.getSteps();
				
				PrintWriter rules_writer = new PrintWriter(this.rulesFilename);
				PrintWriter values_writer = new PrintWriter(this.valuesFilename);
				PrintWriter valuesOrder_writer = new PrintWriter(this.valuesOrderFilename);
				
				rules_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rules_writer.println("<rules>");
				
				values_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				values_writer.println("<values>");
				
				valuesOrder_writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				valuesOrder_writer.println("<values>");
				valuesOrder_writer.print("Support\tantecedent_support\tconsequent_support\tConfidence\tLift\tConv\tCF\tNetConf\tYulesQ\tnAttributes\n");
				
				for (r=0; r < this.associationRules.size(); r++) {
					a_r = this.associationRules.get(r);
					
					
					rules_writer.println("<rule id=\"" + r + "\">");
					values_writer.println("<rule id=\"" + r + "\" rule_support=\"" + AprioriProcess.roundDouble(a_r.getRuleSupport(),2) + "\" antecedent_support=\"" + AprioriProcess.roundDouble(a_r.getAntecedentSupport(),2)+ "\" consequent_support=\"" + AprioriProcess.roundDouble(a_r.getConsequentSupport(),2) + "\" confidence=\"" + AprioriProcess.roundDouble(a_r.getConfidence(),2) +"\" lift=\"" + AprioriProcess.roundDouble(a_r.getLift(),2) + "\" conviction=\"" + AprioriProcess.roundDouble(a_r.getConv(),2) + "\" certainFactor=\"" + AprioriProcess.roundDouble(a_r.getCF(),2) + "\" netConf=\"" + AprioriProcess.roundDouble(a_r.getNetConf(),2) + "\" yulesQ=\"" + AprioriProcess.roundDouble(a_r.getYulesQ(),2) + "\" nAttributes=\"" + (a_r.getAntecedent().size()+ a_r.getConsequent().size()) + "\"/>");
					
					rules_writer.println("<antecedents>");			
					terms = a_r.getAntecedent();
					
					for (i=0; i < terms.size(); i++)
						this.createRule(terms.get(i), step_values, rules_writer);
						
					rules_writer.println("</antecedents>");
					
					rules_writer.println("<consequents>");			
					terms = a_r.getConsequent();
					
					for (i=0; i < terms.size(); i++)
						this.createRule(terms.get(i), step_values, rules_writer);
					
					rules_writer.println("</consequents>");
					
					rules_writer.println("</rule>");
					valuesOrder_writer.print(printRule(a_r));
				}
				 
				rules_writer.println("</rules>");
				values_writer.println("</values>");
				valuesOrder_writer.print("</values>");
				
				this.proc.saveReport(this.associationRules, values_writer);
				
				rules_writer.close();
				values_writer.close();
				valuesOrder_writer.close();
				
				totalTime = System.currentTimeMillis() - startTime;
				this.writeTime();
				
				System.out.println("\nAlgorithm Finished");
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
        }
    }
    
       /**
   * <p>
   * Returns a String with relevant information regarding the mined association rule given
   * </p>
   * @param rule given association rule from which gathering relevant information
     * @return String with relevant information regarding the mined association rule given
   * 
   */
    public String printRule(AssociationRule rule) {
  	  int lenghtrule;
  	  String ruleString;

  	  ruleString = "";
  	 
  	  lenghtrule = rule.getAntecedent().size()+ rule.getConsequent().size();
  	  ruleString += ("" + AprioriProcess.roundDouble(rule.getRuleSupport(),2) + "\t" + AprioriProcess.roundDouble(rule.getAntecedentSupport(),2) + "\t" + AprioriProcess.roundDouble(rule.getConsequentSupport(),2) + "\t" + AprioriProcess.roundDouble(rule.getConfidence(),2) + "\t" + AprioriProcess.roundDouble(rule.getLift(),2) + "\t" + AprioriProcess.roundDouble(rule.getConv(),2) + "\t" + AprioriProcess.roundDouble(rule.getCF(),2) + "\t" + AprioriProcess.roundDouble(rule.getNetConf(),2) + "\t" + AprioriProcess.roundDouble(rule.getYulesQ(),2) + "\t" + lenghtrule + "\n");
  	  
  	 return ruleString;
    }
    
    /**
     * Prints a line with the time taken by the algorithm's execution on the output file.
     */
    public void writeTime() {
    	long seg, min, hor;
        String stringOut = new String("");

        stringOut = "" + totalTime / 1000 + "  " + this.namedataset + rulesFilename + "\n";
        Files.addToFile(this.fileTime, stringOut);
    	totalTime /= 1000;
    	seg = totalTime % 60;
    	totalTime /= 60;
    	min = totalTime % 60;
    	hor = totalTime / 60;
        stringOut = "";
    	
    	if (hor < 10)  stringOut = stringOut + "0"+ hor + ":";
    	else   stringOut = stringOut + hor + ":";

    	if (min < 10)  stringOut = stringOut + "0"+ min + ":";
    	else   stringOut = stringOut + min + ":";

    	if (seg < 10)  stringOut = stringOut + "0"+ seg;
    	else   stringOut = stringOut + seg;

    	stringOut = stringOut + "  " + rulesFilename + "\n";
        Files.addToFile(this.fileHora, stringOut);
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

