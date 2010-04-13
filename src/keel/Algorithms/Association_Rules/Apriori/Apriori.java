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


/**
 * Basic implementation of the Apriori algorithm 
 * for finding frequent itemsets
 */
 	
public class Apriori 
{
	/**	 Number of passes. */
	private int pass;		

	/** Total number of frequent itemsets. */
	private int total;									
	
	/** Minimal support of itemset. */
	private int minSupport = 250;		
	
	/** The root item of the tree. */
	private Item root;				
	
	/** The buffer to write the output to. */
	private BufferedWriter writer;					
	
	/** The dataset. */
	private AprioriDataset dataset;						
	
	/** The frequent itemsets. */
	private Vector frequent = new Vector();			
	
	/** Minimum confidence. */
	private double minConfidence = 0.9;
	
	/** The name of the output file. */
	private String outputFileName; 						
	
	/** The name of the train file. */
	private String trainFileName; 	
	
	/** The name of the test file. */
	private String testFileName;
	
	/** The name of the data file. */
	private String dataFileName;					
	
	/** Maximum number of items that can contain the antecedent. */
	private int antecedentMaxSize = Integer.MAX_VALUE;	
	
	/** Maximum number of items that can contain the consecuent. */
	private int consecuentMaxSize = Integer.MAX_VALUE;	
	
	/** The information about the construction of frequent itemsets. */
	private String frequents = "";						
	
	/** Number of rules generated. */
	private int nRules = 0;				
	
	/** Number of parameters of the algorithm. */
	private int nParam = 4;					
	
	/** The instant of starting the algorithm. */
	private long startTime;

	
	/** Constructor for creating an Apriori object.
	 * 
	 * @param file			The parameters file.
	 * 
	 * @throws Exception	If the algorithm cannot execute properly.
	 */
	public Apriori( String file ) throws Exception
	{
 		pass = 0;
		root = new Item( 0 );

 		try
 		{
 			
 			// starts the time 
      startTime = System.currentTimeMillis();
        
   		/* Sets the options of the execution */
   		StreamTokenizer tokenizer = new StreamTokenizer( new 
   				BufferedReader( new FileReader( file ) ) );
   		initTokenizer( tokenizer) ;
   		setOptions( tokenizer );
    		
    	/* Load the dataset. */
    	dataset = new AprioriDataset( dataFileName );


    	/* Writes the header in the result file. */
    	try
			{
				if ( !outputFileName.equals( "" ) ) 
				{
					writer = new BufferedWriter( new FileWriter( 
							outputFileName ) );
					writer.write( getHeader() );
				}
			}
			catch (	Exception e ) 
			{
				System.err.println( e.getMessage() );
			}

			/* Executes the Apriori algorithm. */
			findFrequentSets();
			getFrequentSets();
			generateRules();
  	}
  	catch( Exception e)
		{
  		System.err.println( "System cannot open file." );
  	}
	}

