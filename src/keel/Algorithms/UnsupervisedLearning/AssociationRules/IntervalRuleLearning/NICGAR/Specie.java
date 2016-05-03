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
 
package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.NICGAR;
/**
 * <p>
 * @author Written by Diana Martín (dmartin@ceis.cujae.edu.cu)
 * @version 1.1
 * @since JDK1.7
 * </p>
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.core.Randomize;

public class Specie {

	ArrayList<Chromosome> chrList;
	boolean children; // if have chr from child pop
	int countParents;
	Chromosome bestChr;
	
	public Specie() {
		super();
		this.chrList = new ArrayList<>();
		this.children = false;
		this.countParents = 0;
	}
	
	/* public Chromosome getBestChr(){
	  	Collections.sort(this.chrList);
	  	return this.chrList.get(0);
	 }*/
	 
	public Chromosome getBestChr(){
	    
		return this.bestChr;
	 }
	
	 public void findBestChr(){
		Collections.sort(this.chrList);
		int i= 0;
		boolean found = false;
        
		
		this.bestChr = this.chrList.get(0).copy();

		for(i=1; i < this.chrList.size() && !found; i++){
		  if(this.bestChr.getFitness() != this.chrList.get(i).getFitness())
			 found = true;
		  else{ //tienen fitness iguales
			  
			  if(this.bestChr.getCF() < this.chrList.get(i).getCF()){
				  this.bestChr = this.chrList.get(i).copy();
			  }
			  else{
				  if (this.bestChr.getCF() == this.chrList.get(i).getCF()){
					  if(this.bestChr.getSupport() < this.chrList.get(i).getSupport()){
						  this.bestChr = this.chrList.get(i).copy();
					  }
					  else{ // si el soporte es igual se selecciona uno de los dos aleatorio
						  if(this.bestChr.getSupport() == this.chrList.get(i).getSupport())
							  if(Randomize.RandintClosed(0, 1) == 0)
								  this.bestChr = this.chrList.get(i).copy(); 
					  }
				  }
			  }
		  }
			
		
		}
		
		

	 }

	  public int getCountParents() {
		return countParents;
	}

	public void setCountParents(int countParents) {
		this.countParents = countParents;
	}
	
	public void incrementCountParents(){
		this.countParents++;
	}

	public Chromosome getSeedSpecie(){
		  
		/*  ArrayList<Chromosome> chrEqualsFitness;
		  Collections.sort(niche);
		  boolean found = false;
		  int i,j, posCloseChr;
		  double distChrNiche, distMinChrNiche = Double.MAX_VALUE;
		  
		  if(niche.size() > 1){
			  chrEqualsFitness = new ArrayList<>();
			  chrEqualsFitness.add(niche.get(0));
			  
			  for(i=1; i < niche.size() && !found; i++){
				  if(niche.get(0).getFitness() != niche.get(i).getFitness())
					  found = true;
				  else{
					chrEqualsFitness.add(niche.get(i));
				  }
			  }
			  
			   posCloseChr = 0;
			   for(j = 1; j <chrEqualsFitness.size(); j++){
				   distChrNiche = this.distanceChrNiche(chrEqualsFitness, chrEqualsFitness.get(j));
				   if(distChrNiche < this.share){
					   if(distChrNiche < distMinChrNiche){
						   distMinChrNiche = distChrNiche;
						   posCloseChr = j;
					   }
				   }
			   }
			   
			   return chrEqualsFitness.get(posCloseChr);
			  
			  
		  }
		  else*/ return this.chrList.get(0);
	  }

	public ArrayList<Chromosome> getChrList() {
		return chrList;
	}

	public void setChrListt(ArrayList<Chromosome> chrList) {
		this.chrList = chrList;
	}

	public boolean containsChildren() {
		return children;
	}

	public void setChildren(boolean children) {
		this.children = children;
	}
	
	public ArrayList<Chromosome> getChildsChr(){
		int i;
		ArrayList<Chromosome> childsChr = new ArrayList<>();
		
		for(i = 0; i < this.chrList.size(); i++){
			if(this.chrList.get(i).isSpecieChild()){
				childsChr.add(this.chrList.get(i));
			}
		}
		
		return childsChr;
	}



}
