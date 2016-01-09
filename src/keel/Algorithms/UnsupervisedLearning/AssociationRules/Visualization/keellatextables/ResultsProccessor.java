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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.keellatextables;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings({"deprecation", "unused", "unchecked"})
/**
 *
 * @author alberto
 */
public class ResultsProccessor {
  private Document readedDocument = null;
    
    private HashMap<String, HashMap<String, Double> > algorithmMeasures = null;
    private HashMap<String, Double> algorithmTotalRules = null;
    
    private HashMap<String, Integer> algorithmSeeds = null;
    
    // measures to exclude from average calc
    private ArrayList<String> excludedFromAverage = null;
    
    // measures sorted (by addition) to show in the table
    private ArrayList<String> sortedMeasures = null;
    
    private String actualAlgorithm;
    private Integer actualRulesNumber = 0;
    
    public ResultsProccessor()
    {
        algorithmMeasures = new HashMap();
        algorithmTotalRules = new HashMap();
        algorithmSeeds = new HashMap();
        
        // Excluded
        excludedFromAverage = new ArrayList();
        excludedFromAverage.add("$\\#R$");
        
        // Sorted
        sortedMeasures = new ArrayList();
    }
    
    private void resetProcessor()
    {
        algorithmMeasures.clear();
    }
    
    public void parseXmlFile(String measuresFile, String algorithm) throws Exception
    {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        //Using factory get an instance of document builder
        DocumentBuilder db = dbf.newDocumentBuilder();

        //parse using builder to get DOM representation of the XML file
        readedDocument = db.parse(measuresFile);
        actualAlgorithm = algorithm;
        parseMeasuresFile();
    }
    
    private void parseMeasuresFile() throws Exception
    {
        //get the root element
        Element docEle = readedDocument.getDocumentElement();

        // Get all rules
        NodeList rules = docEle.getElementsByTagName("rule");
        actualRulesNumber = rules.getLength();
        
        // Vars to save the rule measure (to calculate other measures for that rule).
        Double support = 0.0, confidence = 0.0, antSupport = 0.0, conSuport = 0.0;
        Double lift = 0.0, conviction = 0.0, cf = 0.0, netConf = 0.0;
        
        MeasuresCalculator mc = new MeasuresCalculator();
        
        
        // Put other measures (number of rules..)
        putMeasure("$\\#R$", actualRulesNumber*1.0);
        
        // Update rules number to calculate averages later
        addRulesNumberToAlgorithm(actualRulesNumber);
        
        if(rules != null && rules.getLength() > 0)
        {
            // Add New seed for this algorithm
            addSeed();
            for(int i = 0 ; i < rules.getLength();i++)
            {
                    // Get rule number i
                    Element rule = (Element)rules.item(i);
                    // basic Measures
                    support = processMeasure(rule, "rule_support", "$Av_{Sup}$");
                    antSupport = processMeasure(rule, "antecedent_support", "$Av_{AntSup}$");
                    conSuport = processMeasure(rule, "consequent_support", "$Av_{ConSup}$");

                    /*
                    confidence = processMeasure(rule, "confidence", "$Av_{Conf}$");
                    processMeasure(rule, "lift", "$Av_{Lift}$");
                    processMeasure(rule, "conviction", "$Av_{Conv}$");
                    processMeasure(rule, "certainFactor", "$Av_{CF}$");
                    processMeasure(rule, "netConf", "$Av_{NetConf}$");
                    processMeasure(rule, "yulesQ", "$Av_{YulesQ}$");
                    processMeasure(rule, "nAttributes", "$Av_{Amp}$");
                    */

                    putMeasure("$Av_{Lift}$", mc.calculateLift(antSupport, conSuport, support));
                    putMeasure("$Av_{Conf}$", mc.calculateConfidence(antSupport, conSuport, support));
                    putMeasure("$Av_{CF}$", mc.calculateCF(antSupport, conSuport, support));
                    putMeasure("$Av_{Conviction}$", mc.calculateConviction(antSupport, conSuport, support));
            }
        }
        else
        {
            throw new Exception("NoRules");
        }
    }
    
