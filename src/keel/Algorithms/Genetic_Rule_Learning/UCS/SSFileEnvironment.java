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
 * @author Written by Albert Orriols (La Salle University Ramón Lull, Barcelona)  28/03/2004
 * @author Modified by Xavi Solé (La Salle University Ramón Lull, Barcelona) 03/12/2008
 * @version 1.2
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.UCS;

import java.lang.*;
import java.io.*;
import java.util.*;


public class SSFileEnvironment implements Environment {
/**
 * <p>
 * This is the base class for all the single step problems environments
 * that read the examples from a file. It uses the Dataset API to get all
 * the information needed from the file.
 * </p>
 */
	
	

/**
 * It's the number of the examples of the problem.
 */
    private int numOfExamples;


/**
 * It's an array of examples
 */
    private double[][] example; 


/**
 * It's the array of the actions associated to the
 * examples. 
 */

    private int [] exampleAction;

/**
 * Represents the current example that is examinated
 */
    private int currentExample; 


/**
 * It's the constructor of the class.
 * 
 * @param fileName is the database file name
 * @param readAttrInfo indicate if the attribute information has to be read. 
 */
  public  SSFileEnvironment( String fileName, boolean readAttrInfo ) {        
    String line;

    // The representation parameters are initalized.
    initRepresentationParameters( readAttrInfo );
    
    //Declaring a new instance.
    keel.Dataset.InstanceSet iSet = new keel.Dataset.InstanceSet();
    try{
        iSet.readSet( fileName, readAttrInfo );
        if (keel.Dataset.Attributes.getOutputNumAttributes() != 1){
            System.err.println ("The number of ouput attributes is "+keel.Dataset.Attributes.getOutputNumAttributes());
			System.err.println (" and it has to be 1 in a classification problem.");
			System.exit(-1);
        }
    }catch (keel.Dataset.HeaderFormatException e){
        System.out.println ("READING DATASET ERROR. The format of the header is not correct.");
        e.printStackTrace();
        System.exit(-1);
    }catch (keel.Dataset.DatasetException e){
        System.out.println ("DATASET ERROR.");
        e.printStackTrace();
        System.exit(-1);
    }catch(Exception e){
        System.out.println ("LOADING DATASET ERROR. ");
        e.printStackTrace();
        System.exit(-1);
    }

    //Extracting the instance information
    getInstancesInformation( iSet, readAttrInfo );
    
    //Extracting the attributes information
    if ( readAttrInfo ) getAttributeInformation();
    
    //Normalizing the data
    normalizeIntegerValues();
   
	//Getting the majority class in the training data set
	if ( readAttrInfo ) Config.majorityClass = getMajorityClass (); 
	System.out.println ( "    > The majority class in the training data set is: " + Config.majorityClass );

    //Finally, we print the environment
    //if ( !readAttrInfo ) printInformation();
    //printInformation();
  } // end SSFileEnvironment        


