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

/*
 * KeelToExcel.java
 */
package keel.Algorithms.Preprocess.Converter;

import java.io.File;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;

/**
 * <p>
 * <b> KeelToExcel </b>
 * </p>
 *This class extends from the Exporter class. It is used to read 
 * data with KEEL format and transform them to the EXCEL format.
 *
 * @author Teresa Prieto López (UCO)
 * @version 1.0
 */
public class KeelToExcel extends Exporter {


    /** KeelToExcel class Constructor.
     * Initializes the variable that stores the symbols used to identify null 
     * values.
     *
     * @param  nullValueUser. Null value symbols.
     */
    public KeelToExcel(String nullValueUser) {
        nullValue = nullValueUser;
    }

    /**
     * Method used to transform the data from the KEEL file given as parameter to 
     * Excel format file which will be stored in the second file given. It calls the method
     * Start of its super class Exporter and then call the method Save.
     *
     * @param pathnameInput KEEL file path.
     * @param pathnameOutput Excel file path.
     *
     * @throws Exception if the files can not be read or written.
     */
    public void Start(String pathnameInput, String pathnameOutput) throws Exception {

        super.Start(pathnameInput);

        Save(pathnameOutput);


    }//end Start()

    /**
     * Method that creates the output file with Excel format given as parameter 
     * using all the structures built by the start method of the Exporter class.  
     * @param pathnameOutput Excel file path to generate.
     * @throws Exception if the file can not be written.
     */
    public void Save(String pathnameOutput) throws Exception {
        int i;
        int j;
        int type;
        String filename = new String();
        String element = new String();

        /* Comprobamos si el nombre del fichero tiene la extensiÃ³n .csv, si no la tiene
         * se la ponemos */
        if (pathnameOutput.endsWith(".xls")) {
            filename = pathnameOutput;
        } else {
            filename = pathnameOutput.concat(".xls");
        }
        WritableWorkbook workbook = Workbook.createWorkbook(new File(pathnameOutput));
        WritableSheet sheet = workbook.createSheet("Sheet 1", 0);


        for (i = 0; i < numAttributes; i++) {
            String nameAttribute = (String) attribute[i].getName();
            nameAttribute = nameAttribute.replace("'", "");

            Label label = new Label(i, 0, nameAttribute);
            sheet.addCell(label);

        }



        for (i = 0; i < data[0].size(); i++) {
            for (j = 0; j < numAttributes; j++) {
                type = attribute[j].getType();

                element = (String) data[j].elementAt(i);

                element = element.replace("\"", "");

                if (type == REAL) {
                    if (!element.contains(".")) {
                        element = element.concat(".0");
                    }
                    Number number = new Number(j, i + 1, Double.valueOf(element));
                    sheet.addCell(number);
                } else {
                    Label label = new Label(j, i + 1, element);
                    sheet.addCell(label);
                }
            }
        }

        workbook.write();
        workbook.close();



        File f = new File(filename);

        System.out.println("Fichero " + f.getName() + " creado correctamente");


    }//end Save()
}// end class KeelToExcel

