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

import org.core.*;

public class Organizacion {

  public static int NORMAL = 0;
  public static int TRIVIAL = 1;
  public static int ANORMAL = 2;

  myDataset train; //ejemplos de entrenamiento
  Attribute a; //copia del atributo
  int [] miembros; //posicion de los ejemplos miembros de la org. en el conjunto de entrenamiento
  boolean [] Forg;
  boolean [] Uorg;
  int [] ForgValue;
  double fitness;
  int tipo;
  int clase;
  int nFijos,nUtiles;

  public Organizacion() {
  }

  public Organizacion(Attribute a, myDataset train){
    this.train = train;
    this.a = a;
    Forg = new boolean[train.getnInputs()];
    ForgValue = new int[train.getnInputs()];
    Uorg = new boolean[train.getnInputs()];
    nUtiles = 0;
    nFijos = 0;
    //nUtiles = train.getnInputs();
    /*for (int i = 0; i < nUtiles; i++){
      Uorg[i] = true;
    }*/
  }

  public Organizacion(int pos, Attribute a, myDataset train){
    this.train = train;
    this.tipo = this.TRIVIAL;
    this.a = a;
    miembros = new int[1];
    miembros[0] = pos;
    clase = train.getOutputAsInteger(pos);
    fitness = 0; //Trivial
    Forg = new boolean[train.getnInputs()];
    ForgValue = new int[train.getnInputs()];
    Uorg = new boolean[train.getnInputs()];
    nFijos = nUtiles = train.getnInputs();
    double [] example = train.getExample(pos);
    for (int i = 0; i < Forg.length; i++){
      Forg[i] = true;
      ForgValue[i] = (int)example[i];
      Uorg[i] = true;
    }
  }

  public Organizacion(Organizacion padre, Organizacion madre){
    this.train = padre.train;
    this.a = padre.a;
    int [] miembrosP = padre.miembros;
    int [] miembrosM = madre.miembros;
    miembros = new int[miembrosP.length + miembrosM.length];
    for (int i = 0; i < miembrosP.length; i++){
      miembros[i] = miembrosP[i];
    }
    for (int i = miembrosP.length; i < miembrosM.length+miembrosP.length; i++){
      miembros[i] = miembrosM[i-miembrosP.length];
    }
    clase = padre.clase;
    Forg = new boolean[train.getnInputs()];
    ForgValue = new int[train.getnInputs()];
    Uorg = new boolean[train.getnInputs()];
    nUtiles = 0;
    determinaForg();
  }

  /**
   * Calcula el conjunto de atributos fijos
   */
  public void determinaForg(){
    double [] example = train.getExample(miembros[0]);
    nFijos = 0;
    for (int j = 0; j < example.length; j++){
      Forg[j] = true; //es un Fixed
      ForgValue[j] = (int)example[j];
      //Uorg[j] = false;
    }
    for (int i = 1; i < miembros.length; i++){
      example = train.getExample(miembros[i]);
      for (int j = 0; j < Forg.length; j++){
        Forg[j] = Forg[j]&&(ForgValue[j] == (int) example[j]); //true && false == false, false && false == false
      }
    }
    /*for (int i = 0; i < Forg.length; i++){
      if (Forg[i])
        nFijos++;
    }*/
  }

  public void limpiaUtiles(){
    nUtiles = 0;
    for (int i = 0; i < Uorg.length; i++){
      Uorg[i] = false;
    }
  }

  public boolean estaYdistinto(int atributo, int valor){
    return ((Forg[atributo]&&(ForgValue[atributo] != valor)));
  }

  public void addU(int atributo){
    Uorg[atributo] = true;
    nUtiles++;
  }

