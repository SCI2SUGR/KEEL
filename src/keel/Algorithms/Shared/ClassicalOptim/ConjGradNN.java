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

public class ConjGradNN {
	/** 
	* <p> 
	* <pre>
	* Optimized Classificator/Model by Conjugated Gradient.
	* Also this class is a container for a perceptron neural network and implements the training methods:
	*  * Conjugated Gradient: conjugatedGradient.
	*  * Descendent Gradient: descentGradient.
	*  
	*  
    *		  Input-Layer   Hidden Layer-i x nLayers    Output-Layer
    *			    -
    *			   | I            H							
    *			   | I			  H							  - 
    *			   | I			  H							O  |
    *	nInputs    | I			  H							O  | nOutputs 
    *			   | I			  H							O  |
    *			   | I			  H							O  |
    *			   | I			  H							  -
    *			   | I			  H							
    *			    - 
	* <pre>
	* </p> 
	*/ 
	//Random seed generator
    static Randomize r;
    //Number of Layers
    int nLayers;		
    //Number of Inputs
    int nInputs;
    //Number of Outputs
    int nOutputs;
    //Number of elements in each hidden layer
    int nElements[];
    //Weights. Dimension 1 (Layer number), Dimensions 2 and 3 (bi-dimensional grid of neurons)
    double [][][] weights;
    //Calculated input for each hidden layer
    double [][] input;
    //Calculated output for each hidden layer
    double [] output;
    //Gradient error
    double [][] delta;
    //Error gradient
    double [][][] gradf;
    //A vector with the difference between max_y and min_y, used for scaling the output
    double [] factor;
    //Maximum of each input
    double[] max_x;
    //Minimum of each input
    double[] min_x;
    //Maximum of each output
    double[] max_y;
    //Minimum of each output
    double[] min_y;
    //Input examples
    double [][] Input;
    //Expected output
    double [][] Output;
    /**
     * <p>
     * Constructor for a perceptron neural network from its basic elements.
     * 
     * </p>
     * @param vNelement topology. Number of neurons by hidden layer.
     * @param vInput input examples.
     * @param vOutput expected outputs.
     * @param pr Random generator.
     */
    public ConjGradNN(
                         int vNelement[],                         // Topology
                         double [][] vInput, double [][] vOutput, // Data
                         Randomize pr
                    ) {
        
        r=pr;
        
        Input=duplicate(vInput);
        Output=duplicate(vOutput);
        
        nInputs=vInput[0].length;     			// Number of inputs
        nOutputs=vOutput[0].length;      		// Number of outputs
        nElements=vNelement;					// Number of elements in each hidder layer			
        nLayers=nElements.length;              	// Number of hidden layers
        weights=new double[nLayers+1][][];   	// Weight matrix
        gradf=new double[nLayers+1][][];   		// Error gradient
        input=new double[nLayers+1][];   
        output=new double[nOutputs];
        delta=new double[nLayers+1][];
        
        int i,nInputsAux,j;
        
        for (i=0;i<nLayers;i++) {
            weights[i]=new double[nElements[i]][];
            gradf[i]=new double[nElements[i]][];
            if (i==0) nInputsAux=nInputs; else nInputsAux=weights[i-1].length;
            input[i]=new double[nInputsAux+1];
            delta[i]=new double[nElements[i]];
            for (j=0;j<weights[i].length;j++) {
                weights[i][j]=new double[nInputsAux+1];
                gradf[i][j]=new double[nInputsAux+1];
            }
        }
        weights[i]=new double[nOutputs][];
        gradf[i]=new double[nOutputs][];
        if (nLayers==0) {
            for (j=0;j<weights[i].length;j++) {
                weights[i][j]=new double[nInputs+1];
                gradf[i][j]=new double[nInputs+1];
            }
            input[i]=new double[nInputs+1];
        } else {
            for (j=0;j<weights[i].length;j++) {
                weights[i][j]=new double[weights[i-1].length+1];
                gradf[i][j]=new double[weights[i-1].length+1];
            }
            input[i]=new double[weights[i-1].length+1];
        }
        
        delta[i]=new double[nOutputs];
        
        factor=new double[nOutputs]; 
        max_x=new double[nInputs];
        min_x=new double[nInputs];
        max_y=new double[nOutputs];
        min_y=new double[nOutputs];
        
        for (i=0;i<factor.length;i++) { factor[i]=1; }
        for (i=0;i<nInputs;i++) { max_x[i]=1; min_x[i]=0; }
        for (i=0;i<nOutputs;i++) { max_y[i]=1; min_y[i]=0; }
        
        scale();
        
    }
    
    
    /**
     * <p>
     * calculates the numerical gradient of error function f with weights x using the numerical method based on the tangent calculus.
     * 
     * </p>
     * @param f the error function for neural network. 
     * @param x the weights to evaluate
     * @return the gradient for neural network with weights x using the numerical method based on the tangent calculus
     */
    private double[][][] numericalGradient(FUN f, double x[][][]) {
        
        // To test gradient calculus subroutine it's estimated f(x) gradient 
        double h=0.001f;
        double[][][] result,fplus,fminus,copyx;
        result=new double[x.length][x[0].length][x[0][0].length]; 
        fplus=new double[x.length][x[0].length][x[0][0].length]; 
        fminus=new double[x.length][x[0].length][x[0][0].length]; 
        copyx=new double[x.length][x[0].length][x[0][0].length]; 
        for (int i=0;i<x.length;i++) 
            for (int j=0;j<x[i].length;j++)
                for (int k=0;k<x[i][j].length;k++) copyx[i][j][k]=x[i][j][k];
        for (int i=0;i<x.length;i++) 
            for (int j=0;j<x[i].length;j++)
                for (int k=0;k<x[i][j].length;k++) {
                    copyx[i][j][k]+=h; fplus[i][j][k]=f.evaluate(copyx);
                    copyx[i][j][k]-=(2*h); fminus[i][j][k]=f.evaluate(copyx);
                    copyx[i][j][k]=x[i][j][k];
                    result[i][j][k]=(fplus[i][j][k]-fminus[i][j][k])/(2*h);
                }
                    return result;
        
    }
    /**
     * <p>
     * Copies the size elements of the vector org to dst. 
     * 
     * </p>
     * @param org the vector copy.
     * @param size the number of elements to copy.
     * @param dst the destinity vector.
     */
    private void copy(double org[], int size, double dst[]) {
        for (int i=0;i<size;i++) dst[i]=org[i];
    }
    /**
     * <p>
     * 
     * Creates and returns a copy of vector x.
     *      * 
     * </p>
     * @param x the vector to be copied.
     * @return a clone of vector x.
     */
    double[] duplicate(double x[]) {
        double r[]=new double[x.length];
        for (int i=0;i<x.length;i++) { r[i]=x[i]; }
        return r;
    }
   /**
     * <p>
     * 
     * Creates and returns a copy of vector x.
     *      * 
     * </p>
     * @param x the vector to be copied.
     * @return a clone of vector x.
    */
    double[][] duplicate(double x[][]) {
        double r[][]=new double[x.length][];
        for (int i=0;i<x.length;i++) { 
            r[i]=new double[x[i].length];
            for (int j=0;j<x[i].length;j++) { r[i][j]=x[i][j]; }
        }
        return r;
    }
    
