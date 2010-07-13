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

package keel.Algorithms.Neural_Networks.NNEP_Common.mutators.parametric;



import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.Link;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedLayer;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.LinkedNeuron;
import keel.Algorithms.Neural_Networks.NNEP_Common.neuralnet.SigmNeuron;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.range.Interval;


/**  
 * <p>
 * @author Written by Pedro Antonio Gutierrez Penia (University of Cordoba) 16/7/2007
 * @author Written by Aaron Ruiz Mora (University of Cordoba) 16/7/2007
 * @version 0.1
 * @since JDK1.5
 * </p>
 */

public class SigmNeuronParametricMutator implements INeuronParametricMutator<SigmNeuron> {
	
	/**
	 * <p>
	 * Parametric Mutator of Sigmoidal Neurons.
	 * </p> 
	 */
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Random generator used in mutation */
	
	protected IRandGen randgen;
	
	/** Amplitude coefficient for allowed weights */
	
	protected double amplitude;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor
	 */
	
	public SigmNeuronParametricMutator() 
	{
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------- Setting and getting Attributes
	/////////////////////////////////////////////////////////////////
    
    /**
     * <p>
	 * Returns the amplitude coefficient for allowed weights
	 * </p>
	 * @return double Amplitude coefficient
	 */
	
	public double getAmplitude() {
		return amplitude;
	}
	
    /**
     * <p>
	 * Sets the amplitude coefficient for allowed weights
	 * </p>
	 * @param amplitude New amplitude coefficient
	 */

	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}
	
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
	
	/////////////////////////////////////////////////////////////////
	// -------------- Implementing INeuronParametricMutator interface
	/////////////////////////////////////////////////////////////////

	/**
	 * <p>
	 * Do the parametric mutation over the links of a specific neuron
	 * in a specific layer
	 * </p>
	 * @param neuron Neuron in the layer to mutate
	 * @param layer Layer that contains the neuron
	 * @param nextLayer Next layer
	 * @param indexNeuron Index of neuron in the layer
	 * @param alphaInput Alpha coeficient for the input weigths
	 * @param alphaOutput Alpha coeficient for the output weigths
	 * @param temper Temperature of the individual that is being mutated
	 */
	
	public void parametricMutation(SigmNeuron neuron, LinkedLayer layer, LinkedLayer nextLayer, 
			int indexNeuron, double alphaInput, double alphaOutput, double temper) {

		// Link array
        Link [] links = neuron.getLinks();
        
        //For each input link
        for(int i=0; i<links.length; i++){
            if(!links[i].isBroken()){
            	//Weight increment
                double weigthIncrement = randgen.gaussian(alphaInput*temper);
                
                //Apply the mutation
                links[i].setWeight(links[i].getWeight()+weigthIncrement);
                
                //Control the weigth value
                if(!neuron.getWeightRange().contains(links[i].getWeight())){
                	Interval interval = neuron.getWeightRange();
                	if(links[i].getWeight() > (interval.getRight()*amplitude))
                		links[i].setWeight(interval.getRight()*amplitude);
                	if(links[i].getWeight() < (interval.getLeft()*amplitude))
                		links[i].setWeight(interval.getLeft()*amplitude);
                }
            }
        }
	    
        //For each output link
	    for(int i=0; i<nextLayer.getNofneurons(); i++){
	        
	        //Obtain a neuron
	    	LinkedNeuron linkedNeuron = nextLayer.getNeuron(i);
	    	links = linkedNeuron.getLinks();
	    	
	    	//Weight increment
		    double weigthIncrement = randgen.gaussian(alphaOutput*temper);
		    
			//Apply the mutation
	    	links[indexNeuron].setWeight(links[indexNeuron].getWeight()+weigthIncrement);
	    	
            //Control the weigth value
            if(!linkedNeuron.getWeightRange().contains(links[indexNeuron].getWeight())){
            	Interval interval = linkedNeuron.getWeightRange();
            	if(links[indexNeuron].getWeight() > (interval.getRight()*amplitude))
            		links[indexNeuron].setWeight(interval.getRight()*amplitude);
            	if(links[indexNeuron].getWeight() < (interval.getLeft()*amplitude))
            		links[indexNeuron].setWeight(interval.getLeft()*amplitude);
            }
	    }
	}
	
}

