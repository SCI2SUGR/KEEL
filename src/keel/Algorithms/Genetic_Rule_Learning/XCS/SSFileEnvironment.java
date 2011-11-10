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
 * @author Written by Albert Orriols (La Salle, Ramón Llull University - Barcelona) 28/03/2004
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */


package keel.Algorithms.Genetic_Rule_Learning.XCS;
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Config;
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
	
	
  ///////////////////////////////////////
  // attributes


/**
 * <p>
 * Represents the number of the possible actions in the problem.
 * </p>
 * 
 */
    private int numActions; 


/**
 * <p>
 * Indicates if the classification has been correct.
 * </p>
 * 
 */
    private boolean isCorrect; 

/**
 * <p>
 * Represents the maximum payoff that a classifier can get.
 * </p>
 * 
 */
    private double maxPayOff; 

/**
 * <p>
 * Represents the minimum payoff that a classifier can get.
 * </p>
 * 
 */
    private double minPayOff; 


/**
 * <p>
 * It's the number of the examples of the problem.
 * </p>
 */
    private int numOfExamples;


/**
 * <p>
 * It's an array of examples
 * </p>
 * 
 */
    private double[][] example; 


/**
 * <p>
 * It's the array of the actions associated to the
 * examples. 
 * </p>
 */

    private int [] exampleAction;

/**
 * <p>
 * Represents the current example that is examinated
 * </p>
 * 
 */
    private int currentExample; 
   
/**
 * <p>
 * It indicates if the classification has been executed. In mux problem it's true
 * in every step.
 * 
 */
    private boolean classExecuted; 
    



  ///////////////////////////////////////
  // operations


/**
 * <p>
 * It's the constructor of the class.
 * </p>
 * @param fileName is the name of the file where
 * the attributes of the problem are described.
 * @param readAttrBounds is the name of the file where the 
 * examples of the execution are written.
 */
  public  SSFileEnvironment(String fileName, boolean readAttrBounds) {        
    String line;

    // The representation parameters are initalized.
    initRepresentationParameters();
    
    //Declaring a new instance.
    keel.Dataset.InstanceSet iSet = new keel.Dataset.InstanceSet();
    try{
        iSet.readSet(fileName, readAttrBounds);
        if (keel.Dataset.Attributes.getOutputNumAttributes() != 1){
            System.err.println ("The number of ouput attributes is "+keel.Dataset.Attributes.getOutputNumAttributes()+" and it has to be 1 in a classification problem.");
        }
    }catch (keel.Dataset.HeaderFormatException e){
        System.out.println ("READING DATASET ERROR. The format of the header is not correct.");
        e.printStackTrace();
        System.exit(0);
    }catch (keel.Dataset.DatasetException e){
        System.out.println ("DATASET ERROR.");
        e.printStackTrace();
    }catch(Exception e){
        System.out.println ("LOADING DATASET ERROR. ");
        e.printStackTrace();
    }

    //Extracting the instance information
    getInstancesInformation(iSet);
    
    //Extracting the attributes information
    getAttributeInformation();
    
    //Finally, we print the environment
    //if (!readAttrBounds) printInformation();
  } // end SSFileEnvironment        


  
/**
 * <p>
 * It extracts all the information from the dataset which is needed 
 * by XCS environment.
 * </p>
 * @param iSet is the instanceSet.
 */  
  private void getInstancesInformation(keel.Dataset.InstanceSet iSet){
   
    //Getting information about attributes and examples
    Config.clLength = keel.Dataset.Attributes.getInputNumAttributes();
    numOfExamples = iSet.getNumInstances();
    Config.ternaryRep = false;
    Config.relationName = keel.Dataset.Attributes.getRelationName();
    System.out.println ("The attributes number (without counting the class attribute) is: "+Config.clLength);
    System.out.println ("The examples number is: "+numOfExamples);
   
    //Initializing environment variables.
    System.out.println ("Initializing environment variables");
    initVariables(Config.clLength, numOfExamples);
        
    //Getting all the instances
    for (int i=0; i<numOfExamples; i++){
        example[i] = iSet.getInstance(i).getNormalizedInputValues();
        exampleAction[i] = (int) iSet.getInstance(i).getNormalizedOutputValues()[0];
    }
  }//end getInstancesInformation
  

  
/**
 * <p>
 * It does organize the attribute information gotten in the instance set
 * construction.
 * </p>
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
                //Config.intVector[2*i] = 0;
                //Config.intVector[2*i+1] = Config.enumConv[i].size();
                /*Config.typeOfAttributes[i] = "real";
                Config.enumConv[i] = inputAttr[i].getNominalValuesList();
                Config.attBounds[i][0] = 0;
                Config.attBounds[i][1] = Config.enumConv[i].size()-1;
                for (int k=0; k<numOfExamples; k++){
                    if (example[k][i]!=-1)
                        example[k][i] = example[k][i]/Config.attBounds[i][1];
                }*/
                break;
            case keel.Dataset.Attribute.INTEGER:
                Config.typeOfAttributes[i] = "integer";
                //Config.attBounds[i][0] = inputAttr[i].getMinAttribute();
                //Config.attBounds[i][1] = inputAttr[i].getMaxAttribute();
		Config.attBounds[i][0] = 0;
		Config.attBounds[i][1] = inputAttr[i].getMaxAttribute() - inputAttr[i].getMinAttribute();
		//Config.intVector[2*i] = 0;
                //Config.intVector[2*i+1] = (int) (Config.attBounds[i][1] - Config.attBounds[i][0]);
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
  private void initRepresentationParameters(){
    // Initializations
    classExecuted = false;
    isCorrect = false;
    currentExample = 0;
    
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

    //Initializing minimum and maximum payoff.
    maxPayOff = 1000.0;
    minPayOff = 0.0;
  }//end initRepresentationParameters
  
  
