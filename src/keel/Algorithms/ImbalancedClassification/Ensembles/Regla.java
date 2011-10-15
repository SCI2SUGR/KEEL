/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.ImbalancedClassification.Ensembles;

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

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Regla {

  ArrayList<Selector> antecedente;
  String clase;
  myDataset train;
  int ejemplosCubiertos[];
  int ejemplosBienCubiertos[];
  int nCubiertos,nCubiertosOK; //numero de ejemplos cubiertos
  float fCubiertos, fCubiertosOK;
  double fitness; //si es una regla producida por el GA
  int codigoRegla; //si es una regla producida por el GA

  public Regla() {
    antecedente = new ArrayList<Selector> ();
    ejemplosCubiertos = new int[1];
  }

  public Regla(String clase, myDataset train) {
    antecedente = new ArrayList<Selector> ();
    this.train = train;
    this.clase = clase;
    ejemplosCubiertos = new int[train.size()];
    ejemplosBienCubiertos = new int[train.size()];
  }

  public Regla(myDataset train, String linea) {
    antecedente = new ArrayList<Selector> ();
    this.train = train;
    ejemplosCubiertos = new int[train.size()];
    ejemplosBienCubiertos = new int[train.size()];
    String [] nombres = train.nombres();
    StringTokenizer campo = new StringTokenizer(linea, " ");
    campo.nextToken(); //RULE-X:
    String aux = campo.nextToken(); //IF
    while(!aux.equalsIgnoreCase("THEN")){
      String atributo = campo.nextToken();
      String operador = campo.nextToken();
      String valor = campo.nextToken();
      Selector s = new Selector(atributo,operador,valor);
      s.adjuntaNombres(nombres);
      antecedente.add(s);
      aux = campo.nextToken();
    }
    campo.nextToken(); //class
    campo.nextToken(); //=
    clase = campo.nextToken();
  }

  public void incluyeSelector(Selector s) {
    antecedente.add(s);
  }

  public String printString() {
    String cadena = new String("");
    cadena += "IF ";
    for (int i = 0; i < antecedente.size()-1; i++) {
      cadena += antecedente.get(i).printString()+ "AND ";
    }
    cadena += antecedente.get(antecedente.size()-1).printString();
    cadena += " THEN Class = " + clase + " ("+nCubiertosOK+"/"+nCubiertos+")\n";
    return cadena;
  }

  public String printStringF() {
    String cadena = new String("");
    cadena += "IF ";
    for (int i = 0; i < antecedente.size()-1; i++) {
      cadena += antecedente.get(i).printString()+ "AND ";
    }
    if (antecedente.size() > 0)
      cadena += antecedente.get(antecedente.size()-1).printString();
    cadena += " THEN Class = " + clase + " ("+fCubiertosOK+"/"+fCubiertos+")\n";
    return cadena;
  }

  public Regla copia(){
    Regla r = new Regla(clase, train);
    r.antecedente = new ArrayList<Selector>();
    for (int i = 0; i < antecedente.size(); i++){
      r.antecedente.add(antecedente.get(i).copia());
    }
    r.nCubiertos = nCubiertos;
    r.nCubiertosOK = nCubiertosOK;
    r.ejemplosCubiertos = new int[ejemplosCubiertos.length];
    r.ejemplosCubiertos = ejemplosCubiertos.clone();
    r.ejemplosBienCubiertos = new int[ejemplosBienCubiertos.length];
    r.ejemplosBienCubiertos = ejemplosBienCubiertos.clone();
    r.fitness = fitness;
    r.codigoRegla = codigoRegla;
    return r;
  }

  public int cubiertos(){
    return nCubiertos;
  }

  public int cubiertosOK(){
    return nCubiertosOK;
  }

  public void cubrirEjemplos(){
    nCubiertos = nCubiertosOK = 0;
    for (int i = 0; i < train.size(); i++){
      double [] ejemplo = train.getExample(i);
      if (this.cubre(ejemplo)){
        ejemplosCubiertos[nCubiertos] = i;
        nCubiertos++;
        if (train.getOutputAsString(i).compareToIgnoreCase(this.clase) == 0){
          ejemplosBienCubiertos[nCubiertosOK] = i;
          nCubiertosOK++;
        }
      }
    }
   // System.out.println("Ejemplos cubiertos: " + nCubiertos + " Ejemplos cubiertosOK: " + nCubiertosOK);
  }

    public void cubrirEjemplos(double[] weights){
    fCubiertos = fCubiertosOK = 0;
    for (int i = 0; i < train.size(); i++){
      double [] ejemplo = train.getExample(i);
      if (this.cubre(ejemplo)){
        //ejemplosCubiertos[nCubiertos] = i;
        fCubiertos += weights[i];
        if (train.getOutputAsString(i).compareToIgnoreCase(this.clase) == 0){
          fCubiertosOK += weights[i];
        }
      }
    }
   // System.out.println("Ejemplos cubiertos: " + nCubiertos + " Ejemplos cubiertosOK: " + nCubiertosOK);
    train = null;
    ejemplosCubiertos = null;
    ejemplosBienCubiertos = null;
  }

  public boolean cubre(double [] ejemplo){
    boolean cubierto = true;
    for (int i = 0; (i < antecedente.size())&&(cubierto); i++){
      cubierto = cubierto && (antecedente.get(i).cubre(ejemplo));
    }
    return cubierto;
  }

  public int size(){
    return antecedente.size();
  }

  public boolean contieneAtributo(int att){
    boolean contiene = false;
    for (int i = 0; i < antecedente.size() && !contiene; i++){
      contiene = (antecedente.get(i).atributo == att);
    }
    return contiene;

  }

}
