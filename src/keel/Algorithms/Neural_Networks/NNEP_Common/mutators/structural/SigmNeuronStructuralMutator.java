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

package keel.Algorithms.Neural_Networks.NNEP_Common.mutators.structural;



import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.ILayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.INeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.SigmNeuron;
import net.sf.jclec.util.random.IRandGen;

/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class SigmNeuronStructuralMutator implements INeuronStructuralMutator<SigmNeuron> {
	
	/**
	 * <p>
	 * Structural Mutator of Sigmoidal Neurons.
	 * </p>
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Random generator used in mutation */
	
	protected IRandGen randgen;
	
	/** Minimum value of new weigths */
	
	protected double significativeWeigth;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * <p>
	 * Empty constructor
	 * </p>
	 */
	
	public SigmNeuronStructuralMutator() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Setting and getting Attributes
	/////////////////////////////////////////////////////////////////
	
    /**
     * <p>
	 * Returns the random generator used in mutation
	 * </p>
	 * @return IRandGen Random generator
	 */
	
	public IRandGen getRandgen() {
		return randgen;
	}
	
    /**
     * <p>
	 * Sets the random generator used in mutation
	 * </p>
	 * @param randgen New random generator
	 */

	public void setRandgen(IRandGen randgen) {
		this.randgen = randgen;
	}
	
    /**
     * <p>
	 * Returns the minimum value of new weigths
	 * </p>
	 * @return double Minimum value of new weigths
	 */

	public double getSignificativeWeigth() {
		return significativeWeigth;
	}
	
    /**
     * <p>
	 * Sets the minimum value of new weigths
	 * </p>
	 * @param significativeWeigth New minimum value of new weigths
	 */

	public void setSignificativeWeigth(double significativeWeigth) {
		this.significativeWeigth = significativeWeigth;
	}
	
	/////////////////////////////////////////////////////////////////
	// -------------- Implementing INeuronStructuralMutator interface
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Adds a neuron into a specific layer
	 * </p>
	 * @param neuron Neuron to add into the layer
	 * @param layer Hidden layer to add the neuron
	 * @param previousLayer Previous layer to the selected hidden layer
	 * @param nextLayer Next layer to the selected hidden layer
	 */
	
	public void addNeuron(SigmNeuron neuron, LinkedLayer layer, ILayer<? extends INeuron> previousLayer, 
			LinkedLayer nextLayer) {

		//Array of links
		Link links[];
		
		if(layer.isBiased()){
			links = new Link[previousLayer.getMaxnofneurons()+1];
			links[previousLayer.getMaxnofneurons()] = new Link();
			links[previousLayer.getMaxnofneurons()].setBroken(false);
			links[previousLayer.getMaxnofneurons()].setWeight(neuron.randomWeight(randgen, significativeWeigth));
			neuron.setBiased(true);
		}
		else
			links = new Link[previousLayer.getMaxnofneurons()];
		
		//Init all the links broken
		for(int i=0; i<previousLayer.getMaxnofneurons(); i++){
			links[i] = new Link();
			links[i].setBroken(true);
			links[i].setWeight(0);
		}        
		
		//Number of effective links
		int noflinks = randgen.choose(1, previousLayer.getNofneurons()+1);
		
		//For each effective link
		for(int i=0; i<noflinks; i++){
			
			//Select a neuron randomly
			int selectedNeuron;
			//do {
			selectedNeuron = randgen.choose(0, previousLayer.getNofneurons());
			//}while(!links[selectedNeuron].isBroken());
			
			//Apply a random weight
			links[selectedNeuron].setBroken(false);
			links[selectedNeuron].setOrigin(previousLayer.getNeuron(selectedNeuron));
			links[selectedNeuron].setWeight(neuron.randomWeight(randgen, significativeWeigth));
			links[selectedNeuron].setTarget(neuron);
		}
		
		//Set the links
		neuron.setLinks(links);
		
		//Finally add the neuron
		layer.addNeuron(neuron);
		
		//Number of effective links        
		noflinks = randgen.choose(1, nextLayer.getNofneurons()+1);
		for(int i=0; i<noflinks; i++){
			// Select a node randomly
			int selectedNeuron;
			do {
				selectedNeuron = randgen.choose(0, nextLayer.getNofneurons());
			}while(!(nextLayer.getNeuron(selectedNeuron).getLinks()[layer.getNofneurons()-1].isBroken()));
			
			// Linked neuron of the next layer
			LinkedNeuron linkedNeuron = nextLayer.getNeuron(selectedNeuron);
			
			// Make the link
			Link newLink = new Link();
			newLink.setBroken(false);
			newLink.setOrigin(neuron);
			newLink.setWeight(linkedNeuron.randomWeight(randgen, significativeWeigth));
			newLink.setTarget(linkedNeuron);
			linkedNeuron.setLink(layer.getNofneurons()-1, newLink);
		}
	}
	
	/**
	 * <p>
	 * Removes a neuron of a specific layer
	 * </p>
	 * @param layer Hidden layer to remove the neuron
	 * @param nextLayer Next layer to the selected hidden layer
	 * @param indexNeuron Index of neuron to remove
	 */

	public void removeNeuron(LinkedLayer layer, LinkedLayer nextLayer, int indexNeuron) {

	    //Remove the neuron
	    layer.removeNeuron(indexNeuron);
	    
	    //Fix links of the next layer
	    for(int i=0; i<nextLayer.getNofneurons(); i++){
	    	LinkedNeuron neuron = nextLayer.getNeuron(i);
	    	Link [] links = neuron.getLinks();
	    	if(!neuron.isBiased()){
	    		for(int j=indexNeuron; j<links.length; j++){
		        	if((j+1)==links.length){
		        		links[j].setBroken(true);
		        		links[j].setWeight(0);
		        	}
		        	else{
		        		links[j].setBroken(links[j+1].isBroken());
		        		links[j].setOrigin(links[j+1].getOrigin());
		        		links[j].setTarget(links[j+1].getTarget());
		        		links[j].setWeight(links[j+1].getWeight());
		        	}
	    		}
	    	}
		    else{
	    		for(int j=indexNeuron; j<links.length-1; j++){
		        	if((j+1)==links.length-1){
		        		links[j].setBroken(true);
		        		links[j].setWeight(0);
		        	}
		        	else{
		        		links[j].setBroken(links[j+1].isBroken());
		        		links[j].setOrigin(links[j+1].getOrigin());
		        		links[j].setTarget(links[j+1].getTarget());
		        		links[j].setWeight(links[j+1].getWeight());
		        	}
	    		}
		    }
	    }
	}
	
	/**
	 * <p>
	 * Adds a link to a neuron of an specific layer from
	 * a specific origin neuron
	 * </p>
	 * @param neuron Neuron to add the link
	 * @param layer Hidden layer to add the link
	 * @param previousLayer Previous layer to the selected hidden layer
	 * @param indexNeuron Index of neuron to add the link
	 * @param indexOrigin Index of neuron that its link come from in the previous layer
	 */

	public boolean addLink(SigmNeuron neuron, LinkedLayer layer, ILayer<? extends INeuron> previousLayer, 
			int indexNeuron, int indexOrigin) {

	    //Obtain the links array
	    Link [] links = neuron.getLinks();
	    links[indexOrigin].setBroken(false);
	    links[indexOrigin].setWeight( randgen.raw() );
	    links[indexOrigin].setOrigin(previousLayer.getNeuron(indexOrigin));
	    links[indexOrigin].setTarget(layer.getNeuron(indexNeuron));
	    
	    return true;
	}
	
	/**
	 * <p>
	 * Removes a link of a neuron of an specific layer from
	 * a specific origin neuron
	 * </p>
	 * @param neuron Neuron to remove the link
	 * @param indexOrigin Index of neuron that its link come from in the previous layer
	 */

	public boolean removeLink(SigmNeuron neuron, int indexOrigin) {

		//Obtain the links array
	    Link [] links = neuron.getLinks();
	    
	    links[indexOrigin].setBroken(true);
	    links[indexOrigin].setWeight(0);
	    links[indexOrigin].setOrigin(null);
	    links[indexOrigin].setTarget(null);
	    
	    return true;
	}
	
	/**
	 * <p>
	 * Units the weights of two specific neurons, and stores
	 * the result in the first neuron
	 * </p>
	 * @param firstNeuron First Neuron to unit its weights
	 * @param secondNeuron Second Neuron to unit its weights
	 * @param layer Hidden layer to unit the weights of two selected neurons
	 * @param nextLayer Next layer to the selected hidden layer
	 * @param indexFirstNeuron Index of the first neuron to unit
	 * @param indexSecondNeuron Index of the second neuron to unit
	 */

	public void unitNeuronsWeights(SigmNeuron firstNeuron, SigmNeuron secondNeuron, LinkedLayer layer, LinkedLayer nextLayer, 
			int indexFirstNeuron, int indexSecondNeuron) {

	    //Obtain the links arrays
	    Link [] firstLinks = firstNeuron.getLinks();
	    Link [] secondLinks = secondNeuron.getLinks();
	    
	    //For each link
	    for(int i=0; i<firstLinks.length; i++){
	        //The two neurons have this link
	        if(!firstLinks[i].isBroken() && !secondLinks[i].isBroken()){
	        	
	        	// Mean weight
		        firstLinks[i].setWeight((firstLinks[i].getWeight()+secondLinks[i].getWeight())/2);
	        }
	        //One neuron have this link (0.5 probability of keeping the link)
	        else if((!firstLinks[i].isBroken() || !secondLinks[i].isBroken()) &&
	                (randgen.raw() < 0.5)){
	            if(firstLinks[i].isBroken()){
	                firstLinks[i].setBroken(false);
	                firstLinks[i].setWeight(secondLinks[i].getWeight());
		            firstLinks[i].setOrigin(secondLinks[i].getOrigin());
		            firstLinks[i].setTarget(secondLinks[i].getTarget());
	            }
	        }
	        //No link
	        else{
	            firstLinks[i].setBroken(true);
	            firstLinks[i].setWeight(0);
	            firstLinks[i].setOrigin(null);
	            firstLinks[i].setTarget(null);
	        }
	    }
	    
	    //Change the weights with the next layer
	    for(int i=0; i<nextLayer.getNofneurons(); i++){
	    	LinkedNeuron neuron = nextLayer.getNeuron(i);
	    	Link [] links = neuron.getLinks();
	    	if(!links[indexFirstNeuron].isBroken() && !links[indexSecondNeuron].isBroken()){
	    	    links[indexFirstNeuron].setWeight(links[indexFirstNeuron].getWeight() + links[indexSecondNeuron].getWeight());
	    	}
	    	else if(links[indexFirstNeuron].isBroken() && !links[indexSecondNeuron].isBroken()){
	    	    links[indexFirstNeuron].setBroken(false);
	    	    links[indexFirstNeuron].setWeight(links[indexSecondNeuron].getWeight());
	    	    links[indexFirstNeuron].setOrigin(firstNeuron);
	    	    links[indexFirstNeuron].setTarget(neuron);
	    	}
	    }
	}
	
}