	/** Function to read the options from the execution file and assign 
	 * the values to the parameters.
	 * 
	 * @param options 		The StreamTokenizer that reads the parameters file.
	 * 
	 * @throws Exception	If the format of the file is not correct.
	 */ 
  protected void setOptions( StreamTokenizer options ) throws Exception
	{
  	options.nextToken();
		
  	/* Checks that the file starts with the token algorithm */
		if ( options.sval.equalsIgnoreCase( "algorithm" ) )
		{
			options.nextToken();
			options.nextToken();

			if (!options.sval.equalsIgnoreCase( "Apriori" ) )
				throw new Exception( "The name of the algorithm is not " +
						"correct." );

			options.nextToken();
			options.nextToken();
			
			/* Reads the names of the input files*/
			if ( options.sval.equalsIgnoreCase( "inputData" ) )
			{
				options.nextToken();
				options.nextToken();

				dataFileName = options.sval;
					
				getNextToken( options );
			}
			else
				throw new Exception( "The file must start with the word " +
						"inputData." );
				
			/* Reads the names of the output files*/
			if ( options.sval.equalsIgnoreCase( "outputData" ) )
			{
				options.nextToken();
				options.nextToken();
				trainFileName = options.sval;
				options.nextToken();
				testFileName = options.sval;
				options.nextToken();
				outputFileName = options.sval;
			}
			else
				throw new Exception( "The file must start with the word " +
						"outputData." );
				
			if ( !getNextToken( options ) )
				throw new Exception( "No instances provided." );
			
			if ( options.ttype == StreamTokenizer.TT_EOF )
				return;
			
			for ( int k = 0; k < nParam; k++ )
			{
				/* Reads the minSupport parameter */
				if ( options.sval.equalsIgnoreCase( "minSupport" ) )
				{
					options.nextToken();
					options.nextToken();
				
					if( Integer.parseInt( options.sval ) > 0 )
						minSupport = Integer.parseInt( options.sval );
	
					if ( !getNextToken( options ) )
						return;
					else
						continue;
				}
				
				/* Reads the minConfidence parameter */
				if ( options.sval.equalsIgnoreCase( "minConfidence" ) )
				{
					options.nextToken();
					options.nextToken();
					
					/* Checks that the minConfidence is between 0 and 1. */
					float cf = Float.parseFloat( options.sval );
				
					if(  cf <= 1 || cf >= 0 )
						minConfidence = Float.parseFloat( options.sval );

					if ( !getNextToken( options ) )
						return;
					else
						continue;
				}

				/* Reads the antecedentMaxSize parameter */
				if ( options.sval.equalsIgnoreCase( "antecedentMaxSize" ) )
				{
					options.nextToken();
					options.nextToken();
					
					if( Integer.parseInt( options.sval ) > 0 )
						antecedentMaxSize = Integer.parseInt( options.sval );
	
					if ( !getNextToken( options ) )
						return;
					else
						continue;
				}

				/* Reads the consecuentMaxSize parameter */
				if ( options.sval.equalsIgnoreCase( "consecuentMaxSize" ) )
				{
					options.nextToken();
					options.nextToken();
					
					if( Integer.parseInt( options.sval ) > 0 )
						consecuentMaxSize = Integer.parseInt( options.sval );

					if ( !getNextToken( options ) )
						return;
					else
						continue;
				}
			}
		}
		else
			throw new Exception( "The file must start with the word " +
					"algorithm followed of the name of the algorithm." );
	}

 	/** Function to get the name of the relation and the names, types 
	 * and possible values of every attribute in a dataset.
 	 * 
 	 * @return The name and the attributes of the relation.
 	 */
	private String getHeader()
	{
		String header;
 	  	
  	header = "@relation\t" + dataset.name + "\n";
		
		for ( int i = 0; i < dataset.numAttributes(); i++ )
		{
			Attribute att = (Attribute)dataset.attributes.elementAt( i );
			
 	  	header += "@attribute\t" + att.name() + "\t";
			
			if ( att.name().length() <= 6 )
   	  	header += "\t";
			
			if ( att.isDiscret() )
			{
   	  	header += "{ ";
				
				for ( int j = 0; j < att.numValues(); j++ )
				{
	   	  	header += att.value( j );
					
					if ( j < att.numValues() -1 )
						header += ", ";
				}

				header += " }\n";
			}
			else
			{
				if ( att.getMinRange() != Float.NEGATIVE_INFINITY && 
						att.getMaxRange() != Float.POSITIVE_INFINITY )
			   	  	header += "[ " + att.getMinRange() + ", " + 
								att.getMaxRange() + " ]\n";
				else
			   	  	header += "[]\n";
			}
		}

		return header;
	}
 	  
	/** Function to initialize the stream tokenizer.
	 * 
	 * @param tokenizer		The tokenizer.
	 */
 	private void initTokenizer( StreamTokenizer tokenizer )
 	{
 		tokenizer.resetSyntax();         
 		tokenizer.whitespaceChars( 0, ' ' );    
 		tokenizer.wordChars( ' '+1,'\u00FF' );
 		tokenizer.whitespaceChars( ',',',' );
 		tokenizer.quoteChar( '"' );
 		tokenizer.quoteChar( '\''  );
 		tokenizer.ordinaryChar( '=' );
 		tokenizer.ordinaryChar( '{' );
 		tokenizer.ordinaryChar( '}' );
 		tokenizer.ordinaryChar( '[' );
 	  tokenizer.ordinaryChar( ']' );
 	  tokenizer.eolIsSignificant( true );
 	}
 	
