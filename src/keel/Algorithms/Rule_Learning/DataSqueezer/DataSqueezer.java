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

package keel.Algorithms.Rule_Learning.DataSqueezer;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import keel.Dataset.Attributes;



/** 
   A Java implementation of the DataSqueezer algorithm
   @author Francisco Charte Ojeda
   @version 1.0 (20-01-10)
*/
public class DataSqueezer extends Algorithm {
    /** Inner class to represent a rule
     *
     * Francisco Charte - 17-ene-2010
     */
    private class Rule {
        int classId; // Associated class
        Vector<Integer> attribute; // Attributes affected by the rule
        Vector<Integer> value;     // Values assigned to this attributes
        int weight;  // weight of this rule

        /** Constructor
         * Francisco Charte - 18-ene-2010
         *
         * @param C Class
         */
        public Rule(int C) {
            classId = C;
            attribute = new Vector<Integer>();
            value = new Vector<Integer>();
            weight = 0;
        }

        /** Add a new condition to this rule
         * Francisco Charte - 18-ene-2010
         *
         * @param att Attribute
         * @param val Value
         */
        public void addCondition(int att, int val, int w) {
            attribute.add(att);
            value.add(val);
            // Accumulate the number of itemsets described by the rule
            weight += w / modelDataset.getAttribute(att).numValues();
        }

        /** Check if an itemset meets the conditions of this rule
         * Francisco Charte - 18-ene-2010
         *
         * @param i Itemset to check
         * @return true if meets the conditions of this rule
         */
        public boolean check(Itemset i) {
            for(int index = 0; index < attribute.size(); index++) {
            	if(i.isMissing(attribute.get(index)) ||
                (int )i.getValue(attribute.get(index)) != value.get(index))
                 return false;
            }
            return true;
        }

        public boolean check2(Itemset i) {
            for(int index = 0; index < attribute.size(); index++) {
            	if(!i.isMissing(attribute.get(index)) &&
                (int )i.getValue(attribute.get(index)) != value.get(index))
                 return false;
            }
            return true;
        }

        /** Return the string representation for this rule
         * Francisco Charte - 18-ene-2010
         *
         * @return String representation
         */
        public String generateRule() {
            String rule = "IF ";

            for(int index = 0; index < attribute.size(); index++) {
                if(index > 0) rule += "AND ";

                rule += modelDataset.getAttribute(attribute.get(index)).name();
                rule += "=" + modelDataset.getAttribute(attribute.get(index)).value(value.get(index)) + " ";
            }

            rule += " THEN " + modelDataset.getClassAttribute().name() +
                    "=" + modelDataset.getClassAttribute().value(classId) + "\n";
            
            return rule;
        }

        /** Returns true if the rule has not selectors
         *
         * @return
         */
        public boolean isEmpty() {
            return attribute.size() == 0;
        }
        
        /** Return the class index
         *
         * @return Class index
         */
        public int getClassId() {
            return classId;
        }

        /** Return the weight
         *
         * @return
         */
        public int getWeight() {
            return weight;
        }

        /** Returns the number of selectors in the rule
         *
         * @return
         */
        public int numberOfSelectors() {
            return attribute.size();
        }
    }

    // Francisco Charte - 16-ene-2010
    Vector<Integer> [] classList; // Bidimensional array with indexes of data rows for every class
    Vector<Itemset> POS, NEG; // Vectors with positive and negative examples
    Vector<Itemset> Gpos, Gneg; // Vector with positive and negative examples after reduced

    /** Array containing all the rules */
    Vector<Rule> allRules;
    /** Temporal array to save rules of a class */
    Vector<Rule> rules;

    /** Number of items not classified by the algorithm */
	int notClassified;
	int testNotClassified;
	
	double pruneT;
	double generalizationT;