    /**
     * <p>
     * 
     * Creates and returns a copy of vector x.
     *      * 
     * </p>
     * @param x the vector to be copied.
     * @return a clone of vector x.
    */   
    double[][][] duplicate(double x[][][]) {
        double r[][][]=new double[x.length][][];
        for (int i=0;i<x.length;i++) {
            r[i]=new double[x[i].length][];
            for (int j=0;j<r[i].length;j++) {
                r[i][j]=new double[x[i][j].length];
                for (int k=0;k<r[i][j].length;k++) r[i][j][k]=x[i][j][k];
            }
            
        }
        return r;
    }
    
    
    /**
     * <p>
     * Returns a hyperbolic tangent of x.
     * </p>
     * @param x value in range [-1,+1].
     * @return a hyperbolic tangent of x.
     */
    private double hTan(double x) {
        if (x<-100) return -1;
        if (x>100) return 1;
        return (double)(Math.exp(x)-Math.exp(-x))/(double)(Math.exp(x)+Math.exp(-x));
    }

    /**
     * <p>
     * Returns a sigmoid function of x.
     * </p>
     * @param x value in range [-1,+1].
     * @return a sigmoid function of x.
     */
    private double hTanp(double x) {
        if (x<-100) return 0;
        if (x>100) return 0;
        return 4/Math.pow((Math.exp(x)+Math.exp(-x)),2);
    }
    /**
     * <p>
     * Calculates the gradient for a neural network x
     * 
     * </p>
     * @param f error function for evaluate neural network with weights x
     * @param x weights to evaluate
     * @return the gradient for each weight x
     */
    private double[][][] gradient(FUN f, double x[][][]) {
        
        int i,j,k,l,m;
        double [][][] GRADE=duplicate(gradf); 
        
        for (k=0;k<Input.length;k++) {
            // Forward run
        //example k is copied to input[0]
	    copy(Input[k],Input[k].length,input[0]);
	    input[0][input[0].length-1]=1; 
        //for each hidden layer (x.length-1)    
	    for (i=0;i<x.length-1;i++) {
	    	    //for each neuron in current layer i
                for (j=0;j<x[i].length;j++) {
                	//input for neuron i+1,j is calculated
                    input[i+1][j]=OPV.multiply(x[i][j],input[i]);
                    //sigmoid function is calculated for unit i,j
                    delta[i][j]=hTanp(input[i+1][j]);
                }
                //Hyperbolic tangent is applied for each neuron in next layer
                for (j=0;j<input[i+1].length-1;j++) 
                    input[i+1][j]=hTan(input[i+1][j]);
                
                input[i+1][input[i+1].length-1]=1;
            }
	        //Now the output for the last hidden layer (i+1) is calculated
            for (j=0;j<output.length;j++) 
                output[j]=OPV.multiply(x[i][j],input[i]);
            //And the error for the last layer is measured
            for (j=0;j<delta[i].length;j++) delta[i][j]=output[j]-Output[k][j];
            
            // backguard run
            for (i=x.length-2;i>=0;i--) {
                for (j=0;j<delta[i].length;j++) {
                    double suma=0;
                    for (l=0;l<delta[i+1].length;l++) {
                        suma+=delta[i+1][l]*x[i+1][l][j];
                    }
                    delta[i][j]*=suma;
                }
            }
            
            // Gradient for example k
            for (i=0;i<gradf.length;i++) {
                for (j=0;j<gradf[i].length;j++) {
                    for (m=0;m<gradf[i][j].length; m++) {
                        gradf[i][j][m]=2*delta[i][j]*input[i][m]/Input.length;
                    }
                }
            }
            if (k==0) {
                GRADE=duplicate(gradf); 
            } else {
                GRADE=OPV.sum(GRADE,gradf);
            }
        }
        
        
        return GRADE;
    }
    
    
    /**
     * <p>
     * Prints to standard output N-tier Neural Network x 
     * 
     * </p>
     * @param x the example to print
     */
    public void sample(double x[][][]) {
        
        for (int i=0;i<x.length;i++)
            for (int j=0;j<x[i].length;j++)
                for (int k=0;k<x[i][j].length;k++) System.out.print(x[i][j][k]+" ");
        
    }
        
