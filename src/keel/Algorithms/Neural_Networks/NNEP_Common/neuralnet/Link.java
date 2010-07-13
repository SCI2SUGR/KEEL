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

package keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet;

import javolution.xml.XmlElement;
import javolution.xml.XmlFormat;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class Link {
	
	/**
	 * <p>
	 * Link of a neuron
	 * </p>
	 */
	
    /////////////////////////////////////////////////////////////////
    // ------------------------------------- Marshal/unmarshal format
    /////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Marshal/Unmarshal weight and state of the link
	 * </p> 
	 */
	protected static final javolution.xml.XmlFormat<Link> XML = 
		new XmlFormat<Link>(Link.class) 
		{
			public void format(Link source, XmlElement xml) 
			{
				// Marshal weight
				xml.setAttribute("weight", source.weight);
				// Marshal broken
				xml.setAttribute("broken", source.broken);
			}

			public Link parse(XmlElement xml) 
			{
				// Resulting object
				Link result = (Link) xml.object();
				// Unmarshal weight
				result.weight = xml.getAttribute("weight", 0.);
				// Unmarshal broken
				result.broken = xml.getAttribute("broken", true);
				// Return result
				return result;
			}

			public String defaultName() 
			{
				return "link";
			}
		};
		
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Attributes
	/////////////////////////////////////////////////////////////////
    
    /** Origin neuron */

    protected INeuron origin;
    
    /** Target neuron */

    protected INeuron target;
    
    /** Weight value */
    
    protected double weight;
    
    /** Link state */
    
    protected boolean broken = true;
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    public Link() {
        super();
    }

	/////////////////////////////////////////////////////////////////
    // ------------------------------- Getting and setting attributes
	/////////////////////////////////////////////////////////////////
    
	/**
	 * <p>
	 * Returns the origin neuron of the link, used to obtain its output value
	 * </p>
	 * @return INeuron Origin neuron
	 */
    public INeuron getOrigin() {
        return origin;
    }

	/**
	 * <p>
	 * Sets the origin neuron of the link
	 * </p>
	 * @param origin New origin neuron
	 */
    public void setOrigin(INeuron origin) {
        this.origin = origin;
    }
    
	/**
	 * <p>
	 * Returns the target neuron of the link
	 * </p>
	 * @return INeuron Target neuron
	 */
    public INeuron getTarget() {
        return target;
    }

	/**
	 * <p>
	 * Sets the target neuron of the link
	 * </p>
	 * @param target New target neuron
	 */
    public void setTarget(INeuron target) {
        this.target = target;
    }

	/**
	 * <p>
	 * Returns the weight associated to this link, used to obtain the output value of the destiny neuron
	 * </p>
	 * @return double Weight value
	 */
    public double getWeight() {
        return weight;
    }

	/**
	 * <p>
	 * Sets the weight associated to this link
	 * </p>
	 * @param weight New weight value
	 */
    public void setWeight(double weight) {
        this.weight = weight;
    }
    
	/**
	 * <p>
	 * Returns a boolean indicating if the link is or not broken
	 * </p>
	 * @return true if the link is broken
	 */
	public boolean isBroken(){
	    return broken;
	}
	 
	/**
	 * <p>
	 * Sets a boolean indicating if the link is or not broken
	 * </p>
	 * @param broken New link state
	 */
	public void setBroken(boolean broken){
	    this.broken = broken;
	}
	
	/**
	 * <p>
	 * Checks if this link is equal to another
	 * </p>
	 * @param other Other link to compare
	 * @return true if both links are equal
	 */
	public boolean equals(Link other){
        if(this.hashCode()!=other.hashCode())
            return false;
        else
            return true;
	}
	
	/**
	 * <p>
	 * Returns an integer number that identifies the link
	 * </p>
	 * @return int Hashcode
	 */
	public int hashCode(){

		HashCodeBuilder hcb = new HashCodeBuilder(23, 29);
		hcb.append(weight);
		hcb.append(broken);
		hcb.append(origin);
	    return hcb.toHashCode();
	    
	}

}

