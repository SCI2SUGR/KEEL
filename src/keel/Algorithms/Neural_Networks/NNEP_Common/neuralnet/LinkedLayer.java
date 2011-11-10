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

public abstract class LinkedLayer implements ILayer<LinkedNeuron>{
	
	/**
	 * <p>
	 * Base implementation of a hidden or output layer
	 * </p>
	 */
	
    /////////////////////////////////////////////////////////////////
    // ------------------------------------- Marshal/unmarshal format
    /////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Marshal/Unmarshal initial number of neurons, maximum number of
	 * neurons, each neuron, layer type, weigth range, and a boolean
	 * indicating if the layer is input-biased
	 * </p>
	 */
	protected static final javolution.xml.XmlFormat<LinkedLayer> XML = 
		new XmlFormat<LinkedLayer>(LinkedLayer.class) 
		{
			public void format(LinkedLayer source, XmlElement xml) 
			{
				// Marshal initialmaxnofneurons
				xml.setAttribute("initial-max-n-of-neurons", source.initialmaxnofneurons);
				// Marshal initialminnofneurons
				xml.setAttribute("min-n-of-neurons", source.minnofneurons);
				// Marshal maxnofneurons
				xml.setAttribute("max-n-of-neurons", source.maxnofneurons);
				// Marshal type
				xml.setAttribute("type", source.type);
				// Marshal biased
				xml.setAttribute("biased", source.biased);
				// Marshal each neuron
				xml.add(source.neurons, "neurons");
			}

			public LinkedLayer parse(XmlElement xml) 
			{
				// Resulting object
				LinkedLayer result = (LinkedLayer) xml.object();
				// Unmarshal initialmaxnofneurons
				result.initialmaxnofneurons = xml.getAttribute("initial-max-n-of-neurons", 1);
				// Unmarshal maxnofneurons
				result.minnofneurons = xml.getAttribute("min-n-of-neurons", 1);
				// Unmarshal maxnofneurons
				result.maxnofneurons = xml.getAttribute("max-n-of-neurons", 1);
				// Unmarshal type
				result.type = xml.getAttribute("type", HIDDEN_LAYER);
				// Unmarshal biased
				result.biased = xml.getAttribute("biased", false);
				// Unmarshal each neuron
				result.neurons = xml.<ArrayList<LinkedNeuron>>get("neurons");
				// Return result
				return result;
			}

			public String defaultName() 
			{
				return "linked-layer";
			}
		};
		
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------ Type of layer
	/////////////////////////////////////////////////////////////////
    
    /** Defines hidden layer type */
	
	public static final int HIDDEN_LAYER  = 1;
	
    /** Defines output layer type */
	
	public static final int OUTPUT_LAYER  = 2;
	
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Attributes
	/////////////////////////////////////////////////////////////////
	
    /** Minimum number of neurons for the net */
    
    protected int minnofneurons;
	
    /** Initial maximum number of neurons for the net */
    
    protected int initialmaxnofneurons;
	
    /** Maximum number of neurons for the layer */
    
    protected int maxnofneurons;
	
	/** Array of neurons of the layer */
	
	protected ArrayList<LinkedNeuron> neurons = new ArrayList<LinkedNeuron>();
	
    /** Type of layer (HIDDEN_LAYER or OUTPUT_LAYER) */
	
	protected int type = 0;
    
    /** Is biased? */
    
    protected boolean biased;
	
	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
     * Empty constructor
     * </p>
     */
    public LinkedLayer() {
        super();
    }
    
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Getting and setting attributes
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the minimum number of neurons of this layer
	 * </p>
	 * @return int Minimum number of neurons
	 */
    public int getMinnofneurons() {
        return minnofneurons;
    }
    
    /**
     * <p>
	 * Sets the minimum number of neurons of this layer
	 * </p>
	 * @param minofneurons Minimum number of neurons
	 */
    public void setMinnofneurons(int minofneurons) {
        this.minnofneurons = minofneurons;
    }
    
    /**
     * <p>
	 * Returns the initial maximum number of neurons of this layer
	 * </p>
	 * @return int Initial maximum number of neurons
	 */
    public int getInitialmaxnofneurons() {
        return initialmaxnofneurons;
    }
    
    /**
	 * Sets the initial maximum number of neurons of this layer (without BIAS)
	 * @param initialmaxnofneurons Initial number of neurons
	 */
    public void setInitialmaxnofneurons(int initialmaxnofneurons) {
        this.initialmaxnofneurons = initialmaxnofneurons;
    }
    
    /**
     * <p>
	 * Returns the type of this layer
	 * </p>
	 * @return int Type of layer
	 */
    public int getType() {
        return type;
    }
    
	/**
	 * <p>
	 * Sets the type of this layer
	 * </p>
	 * @param type New type of layer
	 */
    public void setType(int type) {
        this.type = type;
    }
    
    /**
     * <p>
     * Returns true if the layer has a bias neuron
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
     * @param isBiased Boolean has bias neuron
     */
	public void setBiased(boolean isBiased) {
		this.biased = isBiased;
	}
    
    
	/////////////////////////////////////////////////////////////////
	// -------------------------------- Implementing ILayer interface
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the maximum number of neurons of this layer
	 * </p>
	 * @return int Maximum number of neurons
	 */
    public int getMaxnofneurons() {
    		return maxnofneurons;
    }
    
    /**
     * <p>
	 * Sets the maximum number of neurons of this layer
	 * </p>
	 * @param maxnofneurons Number of neurons
	 */
    public void setMaxnofneurons(int maxnofneurons) {
   		this.maxnofneurons = maxnofneurons;
    }
    