    /**
     * <p>
     *  Calculates the output of a perceptron with weights W for input x
     *  
     *  </p>
     *  @param x the example to give the perceptron
     *  @param W the weights of the perceptron
     *  @return the output of perceptron with weights W for input x
     */
    public double[] nn(double x[], double W[][][]) {
        
        // Last layer has linear activation
        copy(x,x.length,input[0]);
        input[0][input[0].length-1]=1; 
        
        int i,j;
        for (i=0;i<W.length-1;i++) {
            
            for (j=0;j<W[i].length;j++) 
                input[i+1][j]=OPV.multiply(W[i][j],input[i]);
            
            for (j=0;j<input[i+1].length-1;j++) 
                input[i+1][j]=hTan(input[i+1][j]);
            input[i+1][input[i+1].length-1]=1;
        }
        for (j=0;j<output.length;j++) 
            output[j]=OPV.multiply(W[i][j],input[i]);
        
        return output;
    }
    /**
     * <p>
     * Returns the mean square error of a perceptron with weights x for all the examples Input
     * 
     * </p>
     * @param x the example to give the perceptron
     * @return the mean square error of the perceptron x output respect the expected output Output
     */
    public double f(double x[][][]) {
        // Mean square error
        double RMS=0;
        for (int i=0;i<Input.length;i++) {
            double error[]=OPV.subtract(nn(Input[i],x),Output[i]);
            RMS+=OPV.multiply(error,error);
        }
        // Mean square error
        return RMS/Input.length;
    }
    
    
    /**
     * <p>
     * Returns the denormalized mean square error of a perceptron with weights x for all the examples Input
     * 
     * </p>
     * 
     * @param x the example to give the perceptron.
     * @param FACTOR a vector with difference between the max and min value for each output
     * @return the denormalized mean square error of the perceptron x output respect the expected output Output
     */
    public double f_denormalized(double x[][][], double FACTOR[]) {
        
        // Mean square error
        double RMS=0;
        for (int i=0;i<Input.length;i++) {
            double error[]=OPV.subtract(nn(Input[i],x),Output[i]);
            for (int j=0;j<error.length;j++) error[j]*=FACTOR[j];
            RMS+=OPV.multiply(error,error);
        }
        // Mean square error
        return RMS/Input.length;
    }
    /**
     * <p>
     * Returns a random value in the range [low, high].
     * 
     * </p>
     * @param low
     * @param high
     * @return
     */
    private double rnd(double low, double high) {
        // random value between 0 and 1
	return r.Rand()*(high-low)+low;
    }
    /**
     * <p>
     * Scales the input examples values and expected output valued
     * 
     * </p>
     */
    public void scale() {
        
        // Data are scaled        
        for (int i=0;i<Input.length;i++) {
            
            if (i==0) {
                max_x=duplicate(Input[i]); max_y=duplicate(Output[i]);
                min_x=duplicate(Input[i]); min_y=duplicate(Output[i]);
            } else {
            	max_x=OPV.maximum(max_x,Input[i]); 
                max_y=OPV.maximum(max_y,Output[i]);
                min_x=OPV.minimum(min_x,Input[i]); 
                min_y=OPV.minimum(min_y,Output[i]);
            }
         }
        
        // Proportional factor for examples        
        for (int i=0;i<Input.length;i++) 
            Input[i]=OPV.scale(Input[i],max_x,min_x);
        for (int i=0;i<Input.length;i++) 
            Output[i]=OPV.scale(Output[i],max_y,min_y);
        
        // Proportional factor between scaled  and not scaled error
        for (int i=0;i<factor.length;i++) factor[i]=max_y[i]-min_y[i];
    }
    
