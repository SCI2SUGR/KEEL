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

/*
 * Created on 20-jul-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.core.*;

/**
 * @author Sebas
 *
 */
public class Hider {
  private int aciertos;
  private int populationSize;
  private int nGenerations;
  private int mayoritaria; //majority class for the default rule
  private double crossPercent;
  private double mutationProbability;
  private double extremeMutationProbability;
  private double pruneExamplesFactor;
  private double errorCoeficient;
  private double penaltyFactor;
  private Vector rules;
  private Vector rulesErr;
  private Vector resultadosTra;
  private Vector resultadosTst;
  private double coefErrTst;
  private double coefErrTra;
  private String cadenaRules;
  private String fichEntTra;
  private String fichOrigTra;
  private String fichEntTst;
  private String fichFunc;
  private String fichSalTra;
  private String fichSalTst;
  private String fichSalRul;
  private long seed;

  /**
   * Empty constructor
   */
  public Hider() {
    aciertos = 0;
    populationSize = 50;
    nGenerations = 15;
    crossPercent = 80;
    mutationProbability = 0.5;
    extremeMutationProbability = 0.05;
    pruneExamplesFactor = 0;
    penaltyFactor = 1;
    errorCoeficient = 0;

    rules = null;
    rulesErr = null;
    resultadosTra = null;
    resultadosTst = null;
    coefErrTst = 0;
    coefErrTra = 0;
    cadenaRules = "";
    fichEntTra = null;
    fichOrigTra = null;
    fichEntTst = null;
    fichFunc = null;
    fichSalTra = null;
    fichSalTst = null;
    fichSalRul = null;
  }

  /**
   * Constructor with configuration file name parameter
   * @param fichParamName
   * @throws Exception
   */
  public Hider(String fichParamName) throws Exception {
    this();

    FileInputStream fis;
    Properties p = new Properties();

    fis = new FileInputStream(fichParamName);
    p.load(fis);
    fis.close();

    Enumeration en = p.elements();
    Enumeration enk = p.keys();
    while (enk.hasMoreElements()) {
      String key = (String) enk.nextElement();
      String value = (String) en.nextElement();

      String[] aux = value.split(" ");
      if (key.equals("inputData")) {
        if (aux.length > 2) { //Can be 3 or 4
          if (aux.length > 3) {
            fichEntTra = aux[0].replaceAll("\"", "").trim();
            fichOrigTra = aux[1].replaceAll("\"", "").trim();
            fichEntTst = aux[2].replaceAll("\"", "").trim();
            fichFunc = aux[3].replaceAll("\"", "").trim();
          }
          else { //lenght == 3; aux[2] can be 'fichEntTst' or 'fichFunc'
            if (aux[2].replaceAll("\"", "").trim().toLowerCase().
                endsWith(".xml")) { //it's fichFunc
              fichOrigTra = aux[0].replaceAll("\"", "").trim();
              fichEntTst = aux[1].replaceAll("\"", "").trim();
              fichFunc = aux[2].replaceAll("\"", "").trim();
            }
            else { //it's fichEntTst
              fichEntTra = aux[0].replaceAll("\"", "").trim();
              fichOrigTra = aux[1].replaceAll("\"", "").trim();
              fichEntTst = aux[2].replaceAll("\"", "").trim();
            }
          }
        }
        else {
          fichOrigTra = aux[0].replaceAll("\"", "").trim();
          fichEntTst = aux[1].replaceAll("\"", "").trim();
        }
      }
      else if (key.equals("outputData")) {
        fichSalTra = aux[0].replaceAll("\"", "").trim();
        fichSalTst = aux[1].replaceAll("\"", "").trim();
        fichSalRul = aux[2].replaceAll("\"", "").trim();
      }
      else if (key.equals("seed")) {
        seed = Long.parseLong(value.trim());
      }
      else if (key.equals("populationSize")) {
        populationSize = Integer.parseInt(value.trim());
      }
      else if (key.equals("nGenerations")) {
        nGenerations = Integer.parseInt(value.trim());
      }
      else if (key.equals("mutationProbability")) {
        mutationProbability = Double.parseDouble(value.trim());
      }
      else if (key.equals("crossPercent")) {
        crossPercent = Integer.parseInt(value.trim());
      }
      else if (key.equals("extremeMutationProbability")) {
        extremeMutationProbability = Double.parseDouble(value.trim());
      }
      else if (key.equals("pruneExamplesFactor")) {
        pruneExamplesFactor = Double.parseDouble(value.trim());
      }
      else if (key.equals("penaltyFactor")) {
        penaltyFactor = Integer.parseInt(value.trim());
      }
      else if (key.equals("errorCoeficient")) {
        errorCoeficient = Double.parseDouble(value.trim());
      }
    }

    Randomize.setSeed(seed);

    if (fichEntTra != null) { //Exists a preprocessed version of the training file
      procesaBaseDatos(fichEntTra);
    }
    else {
      procesaBaseDatos(fichOrigTra);
    }

    //Dataset dsTest = new Dataset(fichEntTst);
    Dataset dsTest = new Dataset();
    dsTest.readClassificationSet(fichEntTst, false);
    limpiaRuido(dsTest);
    testea(dsTest, true); //For the test file...
    escribeFicherosSalida(BaseDatos.obtieneCabecera(dsTest));
  }

