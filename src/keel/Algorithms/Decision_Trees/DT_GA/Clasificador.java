package keel.Algorithms.Decision_Trees.DT_GA;

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
public class Clasificador {

  BaseR reglasArbol, reglasGA;
  int umbralS;
  int tipoGA;
  String claseMayoritaria;

  public Clasificador() {
  }

  public Clasificador(BaseR reglasArbol, BaseR reglasGA, int tipoGA,
                      int umbralS,String claseMay) {
    this.reglasArbol = reglasArbol;
    this.reglasGA = reglasGA;
    this.umbralS = umbralS;
    this.tipoGA = tipoGA;
    this.claseMayoritaria = claseMay;
  }

  /**
   * Clasifica un ejemplo
   * @param ejemplo El ejemplo a clasificar
   * @return el valor de la clase predicha
   */
  public String clasifica(double[] ejemplo) {
    boolean smallDisjunct = false;
    String clase = "<unclassified>";
    int i; //guarda la regla que clasifica
    for (i = 0; (i < reglasArbol.size()) && (clase.equals("<unclassified>")); i++) {
      if (reglasArbol.baseReglas.get(i).cubre(ejemplo)) {
        clase = reglasArbol.baseReglas.get(i).clase;
      }
    }
    i--; //suma uno al salir
    if (i == -1){
      return claseMayoritaria; //El arbol esta vacio!!!
    }
    int reglaArbol = i;
    smallDisjunct = (reglasArbol.baseReglas.get(i).cubiertos() < umbralS);
    if (smallDisjunct) {
      if (tipoGA == DT_GA.GA_SMALL) {
        double pesoMax = 0.0;
        String claseAux;
        for (i = 0; i < reglasGA.size(); i++) {
          Regla r = reglasGA.baseReglas.get(i);
          if (r.codigoRegla == reglaArbol) {
            if (r.cubre(ejemplo)) {
              claseAux = r.clase;
              double peso = r.fitness;
              if (peso > pesoMax) {
                clase = claseAux;
                pesoMax = peso;
              }
            }
          }
        }
      }
      else {
        double pesoMax = 0.0;
        String claseAux;
        for (i = 0; i < reglasGA.size(); i++) {
          Regla r = reglasGA.baseReglas.get(i);
          if (r.cubre(ejemplo)) {
            claseAux = r.clase;
            double peso = r.fitness;
            if (peso > pesoMax) { //esto me lo invento porque no viene en el paper
              clase = claseAux;
              pesoMax = peso;
            }
          }
        }
      }
    }
    return clase;
  }

}
