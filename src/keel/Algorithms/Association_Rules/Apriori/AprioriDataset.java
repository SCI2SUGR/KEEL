/**
* <p>
* @author Written by Cristobal Romero (Universidad de Córdoba) 27/02/2007
* @version 0.1
* @since JDK 1.5
*</p>
*/

package keel.Algorithms.Association_Rules.Apriori;
import java.io.*;
import java.util.*;

import keel.Dataset.Attributes;
import keel.Dataset.DatasetException;
import keel.Dataset.HeaderFormatException;
import keel.Dataset.InstanceSet;


/**
 * A class for handling a database for datamining purposes.
 * Is used for getting data from the database.
 */
public class AprioriDataset 
{
	/** All the items in the dataset. */
	protected Vector elements = new Vector();
	
	/** The transactions. */
	protected int [][]data = new int[100000][];
	
	/** Number of transactions in dataset. */
	protected int nTransactions;
	
	/** The name of the dataset. */
	protected String name = ""; 	
	
	/** The attributes. */
	protected Vector attributes;	
  
	/**
	 * Constructor for creating a DataHandler object with parameters.
	 *
	 * @param	fileName	the name of the database file
	 */
	public AprioriDataset( String fileName )
	{
		try
		{
			StreamTokenizer tokenizer = new StreamTokenizer( new FileReader( 
					fileName ) );
			initTokenizer( tokenizer );
			readHeader( tokenizer );
		}
		catch ( IOException e )
		{
			System.err.println( e.getMessage() );
		}
 	}

