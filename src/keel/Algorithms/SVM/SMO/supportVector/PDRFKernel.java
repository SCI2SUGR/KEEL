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

package keel.Algorithms.SVM.SMO.supportVector;

/**
 * Positive-Definite Reference Function  Kernel
 * To be used with PDFS algorithm
 * @author Julian Luengo Martin
 * 
 */
import keel.Algorithms.SVM.SMO.core.Instance;
import keel.Algorithms.SVM.SMO.core.Instances;
import keel.Algorithms.SVM.SMO.core.Option;
import keel.Algorithms.SVM.SMO.core.Utils;

import java.util.Enumeration;
import java.util.Vector;
public class PDRFKernel
extends CachedKernel {

	/** Type of Positive Definite Functions supported **/
	public static int SymmetricTriangle = 1;
	public static int Gaussian = 2;
	public static int Cauchy = 3;
	public static int Laplace = 4;
	public static int HyperbolicSecant = 5;
	public static int SquaredSinc = 6;

	/** for serialization */
	static final long serialVersionUID = 7672176272736783265L;

	/** The precalculated dotproducts of &lt;inst_i,inst_i&gt; */
	protected double m_kernelPrecalc[];

	/** The d value for the PDRF kernel. */
	protected double m_d = 0.01;

	/** The type of function used by this kernel **/
	protected int m_type = PDRFKernel.SymmetricTriangle;

	/**
	 * default constructor - does nothing.
	 */
	public PDRFKernel() {
		super();
	}

	/**
	 * Constructor. Initializes m_kernelPrecalc[].
	 * 
	 * @param data	the data to use
	 * @param cacheSize	the size of the cache
	 * @param gamma	the bandwidth
	 * @throws Exception	if something goes wrong
	 */
	public PDRFKernel(Instances data, int cacheSize, double gamma)
	throws Exception {

		super();

		setCacheSize(cacheSize);
		setD(gamma);

		buildKernel(data);
	}

	/**
	 * Returns a string describing the kernel
	 * 
	 * @return a description suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String globalInfo() {
		return 
		"The PRDF kernel. K(x, y) = K(u) = Productory_{k=1}^n a^k(u_k)";
	}

	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return 		an enumeration of all the available options.
	 */
	public Enumeration listOptions() {
		Vector		result;
		Enumeration		en;

		result = new Vector();

		en = super.listOptions();
		while (en.hasMoreElements())
			result.addElement(en.nextElement());

		result.addElement(new Option(
				"\tThe d parameter.\n"
				+ "\t(default: 1)",
				"D", 1, "-D <num>"));

		return result.elements();
	}

	/**
	 * Parses a given list of options. <p/>
	 * 
	   <!-- options-start -->
	 * Valid options are: <p/>
	 * 
	 * <pre> -D
	 *  Enables debugging output (if available) to be printed.
	 *  (default: off)</pre>
	 * 
	 * <pre> -no-checks
	 *  Turns off all checks - use with caution!
	 *  (default: checks on)</pre>
	 * 
	 * <pre> -C &lt;num&gt;
	 *  The size of the cache (a prime number).
	 *  (default: 250007)</pre>
	 * 
	 * <pre> -G &lt;num&gt;
	 *  The Gamma parameter.
	 *  (default: 0.01)</pre>
	 * 
	   <!-- options-end -->
	 * 
	 * @param options 	the list of options as an array of strings
	 * @throws Exception 	if an option is not supported
	 */
	public void setOptions(String[] options) throws Exception {
		String	tmpStr;

		tmpStr = Utils.getOption('D', options);
		if (tmpStr.length() != 0)
			setD(Double.parseDouble(tmpStr));
		else
			setD(0.01);

		super.setOptions(options);
	}

	/**
	 * Gets the current settings of the Kernel.
	 *
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String[] getOptions() {
		int       i;
		Vector    result;
		String[]  options;

		result = new Vector();
		options = super.getOptions();
		for (i = 0; i < options.length; i++)
			result.add(options[i]);

		result.add("-D");
		result.add("" + getD());

		return (String[]) result.toArray(new String[result.size()]);	  
	}

	/**
	 * 
	 * @param id1   	the index of instance 1
	 * @param id2		the index of instance 2
	 * @param inst1	the instance 1 object
	 * @return 		the dot product
	 * @throws Exception 	if something goes wrong
	 */
	protected double evaluate(int id1, int id2, Instance inst1)
	throws Exception {
		double u[];
		if (id1 == id2) {
			//for the squared sinc this is not true! but since
			//the result would be 0/0, let it be 1.
			return 1.0; 
		} else {
			Instance inst2 = m_data.instance(id2);
			u = new double[inst2.numAttributes()-1];
			double precalc1;
			if (id1 == -1){
				for(int i=0,j=0;i<inst1.numAttributes();i++){
					if(i!=inst1.classIndex()){
						u[j] = inst1.value(i) - inst2.value(i);
						j++;
					}
				}
			}
			else{
				for(int i=0,j=0;i<inst1.numAttributes();i++){
					if(i!=inst1.classIndex()){
						u[j] = m_data.instance(id1).value(i) - inst2.value(i);
						j++;
					}
				}
			}
			
			double result = evaluatePRDF(u);

			return result;
		}
	}

	/**
	 * Sets the gamma value.
	 * 
	 * @param value	the gamma value
	 */
	public void setD(double value) {
		m_d = value;
	}

	/**
	 * Gets the gamma value.
	 * 
	 * @return		the gamma value
	 */
	public double getD() {
		return m_d;
	}
	
	public void setPDRFType(int type){
		m_type = type;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return 		tip text for this property suitable for
	 * 			displaying in the explorer/experimenter gui
	 */
	public String dTipText() {
		return "The d value.";
	}

	/**
	 * initializes variables etc.
	 * 
	 * @param data	the data to use
	 */
	protected void initVars(Instances data) {
		super.initVars(data);

		m_kernelPrecalc = new double[data.numInstances()];
	}

	/** 
	 * Returns the Capabilities of this kernel.
	 *
	 * @return            the capabilities of this object
	 * @see               Capabilities
	 */
	//  public Capabilities getCapabilities() {
	//	    Capabilities result = super.getCapabilities();
	//    
	//	    result.enable(Capability.NUMERIC_ATTRIBUTES);
	//	    result.enableAllClasses();
	//    
	//	    return result;
	//  }

	/**
	 * builds the kernel with the given data. Initializes the kernel cache. 
	 * The actual size of the cache in bytes is (64 * cacheSize).
	 * 
	 * @param data	the data to base the kernel on
	 * @throws Exception	if something goes wrong
	 */
//	public void buildKernel(Instances data) throws Exception {
		// does kernel handle the data?
		//	    if (!getChecksTurnedOff())
		//	      getCapabilities().testWithFail(data);

//		initVars(data);
//
//		for (int i = 0; i < data.numInstances(); i++)
//			m_kernelPrecalc[i] = dotProd(data.instance(i), data.instance(i));
//	}

	/**
	 * returns a string representation for the Kernel
	 * 
	 * @return 		a string representation of the kernel
	 */
	public String toString() {
		return "PRDF kernel: K(x,y) = Productory_{k=1}^n a^k (x-y)_k";
	}
	
	/**
	 * Evaluates a vector of doubles (an instance) with the 
	 * Positive Definite Functions associated 
	 * @param u The instance to be evaluated
	 * @return The result of evaluate the instance with the PDRF
	 */
	public double evaluatePRDF(double u[]){
		double prod = 1;
		for(int i=0;i<u.length;i++){
			if(m_type == SymmetricTriangle)
				prod *= symmetricTriangle(u[i]);
			else if(m_type == Gaussian)
				prod *= gaussian(u[i]); 
			else if(m_type == Cauchy)
				prod *= cauchy(u[i]); 
			else if(m_type == Laplace)
				prod *= laplace(u[i]); 
			else if(m_type == HyperbolicSecant)
				prod *= hyperbolicSecant(u[i]); 
			else if(m_type == SquaredSinc)
				prod *= squaredSinc(u[i]);
		}
		
		return prod;
	}
	
	/**
	 * Computes the result of the Symmetric Triangle PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double symmetricTriangle(double x){
		return Math.max(0, 1 - m_d*Math.abs(x));
	}
	
	/**
	 * Computes the result of the Gaussian PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double gaussian(double x){
		return Math.exp(-m_d * Math.pow(x, 2));
	}
	
	/**
	 * Computes the result of the Cauchy PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double cauchy(double x){
		return (1.0 / (1.0+ m_d * Math.pow(x,2)));
	}
	
	/**
	 * Computes the result of the Laplace PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double laplace(double x){
		return Math.exp(-m_d * Math.abs(x));
	}
	
	/**
	 * Computes the result of the Hyperbolic Secant PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double hyperbolicSecant(double x){
		return ((2.0)/(Math.exp(m_d * x) + Math.exp(-m_d * x)));
	}
	
	/**
	 * Computes the result of the Squared Sinc PDRF
	 * @param x The input of this function (x-z)
	 * @return The result obtained
	 */
	public double squaredSinc(double x){
		return ((Math.pow(Math.sin(m_d * x), 2)) / (Math.pow(m_d, 2)) * Math.pow(x,2));
	}

}

