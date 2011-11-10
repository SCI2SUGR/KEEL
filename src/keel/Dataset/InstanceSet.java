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

package keel.Dataset;

import java.util.*;
import java.io.*;


/**
 * <p>
 * <b> InstanceSet </b>
 * </p>
 *
 * The instance set class mantains a pool of instances read from the keel
 * formated data file. It provides a set of methods that permit to get
 * each instance, get the whole set of instances, get the number of instances,
 * etc.
 *
 * @author Albert Orriols Puig
 * @version keel0.1
 * @see Instance
 * @see Attributes
 */


public class InstanceSet {
	
/////////////////////////////////////////////////////////////////////////////
//////////////// ATTRIBUTES OF THE INSTANCESET CLASS ////////////////////////
/////////////////////////////////////////////////////////////////////////////


/**
 * Attribute where all the instances of the DB are stored.
 */
  private Instance[] instanceSet;
  
/**
 * String where the header of the file is stored.
 */
  private String header;

/**
 * String where only the attributes definition header is stored
 */
  private String attHeader;

/**
 * Object that collects all the errors happened while reading the test and
 * train datasets.
 */
  static FormatErrorKeeper errorLogger = new FormatErrorKeeper();


/**
 * This object contains the attributes definitions
 */
  private InstanceAttributes attributes;  

/**
 * It indicates if the attributes has not be stored as non-static, permiting
 * the load of different datasets
 */
  private boolean storeAttributesAsNonStatic;

  /**
   * It indicates that the output attribute has been infered as the last one
   */
  private boolean outputInfered;
/////////////////////////////////////////////////////////////////////////////
///////////////// METHODS OF THE INSTANCESET CLASS //////////////////////////
/////////////////////////////////////////////////////////////////////////////
  
/**
 * It instances a new instance of InstanceSet
 */
  public InstanceSet(){
	storeAttributesAsNonStatic = false;
	attributes = null;
  }//end InstanceSet
  

/**
 * InstanceSet
 *
 * This constructor permit define if the attribute's definition need to be
 * stored as non-static (nonStaticAttributes = true). Otherwise, if 
 * nonStaticAttributes = false, using this constructor is equivalent to use
 * the constructor by default.
 */

  public InstanceSet (boolean nonStaticAttributes ){
	storeAttributesAsNonStatic = nonStaticAttributes;
	//if ( storeAttributesAsNonStatic ) Attributes.clearAll();
	attributes = null;
  }//end InstanceSet  
  
  /**
   * Creates a new InstanceSet with the header and Instances from the passed object
   * It performs a deep (new allocated) copy.
   * @param is Original InstanceSet
   */
  public InstanceSet(InstanceSet is){
	  this.instanceSet = Arrays.copyOf(is.instanceSet, is.instanceSet.length);
	  
	  this.header = new String(is.header);
	  this.attHeader = new String(is.attHeader);
	  this.attributes = new InstanceAttributes(is.attributes);
	  this.storeAttributesAsNonStatic = is.storeAttributesAsNonStatic;
  }


/**
 * setAttributesAsNonStatic
 *
 * It stores the static-defined attributes in the class Attributes as
 * non static in the object attributes. After this it does not remove the
 * static-definition of the Attributes; this is in that way to permit to 
 * call this functions for differents datasets from the same problem, such
 * as, a train dataset and the correspondent test dataset.
 */
  public void setAttributesAsNonStatic (){
	attributes = new InstanceAttributes();
	attributes.copyStaticAttributes();
  }//end setAttributesAsNonStatic

/**
 * getAttributeDefinitions
 *
 * It does return the definition of the attibutes contained in the dataset.
 * 
 * @return InstanceAttributes contains the attribute's definitions.
 */

