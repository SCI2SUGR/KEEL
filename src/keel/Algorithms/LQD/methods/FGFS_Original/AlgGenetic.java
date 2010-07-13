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

/**
*
* File: AlgGenetic.java
*
* This genetic algorithm obtains fuzzy rule from
* imprecise or low quality 
*
* @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010
* @version 1.0
*/

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;


public class AlgGenetic {
	
	Vector<IndMichigan>poblation;
    Vector<Vector<Float>> fitness;
    Vector<Float> fitness_total;
    Vector<Boolean> compatible;
    Vector<Float> total;
    float PROBMUTA;
    float PROBCROSS;
    int TAMPO;
    int iterations;
    Interval[][] X;
    Vector<Vector<Float>> Y;
    int classes;
    Vector<partition> partitions;
    int replace;
    int mis;
    
    int alpha;

    FileWriter fs1;
    String fichero_test;
    String fichero_entre;
    Vector<Float> values_classes;
    Vector<Vector<Float>> costs;
    int instances;

    
    int NO_CUBIERTOS;
    int CUBIERTOS_ACIERTOS;
    int CUBIERTOS_FALLOS;
    int CUBIERTOS;
    int type_compatible;
    int reparte;
    
  int pos_classe;
  String file_columns;
  FileWriter column;
	
    public AlgGenetic(int npoblation,  float muta,float cruce,Vector<partition> parte, 
    		int nclasses,Interval[][] x,Vector<Vector<Float>> y, int niterations,
    	 int re, String nombre, int al, String fichero, String entre,Vector<Float> values
    	 ,int ninstances,int des, int tipo_comp, int repar,String columns) throws IOException
    { 
    	
    	NO_CUBIERTOS=0;
    	CUBIERTOS_ACIERTOS=0;
    	CUBIERTOS_FALLOS=0;
    	CUBIERTOS=0;
    	type_compatible=tipo_comp;
    	reparte=repar;
    	
    	X=x;
    	mis=des;
    	fichero_test=fichero;
    	fichero_entre=entre;
    	Y=y;
    	 fs1= new FileWriter(nombre);
    	 file_columns=columns;
    	replace=re;
    	PROBCROSS= cruce;
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
			float valor=0;
			for(int i=0;i<Y.size();i++)
			{
				for(int j=0;j<Y.get(i).size();j++)
				{
					
					if((float)Y.get(i).get(j)==(float)values_classes.get(v))
					{
						
						valor++;
						
					}
				}
			
			}
			
			frequent_classes.add(v, valor);
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
		
			IndMichigan ind = new IndMichigan(x,y,partitions, classes,0,alpha,values_classes,costs,i); 
			poblation.add(i, ind);
		
	
		
		}
		
		
		
		evaluate_poblation();
		
		
		
