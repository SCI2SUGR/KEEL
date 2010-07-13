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
 * Created on 01-may-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.core.*;

/**
 * @author Sebas
 *
 */
public class Evolucion {
    private Vector bc;
    private Vector sumaBondades = null;
    private double sumaFi = 0;
    private Vector poblacion;
    private double probMutacion = 0.5; //[0-1]
    private double probMutExtremo = 0.2; //[0-1]
    private double cep = 0;
    private Vector erroresClase;
    private String funcionEvaluacionXmlFileName = null;

    /**
     * Constructor
     * @param basec
     * @param probMut
     */
    public Evolucion(Vector basec, double probMut, String filename) {
        bc = (Vector) basec.clone();
        poblacion = new Vector();
        erroresClase = new Vector();
        probMutacion = probMut;
        funcionEvaluacionXmlFileName = filename;
    }

//Limits
    /**
     * Left limit
     * @param n
     * @param k
     * @return The left limit
     */
    public static int liz(int n, int k) {
        int f = Codificacion.descodificaF(n, k);
        return k * f + 1;
    }

    /**
     * Right limit
     * @param n
     * @param k
     * @return The right limit
     */
    public static int lde(int n, int k) {
        int f = Codificacion.descodificaF(n, k);
        return (k - 1) * (f + 1);
    }

    /**
     * Upper limit
     * @param n
     * @param k
     * @return The up limit
     */
    public static int lsu(int n, int k) {
        return Codificacion.descodificaC(n, k) + 1;
    }

    /**
     * Lower limit
     * @param n
     * @param k
     * @return The down limit
     */
    public static int lin(int n, int k) {
        int c = Codificacion.descodificaC(n, k);
        return k * c + 1;
    }

//Movements

    /**
     * Left
     * @param n
     * @param k
     * @return The number at the left
     */
    public static int iz(int n, int k) {
        return Math.max(liz(n, k), n - 1);
    }

    /**
     * Right
     * @param n
     * @param k
     * @return The number at the right
     */
    public static int de(int n, int k) {
        return Math.min(lde(n, k), n + 1);
    }

    /**
     * Up
     * @param n
     * @param k
     * @return The number upper
     */
    public static int su(int n, int k) {
        return Math.max(lsu(n, k), n - k + 1);
    }

    /**
     * Down
     * @param n
     * @param k
     * @return The number below
     */
    public static int in(int n, int k) {
        return Math.min(lin(n, k), n + k - 1);
    }

//m order movements
    /**
     * Left
     * @param n
     * @param k
     * @param m
     * @return The number 'm' positions at the left
     */
    public static int iz(int n, int k, int m) {
        return Math.max(liz(n, k), n - m);
    }

    /**
     * Right
     * @param n
     * @param k
     * @param m
     * @return The number 'm' positions at the right
     */
    public static int de(int n, int k, int m) {
        return Math.min(lde(n, k), n + m);
    }

    /**
     * Up
     * @param n
     * @param k
     * @param m
     * @return The number 'm' positions upper
     */
    public static int su(int n, int k, int m) {
        return Math.max(lsu(n, k), n - m * k + m);
    }

    /**
     * Down
     * @param n
     * @param k
     * @param m
     * @return The number below 'm' positions
     */
    public static int in(int n, int k, int m) {
        return Math.min(lin(n, k), n + m * k - m);
    }


    /**
     * @param padre
     * @param madre
     * @param cortes
     * @return Crossed children
     */
    public static Vector[] cruza(Vector padre, Vector madre, Vector cortes) {
        Vector[] hijos = new Vector[2];
        double aleatorio = Randomize.RandClosed(); //Math.random();
        if (aleatorio < 0.5) {
            hijos[0] = (Vector) padre.clone();
        } else {
            hijos[0] = (Vector) madre.clone();
        }
        aleatorio = Randomize.RandClosed(); //Math.random();
        if (aleatorio < 0.5) {
            hijos[1] = (Vector) padre.clone();
        } else {
            hijos[1] = (Vector) madre.clone();
        }

//Cross every gen
        for (int i = 0; i < cortes.size() - 1; i++) {
            int k = 0;
            int[] genHijo = new int[2];
            //System.err.println("Cruzando en el corte "+(i+1)+"-esimo!");

            if (cortes.get(i) instanceof Vector) { //Continuous
                k = ((Vector) cortes.get(i)).size();
                //System.err.println("aki llega?? [continuo]");
                genHijo = cruza(((Long) padre.get(i)).intValue(),
                                ((Long) madre.get(i)).intValue(), k);
            } else { //Discrete
                //k = ((Integer) cortes.get(i)).intValue();
                //System.err.println("aki llega?? [discreto] ");
                Vector aux = cruza(((Long) padre.get(i)).intValue(),
                                   ((Long) madre.get(i)).intValue(), cortes, i);
                //Take first two ones
                genHijo[0] = ((Long) aux.get(0)).intValue();
                if (aux.size() > 1) {
                    genHijo[1] = ((Long) aux.get(1)).intValue();
                }
            }

            if (genHijo[1] == 0) {
                hijos[1] = null;
            } else if (hijos[1] != null) {
                hijos[1].setElementAt(new Long(genHijo[1]), i);
            }
            hijos[0].setElementAt(new Long(genHijo[0]), i);
        }
        return hijos;
    }


