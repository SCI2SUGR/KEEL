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

package keel.Algorithms.PSO_Learning.REPSO;

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
    public int TP, TN, FP, FN;		//particle evaluation parameters
    public int clase;				//particle class
    
    
    
    
    //static fields
    static double constrictionCoefficient;
    static double WeightsUpperLimit;
    static double vmax;
    static double vmin;
    static double w1;
    static double w2;
    static double w3;
    static double Interesting;
    static int numAttributes;		//number of atttributes in the dataset

    
    static  public void InitializeParameters(double c, double w, double _pvmax, double _pvmin, double _pw1, double _pw2, double _pw3, double _pInteresting, int na){
    	constrictionCoefficient=c;
    	WeightsUpperLimit=w;
    	vmax = _pvmax;
    	vmin = _pvmin;
    	w1 = _pw1;
    	w2 = _pw2;
    	w3 = _pw3;
    	Interesting = _pInteresting;
    	numAttributes=na;
    }

	
    //*********************************************************************
    //***************** Particle constructor ******************************
    //*********************************************************************
    
    public Particle(int cl){
    	

    	dim=numAttributes*3;
    	X=new double[dim];
    	V=new double [dim];
    	B=new double [dim];
        
        TP=0;TN=0;FP=0;FN=0;
        bestEvaluation=-1;
        lastEvaluation=-1;
        clase=cl;
    }
    
    //*********************************************************************
    //***************** Random initialization  ****************************
    //*********************************************************************
    
    public void randomInitialization(){
    	
		for(int i=0 ; i<dim ; ++i){
			
			X[i]=Randomize.RanddoubleClosed(0, 1);
    		B[i]=X[i];
			V[i]=Randomize.RanddoubleClosed(vmin,vmax);
		}
    }
    
    //*********************************************************************
    //***************** Fitness function **********************************
    //*********************************************************************
	
	public double evaluation(){
		
		
		if(computeNumConditions()==0)
			return -1;
		
        ParametrosPreEvaluacion();

        
        double Accuracy = ((double)TP/(double)(TP+FP));
        double Coverage = ((double)TP/(double)(TP+FN));
        double Succinctness = 1-(computeNumConditions()-1)/numAttributes;
        
        double fitness = w1*(Accuracy*Coverage)+w2*Succinctness+w3*Interesting;
                
        return fitness;
	}
	
	
	
	public int computeNumConditions(){
		
		int res=0;
		for(int i=0 ; i<numAttributes ; ++i)
			if(GetAttributePresence(i))
				res++;
		
		return res;
	}
	
	public boolean GetAttributePresence(int i){
		
		if(X[i]>0.5)
			return true;
		else
			return false;
		
	}
	
    //*********************************************************************
    //***************** Pre-evaluation parameters *************************
    //*********************************************************************  
	
    public void ParametrosPreEvaluacion(){
  	  
        TP=0;TN=0;FP=0;FN=0;
		  
		for(int i=0 ; i<REPSO.train.getnData() ; ++i){
	            
			if(!REPSO.train.getRemoved(i)){
	
				//veo si coincide el antecedente
	            if(CoverInstance(REPSO.train.getExample(i))){
	
	            	if(REPSO.train.getOutputAsInteger(i)==clase)
	            		TP++;
	                else
	                	FP++;
	            }
	
	            else{
	            	if(REPSO.train.getOutputAsInteger(i)==clase)
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
        for(int d=0 ; d<numAttributes ; ++d){
            
            if(GetAttributePresence(d)){
            	
            	if(REPSO.train.getTipo(d)==myDataset.NOMINAL){
            		if(!CoverNominalAttribute(instance[d],d))
            			return false;
            	}
            	
            	if(REPSO.train.getTipo(d)==myDataset.INTEGER){
            		if(!CoverIntegerAttribute(instance[d],d))
            			return false;
            	}
            	
            	if(REPSO.train.getTipo(d)==myDataset.REAL){
            		if(!CoverRealAttribute(instance[d],d))
            			return false;
            	}
            	
            	
            }
        }
            
        return true;
    }
    
    
    private Boolean CoverIntegerAttribute(double vi, int d){
    	
    	double vr = X[numAttributes*2+d];
    	
    	double max[]=REPSO.train.getemax();
    	double min[]=REPSO.train.getemin();
    	
    	double rango=max[d]-min[d];
    	
    	double x1=Math.ceil(vr*rango)+min[d];
    	
    	//obenter operador
    	if(X[numAttributes+d]>0.5)
    		return vi==x1;
    	else
    		return vi!=x1;
    }
    
    
    private Boolean CoverRealAttribute(double vi, int d){
    	
    	double vr = X[numAttributes*2+d];
    	
    	//obtener valor de la instancia
    	double max[]=REPSO.train.getemax();
    	double min[]=REPSO.train.getemin();
    	
    	double rango=max[d]-min[d];
    	
    	double x1=vr*rango+min[d];
    	
    	//obenter operador
    	if(X[numAttributes+d]>0.5)
    		return vi>=x1;
    	else
    		return vi<x1;
    }
    
    private Boolean CoverNominalAttribute(double vi, int d){
    	
    	double vr = X[numAttributes*2+d];
    	
    	int rango=(int)REPSO.train.devuelveRangos()[d][1];
  
    	double x1=Math.ceil(vr*rango);
    	

    	
    	//obenter operador
    	if(X[numAttributes+d]>0.5)
    		return vi==x1;
    	else
    		return vi!=x1;	
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
	
    public void updateV(Particle G, double wv){
    	
        for(int i=0 ; i<dim ; ++i){
        	
            double c1=Randomize.RanddoubleClosed(0,WeightsUpperLimit);
            double c2=Randomize.RanddoubleClosed(0,WeightsUpperLimit);

            V[i]=constrictionCoefficient*( wv*V[i] + (c1*(B[i]-X[i])) + (c2*(G.B[i]-X[i])) );
            
            if(V[i]>vmax) V[i]=vmax;
            if(V[i]<vmin) V[i]=vmin;    
        }
	}
	
	public void updateX(){
		
		for(int i=0 ; i<dim ; ++i){
        	X[i]=X[i]+V[i];
    		if(X[i]>1)X[i]=1;
    		if(X[i]<0)X[i]=0;	
		}
	}

    
    //*********************************************************************
    //***************** Clone the particle ********************************
    //*********************************************************************   
    
    public Particle cloneParticle(){
    	
    	Particle newParticle=new Particle(clase);
    	
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
    	
    	return newParticle;    
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
    	
    	for(int i=0 ; i<numAttributes ; ++i)
    		if(B[i]>0.5)
    			cont++;
    	
    	return cont;
    	
    }
    
    
    //*********************************************************************
    //***************** Default rule **************************************
    //*********************************************************************
    
    public void setAsDefaultRule(){
        
        for(int i=0 ; i<numAttributes ;++i)
        	X[i]=0;
    }

    //*********************************************************************
    //***************** Return operator of an attribute *******************
    //*********************************************************************
    
    public String getOperator(int i){
        
    	if(REPSO.train.getTipo(i)==myDataset.INTEGER){
    		
    		if(B[numAttributes+i]>0.5)
    			return " = ";
    		else
    			return " != ";
    	}
    	
    	if(REPSO.train.getTipo(i)==myDataset.NOMINAL){
    		
    		if(B[numAttributes+i]>0.5)
    			return " = ";
    		else
    			return " != ";
    	}
    	
    	if(REPSO.train.getTipo(i)==myDataset.REAL){
    		
    		if(B[numAttributes+i]>0.5)
    			return " >= ";
    		else
    			return " < ";
    	}
    	
    	return "";
    }
    
    
    //*********************************************************************
    //***************** Return operator of an attribute *******************
    //*********************************************************************
    
    public String getDomainValue(int i){
        
    	if(REPSO.train.getTipo(i)==myDataset.INTEGER){
    		
        	double vr = X[numAttributes*2+i];
        	
        	double max[]=REPSO.train.getemax();
        	double min[]=REPSO.train.getemin();
        	
        	double rango=max[i]-min[i];
        	
        	Double x1=Math.ceil(vr*rango)+min[i];
        	return x1.toString();
    	}
    	
    	if(REPSO.train.getTipo(i)==myDataset.NOMINAL){
    		
        	double vr = X[numAttributes*2+i];
        	
        	int rango=(int)REPSO.train.devuelveRangos()[i][1];
      
        	double x1=Math.ceil(vr*rango);
        	
        	int res;
        	if(x1==0)
        		res=0;
        	else
        		res=(int)x1;
        	
        	Integer sol=new Integer(res);
        	
        	return sol.toString();
    	}
    	
    	if(REPSO.train.getTipo(i)==myDataset.REAL){
    		
        	double vr = X[numAttributes*2+i];
        	
        	//obtener valor de la instancia
        	double max[]=REPSO.train.getemax();
        	double min[]=REPSO.train.getemin();
        	
        	double rango=max[i]-min[i];
        	
        	Double x1=vr*rango+min[i];
        	return x1.toString();

    	}
    	
    	return "";
    }
    
}

