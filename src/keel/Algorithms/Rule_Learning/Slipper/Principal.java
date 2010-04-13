/**
 * <p>
 * @author Written by Alberto Fernández (University of Granada)  01/07/2008
 * @author Modified by Xavi Solé (La Salle, Ramón Llull University - Barcelona) 03/12/2008
 * @version 1.1
 * @since JDK1.2
 * </p>
 */

package keel.Algorithms.Rule_Learning.Slipper;

import java.util.Vector;
import java.io.FileWriter;

class Principal {

  public static void main(String[] args){
    String param=args[0];

    parseParameters par=new parseParameters();
    par.parseConfigurationFile(param);
    Slipper rip=new Slipper(par);
    rip.execute();

  }
}