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
 * @author Written by Julián Luengo Martín 12/12/2008
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Fuzzy_Rule_Learning.Hybrid.PDFCS;
/**
 * Fuzzy Rule Set built from the PDFC
 * @author Julián Luengo Martín
 *
 */

import keel.Algorithms.SVM.SMO.SMO.BinarySMO;
import keel.Algorithms.SVM.SMO.core.Instance;
import keel.Algorithms.SVM.SMO.core.Instances;
import keel.Algorithms.SVM.SMO.supportVector.Kernel;
import keel.Algorithms.SVM.SMO.supportVector.PDRFKernel;

public class FuzzyRuleSet {
	
	/** Default output value of this rule set */
	protected double m_b0;
	
	/** location parameters of the different membership functions */
	protected double m_z[][];
	
	/** output values of the rules */
	protected double m_b[];
	
	/** The Kernel associated to this Rule set */
	protected Kernel m_kernel;
	
	/** The actual number of rules **/
	protected int m_rules;
	
	/**
	 * Builds a new Fuzzy Rule Set from a Binary SMO
	 * @param smo The reference binary SMO
	 * @param data The data from the binary SMO was built
	 */
	public FuzzyRuleSet(BinarySMO smo,Instances data,Kernel kern){
		double lagrange[];
		int j;
		
		lagrange = smo.getLagrangeMultipliers();
		double temp_z[][] = new double[data.numInstances()][];
		double temp_b[] = new double[data.numInstances()];
		
		j=0;
		for(int i=0;i<lagrange.length;i++){
			if(lagrange[i]>0){
				double temp_values[] = new double[data.numAttributes()-1];
				for(int k=0,l=0;k<data.instance(i).toDoubleArray().length;k++)
					if(k!=data.instance(i).classIndex()){
						temp_values[l] = data.instance(i).toDoubleArray()[k];
						l++;
					}
				temp_z[j] = temp_values;
				temp_b[j] = lagrange[i] * smo.getClasses()[i];
				j++;
			}
		}
		
		//Please NOTE that in Chen and Wang's paper, the b0 parameter
		//is set as the 'b' parameter obtained by the SVM. However, in
		//the SMO optimisation we are using, the decision function uses MINUS b 
		// and in Chen and Wang's paper is PLUS b. So we must change the sign of b
		//to correct this issue
		m_b0 = -smo.getB();
		m_b = new double[j];
		m_z = new double[j][];
		for(int i=0;i<j;i++){
			m_b[i] = temp_b[i];
			m_z[i] = temp_z[i];
		}
		m_rules = j;
		m_kernel = kern;
	}
	
	/**
	 * This method computes the input output mapping of this rule set
	 * @param inst The input instance.
	 * @return The unthresholded output of this rule set for the given instance.
	 */
	public double unthresholdedOutput(Instance inst){
		double nume,denom;
		double u[] = new double[inst.numAttributes()-1];
		PDRFKernel pdrf = (PDRFKernel) m_kernel;
		
		nume = m_b0;
		denom = 1;
		for(int i=0;i<m_b.length;i++){
			
			for(int j=0,l=0;j<inst.numAttributes();j++){
				if(j!=inst.classIndex()){
					u[l] = inst.value(j) - m_z[i][l];
					l++;
				}
			}
			nume += m_b[i] * pdrf.evaluatePRDF(u);
			denom += pdrf.evaluatePRDF(u);
		}
		return (nume/denom);
	}
	
	
	/**
	 * This method returns the number of rules of this Rule Set
	 * @return
	 */
	public int getNumRules(){
		return m_rules;
	}
}

