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

package keel.Algorithms.Genetic_Rule_Learning.Advanced_Ant_Miner_Plus;

import java.util.*;

/**
 * <p>Title: ConjuntoDatos (Dataset) </p>
 * <p>Description: Dataset class represents the dataset read from data files
 * and is used by the ACO algorithm.</p>
 * @author Vicente Rubén del Pino Ruiz
 * @version 1.0
 */

public class ConjuntoDatos {

  private Vector muestras;     //Vector con las muestras sacadas del fichero

  /**
   * Default constructor. Nothing is done.
   */
  public ConjuntoDatos() {
    muestras=new Vector();
  }


  /**
   * Parameter constructor. An dataset is built using the given examples vector.
   * @param datos Vector with the examples to include in the dataset built.
   */
  public ConjuntoDatos(Vector datos){
    muestras= new Vector(datos);
  }

  /**
   * Adds the given example to the dataset.
   * @param ejemplo given example to be added.
   */
  public void insertaMuestra(Muestra ejemplo){
    //addElement Añade un elemento al final del vector (perfecto)
    muestras.addElement(ejemplo);
  }

  /**
   * Removes an example with the given position.
   * @param indice int position of the example to be removed.
   */
  public void eliminaMuestra(int indice){

    muestras.removeElementAt(indice);
  }

  /**
   * Removes the given example from the dataset.
   * @param muestra {@link Muestra} given example to be removed.
   * @return True if the example have been removed, false otherwise.
   */
  public boolean eliminaMuestra(Muestra muestra){
    boolean devolver;
    devolver=muestras.remove(muestra);
    return devolver;
  }

  /**
   *
   * Returns the first example of the dataset.
   * @return {@link Muestra} the first example of the dataset.
   */
  public Muestra obtenerMuestra(){
    Muestra devolver;
    devolver=(Muestra)muestras.firstElement();
    return devolver;

  }

  /**
   * Returns the example in the position given.
   @param indice int given position of the example asked.
   * @return {@link Muestra} the example in the position given.
   */

  public Muestra obtenerMuestra(int indice){
    Muestra devolver;
    devolver=(Muestra)muestras.get(indice);
    return devolver;
  }

  /**
   * Prints the dataset on the standard output.
   * @param nombre String Name of the dataset.
   */

  public void imprimir(String nombre){
    Muestra actual;

    System.out.println("Conjunto de muestras "+nombre);
    for(int i=0;i<muestras.size();i++){
      actual = (Muestra) muestras.get(i);
      actual.imprimir();
    }
  }
  /**
   * Returns the number of examples in the dataset.
   * @return int the number of examples in the dataset.
   */
  public int tamanio(){
    int devolver;
    devolver = muestras.size();
    return devolver;

  }


  /**
   * Returns the probabilities vector related to examples with the pair attribute-value and the list of existing classes given.
   * @param atributo {@link Atributo} pair attribute and value.
   * @param clases Vector of existing classes.
   * @return float [] probabilities array.
   */
  public float [] listaProbabilidadesAtributoClase(Atributo atributo,Vector clases){
    int numClases=clases.size();
    float [] devolver=new float [numClases];
    Muestra actual;
    int total=0;
    boolean terminado=false;
    Atributo clase;

    for(int i=0;i<numClases;i++)
      devolver[i]=0;

    for(int i=0;i<muestras.size();i++){
      actual = (Muestra) muestras.get(i);
      if(actual.estaAtributo(atributo)){//Esta el atributo
        total++;
        terminado=false;
        for(int j=0;j<numClases && !terminado;j++){
          clase=(Atributo)clases.get(j);
          if (actual.estaClase(clase)) {
            devolver[j]++;
            terminado = true;
          }
        }
      }
    }

    for(int i=0;i<numClases;i++)
      if(devolver[i]!=0)
        devolver[i]=devolver[i]/total;

    return devolver;

  }

  /**
   * Removes from dataset the covered cases by the given rule.
   * @param regla {@link Regla} given rule that cover the different cases to be removed.
   * @param cVacias Empty/Null conditions of the rule.
   */
  public void eliminaMuestrasCubiertas(Regla regla,Vector cVacias){
    Vector lista=new Vector();
    Muestra mt;

    for(int i=0;i<muestras.size();i++){
      mt=(Muestra)muestras.get(i);
      if(regla.estanCondicionesEn(mt,cVacias)){
        lista.addElement(mt);
      }
    }
    muestras.removeAll(lista);
  }

