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

package keel.Algorithms.LQD.methods.FGFS_Rule_Weight_Penalty;

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
 * imprecise or low quality. We penalize the rules and
 * we work with weight rule
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
    float PROBCROSS;
    int TAMPO;
    int iteration;
    fuzzy[][] X;
    Vector<Vector<Float>> Y;
    int classes;
    Vector<partition> partitions;
    int replace;
    int alfa;
    FileWriter fs1;
    String fichero_test;
    String fichero_entre;
    FileWriter dis;
    FileWriter column;
    Vector<Float> values_classes;
    Vector<Vector<Float>> costs;
    int examples;
    int desconocidos;
    
  
    int type_compatible;
    int distribute; //Distribute the the points between the rules if the value is 1 (else only point one rule, the winner)
    int type_weight_rule; //with 0 the rule has not weight
    
  int pos_classe;//if one example is not compatible with no rule
  Vector<Interval> test_exh;
  String fichero_dis;
  String file_columns;
  
  

	
    public AlgGenetic(int npoblation,  float muta,float cross,Vector<partition> parte,
    		int nclasses,fuzzy[][] x,Vector<Vector<Float>> y, int niteraciones,
    	 int re, String nombre, int al, String fichero, String entre,Vector<Float> values,
    	 int nejemplos,int des, int tipo_comp, int repar,int asign_weight_rule,String dist,String columns) throws IOException
    { 
    	
    	fichero_dis=dist;
    	file_columns=columns;
    	test_exh=new Vector<Interval>();
    	type_weight_rule=asign_weight_rule;
    	type_compatible=tipo_comp;
    	distribute=repar;

    	X=x;
    	desconocidos=des;
    	fichero_test=fichero;
    	fichero_entre=entre;
    	Y=y;
    	 fs1= new FileWriter(nombre);

    	replace=re;
    	PROBCROSS= cross;
		PROBMUTA= muta;	
		classes=nclasses;
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
                partitions= new Vector<partition>(parte.size());
		fitness_total= new Vector<Float>();
		total= new Vector<Float>();
		partitions=parte;
		values_classes= new Vector<Float>(nclasses);
		costs= new Vector<Vector<Float>>(nclasses);
		TAMPO= npoblation;
		iteration= niteraciones;
		values_classes=values;
		examples=nejemplos;
	    
	     
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
		
		
			pos_classe= position;//quiero la posicion de la clase mas frecuente
		


		//Initialize the population
		for (int i=0;i<TAMPO;i++) 
		{
			//System.out.println("Inicializando poblation[" + i + "]");
			IndMichigan ind = new IndMichigan(x,y,partitions, classes,0,alfa,values_classes,costs,i, type_weight_rule);
			poblation.add(i, ind);
		}
		
		evaluate_poblation();
		//show_fitness();//show the fitness of each rule
		
		//Show the total fitness, summation of all rules
		show_fitness_total(0,0);
		//new BufferedReader(new InputStreamReader(System.in)).readLine();

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
    		int rule_second=-1;//no rule is compatible
    		Vector<Interval> compatibility = new Vector<Interval>(fitness.size());//save the compatibility betwee the example and the rules
    		for (int j=0;j<compatibility.size();j++)
    		{
        		compatibility.add(j,new Interval(-1,-1));
    		}
    		
    		
    		//Get the best rule, for the current example, from compatibility to exist between them
    		for(int j=0; j<TAMPO; j++)
    		{
    			compatibility.add(j, poblation.get(j).getregla().match_alpha(X[i],partitions, alfa));
    			compatibility.set(j,compatibility.get(j).multiplicar(poblation.get(j).getregla().getpeso()));
    			if(rule_second==-1)
    			{
    				//Obtain the consequent of the actual rule
    				Float [] cons_individuo=poblation.get(j).getregla().getconsequent();
    				for (int c=0; c<cons_individuo.length; c++)
    				{
    					if(cons_individuo[c]!=0 && c==pos_classe)
    					{
        				  rule_second=j;
    					}
    				}
    			}
	
    		}//For 
    		    		    	
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
    						no_eliminado=false; //ya la hemos eliminado
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

    		
    		
    		//Get the points of all possible rules winner from the consequent of the rule and
    		//the output of the example (that can be a set of values)

    		
    		Vector<Integer> winner=new Vector<Integer>();//guardamos las reglas winner para distribuir entre ellas los puntos
    		winner.clear();
    		int win=-1;
    		

    		Vector<Float> point=new Vector<Float>();
    		float max=-1;
    		Vector<Float> points = new Vector<Float>();   		
    		

    		
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
    			if(existe==false)// is a winner rule
    			{
    				point=puntuation(r, Y.get(i));
    				for(int p=0;p<point.size();p++)
    					{
    						if(points.contains(point.get(p))==false)
    							points.addElement(point.get(p));
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
    		
		    
    		
    		if((winner.size()!=0 && distribute==1) ||( win!=-1 && distribute==0))
    		{
    			if(distribute==1)
    			{
    				Vector<Float> reparto_points = new Vector<Float>();
    				for(int c=0;c<points.size();c++)
    				{
    					
    					if(points.get(c)!=0)
    						reparto_points.addElement((Float)points.get(c)/winner.size());
    					else
    						reparto_points.addElement(points.get(c));
    				}
    				for(int g=0; g<winner.size();g++)
    				{
    					fitness.set(winner.get(g),summation(fitness.get(winner.get(g)),reparto_points)); //con costes es % coste las reglas
    					compatible.set(winner.get(g), true);
    						
    				}
    			}
    			else
				{
					fitness.set(win,summation(fitness.get(win),points)); 
					compatible.set(win, true);
				}
    		
    		}
    		else //There are not compatible rules 
    		{
    			point=puntuation(-1, Y.get(i));
				for(int p=0;p<point.size();p++)
				{
					
					if(points.contains(point.get(p))==false)
						points.addElement(point.get(p));
				}
    			
    		}
    			
    		calculo_fitness_total(points);//The total fitness is expressed by zerp-one loss 
    		
		}
    	
    	
    }
   public Vector<Float> summation(Vector<Float> values_r,Vector<Float> point) throws IOException
    {
    	Vector<Float> insert= new Vector<Float>();
    	insert.clear();
    	
		
    	for(int i=0; i<values_r.size();i++)
    		{
    			for(int j=0;j<point.size();j++)
    			{
    				float new_value;
    				if(point.get(j)==0)//penalize rule
    					new_value=(float) (values_r.get(i)- (0.1*values_r.get(i)));
    				else//increase the fitness en 1
    					new_value=values_r.get(i)+point.get(j);
    				
    				if(insert.contains(new_value)==false)
    					insert.addElement(new_value);
    			}
    		}
    		
    	    values_r.clear();
    		for(int i=0; i<insert.size();i++)
    		{
    		
    			if(values_r.contains(insert.get(i))==false)
    				values_r.addElement(insert.get(i));
    		}
    	
    
    		
    		//Look for the minimum and maximim
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
 

    
    public Vector<Float> puntuation(int rule, Vector<Float> output)
    {
    	int consequent=0;
    	
    	Vector<Float> point=new Vector<Float>();
    	
		
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
    	
    
   		for(int j=0;j<output.size();j++)
   		{
   			int ant_output=-1;
   			for(int v_classes=0; v_classes<values_classes.size(); v_classes++)
   			{
   				if(values_classes.get(v_classes).compareTo(output.get(j))==0)
   				{   			//		
   					ant_output=v_classes;
   					break;
   				}
   			}
   			if (ant_output!=-1)//Don`t use cost matrix   		  	   		  	
   			{	   		  		
   				if(ant_output==consequent) //point=1 because consequent of the rule = output of the example
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
    		fs1.write("Fitness of the rule "+i+" is " + fitness.get(i)+"\n");
    	}
    	
    	
    }
    public void show_fitness_total(int last, int c) throws IOException
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
    	if(last==0)
    		fs1.write("\n[" + minimo+", "+maximo+"]");
    	
    	else
    	{
    		fs1.write("\n" + minimo);
    		fs1.write("\n" + maximo);
    	}
    		
    	
    }
  
	public void evolution() throws IOException
	{
		   
		 for (int iter=0;iter<iteration;iter++) 
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
			
					
				 // 
				 Vector<Integer> hijo1= new Vector<Integer>();
				 Vector<Integer> hijo2= new Vector<Integer>();
				 for(int i=0;i<poblation.get(padre1).getregla().getAntecedente().length;i++)
				 {
					 hijo1.addElement(poblation.get(padre1).getregla().getAntecedente(i));
					 hijo2.addElement(poblation.get(padre2).getregla().getAntecedente(i));
				 }
				 
			
				 float prob= 0+(float)(Math.random() *1);
				if (prob<PROBCROSS) 
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
						int valor=(int) (0+(float)(Math.random()*(partitions.get(i).size()))); //value between [0 and number of partitions +1]
						hijo1.set(i, valor);
					}
				}
				
				for (int i=0;i<hijo2.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{
						int valor=(int) (0+(float)(Math.random()*(partitions.get(i).size())));//value between [0 and number of partitions +1]
						hijo2.set(i, valor);
					}
				}
			
				//Intermediate population
				pobl_int.addElement(new IndMichigan(hijo1,X,Y,partitions, classes,0,alfa,values_classes,costs,type_weight_rule));
				pobl_int.addElement(new IndMichigan(hijo2,X,Y,partitions, classes,0,alfa,values_classes,costs,type_weight_rule));
					
		
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
			show_fitness_total(0,0);

			//new BufferedReader(new InputStreamReader(System.in)).readLine();
			     
 
		 } //for de iteraciones
	
		 

	 	show_reglas(fs1);	
	 	show_fitness();//show fitness of each rule
	 	
	 	fs1.write("IMPRECISO");
		show_fitness_total(1,0);
		
		
		//poda_reglas();
		
		
		
		test();
		fs1.close();
				
		
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
        int nejemplos;    
        
        
       
        String numero= "";
        for(int c=0;c<1000;c++)
        {
        	System.out.println("\n Test: "+c);
        	column.write(c+"\n");
        	
        	int contador=Integer.parseInt(entrada.readLine());
        	dimx=Integer.parseInt(entrada.readLine());        
            ncol= dimx+1;        
             
            nejemplos=Integer.parseInt(entrada.readLine());        
           
            int nclasses=Integer.parseInt(entrada.readLine());        
           
            float X[][] = new float[nejemplos][dimx];
            Vector<Vector<Float>> C= new Vector<Vector<Float>>(nejemplos);

           
            for(int i=0; i<nejemplos; i++)
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
        		            	//	
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
            	
        	
            }//for 
        
        
        
        
            X= missing.values_missing_test(X, nejemplos, dimx,desconocidos);
    
            
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
				
					 float compa = compatibility.get(j)*poblation.get(j).getregla().getpeso(); //maximo y minimo son iguales, recorrido el 
					 if(compa>comp_regla)
					 {	  
						 comp_regla=compa;
						 regla=j;
					 }
				 }
				 
				 else
					 compatibility.add(j,(float)0.0); 
				
				 
				 
			 }//For 
    		    	   
	        		
	    	 Vector<Float> point=new Vector<Float>();// 
	    	 create_file_columns(regla, C.get(i));
			 if(regla!=-1)
			 {

				 point=puntuation(regla, C.get(i));//point=puntuation(consequent, Y[i]);
				 for(int pun=0;pun<point.size();pun++)
				 {
					
					 if(points.contains(point.get(pun))==false)
						 points.addElement(point.get(pun));
				 }
				 	 
			 }
						
			 else //no rule
	    		{

				 	point=puntuation(-1, Y.get(i));
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


