package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;

public class FuzzyPartition {
   public double vertices[];
   public FuzzyPartition(double []v) {  
      vertices=new double[v.length];
      for (int i=0;i<v.length;i++) vertices[i]=v[i];
   }
   public double []pertenencia(double x) {
     // All terms ownership vector
     double mu[]=new double[vertices.length];
     if (x<=vertices[0]) mu[0]=1;
     for (int i=0;i<vertices.length-1;i++) {
        if (x>vertices[i] && x<=vertices[i+1]) {
           mu[i+1]=(x-vertices[i])/(vertices[i+1]-vertices[i]);
           mu[i]=1-mu[i+1];
        }
     }
     if (x>=vertices[vertices.length-1]) mu[vertices.length-1]=1;
     return mu;
   }
}

