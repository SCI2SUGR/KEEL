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
 * <p>Title: Algorithm</p>
 *
 * <p>Description: It contains the implementation of the algorithm</p>
 *
 *
 * <p>Company: KEEL </p>
 *
 * @author Alberto Fernández
 * @version 1.0
 */

import java.io.IOException;
import org.core.*;
import java.util.Arrays;

public class OCEC {

  public static int MIGRATING = 0;
  public static int EXCHANGING = 1;
  public static int MERGING = 2;

  myDataset train, val, test;
  String outputTr, outputTst, ficheroBR;
  int nClasses, nGenerations, N, n;
  Poblacion[] p;
  Attribute a;
  BaseR baseReglas;

  private boolean somethingWrong = false; //to check if everything is correct.

  /**
   * Default constructor
   */
  public OCEC() {
  }

  /**
   * It reads the data from the input files (training, validation and test) and parse all the parameters
   * from the parameters array.
   * @param parameters parseParameters It contains the input files, output files and parameters
   */
  public OCEC(parseParameters parameters) {

    train = new myDataset();
    val = new myDataset();
    test = new myDataset();
    try {
      System.out.println("\nReading the training set: " +
                         parameters.getTrainingInputFile());
      train.readClassificationSet(parameters.getTrainingInputFile(), true);
      System.out.println("\nReading the validation set: " +
                         parameters.getValidationInputFile());
      val.readClassificationSet(parameters.getValidationInputFile(), false);
      System.out.println("\nReading the test set: " +
                         parameters.getTestInputFile());
      test.readClassificationSet(parameters.getTestInputFile(), false);
    }
    catch (IOException e) {
      System.err.println(
          "There was a problem while reading the input data-sets: " +
          e);
      somethingWrong = true;
    }

    //We may check if there are some numerical attributes, because our algorithm may not handle them:
    //somethingWrong = somethingWrong || train.hasRealAttributes();
    //somethingWrong = somethingWrong || train.hasMissingAttributes();

    //si hay valores reales empleamos una discretizacion uniforme en anchura (5)
    //if (train.hasRealAttributes()) {
    if (train.hasNumericalAttributes()) {
      train.discretize(5);
      val.discretize(5);
      test.discretize(5);
    }

    outputTr = parameters.getTrainingOutputFile();
    outputTst = parameters.getTestOutputFile();

    ficheroBR = parameters.getOutputFile(0);

    //Now we parse the parameters
    long semilla = Long.parseLong(parameters.getParameter(0));
    nGenerations = Integer.parseInt(parameters.getParameter(1));
    //N = Integer.parseInt(parameters.getParameter(2));
    n = Integer.parseInt(parameters.getParameter(2));
    N = (int) (0.1 * train.size()); //10% of the examples
    if (N > train.sumMinorityClasses()){
      N = train.sumMinorityClasses();
    }

    Randomize.setSeed(semilla);

  }

  /**
   * It launches the algorithm
   */
  public void execute() {
    if (somethingWrong) { //We do not execute the program
      System.err.println("An error was found, either the data-set have numerical values or missing values.");
      System.err.println("Aborting the program");
      //We should not use the statement: System.exit(-1);
    }
    else {
      //We do here the algorithm's operations

      nClasses = train.getnClasses();
      a = new Attribute(train.nombres());
      p = new Poblacion[nClasses];
      for (int i = 0; i < nClasses; i++) {
        p[i] = new Poblacion(i, a, train);
      }

      baseReglas = algoritmo();
      baseReglas.printFichero(this.ficheroBR);

      //Finally we should fill the training and test output files
      double accTr = doOutput(this.val, this.outputTr);
      double accTst = doOutput(this.test, this.outputTst);

      System.out.println("Number of Rules: " + baseReglas.size());
      System.out.println("Accuracy in training: " + accTr);
      System.out.println("Accuracy in test: " + accTst);
      System.out.println("Algorithm Finished");
    }
  }

