package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public abstract class Fingrams {

	// /////////// VARIABLES /////////////

	// Table with the examples. Each row is an example and the columns have the
	// attributes of the examples
	// protected List<List<Float>> examples;

	// 3 position vector with the operators used in the system
	// protected List<Integer> operators;

	// Matrix that stores the text of the rules
	protected List<String> textRules;

	// Matrix that stores the complete social network
	protected List<List<Double>> socialNetwork;

	// Matrix that stores the scaled social network
	protected List<List<Double>> scaledSocialNetwork;

	// List that stores the list of examples. Each row means a rule, the
	// length of rules varies
	// protected List<List<Integer>> listExamplesCoveredByRule;

	// Matrix that stores the examples that fire the rules
	// A 1 in position (i,j) means that example j+1 fires rule i
	protected byte[][] matrixRulesXExamples;

	protected double[][] matrixFiringDegreesRulesXExamples;

	// Vector that contains the total firing degrees of rules
	protected List<Double> totalFiringDegrees;

	// Number of rules of the system
	protected int numberRules;

	// Number of examples managed
	protected int numberExamples;

	// Number of antecedents of the rules
	protected List<Integer> numberAntecedents;

	// Set of label conclusions that can appear
	protected List<String> possibleLabelConclusions;

	// Rule identifier
	protected List<String> rulesIdentifiers;

	// Relation
	protected int relation;

	// Type of relation
	protected boolean relationDirected;

	// File location. This place will be used to store every file
	protected String fileLocation;

	// Variables to control the uncovered examples
	protected boolean ruleUncoveredExamples;

	protected int numberUncoveredExamples;

	protected List<Integer> uncoveredExamples;

	// ///////////// END VARIABLES ///////////////

	// ///////////// METHODS ///////////////

	// // // // BUILDERS // // // //
	public Fingrams() {
		textRules = new ArrayList<String>();
		socialNetwork = new ArrayList<List<Double>>();
		scaledSocialNetwork = new ArrayList<List<Double>>();
		// listExamplesCoveredByRule = new ArrayList<List<Integer>>();
		totalFiringDegrees = new ArrayList<Double>();

		numberRules = 0;

		numberAntecedents = new ArrayList<Integer>();
		possibleLabelConclusions = new ArrayList<String>();
		rulesIdentifiers = new ArrayList<String>();

		relation = 0;
		relationDirected = false;
		fileLocation = "";
	}

	public Fingrams(int relation, String fileLocation) {
		textRules = new ArrayList<String>();
		socialNetwork = new ArrayList<List<Double>>();
		scaledSocialNetwork = new ArrayList<List<Double>>();
		// listExamplesCoveredByRule = new ArrayList<List<Integer>>();
		totalFiringDegrees = new ArrayList<Double>();

		numberRules = 0;

		numberAntecedents = new ArrayList<Integer>();
		possibleLabelConclusions = new ArrayList<String>();
		rulesIdentifiers = new ArrayList<String>();

		this.relation = relation;
		if (relation == 0) {
			relationDirected = false;
		} else {
			relationDirected = true;
		}
		this.fileLocation = fileLocation;

	}

	// // // // GETS & SETS // // // //

	public List<String> getTextRules() {
		return textRules;
	}

	public List<List<Double>> getSocialNetwork() {
		return socialNetwork;
	}

	public List<List<Double>> getScaledSocialNetwork() {
		return scaledSocialNetwork;
	}

	public byte[][] getMatrixRulesXExamples() {
		return matrixRulesXExamples;
	}

	public int getNumberRules() {
		return numberRules;
	}

	public int getNumberExamples() {
		return numberExamples;
	}

	public List<Integer> getNumberAntecedents() {
		return numberAntecedents;
	}

	public List<String> getPossibleLabelConclusions() {
		return possibleLabelConclusions;
	}

	public List<String> getRulesIdentifiers() {
		return rulesIdentifiers;
	}

	public int getTypeRelation() {
		return relation;
	}

	public List<Double> getTotalFiringDegrees() {
		return totalFiringDegrees;
	}

	public String getFileLocation() {
		return fileLocation;
	}

	// // // Abstract // // // //

	protected abstract InfoNode calculateInfoNode(int n);

	public abstract int loadInfoFromFS();

	// // // Defined and fixed // // // //

	public void generateMatrix() {
		if (relation == 0) {
			generateUndirectedMatrix();
			relationDirected = false;
		} else {
			if (relation > 0) {
				generateDirectedMatrix();
				relationDirected = true;
			}
		}
	}

	// protected void writeToFileCompleteNetwork() {}

	public String buildConfGraphDotFile(String file) {

		String outputString;
		StringBuilder outputSB = new StringBuilder();
		try {
			DecimalFormat df = new AdministrativeStaff().format();

			Vector<Integer> origRules = new Vector<Integer>();
			Vector<Integer> endRules = new Vector<Integer>();
			Vector<Double> linkWeights = new Vector<Double>();

			String inputString = (new AdministrativeStaff())
					.readFileAsString(file);
			List<String> lines = Arrays.asList(inputString.split("\n"));

			for (int i = 0; i < lines.size(); i++) {

				String l = lines.get(i);

				// It iterates until it finds the *arcs or *edge marker
				if (l.startsWith("*arcs") || l.startsWith("*edges")) {
					i++;

					for (; i < lines.size(); i++) {

						// It reads next line
						l = lines.get(i);

						// Break the line getting the first number (rule) and
						// adding it to the origin of rules
						int ind1 = l.indexOf(" ");
						Integer r1 = new Integer(l.substring(0, ind1));
						origRules.add(r1);

						// Read and store the end of the link (end rule)
						String aux = l.substring(ind1 + 1);
						int ind2 = aux.indexOf(" ");
						Integer r2 = new Integer(aux.substring(0, ind2));
						endRules.add(r2);

						// Read and store the weight of the link
						Double lw = new Double(aux.substring(ind2 + 1));
						linkWeights.add(lw);

					}
				} else if (l.startsWith("*matrix")) {

					String aux = "";
					i++;

					for (int n = 0; n < numberRules && i < lines.size(); n++, i++) {
						// Read the first row
						aux = lines.get(i);

						// It splits the content by columns
						List<String> valuesRow = Arrays.asList(aux
								.split("[ ]+"));

						// Iterate along the values of the row
						for (int j = 0; j < valuesRow.size(); j++) {
							// Gets the next value of the actual row
							String value = valuesRow.get(j);
							if (Double.parseDouble(value) > 0) {
								origRules.add(n + 1);
								endRules.add(j + 1);
								linkWeights.add(Double.parseDouble(value));

							}
						}
						// System.out.println("  -> matrix["+n+"]["+m+"]="+matrix[n][m]);
					}

				}

			}

			if (relationDirected) {
				outputSB.append("digraph \"\" {\n\t" + "name=FINGRAM; "
						+ "ratio=auto; " + "size=\"10,10\"; "
						+ "overlap=\"scale\"; " + "nodesep=0.3; "
						+ "center=true; " + "truecolor=true;\n\n\t");
			} else {
				outputSB.append("graph \"\" {\n\t" + "name=FINGRAM; "
						+ "ratio=auto; " + "size=\"10,10\"; "
						+ "overlap=\"scale\"; " + "nodesep=0.3; "
						+ "center=true; " + "truecolor=true;\n\n\t");

			}
			for (int n = 0; n < numberRules; n++) {
				// String ruleDesc = Rule[n];

				if (isCoveringExamples(matrixRulesXExamples[n])) {
					String shape = "circle";

					InfoNode in = calculateInfoNode(n);

					outputSB.append(rulesIdentifiers.get(n).replaceAll("\\s",
							"")
							+ " [shape="
							+ shape
							+ ",height="
							+ df.format(in.getNodeSize())
							+ ",width="
							+ df.format(in.getNodeSize())
							+ ",fixedsize=true"
							+ ",peripheries="
							+ numberAntecedents.get(n)
							+ ",color=\""
							+ in.getBorderColor()
							+ "\",fillcolor=\""
							+ in.getNodeColor()
							+ "\",fontsize="
							+ in.getFontSize()
							+ ",fontcolor="
							+ in.getFontColor()
							+ ",style=filled"
							+ ",label=\""
							+ in.getNodeInfo()
							+ "\""
							+ ",tooltip=\""
							+ in.getNodeInfoToolTipText() + "\"];\n\t");
				} else {
					String shape = "circle";

					outputSB.append(rulesIdentifiers.get(n).replaceAll("\\s",
							"")
							+ " [shape="
							+ shape
							+ ",height="
							+ df.format(1)
							+ ",width="
							+ df.format(1)
							+ ",fixedsize=true"
							+ ",peripheries="
							+ numberAntecedents.get(n)
							+ ",color=\""
							+ "red"
							+ "\",fillcolor=\""
							+ "red"
							+ "\",fontsize="
							+ 11.8f
							+ ",fontcolor="
							+ "black"
							+ ",style=filled"
							+ ",label=\""
							+ "R"
							+ rulesIdentifiers.get(n).substring(4)
							+ "\\n"
							+ " (cov=0.000"
							+ ")"
							+ "\""
							+ ",tooltip=\""
							+ "R"
							+ rulesIdentifiers.get(n).substring(4)
							+ "\\n"
							+ " (cov=0.000"
							+ ")"
							+ "\"];\n\t");
				}
			}

			if (hasRuleUncoveredExamples()) {
				outputSB.append(createNodeUncoveredExamples());
			}

			if ((origRules.size() != endRules.size())
					|| (origRules.size() != linkWeights.size())) {
				System.out
						.println("ERROR reading and printing the edges of the graph");
			} else {
				int numberEdges = origRules.size();
				int[][] printEdges = null;
				boolean warn = false;
				outputString = outputSB.toString();
				if ((outputString.contains(".ms."))
						|| (outputString.contains(".msc."))
						|| (outputString.contains(".msfd."))
						|| (outputString.contains(".msfdc."))) {
					printEdges = new int[numberRules][numberRules];
					warn = true;
				}
				for (int n = 0; n < numberEdges; n++) {

					int origRule = origRules.get(n) - 1;
					int endRule = endRules.get(n) - 1;

					if (!warn
							|| (warn
									&& (printEdges[origRule - 1][endRule - 1] == 0) && (printEdges[endRule - 1][origRule - 1] == 0))) {

						String linkName = "\n\t"
								+ rulesIdentifiers.get(origRule).replaceAll(
										"\\s", "");
						String linkToolTipText = "R"
								+ rulesIdentifiers.get(origRule).substring(4);

						if (relationDirected) {
							linkName = linkName + " -> ";
							linkToolTipText = linkToolTipText + " -> ";
						} else {
							linkName = linkName + " -- ";
							linkToolTipText = linkToolTipText + " -- ";

						}

						linkName = linkName
								+ rulesIdentifiers.get(endRule).replaceAll(
										"\\s", "");
						linkToolTipText = linkToolTipText + "R"
								+ rulesIdentifiers.get(endRule).substring(4);
						String col = "black";

						outputSB.append("  " + linkName);
						String linkDesc = linkToolTipText + " ("
								+ df.format(linkWeights.get(n)) + ")";
						outputSB.append(" [penwidth="
								+ df.format(1 + 10 * (linkWeights.get(n)))
								+ ",weight="
								+ df.format(100 * (1 - linkWeights.get(n)))
								+ ",color=" + col);

						// If we have a relation that produces directed
						// relations
						if (relationDirected) {
							outputSB.append(",arrowhead=normal");
						}
						// If we have a relation that produces undirected
						// relations

						outputSB.append(",fontsize=15" + ",labelfontcolor="
								+ col + ",label=\""
								+ df.format(linkWeights.get(n)) + "\""
								+ ",tooltip=\"" + linkDesc + "\"" + ",title="
								+ "\"" + linkDesc + "\"];");

						if (warn) {
							printEdges[origRule - 1][endRule - 1] = 1;
						}
					}
				}
				outputSB.append("");
			}
			outputSB.append("}");

		} catch (Exception e) {
			e.printStackTrace();
		}
		outputString = outputSB.toString();
		return outputString;
	}

	public void generateScaledNetwork(String origFile, String destFile, int q) {

		try {
			String command = "\""
					+ (new AdministrativeStaff()).obtainPathPathfinder()
					+ "\" \"" + fileLocation + origFile + "\"" + " " + q;

			// System.out.println("\""+command+"\"");

			Runtime rt = Runtime.getRuntime();
			Process p = rt.exec(command);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));

			String line = null;

			FileWriter fstream = new FileWriter(fileLocation + destFile);

			String salida = "";

			while ((line = input.readLine()) != null) {
				salida += line + "\n";
			}

			BufferedWriter out = new BufferedWriter(fstream);
			out.write(salida);
			// Close the output stream
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void generateFingramImages(String nameFile, String output,
			String drawing) {
		// Generate .svg from .dot file for the scaled network
		String pathDot = "\"" + (new AdministrativeStaff()).obtainPathDot()
				+ "\"";
		String command = pathDot + " -T" + output + " -K" + drawing + " "
				+ "\"" + fileLocation + nameFile + ".dot\" -o \""
				+ fileLocation + nameFile + "." + output + "\"";

		// String auxOut = fileLocation + "aux.txt";

		// System.out.println(command);

		// execute my command
		new AdministrativeStaff().runProcess(command);

		if (output.equals("svg")) {
			(new AdministrativeStaff()).writeParsedSVGBuffer(fileLocation
					+ nameFile + ".svg");
		}
	}

	public void writeToFileNetwork(List<List<Double>> parameterSocialNetwork,
			String nameFile, String nameFileAux) {
		try {

			StringBuilder entradaBuilder = new StringBuilder();
			StringBuilder entradaBuilder2;

			// Construct the .ms file with all the links
			String entrada;

			entradaBuilder.append("*vertices " + parameterSocialNetwork.size()
					+ "\n");

			for (int i = 1; i <= parameterSocialNetwork.size(); i++)
				entradaBuilder
						.append(i
								+ " \""
								+ i
								+ "\" ellipse x_fact 1.22866290766363 y_fact 1.22866290766363 fos 3.5 bw 0.0 ic Emerald\n");

			entradaBuilder.append("*matrix\n");

			String entrada2 = "";
			if (!relationDirected) {
				entradaBuilder2 = new StringBuilder(entradaBuilder);
				for (int i = 0; i < parameterSocialNetwork.size(); i++) {
					for (int j = 0; j < parameterSocialNetwork.size(); j++) {
						entradaBuilder2.append(parameterSocialNetwork.get(i)
								.get(j) + " ");
						if (i < j)
							entradaBuilder.append(parameterSocialNetwork.get(i)
									.get(j) + " ");
						else
							entradaBuilder.append("0 ");
					}
					entradaBuilder.append("\n");
					entradaBuilder2.append("\n");
				}
			} else {
				for (int i = 0; i < parameterSocialNetwork.size(); i++) {
					for (int j = 0; j < parameterSocialNetwork.size(); j++) {
						entradaBuilder.append(parameterSocialNetwork.get(i)
								.get(j) + " ");
					}
					entradaBuilder.append("\n");
				}
				entradaBuilder2 = new StringBuilder(entradaBuilder);

			}

			// Write the information to file
			FileWriter fstream = new FileWriter(fileLocation + nameFile);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(entradaBuilder.toString());
			// Close the output stream
			out.close();

			// Write the information to file to be scaled
			FileWriter fstream2 = new FileWriter(fileLocation + nameFileAux);
			BufferedWriter out2 = new BufferedWriter(fstream2);
			out2.write(entradaBuilder2.toString());
			// Close the output stream
			out2.close();

			// return entrada;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * function generateUndirectedMatrix
	 * 
	 * Need: The list of examples covered by each rule
	 * 
	 * Produce: Nothing
	 * 
	 * Modify: The social network with the information
	 */
	protected void generateUndirectedMatrix() {

		socialNetwork = new ArrayList<List<Double>>(numberRules);
		List<Integer> FRi = new ArrayList<Integer>();

		/*
		 * for (int i = 0; i < numberRules; i++) { socialNetwork.add(new
		 * ArrayList<Double>()); for (int j = 0; j < numberRules; j++) { if (j
		 * == i) socialNetwork.get(i).add(1.0); else
		 * socialNetwork.get(i).add(0.0); }
		 * FRi.add(listExamplesCoveredByRule.get(i).size()); }
		 */

		for (int i = 0; i < numberRules; i++) {
			socialNetwork.add(new ArrayList<Double>());
			FRi.add(elementsInCommon(matrixRulesXExamples[i],
					matrixRulesXExamples[i]));
			for (int j = 0; j < numberRules; j++) {
				if (j == i) {
					socialNetwork.get(i).add(1.0);
				} else {
					socialNetwork.get(i).add(
							(double) elementsInCommon(matrixRulesXExamples[i],
									matrixRulesXExamples[j]));
				}
			}
		}
		/*
		 * for (int i = 0; i < numberRules; i++) { for (int j :
		 * listExamplesCoveredByRule.get(i)) { for (int k = i + 1; k <
		 * numberRules; k++) {
		 * 
		 * if (listExamplesCoveredByRule.get(k).contains(j)) { Double previous =
		 * socialNetwork.get(i).get(k); socialNetwork.get(i).set(k, previous +
		 * 1); socialNetwork.get(k).set(i, previous + 1); } } }
		 * 
		 * }
		 */

		for (int i = 0; i < numberRules; i++) {

			for (int j = 0; j < numberRules; j++) {
				if (FRi.get(i) != 0 && FRi.get(j) != 0 && i != j) {
					socialNetwork.get(i)
							.set(j,
									socialNetwork.get(i).get(j)
											/ (float) Math.sqrt(FRi.get(i)
													* FRi.get(j)));
				}
			}
		}

	}

	abstract protected void generateDirectedMatrix();

	protected int elementsInCommon(byte[] rulei, byte[] rulej) {
		int count = 0;

		for (int i = 0; i < rulei.length; i++) {
			if (((byte) (rulei[i] & rulej[i])) == 1) {
				count++;
			}
		}
		return count;
	}

	protected boolean isCoveringExamples(byte[] rule) {
		for (int i = 0; i < rule.length; i++) {
			if (rule[i] == 1) {
				return true;
			}
		}
		return false;
	}

	abstract public void generateScienceMapFile(double MaxThr);

	abstract public String createNodeUncoveredExamples();

	abstract public boolean hasRuleUncoveredExamples();

	abstract public void printFingramsLegend();
}