    /**
     * Discrete cross
     * @param n
     * @param m
     * @param cortes
     * @param genAMutar
     * @return Returns the intersection individual
     */
    private static Vector cruza(int n, int m, Vector cortes, int genAMutar) {
        Set mutacionesN = new HashSet();
        Set mutacionesM = new HashSet();

        //Add the gen to mutations' list
        mutacionesN.add(new Long(n));
        mutacionesM.add(new Long(m));

        //k will be the 'k' bit position
        //every bit has the same mutation probability


        Vector interseccion = new Vector();

        //System.err.println("N -> "+n+" M -> "+m);

        while (interseccion.size() == 0) {
            //If there is no intersection, then mutate previous mutations in list
            int bitsN = 0;
            int rangoN = ((Integer) cortes.get(genAMutar)).intValue();

            bitsN = rangoN;

            Object[] arrayN = mutacionesN.toArray();
            int i = 0;
            int tam = mutacionesN.size();
            while (i < tam) {
                //System.err.println("sigo en el while --> "+i+" < "+tam);
                int nh = ((Long) arrayN[i]).intValue();

                for (int k = 0; k < bitsN; k++) {
                    int nuevoN = (int) ((nh + Math.pow(2, k)) %
                                        Math.pow(2, k + 1) +
                                        Math.pow(2, k + 1) *
                                        Math.floor(n / Math.pow(2, k + 1)));

                    if (!mutacionesN.contains(new Long(nuevoN))) {
                        mutacionesN.add(new Long(nuevoN));
                        arrayN = mutacionesN.toArray();
                    }
                }
                i++;
            }

            int bitsM = 0;
            int rangoM = ((Integer) cortes.get(genAMutar)).intValue();
            bitsM = rangoM;

            Object[] arrayM = mutacionesM.toArray();
            i = 0;
            tam = mutacionesM.size();
            while (i < tam) {
                //System.err.println("sigo en el while --> "+i+" < "+tam);
                int mh = ((Long) arrayM[i]).intValue();

                for (int k = 0; k < bitsM; k++) {
                    int nuevoM = (int) ((mh + Math.pow(2, k)) %
                                        Math.pow(2, k + 1) +
                                        Math.pow(2, k + 1) *
                                        Math.floor(m / Math.pow(2, k + 1)));

                    if (!mutacionesM.contains(new Long(nuevoM))) {
                        mutacionesM.add(new Long(nuevoM));
                        arrayM = mutacionesM.toArray();
                    }
                }
                i++;
            }
//Calculate intersections...
            Iterator itN2 = mutacionesN.iterator();
            while (itN2.hasNext()) {
                //System.err.println("sigo en el while --> "+itN2.hasNext());
                Long aux = (Long) itN2.next();
                if (mutacionesM.contains(aux)) {
                    interseccion.add(aux);
                }
            }
        }
        return interseccion;
    }


    /**
     * Continuous cross
     * @param n
     * @param m
     * @param k
     * @return Children gens
     */
    public static int[] cruza(int n, int m, int k) {
        int[] hijos = new int[2];
        hijos[0] = 0;
        hijos[1] = 0;

        //System.err.println("descodificando Filas ["+n+","+k+"]");
        int fn = Codificacion.descodificaF(n, k);
        //System.err.println("descodificando Filas ["+m+","+k+"]");
        int fm = Codificacion.descodificaF(m, k);

        //System.err.println("descodificando Columnas ["+n+","+k+"]");
        int cn = Codificacion.descodificaC(n, k);
        //System.err.println("descodificando Columnas ["+m+","+k+"]");
        int cm = Codificacion.descodificaC(m, k);

        int ch1 = -1;
        int fh1 = -1;
        int ch2 = -1;
        int fh2 = -1;
        if (cn != cm && fn != fm) { //If doesn't coincide neither row nor column...
            fh1 = fn;
            ch1 = cm;

            fh2 = fm;
            ch2 = cn;

            int h = 0;
            //Check if it's upper the main diagonal
            if (ch1 >= fh1) {
                h = Codificacion.codifica(fh1, ch1, k);
                hijos[0] = h;
                if (ch2 >= fh2) {
                    h = Codificacion.codifica(fh2, ch2, k);
                    hijos[1] = h;
                }
            } else if (ch2 >= fh2) {
                h = Codificacion.codifica(fh2, ch2, k);
                hijos[0] = h;
            }
        } else if (cn == cm && fn != fm) { //If they coincide only in column
            //Both children will have the same column
            ch1 = cn;
            ch2 = cn;
            int max = Math.abs(fn - fm);
            if (max > 1) {
                int base = Math.min(fm, fn);
                //Take randomly two rows within parents' rows for children
                double aleatorio = Randomize.RandClosed(); //Math.random();
                int offset1 = (int) Math.rint(aleatorio * max);
                fh1 = base + offset1;

                aleatorio = Randomize.RandClosed(); //Math.random();
                int offset2 = (int) Math.rint(aleatorio * max);
                if (offset2 == offset1) {
                    offset2 = (offset2 + 1) > max ? 0 : (offset2 + 1);
                }
                fh2 = base + offset2;

                //Encode children...
                hijos[0] = Codificacion.codifica(fh1, ch1, k);
                hijos[1] = Codificacion.codifica(fh2, ch2, k);
            } else {
                hijos[0] = n;
                hijos[1] = m;
            }
        } else if (cn != cm && fn == fm) { //If they coincide only in row
            //Both children will have the same row
            fh1 = fn;
            fh2 = fn;
            int max = Math.abs(cn - cm);
            if (max > 1) {
                int base = Math.min(cm, cn);
                //Take randomly two columns within parents' columns for children
                double aleatorio = Randomize.RandClosed(); // Math.random();
                int offset1 = (int) Math.rint(aleatorio * max);
                ch1 = base + offset1;

                aleatorio = Randomize.RandClosed(); // Math.random();
                int offset2 = (int) Math.rint(aleatorio * max);
                if (offset2 == offset1) {
                    offset2 = (offset2 + 1) > max ? 0 : (offset2 + 1);
                }
                ch2 = base + offset2;

                //Encode children...
                hijos[0] = Codificacion.codifica(fh1, ch1, k);
                hijos[1] = Codificacion.codifica(fh2, ch2, k);
            } else {
                hijos[0] = n;
                hijos[1] = m;
            }
        } else if (cn == cm && fn == fm) { //If both parents are the same...
            hijos[0] = n;
            hijos[1] = n;
        }

        return hijos;
    }


