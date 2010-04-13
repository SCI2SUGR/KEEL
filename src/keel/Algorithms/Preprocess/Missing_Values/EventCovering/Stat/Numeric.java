package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;

public class Numeric {


  public static double secant(DoubleFunc fun, double a, double x0, double x1) {
       double xi = x0, xii= x1, x;
       double yi = fun.F(x0)-a, yii = fun.F(x1)-a, y;

       do {
             x = xii - yii * (xii - xi) / (yii - yi);
             y = fun.F(x)-a;

             xi = xii; xii = x;
             yi = yii; yii = y;
       } while (Math.abs(y)>PRECISION);

       return x;
  }

   /**
    * find inverse x of fun.F so that a = F(x), 
    * where x>=0, and fun.F is monotonically increasing.
    * start is a starting point, >= 0.
    **/
   public static double binsearch(DoubleFunc fun, double a, double start) {
        double xl = 0, xh = start, w = xh - xl;
        double y = fun.F(xh)-a; 
        while (y<-PRECISION) {
           xl = xh;
           xh = xh + w;
           y = fun.F(xh)-a; 
         
        }

        double xm = xl + w/2;
        y = fun.F(xm)-a; 
        while (Math.abs(y)>PRECISION && Math.abs(w)>PRECISION) {
            if (y>0) xh = xm; else xl = xm;
            w = xh - xl;
            xm = xl + w/2.0;
            y = fun.F(xm)-a; 
      //        System.out.println("w = "+w);
        }
        return xm;
   }

   public static double PRECISION = 1e-14;
}
