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
 * Created on 30-abr-2005
 *
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.Vector;

/**
 * @author Sebas
 */
public class Codificacion {
    /**
     * Data structure for codes
     */
    private Vector baseCodificada;

    /**
     * Cuts to encode-decode
     */
    private static Vector cortesCod;

    /**
     * Empty constructor
     */
    public Codificacion() {
        this.baseCodificada = new Vector();
    }

    /**
     * Constructor with a 'Vector' parameter
     * @param cortes
     */
    public Codificacion(Vector cortes) {
        cortesCod = cortes;
        this.baseCodificada = new Vector();
    }

    /**
     *
     * @return the baseCodificada
     */
    public Vector getBaseCodificada() {
        return this.baseCodificada;
    }

    /**
     * @param codigos The baseCodificada to set.
     */
    public void setBaseCodificada(Vector codigos) {
        this.baseCodificada = codigos;
    }

    /**
     * Encodes the elements
     * @param bd
     */
    public void codificaBase(BaseDatos bd) {
        Vector cortesAtributo = null;
        int k = 0, p = 0;
        Vector codigosEjemplo = null;

        //Look through the elements
        for (int i = 0; i < bd.getNumEjemplos(); i++) { //i => elements
            codigosEjemplo = new Vector();
            //Look through the attributes of each element
            for (int j = 0; j < bd.getNumAtributos(); j++) { //j => attributes
                Long codigo = null;
                if (cortesCod.get(j) instanceof Integer) { //Discrete
                    if (j == bd.getClase()) { //Is the output attribute
                        p = ((Integer) ((Vector) bd.getBase().get(i)).get(j)).
                            intValue();
                        //If output attribute is 'integer' is necessary to adjust the value
                        //to count from the initial value of range
                        if (((String) bd.getTipos().get(j)).equals("integer")) {
                            int valorInicial = ((Integer) bd.getInicial().get(j)).
                                               intValue();
                            p -= valorInicial;
                        }
                        codigo = new Long(p);
                    } else { //Is an input attribute
                        //Position
                        p = ((Integer) ((Vector) bd.getBase().get(i)).get(j)).
                            intValue();
                        //Values' range
                        k = ((Integer) cortesCod.get(j)).intValue(); //For discrete attributes, the cuts' Vector contains its range

                        //If it is an 'integer' attribute is necessary to adjust the value
                        //to count from the initial value of range
                        if (((String) bd.getTipos().get(j)).equals("integer")) {
                            int valorInicial = ((Integer) bd.getInicial().get(j)).
                                               intValue();
                            p -= valorInicial;
                        }
                        //If it's a 'enumerado' attribute is not necessary to adjust because its initial value will be always zero
                        codigo = new Long((long) Math.pow(2, k - p - 1));
                        //System.err.println("K = "+k+", p = "+p+" codigo --> "+codigo+"...");
                    }
                } else { //If it's a continuous attribute ('real')
                    //Take cuts from current attribute
                    cortesAtributo = (Vector) cortesCod.get(j);
                    double valor = ((Double) ((Vector) bd.getBase().get(i)).get(
                            j)).doubleValue();
                    k = cortesAtributo.size();

                    int f;
                    int c;
                    //Search for the current value's cuts and get its row and column numbers
                    //that numbers have to be the same because it's a minimum interval
                    f = c = busca(cortesAtributo, valor);

                    int n = f * (k - 1) + c + 1; //(k-1) is the number of rows and columns in the codification table

                    codigo = new Long(n);
                    //System.err.println("codigo -> "+codigo);

                }
                codigosEjemplo.add(codigo);
            } //for(...) attributes
            baseCodificada.add(codigosEjemplo);
        } //for(...) elements
        //System.exit(0);
    }

