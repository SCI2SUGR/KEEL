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
 * @author Administrator
 * @author Modified by Pedro Antonio GutiÃ©rrez and Juan Carlos FernÃ¡ndez (University of CÃ³rdoba) 23/10/2008
 * @version 1.0
 * @since JDK1.5
 * </p>
 */
package keel.GraphInterKeel.datacf.partitionData;

import org.core.Randomize;
import keel.GraphInterKeel.datacf.util.Dataset;
import org.core.Files;
import java.io.File;
import java.util.Vector;

public class PartitionGenerator {

    /**
     * <p>
     * Class for generating partitions
     * </p>
     */
    /** Constant representing KFold partition */
    public static int _K_FOLD = 1;
    /** Constant representing Holdout partition */
    public static int _HOLDOUT = 2;
    /** Constant representing 5x2 of Dietterich partition */
    public static int _5x2 = 3;

    /**
     * <p>
     * Partitions a dataset
     * </p>
     * @param partitionType Type of partition (see constants)
     * @param originalName Initial name of the file
     * @param newName New name of the files generated with the partition
     * @param seed Seed for random numbers
     * @param nOfPartitions Number of partitions
     * @param totalFractions Number of fractions
     */
    public void partition(int partitionType, String originalName, String newName, long seed, int nOfPartitions, int totalFractions) {

        int tmp;
        // Root directory
        File f = new File(newName);
        if (!f.exists()) {
            f.mkdir();
        }

//        String nombre_base;
//        if (System.getProperty("file.separator").compareTo("\\") == 0) {
//            nombre_base = "\\" +
//                    nombreNuevo.substring(nombreNuevo.lastIndexOf("\\"));
//        } else {
//            nombre_base = "/" +
//                    nombreNuevo.substring(nombreNuevo.lastIndexOf("/"));
//        }

        Dataset data = new Dataset(originalName);

        String nombre_base = System.getProperty("file.separator") + new File(originalName).getName().replaceAll(".dat", "");

        Vector salidas = new Vector();
        int caja[];
        Randomize.setSeed(seed);

        // ---------------------
        // K-fold partition type
        if (partitionType == 1) {

            Vector baraje[] = new Vector[nOfPartitions];
            int salPos = 0;
            int randPos;
            int i, j, k;
            boolean ok;
            String cadena, aux;

            for (i = 0; i < data.getNVariables(); i++) {
                if (data.getOutputs().contains(new String(data.getAttributeIndex(i)))) {
                    salPos = i;
                }
            }

            if (!data.getAttributeTypeIndex(salPos).equalsIgnoreCase("real")) { //clasificaciï¿½n
                if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("integer")) { //Integer class
                    for (i = data.getRangesInt(salPos, 0).intValue(); i <= data.getRangesInt(salPos, 1).intValue(); i++) {
                        salidas.addElement(String.valueOf(i));
                    }
                } else if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("nominal")) {
                    salidas = data.getRange(salPos);
                }
                caja = new int[salidas.size()];
                for (i = 0; i < nOfPartitions; i++) {
                    baraje[i] = new Vector();
                }
                for (i = 0; i < data.getNData(); i++) {
                    caja[salidas.indexOf((String) ((Vector) data.getDataVector().elementAt(i)).elementAt(salPos))]++;
                }
                int repartidor[] = new int[data.getNData()];
                k = 0;
                for (i = 0; i < salidas.size(); i++) {
                    for (j = 0; j < data.getNData(); j++) {
                        if (((String) ((Vector) data.getDataVector().elementAt(j)).elementAt(salPos)).equalsIgnoreCase((String) salidas.elementAt(i))) {
                            repartidor[k] = j;
                            k++;
                        }
                    }
                }

                k = 0;
                for (i = 0; i < caja.length; i++) {
                    for (j = 0; j < caja[i]; j++) {
                        randPos = Randomize.Randint(j, caja[i] - 1);
                        tmp = repartidor[j + k];
                        repartidor[j + k] = repartidor[randPos + k];
                        repartidor[randPos + k] = tmp;
                    }
                    k += caja[i];
                }

                for (i = 0; i < data.getNData(); i++) { //para cada clase
                    baraje[i % nOfPartitions].addElement(new Integer(repartidor[i]));
                }
            } else { //regresiï¿½n
                baraje = new Vector[nOfPartitions];
                int repartidor[] = new int[data.getNData()];
                for (i = 0; i < data.getNData(); i++) {
                    repartidor[i] = i;
                }
                for (i = 0; i < nOfPartitions; i++) {
                    baraje[i] = new Vector();
                }
                for (i = 0; i < data.getNData(); i++) {
                    randPos = Randomize.Randint(i, data.getNData() - 1);
                    tmp = repartidor[i];
                    repartidor[i] = repartidor[randPos];
                    repartidor[randPos] = tmp;
                }

                for (i = 0; i < data.getNData(); i++) { //para cada clase
                    baraje[i % nOfPartitions].addElement(new Integer(repartidor[i]));
                }
            }

