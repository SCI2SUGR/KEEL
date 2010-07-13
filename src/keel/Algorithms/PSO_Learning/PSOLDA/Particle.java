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

package keel.Algorithms.PSO_Learning.PSOLDA;

/**
 * <p>Title: Particle</p>
 *
 * <p>Company: KEEL</p>
 *
 * @author Jose A. Saez Munoz
 * @version 1.0
 */

import org.core.*;

public class Particle {
	
    public double[] X;				//position
    public double[] V;				//velocity
    public double[] B;				//particle best position
    public int dim;					//number of dimensions
    public double bestEvaluation;	//particle's best evaluation
    public double lastEvaluation;	//particle's last evaluation
    private int numAttsActual;
    private int numAttsBest;
    
    
    //static fields
    static double cognitiveWeight;
    static double socialWeight;
    static double inertiaFactor;
    static double vmax;
    static double vmin;
    
    static  public void InitializeParameters(double c1, double c2, double w, double vm){
    	cognitiveWeight=c1;
    	socialWeight=c2;
    	inertiaFactor=w;
    	vmax=vm;
    	vmin=(-1)*vmax;
    }

    
    //*********************************************************************
    //***************** Particle's constructor ****************************
    //*********************************************************************
    
    public Particle(int tam){
    	
    	dim=tam;
    	X=new double[dim];
    	V=new double[dim];
    	B=new double[dim];
        
    	bestEvaluation=-1;
    	lastEvaluation=-1;
        numAttsActual=0;
        numAttsBest=0;
    }
    
    
    //*********************************************************************
    //***************** Random initialization  ****************************
    //*********************************************************************
    
    public void randomInitialization(){
    	
    	numAttsActual=0;
    	numAttsBest=0;
    	
		for(int i=0 ; i<dim ; ++i){
	        X[i]=Randomize.RandClosed();
			B[i]=X[i];
			
			if(X[i]>0.5){
				numAttsActual++;
				numAttsBest++;
			}
				
			V[i]=Randomize.RanddoubleClosed(vmin, vmax);
		}
    }
	
    
    //*********************************************************************
    //***************** Set best position *********************************
    //********************************************************************* 
    
	public void setB(double[] x, double be, int numAtts){
		
		for(int i=0 ; i<dim ; ++i)
			B[i]=x[i];
		
		bestEvaluation=be;
		numAttsBest=numAtts;
	}
        
    
    //*********************************************************************
    //***************** Update velocity and position **********************
    //********************************************************************* 
	
    public void updateV(Particle G){
    	
    	double r1, r2;
        
        for(int i=0 ; i<dim ; ++i){
        	
            r1=Randomize.RandClosed();
            r2=Randomize.RandClosed();
            
            V[i]=inertiaFactor*V[i] + (cognitiveWeight*r1*(B[i]-X[i])) + (socialWeight*r2*(G.B[i]-X[i]));
            if(V[i]>vmax) V[i]=vmax;
            if(V[i]<vmin) V[i]=vmin;
        }
	}
	
    public void updateX(){
    	
    	numAttsActual=0;
    	
		for(int i=0 ; i<dim ; ++i){
        	X[i]=X[i]+V[i];
        	if(X[i]>1) X[i]=1;
        	if(X[i]<0) X[i]=0;
        	
			if(X[i]>0.5)
				numAttsActual++;
		}
	}
	
    
    //*********************************************************************
    //***************** Clone the particle ********************************
    //*********************************************************************   
    
    public Particle cloneParticle(){
    	
    	Particle newParticle=new Particle(dim);
    	
    	for(int i=0 ; i<dim ; ++i){
    		newParticle.X[i]=X[i];
    		newParticle.V[i]=V[i];
    		newParticle.B[i]=B[i];
    	}
    	
    	newParticle.dim=dim;
    	newParticle.bestEvaluation=bestEvaluation;
    	newParticle.lastEvaluation=lastEvaluation;
    	newParticle.numAttsActual=numAttsActual;
    	newParticle.numAttsBest=numAttsBest;
    	
    	return newParticle;
    }
    
    
    //*********************************************************************
    //***************** Number of attributes ******************************
    //********************************************************************* 
    
	public int presentAttsActual(){
		
		return numAttsActual;
	}
	
	public int presentAttsBest(){
		
		return numAttsBest;
	}
	
	
    //*********************************************************************
    //***************** Compare two particles *****************************
    //********************************************************************* 
	
	public boolean isBetter(Particle p2){
		
    	if(bestEvaluation>p2.bestEvaluation)
    		return true;
    	
    	if(bestEvaluation==p2.bestEvaluation && presentAttsBest()<p2.presentAttsBest())
    		return true;
    	
    	return false;
	}
    
}

