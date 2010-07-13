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

package keel.Algorithms.LQD.methods.FGFS_Rule_Weight;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

/**
*
* File: AlgGenetic.java
*
* This genetic algorithm obtains fuzzy rule from
* imprecise or low quality. We work with weight rule, 
* this weight will be calculate with different heuristics
* but also with the genetic algorithm (mutation)
*
* @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
* @version 1.0
*/


public class AlgGenetic {
	
	Vector<IndMichigan>poblation;
    Vector<Vector<Float>> fitness;
    Vector<Float> fitness_total;
    Vector<Boolean> compatible;
    Vector<Float> total;
    float PROBMUTA;
    float PROBCRoss;
    int TAMPO;
    int iterations;
    fuzzy[][] X;
    Vector<Vector<Float>> Y;
    int classes;
    Vector<partition> partitions;
    int replace;
    
    int alpha;
    int NOIMPORTA=-1;
    FileWriter fs1;
    String fichero_test;
    String fichero_entre;
    FileWriter dis;
    FileWriter column;
    Vector<Float> values_classes;
    Vector<Vector<Float>> costs;
    int instances;
    int desconocidos;
    
  
    int type_compatible;
    int reparte;

    int type_weight_rule;
    
  int pos_classe;
  Vector<Interval> test_exh;
  String fichero_dis;
  String file_columns;
  
	
    public AlgGenetic(int npoblation,  float muta,float cruce,Vector<partition> parte, 
    		int nclasses,fuzzy[][] x,Vector<Vector<Float>> y, int niterations,
    	 int reemplazo, String nombre, int al, String fichero, String entre,Vector<Float> values,
    	 int ninstances,int des, int tipo_comp, int repar,int asign_we_rule,
    	 String dist, String columns) throws IOException
    { 
    	
    	fichero_dis=dist;
    	file_columns=columns;
    	test_exh=new Vector<Interval>();
    	type_weight_rule=asign_we_rule;
    	
    	type_compatible=tipo_comp;
    	reparte=repar;
    	
    	X=x;
    	desconocidos=des;
    	fichero_test=fichero;
    	fichero_entre=entre;
    	Y=y;
    	 fs1= new FileWriter(nombre);
    	replace=reemplazo;
    	PROBCRoss= cruce;
		PROBMUTA= muta;	
		classes=nclasses;
		alpha=al;
		fitness= new Vector<Vector<Float>>(npoblation);
		compatible= new Vector<Boolean> (npoblation);
		for (int i=0;i<npoblation;i++) 
		{
			Vector<Float> inicializa= new Vector<Float>();
			inicializa.add((float)0.0);
    		fitness.add(i,inicializa);
    		
    		compatible.add(i,false);
		}
		poblation= new Vector<IndMichigan>(npoblation);
		partitions= new Vector<partition>(parte.size());
		fitness_total= new Vector<Float>();
		total= new Vector<Float>();
		partitions=parte;
		values_classes= new Vector<Float>(nclasses);
		costs= new Vector<Vector<Float>>(nclasses);
		TAMPO= npoblation;
		iterations= niterations;
		values_classes=values;
		
		instances=ninstances;
	    
	     
		
		Vector<Float> frequent_classes= new Vector<Float>();
		
		for(int v=0;v<values_classes.size();v++)
		{
			float value=0;
			for(int i=0;i<Y.size();i++)
			{
				for(int j=0;j<Y.get(i).size();j++)
				{
					
					if((float)Y.get(i).get(j)==(float)values_classes.get(v))
					{
				
						value++;
						
					}
				}
			
			}
			
			frequent_classes.add(v, value);
		}
		
		
		int position=0;
		float cantidad=-1;
		for (int i=0;i<frequent_classes.size();i++) 
		{
		
			if(cantidad<frequent_classes.get(i))
			{
				position=i;
				cantidad=frequent_classes.get(i);
			}
		}
		
		
		pos_classe= position;

		
		for (int i=0;i<TAMPO;i++) 
		{
		
			IndMichigan ind = new IndMichigan(x,y,partitions, classes,0,alpha,values_classes,costs,i, type_weight_rule); 
			poblation.add(i, ind);
		
		}
		
	
		
		evalua_poblation();
	
		
	
		show_fitness_total(0,0);
	
		 evolucion();
    }
    
