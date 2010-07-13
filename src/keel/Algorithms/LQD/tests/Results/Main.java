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

package keel.Algorithms.LQD.tests.Results;

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
* From 100 bootstrap we obtain the minimum
 * and maximum mean (for both train and test).
 * Also we calculate the mean of the
 * 100 bootstrap when we apply the cost matrix
 * in the learning 
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
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		//Read the parameters of the method. This parameters are in one .txt
		String parameters = args[0];
		parameters par = new parameters(args[0]);
		
        
 
        Vector<Float> train_min= new Vector<Float>();
        Vector<Float> train_max= new Vector<Float>();
        Vector<Float> test_min= new Vector<Float>();
        Vector<Float> test_max= new Vector<Float>();
        Vector<Float> test_minerror= new Vector<Float>();
        Vector<Float> test_maxerror= new Vector<Float>();

        Vector<Vector<Float>> confusion = new Vector<Vector<Float>>();
        Vector<Vector<Float>> matrizconfusion = new Vector<Vector<Float>>();
        
        
        Boolean entro=false;
        int ejemplos=0;
        for(int i=0;i<par.files;i++)
        {
        	train_min.add((float)0.0);
        	train_max.add((float)0.0);
        	test_min.add((float)0.0);
        	test_max.add((float)0.0);
        	test_minerror.add((float)0.0);
        	test_maxerror.add((float)0.0);
        	        
        }
        
      //leemos los 100 ficheros de pruebas
        String type="";
        for(int contador=0; contador<par.files; contador++)
        {
        	int file=contador;
        
        	String ninput="";
        	if(par.original_data.contains("10cv")==true)
        	{
        		file++;
        		ninput=par.original_data+"-"+file+".dat";//
        	}
        	else if(par.nameAlgorithm.contains("FGFS_Penalty_rule")==true
        			|| par.nameAlgorithm.contains("FGFS_Rule_Weigh")==true)
        	{
        		ninput=par.original_data+par.nameAlgorithm+"R_"+par.rule+"-"+file+".dat";//
        		//ninput=par.original_data+par.nameAlgorithm+"R_1"+"-"+file+".dat";//
        	}
        	else
        	{
        		if(par.minimum_risk==0)
        			ninput=par.original_data+par.nameAlgorithm+"E_"+par.type_risk+file+".dat";//
        		else if(par.minimum_risk==1)
        			ninput=par.original_data+par.nameAlgorithm+"R_"+par.type_risk+file+".dat";//
        	}
    		System.out.println("\n Input File: "+ninput);
        	
            BufferedReader input = new BufferedReader(new FileReader(ninput));
        	
            String line=input.readLine();
           
            int fin=0;
            while(fin==0)
            {
            	line=input.readLine();
            	System.out.print(".");
            	//new BufferedReader(new InputStreamReader(System.in)).readLine();
            	if(line.contains("IMP"))
            		fin=1;
            }
                     
             if(line.contains("IMPRECISO"))
            {
            	train_min.set(contador,Float.parseFloat(input.readLine()));// Read the minimum of train 
            	train_max.set(contador,Float.parseFloat(input.readLine()));// Read the maximum of train 
            	
            	input.readLine();//read test cost
            	test_min.set(contador,Float.parseFloat(input.readLine()));// Read the minimum of test
            	test_max.set(contador,Float.parseFloat(input.readLine()));// Read the maximum of test
            	
            	
            	if(par.nameAlgorithm.contains("FGFS_Penalty_rule")==false
            			&& par.nameAlgorithm.contains("FGFS_Rule_Weigh")==false)
            	{
            		type=input.readLine();
            		if(type!=null);//red test error or train exh if this is 10cv
            		{
            			test_minerror.set(contador,Float.parseFloat(input.readLine()));// Read the minimum of test
            			test_maxerror.set(contador,Float.parseFloat(input.readLine()));// Read the maximum of test
            		}
            	
            	
            	if(input.readLine()!=null)//Read the confusion matrix
            	{
            		String complete = input.readLine();
            		int variables=0;
            		int position=0;
            		int espacio=0;
            		confusion.clear();
            		Vector<Float> contenido = new Vector<Float>();
            		position=complete.indexOf(" ",espacio+1);
            		while (position!=-1)
            		{
            		
            			espacio = complete.indexOf(" ",position+1);
            			String numero=(complete.substring((position+1),espacio).toString());
            			contenido.addElement(Float.parseFloat(numero));
            			variables++;
            			position=complete.indexOf(" ",espacio+1);
            		}
            		//System.out.println("variables  +"+variables);
            		//new BufferedReader(new InputStreamReader(System.in)).readLine();
            		confusion.addElement(contenido);
            		for(int v=0;v<variables;v++)
            		{
            			complete = input.readLine();
            			position=0;
            			Vector<Float> contenido2 = new Vector<Float>();
            			espacio=0;
            			position=complete.indexOf(" ",espacio+1);
            			while (position!=-1)
            			{
            				espacio = complete.indexOf(" ",position+1);
            				String numero=(complete.substring((position+1),espacio).toString());
            				contenido2.addElement(Float.parseFloat(numero));
            				position=complete.indexOf(" ",espacio+1);
            				//new BufferedReader(new InputStreamReader(System.in)).readLine();
            			}
            			confusion.addElement(contenido2);
            		}
            	
            	
            		if(contador==0)
            		{
            			for(int c=0;c<confusion.size();c++)
            			{
            				Vector <Float> sumatorio =new Vector <Float>();
            				for(int co=0;co<confusion.get(c).size();co++)
            				{
            					sumatorio.addElement(confusion.get(c).get(co));
                			
            				}
            				matrizconfusion.addElement(sumatorio);
            			}
            		}
            	
            		else
            		{
            			for(int c=0;c<confusion.size();c++)
            			{
            				Vector <Float> sumatorio =new Vector <Float>();
            				for(int co=0;co<confusion.get(c).size();co++)
            				{
            					sumatorio.addElement(matrizconfusion.get(c).get(co)+confusion.get(c).get(co));
            				}
            				matrizconfusion.set(c,sumatorio);
            			}
            		}
                 
                
            	 //new BufferedReader(new InputStreamReader(System.in)).readLine();
            	}
            	}
            }
        	
        	
        	
        }
        
        //FileWriter excel = new FileWriter(par.excel);
        FileWriter excel = new FileWriter("results.xls",true);
        excel.write("Nama Algoritm"+"\t"+"dataset"+"\t"+"partitions"+"\t"+"Minimum risl"+"\t"+"Type risk"+"\t"+"Type rule"+"\n");
        excel.write(par.nameAlgorithm+"\t"+par.original_data+"\t"+par.partitions+"\t"+par.minimum_risk+"\t"+par.type_risk+"\t"+par.rule+"\n");
        
        
        //Recorremos los vectores e insertamos en un fichero indicando tambien la media de cada uno
        FileWriter fs1 ;
        FileWriter fs2;
        if(type.compareTo("Train")==0 || par.nameAlgorithm.contains("FGFS_Penalty_rule")==true
        		|| par.nameAlgorithm.contains("FGFS_Rule_Weigh")==true)
        {
        	fs1 = new FileWriter(par.OutputName+"Test_exh.txt");
            fs2 = new FileWriter(par.OutputName+"Train_exh.txt");	
        }
        else
        {
        	fs1 = new FileWriter(par.OutputName+"Risk.txt");
            fs2 = new FileWriter(par.OutputName+"Error.txt");
        }
        
        if(entro==true)
        	fs1.write(ejemplos+"\n");
        float media_train_min=0;
        float media_train_max=0;
        float media_test_min=0;
        float media_test_max=0;
        float media_test_minerror=0;
        float media_test_maxerror=0;
        //float media_ta_min=0;
        //float media_ta_max=0;
        
        
        
        
        fs1.write("Train_min \n");
        fs2.write("Train_min \n");
        
        for(int i=0;i<par.files;i++)
        {
           media_train_min=media_train_min+train_min.get(i);
           fs1.write(train_min.get(i)+" ");
           fs2.write(train_min.get(i)+" ");
           
           System.out.println("train_min "+i);
        }
        fs1.write((media_train_min/par.files)+"\n");
        fs2.write((media_train_min/par.files)+"\n");
        
        
        fs1.write("Train_max \n");
        fs2.write("Train_max \n");
        for(int i=0;i<par.files;i++)
        {
           media_train_max=media_train_max+train_max.get(i);
           fs1.write(train_max.get(i)+" ");
           fs2.write(train_max.get(i)+" ");
         
           System.out.println("train_max "+i);
        }
        fs1.write((media_train_max/par.files)+"\n");
        fs2.write((media_train_max/par.files)+"\n");
        excel.write("Train\t ["+(media_train_min/par.files)+", "+(media_train_max/par.files)+"]\n");
        
       
        	fs1.write("Test_min \n");
        for(int i=0;i<par.files;i++)
        {
           media_test_min=media_test_min+test_min.get(i);
           fs1.write(test_min.get(i)+" ");
           System.out.println("test_min " +i);
        }
        fs1.write((media_test_min/par.files)+"\n");
        
        
        	fs1.write("Test_max \n");
        for(int i=0;i<par.files;i++)
        {
        	media_test_max=media_test_max+test_max.get(i);
            fs1.write(test_max.get(i)+" ");
            System.out.println("test_max "+i);
        }
        fs1.write((media_test_max/par.files)+"\n");
        excel.write("Test (Risk si costes) o test error \t["+(media_test_min/par.files)+", "+(media_test_max/par.files)+"]\n");
        
        
        
        if(type.compareTo("Train")==0)
        	fs2.write("Train_exh_min \n");
        else
        	fs2.write("Test_min \n");
        for(int i=0;i<par.files;i++)
        {
           media_test_minerror=media_test_minerror+test_minerror.get(i);
           fs2.write(test_minerror.get(i)+" ");
           System.out.println("test_min " +i);
        }
        fs2.write((media_test_minerror/par.files)+"\n");
        
        if(type.compareTo("Train")==0)
        	fs2.write("Train_exh_max \n");
        else
        	fs2.write("Test_max \n");
        for(int i=0;i<par.files;i++)
        {
        	media_test_maxerror=media_test_maxerror+test_maxerror.get(i);
            fs2.write(test_maxerror.get(i)+" ");
            System.out.println("test_max "+i);
        }
        fs2.write((media_test_maxerror/par.files)+"\n");
        excel.write("Test (error if cost) or train exh \t ["+(media_test_minerror/par.files)+", "+(media_test_maxerror/par.files)+"]\n");
        

      
        	for(int c=0;c<confusion.size();c++)
        	{
        		fs2.write("\n");
        		for(int co=0;co<confusion.get(c).size();co++)
        		{
        			fs2.write(" ["+c+","+co+"]= "+matrizconfusion.get(c).get(co)/par.files);
        			
        		}
        	
        	}
        	
        
        
        fs1.close();
        fs2.close();
        excel.write("\n");
        excel.close();

	}

}

