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
 * File: WilcoxonDistribution.java.
 *
 * Class modelling Wilcoxon distribution
 *
 * @author Written by Joaquin Derrac (University of Granada) 1/12/2010
 * @version 1.0
 * @since JDK1.5
 */
package keel.GraphInterKeel.statistical.tests;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WilcoxonDistribution{
	
	private static Distribution2KeyTable table;
	
	private static boolean initialized = false;
	
	private static boolean notInteger;
	
	private static NormalDistribution normal;
	private static double leftTail; 
	private static double rightTail;
	private static double doubleTail;

    /**
     * Computes exact p-values for the Wilcoxon distribution, given that N<50
     *
     * @param N N parameter
     * @param R R parameter
     *
     * @return Exact p-value (1.0 if greater than 0.2, -1.0 if not computable)
     */
	public static double computeExactProbability(int N,double R){
		
		double pValue,value1,value2;
		
		if(!initialized){
			loadTable();
			initialized=true;
		}
		
		if(N>50){
			return -1.0;
		}
		
		if(isInt(R)){
			notInteger=false;
		}
		else{
			notInteger=true;
		}
		
		
		try{
		if(!notInteger){		
			pValue=table.get((int)R, N);
			if(pValue==-1.0){
				pValue=1.0;
			}
		}else{
			value1=table.get((int)Math.ceil(R), N);
			if(value1==-1.0){
				value1=1.0;
			}
			value2=table.get((int)Math.floor(R), N);
			if(value2==-1.0){
				value2=1.0;
			}
			
			pValue=(value1+value2)/2.0;
		}
		}
		catch(ArrayIndexOutOfBoundsException e){
			return 1.0;
		}
		return pValue;
		
	}//end-method

    /**
     * Computes asymptotic distribution of the Wilcoxon  statistic
     *
     * @param N N parameter
     * @param R R parameter
     * @param ties Integer vector representing the number and lenght of the ties found in the computation of ranks
     *
     * @return Asymptotic p-value
     */
	public static double computeAsymptoticProbability(int N,double R,int [] ties){
		
		double numerator1,numerator2,denominator;
		double z;
		double sumTies;
		
		normal=new NormalDistribution();
		
		numerator1=R-0.5-(N*(N+1.0)/4.0);
		numerator2=R+0.5-(N*(N+1.0)/4.0);
		
		denominator=Math.sqrt(N*(N+1.0)*((2*N)+1.0)/24.0);
		
		if(ties.length>0){
			sumTies=0.0;
			for(int i=0;i<ties.length;i++){
				sumTies+=ties[i]*((ties[i]*ties[i])-1.0);
			}
			denominator-=sumTies/48.0;
		}
		
		
		z=numerator1/denominator;
		
		leftTail=1.0-normal.getTipifiedProbability(z, true);

		z=numerator2/denominator;

		rightTail=normal.getTipifiedProbability(z, false);
		
		doubleTail=Math.min(leftTail, rightTail)*2.0;
		doubleTail=Math.min(doubleTail,1.0);
		
		return doubleTail;

	}//end-method

    /**
     * Returns double-tailed p-value of the last comparison
     *
     * @return Double-tailed p-value
     */
	public static double getDoubleTail(){
		return doubleTail;
	}//end-method

    /**
     * Returns left-tailed p-value of the last comparison
     *
     * @return Left-tailed p-value
     */
	public static double getLeftTail(){
		return leftTail;
	}//end-method

    /**
     * Returns right-tailed p-value of the last comparison
     *
     * @return Right-tailed p-value
     */
	public static double getRightTail(){
		return rightTail;
	}//end-method

    /**
     * Tests if a given double represents an integer value
     *
     * @param x Double to test
     * @param delta Acceptance threshold
     *
     * @return If a given double represents an integer value
     */
	private static boolean isInt(double x, double delta) {
	    double ceil = Math.ceil(x);
	    return x-delta<ceil && x+delta>ceil;
	}//end-method

    /**
     * Tests if a given double represents an integer value
     *
     * @param x Double to test
     *
     * @return If a given double represents an integer value
     */
	private static boolean isInt(double x) {
	    return isInt(x, 0.000000001);
	}//end-method

    /**
     * Load table for Wilcoxon distribution
     */
	private static void loadTable(){
	
		table=new Distribution2KeyTable(505,51);
		loadXML();

	}//end-method

    /**
     * Get the inner table
     *
     * @return Pointer to the table
     */
	public static Distribution2KeyTable getTable(){
		
		return table;
	}//end-method

    /**
     * Load Wilcoxon distribution table from a XML file
     */
	private static void loadXML(){
		
		try{
            SAXParserFactory spf=SAXParserFactory.newInstance(); 
            SAXParser sp = spf.newSAXParser();                   
           	sp.parse("./help/WilcoxonTable.xml", new WilcoxonReaderFormat());
        }catch(ParserConfigurationException e){                  
        	System.err.println("Parser error");             
        }catch(SAXException e2){                                 
        	System.err.println("SAX error: " + e2.getStackTrace());
        } catch (IOException e3) {
        	System.err.println("Input/Output error: " + e3.getMessage() );
        }
		
	}//end-method
	
}//end-class

/**
 * Inner class representing the format of XML Wilcoxon distribution
 *
 * @author Joaquin
 */
class WilcoxonReaderFormat extends DefaultHandler {

	int n;
	int r;
	String st;

    /**
     * Builder
     */
	public WilcoxonReaderFormat(){
		super();
		
		r=0;
		n=4;
		clearTable();		
	}//end-method

    /**
     * Start document actions
     */
	public void startDocument() throws SAXException{
   
  	}//end-method

	/**
     * End document actions
     */
	public void endDocument()throws SAXException{

  	}//end-method

    /**
     * Start Element actions
     */
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

    }//end-method

    /**
     * Reading characters
     * @param buf Buffer of characters
     * @param offset Offset
     * @param len Number of characters readed
     * @throws org.xml.sax.SAXException
     */
    public void characters(char buf[], int offset, int len) throws SAXException{
        st = new String(buf, offset, len); 
    }//end-method

    /**
     * En of an element
     *
     * @param uri Uri
     * @param localName Local name
     * @param qName Tag name
     */
    public void endElement(String uri, String localName, String qName) {
		if(qName.equals("element")){
			addElement();
			n++;
		}
		if(qName.equals("row")){
			n=4;
			r++;
		}
    }//end-method

    /**
     * Clear asociated table
     */
	private void clearTable(){
		
		WilcoxonDistribution.getTable().clear();
				
	}//end-method

    /**
     * Add an element to the asociated table
     */
	private void addElement(){
	
		WilcoxonDistribution.getTable().addValue(r, n, Double.parseDouble(st));
	}//end-method
    
}//end-class
