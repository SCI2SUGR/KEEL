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

/**
 * <p>
 * @author Written by Julián Luengo Martín 15/02/2007
 * @version 0.1
 * @since JDK 1.5
 * </p>
 */
package keel.Algorithms.Genetic_Rule_Learning.CORE;

import keel.Dataset.*;

import java.util.*;

import org.core.*;

/**
 * <p>
 * This class represents the chromosome (rule) of the CORE algorithm
 * </p>
 */
public class Chromosome implements Comparable{
	
//	ArrayList<Gene> chrom;
	Gene []chrom;
	int numGenes;
	int _class;
	int capturedTokens;
	double fitness;
	long Mu_next;
	boolean evaluated;
	
	/**
	 * <p>
	 * Default constructor. No memory allocated.
	 * </p>
	 */
	public Chromosome(){
//		chrom = new ArrayList<Gene>(Attributes.getInputNumAttributes());
		chrom = new Gene[Attributes.getInputNumAttributes()];
		evaluated = false;
		fitness = -1;
		Mu_next = 0;
		_class = -1;
		numGenes = 0;
		capturedTokens = 0;
	}
	
	/**
	 * <p>
	 * Deep-copy constructor.
	 * </p>
	 * @param c original chromosome
	 */
	public Chromosome(Chromosome c){
		chrom = new Gene[Attributes.getInputNumAttributes()];
		for(int i=0;i<chrom.length;i++){
			if(c.chrom[i] != null)
				this.chrom[i] = new Gene(c.chrom[i]);
			else
				this.chrom[i] = null;
		}
		evaluated = c.evaluated;
		fitness = c.fitness;
		Mu_next = c.Mu_next;
		_class = c._class;
		numGenes = c.numGenes;
		capturedTokens = c.capturedTokens;
		
	}
	
	/**
	 * <p>
	 * Adds a nominal gene to this chromosome (rule)
	 * </p>
	 * @param pos Position in which we put the gene
	 * @param a the attribute related to this gene (nominal!)
	 * @param relation the type of the relation which implements the gene (see Gene.java)
	 * @param values the nominal values that covers this gene
	 */
	public void addNominalGene(int pos, Attribute a,int relation,ArrayList<String> values){
		Gene newGene = new Gene(a,relation);
		
		newGene.addNominalValues(values);
		chrom[pos] = newGene;
		numGenes++;
	}
	
	/**
	 * <p>
	 * Adds a real valued gene
	 * </p>
	 * @param pos Position in which we put the gene
	 * @param a the attribute related to this gene (real or integer!)
	 * @param relation the type of the relation which implements the gene (see Gene.java)
	 * @param value the real values that covers this gene
	 */
	public void addRealGene(int pos,Attribute a, int relation, double value){
		Gene newGene = new Gene(a,relation);
		
		newGene.setRealValue(value);
		chrom[pos] = newGene;
		numGenes++;
	}
	
	/**
	 * <p>
	 * Adds a real-valued gene, and sets its bounds
	 * </p>
	 * @param pos the position of the gene in the chromosome
	 * @param a the attribute related to this gene (real!)
	 * @param relation the type of relation (see Gene.java)
	 * @param value array of 2 positions, with the minimum bound (position [0]) and maximum bound (position [1])
	 */
	public void addRealBoundedGene(int pos,Attribute a, int relation, double value[]){
		Gene newGene = new Gene(a,relation);
		
		newGene.setRealminBound(value[0]);
		newGene.setRealmaxBound(value[1]);
		
		chrom[pos] = newGene;
		numGenes++;
	}
	
	/**
	 * <p>
	 * Deletes one gene from the chromosome
	 * </p>
	 * @param index position of the gene to be deleted
	 */
	public void removeGene(int index){
		chrom[index] = null;
		numGenes--;
	}
	
	/**
	 * <p>
	 * Remove a specific gene from the chromosome, using the Object.equals()
	 * method (must be the SAME object with SAME id)
	 * </p>
	 * @param gene gene object to be deleted from the chromosome
	 */
	public void removeGene(Gene gene){
		for(int i=0;i<chrom.length;i++){
			if(gene.equals(chrom[i])){
				chrom[i] = null;
				numGenes--;
				return;
			}
			
		}
	}
	