    /** Constructor.
	 * 
	 * @param paramFile			The parameters file.
	 * 
	 */
	public DataSqueezer(String paramFile) {
		boolean salir = false;
  		try {

        // starts the time 
         startTime = System.currentTimeMillis();

      		// Sets the options of the execution.
			StreamTokenizer tokenizer = new StreamTokenizer( new BufferedReader( new FileReader( paramFile ) ) );
    		initTokenizer( tokenizer) ;
    		setOptions( tokenizer );
    		
    		// Initializes the dataset.
    		modelDataset = new Dataset( modelFileName, true  );
    		
    		/*check if there are continous attributes*/
    		if(Attributes.hasRealAttributes() || Attributes.hasIntegerAttributes())
    		{
    			System.err.println("DataSqueezer can only handle nominal attributes." );
    			//System.exit(-1);
    			salir = true;
    		}
    		if (!salir){
	    		trainDataset = new Dataset( trainFileName, false  );    		    	
	    		testDataset = new Dataset( testFileName, false  );

                notClassified = 0;
		        testNotClassified = 0;
	       		
	    		// Executes the algorithm.

                // Francisco Charte - 16-ene-2010
                generateRules();
	    		
	    		// Prints the results generates by the algorithm.
	    		printTrain();
				printTest();
				printResult();
			}
	    } 
  		catch ( Exception e ) 
		{
            e.printStackTrace();
  			System.err.println( e.getMessage() );
  			System.exit(-1);
	    }
	}
	
 	
	/** Function to read the options from the execution file and assign the values to the parameters.
	 * 
	 * @param options 		The StreamTokenizer that reads the parameters file.
	 * 
	 * @throws Exception	If the format of the file is not correct.
	 */ 
    protected void setOptions( StreamTokenizer options ) throws Exception {
  		options.nextToken();
		
  		// Checks that the file starts with the token algorithm.
		if ( options.sval.equalsIgnoreCase( "algorithm" ) )
		{
			options.nextToken();
			options.nextToken();

			options.nextToken();
			options.nextToken();
			
			// Reads the names of the input files.
			if ( options.sval.equalsIgnoreCase( "inputData" ) )
			{
				options.nextToken();
				options.nextToken();
				modelFileName = options.sval;
					
				if ( options.nextToken() != StreamTokenizer.TT_EOL )
				{
					trainFileName = options.sval;
					options.nextToken();
					testFileName = options.sval;					
					if( options.nextToken() != StreamTokenizer.TT_EOL )
					{
					  trainFileName = modelFileName;	
					  options.nextToken();
					}										
				}																
				
			}
			else
				throw new Exception( "The file must start with the word inputData." );
				
			while ( true )
			{
				if( options.nextToken() == StreamTokenizer.TT_EOF )
					throw new Exception( "No output file provided." );
			
				if ( options.sval == null )
					continue;
				else if ( options.sval.equalsIgnoreCase( "outputData" ) )
					break;
			}

			/* Reads the names of the output files*/
			
			
				options.nextToken();
				options.nextToken();
				trainOutputFileName = options.sval;
				options.nextToken();
				
				testOutputFileName = options.sval;
				options.nextToken();
				
				resultFileName = options.sval;

				
				options.nextToken();
				options.nextToken();
				options.nextToken();
				options.nextToken();
				options.nextToken();
				options.nextToken();
				pruneT = Double.parseDouble(options.sval);
				options.nextToken();
				options.nextToken();
				options.nextToken();
				options.nextToken();
				options.nextToken();
				generalizationT = Double.parseDouble(options.sval);

		}
		else
			throw new Exception( "The file must start with the word algorithm followed of the name of the algorithm." );
	}

    /** Run the DataSqueezer algorithm
     *
     * Francisco Charte - 16-ene-2010
     */
    public void generateRules() {
        // Separate the rows of the dataset by class
        generateClassLists();

        // Prepare de rules array
        allRules = new Vector<Rule>();

        // For every class in the dataset
        for(int index = 0; index < modelDataset.numClasses(); index++) {
            // Obtain the POS and NEG tables assuming the index class as positive
            obtainPosNegTables(index);

            if (POS.size() > 0) {
            	// Process POS and NEG tables
            	dataSqueezer(index);

            	// Save the rules that classify the current class
            	saveRules();
            }
        }
    }

