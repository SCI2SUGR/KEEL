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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.Alatasetal;

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

import org.core.*;
import keel.Dataset.*;

public class Alatasetal {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

    private myDataset trans;
    
    private String rulesFilename;
    private String valuesFilename;
    private AlatasetalProcess proc;
	private ArrayList<AssociationRule> associationRules;
	
	private int nGen;
	private int randomChromosomes;
	private int r;
	private int tournamentSize;
	private double pc;
	private double pmMin;
	private double pmMax;
	private double a1;
	private double a2;
	private double a3;
	private double a4;
	private double a5;
	private double af;
	private double minConfidence;
	private double minSupport;


    private boolean somethingWrong = false; //to check if everything is correct.

    /**
     * Default constructor
     */
    public Alatasetal() {
    }

    /**
     * It reads the data from the input files and parse all the parameters from the parameters array
     * @param parameters It contains the input files, output files and parameters
     */
    public Alatasetal(parseParameters parameters) {       
        this.trans = new myDataset();
        try {
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
		
		this.rulesFilename = parameters.getAssociationRulesFile();
        this.valuesFilename = parameters.getOutputFile(0);

		long seed = Long.parseLong(parameters.getParameter(0));

        this.nGen = Integer.parseInt( parameters.getParameter(1) );
        this.randomChromosomes = Integer.parseInt( parameters.getParameter(2) );
        int r = Integer.parseInt( parameters.getParameter(3) );
        this.tournamentSize = Integer.parseInt( parameters.getParameter(4) );
        this.pc = Double.parseDouble( parameters.getParameter(5) );
        this.pmMin = Double.parseDouble( parameters.getParameter(6) );
        this.pmMax = Double.parseDouble( parameters.getParameter(7) );
        this.a1 = Double.parseDouble( parameters.getParameter(8) );
        this.a2 = Double.parseDouble( parameters.getParameter(9) );
        this.a3 = Double.parseDouble( parameters.getParameter(10) );
        this.a4 = Double.parseDouble( parameters.getParameter(11) );
        this.a5 = Double.parseDouble( parameters.getParameter(12) );
        this.af = Double.parseDouble( parameters.getParameter(13) );
        this.minConfidence = Double.parseDouble( parameters.getParameter(14) );
        this.minSupport = Double.parseDouble( parameters.getParameter(15) );
        
        this.r = (this.trans.getnVars() >= r) ? r : this.trans.getnVars();
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
        	this.proc = new AlatasetalProcess(this.trans, this.nGen, this.randomChromosomes, this.r, this.tournamentSize, this.pc, this.pmMin, this.pmMax, this.a1, this.a2, this.a3, this.a4, this.a5, this.af);
			this.proc.run();
			this.associationRules = this.proc.generateRulesSet(this.minConfidence, this.minSupport);
			this.proc.printReport(this.associationRules);
			
			try {
				int r, i;
				AssociationRule a_r;
				Gene[] terms;
				ArrayList<Integer> id_attrs;
				
				PrintWriter rules_writer = new PrintWriter(this.rulesFilename);
				PrintWriter values_writer = new PrintWriter(this.valuesFilename);
				
				rules_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rules_writer.println("<association_rules>");
				
				values_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				values_writer.println("<values>");
				
				for (r=0; r < this.associationRules.size(); r++) {
					a_r = this.associationRules.get(r);
					
					rules_writer.println("<rule id=\"" + r + "\">");
					values_writer.println("<rule id=\"" + r + "\" support=\"" + a_r.getSupport() + "\" confidence=\"" + a_r.getConfidence() + "\" />");
					
					rules_writer.println("<antecedents>");			
					terms = a_r.getAntecedents();
					id_attrs = a_r.getIdOfAntecedents();
					
					for (i=0; i < terms.length; i++)
						createRule(terms[i], id_attrs.get(i), rules_writer);
						
					rules_writer.println("</antecedents>");
					
					rules_writer.println("<consequents>");			
					terms = a_r.getConsequents();
					id_attrs = a_r.getIdOfConsequents();
					
					for (i=0; i < terms.length; i++)
						createRule(terms[i], id_attrs.get(i), rules_writer);
					
					rules_writer.println("</consequents>");
					
					rules_writer.println("</rule>");					
				}
				
				rules_writer.println("</association_rules>");
				values_writer.println("</values>");
				
				rules_writer.close();
				values_writer.close();
				
				System.out.println("Algorithm Finished");
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
        }
    }
    
    private void createRule(Gene g, int id_attr, PrintWriter w) {	
    	w.println("<attribute name=\"" + Attributes.getAttribute(id_attr).getName() + "\" value=\"");
    	
    	if (! g.getIsPositiveInterval()) w.print("NOT ");
		
    	if ( trans.getAttributeType(id_attr) == myDataset.NOMINAL ) w.print(Attributes.getAttribute(id_attr).getNominalValue( (int)g.getLowerBound() ));
		else w.print("[" + g.getLowerBound() + ", " + g.getUpperBound() + "]");
    	
		w.print("\" />");
    }    

}
