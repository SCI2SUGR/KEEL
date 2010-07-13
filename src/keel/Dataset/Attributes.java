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

/*
 * Attributes.java
 *
 * Created on 20 de junio de 2004, 10:06
 */
package keel.Dataset;
import java.util.*;

/**
 * <p>
 * <b> Attributes </b>
 * </p>
 *
 * This class is a static class that, basically, contains a Vector of defined
 * attributes in the train file. Although it keeps all the attributes, it divides
 * them in two groups, the input attributes and the output attributes. It could
 * be that, depending on the @inputs and @outputs defined in the train file, some
 * of the attributes are not valid, so, their values are not loaded to the API
 * dataset. Even in this case, the non-careful attributes information is mantained 
 * in this static class.
 *
 * @author Albert Orriols Puig
 * @see Attribute
 * @version keel0.1
 */

public final class Attributes {
	
/////////////////////////////////////////////////////////////////////////////
/////////////// ATTRIBUTES OF THE ATTRIBUTES CLASS //////////////////////////
/////////////////////////////////////////////////////////////////////////////
  
/**
 * It contains all the attributes definitions.
 */
  static Vector attributes = new Vector();
  
/**
 * It contains a reference to all input attributes.
 */
  static Vector inputAttr  = new Vector();
  
/**
 * It contains a reference to all output attributes.
 */
  static Vector outputAttr = new Vector();
  
/**
 * It contains a reference to all undefined attributes.
 */
  static Vector undefinedAttr = new Vector();
  
/**
 * A flag indicating if the vector contains any nominal attribute.
 */
  static boolean hasNominal=false;

/**
 * A flag indicating if the vector contains any integer attribute.
 */
  static boolean hasInteger=false;

/**
 * A flag indicating if the vector contains any real attribute.
 */
  static boolean hasReal=false;
 
/**
 * It indicates if there are missing values 
 */
  static boolean hasMissing = false;
 
/**
 * A vector containing the types of each attribute.
 */
  //private static int []type;
  
/**
 * String that keeps the relation name
 */
  static String relationName;

 
  
/////////////////////////////////////////////////////////////////////////////
///////////////// METHODS OF THE ATTRIBUTES CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////

  
/**
 * clearAll
 * This method clears all the static members of the class.
 * It is used when another dataset is wanted to be loaded
 */
  public static void clearAll(){
      attributes = new Vector();
      inputAttr  = new Vector();
      outputAttr = new Vector();
      undefinedAttr = new Vector();
      hasNominal=false;
      hasInteger=false;
      hasReal=false;
	  hasMissing = false;
      String relationName=null;
  }//end clearAll
  
  
/**
 * This method adds an attribute definition.
 * @param attr is the new attribute to be added.
 */
  static void addAttribute(Attribute attr) {
    attributes.addElement(attr);
    if(attr.getType()==Attribute.NOMINAL) hasNominal=true;
    if(attr.getType()==Attribute.INTEGER) hasInteger=true;
    if(attr.getType()==Attribute.REAL) hasReal=true;
  }//end addAttribute

  
/**
 * The function returns if there is any nominal attribute
 */
  public static boolean hasNominalAttributes() {
    return hasNominal;
  }//end hasNominalAttributes
  

/**
 * The function returns if there is any integer attribute.
 */
  public static boolean hasIntegerAttributes() {
    return hasInteger;
  }//end hasIntegerAttributes
  
  
/**
 * The function returns if there is any real attribute.
 */
  public static boolean hasRealAttributes() {
    return hasReal;
  }//end hasRealAttributes

/**
 * The function returns if there is any missing value
 */
  public static boolean hasMissingValues() {
	return hasMissing;
  }// end hasMissingValues

/**
 * It returns the attribute requested.
 * @param _name is the name of the attribute.
 */  
  public static Attribute getAttribute(String _name) {
    int i;
    for (i=0; i<attributes.size(); i++){
        if ( ((Attribute)attributes.elementAt(i)).getName().equals(_name)){
            break;
        }
    }
    
    if (i == attributes.size()) return null;
    return (Attribute)attributes.elementAt(i);
  }//end getAttribute


/**
 * It does return an array with all attributes
 */
  public static Attribute[] getAttributes(){
    if (attributes.size() == 0) return null;
    Attribute [] attr = new Attribute[attributes.size()];
    for (int i=0; i<attr.length; i++){
      attr[i] = (Attribute)attributes.elementAt(i); 
    }
    return attr;
  }//end getAttribute

  

/**
 * It returns the input attribute being int the position passed as an argument.
 * @param pos is the position of the attribute wanted.
 */
  public static Attribute getInputAttribute(int pos) {
    if (pos<0 || pos >= inputAttr.size()) return null;  
    return (Attribute)inputAttr.elementAt(pos);
  }//end getInputAttribute
 
  
/**
 * It does return all the input attributes
 */
  public static Attribute[] getInputAttributes(){
    if (inputAttr.size() == 0) return null;
    Attribute [] attr = new Attribute[inputAttr.size()];
    for (int i=0; i<attr.length; i++){
      attr[i] = (Attribute)inputAttr.elementAt(i); 
    }
    return attr;
  }//end getInputAttribute

  
/**
 * It does return an String with the @inputs in keel format.
 * @return an string with the @inputs definition  .
 */
  public static String getInputHeader(){
    String aux = "@inputs ";
    String ending = ",";
    for (int i=0; i<inputAttr.size(); i++){
      if (i == inputAttr.size() - 1) ending = "";
      aux += ((Attribute)inputAttr.elementAt(i)).getName() + ending;
    }
    return aux;
  }//end getInputHeader

/**
 * It does return a String with all the input attributes definition in keel
 * format. The order of the attributes is the order of lecture.
 * @return a String with the input attributes definition.
 */
  public static String getInputAttributesHeader(){
    String aux = "";
    for (int i=0; i<inputAttr.size(); i++){
        //Writting the name and type of the attribute
        aux += ((Attribute)inputAttr.elementAt(i)).toString()+"\n";
    }
    return aux;
  }//end getInputAttributesHeader
 
  
/**
 * It does return all the output attributes.
 */
  public static Attribute[] getOutputAttributes(){
    if (outputAttr.size() == 0) return null;
    Attribute [] attr = new Attribute[outputAttr.size()];
    for (int i=0; i<attr.length; i++){
      attr[i] = (Attribute)outputAttr.elementAt(i); 
    }
    return attr;
  }//end outputAttributes

  
/**
 * It returns the output attribute being int the position passed as an argument.
 * @param pos is the position of the attribute wanted.
 */
  public static Attribute getOutputAttribute(int pos) {
    if (pos<0 || pos >= outputAttr.size()) return null;
    return (Attribute)outputAttr.elementAt(pos);
  }//end getOutputAttribute

  
/**
 * It does return an String with the @outputs in keel format.
 * @return an string with the @outputs definition  .
 */
  public static String getOutputHeader(){
    String aux = "@outputs ";
    String ending = ",";
    for (int i=0; i<outputAttr.size(); i++){
      if (i == outputAttr.size() - 1) ending = "";
      aux += ((Attribute)outputAttr.elementAt(i)).getName() + ending;
    }
    return aux;
  }//end getOutputHeader
 
  
/**
 * It does return a String with all the output attributes definition in keel
 * format. The order of the attributes is the order of lecture.
 * @return a String with the output attributes definition.
 */
  public static String getOutputAttributesHeader(){
    String aux = "";
    for (int i=0; i<outputAttr.size(); i++){
        //Writting the name and type of the attribute
        aux += ((Attribute)outputAttr.elementAt(i)).toString()+"\n";
    }
    return aux;
  }//end getOutputAttributesHeader
 


/**
 * It returns the undefined attribute being int the position passed as an argument.
 * @param pos is the position of the attribute wanted.
 */
  public static Attribute getUndefinedAttribute(int pos) {
   if (pos<0 || pos >= undefinedAttr.size()) return null; 
   return (Attribute)undefinedAttr.elementAt(pos);
  }//end getUndefinedAttribute
 
  
/**
 * It does return all the undefined attributes
 */
  public static Attribute[] getUndefinedAttributes(){
    if (undefinedAttr.size() == 0) return null;
    Attribute [] attr = new Attribute[undefinedAttr.size()];
    for (int i=0; i<attr.length; i++){
      attr[i] = (Attribute)undefinedAttr.elementAt(i); 
    }
    return attr;
  }//end getUndefinedAttributes
  

/**
 * It does return a String with all the undefined attributes definition 
 * in keel format. The order of the attributes is the order of lecture.
 * @return a String with the input attributes definition.
 */
  public static String getUndefinedAttributesHeader(){
    String aux = "";
    for (int i=0; i<undefinedAttr.size(); i++){
        //Writting the name and type of the attribute
        aux += ((Attribute)undefinedAttr.elementAt(i)).toString()+"\n";
    }
    return aux;
  }//end getUndefinedAttributesHeader
 

/**
 * It returns the attribute being int the position passed as an argument.
 * @param pos is the position of the attribute wanted.
 */
  public static Attribute getAttribute(int pos) {
   return (Attribute)attributes.elementAt(pos);
  }//end getAttribute

  
  
/**
 * It return the total number of attributes in the API
 * @return an int with the number of attributes
 */
  public static int getNumAttributes() {
    return attributes.size();
  }//end getNumAttributes


  
/**
 * It return the  number of input attributes in the API
 * @return an int with the number of attributes
 */
  public static int getInputNumAttributes() {
    return inputAttr.size();
  }//end getInputNumAttributes

  
  
  
/**
 * It return the number of output attributes in the API
 * @return an int with the number of attributes
 */
  public static int getOutputNumAttributes() {
    return outputAttr.size();
  }//end getOutputNumAttributes

/**
 * It return the number of undefined attributes in the API
 * @return an int with the number of attributes
 */
  public static int getUndefinedNumAttributes() {
    return undefinedAttr.size();
  }//end getUndefinedNumAttributes
  
  
  
  
/**
 * It returns all the attribute names in the dataset except these ones
 * that are already in the vector v.
 * @param v is a vector with the exceptions
 * @return a Vector with the rest of attribute names.
 */
  static Vector getAttributesExcept(Vector v){
      Vector restAt = new Vector();
      for (int i=0; i<attributes.size(); i++){
          String attName = ((Attribute)attributes.get(i)).getName();
          if (!v.contains(attName))  restAt.add(attName);
      }
      return restAt;
  }//end getAttributesExcept
  
  
  
/**
 * It organizes the whole number of attributes to input, output, and 
 * "no-direction" attributes.
 * @param inAttNames  is a vector with the names of all input  attributes.
 * @param outAttNames is a vector with the names of all output attributes.
 */
  static void setOutputInputAttributes(Vector inAttNames, Vector outAttNames){
    int i;
    String attName;
    Attribute att;
    
    for (i=0; i<attributes.size(); i++){
        att = (Attribute)attributes.get(i);
        attName = att.getName();
        if (inAttNames.contains(attName)){  
            att.setDirectionAttribute(Attribute.INPUT);
            inputAttr.add(attributes.get(i));
        }else if (outAttNames.contains(attName)){ 
            att.setDirectionAttribute(Attribute.OUTPUT);
            outputAttr.add(attributes.get(i));
        }else{
            undefinedAttr.add(attributes.get(i));
        }
    }

    //Finally, making some statistics
    hasNominal = false;
    hasInteger = false;
    hasReal    = false;
    
    for (int index=0; index<2; index++){
        int iterations = (index == 0)? inputAttr.size() : outputAttr.size();
        for (i=0; i<iterations; i++){
            att = (index == 0)? (Attribute)inputAttr.elementAt(i) : (Attribute)outputAttr.elementAt(i);
            switch ( att.getType() ){
                case Attribute.NOMINAL:
                    hasNominal = true;
                    break;
                case Attribute.INTEGER:
                    hasInteger = true;
                    break;
                case Attribute.REAL:
                    hasReal = true;
                    break;
            }
        }
    }
  }//end setOutputInputAttributes

  
  
/**
 * This method checks if all the input names vector corresponds with
 * all the attributes in input vector. If not, it returns a false. It
 * is used in a test to check that the definition of input attributes
 * is the same as the definition made in train.
 * @param outputNames is a vector with all input attribute names.
 */
  static boolean areAllDefinedAsInputs(Vector inputNames){
    if (inputNames.size() != inputAttr.size()) return false;
    
    for (int i=0; i<inputAttr.size(); i++){
        if ( !inputNames.contains(((Attribute)inputAttr.elementAt(i)).getName()) ) return false;
    }
    return true;
  }//end areAllDefinedAsInputs

  
  
  
  
/**
 * This method checks if all the output names vector corresponds with
 * all the attributes in output vector. If not, it returns a false. It
 * is used in a test to check that the definition of output attributes
 * is the same as the definition made in train.
 * @param outputNames is a vector with all output attribute names.
 */
  public static boolean areAllDefinedAsOutputs(Vector outputNames){
    if (outputNames.size() != outputAttr.size()) return false;
    
    for (int i=0; i<outputAttr.size(); i++){
        if ( !outputNames.contains(((Attribute)outputAttr.elementAt(i)).getName()) ) return false;
    }
    return true;
  }//end areAllDefinedAsOutputs
  

/**
 * It sets the relation name.
 * @param rel is the name to be set to the relationName
 */
  public static void setRelationName(String rel){
      relationName = rel;
  }//end setRelationName
  
/**
 * It gets the relation name.
 * @return an String with the realtion name.
 */
  public static String getRelationName(){
      return relationName;
  }//end relationName

  
  
/**
 * It does remove an attribute. Removing an attribute only implies, in terms
 * of Attribute static class, to take it out from the input/output attributes
 * list, but it will never be removed from the attributes general list. So
 * it will be placed as a NON-SPECIFIED attribute, as it wasn't declared in 
 * neither @inputs and @outputs definition.
 * @param inputAtt is a boolean that indicates if the attribute to be removed
 * is an input attribute
 * @param whichAtt is an integer that indicates the position of the attribute 
 * to be removed.
 * @return a boolean that will be false if the attribute hasn't been found.
 */
  public static boolean removeAttribute(boolean inputAtt, int whichAtt){
    Attribute atToDel=null;
    if ( inputAtt && (whichAtt >=  inputAttr.size() || whichAtt < 0)) return false;
    if (!inputAtt && (whichAtt >= outputAttr.size() || whichAtt < 0)) return false;
    
    if (inputAtt){//inputAttribute
        atToDel =  (Attribute)inputAttr.elementAt(whichAtt);
        atToDel.setDirectionAttribute(Attribute.DIR_NOT_DEF);
        inputAttr.removeElementAt(whichAtt);
    }
    else{ //output attribute
        atToDel = (Attribute)outputAttr.elementAt(whichAtt);
        atToDel.setDirectionAttribute(Attribute.DIR_NOT_DEF);
        outputAttr.removeElementAt(whichAtt);
    }
    //We get the position where it has to go in the undefined attributes vector.
    int undefPosition = searchUndefPosition(atToDel);
    undefinedAttr.insertElementAt(atToDel, undefPosition);
    
    hasNominal = false;
    hasInteger = false;
    hasReal    = false;
    for (int index=0; index<2; index++){
        int iterations = (index == 0)? inputAttr.size() : outputAttr.size();
        for (int i=0; i<iterations; i++){
            Attribute att = (index == 0)? (Attribute)inputAttr.elementAt(i) : (Attribute)outputAttr.elementAt(i);
            switch ( att.getType() ){
                case Attribute.NOMINAL:
                    hasNominal = true;
                    break;
                case Attribute.INTEGER:
                    hasInteger = true;
                    break;
                case Attribute.REAL:
                    hasReal = true;
                    break;
            }
        }
    }
    
    return true;
  }//end removeAttribute
  
  
  
  
/**
 * It does search the relative position of the input/output attribute 
 * 'whichAtt' in the list of indefined attributes. 
 * @param attToDel is an Attribute reference to the attribute that has to
 * be deleted. 
 * @return an int with the relative position.
 */
  static int searchUndefPosition(Attribute attToDel){
      int undefCount=0, count = 0;
      
      Attribute att_aux = (Attribute)attributes.elementAt(count);
      while (attToDel != att_aux){
         if (att_aux.getDirectionAttribute() == Attribute.DIR_NOT_DEF){
             undefCount++;
         }
         count++;
         att_aux = (Attribute)attributes.elementAt(count);
      }
      return undefCount;
  }//end searchUndefPosition
  
/**
 * It does initializes the statistics to make the statistics. It only
 * works for classifier Datasets (only one output).
 */
  
