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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;


/**
 * 
 * File: fuzzyRule.java
 * 
 * Properties and functions of the fuzzy rule as obtain the antecedent and
 * the consequent of the rule from the confidence the this rule with
 * the instances
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */

public class fuzzyRule {

	Integer[] antecedent;  
	Float[] consequent; // the rule only have one consequent
	int NOIMPORTA=-1;
	int asing_weight_rule=0;
	
	
	public fuzzyRule(Vector<fuzzyPartition> pentradas,int classes,int asign_weight_re)
	{
		antecedent = new Integer[pentradas.size()];
		consequent = new Float[classes];
		asing_weight_rule=asign_weight_re;
		
		for(int i=0; i<pentradas.size(); i++)
			antecedent[i]=NOIMPORTA;
		for(int i=0; i<classes; i++)
			consequent[i]=(float)0;
	}
	
	public void obtain_rule(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			 Vector<Float> values_clases,Vector<Vector<fuzzy>> pesos, int ejemplo,String label,Vector<Float> p) throws IOException
	{
		for (int i=0;i<pentradas.size();i++) //pentradas indicates the total of variables for each example
   	 	{
			float value=0;
			float max=-1;
			int partition=-1;
			for (int j=0;j<pentradas.get(i).size();j++) 
			{
				if(x[ejemplo][i].es_crisp()==1)
				{
					value = pentradas.get(i).membership(j, x[ejemplo][i].a);
				}
				else
					value = pentradas.get(i).aproximation(j, x[ejemplo][i]);
				
				if(value>max)
				{
					max=value;
					partition=j;
				}
			}
			antecedent[i]=partition;
   	 	}
		
		calculateconsequent(x,y,pentradas, classes,COST, alfa,values_clases,pesos,label,p);
		
	}
	public void obtain_rule_random(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int clases, int COSTES, int alfa,
			 Vector<Float> valores_clases,Vector<Vector<fuzzy>> pesos,String etiqueta,Vector<Float> p) throws IOException
	{	 
		for (int i=0;i<pentradas.size();i++) 
  	 	{
			int valor=(int) (0+(float)(Math.random()*(pentradas.get(i).size()))); 
			antecedent[i]=valor;
  	 	}
		
		calculateconsequent(x,y,pentradas, clases,COSTES, alfa,valores_clases,pesos,etiqueta,p);	  
	}
	public void obtain_rule_random_eje(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			 Vector<Float> values_clases,Vector<Vector<fuzzy>> pesos,String label,Vector<Float> p,
			 int instance) throws IOException
	{	 		
		for (int i=0;i<pentradas.size();i++)
   	 	{
			float prob= 0+(float)(Math.random() *1);
			float valor=0;
			float maximo=-1;
			int particion=-1;
			
				for (int j=0;j<pentradas.get(i).size();j++) 
				{
			
					if(x[instance][i].es_crisp()==1)
					{
						valor = pentradas.get(i).membership(j, x[instance][i].a);
					}
					else
						valor = pentradas.get(i).aproximation(j, x[instance][i]);
				
					if(valor>maximo)
					{
						maximo=valor;
						particion=j;
					}
				}
				
				if(prob>0.5)
				{
					if(particion==pentradas.get(i).size()-1)
						particion=0;
					else
						particion=particion+1;
					
				}
			antecedent[i]=particion;
   	 	}
   	 	
		
		calculateconsequent(x,y,pentradas, classes,COST, alfa,values_clases,pesos,label,p);	
		  
		 	
		  
	}
	