    /**
     * Binary search
     * @param cortes
     * @param valor
     * @return row/column number
     */
    private int busca(Vector cortes, double valor) {
        int ini = 0, fin = cortes.size(), med = -1;
        boolean enc = false;
        double minActual, maxActual;

        int fc = -1;

        //If 'valor' is lower than minimum value of range => return the minimum value of range
        if (valor < ((Corte) cortes.get(0)).getCorte()) {
            fc = 0;
        }
        //If 'valor' is greater than maximum value of range => return the maximum value of range
        else if (valor > ((Corte) cortes.get(fin - 1)).getCorte()) {
            fc = fin - 1;
        }
        //If 'valor' is between minimum and maximum values of range => search for it within range
        else {
            while ((fin >= ini) && !enc) {
                med = (int) Math.floor((ini + fin) / 2);

                //Take value of current attribute
                //Current position 'med' is lower limit of interval
                minActual = ((Corte) cortes.get(med)).getCorte();
                //(med+1) mustn't be greater than vector size
                if (med < cortes.size() - 1) {
                    maxActual = ((Corte) cortes.get(med + 1)).getCorte();
                } else { //That case means that the searched interval is found, it is the last one
                    enc = true;
                    fc = cortes.size() - 2;
                    break;
                }

                if (valor >= minActual && valor <= maxActual) {
                    enc = true;
                    //If current value is upper limit of the interval, then it belongs to the next interval
                    //except it's the last interval
                    if (valor == maxActual && med < (fin - 2)) {
                        med++;
                    }

                    fc = med;
                } else {
                    if (valor < minActual) {
                        fin = med - 1;
                    } else if (valor > maxActual) {
                        ini = med + 1;
                    }
                }
            }
        }

        return fc;
    }

    /**
     * Encodes a value from its row and column numbers
     * @param f
     * @param c
     * @param k
     * @return code
     */
    public static int codifica(int f, int c, int k) {
        return (f * (k - 1) + c + 1);
    }

    /*
     public static void descodifica(int n, Vector cortes, double fila, double columna)
     {
      int k=cortes.size();

      int f=(n-1)/(k-1);
      int c=(n-1)%(k-1);

      fila=((Corte)cortes.get(f)).getCorte();
      columna=((Corte)cortes.get(c+1)).getCorte();
     }
     */

    /**
     * Decode row number
     * @param n
     * @param k
     * @return row number
     */
    public static int descodificaF(int n, int k) {
        return (n - 1) / (k - 1);
    }

    /**
     * Decode column number
     * @param n
     * @param k
     * @return column number
     */
    public static int descodificaC(int n, int k) {
        return (n - 1) % (k - 1);
    }

    /**
     * Decode discrete attributes
     * @param n
     * @param k
     * @param inicio
     * @return original value
     */
    public static int[] descodificaD(int n, int k, int inicio) {
        int[] exps = descompone(n, k);
        Vector sal = new Vector();
        int i = 0;
        while (i < exps.length) {
            if (exps[i] >= 0) {
                sal.add(new Integer((k - exps[i] - 1) + inicio));
            }

            i++;
        }

        int[] res = new int[sal.size()];
        for (i = 0; i < sal.size(); i++) {
            res[i] = ((Integer) sal.get(i)).intValue();
        }
        return res;
    }

    /**
     * If any bit is '0', its exponent will be '-1'
     * @param n
     * @param k
     * @return array of exponents
     */
    public static int[] descompone(int n, int k) {
        int[] exponentes = new int[k];

        for (int i = k - 1; i >= 0; i--) {
            int aux = (int) Math.pow(2, i);
            if ((aux & n) != 0) {
                exponentes[k - i - 1] = i;
            } else {
                exponentes[k - i - 1] = -1;
            }
        }

        return exponentes;
    }


    /*
      private static boolean esPotenciaDe2(int n, Entero exp)
      {
     boolean es=true;
     int cont=0;
     if(n > 0)
     {
      while(n > 1 && es)
      {
       if(n % 2 == 0)
       {
        n/=2;
        cont++;
       }
       else
       {
        es=false;
        cont=-1;
       }
      }
     }
     else
     {
      cont=-1;
      //dejamos exp=-1 para saber q es (2^algo)+0
     }
     exp.setValor(cont);
     return es;
      }
     */
    /**
     * @return Returns the cortesCod.
     */
    public static Vector getCortesCod() {
        return cortesCod;
    }

    /**
     * @param cortesCod The cortesCod to set.
     */
    public static void setCortesCod(Vector cortesCod) {
        Codificacion.cortesCod = cortesCod;
        /*System.err.println("Tam -> "+cortesCod.size());
                 for (int i = 0; i < cortesCod.size(); i++){
            System.err.println("Puntos de corte para el atributo "+(i+1));
            for (int j = 0; j < ((Vector)cortesCod.get(i)).size(); j++){
         System.err.print((Corte) ((Vector) cortesCod.get(i)).get(j) + ",");
            }
                 }
                 System.exit(0);*/
    }
}

