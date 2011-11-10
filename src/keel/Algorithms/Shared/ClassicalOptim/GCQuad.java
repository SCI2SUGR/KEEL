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
* @author Written by Luciano Sánchez (University of Oviedo) 27/02/2004
* @author Modified by Enrique A. de la Cal (University of Oviedo) 13/12/2008  
* @version 1.0 
* @since JDK1.4 
* </p> 
*/

package keel.Algorithms.Shared.ClassicalOptim;
import org.core.*;

public class GCQuad {
	/** 
	* <p> 
	* <pre>
	* Wrapper for a perceptron (ConjGradQUAD).
	* </pre>
	* </p> 
	*/
    
    // Stop parameters
    static final int MAX_ITER=10000;        // Maximum number of iterations
    static final double TOL_ERR=1.0e-8;     // Stop condition
    static final double MIN_DELTAGC=1.0e-5; // Stop condition
    //Aggregated neural network
    ConjGradQUAD Cua;
    
    //Maximum of each input
    double[] max_x;
    //Minimum of each input
    double[] min_x;
    //Maximum of each output
    double[] max_y;
    //Minimum of each output
    double[] min_y;
    //Average of each input
    double averageX[];
    //Average of each output
    double averageY[];
    //Input examples
    double inputs[][];
    //Expected outputs
    double outputs[][];
    //Net weights
    double [][][]weights;
    /** 
     * <p> 
     *  trains a perceptron with Quadratic Conjugated Gradient algorithm and returns 
     *  the mean square error of neural network output compared to expected output.
     * 
     * </p>
     * @param nInputs	number of inputs in first layer     	
     * @param nOutputs	number of output in output layer	
     * @param vInputs	training examples
     * @param vOutputs 	expected outputs
     * @param r			random generator
     * @return the mean square error of neural network output compared to expected output. 
     */ 
    public double cuatrain(
                          int nInputs,        // Number of inputs
                          int nOutputs,         // Number of outputs
                          double[][] vInputs, // Training inputs
                          double[][] vOutputs,  // Training outputs
                          Randomize r
                          ) {
        
        int nElements=vInputs.length;
    
        System.out.println("Numero entradas="+nInputs);
        System.out.println("Numero salidas="+nOutputs);
        System.out.println("Numero ejemplos="+nElements);
        
        // A local copy for examples is made
        inputs=ConjGradQUAD.duplicate(vInputs);
        outputs=ConjGradQUAD.duplicate(vOutputs);
        
        // Data are scaled
        scale();
        
        // Data trend is removed
        averageX=ConjGradQUAD.duplicate(inputs[0]);
        averageY=ConjGradQUAD.duplicate(outputs[0]);
        for (int i=1;i<inputs.length;i++) {
            averageX=OPV.sum(averageX, inputs[i]);
            averageY=OPV.sum(averageY, outputs[i]);
        }
        averageX=OPV.multiply(1.0/inputs.length,averageX);
        averageY=OPV.multiply(1.0/inputs.length,averageY);
        
        for (int i=0;i<inputs.length;i++) {
            inputs[i]=OPV.subtract(inputs[i],averageX);  
            outputs[i]=OPV.subtract(outputs[i],averageY);
        }
        
        Cua = new ConjGradQUAD(inputs, outputs, r);
        SquaresErrorQUAD Err = new SquaresErrorQUAD(Cua, inputs, outputs);
        weights=Cua.conjugatedGradient(Err, TOL_ERR, MIN_DELTAGC, MAX_ITER);
        
        // Training error
        double RMS=0;
        for (int i=0;i<inputs.length;i++) {
            double []s = cuaoutput(vInputs[i]);
            double []error = OPV.subtract(vOutputs[i],s);
            RMS += OPV.multiply(error,error);
        }
        RMS/=inputs.length;
        System.out.println("RMS train="+RMS);
        
        return RMS;
    }
    /**
     * <p>
     *  Calculates the output of a perceptron with weights W for input x
     *  
     *  </p>
     *  @param x the example to give the perceptron
     *  @return the output of perceptron with weights W for input x
     *  
     */  
    public double[] cuaoutput( double [] x ) {
        
        // Proportional factors for examples
        double [] xScale = OPV.scale(x,max_x,min_x);
        double [] xAveScale = OPV.subtract(xScale, averageX);
        double [] output  = Cua.quadraticModelOutput(xAveScale,weights);
        double [] aveOutput = OPV.sum(output, averageY);
        return OPV.invScale(aveOutput,max_y,min_y);
        
    }
    /**
     * <p>
     * Scales the input examples values and expected output valued
     * 
     * </p>
     */
    public void scale() {
        
        // Data are scaled        
        for (int i=0;i<inputs.length;i++) {
            
            if (i==0) {
                max_x=ConjGradQUAD.duplicate(inputs[i]);
                max_y=ConjGradQUAD.duplicate(outputs[i]);
                min_x=ConjGradQUAD.duplicate(inputs[i]); 
                min_y=ConjGradQUAD.duplicate(outputs[i]);
            } else {
                max_x=OPV.maximum(max_x,inputs[i]); 
                max_y=OPV.maximum(max_y,outputs[i]);
                min_x=OPV.minimum(min_x,inputs[i]); 
                min_y=OPV.minimum(min_y,outputs[i]);
            }
        }
        
        // Proportional factors for examples
        for (int i=0;i<inputs.length;i++) 
            inputs[i]=OPV.scale(inputs[i],max_x,min_x);
        for (int i=0;i<inputs.length;i++) 
            outputs[i]=OPV.scale(outputs[i],max_y,min_y);
        
    }
    
    
}


