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
/*
 * Main.java
 *
 * Created on 24 de enero de 2005, 10:28
 */

import keel.Dataset.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author  aorriols
 */
public class Main {
    InstanceSet iSet, tSet;
/** 
 * Creates a new instance of Main 
 */
  public Main(String trainName, String testName) {        
    iSet=null; 
    tSet=null;
    //We parse the new file
    try{
        System.out.println("\n-------------------------------------------");
        System.out.println("Parsing the file: "+trainName+".");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        iSet = new InstanceSet();
        iSet.readSet(trainName,true);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println ("\n\nPrinting the train Data: ");
        iSet.print();
    }catch (DatasetException e){
        System.out.println ("\n\n>>>TRAIN Errors");
        e.printAllErrors();
    }catch (HeaderFormatException e2){
        System.err.println ("Exception in header format: "+e2.getMessage());
    }
	/*System.out.println();
    try{
        System.out.println("\n-------------------------------------------");
        System.out.println("Parsing the file: "+testName+".");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        tSet = new InstanceSet();
        tSet.readSet(testName,false);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Printing the test data:");
        tSet.print();
        System.out.println("\n-------------------------------------------");
    }catch (DatasetException e){
        System.out.println ("\n\n>>>TEST Errors.");
        e.printAllErrors();
        tSet.print();
    }catch (HeaderFormatException e2){
        System.err.println ("Exception in header format."+e2.getMessage());
    }
	System.out.println();*/
  }//end Main
    
  
/**
 * It does load another dataset.
 * It has to clear the static variables
 */
  void loadOtherDset(String trainName, String testName){
    Attributes.clearAll();
    
    iSet=null; 
    tSet=null;
    //We parse the new file
    try{
        System.out.println("\n-------------------------------------------");
        System.out.println("Parsing the file: "+trainName+".");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        iSet = new InstanceSet();
        iSet.readSet(trainName,true);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println ("\n\nPrinting the train Data: ");
        iSet.print();
    }catch (DatasetException e){
        System.out.println ("\n\n>>>TRAIN Errors");
        e.printAllErrors();
    }catch (HeaderFormatException e2){
        System.err.println ("Exception in header format: "+e2.getMessage());
    }
	System.out.println();
    try{
        System.out.println("\n-------------------------------------------");
        System.out.println("Parsing the file: "+testName+".");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        tSet = new InstanceSet();
        tSet.readSet(testName,false);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        System.out.println("\n\n-------------------------------------------");
        System.out.println("Printing the test data:");
        tSet.print();
        System.out.println("\n-------------------------------------------");
    }catch (DatasetException e){
        System.out.println ("\n\n>>>TEST Errors.");
        e.printAllErrors();
        tSet.print();
    }catch (HeaderFormatException e2){
        System.err.println ("Exception in header format."+e2.getMessage());
    }
	System.out.println();
  }//end loadOtherDset
  
  
/**
 * Run: Testing the Dataset API
 */
  public void run (){
    System.out.println ("------------------------------------");
    System.out.println ("  Printing normalized test values   ");
    System.out.println ("------------------------------------");
    printNormalized(tSet);
    
    System.out.println ("\n\n------------------------------------");
    System.out.println ("  Printing headers   ");
    System.out.println ("------------------------------------");
    System.out.println (">>>> Input Attributes header: ");
    System.out.println(Attributes.getInputAttributesHeader());
    System.out.println (">>>> Output Attributes header: ");
    System.out.println(Attributes.getOutputAttributesHeader());
    System.out.println (">>>> Input header:  "+Attributes.getInputHeader());
    System.out.println (">>>> Output header: "+Attributes.getOutputHeader());
    System.out.println (">>>> Printing test attributes:" );
    for (int i=0; i<tSet.getNumInstances(); i++){
        System.out.println ("Instance "+i+": "+tSet.getInstance(i).toString());
    }
    
    iSet.removeAttribute(tSet, true, 1);
    System.out.println(">>>Printing iSet!!!");
    iSet.print();
    System.out.println ("\nIset has nominal values: "+Attributes.hasNominalAttributes());
    System.out.println ("Iset has integer values: "+Attributes.hasIntegerAttributes());
    System.out.println ("Iset has real    values: "+Attributes.hasRealAttributes());
    System.out.println (">>>Printing tSet");
    tSet.print();
    System.out.println();
    try{
    System.out.println ("\nGetting the value of the input attr 1: "+tSet.getInputNumericValue(1, 0));
    System.out.println ("\nGetting the value of the output attr 1: "+tSet.getOutputNominalValue(1, 0));
    Instance ins = tSet.getInstance(2);
    System.out.println ("\nThe instance 1 have any missing value: "+ins.existsAnyMissingValue());
    if (ins.existsAnyMissingValue()){
        if (!ins.setInputNumericValue(0, 100)) System.out.println ("Out of bounds.");
        ins.setInputNumericValue(0, 5.0);
        System.out.println ("\nThe instance 1 have any missing value: "+ins.existsAnyMissingValue());
    }
    Instance ins2 = tSet.getInstance(2);
    System.out.println ("\nThe instance 1 have any missing value: "+ins2.existsAnyMissingValue());
    
    }catch (Exception e){
        System.out.println ("Exception: "+e.getMessage());
        e.printStackTrace();
    }
    
  }//end run
  
  
/**
 * Second prove.
 */
  
  public void run2(){
    /*Attribute []inputAttr = Attributes.getInputAttributes();
    for (int i=0; i<inputAttr.length;  i++){
        System.out.println ("--------------------------");
        System.out.println ("Attribute "+i+":");
        inputAttr[i].print();
    }
    Attribute []outputAttr = Attributes.getOutputAttributes();
    for (int i=0; i<outputAttr.length; i++){
        System.out.println ("--------------------------");
        System.out.println ("Attribute "+i+":");
        outputAttr[i].print();
    }
    System.out.println("\n\nThe header of the file is: ");
    System.out.println(iSet.getHeader());    */
  }//end run2
  
  
  
  private void printNormalized (InstanceSet set){
    System.out.println ("Number of instances: "+set.getNumInstances());
    for (int i=0; i<set.getNumInstances(); i++){
        Instance inst = set.getInstance(i);
        double [] inValues  = inst.getNormalizedInputValues();
        if (inst.existsInputMissingValues()){
            boolean [] missing = inst.getInputMissingValues();
            for (int j=0; j<missing.length; j++) if (missing[j]) inValues[j] = -1;
        }
        double [] outValues = inst.getNormalizedOutputValues();
        int k;
        System.out.println (">>Instance "+i+": ");
        System.out.print ("   >Inputs:");
        for (k=0; k<inValues.length; k++){
            String out = (new Double (inValues[k])).toString();
            while (out.length() < 5) out += " ";
            out = out.substring(0,5);
            System.out.print ( out+"  ");
        }
        System.out.print ("   >Outputs:");
        for (k=0; k<outValues.length; k++){
            System.out.print ( (new Double (outValues[k])).toString().substring(0,5)+"  ");
        }
    }
  }//end printNormalized
  
/**
 * @param args the command line arguments
 */
  public static void main(String[] args) {
    if (args.length < 2){
        System.out.println("The BD file name for train and test files has to be passed");
        return;
    }
    
    Main obj = new Main(args[0],args[1]);
    obj.loadOtherDset(args[2],args[3]);
  }//end main
    
}