	void calculateconsequent(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int clases, int COSTES, int alfa,
			 Vector<Float> valores_clases,Vector<Vector<fuzzy>> pesos,String etiqueta,Vector<Float> p)throws IOException
	{
		  Interval maxconfidence=new Interval(0,0);
		  fuzzy betterconf= new fuzzy(0);
		  Interval savesoporte=new Interval(0,0);
		  fuzzy sum_confidence=new fuzzy(0);
		  Vector<fuzzy>confidence= new Vector<fuzzy>();
		  
		  
		  for (int cc=0;cc<consequent.length;cc++)
		  {
			  confidence.add(new fuzzy(0));
		  }
		  
		  int mejorPC=0;//best partition
		  
		  if(COSTES==1)
		  {
			  Interval actual = new Interval(0,0);
			  fuzzy actual1= new fuzzy(0);
		  
			  for(int p1=0;p1<pesos.get(0).size();p1++)
			  {
				  Interval sumatorio = new Interval(0,0);
				  fuzzy sumatorio1= new fuzzy(0);
			 
				  for(int p0=0;p0<pesos.size();p0++)
				  {
					  if(p0!=p1 && etiqueta.compareTo("I")==0 )
					  {
						  Interval valor = new Interval((1-pesos.get(p0).get(p1).geta()),(1-pesos.get(p0).get(p1).getd()));
						  sumatorio.setmin(sumatorio.getmin()+valor.getmin());
						  sumatorio.setmax(sumatorio.getmax()+valor.getmax());
					  }
					  if(p0!=p1 && etiqueta.compareTo("I")!=0 )
					  {
						  fuzzy valor = new fuzzy();
						  fuzzy unidad= new fuzzy(1);
						  valor =fuzzy.resta(unidad, pesos.get(p0).get(p1));
					  
						  sumatorio1= fuzzy.suma(sumatorio1,valor);
					  }
				  }
				  if(etiqueta.compareTo("I")==0 )
				  {
					  if(Dominance.uniform_compatibility(actual, sumatorio)==1)
					  {
						  mejorPC=p1;
						  actual=sumatorio;
					  }
				  }
				  else
				  {
					  if(Ranking.wang(actual1, sumatorio1)==1)
					  {
						  mejorPC=p1;
						  actual1=sumatorio1;
					  }   
				  }	
			  
			  }
		  }
			
		  for (int cc=0;cc<consequent.length;cc++)
		  {
			  for (int i=0;i<consequent.length;i++) 
				  consequent[i]=(float)0;
			  consequent[cc]=(float)1;				 
			
			  Vector<fuzzy> weight_rule= new Vector<fuzzy>();
			  calculateConfidence(x,y,pentradas, clases,COSTES, alfa,valores_clases, pesos,weight_rule,etiqueta,p);

			  confidence.set(cc, weight_rule.get(0));
			  
			  if(etiqueta.compareTo("I")==0)
			  {
				 
				  
				  Interval confi = new Interval(weight_rule.get(0).geta(),weight_rule.get(0).getd());
				  if(Dominance.uniform_compatibility(maxconfidence, confi)==1)//if confi is bigger than maxconfidence
				  {
					  mejorPC=cc;
					  maxconfidence=confi;
					  Interval soporte = new Interval(weight_rule.get(1).geta(),weight_rule.get(1).getd());
					  savesoporte=soporte;
					  
				  }
				  else
					  sum_confidence=fuzzy.suma_ltf(weight_rule.get(0), sum_confidence);
					  
				  
			  }
			  else //RANKING FUZZY
			  {
				  if(Ranking.wang(betterconf, weight_rule.get(0))==1)
				  {
					  mejorPC=cc;
					  betterconf=weight_rule.get(0);
				  }
			  }
		  
			}
			
		
		 
			  for (int i=0;i<consequent.length;i++) 
				  consequent[i]=(float)0;
			  consequent[mejorPC]=(float)1;
	}
		
	public void calculateConfidence(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int clases, int COSTES, int alfa,
			 Vector<Float> valores_clases,Vector<Vector<fuzzy>> pesos,Vector<fuzzy> weight_rule,
			 String etiqueta,Vector<Float> p) throws IOException
	{
	
		
		fuzzy degree = new fuzzy(0);
		int ej_compatibles=0;
		fuzzy sumamab= new fuzzy(0);
		
		for(int i=0;i<2;i++)
		{
			fuzzy inicio= new fuzzy(0);
			weight_rule.add(i, inicio);
		}
		
		for (int i=0;i<x.length;i++)//x.length indicates the number of instances 
		{ 
			Interval ma=new Interval(0,0);	
			ma=match_alfa(x[i],pentradas, alfa); //x[i], are the values of the variables of the instances i, ma is an interval
			fuzzy mab= new fuzzy();
			mab.borrosorectangular(ma.getmin(),ma.getmax());			
			if(mab.getcero()==0)
			{
				fuzzy mc=match_salida(y.get(i),clases,COSTES,valores_clases, pesos,etiqueta); //mc is a float
				fuzzy mabmc= new fuzzy();
				mabmc=fuzzy.multi(mc,mab);
				
				//The cost of the instances is always 1 with imprecise data because we
				//don't have duplications, however with crisp data we have to take 
				//into account the number of duplications made due to the imprecise
				//output. The cost of the instances will be proportional to the number
				//of duplications made
				mabmc=fuzzy.multinumero(p.get(i),mabmc);
				degree=fuzzy.suma(degree,mabmc);		
				sumamab=fuzzy.suma(sumamab, mab);			
			}
			
		}
			
		if(sumamab.getcero()==0) //is not zero, centroide of the sum of all memberships
		{
			fuzzy confianza=new fuzzy();
			double deconfianza=Ranking.value_x(sumamab);
			confianza=fuzzy.divnum(degree,(float) deconfianza);
			weight_rule.set(0,confianza); 
		}

		fuzzy lon= new fuzzy(x.length);
		weight_rule.set(1,fuzzy.div(degree,lon)); //calculate the support that indicates if there are example compatible with the rule


		
	}
	