    public void evalua_poblation() throws IOException
    {
    	
    	for (int i=0;i<TAMPO;i++) 
    	{
    		Vector<Float> initialize= new Vector<Float>();
			initialize.add((float)0.0);
    		fitness.set(i,initialize);
    		
    		compatible.set(i,false);
    	}
    	
    	
    		fitness_total.clear();
    	
    		
    	for (int i=0;i<X.length;i++) 
		{
    		
    		
    		Vector<Interval> compatibility = new Vector<Interval>(fitness.size());
    		for (int j=0;j<compatibility.size();j++) 
    		{
        		compatibility.add(j,new Interval(-1,-1)); 
    		}
    		
    		
    		
    		for(int j=0; j<TAMPO; j++)
    		{
    			
    			compatibility.add(j, poblation.get(j).getregla().match_alpha(X[i],partitions, alpha));
    			compatibility.set(j,compatibility.get(j).multiplicar(poblation.get(j).getregla().getpeso()));
    		}
    		    		    	
    		
    		Vector<Integer> eliminate=  new Vector<Integer>();
    		eliminate.clear();
    		if(type_compatible==1)
    		{
    			for(int j=0;j<compatibility.size();j++)
    			{
    			
    				boolean no_eliminado=true;
    				for(int k=0;k<compatibility.size();k++)
    				{
    					no_eliminado=true;
    					
    			
    					for(int e=0; e<eliminate.size(); e++)
    					{
    						if(eliminate.get(e)==k)
    						{
    							no_eliminado=false;
    			
    							break;
    							
    						}
    						
    			 	 	}
    					if(j!=k && no_eliminado==true)
    					{
    						if(compatibility.get(j).getmax()<compatibility.get(k).getmin() || compatibility.get(j).getmax()<=0)
    						{
    					  
    		 				 eliminate.addElement(j);
    		 				 no_eliminado=false;
    					  
    		 				 break;
    						}
    					}
    		 	 	}
    				
    				if(compatibility.get(j).getmax()<=0 && no_eliminado==true)
				 	{
    		 		 eliminate.addElement(j);
				  
				     
				 	}
    				
    				
    			}

    			
    		}
    		else if(type_compatible==2)
    		{
    			 
    			for(int j=0;j<compatibility.size();j++)
    			{
    			
    				boolean no_eliminado=true;
    				for(int k=0;k<compatibility.size();k++)
    				{
    					no_eliminado=true;
    					
    					for(int e=0; e<eliminate.size(); e++)
    					{
    						if(eliminate.get(e)==k)
    						{
    							no_eliminado=false;
    							break;
    						}
    					   
    			 	 	}
    				
    					if(j!=k && no_eliminado==true)
    					{
    						if(Dominance.uniform_compatibility(compatibility.get(j), compatibility.get(k))==1 || compatibility.get(j).getmax()<=0)
    						{
    					
    		 				 eliminate.addElement(j);
    		 				 no_eliminado=false;
    		 			
    		 				 break;
    						}
    					}
    					
    		 	 	}
    				if(compatibility.get(j).getmax()<=0 && no_eliminado==true)
				 	{
    		 		 eliminate.addElement(j);
				 	}
    			}
    			
    		}
    	
    		
    		
    		
    			Vector<Integer> ganadoras=new Vector<Integer>();
    			ganadoras.clear();
    		
    			int ganadora=-1;
    		
    	
    		Vector<Float> point=new Vector<Float>();
    		float maxima=-1;
    		
    		Vector<Float> points = new Vector<Float>();
    		Vector<Float> points_coste = new Vector<Float>();
    		
    		
    		
    		for(int r=0; r<compatibility.size(); r++)
    		{
    			boolean existe=false;
    			
    			for(int j=0;j<eliminate.size();j++)
    			{ 
    				if(r==eliminate.get(j).intValue())
    				{
    					existe=true;
    					break;
    				}
    			}
    			if(existe==false)// es una regla ganadora
    			{
    						point=puntuacion(r, Y.get(i));
    				
    				
    						for(int p=0;p<point.size();p++)
    						{
    							if(points.contains(point.get(p))==false)
    								points.addElement(point.get(p));
    						}
    						
    					//}
        			
    					if(reparte==0)
    					{
    						if(compatibility.get(r).getmax()>=maxima)
    						{
    							ganadora=r;
    							maxima=compatibility.get(r).getmax();
    						
    						}
    					}
    					else
    						ganadoras.addElement(r);
    					
    				//}
    					
    			}
    			
    		}
    		
		    
    		
    		if((ganadoras.size()!=0 && reparte==1) ||( ganadora!=-1 && reparte==0))
    		{
    			if(reparte==1)
    				{
    					
    					Vector<Float> reparto_points = new Vector<Float>();
    					for(int c=0;c<points.size();c++)
    					{
    					
    						if(points.get(c)!=0)
    							reparto_points.addElement((Float)points.get(c)/ganadoras.size());
    						else
    							reparto_points.addElement(points.get(c));
    					}
    					for(int g=0; g<ganadoras.size();g++)
    					{

    						fitness.set(ganadoras.get(g),sumatorio(fitness.get(ganadoras.get(g)),reparto_points)); 
    						compatible.set(ganadoras.get(g), true);
    					
    					}
    				}
    				else
    				{
    					fitness.set(ganadora,sumatorio(fitness.get(ganadora),points)); //sin coste es % error (0 y 1)
    					compatible.set(ganadora, true);
    				}
    			
    				
    			
    		}
    		else 
    		{
    			
    			
    			point=puntuacion(-1, Y.get(i));
				for(int p=0;p<point.size();p++)
				{
					
					if(points.contains(point.get(p))==false)
						points.addElement(point.get(p));
				}
    			
    			
    			
    		}
    			
    		calculo_fitness_total(points);
    		
		}
    	
    	
    	
    }
   public Vector<Float> sumatorio(Vector<Float> values_r,Vector<Float> point) throws IOException
    {
    	Vector<Float> insert= new Vector<Float>();
    	insert.clear();
    	
		
		
    	for(int i=0; i<values_r.size();i++)
    		{
    			for(int j=0;j<point.size();j++)
    			{
    				float nuevo=values_r.get(i)+point.get(j);
    				if(insert.contains(nuevo)==false)
    					insert.addElement(nuevo);
    			}
    		}
    		
    	  values_r.clear();
    		for(int i=0; i<insert.size();i++)
    		{
    		
    			if(values_r.contains(insert.get(i))==false)
    				values_r.addElement(insert.get(i));
    		}
    	
    	
    		
    		float maximo=-1;
    		float minimo=1;
    		for (int i=0;i<values_r.size();i++)
        	{
        		
        		if(i==0)
        		{
        			maximo=values_r.get(i);
        			minimo=values_r.get(i);
        			
        		}
        			
        		if(minimo>values_r.get(i))
        			minimo= values_r.get(i);
        		
        		 if(maximo<values_r.get(i))
        			maximo=values_r.get(i);
        	}

    		values_r.clear();
    		if(maximo!=minimo)
    		{
    			values_r.addElement(minimo);
    			values_r.addElement(maximo);
    		}
    		else
    			values_r.addElement(maximo);
    			
    	return values_r;
    }
    
