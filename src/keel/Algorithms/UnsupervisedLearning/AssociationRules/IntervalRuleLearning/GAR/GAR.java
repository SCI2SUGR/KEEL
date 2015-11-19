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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.GAR;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.core.*;

import keel.Dataset.*;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the GAR algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @author Modified by Diana Martín (dmartin@ceis.cujae.edu.cu)
 * @version 1.0
 */
public class GAR {

    myDataset trans;
    String assoc_rules_fname;
    String sup_rules_fname;
    String valuesOrderFilename;
    GARProcess ap;
	ArrayList<AssociationRule> assoc_rules;
	 private String fileTime, fileHora, namedataset;

    //We may declare here the algorithm's parameters
	private int nItemset;
	private int nTrials;
	private int popsize;
	private double ps;
	private double pc;
	private double pm;
	private double w;
	private double y;
	private double u;
	private double AF;
	private double minConfidence;
	private double minSupport;
	long startTime, totalTime;


    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public GAR() {
    }

    /**
     * It reads the data from the input files and parse all the parameters
     * from the parameters array.
     * @param parameters parseParameters It contains the input files, output files and parameters
     */
    public GAR(parseParameters parameters) {
    	this.startTime = System.currentTimeMillis();
        this.trans = new myDataset();
        try {
        	this.namedataset = parameters.getTransactionsInputFile();
            System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
            trans.readDataSet( parameters.getTransactionsInputFile() );
        }
        catch (IOException e) {
            System.err.println("There was a problem while reading the input transaction set: " + e);
            somethingWrong = true;
        }

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasNumericalAttributes();
		this.somethingWrong = this.somethingWrong || this.trans.hasMissingAttributes();
		
		this.assoc_rules_fname = parameters.getAssociationRulesFile();
        this.sup_rules_fname = parameters.getOutputFile(0);
        this.valuesOrderFilename = parameters.getOutputFile(1);

        this.fileTime = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/time.txt";
        this.fileHora = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/hora.txt";

		long seed = Long.parseLong(parameters.getParameter(0));

        this.nItemset = Integer.parseInt( parameters.getParameter(1) );
        this.nTrials = Integer.parseInt( parameters.getParameter(2) );
        this.popsize = Integer.parseInt( parameters.getParameter(3) );
        this.ps = Double.parseDouble( parameters.getParameter(4) );
        this.pc = Double.parseDouble( parameters.getParameter(5) );
        this.pm = Double.parseDouble( parameters.getParameter(6) );
        this.w = Double.parseDouble( parameters.getParameter(7) );
        this.y = Double.parseDouble( parameters.getParameter(8) );
        this.u = Double.parseDouble( parameters.getParameter(9) );
        this.AF = Double.parseDouble( parameters.getParameter(10) );
        this.minSupport = Double.parseDouble( parameters.getParameter(11) );
        this.minConfidence = Double.parseDouble( parameters.getParameter(12) );

            
        Randomize.setSeed(seed);
	}

    /**
     * It launches the algorithm
     */
    public void execute() {
        if (somethingWrong) { //We do not execute the program
            System.err.println("An error was found");
            System.err.println("Aborting the program");
            //We should not use the statement: System.exit(-1);
        } 
		else {
        	this.ap = new GARProcess(this.trans, this.nItemset, this.nTrials, this.popsize, this.ps, this.pc, this.pm, this.w, this.y, this.u, this.AF);
			this.ap.run();
			//this.ap.printReport(this.minConfidence, this.minSupport);
			this.assoc_rules = this.ap.getSetRules (this.minConfidence, this.minSupport);
        	        	
			try {
				PrintWriter rule_writer = new PrintWriter(assoc_rules_fname);
				PrintWriter sup_writer = new PrintWriter(sup_rules_fname);
				PrintWriter valuesOrder_writer = new PrintWriter(this.valuesOrderFilename);
				
				rule_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rule_writer.println("<association_rules>");
				
				sup_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				sup_writer.println("<values>");
				
				
			    for (int i=0; i < assoc_rules.size(); i++) {
					AssociationRule a_r = assoc_rules.get(i);
					
					ArrayList<Gene> ant = a_r.getAntecedent();
					ArrayList<Gene> cons = a_r.getConsequent();
					
					rule_writer.println("<rule id=\"" + i + "\">");
					sup_writer.println("<rule id=\"" + i + "\" rule_support=\"" + GARProcess.roundDouble(a_r.getAll_support(),2) + "\" antecedent_support=\"" + GARProcess.roundDouble(a_r.getSupport_Ant(),2) + "\" consequent_support=\"" + GARProcess.roundDouble(a_r.getSupport_cons(),2)
							+ "\" confidence=\"" + GARProcess.roundDouble(a_r.getConfidence(),2) +"\" lift=\"" + GARProcess.roundDouble(a_r.getLift(),2) + "\" conviction=\"" + GARProcess.roundDouble(a_r.getConv(),2) + "\" certainFactor=\"" + GARProcess.roundDouble(a_r.getCF(),2) + "\" netConf=\"" + GARProcess.roundDouble(a_r.getNetConf(),2) + "\" yulesQ=\"" + GARProcess.roundDouble(a_r.getYulesQ(),2) +  "\" nAttributes=\"" + (a_r.getAntecedent().size()+ a_r.getConsequent().size()) + "\"/>");
					
					rule_writer.println("<antecedents>");
					for (int j=0; j < ant.size(); j++)
					{
						Gene g_ant = ant.get(j);
						createRule(g_ant, rule_writer);
					}
					rule_writer.println("</antecedents>");
					
					rule_writer.println("<consequents>");
					Gene g_cons = cons.get(0);
					createRule(g_cons, rule_writer);
					rule_writer.println("</consequents>");
					
					rule_writer.println("</rule>");
					
				}
				
				rule_writer.println("</association_rules>");
				sup_writer.println("</values>");
				
				this.ap.saveReport(this.minConfidence, this.minSupport, sup_writer);
				rule_writer.close();
				sup_writer.close();
				
				valuesOrder_writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				valuesOrder_writer.println("<values>");
	     		
				valuesOrder_writer.print(this.ap.printRules(this.assoc_rules));
	     		
				valuesOrder_writer.print("</values>");
	     		valuesOrder_writer.close();
				
				totalTime = System.currentTimeMillis() - startTime;
				this.writeTime();
				
				System.out.println("Algorithm Finished");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
        }
    }
    
     /**
     * Prints a line with the time taken by the algorithm's execution on the output file.
     */
    public void writeTime() {
    	long seg, min, hor;
        String stringOut = new String("");

        stringOut = "" + totalTime / 1000 + "  " + this.namedataset + assoc_rules_fname + "\n";
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

    	stringOut = stringOut + "  " + assoc_rules_fname + "\n";
        Files.addToFile(this.fileHora, stringOut);
      }
    
    private void createRule(Gene g, PrintWriter w)
    {
    	int attr = g.getAttr();
    	
    	w.println("<attribute name=\"" + Attributes.getInputAttribute(attr).getName() + "\" value=\"");
    	
		if ( g.getType() == myDataset.NOMINAL )
			w.print(Attributes.getInputAttribute(attr).getNominalValue( (int)g.getL() ));
		else w.print("[" + g.getL() + ", " + g.getU() + "]");		
		
		w.println("\" />");
    }    

}
