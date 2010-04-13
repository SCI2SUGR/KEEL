/** 
* <p> 
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.ClassicalOptim;
import org.core.*;

public class GCNet {   
	/** 
	* <p> 
	* <pre>
	* Wrapper for a perceptron (ConjGradNN). Also this class allows to call the desired training method of the aggregated percetron:
	*  * nntrain: for invoking Conjugated Gradient.
	*  * nntrainDG: for invoking the Descendent Gradient.
	* </pre>
	* </p> 
	*/
    // Stop parameters 
    static final int MAX_ITER=10000;         // Maximum number of iterations
    static final double TOL_ERR=1.0e-8;      // Stop condition
    static final double MIN_DELTAGC=1.0e-5;  // Stop condition
    //Aggregated neural network.
    ConjGradNN Net;
    /** 
     * <p> 
     *  trains a perceptron with Conjugated Gradient algorithm and returns 
     *  the mean square error of neural network output compared to expected output.
     * 
     * </p>
     * @param nInputs	number of inputs in first layer     	
     * @param nOutputs	number of output in output layer	
     * @param examples	training examples
     * @param outputs 	expected outputs
     * @param topology	net topology (cardinality of the hidder layers)
     * @param weights	net weights
     * @param r			random generator
     * @return the mean square error of neural network output compared to expected output. 
     */ 
    public double nntrain(
    		int nInputs,      	// Number of inputs
    		int nOutputs,       // Number of outputs
    		double[][]examples, // Training examples
    		double[][]outputs,  // Expected outputs
    		int []topology,    	// Net Topology
    		double [] weights,  // Net Weights
    		Randomize r
                                ) {
        //Number of examples
        int nelem=examples.length;
        
        System.out.println("Numero entradas="+nInputs);
        System.out.println("Numero salidas="+nOutputs);
        System.out.println("Dimension topologia="+topology.length);
        System.out.println("Dimension pesos="+weights.length);
        System.out.println("Numero ejemplos="+nelem);
        //Net is created
        Net = new ConjGradNN(topology, examples, outputs, r);
        
        SquaresErrorNN Err = new SquaresErrorNN(Net);
        Net.randomWeights(1);
        double errf=Net.conjugatedGradient(Err, TOL_ERR, MIN_DELTAGC, MAX_ITER);
        
        // Weights, scales and RMS are returned
        Net.getWeights(weights);
        
      
        System.out.println("RMS train="+errf);
        return errf;
    }
    
    /** 
     * <p> 
     *  trains a perceptron with Conjugated Descendent algorithm and returns 
     *  the mean square error of neural network output compared to expected output.
     * 
     * </p>
     * @param nInputs	number of inputs in first layer     	
     * @param nOutputs	number of output in output layer	
     * @param examples	training examples
     * @param outputs 	expected outputs
     * @param topology	net topology (cardinality of the hidder layers)
     * @param weights	net weights
     * @param r			random generator
     * @return the mean square error of neural network output compared to expected output. 
     */ 
	public double nntrainGD(
			int nInputs,      	// Number of inputs
            int nOutputs,       // Number of outputs
            double[][]examples, // Training examples
            double[][]outputs,  // Expected outputs
            int []topology,    	// Net Topology
            double [] weights,  // Net Weights
            Randomize r
						  ) {
        
        int nElements=examples.length;
        
        System.out.println("Numero entradas="+nInputs);
        System.out.println("Numero salidas="+nOutputs);
        System.out.println("Dimension topologia="+topology.length);
        System.out.println("Dimension pesos="+weights.length);
        System.out.println("Numero ejemplos="+nElements);
        
        Net = new ConjGradNN(topology, examples, outputs, r);
        SquaresErrorNN Err = new SquaresErrorNN(Net);
        Net.randomWeights(1);
        double errf=Net.descentGradient(Err, TOL_ERR, MIN_DELTAGC, MAX_ITER);
        
		Net.getWeights(weights);
        
		
        System.out.println("RMS train="+errf);
        return errf;
    }
	
	/**
	 * Calculated the output of present perceptron with input x and returns it in original scale.
	 * 
	 * @param x the inputs for feeding the perceptron.
	 * @return a vector with the output of perceptron.
	 */
    public double[] nnoutput(
                                   double [] x       // Net inputs
                                   ) {
      
        // Proportional factor for examples
        double [] output = Net.nn(OPV.scale(x,Net.max_x,Net.min_x),Net.weights);
        return OPV.invScale(output,Net.max_y,Net.min_y);

    }
    
}



