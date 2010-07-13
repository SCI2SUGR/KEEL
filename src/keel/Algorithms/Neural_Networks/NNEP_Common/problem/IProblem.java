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

package keel.Algorithms.Neural_Networks.NNEP_Common.problem;

import keel.Algorithms.Neural_Networks.NNEP_Common.data.DoubleTransposedDataSet;
import net.sf.jclec.util.range.Interval;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IProblem{
	
	/**
	 * <p>
	 * Represents a problem with training and test data
	 * </p>
	 */

    /////////////////////////////////////////////////////////////////
    // -------------------------------------------- Managing datasets
    /////////////////////////////////////////////////////////////////
	
    /**
     * <p>
     * Returns the train data associated to this problem
     * </p>
     * @return DataSet Train data set
     */
    public DoubleTransposedDataSet getTrainData();
    
    /**
     * <p>
     * Returns the test data associated to this problem
     * </p>
     * @return DataSet Test data set
     */
    public DoubleTransposedDataSet getTestData();
    
    /**
     * <p>
	 * Returns a boolean value indicating if the DataSets are going to be normalized
     * </p>
	 * @return true if DataSets going to be normalized
	 */
    public boolean isDataNormalized();
    
    /**
     * <p>
	 * Returns a boolean value indicating if the DataSets are going to be log
	 * transformated
     * </p>
	 * @return true if DataSets going to be transformated
	 */
    public boolean isLogTransformation();
    
    /**
     * <p>
	 * Returns the input interval of normalized data
     * </p> 
	 * @return Interval Input normalization interval
	 */
	public Interval getInputInterval();
	
    /**
     * <p>
	 * Returns the input interval of normalized data
     * </p>
	 * @return Interval Output normalization interval
	 */
	public Interval getOutputInterval();
}