  /**
   * It generates the output file from a given dataset and stores it in a file
   * @param dataset myDataset input dataset
   * @param filename String the name of the file
   * @return the Accuracy of the classifier
   */
  private double doOutput(myDataset dataset, String filename) {
    String output = new String("");
    output = dataset.copyHeader(); //we insert the header in the output file
    int aciertos = 0;
    //We write the output for each example
    for (int i = 0; i < dataset.getnData(); i++) {
      //for classification:
      String claseReal = dataset.getOutputAsString(i);
      String prediccion = this.classificationOutput(dataset.getExample(i));
      output += claseReal + " " + prediccion + "\n";
      if (claseReal.equalsIgnoreCase(prediccion)) {
        aciertos++;
      }
    }
    Fichero.escribeFichero(filename, output);
    return (1.0 * aciertos / dataset.size());
  }

  /**
   * It returns the algorithm classification output given an input example
   * @param example double[] The input example
   * @return String the output generated by the algorithm
   */
  private String classificationOutput(double[] example) {
    return baseReglas.clasifica(example);
  }

  private BaseR algoritmo() {
    BaseR baseReglas = new BaseR(train);
    Poblacion nueva = new Poblacion();
    for (int i = 0; i < nGenerations; i++) {
      //System.out.print("Generation[" + i + "]: " + a.printString());
      for (int j = 0; j < p.length; j++) { //para todas las poblaciones
        //System.out.println("Working with population P[" + j + "]: "+p[j].size());
        //p[j].print();
        while (p[j].size() > 1) { //Hay más de una organizacion
          //System.out.println("Working with population P[" + j + "]: "+p[j].size());
          //p[j].print();
          int aleat1, aleat2;
          aleat1 = Randomize.RandintClosed(0, p[j].size()-1);
          do {
            aleat2 = Randomize.RandintClosed(0, p[j].size()-1);
          }
          while (aleat2 == aleat1);
          Organizacion org1 = p[j].dameOrganizacion(aleat1);
          Organizacion org2 = p[j].dameOrganizacion(aleat2);
          int operador = Randomize.RandintClosed(0, 2); //uno entre tres
          //compruebo si elijo migrar cuando en verdad sera mezclar (porque solo hay un miembro)
          if ( (operador == this.MIGRATING) && (org1.miembros.length == 1)) {
            operador = this.MERGING;
          }
          if (operador == this.MIGRATING) {
            Organizacion orgc1 = new Organizacion(a, train);
            Organizacion orgc2 = new Organizacion(a, train);
            migrar(org1, org2, orgc1, orgc2);
            calculaSignificancia(orgc1, j);
            calculaSignificancia(orgc2, j);
            orgc1.calculaFitness();
            orgc2.calculaFitness();
            if (Math.max(orgc1.fitness, orgc2.fitness) >
                Math.max(org1.fitness, org2.fitness)) {
              //borrar ambos padres y meter los hijos
              if (orgc1.tipo == Organizacion.ANORMAL) {
                //es ANORMAL y tengo que generar organizaciones triviales a partir de esta
                generarTriviales(nueva, orgc1); //genero e inserto
              }
              else {
                nueva.organizaciones.add(orgc1);
              }
              if (orgc2.tipo == Organizacion.ANORMAL) {
                //es ANORMAL y tengo que generar organizaciones triviales a partir de esta
                generarTriviales(nueva, orgc2); //genero e inserto
              }
              else {
                nueva.organizaciones.add(orgc2);
              }
            }
            else { //mantener ambos padres en otro caso
              nueva.organizaciones.add(org1.copia());
              nueva.organizaciones.add(org2.copia());
            }
            //Borro los dos padres
            p[j].organizaciones.remove(org1);
            p[j].organizaciones.remove(org2);
          }
          else if (operador == this.MERGING) { //merging
            //System.out.println("Merging operator");
            Organizacion orgc = new Organizacion(org1, org2); //mezcla de ambos
            calculaSignificancia(orgc, j);
            orgc.calculaFitness();
            //Como solo hay un hijo, pasa directamente a la siguiente generacion
            if (orgc.tipo == Organizacion.ANORMAL) {
              //es ANORMAL y tengo que generar organizaciones triviales a partir de esta
              generarTriviales(nueva, orgc); //genero e inserto
            }
            else {
              nueva.organizaciones.add(orgc);
            }
            //Borro los dos padres
            p[j].organizaciones.remove(org1);
            p[j].organizaciones.remove(org2);
          }
          else if ( (operador == this.EXCHANGING) &&
                   ( (org1.miembros.length > 1) ||
                    (org2.miembros.length > 1))) {
            //System.out.println("Exchanging operator");
            Organizacion orgc1 = new Organizacion(a, train);
            Organizacion orgc2 = new Organizacion(a, train);
            intercambiar(org1, org2, orgc1, orgc2);
            calculaSignificancia(orgc1, j);
            calculaSignificancia(orgc2, j);
            orgc1.calculaFitness();
            orgc2.calculaFitness();
            if (Math.max(orgc1.fitness, orgc2.fitness) >
                Math.max(org1.fitness, org2.fitness)) {
              //Inserto los hijos
              if (orgc1.tipo == Organizacion.ANORMAL) {
                //es ANORMAL y tengo que generar organizaciones triviales a partir de esta
                generarTriviales(nueva, orgc1); //genero e inserto
              }
              else {
                nueva.organizaciones.add(orgc1);
              }
              if (orgc2.tipo == Organizacion.ANORMAL) {
                //es ANORMAL y tengo que generar organizaciones triviales a partir de esta
                generarTriviales(nueva, orgc2); //genero e inserto
              }
              else {
                nueva.organizaciones.add(orgc2);
              }
            }
            else { //mantener ambos padres en otro caso
              nueva.organizaciones.add(org1.copia());
              nueva.organizaciones.add(org2.copia());
            }
            //Borro los dos padres
            p[j].organizaciones.remove(org1);
            p[j].organizaciones.remove(org2);
          }
        } //while (hay mas de una organizacion)
        //Actualizo la poblacion insertando todas las generadas:
        p[j].actualiza(nueva);
        nueva.limpia();
      }
    }
    for (int i = 0; i < p.length; i++) {
      p[i].eliminarNoUtiles();
      p[i].mezclar(); //junta dos organizaciones si los atributos útiles de una estan contenidos en la otra (y tienen
      //lo mismos valores, obviamente.
      baseReglas.incluir(p[i]); //construyo y almaceno las reglas de p[i]
    }
    baseReglas.ordenar();
    baseReglas.eliminarSubsumidas();
    System.out.println("Base de Reglas: " + baseReglas.printString());
    return baseReglas;
  }

