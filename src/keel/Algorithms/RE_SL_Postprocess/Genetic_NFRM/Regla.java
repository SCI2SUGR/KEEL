package keel.Algorithms.RE_SL_Postprocess.Genetic_NFRM;

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

public class Regla{

  int[] antecedente; //etiquetas de los antecedentes
  int consecuente; //etiqueta del consecuente
  BaseD baseDatos;

  public Regla(Regla r) {
    this.antecedente = new int[r.antecedente.length];
    for (int k = 0; k < this.antecedente.length; k++) {
      this.antecedente[k] = r.antecedente[k];
    }
    this.baseDatos = r.baseDatos;
    this.consecuente = r.consecuente;
  }

  public Regla(BaseD baseDatos) {
    this.baseDatos = baseDatos;
    antecedente = new int[baseDatos.numVariables()-1];
  }

  public void asignaAntecedente(int [] antecedente){
    for (int i = 0; i < antecedente.length; i++){
      this.antecedente[i] = antecedente[i];
    }
  }

  public double compatibilidadMinimo(double[] ejemplo){//, double [] ajuste) {
    double minimo, grado_pertenencia;
    minimo = 1.0;
    //baseDatos.ajusta(ajuste);
    for (int i = 0; i < antecedente.length; i++) {
      grado_pertenencia = baseDatos.pertenencia(i, antecedente[i], ejemplo[i]);
      minimo = Math.min(grado_pertenencia, minimo);
    }
    return (minimo);

  }

  public Regla clone() {
    Regla r = new Regla(baseDatos);
    r.antecedente = new int[antecedente.length];
    for (int i = 0; i < this.antecedente.length; i++) {
      r.antecedente[i] = this.antecedente[i];
    }
    r.consecuente = this.consecuente;
    return r;
  }

}
