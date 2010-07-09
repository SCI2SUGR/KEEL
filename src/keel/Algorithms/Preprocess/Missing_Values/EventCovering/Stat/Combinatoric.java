package keel.Algorithms.Preprocess.Missing_Values.EventCovering.Stat;
import java.util.Vector;

public class Combinatoric {

// combinatorical functions
// Source: S.Gill Williamson (1985) Combinatorics for Computer Science, Computer Science Press

public static long over(int n, int m) {
   if (n<=0 || n<m || m<0) return 0;
   if (m==0 || m==n) return 1;
   if (m==1 || m==n-1) return n;
   if (n>=binom.size()) bintab(n);
   return readbinom(n,m);
}

private static long readbinom(int n, int m) {
   if (n>=binom.size()) return 0;
   Vector v = (Vector)(binom.elementAt(n));
   if (m>=v.size()) return 0;
   return ((Long)(v.elementAt(m))).longValue();
}

private static void bintab(int n) {

   if (binom.size()==0) {
      Vector v=new Vector();
      v.add(new Long(0));
   }
   for (int i=binom.size(); i<=n; i++) {
      Vector v=new Vector();
      v.add(new Long(1));
      for (int j=1; j<i; j++)
         v.add(new Long(readbinom(i-1,j-1)+readbinom(i-1,j)));
      v.add(new Long(1));
      binom.add(v);
   }
}

private static Vector binom = new Vector();

// Rank of increasing function a[] in colex order
public static long colexIncFuncRank(int[] a) {
   int d = a.length;
   long sum=0;
   for (int i=0; i<d; i++) 
       sum += over(a[i],i+1);
   return sum;
}

// Rank of increasing function a[] (r^d) in lex order
// d = length(a)
public static long lexIncFuncRank(int[] a, int r) {
   int d = a.length;
   long sum=0;
   for (int i=0; i<d; i++)  
       sum += over(r-1-a[i],d-i);
   return over(r,d)-1-sum;
}
   

// Rank of nondecreasing function a[] (r^d) in lex order
// d = length(a)
public static long lexNondecFuncRank(int[] a, int r) {
   int d = a.length;
   long sum=0;
   int pos=0;
   for (int i=0; i<d; i++)  {
       if (i==0) pos=a[i]; else pos+=(a[i]-a[i-1])+1;
       sum += over(r+d-2-pos,d-i);
   }
   return over(r+d-1,d)-1-sum;
}

// Rank of composition a[] (d balls in r=a.length boxes) in lex order
// is mapped to nondecreasing function r^d
public static long lexCompositionRank(int[] a) {
   int d = 0, r = a.length;
   for (int i=0; i<r; i++) d+=a[i];
   long sum=0;
   int pos=0;
   int k=0; // counts balls
   for (int i=0; i<r; i++)  {
       for (int j=0; j<a[i]; j++) {       
         sum += over(r+d-2-pos,d-k);
         k++; pos++;
       }
       pos++;
   }
   return over(r+d-1,d)-1-sum;
}


// next nondecreasing function (r^d) in lex order
// return false at last function
public static boolean nextNondecFunc(int[] a, int r) {
  int d = a.length;
  for (int i=d-1; i>=0; i--) {
     if (a[i]<r-1) { 
        a[i]++; 
        for (int j=i+1; j<d; j++) a[j]=a[i];
        return true; 
     }
  }
  return false; 
}


}
