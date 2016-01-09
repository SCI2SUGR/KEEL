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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
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
public class KeelToPMMLFuzzyProcessor {
    
    private Document readedDocument = null;
    
    private HashMap<String, Integer> items = null;
    private HashMap<String, ArrayList<Integer>> itemsets = null;
    private ArrayList<ArrayList<Integer>> rules = null;
    
    // rules measures such as: confidence, support.. We save name of measure and value
    // The name will be used directly in the PMML file..
    private ArrayList<ArrayList<StringPair > > rulesMeasures = null;
    private HashMap<String, String> itemsetsSupport = null;
    
    private String itemNameValueSeparator;
    private String itemsetIdSeparator;
    
    private int itemCounter;
    private int itemsetCounter;
    
    // Fuzzy atributes with the values
    // attribute name -> LABEL -> VALUE (Each attribute has multiple labels)
    private HashMap<String, HashMap<String, String> > attributeValues = null;
    
    public KeelToPMMLFuzzyProcessor(String itemNVSep, String itemsetSep)
    {
        items = new HashMap();
        itemsets = new HashMap();
        rules = new ArrayList();
        rulesMeasures = new ArrayList();
        itemsetsSupport = new HashMap();
        itemNameValueSeparator = itemNVSep;
        itemsetIdSeparator = itemsetSep;
        attributeValues = new HashMap<String, HashMap<String, String> >();
    }
    
    private void resetProcessor()
    {
        itemsetsSupport.clear();
        rulesMeasures.clear();
        items.clear();
        itemsets.clear();
        rules.clear();
        itemCounter = 0;
        itemsetCounter = 0;
        attributeValues.clear();
    }
    
    public void parseXmlFile(String traFile, String tstFile, String txtFile, String outName)
    {
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        resetProcessor();
        try {

                //Using factory get an instance of document builder
                DocumentBuilder db = dbf.newDocumentBuilder();
                
                // parse tst Fuzzy to get values for attributes
                readedDocument = db.parse(tstFile);
                parseDocumentTst();

                //parse using builder to get DOM representation of the XML file
                readedDocument = db.parse(txtFile);
                parseDocumentTxt();
                
                //parse using builder to get DOM representation of the XML file
                // OJO: Los ficheros generados por KEEL son ficheros mal formados
                // los elementos "rules" cierran con /> y despues de decir los consecuentes con </rules>..
                // hay que quitar el /> al abrir la etiqueta.
                readedDocument = db.parse(traFile);
                parseDocumentTra();
                
                
                // Sort by int ID to see all easy
                items = sortByValues(items);
                itemsets = sortByValuesArray(itemsets);
                
                writeToFile(outName);

        }catch(ParserConfigurationException pce) {
                pce.printStackTrace();
        }catch(SAXException se) {
                se.printStackTrace();
        }catch(IOException ioe) {
                ioe.printStackTrace();
        }
    }
    
    private void parseDocumentTst()
    {   // Attributes with the values for each LABEL
        //get the root element
        Element docEle = readedDocument.getDocumentElement();

        // Get all rules
        NodeList attributes = docEle.getElementsByTagName("attribute");
        //// System.out.println(rules.getLength());
        
        if(attributes != null && attributes.getLength() > 0)
        {
                for(int i = 0 ; i < attributes.getLength();i++)
                {
                        // Get rule number i
                        Element attr = (Element)attributes.item(i);
                        String attrName = attr.getAttribute("name");
                        String attrType = attr.getAttribute("Type");
                        NodeList values = attr.getElementsByTagName("value");
                        
                        
                        for(int j=0;j<values.getLength();j++)
                        {
                            Element v = (Element)values.item(j);
                            
                            String labelName = "";
                            String valuesString = "";
                            
                            if(attrType.toLowerCase().startsWith("nominal"))
                            {
                                valuesString = v.getAttribute("x");
                                labelName = valuesString;
                            }
                            else
                            {
                                labelName = v.getAttribute("labelname");
                                String v1 = v.getAttribute("x1");
                                String v2 = v.getAttribute("x2");
                                String v3 = v.getAttribute("x3");
                                valuesString = v1 + ";" + v2 + ";" + v3;
                            }

                            if(attributeValues.containsKey(attrName))
                            {
                                attributeValues.get(attrName).put(labelName, valuesString);
                            }
                            else
                            {
                                HashMap labelValues = new HashMap<String, String>();
                                labelValues.put(labelName, valuesString);
                                attributeValues.put(attrName, labelValues);
                            }
                        }
                        
                }
        }
        
        //// System.out.println(items.toString());
        //// System.out.println(itemsets.toString());
    }
    
