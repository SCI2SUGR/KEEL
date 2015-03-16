package keel.Algorithms.UnsupervisedLearning.AssociationRules.FuzzyRuleLearning.Fingrams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdministrativeStaff {

	protected String readFileAsString(String filePath)
			throws java.io.IOException {

		StringBuilder fileData = new StringBuilder();

		BufferedReader reader = new BufferedReader(

		new FileReader(filePath));

		char[] buf = new char[1024];

		int numRead = 0;

		while ((numRead = reader.read(buf)) != -1) {

			String readData = String.valueOf(buf, 0, numRead);

			fileData.append(readData);

			buf = new char[1024];

		}

		reader.close();
		return fileData.toString();

	}

	/*
	 * function writeStringToFile
	 * 
	 * Need: A file location and the content to be written
	 * 
	 * Produce: Nothing
	 * 
	 * Modify: It writes the content in the file
	 */
	public void writeStringToFile(String file, String content) {
		try {
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(content);
			// Close the output stream
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	protected DecimalFormat format() {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		df.setMinimumFractionDigits(3);
		DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
		dfs.setDecimalSeparator((new String(".").charAt(0)));
		df.setDecimalFormatSymbols(dfs);
		df.setGroupingSize(20);
		return df;

	}

	protected int runProcess(String command) {
		int result = -1;
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(
				Arrays.asList(command.split(" ")));

		try {
			result = commandExecutor.executeCommand();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error runProcess IOException");
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println("Error runProcess InterruptedException");
			e.printStackTrace();
		}

		// Get the output from the command
		// StringBuilder stdout =
		// commandExecutor.getStandardOutputFromCommand();
		// StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();
		//System.out.println("runProcess correctly executed");
		return result;
	}

	protected String platform() {
		String os = System.getProperty("os.name");
		if (os != null) {
			if (os.startsWith("Windows"))
				return "Windows";
			else if (os.startsWith("Mac"))
				return "Mac";
			else if (os.startsWith("Linux"))
				return "Linux";
		}
		return "Error";
	}

	protected String obtainPathDot() {
		String result = "";
		String platform = this.platform();

		if (System.getProperty("graphpath") != null) {
			result = System.getProperty("graphpath")
					+ System.getProperty("file.separator") + "dot";
			if (platform.equals("Windows")) {
				result += ".exe";
			}
		} else {
			if (platform.equals("Windows")) {
				result = "C:\\Program Files (x86)\\Graphviz2.36\\bin\\dot.exe";
			} else {
				if (platform.equals("Mac")) {
					result = "/usr/local/bin/dot";
				} else {
					if (platform.equals("Linux")) {
						result = "/usr/local/bin/dot";
					}
				}
			}
		}
		return result;

	}

	protected String obtainPathPathfinder() {
		String result = "";
		String platform = this.platform();

		if (System.getProperty("pathfinderpath") != null) {
			result = System.getProperty("pathfinderpath")
					+ System.getProperty("file.separator") + "fast-pathfinder";
			if (platform.equals("Windows")) {
				result += ".exe";
			}
		} else {
			if (platform.equals("Windows")) {
				result = System.getProperty("fingrampath")
						+ "libs\\pathfinder\\fast-pathfinder.exe";
			} else {
				if (platform.equals("Mac")) {
					result = "/Users/dapepan/Documents/workspace/FingramsGenerator/libs/pathfinder/fast-pathfinder";
				} else {
					if (platform.equals("Linux")) {
						result = System.getProperty("fingrampath")
								+ "libs/pathfinder/fast-pathfinder";
					}
				}
			}
		}
		return result;

	}

	protected int writeParsedSVGBuffer(String fileName) {
		int result = -1;

		try {

			// Open the original SVG file
			BufferedReader reader = new BufferedReader(new FileReader(fileName));

			// Open the destination aux file
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					fileName + "aux"), false));

			// Each of the lines will be available here
			String strLine;

			while ((strLine = reader.readLine()) != null) {

				if (strLine.contains("<!-- Rule")
						|| strLine.contains("<!-- UNCOVERED")) {
					// System.out.println(strLine);
					bw.write(strLine);
					bw.newLine();

					String first = reader.readLine();
					String second = reader.readLine();

					String[] splits = first.split("<title>");
					String[] splits2 = second.split("xlink:title=\"");

					strLine = splits[0] + "<title>"
							+ splits2[1].substring(0, splits2[1].length() - 2)
							+ "</title>";
					// System.out.println(strLine);
					bw.write(strLine);
					bw.newLine();

					// System.out.println(second);
					bw.write(second);
					bw.newLine();

				} else {
					bw.write(strLine);
					bw.newLine();
				}

			}

			reader.close();
			// System.out.println("END");
			// Close the input stream

			bw.flush();
			bw.close();

			File originFile = new File(fileName + "aux");
			File targetFile = new File(fileName);

			targetFile.delete();

			if (originFile.renameTo(targetFile)) {
			} else {

				System.out.println("Error: The File " + fileName
						+ " was not correctly created.");

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			result = -1;
		} catch (IOException e) {
			e.printStackTrace();
			result = -1;
		}
		return result;
	}

	public void writeScienceMapToFile(String fileName,
			List<String> rulesIdentifiers,
			List<String> possibleLabelConclusions,
			List<Integer> conclusionRules, List<Integer> numberAntecedents,
			List<List<Double>> socialNetwork, double MaxThr) {

		DecimalFormat df = format();

		try {
			// Open the destination aux file
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
					fileName + ".scienceMap"), false));

			bw.write("Rule length\n===========\n// RuleName (outputClass) => Nb of Premises);\n");
			bw.write("Rules: " + rulesIdentifiers.size() + "\n");

			int numberRules = rulesIdentifiers.size();

			for (int i = 0; i < numberRules; i++) {
				bw.write(rulesIdentifiers.get(i) + " ("
						+ possibleLabelConclusions.get(conclusionRules.get(i))
						+ ")  => " + numberAntecedents.get(i) + "\n");
			}

			bw.write("***********\nSimultaneously Fired Rules (Standardized cofiring measure)\n==========================================================\n");
			double sumWeights = 0;
			for (int i = 0; i < numberRules; i++) {
				for (int j = 0; j < numberRules; j++) {
					sumWeights += (numberAntecedents.get(i) + numberAntecedents
							.get(j)) * socialNetwork.get(i).get(j);
					bw.write(df.format(socialNetwork.get(i).get(j)) + " ");
				}
				bw.write("\n");
			}

			bw.write("**********************************************************\nCI(S)=");

			if (sumWeights > MaxThr) {
				bw.write(0);
			} else
				bw.write(df.format(1 - Math.sqrt(sumWeights / MaxThr)));
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected int countAntecedents(String[] split) {
		// TODO Auto-generated method stub

		String and = "AND";
		int count = 1;
		for (String aux : split) {
			if (aux.equals(and))
				count += 1;
		}
		return count;
	}

	protected String headLegend(int numberConclusions, String title) {

		String head = "";
		head += "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n";
		head += "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.1//EN\"\n";
		head += "\"http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd\">\n";
		head += "<!-- Title: G Pages: 1 -->\n";
		// pslegend.println("<svg width=\"300pt\" height=\""+String.valueOf(100+55*(NbOutLabels+1))+"pt\"");
		// pslegend.println("viewBox=\"0.00 300.00 0.00 "+String.valueOf(100+55*(NbOutLabels+1))+".00 \" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");
		int height = 60 * numberConclusions;
		head += "<svg width=\"600pt\" height=\"" + height + "pt\"\n";
		head += " viewBox=\"40.00 300.00 600 "
				+ height
				+ "\" xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\n";
		head += "<g id=\"graph1\" class=\"graph\" transform=\"scale(0.840651 0.840651) rotate(0) translate(4 852.479)\">\n";
		head += "<polygon fill=\"white\" stroke=\"white\" points=\"-4,5 -4,-852.479 571.116,-852.479 571.116,5 -4,5\"/>\n";
		head += "<text text-anchor=\"start\" x=\"125\" y=\"-460\" font-family=\"Times New Roman,serif\" font-size=\"28.00\" fill=\"black\">"
				+ title + "</text>\n";

		return head;

	}

	protected List<List<Double>> pathfinder(List<List<Double>> originalW, int q) {

		Double maximum, localMinimum, minimum;

		int num_nodes = originalW.size();

		// System.out.println("num_nodos = " + num_nodos);

		// Initiliaze W and D
		List<List<List<Double>>> W = new ArrayList<List<List<Double>>>();
		List<List<Double>> D = new ArrayList<List<Double>>();
		List<List<Double>> PFNET = new ArrayList<List<Double>>();

		for (int i = 0; i < q; i++) {
			W.add(new ArrayList<List<Double>>());
			for (int j = 0; j < num_nodes; j++) {
				W.get(i).add(new ArrayList<Double>());
				if (i == 0) {
					D.add(new ArrayList<Double>());
					PFNET.add(new ArrayList<Double>());
				}
				for (int k = 0; k < num_nodes; k++) {

					if (i == 0) {
						W.get(i).get(j).add(originalW.get(j).get(k));
						D.get(j).add(0.0);
						PFNET.get(j).add(0.0);
					} else {
						W.get(i).get(j).add(0.0);
					}

				}
			}
		}

		// Calculo de las q matrices de pesos
		for (int i = 1; i < q; i++) {
			for (int j = 0; j < num_nodes; j++) {
				for (int k = 0; k < num_nodes; k++) {
					maximum = 0.0;
					for (int z = 0; z < num_nodes && j != k; z++) {
						if (W.get(0).get(j).get(z) != 0.0
								&& W.get(i - 1).get(z).get(k) != 0.0 && z != k
								&& z != j) {
							localMinimum = 0.0;
							if (W.get(0).get(j).get(z) < W.get(i - 1).get(z)
									.get(k))
								localMinimum = W.get(0).get(j).get(z);
							else
								localMinimum = W.get(i - 1).get(z).get(k);

							if (localMinimum > maximum && localMinimum != 0)
								maximum = localMinimum;
						}
					}
					if (maximum != 0.0)
						W.get(i).get(j).set(k, maximum);
					else
						W.get(i).get(j).set(k, 0.0);
				} // for k
			} // for j
		} // for i

		// Calculate the matriz of distances
		for (int i = 0; i < num_nodes; i++) {
			for (int j = 0; j < num_nodes; j++) {
				maximum = 0.0;
				for (int k = 0; k < q; k++)
					if (W.get(k).get(i).get(j) > maximum
							&& W.get(k).get(i).get(j) != 0.0)
						maximum = W.get(k).get(i).get(j);
				if (maximum != 0.0)
					D.get(i).set(j, maximum);
				else
					D.get(i).set(j, 0.0);

			} // for j
		} // for i

		// Obtain the PFNET from the weights and distances matrices
		for (int i = 0; i < num_nodes; i++) {
			for (int j = 0; j < num_nodes; j++) {
				if (D.get(i).get(j) == W.get(0).get(i).get(j)
						&& W.get(0).get(i).get(j) != 0.0)
					PFNET.get(i).set(j, originalW.get(i).get(j));
				/*
				 * else if (W[0][i][j]!=0.0) PFNET[i][j] = FALSE;
				 */
			}
		}

		return PFNET;
	}

	protected List<List<Double>> fastPathfinder(List<List<Double>> originalW,
			int q) {

		Double minimum;

		int numNodes = originalW.size();

		// Define W, distances and PFNET
		List<List<Double>> W = new ArrayList<List<Double>>();
		List<List<Integer>> distances = new ArrayList<List<Integer>>();
		List<List<Double>> PFNET = new ArrayList<List<Double>>();

		// Initialite W, distances and PFNET
		// W stores the same information of originalW
		// distances stores
		// 1 if it is a direct link from i to j
		// 0 if i=j and
		// MAX if there is not direct link
		// PFNET is initialize to 0s

		for (int i = 0; i < numNodes; i++) {
			W.add(new ArrayList<Double>(numNodes));
			distances.add(new ArrayList<Integer>(numNodes));
			PFNET.add(new ArrayList<Double>(numNodes));

			for (int j = 0; j < numNodes; j++) {

				PFNET.get(i).add(0.0);
				if (originalW.get(i).get(j) > 0.0) {
					W.get(i).add(originalW.get(i).get(j));
				} else {
					W.get(i).add(0.0);
				}

				if (originalW.get(i).get(j) > 0.0 && i != j) {
					distances.get(i).add(1);
				} else {
					if (i == j) {
						distances.get(i).add(0);
					} else {
						distances.get(i).add(Integer.MAX_VALUE);
					}

				}// else

			}// for j
		}// for i

		// Compute the weight matrix for each iteration
		// The result is stored in W if a new path, with less than q jumps is
		// found
		// The matrix of distances is updated with the new min-jump path

		for (int k = 0; k < numNodes; k++) {
			for (int i = 0; i < numNodes; i++) {

				int beg = 0;

				for (int j = beg; j < numNodes; j++) {

					minimum = Math.min(W.get(i).get(k), W.get(k).get(j));

					if (W.get(i).get(j) < minimum
							&& (distances.get(i).get(k) + distances.get(k).get(
									j)) <= q) {
						W.get(i).set(j, minimum);

						if (i != j) {
							distances.get(i).set(
									j,
									distances.get(i).get(k)
											+ distances.get(k).get(j));
						}

					} // if(pesos[i][j] < minimo)

				} // for j

				// W[i][i] = 0;

			} // for i

		} // for k

		// Calculate the matrix PFNET from originalW and W
		for (int i = 0; i < numNodes; i++) {
			// Start at (i+1) if the matrix is symmetrical, or 0 if not.
			int beg = 0;
			for (int j = beg; j < numNodes; j++) {
				if (W.get(i).get(j) == originalW.get(i).get(j)
						&& originalW.get(i).get(j) != 0.0)
					PFNET.get(i).set(j, originalW.get(i).get(j));

			}
		}
		return PFNET;
	}

}
