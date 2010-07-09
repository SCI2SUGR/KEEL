package keel.Algorithms.LQD.methods.FGFS_costInstances;

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
	//Un individuo Michigan es una regla
	  fuzzy[][] X;
	  Vector<Vector<Float>> Y;  
	  fuzzyRule individuo;
	  Interval fitness;
	 
	  
	  IndMichigan(fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> valores_clases,Vector<Vector<fuzzy>> pesos,int ejemplo, int asigna_peso_regla,
			  String etiqueta,Vector<fuzzy> p, int es_crisp) throws IOException 
	  {
		  individuo= new fuzzyRule(pentradas,classes,asigna_peso_regla);
		  int contador=0;
		  
		  if(es_crisp==0)
		  {
			  if(ejemplo<(x.length))
				  individuo.obtain_rule(x,y,pentradas,classes,COST, alfa,valores_clases,pesos,ejemplo,etiqueta,p);
			  
			  else
			  {
				  while(ejemplo>=x.length)
				  {
					  ejemplo=ejemplo-x.length;
				  }
			  
				  individuo.obtain_rule_random_eje(x,y,pentradas,classes,COST, alfa,valores_clases,pesos,etiqueta,p, ejemplo);
			  }
		  }
		  else
		  {
			  if(ejemplo<(x.length))
				  individuo.obtain_rule(x,y,pentradas,classes,COST, alfa,valores_clases,pesos,ejemplo,etiqueta,p);
			  else
				  individuo.obtain_rule_random(x,y,pentradas,classes,COST, alfa,valores_clases,pesos,etiqueta,p);
		  }
		  
		  X=x;
		  Y=y;
	  }
    
	  //Obtain the new individual (rule) from two parents
	  IndMichigan(Vector<Integer> ant,fuzzy[][] x,Vector<Vector<Float>> y,Vector<fuzzyPartition> pentradas,int classes, int COST, int alfa,
			  Vector<Float> values_classes,Vector<Vector<fuzzy>> pesos,int asign_weight_rule,
			  String label,Vector<fuzzy> p) throws IOException 
	  {

		  individuo= new fuzzyRule(pentradas,classes, asign_weight_rule);
		  Integer[]a= new Integer[ant.size()];
		  for(int i=0;i<ant.size();i++)
		  {
			  a[i]= ant.get(i);
		  }
		  individuo.setantecedent(a);
		  individuo.calculateconsequent(x, y, pentradas, classes,COST,alfa,values_classes,pesos,label,p);

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