    /**
     * @param individuosIniciales
     * @param baseCodificada
     * @param cortes
     * @return poblacionInicial
     */
    public static Vector inicializaPoblacion(int individuosIniciales,
                                             Vector baseCodificada,
                                             Vector cortes) {
        Vector poblacionInicial = new Vector();
        int tam = baseCodificada.size();

        for (int i = 0; i < individuosIniciales; i++) {
            //Take an element randomly from database
            double aleatorio = Randomize.RandClosed(); //Math.random();
            int pos = (int) Math.rint(aleatorio * (tam - 1));
            //System.err.println("Mira -> "+pos+", a:"+aleatorio+", tam: "+tam);
            Vector ejemplo = (Vector) baseCodificada.get(pos);
            int numAtributos = ejemplo.size();
            //System.err.println("Atributos -> "+numAtributos);
            Vector nuevoEjemplo = new Vector();

            //Move up and right to obtain a rule that covers current example
            //to do that, is necessary to obtain the number of cuts (k) for the attribute that is going to be moved

            int j = 0;
            //System.err.println("");
            boolean malo = false; //Parcheado
            while ((j < numAtributos - 1)&&(!malo)) { //Parcheado
                int n = ((Long) ejemplo.get(j)).intValue();
                int k = 0;
                if (cortes.get(j) instanceof Vector) {
                    k = ((Vector) cortes.get(j)).size();
                } else {
                    k = ((Integer) cortes.get(j)).intValue();
                }
                int fc = Codificacion.descodificaF(n, k);

                aleatorio = Randomize.RandClosed(); //Math.random();
                int mArriba = (int) Math.rint(aleatorio * fc);
                aleatorio = Randomize.RandClosed(); //Math.random();
                int mDerecha = (int) Math.rint(aleatorio * (k - fc - 2));

                int nuevoN = su(n, k, mArriba);
                nuevoN = de(nuevoN, k, mDerecha);

                nuevoEjemplo.add(new Long(nuevoN));
                if (nuevoN < 0){
                    //System.err.print("Mira: (att: "+j+", fila: "+fc+", ej: "+n+", cortes:"+k+" )[" + mArriba + "," + mDerecha +
                    //                 "] -> " + n + "," + nuevoN + "...");
                    malo = true; //Parcheado
                }
                j++;
            }
            //Add output attribute
            if (!malo){ //Parcheado
                int n = ((Long) ejemplo.get(numAtributos - 1)).intValue();
                nuevoEjemplo.add(new Long(n));
                poblacionInicial.add(nuevoEjemplo);
            }else{
                i--; //porque hago i++ (parcheado)
            }
        }
        /*for (int i = 0; i < poblacionInicial.size(); i++){
            System.err.println("Tam regla -> "+((Vector)poblacionInicial.get(i)).size());
        }*/
        return poblacionInicial;
    }


    /**
     * Implements Evolutionary Learning
     * @param nIndPobIni
     * @param nGenerac
     * @param crossPerc
     * @param cortes
     * @param rangos
     * @param fp
     * @return Best rule
     * @throws Exception
     */
    public Vector algoritEvol(int nIndPobIni, int nGenerac, double crossPerc,
                              Vector cortes, Vector rangos, double fp) throws
            Exception {
        Vector mejorRegla = new Vector();
        Entero[] errorMejorRegla = new Entero[2];

        poblacion = inicializaPoblacion(nIndPobIni, bc, cortes);
        /*for (int i = 0; i < poblacion.size(); i++){
            System.err.println("");
            for(int j = 0; j < ((Vector)poblacion.get(i)).size(); j++){
                System.err.print((Long)((Vector)poblacion.get(i)).get(j)+",");
            }
                 }*/
        //System.exit(0);
        //System.out.println("Population Initializated.");

        for (int i = 0; i < nGenerac; i++) {
            //System.out.println("Generation " + (i + 1) + " of " + nGenerac);
            evaluacion(rangos, cortes, fp);
            reemplazo(cortes, crossPerc, nIndPobIni);
        }
        evaluacion(rangos, cortes, fp);

        //The best rule is now at the first position so...
        mejorRegla = (Vector) poblacion.get(0);
        errorMejorRegla = (Entero[]) erroresClase.get(0);

        Vector ret = new Vector();
        ret.add(mejorRegla);
        ret.add(errorMejorRegla);

        return ret;
    }


