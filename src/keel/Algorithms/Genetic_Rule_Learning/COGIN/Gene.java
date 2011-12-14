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

package keel.Algorithms.Genetic_Rule_Learning.COGIN;

import java.util.ArrayList;
import keel.Dataset.Attribute;


/**
 * <p>
 * This class implements a gene as specified by the COGIN algorithm
 * </p>
 * 
 * <p>
 * @author Written by Julián Luengo Martín 08/02/2007
 * @version 0.2
 * @since JDK 1.5
 * </p>
 */
public class Gene {
	int negationBit = 0;
	char featureBits[];
	int bits;
	Attribute att;
	
	/**
	 * <p>
	 * Default constructor
	 * </p>
	 */
	public Gene(){
		negationBit = 0;
		featureBits = null;
	}
	
	/**
	 * <p>
	 * this constructor builds up a gene from the information of an attribute
	 * </p>
	 * @param a the attribute
	 */
	public Gene(Attribute a){
		int nvalues;
		
		att = a;
		negationBit = 0;
		if(att.getType()==Attribute.NOMINAL)
			nvalues = att.getNumNominalValues();
		else
			nvalues =(int) (att.getMaxAttribute() - att.getMinAttribute() +1);
		
		bits = (int)Math.ceil(Math.log(nvalues)/Math.log(2));
		if(bits==0)
			bits = 1;
		featureBits = new char [bits];
	}
	
	/**
	 * <p>
	 * Copy constructor, performs a deep copy of the passed object
	 * </p>
	 * @param gen the gene to be copied
	 */
	public Gene(Gene gen){
		this.negationBit = gen.negationBit;
		this.bits = gen.bits;
		this.att = gen.att;
		featureBits = new char[gen.featureBits.length];
		for(int i=0;i<gen.featureBits.length;i++){
			this.featureBits[i] = gen.featureBits[i];
		}
	}
	
	/**
	 * <p>
	 * Obtains the status of the negation bit
	 * </p>
	 * 
	 */
	public int getNegationBit(){
		return negationBit;
	}
	
	/**
	 * <p>
	 * Obtains the number of bits (computed from the attribute associated)
	 * </p>
	 *
	 */
	public int getNumBits(){
		return bits;
	}
	
	/**
	 * <p>
	 * Get the bit indicated
	 * </p>
	 * @param pos position of the bit to be retrieved
	 * @return the status of the bit
	 */
	public char getBit(int pos){
		return featureBits[pos];
	}
	
	/**
	 * <p>
	 * Sets the negation bit of this gene
	 * </p>
	 * @param isNeg The new negation status
	 */
	public void setNegation(int isNeg){
		negationBit = isNeg;
	}
	
	/**
	 * <p>
	 * Sets the bits status indicated by index and value
	 * </p>
	 * @param i the index of the bit
	 * @param newBit the new status (0 or 1)
	 */
	public void setBit(int i,char newBit){
		featureBits[i] = newBit;
	}
	
	/**
	 * <p>
	 * This method converts from the status of the gene to a 
	 * list of nominals values of the attribute which are currently covered
	 * </p>
	 * @return the arraylist of currently covered nominal values
	 */
	public ArrayList<Integer> bin2nominal(){
		ArrayList<String> binaries = new ArrayList<String>();
		ArrayList<String> complement;
		ArrayList<Integer> nominalList;
		int num;

		String cad = new String("");
		String clone;
		binaries.add(cad);
		for(int i=0;i<featureBits.length;i++){
			//concat the bit to the binary String
			if(featureBits[i]!='#'){
				for(int j=0;j<binaries.size();j++){
					cad = binaries.get(j);
					cad = cad + featureBits[i];
					binaries.set(j, cad);
				}
			}
			//the '#' implies to add the '0' and '1' possibilites
			else{
				complement = new ArrayList<String>();
				for(int j=0;j<binaries.size();j++){
					cad = binaries.get(j);
					clone = new String(cad);
					cad = cad + "1";
					binaries.set(j, cad);
					clone = clone + "0";
					complement.add(clone);
				}
				binaries.addAll(complement);
			}
		}
		nominalList = new ArrayList<Integer>();
		for(int i=0;i<binaries.size();i++){
			num = Integer.parseInt(binaries.get(i),2);
			nominalList.add(new Integer(num));
		}
		return nominalList;
	}
	
	/**
	 * <p>
	 * Test if the passed value (index of the nominal in the 
	 * attribute) is covered by this gene
	 * </p>
	 * @param value the index of the nominal value in the attribute list
	 * @return True if the value is covered by the gene, False otherwise
	 */
	public boolean test(double value){
		int nominalValue = (int)value;
		String binaryRep;
		boolean found;
		boolean allPossibleValuesPattern = true;
		ArrayList<Integer> nominalValues;
		//obtain the codified values of the gene
//		nominalValues = bin2nominal();
		
//		found = false;
//		for(int i=0;i<nominalValues.size() && !found;i++){
//			if(nominalValues.get(i).intValue() == nominalValue)
//				found = true;
//		}
		binaryRep = Integer.toBinaryString(nominalValue);
		found = true;
		for(int i=binaryRep.length()-1, j=featureBits.length-1;i>=0 && found;i--,j--){
			if(featureBits[j]!='#'){
				allPossibleValuesPattern = false;
				if( binaryRep.charAt(i)!=featureBits[j])
					found = false;
			}
		}
		if(allPossibleValuesPattern)
			return true;
		if(found && negationBit == 0)
			return true;
		if(!found && negationBit == 1)
			return true;
		
		return false;
	}
	
	/**
	 * <p>
	 * Transforms the current gene, so the attribute value is now covered
	 * by this gene
	 * </p>
	 * @param value the nominal value to be covered
	 */
	public void applydiffs(double value){
		String binaryRep;
		int nominalValue = (int)value;
		
		binaryRep = Integer.toBinaryString(nominalValue);
		for(int i=binaryRep.length()-1, j=featureBits.length-1;i>=0;i--,j--){
			if(featureBits[j]!='#' && binaryRep.charAt(i)!=featureBits[j] && negationBit==0){
//				if(Randomize.Rand() < 0.5)
					featureBits[j] = binaryRep.charAt(i);
//				else
//					featureBits[j] = '#';
			}
			else if(featureBits[j]=='#' ||( binaryRep.charAt(i)==featureBits[j] && negationBit==1)){
				if(binaryRep.charAt(i) == '1') featureBits[j] = '0';
				if(binaryRep.charAt(i) == '0') featureBits[j] = '1';
				
			}
		}
	}
}

