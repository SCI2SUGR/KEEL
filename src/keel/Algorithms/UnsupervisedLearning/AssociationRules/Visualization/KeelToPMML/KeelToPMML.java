/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. S�nchez (luciano@uniovi.es)
    J. Alcal�-Fdez (jalcala@decsai.ugr.es)
    S. Garc�a (sglopez@ujaen.es)
    A. Fern�ndez (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)
    L.A. Segura (alberto.segura.delgado@gmail.com)

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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.KeelToPMML;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"deprecation", "unused", "unchecked"})
/**
 *
 * @author alberto
 */
public class KeelToPMML {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        if(args.length < 1)
        {
            System.err.println("No config file expecified");
            return;
        }
        
        try{
            // Rules files
            String traFile = "";
            String tstFile = "";
            String txtFuzzyFile = "";
            String path = "";
            File config = null;
            String outputPath = "";
            
            if(!args[0].equals("."))
            {   // we recive an config file to read
                System.out.println("\n\nOpening Config File "+args[0]);
                config = new File(args[0]);
                BufferedReader reader = new BufferedReader(new FileReader(config));
                String text = "";

                while ((text = reader.readLine()) != null)
                {
                    if(text.indexOf("inputData") > -1)
                    {
                        // Find all files. Format is: "path/file.ext" "path/otherFile.ext" ..
                        Matcher m = Pattern.compile("\"[^\"]+").matcher(text);
                        while (m.find())
                        {
                            System.out.println("File found: "+m.group());
                            if(m.group().indexOf(".tst") > -1)
                            {
                                tstFile = m.group().substring(1); // Delete "
                                tstFile = tstFile.replace(".KeelToPMML", "");
                                tstFile = tstFile.replace("/[a-zA-Z0-9]+.tst", "/");
                                int lastSlash = m.group().lastIndexOf("/");
                                path = m.group().substring(1, lastSlash+1);
                                break;
                            }
                            else if(m.group().indexOf(".tra") > -1)
                            {
                                traFile = m.group().substring(1); // Delete "
                                traFile = traFile.replace(".KeelToPMML", "");
                                traFile = traFile.replace("/[a-zA-Z0-9]+.tra", "/");
                                int lastSlash = m.group().lastIndexOf("/");
                                path = m.group().substring(1, lastSlash+1);
                                break;
                            }
                            else if(m.group().indexOf(".txt") > -1)
                            {
                                txtFuzzyFile = m.group().substring(1); // Delete "
                                txtFuzzyFile = tstFile.replace(".KeelToPMML", "");
                                txtFuzzyFile = tstFile.replace("/[a-zA-Z0-9]+.txt", "/");
                                int lastSlash = m.group().lastIndexOf("/");
                                path = m.group().substring(1, lastSlash+1);
                                break;
                            }
                        }
                    }
                    else if(text.indexOf("outputData") > -1)
                    {
                        // Find all files. Format is: "path/file.ext" "path/otherFile.ext" ..
                        Matcher p = Pattern.compile("\"[^\"]+").matcher(text);
                        if(p.find())
                        {
                            outputPath = p.group().substring(1); // Delete ";
                            outputPath = outputPath.replace(".stat", ".pmml");
                            System.out.println("outputPath = " + outputPath);
                        }
                        else
                        {
                            outputPath = null;
                        }
                    }
                }
            }
            else
            {
                // We receive "." -> use this directory to search rule files
                traFile = "";
                tstFile = "";
                txtFuzzyFile = "";
                path = "./";
            }
            
            boolean filesToProccess = true;
            int i = 0;
            while(filesToProccess)
            {
                // Usually the config file has errors and says "result0s0" instead of "result0"
                // it depends on the type of algorithm
                //  result0 -> Algorithm without seed and one execution
                //  result0s0 -> Algorithm with seed and one execution
                traFile = path + "result0s" + i + ".tra";
                tstFile = path + "result0s" + i + ".tst";
                txtFuzzyFile = path + "result0s" + i + "e0.txt";
                config = new File(tstFile);
                if(!config.exists())
                {   // Try without s (No seed algorithm)
                    traFile = path + "result" + i + ".tra";
                    tstFile = path + "result" + i + ".tst";
                    txtFuzzyFile = path + "result" + i + "e0.txt";
                    config = new File(tstFile);
                    if(!config.exists())
                    {   // If not exists, stop
                        filesToProccess = false;
                        break;
                    }
                }

                System.out.println("TST: "+ tstFile);
                System.out.println("TRA: "+ traFile);
                System.out.println("Path: "+ path);
                
                File isFuzzy = new File(txtFuzzyFile);
                
                if(outputPath == null)
                {
                    outputPath = path;
                }
                
                try{
                    // Fuzzy
                    KeelToPMMLFuzzyProcessor p = new KeelToPMMLFuzzyProcessor("=", "&&");
                    p.parseXmlFile(traFile, tstFile, txtFuzzyFile, outputPath);
                }catch(Exception e){
                    // No fuzzy
                    KeelToPMMLProcessor p = new KeelToPMMLProcessor("=", "&&");
                    p.parseXmlFile(traFile, tstFile, outputPath);
                }
                
                /*
                if(isFuzzy.exists())
                {   // Fuzzy
                    KeelToPMMLFuzzyProcessor p = new KeelToPMMLFuzzyProcessor("=", "&&");
                    p.parseXmlFile(traFile, tstFile, txtFuzzyFile, path+"rules"+ i +".pmml");
                }
                else
                {   // No Fuzzy
                    KeelToPMMLProcessor p = new KeelToPMMLProcessor("=", "&&");
                    p.parseXmlFile(traFile, tstFile, path+"rules"+ i +".pmml");
                }
                */
                i++;
            }
            
            System.out.println("PMML file generated! On: "+ outputPath);
            
        }catch(Exception e)
        {
            System.err.println(e);
        }
    }
    
}