		show_fitness_total(0);
		

		
		 evolucion(); 
    }
    
    public void evaluate_poblation() throws IOException
    {
    	
    	for (int i=0;i<TAMPO;i++) 
    	{
    		Vector<Float> inicializa= new Vector<Float>();
			inicializa.add((float)0.0);
    		fitness.set(i,inicializa); 
    		
    		compatible.set(i,false);
    	}
    		fitness_total.clear();
    	
    	NO_CUBIERTOS=0;
        CUBIERTOS_ACIERTOS=0;
        CUBIERTOS_FALLOS=0;
        CUBIERTOS=0;
        
    		
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
    			if(existe==false)
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
    			int fallos=0, acierto=0;
    				CUBIERTOS++;
    				for(int c=0;c<points.size();c++)
    				{
    					if(points.get(c)==0 && fallos==0)
    					{
    						CUBIERTOS_FALLOS++;
    						fallos=1;
    						
    					}
    				
    					 if(points.get(c)==1 && acierto==0)
    					{
    						acierto=1;
    						CUBIERTOS_ACIERTOS++;
    					}
    				}
    				
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
    					fitness.set(ganadora,sumatorio(fitness.get(ganadora),points));
    					compatible.set(ganadora, true);
    				}
    			
    				
    		
    		}
    		else 
    		{
    			NO_CUBIERTOS++;
    			
    			point=puntuacion(-1, Y.get(i));//point=puntuacion(consequent, Y[i]);
			
				for(int p=0;p<point.size();p++)
				{
					
					if(points.contains(point.get(p))==false)
						points.addElement(point.get(p));
				}
    			
    			
    			
    		}
    			
    		calculo_fitness_total(points);//el fitness total viene dato en % error (0 y 1)
    	
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
    				//System.out.println(nuevo);
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
    		
    	Integer [] cons=poblation.get(regla).getregla().getconsequent();
		
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
    		Integer [] cons=poblation.get(regla).getregla().getconsequent();
		
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
    public void show_fitness_total(int ultimo) throws IOException
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
        	fs1.write("\n"+minimo+"\n"+maximo+"\n");
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
			 if(Dominance.domine(fitness.get(p1),fitness.get(p2))==1)//si p1 domina a p2 es mejor, mayor valor
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
						int valor=(int) (0+(float)(Math.random()*(partitions.get(i).size()))); 
						hijo1.set(i, valor);//+NOIMPORTA);
					}
				}
				
				for (int i=0;i<hijo2.size();i++) 
				{
					if (0+(float)(Math.random() *1)<PROBMUTA) 
					{

						int valor=(int) (0+(float)(Math.random()*(partitions.get(i).size())));
				//		
						hijo2.set(i, valor);//+NOIMPORTA);
					}
				}
				
			
				
				
		
				pobl_int.addElement(new IndMichigan(hijo1,X,Y,partitions, classes,0,alpha,values_classes,costs));
				pobl_int.addElement(new IndMichigan(hijo2,X,Y,partitions, classes,0,alpha,values_classes,costs));
				
			 }//replace
			   
			
			 
			Vector<Integer>reglas_ordenadas=Dominance.order(fitness);//ordenamos las reglas de la poblation 
			for (int i=0;i<pobl_int.size();i++) 
			{
				
				poblation.set(reglas_ordenadas.get(i),pobl_int.get(i));
			
			}
			//new BufferedReader(new InputStreamReader(System.in)).readLine();
			evaluate_poblation();
			 
			
			System.out.print("."+iter);
			show_fitness_total(0);

			//new BufferedReader(new InputStreamReader(System.in)).readLine();
			   
			   
			  
 
		 } //for  iteration
	
		 

	 	show_reglas(fs1);	
	 	show_fitness();//muestra el fitness de cada regla
	 	
	 	fs1.write("IMPRECISO");
		show_fitness_total(1);
		
		
		
		
		fs1.write("Test");
		test(0);
		fs1.write("Train");
		test(1);
		fs1.close();
				
		
	}
	
	void show_reglas(FileWriter fs1) throws IOException
	{
		for (int i=0;i<poblation.size();i++)
		{
			poblation.get(i).getregla().show(fs1);
		}
		
	}
	void test(int cual) throws IOException
	{
		
		File fe;
		if(cual==0)
		{
			fe = new File(fichero_test);
			column= new FileWriter(file_columns);
			//fs1.write(fichero_test+" ");
		}
		else
		{
			fe = new File(fichero_entre);
			//fs1.write("\n"+fichero_entre+" ");
		}
        if(fe.exists()==false)
        {
        	System.out.println("El archivo no existe");
        	System.exit(0);
        }
        
        BufferedReader input;// = new BufferedReader(new FileReader(fichero_test));
        if(cual==0)
        	input = new BufferedReader(new FileReader(fichero_test));
        else
        	input = new BufferedReader(new FileReader(fichero_entre));
        Character character;
        
       
      
		int dimx=Main.par.dimx;				   //Number of variables        
		int ncol=dimx+1;       		       //Number of columns in the input file        
		int instances=Main.par.instances;       //Number of instances
		int nclasses = Main.par.nclasses;
        
        
        if(Main.par.partitions_data.contains("10cv")==true) //Calculate the numbers of instances
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
        	 if(cual==0)
             	input = new BufferedReader(new FileReader(fichero_test));
             else
             	input = new BufferedReader(new FileReader(fichero_entre));
        }
        
        System.out.println("The numbers of instances is: "+instances);
        
        
        Interval X[][] = new Interval[instances][dimx];
        Vector<Vector<Float>> C= new Vector<Vector<Float>>(instances);

        Interval rangeX[]= new Interval[dimx];
        for(int j=0; j<ncol-1; j++)
    	{
        	Interval nuevo= new Interval(-1,-1);
			rangeX[j]= nuevo;
    	}
       

        String numero= "";
        int lines=1+dimx+4;
        for(int i=0;i<lines;i++)
        	input.readLine();
        //READ FILE
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
        				Interval nuevo= new Interval(Main.MISSING, Main.MISSING);
        				X[i][j]= nuevo;
        			}
        			else
        			{
        				X[i][j]=fun_aux.to_fuzzy(numero);
        				//System.out.println("The value is: "+X[i][j].getmin()+" "+X[i][j].getmax());
        			
        				
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
        X= missing.values_missing(X, instances, dimx,mis);
        
		 asignainstances(X, C);
	  evalua_poblation_test(dimx,cual); 
		 		
		  if(cual==0)
			  column.close();
		 
		show_fitness_total(1); 
		
		 
		 
	}
	public void asignainstances(Interval[][] x,Vector<Vector<Float>> y)
	{
		X=x;
    	Y=y;
	}
	 
	
	 
	 public void evalua_poblation_test(int dimx, int is_test) throws IOException
	    {
	    
	    	for (int i=0;i<TAMPO;i++) 
	    	{
	    		Vector<Float> inicializa= new Vector<Float>();
				inicializa.add((float)0.0);
	    		fitness.set(i,inicializa); 
	    	}
	    	
	    		fitness_total.clear();
	    		NO_CUBIERTOS=0;
	    		CUBIERTOS_ACIERTOS=0;
	    		CUBIERTOS_FALLOS=0;
	    		CUBIERTOS=0;
	    		
	    	for (int i=0;i<X.length;i++) 
			{
	    		
	    		
	    		System.out.println("Instance "+i);
	    		if(is_test==0)
	    		column.write(i+"\n");
	    		int cubierto=0;
	    		Vector<Float> points = new Vector<Float>(); 
	    	    int sale=0;
	    	    
	    	    if(fichero_test.contains("lon"))
	    	    {
	    	    	for(float t=X[i][0].getmin();t<=X[i][0].getmax() && sale==0;t=(float) (t+1))
	    	    	{
	    	    		for(float v=X[i][1].getmin();v<=X[i][1].getmax()&& sale==0;v=(float) (v+5))
	    	    		{
	    	    			for(float p=X[i][2].getmin();p<=X[i][2].getmax()&& sale==0;p=(float) (p+0.1))
	    	    			{
	    	    				for(float q=X[i][3].getmin();q<=X[i][3].getmax()&& sale==0;q=(float) (q+0.2))
	    	    				{
	        					 
	    	    					Interval point_point[]= new Interval[dimx];
	    	    					point_point[0]=new Interval(t,t);
	    	    					point_point[1]=new Interval(v,v);
	    	    					point_point[2]=new Interval(p,p);
	    	    					point_point[3]=new Interval(q,q);
	    	    					
	    	    					Interval Z[][] = new Interval[1][dimx];
	    	    					Z[0]=point_point;
	        			 
	        				
	        				
	    	    					Vector<Interval> compatibility = new Vector<Interval>(fitness.size());//guarda la compatibility de la reglas con el ejemplo actual
	        				
	    		
	    	    					int regla=-1;
	    	    					for(int j=0; j<TAMPO; j++)
	    	    					{
	    	    						if(compatible.get(j)==true)
	    	    						{
	    	    							regla=j;
	    	    							break;
	    	    						}
	    	    					}
	    	    					
	    	    					float comp_regla=0;
	    	    					for(int j=0; j<TAMPO; j++)
	    	    					{

	    	    						if(compatible.get(j)==true)
	    	    						{
	    	    							compatibility.add(j, poblation.get(j).getregla().match_alpha(Z[0],partitions, alpha));
	    	    							float compa = compatibility.get(j).getmax();  
	    	    							if(compa>comp_regla)
	    	    							{	  
	        								 comp_regla=compa;
	        								 regla=j;
	    	    							}
	    	    						}
	    	    						else
	    	    						{
	    	    							compatibility.add(j,new Interval(0,0)); 
	    	    						}
	        					 
	        					 
	    	    					}//For 
	    		    		    	
	        				 
	    	    					Vector<Float> point=new Vector<Float>();
	    	    					if(is_test==0)
	    	    					create_file_columns(regla, Y.get(i));
	    	    					if(regla!=-1)
	    	    					{
	    	    						cubierto=1;
	    	    						point=puntuacion(regla, Y.get(i));
	    	    						for(int pun=0;pun<point.size();pun++)
	    	    						{
	    	    							if(points.contains(point.get(pun))==false)
	    	    								points.addElement(point.get(pun));
	    	    						}
    							 
    							 
	    	    						if(points.contains(1) && points.contains(0))
	    	    							sale=1;
	        					 
	    	    					}
	    	    					else
	    	    					{
	    	    						point=puntuacion(-1, Y.get(i));
	    	    						for(int pu=0;pu<point.size();pu++)
	    	    						{
	        							
	    	    							if(points.contains(point.get(pu))==false)
	    	    								points.addElement(point.get(pu));
	    	    						}
	    	    					}
	        						
	        			 
	    	    				}
	    	    			}
	    	    		}

	    	    	}
	    	    } 
	    	    else if(fichero_test.contains("100mlP"))
	    		   {
	    	    	
	    	    for(float t=X[i][0].getmin();t<=X[i][0].getmax() && sale==0;t=(float) (t+1))
	        	 {
	        		 for(float v=X[i][1].getmin();v<=X[i][1].getmax()&& sale==0;v=(float) (v+1))
	        		 {
	        			for(float p=X[i][2].getmin();p<=X[i][2].getmax()&& sale==0;p=(float) (p+1))
		        		 {
	        			 
	        				 for(float q=X[i][3].getmin();q<=X[i][3].getmax()&& sale==0;q=(float) (q+1))
			        		 {
	        					
	        				 Interval point_point[]= new Interval[dimx];
	        				 point_point[0]=new Interval(t,t);
	        				 point_point[1]=new Interval(v,v);
	        				 point_point[2]=new Interval(p,p);
	        				 point_point[3]=new Interval(q,q);
	        				
	        				 Interval Z[][] = new Interval[1][dimx];
	        				 Z[0]=point_point;
	        			 
	        				
	    		
	        				 Vector<Interval> compatibility = new Vector<Interval>(fitness.size());
	        				
	    		
	        				 int regla=-1;
	        				 for(int j=0; j<TAMPO; j++)
	        				 {
	         					 if(compatible.get(j)==true)
	         					 {
	         					   regla=j;
	         					   break;
	         					 }
	        				 }
	        				 float comp_regla=0;
	        				 for(int j=0; j<TAMPO; j++)
	        				 {

	        					 if(compatible.get(j)==true)
	        					 {
	        						 compatibility.add(j, poblation.get(j).getregla().match_alpha(Z[0],partitions, alpha));
	        						 float compa = compatibility.get(j).getmax();  
	        						 if(compa>comp_regla)
	        						 {	  
	        							 comp_regla=compa;
	        							 regla=j;
	        						 }
	        					 }
	        					 else
	        					 {
	        						 compatibility.add(j,new Interval(0,0)); 
	        					 }
	        					 
	        					 
	        				 }
	    		    		    	
	        				 
	        				 Vector<Float> point=new Vector<Float>();
	        				 if(is_test==0)
	        				 create_file_columns(regla, Y.get(i));
	        				 if(regla!=-1)
	        				 {
	        					 cubierto=1;
	        					 point=puntuacion(regla, Y.get(i));
 							 for(int pun=0;pun<point.size();pun++)
 							 {
 								 if(points.contains(point.get(pun))==false)
 									 points.addElement(point.get(pun));
 							 }
 							 
 							 
 							 if(points.contains(1) && points.contains(0))
 								 sale=1;
	        					 
	        				 }
	        				 else
	        				 {
	        					 point=puntuacion(-1, Y.get(i));
	        						
	        						for(int pu=0;pu<point.size();pu++)
	        						{
	        							
	        							if(points.contains(point.get(pu))==false)
	        								points.addElement(point.get(pu));
	        						}
	        		    	 }
	        						
	        			 
	        			      	}
			        		 	}
		        	

	        		 }	

	        	 } 
	    		   }
	        		        			
	    	    else if(fichero_test.contains("100mlI"))
	    		   {
	    	    	
	    	    for(float t=X[i][0].getmin();t<=X[i][0].getmax() && sale==0;t=(float) (t+0.5))
	        	 {
	        		 for(float v=X[i][1].getmin();v<=X[i][1].getmax()&& sale==0;v=(float) (v+0.05))
	        		 {
	        			for(float p=X[i][2].getmin();p<=X[i][2].getmax()&& sale==0;p=(float) (p+0.05))
		        		 {
	        			 
	        				 for(float q=X[i][3].getmin();q<=X[i][3].getmax()&& sale==0;q=(float) (q+0.05))
			        		 {
	        					
	        				 Interval point_point[]= new Interval[dimx];
	        				 point_point[0]=new Interval(t,t);
	        				 point_point[1]=new Interval(v,v);
	        				 point_point[2]=new Interval(p,p);
	        				 point_point[3]=new Interval(q,q);
	        				
	        				 Interval Z[][] = new Interval[1][dimx];
	        				 Z[0]=point_point;
	        			 
	        				
	        				
	        				 Vector<Interval> compatibility = new Vector<Interval>(fitness.size());
	        				
	    		
	        			
	        				 int regla=-1;
	        				 for(int j=0; j<TAMPO; j++)
	        				 {
	         					 if(compatible.get(j)==true)
	         					 {
	         					   regla=j;
	         					   break;
	         					 }
	        				 }
	        				
	        				 float comp_regla=0;
	        				 for(int j=0; j<TAMPO; j++)
	        				 {

	        				
	        					 if(compatible.get(j)==true)
	        					 {
	        						 compatibility.add(j, poblation.get(j).getregla().match_alpha(Z[0],partitions, alpha));
	        						
	        						 float compa = compatibility.get(j).getmax();  
	        						 if(compa>comp_regla)
	        						 {	  
	        							 comp_regla=compa;
	        							 regla=j;
	        						 }
	        					 }
	        					 else
	        					 {
	        						 compatibility.add(j,new Interval(0,0));
	        					 }
	        					 
	        					 
	        				 }
	    		    		    	
	        				 
	        				 Vector<Float> point=new Vector<Float>();
	        				 if(is_test==0)
	        				 create_file_columns(regla, Y.get(i));
	        				 if(regla!=-1)
	        				 {
	        					 cubierto=1;
	        					 point=puntuacion(regla, Y.get(i));
							 for(int pun=0;pun<point.size();pun++)
							 {
								 if(points.contains(point.get(pun))==false)
									 points.addElement(point.get(pun));
							 }
							 
							 
							 if(points.contains(1) && points.contains(0))
								 sale=1;
	        					 
	        				 }
	        				 else
	        				 {
	        					 point=puntuacion(-1, Y.get(i));
	        						for(int pu=0;pu<point.size();pu++)
	        						{
	        							
	        							if(points.contains(point.get(pu))==false)
	        								points.addElement(point.get(pu));
	        						}
	        		    	 }
	        						
	        			 
	        			      	}
			        		 	}
		        	

	        		 }	

	        	 }
	    		   }
	    		 	
	    		
	    		 if(cubierto==1)
	    		 {
	    			 int fallos=0;
	    			 int acierto=0;
	    			 CUBIERTOS++;
	    			 for(int c=0;c<points.size();c++)
	    				{
	    					if(points.get(c)==0 && fallos==0)
	    					{
	    						CUBIERTOS_FALLOS++;
	    						fallos=1;
	    						
	    					}
	    				
	    					 if(points.get(c)==1 && acierto==0)
	    					{
	    						acierto=1;
	    						CUBIERTOS_ACIERTOS++;
	    					}
	    				}
	    			
	    		 }
	    		 else
	    			 NO_CUBIERTOS++;

	    	    	calculo_fitness_total(points);
	    		 
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
			 Integer [] cons=poblation.get(regla).getregla().getconsequent();
			
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
	 
	
	 
	 
}