  private int getMajorityClass (){
	int i, max, majClass;
	int []number = new int [ Config.numberOfActions ];

	// Getting the number of classes
	Config.numberOfActions = 2;
	for ( i=0; i<numOfExamples; i++ ){
		if ( exampleAction[i] + 1 > Config.numberOfActions ){
			Config.numberOfActions = exampleAction[i] + 1;
		}
	}

	// Initializing
	for ( i=0;  i<Config.numberOfActions; i++ ){
		number[i] = 0;
	}

	// Counting the number of examples per class
	for ( i=0; i<numOfExamples; i++ ){
		number[ exampleAction[i] ] ++;
	}

	// Getting the majority class
	max = number[0];
	majClass = 0;
	for ( i=1; i<Config.numberOfActions; i++ ){
		if ( number[i] > max ){
			max = number[i];
			majClass = i;
		}
	}

	return majClass;
  }//end getMajorityClass

  
/**
 * It extracts all the information from the dataset which is needed 
 * by UCS environment.
 *
 * @param iSet is the instanceSet.
 */  
  private void getInstancesInformation( keel.Dataset.InstanceSet iSet, boolean readAttrInfo ){
   
    //Getting information about attributes and examples
    numOfExamples = iSet.getNumInstances();
    
	if ( readAttrInfo ) {
		Config.clLength = keel.Dataset.Attributes.getInputNumAttributes();
    	Config.ternaryRep = false;
    	Config.relationName = keel.Dataset.Attributes.getRelationName();
  	}

    //Initializing environment variables.
    System.out.println ("Initializing environment variables");
    initVariables( Config.clLength, numOfExamples, readAttrInfo );
        
    //Getting all the instances
    for (int i=0; i<numOfExamples; i++){
        example[i] = iSet.getInstance(i).getNormalizedInputValues();
        exampleAction[i] = (int) iSet.getInstance(i).getNormalizedOutputValues()[0];
    }
	
	System.out.println ("    > The attributes number (without counting the class attribute) is: "+Config.clLength);
	System.out.println ("    > The examples number is: "+numOfExamples);
	System.out.println ("    > The number of classes is: "+Config.numberOfActions);
  }//end getInstancesInformation
  

  
/**
 * It does organize the attribute information gotten in the instance set
 * construction.
 */
  private void getAttributeInformation(){
    System.out.println ("Getting input attributes information.");
    keel.Dataset.Attribute[] inputAttr = keel.Dataset.Attributes.getInputAttributes();
    //System.out.println ("Displaying input attributes information.");
    for (int i=0; i<inputAttr.length; i++){
        switch (inputAttr[i].getType()){
            case keel.Dataset.Attribute.NOMINAL:
                Config.typeOfAttributes[i] = "integer";
                Config.enumConv[i] = inputAttr[i].getNominalValuesList();
                Config.attBounds[i][0] = 0;
                Config.attBounds[i][1] = Config.enumConv[i].size() - 1;
                break;

            case keel.Dataset.Attribute.INTEGER:
                Config.typeOfAttributes[i] = "integer";
				Config.attBounds[i][0] = 0;
				Config.attBounds[i][1] = inputAttr[i].getMaxAttribute() - inputAttr[i].getMinAttribute();
                break;

            case keel.Dataset.Attribute.REAL:
                Config.typeOfAttributes[i] = "real";
                Config.attBounds[i][0] = inputAttr[i].getMinAttribute();
                Config.attBounds[i][1] = inputAttr[i].getMaxAttribute();
                break;
            default:
                System.out.println ("ERROR in attribute type definition.");
                break;
        }
    }
    
    System.out.println ("Getting output attribute information");
    keel.Dataset.Attribute outAtt = keel.Dataset.Attributes.getOutputAttributes()[0];
    //System.out.println ("Displaying output attribute information");
    switch (outAtt.getType()){
        case keel.Dataset.Attribute.NOMINAL:
            Config.classConv = outAtt.getNominalValuesList();
            Config.numberOfActions = Config.classConv.size();
			System.out.println ( "  >> Reading the number of classes in the training data set: " + Config.numberOfActions );
            break;

        case keel.Dataset.Attribute.INTEGER:
            Config.numberOfActions = (int) (outAtt.getMaxAttribute() - 
                                            outAtt.getMinAttribute() + 1);
            Config.classConv = new Vector();
            int minimumAct = (int)outAtt.getMinAttribute();
            for (int k=0; k<Config.numberOfActions; k++){
                Config.classConv.add((new Integer(minimumAct+k)).toString());
            }
            break;

        case keel.Dataset.Attribute.REAL:
            System.out.println ("ERROR: The class of the dataset has to be a nominal or an integer.");
            System.exit(0);
            break;

        default:
            System.out.println ("ERROR in output attribute type definition.");
    }
  }//end getAttributeInformation

  
/**
 * <p>
 * Does initialize the representation parameters of the enviornment
 * </p>
 */
  private void initRepresentationParameters( boolean readAttrInfo ){
    // Initializations
    currentExample = 0;
   
	if ( readAttrInfo ) { 
    	// When reading the descriptor it will be modified if there is a real 
    	// or integer attribute
    	Config.ternaryRep = false;
    
    	// The  number of actions will be updated while reading the file.
    	Config.numberOfActions = 0;

    	Config.charVector = new char[3];
    	Config.charVector[0] = '0';
    	Config.charVector[1] = '1';
    	Config.charVector[2] = '#';
    	Config.numberOfCharacters = 3;
	}

  }//end initRepresentationParameters
  
  
/**
 * Does reserve memory for all the system variables.
 * 
 * @param attNum is the number of attributes.
 * @param exNum is the number of examples.
 */
  private void initVariables( int attNum, int exNum, boolean readAttrInfo ){
    try{
        // Memory Reservation
        if ( readAttrInfo ) {
			System.out.println ( "  >> Reserving memory for " + attNum + " attributes to store their type and bounds " );
			Config.typeOfAttributes = new String [attNum];
        	Config.attBounds = new double [attNum][2];
        	Config.enumConv = new Vector[attNum];
        }

        example = new double[exNum][attNum];
        exampleAction = new int[exNum];
    }catch(Exception e){
        e.printStackTrace();	
    }
  }//Init variables

  
     
    
/**
 * <p>
 * Prints to a file the normalized data
 * </p>
 */
  private void printNormalizedData(String fileName){
    String line = null;
    String outFileName = fileName +".flt";
    	
    try{
        // The file where the normalized data will be set is opened
        PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(outFileName)));
        // The number of examples is written
        fout.println (example.length);
        for (int i=0; i<example.length; i++){
            for (int j=0; j<Config.clLength; j++){
                if (example[i][j] == -1)    fout.print ("null\t");	
                else                        fout.print (example[i][j]+" ");
            }	
            fout.println (exampleAction[i]);	
        }
        fout.close();	
    }catch (Exception e){
        e.printStackTrace();	
    }
  }//end printNormalizedData
    

  
/////////////////////////////////////////////////////////////////////////
//             Functions to interact with the environment              //
/////////////////////////////////////////////////////////////////////////
  
  
/**
 * The function returns the current state.
 * 
 * @return a double[] with the current state.
 */
  public double[] getCurrentState() {        
    return example[ currentExample ];
  } // end getCurrentState        