    private void parseDocumentTra()
    {
        //get the root element
        Element docEle = readedDocument.getDocumentElement();

        // Get all rules
        NodeList rules = docEle.getElementsByTagName("rule");
        //// System.out.println(rules.getLength());
        
        ArrayList<String> itemsetAntecedents = new ArrayList();
        ArrayList<String> itemsetConsequents = new ArrayList();
        
        if(rules != null && rules.getLength() > 0)
        {
                for(int i = 0 ; i < rules.getLength();i++)
                {
                        // Get rule number i
                        Element rule = (Element)rules.item(i);
                        itemsetAntecedents = processAntecedents(rule);
                        itemsetConsequents = processConsequents(rule);
                        processItemset(itemsetAntecedents, itemsetConsequents);
                        processRule(itemsetAntecedents, itemsetConsequents);
                }
        }
        
        //// System.out.println(items.toString());
        //// System.out.println(itemsets.toString());
    }
    
    private void parseDocumentTxt()
    {
        /*
            COMENTARIO PARA FUZZY:
            En fuzzy tengo las medidas en un fichero .txt, de nombre igual al tst/tra pero añadiendo e0 antes de la extension
            Y en el TST lo que tengo es para cada atributo los valores fuzzy de su LABEL
            Para fuzzy deberia leer el TST antes, preparar los valores para cada label (Hashmap?) y despues leer el TXT (Que es como el TST de los no fuzzy, con las medidas)
            El TXT debe ser el primero en rpocesar, despues el TRA (Para montar los atributos con sus valores) y finalmente el TST para las medidas
        */
        //get the root element
        Element docEle = readedDocument.getDocumentElement();

        // Get all rules
        NodeList rules = docEle.getElementsByTagName("rule");
        // System.out.println(rules.getLength());
        Double support = 0.0, confidence = 0.0, antSupport = 0.0, conSuport = 0.0;
        MeasuresCalculator mc = new MeasuresCalculator();
        
        ArrayList<String> itemsetAntecedents = new ArrayList();
        ArrayList<String> itemsetConsequents = new ArrayList();
        
        if(rules != null && rules.getLength() > 0)
        {
                for(int i = 0 ; i < rules.getLength();i++)
                {
                    // Get rule number i
                    Element rule = (Element)rules.item(i);
                    rulesMeasures.add(new ArrayList());
                    // basic Measures
                    support = processMeasure(rule, "rule_support", "support");
                    antSupport = processMeasure(rule, "antecedent_support", "antecedent_support");
                    conSuport = processMeasure(rule, "consequent_support", "consequent_support");

                    /*
                    processMeasure(rule, "lift");
                    processMeasure(rule, "confidence");
                    processMeasure(rule, "conviction");
                    processMeasure(rule, "certainFactor");
                    processMeasure(rule, "netConf");
                    */

                    putMeasure("lift", mc.calculateLift(antSupport, conSuport, support));
                    putMeasure("confidence", mc.calculateConfidence(antSupport, conSuport, support));
                    putMeasure("certainFactor", mc.calculateCF(antSupport, conSuport, support));
                    putMeasure("conviction", mc.calculateConviction(antSupport, conSuport, support));
                }
        }
        
        // System.out.println(rulesMeasures.toString());
        // System.out.println(rulesMeasures.size());
    }
    
