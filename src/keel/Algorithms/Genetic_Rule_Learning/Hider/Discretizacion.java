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
 * Created on 27-feb-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * @author Sebas
 */
public class Discretizacion {
    /**
     * 'BaseDatos' object to discretize
     */
    private BaseDatos bd = null;

    /**
     * 'Vector' object to store cuts from different attributes
     */
    private Vector cortes = null;

    /**
     * @param cort
     */
    public Discretizacion(Vector cort) {
        cortes = cort;
    }

    /**
     * @param b
     */
    public Discretizacion(BaseDatos b) {
        bd = b;
        cortes = new Vector();
    }

    /**
     * @param b
     * @param cort
     */
    public Discretizacion(BaseDatos b, Vector cort) {
        bd = b;
        cortes = cort;
    }

    /*
      public Discretizacion(String fichero)
      {
     bd=new BaseDatos(fichero);
     cortes=new Vector();
      }
     */

    /**
     * @return Returns the bd.
     */
    public BaseDatos getBd() {
        return bd;
    }

    /**
     * @param b The bd to set.
     */
    public void setBd(BaseDatos b) {
        bd = b;
    }

    /**
     * Makes discretization in database
     * @throws Exception
     */
    public void discretiza() throws Exception {
        //Look through every continuous attribute from database
        for (int i = 0; i < bd.getNumAtributos(); i++) {
            //If it's a continuous attribute...
            if (((Vector) bd.getBase().get(0)).get(i).getClass().toString().
                indexOf("Double") >= 0) {
                //Sort 'Vector' by current 'i' attribute
                Collections.sort(bd.getBase(),
                                 new AtributoComparator(i, bd.getClase()));

                //Initialize cuts
                inicializaCortesAtributo(i);
                HashMap uniones = new HashMap();
                Union union = null;
                for (int j = 0; j < ((Vector) cortes.get(i)).size() - 2; j++) {
                    Corte corteJ = (Corte) ((Vector) cortes.get(i)).get(j);
                    Corte corteJ1 = (Corte) ((Vector) cortes.get(i)).get(j + 1);
                    Corte corteJ2 = (Corte) ((Vector) cortes.get(i)).get(j + 2);

                    //Store as 'Union' the cut to delete and the goodness of that deletion
                    if (condicion(corteJ, corteJ1, corteJ2, i)) {
                        //'corteJ1' is the cut that divides two intervals, so it will be the one to delete
                        //Here stores the cut to delete and the goodness of that deletion
                        double bond = bondad(i, corteJ.getCorte(),
                                             corteJ2.getCorte());
                        Double etiqueta = new Double(corteJ1.getCorte());
                        //Now, gets the majority output value
                        int clase = claseMayoritaria(i, corteJ.getCorte(),
                                corteJ2.getCorte());
                        union = new Union(corteJ1, bond, clase);
                        uniones.put(etiqueta, union);
                    }
                }

                while (uniones.size() > 0) {
                    //First, join intervals that their union will be maximum goodness
                    //and get a new intervals set
                    //that means we delete the appropriate cut from cut's 'Vector'

                    //Search the unions' 'Map' to find the one with maximum goodness
                    int cMaxBond = -1;
                    double bondadMaxima = 0.0;
                    Object[] arrayUniones = uniones.values().toArray();
                    for (int u = 0; u < arrayUniones.length; u++) {
                        if (((Union) arrayUniones[u]).getBondad() >
                            bondadMaxima) {
                            bondadMaxima = ((Union) arrayUniones[u]).getBondad();
                            cMaxBond = u;
                        }
                    }
                    //Store cuts that define contiguous intervals
                    int corteEliminado = ((Vector) cortes.get(i)).indexOf(((
                            Union) arrayUniones[cMaxBond]).getCorte()); //Index of deleted cut

                    //These are sorted bottom-up as in cuts' 'Vector'...
                    int iniIant = corteEliminado - 2; //Initial cut of the interval before the new one
                    int finIant = corteEliminado - 1; //Final cut of the interval before the new one (initial cut of the new interval)

                    //Only add '1' because the deletion of the cut.
                    int iniIpost = corteEliminado; //Initial cut of the interval after the new one (final cut of the new interval)
                    int finIpost = corteEliminado + 1; //Final cut of the interval after the new one

                    Corte cIni = (Corte) ((Vector) cortes.get(i)).get(
                            corteEliminado - 1);
                    Corte cMed = (Corte) ((Vector) cortes.get(i)).get(
                            corteEliminado);
                    Corte cFin = (Corte) ((Vector) cortes.get(i)).get(
                            corteEliminado + 1);

                    //If still matches the union condition...
                    if (condicion(cIni, cMed, cFin, i)) {
                        //Delete this cut to join the intervals
                        ((Vector) cortes.get(i)).remove(((Union) arrayUniones[
                                cMaxBond]).getCorte());

                        //Before deletion, actualize goodness and majority output value of new interval
                        //(interval's goodness is the goodness of its upper cut)
                        //(interval's output value is the output value of its lower cut)
                        if (corteEliminado >= 0) {
                            ((Corte) ((Vector) cortes.get(i)).get(
                                    corteEliminado)).setBondad(bondadMaxima);
                            ((Corte) ((Vector) cortes.get(i)).get(
                                    corteEliminado -
                                    1)).setClase(((Union) arrayUniones[cMaxBond]).
                                                 getClase());
                        }
                        //Delete from 'Map' the 'Union' yet done
                        uniones.remove(new Double(((Union) arrayUniones[
                                cMaxBond]).getCorte().getCorte()));

                        //Check if contiguous intervals of the new one (created by the union done)
                        //can join with this new one.....
                        cIni = null;
                        cMed = null;
                        cFin = null;

                        //Next interval...

                        //If exist next and previous intervals...
                        if (iniIant >= 0 && finIant > iniIant && iniIpost >= 0 &&
                            iniIpost < (((Vector) cortes.get(i)).size() - 1) &&
                            finIpost < ((Vector) cortes.get(i)).size()) {
                            cIni = (Corte) ((Vector) cortes.get(i)).get(finIant);
                            cMed = (Corte) ((Vector) cortes.get(i)).get(
                                    iniIpost);
                            cFin = (Corte) ((Vector) cortes.get(i)).get(
                                    finIpost);
                        }
                        //If doesn't exist the next interval, do nothing
                        /*
                                  else if(iniIpost > ((Vector)cortes.get(i)).size() || finIpost >= ((Vector)cortes.get(i)).size())
                                  {

                                  }
                         */
                        //If exists the next interval but doesn't exists the previous one
                        else if (finIant <= 0 && finIant > iniIant &&
                                 iniIpost >= 0 &&
                                 iniIpost <
                                 (((Vector) cortes.get(i)).size() - 1) &&
                                 finIpost < ((Vector) cortes.get(i)).size()) {
                            cIni = (Corte) ((Vector) cortes.get(i)).get(0);
                            cMed = (Corte) ((Vector) cortes.get(i)).get(
                                    iniIpost);
                            cFin = (Corte) ((Vector) cortes.get(i)).get(
                                    finIpost);
                        }
                        //It could be an error
                        else if (!(iniIpost > ((Vector) cortes.get(i)).size() ||
                                   finIpost >= ((Vector) cortes.get(i)).size())) {
                            Exception e = new Exception(
                                    "Neither next nor previous intervals exist");
                            throw e;
                        }

                        //If there are any possible union, add it to the 'Union' objects 'Vector'
                        if (cIni != null && cMed != null && cFin != null &&
                            condicion(cIni, cMed, cFin, i)) {
                            //cMed (finIant) is the cut that divide both intervals, so it will be the one to delete
                            //Here store the cut to delete and the goodness of the union
                            double bond = bondad(i, cIni.getCorte(),
                                                 cFin.getCorte());
                            int clase = claseMayoritaria(i, cIni.getCorte(),
                                    cFin.getCorte());
                            union = new Union(cMed, bond, clase);
                            uniones.put(new Double(cMed.getCorte()), union);
                        }

                        //Previous interval...

                        //If exist next and previous intervals...
                        if (iniIant >= 0 && finIant > 0 && finIant > iniIant &&
                            iniIpost < ((Vector) cortes.get(i)).size()) {
                            cIni = (Corte) ((Vector) cortes.get(i)).get(iniIant);
                            cMed = (Corte) ((Vector) cortes.get(i)).get(finIant);
                            cFin = (Corte) ((Vector) cortes.get(i)).get(
                                    iniIpost);
                        }
                        //If exists the previous interval but doesn't exists the next one
                        else if (iniIant >= 0 && finIant > 0 &&
                                 finIant > iniIant &&
                                 iniIpost >= ((Vector) cortes.get(i)).size()) {
                            cIni = (Corte) ((Vector) cortes.get(i)).get(iniIant);
                            cMed = (Corte) ((Vector) cortes.get(i)).get(finIant);
                            cFin = (Corte) ((Vector) cortes.get(i)).get(((
                                    Vector) cortes.get(i)).size() - 1);
                        }
                        //If doesn't exist the previous interval, do nothing
                        /*
                                  else if(iniIant < 0 || finIant <= 0)
                                  {
                                  }
                         */
                        //It could be an error
                        else if (!(iniIant < 0 || finIant <= 0)) {
                            Exception e = new Exception(
                                    "Neither next nor previous intervals exist");
                            throw e;
                        }
                        //If there are any possible union, add it to the 'Union' objects 'Vector'
                        if (cIni != null && cMed != null && cFin != null &&
                            condicion(cIni, cMed, cFin, i)) {
                            //cMed (finIant) is the cut that divide both intervals, so it will be the one to delete
                            //Here store the cut to delete and the goodness of the union
                            double bond = bondad(i, cIni.getCorte(),
                                                 cFin.getCorte());
                            int clase = claseMayoritaria(i, cIni.getCorte(),
                                    cFin.getCorte());
                            union = new Union(cMed, bond, clase);
                            uniones.put(new Double(cMed.getCorte()), union);
                        }
                    } else { //if(condicion(...)
                        //If the cut doesn't match the condition in 'condicion(...)', delete it only from the 'Map'
                        //don't delete it from cuts' 'Vector'
                        uniones.remove(new Double(((Union) arrayUniones[
                                cMaxBond]).getCorte().getCorte()));
                    }
                }
            } else { //If it's a discrete attribute
                //System.err.println("Aki ->"+bd.getRangos(i));
                cortes.add(bd.getRangos(i));
            }
        }
    }