/**
 * <p>
 * Does reserve memory for all the system variables.
 * </p>
 * @param attNum is the number of attributes.
 * @param exNum is the number of examples.
 */
  private void initVariables(int attNum, int exNum){
    try{
        // Memory Reservation
        Config.typeOfAttributes = new String [attNum];
        Config.attBounds = new double [attNum][2];
        //Config.intVector = new int [2 * attNum];
        Config.enumConv = new Vector[attNum];
        
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
 * <p>
 * Does determine if the classification was good
 * </p>
 * @return a boolean that indicates if the last classifications is good.
 */
  public boolean wasCorrect() {        
    return isCorrect;
  } // end wasCorrect        

/**
 * <p>
 * This function returns the reward given at applying the action in the
 * environment.
 * </p>
 * @param action is the action chosen to do.
 * * @return a double with the reward
 */
  public double makeAction(int action) {        
    classExecuted = true;
    if (action == exampleAction[currentExample]){
        isCorrect = true;
        return maxPayOff;
    }
    else{
        isCorrect = false;
        return minPayOff;
    }
  } // end makeAction        

  
/**
 * <p>
 * The function returns the current state.
 * </p>
 * @return a double[] with the current state.
 */
  public double[] getCurrentState() {        
    return example[currentExample];
  } // end getCurrentState        


/**
 * <p>
 * Does create a new state of the problem. The XCS have to decide the
 * action to do.
 * </p>
 * @return a double[] with the new situation.
 */
  public double[] newState() {        
    currentExample = (int)(Config.rand() * (double)numOfExamples) ;
    classExecuted = false;
    return example[currentExample];
  } // end newState        


/**
 * <p>
 * Does return the environment maximum payoff
 * </p>
 * @return a double with the environment maximum payoff.
 */
  public double getMaxPayoff(){
    return maxPayOff;
  }//end getMaxPayoff
  
  
    
/**
 * <p>
 * Does return the environment minimum payoff
 * </p>
 * @return a double with the environment minimum payoff.
 */
  public double getMinPayoff(){
    return minPayOff;	
  }//end getMinPayoff


/**
 * <p>
 * Does return if the class has been executed. It is used
 * in the multiple step problems. 
 * </p>
 * @return a boolean indicating if the problem has finished.
 */ 
  public boolean isPerformed(){
    return classExecuted;	
  }//end isPerformed


/**
 * <p>
 * Does return if the class of the environmental state. It's
 * used by the UCS. 
 * </p>
 * @return a int with the class associated to the current environmental
 * state.
 */ 
  public int getEnvironmentClass(){
    return exampleAction[currentExample];	
  } //end getClass





/**
 * <p>
 * It initializes at the first example. It is used in the file 
 * environment to get the examples sequentially.
 * </p>
 */
  public void beginSequentialExamples(){
    currentExample = 0;
  }
    
/**
 * <p>
 * It returns the new Example of a single step file environment.
 * </p>
 */ 
  public double[] getSequentialState(){
    boolean found = false;
    currentExample = (currentExample +1) % numOfExamples;
    while (currentExample < example.length && !found){
            if (example[currentExample] == null) currentExample ++;
            else found = true;
    }
    if (found) return example[currentExample];

    return null;
  }//getSequentialState
  
    
/**
 * <p>
 * It return the number of the examples of the database. It's only
 * used in the file environments. 
 * </p>
 */    
  public int getNumberOfExamples(){
    return numOfExamples;
  }//end getNumberOfExamples

/**
 * <p>
 * It deletes the examples of the database that match with de 
 * classifier passed. It's only used in the file enviornment. 
 * </p>
 * @param cl is the classifier which if the examples matches with, 
 * the have to be eliminate from the set. 
 */    
  public void deleteMatchedExamples(Classifier cl){
    for (int i=0; i<example.length; i++){
        if (example[i] != null){
            if (cl.match (example[i])){
                example[i] = null;
                numOfExamples --;
            }
        }
    }
  }//end deleteMatchedExamples





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
    System.out.println ("\n == Attribute's types");
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
  }//end printInformation
   
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////   
   private void copyAttInfo(){
   	Config.enumConv = new Vector[Config.enumConv.length];
   	  	
   	Config.classConv = (Vector)Config.classConv.clone();
   	
   }

} // end SSFileEnvironment