  /**
   * Removes from the set the covered cases which have the same class as the rule given.
   * @param regla {@link Regla} given rule.
   */
  public void eliminaMuestrasClase(Regla regla){
   Vector lista=new Vector();
   Muestra mt;
   Atributo clase=regla.obtenerReglaPredicha();

   for(int i=0;i<muestras.size();i++){
     mt=(Muestra)muestras.get(i);
     if(mt.estaClase(clase)){
       lista.addElement(mt);
     }
   }


   muestras.removeAll(lista);

 }


  /**
   * Computes the percentage of examples of the dataset covered by the given rule.
   * @param regla {@link Regla} given rule to check with.
   * @param cVacias  Empty/Null conditions of the rule.
   * @return float he percentage of examples covered by the rule.
   */
  public float porcentajeMuestrasCubiertas(Regla regla, Vector cVacias){
    float porcentaje=0;
    float total=muestras.size();
    float cubiertas=0;
    Muestra mt;
    Vector eliminar=new Vector();

    for(int i=0;i<muestras.size();i++){
      mt=(Muestra)muestras.get(i);
      if(regla.cubreMuestra(mt,cVacias)){
        cubiertas++;
        eliminar.addElement(mt);
      }
    }


    muestras.removeAll(eliminar);
    porcentaje=cubiertas;
    return porcentaje;
  }

    /**
   * Returns all the examples in the dataset.
   * @return all the examples {@link Muestra} in the dataset.
   */
  public Vector obtenerMuestras(){
    Vector devolver=new Vector(muestras);
    return devolver;
  }

  /**
   * Returns the majority class of the dataset (most frequent class).
   * @param listaClases  Vector of existing classes.
   * @return int the position of the majority class (class id).
   */
  public int obtenerMayorClase(Vector listaClases){
    int [] ocurrencias=new int [listaClases.size()];
    Muestra mt;
    Atributo clase;
    Atributo original;
    int mejor=0;
    int posMejor=0;

    for(int i=0;i<listaClases.size();i++)
      ocurrencias[i]=0;

    for(int i=0;i<muestras.size();i++){
      mt=(Muestra)muestras.get(i);
      clase=mt.getClase();
      for(int j=0;j<listaClases.size();j++){
        original=(Atributo)listaClases.get(j);
        if(clase.esIgual(original))
          ocurrencias[j]++;
      }
    }

    for(int i=0;i<listaClases.size();i++)
      if(ocurrencias[i]>=mejor){
        mejor=ocurrencias[i];
        posMejor=i;
      }

    return posMejor;

  }

  /**
   * Returns the percentage of examples with the given class covered by the given condition. 
   * @param condicion Given condition to check.
   * @param clase Given class.
   * @return the percentage of examples with the given class covered by the given condition. 
   */
  public float porcentajeMuestrasCondicion(Condicion condicion,Atributo clase){
    Muestra mt;
    Atributo comprobar;
    Atributo at;
    int indice;
    float tamanio=0;
    float aciertos=0;
    float porcentaje;
    double valorOriginal;
    double valorCondicion;

    at=condicion.getValor();
    indice=at.getAtributo();

    for(int i=0;i<muestras.size();i++){
      mt=(Muestra)muestras.get(i);
      comprobar=mt.getValor(indice);//en muestra tb puede estar vacio!!!!!

      if(at.getValor().equals("Null") || comprobar.getValor().equals("Null"))
        tamanio++;
      else
        switch (condicion.getOperador()) {
          case 0: //=
            if (comprobar.esIgual(at)){
              tamanio++;
              if(mt.getClase().esIgual(clase))
                aciertos++;
            }
            break;
          case 1: //<
            valorOriginal = Double.parseDouble(comprobar.getValor());
            valorCondicion = Double.parseDouble(at.getValor());
            if (valorOriginal <= valorCondicion){
              tamanio++;
              if(mt.getClase().esIgual(clase))
                aciertos++;
            }
            break;
          case 2: //>
            valorOriginal = Double.parseDouble(comprobar.getValor());
            valorCondicion = Double.parseDouble(at.getValor());
            if (valorOriginal >= valorCondicion){
              tamanio++;
              if(mt.getClase().esIgual(clase))
                aciertos++;
            }
            break;
        }
      }


    if(aciertos==0)
      porcentaje=0;
    else
      porcentaje=aciertos/tamanio;
    return porcentaje;

  }