  private void migrar(Organizacion padre, Organizacion madre,
                      Organizacion hijo1, Organizacion hijo2) {
    hijo1.clase = padre.clase;
    hijo2.clase = padre.clase;
    int n_ = Math.min(n, padre.miembros.length - 1); //del padre a la madre
    int[] miembros = new int[n_];
    int contador = 0;
    //Selecciono la posicion de los miembros del padre que voy a migrar
    do {
      int aleat;
      boolean condicion = false;
      do {
        aleat = Randomize.RandintClosed(0, padre.miembros.length-1); //elijo un miembro (posicion)
        for (int i = 0; (i < contador) && (!condicion); i++) {
          condicion = (miembros[i] == aleat); //si ya lo he elegido no me vale
        }
      }
      while (condicion);
      miembros[contador] = aleat; //guardo el miembro escogido
      contador++;
    }
    while (contador < n_);
    contador = 0;

    Arrays.sort(miembros); //ordeno las posiciones (ej: {2, 3, 0} -> {0, 2, 3}
    //Primero elimino los migrados del hijo1
    int contador2 = 0;
    hijo1.miembros = new int[padre.miembros.length - n_];
    for (int i = 0; i < padre.miembros.length; i++) {
      boolean tomarValor = true;
      for (int j = contador2; j < miembros.length; j++) {
        tomarValor = tomarValor && (i != miembros[j]);
      }
      if (tomarValor) {
        hijo1.miembros[contador] = padre.miembros[i];
        contador++;
      }
      else {
        contador2++;
      }
    }

    //Ahora migro al hijo2
    hijo2.miembros = new int[madre.miembros.length + n_];
    //copio todos los de la madre
    for (int i = 0; i < madre.miembros.length; i++) {
      hijo2.miembros[i] = madre.miembros[i];
    }
    //y ahora los migrados del padre
    for (int i = madre.miembros.length; i < madre.miembros.length + n_; i++) {
      hijo2.miembros[i] = padre.miembros[miembros[i - madre.miembros.length]];
    }
  }

