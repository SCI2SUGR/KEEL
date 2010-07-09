package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.UnsupervisedAlatasetal;

/**
 * <p>
 * @author Written by Nicolò Flugy Papè (Politecnico di Milano) 24/03/2009
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

import java.util.*;


public class AssociationRule {
  /**
   * <p>
   * It is used for representing and handling an Association Rule.
   * It mainly wraps the methods of a chromosome to offer high-level features later
   * </p>
   */
	
  private Chromosome chr;
  
  /**
   * <p>
   * It creates a new association rule by setting up the chromosome which is based on
   * </p>
   * @param chr The chromosome which this association rule is based on
   */
  public AssociationRule(Chromosome chr) {
	  this.chr = chr;
  }

  /**
   * <p>
   * It allows to clone correctly an association rule
   * </p>
   * @return A copy of the association rule
   */
  public AssociationRule copy() {
	AssociationRule rule = new AssociationRule(this.chr);
	
	return rule;
  }
  
  /**
   * <p>
   * It retrieves the antecedent part of an association rule
   * </p>
   * @return An array of genes only representing antecedent attributes
   */
  public Gene[] getAntecedents() {
	int i, g;
	ArrayList<Integer> indexes;
	Gene[] ants;
	
	indexes = this.chr.getIndexOfAntecedentGenes();
	ants = new Gene[ indexes.size() ];
	
	for (i=0; i < indexes.size(); i++) {
		g = indexes.get(i);
		ants[i] = this.chr.getGene(g);
	}
	  
	return ants;
  }
  
  /**
   * <p>
   * It retrieves the consequent part of an association rule
   * </p>
   * @return An array of genes only representing consequent attributes
   */
  public Gene[] getConsequents() {
	int i, g;
	ArrayList<Integer> indexes;
	Gene[] cons;
	
	indexes = this.chr.getIndexOfConsequentGenes();
	cons = new Gene[ indexes.size() ];
	
	for (i=0; i < indexes.size(); i++) {
		g = indexes.get(i);
		cons[i] = this.chr.getGene(g);
	}	
	  
	return cons;
  }
  
  /**
   * <p>
   * It indicates the attributes which act as antecedents within an association rule
   * </p>
   * @return An array of IDs for the attributes acting as antecedents
   */
  public ArrayList<Integer> getIdOfAntecedents() {
	return ( this.chr.getIndexOfAntecedentGenes() );
  }
  
  /**
   * <p>
   * It indicates the attributes which act as consequents within an association rule
   * </p>
   * @return An array of IDs for the attributes acting as consequents
   */
  public ArrayList<Integer> getIdOfConsequents() {
	return ( this.chr.getIndexOfConsequentGenes() );
  }
  
  /**
   * <p>
   * It indicates the dataset records which have been covered by an association rule
   * </p>
   * @return An array of IDs representing the covered records in the dataset
   */
  public ArrayList<Integer> getCoveredTIDs() {
	return ( this.chr.getCoveredTIDs() );
  }
  
  /**
   * <p>
   * It returns the support of an association rule
   * </p>
   * @return A value representing the support of the association rule
   */
  public double getSupport() {
	return ( this.chr.getRuleSupport() );
  }

  /**
   * <p>
   * It returns the confidence of an association rule
   * </p>
   * @return A value representing the confidence of the association rule
   */
  public double getConfidence() {
	return ( this.chr.getRuleConfidence() );
  }
  
  /**
   * <p>
   * It returns a raw string representation of an association rule
   * </p>
   * @return A raw string representation of the association rule
   */  
  public String toString() {
	return ( this.chr.toString() );
  }
  
}
