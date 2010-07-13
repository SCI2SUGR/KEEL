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

package keel.Algorithms.PSO_Learning.LDWPSO;

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
    public Boolean[] AttribPr;		//attribute presence
    public int dim;					//number of dimensions
    public double bestEvaluation;	//particle's best evaluation
    public double lastEvaluation;	//particle's last evaluation
    public int TP, TN, FP, FN;		//particle evaluation parameters
    public int clase;				//particle class
    
    
    //static fields
    static double indifferenceThreshold;
    static double constrictionCoefficient;
    static double WeightsUpperLimit;
    
    static  public void InitializeParameters(double t, double c, double w){
    	indifferenceThreshold=t;
    	constrictionCoefficient=c;
    	WeightsUpperLimit=w;
    }

	
    //*********************************************************************
    //***************** Particle constructor ******************************
    //*********************************************************************
    
    public Particle(int tam, int cl){
    	
    	dim=tam;
    	X=new double[dim];
    	V=new double [dim];
    	B=new double [dim];
        
        TP=0;TN=0;FP=0;FN=0;
        bestEvaluation=-1;
        lastEvaluation=-1;
        clase=cl;
        
        AttribPr=new Boolean [dim/2];
        for(int d=0 ; d<dim/2 ; ++d)
            AttribPr[d]=true;
    }
    
    //*********************************************************************
    //***************** Random initialization  ****************************
    //*********************************************************************
    
    public void randomInitialization(){
    	
		for(int i=0 ; i<dim ; ++i){
			
			
    		if(i<dim/2 && LDWPSO.train.getTipo(i)==myDataset.NOMINAL )
    			X[i]=Randomize.RanddoubleClosed(0, 1);
    		else
    			X[i]=Randomize.RanddoubleClosed(0, indifferenceThreshold);
    		
			B[i]=X[i];
			V[i]=Randomize.RanddoubleClosed((-1)*indifferenceThreshold, indifferenceThreshold);
		}
    }
    
    //*********************************************************************
    //***************** Fitness function **********************************
    //*********************************************************************
	
	public double evaluation(){
		
		//if particle leaves space search...
			// a) see centers
        	for(int i=0 ; i<dim/2 ; ++i)
        		if(X[i]>1 || X[i]<0)
        			return -1;
        	
			// b) see radius if attribute is not nominal
        	for(int i=dim/2 ; i<dim ; ++i)
        		if(LDWPSO.train.getTipo(i-(dim/2))!=myDataset.NOMINAL && (X[i]>1 || X[i]<0))
        			return -1;
        			
        	// c) see number of presents attributes
        	int cont=0;
        	for(int i=0 ; i<dim/2 ; ++i)
        		if(X[i]<indifferenceThreshold)
        			cont++;
        	
        	if(cont==0)
        		return -1;
            
        //otherwise...
        ParametrosPreEvaluacion();
        
        double res=((double)TP/(double)(TP+FN))*((double)TN/(double)(TN+FP));
        return res;
	}
	
	
    //*********************************************************************
    //***************** Pre-evaluation parameters *************************
    //*********************************************************************  
	
    public void ParametrosPreEvaluacion(){
  	  
        TP=0;TN=0;FP=0;FN=0;
		  
		for(int i=0 ; i<LDWPSO.train.getnData() ; ++i){
	            
			if(!LDWPSO.train.getRemoved(i)){
	
				//veo si coincide el antecedente
	            if(CoverInstance(LDWPSO.train.getExample(i))){
	
	            	if(LDWPSO.train.getOutputAsInteger(i)==clase)
	            		TP++;
	                else
	                	FP++;
	            }
	
	            else{
	            	if(LDWPSO.train.getOutputAsInteger(i)==clase)
	            		FN++;
	                else
	                	TN++;
	            }
			}
		}
		
    }
	
	
    //*********************************************************************
    //***************** Instance coverage  ********************************
    //********************************************************************* 
    
    public Boolean CoverInstance(double[] instance){
        
        //veo si coincide el antecedente
        for(int d=0 ; d<dim/2 ; ++d){
            
            if(GetAttributePresence(d)){
            
            	if(LDWPSO.train.getTipo(d)==myDataset.INTEGER || LDWPSO.train.getTipo(d)==myDataset.REAL){
            		if(!CoverNumericalAttribute(X[d],X[(dim/2)+d],instance[d]))
            			return false;
            	}
            	
            	if(LDWPSO.train.getTipo(d)==myDataset.NOMINAL){
            		if(!CoverNominalAttribute(X[d],instance[d],d))
            			return false;
            	}
            	
            	
            }
        }
            
        return true;
    }
    
    
    private Boolean CoverNumericalAttribute(double v1, double v2, double vi){

        if( (v1>=indifferenceThreshold) || ((v1-v2)<=vi && (v1+v2)>=vi) )
          	return true;
        
        else
            return false;
    }
    
    
    private Boolean CoverNominalAttribute(double v1, double vi, int d){
    	
    	int rango=(int)LDWPSO.train.devuelveRangos()[d][1]+1;
  
    	int x1=(int)((v1*rango)/indifferenceThreshold);
    	int x2=(int)((vi*rango)/indifferenceThreshold);

        if( (v1>=indifferenceThreshold) || (x1==x2) )
          	return true;          

        else
            return false;
    }
    
    //*********************************************************************
    //***************** Set best position and set actual position  ********
    //********************************************************************* 
    
	public void setB(double[] x, double be){
		
		for(int i=0 ; i<dim ; ++i)
			B[i]=x[i];
		
		bestEvaluation=be;
	}
        
        
    public void setX(double[] x){
		
		for(int i=0 ; i<dim ; ++i)
			X[i]=x[i];
	}
    
    //*********************************************************************
    //***************** Update velocity and position **********************
    //********************************************************************* 
	
    public void updateV(Particle G, double w){
    	
        for(int i=0 ; i<dim ; ++i){
        	
            double c1=Randomize.RanddoubleClosed(0,WeightsUpperLimit);
            double c2=Randomize.RanddoubleClosed(0,WeightsUpperLimit);
            
            V[i]=constrictionCoefficient*( w*V[i] + (c1*(B[i]-X[i])) + (c2*(G.B[i]-X[i])) );
        }
	}
	
	public void updateX(){
		
		for(int i=0 ; i<dim ; ++i)
        	X[i]=X[i]+V[i];
	}

    
    //*********************************************************************
    //***************** Clone the particle ********************************
    //*********************************************************************   
    
    public Particle cloneParticle(){
    	
    	Particle newParticle=new Particle(dim, clase);
    	
    	for(int i=0 ; i<dim ; ++i){
    		newParticle.X[i]=X[i];
    		newParticle.V[i]=V[i];
    		newParticle.B[i]=B[i];
    	}
    	
    	newParticle.dim=dim;
    	newParticle.bestEvaluation=bestEvaluation;
    	newParticle.lastEvaluation=lastEvaluation;
    	newParticle.TP=TP;
    	newParticle.TN=TN;
    	newParticle.FP=FP;
    	newParticle.FN=FN;
    	newParticle.clase=clase;
    	
    	for(int i=0 ; i<dim/2 ; ++i)
    		newParticle.AttribPr[i]=AttribPr[i];    	
    	
    	return newParticle;
    }
    
  
    
    public void SetAttributePresence(int pos, Boolean presence){
        
        AttribPr[pos]=presence;
    
    }
    
    
    public Boolean GetAttributePresence(int pos){
    
        return AttribPr[pos];
    }
 


    public void fixAttributePresence(){
    	
    	for(int i=0 ; i<dim/2 ; ++i)
    		if(B[i]>=indifferenceThreshold && B[i]<=1)
    			SetAttributePresence(i,false);
    		else
    			SetAttributePresence(i,true);
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
	
    public int presentAttsBest(){
    	    	
    	int cont=0;
    	
    	for(int i=0 ; i<dim/2 ; ++i)
    		if(GetAttributePresence(i))
    			cont++;
    	
    	return cont;
    	
    }
    
    
    //*********************************************************************
    //***************** Default rule **************************************
    //*********************************************************************
    
    public void setAsDefaultRule(){
        
        for(int i=0 ; i<dim/2 ;++i)
        	SetAttributePresence(i,false);
    }

    
}