	/**
	 * <p>
	 * Evaluates an input example (array of doubles as extracted from KEEL API)
	 * </p>
	 * @param input the input instance
	 * @return the class of this rule if covered, -1 otherwise
	 */
	public int evaluate(double input[]){
		boolean covered = true;
		for(int i=0;i<input.length && covered;i++){
			if(chrom[i]!=null)
				covered = chrom[i].test(input[i]);
		}
		
		if(covered)
			return _class;
		else
			return -1;
	}
	
	/**
	 * <p>
	 * Gets the number of genes of this chromosome
	 * </p>
	 * @return the number of genes
	 */
	public int getNumGenes(){
		return numGenes;
	}
	
	/**
	 * <p>
	 * Applies a random mutation of the specified gene
	 * </p>
	 * @param gen index of the gene to be mutated
	 */
	public void mutateGene(int gen){
		int relation;
		double bounds[] = new double[2];
		double value;
		ArrayList<String> values;
		Attribute a;
		Gene g = chrom[gen];
		if(g != null){
			if(Randomize.Rand()<0.5)
				g.mutate();
			else
				chrom[gen] = null;
		}
		else{
			a = Attributes.getInputAttribute(gen);
			if(Randomize.Rand()<0.5){
				if(Attributes.getInputAttribute(gen).getType()==Attribute.NOMINAL){
					values = new ArrayList<String>();
					for(int i=0;i<a.getNumNominalValues();i++){
						if(Randomize.Rand()<0.5)
							values.add((String)(a.getNominalValuesList().toArray()[i]));
					}
					addNominalGene(gen, a, Randomize.Randint(0, 2), values);
				}
				else{
					relation = Randomize.Randint(2, 8);
					if(relation==Gene.outOfBound || relation==Gene.inBound){
						bounds[0] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
						bounds[1] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
						if(bounds[0]>bounds[1]){
							value = bounds[0];
							bounds[0] = bounds[1];
							bounds[1] = value;
						}
						addRealBoundedGene(gen, a, relation, bounds);
					}
					else{
						value = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
						addRealGene(gen, a, relation, value);
					}
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Applies a mutation on this chromosome, adding a gene (0.5 prob) or deleting
	 * one gene (0.5 prob or if this chromosome has genes for all the attributes)
	 * </p>
	 */
	public void mutate(){
		int selectedGene,relation;
		double value;
		double bounds[] = new double[2];
		boolean found;
		ArrayList<String> values;
		Attribute a;
		if(Randomize.Rand()<0.5 || numGenes == Attributes.getInputNumAttributes()){
			selectedGene = Randomize.Randint(0, chrom.length);
			while(chrom[selectedGene]==null){
				selectedGene = (selectedGene+1)%chrom.length;
			}
			chrom[selectedGene] = null;
			numGenes--;
		}
		else{
			selectedGene = Randomize.Randint(0, Attributes.getInputNumAttributes());
			do{
				found = (chrom[selectedGene]!=null);
				if(found)
					selectedGene = (selectedGene+1)%Attributes.getInputNumAttributes();
			}while(found);
			a = Attributes.getInputAttribute(selectedGene);
			if(Attributes.getInputAttribute(selectedGene).getType()==Attribute.NOMINAL){
				values = new ArrayList<String>();
				for(int i=0;i<a.getNumNominalValues();i++){
					if(Randomize.Rand()<0.5)
						values.add((String)(a.getNominalValuesList().toArray()[i]));
				}
					
				this.addNominalGene(selectedGene, a, Randomize.Randint(0, 2), values);
			}
			else{
				relation = Randomize.Randint(2, 8);
				if(relation==Gene.outOfBound || relation==Gene.inBound){
					bounds[0] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
					bounds[1] = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
					if(bounds[0]>bounds[1]){
						value = bounds[0];
						bounds[0] = bounds[1];
						bounds[1] = value;
					}
					this.addRealBoundedGene(selectedGene, a, relation, bounds);
				}
				else{
					value = Randomize.RanddoubleClosed(a.getMinAttribute(), a.getMaxAttribute());
					this.addRealGene(selectedGene, a, relation, value);
				}
				
			}
		}
			
	}
	
	/**
	 * <p>
	 * Sets the class of this chromosome
	 * </p>
	 * @param c the new consequent class
	 */
	public void setClass(int c){
		_class = c;
	}
	
	/**
	 * <p>
	 * Gets the class of this rule
	 * </p>
	 * @return the consequent class
	 */
	public int getClas(){
		return _class;
	}
	
	/**
	 * <p>
	 * Set this chromosome as evaluated (fitness computed for the current set of genes) or not
	 * </p>
	 * @param ev the new evaluation status
	 */
	public void setEvaluated(boolean ev){
		evaluated = ev;
	}
	
	/**
	 * <p>
	 * Gets the evaluation status
	 * </p>
	 * @return True if the fitness is correct for this gene set, false otherwise
	 */
	public boolean isEvaluated(){
		return evaluated;
	}
	
	/**
	 * <p>
	 * Sets the fitness for this rule
	 * </p>
	 * @param fit the new fitness
	 */
	public void setFitness(double fit){
		fitness = fit;
	}
	
	/**
	 * <p>
	 * Reset the count of captured tokens for this rule in the tokens
	 * competition
	 * </p>
	 */
	public void resetTokens(){
		capturedTokens = 0;
	}
	
	/**
	 * <p>
	 * Increases the amount of captured tokens by 1
	 * </p>
	 */
	public void tokenCaptured(){
		capturedTokens++;
	}
	
	/**
	 * <p>
	 * Decreases the amount of captured tokens by 1
	 * </p>
	 */
	public void tokenLost(){
		capturedTokens--;
	}
	
	/**
	 * <p>
	 * Performs the one-point crossover with other chromosome
	 * </p>
	 * @param c the chrosomome to be crossed with
	 * @param cutpoint the cutpoint of this swapping operation
	 */
	public void swap(Chromosome c,int cutpoint){
		Gene gen;
		Chromosome tmp = new Chromosome(c);
		Attribute a;
		double rnd;
		boolean aux;

//		cutpoint = Math.min(cutpoint, numGenes);
		if(Randomize.Rand()<0.7){
			for(int i=0;i<cutpoint;i++){
				gen = this.chrom[i];
				if(c.chrom[i]!= null && gen==null)
					c.numGenes--;
				if(c.chrom[i]==null && gen!=null)
					c.numGenes++;
				c.chrom[i] = gen;
				gen = tmp.chrom[i];
				if(this.chrom[i]!= null && gen==null)
					numGenes--;
				if(this.chrom[i]==null && gen!=null)
					numGenes++;
				this.chrom[i] = gen;
			}
		}
		else{
			for(int i=0;i<chrom.length;i++){
				if(this.chrom[i]!=null && c.chrom[i]!=null){
					a = Attributes.getInputAttribute(i);
					if(a.getType()!=Attribute.NOMINAL){
						rnd = Randomize.RandClosed();
						this.chrom[i].realValue = rnd*this.chrom[i].realValue + (1-rnd)*c.chrom[i].realValue;
						c.chrom[i].realValue = rnd*c.chrom[i].realValue + (1-rnd)*this.chrom[i].realValue;
					}
					else{
						rnd = Randomize.Randint(0, a.getNumNominalValues());
						for(int v=0;v<rnd;v++){
							aux = c.chrom[i].nominalValue[v];
							c.chrom[i].nominalValue[v] = this.chrom[i].nominalValue[v];
							this.chrom[i].nominalValue[v] = aux;
						}
					}
				}
			}
		}
//		c._class = this._class;
//		this._class = tmp._class;
		
	}
	
	public int compareTo(Object o){
		Chromosome c = (Chromosome) o;
		if(this.fitness < c.fitness)
			return -1;
		if(this.fitness > c.fitness)
			return 1;
		return 0;
	}
	
	
	/**
	 * <p>
	 * Test if two chromosomes are equals by comparing their values
	 * </p>
	 * @param c the chromosome to be compared with
	 * @return True if they are equals in values, false otherwise
	 */
	public boolean same(Chromosome c){
		for(int i=0;i<chrom.length;i++){
			if((chrom[i]==null && c.chrom[i]!= null)||
					(chrom[i]!=null && c.chrom[i]== null))
				return false;
			if((chrom[i]!=null && c.chrom[i]!= null)&&(!chrom[i].same(c.chrom[i])))
				return false;
		}
		return true;
	}
}