            for (i = 0; i < nOfPartitions; i++) {
                printHeader(data, newName + nombre_base + "-" +
                        String.valueOf(nOfPartitions) + "-" + String.valueOf(i + 1) +
                        "tra.dat");
                for (j = 0; j < data.getNData(); j++) {
                    if (!baraje[i].contains(new Integer(j))) {
                        cadena = "";
                        ok = false;
                        for (k = 0; k < data.getNVariables(); k++) {
                            if (!ok) {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += "<null>";
                                } else {
                                    cadena += aux;
                                }
                                ok = true;
                            } else {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += ", <null>";
                                } else {
                                    cadena += ", " + aux;
                                }
                            }
                        }
                        cadena += "\n";
                        Files.addToFile(newName + nombre_base +
                                "-" + String.valueOf(nOfPartitions) + "-" +
                                String.valueOf(i + 1) + "tra.dat", cadena);
                    }
                }

                printHeader(data, newName + nombre_base + "-" +
                        String.valueOf(nOfPartitions) + "-" + String.valueOf(i + 1) +
                        "tst.dat");
                for (j = 0; j < data.getNData(); j++) {
                    if (baraje[i].contains(new Integer(j))) {
                        cadena = "";
                        ok = false;
                        for (k = 0; k < data.getNVariables(); k++) {
                            if (!ok) {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += "<null>";
                                } else {
                                    cadena += aux;
                                }
                                ok = true;
                            } else {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += ", <null>";
                                } else {
                                    cadena += ", " + aux;
                                }
                            }
                        }
                        cadena += "\n";
                        Files.addToFile(newName + nombre_base +
                                "-" + String.valueOf(nOfPartitions) + "-" +
                                String.valueOf(i + 1) + "tst.dat", cadena);
                    }
                }
            }
        } // ---------------------------------
        // ----------------------
        // Holdout partition type
        else if (partitionType == 2) { // Holdout
//        int nPart = nOfPartitions;
//        nCross = porcentajes.getSelectedIndex() + 2;
            Vector baraje[] = new Vector[totalFractions];
            int elemAct = 0;
            int salPos = 0;
            int randPos;
            int i, j, k, l, it;
            boolean hecho, ok;
            String cadena, aux;
            boolean reg = false;

            for (i = 0; i < data.getNVariables(); i++) {
                if (data.getOutputs().contains(new String(data.getAttributeIndex(i)))) {
                    salPos = i;
                }
            }

            if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("integer")) { //integer class
                for (i = data.getRangesInt(salPos, 0).intValue();
                        i <= data.getRangesInt(salPos, 1).intValue(); i++) {
                    salidas.addElement(String.valueOf(i));
                }
            } else if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("nominal")) {
                salidas = data.getRange(salPos);
            } else { //Regression problem

                reg = true;

                for (i = 0; i < data.getNData(); i++) {
                    salidas.addElement((String) ((Vector) data.getDataVector().elementAt(i)).elementAt(salPos));
                }

            }

            for (it = 0; it < nOfPartitions; it++) {
                elemAct = 0;
                caja = new int[salidas.size()];
                for (i = 0; i < totalFractions; i++) {
                    baraje[i] = new Vector();
                }

                //change if regression
                if (reg) {

                    for (i = 0; i < data.getNData(); i++) {
                        randPos = Randomize.Randint(0, totalFractions - 1);
                        baraje[randPos].addElement(new Integer(i));
                    }

                    for (j = 0; j < totalFractions; j++) {
                        while (baraje[j].size() > (int) Math.ceil((double) data.getNData() / (double) totalFractions)) {
                            Integer num = (Integer) baraje[j].lastElement();
                            baraje[j].remove(num);
                            baraje[(j + 1) % totalFractions].addElement(num);
                        }
                    }

                } else {
                    //caja stores the number of intances per class
                    for (i = 0; i < data.getNData(); i++) {
                        caja[salidas.indexOf((String) ((Vector) data.getDataVector().elementAt(i)).elementAt(salPos))]++;
                    }

                    for (i = 0; i < salidas.size(); i++) { //for each class
                        for (j = 0; j < data.getNData(); j++) {
                            if (((String) ((Vector) data.getDataVector().elementAt(j)).elementAt(salPos)).equalsIgnoreCase((String) salidas.elementAt(i))) {
                                randPos = Randomize.Randint(0, totalFractions - 1);
                                baraje[randPos].addElement(new Integer(j));
                            }
                        }

                        // move remaining samples
                        elemAct += caja[i];
                        for (j = 0; j < totalFractions; j++) {
                            while (baraje[j].size() >
                                    (int) Math.ceil((double) elemAct / (double) totalFractions)) {
                                Integer num = (Integer) baraje[j].lastElement();
                                baraje[j].remove(num);
                                baraje[(j + 1) % totalFractions].addElement(num);
                            }
                        }
                        for (j = 0; j < totalFractions; j++) {
                            while (baraje[j].size() >
                                    (int) Math.ceil((double) elemAct / (double) totalFractions)) {
                                Integer num = (Integer) baraje[j].lastElement();
                                baraje[j].remove(num);
                                baraje[(j + 1) % totalFractions].addElement(num);
                            }
                        }
                    }
                }

                // validate test size with remaining samples
                for (j = 0; j < totalFractions; j++) {
                    if (baraje[j].size() < (elemAct / totalFractions)) {
                        hecho = false;
                        for (l = (j + 1) % totalFractions; l != j && !hecho; l = (l + 1) % totalFractions) {
                            if (baraje[l].size() > (elemAct / totalFractions)) {
                                Integer num = (Integer) baraje[l].lastElement();
                                baraje[l].remove(num);
                                baraje[j].addElement(num);
                                hecho = true;
                            }
                        }
                    }
                }

                printHeader(data, newName + nombre_base + "-" +
                        String.valueOf(totalFractions) + "-" + String.valueOf(it + 1) +
                        "tra.dat");
                for (j = 0; j < data.getNData(); j++) {
                    if (!baraje[0].contains(new Integer(j))) {
                        cadena = "";
                        ok = false;
                        for (k = 0; k < data.getNVariables(); k++) {
                            if (!ok) {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += "<null>";
                                } else {
                                    cadena += aux;
                                }
                                ok = true;
                            } else {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += ", <null>";
                                } else {
                                    cadena += ", " + aux;
                                }
                            }
                        }
                        cadena += "\n";
                        Files.addToFile(newName + nombre_base +
                                "-" + String.valueOf(totalFractions) + "-" +
                                String.valueOf(it + 1) + "tra.dat",
                                cadena);
                    }
                }

                printHeader(data, newName + nombre_base + "-" +
                        String.valueOf(totalFractions) + "-" + String.valueOf(it + 1) +
                        "tst.dat");
                for (j = 0; j < data.getNData(); j++) {
                    if (baraje[0].contains(new Integer(j))) {
                        cadena = "";
                        ok = false;
                        for (k = 0; k < data.getNVariables(); k++) {
                            if (!ok) {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += "<null>";
                                } else {
                                    cadena += aux;
                                }
                                ok = true;
                            } else {
                                aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                if (aux == null) {
                                    cadena += ", <null>";
                                } else {
                                    cadena += ", " + aux;
                                }
                            }
                        }
                        cadena += "\n";
                        Files.addToFile(newName + nombre_base +
                                "-" + String.valueOf(totalFractions) + "-" +
                                String.valueOf(it + 1) + "tst.dat",
                                cadena);
                    }
                }
            }
        } // -----------------------------
        // -----------------------------
        // 5x2 Dietterich partition type
        else if (partitionType == 3) { // 5x2 Dietterich
            int nPart = 5;
            int nCross = 2;
            Vector baraje[] = new Vector[nCross];
            int elemAct = 0;
            int salPos = 0;
            int randPos;
            int i, j, k, l, it;
            boolean hecho, ok;
            String cadena, aux;

            if (!data.getAttributeTypeIndex(salPos).equalsIgnoreCase("real")) { //clasificaciï¿½n
                for (i = 0; i < data.getNVariables(); i++) {
                    if (data.getOutputs().contains(new String(data.getAttributeIndex(i)))) {
                        salPos = i;
                    }
                }
                if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("integer")) { // integer class
                    for (i = data.getRangesInt(salPos, 0).intValue();
                            i <= data.getRangesInt(salPos, 1).intValue(); i++) {
                        salidas.addElement(String.valueOf(i));
                    }

                    for (it = 0; it < nPart; it++) {
                        caja = new int[salidas.size()];
                        for (i = 0; i < nCross; i++) {
                            baraje[i] = new Vector();
                        }
                        for (i = 0; i < data.getNData(); i++) {
                            caja[salidas.indexOf((String) ((Vector) data.getDataVector().elementAt(
                                    i)).elementAt(salPos))]++;
                        }

                        for (i = 0; i < salidas.size(); i++) { // for each class
                            for (j = 0; j < data.getNData(); j++) {
                                if (((String) ((Vector) data.getDataVector().elementAt(j)).elementAt(salPos)).equalsIgnoreCase((String) salidas.elementAt(i))) {
                                    randPos = Randomize.Randint(0, nCross - 1);
                                    baraje[randPos].addElement(new Integer(j));
                                }
                            }

                            //move remaining samples
                            elemAct += caja[i];
                            for (j = 0; j < nCross; j++) {
                                while (baraje[j].size() >
                                        (int) Math.ceil((double) elemAct / (double) nCross)) {
                                    Integer num = (Integer) baraje[j].lastElement();
                                    baraje[j].remove(num);
                                    baraje[(j + 1) % nCross].addElement(num);
                                }
                            }
                            for (j = 0; j < nCross; j++) {
                                while (baraje[j].size() >
                                        (int) Math.ceil((double) elemAct / (double) nCross)) {
                                    Integer num = (Integer) baraje[j].lastElement();
                                    baraje[j].remove(num);
                                    baraje[(j + 1) % nCross].addElement(num);
                                }
                            }
                        }

                        //validate test size with remaining samples
                        for (j = 0; j < nCross; j++) {
                            if (baraje[j].size() < (elemAct / nCross)) {
                                hecho = false;
                                for (l = (j + 1) % nCross; l != j && !hecho; l = (l + 1) % nCross) {
                                    if (baraje[l].size() > (elemAct / nCross)) {
                                        Integer num = (Integer) baraje[l].lastElement();
                                        baraje[l].remove(num);
                                        baraje[j].addElement(num);
                                        hecho = true;
                                    }
                                }
                            }
                        }

                        printHeader(data, newName + nombre_base + "-5x2-" +
                                String.valueOf(it + 1) + "tra.dat");
                        for (j = 0; j < data.getNData(); j++) {
                            if (!baraje[0].contains(new Integer(j))) {
                                cadena = "";
                                ok = false;
                                for (k = 0; k < data.getNVariables(); k++) {
                                    if (!ok) {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += "<null>";
                                        } else {
                                            cadena += aux;
                                        }
                                        ok = true;
                                    } else {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += ", <null>";
                                        } else {
                                            cadena += ", " + aux;
                                        }
                                    }
                                }
                                cadena += "\n";
                                Files.addToFile(newName + nombre_base +
                                        "-5x2-" + String.valueOf(it + 1) +
                                        "tra.dat", cadena);
                            }
                        }

                        printHeader(data, newName + nombre_base + "-5x2-" +
                                String.valueOf(it + 1) + "tst.dat");
                        for (j = 0; j < data.getNData(); j++) {
                            if (baraje[0].contains(new Integer(j))) {
                                cadena = "";
                                ok = false;
                                for (k = 0; k < data.getNVariables(); k++) {
                                    if (!ok) {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += "<null>";
                                        } else {
                                            cadena += aux;
                                        }
                                        ok = true;
                                    } else {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += ", <null>";
                                        } else {
                                            cadena += ", " + aux;
                                        }
                                    }
                                }
                                cadena += "\n";
                                Files.addToFile(newName + nombre_base +
                                        "-5x2-" + String.valueOf(it + 1) +
                                        "tst.dat", cadena);
                            }
                        }
                    }

                } else if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("nominal")) {
                    salidas = data.getRange(salPos);

                    for (it = 0; it < nPart; it++) {
                        caja = new int[salidas.size()];
                        for (i = 0; i < nCross; i++) {
                            baraje[i] = new Vector();
                        }
                        for (i = 0; i < data.getNData(); i++) {
                            caja[salidas.indexOf((String) ((Vector) data.getDataVector().elementAt(
                                    i)).elementAt(salPos))]++;
                        }

                        for (i = 0; i < salidas.size(); i++) { // for each class
                            for (j = 0; j < data.getNData(); j++) {
                                if (((String) ((Vector) data.getDataVector().elementAt(j)).elementAt(salPos)).equalsIgnoreCase((String) salidas.elementAt(i))) {
                                    randPos = Randomize.Randint(0, nCross - 1);
                                    baraje[randPos].addElement(new Integer(j));
                                }
                            }

                            //move remaining samples
                            elemAct += caja[i];
                            for (j = 0; j < nCross; j++) {
                                while (baraje[j].size() >
                                        (int) Math.ceil((double) elemAct / (double) nCross)) {
                                    Integer num = (Integer) baraje[j].lastElement();
                                    baraje[j].remove(num);
                                    baraje[(j + 1) % nCross].addElement(num);
                                }
                            }
                            for (j = 0; j < nCross; j++) {
                                while (baraje[j].size() >
                                        (int) Math.ceil((double) elemAct / (double) nCross)) {
                                    Integer num = (Integer) baraje[j].lastElement();
                                    baraje[j].remove(num);
                                    baraje[(j + 1) % nCross].addElement(num);
                                }
                            }
                        }

                        //validate test size with remaining samples
                        for (j = 0; j < nCross; j++) {
                            if (baraje[j].size() < (elemAct / nCross)) {
                                hecho = false;
                                for (l = (j + 1) % nCross; l != j && !hecho; l = (l + 1) % nCross) {
                                    if (baraje[l].size() > (elemAct / nCross)) {
                                        Integer num = (Integer) baraje[l].lastElement();
                                        baraje[l].remove(num);
                                        baraje[j].addElement(num);
                                        hecho = true;
                                    }
                                }
                            }
                        }

                        printHeader(data, newName + nombre_base + "-5x2-" +
                                String.valueOf(it + 1) + "tra.dat");
                        for (j = 0; j < data.getNData(); j++) {
                            if (!baraje[0].contains(new Integer(j))) {
                                cadena = "";
                                ok = false;
                                for (k = 0; k < data.getNVariables(); k++) {
                                    if (!ok) {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += "<null>";
                                        } else {
                                            cadena += aux;
                                        }
                                        ok = true;
                                    } else {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += ", <null>";
                                        } else {
                                            cadena += ", " + aux;
                                        }
                                    }
                                }
                                cadena += "\n";
                                Files.addToFile(newName + nombre_base +
                                        "-5x2-" + String.valueOf(it + 1) +
                                        "tra.dat", cadena);
                            }
                        }

                        printHeader(data, newName + nombre_base + "-5x2-" +
                                String.valueOf(it + 1) + "tst.dat");
                        for (j = 0; j < data.getNData(); j++) {
                            if (baraje[0].contains(new Integer(j))) {
                                cadena = "";
                                ok = false;
                                for (k = 0; k < data.getNVariables(); k++) {
                                    if (!ok) {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += "<null>";
                                        } else {
                                            cadena += aux;
                                        }
                                        ok = true;
                                    } else {
                                        aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                        if (aux == null) {
                                            cadena += ", <null>";
                                        } else {
                                            cadena += ", " + aux;
                                        }
                                    }
                                }
                                cadena += "\n";
                                Files.addToFile(newName + nombre_base +
                                        "-5x2-" + String.valueOf(it + 1) +
                                        "tst.dat", cadena);
                            }
                        }
                    }
                }
            } else { //regresiÃ³n
                baraje = new Vector[2];
                baraje[0] = new Vector();
                baraje[1] = new Vector();
                int repartidor[] = new int[data.getNData()];
                for (i = 0; i < data.getNData(); i++) {
                    repartidor[i] = i;
                }
                for (it = 0; it < nPart; it++) {
                    for (i = 0; i < 2; i++) {
                        baraje[i] = new Vector();
                    }
                    for (i = 0; i < data.getNData(); i++) {
                        randPos = Randomize.Randint(i, data.getNData() - 1);
                        tmp = repartidor[i];
                        repartidor[i] = repartidor[randPos];
                        repartidor[randPos] = tmp;
                    }
                    for (i = 0; i < data.getNData(); i++) {
                        baraje[i % 2].addElement(new Integer(repartidor[i]));
                    }

                    printHeader(data, newName + nombre_base + "-5x2-" +
                            String.valueOf(it + 1) + "tra.dat");
                    for (j = 0; j < data.getNData(); j++) {
                        if (!baraje[0].contains(new Integer(j))) {
                            cadena = "";
                            ok = false;
                            for (k = 0; k < data.getNVariables(); k++) {
                                if (!ok) {
                                    aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                    if (aux == null) {
                                        cadena += "<null>";
                                    } else {
                                        cadena += aux;
                                    }
                                    ok = true;
                                } else {
                                    aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                    if (aux == null) {
                                        cadena += ", <null>";
                                    } else {
                                        cadena += ", " + aux;
                                    }
                                }
                            }
                            cadena += "\n";
                            Files.addToFile(newName + nombre_base +
                                    "-5x2-" + String.valueOf(it + 1) +
                                    "tra.dat", cadena);
                        }
                    }

                    printHeader(data, newName + nombre_base + "-5x2-" +
                            String.valueOf(it + 1) + "tst.dat");
                    for (j = 0; j < data.getNData(); j++) {
                        if (baraje[0].contains(new Integer(j))) {
                            cadena = "";
                            ok = false;
                            for (k = 0; k < data.getNVariables(); k++) {
                                if (!ok) {
                                    aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                    if (aux == null) {
                                        cadena += "<null>";
                                    } else {
                                        cadena += aux;
                                    }
                                    ok = true;
                                } else {
                                    aux = (String) ((Vector) data.getDataVector().elementAt(j)).elementAt(k);
                                    if (aux == null) {
                                        cadena += ", <null>";
                                    } else {
                                        cadena += ", " + aux;
                                    }
                                }
                            }
                            cadena += "\n";
                            Files.addToFile(newName + nombre_base +
                                    "-5x2-" + String.valueOf(it + 1) +
                                    "tst.dat", cadena);
                        }
                    }
                }
            }
        } /*else { //stratification
    try {
    nCross = Integer.parseInt( (String) (valorT.getSelectedItem()));
    }
    catch (Exception ex) {
    JOptionPane.showMessageDialog(this,
    "Insert an integer number into t value, please", "Error", 2);
    return;
    }
    Vector baraje[] = new Vector[nCross];
    int elemAct = 0;
    int salPos = 0;
    int randPos;
    int i, j, k, l;
    boolean hecho, ok;
    String cadena, aux;

    for (i = 0; i < data.getNVariables(); i++) {
    if (data.getOutputs().contains(new String(data.getAttributeIndex(i)))) {
    salPos = i;
    }
    }
    if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("integer")) { // integer class
    for (i = data.getRangesInt(salPos, 0).intValue();
    i <= data.getRangesInt(salPos, 1).intValue(); i++) {
    salidas.addElement(String.valueOf(i));
    }
    }
    else if (data.getAttributeTypeIndex(salPos).equalsIgnoreCase("nominal")) {
    salidas = data.getRange(salPos);
    } else { //Regression problem
    for (i=0; i<data.getNData(); i++) {
    salidas.addElement( (String) ( (Vector) data.getDataVector().elementAt(i)).elementAt(salPos));
    }
    }
    caja = new int[salidas.size()];
    for (i = 0; i < nCross; i++) {
    baraje[i] = new Vector();
    }
    for (i = 0; i < data.getNData(); i++) {
    caja[salidas.indexOf( (String) ( (Vector) data.getDataVector().elementAt(i)).
    elementAt(salPos))]++;
    }

    for (i = 0; i < salidas.size(); i++) { // for each class
    for (j = 0; j < data.getNData(); j++) {
    if ( ( (String) ( (Vector) data.getDataVector().elementAt(j)).elementAt(
    salPos)).equalsIgnoreCase( (String) salidas.elementAt(i))) {
    randPos = Randomize.Randint(0, nCross - 1);
    baraje[randPos].addElement(new Integer(j));
    }
    }

    // move remaining samples
    elemAct += caja[i];
    for (j = 0; j < nCross; j++) {
    while (baraje[j].size() >
    (int) Math.ceil( (double) elemAct / (double) nCross)) {
    Integer num = (Integer) baraje[j].lastElement();
    baraje[j].remove(num);
    baraje[ (j + 1) % nCross].addElement(num);
    }
    }
    for (j = 0; j < nCross; j++) {
    while (baraje[j].size() >
    (int) Math.ceil( (double) elemAct / (double) nCross)) {
    Integer num = (Integer) baraje[j].lastElement();
    baraje[j].remove(num);
    baraje[ (j + 1) % nCross].addElement(num);
    }
    }
    }

    // validate test size with remaining samples
    for (j = 0; j < nCross; j++) {
    if (baraje[j].size() < (elemAct / nCross)) {
    hecho = false;
    for (l = (j + 1) % nCross; l != j && !hecho; l = (l + 1) % nCross) {
    if (baraje[l].size() > (elemAct / nCross)) {
    Integer num = (Integer) baraje[l].lastElement();
    baraje[l].remove(num);
    baraje[j].addElement(num);
    hecho = true;
    }
    }
    }
    }

    for (i = 0; i < nCross; i++) {
    imprimeCabecera(nombre_nuevo.getText() + nombre_base + "-st" +
    String.valueOf(nCross) + "-" + String.valueOf(i + 1) +
    ".dat");
    for (j = 0; j < data.getNData(); j++) {
    if (baraje[i].contains(new Integer(j))) {
    cadena = "";
    ok = false;
    for (k = 0; k < data.getNVariables(); k++) {
    if (!ok) {
    aux = (String) ( (Vector) data.getDataVector().elementAt(j)).
    elementAt(k);
    if (aux == null) {
    cadena += "<null>";
    }
    else {
    cadena += aux;
    }
    ok = true;
    }
    else {
    aux = (String) ( (Vector) data.getDataVector().elementAt(j)).
    elementAt(k);
    if (aux == null) {
    cadena += ", <null>";
    }
    else {
    cadena += ", " + aux;
    }
    }
    }
    cadena += "\n";
    Files.addToFile(nombre_nuevo.getText() + nombre_base +
    "-st" + String.valueOf(nCross) + "-" +
    String.valueOf(i + 1) + ".dat", cadena);
    }
    }
    }
    }
    JOptionPane.showMessageDialog(this,
    "Partition completed successfully",
    "Info", 1);
    }*/
    }

    /**
     * <p>
     * Writes the header of a dataset in given file
     * </p>
     * @param data Dataset to extract the header
     * @param fileName Name of the output file
     */
    private void printHeader(Dataset data, String fileName) {

        String cadena = "";
        int i, j;

        cadena += "@relation " + data.getRelacion() + "\n";

        for (i = 0; i < data.getNVariables(); i++) {
            cadena += "@attribute " + data.getAttributeIndex(i) + " ";
            if (data.getAttributeTypeIndex(i).equalsIgnoreCase("nominal")) { //list
                cadena += "{";
                for (j = 0; j < data.getRange(i).size(); j++) {
                    cadena += (String) data.getRange(i).elementAt(j);
                    if (j < data.getRange(i).size() - 1) {
                        cadena += ", ";
                    }
                }
                cadena += "}\n";
            } else if (data.getAttributeTypeIndex(i).equalsIgnoreCase("integer")) { //int
                cadena += "integer" + " [" + data.getRangesInt(i, 0) + ", " +
                        data.getRangesInt(i, 1) + "]\n";
            } else { //real
                cadena += "real" + " [" + data.getRangesReal(i, 0) + ", " +
                        data.getRangesReal(i, 1) + "]\n";
            }
        }

        cadena += "@inputs ";
        boolean poner = false;
        for (j = 0; j < data.getNInputs(); j++) {
            if (!poner) {
                cadena += (String) (data.getInputs().elementAt(j));
                poner = true;
            } else {
                cadena += ", " + (String) (data.getInputs().elementAt(j));
            }
        }
        cadena += "\n";
        cadena += "@outputs ";
        poner = false;
        for (j = 0; j < data.getNOutputs(); j++) {
            if (!poner) {
                cadena += (String) (data.getOutputs().elementAt(j));
                poner = true;
            } else {
                cadena += ", " + (String) (data.getOutputs().elementAt(j));
            }
        }
        cadena += "\n";
        cadena += "@data\n";
        Files.writeFile(fileName, cadena);
    }
}

