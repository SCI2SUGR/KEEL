package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implements Fuzzy Association Rules Learning Fuzzy Inference-grams
 *
 */
public class FARFingrams extends Fingrams {

	// /////////// VARIABLES /////////////

	// List of supports of rules
	private List<Double> support;

	// List of confidences of rules
	private List<Double> confidence;

	// List of lifts of rules
	private List<Double> lift;

	private Double minSupport;
	private Double maxSupport;

	private Double minLift;
	private Double maxLift;

	// List of possible variables that can appear as conclusions. They can have
	// variables repeated, to correspond with the variable
	// possibleLabelConclusions
	private List<String> possibleVbleConclusions;

	/** Matrix that stores the firingDegrees up to which the examples fire the
	 rules. Each row means a rule, each column the firingDegree of the
	 example j*/
	protected double[][] matrixFiringDegrees;

	// /////////// END VARIABLES /////////////

	// /////////// METHODS /////////////

	// // // // BUILDERS // // // //

    /**
     * Default Constructor. Initiates all basic structures.
     */
	public FARFingrams() {
		super();

		support = new ArrayList<Double>();
		confidence = new ArrayList<Double>();
		lift = new ArrayList<Double>();

		minSupport = 0.0;
		maxSupport = 0.0;
		minLift = 0.0;
		maxLift = 0.0;

		possibleVbleConclusions = new ArrayList<String>();

		ruleUncoveredExamples = false;
		numberUncoveredExamples = 0;
		uncoveredExamples = new ArrayList<Integer>();

	}

                /**
        * Parameter Constructor. Initiates all basic structures, the relation value and the directory to store all resultant files.
     * @param typeRelation given relation value.
     * @param file directory where to store every resultant file.
        */
	public FARFingrams(int typeRelation, String file) {
		super(typeRelation, file);

		support = new ArrayList<Double>();
		confidence = new ArrayList<Double>();
		lift = new ArrayList<Double>();

		minSupport = 0.0;
		maxSupport = 0.0;
		minLift = 0.0;
		maxLift = 0.0;

		possibleVbleConclusions = new ArrayList<String>();

		ruleUncoveredExamples = false;
		numberUncoveredExamples = 0;
		uncoveredExamples = new ArrayList<Integer>();
	}

	// // // // GETS & SETS // // // //

	public List<Double> getSupport() {
		return support;
	}

	public List<Double> getConfidence() {
		return confidence;
	}

	public List<Double> getLift() {
		return lift;
	}

	public List<String> getPossibleVbleConclusions() {
		return possibleVbleConclusions;
	}

	public Double getMinSupport() {
		return minSupport;
	}

	public Double getMaxSupport() {
		return maxSupport;
	}

	public Double getMinLift() {
		return minLift;
	}

	public Double getMaxLift() {
		return maxLift;
	}

	public double[][] getFiringDegrees() {
		return matrixFiringDegrees;
	}

	// // // // ABSTRACT METHODS // // // //

