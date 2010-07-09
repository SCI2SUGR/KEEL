package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

import java.util.*;


public class AssociationRule
{
  private ArrayList<Gene> antecedent;
  private ArrayList<Gene> consequent;
  private double all_support;
  private double support;
  private double confidence;
	

  public AssociationRule()
  {
    this.antecedent = new ArrayList<Gene>();
    this.consequent = new ArrayList<Gene>();
  }

  public AssociationRule copy () {
	int i;
	AssociationRule rule = new AssociationRule();

	for (i=0; i < this.antecedent.size(); i++) {
		rule.addAntecedent((this.antecedent.get(i)).copy());
	}

	for (i=0; i < this.consequent.size(); i++) {
		rule.addConsequent((this.consequent.get(i)).copy());
	}
	
	rule.all_support = this.all_support;
	rule.support = this.support;
	rule.confidence = this.confidence;

	return (rule);
  } 
  
  public void addAntecedent(Gene g)
  {
    antecedent.add(g);
  }
  
  
  public void addConsequent(Gene g)
  {
    consequent.add(g);
  }
  
  
  public ArrayList<Gene> getAntecedent()
  {
    return this.antecedent;
  }
  
  
  public ArrayList<Gene> getConsequent()
  {
    return this.consequent;
  }


  public int getLengthAntecedent()
  {
    return this.antecedent.size();
  }

  public double getAll_support()
  {
	return all_support;
  }


  public void setAll_support(double all_support)
  {
	this.all_support = all_support;
  }

  public double getSupport() {
	return this.support;
  }


  public void setSupport(double support) {
	this.support = support;
  }


  public double getConfidence()
  {
	return confidence;
  }


  public void setConfidence(double confidence)
  {
	this.confidence = confidence;
  }
  
   
  public String toString()
  {
	return ( this.antecedent.toString() + "-> " + this.consequent.toString() + ": " + this.getSupport() + "; " + this.getAll_support() + "; " + this.getConfidence());
  }

}