	public fuzzy match_salida(Vector<Float> output,int classes, int COST, Vector<Float> values_clases,Vector<Vector<fuzzy>> pesos,
			String label) throws IOException
	{
		
		Interval max=new Interval(0);
		fuzzy maxb= new fuzzy(0);		
		fuzzy be= new fuzzy(0);
		fuzzy benefit = new fuzzy(0);
		for (int i=0;i<classes;i++)  
		{
			if(consequent[i]!=0)
			{
				if(COST==0)
				{
					for(int j=0;j<output.size();j++) 
					{
						int ant_output=-1;
						for(int v=0; v<values_clases.size(); v++)
						{
							if(values_clases.get(v).compareTo(output.get(j))==0)
							{
								ant_output=v;
								break;
							}
						}
						if(ant_output!=-1)//no use the cost matrix   		  	
		   		  		{	   		  		
							if(ant_output==i)
							{
		   		  				return  be.borrosorectangular(1, 1);
		   		  				//if the output is a set, the punctuation for the consequent will be 1 
		   		  				//if this consequent matchs with some output (the result is not {0,1}, the result is the bigger point}
		   		  			}
		   		  		}
					
					} //for of output
				}
				
				else //with matrix cost
				{
					for(int j=0;j<output.size();j++) 
					{
						for(int v=0; v<values_clases.size(); v++)
						{ 
							if(values_clases.get(v).compareTo(output.get(j))==0)
							{							
								if(label.compareTo("I")==0) //Interval where a=b and c=d (uniform dominance)
								{
								    
								    Interval per = new Interval(1-pesos.get(v).get(i).getd(), 1-pesos.get(v).get(i).geta());
								    if(Dominance.uniform_compatibility(max, per)==1) //per is bigger than max
									{
								    	max=per;
									}
									
									benefit.borrosorectangular(max.getmin(), max.getmax());
								}
								else //fuzzy ranking
								{
									fuzzy unidad= new fuzzy(1);
									be=fuzzy.resta(unidad,pesos.get(v).get(i));
										if (Ranking.wang(maxb,be)==1) 
											maxb=be;
									benefit=maxb;
									
								}
								break;
							}
						}
					}
				}
				
				break;
			}
 
		}
		
		if(COST==0)//the position of the consequent not match with with the outputs
		{
			return be.borrosorectangular(0, 0);
		}
		else
			return benefit;
		
		
	}
	public Interval match_alfa(fuzzy[] x,Vector<fuzzyPartition> pentradas, int alpha) throws IOException
	{
		//Compatibility between the antecedent of the rule and the example (composes by imprecise data)
		//We obtain this compatibility from alpha cuts
		
		Interval m= new Interval(1,1); 
		float valuealpha=1;
	
		for (int i=0;i<antecedent.length;i++) 
		{
			Interval certainty = new Interval(-1,-1);//save the certainty between the example and antecedent
			for(int c=0;c<alpha;c++)
		   	{
				//value of the alpha cut
		   		valuealpha=1-(float)((float)c*(float)(1/(float)(alpha-1)));
		   		//Initialize the cuts obtained from alpha cuts
		   		Vector<Float> cut = new Vector<Float>();
		   		Vector<Float> cutparticion = new Vector<Float>();
		   		if(x[i].es_rect()==1) //fuzzy(rectangular)
	   			{
		   			//pentradas.get(i).get(antecedent[i]).show();
		   			//Obtain the value of the alpha cut in the partition (x) and them we check if x
		   			// is in the interval
		   			cut=pentradas.get(i).get(antecedent[i]).cut(valuealpha); //value of x
	   			}
		   		else //fuzzy (triangular) 
		   		{
		   			//obtain x from alpha and the variable of the example
		   			//and them we check the membership in the partition
		   			cut=x[i].cut(valuealpha);
		   			cutparticion=pentradas.get(i).get(antecedent[i]).cut(valuealpha); //value of x
		   		}
		   		
		   		for(int k=0; k<cut.size();k++ )
		   		{
		   			if(x[i].es_rect()==1) //fuzzy (rectangular)
		   			{
		   				if((cut.get(k)>=x[i].geta() && cut.get(k)<=x[i].getd()))
		   				{
		   					if(certainty.getmin()==-1 && certainty.getmax()==-1)
		   					{
		   						certainty.setmin(valuealpha);
		   						certainty.setmax(valuealpha);
		   					}
		   					certainty.setmin(tnorma(certainty.getmin(),valuealpha,0));//minimum
		   					certainty.setmax(tconorma(certainty.getmax(),valuealpha)); //maximum
		   				}
		   				
		   			}
		   			else //fuzzy (triangular)
		   				{
		   				//obtain x from alpha and the variable of the example
			   			//and them we check the membership or certainty of x in the antecedent of the rule
		   				 if(certainty.getmin()==-1 && certainty.getmax()==-1)
		   				 {
		   					 certainty.setmin(pentradas.get(i).membership(antecedent[i],cut.get(k)));
		   					 certainty.setmax(pentradas.get(i).membership(antecedent[i],cut.get(k)));
		   				 }
		   				 certainty.setmin(tnorma(certainty.getmin(),pentradas.get(i).membership(antecedent[i],cut.get(k)),0));//minimum
		   				 certainty.setmax(tconorma(certainty.getmax(),pentradas.get(i).membership(antecedent[i],cut.get(k)))); //maximum	 		   					
		   				
		   			}
		   			
		   		} //for cuts
		   		
		   	} //for  alpha
			
			//If the variable of the example (rectangular) no belong into any alpha, 
			//rectangular: we calculate the membership the ends of the interval. This membership  will be
			//the compatibility the this antecedent and the variable of the example 
			if(x[i].es_rect()==1) //fuzzy(rectangular)
   			{
				if(certainty.getmin()==-1 && certainty.getmax()==-1)
				{					
	 		   		certainty.setmin(pentradas.get(i).membership(antecedent[i], x[i].geta()));
	 		   		certainty.setmax(pentradas.get(i).membership(antecedent[i], x[i].getd()));
	 		   		certainty.ordenar();
				}
   			}
			m.setmin(tnorma(m.getmin(),certainty.getmin(),1));
			m.setmax(tnorma(m.getmax(),certainty.getmax(),1));
				
		}//for antecedents
		
		return m;
	}
	
