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
 * Created on 28 de enero de 2005, 9:07
 */

package keel.Dataset;

/**
 * <p>
 * <b> ErrorInfo </b>
 * </p>
 * This class conatins the information about an error apperaed during the dataset
 * read.
 *
 * @author Albert Orriols Puig
 * @version keel0.1
 *
 */
public class ErrorInfo {

/**
 * Definitions of possible ERRORS
 */
  public final static int BadNumberOfValues             = 0;
  public final static int OutputMissingValue            = 1;
  public final static int BadNumericValue               = 2;
  public final static int TrainNominalOutOfRange        = 3;
  public final static int TestNominalOutOfRange         = 4;
  public final static int TrainNumberOutOfRange         = 5;
  public final static int TestNumberOutOfRange          = 6;
  public final static int TypeAlreadyFixed              = 7;
  public final static int AttributeNotDefinedInTrain    = 8;
  public final static int InputTrainAttributeNotDefined = 9;
  public final static int InputTestAttributeNotDefined  = 10;
  public final static int OutputTrainAttributeNotDefined= 11;
  public final static int OutputTestAttributeNotDefined = 12;
  public final static int InputsInTestNotEquals         = 13;
  public final static int OutputsInTestNotEquals        = 14;
  
  
/**
 * It stores the type of the error
 */
  public int typeOfError;

/**
 * It stores the instance number where the error has appeared.
 */
  public int instanceNum;
  
/**
 * It stores the file number where the error has appeared.
 */
  public int fileLineNum;
  
/**
 * It stores the attribute number where the error has appeared.
 */
  public int attributeNum;
  
/**
 * It keeps if the attribute is an input, output or non-defined attribute
 */
  public int attDirection;
  
/**
 * It stores if the error has been in the train dataset. Otherwise
 * it has been in the test dataset.
 */
  public boolean errorInTrain;
  
/**
 * Message to be writen when showing the error
 */
  private String message;
    
/** 
 * Creates a new instance of ErrorInfo 
 */
  public ErrorInfo() {
    typeOfError = -1;
    instanceNum = -1;
    fileLineNum = -1;
    attributeNum = -1;
    attDirection = Attribute.DIR_NOT_DEF;
    errorInTrain = false;
  }//end ErrorInfo
 
  
/**
 * Creates a new instance with the parameters passed.
 */
  public ErrorInfo(int _type, int _iNum, int _lNum, int _atNum, int _atDir, boolean _train, String _msg){
    typeOfError = _type;
    instanceNum = _iNum;
    fileLineNum = _lNum;
    attributeNum = _atNum;
    attDirection = _atDir;
    errorInTrain = _train;                
    message = _msg;
  }//end ErrorInfo

/**
 * It creates a new Error info with the message passed
 * @param msg is the error message
 */
  public ErrorInfo(String msg){
    message = msg;
    typeOfError = -1;
    instanceNum = -1;
    fileLineNum = -1;
    attributeNum = -1;
    attDirection = Attribute.DIR_NOT_DEF;
    errorInTrain = false;
  }//end ErrorInfo

/**
 * It does print an understable message about the error
 */
  public void print(){
    String [] dir = {"Output", "Input"};
    switch (typeOfError){
        case BadNumberOfValues:
                System.err.println("BadNumberOfValuesException >> [line: "+fileLineNum+", instance: "+instanceNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case OutputMissingValue:
                System.err.println("OutputMissingValueException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case BadNumericValue:
                System.err.println("BadNumericValueException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case TrainNominalOutOfRange:
                System.err.println("TrainNominalOutOfRangeException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case TestNominalOutOfRange:
                System.err.println("TestNominalOutOfRangeException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case TrainNumberOutOfRange:
                System.err.println("TrainNumberOutOfRangeException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case TestNumberOutOfRange:
                System.err.println("TestNumberOutOfRangeException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case TypeAlreadyFixed:
                System.err.println("TypeAlreadyFixedException >> [line: "+fileLineNum+", instance: "+instanceNum+", attributeNum: "+attributeNum+", INPUT/OUTPUT: "+dir[Attribute.OUTPUT-attDirection]+" Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case AttributeNotDefinedInTrain:
                System.err.println("AttributeNotDefinedInTrainException >> [line: "+fileLineNum+", attributeNum: "+attributeNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case InputTrainAttributeNotDefined:
                System.err.println("InputTrainAttributeNotDefinedException >> [line: "+fileLineNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case InputTestAttributeNotDefined:
                System.err.println("InputTestAttributeNotDefinedException >> [line: "+fileLineNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case OutputTrainAttributeNotDefined:
                System.err.println("OutputTrainAttributeNotDefinedException >> [line: "+fileLineNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case OutputTestAttributeNotDefined:
                System.err.println("OutputTestAttributeNotDefinedException >> [line: "+fileLineNum+", Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case InputsInTestNotEquals:
                System.err.println("InputsInTestNotEqualsException >> [Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
        case OutputsInTestNotEquals:
                System.err.println("OutputsInTestNotEqualsException >> [Train DB: "+errorInTrain+"]");
                System.err.println(message);
                break;
    }
  }//end print
  
  
}//end of Class ErrorInfo

