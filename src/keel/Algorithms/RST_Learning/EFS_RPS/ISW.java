/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    S. García (sglopez@ujaen.es)
    F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
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
 * 
 * File: ISW.java
 * 
 * A implementation of a rough set based Instance Selection Wrapper class for EFS_RPS.
 * 
 * @author Written by Joaquín Derrac (University of Granada) 13/11/2011 
 * @version 1.0 
 * @since JDK1.5
 * 
 */

package keel.Algorithms.RST_Learning.EFS_RPS;

import java.util.Arrays;

public class ISW{

	private static double data[][];
	private static int FS[];
	private static int featuresSelected;
	
	private static int IS[];
	private static int instancesSelected;
	
	private static int instances;
	private static int features;
	
	private static int neighbors[];
	
	private static int output[];
	
	private static boolean nominal [];
	
	private static double posRegions[];
	private static int orderRegions[];
	
	private static int implicator;
	private static int tnorm;
	
	private static double evaluations;

	public static final int LUKASIEWICZ = 0;
	public static final int KLEENE_DIENES = 1;
	public static final int MIN = 2;
	public static final int PRODUCT = 3;
	
	public static void setImplicator(int val){
		
		if (val==KLEENE_DIENES){
			implicator=KLEENE_DIENES;
		}
		else{
			implicator=LUKASIEWICZ;
		}

	}
	
	public static void setTNorm(int val){
		
		if (val==MIN){
			tnorm=MIN;
		}
		else{
			if(val==PRODUCT){
				tnorm=PRODUCT;
			}
			else{
				tnorm=LUKASIEWICZ;
			}
		}

	}

	/**
	 * Loads the training data into the classifier
	 * 
	 * @param newData Data represented with continuous values
	 */
	public static void setData(double newData[][],boolean vecNominal []){	
		
		instances = newData.length;
		features = newData[0].length;
		
		data = new double [instances][features];
		
		for(int i=0;i<instances;i++){		
			for(int j=0;j<features;j++){		
				data[i][j]=newData[i][j];
			}
		}

		nominal= new boolean[features];
		
		for(int i=0;i <features;i++){
			nominal[i]=vecNominal[i];
		}
		
		FS = new int [features];
		IS = new int [instances];
		
		Arrays.fill(FS, 1);
		featuresSelected=features;
		
		Arrays.fill(IS, 1);
		instancesSelected=instances;

	}//end-method

	/**
	 * Loads the training output into the classifier
	 * 
	 * @param newOutput Output attribute of the training data
	 */
	public static void setOutput(int newOutput[]){	
		
		output=new int [data.length];
		
		System.arraycopy(newOutput,0,output, 0, data.length);
		
	}//end-method
	
	public static boolean isAttribute(int att){
		
		if(FS[att]==0){
			return false;
		}
		
		return true;
	}
	public static void setAttribute(int att){
		
		if(FS[att]==0){
			FS[att]=1;
			featuresSelected++;
		}

	}
	
	public static void unsetAttribute(int att){
		
		if(FS[att]==1){
			FS[att]=0;
			featuresSelected--;
		}

	}
	
	public static void setAttributes(int [] atts){
		
		featuresSelected=0;
		
		for(int i=0;i<features;i++){
			FS[i]=atts[i];
			if(atts[i]==1){
				featuresSelected++;
			}
		}

	}
	
	public static void setInstances(int [] ins){

		instancesSelected=0;
		
		for(int i=0; i< instances;i++){
			IS[i]=ins[i];
			if(ins[i]==1){
				instancesSelected++;
			}
		}

	}
	
	public static int [] getAttributes(){
		 
		int newFS [];
		
		newFS= new int [features];
		
		for(int i=0;i<features;i++){
			newFS[i]=FS[i];
		}
		
		return newFS;

	}
	
	public static int [] getInstances(){
		 
		int newIS [];
		
		newIS= new int [instances];
		
		for(int i=0;i<instances;i++){
			newIS[i]=IS[i];
		}
		
		return newIS;

	}
	
	public static void clearAttributes(){
		
		featuresSelected=0;
		
		Arrays.fill(FS, 0);

	}
	
	public static void clearInstances(){
		
		instancesSelected=0;
		
		Arrays.fill(IS, 0);

	}
	
	public static int getnFeatures(){
		return featuresSelected;
	}
	
	public static int getnInstances(){
		return instancesSelected;
	}
	
	public static void setAllInstances(){
		Arrays.fill(IS, 1);
	}
	
	public static void setAllAttributes(){
		Arrays.fill(FS, 1);
	}
	
	private static void sortRegionsDec(){
		
		double values[];
		
		orderRegions=new int [posRegions.length];
		values=new double [posRegions.length];
		
		System.arraycopy(posRegions, 0, values, 0, values.length);
		
		for(int i=0;i<posRegions.length;i++){
			orderRegions[i]=i;
		}
		
		double auxV;
		int auxI;
		
		for(int i=0;i<values.length;i++){
			for(int j=i+1;j<values.length;j++){
				if(values[i]<values[j]){
					auxV=values[i];
					values[i]=values[j];
					values[j]=auxV;
					
					auxI=orderRegions[i];
					orderRegions[i]=orderRegions[j];
					orderRegions[j]=auxI;
				}
			}
		}
		
	}
	
		
	private static void searchNeighbor(int index){
	
		double dist,minD;
		
		minD=Double.MAX_VALUE;
		
		for(int i=0;i<instances;i++){
			if(IS[i]==1){			
				if(index!=i){
					dist = euclideanDistance(index,i);
					if (dist < minD) {
						minD = dist;
						neighbors[index]=i;	
					}
				}
			}
		}

		evaluations+=1.0/instances;
	}
	