  /**
   * Process the database file 'filename'
   * @param filename
   * @throws Exception
   */
  public void procesaBaseDatos(String filename) throws Exception {
    //Dataset ds = new Dataset(filename);
    Dataset ds = new Dataset();
    ds.readClassificationSet(filename, true);

    if (fichEntTra == null) { //Not exists preprocessed file
      limpiaRuido(ds);
    }

    BaseDatos bd = new BaseDatos(ds);

    Discretizacion d = new Discretizacion(bd);
    d.discretiza();

    Codificacion.setCortesCod(d.getCortes());
    Codificacion cod = new Codificacion();
    cod.codificaBase(bd);
    Vector bc = cod.getBaseCodificada();
    Evolucion ev = new Evolucion(bc, mutationProbability, fichFunc);
    ev.setProbMutExtremo(extremeMutationProbability);
    ev.setCep(errorCoeficient);

    this.rules = new Vector();
    this.rulesErr = new Vector();
    this.resultadosTra = new Vector();
    this.resultadosTst = new Vector();
    int n = bc.size();
    double limit = Discretizacion.multiplica(pruneExamplesFactor, n); //Pruning
    aciertos = 0;
    int ejemplos = bc.size();
    int intentos = 0;
    while ( (bc.size() > limit) && (intentos < 5)) {
      System.out.println("Executing Evolutionary Learning. " + bc.size() +
                         " examples remaining (until " + limit +
                         " limit examples)");
      ev = new Evolucion(bc, this.mutationProbability, fichFunc);
      ev.setProbMutExtremo(extremeMutationProbability);
      ev.setCep(errorCoeficient);

      Vector re = ev.algoritEvol(this.populationSize, this.nGenerations,
                                 this.crossPercent, d.getCortes(),
                                 bd.getRangos(), this.penaltyFactor);

      //Delete from bc every element covered by the rule...
      this.eliminaCubiertos(bc, (Vector) re.get(0),
                            d.getCortes() /*, bd*/);
      if (bc.size() == ejemplos) {
        intentos++;
      }
      else {
        ejemplos = bc.size();
        this.rules.add(re.get(0)); //Adds the rule
        this.rulesErr.add(re.get(1)); //Adds the error for the rule (type Entero[2])
      }
    }
    /****** NUEVO ********/
    //Añado la regla por defecto --> la clase mayoritaria de los ejemplos restantes
    int[] ejemplos_clase = new int[ds.getnClasses()];
    mayoritaria = 0;
    int total = 0;

    if (bc.size() > 0) {
      Vector ejemplo = (Vector) bc.get(0);
      int numAtributos = ejemplo.size();
      for (int i = 0; i < bc.size(); i++) {
        ejemplo = (Vector) bc.get(i);
        int clase = ( (Long) ejemplo.get(numAtributos - 1)).intValue();
        ejemplos_clase[clase]++;
      }
      total = ejemplos_clase[0];
      for (int i = 1; i < ejemplos_clase.length; i++) {
        if (ejemplos_clase[mayoritaria] < ejemplos_clase[i]) {
          mayoritaria = i;
        }
        total += ejemplos_clase[i];
      }
    }
    //Evaluate the original training file
    //Dataset dsTra = new Dataset(fichOrigTra);
    Dataset dsTra = new Dataset();
    dsTra.readClassificationSet(fichOrigTra, false);
    limpiaRuido(dsTra); //Replace "?"'s to valid values
    testea(dsTra, false); //For training file...

    System.out.println("The End");

    for (int i = 0; i < this.rules.size(); i++) {
      String cadena = "\nif ";
      Vector regla = (Vector)this.rules.get(i);
      Entero[] a_ec = (Entero[]) rulesErr.get(i);

      for (int j = 0; j < d.getCortes().size() - 2; j++) { //Look through current rule
        int k = 0;
        if (d.getCortes().get(j) instanceof Vector) { //If it is a continuous attribute...
          k = ( (Vector) d.getCortes().get(j)).size();
          Long num = (Long) regla.get(j);
          int fila = Codificacion.descodificaF(num.intValue(), k);
          int columna = Codificacion.descodificaC(num.intValue(), k);
          double ini = ( (Corte) ( (Vector) d.getCortes().get(j)).get(
              fila)).getCorte();
          double fin = ( (Corte) ( (Vector) d.getCortes().get(j)).get(
              columna + 1)).getCorte();

          if (ini != ( (Double) bd.getInicial().get(j)).doubleValue() ||
              fin != ( (Double) bd.getFinales().get(j)).doubleValue()) {
            if (!cadena.equals("\nif ")) {
              cadena += " and\n   ";
            }

            cadena += bd.getNombres().get(j) + " = [";
            if (ini !=
                ( (Double) bd.getInicial().get(j)).doubleValue()) {
              cadena += ini;
            }
            else {
              cadena += "_";
            }

            if (fin !=
                ( (Double) bd.getFinales().get(j)).doubleValue()) {
              cadena += ", " + fin + ")";
            }
            else {
              cadena += ", _)";
            }
          }
        }
        else { //If it is a discrete attribute...
          k = ( (Integer) d.getCortes().get(j)).intValue();
          Long num = (Long) regla.get(j);
          int[] valores = Codificacion.descodificaD(num.intValue(), k,
              ( (Integer) bd.getInicial().get(j)).intValue());

          boolean primero = true;

          if (valores.length <
              ( (Integer) bd.getRangos().get(j)).intValue()) {
            if (!cadena.equals("\nif ")) {
              cadena += " and\n   ";
            }

            for (int v = 0; v < valores.length; v++) {
              if (valores[v] >= 0) {
                if (primero) {
                  if (bd.getTipos().get(j).equals("enumerado")) {
                    cadena += bd.getNombres().get(j) +
                        " = @#" +
                        ( (Vector) bd.getValEnum().get(j)).
                        get(v);
                  }
                  else {
                    cadena += bd.getNombres().get(j) +
                        " = @#" + valores[v];
                  }
                  primero = false;
                }
                else
                if (bd.getTipos().get(j).equals("enumerado")) {
                  cadena += ", " +
                      ( (Vector) bd.getValEnum().get(j)).
                      get(v);
                }
                else {
                  cadena += ", " + valores[v];
                }
              }
            } //for(valores...)

            cadena += "#@";
            if (valores.length > 1) {
              cadena = cadena.replaceFirst("@#", "{");
              cadena = cadena.replaceFirst("#@", "}");
            }
            else {
              cadena = cadena.replaceFirst("@#", "");
              cadena = cadena.replaceFirst("#@", "");
            }
          }
        }
      } //for(current rule...)

      //Last attribute of the element
      if (d.getCortes().get(d.getCortes().size() - 2) instanceof Vector) { //If it is a continuous attribute...
        int k = ( (Vector) d.getCortes().get(d.getCortes().size() - 2)).
            size();
        Long num = (Long) regla.get(d.getCortes().size() - 2);
        int fila = Codificacion.descodificaF(num.intValue(), k);
        int columna = Codificacion.descodificaC(num.intValue(), k);
        double ini = ( (Corte) ( (Vector) d.getCortes().get(d.getCortes().
            size() - 2)).get(fila)).getCorte();
        double fin = ( (Corte) ( (Vector) d.getCortes().get(d.getCortes().
            size() - 2)).get(columna + 1)).getCorte();

        if (ini !=
            ( (Double) bd.getInicial().get(d.getCortes().size() - 2)).
            doubleValue() ||
            fin !=
            ( (Double) bd.getFinales().get(d.getCortes().size() - 2)).
            doubleValue()) {
          if (!cadena.equals("\nif ")) {
            cadena += " and\n   ";
          }
          cadena += bd.getNombres().get(d.getCortes().size() - 2) +
              " = [";
          if (ini !=
              ( (Double) bd.getInicial().get(d.getCortes().size() - 2)).
              doubleValue()) {
            cadena += ini;
          }
          else {
            cadena += "_";
          }

          if (fin !=
              ( (Double) bd.getFinales().get(d.getCortes().size() - 2)).
              doubleValue()) {
            cadena += ", " + fin + ")";
          }
          else {
            cadena += ", _)";
          }
        }
      }
      else { //If it is a discrete attribute...
        int k = ( (Integer) d.getCortes().get(d.getCortes().size() - 2)).
            intValue();
        Long num = (Long) regla.get(d.getCortes().size() - 2);
        int[] valores = Codificacion.descodificaD(num.intValue(), k,
                                                  ( (Integer) bd.getInicial().
            get(d.getCortes().size() - 2)).
                                                  intValue());

        boolean primero = true;

        if (valores.length <
            ( (Integer) bd.getRangos().get(d.getCortes().size() - 2)).
            intValue()) {
          if (!cadena.equals("\nif ")) {
            cadena += " and\n   ";
          }

          for (int v = 0; v < valores.length; v++) {
            if (valores[v] >= 0) {
              if (primero) {
                if (bd.getTipos().get(d.getCortes().size() - 2).
                    equals("enumerado")) {
                  cadena +=
                      bd.getNombres().get(d.getCortes().
                                          size() -
                                          2) + " = @#" +
                      ( (Vector) bd.
                       getValEnum().get(d.getCortes().
                                        size() - 2)).get(v);
                }
                else {
                  cadena +=
                      bd.getNombres().get(d.getCortes().
                                          size() -
                                          2) + " = @#" + valores[v];
                }

                primero = false;
              }
              else
              if (bd.getTipos().get(d.getCortes().size() - 2).
                  equals("enumerado")) {
                cadena += ", " +
                    ( (Vector) bd.getValEnum().get(d.
                    getCortes().size() - 2)).get(v);
              }
              else {
                cadena += ", " + valores[v];
              }
            }
          }
          cadena += "#@";

          if (valores.length > 1) {
            cadena = cadena.replaceFirst("@#", "{");
            cadena = cadena.replaceFirst("#@", "}");
          }
          else {
            cadena = cadena.replaceFirst("@#", "");
            cadena = cadena.replaceFirst("#@", "");
          }
        }
      }
      if (cadena.equals("\nif ")) {
        cadena += "true";
      }

      cadena += "\nthen\n   " + bd.getNombres().get(bd.getClase()) +
          " = ";

      if ( ( (String) bd.getTipos().get(bd.getClase())).equals("enumerado")) {
        if (bd.getValEnum().get(bd.getClase()) != null) {
          int clase_regla = ( (Long) regla.get(bd.getClase())).intValue();
          int clase = bd.getClase();
          Vector nombre_clases = (Vector) bd.getValEnum().get(clase);
          cadena += (String) (nombre_clases).get(clase_regla);
        }
      }
      //The output is always discrete so we don't need this...
      /*
       else if(((String)bd.getTipos().get(bd.getClase())).equals("real"))
          {
       cadena+=((Double)regla.get(bd.getClase())).doubleValue();
          }
       */
      else if ( ( (String) bd.getTipos().get(bd.getClase())).equals(
          "integer")) {
        cadena +=
            ( ( (Long) regla.get(bd.getClase())).longValue() +
             ( (Integer) bd.getInicial().get(bd.getClase())).
             intValue());
      }

      //Now, add classification error percent
      cadena += " (" + a_ec[1].getValor() + "|" + a_ec[0].getValor() +
          ")";

      cadenaRules += cadena + "\n";

    } //for(rules...)
    if (bc.size() > 0) {
      cadenaRules += "\nif true";
      cadenaRules += "\nthen\n   " + bd.getNombres().get(bd.getClase()) +
          " = " + ds.getOutputValue(mayoritaria);
      cadenaRules += " (" + ejemplos_clase[mayoritaria] + "|" +
          (total - ejemplos_clase[mayoritaria]) +
          ")";
    }
  }