	public float match(float[] x,Vector<fuzzyPartition> pentradas)
	{
		float m=1,m1;
		for (int i=0;i<antecedent.length;i++) 
		{
			m1=pentradas.get(i).membership(antecedent[i],x[i]);
			// and is product
			m=tnorma(m,m1,1);
		}
		
		return m;
	}
	public float tnorma(float x, float y, int tnr) 
	{
	    if (tnr==0) // is the minimum 
	    {
	      if (x<y) return x; 
	      else return y;
	    } 
	    else return x*y;
	}
	public float tconorma (float x, float y)
	{
		  if (x<y) return y; else return x;//return the maximum 
	}
	
	public void show(FileWriter fs1) throws IOException
	{
	 		
  		//System.out.print("\n A: ");
  		fs1.write("\n A: ");//variables
  		for(int j=0; j<antecedent.length; j++)
  		{
  			//System.out.print( antecedent[j]+" ");
  			fs1.write(antecedent[j]+" ");//variables
  		}
  		//System.out.print("\n C: ");
  		fs1.write("\n C: ");//variables
  		for(int j=0; j<consequent.length; j++)
  		{
  			//System.out.print(consequent[j]+" ");
  			fs1.write(+consequent[j]+" ");//variables
  		}
	  
	}
	public void show1() throws IOException
	{
	 		
  		System.out.print("\n A: ");
  		
  		for(int j=0; j<antecedent.length; j++)
  		{
  			System.out.print( antecedent[j]+" ");
  			
  		}
  		System.out.print("\n C: ");
  		
  		for(int j=0; j<consequent.length; j++)
  		{
  			System.out.print(consequent[j]+" ");
  			
  		}

	  
	}
	
	public int size() { return antecedent.length;}
	public void setantecedent(Integer[] A) { antecedent=A; }
	public void setConsecuente(Float[] C) { consequent=C; }
	
    public Integer[] getantecedent()  { return antecedent; }
	public int getantecedent(int n) { return antecedent[n]; }
    
	public Float[] getConsecuente(){ return consequent; }
    public float getConsecuente(int c) { return consequent[c]; }
	public void setConsecuente(int c,float v) { consequent[c]=v; }
	 public float getpeso() 
	    { 
	    	for (int i=0;i<consequent.length;i++) 
	    	{
	    		if(consequent[i]!=0)
	    			return consequent[i];
	    	}
			return 0;
	    }
	 
	 public void setpeso(float v) 
	    { 
	    	for (int i=0;i<consequent.length;i++) 
	    	{
	    		if(consequent[i]!=0)
	    			consequent[i]=v;
	    	}
			
	    }
	 
	 
	 
	 static float haausdorff(Interval A, Interval B)
	 {
			return calculo_hausdorff(Math.abs(A.getmin() - B.getmin()),
						Math.abs(A.getmax() - B.getmax()));	  

	 }


	 static float calculo_hausdorff(float min, float max)
	 {
	 	if(min>max) return min;
	 	else 
	 		return max;
	 	}



	 
	
	
}

