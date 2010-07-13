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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

/**
 * 
 * File: AlgGenetic.java
 * 
 * This genetic algorithm obtains fuzzy rule from 
 * imprecise or low quality using a cost-sensitive
 * learning based in a cost matrix defined by
 * linguistic terms or interval. Also we can use
 * the zero-one loss.
 * 
 * To obtain the fuzzy fitness function we also have
 * to take into account the cost of the instances
 * defined by a fuzzy-value
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/06/2010 
 * @version 1.0 
 */

public class AlgGenetic {
	
	Vector<IndMichigan>poblation;
    Vector<Vector<Float>> fitness;
    Vector<Float> fitness_total;
    Vector<fuzzy> fitness_total_ltf;
    Vector<Boolean> compatible;
    Vector<Float> total;
    float PROBMUTA;
    float PROBCROSS;
    int TAMPO;
    int iteration;
    fuzzy[][] X;
    Vector<Vector<Float>> Y;
    int classes;
    Vector<fuzzyPartition> partitions;
    int replace;
    int COST;
    int alfa;
    FileWriter fs1;
    String fichero_test;
    String fichero_entre;
    FileWriter dis;
    FileWriter column;
    Vector<Float> values_classes;
    Vector<Vector<fuzzy>> costs;
    int crisp;
    
  
    int type_compatible;
    int distribute;//Distribute the the points between the rules if the value is 1 (else only point one rule, the winner)
    int type_weight_rule;//with 0 the rule has not weight
    String label_matrix; //is linguistic term or interval
    
    int pos_classe;//if one example is not compatible with no rule
  Vector<Interval> test_exh;
  Vector<Vector<fuzzy>> test_exh_ltf;
  String fichero_dis;
  String file_columns;
  Vector<fuzzy> P;
  
  Vector<Vector<Integer>> confusion_matrix;
 
