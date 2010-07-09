package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;
public class LinearRegression {


public void add(double X, double Y) {
    x += X; xx += X*X;
    y += Y; yy += Y*Y;
    xy += X*Y;
    n++;
}

public void reset() {
   n=0;
   x=0;
   xx=0;
   y=0;
   yy=0;
   xy=0;
}

public int getN()     { return n; }
public double getX()     { return x; }
public double getY()     { return y; }
public double getXavg()  { return x/n; }
public double getYavg()  { return y/n; }
public double getXX()    { return xx; }
public double getYY()    { return yy; }
public double getXY()    { return xy; }
public double getXXavg() { return xx/n; }
public double getYYavg() { return yy/n; }
public double getXYavg() { return xy/n; }
public double getSxx()   { return xx - x*x/n; }
public double getSyy()   { return yy - y*y/n; }
public double getSxy()   { return xy - x*y/n; }


// beta1
public double getBeta1() { return getSxy() / getSxx(); }
public double getSlope() { return getBeta1(); }

// beta0
public double getBeta0() { return (y/n) - getSlope() * (x/n); }
public double getIntercept() { return getBeta0(); }

// SSe
public double getSSe() { return getSyy() - getBeta1()*getSxy(); }
public double getErrorSumOfSquares() { return getSSe(); }

public double getSSr() { return getBeta1() * getSyy(); }

// sigma^2
public double getSigmaSq() { return getSSe() / (n-2); }
public double getErrorVariance() { return getSigmaSq(); }

// se(beta1)
public double getSeBeta1() { return Math.sqrt( getSigmaSq() / getSxx()); }
public double getStdErrorSlope() { return getSeBeta1(); }

// se(beta0)
public double getSeBeta0() { 
  return Math.sqrt( getSigmaSq() * ( 1/n +  (x/n) * (x/n) / getSxx()) ); }
public double getStdErrorIntercept() { return getSeBeta0(); }

public double getF0() { return getSSr() / getSigmaSq(); }


// correlation
public double getR() { return getSxy() / Math.sqrt(getSxx() * getSyy()); }
public double getCorrelation() { return getR(); }

//
public double getT0() { 
  double r = getR();
  return r * Math.sqrt(n-2) / Math.sqrt(1 - r*r); 
}

private int n = 0;
private double x=0,xx=0,y=0,yy=0,xy=0;

}