    private ArrayList processAntecedents(Element rule)
    {
        NodeList antecedents = rule.getElementsByTagName("antecedents");
        ArrayList<String> itemset = new ArrayList();
        
        if(antecedents != null && antecedents.getLength() > 0)
        {
            Element antecedent = (Element)antecedents.item(0);
            
            // Get attributes (items)
            NodeList attributes = antecedent.getElementsByTagName("attribute");
            if(attributes != null && attributes.getLength() > 0)
            {
                for(int i=0;i<attributes.getLength();i++)
                {
                    Element attr = (Element)attributes.item(i);
                    
                    String name = attr.getAttribute("name");
                    String value = attr.getAttribute("value");
                    
                    String FuzzyValues = attributeValues.get(name).get(value);
                    
                    String itemId = name + itemNameValueSeparator + FuzzyValues;
                    if(!items.containsKey(itemId))
                    {
                        items.put(itemId, itemCounter);
                        itemCounter++;
                    }
                    // Save item to generate itemsets after..
                    itemset.add(itemId);
                }
            }
        }
        
        return itemset;
    }
    
    
    private ArrayList processConsequents(Element rule)
    {
        NodeList antecedents = rule.getElementsByTagName("consequents");
        ArrayList<String> itemset = new ArrayList();
        
        if(antecedents != null && antecedents.getLength() > 0)
        {
            Element antecedent = (Element)antecedents.item(0);
            
            // Get attributes (items)
            NodeList attributes = antecedent.getElementsByTagName("attribute");
            if(attributes != null && attributes.getLength() > 0)
            {
                for(int i=0;i<attributes.getLength();i++)
                {
                    Element attr = (Element)attributes.item(i);
                    
                    String name = attr.getAttribute("name");
                    String value = attr.getAttribute("value");
                    
                    String FuzzyValues = attributeValues.get(name).get(value);
                    
                    String itemId = name + itemNameValueSeparator + FuzzyValues;
                    if(!items.containsKey(itemId))
                    {
                        items.put(itemId, itemCounter);
                        itemCounter++;
                    }
                    // Save item to generate itemsets after..
                    itemset.add(itemId);
                }
            }
        }
        
        return itemset;
    }
    
    private void processItemset(ArrayList<String> ant, ArrayList<String> con)
    {
        String itemsetId = "";
        // antecedents and consecuentes are a different itemset !
        // and then we have that antedents and consequents are one itemset
        itemsetId = join(ant, itemsetIdSeparator);
        if(!itemsets.containsKey(itemsetId))
        {
            ArrayList<Integer> ids = new ArrayList();
            ids.add(itemsetCounter); // First value is the id of the itemset!
            for(String i : ant)
            {
                // Itemset is a hashmap whose key is the concatenation of items id
                // and the value is an array of ints, which are the integer id of the item
                ids.add(items.get(i));
            }
            itemsets.put(itemsetId, ids);
            itemsetCounter++;
        }
        
        itemsetId = join(con, itemsetIdSeparator);
        if(!itemsets.containsKey(itemsetId))
        {
            ArrayList<Integer> ids = new ArrayList();
            ids.add(itemsetCounter); // First value is the id of the itemset!
            for(String i : con)
            {
                // Itemset is a hashmap whose key is the concatenation of items id
                // and the value is an array of ints, which are the integer id of the item
                ids.add(items.get(i));
            }
            itemsets.put(itemsetId, ids);
            itemsetCounter++;
        }
        
        ArrayList<String> concatAC = concat(ant, con);
        itemsetId = join(concatAC, itemsetIdSeparator);
        if(!itemsets.containsKey(itemsetId))
        {
            ArrayList<Integer> ids = new ArrayList();
            ids.add(itemsetCounter); // First value is the id of the itemset!
            for(String i : concatAC)
            {
                // Itemset is a hashmap whose key is the concatenation of items id
                // and the value is an array of ints, which are the integer id of the item
                ids.add(items.get(i));
            }
            itemsets.put(itemsetId, ids);
            itemsetCounter++;
        }
    }
    
    private void processRule(ArrayList<String> ant, ArrayList<String> con)
    {
        // we are going to process our rule with its antecedents and consequents
        String antecedentsId = join(ant, itemsetIdSeparator);
        String consequentsId = join(con, itemsetIdSeparator);
        // Total itemset
        ArrayList<String> concatAC = concat(ant, con);
        String totalItemsetId = join(concatAC, itemsetIdSeparator);
        
        // we save antecedents and consequents integer id on rules array
        rules.add(new ArrayList());
        // itemsets value contains an ints array with:
        //  itemset id, items id.. (firts element(0) contains itemset id , so we add it to the rule)
        rules.get(rules.size()-1).add(itemsets.get(antecedentsId).get(0));
        rules.get(rules.size()-1).add(itemsets.get(consequentsId).get(0));
        
        // Finally, we are going to save antecedent support, because is the unique way to get itemsets support
        // element 2 of rules measures is the "antecedent_support"
        itemsetsSupport.put(antecedentsId, rulesMeasures.get(rules.size()-1).get(2).getValue()); // Antecedent Support
        itemsetsSupport.put(consequentsId, rulesMeasures.get(rules.size()-1).get(3).getValue()); // Antecedent Support
        itemsetsSupport.put(totalItemsetId, rulesMeasures.get(rules.size()-1).get(0).getValue()); // Rule Support = Total Itemset Support
    }
    
