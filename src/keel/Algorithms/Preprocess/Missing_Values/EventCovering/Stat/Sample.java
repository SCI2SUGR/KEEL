package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;
public class Sample {


public void add(double X) {
    x += X; xx += X*X;
    n++;
    if (!mset) { 
      min=X; max=X; mset=true;
    } else {
      if (X>max) max=X;
      if (X<min) min=X;         
    }
}

public void reset() {
   n=0;
   x=0;
   xx=0;
   mset=false;
   min=0; max=0;
}

public int getSampleSize()           { return n; }
public double getSum()               { return x; }
public double getMean()              { return x/n; }
public double getSumSquares()        { return xx; }
public double getMeanSquares()       { return xx/n; }
public double getVariance()          { return (xx - x*x/n)/(n-1); }
public double getStandardDeviation() { return Math.sqrt(getVariance()); }
public double getMin()               { return min; }
public double getMax()               { return max; }
public double getRange()             { return max-min; }
public double getCoefficientOfVariation() { return getStandardDeviation()/getMean(); }

public ConfidenceInterval getMean(double confidence, double sigma)  { 
  ConfidenceInterval c = new ConfidenceInterval();
  c.value = x/n;
  c.confidence = confidence;
  double w = sigma/Math.sqrt(n);
  double alpha = 1 - confidence;
  double t = StatFunc.gaussianPercentage(1 - alpha/2);
  c.min = c.value - w*t;
  c.max = c.value + w*t;
  return c;
}



public ConfidenceInterval getMean(double confidence)  { 
  ConfidenceInterval c = new ConfidenceInterval();
  c.value = x/n;
  c.confidence = confidence;
  double w = Math.sqrt(getVariance())/Math.sqrt(n);
  double t = StatFunc.studentPercentage(confidence,n-1);
  c.min = c.value - w*t;
  c.max = c.value + w*t;
  return c;
}


public ConfidenceInterval getVariance(double confidence)  { 
 ConfidenceInterval c = new ConfidenceInterval();
 c.value = getVariance();
 c.confidence = confidence;
 double w = getVariance()*(n-1);
 double alpha = 1.0 - confidence;
 double xl = StatFunc.chiSquarePercentage(1-alpha/2,n-1);
 double xh = StatFunc.chiSquarePercentage(alpha/2,n-1);
 c.min = w/xl;
 c.max = w/xh;
 return c;
}

public ConfidenceInterval getStandardDeviation(double confidence) { 
 ConfidenceInterval c = getVariance(confidence);
 c.min = Math.sqrt(c.min);
 c.max = Math.sqrt(c.max);
 c.value = Math.sqrt(c.value);
 return c;
}


private int n = 0;
private double x=0,xx=0;
private double min=0, max=0;
private boolean mset=false;

}