  /**
   * Treat undefined data from the database
   * @param dsTra
   */
  private void limpiaRuido(Dataset dsTra) {
    Object[] valores = new Object[dsTra.getNvariables()];
    int[] cont = new int[dsTra.getNvariables()]; //Counters for valid values (not '?')
    HashMap[] saco = new HashMap[dsTra.getNvariables()];
    int[] frecMax = new int[dsTra.getNvariables()];

    for (int j = 0; j < dsTra.getNvariables(); j++) {
      //Initializing valores
      if (dsTra.getTiposIndex(j).equals("real")) { //Continuous
        valores[j] = new Double(0);
      }
      else { //Discrete
        valores[j] = null;
      }
      //Initializing cont
      cont[j] = 0;

      //Initializing saco
      saco[j] = new HashMap();

      //Initializing frecMax
      frecMax[j] = 0;
    }
    for (int i = 0; i < dsTra.getNdatos(); i++) {
      for (int j = 0; j < dsTra.getNvariables(); j++) {
        if (dsTra.getTiposIndex(j).equals("real")) { //Continuous...
          if (!dsTra.isMissing(i, j)) {
            cont[j]++;
            valores[j] = new Double(Discretizacion.suma( ( (Double)
                valores[j]).doubleValue(),
                Double.parseDouble(dsTra.getDatosIndex(i, j))));
          }
        }
        else { //Discrete...
          if (!dsTra.isMissing(i, j)) {
            if (saco[j].containsKey(dsTra.getDatosIndex(i, j))) {
              Entero frec = new Entero( ( (Entero) saco[j].get(
                  dsTra.getDatosIndex(i, j))).getValor() + 1);
              if (frec.getValor() > frecMax[j]) {
                frecMax[j] = frec.getValor();
                valores[j] = dsTra.getDatosIndex(i, j);
              }
              saco[j].put(dsTra.getDatosIndex(i, j), frec);
            }
            else {
              if (frecMax[j] < 1) {
                frecMax[j] = 1;
                valores[j] = dsTra.getDatosIndex(i, j);
              }
              saco[j].put(dsTra.getDatosIndex(i, j), new Entero(1));
            }
          }
        }
      }
    }

    for (int i = 0; i < dsTra.getNdatos(); i++) {
      for (int j = 0; j < dsTra.getNvariables(); j++) {
        if (dsTra.getDatosIndex(i, j).equals("?") &&
            dsTra.getDatosIndex(i, j).equals("<null>")) {
          if (dsTra.getTiposIndex(j).equals("real")) { //Continuous
            //dsTra.getDatosIndex(i).set(j,"" +Discretizacion.divide(((Double) valores[j]).doubleValue(),cont[j]));
            dsTra.ponValor(i, j,
                           "" +
                           Discretizacion.divide( ( (Double) valores[
                j]).doubleValue(), cont[j]));
          }
          else { //Discrete
            dsTra.ponValor(i, j, (String) valores[j]); //dsTra.getDatosIndex(i).set(j, valores[j]);
          }
        }
      }
    }
  }