    private void putMeasure(String beautifulName, Double measureValue)
    {
        if(algorithmMeasures.containsKey(actualAlgorithm))
        {
            if(!algorithmMeasures.get(actualAlgorithm).containsKey(beautifulName))
            {   // Initialize if not exists
                algorithmMeasures.get(actualAlgorithm).put(beautifulName, measureValue);
            }
            else
            {   // update if exists
                Double newValue = algorithmMeasures.get(actualAlgorithm).get(beautifulName) + measureValue;
                algorithmMeasures.get(actualAlgorithm).put(beautifulName, newValue);
            }
        }
        else
        {
            HashMap tmp = new HashMap<String, Double>();
            tmp.put(beautifulName, measureValue);
            algorithmMeasures.put( actualAlgorithm, tmp );
        }
        
        if(!sortedMeasures.contains(beautifulName))
        {
            sortedMeasures.add(beautifulName);
        }
        
    }
    
    private void addSeed()
    {
        if(algorithmSeeds.containsKey(actualAlgorithm))
        {
            Integer newValue = algorithmSeeds.get(actualAlgorithm) + 1;
            algorithmSeeds.put(actualAlgorithm, newValue);
        }
        else
        {
            algorithmSeeds.put( actualAlgorithm, 1 );
        }
        
    }
    
    private void calcAvgRulesBySeed()
    {
        for (Map.Entry<String, Integer> entry : algorithmSeeds.entrySet()) {
            String alg = entry.getKey();
            Integer value = entry.getValue();
            
            Double rules = algorithmMeasures.get(alg).get("$\\#R$");
            rules = rules / value;
            algorithmMeasures.get(alg).put("$\\#R$", rules);
        }
    }
    
    
    private Double processMeasure(Element rule, String measureName, String beautifulName)
    {
        Double measureValue = 0.0;
        if(rule.hasAttribute(measureName))
        {
            measureValue = Double.parseDouble( rule.getAttribute(measureName) );
        }
        
        putMeasure(beautifulName, measureValue);
        
        return measureValue;
    }
    
    private void addRulesNumberToAlgorithm(int numRules)
    {
        if(algorithmTotalRules.containsKey(actualAlgorithm))
        {
            Double now = algorithmTotalRules.get(actualAlgorithm) + numRules;
            algorithmTotalRules.put(actualAlgorithm, now);
        }
        else
        {
            algorithmTotalRules.put(actualAlgorithm, numRules*1.0);
        }
    }
    
