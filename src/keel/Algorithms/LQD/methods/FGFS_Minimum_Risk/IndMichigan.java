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
import java.util.Vector;


/**
 * 
 * File: fuzzy.java
 * 
 * Properties and functions of individual of the population
 * 
 * @author Written by Ana Palacios Jimenez (University of Oviedo) 25/006/2010 
 * @version 1.0 
 */


public class IndMichigan {

	  fuzzy[][] X;
	  Vector<Vector<Float>> Y;  
	  fuzzyRule individuo;
	  Interval fitness;
	 
	  
	  IndMichigan(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> values_classes,Vector<Vector<fuzzy>> pesos,int instance, int asign_weight_rule,
			  String label,Vector<Float> p, int es_crisp) throws IOException 
	  {
		  individuo= new fuzzyRule(pentradas,classes,asign_weight_rule);//initialize the individual
		  int contador=0;
		  
		  if(es_crisp==0)
		  {
			//Obtain the antecedents of the rule and the consequent
			  if(instance<(x.length))
				  individuo.obtain_rule(x,y,pentradas,classes,COST, alfa,values_classes,pesos,instance,label,p);
			  
			  else
			  {
				  while(instance>=x.length)
				  {
					  instance=instance-x.length;
				  }
			  
				  individuo.obtain_rule_random_eje(x,y,pentradas,classes,COST, alfa,values_classes,pesos,label,p, instance);
			  }
		  }
		  else
		  {
			  if(instance<(x.length))
				  individuo.obtain_rule(x,y,pentradas,classes,COST, alfa,values_classes,pesos,instance,label,p);
			  else
				  individuo.obtain_rule_random(x,y,pentradas,classes,COST, alfa,values_classes,pesos,label,p);
		  }
		  X=x;
		  Y=y;
	  }
    
	  //Obtain the new individual (rule) from two parents
	  IndMichigan(Vector<Integer> ant,fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> value_classes,Vector<Vector<fuzzy>> pesos,int asign_weight_rule,
			  String etiqueta,Vector<Float> p) throws IOException 
	  {

		  individuo= new fuzzyRule(pentradas,classes, asign_weight_rule);
		  Integer[]a= new Integer[ant.size()];
		  for(int i=0;i<ant.size();i++)
		  {
			  a[i]= ant.get(i);
		  }
		  individuo.setantecedent(a);
		  
		  individuo.calculateconsequent(x, y, pentradas, classes,COST,alfa,value_classes,pesos,etiqueta,p);

		  X=x;
		  Y=y;

	  }
       
	
	  public fuzzyRule getregla(){return individuo;}
	  public int size() { return individuo.size(); }
	  public Interval getfitness()
	  {
		  return fitness;
	  }
	  public fuzzy[][] getX()
	  {
		  return X;
	  }
	  public Vector<Vector<Float>> getY()
	  {
		  return Y;
	  }
	  public void asignaejemplos(fuzzy[][] x,Vector<Vector<Float>> y) 
	  {
		  X=x;
		  Y=y;
	  }

}

