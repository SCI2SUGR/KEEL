package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Shared.Boosting;


abstract public class Fitness {
  public static final double NOELEGIR=0.99e6f;
  public static final double ITERACIONES=1.0e6f;
  public static final double NEGATIVO=1.1e6f;
  public static final double SINGULAR=1.2e6f;
  public static final double NOCUBRE=1.3e6f;
  public static int Cuentait[];
  abstract public double evalua(GenotypeBoosting g);
}
