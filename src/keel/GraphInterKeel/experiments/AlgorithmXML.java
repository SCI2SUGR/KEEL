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
 * File: AlgorithmXML.java
 *
 * Management of XML algorithm sheets
 *
 * @author Written by Admin 4/8/2009
 * @author Modified Joaquin Derrac 20-5-2010
 * @author Modified Amelia Zafra 28-6-2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.experiments;

import org.jdom.Element;

public class AlgorithmXML implements Comparable {

    public String name;
    public String family;
    public String jarFile;
    public String problemType;
    public boolean m_bInputContinuous;
    public boolean m_bInputInteger;
    public boolean m_bInputNominal;
    public boolean m_bInputImprecise;
    public boolean m_bInputMissing;
    public boolean m_bInputMultiClass;
    public boolean m_bInputMultiOutput;
    public boolean m_bInputMIL;
    public boolean m_bOutputContinuous;
    public boolean m_bOutputInteger;
    public boolean m_bOutputNominal;
    public boolean m_bOutputImprecise;
    public boolean m_bOutputMissing;
    public boolean m_bOutputMultiClass;
    public boolean m_bOutputMultiOutput;

    /**
     * Builder
     * @param xml File with the description of the algorithm
     */
    public AlgorithmXML(Element xml) {

        name = xml.getChildText("name");

        //System.out.println (" \n> Name: "+name);
        family = xml.getChildText("family");

        //System.out.println ("    > family: "+family);
        problemType = xml.getChildText("problem_type");

        //System.out.println ("    > problemType: "+problemType);
        jarFile = xml.getChildText("jar_file");

        if (xml.getChild("input") != null) {
            getInputVariables(xml.getChild("input"));
        }

        if (xml.getChild("output") != null) {
            getOutputVariables(xml.getChild("output"));
        }

    }

    /**
     * Gets input variables type
     *
     * @param definition Definition element
     */
    private void getInputVariables(Element definition) {
        String value;

        value = definition.getChildText("continuous");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputContinuous = true;
        } else {
            m_bInputContinuous = false;
        }
        //System.out.println ("      > Input Continous: "+m_bInputContinuous );

        value = definition.getChildText("integer");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputInteger = true;
        } else {
            m_bInputInteger = false;
        }
        //System.out.println ("      > Input Integer: "+m_bInputInteger );

        value = definition.getChildText("nominal");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputNominal = true;
        } else {
            m_bInputNominal = false;
        }
        //System.out.println ("      > Input Nominal: "+m_bInputNominal );

        value = definition.getChildText("imprecise");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputImprecise = true;
        } else {
            m_bInputImprecise = false;
        }
        //System.out.println ("      > Input Imprecise: "+m_bInputImprecise );

        value = definition.getChildText("missing");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputMissing = true;
        } else {
            m_bInputMissing = false;
        }
        //System.out.println ("      > Input Missing: "+m_bInputMissing );

        value = definition.getChildText("multiclass");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputMultiClass = true;
        } else {
            m_bInputMultiClass = false;
        }
        //System.out.println ("      > Input MultiClass: "+m_bInputMultiClass );

        value = definition.getChildText("multioutput");
        if (value.equalsIgnoreCase("yes")) {
            m_bInputMultiOutput = true;
        } else {
            m_bInputMultiOutput = false;
        }
        //System.out.println ("      > Input MultiOutput: "+m_bInputMultiOutput );
        value = definition.getChildText("multiinstance");
        if ((value != null) && value.equalsIgnoreCase("yes")) {
            m_bInputMIL = true;
        } else {
            m_bInputMIL = false;
        }
    } // end getInputVariables

    /**
     * Gets output variables type
     *
     * @param definition Definition element
     */
    private void getOutputVariables(Element definition) {
        String value;

        value = definition.getChildText("continuous");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputContinuous = true;
        } else {
            m_bOutputContinuous = false;
        }
        //System.out.println ("      > Output Continuous: "+m_bOutputContinuous );

        value = definition.getChildText("integer");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputInteger = true;
        } else {
            m_bOutputInteger = false;
        }
        //System.out.println ("      > Output Integer: "+m_bOutputInteger );

        value = definition.getChildText("nominal");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputNominal = true;
        } else {
            m_bOutputNominal = false;
        }
        //System.out.println ("      > Output Nominal: "+m_bOutputNominal );

        value = definition.getChildText("imprecise");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputImprecise = true;
        } else {
            m_bOutputImprecise = false;
        }
        //System.out.println ("      > Output Imprecise: "+m_bOutputImprecise );

        value = definition.getChildText("missing");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputMissing = true;
        } else {
            m_bOutputMissing = false;
        }
        //System.out.println ("      > Output Missing: "+m_bOutputMissing );

        value = definition.getChildText("multiclass");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputMultiClass = true;
        } else {
            m_bOutputMultiClass = false;
        }
        //System.out.println ("      > Output MultiClass: "+m_bOutputMultiClass );

        value = definition.getChildText("multioutput");
        if (value.equalsIgnoreCase("yes")) {
            m_bOutputMultiOutput = true;
        } else {
            m_bOutputMultiOutput = false;
        }
    //System.out.println ("      > Output MultiOutput: "+m_bOutputMultiOutput );
    } //end getOutputVariables

    /**
     * Implements the lexicographic order
     *
     * @param o Object to compare
     * @return The lexicographic order
     */
    public int compareTo(Object o) {
        AlgorithmXML alg = (AlgorithmXML) o;

        String familyOwn = this.family;
        String familyext = alg.family;

        int result = familyOwn.toLowerCase().compareTo(familyext.toLowerCase());

        if (result != 0) {
            return result;
        } else {
            return this.name.toLowerCase().compareTo(alg.name.toLowerCase());
        }

    }
}