    /**
     * @param rangos
     * @param cortes
     * @param fp
     * @throws Exception
     */
    public void evaluacion(Vector rangos, Vector cortes, double fp) throws
            Exception {
        int N = bc.size();
        erroresClase = new Vector();

        //to mark the best rule...
        int max = -1;
        double bondadMax = 0;
        //Look through the individual
        for (int i = 0; i < poblacion.size(); i++) {
            //System.out.println("Treating chromosome "+(i+1)+" of "+poblacion.size());
            Entero EC = new Entero( -1);
            Entero A = new Entero( -1);
            Vector reglaActual = (Vector) poblacion.get(i);

            ec_a(reglaActual, cortes, EC, A);

            double fiActual = 0;
            double error = 0;
            try {
                error = Discretizacion.divide(EC.getValor(),
                                              EC.getValor() + A.getValor());
            } catch (ArithmeticException ae) {
                error = Double.MAX_VALUE;
            }

            double valorCobertura = cobertura(reglaActual, rangos, cortes);

            if (funcionEvaluacionXmlFileName != null) { //If there is a XML file for the evaluation function...
                Operacion opActual = FuncionEvaluacionBean.getOperacion(this.
                        funcionEvaluacionXmlFileName);

                if (error < cep) {
                    fiActual = evalua(opActual, A.getValor(), 0, N, fp,
                                      valorCobertura);
                } else {
                    fiActual = evalua(opActual, A.getValor(), EC.getValor(), N,
                                      fp, valorCobertura);
                }
            } else { //If there isn't...
                if (error < cep) {
                    fiActual = N + A.getValor();
                } else {
                    fiActual = N - Discretizacion.multiplica(EC.getValor(), fp) +
                               A.getValor();
                }

                fiActual = Discretizacion.suma(fiActual, valorCobertura);
            }

            if (fiActual > bondadMax) {
                max = i;
                bondadMax = fiActual;
            }

            //adding goodness at the end of individual
            if (((Vector) poblacion.get(i)).size() == cortes.size()) {
                ((Vector) poblacion.get(i)).add(new Double(fiActual));
            } else if (((Vector) poblacion.get(i)).size() > cortes.size()) {
                ((Vector) poblacion.get(i)).setElementAt(new Double(fiActual),
                        ((Vector) poblacion.get(0)).size() - 1);
            }

            //Store EC in the appropriate Vector
            Entero[] aux = new Entero[2];
            aux[0] = EC;
            aux[1] = A;
            erroresClase.add(aux); //Vector contains type 'Entero[2]'

            //Addition of goodness
            sumaFi = Discretizacion.suma(sumaFi, fiActual);
        } //for(individuals...)

        if (max > 0) {
            Vector temp = (Vector) poblacion.remove(max);
            poblacion.insertElementAt(temp, 0); //puts the best rule at first position...

            Entero[] tempEnt = new Entero[2];
            tempEnt = (Entero[]) erroresClase.remove(max);
            erroresClase.insertElementAt(tempEnt, 0); //puts class errors for best rule at first position...
        }
    }


