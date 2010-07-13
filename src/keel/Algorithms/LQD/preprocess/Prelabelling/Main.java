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

package keel.Algorithms.LQD.preprocess.Prelabelling;

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
        		
        		//System.out.print( "the character read is "+caracter );
        		
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
            /*
            for(int j=0;j<costes.size();j++)
        	{
            	System.out.println( "cost "+j+" ");
            	costs.get(j).show();
        	}*/
            
            Vector<Vector<fuzzy>> noclassified= new Vector<Vector<fuzzy>>();//the output will be a set of elements
            
       			
            
            while(U.size()!=0)
            {
            	
            	//Calculate the partitions from L and the number of labels
            	for(int j=0;j<dimx;j++)
				{
            	
            		if(L.get(L.size()-1).get(j).getd() > rangoL[j].getmax())
            			rangoL[j].setmax(L.get(L.size()-1).get(j).getd());
			
            		if(L.get(L.size()-1).get(j).geta() < rangoL[j].getmin())
            			rangoL[j].setmin(L.get(L.size()-1).get(j).geta());
				}
            	
            	Vector<fuzzypartition> particione = new Vector<fuzzypartition>(dimx);
            	particione=partitions(L,numlabels,dimx,rangoL);
            	
            	fuzzy actual= new fuzzy(0);
            	int setWinnerU=-1;
            	Vector<Float> semi_label = new Vector<Float>();
            	Vector<fuzzy> setU= new Vector<fuzzy>();
            	float label=-1;
            	
            	
            	Vector<Vector<fuzzy>> compatible = new Vector<Vector<fuzzy>>();
            	for(int i=0;i<U.size();i++)
            	{
            		

            		Vector<fuzzy> comp= new Vector<fuzzy>();
            		comp.addElement(new fuzzy(-1));
            		if(contain(U.get(i),noclassified)==false)
            		{
            			fuzzyrule regla= new fuzzyrule(particione,U.get(i));
            			comp.clear();
            			for(int j=0;j<L.size();j++)
            			{
            				//Membership of U respect to L
            				//System.out.println("Show the instance L "+j);
            				comp.addElement(fuzzy.multi(P.get(j),regla.match_alpha(L.get(j),particione, alfa)));
            				//comp.get(comp.size()-1).show();
            				//new BufferedReader(new InputStreamReader(System.in)).readLine();
            				
            				if(Ranking.wang(actual,comp.get(comp.size()-1))==1) //(actual<membership)
    						{
            				
            					//Save U (more compatible)
            					actual=comp.get(comp.size()-1);
            					setWinnerU=i;
         
            					//System.out.println("The U is " + i);
            					setU=U.get(i);
            					//new BufferedReader(new InputStreamReader(System.in)).readLine();
            					semi_label= CU.get(i);
    						}
    						
            			}
            			
            			
            		} //end if contain
            		compatible.addElement(comp);
            	}//end if U
            	
            	//Sort L respect to the compatibility of U obtained, setWinnerU
            	if(setWinnerU!=-1)
            	{
            		//System.out.println("We obtain one U compatible with L "+setWinnerU);
            		int equal=1;
            		Vector<Float> Lcolocados=order(CL,compatible.get(setWinnerU));
            		int k=3;
            		/*if(k==1)
            		{
            			label=Lcolocados.get(0);
            			System.out.println("the label is "+label);
            		}
            		else
            		{*/
            				for(int j=1;j<k;j++)
                			{
            				//	System.out.println("the "+j+" label is "+Lcolocados.get(j));
            					if(Lcolocados.get(0).compareTo(Lcolocados.get(j))!=0)
            					{
            						equal=0;
            						break;
            					}
                			}
            			
            			if(equal==1)
            			{
            				label=Lcolocados.get(0);
            				//System.out.println("all k are the same "+label);
            			}
            			else //the label es the most freq.
            			{
            				//if(k>2)
            				//{
            					if(Lcolocados.get(0).compareTo(Lcolocados.get(1))==0 || Lcolocados.get(0).compareTo(Lcolocados.get(2))==0)
            						label=Lcolocados.get(0);

            					else if(Lcolocados.get(1).compareTo(Lcolocados.get(2))==0)
            						label=Lcolocados.get(1);

            				//}
            			}
            				
            		//}
            		
            		//new BufferedReader(new InputStreamReader(System.in)).readLine();
            		//Look for if the actual label is contained in the semi-labelled, setWinnerU
                	if(semi_label.contains(label)==true)
                	{
                	    //System.out.print("the instance semi-labelled contains the label selected");
                		U.remove(setWinnerU);
                		CU.remove(setWinnerU);
                		
                		/*for(int i=0;i<U.size();i++)
                        {
                        	for(int j=0;j<U.get(i).size();j++)
                        	{
                        		U.get(i).get(j).show();
                        	}
                        	for(int j=0;j<CU.get(i).size();j++)
                        	{
                        		System.out.println("output is "+CU.get(i).get(j));
                        	}
                        }
                		  
                		new BufferedReader(new InputStreamReader(System.in)).readLine();*/
                		
                		L.addElement(setU);
                		CL.addElement(label);
                		//Obtain the cost of the instance
                		int pos=-1;
               			for(int v_clases=0; v_clases<values_classes.size(); v_clases++)
               			{
               				if(values_classes.get(v_clases).compareTo(label)==0)
            			
               				{
               					pos=v_clases;
               					break;           				
               				}
               			}
                		P.addElement(costs.get(pos));
                		//System.out.println("the cost of the instances is ");
                		//P.get(P.size()-1).show();
                		noclassified.clear();
                		
                		/* for(int i=0;i<L.size();i++)
                         {
                         	for(int j=0;j<L.get(i).size();j++)
                         	{
                         		L.get(i).get(j).show();
                         	}
                         	System.out.println("label is "+CL.get(i)+" and the cost ");
                         	P.get(i).show();
                         	
                         }*/
                 		  
                    //     new BufferedReader(new InputStreamReader(System.in)).readLine();
                         
                	}
                	else  
                	{
                		//System.out.println("Insert U in no classified and the U is ");
                		//for (int c=0;c<setU.size();c++)
                		//	setU.get(c).show();

                		//Insert the U in no classified
                		noclassified.addElement(setU);
                		/*System.out.println("All in no classified are");
                		for (int c1=0;c1<noclassified.size();c1++)
                		{
                			System.out.println("Is "+c1);
                			for (int c2=0;c2<noclassified.get(c1).size();c2++)
                				noclassified.get(c1).get(c2).show();
                		}
                    	new BufferedReader(new InputStreamReader(System.in)).readLine();*/
    					
                	}
            	} //if we obtain one U compatible with L
            	else //Non U is compatible with L
            	{
            		noclassified.clear();
            		for (int u=0;u<U.size();u++)
            		{
            			noclassified.addElement(U.get(u));
            		}
            		
            		//nocompatibilidad=true;
            	}
            	
            	
               	//no classified equal a U --> Non U is compatible with L or the label
            	//selected is different that the provided by the expert (semi-labelled)
            	if(equal(noclassified,U)==true)
            	{
            		
            			
            			L.addElement(U.get(0));
            			if(semi_label.contains(relevant)==false)
                    	{
            				for(int r=relevants.size()-1; r==0;r--)
            				{
            					if(semi_label.contains(relevants.get(r))==true)
            					{
            						relevant=relevants.get(r);
            						break;
            					}
            				}
                    	}
            			CL.addElement(relevant);

            			int pos=-1;
               			for(int v_clases=0; v_clases<values_classes.size(); v_clases++)
               			{
               				if(values_classes.get(v_clases).compareTo(relevant)==0)
            			
               				{
               					pos=v_clases;
               					break;           				
               				}
               			}
               			P.addElement(costs.get(pos));
	            		
            			if(U.size()>1)
            			{
            				U.remove(0);
            				CU.remove(0);
            			}
            			else
            			{
            				U.clear();
            				CU.clear();
            			}
            			noclassified.clear();
            		 
            	} //noclassified==U
            	
                
            	
            }// del while

         
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
			
			
	
			fs1.close();
	  
		//cont++;
  	   //}
       
		
	}
	public static float freq(Vector<Float> CL, Vector<Float> valores)
	{
		int cantidad=0;
		Vector<Integer> cantidades = new Vector<Integer>(valores.size());
		for(int j=0;j<valores.size();j++)
		{
			cantidades.add(j,0);
		}
		for(int i = 0;i <CL.size(); i++)
		{
			for(int j=0;j<valores.size();j++)
			{
				if(CL.get(i)==valores.get(j))
				{
					cantidades.set(j, cantidades.get(j)+1);
					break;
				}
			}
		}
		
		for (int i = 0; i < cantidades.size(); i++) 
		{
			for (int j = 0; j < cantidades.size(); j++) 
			{
				if(cantidades.get(i)<cantidades.get(j)) 
				{
					cantidad = cantidades.get(i);
					cantidades.set(i,cantidades.get(j));
					cantidades.set(j,cantidad);				
				}
			}
		}
		
		
		return valores.get(0);
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
	public static Vector<Float> order ( Vector<Float> CL, Vector<fuzzy> comp) throws IOException
	{
		 Vector<Float> Lcolocado= new  Vector<Float>();
		 for(int i=0;i<CL.size();i++)
			 Lcolocado.addElement(CL.get(i));
		 

		 fuzzy temporal = new fuzzy();
		 float tem=-1;
		 for (int i = 0; i < comp.size(); i++) 
			{
				for (int j = 0; j < comp.size(); j++) 
				{

					if(Ranking.wang(comp.get(i),comp.get(j))==0)
					{
						temporal = comp.get(i);
						comp.set(i,comp.get(j));
						comp.set(j,temporal);
						
						tem = Lcolocado.get(i);
						Lcolocado.set(i,Lcolocado.get(j));
						Lcolocado.set(j,tem);
					
					}
				}
			}
			
			
		
		 
		 return Lcolocado;
	}
	public static boolean equal(Vector<Vector<fuzzy>> U, Vector<Vector<fuzzy>> noclassified) throws IOException
	{
		Vector<Vector<fuzzy>> copiau= new Vector<Vector<fuzzy>>();
		for (int i=0;i<U.size();i++)
		{
			Vector<fuzzy> conte = new Vector<fuzzy>();
			for(int j=0;j<U.get(i).size();j++)
			{
				conte.addElement(U.get(i).get(j));
			}
			copiau.addElement(conte);
		}
		boolean contenido=true;
		if(noclassified.size()==0 || copiau.size()!=noclassified.size())
			return false;
		
		else
		{
		
			for(int i=0;i<noclassified.size();i++)
			{
			
				for(int u=0;u<noclassified.size();u++)
				{
					contenido=true;
					
						for(int v=0;v<noclassified.get(i).size();v++)
						{
							
				
							if(copiau.get(u).get(v).equal(noclassified.get(i).get(v))==false)
							{
								
								contenido=false;
								v=noclassified.get(i).size();
							}
						}
						if(contenido==true)
						{
							copiau.remove(u);
						}
					//}
					if(contenido==true)
						break;
				}
			}
		}
			
		
		if(copiau.size()==0)
			return true;
		else
			return false;

			
	}
	public static boolean contain(Vector<fuzzy> U, Vector<Vector<fuzzy>> noclassified)
	{
		boolean contenido=true;
		if(noclassified.size()==0)
		{
		
			return false;
		}
		else
		{
			for(int i=0;i<noclassified.size();i++)
			{
				contenido=true;
				for(int v=0;v<noclassified.get(i).size();v++)
				{
					if(U.get(v).equal(noclassified.get(i).get(v))==false)
					{
						contenido=false;
					}
					
				}
				if(contenido==true)
					return true;
			}
		}
			
		return contenido;
		
	}
	public static Vector<fuzzypartition> partitions(Vector<Vector<fuzzy>> L, int numlabels, int dimx,interval rangoL[])
	{
	//obtain the partitions
        
        int[] fuzzy= new int[dimx+1]; //zero is crisp and one is fuzzy
        
 
        for(int i=0; i<L.get(0).size(); i++)
        {
        	boolean es_fuzzy=false;
        	for(int j=0; j<L.size(); j++)
        	{
        		if(L.get(j).get(i).geta()!= L.get(j).get(i).getd())
        			es_fuzzy=true;
        	}
        	
        	if(es_fuzzy==true)
        		fuzzy[i]=1;
        }
        fuzzy[fuzzy.length-1]=0;//is the class
        
        
        
        // Definition of the partitions
        Vector<Integer> neparticion = new Vector<Integer>(dimx);  
       
        Vector<fuzzypartition> particione = new Vector<fuzzypartition>(dimx);
        
        for(int i=0; i<fuzzy.length-1; i++)
        {
        	if(fuzzy[i]==1)//is fuzzy
        	{
        	
        				neparticion.add(i, numlabels);
    
        	}
        	
        	else//is crisp
        	{
        		
        		Vector <Float> variables = new Vector<Float>();
        		for(int k=0; k<L.size(); k++)
        		{
        			if(k==0)
        			{
        				variables.addElement(L.get(k).get(i).geta());
            		}
            		else
            		{
            			boolean existe=false;
            			for(int h=0; h<variables.size();h++)
            			{
            				if(variables.get(h)==L.get(k).get(i).geta())
            				{
            					existe=true;
            					break;
            				}
            			}
            			if(existe==false)
            			{
            				variables.addElement(L.get(k).get(i).geta());
            			}
            		}
        			
        		}//for

        		if(variables.size()>4 && numlabels!=0)
    			{
        			neparticion.add(i,numlabels);
    			}
    			else
    				neparticion.add(i, variables.size());
        	
        	}//else crisp
        	fuzzypartition particion= new fuzzypartition(rangoL[i].getmin(),rangoL[i].getmax(),neparticion.get(i)); 
    		particione.add(i,particion);
    		
        }// for obtain partitions
        
        return particione;
       
		
	}
	

}