	private static void computeNeighbors(){
		
		neighbors=new int [instances];
		
		for(int index=0;index<instances;index++){
			searchNeighbor(index);
		}
		
	}
	
	private static double computeAcc(){
		
		double hits;
		
		hits=0.0;
		
		for(int i=0;i<instances;i++){
			if(output[i]==output[neighbors[i]]){
				hits+=1.0;
			}
		}
		
		return hits/(double)instances;
	}
	
	public static double computeISW(){
		
		double acc,bestAcc;
		int bestInstances [];

		evaluations=0.0;
		
		//sort instances according to pos region (based on all features)
        getPosregionsInstances();
        
        //generate an order for instances (decremental)
        sortRegionsDec();
        
        /* After this, there is an order defined on the instances */         
        bestInstances=new int [instances];
        Arrays.fill(IS, 1);
        Arrays.fill(bestInstances, 1);
        instancesSelected=instances;

        // First: all instances
        computeNeighbors();
        
        bestAcc = computeAcc();
       
        //iterate through all instances         
        for(int i=0;i<instances;i++){
        	
        	//remove instances
        	IS[orderRegions[i]]=0;
        	
        	//remove more if they have the same value)
            while(i+1<orderRegions.length && posRegions[orderRegions[i]]==posRegions[orderRegions[i+1]]){
                i++;
                IS[orderRegions[i]]=0;
            }

            //we need more than 0 instances!!
            if(i!=instances-1){
            	
                //update neighbors table
                
                for(int x=0;x<instances;x++){
                	
                	if(IS[neighbors[x]]==0){
                		
                		searchNeighbor(x);
                		
                	}
                }
                
                //compute accuracy
                acc = computeAcc();

                if(acc>=bestAcc){
                    bestAcc = acc;
                    System.arraycopy(IS, 0, bestInstances, 0,instances);
                    
                }
               
            }

        }
        
   
        System.arraycopy(bestInstances, 0, IS, 0,instances);
        
        instancesSelected=0;
        for(int i=0;i<instances;i++){
        	if(IS[i]==1){
        		instancesSelected++;
        	}
        }

        return evaluations;
    }       

	
    //calculate the positive region of the features seperately. 
	private static void getPosregionsInstances() {
		
		double min,impl;
		
		posRegions = new double[instances];
		 
        for(int i=0;i<instances;i++){
            min = Double.MAX_VALUE;
            
            for(int y=0;y<data.length;y++){
                if(output[i]==output[y]){
                    impl = calcimpl(similarity(i,y),1);
                }
                else{
                    impl = calcimpl(similarity(i,y),0);
                }
                
                if(impl<min){
                    min = impl;
                }
            }
            
            posRegions[i] = min;      
        }

    } 
    
    private static double similarity(int x, int y){
	    
    	double [] similarity= new double[features];
    	double sim;
    	double dist;
    	
    	for(int i=0;i<features;i++){
    		if(FS[i]==0){
    			similarity[i]=1.0;
        	}
    		else{
    			if(nominal[i]){
            		if(data[x][i]==data[y][i]){
            			dist = 0;
                    }
            		else{
            			dist = 1;
            		}
                }
                else{
                	dist =(data[x][i]-data[y][i]);
                	dist=dist*dist;
                }
        		
        		similarity[i]=1.0-dist;	
    		}
    			
    	}    	 	
    	
    	sim=calctnorm(similarity);
    	
    	return sim;

    }
    
    private static double calctnorm(double[] args){
    	
    	double tnormd;
    	
        if(args.length==1){
            return args[0];
        }else{
            tnormd = calctnorm(args[0],args[1]);
            
            for(int i=2;i<args.length;i++){
                tnormd = calctnorm(args[i],tnormd);
            }
            return tnormd;
        }
        
    }
    
    private static double calctnorm(double a, double b){
    	
        if(tnorm==MIN){
            return Math.min(a,b);
        }
        else if(tnorm==PRODUCT){
            return a*b;
        }
        else if(tnorm==LUKASIEWICZ){
            return Math.max(0,a+b-1);
        }
        else
            return 0;
    }
    
    private static double calcimpl(double a, double b){
        
    	if(implicator==LUKASIEWICZ){
            return Math.min(1.0,1.0-a+b);
        }
        else if(implicator==KLEENE_DIENES){
            return Math.max(1-a, b);
        }
        
        return 0;
    }
    
    /**
	 * Euclidean instance between two training instances
	 * 
	 * @param a First instance
	 * @param b Second instance
	 * 
	 * @return Unsquared euclidean distance
	 */
	private static double euclideanDistance(int a,int b){
		
		double length=0.0;
		double value;
		
		for (int i=0; i<data[b].length; i++) {
			
			if(FS[i]==1){
				value = data[a][i]-data[b][i];
				length += value*value;
			}
		}
		
		return length;
		
	}//end-method 
    
} //end-class 