    /**
     * <p>
     * Returns the mean square error of the output perceptron calculated with Conjugated Gradient training algorithm.
     * 
     * </p>
     * @param f the error function.
     * @param TOL_ERR the stop error.
     * @param MIN_DELTAGC is not used.
     * @param MAX_ITER number of maximum iteratations.
     * @return the mean square error of the output perceptron calculated with Conjugated Gradient training algorithm.
     */
    public double conjugatedGradient(FUN f, double TOL_ERR, double MIN_DELTAGC, int MAX_ITER) {
        
        int NVAR=0;
        double last_err=0,err=0;
        
        for (int i=0;i<weights.length;i++) NVAR+=weights[i].length*weights[i][0].length;
        
        int iter=0,subiter=0;
        double alfa=0;
        
        double x[][][]=weights;
        double d[][][], gr[][][], g_old[][][];
        double xbus[][][], dbus[][][];
       
        // Conjugated Gradient Algorithm is run
        boolean restart=true;
        boolean debug=false;
        
        g_old = gradient(f,x);
        d=duplicate(g_old);
        
        do {
            if (debug) {
              System.out.println("Debug: X="+AString(x));
              System.out.println("Debug: g_old="+AString(g_old));
            }
            
            gr=gradient(f,x); 
            
            if (restart) {
                d=OPV.signChange(gr);
                restart=false; 
                subiter=0; 
            } else {
                double beta=(OPV.multiply(OPV.subtract(gr,g_old),gr))/
                OPV.multiply(g_old,g_old);
                
                d=OPV.subtract(OPV.multiply(beta,d),gr);
            }

			
			double ngr=Math.sqrt(OPV.multiply(gr,gr));
			double dgr=Math.sqrt(OPV.multiply(d,d));

			
            if (debug) System.out.println("...1");
            
            xbus=duplicate(x); 
            dbus=OPV.multiply(1.0/dgr,duplicate(d));

            if (debug) System.out.println("...2");
            
            LinearSearchBrent BL = new LinearSearchBrent(f,dbus,xbus);    
            if (debug) System.out.println("...3");
            alfa=BL.minimumSearch(r);
            if (debug) System.out.println("...4");
            
            
			x=OPV.sum(x,OPV.multiply(alfa,dbus));
			weights=x;
			g_old=duplicate(gr);
			iter++; 
			subiter++;
			if (subiter>=NVAR) restart=true;  
            
            if (debug) System.out.println("...5");
            
            err=f_denormalized(x,factor);
				
         	if (alfa<1e-4*dgr) {
                // restart=true; // Gradient direction if there're problems
                // System.out.println("Restart");
				System.out.println("return: alpha<"+(1e-4*dgr)+"="+alfa);
				break;
			} 
			
            last_err=err;
            
            
        } while (Math.sqrt(OPV.multiply(gr,gr))>TOL_ERR*gr.length 
                 && iter<MAX_ITER);
        
        return err;
        
        
    }
	