  private void intercambiar(Organizacion padre, Organizacion madre,
                            Organizacion hijo1, Organizacion hijo2) {
    hijo1.clase = padre.clase;
    hijo2.clase = padre.clase;
    int n_ = Math.min(n,
                      (int) Math.min(padre.miembros.length,
                                     madre.miembros.length));
    //puedo intercambiar n ó, el minimo de miembros (padre ó madre)
    int[] miembrosP = new int[n_]; //posiciones de los miembros a intercambiar
    int[] miembrosM = new int[n_]; //posiciones de los miembros a intercambiar
    int contador = 0;
    do {
      int aleat1, aleat2;
      boolean condicion = false;
      do {
        aleat1 = Randomize.RandintClosed(0, padre.miembros.length-1); //elijo un miembro (posicion)
        for (int i = 0; (i < contador) && (!condicion); i++) {
          condicion = (miembrosP[i] == aleat1); //si ya lo he elegido no me vale
        }
      }
      while (condicion);
      condicion = false;
      do {
        aleat2 = Randomize.RandintClosed(0, madre.miembros.length-1); //elijo un miembro (posicion)
        for (int i = 0; (i < contador) && (!condicion); i++) {
          condicion = (miembrosM[i] == aleat2); //si ya lo he elegido no me vale
        }
      }
      while (condicion);
      miembrosP[contador] = aleat1; //guardo el miembro escogido del padre
      miembrosM[contador] = aleat2; //guardo el miembro escogido de la madre
      contador++;
    }
    while (contador < n_);
    contador = 0;

    //Primero copio todos
    hijo1.miembros = new int[padre.miembros.length];
    for (int i = 0; i < padre.miembros.length; i++) {
      hijo1.miembros[i] = padre.miembros[i];
    }
    hijo2.miembros = new int[madre.miembros.length];
    for (int i = 0; i < madre.miembros.length; i++) {
      hijo2.miembros[i] = madre.miembros[i];
    }
    //y ahora los intercambio
    for (int i = 0; i < miembrosP.length; i++) {
      int temp = hijo2.miembros[miembrosM[i]]; //saco el miembro de la madre
      hijo2.miembros[miembrosM[i]] = hijo1.miembros[miembrosP[i]]; //le pongo el miembro del padre
      hijo1.miembros[miembrosP[i]] = temp; //copio el miembro de la madre
    }
  }

  private void calculaSignificancia(Organizacion org, int pobl) {
    org.determinaForg(); //Determinar los atributos fijos (los que toman el mismo valor para todos los miembros)
    org.limpiaUtiles(); //Uorg = conjunto vacio (todos a false)
    for (int i = 0; i < org.Forg.length; i++) { //para todos los atributos fixed
      if (org.Forg[i]) { //Es un atributo Fixed
        int aleat;
        do {
          aleat = Randomize.RandintClosed(0, p.length-1);
        }
        while ((aleat == pobl)||(p[aleat].size() == 0));
        Organizacion orgPrima = p[aleat].dameOrganizacion(Randomize.
            RandintClosed(
                0, p[aleat].size()-1)); //org con clase !=
        if (orgPrima.estaYdistinto(i, org.ForgValue[i])) {
          org.addU(i); //añadir a Uorg
        }
        else {
          a.reducir(i); //reducir S_A_j
        }
      }
    }
    org.actualizarUorg(N);
  }

  private void generarTriviales(Poblacion p, Organizacion org) {
    int miembros = org.miembros.length;
    //System.err.println("anormal");
    for (int i = 0; i < miembros; i++) {
      Organizacion nuevaOrg = new Organizacion(org.miembros[i], a, train);
      p.organizaciones.add(nuevaOrg);
    }
  }

}

