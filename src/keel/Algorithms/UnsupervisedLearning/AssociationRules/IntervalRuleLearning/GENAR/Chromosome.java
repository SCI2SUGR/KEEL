package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.GENAR;


/*
 * Note: this class has a natural ordering that is inconsistent with equals.
 */

public class Chromosome implements Comparable {

	private Gene[] genes;
	private double fit;
	
	public Chromosome(Gene[] genes) {
		this.genes = new Gene[genes.length];
		
		for (int i=0; i < genes.length; i++)  this.genes[i] = genes[i].copy();
	}

	public Chromosome copy() {
		Chromosome chromo = new Chromosome(this.genes);
		chromo.fit = this.fit;

		return  chromo;
	}

	public Gene[] getGenes() {
		return this.genes;
	}

	public Gene getGen(int i) {
		return this.genes[i];
	}

	public int length() {
		return this.genes.length;
	}

	public double getFit() {
		return this.fit;
	}

	public void setFit(double fit) {
		this.fit = fit;
	}	
	
	public int compareTo (Object chr) {
		if (((Chromosome) chr).fit < this.fit)  return -1;
		if (((Chromosome) chr).fit > this.fit)  return 1;
		return 0;
	}

	public String toString() {
		String str = "Size: " + this.genes.length + "; Fit: " + this.fit + "\n";
		
		for (int i=0; i < this.genes.length; i++)
			str += this.genes[i] + "\n";
		return str;
	}

	public void sortGenes () {
		int i, j;
		Gene gen;

		for (i=0; i <this.genes.length-1; i++) {
			for (j=0; j <this.genes.length-i-1; j++) {
				if (this.genes[j].getAttr() > this.genes[j+1].getAttr()) {
					gen = this.genes[j];
					this.genes[j] = this.genes[j+1];
					this.genes[j+1] = gen;
				}
			}
		}
	}
}