    /**
     * Gets evaluation function
     * @param op
     * @param a
     * @param ec
     * @param n
     * @param fp
     * @param cobertura
     * @return the value of individual's goodness
     * @throws Exception
     */
    private double evalua(Operacion op, int a, int ec, int n, double fp,
                          double cobertura) throws Exception {
        Object operador1 = null;
        Object operador2 = null;

        double resultado = 0;

        if (op.getA() != 0) {
            if (op.getIniOp() != null && op.getIniOp().equalsIgnoreCase("A")) {
                operador1 = new Double(a * op.getA());
            } else {
                operador2 = new Double(a * op.getA());
            }
        }

        if (op.getEC() != 0) {
            if (op.getIniOp() != null && op.getIniOp().equalsIgnoreCase("EC")) {
                operador1 = new Double(ec * op.getEC());
            } else {
                operador2 = new Double(ec * op.getEC());
            }
        }

        if (op.getFp() != 0) {
            if (op.getIniOp() != null && op.getIniOp().equalsIgnoreCase("fp")) {
                operador1 = new Double(Discretizacion.multiplica(fp, op.getFp()));
            } else {
                operador2 = new Double(Discretizacion.multiplica(fp, op.getFp()));
            }
        }

        if (op.getN() != 0) {
            if (op.getIniOp() != null && op.getIniOp().equalsIgnoreCase("N")) {
                operador1 = new Double(n * op.getN());
            } else {
                operador2 = new Double(n * op.getN());
            }
        }

        if (op.getCob() != 0) {
            if (op.getIniOp() != null && op.getIniOp().equalsIgnoreCase("cover")) {
                operador1 = new Double(n * op.getCob());
            } else {
                operador2 = new Double(n * op.getCob());
            }
        }

        if (op.getValorConstante() != 0) {
            //to prevent integer-formed values
            if (op.getIniOp().indexOf(".") < 0) {
                op.setIniOp(op.getIniOp() + ".0");
            }

            if (op.getIniOp() != null &&
                op.getIniOp().equals("" + op.getValorConstante())) {
                operador1 = new Double(op.getValorConstante());
            } else {
                operador2 = new Double(op.getValorConstante());
            }
        }

        if (op.getValorOperacion1() != null) {
            operador1 = op.getValorOperacion1();
        }

        if (op.getValorOperacion2() != null) {
            operador2 = op.getValorOperacion2();
        }

        if (operador1 != null && operador2 != null) {
            //Base case
            if (operador1 instanceof Double && operador2 instanceof Double) {
                if (op.getOperacion().equals("add")) {
                    resultado = Discretizacion.suma(((Double) operador1).
                            doubleValue(), ((Double) operador2).doubleValue());
                } else if (op.getOperacion().equals("sub")) {
                    resultado = Discretizacion.resta(((Double) operador1).
                            doubleValue(), ((Double) operador2).doubleValue());
                } else if (op.getOperacion().equals("mult")) {
                    resultado = Discretizacion.multiplica(((Double) operador1).
                            doubleValue(), ((Double) operador2).doubleValue());
                } else if (op.getOperacion().equals("div")) {
                    resultado = Discretizacion.divide(((Double) operador1).
                            doubleValue(), ((Double) operador2).doubleValue());
                }
            } else { //Recursive case
                if (operador1 instanceof Operacion) {
                    double op1 = evalua((Operacion) operador1, a, ec, n, fp,
                                        cobertura);

                    if (operador2 instanceof Operacion) {
                        double op2 = evalua((Operacion) operador2, a, ec, n, fp,
                                            cobertura);

                        if (op.getOperacion().equals("add")) {
                            resultado = Discretizacion.suma(op1, op2);
                        } else if (op.getOperacion().equals("sub")) {
                            resultado = Discretizacion.resta(op1, op2);
                        } else if (op.getOperacion().equals("mult")) {
                            resultado = Discretizacion.multiplica(op1, op2);
                        } else if (op.getOperacion().equals("div")) {
                            resultado = Discretizacion.divide(op1, op2);
                        }
                    } else { //operador2 instanceof Double
                        if (op.getOperacion().equals("add")) {
                            resultado = Discretizacion.suma(op1,
                                    ((Double) operador2).doubleValue());
                        } else if (op.getOperacion().equals("sub")) {
                            resultado = Discretizacion.resta(op1,
                                    ((Double) operador2).doubleValue());
                        } else if (op.getOperacion().equals("mult")) {
                            resultado = Discretizacion.multiplica(op1,
                                    ((Double) operador2).doubleValue());
                        } else if (op.getOperacion().equals("div")) {
                            resultado = Discretizacion.divide(op1,
                                    ((Double) operador2).doubleValue());
                        }
                    }
                } else if (operador2 instanceof Operacion) {
                    double op2 = evalua((Operacion) operador2, a, ec, n, fp,
                                        cobertura);

                    if (op.getOperacion().equals("add")) {
                        resultado = Discretizacion.suma(((Double) operador1).
                                doubleValue(), op2);
                    } else if (op.getOperacion().equals("sub")) {
                        resultado = Discretizacion.resta(((Double) operador1).
                                doubleValue(), op2);
                    } else if (op.getOperacion().equals("mult")) {
                        resultado = Discretizacion.multiplica(((Double)
                                operador1).doubleValue(), op2);
                    } else if (op.getOperacion().equals("div")) {
                        resultado = Discretizacion.divide(((Double) operador1).
                                doubleValue(), op2);
                    }
                }
            }

        } else {
            Exception e = new Exception(
                    "Operacion object must contain two operators.");
            throw e;
        }

        return resultado;
    }