  /** Puts the tokenizer in the first token of the next line.
   * 
   * @param tokenizer		The tokenizer which reads this function.
   * 
   * @return				True if reaches the end of file. False otherwise.
   * 
   * @throws Exception	If cannot read the tokenizer.
   */ 
 	private boolean getNextToken( StreamTokenizer tokenizer )
	{
 		try
		{
 			if ( tokenizer.nextToken() == StreamTokenizer.TT_EOF )
 				return false;
 			else
 			{
 				tokenizer.pushBack();

 				while ( tokenizer.nextToken() != StreamTokenizer.TT_EOL );
  			
 				while ( tokenizer.nextToken() == StreamTokenizer.TT_EOL );
  			
 				if ( tokenizer.sval == null )
 					return false;
 				else
 					return true;
 			}
		}
 		catch( Exception e )
		{
 			System.err.println( e.getMessage() );

 			return false;
  	}
  }
  	
	/** Function to find the frequent itemsets. 
	 * 
	 */
	public void findFrequentSets() 
	{
		boolean running = true;
		int candidates = 0, transactions= 0, pruned = 0, nItemsets;
		
		try
		{
			while ( running )
			{
				pass++;

				candidates = generateCandidates( root, new Vector(), 1 );
				transactions = countSupport();
				pruned = pruneCandidates( root );
				nItemsets = candidates - pruned;
      
				// correct the candidate count on first pass for printing
				if ( pass == 1 )
					candidates = total;

				total += nItemsets;
      
				if ( nItemsets <= pass && pass > 1 )
					running = false;
				else
					frequents += "\n@FrequentItemsetsOfSize " + pass + ": " + 
						candidates;
			}
		}
		catch ( Exception e )
		{
			System.err.println( "Not enough memory." );
		}
	}  

	/** Function to generate new candidates starting the search in the 
	 * given item.
	 * 
	 * @param item			The item to start.
	 * @param current		The current set of candidates.
	 * @param depth			The current depth in the tree.
	 * 
	 * @return				The number of candidates generated.
	 */ 
	public int generateCandidates( Item item, Vector current, int depth )
	{
		Vector v = item.getChildren(); 
		Item child;
		int generated = 0;

		for ( Enumeration e = v.elements(); e.hasMoreElements(); )
		{ 
			child = (Item)e.nextElement(); 
			current.add( child );

			if ( depth == pass-1 )
				generated += copySiblings( child, v, current );
			else if ( depth + 1 < antecedentMaxSize + consecuentMaxSize )
				generated += generateCandidates( child, current, depth + 1 );
      
			current.remove( child );
		} 
    
		return generated;
	}

	/** Function to copy the siblings of an item to its children.
	 * 
	 * @param item			The item.
	 * @param siblings		The siblings of the item.
	 * @param current		The current set of candidates.
	 * 
	 * @return				The number of items copied.
	 */ 
	public int copySiblings( Item item, Vector siblings, Vector current ) 
	{
		Enumeration e = siblings.elements();
		Item parent = item;
		Item sibling = new Item(); 
		int copied = 0;

		while ( sibling.getLabel() < parent.getLabel() && 
				e.hasMoreElements() )
			sibling = (Item)e.nextElement();

		while ( e.hasMoreElements() )
		{
			sibling = (Item)e.nextElement();
			current.add( sibling );
      
			if ( pass <= 2 || checkSubsets( current, root.getChildren(),
					0, 0 ) )
			{
				parent.addChild( new Item( sibling.getLabel() ) );     
				copied++;
			}
      
			current.remove( sibling );
		}

		return copied;
	}
  