    public void calculo_fitness_total(Vector<Float> points) throws IOException
    {
    	
    	Vector<Float> insert= new Vector<Float>();
    	if(fitness_total.size()!=0)
    	{
    		for(int i=0; i<fitness_total.size();i++)
    		{
    			for(int j=0;j<points.size();j++)
    			{
    				float nuevo=fitness_total.get(i)+points.get(j);
    			
    				if(insert.contains(nuevo)==false)
    					insert.addElement(nuevo);
    			}
    		}
    		
    		
    		fitness_total.clear();
    		for(int i=0; i<insert.size();i++)
    		{
    			if(fitness_total.contains(insert.get(i))==false)
    				fitness_total.addElement(insert.get(i));
    		}
    	}
    	
    	if(fitness_total.size()==0)
    	{
    		
    		for(int i=0;i<points.size();i++)
    			fitness_total.addElement(points.get(i));
    	}
   
 
    }
 
    public int salida_r(int regla)
    {
    	int consequent=0;
    		
    	Float [] cons=poblation.get(regla).getregla().getconsequent();
		
    	for (int c=0; c<cons.length; c++)
    	{
    		if(cons[c]!=0)
    		{
    			consequent=c;	
    			break;
    		}
    	}
    	
    	return consequent;
    }
    public Vector<Float> puntuacion(int regla, Vector<Float> salida)
    {
    	int consequent=0;
    	
    	Vector<Float> point=new Vector<Float>();
    	
		
    	if(regla!=-1)
    	{
    		Float [] cons=poblation.get(regla).getregla().getconsequent();
		
    		for (int c=0; c<cons.length; c++)
    		{
    			if(cons[c]!=0)
    			{
    				consequent=c;	
    				break;
    			}
    		}
    	}
    	else
    	{
    		consequent=pos_classe; 
    	}
    	
    	 
    	for(int j=0;j<salida.size();j++)
   		{
   			int ant_salida=-1;
   			for(int v_classes=0; v_classes<values_classes.size(); v_classes++)
   			{
   			
   				if(values_classes.get(v_classes).compareTo(salida.get(j))==0)
			
   				{
   			
   					ant_salida=v_classes;
   					break;
   				}
   			}
   			if (ant_salida!=-1)   		  	
   			{	   		  		
   				if(ant_salida==consequent)
   					point.add((float)1);
   				else
   					point.add((float)0);
   			}
		
		
   		}
   		
     return point;
  
    }
    public void show_fitness() throws IOException
    {
    	for (int i=0;i<fitness.size();i++)
    	{
    		
    		fs1.write("Fitness de la regla "+i+" es " + fitness.get(i)+"\n");//variables
    	}
    	
    	
    }
    public void show_fitness_total(int ultimo, int c) throws IOException
    {
    	
    	float minimo=2;
    	float maximo=-1;
    	
    	for (int i=0;i<fitness_total.size();i++)
    	{
    		
    		 if(minimo>(1-fitness_total.get(i)/X.length))
    			minimo= (1-fitness_total.get(i)/X.length);
    		
    		if(maximo<(1-fitness_total.get(i)/X.length))
    		{
    			
    			maximo=(1-fitness_total.get(i)/X.length);
    		}
    	}

    	
    	if(ultimo==0)
    		fs1.write("\n[" + minimo+", "+maximo+"]");
    	
    	else
    	{
    		fs1.write("\n" + minimo);
    		fs1.write("\n" + maximo);
    	}
    		
    
    }
  
