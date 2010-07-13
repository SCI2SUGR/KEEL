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

package keel.Algorithms.LQD.methods.FGFS_Original;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * 
 * File: Main.java
 * 
 * Read the parameters given by the user.
 * Read the training file
 * Obtain the partitions
 * Replace the missing values
 * 
 * 
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */

public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	static float MISSING=-2;
	static public parameters par;
	public static void main(String[] args) throws IOException {
		
		
		//String parameters = args[0];
		par = new parameters(args[0]);
		
		//int cont=0;
		//while(cont<100)
		//{
			String nentrada=par.original_data;//
			System.out.println("\n Input File: "+nentrada);
        
			int numlabels = par.partitions; //Number of partitions
			int niteraciones=par.niterations; //number of iterations
			int npoblacion=par.npoblation;//Size of poblation
			float cross=par.cross;
			float muta = par.muta;
       
			//Creamos el fichero de entrada
			File fe = new File(nentrada);
			if(fe.exists()==false)
			{
				System.out.println("The file doesn`t exist");
				System.exit(0);
			}
       
			BufferedReader input = new BufferedReader(new FileReader(nentrada)); 
			Character character;
			
			int dimx=par.dimx;				   //Number of variables        
			int ncol=dimx+1;       		       //Number of columns in the input file        
	        int instances=par.instances;       //Number of instances
	        int nclasses = par.nclasses;
	        
	        
	      //Read the input file
	        
	        if(par.partitions_data.contains("10cv")==true) //Calculate the numbers of instances
	        {
	        	int lines=1+dimx+4;
		        for(int i=0;i<lines;i++)
		        	input.readLine();
	        	instances=0;
	        	while(input.readLine()!=null)
	        	{
	        		instances++;
	        	}
	        	 input.close();
	        	 input = new BufferedReader(new FileReader(nentrada));
	        }
	        System.out.println("The numbers of instances is: "+instances);
	        	
			
			
	        Interval X[][] = new Interval[instances][dimx];
	        Vector<Vector<Float>> C= new Vector<Vector<Float>>(instances);
	        
	        Interval rangeX[]= new Interval[dimx];//the range of the variable (minimum and maximum)
	        
	        for(int j=0; j<ncol-1; j++)
	    	{
	        	Interval nuevo= new Interval(-1,-1);
				rangeX[j]= nuevo;
	    	}
	        
	        String numero= "";
	        int lines=1+dimx+4;
	        for(int i=0;i<lines;i++)
	        	input.readLine();
	       
	        //READ THE FILE
	        for(int i=0; i<instances; i++)
	        {
	        	for(int j=0; j<ncol-1; j++)
	        	{
	        		character = (char)input.read();
	            	
	        		while(character!=']' && character!='\n' && character!='?')
	        		{
	        			numero= numero + character;
	        			character = (char)input.read();
	        		}
	        		if(character==']' || character=='?')	
	        		{
	        			numero= numero + character;
	        			character = (char)input.read();//read ,
	        			if(numero.compareTo("?")==0)
	        			{
	        				Interval nuevo= new Interval(MISSING, MISSING);
	        				X[i][j]= nuevo;
	        			}
	        			else
	        			{
	        				X[i][j]=fun_aux.to_fuzzy(numero);
	        				if(i==0 || (rangeX[j].getmax()==-1 && rangeX[j].getmin()==-1))
        					{
        						Interval nuevo = new Interval(X[i][j].getmin(),X[i][j].getmax());
        						rangeX[j]=nuevo;

        					}
        				
        					if(X[i][j].getmax() > rangeX[j].getmax())
        						rangeX[j].setmax(X[i][j].getmax());
                			
        					if(X[i][j].getmin() < rangeX[j].getmin())
        						rangeX[j].setmin(X[i][j].getmin());

        				}
	        			
	        			numero="";
        				
	        			if(X[i][j].getmin()>X[i][j].getmax())
        				{
	        				System.out.println("The values in the file are not correct [4,1]"+X[i][j].getmin()+ " "+X[i][j].getmax() );
        					System.exit(0);
        				}
	        		}
	        	}
	        	//Read the classes of the instance {1,..,x} (imprecise output)
	        	character = (char)input.read();//read {
	        	Vector <Float> salidas_imp= new Vector<Float>();
	        	while(character!='}')
	        	{
	        		character = (char)input.read();//begin with a number
	        		while(character!=',' && character!='}')
	        		{
	        			numero= numero + character;
	        			character = (char)input.read();
	        		}
	    		
	        		salidas_imp.addElement(Float.parseFloat(numero));
	        		numero="";
	        	}
	        	C.add(i,salidas_imp);
	        	character = (char)input.read();
	        	numero="";
	        }//for read file
	        input.close();
	        
	        X= missing.values_missing(X,instances, dimx,1);
        
    int[] fuzzy= new int[dimx+1]; 
        
        for(int i=0; i<X[0].length; i++)
        {
        	boolean es_fuzzy=false;
        	for(int j=0; j<X.length; j++)
        	{
        		if(X[j][i].getmin()!= X[j][i].getmax())
        			es_fuzzy=true;
        	}
        	
        	
        	if(es_fuzzy==true)
        		fuzzy[i]=1;
        }
        fuzzy[fuzzy.length-1]=0;
        
        
        

        Vector<Integer> neparticion = new Vector<Integer>(dimx);  
       
        Vector<partition> particione = new Vector<partition>(dimx);
        partition particions;
        
        
        for(int i=0; i<fuzzy.length-1; i++)
        {
        	if(fuzzy[i]==1)
        	{
        	
        			
        				neparticion.add(i, numlabels);
        		
        	
        	}
        	
        	else
        	{
        		
        		Vector <Float> variables = new Vector<Float>();
        		for(int k=0; k<X.length; k++)
        		{
        			if(k==0)
        			{
        				
        				variables.addElement(X[k][i].getmin());
        				
            		}
            		else 
            		{
            			boolean existe=false;
            			for(int h=0; h<variables.size();h++)
            			{
            				if(variables.get(h)==X[k][i].getmin())
            				{
            					existe=true;
            					break;
            				}
            			}
            			if(existe==false)
            			{
            				
            				variables.addElement(X[k][i].getmin());
            			}
            		}
        			
                		
                }//for
        		
        		if(variables.size()>4 && par.partitions!=0)
    			{
    			
        			neparticion.add(i,par.partitions);
    			}
    			else
    				neparticion.add(i, variables.size());
        	
        	}// else
        	
        	partition particion= new partition(rangeX[i].getmin(),rangeX[i].getmax(),neparticion.get(i)); 
    		particione.add(i,particion);
    		
    		particione.get(i).show();

        }// for obtain partitions
       

        int reemplazar=par.reemplazo;
  	  int alfa=par.alfa;
  	 
  	  Vector<Float> values_classes= new Vector<Float>(nclasses);
	  values_classes = par.classes;
	  
	  String nombre=par.OutputName.substring(0,par.OutputName.lastIndexOf("tra.dat"))+".dat";//args[8];
	  String n_test=par.original_test;
	  String nombre_train  =  par.original_data;
	  String mix = par.OutputName.substring(0,par.OutputName.lastIndexOf('.'))+"-Outputs.dat";
	  String columns = mix;//args[8];
	

	   
	 
  	   
		 
		 
  	AlgGenetic AG= new AlgGenetic(npoblacion, muta,cross,particione, nclasses,X,C,niteraciones,reemplazar,nombre,alfa,n_test,nombre_train,values_classes,instances,1,par.dominance,par.winner_rule,columns);
  	 
   	
	
	  //cont++;
   
//}//while 100 boost


       
		
	}

}