	/** Function to check if the subsets of the itemset to be generated 
	 * are all frequent. 
	 * 
	 * @param current		The current set of candidates.
	 * @param children		The children.
	 * @param mark			
	 * @param depth			The depth in tree search.
	 * 
	 * @return				True if all the subsets are frequents. 
	 * 								False otherwise.
	 */
	public boolean checkSubsets( Vector current, Vector children,
			int mark, int depth ) 
	{
		boolean ok = true;
		Item child;
		int index;
		int i = depth;

		if ( children == null ) 
			return false;

		while ( ok && ( mark <= i ) ) 
		{
			index = children.indexOf( current.elementAt( i ) );
     
			if ( index >= 0 )
				if ( depth < pass - 1 )
				{
					child = (Item)children.elementAt( index );
					ok = checkSubsets( current, child.getChildren(), i + 1, 
							depth + 1 );
				}
    		else
    			ok = false;

			i--;
		}

		return ok;
	}

	/** Function to count the support of the candidates generated on 
	 * 	this pass. 
	 * 
	 * @return	The number of transactions readed.
	 */
	public int countSupport() 
	{
		int rowcount = 0;
		int[] items;
    
		items = dataset.getTransaction( rowcount );
		
		while ( items != null )
		{
			items = sortItems( items );

			rowcount++;
      
			if ( pass == 1 )
			{
				root.incSupport();
				total += generateFirstCandidates( items );
			}
			else
				countSupport( root, items, 0, 1 );

			items = dataset.getTransaction( rowcount );
		}
    
		return rowcount;
	}

	/** Function to generate the first candidates by adding each item 
	 * found in the database to the children of the root item.
	 * 
	 * @param items		All the transactions in the dataset.
	 * 
	 * @return			The number of candidates generated.
	 */ 
	public int generateFirstCandidates( int[] items )
	{
 		Vector v = root.getChildren(); 
 		Enumeration e = v.elements();
 		Item item = new Item();
 		int generated = 0;

 		try
		{
 			for ( int i = 0; i < items.length; i++ )
 			{ 
 				if ( items[i] == -1 )
 					continue;
  				
 				while ( e.hasMoreElements() && item.getLabel() < items[i] )
 					item = (Item)e.nextElement(); 

 				if ( item.getLabel() == items[i] )
 				{
 					item.incSupport();
        
 					if ( e.hasMoreElements() )
 						item = (Item)e.nextElement(); 
 				}
 				else if ( item.getLabel() > items[i] )
 				{
 					int index = v.indexOf( item );
 					Item child = new Item( items[i] );
 					child.incSupport();
 					root.addChild( child, index ); 
 					generated++;
 				}
 				else
 				{ 
 					Item child = new Item( items[i] );
 					child.incSupport();
 					root.addChild( child );
 					generated++;
 				}
 			}
		}
 		catch( Exception ex )
		{
  		System.err.println( ex.getMessage() );
  	}
    
  	return generated;
	}

	/** Function to count the support of an itemset. 
	 * 
	 * @param item		The first item of the set.
	 * @param items		All the itemsets.
	 * @param i			
	 * @param depth
	 */
	public void countSupport( Item item, int[] items, int i, int depth )
	{
		Vector v = item.getChildren(); 
		Item child;
		int tmp;
		Enumeration e = v.elements();

		// loop through the children to check.
		while ( e.hasMoreElements() )
		{
			child = (Item)e.nextElement();
     
			// break, if the whole transaction is checked.
			if ( i == items.length ) 
				break;
      
			// do a linear search for the child in the transaction starting 
			// from i.
			tmp = i;
      
			while ( tmp < items.length && items[tmp] < child.getLabel() )
				tmp++;
      
			// if the same item exists, increase support or go deaper
			if ( tmp < items.length && child.getLabel() == items[tmp] )
			{
				if ( depth == pass )
					child.incSupport();
				else 
					countSupport( child, items, tmp+1, depth+1 );
				
				i = tmp + 1;
			}
		}
	}
  
