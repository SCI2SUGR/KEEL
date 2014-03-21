package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.GENAR;


import java.util.*;


public class AssociationRule
{
  private ArrayList<Gene> antecedent;
  private ArrayList<Gene> consequent;
  private double all_support;
  private double support;
  private double support_cons;
  private double confidence;
  private double lift;
  private double conv;
  private double CF;
  private double netConf;
  private double yulesQ;
	

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
	rule.support_cons = this.support_cons;
	rule.confidence = this.confidence;
	rule.lift = this.lift;
	rule.CF = this.CF;
	rule.conv = this.conv;
	rule.netConf = this.netConf;
	rule.yulesQ = this.yulesQ;

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

  public double getLift() {
	return this.lift;
  }


  public void setLift(double lift) {
	this.lift = lift;
  }
  
  public double getYulesQ() {
	  return yulesQ;
  }

  public void setYulesQ(double yulesQ) {
	  this.yulesQ = yulesQ;
  }

  public double getConfidence()
  {
	return confidence;
  }


  public void setConfidence(double confidence)
  {
	this.confidence = confidence;
  }

  public int getLength()
  {
	return (this.antecedent.size() + this.consequent.size());
  }

  public double getAmplitude(myDataset dataset) {
	  int i;
	  double avAmp;
	  Gene gene;

	  avAmp = 0.0;

	  for (i=0; i < antecedent.size(); i++) {
		  gene = antecedent.get(i);
		  avAmp += ((gene.getU() - gene.getL()) / (dataset.getMax(gene.getAttr()) - dataset.getMin(gene.getAttr())));
	  }

	  for (i=0; i < consequent.size(); i++) {
		  gene = antecedent.get(i);
		  avAmp += ((gene.getU() - gene.getL()) / (dataset.getMax(gene.getAttr()) - dataset.getMin(gene.getAttr())));
	  }

	  avAmp /= (antecedent.size() + consequent.size());

	  return (avAmp);
  }

  public String toString()
  {
	return ( this.antecedent.toString() + "-> " + this.consequent.toString() + ": " + this.getSupport() + "; " + this.getAll_support() + "; " + this.getConfidence() + "; " + this.getLift());
  }

  public double getCF() {
	  return CF;
  }

  public void setCF(double cf) {
	  CF = cf;
  }

  public double getConv() {
	  return conv;
  }

  public void setConv(double conv) {
	  this.conv = conv;
  }

  public double getNetConf() {
	  return netConf;
  }

  public void setNetConf(double netConf) {
	  this.netConf = netConf;
  }

  public double getSupport_cons() {
	  return support_cons;
  }

  public void setSupport_cons(double support_cons) {
	  this.support_cons = support_cons;
  }

  public void sortGenes (ArrayList<Gene> genes) {
	  int i, j;
	  Gene gen;

	  for (i=0; i <genes.size()-1; i++) {
		  for (j=0; j <genes.size()-i-1; j++) {
			  if (genes.get(j).getAttr() > genes.get(j+1).getAttr()) {
				  gen = genes.get(j);
				  genes.set(j, genes.get(j+1));
				  genes.set(j+1,gen);
			  }
		  }
	  }
  }

  public ArrayList<Gene> getGenesRule(){
	  ArrayList<Gene> genesRule = new ArrayList<>();
	  int i;

	  for (i=0; i < this.antecedent.size(); i++) {
		  genesRule.add((this.antecedent.get(i)).copy());
	  }

	  for (i=0; i < this.consequent.size(); i++) {
		  genesRule.add((this.consequent.get(i)).copy());
	  }

	  return genesRule;
  }
     
}