    /**
     * <p>
	 * Add a neuron to the layer
	 * </p>
	 * @param neuron New neuron to add to the layer
	 */
    public void addNeuron(LinkedNeuron neuron) {
        
        //Control the number of neurons
        if(neurons.size() == maxnofneurons)
            return;
        
        //Add the neuron
        neurons.add(neuron);
    }
    
    /**
     * <p>
	 * Removes a neuron of the layer
	 * </p>
	 * @param neuron Neuron to remove
	 * @return true if the neuron has been removes
	 */
    public boolean removeNeuron(LinkedNeuron neuron) {
    	
        //Control the number of neurons
        if(neurons.size() == minnofneurons)
            return false;
    	
        return neurons.remove(neuron);
    }
    
    /**
     * <p>
	 * Removes a neuron of the layer using its index
	 * </p>
	 * @param index Index of the neuron to remove
	 * @return LinkedNeuron Neuron removed
	 */
    public LinkedNeuron removeNeuron(int index) {
        return neurons.remove(index);
    }
    
    /**
     * <p>
	 * Returns a neuron of the layer using its index
	 * </p>
	 * @param index Index of the neuron to remove
	 * @return LinkedNeuron Neuron removed
	 */
    public LinkedNeuron getNeuron(int index) {
        return neurons.get(index);
    }
    
    /**
     * <p>
	 * Returns the number of neurons of this layer
	 * </p>
	 * @return int Number of neurons
	 */
    public int getNofneurons() {
        return neurons.size();
    }
    
    /**
     * <p>
	 * Checks if this layer is equal to another
	 * </p>
	 * @param other Other layer to compare
	 * @return true if both layers are equal
	 */
    public boolean equals(ILayer<LinkedNeuron> other){
        if(this.hashCode()!=other.hashCode())
            return false;
        else
            return true;
    }
    
    /**
     * <p>
	 * Returns an integer number that identifies the layer
	 * </p>
	 * @return int Hashcode
	 */
    public int hashCode(){
        HashCodeBuilder hcb = new HashCodeBuilder(41, 43);
        for(INeuron neuron:neurons)
            hcb.append(neuron);
        return hcb.toHashCode();
    }
    
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
     * Checks if this layer is full of neurons
     * </p>
     * @return true if the layer is full of neurons
     */
    public boolean neuronsFull(){
        if(neurons.size() >= maxnofneurons)
            return true;
        else
            return false;
    }
    
    /**
     * <p>
     * Checks if this layer is empty of neurons
     * </p>
     * @return true if the layer is empty of neurons
     */
    public boolean neuronsEmpty(){
        if(neurons.size() == 0)
            return true;
        else
            return false;
    }
    
    /**
     * <p>
     * Checks if this layer is full of links
     * </p>
     * @return true if the layer is full of links
     */
    public boolean linksFull(ILayer<? extends INeuron> previous){
    	if(!biased) {
    		for(LinkedNeuron neuron:neurons)
    			if(neuron.getNoflinks() < previous.getNofneurons())
    				return false;
    	}
    	else {
    		for(LinkedNeuron neuron:neurons)
    			if(neuron.getNoflinks()-1 < previous.getNofneurons())
    				return false;
    	}
        return true;
    }
    
    /**
     * <p>
     * Checks if this layer is empty of links
     * </p>
     * @return true if the layer is empty of links
     */
    public boolean linksEmpty(){
    	for(LinkedNeuron neuron:neurons){
    		if((neuron.isBiased() && neuron.getNoflinks()>1) ||
    		   (!neuron.isBiased() && neuron.getNoflinks()>0))
    			return false;
    	}
        return true;
    }
    
    /**
     * <p>
	 * Returns the number of effective links of the layer
	 * </p>
	 * @return int Number of effective links
	 */
    public int getNoflinks(){
    	int noflinks=0;
		for(LinkedNeuron neuron:neurons)
			noflinks += neuron.getNoflinks();
		return noflinks;
    }
    
    /**
     * <p>
	 * Keep relevant links, that is, those links whose weight is higher
	 * than certain number
	 * </p>
     * @param significativeWeight Significative weight
	 */
    public void keepRelevantLinks(double significativeWeight){
    		
    	for(LinkedNeuron neuron:neurons)
    		neuron.keepRelevantLinks(significativeWeight);
    }
    
    /**
     * <p>
     * Returns a copy of this linked layer
     * </p>
     * @param previousLayer Previous layer to which copied neurons
     *                      are going to be linked
     * @return LinkedLayer Copy of this linked layer
     */
    public LinkedLayer copy(ILayer<? extends INeuron> previousLayer){

    	LinkedLayer result = null;
    	
		try {		
			// Generate new layer
			result = this.getClass().newInstance();

			// Copy properties of the layer
			result.setMinnofneurons(this.minnofneurons);
			result.setInitialmaxnofneurons(this.initialmaxnofneurons);
	    	result.setMaxnofneurons(this.maxnofneurons);
	    	result.setType(this.type);
	    	result.setBiased(this.biased);
	    	
	    	// Copy each neuron
	    	for(LinkedNeuron neuron:this.neurons)
	    		result.addNeuron(neuron.copy(previousLayer));
	    	
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
	 * New neuron for the layer
	 * </p>
	 * @return LinkedNeuron New neuron for the layer
	 */
    public abstract LinkedNeuron obtainNewNeuron();
}