    /** Group the rows of the dataset by class
     *
     * Francisco Charte - 16-ene-2010
     */
    private void generateClassLists() {
        // Adjust the dimensions of the array of class list
        classList = (Vector<Integer>[] ) Array.newInstance(Vector.class, modelDataset.numClasses());
        // and create the empty list for every class
        for(int index = 0; index < modelDataset.numClasses(); index++) {
            classList[index] = new Vector<Integer>();
        }

        // Iterate over the training dataset rows
        for(int index = 0; index < modelDataset.numItemsets(); index++) {
            // For every sample
            Itemset sample = modelDataset.itemset(index);
            // add his index to the list of their class
            classList[(int ) sample.getClassValue()].add(index);
        }
    }
    
    /** Generate de POS and NEG tables for class 'index'
     * Francisco Charte - 16-ene-2010
     * 
     * @param index Index of the positive class
     */
    private void obtainPosNegTables(int classIndex) {
        // Recreate the tables for the new partition
        POS = new Vector<Itemset>();
        NEG = new Vector<Itemset>();

        // The samples of index class to the POS table
        for(int index = 0; index < classList[classIndex].size(); index++) {
            POS.add((Itemset)modelDataset.itemset(classList[classIndex].get(index)).copy());
        }

        // Every other sample to NEG table
        for(int classI = 0; classI < classList.length; classI++) {
            // If this is the index of positive class
            if(classI == classIndex)
                continue; // step to the next one

            // Add all the samples of this class to NEG table
            for(int index = 0; index < classList[classI].size(); index++) {
                NEG.add((Itemset )modelDataset.itemset(classList[classI].get(index)).copy());
            }
        }
    }
    
    /** Process the data stored in POS and NEG tables generating rules
     * 
     * Francisco Charte - 17/18-ene-2010
     */
    private void dataSqueezer(int C) {
        int k = modelDataset.numAttributes();

        // Generalize de data tables
        Gpos = dataReduce(POS, k);
        Gneg = dataReduce(NEG, k);

        // Initialize the rule list
        rules = new Vector<Rule>();
        int i = 0; // Rule index

        boolean notChange;
        long pruneNumber = Math.round(pruneT*POS.size());

        do {
            // Generate de list of columns in POS
            Vector<Integer> LIST = new Vector<Integer>();
            for(int col = 0; col < modelDataset.numAttributes(); col++) {
                if(col != modelDataset.getClassIndex())
                    LIST.add(col);
            }

            // Add new empty rule
            rules.add(new Rule(C));

            notChange = true; // Gpos not changed
            boolean prune = false;

            do {
                int maxWeight = 0, maxJ = -1, maxA = -1;
                int Saj;

                // Within every column of Gpos that is on LIST
                for(int j = 0; j < LIST.size(); j++) {
                    // For every non missing value from this column
                    for(int a = 0; a < Gpos.size(); a++) {
                        Saj = 0;

                        if(Gpos.get(a).isMissing(LIST.get(j)))
                            continue;

                        // Reference value
                        double value = Gpos.get(a).getValue(LIST.get(j));

                        // Sum every row with this value
                        for(int index = 0; index < Gpos.size(); index++)
                            if(Gpos.get(index).getValue(LIST.get(j)) == value)
                                Saj += (int ) Gpos.get(index).getValue(k);

                        // Scales by the number of valid values for this attribute
                        Saj *= Gpos.get(a).getAttribute(LIST.get(j)).numValues();
                        
                        // Keep the indexes of max weight
                        if(Saj > maxWeight) {
                            maxA = a;
                            maxJ = j;
                            maxWeight = Saj;
                        }
                    } // for a

                    // All the values in Gpos for attribute j are * (missing)
                    if(maxA == -1) {
                        LIST.remove(j);
                        break;
                    }
                } // for j

                // Add "j = a" selector to rules[i]
                if(maxA != -1 && maxJ != -1)  {
                	if ((maxWeight / Gpos.get(maxA).getAttribute(LIST.get(maxJ)).numValues()) > pruneNumber) {
                		rules.get(i).addCondition(
                				LIST.get(maxJ),
                				(int )Gpos.get(maxA).getValue(LIST.get(maxJ)), maxWeight);

                		// Remove j from LIST
                		LIST.remove(maxJ);
                	}
                	else {
                		prune = true;
                	}
                }

            } while(rulesDescribeNeg(i,k) && !LIST.isEmpty() && !prune);

            Rule l = rules.get(i); // Current rule
            if(!l.isEmpty()) {
                // Remove all rows described by rules[i] from Gpos
                Enumeration e = Gpos.elements();
                while(e.hasMoreElements()) {
                    Itemset r = (Itemset )e.nextElement();

                    if(l.check2(r)) {
                        Gpos.remove(r);
                        e = Gpos.elements(); // Forzar reexploraciÃ³n *** Quebradero de cabeza
                        notChange = false; // Gpos has changed
                    }
                }
                i++; // Index of new rule
            } else {
                rules.remove(i); // Remove empty rule
            }
        } while(!Gpos.isEmpty() && !notChange);

        // rules contain the rules to classify class C
        return;

    } // dataSqueezer

