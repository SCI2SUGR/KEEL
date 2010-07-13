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

/**
 * <p>
 * @author Written by Amelia Zafra, Sebastian Ventura (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class AbstractDataset implements IDataset 
{
	/**
	 * <p>
	 * IDataset abstract implementation
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Dataset name */
	
	protected String name;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Internal variables
	/////////////////////////////////////////////////////////////////
	
	/** Dataset specification */
	
	protected Metadata metadata;
	
	/** Cursor position */
	
	protected int cursorPosition;
	
	/** Instance at cursor position */
	
	protected Instance cursorInstance;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	
	public AbstractDataset() 
	{
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// Setting properties
	
	/**
	 * <p>
	 * Sets the name of this dataset
	 * </p> 
	 * @param name New name of the dataset 
	 */	
	public final void setName(String name)
	{
		this.name = name;
	}
	
	// IDataset interface
	
	/**
	 * <p>
     * Get name of this dataset
	 * </p>
     * @return name of this dataset
     */
	public String getName() 
	{
		return name;
	}

    /**
	 * <p>
     * Access to this dataset specification
	 * </p>
     * @return Dataset specification
     */
	public IMetadata getMetadata() 
	{
		return metadata;
	}

	/**  
	 * <p>
	 * Open dataset 	 
	 * </p>
	 * @throws DatasetException If dataset can't be opened
	 */
	public abstract void open() throws DatasetException;

	/**  
	 * <p>
	 * Close dataset
	 * </p>
	 * @throws DatasetException If dataset can't be closed
	 */
	public abstract void close() throws DatasetException;	

	/**
	 * <p>
	 * Returns the cursor position
	 * </p>
	 * @return int Cursor Position
	 */	
	public int getCursorPosition() 
	{
		return cursorPosition;
	}

	/**
	 * <p>
	 * Get the number of Instances. 
	 * This  implementation uses  the reset and next  methods. Current
	 * cursor position is stored into a temporary variable and finally 
	 * recovered.
	 * </p>
	 * @return number of Instances
	 * @throws DatasetException if a source access error occurs
	 */		
	public int numberOfInstances() throws DatasetException
	{
		// Save cursor position
        int currCursorPosition = cursorPosition;
        // Count vectors:
        reset();
        int numbVec = 0;
        while ( next() )
          numbVec = numbVec + 1;
        
        // Restore cursor position;
        reset();
        for (int i = 0; i < currCursorPosition; i++)
          next();
   
        return numbVec;
	}

	/** 
	 * <p>
	 * Reset dataset
	 * </p>
	 * @throws DatasetException if a source access error occurs
	 */	
	public abstract void reset() throws DatasetException;

	/**
	 * <p>
	 * Return the next instance
	 * </p>
	 * @return The next instance
	 * @throws DatasetException if a source access error occurs
	 */
	public abstract boolean next() throws DatasetException;

	/**
	 * <p>
	 * Move cursor to index position
	 * </p>
	 * @param index New cursor position
	 * @return true|false 
	 * @throws DatasetException if a source access error occurs
	 */
	public abstract boolean move(int index) throws DatasetException;
	
    /**
	 * <p>
     * Reads instance at specified row number
	 * </p>
     * @param     rowNumber the row number
     * @return    Instance at specified row
     * @exception DatasetException if an error occurs
     */
    public IInstance read(int rowNumber) throws DatasetException
    {
        move(rowNumber);
        return read();
    }
	
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
    public int read(IInstance [] buffer) throws DatasetException
    {
       return read(buffer, 0, buffer.length);
    }
    
	/**
	 * <p>
	 * Returns cursor instance
	 * </p>
	 * @return Actual instance (if exists)
	 * @throws DatasetException if a source access error occurs
	 */
    public int read( IInstance[] b, int off, int len ) throws DatasetException
    {
    	int i = 0;
    	if(b == null) {
    		throw new DatasetException( "Array b can't be null." );
    	}
    	if( ( off < 0 ) || ( off > b.length ) || ( len < 0 ) || ( ( off + len ) > b.length ) || ( ( off + len ) < 0 ) ) {
    		throw new DatasetException( "Index out of bounds. Check offset and length." );
    	}
    	if (len == 0) {
    		return 0;
    	}
    	for( ; i < len ; i++ ) {
    		if( !next() ) {
    			break;
    		}
    		b[off + i] = read();
    	}
    	return i;
    }   
    
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Internal classes
	/////////////////////////////////////////////////////////////////
	
	protected class Instance implements IInstance
	{

		/**
		 * <p>
		 * Implementation of the IInstance interface
		 * </p>
		 */
		
		/////////////////////////////////////////////////////////////
		// ----------------------------------------------- Properties
		/////////////////////////////////////////////////////////////

		/** Attribute values */
		
		protected double [] values;
		
		/** weight of this instance */
		
		protected double weight;
		
		/////////////////////////////////////////////////////////////
		// --------------------------------------------- Constructors
		/////////////////////////////////////////////////////////////

		/**
		 * <p>
		 * Empty constructor. 
		 * 
		 * Allocates space for internal values 
		 * </p>
		 */		
		protected Instance()
		{
			super();
			this.values = new double[metadata.numberOfAttributes()];
		}
		
		/////////////////////////////////////////////////////////////
		// ------------------------------------------- Public methods
		/////////////////////////////////////////////////////////////

		// Setting values
		
		/**
		 * <p>
		 * Sets a double value in the specified attribute index
		 * </p>
		 * @param attributeIndex Index to set the value
		 * @param attributeValue Value to set
		 */		
		public final void setValue(int attributeIndex, double attributeValue)
		{
			this.values[attributeIndex] = attributeValue;
		}
		
		/**
		 * <p>
		 * Sets a string value in the specified attribute index
		 * </p>
		 * @param attributeIndex Index to set the value
		 * @param attributeValue Value to set
		 */	
		public final void setValue(int attributeIndex, String attributeString)
		{
			
		}

		// Setting weight
		/**
		 * <p>
		 * Sets the weigth of this instance
		 * </p>
		 * @param weight Weight to set
		 */	
		public final void setWeight(double weight)
		{
			this.weight = weight;
		}
		
		// IInstance interface

	    /**
	     * <p>
	     * Get value array of the this instance
	     * </p>
	     * @return value array of the vector (as copy???)
	     */
		public double[] getValues() 
		{
			return values;
		}

	    /**
	     * <p>
	     * Returns the weight of this instance
	     * </p>
	     * @return instance weight
	     */
		public double getWeight() 
		{
			return weight;
		}

	    /**
	     * <p>
	     * Returns vector's attribute value in internal format
	     * </p>
	     * @param attributeIndex attribute index for value to read
	     * @return the specified value as a double (If the corresponding
	     * 		   attribute is categorical then it returns the value's 
	     * 		   index as a double).
	     */
		public double getValue(int attributeIndex) 
		{
			return values[attributeIndex];
		}

	    /**
	     * <p>
	     * Returns value at specified attribute
	     * </p>
	     * @param attribute mining attribute
	     * @return value of mining attribute
	     */
		public double getValue(IAttribute attribute) 
		{
			return values[metadata.getIndex(attribute)];
		}

	    /**
	     * <p>
	     * Returns value at specified attribute name
	     * </p>
	     * @param attributeName mining attribute name
	     * @return value of mining attribute
	     */
		public double getValue(String attributeName) 
		{
			return values[metadata.getIndex(attributeName)];
		}		
	}
}