 	/** Function to read and stores header of a dat file.
 	 * 
 	 * @param tokenizer		The tokenizer where the header is readed.
 	 * 
 	 * @throws IOException	If the tokenizer cannot be readed.s
 	 */ 
 	protected void readHeader( StreamTokenizer tokenizer ) 
		throws IOException
	{
 		String attributeName;
 		Vector attributeValues;
 		int i;

 		// Get name of relation.
 		getFirstToken( tokenizer );
 		try
		{
  		if ( tokenizer.ttype == StreamTokenizer.TT_EOF ) 
  			throw new Exception( "Premature end of file." );

  		if ( tokenizer.sval.equalsIgnoreCase( "@relation" ) )
  		{
  			while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL )
  				name += tokenizer.sval + " ";
  		}
  		else
  			throw new Exception( "Keyword @relation expected." );

  		// Create vectors to hold information temporarily.
  		attributes = new Vector();
 
  		// Get attribute declarations.
  		getFirstToken( tokenizer );
    
  		if ( tokenizer.ttype == StreamTokenizer.TT_EOF ) 
  			throw new Exception( "Premature end of file." );

  		while ( tokenizer.sval.equalsIgnoreCase( "@attribute" ) ) 
  		{
  			// Get attribute name.
  			getNextToken( tokenizer );
  			attributeName = tokenizer.sval;
  			getNextToken( tokenizer );

  			// Check if attribute is nominal.
  			if ( tokenizer.ttype == StreamTokenizer.TT_WORD ) 
  				throw new Exception( "Apriori algorithm can not handle " +
  						"numeric or string attributes." );
  			else
  			{
  				// Attribute is nominal.
  				attributeValues = new Vector();
  				tokenizer.pushBack();
	
  				// Get values for nominal attribute.
  				if ( tokenizer.nextToken() != '{' ) 
  					throw new Exception( "{ expected at beginning of " +
  							"enumeration." );

  				while ( tokenizer.nextToken() != '}' ) 
  				{
  					if ( tokenizer.ttype == StreamTokenizer.TT_EOL ) 
  						throw new Exception( "} expected at end of " +
  								"enumeration." );
  					else 
  						attributeValues.addElement( tokenizer.sval );
  				}

  				if ( attributeValues.size() == 0 )
  					throw new Exception( "No nominal values found." );
  				
  				attributes.addElement( new Attribute( attributeName, 
  						attributeValues, numAttributes() ) );
  			}
      
  			getLastToken( tokenizer, false );
  			getFirstToken( tokenizer );
      
  			if ( tokenizer.ttype == StreamTokenizer.TT_EOF )
  				throw new Exception( "Premature end of file." );
  		}

  		if ( tokenizer.sval.equalsIgnoreCase( "@inputs" ) )
  		{
  			tokenizer.nextToken();
  			
  			while( tokenizer.ttype != StreamTokenizer.TT_EOL )
  			{
  				int k = 0;
  	  		
  				while ( k < attributes.size() && !( (Attribute)attributes.
  						elementAt( k ) ).name().equalsIgnoreCase( 
  								tokenizer.sval ) )
  					k++;
  	  			
  				if ( k < attributes.size() )
 						( (Attribute)attributes.elementAt( k ) ).activate();
  	  			
 					tokenizer.nextToken();
 				}
  		
 				tokenizer.nextToken();
 			}
  		
 			if ( tokenizer.sval.equalsIgnoreCase( "@outputs" ) ) 
 			{
 				tokenizer.nextToken();
  			
 				i = 0;
 				Attribute att;

 				do
  			{
  				att = (Attribute)attributes.elementAt(i);
  				i++;
  			}
  			while ( i < numAttributes() && !tokenizer.sval.equals( 
  					att.name()) );
  			
  			getFirstToken( tokenizer );
  		}

  		/* Read the possible items */
  		for ( i = 0; i < attributes.size(); i++ )
  		{
  			Attribute attribute = (Attribute)attributes.elementAt( i );
  				
  			for ( int j = 0; j < attribute.numValues(); j++ )
  				elements.addElement( attribute.value( j ) );
  		}
  			
  		if (!tokenizer.sval.equalsIgnoreCase( "@data" ) )
  			throw new Exception( "Keyword @data expected." );
  		else
  		{
  	  	while( tokenizer.nextToken() != StreamTokenizer.TT_EOF )
  	  	{
  	  		tokenizer.pushBack();
  				int[] itemset;
  				int[] aux = new int[100];
  				int k = 0;
  					
  	      while( tokenizer.nextToken() != StreamTokenizer.TT_EOL 
  	      					&& tokenizer.ttype != StreamTokenizer.TT_EOF )
  	      {
  					if ( tokenizer.sval.equalsIgnoreCase( "<null>" ) )
  						aux[k] = -1;
  					else
  						aux[k] = indexOf( tokenizer.sval, k ) + 1;	
  				
  					k++;
  				}
  					
  				itemset = new int[i];
  					
  				for ( int j = 0; j < i; j++ )
  					itemset[j] = aux[j];
  						
  	      data[nTransactions] = itemset;	
  	      nTransactions++;
  	  	}
 			}

 			// Check if any attributes have been declared.
 			if ( attributes.size() == 0 ) 
 				throw new Exception( "No attributes declared." );
  			
		}
 		catch ( Exception e )
		{
 			System.err.println( e.getMessage() );
 		}
	}
	
	/** Returns the number of attributes.
	 * 
	 */
	public final int numAttributes()
	{
 		return attributes.size();
	}

	/** Returns the transaction with the given index.
	 * 
	 * @param index		The index of the transaction.
	 */
	public int[] getTransaction( int index )
	{
		return data[index];
	}
	
 	/** Returns the element with the given index.
 	 * 
 	 * @param index		The index of the element.
 	 */
	public String getElement( int index )
	{
 		return (String)elements.elementAt( index );
 	}
	
 	/** Returns the index of the value in the attribute with the given 
 	 * index.
 	 * 
 	 * @param value			The value.
 	 * @param attIndex		The index attribute.
 	 */
	private int indexOf( String value, int attIndex )
	{
 		int k = 0;
 		
 		for ( int i = 0; i < attIndex; i++ )
 			k += ( (Attribute) attributes.elementAt( i ) ).numValues();
 		
 		return k + ( (Attribute) attributes.elementAt( attIndex ) ).
			valueIndex( value );
 	}
 	
 	/** Returns the element with the given label.
 	 * 
 	 * @param label		The label.
 	 */
	public int getElement( String label )
	{
 		int i = 0;
 		
 		while( !( (String) elements.elementAt( i ) ).equalsIgnoreCase( 
 				label ) )
 			i++;
 		
 		return i + 1;
 	}
  	
 	/** Gets next token, skipping empty lines. 
 	 * 
 	 * @param tokenizer		The tokenizer where the function must read.
 	 * 
 	 * @throws IOException	If the tokenizer cannot be readed.
 	 */
 	private void getFirstToken( StreamTokenizer tokenizer ) 
		throws IOException
	{
  	while ( tokenizer.nextToken() == StreamTokenizer.TT_EOL )
    
  	if ( ( tokenizer.ttype == '\'' ) || ( tokenizer.ttype == '"' ) )
  		tokenizer.ttype = StreamTokenizer.TT_WORD;
  }

  /** Gets token and checks if its end of line. 
   * 
   * @param tokenizer		The tokenizer where the function must read.
   * @param endOfFileOk	Read the end of file.
   * 
   * @throws IOException	If the tokenizer cannot be readed.
   */
  private void getLastToken( StreamTokenizer tokenizer, 
  		boolean endOfFileOk ) throws Exception
	{
 		if ( ( tokenizer.nextToken() != StreamTokenizer.TT_EOL ) &&
 				( ( tokenizer.nextToken() != StreamTokenizer.TT_EOF ) || 
 						!endOfFileOk ) )
 			throw new Exception( "End of line expected." );
  }

  /** Gets next token, checking for a premature and of line. 
   * 
   * @param tokenizer		The tokenizer where the function must read.
   * 
   * @throws IOException	If the tokenizer cannot be readed.
   */
  private void getNextToken( StreamTokenizer tokenizer ) 
		throws Exception
	{
  	if ( tokenizer.nextToken() == StreamTokenizer.TT_EOL )
  		throw new Exception( "Premature end of line." );

  	if ( tokenizer.ttype == StreamTokenizer.TT_EOF ) 
  		throw new Exception( "Premature end of file." );
  	else if ( ( tokenizer.ttype == '\'' ) || ( 
  			tokenizer.ttype == '"' ) )
  		tokenizer.ttype = StreamTokenizer.TT_WORD;
	}
	
	/** Function to initialize the stream tokenizer.
	 * 
	 * @param tokenizer		The tokenizer.
	 */
 	private void initTokenizer( StreamTokenizer tokenizer )
	{
 		tokenizer.resetSyntax();         
 		tokenizer.whitespaceChars( 0, ' ' );    
 		tokenizer.wordChars( ' ' + 1 , '\u00FF' );
 		tokenizer.whitespaceChars( ',', ',' );
 		tokenizer.commentChar( '%' );
 		tokenizer.quoteChar( '"' );
 		tokenizer.quoteChar( '\'' );
 		tokenizer.ordinaryChar( '{' );
 		tokenizer.ordinaryChar( '}' );
 		tokenizer.ordinaryChar( '[' );
 		tokenizer.ordinaryChar( ']' );
 		tokenizer.eolIsSignificant( true );
	}
}