    /** Check if the rule ith describe any row from Gneg
     * Francisco Charte - 18-ene-2010
     *
     * @param i Index of current rule in rules vector
     * @return true or false
     */
    private boolean rulesDescribeNeg(int i, int k) {
    	
    	int cont = 0;
    	
        for(int index = 0; index < Gneg.size(); index++)
            if(rules.get(i).check(Gneg.get(index))) {
                cont += Gneg.get(index).getValue(k);
            }
        
        if (cont > Math.round(generalizationT*POS.size())) {
        	return true;
        } else {
            return false;        	
        }        
    }

    /** Apply generalization to the data tables as described by Kurgan
     * Francisco Charte - 17-ene-2010
     *
     * @param D D=POS or D=NEG
     * @param k Number of attributes
     * @return Gpos or Gneg
     */
    private Vector<Itemset> dataReduce(Vector<Itemset> D, int k) {
        Vector<Itemset> G = new Vector<Itemset>(); // G = []

        // Init parameters
        int i = 0;
        Itemset tmp = (Itemset )D.get(0).copy();
        G.add((Itemset )D.get(0).copy());
        G.get(0).setValue(k, 1);

        for(int j = 1; j < D.size(); j++) {
            for(int kk = 0; kk < k; kk++) {
                if(kk == modelDataset.getClassIndex()) continue;

                // process missing 'do not care' values
                if(D.get(j).getValue(kk) != tmp.getValue(kk) || D.get(j).isMissing(kk)) {
                    tmp.setMissing(kk);
                }
            } // for kk

            if(numberOfNonMissingValues(tmp, k) >= 2) {
                for(int index = 0; index < k; index++)
                    G.get(i).setValue(index, tmp.getValue(index));
                
                G.get(i).setValue(k, G.get(i).getValue(k) + 1);
            } else {
                i++;
                tmp = (Itemset )D.get(j).copy();
                G.add((Itemset )D.get(j).copy());
                G.get(i).setValue(k, 1);
            }
        } // for j

        return G;
    }

    /** Accumulate the rules that classify every class
     * Francisco Charte - 17-ene-2010
     *
     */
    private void saveRules() {
        for(int index = 0; index < rules.size(); index++)
            allRules.add(rules.get(index));
    }

    /** Return the number of non missing values in a Itemset
     * Francisco Charte - 17-ene-2010
     * 
     * @param t Itemset
     * @param k Number of attributes
     * @return Number of non missing values
     */
    private int numberOfNonMissingValues(Itemset t, int k) {
        int count = 0;
        for(int index = 0; index < k; index++) {
            if(index == modelDataset.getClassIndex()) continue;
            if(!t.isMissing(index))
                count++;
        }

        return count;
    }

