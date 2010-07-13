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

package keel.Algorithms.LQD.preprocess.Expert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;
import java.io.FileWriter;


/**
*
* File: Main.java
*
* Apply a prelabelling method, to get that the semi-labelled or unlabelled instances
* have only one class. If the example is semi-labelled the expert in the field
* is given meta-information about the possible class of the example
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
	public static void main(String[] args) throws IOException 
	{
		
    	
		//Read the parameters of the method. This parameters are in one .txt
		String parameters = args[0];
		parameters par = new parameters(args[0]);
		
		
		//Files
		String ninput=par.original_data+".dat";//
        System.out.println("\n Input File: "+ninput);
		FileWriter fs1= new FileWriter(par.OutputName+".dat");

		int numlabels=par.partitions;		//Number of partitions for the low quality variables
		int alfa = par.alfa;			   //number of alfa cuts
		int dimx=par.dimx;				   //Number of variables        
		int ncol=dimx+1;       		       //Number of columns in the input file        
        int instances=par.instances;       //Number of instances
        int nclasses = par.nclasses;
        
                	
        	
        File fe = new File(ninput);
        if(fe.exists()==false)
        {
        	System.out.println("The file does not exist");
        	System.exit(0);
        }

        BufferedReader input = new BufferedReader(new FileReader(ninput)); 
        Character character =null;
          
            
        fuzzy X[][] = new fuzzy[instances][dimx];                   //Value of the features in each instance
        Vector<Vector<fuzzy>> L= new Vector<Vector<fuzzy>>();       //The output will be a set of elements (labelled)
        Vector<Vector<fuzzy>> U= new Vector<Vector<fuzzy>>();       //The output will be a set of elementes (semi-labelled or unlabelled)
        Vector<fuzzy> P= new Vector<fuzzy>(); 						//Cost of the instances
            
        Vector<Vector<Float>> C= new Vector<Vector<Float>>(instances);   //The output will be a set of elements (classes of instances)
        Vector<Float> CL= new Vector<Float>();                            //Classes of labelled instances
        Vector<Vector<Float>> CU= new Vector<Vector<Float>>();            //Classes of semi-labelled or unlabelled instances
        interval rangoL[]= new interval[dimx];                          //Minimum and maximum of each variable            
        for(int j=0; j<ncol-1; j++)
        {
        	interval nuevo= new interval(-1,-1);
        	rangoL[j]= nuevo;
        }
       
            
        String numero= "";
        //Read the input file
        int lines=1+dimx+4;
        for(int i=0;i<lines;i++)
        	input.readLine();    
        
        boolean leido=false;
        for(int i=0; i<instances; i++)
        {
        	for(int j=0; j<ncol-1; j++)
        	{
        		if(leido==false)
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
        		
        				fuzzy nuevo= new fuzzy();
        				nuevo.borrosotriangular(MISSING, MISSING, MISSING);
        				X[i][j]= nuevo;
        			}
        			else
        			{
        		
        				X[i][j]=fun_aux.to_fuzzy(numero);
        			}
        				
        			numero="";
        				
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
        		character = (char)input.read();
        		while(character!=',' && character!='}')
        		{
        			numero= numero + character;
        			character = (char)input.read();
        		}
        		salidas_imp.addElement(Float.parseFloat(numero));
        		numero="";
        			
        			
        	}
        	C.add(i,salidas_imp);
        	if(salidas_imp.size()==1) //instances labelled (only one class)
        	{
        		CL.add(salidas_imp.get(0));
        		Vector<fuzzy> l = new Vector<fuzzy>();
        		for(int j=0;j<X[i].length;j++)
        		{
        					l.addElement(X[i][j]);
        					if(i==0 || (rangoL[j].getmax()==-1 && rangoL[j].getmin()==-1))
        					{
        						interval nuevo = new interval(X[i][j].geta(),X[i][j].getd());
        						rangoL[j]=nuevo;
        					}
        				
        					if(X[i][j].getd() > rangoL[j].getmax())
        						rangoL[j].setmax(X[i][j].getd());
            			
        					if(X[i][j].geta() < rangoL[j].getmin())
        						rangoL[j].setmin(X[i][j].geta());
        					
        		}
        			      				  
            		L.addElement(l);
            		P.addElement(new fuzzy(1));
            		
        	}
            	else //instances semi-labelled or unlabelled (several classes or anything)
            	{
            		CU.add(salidas_imp);
            		Vector<fuzzy> u = new Vector<fuzzy>();
            		for(int j=0;j<X[i].length;j++)
        			{
            			u.addElement(X[i][j]);
        			}      				  
            		U.addElement(u);
            	}
            	character = (char)input.read();//\n after }
            	character = (char)input.read();
            	if(character.compareTo('[')==0)
            		leido=true;
            	else
            		leido=false;
            	
            	
            	numero="";
            	

            	
            }//for read
            input.close();
              	        
            //missing values replaced by one interval (minimum of minumim, maximum of maximum)
            L= missing.values_missing(L, L.size(), dimx,2);
            U= missing.values_missing(U, U.size(), dimx,2);
          
            Vector<Float> values_classes= new Vector<Float>(nclasses);
   		   
            for(int i=0;i<nclasses;i++)//nclases is the number of classes
   		   	{
   			   values_classes.add(par.classes.get(i));
   			   //System.out.println("classes"+valores_clases.get(i));
   		   	}
   	  
            float distancia=(float)1/(float)((8*2)+(1*3));
            Vector<fuzzy> costs= new Vector<fuzzy>(nclasses);
   				   
            for(int j=0;j<nclasses;j++)//nclases is the number of classes
            {
            	float iz=0,ce=distancia,ce2=distancia*2,de=distancia*3;
            	// System.out.println("values "+cont+" iz "+iz+" ce "+ce+" ce2 "+ce2+" de "+de);
            	float etiqueta=(par.costs.get(j));
            	int conta=1;
            	//System.out.println("values "+cont+" iz "+iz+" ce "+ce+" ce2 "+ce2+" de "+de);
            	if(etiqueta>0 && etiqueta<10)
            	{
            		while(conta<etiqueta)
            		{
            			iz=ce2;
            			ce=de;
            			ce2=de+distancia;
            			de=ce2+distancia;
            			//System.out.println("values "+cont+" iz "+iz+" ce "+ce+" ce2 "+ce2+" de "+de);
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
            	fuzzy cost = new fuzzy();
            	cost.borrosotrapezoidal(iz, ce, ce2, de);
            	costs.add(j,cost);
            }   
   			
            Vector<Float> relevants = ranking(costs,values_classes);
            float relevant = relevants.get(relevants.size()-1);
          

            fs1.write(dimx+"\n");
			fs1.write(L.size()+"\n");
			fs1.write(nclasses+"");
			for(int e=0;e<L.size();e++)
			{
				fs1.write("\n");
				for(int a=0;a<dimx;a++)
				{
					fs1.write(fuzzy.fichero(L.get(e).get(a))+" ");
				}
				fs1.write("["+P.get(e).a+","+P.get(e).b+","+P.get(e).c+","+P.get(e).d+"]"+" ");
				fs1.write("{");				
				fs1.write(CL.get(e)+"");
				fs1.write("}");				
			}
			for(int e=0;e<U.size();e++)
			{
				fs1.write("\n");
				for(int a=0;a<dimx;a++)
				{
					fs1.write(fuzzy.fichero(U.get(e).get(a))+" ");
				}
				fs1.write("[1.0,1.0,1.0,1.0] ");
				fs1.write("{");				
				fs1.write(relevant+"");
				fs1.write("}");				
			}
			
			
	
			fs1.close();
		
	  
		
		
	}
	
	public static Vector<Float> ranking (Vector<fuzzy> costes, Vector<Float> valores) throws IOException
	{
		
		
	
		 fuzzy temporal = new fuzzy();
		 Vector<fuzzy> coste = new Vector<fuzzy>();
		 Vector<Float> valor = new Vector<Float>();
		 for(int i=0;i<valores.size();i++)
			 valor.addElement(valores.get(i));
		 for (int i = 0; i < costes.size(); i++) 
		 {
			 coste.addElement(costes.get(i));
		 }
		 
		 float tem=-1;
		 for (int i = 0; i < coste.size(); i++) 
			{
				for (int j = 0; j < coste.size(); j++) 
				{
					
					if(Ranking.wang(coste.get(i),coste.get(j))==1) 
					{
						
						temporal = coste.get(i);
						coste.set(i,coste.get(j));
						coste.set(j,temporal);
						
						tem = valor.get(i);
						valor.set(i,valor.get(j));
						valor.set(j,tem);
					
					}
				}
			}
			
		
		
		return valor;//.get(valor.size()-1);
	}
	
	

	

}
