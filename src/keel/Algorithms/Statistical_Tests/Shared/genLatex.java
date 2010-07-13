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

package keel.Algorithms.Statistical_Tests.Shared;
/**
* <p>
* @author Written by Luciano Sanchez (University of Oviedo) 24/02/2005
* @author Modified by Jose Otero (University of Oviedo) 01/12/2008
* @version 1.0
* @since JDK1.5
* </p>
*/

import java.util.*;

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
import org.w3c.dom.Text;


import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;

import java.text.DecimalFormat;

public class genLatex {
    /**
     * <p>
     * Generates an output file in LaTeX format. Standalone tool, 
     * not linked to the graphical interface (12/2008).
     * </p>
     */    	
	
  static DecimalFormat df = new DecimalFormat("0.0000");
  
  /**
   * <p>
   * This is the main method of the class, it calls all the other ones. 
   * @param args Command line arguments (not used)
   * </p>
   */    	
  public static void main (String args[]) {

	String nameResult = "statResult.xml";
	File docFile = new File(nameResult);

    Document doc = null;

	try {
	  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	  DocumentBuilder db = dbf.newDocumentBuilder();
	  doc = db.parse(docFile);
	} catch (java.io.IOException e) {
	  System.out.println("File not found");
	} catch (Exception e) {
	  System.out.print("Problem parsing the file.");
	  e.printStackTrace();
	}
    //all the stuff is done sequentially here
	getMeans(doc);
	System.out.println();
	getMedians(doc);
	System.out.println();
	getpValues(doc);
	System.out.println();
	statpValues(doc);
  }

  /**
   * <p>
   * This method gets the means of an experiment results
   * @param doc The document from where the means are taken
   * </p>
   */   
  static void getMeans(Document doc) {
	// Get a list of all elements in the document

    NodeList list = doc.getElementsByTagName("datasetOutput");
    for (int i=0; i<list.getLength(); i++) {
	  // Get element
	  Element element = (Element)list.item(i);
	  String attrValue = element.getAttribute("datasetName");
	  String numOfAlgo = element.getAttribute("numberOfAlgorithms");
	  int nalg=(new Integer(numOfAlgo)).intValue();

	  if (i==0) {
		System.out.print("\\begin{tabular}[");
		for (int j=0;j<nalg+1;j++) System.out.print("c");
		System.out.println("]");
	  }


	  System.out.print(attrValue+" & ");
	  NodeList list1 = element.getElementsByTagName("mean");
	  for (int j=0; j<list1.getLength(); j++) {
		Element element1 = (Element)list1.item(j);
		Text text1 = (Text)element1.getFirstChild();
		String []string = text1.getData().split(" ");
		double means[] = new double[nalg];
		for (int k=0;k<nalg;k++)
		  means[k]=(new Double(string[k])).doubleValue();

		for (int k=0;k<nalg;k++) {
		  System.out.print(df.format(means[k]));
		  if (k!=nalg-1) System.out.print(" & ");
		}

		System.out.println("\\\\");
	  }
    }
	System.out.println("\\end{tabular}");
  }

  /**
   * <p>
   * This method gets the medians of an experiment results
   * @param doc The document from where the medians are taken
   * </p>
   */   
  static void getMedians(Document doc) {
	// Get a list of all elements in the document
    NodeList list = doc.getElementsByTagName("datasetOutput");
    for (int i=0; i<list.getLength(); i++) {
	  // Get element
	  Element element = (Element)list.item(i);
	  String attrValue = element.getAttribute("datasetName");
	  String numOfAlgo = element.getAttribute("numberOfAlgorithms");
	  int nalg=(new Integer(numOfAlgo)).intValue();

	  if (i==0) {
		System.out.print("\\begin{tabular}[");
		for (int j=0;j<nalg+1;j++) System.out.print("c");
		System.out.println("]");
	  }
	  
	  System.out.print(attrValue+" & ");
	  NodeList list1 = element.getElementsByTagName("median");
	  for (int j=0; j<list1.getLength(); j++) {
		Element element1 = (Element)list1.item(j);
		Text text1 = (Text)element1.getFirstChild();
		String []string = text1.getData().split(" ");
		double means[] = new double[nalg];
		for (int k=0;k<nalg;k++)
		  means[k]=(new Double(string[k])).doubleValue();

		for (int k=0;k<nalg;k++) {
		  System.out.print(df.format(means[k]));
		  if (k!=nalg-1) System.out.print(" & ");
		}

		System.out.println("\\\\");
	  }
    }
	System.out.println("\\end{tabular}");
  }