  /**
   * Deletes elements covered by the rule 'r'
   * @param bc
   * @param r
   * @param cortes
   */
  private void eliminaCubiertos(Vector bc, Vector r,
                                Vector cortes /*, BaseDatos bd*/) {
    //int claseRegla=((Long)r.get(bd.getClase())).intValue();
    //int claseEjemplo=0;
    //int cont=0;

    //Look through the elements...
    for (int i = 0; i < bc.size(); i++) {
      Vector ejemploActual = (Vector) bc.get(i);

      boolean cubierto = true; //If any attribute is not covered then the rule will not cover the element
      //Look through attributes...
      for (int j = 0; j < cortes.size() - 1 && cubierto; j++) { //Only count input attributes...
        int nr = ( (Long) r.get(j)).intValue();
        int ne = ( (Long) ejemploActual.get(j)).intValue();

        int k = 0;
        if (cortes.get(j) instanceof Integer) { //Discrete
          k = ( (Integer) cortes.get(j)).intValue();

          if ( (nr & ne) != 0) {
            cubierto = true;
          }
          else {
            cubierto = false;
          }
        }
        else { //Continuous
          k = ( (Vector) cortes.get(j)).size();

          int fr = Codificacion.descodificaF(nr, k);
          int fe = Codificacion.descodificaF(ne, k);

          int cr = Codificacion.descodificaC(nr, k);
          int ce = Codificacion.descodificaC(ne, k);

          if ( (fr <= fe) && (cr >= ce)) {
            cubierto = true;
          }
          else {
            cubierto = false;
          }
        }

      } //for (attributes...)
      //If the rule covers all attributes...
      if (cubierto) {
        //Before deleting element take output value...
        //claseEjemplo=((Long)ejemploActual.get(bd.getClase())).intValue();
        /*
            if(claseEjemplo == claseRegla)
            {
             aciertos++;
            }
         */
//Decode values...
        /*
            if(bd.getTiposIndex(bd.getClase()).equals("enumerado"))
            {
                String[] par=new String[2];

                par[0]=bd.getRangosEnum(bd.getClase(),claseEjemplo);
                par[1]=bd.getRangosEnum(bd.getClase(),claseRegla);

                resultadosTra.add(par);
            }
            else //integer
            {
         claseEjemplo+=((Integer)bd.getInicial(bd.getClase())).intValue();
         claseRegla+=((Integer)bd.getInicial(bd.getClase())).intValue();

         Resultado actual=new Resultado(claseEjemplo,claseRegla);
             resultadosTra.add(actual);
            }
         */
        //cont++;

        bc.remove(i);
        i--;
      } //if(cubierto)
    } //for(elements...)
  }