 	/** Function to write the list of rules.
     * Francisco Charte - 19-ene-2010
 	 * 
 	 * @return			String with the list of rules
 	 */
	public String writeRules()  {
        String ruleList = "";

        for(int index = 0; index < allRules.size(); index++)
            ruleList += allRules.get(index).generateRule();

        return ruleList;
	}
	

	/** Function to evaluate the class which the itemset must have according to the classification of the rules.
     * Francisco Charte - 19-ene-2010
	 * 
	 * @param i		The itemset to evaluate.
	 * 
	 * @return				The index of the class index predicted or -1 if it's not described by any rule.
	 */
	public int evaluateItemset(Itemset i)  {
		int classId = -1, weight = 0;

        for(int index = 0; index < allRules.size(); index++)
            if(allRules.get(index).check(i) && allRules.get(index).getWeight() > weight) {
                classId = allRules.get(index).getClassId();
                weight = allRules.get(index).getWeight();
            }
        
        return classId;
	}
    
    /** Writes the rules and the results of the training and the test in the file.
     * Francisco Charte - 19-ene-2010
     * 
     * @exception 	If the file cannot be written.
     */
  	public void printResult() throws IOException 
  	{
	  	long totalTime = ( System.currentTimeMillis() - startTime ) / 1000;
	  	long seconds = totalTime % 60;
	  	long minutes = ( ( totalTime - seconds ) % 3600 ) / 60;
  		String result = "";
		PrintWriter resultPrint;

        double numberOfRules = allRules.size(), numberOfSelectors = 0;
        for(int index = 0; index < allRules.size(); index++)
            numberOfSelectors += allRules.get(index).numberOfSelectors();


   		result += writeRules();

        result += "\n\n@NumberOfRules " + numberOfRules;
        result += "\n@TotalNumberOfSelectors " + numberOfSelectors;
        result += "\n@MeanNumberOfSelectorsPerRule " + numberOfSelectors / numberOfRules;
   		   		       			  		
   		result += "\n\n@NumberOfItemsetsTraining " + trainDataset.numItemsets();
  		result += "\n@NumberOfCorrectlyClassifiedTraining " + correct;
  		result += "\n@PercentageOfCorrectlyClassifiedTraining " + (float)(correct*100.0)/(float)trainDataset.numItemsets() + "%" ;
        result += "\n@NumberOfItemsNotClassifiedTraining " + notClassified;
  		result += "\n@PercentageOfItemsNotClassifiedTraining " + (float)(notClassified*100.0)/(float)trainDataset.numItemsets() + "%" ;
  		result += "\n@NumberOfInCorrectlyClassifiedTraining " + (trainDataset.numItemsets()-correct-notClassified);
  		result += "\n@PercentageOfInCorrectlyClassifiedTraining " + (float)((trainDataset.numItemsets()-correct-notClassified)*100.0)/(float)trainDataset.numItemsets() + "%" ;
  		
  	    result += "\n\n@NumberOfItemsetsTest " + testDataset.numItemsets();
	  	result += "\n@NumberOfCorrectlyClassifiedTest " + testCorrect;
	  	result += "\n@PercentageOfCorrectlyClassifiedTest " + (float)(testCorrect*100.0)/(float)testDataset.numItemsets() + "%" ;
        result += "\n@ItemsNotClassifiedTest " + testNotClassified;
	  	result += "\n@PercentageOfItemsNotClassifiedTest " + (float)(testNotClassified*100.0)/(float)testDataset.numItemsets() + "%" ;
	  	result += "\n@NumberOfInCorrectlyClassifiedTest " + (testDataset.numItemsets()-testCorrect-testNotClassified);
	  	result += "\n@PercentageOfInCorrectlyClassifiedTest " + (float)((testDataset.numItemsets()-testCorrect-testNotClassified)*100.0)/(float)testDataset.numItemsets() + "%" ;
	  		  	
	  	result += "\n\n@ElapsedTime " + ( totalTime - minutes * 60 - seconds ) / 3600 + ":" + minutes / 60 + ":" + seconds;

        result += "\n\n" +
                  numberOfRules + "," +
                  numberOfSelectors + "," +
                  numberOfSelectors / numberOfRules + "," +
                  trainDataset.numItemsets() + "," +
                  correct + "," +
                  (float)(correct*100.0)/(float)trainDataset.numItemsets() + "," +
                  notClassified + "," +
                  (float)(notClassified*100.0)/(float)trainDataset.numItemsets() + "," +
                  (trainDataset.numItemsets()-correct-notClassified) + "," +
                  (float)((trainDataset.numItemsets()-correct-notClassified)*100.0)/(float)trainDataset.numItemsets()  + "," +
                  testDataset.numItemsets() + "," +
                  testCorrect + "," +
                  (float)(testCorrect*100.0)/(float)testDataset.numItemsets() + "," +
                  testNotClassified + "," +
                  (float)(testNotClassified*100.0)/(float)testDataset.numItemsets() + "," +
                  (testDataset.numItemsets()-testCorrect-testNotClassified) + "," +
                  (float)((testDataset.numItemsets()-testCorrect-testNotClassified)*100.0)/(float)testDataset.numItemsets() + "\n";

		resultPrint = new PrintWriter( new FileWriter ( resultFileName ) );
		resultPrint.print( getHeader() + "\n@rule list\n\n" + result );
		resultPrint.close();
  	}
    
