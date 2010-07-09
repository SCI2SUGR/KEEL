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