    private Double processMeasure(Element rule, String measureName, String beautifulName)
    {
        String measureValue = rule.getAttribute(measureName);
        rulesMeasures.get(rulesMeasures.size()-1).add(new StringPair(beautifulName, measureValue));
        
        return Double.parseDouble(measureValue);
    }
    
    private void putMeasure(String measureName, Double measureValue)
    {
        String value = measureValue.toString();
        rulesMeasures.get(rulesMeasures.size()-1).add(new StringPair(measureName, value));
    }
    
    private void writeToFile(String outName) throws FileNotFoundException, UnsupportedEncodingException
    {
        String minSupport = itemsetsSupport.get(findSmallerValue(itemsetsSupport));
        String minConfidence = findMinConfidence();
        PrintWriter writer = new PrintWriter(outName, "UTF-8");
        // Writting Info
        writer.println("<PMML xmlns=\"http://www.dmg.org/PMML-4_1\" version=\"4.1\">\n" +
                        "  <Header copyright=\"www.dmg.org\" description=\"Fuzzy Assotiation Rules\"/>\n" +
                        "  <AssociationModel functionName=\"associationRules\" numberOfTransactions=\"Undefined\" numberOfItems=\""+items.size()+"\" minimumSupport=\""+minSupport+"\" minimumConfidence=\""+minConfidence+"\" numberOfItemsets=\""+itemsets.size()+"\" numberOfRules=\""+rules.size()+"\">\n" +
                        "");
        
        writer.println("\n\n<!-- Items -->");
        // Write items
        for (Map.Entry<String, Integer> entry : items.entrySet())
        {
            String key = entry.getKey();
            Integer id = entry.getValue();
            writer.println("<Item id=\""+id+"\" value=\""+key+"\"/>");
        }
        
        writer.println("\n\n<!-- Itemsets -->");
        // Write itemsets
        for (Map.Entry<String, ArrayList<Integer>> entry : itemsets.entrySet())
        {
            String key = entry.getKey();
            ArrayList<Integer> ids = entry.getValue();
            String support = itemsetsSupport.get(key);
            int n = ids.size()-1;
            // First element is the itemset id
            writer.println("<Itemset id=\""+ids.get(0)+"\" support=\""+ support +"\" numberOfItems=\""+n+"\">");
            for (int i=1;i<ids.size();i++)
            {
                Integer id = ids.get(i);
                writer.println("<ItemRef itemRef=\""+id+"\"/>");
            }
            writer.println("</Itemset>");
        }
        
        writer.println("\n\n<!-- Assotiation Rules -->");
        // Write rules
        for(int i=0;i<rules.size();i++)
        {
            String measures = "";
            for(StringPair measure: rulesMeasures.get(i))
            {
                measures += measure.getKey() + "=" + '"' + measure.getValue() + "\" ";
            }
            
            writer.println("<AssociationRule id=\""+i+"\" "+ measures +" antecedent=\""+ rules.get(i).get(0) +"\" consequent=\""+ rules.get(i).get(1) +"\"/>");
        }
        
        // Finish file
        writer.println("</AssociationModel>\n" +
                        "</PMML>");
        
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
   
   // Funtion to find min cofidence looking for minimun confidence value among all the rules
   private String findMinConfidence()
   {
       Double smallerConf = Double.MAX_VALUE;
       for (int i=0; i < rulesMeasures.size(); i++)
       {
           double act = Double.parseDouble(rulesMeasures.get(i).get(1).getValue());
           if(act < smallerConf)
           {
               smallerConf = act;
           }
       }
       
       return smallerConf.toString();
   }
    
}