	public void evolucion() throws IOException
	{
		   
		 for (int iter=0;iter<iterations;iter++) 
		 {

			
			 Vector<IndMichigan> pobl_int= new Vector<IndMichigan>();
			 for (int r=0;r<replace/2;r++) 
			 {
				 int p1, p2, padre1, padre2;
					
				
				 p1=(int) (0+(float)(Math.random()*poblation.size()));
				 p2=(int)(0+(float)(Math.random()*poblation.size()));
				 
		

				
				 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)
					 padre1=p1; 
				 else 
					 padre1=p2;
					
					
				 do
				{
				 p1=(int) (0+(float)(Math.random()*poblation.size()));
				 p2=(int)(0+(float)(Math.random()*poblation.size()));
				 	 
				 
				 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)
					 padre2=p1; 
				 else 
					 padre2=p2;
				 
				 
				}while(padre2==padre1);
							 
				 Vector<Integer> hijo1= new Vector<Integer>();
				 Vector<Integer> hijo2= new Vector<Integer>();
				 for(int i=0;i<poblation.get(padre1).getregla().getantecedent().length;i++)
				 {
					 hijo1.addElement(poblation.get(padre1).getregla().getantecedent(i));
					 hijo2.addElement(poblation.get(padre2).getregla().getantecedent(i));
				 }
				 
				
			
				
				 float prob= 0+(float)(Math.random() *1);
				
				if (prob<PROBCRoss) 
				{ 
					for (int i=0;i<hijo1.size();i++) 
					{
						prob= 0+(float)(Math.random() *1);
						if (prob<0.5) 
						{
						
							int aux=hijo1.get(i);
							hijo1.set(i,hijo2.get(i));
							hijo2.set(i, aux);
						
						}
					}
				}
				
				
		
				for (int i=0;i<hijo1.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{
				
						int value=(int) (0+(float)(Math.random()*(partitions.get(i).size())));
				
						hijo1.set(i, value);//+NOIMPORTA);
					}
				}
				
				for (int i=0;i<hijo2.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{

						int value=(int) (0+(float)(Math.random()*(partitions.get(i).size()))); 

						hijo2.set(i, value);//+NOIMPORTA);
					}
				}
				
				
			
				pobl_int.addElement(new IndMichigan(hijo1,X,Y,partitions, classes,0,alpha,values_classes,costs, type_weight_rule));
				pobl_int.addElement(new IndMichigan(hijo2,X,Y,partitions, classes,0,alpha,values_classes,costs,type_weight_rule));
					
				
							
				
				
				
			 }//replace
			   
			
			Vector<Integer>reglas_ordenadas=Dominance.order(fitness); 
			for (int i=0;i<pobl_int.size();i++) 
			{
				
				poblation.set(reglas_ordenadas.get(i),pobl_int.get(i));
			
			}
			
			
			//Mutation of the weight rule
			for (int r=0;r<replace;r++) 
			{
				int i=(0+(int)(Math.random() *(poblation.size()-1)));
				if (0+(float)(Math.random() *1)<0.4)  
				{
					
					float peso_actual=poblation.get(i).getregla().getpeso();
					
					Interval actual=fitness_regla(i);
					
					
					float peso=0+(float)(Math.random() *1);
					poblation.get(i).getregla().setpeso(peso);
				    
					Interval mutado=fitness_regla(i);
					
					
					
					
					if(Dominance.uniform_compatibility(actual, mutado)==1 && 0+(float)(Math.random() *1)>0.96)
						poblation.get(i).getregla().setpeso(peso_actual);
					
					if(Dominance.uniform_compatibility(mutado, actual)==1 && 0+(float)(Math.random() *1)>0.2)
						poblation.get(i).getregla().setpeso(peso_actual);
						
				
				}
			}
			
			
			