  public InstanceAttributes getAttributeDefinitions (){
	return attributes;
  }//end InstanceAttributes
 
/** 
 * This method reads all the information in a DB and load it to memory.
 * @param fileName is the database file name. 
 * @param isTrain is a flag that indicate if the database is for a train or for a test.
 * @throws DatasetException if there is any semantical error in the input file.
 * @throws HeaderFormatException if there is any lexical or sintactical error in the 
 * header of the input file
 */
  public void readSet( String fileName,boolean isTrain ) throws DatasetException, HeaderFormatException{
    String line;

    System.out.println ("Opening the file: "+fileName+".");
    //Parsing the header of the DB.
    errorLogger = new FormatErrorKeeper();
    
    //Declaring an instance parser
    InstanceParser parser = new InstanceParser( fileName, isTrain );
    
    // Reading information in the header, i.e., @relation, @attribute, @inputs and @outputs
    parseHeader ( parser, isTrain );
    
    System.out.println ( " The number of output attributes is: " + Attributes.getOutputNumAttributes() );
    
    //The attributes statistics are init if we are in train mode.
    if (isTrain && Attributes.getOutputNumAttributes() == 1){
        Attributes.initStatistics();
    }
    
    //A temporal vector is used to store the instances read.
    
    System.out.println ( "\n\n  > Reading the data ");
    Vector tempSet=new Vector(1000,100000);
    while((line=parser.getLine())!=null) {
        //System.out.println ("    > Data line: " + line );
        tempSet.addElement( new Instance( line, isTrain, tempSet.size()) );
    }
   
    //The vector of instances is converted to an array of instances.
    int sizeInstance=tempSet.size();
    System.out.println ("    > Number of instances read: "+tempSet.size());
    instanceSet=new Instance[sizeInstance];
    for (int i=0; i<sizeInstance; i++) {
        instanceSet[i]=(Instance)tempSet.elementAt(i);
    }
	//System.out.println("After converting all instances");
  
	//System.out.println("The error logger has any error: "+errorLogger.getNumErrors()); 
    if (errorLogger.getNumErrors() > 0){
        
		System.out.println ("There has been "+errorLogger.getAllErrors().size()+
                                    " errors in the Dataset format.");
		for (int k=0;k<errorLogger.getNumErrors();k++){
			errorLogger.getError(k).print();
		}
		throw new DatasetException("There has been "+errorLogger.getAllErrors().size()+
                                    " errors in the Dataset format", errorLogger.getAllErrors());
    }
   
	System.out.println ("\n  > Finishing the statistics: (isTrain)"+isTrain+", (# out attributes)"+Attributes.getOutputNumAttributes());
    //If being on a train dataset, the statistics are finished
    if (isTrain && Attributes.getOutputNumAttributes() == 1){ 
        Attributes.finishStatistics();
    }
    
    //close the stream
    parser.close();
    
    System.out.println ("  >> File LOADED CORRECTLY!!");
  }//end of InstanceSet constructor.

  
  
/**
 * It reads the information in the header of the file. 
 * It reads relation's name, attributes' names, and inputs and outputs.
 *
 * @param parser is the parser of the data set
 * @param isTrain is a boolean indicating if this is a train set (and so
 * parameters information must be read) or a test set (parameters information 
 * has not to be read).
 */
  