  /**
   * <p>
   * This method gets the pvalues of an experiment results
   * @param doc The document from where the pvalues are taken
   * </p>
   */   
  static void getpValues(Document doc) {
	// Get a list of all elements in the document
    NodeList list = doc.getElementsByTagName("datasetOutput");
    for (int i=0; i<list.getLength(); i++) {
	  // Get element
	  Element element = (Element)list.item(i);
	  String attrValue = element.getAttribute("datasetName");
	  String numOfAlgo = element.getAttribute("numberOfAlgorithms");
	  int nalg=(new Integer(numOfAlgo)).intValue();

	  System.out.println("\n"+attrValue);

	  System.out.print("\\begin{tabular}[");
	  for (int j=0;j<nalg;j++) System.out.print("c");
	  System.out.println("]");

	  NodeList list1 = element.getElementsByTagName("pvalues");
	  for (int j=0; j<list1.getLength(); j++) {
		Element element1 = (Element)list1.item(j);
		Text text1 = (Text)element1.getFirstChild();
		String []string = text1.getData().split(" ");
		double pv[] = new double[(nalg*nalg-nalg)/2];
		for (int k=0;k<pv.length;k++)
		  pv[k]=(new Double(string[k])).doubleValue();

		int tmp=0;
		System.out.print(" & ");
		for (int k=1;k<nalg;k++) {
		  System.out.print(k);
		  if (k!=nalg-1) System.out.print(" & ");
		}
		System.out.println("\\\\");

		for (int k=1;k<nalg;k++) {
		  System.out.print((k-1)+" & ");
		  for (int l=1;l<nalg; l++) {
			if (l<k) System.out.print("-");
			else System.out.print(df.format(pv[tmp++]));
		    if (l!=nalg-1) System.out.print(" & ");
		  }
		  System.out.println("\\\\");
		}


	  }
	  System.out.println("\\end{tabular}");

    }
  }

  
  /**
   * <p>
   * This method counts how many times the algorithms are different 
   * for different confidence levels
   * @param doc The document from where the medians are taken
   * </p>
   */   
  static void statpValues(Document doc) {
	Vector PV = new Vector();
	NodeList list = doc.getElementsByTagName("datasetOutput");
    for (int i=0; i<list.getLength(); i++) {

	  // Get element
	  Element element = (Element)list.item(i);
	  String attrValue = element.getAttribute("datasetName");
	  String numOfAlgo = element.getAttribute("numberOfAlgorithms");
	  int nalg=(new Integer(numOfAlgo)).intValue();

	  NodeList list1 = element.getElementsByTagName("pvalues");
	  for (int j=0; j<list1.getLength(); j++) {
		Element element1 = (Element)list1.item(j);
		Text text1 = (Text)element1.getFirstChild();
		String []string = text1.getData().split(" ");
		double pv[] = new double[(nalg*nalg-nalg)/2];
		for (int k=0;k<pv.length;k++)
		  pv[k]=(new Double(string[k])).doubleValue();

		// Store the pvalues
		PV.add(pv);

	  }

    }

	double nsig[] = { 0.01, 0.05, 0.10 };
	double tpv[][] = new double[PV.size()][];
	for (int i=0;i<tpv.length;i++)
	  tpv[i]=(double[])PV.elementAt(i);

	System.out.println("Proportion of relevant differences");
	for (int ns=0;ns<nsig.length;ns++) {
	  double pot[] = new double[tpv[0].length];
	  for (int i=0;i<tpv.length;i++) {
		for (int j=0;j<tpv[i].length;j++)
		  if (tpv[i][j]<nsig[ns]) pot[j]++;
	  }
	  System.out.print("Sig. level = "+nsig[ns]+": ");
	  for (int i=0;i<pot.length;i++)
		System.out.print((pot[i]/(double)tpv.length)+" ");
	  System.out.println();
	}


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

