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
 * File: DatasetXML.java
 *
 * This Class manages XML repressentations of data sets
 *
 * @author Written by Admin 4/8/2009
 * @author Modified Joaquin Derrac 20-5-2010
 * @author Modified Amelia Zafra 28-6-2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import java.util.*;

import org.jdom.Element;

public class DatasetXML implements Comparable {

    public String nameAbr;
    public String nameComplete;
    public String problemType;
    public Vector partitions;

    // Variables for checking the correctness of data set and method
    public boolean m_bContinuous;
    public boolean m_bInteger;
    public boolean m_bNominal;
    public boolean m_bMissing;
    public boolean m_bImprecise;
    public boolean m_bMultiClass;
    public boolean m_bMultiOutput;
    public boolean m_bMIL;
    public double missing;
    public Vector properties;
    public int nAttributes;
    public int nClasses;
    public int nInstances;
    public Vector<String> classes;
    public boolean fuzzy;
    public int files;
    public boolean exh_test;
    public String field;
    public boolean user;

    /**
     * Builder
     * @param dataset Node containing the datasets
     */
    public DatasetXML(Element dataset) {

        Element temporal;
        int i;
        String value;


        nameAbr = dataset.getChildText("nameAbr");
        //System.out.println (" \n > Reading dataset: "+nameAbr );
        nameComplete = dataset.getChildText("nameComplete");
        problemType = dataset.getChildText("problemType");
        partitions = new Vector();
        temporal = dataset.getChild("partitions");

        for (i = 0; i < temporal.getChildren().size(); i++) {
            partitions.addElement(new String(((Element) (temporal.getChildren().get(i))).getText()));
        }

        value = dataset.getChildText("continuous");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bContinuous = true;
        } else {
            m_bContinuous = false;
        }
        //System.out.println ("    > Continous: "+m_bContinuous );

        value = dataset.getChildText("integer");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bInteger = true;
        } else {
            m_bInteger = false;
        }
        //System.out.println ("    > Integer: "+m_bInteger );

        value = dataset.getChildText("nominal");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bNominal = true;
        } else {
            m_bNominal = false;
        }
        //System.out.println ("    > Nominal: "+m_bNominal );

        value = dataset.getChildText("imprecise");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bImprecise = true;
        } else {
            m_bImprecise = false;
        }
        //System.out.println ("    > Imprecise: "+m_bImprecise );

        value = dataset.getChildText("missing");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMissing = true;
        } else {
            m_bMissing = false;
        }

        value = dataset.getChildText("multiclass");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMultiClass = true;
        } else {
            m_bMultiClass = false;
        }
        //System.out.println ("    > MutliClass: "+m_bMultiClass );

        value = dataset.getChildText("multioutput");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMultiOutput = true;
        } else {
            m_bMultiOutput = false;
        }
        //System.out.println ("    > MutliOutput: "+m_bMultiOutput );

        value = dataset.getChildText("multiinstance");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            m_bMIL = true;
        } else {
            m_bMIL = false;
        }
        if (dataset.getChild("percMissinValues") != null) {
            missing = Double.parseDouble(dataset.getChildText("percMissingValues"));
        } else {
            missing = 0;
        }

        if (dataset.getChild("nAttributes") != null) {
            nAttributes = Integer.parseInt(dataset.getChildText("nAttributes"));
        }

        if (dataset.getChild("nInstances") != null) {
            nInstances = Integer.parseInt(dataset.getChildText("nInstances"));
        }

        if (problemType.compareTo("Classification") == 0) {
            if (dataset.getChild("nClasses") != null) {
                nClasses = Integer.parseInt(dataset.getChildText("nClasses"));
            }
        }

        classes = new Vector<String>();
        int con = 0;
        value = dataset.getChildText("classes" + con);
        while (value != null) {
            classes.addElement(value);
            con++;
            value = dataset.getChildText("classes" + con);
        }

        value = dataset.getChildText("fuzzy");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            fuzzy = true;
        } else {
            fuzzy = false;
        }

        value = dataset.getChildText("exh_test");
        if (value != null && value.equalsIgnoreCase("Yes")) {
            exh_test = true;
        } else {
            exh_test = false;
        }

        value = dataset.getChildText("files");
        if (value != null) {
            files = Integer.parseInt(value);
        } else {
            files = -1;
        }

        field = dataset.getChildText("field");
        if (dataset.getChild("userDataset") == null) {
            user = false;
        } else {
            user = true;
        }

    }

    /**
     * Implements the lexicographic order
     * @param o Object to compare
     * @return The lexicographic order 
     */
    public int compareTo(Object o) {
        DatasetXML data = (DatasetXML) o;

        return this.nameAbr.compareTo(data.nameAbr);
    }
}