    /**
     *
     * @param cortes2
     * @param campo
     * @throws Exception
     */
    public void imprimeCortes(Vector cortes2, int campo) throws Exception {
        for (int i = 0; i < cortes2.size(); i++) {
            Corte c1 = (Corte) ((Vector) cortes2.get(campo)).get(i);
            Corte c2 = (Corte) ((Vector) cortes2.get(campo)).get(i + 1);
            Corte c3 = (Corte) ((Vector) cortes2.get(campo)).get(i + 2);
            if (condicion(c1, c2, c3, campo)) {
                System.out.println("Corte: " + c2.getCorte() + " Bondad: " +
                                   bondad(campo, c1.getCorte(), c3.getCorte()));
            } else {
                System.out.println("Corte: " + c2.getCorte() + " Bondad: 0");
            }
        }
        System.out.println("--------------------------------------------------------------------------------------");
    }

    /*
     public Vector inicializaCortes()
     {
      cortes=new Vector();
      Vector cortesAtributo=null;
      //Vector base=bd.getBase();

      //Recorro todos los atributos continuos de la base de datos
      for(int i=0;i < bd.getNumAtributos();i++)
      {
       cortesAtributo=new Vector();

          //Si el atributo es continuo...
       //if(((Vector)base.get(0)).get(i).getClass().toString().equals("Double"))
          if(((Vector)bd.getBase().get(0)).get(i).getClass().toString().indexOf("Double") >= 0)
          {
           //Ordenamos el vector por el atributo i
     Collections.sort(bd.getBase(),new AtributoComparator(i,BaseDatos.getClase()));

        //Añadimos el primer elemento a los intervalos
           Corte cInicial=new Corte((Integer)((Vector)bd.getBase().get(0)).get(BaseDatos.getClase()),(Double)((Vector)bd.getBase().get(0)).get(i));
           cortesAtributo.add(cInicial);


           Double corte=null;
           //Cogemos el primer valor del atributo como valor inicial del intervalo inicial
           double iniIntervaloActual=((Double)((Vector)bd.getBase().get(0)).get(i)).doubleValue();
              //Recorremos los ejemplos...
              for(int j=0;j < bd.getNumEjemplos()-1;j++)
              {
               //Cogemos el valor del atributo 'i' en el ejemplo 'j'
     double vj=((Double)((Vector)bd.getBase().get(j)).get(i)).doubleValue();
               //Cogemos el valor del atributo 'i' en el siguiente ejemplo (el 'j+1')
     double vj1=((Double)((Vector)bd.getBase().get(j+1)).get(i)).doubleValue();


               //Cogemos el valor de la clase mayoritaria en el ejemplo 'j'
               int clasej=claseMayoritaria(i,((Double)((Vector)bd.getBase().get(j)).get(i)).doubleValue());

               //Cogemos el valor de la clase mayoritaria en el siguiente ejemplo (el 'j+1')
                  int clasej1=claseMayoritaria(i,((Double)((Vector)bd.getBase().get(j+1)).get(i)).doubleValue());

                  //Si alguna clase mayoritaria vale MIN_VALUE significa que ha habido empate en ese intervalo
               if((vj != vj1 || clasej == Integer.MIN_VALUE || clasej1 == Integer.MIN_VALUE) && (clasej != clasej1 || puro(bd,vj,i) != puro(bd,vj1,i)))
               {
                double aux=suma(vj,vj1);
                aux/=2.0;

                corte=new Double(aux);
                double finIntervaloActual=corte.doubleValue();
                //Calculamos la bondad del intervalo
     double bondadIntervalo=bondad(i,iniIntervaloActual,finIntervaloActual);
                //El corte final del intervalo (finIntervaloActual) es el que lleva la bondad
                Corte c=new Corte(clasej1,corte,bondadIntervalo);
                cortesAtributo.add(c);

                //El corte es también el valor inicial del siguiente intervalo
                iniIntervaloActual=corte.doubleValue();
               }
              }
        //Añadimos el último elemento a los intervalos
              Double corteFin=(Double)((Vector)bd.getBase().get(bd.getNumEjemplos()-1)).get(i);
     double bondadIntervalo=bondad(i,iniIntervaloActual,corteFin.doubleValue());
        Corte cFinal=new Corte((Integer)((Vector)bd.getBase().get(bd.getNumEjemplos()-1)).get(BaseDatos.getClase()),corteFin,bondadIntervalo);
           cortesAtributo.add(cFinal);

           //Añadimos el vector de cortes del atributo al vector general de cortes
           //El tamaño del vector general será el número de atributos
           cortes.add(cortesAtributo);
          }
      }

      return(cortes);
     }
     */


