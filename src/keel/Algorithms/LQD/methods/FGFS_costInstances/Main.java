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

package keel.Algorithms.LQD.methods.FGFS_costInstances;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;


/**
 * 
 * File: Main.java
 * 
 * Read the parameters given by the user.
 * Read the training file
 * Obtain the partitions
 * Replace the missing values
 * Convert imprecise data in crisp
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
	public static void main(String[] args) throws IOException {
		
		
		//Read the parameters of the method. This parameters are in one .txt
		String parameters = args[0];
		parameters par = new parameters(args[0]);
				
		
		int cont=0;
		while(cont<par.files)
		{
			String nentrada=par.original_data+cont+"train.txt";//
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
				System.out.println("El archivo no existe");
				System.exit(0);
			}
       
			BufferedReader input = new BufferedReader(new FileReader(nentrada)); 
			Character caracter;
        
       
        int dimx;//number of features        
        int ncol;//number of columns        
        int ninstance;//number of instances   
        
        
        dimx=Integer.parseInt(input.readLine());        
        System.out.println("\n Number of variables: "+dimx);
        ncol= dimx+1;        
         
        ninstance=Integer.parseInt(input.readLine());        
        System.out.println("Instances: "+ninstance+"\n");
        int nclasses=Integer.parseInt(input.readLine());        
        System.out.println("Classes: "+nclasses+"\n");
        
             
        
        //Save the values of each instances
        fuzzy X[][] = new fuzzy[ninstance][dimx];
        Vector<Vector<Float>> C= new Vector<Vector<Float>>(ninstance);//the output will be a set of elements
        Vector<fuzzy> P= new Vector<fuzzy>(ninstance); //cost of the instance when duplicate with crisp
        
        Interval rangeX[]= new Interval[dimx];//the minimum and maximum of each variable
        
        for(int j=0; j<ncol-1; j++)
    	{
        	Interval nuevo= new Interval(-1,-1);
			rangeX[j]= nuevo;
    	}
       

        String number= "";
        //Read the file
        for(int i=0; i<ninstance; i++)
        {
        	for(int j=0; j<ncol-1; j++)
        	{
        		caracter = (char)input.read();
        		while(caracter!=' ' && caracter!='\n')
        		{
       				number= number + caracter;
       				caracter = (char)input.read();
       			}
        			
       			if(caracter==' ')
       			{
       				if(number.compareTo("-")==0)
        			{
        				fuzzy nuevo= new fuzzy();
        				nuevo.borrosotriangular(MISSING, MISSING, MISSING);
        				X[i][j]= nuevo;
       				}
       				else
       				{
       					X[i][j]=fun_aux.to_fuzzy(number);
       					//Found the range of the variable
       					if(i==0 || (rangeX[j].getmax()==-1 && rangeX[j].getmin()==-1))
       					{
       						Interval nuevo = new Interval(X[i][j].geta(),X[i][j].getd());
        					rangeX[j]=nuevo;
        				}
        				
        				if(X[i][j].getd() > rangeX[j].getmax())
        					rangeX[j].setmax(X[i][j].getd());
            		
        				if(X[i][j].geta() < rangeX[j].getmin())
       						rangeX[j].setmin(X[i][j].geta());
       				}
       				number="";
        				
        			if(X[i][j].geta()>X[i][j].getd())
        			{
        				System.out.println("Incorrect values in the file: Values of the style [4,1]"+X[i][j].geta()+ " "+X[i][j].getd());
        				System.exit(0);
       				}
       			}
        			
        	}
        	
        	caracter = (char)input.read();//read {
        	while(caracter!=' ' && caracter!='\n')
			{
				number= number + caracter;
				caracter = (char)input.read();
			}
        	P.add(i,fun_aux.trapezoidal(number));	
        	
        	number="";
        	caracter = (char)input.read();//leemos la llave
        
        	Vector <Float> salidas_imp= new Vector<Float>();
        	while(caracter!='}')
    		{
        		caracter = (char)input.read();
        		while(caracter!=',' && caracter!='}')
        		{
        			number= number + caracter;
        			caracter = (char)input.read();
        		}
        		
    			salidas_imp.addElement(Float.parseFloat(number));
    			number="";
    			
    			if(caracter!='}')
    			caracter = (char)input.read();//lee el espacio
    			
    		}
        	C.add(i,salidas_imp);
        	caracter = (char)input.read();
        	number="";
        	        	
        }//for read file
        
        input.close();
   
        //Replace the missing values
        X= missing.values_missing(X, ninstance, dimx,1);//1 indicates mean
       

        //Obtain the crisp version  from imprecise data (if the user wants to work with crisp data)  
        String crisp=par.OutputName;
        Vector<Vector<fuzzy>> X1 = new Vector<Vector<fuzzy>>();
        Interval rangeX1[]= new Interval[dimx];
       
        for(int j=0; j<ncol-1; j++)
   		{
       		Interval nuevo= new Interval(-1,-1);
			rangeX1[j]= nuevo;
   		}
        int es_crisp=0;
	 
		
	   if(crisp.contains("Crisp"))
	   {
		   es_crisp=1;
		   for(int i=0;i<X.length;i++)
			{
				for(int j=0;j<X[i].length;j++)
				{
				   X[i][j]=X[i][j].media();
				   
				   if(i==0 || (rangeX[j].getmax()==-1 && rangeX[j].getmin()==-1))
					{
						Interval nuevo = new Interval(X[i][j].geta(),X[i][j].getd());
						rangeX1[j]=nuevo;
					}
				   if(X[i][j].getd() > rangeX1[j].getmax())
						rangeX1[j].setmax(X[i][j].getd());
					if(X[i][j].geta() < rangeX1[j].getmin())
						rangeX1[j].setmin(X[i][j].geta());
				}
			}
		    for(int i=0;i<X.length;i++)
	        {
	        		Vector<fuzzy> x2= new Vector<fuzzy>(dimx);
        			for(int v=0;v<X[i].length;v++)
	    	        {
        				x2.addElement(X[i][v]);
	    	        }
        			X1.addElement(x2);
	        }
	   }
	   fuzzy[][] X2;
	   if(X1.size()!=0)
	        X2 = new fuzzy[X1.size()][X1.get(0).size()];
	   else
		   X2 = new fuzzy[1][1];
	        
	    if(crisp.contains("Crisp"))
	 	  {   
	        for(int i=0;i<X1.size();i++)
			{
				for(int j=0;j<X1.get(i).size();j++)
				{
					X2[i][j]=X1.get(i).get(j);
				}
			}
	        
		   
	   }
        
    
	    //Obtain the crisp and fuzzy variables
	    int[] fuzzy= new int[dimx+1];  //zero represents crisp and one represents fuzzy
        
        fuzzy inputs[][];
        if(es_crisp==1)
        {
        	 inputs= new fuzzy[X2.length][dimx];
        	 inputs=X2;
        }
        else
        {
        	 inputs= new fuzzy[X.length][dimx];
        	 inputs=X;
        }
        
        for(int i=0; i<inputs[0].length; i++)
        {
        	boolean es_fuzzy=false;
        	for(int j=0; j<inputs.length; j++)
        	{
        		if(inputs[j][i].geta()!= inputs[j][i].getd())
        			es_fuzzy=true;
        	}
        	
        	if(es_fuzzy==true)
        		fuzzy[i]=1;
        }
        fuzzy[fuzzy.length-1]=0;//is the class of the example
        
        
        
        //Define the partitions 
        Vector<Integer> neparticion = new Vector<Integer>(dimx);  
        Vector<fuzzyPartition> particione = new Vector<fuzzyPartition>(dimx);//guardamos las particiones de las inputs, 
        
        for(int i=0; i<fuzzy.length-1; i++)//The class is treated later
        {
        	if(fuzzy[i]==1)//is fuzzy
        	{
        		neparticion.add(i, numlabels);        	
        	}
        	
        	else
        	{
        		Vector <Float> variables = new Vector<Float>();
        		for(int k=0; k<inputs.length; k++)
        		{
        			if(k==0)
        			{
        				variables.addElement(inputs[k][i].geta());
            		}
            		else 
            		{
            			boolean existe=false;
            			for(int h=0; h<variables.size();h++)
            			{
            				if(variables.get(h)==inputs[k][i].geta())
            				{
            					existe=true;
            					break;
            				}
            			}
            			if(existe==false)
            			{
            				variables.addElement(inputs[k][i].geta());
            			}
            		}
        			
                		
                }//for
        		
        		if(variables.size()>4 && par.partitions!=0)
    			{
    				neparticion.add(i,par.partitions);
    			}
    			else
    				neparticion.add(i, variables.size());
        	
        	}// else crisp
        	fuzzyPartition particion;
        	if(es_crisp==1)
        		particion= new fuzzyPartition(rangeX1[i].getmin(),rangeX1[i].getmax(),neparticion.get(i));
        	else
        		particion= new fuzzyPartition(rangeX[i].getmin(),rangeX[i].getmax(),neparticion.get(i)); 
    		particione.add(i,particion);
    		particione.get(i).show();

        }//for obtain partitions
       
      
        //Variables used in the genetic algorithm
        int costes=par.minimum_risk;
        int reemplazar=par.reemplazo;
  	   int alfa=par.alfa;

	   Vector<Float> values_classes= new Vector<Float>(nclasses);
	   values_classes = par.classes;
	   Vector<Vector<fuzzy>> cost= new Vector<Vector<fuzzy>>(nclasses);
	  
	   String matrix= par.type_risk;
		  
		   if(matrix.compareTo("I")==0)//cost matrix defined by interval
		   {
		   
			   Vector<fuzzy> c;
			   int matrix_position=0;
			   for(int i=0;i<nclasses;i++)
			   {
				   c= new Vector<fuzzy>(nclasses);
				   
				   for(int j=0;j<nclasses;j++)
				   {
					
					   fuzzy coste = new fuzzy();
					   float iz=par.costs.get(matrix_position);
					   matrix_position++;
					   float d=par.costs.get(matrix_position);
					   matrix_position++;
					   coste.borrosorectangular(iz, d);
					   c.add(j,coste);
				   }
				   
				   cost.add(i,c);
				  
				   
			   }
		   } //if matrix I
		   else //cost matrix defined by linguistic terms
		   {
			   float distancia=(float)1/(float)((8*2)+(1*3));
			   Vector<fuzzy> c;
			   int position_cost=0;
			   for(int i=0;i<nclasses;i++)
			   {
				   c= new Vector<fuzzy>(nclasses);
				   for(int j=0;j<nclasses;j++)
				   {
					   float iz=0,ce=distancia,ce2=distancia*2,de=distancia*3;
					   float etiqueta=par.costs.get(position_cost);
					   System.out.println("The cost of the class "+i+" respect to "+j+" is "+etiqueta);
					   int conta=1;
					   if(etiqueta>0 && etiqueta<10)
					   {
						   while(conta<etiqueta)
						   {
							   iz=ce2;
							   ce=de;
							   ce2=de+distancia;
							   de=ce2+distancia;
							   conta++;
							   
						   }
					   }
					   
					   else if(etiqueta==0)
					   {
						   iz=ce=ce2=de=0;
					   }
					   else if(etiqueta>=10)
					   {
						   iz=ce=ce2=de=1;
					   }
					   
					   fuzzy coste = new fuzzy();
					   coste.borrosotrapezoidal(iz, ce, ce2, de);
					   c.add(j,coste);
					   position_cost++;   
				   }
				   cost.add(i,c);
				 
			   }
			   
			   
		   } //else
		   
		  

		String nombre;
		if(par.minimum_risk==0)
			nombre=par.OutputName+par.nameAlgorithm+"E_"+par.type_risk+cont+".dat";
		else
			nombre=par.OutputName+par.nameAlgorithm+"R_"+par.type_risk+cont+".dat";
		
		String dist=par.OutputName+"Test1000"+par.nameAlgorithm+cont+".dat";
		String columns = par.OutputName+par.nameAlgorithm+"Outputs"+cont+".dat";
		String nombre_train  =  par.original_data+cont+"train.txt";
		String n_test=par.original_data+"T-"+cont+"test.txt";
		AlgGenetic AG;
		if(es_crisp==0)
		{
			AG= new AlgGenetic(npoblacion, muta,cross,particione, nclasses,X,C,P,niteraciones,reemplazar,costes,nombre,alfa,n_test,nombre_train,values_classes,cost,ninstance,par.dominance,par.winner_rule,0,matrix,es_crisp,dist,columns);
		}
		else//the data are crisp
			AG= new AlgGenetic(npoblacion, muta,cross,particione, nclasses,X2,C,P,niteraciones,reemplazar,costes,nombre,alfa,n_test,nombre_train,values_classes,cost,ninstance,par.dominance,par.winner_rule,0,matrix,es_crisp,dist,columns);
		cont++;
  	   }
       
		
	}

}