    /**
     * <p>
     * Returns the mean square error of the output perceptron calculated with Descendent Gradient training algorithm.
     * 
     * <p>
     * @param f the error function.
     * @param TOL_ERR the stop error.
     * @param MIN_DELTAGC is not used.
     * @param MAX_ITER number of maximum iteratations.
     * @return the mean square error of the output perceptron calculated with Descendent Gradient training algorithm.
     */
	   public double descentGradient(FUN f, double TOL_ERR, double MIN_DELTAGC, int MAX_ITER) {
		   
		   int NVAR=0;
		   double last_err=0,err=0;
		   
		   for (int i=0;i<weights.length;i++) NVAR+=weights[i].length*weights[i][0].length;
		   
		   int iter=0,subiter=0;
		   double alpha=0;
		   
		   double x[][][]=weights;
		   double d[][][], gr[][][], g_old[][][];
		   double xbus[][][], dbus[][][];
		   
		   
		   
		   // Gradient Descent
		   boolean restart=true;
		   boolean debug=false;
		   
		   g_old = gradient(f,x);
		   d=duplicate(g_old);
		   
		   do {
			   gr=gradient(f,x); 
			   d=OPV.signChange(gr);
			   xbus=duplicate(x); 
			   dbus=duplicate(d);
			   LinearSearchBrent BL = new LinearSearchBrent(f,dbus,xbus);    
			   alpha=BL.minimumSearch(r);
			   x=OPV.sum(x,OPV.multiply(alpha,dbus));
			   weights=x;
			   g_old=duplicate(gr);
			   iter++; 
			   err=f_denormalized(x,factor);			  
			   if (alpha<0.0001) {
				   System.out.println("return: alpha < 0.0001");
				   break;
			   } 
			   
		   } while (Math.sqrt(OPV.multiply(gr,gr))>TOL_ERR*gr.length 
					&& iter<MAX_ITER);
		   
		   return err;
		   
		   
	   }


