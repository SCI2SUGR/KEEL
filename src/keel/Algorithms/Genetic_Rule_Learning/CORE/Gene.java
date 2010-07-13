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
 * @author Written by Julián Luengo Martín 13/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import keel.Dataset.*;
import org.core.*;
import java.util.*;

/**
 * <p>
 * This class represents a gene (i.e. a relation for a set of attribute values).
 * The real values are stored in normalized form, and the nominal values are stored
 * in a list form.
 * </p>
 */
public class Gene {
	
	int index;
	int relation;
	double realValue;
	double bound1,bound2;
	boolean nominalValue [];
	
	Attribute att;
	int type;
	double min;
	double max;
	String nominalValueList[];
	
	/* for nominal attributes*/
	final static int equal = 0;
	final static int notEqual = 1;
	/* for real-valued attributes */
	final static int greaterThan = 2;
	final static int greaterThanOrEqual = 3;
	final static int lessThanOrEqual = 4;
	final static int lessThan = 5;
	final static int inBound = 6;
	final static int outOfBound = 7;
	
	/**
	 * <p>
	 * Parameterized constructor
	 * </p>
	 * @param a the attribute of this gene
	 * @param rel the type of relation for the values of this gene
	 */
	public Gene(Attribute a,int rel){
		att = a;
		type = a.getType();
		relation = rel;
		if(type != Attribute.NOMINAL){
			min = a.getMinAttribute();
			max = a.getMaxAttribute();
		}
		else{
			nominalValueList = new String[a.getNominalValuesList().size()];
			for(int i=0;i<a.getNominalValuesList().size();i++)
				nominalValueList[i] = a.getNominalValue(i);
			nominalValue = new boolean[ a.getNumNominalValues()];
		}	
				
	}
	
	/**
	 * <p>
	 * Deep-copy constructor
	 * </p>
	 * @param g the original gene
	 */
	public Gene(Gene g){
		this.att = g.att;
		this.bound1 = g.bound2;
		this.bound2 = g.bound2;
		this.index = g.index;
		this.max = g.max;
		this.min = g.min;
		this.relation = g.relation;
		this.realValue = g.realValue;
		this.type = g.type;
		
		if(type != Attribute.NOMINAL){
			min = att.getMinAttribute();
			max = att.getMaxAttribute();
		}
		else{
			nominalValueList = new String[att.getNominalValuesList().size()];
			for(int i=0;i<att.getNominalValuesList().size();i++)
				nominalValueList[i] = att.getNominalValue(i);
			nominalValue = new boolean[ att.getNumNominalValues()];
			for(int i=0;i<nominalValue.length;i++){
				this.nominalValue[i] = g.nominalValue[i];
			}
		}
		
	}
	
	/**
	 * <p>
	 * Sets the real value of this gene (for one value relations, i.e. "greater than", "less or equal than", etc.)
	 * </p>
	 * @param _value the new real value
	 */
	public void setRealValue(double _value){
		realValue = (_value - min)/(max - min);		
	}
	
	/**
	 * <p>
	 * Sets the minimum bound of this gene for the real values (for interval-based relations)
	 * </p>
	 * @param _value the new minimum bound
	 */
	public void setRealminBound(double _value){
		bound1 = (_value - min)/(max - min);				
	}
	
	/**
	 * <p>
	 * Sets the maximum bound of this gene for the real values (for interval-based relations)
	 * </p>
	 * @param _value the new maximum bound
	 */
	public void setRealmaxBound(double _value){
		bound2 = (_value - min)/(max - min);		
	}

	/**
	 * <p>
	 * Retrieves the real value of this gene
	 * </p>
	 * @return the real value of this relation (normalized!)
	 */
	public double getRealValue(){
		return realValue;
	}
	
	/**
	 * <p>
	 * sets the activation for a nominal value, represented by its index in the
	 * nominal values list of the attribute
	 * </p>
	 * @param index the index of the nominal value
	 * @param activation the new activation status
	 */
	public void setNominalValue(int index, boolean activation){
		nominalValue[index] = activation;		
	}
	
	/**
	 * <p>
	 * add a set of nominal values, so their status are now active
	 * </p>
	 * @param values the arraylist with the nominal values to be added
	 */
	public void addNominalValues(ArrayList<String> values){
		
		for(int i =0;i<values.size();i++){
			nominalValue[att.convertNominalValue(values.get(i))] = true;
		}
	}
	
	/**
	 * <p>
	 * removes a set of nominal values from the active list (if active)
	 * </p>
	 * @param values arraylist with the proper nominal values
	 */
	public void removeNominalValues(ArrayList<String> values){

		for(int i =0;i<values.size();i++){
			nominalValue[att.convertNominalValue(values.get(i))] = false;
		}
	}
	
	/**
	 * <p>
	 * Gets the attribute associated to this gene
	 * </p>
	 * @return the attribute of this gene
	 */
	public Attribute getAttribute(){
		return att;
	}
	
	/**
	 * <p>
	 * Mutates this gene. If nominal, swaps its activation status with 0.5  probability.
	 * If real valued, generates a random new value or new bounds.
	 * </p>
	 */
	public void mutate(){
		double rand1,rand2;
		if(type==Attribute.NOMINAL){
			for(int i=0;i<nominalValueList.length;i++){
				if(Randomize.Rand()<0.5)
					nominalValue[i] = !nominalValue[i];
			}
//			relation = Randomize.Randint(0, 2);
		}
		if(type!=Attribute.NOMINAL){
			if(relation!=Gene.inBound && relation!=Gene.outOfBound)
				realValue = Randomize.RandClosed();
			else{
				rand1 = Randomize.RandClosed();
				rand2 = Randomize.RandClosed();
				bound1 = Math.min(rand1, rand2);
				bound2 = Math.max(rand1, rand2);
			}
				
		}
	}
	
	/**
	 * <p>
	 * Test if two genes are the same, comparing all their values.
	 * </p>
	 * @param g the reference gene to be compared with
	 * @return True if equals in values, false otherwise
	 */
	public boolean same(Gene g){
		if(g.att != this.att)
			return false;
		if(this.type != Attribute.NOMINAL){
			if(this.relation != g.relation)
				return false;
			if(this.relation == Gene.inBound || this.relation == Gene.outOfBound){
				if(this.bound1 != g.bound1 || this.bound2 != g.bound2)
					return false;
			}
			else if(this.realValue != g.realValue)
				return false;	
		}
		else{
			for(int i=0;i<nominalValue.length;i++){
				return this.nominalValue[i] == g.nominalValue[i];
			}
		}
		return true;
	}
	
	/**
	 * <p>
	 * Test a value from an instance (of the data set), to see if it is covered.
	 * </p>
	 * @param input the input value (of the same attribute as this Gene!)
	 * @return true if covered, false if not
	 */
	public boolean test(double input){

		if(type==Attribute.NOMINAL){
			if(nominalValue[(int)input] && relation==Gene.equal)
				return true;
			if(!nominalValue[(int)input] && relation==Gene.notEqual)
				return true;
		}
		if(type!=Attribute.NOMINAL){
			input = (input - min)/(max - min);	
			if(relation==Gene.greaterThanOrEqual && input >= realValue)
				return true;
			else if(relation==Gene.greaterThan && input > realValue)
				return true;
			else if (relation==Gene.lessThan && input < realValue)
				return true;
			else if (relation==Gene.lessThanOrEqual && input <= realValue)
				return true;
			else if(relation==Gene.inBound && input>=bound1 && input<=bound2)
				return true;
			else if(relation==Gene.outOfBound && input<bound1 && input>bound2)
				return true;
		}

		return false;
	}
	
}