  public void parseHeader ( InstanceParser parser, boolean isTrain ){

    // 1. Declaration of variables
    Vector inputAttrNames = new Vector();
    Vector outputAttrNames = new Vector();
    
    boolean inputsDef = false;
    boolean outputsDef = false;
    
    String line, aux;
    header = "";
    
    int attCount = 0, lineCount = 0;
    
    attHeader = null;
    
    
    while (  !(line = parser.getLine().trim()).equalsIgnoreCase("@data")  ){ 
        line = line.trim();
        //System.out.println ("  > Line read: " + line +"." );
        lineCount ++;
        if ( line.toLowerCase().indexOf("@relation") != -1 ){
            if ( isTrain )  Attributes.setRelationName ( line.replaceAll("@relation","") );
        }
        
        if ( line.toLowerCase().indexOf("@attribute") != -1 ){
            if (isTrain) insertAttribute ( line );
            attCount ++;
        }
        
        if ( line.toLowerCase().indexOf("@inputs") != -1 ){
            attHeader = header;            
            inputsDef = true;
            
            aux = line.substring( 8 );
            
            if ( isTrain ) insertInputOutput ( aux, lineCount, inputAttrNames, "inputs", isTrain );
         
        }
        
        if ( line.toLowerCase().indexOf("@outputs") != -1 ){
            if ( attHeader == null ) attHeader = header;
            outputsDef = true;
            //System.out.println ( " >>> Defining the output !!!");
            
            aux = line.substring( 8 );
            if ( isTrain ) insertInputOutput ( aux, lineCount, outputAttrNames, "outputs", isTrain );
            
            System.out.println (" >> Size of the output is: "+ outputAttrNames.size() );
        }
        header += line + "\n";
        
    }
    if ( attHeader == null ) attHeader = header;    
    
    processInputsAndOutputs ( isTrain, inputsDef, outputsDef, outputAttrNames, inputAttrNames );
 
  }//end headerParse
  
  
  
 
  void insertAttribute ( String line ){
      
      int indexL, indexR;
      String type;
      
      //Treating string and declaring a string tokenizer
      line.replace ("{"," {");
      //line.replace ("["," [");
      
      //System.out.println ("  > Processing line: "+  line );
      StringTokenizer st = new StringTokenizer ( line, " [{\t" );
     
      //Disregarding the first token. It is @attribute
      st.nextToken();
      
      Attribute at = new Attribute ();
      at.setName ( st.nextToken().trim() );
	  //System.out.println ( "   > Attribute name: "+ at.getName() );
      
      //Next action depends on the type of attribute: continuous or nominal
      if ( !st.hasMoreTokens() ) { // Parsing a nominal attribute with no definition of values
            //System.out.println ("    > Parsing nominal attribute without values ");
            at.setType( Attribute.NOMINAL );
      }
      else if ( line.indexOf("{") != -1 ) { // Parsing a nominal attribute
            //System.out.println ("    > Parsing nominal attribute with values: "+line );
            at.setType( Attribute.NOMINAL );
            at.setFixedBounds ( true );
            
            indexL = line.indexOf ("{");
            indexR = line.indexOf ("}");
            
            //System.out.println ( "      > The Nominal values are: " + line.substring( indexL+1, indexR) );
            StringTokenizer st2 = new StringTokenizer ( line.substring( indexL+1, indexR ), "," );
            
            while ( st2.hasMoreTokens() ){
                at.addNominalValue ( st2.nextToken().trim() );
            }
      }
      else { //Parsing an integer or real
            type = st.nextToken().trim();
            
            //System.out.println ("    > Parsing "+ type + " attributes");
            if ( type.equalsIgnoreCase("integer") ) at.setType( Attribute.INTEGER );
            if ( type.equalsIgnoreCase("real") )    at.setType( Attribute.REAL );
            
            indexL = line.indexOf ("[");
            indexR = line.indexOf ("]");
            
            if ( indexL != -1 && indexR != - 1 ){
                //System.out.println ( "      > The real values are: " + line.substring( indexL+1, indexR) );
                StringTokenizer st2 = new StringTokenizer ( line.substring( indexL+1, indexR ), "," );
                
                double min = Double.parseDouble ( st2.nextToken().trim() );
                double max = Double.parseDouble ( st2.nextToken().trim() );
                
                at.setBounds ( min, max );
            }
            
      }
      
    Attributes.addAttribute ( at );
      
  }//end insertAttribute
  
  
   void insertInputOutput ( String line, int lineCount, Vector collection, String type, boolean isTrain ){
    String attName;
    
    System.out.println( " >> processing: " + line );
    
    //Declaring StringTokenizer
    StringTokenizer st = new StringTokenizer ( line, "," );
            
    while ( st.hasMoreTokens() ) {
        attName = st.nextToken().trim();
                
        if ( Attributes.getAttribute ( attName ) == null ) {
            // If this attribute has not been declared, generate error
            ErrorInfo er = new ErrorInfo( ErrorInfo.InputTestAttributeNotDefined, 0, lineCount, 0, 0, isTrain,
                            ( "The attribute " + attName + " defined in @" + type + 
                             " in test, it has not been defined in @inputs in its train dataset. It will be ignored"));
            InstanceSet.errorLogger.setError(er);
        }
        else {
            System.out.println ("   > " + type + " attribute considered: " + attName + "." );
            collection.add ( attName );
        }
    }
    
  }//end insertInputOutput
  
  
  
