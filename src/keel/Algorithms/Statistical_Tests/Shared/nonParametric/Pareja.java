package keel.Algorithms.Statistical_Tests.Shared.nonParametric;

/**
 * <p>Título: </p>
 * <p>Descripción: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Empresa: </p>
 * @author sin atribuir
 * @version 1.0
 */

public class Pareja implements Comparable {

  public double indice;
  public double valor;

  public Pareja() {

  }

  public Pareja(double i, double v) {
    indice = i;
    valor = v;
  }

  public int compareTo (Object o1) { //ordena por valor

	    if (this.valor > ((Pareja)o1).valor)
	      return -1;
	    else if (this.valor < ((Pareja)o1).valor)
	      return 1;
	    else return 0;
}


}