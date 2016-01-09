package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.keelassotiationrulesbarchart;

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


import java.awt.Color;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
    
    private String actualAlgorithm;
    private Integer actualRulesNumber = 0;
    
    public ResultsProccessor()
    {
        algorithmMeasures = new HashMap();
        algorithmTotalRules = new HashMap();
        
        algorithmSeeds = new HashMap<String, Integer>();
        
        // Excluded
        excludedFromAverage = new ArrayList();
        excludedFromAverage.add("$\\#R$");
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
                    support = processMeasure(rule, "rule_support", "Rule_Support");
                    confidence = processMeasure(rule, "confidence", "Confidence");
                    antSupport = processMeasure(rule, "antecedent_support", "Antecedent Support");
                    conSuport = processMeasure(rule, "consequent_support", "Consequent Support");

                    /*
                    processMeasure(rule, "lift", "Lift");
                    processMeasure(rule, "conviction", "Conviction");
                    processMeasure(rule, "certainFactor", "CF");
                    processMeasure(rule, "netConf", "NetConf");
                    processMeasure(rule, "yulesQ", "yulesQ");
                    processMeasure(rule, "nAttributes", "Num_Attributes"); // ?
                    */

                    putMeasure("Lift", mc.calculateLift(antSupport, conSuport, support));
                    putMeasure("Confidence", mc.calculateConfidence(antSupport, conSuport, support));
                    putMeasure("CF", mc.calculateCF(antSupport, conSuport, support));
                    putMeasure("Conviction", mc.calculateConviction(antSupport, conSuport, support));

                    // Av_div, %Trans
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
    
    
    public void writeToFile(String outName) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        calcMeans();
        calcAvgRulesBySeed();
        
        // Create JFreeChart Dataset
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        
        
        HashMap<String, Double> measuresFirst = algorithmMeasures.entrySet().iterator().next().getValue();
        for (Map.Entry<String, Double> measure : measuresFirst.entrySet())
        {
            String measureName = measure.getKey();
            //Double measureValue = measure.getValue();
            dataset.clear();
            
            for (Map.Entry<String, HashMap<String, Double>> entry : algorithmMeasures.entrySet())
            {
                String alg = entry.getKey();
                Double measureValue = entry.getValue().get(measureName);
                
                // Parse algorithm name to show it correctly
                String aName = alg.substring(0, alg.length()-1);
                int startAlgName = aName.lastIndexOf("/");
                aName = aName.substring(startAlgName + 1);
                
                dataset.addValue(measureValue, aName, measureName);
                
                ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
                JFreeChart barChart = ChartFactory.createBarChart("Assotiation Rules Measures", measureName, measureName, dataset, PlotOrientation.VERTICAL, true, true, false);
                StandardChartTheme.createLegacyTheme().apply(barChart);
                
                CategoryItemRenderer renderer = barChart.getCategoryPlot().getRenderer();
                
                // Black and White
                int numItems = algorithmMeasures.size();
                for(int i=0;i<numItems;i++)
                {
                    Color color = Color.DARK_GRAY;
                    if(i%2 == 1)
                    {
                        color = Color.LIGHT_GRAY;
                    }
                    renderer.setSeriesPaint(i, color);
                    renderer.setSeriesOutlinePaint(i, Color.BLACK);
                }
                
                
                int width = 640 * 2; /* Width of the image */
                int height = 480 * 2; /* Height of the image */ 
                
                // JPEG
                File BarChart = new File( outName + "_" + measureName + "_barchart.jpg" );
                ChartUtilities.saveChartAsJPEG( BarChart , barChart , width , height );
                
                // SVG
                SVGGraphics2D g2 = new SVGGraphics2D(width, height);
                Rectangle r = new Rectangle(0, 0, width, height);
                barChart.draw(g2, r);
                File BarChartSVG = new File( outName + "_" + measureName + "_barchart.svg" );
                SVGUtils.writeToSVG(BarChartSVG, g2.getSVGElement());
            }
        }
        /*
        for (Map.Entry<String, HashMap<String, Double>> entry : algorithmMeasures.entrySet())
        {
            String alg = entry.getKey();
            HashMap<String, Double> measures = entry.getValue();
            
            for (Map.Entry<String, Double> entry1 : measures.entrySet())
            {
                String measureName = entry1.getKey();
                Double measureValue = entry1.getValue();
                
                dataset.addValue(measureValue, alg, measureName);
            }
        }
                */
        
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
           if(!"".equals(value) && Double.parseDouble(value) < valueNumber)
           {
               smallerElement = key;
               valueNumber = Double.parseDouble(value);
           }
       }
       
       return smallerElement;
   }
      
}