			evalua_poblation();
			
			System.out.print("."+iter);
			
			show_fitness_total(0,0);

			
			   
			   
			  
 
		 } //for iterations
	
		 

	 	show_reglas(fs1);	
	 	show_fitness();
	 	
	 	fs1.write("IMPRECISO");
		show_fitness_total(1,0);
		
		
		
		
		test();
		fs1.close();
				
		
	}
	public Interval fitness_regla(int cal_regla) throws IOException
    {	
		Interval fit= new Interval(0,0);       
    		
    	for (int i=0;i<X.length;i++) 
		{
    		
    		
    	
    		Vector<Interval> compatibility = new Vector<Interval>(fitness.size());
    		for (int j=0;j<compatibility.size();j++) 
    		{
        		compatibility.add(j,new Interval(-1,-1)); 
    		}
    		
    	
    		for(int j=0; j<TAMPO; j++)
    		{
    			
    			compatibility.add(j, poblation.get(j).getregla().match_alpha(X[i],partitions, alpha));
    			compatibility.set(j,compatibility.get(j).multiplicar(poblation.get(j).getregla().getpeso()));
    		}
    		    		    	
    		
    		Vector<Integer> eliminate=  new Vector<Integer>();
    		eliminate.clear();
    		
    		
    			for(int j=0;j<compatibility.size();j++)
    			{
    			
    				boolean no_eliminado=true;
    				for(int k=0;k<compatibility.size();k++)
    				{
    					no_eliminado=true;
    					
    					for(int e=0; e<eliminate.size(); e++)
    					{
    						if(eliminate.get(e)==k)
    						{
    							no_eliminado=false; 
    							break;
    						}
    					   
    			 	 	}

    					if(j!=k && no_eliminado==true)
    					{
    						if(Dominance.uniform_compatibility(compatibility.get(j), compatibility.get(k))==1 || compatibility.get(j).getmax()<=0)
    						{

    		 				 eliminate.addElement(j);
    		 				 no_eliminado=false;

    		 				 break;
    						}
    					}
    					
    		 	 	}
    				if(compatibility.get(j).getmax()<=0 && no_eliminado==true)
				 	{
    		 		 eliminate.addElement(j);
				 	}
    			}
    			
    			Vector<Integer> ganadoras=new Vector<Integer>();
    			ganadoras.clear();
    		
    		
    		
    		
    		Vector<Float> point=new Vector<Float>();
    		
    		
    		for(int r=0; r<compatibility.size(); r++)
    		{
    			boolean existe=false;
    			
    			for(int j=0;j<eliminate.size();j++)
    			{ 
    				if(r==eliminate.get(j).intValue())
    				{
    					existe=true;
    					break;
    				}
    			}
    			if(existe==false )
    			{
    				
    				if(r==cal_regla)
    				{
    					
    					point=puntuacion(r, Y.get(i));
    				}
    									
    			}
    			
    				
    			
    		}
    		
		   
    		
    			
    		fit=calculo_fitness_regla(point,fit);
    		
		}
		return fit;
    		
    }
	 public Interval calculo_fitness_regla(Vector<Float> points,Interval fit) throws IOException
	    {
	    	
	    	
	    	Vector<Float> insert= new Vector<Float>();
	    	for(int j=0;j<points.size();j++)
	    	{
	    		float nuevo=fit.getmin()+points.get(j);
	    		if(insert.contains(nuevo)==false)
	    				insert.addElement(nuevo);
	    		
	    		nuevo=fit.getmax()+points.get(j);
	    		if(insert.contains(nuevo)==false)
	    				insert.addElement(nuevo);
	    	}
	    	
	    		
	    		
	    	    float min=0,max=0;
	    		for(int i=0; i<insert.size();i++)
	    		{
	    			if(i==0)
	    			{
	    				min=insert.get(i);
	    				max=insert.get(i);
	    			}
	    			
	    			if(min>insert.get(i))
	    				min=insert.get(i);
	    			if(max<insert.get(i))
	    				max=insert.get(i);
	    		}
	    	
	    		fit.setmax(max);
	    		fit.setmin(min);
	    	
	   return fit;
	    	
	}
	void show_reglas(FileWriter fs1) throws IOException
	{
		for (int i=0;i<poblation.size();i++)
		{
			if(compatible.get(i)==true)
			{
				fs1.write("\nRegla "+i);//variables
				poblation.get(i).getregla().show(fs1);
				
			}
			
		}
		
	}
	void test() throws IOException
	{
	
        
       
		dis= new FileWriter(fichero_dis);
		 column= new FileWriter(file_columns);
        BufferedReader entrada;// = new BufferedReader(new FileReader(fichero_test));
       
        	entrada = new BufferedReader(new FileReader(fichero_test));
        
        Character caracter;
        
       
      
        int dimx;        
        int ncol;        
        int ninstances;    
        
        
       
        
       

        String numero= "";
        for(int c=0;c<1000;c++)
        {
        	System.out.println("\n Test: "+c);
        	column.write(c+"\n");
        	
        	int contador=Integer.parseInt(entrada.readLine());
        	dimx=Integer.parseInt(entrada.readLine());        
            ncol= dimx+1;        
             
            ninstances=Integer.parseInt(entrada.readLine());        
           
            int nclasses=Integer.parseInt(entrada.readLine());   
            
            float X[][] = new float[ninstances][dimx];
            Vector<Vector<Float>> C= new Vector<Vector<Float>>(ninstances);

           
           
            for(int i=0; i<ninstances; i++)
            {
    		
            	for(int j=0; j<ncol-1; j++)
            	{
        		
            		caracter = (char)entrada.read();
            	
        				while(caracter!=' ' && caracter!='\n')
        				{
        					numero= numero + caracter;
        					caracter = (char)entrada.read();
        				}
        			
        				if(caracter==' ' )
        				{
        					if(numero.compareTo("-")==0)
        					{
        					
        						X[i][j]= -1;
        					}
        					else
        					{
        					
        						X[i][j]=Float.parseFloat(numero);
        					}
        					numero="";
        				
        				
        				}
        			
            	}
        		
            
            	caracter = (char)entrada.read();
        
            	
            	Vector <Float> salidas_imp= new Vector<Float>();
            	while(caracter!='}')
            	{
            		caracter = (char)entrada.read();
            		while(caracter!=',' && caracter!='}')
            		{
            			numero= numero + caracter;
            			caracter = (char)entrada.read();
            		}
        		
            		salidas_imp.addElement(Float.parseFloat(numero));
            		numero="";
    			
            		if(caracter!='}')
            			caracter = (char)entrada.read();
    			
            	}
            	C.add(i,salidas_imp);
            	caracter = (char)entrada.read();
            	numero="";

        	
            }
        
        
        
        
            X= missing.values_missing_test(X, ninstances, dimx,desconocidos);
        
       
            
            evalua_poblation_test(dimx,X,C);
		
		 
            show_fitness_total_test(X,c);
		
        }
        
		entrada.close();
		 dis.close();
		 column.close();
			
			float minimo=0;
			float maximo=0;
			for(int t=0;t<test_exh.size();t++)
			{
				minimo=minimo+test_exh.get(t).getmin();
				maximo=maximo+test_exh.get(t).getmax();
			}
			
			fs1.write("\nTest\n"+(minimo/test_exh.size())+"\n"+(maximo/test_exh.size())+"\n");
		
		 
		 
	}
	
	

	 public void show_fitness_total_test(float X[][],int c) throws IOException
	    {
	    	
	    	float minimo=2;
	    	float maximo=-1;
	    	
	    	for (int i=0;i<fitness_total.size();i++)
	    	{
	    		
	    		 if(minimo>(1-fitness_total.get(i)/X.length))
	    			minimo= (1-fitness_total.get(i)/X.length);
	    		
	    	
	    		if(maximo<(1-fitness_total.get(i)/X.length))
	    		{
	    	
	    			maximo=(1-fitness_total.get(i)/X.length);
	    		}
	    	}

	    	
	    	 Interval fit= new Interval(minimo,maximo);
	    	 test_exh.addElement(fit);
	    	 dis.write(c+"\n"+minimo+"\n");
	    	 dis.write(maximo+"\n");
	    	
	    }
	
	 public void create_file_columns(int regla, Vector<Float> salida) throws IOException
	 {
		 
		 
		 if(salida.size()==1)
			 column.write("{"+salida.get(0)+"},");
		 else
		 {
			 for(int i=0;i<salida.size();i++)
			 {
				 if(i==0)
					 column.write("{"+salida.get(0)+",");
				 else if(i==salida.size()-1)
					 column.write(salida.get(i)+"},");
				 else
					 column.write(salida.get(i)+",");
			 }
		 }
		 
		 if(regla!=-1)
		 {
		  Float [] cons=poblation.get(regla).getregla().getconsequent();
			
	 		for (int c=0; c<cons.length; c++)
	 		{
	 			if(cons[c]!=0)
	 			{
	 				column.write(values_classes.get(c)+"\n");	
	 				break;
	 			}
	 		}
		 }
		 else
		 {
			 column.write(values_classes.get(pos_classe)+",");
			 column.write("-1"+"\n");	
		 }
			 
	 }
	 public void evalua_poblation_test(int dimx,float X[][],Vector<Vector<Float>> C) throws IOException
	    {
	    	
		 fitness_total.clear();
			
		 for (int i=0;i<X.length;i++) 
		 {
	    	
			
			 Vector<Float> points = new Vector<Float>();
			 
			 float point_point[]= new float[dimx];
	    	    
	    	 for(int p=0;p<dimx;p++)
	    	 {
	    		 point_point[p]=X[i][p];
	    	 }
	    	    	
	    	 float Z[][] = new float[1][dimx];
	    	 Z[0]=point_point;		
	    		
	    	
	    	 Vector<Float> compatibility = new Vector<Float>(fitness.size());
				
	    	 int regla=-1;   	 
	    	 float comp_regla=0;  				        		 
	    	 for(int j=0; j<TAMPO; j++)
			 {

		
				 if(compatible.get(j)==true)
				 {
					 compatibility.add(j, poblation.get(j).getregla().match(Z[0],partitions));
		
					 float compa = compatibility.get(j)*poblation.get(j).getregla().getpeso();  
					 if(compa>comp_regla)
					 {	  
						 comp_regla=compa;
						 regla=j;
					 }
				 }
				 
				 else
					 compatibility.add(j,(float)0.0); 
				
				 
				 
			 }
    		    	   
	        		
	    	 Vector<Float> point=new Vector<Float>();
	    	 create_file_columns(regla, C.get(i));
			 if(regla!=-1)
			 {

				 point=puntuacion(regla, C.get(i));
				 for(int pun=0;pun<point.size();pun++)
				 {
			
					 if(points.contains(point.get(pun))==false)
						 points.addElement(point.get(pun));
				 }
				 	 
			 }
						
			 else 
	    		{
	    			
	    			point=puntuacion(-1, Y.get(i));
					for(int p=0;p<point.size();p++)
					{
						
						if(points.contains(point.get(p))==false)
							points.addElement(point.get(p));
					}
	    			
	    			
	    			
	    		}

	    	    	calculo_fitness_total(points);
	    	   
			}	 
	    	
	    }
	 
	
	 
	 
}