    public AlgGenetic(int npoblation,  float muta,float cross,Vector<fuzzyPartition> parte, 
    		int nclases,fuzzy[][] x,Vector<Vector<Float>> y, Vector<fuzzy> cost_example,int niteration,
    	 int re, int coste, String nombre, int al, String fichero, String entre,Vector<Float> values,
    	 Vector<Vector<fuzzy>> costes,int nejemplos, int tipo_comp, int repar,int asign_weight_rule,
    	 String matrix, int es_crisp, String dist,String columns) throws IOException
    { 
    	
    	//Initialize the variable
    	P= new Vector<fuzzy>();
    	P=cost_example; //cost of the instances
    	crisp=es_crisp; //if we want to work with the mean or central point from the lqd
    	label_matrix=matrix;
    	fichero_dis=dist;
    	file_columns=columns;
    	test_exh=new Vector<Interval>();
    	test_exh_ltf=new Vector<Vector<fuzzy>>();
    	type_weight_rule=asign_weight_rule;
    	
    	type_compatible=tipo_comp;
    	distribute=repar;
    	
    	X=x;
    	fichero_test=fichero;
    	fichero_entre=entre;
    	Y=y;
    	 fs1= new FileWriter(nombre);
    	COST=coste;
    	replace=re;
    	PROBCROSS= cross;
		PROBMUTA= muta;	
		classes=nclases;
		alfa=al;
		fitness= new Vector<Vector<Float>>(npoblation);//fitness for rule
		compatible= new Vector<Boolean> (npoblation);
		for (int i=0;i<npoblation;i++) 
		{
			Vector<Float> initialize= new Vector<Float>();
			initialize.add((float)0.0);
    		fitness.add(i,initialize);
    		
    		compatible.add(i,false);
		}
		poblation= new Vector<IndMichigan>(npoblation);
		partitions= new Vector<fuzzyPartition>(parte.size());
		fitness_total= new Vector<Float>();
		fitness_total_ltf= new Vector<fuzzy>();
		total= new Vector<Float>();
		partitions=parte;
		values_classes= new Vector<Float>(nclases);
		costs= new Vector<Vector<fuzzy>>(nclases);
		TAMPO= npoblation;
		iteration= niteration;
		values_classes=values;
		costs=costes;
	    
		confusion_matrix=new Vector<Vector<Integer>>();
		for(int v=0;v<values.size();v++)
		{
			Vector<Integer> contenido=new Vector<Integer>();
			Vector<fuzzy> contenidoborroso=new Vector<fuzzy>();
			for(int j=0;j<values.size();j++)
			{
				contenido.addElement(0);
				contenidoborroso.addElement(new fuzzy(0));
			}
			confusion_matrix.addElement(contenido);
				
		}
		
		
		int position=0;
		if(COST==0)
		{
			//Obtain the class more frequent because maybe one example can not be compatible
			//with any rule.
			Vector<Float> frequent_classes= new Vector<Float>();
		
			for(int v=0;v<values_classes.size();v++)
			{
				float valu=0;
				for(int i=0;i<Y.size();i++)
				{
					for(int j=0;j<Y.get(i).size();j++)
					{
					
						if((float)Y.get(i).get(j)==(float)values_classes.get(v))
						{
							valu++;
						}
					}
			
				}
				frequent_classes.add(v, valu);
			}
		
		
			
			float cantidad=-1;
			for (int i=0;i<frequent_classes.size();i++) 
			{
				if(cantidad<frequent_classes.get(i))
				{
					position=i;
					cantidad=frequent_classes.get(i);
				}
			}
		
		
			pos_classe= position;//quiero la posicion de la clase mas frecuente
		}
		else
		{
			//We obtain the class with less cost. This cost is obtained from the cost matrix
			Interval actual = new Interval(0,0);
			fuzzy actual1= new fuzzy(0);
		  
			 
			 for(int p1=0;p1<costs.get(0).size();p1++)
			  {
				  Interval sum = new Interval(0,0);
				  fuzzy sum1= new fuzzy(0);
			 
				  for(int p0=0;p0<costs.size();p0++)
				  {
					  if(p0!=p1 && label_matrix.compareTo("I")==0 )
					  {
						  Interval valu = new Interval((1-costs.get(p0).get(p1).geta()),(1-costs.get(p0).get(p1).getd()));
						  sum.setmin(sum.getmin()+valu.getmin());
						  sum.setmax(sum.getmax()+valu.getmax());
					  }
					  if(p0!=p1 && label_matrix.compareTo("I")!=0 )
					  {
						  fuzzy valu = new fuzzy();
						  fuzzy unit= new fuzzy(1);
						  valu =fuzzy.resta(unit, costs.get(p0).get(p1));
					  
						  sum1= fuzzy.suma(sum1,valu);
					  }
				  }
				  if(label_matrix.compareTo("I")==0 )
				  {
					  if(Dominance.uniform_compatibility(actual, sum)==1)//valor es mejor (mas valor)
					  {
						  position=p1;
						  actual=sum;
					  }
				  }
				  else
				  {
					  if(Ranking.wang(actual1, sum1)==1)//valor es mejor (mas valor)
					  {
						  position=p1;
						  actual1=sum1;
					  }   
				  }	
			  
			  }
			
			 pos_classe= position;//quiero la posicion de la clase mas frecuente
		}

		System.out.println("The class selected by defect is "+position);
		//new BufferedReader(new InputStreamReader(System.in)).readLine();
		
		//Initialize the population
		for (int i=0;i<TAMPO;i++) 
		{
			IndMichigan ind = new IndMichigan(x,y,partitions, classes,COST,alfa,values_classes,costs,i, type_weight_rule,label_matrix,P,es_crisp); 
			poblation.add(i, ind);
		}
		
		
	
		evaluate_poblation();
		//show_fitness();//show the fitness of each rule
		
		//Show the total fitness, summation of all rules
		show_fitness_total(0);

		 evolution();
    }
    
