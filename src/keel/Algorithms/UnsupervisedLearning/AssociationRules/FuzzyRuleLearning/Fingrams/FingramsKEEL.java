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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * <p>It gathers all the parameters, launches the algorithm, and prints out the
	 * results
	 *
 * @author Written by Alvaro Lopez
 * @version 1.1
 * @since JDK1.6
 * </p>
 */
public class FingramsKEEL {
    

	private myDataset dataset;
	private DataBase database;
	private FingramsProcess fingramsProcess;

	private String rulesBaseFile;
	private String dataBaseFile;
	private String fingramsFile;
	private String fileTime, fileHora, namedataset;

	long startTime, totalTime;

	private double minimumLift;
	private int metric;
	private double blankThreshold;
	private int qParameter;
	private String layout;

	private boolean somethingWrong = false; // to check if everything is
											// correct.

	/**
	 * Default constructor.
	 */
	public FingramsKEEL() {
	}

	/**
	 * It reads the data from the input files and parse all the parameters from
	 * the parameters array.
	 * 
	 * @param parameters
	 *            parseParameters It contains the input files, output files and
	 *            parameters
	 */
	public FingramsKEEL(parseParameters parameters) {
		this.startTime = System.currentTimeMillis();
		this.dataset = new myDataset();

		try {
			System.out.println("\nReading the transaction set: "
					+ parameters.getTransactionsInputFile());
			this.dataset.readDataSet(parameters.getTransactionsInputFile());
		} catch (IOException e) {
			System.err
					.println("There was a problem while reading the input transaction set: "
							+ e);
			somethingWrong = true;
		}

		// We may check if there are some numerical attributes, because our
		// algorithm may not handle them:
		// somethingWrong = somethingWrong || train.hasNumericalAttributes();
		this.somethingWrong = this.somethingWrong
				|| this.dataset.hasMissingAttributes();

		this.dataBaseFile = parameters.getInputFile(0);
		this.rulesBaseFile = parameters.getInputFile(1);
		this.fingramsFile = parameters.getOutputFile(0);

		this.fileTime = this.fingramsFile.substring(0,
				this.fingramsFile.lastIndexOf('/'))
				+ "/time.txt";
		this.fileHora = this.fingramsFile.substring(0,
				this.fingramsFile.lastIndexOf('/'))
				+ "/hora.txt";
		this.namedataset = this.dataset.getRelationName();

		// Read the parameters of the algorithm
		this.minimumLift = Double.parseDouble(parameters.getParameter(0));

		if (parameters.getParameter(1).equals("Symmetric")) {
			this.metric = 0;
		} else {
			this.metric = 1;
		}

		this.blankThreshold = Double.parseDouble(parameters.getParameter(2));
		// this.highGoodnessThreshold =
		// Double.parseDouble(parameters.getParameter(3));
		// this.lowGoodnessThreshold =
		// Double.parseDouble(parameters.getParameter(4));
		this.qParameter = Integer.parseInt(parameters.getParameter(3));

		if (parameters.getParameter(4).equals("Kamada-Kawai")) {
			this.layout = "neato";
		} else if (parameters.getParameter(4).equals("Fruchterman-Reingold")) {
			this.layout = "fdp";
		}

		System.err.println("Fingram Parameters:\n\tminimumLift: "
				+ minimumLift
				+ "; \n\tmetric: "
				+ parameters.getParameter(1)
				+ "; \n\tblankThreshold: "
				+ blankThreshold
				// + "; \n\thighGoodnessThreshold: " +
				// highGoodnessThreshold
				// + "; \n\tlowGoodnessThreshold: " +
				// lowGoodnessThreshold
				+ "; \n\tqParameter: " + qParameter + "; \n\tlayout: "
				+ parameters.getParameter(4));

	}

