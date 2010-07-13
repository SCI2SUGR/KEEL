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
* @author Written by Luciano Sanchez (University of Oviedo) 24/02/2005
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/

package keel.Algorithms.Statistical_Tests.Shared;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Comment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;


import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;


public class outputFile {
	/**
	* <p>
	* Appends the results of an experiment to a file
	* </p>
	*/
	
	/**
	* <p>
	* This method appends the results of an experiment to a file
	* @param nameDataset Dataset name
	* @param namesAlgorithms Algorithms names
	* @param mean Mean of each algorithm 
	* @param median Median of each algorithm
	* @param pvalues Cubic matrix of p-values, indexed by confidence, algorithm, algorithm
	* @param sample Error samples
	* @param confidence 1.0 - test levels typically 0.9, 0.95 and 0.99
	* </p>
	*/  
  public static void appendResults(String nameDataset,
								   String namesAlgorithms[],
								   double[] mean,
								   double[] median,
								   double[][][] pvalues,
								   double[][] sample,
								   double[] confidence) {
	
	String nameResult = "statResult.xml";
	String nameBackup = "statBackup.xml";
	
	File docFile = new File(nameResult);
	File backupFile = new File(nameBackup);
	
    Document doc = null;
	
	if (!docFile.exists()) {
	  try {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		doc = builder.newDocument();
		
		// Insert the root element node
		Element rootElement = doc.createElement("experimentation");
		doc.appendChild(rootElement);
		
		
	  } catch (Exception e) {
		System.out.println("Error creating empty document");
		e.printStackTrace();
	  }	
	} else try {
	  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  doc = db.parse(docFile);
	  System.out.println("Document parsed");
	} catch (java.io.IOException e) {
	  System.out.println("File not found");
	} catch (Exception e) {
	  System.out.print("Problem parsing the file.");
	  e.printStackTrace();
	}
	
	// Add the results of a new dataset to the document
	
	// Create the dataset section
	Element datasetElement = doc.createElement("datasetOutput");
	
	// Insert a comment
	Comment comment = doc.createComment("Results of the dataset "+nameDataset);
	datasetElement.appendChild(comment);
	
	int nOfAlg=namesAlgorithms.length;
	
	// Add attributes
	datasetElement.setAttribute("numberOfAlgorithms", (new Integer(nOfAlg)).toString());
	datasetElement.setAttribute("datasetName", nameDataset);
	
	
	// Create a new subsection
	Element meanElement = doc.createElement("mean");
	
	String textMean=new String();
	for (int i=0;i<nOfAlg;i++) textMean+=(new Double(mean[i])).toString()+" ";
	
	// Add a text node to the section
	meanElement.appendChild(doc.createTextNode(textMean));
	
	// Insert the mean subsection
	datasetElement.appendChild(meanElement);
	
	// Create a new subsection
	Element medianElement = doc.createElement("median");
	
	String textMedian=new String();
	for (int i=0;i<nOfAlg;i++) textMedian+=(new Double(median[i])).toString()+" ";
	
	// Add a text node to the section
	medianElement.appendChild(doc.createTextNode(textMedian));
	
	// Insert the mean subsection
	datasetElement.appendChild(medianElement);
	
	// Create pvalues subsections
	for (int cnf=0;cnf<confidence.length;cnf++) {
	  
	  Element pvaluesElement = doc.createElement("pvalues");
	  pvaluesElement.setAttribute("confidenceLevel", (new Double(confidence[cnf])).toString());
	  
	  String textPValue=new String();
	  for (int i=0;i<nOfAlg;i++) 
		for (int j=i+1;j<nOfAlg;j++)
		  textPValue+=(new Double(pvalues[cnf][i][j])).toString()+" ";
	  
	  // Add a text node to the section
	  pvaluesElement.appendChild(doc.createTextNode(textPValue));
	  
	  // Insert the mean subsection
	  datasetElement.appendChild(pvaluesElement);
	}
	
	
	
	// Create a new subsection
	Element sampleElement = doc.createElement("sample");
	
	String textSample=new String();
	for (int i=0;i<nOfAlg;i++) 
	  for (int j=0;j<sample[i].length;j++)
		textSample+=(new Double(sample[i][j])).toString()+" ";
	
	// Add a text node to the section
	sampleElement.appendChild(doc.createTextNode(textSample));
	
	// Create the attribute "number of folds"
	sampleElement.setAttribute("folds",(new Integer(sample[0].length)).toString());
	
	// Insert the samples subsection
	datasetElement.appendChild(sampleElement);
	
	
	// Insert an element
	doc.getDocumentElement().appendChild(datasetElement);
	
	
	// Delete the backup file
	if (backupFile.exists()) backupFile.delete();
	
	// Rename the experiments file
 	docFile.renameTo(backupFile);
	
	// Save the document
    saveDoc(nameResult,doc);
	
	// DBG: Process all nodes in the document
	System.out.println("DBG: document");
	visit(doc, 0);
	
	
  }
  
	/**
	* <p>
	* This method saves the document using a XSL transformation without
	* a style sheet to create an  "identity transformation" that outputs to a file
	* @param outputURL URL to save the document
	* @param doc The document to be saved
	* </p>
	*/
  static void saveDoc(String outputURL,Document doc) {
	try{
	  
	  DOMSource source = new DOMSource(doc);
	  StreamResult result = new StreamResult(new FileOutputStream(outputURL));
	  
	  TransformerFactory transFactory = TransformerFactory.newInstance();
	  Transformer transformer = transFactory.newTransformer();  
	  transformer.transform(source, result);     
	  
	} catch (Exception e) {
	  e.printStackTrace();
	}
  }
  
	/**
	* <p>
	* This method visits all the nodes in our DOM tree
	* @param node Node to visit
	* @param level Level of the node
	* </p>
	*/
  public static void visit(Node node, int level) {
	// Process node
	if (node.getNodeType() == node.TEXT_NODE){
	  
	  System.out.println("Text: ["+node.getNodeValue()+"]");
	  
	} else if (node.getNodeType() == node.COMMENT_NODE) {
	  
	  System.out.println("Comment: "+node.getNodeValue());
	  
	} else if (node.getNodeType() == node.ELEMENT_NODE) {
	  
	  System.out.println("Element: "+node.getNodeName());
	  
	  // Process attributes
	  NamedNodeMap attrs = node.getAttributes();
	  
	  // Get number of attributes in the element
	  int numAttrs = attrs.getLength();
	  
	  // Process each attribute
	  for (int i=0; i<numAttrs; i++) {
        Attr attr = (Attr)attrs.item(i);
		
        // Get attribute name and value
        String attrName = attr.getNodeName();
        String attrValue = attr.getNodeValue();
		System.out.println("Attribute Nombre="+attrName+" Valor="+attrValue);
	  }
	  
	}
    
	// If there are any children, visit each one
	NodeList list = node.getChildNodes();
	for (int i=0; i<list.getLength(); i++) {
	  // Get child node
	  Node childNode = list.item(i);
	  
	  // Visit child node
	  visit(childNode, level+1);
	}
  }
  
  
}

