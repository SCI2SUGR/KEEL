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
package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MOPNAR;

/**
 * <p>
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu) 
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

public class MOPNAR {
	/**
	 * <p>
	 * It gathers all the parameters, launches the algorithm, and prints out the results
	 * </p>
	 */

	private myDataset dataset;

	private String rulesFilename;
	private String valuesFilename;
	private String paretoFilename;
	private MOPNARProcess proc;
	private ArrayList<AssociationRule> associationRulesPareto;
	private String fileTime, fileHora, namedataset;

	private int nTrials;
	private int numObjectives;
	private int H;  //control the population size and weight vectors
	private int T; //Number of the weight vectors in the neighborhood
	private double probDelta; //Probability that parent solutions are selected from the neighborhood
	private int nr; //Maximal number of solutions replaced by each child solution
	private double pm;
	private double af;
	private double percentUpdate;
	long startTime, totalTime;



	private boolean somethingWrong = false; //to check if everything is correct.

	/**
	 * Default constructor
	 */
	public MOPNAR() {
	}

	/**
	 * It reads the data from the input files and parse all the parameters from the parameters array
	 * @param parameters It contains the input files, output files and parameters
	 */
	public MOPNAR(parseParameters parameters) { 
		this.startTime = System.currentTimeMillis();
		this.dataset = new myDataset();
		try {
			System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
			dataset.readDataSet (parameters.getTransactionsInputFile());
		}
		catch (IOException e) {
			System.err.println ("There was a problem while reading the input transaction set: " + e);
			somethingWrong = true;
		}

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasNumericalAttributes();
		this.somethingWrong = this.somethingWrong || this.dataset.hasMissingAttributes();

		this.rulesFilename = parameters.getAssociationRulesFile();
		this.valuesFilename = parameters.getOutputFile(0);
		this.paretoFilename = parameters.getOutputFile(1);

		this.fileTime = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/time.txt";
		this.fileHora = (parameters.getOutputFile(0)).substring(0,(parameters.getOutputFile(0)).lastIndexOf('/')) + "/hora.txt";

		long seed = Long.parseLong (parameters.getParameter(0));
		this.numObjectives = Integer.parseInt (parameters.getParameter(1));
		this.nTrials = Integer.parseInt (parameters.getParameter(2));
		this.H = Integer.parseInt (parameters.getParameter(3));
		this.T = Integer.parseInt (parameters.getParameter(4));
		this.probDelta = Double.parseDouble (parameters.getParameter(5));
		this.nr = Integer.parseInt (parameters.getParameter(6));
		this.pm = Double.parseDouble (parameters.getParameter(7));
		this.af = Double.parseDouble (parameters.getParameter(8));
		this.percentUpdate = Double.parseDouble (parameters.getParameter(9));

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
			this.proc = new MOPNARProcess(this.dataset,this.numObjectives, this.nTrials, this.H, this.T, this.probDelta , this.nr, this.pm, this.af,this.percentUpdate);
			this.proc.run();
			this.associationRulesPareto = this.proc.generateRulesPareto();

			try {
				int r, i;
				Gene gen;
				AssociationRule a_r;

				PrintWriter rules_writer = new PrintWriter(this.rulesFilename);
				PrintWriter values_writer = new PrintWriter(this.valuesFilename);
				PrintWriter pareto_writer = new PrintWriter(this.paretoFilename);

				rules_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rules_writer.println("<association_rules>");				
				values_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				values_writer.println("<values>");
				pareto_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				pareto_writer.println("<values>");

				for (r=0; r < this.associationRulesPareto.size(); r++) {
					a_r = this.associationRulesPareto.get(r);

					ArrayList<Gene> ant = a_r.getAntecedents();
					ArrayList<Gene> cons = a_r.getConsequents();

					rules_writer.println("<rule id=\"" + r + "\">");
					values_writer.println("<rule id=\"" + r + "\" rule_support=\"" + MOPNARProcess.roundDouble(a_r.getSupport(),2) + "\" antecedent_support=\"" + MOPNARProcess.roundDouble(a_r.getAntSupport(),2) + "\" consequent_support=\"" + MOPNARProcess.roundDouble(a_r.getConsSupport(),2) + "\" confidence=\"" + MOPNARProcess.roundDouble(a_r.getConfidence(),2) +"\" lift=\"" + MOPNARProcess.roundDouble(a_r.getLift(),2) + "\" conviction=\"" + MOPNARProcess.roundDouble(a_r.getConv(),2) + "\" certainFactor=\"" + MOPNARProcess.roundDouble(a_r.getCF(),2) + "\" netConf=\"" + MOPNARProcess.roundDouble(a_r.getNetConf(),2) +  "\" yulesQ=\"" + MOPNARProcess.roundDouble(a_r.getYulesQ(),2) + "\" nAttributes=\"" + (a_r.getnAnts()+1) + "\"/>");
					rules_writer.println("<antecedents>");			

					for (i=0; i < ant.size(); i++) {
						gen = ant.get(i);
						createRule(gen, gen.getAttr(), rules_writer);
					}

					rules_writer.println("</antecedents>");				
					rules_writer.println("<consequents>");			

					for (i=0; i < cons.size(); i++) {
						gen = cons.get(i);
						createRule(gen, gen.getAttr(), rules_writer);
					}

					rules_writer.println("</consequents>");

					rules_writer.println("</rule>");					
				}

				rules_writer.println("</association_rules>");
				values_writer.println("</values>");
				//this.proc.saveReport(this.associationRulesPareto, values_writer);
				rules_writer.close();
				values_writer.close();

				pareto_writer.print(this.proc.getParetos());
				pareto_writer.println("</values>");
                                pareto_writer.close();
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


	private void createRule(Gene g, int id_attr, PrintWriter w) {	
		w.println("<attribute name=\"" + Attributes.getAttribute(id_attr).getName() + "\" value=\"");

		if (! g.getIsPositiveInterval()) w.print("NOT ");

		if  (this.dataset.getAttributeType(id_attr) == myDataset.NOMINAL) w.print(Attributes.getAttribute(id_attr).getNominalValue ((int)g.getLowerBound()));
		else w.print("[" + g.getLowerBound() + ", " + g.getUpperBound() + "]");

		w.print("\" />");
	}    

}