	/** Function to prune the candidates. 
	 * 
	 * @param item	The item to start the prune.
	 * 
	 * @return		The number of items pruned.
	 */
	public int pruneCandidates( Item item )
	{
		Vector v = item.getChildren(); 
		Item child = item;
		int pruned = 0;
    
		for ( Enumeration e = new Vector( v ).elements(); 
			e.hasMoreElements(); )
		{ 
			child = (Item)e.nextElement(); 

			// check infrequency, existence and that it is fully counted
			if ( child.getSupport() < minSupport )
			{
				v.remove( child );
				pruned++;
			}
			else
				pruned += pruneCandidates( child );
		}
    
		return pruned;
	}
  
	/* Function to get the frequent itemsets. */
	public void getFrequentSets() 
	{
		getFrequentSets( root, "" );
	}
  
	/** Function to get the frequent itemsets. 
	 * 
	 * @param item		The current item.
	 * @param str		The string that contain the items analized previously
	 */
	public void getFrequentSets( Item item, String str ) 
	{
		Vector v = item.getChildren();

		for ( Enumeration e = v.elements(); e.hasMoreElements(); ) 
		{ 
			item = (Item)e.nextElement(); 
      
			try
			{
				frequent.addElement( str + item.getLabel() );
			}
			catch ( Exception x )
			{ 
				System.out.println( "no output file" );
			}
      
			if ( item.hasChildren() )
				getFrequentSets( item, str + item.getLabel() + " " );
		}
	}

	/** Function to generate the rules. 
	 * 
	 */
	public void generateRules()
	{
		Enumeration e = frequent.elements();
		
		try
		{
			writer.write( "\n@Rules ( Confidence / Support)\n" );
			writer.flush();
		}
		catch ( Exception exc )
		{
			System.err.println( exc.getMessage() );
		}
		
		while ( e.hasMoreElements() )
		{
			String itemGroup = (String)e.nextElement();
			
			/* If the current frequent itemset has more than one element, 
			 * we try to generate the rules. */
			if ( itemGroup.length() > 1 )
			{
				Vector items = extractItems( itemGroup );
				
				double itemsetSupport = getSupport( items );

				for ( int i = 0; i < items.size(); i++ )
				{
					Vector ant = new Vector();
					
					for ( int j = i; j < items.size(); j++ )
					{
						ant.addElement( items.elementAt( j ) );
					
						double confidence = itemsetSupport / getSupport( ant );
						
						if ( items.size() != ant.size() )
						{
							if( confidence >= minConfidence 
								&& antecedentMaxSize >= ant.size() && 
								consecuentMaxSize >=  items.size() - ant.size() )
							{
								printRule( items, ant, confidence, (int)
										itemsetSupport );
								nRules++;
							}
						}
					}
				}
			}
		}

		try
		{
			writer.write( "\n\n@NumberOfRulesGenerated: " + nRules );
			writer.write( "\n" + frequents );
			writer.write( "\n@TotalNumberOfFrequentItemsets: " + total );

			long totalTime = ( System.currentTimeMillis() - startTime ) / 
				1000;
	  	long seconds = totalTime % 60;
	  	long minutes = ( ( totalTime - seconds ) % 3600 ) / 60;
  		  	
  		writer.write( "\n\n@ElapsedTime " + ( totalTime - minutes * 60 - 
  				seconds ) / 3600 + ":" + minutes + ":" + seconds );
  		  	
			writer.flush();
			writer.close();
		}
		catch( IOException ex )
		{
			System.err.println( ex.getMessage() );
		}
	}
	
	/** Function to get the support of one itemset. 
	 * 
	 * @param items		The set of items.
	 * 
	 * @return			The support of the itemset.
	 */
	private double getSupport( Vector items )
	{
		Vector v = root.getChildren();
		Enumeration e = v.elements();
		int i = 0;
		
		while ( e.hasMoreElements() )
		{
			Item current = (Item) e.nextElement();
			
			if( current.getLabel() == Integer.parseInt( (String)items.
					elementAt( i ) ) )
			{
				e = current.getChildren().elements();
				i++;
			}
			
			if ( i == items.size() )
				return current.getSupport();
		}
		
		return -1;
	}
	
