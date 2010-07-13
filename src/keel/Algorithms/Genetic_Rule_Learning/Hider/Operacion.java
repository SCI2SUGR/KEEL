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
 * Created on 21-ago-2005
 */
package keel.Algorithms.Genetic_Rule_Learning.Hider;

/**
 * Binary operations (2 operands)
 * @author Sebas
 */
public class Operacion {
    //If value = 1: The operand appears with positive sign.
    //If value = -1: The operand appears with negative sign.
    //If value = 0: The operand doesn't appear.
    private int A = 1;
    private int EC = 1;
    private int N = 1;
    private int fp = 1;
    private int cob = 1;

    private double valorConstante = 0;
    private Operacion valorOperacion1 = null;
    private Operacion valorOperacion2 = null;

    //Valid operations are: "suma", "resta", "multiplicacion" and "division"
    private String operacion = "suma";
    //Initial value in the operation (for subtractions and divisions)
    private String iniOp = null;


    /*
         public Operacion(Object operand1, Object operand2, String operation)
         {
        A=0;
        EC=0;
        N=0;
        fp=0;

        operacion=operation;
        if(operand1 instanceof Operacion)
        {
            this.valorOperacion1=(Operacion)operand1;
        }
        else
        {
         int valor1=0;

         if(((String)operand1).indexOf("-") >= 0)
             valor1=-1;
         else
             valor1=1;

         if(((String)operand1).toUpperCase().indexOf("A") >= 0)
             A=valor1;
         else if(((String)operand1).toUpperCase().indexOf("EC") >= 0)
              EC=valor1;
         else if(((String)operand1).toUpperCase().indexOf("FP") >= 0)
           fp=valor1;
         else if(((String)operand1).toUpperCase().indexOf("N") >= 0)
           N=valor1;
         else
             valorConstante=Double.parseDouble((String)operand1);
        }

        if(operand2 instanceof Operacion)
        {
            this.valorOperacion1=(Operacion)operand2;
        }
        else
        {
            int valor2=0;

         if(((String)operand2).indexOf("-") >= 0)
             valor2=-1;
         else
             valor2=1;

         if(((String)operand2).toUpperCase().indexOf("A") >= 0)
             A=valor2;
         else if(((String)operand2).toUpperCase().indexOf("EC") >= 0)
              EC=valor2;
         else if(((String)operand2).toUpperCase().indexOf("FP") >= 0)
           fp=valor2;
         else if(((String)operand2).toUpperCase().indexOf("N") >= 0)
           N=valor2;
         else
             valorConstante=Double.parseDouble((String)operand2);
        }
         }
     */

    /**
     *
     * @param clase
     */
    public Operacion(String clase) {
        A = 0;
        EC = 0;
        N = 0;
        fp = 0;
        cob = 0;

        this.operacion = clase;
    }

    /**
     * @return Returns the valorOperacion1.
     */
    public Operacion getValorOperacion1() {
        return valorOperacion1;
    }

    /**
     * @param valorOperacion1 The valorOperacion1 to set.
     */
    public void setValorOperacion1(Operacion valorOperacion1) {
        this.valorOperacion1 = valorOperacion1;
    }

    /**
     * @return Returns the valorOperacion2.
     */
    public Operacion getValorOperacion2() {
        return valorOperacion2;
    }

    /**
     * @param valorOperacion2 The valorOperacion2 to set.
     */
    public void setValorOperacion2(Operacion valorOperacion2) {
        this.valorOperacion2 = valorOperacion2;
    }


    /*
         public void set(String operand1, String operand2, String operation)
         {
        A=0;
        EC=0;
        N=0;
        fp=0;

        operacion=operation;

        int valor1=0;
        int valor2=0;
        if(operand1.indexOf("-") >= 0)
            valor1=-1;
        else
            valor1=1;

        if(operand2.indexOf("-") >= 0)
            valor2=-1;
        else
            valor2=1;

        if(operand1.toUpperCase().indexOf("A") >= 0)
            A=valor1;
        else if(operand1.toUpperCase().indexOf("EC") >= 0)
             EC=valor1;
        else if(operand1.toUpperCase().indexOf("FP") >= 0)
          fp=valor1;
        else if(operand1.toUpperCase().indexOf("N") >= 0)
          N=valor1;
        else
            valorConstante=Double.parseDouble(operand1);

        if(operand2.toUpperCase().indexOf("A") >= 0)
            A=valor2;
        else if(operand2.toUpperCase().indexOf("EC") >= 0)
             EC=valor2;
        else if(operand2.toUpperCase().indexOf("FP") >= 0)
          fp=valor2;
        else if(operand2.toUpperCase().indexOf("N") >= 0)
          N=valor2;
        else
            valorConstante=Double.parseDouble(operand2);
         }
     */
    /**
     *
     * @param operand
     */
    public void set(String operand) {
        int valor = 0;

        if (operand.indexOf("-") >= 0) {
            valor = -1;
        } else {
            valor = 1;
        }

        if (operand.toUpperCase().indexOf("A") >= 0) {
            A = valor;
        } else if (operand.toUpperCase().indexOf("EC") >= 0) {
            EC = valor;
        } else if (operand.toUpperCase().indexOf("FP") >= 0) {
            fp = valor;
        } else if (operand.toUpperCase().indexOf("N") >= 0) {
            N = valor;
        } else if (operand.toUpperCase().indexOf("COVER") >= 0) {
            cob = valor;
        } else {
            valorConstante = Double.parseDouble(operand);
        }
    }


    /**
     * @return Returns the a.
     */
    public int getA() {
        return A;
    }

    /**
     * @param a The a to set.
     */
    public void setA(int a) {
        A = a;
    }

    /**
     * @return Returns the eC.
     */
    public int getEC() {
        return EC;
    }

    /**
     * @param ec The eC to set.
     */
    public void setEC(int ec) {
        EC = ec;
    }

    /**
     * @return Returns the fp.
     */
    public int getFp() {
        return fp;
    }

    /**
     * @param fp The fp to set.
     */
    public void setFp(int fp) {
        this.fp = fp;
    }

    /**
     * @return Returns the n.
     */
    public int getN() {
        return N;
    }

    /**
     * @param n The n to set.
     */
    public void setN(int n) {
        N = n;
    }

    /**
     * @return Returns the operacion.
     */
    public String getOperacion() {
        return operacion;
    }

    /**
     * @param operacion The operacion to set.
     */
    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    /**
     * @return Returns the valorConstante.
     */
    public double getValorConstante() {
        return valorConstante;
    }

    /**
     * @param valorConstante The valorConstante to set.
     */
    public void setValorConstante(double valorConstante) {
        this.valorConstante = valorConstante;
    }

    /**
     * @return Returns the iniOp.
     */
    public String getIniOp() {
        return iniOp;
    }

    /**
     * @param iniOp The iniOp to set.
     */
    public void setIniOp(String iniOp) {
        this.iniOp = iniOp;
    }

    /**
     * @return Returns the cob.
     */
    public int getCob() {
        return cob;
    }

    /**
     * @param cob The cob to set.
     */
    public void setCob(int cob) {
        this.cob = cob;
    }
}