    /**
     * Build the next generation
     * @param cortes
     * @param por100Cruces
     * @param numIndSigGen
     */
    public void reemplazo(Vector cortes, double por100Cruces, int numIndSigGen) {
        Vector nextG = new Vector(); //Next generation temporary 'Vector'

        //Is not necessary to sort because the best rule is at the first position

        Vector mejor = (Vector) poblacion.get(0); //Take the greatest goodness individual that will be at position 0

        nextG.add(mejor); //The best individual passes to the next generation immutable...
        Vector mejorMutado = muta(mejor, cortes);

        nextG.add(mejorMutado); //The same individual passes to the next generation mutated...

        if (numIndSigGen <= 0) {
            numIndSigGen = poblacion.size();
        }

        numIndSigGen -= 2; //There is two positions fewer

        if (por100Cruces <= 0 || por100Cruces > 100) {
            //By default 80% cross...
            por100Cruces = 80;
        }

        double por100Replicas = Math.rint(Discretizacion.resta(100,
                por100Cruces));

//Copies
        //Number of individual that pass without crossing (copies)
        int limite = (int) Math.round(Discretizacion.multiplica(por100Replicas,
                numIndSigGen));
        limite = (int) Math.round(Discretizacion.divide(limite, 100));
        numIndSigGen -= limite;

        for (int i = 0; i < limite; i++) {
            //System.out.println("Selecting individual "+(i+1)+" of "+limite+" for mutation");
            //1º Select individual
            Vector individuo = new Vector();
            select(individuo);

            //2º Mutate
            double aleatorio = Randomize.RandClosed(); //Math.random();
            if (aleatorio <= probMutacion) {
                aleatorio = Randomize.RandClosed(); //Math.random();
                if (aleatorio <= probMutExtremo) {
                    nextG.add(mutaExtremo(individuo, cortes));
                } else {
                    Vector mutado = muta(individuo, cortes);
                    nextG.add(mutado);
                }
            } else {
                nextG.add(individuo);
            }
        }

        //Crosses
        //Number of individual that will be crossed...
        for (int i = 0; i < numIndSigGen; i++) {
            //1º Select individual
            //don't delete individual after crossing
            //System.err.println("Selecting individual "+(i+1)+" of "+numIndSigGen+" for croosing");
            Vector individuo1 = new Vector();
            Vector individuo2 = new Vector();
            select(individuo1, false);
            int pos2 = select(individuo2, false);

            int class1 = ((Long) individuo1.get(individuo1.size() - 2)).
                         intValue();
            int class2 = ((Long) individuo2.get(individuo2.size() - 2)).
                         intValue();
            //cross both individual when they have the same class but they are different
            //if they not, take the next one

            int cont = 0;
            while ((equalIndividual(individuo1, individuo2) ||
                    (!equalIndividual(individuo1, individuo2) &&
                     class1 != class2)) && cont < poblacion.size()) {
                if (pos2 < poblacion.size() - 1) {
                    pos2++;
                } else {
                    pos2 = 0;
                }
                individuo2 = (Vector) poblacion.get(pos2);
                class2 = ((Long) individuo2.get(individuo2.size() - 2)).
                         intValue();
                //System.err.println("while son iguales o tienen distinta clase [mientras haya donde elegir]");
                cont++;
            }
            //if classes are the same...
            if (class1 == class2) {
                Vector[] hijos = cruza(individuo1, individuo2, cortes);
                double aleatorio = Randomize.RandClosed(); // Math.random();
                if (aleatorio <= probMutacion) {
                    nextG.add(muta(hijos[0], cortes));
                } else {
                    nextG.add(hijos[0]);
                }

                if (hijos[1] != null) {
                    aleatorio = Randomize.RandClosed(); //Math.random();
                    if (aleatorio <= probMutacion) {
                        nextG.add(muta(hijos[1], cortes));
                    } else {
                        nextG.add(hijos[1]);
                    }

                    i++;
                }
            } else { //if there are not any individual with the same class, just make a mutation to the individual 1
                Vector aux = muta(individuo1, cortes);
                nextG.add(aux);
            }

        }
        poblacion = (Vector) nextG.clone();
    }

    /**
     * @param individuo
     * @param cortes
     * @return a extreme mutation for individual
     */
    private Vector mutaExtremo(Vector individuo, Vector cortes) {
        Vector result = (Vector) individuo.clone();

        double aleatorio = Randomize.RandClosed(); //Math.random();
        int nGenes = cortes.size() - 1; //Don't take care of the output attribute
        int genAMutar = (int) Math.rint(aleatorio * nGenes);

        int nuevoN = 0;

        if (cortes.get(genAMutar) instanceof Vector) { //Continuous
            int k = ((Vector) cortes.get(genAMutar)).size();
            nuevoN = k - 1;
        } else { //Discrete
            int rango = ((Integer) cortes.get(genAMutar)).intValue();
            nuevoN = (int) Discretizacion.resta(Math.pow(2, rango), 1);
        }

        result.setElementAt(new Long(nuevoN), genAMutar);

        return result;
    }

    /**
     * @param individuo1
     * @param individuo2
     * @return true if the both individual are equals
     */
    private boolean equalIndividual(Vector individuo1, Vector individuo2) {
        boolean res = true;

        for (int i = 0; i < individuo1.size() && res; i++) {
            if (!individuo1.get(i).equals(individuo2.get(i))) {
                res = false;
            }
        }
        return res;
    }

    /**
     * This method without parameters deletes individual by default
     * @param indiv
     */
    private void select(Vector indiv) {
        //This method without parameters deletes individual by default
        select(indiv, true);
    }