  static void initStatistics(){
    if (outputAttr.size() != 1) return;
    
    int classNumber = ((Attribute) outputAttr.elementAt(0)).getNumNominalValues();
    //If the output attribute has not been defined as a nominal or it has not
    //any value in the nominal list, the initalization is aborted. 
    if (classNumber<=0) return;
    
    for (int i=0; i<inputAttr.size(); i++){
        ((Attribute)inputAttr.elementAt(i)).initStatistics(classNumber);
    }
  }//end initStatistics
  
  
/**
 * It does finish the statistics
 */
  static void finishStatistics(){
    if (outputAttr.size() != 1) return;
    
    for (int i=0; i<inputAttr.size(); i++){
        ((Attribute)inputAttr.elementAt(i)).finishStatistics();
    }      
  }//end finishStatistics
  
/**
 * It does print the attributes information
 */
  public static void print(){
      System.out.println("@relation = "+relationName);
    for(int i=0; i<attributes.size(); i++){
        Attribute att = (Attribute)attributes.elementAt(i);
        if (att.getDirectionAttribute() == Attribute.INPUT){
            System.out.println("  > INPUT ATTRIBUTE:     ");
        }else if (att.getDirectionAttribute() == Attribute.OUTPUT){
            System.out.println("  > OUTPUT ATTRIBUTE:    ");
        }else{
            System.out.println("  > UNDEFINED ATTRIBUTE: ");
        }
        att.print();
    }
  }//end print
  

}//end of Attributes class