	@Override
        /**
        * Loads all the information needed for the Fingrams object, setting all its variables, 
        * from a .fs file located on fileLocation. 
        * @return the number of rules read.
        */
	public int loadInfoFromFS() {
		try {
			// Initializations
			this.numberAntecedents = new ArrayList<Integer>();

			// Read the information of the 'file' and put into fileContent
			String fileContent = (new AdministrativeStaff())
					.readFileAsString(fileLocation + ".fs");

			// Split the entire file line by line. Blank lines are elminated
			List<String> line = Arrays.asList(fileContent.split("[\\r\\n]+"));

			// Split the second line, that contains variables and labels that
			// are as consequents, by '(', ')', ';', and ' '
			List<String> outVblLabls = Arrays.asList(line.get(1).split(
					"[(); ]+"));

			//System.out.println("outVblLabls: "+outVblLabls+"; outVblLabls.size(): "+outVblLabls.size());
			
			this.numberRules = Integer.parseInt(line.get(3).split("[: ]+")[1]);
			if (numberRules==0){
				return 0;
			}
			for (int i = 0; i < outVblLabls.size(); i += 2) {
				List<String> outLabls = Arrays.asList(outVblLabls.get(i + 1)
						.split("[,]+"));

				for (int j = 0; j < outLabls.size(); j++) {
					possibleVbleConclusions.add(outVblLabls.get(i));
					possibleLabelConclusions.add(outLabls.get(j));
				}
			}

			// Save the number of rules present in the system read
			
			this.numberExamples = Integer
					.parseInt(line.get(4).split("[: ]+")[1]);

			this.matrixRulesXExamples = new byte[numberRules][numberExamples];
			this.matrixFiringDegrees = new double[numberRules][numberExamples];

			// int[] outClasses = new int[numberRules];

			Double ruleSupport0 = Double.parseDouble(line.get(6).split(
					"(=>)[ ]+")[1].split("[() ]+")[2]);

			// antecedentSupport
			Double antecedentSupport0 = Double.parseDouble(line.get(7).split(
					"(=>)[ ]+")[1].split("[() ]+")[2]);

			// consequentSupport
			Double consequentSupport0 = Double.parseDouble(line.get(8).split(
					"(=>)[ ]+")[1].split("[() ]+")[2]);

			// Store the min and max support with the first rule
			minSupport = ruleSupport0;
			maxSupport = ruleSupport0;

			// Store the min and max lift with the first rule
			minLift = (ruleSupport0 / antecedentSupport0) / consequentSupport0;
			maxLift = (ruleSupport0 / antecedentSupport0) / consequentSupport0;

			for (int i = 0; i < numberRules; i += 1) {

				// Jumps over 4 lines every time
				int j = 5 + i * 4;

				String textRule = line.get(j);

				if (line.get(j).split(":")[1].toUpperCase().replaceAll(" ", "")
						.equals("UNCOVEREDEXAMPLES")) {

					ruleUncoveredExamples = true;

					numberUncoveredExamples = line.get(j + 1).split(",").length;

					for (String numberExample : Arrays.asList(line.get(j + 1)
							.split("(,)[ ]+"))) {
						uncoveredExamples.add(Integer.parseInt(numberExample));
					}

				} else {

					rulesIdentifiers.add(line.get(j).split(":")[0]);

					// Obtain the information from file
					// totalruleSupport
					Double totalRuleSupport = Double
							.parseDouble(line.get(j + 1).split("(=>)[ ]+")[1]
									.split("[() ]+")[1]);

					// ruleSupport
					Double meanRuleSupport = Double.parseDouble(line.get(j + 1)
							.split("(=>)[ ]+")[1].split("[() ]+")[2]);

					// antecedentSupport
					Double meanAntecedentSupport = Double.parseDouble(line.get(
							j + 2).split("(=>)[ ]+")[1].split("[() ]+")[2]);

					// consequentSupport
					Double meanConsequentSupport = Double.parseDouble(line.get(
							j + 3).split("(=>)[ ]+")[1].split("[() ]+")[2]);

					// Stores the information
					// totalFiringDegrees
					totalFiringDegrees.add(totalRuleSupport);

					// support
					support.add(meanRuleSupport);

					// confidence
					confidence.add(support.get(i) / meanAntecedentSupport);

					// lift
					lift.add(confidence.get(i) / meanConsequentSupport);

					// System.out.println(rulesIdentifiers.get(i)+ " : "+
					// lift.get(i));

					int numberAntecedentsRule = new AdministrativeStaff()
							.countAntecedents(line.get(j).split("[ ]+"));

					String outVble = line.get(j).split("[ ]+")[line.get(j)
							.split("[ ]+").length - 3];
					String outLabel = line.get(j).split("[ ]+")[line.get(j)
							.split("[ ]+").length - 1];

					int h;
					for (h = 0; h < possibleLabelConclusions.size(); h++) {
						if (possibleVbleConclusions.get(h).equals(outVble)
								&& possibleLabelConclusions.get(h).equals(
										outLabel)) {
							break;
						}
					}

					if (h == possibleLabelConclusions.size())
						System.out
								.println("101 ERROR: Error treating the file "
										+ fileLocation + " in vble " + outVble
										+ " label " + outLabel);

					// List<Integer> listExamplesCoveredByActualRule = new
					// ArrayList<Integer>();
					// List<Double> actualFiringDegree = new
					// ArrayList<Double>();

					// Load rule support
					String first = Arrays
							.asList(line.get(j + 1).split("(=>)[ ]+")[2]
									.split("[, ]+")).get(0);
					if (first.contains("There")) {
					} else {
						for (String aux : Arrays.asList(line.get(j + 1).split(
								"(=>)[ ]+")[2].split("[, ]+"))) {

							int numberExample = Integer.parseInt(aux
									.split("[(]")[0]);

							Double valueFiring = Double.parseDouble(aux
									.split("[()]")[1]);
							// listExamplesCoveredByActualRule.add(Integer.parseInt(numberExample));
							// actualFiringDegree.add(valueFiring);

							matrixRulesXExamples[i][numberExample] = 1;
							matrixFiringDegrees[i][numberExample] = valueFiring;
						}
					}

					if (minSupport > meanRuleSupport)
						minSupport = meanRuleSupport;

					if (maxSupport < meanRuleSupport)
						maxSupport = meanRuleSupport;

					if (minLift > lift.get(i))
						minLift = lift.get(i);

					if (maxLift < lift.get(i))
						maxLift = lift.get(i);

					// listExamplesCoveredByRule.add(listExamplesCoveredByActualRule);
					// firingDegrees.add(actualFiringDegree);

					numberAntecedents.add(numberAntecedentsRule);

					textRules.add(textRule);

				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return numberRules;

	}

    /**
     * Constructs and returns an {@link InfoNode} of the node with the given id.
     * This InfoNode object stores graphical information of a node used when the Figrams are displayed.
     * @param n given id of the node.
     * @return an InfoNode object.
     */
    @Override
	public InfoNode calculateInfoNode(int n) {

		DecimalFormat df = (new AdministrativeStaff()).format();

		String nodeInfo = "R" + rulesIdentifiers.get(n).substring(4) + "\\n"
				+ " (sup=" + df.format(support.get(n)) + ") \\n" + " (conf="
				+ df.format(confidence.get(n)) + ") \\n (lift="
				+ df.format(lift.get(n)) + ")";

		String nodeInfoToolTipText = this.textRules.get(n) + " (sup="
				+ df.format(support.get(n)) + "; conf="
				+ df.format(confidence.get(n)) + "; lift="
				+ df.format(lift.get(n)) + ")";

		int ind = 100 - (int) ((lift.get(n) - minLift) * (100 / (maxLift - minLift)));

		/*
		 * if (lift.get(n)>2.0) {
		 * System.out.println(this.textRules.get(n)+" ("+lift.get(n)+")"); }//
		 */

		String nodeColor = "grey" + String.valueOf(ind);

		String borderColor = nodeColor;

		if (borderColor.equals("grey100"))
			borderColor = "grey0";

		// Set the font color
		String fontColor = "black";
		if (ind <= 50)
			fontColor = "white";

		// Calculate the size of the node according to its support
		double nodeSize = 1 + 10 * ((double) (support.get(n) - minSupport) / (double) (maxSupport - minSupport));

		// Set the font size
		float fontSize = 10 + 1.8f * ((int) nodeSize);

		
		InfoNode in = new InfoNode(nodeInfo, nodeInfoToolTipText, nodeSize,
				nodeColor, borderColor, fontSize, fontColor);

		return in;
	}

	// // // // OVERRIDE METHODS // // // //

	@Override
	/*
	 * public void generateMatrix() { if (typeRelation == 0) {
	 * super.generateUndirectedMatrix(); } else { if (typeRelation == 1) {
	 * generateDirectedMatrix(); } }
	 */
        
	/**
	 * Generates the complete directed social network matrix with the list of examples covered by each rule.
	 * <code> 
	 * mij=1-(\frac{\sum_{x \in X} |FDi(x)-FDj(x)|}{\sum_{x \in X} FDi(x)}
	 * </code> 
	 * being x the examples that fires rules i and FDi(x) the level up to which
	 * the example x fires the rule i. Note that if an example z does not fire a
	 * rule j, FDj(z)=0
	 * 
	 */
	protected void generateDirectedMatrix() {

		socialNetwork = new ArrayList<List<Double>>(numberRules);
		// List<Float> totalFiringDegrees = new ArrayList<Float>();

		// Create the identity matrix in result
		/*
		 * for (int i = 0; i < numberRules; i++) { socialNetwork.add(new
		 * ArrayList<Double>()); for (int j = 0; j < numberRules; j++) {
		 * socialNetwork.get(i).add(0.0);
		 * 
		 * }
		 * 
		 * }
		 */

		for (int i = 0; i < numberRules; i++) {

			byte[] rulei = matrixRulesXExamples[i];
			socialNetwork.add(new ArrayList<Double>());

			// We calculate the denominator of the metric formula
			double denomi = 0;
			for (int k = 0; k < rulei.length; k++) {
				if (rulei[k] == 1)
					denomi += matrixFiringDegrees[i][k];
			}

			for (int j = 0; j < numberRules; j++) {

				if (i == j) {
					socialNetwork.get(i).add(0.0);
				} else {
					double numij = 0;
					byte[] rulej = matrixRulesXExamples[j];

					for (int k = 0; k < rulei.length; k++) {

						if (rulei[k] == 1) {
							if (rulej[k] == 1) {
								numij += Math.abs(matrixFiringDegrees[i][k]
										- matrixFiringDegrees[j][k]);
							} else {
								numij += matrixFiringDegrees[i][k];
							}

						}// if

					}// for k

					socialNetwork.get(i).add(1 - (numij / denomi));

				}// for j
			}// if i!=j

		}// for i
	}

	/*
	 * private void generateDirectedMatrix2() {
	 * 
	 * socialNetwork = new ArrayList<List<Double>>(numberRules); // List<Float>
	 * totalFiringDegrees = new ArrayList<Float>();
	 * 
	 * // Create the identity matrix in result for (int i = 0; i < numberRules;
	 * i++) { socialNetwork.add(new ArrayList<Double>()); for (int j = 0; j <
	 * numberRules; j++) { socialNetwork.get(i).add(0.0);
	 * 
	 * }
	 * 
	 * }
	 * 
	 * for (int i = 0; i < numberRules; i++) {
	 * 
	 * for (int j = 0; j < listExamplesCoveredByRule.get(i).size(); j++) { int
	 * examplei = listExamplesCoveredByRule.get(i).get(j);
	 * 
	 * for (int k = 0; k < numberRules; k++) {
	 * 
	 * for (int l = 0; l < listExamplesCoveredByRule.get(k).size(); l++) { int
	 * examplek = listExamplesCoveredByRule.get(k).get(l);
	 * 
	 * if (l == 0) { Double previous = socialNetwork.get(i).get(k); Double
	 * currentValue = firingDegrees.get(i).get(j); socialNetwork.get(i) .set(k,
	 * previous + currentValue); } if (examplei == examplek) { Double previous =
	 * socialNetwork.get(i).get(k); Double currentValue =
	 * Math.abs(firingDegrees.get(i) .get(j) - firingDegrees.get(k).get(l)); //
	 * currentValue=1f; socialNetwork.get(i).set( k, previous -
	 * firingDegrees.get(i).get(j) + currentValue); } } } }
	 * 
	 * }
	 * 
	 * for (int i = 0; i < numberRules; i++) { for (int j = 0; j < numberRules;
	 * j++) { if (totalFiringDegrees.get(i) != 0 && totalFiringDegrees.get(j) !=
	 * 0 && i != j) { socialNetwork .get(i) .set(j, 1 -
	 * (socialNetwork.get(i).get(j) / totalFiringDegrees .get(i)));
	 * 
	 * } } }
	 * 
	 * }
	 */

	public void calculateLift(String file) {

		try {

			// Read file 'file' and store its info in fileContent
			String fileContent = (new AdministrativeStaff())
					.readFileAsString(file);

			// Split the entire file line by line
			String[] line = fileContent.split("[\\r\\n]+");

			// Get the number of rules from the file
			numberRules = Integer.parseInt(line[3].split("[: ]+")[1]);

			// // // Obtain the information for the first rule, just to store
			// the min and max values of lift and support
			int j = 4;
			// Obtain the information from file
			// ruleSupport
			Double ruleSupport = Double.parseDouble(line[j + 1]
					.split("(=>)[ ]+")[1].split("[() ]+")[2]);

			// antecedentSupport
			Double antecedentSupport = Double.parseDouble(line[j + 2]
					.split("(=>)[ ]+")[1].split("[() ]+")[2]);

			// consequentSupport
			Double consequentSupport = Double.parseDouble(line[j + 3]
					.split("(=>)[ ]+")[1].split("[() ]+")[2]);

			// Stores the support of the rule
			support.add(ruleSupport);

			// confidence
			confidence.add(support.get(0) / antecedentSupport);

			// lift
			lift.add(confidence.get(0) / consequentSupport);

			// Store the min and max support with the first rule
			minSupport = ruleSupport;
			maxSupport = ruleSupport;

			// Store the min and max lift with the first rule
			minLift = lift.get(0);
			maxLift = lift.get(0);

			// Iterate among the rules to calculate confidence, support and lift
			for (int i = 1; i < numberRules; i += 1) {

				// Jumps over 4 lines every time
				j = 4 + i * 4;

				// Obtain the information from file
				// ruleSupport
				ruleSupport = Double
						.parseDouble(line[j + 1].split("(=>)[ ]+")[1]
								.split("[() ]+")[2]);

				// antecedentSupport
				antecedentSupport = Double.parseDouble(line[j + 2]
						.split("(=>)[ ]+")[1].split("[() ]+")[2]);

				// consequentSupport
				consequentSupport = Double.parseDouble(line[j + 3]
						.split("(=>)[ ]+")[1].split("[() ]+")[2]);

				// Stores the support of the rule
				support.add(ruleSupport);

				// confidence
				confidence.add(support.get(i) / antecedentSupport);

				// lift
				lift.add(confidence.get(i) / consequentSupport);

				if (minSupport > ruleSupport)
					minSupport = ruleSupport;

				if (maxSupport < ruleSupport)
					maxSupport = ruleSupport;

				if (minLift > lift.get(i))
					minLift = lift.get(i);

				if (maxLift < lift.get(i))
					maxLift = lift.get(i);

			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void generateScienceMapFile(double MaxThr) {
	}

	@Override
	public String createNodeUncoveredExamples() {
		// TODO Auto-generated method stub
		String node;

		double coverage = (double) numberUncoveredExamples
				/ this.numberExamples;

		DecimalFormat df = new AdministrativeStaff().format();

		String nodeInfo = "UNCOVERED\\nEXAMPLES\\n(cov=" + df.format(coverage)
				+ ")";
		String nodeInfoToolTipText = "UNCOVERED EXAMPLES (cov="
				+ df.format(coverage) + "): ";

		for (int i = 0; i < uncoveredExamples.size(); i++) {
			nodeInfoToolTipText += uncoveredExamples.get(i) + ", ";
		}

		String nodeColor = "\"red\"";

		String fontColor = "black";

		double nodeSize = 1 + 4 * coverage;

		// Set the font size
		float fontSize = 15 + 2 * ((int) nodeSize);

		node = "UNCOVERED [shape=circle,style=filled,height="
				+ df.format(nodeSize) + ",width=" + df.format(nodeSize)
				+ ",fixedsize=true" + ",peripheries=0" + ",fillcolor="
				+ nodeColor + ",fontsize=" + fontSize + ",fontcolor="
				+ fontColor + ",label=\"" + nodeInfo + "\"" + ",tooltip=\""
				+ nodeInfoToolTipText + "\"];\n\t";

		return node;
		// return "";
	}

	@Override
	public boolean hasRuleUncoveredExamples() {
		// TODO Auto-generated method stub
		// System.out.println(ruleUncoveredExamples);
		examplesCoveredByASingleRule();
		return ruleUncoveredExamples;
	}

	@Override
	public void printFingramsLegend() {

		try {
			DecimalFormat df = (new AdministrativeStaff()).format();
			int numberConclusions = this.possibleLabelConclusions.size();
			int elementsLegend = numberConclusions;
			if (this.hasRuleUncoveredExamples()) {
				elementsLegend++;
			}
			String headLegend = (new AdministrativeStaff()).headLegend(
					elementsLegend, "Output class rules");
			PrintStream pslegend = new PrintStream(new FileOutputStream(
					this.fileLocation + ".legend.svg", false));

			pslegend.print(headLegend);

			for (int n = 0; n < 3; n++) {
				String stroke = "black";
				String color = "grey";
				String label = "";

				stroke = color;

				if (n == 0) {
					color = "black";
					label = "The highest lift (" + df.format(maxLift) + ")";
				} else if (n == 1) {
					color = "grey";
					label = "Medium lift";
				} else if (n == 2) {
					color = "white";
					stroke = "black";
					label = "The lowest lift (" + df.format(minLift) + ")";
				}

				int id = n + 1;
				pslegend.println("<g id=\"node" + id
						+ "\" class=\"node\"><title></title>");
				int x = 125;
				int y = -405 + n * 55;
				pslegend.println("<ellipse fill=\"" + color + "\" stroke=\""
						+ stroke + "\" cx=\"" + x + "\" cy=\"" + y
						+ "\" rx=\"25\" ry=\"25\"/>");

				int dy = y + 5;
				pslegend.print("<text text-anchor=\"start\" x=\"170\" y=\""
						+ dy
						+ "\" font-family=\"Times New Roman,serif\" font-size=\"18.00\">");

				pslegend.println(label + "</text>");
				pslegend.println("</g>");
			}

			pslegend.println("</g>");
			pslegend.println("</svg>");
			pslegend.flush();
			pslegend.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private void examplesCoveredByASingleRule() {
		int count = 0;

		for (int i = 0; i < numberExamples; i++) { // Iterate
													// over the
													// examples
			count = 0;
			for (int j = 0; j < matrixRulesXExamples.length; j++) { // Iterate
																	// over
																	// rules
				if ((byte) (matrixRulesXExamples[j][i]) == 1) {
					count++;
				}

			}
			if (count == 0)
				System.out.println("Example: " + i
						+ " not covered by any rule");
		}

	}
}