    /**
     * Implements roulette algorithm
     * @param indiv
     * @param elimina
     * @return Returns the position of the selected individual
     */
    private int select(Vector indiv, boolean elimina) {
        double aleatorio = Randomize.RandClosed(); //Math.random();
        if (sumaBondades == null) {
            sumaBondades = new Vector();
            sumaBondades.add(new Double(0)); //sumaBondades[0]=0

            for (int i = 0; i < poblacion.size(); i++) {
                double bondad = ((Double) ((Vector) poblacion.get(i)).get(((
                        Vector) poblacion.get(i)).size() - 1)).doubleValue(); //Take the goodness
                bondad = Discretizacion.divide(bondad, sumaFi);

                sumaBondades.add(new Double(Discretizacion.suma(((Double)
                        sumaBondades.get(i)).doubleValue(), bondad)));
            }
        }

        boolean seleccionado = false;
        int i;
        for (i = 0; i < poblacion.size() - 1 && !seleccionado; i++) {
            if (aleatorio >= ((Double) sumaBondades.get(i)).doubleValue() &&
                aleatorio < ((Double) sumaBondades.get(i + 1)).doubleValue()) {
                seleccionado = true;
                //Clone the individual to return it
                for (int j = 0; j < ((Vector) poblacion.get(i)).size(); j++) {
                    indiv.add(((Vector) poblacion.get(i)).get(j));
                }
            }
        }

        if (!seleccionado) {
            for (int j = 0;
                         j <
                         ((Vector) poblacion.get(poblacion.size() - 1)).size();
                         j++) {
                indiv.add(((Vector) poblacion.get(poblacion.size() - 1)).get(j));
            }

            if (elimina) {
                poblacion.remove(poblacion.size() - 1); //Remove this individual from population to avoid repeated ones
            }
        }

        return (i - 1);
    }

    /**
     * @param individuo
     * @param cortes
     * @return mutado
     */
    private Vector muta(Vector individuo, Vector cortes) {
        Vector mutado = (Vector) individuo.clone();

        double aleatorio = Randomize.RandClosed(); //Math.random();
        int nGenes = cortes.size() - 2; //Don't take care of the output attribute
        int genAMutar = (int) Math.rint(aleatorio * nGenes);

        int n = ((Long) individuo.get(genAMutar)).intValue();
        int nuevoN = 0;

        if (cortes.get(genAMutar) instanceof Vector) { //If it's continuous...
            int k = ((Vector) cortes.get(genAMutar)).size(); //Obtain the number of cuts for the attribute
            int f = Codificacion.descodificaF(n, k);
            int c = Codificacion.descodificaC(n, k);

            aleatorio = Randomize.RandClosed(); //Math.random();
            int mArriba = (int) Math.rint(aleatorio * f);
            aleatorio = Randomize.RandClosed(); //Math.random();
            int mDerecha = (int) Math.rint(aleatorio * (k - c - 2));
            aleatorio = Randomize.RandClosed(); //Math.random();
            int mAbajo = (int) Math.rint(aleatorio * (c - f));
            aleatorio = Randomize.RandClosed(); //Math.random();
            int mIzquierda = (int) Math.rint(aleatorio * (c - f));

            nuevoN = su(n, k, mArriba);
            nuevoN = de(nuevoN, k, mDerecha);
            nuevoN = in(nuevoN, k, mAbajo);
            nuevoN = iz(nuevoN, k, mIzquierda);

        } else { //If it's discrete...
            //k will be the position of 'k' bit
            //every bit has the same mutation probability
            int rango = ((Integer) cortes.get(genAMutar)).intValue();
            int bits = (int) Math.round(Math.log(rango) / Math.log(2));
            aleatorio = Randomize.RandClosed(); //Math.random();
            int k = (int) Math.rint(aleatorio * bits);

            nuevoN = (int) ((n + Math.pow(2, k - 1)) % Math.pow(2, k) +
                            Math.pow(2, k) * Math.floor(n / Math.pow(2, k)));
        }

        mutado.setElementAt(new Long(nuevoN), genAMutar);

        return mutado;
    }

    /**
     *
     * @param v
     * @param titulo
     */
    public static void imprime(Vector v, String titulo) {
        System.out.println("\n\n----------------------------------------------");
        System.out.println(titulo + ": ");
        System.out.print("[");
        for (int ii = 0; ii < v.size() - 1; ii++) {
            if (v.get(ii) instanceof Integer) {
                System.out.print(((Integer) v.get(ii)).intValue() + ", ");
            } else if (v.get(ii) instanceof Double) {
                System.out.print(((Double) v.get(ii)).doubleValue() + ", ");
            } else if (v.get(ii) instanceof Long) {
                System.out.print(((Long) v.get(ii)).intValue() + ", ");
            } else {
                System.out.print(v.get(ii) + ", ");
            }
        }

        if (v.get(v.size() - 1) instanceof Integer) {
            System.out.print(((Integer) v.get(v.size() - 1)).intValue() + "]\n");
        } else if (v.get(v.size() - 1) instanceof Double) {
            System.out.print(((Double) v.get(v.size() - 1)).doubleValue() +
                             "]\n");
        } else if (v.get(v.size() - 1) instanceof Long) {
            System.out.print(((Long) v.get(v.size() - 1)).intValue() + "]\n");
        } else {
            System.out.print(v.get(v.size() - 1) + "]\n");
        }
    }

