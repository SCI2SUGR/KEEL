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

import java.util.ArrayList;
import java.util.Arrays;

import javolution.xml.XmlElement;
import javolution.xml.XmlFormat;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.range.Interval;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penya, Aaron Ruiz Mora (University of Cordoba) 17/07/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public abstract class LinkedNeuron implements INeuron {
	
	/**
	 * <p>
	 * Base implementation of a neuron of a hidden or output layer
	 * </p>
	 */
	
    /////////////////////////////////////////////////////////////////
    // ------------------------------------- Marshal/unmarshal format
    /////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Marshal/Unmarshal links and and a boolean indicating if the
	 * neuron is input-biased
	 * </p> 
	 */
	protected static final javolution.xml.XmlFormat<LinkedNeuron> XML = 
		new XmlFormat<LinkedNeuron>(LinkedNeuron.class) 
		{
			public void format(LinkedNeuron source, XmlElement xml) 
			{
				// Marshal biased
				xml.setAttribute("biased", source.biased);
				// Marshal each link
				xml.add(new ArrayList<Link>(Arrays.asList(source.links)), "links");
				// Marshal weightRange
				xml.add(source.weightRange, "weight-range");
			}

			public LinkedNeuron parse(XmlElement xml) 
			{
				// Resulting object
				LinkedNeuron result = (LinkedNeuron) xml.object();
				// Unmarshal biased
				result.biased = xml.getAttribute("biased", false);
				// Unmarshal each link
				ArrayList<Link> list = xml.<ArrayList<Link>>get("links");
				result.links = list.toArray(new Link[list.size()]);
				// Unmarshal weightRange
				result.weightRange = xml.<Interval>get("weight-range");
				// Return result
				return result;
			}

			public String defaultName() 
			{
				return "linked-neuron";
			}
		};
		
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Attributes
	/////////////////////////////////////////////////////////////////
    
    /** Link array */
    
    protected Link[] links;
    
    /** Is biased? */
    
    protected boolean biased;
    
	/** Weight range */
	
	protected Interval weightRange;
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Empty constructor
     * </p>
     */
    public LinkedNeuron() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Getting and setting attributes
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the links array
	 * </p>
	 * @return ILink [] Links array
	 */
    public Link [] getLinks() {
        return links;
    }

	/**
     * <p>
	 * Sets the links of the neuron
     * </p>
	 * @param links [] New links array
	 */
    public void setLinks(Link links[]) {
        this.links = links;
    }

    /**
     * <p>
	 * Returns the link with the neuron specified (0 is bias neuron)
     * </p>
	 * @return Link Link specified
	 */
    public Link getLink(int neuron) {
        return links[neuron];
    }

	/**
     * <p>
	 * Sets the link with the neuron specified (0 is bias neuron)
     * </p>
	 * @param neuron Neuron to set the link
	 * @param link New link of the neuron specified
	 */
    public void setLink(int neuron, Link link) {
        links[neuron] = link;
    }
    
    /**
     * <p>
     * Returns a boolean indicating if the layer has a bias neuron
     * </p>
     * @return true if the layer has a bias neuron
     */
    public boolean isBiased() {
		return biased;
	}
    
    /**
     * <p>
     * Sets a boolean indicating if the layer has a bias neuron
     * </p>
     * @param biased Boolean has bias neuron
     */
	public void setBiased(boolean biased) {
		this.biased = biased;
	}
	
    /**
     * <p>
     * Returns the weight range associated to the links
     * </p>
	 * @return Interval Weight range
	 */
	public Interval getWeightRange() {
		return weightRange;
	}

	/**
     * <p>
	 * Sets the weight range associated to the links
     * </p>
	 * @param weightRange New weight range
	 */
	public void setWeightRange(Interval weightRange) {
		this.weightRange = weightRange;
	}
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Implementing INeuron interface
	/////////////////////////////////////////////////////////////////

    /**
     * <p>
	 * Checks if this neuron is equal to another
     * </p>
	 * @param other Other link to compare
	 * @return true if both neurons are equal
	 */
    public boolean equals(INeuron other){
        if(this.hashCode()!=other.hashCode())
            return false;
        else
            return true;
    }
    
    /**
     * <p>
	 * Returns an integer number that identifies the neuron
     * </p>
	 * @return int Hashcode
	 */
    public int hashCode(){
        HashCodeBuilder hcb = new HashCodeBuilder(31, 37);
        hcb.append(this.getClass().toString());
        hcb.append(links);
        return hcb.toHashCode();
    }
    
	/**
     * <p>
	 * Operates this neuron, using an input array. This means:
     * </p>
     * <p>
	 * 1) apply the input function to all the inputs neuron and sum the result
     * </p>
     * <p>
	 * 2) apply the output function to the result
     * </p>
	 * @param inputs Double array to be used for the inputs observations
	 * @return double Output of the neuron for the array specified
	 */
    public double operate(double []inputs){
        
        //Result of the operation
        double result;
        
        //Init the input
        result = initInput();
        
        //Apply the input function to all the inputs neurons and accumulate
        //the result
        double in;
        double weight;
        for(int i=0; i<links.length; i++)
        {
            if(!links[i].isBroken() && biased && i==links.length-1){
            	//Obtain the weight
	            weight = links[i].getWeight();
	            
	            //Apply the input function
            	result = inputFunction(result, 1, weight);
            }
            else if(!links[i].isBroken())
            {
                //Obtain the output array of the origin neuron
                //with the provided inputs
                in = links[i].getOrigin().operate(inputs);
                
	            //Obtain the weight
	            weight = links[i].getWeight();
	            
	            //Apply the input function
	            result = inputFunction(result, in, weight);
            }
        }
        
        //Apply the output function to the result
        result = outputFunction(result);
        
        //Return the result
        return result;
    }
    
	/**
     * <p>
	 * Operates this neuron using an input matrix as argument
     * </p>
	 * @param inputs Double matrix to be used for the inputs observations
	 * @return double [] Array outputs of the neuron for the matrix specified
	 */
    public double [] operate(double[][] inputs){
    	
        //Result of the operation
        double [] result = new double[inputs[0].length];
        
        //Init the input array
        for(int i=0; i<result.length; i++)
            result[i] = initInput();
        
        //Apply the input function to all the inputs neurons and accumulate
        //the result
        double []ins;
        for(int i=0; i<links.length; i++)
        {
            if(!links[i].isBroken() && biased && links[i].getOrigin()==null){
	            //Apply the input function
	            for(int j=0; j<result.length; j++)
	                result[j] = inputFunction(result[j], 1, links[i].getWeight());
            }
            else if(!links[i].isBroken()){       
                //Obtain the output array of the origin neuron
                //with the provided observations
                ins = links[i].getOrigin().operate(inputs);
	            
	            //Apply the input function
	            for(int j=0; j<result.length; j++)
	                result[j] = inputFunction(result[j], ins[j], links[i].getWeight());
            }
        }
        
        
        //Apply the output function to the result
        for(int i=0; i<result.length; i++)
            result[i] = outputFunction(result[i]);
        
        //Return the result
        return result;
    }
    
    /////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the number of effective links of the neuron
     * </p>
	 * @return int Number of effective links
	 */
    public int getNoflinks(){
    	int noflinks=0;
		for(int i=0; i<links.length ;i++){
			if(!links[i].isBroken())
				noflinks++;
		}
		return noflinks;
    }
    
    /**
     * <p>
     * Returns a random weight for a link
     * </p>
     * @param randGen Random number generator
     * @param significativeWeight Minimum absolute value of the new weight
     * @return double Random weight
     */
    public double randomWeight(IRandGen randGen, double significativeWeight){
        double weight = 0;
        
        do{
        	weight = weightRange.getRandom(randGen);
        }while(Math.abs(weight) < significativeWeight);
        
        return weight;
    }
    
    /**
     * <p>
	 * Keep relevant links, that is, those links whose weight is higher
	 * than certain number
     * </p>
     * @param significativeWeight Significative weight
	 */
    public void keepRelevantLinks(double significativeWeight){
    		
    	for(int i=0; i<links.length; i++){
    		if(!links[i].isBroken() && Math.abs(links[i].getWeight())<significativeWeight){
	    		links[i].setWeight(0);
	    		links[i].setBroken(true);
	    		links[i].setOrigin(null);
	    		links[i].setTarget(null);
    		}
    		if(links[i].isBroken() && Math.abs(links[i].getWeight())!=0){
	    		links[i].setWeight(0);
	    		links[i].setOrigin(null);
	    		links[i].setTarget(null);
    		}
    	}
    }
    
    /**
     * <p>
     * Returns a copy of this linked neuron
     * </p>
     * @param previousLayer Previous layer to which copied neuron
     *                      is going to be linked
     * @return LinkedNeuron Copy of this linked neuron
     */
    public LinkedNeuron copy(ILayer<? extends INeuron> previousLayer){
    	LinkedNeuron result = null;
    	
		try {		
			// Generate new neuron
			result = this.getClass().newInstance();

			// Copy biased property
    		result.setBiased(this.biased);
    		
        	// Copy weight range
        	result.setWeightRange(this.weightRange);
    		
			// Copy links of the neuron
    		Link resultLinks[] = new Link[this.links.length];
    		for(int i=0; i<this.links.length; i++){
    			
    			// Generate new link
    			resultLinks[i] = new Link();
    			
    			// Set link properties
    			resultLinks[i].setBroken(this.links[i].isBroken());
    			if(!resultLinks[i].isBroken()){
    				resultLinks[i].setWeight(this.links[i].getWeight());
    				
    				if(this.links[i].getOrigin()==null)
    					resultLinks[i].setOrigin(null);
    				else
    					resultLinks[i].setOrigin(previousLayer.getNeuron(i));
    				resultLinks[i].setTarget(result);
    			}
    		}
    		result.setLinks(resultLinks);
    		
		}catch (InstantiationException e) {
			System.out.println("Instantiation Error " + e.getLocalizedMessage());
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			System.out.println("Illegal Access Error " + e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		return result;
    }
    
    /////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////
	
    /**
     * <p>
	 * Init the input of the neuron (0 or 1 depending on the kind of neuron)
     * </p>
	 * @return double Initialized value of the input
	 */
    protected abstract double initInput();
    
	/**
     * <p>
	 * Input function of the neuron. Update input for each input neuron
     * </p>
	 * @param input Old input
	 * @param in Output of the input neuron
	 * @param weight Weight of the link to the input neuron
	 * @return double Partial input of the input neuron
	 */
    protected abstract double inputFunction(double input, double in, double weight);
    
	/**
     * <p>
	 * Output function of the neuron
     * </p>
	 * @param input Input of the neuron
	 * @return double output of the neuron
	 */
    protected abstract double outputFunction(double input);
}