    /** Evaluates the training dataset and writes the results in the file.
     * Francisco Charte - 19-ene-2010
     * 
     */
	public void printTrain()
	{
		String text = getHeader();
		for ( int i = 0; i < trainDataset.numItemsets(); i++ )
		{
			try
			{
				Itemset itemset = trainDataset.itemset( i );				
				int cl = evaluateItemset(itemset);

                if(cl == -1)
                    notClassified++;
                
				if ( cl == (int) itemset.getValue( trainDataset.getClassIndex() ) ) 
					correct++;

				text += 
					trainDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + " " +
					(cl == -1 ? "not classified" : trainDataset.getClassAttribute().value( cl )) + "\n";
			}
			catch ( Exception e )
			{
                e.printStackTrace();
				System.err.println( e.getMessage() );
			}
		}
		
   		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( trainOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file: " + e.getMessage() );
   		}
	}
	
    /** Evaluates the test dataset and writes the results in the file.
     * Francisco Charte - 19-ene-2010
     * 
     */
	public void printTest()
	{
		String text = getHeader();
		
		for ( int i = 0; i < testDataset.numItemsets(); i++)
		{
			try
			{
				int cl = (int) evaluateItemset( testDataset.itemset( i ));
				Itemset itemset = testDataset.itemset( i );
                if(cl == -1)
                    testNotClassified++;
			
				if ( cl == (int) itemset.getValue( testDataset.getClassIndex() ) ) 
					testCorrect++;
			
				text += testDataset.getClassAttribute().value( ( (int) itemset.getClassValue()) ) + " " + 
					(cl == -1 ? "not classified" : testDataset.getClassAttribute().value( cl ))+ "\n";
			}
			catch ( Exception e )
			{
                e.printStackTrace();
				System.err.println( e.getMessage());
			}
		}
		
		try 
		{
   			PrintWriter print = new PrintWriter( new FileWriter ( testOutputFileName ) );
   			print.print( text );
   			print.close();
		}
   		catch ( IOException e )
		{
   			System.err.println( "Can not open the training output file." );
   		}
	}
  
	/** Main function.
     * Francisco Charte - 16-ene-2010
	 * 
	 * @param args 			The parameters file.
	 */
   	public static void main(String[] args) {
		if ( args.length != 1){
  			System.err.println("\nError: you have to specify the parameters file\n\tusage: java -jar DataSqueezer.jar parameterfile.txt" );
  			 System.exit(-1);
  		}
      	else{
			new DataSqueezer( args[0] );
		}
   	}
   	
}
