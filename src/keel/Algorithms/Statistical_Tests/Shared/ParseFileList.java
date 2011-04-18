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

/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 24/02/2005
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Statistical_Tests.Shared;

import keel.Algorithms.Shared.Parsing.*;
import org.core.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.*;

public class ParseFileList {
	/**
	* <p>
	* Parse a list of files and perform certain the statistical test identified by 'selector' over them
	* </p>
	*/
    public void statisticalTest(int selector, boolean tty, ProcessConfig pc) {
        Vector nameResults = new Vector();
        String line = new String();
        ProcessDataset pd = new ProcessDataset();
        double sigLevel = pc.parSignificanceLevel;

        // The list of files is arranged in a cubic matrix
        // First index: data file
        // Second index: pattern
        // Third index: output
        Vector data = new Vector();

        int x = 0, y = 0, z = 0;
        BufferedReader in;
        StringTokenizer tokens = new StringTokenizer(line, "\"");
        String tmp, pattern, tmp1;
        double dv;

        int nFiles = 0, nPatterns = 0, nOutputs = 0;
        boolean firstFile = true, firstLine = true;
        String listOfNames = new String();
        String lastRel = new String();
        String[] labels = null;

        while (true) {

            if (nFiles >= pc.parInputData.size()) {
                break;
            }

            tmp = (String) pc.parInputData.get(nFiles);

            if (tmp.length() > 0) {

                System.out.println("Procesing data file [" + tmp + "]");

                nameResults.add(new String(tmp));

                try {
                    // Extract the name of the dataset
                    in = new BufferedReader(new FileReader(tmp));

                    labels = pc.skipHeader(in);
                    if (!lastRel.equals(pc.getRelation())) {

                        lastRel = pc.getRelation();

                        listOfNames += pc.getRelation() + " ";

                    }

                    data.add(new Vector());
                    y = 0;
                    nFiles++;

                    do {
                        pattern = in.readLine();
                        if (pattern == null) {
                            break;
                        }

                        Vector vtmp = (Vector) data.get(x);
                        vtmp.add(new Vector());

                        z = 0;

                        if (firstFile) {
                            nPatterns++;
                        }

                        StringTokenizer tk = new StringTokenizer(pattern, " ");

                        while (tk.hasMoreTokens()) {

                            tmp1 = tk.nextToken();

                            if (labels == null) {
                                dv = Double.parseDouble(tmp1);
                            }

                            else {

                                // Let's search the label
                                dv = labels.length;

                                for (int i = 0; i < labels.length; i++) {
                                    if (tmp1.equalsIgnoreCase(labels[i])) {
                                        dv = i;
                                        break;
                                    }
                                }
                                if (dv == labels.length) {
                                    // pass integer values
                                    if (Pattern.matches("(\\d)*", tmp1)) {
                                        System.out.println("WARNING: label [" +
                                                tmp1 +
                                                "] found, assuming integer output");
                                        dv = Double.parseDouble(tmp1);
                                    } else {
                                        if (tmp1.equalsIgnoreCase(
                                                "unclassified")||tmp1.equalsIgnoreCase("?")) {
                                            dv = -1;
                                        } else {
                                            System.out.println(
                                                    "WARNING: label [" + tmp1 +
                                                    "] not found");
                                        }
                                    }
                                }
                            }

                            Vector vvtmp = (Vector) (vtmp.get(y));

                            vvtmp.add(new Double(dv));

                            if (firstLine) {
                                nOutputs++;
                            }

                            z++;
                        }
                        firstLine = false;
                        y++;

                    } while (true);
                    x++;
                } catch (FileNotFoundException e) {
                    System.err.println(e + " Configuration file not found");
                } catch (IOException e) {
                    System.err.println(e + " Input error");
                } catch (Exception e) {
                    System.err.println(e + " Invalid data");
                }
                firstFile = false;
            }
        }

        // Parse the vector of names and guess the number of experiments

        Vector indexes = new Vector();
        String lastMethod = new String("");
        String header = new String("");
        Vector Vheader = new Vector();

        String dataset = new String("Sin Inicializar");
        int lastIndex = -1;

        for (int i = 0; i < nameResults.size(); i++) {

            String name = (String) (nameResults.get(i));
            String fields[] = name.split("/");
            dataset = fields[3];

            if (!lastMethod.equals(fields[2])) {
                // New method
                indexes.add(new Vector());
                lastIndex++;
                lastMethod = fields[2];
                header += fields[2] + " ";
                Vheader.add(new String(fields[2]));
            }
            ((Vector) indexes.get(lastIndex)).add(new Integer(i));
        }

        System.out.println("Results:");
        System.out.println("Detected " + indexes.size() + " methods");
        System.out.print("Folds=");
        int nFolds = ((Vector) indexes.get(0)).size();

        nFolds /= 2; // Half of files for test

        for (int i = 0; i < indexes.size(); i++) {

            System.out.print(((Vector) indexes.get(i)).size() / 2 + " ");

        }

        System.out.println();
        double[][][][] d;
        double[][][][] dtrain;

        //If the test to be run is not the Global Wilcoxon Test or the Friedman test we
        //check if the number of results is different among the executed algorithms
        if ((selector != StatTest.globalWilcoxonC) && (selector != StatTest.globalWilcoxonR) &&
            (selector != StatTest.FriedmanC) && (selector != StatTest.FriedmanR)&& 
            (selector != StatTest.FriedmanAlignedC) && (selector != StatTest.FriedmanAlignedR)&& 
            (selector != StatTest.QuadeC) && (selector != StatTest.QuadeR)&& 
            (selector != StatTest.ContrastC) && (selector != StatTest.ContrastR)&& 
            (selector != StatTest.MultipleC) && (selector != StatTest.MultipleR)&&
            (selector != StatTest.globalWilcoxonI) && (selector != StatTest.FriedmanI)) {
        	
        	
            for (int i = 0; i < indexes.size(); i++) {
                if (nFolds != ((Vector) indexes.get(i)).size() / 2) {
                    System.out.println("Error: different number of folds");
                    return;
                }
            }
            // Process test files
                d = new double[1][data.size() / 2][][];

                int i = 0;

                for (int i1 = 0; i1 < data.size(); i1++) {
                    //if (i1 % (2 * nfolds) < nfolds) {
                    if (i1 % (2 * nFolds) < nFolds) {
                        Vector vtmp = (Vector) (data.get(i1));
                        d[0][i] = new double[vtmp.size()][];
                        for (int j = 0; j < vtmp.size(); j++) {
                            Vector vvtmp = (Vector) (vtmp.get(j));
                            d[0][i][j] = new double[vvtmp.size()];
                            for (int k = 0; k < vvtmp.size(); k++) {
                                Double vd = (Double) (vvtmp.get(k));
                                d[0][i][j][k] = vd.doubleValue();
                            }
                        }
                        i++;
                    }
                }

                dtrain = new double[1][data.size() / 2][][];

                i = 0;

                for (int i1 = 0; i1 < data.size(); i1++) {
                    if (i1 % (2 * nFolds) >= nFolds) {
                        Vector vtmp = (Vector) (data.get(i1));
                        dtrain[0][i] = new double[vtmp.size()][];
                        for (int j = 0; j < vtmp.size(); j++) {
                            Vector vvtmp = (Vector) (vtmp.get(j));
                            dtrain[0][i][j] = new double[vvtmp.size()];
                            for (int k = 0; k < vvtmp.size(); k++) {
                                Double vd = (Double) (vvtmp.get(k));
                                dtrain[0][i][j][k] = vd.doubleValue();
                            }
                        }
                        i++;
                    }
               }
        } else {
            int i, it, cumulated;
            i = it = cumulated = 0;
            d = new double[indexes.size()][][][]; //d[#Alg][#nfolds][#res][#sal];
            dtrain = new double[indexes.size()][][][]; //d[#Alg][#nfolds][#res][#sal];
            for (int h = 0; h < indexes.size(); h++) {
                nFolds = ((Vector) indexes.get(h)).size() / 2;
                d[h] = new double[nFolds][][];
                for (int i1 = cumulated, tst = 0; i1 < (cumulated) + nFolds; i1++, tst++) { //Test
                    Vector vtmp = (Vector) (data.get(i1));
                    d[h][tst] = new double[vtmp.size()][];
                    for (int j = 0; j < vtmp.size(); j++) {
                        Vector vvtmp = (Vector) (vtmp.get(j));
                        d[h][tst][j] = new double[vvtmp.size()];
                        for (int k = 0; k < vvtmp.size(); k++) {
                            Double vd = (Double) (vvtmp.get(k));
                            d[h][tst][j][k] =  vd.doubleValue();
                        }
                    }
                    i++;
                }
                cumulated += nFolds;
                dtrain[h] = new double[nFolds][][];
                for (int i1 = cumulated,tr = 0; i1 < (cumulated) + nFolds; i1++, tr++) { //train
                    Vector vtmp = (Vector) (data.get(i1));
                    dtrain[h][tr] = new double[vtmp.size()][];
                    for (int j = 0; j < vtmp.size(); j++) {
                        Vector vvtmp = (Vector) (vtmp.get(j));
                        dtrain[h][tr][j] = new double[vvtmp.size()];
                        for (int k = 0; k < vvtmp.size(); k++) {
                            Double vd = (Double) (vvtmp.get(k));
                            dtrain[h][tr][j][k] = vd.doubleValue();
                        }
                    }
                    it++;
                }
                cumulated += nFolds;
        }

    }


    String resultName;
    resultName = pc.parResultTrainName;


    // Statistic test
    StatTest mySt = new StatTest(selector, d, dtrain, sigLevel, resultName, listOfNames,
                                 nameResults, labels);
}
}