  /**
   * Seleccionar N ejemplos de las clases contrarias y ver la combinacion de atributos/valores está en dichos ejemplos
   * @param N int Numero de ejemplos a comprobar
   */
  public void actualizarUorg(int N){
    if (nUtiles > 0){
      int[] ejemplos = new int[N];
      int contador = 0;
      do {
        int aleat;
        boolean condicion;
        do {
          do {
            aleat = Randomize.RandintClosed(0, train.size()-1);
          }
          while (train.getOutputAsInteger(aleat) == clase);
          condicion = false;
          for (int i = 0; (i < contador) && (!condicion); i++) {
            condicion = (ejemplos[i] == aleat);
          }
        }
        while (condicion);
        ejemplos[contador] = aleat;
        contador++;
      }
      while (contador < N);
      //Ya tengo los N ejemplos

      contador = nUtiles;
      for (int i = 0; (i < N) && (contador > 0); i++) {
        double[] example = train.getExample(ejemplos[i]);
        contador = nUtiles;
        for (int j = 0; (j < example.length) && (contador > 0); j++) {
          if (Uorg[j] && (ForgValue[j] == (int)example[j])) {
            //System.err.print("sV["+j+"]:"+ForgValue[j]);
            contador--;
          }
        }
        /*if (contador == 0){
          for (int k = 0; k < example.length; k++){
            System.err.print(example[k]+", ");
          }
          System.err.print(example[example.length-1]+" Org: "+this.printString());
        }*/
        //System.err.println(" Nutiles = "+nUtiles+" & contador = "+contador);
      }

      if (contador > 0) { //Hay alguno no marcado
        for (int i = 0; i < Uorg.length; i++) {
          a.incrementar(i);
        }
      }
      else { //Si no Uorg = {conjunto_vacio}
        for (int i = 0; (i < Uorg.length); i++) {
          Uorg[i] = false;
        }
        nUtiles = 0; //alaaaa
      }
    }
  }

  public void calculaFitness(){
    if (miembros.length == 1){ //si solo tiene un miembro
      this.fitness = 0;
      this.tipo = this.TRIVIAL;
    }else{
      boolean entrar = false;
      fitness = 1;
      for (int i = 0; i < Uorg.length; i++){
        if (Uorg[i]){
          fitness *= a.significance[i];
          entrar = true;
        }
      }if (!entrar){
        fitness = -1;
        this.tipo = this.ANORMAL;
      }else{
        fitness *= miembros.length;
        this.tipo = this.NORMAL;
      }
    }
  }

  public String printString(){
    String cadena = new String("");
    cadena += "Members: {";
    for (int i = 0; i < miembros.length; i++){
      cadena += "e"+miembros[i]+", ";
    }
    cadena += "}; ";
    cadena += "Fixed Attributes: {";
    for (int i = 0; i < Forg.length; i++){
      if (Forg[i])
      cadena += train.nombreVar(i)+"("+train.getExample(miembros[0])[i]+"), ";
    }
    cadena += "}, Useful Attributes("+nUtiles+"): {";
    for (int i = 0; i < Uorg.length; i++){
      if (Uorg[i])
        cadena += train.nombreVar(i)+"("+train.getExample(miembros[0])[i]+"), ";
    }
    cadena += "}, Class: "+clase+", "+fitness+"\n";
    return cadena;
  }

  /**
   * Comprueba si una organizacion esta contenida en otra
   * @param org Organizacion la organizacion a comparar
   * @return boolean si esta organizacion esta contenida en el parametro.
   */
  public boolean contenido(Organizacion org){
    boolean respuesta = true; //estoy contenido en org
    for (int i = 0; (i < Uorg.length)&&respuesta; i++){
      if (Uorg[i]){ //Si esta es una variable util
        respuesta = respuesta &&((org.Uorg[i]&&(ForgValue[i] == org.ForgValue[i])));
      } //sera true si la otra organizacion tambien tiene esa variable y son iguales
    }
    if (respuesta){ //Si esta contenido, le paso sus miembros
      int [] miembrosaux = new int [this.miembros.length + org.miembros.length];
      for (int i = 0; i < this.miembros.length; i++){
        miembrosaux[i] = miembros[i];
      }
      for (int i = 0; i < org.miembros.length; i++){
        miembrosaux[i] = org.miembros[i];
      }
      miembros = new int[miembrosaux.length];
      miembros = miembrosaux.clone();

    }
    return respuesta;
  }

  public Organizacion copia(){
    Organizacion o = new Organizacion(this.a, this.train);
    o.miembros = new int[this.miembros.length];
    for (int i = 0; i < miembros.length; i++){
      o.miembros[i] = this.miembros[i];
    }
    for (int i = 0; i < Forg.length; i++){
      o.Forg[i] = this.Forg[i];
      o.ForgValue[i] = this.ForgValue[i];
      o.Uorg[i] = this.Uorg[i];
    }
    o.fitness = this.fitness;
    o.tipo = this.tipo;
    o.clase = this.clase;
    o.nUtiles = this.nUtiles;
    return o;

  }

}

