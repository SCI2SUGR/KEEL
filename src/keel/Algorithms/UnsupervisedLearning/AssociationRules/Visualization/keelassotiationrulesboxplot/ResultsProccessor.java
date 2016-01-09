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

package keel.Algorithms.UnsupervisedLearning.AssociationRules.Visualization.keelassotiationrulesboxplot;

import java.awt.Color;
import java.awt.Font;
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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author alberto
 */
@SuppressWarnings({"deprecation", "unused", "unchecked"})
public class ResultsProccessor {
  private Document readedDocument = null;
    
    //private HashMap<String, HashMap<String, Double> > algorithmMeasures = null;
    private HashMap<String, HashMap<String, ArrayList<Double> > > algorithmMeasures = null;
    private HashMap<String, Double> algorithmTotalRules = null;
    
    // measures to exclude from average calc
    private ArrayList<String> excludedFromAverage = null;
    
    private String actualAlgorithm;
    private Integer actualRulesNumber = 0;
    
    public ResultsProccessor()
    {
        algorithmMeasures = new HashMap<String, HashMap<String, ArrayList<Double> > >();
        algorithmTotalRules = new HashMap<String, Double>();
        
        // Excluded
        excludedFromAverage = new ArrayList<String>();
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
        putMeasure("Num_Rules", actualRulesNumber*1.0);
        
        // Update rules number to calculate averages later
        addRulesNumberToAlgorithm(actualRulesNumber);
        
        if(rules != null && rules.getLength() > 0)
        {
                for(int i = 0 ; i < rules.getLength();i++)
                {
                        // Get rule number i
                        Element rule = (Element)rules.item(i);
                        
                        // basic Measures
                        support = processMeasure(rule, "rule_support", "Rule_Support");
                        antSupport = processMeasure(rule, "antecedent_support", "Antecedent Support");
                        conSuport = processMeasure(rule, "consequent_support", "Consequent Support");
                        
                        /*
                        confidence = processMeasure(rule, "confidence", "Confidence");
                        processMeasure(rule, "lift", "Lift");
                        processMeasure(rule, "conviction", "Conviction");
                        processMeasure(rule, "certainFactor", "CF");
                        processMeasure(rule, "netConf", "NetConf");
                        processMeasure(rule, "yulesQ", "yulesQ");
                        processMeasure(rule, "nAttributes", "Num_Attributes");
                        */
                        
                        putMeasure("Lift", mc.calculateLift(antSupport, conSuport, support));
                        putMeasure("Confidence", mc.calculateConfidence(antSupport, conSuport, support));
                        putMeasure("CF", mc.calculateCF(antSupport, conSuport, support));
                        putMeasure("Conviction", mc.calculateConviction(antSupport, conSuport, support));
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
                ArrayList<Double> newList = new ArrayList<Double>(); newList.add(measureValue);
                algorithmMeasures.get(actualAlgorithm).put(beautifulName, newList);
            }
            else
            {   // update if exists
                algorithmMeasures.get(actualAlgorithm).get(beautifulName).add(measureValue);
            }
        }
        else
        {
            HashMap<String, ArrayList<Double> > tmp = new HashMap<String, ArrayList<Double> >();
            ArrayList<Double> newList = new ArrayList<Double>(); newList.add(measureValue);
            tmp.put(beautifulName, newList);
            algorithmMeasures.put( actualAlgorithm, tmp );
        }
    }
    
    
    private Double processMeasure(Element rule, String measureName, String beautifulName)
    {
        Double measureValue = 0.0;
        if(rule.hasAttribute(measureName))
        {
            measureValue = Double.parseDouble( rule.getAttribute(measureName) );
        }
        
        // Here, we will add the measure to list, and then we will create the data set (when we create the boxplot)
        
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
    
    /*
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
    */
    
    
    public void writeToFile(String outName) throws FileNotFoundException, UnsupportedEncodingException, IOException
    {
        //calcMeans();
        
        // Create JFreeChart Dataset
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset( );
        
        
        HashMap<String, ArrayList<Double> > measuresFirst = algorithmMeasures.entrySet().iterator().next().getValue();
        for (Map.Entry<String, ArrayList<Double> > measure : measuresFirst.entrySet())
        {
            String measureName = measure.getKey();
            //Double measureValue = measure.getValue();
            dataset.clear();
            
            for (Map.Entry<String, HashMap<String, ArrayList<Double> >> entry : algorithmMeasures.entrySet())
            {
                String alg = entry.getKey();
                ArrayList<Double> measureValues = entry.getValue().get(measureName);
                
                // Parse algorithm name to show it correctly
                String aName = alg.substring(0, alg.length()-1);
                int startAlgName = aName.lastIndexOf("/");
                aName = aName.substring(startAlgName + 1);
                
                dataset.add(measureValues, aName, measureName);
            }
            
            // Tutorial: http://www.java2s.com/Code/Java/Chart/JFreeChartBoxAndWhiskerDemo.htm
            final CategoryAxis xAxis = new CategoryAxis("Algorithm");
            final NumberAxis yAxis = new NumberAxis("Value");
            yAxis.setAutoRangeIncludesZero(false);
            final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
            
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
            
            renderer.setMeanVisible(false);
            renderer.setFillBox(false);
            renderer.setToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
            final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

            Font font = new Font("SansSerif", Font.BOLD, 10);
            //ChartFactory.setChartTheme(StandardChartTheme.createLegacyTheme());
            JFreeChart jchart = new JFreeChart("Assotiation Rules Measures - BoxPlot", font, plot, true);
            //StandardChartTheme.createLegacyTheme().apply(jchart);
         
            int width = 640 * 2; /* Width of the image */
            int height = 480 * 2; /* Height of the image */ 

            // JPEG
            File chart = new File( outName + "_" + measureName + "_boxplot.jpg" );
            ChartUtilities.saveChartAsJPEG( chart , jchart , width , height );

            // SVG
            SVGGraphics2D g2 = new SVGGraphics2D(width, height);
            Rectangle r = new Rectangle(0, 0, width, height);
            jchart.draw(g2, r);
            File BarChartSVG = new File( outName + "_" + measureName + "_boxplot.svg" );
            SVGUtils.writeToSVG(BarChartSVG, g2.getSVGElement());
        }
        
    }
      
}