  void processInputsAndOutputs( boolean isTrain, boolean inputsDef, boolean outputsDef,
                                    Vector outputAttrNames, Vector inputAttrNames ){
  //Afteer parsing the header, the inputs and the outputs are prepared.
    System.out.println (" >> Processing inputs and outputs");
    outputInfered=false;
	if ( isTrain ){
        if (!inputsDef && !outputsDef){
            outputAttrNames.add( Attributes.getAttribute( Attributes.getNumAttributes()-1).getName() );
            inputAttrNames = Attributes.getAttributesExcept(outputAttrNames);
            outputInfered=true;
        }else if (!inputsDef && outputsDef){
            inputAttrNames = Attributes.getAttributesExcept(outputAttrNames);
        }else if (inputsDef && !outputsDef){
            outputAttrNames = Attributes.getAttributesExcept(inputAttrNames);
            outputInfered=true;
        }

        Attributes.setOutputInputAttributes(inputAttrNames, outputAttrNames);
    }
  }//end of processInputsAndOutputs

/**
 * Test if the output attribute has been infered.
 * @return True if the output attribute has been infered. False if not.
 */
  public boolean isOutputInfered(){
        return outputInfered;
  }
  
/**
 * It returns the number of instances.
 * @return an int with the number of instances.
 */
  public int getNumInstances() {
	  if(instanceSet!=null)
		  return instanceSet.length;
	  else
		  return 0;
  }//end numInstances

  
/**
 * Gets the instance located at the cursor position.
 * @return the instance located at the cursor position.
 */
  public Instance getInstance(int whichInstance) {
    if (whichInstance <0 || whichInstance>= instanceSet.length) return null;
    return instanceSet[whichInstance];
  }//end getInstance


/**
 * It returns all the instances of the class.
 * @return Instance[] with all the instances of the class.
 */
  public Instance[] getInstances() {
    return instanceSet;
  }//end getInstances

/**
 * Returns the value of an integer or a real input attribute of an instance
 * in the instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the input attribute.
 * @return a String with the numeric value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public double getInputNumericValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getInputRealValues(whichAttr);
  }//end getInputNumericValue

  
/**
 * Returns the value of an integer or a real output attribute of an instance
 * in the instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the output attribute.
 * @return a String with the numeric value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public double getOutputNumericValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getOutputRealValues(whichAttr);
  }//end getOutputNumericValue

  
/**
 * Returns the value of a nominal input attribute of an instance in the 
 * instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the input attribute.
 * @return a String with the nominal value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public String getInputNominalValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getInputNominalValues(whichAttr);
  }//end getInputNominalValue
  
  
  
/**
 * Returns the value of a nominal output attribute of an instance in the 
 * instanceSet.
 * @param whichInst is the position of the instance.
 * @param whichAttr is the position of the output attribute.
 * @return a String with the nominal value.
 * @throws ArrayIndexOutOfBoundsException If the index is out of the instance
 * set size.
 */
  public String getOutputNominalValue(int whichInst, int whichAttr) throws ArrayIndexOutOfBoundsException{
    if (whichInst<0 || whichInst>= instanceSet.length) 
        throw new ArrayIndexOutOfBoundsException("You are trying to access to "+whichInst+" instance and there are only "+instanceSet.length+"."); 
    return instanceSet[whichInst].getOutputNominalValues(whichAttr);
  }//end getOutputNumericValue
  
  
  
/**
 * It does remove the instance i from the instanceSet.
 * @param instNum is the instance removed from the instanceSet.
 */
  public void removeInstance(int instNum){
    if (instNum<0 || instNum>=instanceSet.length) return;
    Instance[] aux = new Instance[instanceSet.length - 1];
    int add = 0;
    for (int i=0; i<instanceSet.length; i++){
        if (instNum == i) add=1;
        else{
            aux[i-add] = instanceSet[i];
        }
    }
    //Copying the auxiliar to the instanceSet variable
    instanceSet = aux;
    aux = null; //avoiding memory leaks (not necessary in this case)
  }//end removeInstance
 

/**
 * It does remove an attribute. To remove an attribute, the train and the
 * test sets have to be passed to mantain the coherence of the system. 
 * Otherwise, only the attribute of the train set would be removed, leaving
 * inconsistent the instances of the test set, because of having one extra
 * attribute inexistent anymore.
 *
 * @param tSet is the test set. 
 * @param inputAtt is a boolean that is true when the attribute that is 
 * wanted to be removed is an input attribute.
 * @param whichAtt is a integer that indicate the position of the attriubte
 * to be deleted.
 * @return a boolean indicating if the attribute has been deleted
 */
  public boolean removeAttribute(InstanceSet tSet, boolean inputAtt, int whichAtt){
    Attribute attToDel=null;
    //Getting a reference to the attribute to del
    if (inputAtt){
        if ( storeAttributesAsNonStatic && attributes != null )
			attToDel = (Attribute)attributes.getInputAttribute(whichAtt);
		else
			attToDel = (Attribute)Attributes.getInputAttribute(whichAtt);
    }
	else{
        if ( storeAttributesAsNonStatic && attributes != null )
			attToDel = (Attribute)attributes.getOutputAttribute(whichAtt);
		else
        	attToDel = (Attribute)Attributes.getOutputAttribute(whichAtt);
	}
    
	if ( storeAttributesAsNonStatic && attributes != null ){
		System.out.println ("Removing the attribute");
    	if (!attributes.removeAttribute(inputAtt,whichAtt) || ! tSet.attributes.removeAttribute(inputAtt,whichAtt) ) return false;
	}
	else{
    	if (!Attributes.removeAttribute(inputAtt,whichAtt)) return false;
	}
		
    
    for (int i=0; i<instanceSet.length; i++){
		if ( storeAttributesAsNonStatic && attributes != null ){
        	instanceSet[i].removeAttribute(attributes, attToDel, inputAtt, whichAtt);
		}
		else{
        	instanceSet[i].removeAttribute(attToDel, inputAtt, whichAtt);
		}
    }
    
    
    if (tSet != null) for (int i=0; i<tSet.instanceSet.length; i++){
		if ( storeAttributesAsNonStatic && attributes != null )
        	tSet.instanceSet[i].removeAttribute(attributes,attToDel, inputAtt, whichAtt);
		else	
        	tSet.instanceSet[i].removeAttribute(attToDel, inputAtt, whichAtt);
    }
    return true;
  }//end removeAttribute
  
  
  
/**
 * It returns the header.
 * @return a String with the header of the file.
 */
  public String getHeader() {
    return header;
  }//end getHeader

  
  
/**
 * It does return a new header (not necessary the same header as the 
 * input file one). It only includes the valid attributes, those ones
 * defined in @inputs and @outputs (or taken as that role following the
 * keel format specification).
 * @return a String with the new header
 */
  public String getNewHeader(){
    String line = ""; 
    Attribute []attrs = null;

    //Getting the relation name and the attributes
	if ( storeAttributesAsNonStatic && attributes != null ) {
    	line = "@relation "+attributes.getRelationName()+"\n";
		attrs = attributes.getInputAttributes();
	}
	else{
    	line = "@relation "+Attributes.getRelationName()+"\n";
		attrs = Attributes.getInputAttributes();
	}
		
                                                                                                                             

    for (int i=0; i<attrs.length; i++){
        line += attrs[i].toString()+"\n";
    }
                                                                                                                             
    //Gettin all the outputs attributes
	if ( storeAttributesAsNonStatic && attributes != null ){
    	attrs = attributes.getOutputAttributes();
		line += attrs[0].toString()+"\n";
    	
		//Getting @inputs and @outputs
    	line += attributes.getInputHeader()+"\n";
    	line += attributes.getOutputHeader()+"\n";
	}
	else{
		attrs = Attributes.getOutputAttributes();
		line += attrs[0].toString()+"\n";
    	
		//Getting @inputs and @outputs
    	line += Attributes.getInputHeader()+"\n";
    	line += Attributes.getOutputHeader()+"\n";
	}
    
    return line;
  }//end getNewHeader
  
  
/**
 * It does return the original header definiton but
 * without @input and @output in there
 */
  public String getOriginalHeaderWithoutInOut(){
    String line = "";
	Attribute []attrs = null;

	//Getting the relation name and the attributes
	if ( storeAttributesAsNonStatic && attributes != null ){
    	line = "@relation "+attributes.getRelationName()+"\n";
    	attrs = attributes.getAttributes();
	}
	else{
    	line = "@relation "+Attributes.getRelationName()+"\n";
    	attrs = Attributes.getAttributes();
	}

    for (int i=0; i<attrs.length; i++){
        line += attrs[i].toString()+"\n";
    }
    return line;
  }//end getOriginalHeaderWithoutInOut
  
  
  
/**
 * It prints the dataset to the specified PrintWriter
 * @param out is the PrintWriter where to print
 */
  public void print (PrintWriter out){
	for (int i=0; i<instanceSet.length; i++){
		out.println ("> Instance "+i+":");
		
		if ( storeAttributesAsNonStatic && attributes != null )
          	instanceSet[i].print(attributes, out);
		else
          	instanceSet[i].print(out);
		
	}
  }//end print
  
  
/**
 * It prints the dataset to the specified PrintWriter.
 * The order of the attributes is the same as in the 
 * original file
 * @param out is the PrintWriter where to print
 * @param printInOut indicates if the @inputs (1), @outputs(2), 
 * both of them (3) or any (0) has to be printed
 */
  public void printAsOriginal (PrintWriter out, int printInOut){    
      /*Printing the header as the original one*/
	out.println( header );
	
	if ( storeAttributesAsNonStatic && attributes != null ){
		if(printInOut==1 || printInOut==3) 	out.println( attributes.getInputHeader() );
		if(printInOut==2 || printInOut==3) 	out.println( attributes.getOutputHeader() );
   	}
	else{
   		if(printInOut==1 || printInOut==3) 	out.println( Attributes.getInputHeader() );
		if(printInOut==2 || printInOut==3) 	out.println( Attributes.getOutputHeader() );
	} 

	out.print("@data");
	for (int i=0; i<instanceSet.length;i++){
		out.println();
		if ( storeAttributesAsNonStatic && attributes != null )
			instanceSet[i].printAsOriginal( attributes, out );
		else
			instanceSet[i].printAsOriginal( out );
	}
  }//end printAsOriginal
  
  
  public void print (){
	System.out.println ("------------- ATTRIBUTES --------------");
	if ( storeAttributesAsNonStatic && attributes != null ){
		attributes.print();
	}
	else{
		Attributes.print();
	}
	
	System.out.println ("-------------- INSTANCES --------------");
	for (int i=0; i<instanceSet.length; i++){
		System.out.print ("\n> Instance "+i+":");
		
		if ( storeAttributesAsNonStatic && attributes != null ){
			instanceSet[i].print( attributes );
		}
		else
			instanceSet[i].print();
	}
  }//end print
  
  /**
   * Remove all instances from this InstanceSet
   */
  public void clearInstances(){
	  instanceSet = null;
  }
  
  /**
   * It adds the passed instance at the end of the present InstanceSet
   * @param inst the instance to be added
   */
  public void addInstance(Instance inst){
	  int i = 0;
	  Instance nVector[];
	  if(instanceSet!=null){
		  nVector = new Instance[instanceSet.length+1];
		  for(i=0;i<instanceSet.length;i++){
			  nVector[i] = instanceSet[i];
		  }
	  }else
		  nVector = new Instance[1];
	  
	  nVector[i] = inst;
	  instanceSet = nVector;
  }
  
  /**
   * Clear the non-Static attributes. The static class Attributes is not modified.
   */
  public void clearNonStaticAttributes(){
	  attributes = null;
  }
  
  /**
   * Appends the given attribute to the non-static list of the current InstanceSet
   * @param at The Attribute to be Appended
   */
  public void addAttribute(Attribute at){
	  if(attributes==null)
		  attributes = new InstanceAttributes();
	  attributes.addAttribute(at);
  }
  
}//end of InstanceSet Class.
