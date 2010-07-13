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

public class ConjGradQUAD {	
	/** 
	* <p> 
	* <pre>
	* Quadratic optimized Classificator/Model by Conjugated Gradient.
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
	//Number of layers
    int nLayers;
    //Number of inputs
    int nInputs;
    //Number of outputs
    int nOutputs;
    //Number of elements in each hidden layer
    int nElements[];
    //Input examples
    double inputs[][];
    //Expected output 
    double outputs[][];
    //Random seed generator
    static Randomize r;
  
    /**
     * <p>
     * Constructor for a perceptron neural network from its basic elements.
     * 
     * </p>
     * @param vInput input examples.
     * @param vOutput expected outputs.
     * @param pr Random generator.
     */
    public ConjGradQUAD(
                          double [][] vInput, double [][] vOutput, Randomize pr       // Datos
                          ) {
        // Class Initializator
        r=pr;
        
        inputs=duplicate(vInput);
        outputs=duplicate(vOutput);
        
        nInputs=vInput[0].length;    // Number of inputs
        nOutputs=vOutput[0].length;      // Number of outputs
 
        System.out.println("Entradas="+nInputs+" Salidas="+nOutputs);
        // First index: number of outputs
        // Second index: number of inputs + 1 = row concat(A,B)
        // Third index: number of columns A and B
        // WEIGHTS = new double[salida[0].length][entrada[0].length+1][entrada[0].length];
        
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
    double[][][] numericalGradient(FUN f,double x[][][]) {
        
        // To test gradient calculus subrutine it's estimated f(x) gradient
        double h=0.1f;
        double[][][] result,fplus,fminus,copyx;
        result=new double[x.length][x[0].length][x[0][0].length]; 
        fplus=new double[x.length][x[0].length][x[0][0].length]; 
        fminus=new double[x.length][x[0].length][x[0][0].length]; 
        copyx=new double[x.length][x[0].length][x[0][0].length]; 
        for (int i=0;i<x.length;i++) 
            for (int j=0;j<x[i].length;j++)
                for (int k=0;k<x[i][j].length;k++) {
                    copyx[i][j][k]=x[i][j][k];
                }
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
    public static double[] duplicate(double x[]) {
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
    public static double[][] duplicate(double x[][]) {
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
    public static double[][][] duplicate(double x[][][]) {
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
     * Calculates the quadratic model gradient for a neural network x
     * 
     * </p>
     * @param f error function for evaluate neural network with weights x
     * @param x weights to evaluate
     * @return the quadratic gradient for each weight x
     */
    double[][][] gradient(FUN f, double x[][][]) {
        
        boolean debug=false;
        
        // Quadratic Model Gradient
        double G[][][]=new double [x.length][x[0].length][x[0][0].length];
        
        double y[][]=new double[outputs.length][outputs[0].length];
        for (int k=0;k<inputs.length;k++) {
            y[k]=quadraticModelOutput(inputs[k],x);
        }
        
        for (int s=0;s<x.length;s++) {
            int tmp=x[s].length-1;
            for (int alfa=0;alfa<tmp;alfa++) {
                for (int beta=0;beta<tmp;beta++) {
                    G[s][alfa][beta]=0;
                    for (int k=0;k<inputs.length;k++) {
                        G[s][alfa][beta]+=2*(outputs[k][s]-y[k][s])
                        *(inputs[k][alfa])
                        *(inputs[k][beta]);
                    }
                    
                }
            }
            for (int beta=0;beta<tmp;beta++) {
                G[s][tmp][beta]=0;
                for (int k=0;k<inputs.length;k++) {
                    G[s][tmp][beta]+=2*(outputs[k][s]-y[k][s])
                    *(inputs[k][beta]);
                }
            }
        }
        
        for (int i=0;i<G.length;i++)
            for (int j=0;j<G[i].length;j++)
                for (int k=0;k<G[i][j].length;k++) G[i][j][k]*=-1.0/inputs.length;
        
        if (debug) {
          System.out.println("Gradiente="+AString(G));
          double H[][][]=numericalGradient(f,x);
          System.out.println("Gradiente numerico="+AString(H));
        }
        
        return G;
        
    }

   /**
    * <p>
    * Returns the output of the perceptron with weights W for input example x.
    * 
    * </p> 
    * 
    * @param x an input example
    * @param W the weights of a perceptron.
    * @return the output of the perceptron with weights W for input example x.
    */
    public double[] quadraticModelOutput(double x[], double W[][][]) {
        
        // Quadratic Model output
        double [] sal=new double[W.length];
        for (int s=0;s<W.length;s++) {
           int tmp=W[s].length-1; // Number of inputs
           sal[s]=0;
           for (int i=0;i<tmp;i++) {
               double v=0;
               for (int j=0;j<tmp;j++) v+=x[j]*W[s][i][j];
               sal[s]+=v*x[i];
           }
           for (int i=0;i<W[s][tmp].length;i++) {
               sal[s]+=W[s][tmp][i]*x[i];
           }
        }
        return sal;
        
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
     * Returns the mean square error of the output perceptron calculated with Quadratic Conjugated Gradient training algorithm.
     * 
     * </p>
     * @param f the error function.
     * @param TOL_ERR the stop error.
     * @param MIN_DELTAGC is not used.
     * @param MAX_ITER number of maximum iteratations.
     * @return the mean square error of the output perceptron calculated with Quadratic Conjugated Gradient training algorithm.
     */    
    double[][][] conjugatedGradient(FUN ferr, double TOL_ERR, double MIN_DELTAGC, int MAX_ITER) {
        
        System.out.println("Dentro "+MIN_DELTAGC);
   
        int NVAR=0;
        double last_err=0,err=0;
        
        double x[][][] = new double[outputs[0].length][inputs[0].length+1][inputs[0].length];
        randomWeights(x,1);
        System.out.println("X inicial="+AString(x));
        
        for (int i=0;i<x.length;i++) NVAR+=x[i].length*x[i][0].length;
        
        int iter=0,subiter=0;
        double alpha=0;
        
        double d[][][], gr[][][], g_old[][][];
        double xSearch[][][], dSearch[][][];
        
        // Conjugated Gradient Algorithm is run
        boolean restart=true;
        boolean debug=false;
        
        
        g_old = gradient(ferr,x);
        d=duplicate(g_old);
        
        do {
            if (debug) {
                System.out.println("X="+AString(x));
                System.out.println("g_old="+AString(g_old));
            }
            gr=gradient(ferr,x); 
            
            if (restart) {
                d=OPV.signChange(gr);
                restart=false; 
                subiter=0; 
            } else {
                double beta=(OPV.multiply(OPV.subtract(gr,g_old),gr))/
                OPV.multiply(g_old,g_old);
                
                d=OPV.subtract(OPV.multiply(beta,d),gr);
            }
            
            double dgr=Math.sqrt(OPV.multiply(d,d));
            xSearch=duplicate(x); 
            dSearch=OPV.multiply(1.0/dgr,duplicate(d));
            
            LinearSearchBrent BL = new LinearSearchBrent(ferr,dSearch,xSearch);    
            alpha=BL.minimumSearch(r);
            
            if (alpha==0) {
                restart=true;  // Gradient direction if there're problems
                System.out.println("Restart");
            } else {
                x=OPV.sum(x,OPV.multiply(alpha,dSearch));
                g_old=duplicate(gr);
                iter++; 
                subiter++;
                if (subiter>=NVAR) restart=true;  
            }
            
            err=ferr.evaluate(x);
            System.out.println("Iteracion="+(iter/NVAR)+
                               " alfa="+alpha+" ECM="+
                               err+ " Norma del gradiente "+
                               dgr);
            
            last_err=err;
			
            if (alpha<1e-4*dgr) {
                // restart=true; // Gradient direction if there're problems
                // System.out.println("Restart");
				System.out.println("return: alpha<"+(1e-4*dgr)+"="+alpha);
				break;
			} 
            
        } while (Math.sqrt(OPV.multiply(gr,gr))>TOL_ERR*gr.length && iter<MAX_ITER);
        
        return x;
    }
    /**
	 * <p>
	 *  Initializes the matrix of weights with random valued in the range [-x,x].
	 *  
	 * </p>
	 *     
	 * @param x the lower/upper limit for random values.
	 */ 
   void randomWeights(double[][][]weights, double x) {
        for (int i=0;i<weights.length;i++)
            for (int j=0;j<weights[i].length;j++)
                for (int k=0;k<weights[i][j].length;k++)
                    weights[i][j][k]=rnd(-x,x);
    }
   /** 
    * <p> 
    *  Returns a printable version of x.   	
    *
    * </p>
    *
    * @return a String with a printable version of x. 
    */	 
    String AString(double x[]) {
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
    String AString(double x[][]) {
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
    String AString(double x[][][]) {
        String result="";
        for (int i=0;i<x.length;i++) result+=AString(x[i]);
        return result;
    }
    
}


