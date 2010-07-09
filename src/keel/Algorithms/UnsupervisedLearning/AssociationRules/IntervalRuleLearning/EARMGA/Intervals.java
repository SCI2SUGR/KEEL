package keel.Algorithms.UnsupervisedLearning.AssociationRules.IntervalRuleLearning.EARMGA;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class Intervals {
  double x0, x1;

  public Intervals() {
  }

  public boolean isCovered (double X) {
	if ((X >= this.x0) && (X <= this.x1))  return (true);
	else  return (false);
  }

  public double getLeft () {
	return (this.x0);
  }

  public double getRight () {
	return (this.x1);
  }

  public void setLeft (double value) {
	this.x0 = value;
  }

  public void setRight (double value) {
	this.x1 = value;
  }

  public Intervals clone(){
    Intervals d = new Intervals();
    d.x0 = this.x0;
    d.x1 = this.x1;

	return d;
  }
}
