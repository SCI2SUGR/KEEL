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

package keel.Algorithms.Decision_Trees.DT_GA;


import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * <p>Title: Regla (Rule). </p>
 *
 * <p>Description: This class implements a rule of the classifier.</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class Regla {

  ArrayList<Selector> antecedente;
  String clase;
  myDataset train;
  int ejemplosCubiertos[];
  int ejemplosBienCubiertos[];
  int nCubiertos,nCubiertosOK; //numero de ejemplos cubiertos
  double fitness; //si es una regla producida por el GA
  int codigoRegla; //si es una regla producida por el GA

    /**
     * Default Constructor. Basic structures will be initialized.
     */
    public Regla() {
    antecedente = new ArrayList<Selector> ();
    ejemplosCubiertos = new int[1];
  }

    /**
     * Paramenter constructor. The structures will be initialized with the parameters given.
     * @param clase Consequent class of the rule.
     * @param train Training dataset. 
     */
    public Regla(String clase, myDataset train) {
    antecedente = new ArrayList<Selector> ();
    this.train = train;
    this.clase = clase;
    ejemplosCubiertos = new int[train.size()];
    ejemplosBienCubiertos = new int[train.size()];
  }

    /**
     * Paramenter constructor. The structures will be initialized with the parameters given.
     * @param linea String representation of the rule. This will be parsed into rule object.
     * @param train Training dataset. 
     */
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

    /**
     * Adds a Selector (antedent of the rule, attribute-condition).
     * @param s Selector to be added.
     */
    public void incluyeSelector(Selector s) {
    antecedente.add(s);
  }

    /**
     * Returns a String representation of the rule.
     * @return a String representation of the rule.
     */
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

    /**
     * Returns a copy of the rule.
     * @return a copy of the rule.
     */
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

    /**
     * Returns the number of covered examples.
     * @return the number of covered examples. 
     */
    public int cubiertos(){
    return nCubiertos;
  }

    /**
     * Returns the number of correctly covered examples.
     * @return the number of correctly covered examples. 
     */
    public int cubiertosOK(){
    return nCubiertosOK;
  }

    /**
     * Computes the examples covered by the rule.
     */
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
  }

    /**
     * Checks if an example given is covered by the rule.
     * @param ejemplo given example.
     * @return True if an example given is covered by the rule.
     */
    public boolean cubre(double [] ejemplo){
    boolean cubierto = true;
    for (int i = 0; (i < antecedente.size())&&(cubierto); i++){
      cubierto = cubierto && (antecedente.get(i).cubre(ejemplo));
    }
    return cubierto;
  }

    /**
     * Returns the size of the rule (number of antecedents).
     * @return the size of the rule (number of antecedents).
     */
    public int size(){
    return antecedente.size();
  }

    /**
     * Checks if the rule contains the given attribute in its antecedents.
     * @param att given attribute.
     * @return True if the rule contains the given attribute in its antecedents. 
     */
    public boolean contieneAtributo(int att){
    boolean contiene = false;
    for (int i = 0; i < antecedente.size() && !contiene; i++){
      contiene = (antecedente.get(i).atributo == att);
    }
    return contiene;

  }

}