  /*
   public Hider(BaseDatos bd)
   {
    Discretizacion d=new Discretizacion(bd);
    d.discretiza();
    Codificacion cod=new Codificacion();
    cod.codificaBase(d.getCortes(),bd);
    Vector bc=cod.getBaseCodificada();
    Evolucion ev=new Evolucion(bc,mutationProbability);
    Evolucion.setTipos(bd.getTipos());

    rules=new Vector();
    resultados=new Vector();
    int n=bc.size();
    int limit=pruneExamplesFactor*n;
    while(bc.size() > limit)
    {
     ev.setBc(bc); //Actualizamos el objeto con la base codificada actual
     Vector r=ev.algoritEvol(populationSize,nGenerations,crossPercent,d.getCortes(),bd.getRangos(),penaltyFactor);
     rules.add(r);

     //Evolucion.imprime(r,"Regla");

     //Eliminar de bc todos los ejemplos q son cubiertos por la regla...
     eliminaCubiertos(bc,r,d.getCortes());

    }
    System.out.println("Sacabó del tó!!!");

    //System.out.println("Aciertos: "+aciertos);
    //System.out.println("Errores: "+(bd.getNumEjemplos()-aciertos));

    System.out.println("Reglas ("+rules.size()+"):");
    System.out.println("--------");
    for(int i=0; i < rules.size();i++)
    {
     String cadena="\nIF ";
     Vector regla=(Vector)rules.get(i);
     for(int j=0;j < d.getCortes().size()-2;j++)
     {
      int k=0;
      if(d.getCortes().get(j) instanceof Vector) //Si es continuo...
      {
       k=((Vector)d.getCortes().get(j)).size();
       Long num=(Long)regla.get(j);
       int fila=Codificacion.descodificaF(num.intValue(),k);
       int columna=Codificacion.descodificaC(num.intValue(),k);
   double ini=((Corte)((Vector)d.getCortes().get(j)).get(fila)).getCorte();
   double fin=((Corte)((Vector)d.getCortes().get(j)).get(columna+1)).getCorte();
       if(ini != ((Double)bd.getInicial().get(j)).doubleValue() || fin != ((Double)bd.getFinales().get(j)).doubleValue())
       {
        cadena+=bd.getNombres().get(j)+" = [";
        if(ini != ((Double)bd.getInicial().get(j)).doubleValue())
        {
         cadena+=ini;
        }
        else
        {
         cadena+="_";
        }

        if(fin != ((Double)bd.getFinales().get(j)).doubleValue())
        {
         cadena+=", "+fin+") AND\n";
        }
        else
        {
         cadena+=", _) AND\n";
        }
       }

      }
      else //Si es discreto...
      {
       k=((Integer)d.getCortes().get(j)).intValue();
       Long num=(Long)regla.get(j);
       int[] valores=Codificacion.descodificaD(num.intValue(),k,((Integer)bd.getInicial().get(j)).intValue());

       cadena+=bd.getNombres().get(j)+" = "+valores[0];
       if(valores[1] >= 0)
        cadena+=" OR "+bd.getNombres().get(j)+" = "+valores[1];
       cadena+=" AND ";
      }

     }

     if(d.getCortes().get(d.getCortes().size()-2) instanceof Vector) //Si es continuo...
     {
      int k=((Vector)d.getCortes().get(d.getCortes().size()-2)).size();
      Long num=(Long)regla.get(d.getCortes().size()-2);
      int fila=Codificacion.descodificaF(num.intValue(),k);
      int columna=Codificacion.descodificaC(num.intValue(),k);
      double ini=((Corte)((Vector)d.getCortes().get(d.getCortes().size()-2)).get(fila)).getCorte();
      double fin=((Corte)((Vector)d.getCortes().get(d.getCortes().size()-2)).get(columna+1)).getCorte();

      if(ini != ((Double)bd.getInicial().get(d.getCortes().size()-2)).doubleValue() || fin != ((Double)bd.getFinales().get(d.getCortes().size()-2)).doubleValue())
      {
       cadena+=bd.getNombres().get(d.getCortes().size()-2)+" = [";
   if(ini != ((Double)bd.getInicial().get(d.getCortes().size()-2)).doubleValue())
       {
        cadena+=ini;
       }
       else
       {
        cadena+="_";
       }

   if(fin != ((Double)bd.getFinales().get(d.getCortes().size()-2)).doubleValue())
       {
        cadena+=", "+fin+") AND\n";
       }
       else
       {
        cadena+=", _) AND\n";
       }
      }
      //cadena+=bd.getNombres().get(d.getCortes().size()-2)+" = ["+ini+", "+fin+") THEN "+bd.getNombres().get(bd.getClase())+" = ";
     }
     else //Si es discreto...
     {
      int k=((Integer)d.getCortes().get(d.getCortes().size()-2)).intValue();
      Long num=(Long)regla.get(d.getCortes().size()-2);
      int[] valores=Codificacion.descodificaD(num.intValue(),k,((Integer)bd.getInicial().get(d.getCortes().size()-2)).intValue());

      cadena+=bd.getNombres().get(d.getCortes().size()-2)+" = "+valores[0];
      if(valores[1] >= 0)
   cadena+=" OR "+bd.getNombres().get(d.getCortes().size()-2)+" = "+valores[1];

      cadena+=" THEN "+bd.getNombres().get(bd.getClase())+" = ";
     }

     if(((String)bd.getTipos().get(bd.getClase())).equals("enumerado"))
     {
      if(bd.getValEnum().get(bd.getClase()) != null)
      {
       cadena+=(String)((Vector)bd.getValEnum().get(bd.getClase())).get(((Long)regla.get(bd.getClase())).intValue());
      }
     }
     //La clase siempre es discreta...

     else if(((String)bd.getTipos().get(bd.getClase())).equals("integer"))
     {
      cadena+=(((Long)regla.get(bd.getClase())).longValue()+((Integer)bd.getInicial().get(bd.getClase())).intValue());
     }

     System.out.println(cadena);
    }

   }
   */
  /**
   * @return Returns the crossPercent.
   */
  public double getCrossPercent() {
    return this.crossPercent;
  }

