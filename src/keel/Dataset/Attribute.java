/*
 * Attribute.java
 *
 */
package keel.Dataset;
import java.util.*;

/**
 * <p>
 * <b> Attribute </b>
 * </p>
 * It contains an attribute representation. The class attributes are enough to 
 * descrive completly an attribute: name, type, possible values, minimums and 
 * maximums, etc. It offers a collection of functions to get all this information.
 *
 * @author Albert Orriols Puig
 * @version keel0.1
 */
public class Attribute{

/////////////////////////////////////////////////////////////////////////////
//////////////// CONSTANTS OF THE ATTRIBUTE CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////
 
/**
 * Label for NOMINAL values.
 */
  public final static int NOMINAL = 0;

  
/**
 * Label for INTEGER values.
 */
  public final static int INTEGER = 1;
  
/**
 * Label for REAL VALUES
 */
  public final static int REAL = 2;

  
/**
 * Label to identify INPUT attributes
 */
  public final static int INPUT = 1;

/**
 * Label to identify OUTPUT attributes
 */
  public final static int OUTPUT = 2;
  
/**
 * Label to identify attributes that hasn't been defined neither as input or output
 */
  public final static int DIR_NOT_DEF = -1;
  
/////////////////////////////////////////////////////////////////////////////
/////////////// ATTRIBUTES OF THE ATTRIBUTE CLASS ///////////////////////////
/////////////////////////////////////////////////////////////////////////////

/**
 * It indicates if the attribute  is an input (0), an output (1) or has not been 
 * defined neither as input or output (-1)
 */
  private int dirAttribute;
  
/**
 * It keeps the type of the attribute. It can be one of the following values:
 * [Attribute.Nominal, Attribute.Integer, Attribute.Real]
 */
  private int type;

/**
 * It stores the name of the attribute.
 */
  private String name;
  
/**
 * Vector where all the values that can take this nominal attribute are going
 * to be stored. 
 */
  private Vector nominalValues;
  
/**
 * Minimum value that can take a real attribute.
 */
  private double min;
  
/**
 * Maximum value that can take a real attribute.
 */
  private double max;

/**
 * Flag that indicates if it's the first time that an operation is made
 * with the current attribute.
 */
  private boolean firstTime;
  
/**
 * It indicates if the bounds of the attribute has been fixed in its definition.
 */
  private boolean fixedBounds;
  
/**
 * It counts the number of values that can take a nominal attribute
 */
  private int countValues;
  
  
/**
 * It informs that a nominal value not compresed in train list values has been
 * read in test
 */
  private boolean newValuesInTest;

/**
 * It keeps the new values in test
 */
  private Vector newValuesList;

/**
 * It keeps the frequency of each class value
 */
  private int [][] classFrequencies;
  
/**
 * It stores the most used value in a nominal attribute
 */
  private String [] mostUsedValue;
  
/**
 * It stores the integer/real mean for this attribute
 */
  private double [] meanValue;
  
/**
 * It keeps the number of updates per class
 */
  private int [] numStatUpdates;

  
/**
 * It says if statistics has to be made
 */
  private boolean makeStatistics;
  
/////////////////////////////////////////////////////////////////////////////
///////////////// METHODS OF THE ATTRIBUTE CLASS ////////////////////////////
/////////////////////////////////////////////////////////////////////////////

/**
 * Attribute Constructor. It instances a new Attribute instance.
 */
  public Attribute() {
    type=-1;
    countValues=0;
    dirAttribute = DIR_NOT_DEF;
    makeStatistics = false;
  }//end Attribute


/**
 * It sets the attribute type.
 */
  public void setType(int _type) {
    if(type!=-1) {
        System.err.println("Type already fixed !!");
        System.exit(1);
    }
    type=_type;
    firstTime=true;

    //If type is nominal, a new vector has to be created to store the list of
    //values that it can take.
    if(type==NOMINAL) {
        nominalValues=new Vector();
        newValuesList = new Vector();
    } 
    
    //In all cases, the fixedBounds flag is set to false.
    fixedBounds=false;
  }//end setType


/**
 * It does return the type of the attribute
 * @return an int that contains the type of the attribute.
 */
  public int getType() {
    return type;
  }//end getType

  
/**
 * It sets the attribute name
 * @param _name is the name to be set.
 */
  public void setName(String _name) {
    name = _name;
  }//end setName



/**
 * It gets the attribute name
 * @return a String with the attribute name.
 */
  public String getName() {
    return name;
  }//end setName

  
  
/**
 * It sets the bound of the integer or real attribute.
 * @param _min is the minimum value that the attribute can take.
 * @param _max is the maximum value that the attribute can take.
 */
  public void setBounds(double _min,double _max) {
    if(type != REAL && type != INTEGER) return;
    fixedBounds=true;
    min=_min;
    max=_max;
  }//end setBounds


/**
 * It returns the variable fixedBounds.
 * @return a boolean that indicates if the bounds are fixed.
 */
  public boolean getFixedBounds(){
    return fixedBounds;
  }//end getFixedBounds
  
  
  
/**
 * It sets the fixedBounds value
 * @param fBounds is the value that has to be fixed to fixedBounds.
 */
  public void setFixedBounds(boolean fBounds){
      fixedBounds = fBounds;
  }//end setFixedBounds

  
/**
 * It does enlarge the attribute bounds
 * @param value is the value read from the BD file
 */
  public void enlargeBounds(double value) {
    if(type!=REAL && type!=INTEGER) return;

    if(firstTime) {
        //If it's the first attribute update and the bounds are not fixed in its
        //specification, the min and max values are initialized.
        if(!fixedBounds) {
            min=value;
            max=value;
        }
        firstTime=false;
    }

    //valueMeans[instanceClass]+=value;
    countValues++;

    if(fixedBounds) return;
    if(value<min)   min=value;
    if(value>max)   max=value;
  }//end enlargeBounds


/**
 * It update an integer or real value read for an attribute in the test 
 * set if it doesn't match with the bounds defined in the train set. In 
 * this case, it replaces the value read for the nearliest bound (the 
 * minimum or the maximim bound respectively)
 * @param value is the value read from the test file.
 * @return a double with the rectified value.
 */
  public double rectifyValueInBounds (double value){
    if (value < min) return min;
    if (value > max) return max;
    return value;
  }//end rectifyValueInBounds
  
  
/**
 * It does check if the value passed as an argument is bounded by
 * the [min, max] interval.
 * @return a boolean that indicates if the value is bounded.
 */
  public boolean isInBounds(double val){
      return (val>=min && val<=max);
  }//end isInBounds
  
  
/**
 * It returns if the value passed is in the list of nominal values
 * @param val is the value to be checked.
 * @return a boolean indicating if the value is a possible nominal.
 */
  public boolean isNominalValue(String val){
      return nominalValues.contains(val);
  }//end isNominalValue
  
  

/**
 * It returns the minimum possible value in a integer or real attribute
 * @return a double with the minimum value
 */
  
