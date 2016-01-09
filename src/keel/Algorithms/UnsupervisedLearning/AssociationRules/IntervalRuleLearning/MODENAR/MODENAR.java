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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.MODENAR;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.core.*;

import keel.Dataset.*;

/**
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the MODENAR algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu)
 * @version 1.0
 */
public class MODENAR {

	String outputTr, outputTst;
	myDataset trans;
	String assoc_rules_fname;
	String sup_rules_fname;
	private String paretoFilename;
	private String fileTime, fileHora, namedataset;
	MODENARProcess ap;
	private ArrayList<AssociationRule> associationRulesPareto;


	//We may declare here the algorithm's parameters
	int nEvaluations;
	int popSize;
	double CR;//crossover rate
	int threshold;
	int nObj;
	int AF;
	double[]Wk;// weight for each fitness
	long startTime, totalTime;

	private boolean somethingWrong = false; //to check if everything is correct.

	/**
	 * Default constructor
	 */
	public MODENAR() {
	}

	/**
	 * It reads the data from the input files (training, validation and test) and parse all the parameters
	 * from the parameters array.
	 * @param parameters parseParameters It contains the input files, output files and parameters
	 */
	public MODENAR(parseParameters parameters) {
		this.startTime = System.currentTimeMillis();

		int i, posParameter;
		this.trans = new myDataset();
		try {
			this.namedataset = parameters.getTransactionsInputFile();
			System.out.println("\nReading the transaction set: " + parameters.getTransactionsInputFile());
			trans.readDataSet(parameters.getTransactionsInputFile());
		} catch (IOException e) {
			System.err.println(
					"There was a problem while reading the input data-sets: " +
							e);
			somethingWrong = true;
		}

		//We may check if there are some numerical attributes, because our algorithm may not handle them:
		//somethingWrong = somethingWrong || train.hasNumericalAttributes();
		somethingWrong = somethingWrong || trans.hasMissingAttributes();

		this.assoc_rules_fname = parameters.getAssociationRulesFile();
		this.sup_rules_fname = parameters.getOutputFile(0);
		this.paretoFilename = parameters.getOutputFile(1);

		this.fileTime = (parameters.getOutputFile(1)).substring(0,(parameters.getOutputFile(1)).lastIndexOf('/')) + "/time.txt";
		this.fileHora = (parameters.getOutputFile(1)).substring(0,(parameters.getOutputFile(1)).lastIndexOf('/')) + "/hora.txt";

		long seed = Long.parseLong(parameters.getParameter(0));

		// parameters       
		this.popSize = Integer.parseInt( parameters.getParameter(1));
		this.nEvaluations = Integer.parseInt( parameters.getParameter(2) );
		this.CR = Double.parseDouble( parameters.getParameter(3));
		this.threshold = Integer.parseInt( parameters.getParameter(4));
		this.AF = Integer.parseInt( parameters.getParameter(5));
		this.Wk = new double[4];

		for(i=0, posParameter = 5; i < 4; i++, posParameter++)  this.Wk[i]= Double.parseDouble( parameters.getParameter(posParameter));

		Randomize.setSeed(seed);
	}

	/**
	 * It launches the algorithm
	 */
	public void execute() {
		if (somethingWrong) { //We do not execute the program
			System.err.println("An error was found, either the data-set have numerical values or missing values.");
			System.err.println("Aborting the program");
			//We should not use the statement: System.exit(-1);
		} else {
			//We do here the algorithm's operations

			this.ap = new MODENARProcess(this.trans,this.nEvaluations, this.popSize, this.CR, this.threshold, this.Wk, this.AF);
			this.ap.run();
			this.associationRulesPareto = this.ap.getAssoc_rules_Pareto();

			try {
				PrintWriter rule_writer = new PrintWriter(assoc_rules_fname);
				PrintWriter sup_writer = new PrintWriter(sup_rules_fname);  
				PrintWriter pareto_writer = new PrintWriter(this.paretoFilename); 

				rule_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				rule_writer.println("<association_rules>");

				sup_writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				sup_writer.println("<values>");


				pareto_writer.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
				pareto_writer.println("<values>");

				for (int i=0; i < this.associationRulesPareto .size(); i++) {
					AssociationRule a_r = this.associationRulesPareto .get(i);

					ArrayList<Gene> ant = a_r.getAntecedent();
					ArrayList<Gene> cons = a_r.getConsequent();

					rule_writer.println("<rule id=\"" + i + "\">");
					sup_writer.println("<rule id=\"" + i + "\" rule_support=\"" + MODENARProcess.roundDouble(a_r.getAll_support(),2) + "\" antecedent_support=\"" + MODENARProcess.roundDouble(a_r.getSupport(),2) + "\" consequent_support=\"" + MODENARProcess.roundDouble(a_r.getSupport_consq(),2)
							+ "\" confidence=\"" + MODENARProcess.roundDouble(a_r.getConfidence(),2) +"\" lift=\"" + MODENARProcess.roundDouble(a_r.getLift(),2) + "\" conviction=\"" + MODENARProcess.roundDouble(a_r.getConv(),2) + "\" certainFactor=\"" + MODENARProcess.roundDouble(a_r.getCF(),2) + "\" netConf=\"" + MODENARProcess.roundDouble(a_r.getNetConf(),2) + "\" yulesQ=\"" + MODENARProcess.roundDouble(a_r.getYulesQ(),2) + "\" nAttributes=\"" + (a_r.getLengthAntecedent()+ a_r.getLengthConsequent()) + "\"/>");

					rule_writer.println("<antecedents>");

					for (int j=0; j < ant.size(); j++)
					{
						Gene g_ant = ant.get(j);
						createRule(g_ant, rule_writer);
					}
					rule_writer.println("</antecedents>");

					rule_writer.println("<consequents>");

					for (int j=0; j < cons.size(); j++)
					{
						Gene g_cons = cons.get(j);
						createRule(g_cons, rule_writer);
					}
					rule_writer.println("</consequents>");

					rule_writer.println("</rule>");

				}

				rule_writer.println("</association_rules>");
				sup_writer.println("</values>");

				//ap.saveReport(this.associationRulesPareto,sup_writer);
				rule_writer.close();
				sup_writer.close();

				pareto_writer.print(this.ap.getParetos());
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

		w.print("<attribute name=\"" + Attributes.getAttribute(attr).getName() + "\" value=\"");

		if ( g.getType() == myDataset.NOMINAL )
			w.print(Attributes.getAttribute(attr).getNominalValue( (int)g.getL() ));
		else w.print("[" + g.getL() + ", " + g.getU() + "]");		

		w.println("\" />");
	}    

}