  /**
   * @param crossPercent The crossPercent to set.
   */
  public void setCrossPercent(double crossPercent) {
    this.crossPercent = crossPercent;
  }

  /**
   * @return Returns the mutationProbability.
   */
  public double getMutationProbability() {
    return this.mutationProbability;
  }

  /**
   * @param mutationProbability The mutationProbability to set.
   */
  public void setMutationProbability(double mutationProbability) {
    this.mutationProbability = mutationProbability;
  }

  /**
   * @return Returns the nGenerations.
   */
  public int getNGenerations() {
    return this.nGenerations;
  }

  /**
   * @param generations The nGenerations to set.
   */
  public void setNGenerations(int generations) {
    this.nGenerations = generations;
  }

  /**
   * @return Returns the penaltyFactor.
   */
  public double getPenaltyFactor() {
    return this.penaltyFactor;
  }

  /**
   * @param penaltyFactor The penaltyFactor to set.
   */
  public void setPenaltyFactor(double penaltyFactor) {
    this.penaltyFactor = penaltyFactor;
  }

  /**
   * @return Returns the populationSize.
   */
  public int getPopulationSize() {
    return this.populationSize;
  }

  /**
   * @param populationSize The populationSize to set.
   */
  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize;
  }

  /**
   * @return Returns the pruneExamplesFactor.
   */
  public double getPruneExamplesFactor() {
    return this.pruneExamplesFactor;
  }

  /**
   * @param pruneExamplesFactor The pruneExamplesFactor to set.
   */
  public void setPruneExamplesFactor(double pruneExamplesFactor) {
    this.pruneExamplesFactor = pruneExamplesFactor;
  }

  /**
   * @return Returns the rules.
   */
  public Vector getRules() {
    return this.rules;
  }

  /**
   * @param rules The rules to set.
   */
  public void setRules(Vector rules) {
    this.rules = rules;
  }

  /**
   * Test rules with a test file in the 'DataSet' object
   * @param ds
   * @param test true if it's test file and false if it's training file
   */
  public void testea(Dataset ds, boolean test) {
    BaseDatos baseTest = new BaseDatos(ds);
    Codificacion codTest = new Codificacion();
    codTest.codificaBase(baseTest);

    Vector bcTest = codTest.getBaseCodificada();
    int cont = 0; //Counter for covered elements
    int errores = 0;
    aciertos = 0;
    for (int i = 0; i < bcTest.size(); i++) { //Look through the elements
      Vector ejemploActual = (Vector) bcTest.get(i);
      boolean cubierto = false;
      int claseRegla = -1;

      //Look through the rules
      for (int h = 0; h < rules.size() && !cubierto; h++) {
        Vector r = (Vector) rules.get(h);

        /*System.err.println("Mira -> "+baseTest.getClase());
                         for (int j = 0; j < r.size(); j++){
            System.err.println("Mira -> "+((Long)r.get(j)).intValue());
                         }*/
        claseRegla = ( (Long) r.get(baseTest.getClase())).intValue();

        cubierto = true; //If any attribute is not covered then the rule will not cover the element
        //Look through attributes...
        for (int j = 0; j < ejemploActual.size() - 1 && cubierto; j++) { //Only take care of input attributes...
          int nr = ( (Long) r.get(j)).intValue();
          int ne = ( (Long) ejemploActual.get(j)).intValue();

          int k = 0;
          if (Codificacion.getCortesCod().get(j) instanceof Integer) { //Discrete
            k = ( (Integer) Codificacion.getCortesCod().get(j)).
                intValue();

            if ( (nr & ne) != 0) {
              cubierto = true;
            }
            else {
              cubierto = false;
            }
          }
          else { //Continuous
            k = ( (Vector) Codificacion.getCortesCod().get(j)).size();

            int fr = Codificacion.descodificaF(nr, k);
            int fe = Codificacion.descodificaF(ne, k);

            int cr = Codificacion.descodificaC(nr, k);
            int ce = Codificacion.descodificaC(ne, k);

            if ( (fr <= fe) && (cr >= ce)) {
              cubierto = true;
            }
            else {
              cubierto = false;
            }
          }
        } //for (attributes...)
      } //for (rules...)

      if (cubierto) {
        int claseEjemplo = ( (Long) ejemploActual.get(baseTest.getClase())).
            intValue();

        if (claseEjemplo == claseRegla) {
          aciertos++;
        }

        //Decode values...
        if (ds.getTiposIndex(baseTest.getClase()).equals("enumerado")) {
          String[] par = new String[2];

          par[0] = ds.getOutputValue(claseEjemplo); //ds.getRangosEnum(baseTest.getClase(), claseEjemplo);
          par[1] = ds.getOutputValue(claseRegla); //ds.getRangosEnum(baseTest.getClase(), claseRegla);

          if (test) {
            resultadosTst.add(par);
          }
          else {
            resultadosTra.add(par);
          }
        }
        /*else { //integer
         claseEjemplo +=
                 ((Integer) ds.getRangosVar(baseTest.getClase()).get(
                         0)).intValue();
         claseRegla +=
                 ((Integer) ds.getRangosVar(baseTest.getClase()).
                  get(0)).intValue();

         Resultado actual = new Resultado(claseEjemplo, claseRegla);
         if (test) {
             resultadosTst.add(actual);
         } else {
             resultadosTra.add(actual);
         }
                        }*/

        cont++;
      }
      else { //no cubierto
        String[] par = new String[2];
        int claseEjemplo = ( (Long) ejemploActual.get(baseTest.getClase())).
            intValue();
        par[0] = ds.getOutputValue(claseEjemplo); //ds.getRangosEnum(baseTest.getClase(), claseEjemplo);
        par[1] = ds.getOutputValue(mayoritaria);

        if (test) {
          resultadosTst.add(par);
        }
        else {
          resultadosTra.add(par);
        }
      }
    } //for (elements...)

    errores = baseTest.getNumEjemplos() - aciertos;
    if (test) {
      coefErrTst = Discretizacion.divide(errores, baseTest.getNumEjemplos());
    }
    else {
      coefErrTra = Discretizacion.divide(errores, baseTest.getNumEjemplos());
    }

  }

  /**
   * Write output files
   * @param cabecera
   * @throws IOException
   */
  public void escribeFicherosSalida(String cabecera) throws IOException {
    FileWriter fws = new FileWriter(fichSalTra);

//Training file
    fws.write(cabecera);
    for (int i = 0; i < resultadosTra.size(); i++) {
      if (resultadosTra.get(i) instanceof Resultado) {
        Resultado r = (Resultado) resultadosTra.get(i);
        fws.write(r.getClaseEjemplo() + " " + r.getClaseRegla() + "\n");
      }
      else { //String[]
        String[] s = (String[]) resultadosTra.get(i);
        fws.write(s[0] + " " + s[1] + "\n");
      }
    }
    fws.close();

//Test File
    FileWriter fwt = new FileWriter(fichSalTst);
    fwt.write(cabecera);
    for (int i = 0; i < resultadosTst.size(); i++) {
      if (resultadosTst.get(i) instanceof Resultado) {
        Resultado r = (Resultado) resultadosTst.get(i);
        fwt.write(r.getClaseEjemplo() + " " + r.getClaseRegla() + "\n");
      }
      else { //String[]
        String[] s = (String[]) resultadosTst.get(i);
        fwt.write(s[0] + " " + s[1] + "\n");
      }
    }
    fwt.close();

//Rules File
    FileWriter fwr = new FileWriter(fichSalRul);

    fwr.write("Error Training: " + coefErrTra);

    fwr.write("\nError Test: " + coefErrTst);

    fwr.write("\nRules (" + this.rules.size() + "):");
    fwr.write("\n--------");

    System.out.println("\nRules:\n" + cadenaRules);

    fwr.write(cadenaRules);

    fwr.close();
  }

  /**
   * @return Returns the fichEntTra.
   */
  public String getFichEntTra() {
    return fichEntTra;
  }

  /**
   * @param fichEntTra The fichEntTra to set.
   */
  public void setFichEnt(String fichEntTra) {
    this.fichEntTra = fichEntTra;
  }

  /**
   * @return Returns the fichRul.
   */
  public String getFichRul() {
    return fichSalRul;
  }

  /**
   * @param fichSalRul The fichSalRul to set.
   */
  public void setFichSalRul(String fichSalRul) {
    this.fichSalRul = fichSalRul;
  }

  /**
   * @return Returns the fichSalTra.
   */
  public String getFichSalTra() {
    return fichSalTra;
  }

  /**
   * @param fichSalTra The fichSalTra to set.
   */
  public void setFichSalTra(String fichSalTra) {
    this.fichSalTra = fichSalTra;
  }

  /**
   * @return Returns the getFichSalTst.
   */
  public String getFichSalTst() {
    return fichSalTst;
  }

  /**
   * @param fichSalTst The fichSalTst to set.
   */
  public void setFichSalTst(String fichSalTst) {
    this.fichSalTst = fichSalTst;
  }
}

