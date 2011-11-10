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
 *
 * File: Algorithm.java
 *
 * This class creates a report in the exoeriment directory.
 * A file "report.txt" is created in the same result directory
 *
 * @author Written by Juan Carlos Fern�ndez Caballero 13-03-2007
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CreateInform {

    static final int CLASSIFICATION = 0;
    static final int REGRESSION = 1;
    private int experimentType;
    private String[] readingFiles = null;
    private String[] classes = null;
    private FileReader fr = null;
    private BufferedReader br = null;
    private FileWriter fw = null;
    private BufferedWriter bw = null;
    //training and test
    private String set = "";
    private int[][] confussionMatrix = null;
    private int nPartitions = -1;
    private List<Double> ECMPartitionList = null;
    private double ecmBest = 0.0,  ecmMean = 0.0,  ecmDev = 0;

    /**
     * Builder
     *
     * @param	path	report path without extension
     * @param	files	result files path
     * @param	problemType	type of problem (classification, regression)
     */
    public CreateInform(String path, String[] files, int problemType) {
        experimentType = problemType;
        this.readingFiles = files;
        //Creation report.txt
        try {
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method has to invoque for to create the report.
     * Verify the type of problem, type partition and paths for
     * to create the report. Read in iterative way the files of
     * results
     */
    public void execute() {
        StringTokenizer st = null;
        int totalAciertos = 0;
        int totalInstancias = 0;
        String porcentajesParticiones = "";
        String porcentajesCuadraticosParticiones = "";
        double porcentajeTotal = 0.0;
        String relation = "";
        double ecmTotal = 0.0;

        if (experimentType == CLASSIFICATION) {
            this.calcularClases();
        }

        //for training and test
        for (int p = 0; p < 2; p++) {
            totalInstancias = 0;
            totalAciertos = 0;
            porcentajesParticiones = "";
            porcentajesCuadraticosParticiones = "";
            ecmTotal = 0.0;
            String esperada = "";
            String obtenida = "";
            int numeroInstancias = 0;
            nPartitions = 0;
            ecmBest = 0.0;
            ecmMean = 0.0;
            ecmDev = 0;
            ECMPartitionList = new ArrayList<Double>();

            if (p == 0) {
                set = "training";
            } else {
                set = "test";
            }

            //for each file
            for (int i = p; i < readingFiles.length; i = i + 2) {
                nPartitions++;
                try {
                    fr = new FileReader(readingFiles[i]);
                    br = new BufferedReader(fr);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                switch (experimentType) {
                    case CLASSIFICATION:
                        int aciertos = 0;
                        String cadena = "";
                        numeroInstancias = 0; //>
                        double porcentajeParcial = 0.0;

                        try {
                            cadena = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while (cadena != null) {
                            if (cadena.startsWith("@") == false) {
                                st = new StringTokenizer(cadena);
                                esperada = st.nextToken();
                                obtenida = st.nextToken();
                                if (obtenida.equals(esperada) == true) {
                                    aciertos++;
                                }
                                numeroInstancias++;
                                totalInstancias++;

                                this.calcularConfusion(esperada, obtenida);
                            } else if (cadena.startsWith("@relation") == true) {
                                st = new StringTokenizer(cadena);
                                st.nextToken(); //@relation
                                relation = st.nextToken();
                            }

                            try {
                                cadena = br.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        //Partial percentage partition
                        porcentajeParcial = (double) ((double) aciertos / (double) numeroInstancias);
                        porcentajeParcial = CreateInform.round(porcentajeParcial, 3);
                        porcentajesParticiones = porcentajesParticiones + nPartitions + "\t" + Double.toString(porcentajeParcial) + "\n";
                        totalAciertos = totalAciertos + aciertos;
                        break;

                    case REGRESSION:
                        numeroInstancias = 0;
                        String cadenaAux = "";
                        double ecmParcial = 0.0;
                        double valorEsperado = 0.0;
                        double valorObtenido = 0.0;

                        try {
                            cadenaAux = br.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        while (cadenaAux != null) {
                            if (cadenaAux.startsWith("@") == false) {
                                st = new StringTokenizer(cadenaAux);
                                //row 1 ->obtained
                                obtenida = st.nextToken();
                                //row 2-> expected or real
                                esperada = st.nextToken();
                                valorEsperado = Double.valueOf(esperada);
                                valorObtenido = Double.valueOf(obtenida);
                                double aux = (double) Math.abs((double) valorEsperado - (double) valorObtenido);
                                double parcial = (double) Math.pow(aux, 2);
                                ecmParcial = ecmParcial + parcial;
                                ecmTotal = ecmTotal + parcial;
                                numeroInstancias++;
                                totalInstancias++;
                            } else if (cadenaAux.startsWith("@relation") == true) {
                                st = new StringTokenizer(cadenaAux);
                                st.nextToken(); //@relation
                                relation = st.nextToken();
                            }
                            try {
                                cadenaAux = br.readLine();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        double ecmParticion = 0.0;
                        ecmParticion = (double) ((double) ecmParcial / (double) numeroInstancias);
                        ecmParticion = CreateInform.round(ecmParticion, 3);
                        ECMPartitionList.add(Double.valueOf(ecmParticion));

                        /*if(i == readingFiles.length-1)  //last partition
                        porcentajesCuadraticosParticiones = porcentajesCuadraticosParticiones +
                        nPartitions + "\t" + Double.toString(ecmParticion);
                        else
                        porcentajesCuadraticosParticiones = porcentajesCuadraticosParticiones +
                        nPartitions + "\t" + Double.toString(ecmParticion) + "\n";
                         */
                        porcentajesCuadraticosParticiones = porcentajesCuadraticosParticiones +
                                nPartitions + "\t" + Double.toString(ecmParticion) + "\n";
                        break;
                }

                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }//for partitions

            // All partitions have finished
            switch (experimentType) {
                case CLASSIFICATION:
                    porcentajeTotal = (double) ((double) totalAciertos / (double) totalInstancias);
                    porcentajeTotal = CreateInform.round(porcentajeTotal, 3);
                    try {
                        if (set.equals("training") == true) {
                            bw.write("Relation: " + relation);
                            bw.newLine();
                        } else {
                            bw.newLine();
                        }

                        bw.newLine();
                        bw.write("Set:" + set);
                        bw.newLine();

                        bw.write("Total percentage of successes:");
                        bw.newLine();
                        bw.write(Double.toString(porcentajeTotal));
                        bw.newLine();

                        bw.write("Percentage of successes in each partition:");
                        bw.newLine();
                        bw.write(porcentajesParticiones);
                        //bw.newLine(); "\n" In porcentajesParticiones

                        bw.write("Confusion matrix (rows=real class;columns=obtained class):");
                        //bw.newLine();

                        for (int i = 0; i < confussionMatrix.length; i++) {
                            bw.newLine();
                            String filaConfusion = "";
                            for (int j = 0; j < confussionMatrix[0].length; j++) {
                                filaConfusion = filaConfusion + Integer.toString(confussionMatrix[i][j]) + "\t";
                            }
                            bw.write(filaConfusion);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    break;

                case REGRESSION:

                    ecmBest = (double) ECMPartitionList.get(0);
                    for (int i = 0; i < ECMPartitionList.size(); i++) {
                        if ((double) ECMPartitionList.get(i) < ecmBest) {
                            ecmBest = (double) ECMPartitionList.get(i);
                        }
                        ecmMean = (double) (ecmMean + (double) ECMPartitionList.get(i));
                    }
                    ecmMean = (double) (ecmMean / nPartitions);

                    for (int i = 0; i < ECMPartitionList.size(); i++) {
                        ecmDev += Math.pow(((double) ECMPartitionList.get(i) - (double) ecmMean), 2);
                    }
                    ecmDev /= nPartitions;
                    ecmDev = Math.sqrt(ecmDev);

                    ecmBest = CreateInform.round(ecmBest, 3);
                    ecmMean = CreateInform.round(ecmMean, 3);
                    ecmDev = CreateInform.round(ecmDev, 3);

                    try {
                        if (set.equals("training") == true) {
                            bw.write("Relation: " + relation);
                            bw.newLine();
                        } else {
                            bw.newLine();
                        }

                        bw.newLine();
                        bw.write("Set:" + set);
                        bw.newLine();

                        bw.write("Partial Mean Squared Error in each partition:");
                        bw.newLine();
                        bw.write(porcentajesCuadraticosParticiones);
                        //bw.newLine(); "\n" In porcentajesCuadraticosParticiones
                        bw.newLine();

                        bw.write("Best\tMean\tStandar Deviation:");
                        bw.newLine();
                        bw.write(Double.toString(ecmBest) + "\t" + Double.toString(ecmMean) + "\t" +
                                Double.toString(ecmDev));
                        bw.newLine();

                        if (set.equals("test") == true) {
                            bw.newLine();
                            bw.write("------ Experiments Expresions ------\n");
                            bw.newLine();
                            bw.write("Partial MSE = 1/N*(Sum[(Di-Yi)^2]), where\n" +
                                    "\"Di\" is desired result in pattern \"i\",\n" +
                                    "\"Yi\" is obtained result in pattern \"i\",\n" +
                                    "and \"N\" is number of patterns\n");
                            bw.newLine();
                            bw.write("Global MSE = sum(MSEi)/n), where\n" +
                                    "\"MSEi\" is partial MSE for partition \"i\",\n" +
                                    "and \"n\" is number of partitions\n");
                            bw.newLine();
                            bw.write("Standar Deviation = SQRT(1/n*(Sum[(GMSE-PMSEi)^2])), where\n" +
                                    "\"GMSE\" is Global MSE,\n" +
                                    "\"PMSEi\" is Partial MSE in partition \"i\",\n" +
                                    "and \"n\" is number of partitions\n");
                            bw.newLine();
                            bw.write("------ Experiments Expresions ------");
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
            }//switch
        }//for training and test
    //All partitions have finished

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//execute

    /**
     * This method computes the classes for classification problem
     * This classes are used for to create confusion matrix

     */
    private void calcularClases() {
        String cadena = "";
        int indice = 0;
        StringTokenizer st = null;

        try {
            fr = new FileReader(readingFiles[0]);
            br = new BufferedReader(fr);
            cadena = br.readLine();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (cadena != null) {
            if (cadena.startsWith("@") == true) {
                if ((indice = cadena.indexOf("{")) != -1) {
                    cadena = cadena.substring(indice + 1, cadena.length() - 1);
                    st = new StringTokenizer(cadena, ",");
                    classes = new String[st.countTokens()];
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        classes[i] = st.nextToken();
                        i++;
                    }
                    break;
                }
            }
            try {
                cadena = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        confussionMatrix = new int[classes.length][classes.length];
        for (int i = 0; i < classes.length; i++) {
            for (int j = 0; j < classes.length; j++) {
                confussionMatrix[i][j] = 0;
            }
        }

        //delete spaces, tab
        for (int i = 0; i < classes.length; i++) {
            StringTokenizer stAux = new StringTokenizer(classes[i]);
            classes[i] = stAux.nextToken();
        }
    }

    /**
     * This method completes the confusion matrix
     * @param esperada_
     * @param obtenida_
     */
    private void calcularConfusion(String esperada_, String obtenida_) {
        int posi = 0;
        int posj = 0;

        for (int i = 0; i < classes.length; i++) {
            if (classes[i].equals(esperada_) == true) {
                posi = i;
            }
            if (classes[i].equals(obtenida_) == true) {
                posj = i;
            }
        }
        confussionMatrix[posi][posj]++;
    }

    /**
     * Round a double whith a decimal precision
     *
     * @param	num	double value
     * @param	ndecimal number of digits of precision
     *
     */
    public static double round(double num, int ndecimal) {
        double aux0 = Math.pow(10, ndecimal);
        double aux = num * aux0;
        int tmp = (int) aux;

        return (double) (tmp / aux0);
    }

}//CreateInform