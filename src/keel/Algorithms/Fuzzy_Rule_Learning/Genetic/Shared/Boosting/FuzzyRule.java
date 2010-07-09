package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

public class FuzzyRule {

   // terms values meanning
   //          -1 : term has no performance
   // 0..nlabel-1 : Assertion x=label
   //   >= nlabel : 'or' asserts combination

   // We handle type 2 rules with signed consecuents
   // It is equivalent to handle type 3 rules after 
   // rule bank normalization

   public int antecedente[];
   public double consecuente[];

   public FuzzyRule(int ant[], double con[]) {
     antecedente=new int[ant.length];
     for (int i=0;i<ant.length;i++) antecedente[i]=ant[i];
     consecuente=new double[con.length];
     for (int i=0;i<con.length;i++) consecuente[i]=con[i];
   }

   public double tnorma(double a, double b) {
     if (a<b) return a; else return b;
   }
}

