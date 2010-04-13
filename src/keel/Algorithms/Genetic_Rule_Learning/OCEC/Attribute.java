package keel.Algorithms.Genetic_Rule_Learning.OCEC;

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
public class Attribute {

  double[] significance;
  String [] names;

  public Attribute(int n_variables) {
    significance = new double[n_variables];
    for (int i = 0; i < n_variables; i++) {
      significance[i] = 1.0;
    }
    names = new String[n_variables];
  }

  public Attribute(String [] nombres) {
    significance = new double[nombres.length];
    for (int i = 0; i < nombres.length; i++) {
      significance[i] = 1.0;
    }
    this.names = new String[nombres.length];
    this.names = nombres.clone();
  }


  public void reducir(int atributo) {
    significance[atributo] = 0.9 * significance[atributo] + 0.05;
  }

  public void incrementar(int atributo) {
    significance[atributo] = 0.9 * significance[atributo] + 0.2;
  }

  public String printString() {
    String cadena = new String("");
    for (int i = 0; i < significance.length - 1; i++) {
      cadena += names[i] + "(" + significance[i] + "), ";
    }
    cadena += names[names.length-1] + "(" + significance[significance.length - 1] + "). \n";
    return cadena;
  }

  public void print(){
    System.out.print(this.printString());
  }

}
