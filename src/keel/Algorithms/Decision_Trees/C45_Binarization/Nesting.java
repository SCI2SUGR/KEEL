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

package keel.Algorithms.Decision_Trees.C45_Binarization;

import java.util.ArrayList;

/**
 * <p>Title: Nesting</p>
 * <p>Description: This class implements the NESTING OVO scheme
 * <p>Company: KEEL </p>
 * @author Mikel Galar (University of Navarra) 21/10/2010
 * @version 1.0
 * @since JDK1.6
 */
class Nesting {

    Multiclassifier classifier; // classifier from which the new OVO is nested
    int nClasses;
    OVO ovo;
    int empates;    // number of ties
    int[] empate;   // it contains wether the instance in the ith position obtained a tie when classifying or not
    ArrayList<Integer> ties;  
    boolean creating;
    Multiclassifier method; // the nested OVO

    /** 
     * The constructor of a nested OVO
     * @param classifier the root classifier
     * @param ovo the OVO instance of the root classifier
     */
    public Nesting(Multiclassifier classifier, OVO ovo)
    {
        this.classifier = classifier;
        nClasses = classifier.nClasses;
        this.ovo = ovo;
        empates = 0;
        empate = new int[classifier.train.getnData()];
        ties = new ArrayList<Integer>();
        creating = true;
    }

    /**
     * It creates a new nested OVO
     */
    public void newOvo()
    {
        // We have to create a new OVO classifier with the ties
        method = new Multiclassifier(true, classifier);
        method.execute_nesting(empate);
        creating = false;
    }
}
