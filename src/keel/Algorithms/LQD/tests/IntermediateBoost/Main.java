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

package keel.Algorithms.LQD.tests.IntermediateBoost;	

	import java.io.BufferedReader;
	import java.io.File;
	import java.io.FileReader;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.io.InputStreamReader;
	import java.util.Vector;

	/**
	 * 
	 * File: Main.java
	 * 
	 * Read the original dataset and obtain 100 bootstrap from this dataset
	 	 * 
	 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
	 * @version 1.0 
	 */
	public class Main {

		/**
		 * @param args
		 * @throws IOException 
		 */
		public static void main(String[] args) throws IOException {
			// TODO Auto-generated method stub

			parameters par = new parameters(args[0]);
			
			
			InputStreamReader isr= new InputStreamReader(System.in);
	        BufferedReader br = new BufferedReader(isr);
	        String ninput;
	    
	 
	        
	        
	        ninput = par.original_data+".dat";
	        
		        
	   
	        File fe = new File(ninput);
	        if(fe.exists()==false)
	        {
	        	System.out.println("File doesn't exist");
	        	System.exit(0);
	        }
	        
	        BufferedReader input = new BufferedReader(new FileReader(ninput)); 
	        Character caracter;
	        
	       
	        int dimx;        
	        int ncol;        
	        int ninstances;    
	        
	        

	        dimx=Integer.parseInt(input.readLine());        
	        ncol= dimx+1;        
	      
	        ninstances=Integer.parseInt(input.readLine());        
	        
	       
	        int classes=Integer.parseInt(input.readLine());
	        
	        
	        fuzzy X[][] = new fuzzy[ninstances][dimx];
	        Vector<Float> C= new Vector<Float>(ninstances);
	        Vector<fuzzy> P= new Vector<fuzzy>(ninstances);

	        

	     
	       

	        String numero= "";
	        for(int i=0; i<ninstances; i++)
	        {
	        	for(int j=0; j<dimx; j++)
	        	{
	        		
	        		caracter = (char)input.read();
	        		
	        			while(caracter!=' ' && caracter!='\n')
	        			{
	        				numero= numero + caracter;
	        				caracter = (char)input.read();
	        			}
	        			
	        			if(caracter==' ')
	        			{
	        				if(numero.compareTo("-")==0)
	        				{
	        				
	        					fuzzy nuevo= new fuzzy();
	        					nuevo.borrosotriangular(-1, -1, -1);
	        					X[i][j]= nuevo;
	        				}
	        				else
	        				{
	        					X[i][j]=fun_aux.afuzzy(numero);
	        			
	        				}
	        				numero="";
	        				
	        				if(X[i][j].geta()>X[i][j].getd())
	        				{
	        					System.out.println("Incorrect file: Values with this style [4,1]"+X[i][j].geta()+ " "+X[i][j].getd() );
	        					System.exit(0);
	        				}
	        			}
	        			
	        	}
	        	
	        		
	        	
	        	caracter = (char)input.read();//leemos la llave
	        	while(caracter!=' ' && caracter!='\n')
				{
					numero= numero + caracter;
					caracter = (char)input.read();
				}
				
	        	P.add(i,fun_aux.trapezoidal(numero));	
	        	//P.get(i).show();
	        	numero="";
	        	caracter = (char)input.read();
	        

	        	while(caracter!='}')
	    		{
	        		caracter = (char)input.read();
	        		while(caracter!='}')
	        		{
	        			numero= numero + caracter;
	        			caracter = (char)input.read();
	        		}
	        		
	        		C.add(i,Float.parseFloat(numero));
	    			numero="";
	    			
	    			
	    		}
	        	caracter = (char)input.read();


	        	
	        	numero="";

	        	
	        }
	        
	        input.close();
	        
	        
	        
	        for(int cont=0;cont<100;cont++)
	        {
	        	FileWriter t0 = new FileWriter(par.original_data+cont+"train.txt");
	        	FileWriter te0 = new FileWriter(par.original_data+cont+"test.txt");
	        	bootstrap(t0,te0,dimx,ninstances,classes,X,C,P);
	        	t0.close();
	        	te0.close();
	        	
	        }
	        
	        
	       
	   	
		}
		public static void bootstrap(FileWriter fichero,FileWriter ftest, int dimx, int ninstances, int classes,fuzzy[][]X, Vector<Float> C, Vector<fuzzy> P) throws IOException
		{
			
	        fichero.write(dimx+"\n");
	   	 	fichero.write(ninstances+"\n");
	   	 	fichero.write (classes+"\n");
	   	 	
	        Vector<Boolean> introducido = new Vector<Boolean>();
	        for(int i=0; i<ninstances; i++)
	        {
	           introducido.addElement(false);
	        }
	        
	        
	        int pos;
	        for(int i=0; i<ninstances; i++)
	        {
	        	 pos=(int)(0+(float)(Math.random() *ninstances));
	        	 introducido.set(pos, true);
	        	
	        	 for(int j=0;j<X[pos].length;j++)
	        	 {
	        		 if(X[pos][j].geta()==-1 && X[pos][j].getd()==-1 && X[pos][j].getb()==-1)
	        			 fichero.write("- ");
	        		 else if(X[pos][j].getb()==X[pos][j].getc()) //triangular
	        			 fichero.write("["+X[pos][j].geta()+","+X[pos][j].getb()+","+X[pos][j].getd()+"] ");
	        		 else
	        			 fichero.write("["+X[pos][j].geta()+","+X[pos][j].getd()+"] ");
	        			 
	        	 }
	        	
	        	fichero.write("["+P.get(pos).geta()+","+P.get(pos).getb()+","+P.get(pos).getc()+","+P.get(pos).getd()+"] "); 
	        	fichero.write("{"+C.get(pos)+"}\n");
	        		
	        		
	        		
	        	
	        }
	        
	        
	        //los no introducidos van al test
	        ftest.write(dimx+"\n");
	        int no_inser=0;
	        for(int i=0; i<ninstances; i++)
	        {
	           if(introducido.get(i)==false)
	        	   no_inser++;
	        }
	   	 	ftest.write(no_inser+"\n");
	   	 	ftest.write (classes+"\n");
	        for(int i=0; i<ninstances; i++)
	        {
	           if(introducido.get(i)==false)
	           {
	        	   for(int j=0;j<X[i].length;j++)
	        	   {
	        		   if(X[i][j].geta()==-1 && X[i][j].getd()==-1 && X[i][j].getb()==-1)
	            			 ftest.write("- ");
	            		 else if(X[i][j].getb()==X[i][j].getc()) //triangular
	            			 ftest.write("["+X[i][j].geta()+","+X[i][j].getb()+","+X[i][j].getd()+"] ");
	            		 else
	            			 ftest.write("["+X[i][j].geta()+","+X[i][j].getd()+"] ");
	        	   }
	           
	        	   //ftest.write("["+P.get(i).geta()+","+P.get(i).getb()+","+P.get(i).getc()+","+P.get(i).getd()+"] "); 
	        	   ftest.write("{"+C.get(i)+"}\n");
	           }
	           
	        }
	        
			
		}

	}


