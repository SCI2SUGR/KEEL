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
import  keel.Algorithms.Genetic_Rule_Learning.XCS.KeelParser.Parser;
import java.util.*;
import java.lang.*;
import java.io.*;


 
public class XCSControl {
/**
 * <p>
 * The XCSControl class creates an XCS object and makes a run, or makes a
 * cross validation run. In the later case, the configuration files are made
 * from a generic file.
 * 
 * To call a normal run (without cross-validation):
 *	java XCSControl config_file
 * 
 * And a cross validation run:
 *	java XCSControl cv config_file
 *
 * </p>
 */
	
	
   final int numFolds = 10;
 
/**
 * <p>
 * It is the main procedure. A new XCS object is declared and the run method is called.
 * </p>
 */

  public static void main (String args[]){
    long iTime = System.currentTimeMillis();
    if (args.length == 1){
            XCS xcs = new XCS (args[0]);
            xcs.run();
    }
    else if (args.length >= 2 && args[0].toLowerCase().equals("cv")){
            XCSControl xcsControl = new XCSControl();
            xcsControl.runCrossValidation(args[1]);	

    }else{
            System.out.println ("You have to pass the configuration file.");
    }
  }//end Main


/**
 * <p>
 * Creates an XCSControl object
 * </p>
 */
  public void XCSControl (){}



/**
 * <p>
 * Creates the configuration file for all cross-validation runs
 * </p>
 * @param fileName is the configuration file name
 */

  public void runCrossValidation(String fileName){
    for (int i=0; i<numFolds; i++){
        String newFileName = createConfigFile(fileName, i);

        // A XCS run is made with this configuration file
        System.out.println ("=============== THE FOLD "+i+" RUN HAS JUST STARTED ===============");
        XCS xcs = new XCS(newFileName);
        xcs.run();

        // The temporal file has to be removed
        File f = new File(newFileName);
        f.delete();
    }
  }//end runCrossValidation


/**
 * <p>
 * It creates a new config file from the generic file that defines the cross-validation
 * run
 * </p>
 * @param fileName is the main file name
 * @param foldNum is the fold of the configuration file to be generated.
 */
  private String createConfigFile (String fileName, int foldNum){
    PrintWriter fout = null;
    BufferedReader fin = null;
    String newName = null;

    try{
        // The output file is opened. It's called fileName+numFold+."kcf"
        newName = fileName.substring(0,fileName.indexOf("."));
        newName = newName + "." + foldNum + ".tmp.kcf";
        fout = new PrintWriter( new BufferedWriter( new FileWriter(newName,true)));

        // The input file is openend
        fin = new BufferedReader( new FileReader(fileName) ); 
        String s = null;
        int contFrases=0;
        while ( (s=fin.readLine()) != null ){
            if (s!=null){
                if (s.toLowerCase().indexOf("trainfile")>=0){
                    //System.out.println ("In train file, we replace : "+(new Integer(0).toString())+" for "+(new Integer(foldNum).toString())+".");
                    //System.out.println ("\tFrom the string: "+s);
                    s = s.replaceAll("." + new Integer(0).toString() + ".", "." + new Integer(foldNum).toString() + ".");
                }else if (s.toLowerCase().indexOf("statisticfileoutname")>=0){
                    s = s.replaceAll("." + new Integer(0).toString(), "." + new Integer(foldNum).toString());
                }else if (s.toLowerCase().indexOf("testfile")>=0){
                    s = s.replaceAll("." + new Integer(0).toString() + ".", "." + new Integer(foldNum).toString() + ".");
                }else if (s.toLowerCase().indexOf("reductedrulesfile")>=0){
                    s = s.replaceAll("." + new Integer(0).toString() + ".", "." + new Integer(foldNum).toString() + ".");
                }else if (s.toLowerCase().indexOf("populationfile")>=0 && s.toLowerCase().indexOf("optimalpopulationfile")==-1){
                    s = s.replaceAll("." + new Integer(0).toString() + ".", "." + new Integer(foldNum).toString() + ".");
                }	
            }
            fout.println(s);
        }
        // We close fin and fout files
        fout.close();
        fin.close();

    }catch (Exception e){
            System.out.println ("Exception when creating a fold configuration file: ");
            e.printStackTrace();
            System.exit(0);
    }
    return newName;	
  }//end createConfigFile
   
     
} // end of Class XCSControl
                                       