    /**
     *
     * @param reglaActual
     * @param cortes
     * @param EC
     * @param A
     */
    private void ec_a(Vector reglaActual, Vector cortes, Entero EC, Entero A) {
        int errorClase = 0;
        int aciertos = 0;

        //Look through the elements...
        for (int i = 0; i < bc.size(); i++) {
            Vector ejemploActual = (Vector) bc.get(i);

            boolean cumple = true;
            for (int j = 0; (j < (cortes.size() - 1)) && cumple; j++) { //'cortes' has the same number of elements than the number of attributes of 'bc'
                int k = 0;
                int actualEjemplo = ((Long) ejemploActual.get(j)).intValue();
                int actualRegla = ((Long) reglaActual.get(j)).intValue();

                //If it's discrete...
                if (cortes.get(j) instanceof Integer) {
                    k = ((Integer) cortes.get(j)).intValue(); //If it's discrete, store in 'cortes' the range of attribute.

                    //cumple = 'Rule's current attribute covers the element'
                    if ((actualRegla & actualEjemplo) != 0) {
                        cumple = true;
                    } else {
                        cumple = false;
                    }
                } else { //If it's continuous... (instance of 'Corte')
                    k = ((Vector) cortes.get(j)).size(); //Number of cuts of the attribute

                    int fr = Codificacion.descodificaF(actualRegla, k);
                    int fe = Codificacion.descodificaF(actualEjemplo, k);

                    int cr = Codificacion.descodificaC(actualRegla, k);
                    int ce = Codificacion.descodificaC(actualEjemplo, k);

                    //cumple = 'Rule's current attribute covers the element'
                    if ((fr <= fe) && (cr >= ce)) {
                        cumple = true;
                    } else {
                        cumple = false;
                    }
                }
            }
            if (cumple) {
                //If rule covers the element but they have different output values, we add an error
                if (!reglaActual.get(cortes.size() -
                                     1).equals(ejemploActual.get(cortes.size() -
                        1))) {
                    errorClase++;
                } else { //Hit!!!
                    aciertos++;
                }
            }

        }

        EC.setValor(errorClase);
        A.setValor(aciertos);
    }

    /**
     *
     * @param regla
     * @param rangos
     * @param cortes
     * @return Cover coefficient
     */
    public double cobertura(Vector regla, Vector rangos, Vector cortes) {
        double cobert = 1;

        for (int i = 0; i < cortes.size() - 1; i++) {
            double temp = Discretizacion.divide(cob((Long) regla.get(i),
                    cortes.get(i)), rango(rangos.get(i)));
            cobert = Discretizacion.multiplica(cobert, temp);
        }

        return cobert;
    }

    /**
     *
     * @param reglai
     * @param cortesi
     * @return a measure for the cover of the rule
     */
    private double cob(Long reglai, Object cortesi) {
        double res = 0;

        //If it's discrete...
        if (cortesi instanceof Integer) {
            res = ((Integer) cortesi).doubleValue();
        } else { //If it's continuous... (it's a 'Vector' of 'Corte' objects)
            int k = ((Vector) cortesi).size();
            int f = Codificacion.descodificaF(reglai.intValue(), k);
            int c = Codificacion.descodificaC(reglai.intValue(), k);
            double li = ((Corte) ((Vector) cortesi).get(f)).getCorte();
            double ls = ((Corte) ((Vector) cortesi).get(c + 1)).getCorte();

            res = Discretizacion.resta(ls, li);
        }

        return res;
    }

    /**
     *
     * @param rangoi
     * @return the 'double' value of 'rangoi'
     */
    private double rango(Object rangoi) {
        double res = 0;
        if (rangoi instanceof Double) { //Continuous
            res = ((Double) rangoi).doubleValue();
        } else { //Discrete
            res = ((Integer) rangoi).doubleValue();
        }
        return res;
    }


    /**
     * @return Returns the bc.
     */
    public Vector getBc() {
        return bc;
    }

    /**
     * @param bc The bc to set.
     */
    public void setBc(Vector bc) {
        this.bc = bc;
    }

    /**
     * @return Returns the probMutacion.
     */
    public double getProbMutacion() {
        return probMutacion;
    }

    /**
     * @param probMutacion The probMutacion to set.
     */
    public void setProbMutacion(double probMutacion) {
        this.probMutacion = probMutacion;
    }

    /**
     * @return Returns the probMutExtremo.
     */
    public double getProbMutExtremo() {
        return probMutExtremo;
    }

    /**
     * @param probMutExtremo The probMutExtremo to set.
     */
    public void setProbMutExtremo(double probMutExtremo) {
        this.probMutExtremo = probMutExtremo;
    }

    /**
     * @return Returns the erroresClase.
     */
    public Vector getErroresClase() {
        return erroresClase;
    }

    /**
     * @param erroresClase The erroresClase to set.
     */
    public void setErroresClase(Vector erroresClase) {
        this.erroresClase = erroresClase;
    }

    /**
     * @return Returns the erroresClase.
     */
    public Entero[] getErroresClase(int i) {
        return (Entero[]) erroresClase.get(i);
    }

    /**
     * @return Returns the cep.
     */
    public double getCep() {
        return cep;
    }

    /**
     * @param cep The cep to set.
     */
    public void setCep(double cep) {
        this.cep = cep;
    }

    /**
     * @return Returns the funcionEvaluacionXmlFileName.
     */
    public String getFuncionEvaluacionXmlFileName() {
        return funcionEvaluacionXmlFileName;
    }

    /**
     * @param funcionEvaluacionXmlFileName The funcionEvaluacionXmlFileName to set.
     */
    public void setFuncionEvaluacionXmlFileName(
            String funcionEvaluacionXmlFileName) {
        this.funcionEvaluacionXmlFileName = funcionEvaluacionXmlFileName;
    }
}

