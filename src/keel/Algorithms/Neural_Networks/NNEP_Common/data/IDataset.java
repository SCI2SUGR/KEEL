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

package keel.Algorithms.Neural_Networks.NNEP_Common.data;

import net.sf.jclec.IConfigure;
import net.sf.jclec.JCLEC;

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public interface IDataset extends JCLEC, IConfigure 
{
	/**
	 * <p>
	 * Dataset Interface
	 * </p>
	 */
	
	// Dataset information
	
	/**
	 * <p>
     * Get name of this dataset
	 * </p>
     * @return name of this dataset
     */
    public String getName();

    /**
	 * <p>
     * Access to this dataset specification
	 * </p>
     * @return Dataset specification
     */
    public IMetadata getMetadata();
    
	/**
	 * <p>
	 * Get the number of Instances
	 * </p>
	 * @return number of Instances
	 * @throws DatasetException if a source access error occurs
	 */
	public int numberOfInstances() throws DatasetException;
	
	// Open and close dataset
	
	/**  
	 * <p>
	 * Open dataset 	 
	 * </p>
	 * @throws DatasetException If dataset can't be opened
	 */
	public void open() throws DatasetException;

	/**  
	 * <p>
	 * Close dataset
	 * </p>
	 * @throws DatasetException If dataset can't be closed
	 */
	public void close() throws DatasetException;
	
	// Traversal operations
	
	/**
	 * <p>
	 * Return the next instance
	 * </p>
	 * @return The next instance
	 * @throws DatasetException if a source access error occurs
	 */
	public boolean next() throws DatasetException;

	/**
	 * <p>
	 * Move cursor to index position
	 * </p>
	 * @param index New cursor position
	 * @return true|false 
	 * @throws DatasetException if a source access error occurs
	 */
	public boolean move(int index) throws DatasetException;
	
	/** 
	 * <p>
	 * Reset dataset
	 * </p>
	 * @throws DatasetException if a source access error occurs
	 */	
	public void reset() throws DatasetException;	

	/**
	 * <p>
	 * Returns cursor instance
	 * </p>
	 * @return Actual instance (if exists)
	 * @throws DatasetException if a source access error occurs
	 */
	public IInstance read() throws DatasetException;
	
    /**
	 * <p>
     * Reads instance at specified row number
	 * </p>
     * @param     rowNumber the row number
     * @return    Instance at specified row
     * @exception DatasetException if an error occurs
     */
    public IInstance read(int rowNumber) throws DatasetException;
	
    /**
	 * <p>
     * Reads some number of instances from the dataset and stores them 
     * into a buffer  array. The number of  instances actually read is 
     * returned as an integer
	 * </p>
     * @param      buffer the buffer into which the data is read
     * @return     the total number of instances read into the buffer, 
     * 			   or <code>-1</code> is there is no more data because 
     * 			   the end of the stream has been reached
     * @exception  MiningException  if an error occurs
     */    
    public int read(IInstance[] buffer) throws DatasetException;
    
    /**
	 * <p>
     * Reads up to <code>length</code> instances from the input stream 
     * into an array of IInstances. An attempt is made to read as many 
     * as <code>length</code> instances, but  a smaller number  may be 
     * read, possibly  zero. The number of  instances actually read is 
     * returned as an integer
	 * </p>
     * @param      buffer the buffer into which the data is read
     * @param      offset the start offset in array <code>buffer</code>
     *                    at which the data is written
     * @param      length the maximum number of MiningVectors to read
     * @return     the total number of instances read into the buffer, 
     * 			   or <code>-1</code> if there is no more data because 
     * 			   the end of the stream has been 
     * @exception  DatasetException if an error occurs
     */
    public int read(IInstance [] buffer, int offset, int length) throws DatasetException;
    
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Internal classes
	/////////////////////////////////////////////////////////////////
	
	public interface IInstance
	{
		/**
		 * <p>
		 * Dataset instance
		 * </p>
		 */
		
	    /**
	     * <p>
	     * Returns the weight of this instance
	     * </p>
	     * @return instance weight
	     */
		public double getWeight();

	    /**
	     * <p>
	     * Get value array of the this instance
	     * </p>
	     * @return value array of the vector (as copy???)
	     */
	    public double[] getValues();
	    
	    /**
	     * <p>
	     * Returns vector's attribute value in internal format
	     * </p>
	     * @param attributeIndex attribute index for value to read
	     * @return the specified value as a double (If the corresponding
	     * 		   attribute is categorical then it returns the value's 
	     * 		   index as a double).
	     */
	    public double getValue( int attributeIndex );

	    /**
	     * <p>
	     * Returns value at specified attribute
	     * </p>
	     * @param attribute mining attribute
	     * @return value of mining attribute
	     */
	    public double getValue( IAttribute attribute );

	    /**
	     * <p>
	     * Returns value at specified attribute name
	     * </p>
	     * @param attributeName mining attribute name
	     * @return value of mining attribute
	     */
	    public double getValue( String attributeName );
	    
		// Aqui se pueden poner mas metodos, asumiendo que el vector va 
	    // a tener acceso a sus metadatos... 
	}    
}