    /**
     * Inicializa los cortes para la discretización
     * @param i
     * @throws Exception
     */
    public void inicializaCortesAtributo(int i) throws Exception {
        //Look through every continuous attribute in database

        Vector cortesAtributo = new Vector();

        //Add first element to intervals
        Corte cInicial = new Corte((Integer) ((Vector) bd.getBase().get(0)).get(
                bd.getClase()), (Double) ((Vector) bd.getBase().get(0)).get(i));
        cortesAtributo.add(cInicial);

        Double corte = null;
        //Take first value of attribute range as initial value of initial interval
        double iniIntervaloActual = ((Double) ((Vector) bd.getBase().get(0)).
                                     get(i)).doubleValue();
        //Look through the elements...
        for (int j = 0; j < bd.getNumEjemplos() - 1; j++) {
            //Take the value of 'i' attribute in 'j' element
            double vj = ((Double) ((Vector) bd.getBase().get(j)).get(i)).
                        doubleValue();
            //Take the value of 'i' attribute in next element ('j+1')
            double vj1 = ((Double) ((Vector) bd.getBase().get(j + 1)).get(i)).
                         doubleValue();

            //Take the value of majority output attribute in 'j' element
            int clasej = claseMayoritaria(i,
                                          ((Double) ((Vector) bd.getBase().
                    get(j)).get(i)).doubleValue());

            //Take the value of majority output attribute in next element ('j+1')
            int clasej1 = claseMayoritaria(i,
                                           ((Double) ((Vector) bd.getBase().
                    get(j + 1)).get(i)).doubleValue());

            //If any majority output attribute has the value MIN_VALUE, that means that there is a tie in the interval
            if ((vj != vj1 || clasej == Integer.MIN_VALUE ||
                 clasej1 == Integer.MIN_VALUE) &&
                (clasej != clasej1 || puro(vj, i) != puro(vj1, i))) {
                double aux = suma(vj, vj1);
                aux /= 2.0;

                corte = new Double(aux);
                double finIntervaloActual = corte.doubleValue();
                //Calculate interval goodness
                double bondadIntervalo = bondad(i, iniIntervaloActual,
                                                finIntervaloActual);
                //The final cut of interval (finIntervaloActual) is the one that contains the goodness
                Corte c = new Corte(clasej1, corte, bondadIntervalo);
                cortesAtributo.add(c);

                //This cut is also the the initial value of next interval
                iniIntervaloActual = corte.doubleValue();
            }
        }
        //Add last element to intervals
        Double corteFin = (Double) ((Vector) bd.getBase().get(bd.getNumEjemplos() -
                1)).get(i);
        double bondadIntervalo = bondad(i, iniIntervaloActual,
                                        corteFin.doubleValue());
        Corte cFinal = new Corte((Integer) ((Vector) bd.getBase().get(bd.
                getNumEjemplos() - 1)).get(bd.getClase()), corteFin,
                                 bondadIntervalo);
        cortesAtributo.add(cFinal);

        //Add cuts' 'Vector' of this attribute to general cuts' 'Vector'
        //The number of attributes will be general 'Vector' size
        cortes.add(cortesAtributo);
    }

