package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.UnsupervisedGENAR;


public class Gene {
	
	public static final int NOMINAL = 0;
	public static final int INTEGER = 1;
	public static final int REAL = 2;

	private int attr;
	private int type;
	private double l;
	private double u;

	public Gene() {

	}

	public Gene copy() {
		Gene gen = new Gene();
		
		gen.attr = this.attr;
		gen.type = this.type;
		gen.l = this.l;
		gen.u = this.u;

		return gen;
	}

	public int getAttr() {
		return attr;
	}

	public void setAttr(int attr) {
		this.attr = attr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getL() {
		return l;
	}

	public void setL(double l) {
		this.l = l;
	}

	public double getU() {
		return u;
	}

	public void setU(double u) {
		this.u = u;
	}
	
	public String toString() {
		return "A: " + attr + "; T: " + type + "; L: " + l + "; U: " + u;
	}
}