  public double getMinAttribute() {
    return min;
  }//end minAttribute

  

/**
 * It returns the maximum possible value in a integer or real attribute
 * @return a double with the maximum value
 */
  public double getMaxAttribute() {
    return max;
  }//end maxAttribute

  
/**
 * This method add a new value to the list of possible values in a nominal
 * attribute.
 * @param value is the new value to be added. 
 */
  public void addNominalValue(String value) {
    if(type!=NOMINAL) return;
    if (!nominalValues.contains(value)){
        nominalValues.addElement(new String(value));
    }
  }//end addNominalValue

  
  
/**
 * It does return the value most frequent for the class
 * @param whichClass is the class which is wanted to know the most
 *        frequent value.
 * @return a String with the most used value.
 */
  public String getMostFrequentValue(int whichClass){
      if (!makeStatistics || type != NOMINAL || mostUsedValue == null) return null;
      if (whichClass <0 || whichClass >= mostUsedValue.length) return null;
      return mostUsedValue[whichClass];
  }//end getMostFrequentValue
  
  

/**
 * Does return the mean value for that attribute.
 * @param whichClass is the integer value for the class
 * @return a double with the mean value.
 */
  public double getMeanValue(int whichClass){
      if (!makeStatistics || (type != REAL && type!=INTEGER) || meanValue == null) return 0;
      if(whichClass<0 || whichClass >= meanValue.length) return 0;
      return meanValue[whichClass];
  }//end getMeanValue
  
/**
 * It does initializes the variables to make statistics
 * @param classNumber is the number of classes.
 */
  void initStatistics(int classNumber){
    makeStatistics = true;
    if (type == NOMINAL){
        classFrequencies = new int [classNumber][];
        numStatUpdates = new int[classNumber];
        for (int i=0; i<classNumber; i++){
            numStatUpdates[i] = 0;
            classFrequencies[i] = new int[nominalValues.size()];
            for (int j=0; j<nominalValues.size(); j++)
                classFrequencies[i][j] = 0;
        }
    }
    else{
        meanValue = new double [classNumber];
        numStatUpdates = new int[classNumber];
        for (int i=0; i<classNumber; i++){
            meanValue[i] = 0;
            numStatUpdates[i] = 0;
        }
    }
  }//end initStatistics
  

/**
 * It does finish the statistics process.
 */
  void finishStatistics(){
     if (!makeStatistics) return;
     if (type == NOMINAL){
        mostUsedValue = new String [classFrequencies.length];
        for (int i=0; i<mostUsedValue.length; i++){
            int max = classFrequencies[i][0];
            int pos = 0;
            for (int j=1; j<classFrequencies[i].length; j++){
                if (classFrequencies[i][j] > max){
                    max = classFrequencies[i][j];
                    pos = j;
                }
            }
            mostUsedValue[i] = (String)nominalValues.elementAt(pos);
        }
    }
    else{
        for (int i=0; i<meanValue.length; i++){
            meanValue[i] /= (double)numStatUpdates[i];
        }
    }
  }//end finishStatistics
  
  
/**
 * It does increment the frequency that a value of a class has been used.
 * It's called when a new value is read.
 * @param whichClass is the class which frequency has to be increased
 * @param value is the nominal value which frequency has to be increased.
 */
  void increaseClassFrequency(int whichClass, String value){    
     if (makeStatistics && classFrequencies != null && 
         classFrequencies[whichClass] != null &&
         classFrequencies[whichClass] != null){     
              classFrequencies[whichClass] [convertNominalValue(value)]++;
              numStatUpdates[whichClass]++;
      }
  }//end increaseClassFrequency

  
/**
 * It adds the new value to the mean values vector
 * @param whichClass is the class where to add the new value
 * @param value is the value to be added. 
 */
  public void addInMeanValue(int whichClass, double value){
      if (makeStatistics){
        numStatUpdates [whichClass]++;
        meanValue[whichClass] += value;
      }
  }//en addInMeanValue
  
  
/**
 * Adds a new value for a nominal that has been read in the test file.
 * @param value is the new value to be added.
 * @return a boolean indicating if value didn't exist in the list.
 */
  public boolean addTestNominalValue(String value){
    if (type != NOMINAL) return false;

    if (!nominalValues.contains(value)){
      nominalValues.addElement(new String(value));
      newValuesList.addElement(new String(value));
      newValuesInTest = true;
      return true;
    }
    return false;
  }//end addTestNominalValue

  
  
/**
 * It returns a vector with all new nominal values read in test.
 * @return a Vector with all new nominal values.
 */
  public Vector getNewValuesInTest(){
      return newValuesList;
  }//end newValuesList
  
  
/**
 * It returns true if in test have appeared new values.
 * @return a boolean indicating if new values have been read in test.
 */
  public boolean areNewNominalValuesInTest(){
      return newValuesInTest;
  }//return areNewValuesInTest
  
  
/**
 * It returns the number of different values that can take a nominal attribute.
 * @return an int with the number of different values that can take a nominal
 *         attribute.
 */
  public int getNumNominalValues() {
    if(type!=NOMINAL) return -1;
    return nominalValues.size();
  }//end getNumNominalValues
  
  
/**
 * Returns all the possible nominal values
 * @return a Vector with the possible values that the nominal can take
 */
  public Vector getNominalValuesList(){
      return nominalValues;
  }//end getNominalValuesList
  
  

/**
 * It returns de ith value of that nominal attribute
 * @param pos indicate which attribute value is wanted.
 * @return a string with the value.
 */
  public String getNominalValue(int pos) {
    if(type!=NOMINAL) return null;
    return (String)nominalValues.elementAt(pos);
  }//end getNominalValue

  
  
/**
 * It converts a nominal value to a integer
 * @param value is the value that is wanted to be converted
 * @return an int with the converted value.
 */
  public int convertNominalValue(String value) {
    return nominalValues.indexOf(value);
  }//end convertNominalValue
  
  

/**
 * It compares two attributes.
 * @param attr is the second attribute of the comparation.
 * @return a boolean that indicates if the attributes are equal.
 */
  public boolean equals(Attribute attr) {
    if(!name.equals(attr.name)) return false;
    if(attr.type!=type) return false;
    if(type==NOMINAL) {
            if(!nominalValues.equals(attr.nominalValues)) 
                    return false;
    }
    return true;
  }//end equals


/**
 * It sets if the attribute is an input or an output attribute
 * @param _dirAtt is the direction (input/output) of the attribute.
 */
  public void setDirectionAttribute(int _dirAtt){
      dirAttribute = _dirAtt;
  }//end setInputAttribute
  
  
/**
 * It returns if the attribute is an input attribute
 * @return a int that indicates if it's an input or output attribute
 */
  public int getDirectionAttribute(){
      return dirAttribute;
  }//end getDirectionAttribute


  
  
  
/**
 * It does normalize a value.
 * @param val is the value to be normalized.
 * @return a double with the normalized value.
 */
  public double normalizeValue (double val){
      if (type == NOMINAL)  return val;
      if (type == INTEGER)  return val-min;
      if (type == REAL)     return (val-min)/(max-min);
      return val;
  }//end normalizeValue
  
  
/**
 * It returns a String with the attribute information in keel format
 * @return an String with the attribute information.
 */
  public String toString(){
    String []typeNames = {"","integer","real"};
    String aux = "@attribute " + name;
    switch (type){
        case NOMINAL:
            aux += "{";
            String ending = ",";
            for (int i=0; i<nominalValues.size(); i++){
                if (i == nominalValues.size() - 1) ending = "";
                aux += (String)nominalValues.elementAt(i) + ending;
            }
            aux +='}';
            //System.out.println("Caso NOMINAL, aux->"+aux);
            //System.out.println("name->" + name);
            break;
        case INTEGER:
            aux += " integer["+(new Integer ((int)min)).toString();
            aux += ","+ (new Integer ((int)max)).toString()+"]";
            break;
        case REAL:
            aux += " real["+(new Double (min)).toString();
            aux += ","+ (new Double (max)).toString()+"]";
            break;
    }
    return aux;
  }//end toString
  
/**
 * This method prints the attribute information.
 */
  public void print(){
      String [] typesConv = {"Nominal","Integer","Real"};
      System.out.println("    > Name: "+name+".");
      System.out.println("    > Type: "+type );
      System.out.println("    > Type: "+typesConv[type]+".");
      System.out.print  ("    > Input/Output: ");
      switch (dirAttribute){
          case INPUT:
                System.out.println("INPUT");
                break;
          case OUTPUT:
                System.out.println("OUTPUT");
                break;
          default:
                System.out.println("NOT DEFINED");
      }
      System.out.print("    > Range: ");
      switch (type){
          case NOMINAL:
              System.out.print("{");
              for (int i=0; i<nominalValues.size(); i++){
                  System.out.print ((String)nominalValues.elementAt(i)+"  ");
              }
              System.out.print("}");
              break;
          case INTEGER:
              System.out.print("["+(int)min+","+(int)max+"]");
              break;
          default:
              System.out.print("["+min+","+max+"]");      
      }
      if (type == NOMINAL){
          if (mostUsedValue != null){
              System.out.println("\n    > Most used value: ");
              for (int i=0; i<mostUsedValue.length; i++){
                  System.out.print("       > class "+i+":"+mostUsedValue[i]);
                  System.out.println("  ("+classFrequencies[i][convertNominalValue(mostUsedValue[i])]+")." );
              }
          }
      }else if (meanValue != null){
          System.out.println("\n    > Mean used value: ");
          for (int i=0; i<meanValue.length; i++){
              System.out.println("       > class "+i+": "+meanValue[i]);
          }
      }
      System.out.println();
  }//end print
  
  

}//end of class Attribute