    /**
     *
     * @param v
     * @param campo
     * @return p
     * @throws Exception
     */
    private boolean puro(double v, int campo) throws Exception {
        boolean p = true;

//The vector is sorted by field 'campo' values
//so it allow us to apply binary search

        int ini = 0, fin = bd.getNumEjemplos() - 1, med = -1;
        boolean enc = false;
        double actual;

        while (fin >= ini && !enc) {
            med = (ini + fin) / 2;

            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();

            if (v == actual) {
                //When found, return the first position it has been found
                while (v == actual && med > 0) {
                    actual = ((Double) ((Vector) bd.getBase().get(--med)).get(
                            campo)).doubleValue();
                }
                if (v != actual) {
                    med++;
                }

                enc = true;
            } else {
                if (v < actual) {
                    fin = med - 1;
                } else {
                    ini = med + 1;
                }
            }
        }

        if (med > -1 && enc && med < bd.getNumEjemplos()) {
            //Check purity of 'v' beginning in 'med' position
            //that is the position it has been found
            int i = med + 1;
            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();
            int clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                    getClase())).intValue();

            while (actual == v && p && i < bd.getNumEjemplos()) {
                actual = ((Double) ((Vector) bd.getBase().get(i)).get(campo)).
                         doubleValue();
                int claseActual = ((Integer) ((Vector) bd.getBase().get(i)).get(
                        bd.getClase())).intValue();
                if (actual == v && clase != claseActual) {
                    p = false;
                }
                i++;
            }
        } else {
            Exception e = new Exception(
                    "Something wrong happens with vector size. ini: " + ini +
                    ", fin: " + fin);
            throw e;
        }
        return p;
    }

    /**
     * Calculate the goodness
     * @param campo
     * @param vIni
     * @param vFin
     * @return b
     * @throws Exception
     */
    private double bondad(int campo, double vIni, double vFin) throws Exception {
        double b = 0;

        //First, obtain output values in interval and its frequencies

        //The vector is sorted by field 'campo' values
        //so it allow us to apply binary search

        int ini = 0, fin = bd.getNumEjemplos() - 1, med = -1;
        boolean enc = false;
        double actual;

        //Use a 'Map' to contain frequencies of every value of output attribute
        Map frecuencias = new HashMap();

        while (fin >= ini && !enc) {
            med = (ini + fin) / 2;
            //System.out.println("Mira :: Ini = "+ini+"; Med = "+med+"; Fin = "+fin);
            //Take attribute value
            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();

            if (vIni <= actual && actual <= vFin) {
                //When found, go to the first position of the value
                while (vIni <= actual && med > 0) {
                    actual = ((Double) ((Vector) bd.getBase().get(--med)).get(
                            campo)).doubleValue();
                }
                if (vIni > actual) {
                    med++;
                }
                enc = true;
            } else {
                if (actual < vIni) {
                    ini = med + 1;
                } else if (actual >= vFin) {
                    fin = med - 1;
                }
            }
        }

        int mayoritaria = -1;
        int mayorClase = -1;
        if (med > -1 && enc) {
            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();
            int clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                    getClase())).intValue();
            int valorAnt = 0;

            mayoritaria = clase;
            mayorClase = clase;
            while
                    (
                            vIni <= actual &&
                            (
                                    (actual < vFin &&
                                     vFin !=
                                     ((Double) ((Vector) bd.getBase().
                                                get(bd.getNumEjemplos() - 1)).
                                      get(campo)).doubleValue())
                                    || //If it's the last value of elements, then the interval is (<=)
                                    (actual <= vFin &&
                                     vFin ==
                                     ((Double) ((Vector) bd.getBase().
                                                get(bd.getNumEjemplos() - 1)).
                                      get(campo)).doubleValue())
                            )
                            &&
                            med < bd.getNumEjemplos()
                    ) { //while
                if (frecuencias.containsKey(new Integer(clase))) {
                    valorAnt = ((Integer) frecuencias.get(new Integer(clase))).
                               intValue();
                } else {
                    valorAnt = 0;
                }

                frecuencias.put(new Integer(clase), new Integer(valorAnt + 1));

                //Obtain the majority output value
                int valorClase = ((Integer) frecuencias.get(new Integer(clase))).
                                 intValue();
                int valorMayor = ((Integer) frecuencias.get(new Integer(
                        mayoritaria))).intValue();
                if (valorClase > valorMayor) {
                    mayoritaria = clase;
                }
                //Obtain the greatest output value in 'Map' that will be used as limit foor the loop
                if (clase > mayorClase) {
                    mayorClase = clase;
                }

                med++;
                if (med < bd.getNumEjemplos()) {
                    actual = ((Double) ((Vector) bd.getBase().get(med)).get(
                            campo)).doubleValue();
                    clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                            getClase())).intValue();
                }
            }

            //The frequencies of output values are in 'frecuencias' 'Map' and
            //majority output value is in 'mayoritaria'
            //Also the greatest output value in 'Map' is in 'mayorClase' to limit the loop
            double suma = 1.0;
            for (int f = 0; f <= mayorClase; f++) {
                if (f != mayoritaria && frecuencias.containsKey(new Integer(f)) &&
                    ((Integer) frecuencias.get(new Integer(f))).intValue() > 0) {
                    suma += ((Integer) frecuencias.get(new Integer(f))).
                            doubleValue();
                }
            }
            //Calculate interval goodness
            b = ((Integer) frecuencias.get(new Integer(mayoritaria))).
                doubleValue() / suma;
        } else {
            Exception e = new Exception(
                    "Something wrong happens with vector size. ini: " + ini +
                    ", fin: " + fin + ", med: " + med + ", enc: " + enc +
                    ", mayoritaria: " + mayoritaria + ", vIni: " + vIni +
                    ", vFin: " + vFin + ", Campo: "+ campo);
            throw e;
        }

        return b;
    }

    /**
     *
     * @param campo
     * @param vIni
     * @return the majority class for the interval
     * @throws Exception
     */
    private int claseMayoritaria(int campo, double vIni) throws Exception {

        //First, obtain output values in interval and its frequencies

        //The vector is sorted by field 'campo' values
        //so it allow us to apply binary search

        int ini = 0, fin = bd.getNumEjemplos() - 1, med = -1;
        boolean enc = false;
        double actual;

        //Use a 'Vector' to contain output values' frequencies
        Map frecuencias = new HashMap();

        while (fin >= ini && !enc) {
            med = (ini + fin) / 2;
            //Take attribute value
            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();

            if (vIni == actual) { //Search for 'vIni' because is the first value in ['vIni','vFin')
                //When found, go to the first position of the value
                while (vIni == actual && med > 0) {
                    actual = ((Double) ((Vector) bd.getBase().get(--med)).get(
                            campo)).doubleValue();
                }
                if (vIni != actual) {
                    med++;
                }
                enc = true;
            } else {
                if (vIni < actual) {
                    fin = med - 1;
                } else if (actual < vIni) {
                    ini = med + 1;
                }
            }
        }

        int mayoritaria = -1;
        if (med > -1 && enc) {
            //Begin at first position of value ('med')

            int valorAnt = 0;

            boolean repetida = false; //Used to know if there are more than one majority output value

            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();
            int clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                    getClase())).intValue();
            mayoritaria = clase;

            while (vIni == actual && med < bd.getNumEjemplos()) {
                if (frecuencias.containsKey(new Integer(clase))) {
                    valorAnt = ((Integer) frecuencias.get(new Integer(clase))).
                               intValue();
                } else {
                    valorAnt = 0;
                }

                frecuencias.put(new Integer(clase), new Integer(valorAnt + 1)); //Add one more to the frequency of this output value

                //Obtain the majority output value
                int valorClase = ((Integer) frecuencias.get(new Integer(clase))).
                                 intValue();
                int valorMayor = ((Integer) frecuencias.get(new Integer(
                        mayoritaria))).intValue();
                if (valorClase > valorMayor) {
                    mayoritaria = clase;
                }
                med++;
                if (med < bd.getNumEjemplos()) {
                    actual = ((Double) ((Vector) bd.getBase().get(med)).get(
                            campo)).doubleValue();
                    clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                            getClase())).intValue();
                }
            }

            //Look for others majority output values (same frequency)
            Object[] arrayFrecs = frecuencias.values().toArray();
            Object[] arrayClases = frecuencias.keySet().toArray();
            int c = 0;
            int frecMayoritaria = ((Integer) frecuencias.get(new Integer(
                    mayoritaria))).intValue();
            while (c < arrayFrecs.length && !repetida) {
                int frecActual = ((Integer) arrayFrecs[c]).intValue();
                int claseActual = ((Integer) arrayClases[c]).intValue();
                if (frecActual == frecMayoritaria && claseActual != mayoritaria) {
                    repetida = true;
                }
                c++;
            }

            //The frequencies of output values are in 'frecuencias' 'Map' and
            //majority output value is in 'mayoritaria'. Variable 'repetida' is true if there is tie between several majority output values
            if (repetida) {
                //There is more than one majority output value, so return MIN_VALUE
                mayoritaria = Integer.MIN_VALUE;
            }
        } else {
            Exception e = new Exception(
                    "Something wrong happens with vector size. ini: " + ini +
                    ", fin: " + fin + ", med: " + med + ", enc: " + enc +
                    ", mayoritaria: " + mayoritaria + ", vIni: " + vIni);
            throw e;
        }
        return mayoritaria;
    }

    /**
     *
     * @param campo
     * @param vIni
     * @param vFin
     * @return the majority class for the interval
     * @throws Exception
     */
    private int claseMayoritaria(int campo, double vIni, double vFin) throws
            Exception {

        //First, obtain output values in interval and its frequencies

        //The vector is sorted by field 'campo' values
        //so it allow us to apply binary search

        int ini = 0, fin = bd.getNumEjemplos() - 1, med = -1;
        boolean enc = false;
        double actual;

        //Use a 'Vector' to contain output values' frequencies
        Map frecuencias = new HashMap();

        while (fin >= ini && !enc) {
            med = (ini + fin) / 2;
            //Take attribute value
            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();

            if (vIni <= actual && actual < vFin) {
                //When found, go to the first position of the value
                while (vIni <= actual && med > 0) {
                    actual = ((Double) ((Vector) bd.getBase().get(--med)).get(
                            campo)).doubleValue();
                }
                if (vIni != actual) {
                    med++;
                }
                enc = true;
            } else {
                if (vFin <= actual) {
                    fin = med - 1;
                } else if (actual < vIni) {
                    ini = med + 1;
                }
            }
        }

        int mayoritaria = -1;
        if (med > -1 && enc) {
            //Begin at first position of value ('med')

            int valorAnt = 0;
            boolean repetida = false; //Used to know if there are more than one majority output value

            actual = ((Double) ((Vector) bd.getBase().get(med)).get(campo)).
                     doubleValue();
            int clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                    getClase())).intValue();
            mayoritaria = clase;

            while (vIni <= actual && actual < vFin && med < bd.getNumEjemplos()) {
                if (frecuencias.containsKey(new Integer(clase))) {
                    valorAnt = ((Integer) frecuencias.get(new Integer(clase))).
                               intValue();
                } else {
                    valorAnt = 0;
                }

                frecuencias.put(new Integer(clase), new Integer(valorAnt + 1)); //Add one more to the frequency of this output value

                //Obtain majority output value
                int valorClase = valorAnt + 1;
                int valorMayor = ((Integer) frecuencias.get(new Integer(
                        mayoritaria))).intValue();
                if (valorClase > valorMayor) {
                    mayoritaria = clase;
                }

                med++;
                if (med < bd.getNumEjemplos()) {
                    actual = ((Double) ((Vector) bd.getBase().get(med)).get(
                            campo)).doubleValue();
                    clase = ((Integer) ((Vector) bd.getBase().get(med)).get(bd.
                            getClase())).intValue();
                }
            }
            //Look for others majority output values (same frequency)
            Object[] arrayFrecs = frecuencias.values().toArray();
            Object[] arrayClases = frecuencias.keySet().toArray();
            int c = 0;
            int frecMayoritaria = ((Integer) frecuencias.get(new Integer(
                    mayoritaria))).intValue();
            while (c < arrayFrecs.length && !repetida) {
                int frecActual = ((Integer) arrayFrecs[c]).intValue();
                int claseActual = ((Integer) arrayClases[c]).intValue();
                if (frecActual == frecMayoritaria && claseActual != mayoritaria) {
                    repetida = true;
                }
                c++;
            }
            //The frequencies of output values are in 'frecuencias' 'Map' and
            //majority output value is in 'mayoritaria'. Variable 'repetida' is true if there is tie between several majority output values
            if (repetida) {
                //There is more than one majority output value, so return MIN_VALUE
                mayoritaria = Integer.MIN_VALUE;
            }
        } else {
            Exception e = new Exception(
                    "Something wrong happens with vector size. ini: " + ini +
                    ", fin: " + fin + ", med: " + med + ", enc: " + enc +
                    ", mayoritaria: " + mayoritaria + ", vIni: " + vIni);
            throw e;
        }
        return mayoritaria;
    }

    /*
      private void obtieneFrecuencias(int campo)
      {

      //El vector está ordenado por los valores del campo 'campo'
       //así q nos permite utilizar la búsqueda binaria

     double actual;
     double anterior=-1;

     double antiguo=-1;
     int i=0;
     int valorAnt=0;
//Creamos un vector para contener las ocurrencias de cada valor del campo clase
     Vector frecuencias=new Vector();

     actual=((Double)((Vector)bd.getBase().get(0)).get(campo)).doubleValue();
     int clase=((Integer)((Vector)bd.getBase().get(0)).get(bd.getClase())).intValue();

     while(i < bd.getNumEjemplos())
     {
      if(i > 0)
      {
       if(actual == anterior && anterior >= 0)
       {
        if(frecuencias.size() > clase && frecuencias.get(clase) != null)
        {
         valorAnt=((Integer)frecuencias.get(clase)).intValue();
        }
        else
        {
         if(frecuencias.size() < (clase+1))
          frecuencias.setSize(clase+1);
         valorAnt=0;
        }

        frecuencias.setElementAt(new Integer(valorAnt+1),clase);  //Apuntamos una ocurrencia más de la clase

        anterior=actual;

     actual=((Double)((Vector)bd.getBase().get(++i)).get(campo)).doubleValue();
     clase=((Integer)((Vector)bd.getBase().get(++i)).get(bd.getClase())).intValue();
       }
       else
       {
        //Añadimos el vector de frecuencias para
        bd.addFrecuencias(frecuencias);
        //Comenzamos con un vector nuevo
        frecuencias=new Vector();

        if(frecuencias.size() > clase && frecuencias.get(clase) != null)
        {
         valorAnt=((Integer)frecuencias.get(clase)).intValue();
        }
        else
        {
         if(frecuencias.size() < (clase+1))
          frecuencias.setSize(clase+1);
         valorAnt=0;
        }

        frecuencias.setElementAt(new Integer(valorAnt+1),clase);  //Apuntamos una ocurrencia más de la clase

        anterior=actual;

     actual=((Double)((Vector)bd.getBase().get(++i)).get(campo)).doubleValue();
     clase=((Integer)((Vector)bd.getBase().get(++i)).get(bd.getClase())).intValue();
       }

      }
      else //Si es el primer valor...
      {
       if(frecuencias.size() < (clase+1))
        frecuencias.setSize(clase+1);
       valorAnt=0;

       frecuencias.setElementAt(new Integer(valorAnt+1),clase);  //Apuntamos una ocurrencia más de la clase

       anterior=actual;

     actual=((Double)((Vector)bd.getBase().get(++i)).get(campo)).doubleValue();
     clase=((Integer)((Vector)bd.getBase().get(++i)).get(bd.getClase())).intValue();
      }

      for(int f=0;f < frecuencias.size();f++)
      {
       if(frecuencias.get(f) == null)
        frecuencias.setElementAt(new Integer(0),f);
      }
     }
      }
     */



    /**
     *
     * @param j
     * @param j1
     * @param j2
     * @param campo
     * @return true if intervals Ii=[j, j1] and Ii+1=[j1, j2] are available to join themselves and false otherwise
     * @throws Exception
     */
    private boolean condicion(Corte j, Corte j1, Corte j2, int campo) throws
            Exception {
        //Interval Ii=[j,j1]
        //Interval Ii+1=[j1,j2]
        //Check that both intervals have the same output value
        //Interval output value is the output value of the lower limit, then output values for Ii and Ii+1 will be j and j1
        int claseI = j.getClase();
        int claseI1 = j1.getClase();

        boolean dev = false;
        if (claseI != claseI1 && claseI != Integer.MIN_VALUE &&
            claseI1 != Integer.MIN_VALUE) {
            dev = false;
        } else {
            //Interval goodness is the goodness of the upper limit, then goodnesses for Ii and Ii+1 will be j1's and j2's ones
            double bondadI = j1.getBondad();
            double bondadI1 = j2.getBondad();

            //Calculate intervals' goodness
            double media = divide(suma(bondadI, bondadI1), 2.0);
            //Calculate union's goodness
            double bondadUnion = bondad(campo, j.getCorte(), j2.getCorte());

            dev = (bondadUnion >= media);
        }
        return (dev);
    }

    /**
     * @return Returns the cortes.
     */
    public Vector getCortes() {
        return cortes;
    }

    /**
     * @param cort The cortes to set.
     */
    public void setCortes(Vector cort) {
        cortes = cort;
    }


    public String toString() {
        String s = "";
        Iterator it = this.getCortes().iterator();
        int i = 0;
        while (it.hasNext()) {
            Vector v = (Vector) it.next();
            Iterator it2 = v.iterator();
            s += "\nAtributo: " + (String)this.getBd().getNombres().get(i) +
                    "\n";
            s += "-----------------------------------------\n";

            while (it2.hasNext()) {
                Corte c = (Corte) it2.next();
                s += c.getCorte() + ", " + c.getClase() + ", " + c.getBondad() +
                        "\n";
            }
            i++;
        }
        return s;
    }

    /**
     *
     * @param a
     * @param b
     * @return a+b
     */
    public static double suma(double a, double b) { //return a+b
        BigDecimal A = new BigDecimal("" + a);
        BigDecimal B = new BigDecimal("" + b);

        BigDecimal C = A.add(B);

        return C.doubleValue();
    }

    /**
     *
     * @param a
     * @param b
     * @return a-b
     */
    public static double resta(double a, double b) { //return a-b
        BigDecimal A = new BigDecimal("" + a);
        BigDecimal B = new BigDecimal("" + b);

        BigDecimal C = A.subtract(B);

        return C.doubleValue();
    }

    /**
     *
     * @param a
     * @param b
     * @return a*b
     */
    public static double multiplica(double a, double b) { //return a*b
        BigDecimal A = new BigDecimal("" + a);
        BigDecimal B = new BigDecimal("" + b);

        BigDecimal C = A.multiply(B);

        return C.doubleValue();
    }

    /**
     *
     * @param a
     * @param b
     * @return a/b
     */
    public static double divide(double a, double b) { //return a/b
        BigDecimal A = new BigDecimal("" + a);
        BigDecimal B = new BigDecimal("" + b);

        BigDecimal C = A.divide(B, 3, BigDecimal.ROUND_HALF_DOWN);

        return C.doubleValue();
    }
    /*
     private double suma(double a, double b)
     {
      double c=a+b;
      double r=0;

      String decimales=String.valueOf(c);
      String s[]=decimales.split("[.]");

      if (s[1].lastIndexOf("9") > s[1].indexOf("9") || s[1].lastIndexOf("0") > s[1].indexOf("0"))
      {
       int n=0;
       if(s[1].indexOf("9") > 0)
       {
        String num=new String();
        for(int i=0;i < s[1].indexOf("9");i++)
        {
         num+=s[1].charAt(i);
        }
        n=Integer.parseInt(num);
        n++;
       }
       else if(s[1].indexOf("0") > 0)
       {
        String num=new String();
        for(int i=0;i < s[1].indexOf("0");i++)
        {
         num+=s[1].charAt(i);
        }
        n=Integer.parseInt(num);
       }
       r=Double.parseDouble(s[0]+"."+n);
      }
      else
      {
       r=c;
      }

      return r;
     }
     */

}