    public void evaluate_poblation() throws IOException
    {
    	for (int i=0;i<TAMPO;i++) 
    	{
    		Vector<Float> initialize= new Vector<Float>();
			initialize.add((float)0.0);
    		fitness.set(i,initialize); //Initialize the fitness of each rule a zero
    		
    		compatible.set(i,false);
    	}
    	
    	
    	fitness_total.clear();
    	
    		
    	for (int i=0;i<X.length;i++)//x.length indicates the number of example in the training 
		{
    		
    		int rule=-1;
    		Vector<Interval> compatibility = new Vector<Interval>(fitness.size());//save the compatibility betwee the example and the rules
    		for (int j=0;j<compatibility.size();j++) 
    		{
        		compatibility.add(j,new Interval(-1,-1)); 
    		}
    		
    		
    		
    		//Get the best rule, for the current example, from compatibility to exist between them
    		float max_compa=0;
    		for(int j=0; j<TAMPO; j++)
    		{
    			compatibility.add(j, poblation.get(j).getregla().match_alfa(X[i],partitions, alfa));
    			if(crisp==1)
        		{
    				if(max_compa<compatibility.get(j).getmax())
        			{
        				max_compa=compatibility.get(j).getmax();
        				rule=j;
        			}
        		}
    		}
    		    		    	
    		
    		Vector<Integer> eliminate=  new Vector<Integer>();
    		eliminate.clear();
    		
    		if(crisp==0) //Input data are not crisp
    		{
    		if(type_compatible==1)//strict compatibility between rule
    		{
    			//Eliminate the worst rule, that is to say, the maximum of compatibility of one rule is less or
    			//equal than the minimum of compatibility of other rule
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
    						if(compatibility.get(j).getmax()<=compatibility.get(k).getmin() || compatibility.get(j).getmax()<=0)
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
    		}//if strict compatibility
    		
    		else if(type_compatible==2)//uniform dominance
    		{
    			//Eliminate rules that we know that are dominated 
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
    					eliminate.addElement(j);
				 	
    			}
    			
    		}//type 2
    		}//if unput data are not crisp
    		
    		
    		//Get the points of all possible rules winner from the consequent of the rule and
    		//the output of the example (that can be a set of values)
    		
    			
    		Vector<Integer> winner=new Vector<Integer>();//save the winner rules to distribute the points between them
    		winner.clear();
    		int win=-1;
    		
    		Vector<Float> point=new Vector<Float>();
    		float max=-1;
    		//Vector<Integer> salidas= new Vector<Integer>();
    		Vector<Float> points = new Vector<Float>();
    		Vector<Float> pointfitness = new Vector<Float>();
    		Vector<Float> points_cost = new Vector<Float>();
    		Vector<Float> points_costti= new Vector<Float>();
    		
    		int no_crisp=1;
    		for(int r=0; r<compatibility.size() && no_crisp==1 ; r++)
    		{
    			boolean exist=false;
    			if(rule!=-1 || crisp==1)
    			{
    				//Crisp data and only one rule is the winner
    				no_crisp=0;
    				r=rule;
    				if(r!=-1)
    					exist=false;
    				else
    					exist=true;
    			}
    			
    			for(int j=0;j<eliminate.size() && no_crisp==1;j++)
    			{ 
    				if(r==eliminate.get(j).intValue())
    				{
    					exist=true;
    					break;
    				}
    			}
    			if(exist==false)// is a winner rule
    			{    				  
    				if(COST==1)//with cost matrix, but the fitness train is zero-one loss 
    				{
    					COST=0;
    					point=puntuation(r, Y.get(i),0);
    					for(int p=0;p<point.size();p++)
    					{
    						if(pointfitness.contains(point.get(p))==false)
    						{
    							pointfitness.addElement(point.get(p));
    						}
    					}
    					
    					COST=1;
    					point=puntuation(r, Y.get(i),0);
    					for(int p=0;p<point.size();p++)
    					{
    						if(points_costti.contains(point.get(p))==false)
   							{
   								double cost=Ranking.value_x(fuzzy.multinumero(point.get(p), P.get(i)));
   								points_cost.addElement((float)cost);
   								points_costti.addElement(point.get(p));
    						}
    					}
    						
    				}
    				else//no cost matrix
    				{
    					point=puntuation(r, Y.get(i),0);
    					for(int p=0;p<point.size();p++)
    					{
    						if(pointfitness.contains(point.get(p))==false)
    						{
    							double cost=Ranking.value_x(fuzzy.multinumero(point.get(p), P.get(i)));
    							points.addElement((float)cost);
    							pointfitness.addElement(point.get(p));
   							}
   						}  						
    				}
        			
    					if(distribute==0)//only one rule is pointed
    					{
    						if(compatibility.get(r).getmax()>=max)
    						{
    							win=r;
    							max=compatibility.get(r).getmax();
    						}
    						
    					}
    					else
    						winner.addElement(r);
    					
    			}//if exist false
    			
    		}
    		
    		
    		
    		if((winner.size()!=0 && distribute==1) ||(win!=-1 && distribute==0))
    		{
    			if(COST==0)
    			{
    				if(distribute==1)
    				{
        				//Distribute between winner rules
    					Vector<Float> reparto_puntos = new Vector<Float>();
    					for(int c=0;c<points.size();c++)
    					{
    						if(points.get(c)!=0)
    							reparto_puntos.addElement((Float)points.get(c)/winner.size());
    						else
    							reparto_puntos.addElement(points.get(c));
    					}
    					for(int g=0; g<winner.size();g++)
    					{
    						fitness.set(winner.get(g),summation(fitness.get(winner.get(g)),reparto_puntos)); //con costes es % coste las reglas
    						compatible.set(winner.get(g), true);
    					}
    				}
    				else
    				{
    					fitness.set(win,summation(fitness.get(win),points));
    					compatible.set(win, true);
    				}
    			}
    			else //with cost matrix
    			{
    				if(distribute==1)
    				{
    					//Distribute between winner rules
    					Vector<Float> reparto_puntos = new Vector<Float>();
    					for(int c=0;c<points_cost.size();c++)
    					{
    						if(points_cost.get(c)!=0)
    							reparto_puntos.addElement((Float)points_cost.get(c)/winner.size());
    						else
    							reparto_puntos.addElement(points_cost.get(c));
    					}
    					for(int g=0; g<winner.size();g++)
    					{
    						fitness.set(winner.get(g),summation(fitness.get(winner.get(g)),reparto_puntos)); //con costes es % coste las reglas
    						compatible.set(winner.get(g), true);
    					}
    				}
    				else
    				{
    					fitness.set(win,summation(fitness.get(win),points_cost)); 
    					compatible.set(win, true);
    				}
    				
    			}
    		}
    		else //There are not compatible rules
    		{
    			int change=0;
    			if(COST==1)
    			{
    				COST=0;
    				change=1;
    			}
    			point=puntuation(-1, Y.get(i),0);
   				for(int p=0;p<point.size();p++)
   				{
   					if(pointfitness.contains(point.get(p))==false)
   						pointfitness.addElement(point.get(p));    				
   				}
    			if(change==1)
    				COST=1;    			
    		}
    			
    		calculation_fitness_total(pointfitness);//The total fitness is expressed by zerp-one loss (also if the training
    		//is with cost matrix or not. 
    		//show_fitness_total(0);
    		//new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
    	
    	
    	
    }
   public Vector<Float> summation(Vector<Float> values_r,Vector<Float> punto) throws IOException
    {
   		Vector<Float> insert= new Vector<Float>();
   		insert.clear();
	 
   		for(int i=0; i<values_r.size();i++)
   		{
   			for(int j=0;j<punto.size();j++)
   			{
   				float new_value=values_r.get(i)+punto.get(j);
   				if(insert.contains(new_value)==false)
   					insert.addElement(new_value);
   			}
   		}


   		//Insert the new values in the total fitness
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
    
    public void calculation_fitness_total(Vector<Float> points) throws IOException
    {

    	Vector<Float> insert= new Vector<Float>();
    	if(fitness_total.size()!=0)
    	{
    		for(int i=0; i<fitness_total.size();i++)
    		{
    			for(int j=0;j<points.size();j++)
    			{
    				float new_value=fitness_total.get(i)+points.get(j);
    				if(insert.contains(new_value)==false)
    					insert.addElement(new_value);
    			}
    		}
    		
    		//Insert the new values in the total fitness
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
 
 
    public Vector<Float> puntuation(int rule, Vector<Float> output, int test) throws IOException
    {
    	int consequent=0;
    	Vector<Float> punto=new Vector<Float>();
    	
		if(rule!=-1)
    	{
    		Float [] cons=poblation.get(rule).getregla().getconsequent();
    		for (int c=0; c<cons.length; c++)
    		{
    			if(cons[c]!=0)
    			{
    				consequent=c;	
    				break;
    			}
    		}
    	}
    	else //there are not compatible rule
    	{
    		consequent=pos_classe; 
    	}
    	
		//we obtain the position that would have the output of the instance in the consequent (values class 0,1 2,4)
		if(COST==0)
		{
			for(int j=0;j<output.size();j++)
			{
				int ant_salida=-1;
				for(int v_clases=0; v_clases<values_classes.size(); v_clases++)
				{
					if(values_classes.get(v_clases).compareTo(output.get(j))==0)
					{
						ant_salida=v_clases;
						break;
					}
				}
				if (ant_salida!=-1)//Don`t use cost matrix   		  	
				{	   		  		
					if(ant_salida==consequent) //point=1 because consequent of the rule = output of the example
					{
						if(punto.contains(1)==false)
							punto.add((float)1);
					}
					else
					{
						if(punto.contains(0)==false)
							punto.add((float)0);
					}
   				
					if(test==1)
					{
   						Vector<Integer> contenido= confusion_matrix.get(ant_salida);
   						contenido.set(consequent, (contenido.get(consequent)+1));
						confusion_matrix.set(ant_salida, contenido);
						//new BufferedReader(new InputStreamReader(System.in)).readLine();
					}
			
				}
		
		
			}
		}
   	
		else //with cost matrix
		{
			float min=2;
			float max=-1;
			fuzzy maximob= new fuzzy(-1);	
			fuzzy minimob= new fuzzy(2);	
   		
   		
			for(int j=0;j<output.size();j++) 
			{
				for(int v=0; v<values_classes.size(); v++)
				{	
					if(values_classes.get(v).compareTo(output.get(j))==0)
					{
					
						if(label_matrix.compareTo("I")==0)//LTF[0,1] where a=b and c=d (interval--> uniform dominance)
						{						
							//Obtain the minimum the all minimum and the maximum the all maximum and we insert this value as point
							Interval per = new Interval(1-costs.get(v).get(consequent).getd(), 1-costs.get(v).get(consequent).geta());
							if(min>per.getmin())
								min=per.getmin();
							if(max<per.getmax())
								max=per.getmax();
					
						}
						else //Linguistic term --> centroide from the fuzzy ranking
						{
							fuzzy unit= new fuzzy(1);
							fuzzy be=fuzzy.resta(unit,costs.get(v).get(consequent));
							if(Ranking.wang(maximob,be)==1) //be bigger that maximob (maximob<be)
							{
								maximob=be;
								max=(float)Ranking.value_x(be);
							}
							if(Ranking.wang(minimob, be)==0) //(minimob > be)
							{
								minimob=be;
								min=(float)Ranking.value_x(be);
							}
						}
					
						break;
					}
				}
			}
   		
			if(min==max)
				punto.add(min);
	   		else
	   		{
	   			punto.add(min);
	   			punto.add(max);
	   		}
	   	
   		
		
		}
   	    	 
		return punto;
  
    }
    public void show_fitness() throws IOException
    {
    	for (int i=0;i<fitness.size();i++)
    	{
    		fs1.write("Fitness of the rule "+i+" is " + fitness.get(i)+"\n");
    	}
    	
    	
    }
    public void show_fitness_total(int last) throws IOException
    {
    	float minimo=2;
    	float maximo=-1;

    	//In crisp, there are not duplications 
    	for (int i=0;i<fitness_total.size();i++)
    	{    		  
    		if(minimo>(1-fitness_total.get(i)/X.length))
    			minimo= (1-fitness_total.get(i)/X.length);
    		if(maximo<(1-fitness_total.get(i)/X.length))
    		{
    			maximo=(1-fitness_total.get(i)/X.length);
    		}
    	}

    	if(last==0)
    		fs1.write("\n[" + minimo+", "+maximo+"]");
    	
    	else
    	{
    		fs1.write("\n" + minimo);
    		fs1.write("\n" + maximo);
    	}
    		

    }
  
    
    public int iszero(Vector<Float> fit)
    {
    	
			 int iszero=1;
			 for(int j=0;j<fit.size();j++)
			 {
				if(fit.get(j)!=0)
					iszero=0;
			 }
			 
		return iszero;	 
			 
		 
    }
	public void evolution() throws IOException
	{
		   
		 for (int iter=0;iter<iteration;iter++) 
		 {
			 Vector<IndMichigan> pobl_int= new Vector<IndMichigan>();
			 for (int r=0;r<replace/2;r++) 
			 {
				 int p1, p2, padre1, padre2;
				 if(crisp==0)
				 {
					 do
					{
						 p1=(int) (0+(float)(Math.random()*poblation.size()));
					}while(iszero(fitness.get(p1))==1);
				 
					 do{
						 p2=(int)(0+(float)(Math.random()*poblation.size()));
					 }while(iszero(fitness.get(p2))==1);
				 }
				 
				 else
				 {
					 p1=(int) (0+(float)(Math.random()*poblation.size()));
					 p2=(int)(0+(float)(Math.random()*poblation.size()));
				 }
				 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)
					 padre2=p1; 
				 else 
					 padre2=p2;
					 
			
				 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)
					 padre1=p1; 
				 else 
					 padre1=p2;
					
				
				 do
				{
					 if(crisp==0)
					 {
						 do
						{
						 p1=(int) (0+(float)(Math.random()*poblation.size()));
						}while(iszero(fitness.get(p1))==1);
					 
						 do{
						 p2=(int)(0+(float)(Math.random()*poblation.size()));
						 }while(iszero(fitness.get(p2))==1);
					 }
					 
					 else
					 {
						 p1=(int) (0+(float)(Math.random()*poblation.size()));
						 p2=(int)(0+(float)(Math.random()*poblation.size()));
					 }
			
					 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)
						 padre2=p1; 
					 else 
						 padre2=p2;
				 
				 
				}while(padre2==padre1);

									 
				 Vector<Integer> child1= new Vector<Integer>();
				 Vector<Integer> child2= new Vector<Integer>();
				 for(int i=0;i<poblation.get(padre1).getregla().getantecedent().length;i++)
				 {
					 child1.addElement(poblation.get(padre1).getregla().getantecedent(i));
					 child2.addElement(poblation.get(padre2).getregla().getantecedent(i));
				 }
				 
				 float prob= 0+(float)(Math.random() *1);
				if (prob<PROBCROSS) 
				{ 
					for (int i=0;i<child1.size();i++) 
					{
						prob= 0+(float)(Math.random() *1);
						if (prob<0.5) 
						{
							int aux=child1.get(i);
							child1.set(i,child2.get(i));
							child2.set(i, aux);
						}
					}
				}
				
	
				for (int i=0;i<child1.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{
						int value; //value between [0 and number of partitions +1]
						if(child1.get(i)==partitions.get(i).size()-1)
							value=0;
						else
							value=child1.get(i)+1;
						child1.set(i, value);
					}
				}
				
				for (int i=0;i<child2.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{
						int value; 
						if(child2.get(i)==partitions.get(i).size()-1)
							value=0;
						else
							value=child2.get(i)+1;
						child2.set(i, value);
					}
				}
				
				
			 
				//Intermediate population
				pobl_int.addElement(new IndMichigan(child1,X,Y,partitions, classes,COST,alfa,values_classes,costs, type_weight_rule,label_matrix,P));
				pobl_int.addElement(new IndMichigan(child2,X,Y,partitions, classes,COST,alfa,values_classes,costs,type_weight_rule,label_matrix,P));
					
			 }//replace
			   
			 //We order the individuals of the intermediate  population
			Vector<Integer>rules_collocate=Dominance.order(fitness);//order the rules 
			// Replace the worst rules or individuals in the popultation
			for (int i=0;i<pobl_int.size();i++) 
			{
				poblation.set(rules_collocate.get(i),pobl_int.get(i));
			}
			
			//new BufferedReader(new InputStreamReader(System.in)).readLine();
			evaluate_poblation();
			
			System.out.print("."+iter);
			show_fitness_total(0);

			//new BufferedReader(new InputStreamReader(System.in)).readLine();
			   
		 } //for iterations
	
		 

	 	show_rules(fs1);	
	 	show_fitness();//show fitness of each rule
	 	
	 	fs1.write("IMPRECISO");
		show_fitness_total(1);
		
		
		fs1.write("\n");
		COST=1;//Apply cost matrix in test
		test();
		COST=0;//Zero-one loss in test
		test();
		if(label_matrix.compareTo("E")==0)
		{
			COST=2;//Apply cost matrix with linguistic terms
			test();
		}
	
		fs1.close();
		
		
	}
	void show_rules(FileWriter fs1) throws IOException
	{
		for (int i=0;i<poblation.size();i++)
		{
			if(compatible.get(i)==true)
			{
				fs1.write("\nRule "+i);//variables
				poblation.get(i).getregla().show(fs1);
			}
			
		}
		
	}
	void test() throws IOException
	{
	
		for (int i=0;i<confusion_matrix.size();i++) 
    	{
    		Vector<Integer> contenido= new Vector<Integer>();
    		Vector<fuzzy> contenidobo= new Vector<fuzzy>();
    		for (int j=0;j<confusion_matrix.size();j++) 
        	{
    			contenido.add(0);
    			contenidobo.add(new fuzzy(0));
        	}
    		confusion_matrix.set(i,contenido);
    	}
		
        test_exh.clear();
        test_exh_ltf.clear();
		 dis= new FileWriter(fichero_dis);
		 column= new FileWriter(file_columns);
        BufferedReader input;
        input = new BufferedReader(new FileReader(fichero_test));
   
        Character caracter;
        
        int dimx;//number of variables        
        int ncol;//number of columns        
        int nejemplos;//number of examples    
        

        String numero= "";
        int dataset=1000;
        for(int c=0;c<dataset;c++)
        {
        	int contador;
        	System.out.println("\n Test: "+c);
        	column.write(c+"\n");
        	contador=Integer.parseInt(input.readLine());
        	dimx=Integer.parseInt(input.readLine());        
            ncol= dimx+1;        
             
            nejemplos=Integer.parseInt(input.readLine());   
            column.write(nejemplos+"\n");
            
            int nclases=Integer.parseInt(input.readLine());        
            float X[][] = new float[nejemplos][dimx];
            Vector<Vector<Float>> C= new Vector<Vector<Float>>(nejemplos);

          
            for(int i=0; i<nejemplos; i++)
            {
    		
            	for(int j=0; j<ncol-1; j++)
            	{
        		
            		caracter = (char)input.read();
        				while(caracter!=' ' && caracter!='\n')
        				{
        					numero= numero + caracter;
        					caracter = (char)input.read();
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
        		
            	//	Read the output of the instance {1,..,x}
            	caracter = (char)input.read();
        
            	Vector <Float> salidas_imp= new Vector<Float>();
            	while(caracter!='}')
            	{
            		caracter = (char)input.read();
            		while(caracter!=',' && caracter!='}')
            		{
            			numero= numero + caracter;
            			caracter = (char)input.read();
            		}
        		
            		salidas_imp.addElement(Float.parseFloat(numero));
            		numero="";
    			
            		if(caracter!='}')
            			caracter = (char)input.read();//lee el espacio
    			
            	}
            	C.add(i,salidas_imp);
            	caracter = (char)input.read();
            	numero="";
            	
        	
            }//for read the file
        
            Vector<Integer> eliminate= new Vector<Integer>();
            eliminate=missing.values_missing_test(X, nejemplos,dimx);
        
            float X1[][] = new float[(nejemplos-eliminate.size())][dimx];
            Vector<Vector<Float>> C1= new Vector<Vector<Float>>((nejemplos-eliminate.size()));
            int pos=0;
            for(int x1=0; x1<nejemplos;x1++)
            {
            	boolean eliminado = false;
            	for(int e=0;e<eliminate.size();e++)
            	{
            		if(eliminate.get(e)==x1)
            			eliminado=true;
            	}
            	if(eliminado==false)
            	{
            		X1[pos]=X[x1];
            		C1.add(C.get(x1));
            		pos++;
            	}
    		}
         
            X=X1;
            C=C1;
            
            evalua_poblation_test(dimx,X,C);
		
            if(COST==2)
            	show_fitness_total_test_ltf(X,c);
            else
            	show_fitness_total_test(X,c);
		
        }//for the datasets
        
        input.close();
		 dis.close();
		 column.close();
			
		 if(COST==2)
		 {
			 fuzzy sumaminimo= new fuzzy(0);
			 fuzzy sumamaximo= new fuzzy(0);
			 fuzzy ejemplos= new fuzzy(test_exh_ltf.size());
			 for(int t=0;t<test_exh_ltf.size();t++)
				{
					sumaminimo=fuzzy.suma(test_exh_ltf.get(t).get(0),sumaminimo);
					sumamaximo=fuzzy.suma(test_exh_ltf.get(t).get(1),sumamaximo);					
				}
			    sumaminimo=fuzzy.div(sumaminimo,ejemplos);
			    sumamaximo=fuzzy.div(sumamaximo,ejemplos);
				fs1.write("Test\n["+sumaminimo.a+" "+sumaminimo.b+" "+sumaminimo.c+" "+sumaminimo.d+"]\n["+sumamaximo.a+" "+sumamaximo.b+" "+sumamaximo.c+" "+sumamaximo.d+"]\n");
				fs1.write(Ranking.value_x(sumaminimo)+" "+Ranking.value_x(sumamaximo));
				 
		 }
		 else
		 {
			 float minimo=0;
			float maximo=0;
			for(int t=0;t<test_exh.size();t++)
			{
				minimo=minimo+test_exh.get(t).getmin();
				maximo=maximo+test_exh.get(t).getmax();				
			}
			fs1.write("Test\n"+(minimo/test_exh.size())+"\n"+(maximo/test_exh.size())+"\n");
			
			if(COST==0)
			{
				fs1.write("Confusion matrix");
				for (int i=0;i<confusion_matrix.size();i++) 
		    	{
					fs1.write("\n ");
		    		for (int j=0;j<confusion_matrix.size();j++) 
		        	{
		    			fs1.write("["+i+","+j+"]= "+((float)confusion_matrix.get(i).get(j)/test_exh.size())+" ");
		        	}
		    		
		    	}
				fs1.write("\n ");
			}
		 }
		 
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
	    
	
	 public void evalua_poblation_test(int dimx,float X[][],Vector<Vector<Float>> C) throws IOException
	    {
	    	
		 fitness_total.clear();
		 fitness_total_ltf.clear();
			
		 for (int i=0;i<X.length;i++)//x.length indicates the number of example in test 
		 {
			 Vector<Float> points = new Vector<Float>(); 
			 float point_point[]= new float[dimx];
	    	 for(int p=0;p<dimx;p++)
	    	 {
	    		 point_point[p]=X[i][p];
	    	 }
	    	 float Z[][] = new float[1][dimx];
	    	 Z[0]=point_point;		
	    		
	    	
	    	 Vector<Float> compatibilidad = new Vector<Float>(fitness.size());//save the compatibility				
	    	 int rule=-1;   	 
	    	 float comp_regla=0;  				        		 
	    	 for(int j=0; j<TAMPO; j++)
			 {
				 //Calculate the compatibility
				 if(compatible.get(j)==true)
				 {
					 compatibilidad.add(j, poblation.get(j).getregla().match(Z[0],partitions));					 
					 float compa = compatibilidad.get(j)*poblation.get(j).getregla().getpeso(); 
					 if(compa>comp_regla)
					 {	  
						 comp_regla=compa;
						 rule=j;
					 }
				 }
				 
				 else
					 compatibilidad.add(j,(float)0.0); 
			 }
    		    	   
	    	 Vector<Float> point=new Vector<Float>();//winner rule
	    	 Vector<fuzzy> ltf=new Vector<fuzzy>();// winner rule cost matrix


	    	 create_file_columns(rule, C.get(i));
			 if(rule!=-1)
			 {
				if(COST==2) //evaluate with cost matrix
					 ltf=puntuation_ltf(rule, C.get(i));
				 else
				 {
					 point=puntuation(rule, C.get(i),1);
				 
					 for(int pun=0;pun<point.size();pun++)
					 {
						 if(points.contains(point.get(pun))==false)
							 points.addElement(point.get(pun));
					 }
				 }
				 	 
			 }
						
			 else //There are not compatible rule 
	    		{
	    			if(COST==2)
	    				ltf=puntuation_ltf(-1, C.get(i));
	    			else
	    			{
	    				point=puntuation(-1, C.get(i),1);
	    				for(int p=0;p<point.size();p++)
	    				{
	    					
	    					if(points.contains(point.get(p))==false)
	    						points.addElement(point.get(p));
	    				}
	    			
	    			}
	    			
	    		}
	    		
			 	if(COST==2)
		    	    	calculo_fitness_ltf(ltf);
			 	else
			 		calculation_fitness_total(points);
			 
			}	
	    	
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
	 
	 
	  public Vector<fuzzy> puntuation_ltf(int rule, Vector<Float> output) throws IOException
	    {
			int consequent=0;
	    	Vector<fuzzy> point=new Vector<fuzzy>();
	    	
	    	if(rule!=-1)
	    	{
	    		Float [] cons=poblation.get(rule).getregla().getconsequent();
			
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
	    	
	   
	    	fuzzy maximob= new fuzzy(-1);	
	   		fuzzy minimob= new fuzzy(2);	
	   		
	   		
	   		for(int j=0;j<output.size();j++) 
			{
				for(int v=0; v<values_classes.size(); v++)
				{
					if(values_classes.get(v).compareTo(output.get(j))==0)
					{
						fuzzy unit= new fuzzy(1);
						fuzzy be=fuzzy.resta(unit,costs.get(v).get(consequent));
						if(Ranking.wang(maximob,be)==1) //be bigger than maximob (maximob<be)
							maximob=be;
							
						if(Ranking.wang(minimob, be)==0) //(minimob > be)
							minimob=be;
						break;
					}
				}
			}
	   		
	   		if(minimob.equals(maximob))
	   		 point.add(minimob);
	   		else
	   		{
	   			point.add(minimob);
	   			point.add(maximob);
	   		}
	   		 
			
	   	
	   	return point;
	    }
	  
	  public void calculo_fitness_ltf(Vector<fuzzy> puntos) throws IOException
	    {
		  Vector<fuzzy> insert= new Vector<fuzzy>();
	    	if(fitness_total_ltf.size()==0)
	    	{
	    		for(int i=0;i<puntos.size();i++)
	    			fitness_total_ltf.addElement(puntos.get(i));
	    	}
	    	else if(fitness_total_ltf.size()!=0)
	    	{
	    		for(int i=0; i<fitness_total_ltf.size();i++)
	    		{	    	
	    			for(int j=0;j<puntos.size();j++)
	    			{
	    				fuzzy new_value= new fuzzy();
	    				new_value=fuzzy.suma(fitness_total_ltf.get(i),puntos.get(j));
	    				insert.addElement(new_value);
	    			}
	    		}
	    		fuzzy maximob= new fuzzy(-1);	
		   		fuzzy minimob= new fuzzy(-1);
		   		fitness_total_ltf.clear();
		   		

	    		for(int i=0; i<insert.size();i++)
	    		{	
	    			if(maximob.a==-1 && maximob.es_crisp()==1)
	    				maximob=insert.get(i);
	    			else
	    				if(Ranking.wang(maximob,insert.get(i))==1) // (maximob<be)
	    					maximob=insert.get(i);
	    			
	    			if(minimob.a==-1 && minimob.es_crisp()==1)
	    				minimob=insert.get(i);
	    				
	    			else
	    				if(Ranking.wang(minimob, insert.get(i))==0) //(minimob > be)
	    					minimob=insert.get(i);
	    		}
	    		
	    		fitness_total_ltf.addElement(minimob);
	    		fitness_total_ltf.addElement(maximob);
	    	}
	    	
	    }
	  
	  
	  public void show_fitness_total_test_ltf(float X[][],int c) throws IOException
	    {
		  fuzzy maximob= new fuzzy(-1);	
	   		fuzzy minimob= new fuzzy(2);
	   		fuzzy unidad= new fuzzy(1);
	   		fuzzy examples= new fuzzy(X.length);
	   		fuzzy be= new fuzzy(0);
	    	for (int i=0;i<fitness_total_ltf.size();i++)
	    	{
				be=fuzzy.div(fitness_total_ltf.get(i),examples);
				be=fuzzy.resta(unidad, be);
				
				if(Ranking.wang(maximob,be)==1) //(maximob<be)
					maximob=be;
				
				if(Ranking.wang(minimob, be)==0) //(minimob > be)
					minimob=be;
	    		
	    	}
	    	Vector<fuzzy> fit_ejemplo = new Vector<fuzzy>();
	    	fit_ejemplo.addElement(minimob);
	    	fit_ejemplo.addElement(maximob);
	    	test_exh_ltf.addElement(fit_ejemplo);
	    	 
	    	 dis.write(c+"\n"+minimob.a+" "+minimob.b+" "+minimob.c+" "+minimob.d+"\n");
	    	 dis.write(maximob.a+" "+maximob.b+" "+maximob.c+" "+maximob.d+"\n");
	    }
	
	 
	 
	 
}