    private void calcMeans()
    {
        //HashMap<String, Double> measuresFirst = algorithmMeasures.get(actualAlgorithm);
        for (Map.Entry<String, HashMap<String, Double> > entry : algorithmMeasures.entrySet()) {
            String alg = entry.getKey();
            HashMap<String, Double> measuresFirst = entry.getValue();
            
            for (Map.Entry<String, Double> measure : measuresFirst.entrySet())
            {
                String measureName = measure.getKey();
                if(!excludedFromAverage.contains(measureName))
                {
                    Double measureValue = measure.getValue() / algorithmTotalRules.get(alg);
                    algorithmMeasures.get(alg).put(measureName, measureValue);
                }
            }
        }
    }
    
    
    public void writeToFile(String outName) throws FileNotFoundException, UnsupportedEncodingException
    {
        calcMeans();
        calcAvgRulesBySeed();
        
        PrintWriter writer = new PrintWriter(outName, "UTF-8");
        
        // Get number of measures and header for the table from first element
        // "\\hline \n  \\textbf{Algorithm} & \\textbf{Soporte} & \\textbf{Confianza} \\\\ \\hline \\hline"
        String tableHeader = "\\textbf{Algorithm} ";
        
        
        for (String measureName : sortedMeasures)
        {
            tableHeader += "& \\textbf{"+ measureName +"} ";
        }
        
        tableHeader += "\\\\ \\hline \\hline";
        
        Integer measuresNumber = sortedMeasures.size();
        
        // Table columns are:
        // Algortihm || Support | Confianze | .....
        // Algorithm1 & 0.9 & 0.88 & .....
        // "\\begin{tabular}{ l || c | c | ...... }"
        String tableSettings = "\\begin{table*}[ht!]\n" +
                                "\\centering\n" +
                                "\\caption[Caption]{Something}\n" +
                                "\\label{table:label}\n" +
                                "\\scalebox{0.90}{ \n";
        tableSettings += "\\begin{tabular}{ l | ";
        for(int i=0;i<measuresNumber;i++)
        {
            tableSettings += "| c ";
        }
        tableSettings += "";
        tableSettings += "} \n \\hline";
        
        // Write Info, table settings and table header
        writer.println("\\documentclass{article} \n \\begin{document} \n\n % Copy only the table to your LaTeX file");
        writer.println(tableSettings);
        writer.println(tableHeader);
        
        
        // Write table content
        for (Map.Entry<String, HashMap<String, Double> > alg : algorithmMeasures.entrySet())
        {
            String algName = alg.getKey();
            HashMap<String, Double> measures = alg.getValue();
            
            // Parse algorithm name to show it correctly
            String aName = algName.substring(0, algName.length()-1);
            int startAlgName = aName.lastIndexOf("/");
            aName = aName.substring(startAlgName + 1);
            
            String algContent = aName + " ";
            
            for(int i=0;i<sortedMeasures.size();i++)
            {
                String m = String.format( "%.2f", measures.get(sortedMeasures.get(i)) );
                algContent += "& "+ m + " ";
            }
            
            algContent += "\\\\ \\hline";
            
            writer.println(algContent);
        }
        
        writer.println("\\end{tabular}}\n\\end{table*}  \n\n \\end{document}");
        
        writer.flush();
        writer.close();
    }
    
    
    // Aux functions..
    private String join(ArrayList<String> in, String separator)
    {
        String out = "";
        for (String i : in)
        {
            out += i + separator;
        }
        
        return out;
    }
    
    private ArrayList concat(ArrayList<String> a, ArrayList<String> b)
    {
        ArrayList<String> out = new ArrayList();
        for (String i : a)
        {
            out.add(i);
        }
        for (String i : b)
        {
            out.add(i);
        }
        
        return out;
    }
    
    
    
    
    
    // Sort functions
    private static HashMap sortByValues(HashMap map) { 
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                   .compareTo(((Map.Entry) (o2)).getValue());
             }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
               Map.Entry entry = (Map.Entry) it.next();
               sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
   }
    
   private static HashMap sortByValuesArray(HashMap map) { 
        List list = new LinkedList(map.entrySet());
        // Defined Custom Comparator here
        Collections.sort(list, new Comparator() {
             public int compare(Object o1, Object o2) {
                ArrayList<Integer> a1 = (ArrayList<Integer>) ((Map.Entry) (o1)).getValue();
                ArrayList<Integer> a2 = (ArrayList<Integer>) ((Map.Entry) (o2)).getValue();
                return ((Comparable) a1.get(0))
                   .compareTo(a2.get(0));
             }
        });

        // Here I am copying the sorted list in HashMap
        // using LinkedHashMap to preserve the insertion order
        HashMap sortedHashMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
               Map.Entry entry = (Map.Entry) it.next();
               sortedHashMap.put(entry.getKey(), entry.getValue());
        } 
        return sortedHashMap;
   }
   
   // Find smaller value in a HashMap. Used to find min support
   private String findSmallerValue(HashMap<String, String> map)
   {
       double valueNumber = Double.MAX_VALUE;
       String smallerElement = "";
       for (Map.Entry<String, String> entry : map.entrySet())
       {
           String key = entry.getKey();
           String value = entry.getValue();
           if(value != "" && Double.parseDouble(value) < valueNumber)
           {
               smallerElement = key;
               valueNumber = Double.parseDouble(value);
           }
       }
       
       return smallerElement;
   }
      
}