  /**
   * Returns the percentage of examples with the given class and the attribute as null.
   * @param vacio Given condition to check.
   * @param clase Given class.
   * @return the percentage of examples with the given class and the attribute as null.
   */
  public float porcentajeMuestrasVacias(Atributo vacio, Atributo clase){
    float porcentaje;
    float aciertos=0;
    float tamanio=0;
    Muestra mt;

    for(int i=0;i<muestras.size();i++){
      mt=(Muestra)muestras.get(i);
      if(mt.estaAtributo(vacio) && mt.estaClase(clase))
        aciertos++;
      if(mt.estaAtributo(vacio))
        tamanio++;
    }
    if(aciertos==0 || tamanio==0)
      porcentaje=0;
    else
      porcentaje=aciertos/tamanio;
    return porcentaje;
  }


  /**
   * Returns the percentage of examples with the given class.
   * @param clase Given class.
   * @return the percentage of examples with the given class.
   */
  public float porcentajeMuestrasClase(Atributo clase){
    int tamanio=muestras.size();
    Muestra mt;
    float aciertos=0;

    for(int i=0;i<tamanio;i++){
      mt=(Muestra)muestras.get(i);
      if(mt.estaClase(clase))
        aciertos++;
    }

    return aciertos/tamanio;

  }


   /**
   * Returns the percentage of examples for each class.
   * @param clases   Vector of existing classes.
   * @return the percentage of examples for each class.
   */
  public int [] porcentajeMuestrasClase(Vector clases){
   int tamanio=muestras.size();
   Atributo clase;
   Muestra mt;
   boolean parada=false;

   int [] aciertos = new int [clases.size()];
   for(int i=0;i<clases.size();i++)
     aciertos[i]=0;

   for(int i=0;i<tamanio;i++){
     mt=(Muestra)muestras.get(i);
     parada=false;
     for(int j=0;j<clases.size() && !parada;j++){
       clase=(Atributo)clases.get(j);
       if (mt.estaClase(clase)){
         aciertos[j]++;
         parada=true;
       }
     }
   }

   return aciertos;

 }

/**
 * Returns the number of examples covered by the conditions stored 
 * in the rule given.
 * @param hormiga Rule given.
 * @param cVacias Empty/Null conditions vector.
 * @return the number of examples covered by the conditions stored 
 * in the rule given.
 */
  public int numeroMuestrasCondiciones(Regla hormiga, Vector cVacias){
    int devolver = 0;
    Muestra actual;

    for (int i = 0; i < muestras.size(); i++) {
      actual = (Muestra) muestras.get(i);
      if (hormiga.cubreMuestraCondiciones(actual, cVacias))
        devolver++;
    }
    return devolver;
  }


/**
 * Returns the number of examples covered by the whole rule given 
 * (covered by the conditions and with the same class of the rule).
 * Returns the number of correctly covered examples.
 * @param hormiga Rule given.
 * @param cVacias Empty/Null conditions vector.
 * @return the number of examples covered by the whole rule given.
 */
  public int numeroMuestrasCubiertas(Regla hormiga, Vector cVacias){
    int devolver=0;
    Muestra actual;

    for(int i=0;i<muestras.size();i++){
      actual=(Muestra)muestras.get(i);
      if(hormiga.cubreMuestra(actual,cVacias))
        devolver++;
    }
    return devolver;
  }


/**
 * Checks if the rule covers the minimum examples to be considered.
 * @param hormiga Rule to be check.
 * @param cVacias Empty/Null conditions vector.
 * @param minimoCasosRegla Minimum number of covered example.
 * @return True if the rule covers the minimum examples to be considered.
 */
  public boolean cubreMinimo(Regla hormiga,Vector cVacias,int minimoCasosRegla){
    Muestra actual;
    int cubiertas=0;

    for(int i=0;i<muestras.size();i++){
      actual=(Muestra)muestras.get(i);
      if(hormiga.cubreMuestra(actual,cVacias))
        cubiertas++;
      if(cubiertas>=minimoCasosRegla)
        return true;
    }

    return false;

  }


}

