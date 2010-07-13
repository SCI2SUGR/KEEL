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

package keel.Algorithms.LQD.preprocess.Fuzzy_SMOTE;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/**
 *
 * File: Main.java
 *
 * Read the parameters given by the user.
 * Read the training file
 * Analysis the classes to apply the preprocessing method. This
 * method is based in SMOTE but now the inputs have meta-information
 * and the outputs are imprecise (set of values). M and N can be obtained
 * with the preprocessing method or can be indicate by the user
 * Replace the missing values for the mean
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
		
		int cont=0; /*100 ficheros bootstrap*/
		while(cont<par.files)
		{
			//copy the test file
			String ntest=par.original_data+cont+"tst.dat";//
			FileWriter ftest= new FileWriter(par.OutputName+cont+"tst.dat");
			BufferedReader test = new BufferedReader(new FileReader(ntest));
			while(test.ready())
				ftest.write(test.readLine()+"\n");
			
			ftest.close();
			test.close();
	        
			
			//Files
			String ninput=par.original_data+cont+"tra.dat";//
	        System.out.println("\n Input File: "+ninput);
			FileWriter fs1= new FileWriter(par.OutputName+cont+"tra.dat");
			FileWriter inst= new FileWriter(par.OutputName+"Instances"+cont+".txt");
			
			
			
			
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
        
             
        
			//save the variables of each example
			Vector <Vector<fuzzy>> X = new Vector <Vector<fuzzy>>();
			Vector<Vector<Float>> C= new Vector<Vector<Float>>();//the output will be a set of elements
			Vector<Vector<Float>> count_classes = new Vector<Vector<Float>>();
		

			String number= "";
	        //Read the input file
	        int lines=1+dimx+4;
	        for(int i=0;i<lines;i++)
	        	fs1.write(input.readLine()+"\n");    
	        
			
	       // boolean leido=false;
	        for(int i=0; i<instances; i++)
	        {
				Vector<fuzzy> atributos =new Vector<fuzzy>(); 
				for(int j=0; j<ncol-1; j++)
	        	{
	        		//if(leido==false)
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
	        				atributos.addElement(nuevo);
	        			}
	        			else
	        			{
        					atributos.addElement(fun_aux.to_fuzzy(number));
        				}
        				number="";        				
        				if(atributos.get(j).geta()>atributos.get(j).getd())
        				{
        					System.out.println("Incorrect values in the file: Values of the style [4,1]"+atributos.get(j).geta()+ " "+atributos.get(j).getd());
        					System.exit(0);
        				}
        			}
				} //for the variables
				X.addElement(atributos);
        		
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
				if(salidas_imp.size()==1)
				{
					//System.out.println("the class is "+C.get(i).get(0));
					if(count_classes.size()==0)
					{
						Vector<Float> first = new Vector<Float>();
						first.addElement(C.get(i).get(0));
						first.addElement((float)1);
						count_classes.addElement(first);
					}
					else
					{
						boolean existe=false;
						for(int cnt=0;cnt<count_classes.size();cnt++)
						{
							if(count_classes.get(cnt).get(0).compareTo(C.get(i).get(0))==0)
							{
								existe=true;
								Vector<Float> cambio = count_classes.get(cnt);
								cambio.set(1,cambio.get(1)+1);
								count_classes.set(cnt, cambio);
							}
						}
						if(existe==false)
						{
							Vector<Float> first = new Vector<Float>();
							first.addElement(C.get(i).get(0));
							first.addElement((float)1);
							count_classes.addElement(first);
						}
					}
				}//if the count of classes
				
				
				character = (char)input.read();//\n after }
            	
				number="";
			}//for read file
        
        
	        input.close();
      
            //The variables unknown will be replaced by the average 
			X= missing.values_missing(X, instances, dimx,1);
       
		
			//Read the minority classes
			Vector<Float> minority= new Vector<Float>();
			Vector<Integer> N= new Vector<Integer>();
			

			//Sort the classes according to the number of ocurrences
			Vector<Float> temporal= new Vector<Float>();
			for(int cnt=0;cnt<count_classes.size();cnt++)
			{
				for(int j=0;j<count_classes.size();j++)
				{
					if(count_classes.get(j).get(1)>count_classes.get(cnt).get(1))//es decir si j<i 
					{
						temporal = count_classes.get(cnt);
						count_classes.set(cnt,count_classes.get(j));
						count_classes.set(j,temporal);
					}
				}
			}
		
			
			if(par.N.compareTo("[-1]")==0) //we look for the minority class
			{
				for(int cnt=0;cnt<count_classes.size();cnt++)
				{
					minority.addElement(count_classes.get(cnt).get(0));
				}
			}
			else //the expert indicates the minority classes [0,1,2]
			{
				String contenido = par.N.substring(1, par.N.length()-1);
				int inicio=0;
				int posicion = contenido.indexOf(",");
				while(posicion!=-1)
				{
					minority.addElement(Float.parseFloat(contenido.substring(inicio,posicion).toString()));
					inicio=posicion+1;
					posicion = contenido.indexOf(",",posicion+1);
				}
				minority.addElement(Float.parseFloat(contenido.substring(inicio,contenido.length()).toString()));
			}
			
			
		
			if(par.M.compareTo("[-1]")==0) //we look for number of the new instances 
			{
				for(int cnt=0;cnt<count_classes.size();cnt++)
				{
					if(cnt==count_classes.size()-1)
						N.addElement(1);
					else
					{
						float valor=count_classes.get(count_classes.size()-1).get(1)/count_classes.get(cnt).get(1);
						N.addElement((int)(valor+1));
					}
				}
				
			}
			else //the expert indicates the numbers of new instances 
			{
				String contenido = par.M.substring(1, par.M.length()-1);
				
				int inicio=0;
				int posicion = contenido.indexOf(",");
				while(posicion!=-1)
				{
					N.addElement(Integer.parseInt(contenido.substring(inicio,posicion).toString()));
					inicio=posicion+1;
					posicion = contenido.indexOf(",",posicion+1);				
				}
				N.addElement(Integer.parseInt(contenido.substring(inicio,contenido.length()).toString()));
				
			}
			
			
			
			//Read the k neighbour
			int k = par.k;
			
			
			Vector<Vector<fuzzy>> M= new Vector<Vector<fuzzy>>();
			
			for(int i=0;i<minority.size();i++)
			{
				M.clear();
				for(int e=0;e<X.size();e++)
				{
				
					for(int classes=0;classes<C.get(e).size();classes++)
					{
						if(C.get(e).get(classes).compareTo(minority.get(i))==0)
						{
							M.addElement(X.get(e));
						}
					}
				}
				
				//For each minority class we obtain N more
				for(int min=0;min<M.size();min++)
				{
					//Calculate the k neighbour to each minority instance				
					//Calculate the distance between this instances and the rest of instances of M
					Vector<Integer> distancias_vecinos = distance(M,min);
					for(int replicas=0;replicas<N.get(i);replicas++)
					{
						//Select one k between 0 y k-1
						int aleatorio=(int)(0+(float)Math.random()*k);
						int elegido = distancias_vecinos.get(aleatorio);
				
						//Create the new instances from the actual (M) and k
						Vector<fuzzy> sintetico = new Vector<fuzzy>();
						for(int atri=0;atri<dimx;atri++)
						{
						   fuzzy dif= fuzzy.resta(M.get(elegido).get(atri), M.get(min).get(atri));
						   //dif.show();
						   float gap =(float)Math.random()*1;
						   //System.out.println("  gap is "+gap);
						  // borroso.multinumber(gap, dif).show();
						   fuzzy atributo =fuzzy.suma(M.get(min).get(atri),(fuzzy.multinumber(gap, dif)));
						   sintetico.addElement(fuzzy.neg(atributo));
						   //borroso.suma(M.get(min).get(atri),(borroso.multinumber(gap, dif))).show();
						   //new BufferedReader(new InputStreamReader(System.in)).readLine();
						}
						Vector<Float> clas_mino = new Vector<Float>();
						clas_mino.addElement(minority.get(i));	
						//	Add the new instances a X
						X.addElement(sintetico);
						C.addElement(clas_mino);
						//System.out.println(" after inserting a  new instances ");
						/*for(int s=0;s<X.size();s++)
						{
							for(int j=0;j<X.get(s).size();j++)
							{
								X.get(s).get(j).show();
							}
							for(int classes=0;classes<C.get(s).size();classes++)
							{
								System.out.println(" classes "+C.get(s).get(classes));
							}
						}
						new BufferedReader(new InputStreamReader(System.in)).readLine();*/
					}
				}

			}//for the all minority classes
			
			
			//copy the new file
			inst.write(dimx+"\n");
			inst.write(X.size()+"\n");
			inst.write(nclasses+"");
			
			for(int e=0;e<X.size();e++)
			{
				for(int a=0;a<dimx;a++)
				{
					fs1.write(fuzzy.fichero(X.get(e).get(a))+",");
				}
				fs1.write("{");
				for(int classes=0;classes<C.get(e).size();classes++)
				{
					if(classes!=0)
						fs1.write(",");
					fs1.write(C.get(e).get(classes)+"");
				}
				fs1.write("}\n");
				
			}
			
			fs1.close();
			inst.close();
			cont++;
		}
	}
	
	public static Vector<Integer> distance(Vector<Vector<fuzzy>> M, int i) throws IOException
	{
		Vector<fuzzy> distance = new Vector <fuzzy>();
		Vector<Integer> instance = new Vector <Integer>();
		for(int min=0;min<M.size();min++)
		{
			
			if(min!=i)
			{
				fuzzy sumatorio= new fuzzy(0);
				for(int a=0;a<M.get(min).size();a++)
				{
					fuzzy resta = fuzzy.resta(M.get(i).get(a),M.get(min).get(a)); 
					sumatorio = fuzzy.suma(sumatorio, fuzzy.pow(fuzzy.abs(resta), 2));
					
				}
				sumatorio = fuzzy.pow(sumatorio,(float)0.5);
				distance.addElement(sumatorio);
				instance.addElement(min);
			}
			//new BufferedReader(new InputStreamReader(System.in)).readLine();
		}
	
		
		//Sort the distances
		fuzzy temporal = new fuzzy();
		int eje;
		for(int dis=0;dis<distance.size();dis++) 
		{
			for (int j = dis+1; j < distance.size(); j++) 
			{
				if(Ranking.wang(distance.get(dis),distance.get(j))==0) 
				{
					temporal = distance.get(dis);
					distance.set(dis,distance.get(j));
					distance.set(j,temporal);
					
					eje= instance.get(dis);
					instance.set(dis, instance.get(j));
					instance.set(j, eje);
				
				}
				//new BufferedReader(new InputStreamReader(System.in)).readLine();
			}
		}
		
		
		return instance;
	}

	
	
}