	/**
	 * <p>
	 *  Initializes the matrix of weights with random valued in the range [-x,x].
	 *  
	 * </p>
	 *     
	 * @param x the lower/upper limit for random values.
	 */
    public void randomWeights(double x) {
        for (int i=0;i<weights.length;i++)
            for (int j=0;j<weights[i].length;j++)
                for (int k=0;k<weights[i][j].length;k++)
                    weights[i][j][k]=rnd(-x,x);
        
    }
    /**
     * <p>
	 * Updates the matrix of weights with the addition of random valued in the range [-x,x].
	 *    
	 * </p>    
	 * @param x the lower/upper limit for random values.
	 */
    public void changeWeights(double x) {
        for (int i=0;i<weights.length;i++)
            for (int j=0;j<weights[i].length;j++)
                for (int k=0;k<weights[i][j].length;k++)
                    weights[i][j][k]+=rnd(-x,x);
    }
    
    /** 
     * <p> 
     *  Returns a printable version of x.   	
     *
     * </p>
     *
     * @return a String with a printable version of x. 
     */	
    private String AString(double x[]) {
        String result="[";
        for (int i=0;i<x.length-1;i++) result+=(x[i]+" ");
        result+=x[x.length-1];
        result+="]";
        return result;
    }
    /** 
     * <p> 
     *  Returns a printable version of x.   	
     *
     * </p>
     *
     * @return a String with a printable version of x. 
     */		
    private String AString(double x[][]) {
        String result="";
        for (int i=0;i<x.length;i++) result+=AString(x[i]);
        return result;
    }
    /** 
     * <p> 
     *  Returns a printable version of x.   	
     *
     * </p>
     *
     * @return a String with a printable version of x. 
     */		
    private String AString(double x[][][]) {
        String result="";
        for (int i=0;i<x.length;i++) result+=AString(x[i]);
        return result;
    }
    
    /** 
     * <p> 
     *  Prints to standard output the main information about the training algorithm run:
     *  -the matrix of weights
     *  -the original input examples (not scaled) 
     *  -the original obtained output (not scaled)
     *  -the original expected output (not scaled)
     *  
     * </p>
     *
     * @return a String with a printable version of x. 
     */	 
    private void debugOutput() {
        double x[],y[],d[];
        System.out.println("Weight="+AString(weights));
        for (int i=0;i<Input.length;i++) {
            x=OPV.invScale(Input[i],max_x,min_x);
            y=OPV.invScale(nn(Input[i],weights),max_y,min_y);
            d=OPV.invScale(Output[i],max_y,min_y);
            System.out.println(AString(x)+" "+AString(y)+" "+AString(d));
            
        }
    }
    /** 
     * <p> 
     *  Returns a copy of weights in vector p.
     *  
     * </p>
     *
     * @param p an output parameter to obtaing a copy of weights. 
     */
    public void getWeights(double []p) {
        int total=0;
        for (int i=0;i<weights.length;i++)
            for (int j=0;j<weights[i].length;j++)
                for (int k=0;k<weights[i][j].length;k++) p[total++]=weights[i][j][k];
    }
    /** 
     * <p> 
     *  Copy the weights contained in p to the weights matrix.
     *  
     * </p>
     *
     * @param p an output parameter to obtaing a copy of weights. 
     */
    public void setWeights(double p[]) {
        int total=0;
        for (int i=0;i<weights.length;i++)
            for (int j=0;j<weights[i].length;j++)
                for (int k=0;k<weights[i][j].length;k++) weights[i][j][k]=(double)p[total++];
    }
    
    
}


