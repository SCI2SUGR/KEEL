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

import java.util.*;
import org.core.*;

public class BaseR {

  ArrayList<Regla> baseReglas;
  myDataset train;
  String [] variables;

  public BaseR() {
    baseReglas = new ArrayList<Regla> ();
  }

  public BaseR(myDataset train) {
    baseReglas = new ArrayList<Regla> ();
    this.train = train;
    variables = new String[train.getnInputs()];
    for (int i = 0; i < variables.length; i++){
      variables[i] = new String(train.nombreVar(i));
    }
  }


  public void incluir(Poblacion p) {
    for (int i = 0; i < p.size(); i++) {
      Regla r = new Regla(p.dameOrganizacion(i));
      r.asignarNombres(train.nombreClase(r.clase), variables);
      r.calculaRelativeSupport(train);
      baseReglas.add(r);
    }
  }

  /**
   * Ordena las reglas segun su "relative support"
   */
  public void ordenar() {
    Collections.sort(baseReglas);
  }

  /**
   * Elimina aquellas reglas cuyos ejemplos ya esten cubiertos por otras de mas nivel (rs)
   */
  public void eliminarSubsumidas() {
    boolean[] ejemplos = new boolean[train.size()];
    for (int i = 0; i < ejemplos.length; i++) {
      ejemplos[i] = false;
    }
    this.ordenar();
    for (int i = 0; i < baseReglas.size();) {
      int count = 0;
      Regla r = baseReglas.get(i);
      for (int j = 0; j < train.size(); j++) {
        double[] example = train.getExample(j);
        if ( (!ejemplos[j]) && (r.cubre(example))) {
          ejemplos[j] = true;
          count++;
        }
      }
      if (count == 0) {
        baseReglas.remove(i);
      }else{
        //System.err.println("Ejemplos cubiertos por la regla "+i+": "+count);
        i++;
      }
    }
  }

  public String printString(){
    String cadena = new String("");
    cadena += "Number of Rules: "+baseReglas.size()+"\n";
    for (int i = 0; i < baseReglas.size(); i++){
      cadena += "Rule("+(i+1)+"): "+baseReglas.get(i).printString();
    }
    return cadena;
  }

  public String clasifica(double [] example){
    String output = new String("<unclassified>");
    double mv = 0;
    for (int i = 0; i < baseReglas.size(); i++){
      double aux = baseReglas.get(i).matchValue(example);
      if (aux > mv){
        mv = aux;
        output = baseReglas.get(i).nombreClase;
      }
    }
    return output;
  }

  public void printFichero(String fichero){
    Fichero.escribeFichero(fichero,this.printString());
  }

  public int size(){
    return baseReglas.size();
  }

}

