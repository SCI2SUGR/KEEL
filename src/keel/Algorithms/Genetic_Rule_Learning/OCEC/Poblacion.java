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

public class Poblacion {

  ArrayList<Organizacion> organizaciones;

  public Poblacion() {
    organizaciones = new ArrayList<Organizacion>();
  }

  public Poblacion(int clase, Attribute a, myDataset train) {
    organizaciones = new ArrayList<Organizacion> ();
    for (int i = 0; i < train.size(); i++) {
      if (train.getOutputAsInteger(i) == clase) {
        Organizacion o = new Organizacion(i, a, train);
        organizaciones.add(o);
      }
    }
  }

  public int size() {
    return organizaciones.size();
  }

  public Organizacion dameOrganizacion(int pos) {
    return organizaciones.get(pos); //referencia
  }

  public String printString() {
    String cadena = new String("");
    for (int i = 0; i < this.size(); i++) {
      cadena += "Organization[" + i + "]: ";
      cadena += organizaciones.get(i).printString();
    }
    return cadena;
  }

  public void print() {
    System.out.println(this.printString());
  }

  /**
   * eliminar organizaciones sin atributos útiles
   */
  public void eliminarNoUtiles() {
    for (int i = 0; i < organizaciones.size(); ) {
      if (organizaciones.get(i).nUtiles == 0) {
        organizaciones.remove(i);
      }
      /*else if (organizaciones.get(i).nUtiles == organizaciones.get(i).Uorg.length) {
        organizaciones.remove(i);
      }*/
      else {
        i++;
      }
    }
  }

  /**
   *   junta dos organizaciones si los atributos útiles de una estan contenidos en la otra (y tienen
   * lo mismos valores, obviamente.
   */
  public void mezclar() {
    for (int i = 0; i < organizaciones.size() - 1; i++) {
      Organizacion org1 = organizaciones.get(i);
      boolean salir = false;
      for (int j = i + 1; (j < organizaciones.size()) && (!salir); ) {
        if (organizaciones.get(j).contenido(org1)) {
          organizaciones.remove(i);
          i--;
          salir = true;
        }else  if (org1.contenido(organizaciones.get(j))){
          organizaciones.remove(j);
        }
        else {
          j++;
        }
      }
    }
  }

  public void limpia(){
    organizaciones.clear();
  }

  public void actualiza(Poblacion p){
    for (int i = 0; i < p.size(); i++){
      this.organizaciones.add(p.dameOrganizacion(i).copia());
    }
  }

}

