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

package keel.Algorithms.LQD.methods.FGFS_Minimum_Risk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
		
			String nentrada=par.original_data+cont+"tra.dat";//
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
			String amplitude=par.original_data+"Instances"+cont+".txt";//
			File fea = new File(amplitude);
			if(fea.exists()==true)
			{
				BufferedReader ampl = new BufferedReader(new FileReader(amplitude));
				ampl.readLine();
				instances=Integer.parseInt(ampl.readLine());
				ampl.close();
				
			}
       
  
        
			//Save the values of each instances
        fuzzy X[][] = new fuzzy[instances][dimx];
        Vector<Vector<Float>> C= new Vector<Vector<Float>>(instances);//the output will be a set of elements
        Vector<Float> W= new Vector<Float>(instances);//Cost of the instance when duplicate with crisp. 
        //In the imprecise data is always 1 for all the instances
        
        Interval rangeX[]= new Interval[dimx];//the range of the variable (minimum and maximum)
        
        for(int j=0; j<ncol-1; j++)
    	{
        	Interval nuevo= new Interval(-1,-1);
			rangeX[j]= nuevo;
    	}
       


		String number= "";
        //Read the input file
        int lines=1+dimx+4;
        for(int i=0;i<lines;i++)
        	input.readLine();    

        
        //READ THE FILE
        for(int i=0; i<instances; i++)
        {
        	for(int j=0; j<ncol-1; j++)
        	{
        		
        		character = (char)input.read();
        		if(character=='\n')
        			character = (char)input.read();	
        	
        		while(character!=']' && character!='\n' && character!='?')
        		{
        			number= number + character;
        			character = (char)input.read();
        		}
        			
        		if(character==']' || character=='?')	
        		{
        			number= number + character;
        			character = (char)input.read();//read ,
        			if(number.compareTo("?")==0)
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
        		
        	//Read the classes of the instance {1,..,x} (imprecise output)
        	character = (char)input.read();//read {
        	Vector <Float> salidas_imp= new Vector<Float>();
        	while(character!='}')
        	{
        		character = (char)input.read();//begin with a number
        		while(character!=',' && character!='}')
        		{
        			number= number + character;
        			character = (char)input.read();
        		}
    		
        		salidas_imp.addElement(Float.parseFloat(number));
        		number="";
        	}
    			
    		C.add(i,salidas_imp);
        	character = (char)input.read();
        	number="";
        	W.add(i,(float)1);
        	
        }//for read file
        
        input.close();//cerramos el fichero
       
        //Replace the missing values
        X= Missing.values_missing(X,instances, dimx,1);
       
  
        //Obtain the crisp version  from imprecise data (if the user wants to work with crisp data)
        String crisp=par.OutputName;
        Vector<Vector<fuzzy>> X1 = new Vector<Vector<fuzzy>>();
        Vector<Vector<Float>> C1= new Vector<Vector<Float>>();
        Interval rangeX1[]= new Interval[dimx];
        for(int j=0; j<ncol-1; j++)
   		{
       		Interval nuevo= new Interval(-1,-1);
			rangeX1[j]= nuevo;
   		}
        Vector<Float> P1= new Vector<Float>();
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
		   //Duplicate the example with imprecise output
	        for(int i=0;i<C.size();i++)
	        {
	        	if(C.get(i).size()>1)
	        	{
	        		for(int j=0;j<C.get(i).size();j++)
	    	        {
	        			Vector<fuzzy> x2= new Vector<fuzzy>(dimx);
	        			for(int v=0;v<X[i].length;v++)
		    	        {
	        				x2.addElement(X[i][v]);
		    	        }
	        			X1.addElement(x2);
	        			Vector<Float> C2= new Vector<Float>();
	        			C2.addElement(C.get(i).get(j));
	        			C1.addElement(C2);
	        			P1.addElement((float)1/C.get(i).size());
	    	        }
	        	}
	        	else
	        	{
	        		Vector<fuzzy> x2= new Vector<fuzzy>(dimx);
        			for(int v=0;v<X[i].length;v++)
	    	        {
        				x2.addElement(X[i][v]);
	    	        }
        			X1.addElement(x2);
	        		Vector<Float> C2= new Vector<Float>();
	        		C2.addElement(C.get(i).get(0));
	        		C1.addElement(C2);
	        		P1.addElement((float)1/C.get(i).size());
	        		
	        	}
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
	    int[] fuzzy= new int[dimx+1]; //zero represents crisp and one represents fuzzy
        
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
        Vector<fuzzyPartition> particione = new Vector<fuzzyPartition>(dimx); 
        
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
        			
                		
                }// for

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

        }// for obtain partitions
       

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
		   
		   
		   
	   }//else
		  

	    String nombre;
		if(par.minimum_risk==0)
	    	nombre=par.OutputName+par.nameAlgorithm+"E_"+par.type_risk+cont+".dat";
	    else
	    	nombre=par.OutputName+par.nameAlgorithm+"R_"+par.type_risk+cont+".dat";
		
		String dist=par.OutputName+"Test1000"+par.nameAlgorithm+cont+".dat";
		String columns = par.OutputName+par.nameAlgorithm+"Outputs"+cont+".dat";
		String nombre_train  =  par.original_data+cont+"tra.dat";
		String n_test=par.original_data+cont+"tst.dat";
		AlgGenetic AG;
		if(es_crisp==0)
		{
			AG= new AlgGenetic(npoblacion, muta,cross,particione, nclasses,X,C,W,niteraciones,reemplazar,costes,nombre,alfa,n_test,nombre_train,values_classes,cost,instances,par.dominance,par.winner_rule,0,matrix,es_crisp,dist,columns);
		}
		else//the data are crisp
			AG= new AlgGenetic(npoblacion, muta,cross,particione, nclasses,X2,C1,P1,niteraciones,reemplazar,costes,nombre,alfa,n_test,nombre_train,values_classes,cost,instances,par.dominance,par.winner_rule,0,matrix,es_crisp,dist,columns);
		cont++;
  	   }
       
		
	}

}