/**
 * Returns the class of the current example
 *
 * @return an integer with the class of the current example
 */
  public int getCurrentClass (){
	return exampleAction[ currentExample ];
  }//end getCurrentClass


/**
 * Does create a new state of the problem. The UCS have to decide the
 * action to do.
 * 
 * @return a double[] with the new situation.
 */
  public double[] newState() {        
    currentExample = (int)(Config.rand() * (double)numOfExamples) ;
    return example[currentExample];
  } // end newState        

/**
 * It initializes at the first example. It is used in the file 
 * environment to get the examples sequentially.
 */
  public void beginSequentialExamples(){
    currentExample = -1;
  }
    
/**
 * It returns the new Example of a single step file environment.
 * @return a double[] with the current example.
 */ 
  public double[] getSequentialState(){
    boolean found = false;
    currentExample = (currentExample +1) % numOfExamples;
    return example[currentExample];
  }//getSequentialState
  
    
/**
 * It return the number of the examples of the database. It's only
 * used in the file environments. 
 *
 * @return an integer with the number of examples in the dataset
 */    
  public int getNumberOfExamples(){
    return numOfExamples;
  }//end getNumberOfExamples











/**
 * normalizeData
 *
 * It normalizes the data to floats between 0 and 1
 */

  private void normalizeIntegerValues(){
  	for (int i=0; i<numOfExamples; i++){
		for (int j=0; j<Config.clLength; j++){
			if ( example[i][j] != Config.unknownValue ){
				if ( Config.typeOfAttributes[j].equals("integer") ){
					example[i][j] -=  Config.attBounds[j][0];
					example[i][j] /= ( Config.attBounds[j][1] - Config.attBounds[j][0] );
				}else if ( Config.typeOfAttributes[j].equals("real") ){
					//example[i][j] = (example[i][j] - Config.attBounds[j][0]) / (Config.attBounds[j][1] - Config.attBounds[j][0]);
				}
			}
		}
	}
  }// end normalizeData


/**
 * Does print the examples of the environment.
 */ 
  private void printExamplesOfEnvironment(){
    System.out.println ("Number of Examples: "+numOfExamples);
    for (int i=0; i<numOfExamples; i++){
        System.out.print ("Exemple "+i+": ");
        for (int j=0; j<Config.clLength; j++){	
            System.out.print ("\t"+example[i][j]);
        }
        System.out.println ("\tAction: "+exampleAction[i]);
    }	
  }//end printExamplesOfEnvironment



/**
 * Does print the environment to a file.
 */
  private void printEnvironmentToFile(PrintWriter fout){
    fout.println ("Number of Examples: "+numOfExamples);
    for (int i=0; i<numOfExamples; i++){
        fout.print ("Exemple "+i+": ");
        for (int j=0; j<Config.clLength; j++){	
                fout.print ("\t"+example[i][j]);
        }
        fout.println ("\tAction: "+exampleAction[i]);
    }
  }//end printEnvironmentToFile

  


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   private void printInformation (){
   
	System.out.println (" ============================================ "); 
	System.out.println ("\\nn\n == Attribute's types");
    for (int i=0; i<Config.typeOfAttributes.length; i++){
        System.out.println ("\t Attribute "+i+": "+Config.typeOfAttributes[i]);
    }
    System.out.println ("\n == Attribute's Boundaries");
    for (int i=0; i<Config.attBounds.length; i++){
        System.out.println ("\t Attribute "+i+": "+Config.attBounds[i][0]+","+Config.attBounds[i][1]);	
    }
    
    if (Config.enumConv != null){
        System.out.println ("\n == Enumeration information");
        System.out.println ("   > The length of the vector array is:"+Config.enumConv.length);
        for (int i=0; i<Config.enumConv.length; i++){
            if (Config.enumConv[i] != null && Config.enumConv[i].size() != 0){
                System.out.print("\t Attribute "+i+": ");
                for (int j=0; j<Config.enumConv[i].size(); j++){
                    System.out.print((String)Config.enumConv[i].elementAt(j) +" ");	
                }
            }	
        }
        System.out.println();
    }
    
    if (Config.classConv != null){
        System.out.print ("\n == Values that can be taken by the class: ");
        for (int i=0; i<Config.classConv.size(); i++){
            System.out.print ((String) Config.classConv.elementAt(i)+" ");
        }
        System.out.println();
    }
    
    System.out.println ("\n == Data readen from file: attributers + classes:");
    for (int i=0; i<example.length; i++){
        for (int j=0; j<example[i].length; j++){
            System.out.print(example[i][j]+"   ");	
        }    	
        System.out.println(" -- "+ exampleAction[i]);
    }

	System.out.println ("  > Number of classes: " + Config.numberOfActions );
  }//end printInformation
   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
   private void copyAttInfo(){
   	Config.enumConv = new Vector[Config.enumConv.length];
   	  	
   	Config.classConv = (Vector)Config.classConv.clone();
   	
   }

} // end SSFileEnvironment