	/** Function to print one rule. 
	 * 
	 * @param items				All the items of the rule.
	 * @param antecedent		The antecedent of the rule.
	 * @param confidence		The confidence of the rule.
	 * @param support			The support.
	 */
	private void printRule( Vector items, Vector antecedent, double 
			confidence, int support )
	{
		Vector consecuent = new Vector();
		Vector ant = new Vector( antecedent.size() );
		
		for ( int i = 0; i < items.size(); i++ )
			if ( !antecedent.contains( items.elementAt( i ) ) )
				consecuent.addElement( ( (String)items.elementAt( i ) ) );
			
		for ( int i = 0; i < antecedent.size(); i++ )
			ant.addElement( (String)antecedent.elementAt( i ) );
			
		try
		{
			String rule = "\n" + ( nRules + 1 ) + ". if ( " + value( (String)
				ant.elementAt( 0 ) );
			int k = 1;
			
			while( k < ant.size() )
				rule += " && " + value( (String)ant.elementAt( k++ ) );
			
			rule += " ) then\n\t\t\t\t" + value( (String)consecuent.
					elementAt( 0 ) );
			k = 1;

			while( k < consecuent.size() )
				rule += " && " + value( (String)consecuent.elementAt( k++ ) );
			
			writer.write(  rule + " ( " + confidence + " / " + support + " )" 
					+ "\n" );
			writer.flush();
		}
		catch ( Exception e )
		{
			System.err.println( e.getMessage() );
		}
	}
	
	/** Function to extract the items contained in the string. 
	 * 
	 * @param string		The string.
	 * 
	 * @return				All the items contained in string.
	 */
	private Vector extractItems( String string )
	{
		Vector items = new Vector();
		int i = 0;
		
		while( i < string.length() )
		{
			String item = "";

			while ( i < string.length() && string.charAt( i ) != ' ' )
			{
				item = item.concat( string.substring( i, i + 1 ) );
				i++;
			}
			
			if ( item.length() > 0 )
				items.addElement( item );
			
			i++;
		}
		
		return items;
	}
	
	/** Function to sort the items. 
	 * 
	 * @param items		The items to sort.
	 * 
	 * @return			The items sorted.
	 */
	private int[] sortItems( int [] items )
	{
		boolean modify = false;
		
		do
		{
			modify = false;
			int i = 1;
			
			while ( i < items.length )
			{
				if ( items[i - 1] > items[i] && items[i - 1] != -1 )
				{
					int aux = items[i - 1];
					items[i - 1] = items[i];
					items[i] = aux;
					modify = true;
				}
				
				i++;
			}
		}
		while( modify );
		
		return items;
	}

	/** Function get the value of one attribute. 
	 * 
	 * @param valIndex		The index of the value.
	 * 
	 * @return				The value.
	 */
	private String value( String valIndex )
	{
		int k = 0;
		int index = Integer.parseInt( valIndex );
		
		for( int i = 0; i < dataset.attributes.size(); i++ )
		{
			if ( ( (Attribute)dataset.attributes.elementAt( i ) ).numValues()
					+ k >= index )
				return ( (Attribute)dataset.attributes.elementAt( i ) ).name() 
					+ " = \"" + dataset.getElement( Integer.parseInt( valIndex ) 
							- 1 ) + "\"";
			else
				k += ( (Attribute)dataset.attributes.elementAt( i ) ).
					numValues();
		}
		
		return "";
	}
	
	/** Main function.
	 * 
	 * @param args 			The parameters file.
	 * 
	 * @throws Exception 	If the algorithm cannot been executed properly.
	 */
	public static void main( String args[] )
	{
		try 
		{
  		if ( args.length != 1 )       
    		throw new Exception( "\nError: you have to specify the " +
      					"parameters file\n\tusage: java -jar C45.java " +
      					"parameterfile.txt" );

    	else
    	{
    		Apriori apriori = new Apriori( args[0] );
    	}
	  } 
  	catch (Exception e) 
		{
  		System.err.println( e.getMessage() );
	  }
	}
}