	/**
	 * It launches the algorithm
	 */
	public void execute() {
		if (somethingWrong) { // We do not execute the program
			System.err.println("An error was found");
			System.err.println("Aborting the program");
			// We should not use the statement: System.exit(-1);
		} else {
			// System.out.println("Llego al principio del else");
			// System.out.println("this.dataBaseFile: "+this.dataBaseFile);
			this.database = new DataBase(this.dataBaseFile, this.dataset);
			this.fingramsProcess = new FingramsProcess(this.dataset,
					this.database);

			this.fingramsFile = this.fingramsFile.substring(0,
					this.fingramsFile.length() - 4) + "fs";

			System.err.println("Constructing the configuration (.fs) file: "
					+ this.fingramsFile);

			// Generate the config file
			int correct = this.fingramsProcess.generateFile(rulesBaseFile,
					blankThreshold, this.fingramsFile, this.minimumLift);

			//
			if (correct == -1) {
				System.err.println("An error ocurred while writting the file");
				somethingWrong = true;
			} else {

				String fileLocation = fingramsFile.substring(0,
						fingramsFile.length() - 3);
				/*
				 * File file = new File(fileLocation);
				 * 
				 * System.out.println("fileLocation: "+fileLocation);
				 * 
				 * FileReader fr = null; BufferedReader br = null;
				 * 
				 * // Read the first line of the file to get the type of fingram
				 * to // build String type = ""; try { fr = new
				 * FileReader(file); br = new BufferedReader(fr); // Lectura del
				 * fichero String line; if ((line = br.readLine()) == null) {
				 * System.out.println("Error in file, no content"); return; }
				 * else { type = line.toUpperCase(); }
				 * 
				 * } catch (FileNotFoundException e) { // TODO Auto-generated
				 * catch block e.printStackTrace(); } catch (IOException e) { //
				 * TODO Auto-generated catch block e.printStackTrace(); }
				 */
				Fingrams f = new FARFingrams(metric, fileLocation);

				// Read the information from the file
				int nRules = f.loadInfoFromFS();

				if (nRules == 0) {
					System.out
							.println("Fingram empty (with 0 rules) for file " + fileLocation);
				} else {
					// Generate the complete matrix
					f.generateMatrix();

					// Create files to create complete fingrams
					f.writeToFileNetwork(f.getSocialNetwork(), ".Complete.ms",
							".CompleteAux.ms");

					String completeDot = f.buildConfGraphDotFile(fileLocation
							+ ".Complete.ms");

					(new AdministrativeStaff()).writeStringToFile(fileLocation
							+ ".Complete.dot", completeDot);

					f.generateFingramImages(".Complete", "svg", layout);

					f.printFingramsLegend();

					// Create the files to create the scaled fingrams
					f.writeToFileNetwork(
							(new AdministrativeStaff()).fastPathfinder(
									f.getSocialNetwork(), qParameter),
							".Pathfinder.ms", ".CompleteAux.ms");

					String scaledDot = f.buildConfGraphDotFile(fileLocation
							+ ".Pathfinder.ms");

					(new AdministrativeStaff()).writeStringToFile(fileLocation
							+ ".Pathfinder.dot", scaledDot);

					f.generateFingramImages(".Pathfinder", "svg", layout);

				}
			}

		}
	}

	private int runProcess(String command, String tempOutFile) {
		int exitVal = -1;
		Process p = null;
		try {
			// System.out.println("runProcess: 1 -> "+command);
			Runtime rt = Runtime.getRuntime();

			// System.out.println("runProcess: 2 -> "+command);
			// Date d= new Date(System.currentTimeMillis());
			// System.out.println("runProcess: T1 -> "+DateFormat.getDateTimeInstance().format(d));
			p = rt.exec(command);
			// d= new Date(System.currentTimeMillis());
			// System.out.println("runProcess: T2 -> "+DateFormat.getDateTimeInstance().format(d));
			// cleaning output buffer
			InputStream is = p.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);

			FileOutputStream fos = new FileOutputStream(new File("."
					+ System.getProperty("file.separator") + tempOutFile));
			PrintStream pOutFile = new PrintStream(fos);
			String line;
			// d= new Date(System.currentTimeMillis());
			// System.out.println("runProcess: T3 -> "+DateFormat.getDateTimeInstance().format(d));
			while ((line = br.readLine()) != null)
				pOutFile.println(line);

			br.close();
			pOutFile.flush();
			pOutFile.close();
			fos.close();
			exitVal = p.waitFor();

			// System.out.println("runProcess: 3");
			is.close();
			isr.close();
			// p.waitFor();
			// System.out.println("runProcess: waiting.......");
			// d= new Date(System.currentTimeMillis());
			// System.out.println("runProcess: T4 -> "+DateFormat.getDateTimeInstance().format(d));
			// System.out.println("runProcess: 4");
		} catch (Throwable e) {
			e.printStackTrace();
			p.destroy();
		}
		// System.out.println("runProcess: 5");
		return exitVal;
	}

}